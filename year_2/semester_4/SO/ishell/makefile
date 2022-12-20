CC := gcc
CFLAGS := -Wall -O3

SRC_PATH := src

TARGET := ishell
SRC := $(foreach x, $(SRC_PATH), $(wildcard $(addprefix $(x)/*,.c*)))

CLEAN_LIST := $(TARGET)

default: all

.PHONY: all
all: $(TARGET)

$(TARGET): $(SRC)
	$(CC) $(CFLAGS) -o $@ $<

.PHONY: clean
clean:
	@rm -rvf $(CLEAN_LIST)
