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

package views.vatDetails

import java.time.LocalDate

import models.viewModels.VatDetailsViewModel
import models.User
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec

class VatDetailsViewSpec extends ViewBaseSpec {

  object Selectors {
    val pageHeading = "h1"
    val entityNameHeading = "header > p"
    val nextPaymentHeading = "#payments h2"
    val nextPayment = "#payments p"
    val nextReturnHeading = "#next-return h2"
    val nextReturn = "#next-return p"
    val header = "div.test"
    val accountDetails = "#account-details"
    val submittedReturns = "#submitted-returns"
    val vatRegNo = ".form-hint"
    val btaBreadcrumb = "div.breadcrumbs li:nth-of-type(1)"
    val btaBreadcrumbLink = "div.breadcrumbs li:nth-of-type(1) a"
    val vatBreadcrumb = "div.breadcrumbs li:nth-of-type(2)"
    val overdueLabel = "span strong"
    val returnsVatLink = "#vat-returns-link"
  }

  val currentYear: Int = 2018
  private val user = User("123456789")
  val detailsModel = VatDetailsViewModel(
    Some(LocalDate.parse("2018-12-31")),
    Some(LocalDate.parse("2018-12-31")),
    Some("Cheapo Clothing"),
    currentYear
  )
  val overdueReturnDetailsModel = VatDetailsViewModel(
    Some(LocalDate.parse("2017-01-01")),
    Some(LocalDate.parse("2017-01-01")),
    Some("Cheapo Clothing"),
    currentYear,
    returnOverdue = true
  )
  val overduePaymentDetailsModel = VatDetailsViewModel(
    Some(LocalDate.parse("2017-01-01")),
    Some(LocalDate.parse("2018-12-31")),
    Some("Cheapo Clothing"),
    currentYear,
    paymentOverdue = true
  )
  val paymentErrorDetailsModel = VatDetailsViewModel(
    None,
    Some(LocalDate.parse("2018-12-31")),
    Some("Cheapo Clothing"),
    currentYear,
    paymentError = true
  )
  val returnErrorDetailsModel = VatDetailsViewModel(
    Some(LocalDate.parse("2018-12-31")),
    None,
    Some("Cheapo Clothing"),
    currentYear,
    returnError = true
  )
  val bothErrorDetailsModel = VatDetailsViewModel(
    None,
    None,
    Some("Cheapo Clothing"),
    currentYear,
    paymentError = true,
    returnError = true
  )

  mockConfig.features.accountDetails(true)

  "Rendering the VAT details page" should {

    lazy val view = views.html.vatDetails.details(user, detailsModel)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "render breadcrumbs which" should {

      "have the text 'Business tax account'" in {
        elementText(Selectors.btaBreadcrumb) shouldBe "Business tax account"
      }

      "links to bta" in {
        element(Selectors.btaBreadcrumbLink).attr("href") shouldBe "bta-url"
      }

      "have the text 'VAT'" in {
        elementText(Selectors.vatBreadcrumb) shouldBe "VAT"
      }
    }

    "have the correct document title" in {
      document.title shouldBe "VAT"
    }

    "have the correct entity name" in {
      elementText(Selectors.entityNameHeading) shouldBe detailsModel.entityName.getOrElse("Fail")
    }

    "have the correct VRN message" in {
      elementText(Selectors.vatRegNo) shouldBe s"VAT registration number (VRN): ${user.vrn}"
    }

    "have the account details section" should {

      lazy val accountDetails = element(Selectors.accountDetails)

      "have the heading" in {
        accountDetails.select("h2").text() shouldBe "Account details"
      }

      s"have a link to ${controllers.routes.AccountDetailsController.accountDetails().url}" in {
        accountDetails.select("a").attr("href") shouldBe controllers.routes.AccountDetailsController.accountDetails().url
      }

      "have the text" in {
        accountDetails.select("p").text() shouldBe "See your business information and other details."
      }
    }

    "have the submitted returns section" should {

      lazy val submittedReturns = element(Selectors.submittedReturns)

      "have the heading" in {
        submittedReturns.select("h2").text() shouldBe "Submitted returns"
      }

      s"have a link to 'returns-url/$currentYear'" in {
        submittedReturns.select("a").attr("href") shouldBe s"returns-url/$currentYear"
      }

      "have the text" in {
        submittedReturns.select("p").text() shouldBe "Check the returns you've sent us."
      }
    }
  }

  "Rendering the VAT details page with a next return and a next payment" should {

    lazy val view = views.html.vatDetails.details(user, detailsModel)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "render the next return section heading" in {
      elementText(Selectors.nextReturnHeading) shouldBe "Next return due"
    }

    "render the next return section" in {
      elementText(Selectors.nextReturn) shouldBe "31 December 2018"
    }

    "render the next payment section heading" in {
      elementText(Selectors.nextPaymentHeading) shouldBe "Next payment due"
    }

    "render the next payment section" in {
      elementText(Selectors.nextPayment) shouldBe "31 December 2018"
    }

    "render the next payment section vat returns link" in {
      elementText(Selectors.returnsVatLink) shouldBe "View return deadlines"
    }

    "have the correct next payment section vat returns link href" in {
      element(Selectors.returnsVatLink).attr("href") shouldBe mockConfig.vatReturnDeadlinesUrl
    }

  }

  "Rendering the VAT details page without a next return or next payment" should {

    lazy val view = views.html.vatDetails.details(user, VatDetailsViewModel(None, None, None, currentYear))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "render the next return section heading" in {
      elementText(Selectors.nextReturnHeading) shouldBe "Next return due"
    }

    "render the no return message" in {
      elementText(Selectors.nextReturn) shouldBe "No returns due right now"
    }

    "render the next payment section heading" in {
      elementText(Selectors.nextPaymentHeading) shouldBe "Next payment due"
    }

    "render the next payment section" in {
      elementText(Selectors.nextPayment) shouldBe "No payment due right now"
    }

    "render the next payment section vat returns link" in {
      elementText(Selectors.returnsVatLink) shouldBe "View return deadlines"
    }

    "have the correct next payment section vat returns link href" in {
      element(Selectors.returnsVatLink).attr("href") shouldBe mockConfig.vatReturnDeadlinesUrl
    }
  }

  "Rendering the VAT details page with an overdue return" should {

    lazy val view = views.html.vatDetails.details(user, overdueReturnDetailsModel)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "render the overdue label" in {
      elementText(Selectors.overdueLabel) shouldBe "overdue"
    }
  }

  "Rendering the VAT details page with an overdue payment" should {

    lazy val view = views.html.vatDetails.details(user, overduePaymentDetailsModel)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "render the overdue label" in {
      elementText(Selectors.overdueLabel) shouldBe "overdue"
    }
  }

  "Rendering the VAT details page with a payment error" should {

    lazy val view = views.html.vatDetails.details(user, paymentErrorDetailsModel)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "render the next return section heading" in {
      elementText(Selectors.nextReturnHeading) shouldBe "Next return due"
    }

    "render the next return section" in {
      elementText(Selectors.nextReturn) shouldBe "31 December 2018"
    }

    "render the next payment section heading" in {
      elementText(Selectors.nextPaymentHeading) shouldBe "Next payment due"
    }

    "render the next payment section" in {
      elementText(Selectors.nextPayment) shouldBe "Sorry, there is a problem with the service. Try again later."
    }

    "render the next payment section vat returns link" in {
      elementText(Selectors.returnsVatLink) shouldBe "View return deadlines"
    }

    "have the correct next payment section vat returns link href" in {
      element(Selectors.returnsVatLink).attr("href") shouldBe mockConfig.vatReturnDeadlinesUrl
    }
  }

  "Rendering the VAT details page with a return error" should {

    lazy val view = views.html.vatDetails.details(user, returnErrorDetailsModel)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "render the next return section heading" in {
      elementText(Selectors.nextReturnHeading) shouldBe "Next return due"
    }

    "render the next return section" in {
      elementText(Selectors.nextReturn) shouldBe "Sorry, there is a problem with the service. Try again later."
    }

    "render the next payment section heading" in {
      elementText(Selectors.nextPaymentHeading) shouldBe "Next payment due"
    }

    "render the next payment section" in {
      elementText(Selectors.nextPayment) shouldBe "31 December 2018"
    }

    "render the next payment section vat returns link" in {
      elementText(Selectors.returnsVatLink) shouldBe "View return deadlines"
    }

    "have the correct next payment section vat returns link href" in {
      element(Selectors.returnsVatLink).attr("href") shouldBe mockConfig.vatReturnDeadlinesUrl
    }
  }

  "Rendering the VAT details page with payment and return errors" should {

    lazy val view = views.html.vatDetails.details(user, bothErrorDetailsModel)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "render the next return section heading" in {
      elementText(Selectors.nextReturnHeading) shouldBe "Next return due"
    }

    "render the next return section" in {
      elementText(Selectors.nextReturn) shouldBe "Sorry, there is a problem with the service. Try again later."
    }

    "render the next payment section heading" in {
      elementText(Selectors.nextPaymentHeading) shouldBe "Next payment due"
    }

    "render the next payment section" in {
      elementText(Selectors.nextPayment) shouldBe "Sorry, there is a problem with the service. Try again later."
    }

    "render the next payment section vat returns link" in {
      elementText(Selectors.returnsVatLink) shouldBe "View return deadlines"
    }

    "have the correct next payment section vat returns link href" in {
      element(Selectors.returnsVatLink).attr("href") shouldBe mockConfig.vatReturnDeadlinesUrl
    }
  }
}
