package demo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.CategoryTextAnnotation;
import org.jfree.chart.axis.CategoryAnchor;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;

public class SurveyResultsDemo1 extends ApplicationFrame
{
  public SurveyResultsDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(700, 600));
    setContentPane(localJPanel);
  }

  private static CategoryDataset createDataset()
  {
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(2.01D, "Results", "Category 1");
    localDefaultCategoryDataset.addValue(2.02D, "Results", "Category 2");
    localDefaultCategoryDataset.addValue(2.0D, "Results", "Category 3");
    localDefaultCategoryDataset.addValue(1.97D, "Results", "Category 4");
    localDefaultCategoryDataset.addValue(1.44D, "Results", "Category 5");
    localDefaultCategoryDataset.addValue(1.49D, "Results", "Category 6");
    localDefaultCategoryDataset.addValue(1.49D, "Results", "Category 7");
    localDefaultCategoryDataset.addValue(1.48D, "Results", "Category 8");
    localDefaultCategoryDataset.addValue(4.26D, "Results", "Category 9");
    localDefaultCategoryDataset.addValue(4.08D, "Results", "Category 10");
    localDefaultCategoryDataset.addValue(4.03D, "Results", "Category 11");
    localDefaultCategoryDataset.addValue(3.92D, "Results", "Category 12");
    localDefaultCategoryDataset.addValue(3.99D, "Results", "Category 13");
    localDefaultCategoryDataset.addValue(2.23D, "Results", "Category 14");
    localDefaultCategoryDataset.addValue(2.6D, "Results", "Overall");
    return localDefaultCategoryDataset;
  }

  private static JFreeChart createChart(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createBarChart(null, null, null, paramCategoryDataset, PlotOrientation.HORIZONTAL, false, true, false);
    localJFreeChart.setBackgroundPaint(Color.white);
    TextTitle localTextTitle = new TextTitle("Figure 7 | I. Resources - The site offers users relevant, informative and educational resources");
    localTextTitle.setHorizontalAlignment(HorizontalAlignment.LEFT);
    localTextTitle.setBackgroundPaint(Color.red);
    localTextTitle.setPaint(Color.white);
    localJFreeChart.setTitle(localTextTitle);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    localCategoryPlot.setOutlinePaint(null);
    localCategoryPlot.setDomainGridlinesVisible(true);
    localCategoryPlot.setDomainGridlinePosition(CategoryAnchor.END);
    localCategoryPlot.setDomainGridlineStroke(new BasicStroke(0.5F));
    localCategoryPlot.setDomainGridlinePaint(Color.black);
    localCategoryPlot.setRangeGridlinesVisible(false);
    localCategoryPlot.clearRangeMarkers();
    CategoryAxis localCategoryAxis = localCategoryPlot.getDomainAxis();
    localCategoryAxis.setVisible(false);
    localCategoryAxis.setCategoryMargin(0.5D);
    localCategoryPlot.getRangeAxis().setVisible(false);
    BarRenderer localBarRenderer = (BarRenderer)localCategoryPlot.getRenderer();
    localBarRenderer.setSeriesPaint(0, new Color(156, 164, 74));
    localBarRenderer.setDrawBarOutline(false);
    localBarRenderer.setBaseItemLabelsVisible(true);
    localBarRenderer.setBaseItemLabelFont(new Font("SansSerif", 1, 10));
    ItemLabelPosition localItemLabelPosition = new ItemLabelPosition(ItemLabelAnchor.INSIDE3, TextAnchor.CENTER_RIGHT);
    localBarRenderer.setBasePositiveItemLabelPosition(localItemLabelPosition);
    CategoryTextAnnotation localCategoryTextAnnotation1 = new CategoryTextAnnotation("1. White papers are available.", "Category 1", 0.0D);
    localCategoryTextAnnotation1.setFont(new Font("SansSerif", 1, 12));
    localCategoryTextAnnotation1.setTextAnchor(TextAnchor.BOTTOM_LEFT);
    localCategoryTextAnnotation1.setCategoryAnchor(CategoryAnchor.START);
    localCategoryPlot.addAnnotation(localCategoryTextAnnotation1);
    CategoryTextAnnotation localCategoryTextAnnotation2 = new CategoryTextAnnotation("2. White papers enhance users understanding of the firm and its expertise.", "Category 2", 0.0D);
    localCategoryTextAnnotation2.setFont(new Font("SansSerif", 0, 12));
    localCategoryTextAnnotation2.setTextAnchor(TextAnchor.BOTTOM_LEFT);
    localCategoryTextAnnotation2.setCategoryAnchor(CategoryAnchor.START);
    localCategoryPlot.addAnnotation(localCategoryTextAnnotation2);
    CategoryTextAnnotation localCategoryTextAnnotation3 = new CategoryTextAnnotation("3. White papers are relevant to the firm's prospects and clients.", "Category 3", 0.0D);
    localCategoryTextAnnotation3.setFont(new Font("SansSerif", 0, 12));
    localCategoryTextAnnotation3.setTextAnchor(TextAnchor.BOTTOM_LEFT);
    localCategoryTextAnnotation3.setCategoryAnchor(CategoryAnchor.START);
    localCategoryPlot.addAnnotation(localCategoryTextAnnotation3);
    CategoryTextAnnotation localCategoryTextAnnotation4 = new CategoryTextAnnotation("4. White papers are relevant to the firm's positioning.", "Category 4", 0.0D);
    localCategoryTextAnnotation4.setFont(new Font("SansSerif", 0, 12));
    localCategoryTextAnnotation4.setTextAnchor(TextAnchor.BOTTOM_LEFT);
    localCategoryTextAnnotation4.setCategoryAnchor(CategoryAnchor.START);
    localCategoryPlot.addAnnotation(localCategoryTextAnnotation4);
    CategoryTextAnnotation localCategoryTextAnnotation5 = new CategoryTextAnnotation("5. Case studies are available.", "Category 5", 0.0D);
    localCategoryTextAnnotation5.setFont(new Font("SansSerif", 1, 12));
    localCategoryTextAnnotation5.setTextAnchor(TextAnchor.BOTTOM_LEFT);
    localCategoryTextAnnotation5.setCategoryAnchor(CategoryAnchor.START);
    localCategoryPlot.addAnnotation(localCategoryTextAnnotation5);
    CategoryTextAnnotation localCategoryTextAnnotation6 = new CategoryTextAnnotation("6. Case studies enhance users understanding of the firm and its expertise.", "Category 6", 0.0D);
    localCategoryTextAnnotation6.setFont(new Font("SansSerif", 0, 12));
    localCategoryTextAnnotation6.setTextAnchor(TextAnchor.BOTTOM_LEFT);
    localCategoryTextAnnotation6.setCategoryAnchor(CategoryAnchor.START);
    localCategoryPlot.addAnnotation(localCategoryTextAnnotation6);
    CategoryTextAnnotation localCategoryTextAnnotation7 = new CategoryTextAnnotation("7. Case studies are relevant to the firm's prospects and clients.", "Category 7", 0.0D);
    localCategoryTextAnnotation7.setFont(new Font("SansSerif", 0, 12));
    localCategoryTextAnnotation7.setTextAnchor(TextAnchor.BOTTOM_LEFT);
    localCategoryTextAnnotation7.setCategoryAnchor(CategoryAnchor.START);
    localCategoryPlot.addAnnotation(localCategoryTextAnnotation7);
    CategoryTextAnnotation localCategoryTextAnnotation8 = new CategoryTextAnnotation("8. White papers are relevant to the firm's positioning.", "Category 8", 0.0D);
    localCategoryTextAnnotation8.setFont(new Font("SansSerif", 0, 12));
    localCategoryTextAnnotation8.setTextAnchor(TextAnchor.BOTTOM_LEFT);
    localCategoryTextAnnotation8.setCategoryAnchor(CategoryAnchor.START);
    localCategoryPlot.addAnnotation(localCategoryTextAnnotation8);
    CategoryTextAnnotation localCategoryTextAnnotation9 = new CategoryTextAnnotation("9. Case studies are available.", "Category 9", 0.0D);
    localCategoryTextAnnotation9.setFont(new Font("SansSerif", 1, 12));
    localCategoryTextAnnotation9.setTextAnchor(TextAnchor.BOTTOM_LEFT);
    localCategoryTextAnnotation9.setCategoryAnchor(CategoryAnchor.START);
    localCategoryPlot.addAnnotation(localCategoryTextAnnotation9);
    CategoryTextAnnotation localCategoryTextAnnotation10 = new CategoryTextAnnotation("10. Case studies enhance users understanding of the firm and its expertise.", "Category 10", 0.0D);
    localCategoryTextAnnotation10.setFont(new Font("SansSerif", 0, 12));
    localCategoryTextAnnotation10.setTextAnchor(TextAnchor.BOTTOM_LEFT);
    localCategoryTextAnnotation10.setCategoryAnchor(CategoryAnchor.START);
    localCategoryPlot.addAnnotation(localCategoryTextAnnotation10);
    CategoryTextAnnotation localCategoryTextAnnotation11 = new CategoryTextAnnotation("11. Case studies are relevant to the firm's prospects and clients.", "Category 11", 0.0D);
    localCategoryTextAnnotation11.setFont(new Font("SansSerif", 0, 12));
    localCategoryTextAnnotation11.setTextAnchor(TextAnchor.BOTTOM_LEFT);
    localCategoryTextAnnotation11.setCategoryAnchor(CategoryAnchor.START);
    localCategoryPlot.addAnnotation(localCategoryTextAnnotation11);
    CategoryTextAnnotation localCategoryTextAnnotation12 = new CategoryTextAnnotation("12. White papers are relevant to the firm's positioning.", "Category 12", 0.0D);
    localCategoryTextAnnotation12.setFont(new Font("SansSerif", 0, 12));
    localCategoryTextAnnotation12.setTextAnchor(TextAnchor.BOTTOM_LEFT);
    localCategoryTextAnnotation12.setCategoryAnchor(CategoryAnchor.START);
    localCategoryPlot.addAnnotation(localCategoryTextAnnotation12);
    CategoryTextAnnotation localCategoryTextAnnotation13 = new CategoryTextAnnotation("13. Users can easily access resources based on viewer interest.", "Category 13", 0.0D);
    localCategoryTextAnnotation13.setFont(new Font("SansSerif", 1, 12));
    localCategoryTextAnnotation13.setTextAnchor(TextAnchor.BOTTOM_LEFT);
    localCategoryTextAnnotation13.setCategoryAnchor(CategoryAnchor.START);
    localCategoryPlot.addAnnotation(localCategoryTextAnnotation13);
    CategoryTextAnnotation localCategoryTextAnnotation14 = new CategoryTextAnnotation("14. Access to additional hyperlinks enhances users's ability to find relevant information.", "Category 14", 0.0D);
    localCategoryTextAnnotation14.setFont(new Font("SansSerif", 1, 12));
    localCategoryTextAnnotation14.setTextAnchor(TextAnchor.BOTTOM_LEFT);
    localCategoryTextAnnotation14.setCategoryAnchor(CategoryAnchor.START);
    localCategoryPlot.addAnnotation(localCategoryTextAnnotation14);
    CategoryTextAnnotation localCategoryTextAnnotation15 = new CategoryTextAnnotation("15. OVERALL EFFECTIVENESS.", "Overall", 0.0D);
    localCategoryTextAnnotation15.setFont(new Font("SansSerif", 1, 12));
    localCategoryTextAnnotation15.setTextAnchor(TextAnchor.BOTTOM_LEFT);
    localCategoryTextAnnotation15.setCategoryAnchor(CategoryAnchor.START);
    localCategoryPlot.addAnnotation(localCategoryTextAnnotation15);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    SurveyResultsDemo1 localSurveyResultsDemo1 = new SurveyResultsDemo1("Survey Results Demo 1");
    localSurveyResultsDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localSurveyResultsDemo1);
    localSurveyResultsDemo1.setVisible(true);
  }
}