/*     */ package com.dukascopy.dds2.greed.gui.component.table;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.util.LookAndFeelSpecific;
/*     */ import com.dukascopy.dds2.greed.util.PlatformSpecific;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.event.ItemListener;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.event.MouseListener;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.UIManager;
/*     */ import javax.swing.table.JTableHeader;
/*     */ import javax.swing.table.TableCellRenderer;
/*     */ import javax.swing.table.TableColumnModel;
/*     */ 
/*     */ public class CheckBoxHeader extends JCheckBox
/*     */   implements TableCellRenderer, MouseListener, PlatformSpecific, LookAndFeelSpecific
/*     */ {
/*     */   protected CheckBoxHeader rendererComponent;
/*     */   protected int column;
/*  51 */   protected boolean mousePressed = false;
/*     */ 
/*     */   public CheckBoxHeader(ItemListener itemListener, JTable table)
/*     */   {
/*  59 */     this.rendererComponent = this;
/*  60 */     this.rendererComponent.addItemListener(itemListener);
/*  61 */     if (MACOSX) {
/*  62 */       this.rendererComponent.putClientProperty("JComponent.sizeVariant", "small");
/*     */     }
/*     */ 
/*  65 */     JTableHeader header = table.getTableHeader();
/*  66 */     if (header != null)
/*  67 */       header.addMouseListener(this.rendererComponent);
/*     */   }
/*     */ 
/*     */   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
/*     */   {
/*  77 */     if (table != null) {
/*  78 */       JTableHeader header = table.getTableHeader();
/*  79 */       if (header != null)
/*     */       {
/*  81 */         this.rendererComponent.setFont(UIManager.getFont("TableHeader.font"));
/*  82 */         this.rendererComponent.setForeground(UIManager.getColor("TableHeader.foreground"));
/*     */ 
/*  84 */         if (WINDOWS_XP) {
/*  85 */           this.rendererComponent.setBackground(HeaderUI.xpBgColor);
/*     */         }
/*  87 */         if ((VISTA) || (WIN7)) {
/*  88 */           this.rendererComponent.setBackground(HeaderUI.vistaBgLightBlue);
/*     */         }
/*  90 */         if (MACOS) {
/*  91 */           this.rendererComponent.setBackground(HeaderUI.macBgColor);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*  96 */     setColumn(column);
/*  97 */     this.rendererComponent.setHorizontalAlignment(0);
/*  98 */     this.rendererComponent.setMaximumSize(new Dimension(150, 10));
/*  99 */     this.rendererComponent.setBorderPaintedFlat(true);
/* 100 */     if (table.getRowCount() == 0) {
/* 101 */       this.rendererComponent.setSelected(false);
/*     */     }
/* 103 */     return this.rendererComponent;
/*     */   }
/*     */ 
/*     */   protected void setColumn(int column) {
/* 107 */     this.column = column;
/*     */   }
/*     */ 
/*     */   public int getColumn() {
/* 111 */     return this.column;
/*     */   }
/*     */ 
/*     */   public void validate()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void revalidate()
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void firePropertyChange(String propertyName, Object oldValue, Object newValue)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue)
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void handleClickEvent(MouseEvent e)
/*     */   {
/* 140 */     if (this.mousePressed) {
/* 141 */       this.mousePressed = false;
/*     */ 
/* 143 */       JTableHeader header = (JTableHeader)(JTableHeader)e.getSource();
/* 144 */       JTable tableView = header.getTable();
/* 145 */       TableColumnModel columnModel = tableView.getColumnModel();
/* 146 */       int viewColumn = columnModel.getColumnIndexAtX(e.getX());
/* 147 */       int column = tableView.convertColumnIndexToModel(viewColumn);
/*     */ 
/* 149 */       if ((viewColumn == this.column) && (e.getClickCount() == 1) && (column != -1))
/*     */       {
/* 151 */         if (tableView.getRowCount() != 0)
/* 152 */           doClick();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void mouseClicked(MouseEvent e) {
/* 158 */     handleClickEvent(e);
/*     */ 
/* 160 */     ((JTableHeader)e.getSource()).repaint();
/*     */   }
/*     */ 
/*     */   public void mousePressed(MouseEvent e) {
/* 164 */     this.mousePressed = true;
/*     */   }
/*     */ 
/*     */   public void mouseReleased(MouseEvent e)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void mouseEntered(MouseEvent e)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void mouseExited(MouseEvent e)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void paintComponent(Graphics g)
/*     */   {
/* 182 */     super.paintComponent(g);
/*     */ 
/* 184 */     if (WINDOWS_XP) {
/* 185 */       HeaderUI.xpHeaderCheckBoxStyle(g, this);
/*     */     }
/* 187 */     if (CLASSIC) {
/* 188 */       HeaderUI.windowsClassicHeaderCheckBoxStyle(g, this);
/*     */     }
/* 190 */     if (METAL) {
/* 191 */       HeaderUI.metalHeaderCheckBoxStyle(g, this);
/*     */     }
/* 193 */     if ((VISTA) || (WIN7)) {
/* 194 */       HeaderUI.vistaHeaderCheckBoxStyle(g, this);
/*     */     }
/* 196 */     if (MACOS)
/* 197 */       HeaderUI.macOsHeaderCheckBoxStyle(g, this);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.table.CheckBoxHeader
 * JD-Core Version:    0.6.0
 */