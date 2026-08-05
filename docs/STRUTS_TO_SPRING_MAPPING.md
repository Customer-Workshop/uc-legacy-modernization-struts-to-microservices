# Struts to Spring mapping

* `ActionForm` (`web/form/*Form.java`) becomes a validated JSON DTO record.
* Struts action mappings (`WEB-INF/struts-config.xml`) become `@RequestMapping` controller methods.
* `ActionForward` is represented by HTTP status and JSON response; the parity harness maps successful forwards/redirects to 2xx and validation forwards to 4xx.
* `validate()` and `validation.xml` become explicit ordered service validation while preserving resource keys.
* `ActionMessages` becomes `{ "validationErrors": [...] }`.
* `PolicyDAO` JDBC reads become `PolicyRepository` Spring Data queries.
* `PolicyManager.getInstance()` becomes constructor-injected `@Service`.
* JSP `FieldTag` semantic output becomes JSON, with money normalized to two decimal places.

## Later extraction reference architecture

Extracted services use `controller`, `dto`, `service`, `repository`, `model`,
and `exception` packages; constructor injection; `@ControllerAdvice`; Flyway
`V1__schema.sql` and `V2__seed.sql`; and `/internal/reset` plus read endpoints
for parity probes. Add each module's status and routes/probes to
`parity/routes.yaml`; no scenario-specific harness Python changes are needed.

## Settlement module mapping

| Legacy | Spring |
|---|---|
| `SettlementCalculateAction` (`/settlement/calculate.do`) | `POST /api/settlements/calculate` (`SettlementController.calculate`) |
| `SettlementSaveAction` (`/settlement/save.do`) | `POST /api/settlements` (`SettlementController.save`) |
| `PaymentIssueAction` (`/payment/issue.do`) | `POST /api/payments` (`PaymentController.issue`) |
| `PaymentHistoryAction` (`/payment/history.do`) | `GET /api/payments?claimId=` (`PaymentController.history`) |
| `SettlementCalculator` (double + `Math.round`) | `LegacySettlementCalculator` (arithmetic preserved) |
| `SettlementDAO` / `PaymentDAO` JDBC | `SettlementRepository` / `PaymentRepository` Spring Data queries |
| `ClaimsActionSupport.integer`/`decimal` fallbacks | explicit coercions in `SettlementApplicationService` |
| Request-parameter form population | String-typed JSON DTO records (`SettlementRequest`, `PaymentRequest`) |

Parity probes read `GET /api/settlements/latest-by-claim`, `GET /api/payments/{id}`,
and `GET /api/payments/count`; `POST /internal/reset` restores the deterministic seed.
