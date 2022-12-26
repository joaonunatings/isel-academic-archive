#pragma once

#define MAX_PATH 1024
#define MAX_LINE  512

// result status
#define TOO_MANY_FOUND_FILES  1
#define OK 0
#define NTHREADS 9
#define MAX_PAR_LEVEL 3

typedef struct {
	char *path;				// file path
	int count;				// ocurrences count in file
} fresult_t;

typedef struct  {
	int  status;			// the search status
	 	
	int total_processed;	// total processed files
	int total_results;		// total files containing word
	int total_ocorrences;	// total word ocurrences in files
	int results_capacity;	// capacity of results array
	fresult_t *results; 	// describe files containing word
} search_result_t;

typedef struct search_text_args{
    const char * path;
    const char * to_find;
    search_result_t * res;
}search_text_args_t;


// search function
void search(const char *path, const char* to_find, const char *suffix, search_result_t *res);
