#include <stdio.h>

#include <assert.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>
#include <dirent.h>
#include <strings.h>
#include <unistd.h>


#include "search_mt.h"
#include "thread_pool.h"
#include "count_latch.h"

thread_pool_t tp;
count_latch_t cl;

// utilitary functions

// read a line from a file 
static int readline(FILE *f, char line[], int capacity) {
	int i = 0, c;
	while (i < capacity - 1 && (c = fgetc(f)) != EOF && c != '\n')
		line[i++] = c;
	line[i] = 0;
	while (c != EOF && c != '\n') c = fgetc(f);
 	return (i != 0 || c != EOF) ? i : -1;
}

// check if the string "suffix" ends the string "s"
static bool strends(const char *s, const char *suffix) {
	int is = strlen(s) - 1, ip = strlen(suffix) - 1;
	
	while (is >= 0 && ip >= 0 && s[is] == suffix[ip]) { is--; ip--; }

	return ip == -1;
}

// 
// auxiliary functions for search
//

// create a structure that will contain the search results
void search_result_init(search_result_t *res, int limit) {
	bzero(res, sizeof(search_result_t));
	res->results = (fresult_t *) malloc(sizeof(fresult_t)*limit);
	res->results_capacity = limit;
}

// destroy the search results
void search_result_destroy(search_result_t *res) {
	for (int i = 0; i < res->total_results; ++i)  
		free(res->results[i].path);
	free(res->results);
}

/*
   if *fes is NULL force it to point to the next free fresult_t in res
   return false if no free fresult_t exists on res
*/
static bool enforce_result(search_result_t * res, fresult_t ** fres) {
	if (*fres != NULL) return true;
	if (res->total_results == res->results_capacity) return false;
	*fres = res->results + res->total_results;
	res->total_results++;
	(*fres)->count = 0;
	return true;
}

/*
 * search a word in the file with the specified name collecting
 * the result in res. Errors are ignored, but if the found files limit
 * is achieved the fact is memorized in result
 */
void search_text(void * args) {
    
    search_text_args_t * arguments = (search_text_args_t *) args;
    const char * path = arguments->path;
    const char * to_find = arguments->to_find;
    search_result_t * res = arguments->res;

	FILE *f = fopen(path, "r");
	if (f != NULL) {
        char line[MAX_LINE];

        fresult_t * presult = NULL;
        int status = OK;
        pthread_mutex_lock(&cl.lock);
        {
            while (readline(f, line, MAX_LINE) != -1) {
                if (strstr(line, to_find) != NULL) {
                    if (!enforce_result(res, &presult) ) {
                        status = TOO_MANY_FOUND_FILES ;
                        break;
                    }
                    presult->count++;
                }
            }
            if (presult != NULL) {
                presult->path = strdup(path);
                res->total_ocorrences += presult->count;
            }
            res->status |= status;
        }
        pthread_mutex_unlock(&cl.lock);
        fclose(f);
    }
    free(&arguments->path);
    cl_down(&cl);
}

/*
 * Search the folder and corresponding sub-folders  
 * where to find files containing the string "to_find".
 * 
 * Each file is assigned to a single thread, with a max 
 * number of parallel threads running, and each search calls wait_all.
 * 
 * fills a result with the names and ocurrence count of the files that contains 
 * the text presented in "to_find"
 */
void search(const char * path, const char * to_find,  const char *suffix, search_result_t * res) {

	char buffer[MAX_PATH];		// auxiliary buffer

	DIR *dir;
    struct dirent *entry;

    if (!(dir = opendir(path)))
        return;

    while ((entry = readdir(dir)) != NULL) {
        if (entry->d_type == DT_DIR) {
            if (strcmp(entry->d_name, ".") == 0 || strcmp(entry->d_name, "..") == 0)
                continue;
            snprintf(buffer, MAX_PATH,  "%s/%s", path, entry->d_name);
            search(buffer, to_find, suffix, res);
        } else {
            if (strends(entry->d_name, suffix)) {

			    snprintf(buffer, MAX_PATH, "%s/%s", path, entry->d_name);
				
                search_text_args_t * st_args = (search_text_args_t *) malloc(sizeof(search_text_args_t));

                st_args->path = strdup(buffer);
                st_args->to_find = to_find;
                st_args->res = res;
                cl_up(&cl);
                thread_pool_submit(&tp, search_text, st_args);
				res->total_processed++;
			}
        }
    }
    cl_wait_all(&cl);
    closedir(dir);
}

void show_results( const char *folder, search_result_t * res) {
	
	int startidx = strlen(folder);
	for (int i = 0; i < res->total_results; ++i) {
		fresult_t* fres = res->results + i;
		printf("~%s(%d)\n", fres->path + startidx, fres->count);
	}
	
	printf("\n");
	printf("processed: %d\n", res->total_processed);
	printf("total found files: %d\n", res->total_results);
	printf("total word ocorrences: %d\n", res->total_ocorrences);
	
	if (res->status != OK) printf("warn: the found files limit was achieved\n"); 
}

int main(int argc, char *argv[]) {
	if (argc != 4) {
		printf("usage: traverse <folder> <text> <suffix>\n");
		return 1;
	}
	
	search_result_t result;
    
    cl_init(&cl, MAX_PAR_LEVEL);	
	thread_pool_init(&tp, NTHREADS);

	search_result_init(&result, 50000 /* found files limit */);
	
	search(argv[1], argv[2], argv[3],  &result); 

	show_results(argv[1], &result);

	search_result_destroy(&result);
    thread_pool_cleanup(&tp);
    cl_cleanup(&cl);
	 
	return 0;
}