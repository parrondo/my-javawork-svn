/*     */ package org.eclipse.jdt.internal.compiler.lookup;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
/*     */ 
/*     */ public class NestedTypeBinding extends SourceTypeBinding
/*     */ {
/*     */   public SourceTypeBinding enclosingType;
/*     */   public SyntheticArgumentBinding[] enclosingInstances;
/*  18 */   private ReferenceBinding[] enclosingTypes = Binding.UNINITIALIZED_REFERENCE_TYPES;
/*     */   public SyntheticArgumentBinding[] outerLocalVariables;
/*  20 */   private int outerLocalVariablesSlotSize = -1;
/*     */ 
/*     */   public NestedTypeBinding(char[][] typeName, ClassScope scope, SourceTypeBinding enclosingType) {
/*  23 */     super(typeName, enclosingType.fPackage, scope);
/*  24 */     this.tagBits |= 2052L;
/*  25 */     this.enclosingType = enclosingType;
/*     */   }
/*     */ 
/*     */   public SyntheticArgumentBinding addSyntheticArgument(LocalVariableBinding actualOuterLocalVariable)
/*     */   {
/*  32 */     SyntheticArgumentBinding synthLocal = null;
/*     */ 
/*  34 */     if (this.outerLocalVariables == null) {
/*  35 */       synthLocal = new SyntheticArgumentBinding(actualOuterLocalVariable);
/*  36 */       this.outerLocalVariables = new SyntheticArgumentBinding[] { synthLocal };
/*     */     } else {
/*  38 */       int size = this.outerLocalVariables.length;
/*  39 */       int newArgIndex = size;
/*  40 */       int i = size;
/*     */       do { if (this.outerLocalVariables[i].actualOuterLocalVariable == actualOuterLocalVariable)
/*  42 */           return this.outerLocalVariables[i];
/*  43 */         if (this.outerLocalVariables[i].id > actualOuterLocalVariable.id)
/*  44 */           newArgIndex = i;
/*  40 */         i--; } while (i >= 0);
/*     */ 
/*  46 */       SyntheticArgumentBinding[] synthLocals = new SyntheticArgumentBinding[size + 1];
/*  47 */       System.arraycopy(this.outerLocalVariables, 0, synthLocals, 0, newArgIndex);
/*     */        tmp132_129 = new SyntheticArgumentBinding(actualOuterLocalVariable); synthLocal = tmp132_129; synthLocals[newArgIndex] = tmp132_129;
/*  49 */       System.arraycopy(this.outerLocalVariables, newArgIndex, synthLocals, newArgIndex + 1, size - newArgIndex);
/*  50 */       this.outerLocalVariables = synthLocals;
/*     */     }
/*     */ 
/*  53 */     if (this.scope.referenceCompilationUnit().isPropagatingInnerClassEmulation)
/*  54 */       updateInnerEmulationDependents();
/*  55 */     return synthLocal;
/*     */   }
/*     */ 
/*     */   public SyntheticArgumentBinding addSyntheticArgument(ReferenceBinding targetEnclosingType)
/*     */   {
/*  62 */     SyntheticArgumentBinding synthLocal = null;
/*  63 */     if (this.enclosingInstances == null) {
/*  64 */       synthLocal = new SyntheticArgumentBinding(targetEnclosingType);
/*  65 */       this.enclosingInstances = new SyntheticArgumentBinding[] { synthLocal };
/*     */     } else {
/*  67 */       int size = this.enclosingInstances.length;
/*  68 */       int newArgIndex = size;
/*  69 */       int i = size;
/*     */       do { if (this.enclosingInstances[i].type == targetEnclosingType)
/*  71 */           return this.enclosingInstances[i];
/*  72 */         if (enclosingType() == targetEnclosingType)
/*  73 */           newArgIndex = 0;
/*  69 */         i--; } while (i >= 0);
/*     */ 
/*  75 */       SyntheticArgumentBinding[] newInstances = new SyntheticArgumentBinding[size + 1];
/*  76 */       System.arraycopy(this.enclosingInstances, 0, newInstances, newArgIndex == 0 ? 1 : 0, size);
/*     */        tmp130_127 = new SyntheticArgumentBinding(targetEnclosingType); synthLocal = tmp130_127; newInstances[newArgIndex] = tmp130_127;
/*  78 */       this.enclosingInstances = newInstances;
/*     */     }
/*     */ 
/*  81 */     if (this.scope.referenceCompilationUnit().isPropagatingInnerClassEmulation)
/*  82 */       updateInnerEmulationDependents();
/*  83 */     return synthLocal;
/*     */   }
/*     */ 
/*     */   public SyntheticArgumentBinding addSyntheticArgumentAndField(LocalVariableBinding actualOuterLocalVariable)
/*     */   {
/*  90 */     SyntheticArgumentBinding synthLocal = addSyntheticArgument(actualOuterLocalVariable);
/*  91 */     if (synthLocal == null) return null;
/*     */ 
/*  93 */     if (synthLocal.matchingField == null)
/*  94 */       synthLocal.matchingField = addSyntheticFieldForInnerclass(actualOuterLocalVariable);
/*  95 */     return synthLocal;
/*     */   }
/*     */ 
/*     */   public SyntheticArgumentBinding addSyntheticArgumentAndField(ReferenceBinding targetEnclosingType)
/*     */   {
/* 102 */     SyntheticArgumentBinding synthLocal = addSyntheticArgument(targetEnclosingType);
/* 103 */     if (synthLocal == null) return null;
/*     */ 
/* 105 */     if (synthLocal.matchingField == null)
/* 106 */       synthLocal.matchingField = addSyntheticFieldForInnerclass(targetEnclosingType);
/* 107 */     return synthLocal;
/*     */   }
/*     */ 
/*     */   public ReferenceBinding enclosingType()
/*     */   {
/* 113 */     return this.enclosingType;
/*     */   }
/*     */ 
/*     */   public int getEnclosingInstancesSlotSize()
/*     */   {
/* 120 */     return this.enclosingInstances == null ? 0 : this.enclosingInstances.length;
/*     */   }
/*     */ 
/*     */   public int getOuterLocalVariablesSlotSize()
/*     */   {
/* 127 */     if (this.outerLocalVariablesSlotSize < 0) {
/* 128 */       this.outerLocalVariablesSlotSize = 0;
/* 129 */       int outerLocalsCount = this.outerLocalVariables == null ? 0 : this.outerLocalVariables.length;
/* 130 */       for (int i = 0; i < outerLocalsCount; i++) {
/* 131 */         SyntheticArgumentBinding argument = this.outerLocalVariables[i];
/* 132 */         switch (argument.type.id) {
/*     */         case 7:
/*     */         case 8:
/* 135 */           this.outerLocalVariablesSlotSize += 2;
/* 136 */           break;
/*     */         default:
/* 138 */           this.outerLocalVariablesSlotSize += 1;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 143 */     return this.outerLocalVariablesSlotSize;
/*     */   }
/*     */ 
/*     */   public SyntheticArgumentBinding getSyntheticArgument(LocalVariableBinding actualOuterLocalVariable)
/*     */   {
/* 149 */     if (this.outerLocalVariables == null) return null;
/* 150 */     int i = this.outerLocalVariables.length;
/*     */     do { if (this.outerLocalVariables[i].actualOuterLocalVariable == actualOuterLocalVariable)
/* 152 */         return this.outerLocalVariables[i];
/* 150 */       i--; } while (i >= 0);
/*     */ 
/* 153 */     return null;
/*     */   }
/*     */ 
/*     */   public SyntheticArgumentBinding getSyntheticArgument(ReferenceBinding targetEnclosingType, boolean onlyExactMatch)
/*     */   {
/* 159 */     if (this.enclosingInstances == null) return null;
/*     */ 
/* 161 */     int i = this.enclosingInstances.length;
/*     */     do { if ((this.enclosingInstances[i].type == targetEnclosingType) && 
/* 163 */         (this.enclosingInstances[i].actualOuterLocalVariable == null))
/* 164 */         return this.enclosingInstances[i];
/* 161 */       i--; } while (i >= 0);
/*     */ 
/* 169 */     if (!onlyExactMatch) {
/* 170 */       int i = this.enclosingInstances.length;
/*     */       do { if ((this.enclosingInstances[i].actualOuterLocalVariable == null) && 
/* 172 */           (this.enclosingInstances[i].type.findSuperTypeOriginatingFrom(targetEnclosingType) != null))
/* 173 */           return this.enclosingInstances[i];
/* 170 */         i--; } while (i >= 0);
/*     */     }
/*     */ 
/* 175 */     return null;
/*     */   }
/*     */ 
/*     */   public SyntheticArgumentBinding[] syntheticEnclosingInstances() {
/* 179 */     return this.enclosingInstances;
/*     */   }
/*     */ 
/*     */   public ReferenceBinding[] syntheticEnclosingInstanceTypes() {
/* 183 */     if (this.enclosingTypes == UNINITIALIZED_REFERENCE_TYPES) {
/* 184 */       if (this.enclosingInstances == null) {
/* 185 */         this.enclosingTypes = null;
/*     */       } else {
/* 187 */         int length = this.enclosingInstances.length;
/* 188 */         this.enclosingTypes = new ReferenceBinding[length];
/* 189 */         for (int i = 0; i < length; i++) {
/* 190 */           this.enclosingTypes[i] = ((ReferenceBinding)this.enclosingInstances[i].type);
/*     */         }
/*     */       }
/*     */     }
/* 194 */     return this.enclosingTypes;
/*     */   }
/*     */ 
/*     */   public SyntheticArgumentBinding[] syntheticOuterLocalVariables() {
/* 198 */     return this.outerLocalVariables;
/*     */   }
/*     */ 
/*     */   public void updateInnerEmulationDependents()
/*     */   {
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.NestedTypeBinding
 * JD-Core Version:    0.6.0
 */