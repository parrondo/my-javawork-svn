package demo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.HighLowRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class HighLowChartDemo3 extends ApplicationFrame
{
  public HighLowChartDemo3(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  public static OHLCDataset createDataset1()
  {
    OHLCSeries localOHLCSeries = new OHLCSeries("Series 1");
    localOHLCSeries.add(new Day(24, 9, 2007), 50.5D, 53.200000000000003D, 49.799999999999997D, 50.100000000000001D);
    localOHLCSeries.add(new Day(25, 9, 2007), 50.200000000000003D, 51.200000000000003D, 47.799999999999997D, 48.100000000000001D);
    localOHLCSeries.add(new Day(26, 9, 2007), 48.0D, 49.200000000000003D, 45.299999999999997D, 47.399999999999999D);
    localOHLCSeries.add(new Day(27, 9, 2007), 47.5D, 48.299999999999997D, 46.799999999999997D, 46.799999999999997D);
    localOHLCSeries.add(new Day(28, 9, 2007), 46.600000000000001D, 47.0D, 45.100000000000001D, 46.0D);
    localOHLCSeries.add(new Day(1, 10, 2007), 46.600000000000001D, 47.0D, 45.100000000000001D, 46.0D);
    localOHLCSeries.add(new Day(2, 10, 2007), 47.5D, 48.299999999999997D, 46.799999999999997D, 46.799999999999997D);
    localOHLCSeries.add(new Day(3, 10, 2007), 48.0D, 49.200000000000003D, 45.299999999999997D, 47.399999999999999D);
    localOHLCSeries.add(new Day(4, 10, 2007), 50.200000000000003D, 51.200000000000003D, 47.799999999999997D, 48.100000000000001D);
    localOHLCSeries.add(new Day(5, 10, 2007), 50.5D, 53.200000000000003D, 49.799999999999997D, 50.100000000000001D);
    OHLCSeriesCollection localOHLCSeriesCollection = new OHLCSeriesCollection();
    localOHLCSeriesCollection.addSeries(localOHLCSeries);
    return localOHLCSeriesCollection;
  }

  public static OHLCDataset createDataset2()
  {
    OHLCSeries localOHLCSeries = new OHLCSeries("Series 2");
    localOHLCSeries.add(new Day(24, 9, 2007), 5.5D, 6.2D, 4.8D, 5.9D);
    localOHLCSeries.add(new Day(25, 9, 2007), 6.0D, 6.9D, 6.0D, 6.7D);
    localOHLCSeries.add(new Day(26, 9, 2007), 6.8D, 7.5D, 6.4D, 7.1D);
    localOHLCSeries.add(new Day(27, 9, 2007), 7.2D, 8.199999999999999D, 7.0D, 7.9D);
    localOHLCSeries.add(new Day(28, 9, 2007), 7.8D, 8.5D, 7.7D, 8.199999999999999D);
    localOHLCSeries.add(new Day(1, 10, 2007), 8.199999999999999D, 8.5D, 7.7D, 7.8D);
    localOHLCSeries.add(new Day(2, 10, 2007), 7.9D, 8.199999999999999D, 7.0D, 7.2D);
    localOHLCSeries.add(new Day(3, 10, 2007), 7.1D, 7.5D, 6.4D, 6.8D);
    localOHLCSeries.add(new Day(4, 10, 2007), 6.0D, 6.9D, 6.0D, 6.7D);
    localOHLCSeries.add(new Day(5, 10, 2007), 5.5D, 6.2D, 4.8D, 5.9D);
    OHLCSeriesCollection localOHLCSeriesCollection = new OHLCSeriesCollection();
    localOHLCSeriesCollection.addSeries(localOHLCSeries);
    return localOHLCSeriesCollection;
  }

  private static JFreeChart createChart(OHLCDataset paramOHLCDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createHighLowChart("OHLC Demo 3", "Time", "Price", paramOHLCDataset, true);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    HighLowRenderer localHighLowRenderer = (HighLowRenderer)localXYPlot.getRenderer();
    localHighLowRenderer.setBaseStroke(new BasicStroke(2.0F));
    localHighLowRenderer.setSeriesPaint(0, Color.blue);
    DateAxis localDateAxis = (DateAxis)localXYPlot.getDomainAxis();
    localDateAxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
    NumberAxis localNumberAxis1 = (NumberAxis)localXYPlot.getRangeAxis();
    localNumberAxis1.setAutoRangeIncludesZero(false);
    NumberAxis localNumberAxis2 = new NumberAxis("Price 2");
    localNumberAxis2.setAutoRangeIncludesZero(false);
    localXYPlot.setRangeAxis(1, localNumberAxis2);
    localXYPlot.setDataset(1, createDataset2());
    localXYPlot.setRenderer(1, new CandlestickRenderer(10.0D));
    localXYPlot.mapDatasetToRangeAxis(1, 1);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset1());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    HighLowChartDemo3 localHighLowChartDemo3 = new HighLowChartDemo3("JFreeChart: HighLowChartDemo3.java");
    localHighLowChartDemo3.pack();
    RefineryUtilities.centerFrameOnScreen(localHighLowChartDemo3);
    localHighLowChartDemo3.setVisible(true);
  }
}