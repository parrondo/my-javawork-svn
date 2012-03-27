package demo;

import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class LineChartDemo3 extends ApplicationFrame
{
  public LineChartDemo3(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setMouseWheelEnabled(true);
    return localChartPanel;
  }

  private static XYDataset createDataset()
  {
    XYSeriesCollection localXYSeriesCollection = new XYSeriesCollection();
    for (int i = 0; i < 10; ++i)
    {
      XYSeries localXYSeries = new XYSeries("S" + i);
      for (int j = 0; j < 10; ++j)
        localXYSeries.add(j, Math.random() * 100.0D);
      localXYSeriesCollection.addSeries(localXYSeries);
    }
    return localXYSeriesCollection;
  }

  private static JFreeChart createChart(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createXYLineChart("Line Chart Demo 3", "X", "Y", paramXYDataset, PlotOrientation.VERTICAL, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setDomainPannable(true);
    localXYPlot.setRangePannable(true);
    localXYPlot.setDomainZeroBaselineVisible(true);
    localXYPlot.setRangeZeroBaselineVisible(true);
    XYLineAndShapeRenderer localXYLineAndShapeRenderer = (XYLineAndShapeRenderer)localXYPlot.getRenderer();
    localXYLineAndShapeRenderer.setBaseShapesVisible(true);
    localXYLineAndShapeRenderer.setBaseShapesFilled(true);
    localXYLineAndShapeRenderer.setDrawOutlines(true);
    NumberAxis localNumberAxis = (NumberAxis)localXYPlot.getRangeAxis();
    localNumberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    return localJFreeChart;
  }

  public static void main(String[] paramArrayOfString)
  {
    LineChartDemo3 localLineChartDemo3 = new LineChartDemo3("JFreeChart: LineChartDemo3.java");
    localLineChartDemo3.pack();
    RefineryUtilities.centerFrameOnScreen(localLineChartDemo3);
    localLineChartDemo3.setVisible(true);
  }
}