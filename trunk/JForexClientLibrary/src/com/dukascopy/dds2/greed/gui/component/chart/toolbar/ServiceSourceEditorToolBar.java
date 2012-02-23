/*     */ package com.dukascopy.dds2.greed.gui.component.chart.toolbar;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.ide.api.ServiceSourceLanguage;
/*     */ import com.dukascopy.dds2.greed.gui.JForexClientFormLayoutManager;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.ServiceSourceEditorPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.actions.ITreeAction;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.actions.TreeActionFactory;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.actions.TreeActionType;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableCheckBox;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableComboBox;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableMenuItem;
/*     */ import com.dukascopy.dds2.greed.gui.resizing.ResizingManager.ComponentSize;
/*     */ import com.dukascopy.dds2.greed.gui.resizing.components.ResizableIcon;
/*     */ import java.awt.Component;
/*     */ import java.awt.Font;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import javax.swing.AbstractAction;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JList;
/*     */ import javax.swing.JPopupMenu;
/*     */ import javax.swing.JSeparator;
/*     */ import javax.swing.ListCellRenderer;
/*     */ import javax.swing.UIManager;
/*     */ import javax.swing.border.CompoundBorder;
/*     */ import javax.swing.border.EmptyBorder;
/*     */ 
/*     */ public class ServiceSourceEditorToolBar extends DockUndockToolBar
/*     */   implements ActionListener
/*     */ {
/*     */   private static final String OPEN_ACTION = "OpenAction";
/*     */   private static final String SAVE_ACTION = "SaveAction";
/*     */   private static final String SAVE_AS_ACTION = "SaveAsAction";
/*     */   private static final String COMPILE_ACTION = "CompileAction";
/*     */   private static final String FIND_ACTION = "FindAction";
/*     */   private static final String REPLACE_ACTION = "ReplaceAction";
/*     */   private static final String SPLIT_ACTION = "SplitAction";
/*     */   private static final String SWITCH_LAGUAGE_ACTION = "SwitchLanguageAction";
/*     */   private static final String TRANSLATE_ACTION = "TranslateAction";
/*     */   private static final String ORGANIZE_IMPORTS_ACTION = "OrganizeImportsAction";
/*     */   private static final String JAVADOC_HELP_ACTION = "JavadocHelpAction";
/*     */   private final Platform[] PLATFORMS;
/*     */   private ServiceSourceEditorPanel strategySourceEditorPanel;
/*     */   private ResizableIcon newIcon;
/*     */   private ResizableIcon openIcon;
/*     */   private ResizableIcon saveIcon;
/*     */   private ResizableIcon saveAsIcon;
/*     */   private ResizableIcon compileIcon;
/*     */   private ResizableIcon translateIcon;
/*     */   private ResizableIcon findIcon;
/*     */   private ResizableIcon replaceIcon;
/*     */   private ResizableIcon organizeImportsIcon;
/*     */   private ResizableIcon javadocHelpIcon;
/*     */   private JButton newButton;
/*     */   private JButton openButton;
/*     */   private JButton saveButton;
/*     */   private JButton saveAsButton;
/*     */   private JButton compileButton;
/*     */   private JButton translateButton;
/*     */   private JButton findButton;
/*     */   private JButton replaceButton;
/*     */   private JButton organizeImportsButton;
/*     */   private JButton javadocHelpButton;
/*     */   private JLocalizableCheckBox isSplitCheckBox;
/*     */   private JLocalizableComboBox switchLaguageBox;
/*     */   private static WorkspaceJTree workspaceJTree;
/*     */ 
/*     */   public ServiceSourceEditorToolBar()
/*     */   {
/*  78 */     this.PLATFORMS = new Platform[] { new Platform("label.jforex", ServiceSourceLanguage.JAVA, true), new Platform("label.mt4", ServiceSourceLanguage.MQ4, true), new Platform("label.mt5", ServiceSourceLanguage.MQ5, false) };
/*     */   }
/*     */ 
/*     */   public void build(ServiceSourceEditorPanel strategySourceEditorPanel)
/*     */   {
/* 115 */     this.strategySourceEditorPanel = strategySourceEditorPanel;
/* 116 */     workspaceJTree = ((JForexClientFormLayoutManager)GreedContext.get("layoutManager")).getWorkspaceJTree();
/*     */ 
/* 118 */     setLayout(new BoxLayout(this, 0));
/* 119 */     CompoundBorder toolBarBorder = BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(), BorderFactory.createEmptyBorder(2, 2, 2, 2));
/*     */ 
/* 123 */     setBorder(toolBarBorder);
/*     */ 
/* 125 */     initIcons();
/*     */ 
/* 127 */     add(Box.createHorizontalStrut(2));
/*     */ 
/* 129 */     initNew();
/* 130 */     add(Box.createHorizontalStrut(2));
/* 131 */     initOpen();
/* 132 */     add(Box.createHorizontalStrut(2));
/*     */ 
/* 134 */     initSave();
/* 135 */     add(Box.createHorizontalStrut(2));
/* 136 */     initSaveAs();
/*     */ 
/* 138 */     addSeparator();
/*     */ 
/* 140 */     initFind();
/* 141 */     add(Box.createHorizontalStrut(2));
/* 142 */     initReplace();
/* 143 */     add(Box.createHorizontalStrut(2));
/* 144 */     initOrganizeImports();
/*     */ 
/* 146 */     addSeparator();
/* 147 */     initSwitchLaguage();
/*     */ 
/* 149 */     add(Box.createHorizontalStrut(2));
/* 150 */     initTranslate();
/* 151 */     initCompile();
/* 152 */     addSeparator();
/* 153 */     initJavadocHelp();
/*     */ 
/* 155 */     addAlwaysOntopButton();
/* 156 */     add(Box.createHorizontalStrut(2));
/*     */ 
/* 158 */     add(Box.createHorizontalGlue());
/*     */ 
/* 160 */     initIsSplit();
/* 161 */     add(Box.createHorizontalStrut(2));
/* 162 */     reloadSplitCheckBox();
/*     */   }
/*     */ 
/*     */   void addSeparator() {
/* 166 */     add(Box.createHorizontalStrut(2));
/*     */ 
/* 168 */     JSeparator separator = new JSeparator(1);
/* 169 */     setSize(separator, SEPARATOR_SIZE);
/* 170 */     add(separator);
/* 171 */     add(Box.createHorizontalStrut(2));
/*     */   }
/*     */ 
/*     */   private void initIcons() {
/* 175 */     this.newIcon = new ResizableIcon("toolbar_editor_new_active.png");
/* 176 */     this.openIcon = new ResizableIcon("toolbar_editor_open_active.png");
/* 177 */     this.saveIcon = new ResizableIcon("toolbar_editor_save_active.png");
/* 178 */     this.saveAsIcon = new ResizableIcon("toolbar_editor_save_as_active.png");
/* 179 */     this.translateIcon = new ResizableIcon("toolbar_editor_translate_active.png");
/* 180 */     this.compileIcon = new ResizableIcon("toolbar_editor_compile_active.png");
/* 181 */     this.findIcon = new ResizableIcon("toolbar_editor_find_active.png");
/* 182 */     this.replaceIcon = new ResizableIcon("toolbar_editor_replace_active.png");
/* 183 */     this.organizeImportsIcon = new ResizableIcon("toolbar_editor_organize_imports_active.png");
/* 184 */     this.javadocHelpIcon = new ResizableIcon("toolbar_editor_javadoc_help_active.png");
/*     */   }
/*     */ 
/*     */   private void initNew()
/*     */   {
/* 189 */     this.newButton = new JLocalizableButton(this.newIcon, ResizingManager.ComponentSize.SIZE_24X24);
/* 190 */     setSize(this.newButton, ResizingManager.ComponentSize.SIZE_24X24.getSize());
/* 191 */     this.newButton.setToolTipText("tooltip.new");
/*     */ 
/* 193 */     JPopupMenu popup = new JPopupMenu();
/* 194 */     popup.add(new JLocalizableMenuItem(new AbstractAction("label.new.strategy") {
/*     */       public void actionPerformed(ActionEvent e) {
/* 196 */         ((JForexClientFormLayoutManager)GreedContext.get("layoutManager")).getTreeActionFactory().createAction(TreeActionType.ADD_TASK, ServiceSourceEditorToolBar.workspaceJTree).execute(null);
/*     */       }
/*     */     }));
/* 201 */     popup.add(new JLocalizableMenuItem(new AbstractAction("label.new.indicator") {
/*     */       public void actionPerformed(ActionEvent e) {
/* 203 */         ((JForexClientFormLayoutManager)GreedContext.get("layoutManager")).getTreeActionFactory().createAction(TreeActionType.ADD_CUST_IND, ServiceSourceEditorToolBar.workspaceJTree).execute(null);
/*     */       }
/*     */     }));
/* 208 */     this.newButton.addMouseListener(new MouseAdapter(popup) {
/*     */       public void mousePressed(MouseEvent e) {
/* 210 */         this.val$popup.show(e.getComponent(), e.getX(), e.getY());
/*     */       }
/*     */     });
/* 214 */     add(this.newButton);
/*     */   }
/*     */ 
/*     */   private void initOpen()
/*     */   {
/* 219 */     this.openButton = new JLocalizableButton(this.openIcon, ResizingManager.ComponentSize.SIZE_24X24);
/* 220 */     setSize(this.openButton, ResizingManager.ComponentSize.SIZE_24X24.getSize());
/* 221 */     this.openButton.setToolTipText("tooltip.open");
/* 222 */     this.openButton.setActionCommand("OpenAction");
/* 223 */     this.openButton.addActionListener(this);
/* 224 */     add(this.openButton);
/*     */   }
/*     */ 
/*     */   private void initSave() {
/* 228 */     this.saveButton = new JLocalizableButton(this.saveIcon, ResizingManager.ComponentSize.SIZE_24X24);
/* 229 */     setSize(this.saveButton, ResizingManager.ComponentSize.SIZE_24X24.getSize());
/* 230 */     this.saveButton.setToolTipText("SAVE_TOOLTIP");
/* 231 */     this.saveButton.setActionCommand("SaveAction");
/* 232 */     this.saveButton.addActionListener(this);
/* 233 */     add(this.saveButton);
/*     */   }
/*     */ 
/*     */   private void initSaveAs() {
/* 237 */     this.saveAsButton = new JLocalizableButton(this.saveAsIcon, ResizingManager.ComponentSize.SIZE_24X24);
/* 238 */     setSize(this.saveAsButton, ResizingManager.ComponentSize.SIZE_24X24.getSize());
/* 239 */     this.saveAsButton.setToolTipText("SAVE_AS_TOOLTIP");
/* 240 */     this.saveAsButton.setActionCommand("SaveAsAction");
/* 241 */     this.saveAsButton.addActionListener(this);
/* 242 */     add(this.saveAsButton);
/*     */   }
/*     */ 
/*     */   private void initSwitchLaguage() {
/* 246 */     this.switchLaguageBox = new JLocalizableComboBox(this.PLATFORMS, ResizingManager.ComponentSize.SIZE_110X24)
/*     */     {
/*     */       public void translate()
/*     */       {
/*     */       }
/*     */     };
/* 252 */     this.switchLaguageBox.setActionCommand("SwitchLanguageAction");
/* 253 */     this.switchLaguageBox.addActionListener(this);
/* 254 */     this.switchLaguageBox.setRenderer(new ComboRenderer());
/* 255 */     this.switchLaguageBox.addActionListener(new ComboListener(this.switchLaguageBox));
/* 256 */     this.switchLaguageBox.setToolTipText("tooltip.environment");
/*     */ 
/* 259 */     if (this.switchLaguageBox != null) {
/* 260 */       if (this.strategySourceEditorPanel.getSourceLanguage() == ServiceSourceLanguage.MQ4)
/* 261 */         this.switchLaguageBox.setSelectedIndex(1);
/* 262 */       else if (this.strategySourceEditorPanel.getSourceLanguage() == ServiceSourceLanguage.MQ5) {
/* 263 */         this.switchLaguageBox.setSelectedIndex(2);
/*     */       }
/*     */       else
/*     */       {
/* 269 */         this.switchLaguageBox.setSelectedIndex(0);
/*     */       }
/*     */     }
/* 272 */     JLocalizableLabel label = new JLocalizableLabel();
/* 273 */     label.setText("label.environment");
/*     */ 
/* 275 */     add(Box.createHorizontalStrut(10));
/* 276 */     add(label);
/* 277 */     add(Box.createHorizontalStrut(5));
/* 278 */     add(this.switchLaguageBox);
/*     */   }
/*     */ 
/*     */   private void initCompile() {
/* 282 */     this.compileButton = new JLocalizableButton(this.compileIcon, ResizingManager.ComponentSize.SIZE_24X24);
/* 283 */     setSize(this.compileButton, ResizingManager.ComponentSize.SIZE_24X24.getSize());
/* 284 */     this.compileButton.setToolTipText("COMPILE_TOOLTIP");
/* 285 */     this.compileButton.setActionCommand("CompileAction");
/* 286 */     this.compileButton.addActionListener(this);
/* 287 */     add(this.compileButton);
/*     */   }
/*     */ 
/*     */   private void initTranslate() {
/* 291 */     this.translateButton = new JLocalizableButton(this.translateIcon, ResizingManager.ComponentSize.SIZE_24X24);
/* 292 */     setSize(this.translateButton, ResizingManager.ComponentSize.SIZE_24X24.getSize());
/* 293 */     this.translateButton.setToolTipText("tooltip.translate");
/* 294 */     this.translateButton.setActionCommand("TranslateAction");
/* 295 */     this.translateButton.addActionListener(this);
/* 296 */     add(this.translateButton);
/*     */   }
/*     */ 
/*     */   private void initFind() {
/* 300 */     this.findButton = new JLocalizableButton(this.findIcon, ResizingManager.ComponentSize.SIZE_24X24);
/* 301 */     setSize(this.findButton, ResizingManager.ComponentSize.SIZE_24X24.getSize());
/* 302 */     this.findButton.setToolTipText("FIND_TOOLTIP");
/* 303 */     this.findButton.setActionCommand("FindAction");
/* 304 */     this.findButton.addActionListener(this);
/* 305 */     add(this.findButton);
/*     */   }
/*     */ 
/*     */   private void initReplace() {
/* 309 */     this.replaceButton = new JLocalizableButton(this.replaceIcon, ResizingManager.ComponentSize.SIZE_24X24);
/* 310 */     setSize(this.replaceButton, ResizingManager.ComponentSize.SIZE_24X24.getSize());
/* 311 */     this.replaceButton.setToolTipText("REPLACE_TOOLTIP");
/* 312 */     this.replaceButton.setActionCommand("ReplaceAction");
/* 313 */     this.replaceButton.addActionListener(this);
/* 314 */     add(this.replaceButton);
/*     */   }
/*     */ 
/*     */   private void initIsSplit() {
/* 318 */     this.isSplitCheckBox = new JLocalizableCheckBox("label.split.editor", "tooltip.split.editor");
/* 319 */     this.isSplitCheckBox.setActionCommand("SplitAction");
/* 320 */     this.isSplitCheckBox.addActionListener(this);
/* 321 */     add(this.isSplitCheckBox);
/*     */   }
/*     */ 
/*     */   private void initOrganizeImports() {
/* 325 */     this.organizeImportsButton = new JLocalizableButton(this.organizeImportsIcon, ResizingManager.ComponentSize.SIZE_24X24);
/* 326 */     setSize(this.organizeImportsButton, ResizingManager.ComponentSize.SIZE_24X24.getSize());
/* 327 */     this.organizeImportsButton.setToolTipText("tooltip.organize.imports");
/* 328 */     this.organizeImportsButton.setActionCommand("OrganizeImportsAction");
/* 329 */     this.organizeImportsButton.addActionListener(this);
/* 330 */     add(this.organizeImportsButton);
/*     */   }
/*     */ 
/*     */   private void initJavadocHelp() {
/* 334 */     this.javadocHelpButton = new JLocalizableButton(this.javadocHelpIcon, ResizingManager.ComponentSize.SIZE_24X24);
/* 335 */     setSize(this.javadocHelpButton, ResizingManager.ComponentSize.SIZE_24X24.getSize());
/* 336 */     this.javadocHelpButton.setToolTipText("tooltip.javadoc.help");
/* 337 */     this.javadocHelpButton.setActionCommand("JavadocHelpAction");
/* 338 */     this.javadocHelpButton.addActionListener(this);
/*     */ 
/* 340 */     this.javadocHelpButton.setOpaque(false);
/* 341 */     this.javadocHelpButton.setContentAreaFilled(false);
/* 342 */     this.javadocHelpButton.setBorderPainted(false);
/*     */ 
/* 344 */     add(this.javadocHelpButton);
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent e) {
/* 348 */     String actionCommand = e.getActionCommand();
/*     */ 
/* 350 */     if (actionCommand.equals("OpenAction")) {
/* 351 */       this.strategySourceEditorPanel.open();
/* 352 */     } else if (actionCommand.equals("SaveAction")) {
/* 353 */       this.strategySourceEditorPanel.save();
/* 354 */     } else if (actionCommand.equals("SaveAsAction")) {
/* 355 */       this.strategySourceEditorPanel.saveAs();
/* 356 */     } else if (actionCommand.equals("SwitchLanguageAction")) {
/* 357 */       this.strategySourceEditorPanel.setSourceLanguage(((Platform)this.switchLaguageBox.getSelectedItem()).getPlatform());
/*     */ 
/* 359 */       reloadSplitCheckBox();
/* 360 */       if ((!((Platform)this.switchLaguageBox.getSelectedItem()).getPlatform().equals(ServiceSourceLanguage.JAVA)) && (this.isSplitCheckBox != null) && (this.isSplitCheckBox.isSelected()))
/*     */       {
/* 363 */         this.strategySourceEditorPanel.expandTranslatedJavaPane();
/*     */       }
/* 365 */       else this.strategySourceEditorPanel.collapseTranslatedJavaPane();
/*     */ 
/*     */     }
/* 368 */     else if (actionCommand.equals("CompileAction")) {
/* 369 */       this.strategySourceEditorPanel.compile();
/* 370 */     } else if (actionCommand.equals("TranslateAction")) {
/* 371 */       if ((this.strategySourceEditorPanel.translate()) && 
/* 372 */         (!this.isSplitCheckBox.isSelected())) this.isSplitCheckBox.setSelected(true);
/*     */     }
/* 374 */     else if (actionCommand.equals("FindAction")) {
/* 375 */       this.strategySourceEditorPanel.find();
/* 376 */     } else if (actionCommand.equals("ReplaceAction")) {
/* 377 */       this.strategySourceEditorPanel.replace();
/* 378 */     } else if (actionCommand.equals("SplitAction")) {
/* 379 */       this.strategySourceEditorPanel.toggleTranslatedJavaPane();
/* 380 */     } else if (actionCommand.equals("OrganizeImportsAction")) {
/* 381 */       this.strategySourceEditorPanel.organizeImports();
/* 382 */     } else if (actionCommand.equals("JavadocHelpAction")) {
/* 383 */       this.strategySourceEditorPanel.toggleJavadocHelpPane();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void reloadSplitCheckBox()
/*     */   {
/* 391 */     if ((this.isSplitCheckBox != null) && (this.translateButton != null))
/* 392 */       if (((Platform)this.switchLaguageBox.getSelectedItem()).getPlatform().equals(ServiceSourceLanguage.JAVA)) {
/* 393 */         this.isSplitCheckBox.setVisible(false);
/* 394 */         this.translateButton.setVisible(false);
/*     */       } else {
/* 396 */         this.isSplitCheckBox.setVisible(true);
/* 397 */         this.translateButton.setVisible(true);
/*     */       }
/*     */   }
/*     */ 
/*     */   public void selectIsSplitCheckBox()
/*     */   {
/* 484 */     if ((this.isSplitCheckBox != null) && (!this.isSplitCheckBox.isSelected()))
/* 485 */       this.isSplitCheckBox.setSelected(true);
/*     */   }
/*     */ 
/*     */   private class Platform
/*     */     implements ServiceSourceEditorToolBar.CanEnable
/*     */   {
/*     */     private String label;
/*     */     private final ServiceSourceLanguage platform;
/*     */     boolean isEnable;
/*     */ 
/*     */     public Platform(String label, ServiceSourceLanguage platform, boolean isEnable)
/*     */     {
/* 463 */       this.platform = platform;
/* 464 */       this.label = label;
/* 465 */       this.isEnable = isEnable;
/*     */     }
/*     */ 
/*     */     public boolean isEnabled() {
/* 469 */       return this.isEnable;
/*     */     }
/*     */ 
/*     */     public void setEnabled(boolean isEnable) {
/* 473 */       this.isEnable = isEnable;
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 477 */       return this.label;
/*     */     }
/*     */     public ServiceSourceLanguage getPlatform() {
/* 480 */       return this.platform;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class ComboListener
/*     */     implements ActionListener
/*     */   {
/*     */     JLocalizableComboBox combo;
/*     */     Object currentItem;
/*     */ 
/*     */     ComboListener(JLocalizableComboBox combo)
/*     */     {
/* 443 */       this.combo = combo;
/* 444 */       this.currentItem = combo.getSelectedItem();
/*     */     }
/*     */ 
/*     */     public void actionPerformed(ActionEvent e) {
/* 448 */       Object tempItem = this.combo.getSelectedItem();
/* 449 */       if (!((ServiceSourceEditorToolBar.CanEnable)tempItem).isEnabled())
/* 450 */         this.combo.setSelectedItem(this.currentItem);
/*     */       else
/* 452 */         this.currentItem = tempItem;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class ComboRenderer extends JLocalizableLabel
/*     */     implements ListCellRenderer
/*     */   {
/*     */     public ComboRenderer()
/*     */     {
/* 411 */       setOpaque(true);
/* 412 */       setBorder(new EmptyBorder(1, 1, 1, 1));
/*     */     }
/*     */ 
/*     */     public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
/*     */     {
/* 417 */       if (isSelected) {
/* 418 */         setBackground(list.getSelectionBackground());
/* 419 */         setForeground(list.getSelectionForeground());
/*     */       } else {
/* 421 */         setBackground(list.getBackground());
/* 422 */         setForeground(list.getForeground());
/*     */       }
/* 424 */       setFont(list.getFont());
/*     */ 
/* 426 */       if (!((ServiceSourceEditorToolBar.CanEnable)value).isEnabled()) {
/* 427 */         setBackground(list.getBackground());
/* 428 */         setForeground(UIManager.getColor("Label.disabledForeground"));
/* 429 */         setFont(new Font(list.getFont().getName(), 2, list.getFont().getSize()));
/*     */       }
/*     */ 
/* 432 */       setText(value == null ? "" : value.toString());
/* 433 */       return this;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static abstract interface CanEnable
/*     */   {
/*     */     public abstract void setEnabled(boolean paramBoolean);
/*     */ 
/*     */     public abstract boolean isEnabled();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.chart.toolbar.ServiceSourceEditorToolBar
 * JD-Core Version:    0.6.0
 */