/*     */ package com.dukascopy.charts.dialogs.drawings;
/*     */ 
/*     */ import com.dukascopy.charts.dialogs.AbsoluteLayout;
/*     */ import com.dukascopy.charts.dialogs.AbsoluteLayoutConstraints;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Frame;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.text.DecimalFormat;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JRootPane;
/*     */ import javax.swing.JSpinner;
/*     */ import javax.swing.JSpinner.NumberEditor;
/*     */ import javax.swing.SpinnerNumberModel;
/*     */ 
/*     */ public class NumericParamEditDialog extends JDialog
/*     */   implements ActionListener
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private JButton okButton;
/*     */   private JButton cancelButton;
/*  29 */   private JSpinner valueEditor = new JSpinner();
/*     */   private Double resultValue;
/*     */ 
/*     */   public NumericParamEditDialog(Frame parent, String fieldNameKey, double defaultValue, double minValue, double maxValue, int integerDigits, int fractionDigits, double stepSize)
/*     */   {
/*  35 */     super(parent, "Edit " + LocalizationManager.getText(fieldNameKey), true);
/*     */ 
/*  37 */     setLocationRelativeTo(parent);
/*  38 */     setModal(true);
/*     */ 
/*  41 */     this.valueEditor.setModel(new SpinnerNumberModel(defaultValue, minValue, maxValue, stepSize));
/*     */ 
/*  43 */     DecimalFormat format = ((JSpinner.NumberEditor)this.valueEditor.getEditor()).getFormat();
/*  44 */     format.setMaximumFractionDigits(fractionDigits);
/*  45 */     format.setMinimumFractionDigits(fractionDigits);
/*  46 */     format.setMaximumIntegerDigits(integerDigits);
/*     */ 
/*  48 */     JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
/*  49 */     mainPanel.setBorder(BorderFactory.createEmptyBorder(9, 15, 5, 15));
/*     */ 
/*  51 */     JPanel editorPanel = new JPanel(new BorderLayout(10, 0));
/*  52 */     editorPanel.setPreferredSize(new Dimension(2147483647, 20));
/*  53 */     editorPanel.setMaximumSize(new Dimension(2147483647, 20));
/*     */ 
/*  55 */     editorPanel.add(new JLabel(LocalizationManager.getText(fieldNameKey) + ":"), "West");
/*  56 */     editorPanel.add(this.valueEditor, "Center");
/*     */ 
/*  58 */     mainPanel.add(editorPanel, "Center");
/*     */ 
/*  62 */     this.okButton = new JButton("OK");
/*  63 */     this.okButton.setActionCommand("OK");
/*  64 */     this.okButton.addActionListener(this);
/*     */ 
/*  66 */     getRootPane().setDefaultButton(this.okButton);
/*  67 */     this.okButton.setFocusCycleRoot(true);
/*     */ 
/*  69 */     this.cancelButton = new JButton("Cancel");
/*  70 */     this.cancelButton.setActionCommand("Cancel");
/*  71 */     this.cancelButton.addActionListener(this);
/*     */ 
/*  73 */     JPanel buttonPanel = new JPanel(new AbsoluteLayout());
/*  74 */     buttonPanel.setPreferredSize(new Dimension(170, 30));
/*  75 */     buttonPanel.add(this.okButton, new AbsoluteLayoutConstraints(5, 5, 75, 25));
/*  76 */     buttonPanel.add(this.cancelButton, new AbsoluteLayoutConstraints(90, 5, 75, 25));
/*     */ 
/*  78 */     mainPanel.add(buttonPanel, "South");
/*     */ 
/*  82 */     setContentPane(mainPanel);
/*  83 */     setSize(200, 100);
/*  84 */     setResizable(false);
/*  85 */     setVisible(true);
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent e)
/*     */   {
/*  90 */     if ("OK".equals(e.getActionCommand())) {
/*  91 */       this.resultValue = ((Double)this.valueEditor.getValue());
/*  92 */       setVisible(false);
/*  93 */     } else if ("Cancel".equals(e.getActionCommand())) {
/*  94 */       this.resultValue = null;
/*  95 */       setVisible(false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Double getResultValue() {
/* 100 */     return this.resultValue;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.dialogs.drawings.NumericParamEditDialog
 * JD-Core Version:    0.6.0
 */