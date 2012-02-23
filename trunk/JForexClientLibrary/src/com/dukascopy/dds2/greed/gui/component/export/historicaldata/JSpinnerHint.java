/*     */ package com.dukascopy.dds2.greed.gui.component.export.historicaldata;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.gui.l10n.Localizable;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Insets;
/*     */ import java.awt.RenderingHints;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.FocusEvent;
/*     */ import java.beans.PropertyChangeListener;
/*     */ import java.text.DecimalFormat;
/*     */ import java.text.NumberFormat;
/*     */ import java.text.ParseException;
/*     */ import javax.swing.Action;
/*     */ import javax.swing.ActionMap;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JFormattedTextField;
/*     */ import javax.swing.JSpinner;
/*     */ import javax.swing.JSpinner.NumberEditor;
/*     */ import javax.swing.SpinnerModel;
/*     */ import javax.swing.SpinnerNumberModel;
/*     */ import javax.swing.UIManager;
/*     */ import javax.swing.text.DefaultFormatterFactory;
/*     */ import javax.swing.text.NumberFormatter;
/*     */ 
/*     */ public class JSpinnerHint extends JSpinner
/*     */   implements Localizable
/*     */ {
/*  34 */   private String hint = "";
/*  35 */   private String hintKey = "";
/*  36 */   private NumberEditorExtended numberEditorExtended = null;
/*     */ 
/* 206 */   private static final Action DISABLED_ACTION = new DisabledAction(null);
/*     */ 
/*     */   public JSpinnerHint(SpinnerHintNumberModel model, String hintKey)
/*     */   {
/*  39 */     super(model);
/*  40 */     this.hintKey = hintKey;
/*  41 */     LocalizationManager.addLocalizable(this);
/*     */   }
/*     */ 
/*     */   public void localize()
/*     */   {
/*  46 */     this.hint = LocalizationManager.getText(this.hintKey);
/*  47 */     setSize();
/*     */   }
/*     */ 
/*     */   private void setSize() {
/*  51 */     int width = getSpinnerHintWidth(this.hint + getTailString());
/*  52 */     setPreferredSize(new Dimension(width, 22));
/*  53 */     setMinimumSize(new Dimension(width, 22));
/*     */   }
/*     */ 
/*     */   public static int getSpinnerHintWidth(String text) {
/*  57 */     JSpinner sp = new JSpinner();
/*  58 */     Font spinnerFont = sp.getFont();
/*     */ 
/*  60 */     FontMetrics fontMetrics = sp.getFontMetrics(spinnerFont);
/*     */ 
/*  62 */     int width = fontMetrics.stringWidth(text);
/*  63 */     Insets spinnerInsets = sp.getInsets();
/*  64 */     width += spinnerInsets.left + spinnerInsets.right;
/*     */ 
/*  66 */     Dimension dimension = (Dimension)UIManager.get("Spinner.arrowButtonSize");
/*  67 */     int arrowButtonWidth = 0;
/*  68 */     if (dimension != null)
/*  69 */       arrowButtonWidth += dimension.width;
/*     */     else {
/*  71 */       arrowButtonWidth += 25;
/*     */     }
/*  73 */     width += arrowButtonWidth;
/*     */ 
/*  76 */     if (UIManager.getLookAndFeel().getClass().getName().equals("com.apple.laf.AquaLookAndFeel"))
/*     */     {
/*  78 */       width += 4;
/*     */     }
/*     */ 
/*  81 */     return width;
/*     */   }
/*     */ 
/*     */   public static String getTailString() {
/*  85 */     return " 100";
/*     */   }
/*     */ 
/*     */   protected JComponent createEditor(SpinnerModel model)
/*     */   {
/*  90 */     if ((model instanceof SpinnerHintNumberModel)) {
/*  91 */       SpinnerHintNumberModel spinnerHintNumberModel = (SpinnerHintNumberModel)model;
/*  92 */       this.hintKey = spinnerHintNumberModel.getHintKey();
/*  93 */       localize();
/*     */     }
/*     */ 
/*  96 */     setSize();
/*     */ 
/*  98 */     this.numberEditorExtended = new NumberEditorExtended(this, this.hintKey);
/*  99 */     return this.numberEditorExtended;
/*     */   }
/*     */ 
/*     */   public int getTextWidth()
/*     */   {
/* 104 */     JSpinner sp = new JSpinner();
/* 105 */     FontMetrics fontMetrics = getFontMetrics(sp.getFont());
/* 106 */     int width = fontMetrics.stringWidth(this.hint + ": 5555");
/* 107 */     return width;
/*     */   }
/*     */ 
/*     */   private static class JTextFieldHint extends JFormattedTextField
/*     */     implements Localizable
/*     */   {
/* 233 */     private String hint = "";
/* 234 */     private String hintKey = "";
/*     */ 
/*     */     public JTextFieldHint(String hintKey)
/*     */     {
/* 238 */       this.hintKey = hintKey;
/* 239 */       LocalizationManager.addLocalizable(this);
/*     */     }
/*     */ 
/*     */     protected void paintComponent(Graphics g)
/*     */     {
/* 244 */       super.paintComponent(g);
/*     */ 
/* 246 */       JSpinner sp = new JSpinner();
/*     */ 
/* 251 */       FontMetrics fm = g.getFontMetrics(sp.getFont());
/* 252 */       g.setFont(sp.getFont());
/*     */ 
/* 254 */       int fontHeight = fm.getHeight();
/*     */ 
/* 256 */       int offsetY = getHeight() / 2 + fontHeight / 3;
/*     */ 
/* 258 */       if (this.hint != null) {
/* 259 */         Graphics2D graphics = (Graphics2D)g;
/* 260 */         graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/* 261 */         graphics.drawString(this.hint, 2, offsetY);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void localize()
/*     */     {
/* 268 */       this.hint = LocalizationManager.getText(this.hintKey);
/*     */     }
/*     */ 
/*     */     protected void processFocusEvent(FocusEvent e)
/*     */     {
/* 273 */       super.processFocusEvent(e);
/*     */     }
/*     */ 
/*     */     public void commitEdit() throws ParseException
/*     */     {
/* 278 */       super.commitEdit();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class DisabledAction
/*     */     implements Action
/*     */   {
/*     */     public Object getValue(String key)
/*     */     {
/* 209 */       return null;
/*     */     }
/*     */ 
/*     */     public void putValue(String key, Object value) {
/*     */     }
/*     */ 
/*     */     public void setEnabled(boolean b) {
/*     */     }
/*     */ 
/*     */     public boolean isEnabled() {
/* 219 */       return false;
/*     */     }
/*     */ 
/*     */     public void addPropertyChangeListener(PropertyChangeListener l)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void removePropertyChangeListener(PropertyChangeListener l)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void actionPerformed(ActionEvent ae)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class NumberEditorFormatter extends NumberFormatter
/*     */   {
/*     */     private final SpinnerNumberModel model;
/*     */ 
/*     */     NumberEditorFormatter(SpinnerNumberModel model, NumberFormat format)
/*     */     {
/* 177 */       super();
/* 178 */       this.model = model;
/* 179 */       setValueClass(model.getValue().getClass());
/*     */     }
/*     */ 
/*     */     public void setMinimum(Comparable min)
/*     */     {
/* 184 */       this.model.setMinimum(min);
/*     */     }
/*     */ 
/*     */     public Comparable getMinimum()
/*     */     {
/* 189 */       return this.model.getMinimum();
/*     */     }
/*     */ 
/*     */     public void setMaximum(Comparable max)
/*     */     {
/* 194 */       this.model.setMaximum(max);
/*     */     }
/*     */ 
/*     */     public Comparable getMaximum()
/*     */     {
/* 199 */       return this.model.getMaximum();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class NumberEditorExtended extends JSpinner.NumberEditor
/*     */   {
/*     */     public NumberEditorExtended(JSpinner spinner, String hintKey)
/*     */     {
/* 113 */       super();
/*     */ 
/* 115 */       removeAll();
/*     */ 
/* 117 */       JSpinnerHint.JTextFieldHint ftf = new JSpinnerHint.JTextFieldHint(hintKey);
/* 118 */       ftf.setName("Spinner.formattedTextField");
/* 119 */       ftf.setValue(spinner.getValue());
/* 120 */       ftf.addPropertyChangeListener(this);
/* 121 */       ftf.setEditable(false);
/* 122 */       ftf.setInheritsPopupMenu(true);
/*     */ 
/* 124 */       String toolTipText = spinner.getToolTipText();
/* 125 */       if (toolTipText != null) {
/* 126 */         ftf.setToolTipText(toolTipText);
/*     */       }
/*     */ 
/* 129 */       add(ftf);
/*     */ 
/* 136 */       ActionMap ftfMap = ftf.getActionMap();
/*     */ 
/* 138 */       if (ftfMap != null) {
/* 139 */         ftfMap.put("increment", JSpinnerHint.DISABLED_ACTION);
/* 140 */         ftfMap.put("decrement", JSpinnerHint.DISABLED_ACTION);
/*     */       }
/*     */ 
/* 145 */       SpinnerNumberModel model = (SpinnerNumberModel)spinner.getModel();
/* 146 */       NumberFormatter formatter = new JSpinnerHint.NumberEditorFormatter(model, new DecimalFormat("#"));
/* 147 */       DefaultFormatterFactory factory = new DefaultFormatterFactory(formatter);
/* 148 */       JFormattedTextField formattedTextField = getTextField();
/* 149 */       formattedTextField.setEditable(true);
/* 150 */       formattedTextField.setFormatterFactory(factory);
/* 151 */       formattedTextField.setHorizontalAlignment(4);
/*     */       try
/*     */       {
/* 158 */         String maxString = formatter.valueToString(model.getMinimum());
/* 159 */         String minString = formatter.valueToString(model.getMaximum());
/* 160 */         formattedTextField.setColumns(Math.max(maxString.length(), minString.length()));
/*     */       }
/*     */       catch (ParseException e)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.export.historicaldata.JSpinnerHint
 * JD-Core Version:    0.6.0
 */