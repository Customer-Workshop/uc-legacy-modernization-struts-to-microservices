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

The reserved settlement service (8083) should use `controller`, `dto`,
`service`, `repository`, `model`, `config`, and `exception` packages; constructor
injection; `@ControllerAdvice`; Flyway `V1__schema.sql` and `V2__seed.sql`;
and `/internal/reset` plus read endpoints for parity probes. Add its status and
routes/probes to `parity/routes.yaml` only; no harness Python changes should be
needed.
