CC := gcc
CFLAGS := -Wall

TRG_PATH := .
SRC_PATH := src
LIB_PATH := pthread-extensions
INC_PATH = $(LIB_PATH)/inc

LIB_NAME := pthread-extensions

SRC := $(foreach x, $(SRC_PATH), $(wildcard $(addprefix $(x)/*,.c*)))
TRG := $(addprefix $(TRG_PATH)/, $(notdir $(basename $(SRC))))

default: all

.PHONY: all
all: lib $(TRG)

.PHONY: lib
lib:
	@$(MAKE) -C $(LIB_PATH)

$(TRG_PATH)/%: $(SRC_PATH)/%.c*
	$(CC) $(CFLAGS) -I $(INC_PATH) -L $(LIB_PATH)/bin -o $@ $< -l$(LIB_NAME)

.PHONY: clean
clean:
	@rm -rfv $(TRG)

.PHONY: cleanall
cleanall: clean
	@$(MAKE) -C $(LIB_PATH) cleanall
