/*    */ package com.dukascopy.charts.utils;
/*    */ 
/*    */ import java.awt.Component;
/*    */ import java.awt.Graphics;
/*    */ import java.awt.Graphics2D;
/*    */ import java.awt.print.PageFormat;
/*    */ import java.awt.print.Printable;
/*    */ import java.awt.print.PrinterException;
/*    */ import java.awt.print.PrinterJob;
/*    */ import javax.swing.JOptionPane;
/*    */ import javax.swing.RepaintManager;
/*    */ import javax.swing.SwingUtilities;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public final class PrintUtilities
/*    */   implements Printable
/*    */ {
/* 12 */   private static final Logger LOGGER = LoggerFactory.getLogger(PrintUtilities.class);
/*    */   private Component componentToBePrinted;
/*    */ 
/*    */   public static void printComponent(Component c)
/*    */   {
/* 17 */     new PrintUtilities(c).print();
/*    */   }
/*    */ 
/*    */   public PrintUtilities(Component componentToBePrinted) {
/* 21 */     this.componentToBePrinted = componentToBePrinted;
/*    */   }
/*    */ 
/*    */   public void print() {
/* 25 */     PrinterJob printJob = PrinterJob.getPrinterJob();
/* 26 */     printJob.setPrintable(this);
/* 27 */     if (!printJob.printDialog())
/* 28 */       return;
/*    */     try
/*    */     {
/* 31 */       printJob.print();
/*    */     } catch (PrinterException pe) {
/* 33 */       if (SwingUtilities.isEventDispatchThread())
/* 34 */         JOptionPane.showMessageDialog(this.componentToBePrinted, pe.getMessage(), "Failed to print...", 2);
/*    */       else
/* 36 */         LOGGER.warn("Error printing: " + pe);
/*    */     }
/*    */   }
/*    */ 
/*    */   public int print(Graphics g, PageFormat pageFormat, int pageIndex)
/*    */   {
/* 42 */     if (pageIndex > 0) {
/* 43 */       return 1;
/*    */     }
/* 45 */     Graphics2D g2d = (Graphics2D)g;
/* 46 */     g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
/* 47 */     disableDoubleBuffering(this.componentToBePrinted);
/* 48 */     this.componentToBePrinted.paint(g2d);
/* 49 */     enableDoubleBuffering(this.componentToBePrinted);
/* 50 */     return 0;
/*    */   }
/*    */ 
/*    */   public static void disableDoubleBuffering(Component c)
/*    */   {
/* 55 */     RepaintManager currentManager = RepaintManager.currentManager(c);
/* 56 */     currentManager.setDoubleBufferingEnabled(false);
/*    */   }
/*    */ 
/*    */   public static void enableDoubleBuffering(Component c) {
/* 60 */     RepaintManager currentManager = RepaintManager.currentManager(c);
/* 61 */     currentManager.setDoubleBufferingEnabled(true);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.utils.PrintUtilities
 * JD-Core Version:    0.6.0
 */