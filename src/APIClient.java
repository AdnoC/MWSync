import java.net.URLEncoder;
import java.net.URL;
import java.net.URLConnection;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
public class APIClient {
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

        //ArrayList<channelSearchEnum> lcs = new ArrayList<channelSearchEnum>();

        JsonElement el1 = jArray.get(4);
        System.out.println(java.util.Arrays.toString(el1.getAsJsonObject().entrySet().toArray()));
        //JsonArray ar1 = el1.getAsJsonArray();
        //for(JsonElement obj : ar1 )
        //{
          //System.out.println(obj.toString());
            ////channelSearchEnum cse = gson.fromJson( obj , channelSearchEnum.class);
            ////lcs.add(cse);
        //}
        //Map<String, String> map = new Gson().fromJson(new InputStreamReader(conn.getInputStream(), "UTF-8"), new TypeToken<Map<String, String>>(){}.getType());



        //BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        //while ((line = rd.readLine()) != null) {
            //// Process line...
            //System.out.println(line);
        //}
        //wr.close();
        //rd.close();
    } catch (Exception e) {
      System.err.println("Error");
      e.printStackTrace();
    }
  }
}
