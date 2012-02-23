/*    */ package com.dukascopy.dds2.greed.util;
/*    */ 
/*    */ import java.awt.Component;
/*    */ import java.awt.Container;
/*    */ import java.awt.GridBagConstraints;
/*    */ 
/*    */ public class GridBagLayoutHelper
/*    */ {
/*    */   public static void add(int gridx, int gridy, double weightx, double weighty, int gridwidth, int gridheight, GridBagConstraints gbc, Container container, Component component)
/*    */   {
/* 13 */     gbc.gridx = gridx;
/* 14 */     gbc.gridy = gridy;
/* 15 */     gbc.weightx = weightx;
/* 16 */     gbc.weighty = weighty;
/* 17 */     gbc.gridwidth = gridwidth;
/* 18 */     gbc.gridheight = gridheight;
/* 19 */     container.add(component, gbc);
/*    */   }
/*    */ 
/*    */   public static void add(int gridx, int gridy, double weightx, double weighty, int fill, GridBagConstraints gbc, Container container, Component component)
/*    */   {
/* 24 */     gbc.gridx = gridx;
/* 25 */     gbc.gridy = gridy;
/* 26 */     gbc.weightx = weightx;
/* 27 */     gbc.weighty = weighty;
/* 28 */     gbc.fill = fill;
/* 29 */     container.add(component, gbc);
/*    */   }
/*    */ 
/*    */   public static void add(int gridx, int gridy, double weightx, double weighty, int gridwidth, int gridheight, int left, int top, int right, int bottom, GridBagConstraints gbc, Container container, Component component)
/*    */   {
/* 34 */     gbc.gridx = gridx;
/* 35 */     gbc.gridy = gridy;
/* 36 */     gbc.weightx = weightx;
/* 37 */     gbc.weighty = weighty;
/* 38 */     gbc.gridwidth = gridwidth;
/* 39 */     gbc.gridheight = gridheight;
/* 40 */     gbc.insets.left = left;
/* 41 */     gbc.insets.top = top;
/* 42 */     gbc.insets.right = right;
/* 43 */     gbc.insets.bottom = bottom;
/* 44 */     container.add(component, gbc);
/*    */   }
/*    */ 
/*    */   public static void add(int gridx, int gridy, double weightx, double weighty, int gridwidth, int gridheight, int left, int top, int right, int bottom, int fill, int anchor, GridBagConstraints gbc, Container container, Component component)
/*    */   {
/* 49 */     gbc.gridx = gridx;
/* 50 */     gbc.gridy = gridy;
/* 51 */     gbc.weightx = weightx;
/* 52 */     gbc.weighty = weighty;
/* 53 */     gbc.gridwidth = gridwidth;
/* 54 */     gbc.gridheight = gridheight;
/* 55 */     gbc.insets.left = left;
/* 56 */     gbc.insets.top = top;
/* 57 */     gbc.insets.right = right;
/* 58 */     gbc.insets.bottom = bottom;
/* 59 */     gbc.fill = fill;
/* 60 */     gbc.anchor = anchor;
/* 61 */     container.add(component, gbc);
/*    */   }
/*    */ 
/*    */   public static void add(int gridx, int gridy, double weightx, double weighty, int gridwidth, int gridheight, int left, int top, int right, int bottom, int fill, int anchor, int ipadx, int ipady, GridBagConstraints gbc, Container container, Component component)
/*    */   {
/* 67 */     gbc.gridx = gridx;
/* 68 */     gbc.gridy = gridy;
/* 69 */     gbc.weightx = weightx;
/* 70 */     gbc.weighty = weighty;
/* 71 */     gbc.gridwidth = gridwidth;
/* 72 */     gbc.gridheight = gridheight;
/* 73 */     gbc.insets.left = left;
/* 74 */     gbc.insets.top = top;
/* 75 */     gbc.insets.right = right;
/* 76 */     gbc.insets.bottom = bottom;
/* 77 */     gbc.fill = fill;
/* 78 */     gbc.anchor = anchor;
/* 79 */     gbc.ipadx = ipadx;
/* 80 */     gbc.ipady = ipady;
/* 81 */     container.add(component, gbc);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.GridBagLayoutHelper
 * JD-Core Version:    0.6.0
 */