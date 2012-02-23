/*     */ package org.eclipse.jdt.internal.compiler.lookup;
/*     */ 
/*     */ public class InferenceContext
/*     */ {
/*     */   private TypeBinding[][][] collectedSubstitutes;
/*     */   MethodBinding genericMethod;
/*     */   int depth;
/*     */   int status;
/*     */   TypeBinding expectedType;
/*     */   boolean hasExplicitExpectedType;
/*     */   public boolean isUnchecked;
/*     */   TypeBinding[] substitutes;
/*     */   static final int FAILED = 1;
/*     */ 
/*     */   public InferenceContext(MethodBinding genericMethod)
/*     */   {
/*  29 */     this.genericMethod = genericMethod;
/*  30 */     TypeVariableBinding[] typeVariables = genericMethod.typeVariables;
/*  31 */     int varLength = typeVariables.length;
/*  32 */     this.collectedSubstitutes = new TypeBinding[varLength][3];
/*  33 */     this.substitutes = new TypeBinding[varLength];
/*     */   }
/*     */ 
/*     */   public TypeBinding[] getSubstitutes(TypeVariableBinding typeVariable, int constraint) {
/*  37 */     return this.collectedSubstitutes[typeVariable.rank][constraint];
/*     */   }
/*     */ 
/*     */   public boolean hasUnresolvedTypeArgument()
/*     */   {
/*  44 */     int i = 0; for (int varLength = this.substitutes.length; i < varLength; i++) {
/*  45 */       if (this.substitutes[i] == null) {
/*  46 */         return true;
/*     */       }
/*     */     }
/*  49 */     return false;
/*     */   }
/*     */ 
/*     */   public void recordSubstitute(TypeVariableBinding typeVariable, TypeBinding actualType, int constraint) {
/*  53 */     TypeBinding[][] variableSubstitutes = this.collectedSubstitutes[typeVariable.rank];
/*     */ 
/*  55 */     TypeBinding[] constraintSubstitutes = variableSubstitutes[constraint];
/*     */     int length;
/*  57 */     if (constraintSubstitutes == null) {
/*  58 */       int length = 0;
/*  59 */       constraintSubstitutes = new TypeBinding[1];
/*     */     } else {
/*  61 */       length = constraintSubstitutes.length;
/*  62 */       for (int i = 0; i < length; i++) {
/*  63 */         TypeBinding substitute = constraintSubstitutes[i];
/*  64 */         if (substitute == actualType) return;
/*  65 */         if (substitute == null) {
/*  66 */           constraintSubstitutes[i] = actualType;
/*  67 */           break;
/*     */         }
/*     */       }
/*     */ 
/*  71 */       System.arraycopy(constraintSubstitutes, 0, constraintSubstitutes = new TypeBinding[length + 1], 0, length);
/*     */     }
/*  73 */     constraintSubstitutes[length] = actualType;
/*  74 */     variableSubstitutes[constraint] = constraintSubstitutes;
/*     */   }
/*     */ 
/*     */   public String toString() {
/*  78 */     StringBuffer buffer = new StringBuffer(20);
/*  79 */     buffer.append("InferenceContex for ");
/*  80 */     int i = 0; for (int length = this.genericMethod.typeVariables.length; i < length; i++) {
/*  81 */       buffer.append(this.genericMethod.typeVariables[i]);
/*     */     }
/*  83 */     buffer.append(this.genericMethod);
/*  84 */     buffer.append("\n\t[status=");
/*  85 */     switch (this.status) {
/*     */     case 0:
/*  87 */       buffer.append("ok]");
/*  88 */       break;
/*     */     case 1:
/*  90 */       buffer.append("failed]");
/*     */     }
/*     */ 
/*  93 */     if (this.expectedType == null)
/*  94 */       buffer.append(" [expectedType=null]");
/*     */     else {
/*  96 */       buffer.append(" [expectedType=").append(this.expectedType.shortReadableName()).append(']');
/*     */     }
/*  98 */     buffer.append(" [depth=").append(this.depth).append(']');
/*  99 */     buffer.append("\n\t[collected={");
/* 100 */     int i = 0; for (int length = this.collectedSubstitutes == null ? 0 : this.collectedSubstitutes.length; i < length; i++) {
/* 101 */       TypeBinding[][] collected = this.collectedSubstitutes[i];
/* 102 */       for (int j = 0; j <= 2; j++) {
/* 103 */         TypeBinding[] constraintCollected = collected[j];
/* 104 */         if (constraintCollected != null) {
/* 105 */           int k = 0; for (int clength = constraintCollected.length; k < clength; k++) {
/* 106 */             buffer.append("\n\t\t").append(this.genericMethod.typeVariables[i].sourceName);
/* 107 */             switch (j) {
/*     */             case 0:
/* 109 */               buffer.append("=");
/* 110 */               break;
/*     */             case 1:
/* 112 */               buffer.append("<:");
/* 113 */               break;
/*     */             case 2:
/* 115 */               buffer.append(">:");
/*     */             }
/*     */ 
/* 118 */             if (constraintCollected[k] != null) {
/* 119 */               buffer.append(constraintCollected[k].shortReadableName());
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 125 */     buffer.append("}]");
/* 126 */     buffer.append("\n\t[inferred=");
/* 127 */     int count = 0;
/* 128 */     int i = 0; for (int length = this.substitutes == null ? 0 : this.substitutes.length; i < length; i++)
/* 129 */       if (this.substitutes[i] != null) {
/* 130 */         count++;
/* 131 */         buffer.append('{').append(this.genericMethod.typeVariables[i].sourceName);
/* 132 */         buffer.append("=").append(this.substitutes[i].shortReadableName()).append('}');
/*     */       }
/* 134 */     if (count == 0) buffer.append("{}");
/* 135 */     buffer.append(']');
/* 136 */     return buffer.toString();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.InferenceContext
 * JD-Core Version:    0.6.0
 */