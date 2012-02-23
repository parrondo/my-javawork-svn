/*     */ package org.apache.lucene.util.fst;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.store.DataInput;
/*     */ import org.apache.lucene.store.DataOutput;
/*     */ import org.apache.lucene.util.BytesRef;
/*     */ 
/*     */ public final class ByteSequenceOutputs extends Outputs<BytesRef>
/*     */ {
/*     */   private static final BytesRef NO_OUTPUT;
/*     */ 
/*     */   public static ByteSequenceOutputs getSingleton()
/*     */   {
/*  40 */     return new ByteSequenceOutputs();
/*     */   }
/*     */ 
/*     */   public BytesRef common(BytesRef output1, BytesRef output2)
/*     */   {
/*  45 */     assert (output1 != null);
/*  46 */     assert (output2 != null);
/*     */ 
/*  48 */     int pos1 = output1.offset;
/*  49 */     int pos2 = output2.offset;
/*  50 */     int stopAt1 = pos1 + Math.min(output1.length, output2.length);
/*  51 */     while ((pos1 < stopAt1) && 
/*  52 */       (output1.bytes[pos1] == output2.bytes[pos2]))
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
/*  69 */     return new BytesRef(output1.bytes, output1.offset, pos1 - output1.offset);
/*     */   }
/*     */ 
/*     */   public BytesRef subtract(BytesRef output, BytesRef inc)
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
/*  86 */     return new BytesRef(output.bytes, output.offset + inc.length, output.length - inc.length);
/*     */   }
/*     */ 
/*     */   public BytesRef add(BytesRef prefix, BytesRef output)
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
/* 101 */     BytesRef result = new BytesRef(prefix.length + output.length);
/* 102 */     System.arraycopy(prefix.bytes, prefix.offset, result.bytes, 0, prefix.length);
/* 103 */     System.arraycopy(output.bytes, output.offset, result.bytes, prefix.length, output.length);
/* 104 */     prefix.length += output.length;
/* 105 */     return result;
/*     */   }
/*     */ 
/*     */   public void write(BytesRef prefix, DataOutput out)
/*     */     throws IOException
/*     */   {
/* 111 */     assert (prefix != null);
/* 112 */     out.writeVInt(prefix.length);
/* 113 */     out.writeBytes(prefix.bytes, prefix.offset, prefix.length);
/*     */   }
/*     */ 
/*     */   public BytesRef read(DataInput in) throws IOException
/*     */   {
/* 118 */     int len = in.readVInt();
/* 119 */     if (len == 0) {
/* 120 */       return NO_OUTPUT;
/*     */     }
/* 122 */     BytesRef output = new BytesRef(len);
/* 123 */     in.readBytes(output.bytes, 0, len);
/* 124 */     output.length = len;
/* 125 */     return output;
/*     */   }
/*     */ 
/*     */   public BytesRef getNoOutput()
/*     */   {
/* 131 */     return NO_OUTPUT;
/*     */   }
/*     */ 
/*     */   public String outputToString(BytesRef output)
/*     */   {
/* 136 */     return output.utf8ToString();
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  34 */     NO_OUTPUT = new BytesRef();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.fst.ByteSequenceOutputs
 * JD-Core Version:    0.6.0
 */