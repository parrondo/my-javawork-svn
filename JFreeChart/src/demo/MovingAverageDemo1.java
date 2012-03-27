package demo;

import java.awt.Color;
import java.awt.Dimension;
import java.text.SimpleDateFormat;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Month;
import org.jfree.data.time.MovingAverage;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class MovingAverageDemo1 extends ApplicationFrame
{
  public MovingAverageDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  public static XYDataset createDataset()
  {
    TimeSeries localTimeSeries1 = new TimeSeries("L&G European Index Trust");
    localTimeSeries1.add(new Month(2, 2001), 181.80000000000001D);
    localTimeSeries1.add(new Month(3, 2001), 167.30000000000001D);
    localTimeSeries1.add(new Month(4, 2001), 153.80000000000001D);
    localTimeSeries1.add(new Month(5, 2001), 167.59999999999999D);
    localTimeSeries1.add(new Month(6, 2001), 158.80000000000001D);
    localTimeSeries1.add(new Month(7, 2001), 148.30000000000001D);
    localTimeSeries1.add(new Month(8, 2001), 153.90000000000001D);
    localTimeSeries1.add(new Month(9, 2001), 142.69999999999999D);
    localTimeSeries1.add(new Month(10, 2001), 123.2D);
    localTimeSeries1.add(new Month(11, 2001), 131.80000000000001D);
    localTimeSeries1.add(new Month(12, 2001), 139.59999999999999D);
    localTimeSeries1.add(new Month(1, 2002), 142.90000000000001D);
    localTimeSeries1.add(new Month(2, 2002), 138.69999999999999D);
    localTimeSeries1.add(new Month(3, 2002), 137.30000000000001D);
    localTimeSeries1.add(new Month(4, 2002), 143.90000000000001D);
    localTimeSeries1.add(new Month(5, 2002), 139.80000000000001D);
    localTimeSeries1.add(new Month(6, 2002), 137.0D);
    localTimeSeries1.add(new Month(7, 2002), 132.80000000000001D);
    TimeSeries localTimeSeries2 = MovingAverage.createMovingAverage(localTimeSeries1, "Six Month Moving Average", 6, 0);
    TimeSeriesCollection localTimeSeriesCollection = new TimeSeriesCollection();
    localTimeSeriesCollection.addSeries(localTimeSeries1);
    localTimeSeriesCollection.addSeries(localTimeSeries2);
    return localTimeSeriesCollection;
  }

  public static JFreeChart createChart(XYDataset paramXYDataset)
  {
    String str = "Legal & General Unit Trust Prices";
    JFreeChart localJFreeChart = ChartFactory.createTimeSeriesChart(str, "Date", "Price Per Unit", paramXYDataset, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    XYItemRenderer localXYItemRenderer = localXYPlot.getRenderer();
    if (localXYItemRenderer instanceof XYLineAndShapeRenderer)
    {
      XYLineAndShapeRenderer localObject = (XYLineAndShapeRenderer)localXYItemRenderer;
      ((XYLineAndShapeRenderer)localObject).setBaseShapesVisible(false);
      ((XYLineAndShapeRenderer)localObject).setSeriesShapesVisible(0, true);
      ((XYLineAndShapeRenderer)localObject).setUseFillPaint(true);
      ((XYLineAndShapeRenderer)localObject).setBaseFillPaint(Color.white);
    }
    Object localObject = (DateAxis)localXYPlot.getDomainAxis();
    ((DateAxis)localObject).setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));
    return ((JFreeChart)localJFreeChart);
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
    MovingAverageDemo1 localMovingAverageDemo1 = new MovingAverageDemo1("JFreeChart: MovingAverageDemo1.java");
    localMovingAverageDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localMovingAverageDemo1);
    localMovingAverageDemo1.setVisible(true);
  }
}