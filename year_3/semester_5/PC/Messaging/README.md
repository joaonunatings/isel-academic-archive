# Messaging

TCP/IP interface used for messaging between connected clients.

## Information

Last problem set for [Concurrent Programming @ ISEL](https://www.isel.pt/en/leic/concurrent-programming).

- [Project description](Docs/project-description.pdf)
- [Implementation details](Docs/implementation-details.md)

## How to run

### Requirements
- .NET Framework 4.8

The following instructions must be executed in the [root directory of the project](./) (isel-academic-archive/year_2/semester_4/PC/Messaging).

### Run

#### Server
`dotnet run --project App`

#### Client(s)
You can use _telnet_: `telnet localhost 8080` (connect to server).

Available commands:
- _/enter \<chatroom name\>_ - enter chatroom
- _/leave_ - leave current chatroom
- _/exit_ - disconnect from server

## Authors
- João Nunes ([joaonunatingscode](https://github.com/joaonunatingscode))
- Alexandre Silva ([Cors00](https://github.com/Cors00))
- Miguel Marques ([mjbmarques](https://github.com/mjbmarques))