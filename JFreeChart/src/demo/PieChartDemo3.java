package demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class PieChartDemo3 extends ApplicationFrame
{
  public PieChartDemo3(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart(PieDataset paramPieDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createPieChart("Pie Chart Demo 3", paramPieDataset, true, true, false);
    PiePlot localPiePlot = (PiePlot)localJFreeChart.getPlot();
    localPiePlot.setNoDataMessage("No data available so we go into this really long spiel about what that means and it runs off the end of the line but what can you do about that!");
    localPiePlot.setNoDataMessageFont(new Font("Serif", 2, 10));
    localPiePlot.setNoDataMessagePaint(Color.red);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(new DefaultPieDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    PieChartDemo3 localPieChartDemo3 = new PieChartDemo3("Pie Chart Demo 3");
    localPieChartDemo3.pack();
    RefineryUtilities.centerFrameOnScreen(localPieChartDemo3);
    localPieChartDemo3.setVisible(true);
  }
}