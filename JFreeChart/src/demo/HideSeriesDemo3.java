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
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.XYZDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class HideSeriesDemo3 extends ApplicationFrame
{
  public HideSeriesDemo3(String paramString)
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
    HideSeriesDemo3 localHideSeriesDemo3 = new HideSeriesDemo3("JFreeChart: HideSeriesDemo3.java");
    localHideSeriesDemo3.pack();
    RefineryUtilities.centerFrameOnScreen(localHideSeriesDemo3);
    localHideSeriesDemo3.setVisible(true);
  }

  static class MyDemoPanel extends DemoPanel
    implements ActionListener
  {
    private XYItemRenderer renderer;

    public MyDemoPanel()
    {
      super(new BorderLayout());
      XYZDataset localXYZDataset = createSampleDataset();
      JFreeChart localJFreeChart = createChart(localXYZDataset);
      addChart(localJFreeChart);
      ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
      localChartPanel.setMouseWheelEnabled(true);
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

    private XYZDataset createSampleDataset()
    {
      DefaultXYZDataset localDefaultXYZDataset = new DefaultXYZDataset();
      double[] arrayOfDouble1 = { 2.1D, 2.3D, 2.3D };
      double[] arrayOfDouble2 = { 14.1D, 11.1D, 10.0D };
      double[] arrayOfDouble3 = { 2.4D, 2.7D, 2.7D };
      double[][] arrayOfDouble = { arrayOfDouble1, arrayOfDouble2, arrayOfDouble3 };
      localDefaultXYZDataset.addSeries("Series 1", arrayOfDouble);
      arrayOfDouble1 = new double[] { 2.2D, 2.2D, 1.8D };
      arrayOfDouble2 = new double[] { 14.1D, 11.1D, 10.0D };
      arrayOfDouble3 = new double[] { 2.2D, 2.2D, 2.2D };
      arrayOfDouble = new double[][] { arrayOfDouble1, arrayOfDouble2, arrayOfDouble3 };
      localDefaultXYZDataset.addSeries("Series 2", arrayOfDouble);
      arrayOfDouble1 = new double[] { 1.8D, 1.9D, 2.3D, 3.8D };
      arrayOfDouble2 = new double[] { 5.4D, 4.1D, 4.1D, 25.0D };
      arrayOfDouble3 = new double[] { 2.1D, 2.2D, 1.6D, 4.0D };
      arrayOfDouble = new double[][] { arrayOfDouble1, arrayOfDouble2, arrayOfDouble3 };
      localDefaultXYZDataset.addSeries("Series 3", arrayOfDouble);
      return localDefaultXYZDataset;
    }

    private JFreeChart createChart(XYZDataset paramXYZDataset)
    {
      JFreeChart localJFreeChart = ChartFactory.createBubbleChart("Hide Series Demo 3", "X", "Y", paramXYZDataset, PlotOrientation.VERTICAL, true, true, false);
      XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
      localXYPlot.setDomainPannable(true);
      localXYPlot.setRangePannable(true);
      this.renderer = localXYPlot.getRenderer(0);
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