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

* `FieldTag` `type="integer"` truncates doubles with a `(long)` cast instead of rounding, so the
  aged-claims reserve totals render as `23586`, never `23587` (`FieldTag.java:60-66`).
* The aged-claims screen renders reserve *money* totals through the integer formatter
  (`agedClaims.jsp`), so cents are dropped entirely on that report.
* The loss ratio is rendered through the money formatter (`String.format("%.2f")`) even though it
  is a ratio, not a currency amount (`lossRatio.jsp`, `FieldTag.java:52-58`).
* Loss-ratio premium totals include join fan-out: `sum(p.annual_premium)` is computed over the
  `POLICY left join CLAIM left join PAYMENT` row set, so a policy's premium is counted once per
  claim/payment row (`ReportDAO.java:55-87`). AUTO shows `91400.00`, not the sum of distinct AUTO
  premiums.
* A zero premium yields a `0` loss ratio instead of a division error (`ReportDAO.java:78`).
* Report aggregates are read and summed as `double`s, including row counts
  (`ReportDAO.java:42-43,120-121`).
* Reports run against the fixed `report.asof.date` (`2019-04-01`), never the wall clock
  (`northstar.properties:2`, `ReportDAO.java:98`).
* `ReportDAO.agedClaims` concatenated the as-of date into the SQL; the extracted service applies
  it as a parameter in Java (sanctioned, observably identical). `ReportDAO.claimCounts` has a
  string-concatenated `ORDER BY` but no callers; reported as dead code, not migrated.
