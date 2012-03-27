package demo;

import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class LogarithmicAxisDemo1 extends ApplicationFrame
{
  public LogarithmicAxisDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createScatterPlot("Logarithmic Axis Demo 1", "X", "Y", paramXYDataset, PlotOrientation.VERTICAL, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    LogarithmicAxis localLogarithmicAxis1 = new LogarithmicAxis("X");
    LogarithmicAxis localLogarithmicAxis2 = new LogarithmicAxis("Y");
    localXYPlot.setDomainAxis(localLogarithmicAxis1);
    localXYPlot.setRangeAxis(localLogarithmicAxis2);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  private static XYDataset createDataset()
  {
    XYSeries localXYSeries = new XYSeries("Random Data");
    localXYSeries.add(1.0D, 500.19999999999999D);
    localXYSeries.add(5.0D, 694.10000000000002D);
    localXYSeries.add(4.0D, 100.0D);
    localXYSeries.add(12.5D, 734.39999999999998D);
    localXYSeries.add(17.300000000000001D, 453.19999999999999D);
    localXYSeries.add(21.199999999999999D, 500.19999999999999D);
    localXYSeries.add(21.899999999999999D, 9005.5D);
    localXYSeries.add(25.600000000000001D, 734.39999999999998D);
    localXYSeries.add(3000.0D, 453.19999999999999D);
    return new XYSeriesCollection(localXYSeries);
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    LogarithmicAxisDemo1 localLogarithmicAxisDemo1 = new LogarithmicAxisDemo1("JFreeChart: LogarithmicAxisDemo1.java");
    localLogarithmicAxisDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localLogarithmicAxisDemo1);
    localLogarithmicAxisDemo1.setVisible(true);
  }
}