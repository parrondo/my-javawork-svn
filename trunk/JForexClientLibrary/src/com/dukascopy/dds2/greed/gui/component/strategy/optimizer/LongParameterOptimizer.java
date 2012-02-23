/*    */ package com.dukascopy.dds2.greed.gui.component.strategy.optimizer;
/*    */ 
/*    */ import java.math.BigDecimal;
/*    */ import javax.swing.SpinnerNumberModel;
/*    */ 
/*    */ public class LongParameterOptimizer extends AbstractRangeParameterOptimizer<Long>
/*    */ {
/*    */   public LongParameterOptimizer(long value, long maximum, long stepSize, boolean mandatory, boolean readOnly)
/*    */   {
/* 20 */     super(Long.valueOf(value), Long.valueOf(maximum), Long.valueOf(stepSize), mandatory, readOnly);
/*    */   }
/*    */ 
/*    */   protected SpinnerNumberModel createSpinnerModel(Long minimum, Long maximum, Long stepSize)
/*    */   {
/* 26 */     return new SpinnerNumberModel(Long.valueOf(minimum.longValue()), minimum, maximum, Long.valueOf(stepSize.longValue()));
/*    */   }
/*    */ 
/*    */   protected String getValueFormat()
/*    */   {
/* 31 */     return "#"; } 
/*    */   protected Long stringToValue(String string) throws CommitErrorException { // Byte code:
/*    */     //   0: aload_1
/*    */     //   1: ifnull +14 -> 15
/*    */     //   4: aload_1
/*    */     //   5: invokevirtual 7	java/lang/String:trim	()Ljava/lang/String;
/*    */     //   8: invokevirtual 8	java/lang/String:length	()I
/*    */     //   11: iconst_1
/*    */     //   12: if_icmpge +5 -> 17
/*    */     //   15: aconst_null
/*    */     //   16: areturn
/*    */     //   17: aload_1
/*    */     //   18: invokevirtual 7	java/lang/String:trim	()Ljava/lang/String;
/*    */     //   21: invokestatic 9	java/lang/Long:parseLong	(Ljava/lang/String;)J
/*    */     //   24: invokestatic 1	java/lang/Long:valueOf	(J)Ljava/lang/Long;
/*    */     //   27: areturn
/*    */     //   28: astore_2
/*    */     //   29: new 11	com/dukascopy/dds2/greed/gui/component/strategy/optimizer/CommitErrorException
/*    */     //   32: dup
/*    */     //   33: ldc 12
/*    */     //   35: iconst_1
/*    */     //   36: anewarray 13	java/lang/Object
/*    */     //   39: dup
/*    */     //   40: iconst_0
/*    */     //   41: aload_1
/*    */     //   42: invokevirtual 7	java/lang/String:trim	()Ljava/lang/String;
/*    */     //   45: aastore
/*    */     //   46: invokespecial 14	com/dukascopy/dds2/greed/gui/component/strategy/optimizer/CommitErrorException:<init>	(Ljava/lang/String;[Ljava/lang/Object;)V
/*    */     //   49: athrow
/*    */     //
/*    */     // Exception table:
/*    */     //   from	to	target	type
/*    */     //   17	27	28	java/lang/NumberFormatException } 
/* 49 */   protected String valueToString(Object value) { if (value == null)
/* 50 */       return "";
/*    */     try
/*    */     {
/* 53 */       Long longValue = (Long)value;
/* 54 */       return Long.toString(longValue.longValue()); } catch (ClassCastException e) {
/*    */     }
/* 56 */     return "";
/*    */   }
/*    */ 
/*    */   protected Long decimalToValue(BigDecimal decimalValue)
/*    */   {
/* 63 */     return Long.valueOf(decimalValue.longValue());
/*    */   }
/*    */ 
/*    */   protected Long[] getValues(Long from, Long step, Long to)
/*    */   {
/* 68 */     if ((from != null) && (step != null) && (to != null)) {
/* 69 */       if (step.longValue() == 0L) {
/* 70 */         return new Long[] { from };
/*    */       }
/* 72 */       Long[] result = new Long[(int)((to.longValue() - from.longValue()) / step.longValue()) + 1];
/* 73 */       for (int i = 0; i < result.length; i++) {
/* 74 */         result[i] = Long.valueOf(from.longValue() + i * step.longValue());
/*    */       }
/* 76 */       return result;
/*    */     }
/* 78 */     if (from != null) {
/* 79 */       return new Long[] { from };
/*    */     }
/* 81 */     return null;
/*    */   }
/*    */ 
/*    */   protected Long calculateStep(Long from, Long to)
/*    */   {
/* 87 */     if ((from != null) && (to != null)) {
/* 88 */       return Long.valueOf(to.longValue() - from.longValue());
/*    */     }
/* 90 */     return Long.valueOf(0L);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.optimizer.LongParameterOptimizer
 * JD-Core Version:    0.6.0
 */