/*     */ package com.dukascopy.dds2.greed.gui.component.settings;
/*     */ 
/*     */ import com.dukascopy.charts.main.interfaces.DDSChartsController;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*     */ import com.dukascopy.dds2.greed.gui.component.BasicDecoratedFrame;
/*     */ import com.dukascopy.dds2.greed.gui.component.settings.advanced.AdvancedPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.settings.chart.ChartSettingsPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.settings.disclaimer.DisclaimerSettingsPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.settings.general.GeneralPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.settings.period.PeriodSettingsPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.settings.theme.ThemeSettingsPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.settings.workspace.WorkspaceSettingsPanel;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage.FrameType;
/*     */ import com.dukascopy.dds2.greed.gui.util.tabs.HeadersPanel;
/*     */ import com.dukascopy.dds2.greed.gui.util.tabs.TabbedPanel;
/*     */ import com.dukascopy.dds2.greed.util.PlatformSpecific;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.LayoutManager;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.swing.ImageIcon;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JPanel;
/*     */ 
/*     */ public class SettingsTabbedFrame extends BasicDecoratedFrame
/*     */   implements PlatformSpecific
/*     */ {
/*  54 */   private static final Dimension MINIMUM_SIZE = new Dimension(650, 525);
/*  55 */   private static final Dimension MACOSX_MINIMUM_SIZE = new Dimension(690, 560);
/*  56 */   private static final Dimension LINUX_MINIMUM_SIZE = new Dimension(690, 560);
/*     */ 
/*  58 */   private static final ImageIcon GENERAL_TAB_ICON = (ImageIcon)StratUtils.loadIcon("rc/media/preferences_general.png");
/*  59 */   private static final ImageIcon ADVANCED_TAB_ICON = (ImageIcon)StratUtils.loadIcon("rc/media/preferences_advanced.png");
/*  60 */   private static final ImageIcon THEME_TAB_ICON = (ImageIcon)StratUtils.loadIcon("rc/media/preferences_themes.png");
/*  61 */   private static final ImageIcon CHARTS_TAB_ICON = (ImageIcon)StratUtils.loadIcon("rc/media/preferences_charts.png");
/*  62 */   private static final ImageIcon PERDIOS_TAB_ICON = (ImageIcon)StratUtils.loadIcon("rc/media/preferences_custom_period.png");
/*  63 */   private static final ImageIcon WORKSPACE_TAB_ICON = (ImageIcon)StratUtils.loadIcon("rc/media/preferences_workspace.png");
/*  64 */   private static final ImageIcon DISCLAIMER_TAB_ICON = (ImageIcon)StratUtils.loadIcon("rc/media/preferences_disclaimer.png");
/*     */   private final TabbedPanel tabbedPanel;
/*  68 */   private static SettingsTabbedFrame instance = null;
/*     */ 
/*  70 */   private final List<ISettingsPanel> settingsPanels = new ArrayList();
/*     */ 
/*  72 */   private final JLocalizableButton okButton = new JLocalizableButton("button.ok");
/*  73 */   private final JLocalizableButton cancelButton = new JLocalizableButton("button.cancel");
/*  74 */   private final JLocalizableButton defaultButton = new JLocalizableButton("button.default");
/*     */   private GeneralPanel generalPanel;
/*     */ 
/*     */   public static SettingsTabbedFrame getInstance()
/*     */   {
/*  79 */     instance = (SettingsTabbedFrame)GreedContext.get("preferences.frame");
/*  80 */     if (instance == null) {
/*  81 */       instance = new SettingsTabbedFrame();
/*  82 */       GreedContext.putInSingleton("preferences.frame", instance);
/*     */     }
/*     */ 
/*  85 */     instance.setLocationRelativeTo((JFrame)GreedContext.get("clientGui"));
/*     */ 
/*  89 */     return instance;
/*     */   }
/*     */ 
/*     */   public void setVisible(boolean b)
/*     */   {
/*  96 */     resetFields();
/*  97 */     this.okButton.setEnabled(false);
/*  98 */     super.setVisible(b);
/*     */   }
/*     */ 
/*     */   public void dispose()
/*     */   {
/* 103 */     ClientSettingsStorage settingsStorage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/* 104 */     settingsStorage.saveFrameDimension(ClientSettingsStorage.FrameType.PREFERENCES, getSize());
/* 105 */     super.dispose();
/*     */   }
/*     */ 
/*     */   private SettingsTabbedFrame()
/*     */   {
/* 111 */     setTitle("frame.preferences");
/*     */ 
/* 113 */     this.tabbedPanel = new TabbedPanel();
/*     */ 
/* 115 */     addGeneralPanel();
/* 116 */     addChartSettingsPanel();
/* 117 */     addPeriodSettingsPanel();
/* 118 */     addThemeSettingsPanel();
/* 119 */     addWorkspaceSettingsPanel();
/* 120 */     addDisclaimerSettingPanel();
/* 121 */     addAdvancedSettingsPanel();
/*     */ 
/* 123 */     getContentPane().add(this.tabbedPanel, "Center");
/*     */ 
/* 125 */     Dimension buttonsSize = new Dimension(this.cancelButton.getPreferredSize().width + 30, this.cancelButton.getPreferredSize().height);
/*     */ 
/* 127 */     this.okButton.setPreferredSize(buttonsSize);
/* 128 */     this.okButton.setMnemonic(79);
/* 129 */     this.cancelButton.setPreferredSize(buttonsSize);
/* 130 */     this.cancelButton.setMnemonic(67);
/* 131 */     this.defaultButton.setPreferredSize(buttonsSize);
/* 132 */     this.defaultButton.setMnemonic(68);
/*     */ 
/* 134 */     getContentPane().add(new JPanel(new FlowLayout(1, 30, 10))
/*     */     {
/*     */     }
/*     */     , "South");
/*     */ 
/* 142 */     if (MACOSX)
/* 143 */       setMinimumSize(MACOSX_MINIMUM_SIZE);
/* 144 */     else if (LINUX)
/* 145 */       setMinimumSize(LINUX_MINIMUM_SIZE);
/*     */     else {
/* 147 */       setMinimumSize(MINIMUM_SIZE);
/*     */     }
/*     */ 
/* 150 */     setResizable(true);
/*     */ 
/* 152 */     ClientSettingsStorage settingsStorage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/* 153 */     Dimension size = settingsStorage.restoreFrameDimension(ClientSettingsStorage.FrameType.PREFERENCES);
/* 154 */     if (size != null) {
/* 155 */       setSize(size);
/*     */     }
/*     */ 
/* 159 */     setAlwaysOnTop(true);
/* 160 */     setDefaultCloseOperation(2);
/* 161 */     initListeners();
/*     */   }
/*     */ 
/*     */   public void settingsChanged(boolean enableSave) {
/* 165 */     this.okButton.setEnabled(enableSave);
/*     */   }
/*     */ 
/*     */   private void resetFields() {
/* 169 */     for (ISettingsPanel settingsPanel : this.settingsPanels) {
/* 170 */       settingsPanel.resetFields();
/*     */     }
/* 172 */     this.tabbedPanel.setSelected(0);
/*     */   }
/*     */ 
/*     */   private void initListeners() {
/* 176 */     this.okButton.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/* 178 */         boolean verified = true;
/* 179 */         for (int i = 0; i < SettingsTabbedFrame.this.settingsPanels.size(); i++) {
/* 180 */           ISettingsPanel settingsPanel = (ISettingsPanel)SettingsTabbedFrame.this.settingsPanels.get(i);
/* 181 */           if ((settingsPanel == null) || 
/* 182 */             (settingsPanel.verifySettings())) continue;
/* 183 */           verified = false;
/* 184 */           SettingsTabbedFrame.this.tabbedPanel.setSelected(i);
/* 185 */           break;
/*     */         }
/*     */ 
/* 189 */         if (verified) {
/* 190 */           for (ISettingsPanel settingsPanel : SettingsTabbedFrame.this.settingsPanels) {
/* 191 */             settingsPanel.applySettings();
/*     */           }
/* 193 */           DDSChartsController chartsController = (DDSChartsController)GreedContext.get("chartsController");
/* 194 */           if (chartsController != null) {
/* 195 */             chartsController.refreshChartsContent();
/*     */           }
/* 197 */           SettingsTabbedFrame.this.dispose();
/*     */         }
/*     */       }
/*     */     });
/* 202 */     this.cancelButton.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/* 204 */         SettingsTabbedFrame.this.dispose();
/*     */       }
/*     */     });
/* 208 */     this.defaultButton.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 211 */         ISettingsPanel settingsPanel = (ISettingsPanel)SettingsTabbedFrame.this.settingsPanels.get(SettingsTabbedFrame.this.tabbedPanel.getHeadersPanel().getSelectedIndex());
/* 212 */         settingsPanel.resetToDefaults();
/*     */       } } );
/*     */   }
/*     */ 
/*     */   private void addGeneralPanel() {
/* 218 */     this.generalPanel = new GeneralPanel(this);
/* 219 */     this.settingsPanels.add(this.generalPanel);
/* 220 */     this.tabbedPanel.addTab(this.generalPanel, GENERAL_TAB_ICON, "tab.header.general", "tab.header.general.tooltip");
/*     */   }
/*     */ 
/*     */   private void addChartSettingsPanel()
/*     */   {
/* 229 */     ChartSettingsPanel panel = new ChartSettingsPanel(this);
/* 230 */     this.settingsPanels.add(panel);
/* 231 */     this.tabbedPanel.addTab(panel, CHARTS_TAB_ICON, "tab.header.chart", "tab.header.chart.tooltip");
/*     */   }
/*     */ 
/*     */   private void addPeriodSettingsPanel()
/*     */   {
/* 240 */     PeriodSettingsPanel panel = new PeriodSettingsPanel(this);
/* 241 */     this.settingsPanels.add(panel);
/* 242 */     this.tabbedPanel.addTab(panel, PERDIOS_TAB_ICON, "period.panel.title.period", "tab.header.custom.period.tooltip");
/*     */   }
/*     */ 
/*     */   private void addThemeSettingsPanel()
/*     */   {
/* 251 */     ThemeSettingsPanel panel = new ThemeSettingsPanel(this);
/* 252 */     this.settingsPanels.add(panel);
/* 253 */     this.tabbedPanel.addTab(panel, THEME_TAB_ICON, "tab.header.themes", "tab.header.themes.tooltip");
/*     */   }
/*     */ 
/*     */   private void addWorkspaceSettingsPanel()
/*     */   {
/* 262 */     WorkspaceSettingsPanel panel = new WorkspaceSettingsPanel(this);
/* 263 */     this.settingsPanels.add(panel);
/* 264 */     this.tabbedPanel.addTab(panel, WORKSPACE_TAB_ICON, "tab.header.workspace", "tab.header.workspace.tooltip");
/*     */   }
/*     */ 
/*     */   private void addDisclaimerSettingPanel()
/*     */   {
/* 273 */     DisclaimerSettingsPanel panel = new DisclaimerSettingsPanel(this);
/* 274 */     this.settingsPanels.add(panel);
/* 275 */     this.tabbedPanel.addTab(panel, DISCLAIMER_TAB_ICON, "tab.header.disc", "tab.header.disc.tooltip");
/*     */   }
/*     */ 
/*     */   private void addAdvancedSettingsPanel()
/*     */   {
/* 284 */     AdvancedPanel panel = new AdvancedPanel(this);
/* 285 */     this.settingsPanels.add(panel);
/* 286 */     this.tabbedPanel.addTab(panel, ADVANCED_TAB_ICON, "tab.header.advanced", "tab.header.advanced.tooltip");
/*     */   }
/*     */ 
/*     */   public GeneralPanel getGeneralPanel()
/*     */   {
/* 295 */     return this.generalPanel;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.settings.SettingsTabbedFrame
 * JD-Core Version:    0.6.0
 */