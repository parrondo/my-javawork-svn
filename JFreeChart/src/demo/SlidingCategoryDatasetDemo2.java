package demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import javax.swing.BorderFactory;
import javax.swing.BoundedRangeModel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.category.SlidingCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class SlidingCategoryDatasetDemo2 extends ApplicationFrame
{
  public SlidingCategoryDatasetDemo2(String paramString)
  {
    super(paramString);
    setDefaultCloseOperation(3);
    setContentPane(createDemoPanel());
  }

  public static JPanel createDemoPanel()
  {
    return new MyDemoPanel();
  }

  public static void main(String[] paramArrayOfString)
  {
    SlidingCategoryDatasetDemo2 localSlidingCategoryDatasetDemo2 = new SlidingCategoryDatasetDemo2("JFreeChart: SlidingCategoryDatasetDemo2.java");
    localSlidingCategoryDatasetDemo2.pack();
    RefineryUtilities.centerFrameOnScreen(localSlidingCategoryDatasetDemo2);
    localSlidingCategoryDatasetDemo2.setVisible(true);
  }

  static class MyDemoPanel extends DemoPanel
    implements ChangeListener
  {
    JScrollBar scroller;
    SlidingCategoryDataset dataset = new SlidingCategoryDataset(createDataset(), 0, 10);

    public MyDemoPanel()
    {
      super(new BorderLayout());
      JFreeChart localJFreeChart = createChart(this.dataset);
      addChart(localJFreeChart);
      ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
      localChartPanel.setPreferredSize(new Dimension(400, 400));
      this.scroller = new JScrollBar(0, 0, 10, 0, 50);
      add(localChartPanel);
      this.scroller.getModel().addChangeListener(this);
      JPanel localJPanel = new JPanel(new BorderLayout());
      localJPanel.add(this.scroller);
      localJPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
      localJPanel.setBackground(Color.white);
      add(localJPanel, "South");
    }

    private static CategoryDataset createDataset()
    {
      DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
      for (int i = 0; i < 50; ++i)
        localDefaultCategoryDataset.addValue(Math.random() * 100.0D, "S1", "S" + i);
      return localDefaultCategoryDataset;
    }

    private static JFreeChart createChart(CategoryDataset paramCategoryDataset)
    {
      JFreeChart localJFreeChart = ChartFactory.createBarChart("SlidingCategoryDatasetDemo2", "Series", "Value", paramCategoryDataset, PlotOrientation.VERTICAL, false, true, false);
      CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
      CategoryAxis localCategoryAxis = localCategoryPlot.getDomainAxis();
      localCategoryAxis.setMaximumCategoryLabelWidthRatio(0.8F);
      localCategoryAxis.setLowerMargin(0.02D);
      localCategoryAxis.setUpperMargin(0.02D);
      NumberAxis localNumberAxis = (NumberAxis)localCategoryPlot.getRangeAxis();
      localNumberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
      localNumberAxis.setRange(0.0D, 100.0D);
      BarRenderer localBarRenderer = (BarRenderer)localCategoryPlot.getRenderer();
      localBarRenderer.setDrawBarOutline(false);
      GradientPaint localGradientPaint = new GradientPaint(0.0F, 0.0F, Color.blue, 0.0F, 0.0F, new Color(0, 0, 64));
      localBarRenderer.setSeriesPaint(0, localGradientPaint);
      return localJFreeChart;
    }

    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      this.dataset.setFirstCategoryIndex(this.scroller.getValue());
    }
  }
}