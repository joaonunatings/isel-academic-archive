CC := gcc
CFLAGS := -Wall -g

SRC_PATH := ./
INC_PATH := ../inc
LIB_PATH := ../../bin
TRG_PATH := ../../bin/test

LIB_NAME := uthread

SRC := $(foreach x, $(SRC_PATH), $(wildcard $(addprefix $(x)/*,.c*)))
LIB := $(LIB_PATH)/lib$(LIB_NAME).so
TRG := $(addprefix $(TRG_PATH)/, $(notdir $(basename $(SRC))))

default: mkdir all

.PHONY: all
all: $(TRG)

$(TRG_PATH)/%: $(SRC_PATH)/%.c*
	$(CC) $(CFLAGS) -L $(LIB_PATH) -I $(INC_PATH) -o $@ $< -l$(LIB_NAME)

.PHONY: mkdir
mkdir:
	@mkdir -pv $(TRG_PATH)

.PHONY: rmdir
rmdir:
	@rm -rfv $(TRG_PATH)

.PHONY: clean
clean:
	@rm -rfv $(TRG)
