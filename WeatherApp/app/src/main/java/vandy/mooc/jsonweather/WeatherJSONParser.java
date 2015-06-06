package vandy.mooc.jsonweather;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

/**
 * Parses the Json weather data returned from the Weather Services API
 * and returns a List of JsonWeather objects that contain this data.
 */
public class WeatherJSONParser {
    /**
     * Used for logging purposes.
     */
    private final String TAG =
        this.getClass().getCanonicalName();

    /**
     * Parse the @a inputStream and convert it into a List of JsonWeather
     * objects.
     */
    public List<JsonWeather> parseJsonStream(InputStream inputStream)
        throws IOException {
        // TODO -- you fill in here.
        JsonReader reader = new JsonReader(new InputStreamReader(inputStream,
                "UTF-8"));
        try{

            //Log.d(TAG, "Parsing weather results returned as an array");
            JsonWeather weather = parseJsonStreamSingle(reader);
            List<JsonWeather> results = new ArrayList<>();
            if (weather!=null  && weather.getName()!=null){
                results.add(weather);
            }
            return results;
        }finally {
            reader.close();
        }
    }

    /**
     * Parse a single Json stream and convert it into a JsonWeather
     * object.
     */
    public JsonWeather parseJsonStreamSingle(JsonReader reader)
        throws IOException {
        // TODO -- you fill in here.
        JsonWeather weather = new JsonWeather();
        reader.beginObject();
        try{
            while(reader.hasNext()){
                String name =reader.nextName();
                System.out.println(name);
                Log.d(TAG,name);
                switch (name){
                    case JsonWeather.main_JSON:
                        weather.setMain(parseMain(reader));
                        Log.d(TAG, "Found MAIN part of weather");
                        break;
                    case JsonWeather.sys_JSON:
                        weather.setSys(parseSys(reader));
                        Log.d(TAG, "Found SYS part of weather");
                        break;
                    case JsonWeather.wind_JSON:
                        weather.setWind(parseWind(reader));
                        Log.d(TAG, "Found WIND part of weather");
                        break;
                    case JsonWeather.weather_JSON:
                        weather.setWeather(parseWeathers(reader));
                        Log.d(TAG, "Found WEATHERS part of weather");
                        break;
                    case JsonWeather.base_JSON:
                        weather.setBase(reader.nextString());
                        Log.d(TAG, "Found base string in weather " + weather.getBase());
                        break;
                    case JsonWeather.cod_JSON:
                        weather.setCod(reader.nextLong());
                        Log.d(TAG, "Found cod long in weather " + weather.getCod());
                        break;
                    case JsonWeather.id_JSON:
                        weather.setId(reader.nextLong());
                        Log.d(TAG, "Found weather id " + weather.getId());
                        break;
                    case JsonWeather.name_JSON:
                        weather.setName(reader.nextString());
                        Log.d(TAG, "Found weather name " + weather.getName());
                        break;
                    case JsonWeather.dt_JSON:
                        weather.setDt(reader.nextLong());
                        Log.d(TAG, "Found weather dt " + weather.getDt());
                        break;
                    default:
                        reader.skipValue();
                        break;

                }
            }
        }finally {
            reader.endObject();
        }
        return weather;
    }

    /**
     * Parse a Json stream and convert it into a List of JsonWeather
     * objects.
     */
    public List<JsonWeather> parseJsonWeatherArray(JsonReader reader)
        throws IOException {
        List<JsonWeather> result = new ArrayList<>();
        // TODO -- you fill in here.
        reader.beginArray();
        try {
            // If the acronym wasn't expanded return null;
            if (reader.peek() == JsonToken.END_ARRAY)
                return null;


            while(reader.hasNext()){
                result.add(parseJsonStreamSingle(reader));
            }
        } finally {
            reader.endArray();
        }
        return  result;
    }

    /**
     * Parse a Json stream and return a JsonWeather object.
     *
    public JsonWeather parseJsonWeather(JsonReader reader) 
        throws IOException {
        // TODO -- you fill in here.
        reader.beginObject();
        try{

        }finally {
            reader.endObject();
        }
    }
    
    /**
     * Parse a Json stream and return a List of Weather objects.
     */
    public List<Weather> parseWeathers(JsonReader reader) throws IOException {
        // TODO -- you fill in here.
        reader.beginArray();
        List<Weather> weatherList = new ArrayList<>();
        try{
            while (reader.hasNext()){
                weatherList.add(parseWeather(reader));
            }
        }finally {
            reader.endArray();
        }
        return weatherList;
    }

    /**
     * Parse a Json stream and return a Weather object.
     */
    public Weather parseWeather(JsonReader reader) throws IOException {
        // TODO -- you fill in here.
        reader.beginObject();
        Weather weather = new Weather();
        try{
            while (reader.hasNext()){
                String name = reader.nextName();
                switch (name){
                    case Weather.description_JSON:
                        weather.setDescription(reader.nextString());
                        Log.d(TAG, "reading description " + weather.getDescription());
                        break;
                    case Weather.icon_JSON:
                        weather.setIcon(reader.nextString());
                        Log.d(TAG, "reading icon " + weather.getIcon());
                        break;
                    case Weather.id_JSON:
                        weather.setId(reader.nextLong());
                        Log.d(TAG, "reading id " + weather.getId());
                        break;
                    case Weather.main_JSON:
                        weather.setMain(reader.nextString());
                        Log.d(TAG, "reading main from weather " + weather.getMain().toString());
                        break;
                    default:
                        reader.skipValue();
                        break;
                }
            }
        }finally{
            reader.endObject();
        }
        return weather;
    }
    
    /**
     * Parse a Json stream and return a Main Object.
     */
    public Main parseMain(JsonReader reader) 
        throws IOException {
        // TODO -- you fill in here.
        reader.beginObject();
        Main main = new Main();
        try{
            while (reader.hasNext()){
                String name = reader.nextName();
                switch (name){
                    case Main.grndLevel_JSON:
                        main.setGrndLevel(reader.nextDouble());
                        Log.d(TAG, "reading ground level " + main.getGrndLevel());
                        break;
                    case Main.humidity_JSON:
                        main.setHumidity(reader.nextLong());
                        Log.d(TAG,"reading humidity " + main.getHumidity());
                        break;
                    case Main.pressure_JSON:
                        main.setPressure(reader.nextDouble());
                        Log.d(TAG, "reading pressure " + main.getPressure());
                        break;
                    case Main.seaLevel_JSON:
                        main.setSeaLevel(reader.nextDouble());
                        Log.d(TAG,"reading sea level " + main.getSeaLevel()) ;
                        break;
                    case Main.temp_JSON:
                        main.setTemp(reader.nextDouble());
                        Log.d(TAG, "reading temp " + main.getTemp());
                        break;
                    case Main.tempMax_JSON:
                        main.setTempMax(reader.nextDouble());
                        Log.d(TAG, "reading max temp " + main.getTempMax());
                        break;
                    case Main.tempMin_JSON:
                        main.setTempMin(reader.nextDouble());
                        Log.d(TAG, "reading min temp " +main.getTempMin());
                        break;
                    default:
                        reader.skipValue();
                        break;
                }
            }
        }finally{
            reader.endObject();
        }
        return main;
    }

    /**
     * Parse a Json stream and return a Wind Object.
     */
    public Wind parseWind(JsonReader reader) throws IOException {
        // TODO -- you fill in here.
        reader.beginObject();
        Wind wind = new Wind();
        try{
            while (reader.hasNext()){
                String name = reader.nextName();
                switch (name){
                    case Wind.deg_JSON:
                        wind.setDeg(reader.nextDouble());
                        Log.d(TAG,"reading wind degrees " + wind.getDeg());
                        break;
                    case Wind.speed_JSON:
                        wind.setSpeed(reader.nextDouble());
                        Log.d(TAG,"reading wind speed " + wind.getSpeed());
                        break;
                    default:
                        reader.skipValue();
                        break;

                }
            }
        }finally{
            reader.endObject();
        }
        return wind;
    }

    /**
     * Parse a Json stream and return a Sys Object.
     */
    public Sys parseSys(JsonReader reader) throws IOException{
        // TODO -- you fill in here.
        reader.beginObject();

        Sys sys = new Sys();
        try{
            while (reader.hasNext()){
                String name = reader.nextName();
                switch (name){
                    case Sys.country_JSON:
                        sys.setCountry(reader.nextString());
                        Log.d(TAG, "reading country " + sys.getCountry());
                        break;
                    case Sys.message_JSON:
                        sys.setMessage(reader.nextDouble());
                        Log.d(TAG,"reading message " + sys.getMessage());
                        break;
                    case Sys.sunrise_JSON:
                        sys.setSunrise(reader.nextLong());
                        Log.d(TAG, "reading sunrise " + sys.getSunrise());
                        break;
                    case Sys.sunset_JSON:
                        sys.setSunset(reader.nextLong());
                        Log.d(TAG,"reading sunset " + sys.getSunset());
                        break;
                    default:
                        reader.skipValue();
                        break;
                }
            }
        }finally{
            reader.endObject();
        }
        return sys;
    }
}
