/*    */ package com.dukascopy.dds2.greed.gui.component.strategy;
/*    */ 
/*    */ import com.dukascopy.charts.utils.file.filter.AbstractFileFilter;
/*    */ import com.dukascopy.charts.utils.file.filter.CsvFileFilter;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableCheckBox;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class TesterFileMessagesSelectionPanel extends AbstractFileSelectionPanel
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 12 */   private static final Logger LOGGER = LoggerFactory.getLogger(AbstractFileSelectionPanel.class);
/*    */ 
/*    */   public TesterFileMessagesSelectionPanel(String checkBoxCaption, String defaultFileName, TesterParameters testerParameters) {
/* 15 */     super(checkBoxCaption, defaultFileName, testerParameters);
/*    */   }
/*    */ 
/*    */   protected AbstractFileFilter getFileFilter()
/*    */   {
/* 20 */     return new CsvFileFilter();
/*    */   }
/*    */ 
/*    */   public boolean isSelected() {
/* 24 */     return this.chbSaveToFile.isSelected();
/*    */   }
/*    */ 
/*    */   protected void setData()
/*    */   {
/* 29 */     setSelected(this.testerParameters.isSaveMessages());
/* 30 */     if (isSelected())
/* 31 */       setFile(this.testerParameters.getMessagesFile());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.TesterFileMessagesSelectionPanel
 * JD-Core Version:    0.6.0
 */