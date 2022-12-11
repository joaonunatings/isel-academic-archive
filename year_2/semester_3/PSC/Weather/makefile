CC := gcc
CFLAGS := -Wall -Werror

SRC_PATH := src
LIB_PATH := lib

TARGET := weather
SRC := $(foreach x, $(SRC_PATH), $(wildcard $(addprefix $(x)/*,.c*)))
LIB := $(LIB_PATH)/libweather.so

CLEAN_LIST := $(TARGET)

default: all

.PHONY: all
all: lib $(TARGET)

$(TARGET): $(SRC) lib
	$(CC) -L $(LIB_PATH) $(CFLAGS) -o $@ $< -lweather

lib $(LIB): FORCE
	$(MAKE) -C $(LIB_PATH)

FORCE: ;

.PHONY: clean
clean:
	@rm -rvf $(CLEAN_LIST)