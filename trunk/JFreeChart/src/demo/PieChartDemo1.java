package demo;

import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class PieChartDemo1 extends ApplicationFrame
{
  public PieChartDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static PieDataset createDataset()
  {
    DefaultPieDataset localDefaultPieDataset = new DefaultPieDataset();
    localDefaultPieDataset.setValue("One", new Double(43.200000000000003D));
    localDefaultPieDataset.setValue("Two", new Double(10.0D));
    localDefaultPieDataset.setValue("Three", new Double(27.5D));
    localDefaultPieDataset.setValue("Four", new Double(17.5D));
    localDefaultPieDataset.setValue("Five", new Double(11.0D));
    localDefaultPieDataset.setValue("Six", new Double(19.399999999999999D));
    return localDefaultPieDataset;
  }

  private static JFreeChart createChart(PieDataset paramPieDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createPieChart("Pie Chart Demo 1", paramPieDataset, true, true, false);
    TextTitle localTextTitle = localJFreeChart.getTitle();
    localTextTitle.setToolTipText("A title tooltip!");
    PiePlot localPiePlot = (PiePlot)localJFreeChart.getPlot();
    localPiePlot.setLabelFont(new Font("SansSerif", 0, 12));
    localPiePlot.setNoDataMessage("No data available");
    localPiePlot.setCircular(false);
    localPiePlot.setLabelGap(0.02D);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    PieChartDemo1 localPieChartDemo1 = new PieChartDemo1("JFreeChart: PieChartDemo1.java");
    localPieChartDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localPieChartDemo1);
    localPieChartDemo1.setVisible(true);
  }
}