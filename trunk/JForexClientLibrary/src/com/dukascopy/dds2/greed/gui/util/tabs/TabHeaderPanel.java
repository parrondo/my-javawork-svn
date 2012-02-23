/*     */ package com.dukascopy.dds2.greed.gui.util.tabs;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*     */ import java.awt.AlphaComposite;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.Cursor;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.GradientPaint;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Image;
/*     */ import java.awt.LayoutManager;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.util.EnumMap;
/*     */ import java.util.Map;
/*     */ import javax.swing.ImageIcon;
/*     */ import javax.swing.JPanel;
/*     */ 
/*     */ public class TabHeaderPanel extends JPanel
/*     */ {
/*  28 */   private static final Dimension SIZE = new Dimension(80, 56);
/*     */   private static final int ICON_WIDTH = 40;
/*  37 */   public static final Map<State, Color[]> COLORS = new EnumMap() { } ;
/*     */   private final TabbedPanel tabbedPanel;
/*     */   private final JPanel content;
/*     */   private final ImageIcon icon;
/*  62 */   private boolean selected = false;
/*  63 */   private boolean mouseOver = false;
/*     */ 
/*     */   public TabHeaderPanel(TabbedPanel tabbedPanel, JPanel content, ImageIcon icon, String title, String toolTip) {
/*  66 */     this.tabbedPanel = tabbedPanel;
/*  67 */     this.content = content;
/*  68 */     this.icon = icon;
/*     */ 
/*  70 */     setLayout(new TabHeaderPanelLayout(null));
/*  71 */     setPreferredSize(SIZE);
/*  72 */     setSize(SIZE);
/*     */ 
/*  74 */     add(new JLocalizableLabel(title, toolTip, 0)
/*     */     {
/*     */     });
/*  76 */     addMouseListener(new MouseAdapter()
/*     */     {
/*     */       public void mousePressed(MouseEvent e) {
/*  79 */         TabHeaderPanel.this.tabbedPanel.getHeadersPanel().deselectHeaders();
/*  80 */         TabHeaderPanel.this.setSelected(true);
/*     */       }
/*     */ 
/*     */       public void mouseEntered(MouseEvent e)
/*     */       {
/*  85 */         TabHeaderPanel.this.setMouseOver(true);
/*  86 */         TabHeaderPanel.this.setCursor(Cursor.getPredefinedCursor(12));
/*     */       }
/*     */ 
/*     */       public void mouseExited(MouseEvent e)
/*     */       {
/*  91 */         TabHeaderPanel.this.setMouseOver(false);
/*  92 */         TabHeaderPanel.this.setCursor(Cursor.getDefaultCursor());
/*     */       } } );
/*     */   }
/*     */ 
/*     */   public boolean isSelected() {
/*  98 */     return this.selected;
/*     */   }
/*     */ 
/*     */   public void setSelected(boolean selected) {
/* 102 */     this.selected = selected;
/*     */ 
/* 104 */     if (selected) {
/* 105 */       this.tabbedPanel.setContent(this.content);
/*     */     }
/*     */ 
/* 108 */     repaint();
/*     */   }
/*     */ 
/*     */   protected void paintComponent(Graphics g)
/*     */   {
/* 113 */     super.paintComponent(g);
/* 114 */     Graphics2D g2D = (Graphics2D)g;
/* 115 */     drawBackground(g2D);
/* 116 */     g2D.setComposite(AlphaComposite.getInstance(3, 1.0F));
/* 117 */     Image iconImage = this.icon.getImage();
/* 118 */     g2D.drawImage(iconImage, getWidth() / 2 - 20, 0, null);
/*     */   }
/*     */ 
/*     */   private void drawBackground(Graphics2D graphics2D) {
/* 122 */     GradientPaint gradientPaint = null;
/*     */ 
/* 124 */     if (this.selected) {
/* 125 */       gradientPaint = getGradient(State.SELECTED);
/*     */     }
/* 127 */     else if (this.mouseOver)
/* 128 */       gradientPaint = getGradient(State.MOUSE_OVER);
/*     */     else {
/* 130 */       gradientPaint = getGradient(State.DEFAULT);
/*     */     }
/*     */ 
/* 134 */     graphics2D.setPaint(gradientPaint);
/* 135 */     graphics2D.fillRect(0, 0, getWidth(), getHeight());
/*     */   }
/*     */ 
/*     */   private GradientPaint getGradient(State state) {
/* 139 */     boolean isSelected = state == State.SELECTED;
/* 140 */     Color[] colors = (Color[])COLORS.get(state);
/*     */ 
/* 142 */     float x = getWidth() / 2;
/* 143 */     float y = getHeight();
/*     */ 
/* 145 */     return new GradientPaint(x, 0.0F, colors[0], x, y, colors[1], isSelected);
/*     */   }
/*     */ 
/*     */   private void setMouseOver(boolean value)
/*     */   {
/* 153 */     this.mouseOver = value;
/*     */ 
/* 155 */     setCursor(value ? Cursor.getPredefinedCursor(12) : Cursor.getDefaultCursor());
/*     */ 
/* 161 */     repaint();
/*     */   }
/*     */ 
/*     */   private static final class TabHeaderPanelLayout implements LayoutManager
/*     */   {
/*     */     public void addLayoutComponent(String name, Component comp)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void removeLayoutComponent(Component comp)
/*     */     {
/*     */     }
/*     */ 
/*     */     public Dimension preferredLayoutSize(Container parent) {
/* 175 */       return TabHeaderPanel.SIZE;
/*     */     }
/*     */ 
/*     */     public Dimension minimumLayoutSize(Container parent) {
/* 179 */       return TabHeaderPanel.SIZE;
/*     */     }
/*     */ 
/*     */     public void layoutContainer(Container parent) {
/* 183 */       int count = parent.getComponentCount();
/* 184 */       for (int i = 0; i < count; i++) {
/* 185 */         Component component = parent.getComponent(i);
/* 186 */         if (!component.isVisible()) {
/*     */           continue;
/*     */         }
/* 189 */         if ("description".equalsIgnoreCase(component.getName())) {
/* 190 */           Dimension dimension = component.getPreferredSize();
/* 191 */           component.setBounds((int)(TabHeaderPanel.SIZE.getWidth() / 2.0D - dimension.getWidth() / 2.0D), (int)(TabHeaderPanel.SIZE.getHeight() - dimension.getHeight() - 1.0D), (int)dimension.getWidth(), (int)dimension.getHeight());
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static enum State
/*     */   {
/*  32 */     DEFAULT, 
/*  33 */     MOUSE_OVER, 
/*  34 */     SELECTED;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.util.tabs.TabHeaderPanel
 * JD-Core Version:    0.6.0
 */