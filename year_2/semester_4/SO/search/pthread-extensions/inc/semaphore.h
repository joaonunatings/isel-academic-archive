///////////////////////////////////////////
//
// ISEL LEIC G01 LI42D
// SV - 20/21
//
// Semaphore Synchronizer
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

typedef struct semaphore {
    pthread_mutex_t lock;
    pthread_cond_t wait_cond;
    int units;
} semaphore_t;

typedef struct waiter_node waiter_node_t;

//
// Initializes a semaphore instance. 
// initial is the starting number of units.
//
void semaphore_init(semaphore_t *sem, int initial);

//
// Adquires number of units with endless timeout.
//
void semaphore_acquire(semaphore_t *sem, int units);

//
// Adquires number of units with given timeout.
// millis - milliseconds to timeout.
//
bool semaphore_acquire_timed(semaphore_t *sem, int units, long millis);

//
// Delivers the number of units to the semaphore.
//
void semaphore_release(semaphore_t *sem, int units);