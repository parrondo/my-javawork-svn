package demo;

import java.awt.Dimension;
import java.awt.Paint;
import java.text.NumberFormat;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;

public class ItemLabelDemo5 extends ApplicationFrame
{
  public ItemLabelDemo5(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  public static CategoryDataset createDataset()
  {
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(52.829999999999998D, "Germany", "Western EU");
    localDefaultCategoryDataset.addValue(20.829999999999998D, "France", "Western EU");
    localDefaultCategoryDataset.addValue(10.83D, "Great Britain", "Western EU");
    localDefaultCategoryDataset.addValue(7.33D, "Netherlands", "Western EU");
    localDefaultCategoryDataset.addValue(4.66D, "Belgium", "Western EU");
    localDefaultCategoryDataset.addValue(57.140000000000001D, "Spain", "Southern EU");
    localDefaultCategoryDataset.addValue(14.279999999999999D, "Greece", "Southern EU");
    localDefaultCategoryDataset.addValue(14.279999999999999D, "Italy", "Southern EU");
    localDefaultCategoryDataset.addValue(14.279999999999999D, "Portugal", "Southern EU");
    localDefaultCategoryDataset.addValue(100.0D, "Czech Republic", "Eastern EU");
    localDefaultCategoryDataset.addValue(66.659999999999997D, "Denmark", "Scandinavia");
    localDefaultCategoryDataset.addValue(33.329999999999998D, "Finland", "Scandinavia");
    localDefaultCategoryDataset.addValue(0.0D, "", "Africa");
    localDefaultCategoryDataset.addValue(100.0D, "Israel", "Asia");
    return localDefaultCategoryDataset;
  }

  private static JFreeChart createChart(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createStackedBarChart("Item Label Demo 5", null, null, paramCategoryDataset, PlotOrientation.VERTICAL, false, true, false);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    MyStackedBarRenderer localMyStackedBarRenderer = new MyStackedBarRenderer();
    localCategoryPlot.setRenderer(localMyStackedBarRenderer);
    ItemLabelPosition localItemLabelPosition = new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER, TextAnchor.CENTER, 0.0D);
    localMyStackedBarRenderer.setPositiveItemLabelPositionFallback(localItemLabelPosition);
    localMyStackedBarRenderer.setNegativeItemLabelPositionFallback(localItemLabelPosition);
    StandardCategoryItemLabelGenerator localStandardCategoryItemLabelGenerator = new StandardCategoryItemLabelGenerator("{0}", NumberFormat.getInstance());
    localMyStackedBarRenderer.setBaseItemLabelGenerator(localStandardCategoryItemLabelGenerator);
    localMyStackedBarRenderer.setBaseItemLabelsVisible(true);
    NumberAxis localNumberAxis = (NumberAxis)localCategoryPlot.getRangeAxis();
    localNumberAxis.setUpperBound(100.0D);
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
    ItemLabelDemo5 localItemLabelDemo5 = new ItemLabelDemo5("JFreeChart: ItemLabelDemo5.java");
    localItemLabelDemo5.pack();
    RefineryUtilities.centerFrameOnScreen(localItemLabelDemo5);
    localItemLabelDemo5.setVisible(true);
  }

  private static class MyStackedBarRenderer extends StackedBarRenderer
  {
    int oldColumn;
    int count;
    Paint[] list;

    private MyStackedBarRenderer()
    {
      this.oldColumn = -99;
      this.count = 0;
      this.list = DefaultDrawingSupplier.DEFAULT_PAINT_SEQUENCE;
    }

    public Paint getItemPaint(int paramInt1, int paramInt2)
    {
      if (this.oldColumn != paramInt2)
      {
        this.count = 0;
        this.oldColumn = paramInt2;
      }
      else
      {
        this.count += 1;
      }
      return this.list[this.count];
    }
  }
}