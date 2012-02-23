/*    */ package com.dukascopy.charts.data.datacache.preloader;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.charts.data.datacache.wrapper.TimeInterval;
/*    */ 
/*    */ public class InstrumentTimeInterval extends TimeInterval
/*    */   implements IInstrumentTimeInterval
/*    */ {
/*    */   private Instrument instrument;
/*    */ 
/*    */   public InstrumentTimeInterval()
/*    */   {
/*    */   }
/*    */ 
/*    */   public InstrumentTimeInterval(Instrument instrument, long start, long end)
/*    */   {
/* 23 */     super(start, end);
/*    */ 
/* 25 */     this.instrument = instrument;
/*    */   }
/*    */ 
/*    */   public Instrument getInstrument()
/*    */   {
/* 30 */     return this.instrument;
/*    */   }
/*    */ 
/*    */   public void setInstrument(Instrument instrument)
/*    */   {
/* 35 */     this.instrument = instrument;
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 40 */     int prime = 31;
/* 41 */     int result = super.hashCode();
/* 42 */     result = 31 * result + (this.instrument == null ? 0 : this.instrument.hashCode());
/*    */ 
/* 44 */     return result;
/*    */   }
/*    */ 
/*    */   public boolean equals(Object obj)
/*    */   {
/* 49 */     if (this == obj)
/* 50 */       return true;
/* 51 */     if (!super.equals(obj))
/* 52 */       return false;
/* 53 */     if (getClass() != obj.getClass())
/* 54 */       return false;
/* 55 */     InstrumentTimeInterval other = (InstrumentTimeInterval)obj;
/* 56 */     if (this.instrument == null) {
/* 57 */       if (other.instrument != null)
/* 58 */         return false;
/* 59 */     } else if (!this.instrument.equals(other.instrument))
/* 60 */       return false;
/* 61 */     return true;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 66 */     return String.valueOf(this.instrument) + " " + super.toString();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.preloader.InstrumentTimeInterval
 * JD-Core Version:    0.6.0
 */