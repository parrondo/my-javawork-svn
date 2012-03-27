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
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class HideSeriesDemo1 extends ApplicationFrame
{
  public HideSeriesDemo1(String paramString)
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
    HideSeriesDemo1 localHideSeriesDemo1 = new HideSeriesDemo1("JFreeChart: HideSeriesDemo1.java");
    localHideSeriesDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localHideSeriesDemo1);
    localHideSeriesDemo1.setVisible(true);
  }

  static class MyDemoPanel extends DemoPanel
    implements ActionListener
  {
    private XYItemRenderer renderer;

    public MyDemoPanel()
    {
      super(new BorderLayout());
      XYDataset localXYDataset = createSampleDataset();
      JFreeChart localJFreeChart = createChart(localXYDataset);
      addChart(localJFreeChart);
      ChartPanel localChartPanel = new ChartPanel(localJFreeChart, true);
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

    private XYDataset createSampleDataset()
    {
      XYSeries localXYSeries1 = new XYSeries("Series 1");
      localXYSeries1.add(1.0D, 3.3D);
      localXYSeries1.add(2.0D, 4.4D);
      localXYSeries1.add(3.0D, 1.7D);
      XYSeries localXYSeries2 = new XYSeries("Series 2");
      localXYSeries2.add(1.0D, 7.3D);
      localXYSeries2.add(2.0D, 6.8D);
      localXYSeries2.add(3.0D, 9.6D);
      localXYSeries2.add(4.0D, 5.6D);
      XYSeries localXYSeries3 = new XYSeries("Series 3");
      localXYSeries3.add(1.0D, 17.300000000000001D);
      localXYSeries3.add(2.0D, 16.800000000000001D);
      localXYSeries3.add(3.0D, 19.600000000000001D);
      localXYSeries3.add(4.0D, 15.6D);
      XYSeriesCollection localXYSeriesCollection = new XYSeriesCollection();
      localXYSeriesCollection.addSeries(localXYSeries1);
      localXYSeriesCollection.addSeries(localXYSeries2);
      localXYSeriesCollection.addSeries(localXYSeries3);
      return localXYSeriesCollection;
    }

    private JFreeChart createChart(XYDataset paramXYDataset)
    {
      JFreeChart localJFreeChart = ChartFactory.createXYLineChart("Hide Series Demo 1", "X", "Y", paramXYDataset, PlotOrientation.VERTICAL, true, true, false);
      XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
      this.renderer = localXYPlot.getRenderer();
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