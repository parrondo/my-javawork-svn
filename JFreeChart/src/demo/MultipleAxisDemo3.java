package demo;

import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Minute;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class MultipleAxisDemo3 extends ApplicationFrame
{
  public MultipleAxisDemo3(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(600, 270));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart()
  {
    XYDataset localXYDataset1 = createDataset("Series 1", 100.0D, new Minute(), 200);
    JFreeChart localJFreeChart = ChartFactory.createTimeSeriesChart("Multiple Axis Demo 3", "Time of Day", "Primary Range Axis", localXYDataset1, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setOrientation(PlotOrientation.VERTICAL);
    NumberAxis localNumberAxis1 = new NumberAxis("Domain Axis 2");
    localNumberAxis1.setAutoRangeIncludesZero(false);
    localXYPlot.setDomainAxis(1, localNumberAxis1);
    localXYPlot.setDomainAxisLocation(1, AxisLocation.BOTTOM_OR_LEFT);
    NumberAxis localNumberAxis2 = new NumberAxis("Domain Axis 3");
    localNumberAxis1.setAutoRangeIncludesZero(false);
    localXYPlot.setDomainAxis(2, localNumberAxis2);
    localXYPlot.setDomainAxisLocation(2, AxisLocation.BOTTOM_OR_LEFT);
    NumberAxis localNumberAxis3 = new NumberAxis("Range Axis 2");
    localXYPlot.setRangeAxis(1, localNumberAxis3);
    localXYPlot.setRangeAxisLocation(1, AxisLocation.BOTTOM_OR_RIGHT);
    XYDataset localXYDataset2 = createDataset("Series 2", 1000.0D, new Minute(), 170);
    localXYPlot.setDataset(1, localXYDataset2);
    localXYPlot.mapDatasetToDomainAxis(1, 1);
    localXYPlot.mapDatasetToRangeAxis(1, 1);
    localXYPlot.setRenderer(1, new XYLineAndShapeRenderer(true, false));
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  private static XYDataset createDataset(String paramString, double paramDouble, RegularTimePeriod paramRegularTimePeriod, int paramInt)
  {
    TimeSeries localTimeSeries = new TimeSeries(paramString);
    RegularTimePeriod localRegularTimePeriod = paramRegularTimePeriod;
    double d = paramDouble;
    for (int i = 0; i < paramInt; ++i)
    {
      localTimeSeries.add(localRegularTimePeriod, d);
      localRegularTimePeriod = localRegularTimePeriod.next();
      d *= (1.0D + (Math.random() - 0.495D) / 10.0D);
    }
    TimeSeriesCollection localTimeSeriesCollection = new TimeSeriesCollection();
    localTimeSeriesCollection.addSeries(localTimeSeries);
    return localTimeSeriesCollection;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart();
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    MultipleAxisDemo3 localMultipleAxisDemo3 = new MultipleAxisDemo3("JFreeChart: MultipleAxisDemo3.java");
    localMultipleAxisDemo3.pack();
    RefineryUtilities.centerFrameOnScreen(localMultipleAxisDemo3);
    localMultipleAxisDemo3.setVisible(true);
  }
}