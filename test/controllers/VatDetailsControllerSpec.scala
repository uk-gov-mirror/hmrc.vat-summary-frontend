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

package controllers

import java.time.LocalDate

import audit.AuditingService
import audit.models.AuditModel
import common.TestModels._
import common.{SessionKeys, TestModels}
import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import controllers.predicates.HybridUserPredicate
import models._
import models.errors.{BadRequestError, NextPaymentError, ObligationsError}
import models.obligations.{VatReturnObligation, VatReturnObligations}
import models.payments.Payments
import models.viewModels.VatDetailsViewModel
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.Retrieval
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class VatDetailsControllerSpec extends ControllerBaseSpec {

  private trait DetailsTest {

    val obligations: VatReturnObligations = TestModels.obligations
    val payments: Payments = TestModels.payments

    val authResult: Future[_] = successfulAuthResult
    val vatServiceReturnsResult: Future[ServiceResponse[Option[VatReturnObligations]]] = Future.successful(Right(Some(obligations)))
    val vatServicePaymentsResult: Future[ServiceResponse[Option[Payments]]] = Future.successful(Right(Some(payments)))
    val accountDetailsServiceResult: Future[HttpGetResult[CustomerInformation]] = Future.successful(Right(customerInformation))
    val mandationStatusServiceResult: Future[HttpGetResult[MandationStatus]] = Future.successful(Right(validMandationStatus))

    val mockAuthConnector: AuthConnector = mock[AuthConnector]
    val mockVatDetailsService: VatDetailsService = mock[VatDetailsService]
    val mockAccountDetailsService: AccountDetailsService = mock[AccountDetailsService]
    val mockDateService: DateService = mock[DateService]
    val mockAuditService: AuditingService = mock[AuditingService]
    val mockHybridUserPredicate: HybridUserPredicate = new HybridUserPredicate(mockAccountDetailsService)
    val mockEnrolmentsAuthService: EnrolmentsAuthService = new EnrolmentsAuthService(mockAuthConnector)
    val mockMandationService: MandationStatusService = mock[MandationStatusService]

    def setup(needMandationCall: Boolean = true): Any = {
      (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
        .stubs(*, *, *, *)
        .returns(authResult)

      (mockDateService.now: () => LocalDate).stubs().returns(LocalDate.parse("2018-05-01"))

      (mockVatDetailsService.getReturnObligations(_: User, _: LocalDate)(_: HeaderCarrier, _: ExecutionContext))
        .stubs(*, *, *, *)
        .returns(vatServiceReturnsResult)

      (mockVatDetailsService.getPaymentObligations(_: User)(_: HeaderCarrier, _: ExecutionContext))
        .stubs(*, *, *)
        .returns(vatServicePaymentsResult)

      (mockAccountDetailsService.getAccountDetails(_: String)(_: HeaderCarrier, _: ExecutionContext))
        .stubs(*, *, *)
        .returns(accountDetailsServiceResult)

      (mockAuditService.audit(_: AuditModel, _: String)(_: HeaderCarrier, _: ExecutionContext))
        .stubs(*, *, *, *)
        .returns({})

      if(needMandationCall) {
        (mockMandationService.getMandationStatus(_: String)(_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *)
          .returning(mandationStatusServiceResult).anyNumberOfTimes()
      }
    }

    val mockAuthorisedController: AuthorisedController = new AuthorisedController(
      messages,
      mockEnrolmentsAuthService,
      mockHybridUserPredicate,
      mockAppConfig
    )

    def target(needMandationCall: Boolean = true): VatDetailsController = {
      setup(needMandationCall)
      new VatDetailsController(messages,
        mockEnrolmentsAuthService,
        mockAppConfig,
        mockVatDetailsService,
        mockAuthorisedController,
        mockAccountDetailsService,
        mockDateService,
        mockAuditService,
        mockMandationService)
    }
  }

  "Calling the details action" when {

    "the user is logged in and does not have a customerMigratedToETMPDate in session" should {

      "return 200" in new DetailsTest {
        private val result = target().details()(fakeRequest)
        status(result) shouldBe Status.OK
      }

      "return HTML" in new DetailsTest {
        private val result = target().details()(fakeRequest)
        contentType(result) shouldBe Some("text/html")
      }

      "return charset utf-8" in new DetailsTest {
        private val result = target().details()(fakeRequest)
        charset(result) shouldBe Some("utf-8")
      }

      "put a customerMigratedToETMPDate key into the session" in new DetailsTest {
        private val result = target().details()(fakeRequest)
        session(result).get(SessionKeys.migrationToETMP) shouldBe Some("")
      }

      "put a mandation status in the session" in new DetailsTest {
        private val result = target().details()(fakeRequest)
        session(result).get(SessionKeys.mandationStatus) shouldBe Some(Json.stringify(Json.toJson(MandationStatus("MTDfB"))))
      }

      "not overrite the mandation status in the session" in new DetailsTest {
        val fakeRequestWithSession: FakeRequest[AnyContentAsEmpty.type] = FakeRequest().withSession(
          SessionKeys.mandationStatus -> Json.stringify(Json.toJson(MandationStatus("Non MTDfB")))
        )

        private val result = target().details()(fakeRequestWithSession)
        session(result).get(SessionKeys.mandationStatus) shouldBe Some(Json.stringify(Json.toJson(MandationStatus("Non MTDfB"))))
      }
    }

    "the user is logged in and has a customerMigratedToETMPDate in session" should {

      "return 200" in new DetailsTest {
        private val result = target().details()(fakeRequestWithSession)
        status(result) shouldBe Status.OK
      }

      "return HTML" in new DetailsTest {
        private val result = target().details()(fakeRequestWithSession)
        contentType(result) shouldBe Some("text/html")
      }

      "return charset utf-8" in new DetailsTest {
        private val result = target().details()(fakeRequestWithSession)
        charset(result) shouldBe Some("utf-8")
      }

      "not overwrite the customerMigratedToETMPDate value in the session" in new DetailsTest {
        private val result = target().details()(fakeRequestWithSession)
        session(result).get(SessionKeys.migrationToETMP) shouldBe Some("2018-01-01")
      }
    }

    "the user is not logged in" should {

      "return 401 (Unauthorised)" in new DetailsTest {
        override val authResult: Future[Nothing] = Future.failed(MissingBearerToken())
        val result: Future[Result] = target().details()(fakeRequest)
        status(result) shouldBe Status.UNAUTHORIZED
      }
    }

    "the user does not have sufficient enrolments" should {

      "return 403 (Forbidden)" in new DetailsTest {
        override val authResult: Future[Nothing] = Future.failed(InsufficientEnrolments())
        val result: Future[Result] = target().details()(fakeRequest)
        status(result) shouldBe Status.FORBIDDEN
      }
    }

    "the user is not authenticated" should {

      "return 403 (Forbidden)" in new DetailsTest {
        override val authResult: Future[Nothing] = Future.failed(InsufficientConfidenceLevel())
        val result: Future[Result] = target().details()(fakeRequest)
        status(result) shouldBe Status.FORBIDDEN
      }
    }

    "the user is hybrid" should {

      "not attempt to retrieve payment obligations" in new DetailsTest {
        mockAppConfig.features.submitReturnFeatures(true)

        override val accountDetailsServiceResult: Future[HttpGetResult[CustomerInformation]] =
          Future.successful(Right(customerInformationHybrid))

        override def setup(needMandationCall: Boolean = true): Unit = {
          (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
            .stubs(*, *, *, *)
            .returns(authResult)

          (mockDateService.now: () => LocalDate).stubs().returns(LocalDate.parse("2018-05-01"))

          (mockAccountDetailsService.getAccountDetails(_: String)(_: HeaderCarrier, _: ExecutionContext))
            .stubs(*, *, *)
            .returns(accountDetailsServiceResult)

          (mockVatDetailsService.getReturnObligations(_: User, _: LocalDate)(_: HeaderCarrier, _: ExecutionContext))
            .stubs(*, *, *, *)
            .returns(vatServiceReturnsResult)

          (mockVatDetailsService.getPaymentObligations(_: User)(_: HeaderCarrier, _: ExecutionContext))
            .stubs(*, *, *)
            .never()

          (mockAuditService.audit(_: AuditModel, _: String)(_: HeaderCarrier, _: ExecutionContext))
            .stubs(*, *, *, *)
            .returns({})

          (mockMandationService.getMandationStatus(_: String)(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *)
            .returning(mandationStatusServiceResult).once()
        }

        val result: Future[Result] = target().details()(fakeRequest)
        status(result) shouldBe Status.OK
      }
    }

    "the feature switch is turned off" should {
      "the View Returns link should be displayed" in new DetailsTest {
        mockAppConfig.features.submitReturnFeatures(false)
        lazy val result: Future[Result] = target().details()(fakeRequest)
        status(result) shouldBe OK
        await(bodyOf(result)).contains(messages("returnObligation.viewReturns")) shouldBe true
      }
    }

    "the feature switch is turned on" should {
      "return a VatDetailsViewModel as a non MTDfB user" in new DetailsTest {
        (mockMandationService.getMandationStatus(_: String)(_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *)
          .returns(Future.successful(Right(MandationStatus("Non MTDfB"))))

        mockAppConfig.features.submitReturnFeatures(true)
        lazy val result: Future[Result] = target().details()(fakeRequest)
        status(result) shouldBe OK
        await(bodyOf(result)).contains(messages("returnObligation.submit")) shouldBe true
      }

      "return a VatDetailsViewModel as a MTDfB user if no mandation status is returned" in new DetailsTest {
        (mockMandationService.getMandationStatus(_: String)(_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *)
          .returns(Future.successful(Left(BadRequestError("AN ERROR", "HAS OCCURRED"))))

        mockAppConfig.features.submitReturnFeatures(true)
        lazy val result: Future[Result] = target().details()(fakeRequest)
        status(result) shouldBe OK
        await(bodyOf(result)).contains(messages("returnObligation.submit")) shouldBe false
        await(bodyOf(result)).contains(messages("returnObligation.viewReturns")) shouldBe false
      }
    }
  }

  "Calling .constructViewModel with a VatDetailsModel" when {

    lazy val paymentDueDate: Option[String] = Some("2019-03-03")
    lazy val obligationData: Option[String] = Some("2019-06-06")

    "there is both a payment and an obligation" should {

      "return a VatDetailsViewModel with both due dates" in new DetailsTest {
        lazy val expected = VatDetailsViewModel(paymentDueDate, obligationData, Some(entityName), currentYear)
        lazy val result: VatDetailsViewModel = target().constructViewModel(
          Right(Some(obligations)),
          Right(Some(payments)),
          Right(customerInformation),
          Right(validMandationStatus)
        )

        result shouldBe expected
      }
    }

    "there is a payment but no obligation" should {

      "return a VatDetailsViewModel with a payment due date and no obligation due date" in new DetailsTest {
        lazy val expected = VatDetailsViewModel(paymentDueDate, None, Some(entityName), currentYear)
        lazy val result: VatDetailsViewModel = target().constructViewModel(
          Right(None),
          Right(Some(payments)),
          Right(customerInformation),
          Right(validMandationStatus)
        )

        result shouldBe expected
      }
    }

    "there is an obligation but no payment" should {

      "return a VatDetailsViewModel with an obligation due date and no payment due date" in new DetailsTest {
        lazy val expected = VatDetailsViewModel(None, obligationData, Some(entityName), currentYear)
        lazy val result: VatDetailsViewModel = target().constructViewModel(
          Right(Some(obligations)),
          Right(None),
          Right(customerInformation),
          Right(validMandationStatus)
        )

        result shouldBe expected
      }
    }

    "there is no obligation or payment" should {

      "return a VatDetailsViewModel with no obligation due date and no payment due date" in new DetailsTest {
        lazy val expected = VatDetailsViewModel(None, None, Some(entityName), currentYear)
        lazy val result: VatDetailsViewModel = target().constructViewModel(
          Right(None),
          Right(None),
          Right(customerInformation),
          Right(validMandationStatus)
        )

        result shouldBe expected
      }
    }

    "there is no obligation, payment, or entity name" should {

      "return a VatDetailsViewModel with no obligation due date, payment due date, or entity name" in new DetailsTest {
        lazy val expected = VatDetailsViewModel(None, None, None, currentYear)
        lazy val result: VatDetailsViewModel = target().constructViewModel(
          Right(None),
          Right(None),
          Right(customerInformationNoEntityName),
          Right(validMandationStatus)
        )

        result shouldBe expected
      }
    }

    "there is no obligation, payment, or entity name with a non-MTDfB user" should {

      "return a VatDetailsViewModel with no obligation due date, payment due date, or entity name with the isNonMTDfB flag set to true" in new DetailsTest {
        lazy val expected = VatDetailsViewModel(None, None, None, currentYear, isNonMTDfBUser = Some(true))
        lazy val result: VatDetailsViewModel = target().constructViewModel(
          Right(None),
          Right(None),
          Right(customerInformationNoEntityName),
          Right(validNonMTDfBMandationStatus)
        )

        result shouldBe expected
      }
    }

    "there is an error from VAT API" should {
      "return a VatDetailsViewModel with the returnError flag set" in new DetailsTest {
        lazy val expected = VatDetailsViewModel(None, None, Some(entityName), currentYear, returnObligationError = true)
        lazy val result: VatDetailsViewModel = target().constructViewModel(
          Left(ObligationsError),
          Right(None),
          Right(customerInformation),
          Right(validMandationStatus)
        )

        result shouldBe expected
      }
    }

    "there is an error from Financial Data API" should {

      "return a VatDetailsViewModel with the paymentError flag set" in new DetailsTest {
        lazy val expected = VatDetailsViewModel(None, None, Some(entityName), currentYear, paymentError = true)
        lazy val result: VatDetailsViewModel = target().constructViewModel(
          Right(None),
          Left(NextPaymentError),
          Right(customerInformation),
          Right(validMandationStatus)
        )

        result shouldBe expected
      }
    }

    "there is an error from both APIs" should {

      "return a VatDetailsViewModel with the returnError and paymentError flags set" in new DetailsTest {
        lazy val expected = VatDetailsViewModel(
          None, None, Some(entityName), currentYear, returnObligationError = true, paymentError = true
        )
        lazy val result: VatDetailsViewModel = target().constructViewModel(
          Left(ObligationsError),
          Left(NextPaymentError),
          Right(customerInformation),
          Right(validMandationStatus)
        )

        result shouldBe expected
      }
    }

    "the obligation is overdue" should {

      "return a VatDetailsViewModel with the return overdue flag set" in new DetailsTest {
        val overdueObligationDueDate: Option[String] = Some("2017-06-06")
        override val obligations: VatReturnObligations = overdueObligations

        lazy val expected = VatDetailsViewModel(
          paymentDueDate, overdueObligationDueDate, Some(entityName), currentYear, returnObligationOverdue = true
        )
        lazy val result: VatDetailsViewModel = target().constructViewModel(
          Right(Some(obligations)),
          Right(Some(payments)),
          Right(customerInformation),
          Right(validMandationStatus)
        )

        result shouldBe expected
      }
    }

    "the payment is overdue" should {

      "return a VatDetailsViewModel with the payment overdue flag set" in new DetailsTest {
        val overduePaymentDueDate: Option[String] = Some("2017-03-03")
        override val payments: Payments = overduePayment

        lazy val expected = VatDetailsViewModel(
          overduePaymentDueDate, obligationData, Some(entityName), currentYear, paymentOverdue = true
        )
        lazy val result: VatDetailsViewModel = target().constructViewModel(
          Right(Some(obligations)),
          Right(Some(payments)),
          Right(customerInformation),
          Right(validMandationStatus)
        )

        result shouldBe expected
      }
    }
  }

  "Calling .getObligationFlags" when {

    "there is a single due obligation" should {

      "return a VatDetailsDataModel with the correct data" in new DetailsTest {

        val expected = VatDetailsDataModel(
          Some("2019-06-06"),
          hasMultiple = false,
          isOverdue = false,
          hasError = false
        )

        val result: VatDetailsDataModel = target().getObligationFlags(obligations.obligations)
        result shouldBe expected
      }
    }

    "there is a single overdue obligation" should {

      "return a VatDetailsDataModel with the overdue flag set" in new DetailsTest {

        val expected = VatDetailsDataModel(
          Some("2017-06-06"),
          hasMultiple = false,
          isOverdue = true,
          hasError = false
        )

        val result: VatDetailsDataModel = target().getObligationFlags(overdueObligations.obligations)
        result shouldBe expected
      }
    }

    "there are multiple obligations" should {

      "return a VatDetailsDataModel with the hasMultiple flag set" in new DetailsTest {

        val multipleObligations = Seq(
          VatReturnObligation(
            LocalDate.parse("2019-04-04"),
            LocalDate.parse("2019-05-05"),
            LocalDate.parse("2019-06-06"),
            "O",
            None,
            "#001"
          ),
          VatReturnObligation(
            LocalDate.parse("2020-04-04"),
            LocalDate.parse("2020-05-05"),
            LocalDate.parse("2020-06-06"),
            "O",
            None,
            "#001"
          )
        )

        val expected = VatDetailsDataModel(
          Some("2"),
          hasMultiple = true,
          isOverdue = false,
          hasError = false
        )

        val result: VatDetailsDataModel = target().getObligationFlags(multipleObligations)
        result shouldBe expected
      }
    }
  }

  "Calling .retrieveMandationStatus" should {
    "return a mandation status" when {
      "it is available in session" in new DetailsTest {
        implicit val fakeRequestWithSession: FakeRequest[AnyContentAsEmpty.type] = FakeRequest().withSession(
          "mtdVatMandationStatus" -> Json.stringify(Json.toJson(MandationStatus("Non MTDfB")))
        )

        val result: Future[HttpGetResult[MandationStatus]] = target(false).retrieveMandationStatus("111111111")(fakeRequestWithSession)

        await(result) shouldBe Right(MandationStatus("Non MTDfB"))
      }
      "it is needs to be collected from the mandation service" in new DetailsTest {
        implicit val fakeRequestWithEmptySession: FakeRequest[AnyContentAsEmpty.type] = FakeRequest().withSession()

        override def setup(needMandationCall: Boolean): Any = {
          (mockMandationService.getMandationStatus(_: String)(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *)
            .returning(Future.successful(Right(validNonMTDfBMandationStatus))).once()
        }

        val result: Future[HttpGetResult[MandationStatus]] = target().retrieveMandationStatus("111111111")(fakeRequestWithEmptySession)

        await(result) shouldBe Right(MandationStatus("Non MTDfB"))
      }
    }
    "return a HTTP error" when {
      "one is received from the mandation service layer" in new DetailsTest {
        (mockMandationService.getMandationStatus(_: String)(_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *)
          .returning(Future.successful(Left(BadRequestError("", ""))))

        val result: Future[HttpGetResult[MandationStatus]] = target(false).retrieveMandationStatus("111111111")(fakeRequest)
        await(result) shouldBe Left(BadRequestError("", ""))
      }
    }
  }
}
