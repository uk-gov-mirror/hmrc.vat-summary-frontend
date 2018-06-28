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

package views.templates

import models.User
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.HtmlFormat
import views.ViewBaseSpec

class VatCertificateSectionTemplateSpec extends ViewBaseSpec {

  "The vatCertificateSection" when {

    object Selectors {
      val vatCertificateHeading = "#vat-certificate h2 a"
    }

    def view: HtmlFormat.Appendable = views.html.templates.vatCertificateSection(User("123456789"))
    implicit def document: Document = Jsoup.parse(view.body)

    "the vat certificate feature" should {

      "have the correct heading" in {
        elementText(Selectors.vatCertificateHeading) shouldBe "View VAT certificate (opens in a new tab)"
      }

      "have the correct portal link" in {
        elementAttributes(Selectors.vatCertificateHeading).get("href") shouldBe Some("/vat/trader/123456789/certificate")
      }
    }
  }
}