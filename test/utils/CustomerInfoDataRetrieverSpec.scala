/*
 * Copyright 2019 HM Revenue & Customs
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

package utils

import models.{Address, CustomerInformation}
import java.time.LocalDate
import uk.gov.hmrc.play.test.UnitSpec

class CustomerInfoDataRetrieverSpec extends UnitSpec {

  "Calling the retrieveCustomerMigratedToETMPDate function" when {

    "a CustomerInformation response is provided" should {

      "return the date the customer migrated to ETMP" in {

        val result = CustomerInfoDataRetriever.retrieveCustomerMigratedToETMPDate(Right(CustomerInformation(
          None, None, None, None,
          Address("line1", "line2", None, None, None), None, None, None,
          Address("line1", "line2", None, None, None), None, None, None,
          isHybridUser = false, Some("2019-01-01"))))

          result shouldBe Some(LocalDate.parse("2019-01-01"))
      }

      "return None if the customer has not been migrated to ETMP" in {

        val result = CustomerInfoDataRetriever.retrieveCustomerMigratedToETMPDate(Right(CustomerInformation(
          None, None, None, None,
          Address("line1", "line2", None, None, None), None, None, None,
          Address("line1", "line2", None, None, None), None, None, None,
          isHybridUser = false, None)))

        result shouldBe None
      }
    }
  }
}
