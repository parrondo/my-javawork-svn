/*     */ package com.dukascopy.dds2.greed.gui.component.settings.advanced;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*     */ import com.dukascopy.dds2.greed.gui.component.message.FontManager;
/*     */ import com.dukascopy.dds2.greed.gui.component.settings.ISettingsPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.settings.SettingsTabbedFrame;
/*     */ import com.dukascopy.dds2.greed.gui.component.status.GreedStatusBar;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableCheckBox;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableRoundedBorder;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.util.FilePathManager;
/*     */ import com.dukascopy.dds2.greed.util.PlatformInitUtils;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.Insets;
/*     */ import java.awt.LayoutManager;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.event.ItemListener;
/*     */ import java.io.File;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JFileChooser;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.event.DocumentEvent;
/*     */ import javax.swing.event.DocumentListener;
/*     */ import javax.swing.text.Document;
/*     */ 
/*     */ public class AdvancedPanel extends JPanel
/*     */   implements ISettingsPanel
/*     */ {
/*  58 */   private final ClientSettingsStorage storage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/*     */   private SettingsTabbedFrame parent;
/*  62 */   private JTextField cachePathTextField = new JTextField();
/*  63 */   private JTextField myStrategiesPathTextField = new JTextField();
/*  64 */   private JTextField myIndicatorsPathTextField = new JTextField();
/*  65 */   private JTextField myWorkspaceSettingsPathTextField = new JTextField();
/*  66 */   private JTextField myChartTemplatesPathTextField = new JTextField();
/*     */ 
/*  68 */   private JLocalizableCheckBox heapSizeCheckBox = new JLocalizableCheckBox("label.show.memory");
/*  69 */   private JLocalizableCheckBox stopStrategyCheckBox = new JLocalizableCheckBox("label.stop.strategy.on.exception");
/*  70 */   private JLocalizableCheckBox deleteSavedCacheFilesCheckBox = new JLocalizableCheckBox("label.delete.cache");
/*  71 */   private JLocalizableCheckBox forceRecconects = new JLocalizableCheckBox("label.force.reconnects");
/*  72 */   private JLocalizableCheckBox monospacedFontCheckBox = new JLocalizableCheckBox("label.monospaced.font");
/*     */ 
/*  74 */   private JLocalizableLabel cachePathLabel = new JLocalizableLabel("label.local.cache.path");
/*  75 */   private JLocalizableLabel myStrategiesPathLabel = new JLocalizableLabel("label.strategies.path");
/*  76 */   private JLocalizableLabel myChartTemplatesPathLabel = new JLocalizableLabel("label.chart.template.path");
/*  77 */   private JLocalizableLabel myIndicatorsPathLabel = new JLocalizableLabel("label.indicators.path");
/*  78 */   private JLocalizableLabel myWorkspaceSettingsPathLabel = new JLocalizableLabel("label.workspacesettings.path");
/*     */ 
/*  80 */   private JButton cachePathButton = new JButton("...");
/*  81 */   private JButton myStrategiesPathButton = new JButton("...");
/*  82 */   private JButton myChartTemplatesPathButton = new JButton("...");
/*  83 */   private JButton myIndicatorsPathButton = new JButton("...");
/*  84 */   private JButton myWorkspaceSettingsPathButton = new JButton("...");
/*     */ 
/*     */   public AdvancedPanel(SettingsTabbedFrame parent) {
/*  87 */     this.parent = parent;
/*  88 */     buildGUI();
/*     */   }
/*     */ 
/*     */   private void buildGUI() {
/*  92 */     adjustWidth();
/*     */ 
/*  94 */     setBorder(new JLocalizableRoundedBorder(this, "border.advanced.settings"));
/*     */ 
/*  96 */     placeComponents();
/*     */ 
/*  98 */     createListenersFor(this.cachePathButton, this.cachePathTextField, FilePathManager.getInstance().DEFAULT_CACHE_FOLDER_NAME);
/*  99 */     createListenersFor(this.myStrategiesPathButton, this.myStrategiesPathTextField, null);
/* 100 */     createListenersFor(this.myChartTemplatesPathButton, this.myChartTemplatesPathTextField, null);
/* 101 */     createListenersFor(this.myIndicatorsPathButton, this.myIndicatorsPathTextField, null);
/* 102 */     createListenersFor(this.myWorkspaceSettingsPathButton, this.myWorkspaceSettingsPathTextField, null);
/*     */ 
/* 104 */     this.heapSizeCheckBox.addItemListener(new ItemListener() {
/*     */       public void itemStateChanged(ItemEvent itemEvent) {
/* 106 */         AdvancedPanel.this.parent.settingsChanged(true);
/*     */       }
/*     */     });
/* 110 */     this.stopStrategyCheckBox.addItemListener(new ItemListener() {
/*     */       public void itemStateChanged(ItemEvent itemEvent) {
/* 112 */         AdvancedPanel.this.parent.settingsChanged(true);
/*     */       }
/*     */     });
/* 116 */     this.deleteSavedCacheFilesCheckBox.addItemListener(new ItemListener() {
/*     */       public void itemStateChanged(ItemEvent itemEvent) {
/* 118 */         AdvancedPanel.this.parent.settingsChanged(true);
/*     */       }
/*     */     });
/* 122 */     this.monospacedFontCheckBox.addItemListener(new ItemListener() {
/*     */       public void itemStateChanged(ItemEvent itemEvent) {
/* 124 */         AdvancedPanel.this.parent.settingsChanged(true);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private void adjustWidth() {
/* 131 */     Dimension preferredSize = this.cachePathButton.getPreferredSize();
/* 132 */     preferredSize.width -= 10;
/* 133 */     this.cachePathButton.setPreferredSize(preferredSize);
/*     */ 
/* 135 */     preferredSize = this.myStrategiesPathButton.getPreferredSize();
/* 136 */     preferredSize.width -= 10;
/* 137 */     this.myStrategiesPathButton.setPreferredSize(preferredSize);
/*     */ 
/* 139 */     preferredSize = this.myChartTemplatesPathButton.getPreferredSize();
/* 140 */     preferredSize.width -= 10;
/* 141 */     this.myChartTemplatesPathButton.setPreferredSize(preferredSize);
/*     */ 
/* 143 */     preferredSize = this.myIndicatorsPathButton.getPreferredSize();
/* 144 */     preferredSize.width -= 10;
/* 145 */     this.myIndicatorsPathButton.setPreferredSize(preferredSize);
/*     */ 
/* 147 */     preferredSize = this.myWorkspaceSettingsPathButton.getPreferredSize();
/* 148 */     preferredSize.width -= 10;
/* 149 */     this.myWorkspaceSettingsPathButton.setPreferredSize(preferredSize);
/*     */ 
/* 152 */     preferredSize = this.cachePathTextField.getPreferredSize();
/* 153 */     preferredSize.width = 380;
/* 154 */     this.cachePathTextField.setPreferredSize(preferredSize);
/*     */ 
/* 156 */     preferredSize = this.myStrategiesPathTextField.getPreferredSize();
/* 157 */     preferredSize.width = 380;
/* 158 */     this.myStrategiesPathTextField.setPreferredSize(preferredSize);
/*     */ 
/* 160 */     preferredSize = this.myChartTemplatesPathTextField.getPreferredSize();
/* 161 */     preferredSize.width = 380;
/* 162 */     this.myChartTemplatesPathTextField.setPreferredSize(preferredSize);
/*     */ 
/* 164 */     preferredSize = this.myIndicatorsPathTextField.getPreferredSize();
/* 165 */     preferredSize.width = 380;
/* 166 */     this.myIndicatorsPathTextField.setPreferredSize(preferredSize);
/*     */ 
/* 168 */     preferredSize = this.myWorkspaceSettingsPathTextField.getPreferredSize();
/* 169 */     preferredSize.width = 380;
/* 170 */     this.myWorkspaceSettingsPathTextField.setPreferredSize(preferredSize);
/*     */ 
/* 172 */     int LABEL_WIDTH_SHIFT = 120;
/*     */ 
/* 174 */     preferredSize = this.cachePathLabel.getPreferredSize();
/* 175 */     preferredSize.width += 120 - preferredSize.width;
/* 176 */     this.cachePathLabel.setPreferredSize(preferredSize);
/*     */ 
/* 178 */     preferredSize = this.myStrategiesPathLabel.getPreferredSize();
/* 179 */     preferredSize.width += 120 - preferredSize.width;
/* 180 */     this.myStrategiesPathLabel.setPreferredSize(preferredSize);
/*     */ 
/* 182 */     preferredSize = this.myChartTemplatesPathLabel.getPreferredSize();
/* 183 */     preferredSize.width += 120 - preferredSize.width;
/* 184 */     this.myChartTemplatesPathLabel.setPreferredSize(preferredSize);
/*     */ 
/* 186 */     preferredSize = this.myIndicatorsPathLabel.getPreferredSize();
/* 187 */     preferredSize.width += 120 - preferredSize.width;
/* 188 */     this.myIndicatorsPathLabel.setPreferredSize(preferredSize);
/*     */ 
/* 190 */     preferredSize = this.myWorkspaceSettingsPathLabel.getPreferredSize();
/* 191 */     preferredSize.width += 120 - preferredSize.width;
/* 192 */     this.myWorkspaceSettingsPathLabel.setPreferredSize(preferredSize);
/*     */   }
/*     */ 
/*     */   private void placeComponents() {
/* 196 */     JPanel cachePathPanel = new JPanel(new FlowLayout(0))
/*     */     {
/*     */     };
/* 203 */     JPanel myStrategiesPathPanel = new JPanel(new FlowLayout(0))
/*     */     {
/*     */     };
/* 210 */     JPanel myChartTemplatesPathPanel = new JPanel(new FlowLayout(0))
/*     */     {
/*     */     };
/* 217 */     JPanel myIndicatorsPathPanel = new JPanel(new FlowLayout(0))
/*     */     {
/*     */     };
/* 224 */     JPanel myWorkspaceSettingsPathPanel = new JPanel(new FlowLayout(0))
/*     */     {
/*     */     };
/* 231 */     JPanel showMemoryPanel = new JPanel(new BorderLayout())
/*     */     {
/*     */     };
/* 265 */     JPanel deleteSavedFilesPanel = new JPanel(new GridLayout(2, 1))
/*     */     {
/*     */     };
/* 272 */     setLayout(new AdvancedPanelLayoutManager(null));
/* 273 */     add(cachePathPanel);
/* 274 */     add(myStrategiesPathPanel);
/* 275 */     add(myIndicatorsPathPanel);
/* 276 */     add(myWorkspaceSettingsPathPanel);
/* 277 */     add(myChartTemplatesPathPanel);
/* 278 */     add(showMemoryPanel);
/*     */ 
/* 280 */     add(deleteSavedFilesPanel);
/* 281 */     add(new JLocalizableLabel("label.require.restart") { } );
/*     */   }
/*     */   private void createListenersFor(JButton pathButton, JTextField pathTextField, String directoryToAdd) {
/* 285 */     pathButton.addActionListener(new ActionListener(pathTextField, directoryToAdd) {
/*     */       public void actionPerformed(ActionEvent e) {
/* 287 */         String path = this.val$pathTextField.getText();
/* 288 */         if (this.val$directoryToAdd != null) {
/* 289 */           if ((path.endsWith(File.separator)) || (path.endsWith("\\")) || (path.endsWith("/"))) {
/* 290 */             path = path.substring(0, path.length() - 1);
/*     */           }
/* 292 */           if (path.endsWith(this.val$directoryToAdd)) {
/* 293 */             path = path.substring(0, path.length() - this.val$directoryToAdd.length());
/*     */           }
/*     */         }
/* 296 */         JFileChooser fileChooser = new JFileChooser(path);
/* 297 */         fileChooser.setMultiSelectionEnabled(false);
/* 298 */         fileChooser.setFileSelectionMode(1);
/* 299 */         int returnOption = fileChooser.showOpenDialog(AdvancedPanel.this.parent);
/* 300 */         if (returnOption == 0) {
/* 301 */           String absolutePath = fileChooser.getSelectedFile().getAbsolutePath();
/* 302 */           if ((this.val$directoryToAdd != null) && 
/* 303 */             (!absolutePath.endsWith(this.val$directoryToAdd))) {
/* 304 */             if ((!absolutePath.endsWith(File.separator)) && (!absolutePath.endsWith("\\")) && (!absolutePath.endsWith("/"))) {
/* 305 */               absolutePath = absolutePath + File.separator;
/*     */             }
/*     */ 
/* 308 */             absolutePath = absolutePath + this.val$directoryToAdd;
/*     */           }
/*     */ 
/* 311 */           this.val$pathTextField.setText(absolutePath);
/*     */         }
/*     */       }
/*     */     });
/* 316 */     pathTextField.getDocument().addDocumentListener(new DocumentListener() {
/*     */       public void insertUpdate(DocumentEvent e) {
/* 318 */         AdvancedPanel.this.parent.settingsChanged(true);
/*     */       }
/*     */       public void removeUpdate(DocumentEvent e) {
/* 321 */         AdvancedPanel.this.parent.settingsChanged(true);
/*     */       }
/*     */       public void changedUpdate(DocumentEvent e) {
/* 324 */         AdvancedPanel.this.parent.settingsChanged(true);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public void resetFields() {
/* 331 */     this.cachePathTextField.setText(this.storage.getLocalCachePath());
/* 332 */     this.myStrategiesPathTextField.setText(this.storage.getMyStrategiesPath());
/* 333 */     this.myChartTemplatesPathTextField.setText(this.storage.getMyChartTemplatesPath());
/* 334 */     this.myIndicatorsPathTextField.setText(this.storage.getMyIndicatorsPath());
/* 335 */     this.myWorkspaceSettingsPathTextField.setText(this.storage.getMyWorkspaceSettingsFolderPath());
/* 336 */     this.heapSizeCheckBox.setSelected(this.storage.getHeapSizeShown());
/* 337 */     this.stopStrategyCheckBox.setSelected(this.storage.getStopStrategyByException());
/* 338 */     this.deleteSavedCacheFilesCheckBox.setSelected(false);
/* 339 */     this.monospacedFontCheckBox.setSelected(this.storage.isConsoleFontMonospaced());
/*     */   }
/*     */ 
/*     */   public void resetToDefaults()
/*     */   {
/* 344 */     this.cachePathTextField.setText(this.storage.getLocalCachePath());
/* 345 */     this.myStrategiesPathTextField.setText(this.storage.getMyStrategiesPath());
/* 346 */     this.myChartTemplatesPathTextField.setText(this.storage.getMyChartTemplatesPath());
/* 347 */     this.myIndicatorsPathTextField.setText(this.storage.getMyIndicatorsPath());
/* 348 */     this.myWorkspaceSettingsPathTextField.setText(this.storage.getMyWorkspaceSettingsFolderPath());
/*     */ 
/* 350 */     this.heapSizeCheckBox.setSelected(false);
/* 351 */     this.stopStrategyCheckBox.setSelected(true);
/* 352 */     this.deleteSavedCacheFilesCheckBox.setSelected(false);
/* 353 */     this.monospacedFontCheckBox.setSelected(false);
/* 354 */     FontManager.reinitFonts();
/*     */   }
/*     */ 
/*     */   public boolean verifySettings() {
/* 358 */     boolean cachePathIsOk = verifyPath(this.cachePathTextField);
/* 359 */     boolean myStrategiesPathIsOk = verifyPath(this.myStrategiesPathTextField);
/* 360 */     boolean myChartTemplatesPathIsOk = verifyPath(this.myChartTemplatesPathTextField);
/* 361 */     boolean myIndicatorsPathIsOk = verifyPath(this.myIndicatorsPathTextField);
/* 362 */     boolean myWorkspaceSettingsPathIsOk = verifyPath(this.myWorkspaceSettingsPathTextField);
/* 363 */     return (cachePathIsOk) && (myStrategiesPathIsOk) && (myIndicatorsPathIsOk) && (myWorkspaceSettingsPathIsOk) && (myChartTemplatesPathIsOk);
/*     */   }
/*     */ 
/*     */   private boolean verifyPath(JTextField pathTextField) {
/* 367 */     String cachePath = pathTextField.getText();
/* 368 */     File cacheDirFile = new File(cachePath);
/* 369 */     if (!cacheDirFile.exists()) {
/* 370 */       if (!cacheDirFile.mkdirs()) {
/* 371 */         pathTextField.requestFocus();
/* 372 */         pathTextField.selectAll();
/* 373 */         return false;
/*     */       }
/* 375 */     } else if (!cacheDirFile.isDirectory()) {
/* 376 */       pathTextField.requestFocus();
/* 377 */       pathTextField.selectAll();
/* 378 */       return false;
/*     */     }
/* 380 */     return true;
/*     */   }
/*     */ 
/*     */   public void applySettings() {
/* 384 */     this.storage.saveLocalCachePath(this.cachePathTextField.getText());
/* 385 */     this.storage.saveMyStrategiesPath(this.myStrategiesPathTextField.getText());
/* 386 */     this.storage.saveMyIndicatorsPath(this.myIndicatorsPathTextField.getText());
/* 387 */     this.storage.saveMyWorkspaceSettingsFolderPath(this.myWorkspaceSettingsPathTextField.getText());
/* 388 */     this.storage.saveMyChartTemplatesPath(this.myChartTemplatesPathTextField.getText());
/* 389 */     this.storage.saveHeapSizeShown(this.heapSizeCheckBox.isSelected());
/* 390 */     ((ClientForm)GreedContext.get("clientGui")).getStatusBar().setHeapSizePanelVisible(this.heapSizeCheckBox.isSelected());
/* 391 */     this.storage.saveConsoleFontMonospaced(this.monospacedFontCheckBox.isSelected());
/* 392 */     FontManager.reinitFonts();
/* 393 */     this.storage.saveStopStrategyByException(this.stopStrategyCheckBox.isSelected());
/* 394 */     if (this.deleteSavedCacheFilesCheckBox.isSelected()) {
/* 395 */       PlatformInitUtils.deleteCacheAndSettingsFilesAndRestartPlatform(this.deleteSavedCacheFilesCheckBox.isSelected());
/*     */     }
/* 397 */     this.storage.saveForceReconnectsActive(this.forceRecconects.isSelected());
/*     */   }
/*     */ 
/*     */   private static final class AdvancedPanelLayoutManager implements LayoutManager
/*     */   {
/*     */     public void addLayoutComponent(String name, Component comp)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void removeLayoutComponent(Component comp)
/*     */     {
/*     */     }
/*     */ 
/*     */     public Dimension preferredLayoutSize(Container parent) {
/* 411 */       return new Dimension(300, 250);
/*     */     }
/*     */ 
/*     */     public Dimension minimumLayoutSize(Container parent) {
/* 415 */       return new Dimension(300, 250);
/*     */     }
/*     */ 
/*     */     public void layoutContainer(Container container)
/*     */     {
/* 420 */       int count = container.getComponentCount();
/* 421 */       Insets containerInsets = container.getInsets();
/*     */ 
/* 423 */       int curYPos = containerInsets.top;
/* 424 */       int childWidth = container.getWidth() - containerInsets.right - 15;
/* 425 */       for (int i = 0; i < count; i++) {
/* 426 */         Component child = container.getComponent(i);
/* 427 */         if (!child.isVisible()) {
/*     */           continue;
/*     */         }
/* 430 */         int childHeight = (int)child.getPreferredSize().getHeight();
/* 431 */         if ("cachePathPanel".equals(child.getName())) {
/* 432 */           child.setBounds(containerInsets.left + 5, curYPos, childWidth, childHeight);
/* 433 */         } else if ("myStrategiesPathPanel".equals(child.getName())) {
/* 434 */           child.setBounds(containerInsets.left + 5, curYPos, childWidth, childHeight);
/* 435 */         } else if ("myIndicatorsPathPanel".equals(child.getName())) {
/* 436 */           childHeight += 2;
/* 437 */           child.setBounds(containerInsets.left + 5, curYPos, childWidth, childHeight);
/* 438 */         } else if ("myWorkspaceSettingsPathPanel".equals(child.getName())) {
/* 439 */           childHeight += 2;
/* 440 */           child.setBounds(containerInsets.left + 5, curYPos, childWidth, childHeight);
/* 441 */         } else if ("myChartTemplatesPathPanel".equals(child.getName())) {
/* 442 */           childHeight += 2;
/* 443 */           child.setBounds(containerInsets.left + 5, curYPos, childWidth, childHeight);
/* 444 */         } else if ("showMemoryPanel".equals(child.getName())) {
/* 445 */           childHeight += 2;
/* 446 */           child.setBounds(containerInsets.left, curYPos, childWidth, childHeight);
/* 447 */         } else if ("notePanel".equals(child.getName())) {
/* 448 */           childHeight += 5;
/* 449 */           child.setBounds(containerInsets.left + 5, curYPos, childWidth, childHeight);
/* 450 */         } else if ("deleteFilesPanel".equals(child.getName())) {
/* 451 */           childHeight += 5;
/* 452 */           child.setBounds(containerInsets.left, curYPos, childWidth, childHeight);
/* 453 */         } else if ("hintLabel".equals(child.getName())) {
/* 454 */           child.setBounds(containerInsets.left + 9, container.getHeight() - childHeight - containerInsets.bottom, childWidth, childHeight);
/*     */         }
/*     */ 
/* 457 */         curYPos += childHeight;
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.settings.advanced.AdvancedPanel
 * JD-Core Version:    0.6.0
 */