/*    */ package com.dukascopy.charts.view.paintingtechnic;
/*    */ 
/*    */ import com.dukascopy.charts.view.swing.PaintingTechnic;
/*    */ import java.awt.Color;
/*    */ import java.awt.Font;
/*    */ import java.awt.Graphics;
/*    */ import java.awt.image.VolatileImage;
/*    */ import java.util.concurrent.atomic.AtomicBoolean;
/*    */ import javax.swing.JComponent;
/*    */ 
/*    */ class VolatilePaintingTechnic
/*    */   implements PaintingTechnic, InvalidateContentListener
/*    */ {
/*    */   int height;
/*    */   int width;
/*    */   VolatileImage backBuffer;
/* 16 */   AtomicBoolean contentInvalid = new AtomicBoolean(false);
/*    */   final StaticDynamicData staticDynamicData;
/*    */   final InvalidationContent contentToBeInvalidated;
/*    */ 
/*    */   protected VolatilePaintingTechnic(InvalidationContent contentToBeInvalidated, StaticDynamicData staticDynamicData)
/*    */   {
/* 23 */     this.contentToBeInvalidated = contentToBeInvalidated;
/* 24 */     this.staticDynamicData = staticDynamicData;
/*    */   }
/*    */ 
/*    */   public void paint(Graphics g, JComponent jComponent) {
/* 28 */     Color prevColor = g.getColor();
/* 29 */     Font prevFont = g.getFont();
/*    */ 
/* 31 */     reinitBackBufferIfNecessary(jComponent);
/* 32 */     redrawStaticContent(g, jComponent);
/* 33 */     this.staticDynamicData.drawDynamicData(g, jComponent);
/*    */ 
/* 35 */     g.setColor(prevColor);
/* 36 */     g.setFont(prevFont);
/*    */   }
/*    */ 
/*    */   void reinitBackBufferIfNecessary(JComponent jComponent)
/*    */   {
/* 41 */     if ((this.backBuffer != null) && (this.height == jComponent.getHeight()) && (this.width == jComponent.getWidth())) {
/* 42 */       return;
/*    */     }
/* 44 */     createBackBuffer(jComponent);
/* 45 */     this.width = jComponent.getWidth();
/* 46 */     this.height = jComponent.getHeight();
/*    */   }
/*    */ 
/*    */   void createBackBuffer(JComponent jComponent) {
/* 50 */     if (this.backBuffer != null) {
/* 51 */       this.backBuffer.flush();
/* 52 */       this.backBuffer = null;
/*    */     }
/* 54 */     this.backBuffer = jComponent.createVolatileImage(jComponent.getWidth(), jComponent.getHeight());
/*    */   }
/*    */ 
/*    */   void redrawDataOntoBackBuffer(JComponent jComponent) {
/* 58 */     Graphics gBB = this.backBuffer.getGraphics();
/*    */     try {
/* 60 */       this.staticDynamicData.drawStaticData(gBB, jComponent);
/*    */     } finally {
/* 62 */       gBB.dispose();
/*    */     }
/*    */   }
/*    */ 
/*    */   void redrawStaticContent(Graphics g, JComponent jComponent) {
/*    */     do {
/* 68 */       int valCode = this.backBuffer.validate(jComponent.getGraphicsConfiguration());
/* 69 */       if (valCode == 1) {
/* 70 */         redrawDataOntoBackBuffer(jComponent);
/* 71 */       } else if (valCode == 2) {
/* 72 */         createBackBuffer(jComponent);
/* 73 */         redrawDataOntoBackBuffer(jComponent);
/* 74 */       } else if (this.contentInvalid.compareAndSet(true, false)) {
/* 75 */         redrawDataOntoBackBuffer(jComponent);
/*    */       }
/* 77 */       g.drawImage(this.backBuffer, 0, 0, jComponent);
/*    */     }
/* 79 */     while (this.backBuffer.contentsLost());
/*    */   }
/*    */ 
/*    */   public void invalidateContent()
/*    */   {
/* 87 */     this.contentInvalid.set(true);
/*    */   }
/*    */ 
/*    */   public InvalidationContent getInvalidateContentType()
/*    */   {
/* 92 */     return this.contentToBeInvalidated;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.paintingtechnic.VolatilePaintingTechnic
 * JD-Core Version:    0.6.0
 */