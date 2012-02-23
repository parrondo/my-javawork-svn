/*     */ package com.dukascopy.dds2.greed.gui.component;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.swing.ComboBoxEditor;
/*     */ import javax.swing.DefaultListCellRenderer;
/*     */ import javax.swing.Icon;
/*     */ import javax.swing.JColorChooser;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JList;
/*     */ 
/*     */ public class JColorComboBox extends JComboBox
/*     */ {
/*  25 */   public static final Color[] COLORS = { Color.BLUE, Color.GREEN, Color.RED, new Color(49, 125, 167), Color.MAGENTA, Color.ORANGE, new Color(181, 63, 57), new Color(80, 36, 143), Color.GRAY, new Color(255, 128, 64), new Color(0, 100, 50), new Color(170, 210, 210), new Color(100, 0, 50), Color.DARK_GRAY, new Color(150, 150, 0), new Color(0, 255, 128), new Color(40, 80, 80), new Color(0, 128, 128), new Color(192, 192, 192), Color.BLACK, Color.WHITE, Color.getHSBColor(0.0F, 0.745F, 0.87F), Color.getHSBColor(0.35F, 0.6F, 0.65F), Color.getHSBColor(0.333F, 0.865F, 0.494F), Color.getHSBColor(0.0F, 0.748F, 0.56F), Color.getHSBColor(0.088F, 0.341F, 0.862F), Color.getHSBColor(0.118F, 0.545F, 0.862F) };
/*     */ 
/*     */   public JColorComboBox()
/*     */   {
/*  57 */     for (Color color : COLORS) {
/*  58 */       addItem(new ColorIcon(color));
/*     */     }
/*  60 */     setRenderer(new ColorCellRenderer());
/*  61 */     setEditor(new ColorCellEditor());
/*  62 */     setMaximumRowCount(COLORS.length);
/*  63 */     setEditable(true);
/*     */   }
/*     */ 
/*     */   public Color getSelectedColor() {
/*  67 */     return ((ColorIcon)getSelectedItem()).getColor();
/*     */   }
/*     */ 
/*     */   public Color getColorAt(int rowIndex) {
/*  71 */     return ((ColorIcon)getItemAt(rowIndex)).getColor();
/*     */   }
/*     */ 
/*     */   public void showColorChooser() {
/*  75 */     ((ColorCellEditor)getEditor()).showColorChooser();
/*     */   }
/*     */ 
/*     */   public void setSelectedColor(Color selectedColor) {
/*  79 */     setSelectedItem(new ColorIcon(selectedColor));
/*     */   }
/*     */ 
/*     */   private class ColorCellRenderer extends DefaultListCellRenderer
/*     */   {
/*     */     public ColorCellRenderer()
/*     */     {
/* 211 */       setIconTextGap(0);
/* 212 */       setHorizontalAlignment(0);
/*     */     }
/*     */ 
/*     */     public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
/*     */     {
/* 223 */       JLabel label = (JLabel)super.getListCellRendererComponent(list, "", index, isSelected, cellHasFocus);
/*     */ 
/* 225 */       if ((value instanceof JColorComboBox.ColorIcon)) {
/* 226 */         JColorComboBox.ColorIcon icon = (JColorComboBox.ColorIcon)value;
/* 227 */         label.setIcon(icon);
/*     */       }
/*     */ 
/* 230 */       return label;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class ColorCellEditor
/*     */     implements ComboBoxEditor
/*     */   {
/*     */     private final JLabel editorComponent;
/*     */     private Color color;
/* 127 */     private List<ActionListener> actionChangedListeners = new ArrayList();
/*     */ 
/*     */     public ColorCellEditor() {
/* 130 */       this.editorComponent = new JLabel("", JColorComboBox.this)
/*     */       {
/*     */       };
/*     */     }
/*     */ 
/*     */     public Component getEditorComponent()
/*     */     {
/* 145 */       return this.editorComponent;
/*     */     }
/*     */ 
/*     */     public void setItem(Object value)
/*     */     {
/* 150 */       if ((value instanceof JColorComboBox.ColorIcon)) {
/* 151 */         JColorComboBox.ColorIcon icon = (JColorComboBox.ColorIcon)value;
/* 152 */         this.color = icon.getColor();
/* 153 */         this.editorComponent.setIcon(icon);
/*     */       }
/*     */     }
/*     */ 
/*     */     public Object getItem()
/*     */     {
/* 159 */       return new JColorComboBox.ColorIcon(JColorComboBox.this, this.color);
/*     */     }
/*     */ 
/*     */     public void selectAll()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void addActionListener(ActionListener listener) {
/* 167 */       this.actionChangedListeners.add(listener);
/*     */     }
/*     */ 
/*     */     public void removeActionListener(ActionListener listener)
/*     */     {
/* 172 */       this.actionChangedListeners.remove(listener);
/*     */     }
/*     */ 
/*     */     private void fireItemEdited(ActionEvent e) {
/* 176 */       ActionListener[] listeners = (ActionListener[])this.actionChangedListeners.toArray(new ActionListener[this.actionChangedListeners.size()]);
/* 177 */       for (ActionListener listener : listeners)
/* 178 */         listener.actionPerformed(e);
/*     */     }
/*     */ 
/*     */     private void showColorChooser()
/*     */     {
/* 183 */       JColorChooser colorChooser = new JColorChooser(this.color);
/* 184 */       JColorChooser.createDialog(JColorComboBox.this, "Output Color", true, colorChooser, new ActionListener(colorChooser)
/*     */       {
/*     */         public void actionPerformed(ActionEvent e)
/*     */         {
/* 189 */           JColorComboBox.ColorCellEditor.access$102(JColorComboBox.ColorCellEditor.this, this.val$colorChooser.getColor());
/* 190 */           JColorComboBox.ColorCellEditor.this.editorComponent.setIcon(new JColorComboBox.ColorIcon(JColorComboBox.this, JColorComboBox.ColorCellEditor.this.color));
/* 191 */           JColorComboBox.ColorCellEditor.this.fireItemEdited(new ActionEvent(JColorComboBox.ColorCellEditor.this, 0, "colorChange"));
/*     */         }
/*     */       }
/*     */       , new ActionListener()
/*     */       {
/*     */         public void actionPerformed(ActionEvent e)
/*     */         {
/*     */         }
/*     */       }).setVisible(true);
/*     */     }
/*     */   }
/*     */ 
/*     */   private class ColorIcon
/*     */     implements Icon
/*     */   {
/*     */     private static final int HEIGHT = 15;
/*     */     private static final int MARGIN = 5;
/*     */     private final Color color;
/*     */ 
/*     */     public ColorIcon(Color color)
/*     */     {
/*  93 */       this.color = color;
/*     */     }
/*     */ 
/*     */     public int getIconHeight()
/*     */     {
/*  98 */       return 15;
/*     */     }
/*     */ 
/*     */     public int getIconWidth()
/*     */     {
/* 103 */       return 10;
/*     */     }
/*     */ 
/*     */     public void paintIcon(Component c, Graphics g, int x, int y)
/*     */     {
/* 108 */       int width = c.getWidth();
/*     */ 
/* 110 */       g.setColor(this.color);
/* 111 */       g.fillRect(5, y + 1, width - 10, 12);
/* 112 */       g.setColor(Color.BLACK);
/* 113 */       g.drawRect(5, y + 1, width - 10, 12);
/*     */     }
/*     */ 
/*     */     public Color getColor() {
/* 117 */       return this.color;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.JColorComboBox
 * JD-Core Version:    0.6.0
 */