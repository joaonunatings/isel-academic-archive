
#include "uthread.h"
#include "usynch.h"
#include "../inc/waitblock.h"

/////////////////////////////////////////////
//
// ISEL LEIC G01 LI42D
// SV - 20/21
//
// UThread Library Tests
//
// Alexandre Silva 47192
// João Nunes 47220
// Miguel Marques 47204
//
////////////////////////////////////////////

//
// Initializes the struct values.
//
void init_cb(barrier_cb * cb, int parties) {
    cb->parties = parties;
    cb->blocked_threads = 0;
    init_list_head(&cb->waiters);
}

//
// Confirms if the barrier will trip.
//
bool is_barrier_tripped(barrier_cb * cb) {
    if(cb->blocked_threads < cb->parties - 1) {
        
        return false;
    } else 
        return true;

}

//
// Blocks a thread and sends it to the waiting list if blocked_threads < parties - 1 (barrier hasn't tripped)
// otherwise it releases all the blocked threads.
//
int await_cb(barrier_cb * cb) {
    int arrival = -1;

    if(is_barrier_tripped(cb)){
        arrival = cb->blocked_threads;
        

        while (!is_list_empty(&(cb->waiters))) {
		    waitblock_t * pwblock = container_of(remove_list_first(&(cb->waiters)), waitblock_t, entry);
            
		    ut_activate(pwblock->thread);
	    }
        cb->blocked_threads = 0;
        ut_yield();
    }else{
        arrival = cb->blocked_threads ;
        cb->blocked_threads += 1;
        waitblock_t wblock;

		init_waitblock(&wblock);
		insert_list_last(&cb->waiters, &wblock.entry);
		ut_deactivate();
    }
    return arrival;
}