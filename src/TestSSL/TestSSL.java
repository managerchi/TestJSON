package TestSSL;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TestSSL {

    public static void main(String[] args) throws Exception {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        } };
        // Install the all-trusting trust manager
        final SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

        
        final String ADDRESS_START = "<TD class=\"green-W9\">";
        final String ADDRESS_END = "</TD>";
        String address;

        BufferedReader br = null;
        
		try {
 
			String line;
 
			br = new BufferedReader(new FileReader("E:\\Users\\Chi\\Documents\\Parking\\台灣聯通停車場開發股份有限公司.htm"));
 
			while ((line = br.readLine()) != null) {
				if (line.contains(ADDRESS_START)) {
					System.out.println(line);
					System.out.println(ADDRESS_START.length());
					
					if (line.contains(ADDRESS_END)) {
						address = line.substring(line.indexOf(ADDRESS_START)+ADDRESS_START.length(), line.indexOf(ADDRESS_END));
					}
					else {
						address = line.substring(line.indexOf(ADDRESS_START)+ADDRESS_START.length());
					}
					System.out.println("address("+address+")");

				}
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

        
        
        //URL url = new URL("https://www.google.com");
        //URL url = new URL("https://maps.googleapis.com/maps/api/geocode/xml?address=%E9%81%94%E8%A7%80%E8%B7%AF+41");
        URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?address=%E9%81%94%E8%A7%80%E8%B7%AF+41");
        //URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=London,uk");
        //URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=10280&mode=json&units=metric&cnt=14");
        
        URLConnection con = url.openConnection();
        final Reader reader = new InputStreamReader(con.getInputStream());
        br = new BufferedReader(reader);        
        String line = "";
        StringBuffer buffer = new StringBuffer();

        while ((line = br.readLine()) != null) {
            //System.out.println(line);
            buffer.append(line);
            //buffer.append(line + "\n");
        }        
        br.close();

        String jsonStr = null;

        jsonStr = buffer.toString();
        //System.out.println(jsonStr);

        getGeocodeFromJson(jsonStr);
        //getWeatherDataFromJson(jsonStr);


    } // End of main 

    
    
    static private void getGeocodeFromJson(String jsonStr)
            throws JSONException {

        final String RESULTS = "results";
        final String ADDRESS_COMPONENTS = "address_components";
        final String LONG_NAME = "long_name";
        final String SHORT_NAME = "short_name";
        final String FORMATTED_ADDRESS = "formatted_address";

        // Location coordinate
        final String GEOMETRY = "geometry";
        final String LOCATION = "location";
        final String LATITUDE = "lat";
        final String LONGITUDE = "lng";
        final String LOCATION_TYPE = "location_type";
        final String VIEWPOINT = "viewpoint";
        final String NORTHEAST = "northeast";
        final String NORTHEAST_LATITUDE = "lat";
        final String NORTHEAST_LONGITUDE = "lng";
        final String SOUTHWEST = "southwest";
        final String SOUTHWEST_LATITUDE = "lat";
        final String SOUTHWEST_LONGITUDE = "lng";

        final String PLACE_ID = "place_id";
        final String TYPES = "types";
        String type;

        final String STATUS = "status";
        
        try {
            //System.out.println(jsonStr);

            JSONObject geocodeJson = new JSONObject(jsonStr);
            JSONArray resultsArray = geocodeJson.getJSONArray(RESULTS);
            String status = geocodeJson.getString(STATUS);
            System.out.println(status);

            if (status.equals("OK")) {
            	for(int i = 0; i < resultsArray.length(); i++) {
                    JSONObject resultJson = resultsArray.getJSONObject(i);

                    JSONArray addressArray = resultJson.getJSONArray(ADDRESS_COMPONENTS);

                    JSONObject geometryJson = resultJson.getJSONObject(GEOMETRY);
                    JSONObject locationCoord = geometryJson.getJSONObject(LOCATION);
                    double locationLatitude = locationCoord.getDouble(LATITUDE);
                    double locationLongitude = locationCoord.getDouble(LONGITUDE);
                    
                    System.out.println("("+locationLatitude+","+locationLongitude+")");

                    
                    String placeID = resultJson.getString(PLACE_ID);
                    System.out.println(placeID);

                    JSONArray typesArray = resultJson.getJSONArray(TYPES);
                    
                    for(int j = 0; j < typesArray.length(); j++) {
                        type = typesArray.getString(j);
                    }
                    
                    for(int j = 0; j < addressArray.length(); j++) {
                        String longName;
                        String shortName;
                        
                        // Get the JSON object representing the day
                        JSONObject addrssComponentsJson = addressArray.getJSONObject(j);

                        // Cheating to convert this to UTC time, which is what we want anyhow
                        //dateTime = dayTime.setJulianDay(julianStartDay+i);

                        longName = addrssComponentsJson.getString(LONG_NAME);
                        shortName = addrssComponentsJson.getString(SHORT_NAME);

                    	System.out.print("("+longName+","+shortName+")");

                        typesArray = addrssComponentsJson.getJSONArray(TYPES);
                        for(int k = 0; k < typesArray.length(); k++) {
                        	type = typesArray.getString(k);
                        	
                        	System.out.println(type);
                        } // k              
                    } // j
                } // i
            }
            
        } catch (JSONException e) {
            System.out.println(e.getMessage());

        }
    }

    
    
    static private void getWeatherDataFromJson(String forecastJsonStr)
            throws JSONException {

        // Now we have a String representing the complete forecast in JSON Format.
        // Fortunately parsing is easy:  constructor takes the JSON string and converts it
        // into an Object hierarchy for us.

        // These are the names of the JSON objects that need to be extracted.

        // Location information
        final String CITY = "city";
        final String CITY_NAME = "name";
        final String COORD = "coord";

        // Location coordinate
        final String LATITUDE = "lat";
        final String LONGITUDE = "lon";

        // Weather information.  Each day's forecast info is an element of the "list" array.
        final String LIST = "list";

        final String PRESSURE = "pressure";
        final String HUMIDITY = "humidity";
        final String WINDSPEED = "speed";
        final String WIND_DIRECTION = "deg";

        // All temperatures are children of the "temp" object.
        final String DT = "dt";
        final String TEMPERATURE = "temp";
        final String MAX = "max";
        final String MIN = "min";

        final String WEATHER = "weather";
        final String DESCRIPTION = "main";
        final String WEATHER_ID = "id";

        try {
            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(LIST);

            JSONObject cityJson = forecastJson.getJSONObject(CITY);
            String cityName = cityJson.getString(CITY_NAME);

            JSONObject cityCoord = cityJson.getJSONObject(COORD);
            double cityLatitude = cityCoord.getDouble(LATITUDE);
            double cityLongitude = cityCoord.getDouble(LONGITUDE);
            
            System.out.println("("+cityLatitude+","+cityLongitude+")");


            //long locationId = addLocation(locationSetting, cityName, cityLatitude, cityLongitude);

            // Insert the new weather information into the database
            //Vector<ContentValues> cVVector = new Vector<ContentValues>(weatherArray.length());

            // OWM returns daily forecasts based upon the local time of the city that is being
            // asked for, which means that we need to know the GMT offset to translate this data
            // properly.

            // Since this data is also sent in-order and the first day is always the
            // current day, we're going to take advantage of that to get a nice
            // normalized UTC date for all of our weather.

            //Time dayTime = new Time();
            //dayTime.setToNow();

            // we start at the day returned by local time. Otherwise this is a mess.
            //int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

            // now we work exclusively in UTC
            //dayTime = new Time();

            for(int i = 0; i < weatherArray.length(); i++) {
                // These are the values that will be collected.
                long dateTime;
                double pressure;
                int humidity;
                double windSpeed;
                double windDirection;

                double high;
                double low;

                String description;
                int weatherId;

                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                // Cheating to convert this to UTC time, which is what we want anyhow
                //dateTime = dayTime.setJulianDay(julianStartDay+i);

                int dt = dayForecast.getInt(DT);
                System.out.println(dt);
                
                pressure = dayForecast.getDouble(PRESSURE);
                humidity = dayForecast.getInt(HUMIDITY);
                windSpeed = dayForecast.getDouble(WINDSPEED);
                windDirection = dayForecast.getDouble(WIND_DIRECTION);

                // Description is in a child array called "weather", which is 1 element long.
                // That element also contains a weather code.
                JSONObject weatherObject =
                        dayForecast.getJSONArray(WEATHER).getJSONObject(0);
                description = weatherObject.getString(DESCRIPTION);
                weatherId = weatherObject.getInt(WEATHER_ID);

                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.
                JSONObject temperatureObject = dayForecast.getJSONObject(TEMPERATURE);
                high = temperatureObject.getDouble(MAX);
                low = temperatureObject.getDouble(MIN);

                
            }

            
        } catch (JSONException e) {
            System.out.println(e.getMessage());

        }
    }

} // End of the class //