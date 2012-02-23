/*     */ package com.dukascopy.charts.dialogs.indicators;
/*     */ 
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ import com.dukascopy.charts.chartbuilder.GuiRefresher;
/*     */ import com.dukascopy.charts.dialogs.indicators.listener.IndicatorChangeListener;
/*     */ import com.dukascopy.charts.dialogs.indicators.listener.IndicatorChangeListener.ParameterType;
/*     */ import com.dukascopy.charts.math.dataprovider.IIndicatorsContainer;
/*     */ import com.dukascopy.charts.math.indicators.IndicatorsProvider;
/*     */ import com.dukascopy.dds2.greed.gui.component.JRoundedBorder;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableRoundedBorder;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Insets;
/*     */ import java.util.List;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.DefaultListModel;
/*     */ import javax.swing.JList;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.ListModel;
/*     */ import javax.swing.SwingUtilities;
/*     */ import javax.swing.border.AbstractBorder;
/*     */ import javax.swing.plaf.UIResource;
/*     */ import javax.swing.table.DefaultTableCellRenderer;
/*     */ import javax.swing.table.JTableHeader;
/*     */ import javax.swing.table.TableModel;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class IndicatorSetupPanel extends JPanel
/*     */   implements IndicatorChangeListener
/*     */ {
/*  46 */   private static final Logger LOGGER = LoggerFactory.getLogger(IndicatorSetupPanel.class);
/*     */   private static final int DEFAULT_WIDTH_X_AXIS = 860;
/*     */   private static final int DEFAULT_WIDTH_Y_AXIS = 490;
/*     */   private static final int DEFAULT_HEIGHT = 200;
/*     */   private final AddEditIndicatorDialog parent;
/*     */   private final GuiRefresher guiRefresher;
/*     */   private final IIndicatorsContainer indicatorsContainer;
/*     */   private final boolean isTicks;
/*     */   private final boolean xAxis;
/*     */   private final ParametersPanel parametersPanel;
/*     */   private final OutputsPanel outputsPanel;
/*     */   private final SelectedIndicatorsPanel selectedIndicatorsPanel;
/*     */   private final JPanel emptyPanel;
/*     */   private IndicatorWrapper indicatorWrapper;
/*     */ 
/*     */   public IndicatorSetupPanel(AddEditIndicatorDialog parent, GuiRefresher guiRefresher, IIndicatorsContainer indicatorsContainer, boolean isTicks, boolean xAxis)
/*     */   {
/*  72 */     setLayout(new BoxLayout(this, xAxis ? 2 : 3));
/*  73 */     setBorder(BorderFactory.createEmptyBorder());
/*     */ 
/*  75 */     this.parent = parent;
/*  76 */     this.guiRefresher = guiRefresher;
/*  77 */     this.indicatorsContainer = indicatorsContainer;
/*  78 */     this.isTicks = isTicks;
/*  79 */     this.xAxis = xAxis;
/*     */ 
/*  81 */     add(this.parametersPanel = new ParametersPanel());
/*  82 */     add(this.outputsPanel = new OutputsPanel());
/*     */ 
/*  84 */     add(this.selectedIndicatorsPanel = new SelectedIndicatorsPanel());
/*  85 */     add(this.emptyPanel = new JPanel());
/*     */ 
/*  87 */     this.selectedIndicatorsPanel.setVisible(false);
/*  88 */     this.emptyPanel.setVisible(false);
/*     */   }
/*     */ 
/*     */   public void setListOfIndicators(List<IndicatorWrapper> indicatorWrappers)
/*     */   {
/*  93 */     if ((indicatorWrappers != null) && (indicatorWrappers.size() > 1)) {
/*  94 */       this.parametersPanel.setVisible(false);
/*  95 */       this.outputsPanel.setVisible(false);
/*  96 */       this.selectedIndicatorsPanel.setVisible(true);
/*  97 */       this.emptyPanel.setVisible(true);
/*     */     } else {
/*  99 */       this.parametersPanel.setVisible(true);
/* 100 */       this.outputsPanel.setVisible(true);
/* 101 */       this.selectedIndicatorsPanel.setVisible(false);
/* 102 */       this.emptyPanel.setVisible(false);
/*     */     }
/*     */ 
/* 105 */     this.selectedIndicatorsPanel.updateList(indicatorWrappers);
/*     */   }
/*     */ 
/*     */   public void setIndicator(IndicatorWrapper indicatorWrapper) {
/* 109 */     if (LOGGER.isDebugEnabled()) {
/* 110 */       LOGGER.debug("Set indicator : " + indicatorWrapper);
/*     */     }
/*     */ 
/* 113 */     this.parametersPanel.table.editingCanceled(null);
/* 114 */     this.outputsPanel.table.editingCanceled(null);
/* 115 */     this.indicatorWrapper = indicatorWrapper;
/*     */ 
/* 117 */     this.parametersPanel.tableModel.setIndicator(indicatorWrapper);
/* 118 */     this.outputsPanel.table.setIndicator(indicatorWrapper);
/*     */ 
/* 120 */     if ((!this.xAxis) && (this.parametersPanel.getPreferredSize().getHeight() == 30.0D))
/* 121 */       this.parametersPanel.setVisible(false);
/*     */     else {
/* 123 */       this.parametersPanel.setVisible(true);
/*     */     }
/*     */ 
/* 126 */     indicatorChanged(null);
/*     */   }
/*     */ 
/*     */   public void indicatorChanged(IndicatorChangeListener.ParameterType parameterType)
/*     */   {
/* 134 */     if (LOGGER.isDebugEnabled()) {
/* 135 */       LOGGER.debug("Indicator changed : " + parameterType);
/*     */     }
/*     */ 
/* 138 */     if ((parameterType == IndicatorChangeListener.ParameterType.OUTPUT) || (parameterType == IndicatorChangeListener.ParameterType.LEVEL)) {
/* 139 */       if (this.indicatorWrapper.shouldBeShownOnSubWin()) {
/* 140 */         this.guiRefresher.refreshSubContentByIndicatorId(Integer.valueOf(this.indicatorWrapper.getId()));
/*     */       }
/*     */       else {
/* 143 */         this.guiRefresher.refreshMainContent();
/*     */       }
/* 145 */       this.indicatorsContainer.editIndicator(this.indicatorWrapper, this.parent.subChartId, true);
/*     */     }
/*     */     else {
/* 148 */       this.indicatorsContainer.editIndicator(this.indicatorWrapper, this.parent.subChartId, false);
/* 149 */       SwingUtilities.invokeLater(new Runnable() {
/*     */         public void run() {
/* 151 */           ((IndicatorOutputTableCellEditor)IndicatorSetupPanel.access$200(IndicatorSetupPanel.this).table.getCellRenderer(0, 4)).build();
/* 152 */           ((IndicatorOutputTableCellEditor)IndicatorSetupPanel.access$200(IndicatorSetupPanel.this).table.getCellRenderer(0, 5)).build();
/* 153 */           IndicatorSetupPanel.this.outputsPanel.repaint();
/* 154 */           IndicatorSetupPanel.this.outputsPanel.revalidate();
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */ 
/*     */   public Dimension getPreferredSize() {
/* 162 */     return getDimension();
/*     */   }
/*     */ 
/*     */   public Dimension getMinimumSize()
/*     */   {
/* 167 */     return getPreferredSize();
/*     */   }
/*     */ 
/*     */   private Dimension getDimension() {
/* 171 */     int width = this.xAxis ? 860 : 490;
/* 172 */     int height = this.xAxis ? 200 : (this.parametersPanel.isVisible() ? this.parametersPanel.getPreferredSize().height : -30) + this.outputsPanel.getPreferredSize().height + 30;
/*     */ 
/* 175 */     return new Dimension(width, height);
/*     */   }
/*     */ 
/*     */   private Dimension getPanelDimension(TableModel tableModel, int defaultWidthXAxis, int defaultWidthYAxis, int defaultHeight, int extraRate)
/*     */   {
/* 180 */     if (this.xAxis) {
/* 181 */       return new Dimension(defaultWidthXAxis, defaultHeight);
/*     */     }
/* 183 */     int height = 0;
/* 184 */     height += tableModel.getRowCount() * 30 + 30 * extraRate;
/* 185 */     return new Dimension(defaultWidthYAxis, height);
/*     */   }
/*     */ 
/*     */   private class SelectedIndicatorsList extends JList
/*     */   {
/*     */     public SelectedIndicatorsList(ListModel listModel)
/*     */     {
/* 368 */       super();
/*     */     }
/*     */   }
/*     */ 
/*     */   private class SelectedIndicatorsPanel extends JPanel
/*     */   {
/* 331 */     private JLocalizableLabel selectedIndicators = new JLocalizableLabel("label.selected.indicators", 0.0F);
/* 332 */     private DefaultListModel listModel = new DefaultListModel();
/*     */ 
/*     */     public SelectedIndicatorsPanel() {
/* 335 */       setLayout(new BoxLayout(this, 1));
/* 336 */       setBorder(new JRoundedBorder(this, null, 10, 10, 10, 10));
/*     */ 
/* 338 */       setPreferredSize(getPreferredSize());
/*     */ 
/* 340 */       JScrollPane scrollPane = new JScrollPane(new IndicatorSetupPanel.SelectedIndicatorsList(IndicatorSetupPanel.this, this.listModel));
/* 341 */       scrollPane.setBorder(new JRoundedBorder(this, null, 5, 5, 5, 5));
/*     */ 
/* 343 */       this.selectedIndicators.setAlignmentX(0.0F);
/* 344 */       scrollPane.setAlignmentX(0.0F);
/*     */ 
/* 346 */       add(this.selectedIndicators);
/* 347 */       add(scrollPane);
/*     */     }
/*     */ 
/*     */     public void updateList(List<IndicatorWrapper> indicatorWrappers) {
/* 351 */       this.listModel.clear();
/* 352 */       if (indicatorWrappers != null)
/* 353 */         for (IndicatorWrapper iw : indicatorWrappers) {
/* 354 */           this.listModel.addElement(getItemTitle(iw.getName()));
/* 355 */           this.selectedIndicators.setTextParams(new Object[] { Integer.valueOf(this.listModel.getSize()) });
/* 356 */           this.selectedIndicators.setText("label.selected.indicators");
/*     */         }
/*     */     }
/*     */ 
/*     */     private String getItemTitle(String value)
/*     */     {
/* 362 */       return value.toUpperCase() + " - " + IndicatorsProvider.getInstance().getTitle(value);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class IndicatorsTableBorder extends AbstractBorder
/*     */     implements UIResource
/*     */   {
/*     */     private Color shadow;
/*     */     private Color highlight;
/* 286 */     private int columnIndex = 0;
/*     */ 
/*     */     public IndicatorsTableBorder(Color shadow, Color highlight) {
/* 289 */       this.shadow = shadow;
/* 290 */       this.highlight = highlight;
/*     */     }
/*     */ 
/*     */     public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
/* 294 */       Color oldColor = g.getColor();
/* 295 */       g.translate(x, y);
/*     */ 
/* 297 */       g.setColor(this.shadow);
/* 298 */       g.drawLine(0, height - 2, width, height - 2);
/*     */ 
/* 302 */       if (this.columnIndex > 0) {
/* 303 */         g.setColor(this.shadow);
/* 304 */         g.drawLine(0, 0, 0, height - 2);
/*     */       }
/*     */ 
/* 307 */       g.translate(-x, -y);
/* 308 */       g.setColor(oldColor);
/*     */     }
/*     */ 
/*     */     public Insets getBorderInsets(Component c) {
/* 312 */       return getBorderInsets(c, new Insets(0, 0, 0, 0));
/*     */     }
/*     */ 
/*     */     public Insets getBorderInsets(Component c, Insets insets) {
/* 316 */       insets.top = 3;
/* 317 */       insets.left = 5;
/* 318 */       insets.bottom = 5;
/* 319 */       insets.right = 0;
/*     */ 
/* 321 */       return insets;
/*     */     }
/*     */ 
/*     */     public void setColumnIndex(int columnIndex) {
/* 325 */       this.columnIndex = columnIndex;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class OutputsPanel extends JPanel
/*     */   {
/*     */     public final IndicatorOutputsTableModel tableModel;
/*     */     private final IndicatorOutputsTable table;
/* 229 */     private final int DEFAULT_HEIGHT = 200;
/* 230 */     private final int DEFAULT_WIDTH_X_AXIS = 532;
/* 231 */     private final int DEFAULT_WIDTH_Y_AXIS = 500;
/*     */ 
/*     */     public OutputsPanel()
/*     */     {
/* 235 */       super();
/* 236 */       setBorder(new JLocalizableRoundedBorder(this, "title.output.panel"));
/*     */ 
/* 238 */       this.tableModel = new IndicatorOutputsTableModel(IndicatorSetupPanel.this);
/* 239 */       this.table = new IndicatorOutputsTable(this.tableModel, IndicatorSetupPanel.this)
/*     */       {
/*     */       };
/* 243 */       JTableHeader jTableHeader = new JTableHeader(this.table.getColumnModel());
/* 244 */       jTableHeader.setReorderingAllowed(false);
/* 245 */       IndicatorSetupPanel.IndicatorsTableBorder border = new IndicatorSetupPanel.IndicatorsTableBorder(Color.LIGHT_GRAY, Color.LIGHT_GRAY);
/*     */ 
/* 247 */       jTableHeader.setDefaultRenderer(new DefaultTableCellRenderer(IndicatorSetupPanel.this, border)
/*     */       {
/*     */         public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
/* 250 */           setFont(table.getFont());
/* 251 */           setValue(value);
/*     */ 
/* 253 */           this.val$border.setColumnIndex(column);
/* 254 */           super.setBorder(this.val$border);
/*     */ 
/* 256 */           return this;
/*     */         }
/*     */       });
/* 260 */       this.table.setTableHeader(jTableHeader);
/*     */ 
/* 262 */       add(new JScrollPane(this.table, IndicatorSetupPanel.this)
/*     */       {
/*     */       }
/*     */       , "Center");
/*     */     }
/*     */ 
/*     */     public Dimension getPreferredSize()
/*     */     {
/* 270 */       Dimension preferred = IndicatorSetupPanel.this.getPanelDimension(this.tableModel, 532, 500, 200, 2);
/* 271 */       if (preferred.height > 200) {
/* 272 */         preferred.height = 200;
/*     */       }
/* 274 */       return preferred;
/*     */     }
/*     */ 
/*     */     public Dimension getMinimumSize()
/*     */     {
/* 279 */       return getPreferredSize();
/*     */     }
/*     */   }
/*     */ 
/*     */   private class ParametersPanel extends JPanel
/*     */   {
/*     */     public final IndicatorParametersTableModel tableModel;
/*     */     private final IndicatorParametersTable table;
/* 194 */     private final int DEFAULT_HEIGHT = 200;
/* 195 */     private final int DEFAULT_WIDTH_X_AXIS = 330;
/* 196 */     private final int DEFAULT_WIDTH_Y_AXIS = 500;
/*     */ 
/*     */     public ParametersPanel() {
/* 199 */       super();
/* 200 */       setBorder(new JLocalizableRoundedBorder(this, "title.input.panel"));
/*     */ 
/* 202 */       this.tableModel = new IndicatorParametersTableModel(IndicatorSetupPanel.this, IndicatorSetupPanel.this.isTicks);
/* 203 */       this.table = new IndicatorParametersTable(this.tableModel, IndicatorSetupPanel.this.isTicks, IndicatorSetupPanel.this)
/*     */       {
/*     */       };
/* 207 */       this.table.setTableHeader(null);
/* 208 */       add(new JScrollPane(this.table, IndicatorSetupPanel.this)
/*     */       {
/*     */       }
/*     */       , "Center");
/*     */     }
/*     */ 
/*     */     public Dimension getPreferredSize()
/*     */     {
/* 216 */       return IndicatorSetupPanel.this.getPanelDimension(this.tableModel, 330, 500, 200, 1);
/*     */     }
/*     */ 
/*     */     public Dimension getMinimumSize()
/*     */     {
/* 221 */       return getPreferredSize();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.dialogs.indicators.IndicatorSetupPanel
 * JD-Core Version:    0.6.0
 */