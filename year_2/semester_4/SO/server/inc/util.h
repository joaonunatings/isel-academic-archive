#pragma once

#include <stddef.h>

#define INLINE __attribute__((always_inline)) static inline
#define new(type, factor) (type *)malloc(sizeof(type) * factor)

void str_to_upper(char* buf);

void str_to_lower(char* buf);

void le_str_set(char* buf, size_t num, size_t size);

size_t le_str_get(const char* buf, size_t size);