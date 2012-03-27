package demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Paint;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class MouseOverDemo1 extends ApplicationFrame
{
  public MouseOverDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static CategoryDataset createDataset()
  {
    String str1 = "First";
    String str2 = "Second";
    String str3 = "Third";
    String str4 = "Category 1";
    String str5 = "Category 2";
    String str6 = "Category 3";
    String str7 = "Category 4";
    String str8 = "Category 5";
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(1.0D, str1, str4);
    localDefaultCategoryDataset.addValue(4.0D, str1, str5);
    localDefaultCategoryDataset.addValue(3.0D, str1, str6);
    localDefaultCategoryDataset.addValue(5.0D, str1, str7);
    localDefaultCategoryDataset.addValue(5.0D, str1, str8);
    localDefaultCategoryDataset.addValue(5.0D, str2, str4);
    localDefaultCategoryDataset.addValue(7.0D, str2, str5);
    localDefaultCategoryDataset.addValue(6.0D, str2, str6);
    localDefaultCategoryDataset.addValue(8.0D, str2, str7);
    localDefaultCategoryDataset.addValue(4.0D, str2, str8);
    localDefaultCategoryDataset.addValue(4.0D, str3, str4);
    localDefaultCategoryDataset.addValue(3.0D, str3, str5);
    localDefaultCategoryDataset.addValue(2.0D, str3, str6);
    localDefaultCategoryDataset.addValue(3.0D, str3, str7);
    localDefaultCategoryDataset.addValue(6.0D, str3, str8);
    return localDefaultCategoryDataset;
  }

  private static JFreeChart createChart(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createBarChart("Mouseover Demo 1", "Category", "Value", paramCategoryDataset, PlotOrientation.VERTICAL, true, true, false);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    localCategoryPlot.setDomainGridlinesVisible(true);
    NumberAxis localNumberAxis = (NumberAxis)localCategoryPlot.getRangeAxis();
    localNumberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    MyBarRenderer localMyBarRenderer = new MyBarRenderer();
    localMyBarRenderer.setDrawBarOutline(true);
    localCategoryPlot.setRenderer(localMyBarRenderer);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    GradientPaint localGradientPaint1 = new GradientPaint(0.0F, 0.0F, Color.blue, 0.0F, 0.0F, new Color(0, 0, 64));
    GradientPaint localGradientPaint2 = new GradientPaint(0.0F, 0.0F, Color.green, 0.0F, 0.0F, new Color(0, 64, 0));
    GradientPaint localGradientPaint3 = new GradientPaint(0.0F, 0.0F, Color.red, 0.0F, 0.0F, new Color(64, 0, 0));
    localMyBarRenderer.setSeriesPaint(0, localGradientPaint1);
    localMyBarRenderer.setSeriesPaint(1, localGradientPaint2);
    localMyBarRenderer.setSeriesPaint(2, localGradientPaint3);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    MyBarRenderer localMyBarRenderer = (MyBarRenderer)localCategoryPlot.getRenderer();
    MyDemoPanel localMyDemoPanel = new MyDemoPanel(localMyBarRenderer);
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localMyDemoPanel.addChart(localJFreeChart);
    localChartPanel.addChartMouseListener(localMyDemoPanel);
    localMyDemoPanel.add(localChartPanel);
    return localMyDemoPanel;
  }

  public static void main(String[] paramArrayOfString)
  {
    MouseOverDemo1 localMouseOverDemo1 = new MouseOverDemo1("JFreeChart: MouseoverDemo1.java");
    localMouseOverDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localMouseOverDemo1);
    localMouseOverDemo1.setVisible(true);
  }

  static class MyDemoPanel extends DemoPanel
    implements ChartMouseListener
  {
    private MouseOverDemo1.MyBarRenderer renderer;

    public MyDemoPanel(MouseOverDemo1.MyBarRenderer paramMyBarRenderer)
    {
      super(new BorderLayout());
      this.renderer = paramMyBarRenderer;
    }

    public void chartMouseMoved(ChartMouseEvent paramChartMouseEvent)
    {
      ChartEntity localChartEntity = paramChartMouseEvent.getEntity();
      if (!(localChartEntity instanceof CategoryItemEntity))
      {
        this.renderer.setHighlightedItem(-1, -1);
        return;
      }
      CategoryItemEntity localCategoryItemEntity = (CategoryItemEntity)localChartEntity;
      CategoryDataset localCategoryDataset = localCategoryItemEntity.getDataset();
      this.renderer.setHighlightedItem(localCategoryDataset.getRowIndex(localCategoryItemEntity.getRowKey()), localCategoryDataset.getColumnIndex(localCategoryItemEntity.getColumnKey()));
    }

    public void chartMouseClicked(ChartMouseEvent paramChartMouseEvent)
    {
    }
  }

  static class MyBarRenderer extends BarRenderer
  {
    private int highlightRow = -1;
    private int highlightColumn = -1;

    public void setHighlightedItem(int paramInt1, int paramInt2)
    {
      if ((this.highlightRow == paramInt1) && (this.highlightColumn == paramInt2))
        return;
      this.highlightRow = paramInt1;
      this.highlightColumn = paramInt2;
      notifyListeners(new RendererChangeEvent(this));
    }

    public Paint getItemOutlinePaint(int paramInt1, int paramInt2)
    {
      if ((paramInt1 == this.highlightRow) && (paramInt2 == this.highlightColumn))
        return Color.yellow;
      return super.getItemOutlinePaint(paramInt1, paramInt2);
    }
  }
}