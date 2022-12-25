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

#ifndef UTHREAD_H
#define UTHREAD_H


typedef struct uthread uthread_t;

//
// Represents the states a thread can have
//
enum ut_state{STATE_BLOCKED, STATE_READY, STATE_RUNNING};

//
// Represents the priorities a thread can receive
//
enum ut_priority{IDLE, LOW, NORMAL, HIGH, PRIORITY_LEVELS};

//
// Initialize the library.
// This function must be the first to be called. 
//
void ut_init();

//
// Cleanup all UThread internal resources.
//
void ut_end();

//
// Run the user threads. The operating system thread that calls this function
// performs a context switch to a user thread and resumes execution only when
// all user threads have exited.
//
void ut_run();

//
// Creates a user thread to run the specified function, passing it arg as argument.
// The new thread is placed at the end of the ready queue.
//
uthread_t * ut_create(void (*start_routine)(void *), void * arg, enum ut_priority p);

//
// Terminates the execution of the currently running thread. All associated
// resources are released after the context switch to the next ready thread.
//
void ut_exit();

//
// Relinquishes the processor to the first user thread in the ready queue.
// If there are no ready threads, the function returns immediately.
//
void ut_yield();

//
// Returns a handle to the executing user thread.
//
uthread_t * ut_self();

//
// Returns the state of the requested thread.
//
int ut_state(uthread_t* requested_uthread);

//
// Returns the priority of the requested thread.
//
int ut_priority(uthread_t* requested_uthread);

//
// Halts the execution of the current user thread.
//
void ut_deactivate();

//
// Places the specified user thread at the end of the ready queue,
// making it eligible to run.
//
void ut_activate(uthread_t * handle);

#endif
