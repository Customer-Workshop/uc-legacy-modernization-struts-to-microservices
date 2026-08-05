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

* `OpenReportAction` (`/report/openByAdjuster.do`) becomes `GET /api/reports/open-claims-by-adjuster` on the reporting service (8085).
* `LossRatioReportAction` (`/report/lossRatio.do`) becomes `GET /api/reports/loss-ratio`.
* `AgedClaimsReportAction` (`/report/agedClaims.do`) becomes `GET /api/reports/aged-claims`.
* `ReportDAO` raw-map rows become typed DTO records (`OpenClaimsRow`, `LossRatioRow`, `AgedClaimsRow`); the aggregate SQL is carried over verbatim in `ReportRepository`, except the aged-claims as-of date, which moves from string concatenation to a bind parameter (observably identical).
* JSP `FieldTag` integer rendering (`(long) Double.parseDouble`) becomes explicit truncation in `ReportApplicationService`; money rendering is normalized by the parity harness.
* The fixed `report.asof.date` (2019-04-01) from `northstar.properties` becomes the `report.asof.date` Spring property with the same default.
* Reports are read-only, so `/internal/reset` is intentionally idempotent, matching the policy service convention.

## Later extraction reference architecture

The reserved settlement service (8083) should use `controller`, `dto`,
`service`, `repository`, `model`, `config`, and `exception` packages; constructor
injection; `@ControllerAdvice`; Flyway `V1__schema.sql` and `V2__seed.sql`;
and `/internal/reset` plus read endpoints for parity probes. Add its status and
routes/probes to `parity/routes.yaml` only; no harness Python changes should be
needed.
