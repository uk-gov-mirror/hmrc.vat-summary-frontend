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

package models.payments

import play.api.Logger
import play.api.libs.json.Reads._
import play.api.libs.json._

sealed trait ChargeType {
  val value: String
  override def toString: String = value
}
case object ReturnCharge extends ChargeType {
  override val value = "VAT Return Charge"
}
case object ReturnDebitCharge extends ChargeType {
  override val value: String = "VAT Return Debit Charge"
}
case object ReturnCreditCharge extends ChargeType {
  override val value: String = "VAT Return Credit Charge"
}
case object OACharge extends ChargeType {
  override val value: String = "VAT Officer's Assessment"
}
case object OACreditCharge extends ChargeType {
  override val value: String = "VAT OA Credit Charge"
}
case object OADebitCharge extends ChargeType {
  override val value: String = "VAT OA Debit Charge"
}
case object OADefaultInterestCharge extends ChargeType {
  override val value: String = "VAT OA Default Interest"
}
case object OAFurtherInterestCharge extends ChargeType {
  override val value: String = "VAT OA Further Interest"
}
case object DefaultSurcharge extends ChargeType {
  override val value: String = "VAT Default Surcharge"
}
case object CentralAssessmentCharge extends ChargeType {
  override val value: String = "VAT Central Assessment"
}
case object ErrorCorrectionCharge extends ChargeType {
  override val value: String = "VAT Error Correction"
}
case object ErrorCorrectionCreditCharge extends ChargeType {
  override val value: String = "VAT EC Credit Charge"
}
case object ErrorCorrectionDebitCharge extends ChargeType {
  override val value: String = "VAT EC Debit Charge"
}
case object RepaymentSupplement extends ChargeType {
  override val value: String = "VAT Repayment Supplement"
}
case object RepaySupplement extends ChargeType {
  override val value: String = "VAT Repay Supplement"
}
case object AAInterestCharge extends ChargeType {
  override val value: String = "VAT AA Default Interest"
}
case object AAFurtherInterestCharge extends ChargeType {
  override val value: String = "VAT AA Further Interest"
}
case object AACharge extends ChargeType {
  override val value: String = "VAT Additional Assessment"
}
case object BnpRegPre2010Charge extends ChargeType {
  override val value: String = "VAT BNP of Reg Pre 2010"
}
case object BnpRegPost2010Charge extends ChargeType {
  override val value: String = "VAT BNP of Reg Post 2010"
}
case object FtnMatPre2010Charge extends ChargeType {
  override val value: String = "VAT FTN Mat Change Pre 2010"
}
case object FtnMatPost2010Charge extends ChargeType {
  override val value: String = "VAT FTN Mat Change Post 2010"
}
case object FtnEachPartnerCharge extends ChargeType {
  override val value: String = "VAT FTN Each Partner"
}
case object MiscPenaltyCharge extends ChargeType {
  override val value: String = "VAT Miscellaneous Penalty"
}
case object MpPre2009Charge extends ChargeType {
  override val value: String = "VAT MP pre 2009"
}
case object MpRepeatedPre2009Charge extends ChargeType {
  override val value: String = "VAT MP (R) pre 2009"
}
case object CivilEvasionPenaltyCharge extends ChargeType {
  override val value: String = "VAT Civil Evasion Penalty"
}
case object VatOAInaccuraciesFrom2009 extends ChargeType {
  override val value: String = "VAT OA Inaccuracies from 2009"
}
case object InaccuraciesAssessmentsPenCharge extends ChargeType {
  override val value: String = "VAT Inaccuracy Assessments pen"
}
case object InaccuraciesReturnReplacedCharge extends ChargeType {
  override val value: String = "VAT Inaccuracy rturn replaced"
}
case object WrongDoingPenaltyCharge extends ChargeType {
  override val value: String = "VAT Wrong Doing Penalty"
}
case object CarterPenaltyCharge extends ChargeType {
  override val value: String = "VAT Carter Penalty"
}
case object FailureToNotifyRCSLCharge extends ChargeType {
  override val value: String = "VAT FTN RCSL"
}
case object FailureToSubmitRCSLCharge extends ChargeType {
  override val value: String = "VAT Failure to submit RCSL"
}
case object VatInaccuraciesInECSalesCharge extends ChargeType {
  override val value: String = "VAT Inaccuracies in EC Sales"
}
case object VatECDefaultInterestCharge extends ChargeType {
  override val value: String = "VAT EC Default Interest"
}
case object VatECFurtherInterestCharge extends ChargeType {
  override val value: String = "VAT EC Further Interest"
}
case object VatSecurityDepositRequestCharge extends ChargeType {
  override val value: String = "VAT Security Deposit Request"
}
case object VatProtectiveAssessmentCharge extends ChargeType {
  override val value: String = "VAT Protective Assessment"
}
case object VatPADefaultInterestCharge extends ChargeType {
  override val value: String = "VAT PA Default Interest"
}

case object VatFailureToSubmitECSalesCharge extends ChargeType {
  override val value: String = "VAT Failure to Submit EC Sales"
}
case object VatOfficersAssessmentFurtherInterestCharge extends ChargeType {
  override val value: String = "VAT OA Further Interest"
}
case object StatutoryInterestCharge extends ChargeType {
  override val value: String = "VAT Statutory Interest"
}

object ChargeType {

  val logger = Logger(getClass.getSimpleName)

  val allChargeTypes: Set[ChargeType] = Set(
    ReturnCharge,
    ReturnDebitCharge,
    ReturnCreditCharge,
    OACharge,
    OACreditCharge,
    OADebitCharge,
    DefaultSurcharge,
    CentralAssessmentCharge,
    ErrorCorrectionCharge,
    ErrorCorrectionCreditCharge,
    ErrorCorrectionDebitCharge,
    RepaymentSupplement,
    RepaySupplement,
    OADefaultInterestCharge,
    AACharge,
    AAInterestCharge,
    AAFurtherInterestCharge,
    BnpRegPre2010Charge,
    BnpRegPost2010Charge,
    FtnEachPartnerCharge,
    FtnMatPost2010Charge,
    FtnMatPre2010Charge,
    MiscPenaltyCharge,
    MpPre2009Charge,
    MpRepeatedPre2009Charge,
    CivilEvasionPenaltyCharge,
    VatOAInaccuraciesFrom2009,
    InaccuraciesAssessmentsPenCharge,
    InaccuraciesReturnReplacedCharge,
    WrongDoingPenaltyCharge,
    CarterPenaltyCharge,
    FailureToNotifyRCSLCharge,
    FailureToSubmitRCSLCharge,
    VatInaccuraciesInECSalesCharge,
    VatFailureToSubmitECSalesCharge,
    VatOfficersAssessmentFurtherInterestCharge,
    StatutoryInterestCharge,
    VatPADefaultInterestCharge,
    VatProtectiveAssessmentCharge,
    VatSecurityDepositRequestCharge,
    VatECFurtherInterestCharge,
    VatECDefaultInterestCharge
  )

  def apply: String => ChargeType = input => {
    allChargeTypes.find { chargeType =>
      chargeType.value.toUpperCase.equals(input.trim.toUpperCase)
    }.getOrElse(throw new IllegalArgumentException("Invalid Charge Type"))

  }

  def unapply(arg: ChargeType): String = arg.value

  def isValidChargeType(input: String): Boolean = {
    try {
      ChargeType.apply(input)
      true
    } catch {
      case t: IllegalArgumentException =>
        logger.info(s"""Invalid Charge Type - Received "$input"""", t)
        false
    }
  }

  implicit val reads: Reads[ChargeType] = __.read[String].map(apply)

  implicit val writes: Writes[ChargeType] = Writes { charge => JsString(charge.value) }

}