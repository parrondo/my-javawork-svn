/*     */ package com.dukascopy.charts.dialogs.indicators;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ import com.dukascopy.charts.chartbuilder.GuiRefresher;
/*     */ import com.dukascopy.charts.chartbuilder.IDataManagerAndIndicatorsContainer;
/*     */ import com.dukascopy.charts.dialogs.indicators.listener.IndicatorChangeListener.ParameterType;
/*     */ import com.dukascopy.charts.utils.helper.LocalizedMessageHelper;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.util.List;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JPanel;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class EditIndicatorDialog extends AddEditIndicatorDialog
/*     */ {
/*  30 */   private static final Logger LOGGER = LoggerFactory.getLogger(EditIndicatorDialog.class);
/*     */   private JPanel contentPane;
/*     */ 
/*     */   public EditIndicatorDialog(int subPanelId, IndicatorWrapper indicatorWrapper, JFrame parentFrame, IDataManagerAndIndicatorsContainer dataManagerAndIndicatorsContainer, GuiRefresher guiRefresher, Period period, DataType dataType)
/*     */   {
/*  44 */     super(parentFrame, dataManagerAndIndicatorsContainer, indicatorWrapper, guiRefresher, period, dataType, subPanelId);
/*  45 */     reinitParametersPanel();
/*  46 */     positionWindowAndMakeItVisible();
/*     */   }
/*     */ 
/*     */   protected void adjustSize() {
/*  50 */     Dimension setupPaneSize = this.setupPanel.getPreferredSize();
/*  51 */     int levelsPanelHeight = this.levelsPanel == null ? 30 : this.levelsPanel.getPreferredSize().height;
/*     */ 
/*  53 */     int width = setupPaneSize.width;
/*  54 */     int height = setupPaneSize.height + levelsPanelHeight + this.generalParamsPanel.getPreferredSize().height + 30;
/*  55 */     setMinimumSize(new Dimension(width, height));
/*     */   }
/*     */ 
/*     */   protected void build()
/*     */   {
/*  62 */     boolean isTicks = (this.period == Period.TICK) && (this.dataType == DataType.TICKS);
/*     */ 
/*  64 */     this.setupPanel = new IndicatorSetupPanel(this, this.guiRefresher, this.indicatorsContainer, isTicks, false);
/*     */ 
/*  72 */     this.addEditButton = new JButton(LocalizationManager.getText("button.ok"))
/*     */     {
/*     */     };
/*  77 */     this.cancelButton = new JButton(LocalizationManager.getText("button.cancel"))
/*     */     {
/*     */     };
/*  81 */     createGUI();
/*     */   }
/*     */ 
/*     */   protected void reinitParametersPanel() {
/*  85 */     this.copyOfOriginalIndicator = this.indicatorWrapper.clone();
/*  86 */     this.setupPanel.setIndicator(this.indicatorWrapper);
/*  87 */     validate();
/*     */   }
/*     */ 
/*     */   private void createGUI() {
/*  91 */     this.contentPane = new JPanel(new GridBagLayout());
/*  92 */     GridBagConstraints gbc = new GridBagConstraints();
/*  93 */     layoutParametersPanel(this.contentPane, gbc);
/*  94 */     if (this.indicatorWrapper.isLevelsEnabled()) {
/*  95 */       this.levelsPanel = new IndicatorLevelsPanel(this, this.indicatorWrapper, this.guiRefresher, this.indicatorsContainer, this.setupPanel);
/*  96 */       layoutLevelsPanel(this.contentPane, gbc);
/*     */     }
/*     */ 
/*  99 */     this.generalParamsPanel = new IndicatorGeneralParamsPanel(this.indicatorWrapper);
/* 100 */     layoutGeneralParamsPanel(this.contentPane, gbc);
/*     */ 
/* 102 */     layoutButtons(this.contentPane, gbc);
/* 103 */     setContentPane(this.contentPane);
/*     */   }
/*     */ 
/*     */   private void layoutButtons(JPanel contentPane, GridBagConstraints gbc) {
/* 107 */     JPanel buttonsPanel = new JPanel(new GridBagLayout());
/* 108 */     gbc.fill = 3;
/* 109 */     gbc.anchor = 10;
/* 110 */     gbc.gridx = 0;
/* 111 */     gbc.gridy = 3;
/* 112 */     gbc.weightx = 1.0D;
/* 113 */     gbc.weighty = 0.0D;
/* 114 */     gbc.insets.left = 5;
/* 115 */     gbc.insets.top = 5;
/* 116 */     gbc.insets.right = 5;
/* 117 */     gbc.insets.bottom = 5;
/* 118 */     contentPane.add(buttonsPanel, gbc);
/*     */ 
/* 120 */     gbc.fill = 3;
/* 121 */     gbc.anchor = 10;
/* 122 */     gbc.gridx = 0;
/* 123 */     gbc.gridy = 0;
/* 124 */     gbc.weightx = 0.0D;
/* 125 */     gbc.weighty = 0.0D;
/* 126 */     gbc.gridwidth = 1;
/* 127 */     gbc.gridheight = 1;
/* 128 */     gbc.insets.left = 5;
/* 129 */     gbc.insets.top = 0;
/* 130 */     gbc.insets.right = 0;
/* 131 */     gbc.insets.bottom = 0;
/* 132 */     buttonsPanel.add(this.addEditButton, gbc);
/*     */ 
/* 134 */     gbc.fill = 3;
/* 135 */     gbc.anchor = 10;
/* 136 */     gbc.gridx = 1;
/* 137 */     gbc.gridy = 0;
/* 138 */     gbc.weightx = 0.0D;
/* 139 */     gbc.weighty = 0.0D;
/* 140 */     gbc.insets.left = 5;
/* 141 */     gbc.insets.top = 0;
/* 142 */     gbc.insets.right = 5;
/* 143 */     gbc.insets.bottom = 0;
/* 144 */     buttonsPanel.add(this.cancelButton, gbc);
/*     */   }
/*     */ 
/*     */   protected void layoutParametersPanel(JPanel contentPane, GridBagConstraints gbc) {
/* 148 */     gbc.fill = 1;
/* 149 */     gbc.gridx = 0;
/* 150 */     gbc.gridy = 0;
/* 151 */     gbc.weightx = 0.0D;
/* 152 */     gbc.weighty = 0.0D;
/* 153 */     gbc.insets.left = 0;
/* 154 */     gbc.insets.top = 0;
/* 155 */     gbc.insets.right = 0;
/* 156 */     gbc.insets.bottom = 0;
/* 157 */     contentPane.add(this.setupPanel, gbc);
/*     */   }
/*     */   protected void layoutLevelsPanel(JPanel contentPane, GridBagConstraints gbc) {
/* 160 */     gbc.fill = 1;
/* 161 */     gbc.gridx = 0;
/* 162 */     gbc.gridy = 1;
/* 163 */     gbc.weightx = 0.0D;
/* 164 */     gbc.weighty = 1.0D;
/* 165 */     gbc.insets.left = 0;
/* 166 */     gbc.insets.top = 0;
/* 167 */     gbc.insets.right = 0;
/* 168 */     gbc.insets.bottom = 0;
/* 169 */     contentPane.add(this.levelsPanel, gbc);
/*     */   }
/*     */ 
/*     */   protected void layoutGeneralParamsPanel(JPanel contentPane, GridBagConstraints gbc) {
/* 173 */     gbc.fill = 1;
/* 174 */     gbc.gridx = 0;
/* 175 */     gbc.gridy = 2;
/* 176 */     gbc.weightx = 0.0D;
/* 177 */     gbc.weighty = 0.0D;
/* 178 */     gbc.insets.left = 0;
/* 179 */     gbc.insets.top = 0;
/* 180 */     gbc.insets.right = 0;
/* 181 */     gbc.insets.bottom = 0;
/*     */ 
/* 183 */     contentPane.add(this.generalParamsPanel, gbc);
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent e) {
/* 187 */     if (e.getActionCommand().equals("cancel")) {
/* 188 */       processCancelAction();
/* 189 */       this.indicatorWrapper = null;
/* 190 */       finish();
/* 191 */       this.setupPanel.setIndicator(null);
/* 192 */     } else if (e.getActionCommand().equals("add_edit")) {
/* 193 */       if ((this.levelsPanel != null) && (this.indicatorWrapper.isLevelsEnabled())) {
/* 194 */         List levels = this.levelsPanel.indicatorLevelsController.getLevels();
/* 195 */         Double identicalLevelValue = this.levelsPanel.indicatorLevelsController.getIdenticalLevelValue(levels);
/* 196 */         if (identicalLevelValue == null) {
/* 197 */           this.indicatorWrapper.setLevelInfoList(levels);
/* 198 */           this.setupPanel.indicatorChanged(IndicatorChangeListener.ParameterType.LEVEL);
/* 199 */           finish();
/* 200 */           this.setupPanel.setIndicator(null);
/*     */         }
/*     */         else {
/* 203 */           String text = "level.value.num.has.to.be.unique";
/* 204 */           LocalizedMessageHelper.showInformtionMessage(this, LocalizationManager.getTextWithArguments(text, new Object[] { identicalLevelValue }));
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 209 */         finish();
/* 210 */         this.setupPanel.setIndicator(null);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void setTitle() {
/* 216 */     setTitle(LocalizationManager.getText("title.edit.indicator"));
/*     */   }
/*     */ 
/*     */   protected void processCancelAction() {
/* 220 */     if (LOGGER.isDebugEnabled()) {
/* 221 */       LOGGER.debug("Canceling editing indicator: " + this.indicatorWrapper.getId());
/*     */     }
/* 223 */     if (this.indicatorWrapper != null) {
/* 224 */       if (this.copyOfOriginalIndicator != null) {
/* 225 */         this.indicatorWrapper.copySettingsFrom(this.copyOfOriginalIndicator);
/*     */       }
/* 227 */       if (this.levelsPanel != null) {
/* 228 */         this.levelsPanel.indicatorLevelsController.reset();
/* 229 */         this.indicatorWrapper.setLevelInfoList(this.levelsPanel.indicatorLevelsController.getLevels());
/* 230 */         this.indicatorsContainer.editIndicator(this.indicatorWrapper, this.subChartId, false);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.dialogs.indicators.EditIndicatorDialog
 * JD-Core Version:    0.6.0
 */