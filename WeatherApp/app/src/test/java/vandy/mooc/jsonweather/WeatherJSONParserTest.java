package vandy.mooc.jsonweather;

import junit.framework.TestCase;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by sandie on 5/31/15.
 */
public class WeatherJSONParserTest extends TestCase {

    private final static String sWeather_Web_Service_URL =
            "http://api.openweathermap.org/data/2.5/weather?q=London,uk";

    public void testParseJsonStream() throws Exception {
        final URL url = new URL(sWeather_Web_Service_URL);
        // Opens a connection to the Acronym Service.
        HttpURLConnection urlConnection =
                (HttpURLConnection) url.openConnection();
        try(InputStream in=
         new BufferedInputStream(urlConnection.getInputStream())){
            final WeatherJSONParser parser = new WeatherJSONParser();
            List<JsonWeather> results = parser.parseJsonStream(in);
            assertTrue(results.size()>0);
        }finally {
            urlConnection.disconnect();
        }

    }

    public void testParseJsonStreamSingle() throws Exception {
        assertFalse("Not yet implemented",true);
    }

    public void testParseJsonWeatherArray() throws Exception {
        assertFalse("Not yet implemented",true);
    }

    public void testParseWeathers() throws Exception {
        assertFalse("Not yet implemented",true);
    }

    public void testParseWeather() throws Exception {
        assertFalse("Not yet implemented",true);
    }

    public void testParseMain() throws Exception {
        assertFalse("Not yet implemented",true);
    }

    public void testParseWind() throws Exception {
        assertFalse("Not yet implemented",true);
    }

    public void testParseSys() throws Exception {
        assertFalse("Not yet implemented",true);
    }
}