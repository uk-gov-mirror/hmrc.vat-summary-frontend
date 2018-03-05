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

package testOnly.controllers

import javax.inject.{Inject, Singleton}

import config.AppConfig
import models.payments.PaymentDetailsModel
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisedFunctions}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.Future

@Singleton
class PaymentsStubController @Inject()(val messagesApi: MessagesApi,
                                       val authConnector: AuthConnector,
                                       authFunctions: AuthorisedFunctions,
                                       implicit val appConfig: AppConfig)
  extends FrontendController with I18nSupport {

  def stub(): Action[AnyContent] = Action.async { implicit request =>
    authFunctions.authorised() {
      val data: String = request.session("payment-data")
      val model: PaymentDetailsModel = Json.parse(data).as[PaymentDetailsModel]
      Future.successful(Ok(testOnly.views.html.paymentsStub(model)))
    } recoverWith {
      case _ => Future.successful(Forbidden(views.html.errors.unauthorised()))
    }
  }
}