# Travel4Me

CLI application using a database system to track travels, reservations and expenses from bus or train transportation.

## Information
Project for Information Systems I @ ISEL (now called [Introduction to Information Systems](https://www.isel.pt/en/leic/introduction-information-systems)).
The project was divided in 3 phases.

- [Project description](docs/project-description.pdf)
- [Report phase 1](docs/report1.pdf)
- [Report phase 3](docs/report2.pdf)

## How to run

### Requirements
- SQL Server ([guide](https://learn.microsoft.com/en-us/sql/database-engine/install-windows/install-sql-server?view=sql-server-ver16))
- Java SDK 8

The following instructions must be executed in the root directory of the software part of this project (isel-academic-archive/year_2/semester_3/SI1/Travel4Me)

### Database
1. Create a database in SQL Server ([guide](https://learn.microsoft.com/en-us/sql/relational-databases/databases/create-a-database?view=sql-server-ver16))
2. Execute the script [createTable.sql](src/main/sql/createTable.sql) to create the tables.
3. Execute the script [insertTable.sql](src/main/sql/insertTable.sql) to insert the data. Keep in mind the date which this is executed because it can break certain constraints.

- If you wish to delete all the data from the tables, execute the script [deleteTable.sql](src/main/sql/deleteTable.sql).
- If you wish to drop all the tables, execute the script [dropTable.sql](src/main/sql/removeTable.sql).

### Compile

#### Linux
`./gradlew clean fatJar`

#### Windows
`gradlew clean fatJar`

### Run
There's a set of environment variables you must set before running the application:
- _T4M_USER_ - user to connect to the database (default: _sa_)
- _T4M_PASSWORD_ - password to connect to the database
- _T4M_DATABASE_ - database name (must set)
- _T4M_IP_ - database ip address (default: _localhost_)
- _T4M_PORT_ - database port (default: _1433_)

After this, you can run:

`java -jar build/libs/Travel4Me-1.0-SNAPSHOT.jar`

## Authors
- João Nunes ([joaonunatingscode](https://github.com/joaonunatingscode))
- Alexandre Silva ([Cors00](https://github.com/Cors00))