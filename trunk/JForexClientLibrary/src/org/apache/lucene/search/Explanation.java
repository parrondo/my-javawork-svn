/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ public class Explanation
/*     */   implements Serializable
/*     */ {
/*     */   private float value;
/*     */   private String description;
/*     */   private ArrayList<Explanation> details;
/*     */ 
/*     */   public Explanation()
/*     */   {
/*     */   }
/*     */ 
/*     */   public Explanation(float value, String description)
/*     */   {
/*  32 */     this.value = value;
/*  33 */     this.description = description;
/*     */   }
/*     */ 
/*     */   public boolean isMatch()
/*     */   {
/*  45 */     return 0.0F < getValue();
/*     */   }
/*     */ 
/*     */   public float getValue()
/*     */   {
/*  51 */     return this.value;
/*     */   }
/*  53 */   public void setValue(float value) { this.value = value; }
/*     */ 
/*     */   public String getDescription() {
/*  56 */     return this.description;
/*     */   }
/*     */   public void setDescription(String description) {
/*  59 */     this.description = description;
/*     */   }
/*     */ 
/*     */   protected String getSummary()
/*     */   {
/*  67 */     return getValue() + " = " + getDescription();
/*     */   }
/*     */ 
/*     */   public Explanation[] getDetails()
/*     */   {
/*  72 */     if (this.details == null)
/*  73 */       return null;
/*  74 */     return (Explanation[])this.details.toArray(new Explanation[0]);
/*     */   }
/*     */ 
/*     */   public void addDetail(Explanation detail)
/*     */   {
/*  79 */     if (this.details == null)
/*  80 */       this.details = new ArrayList();
/*  81 */     this.details.add(detail);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*  87 */     return toString(0);
/*     */   }
/*     */   protected String toString(int depth) {
/*  90 */     StringBuilder buffer = new StringBuilder();
/*  91 */     for (int i = 0; i < depth; i++) {
/*  92 */       buffer.append("  ");
/*     */     }
/*  94 */     buffer.append(getSummary());
/*  95 */     buffer.append("\n");
/*     */ 
/*  97 */     Explanation[] details = getDetails();
/*  98 */     if (details != null) {
/*  99 */       for (int i = 0; i < details.length; i++) {
/* 100 */         buffer.append(details[i].toString(depth + 1));
/*     */       }
/*     */     }
/*     */ 
/* 104 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */   public String toHtml()
/*     */   {
/* 110 */     StringBuilder buffer = new StringBuilder();
/* 111 */     buffer.append("<ul>\n");
/*     */ 
/* 113 */     buffer.append("<li>");
/* 114 */     buffer.append(getSummary());
/* 115 */     buffer.append("<br />\n");
/*     */ 
/* 117 */     Explanation[] details = getDetails();
/* 118 */     if (details != null) {
/* 119 */       for (int i = 0; i < details.length; i++) {
/* 120 */         buffer.append(details[i].toHtml());
/*     */       }
/*     */     }
/*     */ 
/* 124 */     buffer.append("</li>\n");
/* 125 */     buffer.append("</ul>\n");
/*     */ 
/* 127 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */   public static abstract class IDFExplanation
/*     */     implements Serializable
/*     */   {
/*     */     public abstract float getIdf();
/*     */ 
/*     */     public abstract String explain();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.Explanation
 * JD-Core Version:    0.6.0
 */