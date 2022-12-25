CC := gcc
CFLAGS := -Wall

TRG_NAME := uthread
INC_PATH := src/inc
SRC_PATH := src
TEST_PATH := src/test
OBJ_PATH := obj
BIN_PATH := bin

TRG := $(BIN_PATH)/lib$(TRG_NAME).so
OBJ = $(shell find $(OBJ_PATH) -maxdepth 1 -name '*.o')

CLEAN_LIST := $(TRG)
DIR_LIST := $(OBJ_PATH) $(BIN_PATH)

default: mkdir all tests

.PHONY: all
all: $(TRG)

$(TRG): asm c $(OBJ)
	$(CC) $(CFLAGS) -shared -o $@ $(OBJ)

asm: force
	@$(MAKE) -C $(SRC_PATH)/$@

c: force
	@$(MAKE) -C $(SRC_PATH)/$@

.PHONY: tests
tests: all
	@$(MAKE) -C $(TEST_PATH)

.PHONY: mkdir
mkdir:
	@mkdir -pv $(DIR_LIST)

.PHONY: rmdir
rmdir:
	@rm -rfv $(DIR_LIST)

.PHONY: clean
clean:
	@rm -rfv $(CLEAN_LIST)

.PHONY: cleanall
cleanall:
	@rm -rfv $(CLEAN_LIST)
	@$(MAKE) -C $(SRC_PATH)/c clean
	@$(MAKE) -C $(SRC_PATH)/asm clean
	@$(MAKE) -C $(TEST_PATH) clean

.PHONY: force
force: ;
