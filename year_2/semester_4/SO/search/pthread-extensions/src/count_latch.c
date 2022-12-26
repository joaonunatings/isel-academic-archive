#include "count_latch.h"
#include <unistd.h>
#include <stdio.h>

void cl_init(count_latch_t *latch, int par_level) {
    pthread_mutex_init(&latch->lock, NULL);
	pthread_cond_init(&latch->waiters, NULL);
    pthread_cond_init(&latch->waiters_set, NULL);
    latch->par_level = par_level;
	latch->worker_count = 0;
    latch->waiters_count = 0;
}

void cl_wait_all(count_latch_t *latch) {
    pthread_mutex_lock(&latch->lock);
    {
        while(latch->worker_count > 0) 
            pthread_cond_wait(&latch->waiters_set, &latch->lock);
    }
    pthread_mutex_unlock(&latch->lock);
}

void cl_up(count_latch_t *latch) {
    pthread_mutex_lock(&latch->lock);
    {
        while(latch->par_level != 0 && latch->worker_count >= latch->par_level) {
            latch->waiters_count += 1;
            pthread_cond_wait(&latch->waiters, &latch->lock);
            latch->waiters_count -= 1;
        }
        latch->worker_count += 1;
    }
    pthread_mutex_unlock(&latch->lock);
}

void cl_down(count_latch_t *latch) {
    pthread_mutex_lock(&latch->lock);
    {
        if (latch->worker_count != 0) {
            latch->worker_count -= 1;

            if (latch->worker_count == 0) 
                pthread_cond_broadcast(&latch->waiters_set);
            else
                if (latch->worker_count < latch->par_level && latch->waiters_count > 0)
                    pthread_cond_signal(&latch->waiters);
        }
    }
    pthread_mutex_unlock(&latch->lock);
}

void cl_cleanup(count_latch_t * latch) {
	pthread_mutex_destroy(&latch->lock);
	pthread_cond_destroy(&latch->waiters);
    pthread_cond_destroy(&latch->waiters_set);
}