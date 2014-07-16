import  java.util.ArrayList;
public class MALSearchResults {
  public static boolean onlyMangas = true;
  protected ArrayList<MALSearchResult> results;
  // Maps valid search result index with result array indexes
  protected String queryString;

  public MALSearchResults(String query) {
    results = new ArrayList<MALSearchResult>();
    queryString = query;
  }
  public MALSearchResults() {
    this("");
  }

  public void add(String title, String id, String type) {
    add(title, id, type, "");
  }

  public void add(String title, String id, String type, String image) {
    if(MALSearchResults.onlyMangas && type.equalsIgnoreCase("manga")) {
      results.add(new MALSearchResult(title, id, type, image));
    }
  }
  public int size(){
    return results.size();
  }
  public int length(){
    return this.size();
  }
  public String getTitle(int index) {
    return results.get(index).title;
  }
  public String getId(int index) {
    return results.get(index).id;
  }
  public String getType(int index) {
    return results.get(index).type;
  }
  public String getQueryString() {
    return queryString;
  }
  public MALSearchResult get(int index) {
    return results.get(index);
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
    for(int i = 0; i < results.size(); i++) {
      // If the titles match
      if(results.get(i).equals(title)) {
        System.out.println("Found match'"+results.get(i).title+"'" + " " + index);
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
  protected class MALSearchResult {
    protected String title;
    protected String id;
    protected String type;
    protected String imageUrl;
    public String chapter;
    public MALSearchResult(String title, String id, String type, String imageUrl) {
      this.title = title;
      this.id = id;
      this.type = type;
      this.imageUrl = imageUrl;
    }
    public String getTitle() {
      return title;
    }
    public String getId() {
      return id;
    }
    public String getImage() {
      return imageUrl;
    }
    public String getType() {
      return type;
    }
    @Override
    public boolean equals(Object o) {
      if(o instanceof String) {
        String s = (String) o;
        return s.equalsIgnoreCase(title);
      } else if (o instanceof MALSearchResult) {
        MALSearchResult m = (MALSearchResult) o;
        return m.title.equalsIgnoreCase(title);
      } else {
        return o.toString().equalsIgnoreCase(title);
      }
    }
  }
}
