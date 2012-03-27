package demo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

public class PerformanceTest1 extends ApplicationFrame {
	private TimeSeries timings = new TimeSeries("Timings");

	public PerformanceTest1(String paramString) {
		super(paramString);
		TimeSeriesCollection localTimeSeriesCollection = new TimeSeriesCollection(
				this.timings);
		JFreeChart localJFreeChart = createChart(localTimeSeriesCollection);
		ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
		localChartPanel.setPreferredSize(new Dimension(500, 270));
		localChartPanel.setMouseZoomable(true);
		setContentPane(localChartPanel);
	}

	private static JFreeChart createChart(XYDataset paramXYDataset) {
		JFreeChart localJFreeChart = ChartFactory.createTimeSeriesChart(
				"Performance Test 1", "Time", "Milliseconds", paramXYDataset,
				true, true, false);
		localJFreeChart.setBackgroundPaint(Color.white);
		XYPlot localXYPlot = (XYPlot) localJFreeChart.getPlot();
		localXYPlot.setBackgroundPaint(Color.lightGray);
		localXYPlot.setDomainGridlinePaint(Color.white);
		localXYPlot.setRangeGridlinePaint(Color.white);
		localXYPlot.setAxisOffset(new RectangleInsets(5.0D, 5.0D, 5.0D, 5.0D));
		localXYPlot.setDomainCrosshairVisible(true);
		localXYPlot.setRangeCrosshairVisible(true);
		XYItemRenderer localXYItemRenderer = localXYPlot.getRenderer();
		if (localXYItemRenderer instanceof StandardXYItemRenderer) {
			StandardXYItemRenderer localStandardXYItemRenderer = (StandardXYItemRenderer) localXYItemRenderer;
			localStandardXYItemRenderer.setSeriesStroke(0,
					new BasicStroke(1.1F));
		}
		return localJFreeChart;
	}

	public static JPanel createDemoPanel() {
		JFreeChart localJFreeChart = createChart(null);
		return new ChartPanel(localJFreeChart);
	}

	public void addObservation(long paramLong) {
		this.timings.addOrUpdate(new Millisecond(), paramLong);
	}

	public static void main2(String[] paramArrayOfString) {
		PerformanceTest1 localPerformanceTest1 = new PerformanceTest1(
				"Performance Test 1");
		localPerformanceTest1.pack();
		RefineryUtilities.centerFrameOnScreen(localPerformanceTest1);
		localPerformanceTest1.setVisible(true);
		TimeSeries localTimeSeries = new TimeSeries("Test");
		localTimeSeries.setMaximumItemAge(200L);
		while (true) {
			Millisecond localMillisecond = new Millisecond();
			long l1 = System.currentTimeMillis();
			for (int i = 0; i < 200; ++i) {
				localMillisecond = (Millisecond) localMillisecond.next();
				localTimeSeries.addOrUpdate(localMillisecond, 1.0D);
			}
			long l2 = System.currentTimeMillis();
			localPerformanceTest1.addObservation(l2 - l1);
		}
	}

	public static void main4(String[] paramArrayOfString) {
		TimeSeries localTimeSeries = new TimeSeries("Test");
		localTimeSeries.setMaximumItemCount(4000);
		FixedMillisecond localFixedMillisecond = new FixedMillisecond();
		for (int i = 0; i < 40000; ++i) {
			long l1 = System.currentTimeMillis();
			for (int j = 0; j < 400; ++j) {
				localFixedMillisecond = (FixedMillisecond) localFixedMillisecond
						.next();
				localTimeSeries.add(localFixedMillisecond, Math.random());
			}
			long l2 = System.currentTimeMillis();
			System.out.println(i + " --> " + (l2 - l1) + " ("
					+ Runtime.getRuntime().freeMemory() + " / "
					+ Runtime.getRuntime().totalMemory() + ")");
		}
	}

	public static void main5(String[] paramArrayOfString) {
		XYSeries localXYSeries = new XYSeries("Test");
		localXYSeries.setMaximumItemCount(4000);
		int i = 0;
		for (int j = 0; j < 40000; ++j) {
			long l1 = System.currentTimeMillis();
			for (int k = 0; k < 4000; ++k)
				localXYSeries.add(i++, Math.random());
			long l2 = System.currentTimeMillis();
			System.out.println(j + " --> " + (l2 - l1) + " ("
					+ Runtime.getRuntime().freeMemory() + " / "
					+ Runtime.getRuntime().totalMemory() + ")");
		}
	}

	public static void main(String[] paramArrayOfString) {
		ArrayList localArrayList = new ArrayList();
		for (int i = 0; i < 4000; ++i)
			localArrayList.add(new Double(Math.random()));
		int i = 0;
		for (int j = 0; j < 20000; ++j) {
			long l1 = System.currentTimeMillis();
			for (int k = 0; k < 1000000; ++k)
				i += k;
			long l2 = System.currentTimeMillis();
			System.out.println(j + " --> " + (l2 - l1) + " ("
					+ Runtime.getRuntime().freeMemory() + " / "
					+ Runtime.getRuntime().totalMemory() + ")");
		}
	}

	public static void main3(String[] paramArrayOfString) {
		ArrayList localArrayList = new ArrayList();
		Millisecond localMillisecond = new Millisecond();
		for (int i = 0; i < 200; ++i) {
			localMillisecond = (Millisecond) localMillisecond.next();
			localArrayList.add(localMillisecond);
		}
		for (int i = 0; i < 2000; ++i) {
			long l1 = System.currentTimeMillis();
			Collections.binarySearch(localArrayList, new Millisecond());
			long l2 = System.currentTimeMillis();
			System.out.println(i + " --> " + (l2 - l1) + " ("
					+ Runtime.getRuntime().freeMemory() + " / "
					+ Runtime.getRuntime().totalMemory() + ")");
		}
	}
}