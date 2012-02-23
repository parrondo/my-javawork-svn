/*     */ package com.dukascopy.dds2.greed.gui.util.spinners;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.util.GridBagLayoutHelper;
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.math.BigDecimal;
/*     */ import java.text.ParseException;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import javax.swing.AbstractSpinnerModel;
/*     */ import javax.swing.InputVerifier;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JFormattedTextField;
/*     */ import javax.swing.JFormattedTextField.AbstractFormatter;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JSpinner;
/*     */ import javax.swing.SpinnerModel;
/*     */ import javax.swing.Timer;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.ChangeListener;
/*     */ import javax.swing.event.DocumentEvent;
/*     */ import javax.swing.event.DocumentListener;
/*     */ import javax.swing.text.AttributeSet;
/*     */ import javax.swing.text.BadLocationException;
/*     */ import javax.swing.text.Document;
/*     */ import javax.swing.text.PlainDocument;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class CommonJSpinner extends JPanel
/*     */   implements ActionListener
/*     */ {
/*  49 */   private static final Logger LOGGER = LoggerFactory.getLogger(CommonJSpinner.class);
/*     */ 
/*  51 */   private boolean ignoreUpdateFromSpinner = false;
/*     */   private Timer timer;
/*     */   protected JSpinner spinner;
/*     */   private JLabel errorLabel;
/*     */ 
/*     */   public CommonJSpinner(double value, double maximum, double stepSize, int precision, boolean round05Pip)
/*     */   {
/*  57 */     this(value, 0.0D, maximum, stepSize, precision, round05Pip, true);
/*     */   }
/*     */ 
/*     */   public CommonJSpinner(double value, double minimum, double maximum, double stepSize, int precision, boolean round05Pip, boolean isAmount) {
/*  61 */     super(new GridBagLayout());
/*     */ 
/*  63 */     this.spinner = new JSpinner(new AmountSpinnerModel(value, minimum, maximum, stepSize, precision, round05Pip, null), round05Pip, precision)
/*     */     {
/*     */       public void setEnabled(boolean enabled)
/*     */       {
/*  67 */         super.setEnabled(enabled);
/*  68 */         getEditor().setEnabled(enabled);
/*     */       }
/*     */ 
/*     */       protected JComponent createEditor(SpinnerModel model)
/*     */       {
/*  73 */         model.addChangeListener(new ChangeListener(model)
/*     */         {
/*     */           public void stateChanged(ChangeEvent e) {
/*  76 */             if (!CommonJSpinner.this.ignoreUpdateFromSpinner) {
/*  77 */               BigDecimal value = (BigDecimal)this.val$model.getValue();
/*  78 */               ((JFormattedTextField)CommonJSpinner.1.this.getEditor()).setValue(value);
/*     */             }
/*     */           }
/*     */         });
/*  82 */         JFormattedTextField editor = new JFormattedTextField(new JFormattedTextField.AbstractFormatter(model)
/*     */         {
/*     */           public Object stringToValue(String text) throws ParseException {
/*     */             try {
/*  86 */               BigDecimal value = new BigDecimal(text);
/*  87 */               if (((CommonJSpinner.AmountSpinnerModel)this.val$model).round(value).compareTo(value) != 0) {
/*  88 */                 throw new ParseException("Incorrect value", 0);
/*     */               }
/*  90 */               CommonJSpinner.AmountSpinnerModel model = (CommonJSpinner.AmountSpinnerModel)CommonJSpinner.1.this.getModel();
/*  91 */               model.setValue(value);
/*  92 */               return model.getValue(); } catch (IllegalArgumentException e) {
/*     */             }
/*  94 */             throw new ParseException(e.getMessage(), 0);
/*     */           }
/*     */ 
/*     */           public String valueToString(Object value)
/*     */             throws ParseException
/*     */           {
/* 100 */             if (value == null)
/* 101 */               return "";
/* 102 */             if ((value instanceof BigDecimal)) {
/* 103 */               return ((BigDecimal)value).toPlainString();
/*     */             }
/* 105 */             return value.toString();
/*     */           }
/*     */         })
/*     */         {
/*     */           protected Document createDefaultModel()
/*     */           {
/* 111 */             return new CommonJSpinner.AmountDocument(CommonJSpinner.this, this, CommonJSpinner.1.this.val$round05Pip ? CommonJSpinner.1.this.val$precision + 1 : CommonJSpinner.1.this.val$precision);
/*     */           }
/*     */         };
/* 114 */         editor.setText(((BigDecimal)getModel().getValue()).toPlainString());
/* 115 */         editor.getDocument().addDocumentListener(new DocumentListener(editor)
/*     */         {
/*     */           public void insertUpdate(DocumentEvent e) {
/* 118 */             CommonJSpinner.access$102(CommonJSpinner.this, true);
/*     */             try {
/* 120 */               this.val$editor.commitEdit();
/*     */             } catch (ParseException e1) {
/*     */             }
/*     */             finally {
/* 124 */               CommonJSpinner.access$102(CommonJSpinner.this, false);
/*     */             }
/*     */           }
/*     */ 
/*     */           public void removeUpdate(DocumentEvent e)
/*     */           {
/* 130 */             CommonJSpinner.access$102(CommonJSpinner.this, true);
/*     */             try {
/* 132 */               this.val$editor.commitEdit();
/*     */             } catch (ParseException e1) {
/*     */             }
/*     */             finally {
/* 136 */               CommonJSpinner.access$102(CommonJSpinner.this, false);
/*     */             }
/*     */           }
/*     */ 
/*     */           public void changedUpdate(DocumentEvent e)
/*     */           {
/* 142 */             CommonJSpinner.access$102(CommonJSpinner.this, true);
/*     */             try {
/* 144 */               this.val$editor.commitEdit();
/*     */             } catch (ParseException e1) {
/*     */             }
/*     */             finally {
/* 148 */               CommonJSpinner.access$102(CommonJSpinner.this, false);
/*     */             }
/*     */           }
/*     */         });
/* 152 */         editor.setInputVerifier(new InputVerifier() {
/*     */           public boolean verify(JComponent input) {
/* 154 */             if ((input instanceof JFormattedTextField)) {
/* 155 */               JFormattedTextField ftf = (JFormattedTextField)input;
/* 156 */               JFormattedTextField.AbstractFormatter formatter = ftf.getFormatter();
/* 157 */               if (formatter != null) {
/* 158 */                 String text = ftf.getText();
/*     */                 try {
/* 160 */                   formatter.stringToValue(text);
/* 161 */                   return true;
/*     */                 } catch (ParseException pe) {
/* 163 */                   return false;
/*     */                 }
/*     */               }
/*     */             }
/* 167 */             return true;
/*     */           }
/*     */         });
/* 170 */         editor.setFocusLostBehavior(3);
/* 171 */         return editor;
/*     */       }
/*     */ 
/*     */       public Object getValue()
/*     */       {
/*     */         try {
/* 177 */           ((JFormattedTextField)getEditor()).commitEdit();
/* 178 */           return super.getValue(); } catch (ParseException e) {
/*     */         }
/* 180 */         return null;
/*     */       }
/*     */     };
/* 187 */     this.errorLabel = new JLabel()
/*     */     {
/*     */       public Dimension getPreferredSize() {
/* 190 */         return CommonJSpinner.this.spinner.getPreferredSize();
/*     */       }
/*     */     };
/* 194 */     if (isAmount) {
/* 195 */       AmountSpinnerModel model = (AmountSpinnerModel)this.spinner.getModel();
/* 196 */       if (GreedContext.isMiniFxAccount()) {
/* 197 */         model.setPrecision(6);
/* 198 */         ((AmountDocument)((JFormattedTextField)this.spinner.getEditor()).getDocument()).setPrecision(6);
/*     */       } else {
/* 200 */         model.setPrecision(2);
/* 201 */         ((AmountDocument)((JFormattedTextField)this.spinner.getEditor()).getDocument()).setPrecision(2);
/*     */       }
/*     */     }
/*     */ 
/* 205 */     this.errorLabel.setVisible(false);
/* 206 */     this.errorLabel.setForeground(Color.RED);
/* 207 */     GridBagConstraints gbc = new GridBagConstraints();
/* 208 */     GridBagLayoutHelper.add(0, 0, 1.0D, 1.0D, 1, 1, 0, 0, 0, 0, 1, 10, gbc, this, this.errorLabel);
/* 209 */     GridBagLayoutHelper.add(0, 0, 1.0D, 1.0D, 1, 1, 0, 0, 0, 0, 1, 10, gbc, this, this.spinner);
/*     */   }
/*     */ 
/*     */   public void addChangeListener(ChangeListener listener)
/*     */   {
/* 215 */     this.spinner.getModel().addChangeListener(listener);
/*     */   }
/*     */ 
/*     */   public void removeChangeListener(ChangeListener listener) {
/* 219 */     this.spinner.getModel().removeChangeListener(listener);
/*     */   }
/*     */ 
/*     */   public void setHorizontalAlignment(int alligment) {
/* 223 */     ((JFormattedTextField)this.spinner.getEditor()).setHorizontalAlignment(alligment);
/*     */   }
/*     */ 
/*     */   public void setMinimum(BigDecimal minimum) {
/* 227 */     AmountSpinnerModel model = (AmountSpinnerModel)this.spinner.getModel();
/* 228 */     model.setMinimum(minimum);
/*     */   }
/*     */ 
/*     */   public BigDecimal getMinimum() {
/* 232 */     AmountSpinnerModel model = (AmountSpinnerModel)this.spinner.getModel();
/* 233 */     return model.getMinimum().stripTrailingZeros();
/*     */   }
/*     */ 
/*     */   public void setMaximum(BigDecimal maximum) {
/* 237 */     AmountSpinnerModel model = (AmountSpinnerModel)this.spinner.getModel();
/* 238 */     model.setMaximum(maximum);
/*     */   }
/*     */ 
/*     */   public BigDecimal getMaximum() {
/* 242 */     AmountSpinnerModel model = (AmountSpinnerModel)this.spinner.getModel();
/* 243 */     return model.getMaximum();
/*     */   }
/*     */ 
/*     */   public void setStepSize(BigDecimal stepSize) {
/* 247 */     AmountSpinnerModel model = (AmountSpinnerModel)this.spinner.getModel();
/* 248 */     model.setStepSize(stepSize);
/*     */   }
/*     */ 
/*     */   public BigDecimal getStepSize() {
/* 252 */     AmountSpinnerModel model = (AmountSpinnerModel)this.spinner.getModel();
/* 253 */     return model.getStepSize();
/*     */   }
/*     */ 
/*     */   public void setEnabled(boolean enabled)
/*     */   {
/* 258 */     super.setEnabled(enabled);
/* 259 */     this.spinner.setEnabled(enabled);
/*     */   }
/*     */ 
/*     */   public void clear() {
/* 263 */     ((JFormattedTextField)this.spinner.getEditor()).setText("");
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent e)
/*     */   {
/* 268 */     this.errorLabel.setVisible(false);
/* 269 */     this.spinner.setVisible(true);
/* 270 */     this.spinner.getEditor().requestFocusInWindow();
/* 271 */     this.timer = null;
/*     */   }
/*     */ 
/*     */   public boolean validateEditor() {
/*     */     try {
/* 276 */       ((JFormattedTextField)this.spinner.getEditor()).commitEdit();
/* 277 */       return true; } catch (ParseException e) { JFormattedTextField editor = (JFormattedTextField)this.spinner.getEditor();
/*     */       String errorText;
/*     */       try {
/* 282 */         BigDecimal value = new BigDecimal(editor.getText());
/* 283 */         AmountSpinnerModel model = (AmountSpinnerModel)this.spinner.getModel();
/* 284 */         BigDecimal roundedValue = model.round(value);
/*     */         String errorText;
/* 285 */         if ((roundedValue.compareTo(value) != 0) && (model.round05Pip)) {
/* 286 */           errorText = LocalizationManager.getTextWithArguments("round.05.pip.error", new Object[] { model.getMaximum().toPlainString() });
/*     */         }
/*     */         else
/*     */         {
/*     */           String errorText;
/* 287 */           if (value.compareTo(model.getMaximum()) > 0) {
/* 288 */             errorText = LocalizationManager.getTextWithArguments("maximum.number", new Object[] { model.getMaximum().toPlainString() });
/*     */           }
/*     */           else
/*     */           {
/*     */             String errorText;
/* 289 */             if (value.compareTo(model.getMinimum()) < 0) {
/* 290 */               errorText = LocalizationManager.getTextWithArguments("minimum.number", new Object[] { model.getMinimum().toPlainString() });
/*     */             }
/*     */             else
/*     */             {
/*     */               String errorText;
/* 291 */               if (value.abs().toString().length() - 1 != model.precision) {
/* 292 */                 errorText = LocalizationManager.getText("precision.to.large");
/*     */               } else {
/* 294 */                 LOGGER.error("Valid text could not be validated");
/* 295 */                 return false;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       } catch (NumberFormatException ex) {
/* 298 */         errorText = LocalizationManager.getText("not.a.number");
/*     */       }
/* 300 */       this.errorLabel.setText(errorText);
/* 301 */       this.spinner.setVisible(false);
/* 302 */       this.errorLabel.setVisible(true);
/* 303 */       if ((this.timer == null) || (!this.timer.isRunning())) {
/* 304 */         this.timer = new Timer(2000, this);
/* 305 */         this.timer.setRepeats(false);
/* 306 */         this.timer.start();
/*     */       } }
/* 308 */     return false;
/*     */   }
/*     */ 
/*     */   public void setPrecision(BigDecimal precision)
/*     */   {
/* 313 */     AmountSpinnerModel model = (AmountSpinnerModel)this.spinner.getModel();
/* 314 */     model.setPrecision(precision.intValue());
/*     */ 
/* 316 */     ((AmountDocument)((JFormattedTextField)this.spinner.getEditor()).getDocument()).setPrecision(precision.intValue());
/*     */   }
/*     */ 
/*     */   public void setValue(Object value)
/*     */   {
/* 322 */     this.spinner.getModel().setValue(value);
/*     */   }
/*     */ 
/*     */   public Object getValue() {
/* 326 */     return this.spinner.getModel().getValue();
/*     */   }
/*     */ 
/*     */   private class AmountDocument extends PlainDocument
/*     */   {
/*     */     private Pattern pattern;
/*     */     private JFormattedTextField originator;
/* 442 */     private int length = 2147483647;
/* 443 */     private int precision = -2147483648;
/*     */ 
/*     */     public AmountDocument(JFormattedTextField originator, int precision)
/*     */     {
/* 447 */       this.originator = originator;
/* 448 */       setPrecision(precision);
/*     */     }
/*     */ 
/*     */     public void setPrecision(int aPrecision) {
/* 452 */       if (this.precision != aPrecision)
/*     */       {
/* 454 */         this.precision = aPrecision;
/*     */         String pattern;
/* 456 */         if (0 == this.precision) {
/* 457 */           String pattern = "\\d+(\\x2e\\d*)?";
/* 458 */           setLength(this.length);
/*     */         } else {
/* 460 */           pattern = "(\\d{1," + this.length + "})?(\\x2e\\d{0," + this.precision + "})?";
/*     */         }
/* 462 */         setPattern(Pattern.compile(pattern));
/*     */       }
/*     */     }
/*     */ 
/*     */     public void insertString(int index, String string, AttributeSet attributeSet) throws BadLocationException {
/* 467 */       String text = this.originator.getText();
/*     */ 
/* 469 */       String temp = text.substring(0, index) + string + text.substring(index);
/* 470 */       Matcher matcher = this.pattern.matcher(temp);
/* 471 */       if ((matcher.matches()) && (temp.length() <= this.length)) {
/* 472 */         if (text.startsWith(".")) {
/* 473 */           super.insertString(0, "0", attributeSet);
/* 474 */           super.insertString(index + 1, string, attributeSet);
/*     */         } else {
/* 476 */           super.insertString(index, string, attributeSet);
/*     */         }
/*     */       } else {
/* 479 */         Toolkit.getDefaultToolkit().beep();
/* 480 */         return;
/*     */       }
/*     */     }
/*     */ 
/*     */     protected void setPattern(Pattern pattern) {
/* 485 */       this.pattern = pattern;
/*     */     }
/*     */ 
/*     */     protected void setLength(int length) {
/* 489 */       this.length = length;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class AmountSpinnerModel extends AbstractSpinnerModel
/*     */     implements SpinnerModel
/*     */   {
/*     */     private BigDecimal value;
/*     */     private BigDecimal minimum;
/*     */     private BigDecimal maximum;
/*     */     private BigDecimal stepSize;
/*     */     private int precision;
/*     */     private boolean round05Pip;
/*     */ 
/*     */     private AmountSpinnerModel(double value, double minimum, double maximum, double stepSize, int precision, boolean round05Pip)
/*     */     {
/* 339 */       this.value = BigDecimal.valueOf(value);
/* 340 */       this.minimum = BigDecimal.valueOf(minimum);
/* 341 */       this.maximum = BigDecimal.valueOf(maximum);
/* 342 */       this.stepSize = BigDecimal.valueOf(stepSize);
/* 343 */       this.precision = precision;
/* 344 */       this.round05Pip = round05Pip;
/*     */     }
/*     */ 
/*     */     public Object getValue()
/*     */     {
/* 349 */       return this.value;
/*     */     }
/*     */ 
/*     */     public void setValue(Object value)
/*     */     {
/* 354 */       if ((value == null) || (!(value instanceof BigDecimal))) {
/* 355 */         throw new IllegalArgumentException("Number is null or not a BigDecimal");
/*     */       }
/* 357 */       BigDecimal newValue = (BigDecimal)value;
/* 358 */       newValue = round(newValue);
/* 359 */       if ((newValue.compareTo(this.maximum) > 0) || (newValue.compareTo(this.minimum) < 0)) {
/* 360 */         throw new IllegalArgumentException("Number is less than minimum or bigger than maximum: " + newValue + " max: " + this.maximum + " min:" + this.minimum);
/*     */       }
/* 362 */       this.value = newValue;
/* 363 */       fireStateChanged();
/*     */     }
/*     */ 
/*     */     public Object getNextValue()
/*     */     {
/* 368 */       BigDecimal newValue = round(this.value.add(this.stepSize));
/* 369 */       if ((newValue.compareTo(this.maximum) > 0) || (newValue.compareTo(this.minimum) < 0)) {
/* 370 */         return null;
/*     */       }
/* 372 */       return newValue;
/*     */     }
/*     */ 
/*     */     public Object getPreviousValue()
/*     */     {
/* 377 */       BigDecimal newValue = round(this.value.subtract(this.stepSize));
/* 378 */       if ((newValue.compareTo(this.maximum) > 0) || (newValue.compareTo(this.minimum) < 0)) {
/* 379 */         return null;
/*     */       }
/* 381 */       return newValue;
/*     */     }
/*     */ 
/*     */     public BigDecimal round(BigDecimal value) {
/* 385 */       if (this.round05Pip) {
/* 386 */         return StratUtils.round05(value, this.precision);
/*     */       }
/* 388 */       return StratUtils.roundHalfEven(value, this.precision);
/*     */     }
/*     */ 
/*     */     public BigDecimal getMinimum()
/*     */     {
/* 393 */       return this.minimum.stripTrailingZeros();
/*     */     }
/*     */ 
/*     */     public void setMinimum(BigDecimal minimum) {
/* 397 */       this.minimum = minimum;
/* 398 */       if (this.value.compareTo(minimum) < 0) {
/* 399 */         this.value = minimum;
/* 400 */         fireStateChanged();
/*     */       }
/*     */     }
/*     */ 
/*     */     public BigDecimal getMaximum() {
/* 405 */       return this.maximum.stripTrailingZeros();
/*     */     }
/*     */ 
/*     */     public void setMaximum(BigDecimal maximum) {
/* 409 */       this.maximum = maximum;
/* 410 */       if (this.value.compareTo(maximum) > 0) {
/* 411 */         this.value = maximum;
/* 412 */         fireStateChanged();
/*     */       }
/*     */     }
/*     */ 
/*     */     public BigDecimal getStepSize() {
/* 417 */       return this.stepSize;
/*     */     }
/*     */ 
/*     */     public void setStepSize(BigDecimal stepSize) {
/* 421 */       this.stepSize = stepSize;
/*     */     }
/*     */ 
/*     */     public void setPrecision(int precision)
/*     */     {
/* 429 */       if (precision != this.precision) {
/* 430 */         this.precision = precision;
/* 431 */         if (this.value.compareTo(round(this.value)) != 0) {
/* 432 */           this.value = round(this.value);
/* 433 */           fireStateChanged();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.util.spinners.CommonJSpinner
 * JD-Core Version:    0.6.0
 */