/*    */ package org.apache.lucene.index;
/*    */ 
/*    */ import java.io.Closeable;
/*    */ import java.io.IOException;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ import java.util.HashMap;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import java.util.Map.Entry;
/*    */ import org.apache.lucene.search.Similarity;
/*    */ import org.apache.lucene.store.Directory;
/*    */ import org.apache.lucene.store.IndexOutput;
/*    */ import org.apache.lucene.util.IOUtils;
/*    */ 
/*    */ final class NormsWriter extends InvertedDocEndConsumer
/*    */ {
/* 41 */   private final byte defaultNorm = Similarity.getDefault().encodeNormValue(1.0F);
/*    */   private FieldInfos fieldInfos;
/*    */ 
/*    */   public InvertedDocEndConsumerPerThread addThread(DocInverterPerThread docInverterPerThread)
/*    */   {
/* 45 */     return new NormsWriterPerThread(docInverterPerThread, this);
/*    */   }
/*    */ 
/*    */   public void abort()
/*    */   {
/*    */   }
/*    */ 
/*    */   void files(Collection<String> files) {
/*    */   }
/*    */ 
/*    */   void setFieldInfos(FieldInfos fieldInfos) {
/* 56 */     this.fieldInfos = fieldInfos;
/*    */   }
/*    */ 
/*    */   public void flush(Map<InvertedDocEndConsumerPerThread, Collection<InvertedDocEndConsumerPerField>> threadsAndFields, SegmentWriteState state)
/*    */     throws IOException
/*    */   {
/* 64 */     Map byField = new HashMap();
/*    */ 
/* 70 */     for (Map.Entry entry : threadsAndFields.entrySet()) {
/* 71 */       Collection fields = (Collection)entry.getValue();
/* 72 */       Iterator fieldsIt = fields.iterator();
/*    */ 
/* 74 */       while (fieldsIt.hasNext()) {
/* 75 */         NormsWriterPerField perField = (NormsWriterPerField)fieldsIt.next();
/*    */ 
/* 77 */         if (perField.upto > 0)
/*    */         {
/* 79 */           List l = (List)byField.get(perField.fieldInfo);
/* 80 */           if (l == null) {
/* 81 */             l = new ArrayList();
/* 82 */             byField.put(perField.fieldInfo, l);
/*    */           }
/* 84 */           l.add(perField);
/*    */         }
/*    */         else
/*    */         {
/* 88 */           fieldsIt.remove();
/*    */         }
/*    */       }
/*    */     }
/* 92 */     String normsFileName = IndexFileNames.segmentFileName(state.segmentName, "nrm");
/* 93 */     IndexOutput normsOut = state.directory.createOutput(normsFileName);
/* 94 */     boolean success = false;
/*    */     try {
/* 96 */       normsOut.writeBytes(SegmentNorms.NORMS_HEADER, 0, SegmentNorms.NORMS_HEADER.length);
/*    */ 
/* 98 */       int numField = this.fieldInfos.size();
/*    */ 
/* 100 */       int normCount = 0;
/*    */ 
/* 102 */       for (int fieldNumber = 0; fieldNumber < numField; fieldNumber++)
/*    */       {
/* 104 */         FieldInfo fieldInfo = this.fieldInfos.fieldInfo(fieldNumber);
/*    */ 
/* 106 */         List toMerge = (List)byField.get(fieldInfo);
/* 107 */         int upto = 0;
/* 108 */         if (toMerge != null)
/*    */         {
/* 110 */           int numFields = toMerge.size();
/*    */ 
/* 112 */           normCount++;
/*    */ 
/* 114 */           NormsWriterPerField[] fields = new NormsWriterPerField[numFields];
/* 115 */           int[] uptos = new int[numFields];
/*    */ 
/* 117 */           for (int j = 0; j < numFields; j++) {
/* 118 */             fields[j] = ((NormsWriterPerField)toMerge.get(j));
/*    */           }
/* 120 */           int numLeft = numFields;
/*    */ 
/* 122 */           while (numLeft > 0)
/*    */           {
/* 124 */             assert (uptos[0] < fields[0].docIDs.length) : (" uptos[0]=" + uptos[0] + " len=" + fields[0].docIDs.length);
/*    */ 
/* 126 */             int minLoc = 0;
/* 127 */             int minDocID = fields[0].docIDs[uptos[0]];
/*    */ 
/* 129 */             for (int j = 1; j < numLeft; j++) {
/* 130 */               int docID = fields[j].docIDs[uptos[j]];
/* 131 */               if (docID < minDocID) {
/* 132 */                 minDocID = docID;
/* 133 */                 minLoc = j;
/*    */               }
/*    */             }
/*    */ 
/* 137 */             assert (minDocID < state.numDocs);
/*    */ 
/* 140 */             for (; upto < minDocID; upto++) {
/* 141 */               normsOut.writeByte(this.defaultNorm);
/*    */             }
/* 143 */             normsOut.writeByte(fields[minLoc].norms[uptos[minLoc]]);
/* 144 */             uptos[minLoc] += 1;
/* 145 */             upto++;
/*    */ 
/* 147 */             if (uptos[minLoc] == fields[minLoc].upto) {
/* 148 */               fields[minLoc].reset();
/* 149 */               if (minLoc != numLeft - 1) {
/* 150 */                 fields[minLoc] = fields[(numLeft - 1)];
/* 151 */                 uptos[minLoc] = uptos[(numLeft - 1)];
/*    */               }
/* 153 */               numLeft--;
/*    */             }
/*    */ 
/*    */           }
/*    */ 
/* 158 */           for (; upto < state.numDocs; upto++)
/* 159 */             normsOut.writeByte(this.defaultNorm);
/* 160 */         } else if ((fieldInfo.isIndexed) && (!fieldInfo.omitNorms)) {
/* 161 */           normCount++;
/*    */ 
/* 163 */           for (; upto < state.numDocs; upto++) {
/* 164 */             normsOut.writeByte(this.defaultNorm);
/*    */           }
/*    */         }
/* 167 */         assert (4 + normCount * state.numDocs == normsOut.getFilePointer()) : (".nrm file size mismatch: expected=" + (4 + normCount * state.numDocs) + " actual=" + normsOut.getFilePointer());
/*    */       }
/* 169 */       success = true;
/*    */     } finally {
/* 171 */       if (success)
/* 172 */         IOUtils.close(new Closeable[] { normsOut });
/*    */       else
/* 174 */         IOUtils.closeWhileHandlingException(new Closeable[] { normsOut });
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.NormsWriter
 * JD-Core Version:    0.6.0
 */