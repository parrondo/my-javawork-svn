/*     */ package org.apache.lucene.util.fst;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.util.ArrayUtil;
/*     */ import org.apache.lucene.util.BytesRef;
/*     */ import org.apache.lucene.util.IntsRef;
/*     */ import org.apache.lucene.util.RamUsageEstimator;
/*     */ 
/*     */ public class Builder<T>
/*     */ {
/*     */   private final NodeHash<T> dedupHash;
/*     */   private final FST<T> fst;
/*     */   private final T NO_OUTPUT;
/*     */   private final int minSuffixCount1;
/*     */   private final int minSuffixCount2;
/*     */   private final boolean doShareNonSingletonNodes;
/*     */   private final int shareMaxTailLength;
/*  68 */   private final IntsRef lastInput = new IntsRef();
/*     */   private UnCompiledNode<T>[] frontier;
/* 268 */   private final IntsRef scratchIntsRef = new IntsRef(10);
/*     */ 
/*     */   public Builder(FST.INPUT_TYPE inputType, Outputs<T> outputs)
/*     */   {
/*  82 */     this(inputType, 0, 0, true, true, 2147483647, outputs);
/*     */   }
/*     */ 
/*     */   public Builder(FST.INPUT_TYPE inputType, int minSuffixCount1, int minSuffixCount2, boolean doShareSuffix, boolean doShareNonSingletonNodes, int shareMaxTailLength, Outputs<T> outputs)
/*     */   {
/* 124 */     this.minSuffixCount1 = minSuffixCount1;
/* 125 */     this.minSuffixCount2 = minSuffixCount2;
/* 126 */     this.doShareNonSingletonNodes = doShareNonSingletonNodes;
/* 127 */     this.shareMaxTailLength = shareMaxTailLength;
/* 128 */     this.fst = new FST(inputType, outputs);
/* 129 */     if (doShareSuffix)
/* 130 */       this.dedupHash = new NodeHash(this.fst);
/*     */     else {
/* 132 */       this.dedupHash = null;
/*     */     }
/* 134 */     this.NO_OUTPUT = outputs.getNoOutput();
/*     */ 
/* 136 */     UnCompiledNode[] f = (UnCompiledNode[])new UnCompiledNode[10];
/* 137 */     this.frontier = f;
/* 138 */     for (int idx = 0; idx < this.frontier.length; idx++)
/* 139 */       this.frontier[idx] = new UnCompiledNode(this, idx);
/*     */   }
/*     */ 
/*     */   public int getTotStateCount()
/*     */   {
/* 144 */     return this.fst.nodeCount;
/*     */   }
/*     */ 
/*     */   public long getTermCount() {
/* 148 */     return this.frontier[0].inputCount;
/*     */   }
/*     */ 
/*     */   public int getMappedStateCount() {
/* 152 */     return this.dedupHash == null ? 0 : this.fst.nodeCount;
/*     */   }
/*     */ 
/*     */   private CompiledNode compileNode(UnCompiledNode<T> n, int tailLength)
/*     */     throws IOException
/*     */   {
/*     */     int address;
/*     */     int address;
/* 157 */     if ((this.dedupHash != null) && ((this.doShareNonSingletonNodes) || (n.numArcs <= 1)) && (tailLength <= this.shareMaxTailLength))
/*     */     {
/*     */       int address;
/* 158 */       if (n.numArcs == 0)
/* 159 */         address = this.fst.addNode(n);
/*     */       else
/* 161 */         address = this.dedupHash.add(n);
/*     */     }
/*     */     else {
/* 164 */       address = this.fst.addNode(n);
/*     */     }
/* 166 */     assert (address != -2);
/*     */ 
/* 168 */     n.clear();
/*     */ 
/* 170 */     CompiledNode fn = new CompiledNode();
/* 171 */     fn.address = address;
/* 172 */     return fn;
/*     */   }
/*     */ 
/*     */   private void compilePrevTail(int prefixLenPlus1) throws IOException {
/* 176 */     assert (prefixLenPlus1 >= 1);
/*     */ 
/* 178 */     for (int idx = this.lastInput.length; idx >= prefixLenPlus1; idx--) {
/* 179 */       boolean doPrune = false;
/* 180 */       boolean doCompile = false;
/*     */ 
/* 182 */       UnCompiledNode node = this.frontier[idx];
/* 183 */       UnCompiledNode parent = this.frontier[(idx - 1)];
/*     */ 
/* 185 */       if (node.inputCount < this.minSuffixCount1) {
/* 186 */         doPrune = true;
/* 187 */         doCompile = true;
/* 188 */       } else if (idx > prefixLenPlus1)
/*     */       {
/* 190 */         if ((parent.inputCount < this.minSuffixCount2) || ((this.minSuffixCount2 == 1) && (parent.inputCount == 1L)))
/*     */         {
/* 201 */           doPrune = true;
/*     */         }
/*     */         else
/*     */         {
/* 205 */           doPrune = false;
/*     */         }
/* 207 */         doCompile = true;
/*     */       }
/*     */       else
/*     */       {
/* 211 */         doCompile = this.minSuffixCount2 == 0;
/*     */       }
/*     */ 
/* 216 */       if ((node.inputCount < this.minSuffixCount2) || ((this.minSuffixCount2 == 1) && (node.inputCount == 1L)))
/*     */       {
/* 218 */         for (int arcIdx = 0; arcIdx < node.numArcs; arcIdx++) {
/* 219 */           UnCompiledNode target = (UnCompiledNode)node.arcs[arcIdx].target;
/* 220 */           target.clear();
/*     */         }
/* 222 */         node.numArcs = 0;
/*     */       }
/*     */ 
/* 225 */       if (doPrune)
/*     */       {
/* 227 */         node.clear();
/* 228 */         parent.deleteLast(this.lastInput.ints[(this.lastInput.offset + idx - 1)], node);
/*     */       }
/*     */       else {
/* 231 */         if (this.minSuffixCount2 != 0) {
/* 232 */           compileAllTargets(node, this.lastInput.length - idx);
/*     */         }
/* 234 */         Object nextFinalOutput = node.output;
/*     */ 
/* 241 */         boolean isFinal = (node.isFinal) || (node.numArcs == 0);
/*     */ 
/* 243 */         if (doCompile)
/*     */         {
/* 247 */           parent.replaceLast(this.lastInput.ints[(this.lastInput.offset + idx - 1)], compileNode(node, 1 + this.lastInput.length - idx), nextFinalOutput, isFinal);
/*     */         }
/*     */         else
/*     */         {
/* 254 */           parent.replaceLast(this.lastInput.ints[(this.lastInput.offset + idx - 1)], node, nextFinalOutput, isFinal);
/*     */ 
/* 262 */           this.frontier[idx] = new UnCompiledNode(this, idx);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void add(BytesRef input, T output)
/*     */     throws IOException
/*     */   {
/* 271 */     assert (this.fst.getInputType() == FST.INPUT_TYPE.BYTE1);
/* 272 */     this.scratchIntsRef.grow(input.length);
/* 273 */     for (int i = 0; i < input.length; i++) {
/* 274 */       this.scratchIntsRef.ints[i] = (input.bytes[(i + input.offset)] & 0xFF);
/*     */     }
/* 276 */     this.scratchIntsRef.length = input.length;
/* 277 */     add(this.scratchIntsRef, output);
/*     */   }
/*     */ 
/*     */   public void add(char[] s, int offset, int length, T output)
/*     */     throws IOException
/*     */   {
/* 283 */     assert (this.fst.getInputType() == FST.INPUT_TYPE.BYTE4);
/* 284 */     int charIdx = offset;
/* 285 */     int intIdx = 0;
/* 286 */     int charLimit = offset + length;
/* 287 */     while (charIdx < charLimit) {
/* 288 */       this.scratchIntsRef.grow(intIdx + 1);
/* 289 */       int utf32 = Character.codePointAt(s, charIdx);
/* 290 */       this.scratchIntsRef.ints[intIdx] = utf32;
/* 291 */       charIdx += Character.charCount(utf32);
/* 292 */       intIdx++;
/*     */     }
/* 294 */     this.scratchIntsRef.length = intIdx;
/* 295 */     add(this.scratchIntsRef, output);
/*     */   }
/*     */ 
/*     */   public void add(CharSequence s, T output)
/*     */     throws IOException
/*     */   {
/* 301 */     assert (this.fst.getInputType() == FST.INPUT_TYPE.BYTE4);
/* 302 */     int charIdx = 0;
/* 303 */     int intIdx = 0;
/* 304 */     int charLimit = s.length();
/* 305 */     while (charIdx < charLimit) {
/* 306 */       this.scratchIntsRef.grow(intIdx + 1);
/* 307 */       int utf32 = Character.codePointAt(s, charIdx);
/* 308 */       this.scratchIntsRef.ints[intIdx] = utf32;
/* 309 */       charIdx += Character.charCount(utf32);
/* 310 */       intIdx++;
/*     */     }
/* 312 */     this.scratchIntsRef.length = intIdx;
/* 313 */     add(this.scratchIntsRef, output);
/*     */   }
/*     */ 
/*     */   public void add(IntsRef input, T output)
/*     */     throws IOException
/*     */   {
/* 321 */     assert ((this.lastInput.length == 0) || (input.compareTo(this.lastInput) >= 0)) : ("inputs are added out of order lastInput=" + this.lastInput + " vs input=" + input);
/* 322 */     assert (validOutput(output));
/*     */ 
/* 325 */     if (input.length == 0)
/*     */     {
/* 331 */       this.frontier[0].inputCount += 1L;
/* 332 */       this.frontier[0].isFinal = true;
/* 333 */       this.fst.setEmptyOutput(output);
/* 334 */       return;
/*     */     }
/*     */ 
/* 338 */     int pos1 = 0;
/* 339 */     int pos2 = input.offset;
/* 340 */     int pos1Stop = Math.min(this.lastInput.length, input.length);
/*     */     while (true)
/*     */     {
/* 343 */       this.frontier[pos1].inputCount += 1L;
/* 344 */       if ((pos1 >= pos1Stop) || (this.lastInput.ints[pos1] != input.ints[pos2])) {
/*     */         break;
/*     */       }
/* 347 */       pos1++;
/* 348 */       pos2++;
/*     */     }
/* 350 */     int prefixLenPlus1 = pos1 + 1;
/*     */ 
/* 352 */     if (this.frontier.length < input.length + 1) {
/* 353 */       UnCompiledNode[] next = new UnCompiledNode[ArrayUtil.oversize(input.length + 1, RamUsageEstimator.NUM_BYTES_OBJECT_REF)];
/*     */ 
/* 355 */       System.arraycopy(this.frontier, 0, next, 0, this.frontier.length);
/* 356 */       for (int idx = this.frontier.length; idx < next.length; idx++) {
/* 357 */         next[idx] = new UnCompiledNode(this, idx);
/*     */       }
/* 359 */       this.frontier = next;
/*     */     }
/*     */ 
/* 364 */     compilePrevTail(prefixLenPlus1);
/*     */ 
/* 367 */     for (int idx = prefixLenPlus1; idx <= input.length; idx++) {
/* 368 */       this.frontier[(idx - 1)].addArc(input.ints[(input.offset + idx - 1)], this.frontier[idx]);
/*     */ 
/* 371 */       this.frontier[idx].inputCount += 1L;
/*     */     }
/*     */ 
/* 374 */     UnCompiledNode lastNode = this.frontier[input.length];
/* 375 */     lastNode.isFinal = true;
/* 376 */     lastNode.output = this.NO_OUTPUT;
/*     */ 
/* 380 */     for (int idx = 1; idx < prefixLenPlus1; idx++) {
/* 381 */       UnCompiledNode node = this.frontier[idx];
/* 382 */       UnCompiledNode parentNode = this.frontier[(idx - 1)];
/*     */ 
/* 384 */       Object lastOutput = parentNode.getLastOutput(input.ints[(input.offset + idx - 1)]);
/* 385 */       assert (validOutput(lastOutput));
/*     */       Object commonOutputPrefix;
/* 390 */       if (lastOutput != this.NO_OUTPUT) {
/* 391 */         Object commonOutputPrefix = this.fst.outputs.common(output, lastOutput);
/* 392 */         assert (validOutput(commonOutputPrefix));
/* 393 */         Object wordSuffix = this.fst.outputs.subtract(lastOutput, commonOutputPrefix);
/* 394 */         assert (validOutput(wordSuffix));
/* 395 */         parentNode.setLastOutput(input.ints[(input.offset + idx - 1)], commonOutputPrefix);
/* 396 */         node.prependOutput(wordSuffix);
/*     */       }
/*     */       else
/*     */       {
/*     */         Object wordSuffix;
/* 398 */         commonOutputPrefix = wordSuffix = this.NO_OUTPUT;
/*     */       }
/*     */ 
/* 401 */       output = this.fst.outputs.subtract(output, commonOutputPrefix);
/* 402 */       assert (validOutput(output));
/*     */     }
/*     */ 
/* 405 */     if ((this.lastInput.length == input.length) && (prefixLenPlus1 == 1 + input.length))
/*     */     {
/* 408 */       lastNode.output = this.fst.outputs.merge(lastNode.output, output);
/*     */     }
/*     */     else
/*     */     {
/* 412 */       this.frontier[(prefixLenPlus1 - 1)].setLastOutput(input.ints[(input.offset + prefixLenPlus1 - 1)], output);
/*     */     }
/*     */ 
/* 416 */     this.lastInput.copy(input);
/*     */   }
/*     */ 
/*     */   private boolean validOutput(T output)
/*     */   {
/* 422 */     return (output == this.NO_OUTPUT) || (!output.equals(this.NO_OUTPUT));
/*     */   }
/*     */ 
/*     */   public FST<T> finish()
/*     */     throws IOException
/*     */   {
/* 430 */     compilePrevTail(1);
/*     */ 
/* 432 */     if ((this.frontier[0].inputCount < this.minSuffixCount1) || (this.frontier[0].inputCount < this.minSuffixCount2) || (this.frontier[0].numArcs == 0)) {
/* 433 */       if (this.fst.emptyOutput == null)
/* 434 */         return null;
/* 435 */       if ((this.minSuffixCount1 > 0) || (this.minSuffixCount2 > 0))
/*     */       {
/* 437 */         return null;
/*     */       }
/* 439 */       this.fst.finish(compileNode(this.frontier[0], this.lastInput.length).address);
/*     */ 
/* 441 */       return this.fst;
/*     */     }
/*     */ 
/* 444 */     if (this.minSuffixCount2 != 0) {
/* 445 */       compileAllTargets(this.frontier[0], this.lastInput.length);
/*     */     }
/*     */ 
/* 448 */     this.fst.finish(compileNode(this.frontier[0], this.lastInput.length).address);
/*     */ 
/* 457 */     return this.fst;
/*     */   }
/*     */ 
/*     */   private void compileAllTargets(UnCompiledNode<T> node, int tailLength) throws IOException {
/* 461 */     for (int arcIdx = 0; arcIdx < node.numArcs; arcIdx++) {
/* 462 */       Arc arc = node.arcs[arcIdx];
/* 463 */       if (arc.target.isCompiled())
/*     */         continue;
/* 465 */       UnCompiledNode n = (UnCompiledNode)arc.target;
/* 466 */       if (n.numArcs == 0)
/*     */       {
/* 468 */         arc.isFinal = (n.isFinal = 1);
/*     */       }
/* 470 */       arc.target = compileNode(n, tailLength - 1);
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class UnCompiledNode<T>
/*     */     implements Builder.Node
/*     */   {
/*     */     final Builder<T> owner;
/*     */     int numArcs;
/*     */     Builder.Arc<T>[] arcs;
/*     */     T output;
/*     */     boolean isFinal;
/*     */     long inputCount;
/*     */     final int depth;
/*     */ 
/*     */     public UnCompiledNode(Builder<T> owner, int depth)
/*     */     {
/* 517 */       this.owner = owner;
/* 518 */       this.arcs = ((Builder.Arc[])new Builder.Arc[1]);
/* 519 */       this.arcs[0] = new Builder.Arc();
/* 520 */       this.output = owner.NO_OUTPUT;
/* 521 */       this.depth = depth;
/*     */     }
/*     */ 
/*     */     public boolean isCompiled() {
/* 525 */       return false;
/*     */     }
/*     */ 
/*     */     public void clear() {
/* 529 */       this.numArcs = 0;
/* 530 */       this.isFinal = false;
/* 531 */       this.output = this.owner.NO_OUTPUT;
/* 532 */       this.inputCount = 0L;
/*     */     }
/*     */ 
/*     */     public T getLastOutput(int labelToMatch)
/*     */     {
/* 539 */       assert (this.numArcs > 0);
/* 540 */       assert (this.arcs[(this.numArcs - 1)].label == labelToMatch);
/* 541 */       return this.arcs[(this.numArcs - 1)].output;
/*     */     }
/*     */ 
/*     */     public void addArc(int label, Builder.Node target) {
/* 545 */       assert (label >= 0);
/* 546 */       assert ((this.numArcs == 0) || (label > this.arcs[(this.numArcs - 1)].label)) : ("arc[-1].label=" + this.arcs[(this.numArcs - 1)].label + " new label=" + label + " numArcs=" + this.numArcs);
/* 547 */       if (this.numArcs == this.arcs.length) {
/* 548 */         Builder.Arc[] newArcs = new Builder.Arc[ArrayUtil.oversize(this.numArcs + 1, RamUsageEstimator.NUM_BYTES_OBJECT_REF)];
/*     */ 
/* 550 */         System.arraycopy(this.arcs, 0, newArcs, 0, this.arcs.length);
/* 551 */         for (int arcIdx = this.numArcs; arcIdx < newArcs.length; arcIdx++) {
/* 552 */           newArcs[arcIdx] = new Builder.Arc();
/*     */         }
/* 554 */         this.arcs = newArcs;
/*     */       }
/* 556 */       Builder.Arc arc = this.arcs[(this.numArcs++)];
/* 557 */       arc.label = label;
/* 558 */       arc.target = target;
/* 559 */       arc.output = (arc.nextFinalOutput = this.owner.NO_OUTPUT);
/* 560 */       arc.isFinal = false;
/*     */     }
/*     */ 
/*     */     public void replaceLast(int labelToMatch, Builder.Node target, T nextFinalOutput, boolean isFinal) {
/* 564 */       assert (this.numArcs > 0);
/* 565 */       Builder.Arc arc = this.arcs[(this.numArcs - 1)];
/* 566 */       assert (arc.label == labelToMatch) : ("arc.label=" + arc.label + " vs " + labelToMatch);
/* 567 */       arc.target = target;
/*     */ 
/* 569 */       arc.nextFinalOutput = nextFinalOutput;
/* 570 */       arc.isFinal = isFinal;
/*     */     }
/*     */ 
/*     */     public void deleteLast(int label, Builder.Node target) {
/* 574 */       assert (this.numArcs > 0);
/* 575 */       assert (label == this.arcs[(this.numArcs - 1)].label);
/* 576 */       assert (target == this.arcs[(this.numArcs - 1)].target);
/* 577 */       this.numArcs -= 1;
/*     */     }
/*     */ 
/*     */     public void setLastOutput(int labelToMatch, T newOutput) {
/* 581 */       assert (this.owner.validOutput(newOutput));
/* 582 */       assert (this.numArcs > 0);
/* 583 */       Builder.Arc arc = this.arcs[(this.numArcs - 1)];
/* 584 */       assert (arc.label == labelToMatch);
/* 585 */       arc.output = newOutput;
/*     */     }
/*     */ 
/*     */     public void prependOutput(T outputPrefix)
/*     */     {
/* 590 */       assert (this.owner.validOutput(outputPrefix));
/*     */ 
/* 592 */       for (int arcIdx = 0; arcIdx < this.numArcs; arcIdx++) {
/* 593 */         this.arcs[arcIdx].output = this.owner.fst.outputs.add(outputPrefix, this.arcs[arcIdx].output);
/* 594 */         assert (this.owner.validOutput(this.arcs[arcIdx].output));
/*     */       }
/*     */ 
/* 597 */       if (this.isFinal) {
/* 598 */         this.output = this.owner.fst.outputs.add(outputPrefix, this.output);
/* 599 */         assert (this.owner.validOutput(this.output));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class CompiledNode
/*     */     implements Builder.Node
/*     */   {
/*     */     int address;
/*     */ 
/*     */     public boolean isCompiled()
/*     */     {
/* 494 */       return true;
/*     */     }
/*     */   }
/*     */ 
/*     */   static abstract interface Node
/*     */   {
/*     */     public abstract boolean isCompiled();
/*     */   }
/*     */ 
/*     */   static class Arc<T>
/*     */   {
/*     */     public int label;
/*     */     public Builder.Node target;
/*     */     public boolean isFinal;
/*     */     public T output;
/*     */     public T nextFinalOutput;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.fst.Builder
 * JD-Core Version:    0.6.0
 */