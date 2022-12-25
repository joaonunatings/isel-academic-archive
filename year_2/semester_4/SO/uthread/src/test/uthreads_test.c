// uthreads_tests.c : Defines the entry point for the console application.
//

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

#define DEBUG

#define MAX_THREADS 6

//                                                         
// Forward declaration of internal operations.
//                                                         

uthread_t *threads[MAX_THREADS];

void test_thread (void * argument) {
    uint8_t c;
    uint64_t index;
    c = (uint8_t) (uint64_t) argument;    
     
    for (index = 0; index < 5; ++index) {
        putchar(c); fflush(stdout);
         
        if ((rand() % 2) == 0) {
            usleep(500000); 
            ut_yield();
        }     
    }
}

///////////////////////////////////////////////////////////////////////
//                                                         		     //
//   Test 0: Only main thread	 							 		 //
//                                                         			 //
///////////////////////////////////////////////////////////////////////	

void test0_no_threads ()  {
    printf("\n :: Test 0 only main thread - BEGIN :: \n\n");

    ut_run();

    printf("\n\n :: Test 0 only main thread - END :: \n\n");
}

///////////////////////////////////////////////////////////////////////
//                                                         		     //
//        Test 1 One thread: Prints its number M times    			 //
//                                                         			 //
///////////////////////////////////////////////////////////////////////	

void test1_one_thread ()  {
    printf("\n :: Test 1 one thread - BEGIN :: \n\n");

    threads[0] = ut_create(test_thread, (void *) ('0'), LOW);

    ut_run();

    printf("\n\n :: Test 1 one thread- END :: \n\n");
}

///////////////////////////////////////////////////////////////////////
//                                                         		     //
//  Test 2 N threads: Prints its number M times with same priority   //
//                                                         			 //
///////////////////////////////////////////////////////////////////////	

void test2_same_priority(){
	printf("\n :: Test 2 with same priority - BEGIN :: \n\n");

	for (uint64_t index = 0; index < MAX_THREADS; ++index) {
		threads[index] = ut_create(test_thread, (void *) ('0' + index), LOW);
	}  

	ut_run();

	printf("\n\n :: Test 2 with same priority - END :: \n\n");
}

///////////////////////////////////////////////////////////////////////
//                                                         		     //
//   Test 3 N threads: Prints its number with different priorities	 //
//                                                         			 //
///////////////////////////////////////////////////////////////////////	

void test3_different_priorities ()  {
    printf("\n :: Test 3 with different priorities - BEGIN :: \n\n");

    threads[0] = ut_create(test_thread, (void *) ('0'), LOW);
	threads[1] = ut_create(test_thread, (void *) ('1'), LOW);
	threads[2] = ut_create(test_thread, (void *) ('2'), NORMAL);
	threads[3] = ut_create(test_thread, (void *) ('3'), NORMAL);
	threads[4] = ut_create(test_thread, (void *) ('4'), HIGH);
	threads[5] = ut_create(test_thread, (void *) ('5'), HIGH);

    ut_run();

    printf("\n\n :: Test 3 with different priorities - END :: \n\n");
}

void to_continue(){
	printf("\n\nPRESS [ENTER] TO CONTINUE\n\n");
	getchar();
}

int main () {
	ut_init();

	test0_no_threads();
    to_continue();
 
	test1_one_thread();
    to_continue();

	test2_same_priority();
    to_continue();

	test3_different_priorities();
    to_continue();
	 
	ut_end();

	return 0;
}


