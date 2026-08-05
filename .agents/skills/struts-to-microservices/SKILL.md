---
name: struts-to-microservices
description: Repo-specific commands and mechanics for the Struts extraction demo.
---

Read `.workshop/playbooks/struts-to-microservices.devin.md` first. This file
contains only local mechanics.

* `make up NS=dev` starts Postgres and both services. Base ports are 8081,
  8082, and 5432; `PORT_OFFSET=N` adds N to each host port. Compose project,
  volume, and database names include `NS`, so use a unique namespace.
  Maven Docker builds use Central by default; when Central is rate-limited,
  rebuild with `MAVEN_MIRROR=https://maven.aliyun.com/repository/central make up NS=dev`.
* `make down NS=dev`; use `docker compose -p claims-dev down -v` to revert a
  run completely.
* `make parity NS=dev [MODULE=policy] [SCENARIO=x]`; read the generated
  Markdown table in `parity/report.md` and machine-readable `report.json`.
  Reports are generated and gitignored; failures exit non-zero.
* Transcripts live in `transcripts/` and are immutable. Routes, status
  equivalence, response extraction, validation-error fields, module statuses,
  and DB probes live declaratively in `parity/routes.yaml`. Adding settlement
  requires changing only that YAML plus service code: set
  `settlement: extracted`, add its route `business_fields` and probe
  definitions, and use `/internal/reset` and read endpoints. Do not add
  scenario branches to `parity/replay.py`.
* Both services run Flyway migrations from `src/main/resources/db/migration`.
  `/internal/reset` restores deterministic seed state.
* `make test`, `make lint`, `make sast`, and `make build` run tests, Spotless
  plus Ruff, Semgrep, and Maven builds.
* To revert generated output, run `docker compose -p claims-dev down -v` and
  `git clean -f parity/report.json parity/report.md` (never clean transcripts).
