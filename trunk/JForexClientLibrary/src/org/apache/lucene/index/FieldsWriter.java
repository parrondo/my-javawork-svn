/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.util.List;
/*     */ import org.apache.lucene.document.Document;
/*     */ import org.apache.lucene.document.Fieldable;
/*     */ import org.apache.lucene.document.NumericField;
/*     */ import org.apache.lucene.store.Directory;
/*     */ import org.apache.lucene.store.IndexInput;
/*     */ import org.apache.lucene.store.IndexOutput;
/*     */ import org.apache.lucene.store.RAMOutputStream;
/*     */ import org.apache.lucene.util.IOUtils;
/*     */ 
/*     */ final class FieldsWriter
/*     */ {
/*     */   static final int FIELD_IS_TOKENIZED = 1;
/*     */   static final int FIELD_IS_BINARY = 2;
/*     */ 
/*     */   @Deprecated
/*     */   static final int FIELD_IS_COMPRESSED = 4;
/*     */   private static final int _NUMERIC_BIT_SHIFT = 3;
/*     */   static final int FIELD_IS_NUMERIC_MASK = 56;
/*     */   static final int FIELD_IS_NUMERIC_INT = 8;
/*     */   static final int FIELD_IS_NUMERIC_LONG = 16;
/*     */   static final int FIELD_IS_NUMERIC_FLOAT = 24;
/*     */   static final int FIELD_IS_NUMERIC_DOUBLE = 32;
/*     */   static final int FORMAT = 0;
/*     */   static final int FORMAT_VERSION_UTF8_LENGTH_IN_BYTES = 1;
/*     */   static final int FORMAT_LUCENE_3_0_NO_COMPRESSED_FIELDS = 2;
/*     */   static final int FORMAT_LUCENE_3_2_NUMERIC_FIELDS = 3;
/*     */   static final int FORMAT_CURRENT = 3;
/*     */   private FieldInfos fieldInfos;
/*     */   private Directory directory;
/*     */   private String segment;
/*     */   private IndexOutput fieldsStream;
/*     */   private IndexOutput indexStream;
/*     */ 
/*     */   FieldsWriter(Directory directory, String segment, FieldInfos fn)
/*     */     throws IOException
/*     */   {
/*  77 */     this.directory = directory;
/*  78 */     this.segment = segment;
/*  79 */     this.fieldInfos = fn;
/*     */ 
/*  81 */     boolean success = false;
/*     */     try {
/*  83 */       this.fieldsStream = directory.createOutput(IndexFileNames.segmentFileName(segment, "fdt"));
/*  84 */       this.indexStream = directory.createOutput(IndexFileNames.segmentFileName(segment, "fdx"));
/*     */ 
/*  86 */       this.fieldsStream.writeInt(3);
/*  87 */       this.indexStream.writeInt(3);
/*     */ 
/*  89 */       success = true;
/*     */     } finally {
/*  91 */       if (!success)
/*  92 */         abort();
/*     */     }
/*     */   }
/*     */ 
/*     */   FieldsWriter(IndexOutput fdx, IndexOutput fdt, FieldInfos fn)
/*     */   {
/*  98 */     this.directory = null;
/*  99 */     this.segment = null;
/* 100 */     this.fieldInfos = fn;
/* 101 */     this.fieldsStream = fdt;
/* 102 */     this.indexStream = fdx;
/*     */   }
/*     */ 
/*     */   void setFieldsStream(IndexOutput stream) {
/* 106 */     this.fieldsStream = stream;
/*     */   }
/*     */ 
/*     */   void flushDocument(int numStoredFields, RAMOutputStream buffer)
/*     */     throws IOException
/*     */   {
/* 114 */     this.indexStream.writeLong(this.fieldsStream.getFilePointer());
/* 115 */     this.fieldsStream.writeVInt(numStoredFields);
/* 116 */     buffer.writeTo(this.fieldsStream);
/*     */   }
/*     */ 
/*     */   void skipDocument() throws IOException {
/* 120 */     this.indexStream.writeLong(this.fieldsStream.getFilePointer());
/* 121 */     this.fieldsStream.writeVInt(0);
/*     */   }
/*     */ 
/*     */   void close() throws IOException {
/* 125 */     if (this.directory != null)
/*     */       try {
/* 127 */         IOUtils.close(new Closeable[] { this.fieldsStream, this.indexStream });
/*     */       } finally {
/* 129 */         this.fieldsStream = (this.indexStream = null);
/*     */       }
/*     */   }
/*     */ 
/*     */   void abort()
/*     */   {
/* 135 */     if (this.directory != null) {
/*     */       try {
/* 137 */         close();
/*     */       } catch (IOException ignored) {
/*     */       }
/*     */       try {
/* 141 */         this.directory.deleteFile(IndexFileNames.segmentFileName(this.segment, "fdt"));
/*     */       } catch (IOException ignored) {
/*     */       }
/*     */       try {
/* 145 */         this.directory.deleteFile(IndexFileNames.segmentFileName(this.segment, "fdx"));
/*     */       } catch (IOException ignored) {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   final void writeField(FieldInfo fi, Fieldable field) throws IOException {
/* 152 */     this.fieldsStream.writeVInt(fi.number);
/* 153 */     int bits = 0;
/* 154 */     if (field.isTokenized())
/* 155 */       bits |= 1;
/* 156 */     if (field.isBinary())
/* 157 */       bits |= 2;
/* 158 */     if ((field instanceof NumericField)) {
/* 159 */       switch (1.$SwitchMap$org$apache$lucene$document$NumericField$DataType[((NumericField)field).getDataType().ordinal()]) {
/*     */       case 1:
/* 161 */         bits |= 8; break;
/*     */       case 2:
/* 163 */         bits |= 16; break;
/*     */       case 3:
/* 165 */         bits |= 24; break;
/*     */       case 4:
/* 167 */         bits |= 32; break;
/*     */       default:
/* 169 */         if ($assertionsDisabled) break; throw new AssertionError("Should never get here");
/*     */       }
/*     */     }
/* 172 */     this.fieldsStream.writeByte((byte)bits);
/*     */ 
/* 174 */     if (field.isBinary())
/*     */     {
/* 178 */       byte[] data = field.getBinaryValue();
/* 179 */       int len = field.getBinaryLength();
/* 180 */       int offset = field.getBinaryOffset();
/*     */ 
/* 182 */       this.fieldsStream.writeVInt(len);
/* 183 */       this.fieldsStream.writeBytes(data, offset, len);
/* 184 */     } else if ((field instanceof NumericField)) {
/* 185 */       NumericField nf = (NumericField)field;
/* 186 */       Number n = nf.getNumericValue();
/* 187 */       switch (1.$SwitchMap$org$apache$lucene$document$NumericField$DataType[nf.getDataType().ordinal()]) {
/*     */       case 1:
/* 189 */         this.fieldsStream.writeInt(n.intValue()); break;
/*     */       case 2:
/* 191 */         this.fieldsStream.writeLong(n.longValue()); break;
/*     */       case 3:
/* 193 */         this.fieldsStream.writeInt(Float.floatToIntBits(n.floatValue())); break;
/*     */       case 4:
/* 195 */         this.fieldsStream.writeLong(Double.doubleToLongBits(n.doubleValue())); break;
/*     */       default:
/* 197 */         if ($assertionsDisabled) break; throw new AssertionError("Should never get here");
/*     */       }
/*     */     } else {
/* 200 */       this.fieldsStream.writeString(field.stringValue());
/*     */     }
/*     */   }
/*     */ 
/*     */   final void addRawDocuments(IndexInput stream, int[] lengths, int numDocs)
/*     */     throws IOException
/*     */   {
/* 210 */     long position = this.fieldsStream.getFilePointer();
/* 211 */     long start = position;
/* 212 */     for (int i = 0; i < numDocs; i++) {
/* 213 */       this.indexStream.writeLong(position);
/* 214 */       position += lengths[i];
/*     */     }
/* 216 */     this.fieldsStream.copyBytes(stream, position - start);
/* 217 */     assert (this.fieldsStream.getFilePointer() == position);
/*     */   }
/*     */ 
/*     */   final void addDocument(Document doc) throws IOException {
/* 221 */     this.indexStream.writeLong(this.fieldsStream.getFilePointer());
/*     */ 
/* 223 */     int storedCount = 0;
/* 224 */     List fields = doc.getFields();
/* 225 */     for (Fieldable field : fields) {
/* 226 */       if (field.isStored())
/* 227 */         storedCount++;
/*     */     }
/* 229 */     this.fieldsStream.writeVInt(storedCount);
/*     */ 
/* 233 */     for (Fieldable field : fields)
/* 234 */       if (field.isStored())
/* 235 */         writeField(this.fieldInfos.fieldInfo(field.name()), field);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.FieldsWriter
 * JD-Core Version:    0.6.0
 */