/*     */ package com.dukascopy.charts.view.swing;
/*     */ 
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.chartbuilder.MouseAndKeyAdapterBuilder;
/*     */ import com.dukascopy.charts.chartbuilder.SubIndicatorGroup;
/*     */ import com.dukascopy.charts.mouseandkeyadaptors.ChartsMouseAndKeyAdapter;
/*     */ import com.dukascopy.charts.persistence.ITheme;
/*     */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*     */ import com.dukascopy.charts.view.paintingtechnic.InvalidationContent;
/*     */ import com.dukascopy.charts.view.paintingtechnic.PaintingTechnicBuilder;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Color;
/*     */ import java.awt.Cursor;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import javax.swing.JComponent;
/*     */ 
/*     */ public class ChartSwingViewBuilder
/*     */ {
/*     */   final MouseAndKeyAdapterBuilder mouseAndKeyAdapterBuilder;
/*     */   public static final int SUB_CHART_VIEW_DEF_HEIGHT = 100;
/*     */   public static final int COMMON_AXIS_X_PANEL_HEIGHT = 12;
/*     */   public static final int DIV_PANEL_DEF_HEIGHT = 3;
/*     */   private final ChartState chartState;
/*     */ 
/*     */   public ChartSwingViewBuilder(MouseAndKeyAdapterBuilder mouseAndKeyAdapterBuilder, ChartState chartState)
/*     */   {
/*  30 */     this.mouseAndKeyAdapterBuilder = mouseAndKeyAdapterBuilder;
/*  31 */     this.chartState = chartState;
/*     */   }
/*     */ 
/*     */   public JComponent createMainChartView(PaintingTechnicBuilder paintingTechnicBuilder) {
/*  35 */     JComponent mainChartView = new JComponent()
/*     */     {
/*     */     };
/*  36 */     mainChartView.setName("MainChartView");
/*     */ 
/*  38 */     AbstractPanel mainAxisYPanel = createPanel(paintingTechnicBuilder, InvalidationContent.MAINAXISYPANEL, "MainAxisYPanel");
/*  39 */     ChartsMouseAndKeyAdapter chartsMouseAndKeyAdapter = this.mouseAndKeyAdapterBuilder.createMouseAndKeyAdapterForMain(InvalidationContent.MAINAXISYPANEL);
/*  40 */     mainAxisYPanel.addMouseListener(chartsMouseAndKeyAdapter);
/*  41 */     mainAxisYPanel.addMouseMotionListener(chartsMouseAndKeyAdapter);
/*     */ 
/*  43 */     AbstractPanel mainChartPanel = createPanel(paintingTechnicBuilder, InvalidationContent.MAINCHARTPANEL, "MainChartPanel");
/*  44 */     chartsMouseAndKeyAdapter = this.mouseAndKeyAdapterBuilder.createMouseAndKeyAdapterForMain(InvalidationContent.MAINCHARTPANEL);
/*  45 */     mainChartPanel.addMouseListener(chartsMouseAndKeyAdapter);
/*  46 */     mainChartPanel.addMouseMotionListener(chartsMouseAndKeyAdapter);
/*  47 */     mainChartPanel.addMouseWheelListener(chartsMouseAndKeyAdapter);
/*  48 */     mainChartPanel.addKeyListener(chartsMouseAndKeyAdapter);
/*  49 */     mainChartPanel.addFocusListener(chartsMouseAndKeyAdapter);
/*     */ 
/*  51 */     mainChartView.setOpaque(true);
/*     */ 
/*  53 */     layoutComponents(mainChartView, mainChartPanel, mainAxisYPanel);
/*     */ 
/*  55 */     return mainChartView;
/*     */   }
/*     */ 
/*     */   public JComponent createSubChartView(PaintingTechnicBuilder paintingTechnicBuilder, SubIndicatorGroup subIndicatorGroup) {
/*  59 */     JComponent subChartView = new JComponent()
/*     */     {
/*     */     };
/*  60 */     subChartView.setName("SubChartView");
/*     */ 
/*  62 */     AbstractPanel subChartPanel = createSubPanel(paintingTechnicBuilder, InvalidationContent.SUBCHARTPANEL, subIndicatorGroup);
/*  63 */     AbstractPanel subAxisYPanel = createSubPanel(paintingTechnicBuilder, InvalidationContent.SUBAXISYPANEL, subIndicatorGroup);
/*     */ 
/*  65 */     ChartsMouseAndKeyAdapter chartsMouseAndKeyAdapter = this.mouseAndKeyAdapterBuilder.createMouseAndKeyAdapterForSub(InvalidationContent.SUBCHARTPANEL, subIndicatorGroup);
/*  66 */     subChartPanel.addMouseListener(chartsMouseAndKeyAdapter);
/*  67 */     subChartPanel.addMouseMotionListener(chartsMouseAndKeyAdapter);
/*  68 */     subChartPanel.addMouseWheelListener(chartsMouseAndKeyAdapter);
/*  69 */     subChartPanel.addKeyListener(chartsMouseAndKeyAdapter);
/*     */ 
/*  71 */     chartsMouseAndKeyAdapter = this.mouseAndKeyAdapterBuilder.createMouseAndKeyAdapterForSub(InvalidationContent.SUBAXISYPANEL, subIndicatorGroup);
/*  72 */     subAxisYPanel.addMouseListener(chartsMouseAndKeyAdapter);
/*  73 */     subAxisYPanel.addMouseMotionListener(chartsMouseAndKeyAdapter);
/*     */ 
/*  76 */     subChartView.setOpaque(true);
/*  77 */     subChartView.setSize(new Dimension(0, 100));
/*     */ 
/*  79 */     layoutComponents(subChartView, subChartPanel, subAxisYPanel);
/*     */ 
/*  81 */     return subChartView;
/*     */   }
/*     */ 
/*     */   public JComponent createCommonAxisXPane(PaintingTechnicBuilder paintingTechnicBuilder) {
/*  85 */     AbstractPanel commonAxisXPanel = createPanel(paintingTechnicBuilder, InvalidationContent.COMMONAXISXPANEL, "CommonAxisXPanel");
/*     */ 
/*  87 */     ChartsMouseAndKeyAdapter chartsMouseAndKeyAdapter = this.mouseAndKeyAdapterBuilder.createMouseAndKeyAdapterForMain(InvalidationContent.COMMONAXISXPANEL);
/*  88 */     commonAxisXPanel.addMouseListener(chartsMouseAndKeyAdapter);
/*  89 */     commonAxisXPanel.addMouseMotionListener(chartsMouseAndKeyAdapter);
/*     */ 
/*  91 */     commonAxisXPanel.setSize(new Dimension(0, 12));
/*     */ 
/*  93 */     return commonAxisXPanel;
/*     */   }
/*     */ 
/*     */   public JComponent createDivisionPanel(JComponent chartViewContainer, JComponent mainChartView) {
/*  97 */     JComponent divisionPanel = new JComponent() {
/*     */       protected void paintComponent(Graphics g) {
/*  99 */         Color prevColor = g.getColor();
/* 100 */         g.setColor(ChartSwingViewBuilder.this.chartState.getTheme().getColor(ITheme.ChartElement.BACKGROUND));
/* 101 */         Dimension dimension = getSize();
/* 102 */         g.fillRect(0, 0, (int)dimension.getWidth(), (int)dimension.getHeight());
/* 103 */         g.setColor(prevColor);
/* 104 */         g.drawLine(0, 0, 0, (int)dimension.getHeight());
/* 105 */         g.setColor(prevColor);
/*     */       }
/*     */     };
/* 108 */     divisionPanel.setName("DivisionPanel");
/*     */ 
/* 110 */     MouseAdapter mouseListener = this.mouseAndKeyAdapterBuilder.getMouseAndKeyAdapterForDivisionPanel(chartViewContainer, mainChartView);
/* 111 */     divisionPanel.addMouseMotionListener(mouseListener);
/* 112 */     divisionPanel.addMouseListener(mouseListener);
/*     */ 
/* 114 */     divisionPanel.setSize(new Dimension(0, 3));
/* 115 */     divisionPanel.setCursor(Cursor.getPredefinedCursor(8));
/* 116 */     return divisionPanel;
/*     */   }
/*     */ 
/*     */   void layoutComponents(JComponent mainChartView, JComponent mainChartsPanel, JComponent mainAxisYPanel)
/*     */   {
/* 122 */     mainChartView.setLayout(new BorderLayout());
/* 123 */     mainChartView.add(mainChartsPanel, "Center");
/* 124 */     mainChartView.add(mainAxisYPanel, "East");
/*     */   }
/*     */ 
/*     */   AbstractPanel createPanel(PaintingTechnicBuilder paintingTechnicBuilder, InvalidationContent invalidationContent, String swingName) {
/* 128 */     AbstractPanel abstractPanel = new AbstractPanel(paintingTechnicBuilder.createPaintingTechnic(invalidationContent));
/* 129 */     abstractPanel.setName(swingName);
/* 130 */     return abstractPanel;
/*     */   }
/*     */ 
/*     */   AbstractPanel createSubPanel(PaintingTechnicBuilder paintingTechnicBuilder, InvalidationContent invalidationContent, SubIndicatorGroup subIndicatorGroup) {
/* 134 */     return new AbstractPanel(paintingTechnicBuilder.createPaintingTechnicForSubPanel(invalidationContent, subIndicatorGroup));
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.swing.ChartSwingViewBuilder
 * JD-Core Version:    0.6.0
 */