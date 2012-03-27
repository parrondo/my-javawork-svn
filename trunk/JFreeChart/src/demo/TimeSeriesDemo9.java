package demo;

import java.awt.Dimension;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class TimeSeriesDemo9 extends ApplicationFrame
{
  public TimeSeriesDemo9(String paramString)
  {
    super(paramString);
    XYDataset localXYDataset = createDataset();
    JFreeChart localJFreeChart = createChart(localXYDataset);
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localChartPanel);
  }

  private static JFreeChart createChart(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createTimeSeriesChart("Time Series Demo 9", "Date", "Price Per Unit", paramXYDataset, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    XYItemRenderer localXYItemRenderer = localXYPlot.getRenderer();
    if (localXYItemRenderer instanceof XYLineAndShapeRenderer)
    {
      XYLineAndShapeRenderer localXYLineAndShapeRenderer = (XYLineAndShapeRenderer)localXYItemRenderer;
      localXYLineAndShapeRenderer.setBaseShapesVisible(true);
      localXYLineAndShapeRenderer.setBaseShapesFilled(true);
      localXYLineAndShapeRenderer.setSeriesShape(0, new Ellipse2D.Double(-3.0D, -3.0D, 6.0D, 6.0D));
      localXYLineAndShapeRenderer.setSeriesShape(1, new Rectangle2D.Double(-3.0D, -3.0D, 6.0D, 6.0D));
      GeneralPath localGeneralPath1 = new GeneralPath();
      localGeneralPath1.moveTo(0.0F, -3.0F);
      localGeneralPath1.lineTo(3.0F, 3.0F);
      localGeneralPath1.lineTo(-3.0F, 3.0F);
      localGeneralPath1.closePath();
      localXYLineAndShapeRenderer.setSeriesShape(2, localGeneralPath1);
      GeneralPath localGeneralPath2 = new GeneralPath();
      localGeneralPath2.moveTo(-1.0F, -3.0F);
      localGeneralPath2.lineTo(1.0F, -3.0F);
      localGeneralPath2.lineTo(1.0F, -1.0F);
      localGeneralPath2.lineTo(3.0F, -1.0F);
      localGeneralPath2.lineTo(3.0F, 1.0F);
      localGeneralPath2.lineTo(1.0F, 1.0F);
      localGeneralPath2.lineTo(1.0F, 3.0F);
      localGeneralPath2.lineTo(-1.0F, 3.0F);
      localGeneralPath2.lineTo(-1.0F, 1.0F);
      localGeneralPath2.lineTo(-3.0F, 1.0F);
      localGeneralPath2.lineTo(-3.0F, -1.0F);
      localGeneralPath2.lineTo(-1.0F, -1.0F);
      localGeneralPath2.closePath();
      localXYLineAndShapeRenderer.setSeriesShape(3, localGeneralPath2);
    }
    localXYPlot.getDomainAxis().setVisible(false);
    localXYPlot.getRangeAxis().setVisible(false);
    return localJFreeChart;
  }

  private static XYDataset createDataset()
  {
    TimeSeriesCollection localTimeSeriesCollection = new TimeSeriesCollection();
    for (int i = 0; i < 4; ++i)
      localTimeSeriesCollection.addSeries(createTimeSeries(i, 10));
    return localTimeSeriesCollection;
  }

  private static TimeSeries createTimeSeries(int paramInt1, int paramInt2)
  {
    TimeSeries localTimeSeries = new TimeSeries("Series " + paramInt1);
    Day localDay = new Day();
    for (int i = 0; i < paramInt2; ++i)
    {
      localTimeSeries.add(localDay, Math.random());
      localDay = (Day)localDay.next();
    }
    return localTimeSeries;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    TimeSeriesDemo9 localTimeSeriesDemo9 = new TimeSeriesDemo9("Time Series Demo 9");
    localTimeSeriesDemo9.pack();
    RefineryUtilities.centerFrameOnScreen(localTimeSeriesDemo9);
    localTimeSeriesDemo9.setVisible(true);
  }
}