CC := gcc
LDLIBS := -ljansson -lcurl
CFLAGS := -Wall -Werror

TARGET_PATH := .
OBJ_PATH := obj
SRC_PATH := src

TARGET := $(TARGET_PATH)/libweather.so
SRC := $(foreach x, $(SRC_PATH), $(wildcard $(addprefix $(x)/*,.c*)))
OBJ := $(addprefix $(OBJ_PATH)/, $(addsuffix .o, $(notdir $(basename $(SRC)))))

DISTCLEAN_LIST := $(OBJ)
CLEAN_LIST := $(TARGET) $(DISTCLEAN_LIST)

default: makedir all

$(TARGET): $(OBJ)
	$(CC) $(CFLAGS) -shared -o $@ $< $(LDLIBS)

$(OBJ_PATH)/%.o: $(SRC_PATH)/%.c*
	$(CC) $(CFLAGS) -fpic -c -o $@ $<

.PHONY: makedir
makedir:
	@mkdir -p $(OBJ_PATH)

.PHONY: all
all: $(TARGET)

.PHONY: clean
clean:
	@rm -vrf $(CLEAN_LIST)