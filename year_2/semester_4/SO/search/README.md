# search

Simple program which searches for files containing a string. The user must input a folder, a match string and a file extension suffix.
This implementation uses the _pthread-extensions_ which is a library containing semaphore, count latch and thread pool implementations (also part of this problem set).

## Information
This is the fifth problem set for Operating Systems @ ISEL (now called [Systems Virtualization Techniques](https://www.isel.pt/en/leic/systems-virtualization-techniques)).

- [Problem description](docs/problem-description.pdf)

There are two available implementations for this program:

- **search**: searches the files sequentially.

- **search_mt**: searches the files using a multithreading environment by using the _pthread-extensions_ library.

## How to run

### Requirements
- Unix based OS
- [Make](https://www.gnu.org/software/make/)
- [GCC](https://gcc.gnu.org/)

The following instructions must be executed in the [root directory of the project](./) (_isel-academic-archive/year_2/semester_4/SO/search_).

### Compile
1. `make`
2. `export LD_LIBRARY_PATH="$(pwd)/pthread-extensions/bin":$LD_LIBRARY_PATH`

### Run
`./search_mt <folder> <text> <suffix>`

There are tests for _pthread-extensions_ library present in [_pthread-extensions/bin/test_](pthread-extensions/bin/test)

## Authors
- João Nunes ([joaonunatings](https://github.com/joaonunatings))
- Alexandre Silva ([Cors00](https://github.com/Cors00))
- Miguel Marques ([mjbmarques](https://github.com/mjbmarques))