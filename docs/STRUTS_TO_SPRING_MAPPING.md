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

* `SettlementCalculateAction` (`/settlement/calculate.do`) becomes `POST /api/settlements/calculate`.
* `SettlementSaveAction` (`/settlement/save.do`) becomes `POST /api/settlements` (persists via `SettlementRepository`).
* `SettlementDetailAction` (`/settlement/detail.do`) becomes `GET /api/settlements/{id}`; the latest-per-claim view used by parity probes is `GET /api/settlements/claim`.
* `PaymentIssueAction` (`/payment/issue.do`) becomes `POST /api/payments`.
* `PaymentHistoryAction` (`/payment/history.do`) becomes `GET /api/payments?claimId=`.
* `PaymentDetailAction` (`/payment/detail.do`) becomes `GET /api/payments/{id}`; `GET /api/payments/count` serves the per-claim payment-count probe. `PaymentRemittanceAction` is covered by the same list/read endpoints.
* `SettlementForm`/`PaymentForm` (String-typed) become `SettlementCalcRequest`/`PaymentIssueRequest` records that keep String fields so the Struts coercions stay explicit (`LegacyCoercions`).
* `SettlementCalculator.getInstance()` singleton becomes the static, side-effect-free `service/SettlementCalculator` with the double arithmetic preserved value-for-value.
* `SettlementDAO`/`PaymentDAO` JDBC (including `nextId` max+1 key generation) become Spring Data repositories with `coalesce(max(id),0)+1` queries; every legacy statement was already parameterized except `nextId`'s concatenated table name, which is now a fixed JPQL query.
* JSP money rendering (`FieldTag type="money"`, `String.format("%.2f")`) becomes `LegacyMoney.format`.
* The service keeps a local Flyway-seeded read model of `claim`/`policy` (id and
  policy limit only) for limit lookups. Claims created through
  claims-intake-service after seeding (ids 121+) are not synchronized into it,
  so they cannot yet be settled; wiring a claim/policy sync or service-to-service
  lookup is deferred to the workbench/reporting extractions, which own the
  cross-module claim lifecycle.

## Later extraction reference architecture

The reserved settlement service (8083) should use `controller`, `dto`,
`service`, `repository`, `model`, `config`, and `exception` packages; constructor
injection; `@ControllerAdvice`; Flyway `V1__schema.sql` and `V2__seed.sql`;
and `/internal/reset` plus read endpoints for parity probes. Add its status and
routes/probes to `parity/routes.yaml` only; no harness Python changes should be
needed.
