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
* Settlement arithmetic subtracts depreciation before deductible, floors before capping, uses strict `>` for the cap, and rounds Java `double` values last (`SettlementCalculator.java:24-43`).
* Blank settlement deductibles become zero (`SettlementCalculateAction.java:34-37`, `SettlementSaveAction.java:29-33`).
* Calculate alone defaults a missing claim or policy limit to `10000`; save dereferences the claim and policy without that fallback (`SettlementCalculateAction.java:23-31`, `SettlementSaveAction.java:24-33`).
* Payment amount parsing falls back to the latest settlement amount, payment methods are stored verbatim, and issued checks use `CHK-<id>` (`PaymentIssueAction.java:22-35`).
* Settlement save uses the session operator and fixed date `2019-04-01` (`SettlementSaveAction.java:34-38`).
