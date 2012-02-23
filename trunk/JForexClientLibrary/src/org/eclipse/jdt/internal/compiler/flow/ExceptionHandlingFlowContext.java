/*     */ package org.eclipse.jdt.internal.compiler.flow;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ASTNode;
/*     */ import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Argument;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Javadoc;
/*     */ import org.eclipse.jdt.internal.compiler.ast.SubRoutineStatement;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TryStatement;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TypeReference;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.ObjectCache;
/*     */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Scope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class ExceptionHandlingFlowContext extends FlowContext
/*     */ {
/*     */   public static final int BitCacheSize = 32;
/*     */   public ReferenceBinding[] handledExceptions;
/*     */   int[] isReached;
/*     */   int[] isNeeded;
/*     */   UnconditionalFlowInfo[] initsOnExceptions;
/*  40 */   ObjectCache indexes = new ObjectCache();
/*     */   boolean isMethodContext;
/*     */   public UnconditionalFlowInfo initsOnReturn;
/*     */   public FlowContext initializationParent;
/*     */   public ArrayList extendedExceptions;
/*     */ 
/*     */   public ExceptionHandlingFlowContext(FlowContext parent, ASTNode associatedNode, ReferenceBinding[] handledExceptions, FlowContext initializationParent, BlockScope scope, UnconditionalFlowInfo flowInfo)
/*     */   {
/*  57 */     super(parent, associatedNode);
/*  58 */     this.isMethodContext = (scope == scope.methodScope());
/*  59 */     this.handledExceptions = handledExceptions;
/*  60 */     int count = handledExceptions.length; int cacheSize = count / 32 + 1;
/*  61 */     this.isReached = new int[cacheSize];
/*  62 */     this.isNeeded = new int[cacheSize];
/*  63 */     this.initsOnExceptions = new UnconditionalFlowInfo[count];
/*  64 */     boolean markExceptionsAndThrowableAsReached = 
/*  65 */       (!this.isMethodContext) || (scope.compilerOptions().reportUnusedDeclaredThrownExceptionExemptExceptionAndThrowable);
/*  66 */     for (int i = 0; i < count; i++) {
/*  67 */       ReferenceBinding handledException = handledExceptions[i];
/*  68 */       this.indexes.put(handledException, i);
/*  69 */       if (handledException.isUncheckedException(true)) {
/*  70 */         if ((markExceptionsAndThrowableAsReached) || (
/*  71 */           (handledException.id != 21) && 
/*  72 */           (handledException.id != 25))) {
/*  73 */           this.isReached[(i / 32)] |= 1 << i % 32;
/*     */         }
/*  75 */         this.initsOnExceptions[i] = flowInfo.unconditionalCopy();
/*     */       } else {
/*  77 */         this.initsOnExceptions[i] = FlowInfo.DEAD_END;
/*     */       }
/*     */     }
/*  80 */     if (!this.isMethodContext) {
/*  81 */       System.arraycopy(this.isReached, 0, this.isNeeded, 0, cacheSize);
/*     */     }
/*  83 */     this.initsOnReturn = FlowInfo.DEAD_END;
/*  84 */     this.initializationParent = initializationParent;
/*     */   }
/*     */ 
/*     */   public void complainIfUnusedExceptionHandlers(AbstractMethodDeclaration method) {
/*  88 */     MethodScope scope = method.scope;
/*     */ 
/*  90 */     if (((method.binding.modifiers & 0x30000000) != 0) && 
/*  91 */       (!scope.compilerOptions().reportUnusedDeclaredThrownExceptionWhenOverriding)) {
/*  92 */       return;
/*     */     }
/*     */ 
/*  96 */     TypeBinding[] docCommentReferences = (TypeBinding[])null;
/*  97 */     int docCommentReferencesLength = 0;
/*     */ 
/*  99 */     if ((scope.compilerOptions().reportUnusedDeclaredThrownExceptionIncludeDocCommentReference) && 
/* 100 */       (method.javadoc != null) && 
/* 101 */       (method.javadoc.exceptionReferences != null) && 
/* 102 */       ((docCommentReferencesLength = method.javadoc.exceptionReferences.length) > 0)) {
/* 103 */       docCommentReferences = new TypeBinding[docCommentReferencesLength];
/* 104 */       for (int i = 0; i < docCommentReferencesLength; i++) {
/* 105 */         docCommentReferences[i] = method.javadoc.exceptionReferences[i].resolvedType;
/*     */       }
/*     */     }
/* 108 */     int i = 0; for (int count = this.handledExceptions.length; i < count; i++) {
/* 109 */       int index = this.indexes.get(this.handledExceptions[i]);
/* 110 */       if ((this.isReached[(index / 32)] & 1 << index % 32) == 0) {
/* 111 */         int j = 0;
/* 112 */         while (docCommentReferences[j] != this.handledExceptions[i])
/*     */         {
/* 111 */           j++; if (j < docCommentReferencesLength)
/*     */           {
/*     */             continue;
/*     */           }
/*     */ 
/* 116 */           scope.problemReporter().unusedDeclaredThrownException(
/* 117 */             this.handledExceptions[index], 
/* 118 */             method, 
/* 119 */             method.thrownExceptions[index]);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void complainIfUnusedExceptionHandlers(BlockScope scope, TryStatement tryStatement) {
/* 126 */     int i = 0; for (int count = this.handledExceptions.length; i < count; i++) {
/* 127 */       int index = this.indexes.get(this.handledExceptions[i]);
/* 128 */       int cacheIndex = index / 32;
/* 129 */       int bitMask = 1 << index % 32;
/* 130 */       if ((this.isReached[cacheIndex] & bitMask) == 0) {
/* 131 */         scope.problemReporter().unreachableCatchBlock(
/* 132 */           this.handledExceptions[index], 
/* 133 */           tryStatement.catchArguments[index].type);
/*     */       }
/* 135 */       else if ((this.isNeeded[cacheIndex] & bitMask) == 0)
/* 136 */         scope.problemReporter().hiddenCatchBlock(
/* 137 */           this.handledExceptions[index], 
/* 138 */           tryStatement.catchArguments[index].type);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String individualToString()
/*     */   {
/* 145 */     StringBuffer buffer = new StringBuffer("Exception flow context");
/* 146 */     int length = this.handledExceptions.length;
/* 147 */     for (int i = 0; i < length; i++) {
/* 148 */       int cacheIndex = i / 32;
/* 149 */       int bitMask = 1 << i % 32;
/* 150 */       buffer.append('[').append(this.handledExceptions[i].readableName());
/* 151 */       if ((this.isReached[cacheIndex] & bitMask) != 0) {
/* 152 */         if ((this.isNeeded[cacheIndex] & bitMask) == 0)
/* 153 */           buffer.append("-masked");
/*     */         else
/* 155 */           buffer.append("-reached");
/*     */       }
/*     */       else {
/* 158 */         buffer.append("-not reached");
/*     */       }
/* 160 */       buffer.append('-').append(this.initsOnExceptions[i].toString()).append(']');
/*     */     }
/* 162 */     buffer.append("[initsOnReturn -").append(this.initsOnReturn.toString()).append(']');
/* 163 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */   public UnconditionalFlowInfo initsOnException(ReferenceBinding exceptionType)
/*     */   {
/*     */     int index;
/* 168 */     if ((index = this.indexes.get(exceptionType)) < 0) {
/* 169 */       return FlowInfo.DEAD_END;
/*     */     }
/* 171 */     return this.initsOnExceptions[index];
/*     */   }
/*     */ 
/*     */   public UnconditionalFlowInfo initsOnReturn() {
/* 175 */     return this.initsOnReturn;
/*     */   }
/*     */ 
/*     */   public void mergeUnhandledException(TypeBinding newException)
/*     */   {
/* 183 */     if (this.extendedExceptions == null) {
/* 184 */       this.extendedExceptions = new ArrayList(5);
/* 185 */       for (int i = 0; i < this.handledExceptions.length; i++) {
/* 186 */         this.extendedExceptions.add(this.handledExceptions[i]);
/*     */       }
/*     */     }
/* 189 */     boolean isRedundant = false;
/*     */ 
/* 191 */     for (int i = this.extendedExceptions.size() - 1; i >= 0; i--) {
/* 192 */       switch (Scope.compareTypes(newException, (TypeBinding)this.extendedExceptions.get(i))) {
/*     */       case 1:
/* 194 */         this.extendedExceptions.remove(i);
/* 195 */         break;
/*     */       case -1:
/* 197 */         isRedundant = true;
/*     */       case 0:
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 203 */     if (!isRedundant)
/* 204 */       this.extendedExceptions.add(newException);
/*     */   }
/*     */ 
/*     */   public void recordHandlingException(ReferenceBinding exceptionType, UnconditionalFlowInfo flowInfo, TypeBinding raisedException, ASTNode invocationSite, boolean wasAlreadyDefinitelyCaught)
/*     */   {
/* 215 */     int index = this.indexes.get(exceptionType);
/* 216 */     int cacheIndex = index / 32;
/* 217 */     int bitMask = 1 << index % 32;
/* 218 */     if (!wasAlreadyDefinitelyCaught) {
/* 219 */       this.isNeeded[cacheIndex] |= bitMask;
/*     */     }
/* 221 */     this.isReached[cacheIndex] |= bitMask;
/*     */ 
/* 223 */     this.initsOnExceptions[index] = 
/* 224 */       ((this.initsOnExceptions[index].tagBits & 0x1) == 0 ? 
/* 225 */       this.initsOnExceptions[index].mergedWith(flowInfo) : 
/* 226 */       flowInfo.unconditionalCopy());
/*     */   }
/*     */ 
/*     */   public void recordReturnFrom(UnconditionalFlowInfo flowInfo) {
/* 230 */     if ((flowInfo.tagBits & 0x1) == 0)
/* 231 */       if ((this.initsOnReturn.tagBits & 0x1) == 0) {
/* 232 */         this.initsOnReturn = this.initsOnReturn.mergedWith(flowInfo);
/*     */       }
/*     */       else
/* 235 */         this.initsOnReturn = ((UnconditionalFlowInfo)flowInfo.copy());
/*     */   }
/*     */ 
/*     */   public SubRoutineStatement subroutine()
/*     */   {
/* 248 */     if ((this.associatedNode instanceof SubRoutineStatement))
/*     */     {
/* 250 */       if (this.parent.subroutine() == this.associatedNode)
/* 251 */         return null;
/* 252 */       return (SubRoutineStatement)this.associatedNode;
/*     */     }
/* 254 */     return null;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.flow.ExceptionHandlingFlowContext
 * JD-Core Version:    0.6.0
 */