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
* Settlement claim IDs default to 119 and covered/depreciation values default to 5000/0 on conversion failure (`SettlementCalculateAction.java:24-34`).
* Settlement calculation treats blank deductible as zero (`SettlementCalculateAction.java:35-38`; `SettlementCalculator.java:27-30`).
* Settlement arithmetic floors negative post-deductible values at zero and caps strictly above the policy limit (`SettlementCalculator.java:31-37`).
* Settlement arithmetic uses binary-double `Math.round`, distinct from output formatting (`SettlementCalculator.java:37-38`; `FieldTag.java:789-800`).
* Saved settlements use the session operator and fixed calculated date `2019-04-01` (`SettlementSaveAction.java:80-84`).
* Settlement and payment IDs use max-plus-one allocation (`ClaimsActionSupport.java:61-72`).
* Payment claim ID and amount use 119/latest-settlement fallbacks; checks use `CHK-<paymentId>`, with fixed issue date and `ISSUED` status (`PaymentIssueAction.java:115-128`).
* Empty payment-history claim ID selects claim 119 (`PaymentHistoryAction.java:24-26`).
