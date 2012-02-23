/*    */ package com.dukascopy.dds2.greed.util;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.gui.ClientFormLayoutManager;
/*    */ import java.awt.AWTException;
/*    */ import java.awt.Dimension;
/*    */ import java.awt.Graphics2D;
/*    */ import java.awt.Point;
/*    */ import java.awt.Rectangle;
/*    */ import java.awt.Robot;
/*    */ import java.awt.image.BufferedImage;
/*    */ import java.io.IOException;
/*    */ import javax.swing.ImageIcon;
/*    */ import javax.swing.JComponent;
/*    */ import javax.swing.JLabel;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class PrintScreenUtilities
/*    */ {
/* 19 */   private static final Logger LOGGER = LoggerFactory.getLogger(PrintScreenUtilities.class);
/*    */ 
/*    */   public static BufferedImage printComponent(JComponent component2print) throws AWTException, IOException {
/* 22 */     Point locationOnScreen = component2print.getLocationOnScreen();
/* 23 */     Dimension size = component2print.getSize();
/*    */ 
/* 25 */     Rectangle printRectg = new Rectangle(locationOnScreen.x, locationOnScreen.y, (int)size.getWidth(), (int)size.getHeight());
/* 26 */     BufferedImage capture = new Robot().createScreenCapture(printRectg);
/* 27 */     return capture;
/*    */   }
/*    */ 
/*    */   public static BufferedImage printChartsTabbedPane() {
/* 31 */     JComponent tabbedPane = ((ClientFormLayoutManager)GreedContext.get("layoutManager")).getChartTabsAndFramesPanel();
/*    */     try {
/* 33 */       return printComponent(tabbedPane);
/*    */     } catch (AWTException e) {
/* 35 */       LOGGER.warn(e.getMessage());
/*    */     } catch (IOException e) {
/* 37 */       LOGGER.warn(e.getMessage());
/*    */     }
/* 39 */     return null;
/*    */   }
/*    */ 
/*    */   public static JLabel createScaledPanel(BufferedImage bufferedImage, int scaleConstant) {
/* 43 */     return new JLabel(new ImageIcon(resize(bufferedImage, bufferedImage.getWidth() / scaleConstant, bufferedImage.getHeight() / scaleConstant)));
/*    */   }
/*    */ 
/*    */   public static JLabel createScaledPanel(int scaleConstant) {
/* 47 */     BufferedImage bufferedImage = printChartsTabbedPane();
/* 48 */     return new JLabel(new ImageIcon(resize(bufferedImage, bufferedImage.getWidth() / scaleConstant, bufferedImage.getHeight() / scaleConstant)));
/*    */   }
/*    */ 
/*    */   private static BufferedImage resize(BufferedImage image, int width, int height) {
/* 52 */     BufferedImage resizedImage = new BufferedImage(width, height, 2);
/*    */ 
/* 54 */     Graphics2D g = resizedImage.createGraphics();
/* 55 */     g.drawImage(image, 0, 0, width, height, null);
/* 56 */     g.dispose();
/* 57 */     return resizedImage;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.PrintScreenUtilities
 * JD-Core Version:    0.6.0
 */