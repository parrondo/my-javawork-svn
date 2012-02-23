/*     */ package com.dukascopy.dds2.greed.gui.component.strategy;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.system.Commissions;
/*     */ import com.dukascopy.api.system.Overnights;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*     */ import com.dukascopy.dds2.greed.util.AbstractCurrencyConverter;
/*     */ import com.dukascopy.dds2.greed.util.GridBagLayoutHelper;
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.util.Currency;
/*     */ import java.util.Set;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JSpinner;
/*     */ import javax.swing.JSpinner.NumberEditor;
/*     */ import javax.swing.SpinnerModel;
/*     */ import javax.swing.SpinnerNumberModel;
/*     */ import javax.swing.SwingUtilities;
/*     */ 
/*     */ public class AccountSettingsPanel extends JPanel
/*     */ {
/*     */   private static final int SPINNERS_WIDTH = 100;
/*     */   private JSpinner initialDepositSpinner;
/*     */   private JComboBox accountCurrencyComboBox;
/*     */   private JSpinner mcEquitySpinner;
/*     */   private JButton commissionsButton;
/*     */   private JButton overnightsButton;
/*     */   private JSpinner maxLeverageSpinner;
/*     */   private JSpinner marginCallSpinner;
/*     */   private JButton buttonOk;
/*     */   private JButton buttonCancel;
/*     */   private Commissions tempCommissions;
/*     */   private Overnights tempOvernights;
/*     */   private JDialog modalDialog;
/*     */   private boolean modalResult;
/*     */   private double commitedDeposit;
/*     */   private double commitedEquity;
/*     */   private Integer commitedLeverage;
/*     */   private Integer commitedMarginCall;
/*     */   private Currency commitedCurrency;
/*  71 */   private Commissions commitedCommissions = new Commissions(false);
/*  72 */   private Overnights commitedOvernights = new Overnights(false);
/*     */ 
/*     */   public AccountSettingsPanel()
/*     */   {
/*  78 */     initUI();
/*     */   }
/*     */ 
/*     */   private void initUI()
/*     */   {
/*  85 */     Set majors = AbstractCurrencyConverter.getMajors();
/*  86 */     this.accountCurrencyComboBox = new JComboBox(majors.toArray(new Currency[majors.size()]));
/*  87 */     this.accountCurrencyComboBox.setMinimumSize(this.accountCurrencyComboBox.getPreferredSize());
/*  88 */     this.accountCurrencyComboBox.setSelectedItem(Instrument.EURUSD.getSecondaryCurrency());
/*  89 */     this.commitedCurrency = ((Currency)this.accountCurrencyComboBox.getSelectedItem());
/*     */ 
/*  91 */     this.maxLeverageSpinner = new JSpinner(new SpinnerNumberModel(100, 1, 2147483647, 1))
/*     */     {
/*     */       public Dimension getPreferredSize() {
/*  94 */         Dimension size = super.getPreferredSize();
/*  95 */         size.width = 100;
/*  96 */         return size;
/*     */       }
/*     */     };
/*  99 */     this.commitedLeverage = ((Integer)this.maxLeverageSpinner.getValue());
/*     */ 
/* 101 */     this.marginCallSpinner = new JSpinner(new SpinnerNumberModel(200, 1, 2147483647, 1))
/*     */     {
/*     */       public Dimension getPreferredSize() {
/* 104 */         Dimension size = super.getPreferredSize();
/* 105 */         size.width = 100;
/* 106 */         return size;
/*     */       }
/*     */     };
/* 109 */     this.commitedMarginCall = ((Integer)this.marginCallSpinner.getValue());
/*     */ 
/* 111 */     this.mcEquitySpinner = new JSpinner(new SpinnerNumberModel(0.0D, 0.0D, 2147483647.0D, 5000.0D))
/*     */     {
/*     */       protected JComponent createEditor(SpinnerModel model) {
/* 114 */         return new JSpinner.NumberEditor(this, "#,##0.###");
/*     */       }
/*     */ 
/*     */       public Dimension getPreferredSize() {
/* 118 */         Dimension size = super.getPreferredSize();
/* 119 */         size.width = 100;
/* 120 */         return size;
/*     */       }
/*     */     };
/* 123 */     this.commitedEquity = ((Double)this.mcEquitySpinner.getValue()).doubleValue();
/*     */ 
/* 125 */     this.commissionsButton = new JLocalizableButton("label.commissions");
/* 126 */     this.commissionsButton.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 129 */         CommissionsDialog commissionsDialog = new CommissionsDialog(AccountSettingsPanel.this, AccountSettingsPanel.this.tempCommissions);
/* 130 */         if (commissionsDialog.getCommissions() != null)
/* 131 */           AccountSettingsPanel.access$002(AccountSettingsPanel.this, commissionsDialog.getCommissions());
/*     */       }
/*     */     });
/* 135 */     this.overnightsButton = new JLocalizableButton("label.overnights");
/* 136 */     this.overnightsButton.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 139 */         OvernightsDialog overnightsDialog = new OvernightsDialog(AccountSettingsPanel.this, AccountSettingsPanel.this.tempOvernights);
/* 140 */         if (overnightsDialog.getOvernights() != null)
/* 141 */           AccountSettingsPanel.access$102(AccountSettingsPanel.this, overnightsDialog.getOvernights());
/*     */       }
/*     */     });
/* 145 */     this.initialDepositSpinner = new JSpinner(new SpinnerNumberModel(50000.0D, 0.0D, 2147483647.0D, 10000.0D))
/*     */     {
/*     */       protected JComponent createEditor(SpinnerModel model) {
/* 148 */         return new JSpinner.NumberEditor(this, "#,##0.###");
/*     */       }
/*     */ 
/*     */       public Dimension getPreferredSize() {
/* 152 */         Dimension size = super.getPreferredSize();
/* 153 */         size.width = 100;
/* 154 */         return size;
/*     */       }
/*     */     };
/* 157 */     this.commitedDeposit = ((Double)this.initialDepositSpinner.getValue()).doubleValue();
/* 158 */     this.buttonOk = new JLocalizableButton("button.ok");
/* 159 */     this.buttonCancel = new JLocalizableButton("button.cancel");
/*     */ 
/* 161 */     JPanel commissionsOvernightsPanel = new JPanel(new GridLayout(1, 2, 5, 0));
/* 162 */     commissionsOvernightsPanel.add(this.commissionsButton);
/* 163 */     commissionsOvernightsPanel.add(this.overnightsButton);
/*     */ 
/* 165 */     JPanel dialogButtonsPanel = new JPanel(new GridLayout(1, 2, 5, 0));
/* 166 */     dialogButtonsPanel.add(this.buttonOk);
/* 167 */     dialogButtonsPanel.add(this.buttonCancel);
/*     */ 
/* 169 */     setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
/* 170 */     setLayout(new GridBagLayout());
/* 171 */     GridBagConstraints gbc = new GridBagConstraints();
/* 172 */     gbc.anchor = 17;
/* 173 */     gbc.fill = 0;
/* 174 */     GridBagLayoutHelper.add(0, 0, 0.0D, 0.0D, 1, 1, 0, 0, 0, 0, gbc, this, new JLocalizableLabel("label.initial.deposit"));
/* 175 */     gbc.fill = 2;
/* 176 */     GridBagLayoutHelper.add(1, 0, 1.0D, 0.0D, 1, 1, 5, 0, 0, 0, gbc, this, this.initialDepositSpinner);
/* 177 */     GridBagLayoutHelper.add(2, 0, 1.0D, 0.0D, 1, 1, 5, 0, 0, 0, gbc, this, this.accountCurrencyComboBox);
/* 178 */     gbc.fill = 0;
/* 179 */     GridBagLayoutHelper.add(3, 0, 0.0D, 0.0D, 1, 1, 10, 0, 0, 0, gbc, this, new JLocalizableLabel("label.leverage"));
/* 180 */     gbc.fill = 2;
/* 181 */     GridBagLayoutHelper.add(4, 0, 1.0D, 0.0D, 1, 1, 5, 0, 0, 0, gbc, this, this.maxLeverageSpinner);
/* 182 */     gbc.fill = 0;
/* 183 */     GridBagLayoutHelper.add(0, 1, 0.0D, 0.0D, 1, 1, 0, 5, 0, 0, gbc, this, new JLocalizableLabel("label.MC"));
/* 184 */     gbc.fill = 2;
/* 185 */     GridBagLayoutHelper.add(1, 1, 1.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, this, this.marginCallSpinner);
/* 186 */     gbc.fill = 0;
/* 187 */     GridBagLayoutHelper.add(3, 1, 0.0D, 0.0D, 1, 1, 10, 5, 0, 0, gbc, this, new JLocalizableLabel("label.mc.equity"));
/* 188 */     gbc.fill = 2;
/* 189 */     GridBagLayoutHelper.add(4, 1, 1.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, this, this.mcEquitySpinner);
/* 190 */     gbc.fill = 0;
/* 191 */     GridBagLayoutHelper.add(0, 2, 0.0D, 0.0D, 5, 1, 0, 10, 0, 0, gbc, this, commissionsOvernightsPanel);
/* 192 */     gbc.anchor = 14;
/* 193 */     GridBagLayoutHelper.add(0, 3, 0.0D, 1.0D, 5, 1, 0, 10, 0, 0, gbc, this, dialogButtonsPanel);
/*     */ 
/* 195 */     this.buttonOk.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 198 */         if ((AccountSettingsPanel.this.modalDialog != null) && 
/* 199 */           (AccountSettingsPanel.this.commit())) {
/* 200 */           AccountSettingsPanel.access$402(AccountSettingsPanel.this, true);
/* 201 */           AccountSettingsPanel.this.modalDialog.dispose();
/*     */         }
/*     */       }
/*     */     });
/* 206 */     this.buttonCancel.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 209 */         if (AccountSettingsPanel.this.modalDialog != null) {
/* 210 */           AccountSettingsPanel.access$402(AccountSettingsPanel.this, false);
/* 211 */           AccountSettingsPanel.this.modalDialog.dispose();
/*     */         }
/*     */       } } );
/*     */   }
/*     */ 
/*     */   void set(double initDeposit, String accountCurrency) {
/* 218 */     this.initialDepositSpinner.setValue(Double.valueOf(initDeposit));
/* 219 */     for (int i = 0; i < this.accountCurrencyComboBox.getItemCount(); i++) {
/* 220 */       Currency currency = (Currency)this.accountCurrencyComboBox.getItemAt(i);
/* 221 */       if (currency.getCurrencyCode().equals(accountCurrency)) {
/* 222 */         this.accountCurrencyComboBox.setSelectedIndex(i);
/* 223 */         break;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   void set(int maxLeverage, int mcLeverage, double mcEquity) {
/* 229 */     this.maxLeverageSpinner.setValue(Integer.valueOf(maxLeverage));
/* 230 */     this.marginCallSpinner.setValue(Integer.valueOf(mcLeverage));
/* 231 */     this.mcEquitySpinner.setValue(Double.valueOf(mcEquity));
/*     */   }
/*     */ 
/*     */   void set(Commissions commissions, Overnights overnights) {
/* 235 */     this.tempCommissions = (commissions == null ? new Commissions(false) : commissions);
/* 236 */     this.tempOvernights = (overnights == null ? new Overnights(false) : overnights);
/*     */   }
/*     */ 
/*     */   public Currency getAccountCurrency() {
/* 240 */     return this.commitedCurrency;
/*     */   }
/*     */ 
/*     */   public Integer getMaxLeverage() {
/* 244 */     return this.commitedLeverage;
/*     */   }
/*     */ 
/*     */   public Integer getMcLeverage() {
/* 248 */     return this.commitedMarginCall;
/*     */   }
/*     */ 
/*     */   public double getMcEquity() {
/* 252 */     return this.commitedEquity;
/*     */   }
/*     */ 
/*     */   public double getInitialDeposit() {
/* 256 */     return this.commitedDeposit;
/*     */   }
/*     */ 
/*     */   public Commissions getCommissions() {
/* 260 */     return this.commitedCommissions;
/*     */   }
/*     */ 
/*     */   public Overnights getOvernights() {
/* 264 */     return this.commitedOvernights;
/*     */   }
/*     */ 
/*     */   private boolean commit()
/*     */   {
/* 272 */     this.commitedDeposit = ((Double)this.initialDepositSpinner.getValue()).doubleValue();
/* 273 */     this.commitedEquity = ((Double)this.mcEquitySpinner.getValue()).doubleValue();
/* 274 */     this.commitedMarginCall = ((Integer)this.marginCallSpinner.getValue());
/* 275 */     this.commitedLeverage = ((Integer)this.maxLeverageSpinner.getValue());
/* 276 */     this.commitedCurrency = ((Currency)this.accountCurrencyComboBox.getSelectedItem());
/* 277 */     this.commitedCommissions = this.tempCommissions;
/* 278 */     this.commitedOvernights = this.tempOvernights;
/* 279 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean showModalDialog(Component parent, String title)
/*     */   {
/* 290 */     JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent));
/* 291 */     dialog.setDefaultCloseOperation(2);
/* 292 */     dialog.getContentPane().add(this);
/* 293 */     dialog.setModal(true);
/* 294 */     dialog.setResizable(false);
/* 295 */     dialog.setTitle(title);
/* 296 */     this.modalDialog = dialog;
/*     */     try {
/* 298 */       this.modalResult = false;
/* 299 */       dialog.pack();
/* 300 */       dialog.setLocationRelativeTo(parent);
/* 301 */       dialog.setVisible(true);
/* 302 */       boolean bool = this.modalResult;
/*     */       return bool; } finally { this.modalDialog = null; } throw localObject;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.AccountSettingsPanel
 * JD-Core Version:    0.6.0
 */