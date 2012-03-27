package demo;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.BubbleXYItemLabelGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardXYZToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBubbleRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYZDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;

public class BubbleChartDemo2 extends ApplicationFrame
{
  public BubbleChartDemo2(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart(XYZDataset paramXYZDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createBubbleChart("Bubble Chart Demo 2", "X", "Y", paramXYZDataset, PlotOrientation.VERTICAL, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setRenderer(new XYBubbleRenderer(0));
    localXYPlot.setForegroundAlpha(0.65F);
    localXYPlot.setDomainPannable(true);
    localXYPlot.setRangePannable(true);
    XYItemRenderer localXYItemRenderer = localXYPlot.getRenderer();
    localXYItemRenderer.setSeriesPaint(0, Color.blue);
    localXYItemRenderer.setBaseItemLabelGenerator(new BubbleXYItemLabelGenerator());
    localXYItemRenderer.setBaseToolTipGenerator(new StandardXYZToolTipGenerator());
    localXYItemRenderer.setBaseItemLabelsVisible(true);
    localXYItemRenderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER));
    NumberAxis localNumberAxis1 = (NumberAxis)localXYPlot.getDomainAxis();
    localNumberAxis1.setRange(0.0D, 10.0D);
    NumberAxis localNumberAxis2 = (NumberAxis)localXYPlot.getRangeAxis();
    localNumberAxis2.setRange(0.0D, 10.0D);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(new SampleXYZDataset2());
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setMouseWheelEnabled(true);
    return localChartPanel;
  }

  public static void main(String[] paramArrayOfString)
  {
    BubbleChartDemo2 localBubbleChartDemo2 = new BubbleChartDemo2("JFreeChart: BubbleChartDemo2.java");
    localBubbleChartDemo2.pack();
    RefineryUtilities.centerFrameOnScreen(localBubbleChartDemo2);
    localBubbleChartDemo2.setVisible(true);
  }
}