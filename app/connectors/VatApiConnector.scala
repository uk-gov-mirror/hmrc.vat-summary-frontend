/*
 * Copyright 2017 HM Revenue & Customs
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

package connectors

import java.time.LocalDate
import javax.inject.{Inject, Singleton}

import config.AppConfig
import connectors.httpParsers.ObligationsHttpParser.HttpGetResult
import models.Obligation.Status
import models.Obligations
import play.api.Logger
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VatApiConnector @Inject()(http: HttpClient, appConfig: AppConfig) {

  import connectors.httpParsers.ObligationsHttpParser.ObligationsReads

  private[connectors] def obligationsUrl(vrn: String): String = s"${appConfig.vatApiBaseUrl}/vat/$vrn/obligations"

  def getObligations(vrn: String, from: LocalDate, to: LocalDate, status: Status.Value)(implicit hc: HeaderCarrier, ec: ExecutionContext)
  : Future[HttpGetResult[Obligations]] = {
    http.GET(obligationsUrl(vrn), Seq("from" -> from.toString, "to" -> to.toString, "status" -> status.toString))
      .map {
        case rObs@Right(_) => rObs
        case lError@Left(error) =>
          Logger.info("VatApiConnector received error: " + error.message)
          lError
      }
  }

}