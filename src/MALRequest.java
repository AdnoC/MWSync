import java.net.HttpURLConnection;
import org.xml.sax.InputSource;
import java.nio.charset.Charset;
import java.io.ByteArrayInputStream;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.URL;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;

import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
public class MALRequest {
  /**
   * NOTE: Mangas need to be added to the list before they can be updated
   *
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

  protected String requestURL;
  protected Map<String, String> params;
  protected RequestType type;
  protected Document document;
  protected static boolean[] auth = new boolean[2];
  protected static String basicAuth = "";

  public enum RequestType {
    LOGIN, ADD, UPDATE, SEARCH;

    protected String[] requiredParams() {
      switch(this) {
        case LOGIN:
          return new String[0];
        case ADD:
          return new String[]{ "id", "data" };
        case UPDATE:
          return new String[]{ "id", "data" };
        case SEARCH:
          return new String[]{ "q" };
        default:
          return new String[0];
      }

    }
    protected String getURL() {
      switch(this) {
        case LOGIN:
          return  "http://myanimelist.net/api/account/verify_credentials.xml";
        case ADD:
          return "http://myanimelist.net/api/mangalist/add/id.xml";
        case UPDATE:
          return "http://myanimelist.net/api/mangalist/update/id.xml";
        case SEARCH:
          return "http://myanimelist.net/api/manga/search.xml";
        default:
          return "";
      }
    }
  }

  public MALRequest() {
    this(RequestType.LOGIN);
  }
  public MALRequest(RequestType rType) {
    setType(rType);
  }

  public String addParam(String key, String value) {
    if(params == null) {
      params = new HashMap<String, String>();
    }
    return params.put(key, value);
  }
  public String removeParam(String key) {
    if(params == null) {
      params = new HashMap<String, String>();
      return null;
    }
    return params.remove(key);
  }

  protected String getRequestURL() {
    String ret = requestURL;
    if(requestURL.indexOf("id") != -1 && params != null && params.containsKey("id")) {
      ret = ret.replaceAll("id", params.get("id"));
    }
    if(params != null && !params.isEmpty()) {
      ret += "?" +  Utils.buildParamsFromMap(params);
    }
    return ret;
  }
  public void changeType(RequestType rType) {
    clear();
    setType(rType);
  }
  protected void setType(RequestType rType) {
    this.type = rType;
    this.requestURL = rType.getURL();
  }
  /**
   * Clears all data for this request.
   */
  protected void clear() {
    document = null;
    params = null;
  }

  public Document getDocument() {
    if(document == null) {
      return requestDocument();
    } else {
      return document;
    }
  }
  public Document requestDocument(){
    try{
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        DocumentBuilder dbuilder = factory.newDocumentBuilder();
        String req = request();
      try {
        if(req == null || req == "") {
          return null;
        }
        // Fix simple xml entity errors
        //req = req.replaceAll("&rsquo", "&amp;rsquo");
//req = new String(Charset.forName("UTF-8").encode(req).array(), "UTF-8");
        InputStream is = new ByteArrayInputStream(req.getBytes());
        //InputSource is = new InputSource(new ByteArrayInputStream(req.getBytes()));
        //is.setEncoding("UTF-8");
        Document dom = dbuilder.parse(is);
        this.document = dom;
      } catch(SAXException saxe) {
        // If we had a sax exception, replace all html entities and try parsing
        // again. This will solve 99% of the problems.
        // @TODO: Add handles for all html entities
        req = req.replaceAll("&rsquo", "&amp;rsquo");
        req = req.replaceAll("&ldquo", "&amp;ldquo");
        req = req.replaceAll("&rdquo", "&amp;rdquo");
        try{
          InputStream is = new ByteArrayInputStream(req.getBytes());
          try{
            Document dom = dbuilder.parse(is);
            this.document = dom;
          } catch(SAXException saxe2) {
            saxe2.printStackTrace();
          }
        } catch(IOException ioe) {
          ioe.printStackTrace();
        }
      } catch(IOException ioe) {
        ioe.printStackTrace();
      }
    } catch(ParserConfigurationException pce) {
      pce.printStackTrace();
    }
    return this.document;
  }
  public String request() throws BadRequestParamsException {
    if(! canRequest()) {
      ArrayList<String> req = new ArrayList<String>(Arrays.asList(type.requiredParams()));
      req.removeAll(params.keySet());
      throw new BadRequestParamsException(req.toArray(new String[req.size()]));
    }
    //String urlStr = requestURL;
    //if(params != null && !params.isEmpty()) {
      //String data = Utils.buildParamsFromMap(params);
      //urlStr += "?" + data;
    //}
    String urlStr = getRequestURL();
    System.out.println("URL: " + urlStr);

    // Send data
    try{
      URL url = new URL(urlStr);
      HttpURLConnection conn = null;
      try {
        conn = (HttpURLConnection) url.openConnection();
        addAuth(conn);
        conn.setDoOutput(true);
        int rCode = conn.getResponseCode();
        System.out.println("CODE: " + rCode);


        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        StringBuilder builder = new StringBuilder();
        String aux = "";
          while ((aux = reader.readLine()) != null) {
            builder.append(aux);
        }

        return builder.toString();


      } catch(IOException ioe) {
        ioe.printStackTrace();
      }
    } catch(MalformedURLException mue) {
      mue.printStackTrace();
    }
    //@TODO: Make this return a value
    return null;
  }
  public boolean canRequest() {
    for(String param : this.type.requiredParams()) {
      if(! params.containsKey(param)) {
        return false;
      }
    }
    return true;
  }

  protected static void addAuth(URLConnection uc) {
    uc.setRequestProperty("Authorization", basicAuth);
    // Until MAL whitelists me, need to use chrome's user-agent for testing.
    //uc.setRequestProperty("http.agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36");
    uc.setRequestProperty("http.agent", "MWSync");
  }

  public static void setAuth(String user, String pass) {
    resetAuth();
    String userpass = user + ":" + pass;
    MALRequest.basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());
  }

  public static void resetAuth() {
    MALRequest.auth = new boolean[2];
  }

  public static boolean isAuthorized() {
    // Set a cache of whether we are authorized so we don't have to make multiple
    // requests for authorization.
    if(! MALRequest.auth[0]) {
      int response = 401;
      try{
        String urlStr = RequestType.LOGIN.getURL();
        URL url = new URL(urlStr);
        try {
          HttpURLConnection conn = (HttpURLConnection) url.openConnection();
          addAuth(conn);
          conn.setDoOutput(true);

          response = conn.getResponseCode();

          conn.disconnect();
        } catch(IOException ioe) {
          ioe.printStackTrace();
        }
      } catch(MalformedURLException mue) {
        mue.printStackTrace();
      }

      MALRequest.auth[0] = true;
      MALRequest.auth[1] = response == 200;
    }
    return MALRequest.auth[1];
  }
}
