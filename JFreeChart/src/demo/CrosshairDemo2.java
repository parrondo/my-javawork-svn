package demo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.CompoundBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.event.ChartProgressEvent;
import org.jfree.chart.event.ChartProgressListener;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
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

public class CrosshairDemo2 extends ApplicationFrame
{
  public CrosshairDemo2(String paramString)
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
    CrosshairDemo2 localCrosshairDemo2 = new CrosshairDemo2("JFreeChart: CrosshairDemo2.java");
    localCrosshairDemo2.pack();
    RefineryUtilities.centerFrameOnScreen(localCrosshairDemo2);
    localCrosshairDemo2.setVisible(true);
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
      return this.data.length;
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
    implements ChartChangeListener, ChartProgressListener
  {
    private static final int SERIES_COUNT = 4;
    private TimeSeriesCollection[] datasets = new TimeSeriesCollection[4];
    private TimeSeries[] series = new TimeSeries[4];
    private ChartPanel chartPanel;
    private CrosshairDemo2.DemoTableModel model;

    public MyDemoPanel()
    {
      super(new BorderLayout());
      JPanel localJPanel1 = new JPanel(new BorderLayout());
      JFreeChart localJFreeChart = createChart();
      addChart(localJFreeChart);
      this.chartPanel = new ChartPanel(localJFreeChart);
      this.chartPanel.setPreferredSize(new Dimension(600, 270));
      this.chartPanel.setDomainZoomable(true);
      this.chartPanel.setRangeZoomable(true);
      CompoundBorder localCompoundBorder = BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4), BorderFactory.createEtchedBorder());
      this.chartPanel.setBorder(localCompoundBorder);
      localJPanel1.add(this.chartPanel);
      JPanel localJPanel2 = new JPanel(new BorderLayout());
      localJPanel2.setPreferredSize(new Dimension(400, 120));
      localJPanel2.setBorder(BorderFactory.createEmptyBorder(0, 4, 4, 4));
      this.model = new CrosshairDemo2.DemoTableModel(4);
      for (int i = 0; i < 4; ++i)
      {
    	XYPlot localObject = (XYPlot)localJFreeChart.getPlot();
        this.model.setValueAt(((XYPlot)localObject).getDataset(i).getSeriesKey(0), i, 0);
        this.model.setValueAt(new Double("0.00"), i, 1);
        this.model.setValueAt(new Double("0.00"), i, 2);
        this.model.setValueAt(new Double("0.00"), i, 3);
        this.model.setValueAt(new Double("0.00"), i, 4);
        this.model.setValueAt(new Double("0.00"), i, 5);
        this.model.setValueAt(new Double("0.00"), i, 6);
      }
      JTable localJTable = new JTable(this.model);
      Object localObject = new DateCellRenderer(new SimpleDateFormat("HH:mm:ss"));
      NumberCellRenderer localNumberCellRenderer = new NumberCellRenderer();
      localJTable.getColumnModel().getColumn(1).setCellRenderer((TableCellRenderer)localObject);
      localJTable.getColumnModel().getColumn(2).setCellRenderer(localNumberCellRenderer);
      localJTable.getColumnModel().getColumn(3).setCellRenderer((TableCellRenderer)localObject);
      localJTable.getColumnModel().getColumn(4).setCellRenderer(localNumberCellRenderer);
      localJTable.getColumnModel().getColumn(5).setCellRenderer((TableCellRenderer)localObject);
      localJTable.getColumnModel().getColumn(6).setCellRenderer(localNumberCellRenderer);
      localJPanel2.add(new JScrollPane(localJTable));
      localJPanel1.add(localJPanel2, "South");
      add(localJPanel1);
    }

    private XYDataset createDataset(int paramInt1, String paramString, double paramDouble, RegularTimePeriod paramRegularTimePeriod, int paramInt2)
    {
      this.series[paramInt1] = new TimeSeries(paramString);
      RegularTimePeriod localRegularTimePeriod = paramRegularTimePeriod;
      double d = paramDouble;
      for (int i = 0; i < paramInt2; ++i)
      {
        this.series[paramInt1].add(localRegularTimePeriod, d);
        localRegularTimePeriod = localRegularTimePeriod.next();
        d *= (1.0D + (Math.random() - 0.495D) / 10.0D);
      }
      this.datasets[paramInt1] = new TimeSeriesCollection();
      this.datasets[paramInt1].addSeries(this.series[paramInt1]);
      return this.datasets[paramInt1];
    }

    public void chartChanged(ChartChangeEvent paramChartChangeEvent)
    {
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
      for (int i = 0; i < 4; ++i)
      {
        TimeSeriesDataItem localTimeSeriesDataItem;
        Number localNumber;
        this.model.setValueAt(new Long(l1), i, 1);
        int[] arrayOfInt = this.datasets[i].getSurroundingItems(0, l1);
        long l2 = 0L;
        long l3 = 0L;
        double d2 = 0.0D;
        double d3 = 0.0D;
        if (arrayOfInt[0] >= 0)
        {
          localTimeSeriesDataItem = this.series[i].getDataItem(arrayOfInt[0]);
          l2 = localTimeSeriesDataItem.getPeriod().getMiddleMillisecond();
          localNumber = localTimeSeriesDataItem.getValue();
          if (localNumber != null)
          {
            d2 = localNumber.doubleValue();
            this.model.setValueAt(new Double(d2), i, 4);
          }
          else
          {
            this.model.setValueAt(null, i, 4);
          }
          this.model.setValueAt(new Long(l2), i, 3);
        }
        else
        {
          this.model.setValueAt(new Double(0.0D), i, 4);
          this.model.setValueAt(new Double(localXYPlot.getDomainAxis().getRange().getLowerBound()), i, 3);
        }
        if (arrayOfInt[1] >= 0)
        {
          localTimeSeriesDataItem = this.series[i].getDataItem(arrayOfInt[1]);
          l3 = localTimeSeriesDataItem.getPeriod().getMiddleMillisecond();
          localNumber = localTimeSeriesDataItem.getValue();
          if (localNumber != null)
          {
            d3 = localNumber.doubleValue();
            this.model.setValueAt(new Double(d3), i, 6);
          }
          else
          {
            this.model.setValueAt(null, i, 6);
          }
          this.model.setValueAt(new Long(l3), i, 5);
        }
        else
        {
          this.model.setValueAt(new Double(0.0D), i, 6);
          this.model.setValueAt(new Double(localXYPlot.getDomainAxis().getRange().getUpperBound()), i, 5);
        }
        double d4 = 0.0D;
        if (l3 - l2 > 0L)
          d4 = d2 + (l1 - l2) / (l3 - l2) * (d3 - d2);
        else
          d4 = d2;
        this.model.setValueAt(new Double(d4), i, 2);
      }
    }

    private JFreeChart createChart()
    {
      JFreeChart localJFreeChart = ChartFactory.createTimeSeriesChart("Crosshair Demo 2", "Time of Day", "Value", null, true, true, false);
      XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
      XYDataset[] arrayOfXYDataset = new XYDataset[4];
      for (int i = 0; i < 4; ++i)
      {
        arrayOfXYDataset[i] = createDataset(i, "Series " + i, 100.0D + i * 200.0D, new Minute(), 200);
        if (i == 0)
        {
          localXYPlot.setDataset(arrayOfXYDataset[i]);
        }
        else
        {
          localXYPlot.setDataset(i, arrayOfXYDataset[i]);
          localXYPlot.setRangeAxis(i, new NumberAxis("Axis " + (i + 1)));
          localXYPlot.mapDatasetToRangeAxis(i, i);
          localXYPlot.setRenderer(i, new XYLineAndShapeRenderer(true, false));
        }
      }
      localJFreeChart.addChangeListener(this);
      localJFreeChart.addProgressListener(this);
      localXYPlot.setOrientation(PlotOrientation.VERTICAL);
      localXYPlot.setDomainCrosshairVisible(true);
      localXYPlot.setDomainCrosshairLockedOnData(false);
      localXYPlot.setRangeCrosshairVisible(false);
      ChartUtilities.applyCurrentTheme(localJFreeChart);
      return localJFreeChart;
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
      for (int i = 0; i < 4; ++i)
      {
        int j = this.series[i].getIndex(new Minute(new Date(l1)));
        if (j < 0)
          continue;
        TimeSeriesDataItem localTimeSeriesDataItem1 = this.series[i].getDataItem(Math.min(199, Math.max(0, j)));
        TimeSeriesDataItem localTimeSeriesDataItem2 = this.series[i].getDataItem(Math.max(0, j - 1));
        TimeSeriesDataItem localTimeSeriesDataItem3 = this.series[i].getDataItem(Math.min(199, j + 1));
        long l2 = localTimeSeriesDataItem1.getPeriod().getMiddleMillisecond();
        double d2 = localTimeSeriesDataItem1.getValue().doubleValue();
        long l3 = localTimeSeriesDataItem2.getPeriod().getMiddleMillisecond();
        double d3 = localTimeSeriesDataItem2.getValue().doubleValue();
        long l4 = localTimeSeriesDataItem3.getPeriod().getMiddleMillisecond();
        double d4 = localTimeSeriesDataItem3.getValue().doubleValue();
        this.model.setValueAt(new Long(l2), i, 1);
        this.model.setValueAt(new Double(d2), i, 2);
        this.model.setValueAt(new Long(l3), i, 3);
        this.model.setValueAt(new Double(d3), i, 4);
        this.model.setValueAt(new Long(l4), i, 5);
        this.model.setValueAt(new Double(d4), i, 6);
      }
    }
  }
}