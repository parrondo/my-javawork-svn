/*     */ package com.dukascopy.dds2.greed.util;
/*     */ 
/*     */ import com.dukascopy.dds2.calc.PriceUtil;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.RoundingMode;
/*     */ 
/*     */ public class HalfPipRounder
/*     */ {
/*  12 */   private static BigDecimal minimum = BigDecimal.ZERO;
/*     */ 
/*  41 */   public static final BigDecimal PIP3 = new BigDecimal("0.001");
/*  42 */   public static final BigDecimal PIP5 = new BigDecimal("0.00001");
/*     */ 
/*  96 */   static final BigDecimal TWO = new BigDecimal("2");
/*  97 */   static final BigDecimal FIVE = new BigDecimal("5");
/*     */ 
/*  99 */   static final BigDecimal MINUS = new BigDecimal("-1");
/* 100 */   static final BigDecimal PLUS = new BigDecimal("1");
/*     */ 
/*     */   public static BigDecimal halfPipRound(BigDecimal price)
/*     */   {
/*  16 */     BigDecimal result = null;
/*     */ 
/*  18 */     BigDecimal pipValue = PriceUtil.pipValue(price);
/*     */     BigDecimal onePipMore;
/*  23 */     if (pipValue.equals(PriceUtil.PIP4)) {
/*  24 */       int pip = 4;
/*  25 */       onePipMore = PIP5;
/*     */     }
/*     */     else
/*     */     {
/*     */       BigDecimal onePipMore;
/*  26 */       if (pipValue.equals(PriceUtil.PIP2)) {
/*  27 */         int pip = 2;
/*  28 */         onePipMore = PIP3;
/*     */       } else {
/*  30 */         throw new IllegalArgumentException("Price does not meet the requirements!");
/*     */       }
/*     */     }
/*     */     BigDecimal onePipMore;
/*     */     int pip;
/*  33 */     BigDecimal priceRound = new BigDecimal(price.toString());
/*  34 */     priceRound = priceRound.setScale(pip, RoundingMode.DOWN);
/*  35 */     BigDecimal pipCount = price.subtract(priceRound).divide(onePipMore);
/*  36 */     result = priceRound.add(new BigDecimal(closeToFive(pipCount.longValue())).multiply(onePipMore));
/*     */ 
/*  38 */     return result;
/*     */   }
/*     */ 
/*     */   public static long closeToFive(long val)
/*     */   {
/*  45 */     if ((val > 9L) || (val < 0L))
/*  46 */       throw new IllegalArgumentException(val + " - bad value");
/*  47 */     if (5L - val > 0L) {
/*  48 */       return 0L;
/*     */     }
/*  50 */     return 5L;
/*     */   }
/*     */ 
/*     */   public static BigDecimal adjustPrice(BigDecimal price, int direction)
/*     */   {
/*  56 */     BigDecimal result = null;
/*     */ 
/*  58 */     BigDecimal normalized = new BigDecimal(price.toString());
/*     */ 
/*  62 */     if (normalized.signum() == -1) {
/*  63 */       normalized = normalized.negate();
/*     */     }
/*     */ 
/*  66 */     BigDecimal pipValue = PriceUtil.pipValue(normalized);
/*     */ 
/*  68 */     BigDecimal increment = pipValue.equals(PriceUtil.PIP4) ? PIP5 : PIP3;
/*     */ 
/*  74 */     BigDecimal addOrSubstract = increment.multiply(PLUS).multiply(getDirection(direction));
/*     */     BigDecimal candidate;
/*     */     BigDecimal candidate;
/*  78 */     if (addOrSubstract.signum() >= 0) {
/*  79 */       candidate = normalized.add(addOrSubstract);
/*     */     }
/*     */     else
/*     */     {
/*     */       BigDecimal candidate;
/*  81 */       if (increment.subtract(price.subtract(normalized)).compareTo(BigDecimal.ZERO) > 0)
/*  82 */         candidate = normalized.add(addOrSubstract);
/*     */       else {
/*  84 */         candidate = normalized;
/*     */       }
/*     */     }
/*     */ 
/*  88 */     if (candidate.compareTo(minimum) > 0) {
/*  89 */       result = candidate;
/*     */     }
/*     */ 
/*  92 */     return result;
/*     */   }
/*     */ 
/*     */   static BigDecimal getDirection(int direction)
/*     */   {
/* 103 */     if (direction == 1)
/* 104 */       return PLUS;
/* 105 */     if (direction == -1) {
/* 106 */       return MINUS;
/*     */     }
/* 108 */     throw new IllegalArgumentException("Bad direction value, permitted values: 1, -1 ");
/*     */   }
/*     */ 
/*     */   public static BigDecimal getHalfPip(BigDecimal price)
/*     */   {
/* 113 */     return PriceUtil.pipValue(price).divide(TWO);
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.HalfPipRounder
 * JD-Core Version:    0.6.0
 */