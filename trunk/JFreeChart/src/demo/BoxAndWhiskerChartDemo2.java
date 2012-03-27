package demo;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBoxAndWhiskerRenderer;
import org.jfree.data.statistics.BoxAndWhiskerCalculator;
import org.jfree.data.statistics.BoxAndWhiskerXYDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerXYDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class BoxAndWhiskerChartDemo2 extends ApplicationFrame
{
  public BoxAndWhiskerChartDemo2(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static BoxAndWhiskerXYDataset createDataset()
  {
    DefaultBoxAndWhiskerXYDataset localDefaultBoxAndWhiskerXYDataset = new DefaultBoxAndWhiskerXYDataset("Series Name");
    Object localObject = new Day();
    for (int i = 0; i < 10; ++i)
    {
      List localList = createValueList(0.0D, 20.0D, 20);
      localDefaultBoxAndWhiskerXYDataset.add(((RegularTimePeriod)localObject).getStart(), BoxAndWhiskerCalculator.calculateBoxAndWhiskerStatistics(localList));
      localObject = ((RegularTimePeriod)localObject).next();
    }
    return ((BoxAndWhiskerXYDataset)localDefaultBoxAndWhiskerXYDataset);
  }

  private static List createValueList(double paramDouble1, double paramDouble2, int paramInt)
  {
    ArrayList localArrayList = new ArrayList();
    for (int i = 0; i < paramInt; ++i)
    {
      double d = paramDouble1 + Math.random() * (paramDouble2 - paramDouble1);
      localArrayList.add(new Double(d));
    }
    return localArrayList;
  }

  private static JFreeChart createChart(BoxAndWhiskerXYDataset paramBoxAndWhiskerXYDataset)
  {
    DateAxis localDateAxis = new DateAxis("Day");
    NumberAxis localNumberAxis = new NumberAxis("Value");
    XYBoxAndWhiskerRenderer localXYBoxAndWhiskerRenderer = new XYBoxAndWhiskerRenderer();
    XYPlot localXYPlot = new XYPlot(paramBoxAndWhiskerXYDataset, localDateAxis, localNumberAxis, localXYBoxAndWhiskerRenderer);
    JFreeChart localJFreeChart = new JFreeChart("Box-and-Whisker Chart Demo 2", localXYPlot);
    localJFreeChart.setBackgroundPaint(Color.white);
    localXYPlot.setBackgroundPaint(Color.lightGray);
    localXYPlot.setDomainGridlinePaint(Color.white);
    localXYPlot.setDomainGridlinesVisible(true);
    localXYPlot.setRangeGridlinePaint(Color.white);
    localXYPlot.setDomainPannable(true);
    localXYPlot.setRangePannable(true);
    localNumberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
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
    BoxAndWhiskerChartDemo2 localBoxAndWhiskerChartDemo2 = new BoxAndWhiskerChartDemo2("JFreeChart: BoxAndWhiskerChartDemo2.java");
    localBoxAndWhiskerChartDemo2.pack();
    RefineryUtilities.centerFrameOnScreen(localBoxAndWhiskerChartDemo2);
    localBoxAndWhiskerChartDemo2.setVisible(true);
  }
}