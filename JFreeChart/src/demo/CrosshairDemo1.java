package demo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.border.CompoundBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.ChartProgressEvent;
import org.jfree.chart.event.ChartProgressListener;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;
import org.jfree.data.time.Minute;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.DateCellRenderer;
import org.jfree.ui.NumberCellRenderer;
import org.jfree.ui.RefineryUtilities;

public class CrosshairDemo1 extends ApplicationFrame
{
  public CrosshairDemo1(String paramString)
  {
    super(paramString);
    setContentPane(new MyDemoPanel());
  }

  public static JPanel createDemoPanel()
  {
    return new MyDemoPanel();
  }

  public static void main(String[] paramArrayOfString)
  {
    CrosshairDemo1 localCrosshairDemo1 = new CrosshairDemo1("JFreeChart: CrosshairDemo1.java");
    localCrosshairDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localCrosshairDemo1);
    localCrosshairDemo1.setVisible(true);
  }

  static class DemoTableModel extends AbstractTableModel
    implements TableModel
  {
    private Object[][] data;

    public DemoTableModel(int paramInt)
    {
      this.data = new Object[paramInt][7];
    }

    public int getColumnCount()
    {
      return 7;
    }

    public int getRowCount()
    {
      return 1;
    }

    public Object getValueAt(int paramInt1, int paramInt2)
    {
      return this.data[paramInt1][paramInt2];
    }

    public void setValueAt(Object paramObject, int paramInt1, int paramInt2)
    {
      this.data[paramInt1][paramInt2] = paramObject;
      fireTableDataChanged();
    }

    public String getColumnName(int paramInt)
    {
      switch (paramInt)
      {
      case 0:
        return "Series Name:";
      case 1:
        return "X:";
      case 2:
        return "Y:";
      case 3:
        return "X (prev)";
      case 4:
        return "Y (prev):";
      case 5:
        return "X (next):";
      case 6:
        return "Y (next):";
      }
      return null;
    }
  }

  private static class MyDemoPanel extends DemoPanel
    implements ChangeListener, ChartProgressListener
  {
    private TimeSeries series;
    private ChartPanel chartPanel;
    private CrosshairDemo1.DemoTableModel model;
    private JFreeChart chart = createChart();
    private JSlider slider;

    public MyDemoPanel()
    {
      super(new BorderLayout());
      addChart(this.chart);
      this.chart.addProgressListener(this);
      this.chartPanel = new ChartPanel(this.chart);
      this.chartPanel.setPreferredSize(new Dimension(600, 250));
      this.chartPanel.setDomainZoomable(true);
      this.chartPanel.setRangeZoomable(true);
      CompoundBorder localCompoundBorder = BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4), BorderFactory.createEtchedBorder());
      this.chartPanel.setBorder(localCompoundBorder);
      add(this.chartPanel);
      JPanel localJPanel = new JPanel(new BorderLayout());
      localJPanel.setPreferredSize(new Dimension(400, 80));
      localJPanel.setBorder(BorderFactory.createEmptyBorder(0, 4, 4, 4));
      this.model = new CrosshairDemo1.DemoTableModel(3);
      XYPlot localXYPlot = (XYPlot)this.chart.getPlot();
      this.model.setValueAt(localXYPlot.getDataset().getSeriesKey(0), 0, 0);
      this.model.setValueAt(new Double("0.00"), 0, 1);
      this.model.setValueAt(new Double("0.00"), 0, 2);
      this.model.setValueAt(new Double("0.00"), 0, 3);
      this.model.setValueAt(new Double("0.00"), 0, 4);
      this.model.setValueAt(new Double("0.00"), 0, 5);
      this.model.setValueAt(new Double("0.00"), 0, 6);
      JTable localJTable = new JTable(this.model);
      DateCellRenderer localDateCellRenderer = new DateCellRenderer(new SimpleDateFormat("HH:mm"));
      NumberCellRenderer localNumberCellRenderer = new NumberCellRenderer();
      localJTable.getColumnModel().getColumn(1).setCellRenderer(localDateCellRenderer);
      localJTable.getColumnModel().getColumn(2).setCellRenderer(localNumberCellRenderer);
      localJTable.getColumnModel().getColumn(3).setCellRenderer(localDateCellRenderer);
      localJTable.getColumnModel().getColumn(4).setCellRenderer(localNumberCellRenderer);
      localJTable.getColumnModel().getColumn(5).setCellRenderer(localDateCellRenderer);
      localJTable.getColumnModel().getColumn(6).setCellRenderer(localNumberCellRenderer);
      JScrollPane localJScrollPane = new JScrollPane(localJTable);
      localJPanel.add(localJScrollPane);
      this.slider = new JSlider(0, 100, 50);
      this.slider.addChangeListener(this);
      localJPanel.add(this.slider, "South");
      add(localJPanel, "South");
    }

    private JFreeChart createChart()
    {
      XYDataset localXYDataset = createDataset("Random 1", 100.0D, new Minute(), 200);
      JFreeChart localJFreeChart = ChartFactory.createTimeSeriesChart("Crosshair Demo 1", "Time of Day", "Value", localXYDataset, true, true, false);
      XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
      localXYPlot.setOrientation(PlotOrientation.VERTICAL);
      localXYPlot.setDomainCrosshairVisible(true);
      localXYPlot.setDomainCrosshairLockedOnData(false);
      localXYPlot.setRangeCrosshairVisible(false);
      return localJFreeChart;
    }

    private XYDataset createDataset(String paramString, double paramDouble, RegularTimePeriod paramRegularTimePeriod, int paramInt)
    {
      this.series = new TimeSeries(paramString);
      RegularTimePeriod localRegularTimePeriod = paramRegularTimePeriod;
      double d = paramDouble;
      for (int i = 0; i < paramInt; ++i)
      {
        this.series.add(localRegularTimePeriod, d);
        localRegularTimePeriod = localRegularTimePeriod.next();
        d *= (1.0D + (Math.random() - 0.495D) / 10.0D);
      }
      TimeSeriesCollection localTimeSeriesCollection = new TimeSeriesCollection();
      localTimeSeriesCollection.addSeries(this.series);
      return localTimeSeriesCollection;
    }

    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      int i = this.slider.getValue();
      XYPlot localXYPlot = (XYPlot)this.chart.getPlot();
      ValueAxis localValueAxis = localXYPlot.getDomainAxis();
      Range localRange = localValueAxis.getRange();
      double d = localValueAxis.getLowerBound() + i / 100.0D * localRange.getLength();
      localXYPlot.setDomainCrosshairValue(d);
    }

    public void chartProgress(ChartProgressEvent paramChartProgressEvent)
    {
      if (paramChartProgressEvent.getType() != 2)
        return;
      if (this.chartPanel == null)
        return;
      JFreeChart localJFreeChart = this.chartPanel.getChart();
      if (localJFreeChart == null)
        return;
      XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
      XYDataset localXYDataset = localXYPlot.getDataset();
      Comparable localComparable = localXYDataset.getSeriesKey(0);
      double d1 = localXYPlot.getDomainCrosshairValue();
      this.model.setValueAt(localComparable, 0, 0);
      long l1 = (long)d1;
      this.model.setValueAt(new Long(l1), 0, 1);
      int i = this.series.getIndex(new Minute(new Date(l1)));
      if (i < 0)
        return;
      TimeSeriesDataItem localTimeSeriesDataItem1 = this.series.getDataItem(Math.min(199, Math.max(0, i)));
      TimeSeriesDataItem localTimeSeriesDataItem2 = this.series.getDataItem(Math.max(0, i - 1));
      TimeSeriesDataItem localTimeSeriesDataItem3 = this.series.getDataItem(Math.min(199, i + 1));
      long l2 = localTimeSeriesDataItem1.getPeriod().getMiddleMillisecond();
      double d2 = localTimeSeriesDataItem1.getValue().doubleValue();
      long l3 = localTimeSeriesDataItem2.getPeriod().getMiddleMillisecond();
      double d3 = localTimeSeriesDataItem2.getValue().doubleValue();
      long l4 = localTimeSeriesDataItem3.getPeriod().getMiddleMillisecond();
      double d4 = localTimeSeriesDataItem3.getValue().doubleValue();
      this.model.setValueAt(new Long(l2), 0, 1);
      this.model.setValueAt(new Double(d2), 0, 2);
      this.model.setValueAt(new Long(l3), 0, 3);
      this.model.setValueAt(new Double(d3), 0, 4);
      this.model.setValueAt(new Long(l4), 0, 5);
      this.model.setValueAt(new Double(d4), 0, 6);
    }
  }
}