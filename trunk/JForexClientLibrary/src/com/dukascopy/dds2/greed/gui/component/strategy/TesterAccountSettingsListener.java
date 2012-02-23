/*    */ package com.dukascopy.dds2.greed.gui.component.strategy;
/*    */ 
/*    */ import com.dukascopy.api.system.Commissions;
/*    */ import com.dukascopy.api.system.Overnights;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ import java.util.Currency;
/*    */ 
/*    */ public class TesterAccountSettingsListener
/*    */   implements ActionListener
/*    */ {
/*    */   private final TesterParameters testerParameters;
/*    */   private final StrategyTestPanel strategyTestPanel;
/*    */ 
/*    */   public TesterAccountSettingsListener(TesterParameters testerParameters, StrategyTestPanel strategyTestPanel)
/*    */   {
/* 19 */     this.testerParameters = testerParameters;
/* 20 */     this.strategyTestPanel = strategyTestPanel;
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent e)
/*    */   {
/* 25 */     AccountSettingsPanel panel = new AccountSettingsPanel();
/* 26 */     panel.set(this.testerParameters.getInitialDeposit(), this.testerParameters.getAccountCurrency().getCurrencyCode());
/* 27 */     panel.set(this.testerParameters.getMaxLeverage(), this.testerParameters.getMcLeverage(), this.testerParameters.getMcEquity());
/* 28 */     panel.set(this.testerParameters.getCommissions(), this.testerParameters.getOvernights());
/*    */ 
/* 30 */     if (panel.showModalDialog(this.strategyTestPanel, LocalizationManager.getText("dialog.tester.account"))) {
/* 31 */       double initialDeposit = panel.getInitialDeposit();
/* 32 */       Integer maxLeverage = panel.getMaxLeverage();
/* 33 */       Integer mcLeverage = panel.getMcLeverage();
/* 34 */       double mcEquity = panel.getMcEquity();
/* 35 */       Commissions commissions = panel.getCommissions();
/* 36 */       Overnights overnights = panel.getOvernights();
/* 37 */       if (!panel.getAccountCurrency().equals(this.testerParameters.getAccountCurrency())) {
/* 38 */         Currency accountCurrency = panel.getAccountCurrency();
/* 39 */         this.testerParameters.setAccountCurrency(accountCurrency);
/*    */       }
/*    */ 
/* 42 */       this.testerParameters.setInitialDeposit(initialDeposit);
/* 43 */       this.testerParameters.setMaxLeverage(maxLeverage.intValue());
/* 44 */       this.testerParameters.setMcLeverage(mcLeverage.intValue());
/* 45 */       this.testerParameters.setMcEquity(mcEquity);
/* 46 */       this.testerParameters.setCommissions(commissions);
/* 47 */       this.testerParameters.setOvernights(overnights);
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.TesterAccountSettingsListener
 * JD-Core Version:    0.6.0
 */