/*     */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.preset;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI.RoundedBorder;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI.StrategiesButton;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI.StrategiesComboBoxUI;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI.StrategyComponentWrapper;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI.StrategyLabel;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyNewBean;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.parameter.StrategyParamsPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.properties.IStrategyPropertiesPanelBuilder;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.properties.StrategyPropertiesPanelBuilder;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.toolbar.StrategiesToolbarUIConstants;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*     */ import com.dukascopy.dds2.greed.gui.resizing.components.ResizableIcon;
/*     */ import com.dukascopy.dds2.greed.util.GridBagLayoutHelper;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.Font;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.Insets;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.event.ItemListener;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.ComboBoxEditor;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ 
/*     */ public class StrategyPresetsDialog extends JDialog
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  44 */   private static final Dimension DIALOG_SIZE = new Dimension(450, 450);
/*     */   private IStrategyPresetsController presetsController;
/*     */   private IStrategyPropertiesPanelBuilder propertiesBuilder;
/*     */   private StrategyNewBean strategy;
/*     */   private JComboBox presetComboBox;
/*     */   private StrategyParamsPanel parametersPanel;
/*     */   private GridBagConstraints gbc;
/*  56 */   private boolean setParams = false;
/*     */ 
/*     */   public StrategyPresetsDialog(StrategyNewBean strategy, boolean runAfter) {
/*  59 */     this.strategy = strategy;
/*     */ 
/*  62 */     for (StrategyPreset preset : strategy.getStrategyPresets()) {
/*  63 */       preset.saveTemporalState();
/*     */     }
/*     */ 
/*  66 */     this.presetsController = new DefaultStrategyPresetsController();
/*  67 */     this.propertiesBuilder = new StrategyPropertiesPanelBuilder();
/*     */ 
/*  69 */     getContentPane().setLayout(new GridBagLayout());
/*     */ 
/*  71 */     setModal(true);
/*  72 */     setResizable(false);
/*  73 */     setTitle(LocalizationManager.getText("strategy.parameters.dialog.title"));
/*  74 */     setSize(DIALOG_SIZE);
/*  75 */     setLocationRelativeTo(null);
/*     */ 
/*  77 */     setDefaultCloseOperation(2);
/*     */ 
/*  79 */     this.gbc = new GridBagConstraints();
/*  80 */     this.gbc.insets.left = 10;
/*  81 */     this.gbc.insets.right = 10;
/*  82 */     this.gbc.insets.top = 10;
/*     */ 
/*  84 */     buildPresetsPanel();
/*     */ 
/*  86 */     buildParametersPanel();
/*     */ 
/*  88 */     buildButtonsPanel(runAfter);
/*     */ 
/*  90 */     setVisible(true);
/*     */   }
/*     */ 
/*     */   public boolean doSetParams() {
/*  94 */     return this.setParams;
/*     */   }
/*     */ 
/*     */   private void buildParametersPanel()
/*     */   {
/*  99 */     JPanel labelsPanel = new JPanel();
/* 100 */     labelsPanel.setLayout(new FlowLayout(1, 4, 0));
/*     */ 
/* 102 */     JLabel varLabel = new JLocalizableLabel(LocalizationManager.getText("strategy.parameters.dialog.variable"));
/* 103 */     varLabel.setFont(varLabel.getFont().deriveFont(1));
/* 104 */     varLabel.setPreferredSize(StrategyLabel.LABEL_SIZE);
/* 105 */     varLabel.setHorizontalAlignment(4);
/* 106 */     labelsPanel.add(varLabel);
/*     */ 
/* 108 */     JLabel valLabel = new JLocalizableLabel(LocalizationManager.getText("strategy.parameters.dialog.value"));
/* 109 */     valLabel.setFont(valLabel.getFont().deriveFont(1));
/* 110 */     valLabel.setPreferredSize(StrategyComponentWrapper.MAXIMUM_DIZE);
/* 111 */     labelsPanel.add(valLabel);
/*     */ 
/* 113 */     GridBagLayoutHelper.add(0, 1, 1.0D, 0.0D, 2, this.gbc, getContentPane(), labelsPanel);
/*     */ 
/* 115 */     this.parametersPanel = this.propertiesBuilder.buildParametersPanel(this.presetComboBox, this.strategy, (StrategyPreset)this.presetComboBox.getSelectedItem());
/*     */ 
/* 117 */     JScrollPane scrollPane = new JScrollPane(this.parametersPanel);
/*     */ 
/* 119 */     scrollPane.setBorder(new RoundedBorder());
/* 120 */     scrollPane.getInsets().set(10, 10, 10, 10);
/*     */ 
/* 122 */     JPanel borderPanel = new JPanel();
/* 123 */     borderPanel.setLayout(new BoxLayout(borderPanel, 1));
/* 124 */     borderPanel.add(scrollPane);
/* 125 */     borderPanel.add(Box.createVerticalGlue());
/*     */ 
/* 127 */     GridBagLayoutHelper.add(0, 2, 1.0D, 1.0D, 1, this.gbc, getContentPane(), borderPanel);
/*     */   }
/*     */ 
/*     */   private void buildButtonsPanel(boolean runAfter)
/*     */   {
/* 132 */     JPanel buttonsPanel = new JPanel();
/* 133 */     buttonsPanel.setLayout(new FlowLayout(1, 10, 5));
/*     */ 
/* 135 */     JButton setParamsButton = new JLocalizableButton();
/* 136 */     setParamsButton.setText(runAfter ? LocalizationManager.getText("strategy.parameters.dialog.button.run") : LocalizationManager.getText("strategy.parameters.dialog.button.set"));
/*     */ 
/* 140 */     setParamsButton.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 145 */         if (StrategyPresetsDialog.this.presetsController.parametersValid(StrategyPresetsDialog.this.parametersPanel))
/*     */         {
/* 148 */           StrategyPreset activePreset = (StrategyPreset)StrategyPresetsDialog.this.presetComboBox.getSelectedItem();
/* 149 */           StrategyPresetsDialog.this.strategy.setActivePreset(activePreset);
/*     */ 
/* 151 */           for (StrategyPreset preset : StrategyPresetsDialog.this.strategy.getStrategyPresets()) {
/* 152 */             preset.removeTemporalState();
/*     */           }
/*     */ 
/* 155 */           StrategyPresetsDialog.access$402(StrategyPresetsDialog.this, true);
/* 156 */           StrategyPresetsDialog.this.dispose();
/*     */         }
/*     */       }
/*     */     });
/* 161 */     buttonsPanel.add(setParamsButton);
/*     */ 
/* 163 */     JButton cancelButton = new JLocalizableButton(LocalizationManager.getText("strategy.parameters.dialog.button.cancel"));
/* 164 */     cancelButton.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 169 */         for (StrategyPreset preset : StrategyPresetsDialog.this.strategy.getStrategyPresets()) {
/* 170 */           preset.resetFromTemporalState();
/*     */         }
/*     */ 
/* 173 */         StrategyPresetsDialog.access$402(StrategyPresetsDialog.this, false);
/* 174 */         StrategyPresetsDialog.this.dispose();
/*     */       }
/*     */     });
/* 178 */     buttonsPanel.add(cancelButton);
/*     */ 
/* 180 */     this.gbc.insets.bottom = 10;
/* 181 */     GridBagLayoutHelper.add(0, 3, 0.0D, 0.0D, 2, this.gbc, getContentPane(), buttonsPanel);
/*     */   }
/*     */ 
/*     */   private void buildPresetsPanel() {
/* 185 */     JPanel presetContainerPanel = new JPanel();
/* 186 */     presetContainerPanel.setOpaque(false);
/* 187 */     presetContainerPanel.setLayout(new FlowLayout(1, 2, 2));
/*     */ 
/* 189 */     JLabel label = new JLabel();
/* 190 */     label.setText(LocalizationManager.getText("strategy.parameters.dialog.preset"));
/* 191 */     label.setFont(label.getFont().deriveFont(1));
/* 192 */     label.setHorizontalAlignment(4);
/* 193 */     label.setPreferredSize(StrategyLabel.LABEL_SIZE);
/*     */ 
/* 195 */     presetContainerPanel.add(label);
/* 196 */     presetContainerPanel.add(Box.createHorizontalStrut(3));
/*     */ 
/* 198 */     this.presetComboBox = new JComboBox();
/* 199 */     this.presetComboBox.setUI(new StrategiesComboBoxUI());
/* 200 */     this.presetComboBox.setPreferredSize(new Dimension(100, 22));
/* 201 */     this.presetComboBox.setEditable(true);
/*     */ 
/* 203 */     this.presetComboBox.setModel(new StrategyPresetsComboBoxModel(this.strategy.getStrategyPresets()));
/* 204 */     this.presetComboBox.setSelectedItem(this.strategy.getActivePreset());
/*     */ 
/* 206 */     ComboBoxEditor editor = this.presetComboBox.getEditor();
/*     */ 
/* 208 */     editor.addActionListener(new ActionListener(editor)
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 211 */         StrategyPresetsDialog.this.presetComboBox.setSelectedItem(this.val$editor.getItem());
/*     */ 
/* 213 */         StrategyPresetsComboBoxModel comboBoxModel = (StrategyPresetsComboBoxModel)StrategyPresetsDialog.this.presetComboBox.getModel();
/* 214 */         StrategyPresetsDialog.this.presetsController.savePreset(StrategyPresetsDialog.this.strategy, comboBoxModel, StrategyPresetsDialog.this.parametersPanel);
/*     */ 
/* 216 */         StrategyPresetsDialog.this.presetComboBox.setSelectedItem(comboBoxModel.getElementAt(comboBoxModel.getSize() - 1));
/*     */       }
/*     */     });
/* 220 */     this.presetComboBox.addItemListener(new ItemListener()
/*     */     {
/*     */       public void itemStateChanged(ItemEvent e) {
/* 223 */         if (e.getStateChange() == 1)
/*     */         {
/* 225 */           Object selectedItem = StrategyPresetsDialog.this.presetComboBox.getSelectedItem();
/*     */ 
/* 227 */           if (((selectedItem instanceof StrategyPreset)) && 
/* 228 */             (StrategyPresetsDialog.this.parametersPanel != null)) {
/* 229 */             StrategyPreset selectedPreset = (StrategyPreset)selectedItem;
/* 230 */             StrategyPresetsDialog.this.propertiesBuilder.updateParametersPanel(StrategyPresetsDialog.this.parametersPanel, selectedPreset.getStrategyParameters());
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/* 237 */     presetContainerPanel.add(this.presetComboBox);
/*     */ 
/* 239 */     StrategiesButton savePresetButton = new StrategiesButton(StrategiesToolbarUIConstants.PRESET_SAVE_ICON, StrategiesToolbarUIConstants.PRESET_SAVE_ICON);
/*     */ 
/* 242 */     savePresetButton.setToolTipKey("strategies.button.save.preset");
/* 243 */     savePresetButton.setPreferredSize(new Dimension(StrategiesToolbarUIConstants.PRESET_SAVE_ICON.getIconWidth() + 10, (int)savePresetButton.getPreferredSize().getHeight()));
/*     */ 
/* 246 */     savePresetButton.addActionListener(new Object()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 249 */         StrategyPresetsComboBoxModel comboBoxModel = (StrategyPresetsComboBoxModel)StrategyPresetsDialog.this.presetComboBox.getModel();
/* 250 */         StrategyPresetsDialog.this.presetsController.savePreset(StrategyPresetsDialog.this.strategy, comboBoxModel, StrategyPresetsDialog.this.parametersPanel);
/*     */ 
/* 253 */         Object selectedItem = StrategyPresetsDialog.this.presetComboBox.getSelectedItem();
/* 254 */         if ((selectedItem instanceof StrategyPreset))
/* 255 */           StrategyPresetsDialog.this.presetComboBox.getEditor().setItem(selectedItem);
/*     */       }
/*     */     });
/* 261 */     StrategiesButton removePresetButton = new StrategiesButton(StrategiesToolbarUIConstants.PRESET_REMOVE_ICON, StrategiesToolbarUIConstants.PRESET_REMOVE_ICON);
/*     */ 
/* 263 */     removePresetButton.setToolTipKey("strategies.button.remove.preset");
/* 264 */     removePresetButton.setPreferredSize(new Dimension(StrategiesToolbarUIConstants.PRESET_REMOVE_ICON.getIconWidth() + 10, (int)removePresetButton.getPreferredSize().getHeight()));
/*     */ 
/* 267 */     removePresetButton.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 270 */         StrategyPresetsComboBoxModel comboBoxModel = (StrategyPresetsComboBoxModel)StrategyPresetsDialog.this.presetComboBox.getModel();
/* 271 */         StrategyPresetsDialog.this.presetsController.deletePreset(StrategyPresetsDialog.this.strategy, comboBoxModel);
/*     */       }
/*     */     });
/* 275 */     StrategiesButton restorePresetButton = new StrategiesButton(StrategiesToolbarUIConstants.PRESET_RESTORE_ICON, StrategiesToolbarUIConstants.PRESET_RESTORE_FADED_ICON);
/*     */ 
/* 277 */     restorePresetButton.setToolTipKey("strategies.button.restore.preset");
/* 278 */     restorePresetButton.setPreferredSize(new Dimension(StrategiesToolbarUIConstants.STRATEGIES_NEW_ICON.getIconWidth() + 10, (int)restorePresetButton.getPreferredSize().getHeight()));
/*     */ 
/* 281 */     restorePresetButton.addActionListener(new Object()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 284 */         Object selectedItem = StrategyPresetsDialog.this.presetComboBox.getSelectedItem();
/* 285 */         if ((selectedItem instanceof StrategyPreset)) {
/* 286 */           StrategyPreset selectedPreset = (StrategyPreset)selectedItem;
/*     */ 
/* 288 */           if (selectedPreset.isModified()) {
/* 289 */             selectedPreset.restorePreset();
/* 290 */             StrategyPresetsDialog.this.presetComboBox.getEditor().setItem(selectedItem);
/* 291 */             StrategyPresetsDialog.this.propertiesBuilder.updateParametersPanel(StrategyPresetsDialog.this.parametersPanel, selectedPreset.getStrategyParameters());
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/* 297 */     presetContainerPanel.add(savePresetButton);
/* 298 */     presetContainerPanel.add(removePresetButton);
/* 299 */     presetContainerPanel.add(restorePresetButton);
/*     */ 
/* 301 */     GridBagLayoutHelper.add(0, 0, 1.0D, 0.0D, 2, this.gbc, getContentPane(), presetContainerPanel);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.preset.StrategyPresetsDialog
 * JD-Core Version:    0.6.0
 */