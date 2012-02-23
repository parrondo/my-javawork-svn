/*     */ package org.eclipse.jdt.internal.compiler.classfmt;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.env.IBinaryNestedType;
/*     */ 
/*     */ public class InnerClassInfo extends ClassFileStruct
/*     */   implements IBinaryNestedType
/*     */ {
/*  21 */   int innerClassNameIndex = -1;
/*  22 */   int outerClassNameIndex = -1;
/*  23 */   int innerNameIndex = -1;
/*     */   private char[] innerClassName;
/*     */   private char[] outerClassName;
/*     */   private char[] innerName;
/*  27 */   private int accessFlags = -1;
/*  28 */   private boolean readInnerClassName = false;
/*  29 */   private boolean readOuterClassName = false;
/*  30 */   private boolean readInnerName = false;
/*     */ 
/*     */   public InnerClassInfo(byte[] classFileBytes, int[] offsets, int offset) {
/*  33 */     super(classFileBytes, offsets, offset);
/*  34 */     this.innerClassNameIndex = u2At(0);
/*  35 */     this.outerClassNameIndex = u2At(2);
/*  36 */     this.innerNameIndex = u2At(4);
/*     */   }
/*     */ 
/*     */   public char[] getEnclosingTypeName()
/*     */   {
/*  46 */     if (!this.readOuterClassName)
/*     */     {
/*  48 */       this.readOuterClassName = true;
/*  49 */       if (this.outerClassNameIndex != 0) {
/*  50 */         int utf8Offset = 
/*  51 */           this.constantPoolOffsets[u2At(
/*  52 */           this.constantPoolOffsets[this.outerClassNameIndex] - this.structOffset + 1)] - 
/*  53 */           this.structOffset;
/*  54 */         this.outerClassName = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
/*     */       }
/*     */     }
/*     */ 
/*  58 */     return this.outerClassName;
/*     */   }
/*     */ 
/*     */   public int getModifiers()
/*     */   {
/*  66 */     if (this.accessFlags == -1)
/*     */     {
/*  68 */       this.accessFlags = u2At(6);
/*     */     }
/*  70 */     return this.accessFlags;
/*     */   }
/*     */ 
/*     */   public char[] getName()
/*     */   {
/*  80 */     if (!this.readInnerClassName)
/*     */     {
/*  82 */       this.readInnerClassName = true;
/*  83 */       if (this.innerClassNameIndex != 0) {
/*  84 */         int classOffset = this.constantPoolOffsets[this.innerClassNameIndex] - this.structOffset;
/*  85 */         int utf8Offset = this.constantPoolOffsets[u2At(classOffset + 1)] - this.structOffset;
/*  86 */         this.innerClassName = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
/*     */       }
/*     */     }
/*  89 */     return this.innerClassName;
/*     */   }
/*     */ 
/*     */   public char[] getSourceName()
/*     */   {
/*  98 */     if (!this.readInnerName) {
/*  99 */       this.readInnerName = true;
/* 100 */       if (this.innerNameIndex != 0) {
/* 101 */         int utf8Offset = this.constantPoolOffsets[this.innerNameIndex] - this.structOffset;
/* 102 */         this.innerName = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
/*     */       }
/*     */     }
/* 105 */     return this.innerName;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 112 */     StringBuffer buffer = new StringBuffer();
/* 113 */     if (getName() != null) {
/* 114 */       buffer.append(getName());
/*     */     }
/* 116 */     buffer.append("\n");
/* 117 */     if (getEnclosingTypeName() != null) {
/* 118 */       buffer.append(getEnclosingTypeName());
/*     */     }
/* 120 */     buffer.append("\n");
/* 121 */     if (getSourceName() != null) {
/* 122 */       buffer.append(getSourceName());
/*     */     }
/* 124 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */   void initialize()
/*     */   {
/* 131 */     getModifiers();
/* 132 */     getName();
/* 133 */     getSourceName();
/* 134 */     getEnclosingTypeName();
/* 135 */     reset();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.classfmt.InnerClassInfo
 * JD-Core Version:    0.6.0
 */