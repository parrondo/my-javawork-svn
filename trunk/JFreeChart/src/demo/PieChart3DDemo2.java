package demo;

import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.util.Rotation;

public class PieChart3DDemo2 extends ApplicationFrame
{
  public PieChart3DDemo2(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart(PieDataset paramPieDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createPieChart3D("Pie Chart 3D Demo 2", paramPieDataset, true, false, false);
    PiePlot3D localPiePlot3D = (PiePlot3D)localJFreeChart.getPlot();
    localPiePlot3D.setStartAngle(270.0D);
    localPiePlot3D.setDirection(Rotation.ANTICLOCKWISE);
    localPiePlot3D.setForegroundAlpha(0.6F);
    return localJFreeChart;
  }

  private static PieDataset createDataset()
  {
    DefaultPieDataset localDefaultPieDataset = new DefaultPieDataset();
    localDefaultPieDataset.setValue("Java", new Double(43.200000000000003D));
    localDefaultPieDataset.setValue("Visual Basic", new Double(10.0D));
    localDefaultPieDataset.setValue("C/C++", new Double(17.5D));
    localDefaultPieDataset.setValue("PHP", new Double(32.5D));
    localDefaultPieDataset.setValue("Perl", new Double(12.5D));
    return localDefaultPieDataset;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    Rotator localRotator = new Rotator((PiePlot3D)localJFreeChart.getPlot());
    localRotator.start();
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    PieChart3DDemo2 localPieChart3DDemo2 = new PieChart3DDemo2("JFreeChart: PieChart3DDemo2.java");
    localPieChart3DDemo2.pack();
    RefineryUtilities.centerFrameOnScreen(localPieChart3DDemo2);
    localPieChart3DDemo2.setVisible(true);
  }
}