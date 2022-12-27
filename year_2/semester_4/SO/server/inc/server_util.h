#pragma once

#include <uv.h>
#include <stdbool.h>

#define SRV_DEFAULT_ADDR_STR "0.0.0.0"
#define SRV_MAX_BACKLOG 5

#define CHECK(err, msg, quit)           \
    if (err < 0)                        \
        error(err, msg, quit)           \

typedef struct loop_data {
    void* null;
} loop_data_t;

uv_loop_t* loop_init();

void loop_destroy(uv_loop_t* loop);


/* COMMON */
void error(int err, const char * msg, bool exit);

void get_ip4_name(struct sockaddr_in handle_addr, char* handle_addr_str);

struct sockaddr_in get_ip4_addr(char* addr_str, int port);

void handle_destroy(uv_handle_t* handle);


/* TCP */
typedef struct tcp_data {
    char* addr_str;
    int port;
    void (*func)(char* buf);
} tcp_data_t;

tcp_data_t* tcp_data_init(char* addr_str, int port, void (*func)(char* buf));

void tcp_data_destroy(tcp_data_t* tcp_data);

uv_tcp_t* tcp_handle_init(uv_loop_t* loop, char* addr_str, int port, void (*func)(char* buf));

void tcp_handle_destroy(uv_tcp_t* handle);

struct sockaddr_in get_tcp_peername(uv_tcp_t* handle);

struct sockaddr_in get_tcp_sockname(uv_tcp_t* handle);


/* UDP */
typedef struct udp_data {
    char* addr_str;
    int port;
    // * ...
} udp_data_t;

udp_data_t* udp_data_init(char* addr_str, int port);

void udp_data_destroy(udp_data_t* udp_data);

uv_udp_t* udp_handle_init(uv_loop_t* loop, char* addr_str, int port);

void udp_handle_destroy(uv_udp_t* handle);