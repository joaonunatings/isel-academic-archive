ASMC := as
ASMFLAGS := -c

TRG_NAME := ctxswitch
OBJ_PATH := ../../obj
SRC_PATH := ./

TRG := $(OBJ_PATH)/$(TRG_NAME).o
SRC := $(foreach x, $(SRC_PATH), $(wildcard $(addprefix $(x)/*,.s*)))

CLEAN_LIST := $(TRG)

default: mkdir all

.PHONY: all
all: $(TRG)

$(TRG): $(SRC)
	$(ASMC) $(ASMFLAGS) -o $@ $<

.PHONY: mkdir
mkdir:
	@mkdir -pv $(OBJ_PATH)

.PHONY: rmdir
rmdir:
	@rm -rfv $(OBJ_PATH)

.PHONY: clean
clean:
	@rm -rfv $(CLEAN_LIST)