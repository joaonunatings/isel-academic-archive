# server

Simple server which supports TCP and UDP requests. This implementation makes use of the [_libuv_](https://libuv.org/) library.

## Information

This is the fifth problem set for Operating Systems @ ISEL (now called [Systems Virtualization Techniques](https://www.isel.pt/en/leic/systems-virtualization-techniques)).

- [Problem description](docs/problem-description.pdf)

### Supported requests
This server accepts TCP and UDP requests and process them accordingly.

#### TCP Requests
- Connect using **port 54321** and server echoes client's message in **lowercase**.
- Connect using **port 56789** and server echoes client's message in **uppercase**.

#### UDP Requests
- Client sends an 8 bytes request containing:
  - byte 0: character 'Q'
  - byte 1: type of request:
    - '1' - total sessions established using port 54321
    - '2' - total sessions established using port 56789
    - '3' - total characters received using port 54321
    - '4' - total characters received using port 56789
  - bytes 2-5: arbitrary identifier (client defined)
  - bytes 6-7: sequence number (client defined)

- Server sends a 16 bytes response
  - byte 0: character 'A'
  - byte 1: type of response (same as type of request)
  - bytes 2-5: arbitrary identifier (client defined)
  - bytes 6-7: sequence number (client defined)
  - bytes 8-15: response value

## How to run

### Requirements
- Unix based OS
- [Make](https://www.gnu.org/software/make/)
- [GCC](https://gcc.gnu.org/)
- [libuv](https://libuv.org/)

The following instructions must be executed in the [root directory of the project](./) (_isel-academic-archive/year_2/semester_4/SO/server_).

### Compile
`make`

### Run
`./server`

For the client you can use [netcat](https://netcat.sourceforge.net/).

## Authors
- João Nunes ([joaonunatings](https://github.com/joaonunatings))
- Alexandre Silva ([Cors00](https://github.com/Cors00))
- Miguel Marques ([mjbmarques](https://github.com/mjbmarques))