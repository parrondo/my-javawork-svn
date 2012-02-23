/*    */ package com.dukascopy.dds2.greed.gui.component;
/*    */ 
/*    */ import java.awt.Component;
/*    */ 
/*    */ public class BottomExpandablePane extends ExpandablePane
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public BottomExpandablePane(String name, Component topComponent, Component bottomComponent, double splitProportionalLocation, int dividerSize, boolean expanded, boolean resizable)
/*    */   {
/* 16 */     super(name, 0, topComponent, bottomComponent, splitProportionalLocation, dividerSize, 1.0D, expanded, resizable);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.BottomExpandablePane
 * JD-Core Version:    0.6.0
 */