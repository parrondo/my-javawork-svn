package demo;

import java.awt.Dimension;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class XYAreaChartDemo2 extends ApplicationFrame
{
  public XYAreaChartDemo2(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static XYDataset createDataset()
  {
    TimeSeries localTimeSeries = new TimeSeries("Random 1");
    double d = 0.0D;
    Day localDay = new Day();
    for (int i = 0; i < 200; ++i)
    {
      d = d + Math.random() - 0.5D;
      localTimeSeries.add(localDay, d);
      localDay = (Day)localDay.next();
    }
    TimeSeriesCollection localTimeSeriesCollection = new TimeSeriesCollection(localTimeSeries);
    return localTimeSeriesCollection;
  }

  private static JFreeChart createChart(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createXYAreaChart("XY Area Chart Demo 2", "Time", "Value", paramXYDataset, PlotOrientation.VERTICAL, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setDomainPannable(true);
    DateAxis localDateAxis = new DateAxis("Time");
    localDateAxis.setLowerMargin(0.0D);
    localDateAxis.setUpperMargin(0.0D);
    localXYPlot.setDomainAxis(localDateAxis);
    localXYPlot.setForegroundAlpha(0.5F);
    XYItemRenderer localXYItemRenderer = localXYPlot.getRenderer();
    localXYItemRenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator("{0}: ({1}, {2})", new SimpleDateFormat("d-MMM-yyyy"), new DecimalFormat("#,##0.00")));
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    return new ChartPanel(createChart(createDataset()));
  }

  public static void main(String[] paramArrayOfString)
  {
    XYAreaChartDemo2 localXYAreaChartDemo2 = new XYAreaChartDemo2("XY Area Chart Demo 2");
    localXYAreaChartDemo2.pack();
    RefineryUtilities.centerFrameOnScreen(localXYAreaChartDemo2);
    localXYAreaChartDemo2.setVisible(true);
  }
}