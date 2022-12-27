CC := gcc
CFLAGS := -Wall -Wextra -pedantic -Wno-unused-parameter -Wno-unused-variable

TRG_NAME := server

TRG_PATH := ./
INC_PATH := inc
SRC_PATH := src

TRG := $(TRG_PATH)/$(TRG_NAME)
SRC := $(foreach x, $(SRC_PATH), $(wildcard $(addprefix $(x)/*,.c*)))

#print:
#	@echo $(SRC)
#	@echo $(TRG)

default: all

.PHONY: all
all: $(TRG)

$(TRG): $(SRC)
	$(CC) $(CFLAGS) -I $(INC_PATH) -o $@ $^ -luv

.PHONY: clean
clean:
	@rm -rfv $(TRG)