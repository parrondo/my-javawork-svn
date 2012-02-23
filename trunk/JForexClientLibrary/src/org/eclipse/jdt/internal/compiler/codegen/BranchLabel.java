/*     */ package org.eclipse.jdt.internal.compiler.codegen;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
/*     */ 
/*     */ public class BranchLabel extends Label
/*     */ {
/*  20 */   private int[] forwardReferences = new int[10];
/*  21 */   private int forwardReferenceCount = 0;
/*     */   BranchLabel delegate;
/*     */   public int tagBits;
/*     */   public static final int WIDE = 1;
/*     */   public static final int USED = 2;
/*     */ 
/*     */   public BranchLabel()
/*     */   {
/*     */   }
/*     */ 
/*     */   public BranchLabel(CodeStream codeStream)
/*     */   {
/*  37 */     super(codeStream);
/*     */   }
/*     */ 
/*     */   void addForwardReference(int pos)
/*     */   {
/*  44 */     if (this.delegate != null) {
/*  45 */       this.delegate.addForwardReference(pos);
/*  46 */       return;
/*     */     }
/*  48 */     int count = this.forwardReferenceCount;
/*  49 */     if (count >= 1) {
/*  50 */       int previousValue = this.forwardReferences[(count - 1)];
/*  51 */       if (previousValue < pos)
/*     */       {
/*     */         int length;
/*  53 */         if (count >= (length = this.forwardReferences.length))
/*  54 */           System.arraycopy(this.forwardReferences, 0, this.forwardReferences = new int[2 * length], 0, length);
/*  55 */         this.forwardReferences[(this.forwardReferenceCount++)] = pos;
/*  56 */       } else if (previousValue > pos) {
/*  57 */         int[] refs = this.forwardReferences;
/*     */ 
/*  59 */         int i = 0; for (int max = this.forwardReferenceCount; i < max; i++)
/*  60 */           if (refs[i] == pos) return;
/*     */         int length;
/*  63 */         if (count >= (length = refs.length))
/*  64 */           System.arraycopy(refs, 0, this.forwardReferences = new int[2 * length], 0, length);
/*  65 */         this.forwardReferences[(this.forwardReferenceCount++)] = pos;
/*  66 */         Arrays.sort(this.forwardReferences, 0, this.forwardReferenceCount);
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/*     */       int length;
/*  70 */       if (count >= (length = this.forwardReferences.length))
/*  71 */         System.arraycopy(this.forwardReferences, 0, this.forwardReferences = new int[2 * length], 0, length);
/*  72 */       this.forwardReferences[(this.forwardReferenceCount++)] = pos;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void becomeDelegateFor(BranchLabel otherLabel)
/*     */   {
/*  81 */     otherLabel.delegate = this;
/*     */ 
/*  84 */     int otherCount = otherLabel.forwardReferenceCount;
/*  85 */     if (otherCount == 0) return;
/*     */ 
/*  87 */     int[] mergedForwardReferences = new int[this.forwardReferenceCount + otherCount];
/*  88 */     int indexInMerge = 0;
/*  89 */     int j = 0;
/*  90 */     int i = 0;
/*  91 */     int max = this.forwardReferenceCount;
/*  92 */     int max2 = otherLabel.forwardReferenceCount;
/*  93 */     for (; i < max; i++) {
/*  94 */       int value1 = this.forwardReferences[i];
/*     */       while (true) {
/*  96 */         int value2 = otherLabel.forwardReferences[j];
/*  97 */         if (value1 < value2) {
/*  98 */           mergedForwardReferences[(indexInMerge++)] = value1;
/*     */         }
/* 100 */         else if (value1 == value2) {
/* 101 */           mergedForwardReferences[(indexInMerge++)] = value1;
/* 102 */           j++;
/*     */         }
/*     */         else {
/* 105 */           mergedForwardReferences[(indexInMerge++)] = value2;
/*     */ 
/*  95 */           j++; if (j < max2)
/*     */           {
/*     */             continue;
/*     */           }
/*     */ 
/* 108 */           mergedForwardReferences[(indexInMerge++)] = value1;
/*     */         }
/*     */       }
/*     */     }
/* 110 */     for (; j < max2; j++) {
/* 111 */       mergedForwardReferences[(indexInMerge++)] = otherLabel.forwardReferences[j];
/*     */     }
/* 113 */     this.forwardReferences = mergedForwardReferences;
/* 114 */     this.forwardReferenceCount = indexInMerge;
/*     */   }
/*     */ 
/*     */   void branch()
/*     */   {
/* 121 */     this.tagBits |= 2;
/* 122 */     if (this.delegate != null) {
/* 123 */       this.delegate.branch();
/* 124 */       return;
/*     */     }
/* 126 */     if (this.position == -1) {
/* 127 */       addForwardReference(this.codeStream.position);
/*     */ 
/* 129 */       this.codeStream.position += 2;
/* 130 */       this.codeStream.classFileOffset += 2;
/*     */     }
/*     */     else
/*     */     {
/* 135 */       this.codeStream.writePosition(this);
/*     */     }
/*     */   }
/*     */ 
/*     */   void branchWide()
/*     */   {
/* 143 */     this.tagBits |= 2;
/* 144 */     if (this.delegate != null) {
/* 145 */       this.delegate.branchWide();
/* 146 */       return;
/*     */     }
/* 148 */     if (this.position == -1) {
/* 149 */       addForwardReference(this.codeStream.position);
/*     */ 
/* 151 */       this.tagBits |= 1;
/* 152 */       this.codeStream.position += 4;
/* 153 */       this.codeStream.classFileOffset += 4;
/*     */     } else {
/* 155 */       this.codeStream.writeWidePosition(this);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int forwardReferenceCount() {
/* 160 */     if (this.delegate != null) this.delegate.forwardReferenceCount();
/* 161 */     return this.forwardReferenceCount;
/*     */   }
/*     */   public int[] forwardReferences() {
/* 164 */     if (this.delegate != null) this.delegate.forwardReferences();
/* 165 */     return this.forwardReferences;
/*     */   }
/*     */   public void initialize(CodeStream stream) {
/* 168 */     this.codeStream = stream;
/* 169 */     this.position = -1;
/* 170 */     this.forwardReferenceCount = 0;
/* 171 */     this.delegate = null;
/*     */   }
/*     */   public boolean isCaseLabel() {
/* 174 */     return false;
/*     */   }
/*     */   public boolean isStandardLabel() {
/* 177 */     return true;
/*     */   }
/*     */ 
/*     */   public void place()
/*     */   {
/* 189 */     if (this.position == -1) {
/* 190 */       this.position = this.codeStream.position;
/* 191 */       this.codeStream.addLabel(this);
/* 192 */       int oldPosition = this.position;
/* 193 */       boolean isOptimizedBranch = false;
/* 194 */       if (this.forwardReferenceCount != 0) {
/* 195 */         isOptimizedBranch = (this.forwardReferences[(this.forwardReferenceCount - 1)] + 2 == this.position) && (this.codeStream.bCodeStream[(this.codeStream.classFileOffset - 3)] == -89);
/* 196 */         if (isOptimizedBranch) {
/* 197 */           if (this.codeStream.lastAbruptCompletion == this.position) {
/* 198 */             this.codeStream.lastAbruptCompletion = -1;
/*     */           }
/* 200 */           this.codeStream.position = (this.position -= 3);
/* 201 */           this.codeStream.classFileOffset -= 3;
/* 202 */           this.forwardReferenceCount -= 1;
/* 203 */           if (this.codeStream.lastEntryPC == oldPosition) {
/* 204 */             this.codeStream.lastEntryPC = this.position;
/*     */           }
/*     */ 
/* 207 */           if ((this.codeStream.generateAttributes & 0x1C) != 0) {
/* 208 */             LocalVariableBinding[] locals = this.codeStream.locals;
/* 209 */             int i = 0; for (int max = locals.length; i < max; i++) {
/* 210 */               LocalVariableBinding local = locals[i];
/* 211 */               if ((local != null) && (local.initializationCount > 0)) {
/* 212 */                 if (local.initializationPCs[((local.initializationCount - 1 << 1) + 1)] == oldPosition)
/*     */                 {
/* 215 */                   local.initializationPCs[((local.initializationCount - 1 << 1) + 1)] = this.position;
/*     */                 }
/* 217 */                 if (local.initializationPCs[(local.initializationCount - 1 << 1)] == oldPosition) {
/* 218 */                   local.initializationPCs[(local.initializationCount - 1 << 1)] = this.position;
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/* 223 */           if ((this.codeStream.generateAttributes & 0x2) != 0)
/*     */           {
/* 225 */             this.codeStream.removeUnusedPcToSourceMapEntries();
/*     */           }
/*     */         }
/*     */       }
/* 229 */       for (int i = 0; i < this.forwardReferenceCount; i++) {
/* 230 */         this.codeStream.writePosition(this, this.forwardReferences[i]);
/*     */       }
/*     */ 
/* 235 */       if (isOptimizedBranch)
/* 236 */         this.codeStream.optimizeBranch(oldPosition, this);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 245 */     String basic = getClass().getName();
/* 246 */     basic = basic.substring(basic.lastIndexOf('.') + 1);
/* 247 */     StringBuffer buffer = new StringBuffer(basic);
/* 248 */     buffer.append('@').append(Integer.toHexString(hashCode()));
/* 249 */     buffer.append("(position=").append(this.position);
/* 250 */     if (this.delegate != null) buffer.append("delegate=").append(this.delegate);
/* 251 */     buffer.append(", forwards = [");
/* 252 */     for (int i = 0; i < this.forwardReferenceCount - 1; i++)
/* 253 */       buffer.append(this.forwardReferences[i] + ", ");
/* 254 */     if (this.forwardReferenceCount >= 1)
/* 255 */       buffer.append(this.forwardReferences[(this.forwardReferenceCount - 1)]);
/* 256 */     buffer.append("] )");
/* 257 */     return buffer.toString();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.codegen.BranchLabel
 * JD-Core Version:    0.6.0
 */