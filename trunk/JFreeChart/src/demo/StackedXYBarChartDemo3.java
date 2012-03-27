package demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StackedXYBarRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.TimeTableXYDataset;
import org.jfree.data.time.Year;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.GradientPaintTransformType;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.StandardGradientPaintTransformer;

public class StackedXYBarChartDemo3 extends ApplicationFrame
{
  public StackedXYBarChartDemo3(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static TableXYDataset createDataset()
  {
    TimeTableXYDataset localTimeTableXYDataset = new TimeTableXYDataset();
    localTimeTableXYDataset.add(new Year(2002), 41287.0D, "Landfilled");
    localTimeTableXYDataset.add(new Year(2003), 41096.0D, "Landfilled");
    localTimeTableXYDataset.add(new Year(2004), 39603.0D, "Landfilled");
    localTimeTableXYDataset.add(new Year(2005), 39693.0D, "Landfilled");
    localTimeTableXYDataset.add(new Year(2006), 37227.0D, "Landfilled");
    localTimeTableXYDataset.add(new Year(2002), 7717.0D, "Recycled");
    localTimeTableXYDataset.add(new Year(2003), 8317.0D, "Recycled");
    localTimeTableXYDataset.add(new Year(2004), 9493.0D, "Recycled");
    localTimeTableXYDataset.add(new Year(2005), 11228.0D, "Recycled");
    localTimeTableXYDataset.add(new Year(2006), 14941.0D, "Recycled");
    return localTimeTableXYDataset;
  }

  private static JFreeChart createChart(TableXYDataset paramTableXYDataset)
  {
    DateAxis localDateAxis = new DateAxis("Year");
    localDateAxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
    localDateAxis.setLowerMargin(0.01D);
    localDateAxis.setUpperMargin(0.01D);
    NumberAxis localNumberAxis = new NumberAxis("Tonnes");
    localNumberAxis.setNumberFormatOverride(new DecimalFormat("0.0%"));
    StackedXYBarRenderer localStackedXYBarRenderer = new StackedXYBarRenderer(0.3D);
    localStackedXYBarRenderer.setRenderAsPercentages(true);
    localStackedXYBarRenderer.setDrawBarOutline(false);
    localStackedXYBarRenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator("{0} : {1} = {2} tonnes", new SimpleDateFormat("yyyy"), new DecimalFormat("#,##0")));
    XYPlot localXYPlot = new XYPlot(paramTableXYDataset, localDateAxis, localNumberAxis, localStackedXYBarRenderer);
    localXYPlot.setBackgroundPaint(Color.lightGray);
    localXYPlot.setDomainGridlinePaint(Color.white);
    localXYPlot.setRangeGridlinePaint(Color.white);
    JFreeChart localJFreeChart = new JFreeChart("Waste Management", localXYPlot);
    localJFreeChart.setBackgroundPaint(Color.white);
    localJFreeChart.addSubtitle(new TextTitle("St Albans City and District Council"));
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    GradientPaint localGradientPaint1 = new GradientPaint(0.0F, 0.0F, new Color(64, 0, 0), 0.0F, 0.0F, Color.red);
    GradientPaint localGradientPaint2 = new GradientPaint(0.0F, 0.0F, new Color(0, 64, 0), 0.0F, 0.0F, Color.green);
    localStackedXYBarRenderer.setSeriesPaint(0, localGradientPaint1);
    localStackedXYBarRenderer.setSeriesPaint(1, localGradientPaint2);
    localStackedXYBarRenderer.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.HORIZONTAL));
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    StackedXYBarChartDemo3 localStackedXYBarChartDemo3 = new StackedXYBarChartDemo3("JFreeChart: StackedXYBarChartDemo3");
    localStackedXYBarChartDemo3.pack();
    RefineryUtilities.centerFrameOnScreen(localStackedXYBarChartDemo3);
    localStackedXYBarChartDemo3.setVisible(true);
  }
}