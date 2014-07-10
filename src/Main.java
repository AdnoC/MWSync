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
public class Main {

  public static void main(String[] args) {
    //test();
    new Main();
  }

  public Main() {
    UserInterface ui = getUI();
    Controller control = new Controller();
    ui.registerController(control);
  }

  /**
   * Returns the appropriate subclass of UI for the environment
   * @return a GUI or command line UI.
   */
  public UserInterface getUI() {
    //@TODO: return valid ui objects after I make the classes.
    if(Utils.isGUI()) {
      return null;
    } else {
      return null;
    }
  }

  public static void test() {
    //testUpdate();
    //testSearch();
  }
  private static void testSearch() {
    MALRequest malr = new MALRequest(MALRequest.RequestType.SEARCH);
    String title = "witch hunter";
    malr.addParam("q", title);
    Document doc = malr.requestDocument();
    NodeList nl = doc.getElementsByTagName("title");
    for(int i = 0; i < nl.getLength(); i++) {
      Node nd = nl.item(i);
      String nTitle = nd.getTextContent();
      System.out.println(nTitle);
      if(nTitle.equalsIgnoreCase(title)) {
        Element el1 = (Element) nd.getParentNode();
        Node ndid = el1.getElementsByTagName("id").item(0);
        System.out.println(ndid.getNodeName() + " :: " + ndid.getTextContent());
      }
    }
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
}
