import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
/**
 * Data storage class that parses a Manga from MW API and stores its information.
 */
public class MWItem implements MangaItem {
/*
   {
            "genre": ",3,5,7,19,25,29",
            "parser_id": 9472,
            "readed": [
                {
                    "chash": "20e4a491e8960caa2c303543c16852449d464",
                    "hash": "72ed17e351a2a5a184d4f8f26d3cb",
                    "link": "http://www.mangaeden.com/en-manga/aragami-hime/1/1/",
                    "is_read": true,
                    "mhash": "mangaeden48741b4988601a79b2a98a54c1520e4a",
                    "date_long": 1350265259620
                },
                {
                    "chash": "20e4a59ebb739fd561864265dd1e6a233cb74",
                    "hash": "474f9c9fd62c22734d18f7e596dc086",
                    "link": "http://www.mangaeden.com/en-manga/aragami-hime/2/1/",
                    "is_read": true,
                    "mhash": "mangaeden48741b4988601a79b2a98a54c1520e4a",
                    "date_long": 1350265278610
                }
            ],
            "status": "2",
            "hash": "2f3cfbd316f8580929303e4ed946840b",
            "catalog": "http://www.mangaeden.com//en-manga/aragami-hime/",
            "reading_direction": "true",
            "image": "http://cdn.mangaeden.com/mangasimg/0b/0b51943eadd45a4985b5132280169cbbf7316386dd07f63c308a4492.jpg",
            "ihash": "cdnmangaeden8a9e01b8c8477898fc0975942fe29782.jpg",
            "mhash": "mangaeden48741b4988601a79b2a98a54c1520e4a",
            "id": "507b683ee4b05d648e64f814",
            "author": "AMANO+Sakuya",
            "title": "Aragami+Hime",
            "uniq": "aragami-hime",
            "mature": "0",
            "rating": "0",
            "date_long": 1350264894210
  },
*/
  public String getImage() {
    return "";
  }
  public String getTitle() {
    return title;
  }
  public String getDecodedTitle() {
    return decodedTitle;
  }
  public int getChapter() {
    return lastRead;
  }
  public int getLastRead() {
    return lastRead;
  }
  protected String stripString(String str) {
    return str.replaceAll("^\"|\"$", "");
  }
  /**
   * Parses a link to a chapter and returns the chapter number.
   * @param link
   *  The string containing the chapter URL.
   * @return
   *  An int of the number of the chapter.
   */
  protected int getChapterFromLink(String link) {
    //@TODO: Possibly map out what sites use what format and use different
    //  algorithm depending on site.
    //System.out.println("Parsing " + link);
    //System.out.println("Last char " + link.charAt(link.length() - 2));
    /*
     * chapter styles:
     * "http://www.mangaeden.com/en-manga/aragami-hime/2/1/"
     *    At end of url chapter then volume
     * "http://www.batoto.net/read/_/107406/asu-no-yoichi_ch7_by_franky-house",
     *    Has 'ch' followed by chapter number
     * "http://starkana.com/manga/F/Fairy_Tail/chapter/315"
     *    chapter # at end of url
     * "http://www.mangapanda.com/94-485-1/bleach/chapter-31.html"
     *    Has 'chapter', a separator, then the #
     * "http://mangafox.me/manga/binbougami_ga/v09/c039/1.html"
     * http://www.batoto.net/read/_/50800/amagoi_v2_by_idws-scans
     *    DOes not contain a chapter #
     */
    final int CHAPTER_OFFSET = 7;
    final int CH_OFFSET = 2;
    final int C_OFFSET = 1;
    if(link.indexOf("batoto.net") != -1) {
      int chLoc = link.lastIndexOf("ch");
      if(chLoc != -1) {
        for(int i = chLoc + CH_OFFSET; i < link.length(); i++) {
          if(! Character.isDigit(link.charAt(i))) {
            String chStr = link.substring(chLoc + CH_OFFSET, i);
            //System.out.println("STR2: " + chStr);
            try {
              int chNum = Integer.parseInt(chStr);
              return chNum;
            } catch(NumberFormatException nfe) {

            }
          }
        }
      } else {
        return 0;
      }
    } else if(link.indexOf("mangaeden.com") != -1) {
      int chEnd = link.lastIndexOf('/', link.length()-3);
      int chBegin = link.lastIndexOf('/', chEnd - 1)+1;
      String chStr = link.substring(chBegin, chEnd);
      try {
        int chNum = Integer.parseInt(chStr);
        return chNum;
      } catch(NumberFormatException nfe) {

      }
    } else if(link.indexOf("mangapanda.com") != -1 || link.indexOf("sarkana.com") != -1 ||
        link.indexOf("mangareader.net") != -1) {
      int chLoc = link.lastIndexOf("chapter");
      if(chLoc != -1) {
        if(! Character.isDigit(link.charAt(chLoc + CHAPTER_OFFSET))) {
          chLoc += 1;
        }
        for(int i = chLoc + CHAPTER_OFFSET; i < link.length(); i++) {
          if(! Character.isDigit(link.charAt(i))) {
            String chStr = link.substring(chLoc + CHAPTER_OFFSET, i);
              //System.out.println("STR1: " + chStr);
            try {
              int chNum = Integer.parseInt(chStr);
              return chNum;
            } catch(NumberFormatException nfe) {

            }
          }
        }
      }
    } else if(link.indexOf("mangafox.me") != -1) {
      int chLoc = link.lastIndexOf("c");
      if(chLoc != -1) {
        for(int i = chLoc + C_OFFSET; i < link.length(); i++) {
          if(! Character.isDigit(link.charAt(i))) {
            String chStr = link.substring(chLoc + C_OFFSET, i);
            try {
              int chNum = Integer.parseInt(chStr);
              return chNum;
            } catch(NumberFormatException nfe) {

            }
          }
        }
      } else {
        return 0;
      }
    }

    int chLoc = link.lastIndexOf("chapter");
    if(chLoc != -1) {
      if(! Character.isDigit(link.charAt(chLoc + CHAPTER_OFFSET))) {
        chLoc += 1;
      }
      for(int i = chLoc + CHAPTER_OFFSET; i < link.length(); i++) {
        if(! Character.isDigit(link.charAt(i))) {
          String chStr = link.substring(chLoc + CHAPTER_OFFSET, i);
            //System.out.println("STR1: " + chStr);
          try {
            int chNum = Integer.parseInt(chStr);
            return chNum;
          } catch(NumberFormatException nfe) {

          }
        }
      }
    } else {
      chLoc = link.lastIndexOf("ch");
      if(chLoc != -1) {
        for(int i = chLoc + CH_OFFSET; i < link.length(); i++) {
          if(! Character.isDigit(link.charAt(i))) {
            String chStr = link.substring(chLoc + CH_OFFSET, i);
            //System.out.println("STR2: " + chStr);
            try {
              int chNum = Integer.parseInt(chStr);
              return chNum;
            } catch(NumberFormatException nfe) {

            }
          }
        }
      } else if(link.charAt(link.length() - 2) == '/'){
        int chEnd = link.lastIndexOf('/', link.length()-3);
        int chBegin = link.lastIndexOf('/', chEnd - 1)+1;
        String chStr = link.substring(chBegin, chEnd);
        System.out.println("STR3: " + chStr);
        try {
          int chNum = Integer.parseInt(chStr);
          return chNum;
        } catch(NumberFormatException nfe) {

        }

      }
    }
    return 0;
  }
  /**
   * Constructor for MWItem.
   * @param jso
   *  An Manga object from MW to parse.
   */
  public MWItem(JsonElement jse) {
    JsonObject jso = jse.getAsJsonObject();
    // Set fields that we usually have no problem with
    genre = jso.get("genre").toString();
    parserId = Integer.parseInt(stripString(jso.get("parser_id").toString()));
    status = Integer.parseInt(stripString(jso.get("status").toString()));
    hash = jso.get("hash").toString();
    catalog = jso.get("catalog").toString();
    readingDirection = Boolean.parseBoolean(jso.get("reading_direction").toString());
    image = jso.get("image").toString();
    mhash = jso.get("mhash").toString();
    id = jso.get("id").toString();
    author = jso.get("author").toString();
    title = jso.get("title").toString();
    try {
      decodedTitle = URLDecoder.decode(title, "UTF-8");
      decodedTitle = stripString(decodedTitle);
    } catch(UnsupportedEncodingException uee) {
      uee.printStackTrace();
    }
    uniq = jso.get("uniq").toString();
    mature = Integer.parseInt(stripString(jso.get("mature").toString()));
    rating = Integer.parseInt(stripString(jso.get("rating").toString()));

    // Be more careful with fields that have been problematic
    JsonElement tmpE = jso.get("ihash");
    if(tmpE != null) {
      ihash = jso.get("ihash").toString();
    } else {
      ihash = "";
    }

    // Get the number of the latest read chapter.
    tmpE = jso.get("readed");
    if(tmpE != null) {
      JsonArray readed = tmpE.getAsJsonArray();
      for(JsonElement arrEl : readed) {
        JsonObject tmpO = arrEl.getAsJsonObject();
        if(Boolean.parseBoolean(tmpO.get("is_read").toString())) {
          int chapter = getChapterFromLink(tmpO.get("link").toString());
          if(chapter > lastRead) {
            lastRead = chapter;
          }
        }
      }
    }

  }
  @Override
  public String toString() {
    String rTitle = "";
    try {
      rTitle = URLDecoder.decode(title, "UTF-8");
    } catch(UnsupportedEncodingException uee) {
      uee.printStackTrace();
    }
    return rTitle + " - " + lastRead + " :: " + id;
  }

  // Data fields containing information about the manga.
  protected String genre;
  protected int parserId;
  protected int status;
  protected String hash;
  protected String catalog;
  protected boolean readingDirection;
  protected String image;
  protected String ihash;
  protected String mhash;
  protected String id;
  protected String author;
  protected String title;
  protected String decodedTitle;
  protected String uniq;
  protected int mature;
  protected int rating;
  protected long dateLong;

  protected int lastRead = 0;
  /**
   * Extraneous class that is going to be removed.
   */
  private class MWReaded {
    public String chash;
    public String hash;
    public String link;
    public boolean isRead;
    public String mhash;
    public long dateLong;
  }
}
