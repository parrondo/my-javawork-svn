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
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class PriceVolumeDemo2 extends ApplicationFrame
{
  public PriceVolumeDemo2(String paramString)
  {
    super(paramString);
    JFreeChart localJFreeChart = createChart();
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart, true, true, true, false, true);
    localChartPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localChartPanel);
  }

  private static JFreeChart createChart()
  {
    OHLCDataset localOHLCDataset = createPriceDataset();
    String str = "Sun Microsystems (SUNW)";
    JFreeChart localJFreeChart = ChartFactory.createHighLowChart(str, "Date", "Price", localOHLCDataset, true);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    DateAxis localDateAxis = (DateAxis)localXYPlot.getDomainAxis();
    localDateAxis.setLowerMargin(0.01D);
    localDateAxis.setUpperMargin(0.01D);
    NumberAxis localNumberAxis1 = (NumberAxis)localXYPlot.getRangeAxis();
    localNumberAxis1.setLowerMargin(0.6D);
    localNumberAxis1.setAutoRangeIncludesZero(false);
    XYItemRenderer localXYItemRenderer = localXYPlot.getRenderer();
    localXYItemRenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator("{0}: ({1}, {2})", new SimpleDateFormat("d-MMM-yyyy"), new DecimalFormat("0.00")));
    NumberAxis localNumberAxis2 = new NumberAxis("Volume");
    localNumberAxis2.setUpperMargin(1.0D);
    localXYPlot.setRangeAxis(1, localNumberAxis2);
    localXYPlot.setDataset(1, createVolumeDataset());
    localXYPlot.setRangeAxis(1, localNumberAxis2);
    localXYPlot.mapDatasetToRangeAxis(1, 1);
    XYBarRenderer localXYBarRenderer = new XYBarRenderer();
    localXYBarRenderer.setDrawBarOutline(false);
    localXYBarRenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator("{0}: ({1}, {2})", new SimpleDateFormat("d-MMM-yyyy"), new DecimalFormat("0,000.00")));
    localXYPlot.setRenderer(1, localXYBarRenderer);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    localXYBarRenderer.setShadowVisible(false);
    localXYBarRenderer.setBarPainter(new StandardXYBarPainter());
    return localJFreeChart;
  }

  private static OHLCDataset createPriceDataset()
  {
    OHLCSeries localOHLCSeries = new OHLCSeries("SUNW");
    localOHLCSeries.add(new Day(12, 4, 2007), 5.9D, 5.96D, 5.87D, 5.9D);
    localOHLCSeries.add(new Day(13, 4, 2007), 5.89D, 5.9D, 5.78D, 5.8D);
    localOHLCSeries.add(new Day(16, 4, 2007), 5.81D, 5.87D, 5.79D, 5.86D);
    localOHLCSeries.add(new Day(17, 4, 2007), 5.87D, 5.95D, 5.82D, 5.95D);
    localOHLCSeries.add(new Day(18, 4, 2007), 5.89D, 5.95D, 5.87D, 5.94D);
    localOHLCSeries.add(new Day(19, 4, 2007), 5.87D, 5.96D, 5.86D, 5.89D);
    localOHLCSeries.add(new Day(20, 4, 2007), 5.94D, 5.95D, 5.87D, 5.93D);
    localOHLCSeries.add(new Day(23, 4, 2007), 5.93D, 5.95D, 5.89D, 5.92D);
    localOHLCSeries.add(new Day(24, 4, 2007), 5.93D, 5.97D, 5.88D, 5.94D);
    localOHLCSeries.add(new Day(25, 4, 2007), 5.58D, 5.58D, 5.17D, 5.27D);
    localOHLCSeries.add(new Day(26, 4, 2007), 5.25D, 5.3D, 5.2D, 5.25D);
    localOHLCSeries.add(new Day(27, 4, 2007), 5.23D, 5.28D, 5.19D, 5.26D);
    localOHLCSeries.add(new Day(30, 4, 2007), 5.25D, 5.26D, 5.2D, 5.22D);
    localOHLCSeries.add(new Day(1, 5, 2007), 5.23D, 5.24D, 4.99D, 5.09D);
    localOHLCSeries.add(new Day(2, 5, 2007), 5.09D, 5.17D, 5.08D, 5.15D);
    localOHLCSeries.add(new Day(3, 5, 2007), 5.14D, 5.2D, 5.11D, 5.18D);
    localOHLCSeries.add(new Day(4, 5, 2007), 5.21D, 5.3D, 5.2D, 5.24D);
    localOHLCSeries.add(new Day(7, 5, 2007), 5.22D, 5.28D, 5.21D, 5.22D);
    localOHLCSeries.add(new Day(8, 5, 2007), 5.24D, 5.27D, 5.21D, 5.22D);
    localOHLCSeries.add(new Day(9, 5, 2007), 5.21D, 5.22D, 5.15D, 5.2D);
    localOHLCSeries.add(new Day(10, 5, 2007), 5.16D, 5.19D, 5.13D, 5.13D);
    localOHLCSeries.add(new Day(11, 5, 2007), 5.14D, 5.18D, 5.12D, 5.15D);
    localOHLCSeries.add(new Day(14, 5, 2007), 5.16D, 5.29D, 5.16D, 5.22D);
    localOHLCSeries.add(new Day(15, 5, 2007), 5.22D, 5.23D, 5.13D, 5.14D);
    localOHLCSeries.add(new Day(16, 5, 2007), 5.14D, 5.16D, 5.07D, 5.12D);
    localOHLCSeries.add(new Day(17, 5, 2007), 5.35D, 5.43D, 5.3D, 5.3D);
    localOHLCSeries.add(new Day(18, 5, 2007), 5.35D, 5.35D, 5.26D, 5.29D);
    localOHLCSeries.add(new Day(21, 5, 2007), 5.29D, 5.39D, 5.24D, 5.39D);
    localOHLCSeries.add(new Day(22, 5, 2007), 5.39D, 5.42D, 5.34D, 5.38D);
    localOHLCSeries.add(new Day(23, 5, 2007), 5.37D, 5.43D, 5.36D, 5.38D);
    localOHLCSeries.add(new Day(24, 5, 2007), 5.36D, 5.37D, 5.15D, 5.15D);
    localOHLCSeries.add(new Day(25, 5, 2007), 5.18D, 5.21D, 5.15D, 5.16D);
    localOHLCSeries.add(new Day(29, 5, 2007), 5.16D, 5.17D, 5.0D, 5.06D);
    localOHLCSeries.add(new Day(30, 5, 2007), 5.01D, 5.15D, 4.96D, 5.12D);
    localOHLCSeries.add(new Day(31, 5, 2007), 5.16D, 5.19D, 5.07D, 5.1D);
    localOHLCSeries.add(new Day(1, 6, 2007), 5.12D, 5.2D, 5.12D, 5.18D);
    localOHLCSeries.add(new Day(4, 6, 2007), 5.15D, 5.24D, 5.07D, 5.11D);
    localOHLCSeries.add(new Day(5, 6, 2007), 5.08D, 5.08D, 4.97D, 5.07D);
    localOHLCSeries.add(new Day(6, 6, 2007), 5.03D, 5.05D, 4.99D, 5.02D);
    localOHLCSeries.add(new Day(7, 6, 2007), 5.0D, 5.05D, 4.97D, 4.97D);
    localOHLCSeries.add(new Day(8, 6, 2007), 4.98D, 5.04D, 4.95D, 5.04D);
    localOHLCSeries.add(new Day(11, 6, 2007), 5.05D, 5.06D, 4.95D, 4.96D);
    localOHLCSeries.add(new Day(12, 6, 2007), 4.95D, 5.01D, 4.92D, 4.92D);
    localOHLCSeries.add(new Day(13, 6, 2007), 4.95D, 4.99D, 4.83D, 4.99D);
    localOHLCSeries.add(new Day(14, 6, 2007), 5.05D, 5.1D, 5.02D, 5.08D);
    localOHLCSeries.add(new Day(15, 6, 2007), 5.13D, 5.13D, 5.03D, 5.05D);
    localOHLCSeries.add(new Day(18, 6, 2007), 5.06D, 5.07D, 5.01D, 5.05D);
    localOHLCSeries.add(new Day(19, 6, 2007), 5.03D, 5.16D, 5.03D, 5.1D);
    localOHLCSeries.add(new Day(20, 6, 2007), 5.14D, 5.15D, 5.05D, 5.05D);
    localOHLCSeries.add(new Day(21, 6, 2007), 5.07D, 5.18D, 5.06D, 5.13D);
    localOHLCSeries.add(new Day(22, 6, 2007), 5.11D, 5.14D, 5.08D, 5.08D);
    localOHLCSeries.add(new Day(25, 6, 2007), 5.08D, 5.1D, 4.99D, 5.02D);
    localOHLCSeries.add(new Day(26, 6, 2007), 5.04D, 5.1D, 4.99D, 5.01D);
    localOHLCSeries.add(new Day(27, 6, 2007), 5.0D, 5.09D, 4.99D, 5.07D);
    localOHLCSeries.add(new Day(28, 6, 2007), 5.08D, 5.19D, 5.07D, 5.16D);
    localOHLCSeries.add(new Day(29, 6, 2007), 5.19D, 5.33D, 5.16D, 5.26D);
    localOHLCSeries.add(new Day(2, 7, 2007), 5.13D, 5.33D, 5.12D, 5.19D);
    localOHLCSeries.add(new Day(3, 7, 2007), 5.2D, 5.24D, 5.17D, 5.22D);
    localOHLCSeries.add(new Day(5, 7, 2007), 5.28D, 5.35D, 5.24D, 5.33D);
    localOHLCSeries.add(new Day(6, 7, 2007), 5.36D, 5.49D, 5.34D, 5.38D);
    localOHLCSeries.add(new Day(9, 7, 2007), 5.4D, 5.44D, 5.31D, 5.33D);
    localOHLCSeries.add(new Day(10, 7, 2007), 5.29D, 5.41D, 5.28D, 5.32D);
    localOHLCSeries.add(new Day(11, 7, 2007), 5.29D, 5.38D, 5.29D, 5.38D);
    localOHLCSeries.add(new Day(12, 7, 2007), 5.38D, 5.43D, 5.33D, 5.43D);
    localOHLCSeries.add(new Day(13, 7, 2007), 5.42D, 5.43D, 5.34D, 5.37D);
    localOHLCSeries.add(new Day(16, 7, 2007), 5.33D, 5.38D, 5.3D, 5.34D);
    OHLCSeriesCollection localOHLCSeriesCollection = new OHLCSeriesCollection();
    localOHLCSeriesCollection.addSeries(localOHLCSeries);
    return localOHLCSeriesCollection;
  }

  private static IntervalXYDataset createVolumeDataset()
  {
    TimeSeries localTimeSeries = new TimeSeries("Volume");
    localTimeSeries.add(new Day(12, 4, 2007), 49646800.0D);
    localTimeSeries.add(new Day(13, 4, 2007), 67319300.0D);
    localTimeSeries.add(new Day(16, 4, 2007), 56840200.0D);
    localTimeSeries.add(new Day(17, 4, 2007), 40752300.0D);
    localTimeSeries.add(new Day(18, 4, 2007), 42533800.0D);
    localTimeSeries.add(new Day(19, 4, 2007), 34232100.0D);
    localTimeSeries.add(new Day(20, 4, 2007), 32437600.0D);
    localTimeSeries.add(new Day(23, 4, 2007), 28755900.0D);
    localTimeSeries.add(new Day(24, 4, 2007), 74503300.0D);
    localTimeSeries.add(new Day(25, 4, 2007), 322214000.0D);
    localTimeSeries.add(new Day(26, 4, 2007), 96141700.0D);
    localTimeSeries.add(new Day(27, 4, 2007), 62326200.0D);
    localTimeSeries.add(new Day(30, 4, 2007), 52334400.0D);
    localTimeSeries.add(new Day(1, 5, 2007), 133096100.0D);
    localTimeSeries.add(new Day(2, 5, 2007), 93874500.0D);
    localTimeSeries.add(new Day(3, 5, 2007), 101011400.0D);
    localTimeSeries.add(new Day(4, 5, 2007), 56629700.0D);
    localTimeSeries.add(new Day(7, 5, 2007), 43302200.0D);
    localTimeSeries.add(new Day(8, 5, 2007), 51456500.0D);
    localTimeSeries.add(new Day(9, 5, 2007), 48230900.0D);
    localTimeSeries.add(new Day(10, 5, 2007), 65536000.0D);
    localTimeSeries.add(new Day(11, 5, 2007), 46469700.0D);
    localTimeSeries.add(new Day(14, 5, 2007), 118580000.0D);
    localTimeSeries.add(new Day(15, 5, 2007), 50774100.0D);
    localTimeSeries.add(new Day(16, 5, 2007), 44590200.0D);
    localTimeSeries.add(new Day(17, 5, 2007), 125482500.0D);
    localTimeSeries.add(new Day(18, 5, 2007), 49987500.0D);
    localTimeSeries.add(new Day(21, 5, 2007), 48387400.0D);
    localTimeSeries.add(new Day(22, 5, 2007), 67992600.0D);
    localTimeSeries.add(new Day(23, 5, 2007), 45629200.0D);
    localTimeSeries.add(new Day(24, 5, 2007), 123288600.0D);
    localTimeSeries.add(new Day(25, 5, 2007), 47345300.0D);
    localTimeSeries.add(new Day(29, 5, 2007), 90454000.0D);
    localTimeSeries.add(new Day(30, 5, 2007), 73049200.0D);
    localTimeSeries.add(new Day(31, 5, 2007), 60395400.0D);
    localTimeSeries.add(new Day(1, 6, 2007), 48792300.0D);
    localTimeSeries.add(new Day(4, 6, 2007), 109972800.0D);
    localTimeSeries.add(new Day(5, 6, 2007), 198201200.0D);
    localTimeSeries.add(new Day(6, 6, 2007), 121415700.0D);
    localTimeSeries.add(new Day(7, 6, 2007), 56637400.0D);
    localTimeSeries.add(new Day(8, 6, 2007), 64116600.0D);
    localTimeSeries.add(new Day(11, 6, 2007), 60274800.0D);
    localTimeSeries.add(new Day(12, 6, 2007), 93451300.0D);
    localTimeSeries.add(new Day(13, 6, 2007), 103309000.0D);
    localTimeSeries.add(new Day(14, 6, 2007), 103135400.0D);
    localTimeSeries.add(new Day(15, 6, 2007), 104415900.0D);
    localTimeSeries.add(new Day(18, 6, 2007), 51506900.0D);
    localTimeSeries.add(new Day(19, 6, 2007), 74592100.0D);
    localTimeSeries.add(new Day(20, 6, 2007), 69027600.0D);
    localTimeSeries.add(new Day(21, 6, 2007), 97212500.0D);
    localTimeSeries.add(new Day(22, 6, 2007), 51612000.0D);
    localTimeSeries.add(new Day(25, 6, 2007), 63845400.0D);
    localTimeSeries.add(new Day(26, 6, 2007), 84818400.0D);
    localTimeSeries.add(new Day(27, 6, 2007), 73512900.0D);
    localTimeSeries.add(new Day(28, 6, 2007), 75275100.0D);
    localTimeSeries.add(new Day(29, 6, 2007), 104203900.0D);
    localTimeSeries.add(new Day(2, 7, 2007), 66540400.0D);
    localTimeSeries.add(new Day(3, 7, 2007), 28520800.0D);
    localTimeSeries.add(new Day(5, 7, 2007), 47176300.0D);
    localTimeSeries.add(new Day(6, 7, 2007), 70084800.0D);
    localTimeSeries.add(new Day(9, 7, 2007), 91630800.0D);
    localTimeSeries.add(new Day(10, 7, 2007), 114071700.0D);
    localTimeSeries.add(new Day(11, 7, 2007), 34217900.0D);
    localTimeSeries.add(new Day(12, 7, 2007), 30298000.0D);
    localTimeSeries.add(new Day(13, 7, 2007), 40423500.0D);
    localTimeSeries.add(new Day(16, 7, 2007), 39238000.0D);
    return new TimeSeriesCollection(localTimeSeries);
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart();
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    PriceVolumeDemo2 localPriceVolumeDemo2 = new PriceVolumeDemo2("JFreeChart: PriceVolumeDemo2.java");
    localPriceVolumeDemo2.pack();
    RefineryUtilities.centerFrameOnScreen(localPriceVolumeDemo2);
    localPriceVolumeDemo2.setVisible(true);
  }
}