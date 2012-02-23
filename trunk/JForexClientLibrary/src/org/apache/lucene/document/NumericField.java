/*     */ package org.apache.lucene.document;
/*     */ 
/*     */ import java.io.Reader;
/*     */ import org.apache.lucene.analysis.NumericTokenStream;
/*     */ import org.apache.lucene.analysis.TokenStream;
/*     */ import org.apache.lucene.index.FieldInfo.IndexOptions;
/*     */ 
/*     */ public final class NumericField extends AbstractField
/*     */ {
/*     */   private transient NumericTokenStream numericTS;
/*     */   private DataType type;
/*     */   private final int precisionStep;
/*     */ 
/*     */   public NumericField(String name)
/*     */   {
/* 153 */     this(name, 4, Field.Store.NO, true);
/*     */   }
/*     */ 
/*     */   public NumericField(String name, Field.Store store, boolean index)
/*     */   {
/* 167 */     this(name, 4, store, index);
/*     */   }
/*     */ 
/*     */   public NumericField(String name, int precisionStep)
/*     */   {
/* 180 */     this(name, precisionStep, Field.Store.NO, true);
/*     */   }
/*     */ 
/*     */   public NumericField(String name, int precisionStep, Field.Store store, boolean index)
/*     */   {
/* 195 */     super(name, store, index ? Field.Index.ANALYZED_NO_NORMS : Field.Index.NO, Field.TermVector.NO);
/* 196 */     this.precisionStep = precisionStep;
/* 197 */     setIndexOptions(FieldInfo.IndexOptions.DOCS_ONLY);
/*     */   }
/*     */ 
/*     */   public TokenStream tokenStreamValue()
/*     */   {
/* 202 */     if (!isIndexed())
/* 203 */       return null;
/* 204 */     if (this.numericTS == null)
/*     */     {
/* 207 */       this.numericTS = new NumericTokenStream(this.precisionStep);
/*     */ 
/* 209 */       if (this.fieldsData != null) {
/* 210 */         assert (this.type != null);
/* 211 */         Number val = (Number)this.fieldsData;
/* 212 */         switch (1.$SwitchMap$org$apache$lucene$document$NumericField$DataType[this.type.ordinal()]) {
/*     */         case 1:
/* 214 */           this.numericTS.setIntValue(val.intValue()); break;
/*     */         case 2:
/* 216 */           this.numericTS.setLongValue(val.longValue()); break;
/*     */         case 3:
/* 218 */           this.numericTS.setFloatValue(val.floatValue()); break;
/*     */         case 4:
/* 220 */           this.numericTS.setDoubleValue(val.doubleValue()); break;
/*     */         default:
/* 222 */           if ($assertionsDisabled) break; throw new AssertionError("Should never get here");
/*     */         }
/*     */       }
/*     */     }
/* 226 */     return this.numericTS;
/*     */   }
/*     */ 
/*     */   public byte[] getBinaryValue(byte[] result)
/*     */   {
/* 232 */     return null;
/*     */   }
/*     */ 
/*     */   public Reader readerValue()
/*     */   {
/* 237 */     return null;
/*     */   }
/*     */ 
/*     */   public String stringValue()
/*     */   {
/* 245 */     return this.fieldsData == null ? null : this.fieldsData.toString();
/*     */   }
/*     */ 
/*     */   public Number getNumericValue()
/*     */   {
/* 250 */     return (Number)this.fieldsData;
/*     */   }
/*     */ 
/*     */   public int getPrecisionStep()
/*     */   {
/* 255 */     return this.precisionStep;
/*     */   }
/*     */ 
/*     */   public DataType getDataType()
/*     */   {
/* 262 */     return this.type;
/*     */   }
/*     */ 
/*     */   public NumericField setLongValue(long value)
/*     */   {
/* 272 */     if (this.numericTS != null) this.numericTS.setLongValue(value);
/* 273 */     this.fieldsData = Long.valueOf(value);
/* 274 */     this.type = DataType.LONG;
/* 275 */     return this;
/*     */   }
/*     */ 
/*     */   public NumericField setIntValue(int value)
/*     */   {
/* 285 */     if (this.numericTS != null) this.numericTS.setIntValue(value);
/* 286 */     this.fieldsData = Integer.valueOf(value);
/* 287 */     this.type = DataType.INT;
/* 288 */     return this;
/*     */   }
/*     */ 
/*     */   public NumericField setDoubleValue(double value)
/*     */   {
/* 298 */     if (this.numericTS != null) this.numericTS.setDoubleValue(value);
/* 299 */     this.fieldsData = Double.valueOf(value);
/* 300 */     this.type = DataType.DOUBLE;
/* 301 */     return this;
/*     */   }
/*     */ 
/*     */   public NumericField setFloatValue(float value)
/*     */   {
/* 311 */     if (this.numericTS != null) this.numericTS.setFloatValue(value);
/* 312 */     this.fieldsData = Float.valueOf(value);
/* 313 */     this.type = DataType.FLOAT;
/* 314 */     return this;
/*     */   }
/*     */ 
/*     */   public static enum DataType
/*     */   {
/* 138 */     INT, LONG, FLOAT, DOUBLE;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.document.NumericField
 * JD-Core Version:    0.6.0
 */