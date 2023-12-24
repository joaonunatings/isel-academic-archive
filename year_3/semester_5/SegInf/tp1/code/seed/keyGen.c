#include <string.h>
#include <stdio.h>
#include <stdlib.h>

#define KEYSIZE 16

static char key[KEYSIZE];

static unsigned int defaultStartTime = 1524013729;
static unsigned int defaultEndTime = 1524020929;

int main(int argc, char* argv[]) {

	if (argc < 3) {
		printf("Provide program arguments:\nkeyGen <startTime> <endTime> [outFile]\nYou can also use the LAB values for <startTime> and <endTime> with 'default' keyword\n");
		exit(-1);
	
	}
	unsigned int startTime, endTime;
	if (strcmp(argv[1], "default") == 0)
		startTime = defaultStartTime;
	else 
		startTime = strtol(argv[1], NULL, 10);
	
	if (strcmp(argv[2], "default") == 0)
		endTime = defaultEndTime;
	else
		endTime = strtol(argv[2], NULL, 10);

	if (startTime > endTime) {
		printf("Provide a valid timestamp\n");
		exit(-1);
	}

	printf("Start time: %d\nEnd time: %d\n", startTime, endTime);

	char filename[256];

	if (argc > 3) {
		strcpy(filename, argv[3]);
	} else {
		strcpy(filename, "keys\0");
	}

	printf("Filename: %s\n", filename);

	FILE *out_file = fopen("keys", "w");

	if (out_file == NULL) {
		printf("Error! Could not open file\n");
		exit(-1);
	}

	puts("Generating keys...");

	for (int i = 0; i < 10; i++) {
		printf("\rNumber %d", i);
		fflush(stdout);
	}

	int keyNumber = 1;
	for (unsigned int seed = startTime; seed <= endTime; ++seed) {	
	        srand(seed);    
    
        	for (int i = 0; i< KEYSIZE; i++) {    
                	key[i] = rand()%256;    
                	fprintf(out_file, "%.2x", (unsigned char)key[i]);    
        	}    
        	fprintf(out_file, "\n");

		printf("\rKey no. %d: ", keyNumber);
		fflush(stdout);
		char* keyPtr = key;
		while (*keyPtr) {
			printf("%02x", (unsigned char)*keyPtr++);
		}

		keyNumber++;
	}

	fclose(out_file);

	puts("\nDone!");
}
