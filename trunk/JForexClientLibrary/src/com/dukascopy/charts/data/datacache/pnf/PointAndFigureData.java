/*    */ package com.dukascopy.charts.data.datacache.pnf;
/*    */ 
/*    */ import com.dukascopy.api.feed.IPointAndFigure;
/*    */ import com.dukascopy.charts.data.datacache.priceaggregation.AbstractPriceAggregationData;
/*    */ 
/*    */ public class PointAndFigureData extends AbstractPriceAggregationData
/*    */   implements IPointAndFigure
/*    */ {
/*    */   private Boolean rising;
/*    */ 
/*    */   public PointAndFigureData()
/*    */   {
/*    */   }
/*    */ 
/*    */   public PointAndFigureData(long time, long endTime, double open, double close, double low, double high, double vol, long formedElementsCount, Boolean rising)
/*    */   {
/* 25 */     super(time, endTime, open, close, low, high, vol, formedElementsCount);
/* 26 */     this.rising = rising;
/*    */   }
/*    */ 
/*    */   public Boolean isRising()
/*    */   {
/* 32 */     return this.rising;
/*    */   }
/*    */ 
/*    */   public void setRising(Boolean rising) {
/* 36 */     this.rising = rising;
/*    */   }
/*    */ 
/*    */   public PointAndFigureData clone()
/*    */   {
/* 41 */     return (PointAndFigureData)super.clone();
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 46 */     String pf = new StringBuilder().append("P&F: ").append(Boolean.TRUE.equals(isRising()) ? "X" : isRising() == null ? "-" : "O").toString();
/* 47 */     return new StringBuilder().append(super.toString()).append(" ").append(pf).toString();
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 52 */     int prime = 31;
/* 53 */     int result = super.hashCode();
/* 54 */     result = 31 * result + (this.rising == null ? 0 : this.rising.hashCode());
/* 55 */     return result;
/*    */   }
/*    */ 
/*    */   public boolean equals(Object obj)
/*    */   {
/* 60 */     if (this == obj)
/* 61 */       return true;
/* 62 */     if (!super.equals(obj))
/* 63 */       return false;
/* 64 */     if (getClass() != obj.getClass())
/* 65 */       return false;
/* 66 */     PointAndFigureData other = (PointAndFigureData)obj;
/* 67 */     if (this.rising == null) {
/* 68 */       if (other.rising != null)
/* 69 */         return false;
/* 70 */     } else if (!this.rising.equals(other.rising))
/* 71 */       return false;
/* 72 */     return true;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.pnf.PointAndFigureData
 * JD-Core Version:    0.6.0
 */