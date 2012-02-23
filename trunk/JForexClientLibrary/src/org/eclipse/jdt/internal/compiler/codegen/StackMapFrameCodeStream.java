/*     */ package org.eclipse.jdt.internal.compiler.codegen;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.ClassFile;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Scope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ 
/*     */ public class StackMapFrameCodeStream extends CodeStream
/*     */ {
/*     */   public int[] stateIndexes;
/*     */   public int stateIndexesCounter;
/*     */   private HashMap framePositions;
/*     */   public Set exceptionMarkers;
/*     */   public ArrayList stackDepthMarkers;
/*     */   public ArrayList stackMarkers;
/*     */ 
/*     */   public StackMapFrameCodeStream(ClassFile givenClassFile)
/*     */   {
/* 136 */     super(givenClassFile);
/* 137 */     this.generateAttributes |= 16;
/*     */   }
/*     */ 
/*     */   public void addDefinitelyAssignedVariables(Scope scope, int initStateIndex) {
/* 141 */     for (int i = 0; i < this.visibleLocalsCount; i++) {
/* 142 */       LocalVariableBinding localBinding = this.visibleLocals[i];
/* 143 */       if (localBinding == null)
/*     */         continue;
/* 145 */       boolean isDefinitelyAssigned = isDefinitelyAssigned(scope, initStateIndex, localBinding);
/* 146 */       if (!isDefinitelyAssigned) {
/* 147 */         if (this.stateIndexes != null) {
/* 148 */           int j = 0; for (int max = this.stateIndexesCounter; j < max; j++)
/* 149 */             if (isDefinitelyAssigned(scope, this.stateIndexes[j], localBinding)) {
/* 150 */               if ((localBinding.initializationCount != 0) && (localBinding.initializationPCs[((localBinding.initializationCount - 1 << 1) + 1)] == -1))
/*     */               {
/*     */                 break;
/*     */               }
/*     */ 
/* 160 */               localBinding.recordInitializationStartPC(this.position);
/*     */ 
/* 162 */               break;
/*     */             }
/*     */         }
/*     */       }
/*     */       else {
/* 167 */         if ((localBinding.initializationCount != 0) && (localBinding.initializationPCs[((localBinding.initializationCount - 1 << 1) + 1)] == -1))
/*     */         {
/*     */           continue;
/*     */         }
/*     */ 
/* 177 */         localBinding.recordInitializationStartPC(this.position);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addExceptionMarker(int pc, TypeBinding typeBinding)
/*     */   {
/* 184 */     if (this.exceptionMarkers == null) {
/* 185 */       this.exceptionMarkers = new HashSet();
/*     */     }
/* 187 */     if (typeBinding == null)
/* 188 */       this.exceptionMarkers.add(new ExceptionMarker(pc, ConstantPool.JavaLangThrowableConstantPoolName));
/*     */     else
/* 190 */       switch (typeBinding.id) {
/*     */       case 12:
/* 192 */         this.exceptionMarkers.add(new ExceptionMarker(pc, ConstantPool.JavaLangClassNotFoundExceptionConstantPoolName));
/* 193 */         break;
/*     */       case 7:
/* 195 */         this.exceptionMarkers.add(new ExceptionMarker(pc, ConstantPool.JavaLangNoSuchFieldErrorConstantPoolName));
/* 196 */         break;
/*     */       default:
/* 198 */         this.exceptionMarkers.add(new ExceptionMarker(pc, typeBinding.constantPoolName()));
/*     */       }
/*     */   }
/*     */ 
/*     */   public void addFramePosition(int pc) {
/* 203 */     Integer newEntry = new Integer(pc);
/*     */     FramePosition value;
/* 205 */     if ((value = (FramePosition)this.framePositions.get(newEntry)) != null)
/* 206 */       value.counter += 1;
/*     */     else
/* 208 */       this.framePositions.put(newEntry, new FramePosition());
/*     */   }
/*     */ 
/*     */   public void optimizeBranch(int oldPosition, BranchLabel lbl) {
/* 212 */     super.optimizeBranch(oldPosition, lbl);
/* 213 */     removeFramePosition(oldPosition);
/*     */   }
/*     */   public void removeFramePosition(int pc) {
/* 216 */     Integer entry = new Integer(pc);
/*     */     FramePosition value;
/* 218 */     if ((value = (FramePosition)this.framePositions.get(entry)) != null) {
/* 219 */       value.counter -= 1;
/* 220 */       if (value.counter <= 0)
/* 221 */         this.framePositions.remove(entry);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addVariable(LocalVariableBinding localBinding) {
/* 226 */     if (localBinding.initializationPCs == null) {
/* 227 */       record(localBinding);
/*     */     }
/* 229 */     localBinding.recordInitializationStartPC(this.position);
/*     */   }
/*     */   private void addStackMarker(int pc, int destinationPC) {
/* 232 */     if (this.stackMarkers == null) {
/* 233 */       this.stackMarkers = new ArrayList();
/* 234 */       this.stackMarkers.add(new StackMarker(pc, destinationPC));
/*     */     } else {
/* 236 */       int size = this.stackMarkers.size();
/* 237 */       if ((size == 0) || (((StackMarker)this.stackMarkers.get(size - 1)).pc != this.position))
/* 238 */         this.stackMarkers.add(new StackMarker(pc, destinationPC));
/*     */     }
/*     */   }
/*     */ 
/*     */   private void addStackDepthMarker(int pc, int delta, TypeBinding typeBinding) {
/* 243 */     if (this.stackDepthMarkers == null) {
/* 244 */       this.stackDepthMarkers = new ArrayList();
/* 245 */       this.stackDepthMarkers.add(new StackDepthMarker(pc, delta, typeBinding));
/*     */     } else {
/* 247 */       int size = this.stackDepthMarkers.size();
/* 248 */       if ((size == 0) || (((StackDepthMarker)this.stackDepthMarkers.get(size - 1)).pc != this.position))
/* 249 */         this.stackDepthMarkers.add(new StackDepthMarker(pc, delta, typeBinding));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void decrStackSize(int offset) {
/* 254 */     super.decrStackSize(offset);
/* 255 */     addStackDepthMarker(this.position, -1, null);
/*     */   }
/*     */   public void recordExpressionType(TypeBinding typeBinding) {
/* 258 */     addStackDepthMarker(this.position, 0, typeBinding);
/*     */   }
/*     */ 
/*     */   public void generateClassLiteralAccessForType(TypeBinding accessedType, FieldBinding syntheticFieldBinding)
/*     */   {
/* 264 */     if ((accessedType.isBaseType()) && (accessedType != TypeBinding.NULL)) {
/* 265 */       getTYPE(accessedType.id);
/* 266 */       return;
/*     */     }
/*     */ 
/* 269 */     if (this.targetLevel >= 3211264L)
/*     */     {
/* 271 */       ldc(accessedType);
/*     */     }
/*     */     else {
/* 274 */       BranchLabel endLabel = new BranchLabel(this);
/* 275 */       if (syntheticFieldBinding != null) {
/* 276 */         fieldAccess(-78, syntheticFieldBinding, null);
/* 277 */         dup();
/* 278 */         ifnonnull(endLabel);
/* 279 */         pop();
/*     */       }
/*     */ 
/* 293 */       ExceptionLabel classNotFoundExceptionHandler = new ExceptionLabel(this, TypeBinding.NULL);
/* 294 */       classNotFoundExceptionHandler.placeStart();
/* 295 */       ldc(accessedType == TypeBinding.NULL ? "java.lang.Object" : String.valueOf(accessedType.constantPoolName()).replace('/', '.'));
/* 296 */       invokeClassForName();
/*     */ 
/* 315 */       classNotFoundExceptionHandler.placeEnd();
/*     */ 
/* 317 */       if (syntheticFieldBinding != null) {
/* 318 */         dup();
/* 319 */         fieldAccess(-77, syntheticFieldBinding, null);
/*     */       }
/* 321 */       int fromPC = this.position;
/* 322 */       goto_(endLabel);
/* 323 */       int savedStackDepth = this.stackDepth;
/*     */ 
/* 329 */       pushExceptionOnStack(TypeBinding.NULL);
/* 330 */       classNotFoundExceptionHandler.place();
/*     */ 
/* 335 */       newNoClassDefFoundError();
/* 336 */       dup_x1();
/* 337 */       swap();
/*     */ 
/* 340 */       invokeThrowableGetMessage();
/*     */ 
/* 343 */       invokeNoClassDefFoundErrorStringConstructor();
/* 344 */       athrow();
/* 345 */       endLabel.place();
/* 346 */       addStackMarker(fromPC, this.position);
/* 347 */       this.stackDepth = savedStackDepth;
/*     */     }
/*     */   }
/*     */ 
/*     */   public ExceptionMarker[] getExceptionMarkers() {
/* 351 */     Set exceptionMarkerSet = this.exceptionMarkers;
/* 352 */     if (this.exceptionMarkers == null) return null;
/* 353 */     int size = exceptionMarkerSet.size();
/* 354 */     ExceptionMarker[] markers = new ExceptionMarker[size];
/* 355 */     int n = 0;
/* 356 */     for (Iterator iterator = exceptionMarkerSet.iterator(); iterator.hasNext(); ) {
/* 357 */       markers[(n++)] = ((ExceptionMarker)iterator.next());
/*     */     }
/* 359 */     Arrays.sort(markers);
/*     */ 
/* 366 */     return markers;
/*     */   }
/*     */   public int[] getFramePositions() {
/* 369 */     Set set = this.framePositions.keySet();
/* 370 */     int size = set.size();
/* 371 */     int[] positions = new int[size];
/* 372 */     int n = 0;
/* 373 */     for (Iterator iterator = set.iterator(); iterator.hasNext(); ) {
/* 374 */       positions[(n++)] = ((Integer)iterator.next()).intValue();
/*     */     }
/* 376 */     Arrays.sort(positions);
/*     */ 
/* 383 */     return positions;
/*     */   }
/*     */   public StackDepthMarker[] getStackDepthMarkers() {
/* 386 */     if (this.stackDepthMarkers == null) return null;
/* 387 */     int length = this.stackDepthMarkers.size();
/* 388 */     if (length == 0) return null;
/* 389 */     StackDepthMarker[] result = new StackDepthMarker[length];
/* 390 */     this.stackDepthMarkers.toArray(result);
/* 391 */     return result;
/*     */   }
/*     */   public StackMarker[] getStackMarkers() {
/* 394 */     if (this.stackMarkers == null) return null;
/* 395 */     int length = this.stackMarkers.size();
/* 396 */     if (length == 0) return null;
/* 397 */     StackMarker[] result = new StackMarker[length];
/* 398 */     this.stackMarkers.toArray(result);
/* 399 */     return result;
/*     */   }
/*     */   public boolean hasFramePositions() {
/* 402 */     return this.framePositions.size() != 0;
/*     */   }
/*     */   public void init(ClassFile targetClassFile) {
/* 405 */     super.init(targetClassFile);
/* 406 */     this.stateIndexesCounter = 0;
/* 407 */     if (this.framePositions != null) {
/* 408 */       this.framePositions.clear();
/*     */     }
/* 410 */     if (this.exceptionMarkers != null) {
/* 411 */       this.exceptionMarkers.clear();
/*     */     }
/* 413 */     if (this.stackDepthMarkers != null) {
/* 414 */       this.stackDepthMarkers.clear();
/*     */     }
/* 416 */     if (this.stackMarkers != null)
/* 417 */       this.stackMarkers.clear();
/*     */   }
/*     */ 
/*     */   public void initializeMaxLocals(MethodBinding methodBinding)
/*     */   {
/* 422 */     super.initializeMaxLocals(methodBinding);
/* 423 */     if (this.framePositions == null)
/* 424 */       this.framePositions = new HashMap();
/*     */     else
/* 426 */       this.framePositions.clear();
/*     */   }
/*     */ 
/*     */   public void popStateIndex() {
/* 430 */     this.stateIndexesCounter -= 1;
/*     */   }
/*     */   public void pushStateIndex(int naturalExitMergeInitStateIndex) {
/* 433 */     if (this.stateIndexes == null) {
/* 434 */       this.stateIndexes = new int[3];
/*     */     }
/* 436 */     int length = this.stateIndexes.length;
/* 437 */     if (length == this.stateIndexesCounter)
/*     */     {
/* 439 */       System.arraycopy(this.stateIndexes, 0, this.stateIndexes = new int[length * 2], 0, length);
/*     */     }
/* 441 */     this.stateIndexes[(this.stateIndexesCounter++)] = naturalExitMergeInitStateIndex;
/*     */   }
/*     */   public void removeNotDefinitelyAssignedVariables(Scope scope, int initStateIndex) {
/* 444 */     int index = this.visibleLocalsCount;
/* 445 */     for (int i = 0; i < index; i++) {
/* 446 */       LocalVariableBinding localBinding = this.visibleLocals[i];
/* 447 */       if ((localBinding != null) && (localBinding.initializationCount > 0)) {
/* 448 */         boolean isDefinitelyAssigned = isDefinitelyAssigned(scope, initStateIndex, localBinding);
/* 449 */         if (!isDefinitelyAssigned) {
/* 450 */           if (this.stateIndexes != null) {
/* 451 */             int j = 0; for (int max = this.stateIndexesCounter; j < max; j++) {
/* 452 */               if (isDefinitelyAssigned(scope, this.stateIndexes[j], localBinding)) {
/*     */                 break;
/*     */               }
/*     */             }
/*     */           }
/* 457 */           localBinding.recordInitializationEndPC(this.position);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void reset(ClassFile givenClassFile) {
/* 463 */     super.reset(givenClassFile);
/* 464 */     this.stateIndexesCounter = 0;
/* 465 */     if (this.framePositions != null) {
/* 466 */       this.framePositions.clear();
/*     */     }
/* 468 */     if (this.exceptionMarkers != null) {
/* 469 */       this.exceptionMarkers.clear();
/*     */     }
/* 471 */     if (this.stackDepthMarkers != null) {
/* 472 */       this.stackDepthMarkers.clear();
/*     */     }
/* 474 */     if (this.stackMarkers != null)
/* 475 */       this.stackMarkers.clear();
/*     */   }
/*     */ 
/*     */   protected void writePosition(BranchLabel label) {
/* 479 */     super.writePosition(label);
/* 480 */     addFramePosition(label.position);
/*     */   }
/*     */   protected void writePosition(BranchLabel label, int forwardReference) {
/* 483 */     super.writePosition(label, forwardReference);
/* 484 */     addFramePosition(label.position);
/*     */   }
/*     */   protected void writeSignedWord(int pos, int value) {
/* 487 */     super.writeSignedWord(pos, value);
/* 488 */     addFramePosition(this.position);
/*     */   }
/*     */   protected void writeWidePosition(BranchLabel label) {
/* 491 */     super.writeWidePosition(label);
/* 492 */     addFramePosition(label.position);
/*     */   }
/*     */   public void areturn() {
/* 495 */     super.areturn();
/* 496 */     addFramePosition(this.position);
/*     */   }
/*     */   public void ireturn() {
/* 499 */     super.ireturn();
/* 500 */     addFramePosition(this.position);
/*     */   }
/*     */   public void lreturn() {
/* 503 */     super.lreturn();
/* 504 */     addFramePosition(this.position);
/*     */   }
/*     */   public void freturn() {
/* 507 */     super.freturn();
/* 508 */     addFramePosition(this.position);
/*     */   }
/*     */   public void dreturn() {
/* 511 */     super.dreturn();
/* 512 */     addFramePosition(this.position);
/*     */   }
/*     */   public void return_() {
/* 515 */     super.return_();
/* 516 */     addFramePosition(this.position);
/*     */   }
/*     */   public void athrow() {
/* 519 */     super.athrow();
/* 520 */     addFramePosition(this.position);
/*     */   }
/*     */   public void pushOnStack(TypeBinding binding) {
/* 523 */     super.pushOnStack(binding);
/* 524 */     addStackDepthMarker(this.position, 1, binding);
/*     */   }
/*     */   public void pushExceptionOnStack(TypeBinding binding) {
/* 527 */     super.pushExceptionOnStack(binding);
/* 528 */     addExceptionMarker(this.position, binding);
/*     */   }
/*     */   public void goto_(BranchLabel label) {
/* 531 */     super.goto_(label);
/* 532 */     addFramePosition(this.position);
/*     */   }
/*     */   public void goto_w(BranchLabel label) {
/* 535 */     super.goto_w(label);
/* 536 */     addFramePosition(this.position);
/*     */   }
/*     */   public void resetInWideMode() {
/* 539 */     resetSecretLocals();
/* 540 */     super.resetInWideMode();
/*     */   }
/*     */   public void resetSecretLocals() {
/* 543 */     int i = 0; for (int max = this.locals.length; i < max; i++) {
/* 544 */       LocalVariableBinding localVariableBinding = this.locals[i];
/* 545 */       if ((localVariableBinding == null) || (!localVariableBinding.isSecret()))
/*     */         continue;
/* 547 */       localVariableBinding.resetInitializations();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class ExceptionMarker
/*     */     implements Comparable
/*     */   {
/*     */     public char[] constantPoolName;
/*     */     public int pc;
/*     */ 
/*     */     public ExceptionMarker(int pc, char[] constantPoolName)
/*     */     {
/*  36 */       this.pc = pc;
/*  37 */       this.constantPoolName = constantPoolName;
/*     */     }
/*     */     public int compareTo(Object o) {
/*  40 */       if ((o instanceof ExceptionMarker)) {
/*  41 */         return this.pc - ((ExceptionMarker)o).pc;
/*     */       }
/*  43 */       return 0;
/*     */     }
/*     */     public boolean equals(Object obj) {
/*  46 */       if ((obj instanceof ExceptionMarker)) {
/*  47 */         ExceptionMarker marker = (ExceptionMarker)obj;
/*  48 */         return (this.pc == marker.pc) && (CharOperation.equals(this.constantPoolName, marker.constantPoolName));
/*     */       }
/*  50 */       return false;
/*     */     }
/*     */     public int hashCode() {
/*  53 */       return this.pc + this.constantPoolName.hashCode();
/*     */     }
/*     */     public String toString() {
/*  56 */       StringBuffer buffer = new StringBuffer();
/*  57 */       buffer.append('(').append(this.pc).append(',').append(this.constantPoolName).append(')');
/*  58 */       return String.valueOf(buffer); }  } 
/*     */   static class FramePosition { int counter; }
/*     */ 
/*     */   public static class StackDepthMarker { public int pc;
/*     */     public int delta;
/*     */     public TypeBinding typeBinding;
/*     */ 
/*  68 */     public StackDepthMarker(int pc, int delta, TypeBinding typeBinding) { this.pc = pc;
/*  69 */       this.typeBinding = typeBinding;
/*  70 */       this.delta = delta; }
/*     */ 
/*     */     public StackDepthMarker(int pc, int delta)
/*     */     {
/*  74 */       this.pc = pc;
/*  75 */       this.delta = delta;
/*     */     }
/*     */ 
/*     */     public String toString() {
/*  79 */       StringBuffer buffer = new StringBuffer();
/*  80 */       buffer.append('(').append(this.pc).append(',').append(this.delta);
/*  81 */       if (this.typeBinding != null) {
/*  82 */         buffer
/*  83 */           .append(',')
/*  84 */           .append(this.typeBinding.qualifiedPackageName())
/*  85 */           .append(this.typeBinding.qualifiedSourceName());
/*     */       }
/*  87 */       buffer.append(')');
/*  88 */       return String.valueOf(buffer);
/*     */     } } 
/*     */   public static class StackMarker {
/*     */     public int pc;
/*     */     public int destinationPC;
/*     */     public VerificationTypeInfo[] infos;
/*     */ 
/*     */     public StackMarker(int pc, int destinationPC) {
/*  98 */       this.pc = pc;
/*  99 */       this.destinationPC = destinationPC;
/*     */     }
/*     */ 
/*     */     public void setInfos(VerificationTypeInfo[] infos) {
/* 103 */       this.infos = infos;
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 107 */       StringBuffer buffer = new StringBuffer();
/* 108 */       buffer
/* 109 */         .append("[copy stack items from ")
/* 110 */         .append(this.pc)
/* 111 */         .append(" to ")
/* 112 */         .append(this.destinationPC);
/* 113 */       if (this.infos != null) {
/* 114 */         int i = 0; for (int max = this.infos.length; i < max; i++) {
/* 115 */           if (i > 0) buffer.append(',');
/* 116 */           buffer.append(this.infos[i]);
/*     */         }
/*     */       }
/* 119 */       buffer.append(']');
/* 120 */       return String.valueOf(buffer);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.codegen.StackMapFrameCodeStream
 * JD-Core Version:    0.6.0
 */