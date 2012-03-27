package demo;

import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.xml.DatasetReader;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class XMLBarChartDemo extends ApplicationFrame
{
  public XMLBarChartDemo(String paramString)
  {
    super(paramString);
    CategoryDataset localCategoryDataset = null;
    URL localURL = super.getClass().getResource("/demo/categorydata.xml");
    try
    {
      InputStream localInputStream = localURL.openStream();
      localCategoryDataset = DatasetReader.readCategoryDatasetFromXML(localInputStream);
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
    JFreeChart localJFreeChart = ChartFactory.createBarChart("Bar Chart", "Domain", "Range", localCategoryDataset, PlotOrientation.VERTICAL, true, true, false);
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localChartPanel);
  }

  public static void main(String[] paramArrayOfString)
  {
    XMLBarChartDemo localXMLBarChartDemo = new XMLBarChartDemo("XML Bar Chart Demo");
    localXMLBarChartDemo.pack();
    RefineryUtilities.centerFrameOnScreen(localXMLBarChartDemo);
    localXMLBarChartDemo.setVisible(true);
  }
}