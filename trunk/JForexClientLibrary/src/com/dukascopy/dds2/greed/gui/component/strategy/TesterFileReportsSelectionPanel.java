/*    */ package com.dukascopy.dds2.greed.gui.component.strategy;
/*    */ 
/*    */ import com.dukascopy.charts.utils.file.filter.AbstractFileFilter;
/*    */ import com.dukascopy.charts.utils.file.filter.HtmlFileFilter;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableCheckBox;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class TesterFileReportsSelectionPanel extends AbstractFileSelectionPanel
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 11 */   private static final Logger LOGGER = LoggerFactory.getLogger(TesterFileReportsSelectionPanel.class);
/*    */ 
/*    */   public TesterFileReportsSelectionPanel(String checkBoxCaption, String defaultFileName, TesterParameters testerParameters) {
/* 14 */     super(checkBoxCaption, defaultFileName, testerParameters);
/*    */   }
/*    */ 
/*    */   protected AbstractFileFilter getFileFilter()
/*    */   {
/* 19 */     return new HtmlFileFilter();
/*    */   }
/*    */ 
/*    */   public boolean isSelected() {
/* 23 */     return this.chbSaveToFile.isSelected();
/*    */   }
/*    */ 
/*    */   protected void setData()
/*    */   {
/* 28 */     setSelected(this.testerParameters.isSaveReportFile());
/* 29 */     if (isSelected())
/* 30 */       setFile(this.testerParameters.getReportFile());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.TesterFileReportsSelectionPanel
 * JD-Core Version:    0.6.0
 */