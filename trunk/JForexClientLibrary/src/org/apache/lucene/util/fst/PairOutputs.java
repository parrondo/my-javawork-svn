/*     */ package org.apache.lucene.util.fst;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.store.DataInput;
/*     */ import org.apache.lucene.store.DataOutput;
/*     */ 
/*     */ public class PairOutputs<A, B> extends Outputs<Pair<A, B>>
/*     */ {
/*     */   private final Pair<A, B> NO_OUTPUT;
/*     */   private final Outputs<A> outputs1;
/*     */   private final Outputs<B> outputs2;
/*     */ 
/*     */   public PairOutputs(Outputs<A> outputs1, Outputs<B> outputs2)
/*     */   {
/*  65 */     this.outputs1 = outputs1;
/*  66 */     this.outputs2 = outputs2;
/*  67 */     this.NO_OUTPUT = new Pair(outputs1.getNoOutput(), outputs2.getNoOutput());
/*     */   }
/*     */ 
/*     */   public Pair<A, B> get(A output1, B output2) {
/*  71 */     if ((output1 == this.outputs1.getNoOutput()) && (output2 == this.outputs2.getNoOutput())) {
/*  72 */       return this.NO_OUTPUT;
/*     */     }
/*  74 */     return new Pair(output1, output2);
/*     */   }
/*     */ 
/*     */   public Pair<A, B> common(Pair<A, B> pair1, Pair<A, B> pair2)
/*     */   {
/*  80 */     return get(this.outputs1.common(pair1.output1, pair2.output1), this.outputs2.common(pair1.output2, pair2.output2));
/*     */   }
/*     */ 
/*     */   public Pair<A, B> subtract(Pair<A, B> output, Pair<A, B> inc)
/*     */   {
/*  86 */     return get(this.outputs1.subtract(output.output1, inc.output1), this.outputs2.subtract(output.output2, inc.output2));
/*     */   }
/*     */ 
/*     */   public Pair<A, B> add(Pair<A, B> prefix, Pair<A, B> output)
/*     */   {
/*  92 */     return get(this.outputs1.add(prefix.output1, output.output1), this.outputs2.add(prefix.output2, output.output2));
/*     */   }
/*     */ 
/*     */   public void write(Pair<A, B> output, DataOutput writer)
/*     */     throws IOException
/*     */   {
/*  98 */     this.outputs1.write(output.output1, writer);
/*  99 */     this.outputs2.write(output.output2, writer);
/*     */   }
/*     */ 
/*     */   public Pair<A, B> read(DataInput in) throws IOException
/*     */   {
/* 104 */     Object output1 = this.outputs1.read(in);
/* 105 */     Object output2 = this.outputs2.read(in);
/* 106 */     return get(output1, output2);
/*     */   }
/*     */ 
/*     */   public Pair<A, B> getNoOutput()
/*     */   {
/* 111 */     return this.NO_OUTPUT;
/*     */   }
/*     */ 
/*     */   public String outputToString(Pair<A, B> output)
/*     */   {
/* 116 */     return "<pair:" + this.outputs1.outputToString(output.output1) + "," + this.outputs2.outputToString(output.output2) + ">";
/*     */   }
/*     */ 
/*     */   public static class Pair<A, B>
/*     */   {
/*     */     public final A output1;
/*     */     public final B output2;
/*     */ 
/*     */     public Pair(A output1, B output2)
/*     */     {
/*  42 */       this.output1 = output1;
/*  43 */       this.output2 = output2;
/*     */     }
/*     */ 
/*     */     public boolean equals(Object other)
/*     */     {
/*  48 */       if (other == this)
/*  49 */         return true;
/*  50 */       if ((other instanceof Pair)) {
/*  51 */         Pair pair = (Pair)other;
/*  52 */         return (this.output1.equals(pair.output1)) && (this.output2.equals(pair.output2));
/*     */       }
/*  54 */       return false;
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/*  60 */       return this.output1.hashCode() + this.output2.hashCode();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.fst.PairOutputs
 * JD-Core Version:    0.6.0
 */