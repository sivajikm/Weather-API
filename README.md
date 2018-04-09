# Weather-API
Weather API Sample
·         Consume US weather data from https://openweathermap.org/.

·         Provide an HTTP GET /wind/{zipCode} method that takes a zipcode as a required path parameter and returns a wind resource.

·         Validates input data.

·         Response format should be JSON.

·         Cache the resource for 15 minutes to avoid expensive calls to the OpenWeatherMap API.

·         Provide a CLI command that will bust the cache if needed.

·         Ensure that the cache is thread safe.

·         Response fields should include:

o    Wind Speed

o    Wind Direction
