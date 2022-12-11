//
// Created by nunes on 12/11/22.
//

#ifndef WEATHER_WEATHER_H
#define WEATHER_WEATHER_H

typedef struct date {
    int day;
    int month;
    int year;
} Date;

typedef struct weather {
    float temperature;
    float wind_speed;
    float humidity;
    float cloud_cover;
} Weather;

typedef struct location {
    float latitude;
    float longitude;
    char *name;
} Location;

typedef struct weatherNode {
    Location* location;
    Weather* weather;
    struct weatherNode* next;
} weatherList;

weatherList* addWeather(weatherList* listHead, Weather* weather, Location* location);

typedef struct locationNode {
    Location* location;
    struct locationNode* next;
} locationList;

locationList* addLocation(locationList* listHead, char* name, float latitude, float longitude);

weatherList* weather_get_info(Date* date, locationList* locations);

void weather_free_info(weatherList* weather_head, locationList* location_head);

#endif //WEATHER_WEATHER_H
