/*     */ package com.dukascopy.charts.dialogs.drawings;
/*     */ 
/*     */ import com.dukascopy.charts.chartbuilder.GuiRefresher;
/*     */ import com.dukascopy.charts.dialogs.AbsoluteLayout;
/*     */ import com.dukascopy.charts.dialogs.AbsoluteLayoutConstraints;
/*     */ import com.dukascopy.charts.drawings.IRetracementLevels;
/*     */ import com.dukascopy.charts.main.DDSChartsControllerImpl;
/*     */ import com.dukascopy.charts.main.interfaces.DDSChartsController;
/*     */ import com.dukascopy.charts.persistence.IChartClient;
/*     */ import com.dukascopy.charts.utils.helper.LocalizedMessageHelper;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Color;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Insets;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.ContainerEvent;
/*     */ import java.awt.event.ContainerListener;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.ComboBoxEditor;
/*     */ import javax.swing.DefaultComboBoxModel;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.ChangeListener;
/*     */ import javax.swing.event.DocumentEvent;
/*     */ import javax.swing.event.DocumentListener;
/*     */ import javax.swing.text.Document;
/*     */ 
/*     */ public class RetracementLevelsDialog extends JDialog
/*     */ {
/*     */   static final int LEVELS_PANEL_WIDTH = 265;
/*  46 */   public static final Dimension DIALOG_SIZE = new Dimension(400, 320);
/*     */   IRetracementLevels retracementChartObj;
/*     */   GuiRefresher guiRefresher;
/*     */   RetracementLevelsController retracementLevelsController;
/*     */   IChartClient chartClient;
/*  55 */   JPanel mainPanel = new JPanel();
/*  56 */   JPanel presetsPanel = new JPanel();
/*  57 */   JScrollPane levelsScrollPane = new JScrollPane();
/*  58 */   JPanel levelsPanel = new JPanel();
/*  59 */   JPanel internalPanel = new JPanel();
/*     */ 
/*  61 */   JComboBox presetsCombo = new JComboBox();
/*  62 */   JLocalizableButton savePresetButton = new JLocalizableButton(StratUtils.loadImageIcon("rc/media/tree_charts_save_preset_active.png"));
/*  63 */   JLocalizableButton deletePresetButton = new JLocalizableButton(StratUtils.loadImageIcon("rc/media/tree_charts_delete_preset_active.png"));
/*     */ 
/*  65 */   JLocalizableButton addButton = new JLocalizableButton("button.add");
/*  66 */   JLocalizableButton deleteButton = new JLocalizableButton("button.remove");
/*  67 */   JLocalizableButton okButton = new JLocalizableButton("button.ok");
/*  68 */   JLocalizableButton cancelButton = new JLocalizableButton("button.cancel");
/*     */   Map<String, List<Object[]>> presets;
/*     */ 
/*     */   public RetracementLevelsDialog(JFrame parent, IRetracementLevels retracementChartObj, GuiRefresher guiRefresher, Color defaultColor, boolean allowAllPercents)
/*     */   {
/*  79 */     super(parent);
/*  80 */     setModal(true);
/*  81 */     this.retracementChartObj = retracementChartObj;
/*  82 */     this.guiRefresher = guiRefresher;
/*     */ 
/*  84 */     this.chartClient = DDSChartsControllerImpl.getInstance().getChartClient();
/*     */ 
/*  86 */     this.presets = this.chartClient.restoreHorizontalRetracementPresets(retracementChartObj.getType());
/*     */ 
/*  88 */     initPresetsPanel();
/*  89 */     initLevelsScrollPane();
/*  90 */     initInternalPanel();
/*  91 */     createButtonListeners();
/*  92 */     addComponentsToMainPanel(this.levelsScrollPane);
/*     */ 
/*  94 */     setTitle(LocalizationManager.getText("dialog.title.edit.levels"));
/*  95 */     getContentPane().add(this.mainPanel);
/*  96 */     setPreferredSize(DIALOG_SIZE);
/*  97 */     setResizable(false);
/*     */ 
/*  99 */     ChangeListener changeListener = new ChangeListener() {
/*     */       public void stateChanged(ChangeEvent e) {
/* 101 */         if ((e != null) && ((e.getSource() instanceof Boolean))) {
/* 102 */           RetracementLevelsDialog.this.deleteButton.setEnabled(((Boolean)e.getSource()).booleanValue());
/*     */         }
/*     */         else {
/* 105 */           RetracementLevelsDialog.this.retracementChartObj.setLevels(RetracementLevelsDialog.this.retracementLevelsController.getLevels());
/* 106 */           RetracementLevelsDialog.this.guiRefresher.repaintMainContent();
/*     */         }
/*     */       }
/*     */     };
/* 111 */     this.retracementLevelsController = new RetracementLevelsController(retracementChartObj.getLevels(), defaultColor, this.levelsPanel, changeListener, 265, allowAllPercents);
/*     */   }
/*     */ 
/*     */   void initInternalPanel()
/*     */   {
/* 122 */     this.internalPanel.setLayout(new BoxLayout(this.internalPanel, 1));
/* 123 */     this.internalPanel.setPreferredSize(new Dimension(265, 200));
/* 124 */     addHeader(this.internalPanel);
/* 125 */     addLevels(this.internalPanel);
/*     */   }
/*     */ 
/*     */   void initLevelsScrollPane() {
/* 129 */     this.levelsScrollPane.setPreferredSize(new Dimension(265, 220));
/* 130 */     this.levelsScrollPane.setVerticalScrollBarPolicy(20);
/* 131 */     this.levelsScrollPane.setHorizontalScrollBarPolicy(31);
/* 132 */     this.levelsScrollPane.setViewportView(this.internalPanel);
/*     */   }
/*     */ 
/*     */   void addComponentsToMainPanel(JScrollPane levelsScrollPane) {
/* 136 */     this.mainPanel.setLayout(new AbsoluteLayout());
/*     */ 
/* 138 */     this.mainPanel.add(this.presetsPanel, new AbsoluteLayoutConstraints(20, 15, 265, 20));
/* 139 */     this.mainPanel.add(levelsScrollPane, new AbsoluteLayoutConstraints(20, 45, 265, 220));
/* 140 */     this.mainPanel.add(this.addButton, new AbsoluteLayoutConstraints(300, 50, 80, 20));
/* 141 */     this.mainPanel.add(this.deleteButton, new AbsoluteLayoutConstraints(300, 90, 80, 20));
/* 142 */     this.mainPanel.add(this.okButton, new AbsoluteLayoutConstraints(77, 273, 80, 20));
/* 143 */     this.mainPanel.add(this.cancelButton, new AbsoluteLayoutConstraints(192, 273, 80, 20));
/*     */   }
/*     */ 
/*     */   void createButtonListeners() {
/* 147 */     this.addButton.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/* 149 */         RetracementLevelsDialog.this.retracementLevelsController.addLevel();
/* 150 */         RetracementLevelsDialog.this.okButton.setEnabled(true);
/* 151 */         RetracementLevelsDialog.this.addButton.setEnabled(true);
/* 152 */         RetracementLevelsDialog.this.retracementChartObj.setLevels(RetracementLevelsDialog.this.retracementLevelsController.getLevels());
/* 153 */         RetracementLevelsDialog.this.guiRefresher.repaintMainContent();
/*     */       }
/*     */     });
/* 157 */     this.deleteButton.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/* 159 */         if (RetracementLevelsDialog.this.retracementLevelsController.deleteLevels()) {
/* 160 */           RetracementLevelsDialog.this.okButton.setEnabled(false);
/* 161 */           RetracementLevelsDialog.this.addButton.setEnabled(false);
/*     */         }
/* 163 */         RetracementLevelsDialog.this.retracementChartObj.setLevels(RetracementLevelsDialog.this.retracementLevelsController.getLevels());
/* 164 */         RetracementLevelsDialog.this.guiRefresher.repaintMainContent();
/*     */ 
/* 166 */         RetracementLevelsDialog.this.deleteButton.setEnabled(RetracementLevelsDialog.this.retracementLevelsController.doesAnySelectionExist());
/*     */       }
/*     */     });
/* 170 */     this.cancelButton.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/* 172 */         RetracementLevelsDialog.this.retracementLevelsController.reset();
/* 173 */         RetracementLevelsDialog.this.retracementChartObj.setLevels(RetracementLevelsDialog.this.retracementLevelsController.getLevels());
/* 174 */         RetracementLevelsDialog.this.guiRefresher.repaintMainContent();
/* 175 */         RetracementLevelsDialog.this.dispose();
/*     */       }
/*     */     });
/* 179 */     this.okButton.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/* 181 */         List levels = RetracementLevelsDialog.this.retracementLevelsController.getLevels();
/* 182 */         Double identicalLevelValue = RetracementLevelsDialog.this.retracementLevelsController.getIdenticalLevelValue(levels);
/* 183 */         if (identicalLevelValue == null) {
/* 184 */           RetracementLevelsDialog.this.retracementChartObj.setLevels(levels);
/* 185 */           RetracementLevelsDialog.this.guiRefresher.repaintMainContent();
/* 186 */           RetracementLevelsDialog.this.dispose();
/*     */         }
/*     */         else {
/* 189 */           String text = RetracementLevelsDialog.this.retracementChartObj.isLevelValuesInPercents() ? "level.value.has.to.be.unique" : "level.value.num.has.to.be.unique";
/*     */ 
/* 191 */           LocalizedMessageHelper.showInformtionMessage(RetracementLevelsDialog.this, LocalizationManager.getTextWithArguments(text, new Object[] { Double.valueOf(identicalLevelValue.doubleValue() * 100.0D) }));
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   void addHeader(JPanel internalPanel)
/*     */   {
/* 200 */     JPanel levelsHeaderPanel = new JPanel();
/* 201 */     levelsHeaderPanel.setLayout(new AbsoluteLayout());
/* 202 */     levelsHeaderPanel.add(new JLocalizableLabel("label"), new AbsoluteLayoutConstraints(30, 0, 80, 20));
/* 203 */     String levelCaption = this.retracementChartObj.isLevelValuesInPercents() ? "level.percent" : "label.caption.level";
/* 204 */     levelsHeaderPanel.add(new JLocalizableLabel(levelCaption), new AbsoluteLayoutConstraints(100, 0, 80, 20));
/* 205 */     levelsHeaderPanel.setMaximumSize(new Dimension(265, 20));
/* 206 */     internalPanel.add(levelsHeaderPanel);
/*     */   }
/*     */ 
/*     */   void addLevels(JPanel internalPanel) {
/* 210 */     this.levelsPanel.setLayout(new BoxLayout(this.levelsPanel, 1));
/* 211 */     this.levelsPanel.setPreferredSize(new Dimension(265, 20));
/* 212 */     this.levelsPanel.addContainerListener(new ContainerListener(internalPanel) {
/*     */       public void componentAdded(ContainerEvent e) {
/* 214 */         adjustHeight();
/*     */       }
/*     */ 
/*     */       public void componentRemoved(ContainerEvent e) {
/* 218 */         adjustHeight();
/*     */       }
/*     */ 
/*     */       private void adjustHeight() {
/* 222 */         int count = RetracementLevelsDialog.this.levelsPanel.getComponentCount();
/* 223 */         RetracementLevelsDialog.this.levelsPanel.setMaximumSize(new Dimension(265, count * 23));
/* 224 */         RetracementLevelsDialog.this.levelsPanel.setMinimumSize(new Dimension(265, count * 23));
/* 225 */         this.val$internalPanel.setPreferredSize(new Dimension(265, count * 23 + 20));
/*     */       }
/*     */     });
/* 229 */     internalPanel.add(this.levelsPanel);
/*     */   }
/*     */ 
/*     */   void initPresetsPanel() {
/* 233 */     this.presetsPanel.setLayout(new BorderLayout(5, 5));
/* 234 */     this.presetsPanel.add(new JLocalizableLabel("label.presets"), "Before");
/*     */ 
/* 236 */     int count = this.presets.keySet().size();
/* 237 */     String[] items = (String[])this.presets.keySet().toArray(new String[count]);
/* 238 */     this.presetsCombo.setModel(new DefaultComboBoxModel(items));
/* 239 */     this.presetsCombo.setEditable(true);
/* 240 */     this.presetsCombo.setSelectedIndex(-1);
/*     */ 
/* 242 */     for (String presetName : items) {
/* 243 */       List presetLevels = (List)this.presets.get(presetName);
/* 244 */       if (this.retracementChartObj.compareLevels(this.retracementChartObj.getLevels(), presetLevels)) {
/* 245 */         this.presetsCombo.setSelectedItem(presetName);
/* 246 */         if (!"Default".equals(presetName)) break;
/* 247 */         this.savePresetButton.setEnabled(false);
/* 248 */         this.deletePresetButton.setEnabled(false); break;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 254 */     this.presetsPanel.add(this.presetsCombo, "Center");
/*     */ 
/* 256 */     JPanel buttonsPanel = new JPanel(new BorderLayout(5, 5));
/*     */ 
/* 258 */     this.savePresetButton.setMargin(new Insets(1, 1, 1, 1));
/* 259 */     this.savePresetButton.setToolTipKey("button.preset.save");
/* 260 */     buttonsPanel.add(this.savePresetButton, "Center");
/*     */ 
/* 262 */     this.deletePresetButton.setMargin(new Insets(1, 1, 1, 1));
/* 263 */     this.deletePresetButton.setToolTipKey("button.preset.remove");
/* 264 */     buttonsPanel.add(this.deletePresetButton, "After");
/*     */ 
/* 266 */     this.presetsPanel.add(buttonsPanel, "After");
/*     */ 
/* 270 */     this.presetsCombo.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 274 */         String key = (String)RetracementLevelsDialog.this.presetsCombo.getSelectedItem();
/* 275 */         List levels = (List)RetracementLevelsDialog.this.presets.get(key);
/* 276 */         if (levels != null) {
/* 277 */           RetracementLevelsDialog.this.retracementLevelsController.set(levels);
/*     */         }
/*     */ 
/* 280 */         RetracementLevelsDialog.this.savePresetButton.setEnabled(!key.equals("Default"));
/* 281 */         RetracementLevelsDialog.this.deletePresetButton.setEnabled(!key.equals("Default"));
/*     */       }
/*     */     });
/* 285 */     JTextField textField = (JTextField)this.presetsCombo.getEditor().getEditorComponent();
/* 286 */     textField.getDocument().addDocumentListener(new DocumentListener()
/*     */     {
/*     */       public void removeUpdate(DocumentEvent e) {
/* 289 */         RetracementLevelsDialog.this.checkNewComboBoxText();
/*     */       }
/*     */ 
/*     */       public void insertUpdate(DocumentEvent e) {
/* 293 */         RetracementLevelsDialog.this.checkNewComboBoxText();
/*     */       }
/*     */ 
/*     */       public void changedUpdate(DocumentEvent e) {
/* 297 */         RetracementLevelsDialog.this.checkNewComboBoxText();
/*     */       }
/*     */     });
/* 301 */     this.savePresetButton.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 305 */         String name = (String)RetracementLevelsDialog.this.presetsCombo.getSelectedItem();
/*     */ 
/* 307 */         List levels = RetracementLevelsDialog.this.retracementLevelsController.getLevels(true);
/*     */ 
/* 310 */         Iterator it = RetracementLevelsDialog.this.presets.keySet().iterator();
/* 311 */         while (it.hasNext()) {
/* 312 */           String presetName = (String)it.next();
/*     */ 
/* 314 */           if (!presetName.equals(name))
/*     */           {
/* 316 */             List presetLevels = (List)RetracementLevelsDialog.this.presets.get(presetName);
/*     */ 
/* 318 */             if (RetracementLevelsDialog.this.retracementChartObj.compareLevels(levels, presetLevels))
/*     */             {
/* 320 */               LocalizedMessageHelper.showErrorMessage(RetracementLevelsDialog.this, LocalizationManager.getTextWithArguments("preset.levels.already.exist", new Object[] { presetName }));
/*     */ 
/* 324 */               return;
/*     */             }
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 330 */         if (!RetracementLevelsDialog.this.presets.containsKey(name)) {
/* 331 */           RetracementLevelsDialog.this.presetsCombo.addItem(name);
/*     */         }
/*     */ 
/* 334 */         RetracementLevelsDialog.this.presets.put(name, levels);
/* 335 */         RetracementLevelsDialog.this.chartClient.saveHorizontalRetracementPresets(RetracementLevelsDialog.this.retracementChartObj.getType(), RetracementLevelsDialog.this.presets);
/*     */       }
/*     */     });
/* 339 */     this.deletePresetButton.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 342 */         String presetName = (String)RetracementLevelsDialog.this.presetsCombo.getSelectedItem();
/* 343 */         RetracementLevelsDialog.this.presets.remove(presetName);
/* 344 */         RetracementLevelsDialog.this.chartClient.saveHorizontalRetracementPresets(RetracementLevelsDialog.this.retracementChartObj.getType(), RetracementLevelsDialog.this.presets);
/* 345 */         RetracementLevelsDialog.this.presetsCombo.removeItem(presetName);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private void checkNewComboBoxText() {
/* 352 */     String text = ((JTextField)this.presetsCombo.getEditor().getEditorComponent()).getText();
/*     */ 
/* 354 */     if (("Default".equalsIgnoreCase(text)) || (text.trim().isEmpty())) {
/* 355 */       this.savePresetButton.setEnabled(false);
/* 356 */       this.deletePresetButton.setEnabled(false);
/* 357 */       return;
/*     */     }
/*     */ 
/* 360 */     if (!this.savePresetButton.isEnabled()) {
/* 361 */       this.savePresetButton.setEnabled(true);
/*     */     }
/*     */ 
/* 364 */     this.deletePresetButton.setEnabled(false);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.dialogs.drawings.RetracementLevelsDialog
 * JD-Core Version:    0.6.0
 */