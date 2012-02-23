/*     */ package com.dukascopy.dds2.greed.gui.component.strategy.optimizer;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.agent.strategy.SpinnerMouseWheelListener;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Component;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.Insets;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.event.ItemListener;
/*     */ import java.math.BigDecimal;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JFormattedTextField;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JSpinner;
/*     */ import javax.swing.JSpinner.NumberEditor;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.SpinnerNumberModel;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.ChangeListener;
/*     */ import javax.swing.event.DocumentEvent;
/*     */ import javax.swing.event.DocumentListener;
/*     */ import javax.swing.text.Document;
/*     */ 
/*     */ public abstract class AbstractRangeParameterOptimizer<D extends Number> extends AbstractOptimizer
/*     */ {
/*     */   private JCheckBox chbRangeEnable;
/*     */   private JLocalizableLabel lblRangeStep;
/*     */   private JLocalizableLabel lblRangeTo;
/*     */   private JTextField txtStartValue;
/*     */   private JTextField txtRangeStep;
/*     */   private JSpinner spnRangeTo;
/*     */ 
/*     */   protected AbstractRangeParameterOptimizer(D value, D maximum, D stepSize, boolean mandatory, boolean readOnly)
/*     */   {
/*  50 */     super(mandatory, readOnly);
/*  51 */     this.txtStartValue = new JTextField();
/*  52 */     this.txtStartValue.setColumns(15);
/*  53 */     this.txtStartValue.setText(valueToString(value));
/*  54 */     this.txtStartValue.setEnabled(!readOnly);
/*  55 */     this.txtStartValue.getDocument().addDocumentListener(new DocumentListener()
/*     */     {
/*     */       public void removeUpdate(DocumentEvent e)
/*     */       {
/*  59 */         AbstractRangeParameterOptimizer.this.updateRange();
/*  60 */         AbstractRangeParameterOptimizer.this.fireParametersChanged();
/*     */       }
/*     */ 
/*     */       public void insertUpdate(DocumentEvent e)
/*     */       {
/*  65 */         AbstractRangeParameterOptimizer.this.updateRange();
/*  66 */         AbstractRangeParameterOptimizer.this.fireParametersChanged();
/*     */       }
/*     */ 
/*     */       public void changedUpdate(DocumentEvent e)
/*     */       {
/*  71 */         AbstractRangeParameterOptimizer.this.updateRange();
/*  72 */         AbstractRangeParameterOptimizer.this.fireParametersChanged();
/*     */       }
/*     */     });
/*  75 */     this.chbRangeEnable = new JCheckBox();
/*  76 */     this.chbRangeEnable.addItemListener(new ItemListener()
/*     */     {
/*     */       public void itemStateChanged(ItemEvent e) {
/*  79 */         AbstractRangeParameterOptimizer.this.updateComponentState();
/*  80 */         if (AbstractRangeParameterOptimizer.this.chbRangeEnable.isSelected()) {
/*  81 */           AbstractRangeParameterOptimizer.this.updateRange();
/*     */         }
/*  83 */         AbstractRangeParameterOptimizer.this.fireParametersChanged();
/*     */       }
/*     */     });
/*  86 */     this.lblRangeStep = new JLocalizableLabel("optimizer.label.step");
/*  87 */     this.txtRangeStep = new JTextField();
/*  88 */     this.txtRangeStep.setColumns(15);
/*  89 */     this.txtRangeStep.setText(valueToString(stepSize));
/*  90 */     this.txtRangeStep.getDocument().addDocumentListener(new DocumentListener()
/*     */     {
/*     */       public void removeUpdate(DocumentEvent e)
/*     */       {
/*  94 */         AbstractRangeParameterOptimizer.this.updateRange();
/*  95 */         AbstractRangeParameterOptimizer.this.fireParametersChanged();
/*     */       }
/*     */ 
/*     */       public void insertUpdate(DocumentEvent e)
/*     */       {
/* 100 */         AbstractRangeParameterOptimizer.this.updateRange();
/* 101 */         AbstractRangeParameterOptimizer.this.fireParametersChanged();
/*     */       }
/*     */ 
/*     */       public void changedUpdate(DocumentEvent e)
/*     */       {
/* 106 */         AbstractRangeParameterOptimizer.this.updateRange();
/* 107 */         AbstractRangeParameterOptimizer.this.fireParametersChanged();
/*     */       }
/*     */     });
/* 110 */     this.lblRangeTo = new JLocalizableLabel("optimizer.label.to");
/*     */ 
/* 112 */     SpinnerNumberModel model = createSpinnerModel(value, maximum, stepSize);
/* 113 */     model.setStepSize(stepSize);
/* 114 */     this.spnRangeTo = createSpinner(value, model, getValueFormat());
/* 115 */     this.spnRangeTo.addChangeListener(new ChangeListener()
/*     */     {
/*     */       public void stateChanged(ChangeEvent e) {
/* 118 */         AbstractRangeParameterOptimizer.this.fireParametersChanged();
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public Component getMainComponent() {
/* 125 */     return this.txtStartValue;
/*     */   }
/*     */ 
/*     */   public void layoutOptimizerComponents(JPanel container, Object value)
/*     */   {
/* 130 */     super.layoutOptimizerComponents(container, value);
/* 131 */     container.setLayout(new GridBagLayout());
/* 132 */     container.add(this.chbRangeEnable, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 0, 0), 0, 0));
/* 133 */     container.add(this.lblRangeStep, new GridBagConstraints(1, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 5, 0, 0), 0, 0));
/* 134 */     container.add(this.txtRangeStep, new GridBagConstraints(2, 0, 1, 1, 0.5D, 0.0D, 17, 2, new Insets(0, 5, 0, 0), 0, 0));
/* 135 */     container.add(this.lblRangeTo, new GridBagConstraints(3, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 5, 0, 0), 0, 0));
/* 136 */     container.add(this.spnRangeTo, new GridBagConstraints(4, 0, 1, 1, 0.5D, 0.0D, 17, 2, new Insets(0, 5, 0, 0), 0, 0));
/*     */ 
/* 138 */     container.add(new JPanel(new BorderLayout()), new GridBagConstraints(5, 0, 1, 1, 1.0D, 0.0D, 17, 0, new Insets(0, 0, 0, 0), 0, 0));
/*     */ 
/* 140 */     updateComponentState();
/*     */   }
/*     */ 
/*     */   public void validateParams()
/*     */     throws CommitErrorException
/*     */   {
/* 146 */     Number numberFrom = stringToValue(this.txtStartValue.getText());
/* 147 */     Number numberStep = stringToValue(this.txtRangeStep.getText());
/* 148 */     Number numberTo = (Number)this.spnRangeTo.getValue();
/*     */ 
/* 150 */     if (!this.chbRangeEnable.isSelected())
/*     */     {
/* 152 */       if ((numberFrom == null) && 
/* 153 */         (isMandatory())) {
/* 154 */         throw new CommitErrorException("optimizer.dialog.error.value.is.required");
/*     */       }
/*     */ 
/*     */     }
/* 160 */     else if ((numberFrom == null) || (numberStep == null) || (numberTo == null))
/* 161 */       throw new CommitErrorException("optimizer.dialog.error.value.range.is.required");
/*     */   }
/*     */ 
/*     */   public D[] getParams()
/*     */   {
/*     */     try
/*     */     {
/* 170 */       Number numberFrom = stringToValue(this.txtStartValue.getText());
/*     */ 
/* 172 */       if (!this.chbRangeEnable.isSelected()) {
/* 173 */         if (numberFrom == null) {
/* 174 */           return null;
/*     */         }
/* 176 */         return getValues(numberFrom, null, null);
/*     */       }
/*     */ 
/* 180 */       Number numberStep = stringToValue(this.txtRangeStep.getText());
/* 181 */       Number numberTo = (Number)this.spnRangeTo.getValue();
/*     */ 
/* 183 */       if ((numberFrom != null) && (numberStep != null) && (numberTo != null)) {
/* 184 */         if (numberFrom.doubleValue() <= numberTo.doubleValue()) {
/* 185 */           return getValues(numberFrom, numberStep, numberTo);
/*     */         }
/* 187 */         return getValues(numberFrom, null, null);
/*     */       }
/*     */ 
/* 192 */       return null;
/*     */     }
/*     */     catch (CommitErrorException e) {
/*     */     }
/* 196 */     return null;
/*     */   }
/*     */ 
/*     */   public void setParams(Object[] values)
/*     */   {
/* 202 */     this.chbRangeEnable.setSelected(false);
/* 203 */     if (values.length > 0) {
/* 204 */       this.txtStartValue.setText(valueToString(values[0]));
/*     */     }
/* 206 */     if (values.length > 1) {
/* 207 */       Number step = calculateStep((Number)values[0], (Number)values[1]);
/* 208 */       this.txtRangeStep.setText(valueToString(step));
/* 209 */       this.spnRangeTo.setValue(values[(values.length - 1)]);
/* 210 */       this.chbRangeEnable.setSelected(true);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void updateRange() {
/*     */     Number valueFrom;
/*     */     Number valueStep;
/*     */     try {
/* 219 */       valueFrom = stringToValue(this.txtStartValue.getText());
/* 220 */       valueStep = stringToValue(this.txtRangeStep.getText());
/*     */     }
/*     */     catch (CommitErrorException e) {
/* 223 */       return;
/*     */     }
/*     */ 
/* 226 */     SpinnerNumberModel mdlRangeTo = (SpinnerNumberModel)this.spnRangeTo.getModel();
/*     */ 
/* 228 */     if ((valueStep != null) && (valueFrom != null)) {
/* 229 */       mdlRangeTo.setStepSize(valueStep);
/* 230 */       mdlRangeTo.setMinimum((Comparable)valueFrom);
/* 231 */       if (valueStep.doubleValue() == 0.0D)
/*     */       {
/* 233 */         return;
/*     */       }
/*     */       Number valueTo;
/*     */       Number valueTo;
/* 237 */       if (valueFrom.doubleValue() >= mdlRangeTo.getNumber().doubleValue()) {
/* 238 */         valueTo = valueFrom;
/*     */       }
/*     */       else
/*     */       {
/*     */         Number valueTo;
/* 240 */         if (this.spnRangeTo.getValue() == null) {
/* 241 */           valueTo = (Number)this.spnRangeTo.getNextValue();
/*     */         }
/*     */         else
/*     */         {
/* 245 */           BigDecimal from = BigDecimal.valueOf(valueFrom.doubleValue());
/* 246 */           BigDecimal step = BigDecimal.valueOf(valueStep.doubleValue());
/* 247 */           BigDecimal to = BigDecimal.valueOf(((Number)this.spnRangeTo.getNextValue()).doubleValue());
/*     */ 
/* 249 */           BigDecimal[] div = to.subtract(from).divideAndRemainder(step);
/*     */           Number valueTo;
/* 252 */           if (div[1].doubleValue() == 0.0D) {
/* 253 */             valueTo = null;
/*     */           }
/*     */           else
/*     */           {
/*     */             Number valueTo;
/* 254 */             if (div[1].abs().divide(step, 4).doubleValue() == 0.0D)
/* 255 */               valueTo = decimalToValue(from.add(div[0].multiply(step)));
/*     */             else
/* 257 */               valueTo = decimalToValue(from.add(div[0].add(BigDecimal.valueOf(1L)).multiply(step)));
/*     */           }
/*     */         }
/*     */       }
/* 261 */       if (valueTo != null)
/* 262 */         this.spnRangeTo.setValue(valueTo);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void updateComponentState()
/*     */   {
/* 268 */     boolean optimizerOn = this.chbRangeEnable.isSelected();
/* 269 */     if (this.txtRangeStep != null) {
/* 270 */       this.lblRangeStep.setEnabled(optimizerOn);
/* 271 */       this.txtRangeStep.setEnabled(optimizerOn);
/* 272 */       this.lblRangeTo.setEnabled(optimizerOn);
/* 273 */       this.spnRangeTo.setEnabled(optimizerOn);
/*     */     }
/*     */   }
/*     */ 
/*     */   private JSpinner createSpinner(D value, SpinnerNumberModel model, String format) {
/* 278 */     JSpinner spinner = new JSpinner(model);
/* 279 */     JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinner, format);
/* 280 */     editor.getTextField().setHorizontalAlignment(2);
/* 281 */     editor.getTextField().setColumns(20);
/* 282 */     editor.getTextField().setEditable(false);
/* 283 */     spinner.setEditor(editor);
/* 284 */     spinner.addMouseWheelListener(new SpinnerMouseWheelListener(spinner));
/* 285 */     spinner.setValue(value);
/* 286 */     return spinner;
/*     */   }
/*     */ 
/*     */   protected abstract String valueToString(Object paramObject);
/*     */ 
/*     */   protected abstract D stringToValue(String paramString)
/*     */     throws CommitErrorException;
/*     */ 
/*     */   protected abstract D decimalToValue(BigDecimal paramBigDecimal);
/*     */ 
/*     */   protected abstract String getValueFormat();
/*     */ 
/*     */   protected abstract SpinnerNumberModel createSpinnerModel(D paramD1, D paramD2, D paramD3);
/*     */ 
/*     */   protected abstract D[] getValues(D paramD1, D paramD2, D paramD3);
/*     */ 
/*     */   protected abstract D calculateStep(D paramD1, D paramD2);
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.optimizer.AbstractRangeParameterOptimizer
 * JD-Core Version:    0.6.0
 */