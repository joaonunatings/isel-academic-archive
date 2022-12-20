/*
Custom implementation of Unix's shell, capable of executing programs with or without redirection of input and/or output.

Example of commands that supports:
$ ls
$ cat text.txt | grep abc | sort > out.txt
$ useradd userName -m -s path_to_new_shell
$ exit

Project made for Operating Systems subject teached by João Trindade in 20/21.
Authors: 
- Alexandre Silva Nr 47192 LI42D 
- João Nunes Nr 47220 LI42D 
- Miguel Marques Nr 47204 LI42D 
*/

#ifndef ISHELL_H
#define ISHELL_H

#include <unistd.h>

#define BUF_SIZE 1024
#define ARGS_SIZE 127

#define P_RD 0
#define P_WR 1

#define COLOR_GREEN "\033[0;32m"
#define COLOR_RED "\033[0;31m"
#define COLOR_RESET "\033[0m"

#define STD_DEFAULT {STDIN_FILENO, STDOUT_FILENO}

void print_err(int errnum, char* err_message);
void run_cmd(char* cmd);
pid_t new_fork(char* args[], char* filename, int ipipe[], int opipe[]);
void parent_proc(pid_t pid, int pipe[]);
void child_proc(char* args[], int ipipe[], int opipe[], int out_fd);

#endif 