/*
 * Copyright 2018 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers.predicates

import common.{EnrolmentKeys, SessionKeys}
import config.AppConfig
import javax.inject.Inject
import models.{MandationStatus, User}
import common.{FinancialTransactionsConstants => keys}
import play.api.Logger
import play.api.i18n.MessagesApi
import play.api.mvc.Results._
import play.api.mvc.{AnyContent, Request, Result}
import services.{EnrolmentsAuthService, MandationStatusService}
import uk.gov.hmrc.auth.core.retrieve.Retrievals.allEnrolments
import uk.gov.hmrc.auth.core.{AuthorisationException, Enrolment, EnrolmentIdentifier, NoActiveSession}

import scala.concurrent.{ExecutionContext, Future}

class AgentPredicate @Inject()(authService: EnrolmentsAuthService,
                               val messagesApi: MessagesApi,
                               mandationStatusService: MandationStatusService,
                               implicit val ec: ExecutionContext,
                               implicit val appConfig: AppConfig) {

  def authoriseAsAgent[A](block: Request[AnyContent] => User => Future[Result])
                         (implicit request: Request[AnyContent], user: User): Future[Result] = {

    val agentDelegatedAuthorityRule: String => Enrolment = vrn =>
      Enrolment(EnrolmentKeys.mtdVatEnrolmentKey)
        .withIdentifier(EnrolmentKeys.mtdVatIdentifierKey, vrn)
        .withDelegatedAuthRule(EnrolmentKeys.agentDelegatedAuthRuleKey)

    request.session.get(SessionKeys.agentSessionVrn) match {
      case Some(vrn) =>
        authService
          .authorised(agentDelegatedAuthorityRule(vrn))
          .retrieve(allEnrolments) {
            enrolments =>
              enrolments.enrolments.collectFirst {
                case Enrolment(EnrolmentKeys.agentEnrolmentKey, EnrolmentIdentifier(_, arn) :: _, EnrolmentKeys.activated, _) => arn
              } match {
                case Some(arn) => block(request)(User(vrn, arn = Some(arn)))
                case None =>
                  Logger.debug("[AuthPredicate][authoriseAsAgent] - Agent with no HMRC-AS-AGENT enrolment. Rendering unauthorised view.")
                  //TODO: need error page
                  Future.successful(Forbidden)
              }
          } recover {
          case _: NoActiveSession =>
            Logger.debug(s"AuthoriseAsAgentWithClient][authoriseAsAgent] - No active session. Redirecting to ${appConfig.signInUrl}")
            Redirect(appConfig.signInUrl)
          case _: AuthorisationException =>
            //TODO: add url
            Logger.debug(s"[AuthoriseAsAgentWithClient][authoriseAsAgent] - Agent does not have delegated authority for Client. " +
              s"Redirecting to 'url'")
            Redirect("")
        }
      case None =>
        //TODO: add url
        Logger.debug(s"[AuthPredicate][authoriseAsAgent] - No Client VRN in session. Redirecting to 'url'")
        Future.successful(Redirect(""))
    }
  }

  private def checkMandationStatus(block: Request[AnyContent] => User => Future[Result])
                                  (implicit request: Request[AnyContent], user: User): Future[Result] = {

    mandationStatusService.getMandationStatus(user.vrn) map {
      case Right(MandationStatus(keys.nonMTDfB)) => block(request)(user)
      case Left(_) =>
        Logger.warn(s"[AuthPredicate][authoriseAsAgent] - No Client VRN in session. Redirecting to 'url'")
        InternalServerError
    }
  }
}
