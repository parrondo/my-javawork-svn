package demo;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class XYStepRendererDemo2 extends ApplicationFrame
{
  public XYStepRendererDemo2(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 300));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createXYLineChart("XYStepRendererDemo2", "X", "Y", paramXYDataset, PlotOrientation.VERTICAL, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setDomainPannable(true);
    localXYPlot.setRangePannable(true);
    ValueAxis localValueAxis = localXYPlot.getRangeAxis();
    localValueAxis.setUpperMargin(0.15D);
    XYStepRenderer localXYStepRenderer = new XYStepRenderer();
    localXYStepRenderer.setSeriesStroke(0, new BasicStroke(2.0F));
    localXYStepRenderer.setSeriesStroke(1, new BasicStroke(2.0F));
    localXYStepRenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
    localXYStepRenderer.setDefaultEntityRadius(6);
    localXYStepRenderer.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
    localXYStepRenderer.setBaseItemLabelsVisible(true);
    localXYStepRenderer.setBaseItemLabelFont(new Font("Dialog", 1, 14));
    localXYPlot.setRenderer(localXYStepRenderer);
    return localJFreeChart;
  }

  private static XYDataset createDataset()
  {
    XYSeries localXYSeries1 = new XYSeries("Series 1");
    localXYSeries1.add(1.0D, 3.0D);
    localXYSeries1.add(2.0D, 4.0D);
    localXYSeries1.add(3.0D, 2.0D);
    localXYSeries1.add(6.0D, 3.0D);
    XYSeries localXYSeries2 = new XYSeries("Series 2");
    localXYSeries2.add(1.0D, 7.0D);
    localXYSeries2.add(2.0D, 6.0D);
    localXYSeries2.add(3.0D, 9.0D);
    localXYSeries2.add(4.0D, 5.0D);
    localXYSeries2.add(6.0D, 4.0D);
    XYSeriesCollection localXYSeriesCollection = new XYSeriesCollection();
    localXYSeriesCollection.addSeries(localXYSeries1);
    localXYSeriesCollection.addSeries(localXYSeries2);
    return localXYSeriesCollection;
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
    XYStepRendererDemo2 localXYStepRendererDemo2 = new XYStepRendererDemo2("JFreeChart: XYStepRendererDemo2.java");
    localXYStepRendererDemo2.pack();
    RefineryUtilities.centerFrameOnScreen(localXYStepRendererDemo2);
    localXYStepRendererDemo2.setVisible(true);
  }
}