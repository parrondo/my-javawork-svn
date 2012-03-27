package demo;

import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeTableXYDataset;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class StackedXYAreaChartDemo3 extends ApplicationFrame
{
  public StackedXYAreaChartDemo3(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static TableXYDataset createDataset()
  {
    TimeTableXYDataset localTimeTableXYDataset = new TimeTableXYDataset();
    localTimeTableXYDataset.add(new Day(14, 2, 2007), 87.0D, "Series 1");
    localTimeTableXYDataset.add(new Day(15, 2, 2007), 67.0D, "Series 1");
    localTimeTableXYDataset.add(new Day(16, 2, 2007), 78.0D, "Series 1");
    localTimeTableXYDataset.add(new Day(17, 2, 2007), 55.0D, "Series 1");
    localTimeTableXYDataset.add(new Day(18, 2, 2007), 58.0D, "Series 1");
    localTimeTableXYDataset.add(new Day(19, 2, 2007), 60.0D, "Series 1");
    localTimeTableXYDataset.add(new Day(14, 2, 2007), 45.0D, "Series 2");
    localTimeTableXYDataset.add(new Day(15, 2, 2007), 67.0D, "Series 2");
    localTimeTableXYDataset.add(new Day(16, 2, 2007), 61.0D, "Series 2");
    localTimeTableXYDataset.add(new Day(17, 2, 2007), 58.0D, "Series 2");
    localTimeTableXYDataset.add(new Day(18, 2, 2007), 73.0D, "Series 2");
    localTimeTableXYDataset.add(new Day(19, 2, 2007), 64.0D, "Series 2");
    localTimeTableXYDataset.add(new Day(14, 2, 2007), 32.0D, "Series 3");
    localTimeTableXYDataset.add(new Day(15, 2, 2007), 38.0D, "Series 3");
    localTimeTableXYDataset.add(new Day(16, 2, 2007), 43.0D, "Series 3");
    localTimeTableXYDataset.add(new Day(17, 2, 2007), 12.0D, "Series 3");
    localTimeTableXYDataset.add(new Day(18, 2, 2007), 19.0D, "Series 3");
    localTimeTableXYDataset.add(new Day(19, 2, 2007), 26.0D, "Series 3");
    return localTimeTableXYDataset;
  }

  private static JFreeChart createChart(TableXYDataset paramTableXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createStackedXYAreaChart("Stacked XY Area Chart Demo 3", "X Value", "Y Value", paramTableXYDataset, PlotOrientation.VERTICAL, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    DateAxis localDateAxis = new DateAxis("Date");
    localXYPlot.setDomainAxis(localDateAxis);
    localDateAxis.setLowerMargin(0.0D);
    localDateAxis.setUpperMargin(0.0D);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    StackedXYAreaChartDemo3 localStackedXYAreaChartDemo3 = new StackedXYAreaChartDemo3("Stacked XY Area Chart Demo 3");
    localStackedXYAreaChartDemo3.pack();
    RefineryUtilities.centerFrameOnScreen(localStackedXYAreaChartDemo3);
    localStackedXYAreaChartDemo3.setVisible(true);
  }
}