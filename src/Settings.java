import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.OutputStream;
import java.io.InputStream;
//import java.io.BufferedInputStream;
//import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.HashMap;


import java.util.Properties;
public class Settings {
  public static final Settings SETTINGS = new Settings();
  protected static final String CONFIG_NAME = "MW_SYNC_CONF";
  public static final String MAL_NAME = "MyAnimeList";
  public static final String MW_NAME = "MangaWatcher";
  protected static final String USER_STR = "Username";

  protected Properties props;
  Path file;

  protected Settings() {
    props = new Properties();
    file = Paths.get(System.getProperty("user.home"), CONFIG_NAME);
    if(Files.exists(file)) {
      try (
        InputStream is = Files.newInputStream(file);
      ) {
        props.load(is);
      } catch(IOException ioe) {

      }
    }
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        save();
      }
    });
  }

  protected void save() {
    try (
      OutputStream os = Files.newOutputStream(file);
    ){
      props.store(os, null);
    } catch(IOException ioe) {

    }
  }

  public String getMWName() {
    return getName(MW_NAME);
  }
  public String getMALName() {
    return getName(MAL_NAME);
  }
  public String getName(String service) {
System.out.println(props.getProperty(service + "." + USER_STR));
    return props.getProperty(service + "." + USER_STR, "");
  }
  public void setMALName(String name) {
    setName(MAL_NAME, name);
  }
  public void setMWName(String name) {
    setName(MW_NAME, name);
  }
  public void setName(String service, String name) {
    props.setProperty(service + "." + USER_STR, name);
  }

  public void mapMangaPair(String mhash, String malId) {
    props.setProperty(mhash, malId);
  }
  public String getMALId(String mhash) {
    return props.getProperty(mhash);
  }


  public static void main(String[] args) {
    //HashMap<String, String> map = new HashMap<String, String>();
    //map.put("a", "b");
    //map.put("b", "\n\\n");
    //map.put("c", "asdf");
    //try {
      //Path file = Paths.get(System.getProperty("user.home"), "MYCONF");
      //if(! Files.exists(file)) {
        //Files.createFile(file);
      //}
      //ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(file));
      //oos.writeObject(map);


      //if(! Files.exists(file)) {
        //Files.createFile(file);
      //}
      //ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(file));
      //HashMap<String, String> m2;
      //m2 = (HashMap<String, String>) ois.readObject();
      //for(String s : m2.keySet()) {
        //System.out.println("Key: " + s);
        //System.out.println("Val: " + m2.get(s));

      //}

    //} catch(IOException ioe) {
      //ioe.printStackTrace();
    //} catch(ClassNotFoundException cnfe) {
    //}

  }
}
