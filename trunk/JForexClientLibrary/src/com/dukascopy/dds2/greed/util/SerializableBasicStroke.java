/*    */ package com.dukascopy.dds2.greed.util;
/*    */ 
/*    */ import java.awt.BasicStroke;
/*    */ import java.io.IOException;
/*    */ import java.io.InvalidObjectException;
/*    */ import java.io.ObjectInputStream;
/*    */ import java.io.ObjectOutputStream;
/*    */ import java.io.ObjectStreamException;
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public class SerializableBasicStroke extends BasicStroke
/*    */   implements Serializable
/*    */ {
/*    */   public static BasicStroke serializable(BasicStroke target)
/*    */   {
/* 50 */     return (target instanceof Serializable) ? target : new SerializableBasicStroke(target.getLineWidth(), target.getEndCap(), target.getLineJoin(), target.getMiterLimit(), target.getDashArray(), target.getDashPhase());
/*    */   }
/*    */ 
/*    */   public SerializableBasicStroke()
/*    */   {
/*    */   }
/*    */ 
/*    */   public SerializableBasicStroke(float lineWidth, int endCap, int lineJoin, float miterLimit, float[] dashArray, float dashPhase)
/*    */   {
/* 63 */     super(lineWidth, endCap, lineJoin, miterLimit, dashArray, dashPhase);
/*    */   }
/*    */ 
/*    */   private Object writeReplace() throws ObjectStreamException {
/* 67 */     return new Serial(this);
/*    */   }
/*    */ 
/*    */   private static class Serial
/*    */     implements Serializable
/*    */   {
/*    */     static final long serialVersionUID = 1L;
/*    */     private transient SerializableBasicStroke replacement;
/*    */ 
/*    */     Serial(SerializableBasicStroke replacement)
/*    */     {
/* 12 */       this.replacement = replacement;
/*    */     }
/*    */ 
/*    */     private void writeObject(ObjectOutputStream out) throws IOException
/*    */     {
/* 17 */       out.writeFloat(this.replacement.getLineWidth());
/* 18 */       out.writeInt(this.replacement.getEndCap());
/* 19 */       out.writeInt(this.replacement.getLineJoin());
/* 20 */       out.writeFloat(this.replacement.getMiterLimit());
/* 21 */       out.writeUnshared(this.replacement.getDashArray());
/* 22 */       out.writeFloat(this.replacement.getDashPhase());
/*    */     }
/*    */ 
/*    */     private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
/*    */     {
/*    */       try {
/* 28 */         this.replacement = new SerializableBasicStroke(in.readFloat(), in.readInt(), in.readInt(), in.readFloat(), (float[])(float[])in.readUnshared(), in.readFloat());
/*    */       }
/*    */       catch (IllegalArgumentException exc)
/*    */       {
/* 37 */         InvalidObjectException wrapper = new InvalidObjectException(exc.getMessage());
/*    */ 
/* 39 */         wrapper.initCause(exc);
/* 40 */         throw wrapper;
/*    */       }
/*    */     }
/*    */ 
/*    */     private Object readResolve() throws ObjectStreamException {
/* 45 */       return this.replacement;
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.SerializableBasicStroke
 * JD-Core Version:    0.6.0
 */