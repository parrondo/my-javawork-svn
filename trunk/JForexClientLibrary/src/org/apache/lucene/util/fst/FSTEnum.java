/*     */ package org.apache.lucene.util.fst;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.util.ArrayUtil;
/*     */ import org.apache.lucene.util.RamUsageEstimator;
/*     */ 
/*     */ abstract class FSTEnum<T>
/*     */ {
/*     */   protected final FST<T> fst;
/*  33 */   protected FST.Arc<T>[] arcs = new FST.Arc[10];
/*     */ 
/*  35 */   protected T[] output = (Object[])new Object[10];
/*     */   protected final T NO_OUTPUT;
/*  38 */   protected final FST.Arc<T> scratchArc = new FST.Arc();
/*     */   protected int upto;
/*     */   protected int targetLength;
/*     */ 
/*     */   protected FSTEnum(FST<T> fst)
/*     */   {
/*  47 */     this.fst = fst;
/*  48 */     this.NO_OUTPUT = fst.outputs.getNoOutput();
/*  49 */     fst.getFirstArc(getArc(0));
/*  50 */     this.output[0] = this.NO_OUTPUT; } 
/*     */   protected abstract int getTargetLabel();
/*     */ 
/*     */   protected abstract int getCurrentLabel();
/*     */ 
/*     */   protected abstract void setCurrentLabel(int paramInt);
/*     */ 
/*     */   protected abstract void grow();
/*     */ 
/*  62 */   protected final void rewindPrefix() throws IOException { if (this.upto == 0)
/*     */     {
/*  64 */       this.upto = 1;
/*  65 */       this.fst.readFirstTargetArc(getArc(0), getArc(1));
/*  66 */       return;
/*     */     }
/*     */ 
/*  70 */     int currentLimit = this.upto;
/*  71 */     this.upto = 1;
/*  72 */     while ((this.upto < currentLimit) && (this.upto <= this.targetLength + 1)) {
/*  73 */       int cmp = getCurrentLabel() - getTargetLabel();
/*  74 */       if (cmp < 0) {
/*     */         break;
/*     */       }
/*  77 */       if (cmp > 0)
/*     */       {
/*  79 */         FST.Arc arc = getArc(this.upto);
/*  80 */         this.fst.readFirstTargetArc(getArc(this.upto - 1), arc);
/*     */ 
/*  82 */         break;
/*     */       }
/*  84 */       this.upto += 1;
/*     */     } }
/*     */ 
/*     */   protected void doNext()
/*     */     throws IOException
/*     */   {
/*  90 */     if (this.upto == 0)
/*     */     {
/*  92 */       this.upto = 1;
/*  93 */       this.fst.readFirstTargetArc(getArc(0), getArc(1));
/*     */     }
/*     */     else
/*     */     {
/*  97 */       while (this.arcs[this.upto].isLast()) {
/*  98 */         this.upto -= 1;
/*  99 */         if (this.upto == 0)
/*     */         {
/* 101 */           return;
/*     */         }
/*     */       }
/* 104 */       this.fst.readNextArc(this.arcs[this.upto]);
/*     */     }
/*     */ 
/* 107 */     pushFirst();
/*     */   }
/*     */ 
/*     */   protected void doSeekCeil()
/*     */     throws IOException
/*     */   {
/* 127 */     rewindPrefix();
/*     */ 
/* 130 */     FST.Arc arc = getArc(this.upto);
/* 131 */     int targetLabel = getTargetLabel();
/*     */     while (true)
/*     */     {
/* 139 */       if ((arc.bytesPerArc != 0) && (arc.label != -1))
/*     */       {
/* 144 */         FST.BytesReader in = this.fst.getBytesReader(0);
/* 145 */         int low = arc.arcIdx;
/* 146 */         int high = arc.numArcs - 1;
/* 147 */         int mid = 0;
/*     */ 
/* 149 */         boolean found = false;
/* 150 */         while (low <= high) {
/* 151 */           mid = low + high >>> 1;
/* 152 */           in.pos = (arc.posArcsStart - arc.bytesPerArc * mid - 1);
/* 153 */           int midLabel = this.fst.readLabel(in);
/* 154 */           int cmp = midLabel - targetLabel;
/*     */ 
/* 156 */           if (cmp < 0) {
/* 157 */             low = mid + 1;
/* 158 */           } else if (cmp > 0) {
/* 159 */             high = mid - 1;
/*     */           } else {
/* 161 */             found = true;
/* 162 */             break;
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 168 */         if (found)
/*     */         {
/* 170 */           arc.arcIdx = (mid - 1);
/* 171 */           this.fst.readNextRealArc(arc, in);
/* 172 */           assert (arc.arcIdx == mid);
/* 173 */           assert (arc.label == targetLabel) : ("arc.label=" + arc.label + " vs targetLabel=" + targetLabel + " mid=" + mid);
/* 174 */           this.output[this.upto] = this.fst.outputs.add(this.output[(this.upto - 1)], arc.output);
/* 175 */           if (targetLabel == -1) {
/* 176 */             return;
/*     */           }
/* 178 */           setCurrentLabel(arc.label);
/* 179 */           incr();
/* 180 */           arc = this.fst.readFirstTargetArc(arc, getArc(this.upto));
/* 181 */           targetLabel = getTargetLabel();
/* 182 */           continue;
/* 183 */         }if (low == arc.numArcs)
/*     */         {
/* 185 */           arc.arcIdx = (arc.numArcs - 2);
/* 186 */           this.fst.readNextRealArc(arc, in);
/* 187 */           assert (arc.isLast());
/*     */ 
/* 190 */           this.upto -= 1;
/*     */           while (true) {
/* 192 */             if (this.upto == 0) {
/* 193 */               return;
/*     */             }
/* 195 */             FST.Arc prevArc = getArc(this.upto);
/*     */ 
/* 197 */             if (!prevArc.isLast()) {
/* 198 */               this.fst.readNextArc(prevArc);
/* 199 */               pushFirst();
/* 200 */               return;
/*     */             }
/* 202 */             this.upto -= 1;
/*     */           }
/*     */         }
/* 205 */         arc.arcIdx = ((low > high ? low : high) - 1);
/* 206 */         this.fst.readNextRealArc(arc, in);
/* 207 */         assert (arc.label > targetLabel);
/* 208 */         pushFirst();
/* 209 */         return;
/*     */       }
/*     */ 
/* 213 */       if (arc.label == targetLabel)
/*     */       {
/* 215 */         this.output[this.upto] = this.fst.outputs.add(this.output[(this.upto - 1)], arc.output);
/* 216 */         if (targetLabel == -1) {
/* 217 */           return;
/*     */         }
/* 219 */         setCurrentLabel(arc.label);
/* 220 */         incr();
/* 221 */         arc = this.fst.readFirstTargetArc(arc, getArc(this.upto));
/* 222 */         targetLabel = getTargetLabel(); continue;
/* 223 */       }if (arc.label > targetLabel) {
/* 224 */         pushFirst();
/* 225 */         return;
/* 226 */       }if (arc.isLast())
/*     */       {
/* 229 */         this.upto -= 1;
/*     */         while (true) {
/* 231 */           if (this.upto == 0) {
/* 232 */             return;
/*     */           }
/* 234 */           FST.Arc prevArc = getArc(this.upto);
/*     */ 
/* 236 */           if (!prevArc.isLast()) {
/* 237 */             this.fst.readNextArc(prevArc);
/* 238 */             pushFirst();
/* 239 */             return;
/*     */           }
/* 241 */           this.upto -= 1;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 246 */       this.fst.readNextArc(arc);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void doSeekFloor()
/*     */     throws IOException
/*     */   {
/* 265 */     rewindPrefix();
/*     */ 
/* 269 */     FST.Arc arc = getArc(this.upto);
/* 270 */     int targetLabel = getTargetLabel();
/*     */     while (true)
/*     */     {
/* 278 */       if ((arc.bytesPerArc != 0) && (arc.label != -1))
/*     */       {
/* 282 */         FST.BytesReader in = this.fst.getBytesReader(0);
/* 283 */         int low = arc.arcIdx;
/* 284 */         int high = arc.numArcs - 1;
/* 285 */         int mid = 0;
/*     */ 
/* 287 */         boolean found = false;
/* 288 */         while (low <= high) {
/* 289 */           mid = low + high >>> 1;
/* 290 */           in.pos = (arc.posArcsStart - arc.bytesPerArc * mid - 1);
/* 291 */           int midLabel = this.fst.readLabel(in);
/* 292 */           int cmp = midLabel - targetLabel;
/*     */ 
/* 294 */           if (cmp < 0) {
/* 295 */             low = mid + 1;
/* 296 */           } else if (cmp > 0) {
/* 297 */             high = mid - 1;
/*     */           } else {
/* 299 */             found = true;
/* 300 */             break;
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 306 */         if (found)
/*     */         {
/* 309 */           arc.arcIdx = (mid - 1);
/* 310 */           this.fst.readNextRealArc(arc, in);
/* 311 */           assert (arc.arcIdx == mid);
/* 312 */           assert (arc.label == targetLabel) : ("arc.label=" + arc.label + " vs targetLabel=" + targetLabel + " mid=" + mid);
/* 313 */           this.output[this.upto] = this.fst.outputs.add(this.output[(this.upto - 1)], arc.output);
/* 314 */           if (targetLabel == -1) {
/* 315 */             return;
/*     */           }
/* 317 */           setCurrentLabel(arc.label);
/* 318 */           incr();
/* 319 */           arc = this.fst.readFirstTargetArc(arc, getArc(this.upto));
/* 320 */           targetLabel = getTargetLabel();
/* 321 */           continue;
/* 322 */         }if (high == -1)
/*     */         {
/*     */           while (true)
/*     */           {
/* 332 */             this.fst.readFirstTargetArc(getArc(this.upto - 1), arc);
/* 333 */             if (arc.label < targetLabel)
/*     */             {
/* 336 */               while ((!arc.isLast()) && (this.fst.readNextArcLabel(arc) < targetLabel)) {
/* 337 */                 this.fst.readNextArc(arc);
/*     */               }
/* 339 */               pushLast();
/* 340 */               return;
/*     */             }
/* 342 */             this.upto -= 1;
/* 343 */             if (this.upto == 0) {
/* 344 */               return;
/*     */             }
/* 346 */             targetLabel = getTargetLabel();
/* 347 */             arc = getArc(this.upto);
/*     */           }
/*     */         }
/*     */ 
/* 351 */         arc.arcIdx = ((low > high ? high : low) - 1);
/*     */ 
/* 353 */         this.fst.readNextRealArc(arc, in);
/* 354 */         assert ((arc.isLast()) || (this.fst.readNextArcLabel(arc) > targetLabel));
/* 355 */         assert (arc.label < targetLabel);
/* 356 */         pushLast();
/* 357 */         return;
/*     */       }
/*     */ 
/* 361 */       if (arc.label == targetLabel)
/*     */       {
/* 363 */         this.output[this.upto] = this.fst.outputs.add(this.output[(this.upto - 1)], arc.output);
/* 364 */         if (targetLabel == -1) {
/* 365 */           return;
/*     */         }
/* 367 */         setCurrentLabel(arc.label);
/* 368 */         incr();
/* 369 */         arc = this.fst.readFirstTargetArc(arc, getArc(this.upto));
/* 370 */         targetLabel = getTargetLabel(); continue;
/* 371 */       }if (arc.label > targetLabel)
/*     */       {
/*     */         while (true)
/*     */         {
/* 379 */           this.fst.readFirstTargetArc(getArc(this.upto - 1), arc);
/* 380 */           if (arc.label < targetLabel)
/*     */           {
/* 383 */             while ((!arc.isLast()) && (this.fst.readNextArcLabel(arc) < targetLabel)) {
/* 384 */               this.fst.readNextArc(arc);
/*     */             }
/* 386 */             pushLast();
/* 387 */             return;
/*     */           }
/* 389 */           this.upto -= 1;
/* 390 */           if (this.upto == 0) {
/* 391 */             return;
/*     */           }
/* 393 */           targetLabel = getTargetLabel();
/* 394 */           arc = getArc(this.upto);
/*     */         }
/*     */       }
/* 396 */       if (arc.isLast())
/*     */         break;
/* 398 */       if (this.fst.readNextArcLabel(arc) > targetLabel) {
/* 399 */         pushLast();
/* 400 */         return;
/*     */       }
/*     */ 
/* 403 */       this.fst.readNextArc(arc);
/*     */     }
/*     */ 
/* 406 */     pushLast();
/*     */   }
/*     */ 
/*     */   private void incr()
/*     */   {
/* 414 */     this.upto += 1;
/* 415 */     grow();
/* 416 */     if (this.arcs.length <= this.upto) {
/* 417 */       FST.Arc[] newArcs = new FST.Arc[ArrayUtil.oversize(1 + this.upto, RamUsageEstimator.NUM_BYTES_OBJECT_REF)];
/*     */ 
/* 419 */       System.arraycopy(this.arcs, 0, newArcs, 0, this.arcs.length);
/* 420 */       this.arcs = newArcs;
/*     */     }
/* 422 */     if (this.output.length <= this.upto) {
/* 423 */       Object[] newOutput = (Object[])new Object[ArrayUtil.oversize(1 + this.upto, RamUsageEstimator.NUM_BYTES_OBJECT_REF)];
/*     */ 
/* 425 */       System.arraycopy(this.output, 0, newOutput, 0, this.output.length);
/* 426 */       this.output = newOutput;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void pushFirst()
/*     */     throws IOException
/*     */   {
/* 434 */     FST.Arc arc = this.arcs[this.upto];
/* 435 */     assert (arc != null);
/*     */     while (true)
/*     */     {
/* 438 */       this.output[this.upto] = this.fst.outputs.add(this.output[(this.upto - 1)], arc.output);
/* 439 */       if (arc.label == -1)
/*     */       {
/*     */         break;
/*     */       }
/*     */ 
/* 444 */       setCurrentLabel(arc.label);
/* 445 */       incr();
/*     */ 
/* 447 */       FST.Arc nextArc = getArc(this.upto);
/* 448 */       this.fst.readFirstTargetArc(arc, nextArc);
/* 449 */       arc = nextArc;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void pushLast()
/*     */     throws IOException
/*     */   {
/* 457 */     FST.Arc arc = this.arcs[this.upto];
/* 458 */     assert (arc != null);
/*     */     while (true)
/*     */     {
/* 461 */       setCurrentLabel(arc.label);
/* 462 */       this.output[this.upto] = this.fst.outputs.add(this.output[(this.upto - 1)], arc.output);
/* 463 */       if (arc.label == -1)
/*     */       {
/*     */         break;
/*     */       }
/* 467 */       incr();
/*     */ 
/* 469 */       arc = this.fst.readLastTargetArc(arc, getArc(this.upto));
/*     */     }
/*     */   }
/*     */ 
/*     */   private FST.Arc<T> getArc(int idx) {
/* 474 */     if (this.arcs[idx] == null) {
/* 475 */       this.arcs[idx] = new FST.Arc();
/*     */     }
/* 477 */     return this.arcs[idx];
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.fst.FSTEnum
 * JD-Core Version:    0.6.0
 */