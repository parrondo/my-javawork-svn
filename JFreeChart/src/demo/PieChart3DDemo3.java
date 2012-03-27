package demo;

import java.awt.Dimension;
import java.text.AttributedString;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.util.Rotation;

public class PieChart3DDemo3 extends ApplicationFrame
{
  public PieChart3DDemo3(String paramString)
  {
    super(paramString);
    PieDataset localPieDataset = createDataset();
    JFreeChart localJFreeChart = createChart(localPieDataset);
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localChartPanel);
  }

  private static PieDataset createDataset()
  {
    DefaultPieDataset localDefaultPieDataset = new DefaultPieDataset();
    localDefaultPieDataset.setValue("Java", new Double(43.200000000000003D));
    localDefaultPieDataset.setValue("Visual Basic", new Double(10.0D));
    localDefaultPieDataset.setValue("C/C++", new Double(17.5D));
    localDefaultPieDataset.setValue("PHP", new Double(32.5D));
    localDefaultPieDataset.setValue("Perl", new Double(1.0D));
    return localDefaultPieDataset;
  }

  private static JFreeChart createChart(PieDataset paramPieDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createPieChart3D("Pie Chart 3D Demo 3", paramPieDataset, true, true, false);
    PiePlot3D localPiePlot3D = (PiePlot3D)localJFreeChart.getPlot();
    localPiePlot3D.setStartAngle(290.0D);
    localPiePlot3D.setDirection(Rotation.CLOCKWISE);
    localPiePlot3D.setForegroundAlpha(0.5F);
    localPiePlot3D.setNoDataMessage("No data to display");
    localPiePlot3D.setLabelGenerator(new CustomLabelGenerator());
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    PieChart3DDemo3 localPieChart3DDemo3 = new PieChart3DDemo3("JFreeChart: PieChart3DDemo3.java");
    localPieChart3DDemo3.pack();
    RefineryUtilities.centerFrameOnScreen(localPieChart3DDemo3);
    localPieChart3DDemo3.setVisible(true);
  }

  static class CustomLabelGenerator
    implements PieSectionLabelGenerator
  {
    public String generateSectionLabel(PieDataset paramPieDataset, Comparable paramComparable)
    {
      String str = null;
      if ((paramPieDataset != null) && (!(paramComparable.equals("PHP"))))
        str = paramComparable.toString();
      return str;
    }

    public AttributedString generateAttributedSectionLabel(PieDataset paramPieDataset, Comparable paramComparable)
    {
      return null;
    }
  }
}