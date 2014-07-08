import java.util.Map;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
public class Utils {

  public static String buildParamsFromMap(Map m) {
    String data = "";
    for(Map.Entry<String, String> entry : params.entrySet()) {
      try {
        data += "&" + URLEncoder.encode(entry.getKey(), "UTF-8") + "=";
        data += URLEncoder.encode(entry.getValue(), "UTF-8");
      } catch(UnsupportedEncodingException uee) {
        uee.printStackTrace();
      }
    }
    return data;
  }

}
