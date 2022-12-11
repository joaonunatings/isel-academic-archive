//
// Created by nunes on 12/11/22.
//

#include "../inc/weather.h"
#include "weather_p.h"

#include <stdlib.h>
#include <stdio.h>
#include <time.h>
#include <jansson.h>
#include <curl/curl.h>
#include <string.h>

static MemoryChunk chunk;

weatherList* weather_get_info(Date* date, locationList* locations) {
    weatherList* weatherInfo = NULL;

    if (NULL == locations) {
        fprintf(stderr, "No locations added.\n");
        return weatherInfo;
    }

    for (locationList* p = locations; p != NULL; p = p->next)
        weatherInfo = addWeather(weatherInfo, weather_get(date, p->location), p->location);

    return weatherInfo;
}

weatherList* addWeather(weatherList* weather_head, Weather* weather, Location* location) {
    if (weather_head == NULL) {
        weatherList* weatherNode = (weatherList*) malloc(sizeof(weatherList));
        weatherNode->location = location;
        weatherNode->weather = weather;
        weatherNode->next = NULL;
        return weatherNode;
    } else
        weather_head->next = addWeather(weather_head->next, weather, location);
    return weather_head;
}

locationList* addLocation(locationList* location_head, char* name, float latitude, float longitude) {
    if (location_head== NULL) {
        locationList* locationNode = (locationList*) malloc(sizeof(locationList));
        locationNode->location = (Location*)malloc(sizeof(Location));
        locationNode->location->name = name;
        locationNode->location->latitude = latitude;
        locationNode->location->longitude = longitude;
        locationNode->next = NULL;
        return locationNode;
    } else
        location_head->next = addLocation(location_head->next, name, latitude, longitude);
    return location_head;
}

Weather* weather_get(Date* date, Location* location) {
    char* api_key_key = "WEATHER_API_KEY";
    size_t buffer_size = 64;
    char api_key_value[buffer_size];
    if (!getenv(api_key_key)) {
        fprintf(stderr, "WEATHER_API_KEY is not set. Please check https://openweathermap.org/api.\n");
        exit(1);
    }
    if (snprintf(api_key_value, buffer_size, "%s", getenv(api_key_key)) >= buffer_size) {
        fprintf(stderr, "Could not fit WEATHER_API_KEY into buffer. Maybe the key is invalid?");
        exit(1);
    }

    struct tm t;
    time_t t_of_day;
    t.tm_year = date->year - 1900;
    t.tm_mon = date->month - 1;
    t.tm_mday = date->day;
    t.tm_hour = 12;
    t.tm_min = 0;
    t.tm_sec = 0;
    t.tm_isdst = -1;
    t_of_day = mktime(&t);
    unsigned long requested_time = t_of_day, current_time = time(NULL);
    long time_diff = (long)(requested_time - current_time);
    unsigned long day_offset = (abs(time_diff) / (3600 * 24)) + 1;

    char* url = (char*)malloc(1024 * sizeof(char));
    json_t* root_obj = NULL;
    json_t* requested_day_obj = NULL;

    Weather* weather_result = (Weather*)malloc(sizeof(Weather));
    float temperature = 0;

    // Time requested in the future
    if (time_diff > 0) {
        if (day_offset > MAX_FUTURE_DAYS) {
            fprintf(stderr, "Date %d/%d/%d out of possible API bounds.\n", date->day, date->month, date->year);
            weather_get_free(url, root_obj);
            free(weather_result);
            exit(-1);
        }

        REQUEST_FUTURE_URL(url, location->latitude, location->longitude, api_key_value);
        root_obj = http_get_json_data(url);
        json_t* daily_array = json_object_get(root_obj, "daily");
        requested_day_obj = json_array_get(daily_array, day_offset);
        json_t* temp_obj = json_object_get(requested_day_obj, "temp");
        json_t* temp_day_obj = json_object_get(temp_obj, "day");
        temperature = (float)json_number_value(temp_day_obj);
    }
        // Time requested in the past
    else {
        if (day_offset - 1 > MAX_PAST_DAYS) {
            fprintf(stderr, "Date %d/%d/%d out of possible API bounds.\n", date->day, date->month, date->year);
            weather_get_free(url, root_obj);
            free(weather_result);
            exit(-1);
        }

        REQUEST_PAST_URL(url, location->latitude, location->longitude, requested_time, api_key_value);
        root_obj = http_get_json_data(url);
        requested_day_obj = json_object_get(root_obj, "current");
        json_t* temp_obj = json_object_get(requested_day_obj, "temp");
        temperature = (float)json_number_value(temp_obj);
    }

    weather_result->temperature = temperature;

    weather_insert_values(weather_result, requested_day_obj);

    weather_get_free(url, root_obj);

    return weather_result;
}

json_t* http_get_json_data(const char* url) {
    http_get(url);

    json_error_t error;
    json_t* root = json_loads(chunk.memory, JSON_DECODE_ANY, &error);

    free(chunk.memory);

    return root;
}

void http_get(const char* url) {
    chunk.memory = malloc(1);
    chunk.size = 0;

    curl_global_init(CURL_GLOBAL_DEFAULT);
    CURL* curl = curl_easy_init();
    if (curl == NULL) {
        fprintf(stderr, "curl_init error\n");
        goto cleanup;
    }

    curl_easy_setopt(curl, CURLOPT_URL, url);
    curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, http_get_callback);
    curl_easy_setopt(curl, CURLOPT_WRITEDATA, (void*) &chunk);
    CURLcode res = curl_easy_perform(curl);
    if (res != CURLE_OK) {
        fprintf(stderr, "curl_easy_perform() failed: %s\n", curl_easy_strerror(res));
    }

    cleanup:
    curl_easy_cleanup(curl);
    curl_global_cleanup();
}

size_t http_get_callback(void* contents, size_t size, size_t nmemb, void* userp) {
    size_t realsize = size * nmemb;
    MemoryChunk* mem = (MemoryChunk*)userp;

    char* ptr = realloc(mem->memory, mem->size + realsize + 1);
    if (ptr == NULL) {
        printf("not enough memory (realloc returned NULL)\n");
        return 0;
    }

    mem->memory = ptr;
    memcpy(&(mem->memory[mem->size]), contents, realsize);
    mem->size += realsize;
    mem->memory[mem->size] = 0;

    return realsize;
}

void weather_insert_values (Weather* weather, json_t* weather_info) {
    json_t* wind_speed_obj = json_object_get(weather_info, "wind_speed");
    json_t* humidity_obj = json_object_get(weather_info, "humidity");
    json_t* clouds_obj = json_object_get(weather_info, "clouds");


    const float wind_speed = (float)json_number_value(wind_speed_obj);
    const float humidity = json_integer_value(humidity_obj);
    const float clouds = json_integer_value(clouds_obj);

    weather->wind_speed = wind_speed;
    weather->humidity = humidity;
    weather->cloud_cover = clouds;
}


void weather_get_free (char* url, json_t* root) {
    free(url);
    json_decref(root);
}

void weather_free_info(weatherList* weather_head, locationList* location_head) {
    weatherList_clear(weather_head);
    locationList_clear(location_head);
}

void weatherList_clear(weatherList* weather_head) {
    for (weatherList *next, *p = weather_head; p != NULL; p = next) {
        next = p->next;
        free(p->weather);
        free(p);
    }
}

void locationList_clear(locationList* location_head) {
    for (locationList *next  , *p = location_head; p!=NULL;p=next) {
        next= p->next;
        free(p->location->name);
        free(p->location);
        free(p);
    }
}