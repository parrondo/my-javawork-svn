/*    */ package com.dukascopy.charts.main.nulls;
/*    */ 
/*    */ import com.dukascopy.api.IChartObject;
/*    */ import java.util.Iterator;
/*    */ 
/*    */ class NullIChartIterator
/*    */   implements Iterator<IChartObject>
/*    */ {
/*    */   public boolean hasNext()
/*    */   {
/* 10 */     return false;
/*    */   }
/*    */ 
/*    */   public IChartObject next() {
/* 14 */     return null;
/*    */   }
/*    */ 
/*    */   public void remove()
/*    */   {
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.main.nulls.NullIChartIterator
 * JD-Core Version:    0.6.0
 */