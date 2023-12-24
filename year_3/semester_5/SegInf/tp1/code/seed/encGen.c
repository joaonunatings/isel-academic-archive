#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <unistd.h>

#define KEYSIZE 16

void generate_key(unsigned int seed);

int main() {
	printf("Time since epoch: %lld\n\n", (long long) time(NULL));

	printf("Running without feeding a seed (srand uses default seed: 1)\n");
	for(int i = 0; i < 3; ++i) {
		generate_key(1);
	}

	printf("Running with a seed time(NULL)\n");
	for(int i = 0; i < 3; ++i) {
		generate_key(time(NULL));
	}	
	printf("Waiting 1 seconds, seed should be different\n");
	sleep(1);
	generate_key(time(NULL));
	
}

void generate_key(unsigned int seed) {
	char key[KEYSIZE];

	srand(seed);

	for (int i = 0; i< KEYSIZE; i++) {
		key[i] = rand()%256;
		printf("%.2x", (unsigned char)key[i]);
	}
	printf("\n");
}
