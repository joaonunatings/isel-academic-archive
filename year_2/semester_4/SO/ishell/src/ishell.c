#include "ishell.h"
#include <stdio.h>
#include <unistd.h>
#include <errno.h>
#include <sys/wait.h>
#include <string.h>
#include <stdlib.h>
#include <fcntl.h>

//Process user input
int main() {
    char cmd[BUF_SIZE];
    for (;;) {
        printf("%s$ %s", COLOR_GREEN, COLOR_RESET);
        if (!fgets(cmd, BUF_SIZE - 1, stdin)) {
            print_err(-1, "Stdout closed");
            exit(-1);
        }
        size_t pos = strlen(cmd) - 1;
        if (cmd[pos] == '\n') cmd[pos] = 0;
        if (strcmp(cmd, "exit") == 0) {
            puts("exit");
            break;
        } 
        run_cmd(cmd);
    }
    return 0;
}
//Handles pipes and splits commands
void run_cmd(char *cmd) {
    char* token, *save_ptr = cmd, *filename = NULL;
    char* args[ARGS_SIZE] = { NULL };
    int arg_count = 0;
    int ipipe[2] = STD_DEFAULT, opipe[2] = STD_DEFAULT;
    //Keeps splitting by space and adding arguments until no more args to split
    while ((token = strtok_r(save_ptr, " ", &save_ptr))) {
        if (*token == '|') {
            if(pipe(opipe) == -1) {
                print_err(errno, strerror(errno));
                return;
                }
            args[arg_count] = NULL;
            new_fork(args, filename, ipipe, opipe);
            filename = NULL;
            memcpy(ipipe, opipe, sizeof(ipipe));
            arg_count = 0;
        } else if (*token == '>') {
            filename = strtok_r(save_ptr, " ", &save_ptr);
            if(filename == NULL) {
                print_err(3, "Failed getting file");
                return;
            }
        } else {
            args[arg_count++] = token;
        }
        
    }
    //Last command to be ran
    if (arg_count > 0) {
        memcpy(ipipe, opipe, sizeof(ipipe));
        args[arg_count] = NULL;
        int last_pipe[2] = STD_DEFAULT;
        new_fork(args, filename, ipipe, last_pipe);

        int status;
        pid_t wpid;
        //Waits for all childs to finish/die
        while ((wpid = wait(&status)) > 0) {
            if (!WIFEXITED(status)) {
                print_err(status, "Child exited abnormally");
                return;
            }
        }
    }
}

//Creates a new fork to exec the given args command with the corresponding pipes
pid_t new_fork(char* args[], char* filename, int ipipe[], int opipe[]) {
    int out_fd = STDOUT_FILENO;
    pid_t pid = fork();

    switch (pid) {
            //Error case
            case -1:
                print_err(-1, strerror(errno));
                break;
            //Child process
            case 0:
                if (filename) {
                    out_fd = open(filename, O_WRONLY | O_CREAT | O_TRUNC, 0666);
                    if (out_fd == -1) {
                        print_err(-1, strerror(errno));
                        _exit(-1);
                    }
                }
                child_proc(args, ipipe, opipe, out_fd);
                break;
            //Parent process
            default:
                parent_proc(pid, ipipe);
                break;
        }
    return pid;
}

//Parent process that closes unused pipe
void parent_proc(pid_t pid, int pipe[]) {
    if (pipe[P_RD] != STDIN_FILENO) {
        close(pipe[P_WR]);
        close(pipe[P_RD]);
    }
}

//Child process that is in charge of redirecting input and output
void child_proc(char* args[], int ipipe[], int opipe[], int out_fd) {
    //Handling input
    if (ipipe[P_RD] != STDIN_FILENO) {
        close(ipipe[P_WR]);
        dup2(ipipe[P_RD], STDIN_FILENO);
        close(ipipe[P_RD]);
    }

    //Handling output
    if (opipe[P_WR] != STDOUT_FILENO) {
        close(opipe[P_RD]);
        dup2(opipe[P_WR], STDOUT_FILENO);
        close(opipe[P_WR]);
    }
    //Handling filename descriptor
    if (out_fd != STDOUT_FILENO) {
        dup2(out_fd, STDOUT_FILENO);
        close(out_fd);
    }

    int err = execvp(args[0], args);
    //Should never get here unless error occurred
    print_err(err, strerror(errno));
    _exit(err);
}

//Prints error occurred
void print_err(int res, char* message) {
    printf("%sError %d%s : %s\n", COLOR_RED, res, COLOR_RESET, message);
}