import java.net.URLEncoder;
import java.net.URL;
import java.net.URLConnection;
import java.io.UnsupportedEncodingException;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
public class APIClient {
  public static JsonElement GetMangas() {
    return GetMangas(0);
  }
  public static JsonElement GetMangas(int offset) {
    try {

      String urlStr = "http://mangawatcher.org/pages/manga/mangas/get";
      String data = "";
      urlStr = urlStr + "?" + data;

      // Send data
      URL url = new URL(urlStr);
      URLConnection conn = url.openConnection();
      conn.setDoOutput(true);

      // Get the response
      JsonParser parser = new JsonParser();
      InputStreamReader isr = new InputStreamReader(conn.getInputStream());
      JsonObject jObj = parser.parse(isr).getAsJsonObject();
      JsonArray jArray = jObj.get("items").getAsJsonArray();

      for(JsonElement el : jArray) {
        new MWItem(el);
      }
    } catch (Exception e) {
      System.err.println("Error");
      e.printStackTrace();
    }
    // @TODO: Return a value
    return null;
  }
  public static JsonElement MWRequest() {
    return null;
  }
  public static JsonElement MWRequest(String url, HashMap<String, String> params) {
    url += "?";
    String key1 = "login";
    String val1 = Config.USERNAME;
    String key2 = "pass";
    String val2 = Config.PASSWORD;;

    String data = "";
    // Construct data
    try {
    String data = URLEncoder.encode(key1, "UTF-8") + "=" + URLEncoder.encode(val1, "UTF-8");
    data += "&" + URLEncoder.encode(key2, "UTF-8") + "=" + URLEncoder.encode(val2, "UTF-8");
      } catch(UnsupportedEncodingException uee) {
        uee.printStackTrace();
      }

    for(Map.Entry<String, String> entry : params.entrySet()) {
      try {
        data += "&" + URLEncoder.encode(entry.getKey(), "UTF-8") + "=";
        data += URLEncoder.encode(entry.getValue(), "UTF-8");
      } catch(UnsupportedEncodingException uee) {
        uee.printStackTrace();
      }
    }
    return null;
  }
  public static void main(String[] args) {
    try {

        String urlStr = "http://mangawatcher.org/pages/manga/mangas/get";
        String key1 = "login";
        String val1 = Config.USERNAME;
        String key2 = "pass";
        String val2 = Config.PASSWORD;;
        // Construct data
        String data = URLEncoder.encode(key1, "UTF-8") + "=" + URLEncoder.encode(val1, "UTF-8");
        data += "&" + URLEncoder.encode(key2, "UTF-8") + "=" + URLEncoder.encode(val2, "UTF-8");
        urlStr = urlStr + "?" + data;


        // Send data
        URL url = new URL(urlStr);
        URLConnection conn = url.openConnection();
        conn.setDoOutput(true);
        //OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        //wr.write(data);
        //wr.flush();

        // Get the response
        JsonParser parser = new JsonParser();
        InputStreamReader isr = new InputStreamReader(conn.getInputStream());
        JsonObject jObj = parser.parse(isr).getAsJsonObject();
        JsonArray jArray = jObj.get("items").getAsJsonArray();

        for(JsonElement el : jArray) {
          new MWItem(el);
        }
    } catch (Exception e) {
      System.err.println("Error");
      e.printStackTrace();
    }
  }
}
