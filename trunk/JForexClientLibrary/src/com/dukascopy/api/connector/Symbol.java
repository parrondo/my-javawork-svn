/*    */ package com.dukascopy.api.connector;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public class Symbol
/*    */   implements Serializable, Comparable<Object>
/*    */ {
/*    */   Instrument instrument;
/*    */ 
/*    */   public Symbol(String instrumentAsString)
/*    */   {
/* 11 */     this.instrument = Instrument.fromString(instrumentAsString);
/*    */   }
/*    */ 
/*    */   public Symbol(Instrument instrument) {
/* 15 */     this.instrument = instrument;
/*    */   }
/*    */ 
/*    */   public int compareTo(Object o)
/*    */   {
/* 20 */     if (o == null)
/* 21 */       return 1;
/* 22 */     if ((o instanceof Instrument))
/* 23 */       return this.instrument.compareTo((Instrument)o);
/* 24 */     if ((o instanceof String)) {
/* 25 */       return this.instrument.toString().compareTo((String)o);
/*    */     }
/* 27 */     return 1;
/*    */   }
/*    */ 
/*    */   public boolean equals(Object symbol)
/*    */   {
/* 33 */     if (symbol == null)
/* 34 */       return false;
/* 35 */     if ((symbol instanceof Instrument))
/* 36 */       return this.instrument.equals((Instrument)symbol);
/* 37 */     if ((symbol instanceof String)) {
/* 38 */       return this.instrument.equals(Instrument.fromString((String)symbol));
/*    */     }
/* 40 */     return false;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.api.connector.Symbol
 * JD-Core Version:    0.6.0
 */