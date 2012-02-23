/*     */ package org.apache.lucene.analysis;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
/*     */ import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
/*     */ import org.apache.lucene.util.AttributeSource;
/*     */ import org.apache.lucene.util.AttributeSource.AttributeFactory;
/*     */ import org.apache.lucene.util.CharacterUtils;
/*     */ import org.apache.lucene.util.CharacterUtils.CharacterBuffer;
/*     */ import org.apache.lucene.util.Version;
/*     */ import org.apache.lucene.util.VirtualMethod;
/*     */ 
/*     */ public abstract class CharTokenizer extends Tokenizer
/*     */ {
/* 154 */   private int offset = 0; private int bufferIndex = 0; private int dataLen = 0; private int finalOffset = 0;
/*     */   private static final int MAX_WORD_LEN = 255;
/*     */   private static final int IO_BUFFER_SIZE = 4096;
/* 158 */   private final CharTermAttribute termAtt = (CharTermAttribute)addAttribute(CharTermAttribute.class);
/* 159 */   private final OffsetAttribute offsetAtt = (OffsetAttribute)addAttribute(OffsetAttribute.class);
/*     */   private final CharacterUtils charUtils;
/* 162 */   private final CharacterUtils.CharacterBuffer ioBuffer = CharacterUtils.newCharacterBuffer(4096);
/*     */ 
/*     */   @Deprecated
/*     */   private final boolean useOldAPI;
/*     */ 
/*     */   @Deprecated
/*     */   private static final VirtualMethod<CharTokenizer> isTokenCharMethod;
/*     */ 
/*     */   @Deprecated
/*     */   private static final VirtualMethod<CharTokenizer> normalizeMethod;
/*     */ 
/*     */   public CharTokenizer(Version matchVersion, Reader input)
/*     */   {
/*  79 */     super(input);
/*  80 */     this.charUtils = CharacterUtils.getInstance(matchVersion);
/*  81 */     this.useOldAPI = useOldAPI(matchVersion);
/*     */   }
/*     */ 
/*     */   public CharTokenizer(Version matchVersion, AttributeSource source, Reader input)
/*     */   {
/*  97 */     super(source, input);
/*  98 */     this.charUtils = CharacterUtils.getInstance(matchVersion);
/*  99 */     this.useOldAPI = useOldAPI(matchVersion);
/*     */   }
/*     */ 
/*     */   public CharTokenizer(Version matchVersion, AttributeSource.AttributeFactory factory, Reader input)
/*     */   {
/* 114 */     super(factory, input);
/* 115 */     this.charUtils = CharacterUtils.getInstance(matchVersion);
/* 116 */     this.useOldAPI = useOldAPI(matchVersion);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public CharTokenizer(Reader input)
/*     */   {
/* 127 */     this(Version.LUCENE_30, input);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public CharTokenizer(AttributeSource source, Reader input)
/*     */   {
/* 139 */     this(Version.LUCENE_30, source, input);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public CharTokenizer(AttributeSource.AttributeFactory factory, Reader input)
/*     */   {
/* 151 */     this(Version.LUCENE_30, factory, input);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   protected boolean isTokenChar(char c)
/*     */   {
/* 201 */     return isTokenChar(c);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   protected char normalize(char c)
/*     */   {
/* 220 */     return (char)normalize(c);
/*     */   }
/*     */ 
/*     */   protected boolean isTokenChar(int c)
/*     */   {
/* 240 */     throw new UnsupportedOperationException("since LUCENE_31 subclasses of CharTokenizer must implement isTokenChar(int)");
/*     */   }
/*     */ 
/*     */   protected int normalize(int c)
/*     */   {
/* 259 */     return c;
/*     */   }
/*     */ 
/*     */   public final boolean incrementToken() throws IOException
/*     */   {
/* 264 */     clearAttributes();
/* 265 */     if (this.useOldAPI)
/* 266 */       return incrementTokenOld();
/* 267 */     int length = 0;
/* 268 */     int start = -1;
/* 269 */     char[] buffer = this.termAtt.buffer();
/*     */     while (true) {
/* 271 */       if (this.bufferIndex >= this.dataLen) {
/* 272 */         this.offset += this.dataLen;
/* 273 */         if (!this.charUtils.fill(this.ioBuffer, this.input)) {
/* 274 */           this.dataLen = 0;
/* 275 */           if (length > 0) {
/*     */             break;
/*     */           }
/* 278 */           this.finalOffset = correctOffset(this.offset);
/* 279 */           return false;
/*     */         }
/*     */ 
/* 282 */         this.dataLen = this.ioBuffer.getLength();
/* 283 */         this.bufferIndex = 0;
/*     */       }
/*     */ 
/* 286 */       int c = this.charUtils.codePointAt(this.ioBuffer.getBuffer(), this.bufferIndex);
/* 287 */       this.bufferIndex += Character.charCount(c);
/*     */ 
/* 289 */       if (isTokenChar(c)) {
/* 290 */         if (length == 0) {
/* 291 */           assert (start == -1);
/* 292 */           start = this.offset + this.bufferIndex - 1;
/* 293 */         } else if (length >= buffer.length - 1) {
/* 294 */           buffer = this.termAtt.resizeBuffer(2 + length);
/*     */         }
/* 296 */         length += Character.toChars(normalize(c), buffer, length);
/* 297 */         if (length >= 255)
/* 298 */           break; 
/*     */       } else {
/* 299 */         if (length > 0)
/*     */           break;
/*     */       }
/*     */     }
/* 303 */     this.termAtt.setLength(length);
/* 304 */     assert (start != -1);
/* 305 */     this.offsetAtt.setOffset(correctOffset(start), this.finalOffset = correctOffset(start + length));
/* 306 */     return true;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   private boolean incrementTokenOld()
/*     */     throws IOException
/*     */   {
/* 317 */     int length = 0;
/* 318 */     int start = -1;
/* 319 */     char[] buffer = this.termAtt.buffer();
/* 320 */     char[] oldIoBuffer = this.ioBuffer.getBuffer();
/*     */     while (true)
/*     */     {
/* 323 */       if (this.bufferIndex >= this.dataLen) {
/* 324 */         this.offset += this.dataLen;
/* 325 */         this.dataLen = this.input.read(oldIoBuffer);
/* 326 */         if (this.dataLen == -1) {
/* 327 */           this.dataLen = 0;
/* 328 */           if (length > 0) {
/*     */             break;
/*     */           }
/* 331 */           this.finalOffset = correctOffset(this.offset);
/* 332 */           return false;
/*     */         }
/*     */ 
/* 335 */         this.bufferIndex = 0;
/*     */       }
/*     */ 
/* 338 */       char c = oldIoBuffer[(this.bufferIndex++)];
/*     */ 
/* 340 */       if (isTokenChar(c))
/*     */       {
/* 342 */         if (length == 0) {
/* 343 */           assert (start == -1);
/* 344 */           start = this.offset + this.bufferIndex - 1;
/* 345 */         } else if (length == buffer.length) {
/* 346 */           buffer = this.termAtt.resizeBuffer(1 + length);
/*     */         }
/*     */ 
/* 349 */         buffer[(length++)] = normalize(c);
/*     */ 
/* 351 */         if (length == 255)
/* 352 */           break;
/*     */       } else {
/* 354 */         if (length > 0)
/*     */           break;
/*     */       }
/*     */     }
/* 358 */     this.termAtt.setLength(length);
/* 359 */     assert (start != -1);
/* 360 */     this.offsetAtt.setOffset(correctOffset(start), correctOffset(start + length));
/* 361 */     return true;
/*     */   }
/*     */ 
/*     */   public final void end()
/*     */   {
/* 369 */     this.offsetAtt.setOffset(this.finalOffset, this.finalOffset);
/*     */   }
/*     */ 
/*     */   public void reset(Reader input) throws IOException
/*     */   {
/* 374 */     super.reset(input);
/* 375 */     this.bufferIndex = 0;
/* 376 */     this.offset = 0;
/* 377 */     this.dataLen = 0;
/* 378 */     this.finalOffset = 0;
/* 379 */     this.ioBuffer.reset();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   private boolean useOldAPI(Version matchVersion)
/*     */   {
/* 387 */     Class clazz = getClass();
/* 388 */     if ((matchVersion.onOrAfter(Version.LUCENE_31)) && ((isTokenCharMethod.isOverriddenAsOf(clazz)) || (normalizeMethod.isOverriddenAsOf(clazz))))
/*     */     {
/* 390 */       throw new IllegalArgumentException("For matchVersion >= LUCENE_31, CharTokenizer subclasses must not override isTokenChar(char) or normalize(char).");
/*     */     }
/* 392 */     return !matchVersion.onOrAfter(Version.LUCENE_31);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 174 */     isTokenCharMethod = new VirtualMethod(CharTokenizer.class, "isTokenChar", new Class[] { Character.TYPE });
/*     */ 
/* 181 */     normalizeMethod = new VirtualMethod(CharTokenizer.class, "normalize", new Class[] { Character.TYPE });
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.CharTokenizer
 * JD-Core Version:    0.6.0
 */