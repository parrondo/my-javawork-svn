/*     */ package org.apache.lucene.search.function;
/*     */ 
/*     */ public class FieldScoreQuery extends ValueSourceQuery
/*     */ {
/*     */   public FieldScoreQuery(String field, Type type)
/*     */   {
/* 105 */     super(getValueSource(field, type));
/*     */   }
/*     */ 
/*     */   private static ValueSource getValueSource(String field, Type type)
/*     */   {
/* 110 */     if (type == Type.BYTE) {
/* 111 */       return new ByteFieldSource(field);
/*     */     }
/* 113 */     if (type == Type.SHORT) {
/* 114 */       return new ShortFieldSource(field);
/*     */     }
/* 116 */     if (type == Type.INT) {
/* 117 */       return new IntFieldSource(field);
/*     */     }
/* 119 */     if (type == Type.FLOAT) {
/* 120 */       return new FloatFieldSource(field);
/*     */     }
/* 122 */     throw new IllegalArgumentException(type + " is not a known Field Score Query Type!");
/*     */   }
/*     */ 
/*     */   public static class Type
/*     */   {
/*  74 */     public static final Type BYTE = new Type("byte");
/*     */ 
/*  77 */     public static final Type SHORT = new Type("short");
/*     */ 
/*  80 */     public static final Type INT = new Type("int");
/*     */ 
/*  83 */     public static final Type FLOAT = new Type("float");
/*     */     private String typeName;
/*     */ 
/*     */     private Type(String name)
/*     */     {
/*  87 */       this.typeName = name;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/*  92 */       return getClass().getName() + "::" + this.typeName;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.function.FieldScoreQuery
 * JD-Core Version:    0.6.0
 */