import  java.util.ArrayList;
public class MALSearchResults {
  protected ArrayList<String> titles;
  protected ArrayList<String> ids;

  public MALSearchResults() {
    titles = new ArrayList<String>();
    ids = new ArrayList<String>();
  }

  public void add(String title, String id) {
    titles.add(title);
    ids.add(id);
  }
  public int length(){
    return ids.size();
  }
  public String getTitle(int index) {
    return titles.get(index);
  }
  public String getId(int index) {
    return ids.get(index);
  }

}
