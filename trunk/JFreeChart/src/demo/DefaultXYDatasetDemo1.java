package demo;

import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class DefaultXYDatasetDemo1 extends ApplicationFrame
{
  public DefaultXYDatasetDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart(XYDataset paramXYDataset)
  {
    return ChartFactory.createScatterPlot("DefaultXYDatasetDemo1", "X", "Y", paramXYDataset, PlotOrientation.VERTICAL, true, false, false);
  }

  private static XYDataset createDataset()
  {
    DefaultXYDataset localDefaultXYDataset = new DefaultXYDataset();
    double[] arrayOfDouble1 = { 1.0D, 2.0D, 3.0D, 4.0D, 5.0D, 6.0D, 7.0D, 8.0D };
    double[] arrayOfDouble2 = { 8.0D, 7.0D, 6.0D, 5.0D, 4.0D, 3.0D, 2.0D, 1.0D };
    double[][] arrayOfDouble3 = { arrayOfDouble1, arrayOfDouble2 };
    localDefaultXYDataset.addSeries("Series 1", arrayOfDouble3);
    double[] arrayOfDouble4 = { 1.0D, 2.0D, 3.0D, 4.0D, 5.0D, 6.0D, 7.0D, 8.0D };
    double[] arrayOfDouble5 = { 1.0D, 2.0D, 3.0D, 4.0D, 5.0D, 6.0D, 7.0D, 8.0D };
    double[][] arrayOfDouble6 = { arrayOfDouble4, arrayOfDouble5 };
    localDefaultXYDataset.addSeries("Series 2", arrayOfDouble6);
    return localDefaultXYDataset;
  }

  public static JPanel createDemoPanel()
  {
    return new ChartPanel(createChart(createDataset()));
  }

  public static void main(String[] paramArrayOfString)
  {
    DefaultXYDatasetDemo1 localDefaultXYDatasetDemo1 = new DefaultXYDatasetDemo1("JFreeChart: DefautlXYDatasetDemo1.java");
    localDefaultXYDatasetDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localDefaultXYDatasetDemo1);
    localDefaultXYDatasetDemo1.setVisible(true);
  }
}