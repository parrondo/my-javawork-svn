/*     */ package com.dukascopy.dds2.greed.gui.component.strategy.optimizer;
/*     */ 
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Component;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.event.ItemListener;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JPanel;
/*     */ 
/*     */ public class BooleanParameterOptimizer extends AbstractOptimizer
/*     */ {
/*     */   private JComboBox cmbFirstValue;
/*     */   private JComboBox cmbSecondValue;
/*     */ 
/*     */   public BooleanParameterOptimizer(boolean mandatory, boolean readOnly, Boolean value)
/*     */   {
/*  28 */     super(mandatory, readOnly);
/*     */ 
/*  30 */     if (mandatory)
/*  31 */       this.cmbFirstValue = new JComboBox(new Object[] { Boolean.TRUE, Boolean.FALSE });
/*     */     else {
/*  33 */       this.cmbFirstValue = new JComboBox(new Object[] { "", Boolean.TRUE, Boolean.FALSE });
/*     */     }
/*  35 */     this.cmbFirstValue.setEnabled(!readOnly);
/*  36 */     this.cmbSecondValue = new JComboBox(new Object[] { "", Boolean.TRUE, Boolean.FALSE });
/*     */ 
/*  39 */     this.cmbFirstValue.setSelectedItem(value);
/*  40 */     this.cmbFirstValue.addItemListener(new ItemListener()
/*     */     {
/*     */       public void itemStateChanged(ItemEvent e) {
/*  43 */         if (BooleanParameterOptimizer.this.cmbFirstValue.getSelectedItem() == null) {
/*  44 */           BooleanParameterOptimizer.this.cmbSecondValue.setSelectedIndex(0);
/*     */         }
/*  46 */         BooleanParameterOptimizer.this.fireParametersChanged();
/*     */       }
/*     */     });
/*  49 */     this.cmbSecondValue.setSelectedIndex(0);
/*  50 */     this.cmbSecondValue.addItemListener(new ItemListener()
/*     */     {
/*     */       public void itemStateChanged(ItemEvent e) {
/*  53 */         BooleanParameterOptimizer.this.fireParametersChanged();
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public Component getMainComponent() {
/*  60 */     return this.cmbFirstValue;
/*     */   }
/*     */ 
/*     */   public void layoutOptimizerComponents(JPanel container, Object value)
/*     */   {
/*  65 */     super.layoutOptimizerComponents(container, value);
/*  66 */     container.setLayout(new BorderLayout());
/*  67 */     container.add(this.cmbSecondValue, "West");
/*     */   }
/*     */ 
/*     */   public void validateParams() throws CommitErrorException
/*     */   {
/*  72 */     Object firstValue = this.cmbFirstValue.getSelectedItem();
/*  73 */     Object secondValue = this.cmbSecondValue.getSelectedItem();
/*  74 */     if ((firstValue == null) || (!(firstValue instanceof Boolean))) {
/*  75 */       if (isMandatory()) {
/*  76 */         throw new CommitErrorException("optimizer.dialog.error.value.must.be.selected");
/*     */       }
/*  78 */       if ((secondValue instanceof Boolean))
/*  79 */         throw new CommitErrorException("optimizer.dialog.error.first.value.must.be.selected");
/*     */     }
/*     */   }
/*     */ 
/*     */   public Object[] getParams()
/*     */   {
/*  86 */     Object firstValue = this.cmbFirstValue.getSelectedItem();
/*  87 */     Object secondValue = this.cmbSecondValue.getSelectedItem();
/*  88 */     if ((firstValue == null) || (!(firstValue instanceof Boolean))) {
/*  89 */       return null;
/*     */     }
/*  91 */     if ((secondValue instanceof Boolean)) {
/*  92 */       if (secondValue.equals(firstValue)) {
/*  93 */         return new Boolean[] { (Boolean)firstValue };
/*     */       }
/*  95 */       return new Boolean[] { (Boolean)firstValue, (Boolean)secondValue };
/*     */     }
/*     */ 
/*  99 */     return new Boolean[] { (Boolean)firstValue };
/*     */   }
/*     */ 
/*     */   public void setParams(Object[] values)
/*     */   {
/* 105 */     if (values.length > 0) {
/* 106 */       this.cmbFirstValue.setSelectedItem(values[0]);
/*     */     }
/* 108 */     if (values.length > 1)
/* 109 */       this.cmbSecondValue.setSelectedItem(values[1]);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.optimizer.BooleanParameterOptimizer
 * JD-Core Version:    0.6.0
 */