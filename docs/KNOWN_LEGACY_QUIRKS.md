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
* Settlement arithmetic stays in `double` with `Math.round(amount * 100.0) / 100.0`, so a 1.005 covered amount pays 1.00 (binary double sits below the half cent); `BigDecimal` HALF_UP would pay 1.01 (`SettlementCalculator.java:37`).
* The same 1.005 covered amount *displays* as 1.01 because `FieldTag` money formatting (`String.format("%.2f")`) rounds the shortest decimal representation half-up (`FieldTag.java:52-58`).
* Blank deductible coerces to zero rather than validation-failing (`SettlementCalculator.java:26-29`).
* A deductible above the loss floors the settlement at 0.00 while still reporting the full deductible applied (`SettlementCalculator.java:32-34`).
* Settlements above the policy limit cap at the limit with `cappedAtLimit=true` (`SettlementCalculator.java:35-36`).
* Settlement/payment screens default missing or unparseable `claimId` to 119, covered amount to 5000, depreciation to 0, and a missing policy to a 10000 limit (`SettlementCalculateAction.java:23-33`).
* Save and issue hardcode operator `supervisor`, calculated date `2019-04-01`, and issued date `2019-04-03`; check numbers are `CHK-<paymentId>` (`SettlementSaveAction.java:36-38`, `PaymentIssueAction.java:33-35`).
* New settlement and payment IDs are `max(id)+1`, so the first issued payment after seeding is 61 (`ClaimsActionSupport.nextId`).
* A blank payment amount falls back to the claim's latest settlement amount (`PaymentIssueAction.java:30-31`).
