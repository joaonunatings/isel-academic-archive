#include "tests.h"

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




#define PARTIES_NR 4

uthread_t *threads[PARTIES_NR*2];
uthread_t *threads2[PARTIES_NR*2];

void print_threads_priorities(uthread_t * threads[], int max_threads){
	printf("\n");
	uint64_t index;
	for (index = 0; index < max_threads; ++index){
		printf("\nThread %c:  %d", (char)('0' + index), ut_priority(threads[index]));
	}
	printf("\n\n");
}

void print_threads_states(uthread_t * threads[], int max_threads){
	printf("\n");
	uint64_t index;
	for (index = 0; index < max_threads; ++index){
		printf("\nThread %c:  %d", (char)('0' + index), ut_state(threads[index]));
	}
	printf("\n\n");
}

void dummyThread(void * arg) {
	barrier_cb * cb  = (barrier_cb *)arg;
	
	printf("Thread #0 begin\n");
	 
	usleep(1500000);
	
	await_cb(cb);
	printf("\nThread #0 ended\n");
}

///////////////////////////////////////////////////////////////////////
//                                                         		     //
//   Test 0: Empty Barrier	 							 		 	 //
//                                                         			 //
///////////////////////////////////////////////////////////////////////	

void cb_test0_empty(){
	barrier_cb cb;

	printf("\n :: Test 0 empty barrier - BEGIN :: \n\n");

	init_cb(&cb, 0);

	threads[0] = ut_create(dummyThread, &cb, LOW);

	ut_run();

    printf("\n :: Test 0 empty barrier - END :: \n\n");
}

///////////////////////////////////////////////////////////////////////
//                                                         		     //
//   Test 1: One Party added to the Cyclic Barrier	 			     //
//                                                         			 //
///////////////////////////////////////////////////////////////////////	

void cb_test1_one_party(){
	barrier_cb cb;

	printf("\n :: Test 1 one party - BEGIN :: \n\n");

	init_cb(&cb, 1);

	threads[0] = ut_create(dummyThread, &cb, LOW);

	ut_run();

    printf("\n :: Test 1 one party - END :: \n\n");
}

///////////////////////////////////////////////////////////////////////
//                                                         		     //
//   Test 2: Threads with notifiers and waiters printing its states  //
//                                                         			 //
///////////////////////////////////////////////////////////////////////	

void notifier_thread_with_states(void * arg) {
	barrier_cb * cb  = (barrier_cb *)arg;
	
	printf("\nNotifier thread begin\n");
	print_threads_states(threads, PARTIES_NR);

	usleep(1000000);
	 
	int arrival = await_cb(cb);

	usleep(700000);
	print_threads_states(threads, PARTIES_NR);

	printf("\nNotifier %d thread end\n", arrival);
}

void waiter_thread_with_states(void * arg) {
	barrier_cb * cb = (barrier_cb *)arg;
	
	printf("\nWaiter thread begin\n");
	
	printf("\nWaiter: waiting for thread notification!\n");
	print_threads_states(threads, PARTIES_NR);
	usleep(1000000);

	int arrival = await_cb(cb);
	usleep(700000);

	if(arrival == 0)
		printf("\n-----BARRIER TRIPPED-----\n");

	print_threads_states(threads, PARTIES_NR);
	
	printf("\nWaiter %d thread end!\n", arrival);	
}

void cb_test2_with_states(){
	barrier_cb cb;
	
	printf("\n :: Test 2 printing states - BEGIN :: \n\n");
	
	init_cb(&cb, PARTIES_NR);

	for (size_t i = 0; i < PARTIES_NR - 1; i++) {
       threads[i] = ut_create(waiter_thread_with_states, &cb, HIGH);
    }	

	threads[PARTIES_NR - 1] = ut_create(notifier_thread_with_states, &cb, LOW);
	ut_run();
	
	printf("\n :: Test 2 printing states - END :: \n\n");
}

///////////////////////////////////////////////////////////////////////
//                                                         		     //
//   Test 3: With and without Cyclic Barrier	 				     //
//                                                         			 //
///////////////////////////////////////////////////////////////////////	

void test_thread (void * argument) {
    uint8_t c;
    uint64_t index;
    c = (uint8_t) (uint64_t) argument;    
     
    for (index = 0; index < 5; ++index) {
        putchar(c); fflush(stdout);
         
        if ((rand() % 2) == 0) {
            usleep(300000); 
            ut_yield();
        }     
    }
}

void cb_test3_with_and_without_cyclic_barrier(){
	barrier_cb cb;
	
	printf("\n :: Test 3 with and without Cyclic Barrier - BEGIN :: \n\n");

	usleep(1000000); 
	
	init_cb(&cb, PARTIES_NR);

	size_t i;
	for (i = 0; i < PARTIES_NR - 1; i++) {
       threads[i] = ut_create(waiter_thread_with_states, &cb, HIGH);
    }	
	threads[i++] = ut_create(notifier_thread_with_states, &cb, LOW);

	threads[i++] = ut_create(test_thread, (void *) ('4'), LOW);
	threads[i++] = ut_create(test_thread, (void *) ('5'), LOW);
	threads[i++] = ut_create(test_thread, (void *) ('6'), NORMAL);
	threads[i++] = ut_create(test_thread, (void *) ('7'), HIGH);

	ut_run();
	
	printf("\n\n :: Test 3 with and without Cyclic Barrier - END :: \n\n");
}

///////////////////////////////////////////////////////////////////////
//                                                         		     //
//   Test 4: With and without Cyclic Barrier reusable			     //
//                                                         			 //
///////////////////////////////////////////////////////////////////////
//   Even though the prints are showing the threads states and 		 //
//priorities, the prints are also showing the states and priorities  //
//of threads that already ran. Which may lead to misinformation      //
//about those threads.												 //
///////////////////////////////////////////////////////////////////////

void waiter_thread(void * arg) {
	barrier_cb * cb = (barrier_cb *)arg;
	await_cb(cb);
}


void cb_test4_with_and_without_cyclic_barrier_reusable(){
	barrier_cb cb;

	init_cb(&cb, PARTIES_NR);

	for (size_t i = 0; i < PARTIES_NR; i++) {
       ut_create(waiter_thread, &cb, HIGH);
    }	

	size_t i;
	for (i = 0; i < PARTIES_NR - 1; i++) {
       threads[i] = ut_create(waiter_thread_with_states, &cb, NORMAL);
    }	
	threads[i++] = ut_create(notifier_thread_with_states, &cb, LOW);

	threads[i++] = ut_create(test_thread, (void *) ('7'), LOW);
	threads[i++] = ut_create(test_thread, (void *) ('8'), NORMAL);
	threads[i++] = ut_create(test_thread, (void *) ('9'), NORMAL);
	threads[i++] = ut_create(test_thread, (void *) ('0'), HIGH);

	ut_run();
}


void to_continue(){
	printf("\nPRESS [ENTER] TO CONTINUE\n");
	for (size_t i = 0; i < PARTIES_NR*2; i++){
		threads[i] = NULL;
	}
	
	getchar();
}

int main() {
	ut_init();

	cb_test0_empty();
	to_continue();

	cb_test1_one_party();
	to_continue();

	cb_test2_with_states();
	to_continue();

	cb_test3_with_and_without_cyclic_barrier();
	to_continue();

	cb_test4_with_and_without_cyclic_barrier_reusable();
	to_continue();

	ut_end();
    return 0;
}