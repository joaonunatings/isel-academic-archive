#include "semaphore.h"

#include <stdio.h>
#include <pthread.h>

semaphore_t sem;
int timeout;

void * adquire(void * arg) {
    int units = *((int *) arg);
    semaphore_acquire(&sem, units);
    printf("Took %d units from semaphore\n", units);
    return NULL;
}

void * adquire_timed(void * arg) {
    int units = *((int *) arg);
    semaphore_acquire_timed(&sem, units, timeout);
    printf("Timeout or took %d units from semaphore\n", units);
    return NULL;
}

void * release(void * arg) {
    int units = *((int *) arg);
    semaphore_release(&sem, units);
    printf("Delivered %d units to semaphore\n", units);
    return NULL;
}

void adquire_all_units() {
    semaphore_init(&sem, 30);
    printf("Starting units: %d\n", sem.units);
    pthread_t thread1;
    pthread_t thread2;
    pthread_t thread3;
    int units[3] = { 15, 10, 5 };
    pthread_create(&thread1, NULL, adquire, (void * )(&units[0]));
    pthread_create(&thread2, NULL, adquire, (void * )(&units[1]));
    pthread_create(&thread3, NULL, adquire, (void * )(&units[2]));

    pthread_join(thread1, NULL);
    pthread_join(thread2, NULL);
    pthread_join(thread3, NULL);
    printf("Ending units: %d\n", sem.units);
    pthread_mutex_destroy(&sem.lock);
    pthread_cond_destroy(&sem.wait_cond);
}

void adquire_not_enough_units() {
    semaphore_init(&sem, 30);
    printf("Starting units: %d\n", sem.units);
    pthread_t thread1;
    pthread_t thread2;
    pthread_t thread3;
    int units[2] = { 30, 10 };
    pthread_create(&thread1, NULL, adquire, (void * )(&units[0]));
    pthread_create(&thread2, NULL, adquire, (void * )(&units[1]));

    int units_to_release = 0;
    puts("> Insert units to release: ");
    scanf("%d", &units_to_release);
    pthread_create(&thread3, NULL, release, (void *)(&units_to_release));
    printf("> Released %d units (Press enter to continue)\n", units_to_release);

    pthread_join(thread1, NULL);    
    pthread_join(thread2, NULL);
    pthread_join(thread3, NULL);
    printf("Ending units: %d\n", sem.units);
    pthread_mutex_destroy(&sem.lock);
    pthread_cond_destroy(&sem.wait_cond);
}

void adquire_timed_not_enough_units() {
    semaphore_init(&sem, 30);
    printf("Starting units: %d\n", sem.units);
    puts("> Insert timeout for adquire: ");
    scanf("%d", &timeout);
    pthread_t thread1;
    pthread_t thread2;
    pthread_t thread3;
    int units[2] = { 30, 10 };
    pthread_create(&thread1, NULL, adquire, (void * )(&units[0]));
    pthread_create(&thread2, NULL, adquire_timed, (void * )(&units[1]));

    int units_to_release = 0;
    puts("> Insert units to release: ");
    scanf("%d", &units_to_release);
    pthread_create(&thread3, NULL, release, (void *)(&units_to_release));
    printf("> Released %d units\n", units_to_release);

    pthread_join(thread1, NULL);    
    pthread_join(thread2, NULL);
    pthread_join(thread3, NULL);
    printf("Ending units: %d\n", sem.units);
    pthread_mutex_destroy(&sem.lock);
    pthread_cond_destroy(&sem.wait_cond);
}

int main() {
    puts("\n> Start of test: only_adquire_no_release");
    adquire_all_units();
    puts("> End of test (Press enter to continue)\n"); getchar();

    puts("\n> Start of test: adquire_not_enough_units");
    adquire_not_enough_units();
    puts("> End of test (Press enter to continue)\n"); getchar();

    puts("\n> Start of test: adquire_timed");
    adquire_timed_not_enough_units();
    puts("> End of test (Press enter to continue)\n"); getchar();
}