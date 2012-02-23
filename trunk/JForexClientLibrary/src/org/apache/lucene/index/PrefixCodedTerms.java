/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Iterator;
/*     */ import org.apache.lucene.store.IndexInput;
/*     */ import org.apache.lucene.store.RAMFile;
/*     */ import org.apache.lucene.store.RAMInputStream;
/*     */ import org.apache.lucene.store.RAMOutputStream;
/*     */ import org.apache.lucene.util.BytesRef;
/*     */ import org.apache.lucene.util.StringHelper;
/*     */ 
/*     */ class PrefixCodedTerms
/*     */   implements Iterable<Term>
/*     */ {
/*     */   final RAMFile buffer;
/*     */ 
/*     */   private PrefixCodedTerms(RAMFile buffer)
/*     */   {
/*  39 */     this.buffer = buffer;
/*     */   }
/*     */ 
/*     */   public long getSizeInBytes()
/*     */   {
/*  44 */     return this.buffer.getSizeInBytes();
/*     */   }
/*     */ 
/*     */   public Iterator<Term> iterator()
/*     */   {
/*  49 */     return new PrefixCodedTermsIterator();
/*     */   }
/*     */ 
/*     */   public static class Builder
/*     */   {
/*  97 */     private RAMFile buffer = new RAMFile();
/*  98 */     private RAMOutputStream output = new RAMOutputStream(this.buffer);
/*  99 */     private Term lastTerm = new Term("");
/* 100 */     private BytesRef lastBytes = new BytesRef();
/* 101 */     private BytesRef scratch = new BytesRef();
/*     */ 
/*     */     public void add(Term term)
/*     */     {
/* 105 */       assert ((this.lastTerm.equals(new Term(""))) || (term.compareTo(this.lastTerm) > 0));
/*     */ 
/* 107 */       this.scratch.copy(term.text);
/*     */       try {
/* 109 */         int prefix = sharedPrefix(this.lastBytes, this.scratch);
/* 110 */         int suffix = this.scratch.length - prefix;
/* 111 */         if (term.field.equals(this.lastTerm.field)) {
/* 112 */           this.output.writeVInt(prefix << 1);
/*     */         } else {
/* 114 */           this.output.writeVInt(prefix << 1 | 0x1);
/* 115 */           this.output.writeString(term.field);
/*     */         }
/* 117 */         this.output.writeVInt(suffix);
/* 118 */         this.output.writeBytes(this.scratch.bytes, this.scratch.offset + prefix, suffix);
/* 119 */         this.lastBytes.copy(this.scratch);
/* 120 */         this.lastTerm.text = term.text;
/* 121 */         this.lastTerm.field = term.field;
/*     */       } catch (IOException e) {
/* 123 */         throw new RuntimeException(e);
/*     */       }
/*     */     }
/*     */ 
/*     */     public PrefixCodedTerms finish()
/*     */     {
/*     */       try {
/* 130 */         this.output.close();
/* 131 */         return new PrefixCodedTerms(this.buffer, null); } catch (IOException e) {
/*     */       }
/* 133 */       throw new RuntimeException(e);
/*     */     }
/*     */ 
/*     */     private int sharedPrefix(BytesRef term1, BytesRef term2)
/*     */     {
/* 138 */       int pos1 = 0;
/* 139 */       int pos1End = pos1 + Math.min(term1.length, term2.length);
/* 140 */       int pos2 = 0;
/* 141 */       while (pos1 < pos1End) {
/* 142 */         if (term1.bytes[(term1.offset + pos1)] != term2.bytes[(term2.offset + pos2)]) {
/* 143 */           return pos1;
/*     */         }
/* 145 */         pos1++;
/* 146 */         pos2++;
/*     */       }
/* 148 */       return pos1;
/*     */     }
/*     */   }
/*     */ 
/*     */   class PrefixCodedTermsIterator
/*     */     implements Iterator<Term>
/*     */   {
/*     */     final IndexInput input;
/*  54 */     String field = "";
/*  55 */     BytesRef bytes = new BytesRef();
/*  56 */     Term term = new Term(this.field, "");
/*     */ 
/*     */     PrefixCodedTermsIterator() {
/*     */       try {
/*  60 */         this.input = new RAMInputStream(PrefixCodedTerms.this.buffer);
/*     */       } catch (IOException e) {
/*  62 */         throw new RuntimeException(e);
/*     */       }
/*     */     }
/*     */ 
/*     */     public boolean hasNext() {
/*  67 */       return this.input.getFilePointer() < this.input.length();
/*     */     }
/*     */ 
/*     */     public Term next() {
/*  71 */       assert (hasNext());
/*     */       try {
/*  73 */         int code = this.input.readVInt();
/*  74 */         if ((code & 0x1) != 0)
/*     */         {
/*  76 */           this.field = StringHelper.intern(this.input.readString());
/*     */         }
/*  78 */         int prefix = code >>> 1;
/*  79 */         int suffix = this.input.readVInt();
/*  80 */         this.bytes.grow(prefix + suffix);
/*  81 */         this.input.readBytes(this.bytes.bytes, prefix, suffix);
/*  82 */         this.bytes.length = (prefix + suffix);
/*  83 */         this.term.set(this.field, this.bytes.utf8ToString());
/*  84 */         return this.term; } catch (IOException e) {
/*     */       }
/*  86 */       throw new RuntimeException(e);
/*     */     }
/*     */ 
/*     */     public void remove()
/*     */     {
/*  91 */       throw new UnsupportedOperationException();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.PrefixCodedTerms
 * JD-Core Version:    0.6.0
 */