import  java.util.ArrayList;
public class MALSearchResults {
  protected ArrayList<String> titles;
  protected ArrayList<String> ids;
  protected ArrayList<String> types;
  protected String queryString;

  public MALSearchResults(String query) {
    titles = new ArrayList<String>();
    ids = new ArrayList<String>();
    types = new ArrayList<String>();
    queryString = query;
  }
  public MALSearchResults() {
    this("");
  }

  public void add(String title, String id, String type) {
    titles.add(title);
    ids.add(id);
    types.add(type);
  }
  public int size(){
    return ids.size();
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
  public String getType(int index) {
    return types.get(index);
  }
  public String getQueryString() {
    return queryString;
  }

  /**
   * Searches for a title in this search results, matching ignoring case.
   * @param title
   *  The title to search for
   * @return
   *  Returns the index of the title (index >= 0) on success.
   *  Returns -1 if there was no match.
   *  Returns -2 if there was multiple matches.
   */
  public int getIdForTitle(String title) {
    System.out.println("Searching for '"+title+"'");
    // Initialize the search index as not found.
    int index = -1;
    // For each title in the seach results
    for(int i = 0; i < titles.size(); i++) {
      // If the titles match
      if(title.equalsIgnoreCase(titles.get(i))) {
        System.out.println("Found match'"+titles.get(i)+"'" + " " + index);
        // If we have not found any other matches yet
        if(index == -1) {
          // Set the search index to this index
          index = i;
        // If we already found a match
        } else {
          // Set that we found more than one and stop searching
          index = -2;
          return index;
        }
      }
    }
    // Return the search index.
    return index;
  }
}
