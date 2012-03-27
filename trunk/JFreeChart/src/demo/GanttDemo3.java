package demo;

import java.awt.Dimension;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.GanttRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;

public class GanttDemo3 extends ApplicationFrame
{
  public GanttDemo3(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 370));
    setContentPane(localJPanel);
  }

  public static IntervalCategoryDataset createDataset()
  {
    TaskSeries localTaskSeries1 = new TaskSeries("Scheduled");
    localTaskSeries1.add(new Task("Write Proposal", new SimpleTimePeriod(date(1, 3, 2001), date(5, 3, 2001))));
    localTaskSeries1.add(new Task("Obtain Approval", new SimpleTimePeriod(date(9, 3, 2001), date(9, 3, 2001))));
    localTaskSeries1.add(new Task("Requirements Analysis", new SimpleTimePeriod(date(10, 3, 2001), date(5, 4, 2001))));
    localTaskSeries1.add(new Task("Design Phase", new SimpleTimePeriod(date(6, 4, 2001), date(30, 4, 2001))));
    localTaskSeries1.add(new Task("Design Signoff", new SimpleTimePeriod(date(2, 5, 2001), date(2, 5, 2001))));
    localTaskSeries1.add(new Task("Alpha Implementation", new SimpleTimePeriod(date(3, 5, 2001), date(31, 6, 2001))));
    localTaskSeries1.add(new Task("Design Review", new SimpleTimePeriod(date(1, 7, 2001), date(8, 7, 2001))));
    localTaskSeries1.add(new Task("Revised Design Signoff", new SimpleTimePeriod(date(10, 7, 2001), date(10, 7, 2001))));
    localTaskSeries1.add(new Task("Beta Implementation", new SimpleTimePeriod(date(12, 7, 2001), date(12, 8, 2001))));
    localTaskSeries1.add(new Task("Testing", new SimpleTimePeriod(date(13, 8, 2001), date(31, 9, 2001))));
    localTaskSeries1.add(new Task("Final Implementation", new SimpleTimePeriod(date(1, 10, 2001), date(15, 10, 2001))));
    localTaskSeries1.add(new Task("Signoff", new SimpleTimePeriod(date(28, 10, 2001), date(30, 10, 2001))));
    TaskSeries localTaskSeries2 = new TaskSeries("Actual");
    localTaskSeries2.add(new Task("Write Proposal", new SimpleTimePeriod(date(1, 3, 2001), date(5, 3, 2001))));
    localTaskSeries2.add(new Task("Obtain Approval", new SimpleTimePeriod(date(9, 3, 2001), date(9, 3, 2001))));
    localTaskSeries2.add(new Task("Requirements Analysis", new SimpleTimePeriod(date(10, 3, 2001), date(15, 4, 2001))));
    localTaskSeries2.add(new Task("Design Phase", new SimpleTimePeriod(date(15, 4, 2001), date(17, 5, 2001))));
    localTaskSeries2.add(new Task("Design Signoff", new SimpleTimePeriod(date(30, 5, 2001), date(30, 5, 2001))));
    localTaskSeries2.add(new Task("Alpha Implementation", new SimpleTimePeriod(date(1, 6, 2001), date(12, 8, 2001))));
    localTaskSeries2.add(new Task("Design Review", new SimpleTimePeriod(date(12, 8, 2001), date(22, 8, 2001))));
    localTaskSeries2.add(new Task("Revised Design Signoff", new SimpleTimePeriod(date(25, 8, 2001), date(27, 8, 2001))));
    localTaskSeries2.add(new Task("Beta Implementation", new SimpleTimePeriod(date(27, 8, 2001), date(30, 9, 2001))));
    localTaskSeries2.add(new Task("Testing", new SimpleTimePeriod(date(31, 9, 2001), date(17, 10, 2001))));
    localTaskSeries2.add(new Task("Final Implementation", new SimpleTimePeriod(date(18, 10, 2001), date(5, 11, 2001))));
    localTaskSeries2.add(new Task("Signoff", new SimpleTimePeriod(date(10, 11, 2001), date(11, 11, 2001))));
    TaskSeriesCollection localTaskSeriesCollection = new TaskSeriesCollection();
    localTaskSeriesCollection.add(localTaskSeries1);
    localTaskSeriesCollection.add(localTaskSeries2);
    return localTaskSeriesCollection;
  }

  private static Date date(int paramInt1, int paramInt2, int paramInt3)
  {
    Calendar localCalendar = Calendar.getInstance();
    localCalendar.set(paramInt3, paramInt2, paramInt1);
    Date localDate = localCalendar.getTime();
    return localDate;
  }

  private static JFreeChart createChart(IntervalCategoryDataset paramIntervalCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createGanttChart("Gantt Chart Demo", "Task", "Date", paramIntervalCategoryDataset, true, true, false);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    localCategoryPlot.setRangePannable(true);
    localCategoryPlot.getDomainAxis().setMaximumCategoryLabelWidthRatio(10.0F);
    DateAxis localDateAxis = (DateAxis)localCategoryPlot.getRangeAxis();
    localDateAxis.setUpperMargin(0.2D);
    GanttRenderer localGanttRenderer = (GanttRenderer)localCategoryPlot.getRenderer();
    localGanttRenderer.setDrawBarOutline(false);
    localGanttRenderer.setBaseItemLabelGenerator(new MyLabelGenerator(new SimpleDateFormat("d-MMM")));
    localGanttRenderer.setBaseItemLabelsVisible(true);
    localGanttRenderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE3, TextAnchor.CENTER_LEFT));
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
    GanttDemo3 localGanttDemo3 = new GanttDemo3("JFreeChart: GanttDemo3.java");
    localGanttDemo3.pack();
    RefineryUtilities.centerFrameOnScreen(localGanttDemo3);
    localGanttDemo3.setVisible(true);
  }

  static class MyLabelGenerator
    implements CategoryItemLabelGenerator
  {
    DateFormat df;

    public MyLabelGenerator(DateFormat paramDateFormat)
    {
      this.df = paramDateFormat;
    }

    public String generateLabel(CategoryDataset paramCategoryDataset, int paramInt1, int paramInt2)
    {
      Number localNumber = null;
      if (paramCategoryDataset instanceof IntervalCategoryDataset)
      {
        IntervalCategoryDataset localIntervalCategoryDataset = (IntervalCategoryDataset)paramCategoryDataset;
        localNumber = localIntervalCategoryDataset.getEndValue(paramInt1, paramInt2);
      }
      else
      {
        localNumber = paramCategoryDataset.getValue(paramInt1, paramInt2);
      }
      if (localNumber == null)
        return "null";
      long l = localNumber.longValue();
      Date localDate = new Date(l);
      return this.df.format(localDate);
    }

    public String generateColumnLabel(CategoryDataset paramCategoryDataset, int paramInt)
    {
      return paramCategoryDataset.getColumnKey(paramInt).toString();
    }

    public String generateRowLabel(CategoryDataset paramCategoryDataset, int paramInt)
    {
      return paramCategoryDataset.getRowKey(paramInt).toString();
    }
  }
}