#include "semaphore.h"

#include <stdbool.h>
#include <pthread.h>
#include <stdlib.h>
#include <time.h>

void semaphore_init(semaphore_t *sem, int initial) {
    pthread_mutex_init(&sem->lock, NULL);
    pthread_cond_init(&sem->wait_cond, NULL);
    sem->units = initial;
}
 
void semaphore_acquire(semaphore_t *sem, int units) {
    pthread_mutex_lock(&sem->lock);
    {
        while (sem->units < units) {
            pthread_cond_wait(&sem->wait_cond, &sem->lock);
        }
        sem->units -= units;
    }
    pthread_mutex_unlock(&sem->lock);
}

bool semaphore_acquire_timed(semaphore_t *sem, int units, long millis) {
    int res = -1;
    pthread_mutex_lock(&sem->lock);
    {
        static struct timespec ts = {0, 0};
        ts.tv_sec = time(NULL) + millis / 1000;
        while (sem->units < units) {
            res = pthread_cond_timedwait(&sem->wait_cond, &sem->lock, &ts);
            if (res != 0)
                goto end;
        }
        sem->units -= units;
    }
end:
    pthread_mutex_unlock(&sem->lock);
    return (res == 0) ? true : false;
}


void semaphore_release(semaphore_t *sem, int units) {
    pthread_mutex_lock(&sem->lock);
    {
        sem->units += units;
        if (sem->units > 0)
            pthread_cond_broadcast(&sem->wait_cond);
    }
    pthread_mutex_unlock(&sem->lock);
}