package demo;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CompassFormat;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Minute;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class CompassFormatDemo1 extends ApplicationFrame
{
  public CompassFormatDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static XYDataset createDirectionDataset(int paramInt)
  {
    TimeSeriesCollection localTimeSeriesCollection = new TimeSeriesCollection();
    TimeSeries localTimeSeries = new TimeSeries("Wind Direction");
    Object localObject = new Minute();
    double d = 180.0D;
    for (int i = 0; i < paramInt; ++i)
    {
      localTimeSeries.add((RegularTimePeriod)localObject, d);
      localObject = ((RegularTimePeriod)localObject).next();
      d += (Math.random() - 0.5D) * 15.0D;
      if (d < 0.0D)
      {
        d += 360.0D;
      }
      else
      {
        if (d <= 360.0D)
          continue;
        d -= 360.0D;
      }
    }
    localTimeSeriesCollection.addSeries(localTimeSeries);
    return ((XYDataset)localTimeSeriesCollection);
  }

  private static XYDataset createForceDataset(int paramInt)
  {
    TimeSeriesCollection localTimeSeriesCollection = new TimeSeriesCollection();
    TimeSeries localTimeSeries = new TimeSeries("Wind Force");
    Object localObject = new Minute();
    double d = 3.0D;
    for (int i = 0; i < paramInt; ++i)
    {
      localTimeSeries.add((RegularTimePeriod)localObject, d);
      localObject = ((RegularTimePeriod)localObject).next();
      d = Math.max(0.5D, d + (Math.random() - 0.5D) * 0.5D);
    }
    localTimeSeriesCollection.addSeries(localTimeSeries);
    return ((XYDataset)localTimeSeriesCollection);
  }

  private static JFreeChart createChart()
  {
    XYDataset localXYDataset = createDirectionDataset(600);
    JFreeChart localJFreeChart = ChartFactory.createTimeSeriesChart("Time", "Date", "Direction", localXYDataset, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setDomainPannable(true);
    localXYPlot.setRangePannable(true);
    localXYPlot.getDomainAxis().setLowerMargin(0.0D);
    localXYPlot.getDomainAxis().setUpperMargin(0.0D);
    NumberAxis localNumberAxis1 = (NumberAxis)localXYPlot.getRangeAxis();
    localNumberAxis1.setAutoRangeIncludesZero(false);
    TickUnits localTickUnits = new TickUnits();
    localTickUnits.add(new NumberTickUnit(180.0D, new CompassFormat()));
    localTickUnits.add(new NumberTickUnit(90.0D, new CompassFormat()));
    localTickUnits.add(new NumberTickUnit(45.0D, new CompassFormat()));
    localTickUnits.add(new NumberTickUnit(22.5D, new CompassFormat()));
    localNumberAxis1.setStandardTickUnits(localTickUnits);
    localXYPlot.setRangeAxis(localNumberAxis1);
    XYAreaRenderer localXYAreaRenderer = new XYAreaRenderer();
    NumberAxis localNumberAxis2 = new NumberAxis("Force");
    localNumberAxis2.setRange(0.0D, 12.0D);
    localXYAreaRenderer.setSeriesPaint(0, new Color(0, 0, 255, 128));
    localXYPlot.setDataset(1, createForceDataset(600));
    localXYPlot.setRenderer(1, localXYAreaRenderer);
    localXYPlot.setRangeAxis(1, localNumberAxis2);
    localXYPlot.mapDatasetToRangeAxis(1, 1);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart();
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setMouseWheelEnabled(true);
    return localChartPanel;
  }

  public static void main(String[] paramArrayOfString)
  {
    CompassFormatDemo1 localCompassFormatDemo1 = new CompassFormatDemo1("JFreeChart: CompassFormatDemo1.java");
    localCompassFormatDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localCompassFormatDemo1);
    localCompassFormatDemo1.setVisible(true);
  }
}