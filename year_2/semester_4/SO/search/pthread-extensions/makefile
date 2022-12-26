CC := gcc
CFLAGS := -Wall -g

LIB_NAME := pthread-extensions

SRC_PATH := src
INC_PATH := inc
OBJ_PATH := obj
TRG_PATH := bin

TRG := $(TRG_PATH)/lib$(LIB_NAME).so
SRC := $(foreach x, $(SRC_PATH), $(wildcard $(addprefix $(x)/*,.c*)))
OBJ := $(addprefix $(OBJ_PATH)/, $(addsuffix .o, $(notdir $(basename $(SRC)))))

DIR_LIST := $(OBJ_PATH) $(TRG_PATH)
CLEAN_LIST := $(OBJ) $(TRG)

default: mkdir all tests

.PHONY: all
all: $(TRG)

$(TRG): $(OBJ)
	$(CC) $(CFLAGS) -shared -o $@ $(OBJ)

$(OBJ_PATH)/%.o: $(SRC_PATH)/%.c*
	$(CC) $(CFLAGS) -I $(INC_PATH) -c -pthread -o $@ $<

.PHONY: tests
tests: all
	@$(MAKE) -C test

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
cleanall: clean
	@$(MAKE) -C test clean