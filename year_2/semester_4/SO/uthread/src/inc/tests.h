#ifndef TESTS_H
#define TESTS_H

#include <stdint.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>
#include <stdio.h>
#include <unistd.h>

#include "uthread.h"
#include "list.h"
#include "usynch.h"
//
// Prints states of the given list
//
void print_threads_states(uthread_t * threads[], int max_threads);

//
// Prints priorities of the given list
//
void print_threads_priorities(uthread_t * threads[], int max_threads);

#endif