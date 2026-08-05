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

## Settlement module

* Settlement money math is `double` with `Math.round(amount * 100.0) / 100.0`, so a covered amount of `1.005` pays `1.00`, not BigDecimal's `1.01` (`SettlementCalculator.java:38`).
* A blank deductible is coerced to `"0"` before parsing; an unparseable non-blank deductible throws (`SettlementCalculateAction.java:36-38`, `SettlementCalculator.java:27-30`).
* A deductible larger than the loss floors the settlement at zero (`SettlementCalculator.java:33-35`).
* Settlements above the policy limit are capped and flagged `cappedAtLimit` (`SettlementCalculator.java:36-37`).
* Blank or unparseable `claimId` falls back to claim `119`; blank `coveredAmount` defaults to `5000`; blank `depreciation` defaults to `0` (`SettlementCalculateAction.java:24-34`, `ClaimsActionSupport.java:83-97`).
* A missing claim or policy falls back to a `10000` policy limit on calculate (`SettlementCalculateAction.java:26-32`).
* Saves stamp the recorded session user (`supervisor`) and a fixed audit date `2019-04-01` (`SettlementSaveAction.java:39-41`).
* Payments are issued with a fixed date `2019-04-03`, status `ISSUED`, and a check number derived from the allocated id (`CHK-<paymentId>`) (`PaymentIssueAction.java:34-37`).
* A blank payment amount defaults to the latest settlement amount for the claim (`PaymentIssueAction.java:31-33`).
* IDs are allocated as `max(id) + 1` (`ClaimsActionSupport.java:113-127`).
* Payment history with a blank `claimId` falls back to claim `119` (`PaymentHistoryAction.java:22`).
