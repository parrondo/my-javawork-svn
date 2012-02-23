/*    */ package com.dukascopy.dds2.greed.gui.util.localization;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.component.JRoundedBorder;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ import javax.swing.JComponent;
/*    */ 
/*    */ public class MarketDepthHeaderBorder extends JRoundedBorder
/*    */ {
/* 22 */   private String key = null;
/* 23 */   private JComponent parent = null;
/*    */ 
/*    */   public MarketDepthHeaderBorder(JComponent parent, boolean isCommodity) {
/* 26 */     super(parent, isCommodity ? "header.marketd.thousand" : "header.marketd.mio");
/* 27 */     this.parent = parent;
/* 28 */     setIsCommodity(isCommodity);
/*    */   }
/*    */ 
/*    */   public void setIsCommodity(boolean isCommodity) {
/* 32 */     if (isCommodity)
/* 33 */       this.key = "header.marketd.thousand";
/*    */     else {
/* 35 */       this.key = "header.marketd.mio";
/*    */     }
/*    */ 
/* 38 */     setHeaderText(LocalizationManager.getText(this.key));
/* 39 */     this.parent.repaint();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.util.localization.MarketDepthHeaderBorder
 * JD-Core Version:    0.6.0
 */