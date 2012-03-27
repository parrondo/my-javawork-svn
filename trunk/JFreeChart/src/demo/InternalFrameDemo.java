package demo;

import java.awt.Container;
import java.awt.Dimension;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Minute;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class InternalFrameDemo extends ApplicationFrame
{
  public InternalFrameDemo(String paramString)
  {
    super(paramString);
    JDesktopPane localJDesktopPane = new JDesktopPane();
    localJDesktopPane.setPreferredSize(new Dimension(600, 400));
    JInternalFrame localJInternalFrame1 = createFrame1();
    localJDesktopPane.add(localJInternalFrame1);
    localJInternalFrame1.pack();
    localJInternalFrame1.setVisible(true);
    JInternalFrame localJInternalFrame2 = createFrame2();
    localJDesktopPane.add(localJInternalFrame2);
    localJInternalFrame2.pack();
    localJInternalFrame2.setLocation(100, 200);
    localJInternalFrame2.setVisible(true);
    getContentPane().add(localJDesktopPane);
  }

  private JInternalFrame createFrame1()
  {
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(34.0D, "Series 1", "Category 1");
    localDefaultCategoryDataset.addValue(23.0D, "Series 1", "Category 2");
    localDefaultCategoryDataset.addValue(54.0D, "Series 1", "Category 3");
    JFreeChart localJFreeChart = ChartFactory.createBarChart("Bar Chart", "Category", "Series", localDefaultCategoryDataset, PlotOrientation.VERTICAL, true, true, false);
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setPreferredSize(new Dimension(200, 100));
    JInternalFrame localJInternalFrame = new JInternalFrame("Frame 1", true);
    localJInternalFrame.getContentPane().add(localChartPanel);
    return localJInternalFrame;
  }

  private JInternalFrame createFrame2()
  {
    XYDataset localXYDataset = createDataset("Series 1", 100.0D, new Minute(), 200);
    JFreeChart localJFreeChart = ChartFactory.createTimeSeriesChart("Time Series Chart", "Time of Day", "Value", localXYDataset, true, true, false);
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setPreferredSize(new Dimension(200, 100));
    JInternalFrame localJInternalFrame = new JInternalFrame("Frame 2", true);
    localJInternalFrame.getContentPane().add(localChartPanel);
    return localJInternalFrame;
  }

  private XYDataset createDataset(String paramString, double paramDouble, RegularTimePeriod paramRegularTimePeriod, int paramInt)
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

  public static void main(String[] paramArrayOfString)
  {
    InternalFrameDemo localInternalFrameDemo = new InternalFrameDemo("Internal Frame Demo");
    localInternalFrameDemo.pack();
    RefineryUtilities.centerFrameOnScreen(localInternalFrameDemo);
    localInternalFrameDemo.setVisible(true);
  }
}