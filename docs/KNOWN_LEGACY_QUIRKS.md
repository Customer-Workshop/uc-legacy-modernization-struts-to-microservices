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
* Settlement math uses `double` arithmetic with `Math.round(amount * 100.0) / 100.0`, so a covered amount of `1.005` settles at `1.00`, not `1.01` (`SettlementCalculator.java`, mirrored in `settlement-service/.../service/SettlementCalculator.java`).
* A blank deductible is coerced to `"0"` before calculation (`SettlementCalculateAction.java`, mirrored in `SettlementApplicationService.calculate`).
* A negative post-deductible amount floors to `0` with `cappedAtLimit=false`; the cap flag compares the pre-rounding amount to the policy limit (`SettlementCalculator.java`).
* Settlement/payment fallbacks: `claimId` defaults to `119`, covered amount to `5000`, depreciation to `0`, missing claim or policy to a `10000` limit, and payment amount to the latest settlement amount (`SettlementCalculateAction.java`, `PaymentIssueAction.java`, mirrored in `SettlementApplicationService`).
* Settlement save records operator `supervisor` and fixed date `2019-04-01`; payments use issued date `2019-04-03`, check number `CHK-<paymentId>`, and status `ISSUED` (`SettlementSaveAction.java`, `PaymentIssueAction.java`).
* IDs are generated with `coalesce(max(id),0)+1`, so after a reset the first issued payment is `61`/`CHK-61` (`ClaimsActionSupport.nextId`).
