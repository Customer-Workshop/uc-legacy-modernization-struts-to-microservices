# DJ-89 shared contract — settlement extraction + parity dashboard

This document is the locked contract for DJ-89. Two workstreams build against it
simultaneously (A: `settlement-service`, B: the HTML parity dashboard). Neither
workstream changes anything below unilaterally: a change request goes to the
coordinating session, which re-briefs both.

## 1. settlement-service HTTP contract (port 8083)

Money is serialized as a **string with exactly two decimals**. Errors are
`{"validationErrors": [...]}`; the field is present (possibly empty) on every
response listed here.

| Method | Path | Request body / query | Response |
|---|---|---|---|
| POST | `/api/settlements/calculate` | `claimId, coveredAmount, deductible, depreciation` | `settlementAmount, coveredAmount, deductibleApplied, depreciation, cappedAtLimit, validationErrors` |
| POST | `/api/settlements` | same body, persists | the calculate fields plus `settlementId, savedBy` |
| GET | `/api/settlements/claims/{claimId}` | — | `settlementAmount, cappedAtLimit` (latest settlement for the claim) |
| POST | `/api/payments` | `claimId, amount, payeeName, paymentMethod` | `checkNumber, paymentAmount, paymentStatus` |
| GET | `/api/payments` | `claimId` filter; absent or blank defaults to legacy claim 119 | array of `paymentId, checkNumber, paymentAmount, paymentStatus` |
| GET | `/api/payments/{paymentId}` | — | `paymentId, checkNumber, paymentAmount, paymentStatus` |
| POST | `/internal/reset` | — | restores deterministic seed state |

Serialization rules, derived from the legacy `FieldTag` and the golden
transcripts — these are part of the contract because the parity harness compares
normalized strings:

* `cappedAtLimit` is the **string** `"true"` / `"false"`, not a JSON boolean.
  The harness normalizes it with `text`, and Python renders a JSON boolean as
  `True` / `False`, which would never match.
* Money fields are rendered the way legacy `FieldTag` rendered them:
  `String.format("%.2f", doubleValue)`. This is *not* the same rounding as the
  settlement arithmetic. In `settlement_half_cent` the same input produces
  `coveredAmount` `1.01` (formatter, half-up on the shortest decimal
  representation of the `double`) and `settlementAmount` `1.00`
  (`Math.round(1.005 * 100.0) / 100.0` == 100). Reproduce both.
* `claimId` / `settlementId` / `paymentId` are JSON integers.

Determinism: after `POST /internal/reset` the seed mirrors the legacy database —
`SETTLEMENT` rows 1..120 (`settlement_id == claim_id`) and `PAYMENT` rows 1..60
(claims 1..60). Ids are allocated as `max(id) + 1`, so within a parity run
`settlement_save` creates settlement 121 and `payment_issue` creates payment 61
(`CHK-61`) on every run, including repeat runs after a reset.

Policy limits are read from policy-service `GET /api/policies/{policyId}`.
Policy data is not duplicated into the settlement schema.

## 2. `parity/report.json` schema

The harness writes it; the dashboard reads it. Nothing else may be assumed.

```json
{
  "generated_at": "2026-08-13T09:00:00Z",
  "modules": {"policy": "extracted", "settlement": "extracted", "workbench": "pending"},
  "results": [{"scenario": "settlement_calculate", "module": "settlement", "status": "PASS", "message": ""}],
  "summary": {"PASS": 0, "FAIL": 0, "SKIP": 0}
}
```

`modules` is copied from `parity/routes.yaml`; `generated_at` is UTC ISO-8601
with a trailing `Z`; `status` is `PASS`, `FAIL`, or a string starting with
`SKIP`.

## 3. Probe grammar (already landed with this contract)

`parity/replay.py` resolves a `db_state` key against the **longest dotted probe
name** declared in `parity/routes.yaml`; the segment after the matched name is
the identifier substituted into `{id}`, and the remaining segments are the field
name (empty means the field `value`). A field may declare `count: true` to
compare the length of the list at its `from` pointer instead of a normalized
scalar, alongside the existing `exists: true`.

Both additions are generic grammar, not scenario logic: `settlement.claim.119.amount`
resolves against a probe named `settlement.claim`, and `payment.count.claim.119`
against a probe named `payment.count.claim`. `parity/replay.py` gained
`--base-url-settlement` at the same time. No further harness change is expected
for this ticket; per-scenario branching in `parity/replay.py` remains forbidden.

## 4. File ownership

Kept disjoint so the two branches merge cleanly.

* **A** — `services/settlement-service/**`, `docker-compose.yml`, `Makefile`,
  `parity/routes.yaml`, `services/README.md`, `docs/STRUTS_TO_SPRING_MAPPING.md`,
  `docs/KNOWN_LEGACY_QUIRKS.md`, `.github/workflows/ci.yml`.
* **B** — `parity/html_report.py`, `parity/replay.py` (`write_report` only),
  `parity/fixtures/report.sample.json`, parity tests, `parity/.gitignore`.

Golden transcripts under `transcripts/` are immutable for both, and the legacy
monolith repository is never modified.
