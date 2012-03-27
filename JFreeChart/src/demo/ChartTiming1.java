package demo;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.Timer;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

public class ChartTiming1
  implements ActionListener
{
  private boolean finished;

  public void run()
  {
    this.finished = false;
    DefaultPieDataset localDefaultPieDataset = new DefaultPieDataset();
    localDefaultPieDataset.setValue("One", new Double(10.300000000000001D));
    localDefaultPieDataset.setValue("Two", new Double(8.5D));
    localDefaultPieDataset.setValue("Three", new Double(3.9D));
    localDefaultPieDataset.setValue("Four", new Double(3.9D));
    localDefaultPieDataset.setValue("Five", new Double(3.9D));
    localDefaultPieDataset.setValue("Six", new Double(3.9D));
    boolean bool = true;
    JFreeChart localJFreeChart = ChartFactory.createPieChart("Testing", localDefaultPieDataset, bool, true, false);
    BufferedImage localBufferedImage = new BufferedImage(400, 300, 1);
    Graphics2D localGraphics2D = localBufferedImage.createGraphics();
    Rectangle2D.Double localDouble = new Rectangle2D.Double(0.0D, 0.0D, 400.0D, 300.0D);
    Timer localTimer = new Timer(10000, this);
    localTimer.setRepeats(false);
    int i = 0;
    localTimer.start();
    while (!(this.finished))
    {
      localJFreeChart.draw(localGraphics2D, localDouble, null, null);
      System.out.println("Charts drawn..." + i);
      if (this.finished)
        continue;
      ++i;
    }
    System.out.println("DONE");
  }

  public void actionPerformed(ActionEvent paramActionEvent)
  {
    this.finished = true;
  }

  public static void main(String[] paramArrayOfString)
  {
    ChartTiming1 localChartTiming1 = new ChartTiming1();
    localChartTiming1.run();
  }
}