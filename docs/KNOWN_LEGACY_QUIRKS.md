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
