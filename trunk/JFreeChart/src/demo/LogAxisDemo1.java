package demo;

import java.awt.BasicStroke;
import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class LogAxisDemo1 extends ApplicationFrame
{
  public LogAxisDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createScatterPlot("Log Axis Demo 1", "X", "Y", paramXYDataset, PlotOrientation.VERTICAL, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setDomainPannable(true);
    localXYPlot.setRangePannable(true);
    localXYPlot.setDomainGridlineStroke(new BasicStroke(1.0F));
    localXYPlot.setRangeGridlineStroke(new BasicStroke(1.0F));
    localXYPlot.setDomainMinorGridlinesVisible(true);
    localXYPlot.setRangeMinorGridlinesVisible(true);
    localXYPlot.setDomainMinorGridlineStroke(new BasicStroke(0.1F));
    localXYPlot.setRangeMinorGridlineStroke(new BasicStroke(0.1F));
    LogAxis localLogAxis1 = new LogAxis("X");
    LogAxis localLogAxis2 = new LogAxis("Y");
    localXYPlot.setDomainAxis(localLogAxis1);
    localXYPlot.setRangeAxis(localLogAxis2);
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
    localXYSeries.add(6663000.0D, 6453.1999999999998D);
    return new XYSeriesCollection(localXYSeries);
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setMouseWheelEnabled(true);
    return localChartPanel;
  }

  public static void main(String[] paramArrayOfString)
  {
    LogAxisDemo1 localLogAxisDemo1 = new LogAxisDemo1("JFreeChart: LogAxisDemo1.java");
    localLogAxisDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localLogAxisDemo1);
    localLogAxisDemo1.setVisible(true);
  }
}