/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import B;
/*     */ import java.io.Serializable;
/*     */ import org.apache.lucene.util.ArrayUtil;
/*     */ 
/*     */ public class Payload
/*     */   implements Serializable, Cloneable
/*     */ {
/*     */   protected byte[] data;
/*     */   protected int offset;
/*     */   protected int length;
/*     */ 
/*     */   public Payload()
/*     */   {
/*     */   }
/*     */ 
/*     */   public Payload(byte[] data)
/*     */   {
/*  60 */     this(data, 0, data.length);
/*     */   }
/*     */ 
/*     */   public Payload(byte[] data, int offset, int length)
/*     */   {
/*  73 */     if ((offset < 0) || (offset + length > data.length)) {
/*  74 */       throw new IllegalArgumentException();
/*     */     }
/*  76 */     this.data = data;
/*  77 */     this.offset = offset;
/*  78 */     this.length = length;
/*     */   }
/*     */ 
/*     */   public void setData(byte[] data)
/*     */   {
/*  87 */     setData(data, 0, data.length);
/*     */   }
/*     */ 
/*     */   public void setData(byte[] data, int offset, int length)
/*     */   {
/*  96 */     this.data = data;
/*  97 */     this.offset = offset;
/*  98 */     this.length = length;
/*     */   }
/*     */ 
/*     */   public byte[] getData()
/*     */   {
/* 106 */     return this.data;
/*     */   }
/*     */ 
/*     */   public int getOffset()
/*     */   {
/* 113 */     return this.offset;
/*     */   }
/*     */ 
/*     */   public int length()
/*     */   {
/* 120 */     return this.length;
/*     */   }
/*     */ 
/*     */   public byte byteAt(int index)
/*     */   {
/* 127 */     if ((0 <= index) && (index < this.length)) {
/* 128 */       return this.data[(this.offset + index)];
/*     */     }
/* 130 */     throw new ArrayIndexOutOfBoundsException(index);
/*     */   }
/*     */ 
/*     */   public byte[] toByteArray()
/*     */   {
/* 137 */     byte[] retArray = new byte[this.length];
/* 138 */     System.arraycopy(this.data, this.offset, retArray, 0, this.length);
/* 139 */     return retArray;
/*     */   }
/*     */ 
/*     */   public void copyTo(byte[] target, int targetOffset)
/*     */   {
/* 149 */     if (this.length > target.length + targetOffset) {
/* 150 */       throw new ArrayIndexOutOfBoundsException();
/*     */     }
/* 152 */     System.arraycopy(this.data, this.offset, target, targetOffset, this.length);
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/*     */     try
/*     */     {
/* 163 */       Payload clone = (Payload)super.clone();
/*     */ 
/* 165 */       if ((this.offset == 0) && (this.length == this.data.length))
/*     */       {
/* 167 */         clone.data = ((byte[])this.data.clone());
/*     */       }
/*     */       else
/*     */       {
/* 171 */         clone.data = toByteArray();
/* 172 */         clone.offset = 0;
/*     */       }
/* 174 */       return clone; } catch (CloneNotSupportedException e) {
/*     */     }
/* 176 */     throw new RuntimeException(e);
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 182 */     if (obj == this)
/* 183 */       return true;
/* 184 */     if ((obj instanceof Payload)) {
/* 185 */       Payload other = (Payload)obj;
/* 186 */       if (this.length == other.length) {
/* 187 */         for (int i = 0; i < this.length; i++)
/* 188 */           if (this.data[(this.offset + i)] != other.data[(other.offset + i)])
/* 189 */             return false;
/* 190 */         return true;
/*     */       }
/* 192 */       return false;
/*     */     }
/* 194 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 199 */     return ArrayUtil.hashCode(this.data, this.offset, this.offset + this.length);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.Payload
 * JD-Core Version:    0.6.0
 */