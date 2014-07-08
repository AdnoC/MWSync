import java.util.HashMap;
import java.util.Map;
public class MALData extends HashMap<String, String> {
  private static final long serialVersionUID = 5438579298L;

  public MALData() {

  }

  @Override
  public String toString() {
    StringBuilder ret = new StringBuilder();
    ret.append("<entry>");
    for(Map.Entry<String, String> entry : this.entrySet()) {
      ret.append("<" + entry.getKey() + ">");
      ret.append(entry.getValue());
      ret.append("</" + entry.getKey() + ">");
    }
    ret.append("</entry>");
    return ret.toString();
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
