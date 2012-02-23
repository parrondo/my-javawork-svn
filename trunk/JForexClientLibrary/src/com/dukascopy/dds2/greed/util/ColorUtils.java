/*    */ package com.dukascopy.dds2.greed.util;
/*    */ 
/*    */ import java.awt.Color;
/*    */ 
/*    */ public final class ColorUtils
/*    */ {
/*    */   public static boolean isVisible(Color colorX, Color colorY)
/*    */   {
/* 13 */     if (colorX == null) {
/* 14 */       return false;
/*    */     }
/*    */ 
/* 17 */     if (colorY == null) {
/* 18 */       return true;
/*    */     }
/*    */ 
/* 21 */     return (Math.abs(colorX.getRed() - colorY.getRed()) > 50) || (Math.abs(colorX.getGreen() - colorY.getGreen()) > 50) || (Math.abs(colorX.getBlue() - colorY.getBlue()) > 50);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.ColorUtils
 * JD-Core Version:    0.6.0
 */