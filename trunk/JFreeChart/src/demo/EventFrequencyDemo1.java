package demo;

import java.awt.Dimension;
import java.text.DateFormat;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Day;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class EventFrequencyDemo1 extends ApplicationFrame
{
  public EventFrequencyDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createBarChart("Event Frequency Demo", "Category", "Value", paramCategoryDataset, PlotOrientation.HORIZONTAL, true, true, false);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    localCategoryPlot.setRangePannable(true);
    localCategoryPlot.getDomainAxis().setMaximumCategoryLabelWidthRatio(10.0F);
    localCategoryPlot.setRangeAxis(new DateAxis("Date"));
    StandardCategoryToolTipGenerator localStandardCategoryToolTipGenerator = new StandardCategoryToolTipGenerator("{0}, {1} : {2}", DateFormat.getDateInstance());
    LineAndShapeRenderer localLineAndShapeRenderer = new LineAndShapeRenderer(false, true);
    localLineAndShapeRenderer.setBaseToolTipGenerator(localStandardCategoryToolTipGenerator);
    localCategoryPlot.setRenderer(localLineAndShapeRenderer);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  private static CategoryDataset createDataset()
  {
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    Day localDay1 = new Day(12, 6, 2002);
    Day localDay2 = new Day(14, 6, 2002);
    Day localDay3 = new Day(15, 6, 2002);
    Day localDay4 = new Day(10, 7, 2002);
    Day localDay5 = new Day(20, 7, 2002);
    Day localDay6 = new Day(22, 8, 2002);
    localDefaultCategoryDataset.setValue(new Long(localDay1.getMiddleMillisecond()), "Series 1", "Requirement 1");
    localDefaultCategoryDataset.setValue(new Long(localDay1.getMiddleMillisecond()), "Series 1", "Requirement 2");
    localDefaultCategoryDataset.setValue(new Long(localDay2.getMiddleMillisecond()), "Series 1", "Requirement 3");
    localDefaultCategoryDataset.setValue(new Long(localDay3.getMiddleMillisecond()), "Series 2", "Requirement 1");
    localDefaultCategoryDataset.setValue(new Long(localDay4.getMiddleMillisecond()), "Series 2", "Requirement 3");
    localDefaultCategoryDataset.setValue(new Long(localDay5.getMiddleMillisecond()), "Series 3", "Requirement 2");
    localDefaultCategoryDataset.setValue(new Long(localDay6.getMiddleMillisecond()), "Series 1", "Requirement 4");
    return localDefaultCategoryDataset;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    EventFrequencyDemo1 localEventFrequencyDemo1 = new EventFrequencyDemo1("JFreeChart: EventFrequencyDemo1.java");
    localEventFrequencyDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localEventFrequencyDemo1);
    localEventFrequencyDemo1.setVisible(true);
  }
}