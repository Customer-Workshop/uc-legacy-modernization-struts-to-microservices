# Struts to Spring mapping

* `ActionForm` (`web/form/*Form.java`) becomes a validated JSON DTO record.
* Struts action mappings (`WEB-INF/struts-config.xml`) become `@RequestMapping` controller methods.
* `ActionForward` is represented by HTTP status and JSON response; the parity harness maps successful forwards/redirects to 2xx and validation forwards to 4xx.
* `validate()` and `validation.xml` become explicit ordered service validation while preserving resource keys.
* `ActionMessages` becomes `{ "validationErrors": [...] }`.
* `PolicyDAO` JDBC reads become `PolicyRepository` Spring Data queries.
* `PolicyManager.getInstance()` becomes constructor-injected `@Service`.
* JSP `FieldTag` semantic output becomes JSON, with money normalized to two decimal places.

## Settlement and payment mappings

* `SettlementCalculateAction` → `POST /api/settlements/calculate`
* `SettlementSaveAction` → `POST /api/settlements`
* `SettlementDetailAction` → `GET /api/settlements/claims/{claimId}`
* `PaymentIssueAction` → `POST /api/payments`
* `PaymentHistoryAction` → `GET /api/payments?claimId={claimId}`
* `PaymentDetailAction` → `GET /api/payments/{paymentId}`

## Extraction reference architecture

The extracted settlement service (8083) uses `controller`, `dto`, `service`,
`repository`, `model`, and `exception` packages; constructor injection;
`@ControllerAdvice`; Flyway migrations and deterministic `/internal/reset`.
Cross-service claim and policy reads replace the legacy monolith joins while
settlement and payment tables remain owned by this service. Routes and probes
are declarative in `parity/routes.yaml`; no scenario-specific harness code is
needed.
