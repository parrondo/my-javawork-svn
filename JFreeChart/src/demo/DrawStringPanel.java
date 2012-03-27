package demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
import org.jfree.text.TextUtilities;
import org.jfree.ui.TextAnchor;

public class DrawStringPanel extends JPanel
{
  private static final Dimension PREFERRED_SIZE = new Dimension(500, 300);
  private boolean rotate;
  private String text = "Hello World";
  private TextAnchor anchor = TextAnchor.TOP_LEFT;
  private TextAnchor rotationAnchor = TextAnchor.TOP_LEFT;
  private Font font = new Font("Serif", 0, 12);
  private double angle;

  public DrawStringPanel(String paramString, boolean paramBoolean)
  {
    this.text = paramString;
    this.rotate = paramBoolean;
  }

  public Dimension getPreferredSize()
  {
    return PREFERRED_SIZE;
  }

  public void setAnchor(TextAnchor paramTextAnchor)
  {
    this.anchor = paramTextAnchor;
  }

  public void setRotationAnchor(TextAnchor paramTextAnchor)
  {
    this.rotationAnchor = paramTextAnchor;
  }

  public void setAngle(double paramDouble)
  {
    this.angle = paramDouble;
  }

  public Font getFont()
  {
    return this.font;
  }

  public void setFont(Font paramFont)
  {
    this.font = paramFont;
  }

  public void paintComponent(Graphics paramGraphics)
  {
    super.paintComponent(paramGraphics);
    Graphics2D localGraphics2D = (Graphics2D)paramGraphics;
    Dimension localDimension = getSize();
    Insets localInsets = getInsets();
    Rectangle2D.Double localDouble = new Rectangle2D.Double(localInsets.left, localInsets.top, localDimension.getWidth() - localInsets.left - localInsets.right, localDimension.getHeight() - localInsets.top - localInsets.bottom);
    double d1 = localDouble.getCenterX();
    double d2 = localDouble.getCenterY();
    Line2D.Double localDouble1 = new Line2D.Double(d1 - 2.0D, d2 + 2.0D, d1 + 2.0D, d2 - 2.0D);
    Line2D.Double localDouble2 = new Line2D.Double(d1 - 2.0D, d2 - 2.0D, d1 + 2.0D, d2 + 2.0D);
    localGraphics2D.setPaint(Color.red);
    localGraphics2D.draw(localDouble1);
    localGraphics2D.draw(localDouble2);
    localGraphics2D.setFont(this.font);
    localGraphics2D.setPaint(Color.black);
    if (this.rotate)
      TextUtilities.drawRotatedString(this.text, localGraphics2D, (float)d1, (float)d2, this.anchor, this.angle, this.rotationAnchor);
    else
      TextUtilities.drawAlignedString(this.text, localGraphics2D, (float)d1, (float)d2, this.anchor);
  }
}