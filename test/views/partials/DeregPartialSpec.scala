/*
 * Copyright 2020 HM Revenue & Customs
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

package views.partials

import java.time.LocalDate

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec
import views.html.partials.deregPartial
import common.TestModels._

class DeregPartialSpec  extends ViewBaseSpec {

  object DeregPartialMessages {
    val deregTitle = "Cancel VAT registration"
    val deregContent = "Cancel your VAT registration if you’re closing the business, transferring ownership or do not need to be VAT registered."
    val historicDeregTitle = "Your VAT registration"
    val historicDeregContent: String => String = date => s"Your VAT registration was cancelled on $date."
    val historicDeregLink = "How to register for VAT (opens in a new window or tab)."
    val pendingDeregTitle = "Cancel VAT registration"
    val pendingDeregContent = "Your request to cancel your VAT registration is pending."
    val futureDeregTitle = "Your VAT registration"
    val futureDeregContent: String => String = date => s"Your VAT registration will be cancelled on $date."
    val futureDeregLink = "How to register for VAT (opens in new window or tab)."
  }

  "Rendering the partial" when {

    "user is registered" when {

      "user is not pending deregistration" when {

        lazy val view = deregPartial(vatDetailsModel, LocalDate.parse("2020-01-01"))(messages, mockConfig, user)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "display a section for cancelling registration" which {

          s"should have the correct title of ${DeregPartialMessages.deregTitle}" in {
            elementText("h3") shouldBe DeregPartialMessages.deregTitle
          }

          s"link to ${mockConfig.deregisterVatUrl}" in {
            element("h3 > a").attr("href") shouldBe mockConfig.deregisterVatUrl
          }

          s"have correct content of ${DeregPartialMessages.deregContent}" in {
            elementText("p") shouldBe DeregPartialMessages.deregContent
          }
        }
      }

      "user is pending deregistration" should {

        lazy val view = deregPartial(vatDetailsPendingDeregModel, LocalDate.parse("2020-01-01"))(messages, mockConfig, user)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "display a section for pending deregistration" which {

          s"should have the correct title of ${DeregPartialMessages.pendingDeregTitle}" in {
            elementText("h3") shouldBe DeregPartialMessages.pendingDeregTitle
          }

          s"have the correct content" in {
            elementText("p") shouldBe DeregPartialMessages.pendingDeregContent
          }
        }
      }
    }

    "client is not registered" when {

      "the effectDateOfCancellation is before the currentDate" should {

        "display the historic dereg partial" which {

          lazy val view = deregPartial(vatDetailsDeregModel, LocalDate.parse("2020-03-03"))(messages, mockConfig, user)
          lazy implicit val document: Document = Jsoup.parse(view.body)

          s"should have the correct title of ${DeregPartialMessages.historicDeregTitle}" in {
            elementText("h3") shouldBe DeregPartialMessages.historicDeregTitle
          }

          s"have correct content of ${DeregPartialMessages.historicDeregContent("2 February 2020")}" in {
            elementText("p") shouldBe DeregPartialMessages.historicDeregContent("2 February 2020")
          }

          s"have a link to ${mockConfig.govUkVatRegistrationUrl}" in {
            element("p > a").attr("href") shouldBe mockConfig.govUkVatRegistrationUrl
          }
        }
      }

      "client has a deregister date in the future" should {

        "display a section for future registration" which {

          lazy val view = deregPartial(vatDetailsDeregModel, LocalDate.parse("2020-01-01"))(messages, mockConfig, user)
          lazy implicit val document: Document = Jsoup.parse(view.body)

          s"should have the correct title of ${DeregPartialMessages.futureDeregTitle}" in {
            elementText("h3") shouldBe DeregPartialMessages.futureDeregTitle
          }

          s"have correct content of ${DeregPartialMessages.futureDeregContent("2 February 2020")}" in {
            elementText("p") shouldBe DeregPartialMessages.futureDeregContent("2 February 2020")
          }

          s"link with text of ${DeregPartialMessages.futureDeregLink}" in {
            element("a").text shouldBe DeregPartialMessages.futureDeregLink
          }

          s"link to ${mockConfig.govUkVatRegistrationUrl}" in {
            element("a").attr("href") shouldBe mockConfig.govUkVatRegistrationUrl
          }
        }
      }
    }
  }
}
