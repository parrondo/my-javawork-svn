/*     */ package com.dukascopy.dds2.greed.gui.component.settings.period.panel;
/*     */ 
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.component.JRoundedBorder;
/*     */ import com.dukascopy.dds2.greed.gui.component.settings.FormBuilder;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableRoundedBorder;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JList;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.ListSelectionModel;
/*     */ import javax.swing.event.ListSelectionEvent;
/*     */ import javax.swing.event.ListSelectionListener;
/*     */ 
/*     */ public abstract class AbstractModePanel extends JPanel
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private CustomPeriodListPanel customPeriodTablePanel;
/*     */   private JRoundedBorder roundedBorder;
/*     */   private JPanel addDeleteCustomPeriodButtonPanel;
/*     */   private JButton btnAdd;
/*     */   private JButton btnDelete;
/*     */   private final String title;
/*     */   private final List<JForexPeriod> defaultPeriods;
/*     */ 
/*     */   public AbstractModePanel(String title, List<JForexPeriod> defaultPeriods)
/*     */   {
/*  41 */     this.title = title;
/*  42 */     this.defaultPeriods = defaultPeriods;
/*     */ 
/*  44 */     init();
/*     */   }
/*     */ 
/*     */   private void init() {
/*  48 */     setLayout(new BorderLayout());
/*     */ 
/*  50 */     add(getLeftPanel(), "West");
/*  51 */     add(getCustomPeriodTablePanel(), "Center");
/*     */ 
/*  53 */     setBorder(getRoundedBorder()); } 
/*     */   protected abstract JPanel getLeftPanel();
/*     */ 
/*     */   protected abstract JForexPeriod getSelectedPeriod();
/*     */ 
/*  60 */   protected CustomPeriodListPanel getCustomPeriodTablePanel() { if (this.customPeriodTablePanel == null) {
/*  61 */       this.customPeriodTablePanel = new CustomPeriodListPanel();
/*  62 */       this.customPeriodTablePanel.add(getAddDeleteCustomPeriodButtonPanel(), "West");
/*     */ 
/*  65 */       this.customPeriodTablePanel.getPeriodsList().getSelectionModel().addListSelectionListener(new ListSelectionListener()
/*     */       {
/*     */         public void valueChanged(ListSelectionEvent e) {
/*  68 */           AbstractModePanel.this.getBtnDelete().setEnabled(AbstractModePanel.this.customPeriodTablePanel.getPeriodsList().getSelectedValues().length != 0);
/*     */         }
/*     */       });
/*     */     }
/*  73 */     return this.customPeriodTablePanel; }
/*     */ 
/*     */   public List<JForexPeriod> getSubscribedPeriods()
/*     */   {
/*  77 */     return getCustomPeriodTablePanel().getPeriods();
/*     */   }
/*     */ 
/*     */   public void setSubscribedPeriods(List<JForexPeriod> subscribedPeriods) {
/*  81 */     getCustomPeriodTablePanel().setPeriods(subscribedPeriods);
/*     */   }
/*     */ 
/*     */   protected JRoundedBorder getRoundedBorder() {
/*  85 */     if (this.roundedBorder == null) {
/*  86 */       this.roundedBorder = new JLocalizableRoundedBorder(this, getTitle());
/*     */     }
/*  88 */     return this.roundedBorder;
/*     */   }
/*     */ 
/*     */   protected JPanel getAddDeleteCustomPeriodButtonPanel()
/*     */   {
/*  93 */     if (this.addDeleteCustomPeriodButtonPanel == null) {
/*  94 */       this.addDeleteCustomPeriodButtonPanel = new JPanel(new GridBagLayout());
/*     */ 
/*  96 */       FormBuilder fb = new FormBuilder(this.addDeleteCustomPeriodButtonPanel);
/*     */ 
/*  98 */       fb.addFirstField(Box.createGlue());
/*  99 */       fb.addMiddleField(getBtnAdd());
/* 100 */       fb.addLastField(Box.createGlue());
/* 101 */       fb.startNewRow();
/*     */ 
/* 103 */       fb.addFirstField(Box.createGlue());
/* 104 */       fb.addMiddleField(getBtnDelete());
/* 105 */       fb.addLastField(Box.createGlue());
/* 106 */       fb.startNewRow();
/*     */     }
/*     */ 
/* 109 */     return this.addDeleteCustomPeriodButtonPanel;
/*     */   }
/*     */ 
/*     */   protected JForexPeriod addPeriod(JForexPeriod period) {
/* 113 */     if ((period != null) && (!getCustomPeriodTablePanel().periodAlreadyAdded(period))) {
/* 114 */       getCustomPeriodTablePanel().addPeriod(period);
/* 115 */       return period;
/*     */     }
/* 117 */     return null;
/*     */   }
/*     */ 
/*     */   public void addPeriods(List<JForexPeriod> periods) {
/* 121 */     getCustomPeriodTablePanel().addPeriods(periods);
/*     */   }
/*     */ 
/*     */   protected void deletePeriods(List<JForexPeriod> periods) {
/* 125 */     getCustomPeriodTablePanel().removePeriods(periods);
/*     */   }
/*     */ 
/*     */   protected JButton getBtnAdd() {
/* 129 */     if (this.btnAdd == null) {
/* 130 */       this.btnAdd = new JButton(">>");
/* 131 */       this.btnAdd.addActionListener(new ActionListener()
/*     */       {
/*     */         public void actionPerformed(ActionEvent e) {
/* 134 */           AbstractModePanel.this.addPeriod(AbstractModePanel.this.getSelectedPeriod());
/*     */         }
/*     */       });
/* 137 */       this.btnAdd.setEnabled(false);
/*     */     }
/* 139 */     return this.btnAdd;
/*     */   }
/*     */ 
/*     */   protected JButton getBtnDelete() {
/* 143 */     if (this.btnDelete == null) {
/* 144 */       this.btnDelete = new JButton("<<");
/* 145 */       this.btnDelete.addActionListener(new ActionListener()
/*     */       {
/*     */         public void actionPerformed(ActionEvent e) {
/* 148 */           AbstractModePanel.this.deletePeriods(AbstractModePanel.this.getCustomPeriodTablePanel().getSelectedPeriods());
/*     */         }
/*     */       });
/* 151 */       this.btnDelete.setEnabled(false);
/*     */     }
/* 153 */     return this.btnDelete;
/*     */   }
/*     */ 
/*     */   protected String getTitle() {
/* 157 */     return this.title;
/*     */   }
/*     */ 
/*     */   public void resetPeriodsToDefaults() {
/* 161 */     getCustomPeriodTablePanel().clear();
/*     */ 
/* 163 */     List periods = new ArrayList();
/* 164 */     periods.addAll(getDefaultPeriods());
/*     */ 
/* 169 */     List inUsePeriods = CustomPeriodsRenderer.getInUsePeriods();
/* 170 */     for (JForexPeriod period : inUsePeriods) {
/* 171 */       if (!periods.contains(period)) {
/* 172 */         periods.add(period);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 179 */     periods = ((ClientSettingsStorage)GreedContext.get("settingsStorage")).sortChartPeriods(periods);
/*     */ 
/* 181 */     getCustomPeriodTablePanel().addPeriods(periods);
/*     */   }
/*     */ 
/*     */   protected List<JForexPeriod> getDefaultPeriods() {
/* 185 */     return this.defaultPeriods;
/*     */   }
/*     */ 
/*     */   public List<JForexPeriod> getPresentedPeriods() {
/* 189 */     return getCustomPeriodTablePanel().getPresentedPeriods();
/*     */   }
/*     */ 
/*     */   public void clear() {
/* 193 */     getCustomPeriodTablePanel().clear();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.settings.period.panel.AbstractModePanel
 * JD-Core Version:    0.6.0
 */