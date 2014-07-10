import java.util.Map;
import java.net.URLEncoder;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import org.w3c.dom.Document;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import javax.xml.transform.TransformerException;
import java.io.UnsupportedEncodingException;
public class Utils {

  public static String buildParamsFromMap(Map<String, String> map) {
    //@TODO Change this to a StringBuilder
    String data = "";
    for(Map.Entry<String, String> entry : map.entrySet()) {
      try {
        data += "&" + URLEncoder.encode(entry.getKey(), "UTF-8") + "=";
        data += URLEncoder.encode(entry.getValue(), "UTF-8");
      } catch(UnsupportedEncodingException uee) {
        uee.printStackTrace();
      }
    }
    return data;
  }

  public static void printDocument(Document doc) {
    try {
      printDocument(doc, System.out);
    } catch(IOException ioe) {
      ioe.printStackTrace();
    } catch(TransformerException te) {
      te.printStackTrace();
    }
  }
  public static void printDocument(Document doc, OutputStream out) throws IOException, TransformerException {
    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer transformer = tf.newTransformer();
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

    transformer.transform(new DOMSource(doc), 
         new StreamResult(new OutputStreamWriter(out, "UTF-8")));
  }
  public static boolean isGUI() {
    return (System.console() == null)
    //if (System.console() != null) {
        //console.format("Interactive Console Environment");
    //} else if (!java.awt.GraphicsEnvironment.isHeadless()) {
        //javax.swing.JOptionPane.showMessageDialog(null, "GUI Environment");
    //} else {
      //"There is no way to have interactions";
    //}
  }
}
