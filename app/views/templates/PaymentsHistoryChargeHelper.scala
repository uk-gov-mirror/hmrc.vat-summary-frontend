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

package views.templates

import common.FinancialTransactionsConstants

sealed case class PaymentsHistoryChargeHelper(name: String, title: String, description: String, id: String = "")

object PaymentsHistoryChargeHelper {
  object VatReturnCreditCharge extends PaymentsHistoryChargeHelper(
    FinancialTransactionsConstants.vatReturnCreditCharge,
    "paymentsHistory.vatReturnCreditChargeTitle",
    "paymentsHistory.vatReturnCreditChargeDescription")

  object VatReturnDebitCharge extends PaymentsHistoryChargeHelper(
    FinancialTransactionsConstants.vatReturnDebitCharge,
    "paymentsHistory.vatReturnDebitChargeTitle",
    "paymentsHistory.vatReturnDebitChargeDescription")

  object VatOfficerAssessmentCreditCharge extends PaymentsHistoryChargeHelper(
    FinancialTransactionsConstants.officerAssessmentCreditCharge,
    "paymentsHistory.officerAssessmentChargeTitle",
    "paymentsHistory.officerAssessmentCreditChargeDescription")

  object VatOfficerAssessmentDebitCharge extends PaymentsHistoryChargeHelper(
    FinancialTransactionsConstants.officerAssessmentDebitCharge,
    "paymentsHistory.officerAssessmentChargeTitle",
    "paymentsHistory.officerAssessmentDebitChargeDescription")

  object VatCentralAssessment extends PaymentsHistoryChargeHelper(
    FinancialTransactionsConstants.vatCentralAssessment,
    "paymentsHistory.vatCentralAssessmentTitle",
    "paymentsHistory.vatCentralAssessmentDescription")

  object VatDefaultSurcharge extends PaymentsHistoryChargeHelper(
    FinancialTransactionsConstants.vatDefaultSurcharge,
    "paymentsHistory.vatDefaultSurchargeTitle",
    "paymentsHistory.vatDefaultSurchargeDescription")

  object ErrorCorrectionDebitCharge extends PaymentsHistoryChargeHelper(
    FinancialTransactionsConstants.errorCorrectionDebitCharge,
    "paymentsHistory.vatErrorCorrectionDebitChargeTitle",
    "paymentsHistory.vatErrorCorrectionChargeDescription"
  )

  object ErrorCorrectionCreditCharge extends PaymentsHistoryChargeHelper(
    FinancialTransactionsConstants.errorCorrectionCreditCharge,
    "paymentsHistory.vatErrorCorrectionCreditChargeTitle",
    "paymentsHistory.vatErrorCorrectionChargeDescription"
  )

  object VatRepaymentSupplement extends PaymentsHistoryChargeHelper(
    FinancialTransactionsConstants.vatRepaymentSupplement,
    "paymentsHistory.vatRepaymentSupplementTitle",
    "paymentsHistory.vatRepaymentSupplementDescription",
    "repayment"
  )

  val values = Seq(VatReturnDebitCharge, VatReturnCreditCharge, VatOfficerAssessmentCreditCharge,
    VatOfficerAssessmentDebitCharge, VatCentralAssessment, VatDefaultSurcharge,
    ErrorCorrectionDebitCharge, ErrorCorrectionCreditCharge, VatRepaymentSupplement)

  def getChargeType(lookupName: String): Option[PaymentsHistoryChargeHelper] = {
    values.find(_.name == lookupName)
  }
}
