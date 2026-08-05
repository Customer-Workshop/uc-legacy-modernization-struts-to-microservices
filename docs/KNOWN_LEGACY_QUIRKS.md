# Known legacy quirks

These behaviors are intentionally preserved from the legacy source.

* `ClaimsActionSupport.integer` and `decimal` catch conversion failures and return a fallback (`ClaimsActionSupport.java:32-46`).
* `ClaimsActionSupport.normalizedDate` uses lenient `SimpleDateFormat`, so `02/30/2019` becomes `2019-03-02` (`ClaimsActionSupport.java:48-59`).
* Blank or unparseable dates default to `2019-04-01` (`ClaimsActionSupport.java:48-59`).
* FNOL hardcodes policy `9001`, loss type `WATER`, adjuster `adjuster1`, and reporter `supervisor` (`IntakeSubmitAction.java:37-45`).
* FNOL validation preserves claimant-before-description-before-date order and does not require loss date (`IntakeSubmitAction.java:23-35`, `validation.xml`).
* Login uses exact supervisor credentials or prefix matching for adjusters (`LoginAction.java:18-25`).
* `FieldTag` renders money with two decimal places (`FieldTag.java:789-800`).
* Empty policy line defaults to `AUTO` (`PolicySearchAction.java:20-23`).
* Invalid policy IDs fall back to policy 1 (`PolicyViewAction.java:20-22`).
* Settlement money math is `double` with `Math.round(amount * 100.0) / 100.0`, so a covered amount of `1.005` pays `1.00`, not the `1.01` BigDecimal half-up would give (`SettlementCalculator.java:36-38`).
* Blank deductibles are treated as `"0"` before calculation (`SettlementCalculateAction.java:36-38`).
* A settlement below zero after deductible floors at `0`; above the policy limit it caps at the limit with `cappedAtLimit` set (`SettlementCalculator.java:31-37`).
* Settlement and payment actions fall back to claim `119` for a blank or garbled `claimId`, covered amount `5000`, depreciation `0`, and policy limit `10000` when the claim or policy is missing (`SettlementCalculateAction.java:24-33`).
* `PaymentDetailAction` falls back to payment `61` for an invalid `paymentId` (`PaymentDetailAction.java:18`).
* Saves stamp `calculated_by` from the session operator (`supervisor` in the recordings) and the fixed date `2019-04-01`; issued payments stamp `2019-04-03`, status `ISSUED`, and check number `CHK-<paymentId>` (`SettlementSaveAction.java:37-40`, `PaymentIssueAction.java:26-36`).
* `PaymentIssueAction` defaults a blank or unparseable amount to the latest settlement amount for the claim (`PaymentIssueAction.java:30-32`).
* Settlement and payment IDs are allocated with `select coalesce(max(id),0)+1` (`ClaimsActionSupport.nextId`).
