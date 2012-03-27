package demo;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CompassPlot;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.data.general.ValueDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class CompassDemo1 extends ApplicationFrame
{
  public CompassDemo1(String paramString)
  {
    super(paramString);
    ChartPanel localChartPanel = (ChartPanel)createDemoPanel();
    localChartPanel.setPreferredSize(new Dimension(500, 270));
    localChartPanel.setEnforceFileExtensions(false);
    setContentPane(localChartPanel);
  }

  private static JFreeChart createChart(ValueDataset paramValueDataset)
  {
    CompassPlot localCompassPlot = new CompassPlot(paramValueDataset);
    localCompassPlot.setSeriesNeedle(7);
    localCompassPlot.setSeriesPaint(0, Color.black);
    localCompassPlot.setSeriesOutlinePaint(0, Color.black);
    localCompassPlot.setRosePaint(Color.red);
    localCompassPlot.setRoseHighlightPaint(Color.gray);
    localCompassPlot.setRoseCenterPaint(Color.white);
    localCompassPlot.setDrawBorder(false);
    JFreeChart localJFreeChart = new JFreeChart(localCompassPlot);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(new DefaultValueDataset(new Double(45.0D)));
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    CompassDemo1 localCompassDemo1 = new CompassDemo1("JFreeChart: CompassDemo1.java");
    localCompassDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localCompassDemo1);
    localCompassDemo1.setVisible(true);
  }
}