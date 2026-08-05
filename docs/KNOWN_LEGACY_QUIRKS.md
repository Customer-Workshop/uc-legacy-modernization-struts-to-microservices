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
* Settlement math is `double` arithmetic rounded with `Math.round(amount * 100.0) / 100.0`, so a covered amount of `1.005` settles at `1.00`, not the `1.01` BigDecimal HALF_UP would pay (`SettlementCalculator.java:24-46`).
* A blank deductible is coerced to zero before calculation (`SettlementCalculator.java:26-29`, `SettlementCalculateAction.java:35-37`).
* Settlement screens default `claimId` to `119`, `coveredAmount` to `5000`, and `depreciation` to `0` when parameters are missing or unparseable (`SettlementCalculateAction.java:23-33`, `PaymentIssueAction.java:22`).
* A missing claim or policy on the calculate screen falls back to a `10000` policy limit (`SettlementCalculateAction.java:25-31`).
* Saved settlements stamp the fixed date `2019-04-01`; issued payments stamp `2019-04-03`, check number `CHK-<paymentId>`, and status `ISSUED` (`SettlementSaveAction.java:38`, `PaymentIssueAction.java:33-35`).
* A payment with no `amount` defaults to the latest settlement amount for the claim (`PaymentIssueAction.java:30-31`).
* IDs are allocated with `select coalesce(max(id),0)+1` on the target table (`ClaimsActionSupport.nextId`).
