#include "util.h"

#include <ctype.h>

void str_to_upper(char* buf) {
    for (; *buf; ++buf) *buf = toupper(*buf);
}

void str_to_lower(char* buf) {
    for (; *buf; ++buf) *buf = tolower(*buf);
}

void le_str_set(char* buf, size_t num, size_t size) {
    for (size_t i = 0; i < size; ++i) {
        buf[i] = (unsigned char)(num & 0xFF);
        num = num >> 8;
    }
}

size_t le_str_get(const char* buf, size_t size) {
    size_t value, temp;
    value = temp = 0;
    for (size_t i = 0; i < size; ++i) {
        temp = (unsigned char)(buf[i]) << (8 * i);
        value += temp;
    }

    return value;
}