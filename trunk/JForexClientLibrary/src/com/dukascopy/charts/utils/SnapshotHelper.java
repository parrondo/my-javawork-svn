/*    */ package com.dukascopy.charts.utils;
/*    */ 
/*    */ import java.awt.Component;
/*    */ import java.awt.Graphics2D;
/*    */ import java.awt.Image;
/*    */ import java.awt.Toolkit;
/*    */ import java.awt.datatransfer.Clipboard;
/*    */ import java.awt.datatransfer.ClipboardOwner;
/*    */ import java.awt.datatransfer.DataFlavor;
/*    */ import java.awt.datatransfer.Transferable;
/*    */ import java.awt.datatransfer.UnsupportedFlavorException;
/*    */ import java.awt.image.BufferedImage;
/*    */ import java.awt.image.RenderedImage;
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import javax.imageio.ImageIO;
/*    */ import javax.swing.JComponent;
/*    */ 
/*    */ public final class SnapshotHelper
/*    */ {
/*    */   public static void saveImageToClipboard(Image image, Component activeChartWindow)
/*    */   {
/* 17 */     Clipboard clipboard = activeChartWindow.getToolkit().getSystemClipboard();
/* 18 */     clipboard.setContents(new Transferable(image) {
/*    */       public DataFlavor[] getTransferDataFlavors() {
/* 20 */         return new DataFlavor[] { DataFlavor.imageFlavor };
/*    */       }
/*    */ 
/*    */       public boolean isDataFlavorSupported(DataFlavor flavor) {
/* 24 */         return DataFlavor.imageFlavor.equals(flavor);
/*    */       }
/*    */ 
/*    */       public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
/* 28 */         if (!isDataFlavorSupported(flavor)) {
/* 29 */           throw new UnsupportedFlavorException(flavor);
/*    */         }
/* 31 */         return this.val$image;
/*    */       }
/*    */     }
/*    */     , new ClipboardOwner()
/*    */     {
/*    */       public void lostOwnership(Clipboard clipboard, Transferable contents)
/*    */       {
/*    */       }
/*    */     });
/*    */   }
/*    */ 
/*    */   public static void saveImageToAFile(RenderedImage image, File file)
/*    */     throws IOException
/*    */   {
/* 44 */     String absolutePath = file.getAbsolutePath();
/* 45 */     int indexOfExtension = absolutePath.lastIndexOf(".");
/* 46 */     if (indexOfExtension == -1)
/* 47 */       file = new File(absolutePath + ".png");
/* 48 */     else if (!"png".equalsIgnoreCase(absolutePath.substring(indexOfExtension + 1))) {
/* 49 */       file = new File(absolutePath + ".png");
/*    */     }
/*    */ 
/* 52 */     ImageIO.write(image, "png", file);
/*    */   }
/*    */ 
/*    */   public static BufferedImage writeToImage(JComponent activeChartWindow)
/*    */   {
/* 59 */     BufferedImage image = new BufferedImage(activeChartWindow.getWidth(), activeChartWindow.getHeight(), 1);
/* 60 */     Graphics2D gImg = image.createGraphics();
/*    */     try
/*    */     {
/* 63 */       activeChartWindow.paint(gImg);
/*    */     } finally {
/* 65 */       gImg.dispose();
/*    */     }
/*    */ 
/* 68 */     return image;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.utils.SnapshotHelper
 * JD-Core Version:    0.6.0
 */