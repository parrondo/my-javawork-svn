package demo;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.FontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Graphics2D;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import org.jfree.chart.JFreeChart;

public class PDFChartTransferable
  implements Transferable
{
  final DataFlavor pdfFlavor;
  private JFreeChart chart;
  private int width;
  private int height;

  public PDFChartTransferable(JFreeChart paramJFreeChart, int paramInt1, int paramInt2)
  {
    this(paramJFreeChart, paramInt1, paramInt2, true);
  }

  public PDFChartTransferable(JFreeChart paramJFreeChart, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    this.pdfFlavor = new DataFlavor("application/pdf", "PDF");
    try
    {
      this.chart = ((JFreeChart)paramJFreeChart.clone());
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      this.chart = paramJFreeChart;
    }
    this.width = paramInt1;
    this.height = paramInt2;
  }

  public DataFlavor[] getTransferDataFlavors()
  {
    return new DataFlavor[] { this.pdfFlavor };
  }

  public boolean isDataFlavorSupported(DataFlavor paramDataFlavor)
  {
    return this.pdfFlavor.equals(paramDataFlavor);
  }

  public Object getTransferData(DataFlavor paramDataFlavor)
    throws UnsupportedFlavorException, IOException
  {
    if (this.pdfFlavor.equals(paramDataFlavor))
    {
      ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
      writeChartAsPDF(localByteArrayOutputStream, this.chart, this.width, this.height, new DefaultFontMapper());
      return new ByteArrayInputStream(localByteArrayOutputStream.toByteArray());
    }
    throw new UnsupportedFlavorException(paramDataFlavor);
  }

  public static void writeChartAsPDF(ByteArrayOutputStream paramByteArrayOutputStream, JFreeChart paramJFreeChart, int paramInt1, int paramInt2, FontMapper paramFontMapper)
    throws IOException
  {
    Rectangle localRectangle = new Rectangle(paramInt1, paramInt2);
    Document localDocument = new Document(localRectangle, 50.0F, 50.0F, 50.0F, 50.0F);
    try
    {
      PdfWriter localPdfWriter = PdfWriter.getInstance(localDocument, paramByteArrayOutputStream);
      localDocument.addAuthor("JFreeChart");
      localDocument.addSubject("Demonstration");
      localDocument.open();
      PdfContentByte localPdfContentByte = localPdfWriter.getDirectContent();
      PdfTemplate localPdfTemplate = localPdfContentByte.createTemplate(paramInt1, paramInt2);
      Graphics2D localGraphics2D = localPdfTemplate.createGraphics(paramInt1, paramInt2, paramFontMapper);
      Rectangle2D.Double localDouble = new Rectangle2D.Double(0.0D, 0.0D, paramInt1, paramInt2);
      paramJFreeChart.draw(localGraphics2D, localDouble);
      localGraphics2D.dispose();
      localPdfContentByte.addTemplate(localPdfTemplate, 0.0F, 0.0F);
    }
    catch (DocumentException localDocumentException)
    {
      System.err.println(localDocumentException.getMessage());
    }
    localDocument.close();
  }
}