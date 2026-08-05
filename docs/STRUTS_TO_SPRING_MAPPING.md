# Struts to Spring mapping

* `ActionForm` (`web/form/*Form.java`) becomes a validated JSON DTO record.
* Struts action mappings (`WEB-INF/struts-config.xml`) become `@RequestMapping` controller methods.
* `ActionForward` is represented by HTTP status and JSON response; the parity harness maps successful forwards/redirects to 2xx and validation forwards to 4xx.
* `validate()` and `validation.xml` become explicit ordered service validation while preserving resource keys.
* `ActionMessages` becomes `{ "validationErrors": [...] }`.
* `PolicyDAO` JDBC reads become `PolicyRepository` Spring Data queries.
* `PolicyManager.getInstance()` becomes constructor-injected `@Service`.
* JSP `FieldTag` semantic output becomes JSON, with money normalized to two decimal places.

## Workbench module

* `WorkbenchAssignAction` (`/workbench/assign.do`) becomes `POST /api/workbench/claims/{claimId}/assign` on the workbench service (8084); the request body carries `adjuster`, matching the parameter the legacy Action actually reads.
* `WorkbenchStatusAction` (`/workbench/status.do?status=...`) becomes `POST /api/workbench/claims/{claimId}/status?status=...`.
* `WorkbenchReserveAction` (`/workbench/reserve.do`) becomes `POST /api/workbench/claims/{claimId}/reserve` with `reserveAmount` in the body.
* `claimId` stays a string path variable so the legacy fallback-to-119 coercion is preserved instead of a framework 400.
* The string-concatenated `update CLAIM set ...` statements become parameterized Spring Data `@Modifying` queries (the sanctioned injection fix; observably identical).
* The workbench service shares the `intake` schema (same `claim` table) because legacy workbench actions mutate claims created by FNOL; it runs with Flyway disabled and relies on the intake service's migrations. Cross-service data ownership is a follow-up architectural decision.
* `GET /api/workbench/claims/{id}` plus `/internal/reset` support parity probes and deterministic reseeding.

## Later extraction reference architecture

The reserved settlement service (8083) should use `controller`, `dto`,
`service`, `repository`, `model`, `config`, and `exception` packages; constructor
injection; `@ControllerAdvice`; Flyway `V1__schema.sql` and `V2__seed.sql`;
and `/internal/reset` plus read endpoints for parity probes. Add its status and
routes/probes to `parity/routes.yaml` only; no harness Python changes should be
needed.
