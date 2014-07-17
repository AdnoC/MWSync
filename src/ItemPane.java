import javax.swing.JPanel;
import javax.swing.BorderFactory;
import java.awt.Color;
import javax.swing.SwingConstants;
import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.JButton;
import java.awt.BorderLayout;
import javax.swing.ImageIcon;
import java.awt.Font;
import javax.swing.JLabel;
import java.net.URL;
import java.net.MalformedURLException;
public class ItemPane {
  protected String chapter;
  protected JComponent container;
  public JComponent getContainer() {
    return container;
  }
  protected JLabel name;
  public void resize(Dimension dim) {
    container.setPreferredSize(dim);
    Font labelFont = name.getFont();
    String labelText = name.getText();

    int stringWidth = name.getFontMetrics(labelFont).stringWidth(labelText);
    int componentWidth = name.getWidth();

    // Find out how much the font can grow in width.
    double widthRatio = (double)componentWidth / (double)stringWidth;

    int newFontSize = (int)(labelFont.getSize() * widthRatio);
    int componentHeight = name.getHeight();

    // Pick a new font size so it will not be larger than the height of label.
    int fontSizeToUse = Math.min(newFontSize, componentHeight);

    // Set the label's font size to the newly determined size.
    name.setFont(new Font(labelFont.getName(), Font.PLAIN, fontSizeToUse - 3));
  }


  protected ItemPane(MangaItem malsr) {
    container = new JPanel(new BorderLayout());
    try {
      URL url = new URL(malsr.getImage());
      ImageIcon ii = new ImageIcon(url);
      ImageResizeLabel image = new ImageResizeLabel(ii);
      container.add(image, BorderLayout.WEST);
    } catch(MalformedURLException mue) {
      // Don't do anything.
    }
    name = new JLabel(malsr.getReadableTitle(), SwingConstants.CENTER);
    container.add(name, BorderLayout.CENTER);

    name.setFont(new Font(name.getFont().getName(), Font.PLAIN, 20));
    if(malsr.getChapter() != 0) {
      name.setText(name.getText() + ": " + malsr.getChapter());
    }
    name.setText("<html>" + name.getText() + "</html>");
    container.setOpaque(true);
    container.setBorder(BorderFactory.createLineBorder(Color.BLACK));
  }

  public static ItemPane newSuccess(MangaItem malsr) {
    ItemPane ip = new ItemPane(malsr);
    ip.container.setPreferredSize(new Dimension(500 - 3 * GUI.COMMON_SCROLLBAR_WIDTH, 500 / 4));
    ip.container.setBackground(new Color(102, 255, 102));
    return ip;
  }
  public static ItemPane newFailure(MangaItem malsr) {
    ItemPane ip = new ItemPane(malsr);
    ip.container.setBackground(new Color(255, 102, 102));
    ip.container.setPreferredSize(new Dimension(500 - 3 * GUI.COMMON_SCROLLBAR_WIDTH, 500 / 4));
    return ip;
  }
  public static ItemPane newSearch(MangaItem malsr) {
    ItemPane ip = new ItemPane(malsr);
    ip.container.setBackground(new Color(102, 255, 253));
    JButton jb = new JButton();
    jb.add(ip.container);
    ip.container = jb;
    return ip;

  }
  private class ImageResizeLabel extends JLabel {
    private static final long serialVersionUID = 784058209486L;
    public ImageResizeLabel(ImageIcon ii) {
      super(ii);

    }

    @Override
     protected void paintComponent(java.awt.Graphics g) {
      ImageIcon icon = (ImageIcon) getIcon();
      int iconWidth = icon.getIconWidth();
      int iconHeight = icon.getIconHeight();
      double iconAspect = (double) iconHeight / iconWidth;

      int w = getWidth();
      int h = getHeight();
      double canvasAspect = (double) h / w;

      int x = 0, y = 0;

      // Maintain aspect ratio.
      if(iconAspect < canvasAspect)
      {
       // Drawing space is taller than image.
       y = h;
       h = (int) (w * iconAspect);
       y = (y - h) / 2; // center it along vertical
      }
      else
      {
       // Drawing space is wider than image.
       x = w;
       w = (int) (h / iconAspect);
       x = (x - w) / 2; // center it along horizontal
      }

      java.awt.Image img = icon.getImage();
      g.drawImage(img, x, y, w + x, h + y, 0, 0, iconWidth, iconHeight, null);
     }
  }
}
