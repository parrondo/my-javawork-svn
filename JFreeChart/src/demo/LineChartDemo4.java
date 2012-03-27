package demo;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class LineChartDemo4 extends ApplicationFrame
{
  public LineChartDemo4(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createXYLineChart("Line Chart Demo 4", "X", "Y", paramXYDataset, PlotOrientation.VERTICAL, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setDomainZeroBaselineVisible(true);
    localXYPlot.setRangeZeroBaselineVisible(true);
    localXYPlot.getDomainAxis().setLowerMargin(0.0D);
    localXYPlot.getDomainAxis().setUpperMargin(0.0D);
    localXYPlot.setDomainPannable(true);
    localXYPlot.setRangePannable(true);
    XYLineAndShapeRenderer localXYLineAndShapeRenderer = (XYLineAndShapeRenderer)localXYPlot.getRenderer();
    localXYLineAndShapeRenderer.setLegendLine(new Rectangle2D.Double(-4.0D, -3.0D, 8.0D, 6.0D));
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(new SampleXYDataset());
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setMouseWheelEnabled(true);
    return localChartPanel;
  }

  public static void main(String[] paramArrayOfString)
  {
    LineChartDemo4 localLineChartDemo4 = new LineChartDemo4("JFreeChart: LineChartDemo4.java");
    localLineChartDemo4.pack();
    RefineryUtilities.centerFrameOnScreen(localLineChartDemo4);
    localLineChartDemo4.setVisible(true);
  }
}