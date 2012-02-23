/*     */ package org.apache.lucene.util.fst;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.store.DataInput;
/*     */ import org.apache.lucene.store.DataOutput;
/*     */ import org.apache.lucene.util.ArrayUtil;
/*     */ import org.apache.lucene.util.CodecUtil;
/*     */ 
/*     */ public class FST<T>
/*     */ {
/*     */   public final INPUT_TYPE inputType;
/*     */   private static final int BIT_FINAL_ARC = 1;
/*     */   private static final int BIT_LAST_ARC = 2;
/*     */   private static final int BIT_TARGET_NEXT = 4;
/*     */   private static final int BIT_STOP_NODE = 8;
/*     */   private static final int BIT_ARC_HAS_OUTPUT = 16;
/*     */   private static final int BIT_ARC_HAS_FINAL_OUTPUT = 32;
/*     */   private static final int BIT_ARCS_AS_FIXED_ARRAY = 64;
/*     */   static final int FIXED_ARRAY_SHALLOW_DISTANCE = 3;
/*     */   static final int FIXED_ARRAY_NUM_ARCS_SHALLOW = 5;
/*     */   static final int FIXED_ARRAY_NUM_ARCS_DEEP = 10;
/*  76 */   private int[] bytesPerArc = new int[0];
/*     */   private static final String FILE_FORMAT_NAME = "FST";
/*     */   private static final int VERSION_START = 0;
/*     */   private static final int VERSION_INT_NUM_BYTES_PER_ARC = 1;
/*     */   private static final int VERSION_CURRENT = 1;
/*     */   private static final int FINAL_END_NODE = -1;
/*     */   private static final int NON_FINAL_END_NODE = 0;
/*     */   T emptyOutput;
/*     */   private byte[] emptyOutputBytes;
/*     */   private byte[] bytes;
/* 101 */   int byteUpto = 0;
/*     */ 
/* 103 */   private int startNode = -1;
/*     */   public final Outputs<T> outputs;
/*     */   private int lastFrozenNode;
/*     */   private final T NO_OUTPUT;
/*     */   public int nodeCount;
/*     */   public int arcCount;
/*     */   public int arcWithOutputCount;
/*     */   public static final int END_LABEL = -1;
/*     */   private Arc<T>[] cachedRootArcs;
/*     */   private final FST<T>.BytesWriter writer;
/*     */ 
/*     */   static boolean flag(int flags, int bit)
/*     */   {
/* 169 */     return (flags & bit) != 0;
/*     */   }
/*     */ 
/*     */   public FST(INPUT_TYPE inputType, Outputs<T> outputs)
/*     */   {
/* 176 */     this.inputType = inputType;
/* 177 */     this.outputs = outputs;
/* 178 */     this.bytes = new byte[''];
/* 179 */     this.NO_OUTPUT = outputs.getNoOutput();
/*     */ 
/* 181 */     this.writer = new BytesWriter();
/*     */ 
/* 183 */     this.emptyOutput = null;
/*     */   }
/*     */ 
/*     */   public FST(DataInput in, Outputs<T> outputs) throws IOException
/*     */   {
/* 188 */     this.outputs = outputs;
/* 189 */     this.writer = null;
/* 190 */     CodecUtil.checkHeader(in, "FST", 1, 1);
/* 191 */     if (in.readByte() == 1)
/*     */     {
/* 193 */       int numBytes = in.readVInt();
/*     */ 
/* 195 */       this.bytes = new byte[numBytes];
/* 196 */       in.readBytes(this.bytes, 0, numBytes);
/* 197 */       this.emptyOutput = outputs.read(getBytesReader(numBytes - 1));
/*     */     } else {
/* 199 */       this.emptyOutput = null;
/*     */     }
/* 201 */     byte t = in.readByte();
/* 202 */     switch (t) {
/*     */     case 0:
/* 204 */       this.inputType = INPUT_TYPE.BYTE1;
/* 205 */       break;
/*     */     case 1:
/* 207 */       this.inputType = INPUT_TYPE.BYTE2;
/* 208 */       break;
/*     */     case 2:
/* 210 */       this.inputType = INPUT_TYPE.BYTE4;
/* 211 */       break;
/*     */     default:
/* 213 */       throw new IllegalStateException("invalid input type " + t);
/*     */     }
/* 215 */     this.startNode = in.readVInt();
/* 216 */     this.nodeCount = in.readVInt();
/* 217 */     this.arcCount = in.readVInt();
/* 218 */     this.arcWithOutputCount = in.readVInt();
/*     */ 
/* 220 */     this.bytes = new byte[in.readVInt()];
/* 221 */     in.readBytes(this.bytes, 0, this.bytes.length);
/* 222 */     this.NO_OUTPUT = outputs.getNoOutput();
/*     */ 
/* 224 */     cacheRootArcs();
/*     */   }
/*     */ 
/*     */   public INPUT_TYPE getInputType() {
/* 228 */     return this.inputType;
/*     */   }
/*     */ 
/*     */   public int sizeInBytes()
/*     */   {
/* 233 */     return this.bytes.length;
/*     */   }
/*     */ 
/*     */   void finish(int startNode) throws IOException {
/* 237 */     if ((startNode == -1) && (this.emptyOutput != null)) {
/* 238 */       startNode = 0;
/*     */     }
/* 240 */     if (this.startNode != -1) {
/* 241 */       throw new IllegalStateException("already finished");
/*     */     }
/* 243 */     byte[] finalBytes = new byte[this.writer.posWrite];
/* 244 */     System.arraycopy(this.bytes, 0, finalBytes, 0, this.writer.posWrite);
/* 245 */     this.bytes = finalBytes;
/* 246 */     this.startNode = startNode;
/*     */ 
/* 248 */     cacheRootArcs();
/*     */   }
/*     */ 
/*     */   private void cacheRootArcs()
/*     */     throws IOException
/*     */   {
/* 254 */     this.cachedRootArcs = ((Arc[])new Arc['']);
/* 255 */     Arc arc = new Arc();
/* 256 */     getFirstArc(arc);
/* 257 */     BytesReader in = getBytesReader(0);
/* 258 */     if (targetHasArcs(arc)) {
/* 259 */       readFirstRealArc(arc.target, arc);
/*     */       while (true) {
/* 261 */         assert (arc.label != -1);
/* 262 */         if (arc.label >= this.cachedRootArcs.length) break;
/* 263 */         this.cachedRootArcs[arc.label] = new Arc().copyFrom(arc);
/*     */ 
/* 267 */         if (arc.isLast()) {
/*     */           break;
/*     */         }
/* 270 */         readNextRealArc(arc, in);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   void setEmptyOutput(T v) throws IOException {
/* 276 */     if (this.emptyOutput != null)
/* 277 */       this.emptyOutput = this.outputs.merge(this.emptyOutput, v);
/*     */     else {
/* 279 */       this.emptyOutput = v;
/*     */     }
/*     */ 
/* 284 */     int posSave = this.writer.posWrite;
/* 285 */     this.outputs.write(this.emptyOutput, this.writer);
/* 286 */     this.emptyOutputBytes = new byte[this.writer.posWrite - posSave];
/*     */ 
/* 289 */     int stopAt = (this.writer.posWrite - posSave) / 2;
/* 290 */     int upto = 0;
/* 291 */     while (upto < stopAt) {
/* 292 */       byte b = this.bytes[(posSave + upto)];
/* 293 */       this.bytes[(posSave + upto)] = this.bytes[(this.writer.posWrite - upto - 1)];
/* 294 */       this.bytes[(this.writer.posWrite - upto - 1)] = b;
/* 295 */       upto++;
/*     */     }
/* 297 */     System.arraycopy(this.bytes, posSave, this.emptyOutputBytes, 0, this.writer.posWrite - posSave);
/* 298 */     this.writer.posWrite = posSave;
/*     */   }
/*     */ 
/*     */   public void save(DataOutput out) throws IOException {
/* 302 */     if (this.startNode == -1) {
/* 303 */       throw new IllegalStateException("call finish first");
/*     */     }
/* 305 */     CodecUtil.writeHeader(out, "FST", 1);
/*     */ 
/* 308 */     if (this.emptyOutput != null) {
/* 309 */       out.writeByte(1);
/* 310 */       out.writeVInt(this.emptyOutputBytes.length);
/* 311 */       out.writeBytes(this.emptyOutputBytes, 0, this.emptyOutputBytes.length);
/*     */     } else {
/* 313 */       out.writeByte(0);
/*     */     }
/*     */     byte t;
/*     */     byte t;
/* 316 */     if (this.inputType == INPUT_TYPE.BYTE1) {
/* 317 */       t = 0;
/*     */     }
/*     */     else
/*     */     {
/*     */       byte t;
/* 318 */       if (this.inputType == INPUT_TYPE.BYTE2)
/* 319 */         t = 1;
/*     */       else
/* 321 */         t = 2;
/*     */     }
/* 323 */     out.writeByte(t);
/* 324 */     out.writeVInt(this.startNode);
/* 325 */     out.writeVInt(this.nodeCount);
/* 326 */     out.writeVInt(this.arcCount);
/* 327 */     out.writeVInt(this.arcWithOutputCount);
/* 328 */     out.writeVInt(this.bytes.length);
/* 329 */     out.writeBytes(this.bytes, 0, this.bytes.length);
/*     */   }
/*     */ 
/*     */   private void writeLabel(int v) throws IOException {
/* 333 */     assert (v >= 0) : ("v=" + v);
/* 334 */     if (this.inputType == INPUT_TYPE.BYTE1) {
/* 335 */       assert (v <= 255) : ("v=" + v);
/* 336 */       this.writer.writeByte((byte)v);
/* 337 */     } else if (this.inputType == INPUT_TYPE.BYTE2) {
/* 338 */       assert (v <= 65535) : ("v=" + v);
/* 339 */       this.writer.writeVInt(v);
/*     */     }
/*     */     else {
/* 342 */       this.writer.writeVInt(v);
/*     */     }
/*     */   }
/*     */ 
/*     */   int readLabel(DataInput in)
/*     */     throws IOException
/*     */   {
/*     */     int v;
/*     */     int v;
/* 348 */     if (this.inputType == INPUT_TYPE.BYTE1)
/* 349 */       v = in.readByte() & 0xFF;
/*     */     else {
/* 351 */       v = in.readVInt();
/*     */     }
/* 353 */     return v;
/*     */   }
/*     */ 
/*     */   public boolean targetHasArcs(Arc<T> arc)
/*     */   {
/* 359 */     return arc.target > 0;
/*     */   }
/*     */ 
/*     */   int addNode(Builder.UnCompiledNode<T> node)
/*     */     throws IOException
/*     */   {
/* 366 */     if (node.numArcs == 0) {
/* 367 */       if (node.isFinal) {
/* 368 */         return -1;
/*     */       }
/* 370 */       return 0;
/*     */     }
/*     */ 
/* 374 */     int startAddress = this.writer.posWrite;
/*     */ 
/* 377 */     boolean doFixedArray = shouldExpand(node);
/*     */     int fixedArrayStart;
/*     */     int fixedArrayStart;
/* 379 */     if (doFixedArray) {
/* 380 */       if (this.bytesPerArc.length < node.numArcs) {
/* 381 */         this.bytesPerArc = new int[ArrayUtil.oversize(node.numArcs, 1)];
/*     */       }
/*     */ 
/* 384 */       this.writer.writeByte(64);
/* 385 */       this.writer.writeVInt(node.numArcs);
/*     */ 
/* 389 */       this.writer.writeInt(0);
/* 390 */       fixedArrayStart = this.writer.posWrite;
/*     */     }
/*     */     else {
/* 393 */       fixedArrayStart = 0;
/*     */     }
/*     */ 
/* 396 */     this.nodeCount += 1;
/* 397 */     this.arcCount += node.numArcs;
/*     */ 
/* 399 */     int lastArc = node.numArcs - 1;
/*     */ 
/* 401 */     int lastArcStart = this.writer.posWrite;
/* 402 */     int maxBytesPerArc = 0;
/* 403 */     for (int arcIdx = 0; arcIdx < node.numArcs; arcIdx++) {
/* 404 */       Builder.Arc arc = node.arcs[arcIdx];
/* 405 */       Builder.CompiledNode target = (Builder.CompiledNode)arc.target;
/* 406 */       int flags = 0;
/*     */ 
/* 408 */       if (arcIdx == lastArc) {
/* 409 */         flags += 2;
/*     */       }
/*     */ 
/* 412 */       if ((this.lastFrozenNode == target.address) && (!doFixedArray)) {
/* 413 */         flags += 4;
/*     */       }
/*     */ 
/* 416 */       if (arc.isFinal) {
/* 417 */         flags++;
/* 418 */         if (arc.nextFinalOutput != this.NO_OUTPUT)
/* 419 */           flags += 32;
/*     */       }
/*     */       else {
/* 422 */         assert (arc.nextFinalOutput == this.NO_OUTPUT);
/*     */       }
/*     */ 
/* 425 */       boolean targetHasArcs = target.address > 0;
/*     */ 
/* 427 */       if (!targetHasArcs) {
/* 428 */         flags += 8;
/*     */       }
/*     */ 
/* 431 */       if (arc.output != this.NO_OUTPUT) {
/* 432 */         flags += 16;
/*     */       }
/*     */ 
/* 435 */       this.writer.writeByte((byte)flags);
/* 436 */       writeLabel(arc.label);
/*     */ 
/* 440 */       if (arc.output != this.NO_OUTPUT) {
/* 441 */         this.outputs.write(arc.output, this.writer);
/* 442 */         this.arcWithOutputCount += 1;
/*     */       }
/* 444 */       if (arc.nextFinalOutput != this.NO_OUTPUT) {
/* 445 */         this.outputs.write(arc.nextFinalOutput, this.writer);
/*     */       }
/*     */ 
/* 448 */       if ((targetHasArcs) && ((doFixedArray) || (this.lastFrozenNode != target.address))) {
/* 449 */         assert (target.address > 0);
/* 450 */         this.writer.writeInt(target.address);
/*     */       }
/*     */ 
/* 456 */       if (doFixedArray) {
/* 457 */         this.bytesPerArc[arcIdx] = (this.writer.posWrite - lastArcStart);
/* 458 */         lastArcStart = this.writer.posWrite;
/* 459 */         maxBytesPerArc = Math.max(maxBytesPerArc, this.bytesPerArc[arcIdx]);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 469 */     if (doFixedArray) {
/* 470 */       assert (maxBytesPerArc > 0);
/*     */ 
/* 473 */       int sizeNeeded = fixedArrayStart + node.numArcs * maxBytesPerArc;
/* 474 */       this.bytes = ArrayUtil.grow(this.bytes, sizeNeeded);
/*     */ 
/* 476 */       this.bytes[(fixedArrayStart - 4)] = (byte)(maxBytesPerArc >> 24);
/* 477 */       this.bytes[(fixedArrayStart - 3)] = (byte)(maxBytesPerArc >> 16);
/* 478 */       this.bytes[(fixedArrayStart - 2)] = (byte)(maxBytesPerArc >> 8);
/* 479 */       this.bytes[(fixedArrayStart - 1)] = (byte)maxBytesPerArc;
/*     */ 
/* 482 */       int srcPos = this.writer.posWrite;
/* 483 */       int destPos = fixedArrayStart + node.numArcs * maxBytesPerArc;
/* 484 */       this.writer.posWrite = destPos;
/* 485 */       for (int arcIdx = node.numArcs - 1; arcIdx >= 0; arcIdx--)
/*     */       {
/* 487 */         destPos -= maxBytesPerArc;
/* 488 */         srcPos -= this.bytesPerArc[arcIdx];
/* 489 */         if (srcPos != destPos) {
/* 490 */           assert (destPos > srcPos);
/* 491 */           System.arraycopy(this.bytes, srcPos, this.bytes, destPos, this.bytesPerArc[arcIdx]);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 499 */     int endAddress = this.lastFrozenNode = this.writer.posWrite - 1;
/*     */ 
/* 501 */     int left = startAddress;
/* 502 */     int right = endAddress;
/* 503 */     while (left < right) {
/* 504 */       byte b = this.bytes[left];
/* 505 */       this.bytes[(left++)] = this.bytes[right];
/* 506 */       this.bytes[(right--)] = b;
/*     */     }
/*     */ 
/* 509 */     return endAddress;
/*     */   }
/*     */ 
/*     */   public Arc<T> getFirstArc(Arc<T> arc)
/*     */   {
/* 515 */     if (this.emptyOutput != null) {
/* 516 */       arc.flags = 3;
/* 517 */       arc.nextFinalOutput = this.emptyOutput;
/*     */     } else {
/* 519 */       arc.flags = 2;
/* 520 */       arc.nextFinalOutput = this.NO_OUTPUT;
/*     */     }
/* 522 */     arc.output = this.NO_OUTPUT;
/*     */ 
/* 526 */     arc.target = this.startNode;
/* 527 */     return arc;
/*     */   }
/*     */ 
/*     */   public Arc<T> readLastTargetArc(Arc<T> follow, Arc<T> arc)
/*     */     throws IOException
/*     */   {
/* 538 */     if (!targetHasArcs(follow))
/*     */     {
/* 540 */       assert (follow.isFinal());
/* 541 */       arc.label = -1;
/* 542 */       arc.output = follow.nextFinalOutput;
/* 543 */       arc.flags = 2;
/* 544 */       return arc;
/*     */     }
/* 546 */     BytesReader in = getBytesReader(follow.target);
/* 547 */     arc.flags = in.readByte();
/* 548 */     if (arc.flag(64))
/*     */     {
/* 550 */       arc.numArcs = in.readVInt();
/* 551 */       arc.bytesPerArc = in.readInt();
/*     */ 
/* 553 */       arc.posArcsStart = in.pos;
/* 554 */       arc.arcIdx = (arc.numArcs - 2);
/*     */     }
/*     */     else {
/* 557 */       arc.bytesPerArc = 0;
/*     */ 
/* 559 */       while (!arc.isLast())
/*     */       {
/* 561 */         readLabel(in);
/* 562 */         if (arc.flag(16)) {
/* 563 */           this.outputs.read(in);
/*     */         }
/* 565 */         if (arc.flag(32)) {
/* 566 */           this.outputs.read(in);
/*     */         }
/* 568 */         if ((!arc.flag(8)) && 
/* 569 */           (!arc.flag(4)))
/*     */         {
/* 571 */           in.pos -= 4;
/*     */         }
/* 573 */         arc.flags = in.readByte();
/*     */       }
/* 575 */       arc.nextArc = (in.pos + 1);
/*     */     }
/* 577 */     readNextRealArc(arc, in);
/* 578 */     assert (arc.isLast());
/* 579 */     return arc;
/*     */   }
/*     */ 
/*     */   public Arc<T> readFirstTargetArc(Arc<T> follow, Arc<T> arc)
/*     */     throws IOException
/*     */   {
/* 593 */     if (follow.isFinal())
/*     */     {
/* 595 */       arc.label = -1;
/* 596 */       arc.output = follow.nextFinalOutput;
/* 597 */       if (follow.target <= 0) {
/* 598 */         arc.flags = 2;
/*     */       } else {
/* 600 */         arc.flags = 0;
/* 601 */         arc.nextArc = follow.target;
/*     */       }
/*     */ 
/* 604 */       return arc;
/*     */     }
/* 606 */     return readFirstRealArc(follow.target, arc);
/*     */   }
/*     */ 
/*     */   Arc<T> readFirstRealArc(int address, Arc<T> arc)
/*     */     throws IOException
/*     */   {
/* 613 */     BytesReader in = getBytesReader(address);
/*     */ 
/* 615 */     arc.flags = in.readByte();
/*     */ 
/* 617 */     if (arc.flag(64))
/*     */     {
/* 620 */       arc.numArcs = in.readVInt();
/* 621 */       arc.bytesPerArc = in.readInt();
/* 622 */       arc.arcIdx = -1;
/* 623 */       arc.nextArc = (arc.posArcsStart = in.pos);
/*     */     }
/*     */     else {
/* 626 */       arc.nextArc = address;
/* 627 */       arc.bytesPerArc = 0;
/*     */     }
/* 629 */     return readNextRealArc(arc, in);
/*     */   }
/*     */ 
/*     */   boolean isExpandedTarget(Arc<T> follow)
/*     */     throws IOException
/*     */   {
/* 639 */     if (!targetHasArcs(follow)) {
/* 640 */       return false;
/*     */     }
/* 642 */     BytesReader in = getBytesReader(follow.target);
/* 643 */     byte b = in.readByte();
/* 644 */     return (b & 0x40) != 0;
/*     */   }
/*     */ 
/*     */   public Arc<T> readNextArc(Arc<T> arc)
/*     */     throws IOException
/*     */   {
/* 650 */     if (arc.label == -1)
/*     */     {
/* 652 */       if (arc.nextArc <= 0)
/*     */       {
/* 654 */         return null;
/*     */       }
/* 656 */       return readFirstRealArc(arc.nextArc, arc);
/*     */     }
/* 658 */     return readNextRealArc(arc, getBytesReader(0));
/*     */   }
/*     */ 
/*     */   public int readNextArcLabel(Arc<T> arc)
/*     */     throws IOException
/*     */   {
/* 665 */     assert (!arc.isLast());
/*     */     BytesReader in;
/* 668 */     if (arc.label == -1)
/*     */     {
/* 670 */       BytesReader in = getBytesReader(arc.nextArc);
/* 671 */       byte flags = this.bytes[in.pos];
/* 672 */       if (flag(flags, 64))
/*     */       {
/* 674 */         in.pos -= 1;
/* 675 */         in.readVInt();
/* 676 */         in.readInt();
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/*     */       BytesReader in;
/* 679 */       if (arc.bytesPerArc != 0)
/*     */       {
/* 682 */         in = getBytesReader(arc.posArcsStart - (1 + arc.arcIdx) * arc.bytesPerArc);
/*     */       }
/*     */       else
/*     */       {
/* 686 */         in = getBytesReader(arc.nextArc);
/*     */       }
/*     */     }
/*     */ 
/* 690 */     in.readByte();
/* 691 */     return readLabel(in);
/*     */   }
/*     */ 
/*     */   Arc<T> readNextRealArc(Arc<T> arc, FST<T>.BytesReader in) throws IOException
/*     */   {
/* 696 */     if (arc.bytesPerArc != 0)
/*     */     {
/* 698 */       arc.arcIdx += 1;
/* 699 */       assert (arc.arcIdx < arc.numArcs);
/* 700 */       in.pos = (arc.posArcsStart - arc.arcIdx * arc.bytesPerArc);
/*     */     }
/*     */     else {
/* 703 */       in.pos = arc.nextArc;
/*     */     }
/* 705 */     arc.flags = in.readByte();
/* 706 */     arc.label = readLabel(in);
/*     */ 
/* 708 */     if (arc.flag(16))
/* 709 */       arc.output = this.outputs.read(in);
/*     */     else {
/* 711 */       arc.output = this.outputs.getNoOutput();
/*     */     }
/*     */ 
/* 714 */     if (arc.flag(32))
/* 715 */       arc.nextFinalOutput = this.outputs.read(in);
/*     */     else {
/* 717 */       arc.nextFinalOutput = this.outputs.getNoOutput();
/*     */     }
/*     */ 
/* 720 */     if (arc.flag(8)) {
/* 721 */       if (arc.flag(1))
/* 722 */         arc.target = -1;
/*     */       else {
/* 724 */         arc.target = 0;
/*     */       }
/* 726 */       arc.nextArc = in.pos;
/* 727 */     } else if (arc.flag(4)) {
/* 728 */       arc.nextArc = in.pos;
/* 729 */       if (!arc.flag(2)) {
/* 730 */         if (arc.bytesPerArc == 0)
/*     */         {
/* 732 */           seekToNextNode(in);
/*     */         }
/* 734 */         else in.pos = (arc.posArcsStart - arc.bytesPerArc * arc.numArcs);
/*     */       }
/*     */ 
/* 737 */       arc.target = in.pos;
/*     */     } else {
/* 739 */       arc.target = in.readInt();
/* 740 */       arc.nextArc = in.pos;
/*     */     }
/*     */ 
/* 743 */     return arc;
/*     */   }
/*     */ 
/*     */   public Arc<T> findTargetArc(int labelToMatch, Arc<T> follow, Arc<T> arc)
/*     */     throws IOException
/*     */   {
/* 749 */     assert (this.cachedRootArcs != null);
/*     */ 
/* 751 */     if ((follow.target == this.startNode) && (labelToMatch != -1) && (labelToMatch < this.cachedRootArcs.length)) {
/* 752 */       Arc result = this.cachedRootArcs[labelToMatch];
/* 753 */       if (result == null) {
/* 754 */         return result;
/*     */       }
/* 756 */       arc.copyFrom(result);
/* 757 */       return arc;
/*     */     }
/*     */ 
/* 761 */     if (labelToMatch == -1) {
/* 762 */       if (follow.isFinal()) {
/* 763 */         arc.output = follow.nextFinalOutput;
/* 764 */         arc.label = -1;
/* 765 */         return arc;
/*     */       }
/* 767 */       return null;
/*     */     }
/*     */ 
/* 771 */     if (!targetHasArcs(follow)) {
/* 772 */       return null;
/*     */     }
/*     */ 
/* 777 */     BytesReader in = getBytesReader(follow.target);
/*     */ 
/* 781 */     if ((in.readByte() & 0x40) != 0)
/*     */     {
/* 783 */       arc.numArcs = in.readVInt();
/*     */ 
/* 785 */       arc.bytesPerArc = in.readInt();
/* 786 */       arc.posArcsStart = in.pos;
/* 787 */       int low = 0;
/* 788 */       int high = arc.numArcs - 1;
/* 789 */       while (low <= high)
/*     */       {
/* 791 */         int mid = low + high >>> 1;
/* 792 */         in.pos = (arc.posArcsStart - arc.bytesPerArc * mid - 1);
/* 793 */         int midLabel = readLabel(in);
/* 794 */         int cmp = midLabel - labelToMatch;
/* 795 */         if (cmp < 0) {
/* 796 */           low = mid + 1;
/* 797 */         } else if (cmp > 0) {
/* 798 */           high = mid - 1;
/*     */         } else {
/* 800 */           arc.arcIdx = (mid - 1);
/*     */ 
/* 802 */           return readNextRealArc(arc, in);
/*     */         }
/*     */       }
/*     */ 
/* 806 */       return null;
/*     */     }
/*     */ 
/* 810 */     readFirstTargetArc(follow, arc);
/*     */     while (true)
/*     */     {
/* 816 */       if (arc.label == labelToMatch)
/*     */       {
/* 818 */         return arc;
/* 819 */       }if (arc.label > labelToMatch)
/* 820 */         return null;
/* 821 */       if (arc.isLast()) {
/* 822 */         return null;
/*     */       }
/* 824 */       readNextArc(arc);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void seekToNextNode(FST<T>.BytesReader in)
/*     */     throws IOException
/*     */   {
/*     */     while (true)
/*     */     {
/* 833 */       int flags = in.readByte();
/* 834 */       readLabel(in);
/*     */ 
/* 836 */       if (flag(flags, 16)) {
/* 837 */         this.outputs.read(in);
/*     */       }
/*     */ 
/* 840 */       if (flag(flags, 32)) {
/* 841 */         this.outputs.read(in);
/*     */       }
/*     */ 
/* 844 */       if ((!flag(flags, 8)) && (!flag(flags, 4))) {
/* 845 */         in.readInt();
/*     */       }
/*     */ 
/* 848 */       if (flag(flags, 2))
/* 849 */         return;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getNodeCount()
/*     */   {
/* 856 */     return 1 + this.nodeCount;
/*     */   }
/*     */ 
/*     */   public int getArcCount() {
/* 860 */     return this.arcCount;
/*     */   }
/*     */ 
/*     */   public int getArcWithOutputCount() {
/* 864 */     return this.arcWithOutputCount;
/*     */   }
/*     */ 
/*     */   private boolean shouldExpand(Builder.UnCompiledNode<T> node)
/*     */   {
/* 883 */     return ((node.depth <= 3) && (node.numArcs >= 5)) || (node.numArcs >= 10);
/*     */   }
/*     */ 
/*     */   final FST<T>.BytesReader getBytesReader(int pos)
/*     */   {
/* 917 */     return new BytesReader(pos);
/*     */   }
/*     */ 
/*     */   final class BytesReader extends DataInput {
/*     */     int pos;
/*     */ 
/*     */     public BytesReader(int pos) {
/* 925 */       this.pos = pos;
/*     */     }
/*     */ 
/*     */     public byte readByte()
/*     */     {
/* 930 */       return FST.this.bytes[(this.pos--)];
/*     */     }
/*     */ 
/*     */     public void readBytes(byte[] b, int offset, int len)
/*     */     {
/* 935 */       for (int i = 0; i < len; i++)
/* 936 */         b[(offset + i)] = FST.access$000(FST.this)[(this.pos--)];
/*     */     }
/*     */   }
/*     */ 
/*     */   class BytesWriter extends DataOutput
/*     */   {
/*     */     int posWrite;
/*     */ 
/*     */     public BytesWriter()
/*     */     {
/* 894 */       this.posWrite = 1;
/*     */     }
/*     */ 
/*     */     public void writeByte(byte b)
/*     */     {
/* 899 */       if (FST.this.bytes.length == this.posWrite) {
/* 900 */         FST.access$002(FST.this, ArrayUtil.grow(FST.this.bytes));
/*     */       }
/* 902 */       assert (this.posWrite < FST.this.bytes.length) : ("posWrite=" + this.posWrite + " bytes.length=" + FST.this.bytes.length);
/* 903 */       FST.this.bytes[(this.posWrite++)] = b;
/*     */     }
/*     */ 
/*     */     public void writeBytes(byte[] b, int offset, int length)
/*     */     {
/* 908 */       int size = this.posWrite + length;
/* 909 */       FST.access$002(FST.this, ArrayUtil.grow(FST.this.bytes, size));
/* 910 */       System.arraycopy(b, offset, FST.this.bytes, this.posWrite, length);
/* 911 */       this.posWrite += length;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final class Arc<T>
/*     */   {
/*     */     public int label;
/*     */     public T output;
/*     */     int target;
/*     */     byte flags;
/*     */     public T nextFinalOutput;
/*     */     int nextArc;
/*     */     int posArcsStart;
/*     */     int bytesPerArc;
/*     */     int arcIdx;
/*     */     int numArcs;
/*     */ 
/*     */     public Arc<T> copyFrom(Arc<T> other)
/*     */     {
/* 138 */       this.label = other.label;
/* 139 */       this.target = other.target;
/* 140 */       this.flags = other.flags;
/* 141 */       this.output = other.output;
/* 142 */       this.nextFinalOutput = other.nextFinalOutput;
/* 143 */       this.nextArc = other.nextArc;
/* 144 */       if (other.bytesPerArc != 0) {
/* 145 */         this.bytesPerArc = other.bytesPerArc;
/* 146 */         this.posArcsStart = other.posArcsStart;
/* 147 */         this.arcIdx = other.arcIdx;
/* 148 */         this.numArcs = other.numArcs;
/*     */       } else {
/* 150 */         this.bytesPerArc = 0;
/*     */       }
/* 152 */       return this;
/*     */     }
/*     */ 
/*     */     boolean flag(int flag) {
/* 156 */       return FST.flag(this.flags, flag);
/*     */     }
/*     */ 
/*     */     public boolean isLast() {
/* 160 */       return flag(2);
/*     */     }
/*     */ 
/*     */     public boolean isFinal() {
/* 164 */       return flag(1);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static enum INPUT_TYPE
/*     */   {
/*  46 */     BYTE1, BYTE2, BYTE4;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.fst.FST
 * JD-Core Version:    0.6.0
 */