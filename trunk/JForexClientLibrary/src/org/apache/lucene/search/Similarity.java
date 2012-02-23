/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ import java.util.Collection;
/*     */ import org.apache.lucene.index.FieldInvertState;
/*     */ import org.apache.lucene.index.Term;
/*     */ import org.apache.lucene.util.SmallFloat;
/*     */ import org.apache.lucene.util.VirtualMethod;
/*     */ 
/*     */ public abstract class Similarity
/*     */   implements Serializable
/*     */ {
/* 533 */   private static final VirtualMethod<Similarity> withoutDocFreqMethod = new VirtualMethod(Similarity.class, "idfExplain", new Class[] { Term.class, Searcher.class });
/*     */ 
/* 535 */   private static final VirtualMethod<Similarity> withDocFreqMethod = new VirtualMethod(Similarity.class, "idfExplain", new Class[] { Term.class, Searcher.class, Integer.TYPE });
/*     */ 
/* 538 */   private final boolean hasIDFExplainWithDocFreqAPI = VirtualMethod.compareImplementationDistance(getClass(), withDocFreqMethod, withoutDocFreqMethod) >= 0;
/*     */ 
/* 544 */   private static Similarity defaultImpl = new DefaultSimilarity();
/*     */   public static final int NO_DOC_ID_PROVIDED = -1;
/* 571 */   private static final float[] NORM_TABLE = new float[256];
/*     */ 
/*     */   public static void setDefault(Similarity similarity)
/*     */   {
/* 555 */     defaultImpl = similarity;
/*     */   }
/*     */ 
/*     */   public static Similarity getDefault()
/*     */   {
/* 567 */     return defaultImpl;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static float decodeNorm(byte b)
/*     */   {
/* 585 */     return NORM_TABLE[(b & 0xFF)];
/*     */   }
/*     */ 
/*     */   public float decodeNormValue(byte b)
/*     */   {
/* 597 */     return NORM_TABLE[(b & 0xFF)];
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static float[] getNormDecoder()
/*     */   {
/* 608 */     return NORM_TABLE;
/*     */   }
/*     */ 
/*     */   public abstract float computeNorm(String paramString, FieldInvertState paramFieldInvertState);
/*     */ 
/*     */   @Deprecated
/*     */   public final float lengthNorm(String fieldName, int numTokens)
/*     */   {
/* 669 */     throw new UnsupportedOperationException("please use computeNorm instead");
/*     */   }
/*     */ 
/*     */   public abstract float queryNorm(float paramFloat);
/*     */ 
/*     */   public byte encodeNormValue(float f)
/*     */   {
/* 706 */     return SmallFloat.floatToByte315(f);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static byte encodeNorm(float f)
/*     */   {
/* 719 */     return SmallFloat.floatToByte315(f);
/*     */   }
/*     */ 
/*     */   public float tf(int freq)
/*     */   {
/* 739 */     return tf(freq);
/*     */   }
/*     */ 
/*     */   public abstract float sloppyFreq(int paramInt);
/*     */ 
/*     */   public abstract float tf(float paramFloat);
/*     */ 
/*     */   public Explanation.IDFExplanation idfExplain(Term term, Searcher searcher, int docFreq)
/*     */     throws IOException
/*     */   {
/* 798 */     if (!this.hasIDFExplainWithDocFreqAPI)
/*     */     {
/* 800 */       return idfExplain(term, searcher);
/*     */     }
/* 802 */     int df = docFreq;
/* 803 */     int max = searcher.maxDoc();
/* 804 */     float idf = idf(df, max);
/* 805 */     return new Explanation.IDFExplanation(df, max, idf)
/*     */     {
/*     */       public String explain() {
/* 808 */         return "idf(docFreq=" + this.val$df + ", maxDocs=" + this.val$max + ")";
/*     */       }
/*     */ 
/*     */       public float getIdf()
/*     */       {
/* 813 */         return this.val$idf;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public Explanation.IDFExplanation idfExplain(Term term, Searcher searcher)
/*     */     throws IOException
/*     */   {
/* 828 */     return idfExplain(term, searcher, searcher.docFreq(term));
/*     */   }
/*     */ 
/*     */   public Explanation.IDFExplanation idfExplain(Collection<Term> terms, Searcher searcher)
/*     */     throws IOException
/*     */   {
/* 846 */     int max = searcher.maxDoc();
/* 847 */     float idf = 0.0F;
/* 848 */     StringBuilder exp = new StringBuilder();
/* 849 */     for (Term term : terms) {
/* 850 */       int df = searcher.docFreq(term);
/* 851 */       idf += idf(df, max);
/* 852 */       exp.append(" ");
/* 853 */       exp.append(term.text());
/* 854 */       exp.append("=");
/* 855 */       exp.append(df);
/*     */     }
/* 857 */     float fIdf = idf;
/* 858 */     return new Explanation.IDFExplanation(fIdf, exp)
/*     */     {
/*     */       public float getIdf() {
/* 861 */         return this.val$fIdf;
/*     */       }
/*     */ 
/*     */       public String explain() {
/* 865 */         return this.val$exp.toString();
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public abstract float idf(int paramInt1, int paramInt2);
/*     */ 
/*     */   public abstract float coord(int paramInt1, int paramInt2);
/*     */ 
/*     */   public float scorePayload(int docId, String fieldName, int start, int end, byte[] payload, int offset, int length)
/*     */   {
/* 918 */     return 1.0F;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 574 */     for (int i = 0; i < 256; i++)
/* 575 */       NORM_TABLE[i] = SmallFloat.byte315ToFloat((byte)i);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.Similarity
 * JD-Core Version:    0.6.0
 */