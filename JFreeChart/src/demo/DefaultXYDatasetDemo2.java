package demo;

import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class DefaultXYDatasetDemo2 extends ApplicationFrame
{
  public DefaultXYDatasetDemo2(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createScatterPlot("DefaultXYDatasetDemo2", "X", "Y", paramXYDataset, PlotOrientation.VERTICAL, true, false, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setDomainPannable(true);
    localXYPlot.setRangePannable(true);
    return localJFreeChart;
  }

  private static XYDataset createDataset()
  {
    DefaultXYDataset localDefaultXYDataset = new DefaultXYDataset();
    double[] arrayOfDouble1 = new double[1000];
    double[] arrayOfDouble2 = new double[1000];
    for (int i = 0; i < 1000; ++i)
    {
      arrayOfDouble1[i] = (Math.random() + 1.0D);
      arrayOfDouble2[i] = (Math.random() + 1.0D);
    }
    double[][] arrayOfDouble = { arrayOfDouble1, arrayOfDouble2 };
    localDefaultXYDataset.addSeries("Series 1", arrayOfDouble);
    return localDefaultXYDataset;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setMouseWheelEnabled(true);
    return localChartPanel;
  }

  public static void main(String[] paramArrayOfString)
  {
    DefaultXYDatasetDemo2 localDefaultXYDatasetDemo2 = new DefaultXYDatasetDemo2("JFreeChart: DefaultXYDatasetDemo2.java");
    localDefaultXYDatasetDemo2.pack();
    RefineryUtilities.centerFrameOnScreen(localDefaultXYDatasetDemo2);
    localDefaultXYDatasetDemo2.setVisible(true);
  }
}