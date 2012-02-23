/*     */ package com.dukascopy.dds2.greed.gui.component.strategy.optimizer;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.gui.l10n.Localizable;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.CardLayout;
/*     */ import java.awt.Component;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.text.MessageFormat;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ 
/*     */ public abstract class AbstractLookupParameterOptimizer extends AbstractOptimizer
/*     */   implements Localizable
/*     */ {
/*     */   private static final String COMPONENT_CONSTR = "COMPONENT";
/*     */   private static final String LABEL_CONSTR = "LABEL";
/*     */   private JButton btnShowDialog;
/*     */   private JPanel pnlControls;
/*     */   private CardLayout layout;
/*     */   private JLabel lblNumberOfSelected;
/*     */   private Object[] dialogValues;
/*     */   protected Component mainComponent;
/*     */ 
/*     */   protected AbstractLookupParameterOptimizer(Component mainComponent, Object value, boolean mandatory, boolean readOnly)
/*     */   {
/*  41 */     super(mandatory, readOnly);
/*  42 */     this.mainComponent = mainComponent;
/*  43 */     this.mainComponent.setEnabled(!readOnly);
/*  44 */     setValue(mainComponent, value);
/*     */ 
/*  49 */     Object actualValue = getValue(mainComponent);
/*  50 */     if (actualValue != null) {
/*  51 */       this.dialogValues = new Object[] { actualValue };
/*     */     }
/*     */ 
/*  54 */     this.btnShowDialog = new JButton("+");
/*  55 */     this.btnShowDialog.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/*  58 */         Object[] values = AbstractLookupParameterOptimizer.this.showDialog(AbstractLookupParameterOptimizer.this.btnShowDialog, AbstractLookupParameterOptimizer.this.getParams());
/*  59 */         if (values != null) {
/*  60 */           AbstractLookupParameterOptimizer.access$102(AbstractLookupParameterOptimizer.this, values);
/*  61 */           AbstractLookupParameterOptimizer.this.updateLayout();
/*  62 */           AbstractLookupParameterOptimizer.this.fireParametersChanged();
/*     */         }
/*     */       }
/*     */     });
/*  66 */     this.layout = new CardLayout();
/*  67 */     this.pnlControls = new JPanel(this.layout);
/*  68 */     this.lblNumberOfSelected = new JLabel();
/*  69 */     this.pnlControls.add(this.lblNumberOfSelected, "LABEL");
/*  70 */     this.pnlControls.add(mainComponent, "COMPONENT");
/*  71 */     this.layout.show(this.pnlControls, "COMPONENT");
/*  72 */     updateLayout();
/*  73 */     LocalizationManager.addLocalizable(this);
/*     */   }
/*     */ 
/*     */   public void localize()
/*     */   {
/*  78 */     updateLayout();
/*     */   }
/*     */ 
/*     */   protected void updateLayout() {
/*  82 */     if ((this.dialogValues == null) || (this.dialogValues.length < 1)) {
/*  83 */       this.layout.show(this.pnlControls, "COMPONENT");
/*  84 */       setValue(this.mainComponent, null);
/*     */     }
/*  86 */     else if (this.dialogValues.length == 1) {
/*  87 */       this.layout.show(this.pnlControls, "COMPONENT");
/*  88 */       setValue(this.mainComponent, this.dialogValues[0]);
/*     */     }
/*     */     else {
/*  91 */       this.lblNumberOfSelected.setText(MessageFormat.format(LocalizationManager.getText("optimizer.label.template.number.of.selected"), new Object[] { Integer.valueOf(this.dialogValues.length) }));
/*     */ 
/*  97 */       StringBuffer toolTipText = new StringBuffer();
/*  98 */       for (int i = 0; i < this.dialogValues.length; i++) {
/*  99 */         String string = valueToString(this.dialogValues[i]);
/* 100 */         if (string != null) {
/* 101 */           if (i > 0) {
/* 102 */             toolTipText.append(", ");
/*     */           }
/* 104 */           toolTipText.append(string);
/*     */         }
/*     */       }
/* 107 */       this.lblNumberOfSelected.setToolTipText(toolTipText.toString());
/* 108 */       this.layout.show(this.pnlControls, "LABEL"); } 
/*     */   }
/*     */   protected abstract Object[] showDialog(Component paramComponent, Object[] paramArrayOfObject);
/*     */ 
/*     */   protected abstract String valueToString(Object paramObject);
/*     */ 
/*     */   protected abstract void setValue(Component paramComponent, Object paramObject);
/*     */ 
/*     */   protected abstract void validateValue(Component paramComponent) throws CommitErrorException;
/*     */ 
/*     */   protected abstract Object getValue(Component paramComponent);
/*     */ 
/* 124 */   public Component getMainComponent() { return this.pnlControls;
/*     */   }
/*     */ 
/*     */   public final void layoutOptimizerComponents(JPanel container, Object value)
/*     */   {
/* 129 */     super.layoutOptimizerComponents(container, value);
/* 130 */     container.setLayout(new BorderLayout());
/* 131 */     container.add(this.btnShowDialog, "West");
/*     */   }
/*     */ 
/*     */   public void validateParams() throws CommitErrorException
/*     */   {
/* 136 */     if ((this.dialogValues == null) || (this.dialogValues.length < 2))
/* 137 */       validateValue(this.mainComponent);
/*     */   }
/*     */ 
/*     */   public Object[] getParams()
/*     */   {
/* 143 */     if ((this.dialogValues != null) && (this.dialogValues.length > 1)) {
/* 144 */       return this.dialogValues;
/*     */     }
/* 146 */     Object mainValue = getValue(this.mainComponent);
/* 147 */     if (mainValue == null) {
/* 148 */       return null;
/*     */     }
/* 150 */     return new Object[] { mainValue };
/*     */   }
/*     */ 
/*     */   public void setParams(Object[] values)
/*     */   {
/* 157 */     if (values.length < 1) {
/* 158 */       this.dialogValues = new Object[0];
/* 159 */       setValue(this.mainComponent, null);
/* 160 */     } else if (values.length == 1) {
/* 161 */       this.dialogValues = new Object[] { values[0] };
/* 162 */       setValue(this.mainComponent, values[0]);
/*     */     } else {
/* 164 */       this.dialogValues = values;
/*     */     }
/* 166 */     updateLayout();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.optimizer.AbstractLookupParameterOptimizer
 * JD-Core Version:    0.6.0
 */