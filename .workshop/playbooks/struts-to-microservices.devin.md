# Playbook: Extract one Struts module into a verified Spring Boot microservice

> **Facilitator / presenter:** this file is the source for a **Devin Playbook**.
> Copy its contents into your Devin organization (Settings → Playbooks → *Create
> a new Playbook*) so sessions can invoke it as `!struts-to-microservice`.
> See [Creating Playbooks](https://docs.devin.ai/product-guides/creating-playbooks).
> The repo-specific commands (make targets, service ports, harness paths) live in
> the companion Skill at `.agents/skills/struts-to-microservices/SKILL.md`, which
> Devin auto-loads when working in this repo.

## Overview

Take **one** module of a Struts 1.x monolith — a coherent set of Actions, form
beans, JSPs, and DAOs — and extract it into a runnable Spring Boot service whose
behavior is **proven identical** to the legacy application. The outcome is a PR
containing the new service, its mapping notes, and a parity report showing that
every recorded legacy interaction replays against the new service with the same
business outcome.

The value is not that Struts code becomes Spring code. Anyone can do that. The
value is that the migration is **gated**: no module is considered migrated until
the recorded behavior of the legacy system reproduces exactly against the new
service.

## The one principle: the running legacy application is the source of truth

Not the documentation, not the code's apparent intent, not what the logic
*should* do. Struts 1.x has a decade of accumulated framework behavior baked into
what users actually see: `BeanUtils` coerces blank form fields to zero,
`SimpleDateFormat` in lenient mode rolls invalid dates forward, `double`-based
money math rounds half-up, validation returns specific resource-bundle keys in a
specific order. Users, downstream systems, and reports depend on those outcomes,
whether or not anyone intended them.

So: reproduce the observed behavior faithfully and **flag** every quirk you
reproduce. Fixing a legacy behavior is a separate, deliberate decision made with
the business — never a side effect of migration. This is why "the new code looks
correct" is not a passing grade, and why every extraction is gated by replaying
recorded legacy transcripts.

## Required from user

- **The module to extract** — the seam in the legacy estate, named by its Struts
  Actions and DAOs (e.g. the settlement/payments module: `SettlementAction`,
  `IssuePaymentAction`, `PaymentDAO`, `SettlementCalculator`).
- **The target service** — the Spring Boot service to produce, and the endpoints
  it must expose to cover the module's behavior.
- **The transcript scope** — which recorded legacy transcripts define "done" for
  this module (the parity contract this session must turn green).
- **Namespace / port** — an isolated run space so concurrent extractions do not
  collide.

## Procedure

1. **Read the module end to end in the legacy estate.** For each Action in
   scope: its `struts-config` mapping (path, form bean, forwards, validation),
   the form bean's fields and `validate()`, every JSP that posts to it or renders
   its result, every DAO and SQL statement it reaches, and the resource-bundle
   keys it emits. Write down the module's **inbound contract** (what a user can
   submit) and its **outbound contract** (what the user, the database, and any
   report observe afterwards).
2. **Separate business logic from framework plumbing.** Sort every method you
   found into: (a) genuine domain rules — the settlement formula, limit capping,
   status transitions, eligibility checks; (b) Struts/JSP plumbing —
   `ActionForm` population, `ActionForward` selection, taglib rendering,
   `ActionMessages`; (c) persistence. Only (a) is migrated as logic. (b) is
   replaced by the framework. (c) is rewritten against the target data access
   layer. Explicitly list anything you cannot classify — an ambiguous item is
   usually a domain rule hiding inside plumbing, and those are exactly the ones
   that break parity.
3. **Identify the coercions the framework was silently performing.** Before
   writing any new code, enumerate for each form field: its declared type, what
   `BeanUtils` does with an empty submission, what the configured date format
   does with an out-of-range value, and how numbers are rounded on the way to the
   database. These are behavior, not accidents — they must appear somewhere in
   the new service.
4. **Write the service** following the target reference architecture: controllers
   for the inbound contract, DTOs (validated) replacing form beans, a domain/
   service layer holding the rules from step 2(a), and a repository layer for
   persistence. Preserve every rule value-for-value, including the coercions from
   step 3. Where you reproduce a quirk, add a one-line comment stating it is
   legacy-faithful — a note, not an endorsement — and list it in the PR.
5. **Replay the golden transcripts** recorded from the running legacy
   application against the new service, and produce the parity report. (Exact
   commands are in the Skill.) Parity compares business outcomes — status,
   redirect/result target, the business fields in the response, validation error
   keys, and resulting database state — not markup.
6. **Close the loop.** If any transcript diverges, investigate **against the
   legacy application**: re-read the Action, the form bean, the DAO, and the
   framework behavior involved. Correct the service and re-run. Never edit,
   loosen, or re-record a transcript to make it pass — the transcript is the
   contract.
7. **Harden what you produced**, in the same session: unit tests that lock in the
   domain rules you migrated (especially each reproduced quirk), a clean build
   with no new lint or type failures, and no new SAST findings. Legacy smells you
   carried over (string-concatenated SQL, in particular) get fixed here — this is
   the one place where changing legacy code is correct, because a parameterized
   query is observably identical and the transcripts prove it.
8. **Deliver a PR** containing the service, the mapping notes, the tests, and the
   parity report, so a reviewer sees the evidence rather than just the diff. CI
   re-runs the whole loop on the PR.

## Specifications (postconditions)

- The service builds, starts, and serves the module's endpoints.
- Every in-scope golden transcript replays green: business fields, validation
  keys, and resulting state all match the legacy recording.
- Domain rules migrated in this session are covered by unit tests, including one
  test per reproduced legacy quirk.
- The PR contains the service, the Struts→Spring mapping notes, the tests, and
  the parity report.
- Every reproduced quirk and every deliberately-changed behavior is listed
  explicitly in the PR. Nothing is silently altered.
- No new lint, type, or SAST findings; any legacy injection smell inside the
  extracted module is parameterized.

## Advice and pointers

- Migrate the seam, not the file. A Struts module rarely maps one-to-one onto
  classes; follow the behavior (Action → JSP → DAO → report) and take the whole
  vertical slice.
- Parity is per-scenario, not aggregate. A service can handle the happy path
  perfectly and diverge on every blank-field or boundary submission — which is
  precisely where legacy framework coercions live.
- A transcript that is hard to make pass is telling you the extraction diverged.
  Re-read the Action before you doubt the transcript.
- Validation is part of the contract. The same invalid submission must produce
  the same error keys — downstream screens, translations, and support scripts key
  off them.
- Dead code found during analysis is not migrated. Report it; deleting it from
  the legacy estate is a separate decision.
- Struts form beans are `String`-typed by design, which is why the framework's
  conversion layer carries so much behavior. Modern DTOs are strongly typed, so
  the conversion that Struts did implicitly must become explicit in the new code
  or the behavior silently changes.

### Worked example: the blank deductible divergence

A real defect this loop caught, and the canonical illustration of "the running
legacy application is truth":

- In the legacy FNOL screen, the deductible field is optional. `ClaimForm`
  declares it as a `String`, and Struts populates it via `BeanUtils`, which
  converts an empty submission to **`0`**. The settlement calculator therefore
  subtracts a zero deductible, and the claimant is paid the full covered amount.
- The extracted service models the DTO field as a nullable `BigDecimal`, which is
  the correct modern design. A blank submission binds to `null`, and the
  calculator skips the deduction — producing the *same* settlement on most
  claims, so the happy-path tests and a code review both pass.
- The divergence only appears where the legacy zero is load-bearing: transcripts
  that combine a blank deductible with policy-limit capping settle at a different
  amount, because the capping order differs once the deduction is skipped.
- The parity report fails:
  `settlement_blank_deductible | FAIL | settlement 4,750.00 != legacy 4,500.00`.
- The fix is to reproduce the coercion explicitly — bind blank to zero at the DTO
  boundary and note it as legacy-faithful — not to adjust the transcript. A
  reviewer reading the new code would call the nullable field *better*. The
  recorded behavior is what the business actually runs on.

## Forbidden actions

- Do **not** edit, delete, or re-record a golden transcript to make a run go
  green. Fix the service, not the contract.
- Do **not** "improve" legacy business logic during extraction — reproduce it
  faithfully, flag it, and raise the improvement separately. (Parameterizing an
  injectable query inside the extracted module is the one sanctioned exception:
  it is observably identical and the transcripts prove it.)
- Do **not** modify the legacy monolith repo as part of an extraction. It is the
  durable before-state and the source of truth.
- Do **not** write into another run's namespace or port.
- Do **not** extract more than the one module in scope for this session.

## Parallel fan-out

Modules are independent once their seams are identified, so extractions
parallelize cleanly: run one session per module — each with its own namespace,
branch, and transcript scope — or one orchestrator session that spawns a child
per module and monitors them to green. Because this playbook fixes the procedure
and the parity contract, every session's output is consistent and independently
verified: the same review bar applied N times in parallel instead of once in
series.

## Running unattended

The same procedure works without a human in the loop. On a schedule, a session
can re-run the full parity suite against the extracted services and open an issue
on any drift. Event-driven, a CI failure or a new commit on the legacy repo can
trigger a session that re-replays the affected transcripts and pushes a fix. The
playbook is what makes unattended runs safe — the contract does not change just
because nobody is watching.
