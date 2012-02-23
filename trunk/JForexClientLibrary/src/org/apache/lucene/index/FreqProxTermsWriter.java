/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import org.apache.lucene.util.BitVector;
/*     */ import org.apache.lucene.util.CollectionUtil;
/*     */ import org.apache.lucene.util.UnicodeUtil.UTF8Result;
/*     */ 
/*     */ final class FreqProxTermsWriter extends TermsHashConsumer
/*     */ {
/*     */   private byte[] payloadBuffer;
/* 323 */   final UnicodeUtil.UTF8Result termsUTF8 = new UnicodeUtil.UTF8Result();
/*     */ 
/*     */   public TermsHashConsumerPerThread addThread(TermsHashPerThread perThread)
/*     */   {
/*  35 */     return new FreqProxTermsWriterPerThread(perThread);
/*     */   }
/*     */ 
/*     */   private static int compareText(char[] text1, int pos1, char[] text2, int pos2) {
/*     */     while (true) {
/*  40 */       char c1 = text1[(pos1++)];
/*  41 */       char c2 = text2[(pos2++)];
/*  42 */       if (c1 != c2) {
/*  43 */         if (65535 == c2)
/*  44 */           return 1;
/*  45 */         if (65535 == c1) {
/*  46 */           return -1;
/*     */         }
/*  48 */         return c1 - c2;
/*  49 */       }if (65535 == c1)
/*  50 */         return 0;
/*     */     }
/*     */   }
/*     */ 
/*     */   void abort()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void flush(Map<TermsHashConsumerPerThread, Collection<TermsHashConsumerPerField>> threadsAndFields, SegmentWriteState state)
/*     */     throws IOException
/*     */   {
/*  67 */     List allFields = new ArrayList();
/*     */ 
/*  69 */     for (Map.Entry entry : threadsAndFields.entrySet())
/*     */     {
/*  71 */       Collection fields = (Collection)entry.getValue();
/*     */ 
/*  73 */       for (TermsHashConsumerPerField i : fields) {
/*  74 */         FreqProxTermsWriterPerField perField = (FreqProxTermsWriterPerField)i;
/*  75 */         if (perField.termsHashPerField.numPostings > 0) {
/*  76 */           allFields.add(perField);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*  81 */     CollectionUtil.quickSort(allFields);
/*  82 */     int numAllFields = allFields.size();
/*     */ 
/*  85 */     FormatPostingsFieldsConsumer consumer = new FormatPostingsFieldsWriter(state, this.fieldInfos);
/*     */     try
/*     */     {
/*  98 */       int start = 0;
/*  99 */       while (start < numAllFields) {
/* 100 */         FieldInfo fieldInfo = ((FreqProxTermsWriterPerField)allFields.get(start)).fieldInfo;
/* 101 */         String fieldName = fieldInfo.name;
/*     */ 
/* 103 */         int end = start + 1;
/* 104 */         while ((end < numAllFields) && (((FreqProxTermsWriterPerField)allFields.get(end)).fieldInfo.name.equals(fieldName))) {
/* 105 */           end++;
/*     */         }
/* 107 */         FreqProxTermsWriterPerField[] fields = new FreqProxTermsWriterPerField[end - start];
/* 108 */         for (int i = start; i < end; i++) {
/* 109 */           fields[(i - start)] = ((FreqProxTermsWriterPerField)allFields.get(i));
/*     */ 
/* 113 */           if (fieldInfo.indexOptions == FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) {
/* 114 */             fieldInfo.storePayloads |= fields[(i - start)].hasPayloads;
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 120 */         appendPostings(fieldName, state, fields, consumer);
/*     */ 
/* 122 */         for (int i = 0; i < fields.length; i++) {
/* 123 */           TermsHashPerField perField = fields[i].termsHashPerField;
/* 124 */           int numPostings = perField.numPostings;
/* 125 */           perField.reset();
/* 126 */           perField.shrinkHash(numPostings);
/* 127 */           fields[i].reset();
/*     */         }
/*     */ 
/* 130 */         start = end;
/*     */       }
/*     */ 
/* 133 */       for (Map.Entry entry : threadsAndFields.entrySet()) {
/* 134 */         FreqProxTermsWriterPerThread perThread = (FreqProxTermsWriterPerThread)entry.getKey();
/* 135 */         perThread.termsHashPerThread.reset(true);
/*     */       }
/*     */     } finally {
/* 138 */       consumer.finish();
/*     */     }
/*     */   }
/*     */ 
/*     */   void appendPostings(String fieldName, SegmentWriteState state, FreqProxTermsWriterPerField[] fields, FormatPostingsFieldsConsumer consumer)
/*     */     throws CorruptIndexException, IOException
/*     */   {
/* 152 */     int numFields = fields.length;
/*     */ 
/* 154 */     FreqProxFieldMergeState[] mergeStates = new FreqProxFieldMergeState[numFields];
/*     */ 
/* 156 */     for (int i = 0; i < numFields; i++) {
/* 157 */       FreqProxFieldMergeState fms = mergeStates[i] =  = new FreqProxFieldMergeState(fields[i]);
/*     */ 
/* 159 */       assert (fms.field.fieldInfo == fields[0].fieldInfo);
/*     */ 
/* 162 */       boolean result = fms.nextTerm();
/* 163 */       assert (result);
/*     */     }
/*     */ 
/* 166 */     FormatPostingsTermsConsumer termsConsumer = consumer.addField(fields[0].fieldInfo);
/* 167 */     Term protoTerm = new Term(fieldName);
/*     */ 
/* 169 */     FreqProxFieldMergeState[] termStates = new FreqProxFieldMergeState[numFields];
/*     */ 
/* 171 */     FieldInfo.IndexOptions currentFieldIndexOptions = fields[0].fieldInfo.indexOptions;
/*     */     Map segDeletes;
/*     */     Map segDeletes;
/* 174 */     if ((state.segDeletes != null) && (state.segDeletes.terms.size() > 0))
/* 175 */       segDeletes = state.segDeletes.terms;
/*     */     else {
/* 177 */       segDeletes = null;
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 186 */       while (numFields > 0)
/*     */       {
/* 189 */         termStates[0] = mergeStates[0];
/* 190 */         int numToMerge = 1;
/*     */ 
/* 193 */         for (int i = 1; i < numFields; i++) {
/* 194 */           char[] text = mergeStates[i].text;
/* 195 */           int textOffset = mergeStates[i].textOffset;
/* 196 */           int cmp = compareText(text, textOffset, termStates[0].text, termStates[0].textOffset);
/*     */ 
/* 198 */           if (cmp < 0) {
/* 199 */             termStates[0] = mergeStates[i];
/* 200 */             numToMerge = 1;
/* 201 */           } else if (cmp == 0) {
/* 202 */             termStates[(numToMerge++)] = mergeStates[i];
/*     */           }
/*     */         }
/* 205 */         FormatPostingsDocsConsumer docConsumer = termsConsumer.addTerm(termStates[0].text, termStates[0].textOffset);
/*     */         int delDocLimit;
/*     */         int delDocLimit;
/* 208 */         if (segDeletes != null) {
/* 209 */           Integer docIDUpto = (Integer)segDeletes.get(protoTerm.createTerm(termStates[0].termText()));
/*     */           int delDocLimit;
/* 210 */           if (docIDUpto != null)
/* 211 */             delDocLimit = docIDUpto.intValue();
/*     */           else
/* 213 */             delDocLimit = 0;
/*     */         }
/*     */         else {
/* 216 */           delDocLimit = 0;
/*     */         }
/*     */ 
/*     */         try
/*     */         {
/* 223 */           while (numToMerge > 0)
/*     */           {
/* 225 */             FreqProxFieldMergeState minState = termStates[0];
/* 226 */             for (int i = 1; i < numToMerge; i++) {
/* 227 */               if (termStates[i].docID < minState.docID)
/* 228 */                 minState = termStates[i];
/*     */             }
/* 230 */             int termDocFreq = minState.termFreq;
/*     */ 
/* 232 */             FormatPostingsPositionsConsumer posConsumer = docConsumer.addDoc(minState.docID, termDocFreq);
/*     */ 
/* 246 */             if (minState.docID < delDocLimit)
/*     */             {
/* 250 */               if (state.deletedDocs == null) {
/* 251 */                 state.deletedDocs = new BitVector(state.numDocs);
/*     */               }
/* 253 */               state.deletedDocs.set(minState.docID);
/*     */             }
/*     */ 
/* 256 */             ByteSliceReader prox = minState.prox;
/*     */ 
/* 261 */             if (currentFieldIndexOptions == FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS)
/*     */             {
/*     */               try
/*     */               {
/* 265 */                 int position = 0;
/* 266 */                 for (int j = 0; j < termDocFreq; j++) {
/* 267 */                   int code = prox.readVInt();
/* 268 */                   position += (code >> 1);
/*     */                   int payloadLength;
/* 271 */                   if ((code & 0x1) != 0)
/*     */                   {
/* 273 */                     int payloadLength = prox.readVInt();
/*     */ 
/* 275 */                     if ((this.payloadBuffer == null) || (this.payloadBuffer.length < payloadLength)) {
/* 276 */                       this.payloadBuffer = new byte[payloadLength];
/*     */                     }
/* 278 */                     prox.readBytes(this.payloadBuffer, 0, payloadLength);
/*     */                   }
/*     */                   else {
/* 281 */                     payloadLength = 0;
/*     */                   }
/* 283 */                   posConsumer.addPosition(position, this.payloadBuffer, 0, payloadLength);
/*     */                 }
/*     */               } finally {
/* 286 */                 posConsumer.finish();
/*     */               }
/*     */             }
/*     */ 
/* 290 */             if (!minState.nextDoc())
/*     */             {
/* 293 */               int upto = 0;
/* 294 */               for (int i = 0; i < numToMerge; i++)
/* 295 */                 if (termStates[i] != minState)
/* 296 */                   termStates[(upto++)] = termStates[i];
/* 297 */               numToMerge--;
/* 298 */               assert (upto == numToMerge);
/*     */ 
/* 302 */               if (!minState.nextTerm())
/*     */               {
/* 305 */                 upto = 0;
/* 306 */                 for (int i = 0; i < numFields; i++)
/* 307 */                   if (mergeStates[i] != minState)
/* 308 */                     mergeStates[(upto++)] = mergeStates[i];
/* 309 */                 numFields--;
/* 310 */                 assert (upto == numFields);
/*     */               }
/*     */             }
/*     */           }
/*     */         } finally {
/* 315 */           docConsumer.finish();
/*     */         }
/*     */       }
/*     */     } finally {
/* 319 */       termsConsumer.finish();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.FreqProxTermsWriter
 * JD-Core Version:    0.6.0
 */