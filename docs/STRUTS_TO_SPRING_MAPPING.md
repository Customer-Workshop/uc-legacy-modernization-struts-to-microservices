# Struts to Spring mapping

* `ActionForm` (`web/form/*Form.java`) becomes a validated JSON DTO record.
* Struts action mappings (`WEB-INF/struts-config.xml`) become `@RequestMapping` controller methods.
* `ActionForward` is represented by HTTP status and JSON response; the parity harness maps successful forwards/redirects to 2xx and validation forwards to 4xx.
* `validate()` and `validation.xml` become explicit ordered service validation while preserving resource keys.
* `ActionMessages` becomes `{ "validationErrors": [...] }`.
* `PolicyDAO` JDBC reads become `PolicyRepository` Spring Data queries.
* `PolicyManager.getInstance()` becomes constructor-injected `@Service`.
* JSP `FieldTag` semantic output becomes JSON, with money normalized to two decimal places.

## Reporting module

* `ReportIndexAction` (`/report/index`) becomes `GET /api/reports`.
* `OpenReportAction` (`/report/openByAdjuster`) becomes `GET /api/reports/open-by-adjuster`.
* `LossRatioReportAction` (`/report/lossRatio`) becomes `GET /api/reports/loss-ratio`.
* `AgedClaimsReportAction` (`/report/agedClaims`) becomes `GET /api/reports/aged-claims`.
* `ReportDAO` raw-map JDBC aggregates become `ReportRepository` (`JdbcTemplate`) queries; the
  aged-claims as-of date, previously concatenated into the SQL string, is applied in Java against
  the parameterless portable query (the sanctioned injection fix).
* `ReportFormatter`/`FieldTag` rendering (`type="money"` -> `%.2f`, `type="integer"` -> `(long)`
  truncation) becomes `LegacyReportFormat`, so the JSON business fields are the exact strings the
  legacy screens rendered.
* `report.asof.date` in `northstar.properties` becomes the `northstar.report.asof-date` property
  (fixed at `2019-04-01`).
* `ReportDAO.claimCounts(String orderBy)` (string-concatenated `ORDER BY`) has no callers in the
  legacy estate; it is dead code and was reported, not migrated.
* The reporting schema is a snapshot of the legacy database at transcript-recording time: the
  fixed seed plus claim 121 (fnol/workbench scenarios) and payment 61 (payment_issue scenario),
  because the report transcripts were captured after those mutating scenarios ran.

## Later extraction reference architecture

The reserved settlement service (8083) should use `controller`, `dto`,
`service`, `repository`, `model`, `config`, and `exception` packages; constructor
injection; `@ControllerAdvice`; Flyway `V1__schema.sql` and `V2__seed.sql`;
and `/internal/reset` plus read endpoints for parity probes. Add its status and
routes/probes to `parity/routes.yaml` only; no harness Python changes should be
needed.
