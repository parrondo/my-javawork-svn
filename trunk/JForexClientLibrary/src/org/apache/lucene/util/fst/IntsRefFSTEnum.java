/*     */ package org.apache.lucene.util.fst;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.util.IntsRef;
/*     */ 
/*     */ public final class IntsRefFSTEnum<T> extends FSTEnum<T>
/*     */ {
/*  30 */   private final IntsRef current = new IntsRef(10);
/*  31 */   private final InputOutput<T> result = new InputOutput();
/*     */   private IntsRef target;
/*     */ 
/*     */   public IntsRefFSTEnum(FST<T> fst)
/*     */   {
/*  43 */     super(fst);
/*  44 */     this.result.input = this.current;
/*  45 */     this.current.offset = 1;
/*     */   }
/*     */ 
/*     */   public InputOutput<T> current() {
/*  49 */     return this.result;
/*     */   }
/*     */ 
/*     */   public InputOutput<T> next() throws IOException
/*     */   {
/*  54 */     doNext();
/*  55 */     return setResult();
/*     */   }
/*     */ 
/*     */   public InputOutput<T> seekCeil(IntsRef target) throws IOException
/*     */   {
/*  60 */     this.target = target;
/*  61 */     this.targetLength = target.length;
/*  62 */     super.doSeekCeil();
/*  63 */     return setResult();
/*     */   }
/*     */ 
/*     */   public InputOutput<T> seekFloor(IntsRef target) throws IOException
/*     */   {
/*  68 */     this.target = target;
/*  69 */     this.targetLength = target.length;
/*  70 */     super.doSeekFloor();
/*  71 */     return setResult();
/*     */   }
/*     */ 
/*     */   protected int getTargetLabel()
/*     */   {
/*  76 */     if (this.upto - 1 == this.target.length) {
/*  77 */       return -1;
/*     */     }
/*  79 */     return this.target.ints[(this.target.offset + this.upto - 1)];
/*     */   }
/*     */ 
/*     */   protected int getCurrentLabel()
/*     */   {
/*  86 */     return this.current.ints[this.upto];
/*     */   }
/*     */ 
/*     */   protected void setCurrentLabel(int label)
/*     */   {
/*  91 */     this.current.ints[this.upto] = label;
/*     */   }
/*     */ 
/*     */   protected void grow()
/*     */   {
/*  96 */     this.current.grow(this.upto + 1);
/*     */   }
/*     */ 
/*     */   private InputOutput<T> setResult() {
/* 100 */     if (this.upto == 0) {
/* 101 */       return null;
/*     */     }
/* 103 */     this.current.length = (this.upto - 1);
/* 104 */     this.result.output = this.output[this.upto];
/* 105 */     return this.result;
/*     */   }
/*     */ 
/*     */   public static class InputOutput<T>
/*     */   {
/*     */     public IntsRef input;
/*     */     public T output;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.fst.IntsRefFSTEnum
 * JD-Core Version:    0.6.0
 */