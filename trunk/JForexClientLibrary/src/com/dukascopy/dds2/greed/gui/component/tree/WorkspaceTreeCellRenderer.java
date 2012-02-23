/*     */ package com.dukascopy.dds2.greed.gui.component.tree;
/*     */ 
/*     */ import com.dukascopy.api.IChart.Type;
/*     */ import com.dukascopy.api.IChartObject;
/*     */ import com.dukascopy.api.impl.CustIndicatorWrapper;
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.ide.api.ServiceSourceLanguage;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.ChartTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.CurrenciesTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.CurrencyTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.CustIndTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.DrawingTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.IndicatorTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.ServicesTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.StrategyTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.Localizable;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.resizing.components.ResizableIcon;
/*     */ import com.dukascopy.dds2.greed.util.ColorUtils;
/*     */ import com.dukascopy.dds2.greed.util.GuiResourceLoader;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Font;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.swing.Icon;
/*     */ import javax.swing.JTree;
/*     */ import javax.swing.tree.DefaultTreeCellRenderer;
/*     */ 
/*     */ public class WorkspaceTreeCellRenderer extends DefaultTreeCellRenderer
/*     */   implements Localizable
/*     */ {
/*     */   private static final String JAVA_EXT = ".java";
/*  39 */   private Icon openFolderIcon = GuiResourceLoader.getInstance().loadImageIcon("rc/media/tree_charts_expand.png");
/*  40 */   private Icon closedFolderIcon = GuiResourceLoader.getInstance().loadImageIcon("rc/media/tree_charts_collapse.png");
/*     */ 
/*  42 */   private static Icon currencyOpenFolderIcon = GuiResourceLoader.getInstance().loadImageIcon("rc/media/tree_instruments_expand.png");
/*  43 */   private static Icon currencyClosedFolderIcon = GuiResourceLoader.getInstance().loadImageIcon("rc/media/tree_instruments_collapse.png");
/*     */ 
/*  45 */   private static Icon currencyIcon = GuiResourceLoader.getInstance().loadImageIcon("rc/media/tree_instrument_active.png");
/*  46 */   private static Icon currencOffIcon = GuiResourceLoader.getInstance().loadImageIcon("rc/media/tree_instrument_inactive.png");
/*     */ 
/*  48 */   private static Icon strategyOpenFolderIcon = GuiResourceLoader.getInstance().loadImageIcon("rc/media/tree_strategies_expand.png");
/*  49 */   private static Icon strategyClosedFolderIcon = GuiResourceLoader.getInstance().loadImageIcon("rc/media/tree_strategies_collapse.png");
/*     */ 
/*  51 */   private Icon instrumentIcon = GuiResourceLoader.getInstance().loadImageIcon("rc/media/tree_charts_branch.png");
/*     */ 
/*  53 */   private static final Map<IChart.Type, ResizableIcon> drawingIcons = new HashMap();
/*     */   private static Icon indicatorBinaryIcon;
/*     */   private static Icon indicatorSourceJavaIcon;
/*     */   private static Icon indicatorSourceOtherIcon;
/*     */   private static WorkspaceTreeIcon javaStrategyIcon;
/*     */   private static WorkspaceTreeIcon otherStrategyIcon;
/*     */   private static WorkspaceTreeIcon jfxStrategyIcon;
/*     */   private Font regularFont;
/*     */   private Font boldFont;
/*     */   private Font notTradableFont;
/*     */ 
/*     */   public WorkspaceTreeCellRenderer()
/*     */   {
/*  74 */     LocalizationManager.addLocalizable(this);
/*     */   }
/*     */ 
/*     */   public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
/*     */   {
/*  80 */     if ((this.regularFont == null) || (this.boldFont == null) || (this.notTradableFont == null)) {
/*  81 */       Font superFont = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus).getFont();
/*  82 */       setupFonts(superFont);
/*     */     }
/*     */ 
/*  85 */     setIcons(value, expanded);
/*  86 */     Component component = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
/*  87 */     component.setFont(getFontForComponent(value));
/*     */ 
/*  89 */     if (component.getBackground() != null) {
/*  90 */       component.setForeground(getForeground(component.getBackground(), component.getForeground(), value, selected));
/*     */     }
/*  92 */     else if (selected)
/*  93 */       component.setForeground(getForeground(Color.WHITE, component.getForeground(), value, selected));
/*     */     else {
/*  95 */       component.setForeground(getForeground(tree.getBackground(), tree.getForeground(), value, selected));
/*     */     }
/*     */ 
/*  98 */     return component;
/*     */   }
/*     */ 
/*     */   Font getFontForComponent(Object value) {
/* 102 */     if ((value != null) && ((value instanceof CurrencyTreeNode)) && (!((CurrencyTreeNode)value).isTradable())) {
/* 103 */       return this.notTradableFont;
/*     */     }
/* 105 */     return this.regularFont;
/*     */   }
/*     */ 
/*     */   Color getForeground(Color background, Color foreground, Object value, boolean selected)
/*     */   {
/* 110 */     if (value != null) {
/* 111 */       if (((value instanceof CurrencyTreeNode)) && (!((CurrencyTreeNode)value).isTradable())) {
/* 112 */         return Color.GRAY;
/*     */       }
/*     */ 
/* 115 */       if (((value instanceof IndicatorTreeNode)) && (!selected)) {
/* 116 */         IndicatorWrapper indicator = ((IndicatorTreeNode)value).getIndicator();
/* 117 */         Color[] colors = indicator.getOutputColors();
/* 118 */         if ((colors != null) && (colors.length == 1)) {
/* 119 */           return colors[0];
/*     */         }
/*     */       }
/*     */ 
/* 123 */       if (((value instanceof DrawingTreeNode)) && (!selected)) {
/* 124 */         Color drawingColor = ((DrawingTreeNode)value).getDrawing().getColor();
/*     */ 
/* 126 */         if (ColorUtils.isVisible(drawingColor, background)) {
/* 127 */           return drawingColor;
/*     */         }
/*     */       }
/*     */     }
/* 131 */     return foreground;
/*     */   }
/*     */ 
/*     */   void setIcons(Object value, boolean expanded) {
/* 135 */     WorkspaceTreeNode node = (WorkspaceTreeNode)value;
/* 136 */     if ((node instanceof ChartTreeNode)) {
/* 137 */       setTreeCellIcon(this.instrumentIcon);
/* 138 */     } else if ((node instanceof CurrenciesTreeNode)) {
/* 139 */       setCurrenciesTreeNodeIcons(expanded);
/* 140 */     } else if ((node instanceof CurrencyTreeNode)) {
/* 141 */       setCurrencyTreeNodeIcons((CurrencyTreeNode)node);
/* 142 */     } else if ((node instanceof ServicesTreeNode)) {
/* 143 */       setServicesTreeNodeIcons(expanded);
/* 144 */     } else if ((node instanceof StrategyTreeNode)) {
/* 145 */       setStrategyTreeNodeIcons((StrategyTreeNode)node);
/* 146 */     } else if ((node instanceof IndicatorTreeNode)) {
/* 147 */       setIndicatorTreeNodeIcons((IndicatorTreeNode)node);
/* 148 */     } else if ((node instanceof CustIndTreeNode)) {
/* 149 */       setCustomIndicatorTreeNodeIcons((CustIndTreeNode)node);
/* 150 */     } else if ((node instanceof DrawingTreeNode)) {
/* 151 */       setTreeCellIcon((Icon)drawingIcons.get(((DrawingTreeNode)node).getDrawing().getType()));
/*     */     } else {
/* 153 */       setOpenIcon(this.openFolderIcon);
/* 154 */       setClosedIcon(this.closedFolderIcon);
/* 155 */       setLeafIcon(this.closedFolderIcon);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void initDrawingIcons() {
/* 160 */     drawingIcons.put(IChart.Type.OHLC_INFORMER, new ResizableIcon("drawing_ohlc_active.png"));
/* 161 */     drawingIcons.put(IChart.Type.PERCENT, new ResizableIcon("drawing_line_percent_active.png"));
/* 162 */     drawingIcons.put(IChart.Type.CHANNEL, new ResizableIcon("drawing_line_parallel_active.png"));
/* 163 */     drawingIcons.put(IChart.Type.POLY_LINE, new ResizableIcon("drawing_line_poly_active.png"));
/* 164 */     drawingIcons.put(IChart.Type.SHORT_LINE, new ResizableIcon("drawing_line_short_active.png"));
/* 165 */     drawingIcons.put(IChart.Type.LONG_LINE, new ResizableIcon("drawing_line_long_active.png"));
/* 166 */     drawingIcons.put(IChart.Type.RAY_LINE, new ResizableIcon("drawing_line_ray_active.png"));
/*     */ 
/* 168 */     drawingIcons.put(IChart.Type.RECTANGLE, new ResizableIcon("drawing_rectangle_active.png"));
/* 169 */     drawingIcons.put(IChart.Type.TRIANGLE, new ResizableIcon("drawing_triangle_active.png"));
/* 170 */     drawingIcons.put(IChart.Type.ELLIPSE, new ResizableIcon("drawing_ellipse_active.png"));
/*     */ 
/* 172 */     drawingIcons.put(IChart.Type.SIGNAL_UP, new ResizableIcon("drawing_arrow_up_green_active.png"));
/* 173 */     drawingIcons.put(IChart.Type.SIGNAL_DOWN, new ResizableIcon("drawing_arrow_down_red_active.png"));
/*     */ 
/* 175 */     drawingIcons.put(IChart.Type.HLINE, new ResizableIcon("drawing_line_horizontal_active.png"));
/* 176 */     drawingIcons.put(IChart.Type.VLINE, new ResizableIcon("drawing_line_vertical_active.png"));
/*     */ 
/* 178 */     drawingIcons.put(IChart.Type.TEXT, new ResizableIcon("drawing_text_active.png"));
/* 179 */     drawingIcons.put(IChart.Type.CYCLES, new ResizableIcon("drawing_periods_active.png"));
/*     */ 
/* 181 */     drawingIcons.put(IChart.Type.TIMEMARKER, new ResizableIcon("drawing_marker_time_active.png"));
/* 182 */     drawingIcons.put(IChart.Type.PRICEMARKER, new ResizableIcon("drawing_marker_price_active.png"));
/*     */   }
/*     */ 
/*     */   void setIndicatorTreeNodeIcons(IndicatorTreeNode indicatorTreeNode) {
/* 186 */     setTreeCellIcon(indicatorBinaryIcon);
/*     */   }
/*     */ 
/*     */   void setCustomIndicatorTreeNodeIcons(CustIndTreeNode indicatorTreeNode)
/*     */   {
/* 192 */     CustIndicatorWrapper wrapper = indicatorTreeNode.getServiceWrapper();
/* 193 */     String name = wrapper.getName();
/*     */     Icon icon;
/*     */     Icon icon;
/* 194 */     if ((name != null) && (name.length() > ".java".length())) {
/* 195 */       String ext = name.substring(name.length() - ".java".length());
/*     */       Icon icon;
/* 196 */       if (ext.equalsIgnoreCase(".java"))
/* 197 */         icon = indicatorSourceJavaIcon;
/*     */       else
/* 199 */         icon = indicatorSourceOtherIcon;
/*     */     }
/*     */     else {
/* 202 */       icon = indicatorSourceOtherIcon;
/*     */     }
/*     */ 
/* 205 */     setTreeCellIcon(icon);
/*     */   }
/*     */ 
/*     */   void setStrategyTreeNodeIcons(StrategyTreeNode strategyTreeNode)
/*     */   {
/*     */     WorkspaceTreeIcon icon;
/*     */     WorkspaceTreeIcon icon;
/* 211 */     if (!strategyTreeNode.isEditable()) {
/* 212 */       icon = jfxStrategyIcon;
/*     */     }
/*     */     else
/*     */     {
/*     */       WorkspaceTreeIcon icon;
/* 213 */       if (strategyTreeNode.getServiceSourceLanguage() == ServiceSourceLanguage.JAVA)
/* 214 */         icon = javaStrategyIcon;
/*     */       else {
/* 216 */         icon = otherStrategyIcon;
/*     */       }
/*     */     }
/* 219 */     if (strategyTreeNode.isInitializing())
/* 220 */       icon.setMode(WorkspaceTreeIcon.TreeIconMode.INITIALIZING);
/* 221 */     else if (strategyTreeNode.isRunningRemotely())
/* 222 */       icon.setMode(WorkspaceTreeIcon.TreeIconMode.RUNNING_REMOTELY);
/* 223 */     else if (strategyTreeNode.isRunning())
/*     */     {
/* 228 */       icon.setMode(WorkspaceTreeIcon.TreeIconMode.RUNNING_LOCALLY);
/*     */     }
/*     */     else {
/* 231 */       icon.setMode(null);
/*     */     }
/*     */ 
/* 234 */     setTreeCellIcon(icon);
/*     */   }
/*     */ 
/*     */   void setServicesTreeNodeIcons(boolean expanded) {
/* 238 */     if (expanded) {
/* 239 */       setOpenIcon(strategyOpenFolderIcon);
/*     */     } else {
/* 241 */       setClosedIcon(strategyClosedFolderIcon);
/* 242 */       setLeafIcon(strategyClosedFolderIcon);
/*     */     }
/*     */   }
/*     */ 
/*     */   void setCurrencyTreeNodeIcons(CurrencyTreeNode currencyTreeNode) {
/* 247 */     if (currencyTreeNode.isTradable())
/* 248 */       setTreeCellIcon(currencyIcon);
/*     */     else
/* 250 */       setTreeCellIcon(currencOffIcon);
/*     */   }
/*     */ 
/*     */   void setCurrenciesTreeNodeIcons(boolean expanded)
/*     */   {
/* 255 */     if (expanded) {
/* 256 */       setOpenIcon(currencyOpenFolderIcon);
/*     */     } else {
/* 258 */       setClosedIcon(currencyClosedFolderIcon);
/* 259 */       setLeafIcon(currencyClosedFolderIcon);
/*     */     }
/*     */   }
/*     */ 
/*     */   void setTreeCellIcon(Icon icon) {
/* 264 */     setOpenIcon(icon);
/* 265 */     setClosedIcon(icon);
/* 266 */     setLeafIcon(icon);
/*     */   }
/*     */ 
/*     */   void setupFonts(Font superFont) {
/* 270 */     this.regularFont = superFont;
/* 271 */     this.boldFont = new Font(this.regularFont.getName(), 1, this.regularFont.getSize());
/* 272 */     this.notTradableFont = new Font(this.regularFont.getName(), 2, this.regularFont.getSize());
/*     */   }
/*     */ 
/*     */   public void localize()
/*     */   {
/* 277 */     if (getFont() != null) {
/* 278 */       setFont(LocalizationManager.getDefaultFont(getFont().getSize()));
/* 279 */       setupFonts(getFont());
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  55 */     initDrawingIcons();
/*     */ 
/*  58 */     indicatorBinaryIcon = GuiResourceLoader.getInstance().loadImageIcon("rc/media/tree_indicator_binary.png");
/*     */ 
/*  61 */     indicatorSourceJavaIcon = GuiResourceLoader.getInstance().loadImageIcon("rc/media/tree_indicator_source_java.png");
/*  62 */     indicatorSourceOtherIcon = GuiResourceLoader.getInstance().loadImageIcon("rc/media/tree_indicator_source_other.png");
/*     */ 
/*  64 */     javaStrategyIcon = new WorkspaceTreeIcon("rc/media/tree_strategy_source_java.png");
/*  65 */     otherStrategyIcon = new WorkspaceTreeIcon("rc/media/tree_strategy_source_other.png");
/*  66 */     jfxStrategyIcon = new WorkspaceTreeIcon("rc/media/tree_strategy_binary.png");
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.WorkspaceTreeCellRenderer
 * JD-Core Version:    0.6.0
 */