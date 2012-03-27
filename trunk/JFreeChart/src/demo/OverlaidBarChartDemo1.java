package demo;

import java.awt.Dimension;
import java.io.PrintStream;
import javax.swing.JPanel;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class OverlaidBarChartDemo1 extends ApplicationFrame
{
  public OverlaidBarChartDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  public static JFreeChart createChart()
  {
    DefaultCategoryDataset localDefaultCategoryDataset1 = new DefaultCategoryDataset();
    localDefaultCategoryDataset1.addValue(1.0D, "S1", "Category 1");
    localDefaultCategoryDataset1.addValue(4.0D, "S1", "Category 2");
    localDefaultCategoryDataset1.addValue(3.0D, "S1", "Category 3");
    localDefaultCategoryDataset1.addValue(5.0D, "S1", "Category 4");
    localDefaultCategoryDataset1.addValue(5.0D, "S1", "Category 5");
    localDefaultCategoryDataset1.addValue(7.0D, "S1", "Category 6");
    localDefaultCategoryDataset1.addValue(7.0D, "S1", "Category 7");
    localDefaultCategoryDataset1.addValue(8.0D, "S1", "Category 8");
    localDefaultCategoryDataset1.addValue(5.0D, "S2", "Category 1");
    localDefaultCategoryDataset1.addValue(7.0D, "S2", "Category 2");
    localDefaultCategoryDataset1.addValue(6.0D, "S2", "Category 3");
    localDefaultCategoryDataset1.addValue(8.0D, "S2", "Category 4");
    localDefaultCategoryDataset1.addValue(4.0D, "S2", "Category 5");
    localDefaultCategoryDataset1.addValue(4.0D, "S2", "Category 6");
    localDefaultCategoryDataset1.addValue(2.0D, "S2", "Category 7");
    localDefaultCategoryDataset1.addValue(1.0D, "S2", "Category 8");
    StandardCategoryItemLabelGenerator localStandardCategoryItemLabelGenerator = new StandardCategoryItemLabelGenerator();
    BarRenderer localBarRenderer = new BarRenderer();
    localBarRenderer.setBaseItemLabelGenerator(localStandardCategoryItemLabelGenerator);
    localBarRenderer.setBaseItemLabelsVisible(true);
    CategoryPlot localCategoryPlot = new CategoryPlot();
    localCategoryPlot.setDataset(localDefaultCategoryDataset1);
    localCategoryPlot.setRenderer(localBarRenderer);
    localCategoryPlot.setDomainAxis(new CategoryAxis("Category"));
    localCategoryPlot.setRangeAxis(new NumberAxis("Value"));
    localCategoryPlot.setOrientation(PlotOrientation.VERTICAL);
    localCategoryPlot.setRangeGridlinesVisible(true);
    localCategoryPlot.setDomainGridlinesVisible(true);
    DefaultCategoryDataset localDefaultCategoryDataset2 = new DefaultCategoryDataset();
    localDefaultCategoryDataset2.addValue(9.0D, "T1", "Category 1");
    localDefaultCategoryDataset2.addValue(7.0D, "T1", "Category 2");
    localDefaultCategoryDataset2.addValue(2.0D, "T1", "Category 3");
    localDefaultCategoryDataset2.addValue(6.0D, "T1", "Category 4");
    localDefaultCategoryDataset2.addValue(6.0D, "T1", "Category 5");
    localDefaultCategoryDataset2.addValue(9.0D, "T1", "Category 6");
    localDefaultCategoryDataset2.addValue(5.0D, "T1", "Category 7");
    localDefaultCategoryDataset2.addValue(4.0D, "T1", "Category 8");
    LineAndShapeRenderer localLineAndShapeRenderer1 = new LineAndShapeRenderer();
    localCategoryPlot.setDataset(1, localDefaultCategoryDataset2);
    localCategoryPlot.setRenderer(1, localLineAndShapeRenderer1);
    NumberAxis localNumberAxis = new NumberAxis("Axis 2");
    localCategoryPlot.setRangeAxis(1, localNumberAxis);
    DefaultCategoryDataset localDefaultCategoryDataset3 = new DefaultCategoryDataset();
    localDefaultCategoryDataset3.addValue(94.0D, "R1", "Category 1");
    localDefaultCategoryDataset3.addValue(75.0D, "R1", "Category 2");
    localDefaultCategoryDataset3.addValue(22.0D, "R1", "Category 3");
    localDefaultCategoryDataset3.addValue(74.0D, "R1", "Category 4");
    localDefaultCategoryDataset3.addValue(83.0D, "R1", "Category 5");
    localDefaultCategoryDataset3.addValue(9.0D, "R1", "Category 6");
    localDefaultCategoryDataset3.addValue(23.0D, "R1", "Category 7");
    localDefaultCategoryDataset3.addValue(98.0D, "R1", "Category 8");
    localCategoryPlot.setDataset(2, localDefaultCategoryDataset3);
    LineAndShapeRenderer localLineAndShapeRenderer2 = new LineAndShapeRenderer();
    localCategoryPlot.setRenderer(2, localLineAndShapeRenderer2);
    localCategoryPlot.mapDatasetToRangeAxis(2, 1);
    localCategoryPlot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
    localCategoryPlot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
    JFreeChart localJFreeChart = new JFreeChart(localCategoryPlot);
    localJFreeChart.setTitle("Overlaid Bar Chart");
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart();
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.addChartMouseListener(new ChartMouseListener()
    {
      public void chartMouseClicked(ChartMouseEvent paramChartMouseEvent)
      {
        System.out.println(paramChartMouseEvent.getEntity());
      }

      public void chartMouseMoved(ChartMouseEvent paramChartMouseEvent)
      {
      }
    });
    return localChartPanel;
  }

  public static void main(String[] paramArrayOfString)
  {
    OverlaidBarChartDemo1 localOverlaidBarChartDemo1 = new OverlaidBarChartDemo1("JFreeChart: OverlaidBarChartDemo1.java");
    localOverlaidBarChartDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localOverlaidBarChartDemo1);
    localOverlaidBarChartDemo1.setVisible(true);
  }
}