/*
 * Copyright 2021 HM Revenue & Customs
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

package stubs

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.WireMockMethods
import play.api.http.Status._
import play.api.libs.json.{JsValue, Json}

object CustomerInfoStub extends WireMockMethods {

  private val customerInfoUri = "/vat-subscription/([0-9]+)/full-information"

  def stubCustomerInfo(customerInfo: JsValue): StubMapping = {
    when(method = GET, uri = customerInfoUri)
      .thenReturn(status = OK, body = customerInfo)
  }

  def stubErrorFromApi(stubbedUrl: String = customerInfoUri): StubMapping = {
    when(method = GET, uri = stubbedUrl)
      .thenReturn(status = INTERNAL_SERVER_ERROR, body = errorJson)
  }

  //noinspection ScalaStyle
  def customerInfoJson(isPartialMigration: Boolean, hasVerifiedEmail: Boolean): JsValue = Json.obj(
    "customerDetails" -> Json.obj(
      "organisationName" -> "Cheapo Clothing Ltd",
      "firstName" -> "Betty",
      "lastName" -> "Jones",
      "tradingName" -> "Cheapo Clothing",
      "isPartialMigration" ->  isPartialMigration,
      "effectiveRegistrationDate" -> "2017-01-01",
      "hybridToFullMigrationDate" -> "2017-05-06",
      "customerMigratedToETMPDate" -> "2017-05-05",
      "isInsolvent" -> false,
      "continueToTrade" -> true,
      "insolvencyType" -> "01",
      "insolvencyDate" -> "2018-01-01"
    ),
    "ppob" -> Json.obj(
      "address" -> Json.obj(
        "line1" -> "Bedrock Quarry",
        "line2" -> "Bedrock",
        "line3" -> "Graveldon",
        "postCode" -> "GV2 4BB"
      ),
      "contactDetails" -> Json.obj(
        "primaryPhoneNumber" -> "01632 982028",
        "mobileNumber" -> "07700 900018",
        "emailAddress" -> "bettylucknexttime@gmail.com",
        "emailVerified" -> hasVerifiedEmail
      )
    ),
    "bankDetails" -> Json.obj(
      "accountHolderName" -> "Mrs Betty Jones",
      "bankAccountNumber" -> "****1234",
      "sortCode" -> "69****"
    ),
    "returnPeriod" -> Json.obj(
      "stdReturnPeriod" -> "MM"
    ),
    "nonStdTaxPeriods" -> Json.arr(
      Json.obj(
        "periodStart" -> "2018-01-01",
        "periodEnd" -> "2018-01-15"
      ),
      Json.obj(
        "periodStart" -> "2018-01-06",
        "periodEnd" -> "2018-01-28"
      )
    ),
    "firstNonNSTPPeriod" -> Json.obj(
      "periodStart" -> "2018-01-29",
      "periodEnd" -> "2018-01-31"
    ),
    "partyType" -> "7",
    "primaryMainCode" -> "10410",
    "pendingChanges" -> Json.obj(
      "mandationStatus" -> "MTDfB Voluntary"
    ),
    "deregistration" -> Json.obj(
      "effectDateOfCancellation" -> "2020-01-01"
    ),
    "changeIndicators" -> Json.obj(
      "deregister" -> false
    ),
    "missingTrader" -> false,
    "mandationStatus" -> "MTDfB"
  )

  //noinspection ScalaStyle
  def customerInfoJsonNonMtdfb(isPartialMigration: Boolean, hasVerifiedEmail: Boolean): JsValue = Json.obj(
    "customerDetails" -> Json.obj(
      "organisationName" -> "Cheapo Clothing Ltd",
      "firstName" -> "Betty",
      "lastName" -> "Jones",
      "tradingName" -> "Cheapo Clothing",
      "isPartialMigration" ->  isPartialMigration,
      "vatRegistrationDate" -> "2017-01-01",
      "customerMigratedToETMPDate" -> "2017-05-05",
      "isInsolvent" -> false,
      "continueToTrade" -> true,
      "insolvencyType" -> "01",
      "insolvencyDate" -> "2018-01-01"
    ),
    "ppob" -> Json.obj(
      "address" -> Json.obj(
        "line1" -> "Bedrock Quarry",
        "line2" -> "Bedrock",
        "line3" -> "Graveldon",
        "postCode" -> "GV2 4BB"
      ),
      "contactDetails" -> Json.obj(
        "primaryPhoneNumber" -> "01632 982028",
        "mobileNumber" -> "07700 900018",
        "emailAddress" -> "bettylucknexttime@gmail.com",
        "emailVerified" -> hasVerifiedEmail
      )
    ),
    "bankDetails" -> Json.obj(
      "accountHolderName" -> "Mrs Betty Jones",
      "bankAccountNumber" -> "****1234",
      "sortCode" -> "69****"
    ),
    "returnPeriod" -> Json.obj(
      "stdReturnPeriod" -> "MM"
    ),
    "nonStdTaxPeriods" -> Json.arr(
      Json.obj(
        "periodStart" -> "2018-01-01",
        "periodEnd" -> "2018-01-15"
      ),
      Json.obj(
        "periodStart" -> "2018-01-06",
        "periodEnd" -> "2018-01-28"
      )
    ),
    "firstNonNSTPPeriod" -> Json.obj(
      "periodStart" -> "2018-01-29",
      "periodEnd" -> "2018-01-31"
    ),
    "partyType" -> "7",
    "primaryMainCode" -> "10410",
    "pendingChanges" -> Json.obj(
      "mandationStatus" -> "MTDfB Voluntary"
    ),
    "deregistration" -> Json.obj(
      "effectDateOfCancellation" -> "2020-01-01"
    ),
    "changeIndicators" -> Json.obj(
      "deregister" -> false
    ),
    "missingTrader" -> false,
    "mandationStatus" -> "Non MTDfB"
  )

  private val errorJson = Json.obj(
    "code" -> "500",
    "message" -> "INTERNAL_SERVER_ERROR"
  )
}
