/*    */ package com.dukascopy.dds2.calc;
/*    */ 
/*    */ import com.dukascopy.transport.common.model.type.OfferSide;
/*    */ import com.dukascopy.transport.common.model.type.PositionSide;
/*    */ import java.math.BigDecimal;
/*    */ import java.math.RoundingMode;
/*    */ 
/*    */ public class PriceUtil
/*    */ {
/* 17 */   public static final BigDecimal PIP2 = new BigDecimal("0.01");
/*    */ 
/* 19 */   public static final BigDecimal PIP4 = new BigDecimal("0.0001");
/*    */ 
/* 21 */   public static final BigDecimal BORDER = new BigDecimal("20");
/*    */ 
/* 23 */   public static final BigDecimal DEFAULT_SLIPPAGE = new BigDecimal("5");
/*    */ 
/*    */   public static BigDecimal calculatePlPipsFromPriceDifference(BigDecimal priceCurr, BigDecimal priceOpen, PositionSide positionSide, int pipScale)
/*    */   {
/* 27 */     BigDecimal difference = positionSide == PositionSide.SHORT ? priceOpen.subtract(priceCurr) : priceCurr.subtract(priceOpen);
/* 28 */     BigDecimal result = difference.movePointRight(pipScale).stripTrailingZeros();
/* 29 */     return result;
/*    */   }
/*    */ 
/*    */   public static BigDecimal pipValue(BigDecimal price)
/*    */   {
/* 49 */     return price.compareTo(BORDER) <= 0 ? PIP4 : PIP2;
/*    */   }
/*    */ 
/*    */   public static BigDecimal getDefaultSlippage(BigDecimal price)
/*    */   {
/* 60 */     return DEFAULT_SLIPPAGE.multiply(pipValue(price));
/*    */   }
/*    */ 
/*    */   /** @deprecated */
/*    */   public static BigDecimal getPriceWithPipCommission(BigDecimal price, BigDecimal pipCommission, OfferSide side)
/*    */   {
/* 73 */     BigDecimal pipValue = pipValue(price);
/* 74 */     return getPriceWithPipCommission(price, pipValue, pipCommission, side);
/*    */   }
/*    */ 
/*    */   public static BigDecimal getPriceWithPipCommission(BigDecimal price, BigDecimal pipValue, BigDecimal pipCommission, OfferSide side) {
/* 78 */     if (pipCommission == null) {
/* 79 */       return price;
/*    */     }
/*    */ 
/* 82 */     BigDecimal priceChange = pipCommission.multiply(pipValue);
/*    */     RoundingMode roundingMode;
/*    */     RoundingMode roundingMode;
/* 86 */     if (side == OfferSide.ASK) {
/* 87 */       BigDecimal newPrice = price.add(priceChange);
/* 88 */       roundingMode = RoundingMode.UP;
/*    */     } else {
/* 90 */       newPrice = price.subtract(priceChange);
/* 91 */       roundingMode = RoundingMode.DOWN;
/*    */     }
/*    */ 
/* 94 */     BigDecimal newPrice = newPrice.setScale(pipValue.scale() + 1, roundingMode);
/*    */ 
/* 96 */     return newPrice;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.dds2.calc.PriceUtil
 * JD-Core Version:    0.6.0
 */