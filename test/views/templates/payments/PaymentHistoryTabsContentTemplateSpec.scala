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

package views.templates.payments

import java.time.LocalDate

import models.User
import models.payments.ReturnDebitCharge
import models.viewModels.PaymentsHistoryModel
import play.twirl.api.Html
import views.html.templates.payments.{PaymentsHistoryCharge, PaymentsHistoryTabsContent}
import views.templates.TemplateBaseSpec

class PaymentHistoryTabsContentTemplateSpec extends TemplateBaseSpec {

  val paymentsHistoryCharge: PaymentsHistoryCharge = injector.instanceOf[PaymentsHistoryCharge]
  val paymentsHistoryTabsContent: PaymentsHistoryTabsContent = injector.instanceOf[PaymentsHistoryTabsContent]

  val currentYear = 2018
  val previousYear = 2017
  val exampleAmount = 100
  val singleYear: Seq[Int] = Seq(currentYear)
  val multipleYears: Seq[Int] = Seq(currentYear, previousYear)
  implicit val user: User = User("999999999")

  val transaction: PaymentsHistoryModel = PaymentsHistoryModel(
    chargeType = ReturnDebitCharge,
    taxPeriodFrom = Some(LocalDate.parse(s"2018-01-01")),
    taxPeriodTo = Some(LocalDate.parse(s"2018-02-01")),
    amount = exampleAmount,
    clearedDate = Some(LocalDate.parse(s"2018-03-01"))
  )

  val sectionExampleWithPayment: String =
    s"""
      |<div id="$currentYear" class="govuk-tabs__panel">
      |  <h2 class="govuk-heading-m">$currentYear</h2>
      |  <table class="govuk-table">
      |    <thead class="govuk-table__head">
      |      <tr class="govuk-table__row">
      |        <th class="govuk-table__header govuk-visually-hidden" scope="col"> Payment received </th>
      |        <th class="govuk-table__header govuk-visually-hidden" scope="col"> Description </th>
      |        <th scope="col" class="numeric govuk-table__header govuk-visually-hidden"> Amount </th>
      |      </tr>
      |    </thead>
      |    <tbody class="govuk-table__body">
      |      ${paymentsHistoryCharge(transaction)}
      |    </tbody>
      |  </table>
      |</div>
    """.stripMargin

  def sectionExampleWithoutPayment(year: Int, divClassHidden: Boolean = false): String = {
    val hiddenDivClass: String =
      if(divClassHidden) {" govuk-tabs__panel--hidden"}
      else {""}
    val sectionAttributes: String =
        s"""id="$year" class="govuk-tabs__panel$hiddenDivClass""""
    s"""
      |<div $sectionAttributes>
      |  <h2 class="govuk-heading-m">$year</h2>
      |  <p class="govuk-body">You have not made or received any payments using the new VAT service this year.</p>
      |</div>
    """.stripMargin
  }

  "The payment history tabs content template" when {

    "the showPreviousPaymentsTab boolean is set to false" when {

      "there is one year" when {

        "there are no transactions" should {

          "render the correct HTML" in {

            val expectedMarkup = Html(sectionExampleWithoutPayment(currentYear))

            val result = paymentsHistoryTabsContent(
              singleYear, Seq.empty, showPreviousPaymentsTab = false
            )

            formatHtml(result) shouldBe formatHtml(expectedMarkup)
          }
        }

        "there are some transactions" should {

          "render the correct HTML" in {

            val expectedMarkup = Html(sectionExampleWithPayment)

            val result = paymentsHistoryTabsContent(
              singleYear, Seq(transaction), showPreviousPaymentsTab = false
            )

            formatHtml(result) shouldBe formatHtml(expectedMarkup)
          }
        }
      }

      "there are two years" when {

        "there are no transactions" should {

          "render the correct HTML" in {
            val expectedMarkup = Html(
              sectionExampleWithoutPayment(currentYear) + sectionExampleWithoutPayment(previousYear, divClassHidden = true)
            )

            val result = paymentsHistoryTabsContent(
              multipleYears, Seq.empty, showPreviousPaymentsTab = false
            )

            formatHtml(result) shouldBe formatHtml(expectedMarkup)
          }
        }

        "there are some transactions" should {

          "render the correct HTML" in {

            val expectedMarkup = Html(sectionExampleWithPayment + sectionExampleWithoutPayment(previousYear, divClassHidden = true))

            val result = paymentsHistoryTabsContent(
              multipleYears, Seq(transaction), showPreviousPaymentsTab = false
            )

            formatHtml(result) shouldBe formatHtml(expectedMarkup)
          }
        }
      }
    }

    "the showPreviousPaymentsTab boolean is set to true" should {

      "render the correct HTML" in {

        val expectedMarkup = Html(
          sectionExampleWithoutPayment(currentYear) +
          s"""
            |<div id="previous-payments" class="govuk-tabs__panel">
            |  <h2 class="govuk-heading-m">Previous payments</h2>
            |  <p class="govuk-body">
            |    You can
            |    <a class="govuk-link" href="${mockAppConfig.portalNonHybridPreviousPaymentsUrl(user.vrn)}">
            |      view your previous payments (opens in new tab)
            |    </a>
            |    if you made payments before joining Making Tax Digital.
            |  </p>
            |</div>
          """.stripMargin
        )

        val result = paymentsHistoryTabsContent(
          singleYear, Seq.empty, showPreviousPaymentsTab = true
        )

        formatHtml(result) shouldBe formatHtml(expectedMarkup)
      }
    }
  }
}
