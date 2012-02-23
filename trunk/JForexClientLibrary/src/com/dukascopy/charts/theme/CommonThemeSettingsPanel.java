/*      */ package com.dukascopy.charts.theme;
/*      */ 
/*      */ import com.dukascopy.charts.drawings.DrawingsHelper.DashPattern;
/*      */ import com.dukascopy.charts.drawings.DrawingsHelper.LinePatternIcon;
/*      */ import com.dukascopy.charts.main.DDSChartsControllerImpl;
/*      */ import com.dukascopy.charts.main.interfaces.DDSChartsController;
/*      */ import com.dukascopy.charts.persistence.ITheme;
/*      */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*      */ import com.dukascopy.charts.persistence.ITheme.StrokeElement;
/*      */ import com.dukascopy.charts.persistence.ITheme.TextElement;
/*      */ import com.dukascopy.charts.persistence.ThemeManager;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*      */ import com.dukascopy.dds2.greed.gui.component.JColorComboBox;
/*      */ import com.dukascopy.dds2.greed.gui.component.JRoundedBorder;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableRoundedBorder;
/*      */ import com.dukascopy.dds2.greed.gui.resizing.ResizingManager.ComponentSize;
/*      */ import com.dukascopy.dds2.greed.gui.resizing.components.JResizableButton;
/*      */ import com.dukascopy.dds2.greed.gui.resizing.components.ResizableIcon;
/*      */ import java.awt.BasicStroke;
/*      */ import java.awt.Color;
/*      */ import java.awt.Component;
/*      */ import java.awt.Container;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.Font;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.Graphics2D;
/*      */ import java.awt.GraphicsConfiguration;
/*      */ import java.awt.GridBagConstraints;
/*      */ import java.awt.GridBagLayout;
/*      */ import java.awt.GridLayout;
/*      */ import java.awt.RenderingHints;
/*      */ import java.awt.Stroke;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ import java.awt.event.ItemEvent;
/*      */ import java.awt.event.ItemListener;
/*      */ import java.awt.image.VolatileImage;
/*      */ import java.util.EnumSet;
/*      */ import java.util.HashMap;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import javax.swing.Box;
/*      */ import javax.swing.ComboBoxEditor;
/*      */ import javax.swing.DefaultListCellRenderer;
/*      */ import javax.swing.Icon;
/*      */ import javax.swing.JButton;
/*      */ import javax.swing.JComboBox;
/*      */ import javax.swing.JComponent;
/*      */ import javax.swing.JLabel;
/*      */ import javax.swing.JList;
/*      */ import javax.swing.JOptionPane;
/*      */ import javax.swing.JPanel;
/*      */ import javax.swing.JScrollPane;
/*      */ import javax.swing.JSpinner;
/*      */ import javax.swing.JTextField;
/*      */ import javax.swing.JTree;
/*      */ import javax.swing.SpinnerModel;
/*      */ import javax.swing.SpinnerNumberModel;
/*      */ import javax.swing.border.LineBorder;
/*      */ import javax.swing.event.ChangeEvent;
/*      */ import javax.swing.event.ChangeListener;
/*      */ import javax.swing.event.DocumentEvent;
/*      */ import javax.swing.event.DocumentListener;
/*      */ import javax.swing.event.TreeExpansionEvent;
/*      */ import javax.swing.event.TreeExpansionListener;
/*      */ import javax.swing.event.TreeSelectionEvent;
/*      */ import javax.swing.event.TreeSelectionListener;
/*      */ import javax.swing.text.Document;
/*      */ import javax.swing.tree.DefaultMutableTreeNode;
/*      */ import javax.swing.tree.DefaultTreeCellRenderer;
/*      */ import javax.swing.tree.DefaultTreeModel;
/*      */ import javax.swing.tree.TreeNode;
/*      */ import javax.swing.tree.TreePath;
/*      */ 
/*      */ public class CommonThemeSettingsPanel extends JPanel
/*      */ {
/*      */   public static final int CONTROLS_DEFAULT_HEIGHT = 17;
/*      */   protected ThemesPanel themesPanel;
/*      */   protected EditorsPanel editorsPanel;
/*      */   protected JTree propertiesTree;
/*      */   protected ThemePreviewPanel previewPanel;
/*      */   protected ITheme theme;
/*      */   private final int chartId;
/*      */ 
/*      */   public CommonThemeSettingsPanel(int chartId, ITheme selectedTheme)
/*      */   {
/*  100 */     this.chartId = chartId;
/*  101 */     this.theme = selectedTheme;
/*      */ 
/*  103 */     build();
/*      */   }
/*      */ 
/*      */   public void applySettings() {
/*  107 */     if (this.theme == null) {
/*  108 */       return;
/*      */     }
/*      */ 
/*  111 */     DDSChartsController chartsController = DDSChartsControllerImpl.getInstance();
/*      */ 
/*  113 */     String themeName = this.themesPanel.getSelectedThemeName();
/*      */ 
/*  115 */     if (!ThemeManager.isDefault(themeName)) {
/*  116 */       ITheme clone = this.theme.clone();
/*  117 */       clone.setName(themeName);
/*      */ 
/*  119 */       if (ThemeManager.isExist(themeName))
/*  120 */         ThemeManager.modify(clone);
/*      */       else {
/*  122 */         ThemeManager.add(clone);
/*      */       }
/*      */     }
/*  125 */     chartsController.setTheme(this.chartId, themeName);
/*      */ 
/*  127 */     chartsController.refreshChartsContent();
/*      */   }
/*      */ 
/*      */   public void resetFields() {
/*  131 */     String themeName = DDSChartsControllerImpl.getInstance().getTheme(this.chartId);
/*  132 */     this.theme = ThemeManager.getTheme(themeName);
/*  133 */     if (this.theme == null)
/*  134 */       this.theme = ThemeManager.getDefaultTheme();
/*      */     else {
/*  136 */       this.theme = this.theme.clone();
/*      */     }
/*  138 */     this.themesPanel.setTheme(this.theme);
/*      */ 
/*  140 */     repaintComponents();
/*      */   }
/*      */ 
/*      */   public void resetToDefaults() {
/*  144 */     this.theme = ThemeManager.getDefaultTheme();
/*  145 */     this.themesPanel.setTheme(this.theme);
/*      */ 
/*  147 */     repaintComponents();
/*      */   }
/*      */ 
/*      */   public boolean verifySettings() {
/*  151 */     String themeName = this.themesPanel.getSelectedThemeName();
/*      */ 
/*  153 */     if ((themeName == null) || (themeName.trim().length() == 0)) {
/*  154 */       JOptionPane.showMessageDialog(this, LocalizationManager.getText("validation.themename.empty"), LocalizationManager.getText("title.themename"), 2);
/*      */ 
/*  159 */       this.themesPanel.setHighlighted(true);
/*  160 */       return false;
/*      */     }
/*  162 */     if ((ThemeManager.isDefault(themeName)) && 
/*  163 */       (!this.theme.equals(ThemeManager.getTheme(themeName)))) {
/*  164 */       JOptionPane.showMessageDialog(this, LocalizationManager.getText("validation.themename"), LocalizationManager.getText("title.themename"), 2);
/*      */ 
/*  169 */       this.themesPanel.setHighlighted(true);
/*  170 */       return false;
/*      */     }
/*      */ 
/*  174 */     return true;
/*      */   }
/*      */ 
/*      */   protected void build() {
/*  178 */     setLayout(new GridLayout(1, 2));
/*      */ 
/*  180 */     this.themesPanel = new ThemesPanel(this);
/*  181 */     this.editorsPanel = new EditorsPanel(this);
/*  182 */     this.propertiesTree = new PropertiesTree(this);
/*  183 */     this.previewPanel = new ThemePreviewPanel();
/*      */ 
/*  185 */     add(new JPanel()
/*      */     {
/*      */     });
/*  192 */     add(new JPanel()
/*      */     {
/*      */     });
/*      */   }
/*      */ 
/*      */   protected void settingsChanged(boolean enableSave)
/*      */   {
/*  205 */     repaintComponents();
/*      */   }
/*      */ 
/*      */   protected void repaintComponents() {
/*  209 */     if (this.propertiesTree != null) {
/*  210 */       this.propertiesTree.repaint();
/*      */     }
/*      */ 
/*  213 */     if (this.previewPanel != null) {
/*  214 */       this.previewPanel.set(this.theme);
/*  215 */       this.previewPanel.repaint();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected static void gbc(Container container, Component component, int gridX, int gridY, int weightX, int weightY, int anchor, int fill)
/*      */   {
/*  227 */     gbc(container, component, gridX, gridY, weightX, weightY, 1, 1, anchor, fill);
/*      */   }
/*      */ 
/*      */   protected static void gbc(Container container, Component component, int gridX, int gridY, int weightX, int weightY, int gridWidth, int gridHeight, int anchor, int fill)
/*      */   {
/*  238 */     gbc(container, component, gridX, gridY, weightX, weightY, gridWidth, gridHeight, 0, 0, 0, 0, anchor, fill);
/*      */   }
/*      */ 
/*      */   protected static void gbc(Container container, Component component, int gridX, int gridY, int weightX, int weightY, int gridWidth, int gridHeight, int insetLeft, int insetRight, int insetTop, int insetBottom, int anchor, int fill)
/*      */   {
/*  251 */     GridBagConstraints gbc = new GridBagConstraints();
/*      */ 
/*  253 */     gbc.gridx = gridX;
/*  254 */     gbc.gridy = gridY;
/*  255 */     gbc.weightx = weightX;
/*  256 */     gbc.weighty = weightY;
/*  257 */     gbc.gridwidth = gridWidth;
/*  258 */     gbc.gridheight = gridHeight;
/*  259 */     gbc.anchor = anchor;
/*  260 */     gbc.fill = fill;
/*  261 */     gbc.insets.left = insetLeft;
/*  262 */     gbc.insets.right = insetRight;
/*  263 */     gbc.insets.top = insetTop;
/*  264 */     gbc.insets.bottom = insetBottom;
/*      */ 
/*  266 */     container.add(component, gbc);
/*      */   }
/*      */ 
/*      */   private static final class StrokeIcon
/*      */     implements Icon
/*      */   {
/*      */     private static final int WIDTH = 14;
/*      */     private static final int HEIGHT = 14;
/*      */     private final Stroke stroke;
/*      */     private final Color color;
/*      */ 
/*      */     public StrokeIcon(Stroke stroke, Color color)
/*      */     {
/* 1132 */       this.stroke = stroke;
/* 1133 */       this.color = color;
/*      */     }
/*      */ 
/*      */     public StrokeIcon(Stroke stroke) {
/* 1137 */       this(stroke, Color.BLACK);
/*      */     }
/*      */ 
/*      */     public int getIconHeight()
/*      */     {
/* 1142 */       return 14;
/*      */     }
/*      */ 
/*      */     public int getIconWidth()
/*      */     {
/* 1147 */       return 14;
/*      */     }
/*      */ 
/*      */     public void paintIcon(Component c, Graphics g, int x, int y)
/*      */     {
/* 1152 */       Graphics2D g2d = (Graphics2D)g;
/*      */ 
/* 1154 */       VolatileImage icon = g2d.getDeviceConfiguration().createCompatibleVolatileImage(14, 14, 2);
/*      */ 
/* 1156 */       Graphics2D iconGraphics = (Graphics2D)icon.getGraphics();
/* 1157 */       iconGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/* 1158 */       iconGraphics.setColor(this.color);
/* 1159 */       iconGraphics.setStroke(this.stroke);
/*      */ 
/* 1161 */       iconGraphics.drawLine(1, 1, 12, 12);
/* 1162 */       icon.flush();
/*      */ 
/* 1164 */       g2d.drawImage(icon, x, y, null);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static final class ColorIcon
/*      */     implements Icon
/*      */   {
/*      */     private static final int WIDTH = 14;
/*      */     private static final int HEIGHT = 14;
/*      */     private final Color color;
/*      */ 
/*      */     public ColorIcon(Color color)
/*      */     {
/* 1088 */       this.color = color;
/*      */     }
/*      */ 
/*      */     public int getIconHeight()
/*      */     {
/* 1093 */       return 14;
/*      */     }
/*      */ 
/*      */     public int getIconWidth()
/*      */     {
/* 1098 */       return 14;
/*      */     }
/*      */ 
/*      */     public void paintIcon(Component c, Graphics g, int x, int y)
/*      */     {
/* 1103 */       Graphics2D g2d = (Graphics2D)g;
/*      */ 
/* 1105 */       VolatileImage icon = g2d.getDeviceConfiguration().createCompatibleVolatileImage(14, 14, 2);
/*      */ 
/* 1107 */       Graphics2D iconGraphics = (Graphics2D)icon.getGraphics();
/* 1108 */       iconGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*      */ 
/* 1110 */       iconGraphics.setColor(this.color);
/* 1111 */       iconGraphics.fillOval(1, 1, 12, 12);
/* 1112 */       iconGraphics.setColor(Color.BLACK);
/* 1113 */       iconGraphics.drawOval(1, 1, 12, 12);
/* 1114 */       icon.flush();
/*      */ 
/* 1116 */       g2d.drawImage(icon, x, y, null);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static final class ThemePropertiesCellRenderer extends DefaultTreeCellRenderer
/*      */   {
/*      */     final CommonThemeSettingsPanel themeSettingsPanel;
/*      */ 
/*      */     public ThemePropertiesCellRenderer(CommonThemeSettingsPanel themeSettingsPanel)
/*      */     {
/* 1035 */       this.themeSettingsPanel = themeSettingsPanel;
/*      */     }
/*      */ 
/*      */     public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
/*      */     {
/* 1042 */       Component component = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
/*      */ 
/* 1044 */       if ((value instanceof DefaultMutableTreeNode)) {
/* 1045 */         Object userObject = ((DefaultMutableTreeNode)value).getUserObject();
/*      */ 
/* 1047 */         if ((userObject instanceof ITheme.ChartElement)) {
/* 1048 */           ITheme.ChartElement chartElement = (ITheme.ChartElement)userObject;
/* 1049 */           setText(chartElement.getDescription());
/* 1050 */           if (this.themeSettingsPanel.theme != null) {
/* 1051 */             setIcon(new CommonThemeSettingsPanel.ColorIcon(this.themeSettingsPanel.theme.getColor(chartElement)));
/*      */           }
/*      */         }
/* 1054 */         else if ((userObject instanceof ITheme.TextElement)) {
/* 1055 */           ITheme.TextElement textElement = (ITheme.TextElement)userObject;
/* 1056 */           setText(textElement.getDescription());
/*      */         }
/* 1058 */         else if ((userObject instanceof ITheme.StrokeElement)) {
/* 1059 */           ITheme.StrokeElement strokeElement = (ITheme.StrokeElement)userObject;
/* 1060 */           setText(strokeElement.getDescription());
/*      */ 
/* 1062 */           Color color = null;
/* 1063 */           if (ITheme.StrokeElement.LAST_CANDLE_TRACKING_LINE_STROKE.equals(strokeElement)) {
/* 1064 */             color = this.themeSettingsPanel.theme.getColor(ITheme.ChartElement.LAST_CANDLE_TRACKING_LINE);
/*      */           }
/*      */ 
/* 1067 */           if (this.themeSettingsPanel.theme != null) {
/* 1068 */             setIcon(new CommonThemeSettingsPanel.StrokeIcon(this.themeSettingsPanel.theme.getStroke(strokeElement), color));
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 1073 */       return component;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static final class StrokeEditorPanel extends JPanel
/*      */     implements CommonThemeSettingsPanel.IEditor<ITheme.StrokeElement>
/*      */   {
/*  877 */     private static final Set<ITheme.StrokeElement> strokesWithoutLineWidth = EnumSet.of(ITheme.StrokeElement.GRID_STROKE);
/*      */     private final CommonThemeSettingsPanel themePanel;
/*      */     private ITheme.StrokeElement strokeElement;
/*      */     private JComboBox cmbLineDash;
/*      */     private JComboBox cmbLineWidth;
/*      */ 
/*      */     public StrokeEditorPanel(CommonThemeSettingsPanel themePanel)
/*      */     {
/*  885 */       this.themePanel = themePanel;
/*  886 */       setLayout(new GridBagLayout());
/*      */ 
/*  888 */       GridBagConstraints gbc = new GridBagConstraints()
/*      */       {
/*      */       };
/*  896 */       add(getCmbLineDash(), gbc);
/*      */ 
/*  898 */       gbc.gridx = 1;
/*  899 */       gbc.weightx = 0.1D;
/*  900 */       gbc.fill = 0;
/*  901 */       gbc.anchor = 10;
/*      */ 
/*  903 */       add(Box.createGlue(), gbc);
/*      */ 
/*  905 */       gbc.gridx = 2;
/*  906 */       gbc.weightx = 1.0D;
/*  907 */       gbc.fill = 1;
/*  908 */       gbc.anchor = 13;
/*      */ 
/*  910 */       add(getCmbLineWidth(), gbc);
/*      */     }
/*      */ 
/*      */     public void set(ITheme.StrokeElement strokeElement)
/*      */     {
/*  915 */       this.strokeElement = strokeElement;
/*      */ 
/*  917 */       if (strokeElement != null) {
/*  918 */         BasicStroke stroke = this.themePanel.theme.getStroke(strokeElement);
/*      */ 
/*  920 */         if (strokesWithoutLineWidth.contains(strokeElement)) {
/*  921 */           getCmbLineWidth().setVisible(false);
/*      */         } else {
/*  923 */           getCmbLineWidth().setVisible(true);
/*  924 */           getCmbLineWidth().setSelectedItem(Integer.valueOf(new Float(stroke.getLineWidth()).intValue()));
/*      */         }
/*      */ 
/*  927 */         selectDashPattern(stroke.getDashArray());
/*      */       }
/*      */     }
/*      */ 
/*      */     private void selectDashPattern(float[] dashArray) {
/*  932 */       for (int i = 0; i < getCmbLineDash().getItemCount(); i++) {
/*  933 */         Object o = getCmbLineDash().getItemAt(i);
/*  934 */         if ((o instanceof DrawingsHelper.DashPattern)) {
/*  935 */           DrawingsHelper.DashPattern dashPattern = (DrawingsHelper.DashPattern)o;
/*  936 */           if (dashPattern.equals(dashArray))
/*  937 */             getCmbLineDash().setSelectedItem(dashPattern);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     private void setStroke()
/*      */     {
/*  944 */       if (this.strokeElement != null) {
/*  945 */         this.themePanel.theme.setStroke(this.strokeElement, new BasicStroke(((Integer)getCmbLineWidth().getSelectedItem()).intValue(), 0, 2, 0.0F, ((DrawingsHelper.DashPattern)getCmbLineDash().getSelectedItem()).getDashArray(), 0.0F));
/*      */ 
/*  955 */         this.themePanel.settingsChanged(true);
/*      */       }
/*      */     }
/*      */ 
/*      */     public JComboBox getCmbLineDash() {
/*  960 */       if (this.cmbLineDash == null) {
/*  961 */         this.cmbLineDash = new JComboBox();
/*  962 */         for (DrawingsHelper.DashPattern dashPattern : DrawingsHelper.DashPattern.values()) {
/*  963 */           this.cmbLineDash.addItem(dashPattern);
/*      */         }
/*      */ 
/*  966 */         this.cmbLineDash.setRenderer(new DefaultListCellRenderer() {
/*      */           public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
/*  968 */             Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
/*  969 */             JLabel label = new JLabel();
/*  970 */             Icon icon = new DrawingsHelper.LinePatternIcon((DrawingsHelper.DashPattern)value);
/*  971 */             label.setIcon(icon);
/*  972 */             label.setOpaque(true);
/*  973 */             label.setHorizontalAlignment(0);
/*  974 */             label.setForeground(comp.getForeground());
/*  975 */             label.setBackground(comp.getBackground());
/*  976 */             label.setPreferredSize(new Dimension(50, 17));
/*  977 */             return label;
/*      */           }
/*      */         });
/*  981 */         this.cmbLineDash.addActionListener(new ActionListener()
/*      */         {
/*      */           public void actionPerformed(ActionEvent e) {
/*  984 */             CommonThemeSettingsPanel.StrokeEditorPanel.this.getCmbLineWidth().repaint();
/*  985 */             CommonThemeSettingsPanel.StrokeEditorPanel.this.setStroke();
/*      */           }
/*      */         });
/*      */       }
/*  990 */       return this.cmbLineDash;
/*      */     }
/*      */ 
/*      */     public JComboBox getCmbLineWidth() {
/*  994 */       if (this.cmbLineWidth == null) {
/*  995 */         this.cmbLineWidth = new JComboBox();
/*      */ 
/*  997 */         for (int i = 1; i <= 9; i += 2) {
/*  998 */           this.cmbLineWidth.addItem(Integer.valueOf(i));
/*      */         }
/*      */ 
/* 1001 */         this.cmbLineWidth.setRenderer(new DefaultListCellRenderer() {
/*      */           public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
/* 1003 */             Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
/* 1004 */             JLabel label = new JLabel();
/* 1005 */             Icon icon = new DrawingsHelper.LinePatternIcon((DrawingsHelper.DashPattern)CommonThemeSettingsPanel.StrokeEditorPanel.this.getCmbLineDash().getSelectedItem(), ((Integer)value).intValue());
/* 1006 */             label.setIcon(icon);
/* 1007 */             label.setOpaque(true);
/* 1008 */             label.setHorizontalAlignment(0);
/* 1009 */             label.setForeground(comp.getForeground());
/* 1010 */             label.setBackground(comp.getBackground());
/* 1011 */             label.setPreferredSize(new Dimension(50, 17));
/* 1012 */             return label;
/*      */           }
/*      */         });
/* 1016 */         this.cmbLineWidth.addActionListener(new ActionListener()
/*      */         {
/*      */           public void actionPerformed(ActionEvent e) {
/* 1019 */             CommonThemeSettingsPanel.StrokeEditorPanel.this.setStroke();
/*      */           } } );
/*      */       }
/* 1023 */       return this.cmbLineWidth;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static final class FontEditorPanel extends JPanel
/*      */     implements CommonThemeSettingsPanel.IEditor<ITheme.TextElement>
/*      */   {
/*  791 */     private static final Dimension SPINNER_SIZE = new Dimension(35, 20);
/*      */     private static final int MAX_FONT_SIZE = 12;
/*      */     private final CommonThemeSettingsPanel themePanel;
/*      */     private ITheme.TextElement textElement;
/*      */     private final JComboBox namesComboBox;
/*      */     private final JSpinner sizeSpinner;
/*      */ 
/*      */     public FontEditorPanel(CommonThemeSettingsPanel themePanel)
/*      */     {
/*  800 */       this.themePanel = themePanel;
/*  801 */       setLayout(new GridBagLayout());
/*      */ 
/*  803 */       GridBagConstraints gbc = new GridBagConstraints()
/*      */       {
/*      */       };
/*  811 */       this.namesComboBox = new JComboBox()
/*      */       {
/*      */       };
/*  825 */       add(this.namesComboBox, gbc);
/*      */ 
/*  827 */       this.sizeSpinner = new JSpinner(new SpinnerNumberModel(8, 8, 12, 1))
/*      */       {
/*      */       };
/*  837 */       gbc.gridx = 1;
/*  838 */       gbc.weightx = 0.0D;
/*  839 */       gbc.fill = 0;
/*  840 */       gbc.anchor = 13;
/*  841 */       gbc.insets.left = 5;
/*  842 */       add(this.sizeSpinner, gbc);
/*      */     }
/*      */ 
/*      */     public void set(ITheme.TextElement textElement) {
/*  846 */       this.textElement = textElement;
/*      */ 
/*  848 */       if (textElement != null) {
/*  849 */         Font font = this.themePanel.theme.getFont(textElement);
/*      */ 
/*  851 */         this.namesComboBox.setSelectedItem(font.getName());
/*      */ 
/*  853 */         this.sizeSpinner.setValue(Integer.valueOf(font.getSize()));
/*      */       }
/*      */     }
/*      */ 
/*      */     private void setFont() {
/*  858 */       if (this.textElement != null) {
/*  859 */         this.themePanel.theme.setFont(this.textElement, new Font((String)this.namesComboBox.getSelectedItem(), 0, ((Integer)this.sizeSpinner.getValue()).intValue()));
/*      */ 
/*  867 */         this.themePanel.settingsChanged(true);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static final class ColorEditorPanel extends JPanel
/*      */     implements CommonThemeSettingsPanel.IEditor<ITheme.ChartElement>
/*      */   {
/*      */     private final CommonThemeSettingsPanel themeSettingsPanel;
/*      */     private final JColorComboBox colorComboBox;
/*      */     private ITheme.ChartElement chartElement;
/*      */ 
/*      */     public ColorEditorPanel(CommonThemeSettingsPanel themeSettingsPanel)
/*      */     {
/*  731 */       this.themeSettingsPanel = themeSettingsPanel;
/*  732 */       setLayout(new GridBagLayout());
/*      */ 
/*  734 */       this.colorComboBox = new JColorComboBox(themeSettingsPanel)
/*      */       {
/*      */       };
/*  746 */       add(this.colorComboBox, new GridBagConstraints()
/*      */       {
/*      */       });
/*  755 */       add(new JResizableButton(new ResizableIcon("preferences_colors.png"), ResizingManager.ComponentSize.SIZE_20X20)
/*      */       {
/*      */       }
/*      */       , new GridBagConstraints()
/*      */       {
/*      */       });
/*      */     }
/*      */ 
/*      */     public void set(ITheme.ChartElement chartElement)
/*      */     {
/*  778 */       this.chartElement = chartElement;
/*      */ 
/*  780 */       if (chartElement != null)
/*  781 */         this.colorComboBox.setSelectedColor(this.themeSettingsPanel.theme.getColor(chartElement));
/*      */     }
/*      */   }
/*      */ 
/*      */   private static final class TextPanel extends JPanel
/*      */     implements CommonThemeSettingsPanel.IEditor<String>
/*      */   {
/*      */     private final JLabel textLabel;
/*      */ 
/*      */     public TextPanel()
/*      */     {
/*  696 */       setLayout(new GridBagLayout());
/*      */ 
/*  698 */       this.textLabel = new JLabel();
/*      */ 
/*  700 */       GridBagConstraints gbc = new GridBagConstraints();
/*  701 */       gbc.gridx = 0;
/*  702 */       gbc.gridy = 0;
/*  703 */       gbc.fill = 2;
/*  704 */       gbc.weightx = 1.0D;
/*  705 */       gbc.weighty = 0.0D;
/*  706 */       gbc.anchor = 17;
/*  707 */       add(this.textLabel, gbc);
/*      */ 
/*  709 */       gbc.gridx = 1;
/*  710 */       gbc.weightx = 0.0D;
/*  711 */       gbc.fill = 0;
/*  712 */       gbc.anchor = 13;
/*  713 */       add(Box.createVerticalStrut(20), gbc);
/*      */     }
/*      */ 
/*      */     public void set(String text) {
/*  717 */       this.textLabel.setText("Select property to setup");
/*      */     }
/*      */   }
/*      */ 
/*      */   private static abstract interface IEditor<T>
/*      */   {
/*      */     public abstract void set(T paramT);
/*      */   }
/*      */ 
/*      */   private static final class EditorsPanel extends JPanel
/*      */   {
/*      */     private final JRoundedBorder border;
/*  625 */     private final Map<Class<?>, CommonThemeSettingsPanel.IEditor> editors = new HashMap();
/*      */ 
/*  627 */     private final Map<Class<?>, String> headers = new HashMap();
/*      */ 
/*      */     public EditorsPanel(CommonThemeSettingsPanel themeSettingsPanel) {
/*  630 */       setLayout(new GridBagLayout());
/*      */ 
/*  632 */       this.border = new JRoundedBorder(this, "");
/*  633 */       setBorder(this.border);
/*      */ 
/*  635 */       this.editors.put(String.class, new CommonThemeSettingsPanel.TextPanel());
/*  636 */       this.headers.put(String.class, "");
/*  637 */       this.editors.put(ITheme.ChartElement.class, new CommonThemeSettingsPanel.ColorEditorPanel(themeSettingsPanel));
/*  638 */       this.headers.put(ITheme.ChartElement.class, "Color");
/*  639 */       this.editors.put(ITheme.TextElement.class, new CommonThemeSettingsPanel.FontEditorPanel(themeSettingsPanel));
/*  640 */       this.headers.put(ITheme.TextElement.class, "Font");
/*  641 */       this.editors.put(ITheme.StrokeElement.class, new CommonThemeSettingsPanel.StrokeEditorPanel(themeSettingsPanel));
/*  642 */       this.headers.put(ITheme.StrokeElement.class, "Stroke");
/*      */ 
/*  645 */       GridBagConstraints gbc = new GridBagConstraints();
/*  646 */       gbc.gridx = 0;
/*  647 */       gbc.weightx = 1.0D;
/*  648 */       gbc.weighty = 0.0D;
/*  649 */       gbc.fill = 2;
/*  650 */       gbc.anchor = 11;
/*      */ 
/*  652 */       gbc.gridy = 0;
/*  653 */       add((JComponent)this.editors.get(String.class), gbc);
/*      */ 
/*  655 */       gbc.gridy = 0;
/*  656 */       add((JComponent)this.editors.get(ITheme.ChartElement.class), gbc);
/*      */ 
/*  658 */       gbc.gridy = 0;
/*  659 */       add((JComponent)this.editors.get(ITheme.TextElement.class), gbc);
/*      */ 
/*  661 */       gbc.gridy = 0;
/*  662 */       add((JComponent)this.editors.get(ITheme.StrokeElement.class), gbc);
/*      */     }
/*      */ 
/*      */     public void set(Object object)
/*      */     {
/*  667 */       Class objectClass = object.getClass();
/*  668 */       this.border.setHeaderText((String)this.headers.get(objectClass));
/*      */ 
/*  670 */       for (Class editorClass : this.editors.keySet()) {
/*  671 */         CommonThemeSettingsPanel.IEditor editor = (CommonThemeSettingsPanel.IEditor)this.editors.get(editorClass);
/*      */ 
/*  673 */         if (editorClass.equals(objectClass)) {
/*  674 */           editor.set(object);
/*  675 */           ((JComponent)editor).setVisible(true);
/*      */         } else {
/*  677 */           ((JComponent)editor).setVisible(false);
/*      */         }
/*      */       }
/*      */ 
/*  681 */       repaint();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected static final class ThemesPanel extends JPanel
/*      */   {
/*  421 */     private static final Dimension BUTTON_SIZE = new Dimension(20, 20);
/*  422 */     private static final Icon ADD_ICON = StratUtils.loadImageIcon("rc/media/dialog_strategy_add_preset_active.png");
/*  423 */     private static final Icon SAVE_ICON = StratUtils.loadImageIcon("rc/media/dialog_strategy_save_preset_active.png");
/*  424 */     private static final Icon DELETE_ICON = StratUtils.loadImageIcon("rc/media/dialog_strategy_delete_preset_active.png");
/*      */     private final JComboBox comboBox;
/*      */     private final JButton addButton;
/*      */     private final JButton saveButton;
/*      */     private final JButton deleteButton;
/*      */ 
/*      */     public ThemesPanel(CommonThemeSettingsPanel themeSettingsPanel)
/*      */     {
/*  432 */       setLayout(new GridBagLayout());
/*  433 */       setBorder(new JLocalizableRoundedBorder(this, "border.themes"));
/*      */ 
/*  435 */       this.comboBox = new JComboBox();
/*  436 */       this.comboBox.setEditable(true);
/*      */ 
/*  438 */       fill();
/*      */ 
/*  440 */       this.comboBox.addItemListener(new ItemListener(themeSettingsPanel) {
/*      */         public void itemStateChanged(ItemEvent e) {
/*  442 */           String selectedThemeName = (String)CommonThemeSettingsPanel.ThemesPanel.this.comboBox.getSelectedItem();
/*      */ 
/*  444 */           if (ThemeManager.isExist(selectedThemeName)) {
/*  445 */             this.val$themeSettingsPanel.theme = ThemeManager.getTheme(selectedThemeName).clone();
/*  446 */             this.val$themeSettingsPanel.settingsChanged(true);
/*      */           }
/*      */         }
/*      */       });
/*  451 */       JTextField comboTextField = (JTextField)this.comboBox.getEditor().getEditorComponent();
/*      */ 
/*  453 */       comboTextField.getDocument().addDocumentListener(new DocumentListener()
/*      */       {
/*      */         public void changedUpdate(DocumentEvent e)
/*      */         {
/*  457 */           CommonThemeSettingsPanel.ThemesPanel.this.checkThemeName();
/*      */         }
/*      */ 
/*      */         public void removeUpdate(DocumentEvent e)
/*      */         {
/*  462 */           CommonThemeSettingsPanel.ThemesPanel.this.checkThemeName();
/*      */         }
/*      */ 
/*      */         public void insertUpdate(DocumentEvent e)
/*      */         {
/*  467 */           CommonThemeSettingsPanel.ThemesPanel.this.checkThemeName();
/*      */         }
/*      */       });
/*  472 */       this.addButton = new JLocalizableButton(ADD_ICON, themeSettingsPanel)
/*      */       {
/*      */       };
/*  498 */       this.saveButton = new JLocalizableButton(SAVE_ICON, themeSettingsPanel)
/*      */       {
/*      */       };
/*  523 */       this.deleteButton = new JLocalizableButton(DELETE_ICON, themeSettingsPanel)
/*      */       {
/*      */       };
/*  547 */       GridBagConstraints gbc = new GridBagConstraints();
/*  548 */       gbc.gridy = 0;
/*      */ 
/*  550 */       gbc.gridx = 0;
/*  551 */       gbc.fill = 2;
/*  552 */       gbc.weightx = 1.0D;
/*  553 */       gbc.anchor = 17;
/*  554 */       add(this.comboBox, gbc);
/*      */ 
/*  556 */       gbc.fill = 0;
/*  557 */       gbc.weightx = 0.0D;
/*  558 */       gbc.anchor = 13;
/*  559 */       gbc.insets.left = 5;
/*      */ 
/*  561 */       gbc.gridx = 1;
/*  562 */       add(this.addButton, gbc);
/*      */ 
/*  564 */       gbc.gridx = 2;
/*  565 */       add(this.saveButton, gbc);
/*      */ 
/*  567 */       gbc.gridx = 3;
/*  568 */       add(this.deleteButton, gbc);
/*      */     }
/*      */ 
/*      */     public void setTheme(ITheme theme) {
/*  572 */       fill();
/*  573 */       setHighlighted(false);
/*  574 */       this.comboBox.setSelectedItem(theme.getName());
/*      */     }
/*      */ 
/*      */     private void fill() {
/*  578 */       this.comboBox.removeAllItems();
/*      */ 
/*  580 */       for (ITheme theme : ThemeManager.getThemes())
/*  581 */         this.comboBox.addItem(theme.getName());
/*      */     }
/*      */ 
/*      */     private void checkThemeName()
/*      */     {
/*  586 */       setHighlighted(false);
/*  587 */       String themeName = getSelectedThemeName();
/*      */ 
/*  589 */       if (ThemeManager.isDefault(themeName)) {
/*  590 */         this.saveButton.setEnabled(false);
/*  591 */         this.deleteButton.setEnabled(false);
/*  592 */         return;
/*      */       }
/*  594 */       if (themeName.trim().isEmpty()) {
/*  595 */         this.saveButton.setEnabled(false);
/*  596 */         this.deleteButton.setEnabled(false);
/*  597 */         setHighlighted(true);
/*      */       } else {
/*  599 */         this.saveButton.setEnabled(true);
/*  600 */         this.deleteButton.setEnabled(ThemeManager.isExist(themeName));
/*      */       }
/*      */     }
/*      */ 
/*      */     public String getSelectedThemeName() {
/*  605 */       return ((JTextField)this.comboBox.getEditor().getEditorComponent()).getText();
/*      */     }
/*      */ 
/*      */     public void setHighlighted(boolean value) {
/*  609 */       if (value)
/*  610 */         this.comboBox.setBorder(new LineBorder(Color.RED, 1));
/*      */       else {
/*  612 */         this.comboBox.setBorder(null);
/*      */       }
/*  614 */       this.comboBox.requestFocus();
/*      */     }
/*      */   }
/*      */ 
/*      */   private static final class PropertiesTree extends JTree
/*      */   {
/*      */     public PropertiesTree(CommonThemeSettingsPanel themeSettingsPanel)
/*      */     {
/*  274 */       DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
/*  275 */       rootNode.add(new DefaultMutableTreeNode("Common")
/*      */       {
/*      */       });
/*  285 */       rootNode.add(new DefaultMutableTreeNode("Ticks")
/*      */       {
/*      */       });
/*  291 */       rootNode.add(new DefaultMutableTreeNode("Bars")
/*      */       {
/*      */       });
/*  298 */       rootNode.add(new DefaultMutableTreeNode("Line")
/*      */       {
/*      */       });
/*  304 */       rootNode.add(new DefaultMutableTreeNode("Candles")
/*      */       {
/*      */       });
/*  313 */       rootNode.add(new DefaultMutableTreeNode("Axis")
/*      */       {
/*      */       });
/*  323 */       rootNode.add(new DefaultMutableTreeNode("Orders")
/*      */       {
/*      */       });
/*  334 */       rootNode.add(new DefaultMutableTreeNode("Misc")
/*      */       {
/*      */       });
/*  343 */       rootNode.add(new DefaultMutableTreeNode("Text")
/*      */       {
/*      */       });
/*  352 */       rootNode.add(new DefaultMutableTreeNode("Table")
/*      */       {
/*      */       });
/*  361 */       rootNode.add(new DefaultMutableTreeNode("Historical tester")
/*      */       {
/*      */       });
/*  369 */       setModel(new DefaultTreeModel(rootNode));
/*  370 */       setRootVisible(false);
/*  371 */       setToggleClickCount(1);
/*  372 */       setAutoscrolls(true);
/*  373 */       setScrollsOnExpand(true);
/*  374 */       setExpandsSelectedPaths(true);
/*      */ 
/*  376 */       setCellRenderer(new CommonThemeSettingsPanel.ThemePropertiesCellRenderer(themeSettingsPanel));
/*      */ 
/*  378 */       addTreeSelectionListener(new TreeSelectionListener(themeSettingsPanel)
/*      */       {
/*      */         public void valueChanged(TreeSelectionEvent e) {
/*  381 */           TreePath selectionPath = e.getNewLeadSelectionPath();
/*      */ 
/*  383 */           if (selectionPath != null) {
/*  384 */             DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)selectionPath.getLastPathComponent();
/*  385 */             this.val$themeSettingsPanel.editorsPanel.set(selectedNode.getUserObject());
/*      */           }
/*      */         }
/*      */       });
/*  390 */       addTreeExpansionListener(new TreeExpansionListener(themeSettingsPanel, rootNode)
/*      */       {
/*      */         public void treeExpanded(TreeExpansionEvent event) {
/*  393 */           Object node = event.getPath().getLastPathComponent();
/*  394 */           collapse(node);
/*  395 */           this.val$themeSettingsPanel.editorsPanel.set(node.toString());
/*      */         }
/*      */ 
/*      */         public void treeCollapsed(TreeExpansionEvent event)
/*      */         {
/*  400 */           Object node = event.getPath().getLastPathComponent();
/*  401 */           this.val$themeSettingsPanel.editorsPanel.set(node.toString());
/*      */         }
/*      */ 
/*      */         private void collapse(Object excludeNode) {
/*  405 */           for (int i = 0; i < this.val$rootNode.getChildCount(); i++) {
/*  406 */             TreeNode node = this.val$rootNode.getChildAt(i);
/*      */ 
/*  408 */             if (node != excludeNode)
/*  409 */               CommonThemeSettingsPanel.PropertiesTree.this.collapsePath(new TreePath(new Object[] { this.val$rootNode, node }));
/*      */           }
/*      */         }
/*      */       });
/*  415 */       expandRow(0);
/*      */     }
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.theme.CommonThemeSettingsPanel
 * JD-Core Version:    0.6.0
 */