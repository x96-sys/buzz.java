BUILD_DIR       = out
MAIN_BUILD      = $(BUILD_DIR)/main
TEST_BUILD      = $(BUILD_DIR)/test
COVERAGE_REPORT = $(BUILD_DIR)/coverage

SRC_MAIN      = src/main
SRC_TEST      = src/test

TOOLS_DIR      = tools

JUNIT_VERSION = 1.13.4
JUNIT_JAR     = $(TOOLS_DIR)/junit-platform-console-standalone.jar
JUNIT_URL     = https://maven.org/maven2/org/junit/platform/junit-platform-console-standalone/$(JUNIT_VERSION)/junit-platform-console-standalone-$(JUNIT_VERSION).jar
JUNIT_SHA256  = 3fdfc37e29744a9a67dd5365e81467e26fbde0b7aa204e6f8bbe79eeaa7ae892

JACOCO_VERSION = 0.8.13
JACOCO_BASE    = https://maven.org/maven2/org/jacoco

JACOCO_CLI_VERSION = $(JACOCO_VERSION)
JACOCO_CLI_JAR     = $(TOOLS_DIR)/jacococli.jar
JACOCO_CLI_URL     = $(JACOCO_BASE)/org.jacoco.cli/$(JACOCO_CLI_VERSION)/org.jacoco.cli-$(JACOCO_CLI_VERSION)-nodeps.jar
JACOCO_CLI_SHA256  = 8f748683833d4dc4d72cea5d6b43f49344687b831e0582c97bcb9b984e3de0a3

JACOCO_AGENT_VERSION = $(JACOCO_VERSION)
JACOCO_AGENT_JAR     = $(TOOLS_DIR)/jacocoagent-runtime.jar
JACOCO_AGENT_URL     = $(JACOCO_BASE)/org.jacoco.agent/$(JACOCO_AGENT_VERSION)/org.jacoco.agent-$(JACOCO_AGENT_VERSION)-runtime.jar
JACOCO_AGENT_SHA256  = 47e700ccb0fdb9e27c5241353f8161938f4e53c3561dd35e063c5fe88dc3349b

GJF_VERSION = 1.28.0
GJF_JAR     = $(TOOLS_DIR)/gjf.jar
GJF_URL     = https://maven.org/maven2/com/google/googlejavaformat/google-java-format/$(GJF_VERSION)/google-java-format-$(GJF_VERSION)-all-deps.jar
GJF_SHA256  = 32342e7c1b4600f80df3471da46aee8012d3e1445d5ea1be1fb71289b07cc735

JAVA_SOURCES      := $(shell find $(SRC_MAIN) -name "*.java")
JAVA_TEST_SOURCES := $(shell find $(SRC_TEST) -name "*.java")

BUILD_INFO = https://gist.githubusercontent.com/tfs91/d8a380974ee7f640e0692855b643ec01/raw/62720672c6f7329b19dd243bfa18b3a780dd3f0b/generate_build_info.rb

DISTRO_JAR = org.x96.sys.foundation.buzz.jar

build/info:
	@curl -sSL $(BUILD_INFO) | ruby - src/main/ org.x96.sys.buzz

build: build/info clean/build/main
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

format: tools/gjf
	@find src -name "*.java" -print0 | xargs -0 java -jar $(GJF_JAR) --aosp --replace
	@echo "[✨] [gjf] Formatting complete"

define deps
$1/$2: $1
	@expected="$($3_SHA256)"; \
	jar="$($3_JAR)"; \
	url="$($3_URL)"; \
	tmp="$$$$(mktemp)"; \
	if [ ! -f "$$$$jar" ]; then \
		echo "[📦] [🚛] [$($3_VERSION)] [$2]"; \
		curl -sSL -o "$$$$tmp" "$$$$url"; \
		actual="$$$$(shasum -a 256 $$$$tmp | awk '{print $$$$1}')"; \
		if [ "$$$$expected" = "$$$$actual" ]; then mv "$$$$tmp" "$$$$jar"; \
		echo "[📦] [📍] [$($3_VERSION)] [$2] [🐚]"; else rm "$$$$tmp"; \
		echo "[❌] [hash mismatch] [$2]"; exit 1; fi; \
	else \
		actual="$$$$(shasum -a 256 $$$$jar | awk '{print $$$$1}')"; \
		if [ "$$$$expected" = "$$$$actual" ]; \
		then echo "[📦] [📍] [$($3_VERSION)] [🐚] [$2]"; \
		else \
			echo "[❌] [hash mismatch] [$2]"; \
			curl -sSL -o "$$$$tmp" "$$$$url"; \
			actual="$$$$(shasum -a 256 $$$$tmp | awk '{print $$$$1}')"; \
			if [ "$$$$expected" = "$$$$actual" ]; then mv "$$$$tmp" "$$$$jar"; \
			echo "[📦] [♻️] [$($3_VERSION)] [🐚] [$2]"; else rm "$$$$tmp"; \
			echo "[❌] [download failed] [$2]"; exit 1; fi; \
		fi; \
	fi
endef

kit: \
	$(TOOLS_DIR)/junit \
	$(TOOLS_DIR)/gjf \
	$(TOOLS_DIR)/jacoco_cli \
	$(TOOLS_DIR)/jacoco_agent

$(eval $(call deps,$(TOOLS_DIR),junit,JUNIT))
$(eval $(call deps,$(TOOLS_DIR),gjf,GJF))
$(eval $(call deps,$(TOOLS_DIR),jacoco_cli,JACOCO_CLI))
$(eval $(call deps,$(TOOLS_DIR),jacoco_agent,JACOCO_AGENT))

$(BUILD_DIR) $(MAIN_BUILD) $(TEST_BUILD) $(TOOLS_DIR) $(COVERAGE_REPORT):
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
	@rm -rf $(TOOLS_DIR)
	@echo "[🛀] [clean] [$(TOOLS_DIR)]"

clean: \
	clean/build \
	clean/kit
	@echo "[🔬] [clean]"
	@rm -f $(DISTRO_JAR)
