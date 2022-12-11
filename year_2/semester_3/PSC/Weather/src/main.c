#include "../lib/inc/weather.h"
#include "main.h"

#include <stdlib.h>
#include <stdio.h>
#include <string.h>

int main(int argc, char* argv[]) {
    int day, month, year;
    char* ptr;

    if (argc != 3) {
        error:
        fprintf(stderr, "Invalid arguments. Please use: weather <date> <locations file>.\nDate format: dd/mm/yyyy\nLocations file format (each line contains): location latitude longitude\n");
        exit(-1);
    }

    char* date_str = argv[1];
    ptr = strtok(date_str, "/");
    if (ptr == NULL) {
        fprintf(stderr, "Invalid day. Valid date format: dd/mm/yyyy\n");
        goto error;
    }
    day = atoi(ptr);

    ptr = strtok(NULL, "/");
    if (ptr == NULL) {
        fprintf(stderr, "Invalid month. Valid date format: dd/mm/yyyy\n");
        goto error;
    }
    month = atoi(ptr);

    ptr = strtok(NULL, "/");
    if (ptr == NULL) {
        fprintf(stderr, "Invalid year. Valid date format: dd/mm/yyyy\n");
        goto error;
    }
    year = atoi(ptr);

    if (!(YEAR_LIMIT(year) && MONTH_LIMIT(month) && DAY_LIMIT(day))) {
        puts("out of limits");
        goto error;
    }

    FILE* file = fopen(argv[2], "r");
    if (file == NULL) {
        puts("file not found");
        goto error;
    }

    Date date = { .year = year , .month = month, .day = day };

    locationList* locations = read_locations(file);

    weatherList* weathers = weather_get_info(&date,locations);

    write_weather(&date, weathers);

    weather_free_info(weathers, locations);
}

locationList* read_locations(FILE* file) {

    locationList* locations = NULL;


    int buffer_size = 256;
    char buffer[buffer_size];
    char* ptr = buffer;
    while (fgets(buffer, buffer_size, file) != NULL) {
        ptr = strtok(buffer, " ");
        char* name = (char*)malloc(sizeof(buffer_size));
        strcpy(name, ptr);
        ptr = strtok(NULL, " ");
        float latitude = strtof (ptr, NULL);
        ptr = strtok(NULL, " ");
        float longitude = strtof(ptr, NULL);
        locations = addLocation(locations, name, latitude, longitude);
    }

    fclose(file);

    return locations;
}

void write_weather(Date* date, weatherList * weathers) {
    FILE* file;
    file = fopen (OUTPUT_FILENAME,"w");

    char* n;
    float t, w, h, c;
    fprintf(file,"%d/%d/%d \n",date->day,date->month,date->year);
    fprintf(file, "Location,Temperature (K),Wind Speed (m/s),Humidity(%%),Cloud Cover (%%)\n");
    for (weatherList *next, *p = weathers; p != NULL; p = next) {
        n = p->location->name;
        t = p->weather->temperature;
        w = p->weather->wind_speed;
        h = p->weather->humidity;
        c = p->weather->cloud_cover;
        fprintf(file,"%s,%g,%g,%g,%g \n",n,t,w,h,c);
        next = p->next;
    }

    printf("File %s written.\n", OUTPUT_FILENAME);

    fclose(file);
}