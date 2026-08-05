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
* Workbench actions fall back to claim 119 for a missing or unparseable `claimId` (`ClaimsActionSupport.integer`, `Workbench*Action.java`).
* Workbench assign reads the `adjuster` request parameter (not the form's `assignedAdjuster` field) and defaults a blank value to `adjuster2` (`WorkbenchAssignAction.java:20-24`).
* Workbench status change defaults a blank status to `INVESTIGATING` (`WorkbenchStatusAction.java:20-24`).
* Workbench reserve change keeps `double` arithmetic and defaults an unparseable amount to `4500` (`WorkbenchReserveAction.java:19-21`).
* Workbench updates against a nonexistent claim are silent no-ops that still render the echoed value (`WorkbenchAssignAction.java`, `WorkbenchStatusAction.java`, `WorkbenchReserveAction.java`).
* Workbench notes default blank text to `Review completed` and are never persisted (`WorkbenchNoteAction.java:19-23`).
* Workbench status and reserve history screens always render empty lists even though history tables exist (`WorkbenchStatusHistoryAction.java`, `WorkbenchReserveHistoryAction.java`).
