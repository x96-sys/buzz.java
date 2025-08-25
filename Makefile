BUILD_DIR       = out
MAIN_BUILD      = $(BUILD_DIR)/main
TEST_BUILD      = $(BUILD_DIR)/test
COVERAGE_REPORT = $(BUILD_DIR)/coverage

SRC_MAIN      = src/main
SRC_TEST      = src/test

TOOL_DIR      = tools

JUNIT_VERSION = 1.13.4
JUNIT_JAR     = $(TOOL_DIR)/junit-platform-console-standalone.jar
JUNIT_URL     = https://maven.org/maven2/org/junit/platform/junit-platform-console-standalone/$(JUNIT_VERSION)/junit-platform-console-standalone-$(JUNIT_VERSION).jar

JACOCO_VERSION = 0.8.13
JACOCO_BASE    = https://maven.org/maven2/org/jacoco

JACOCO_CLI_VERSION = $(JACOCO_VERSION)
JACOCO_CLI_JAR     = $(TOOL_DIR)/jacococli.jar
JACOCO_CLI_URL     = $(JACOCO_BASE)/org.jacoco.cli/$(JACOCO_CLI_VERSION)/org.jacoco.cli-$(JACOCO_CLI_VERSION)-nodeps.jar

JACOCO_AGENT_VERSION = $(JACOCO_VERSION)
JACOCO_AGENT_JAR     = $(TOOL_DIR)/jacocoagent-runtime.jar
JACOCO_AGENT_URL     = $(JACOCO_BASE)/org.jacoco.agent/$(JACOCO_AGENT_VERSION)/org.jacoco.agent-$(JACOCO_AGENT_VERSION)-runtime.jar

JAVA_SOURCES      := $(shell find $(SRC_MAIN) -name "*.java")
JAVA_TEST_SOURCES := $(shell find $(SRC_TEST) -name "*.java")

BUILD_INFO = https://gist.githubusercontent.com/tfs91/d8a380974ee7f640e0692855b643ec01/raw/62720672c6f7329b19dd243bfa18b3a780dd3f0b/generate_build_info.rb

DISTRO_JAR = org.x96.sys.foundation.buzz.jar

build/info:
	@curl -sSL $(BUILD_INFO) | ruby - src/main/ org.x96.sys.foundation.buzz

build: build/info clean/build/main | $(MAIN_BUILD)
	@javac -d $(MAIN_BUILD) $(JAVA_SOURCES)
	@echo "[🦿] [compiled] [$(MAIN_BUILD)]"

build/test: kit clean/build/test build
	@javac -cp $(JUNIT_JAR):$(MAIN_BUILD) -d $(TEST_BUILD) \
	   $(shell find $(SRC_TEST) -name "*.java")
	@echo "[🦾] [compiled] [$(TEST_BUILD)]"

test: build/test
	@java -jar $(JUNIT_JAR) \
	   execute \
	   --class-path $(TEST_BUILD):$(MAIN_BUILD):$(CLI_BUILD) \
	   --scan-class-path

coverage: clean/coverage build/test | $(COVERAGE_REPORT)
	@echo "[📊] Running tests with JaCoCo agent..."
	@java -javaagent:$(JACOCO_AGENT_JAR)=destfile=$(BUILD_DIR)/jacoco.exec \
		-jar $(JUNIT_JAR) \
		execute \
		--class-path $(TEST_BUILD):$(MAIN_BUILD) \
		--scan-class-path
	@echo "[📑] Generating coverage report..."
	@java -jar $(JACOCO_CLI_JAR) report $(BUILD_DIR)/jacoco.exec \
		--classfiles $(MAIN_BUILD) \
		--sourcefiles $(SRC_MAIN) \
		--html $(COVERAGE_REPORT) \
		--xml  $(COVERAGE_REPORT)/coverage.xml \
		--csv  $(COVERAGE_REPORT)/coverage.csv
	@echo "[✅] Coverage report available in $(COVERAGE_REPORT)/index.html"

define deps
$1/$2: $1
	@if [ ! -f "$$($3_JAR)" ]; then \
		echo "[📦] [🚛] [$$($3_VERSION)] [$2]"; \
		curl -sSL -o $$($3_JAR) $$($3_URL); \
	else \
		echo "[📦] [📍] [$$($3_VERSION)] [$2]"; \
	fi
endef

$(eval $(call deps,$(TOOL_DIR),junit,JUNIT))
$(eval $(call deps,$(TOOL_DIR),jacoco_cli,JACOCO_CLI))
$(eval $(call deps,$(TOOL_DIR),jacoco_agent,JACOCO_AGENT))

kit: \
	$(TOOL_DIR)/junit \
	$(TOOL_DIR)/jacoco_cli \
	$(TOOL_DIR)/jacoco_agent

$(BUILD_DIR) $(MAIN_BUILD) $(TEST_BUILD) $(TOOL_DIR) $(COVERAGE_REPORT):
	@mkdir -p $@

distro:
	@jar cf $(DISTRO_JAR) -C $(MAIN_BUILD) .
	@echo "[☕️] [bin] [$(DISTRO_JAR)]"

clean/build:
	@rm -rf $(BUILD_DIR)
	@echo "[🧽] [clean] [$(BUILD_DIR)]"

clean/build/main:
	@rm -rf $(MAIN_BUILD)
	@echo "[🧼] [clean] [$(MAIN_BUILD)]"

clean/build/test:
	@rm -rf $(TEST_BUILD)
	@echo "[🧹] [clean] [$(TEST_BUILD)]"

clean/coverage:
	@rm -rf $(COVERAGE_REPORT)
	@echo "[🫧] [clean] [$(COVERAGE_REPORT)]"

clean/kit:
	@rm -rf $(TOOL_DIR)
	@echo "[🛀] [clean] [$(TOOL_DIR)]"

clean: \
	clean/build \
	clean/build/main \
	clean/build/test \
	clean/kit \
	@echo "[🔬] [clean]"
	@rm -f $(DISTRO_JAR)
