/*     */ package org.apache.lucene.util.fst;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.store.DataInput;
/*     */ import org.apache.lucene.store.DataOutput;
/*     */ import org.apache.lucene.util.IntsRef;
/*     */ 
/*     */ public final class IntSequenceOutputs extends Outputs<IntsRef>
/*     */ {
/*     */   private static final IntsRef NO_OUTPUT;
/*     */ 
/*     */   public static IntSequenceOutputs getSingleton()
/*     */   {
/*  40 */     return new IntSequenceOutputs();
/*     */   }
/*     */ 
/*     */   public IntsRef common(IntsRef output1, IntsRef output2)
/*     */   {
/*  45 */     assert (output1 != null);
/*  46 */     assert (output2 != null);
/*     */ 
/*  48 */     int pos1 = output1.offset;
/*  49 */     int pos2 = output2.offset;
/*  50 */     int stopAt1 = pos1 + Math.min(output1.length, output2.length);
/*  51 */     while ((pos1 < stopAt1) && 
/*  52 */       (output1.ints[pos1] == output2.ints[pos2]))
/*     */     {
/*  55 */       pos1++;
/*  56 */       pos2++;
/*     */     }
/*     */ 
/*  59 */     if (pos1 == output1.offset)
/*     */     {
/*  61 */       return NO_OUTPUT;
/*  62 */     }if (pos1 == output1.offset + output1.length)
/*     */     {
/*  64 */       return output1;
/*  65 */     }if (pos2 == output2.offset + output2.length)
/*     */     {
/*  67 */       return output2;
/*     */     }
/*  69 */     return new IntsRef(output1.ints, output1.offset, pos1 - output1.offset);
/*     */   }
/*     */ 
/*     */   public IntsRef subtract(IntsRef output, IntsRef inc)
/*     */   {
/*  75 */     assert (output != null);
/*  76 */     assert (inc != null);
/*  77 */     if (inc == NO_OUTPUT)
/*     */     {
/*  79 */       return output;
/*  80 */     }if (inc.length == output.length)
/*     */     {
/*  82 */       return NO_OUTPUT;
/*     */     }
/*  84 */     assert (inc.length < output.length) : ("inc.length=" + inc.length + " vs output.length=" + output.length);
/*  85 */     assert (inc.length > 0);
/*  86 */     return new IntsRef(output.ints, output.offset + inc.length, output.length - inc.length);
/*     */   }
/*     */ 
/*     */   public IntsRef add(IntsRef prefix, IntsRef output)
/*     */   {
/*  92 */     assert (prefix != null);
/*  93 */     assert (output != null);
/*  94 */     if (prefix == NO_OUTPUT)
/*  95 */       return output;
/*  96 */     if (output == NO_OUTPUT) {
/*  97 */       return prefix;
/*     */     }
/*  99 */     assert (prefix.length > 0);
/* 100 */     assert (output.length > 0);
/* 101 */     IntsRef result = new IntsRef(prefix.length + output.length);
/* 102 */     System.arraycopy(prefix.ints, prefix.offset, result.ints, 0, prefix.length);
/* 103 */     System.arraycopy(output.ints, output.offset, result.ints, prefix.length, output.length);
/* 104 */     prefix.length += output.length;
/* 105 */     return result;
/*     */   }
/*     */ 
/*     */   public void write(IntsRef prefix, DataOutput out)
/*     */     throws IOException
/*     */   {
/* 111 */     assert (prefix != null);
/* 112 */     out.writeVInt(prefix.length);
/* 113 */     for (int idx = 0; idx < prefix.length; idx++)
/* 114 */       out.writeVInt(prefix.ints[(prefix.offset + idx)]);
/*     */   }
/*     */ 
/*     */   public IntsRef read(DataInput in)
/*     */     throws IOException
/*     */   {
/* 120 */     int len = in.readVInt();
/* 121 */     if (len == 0) {
/* 122 */       return NO_OUTPUT;
/*     */     }
/* 124 */     IntsRef output = new IntsRef(len);
/* 125 */     for (int idx = 0; idx < len; idx++) {
/* 126 */       output.ints[idx] = in.readVInt();
/*     */     }
/* 128 */     output.length = len;
/* 129 */     return output;
/*     */   }
/*     */ 
/*     */   public IntsRef getNoOutput()
/*     */   {
/* 135 */     return NO_OUTPUT;
/*     */   }
/*     */ 
/*     */   public String outputToString(IntsRef output)
/*     */   {
/* 140 */     return output.toString();
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  34 */     NO_OUTPUT = new IntsRef();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.fst.IntSequenceOutputs
 * JD-Core Version:    0.6.0
 */