/*     */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI;
/*     */ 
/*     */ import java.awt.Dimension;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Insets;
/*     */ import javax.swing.ComboBoxEditor;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.ListCellRenderer;
/*     */ import javax.swing.border.Border;
/*     */ import javax.swing.border.EmptyBorder;
/*     */ import javax.swing.plaf.basic.BasicComboBoxEditor;
/*     */ import javax.swing.plaf.basic.BasicComboBoxRenderer;
/*     */ import javax.swing.plaf.basic.BasicComboBoxUI;
/*     */ 
/*     */ public class ResizableComboBoxUI extends BasicComboBoxUI
/*     */ {
/*  23 */   public static final Dimension DEFAULT_SIZE = new Dimension(90, 20);
/*     */   private Dimension defaultSize;
/*     */ 
/*     */   public ResizableComboBoxUI()
/*     */   {
/*  28 */     this(DEFAULT_SIZE);
/*     */   }
/*     */ 
/*     */   public ResizableComboBoxUI(Dimension defaultSize) {
/*  32 */     this.defaultSize = defaultSize;
/*     */   }
/*     */ 
/*     */   protected ListCellRenderer createRenderer()
/*     */   {
/*  37 */     BasicComboBoxRenderer renderer = (BasicComboBoxRenderer)super.createRenderer();
/*  38 */     renderer.setBorder(new EmptyBorder(CommonUIConstants.DEFAULT_COMPONENT_INSETS));
/*  39 */     return renderer;
/*     */   }
/*     */ 
/*     */   protected ComboBoxEditor createEditor()
/*     */   {
/*  44 */     BasicComboBoxEditor editor = (BasicComboBoxEditor)super.createEditor();
/*  45 */     JTextField textField = (JTextField)editor.getEditorComponent();
/*  46 */     textField.setBorder(new EmptyBorder(CommonUIConstants.DEFAULT_COMPONENT_INSETS));
/*  47 */     return editor;
/*     */   }
/*     */ 
/*     */   public Dimension getPreferredSize(JComponent c)
/*     */   {
/*  52 */     return computeSize(c);
/*     */   }
/*     */ 
/*     */   public Dimension getMinimumSize(JComponent c)
/*     */   {
/*  57 */     return computeSize(c);
/*     */   }
/*     */ 
/*     */   public Dimension getMaximumSize(JComponent c)
/*     */   {
/*  62 */     return super.getMaximumSize(c);
/*     */   }
/*     */ 
/*     */   protected Dimension computeSize(JComponent c)
/*     */   {
/*  73 */     JComboBox box = (JComboBox)c;
/*  74 */     FontMetrics fontMetrics = box.getFontMetrics(box.getFont());
/*     */ 
/*  76 */     int finalWidth = 0;
/*  77 */     int itemsCount = box.getItemCount();
/*     */ 
/*  79 */     for (int i = 0; i < itemsCount; i++) {
/*  80 */       String obj = box.getItemAt(i).toString();
/*  81 */       int objWidth = fontMetrics.stringWidth(obj);
/*     */ 
/*  83 */       if (objWidth > finalWidth) {
/*  84 */         finalWidth = objWidth;
/*     */       }
/*     */     }
/*     */ 
/*  88 */     if (finalWidth > 0)
/*     */     {
/*  90 */       int buttonWidth = (int)this.arrowButton.getPreferredSize().getWidth();
/*  91 */       Insets buttonInsets = this.arrowButton.getBorder().getBorderInsets(c);
/*  92 */       buttonWidth += buttonInsets.left + buttonInsets.right;
/*     */ 
/*  94 */       finalWidth += buttonWidth;
/*     */ 
/*  96 */       finalWidth += CommonUIConstants.DEFAULT_COMPONENT_INSETS.left;
/*  97 */       finalWidth += CommonUIConstants.DEFAULT_COMPONENT_INSETS.right;
/*     */     }
/*     */     else {
/* 100 */       finalWidth = (int)this.defaultSize.getWidth();
/*     */     }
/*     */ 
/* 103 */     if (box.isEditable()) {
/* 104 */       finalWidth += 2;
/*     */     }
/*     */ 
/* 107 */     return new Dimension(finalWidth, (int)this.defaultSize.getHeight());
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI.ResizableComboBoxUI
 * JD-Core Version:    0.6.0
 */