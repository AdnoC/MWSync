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
public class MWAPI {
  public static void main(String[] args) {
    testUpdate();
  }
  private static void testUpdate() {
    MALRequest malr = new MALRequest(MALRequest.RequestType.UPDATE);
    malr.addParam("id", "8456");
    MALData mald = new MALData();
    mald.put("status", "1");
    mald.put("chapter", "1");
    malr.addParam("data", mald.toString());
    System.out.println(malr.requestString());

  }
  private void oldMain(){
    if(1==1) {
    return;}
    try {

      /*
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
        JsonObject jObj = parser.parse(isr).getAsJsonObject();*/
        int off = 0;
        while(true){
          JsonObject jObj =  GetMangas(off).getAsJsonObject();
          int count = jObj.get("count").getAsInt();
          if(count <= 0) {
            break;
          }
          JsonArray jArray = jObj.get("items").getAsJsonArray();

          for(JsonElement el : jArray) {
            System.out.println((new MWItem(el)).toString());
          }
          off += 20;
        }
    } catch (Exception e) {
      System.err.println("Error");
      e.printStackTrace();
    }
  }

  public static JsonElement GetMangas() {
    return GetMangas(0);
  }
  public static JsonElement GetMangas(int offset) {
    JsonElement ret = null;
    try {

      String urlStr = "http://mangawatcher.org/pages/manga/mangas/get";
      String data = "";
      urlStr += "?";
      String key1 = "login";
      String val1 = Config.MWUSERNAME;
      String key2 = "pass";
      String val2 = Config.MWPASSWORD;;

      // Construct data
      try {
        data = URLEncoder.encode(key1, "UTF-8") + "=" + URLEncoder.encode(val1, "UTF-8");
        data += "&" + URLEncoder.encode(key2, "UTF-8") + "=" + URLEncoder.encode(val2, "UTF-8");
        data += "&" + URLEncoder.encode("offset", "UTF-8") + "=" + offset;
      } catch(UnsupportedEncodingException uee) {
        uee.printStackTrace();
      }
      urlStr += data;

      // Send data
      URL url = new URL(urlStr);
      URLConnection conn = url.openConnection();
      conn.setDoOutput(true);

      // Get the response
      JsonParser parser = new JsonParser();
      InputStreamReader isr = new InputStreamReader(conn.getInputStream());
      ret = parser.parse(isr);
    } catch (Exception e) {
      System.err.println("Error");
      e.printStackTrace();
    }
    return ret;
  }
  public static JsonElement MWRequest() {
    return null;
  }
  public static JsonElement MWRequest(String url, HashMap<String, String> params) {
    url += "?";
    String key1 = "login";
    String val1 = Config.MWUSERNAME;
    String key2 = "pass";
    String val2 = Config.MWPASSWORD;;

    String data = "";
    // Construct data
    try {
    data = URLEncoder.encode(key1, "UTF-8") + "=" + URLEncoder.encode(val1, "UTF-8");
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
}
