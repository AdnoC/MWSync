import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.URL;
import java.io.UnsupportedEncodingException;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
public class MALRequest {

  /**
   * Requests that will be used:
   *  http://myanimelist.net/api/manga/search.xml
   *   Search Manga
   * http://myanimelist.net/api/mangalist/add/id.xml
   *   Add manga
   * http://myanimelist.net/api/animelist/update/id.xml
   *   Update manga
   * http://myanimelist.net/api/account/verify_credentials.xml
   *   Verify account credentials
   */
  //public Enum REQUEST_TYPE {
  //}
  protected void addAuth(URLConnection uc) {
    String userpass = Config.MAL_USERNAME + ":" + Config.MAL_PASSWORD;
    String encoding = new sun.misc.BASE64Encoder().encode(userpass.getBytes());
    uc.setRequestProperty("Authorization", "Basic " + encoding);
    // Until MAL whitelists me, need to use chrome's user-agent for testing.
    uc.setRequestProperty("http.agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36");
    //uc.setRequestProperty("http.agent", "MWSync");
  }
  public MALRequest() {
      //String urlStr = "http://myanimelist.net/api/manga/search.xml?q=full+metal";
      String urlStr = "http://myanimelist.net/api/account/verify_credentials.xml";
      String data = "";

      // Construct data
      //try {
        //data = URLEncoder.encode(key1, "UTF-8") + "=" + URLEncoder.encode(val1, "UTF-8");
        //data += "&" + URLEncoder.encode(key2, "UTF-8") + "=" + URLEncoder.encode(val2, "UTF-8");
        //data += "&" + URLEncoder.encode("offset", "UTF-8") + "=" + offset;
      //} catch(UnsupportedEncodingException uee) {
        //uee.printStackTrace();
      //}

      // Send data
      try{
      URL url = new URL(urlStr);
      URLConnection conn = url.openConnection();
      addAuth(conn);
      conn.setDoOutput(true);


      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document dom = builder.parse(conn.getInputStream());

      InputStreamReader isr = new InputStreamReader(conn.getInputStream());
      //Get the response
      BufferedReader br = new BufferedReader(isr);
      String str = br.readLine();
      while(str != null && !str.equals("")) {
        System.out.println(str);
      str = br.readLine();
      }
      }catch(Exception e){e.printStackTrace();}

  }
}
