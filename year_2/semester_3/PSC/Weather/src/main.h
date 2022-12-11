#ifndef WEATHER_MAIN_H
#define WEATHER_MAIN_H

#include "../lib/inc/weather.h"
#include <stdio.h>

#define MIN_YEAR 1900
#define MIN_MONTH 1
#define MAX_MONTH 12
#define MIN_DAY 1
#define MAX_DAY 31

#define YEAR_LIMIT(year) (year >= MIN_YEAR)
#define MONTH_LIMIT(month) (month >= MIN_MONTH && month <= MAX_MONTH)
#define DAY_LIMIT(day) (day >= MIN_DAY && day <= MAX_DAY)

#define OUTPUT_FILENAME "output.csv"

locationList* read_locations(FILE* file);

void write_weather(Date* date, weatherList* weathers);

#endif //WEATHER_MAIN_H
