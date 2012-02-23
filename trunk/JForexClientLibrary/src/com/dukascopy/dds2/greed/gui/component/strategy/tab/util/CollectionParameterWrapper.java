/*    */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.util;
/*    */ 
/*    */ public class CollectionParameterWrapper
/*    */ {
/*    */   private Object[] values;
/*    */   private Object selectedValue;
/*    */ 
/*    */   public CollectionParameterWrapper(Object[] values, Object selectedValue)
/*    */   {
/* 19 */     this.values = values;
/* 20 */     this.selectedValue = selectedValue;
/*    */   }
/*    */ 
/*    */   public Object[] getValues() {
/* 24 */     return this.values;
/*    */   }
/*    */ 
/*    */   public Object getSelectedValue() {
/* 28 */     return this.selectedValue;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.util.CollectionParameterWrapper
 * JD-Core Version:    0.6.0
 */