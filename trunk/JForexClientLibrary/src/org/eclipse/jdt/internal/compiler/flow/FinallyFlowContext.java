/*     */ package org.eclipse.jdt.internal.compiler.flow;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ast.ASTNode;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Expression;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Reference;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Scope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class FinallyFlowContext extends FlowContext
/*     */ {
/*     */   Reference[] finalAssignments;
/*     */   VariableBinding[] finalVariables;
/*     */   int assignCount;
/*     */   LocalVariableBinding[] nullLocals;
/*     */   Expression[] nullReferences;
/*     */   int[] nullCheckTypes;
/*     */   int nullCount;
/*     */ 
/*     */   public FinallyFlowContext(FlowContext parent, ASTNode associatedNode)
/*     */   {
/*  38 */     super(parent, associatedNode);
/*     */   }
/*     */ 
/*     */   public void complainOnDeferredChecks(FlowInfo flowInfo, BlockScope scope)
/*     */   {
/*  49 */     for (int i = 0; i < this.assignCount; i++) {
/*  50 */       VariableBinding variable = this.finalVariables[i];
/*  51 */       if (variable == null)
/*     */         continue;
/*  53 */       boolean complained = false;
/*  54 */       if ((variable instanceof FieldBinding))
/*     */       {
/*  56 */         if (flowInfo.isPotentiallyAssigned((FieldBinding)variable)) {
/*  57 */           complained = true;
/*  58 */           scope.problemReporter().duplicateInitializationOfBlankFinalField((FieldBinding)variable, this.finalAssignments[i]);
/*     */         }
/*     */ 
/*     */       }
/*  62 */       else if (flowInfo.isPotentiallyAssigned((LocalVariableBinding)variable)) {
/*  63 */         complained = true;
/*  64 */         scope.problemReporter().duplicateInitializationOfFinalLocal(
/*  65 */           (LocalVariableBinding)variable, 
/*  66 */           this.finalAssignments[i]);
/*     */       }
/*     */ 
/*  71 */       if (complained) {
/*  72 */         FlowContext currentContext = this.parent;
/*  73 */         while (currentContext != null)
/*     */         {
/*  75 */           currentContext.removeFinalAssignmentIfAny(this.finalAssignments[i]);
/*     */ 
/*  77 */           currentContext = currentContext.parent;
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  83 */     if (this.deferNullDiagnostic) {
/*  84 */       for (int i = 0; i < this.nullCount; i++) {
/*  85 */         this.parent.recordUsingNullReference(scope, this.nullLocals[i], 
/*  86 */           this.nullReferences[i], this.nullCheckTypes[i], flowInfo);
/*     */       }
/*     */     }
/*     */     else
/*  90 */       for (int i = 0; i < this.nullCount; i++) {
/*  91 */         Expression expression = this.nullReferences[i];
/*     */ 
/*  93 */         LocalVariableBinding local = this.nullLocals[i];
/*  94 */         switch (this.nullCheckTypes[i]) {
/*     */         case 256:
/*     */         case 512:
/*  97 */           if (!flowInfo.isDefinitelyNonNull(local)) break;
/*  98 */           if (this.nullCheckTypes[i] == 512)
/*  99 */             scope.problemReporter().localVariableRedundantCheckOnNonNull(local, expression);
/*     */           else {
/* 101 */             scope.problemReporter().localVariableNonNullComparedToNull(local, expression);
/*     */           }
/* 103 */           break;
/*     */         case 257:
/*     */         case 513:
/*     */         case 769:
/*     */         case 1025:
/* 110 */           if (flowInfo.isDefinitelyNull(local)) {
/* 111 */             switch (this.nullCheckTypes[i] & 0xFFFFFF00) {
/*     */             case 256:
/* 113 */               scope.problemReporter().localVariableRedundantCheckOnNull(local, expression);
/* 114 */               break;
/*     */             case 512:
/* 116 */               scope.problemReporter().localVariableNullComparedToNonNull(local, expression);
/* 117 */               break;
/*     */             case 768:
/* 119 */               scope.problemReporter().localVariableRedundantNullAssignment(local, expression);
/* 120 */               break;
/*     */             case 1024:
/* 122 */               scope.problemReporter().localVariableNullInstanceof(local, expression);
/*     */             }
/*     */           }
/*     */ 
/* 126 */           break;
/*     */         case 3:
/* 128 */           if (flowInfo.isDefinitelyNull(local)) {
/* 129 */             scope.problemReporter().localVariableNullReference(local, expression);
/*     */           }
/* 132 */           else if (flowInfo.isPotentiallyNull(local))
/* 133 */             scope.problemReporter().localVariablePotentialNullReference(local, expression);
/*     */         }
/*     */       }
/*     */   }
/*     */ 
/*     */   public String individualToString()
/*     */   {
/* 145 */     StringBuffer buffer = new StringBuffer("Finally flow context");
/* 146 */     buffer.append("[finalAssignments count - ").append(this.assignCount).append(']');
/* 147 */     buffer.append("[nullReferences count - ").append(this.nullCount).append(']');
/* 148 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */   public boolean isSubRoutine() {
/* 152 */     return true;
/*     */   }
/*     */ 
/*     */   protected boolean recordFinalAssignment(VariableBinding binding, Reference finalAssignment)
/*     */   {
/* 158 */     if (this.assignCount == 0) {
/* 159 */       this.finalAssignments = new Reference[5];
/* 160 */       this.finalVariables = new VariableBinding[5];
/*     */     } else {
/* 162 */       if (this.assignCount == this.finalAssignments.length)
/* 163 */         System.arraycopy(
/* 164 */           this.finalAssignments, 
/* 165 */           0, 
/* 166 */           this.finalAssignments = new Reference[this.assignCount * 2], 
/* 167 */           0, 
/* 168 */           this.assignCount);
/* 169 */       System.arraycopy(
/* 170 */         this.finalVariables, 
/* 171 */         0, 
/* 172 */         this.finalVariables = new VariableBinding[this.assignCount * 2], 
/* 173 */         0, 
/* 174 */         this.assignCount);
/*     */     }
/* 176 */     this.finalAssignments[this.assignCount] = finalAssignment;
/* 177 */     this.finalVariables[(this.assignCount++)] = binding;
/* 178 */     return true;
/*     */   }
/*     */ 
/*     */   public void recordUsingNullReference(Scope scope, LocalVariableBinding local, Expression reference, int checkType, FlowInfo flowInfo)
/*     */   {
/* 183 */     if (((flowInfo.tagBits & 0x1) == 0) && (!flowInfo.isDefinitelyUnknown(local))) {
/* 184 */       if (this.deferNullDiagnostic) {
/* 185 */         switch (checkType) {
/*     */         case 256:
/*     */         case 257:
/*     */         case 512:
/*     */         case 513:
/*     */         case 769:
/*     */         case 1025:
/* 192 */           if (flowInfo.cannotBeNull(local)) {
/* 193 */             if (checkType == 512)
/* 194 */               scope.problemReporter().localVariableRedundantCheckOnNonNull(local, reference);
/*     */             else {
/* 196 */               scope.problemReporter().localVariableNonNullComparedToNull(local, reference);
/*     */             }
/* 198 */             return;
/*     */           }
/* 200 */           if (!flowInfo.canOnlyBeNull(local)) break;
/* 201 */           switch (checkType & 0xFFFFFF00) {
/*     */           case 256:
/* 203 */             scope.problemReporter().localVariableRedundantCheckOnNull(local, reference);
/* 204 */             return;
/*     */           case 512:
/* 206 */             scope.problemReporter().localVariableNullComparedToNonNull(local, reference);
/* 207 */             return;
/*     */           case 768:
/* 209 */             scope.problemReporter().localVariableRedundantNullAssignment(local, reference);
/* 210 */             return;
/*     */           case 1024:
/* 212 */             scope.problemReporter().localVariableNullInstanceof(local, reference);
/* 213 */             return;
/*     */           }
/*     */ 
/* 216 */           break;
/*     */         case 3:
/* 218 */           if (flowInfo.cannotBeNull(local)) {
/* 219 */             return;
/*     */           }
/* 221 */           if (!flowInfo.canOnlyBeNull(local)) break;
/* 222 */           scope.problemReporter().localVariableNullReference(local, reference);
/* 223 */           return;
/*     */         default:
/* 223 */           break;
/*     */         }
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 231 */         switch (checkType) {
/*     */         case 256:
/*     */         case 512:
/* 234 */           if (!flowInfo.isDefinitelyNonNull(local)) break;
/* 235 */           if (checkType == 512)
/* 236 */             scope.problemReporter().localVariableRedundantCheckOnNonNull(local, reference);
/*     */           else {
/* 238 */             scope.problemReporter().localVariableNonNullComparedToNull(local, reference);
/*     */           }
/* 240 */           return;
/*     */         case 257:
/*     */         case 513:
/*     */         case 769:
/*     */         case 1025:
/* 247 */           if (flowInfo.isDefinitelyNull(local)) {
/* 248 */             switch (checkType & 0xFFFFFF00) {
/*     */             case 256:
/* 250 */               scope.problemReporter().localVariableRedundantCheckOnNull(local, reference);
/* 251 */               return;
/*     */             case 512:
/* 253 */               scope.problemReporter().localVariableNullComparedToNonNull(local, reference);
/* 254 */               return;
/*     */             case 768:
/* 256 */               scope.problemReporter().localVariableRedundantNullAssignment(local, reference);
/* 257 */               return;
/*     */             case 1024:
/* 259 */               scope.problemReporter().localVariableNullInstanceof(local, reference);
/* 260 */               return;
/*     */             }
/*     */           }
/* 263 */           break;
/*     */         case 3:
/* 265 */           if (flowInfo.isDefinitelyNull(local)) {
/* 266 */             scope.problemReporter().localVariableNullReference(local, reference);
/* 267 */             return;
/*     */           }
/* 269 */           if (flowInfo.isPotentiallyNull(local)) {
/* 270 */             scope.problemReporter().localVariablePotentialNullReference(local, reference);
/* 271 */             return;
/*     */           }
/* 273 */           if (flowInfo.isDefinitelyNonNull(local)) {
/* 274 */             return;
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 281 */       recordNullReference(local, reference, checkType);
/*     */     }
/*     */   }
/*     */ 
/*     */   void removeFinalAssignmentIfAny(Reference reference)
/*     */   {
/* 287 */     for (int i = 0; i < this.assignCount; i++)
/* 288 */       if (this.finalAssignments[i] == reference) {
/* 289 */         this.finalAssignments[i] = null;
/* 290 */         this.finalVariables[i] = null;
/* 291 */         return;
/*     */       }
/*     */   }
/*     */ 
/*     */   protected void recordNullReference(LocalVariableBinding local, Expression expression, int status)
/*     */   {
/* 298 */     if (this.nullCount == 0) {
/* 299 */       this.nullLocals = new LocalVariableBinding[5];
/* 300 */       this.nullReferences = new Expression[5];
/* 301 */       this.nullCheckTypes = new int[5];
/*     */     }
/* 303 */     else if (this.nullCount == this.nullLocals.length) {
/* 304 */       int newLength = this.nullCount * 2;
/* 305 */       System.arraycopy(this.nullLocals, 0, 
/* 306 */         this.nullLocals = new LocalVariableBinding[newLength], 0, 
/* 307 */         this.nullCount);
/* 308 */       System.arraycopy(this.nullReferences, 0, 
/* 309 */         this.nullReferences = new Expression[newLength], 0, 
/* 310 */         this.nullCount);
/* 311 */       System.arraycopy(this.nullCheckTypes, 0, 
/* 312 */         this.nullCheckTypes = new int[newLength], 0, 
/* 313 */         this.nullCount);
/*     */     }
/* 315 */     this.nullLocals[this.nullCount] = local;
/* 316 */     this.nullReferences[this.nullCount] = expression;
/* 317 */     this.nullCheckTypes[(this.nullCount++)] = status;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.flow.FinallyFlowContext
 * JD-Core Version:    0.6.0
 */