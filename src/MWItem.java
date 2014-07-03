public class MWItem {
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
  public MWItem () {
  }
  public String genre;
  public int parserId;
  public int status;
  public String hash;
  public String catalog;
  public boolean readingDirection;
  public String image;
  public String ihash;
  public String mhash;
  public String id;
  public String author;
  public String title;
  public String uniq;
  public int mature;
  public int rating;
  public long dateLong;

  private class MWReaded {
    public String chash;
    public String hash;
    public String link;
    public boolean isRead;
    public String mhash;
    public long dateLong;
  }
}
