# Weather

CLI application which shows information about the weather. 
Given a set of locations and a date, the program creates a CSV file with weather information for each location.

## Information

This was the last problem set (of three, being the first two mere academic exercises) for [Computer Systems Programming @ ISEL](https://www.isel.pt/en/leic/computer-systems-programming).
This program uses the [OpenWeather API](https://openweathermap.org/api) in order to accurately retrieve weather information.

- [Project description](docs/project-description.pdf) (Portuguese)

## How to run

### Requirements

Obtain an API key from [OpenWeather API](https://openweathermap.org/api). Then set the key to the environment variable **WEATHER_API_KEY**.

#### Software
- Unix based OS
- [Make](https://www.gnu.org/software/make/)
- [GCC](https://gcc.gnu.org/)

#### Libraries
- [jansson](https://github.com/akheron/jansson)
- [curl](https://curl.se/)

The following instructions must be executed in the root directory of the project (isel-academic-archive/year_2/semester_3/PSC/Weather)

### Compile
1. `make` 
2. `export LD_LIBRARY_PATH="$(pwd)/lib":$LD_LIBRARY_PATH`

### Run
`./weather <date> <locations file>` 

The file _locations_ provides a simple example of real locations, so you can run: `./weather 11/12/2022 locations` (please mind the date limits according to [this](https://openweathermap.org/api))

## Authors
- João Nunes ([joaonunatings](https://github.com/joaonunatings))
- Pedro Batista ([bigskydiver](https://github.com/bigskydiver))
