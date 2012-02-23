/*    */ package com.dukascopy.dds2.greed.gui.component.dialog;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableComboBox;
/*    */ import java.math.BigDecimal;
/*    */ import javax.swing.DefaultComboBoxModel;
/*    */ 
/*    */ public class JTimeUnitComboBox extends JLocalizableComboBox
/*    */ {
/* 24 */   private static DefaultComboBoxModel dcbm = new DefaultComboBoxModel();
/*    */ 
/*    */   public void translate()
/*    */   {
/* 53 */     populate();
/*    */   }
/*    */ 
/*    */   private void populate() {
/* 57 */     int selected = getSelectedIndex();
/* 58 */     dcbm.removeAllElements();
/* 59 */     for (TimeUnit tu : TimeUnit.values()) {
/* 60 */       dcbm.addElement(LocalizationManager.getText(tu.getKey()));
/*    */     }
/* 62 */     setModel(dcbm);
/* 63 */     setSelectedIndex(selected);
/*    */   }
/*    */ 
/*    */   public BigDecimal getSelectedTime() {
/* 67 */     for (TimeUnit tu : TimeUnit.values()) {
/* 68 */       if (tu.ordinal() == getSelectedIndex()) {
/* 69 */         return new BigDecimal(tu.getSeconds());
/*    */       }
/*    */     }
/* 72 */     return null;
/*    */   }
/*    */ 
/*    */   static enum TimeUnit
/*    */   {
/* 31 */     HOUR("combo.hr", 3600L), 
/* 32 */     MINUTE("combo.min", 60L);
/*    */ 
/*    */     private final String captionKey;
/*    */     private final long timeInSeconds;
/*    */ 
/* 38 */     private TimeUnit(String key, long timeInSeconds) { this.captionKey = key;
/* 39 */       this.timeInSeconds = timeInSeconds; }
/*    */ 
/*    */     protected String getKey()
/*    */     {
/* 43 */       return this.captionKey;
/*    */     }
/*    */ 
/*    */     protected long getSeconds() {
/* 47 */       return this.timeInSeconds;
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dialog.JTimeUnitComboBox
 * JD-Core Version:    0.6.0
 */