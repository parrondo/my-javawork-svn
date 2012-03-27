package demo;

import java.awt.Dimension;
import java.awt.Font;
import java.text.DecimalFormat;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class BarChartDemo5 extends ApplicationFrame
{
  public BarChartDemo5(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static CategoryDataset createDataset()
  {
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    String str = "Prison Population Rates";
    localDefaultCategoryDataset.addValue(59.0D, str, "Norway");
    localDefaultCategoryDataset.addValue(69.0D, str, "Switzerland");
    localDefaultCategoryDataset.addValue(85.0D, str, "France");
    localDefaultCategoryDataset.addValue(93.0D, str, "Syria");
    localDefaultCategoryDataset.addValue(96.0D, str, "Germany");
    localDefaultCategoryDataset.addValue(111.0D, str, "China");
    localDefaultCategoryDataset.addValue(116.0D, str, "Australia");
    localDefaultCategoryDataset.addValue(121.0D, str, "Egypt");
    localDefaultCategoryDataset.addValue(129.0D, str, "England & Wales");
    localDefaultCategoryDataset.addValue(157.0D, str, "New Zealand");
    localDefaultCategoryDataset.addValue(205.0D, str, "Chile");
    localDefaultCategoryDataset.addValue(229.0D, str, "Iran");
    localDefaultCategoryDataset.addValue(359.0D, str, "Singapore");
    localDefaultCategoryDataset.addValue(404.0D, str, "South Africa");
    localDefaultCategoryDataset.addValue(406.0D, str, "Ukraine");
    localDefaultCategoryDataset.addValue(686.0D, str, "USA");
    return localDefaultCategoryDataset;
  }

  private static JFreeChart createChart(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createBarChart("Prison Population Rates - Selected Countries", "Country", "Prisoners Per 100,000 National Population", paramCategoryDataset, PlotOrientation.HORIZONTAL, false, true, false);
    localJFreeChart.addSubtitle(new TextTitle("Source: http://www.homeoffice.gov.uk/rds/pdfs2/r188.pdf", new Font("Dialog", 2, 10)));
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    localCategoryPlot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
    localCategoryPlot.setRangePannable(true);
    BarRenderer localBarRenderer = (BarRenderer)localCategoryPlot.getRenderer();
    localBarRenderer.setItemLabelAnchorOffset(9.0D);
    localBarRenderer.setBaseItemLabelsVisible(true);
    localBarRenderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
    localBarRenderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator("{0}, {1}) = {2} per 100,000", new DecimalFormat("0")));
    CategoryAxis localCategoryAxis = localCategoryPlot.getDomainAxis();
    localCategoryAxis.setCategoryMargin(0.25D);
    localCategoryAxis.setUpperMargin(0.02D);
    localCategoryAxis.setLowerMargin(0.02D);
    NumberAxis localNumberAxis = (NumberAxis)localCategoryPlot.getRangeAxis();
    localNumberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    localNumberAxis.setUpperMargin(0.1D);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
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
    BarChartDemo5 localBarChartDemo5 = new BarChartDemo5("JFreeChart: BarChartDemo5.java");
    localBarChartDemo5.pack();
    RefineryUtilities.centerFrameOnScreen(localBarChartDemo5);
    localBarChartDemo5.setVisible(true);
  }
}