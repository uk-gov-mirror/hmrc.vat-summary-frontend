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

package services

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.{Inject, Singleton}

import cats.data.EitherT
import cats.implicits._
import connectors.VatApiConnector
import connectors.httpParsers.ObligationsHttpParser._
import models.Obligation.Status._
import models.{Obligation, Obligations, User}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VatDetailsService @Inject()(connector: VatApiConnector) {

  implicit def localDateOrdering: Ordering[LocalDate] = {
    Ordering.fromLessThan(_ isAfter _)
  }

  def retrieveNextReturnObligation(obligations: Obligations): Obligation = {
    obligations.obligations.maxBy(_.due)
  }

  def getVatDetails(user: User)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpGetResult[Obligation]] = {
    val numDaysPrior = 90
    val numDaysAhead = 395
    val now = LocalDate.now()
    val dateFrom = now.minus(numDaysPrior, ChronoUnit.DAYS)
    val dateTo = now.plus(numDaysAhead, ChronoUnit.DAYS)

    // TODO: possibly return the EitherT straight to the controller and use fold
    EitherT(connector.getObligations(user.vrn, dateFrom, dateTo, Outstanding))
      .map(retrieveNextReturnObligation).value
  }

}