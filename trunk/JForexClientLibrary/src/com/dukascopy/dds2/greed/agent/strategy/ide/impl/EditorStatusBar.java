/*     */ package com.dukascopy.dds2.greed.agent.strategy.ide.impl;
/*     */ 
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.Insets;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.event.CaretEvent;
/*     */ import javax.swing.event.CaretListener;
/*     */ import javax.swing.event.DocumentEvent;
/*     */ import javax.swing.event.DocumentListener;
/*     */ import javax.swing.text.Document;
/*     */ import javax.swing.text.Element;
/*     */ import javax.swing.text.JTextComponent;
/*     */ 
/*     */ public class EditorStatusBar extends JPanel
/*     */   implements CaretListener, DocumentListener
/*     */ {
/*     */   private static final String NB_CHAR_LABEL = "nb char: ";
/*     */   private static final String LN_LABEL = "Ln: ";
/*     */   private static final String COL_LABEL = "  Col: ";
/*     */   private JLabel lblCharactersCount;
/*     */   private JLabel lblTextType;
/*     */   private JLabel lblRowColumnSel;
/*     */   private JLabel lblEncoding;
/*     */   private int charactersCount;
/*     */   private int rowPosition;
/*     */   private int columnPosition;
/*     */   private int charactersSelected;
/*     */ 
/*     */   public EditorStatusBar()
/*     */   {
/*  30 */     this.lblCharactersCount = new JLabel("nb char: " + this.charactersCount);
/*  31 */     this.lblTextType = new JLabel("Text type: plain text");
/*  32 */     this.lblRowColumnSel = new JLabel(createLabel());
/*  33 */     this.lblEncoding = new JLabel("enc: UTF-8");
/*     */ 
/*  35 */     this.lblCharactersCount.setBorder(BorderFactory.createEtchedBorder());
/*  36 */     this.lblTextType.setBorder(BorderFactory.createEtchedBorder());
/*  37 */     this.lblRowColumnSel.setBorder(BorderFactory.createEtchedBorder());
/*  38 */     this.lblEncoding.setBorder(BorderFactory.createEtchedBorder());
/*     */ 
/*  40 */     setLayout(new GridBagLayout());
/*  41 */     add(this.lblCharactersCount, new GridBagConstraints(0, 0, 1, 1, 0.25D, 1.0D, 10, 1, new Insets(0, 29, 0, 0), 0, 0));
/*  42 */     add(this.lblTextType, new GridBagConstraints(1, 0, 1, 1, 0.25D, 1.0D, 10, 1, new Insets(0, 0, 0, 0), 0, 0));
/*  43 */     add(this.lblRowColumnSel, new GridBagConstraints(2, 0, 1, 1, 0.25D, 1.0D, 10, 1, new Insets(0, 0, 0, 0), 0, 0));
/*  44 */     add(this.lblEncoding, new GridBagConstraints(3, 0, 1, 1, 0.25D, 1.0D, 10, 1, new Insets(0, 0, 0, 0), 0, 0));
/*     */   }
/*     */ 
/*     */   public void updateCharactersCount(int length) {
/*  48 */     this.charactersCount += length;
/*  49 */     this.lblCharactersCount.setText("nb char: " + this.charactersCount);
/*     */   }
/*     */ 
/*     */   public void updateRowPosition(int newRowPosition) {
/*  53 */     this.rowPosition = (newRowPosition + 1);
/*  54 */     String label = createLabel();
/*  55 */     this.lblRowColumnSel.setText(label);
/*     */   }
/*     */ 
/*     */   public void updateColumnPosition(int newColPosition) {
/*  59 */     this.columnPosition = (newColPosition + 1);
/*  60 */     String label = createLabel();
/*  61 */     this.lblRowColumnSel.setText(label);
/*     */   }
/*     */ 
/*     */   public int getColumnAtCaret(JTextComponent caretEvent) {
/*  65 */     int caretPosition = caretEvent.getCaretPosition();
/*  66 */     Element root = caretEvent.getDocument().getDefaultRootElement();
/*  67 */     int line = root.getElementIndex(caretPosition);
/*  68 */     int lineStart = root.getElement(line).getStartOffset();
/*  69 */     return caretPosition - lineStart;
/*     */   }
/*     */ 
/*     */   public int getLineAtCaret(JTextComponent caretEvent) {
/*  73 */     int caretPosition = caretEvent.getCaretPosition();
/*  74 */     Element root = caretEvent.getDocument().getDefaultRootElement();
/*  75 */     return root.getElementIndex(caretPosition);
/*     */   }
/*     */ 
/*     */   private String createLabel() {
/*  79 */     StringBuilder label = new StringBuilder();
/*  80 */     label.append("Ln: ").append(this.rowPosition);
/*  81 */     label.append("  Col: ").append(this.columnPosition);
/*     */ 
/*  83 */     return label.toString();
/*     */   }
/*     */ 
/*     */   public void caretUpdate(CaretEvent caretEvent)
/*     */   {
/*  88 */     JTextComponent jTextArea = (JTextComponent)caretEvent.getSource();
/*  89 */     int lineAtCaret = getLineAtCaret(jTextArea);
/*  90 */     updateRowPosition(lineAtCaret);
/*  91 */     int columnAtCaret = getColumnAtCaret(jTextArea);
/*  92 */     updateColumnPosition(columnAtCaret);
/*     */   }
/*     */ 
/*     */   public void insertUpdate(DocumentEvent documentEvent)
/*     */   {
/*  97 */     updateCharactersCount(documentEvent.getLength());
/*     */   }
/*     */ 
/*     */   public void removeUpdate(DocumentEvent documentEvent) {
/* 101 */     updateCharactersCount(-documentEvent.getLength());
/*     */   }
/*     */ 
/*     */   public void changedUpdate(DocumentEvent e)
/*     */   {
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.ide.impl.EditorStatusBar
 * JD-Core Version:    0.6.0
 */