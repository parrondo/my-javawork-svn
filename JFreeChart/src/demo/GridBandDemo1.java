package demo;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class GridBandDemo1 extends ApplicationFrame
{
  public GridBandDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createScatterPlot("Grid Band Demo 1", "X", "Y", paramXYDataset, PlotOrientation.VERTICAL, true, false, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setNoDataMessage("NO DATA");
    localXYPlot.setRangeZeroBaselineVisible(true);
    localXYPlot.setDomainPannable(true);
    localXYPlot.setRangePannable(true);
    localXYPlot.setDomainTickBandPaint(new Color(0, 100, 0, 50));
    localXYPlot.setRangeTickBandPaint(new Color(0, 100, 0, 50));
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    XYSeries localXYSeries = new XYSeries("Random Data");
    for (int i = 0; i < 100; ++i)
      localXYSeries.add(Math.random() + 1.0D, Math.random() + 1.0D);
    XYSeriesCollection localXYSeriesCollection = new XYSeriesCollection();
    localXYSeriesCollection.addSeries(localXYSeries);
    JFreeChart localJFreeChart = createChart(localXYSeriesCollection);
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setMouseWheelEnabled(true);
    return localChartPanel;
  }

  public static void main(String[] paramArrayOfString)
  {
    GridBandDemo1 localGridBandDemo1 = new GridBandDemo1("JFreeChart: GridBandDemo1.java");
    localGridBandDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localGridBandDemo1);
    localGridBandDemo1.setVisible(true);
  }
}