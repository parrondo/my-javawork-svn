/*    */ package com.dukascopy.dds2.greed.gui.component;
/*    */ 
/*    */ import java.awt.Component;
/*    */ import javax.swing.JSplitPane;
/*    */ 
/*    */ public class ExpandableSplitPane extends JSplitPane
/*    */ {
/*    */   private static final long serialVersionUID = 4921415884127348852L;
/*    */   private static final int MINIMUM_LOCATION = 1;
/*    */   private static final int ARBITRARY_TOLERANCE = 15;
/*    */ 
/*    */   public ExpandableSplitPane(String name, int orientation, Component leftComponent, Component rightComponent)
/*    */   {
/* 24 */     super(orientation, leftComponent, rightComponent);
/* 25 */     setName(name);
/*    */ 
/* 27 */     setOneTouchExpandable(false);
/*    */   }
/*    */ 
/*    */   public ExpandableSplitPane(String name) {
/* 31 */     this(name, 0, null, null);
/*    */   }
/*    */ 
/*    */   public boolean isMinimized() {
/* 35 */     return (getDividerLocation() == 1) || (getDividerLocation() <= 15);
/*    */   }
/*    */ 
/*    */   public boolean isMaximized()
/*    */   {
/* 40 */     return (getDividerLocation() == 1) || (getDividerLocation() >= getHeight() - 15);
/*    */   }
/*    */ 
/*    */   public void minimize()
/*    */   {
/* 45 */     setDividerLocation(0.0D);
/*    */   }
/*    */ 
/*    */   public void maximize() {
/* 49 */     setDividerLocation(1.0D);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.ExpandableSplitPane
 * JD-Core Version:    0.6.0
 */