import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
public class MALClient {
  protected static MALSearchResults lastSearch = null;
  protected static MALSearchResults myList;
  protected static String lastSearchString;
  public static boolean updateManga(String id, String chapter) {
    addManga(id);
    MALRequest malr = new MALRequest(MALRequest.RequestType.UPDATE);
    malr.addParam("id", id);
    MALData mald = new MALData();
    mald.put("status", "1");
    mald.put("chapter", chapter);
    malr.addParam("data", mald.toString());
    String response = malr.request();
    return response.equals("Updated");
  }
  public static void addManga(String id) {
    MALRequest malr = new MALRequest(MALRequest.RequestType.ADD);
    malr.addParam("id", id);
    MALData mald = new MALData();
    mald.put("status", "1");
    malr.addParam("data", mald.toString());
    malr.request();
  }
  public static MALSearchResults searchMangas(String title) {
    // If we cached the search, just return it
    if(lastSearchString != null && lastSearchString.equals(title)) {
      return lastSearch;
    }
    MALSearchResults malSR = new MALSearchResults(title);
    MALRequest malr = new MALRequest(MALRequest.RequestType.SEARCH);
    malr.addParam("q", title);
    Document doc = malr.requestDocument();
    if(doc == null) {
      System.out.println("----- DOC NULL for " + title);
      return null;
    }
    NodeList nl = doc.getElementsByTagName("title");
    for(int i = 0; i < nl.getLength(); i++) {
      Node nd = nl.item(i);
      String nTitle = nd.getTextContent();
      Element el1 = (Element) nd.getParentNode();
      Node ndid = el1.getElementsByTagName("id").item(0);
      String nId = ndid.getTextContent();
      String nTy = el1.getElementsByTagName("type").item(0).getTextContent();
      String nIm = el1.getElementsByTagName("image").item(0).getTextContent();
      malSR.add(nTitle, nId, nTy, nIm);
    }
    // Cache the query
    lastSearch = malSR;
    lastSearchString = title;
    return malSR;
  }

  public static MALSearchResults getList() {
    MALSearchResults malSR = new MALSearchResults("GET_LIST");
    MALRequest malr = new MALRequest(MALRequest.RequestType.GET_LIST);
    Document doc = malr.requestDocument();
    if(doc == null) {
      return null;
    }
    NodeList nl = doc.getElementsByTagName("manga");
    for(int i = 0; i < nl.getLength(); i++) {
      Element nd = (Element)nl.item(i);
      String sId = nd.getElementsByTagName("series_mangadb_id").item(0).getTextContent();
      String sChap = nd.getElementsByTagName("my_read_chapters").item(0).getTextContent();
      int sChapInt = Integer.valueOf(sChap);

      malSR.add(sId, sChapInt);
    }
    myList = malSR;
    return malSR;
  }

  /**
   * Returns whether the manga should be upserted into MAL based on user's list
   * @param malId The is of the manga to look for
   * @return <pre> Returns a short with the following meaning:
   *  0: The manga is up to date on MAL and nothing should be pushed.
   *  1: The manga is in the list already, but the chapter # should be updated.
   *  2: The manga is not in the list and should be added and updated.</pre>
   */
  public static short canUpsert(String malId, MangaItem mi) {
    int index = myList.getIndexForId(malId);
    // If the manga is not in the user's list
    if(index == -1) {
      return 2;
    } else {
      int listChap = myList.get(index).getChapter();
      int miChap = mi.getChapter();
      short ret = (short) (listChap < miChap ? 1 : 0);
      return ret;
    }
  }
}
