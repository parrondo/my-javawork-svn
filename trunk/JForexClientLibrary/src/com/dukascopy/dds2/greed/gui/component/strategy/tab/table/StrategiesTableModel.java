/*    */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.table;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyNewBean;
/*    */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*    */ import java.util.ArrayList;
/*    */ import java.util.HashMap;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import javax.swing.table.DefaultTableModel;
/*    */ 
/*    */ public class StrategiesTableModel extends DefaultTableModel
/*    */ {
/*    */   private ClientSettingsStorage settingsStorage;
/* 22 */   private List<StrategyNewBean> strategies = new ArrayList();
/* 23 */   private Map<StrategyNewBean, CommentsTableCellEditor> commentsEditorsMap = new HashMap();
/*    */ 
/*    */   public StrategiesTableModel(List<StrategyNewBean> strategies, ClientSettingsStorage settingsStorage) {
/* 26 */     this.strategies = strategies;
/* 27 */     this.settingsStorage = settingsStorage;
/*    */ 
/* 29 */     for (StrategyNewBean bean : strategies)
/* 30 */       this.commentsEditorsMap.put(bean, new CommentsTableCellEditor(bean, settingsStorage));
/*    */   }
/*    */ 
/*    */   public void insertRow(int row, Object[] rowData)
/*    */   {
/* 36 */     super.insertRow(row, rowData);
/* 37 */     this.commentsEditorsMap.put(this.strategies.get(row), new CommentsTableCellEditor((StrategyNewBean)this.strategies.get(row), this.settingsStorage));
/*    */   }
/*    */ 
/*    */   public void removeRow(int row)
/*    */   {
/* 43 */     this.commentsEditorsMap.remove(this.strategies.get(row));
/* 44 */     this.strategies.remove(row);
/*    */ 
/* 46 */     super.removeRow(row);
/*    */   }
/*    */ 
/*    */   public List<StrategyNewBean> getStrategies() {
/* 50 */     return this.strategies;
/*    */   }
/*    */ 
/*    */   public void setStrategies(List<StrategyNewBean> strategies) {
/* 54 */     this.strategies = strategies;
/*    */   }
/*    */ 
/*    */   public CommentsTableCellEditor getCommentsEditor(int row) {
/* 58 */     return (CommentsTableCellEditor)this.commentsEditorsMap.get(this.strategies.get(row));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.table.StrategiesTableModel
 * JD-Core Version:    0.6.0
 */