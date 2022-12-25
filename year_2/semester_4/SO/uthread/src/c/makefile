CC := gcc
CFLAGS := -c -g -fPIC -Wall

SRC_PATH := ./
INC_PATH := ../inc
OBJ_PATH := ../../obj

SRC := $(foreach x, $(SRC_PATH), $(wildcard $(addprefix $(x)/*,.c*)))
OBJ := $(addprefix $(OBJ_PATH)/, $(addsuffix .o, $(notdir $(basename $(SRC)))))

default: mkdir all

.PHONY: all
all: $(OBJ)

$(OBJ_PATH)/%.o: $(SRC_PATH)/%.c*
	$(CC) $(CFLAGS) -I $(INC_PATH) -o $@ $<

.PHONY: mkdir
mkdir:
	@mkdir -pv $(OBJ_PATH)

.PHONY: rmdir
rmdir:
	@rm -rfv $(OBJ_PATH)

.PHONY: clean
clean:
	@rm -rfv $(OBJ)