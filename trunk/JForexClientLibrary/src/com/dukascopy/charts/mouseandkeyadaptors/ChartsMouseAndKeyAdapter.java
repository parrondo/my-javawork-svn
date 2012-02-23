/*    */ package com.dukascopy.charts.mouseandkeyadaptors;
/*    */ 
/*    */ import java.awt.event.FocusEvent;
/*    */ import java.awt.event.FocusListener;
/*    */ import java.awt.event.KeyEvent;
/*    */ import java.awt.event.KeyListener;
/*    */ import java.awt.event.MouseAdapter;
/*    */ import java.awt.event.MouseEvent;
/*    */ 
/*    */ public class ChartsMouseAndKeyAdapter extends MouseAdapter
/*    */   implements KeyListener, FocusListener
/*    */ {
/*    */   protected boolean mouseOverPane;
/*    */ 
/*    */   public void mouseEntered(MouseEvent e)
/*    */   {
/* 16 */     this.mouseOverPane = true;
/*    */   }
/*    */ 
/*    */   public void mouseExited(MouseEvent e)
/*    */   {
/* 21 */     this.mouseOverPane = false;
/*    */   }
/*    */ 
/*    */   public void keyTyped(KeyEvent e)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void keyPressed(KeyEvent e)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void keyReleased(KeyEvent e)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void focusGained(FocusEvent e)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void focusLost(FocusEvent e)
/*    */   {
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.mouseandkeyadaptors.ChartsMouseAndKeyAdapter
 * JD-Core Version:    0.6.0
 */