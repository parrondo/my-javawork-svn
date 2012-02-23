/*    */ package com.dukascopy.calculator.utils;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*    */ import javax.swing.ImageIcon;
/*    */ 
/*    */ public class ImageHelper
/*    */ {
/*    */   public static final String DOWN_ARROW = "downarrow.png";
/*    */   public static final String RIGHT_ARROW = "rightarrow.png";
/*    */   public static final String LEFT_ARROW = "leftarrow.png";
/*    */   public static final String UP_ARROW = "uparrow.png";
/*    */   public static final String INFO = "info.png";
/*    */ 
/*    */   public static ImageIcon getImageIcon(String name, int size)
/*    */   {
/* 19 */     return StratUtils.loadImageIcon("resources/icons/" + size + name);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.utils.ImageHelper
 * JD-Core Version:    0.6.0
 */