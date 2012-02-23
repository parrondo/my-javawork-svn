/*    */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.parameter;
/*    */ 
/*    */ import java.util.List;
/*    */ import javax.swing.JPanel;
/*    */ 
/*    */ public class StrategyParamsPanel extends JPanel
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private List<StrategyParameterPanel> params;
/*    */ 
/*    */   public List<StrategyParameterPanel> getParams()
/*    */   {
/* 14 */     return this.params;
/*    */   }
/*    */ 
/*    */   public void setParams(List<StrategyParameterPanel> params) {
/* 18 */     this.params = params;
/*    */ 
/* 20 */     removeAll();
/*    */ 
/* 22 */     for (StrategyParameterPanel panel : params)
/* 23 */       add(panel);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.parameter.StrategyParamsPanel
 * JD-Core Version:    0.6.0
 */