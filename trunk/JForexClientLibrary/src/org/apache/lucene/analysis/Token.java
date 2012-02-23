/*     */ package org.apache.lucene.analysis;
/*     */ 
/*     */ import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
/*     */ import org.apache.lucene.analysis.tokenattributes.FlagsAttribute;
/*     */ import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
/*     */ import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
/*     */ import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
/*     */ import org.apache.lucene.analysis.tokenattributes.TermAttributeImpl;
/*     */ import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
/*     */ import org.apache.lucene.index.Payload;
/*     */ import org.apache.lucene.util.Attribute;
/*     */ import org.apache.lucene.util.AttributeImpl;
/*     */ import org.apache.lucene.util.AttributeReflector;
/*     */ import org.apache.lucene.util.AttributeSource.AttributeFactory;
/*     */ 
/*     */ public class Token extends TermAttributeImpl
/*     */   implements TypeAttribute, PositionIncrementAttribute, FlagsAttribute, OffsetAttribute, PayloadAttribute
/*     */ {
/*     */   private int startOffset;
/*     */   private int endOffset;
/* 128 */   private String type = "word";
/*     */   private int flags;
/*     */   private Payload payload;
/* 131 */   private int positionIncrement = 1;
/*     */ 
/* 609 */   public static final AttributeSource.AttributeFactory TOKEN_ATTRIBUTE_FACTORY = new TokenAttributeFactory(AttributeSource.AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY);
/*     */ 
/*     */   public Token()
/*     */   {
/*     */   }
/*     */ 
/*     */   public Token(int start, int end)
/*     */   {
/* 142 */     this.startOffset = start;
/* 143 */     this.endOffset = end;
/*     */   }
/*     */ 
/*     */   public Token(int start, int end, String typ)
/*     */   {
/* 152 */     this.startOffset = start;
/* 153 */     this.endOffset = end;
/* 154 */     this.type = typ;
/*     */   }
/*     */ 
/*     */   public Token(int start, int end, int flags)
/*     */   {
/* 165 */     this.startOffset = start;
/* 166 */     this.endOffset = end;
/* 167 */     this.flags = flags;
/*     */   }
/*     */ 
/*     */   public Token(String text, int start, int end)
/*     */   {
/* 180 */     append(text);
/* 181 */     this.startOffset = start;
/* 182 */     this.endOffset = end;
/*     */   }
/*     */ 
/*     */   public Token(String text, int start, int end, String typ)
/*     */   {
/* 195 */     append(text);
/* 196 */     this.startOffset = start;
/* 197 */     this.endOffset = end;
/* 198 */     this.type = typ;
/*     */   }
/*     */ 
/*     */   public Token(String text, int start, int end, int flags)
/*     */   {
/* 212 */     append(text);
/* 213 */     this.startOffset = start;
/* 214 */     this.endOffset = end;
/* 215 */     this.flags = flags;
/*     */   }
/*     */ 
/*     */   public Token(char[] startTermBuffer, int termBufferOffset, int termBufferLength, int start, int end)
/*     */   {
/* 229 */     copyBuffer(startTermBuffer, termBufferOffset, termBufferLength);
/* 230 */     this.startOffset = start;
/* 231 */     this.endOffset = end;
/*     */   }
/*     */ 
/*     */   public void setPositionIncrement(int positionIncrement)
/*     */   {
/* 261 */     if (positionIncrement < 0) {
/* 262 */       throw new IllegalArgumentException("Increment must be zero or greater: " + positionIncrement);
/*     */     }
/* 264 */     this.positionIncrement = positionIncrement;
/*     */   }
/*     */ 
/*     */   public int getPositionIncrement()
/*     */   {
/* 271 */     return this.positionIncrement;
/*     */   }
/*     */ 
/*     */   public final int startOffset()
/*     */   {
/* 281 */     return this.startOffset;
/*     */   }
/*     */ 
/*     */   public void setStartOffset(int offset)
/*     */   {
/* 287 */     this.startOffset = offset;
/*     */   }
/*     */ 
/*     */   public final int endOffset()
/*     */   {
/* 294 */     return this.endOffset;
/*     */   }
/*     */ 
/*     */   public void setEndOffset(int offset)
/*     */   {
/* 300 */     this.endOffset = offset;
/*     */   }
/*     */ 
/*     */   public void setOffset(int startOffset, int endOffset)
/*     */   {
/* 306 */     this.startOffset = startOffset;
/* 307 */     this.endOffset = endOffset;
/*     */   }
/*     */ 
/*     */   public final String type()
/*     */   {
/* 312 */     return this.type;
/*     */   }
/*     */ 
/*     */   public final void setType(String type)
/*     */   {
/* 318 */     this.type = type;
/*     */   }
/*     */ 
/*     */   public int getFlags()
/*     */   {
/* 332 */     return this.flags;
/*     */   }
/*     */ 
/*     */   public void setFlags(int flags)
/*     */   {
/* 339 */     this.flags = flags;
/*     */   }
/*     */ 
/*     */   public Payload getPayload()
/*     */   {
/* 346 */     return this.payload;
/*     */   }
/*     */ 
/*     */   public void setPayload(Payload payload)
/*     */   {
/* 353 */     this.payload = payload;
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 361 */     super.clear();
/* 362 */     this.payload = null;
/* 363 */     this.positionIncrement = 1;
/* 364 */     this.flags = 0;
/* 365 */     this.startOffset = (this.endOffset = 0);
/* 366 */     this.type = "word";
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/* 371 */     Token t = (Token)super.clone();
/*     */ 
/* 373 */     if (this.payload != null) {
/* 374 */       t.payload = ((Payload)this.payload.clone());
/*     */     }
/* 376 */     return t;
/*     */   }
/*     */ 
/*     */   public Token clone(char[] newTermBuffer, int newTermOffset, int newTermLength, int newStartOffset, int newEndOffset)
/*     */   {
/* 385 */     Token t = new Token(newTermBuffer, newTermOffset, newTermLength, newStartOffset, newEndOffset);
/* 386 */     t.positionIncrement = this.positionIncrement;
/* 387 */     t.flags = this.flags;
/* 388 */     t.type = this.type;
/* 389 */     if (this.payload != null)
/* 390 */       t.payload = ((Payload)this.payload.clone());
/* 391 */     return t;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 396 */     if (obj == this) {
/* 397 */       return true;
/*     */     }
/* 399 */     if ((obj instanceof Token)) {
/* 400 */       Token other = (Token)obj;
/* 401 */       return (this.startOffset == other.startOffset) && (this.endOffset == other.endOffset) && (this.flags == other.flags) && (this.positionIncrement == other.positionIncrement) && (this.type == null ? other.type == null : this.type.equals(other.type)) && (this.payload == null ? other.payload == null : this.payload.equals(other.payload)) && (super.equals(obj));
/*     */     }
/*     */ 
/* 410 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 415 */     int code = super.hashCode();
/* 416 */     code = code * 31 + this.startOffset;
/* 417 */     code = code * 31 + this.endOffset;
/* 418 */     code = code * 31 + this.flags;
/* 419 */     code = code * 31 + this.positionIncrement;
/* 420 */     if (this.type != null)
/* 421 */       code = code * 31 + this.type.hashCode();
/* 422 */     if (this.payload != null)
/* 423 */       code = code * 31 + this.payload.hashCode();
/* 424 */     return code;
/*     */   }
/*     */ 
/*     */   private void clearNoTermBuffer()
/*     */   {
/* 429 */     this.payload = null;
/* 430 */     this.positionIncrement = 1;
/* 431 */     this.flags = 0;
/* 432 */     this.startOffset = (this.endOffset = 0);
/* 433 */     this.type = "word";
/*     */   }
/*     */ 
/*     */   public Token reinit(char[] newTermBuffer, int newTermOffset, int newTermLength, int newStartOffset, int newEndOffset, String newType)
/*     */   {
/* 443 */     clearNoTermBuffer();
/* 444 */     copyBuffer(newTermBuffer, newTermOffset, newTermLength);
/* 445 */     this.payload = null;
/* 446 */     this.positionIncrement = 1;
/* 447 */     this.startOffset = newStartOffset;
/* 448 */     this.endOffset = newEndOffset;
/* 449 */     this.type = newType;
/* 450 */     return this;
/*     */   }
/*     */ 
/*     */   public Token reinit(char[] newTermBuffer, int newTermOffset, int newTermLength, int newStartOffset, int newEndOffset)
/*     */   {
/* 460 */     clearNoTermBuffer();
/* 461 */     copyBuffer(newTermBuffer, newTermOffset, newTermLength);
/* 462 */     this.startOffset = newStartOffset;
/* 463 */     this.endOffset = newEndOffset;
/* 464 */     this.type = "word";
/* 465 */     return this;
/*     */   }
/*     */ 
/*     */   public Token reinit(String newTerm, int newStartOffset, int newEndOffset, String newType)
/*     */   {
/* 475 */     clear();
/* 476 */     append(newTerm);
/* 477 */     this.startOffset = newStartOffset;
/* 478 */     this.endOffset = newEndOffset;
/* 479 */     this.type = newType;
/* 480 */     return this;
/*     */   }
/*     */ 
/*     */   public Token reinit(String newTerm, int newTermOffset, int newTermLength, int newStartOffset, int newEndOffset, String newType)
/*     */   {
/* 490 */     clear();
/* 491 */     append(newTerm, newTermOffset, newTermOffset + newTermLength);
/* 492 */     this.startOffset = newStartOffset;
/* 493 */     this.endOffset = newEndOffset;
/* 494 */     this.type = newType;
/* 495 */     return this;
/*     */   }
/*     */ 
/*     */   public Token reinit(String newTerm, int newStartOffset, int newEndOffset)
/*     */   {
/* 505 */     clear();
/* 506 */     append(newTerm);
/* 507 */     this.startOffset = newStartOffset;
/* 508 */     this.endOffset = newEndOffset;
/* 509 */     this.type = "word";
/* 510 */     return this;
/*     */   }
/*     */ 
/*     */   public Token reinit(String newTerm, int newTermOffset, int newTermLength, int newStartOffset, int newEndOffset)
/*     */   {
/* 520 */     clear();
/* 521 */     append(newTerm, newTermOffset, newTermOffset + newTermLength);
/* 522 */     this.startOffset = newStartOffset;
/* 523 */     this.endOffset = newEndOffset;
/* 524 */     this.type = "word";
/* 525 */     return this;
/*     */   }
/*     */ 
/*     */   public void reinit(Token prototype)
/*     */   {
/* 533 */     copyBuffer(prototype.buffer(), 0, prototype.length());
/* 534 */     this.positionIncrement = prototype.positionIncrement;
/* 535 */     this.flags = prototype.flags;
/* 536 */     this.startOffset = prototype.startOffset;
/* 537 */     this.endOffset = prototype.endOffset;
/* 538 */     this.type = prototype.type;
/* 539 */     this.payload = prototype.payload;
/*     */   }
/*     */ 
/*     */   public void reinit(Token prototype, String newTerm)
/*     */   {
/* 548 */     setEmpty().append(newTerm);
/* 549 */     this.positionIncrement = prototype.positionIncrement;
/* 550 */     this.flags = prototype.flags;
/* 551 */     this.startOffset = prototype.startOffset;
/* 552 */     this.endOffset = prototype.endOffset;
/* 553 */     this.type = prototype.type;
/* 554 */     this.payload = prototype.payload;
/*     */   }
/*     */ 
/*     */   public void reinit(Token prototype, char[] newTermBuffer, int offset, int length)
/*     */   {
/* 565 */     copyBuffer(newTermBuffer, offset, length);
/* 566 */     this.positionIncrement = prototype.positionIncrement;
/* 567 */     this.flags = prototype.flags;
/* 568 */     this.startOffset = prototype.startOffset;
/* 569 */     this.endOffset = prototype.endOffset;
/* 570 */     this.type = prototype.type;
/* 571 */     this.payload = prototype.payload;
/*     */   }
/*     */ 
/*     */   public void copyTo(AttributeImpl target)
/*     */   {
/* 576 */     if ((target instanceof Token)) {
/* 577 */       Token to = (Token)target;
/* 578 */       to.reinit(this);
/*     */ 
/* 580 */       if (this.payload != null)
/* 581 */         to.payload = ((Payload)this.payload.clone());
/*     */     }
/*     */     else {
/* 584 */       super.copyTo(target);
/* 585 */       ((OffsetAttribute)target).setOffset(this.startOffset, this.endOffset);
/* 586 */       ((PositionIncrementAttribute)target).setPositionIncrement(this.positionIncrement);
/* 587 */       ((PayloadAttribute)target).setPayload(this.payload == null ? null : (Payload)this.payload.clone());
/* 588 */       ((FlagsAttribute)target).setFlags(this.flags);
/* 589 */       ((PayloadAttribute)target).setType(this.type);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void reflectWith(AttributeReflector reflector)
/*     */   {
/* 595 */     super.reflectWith(reflector);
/* 596 */     reflector.reflect(OffsetAttribute.class, "startOffset", Integer.valueOf(this.startOffset));
/* 597 */     reflector.reflect(OffsetAttribute.class, "endOffset", Integer.valueOf(this.endOffset));
/* 598 */     reflector.reflect(PositionIncrementAttribute.class, "positionIncrement", Integer.valueOf(this.positionIncrement));
/* 599 */     reflector.reflect(PayloadAttribute.class, "payload", this.payload);
/* 600 */     reflector.reflect(FlagsAttribute.class, "flags", Integer.valueOf(this.flags));
/* 601 */     reflector.reflect(PayloadAttribute.class, "type", this.type);
/*     */   }
/*     */ 
/*     */   public static final class TokenAttributeFactory extends AttributeSource.AttributeFactory
/*     */   {
/*     */     private final AttributeSource.AttributeFactory delegate;
/*     */ 
/*     */     public TokenAttributeFactory(AttributeSource.AttributeFactory delegate)
/*     */     {
/* 623 */       this.delegate = delegate;
/*     */     }
/*     */ 
/*     */     public AttributeImpl createAttributeInstance(Class<? extends Attribute> attClass)
/*     */     {
/* 628 */       return attClass.isAssignableFrom(Token.class) ? new Token() : this.delegate.createAttributeInstance(attClass);
/*     */     }
/*     */ 
/*     */     public boolean equals(Object other)
/*     */     {
/* 634 */       if (this == other) return true;
/* 635 */       if ((other instanceof TokenAttributeFactory)) {
/* 636 */         TokenAttributeFactory af = (TokenAttributeFactory)other;
/* 637 */         return this.delegate.equals(af.delegate);
/*     */       }
/* 639 */       return false;
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 644 */       return this.delegate.hashCode() ^ 0xA45AA31;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.Token
 * JD-Core Version:    0.6.0
 */