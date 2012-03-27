package demo;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.Size2D;

public class LegendTitleToImageDemo1
{
  public static void main(String[] paramArrayOfString)
    throws IOException
  {
    DefaultPieDataset localDefaultPieDataset = new DefaultPieDataset();
    localDefaultPieDataset.setValue("A", 1.0D);
    localDefaultPieDataset.setValue("B", 2.0D);
    localDefaultPieDataset.setValue("C", 3.0D);
    JFreeChart localJFreeChart = ChartFactory.createPieChart("Test", localDefaultPieDataset, true, false, false);
    LegendTitle localLegendTitle = localJFreeChart.getLegend();
    localLegendTitle.setMargin(0.0D, 0.0D, 1.0D, 1.0D);
    BufferedImage localBufferedImage1 = new BufferedImage(1, 1, 2);
    Graphics2D localGraphics2D1 = localBufferedImage1.createGraphics();
    Size2D localSize2D = localLegendTitle.arrange(localGraphics2D1);
    localGraphics2D1.dispose();
    int i = (int)Math.rint(localSize2D.width);
    int j = (int)Math.rint(localSize2D.height);
    BufferedImage localBufferedImage2 = new BufferedImage(i, j, 2);
    Graphics2D localGraphics2D2 = localBufferedImage2.createGraphics();
    localLegendTitle.draw(localGraphics2D2, new Rectangle2D.Double(0.0D, 0.0D, i, j));
    localGraphics2D2.dispose();
    BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(new FileOutputStream(new File("LegendTitleToImageDemo1.png")));
    ChartUtilities.writeBufferedImageAsPNG(localBufferedOutputStream, localBufferedImage2);
    localBufferedOutputStream.close();
  }
}