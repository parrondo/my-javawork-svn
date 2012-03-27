package demo;

import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.function.Function2D;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class Function2DDemo1 extends ApplicationFrame
{
  public Function2DDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createXYLineChart("Function2DDemo1 ", "X", "Y", paramXYDataset, PlotOrientation.VERTICAL, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setDomainPannable(true);
    localXYPlot.setRangePannable(true);
    ValueAxis localValueAxis1 = localXYPlot.getDomainAxis();
    localValueAxis1.setLowerMargin(0.0D);
    localValueAxis1.setUpperMargin(0.0D);
    localValueAxis1.setRange(-2.0D, 2.0D);
    ValueAxis localValueAxis2 = localXYPlot.getRangeAxis();
    localValueAxis2.setRange(0.0D, 5.0D);
    return localJFreeChart;
  }

  public static XYDataset createDataset()
  {
    XYDataset localXYDataset = DatasetUtilities.sampleFunction2D(new X2(), -40.0D, 40.0D, 400, "f(x)");
    return localXYDataset;
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
    Function2DDemo1 localFunction2DDemo1 = new Function2DDemo1("JFreeChart: Function2DDemo1.java");
    localFunction2DDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localFunction2DDemo1);
    localFunction2DDemo1.setVisible(true);
  }

  static class X2
    implements Function2D
  {
    public double getValue(double paramDouble)
    {
      return (paramDouble * paramDouble + 2.0D);
    }
  }
}