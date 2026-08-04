# Struts to Spring mapping

* `ActionForm` (`web/form/*Form.java`) becomes a validated JSON DTO record.
* Struts action mappings (`WEB-INF/struts-config.xml`) become `@RequestMapping` controller methods.
* `ActionForward` is represented by HTTP status and JSON response; the parity harness maps successful forwards/redirects to 2xx and validation forwards to 4xx.
* `validate()` and `validation.xml` become explicit ordered service validation while preserving resource keys.
* `ActionMessages` becomes `{ "validationErrors": [...] }`.
* `PolicyDAO` JDBC reads become `PolicyRepository` Spring Data queries.
* `PolicyManager.getInstance()` becomes constructor-injected `@Service`.
* JSP `FieldTag` semantic output becomes JSON, with money normalized to two decimal places.

## Settlement/payment mappings

The extracted settlement service (8083) uses `controller`, `dto`, `service`,
`repository`, `model`, `config`, and `exception` packages; constructor
injection; `@ControllerAdvice`; Flyway `V1__schema.sql` and `V2__seed.sql`;
and `/internal/reset` plus read endpoints for parity probes.

| Legacy action | Spring endpoint | Persistence |
|---|---|---|
| SettlementCalculateAction | POST `/api/settlements/calculate` | SettlementCalculator + claim/policy |
| SettlementSaveAction | POST `/api/settlements` | SettlementRepository |
| SettlementDetailAction | GET `/api/settlements/detail` | SettlementRepository |
| PaymentIssueAction | POST `/api/payments` | PaymentRepository + latest Settlement |
| PaymentHistoryAction | GET `/api/payments` | PaymentRepository |
| PaymentDetailAction | GET `/api/payments/{id}` | PaymentRepository |
| PaymentRemittanceAction | GET `/api/payments/remittance` | PaymentRepository |

The legacy `SettlementService.calculateAndSave` was dead code: no in-scope
Action referenced it, so it was not migrated. Settlement status and routes/probes
are declared in `parity/routes.yaml`; no scenario-specific harness logic is
needed.
