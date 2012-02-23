/*    */ package com.dukascopy.dds2.greed.gui.component.table;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*    */ import java.awt.Color;
/*    */ import java.awt.SystemColor;
/*    */ import java.math.BigDecimal;
/*    */ import java.text.MessageFormat;
/*    */ import javax.swing.table.DefaultTableCellRenderer;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class PriceRenderer extends DefaultTableCellRenderer
/*    */ {
/* 26 */   private static final Logger LOGGER = LoggerFactory.getLogger(PriceRenderer.class);
/*    */   private static final String NA = "N/A";
/*    */ 
/*    */   protected void setValue(Object object)
/*    */   {
/* 30 */     if (!(object instanceof BigDecimal)) {
/* 31 */       return;
/*    */     }
/* 33 */     if (object == BigDecimal.ZERO) {
/* 34 */       setText("N/A");
/* 35 */       setHorizontalAlignment(4);
/* 36 */       return;
/*    */     }
/*    */ 
/* 39 */     String[] str = GuiUtilsAndConstants.splitPriceForRendering((BigDecimal)object);
/* 40 */     Color bgColor = getBackground();
/*    */     try {
/* 42 */       setText(MessageFormat.format(bgColor.equals(SystemColor.textHighlight) ? "<html><font color=#ffffff>{0}{1}<font size=-2>{2}</font></font></html>" : "<html><font color=#000000>{0}{1}<font size=-2 color=\"gray\">{2}</font></font></html>", new Object[] { str[0], str[1], str[3] }));
/*    */     } catch (Exception ex) {
/* 44 */       LOGGER.error("Rendering error", ex);
/* 45 */       setText("N/A");
/*    */     }
/* 47 */     setHorizontalAlignment(4);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.table.PriceRenderer
 * JD-Core Version:    0.6.0
 */