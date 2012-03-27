package demo;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.io.PrintStream;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class MouseListenerDemo2 extends ApplicationFrame
  implements ChartMouseListener
{
  public MouseListenerDemo2(String paramString)
  {
    super(paramString);
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(1.0D, "S1", "C1");
    localDefaultCategoryDataset.addValue(4.0D, "S1", "C2");
    localDefaultCategoryDataset.addValue(3.0D, "S1", "C3");
    localDefaultCategoryDataset.addValue(5.0D, "S1", "C4");
    localDefaultCategoryDataset.addValue(5.0D, "S1", "C5");
    localDefaultCategoryDataset.addValue(6.0D, "S1", "C6");
    localDefaultCategoryDataset.addValue(7.0D, "S1", "C7");
    localDefaultCategoryDataset.addValue(8.0D, "S1", "C8");
    localDefaultCategoryDataset.addValue(5.0D, "S2", "C1");
    localDefaultCategoryDataset.addValue(7.0D, "S2", "C2");
    localDefaultCategoryDataset.addValue(6.0D, "S2", "C3");
    localDefaultCategoryDataset.addValue(8.0D, "S2", "C4");
    localDefaultCategoryDataset.addValue(4.0D, "S2", "C5");
    localDefaultCategoryDataset.addValue(4.0D, "S2", "C6");
    localDefaultCategoryDataset.addValue(3.0D, "S2", "C7");
    localDefaultCategoryDataset.addValue(1.0D, "S2", "C8");
    localDefaultCategoryDataset.addValue(4.0D, "S3", "C1");
    localDefaultCategoryDataset.addValue(3.0D, "S3", "C2");
    localDefaultCategoryDataset.addValue(2.0D, "S3", "C3");
    localDefaultCategoryDataset.addValue(3.0D, "S3", "C4");
    localDefaultCategoryDataset.addValue(6.0D, "S3", "C5");
    localDefaultCategoryDataset.addValue(3.0D, "S3", "C6");
    localDefaultCategoryDataset.addValue(4.0D, "S3", "C7");
    localDefaultCategoryDataset.addValue(3.0D, "S3", "C8");
    JFreeChart localJFreeChart = ChartFactory.createBarChart("MouseListenerDemo2", "Category", "Value", localDefaultCategoryDataset, PlotOrientation.VERTICAL, true, true, false);
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.addChartMouseListener(this);
    localChartPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localChartPanel);
  }

  public void chartMouseClicked(ChartMouseEvent paramChartMouseEvent)
  {
    ChartEntity localChartEntity = paramChartMouseEvent.getEntity();
    if (localChartEntity != null)
      System.out.println("Mouse clicked: " + localChartEntity.toString());
    else
      System.out.println("Mouse clicked: null entity.");
  }

  public void chartMouseMoved(ChartMouseEvent paramChartMouseEvent)
  {
    int i = paramChartMouseEvent.getTrigger().getX();
    int j = paramChartMouseEvent.getTrigger().getY();
    ChartEntity localChartEntity = paramChartMouseEvent.getEntity();
    if (localChartEntity != null)
      System.out.println("Mouse moved: " + i + ", " + j + ": " + localChartEntity.toString());
    else
      System.out.println("Mouse moved: " + i + ", " + j + ": null entity.");
  }

  public static void main(String[] paramArrayOfString)
  {
    MouseListenerDemo2 localMouseListenerDemo2 = new MouseListenerDemo2("JFreeChart: MouseListenerDemo2.java");
    localMouseListenerDemo2.pack();
    RefineryUtilities.centerFrameOnScreen(localMouseListenerDemo2);
    localMouseListenerDemo2.setVisible(true);
  }
}