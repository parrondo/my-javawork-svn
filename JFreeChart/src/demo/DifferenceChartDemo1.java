package demo;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDifferenceRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class DifferenceChartDemo1 extends ApplicationFrame
{
  public DifferenceChartDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static XYDataset createDataset()
  {
    TimeSeries localTimeSeries1 = new TimeSeries("Random 1");
    TimeSeries localTimeSeries2 = new TimeSeries("Random 2");
    double d1 = 0.0D;
    double d2 = 0.0D;
    Day localDay = new Day();
    for (int i = 0; i < 200; ++i)
    {
      d1 = d1 + Math.random() - 0.5D;
      d2 = d2 + Math.random() - 0.5D;
      localTimeSeries1.add(localDay, d1);
      localTimeSeries2.add(localDay, d2);
      localDay = (Day)localDay.next();
    }
    TimeSeriesCollection localTimeSeriesCollection = new TimeSeriesCollection();
    localTimeSeriesCollection.addSeries(localTimeSeries1);
    localTimeSeriesCollection.addSeries(localTimeSeries2);
    return localTimeSeriesCollection;
  }

  private static JFreeChart createChart(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createTimeSeriesChart("Difference Chart Demo 1", "Time", "Value", paramXYDataset, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setDomainPannable(true);
    XYDifferenceRenderer localXYDifferenceRenderer = new XYDifferenceRenderer(Color.green, Color.red, false);
    localXYDifferenceRenderer.setRoundXCoordinates(true);
    localXYPlot.setDomainCrosshairLockedOnData(true);
    localXYPlot.setRangeCrosshairLockedOnData(true);
    localXYPlot.setDomainCrosshairVisible(true);
    localXYPlot.setRangeCrosshairVisible(true);
    localXYPlot.setRenderer(localXYDifferenceRenderer);
    DateAxis localDateAxis = new DateAxis("Time");
    localDateAxis.setLowerMargin(0.0D);
    localDateAxis.setUpperMargin(0.0D);
    localXYPlot.setDomainAxis(localDateAxis);
    localXYPlot.setForegroundAlpha(0.5F);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
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
    DifferenceChartDemo1 localDifferenceChartDemo1 = new DifferenceChartDemo1("JFreeChart: DifferenceChartDemo1.java");
    localDifferenceChartDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localDifferenceChartDemo1);
    localDifferenceChartDemo1.setVisible(true);
  }
}