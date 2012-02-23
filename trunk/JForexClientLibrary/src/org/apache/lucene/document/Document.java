/*     */ package org.apache.lucene.document;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ 
/*     */ public final class Document
/*     */   implements Serializable
/*     */ {
/*  40 */   List<Fieldable> fields = new ArrayList();
/*  41 */   private float boost = 1.0F;
/*     */ 
/* 184 */   private static final Field[] NO_FIELDS = new Field[0];
/*     */ 
/* 215 */   private static final Fieldable[] NO_FIELDABLES = new Fieldable[0];
/*     */ 
/* 240 */   private static final String[] NO_STRINGS = new String[0];
/*     */ 
/* 264 */   private static final byte[][] NO_BYTES = new byte[0][];
/*     */ 
/*     */   public void setBoost(float boost)
/*     */   {
/*  59 */     this.boost = boost;
/*     */   }
/*     */ 
/*     */   public float getBoost()
/*     */   {
/*  75 */     return this.boost;
/*     */   }
/*     */ 
/*     */   public final void add(Fieldable field)
/*     */   {
/*  89 */     this.fields.add(field);
/*     */   }
/*     */ 
/*     */   public final void removeField(String name)
/*     */   {
/* 103 */     Iterator it = this.fields.iterator();
/* 104 */     while (it.hasNext()) {
/* 105 */       Fieldable field = (Fieldable)it.next();
/* 106 */       if (field.name().equals(name)) {
/* 107 */         it.remove();
/* 108 */         return;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public final void removeFields(String name)
/*     */   {
/* 123 */     Iterator it = this.fields.iterator();
/* 124 */     while (it.hasNext()) {
/* 125 */       Fieldable field = (Fieldable)it.next();
/* 126 */       if (field.name().equals(name))
/* 127 */         it.remove();
/*     */     }
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public final Field getField(String name)
/*     */   {
/* 143 */     return (Field)getFieldable(name);
/*     */   }
/*     */ 
/*     */   public Fieldable getFieldable(String name)
/*     */   {
/* 152 */     for (Fieldable field : this.fields) {
/* 153 */       if (field.name().equals(name))
/* 154 */         return field;
/*     */     }
/* 156 */     return null;
/*     */   }
/*     */ 
/*     */   public final String get(String name)
/*     */   {
/* 167 */     for (Fieldable field : this.fields) {
/* 168 */       if ((field.name().equals(name)) && (!field.isBinary()))
/* 169 */         return field.stringValue();
/*     */     }
/* 171 */     return null;
/*     */   }
/*     */ 
/*     */   public final List<Fieldable> getFields()
/*     */   {
/* 181 */     return this.fields;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public final Field[] getFields(String name)
/*     */   {
/* 201 */     List result = new ArrayList();
/* 202 */     for (Fieldable field : this.fields) {
/* 203 */       if (field.name().equals(name)) {
/* 204 */         result.add((Field)field);
/*     */       }
/*     */     }
/*     */ 
/* 208 */     if (result.size() == 0) {
/* 209 */       return NO_FIELDS;
/*     */     }
/* 211 */     return (Field[])result.toArray(new Field[result.size()]);
/*     */   }
/*     */ 
/*     */   public Fieldable[] getFieldables(String name)
/*     */   {
/* 226 */     List result = new ArrayList();
/* 227 */     for (Fieldable field : this.fields) {
/* 228 */       if (field.name().equals(name)) {
/* 229 */         result.add(field);
/*     */       }
/*     */     }
/*     */ 
/* 233 */     if (result.size() == 0) {
/* 234 */       return NO_FIELDABLES;
/*     */     }
/* 236 */     return (Fieldable[])result.toArray(new Fieldable[result.size()]);
/*     */   }
/*     */ 
/*     */   public final String[] getValues(String name)
/*     */   {
/* 252 */     List result = new ArrayList();
/* 253 */     for (Fieldable field : this.fields) {
/* 254 */       if ((field.name().equals(name)) && (!field.isBinary())) {
/* 255 */         result.add(field.stringValue());
/*     */       }
/*     */     }
/* 258 */     if (result.size() == 0) {
/* 259 */       return NO_STRINGS;
/*     */     }
/* 261 */     return (String[])result.toArray(new String[result.size()]);
/*     */   }
/*     */ 
/*     */   public final byte[][] getBinaryValues(String name)
/*     */   {
/* 276 */     List result = new ArrayList();
/* 277 */     for (Fieldable field : this.fields) {
/* 278 */       if ((field.name().equals(name)) && (field.isBinary())) {
/* 279 */         result.add(field.getBinaryValue());
/*     */       }
/*     */     }
/* 282 */     if (result.size() == 0) {
/* 283 */       return NO_BYTES;
/*     */     }
/* 285 */     return (byte[][])result.toArray(new byte[result.size()][]);
/*     */   }
/*     */ 
/*     */   public final byte[] getBinaryValue(String name)
/*     */   {
/* 298 */     for (Fieldable field : this.fields) {
/* 299 */       if ((field.name().equals(name)) && (field.isBinary()))
/* 300 */         return field.getBinaryValue();
/*     */     }
/* 302 */     return null;
/*     */   }
/*     */ 
/*     */   public final String toString()
/*     */   {
/* 308 */     StringBuilder buffer = new StringBuilder();
/* 309 */     buffer.append("Document<");
/* 310 */     for (int i = 0; i < this.fields.size(); i++) {
/* 311 */       Fieldable field = (Fieldable)this.fields.get(i);
/* 312 */       buffer.append(field.toString());
/* 313 */       if (i != this.fields.size() - 1)
/* 314 */         buffer.append(" ");
/*     */     }
/* 316 */     buffer.append(">");
/* 317 */     return buffer.toString();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.document.Document
 * JD-Core Version:    0.6.0
 */