/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.io.Reader;
/*     */ import org.apache.lucene.analysis.Analyzer;
/*     */ import org.apache.lucene.analysis.TokenStream;
/*     */ import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
/*     */ import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
/*     */ import org.apache.lucene.document.Document;
/*     */ import org.apache.lucene.document.Fieldable;
/*     */ import org.apache.lucene.util.AttributeSource;
/*     */ 
/*     */ final class DocInverterPerField extends DocFieldConsumerPerField
/*     */ {
/*     */   private final DocInverterPerThread perThread;
/*     */   private final FieldInfo fieldInfo;
/*     */   final InvertedDocConsumerPerField consumer;
/*     */   final InvertedDocEndConsumerPerField endConsumer;
/*     */   final DocumentsWriter.DocState docState;
/*     */   final FieldInvertState fieldState;
/*     */ 
/*     */   public DocInverterPerField(DocInverterPerThread perThread, FieldInfo fieldInfo)
/*     */   {
/*  46 */     this.perThread = perThread;
/*  47 */     this.fieldInfo = fieldInfo;
/*  48 */     this.docState = perThread.docState;
/*  49 */     this.fieldState = perThread.fieldState;
/*  50 */     this.consumer = perThread.consumer.addField(this, fieldInfo);
/*  51 */     this.endConsumer = perThread.endConsumer.addField(this, fieldInfo);
/*     */   }
/*     */ 
/*     */   void abort()
/*     */   {
/*     */     try {
/*  57 */       this.consumer.abort();
/*     */     } finally {
/*  59 */       this.endConsumer.abort();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void processFields(Fieldable[] fields, int count)
/*     */     throws IOException
/*     */   {
/*  67 */     this.fieldState.reset(this.docState.doc.getBoost());
/*     */ 
/*  69 */     int maxFieldLength = this.docState.maxFieldLength;
/*     */ 
/*  71 */     boolean doInvert = this.consumer.start(fields, count);
/*     */ 
/*  73 */     for (int i = 0; i < count; i++)
/*     */     {
/*  75 */       Fieldable field = fields[i];
/*     */ 
/*  80 */       if ((field.isIndexed()) && (doInvert))
/*     */       {
/*  82 */         if (i > 0) {
/*  83 */           this.fieldState.position += (this.docState.analyzer == null ? 0 : this.docState.analyzer.getPositionIncrementGap(this.fieldInfo.name));
/*     */         }
/*  85 */         if (!field.isTokenized()) {
/*  86 */           String stringValue = field.stringValue();
/*  87 */           int valueLength = stringValue.length();
/*  88 */           this.perThread.singleToken.reinit(stringValue, 0, valueLength);
/*  89 */           this.fieldState.attributeSource = this.perThread.singleToken;
/*  90 */           this.consumer.start(field);
/*     */ 
/*  92 */           boolean success = false;
/*     */           try {
/*  94 */             this.consumer.add();
/*  95 */             success = true;
/*     */           } finally {
/*  97 */             if (!success)
/*  98 */               this.docState.docWriter.setAborting();
/*     */           }
/* 100 */           this.fieldState.offset += valueLength;
/* 101 */           this.fieldState.length += 1;
/* 102 */           this.fieldState.position += 1;
/*     */         }
/*     */         else {
/* 105 */           TokenStream streamValue = field.tokenStreamValue();
/*     */           TokenStream stream;
/*     */           TokenStream stream;
/* 107 */           if (streamValue != null) {
/* 108 */             stream = streamValue;
/*     */           }
/*     */           else
/*     */           {
/* 113 */             Reader readerValue = field.readerValue();
/*     */             Reader reader;
/*     */             Reader reader;
/* 115 */             if (readerValue != null) {
/* 116 */               reader = readerValue;
/*     */             } else {
/* 118 */               String stringValue = field.stringValue();
/* 119 */               if (stringValue == null)
/* 120 */                 throw new IllegalArgumentException("field must have either TokenStream, String or Reader value");
/* 121 */               this.perThread.stringReader.init(stringValue);
/* 122 */               reader = this.perThread.stringReader;
/*     */             }
/*     */ 
/* 126 */             stream = this.docState.analyzer.reusableTokenStream(this.fieldInfo.name, reader);
/*     */           }
/*     */ 
/* 130 */           stream.reset();
/*     */ 
/* 132 */           int startLength = this.fieldState.length;
/*     */           try
/*     */           {
/* 135 */             boolean hasMoreTokens = stream.incrementToken();
/*     */ 
/* 137 */             this.fieldState.attributeSource = stream;
/*     */ 
/* 139 */             OffsetAttribute offsetAttribute = (OffsetAttribute)this.fieldState.attributeSource.addAttribute(OffsetAttribute.class);
/* 140 */             PositionIncrementAttribute posIncrAttribute = (PositionIncrementAttribute)this.fieldState.attributeSource.addAttribute(PositionIncrementAttribute.class);
/*     */ 
/* 142 */             this.consumer.start(field);
/*     */ 
/* 153 */             while (hasMoreTokens)
/*     */             {
/* 155 */               int posIncr = posIncrAttribute.getPositionIncrement();
/* 156 */               this.fieldState.position += posIncr;
/* 157 */               if (this.fieldState.position > 0) {
/* 158 */                 this.fieldState.position -= 1;
/*     */               }
/*     */ 
/* 161 */               if (posIncr == 0) {
/* 162 */                 this.fieldState.numOverlap += 1;
/*     */               }
/* 164 */               boolean success = false;
/*     */               try
/*     */               {
/* 172 */                 this.consumer.add();
/* 173 */                 success = true;
/*     */               } finally {
/* 175 */                 if (!success)
/* 176 */                   this.docState.docWriter.setAborting();
/*     */               }
/* 178 */               this.fieldState.position += 1;
/* 179 */               if (++this.fieldState.length >= maxFieldLength) {
/* 180 */                 if (this.docState.infoStream == null) break;
/* 181 */                 this.docState.infoStream.println("maxFieldLength " + maxFieldLength + " reached for field " + this.fieldInfo.name + ", ignoring following tokens"); break;
/*     */               }
/*     */ 
/* 185 */               hasMoreTokens = stream.incrementToken();
/*     */             }
/*     */ 
/* 188 */             stream.end();
/*     */ 
/* 190 */             this.fieldState.offset += offsetAttribute.endOffset();
/*     */           } finally {
/* 192 */             stream.close();
/*     */           }
/*     */         }
/*     */ 
/* 196 */         this.fieldState.offset += (this.docState.analyzer == null ? 0 : this.docState.analyzer.getOffsetGap(field));
/* 197 */         this.fieldState.boost *= field.getBoost();
/*     */       }
/*     */ 
/* 202 */       fields[i] = null;
/*     */     }
/*     */ 
/* 205 */     this.consumer.finish();
/* 206 */     this.endConsumer.finish();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.DocInverterPerField
 * JD-Core Version:    0.6.0
 */