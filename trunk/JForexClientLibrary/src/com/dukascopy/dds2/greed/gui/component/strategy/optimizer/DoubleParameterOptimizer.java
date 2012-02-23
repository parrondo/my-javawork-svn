/*    */ package com.dukascopy.dds2.greed.gui.component.strategy.optimizer;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.component.DoubleSpinnerModel;
/*    */ import java.math.BigDecimal;
/*    */ import javax.swing.SpinnerNumberModel;
/*    */ 
/*    */ public class DoubleParameterOptimizer extends AbstractRangeParameterOptimizer<Double>
/*    */ {
/*    */   public DoubleParameterOptimizer(double value, double maximum, double stepSize, boolean mandatory, boolean readOnly)
/*    */   {
/* 21 */     super(Double.valueOf(value), Double.valueOf(maximum), Double.valueOf(stepSize), mandatory, readOnly);
/*    */   }
/*    */ 
/*    */   protected SpinnerNumberModel createSpinnerModel(Double minimum, Double maximum, Double stepSize)
/*    */   {
/* 26 */     return new DoubleSpinnerModel(minimum.doubleValue(), minimum.doubleValue(), maximum.doubleValue(), stepSize.doubleValue());
/*    */   }
/*    */ 
/*    */   protected String getValueFormat()
/*    */   {
/* 31 */     return "0.0###################"; } 
/*    */   protected Double stringToValue(String string) throws CommitErrorException { // Byte code:
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
/*    */     //   21: invokestatic 9	java/lang/Double:parseDouble	(Ljava/lang/String;)D
/*    */     //   24: invokestatic 1	java/lang/Double:valueOf	(D)Ljava/lang/Double;
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
/* 53 */       Double doubleValue = (Double)value;
/* 54 */       return Double.toString(doubleValue.doubleValue()); } catch (ClassCastException e) {
/*    */     }
/* 56 */     return "";
/*    */   }
/*    */ 
/*    */   protected Double decimalToValue(BigDecimal decimalValue)
/*    */   {
/* 63 */     return Double.valueOf(decimalValue.doubleValue());
/*    */   }
/*    */ 
/*    */   protected Double[] getValues(Double from, Double step, Double to)
/*    */   {
/* 69 */     if ((from != null) && (step != null) && (to != null)) {
/* 70 */       if (step.doubleValue() == 0.0D) {
/* 71 */         return new Double[] { from };
/*    */       }
/* 73 */       BigDecimal decFrom = BigDecimal.valueOf(from.doubleValue());
/* 74 */       BigDecimal decStep = BigDecimal.valueOf(step.doubleValue());
/* 75 */       BigDecimal[] div = BigDecimal.valueOf(to.doubleValue()).subtract(decFrom).divideAndRemainder(decStep);
/* 76 */       Double[] result = new Double[div[0].intValue() + 1];
/* 77 */       for (int i = 0; i < result.length; i++) {
/* 78 */         result[i] = Double.valueOf(decStep.multiply(BigDecimal.valueOf(i)).add(decFrom).doubleValue());
/*    */       }
/* 80 */       return result;
/*    */     }
/* 82 */     if (from != null) {
/* 83 */       return new Double[] { from };
/*    */     }
/* 85 */     return null;
/*    */   }
/*    */ 
/*    */   protected Double calculateStep(Double from, Double to)
/*    */   {
/* 91 */     if ((from != null) && (to != null)) {
/* 92 */       return Double.valueOf(to.doubleValue() - from.doubleValue());
/*    */     }
/* 94 */     return Double.valueOf(0.0D);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.optimizer.DoubleParameterOptimizer
 * JD-Core Version:    0.6.0
 */