package demo;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class PieChartDemo5 extends ApplicationFrame
{
  public PieChartDemo5(String paramString)
  {
    super(paramString);
    setContentPane(createDemoPanel());
  }

  public static JPanel createDemoPanel()
  {
    DemoPanel localDemoPanel = new DemoPanel(new GridLayout(2, 2));
    DefaultPieDataset localDefaultPieDataset = new DefaultPieDataset();
    localDefaultPieDataset.setValue("Section 1", 23.300000000000001D);
    localDefaultPieDataset.setValue("Section 2", 56.5D);
    localDefaultPieDataset.setValue("Section 3", 43.299999999999997D);
    localDefaultPieDataset.setValue("Section 4", 11.1D);
    JFreeChart localJFreeChart1 = ChartFactory.createPieChart("Chart 1", localDefaultPieDataset, false, false, false);
    localJFreeChart1.addSubtitle(new TextTitle("setCircular(true);", new Font("Dialog", 0, 12)));
    PiePlot localPiePlot1 = (PiePlot)localJFreeChart1.getPlot();
    localPiePlot1.setCircular(true);
    localPiePlot1.setInteriorGap(0.04D);
    localPiePlot1.setMaximumLabelWidth(0.2D);
    JFreeChart localJFreeChart2 = ChartFactory.createPieChart("Chart 2", localDefaultPieDataset, false, false, false);
    localJFreeChart2.addSubtitle(new TextTitle("setCircular(false);", new Font("Dialog", 0, 12)));
    PiePlot localPiePlot2 = (PiePlot)localJFreeChart2.getPlot();
    localPiePlot2.setCircular(false);
    localPiePlot2.setInteriorGap(0.04D);
    localPiePlot2.setMaximumLabelWidth(0.2D);
    JFreeChart localJFreeChart3 = ChartFactory.createPieChart3D("Chart 3", localDefaultPieDataset, false, false, false);
    localJFreeChart3.addSubtitle(new TextTitle("setCircular(true);", new Font("Dialog", 0, 12)));
    PiePlot3D localPiePlot3D1 = (PiePlot3D)localJFreeChart3.getPlot();
    localPiePlot3D1.setForegroundAlpha(0.6F);
    localPiePlot3D1.setCircular(true);
    localPiePlot3D1.setInteriorGap(0.04D);
    localPiePlot3D1.setMaximumLabelWidth(0.2D);
    JFreeChart localJFreeChart4 = ChartFactory.createPieChart3D("Chart 4", localDefaultPieDataset, false, false, false);
    localJFreeChart4.addSubtitle(new TextTitle("setCircular(false);", new Font("Dialog", 0, 12)));
    PiePlot3D localPiePlot3D2 = (PiePlot3D)localJFreeChart4.getPlot();
    localPiePlot3D2.setForegroundAlpha(0.6F);
    localPiePlot3D2.setCircular(false);
    localPiePlot3D2.setInteriorGap(0.04D);
    localPiePlot3D2.setMaximumLabelWidth(0.2D);
    localDemoPanel.add(new ChartPanel(localJFreeChart1));
    localDemoPanel.add(new ChartPanel(localJFreeChart2));
    localDemoPanel.add(new ChartPanel(localJFreeChart3));
    localDemoPanel.add(new ChartPanel(localJFreeChart4));
    localDemoPanel.addChart(localJFreeChart1);
    localDemoPanel.addChart(localJFreeChart2);
    localDemoPanel.addChart(localJFreeChart3);
    localDemoPanel.addChart(localJFreeChart4);
    localDemoPanel.setPreferredSize(new Dimension(800, 600));
    return localDemoPanel;
  }

  public static void main(String[] paramArrayOfString)
  {
    PieChartDemo5 localPieChartDemo5 = new PieChartDemo5("JFreeChart: PieChartDemo5.java");
    localPieChartDemo5.pack();
    RefineryUtilities.centerFrameOnScreen(localPieChartDemo5);
    localPieChartDemo5.setVisible(true);
  }
}