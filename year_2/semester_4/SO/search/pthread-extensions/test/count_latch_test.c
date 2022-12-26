#include <unistd.h>
#include <stdio.h>
#include <pthread.h>
#include "count_latch.h"

void * notifier_thread(void * arg) {
	count_latch_t * cl  = (count_latch_t *)arg;

	printf("\nNotifier thread begin\n");

	usleep(2000000);
	 
    pthread_mutex_lock(&cl->lock);
	{
        printf("\nNotifier thread end: %d\n", cl->worker_count);
    }
    pthread_mutex_unlock(&cl->lock);

    cl_down(cl);

    return NULL;
}

void * waiter_thread(void * arg) {
	count_latch_t * cl  = (count_latch_t *)arg;

	printf("\nMAX PAR_LEVEL thread begin\n");

	usleep(4000000);
	 
    pthread_mutex_lock(&cl->lock);
	{
        printf("\nMAX PAR_LEVEL thread end: %d\n", cl->worker_count);
    }
    pthread_mutex_unlock(&cl->lock);

    cl_down(cl);

    return NULL;
}

int main() {

	count_latch_t cl;
	pthread_t source1, source2, source3, source4, source6, source7;
	
	cl_init(&cl, 3);
	
	printf(":: START ::\n");
	
	cl_up(&cl);
	pthread_create(&source1, NULL, notifier_thread, &cl);
	cl_up(&cl);
	pthread_create(&source2, NULL, notifier_thread, &cl);
	cl_up(&cl);
    pthread_create(&source3, NULL, notifier_thread, &cl);
	cl_up(&cl);
	pthread_create(&source4, NULL, waiter_thread, &cl);
	cl_wait_all(&cl);

	cl_up(&cl);
    pthread_create(&source6, NULL, notifier_thread, &cl);
	cl_wait_all(&cl);
	printf("\nOK1\n");
	
	cl_up(&cl);
	pthread_create(&source7, NULL, notifier_thread, &cl);
	cl_wait_all(&cl);
	printf("\nOK2\n");
	
	printf("\n:: DONE ::\n");
	
	pthread_join(source1, NULL);
	pthread_join(source2, NULL);
    pthread_join(source3, NULL);
    pthread_join(source4, NULL);
	pthread_join(source6, NULL);
    pthread_join(source7, NULL);
	
	printf(":: END ::\n");

	return 0;
}
