/*    */ package com.dukascopy.dds2.greed.gui.component.quoter;
/*    */ 
/*    */ import java.awt.Container;
/*    */ import java.awt.Point;
/*    */ import java.awt.event.MouseEvent;
/*    */ import java.awt.event.MouseListener;
/*    */ import java.awt.event.MouseMotionListener;
/*    */ import javax.swing.JComponent;
/*    */ import javax.swing.JFrame;
/*    */ 
/*    */ public class MoveMouseListener
/*    */   implements MouseListener, MouseMotionListener
/*    */ {
/*    */   JComponent target;
/*    */   Point startDrag;
/*    */   Point startLoc;
/*    */ 
/*    */   public MoveMouseListener(JComponent target)
/*    */   {
/* 22 */     this.target = target;
/*    */   }
/*    */ 
/*    */   public static JFrame getFrame(Container target) {
/* 26 */     if ((target instanceof JFrame)) {
/* 27 */       return (JFrame)target;
/*    */     }
/* 29 */     return getFrame(target.getParent());
/*    */   }
/*    */ 
/*    */   Point getScreenLocation(MouseEvent e) {
/* 33 */     Point cursor = e.getPoint();
/* 34 */     Point target_location = this.target.getLocationOnScreen();
/* 35 */     return new Point((int)(target_location.getX() + cursor.getX()), (int)(target_location.getY() + cursor.getY()));
/*    */   }
/*    */ 
/*    */   public void mouseClicked(MouseEvent e)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void mouseEntered(MouseEvent e)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void mouseExited(MouseEvent e) {
/*    */   }
/*    */ 
/*    */   public void mousePressed(MouseEvent e) {
/* 50 */     this.startDrag = getScreenLocation(e);
/* 51 */     this.startLoc = getFrame(this.target).getLocation();
/*    */   }
/*    */ 
/*    */   public void mouseReleased(MouseEvent e) {
/*    */   }
/*    */ 
/*    */   public void mouseDragged(MouseEvent e) {
/* 58 */     Point current = getScreenLocation(e);
/* 59 */     Point offset = new Point((int)current.getX() - (int)this.startDrag.getX(), (int)current.getY() - (int)this.startDrag.getY());
/*    */ 
/* 62 */     JFrame frame = getFrame(this.target);
/* 63 */     Point new_location = new Point((int)(this.startLoc.getX() + offset.getX()), (int)(this.startLoc.getY() + offset.getY()));
/*    */ 
/* 66 */     frame.setLocation(new_location);
/*    */   }
/*    */ 
/*    */   public void mouseMoved(MouseEvent e)
/*    */   {
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.quoter.MoveMouseListener
 * JD-Core Version:    0.6.0
 */