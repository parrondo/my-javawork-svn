/*     */ package com.dukascopy.dds2.greed.gui.component;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*     */ import com.dukascopy.dds2.greed.util.PlatformSpecific;
/*     */ import com.dukascopy.transport.common.msg.request.AccountInfoMessage;
/*     */ import java.awt.Color;
/*     */ import java.awt.Cursor;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Insets;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.InputVerifier;
/*     */ import javax.swing.JFormattedTextField.AbstractFormatterFactory;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.Timer;
/*     */ 
/*     */ public class PriceAmountTextFieldM extends JPanel
/*     */   implements PlatformSpecific
/*     */ {
/*     */   public static final String ID_JP_PRICEPANEL = "ID_JP_PRICEPANEL";
/*     */   private PriceAmountTextField textField;
/*  31 */   private JLocalizableLabel label = new JLocalizableLabel();
/*     */   private Timer timer;
/*     */ 
/*     */   public PriceAmountTextFieldM()
/*     */   {
/*  41 */     setName("ID_JP_PRICEPANEL");
/*     */   }
/*     */ 
/*     */   public PriceAmountTextFieldM(int length)
/*     */   {
/*  46 */     this();
/*  47 */     setOpaque(false);
/*  48 */     build(length, 0);
/*     */   }
/*     */ 
/*     */   public PriceAmountTextFieldM(int length, int precision)
/*     */   {
/*  58 */     this();
/*  59 */     build(length, precision);
/*     */   }
/*     */ 
/*     */   private void build(int length, int precision) {
/*  63 */     this.textField = new PriceAmountTextField(length, precision);
/*  64 */     if (MACOSX) {
/*  65 */       this.textField.putClientProperty("JComponent.sizeVariant", "small");
/*  66 */       this.label.putClientProperty("JComponent.sizeVariant", "small");
/*     */     }
/*  68 */     setLayout(new BoxLayout(this, 1));
/*  69 */     add(this.textField);
/*  70 */     add(this.label);
/*  71 */     this.label.setVisible(false);
/*  72 */     this.label.setForeground(Color.RED);
/*  73 */     this.label.addMouseListener(new MouseAdapter() {
/*     */       public void mouseClicked(MouseEvent e) {
/*  75 */         PriceAmountTextFieldM.this.hideMessage();
/*     */       }
/*     */ 
/*     */       public void mousePressed(MouseEvent e) {
/*  79 */         PriceAmountTextFieldM.this.hideMessage();
/*     */       }
/*     */ 
/*     */       public void mouseEntered(MouseEvent e) {
/*  83 */         PriceAmountTextFieldM.this.setCursor(Cursor.getPredefinedCursor(12));
/*     */       }
/*     */ 
/*     */       public void mouseExited(MouseEvent e) {
/*  87 */         PriceAmountTextFieldM.this.setCursor(Cursor.getDefaultCursor());
/*     */       }
/*     */     });
/*  90 */     this.timer = new Timer(5000, new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/*  92 */         PriceAmountTextFieldM.this.hideMessage();
/*     */       }
/*     */     });
/*  95 */     this.timer.setRepeats(false);
/*     */   }
/*     */ 
/*     */   public void showMessage(String message) {
/*  99 */     this.label.setText(message);
/* 100 */     if (this.timer.isRunning()) {
/* 101 */       this.timer.restart();
/*     */     } else {
/* 103 */       Dimension dim = this.textField.getSize();
/* 104 */       this.textField.setVisible(false);
/* 105 */       this.label.setPreferredSize(dim);
/* 106 */       this.label.setVisible(true);
/* 107 */       this.timer.start();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void hideMessage() {
/* 111 */     if (this.timer.isRunning()) this.timer.stop();
/* 112 */     this.label.setVisible(false);
/* 113 */     this.textField.setVisible(true);
/*     */   }
/*     */ 
/*     */   public void setEnabled(boolean enabled)
/*     */   {
/* 118 */     this.textField.setEnabled(enabled);
/*     */   }
/*     */   public void setMargin(Insets m) {
/* 121 */     this.textField.setMargin(m);
/*     */   }
/*     */   public Insets getMargin() {
/* 124 */     return this.textField.getMargin();
/*     */   }
/*     */   public void setColumns(int columns) {
/* 127 */     this.textField.setColumns(columns);
/*     */   }
/*     */   public void setValue(Object value) {
/* 130 */     this.textField.setValue(value);
/*     */   }
/*     */   public void setFormatterFactory(JFormattedTextField.AbstractFormatterFactory afFactory) {
/* 133 */     this.textField.setFormatterFactory(afFactory);
/*     */   }
/*     */   public void setInputVerifier(InputVerifier inputVerifier) {
/* 136 */     this.textField.setInputVerifier(inputVerifier);
/*     */   }
/*     */   public void setFocusLostBehavior(int behav) {
/* 139 */     this.textField.setFocusLostBehavior(behav);
/*     */   }
/*     */   public void setText(String text) {
/* 142 */     this.textField.setText(text);
/*     */   }
/*     */   public String getText() {
/* 145 */     return this.textField.getText();
/*     */   }
/*     */   public void setHorizontalAlignment(int alignment) {
/* 148 */     this.textField.setHorizontalAlignment(alignment);
/*     */   }
/*     */   public void setCaretPosition(int caretPosition) {
/* 151 */     this.textField.setCaretPosition(caretPosition);
/*     */   }
/*     */   public int getCaretPosition() {
/* 154 */     return this.textField.getCaretPosition();
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 160 */     this.textField.clear();
/*     */   }
/*     */ 
/*     */   public void onAccountInfo(AccountInfoMessage accountInfo)
/*     */   {
/* 165 */     if (accountInfo.isMiniFx())
/* 166 */       this.textField.setPrecision(6);
/*     */     else
/* 168 */       this.textField.setPrecision(2);
/*     */   }
/*     */ 
/*     */   public void setTextField(PriceAmountTextField textField)
/*     */   {
/* 173 */     this.textField = textField;
/*     */   }
/*     */ 
/*     */   public PriceAmountTextField getTextField() {
/* 177 */     return this.textField;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.PriceAmountTextFieldM
 * JD-Core Version:    0.6.0
 */