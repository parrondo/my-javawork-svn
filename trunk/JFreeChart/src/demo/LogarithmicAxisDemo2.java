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

public class LogarithmicAxisDemo2 extends ApplicationFrame
{
  public LogarithmicAxisDemo2(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createScatterPlot("Logarithmic Axis Demo 2", "X", "Y", paramXYDataset, PlotOrientation.VERTICAL, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    LogarithmicAxis localLogarithmicAxis1 = new LogarithmicAxis("X");
    localLogarithmicAxis1.setExpTickLabelsFlag(true);
    localLogarithmicAxis1.setStrictValuesFlag(false);
    LogarithmicAxis localLogarithmicAxis2 = new LogarithmicAxis("Y");
    localLogarithmicAxis2.setAllowNegativesFlag(true);
    localLogarithmicAxis2.setLog10TickLabelsFlag(true);
    localXYPlot.setDomainAxis(localLogarithmicAxis1);
    localXYPlot.setRangeAxis(localLogarithmicAxis2);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  private static XYDataset createDataset()
  {
    XYSeries localXYSeries = new XYSeries("Series 1");
    localXYSeries.add(-500.0D, -500.0D);
    localXYSeries.add(-50.0D, -50.0D);
    localXYSeries.add(-5.0D, -5.0D);
    localXYSeries.add(0.0D, 0.0D);
    localXYSeries.add(5.0D, 5.0D);
    localXYSeries.add(50.0D, 50.0D);
    localXYSeries.add(500.0D, 500.0D);
    return new XYSeriesCollection(localXYSeries);
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    LogarithmicAxisDemo2 localLogarithmicAxisDemo2 = new LogarithmicAxisDemo2("JFreeChart: LogarithmicAxisDemo2.java");
    localLogarithmicAxisDemo2.pack();
    RefineryUtilities.centerFrameOnScreen(localLogarithmicAxisDemo2);
    localLogarithmicAxisDemo2.setVisible(true);
  }
}