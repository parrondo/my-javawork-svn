/*     */ package org.apache.lucene.util.fst;
/*     */ 
/*     */ import java.io.IOException;
/*     */ 
/*     */ final class NodeHash<T>
/*     */ {
/*     */   private int[] table;
/*     */   private int count;
/*     */   private int mask;
/*     */   private final FST<T> fst;
/*  29 */   private final FST.Arc<T> scratchArc = new FST.Arc();
/*     */ 
/*     */   public NodeHash(FST<T> fst) {
/*  32 */     this.table = new int[16];
/*  33 */     this.mask = 15;
/*  34 */     this.fst = fst;
/*     */   }
/*     */ 
/*     */   private boolean nodesEqual(Builder.UnCompiledNode<T> node, int address) throws IOException {
/*  38 */     FST.BytesReader in = this.fst.getBytesReader(0);
/*  39 */     this.fst.readFirstRealArc(address, this.scratchArc);
/*  40 */     if ((this.scratchArc.bytesPerArc != 0) && (node.numArcs != this.scratchArc.numArcs)) {
/*  41 */       return false;
/*     */     }
/*  43 */     for (int arcUpto = 0; arcUpto < node.numArcs; arcUpto++) {
/*  44 */       Builder.Arc arc = node.arcs[arcUpto];
/*  45 */       if ((arc.label != this.scratchArc.label) || (!arc.output.equals(this.scratchArc.output)) || (((Builder.CompiledNode)arc.target).address != this.scratchArc.target) || (!arc.nextFinalOutput.equals(this.scratchArc.nextFinalOutput)) || (arc.isFinal != this.scratchArc.isFinal()))
/*     */       {
/*  50 */         return false;
/*     */       }
/*     */ 
/*  53 */       if (this.scratchArc.isLast())
/*     */       {
/*  55 */         return arcUpto == node.numArcs - 1;
/*     */       }
/*     */ 
/*  60 */       this.fst.readNextRealArc(this.scratchArc, in);
/*     */     }
/*     */ 
/*  63 */     return false;
/*     */   }
/*     */ 
/*     */   private int hash(Builder.UnCompiledNode<T> node)
/*     */   {
/*  69 */     int PRIME = 31;
/*     */ 
/*  71 */     int h = 0;
/*     */ 
/*  73 */     for (int arcIdx = 0; arcIdx < node.numArcs; arcIdx++) {
/*  74 */       Builder.Arc arc = node.arcs[arcIdx];
/*     */ 
/*  76 */       h = 31 * h + arc.label;
/*  77 */       h = 31 * h + ((Builder.CompiledNode)arc.target).address;
/*  78 */       h = 31 * h + arc.output.hashCode();
/*  79 */       h = 31 * h + arc.nextFinalOutput.hashCode();
/*  80 */       if (arc.isFinal) {
/*  81 */         h += 17;
/*     */       }
/*     */     }
/*     */ 
/*  85 */     return h & 0x7FFFFFFF;
/*     */   }
/*     */ 
/*     */   private int hash(int node) throws IOException
/*     */   {
/*  90 */     int PRIME = 31;
/*  91 */     FST.BytesReader in = this.fst.getBytesReader(0);
/*     */ 
/*  93 */     int h = 0;
/*  94 */     this.fst.readFirstRealArc(node, this.scratchArc);
/*     */     while (true)
/*     */     {
/*  97 */       h = 31 * h + this.scratchArc.label;
/*  98 */       h = 31 * h + this.scratchArc.target;
/*  99 */       h = 31 * h + this.scratchArc.output.hashCode();
/* 100 */       h = 31 * h + this.scratchArc.nextFinalOutput.hashCode();
/* 101 */       if (this.scratchArc.isFinal()) {
/* 102 */         h += 17;
/*     */       }
/* 104 */       if (this.scratchArc.isLast()) {
/*     */         break;
/*     */       }
/* 107 */       this.fst.readNextRealArc(this.scratchArc, in);
/*     */     }
/*     */ 
/* 110 */     return h & 0x7FFFFFFF;
/*     */   }
/*     */ 
/*     */   public int add(Builder.UnCompiledNode<T> node) throws IOException
/*     */   {
/* 115 */     int h = hash(node);
/* 116 */     int pos = h & this.mask;
/* 117 */     int c = 0;
/*     */     while (true) {
/* 119 */       int v = this.table[pos];
/* 120 */       if (v == 0)
/*     */       {
/* 122 */         int address = this.fst.addNode(node);
/*     */ 
/* 124 */         assert (hash(address) == h) : ("frozenHash=" + hash(address) + " vs h=" + h);
/* 125 */         this.count += 1;
/* 126 */         this.table[pos] = address;
/* 127 */         if (this.table.length < 2 * this.count) {
/* 128 */           rehash();
/*     */         }
/* 130 */         return address;
/* 131 */       }if (nodesEqual(node, v))
/*     */       {
/* 133 */         return v;
/*     */       }
/*     */ 
/* 137 */       c++; pos = pos + c & this.mask;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void addNew(int address) throws IOException
/*     */   {
/* 143 */     int pos = hash(address) & this.mask;
/* 144 */     int c = 0;
/*     */     while (true) {
/* 146 */       if (this.table[pos] == 0) {
/* 147 */         this.table[pos] = address;
/* 148 */         break;
/*     */       }
/*     */ 
/* 152 */       c++; pos = pos + c & this.mask;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void rehash() throws IOException {
/* 157 */     int[] oldTable = this.table;
/* 158 */     this.table = new int[2 * this.table.length];
/* 159 */     this.mask = (this.table.length - 1);
/* 160 */     for (int idx = 0; idx < oldTable.length; idx++) {
/* 161 */       int address = oldTable[idx];
/* 162 */       if (address != 0)
/* 163 */         addNew(address);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int count()
/*     */   {
/* 169 */     return this.count;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.fst.NodeHash
 * JD-Core Version:    0.6.0
 */