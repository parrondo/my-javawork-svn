/*    */ package com.dukascopy.dds2.greed.gui.component.table;
/*    */ 
/*    */ import java.awt.Dimension;
/*    */ import java.awt.Point;
/*    */ import javax.swing.BoundedRangeModel;
/*    */ import javax.swing.JScrollBar;
/*    */ import javax.swing.JScrollPane;
/*    */ import javax.swing.JTable;
/*    */ import javax.swing.JViewport;
/*    */ import javax.swing.event.ChangeEvent;
/*    */ import javax.swing.event.ChangeListener;
/*    */ import javax.swing.plaf.basic.BasicScrollPaneUI;
/*    */ 
/*    */ public class TableScrollPaneUI extends BasicScrollPaneUI
/*    */ {
/*    */   JTable table;
/*    */ 
/*    */   public TableScrollPaneUI(JTable table)
/*    */   {
/* 19 */     this.table = table;
/*    */   }
/*    */ 
/*    */   protected ChangeListener createVSBChangeListener()
/*    */   {
/* 24 */     return new myVSBChangeListener();
/*    */   }
/*    */ 
/*    */   protected ChangeListener createHSBChangeListener()
/*    */   {
/* 29 */     return new myHSBChangeListener();
/*    */   }
/*    */ 
/*    */   protected ChangeListener createViewportChangeListener() {
/* 33 */     return new myViewportChangeHandler();
/*    */   }
/*    */ 
/*    */   public void showCoordinates(Point p, JViewport viewport)
/*    */   {
/* 44 */     int br = this.table.rowAtPoint(p) + 1;
/* 45 */     int bc = this.table.columnAtPoint(p);
/* 46 */     Dimension d = viewport.getExtentSize();
/* 47 */     p = new Point(p.x + d.width, p.y + d.height);
/* 48 */     int er = this.table.rowAtPoint(p) + 1;
/* 49 */     if (er == 0)
/* 50 */       er = this.table.getRowCount();
/* 51 */     int ec = this.table.columnAtPoint(p);
/* 52 */     if (ec != 0)
/* 53 */       ec = this.table.getColumnCount();
/*    */   }
/*    */ 
/*    */   public class myVSBChangeListener
/*    */     implements ChangeListener
/*    */   {
/*    */     public myVSBChangeListener()
/*    */     {
/*    */     }
/*    */ 
/*    */     public void stateChanged(ChangeEvent e)
/*    */     {
/* 74 */       JViewport viewport = TableScrollPaneUI.this.scrollpane.getViewport();
/* 75 */       if (viewport != null) {
/* 76 */         BoundedRangeModel model = (BoundedRangeModel)(BoundedRangeModel)e.getSource();
/* 77 */         Point p = viewport.getViewPosition();
/* 78 */         p.y = model.getValue();
/* 79 */         JScrollBar sb = TableScrollPaneUI.this.scrollpane.getVerticalScrollBar();
/* 80 */         if (sb.getValueIsAdjusting())
/* 81 */           TableScrollPaneUI.this.showCoordinates(p, viewport);
/*    */         else
/* 83 */           viewport.setViewPosition(p);
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   public class myHSBChangeListener
/*    */     implements ChangeListener
/*    */   {
/*    */     public myHSBChangeListener()
/*    */     {
/*    */     }
/*    */ 
/*    */     public void stateChanged(ChangeEvent e)
/*    */     {
/* 58 */       JViewport viewport = TableScrollPaneUI.this.scrollpane.getViewport();
/* 59 */       if (viewport != null) {
/* 60 */         BoundedRangeModel model = (BoundedRangeModel)(BoundedRangeModel)e.getSource();
/* 61 */         Point p = viewport.getViewPosition();
/* 62 */         p.x = model.getValue();
/* 63 */         JScrollBar sb = TableScrollPaneUI.this.scrollpane.getHorizontalScrollBar();
/* 64 */         if (sb.getValueIsAdjusting())
/* 65 */           TableScrollPaneUI.this.showCoordinates(p, viewport);
/*    */         else
/* 67 */           viewport.setViewPosition(p);
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   public class myViewportChangeHandler
/*    */     implements ChangeListener
/*    */   {
/*    */     public myViewportChangeHandler()
/*    */     {
/*    */     }
/*    */ 
/*    */     public void stateChanged(ChangeEvent e)
/*    */     {
/* 39 */       TableScrollPaneUI.this.syncScrollPaneWithViewport();
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.table.TableScrollPaneUI
 * JD-Core Version:    0.6.0
 */