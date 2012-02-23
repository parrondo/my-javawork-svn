/*    */ package com.dukascopy.dds2.greed.gui.component.settings;
/*    */ 
/*    */ import java.awt.Component;
/*    */ import java.awt.Container;
/*    */ import java.awt.GridBagConstraints;
/*    */ import javax.swing.JPanel;
/*    */ 
/*    */ public abstract class AbstractSettingsPanel extends JPanel
/*    */   implements ISettingsPanel
/*    */ {
/*    */   protected final SettingsTabbedFrame parent;
/*    */ 
/*    */   protected AbstractSettingsPanel(SettingsTabbedFrame parent)
/*    */   {
/* 18 */     this.parent = parent;
/*    */ 
/* 20 */     build();
/*    */   }
/*    */ 
/*    */   protected abstract void build();
/*    */ 
/*    */   protected static void gbc(Container container, Component component, int gridX, int gridY, int weightX, int weightY, int anchor, int fill)
/*    */   {
/* 32 */     gbc(container, component, gridX, gridY, weightX, weightY, 1, 1, anchor, fill);
/*    */   }
/*    */ 
/*    */   protected static void gbc(Container container, Component component, int gridX, int gridY, int weightX, int weightY, int gridWidth, int gridHeight, int anchor, int fill)
/*    */   {
/* 43 */     gbc(container, component, gridX, gridY, weightX, weightY, gridWidth, gridHeight, 0, 0, 0, 0, anchor, fill);
/*    */   }
/*    */ 
/*    */   protected static void gbc(Container container, Component component, int gridX, int gridY, int weightX, int weightY, int gridWidth, int gridHeight, int insetLeft, int insetRight, int insetTop, int insetBottom, int anchor, int fill)
/*    */   {
/* 68 */     GridBagConstraints gbc = new GridBagConstraints();
/*    */ 
/* 70 */     gbc.gridx = gridX;
/* 71 */     gbc.gridy = gridY;
/* 72 */     gbc.weightx = weightX;
/* 73 */     gbc.weighty = weightY;
/* 74 */     gbc.gridwidth = gridWidth;
/* 75 */     gbc.gridheight = gridHeight;
/* 76 */     gbc.anchor = anchor;
/* 77 */     gbc.fill = fill;
/* 78 */     gbc.insets.left = insetLeft;
/* 79 */     gbc.insets.right = insetRight;
/* 80 */     gbc.insets.top = insetTop;
/* 81 */     gbc.insets.bottom = insetBottom;
/*    */ 
/* 83 */     container.add(component, gbc);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.settings.AbstractSettingsPanel
 * JD-Core Version:    0.6.0
 */