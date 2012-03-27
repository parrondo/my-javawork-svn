package demo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StatisticalLineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.statistics.DefaultStatisticalCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class HideSeriesDemo2 extends ApplicationFrame
{
  public HideSeriesDemo2(String paramString)
  {
    super(paramString);
    setContentPane(new MyDemoPanel());
  }

  public static JPanel createDemoPanel()
  {
    return new MyDemoPanel();
  }

  public static void main(String[] paramArrayOfString)
  {
    HideSeriesDemo2 localHideSeriesDemo2 = new HideSeriesDemo2("JFreeChart: HideSeriesDemo2.java");
    localHideSeriesDemo2.pack();
    RefineryUtilities.centerFrameOnScreen(localHideSeriesDemo2);
    localHideSeriesDemo2.setVisible(true);
  }

  static class MyDemoPanel extends DemoPanel
    implements ActionListener
  {
    private CategoryItemRenderer renderer;

    public MyDemoPanel()
    {
      super(new BorderLayout());
      CategoryDataset localCategoryDataset = createSampleDataset();
      JFreeChart localJFreeChart = createChart(localCategoryDataset);
      addChart(localJFreeChart);
      ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
      JPanel localJPanel = new JPanel();
      JCheckBox localJCheckBox1 = new JCheckBox("Series 1");
      localJCheckBox1.setActionCommand("S1");
      localJCheckBox1.addActionListener(this);
      localJCheckBox1.setSelected(true);
      JCheckBox localJCheckBox2 = new JCheckBox("Series 2");
      localJCheckBox2.setActionCommand("S2");
      localJCheckBox2.addActionListener(this);
      localJCheckBox2.setSelected(true);
      JCheckBox localJCheckBox3 = new JCheckBox("Series 3");
      localJCheckBox3.setActionCommand("S3");
      localJCheckBox3.addActionListener(this);
      localJCheckBox3.setSelected(true);
      localJPanel.add(localJCheckBox1);
      localJPanel.add(localJCheckBox2);
      localJPanel.add(localJCheckBox3);
      add(localChartPanel);
      add(localJPanel, "South");
      localChartPanel.setPreferredSize(new Dimension(500, 270));
    }

    private CategoryDataset createSampleDataset()
    {
      DefaultStatisticalCategoryDataset localDefaultStatisticalCategoryDataset = new DefaultStatisticalCategoryDataset();
      localDefaultStatisticalCategoryDataset.add(10.0D, 2.4D, "Row 1", "Column 1");
      localDefaultStatisticalCategoryDataset.add(15.0D, 4.4D, "Row 1", "Column 2");
      localDefaultStatisticalCategoryDataset.add(13.0D, 2.1D, "Row 1", "Column 3");
      localDefaultStatisticalCategoryDataset.add(7.0D, 1.3D, "Row 1", "Column 4");
      localDefaultStatisticalCategoryDataset.add(22.0D, 2.4D, "Row 2", "Column 1");
      localDefaultStatisticalCategoryDataset.add(18.0D, 4.4D, "Row 2", "Column 2");
      localDefaultStatisticalCategoryDataset.add(28.0D, 2.1D, "Row 2", "Column 3");
      localDefaultStatisticalCategoryDataset.add(7.0D, 1.3D, "Row 2", "Column 4");
      localDefaultStatisticalCategoryDataset.add(2.0D, 2.4D, "Row 3", "Column 1");
      localDefaultStatisticalCategoryDataset.add(8.0D, 4.4D, "Row 3", "Column 2");
      localDefaultStatisticalCategoryDataset.add(8.0D, 2.1D, "Row 3", "Column 3");
      localDefaultStatisticalCategoryDataset.add(7.0D, 1.3D, "Row 3", "Column 4");
      return localDefaultStatisticalCategoryDataset;
    }

    private JFreeChart createChart(CategoryDataset paramCategoryDataset)
    {
      JFreeChart localJFreeChart = ChartFactory.createAreaChart("Hide Series Demo 2", "Category", "Value", paramCategoryDataset, PlotOrientation.VERTICAL, true, true, false);
      CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
      localCategoryPlot.setRenderer(new StatisticalLineAndShapeRenderer());
      this.renderer = localCategoryPlot.getRenderer(0);
      return localJFreeChart;
    }

    public void actionPerformed(ActionEvent paramActionEvent)
    {
      int i = -1;
      if (paramActionEvent.getActionCommand().equals("S1"))
        i = 0;
      else if (paramActionEvent.getActionCommand().equals("S2"))
        i = 1;
      else if (paramActionEvent.getActionCommand().equals("S3"))
        i = 2;
      if (i < 0)
        return;
      boolean bool = this.renderer.getItemVisible(i, 0);
      this.renderer.setSeriesVisible(i, new Boolean(!(bool)));
    }
  }
}