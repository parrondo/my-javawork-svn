package demo;

import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.text.NumberFormat;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.PieDataset;
import org.jfree.data.xml.DatasetReader;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class XMLPieChartDemo extends ApplicationFrame
{
  public XMLPieChartDemo(String paramString)
  {
    super(paramString);
    PieDataset localPieDataset = null;
    URL localURL = super.getClass().getResource("/org/jfree/chart/demo/piedata.xml");
    try
    {
      InputStream localInputStream = localURL.openStream();
      localPieDataset = DatasetReader.readPieDatasetFromXML(localInputStream);
    }
    catch (IOException localIOException)
    {
      System.out.println(localIOException.getMessage());
    }
    JFreeChart localJFreeChart = ChartFactory.createPieChart("Pie Chart Demo 1", localPieDataset, true, true, false);
    localJFreeChart.setBackgroundPaint(Color.yellow);
    PiePlot localPiePlot = (PiePlot)localJFreeChart.getPlot();
    localPiePlot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} = {2}", NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance()));
    localPiePlot.setNoDataMessage("No data available");
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localChartPanel);
  }

  public static void main(String[] paramArrayOfString)
  {
    XMLPieChartDemo localXMLPieChartDemo = new XMLPieChartDemo("XML Pie Chart Demo");
    localXMLPieChartDemo.pack();
    RefineryUtilities.centerFrameOnScreen(localXMLPieChartDemo);
    localXMLPieChartDemo.setVisible(true);
  }
}