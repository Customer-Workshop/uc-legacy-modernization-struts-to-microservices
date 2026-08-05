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

## Reporting module

* The reporting calendar is pinned to `report.asof.date` = `2019-04-01`; aged buckets never move with the real clock (`ReportDAO.java:98`).
* Loss ratio joins `POLICY left join CLAIM left join PAYMENT` and sums over the fanned-out rows, so `annual_premium` is counted once per claim/payment row and `reserve_amount` once per payment row — not once per policy (`ReportDAO.java:63-69`).
* A business line with zero premium reports loss ratio `0` instead of failing on division by zero (`ReportDAO.java:78`).
* Loss ratio excludes no claim statuses: DENIED and CLOSED claims still contribute reserve and payments (`ReportDAO.java:63-69`).
* The aged-claims report renders reserve totals through the integer `FieldTag` formatter, truncating fractional cents rather than rounding (`agedClaims.jsp`, `FieldTag.java` integer branch).
* Counts are read as doubles and rendered via `(long) Double.parseDouble(...)` truncation (`ReportDAO.java:42,120`, `FieldTag.java`).
* Aged buckets use `<=30/<=60/<=90` day boundaries against the reported date, exclude CLOSED and DENIED, and sort by the bucket label text (`ReportDAO.java:99-116`).
* Open-claims report counts only OPEN and INVESTIGATING statuses (`ReportDAO.java:37`).
