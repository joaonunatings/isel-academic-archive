# UThread

Simple thread library. This implementation features [semaphore](https://en.wikipedia.org/wiki/Semaphore_(programming)) and [cyclic barrier](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/CyclicBarrier.html).

## Information
This is the fourth problem set for Operating Systems @ ISEL (now called [Systems Virtualization Techniques](https://www.isel.pt/en/leic/systems-virtualization-techniques)).

- [Problem description](docs/problem-description.pdf)

## How to run

### Requirements
- Unix based OS
- [Make](https://www.gnu.org/software/make/)
- [GCC](https://gcc.gnu.org/)

The following instructions must be executed in the root directory of the project (isel-academic-archive/year_2/semester_4/SO/uthread)

### Compile
1. `make`
2. `export LD_LIBRARY_PATH="$(pwd)/bin":$LD_LIBRARY_PATH`

### Run
There are tests in the _bin/test_ which you can run:

`./bin/test/<name of test>`

## Authors
- João Nunes ([joaonunatingscode](https://github.com/bigskydiver))
- Alexandre Silva ([Cors00](https://github.com/Cors00))
- Miguel Marques ([mjbmarques](https://github.com/mjbmarques))