/*    */ package com.dukascopy.dds2.greed.gui.window;
/*    */ 
/*    */ import java.awt.Dimension;
/*    */ import java.awt.Point;
/*    */ import java.awt.Toolkit;
/*    */ import javax.swing.JFrame;
/*    */ 
/*    */ public class WindowManager
/*    */ {
/*    */   private Point initialPivot;
/*    */   private Point pivot;
/*    */   private Dimension screen;
/* 17 */   private boolean isClipping = false;
/*    */ 
/*    */   public WindowManager(int initialX, int initialY, boolean isClipping)
/*    */   {
/* 27 */     this.initialPivot = new Point(initialX, initialY);
/* 28 */     this.pivot = new Point(initialX, initialY);
/* 29 */     this.isClipping = isClipping;
/* 30 */     this.screen = Toolkit.getDefaultToolkit().getScreenSize();
/*    */   }
/*    */ 
/*    */   public void layout(JFrame frame)
/*    */   {
/* 38 */     Dimension frameSize = frame.getSize();
/* 39 */     if (this.isClipping) {
/* 40 */       if (this.pivot.getX() + frameSize.getWidth() < this.screen.getWidth()) {
/* 41 */         if (this.pivot.getY() + frameSize.getHeight() > this.screen.getHeight()) {
/* 42 */           this.pivot.setLocation(this.pivot.getX(), this.initialPivot.getY());
/*    */         }
/* 44 */         frame.setLocation(this.pivot);
/*    */       } else {
/* 46 */         getPoint(frameSize.getWidth(), frameSize.getHeight());
/* 47 */         if (this.pivot.getY() + frameSize.getHeight() > this.screen.getHeight()) {
/* 48 */           this.pivot.setLocation(this.pivot.getX(), this.initialPivot.getY());
/*    */         }
/* 50 */         frame.setLocation(this.pivot);
/*    */       }
/*    */     }
/* 53 */     else frame.setLocation(this.pivot);
/*    */ 
/* 55 */     getPoint(frameSize.getWidth(), frameSize.getHeight());
/*    */   }
/*    */ 
/*    */   private void getPoint(double frameX, double frameY) {
/* 59 */     if (this.pivot.getX() + frameX > this.screen.getWidth()) {
/* 60 */       if (this.pivot.getY() + frameY < this.screen.getHeight())
/* 61 */         this.pivot = new Point((int)this.initialPivot.getX(), (int)(this.pivot.getY() + frameY));
/*    */       else
/* 63 */         this.pivot = new Point(this.initialPivot);
/*    */     }
/*    */     else
/* 66 */       this.pivot = new Point((int)(this.pivot.getX() + frameX), (int)this.pivot.getY());
/*    */   }
/*    */ 
/*    */   public void resetStartingPoint() {
/* 70 */     this.pivot = new Point(this.initialPivot);
/*    */   }
/*    */ 
/*    */   public void setIsClipping(boolean isClipping)
/*    */   {
/* 78 */     this.isClipping = isClipping;
/*    */   }
/*    */ 
/*    */   public boolean getIsClipping()
/*    */   {
/* 85 */     return this.isClipping;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.window.WindowManager
 * JD-Core Version:    0.6.0
 */