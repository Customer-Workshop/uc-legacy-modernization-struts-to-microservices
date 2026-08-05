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

* `/workbench/*` action mappings (`WEB-INF/struts-config.xml`) become
  `WorkbenchController` under `/api/workbench/claims`:
  * `/workbench/list` -> `GET /api/workbench/claims` (OPEN claims plus `claimCount`)
  * `/workbench/view` -> `GET /api/workbench/claims/{claimId}` (also the `claim.<id>.<field>` parity probe endpoint; a missing claim is HTTP 404)
  * `/workbench/assign` -> `POST /api/workbench/claims/{claimId}/assign`
  * `/workbench/status` -> `POST /api/workbench/claims/{claimId}/status?status=...`
  * `/workbench/reserve` -> `POST /api/workbench/claims/{claimId}/reserve`
  * `/workbench/note` -> `POST /api/workbench/claims/{claimId}/note`
  * `/workbench/notes` -> `GET /api/workbench/claims/{claimId}/notes`
  * `/workbench/statusHistory` -> `GET /api/workbench/claims/{claimId}/status-history`
  * `/workbench/reserveHistory` -> `GET /api/workbench/claims/{claimId}/reserve-history`
* `WorkbenchForm` (all-`String` properties, no workbench validation rules) becomes
  string-typed request records; the Struts coercions (`ClaimsActionSupport.integer`
  / `decimal` fallbacks) become explicit methods in `WorkbenchApplicationService`.
* The string-concatenated `UPDATE CLAIM ...` statements in `WorkbenchAssignAction`,
  `WorkbenchStatusAction`, and `WorkbenchReserveAction` become parameterized JPA
  updates (the one sanctioned legacy change; transcripts prove equivalence).
* `ClaimDAO.findByStatus` / `NoteDAO.findByClaim` become `ClaimRepository` /
  `ClaimNoteRepository` Spring Data queries.
* The workbench schema seeds claims 121 and 122 in their recorded post-FNOL
  states because the golden transcripts were captured against one shared legacy
  database after the intake scenarios ran; each microservice owns an isolated
  schema copy of that recorded state (the same pattern intake uses for `policy`).
* Legacy `RESERVE_HISTORY` / `STATUS_HISTORY` tables are dead data: the history
  Actions never query them, so they are not migrated (reported, not deleted).

## Later extraction reference architecture

The reserved settlement service (8083) should use `controller`, `dto`,
`service`, `repository`, `model`, `config`, and `exception` packages; constructor
injection; `@ControllerAdvice`; Flyway `V1__schema.sql` and `V2__seed.sql`;
and `/internal/reset` plus read endpoints for parity probes. Add its status and
routes/probes to `parity/routes.yaml` only; no harness Python changes should be
needed.
