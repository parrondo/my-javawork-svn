/*    */ package com.dukascopy.dds2.greed.gui.component.strategy.optimizer;
/*    */ 
/*    */ import java.math.BigDecimal;
/*    */ import javax.swing.SpinnerNumberModel;
/*    */ 
/*    */ public class IntegerParameterOptimizer extends AbstractRangeParameterOptimizer<Integer>
/*    */ {
/*    */   public IntegerParameterOptimizer(int value, int maximum, int stepSize, boolean mandatory, boolean readOnly)
/*    */   {
/* 20 */     super(Integer.valueOf(value), Integer.valueOf(maximum), Integer.valueOf(stepSize), mandatory, readOnly);
/*    */   }
/*    */ 
/*    */   protected SpinnerNumberModel createSpinnerModel(Integer minimum, Integer maximum, Integer stepSize)
/*    */   {
/* 25 */     return new SpinnerNumberModel(minimum.intValue(), minimum.intValue(), maximum.intValue(), stepSize.intValue());
/*    */   }
/*    */ 
/*    */   protected String getValueFormat()
/*    */   {
/* 30 */     return "#"; } 
/*    */   protected Integer stringToValue(String string) throws CommitErrorException { // Byte code:
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
/*    */     //   21: invokestatic 9	java/lang/Integer:parseInt	(Ljava/lang/String;)I
/*    */     //   24: invokestatic 1	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
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
/* 48 */   protected String valueToString(Object value) { if (value == null)
/* 49 */       return "";
/*    */     try
/*    */     {
/* 52 */       Integer intValue = (Integer)value;
/* 53 */       return Integer.toString(intValue.intValue()); } catch (ClassCastException e) {
/*    */     }
/* 55 */     return "";
/*    */   }
/*    */ 
/*    */   protected Integer decimalToValue(BigDecimal decimalValue)
/*    */   {
/* 62 */     return Integer.valueOf(decimalValue.intValue());
/*    */   }
/*    */ 
/*    */   protected Integer[] getValues(Integer from, Integer step, Integer to)
/*    */   {
/* 67 */     if ((from != null) && (step != null) && (to != null)) {
/* 68 */       if (step.intValue() == 0) {
/* 69 */         return new Integer[] { from };
/*    */       }
/* 71 */       Integer[] result = new Integer[(to.intValue() - from.intValue()) / step.intValue() + 1];
/* 72 */       for (int i = 0; i < result.length; i++) {
/* 73 */         result[i] = Integer.valueOf(from.intValue() + i * step.intValue());
/*    */       }
/* 75 */       return result;
/*    */     }
/* 77 */     if (from != null) {
/* 78 */       return new Integer[] { from };
/*    */     }
/* 80 */     return null;
/*    */   }
/*    */ 
/*    */   protected Integer calculateStep(Integer from, Integer to)
/*    */   {
/* 86 */     if ((from != null) && (to != null)) {
/* 87 */       return Integer.valueOf(to.intValue() - from.intValue());
/*    */     }
/* 89 */     return Integer.valueOf(0);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.optimizer.IntegerParameterOptimizer
 * JD-Core Version:    0.6.0
 */