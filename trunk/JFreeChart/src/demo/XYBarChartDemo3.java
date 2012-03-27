package demo;

import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class XYBarChartDemo3 extends ApplicationFrame
{
  public XYBarChartDemo3(String paramString)
  {
    super(paramString);
    SimpleIntervalXYDataset localSimpleIntervalXYDataset = new SimpleIntervalXYDataset();
    JFreeChart localJFreeChart = createChart(localSimpleIntervalXYDataset);
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setPreferredSize(new Dimension(500, 300));
    setContentPane(localChartPanel);
  }

  private static JFreeChart createChart(IntervalXYDataset paramIntervalXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createXYBarChart("Sample", "X", false, "Y", paramIntervalXYDataset, PlotOrientation.VERTICAL, true, true, false);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    return new ChartPanel(createChart(new SimpleIntervalXYDataset()));
  }

  public static void main(String[] paramArrayOfString)
  {
    XYBarChartDemo3 localXYBarChartDemo3 = new XYBarChartDemo3("XY Bar Chart Demo 3");
    localXYBarChartDemo3.pack();
    RefineryUtilities.centerFrameOnScreen(localXYBarChartDemo3);
    localXYBarChartDemo3.setVisible(true);
  }
}