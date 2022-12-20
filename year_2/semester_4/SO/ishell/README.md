# ishell

Simple shell. This implementation makes use of _fork()_ and pipes its data accordingly.

## Information
This is the first problem set for Operating Systems @ ISEL (now called [Systems Virtualization Techniques](https://www.isel.pt/en/leic/systems-virtualization-techniques)).

- [Problem description](docs/problem-description.pdf)

## How to run

### Requirements
- Unix based OS
- [Make](https://www.gnu.org/software/make/)
- [GCC](https://gcc.gnu.org/)

The following instructions must be executed in the root directory of the project (isel-academic-archive/year_2/semester_4/SO/ishell)

### Compile
`make`

### Run
1. `./ishell`

Or create a user and add a shell to it (needs root permission):
1. `useradd <user_name> -m -s "$(pwd)/ishell"`
2. `passwd <user_name>`
3. `su <user_name>`

## Authors
- João Nunes ([joaonunatingscode](https://github.com/bigskydiver))
- Alexandre Silva ([Cors00](https://github.com/Cors00))
- Miguel Marques ([mjbmarques](https://github.com/mjbmarques))