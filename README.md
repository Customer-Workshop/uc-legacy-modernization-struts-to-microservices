# Struts to microservices: before state

This repository is the checkpoint before extracting the settlement module. It
contains policy and FNOL services plus a parity gate over immutable legacy
transcripts.

```sh
make up NS=dev
make parity NS=dev
```

| Area | Location |
|---|---|
| Extracted services | `services/policy-service`, `services/claims-intake-service` |
| Golden transcripts | `transcripts/` |
| Replay harness | `parity/` |
| Mapping and quirks | `docs/` |

Read `.workshop/playbooks/struts-to-microservices.devin.md`, then use the repo
mechanics in `.agents/skills/struts-to-microservices/SKILL.md`.