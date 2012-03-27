package demo;

import java.awt.Font;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.block.BlockContainer;
import org.jfree.chart.block.BorderArrangement;
import org.jfree.chart.block.LabelBlock;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RefineryUtilities;

public class LegendWrapperDemo1 extends ApplicationFrame
{
  public LegendWrapperDemo1(String paramString)
  {
    super(paramString);
    setContentPane(createDemoPanel());
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
    JFreeChart localJFreeChart = ChartFactory.createPieChart("Legend Wrapper Demo 1", paramPieDataset, false, true, false);
    PiePlot localPiePlot = (PiePlot)localJFreeChart.getPlot();
    localPiePlot.setLabelFont(new Font("SansSerif", 0, 12));
    localPiePlot.setNoDataMessage("No data available");
    localPiePlot.setCircular(true);
    localPiePlot.setLabelGap(0.02D);
    LegendTitle localLegendTitle = new LegendTitle(localJFreeChart.getPlot());
    BlockContainer localBlockContainer1 = new BlockContainer(new BorderArrangement());
    localBlockContainer1.setFrame(new BlockBorder(1.0D, 1.0D, 1.0D, 1.0D));
    LabelBlock localLabelBlock1 = new LabelBlock("Legend Items:", new Font("SansSerif", 1, 12));
    localLabelBlock1.setPadding(5.0D, 5.0D, 5.0D, 5.0D);
    localBlockContainer1.add(localLabelBlock1, RectangleEdge.TOP);
    LabelBlock localLabelBlock2 = new LabelBlock("Source: http://www.jfree.org");
    localLabelBlock2.setPadding(8.0D, 20.0D, 2.0D, 5.0D);
    localBlockContainer1.add(localLabelBlock2, RectangleEdge.BOTTOM);
    BlockContainer localBlockContainer2 = localLegendTitle.getItemContainer();
    localBlockContainer2.setPadding(2.0D, 10.0D, 5.0D, 2.0D);
    localBlockContainer1.add(localBlockContainer2);
    localLegendTitle.setWrapper(localBlockContainer1);
    localLegendTitle.setPosition(RectangleEdge.RIGHT);
    localLegendTitle.setHorizontalAlignment(HorizontalAlignment.LEFT);
    localJFreeChart.addSubtitle(localLegendTitle);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    LegendWrapperDemo1 localLegendWrapperDemo1 = new LegendWrapperDemo1("JFreeChart: LegendWrapperDemo1.java");
    localLegendWrapperDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localLegendWrapperDemo1);
    localLegendWrapperDemo1.setVisible(true);
  }
}