package demo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.GridLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.UIManager;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategorySeriesLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.DeviationRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.Week;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;
import org.jfree.util.Rotation;

public class ThumbnailDemo1 extends ApplicationFrame
{
  public ThumbnailDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static CategoryDataset createDataset1()
  {
    String str1 = "First";
    String str2 = "Second";
    String str3 = "Third";
    String str4 = "Category 1";
    String str5 = "Category 2";
    String str6 = "Category 3";
    String str7 = "Category 4";
    String str8 = "Category 5";
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(1.0D, str1, str4);
    localDefaultCategoryDataset.addValue(4.0D, str1, str5);
    localDefaultCategoryDataset.addValue(3.0D, str1, str6);
    localDefaultCategoryDataset.addValue(5.0D, str1, str7);
    localDefaultCategoryDataset.addValue(5.0D, str1, str8);
    localDefaultCategoryDataset.addValue(5.0D, str2, str4);
    localDefaultCategoryDataset.addValue(7.0D, str2, str5);
    localDefaultCategoryDataset.addValue(6.0D, str2, str6);
    localDefaultCategoryDataset.addValue(8.0D, str2, str7);
    localDefaultCategoryDataset.addValue(4.0D, str2, str8);
    localDefaultCategoryDataset.addValue(4.0D, str3, str4);
    localDefaultCategoryDataset.addValue(3.0D, str3, str5);
    localDefaultCategoryDataset.addValue(2.0D, str3, str6);
    localDefaultCategoryDataset.addValue(3.0D, str3, str7);
    localDefaultCategoryDataset.addValue(6.0D, str3, str8);
    return localDefaultCategoryDataset;
  }

  private static JFreeChart createChart1(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createBarChart("Bar Chart Demo 1", "Category", "Value", paramCategoryDataset, PlotOrientation.VERTICAL, true, true, false);
    localJFreeChart.setBackgroundPaint(Color.white);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    NumberAxis localNumberAxis = (NumberAxis)localCategoryPlot.getRangeAxis();
    localNumberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    BarRenderer localBarRenderer = (BarRenderer)localCategoryPlot.getRenderer();
    localBarRenderer.setDrawBarOutline(false);
    GradientPaint localGradientPaint1 = new GradientPaint(0.0F, 0.0F, Color.blue, 0.0F, 0.0F, new Color(0, 0, 64));
    GradientPaint localGradientPaint2 = new GradientPaint(0.0F, 0.0F, Color.green, 0.0F, 0.0F, new Color(0, 64, 0));
    GradientPaint localGradientPaint3 = new GradientPaint(0.0F, 0.0F, Color.red, 0.0F, 0.0F, new Color(64, 0, 0));
    localBarRenderer.setSeriesPaint(0, localGradientPaint1);
    localBarRenderer.setSeriesPaint(1, localGradientPaint2);
    localBarRenderer.setSeriesPaint(2, localGradientPaint3);
    localBarRenderer.setLegendItemToolTipGenerator(new StandardCategorySeriesLabelGenerator("Tooltip: {0}"));
    CategoryAxis localCategoryAxis = localCategoryPlot.getDomainAxis();
    localCategoryAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(0.5235987755982988D));
    return localJFreeChart;
  }

  private static PieDataset createDataset2()
  {
    DefaultPieDataset localDefaultPieDataset = new DefaultPieDataset();
    localDefaultPieDataset.setValue("Java", new Double(43.200000000000003D));
    localDefaultPieDataset.setValue("Visual Basic", new Double(10.0D));
    localDefaultPieDataset.setValue("C/C++", new Double(17.5D));
    localDefaultPieDataset.setValue("PHP", new Double(32.5D));
    localDefaultPieDataset.setValue("Perl", null);
    return localDefaultPieDataset;
  }

  private static JFreeChart createChart2(PieDataset paramPieDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createPieChart3D("Pie Chart 3D Demo 1", paramPieDataset, true, true, false);
    localJFreeChart.setBackgroundPaint(Color.white);
    PiePlot3D localPiePlot3D = (PiePlot3D)localJFreeChart.getPlot();
    localPiePlot3D.setDarkerSides(true);
    localPiePlot3D.setStartAngle(290.0D);
    localPiePlot3D.setDirection(Rotation.CLOCKWISE);
    localPiePlot3D.setForegroundAlpha(0.5F);
    localPiePlot3D.setOutlinePaint(null);
    localPiePlot3D.setNoDataMessage("No data to display");
    return localJFreeChart;
  }

  private static CategoryDataset createDataset3()
  {
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(81.0D, "Against all torture", "Italy");
    localDefaultCategoryDataset.addValue(72.0D, "Against all torture", "Great Britain");
    localDefaultCategoryDataset.addValue(58.0D, "Against all torture", "USA");
    localDefaultCategoryDataset.addValue(48.0D, "Against all torture", "Israel");
    localDefaultCategoryDataset.addValue(43.0D, "Against all torture", "Russia");
    localDefaultCategoryDataset.addValue(23.0D, "Against all torture", "India");
    localDefaultCategoryDataset.addValue(59.0D, "Against all torture", "Average (*)");
    localDefaultCategoryDataset.addValue(5.0D, "-", "Italy");
    localDefaultCategoryDataset.addValue(4.0D, "-", "Great Britain");
    localDefaultCategoryDataset.addValue(6.0D, "-", "USA");
    localDefaultCategoryDataset.addValue(9.0D, "-", "Israel");
    localDefaultCategoryDataset.addValue(20.0D, "-", "Russia");
    localDefaultCategoryDataset.addValue(45.0D, "-", "India");
    localDefaultCategoryDataset.addValue(12.0D, "-", "Average (*)");
    localDefaultCategoryDataset.addValue(14.0D, "Some degree permissible", "Italy");
    localDefaultCategoryDataset.addValue(24.0D, "Some degree permissible", "Great Britain");
    localDefaultCategoryDataset.addValue(36.0D, "Some degree permissible", "USA");
    localDefaultCategoryDataset.addValue(43.0D, "Some degree permissible", "Israel");
    localDefaultCategoryDataset.addValue(37.0D, "Some degree permissible", "Russia");
    localDefaultCategoryDataset.addValue(32.0D, "Some degree permissible", "India");
    localDefaultCategoryDataset.addValue(29.0D, "Some degree permissible", "Average (*)");
    return localDefaultCategoryDataset;
  }

  private static JFreeChart createChart3(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createStackedBarChart("Public Opinion : Torture of Prisoners", "Country", "%", paramCategoryDataset, PlotOrientation.HORIZONTAL, false, true, false);
    localJFreeChart.getTitle().setMargin(2.0D, 0.0D, 0.0D, 0.0D);
    TextTitle localTextTitle1 = new TextTitle("Source: http://news.bbc.co.uk/1/hi/world/6063386.stm", new Font("Dialog", 0, 11));
    localTextTitle1.setPosition(RectangleEdge.BOTTOM);
    localTextTitle1.setHorizontalAlignment(HorizontalAlignment.RIGHT);
    localTextTitle1.setMargin(0.0D, 0.0D, 4.0D, 4.0D);
    localJFreeChart.addSubtitle(localTextTitle1);
    TextTitle localTextTitle2 = new TextTitle("(*) Across 27,000 respondents in 25 countries", new Font("Dialog", 0, 11));
    localTextTitle2.setPosition(RectangleEdge.BOTTOM);
    localTextTitle2.setHorizontalAlignment(HorizontalAlignment.RIGHT);
    localTextTitle2.setMargin(4.0D, 0.0D, 2.0D, 4.0D);
    localJFreeChart.addSubtitle(localTextTitle2);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    LegendItemCollection localLegendItemCollection = new LegendItemCollection();
    localLegendItemCollection.add(new LegendItem("Against all torture", null, null, null, new Rectangle2D.Double(-6.0D, -3.0D, 12.0D, 6.0D), Color.green));
    localLegendItemCollection.add(new LegendItem("Some degree permissible", null, null, null, new Rectangle2D.Double(-6.0D, -3.0D, 12.0D, 6.0D), Color.red));
    localCategoryPlot.setFixedLegendItems(localLegendItemCollection);
    localCategoryPlot.setInsets(new RectangleInsets(5.0D, 5.0D, 5.0D, 20.0D));
    LegendTitle localLegendTitle = new LegendTitle(localCategoryPlot);
    localLegendTitle.setPosition(RectangleEdge.BOTTOM);
    localJFreeChart.addSubtitle(localLegendTitle);
    localCategoryPlot.setBackgroundPaint(Color.lightGray);
    localCategoryPlot.setDomainGridlinePaint(Color.white);
    localCategoryPlot.setDomainGridlinesVisible(true);
    localCategoryPlot.setRangeGridlinePaint(Color.white);
    NumberAxis localNumberAxis = (NumberAxis)localCategoryPlot.getRangeAxis();
    localNumberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    localNumberAxis.setUpperMargin(0.0D);
    BarRenderer localBarRenderer = (BarRenderer)localCategoryPlot.getRenderer();
    localBarRenderer.setDrawBarOutline(false);
    GradientPaint localGradientPaint1 = new GradientPaint(0.0F, 0.0F, Color.green, 0.0F, 0.0F, new Color(0, 64, 0));
    Color localColor = new Color(0, 0, 0, 0);
    GradientPaint localGradientPaint2 = new GradientPaint(0.0F, 0.0F, Color.red, 0.0F, 0.0F, new Color(64, 0, 0));
    localBarRenderer.setSeriesPaint(0, localGradientPaint1);
    localBarRenderer.setSeriesPaint(1, localColor);
    localBarRenderer.setSeriesPaint(2, localGradientPaint2);
    return localJFreeChart;
  }

  private static XYDataset createDataset4()
  {
    YIntervalSeries localYIntervalSeries1 = new YIntervalSeries("Series 1");
    YIntervalSeries localYIntervalSeries2 = new YIntervalSeries("Series 2");
    Object localObject = new Week();
    double d1 = 100.0D;
    double d2 = 100.0D;
    for (int i = 0; i <= 52; ++i)
    {
      double d3 = 0.05D * i;
      localYIntervalSeries1.add(((RegularTimePeriod)localObject).getFirstMillisecond(), d1, d1 - d3, d1 + d3);
      d1 = d1 + Math.random() - 0.45D;
      double d4 = 0.07000000000000001D * i;
      localYIntervalSeries2.add(((RegularTimePeriod)localObject).getFirstMillisecond(), d2, d2 - d4, d2 + d4);
      d2 = d2 + Math.random() - 0.55D;
      localObject = ((RegularTimePeriod)localObject).next();
    }
    YIntervalSeriesCollection localYIntervalSeriesCollection = new YIntervalSeriesCollection();
    localYIntervalSeriesCollection.addSeries(localYIntervalSeries1);
    localYIntervalSeriesCollection.addSeries(localYIntervalSeries2);
    return ((XYDataset)localYIntervalSeriesCollection);
  }

  private static JFreeChart createChart4(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createTimeSeriesChart("Projected Values - Test", "Date", "Index Projection", paramXYDataset, true, true, false);
    localJFreeChart.setBackgroundPaint(Color.white);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setInsets(new RectangleInsets(5.0D, 5.0D, 5.0D, 20.0D));
    localXYPlot.setBackgroundPaint(Color.lightGray);
    localXYPlot.setAxisOffset(new RectangleInsets(5.0D, 5.0D, 5.0D, 5.0D));
    localXYPlot.setDomainGridlinePaint(Color.white);
    localXYPlot.setRangeGridlinePaint(Color.white);
    DeviationRenderer localDeviationRenderer = new DeviationRenderer(true, false);
    localDeviationRenderer.setSeriesStroke(0, new BasicStroke(3.0F, 1, 1));
    localDeviationRenderer.setSeriesStroke(0, new BasicStroke(3.0F, 1, 1));
    localDeviationRenderer.setSeriesStroke(1, new BasicStroke(3.0F, 1, 1));
    localDeviationRenderer.setSeriesFillPaint(0, new Color(255, 200, 200));
    localDeviationRenderer.setSeriesFillPaint(1, new Color(200, 200, 255));
    localXYPlot.setRenderer(localDeviationRenderer);
    NumberAxis localNumberAxis = (NumberAxis)localXYPlot.getRangeAxis();
    localNumberAxis.setAutoRangeIncludesZero(false);
    localNumberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    return localJFreeChart;
  }

  private static IntervalXYDataset createDataset5()
  {
    HistogramDataset localHistogramDataset = new HistogramDataset();
    double[] arrayOfDouble = new double[1000];
    Random localRandom = new Random(12345678L);
    for (int i = 0; i < 1000; ++i)
      arrayOfDouble[i] = (localRandom.nextGaussian() + 5.0D);
    localHistogramDataset.addSeries("H1", arrayOfDouble, 100, 2.0D, 8.0D);
    arrayOfDouble = new double[1000];
    for (int i = 0; i < 1000; ++i)
      arrayOfDouble[i] = (localRandom.nextGaussian() + 7.0D);
    localHistogramDataset.addSeries("H2", arrayOfDouble, 100, 4.0D, 10.0D);
    return localHistogramDataset;
  }

  private static JFreeChart createChart5(IntervalXYDataset paramIntervalXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createHistogram("Histogram Demo 1", null, null, paramIntervalXYDataset, PlotOrientation.VERTICAL, true, true, false);
    localJFreeChart.setBackgroundPaint(Color.white);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setBackgroundPaint(Color.lightGray);
    localXYPlot.setDomainGridlinePaint(Color.white);
    localXYPlot.setRangeGridlinePaint(Color.white);
    localXYPlot.setForegroundAlpha(0.85F);
    XYBarRenderer localXYBarRenderer = (XYBarRenderer)localXYPlot.getRenderer();
    localXYBarRenderer.setDrawBarOutline(false);
    return localJFreeChart;
  }

  private static CategoryDataset createDataset6()
  {
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(212.0D, "Classes", "JDK 1.0");
    localDefaultCategoryDataset.addValue(504.0D, "Classes", "JDK 1.1");
    localDefaultCategoryDataset.addValue(1520.0D, "Classes", "SDK 1.2");
    localDefaultCategoryDataset.addValue(1842.0D, "Classes", "SDK 1.3");
    localDefaultCategoryDataset.addValue(2991.0D, "Classes", "SDK 1.4");
    return localDefaultCategoryDataset;
  }

  private static JFreeChart createChart6(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createLineChart("Java Standard Class Library", "Release", "Class Count", paramCategoryDataset, PlotOrientation.VERTICAL, false, true, false);
    localJFreeChart.addSubtitle(new TextTitle("Number of Classes By Release"));
    TextTitle localTextTitle = new TextTitle("Source: Java In A Nutshell (4th Edition) by David Flanagan (O'Reilly)");
    localTextTitle.setFont(new Font("SansSerif", 0, 10));
    localTextTitle.setPosition(RectangleEdge.BOTTOM);
    localTextTitle.setHorizontalAlignment(HorizontalAlignment.RIGHT);
    localJFreeChart.addSubtitle(localTextTitle);
    localJFreeChart.setBackgroundPaint(Color.white);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    localCategoryPlot.setBackgroundPaint(Color.lightGray);
    localCategoryPlot.setRangeGridlinePaint(Color.white);
    NumberAxis localNumberAxis = (NumberAxis)localCategoryPlot.getRangeAxis();
    localNumberAxis.setUpperMargin(0.15D);
    localNumberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    LineAndShapeRenderer localLineAndShapeRenderer = (LineAndShapeRenderer)localCategoryPlot.getRenderer();
    localLineAndShapeRenderer.setBaseShapesVisible(true);
    localLineAndShapeRenderer.setDrawOutlines(true);
    localLineAndShapeRenderer.setUseFillPaint(true);
    localLineAndShapeRenderer.setBaseFillPaint(Color.white);
    localLineAndShapeRenderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
    localLineAndShapeRenderer.setBaseItemLabelsVisible(true);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JPanel localJPanel = new JPanel(new GridLayout(2, 3));
    JFreeChart localJFreeChart1 = createChart1(createDataset1());
    ChartUtilities.applyCurrentTheme(localJFreeChart1);
    BufferedImage localBufferedImage1 = localJFreeChart1.createBufferedImage(120, 80, 360.0D, 240.0D, null);
    ImageIcon localImageIcon1 = new ImageIcon(localBufferedImage1);
    localJPanel.add(new JButton(localImageIcon1));
    JFreeChart localJFreeChart2 = createChart2(createDataset2());
    ChartUtilities.applyCurrentTheme(localJFreeChart2);
    BufferedImage localBufferedImage2 = localJFreeChart2.createBufferedImage(120, 80, 360.0D, 240.0D, null);
    ImageIcon localImageIcon2 = new ImageIcon(localBufferedImage2);
    localJPanel.add(new JButton(localImageIcon2));
    JFreeChart localJFreeChart3 = createChart3(createDataset3());
    ChartUtilities.applyCurrentTheme(localJFreeChart3);
    BufferedImage localBufferedImage3 = localJFreeChart3.createBufferedImage(120, 80, 360.0D, 240.0D, null);
    ImageIcon localImageIcon3 = new ImageIcon(localBufferedImage3);
    localJPanel.add(new JButton(localImageIcon3));
    JFreeChart localJFreeChart4 = createChart4(createDataset4());
    ChartUtilities.applyCurrentTheme(localJFreeChart4);
    BufferedImage localBufferedImage4 = localJFreeChart4.createBufferedImage(120, 80, 360.0D, 240.0D, null);
    ImageIcon localImageIcon4 = new ImageIcon(localBufferedImage4);
    localJPanel.add(new JButton(localImageIcon4));
    JFreeChart localJFreeChart5 = createChart5(createDataset5());
    ChartUtilities.applyCurrentTheme(localJFreeChart5);
    BufferedImage localBufferedImage5 = localJFreeChart5.createBufferedImage(120, 80, 360.0D, 240.0D, null);
    ImageIcon localImageIcon5 = new ImageIcon(localBufferedImage5);
    localJPanel.add(new JButton(localImageIcon5));
    JFreeChart localJFreeChart6 = createChart6(createDataset6());
    ChartUtilities.applyCurrentTheme(localJFreeChart6);
    BufferedImage localBufferedImage6 = localJFreeChart6.createBufferedImage(120, 80, 360.0D, 240.0D, null);
    ImageIcon localImageIcon6 = new ImageIcon(localBufferedImage6);
    localJPanel.add(new JButton(localImageIcon6));
    return localJPanel;
  }

  public static void main(String[] paramArrayOfString)
  {
    try
    {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    ThumbnailDemo1 localThumbnailDemo1 = new ThumbnailDemo1("JFreeChart: ThumbnailDemo1.java");
    localThumbnailDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localThumbnailDemo1);
    localThumbnailDemo1.setVisible(true);
  }
}