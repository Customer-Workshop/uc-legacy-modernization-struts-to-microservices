---
name: struts-to-microservices
description: Repo-specific commands and mechanics for the Struts extraction demo.
---

Read `.workshop/playbooks/struts-to-microservices.devin.md` first. This file
contains only local mechanics.

* `make up NS=dev` starts Postgres and both services. Base ports are 8081,
  8082, and 5432; `PORT_OFFSET=N` adds N to each host port. Compose project,
  volume, and database names include `NS`, so use a unique namespace.
* `make down NS=dev`; use `docker compose -p claims-dev down -v` to revert a
  run completely.
* `make parity NS=dev [MODULE=policy] [SCENARIO=x]`; read `parity/report.md`
  and `parity/report.json`. Reports are generated and gitignored.
* Transcripts live in `transcripts/` and are immutable. Routes and module
  statuses live in `parity/routes.yaml`; adding settlement requires changing
  only that YAML plus service code: set `settlement: extracted`, add its
  route mappings and probe prefixes, and use `/internal/reset` and read
  endpoints.
* Both services run Flyway migrations from `src/main/resources/db/migration`.
  `/internal/reset` restores deterministic seed state.
* `make test`, `make lint`, `make sast`, and `make build` run tests, Spotless
  plus Ruff, Semgrep, and Maven builds.
* To revert generated output, run `docker compose -p claims-dev down -v` and
  `git clean -f parity/report.json parity/report.md` (never clean transcripts).
