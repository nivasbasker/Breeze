# Breeze Weather App

Breeze is a native Android weather application that provides real-time weather updates and
forecasts. The App follows the MVVM (Model-View-ViewModel) architectural pattern,
which helps in separating concerns and improving code maintainability.This app allows users to
search for weather conditions using city names or postal codes.
It offers a user-friendly interface with preference management.

you can locate the latest apk [here](https://github.com/nivasbasker/Breeze/tree/master/outputs)

## Features

1. Real-Time Weather Updates: Get current weather data including temperature, wind speed, sea level,
   pressure, and humidity.
2. Weather Forecast: View hourly weather forecasts for the next 10 hours.
3. City Search: Search for weather updates using city names or postal codes.
4. Temperature Units: Switch between Celsius and Kelvin temperature units.
5. Caching: Automatically caches the last searched weather data and city for quick access.
6. Dynamic Backgrounds: Changes the background based on the current weather condition (e.g., Clear,
   Clouds, Rain).

## Main Components

1. Activities/View:

ActivityMainBinding: Automatically generated binding class for the main activity.
ForecastAdapter: RecyclerView adapter for displaying hourly forecast items.
MainActivity: The main activity that handles user interactions and displays weather data.

2. ViewModel:

BreezeViewModel: Manages UI-related data and handles background operations.

3. Model:

WeatherRepo: Handles data fetching from the API.
WeatherCaller: Manages API requests using Volley.
City, WeatherData, ForecastItem: Data classes representing the structure of the data.

4. Utility:
   CacheManager: Manages caching of weather data and city information.

## API Integration

The app uses the OpenWeatherMap API to fetch weather data and forecasts.
API calls are managed using the Volley library.

## Search Functionality

Users can search for weather updates by entering a city name or postal code.
The app validates the input and fetches latitude and longitude using the OpenWeatherMap Geocoding
API.

## Error Handling

Proper error handling mechanisms are in place to manage network errors, invalid user input, network
availability and other potential issues.
Informative error messages are displayed to the user when necessary.

## Caching

The app caches the last searched weather data and city using SharedPreferences.
Cached data is displayed upon app startup for quick access.

## Additional Features

Multiple Days Forecast: Displaying weather forecasts for multiple days.
User Preferences: Allowing users to customize the application (e.g., temperature units, update
frequency).