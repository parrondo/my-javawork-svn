/*     */ package org.apache.lucene.queryParser;
/*     */ 
/*     */ public class Token
/*     */ {
/*     */   public int kind;
/*     */   public int beginLine;
/*     */   public int beginColumn;
/*     */   public int endLine;
/*     */   public int endColumn;
/*     */   public String image;
/*     */   public Token next;
/*     */   public Token specialToken;
/*     */ 
/*     */   public Object getValue()
/*     */   {
/*  65 */     return null;
/*     */   }
/*     */ 
/*     */   public Token()
/*     */   {
/*     */   }
/*     */ 
/*     */   public Token(int kind)
/*     */   {
/*  78 */     this(kind, null);
/*     */   }
/*     */ 
/*     */   public Token(int kind, String image)
/*     */   {
/*  86 */     this.kind = kind;
/*  87 */     this.image = image;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*  95 */     return this.image;
/*     */   }
/*     */ 
/*     */   public static Token newToken(int ofKind, String image)
/*     */   {
/* 112 */     switch (ofKind) {
/*     */     }
/* 114 */     return new Token(ofKind, image);
/*     */   }
/*     */ 
/*     */   public static Token newToken(int ofKind)
/*     */   {
/* 120 */     return newToken(ofKind, null);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.queryParser.Token
 * JD-Core Version:    0.6.0
 */