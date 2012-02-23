/*     */ package com.dukascopy.dds2.greed.gui.component;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.util.HalfPipRounder;
/*     */ import com.dukascopy.dds2.greed.util.PlatformSpecific;
/*     */ import java.awt.Insets;
/*     */ import java.awt.event.FocusEvent;
/*     */ import java.io.PrintStream;
/*     */ import java.math.BigDecimal;
/*     */ import java.text.ParseException;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.InputVerifier;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JFormattedTextField;
/*     */ import javax.swing.JFormattedTextField.AbstractFormatter;
/*     */ import javax.swing.JFormattedTextField.AbstractFormatterFactory;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JSpinner;
/*     */ import javax.swing.SpinnerModel;
/*     */ import javax.swing.SwingUtilities;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class PriceSpinner extends JSpinner
/*     */   implements PlatformSpecific
/*     */ {
/*  26 */   private static final Logger LOGGER = LoggerFactory.getLogger(PriceSpinner.class);
/*     */   private PriceAmountTextFieldM textField;
/*     */   private PriceString priceString;
/*  31 */   private int caretPosition = 0;
/*     */   private BigDecimal minimum;
/*     */   private BigDecimal maximum;
/*     */ 
/*     */   public PriceSpinner()
/*     */   {
/*  37 */     this.priceString = new PriceString();
/*  38 */     setMinimum(new BigDecimal(0));
/*  39 */     setMaximum(new BigDecimal("1000000"));
/*     */   }
/*     */ 
/*     */   public void setText(String text) {
/*  43 */     getTextField().setText(text);
/*     */   }
/*     */ 
/*     */   protected JComponent createEditor(SpinnerModel model)
/*     */   {
/*  48 */     setTextField(new PriceAmountTextFieldM(8)
/*     */     {
/*     */       protected void processFocusEvent(FocusEvent event) {
/*  51 */         if (1005 == event.getID()) {
/*     */           try
/*     */           {
/*  54 */             PriceSpinner.access$002(PriceSpinner.this, new PriceSpinner.PriceString(PriceSpinner.this, getTextField().getText()));
/*     */           } catch (Exception e) {
/*  56 */             PriceSpinner.LOGGER.error(e.getMessage(), e);
/*     */           }
/*     */         }
/*  59 */         super.processFocusEvent(event);
/*     */       }
/*     */     });
/*  62 */     getTextField().setMargin(new Insets(0, 5, 0, 5));
/*  63 */     if (MACOSX) {
/*  64 */       getTextField().putClientProperty("JComponent.sizeVariant", "small");
/*     */     }
/*     */ 
/*  67 */     getTextField().setColumns(8);
/*  68 */     getTextField().setValue(new PriceString());
/*  69 */     getTextField().setFormatterFactory(new JFormattedTextField.AbstractFormatterFactory()
/*     */     {
/*     */       public JFormattedTextField.AbstractFormatter getFormatter(JFormattedTextField tf) {
/*  72 */         return new PriceSpinner.PriceFormatter(PriceSpinner.this, null);
/*     */       }
/*     */     });
/*  75 */     getTextField().setInputVerifier(new PriceAmountVerifier(null));
/*  76 */     getTextField().setFocusLostBehavior(1);
/*  77 */     return getTextField();
/*     */   }
/*     */ 
/*     */   public void setMargin(Insets m)
/*     */   {
/*  83 */     getTextField().setMargin(m);
/*     */   }
/*     */   public Insets getMargin() {
/*  86 */     return getTextField().getMargin();
/*     */   }
/*     */ 
/*     */   public void clear() {
/*  90 */     getTextField().clear();
/*     */   }
/*     */   public void setHorizontalAlignment(int alignment) {
/*  93 */     getTextField().setHorizontalAlignment(alignment);
/*     */   }
/*     */   public void showMessage(String message) {
/*  96 */     getTextField().showMessage(message);
/*     */   }
/*     */ 
/*     */   public JComponent getEditor()
/*     */   {
/* 109 */     return getTextField();
/*     */   }
/*     */ 
/*     */   public Object getValue()
/*     */   {
/* 114 */     return this.priceString;
/*     */   }
/*     */ 
/*     */   public void setValue(Object value)
/*     */   {
/* 119 */     if ((value == null) || (!(value instanceof PriceString))) {
/* 120 */       throw new IllegalArgumentException("Number is null or not a PriceString");
/*     */     }
/* 122 */     PriceString newValue = (PriceString)value;
/* 123 */     BigDecimal newPrice = new BigDecimal(newValue.price);
/*     */ 
/* 125 */     if ((newPrice.compareTo(this.maximum) > 0) || (newPrice.compareTo(this.minimum) < 0)) {
/* 126 */       throw new IllegalArgumentException("Price is less than minimum or bigger than maximum: " + newPrice + " max: " + this.maximum + " min:" + this.minimum);
/*     */     }
/* 128 */     getTextField().setValue(newValue);
/* 129 */     getTextField().setCaretPosition(this.caretPosition);
/*     */ 
/* 131 */     fireStateChanged();
/*     */   }
/*     */ 
/*     */   public Object getPreviousValue()
/*     */   {
/* 136 */     return adjustPrice(-1);
/*     */   }
/*     */ 
/*     */   public Object getNextValue()
/*     */   {
/* 141 */     return adjustPrice(1);
/*     */   }
/*     */ 
/*     */   public BigDecimal getMinimum() {
/* 145 */     return this.minimum;
/*     */   }
/*     */ 
/*     */   public void setMinimum(BigDecimal min) {
/* 149 */     this.minimum = min;
/*     */   }
/*     */ 
/*     */   public BigDecimal getMaximum() {
/* 153 */     return this.maximum;
/*     */   }
/*     */ 
/*     */   public void setMaximum(BigDecimal max) {
/* 157 */     this.maximum = max;
/*     */   }
/*     */ 
/*     */   public boolean validatePrice() {
/* 161 */     BigDecimal newValue = new BigDecimal(getTextField().getText());
/*     */ 
/* 163 */     return (newValue.compareTo(this.maximum) <= 0) && (newValue.compareTo(this.minimum) >= 0);
/*     */   }
/*     */ 
/*     */   public void refreshToMin()
/*     */   {
/* 169 */     getTextField().setValue(new PriceString(getMinimum().toPlainString()));
/*     */   }
/*     */ 
/*     */   public void refreshToMax() {
/* 173 */     getTextField().setValue(new PriceString(getMaximum().toPlainString()));
/*     */   }
/*     */ 
/*     */   private PriceString adjustPrice(int direction)
/*     */   {
/* 208 */     this.caretPosition = getTextField().getCaretPosition();
/* 209 */     String text = getTextField().getText();
/* 210 */     BigDecimal normalized = new BigDecimal(text);
/* 211 */     BigDecimal adjusted = HalfPipRounder.adjustPrice(normalized, direction);
/*     */ 
/* 213 */     if (adjusted != null) {
/* 214 */       if ((adjusted.compareTo(this.maximum) > 0) || (adjusted.compareTo(this.minimum) < 0)) {
/* 215 */         return null;
/*     */       }
/* 217 */       return new PriceString(adjusted.toString());
/*     */     }
/*     */ 
/* 220 */     return new PriceString(text);
/*     */   }
/*     */ 
/*     */   public String getText()
/*     */   {
/* 225 */     if (("".equalsIgnoreCase(this.textField.getText())) && (isEnabled())) {
/* 226 */       showMessage("label.error.property.is.empty");
/*     */     }
/* 228 */     return getTextField().getText();
/*     */   }
/*     */ 
/*     */   PriceAmountVerifier getVerifier() {
/* 232 */     return (PriceAmountVerifier)getTextField().getInputVerifier();
/*     */   }
/*     */ 
/*     */   private void setTextField(PriceAmountTextFieldM textField)
/*     */   {
/* 239 */     this.textField = textField;
/*     */   }
/*     */ 
/*     */   private PriceAmountTextFieldM getTextField()
/*     */   {
/* 246 */     return this.textField;
/*     */   }
/*     */ 
/*     */   public void setPriceString(PriceString priceString) {
/* 250 */     this.priceString = priceString;
/*     */   }
/*     */ 
/*     */   public PriceString getPriceString() {
/* 254 */     return this.priceString;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 316 */     SwingUtilities.invokeLater(new Runnable()
/*     */     {
/*     */       public void run()
/*     */       {
/* 320 */         JFrame frame = new JFrame("SpinnerDemo");
/* 321 */         frame.setDefaultCloseOperation(3);
/*     */ 
/* 324 */         JPanel newContentPane = new JPanel();
/* 325 */         newContentPane.setLayout(new BoxLayout(newContentPane, 1));
/* 326 */         PriceSpinner priceSpinner = new PriceSpinner();
/* 327 */         priceSpinner.setText("12.08175");
/* 328 */         System.err.println("text = " + priceSpinner.getText());
/* 329 */         System.err.println("val = " + ((PriceSpinner.PriceString)priceSpinner.getValue()).toString());
/* 330 */         newContentPane.add(priceSpinner);
/*     */ 
/* 332 */         newContentPane.setOpaque(true);
/* 333 */         frame.setContentPane(newContentPane);
/*     */ 
/* 336 */         frame.pack();
/* 337 */         frame.setVisible(true);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public class PriceString
/*     */   {
/*     */     private String price;
/*     */ 
/*     */     PriceString()
/*     */     {
/*     */     }
/*     */ 
/*     */     PriceString(String val)
/*     */     {
/* 305 */       this.price = val;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 310 */       return this.price;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class PriceFormatter extends JFormattedTextField.AbstractFormatter
/*     */   {
/*     */     private PriceFormatter()
/*     */     {
/*     */     }
/*     */ 
/*     */     public Object stringToValue(String text)
/*     */       throws ParseException
/*     */     {
/*     */       try
/*     */       {
/* 285 */         new PriceSpinner.PriceString(PriceSpinner.this, text);
/*     */       } catch (Exception e) {
/* 287 */         PriceSpinner.LOGGER.error("bad input " + text + "  " + e.getMessage(), e);
/*     */       }
/* 289 */       return new PriceSpinner.PriceString(PriceSpinner.this, text);
/*     */     }
/*     */ 
/*     */     public String valueToString(Object value) throws ParseException
/*     */     {
/* 294 */       return ((PriceSpinner.PriceString)value).toString();
/*     */     }
/*     */   }
/*     */ 
/*     */   private class PriceAmountVerifier extends InputVerifier
/*     */   {
/*     */     private PriceAmountVerifier()
/*     */     {
/*     */     }
/*     */ 
/*     */     public boolean verify(JComponent input)
/*     */     {
/* 261 */       if ((input instanceof JFormattedTextField)) {
/* 262 */         String text = ((JFormattedTextField)input).getText();
/* 263 */         JFormattedTextField.AbstractFormatter formatter = ((JFormattedTextField)input).getFormatter();
/*     */         try
/*     */         {
/* 266 */           formatter.stringToValue(text);
/* 267 */           return true;
/*     */         } catch (ParseException e) {
/* 269 */           return false;
/*     */         }
/*     */       }
/* 272 */       return true;
/*     */     }
/*     */ 
/*     */     public boolean shouldYieldFocus(JComponent input)
/*     */     {
/* 277 */       return verify(input);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.PriceSpinner
 * JD-Core Version:    0.6.0
 */