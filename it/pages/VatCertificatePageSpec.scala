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
 * WITHOUT WARRANTIED OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pages

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.IntegrationBaseSpec
import play.api.http.Status
import play.api.libs.ws.{WSRequest, WSResponse}
import stubs.{AuthStub, CustomerInfoStub}

class VatCertificatePageSpec extends IntegrationBaseSpec {

  private trait Test {
    def setupStubs(): StubMapping

    def request(isAgent: Boolean = false): WSRequest = {
      setupStubs()
      if (isAgent) {
        buildRequest("/vat-certificate", formatSessionVrn(Some("1112221112")))
      } else {
        buildRequest("/vat-certificate")
      }
    }
  }

  "Calling the vat certificate route with an authenticated user" when {

    "the user is a non agent" should {

      "return 200 with the agent title" in new Test {
        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          CustomerInfoStub.stubCustomerInfo()
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe Status.OK
        response.body.contains("Your VAT Certificate") shouldBe true
      }
    }

    "the user is an agent" should {

      "return 200 with the non-agent title" in new Test {
        override def setupStubs(): StubMapping = {
          AuthStub.agentAuthorised()
          CustomerInfoStub.stubCustomerInfo()
        }

        val response: WSResponse = await(request(true).get())
        response.status shouldBe Status.OK
        response.body.contains("Your client&#x27;s VAT Certificate<") shouldBe true
      }
    }
  }
}