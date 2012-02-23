/*     */ package com.dukascopy.charts.dialogs.indicators;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
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
/*     */ public class ColorJComboBox extends JComboBox
/*     */ {
/*  26 */   public static final Color BORDO = new Color(181, 63, 57);
/*  27 */   public static final Color FIOLET = new Color(80, 36, 143);
/*  28 */   public static final Color DARKBLUE = new Color(49, 125, 167);
/*  29 */   public static final Color YELLOWORANGE = new Color(255, 128, 64);
/*  30 */   public static final Color ORANGE = new Color(200, 160, 0);
/*  31 */   public static final Color BRIGHTBLUE = new Color(170, 210, 210);
/*  32 */   public static final Color BLUE2 = new Color(40, 80, 80);
/*  33 */   public static final Color RED2 = new Color(100, 0, 50);
/*  34 */   public static final Color GREEN2 = new Color(0, 100, 50);
/*  35 */   public static final Color YELLOW2 = new Color(150, 150, 0);
/*  36 */   public static final Color COL3 = new Color(0, 255, 128);
/*  37 */   public static final Color COL4 = new Color(0, 128, 128);
/*  38 */   public static final Color COL5 = new Color(192, 192, 192);
/*  39 */   public static final Color COL6 = new Color(255, 255, 100);
/*  40 */   public static final Color BLUE3 = new Color(0, 64, 64);
/*  41 */   public static final Color RED = Color.getHSBColor(0.0F, 0.745F, 0.87F);
/*  42 */   public static final Color GREEN = Color.getHSBColor(0.35F, 0.6F, 0.65F);
/*  43 */   public static final Color DARK_GREEN = Color.getHSBColor(0.333F, 0.865F, 0.494F);
/*  44 */   public static final Color DARK_RED = Color.getHSBColor(0.0F, 0.748F, 0.56F);
/*  45 */   public static final Color labelFG = Color.getHSBColor(0.088F, 0.341F, 0.862F);
/*  46 */   public static final Color labelBack = Color.getHSBColor(0.118F, 0.545F, 0.862F);
/*     */ 
/*  48 */   public static final Color[] colArr = { Color.BLUE, Color.GREEN, Color.RED, DARKBLUE, Color.MAGENTA, Color.ORANGE, BORDO, FIOLET, Color.GRAY, YELLOWORANGE, GREEN2, BRIGHTBLUE, RED2, Color.DARK_GRAY, YELLOW2, COL3, BLUE2, COL4, COL5, Color.BLACK, Color.WHITE, RED, GREEN, DARK_GREEN, DARK_RED, labelFG, labelBack };
/*     */ 
/*  82 */   private static final Object[][] comboStyles = new Object[colArr.length][];
/*     */ 
/*     */   public ColorJComboBox()
/*     */   {
/*  90 */     super(comboStyles);
/*  91 */     setRenderer(new ColorCellRenderer());
/*  92 */     setEditor(new ColorCellEditor());
/*  93 */     setMaximumRowCount(19);
/*  94 */     setEditable(true);
/*     */   }
/*     */ 
/*     */   public Color getSelectedColor() {
/*  98 */     return (Color)((Object[])(Object[])getSelectedItem())[1];
/*     */   }
/*     */ 
/*     */   public Color getColorAt(int k) {
/* 102 */     return (Color)((Object[])(Object[])getItemAt(k))[1];
/*     */   }
/*     */ 
/*     */   public void showColorChooser() {
/* 106 */     ((ColorCellEditor)getEditor()).showColorChooser();
/*     */   }
/*     */ 
/*     */   public Dimension getMaximumSize()
/*     */   {
/* 111 */     Dimension size = super.getMaximumSize();
/* 112 */     Dimension psize = super.getPreferredSize();
/* 113 */     size.width = 100;
/* 114 */     size.height = psize.height;
/* 115 */     return size;
/*     */   }
/*     */ 
/*     */   public Dimension getMinimumSize() {
/* 119 */     Dimension size = super.getMinimumSize();
/* 120 */     Dimension psize = super.getPreferredSize();
/* 121 */     size.width = 100;
/* 122 */     size.height = psize.height;
/* 123 */     return size;
/*     */   }
/*     */ 
/*     */   public Dimension getPreferredSize() {
/* 127 */     Dimension size = super.getPreferredSize();
/* 128 */     size.width = 100;
/* 129 */     return size;
/*     */   }
/*     */ 
/*     */   public void setSelectedColor(Color selectedColor) {
/* 133 */     setSelectedItem(new Object[] { new ColorIcon(selectedColor), selectedColor });
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  83 */     int i = 0;
/*  84 */     for (Color c : colArr)
/*  85 */       comboStyles[(i++)] = { new ColorIcon(c), c };
/*     */   }
/*     */ 
/*     */   public static class ColorCellRenderer extends DefaultListCellRenderer
/*     */   {
/* 245 */     protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
/*     */ 
/*     */     public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
/*     */     {
/* 250 */       JLabel renderer = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
/* 251 */       ColorJComboBox.ColorIcon theIcon = null;
/*     */ 
/* 253 */       if ((value instanceof Object[])) {
/* 254 */         Object[] values = (Object[])(Object[])value;
/* 255 */         theIcon = (ColorJComboBox.ColorIcon)values[0];
/*     */       }
/* 257 */       if (theIcon != null) {
/* 258 */         renderer.setIcon(theIcon);
/*     */       }
/* 260 */       renderer.setText("");
/* 261 */       return renderer;
/*     */     }
/*     */   }
/*     */ 
/*     */   public class ColorCellEditor
/*     */     implements ComboBoxEditor
/*     */   {
/* 163 */     private JLabel editorComponent = new JLabel();
/*     */     private Color color;
/* 165 */     private List<ActionListener> actionChangedListeners = new ArrayList();
/*     */ 
/*     */     public ColorCellEditor() {
/* 168 */       this.editorComponent = new JLabel();
/* 169 */       this.editorComponent.addMouseListener(new MouseAdapter(ColorJComboBox.this)
/*     */       {
/*     */         public void mouseClicked(MouseEvent e) {
/* 172 */           if ((e.getButton() == 1) && (e.getClickCount() == 2))
/* 173 */             ColorJComboBox.ColorCellEditor.this.showColorChooser();
/*     */         }
/*     */       });
/*     */     }
/*     */ 
/*     */     public Component getEditorComponent()
/*     */     {
/* 181 */       return this.editorComponent;
/*     */     }
/*     */ 
/*     */     public void setItem(Object value)
/*     */     {
/* 186 */       ColorJComboBox.ColorIcon theIcon = null;
/*     */ 
/* 188 */       if ((value instanceof Object[])) {
/* 189 */         Object[] values = (Object[])(Object[])value;
/* 190 */         theIcon = (ColorJComboBox.ColorIcon)values[0];
/* 191 */         this.color = ((Color)values[1]);
/*     */       }
/* 193 */       if (theIcon != null) {
/* 194 */         this.editorComponent.setIcon(theIcon);
/*     */       }
/* 196 */       this.editorComponent.setText("");
/*     */     }
/*     */ 
/*     */     public Object getItem()
/*     */     {
/* 201 */       return new Object[] { new ColorJComboBox.ColorIcon(this.color), this.color };
/*     */     }
/*     */ 
/*     */     public void selectAll()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void addActionListener(ActionListener listener)
/*     */     {
/* 210 */       this.actionChangedListeners.add(listener);
/*     */     }
/*     */ 
/*     */     public void removeActionListener(ActionListener listener)
/*     */     {
/* 215 */       this.actionChangedListeners.remove(listener);
/*     */     }
/*     */ 
/*     */     private void fireItemEdited(ActionEvent e) {
/* 219 */       ActionListener[] listeners = (ActionListener[])this.actionChangedListeners.toArray(new ActionListener[this.actionChangedListeners.size()]);
/* 220 */       for (ActionListener listener : listeners)
/* 221 */         listener.actionPerformed(e);
/*     */     }
/*     */ 
/*     */     private void showColorChooser()
/*     */     {
/* 226 */       JColorChooser colorChooser = new JColorChooser(this.color);
/* 227 */       JDialog colorChooserDialog = JColorChooser.createDialog(ColorJComboBox.this, "Output Color", false, colorChooser, new ActionListener(colorChooser)
/*     */       {
/*     */         public void actionPerformed(ActionEvent e) {
/* 230 */           ColorJComboBox.ColorCellEditor.access$102(ColorJComboBox.ColorCellEditor.this, this.val$colorChooser.getColor());
/* 231 */           ColorJComboBox.ColorCellEditor.this.editorComponent.setIcon(new ColorJComboBox.ColorIcon(ColorJComboBox.ColorCellEditor.this.color));
/* 232 */           ColorJComboBox.ColorCellEditor.this.fireItemEdited(new ActionEvent(ColorJComboBox.ColorCellEditor.this, 0, "colorChange"));
/*     */         }
/*     */       }
/*     */       , new ActionListener()
/*     */       {
/*     */         public void actionPerformed(ActionEvent e)
/*     */         {
/*     */         }
/*     */       });
/* 240 */       colorChooserDialog.setVisible(true);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class ColorIcon
/*     */     implements Icon
/*     */   {
/*     */     private Color color;
/* 138 */     private int width = 80;
/* 139 */     private int height = 15;
/*     */ 
/*     */     public ColorIcon(Color color) {
/* 142 */       this.color = color;
/*     */     }
/*     */ 
/*     */     public int getIconHeight() {
/* 146 */       return this.height;
/*     */     }
/*     */ 
/*     */     public int getIconWidth() {
/* 150 */       return this.width;
/*     */     }
/*     */ 
/*     */     public void paintIcon(Component c, Graphics g, int x, int y) {
/* 154 */       g.setColor(this.color);
/* 155 */       g.fillRect(x + 5, y + 2, this.width - 12, this.height - 4);
/* 156 */       g.setColor(Color.BLACK);
/* 157 */       g.drawRect(x + 4, y + 1, this.width - 11, this.height - 3);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.dialogs.indicators.ColorJComboBox
 * JD-Core Version:    0.6.0
 */