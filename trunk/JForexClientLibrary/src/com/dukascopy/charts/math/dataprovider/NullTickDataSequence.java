/*    */ package com.dukascopy.charts.math.dataprovider;
/*    */ 
/*    */ import com.dukascopy.charts.data.datacache.CandleData;
/*    */ import com.dukascopy.charts.data.datacache.TickData;
/*    */ import java.util.Collections;
/*    */ 
/*    */ public class NullTickDataSequence extends TickDataSequence
/*    */ {
/*    */   public NullTickDataSequence()
/*    */   {
/* 19 */     super(9223372036854775807L, 9223372036854775807L, 0, 0, new TickData[0], new long[0][], 0, 0, new CandleData[0], new CandleData[0], Collections.emptyMap(), Collections.emptyMap(), true, true);
/*    */   }
/*    */ 
/*    */   public boolean isEmpty()
/*    */   {
/* 39 */     return true;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.math.dataprovider.NullTickDataSequence
 * JD-Core Version:    0.6.0
 */