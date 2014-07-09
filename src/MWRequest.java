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

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
public class MWRequest {

  public ArrayList<MWItem> getMangas(int offset) {
    ArrayList<MWItem> mwItems = new ArrayList<MWItem>();
    try {

      JsonObject jObj =  getMangasJson(offset).getAsJsonObject();
      int count = jObj.get("count").getAsInt();
      if(count > 0) {
        JsonArray jArray = jObj.get("items").getAsJsonArray();

        for(JsonElement el : jArray) {
          mwItems.add(new MWItem(el));
        }
      }
    } catch (Exception e) {
      System.err.println("Error");
      e.printStackTrace();
    }
    return mwItems;
  }
  public static JsonElement getMangasJson() {
    return getMangasJson(0);
  }
  public static JsonElement getMangasJson(int offset) {
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
  private void printAllMangas(){
    if(1==1) {
    return;}
    try {
        int off = 0;
        while(true){
          JsonObject jObj =  getMangasJson(off).getAsJsonObject();
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
}
