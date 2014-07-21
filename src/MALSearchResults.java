import  java.util.ArrayList;
import java.util.regex.Pattern;
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
    add(title, id, type, "", 0);
  }

  public void add(String title, String id, String type, String image) {
    add(title, id, type, image, 0);
  }

  public void add(String title, String id, String type, String image, int chapter) {
    if(MALSearchResults.onlyMangas && type.equalsIgnoreCase("manga")) {
      results.add(new MALSearchResult(title, id, type, image, chapter));
    }
  }
  public void add(String id, int chapter) {
    add("", id, "manga", "", chapter);
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

  public int getIndexForId(String id) {
    for(int i = 0; i < results.size(); i++) {
      if(id.equals(results.get(i).getId())) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Searches for a title in this search results, matching ignoring case.
   * @param title The title to search for
   * @return <pre>
   *  Returns the index of the title (index >= 0) on success.
   *  Returns -1 if there was no match.
   *  Returns -2 if there was multiple matches.
   *  </pre>
   */
  public int getIdForTitle(String title) {
    title = processWord(title);
    // Initialize the search index as not found.
    int index = -1;
    // For each title in the seach results
    for(int i = 0; i < results.size(); i++) {
      // If the titles match
      if(results.get(i).equals(title)) {
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
  private static final Pattern UNDESIRABLES = Pattern.compile("[\\Q][(){},.;!?<>%\\E]");
  private static String processWord(String x) {
      return UNDESIRABLES.matcher(x).replaceAll("");
  }
  protected class MALSearchResult implements MangaItem {
    protected String title;
    protected String matchingTitle;
    protected String id;
    protected String type;
    protected String imageUrl;
    public int chapter;
    public MALSearchResult(String title, String id, String type, String imageUrl) {
      this(title, id, type, imageUrl, 0);
    }
    public MALSearchResult(String title, String id, String type, String imageUrl, int chapter) {
      this.title = title;
      this.matchingTitle = processWord(title);
      this.id = id;
      this.type = type;
      this.imageUrl = imageUrl;
      this.chapter = chapter;
    }
    public String getReadableTitle() {
      return title;
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
    public int getChapter() {
      return chapter;
    }
    @Override
    public boolean equals(Object o) {
      if(o instanceof String) {
        String s = (String) o;
        // Stripping non-words makes for better matching when there are things like '!'
        return s.equalsIgnoreCase(matchingTitle);
      } else if (o instanceof MALSearchResult) {
        MALSearchResult m = (MALSearchResult) o;
        return m.title.equalsIgnoreCase(matchingTitle);
      } else {
        return o.toString().equalsIgnoreCase(matchingTitle);
      }
    }
  }
}
