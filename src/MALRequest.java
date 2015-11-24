import java.net.HttpURLConnection;
import java.util.zip.GZIPInputStream;
import java.util.regex.Pattern;
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
import org.xml.sax.SAXParseException;
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

  // Matches any ampersand that is not followed by an XML entity.
  private static final Pattern HTML_ENTITIES = Pattern.compile("\\&(?!quot|amp|lt|gt)");

  public enum RequestType {
    LOGIN, ADD, UPDATE, SEARCH, GET_LIST;

    protected String getRequestType() {
      switch(this) {
        case LOGIN:
          return "GET";
        case ADD:
          return "POST";
        case UPDATE:
          return "POST";
        case SEARCH:
          return "GET";
        case GET_LIST:
          return "GET";
        default:
          return "GET";
      }
    }
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
        case GET_LIST:
          String str = "http://myanimelist.net/malappinfo.php?status=all&type=manga&u=";
          str += Settings.SETTINGS.getMALName();
          return str;
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
        if(req == null || req.length() == 0 || req == "") {
          return null;
        }
        req = escapeHTMLEntities(req);
        try (
             InputStream is = new ByteArrayInputStream(req.getBytes());
            ) {
          Document dom = dbuilder.parse(is);
          this.document = dom;
        } catch(SAXException saxe) {
          saxe.printStackTrace();
          System.out.println(req);
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
      //DEBUG
      System.out.println("Bad params");
      throw new BadRequestParamsException(req.toArray(new String[req.size()]));
    }
    //String urlStr = requestURL;
    //if(params != null && !params.isEmpty()) {
      //String data = Utils.buildParamsFromMap(params);
      //urlStr += "?" + data;
    //}
    String urlStr = getRequestURL();

    // Send data
    try{
      URL url = new URL(urlStr);
      HttpURLConnection conn = null;
      try {
        conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(this.type.getRequestType());
        conn.setDoOutput(true);
        //conn.setDoInput(true);
        addAuth(conn);
        /* if(this.type == RequestType.GET_LIST) { */
        /*   conn.setChunkedStreamingMode(-1); */
        /* } */
        int rCode = conn.getResponseCode();
        // MAL returns 501 if you try to add an item that is already in your list
        if(rCode == 501 && type == RequestType.ADD) {
          conn.disconnect();
          return "Already in list";
        }


        String str = null;
        try(java.util.Scanner s = new java.util.Scanner(conn.getInputStream(), "UTF-8")) {
          str =  s.useDelimiter("\\A").hasNext() ? s.next() : "";
        }
        conn.disconnect();
        if(this.type == RequestType.GET_LIST) {
          str = new String(str.getBytes(), "UTF-8");
        }
        return str;


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

  protected static void addAuth(HttpURLConnection uc) {
    uc.setRequestProperty("Authorization", basicAuth);
    // Until MAL whitelists me, need to use chrome's user-agent for testing.
    uc.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
    uc.setRequestProperty("Accept-Language", "en");
    // I got on the whitelist! woohoo!
    //uc.setRequestProperty("http.agent", "MWSync");
    // Apparently MAL wants me to use this user-agent
    uc.setRequestProperty("http.agent", "api-indiv-0DE402D09B6DD58E021FCF8C977E51A7");
    // Set a cookie so that Incapsula doesn't complain when getting the user's list
    uc.setRequestProperty("Cookie", "incap_ses_133_81958=T2RyV+YSYTxv7IG654PYAZXZUFYAAAAAhX50qTghmxnoAXaUz9dp2A==; visid_incap_81958=OLUTI+d1RMm5Nwe1qrnRmMy8uVQAAAAAQUIPAAAAAABWdD0a4vqsjxQi2kXz7/JX; incap_ses_32_81958=6hZtEhHmeXZkHWvUKrBxAMj3U1YAAAAAtttCfwHFCUrsgBTaCK486A==");
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

  /**
   * Escapes HTML entities in a string.
   * @param str The string containing the response from the MAL query
   * @return A string with all HTML entities escaped.
   */
  private static String escapeHTMLEntities(String x) {
      return HTML_ENTITIES.matcher(x).replaceAll("&amp;");
  }

}
