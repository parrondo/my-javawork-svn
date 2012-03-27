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
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class MouseListenerDemo1 extends ApplicationFrame
  implements ChartMouseListener
{
  public MouseListenerDemo1(String paramString)
  {
    super(paramString);
    DefaultPieDataset localDefaultPieDataset = new DefaultPieDataset();
    localDefaultPieDataset.setValue("Java", new Double(43.200000000000003D));
    localDefaultPieDataset.setValue("Visual Basic", new Double(0.0D));
    localDefaultPieDataset.setValue("C/C++", new Double(17.5D));
    JFreeChart localJFreeChart = ChartFactory.createPieChart("MouseListenerDemo1", localDefaultPieDataset, true, true, false);
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart, false, false, false, false, false);
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
    MouseListenerDemo1 localMouseListenerDemo1 = new MouseListenerDemo1("JFreeChart: MouseListenerDemo1.java");
    localMouseListenerDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localMouseListenerDemo1);
    localMouseListenerDemo1.setVisible(true);
  }
}