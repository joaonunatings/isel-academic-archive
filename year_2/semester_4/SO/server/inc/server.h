///////////////////////////////////////////
//
// ISEL LEIC G01 LI42D
// SV - 20/21
// Série 6
//
// Asynchronous Server
//
// Authors:
// Alexandre Silva - 47192
// João Nunes - 47220
// Miguel Marques - 47204
//
///////////////////////////////////////////
#pragma once

#include <uv.h>

#define UDP_REQ_LEN 8
#define UDP_RES_LEN 16

enum REQ_TYPE {
    TOTAL_SESSIONS_54321,
    TOTAL_SESSIONS_56789,
    TOTAL_CHARS_54321,
    TOTAL_CHARS_56789,
    NUM_REQS
};

static int data[NUM_REQS];


/* COMMON */
void close_cb(uv_handle_t* handle);

void alloc_cb(uv_handle_t* handle, size_t suggested_size, uv_buf_t* buf);


/* TCP */
uv_tcp_t* tcp_server_init(uv_loop_t* loop, char* addr_str, int port, void (*func)(char* buf));

void tcp_client_init(uv_stream_t* server, uv_loop_t* loop, void (*func)(char* buf));

void connection_cb(uv_stream_t* server, int status);

void read_cb(uv_stream_t *stream, ssize_t nread, const uv_buf_t *buf);

void write_cb(uv_write_t* req, int status);


/* UDP */
uv_udp_t* udp_server_init(uv_loop_t* loop, char* addr_str, int port);

void udp_recv_cb(uv_udp_t *handle, ssize_t nread, const uv_buf_t *buf, const struct sockaddr *addr, unsigned flags);

int udp_process_response(char* buf);

void udp_send_cb(uv_udp_send_t* req, int status);