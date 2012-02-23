/*     */ package org.apache.lucene.util.fst;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.store.DataInput;
/*     */ import org.apache.lucene.store.DataOutput;
/*     */ 
/*     */ public final class PositiveIntOutputs extends Outputs<Long>
/*     */ {
/*     */   private static final Long NO_OUTPUT;
/*     */   private final boolean doShare;
/*     */   private static final PositiveIntOutputs singletonShare;
/*     */   private static final PositiveIntOutputs singletonNoShare;
/*     */ 
/*     */   private PositiveIntOutputs(boolean doShare)
/*     */   {
/*  44 */     this.doShare = doShare;
/*     */   }
/*     */ 
/*     */   public static PositiveIntOutputs getSingleton(boolean doShare) {
/*  48 */     return doShare ? singletonShare : singletonNoShare;
/*     */   }
/*     */ 
/*     */   public Long get(long v) {
/*  52 */     if (v == 0L) {
/*  53 */       return NO_OUTPUT;
/*     */     }
/*  55 */     return Long.valueOf(v);
/*     */   }
/*     */ 
/*     */   public Long common(Long output1, Long output2)
/*     */   {
/*  61 */     assert (valid(output1));
/*  62 */     assert (valid(output2));
/*  63 */     if ((output1 == NO_OUTPUT) || (output2 == NO_OUTPUT))
/*  64 */       return NO_OUTPUT;
/*  65 */     if (this.doShare) {
/*  66 */       assert (output1.longValue() > 0L);
/*  67 */       assert (output2.longValue() > 0L);
/*  68 */       return Long.valueOf(Math.min(output1.longValue(), output2.longValue()));
/*  69 */     }if (output1.equals(output2)) {
/*  70 */       return output1;
/*     */     }
/*  72 */     return NO_OUTPUT;
/*     */   }
/*     */ 
/*     */   public Long subtract(Long output, Long inc)
/*     */   {
/*  78 */     assert (valid(output));
/*  79 */     assert (valid(inc));
/*  80 */     assert (output.longValue() >= inc.longValue());
/*     */ 
/*  82 */     if (inc == NO_OUTPUT)
/*  83 */       return output;
/*  84 */     if (output.equals(inc)) {
/*  85 */       return NO_OUTPUT;
/*     */     }
/*  87 */     return Long.valueOf(output.longValue() - inc.longValue());
/*     */   }
/*     */ 
/*     */   public Long add(Long prefix, Long output)
/*     */   {
/*  93 */     assert (valid(prefix));
/*  94 */     assert (valid(output));
/*  95 */     if (prefix == NO_OUTPUT)
/*  96 */       return output;
/*  97 */     if (output == NO_OUTPUT) {
/*  98 */       return prefix;
/*     */     }
/* 100 */     return Long.valueOf(prefix.longValue() + output.longValue());
/*     */   }
/*     */ 
/*     */   public void write(Long output, DataOutput out)
/*     */     throws IOException
/*     */   {
/* 106 */     assert (valid(output));
/* 107 */     out.writeVLong(output.longValue());
/*     */   }
/*     */ 
/*     */   public Long read(DataInput in) throws IOException
/*     */   {
/* 112 */     long v = in.readVLong();
/* 113 */     if (v == 0L) {
/* 114 */       return NO_OUTPUT;
/*     */     }
/* 116 */     return Long.valueOf(v);
/*     */   }
/*     */ 
/*     */   private boolean valid(Long o)
/*     */   {
/* 121 */     assert (o != null);
/* 122 */     assert ((o instanceof Long));
/* 123 */     assert ((o == NO_OUTPUT) || (o.longValue() > 0L));
/* 124 */     return true;
/*     */   }
/*     */ 
/*     */   public Long getNoOutput()
/*     */   {
/* 129 */     return NO_OUTPUT;
/*     */   }
/*     */ 
/*     */   public String outputToString(Long output)
/*     */   {
/* 134 */     return output.toString();
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  36 */     NO_OUTPUT = new Long(0L);
/*     */ 
/*  40 */     singletonShare = new PositiveIntOutputs(true);
/*  41 */     singletonNoShare = new PositiveIntOutputs(false);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.fst.PositiveIntOutputs
 * JD-Core Version:    0.6.0
 */