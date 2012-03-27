package demo;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.ScatterRenderer;
import org.jfree.data.statistics.DefaultMultiValueCategoryDataset;
import org.jfree.data.statistics.MultiValueCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

public class ScatterRendererDemo1 extends ApplicationFrame
{
  public ScatterRendererDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static List listOfValues(double[] paramArrayOfDouble)
  {
    ArrayList localArrayList = new ArrayList();
    for (int i = 0; i < paramArrayOfDouble.length; ++i)
      localArrayList.add(new Double(paramArrayOfDouble[i]));
    return localArrayList;
  }

  private static MultiValueCategoryDataset createDataset()
  {
    DefaultMultiValueCategoryDataset localDefaultMultiValueCategoryDataset = new DefaultMultiValueCategoryDataset();
    localDefaultMultiValueCategoryDataset.add(listOfValues(new double[] { 1.0D, 2.0D, 3.0D }), "Series 1", "C1");
    localDefaultMultiValueCategoryDataset.add(listOfValues(new double[] { 1.2D, 2.2D, 3.2D }), "Series 1", "C2");
    localDefaultMultiValueCategoryDataset.add(listOfValues(new double[] { 1.4D, 2.4D, 3.4D }), "Series 1", "C3");
    localDefaultMultiValueCategoryDataset.add(listOfValues(new double[] { 1.0D, 3.0D }), "Series 2", "C1");
    localDefaultMultiValueCategoryDataset.add(listOfValues(new double[] { 1.2D, 3.2D }), "Series 2", "C2");
    localDefaultMultiValueCategoryDataset.add(listOfValues(new double[] { 1.4D, 3.6D }), "Series 2", "C3");
    return localDefaultMultiValueCategoryDataset;
  }

  private static JFreeChart createChart(MultiValueCategoryDataset paramMultiValueCategoryDataset)
  {
    CategoryPlot localCategoryPlot = new CategoryPlot(paramMultiValueCategoryDataset, new CategoryAxis("Category"), new NumberAxis("Value"), new ScatterRenderer());
    localCategoryPlot.setBackgroundPaint(Color.lightGray);
    localCategoryPlot.setDomainGridlinePaint(Color.white);
    localCategoryPlot.setRangeGridlinePaint(Color.white);
    localCategoryPlot.setAxisOffset(new RectangleInsets(4.0D, 4.0D, 4.0D, 4.0D));
    JFreeChart localJFreeChart = new JFreeChart("ScatterRendererDemo1", localCategoryPlot);
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
    ScatterRendererDemo1 localScatterRendererDemo1 = new ScatterRendererDemo1("JFreeChart: ScatterRendererDemo1.java");
    localScatterRendererDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localScatterRendererDemo1);
    localScatterRendererDemo1.setVisible(true);
  }
}