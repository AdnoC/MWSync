import java.net.HttpURLConnection;
import java.io.IOException;
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

  protected int manga_offset = 0;
  protected static String authString = "";
  protected static boolean[] auth = new boolean[2];
  public static void resetAuth() {
    MWRequest.auth = new boolean[2];
  }
  public static void setAuth(String user, String pass) {
    resetAuth();
    pass = MD5Gen.encryptPass(user, pass);
    String data = "";
    try {
      data = URLEncoder.encode("login", "UTF-8") + "=" + URLEncoder.encode(user, "UTF-8");
      data += "&" + URLEncoder.encode("pass", "UTF-8") + "=" + URLEncoder.encode(pass, "UTF-8");
    } catch(UnsupportedEncodingException uee) {
      uee.printStackTrace();
    }
    MWRequest.authString = data;
  }

  public static boolean isAuthorized() {
    if(! MWRequest.auth[0]) {
      int response = 401;
      try {
        String urlStr = "http://mangawatcher.org/pages/manga/auth/signin?";
        urlStr += MWRequest.authString;


        // Send data
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        response = conn.getResponseCode();

        MWRequest.auth[0] = true;
        MWRequest.auth[1] = response == 200;
        conn.disconnect();
      } catch (Exception e) {
        System.err.println("Error");
        e.printStackTrace();
      }
    }
    return MWRequest.auth[1];
  }
  public ArrayList<MWItem> getMangas() {
    ArrayList<MWItem> list = getMangas(manga_offset);
    manga_offset += 20;
    return list;
  }
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
      String data = MWRequest.authString;
      urlStr += "?";

      // Construct data
      try {
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
      try(
          InputStreamReader isr = new InputStreamReader(conn.getInputStream());
         ) {
        ret = parser.parse(isr);
      } catch (IOException ioe) {
        ioe.printStackTrace();
      }
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

    String data = MWRequest.authString;
    // Construct data

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
