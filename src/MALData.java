import java.util.HashMap;
public class MALData {
  HashMap<String, String> data;
  public void initData() {
    data.put("chapter", "");
    data.put("volume", "");
    data.put("status", "");
    data.put("score", "");
    data.put("downloaded_chapters", "");
    data.put("times_reread", "");
    data.put("reread_value", "");
    data.put("date_start", "");
    data.put("date_finish", "");
    data.put("priority", "");
    data.put("enable_discussion", "");
    data.put("enable_rereading", "");
    data.put("comments", "");
    data.put("scan_group", "");
    data.put("tags", "");
    data.put("retail_volumes", "");
  }
  public MALData() {

  }
  /*
    <?xml version="1.0" encoding="UTF-8"?>
      <entry>
        <chapter>6</chapter>
        <volume>1</volume>
        <status>1</status>
        <score>8</score>
        <downloaded_chapters></downloaded_chapters>
        <times_reread></times_reread>
        <reread_value></reread_value>
        <date_start></date_start>
        <date_finish></date_finish>
        <priority></priority>
        <enable_discussion></enable_discussion>
        <enable_rereading></enable_rereading>
        <comments></comments>
        <scan_group></scan_group>
        <tags></tags>
        <retail_volumes></retail_volumes>
      </entry>
*/
}
