/*    */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.table.StrategiesTableModel;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ import java.util.Date;
/*    */ import javax.swing.Timer;
/*    */ 
/*    */ public class ElapsedTimeActionListener
/*    */   implements ActionListener
/*    */ {
/*    */   private int rowIndex;
/*    */   private StrategiesTableModel tableModel;
/*    */   private StrategyNewBean strategyBean;
/* 21 */   private Date date = new Date();
/* 22 */   private long elapsedTime = 0L;
/*    */ 
/*    */   public ElapsedTimeActionListener(int rowIndex, StrategiesTableModel tableModel, StrategyNewBean strategyBean) {
/* 25 */     this.rowIndex = rowIndex;
/* 26 */     this.tableModel = tableModel;
/* 27 */     this.strategyBean = strategyBean;
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent e)
/*    */   {
/* 32 */     Timer timer = (Timer)e.getSource();
/* 33 */     this.elapsedTime += timer.getDelay();
/* 34 */     this.date.setTime(this.elapsedTime);
/* 35 */     this.strategyBean.setDurationTime(this.date);
/* 36 */     this.tableModel.fireTableCellUpdated(this.rowIndex, 4);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.ElapsedTimeActionListener
 * JD-Core Version:    0.6.0
 */