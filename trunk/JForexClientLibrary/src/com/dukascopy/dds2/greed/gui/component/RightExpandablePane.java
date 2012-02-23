/*    */ package com.dukascopy.dds2.greed.gui.component;
/*    */ 
/*    */ import java.awt.Component;
/*    */ 
/*    */ public class RightExpandablePane extends ExpandablePane
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public RightExpandablePane(String name, Component mainComponent, Component rightComponent, double splitProportionalLocation, int dividerSize, boolean expanded, boolean resizable)
/*    */   {
/* 15 */     super(name, 1, mainComponent, rightComponent, splitProportionalLocation, dividerSize, 1.0D, expanded, resizable);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.RightExpandablePane
 * JD-Core Version:    0.6.0
 */