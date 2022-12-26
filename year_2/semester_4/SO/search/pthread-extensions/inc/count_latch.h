///////////////////////////////////////////
//
// ISEL LEIC G01 LI42D
// SV - 20/21
//
// Count Latch Synchronizer:
//  One or more threads distribute workload through various
//  worker threads.
//
// Authors:
// Alexandre Silva - 47192
// João Nunes - 47220
// Miguel Marques - 47204
//
///////////////////////////////////////////

#pragma once

#include <stdbool.h>
#include <pthread.h>

typedef struct count_latch {
	pthread_mutex_t lock;
	pthread_cond_t waiters;
	pthread_cond_t waiters_set;
    int par_level;
	int worker_count;
	int waiters_count;
} count_latch_t;

//
// Initializes count latch instance.
// par_level - number of worker threads working simultaneously
// If par_level = 0, then there's no parallelism limit.
//
void cl_init(count_latch_t *latch, int par_level);

//
// Wait until workload reaches zero.
//
void cl_wait_all(count_latch_t *latch);

//
// Increments workload. 
// Blocks summoned thread if maximum parallelism level has been reached.
//
void cl_up(count_latch_t *latch);

//
// Decrements workload. 
// If there's availability (not at the limit parallelism level), unblock any 
// blocked thread by cl_up. 
// Unblock all threads blocked by cl_wait_all if workload reaches zero.
//
void cl_down(count_latch_t *latch);

//
// Destroys cond_t and mutex 
//
void cl_cleanup(count_latch_t *latch);