import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
public class MALClient {
  protected static MALSearchResults lastSearch = null;
  protected static String lastSearchString;
  public static boolean updateManga(String id, String chapter) {
    addManga(id);
    MALRequest malr = new MALRequest(MALRequest.RequestType.UPDATE);
    malr.addParam("id", id);
    MALData mald = new MALData();
    mald.put("status", "1");
    mald.put("chapter", chapter);
    malr.addParam("data", mald.toString());
    String response = malr.requestString();
    return response.equals("Updated");
  }
  public static void addManga(String id) {
    MALRequest malr = new MALRequest(MALRequest.RequestType.ADD);
    malr.addParam("id", id);
    MALData mald = new MALData();
    mald.put("status", "1");
    malr.addParam("data", mald.toString());
    malr.requestString();
  }
  public static MALSearchResults searchMangas(String title) {
    if(lastSearchString.equals(title)) {
      return lastSearch;
    }
    MALSearchResults malSR = new MALSearchResults();
    MALRequest malr = new MALRequest(MALRequest.RequestType.SEARCH);
    malr.addParam("q", title);
    Document doc = malr.requestDocument();
    NodeList nl = doc.getElementsByTagName("title");
    for(int i = 0; i < nl.getLength(); i++) {
      Node nd = nl.item(i);
      String nTitle = nd.getTextContent();
      Element el1 = (Element) nd.getParentNode();
      Node ndid = el1.getElementsByTagName("id").item(0);
      String nId = ndid.getTextContent();
      malSR.add(nTitle, nId);
    }
    lastSearch = malSR;
    return malSR;
  }

}
