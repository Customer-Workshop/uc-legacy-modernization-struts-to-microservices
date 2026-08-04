# Service slots

`policy-service` (8081) and `claims-intake-service` (8082) are extracted.
`settlement-service` (8083) exposes settlement calculation/save and payment
issue/history/detail/remittance endpoints with a dedicated Flyway-managed
`settlement` schema.
