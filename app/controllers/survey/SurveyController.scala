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

package controllers.survey

import audit.AuditingService
import audit.models.ExitSurveyAuditing.ExitSurveyAuditModel
import com.google.inject.{Inject, Singleton}
import config.AppConfig
import forms.SurveyJourneyForm
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.controller.BaseController
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

@Singleton
class SurveyController @Inject()(val messagesApi: MessagesApi,
                                 val auditingService: AuditingService,
                                 implicit val appConfig: AppConfig
                                   ) extends BaseController with I18nSupport {

  val yourJourney: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(views.html.survey.journey(SurveyJourneyForm.form)))
  }

  def submit: Action[AnyContent] = Action.async {
    implicit request =>
      SurveyJourneyForm.form.bindFromRequest().fold(
        errors => {
          Future.successful(BadRequest(views.html.survey.journey(errors)))
        },
        surveyDetail => {
          auditingService.audit(ExitSurveyAuditModel(surveyDetail), controllers.survey.routes.SurveyController.yourJourney().url)
          Future.successful(Redirect(appConfig.surveyThankYouUrl))
        }
      )
  }
}