package demo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import org.jfree.ui.Drawable;

public class CircleDrawer
  implements Drawable
{
  private Paint outlinePaint;
  private Stroke outlineStroke;
  private Paint fillPaint;

  public CircleDrawer(Paint paramPaint1, Stroke paramStroke, Paint paramPaint2)
  {
    this.outlinePaint = paramPaint1;
    this.outlineStroke = paramStroke;
    this.fillPaint = paramPaint2;
  }

  public void draw(Graphics2D paramGraphics2D, Rectangle2D paramRectangle2D)
  {
    Ellipse2D.Double localDouble = new Ellipse2D.Double(paramRectangle2D.getX(), paramRectangle2D.getY(), paramRectangle2D.getWidth(), paramRectangle2D.getHeight());
    if (this.fillPaint != null)
    {
      paramGraphics2D.setPaint(this.fillPaint);
      paramGraphics2D.fill(localDouble);
    }
    if ((this.outlinePaint != null) && (this.outlineStroke != null))
    {
      paramGraphics2D.setPaint(this.outlinePaint);
      paramGraphics2D.setStroke(this.outlineStroke);
      paramGraphics2D.draw(localDouble);
    }
    paramGraphics2D.setPaint(Color.black);
    paramGraphics2D.setStroke(new BasicStroke(1.0F));
    Line2D.Double localDouble1 = new Line2D.Double(paramRectangle2D.getCenterX(), paramRectangle2D.getMinY(), paramRectangle2D.getCenterX(), paramRectangle2D.getMaxY());
    Line2D.Double localDouble2 = new Line2D.Double(paramRectangle2D.getMinX(), paramRectangle2D.getCenterY(), paramRectangle2D.getMaxX(), paramRectangle2D.getCenterY());
    paramGraphics2D.draw(localDouble1);
    paramGraphics2D.draw(localDouble2);
  }
}