package demo;

import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.ThermometerPlot;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class ThermometerDemo2 extends ApplicationFrame
{
  public ThermometerDemo2(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart()
  {
    DefaultValueDataset localDefaultValueDataset = new DefaultValueDataset(37.200000000000003D);
    ThermometerPlot localThermometerPlot = new ThermometerPlot(localDefaultValueDataset);
    JFreeChart localJFreeChart = new JFreeChart("ThermometerDemo2", localThermometerPlot);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart();
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    ThermometerDemo2 localThermometerDemo2 = new ThermometerDemo2("JFreeChart: ThermometerDemo2.java");
    localThermometerDemo2.pack();
    RefineryUtilities.centerFrameOnScreen(localThermometerDemo2);
    localThermometerDemo2.setVisible(true);
  }
}