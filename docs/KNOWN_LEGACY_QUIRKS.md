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
* Settlement math runs in `double` with `Math.round(amount * 100.0) / 100.0`, so a covered
  amount of `1.005` settles at `1.00`, not the `1.01` exact decimal arithmetic would pay
  (`SettlementCalculator.java:26-45`).
* Blank deductibles coerce to `0` and a deductible above the loss floors the settlement at
  `0.00` (`SettlementCalculator.java:28-36`).
* Settlement and payment requests default a missing/unparseable `claimId` to `119`, covered
  amount to `5000`, and depreciation to `0` (`SettlementCalculateAction.java:24-33`).
* Calculate falls back to a `10000` policy limit when the claim or policy is missing
  (`SettlementCalculateAction.java:26-32`).
* Saves stamp `calculated_by` from the session operator (`supervisor`) and the fixed date
  `2019-04-01`; issued payments stamp `2019-04-03`, `CHK-<paymentId>` check numbers, and
  `ISSUED` status (`SettlementSaveAction.java:36-39`, `PaymentIssueAction.java:27-37`).
* A blank payment amount defaults to the latest settlement amount for the claim
  (`PaymentIssueAction.java:31-33`).
* Ids are allocated as `max(id)+1` on the seeded tables, so the first issued payment is `61`
  (`ClaimsActionSupport.nextId`).
* Invalid policy IDs fall back to policy 1 (`PolicyViewAction.java:20-22`).
