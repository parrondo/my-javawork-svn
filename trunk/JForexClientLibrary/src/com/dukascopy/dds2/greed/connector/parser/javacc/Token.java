/*     */ package com.dukascopy.dds2.greed.connector.parser.javacc;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ 
/*     */ public class Token
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
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
/*  72 */     return null;
/*     */   }
/*     */ 
/*     */   public Token()
/*     */   {
/*     */   }
/*     */ 
/*     */   public Token(int kind)
/*     */   {
/*  85 */     this(kind, null);
/*     */   }
/*     */ 
/*     */   public Token(int kind, String image)
/*     */   {
/*  93 */     this.kind = kind;
/*  94 */     this.image = image;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 102 */     return this.image;
/*     */   }
/*     */ 
/*     */   public static Token newToken(int ofKind, String image)
/*     */   {
/* 119 */     switch (ofKind) {
/*     */     }
/* 121 */     return new Token(ofKind, image);
/*     */   }
/*     */ 
/*     */   public static Token newToken(int ofKind)
/*     */   {
/* 127 */     return newToken(ofKind, null);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.parser.javacc.Token
 * JD-Core Version:    0.6.0
 */