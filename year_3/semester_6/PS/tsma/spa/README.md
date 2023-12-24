# Team Shift Management Application - SPA
Developed by: Alexandre Luís & João Nunes

## Execution

### Requirements
- [Docker](https://www.docker.com/) (with Docker Compose)

### Configuration
Configuration file example: _.env.example_

Please configure the AZURE_CLIENT_ID environment variable. Access Azure AD and copy the Client (application) ID.

After editing this file, rename it to _.env_.

### Run
Make sure the Docker daemon is running.
First run will take a while because it will pull images from Docker Hub.

#### Linux & Mac

Start: `./run.sh start`

Stop: `./run.sh stop`

#### Windows

Start: `./run.cmd start`

Stop: `./run.cmd stop`
