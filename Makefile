NS ?= dev
PORT_OFFSET ?= 0
POLICY_PORT := $(shell expr 8081 + $(PORT_OFFSET))
INTAKE_PORT := $(shell expr 8082 + $(PORT_OFFSET))
REPORTING_PORT := $(shell expr 8084 + $(PORT_OFFSET))
POSTGRES_PORT := $(shell expr 5432 + $(PORT_OFFSET))
export NS PORT_OFFSET POLICY_PORT INTAKE_PORT REPORTING_PORT POSTGRES_PORT
MVN ?= mvn
MAVEN_FLAGS ?= -Dmaven.repo.local=$(HOME)/.m2/repository -Daether.connector.basic.connectionMaxRetry=5 -Daether.connector.basic.requestTimeout=60000
.PHONY: up down parity test lint sast sync-transcripts build
up:
	docker compose -p claims-$(NS) up -d --build
down:
	docker compose -p claims-$(NS) down
parity:
	python3 -m parity.replay --base-url-policy http://localhost:$(POLICY_PORT) --base-url-intake http://localhost:$(INTAKE_PORT) --base-url-reporting http://localhost:$(REPORTING_PORT) $(if $(MODULE),--module $(MODULE),) $(if $(SCENARIO),--scenario $(SCENARIO),)
test:
	cd services/policy-service && $(MVN) $(MAVEN_FLAGS) -q test
	cd services/claims-intake-service && $(MVN) $(MAVEN_FLAGS) -q test
	cd services/reporting-service && $(MVN) $(MAVEN_FLAGS) -q test
lint:
	cd services/policy-service && $(MVN) $(MAVEN_FLAGS) -q spotless:check
	cd services/claims-intake-service && $(MVN) $(MAVEN_FLAGS) -q spotless:check
	cd services/reporting-service && $(MVN) $(MAVEN_FLAGS) -q spotless:check
	ruff check parity
sast:
	semgrep --error --config p/java --config p/python services parity
sync-transcripts:
	@set -eu; ref=$${LEGACY_REF:-main}; tmp=$$(mktemp -d); trap 'rm -rf "$$tmp"' EXIT; git clone --no-checkout "$${LEGACY_REPO:-https://github.com/Cognition-Partner-Workshops/ts-java-struts-claims-management.git}" "$$tmp/legacy"; git -C "$$tmp/legacy" fetch origin "$$ref"; git -C "$$tmp/legacy" checkout FETCH_HEAD; cp "$$tmp/legacy"/transcripts/*.json "$$tmp/legacy"/transcripts/README.md transcripts/; git -C "$$tmp/legacy" rev-parse HEAD > transcripts/SOURCE_SHA
build:
	cd services/policy-service && $(MVN) $(MAVEN_FLAGS) -q package -DskipTests
	cd services/claims-intake-service && $(MVN) $(MAVEN_FLAGS) -q package -DskipTests
	cd services/reporting-service && $(MVN) $(MAVEN_FLAGS) -q package -DskipTests
