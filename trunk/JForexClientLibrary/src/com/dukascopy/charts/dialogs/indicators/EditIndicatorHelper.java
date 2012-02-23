/*     */ package com.dukascopy.charts.dialogs.indicators;
/*     */ 
/*     */ import com.dukascopy.api.indicators.OutputParameterInfo.DrawingStyle;
/*     */ import com.dukascopy.charts.view.drawingstrategies.IndicatorDrawingStrategy;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.GraphicsConfiguration;
/*     */ import java.awt.Image;
/*     */ import java.awt.event.ItemListener;
/*     */ import java.awt.event.MouseWheelEvent;
/*     */ import java.awt.event.MouseWheelListener;
/*     */ import java.awt.image.ImageObserver;
/*     */ import java.awt.image.VolatileImage;
/*     */ import java.util.Arrays;
/*     */ import java.util.EnumMap;
/*     */ import java.util.Map;
/*     */ import javax.swing.DefaultListCellRenderer;
/*     */ import javax.swing.Icon;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JList;
/*     */ import javax.swing.JSpinner;
/*     */ import javax.swing.ListCellRenderer;
/*     */ import javax.swing.SpinnerModel;
/*     */ import javax.swing.SpinnerNumberModel;
/*     */ import javax.swing.event.ChangeListener;
/*     */ 
/*     */ public class EditIndicatorHelper
/*     */ {
/*     */   public static final int ROW_HEIGHT = 30;
/* 236 */   private static final ListCellRenderer DRAWING_STYLE_RENDERER = new DefaultListCellRenderer()
/*     */   {
/* 238 */     final Map<OutputParameterInfo.DrawingStyle, EditIndicatorHelper.LinePatternIcon> ICONS = new EnumMap(OutputParameterInfo.DrawingStyle.class);
/*     */ 
/*     */     public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
/*     */     {
/* 242 */       JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
/* 243 */       label.setText(null);
/* 244 */       label.setIcon(getIcon((OutputParameterInfo.DrawingStyle)value));
/* 245 */       return label;
/*     */     }
/*     */ 
/*     */     private EditIndicatorHelper.LinePatternIcon getIcon(OutputParameterInfo.DrawingStyle drawingStyle) {
/* 249 */       EditIndicatorHelper.LinePatternIcon icon = (EditIndicatorHelper.LinePatternIcon)this.ICONS.get(drawingStyle);
/* 250 */       if (icon == null) {
/* 251 */         icon = new EditIndicatorHelper.LinePatternIcon(drawingStyle)
/*     */         {
/*     */           public int getIconWidth() {
/* 254 */             return EditIndicatorHelper.4.this.getWidth();
/*     */           }
/*     */         };
/*     */       }
/* 259 */       return icon;
/*     */     }
/* 236 */   };
/*     */ 
/*     */   public static JComboBox createDrawingStyleEditor(Object value, ItemListener itemListener)
/*     */   {
/*  53 */     OutputParameterInfo.DrawingStyle drawingStyle = (OutputParameterInfo.DrawingStyle)value;
/*  54 */     OutputParameterInfo.DrawingStyle[] drawingStyles = null;
/*  55 */     if (Arrays.binarySearch(IndicatorOutputsTableModel.LINES, drawingStyle) >= 0) {
/*  56 */       drawingStyles = IndicatorOutputsTableModel.LINES;
/*     */     }
/*  58 */     else if (Arrays.binarySearch(IndicatorOutputsTableModel.LEVELS, drawingStyle) >= 0) {
/*  59 */       drawingStyles = IndicatorOutputsTableModel.LEVELS;
/*     */     }
/*     */ 
/*  62 */     if (drawingStyles != null) {
/*  63 */       return new JComboBox(drawingStyles, drawingStyle, itemListener)
/*     */       {
/*     */       };
/*     */     }
/*     */ 
/*  73 */     return null;
/*     */   }
/*     */ 
/*     */   public static JComboBox createDrawingStyleEditor(Object value)
/*     */   {
/*  81 */     return createDrawingStyleEditor(value, null);
/*     */   }
/*     */ 
/*     */   public static JSpinner createShiftEditor(Object value) {
/*  85 */     return createShiftEditor(value, null);
/*     */   }
/*     */ 
/*     */   private static JSpinner createSpinnerEditor(Object value, ChangeListener changeListener, int minimum, int maximum, int stepSize)
/*     */   {
/*  95 */     return new JSpinner(new SpinnerNumberModel((Integer)value, Integer.valueOf(minimum), Integer.valueOf(maximum), Integer.valueOf(stepSize)), changeListener)
/*     */     {
/*     */     };
/*     */   }
/*     */ 
/*     */   public static JSpinner createShiftEditor(Object value, ChangeListener changeListener)
/*     */   {
/* 121 */     return createSpinnerEditor(value, changeListener, -100, 100, 1);
/*     */   }
/*     */ 
/*     */   public static JSpinner createWidthEditor(Object value, ChangeListener changeListener)
/*     */   {
/* 131 */     return createSpinnerEditor(value, changeListener, 1, 5, 1);
/*     */   }
/*     */ 
/*     */   public static JSpinner createWidthEditor(Object value)
/*     */   {
/* 138 */     return createWidthEditor(value, null);
/*     */   }
/*     */ 
/*     */   public static JComboBox createTransparencyEditor(Object value, ItemListener itemListener)
/*     */   {
/* 149 */     Float alpha = (Float)value;
/* 150 */     Float[] alphaValues = new Float[10];
/* 151 */     for (int i = 1; i <= 10; i++) {
/* 152 */       alphaValues[(10 - i)] = Float.valueOf(i / 10.0F);
/*     */     }
/*     */ 
/* 155 */     return new JComboBox(alphaValues, alpha, itemListener)
/*     */     {
/*     */     };
/*     */   }
/*     */ 
/*     */   public static JComboBox createTransparencyEditor(Object value)
/*     */   {
/* 187 */     return createTransparencyEditor(value, null);
/*     */   }
/*     */ 
/*     */   private static abstract class LinePatternIcon
/*     */     implements Icon, ImageObserver
/*     */   {
/*     */     private static final int DEFAULT_HEIGHT = 10;
/*     */     private OutputParameterInfo.DrawingStyle drawingStyle;
/*     */ 
/*     */     public LinePatternIcon(OutputParameterInfo.DrawingStyle drawingStyle)
/*     */     {
/* 200 */       this.drawingStyle = drawingStyle;
/*     */     }
/*     */ 
/*     */     public int getIconHeight()
/*     */     {
/* 205 */       return 10;
/*     */     }
/*     */ 
/*     */     public void paintIcon(Component c, Graphics g, int x, int y)
/*     */     {
/* 210 */       Graphics2D g2d = (Graphics2D)g;
/*     */ 
/* 212 */       VolatileImage icon = g2d.getDeviceConfiguration().createCompatibleVolatileImage(getIconWidth(), getIconHeight(), 2);
/*     */ 
/* 214 */       Graphics2D iconGraphics = (Graphics2D)icon.getGraphics();
/*     */ 
/* 216 */       iconGraphics.setBackground(new Color(255, 255, 255, 0));
/* 217 */       iconGraphics.clearRect(0, 0, getIconWidth(), 10);
/*     */ 
/* 219 */       iconGraphics.setStroke(IndicatorDrawingStrategy.getStroke(this.drawingStyle, 1));
/* 220 */       iconGraphics.setColor(Color.BLACK);
/* 221 */       iconGraphics.drawLine(5, 4, getIconWidth() - 10, 4);
/* 222 */       iconGraphics.drawLine(5, 5, getIconWidth() - 10, 5);
/* 223 */       iconGraphics.drawLine(5, 6, getIconWidth() - 10, 6);
/*     */ 
/* 225 */       icon.flush();
/*     */ 
/* 227 */       g2d.drawImage(icon, x, y, this);
/*     */     }
/*     */ 
/*     */     public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height)
/*     */     {
/* 232 */       return false;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.dialogs.indicators.EditIndicatorHelper
 * JD-Core Version:    0.6.0
 */