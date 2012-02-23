/*     */ package com.dukascopy.charts.view.swing;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.drawings.IPatternWidgetChartObject.Pattern;
/*     */ import com.dukascopy.api.drawings.IWidgetChartObject;
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.chartbuilder.IMainOperationManager;
/*     */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*     */ import com.dukascopy.charts.data.datacache.Data;
/*     */ import com.dukascopy.charts.drawings.AbstractWidgetChartObject;
/*     */ import com.dukascopy.charts.drawings.IDrawingsManager;
/*     */ import com.dukascopy.charts.drawings.PatternWidgetChartObject;
/*     */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*     */ import com.dukascopy.charts.mappers.time.ITimeToXMapper;
/*     */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*     */ import com.dukascopy.charts.math.dataprovider.AbstractDataSequence;
/*     */ import com.dukascopy.charts.utils.formatter.DateFormatter;
/*     */ import com.dukascopy.charts.utils.formatter.FormattersManager;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.util.SpringUtilities;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.event.ItemListener;
/*     */ import java.util.EnumMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.swing.ButtonGroup;
/*     */ import javax.swing.ComboBoxModel;
/*     */ import javax.swing.Icon;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JList;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JProgressBar;
/*     */ import javax.swing.JRadioButton;
/*     */ import javax.swing.JSlider;
/*     */ import javax.swing.ListCellRenderer;
/*     */ import javax.swing.ListModel;
/*     */ import javax.swing.SpringLayout;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.ChangeListener;
/*     */ import javax.swing.event.PopupMenuEvent;
/*     */ import javax.swing.event.PopupMenuListener;
/*     */ 
/*     */ public class PatternChartWidgetPanel extends AbstractChartWidgetPanel
/*     */ {
/*  85 */   private static final long serialVersionUID = 1L;
/*  67 */   private static final Icon ICON_ASCENDING_TRIANGLE = StratUtils.loadImageIcon("rc/media/pattern_ascending_triangle.jpg");
/*  68 */   private static final Icon ICON_CHANNEL_DOWN = StratUtils.loadImageIcon("rc/media/pattern_channel_down.jpg");
/*  69 */   private static final Icon ICON_CHANNEL_UP = StratUtils.loadImageIcon("rc/media/pattern_channel_up.jpg");
/*  70 */   private static final Icon ICON_DESCENDING_TRIANGLE = StratUtils.loadImageIcon("rc/media/pattern_descending_triangle.jpg");
/*  71 */   private static final Icon ICON_DOUBLE_BOTTOM = StratUtils.loadImageIcon("rc/media/pattern_double_bottom.jpg");
/*  72 */   private static final Icon ICON_DOUBLE_TOP = StratUtils.loadImageIcon("rc/media/pattern_double_top.jpg");
/*  73 */   private static final Icon ICON_FALLING_WEDGE = StratUtils.loadImageIcon("rc/media/pattern_falling_wedge.jpg");
/*  74 */   private static final Icon ICON_FLAG = StratUtils.loadImageIcon("rc/media/pattern_flag.jpg");
/*  75 */   private static final Icon ICON_HEAD_AND_SHOULDERS = StratUtils.loadImageIcon("rc/media/pattern_head_and_shoulders.jpg");
/*  76 */   private static final Icon ICON_INVERSE_HEAD_AND_SHOULDERS = StratUtils.loadImageIcon("rc/media/pattern_inverse_head_and_shoulders.jpg");
/*  77 */   private static final Icon ICON_PENNANT = StratUtils.loadImageIcon("rc/media/pattern_pennant.jpg");
/*  78 */   private static final Icon ICON_RECTANGLE = StratUtils.loadImageIcon("rc/media/pattern_rectangle.jpg");
/*  79 */   private static final Icon ICON_RISING_WEDGE = StratUtils.loadImageIcon("rc/media/pattern_rising_wedge.jpg");
/*  80 */   private static final Icon ICON_TRIANGLE = StratUtils.loadImageIcon("rc/media/pattern_triangle.jpg");
/*  81 */   private static final Icon ICON_TRIPLE_BOTTOM = StratUtils.loadImageIcon("rc/media/pattern_triple_bottom.jpg");
/*  82 */   private static final Icon ICON_TRIPLE_TOP = StratUtils.loadImageIcon("rc/media/pattern_triple_top.jpg");
/*     */ 
/*  85 */   private static final Map<IPatternWidgetChartObject.Pattern, String> patternsStringValues = new EnumMap() { private static final long serialVersionUID = 1L; } ;
/*     */   private final PatternWidgetCalculationModule calculationModule;
/*     */   private final IMainOperationManager mainOperationManager;
/*     */   private JRadioButton rdbSortByQuality;
/*     */   private JRadioButton rdbSortByMagnitude;
/*     */   private JComboBox cmbFoundPatterns;
/*     */   private JButton btnFind;
/*     */   private JCheckBox chkShowAll;
/*     */   private JCheckBox chkOnlyEmerging;
/*     */   private JLabel lblPatternStart;
/*     */   private JLabel lblLength;
/*     */   private JProgressBar prgBarQuality;
/*     */   private JProgressBar prgBarMagnitude;
/*     */   private JRadioButton rdbClose;
/*     */   private JRadioButton rdbHighLow;
/*     */   private JSlider sldQuality;
/*     */   private JSlider sldMagnitude;
/*     */   private JLabel lblQuality;
/*     */   private JLabel lblMagnitude;
/* 130 */   private boolean userInteractingWithCombo = false;
/* 131 */   private boolean updateComboAfterUserInteraction = false;
/*     */ 
/*     */   public PatternChartWidgetPanel(PatternWidgetChartObject chartObject, IDrawingsManager drawingManager, ChartState chartState, Map<DataType, AbstractDataSequenceProvider<? extends AbstractDataSequence<? extends Data>, ? extends Data>> allDataSequenceProviders, GeometryCalculator geometryCalculator, IMainOperationManager mainOperationManager, ITimeToXMapper timeToXMapper, IValueToYMapper valueToYMapper)
/*     */   {
/* 144 */     super(chartObject, drawingManager);
/*     */ 
/* 146 */     this.mainOperationManager = mainOperationManager;
/*     */ 
/* 148 */     this.calculationModule = new PatternWidgetCalculationModule(this, chartObject, chartState, allDataSequenceProviders, geometryCalculator, timeToXMapper, valueToYMapper);
/*     */ 
/* 158 */     this.calculationModule.setPivotPointsPrice(chartObject.getPivotPointsPrice());
/* 159 */     this.calculationModule.setPatternQuality(chartObject.getDesiredMinQuality());
/*     */   }
/*     */ 
/*     */   public void drawPatterns(Graphics g)
/*     */   {
/* 167 */     this.calculationModule.recalculateIfNecessary(g);
/*     */   }
/*     */ 
/*     */   public void recalculate() {
/* 171 */     this.calculationModule.recalculate(null);
/*     */   }
/*     */ 
/*     */   public void selectPattern(PatternWidgetCalculationModule.PatternOutput pattern) {
/* 175 */     DateFormatter df = ((AbstractWidgetChartObject)this.chartObject).getFormattersManager().getDateFormatter();
/* 176 */     getLblPatternStart().setText(df.formatTimeWithoutDate(pattern.getTime()) + " " + df.formatDateWithoutTime(pattern.getTime()));
/* 177 */     getLblLength().setText(pattern.getLength() + " bars");
/*     */ 
/* 179 */     getPrgBarQuality().setValue(pattern.getQuality());
/* 180 */     setupProgressBarColor(getPrgBarQuality(), pattern.getQuality());
/* 181 */     getPrgBarQuality().setVisible(true);
/*     */ 
/* 183 */     getPrgBarMagnitude().setValue(pattern.getMagnitude());
/* 184 */     setupProgressBarColor(getPrgBarMagnitude(), pattern.getMagnitude());
/* 185 */     getPrgBarMagnitude().setVisible(true);
/*     */ 
/* 187 */     this.calculationModule.setSelectedPattern(pattern);
/*     */   }
/*     */ 
/*     */   public void deselectAllPatterns() {
/* 191 */     getLblPatternStart().setText("");
/* 192 */     getLblLength().setText("");
/* 193 */     getPrgBarQuality().setVisible(false);
/* 194 */     getPrgBarMagnitude().setVisible(false);
/* 195 */     this.calculationModule.setSelectedPattern(null);
/*     */   }
/*     */ 
/*     */   protected String getTitle()
/*     */   {
/* 202 */     return LocalizationManager.getText("item.pattern.widget");
/*     */   }
/*     */ 
/*     */   protected JPanel createInfoContentPanel()
/*     */   {
/* 207 */     JPanel infoPanel = new JPanel(new SpringLayout());
/* 208 */     infoPanel.setOpaque(false);
/*     */ 
/* 210 */     String fontName = this.chartObject.getFont().getFontName();
/* 211 */     Color color = this.chartObject.getColor();
/*     */ 
/* 213 */     ButtonGroup sortByGroup = new ButtonGroup();
/* 214 */     sortByGroup.add(getRdbSortByQuality());
/* 215 */     sortByGroup.add(getRdbSortByMagnitude());
/*     */ 
/* 217 */     JPanel sortByPanel = createNCPairComponentsPanel(getRdbSortByQuality(), getRdbSortByMagnitude());
/* 218 */     JPanel showOptionsPanel = createNCPairComponentsPanel(getChkShowAll(), getChkOnlyEmerging());
/*     */ 
/* 220 */     JPanel foundPatternsPanel = createCEPairComponentsPanel(getCmbFoundPatterns(), getBtnFind());
/* 221 */     foundPatternsPanel.setMaximumSize(new Dimension(foundPatternsPanel.getPreferredSize().width, 20));
/*     */ 
/* 223 */     infoPanel.add(createRightAlignedLabel("menu.item.sort.by", fontName, color));
/* 224 */     infoPanel.add(createCEPairComponentsPanel(sortByPanel, showOptionsPanel));
/*     */ 
/* 226 */     infoPanel.add(createRightAlignedLabel("menu.item.found.patterns", fontName, color));
/* 227 */     infoPanel.add(foundPatternsPanel);
/*     */ 
/* 229 */     infoPanel.add(createRightAlignedLabel("menu.item.pattern.start", fontName, color));
/* 230 */     infoPanel.add(getLblPatternStart());
/*     */ 
/* 232 */     infoPanel.add(createRightAlignedLabel("menu.item.length", fontName, color));
/* 233 */     infoPanel.add(getLblLength());
/*     */ 
/* 235 */     infoPanel.add(createRightAlignedLabel("menu.item.quality", fontName, color));
/* 236 */     infoPanel.add(getPrgBarQuality());
/*     */ 
/* 238 */     infoPanel.add(createRightAlignedLabel("menu.item.magnitude", fontName, color));
/* 239 */     infoPanel.add(getPrgBarMagnitude());
/*     */ 
/* 241 */     SpringUtilities.makeCompactGrid(infoPanel, 6, 2, 5, 5, 10, 5);
/*     */ 
/* 243 */     return infoPanel;
/*     */   }
/*     */ 
/*     */   protected JPanel createCustomEditPanel()
/*     */   {
/* 249 */     JPanel editPanel = new JPanel(new SpringLayout());
/* 250 */     editPanel.setOpaque(false);
/*     */ 
/* 252 */     String fontName = this.chartObject.getFont().getFontName();
/* 253 */     Color color = this.chartObject.getColor();
/*     */ 
/* 255 */     ButtonGroup priceGroup = new ButtonGroup();
/* 256 */     priceGroup.add(getRdbClose());
/* 257 */     priceGroup.add(getRdbHighLow());
/*     */ 
/* 259 */     editPanel.add(createNCPairComponentsPanel(getRdbClose(), getRdbHighLow()));
/* 260 */     editPanel.add(createLabel("menu.item.price", fontName, color));
/*     */ 
/* 262 */     editPanel.add(new JLabel());
/* 263 */     editPanel.add(new JLabel());
/*     */ 
/* 265 */     editPanel.add(getSldQuality());
/* 266 */     editPanel.add(createCEPairComponentsPanel(getLblQuality(), createLocalizableLabel("menu.item.lowercase.quality", fontName, color)));
/*     */ 
/* 268 */     editPanel.add(getSldMagnitude());
/* 269 */     editPanel.add(createCEPairComponentsPanel(getLblMagnitude(), createLocalizableLabel("menu.item.lowercase.magnitude", fontName, color)));
/*     */ 
/* 273 */     editPanel.add(createWCPairComponentsPanel(createCheckBox(IPatternWidgetChartObject.Pattern.ASCENDING_TRIANGLE, true), new JLabel(ICON_ASCENDING_TRIANGLE)));
/* 274 */     editPanel.add(createLabel("menu.item.ascending.triangle", fontName, color));
/*     */ 
/* 276 */     editPanel.add(createWCPairComponentsPanel(createCheckBox(IPatternWidgetChartObject.Pattern.DESCENDING_TRIANGLE, true), new JLabel(ICON_DESCENDING_TRIANGLE)));
/* 277 */     editPanel.add(createLabel("menu.item.descending.triangle", fontName, color));
/*     */ 
/* 279 */     editPanel.add(createWCPairComponentsPanel(createCheckBox(IPatternWidgetChartObject.Pattern.CHANNEL_UP, true), new JLabel(ICON_CHANNEL_UP)));
/* 280 */     editPanel.add(createLabel("menu.item.channel.up", fontName, color));
/*     */ 
/* 282 */     editPanel.add(createWCPairComponentsPanel(createCheckBox(IPatternWidgetChartObject.Pattern.CHANNEL_DOWN, true), new JLabel(ICON_CHANNEL_DOWN)));
/* 283 */     editPanel.add(createLabel("menu.item.channel.down", fontName, color));
/*     */ 
/* 285 */     editPanel.add(createWCPairComponentsPanel(createCheckBox(IPatternWidgetChartObject.Pattern.DOUBLE_TOP, false), new JLabel(ICON_DOUBLE_TOP)));
/* 286 */     editPanel.add(createLabel("menu.item.double.top", fontName, color));
/*     */ 
/* 288 */     editPanel.add(createWCPairComponentsPanel(createCheckBox(IPatternWidgetChartObject.Pattern.DOUBLE_BOTTOM, false), new JLabel(ICON_DOUBLE_BOTTOM)));
/* 289 */     editPanel.add(createLabel("menu.item.double.bottom", fontName, color));
/*     */ 
/* 291 */     editPanel.add(createWCPairComponentsPanel(createCheckBox(IPatternWidgetChartObject.Pattern.HEAD_AND_SHOULDERS, false), new JLabel(ICON_HEAD_AND_SHOULDERS)));
/* 292 */     editPanel.add(createLabel("menu.item.head.and.shoulders", fontName, color));
/*     */ 
/* 294 */     editPanel.add(createWCPairComponentsPanel(createCheckBox(IPatternWidgetChartObject.Pattern.INVERSE_HEAD_AND_SHOULDERS, false), new JLabel(ICON_INVERSE_HEAD_AND_SHOULDERS)));
/* 295 */     editPanel.add(createLabel("menu.item.inverse.head.and.shoulders", fontName, color));
/*     */ 
/* 297 */     editPanel.add(createWCPairComponentsPanel(createCheckBox(IPatternWidgetChartObject.Pattern.RISING_WEDGE, true), new JLabel(ICON_RISING_WEDGE)));
/* 298 */     editPanel.add(createLabel("menu.item.rising.wedge", fontName, color));
/*     */ 
/* 300 */     editPanel.add(createWCPairComponentsPanel(createCheckBox(IPatternWidgetChartObject.Pattern.FALLING_WEDGE, true), new JLabel(ICON_FALLING_WEDGE)));
/* 301 */     editPanel.add(createLabel("menu.item.falling.wedge", fontName, color));
/*     */ 
/* 303 */     editPanel.add(createWCPairComponentsPanel(createCheckBox(IPatternWidgetChartObject.Pattern.TRIPLE_TOP, false), new JLabel(ICON_TRIPLE_TOP)));
/* 304 */     editPanel.add(createLabel("menu.item.triple.top", fontName, color));
/*     */ 
/* 306 */     editPanel.add(createWCPairComponentsPanel(createCheckBox(IPatternWidgetChartObject.Pattern.TRIPLE_BOTTOM, false), new JLabel(ICON_TRIPLE_BOTTOM)));
/* 307 */     editPanel.add(createLabel("menu.item.triple.bottom", fontName, color));
/*     */ 
/* 309 */     editPanel.add(createWCPairComponentsPanel(createCheckBox(IPatternWidgetChartObject.Pattern.RECTANGLE, true), new JLabel(ICON_RECTANGLE)));
/* 310 */     editPanel.add(createLabel("menu.item.rectangle", fontName, color));
/*     */ 
/* 312 */     editPanel.add(createWCPairComponentsPanel(createCheckBox(IPatternWidgetChartObject.Pattern.TRIANGLE, true), new JLabel(ICON_TRIANGLE)));
/* 313 */     editPanel.add(createLabel("menu.item.triangle", fontName, color));
/*     */ 
/* 315 */     editPanel.add(createWCPairComponentsPanel(createCheckBox(IPatternWidgetChartObject.Pattern.PENNANT, false), new JLabel(ICON_PENNANT)));
/* 316 */     editPanel.add(createLabel("menu.item.pennant", fontName, color));
/*     */ 
/* 318 */     editPanel.add(createWCPairComponentsPanel(createCheckBox(IPatternWidgetChartObject.Pattern.FLAG, false), new JLabel(ICON_FLAG)));
/* 319 */     editPanel.add(createLabel("menu.item.flag", fontName, color));
/*     */ 
/* 321 */     SpringUtilities.makeCompactGrid(editPanel, 10, 4, 5, 5, 10, 3);
/*     */ 
/* 324 */     Dimension patternsPanelMinSize = ((SpringLayout)editPanel.getLayout()).minimumLayoutSize(editPanel);
/* 325 */     editPanel.setMinimumSize(patternsPanelMinSize);
/*     */ 
/* 327 */     return editPanel;
/*     */   }
/*     */ 
/*     */   protected Dimension getCommonEditPanelSize()
/*     */   {
/* 332 */     return new Dimension(300, 160);
/*     */   }
/*     */ 
/*     */   public void setWidgetBounds(Rectangle r)
/*     */   {
/* 338 */     if ("VIEW_MODE".equals(this.mode)) {
/* 339 */       setWidgetPosition(r.x, r.y);
/*     */ 
/* 341 */       if ((r.width != getWidth()) || (r.height != getHeight()))
/*     */       {
/* 343 */         r.width = Math.max(300, r.width);
/* 344 */         r.height = Math.max(160, r.height);
/* 345 */         this.chartObject.setPreferredSize(new Dimension(r.width, r.height));
/* 346 */         drawingModelModified();
/*     */       }
/* 348 */     } else if ("EDIT_COMMON_MODE".equals(this.mode)) {
/* 349 */       setWidgetPosition(r.x, r.y);
/*     */     } else {
/* 351 */       super.setWidgetBounds(r);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void setupProgressBarColor(JProgressBar progressBar, int percent)
/*     */   {
/* 358 */     if (percent < 40)
/* 359 */       progressBar.setForeground(Color.red);
/* 360 */     else if (percent < 70)
/* 361 */       progressBar.setForeground(Color.yellow);
/*     */     else
/* 363 */       progressBar.setForeground(Color.green);
/*     */   }
/*     */ 
/*     */   private JPanel createNCPairComponentsPanel(JComponent north, JComponent center)
/*     */   {
/* 368 */     JPanel panel = new JPanel(new BorderLayout());
/* 369 */     panel.setOpaque(false);
/* 370 */     panel.add(north, "North");
/* 371 */     panel.add(center, "Center");
/*     */ 
/* 373 */     return panel;
/*     */   }
/*     */ 
/*     */   private JPanel createCEPairComponentsPanel(JComponent center, JComponent east) {
/* 377 */     JPanel panel = new JPanel(new BorderLayout());
/* 378 */     panel.setOpaque(false);
/* 379 */     panel.add(center, "Center");
/* 380 */     panel.add(east, "East");
/*     */ 
/* 382 */     return panel;
/*     */   }
/*     */ 
/*     */   private JPanel createWCPairComponentsPanel(JComponent west, JComponent center) {
/* 386 */     JPanel panel = new JPanel(new BorderLayout());
/* 387 */     panel.setOpaque(false);
/* 388 */     panel.add(west, "West");
/* 389 */     panel.add(center, "Center");
/*     */ 
/* 391 */     return panel;
/*     */   }
/*     */ 
/*     */   public void updateInfoTab() {
/* 395 */     if (this.userInteractingWithCombo) {
/* 396 */       this.updateComboAfterUserInteraction = true;
/*     */ 
/* 398 */       return;
/*     */     }
/*     */ 
/* 401 */     List foundPatterns = this.calculationModule.getFoundPatterns();
/*     */ 
/* 403 */     ComboBoxModel model = getCmbFoundPatterns().getModel();
/*     */ 
/* 405 */     PatternWidgetCalculationModule.PatternOutput selected = (PatternWidgetCalculationModule.PatternOutput)model.getSelectedItem();
/* 406 */     boolean hasSelected = false;
/*     */ 
/* 408 */     getCmbFoundPatterns().removeAllItems();
/* 409 */     for (PatternWidgetCalculationModule.PatternOutput pattern : foundPatterns) {
/* 410 */       getCmbFoundPatterns().addItem(pattern);
/* 411 */       if (pattern.equals(selected)) {
/* 412 */         hasSelected = true;
/* 413 */         selected = pattern;
/*     */       }
/*     */     }
/*     */ 
/* 417 */     if (hasSelected)
/* 418 */       model.setSelectedItem(selected);
/*     */   }
/*     */ 
/*     */   private JComboBox getCmbFoundPatterns()
/*     */   {
/* 424 */     if (this.cmbFoundPatterns == null) {
/* 425 */       this.cmbFoundPatterns = new JComboBox();
/* 426 */       this.cmbFoundPatterns.setRenderer(createPatternsListCellRenderer());
/*     */ 
/* 428 */       this.cmbFoundPatterns.addItemListener(new ItemListener()
/*     */       {
/*     */         public void itemStateChanged(ItemEvent e)
/*     */         {
/* 433 */           if (e.getStateChange() == 1) {
/* 434 */             PatternWidgetCalculationModule.PatternOutput value = (PatternWidgetCalculationModule.PatternOutput)PatternChartWidgetPanel.this.getCmbFoundPatterns().getSelectedItem();
/* 435 */             PatternChartWidgetPanel.this.selectPattern(value);
/*     */           } else {
/* 437 */             PatternChartWidgetPanel.this.deselectAllPatterns();
/*     */           }
/*     */         }
/*     */       });
/* 442 */       this.cmbFoundPatterns.addPopupMenuListener(new PopupMenuListener()
/*     */       {
/*     */         public void popupMenuWillBecomeVisible(PopupMenuEvent e)
/*     */         {
/* 446 */           PatternChartWidgetPanel.access$102(PatternChartWidgetPanel.this, true);
/*     */         }
/*     */ 
/*     */         public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
/*     */         {
/* 451 */           PatternChartWidgetPanel.access$102(PatternChartWidgetPanel.this, false);
/* 452 */           if (PatternChartWidgetPanel.this.updateComboAfterUserInteraction) {
/* 453 */             PatternChartWidgetPanel.this.updateInfoTab();
/*     */ 
/* 455 */             PatternChartWidgetPanel.access$202(PatternChartWidgetPanel.this, false);
/*     */           }
/*     */         }
/*     */ 
/*     */         public void popupMenuCanceled(PopupMenuEvent e)
/*     */         {
/*     */         }
/*     */       });
/*     */     }
/* 465 */     return this.cmbFoundPatterns;
/*     */   }
/*     */ 
/*     */   private ListCellRenderer createPatternsListCellRenderer() {
/* 469 */     return new Object()
/*     */     {
/*     */       public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
/*     */       {
/* 473 */         PatternWidgetCalculationModule.PatternOutput output = (PatternWidgetCalculationModule.PatternOutput)value;
/* 474 */         JLabel label = new JLabel();
/* 475 */         if (output != null) {
/* 476 */           int percent = 0;
/* 477 */           if (((PatternWidgetChartObject)PatternChartWidgetPanel.this.chartObject).getSortPatternsByCriteria() == 0)
/* 478 */             percent = output.getQuality();
/*     */           else {
/* 480 */             percent = output.getMagnitude();
/*     */           }
/*     */ 
/* 483 */           label.setText(new StringBuilder().append((String)PatternChartWidgetPanel.patternsStringValues.get(output.getPattern())).append(" ").append(percent).append("%").append(output.isWholePattern() ? "" : "*").toString());
/*     */         }
/*     */ 
/* 486 */         if (list.getModel().getSize() == 0) {
/* 487 */           label.setText("Nothing found");
/*     */         }
/*     */ 
/* 490 */         if (isSelected) {
/* 491 */           label.setForeground(Color.BLUE);
/*     */         }
/* 494 */         else if ((output == null) || (output.isWholePattern()))
/* 495 */           label.setForeground(list.getForeground());
/*     */         else {
/* 497 */           label.setForeground(Color.GREEN);
/*     */         }
/*     */ 
/* 500 */         return label;
/*     */       } } ;
/*     */   }
/*     */ 
/*     */   private JButton getBtnFind() {
/* 506 */     if (this.btnFind == null) {
/* 507 */       this.btnFind = new JButton("Go");
/* 508 */       this.btnFind.setOpaque(false);
/* 509 */       this.btnFind.setPreferredSize(new Dimension(33, 18));
/* 510 */       this.btnFind.setBorder(null);
/*     */ 
/* 512 */       this.btnFind.addActionListener(new ActionListener()
/*     */       {
/*     */         public void actionPerformed(ActionEvent e)
/*     */         {
/* 516 */           PatternWidgetCalculationModule.PatternOutput pattern = (PatternWidgetCalculationModule.PatternOutput)PatternChartWidgetPanel.this.getCmbFoundPatterns().getSelectedItem();
/* 517 */           if (pattern != null)
/* 518 */             PatternChartWidgetPanel.this.mainOperationManager.setCustomRange(30, pattern.getTime(), 30 + pattern.getLength());
/*     */         }
/*     */       });
/*     */     }
/* 523 */     return this.btnFind;
/*     */   }
/*     */ 
/*     */   private JCheckBox getChkShowAll() {
/* 527 */     if (this.chkShowAll == null) {
/* 528 */       this.chkShowAll = createCheckBox("menu.item.show.all", this.chartObject.getFont().getFontName(), this.chartObject.getColor());
/* 529 */       this.chkShowAll.setSelected(((PatternWidgetChartObject)this.chartObject).isShowAll());
/*     */ 
/* 531 */       this.chkShowAll.setPreferredSize(new Dimension(100, 13));
/* 532 */       this.chkShowAll.setMinimumSize(new Dimension(100, 13));
/* 533 */       this.chkShowAll.setMaximumSize(new Dimension(100, 13));
/*     */ 
/* 535 */       this.chkShowAll.addItemListener(new ItemListener()
/*     */       {
/*     */         public void itemStateChanged(ItemEvent e)
/*     */         {
/* 539 */           boolean showAll = e.getStateChange() == 1;
/*     */ 
/* 541 */           ((PatternWidgetChartObject)PatternChartWidgetPanel.this.chartObject).setShowAll(showAll);
/* 542 */           PatternChartWidgetPanel.this.drawingModelModified();
/*     */         } } );
/*     */     }
/* 546 */     return this.chkShowAll;
/*     */   }
/*     */ 
/*     */   private JCheckBox getChkOnlyEmerging() {
/* 550 */     if (this.chkOnlyEmerging == null) {
/* 551 */       this.chkOnlyEmerging = createCheckBox("menu.item.only.emerging", this.chartObject.getFont().getFontName(), this.chartObject.getColor());
/* 552 */       this.chkOnlyEmerging.setSelected(((PatternWidgetChartObject)this.chartObject).isOnlyEmerging());
/*     */ 
/* 554 */       this.chkOnlyEmerging.setPreferredSize(new Dimension(100, 13));
/* 555 */       this.chkOnlyEmerging.setMinimumSize(new Dimension(100, 13));
/* 556 */       this.chkOnlyEmerging.setMaximumSize(new Dimension(100, 13));
/*     */ 
/* 558 */       this.chkOnlyEmerging.addItemListener(new ItemListener()
/*     */       {
/*     */         public void itemStateChanged(ItemEvent e)
/*     */         {
/* 562 */           boolean onlyEmerging = e.getStateChange() == 1;
/*     */ 
/* 564 */           ((PatternWidgetChartObject)PatternChartWidgetPanel.this.chartObject).setOnlyEmerging(onlyEmerging);
/* 565 */           PatternChartWidgetPanel.this.drawingModelModified();
/*     */ 
/* 567 */           PatternChartWidgetPanel.this.recalculate();
/*     */         } } );
/*     */     }
/* 571 */     return this.chkOnlyEmerging;
/*     */   }
/*     */ 
/*     */   private JLabel getLblPatternStart() {
/* 575 */     if (this.lblPatternStart == null) {
/* 576 */       this.lblPatternStart = createBoldLabel(this.chartObject.getFont().getFontName(), this.chartObject.getColor());
/*     */     }
/* 578 */     return this.lblPatternStart;
/*     */   }
/*     */ 
/*     */   private JLabel getLblLength() {
/* 582 */     if (this.lblLength == null) {
/* 583 */       this.lblLength = createBoldLabel(this.chartObject.getFont().getFontName(), this.chartObject.getColor());
/*     */     }
/* 585 */     return this.lblLength;
/*     */   }
/*     */ 
/*     */   private JProgressBar getPrgBarQuality() {
/* 589 */     if (this.prgBarQuality == null) {
/* 590 */       this.prgBarQuality = createProgressBar();
/*     */     }
/* 592 */     return this.prgBarQuality;
/*     */   }
/*     */ 
/*     */   private JProgressBar getPrgBarMagnitude() {
/* 596 */     if (this.prgBarMagnitude == null) {
/* 597 */       this.prgBarMagnitude = createProgressBar();
/*     */     }
/* 599 */     return this.prgBarMagnitude;
/*     */   }
/*     */ 
/*     */   private JProgressBar createProgressBar() {
/* 603 */     JProgressBar prgBar = new JProgressBar();
/* 604 */     prgBar.setStringPainted(true);
/* 605 */     prgBar.setMinimum(0);
/* 606 */     prgBar.setMaximum(100);
/* 607 */     prgBar.setVisible(false);
/* 608 */     prgBar.setMinimumSize(new Dimension(75, 13));
/* 609 */     prgBar.setPreferredSize(new Dimension(prgBar.getPreferredSize().width, 18));
/* 610 */     prgBar.setMaximumSize(new Dimension(2147483647, 25));
/*     */ 
/* 612 */     return prgBar;
/*     */   }
/*     */ 
/*     */   private JRadioButton getRdbSortByQuality() {
/* 616 */     if (this.rdbSortByQuality == null) {
/* 617 */       this.rdbSortByQuality = createRadioButton("menu.item.lowercase.quality", this.chartObject.getFont().getFontName(), this.chartObject.getColor());
/* 618 */       boolean selected = ((PatternWidgetChartObject)this.chartObject).getSortPatternsByCriteria() == 0;
/* 619 */       this.rdbSortByQuality.setSelected(selected);
/*     */ 
/* 621 */       this.rdbSortByQuality.addItemListener(createSortByRadioButtonItemListener(0));
/*     */     }
/* 623 */     return this.rdbSortByQuality;
/*     */   }
/*     */ 
/*     */   private JRadioButton getRdbSortByMagnitude() {
/* 627 */     if (this.rdbSortByMagnitude == null) {
/* 628 */       this.rdbSortByMagnitude = createRadioButton("menu.item.lowercase.magnitude", this.chartObject.getFont().getFontName(), this.chartObject.getColor());
/* 629 */       boolean selected = ((PatternWidgetChartObject)this.chartObject).getSortPatternsByCriteria() == 1;
/* 630 */       this.rdbSortByMagnitude.setSelected(selected);
/* 631 */       this.rdbSortByMagnitude.setPreferredSize(new Dimension(75, 13));
/*     */ 
/* 633 */       this.rdbSortByMagnitude.addItemListener(createSortByRadioButtonItemListener(1));
/*     */     }
/* 635 */     return this.rdbSortByMagnitude;
/*     */   }
/*     */ 
/*     */   private ItemListener createSortByRadioButtonItemListener(int criteria) {
/* 639 */     return new ItemListener(criteria)
/*     */     {
/*     */       public void itemStateChanged(ItemEvent e)
/*     */       {
/* 643 */         if (e.getStateChange() == 1) {
/* 644 */           ((PatternWidgetChartObject)PatternChartWidgetPanel.this.chartObject).setSortPatternsByCriteria(this.val$criteria);
/* 645 */           PatternChartWidgetPanel.this.drawingModelModified();
/*     */ 
/* 647 */           PatternChartWidgetPanel.this.recalculate();
/*     */         }
/*     */       } } ;
/*     */   }
/*     */ 
/*     */   private JRadioButton getRdbClose() {
/* 654 */     if (this.rdbClose == null) {
/* 655 */       this.rdbClose = createRadioButton("menu.item.close", this.chartObject.getFont().getFontName(), this.chartObject.getColor());
/* 656 */       boolean selected = "Close".equals(((PatternWidgetChartObject)this.chartObject).getPivotPointsPrice());
/* 657 */       this.rdbClose.setSelected(selected);
/*     */ 
/* 659 */       this.rdbClose.addItemListener(createPriceRadioButtonItemListener("Close"));
/*     */     }
/* 661 */     return this.rdbClose;
/*     */   }
/*     */ 
/*     */   private JRadioButton getRdbHighLow() {
/* 665 */     if (this.rdbHighLow == null) {
/* 666 */       this.rdbHighLow = createRadioButton("menu.item.high.low", this.chartObject.getFont().getFontName(), this.chartObject.getColor());
/* 667 */       boolean selected = "High/Low".equals(((PatternWidgetChartObject)this.chartObject).getPivotPointsPrice());
/* 668 */       this.rdbHighLow.setSelected(selected);
/*     */ 
/* 670 */       this.rdbHighLow.addItemListener(createPriceRadioButtonItemListener("High/Low"));
/*     */     }
/* 672 */     return this.rdbHighLow;
/*     */   }
/*     */ 
/*     */   private ItemListener createPriceRadioButtonItemListener(String price) {
/* 676 */     return new ItemListener(price)
/*     */     {
/*     */       public void itemStateChanged(ItemEvent e)
/*     */       {
/* 680 */         if (e.getStateChange() == 1) {
/* 681 */           ((PatternWidgetChartObject)PatternChartWidgetPanel.this.chartObject).setPivotPointsPrice(this.val$price);
/* 682 */           PatternChartWidgetPanel.this.drawingModelModified();
/* 683 */           PatternChartWidgetPanel.this.calculationModule.setPivotPointsPrice(this.val$price);
/* 684 */           PatternChartWidgetPanel.this.recalculate();
/*     */         }
/*     */       } } ;
/*     */   }
/*     */ 
/*     */   private JSlider getSldQuality() {
/* 691 */     if (this.sldQuality == null) {
/* 692 */       this.sldQuality = createSlider("menu.item.tooltip.slider.quality", ((PatternWidgetChartObject)this.chartObject).getDesiredMinQuality());
/* 693 */       this.sldQuality.addChangeListener(new ChangeListener()
/*     */       {
/*     */         public void stateChanged(ChangeEvent e)
/*     */         {
/* 697 */           PatternChartWidgetPanel.this.getLblQuality().setText(PatternChartWidgetPanel.this.sldQuality.getValue() + "%");
/* 698 */           if (!PatternChartWidgetPanel.this.sldQuality.getValueIsAdjusting()) {
/* 699 */             ((PatternWidgetChartObject)PatternChartWidgetPanel.this.chartObject).setDesiredMinQuality(PatternChartWidgetPanel.this.sldQuality.getValue());
/*     */ 
/* 701 */             PatternChartWidgetPanel.this.drawingModelModified();
/*     */ 
/* 703 */             PatternChartWidgetPanel.this.calculationModule.setPatternQuality(PatternChartWidgetPanel.this.sldQuality.getValue());
/*     */ 
/* 705 */             PatternChartWidgetPanel.this.recalculate();
/*     */           }
/*     */         } } );
/*     */     }
/* 710 */     return this.sldQuality;
/*     */   }
/*     */ 
/*     */   private JLabel getLblQuality() {
/* 714 */     if (this.lblQuality == null) {
/* 715 */       this.lblQuality = new JLabel();
/* 716 */       this.lblQuality.setMinimumSize(new Dimension(30, this.lblQuality.getMinimumSize().height));
/* 717 */       this.lblQuality.setMaximumSize(new Dimension(30, this.lblQuality.getMaximumSize().height));
/* 718 */       this.lblQuality.setSize(30, this.lblQuality.getSize().height);
/* 719 */       this.lblQuality.setForeground(this.chartObject.getColor());
/* 720 */       this.lblQuality.setText(((PatternWidgetChartObject)this.chartObject).getDesiredMinQuality() + "%");
/*     */     }
/* 722 */     return this.lblQuality;
/*     */   }
/*     */ 
/*     */   private JSlider getSldMagnitude() {
/* 726 */     if (this.sldMagnitude == null) {
/* 727 */       this.sldMagnitude = createSlider("menu.item.tooltip.slider.magnitude", ((PatternWidgetChartObject)this.chartObject).getDesiredMinMagnitude());
/* 728 */       this.sldMagnitude.addChangeListener(new ChangeListener()
/*     */       {
/*     */         public void stateChanged(ChangeEvent e)
/*     */         {
/* 732 */           PatternChartWidgetPanel.this.getLblMagnitude().setText(PatternChartWidgetPanel.this.sldMagnitude.getValue() + "%");
/*     */ 
/* 734 */           if (!PatternChartWidgetPanel.this.sldMagnitude.getValueIsAdjusting()) {
/* 735 */             ((PatternWidgetChartObject)PatternChartWidgetPanel.this.chartObject).setDesiredMinMagnitude(PatternChartWidgetPanel.this.sldMagnitude.getValue());
/*     */ 
/* 737 */             PatternChartWidgetPanel.this.drawingModelModified();
/*     */ 
/* 739 */             PatternChartWidgetPanel.this.recalculate();
/*     */           }
/*     */         } } );
/*     */     }
/* 744 */     return this.sldMagnitude;
/*     */   }
/*     */ 
/*     */   private JLabel getLblMagnitude() {
/* 748 */     if (this.lblMagnitude == null) {
/* 749 */       this.lblMagnitude = new JLabel();
/* 750 */       this.lblMagnitude.setMinimumSize(new Dimension(30, 0));
/* 751 */       this.lblMagnitude.setMaximumSize(new Dimension(30, 2147483647));
/* 752 */       this.lblMagnitude.setSize(30, this.lblMagnitude.getSize().height);
/* 753 */       this.lblMagnitude.setForeground(this.chartObject.getColor());
/* 754 */       this.lblMagnitude.setText(((PatternWidgetChartObject)this.chartObject).getDesiredMinMagnitude() + "%");
/*     */     }
/* 756 */     return this.lblMagnitude;
/*     */   }
/*     */ 
/*     */   protected JRadioButton createRadioButton(String localizationKey, String fontName, Color color)
/*     */   {
/* 761 */     JRadioButton radioButton = super.createRadioButton(localizationKey, fontName, color);
/* 762 */     radioButton.setPreferredSize(new Dimension(60, 13));
/* 763 */     radioButton.setMinimumSize(new Dimension(40, 13));
/* 764 */     radioButton.setMaximumSize(new Dimension(60, 13));
/*     */ 
/* 766 */     return radioButton;
/*     */   }
/*     */ 
/*     */   private JSlider createSlider(String tooltipLocalizationKey, int defaultValue) {
/* 770 */     JSlider slider = new JSlider(0, 100, defaultValue);
/* 771 */     slider.setOpaque(false);
/* 772 */     slider.setMajorTickSpacing(50);
/* 773 */     slider.setMinorTickSpacing(10);
/* 774 */     slider.setPaintTicks(true);
/*     */ 
/* 776 */     if (tooltipLocalizationKey != null) {
/* 777 */       slider.setToolTipText(LocalizationManager.getText(tooltipLocalizationKey));
/*     */     }
/*     */ 
/* 780 */     return slider;
/*     */   }
/*     */ 
/*     */   private JCheckBox createCheckBox(IPatternWidgetChartObject.Pattern pattern, boolean enabled) {
/* 784 */     JCheckBox chk = new JCheckBox();
/* 785 */     chk.setOpaque(false);
/*     */ 
/* 787 */     chk.setSelected(((PatternWidgetChartObject)this.chartObject).getPatternsToAnalyze().contains(pattern));
/*     */ 
/* 789 */     chk.addItemListener(new ItemListener(pattern)
/*     */     {
/*     */       public void itemStateChanged(ItemEvent e)
/*     */       {
/* 793 */         PatternChartWidgetPanel.this.onPatternSelectionChanged(this.val$pattern, 1 == e.getStateChange());
/*     */       }
/*     */     });
/* 797 */     chk.setEnabled(enabled);
/* 798 */     if (!enabled) {
/* 799 */       chk.setToolTipText("Work in progress. Will be available in next platform release...");
/*     */     }
/*     */ 
/* 802 */     return chk;
/*     */   }
/*     */ 
/*     */   private void onPatternSelectionChanged(IPatternWidgetChartObject.Pattern pattern, boolean selected) {
/* 806 */     if (selected)
/* 807 */       ((PatternWidgetChartObject)this.chartObject).addPattern(pattern);
/*     */     else {
/* 809 */       ((PatternWidgetChartObject)this.chartObject).removePattern(pattern);
/*     */     }
/*     */ 
/* 812 */     recalculate();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.swing.PatternChartWidgetPanel
 * JD-Core Version:    0.6.0
 */