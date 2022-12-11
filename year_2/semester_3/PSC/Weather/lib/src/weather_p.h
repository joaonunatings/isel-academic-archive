#ifndef WEATHER_WEATHER_P_H
#define WEATHER_WEATHER_P_H

#include "../inc/weather.h"
#include <jansson.h>

#define FUTURE_URL "https://api.openweathermap.org/data/2.5/onecall?lat=%f&lon=%f&exclude=hourly,minutely&appid=%s"
#define REQUEST_FUTURE_URL(str, latitude, longitude, api_key) sprintf(str, FUTURE_URL, latitude, longitude, api_key)

#define PAST_URL "https://api.openweathermap.org/data/2.5/onecall/timemachine?lat=%f&lon=%f&dt=%lu&appid=%s"
#define REQUEST_PAST_URL(str, latitude, longitude, date, api_key) sprintf(str, PAST_URL, latitude, longitude, date, api_key)

#define MAX_FUTURE_DAYS 7
#define MAX_PAST_DAYS 5

typedef struct MemoryStruct {
    char *memory;
    size_t size;
} MemoryChunk;

Weather* weather_get(Date* date, Location* location);

void weather_get_free (char* url, json_t* root);

json_t* http_get_json_data(const char* url);

void http_get(const char* url);

size_t http_get_callback(void* contents, size_t size, size_t nmemb, void* userp);

void weather_insert_values (Weather* weather, json_t* weather_info);

void weatherList_clear(weatherList* weather_head);

void locationList_clear(locationList* location_head);

#endif //WEATHER_WEATHER_P_H
