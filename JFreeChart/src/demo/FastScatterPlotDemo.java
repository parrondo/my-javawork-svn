package demo;

import java.awt.Dimension;
import java.awt.RenderingHints;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.FastScatterPlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class FastScatterPlotDemo extends ApplicationFrame
{
  private static final int COUNT = 100000;
  private float[][] data = new float[2][100000];

  public FastScatterPlotDemo(String paramString)
  {
    super(paramString);
    populateData();
    NumberAxis localNumberAxis1 = new NumberAxis("X");
    localNumberAxis1.setAutoRangeIncludesZero(false);
    NumberAxis localNumberAxis2 = new NumberAxis("Y");
    localNumberAxis2.setAutoRangeIncludesZero(false);
    FastScatterPlot localFastScatterPlot = new FastScatterPlot(this.data, localNumberAxis1, localNumberAxis2);
    localFastScatterPlot.setDomainPannable(true);
    localFastScatterPlot.setRangePannable(true);
    JFreeChart localJFreeChart = new JFreeChart("Fast Scatter Plot", localFastScatterPlot);
    localJFreeChart.addSubtitle(new TextTitle("This chart contains 100000 data points."));
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    localJFreeChart.getRenderingHints().put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart, true);
    localChartPanel.setPreferredSize(new Dimension(500, 270));
    localChartPanel.setDomainZoomable(true);
    localChartPanel.setRangeZoomable(true);
    localChartPanel.setMinimumDrawHeight(10);
    localChartPanel.setMaximumDrawHeight(2000);
    localChartPanel.setMinimumDrawWidth(20);
    localChartPanel.setMaximumDrawWidth(2000);
    localChartPanel.setMouseWheelEnabled(true);
    setContentPane(localChartPanel);
  }

  private void populateData()
  {
    for (int i = 0; i < this.data[0].length; ++i)
    {
      float f = i + 100000.0F;
      this.data[0][i] = f;
      this.data[1][i] = (100000.0F + (float)Math.random() * 100000.0F);
    }
  }

  public static void main(String[] paramArrayOfString)
  {
    FastScatterPlotDemo localFastScatterPlotDemo = new FastScatterPlotDemo("JFreeChart: FastScatterPlotDemo.java");
    localFastScatterPlotDemo.pack();
    RefineryUtilities.centerFrameOnScreen(localFastScatterPlotDemo);
    localFastScatterPlotDemo.setVisible(true);
  }
}