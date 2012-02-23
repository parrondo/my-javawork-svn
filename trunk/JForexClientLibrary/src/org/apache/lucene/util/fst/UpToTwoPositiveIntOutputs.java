/*     */ package org.apache.lucene.util.fst;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.store.DataInput;
/*     */ import org.apache.lucene.store.DataOutput;
/*     */ 
/*     */ public final class UpToTwoPositiveIntOutputs extends Outputs<Object>
/*     */ {
/*     */   private static final Long NO_OUTPUT;
/*     */   private final boolean doShare;
/*     */   private static final UpToTwoPositiveIntOutputs singletonShare;
/*     */   private static final UpToTwoPositiveIntOutputs singletonNoShare;
/*     */ 
/*     */   private UpToTwoPositiveIntOutputs(boolean doShare)
/*     */   {
/*  81 */     this.doShare = doShare;
/*     */   }
/*     */ 
/*     */   public static UpToTwoPositiveIntOutputs getSingleton(boolean doShare) {
/*  85 */     return doShare ? singletonShare : singletonNoShare;
/*     */   }
/*     */ 
/*     */   public Long get(long v) {
/*  89 */     if (v == 0L) {
/*  90 */       return NO_OUTPUT;
/*     */     }
/*  92 */     return Long.valueOf(v);
/*     */   }
/*     */ 
/*     */   public TwoLongs get(long first, long second)
/*     */   {
/*  97 */     return new TwoLongs(first, second);
/*     */   }
/*     */ 
/*     */   public Long common(Object _output1, Object _output2)
/*     */   {
/* 102 */     assert (valid(_output1, false));
/* 103 */     assert (valid(_output2, false));
/* 104 */     Long output1 = (Long)_output1;
/* 105 */     Long output2 = (Long)_output2;
/* 106 */     if ((output1 == NO_OUTPUT) || (output2 == NO_OUTPUT))
/* 107 */       return NO_OUTPUT;
/* 108 */     if (this.doShare) {
/* 109 */       assert (output1.longValue() > 0L);
/* 110 */       assert (output2.longValue() > 0L);
/* 111 */       return Long.valueOf(Math.min(output1.longValue(), output2.longValue()));
/* 112 */     }if (output1.equals(output2)) {
/* 113 */       return output1;
/*     */     }
/* 115 */     return NO_OUTPUT;
/*     */   }
/*     */ 
/*     */   public Long subtract(Object _output, Object _inc)
/*     */   {
/* 121 */     assert (valid(_output, false));
/* 122 */     assert (valid(_inc, false));
/* 123 */     Long output = (Long)_output;
/* 124 */     Long inc = (Long)_inc;
/* 125 */     assert (output.longValue() >= inc.longValue());
/*     */ 
/* 127 */     if (inc == NO_OUTPUT)
/* 128 */       return output;
/* 129 */     if (output.equals(inc)) {
/* 130 */       return NO_OUTPUT;
/*     */     }
/* 132 */     return Long.valueOf(output.longValue() - inc.longValue());
/*     */   }
/*     */ 
/*     */   public Object add(Object _prefix, Object _output)
/*     */   {
/* 138 */     assert (valid(_prefix, false));
/* 139 */     assert (valid(_output, true));
/* 140 */     Long prefix = (Long)_prefix;
/* 141 */     if ((_output instanceof Long)) {
/* 142 */       Long output = (Long)_output;
/* 143 */       if (prefix == NO_OUTPUT)
/* 144 */         return output;
/* 145 */       if (output == NO_OUTPUT) {
/* 146 */         return prefix;
/*     */       }
/* 148 */       return Long.valueOf(prefix.longValue() + output.longValue());
/*     */     }
/*     */ 
/* 151 */     TwoLongs output = (TwoLongs)_output;
/* 152 */     long v = prefix.longValue();
/* 153 */     return new TwoLongs(output.first + v, output.second + v);
/*     */   }
/*     */ 
/*     */   public void write(Object _output, DataOutput out)
/*     */     throws IOException
/*     */   {
/* 159 */     assert (valid(_output, true));
/* 160 */     if ((_output instanceof Long)) {
/* 161 */       Long output = (Long)_output;
/* 162 */       out.writeVLong(output.longValue() << 1);
/*     */     } else {
/* 164 */       TwoLongs output = (TwoLongs)_output;
/* 165 */       out.writeVLong(output.first << 1 | 1L);
/* 166 */       out.writeVLong(output.second);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Object read(DataInput in) throws IOException
/*     */   {
/* 172 */     long code = in.readVLong();
/* 173 */     if ((code & 1L) == 0L)
/*     */     {
/* 175 */       long v = code >>> 1;
/* 176 */       if (v == 0L) {
/* 177 */         return NO_OUTPUT;
/*     */       }
/* 179 */       return Long.valueOf(v);
/*     */     }
/*     */ 
/* 183 */     long first = code >>> 1;
/* 184 */     long second = in.readVLong();
/* 185 */     return new TwoLongs(first, second);
/*     */   }
/*     */ 
/*     */   private boolean valid(Long o)
/*     */   {
/* 190 */     assert (o != null);
/* 191 */     assert ((o instanceof Long));
/* 192 */     assert ((o == NO_OUTPUT) || (o.longValue() > 0L));
/* 193 */     return true;
/*     */   }
/*     */ 
/*     */   private boolean valid(Object _o, boolean allowDouble)
/*     */   {
/* 198 */     if (!allowDouble) {
/* 199 */       assert ((_o instanceof Long));
/* 200 */       return valid((Long)_o);
/* 201 */     }if ((_o instanceof TwoLongs)) {
/* 202 */       return true;
/*     */     }
/* 204 */     return valid((Long)_o);
/*     */   }
/*     */ 
/*     */   public Object getNoOutput()
/*     */   {
/* 210 */     return NO_OUTPUT;
/*     */   }
/*     */ 
/*     */   public String outputToString(Object output)
/*     */   {
/* 215 */     return output.toString();
/*     */   }
/*     */ 
/*     */   public Object merge(Object first, Object second)
/*     */   {
/* 220 */     assert (valid(first, false));
/* 221 */     assert (valid(second, false));
/* 222 */     return new TwoLongs(((Long)first).longValue(), ((Long)second).longValue());
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  73 */     NO_OUTPUT = new Long(0L);
/*     */ 
/*  77 */     singletonShare = new UpToTwoPositiveIntOutputs(true);
/*  78 */     singletonNoShare = new UpToTwoPositiveIntOutputs(false);
/*     */   }
/*     */ 
/*     */   public static final class TwoLongs
/*     */   {
/*     */     final long first;
/*     */     final long second;
/*     */ 
/*     */     public TwoLongs(long first, long second)
/*     */     {
/*  46 */       this.first = first;
/*  47 */       this.second = second;
/*  48 */       assert (first >= 0L);
/*  49 */       assert (second >= 0L);
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/*  54 */       return "TwoLongs:" + this.first + "," + this.second;
/*     */     }
/*     */ 
/*     */     public boolean equals(Object _other)
/*     */     {
/*  59 */       if ((_other instanceof TwoLongs)) {
/*  60 */         TwoLongs other = (TwoLongs)_other;
/*  61 */         return (this.first == other.first) && (this.second == other.second);
/*     */       }
/*  63 */       return false;
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/*  69 */       return (int)(this.first ^ this.first >>> 32 ^ (this.second ^ this.second >> 32));
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.fst.UpToTwoPositiveIntOutputs
 * JD-Core Version:    0.6.0
 */