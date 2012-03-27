package demo;

import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.function.NormalDistributionFunction2D;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class NormalDistributionDemo1 extends ApplicationFrame
{
  public NormalDistributionDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static XYDataset createDataset()
  {
    NormalDistributionFunction2D localNormalDistributionFunction2D = new NormalDistributionFunction2D(0.0D, 1.0D);
    XYDataset localXYDataset = DatasetUtilities.sampleFunction2D(localNormalDistributionFunction2D, -5.0D, 5.0D, 100, "Normal");
    return localXYDataset;
  }

  public static JFreeChart createChart(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createXYLineChart("Normal Distribution", "X", "Y", paramXYDataset, PlotOrientation.VERTICAL, true, true, false);
    return localJFreeChart;
  }

  public static void main(String[] paramArrayOfString)
  {
    NormalDistributionDemo1 localNormalDistributionDemo1 = new NormalDistributionDemo1("JFreeChart: NormalDistributionDemo1.java");
    localNormalDistributionDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localNormalDistributionDemo1);
    localNormalDistributionDemo1.setVisible(true);
  }
}