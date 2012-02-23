/*     */ package org.apache.lucene.util;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.LinkedList;
/*     */ 
/*     */ public abstract class AttributeImpl
/*     */   implements Cloneable, Serializable, Attribute
/*     */ {
/*     */ 
/*     */   @Deprecated
/*     */   private static final VirtualMethod<AttributeImpl> toStringMethod;
/*     */ 
/*     */   @Deprecated
/* 104 */   protected boolean enableBackwards = true;
/*     */ 
/*     */   public abstract void clear();
/*     */ 
/*     */   public String toString()
/*     */   {
/*  59 */     return reflectAsString(false);
/*     */   }
/*     */ 
/*     */   public final String reflectAsString(boolean prependAttClass)
/*     */   {
/*  75 */     StringBuilder buffer = new StringBuilder();
/*  76 */     reflectWith(new AttributeReflector(buffer, prependAttClass) {
/*     */       public void reflect(Class<? extends Attribute> attClass, String key, Object value) {
/*  78 */         if (this.val$buffer.length() > 0) {
/*  79 */           this.val$buffer.append(',');
/*     */         }
/*  81 */         if (this.val$prependAttClass) {
/*  82 */           this.val$buffer.append(attClass.getName()).append('#');
/*     */         }
/*  84 */         this.val$buffer.append(key).append('=').append(value == null ? "null" : value);
/*     */       }
/*     */     });
/*  87 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   private boolean assertExternalClass(Class<? extends AttributeImpl> clazz)
/*     */   {
/* 112 */     String name = clazz.getName();
/* 113 */     return ((!name.startsWith("org.apache.lucene.")) && (!name.startsWith("org.apache.solr."))) || (name.equals("org.apache.lucene.util.TestAttributeSource$TestAttributeImpl"));
/*     */   }
/*     */ 
/*     */   public void reflectWith(AttributeReflector reflector)
/*     */   {
/* 142 */     Class clazz = getClass();
/* 143 */     LinkedList interfaces = AttributeSource.getAttributeInterfaces(clazz);
/* 144 */     if (interfaces.size() != 1) {
/* 145 */       throw new UnsupportedOperationException(clazz.getName() + " implements more than one Attribute interface, the default reflectWith() implementation cannot handle this.");
/*     */     }
/*     */ 
/* 148 */     Class interf = (Class)((WeakReference)interfaces.getFirst()).get();
/*     */ 
/* 151 */     if ((this.enableBackwards) && (toStringMethod.isOverriddenAsOf(clazz))) {
/* 152 */       assert (assertExternalClass(clazz)) : "no Lucene/Solr classes should fallback to toString() parsing";
/*     */ 
/* 154 */       for (String part : toString().split(",")) {
/* 155 */         int pos = part.indexOf('=');
/* 156 */         if (pos < 0) {
/* 157 */           throw new UnsupportedOperationException("The backwards compatibility layer to support reflectWith() on old AtributeImpls expects the toString() implementation to return a correct format as specified for method reflectAsString(false)");
/*     */         }
/*     */ 
/* 160 */         reflector.reflect(interf, part.substring(0, pos).trim(), part.substring(pos + 1));
/*     */       }
/* 162 */       return;
/*     */     }
/*     */ 
/* 166 */     Field[] fields = clazz.getDeclaredFields();
/*     */     try {
/* 168 */       for (int i = 0; i < fields.length; i++) {
/* 169 */         Field f = fields[i];
/* 170 */         if (!Modifier.isStatic(f.getModifiers())) {
/* 171 */           f.setAccessible(true);
/* 172 */           reflector.reflect(interf, f.getName(), f.get(this));
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (IllegalAccessException e) {
/* 177 */       throw new RuntimeException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public abstract void copyTo(AttributeImpl paramAttributeImpl);
/*     */ 
/*     */   public Object clone()
/*     */   {
/* 194 */     Object clone = null;
/*     */     try {
/* 196 */       clone = super.clone();
/*     */     } catch (CloneNotSupportedException e) {
/* 198 */       throw new RuntimeException(e);
/*     */     }
/* 200 */     return clone;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  94 */     toStringMethod = new VirtualMethod(AttributeImpl.class, "toString", new Class[0]);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.AttributeImpl
 * JD-Core Version:    0.6.0
 */