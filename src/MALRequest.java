import java.net.HttpURLConnection;
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

  // NOTE: I can probably just delete things inside problematic tags. If not, putting it
  // inside a <![CDATA[  ]]> tag should work
  protected String requestURL;
  protected Map<String, String> params;
  protected RequestType type;
  protected Document document;
  protected static boolean[] auth = new boolean[2];
  protected static String basicAuth = "";
  // Matches the synopsis tag, any character repeated any number of times, then the end synopsis
  // tag. Does a reluctant match for the things in between the two tags
  private static final Pattern SYNOPSIS_MATCH = Pattern.compile("<synopsis>.*?<\\/synopsis>");

  public enum RequestType {
    LOGIN, ADD, UPDATE, SEARCH, GET_LIST;

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
    System.out.println("MALREQ CONSTRUCT");
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
        req = removeSynopsis(req);
        try {
          // Fix simple xml entity errors
          //req = req.replaceAll("&rsquo", "&amp;rsquo");
          InputStream is = new ByteArrayInputStream(req.getBytes());
          //InputSource is = new InputSource(new ByteArrayInputStream(req.getBytes()));
          //is.setEncoding("UTF-8");
          Document dom = dbuilder.parse(is);
          this.document = dom;
        } catch(SAXException saxe) {
          // If we had a sax exception, replace all html entities and try parsing
          // again. This will solve 99% of the problems.
          // @TODO: Add handles for all html entities
          //req = escapeHtmlEntities(req);
          //try{
            //InputStream is = new ByteArrayInputStream(req.getBytes());
            //try{
              //Document dom = dbuilder.parse(is);
              //this.document = dom;
            //} catch(SAXException saxe2) {
              //saxe2.printStackTrace();
            //}
          //} catch(IOException ioe) {
            //ioe.printStackTrace();
          //}
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
      System.out.println("CAN'T REQ");
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
    //DEBUG
    System.out.println("URL STR: " + urlStr);

    // Send data
    try{
      URL url = new URL(urlStr);
      HttpURLConnection conn = null;
      try {
        conn = (HttpURLConnection) url.openConnection();
        addAuth(conn);
        //conn.setDoOutput(true);
        int rCode = conn.getResponseCode();
        //DEBUG
        System.out.println("MESS: " + conn.getResponseMessage());
        // MAL returns 501 if you try to add an item that is already in your list
        if(rCode == 501 && type == RequestType.ADD) {
          return "Already in list";
        }


        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        StringBuilder builder = new StringBuilder();
        String aux = "";
          while ((aux = reader.readLine()) != null) {
            builder.append(aux);
        }

        //DEBUG
        System.out.println("DOC: " + builder.toString());
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
    // I got on the whitelist! woohoo!
    //uc.setRequestProperty("http.agent", "MWSync");
    // Apparently MAL wants me to use this user-agent
    uc.setRequestProperty("http.agent", "api-indiv-0DE402D09B6DD58E021FCF8C977E51A7");
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
   * <pre>Removes the synopsis from MAL's xml.
   * This is done since they do not escape it and it sometimes contains HTML
   * entities that break the XML parser.</pre>
   * @param x The string containing the response from the MAL query
   * @return The response, but without any synopsii
   */
  private static String removeSynopsis(String x) {
      return SYNOPSIS_MATCH.matcher(x).replaceAll("");
  }

  /**
   * <pre>Escapes HTML entities in a string. No longer used since I now just get rid of the problematic
   * area.</pre>
   * @param str The string containing the response from the MAL query
   * @return The response, but without any synopsii
   */
  public String escapeHtmlEntities(String str) {
    // I feel like there must be a better way of doing this...
    // EDIT: Fount one!
    str = str.replaceAll("&nbsp","&amp;nbsp"); // no-break space (non-breaking space)
    str = str.replaceAll("&iexcl","&amp;iexcl"); // inverted exclamation mark
    str = str.replaceAll("&cent","&amp;cent"); // cent sign
    str = str.replaceAll("&pound","&amp;pound"); // pound sign
    str = str.replaceAll("&curren","&amp;curren"); // currency sign
    str = str.replaceAll("&yen","&amp;yen"); // yen sign (yuan sign)
    str = str.replaceAll("&brvbar","&amp;brvbar"); // broken bar (broken vertical bar)
    str = str.replaceAll("&sect","&amp;sect"); // section sign
    str = str.replaceAll("&uml","&amp;uml"); // diaeresis (spacing diaeresis); see German umlaut
    str = str.replaceAll("&copy","&amp;copy"); // copyright sign
    str = str.replaceAll("&ordf","&amp;ordf"); // feminine ordinal indicator
    str = str.replaceAll("&laquo","&amp;laquo"); // left-pointing double angle quotation mark (left pointing guillemet)
    str = str.replaceAll("&not","&amp;not"); // not sign
    str = str.replaceAll("&shy","&amp;shy"); // soft hyphen (discretionary hyphen)
    str = str.replaceAll("&reg","&amp;reg"); // registered sign ( = registered trade mark sign)
    str = str.replaceAll("&macr","&amp;macr"); // macron (spacing macron = overline = APL overbar)
    str = str.replaceAll("&deg","&amp;deg"); // degree sign
    str = str.replaceAll("&plusmn","&amp;plusmn"); // plus-minus sign (plus-or-minus sign)
    str = str.replaceAll("&sup2","&amp;sup2"); // superscript two (superscript digit two = squared)
    str = str.replaceAll("&sup3","&amp;sup3"); // superscript three (superscript digit three = cubed)
    str = str.replaceAll("&acute","&amp;acute"); // acute accent (spacing acute)
    str = str.replaceAll("&micro","&amp;micro"); // micro sign
    str = str.replaceAll("&para","&amp;para"); // pilcrow sign ( = paragraph sign)
    str = str.replaceAll("&middot","&amp;middot"); // middle dot (Georgian comma = Greek middle dot)
    str = str.replaceAll("&cedil","&amp;cedil"); // cedilla (spacing cedilla)
    str = str.replaceAll("&sup1","&amp;sup1"); // superscript one (superscript digit one)
    str = str.replaceAll("&ordm","&amp;ordm"); // masculine ordinal indicator
    str = str.replaceAll("&raquo","&amp;raquo"); // right-pointing double angle quotation mark (right pointing guillemet)
    str = str.replaceAll("&frac14","&amp;frac14"); // vulgar fraction one quarter (fraction one quarter)
    str = str.replaceAll("&frac12","&amp;frac12"); // vulgar fraction one half (fraction one half)
    str = str.replaceAll("&frac34","&amp;frac34"); // vulgar fraction three quarters (fraction three quarters)
    str = str.replaceAll("&iquest","&amp;iquest"); // inverted question mark (turned question mark)
    str = str.replaceAll("&Agrave","&amp;Agrave"); // Latin capital letter A with grave (Latin capital letter A grave)
    str = str.replaceAll("&Aacute","&amp;Aacute"); // Latin capital letter A with acute
    str = str.replaceAll("&Acirc","&amp;Acirc"); // Latin capital letter A with circumflex
    str = str.replaceAll("&Atilde","&amp;Atilde"); // Latin capital letter A with tilde
    str = str.replaceAll("&Auml","&amp;Auml"); // Latin capital letter A with diaeresis
    str = str.replaceAll("&Aring","&amp;Aring"); // Latin capital letter A with ring above (Latin capital letter A ring)
    str = str.replaceAll("&AElig","&amp;AElig"); // Latin capital letter AE (Latin capital ligature AE)
    str = str.replaceAll("&Ccedil","&amp;Ccedil"); // Latin capital letter C with cedilla
    str = str.replaceAll("&Egrave","&amp;Egrave"); // Latin capital letter E with grave
    str = str.replaceAll("&Eacute","&amp;Eacute"); // Latin capital letter E with acute
    str = str.replaceAll("&Ecirc","&amp;Ecirc"); // Latin capital letter E with circumflex
    str = str.replaceAll("&Euml","&amp;Euml"); // Latin capital letter E with diaeresis
    str = str.replaceAll("&Igrave","&amp;Igrave"); // Latin capital letter I with grave
    str = str.replaceAll("&Iacute","&amp;Iacute"); // Latin capital letter I with acute
    str = str.replaceAll("&Icirc","&amp;Icirc"); // Latin capital letter I with circumflex
    str = str.replaceAll("&Iuml","&amp;Iuml"); // Latin capital letter I with diaeresis
    str = str.replaceAll("&ETH","&amp;ETH"); // Latin capital letter ETH
    str = str.replaceAll("&Ntilde","&amp;Ntilde"); // Latin capital letter N with tilde
    str = str.replaceAll("&Ograve","&amp;Ograve"); // Latin capital letter O with grave
    str = str.replaceAll("&Oacute","&amp;Oacute"); // Latin capital letter O with acute
    str = str.replaceAll("&Ocirc","&amp;Ocirc"); // Latin capital letter O with circumflex
    str = str.replaceAll("&Otilde","&amp;Otilde"); // Latin capital letter O with tilde
    str = str.replaceAll("&Ouml","&amp;Ouml"); // Latin capital letter O with diaeresis
    str = str.replaceAll("&times","&amp;times"); // multiplication sign
    str = str.replaceAll("&Oslash","&amp;Oslash"); // Latin capital letter O with stroke (Latin capital letter O slash)
    str = str.replaceAll("&Ugrave","&amp;Ugrave"); // Latin capital letter U with grave
    str = str.replaceAll("&Uacute","&amp;Uacute"); // Latin capital letter U with acute
    str = str.replaceAll("&Ucirc","&amp;Ucirc"); // Latin capital letter U with circumflex
    str = str.replaceAll("&Uuml","&amp;Uuml"); // Latin capital letter U with diaeresis
    str = str.replaceAll("&Yacute","&amp;Yacute"); // Latin capital letter Y with acute
    str = str.replaceAll("&THORN","&amp;THORN"); // Latin capital letter THORN
    str = str.replaceAll("&szlig","&amp;szlig"); // Latin small letter sharp s (ess-zed); see German Eszett
    str = str.replaceAll("&agrave","&amp;agrave"); // Latin small letter a with grave
    str = str.replaceAll("&aacute","&amp;aacute"); // Latin small letter a with acute
    str = str.replaceAll("&acirc","&amp;acirc"); // Latin small letter a with circumflex
    str = str.replaceAll("&atilde","&amp;atilde"); // Latin small letter a with tilde
    str = str.replaceAll("&auml","&amp;auml"); // Latin small letter a with diaeresis
    str = str.replaceAll("&aring","&amp;aring"); // Latin small letter a with ring above
    str = str.replaceAll("&aelig","&amp;aelig"); // Latin small letter ae (Latin small ligature ae)
    str = str.replaceAll("&ccedil","&amp;ccedil"); // Latin small letter c with cedilla
    str = str.replaceAll("&egrave","&amp;egrave"); // Latin small letter e with grave
    str = str.replaceAll("&eacute","&amp;eacute"); // Latin small letter e with acute
    str = str.replaceAll("&ecirc","&amp;ecirc"); // Latin small letter e with circumflex
    str = str.replaceAll("&euml","&amp;euml"); // Latin small letter e with diaeresis
    str = str.replaceAll("&igrave","&amp;igrave"); // Latin small letter i with grave
    str = str.replaceAll("&iacute","&amp;iacute"); // Latin small letter i with acute
    str = str.replaceAll("&icirc","&amp;icirc"); // Latin small letter i with circumflex
    str = str.replaceAll("&iuml","&amp;iuml"); // Latin small letter i with diaeresis
    str = str.replaceAll("&eth","&amp;eth"); // Latin small letter eth
    str = str.replaceAll("&ntilde","&amp;ntilde"); // Latin small letter n with tilde
    str = str.replaceAll("&ograve","&amp;ograve"); // Latin small letter o with grave
    str = str.replaceAll("&oacute","&amp;oacute"); // Latin small letter o with acute
    str = str.replaceAll("&ocirc","&amp;ocirc"); // Latin small letter o with circumflex
    str = str.replaceAll("&otilde","&amp;otilde"); // Latin small letter o with tilde
    str = str.replaceAll("&ouml","&amp;ouml"); // Latin small letter o with diaeresis
    str = str.replaceAll("&divide","&amp;divide"); // division sign
    str = str.replaceAll("&oslash","&amp;oslash"); // Latin small letter o with stroke (Latin small letter o slash)
    str = str.replaceAll("&ugrave","&amp;ugrave"); // Latin small letter u with grave
    str = str.replaceAll("&uacute","&amp;uacute"); // Latin small letter u with acute
    str = str.replaceAll("&ucirc","&amp;ucirc"); // Latin small letter u with circumflex
    str = str.replaceAll("&uuml","&amp;uuml"); // Latin small letter u with diaeresis
    str = str.replaceAll("&yacute","&amp;yacute"); // Latin small letter y with acute
    str = str.replaceAll("&thorn","&amp;thorn"); // Latin small letter thorn
    str = str.replaceAll("&yuml","&amp;yuml"); // Latin small letter y with diaeresis
    str = str.replaceAll("&OElig","&amp;OElig"); // Latin capital ligature oe
    str = str.replaceAll("&oelig","&amp;oelig"); // Latin small ligature oe
    str = str.replaceAll("&Scaron","&amp;Scaron"); // Latin capital letter s with caron
    str = str.replaceAll("&scaron","&amp;scaron"); // Latin small letter s with caron
    str = str.replaceAll("&Yuml","&amp;Yuml"); // Latin capital letter y with diaeresis
    str = str.replaceAll("&fnof","&amp;fnof"); // Latin small letter f with hook (function = florin)
    str = str.replaceAll("&circ","&amp;circ"); // modifier letter circumflex accent
    str = str.replaceAll("&tilde","&amp;tilde"); // small tilde
    str = str.replaceAll("&Alpha","&amp;Alpha"); // Greek capital letter Alpha
    str = str.replaceAll("&Beta","&amp;Beta"); // Greek capital letter Beta
    str = str.replaceAll("&Gamma","&amp;Gamma"); // Greek capital letter Gamma
    str = str.replaceAll("&Delta","&amp;Delta"); // Greek capital letter Delta
    str = str.replaceAll("&Epsilon","&amp;Epsilon"); // Greek capital letter Epsilon
    str = str.replaceAll("&Zeta","&amp;Zeta"); // Greek capital letter Zeta
    str = str.replaceAll("&Eta","&amp;Eta"); // Greek capital letter Eta
    str = str.replaceAll("&Theta","&amp;Theta"); // Greek capital letter Theta
    str = str.replaceAll("&Iota","&amp;Iota"); // Greek capital letter Iota
    str = str.replaceAll("&Kappa","&amp;Kappa"); // Greek capital letter Kappa
    str = str.replaceAll("&Lambda","&amp;Lambda"); // Greek capital letter Lambda
    str = str.replaceAll("&Mu","&amp;Mu"); // Greek capital letter Mu
    str = str.replaceAll("&Nu","&amp;Nu"); // Greek capital letter Nu
    str = str.replaceAll("&Xi","&amp;Xi"); // Greek capital letter Xi
    str = str.replaceAll("&Omicron","&amp;Omicron"); // Greek capital letter Omicron
    str = str.replaceAll("&Pi","&amp;Pi"); // Greek capital letter Pi
    str = str.replaceAll("&Rho","&amp;Rho"); // Greek capital letter Rho
    str = str.replaceAll("&Sigma","&amp;Sigma"); // Greek capital letter Sigma
    str = str.replaceAll("&Tau","&amp;Tau"); // Greek capital letter Tau
    str = str.replaceAll("&Upsilon","&amp;Upsilon"); // Greek capital letter Upsilon
    str = str.replaceAll("&Phi","&amp;Phi"); // Greek capital letter Phi
    str = str.replaceAll("&Chi","&amp;Chi"); // Greek capital letter Chi
    str = str.replaceAll("&Psi","&amp;Psi"); // Greek capital letter Psi
    str = str.replaceAll("&Omega","&amp;Omega"); // Greek capital letter Omega
    str = str.replaceAll("&alpha","&amp;alpha"); // Greek small letter alpha
    str = str.replaceAll("&beta","&amp;beta"); // Greek small letter beta
    str = str.replaceAll("&gamma","&amp;gamma"); // Greek small letter gamma
    str = str.replaceAll("&delta","&amp;delta"); // Greek small letter delta
    str = str.replaceAll("&epsilon","&amp;epsilon"); // Greek small letter epsilon
    str = str.replaceAll("&zeta","&amp;zeta"); // Greek small letter zeta
    str = str.replaceAll("&eta","&amp;eta"); // Greek small letter eta
    str = str.replaceAll("&theta","&amp;theta"); // Greek small letter theta
    str = str.replaceAll("&iota","&amp;iota"); // Greek small letter iota
    str = str.replaceAll("&kappa","&amp;kappa"); // Greek small letter kappa
    str = str.replaceAll("&lambda","&amp;lambda"); // Greek small letter lambda
    str = str.replaceAll("&mu","&amp;mu"); // Greek small letter mu
    str = str.replaceAll("&nu","&amp;nu"); // Greek small letter nu
    str = str.replaceAll("&xi","&amp;xi"); // Greek small letter xi
    str = str.replaceAll("&omicron","&amp;omicron"); // Greek small letter omicron
    str = str.replaceAll("&pi","&amp;pi"); // Greek small letter pi
    str = str.replaceAll("&rho","&amp;rho"); // Greek small letter rho
    str = str.replaceAll("&sigmaf","&amp;sigmaf"); // Greek small letter final sigma
    str = str.replaceAll("&sigma","&amp;sigma"); // Greek small letter sigma
    str = str.replaceAll("&tau","&amp;tau"); // Greek small letter tau
    str = str.replaceAll("&upsilon","&amp;upsilon"); // Greek small letter upsilon
    str = str.replaceAll("&phi","&amp;phi"); // Greek small letter phi
    str = str.replaceAll("&chi","&amp;chi"); // Greek small letter chi
    str = str.replaceAll("&psi","&amp;psi"); // Greek small letter psi
    str = str.replaceAll("&omega","&amp;omega"); // Greek small letter omega
    str = str.replaceAll("&thetasym","&amp;thetasym"); // Greek theta symbol
    str = str.replaceAll("&upsih","&amp;upsih"); // Greek Upsilon with hook symbol
    str = str.replaceAll("&piv","&amp;piv"); // Greek pi symbol
    str = str.replaceAll("&ensp","&amp;ensp"); // en space
    str = str.replaceAll("&emsp","&amp;emsp"); // em space
    str = str.replaceAll("&thinsp","&amp;thinsp"); // thin space
    str = str.replaceAll("&zwnj","&amp;zwnj"); // zero-width non-joiner
    str = str.replaceAll("&zwj","&amp;zwj"); // zero-width joiner
    str = str.replaceAll("&lrm","&amp;lrm"); // left-to-right mark
    str = str.replaceAll("&rlm","&amp;rlm"); // right-to-left mark
    str = str.replaceAll("&ndash","&amp;ndash"); // en dash
    str = str.replaceAll("&mdash","&amp;mdash"); // em dash
    str = str.replaceAll("&lsquo","&amp;lsquo"); // left single quotation mark
    str = str.replaceAll("&rsquo","&amp;rsquo"); // right single quotation mark
    str = str.replaceAll("&sbquo","&amp;sbquo"); // single low-9 quotation mark
    str = str.replaceAll("&ldquo","&amp;ldquo"); // left double quotation mark
    str = str.replaceAll("&rdquo","&amp;rdquo"); // right double quotation mark
    str = str.replaceAll("&bdquo","&amp;bdquo"); // double low-9 quotation mark
    str = str.replaceAll("&dagger","&amp;dagger"); // dagger
    str = str.replaceAll("&Dagger","&amp;Dagger"); // double dagger
    str = str.replaceAll("&bull","&amp;bull"); // bullet (black small circle)
    str = str.replaceAll("&hellip","&amp;hellip"); // horizontal ellipsis (three dot leader)
    str = str.replaceAll("&permil","&amp;permil"); // per mille sign
    str = str.replaceAll("&prime","&amp;prime"); // prime (minutes = feet)
    str = str.replaceAll("&Prime","&amp;Prime"); // double prime (seconds = inches)
    str = str.replaceAll("&lsaquo","&amp;lsaquo"); // single left-pointing angle quotation mark
    str = str.replaceAll("&rsaquo","&amp;rsaquo"); // single right-pointing angle quotation mark
    str = str.replaceAll("&oline","&amp;oline"); // overline (spacing overscore)
    str = str.replaceAll("&frasl","&amp;frasl"); // fraction slash (Solidus (punctuation)|solidus)
    str = str.replaceAll("&euro","&amp;euro"); // euro sign
    str = str.replaceAll("&image","&amp;image"); // black-letter capital I (imaginary part)
    str = str.replaceAll("&weierp","&amp;weierp"); // script capital P (power set = Weierstrass p)
    str = str.replaceAll("&real","&amp;real"); // black-letter capital R (real part symbol)
    str = str.replaceAll("&trade","&amp;trade"); // trademark sign
    str = str.replaceAll("&alefsym","&amp;alefsym"); // alef symbol (first transfinite cardinal)
    str = str.replaceAll("&larr","&amp;larr"); // leftwards arrow
    str = str.replaceAll("&uarr","&amp;uarr"); // upwards arrow
    str = str.replaceAll("&rarr","&amp;rarr"); // rightwards arrow
    str = str.replaceAll("&darr","&amp;darr"); // downwards arrow
    str = str.replaceAll("&harr","&amp;harr"); // left right arrow
    str = str.replaceAll("&crarr","&amp;crarr"); // downwards arrow with corner leftwards (carriage return)
    str = str.replaceAll("&lArr","&amp;lArr"); // leftwards double arrow
    str = str.replaceAll("&uArr","&amp;uArr"); // upwards double arrow
    str = str.replaceAll("&rArr","&amp;rArr"); // rightwards double arrow
    str = str.replaceAll("&dArr","&amp;dArr"); // downwards double arrow
    str = str.replaceAll("&hArr","&amp;hArr"); // left right double arrow
    str = str.replaceAll("&forall","&amp;forall"); // for all
    str = str.replaceAll("&part","&amp;part"); // partial differential
    str = str.replaceAll("&exist","&amp;exist"); // there exists
    str = str.replaceAll("&empty","&amp;empty"); // empty set (null set = diameter)
    str = str.replaceAll("&nabla","&amp;nabla"); // nabla (backward difference)
    str = str.replaceAll("&isin","&amp;isin"); // element of
    str = str.replaceAll("&notin","&amp;notin"); // not an element of
    str = str.replaceAll("&ni","&amp;ni"); // contains as member
    str = str.replaceAll("&prod","&amp;prod"); // n-ary product (product sign)
    str = str.replaceAll("&sum","&amp;sum"); // n-ary summation
    str = str.replaceAll("&minus","&amp;minus"); // minus sign
    str = str.replaceAll("&lowast","&amp;lowast"); // asterisk operator
    str = str.replaceAll("&radic","&amp;radic"); // square root (radical sign)
    str = str.replaceAll("&prop","&amp;prop"); // proportional to
    str = str.replaceAll("&infin","&amp;infin"); // infinity
    str = str.replaceAll("&ang","&amp;ang"); // angle
    str = str.replaceAll("&and","&amp;and"); // logical and (wedge)
    str = str.replaceAll("&or","&amp;or"); // logical or (vee)
    str = str.replaceAll("&cap","&amp;cap"); // intersection (cap)
    str = str.replaceAll("&cup","&amp;cup"); // union (cup)
    str = str.replaceAll("&int","&amp;int"); // integral
    str = str.replaceAll("&there4","&amp;there4"); // therefore
    str = str.replaceAll("&sim","&amp;sim"); // tilde operator (varies with = similar to)
    str = str.replaceAll("&cong","&amp;cong"); // congruent to
    str = str.replaceAll("&asymp","&amp;asymp"); // almost equal to (asymptotic to)
    str = str.replaceAll("&ne","&amp;ne"); // not equal to
    str = str.replaceAll("&equiv","&amp;equiv"); // identical to; sometimes used for 'equivalent to'
    str = str.replaceAll("&le","&amp;le"); // less-than or equal to
    str = str.replaceAll("&ge","&amp;ge"); // greater-than or equal to
    str = str.replaceAll("&sub","&amp;sub"); // subset of
    str = str.replaceAll("&sup","&amp;sup"); // superset of
    str = str.replaceAll("&nsub","&amp;nsub"); // not a subset of
    str = str.replaceAll("&sube","&amp;sube"); // subset of or equal to
    str = str.replaceAll("&supe","&amp;supe"); // superset of or equal to
    str = str.replaceAll("&oplus","&amp;oplus"); // circled plus (direct sum)
    str = str.replaceAll("&otimes","&amp;otimes"); // circled times (vector product)
    str = str.replaceAll("&perp","&amp;perp"); // up tack (orthogonal to = perpendicular)
    str = str.replaceAll("&sdot","&amp;sdot"); // dot operator
    str = str.replaceAll("&lceil","&amp;lceil"); // left ceiling (APL upstile)
    str = str.replaceAll("&rceil","&amp;rceil"); // right ceiling
    str = str.replaceAll("&lfloor","&amp;lfloor"); // left floor (APL downstile)
    str = str.replaceAll("&rfloor","&amp;rfloor"); // right floor
    str = str.replaceAll("&lang","&amp;lang"); // left-pointing angle bracket (bra)
    str = str.replaceAll("&rang","&amp;rang"); // right-pointing angle bracket (ket)
    str = str.replaceAll("&loz","&amp;loz"); // lozenge
    str = str.replaceAll("&spades","&amp;spades"); // black spade suit
    str = str.replaceAll("&clubs","&amp;clubs"); // black club suit (shamrock)
    str = str.replaceAll("&hearts","&amp;hearts"); // black heart suit (valentine)
    str = str.replaceAll("&diams","&amp;diams"); // black diamond suit
    return str;
  }
}
