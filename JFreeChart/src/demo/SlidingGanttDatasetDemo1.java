package demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.BoundedRangeModel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.renderer.category.GanttRenderer;
import org.jfree.data.gantt.GanttCategoryDataset;
import org.jfree.data.gantt.SlidingGanttCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.Layer;
import org.jfree.ui.RefineryUtilities;

public class SlidingGanttDatasetDemo1 extends ApplicationFrame
{
  public SlidingGanttDatasetDemo1(String paramString)
  {
    super(paramString);
    setDefaultCloseOperation(3);
    setContentPane(createDemoPanel());
  }

  public static JPanel createDemoPanel()
  {
    return new DemoPanel();
  }

  public static void main(String[] paramArrayOfString)
  {
    SlidingGanttDatasetDemo1 localSlidingGanttDatasetDemo1 = new SlidingGanttDatasetDemo1("JFreeChart: SlidingGanttDatasetDemo1.java");
    localSlidingGanttDatasetDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localSlidingGanttDatasetDemo1);
    localSlidingGanttDatasetDemo1.setVisible(true);
  }

  static class DemoPanel extends JPanel
    implements ChangeListener
  {
    JScrollBar scroller;
    SlidingGanttCategoryDataset dataset = new SlidingGanttCategoryDataset(createDataset(), 0, 15);

    public DemoPanel()
    {
      super(new BorderLayout());
      JFreeChart localJFreeChart = createChart(this.dataset);
      ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
      localChartPanel.setPreferredSize(new Dimension(400, 400));
      this.scroller = new JScrollBar(1, 0, 15, 0, 50);
      add(localChartPanel);
      this.scroller.getModel().addChangeListener(this);
      JPanel localJPanel = new JPanel(new BorderLayout());
      localJPanel.add(this.scroller);
      localJPanel.setBorder(BorderFactory.createEmptyBorder(66, 2, 2, 2));
      add(localJPanel, "East");
    }

    public static GanttCategoryDataset createDataset()
    {
      TaskSeries localTaskSeries = new TaskSeries("Scheduled");
      Day localDay1 = new Day();
      Day localDay2 = new Day();
      for (int i = 0; i < 50; ++i)
      {
        int j = (int)(Math.random() * 10.0D) + 1;
        for (int k = 0; k < j; ++k)
          localDay2 = (Day)localDay2.next();
        localTaskSeries.add(new Task("Task " + i, new Date(localDay1.getMiddleMillisecond()), new Date(localDay2.getMiddleMillisecond())));
        localDay1 = (Day)localDay2.next();
        localDay2 = (Day)localDay2.next();
      }
      TaskSeriesCollection localTaskSeriesCollection = new TaskSeriesCollection();
      localTaskSeriesCollection.add(localTaskSeries);
      return localTaskSeriesCollection;
    }

    private static JFreeChart createChart(SlidingGanttCategoryDataset paramSlidingGanttCategoryDataset)
    {
      JFreeChart localJFreeChart = ChartFactory.createGanttChart("Gantt Chart Demo", "Task", "Date", paramSlidingGanttCategoryDataset, true, true, false);
      CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
      Hour localHour = new Hour(1, 14, 5, 2008);
      for (int i = 0; i < 12; ++i)
      {
        IntervalMarker localObject = new IntervalMarker(localHour.getFirstMillisecond(), localHour.getLastMillisecond(), Color.lightGray);
        localCategoryPlot.addRangeMarker((Marker)localObject, Layer.BACKGROUND);
        localHour = (Hour)localHour.next().next();
      }
      localCategoryPlot.getDomainAxis().setMaximumCategoryLabelWidthRatio(10.0F);
      DateAxis localDateAxis = (DateAxis)localCategoryPlot.getRangeAxis();
      localDateAxis.setRange(DatasetUtilities.findRangeBounds(paramSlidingGanttCategoryDataset.getUnderlyingDataset(), true));
      Object localObject = (GanttRenderer)localCategoryPlot.getRenderer();
      ((GanttRenderer)localObject).setDrawBarOutline(false);
      ((GanttRenderer)localObject).setShadowVisible(false);
      return ((JFreeChart)localJFreeChart);
    }

    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      this.dataset.setFirstCategoryIndex(this.scroller.getValue());
    }
  }
}