/////////////////////////////////////////////////////////////////
//
// CCISEL 
// 2007-2021
//
// UThread library:
//   User threads supporting cooperative multithreading.
//
// Authors:
//   Carlos Martins, Jo�o Trindade, Duarte Nunes, Jorge Martins
// 

/////////////////////////////////////////////
//
// ISEL LEIC G01 LI42D
// SV - 20/21
//
// UThread Library
//
// Alexandre Silva 47192
// João Nunes 47220
// Miguel Marques 47204
//
////////////////////////////////////////////

#pragma once

#include <stdio.h>
#include "uthread.h"
#include "list.h"


//
// A single-shot broadcast event. Threads are kept blocked in wait
// until set is called.
//
 
typedef struct event {
	bool signaled;
	list_entry_t waiters;
} event_t;
 
void event_init(event_t * event);
 
void event_wait(event_t * event);
 
void event_set(event_t * event);

INLINE bool event_value(event_t * event) {
	return event->signaled; 
}


//
// A semaphore, containing the current number of units, upper bounded
// by limit.
//
typedef struct semaphore {
	list_entry_t waiters;
	int units;
	int limit;
} semaphore_t;

//
// Initializes a semaphore instance. Units is the starting number of
// available permits and limit is the maximum number of units allowed
// for the specified semaphore instance.
//
void sem_init(semaphore_t * sem, int units, int limit);

//
// Gets the specified number of units from the semaphore. If there
// aren't enough units available, the calling thread is blocked until
// they are added by a call to sem_post.
//
void sem_wait(semaphore_t * sem, int units);

//
// Adds the specified number of units to the semaphore, possibly
// unblocking waiting threads.
//
void sem_post(semaphore_t * sem, int units);

/////////////////////////
//
// A synchronization aid that allows a set of threads to all wait for each other to reach a common barrier point.
//

//
// This struct represents the parties needed to trip the barrier, the number of the blocked threads in the waiting list and the waiting list itself.
//
typedef struct barrier{
	int parties;
	int blocked_threads;
	list_entry_t waiters;
} barrier_cb;

//
// Initializes the struct values.
//
void init_cb(barrier_cb * cb, int parties);

//
// Blocks a thread and sends it to the waiting list if blocked_threads < parties - 1 (barrier hasn't tripped)
// otherwise it releases all the blocked threads.
//
int await_cb(barrier_cb * cb);
