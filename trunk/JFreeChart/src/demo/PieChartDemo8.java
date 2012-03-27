package demo;

import java.awt.Dimension;
import java.awt.font.TextAttribute;
import java.text.AttributedString;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class PieChartDemo8 extends ApplicationFrame
{
  public PieChartDemo8(String paramString)
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
    JFreeChart localJFreeChart = ChartFactory.createPieChart("Pie Chart Demo 8", paramPieDataset, false, true, false);
    PiePlot localPiePlot = (PiePlot)localJFreeChart.getPlot();
    localPiePlot.setLabelGenerator(new CustomLabelGenerator());
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    PieChartDemo8 localPieChartDemo8 = new PieChartDemo8("JFreeChart: PieChartDemo8.java");
    localPieChartDemo8.pack();
    RefineryUtilities.centerFrameOnScreen(localPieChartDemo8);
    localPieChartDemo8.setVisible(true);
  }

  static class CustomLabelGenerator
    implements PieSectionLabelGenerator
  {
    public String generateSectionLabel(PieDataset paramPieDataset, Comparable paramComparable)
    {
      String str = null;
      if ((paramPieDataset != null) && (!(paramComparable.equals("Two"))))
        str = paramComparable.toString();
      return str;
    }

    public AttributedString generateAttributedSectionLabel(PieDataset paramPieDataset, Comparable paramComparable)
    {
      AttributedString localAttributedString = null;
      String str1 = paramComparable.toString();
      String str2 = str1 + " : " + String.valueOf(paramPieDataset.getValue(paramComparable));
      localAttributedString = new AttributedString(str2);
      localAttributedString.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD, 0, str1.length() - 1);
      return localAttributedString;
    }
  }
}