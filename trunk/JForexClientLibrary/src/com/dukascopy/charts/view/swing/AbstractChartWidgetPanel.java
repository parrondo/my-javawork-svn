/*     */ package com.dukascopy.charts.view.swing;
/*     */ 
/*     */ import com.dukascopy.api.drawings.IWidgetChartObject;
/*     */ import com.dukascopy.charts.dialogs.indicators.ColorJComboBox;
/*     */ import com.dukascopy.charts.drawings.AbstractWidgetChartObject;
/*     */ import com.dukascopy.charts.drawings.IDrawingsManager;
/*     */ import com.dukascopy.charts.listeners.ChartsActionListenerRegistry;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableCheckBox;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*     */ import com.dukascopy.dds2.greed.util.SpringUtilities;
/*     */ import java.awt.AlphaComposite;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.CardLayout;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Composite;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.Font;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.GraphicsConfiguration;
/*     */ import java.awt.GraphicsEnvironment;
/*     */ import java.awt.Image;
/*     */ import java.awt.LayoutManager;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.RenderingHints;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.event.ItemListener;
/*     */ import java.awt.image.ImageObserver;
/*     */ import java.awt.image.VolatileImage;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.Icon;
/*     */ import javax.swing.ImageIcon;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JList;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JProgressBar;
/*     */ import javax.swing.JRadioButton;
/*     */ import javax.swing.ListCellRenderer;
/*     */ import javax.swing.SpringLayout;
/*     */ import javax.swing.plaf.basic.BasicButtonUI;
/*     */ 
/*     */ public abstract class AbstractChartWidgetPanel extends JPanel
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private static final int WIDGET_INSETS = 2;
/*     */   private static final int HEADER_HEIGHT = 20;
/*     */   protected static final int LABEL_COLUMN_WIDTH = 160;
/*     */   public static final String VIEW_MODE = "VIEW_MODE";
/*     */   public static final String EDIT_COMMON_MODE = "EDIT_COMMON_MODE";
/*     */   public static final String EDIT_CUSTOM_MODE = "EDIT_CUSTOM_MODE";
/*  72 */   private static final ImageIcon viewModeIcon = StratUtils.loadImageIcon("rc/media/widget_view.png");
/*  73 */   private static final ImageIcon viewModeRolloverIcon = StratUtils.loadImageIcon("rc/media/widget_view_rollover.png");
/*  74 */   private static final ImageIcon commonSettingsModeIcon = StratUtils.loadImageIcon("rc/media/widget_settings.png");
/*  75 */   private static final ImageIcon commonSettingsModeRolloverIcon = StratUtils.loadImageIcon("rc/media/widget_settings_rollover.png");
/*  76 */   private static final ImageIcon customSettingsModeIcon = StratUtils.loadImageIcon("rc/media/widget_settings_custom.png");
/*  77 */   private static final ImageIcon customSettingsModeRolloverIcon = StratUtils.loadImageIcon("rc/media/widget_settings_custom_rollover.png");
/*  78 */   private static final ImageIcon closeIcon = StratUtils.loadImageIcon("rc/media/widget_close.png");
/*  79 */   private static final ImageIcon closeRolloverIcon = StratUtils.loadImageIcon("rc/media/widget_close_rollover.png");
/*     */   private final IDrawingsManager drawingManager;
/*     */   protected final IWidgetChartObject chartObject;
/*  86 */   private final CardLayout widgetModeCardLayout = new CardLayout();
/*     */ 
/*  88 */   protected String mode = "VIEW_MODE";
/*     */   private JButton viewButton;
/*     */   private JButton commonSettingsButton;
/*     */   private JButton customSettingsButton;
/*     */   private JButton closeButton;
/*     */   private JPanel menuButtonsPanel;
/*     */   private JPanel customEditPanel;
/*     */   protected JPanel cardHolder;
/*     */   protected JPanel headerPanel;
/*     */   protected JPanel centerPanel;
/*     */   protected JPanel contentPanel;
/*     */   protected JPanel commonEditPanel;
/* 103 */   private boolean initialized = false;
/*     */ 
/* 105 */   private boolean mouseOver = false;
/*     */   private Dimension minEditModeSize;
/*     */ 
/*     */   public AbstractChartWidgetPanel(IWidgetChartObject chartObject, IDrawingsManager drawingManager)
/*     */   {
/* 111 */     this.drawingManager = drawingManager;
/* 112 */     this.chartObject = chartObject;
/* 113 */     ((AbstractWidgetChartObject)this.chartObject).setChartWidgetPanel(this);
/*     */ 
/* 115 */     setMinimumSize(new Dimension(80, 20));
/*     */ 
/* 117 */     SpringLayout widgetLayout = new SpringLayout();
/* 118 */     setLayout(widgetLayout);
/*     */ 
/* 120 */     int width = chartObject.getSize().width;
/* 121 */     int height = chartObject.getSize().height;
/* 122 */     setSize(width, height);
/* 123 */     setOpaque(false);
/*     */ 
/* 126 */     this.headerPanel = new JPanel(new BorderLayout())
/*     */     {
/*     */       private static final long serialVersionUID = 1L;
/*     */ 
/*     */       protected void paintComponent(Graphics g) {
/* 132 */         g.setColor(AbstractChartWidgetPanel.this.chartObject.getColor());
/* 133 */         Font oldFont = g.getFont();
/* 134 */         g.setFont(new Font(oldFont.getFontName(), 1, oldFont.getSize() + 1));
/* 135 */         g.drawString(AbstractChartWidgetPanel.this.getTitle(), 4, 16);
/* 136 */         g.setFont(oldFont);
/*     */       }
/*     */     };
/* 139 */     this.headerPanel.setOpaque(false);
/* 140 */     this.headerPanel.setPreferredSize(new Dimension(2147483647, 20));
/* 141 */     this.headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, chartObject.getColor()));
/* 142 */     this.headerPanel.setVisible(chartObject.isHeaderVisible());
/*     */ 
/* 146 */     add(getMenuButtonsPanel());
/* 147 */     widgetLayout.putConstraint("North", getMenuButtonsPanel(), 1, "North", this);
/* 148 */     widgetLayout.putConstraint("East", getMenuButtonsPanel(), -2, "East", this);
/*     */ 
/* 151 */     this.contentPanel = createInfoContentPanel();
/*     */ 
/* 154 */     this.cardHolder = new JPanel(this.widgetModeCardLayout);
/* 155 */     this.cardHolder.setOpaque(false);
/* 156 */     this.cardHolder.add(this.contentPanel, "VIEW_MODE");
/* 157 */     this.cardHolder.add(getCommonEditPanel(), "EDIT_COMMON_MODE");
/* 158 */     this.cardHolder.add(getCustomEditPanel(), "EDIT_CUSTOM_MODE");
/*     */ 
/* 160 */     this.widgetModeCardLayout.show(this.cardHolder, this.mode);
/*     */ 
/* 162 */     this.centerPanel = new JPanel(new BorderLayout());
/* 163 */     this.centerPanel.setOpaque(false);
/* 164 */     this.centerPanel.add(this.headerPanel, "North");
/* 165 */     this.centerPanel.add(this.cardHolder, "Center");
/*     */ 
/* 167 */     add(this.centerPanel);
/* 168 */     widgetLayout.putConstraint("North", this.centerPanel, 0, "North", this);
/* 169 */     widgetLayout.putConstraint("West", this.centerPanel, 0, "West", this);
/* 170 */     widgetLayout.putConstraint("East", this.centerPanel, 0, "East", this);
/* 171 */     widgetLayout.putConstraint("South", this.centerPanel, 0, "South", this);
/*     */ 
/* 174 */     MouseChartWidgetInputHandler handler = new MouseChartWidgetInputHandler(this);
/* 175 */     addMouseListener(handler);
/* 176 */     addMouseMotionListener(handler);
/*     */   }
/*     */ 
/*     */   protected abstract JPanel createInfoContentPanel();
/*     */ 
/*     */   private JPanel getCustomEditPanel() {
/* 183 */     if (this.customEditPanel == null) {
/* 184 */       this.customEditPanel = createCustomEditPanel();
/*     */     }
/*     */ 
/* 187 */     return this.customEditPanel;
/*     */   }
/*     */ 
/*     */   protected JPanel createCustomEditPanel() {
/* 191 */     JPanel panel = new JPanel();
/* 192 */     panel.setOpaque(false);
/*     */ 
/* 194 */     return panel;
/*     */   }
/*     */ 
/*     */   protected void drawingModelModified()
/*     */   {
/* 199 */     this.drawingManager.getChartActionListenerRegistry().drawingChanged(this.chartObject);
/*     */   }
/*     */ 
/*     */   public JPanel getMenuButtonsPanel() {
/* 203 */     if (this.menuButtonsPanel == null) {
/* 204 */       this.menuButtonsPanel = new JPanel(new FlowLayout(2, 3, 0));
/* 205 */       this.menuButtonsPanel.setOpaque(false);
/* 206 */       this.menuButtonsPanel.setVisible(false);
/* 207 */       this.menuButtonsPanel.setSize(80, 20);
/* 208 */       this.menuButtonsPanel.setPreferredSize(new Dimension(80, 20));
/* 209 */       this.menuButtonsPanel.add(getViewButton());
/* 210 */       this.menuButtonsPanel.add(getCommonSettingsButton());
/* 211 */       this.menuButtonsPanel.add(getCustomSettingsButton());
/* 212 */       this.menuButtonsPanel.add(getCloseButton());
/*     */     }
/* 214 */     return this.menuButtonsPanel;
/*     */   }
/*     */ 
/*     */   private JButton createMenuButton(Icon defaultIcon, Icon rolloverIcon, String tooltipKey, String mode) {
/* 218 */     JButton btn = new JButton(defaultIcon);
/* 219 */     Dimension size = new Dimension(16, 16);
/* 220 */     btn.setPreferredSize(size);
/* 221 */     btn.setSize(size);
/* 222 */     btn.setMinimumSize(size);
/* 223 */     btn.setMaximumSize(size);
/* 224 */     btn.setBorderPainted(false);
/* 225 */     btn.setUI(new BasicButtonUI());
/* 226 */     btn.setOpaque(false);
/* 227 */     btn.setRolloverIcon(rolloverIcon);
/* 228 */     if (tooltipKey != null) {
/* 229 */       btn.setToolTipText(LocalizationManager.getText(tooltipKey));
/*     */     }
/*     */ 
/* 232 */     btn.addMouseListener(new MouseChartWidgetSubComponentsMouseHandler(this));
/*     */ 
/* 234 */     if (mode != null) {
/* 235 */       btn.addActionListener(new ActionListener(mode)
/*     */       {
/*     */         public void actionPerformed(ActionEvent e)
/*     */         {
/* 239 */           AbstractChartWidgetPanel.this.switchToMode(this.val$mode);
/*     */         }
/*     */       });
/*     */     }
/* 244 */     return btn;
/*     */   }
/*     */ 
/*     */   public JButton getViewButton() {
/* 248 */     if (this.viewButton == null) {
/* 249 */       this.viewButton = createMenuButton(viewModeIcon, viewModeRolloverIcon, "menu.item.tooltip.return.to.main.view", "VIEW_MODE");
/* 250 */       this.viewButton.setVisible(false);
/*     */     }
/* 252 */     return this.viewButton;
/*     */   }
/*     */ 
/*     */   public JButton getCommonSettingsButton() {
/* 256 */     if (this.commonSettingsButton == null) {
/* 257 */       this.commonSettingsButton = createMenuButton(commonSettingsModeIcon, commonSettingsModeRolloverIcon, "menu.item.tooltip.common.widget.settings", "EDIT_COMMON_MODE");
/*     */     }
/* 259 */     return this.commonSettingsButton;
/*     */   }
/*     */ 
/*     */   public JButton getCustomSettingsButton() {
/* 263 */     if (this.customSettingsButton == null) {
/* 264 */       this.customSettingsButton = createMenuButton(customSettingsModeIcon, customSettingsModeRolloverIcon, "menu.item.tooltip.custom.widget.settings", "EDIT_CUSTOM_MODE");
/*     */     }
/* 266 */     return this.customSettingsButton;
/*     */   }
/*     */ 
/*     */   public JButton getCloseButton() {
/* 270 */     if (this.closeButton == null) {
/* 271 */       this.closeButton = createMenuButton(closeIcon, closeRolloverIcon, null, null);
/*     */ 
/* 273 */       this.closeButton.addActionListener(new ActionListener()
/*     */       {
/*     */         public void actionPerformed(ActionEvent e)
/*     */         {
/* 277 */           AbstractChartWidgetPanel.this.drawingManager.remove(AbstractChartWidgetPanel.this.chartObject);
/*     */         } } );
/*     */     }
/* 281 */     return this.closeButton;
/*     */   }
/*     */ 
/*     */   private void switchToMode(String mode) {
/* 285 */     if ("VIEW_MODE".equals(mode))
/*     */     {
/* 288 */       getViewButton().setVisible(false);
/* 289 */       getCommonSettingsButton().setEnabled(true);
/* 290 */       getCustomSettingsButton().setEnabled(true);
/*     */ 
/* 292 */       setSize(this.chartObject.getSize());
/*     */     }
/* 294 */     else if ("EDIT_COMMON_MODE".equals(mode))
/*     */     {
/* 297 */       getViewButton().setVisible(true);
/* 298 */       getCommonSettingsButton().setEnabled(false);
/* 299 */       getCustomSettingsButton().setEnabled(true);
/*     */ 
/* 301 */       this.minEditModeSize = getCommonEditPanelSize();
/*     */ 
/* 303 */       setSize(this.minEditModeSize);
/*     */     }
/* 305 */     else if ("EDIT_CUSTOM_MODE".equals(mode))
/*     */     {
/* 308 */       getViewButton().setVisible(true);
/* 309 */       getCommonSettingsButton().setEnabled(true);
/* 310 */       getCustomSettingsButton().setEnabled(false);
/*     */ 
/* 312 */       onCustomEditPanelShown(getCustomEditPanel());
/*     */ 
/* 314 */       Dimension size = getCustomEditPanel().getMinimumSize();
/* 315 */       int widgetHeight = (int)size.getHeight() + getHeaderHeight() + 4;
/*     */ 
/* 317 */       this.minEditModeSize = new Dimension(size.width, widgetHeight);
/*     */ 
/* 319 */       setSize(this.minEditModeSize);
/*     */     }
/*     */     else
/*     */     {
/* 323 */       throw new IllegalStateException("Unknown widget mode");
/*     */     }
/*     */ 
/* 326 */     this.mode = mode;
/* 327 */     this.widgetModeCardLayout.show(this.cardHolder, mode);
/*     */   }
/*     */ 
/*     */   protected Dimension getCommonEditPanelSize() {
/* 331 */     int width = ((SpringLayout)getCommonEditPanel().getLayout()).minimumLayoutSize(getCommonEditPanel()).width;
/* 332 */     return new Dimension(width, 150);
/*     */   }
/*     */ 
/*     */   protected void paintComponent(Graphics g)
/*     */   {
/* 337 */     if (!this.initialized) {
/* 338 */       this.initialized = true;
/* 339 */       int x = (int)(getParent().getWidth() * this.chartObject.getPosX());
/* 340 */       int y = (int)(getParent().getHeight() * this.chartObject.getPosY());
/* 341 */       setLocation(x, y);
/*     */     }
/*     */ 
/* 344 */     beforePaintingBackground();
/*     */ 
/* 346 */     Graphics2D g2 = (Graphics2D)g;
/*     */ 
/* 348 */     Color oldColor = g2.getColor();
/* 349 */     g2.setColor(this.chartObject.getFillColor());
/*     */ 
/* 351 */     if (this.chartObject.getFillOpacity() == 0.0F) {
/* 352 */       if (this.mouseOver) {
/* 353 */         Composite old = g2.getComposite();
/* 354 */         g2.setComposite(AlphaComposite.getInstance(3).derive(0.5F));
/*     */ 
/* 356 */         g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
/*     */ 
/* 358 */         g2.setComposite(old);
/*     */       }
/*     */     } else {
/* 361 */       Composite old = g2.getComposite();
/* 362 */       g2.setComposite(AlphaComposite.getInstance(3).derive(this.chartObject.getFillOpacity()));
/*     */ 
/* 364 */       Object oldAntialiasing = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
/* 365 */       g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/* 366 */       g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
/*     */ 
/* 368 */       g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAntialiasing);
/*     */ 
/* 370 */       g2.setComposite(old);
/*     */     }
/*     */ 
/* 373 */     g2.setColor(oldColor);
/*     */   }
/*     */ 
/*     */   protected void beforePaintingBackground()
/*     */   {
/*     */   }
/*     */ 
/*     */   private JPanel getCommonEditPanel()
/*     */   {
/* 386 */     if (this.commonEditPanel == null) {
/* 387 */       SpringLayout layout = new SpringLayout();
/*     */ 
/* 389 */       JPanel editPanel = new JPanel(layout);
/* 390 */       editPanel.setOpaque(false);
/*     */ 
/* 392 */       String fontName = this.chartObject.getFont().getFamily();
/* 393 */       Color color = this.chartObject.getColor();
/*     */ 
/* 395 */       JLabel label = createRightAlignedLabel("menu.item.widget.title", fontName, color);
/* 396 */       editPanel.add(label);
/* 397 */       editPanel.add(getChkTitle());
/*     */ 
/* 399 */       label = createRightAlignedLabel("menu.item.font", fontName, color);
/* 400 */       label.setPreferredSize(new Dimension(160, 0));
/* 401 */       editPanel.add(label);
/* 402 */       editPanel.add(getCmbFontName());
/*     */ 
/* 404 */       label = new JLabel("");
/* 405 */       editPanel.add(label);
/* 406 */       editPanel.add(getCmbForegroundColor());
/*     */ 
/* 408 */       label = createRightAlignedLabel("menu.item.background", fontName, color);
/* 409 */       editPanel.add(label);
/* 410 */       editPanel.add(getCmbBackgroundOpacity());
/*     */ 
/* 412 */       label = new JLabel("");
/* 413 */       editPanel.add(label);
/* 414 */       editPanel.add(getCmbBackgroundColor());
/*     */ 
/* 416 */       SpringUtilities.makeCompactGrid(editPanel, 5, 2, 3, 3, 5, 5);
/*     */ 
/* 418 */       this.commonEditPanel = editPanel;
/*     */     }
/*     */ 
/* 421 */     return this.commonEditPanel;
/*     */   }
/*     */ 
/*     */   protected JLocalizableLabel createLocalizableLabel(String localizationKey, String fontName, Color color) {
/* 425 */     JLocalizableLabel label = new JLocalizableLabel(localizationKey);
/* 426 */     label.setForeground(color);
/* 427 */     Font currFont = label.getFont();
/* 428 */     label.setFont(new Font(fontName, currFont.getStyle(), currFont.getSize()));
/*     */ 
/* 430 */     return label;
/*     */   }
/*     */ 
/*     */   protected JLabel createLabel(String localizationKey, String fontName, Color color) {
/* 434 */     JLabel label = new JLabel(LocalizationManager.getText(localizationKey));
/* 435 */     label.setForeground(color);
/* 436 */     Font currFont = label.getFont();
/* 437 */     label.setFont(new Font(fontName, currFont.getStyle(), currFont.getSize()));
/*     */ 
/* 439 */     return label;
/*     */   }
/*     */ 
/*     */   protected JLabel createRightAlignedLabel(String localizationKey, String fontName, Color color) {
/* 443 */     JLabel label = createLocalizableLabel(localizationKey, fontName, color);
/* 444 */     label.setHorizontalAlignment(4);
/*     */ 
/* 446 */     return label;
/*     */   }
/*     */ 
/*     */   protected JLabel createBoldLabel(String fontName, Color color) {
/* 450 */     JLabel label = new JLabel();
/* 451 */     label.setForeground(color);
/* 452 */     Font currFont = label.getFont();
/* 453 */     label.setFont(new Font(fontName, 1, currFont.getSize()));
/*     */ 
/* 455 */     return label;
/*     */   }
/*     */ 
/*     */   protected JCheckBox createCheckBox(String localizationKey, String fontName, Color color) {
/* 459 */     JCheckBox chk = new JLocalizableCheckBox(localizationKey);
/* 460 */     chk.setOpaque(false);
/* 461 */     chk.setForeground(color);
/* 462 */     Font currFont = chk.getFont();
/* 463 */     chk.setFont(new Font(fontName, currFont.getStyle(), currFont.getSize()));
/*     */ 
/* 465 */     return chk;
/*     */   }
/*     */ 
/*     */   protected JRadioButton createRadioButton(String localizationKey, String fontName, Color color) {
/* 469 */     JRadioButton radioButton = new JRadioButton();
/*     */ 
/* 471 */     Font currFont = radioButton.getFont();
/* 472 */     radioButton.setFont(new Font(fontName, currFont.getStyle(), currFont.getSize()));
/* 473 */     radioButton.setText(LocalizationManager.getText(localizationKey));
/* 474 */     radioButton.setForeground(color);
/* 475 */     radioButton.setOpaque(false);
/*     */ 
/* 477 */     return radioButton;
/*     */   }
/*     */ 
/*     */   private JCheckBox getChkTitle() {
/* 481 */     JCheckBox chkTitle = new JCheckBox();
/* 482 */     chkTitle.setOpaque(false);
/* 483 */     chkTitle.setSelected(this.chartObject.isHeaderVisible());
/* 484 */     chkTitle.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 488 */         JCheckBox checkBox = (JCheckBox)e.getSource();
/* 489 */         AbstractChartWidgetPanel.this.chartObject.setHeaderVisible(checkBox.isSelected());
/* 490 */         AbstractChartWidgetPanel.this.drawingModelModified();
/*     */       }
/*     */     });
/* 494 */     return chkTitle;
/*     */   }
/*     */ 
/*     */   private JComboBox getCmbForegroundColor() {
/* 498 */     ColorJComboBox cmbForegroundColor = new ColorJComboBox();
/* 499 */     cmbForegroundColor.setSelectedColor(this.chartObject.getColor());
/*     */ 
/* 501 */     cmbForegroundColor.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 505 */         ColorJComboBox combo = (ColorJComboBox)e.getSource();
/* 506 */         AbstractChartWidgetPanel.this.chartObject.setColor(combo.getSelectedColor());
/* 507 */         AbstractChartWidgetPanel.this.drawingModelModified();
/*     */       }
/*     */     });
/* 511 */     return cmbForegroundColor;
/*     */   }
/*     */ 
/*     */   private JComboBox getCmbBackgroundColor() {
/* 515 */     ColorJComboBox cmbBackgroundColor = new ColorJComboBox();
/* 516 */     cmbBackgroundColor.setSelectedColor(this.chartObject.getFillColor());
/*     */ 
/* 518 */     cmbBackgroundColor.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 522 */         ColorJComboBox combo = (ColorJComboBox)e.getSource();
/* 523 */         AbstractChartWidgetPanel.this.chartObject.setFillColor(combo.getSelectedColor());
/* 524 */         AbstractChartWidgetPanel.this.drawingModelModified();
/*     */       }
/*     */     });
/* 528 */     return cmbBackgroundColor;
/*     */   }
/*     */ 
/*     */   private JComboBox getCmbFontName() {
/* 532 */     String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
/*     */ 
/* 534 */     JComboBox cmbFontName = new JComboBox(fontNames);
/* 535 */     cmbFontName.setSelectedItem(this.chartObject.getFont().getFamily());
/*     */ 
/* 537 */     cmbFontName.addItemListener(new ItemListener(cmbFontName) {
/*     */       public void itemStateChanged(ItemEvent e) {
/* 539 */         if (e.getStateChange() == 1) {
/* 540 */           String newFontFamilyName = (String)this.val$cmbFontName.getSelectedItem();
/* 541 */           Font currFont = this.val$cmbFontName.getFont();
/* 542 */           AbstractChartWidgetPanel.this.chartObject.setFont(new Font(newFontFamilyName, currFont.getStyle(), currFont.getSize()));
/* 543 */           AbstractChartWidgetPanel.this.drawingModelModified();
/*     */         }
/*     */       }
/*     */     });
/* 548 */     cmbFontName.setPreferredSize(new Dimension(100, 18));
/* 549 */     cmbFontName.setMaximumSize(new Dimension(100, 18));
/* 550 */     cmbFontName.setMinimumSize(new Dimension(100, 18));
/*     */ 
/* 552 */     cmbFontName.setRenderer(new ListCellRenderer()
/*     */     {
/*     */       public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
/*     */       {
/* 556 */         String fontFamilyName = (String)value;
/* 557 */         JLabel label = new JLabel(fontFamilyName);
/*     */ 
/* 559 */         Font currFont = label.getFont();
/* 560 */         label.setFont(new Font(fontFamilyName, currFont.getStyle(), currFont.getSize()));
/*     */ 
/* 562 */         return label;
/*     */       }
/*     */     });
/* 566 */     return cmbFontName;
/*     */   }
/*     */ 
/*     */   private JComboBox getCmbBackgroundOpacity() {
/* 570 */     JComboBox cmbBackgroundOpacity = new JComboBox();
/* 571 */     cmbBackgroundOpacity.setPreferredSize(new Dimension(100, 18));
/* 572 */     cmbBackgroundOpacity.setMaximumSize(new Dimension(100, 18));
/* 573 */     cmbBackgroundOpacity.setMinimumSize(new Dimension(100, 18));
/* 574 */     cmbBackgroundOpacity.addItem(new Integer(0));
/* 575 */     cmbBackgroundOpacity.addItem(new Integer(5));
/* 576 */     for (int i = 10; i <= 100; i += 10) {
/* 577 */       cmbBackgroundOpacity.addItem(new Integer(i));
/*     */     }
/*     */ 
/* 580 */     cmbBackgroundOpacity.setRenderer(new ListCellRenderer()
/*     */     {
/*     */       public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
/*     */       {
/* 584 */         JLabel label = new JLabel();
/*     */ 
/* 586 */         if (((Integer)value).intValue() == 0) {
/* 587 */           label.setText(LocalizationManager.getText("menu.item.no.fill"));
/* 588 */         } else if (((Integer)value).intValue() == 100) {
/* 589 */           label.setText(LocalizationManager.getText("menu.item.opaque"));
/*     */         } else {
/* 591 */           AbstractChartWidgetPanel.ColorIcon icon = new AbstractChartWidgetPanel.ColorIcon(AbstractChartWidgetPanel.this.chartObject.getFillColor());
/* 592 */           icon.setOpacity(((Integer)value).intValue() / 100.0F);
/* 593 */           label.setIcon(icon);
/* 594 */           label.setText(String.valueOf(value) + "%");
/*     */         }
/*     */ 
/* 597 */         return label;
/*     */       }
/*     */     });
/* 601 */     cmbBackgroundOpacity.setSelectedItem(new Integer((int)(this.chartObject.getFillOpacity() * 100.0F)));
/*     */ 
/* 604 */     cmbBackgroundOpacity.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 608 */         JComboBox combo = (JComboBox)e.getSource();
/* 609 */         AbstractChartWidgetPanel.this.chartObject.setFillOpacity(((Integer)combo.getSelectedItem()).intValue() / 100.0F);
/* 610 */         AbstractChartWidgetPanel.this.drawingModelModified();
/*     */       }
/*     */     });
/* 614 */     return cmbBackgroundOpacity;
/*     */   }
/*     */ 
/*     */   public void setWidgetPosition(int x, int y)
/*     */   {
/* 624 */     Point currLoc = getLocation();
/*     */ 
/* 626 */     if (currLoc.x != x) {
/* 627 */       float posX = x / getParent().getWidth();
/* 628 */       this.chartObject.setPosX(posX);
/* 629 */       drawingModelModified();
/*     */     }
/* 631 */     if (currLoc.y != y) {
/* 632 */       float posY = y / getParent().getHeight();
/* 633 */       this.chartObject.setPosY(posY);
/* 634 */       drawingModelModified();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setWidgetBounds(Rectangle r)
/*     */   {
/* 645 */     setWidgetPosition(r.x, r.y);
/*     */ 
/* 647 */     if ((r.width != getWidth()) || (r.height != getHeight()))
/* 648 */       if ("VIEW_MODE".equals(this.mode)) {
/* 649 */         this.chartObject.setPreferredSize(new Dimension(r.width, r.height));
/* 650 */         drawingModelModified();
/*     */       } else {
/* 652 */         r.width = Math.max(this.minEditModeSize.width, r.width);
/* 653 */         r.height = Math.max(this.minEditModeSize.height, r.height);
/* 654 */         setBounds(r);
/*     */       }
/*     */   }
/*     */ 
/*     */   public Dimension getChartObjectSize()
/*     */   {
/* 661 */     return this.chartObject.getSize();
/*     */   }
/*     */ 
/*     */   public int getHeaderHeight() {
/* 665 */     return this.headerPanel.getHeight();
/*     */   }
/*     */ 
/*     */   public void setHeaderVisible(boolean visible) {
/* 669 */     this.headerPanel.setVisible(visible);
/*     */   }
/*     */ 
/*     */   public void setMouseOver(boolean state) {
/* 673 */     this.mouseOver = state;
/*     */   }
/*     */ 
/*     */   public void updateFonts(String fontName, Color color) {
/* 677 */     this.headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, color));
/*     */ 
/* 679 */     updateFontsRecursively(this, fontName, color);
/*     */   }
/*     */ 
/*     */   protected void updateFontsRecursively(Container container, String fontName, Color color) {
/* 683 */     for (Component comp : container.getComponents()) {
/* 684 */       if (((comp instanceof JComboBox)) || ((comp instanceof JProgressBar)) || ((comp instanceof JButton)))
/*     */       {
/*     */         continue;
/*     */       }
/*     */ 
/* 690 */       comp.setForeground(color);
/* 691 */       Font currFont = comp.getFont();
/* 692 */       comp.setFont(new Font(fontName, currFont.getStyle(), currFont.getSize()));
/* 693 */       if ((comp instanceof Container))
/* 694 */         updateFontsRecursively((Container)comp, fontName, color);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected abstract String getTitle();
/*     */ 
/*     */   protected void onCustomEditPanelShown(JPanel editPanel)
/*     */   {
/*     */   }
/*     */ 
/*     */   public static class ColorIcon
/*     */     implements Icon, ImageObserver
/*     */   {
/*     */     private static final int WIDTH = 42;
/*     */     private static final int HEIGHT = 12;
/*     */     private Color color;
/* 718 */     private float alpha = 1.0F;
/*     */ 
/*     */     public ColorIcon(Color color) {
/* 721 */       this.color = color;
/*     */     }
/*     */ 
/*     */     public int getIconHeight()
/*     */     {
/* 726 */       return 12;
/*     */     }
/*     */ 
/*     */     public int getIconWidth()
/*     */     {
/* 731 */       return 42;
/*     */     }
/*     */ 
/*     */     public void paintIcon(Component c, Graphics g, int x, int y)
/*     */     {
/* 737 */       Graphics2D g2d = (Graphics2D)g;
/*     */ 
/* 739 */       VolatileImage icon = g2d.getDeviceConfiguration().createCompatibleVolatileImage(getIconWidth(), getIconHeight(), 2);
/*     */ 
/* 741 */       Graphics2D iconGraphics = (Graphics2D)icon.getGraphics();
/*     */ 
/* 743 */       iconGraphics.setColor(this.color);
/*     */ 
/* 745 */       Composite composite = g2d.getComposite();
/* 746 */       g2d.setComposite(AlphaComposite.getInstance(3, this.alpha));
/* 747 */       iconGraphics.fillRect(0, 0, getIconWidth(), getIconHeight());
/* 748 */       icon.flush();
/*     */ 
/* 750 */       g2d.drawImage(icon, x, y, this);
/*     */ 
/* 752 */       g2d.setComposite(composite);
/*     */     }
/*     */ 
/*     */     public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height)
/*     */     {
/* 757 */       return false;
/*     */     }
/*     */ 
/*     */     public void setOpacity(float alpha) {
/* 761 */       this.alpha = alpha;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.swing.AbstractChartWidgetPanel
 * JD-Core Version:    0.6.0
 */