# Struts to Spring mapping

* `ActionForm` (`web/form/*Form.java`) becomes a validated JSON DTO record.
* Struts action mappings (`WEB-INF/struts-config.xml`) become `@RequestMapping` controller methods.
* `ActionForward` is represented by HTTP status and JSON response; the parity harness maps successful forwards/redirects to 2xx and validation forwards to 4xx.
* `validate()` and `validation.xml` become explicit ordered service validation while preserving resource keys.
* `ActionMessages` becomes `{ "validationErrors": [...] }`.
* `PolicyDAO` JDBC reads become `PolicyRepository` Spring Data queries.
* `PolicyManager.getInstance()` becomes constructor-injected `@Service`.
* JSP `FieldTag` semantic output becomes JSON, with money normalized to two decimal places.

## Settlement module (settlement-service, 8083)

* `SettlementCalculateAction` (`/claims/settlement/calculate.do`) becomes `POST /api/settlements/calculate`.
* `SettlementSaveAction` (`/claims/settlement/save.do`) becomes `POST /api/settlements`; `savedBy` carries the legacy `calculatedBy`.
* `SettlementDetailAction` becomes `GET /api/settlements/detail?claimId=` plus `GET /api/settlements/by-claim` (latest settlement per claim, used by parity probes).
* `PaymentIssueAction` (`/claims/payment/issue.do`) becomes `POST /api/payments`.
* `PaymentHistoryAction` (`/claims/payment/history.do`) becomes `GET /api/payments?claimId=`.
* `PaymentDetailAction` becomes `GET /api/payments/{id}`; `GET /api/payments/count` serves the parity count probe.
* `PaymentRemittanceAction` becomes `GET /api/payments/remittance?claimId=` (payments, count, and `PaymentDAO.totalIssued` total).
* `SettlementForm`/`PaymentForm` become String-typed `SettlementRequest`/`PaymentRequest` records; the `ClaimsActionSupport.integer`/`decimal` fallbacks are applied explicitly in `SettlementApplicationService`.
* `SettlementCalculator.getInstance()` double arithmetic becomes an injected `SettlementCalculator` component that keeps the legacy `Math.round` cent rounding.
* `SettlementDAO`/`PaymentDAO` JDBC (including `nextId` max+1 allocation) become Spring Data repositories with `coalesce(max(id),0)+1` queries.
* `SettlementService.calculateAndSave` (operator `supervisor`, date `2019-03-01`) is dead code in the web flow and was not migrated; the actions' own path is authoritative.

## Later extraction reference architecture

The reserved settlement service (8083) should use `controller`, `dto`,
`service`, `repository`, `model`, `config`, and `exception` packages; constructor
injection; `@ControllerAdvice`; Flyway `V1__schema.sql` and `V2__seed.sql`;
and `/internal/reset` plus read endpoints for parity probes. Add its status and
routes/probes to `parity/routes.yaml` only; no harness Python changes should be
needed.
