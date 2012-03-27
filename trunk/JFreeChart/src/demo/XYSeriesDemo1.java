package demo;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.List;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class XYSeriesDemo1 extends ApplicationFrame
{
  public XYSeriesDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createXYLineChart("XY Series Demo 1", "X", "Y", paramXYDataset, PlotOrientation.VERTICAL, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setDomainPannable(true);
    localXYPlot.setRangePannable(true);
    NumberAxis localNumberAxis1 = new NumberAxis(null);
    localXYPlot.setDomainAxis(1, localNumberAxis1);
    NumberAxis localNumberAxis2 = new NumberAxis(null);
    localXYPlot.setRangeAxis(1, localNumberAxis2);
    List localList = Arrays.asList(new Integer[] { new Integer(0), new Integer(1) });
    localXYPlot.mapDatasetToDomainAxes(0, localList);
    localXYPlot.mapDatasetToRangeAxes(0, localList);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  private static XYDataset createDataset()
  {
    XYSeries localXYSeries = new XYSeries("Random Data");
    localXYSeries.add(1.0D, 500.19999999999999D);
    localXYSeries.add(5.0D, 694.10000000000002D);
    localXYSeries.add(4.0D, 100.0D);
    localXYSeries.add(12.5D, 734.39999999999998D);
    localXYSeries.add(17.300000000000001D, 453.19999999999999D);
    localXYSeries.add(21.199999999999999D, 500.19999999999999D);
    localXYSeries.add(21.899999999999999D, null);
    localXYSeries.add(25.600000000000001D, 734.39999999999998D);
    localXYSeries.add(30.0D, 453.19999999999999D);
    return new XYSeriesCollection(localXYSeries);
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
    XYSeriesDemo1 localXYSeriesDemo1 = new XYSeriesDemo1("JFreeChart: XYSeriesDemo1.java");
    localXYSeriesDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localXYSeriesDemo1);
    localXYSeriesDemo1.setVisible(true);
  }
}