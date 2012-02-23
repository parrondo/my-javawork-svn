/*      */ package com.dukascopy.api.impl;
/*      */ 
/*      */ import com.dukascopy.api.ConnectorIndicator;
/*      */ import com.dukascopy.api.ConnectorIndicator.ConnectorIndicatorLevel;
/*      */ import com.dukascopy.api.IChartObject;
/*      */ import com.dukascopy.api.IIndicators.AppliedPrice;
/*      */ import com.dukascopy.api.OfferSide;
/*      */ import com.dukascopy.api.connector.IConnector;
/*      */ import com.dukascopy.api.indicators.BooleanOptInputDescription;
/*      */ import com.dukascopy.api.indicators.DoubleListDescription;
/*      */ import com.dukascopy.api.indicators.DoubleRangeDescription;
/*      */ import com.dukascopy.api.indicators.IDrawingIndicator;
/*      */ import com.dukascopy.api.indicators.IIndicator;
/*      */ import com.dukascopy.api.indicators.IIndicatorDrawingSupport;
/*      */ import com.dukascopy.api.indicators.IndicatorInfo;
/*      */ import com.dukascopy.api.indicators.InputParameterInfo;
/*      */ import com.dukascopy.api.indicators.IntegerListDescription;
/*      */ import com.dukascopy.api.indicators.IntegerRangeDescription;
/*      */ import com.dukascopy.api.indicators.OptInputParameterInfo;
/*      */ import com.dukascopy.api.indicators.OutputParameterInfo;
/*      */ import com.dukascopy.api.indicators.OutputParameterInfo.DrawingStyle;
/*      */ import com.dukascopy.api.indicators.OutputParameterInfo.Type;
/*      */ import com.dukascopy.charts.math.indicators.IndicatorsProvider;
/*      */ import com.dukascopy.charts.persistence.IdManager;
/*      */ import com.dukascopy.dds2.greed.gui.component.JColorComboBox;
/*      */ import java.awt.Color;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.Stroke;
/*      */ import java.lang.reflect.Method;
/*      */ import java.security.InvalidParameterException;
/*      */ import java.text.DecimalFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import org.slf4j.Logger;
/*      */ import org.slf4j.LoggerFactory;
/*      */ 
/*      */ public class IndicatorWrapper
/*      */   implements Cloneable
/*      */ {
/*   42 */   private static final Logger LOGGER = LoggerFactory.getLogger(IndicatorWrapper.class);
/*      */   public static final float DEFAULT_OUTPUT_OPACITY_ALPHA = 1.0F;
/*   45 */   public static final Color DEFAULT_OUTPUT_COLOR = Color.BLACK;
/*      */   public static final int DEFAULT_OUTPUT_WIDTH = 1;
/*      */   protected IndicatorHolder indicatorHolder;
/*      */   private OfferSide[] sidesForTicks;
/*      */   private IIndicators.AppliedPrice[] appliedPricesForCandles;
/*      */   private Object[] optParams;
/*      */   private Color[] outputColors;
/*      */   private Color[] outputColors2;
/*      */   private OutputParameterInfo.DrawingStyle[] drawingStyles;
/*      */   private int[] lineWidths;
/*      */   private int[] outputShifts;
/*      */   private boolean[] showValuesOnChart;
/*      */   private boolean[] showOutputs;
/*      */   private float[] opacityAlphas;
/*      */   private int id;
/*      */   private String name;
/*      */   private Method selfdrawingMethod;
/*      */   private Method minMaxMethod;
/*      */   private Integer subPanelId;
/*      */   private Integer chartPanelId;
/*      */   private List<IChartObject> chartObjects;
/*   77 */   private boolean changeTreeSelection = true;
/*      */   private List<LevelInfo> levelInfoList;
/*   82 */   private boolean recalculateOnNewCandleOnly = false;
/*      */ 
/*      */   public IndicatorWrapper()
/*      */   {
/*      */   }
/*      */ 
/*      */   public IndicatorWrapper(String name) {
/*   89 */     this(IndicatorsProvider.getInstance().getIndicatorHolder(name), -1);
/*      */   }
/*      */ 
/*      */   public IndicatorWrapper(String name, int id) {
/*   93 */     this(IndicatorsProvider.getInstance().getIndicatorHolder(name), id);
/*      */   }
/*      */ 
/*      */   public IndicatorWrapper(IndicatorHolder indicatorHolder) {
/*   97 */     this(indicatorHolder, -1);
/*      */   }
/*      */ 
/*      */   public IndicatorWrapper(IndicatorHolder indicatorHolder, Object[] optParams) {
/*  101 */     this(indicatorHolder, -1, optParams, null, null, null, null);
/*      */   }
/*      */ 
/*      */   public IndicatorWrapper(IndicatorHolder indicatorHolder, Object[] optParams, Color[] outputColors, OutputParameterInfo.DrawingStyle[] outputDrawingStyles, int[] outputWidths, int[] outputShifts)
/*      */   {
/*  120 */     this(indicatorHolder, -1, optParams, outputColors, outputDrawingStyles, outputWidths, outputShifts);
/*      */   }
/*      */ 
/*      */   protected IndicatorWrapper(IndicatorHolder indicatorHolder, int id, Object[] optParams, Color[] outputColors, OutputParameterInfo.DrawingStyle[] outputDrawingStyles, int[] outputWidths, int[] outputShifts)
/*      */   {
/*  140 */     if (LOGGER.isDebugEnabled()) {
/*  141 */       LOGGER.debug(new StringBuilder().append("Creating indicator wrapper for : ").append(indicatorHolder.getIndicator().getIndicatorInfo().getName()).toString());
/*      */     }
/*  143 */     init(indicatorHolder, id);
/*  144 */     this.sidesForTicks = extractSidesForTicks(indicatorHolder.getIndicator());
/*  145 */     this.appliedPricesForCandles = extractAppliedPrices(indicatorHolder.getIndicator());
/*      */ 
/*  147 */     int numberOfOutputs = indicatorHolder.getIndicator().getIndicatorInfo().getNumberOfOutputs();
/*      */ 
/*  150 */     this.optParams = (optParams == null ? extractOptParams(indicatorHolder.getIndicator()) : optParams);
/*      */ 
/*  152 */     if (outputColors == null) {
/*  153 */       this.outputColors = new Color[numberOfOutputs];
/*  154 */       this.outputColors2 = new Color[numberOfOutputs];
/*  155 */       initColors();
/*      */     }
/*  158 */     else if (outputColors.length < numberOfOutputs) {
/*  159 */       Color[] colors = new Color[numberOfOutputs];
/*  160 */       Arrays.fill(colors, DEFAULT_OUTPUT_COLOR);
/*  161 */       System.arraycopy(outputColors, 0, colors, 0, outputColors.length);
/*  162 */       this.outputColors = colors;
/*      */     }
/*      */     else {
/*  165 */       this.outputColors = outputColors;
/*      */     }
/*      */ 
/*  168 */     if (outputDrawingStyles == null) {
/*  169 */       this.drawingStyles = new OutputParameterInfo.DrawingStyle[numberOfOutputs];
/*  170 */       initDrawingStyles();
/*      */     }
/*  173 */     else if (outputDrawingStyles.length < numberOfOutputs) {
/*  174 */       OutputParameterInfo.DrawingStyle[] drawingStyles = new OutputParameterInfo.DrawingStyle[numberOfOutputs];
/*  175 */       for (int i = 0; i < numberOfOutputs; i++) {
/*  176 */         drawingStyles[i] = indicatorHolder.getIndicator().getOutputParameterInfo(i).getDrawingStyle();
/*      */       }
/*  178 */       System.arraycopy(outputDrawingStyles, 0, drawingStyles, 0, outputDrawingStyles.length);
/*  179 */       this.drawingStyles = drawingStyles;
/*      */     }
/*      */     else {
/*  182 */       this.drawingStyles = outputDrawingStyles;
/*      */     }
/*      */ 
/*  185 */     if (outputWidths == null) {
/*  186 */       this.lineWidths = new int[numberOfOutputs];
/*  187 */       initLineWidths();
/*      */     }
/*  190 */     else if (outputWidths.length < numberOfOutputs) {
/*  191 */       int[] widths = new int[numberOfOutputs];
/*  192 */       Arrays.fill(widths, 1);
/*  193 */       System.arraycopy(outputWidths, 0, widths, 0, outputWidths.length);
/*  194 */       this.lineWidths = widths;
/*      */     }
/*      */     else {
/*  197 */       this.lineWidths = outputWidths;
/*      */     }
/*      */ 
/*  200 */     if (outputShifts == null) {
/*  201 */       this.outputShifts = new int[numberOfOutputs];
/*  202 */       initOutputShifts();
/*      */     }
/*      */     else {
/*  205 */       this.outputShifts = outputShifts;
/*      */     }
/*      */ 
/*  208 */     this.opacityAlphas = extractOpacityAlphas(indicatorHolder.getIndicator());
/*      */ 
/*  210 */     extractMethods();
/*      */   }
/*      */ 
/*      */   public IndicatorWrapper(String name, int id, OfferSide[] sidesForTicks, IIndicators.AppliedPrice[] appliedPricesForCandles, Object[] optParams, Color[] outputColors, OutputParameterInfo.DrawingStyle[] drawingStyles, int[] lineWidths, int[] outputShifts, Integer subPanelId, List<IChartObject> chartObjects)
/*      */     throws InvalidParameterException
/*      */   {
/*  227 */     this(name, id, sidesForTicks, appliedPricesForCandles, optParams, outputColors, new boolean[outputColors.length], new boolean[outputColors.length], new float[outputColors.length], drawingStyles, lineWidths, outputShifts, subPanelId, chartObjects);
/*      */   }
/*      */ 
/*      */   public IndicatorWrapper(String name, int id, OfferSide[] sidesForTicks, IIndicators.AppliedPrice[] appliedPricesForCandles, Object[] optParams, Color[] outputColors, boolean[] valuesOnChart, boolean[] showOutputs, float[] opacityAlphas, OutputParameterInfo.DrawingStyle[] drawingStyles, int[] lineWidths, int[] outputShifts, Integer subPanelId, List<IChartObject> chartObjects)
/*      */     throws InvalidParameterException
/*      */   {
/*  247 */     this(name, id, sidesForTicks, appliedPricesForCandles, optParams, outputColors, null, drawingStyles, lineWidths, outputShifts, subPanelId, chartObjects);
/*      */   }
/*      */ 
/*      */   public IndicatorWrapper(String name, OfferSide[] sidesForTicks, IIndicators.AppliedPrice[] appliedPricesForCandles, Object[] optParams, Color[] outputColors, OutputParameterInfo.DrawingStyle[] drawingStyles, int[] lineWidths, int[] outputShifts)
/*      */     throws InvalidParameterException
/*      */   {
/*  273 */     this(name, sidesForTicks, appliedPricesForCandles, optParams, outputColors, new boolean[outputColors.length], new boolean[outputColors.length], new float[outputColors.length], drawingStyles, lineWidths, outputShifts);
/*      */   }
/*      */ 
/*      */   public IndicatorWrapper(String name, OfferSide[] sidesForTicks, IIndicators.AppliedPrice[] appliedPricesForCandles, Object[] optParams, Color[] outputColors, boolean[] valuesOnChart, boolean[] showOutputs, float[] opacityAlphas, OutputParameterInfo.DrawingStyle[] drawingStyles, int[] lineWidths, int[] outputShifts)
/*      */     throws InvalidParameterException
/*      */   {
/*  300 */     this(name, -1, sidesForTicks, appliedPricesForCandles, optParams, outputColors, valuesOnChart, showOutputs, opacityAlphas, drawingStyles, lineWidths, outputShifts, null, null);
/*      */   }
/*      */ 
/*      */   protected IndicatorWrapper(IndicatorHolder indicatorHolder, int id)
/*      */   {
/*  318 */     this(indicatorHolder, id, null, null, null, null, null);
/*      */   }
/*      */ 
/*      */   public IndicatorWrapper(String name, int id, OfferSide[] sidesForTicks, IIndicators.AppliedPrice[] appliedPricesForCandles, Object[] optParams, Color[] outputColors, Color[] outputColors2, OutputParameterInfo.DrawingStyle[] drawingStyles, int[] lineWidths, int[] outputShifts, Integer subPanelId, List<IChartObject> chartObjects)
/*      */     throws InvalidParameterException
/*      */   {
/*  335 */     this(name, id, sidesForTicks, appliedPricesForCandles, optParams, outputColors, outputColors2, new boolean[outputColors.length], new boolean[outputColors.length], new float[outputColors.length], drawingStyles, lineWidths, outputShifts, subPanelId, chartObjects, null);
/*      */   }
/*      */ 
/*      */   public IndicatorWrapper(String name, int id, OfferSide[] sidesForTicks, IIndicators.AppliedPrice[] appliedPricesForCandles, Object[] optParams, Color[] outputColors, Color[] outputColors2, OutputParameterInfo.DrawingStyle[] drawingStyles, int[] lineWidths, int[] outputShifts, Integer subPanelId, List<IChartObject> chartObjects, List<LevelInfo> levelInfoList)
/*      */     throws InvalidParameterException
/*      */   {
/*  369 */     this(name, id, sidesForTicks, appliedPricesForCandles, optParams, outputColors, outputColors2, new boolean[outputColors.length], new boolean[outputColors.length], new float[outputColors.length], drawingStyles, lineWidths, outputShifts, subPanelId, chartObjects, levelInfoList);
/*      */   }
/*      */ 
/*      */   public IndicatorWrapper(String name, int id, OfferSide[] sidesForTicks, IIndicators.AppliedPrice[] appliedPricesForCandles, Object[] optParams, Color[] outputColors, Color[] outputColors2, boolean[] valuesOnChart, boolean[] showOutputs, float[] opacityAlphas, OutputParameterInfo.DrawingStyle[] drawingStyles, int[] lineWidths, int[] outputShifts, Integer subPanelId, List<IChartObject> chartObjects, List<LevelInfo> levelInfoList)
/*      */     throws InvalidParameterException
/*      */   {
/*  392 */     init(name, id);
/*  393 */     validate(sidesForTicks, appliedPricesForCandles, optParams, outputColors);
/*  394 */     this.sidesForTicks = sidesForTicks;
/*  395 */     this.appliedPricesForCandles = appliedPricesForCandles;
/*  396 */     this.optParams = optParams;
/*  397 */     this.outputColors = outputColors;
/*  398 */     this.outputColors2 = (outputColors2 == null ? this.outputColors : outputColors2);
/*  399 */     this.showValuesOnChart = valuesOnChart;
/*  400 */     this.showOutputs = showOutputs;
/*  401 */     this.subPanelId = subPanelId;
/*  402 */     this.chartObjects = chartObjects;
/*  403 */     this.levelInfoList = levelInfoList;
/*      */ 
/*  405 */     if (drawingStyles == null) {
/*  406 */       this.drawingStyles = new OutputParameterInfo.DrawingStyle[outputColors.length];
/*  407 */       initDrawingStyles();
/*      */     } else {
/*  409 */       this.drawingStyles = drawingStyles;
/*      */     }
/*  411 */     if (lineWidths == null) {
/*  412 */       this.lineWidths = new int[outputColors.length];
/*  413 */       Arrays.fill(this.lineWidths, 1);
/*      */     } else {
/*  415 */       this.lineWidths = lineWidths;
/*      */     }
/*      */ 
/*  418 */     if (outputShifts == null) {
/*  419 */       this.outputShifts = new int[outputColors.length];
/*  420 */       Arrays.fill(this.outputShifts, 0);
/*      */     } else {
/*  422 */       this.outputShifts = outputShifts;
/*      */     }
/*      */ 
/*  425 */     if (null == opacityAlphas) {
/*  426 */       this.opacityAlphas = new float[outputColors.length];
/*  427 */       Arrays.fill(this.opacityAlphas, 1.0F);
/*      */     } else {
/*  429 */       this.opacityAlphas = opacityAlphas;
/*      */     }
/*      */ 
/*  432 */     extractMethods();
/*      */   }
/*      */ 
/*      */   public IndicatorWrapper(String name, OfferSide[] sidesForTicks, IIndicators.AppliedPrice[] appliedPricesForCandles, Object[] optParams, Color[] outputColors, Color[] outputColors2, OutputParameterInfo.DrawingStyle[] drawingStyles, int[] lineWidths, int[] outputShifts)
/*      */     throws InvalidParameterException
/*      */   {
/*  446 */     this(name, sidesForTicks, appliedPricesForCandles, optParams, outputColors, outputColors2, new boolean[outputColors.length], new boolean[outputColors.length], new float[outputColors.length], drawingStyles, lineWidths, outputShifts);
/*      */   }
/*      */ 
/*      */   public IndicatorWrapper(String name, OfferSide[] sidesForTicks, IIndicators.AppliedPrice[] appliedPricesForCandles, Object[] optParams, Color[] outputColors, Color[] outputColors2, boolean[] valuesOnChart, boolean[] showOutputs, float[] opacityAlphas, OutputParameterInfo.DrawingStyle[] drawingStyles, int[] lineWidths, int[] outputShifts)
/*      */     throws InvalidParameterException
/*      */   {
/*  476 */     this(name, -1, sidesForTicks, appliedPricesForCandles, optParams, outputColors, outputColors2, valuesOnChart, showOutputs, opacityAlphas, drawingStyles, lineWidths, outputShifts, null, null, null);
/*      */   }
/*      */ 
/*      */   public boolean isChangeTreeSelection()
/*      */   {
/*  501 */     return this.changeTreeSelection;
/*      */   }
/*      */ 
/*      */   public void setChangeTreeSelection(boolean changeTreeSelection)
/*      */   {
/*  510 */     this.changeTreeSelection = changeTreeSelection;
/*      */   }
/*      */ 
/*      */   private void validate(OfferSide[] sidesForTicks, IIndicators.AppliedPrice[] appliedPricesForCandles, Object[] optParams, Color[] outputColors) {
/*  514 */     if ((this.indicatorHolder == null) || (sidesForTicks == null) || (sidesForTicks.length != this.indicatorHolder.getIndicator().getIndicatorInfo().getNumberOfInputs()) || (appliedPricesForCandles == null) || (appliedPricesForCandles.length != this.indicatorHolder.getIndicator().getIndicatorInfo().getNumberOfInputs()) || (optParams == null) || (optParams.length != this.indicatorHolder.getIndicator().getIndicatorInfo().getNumberOfOptionalInputs()) || (outputColors == null) || (outputColors.length != this.indicatorHolder.getIndicator().getIndicatorInfo().getNumberOfOutputs()))
/*      */     {
/*  524 */       throw new InvalidParameterException("Some of the parameters are wrong");
/*      */     }
/*  526 */     for (OfferSide offerSide : sidesForTicks) {
/*  527 */       if (offerSide == null) {
/*  528 */         throw new InvalidParameterException("Wrong offer side parameter value");
/*      */       }
/*      */     }
/*  531 */     for (IIndicators.AppliedPrice appliedPrice : appliedPricesForCandles) {
/*  532 */       if (appliedPrice == null) {
/*  533 */         throw new InvalidParameterException("Wrong applied price parameter value");
/*      */       }
/*      */     }
/*  536 */     for (Object optParam : optParams) {
/*  537 */       if ((optParam != null) && (((optParam instanceof Integer)) || ((optParam instanceof Double)) || ((optParam instanceof Boolean))))
/*      */       {
/*      */         continue;
/*      */       }
/*      */ 
/*  545 */       throw new InvalidParameterException("Wrong optional parameter value");
/*      */     }
/*      */   }
/*      */ 
/*      */   private void init(String name, int id)
/*      */   {
/*  551 */     init(IndicatorsProvider.getInstance().getIndicatorHolder(name), id);
/*  552 */     this.name = name;
/*      */   }
/*      */ 
/*      */   private void init(IndicatorHolder indicatorHolder, int id) {
/*  556 */     if (id < 0)
/*  557 */       this.id = IdManager.getNextIndicatorId();
/*      */     else {
/*  559 */       this.id = id;
/*      */     }
/*  561 */     IIndicator indicator = indicatorHolder.getIndicator();
/*  562 */     this.name = indicator.getIndicatorInfo().getName();
/*  563 */     this.indicatorHolder = indicatorHolder;
/*  564 */     setChartPanelId(Integer.valueOf(id));
/*      */ 
/*  566 */     int outpCount = indicator.getIndicatorInfo().getNumberOfOutputs();
/*  567 */     this.showValuesOnChart = new boolean[outpCount];
/*  568 */     this.showOutputs = new boolean[outpCount];
/*  569 */     this.opacityAlphas = new float[outpCount];
/*  570 */     for (int i = 0; i < outpCount; i++) {
/*  571 */       OutputParameterInfo info = indicator.getOutputParameterInfo(i);
/*  572 */       setShowValueOnChart(i, info.isShowValueOnChart());
/*  573 */       setShowOutput(i, info.isShowOutput());
/*  574 */       setOpacityAlpha(i, info.getOpacityAlpha());
/*      */     }
/*      */   }
/*      */ 
/*      */   private void initColors() {
/*  579 */     for (int i = 0; i < this.outputColors.length; i++) {
/*  580 */       OutputParameterInfo outputParameterInfo = this.indicatorHolder.getIndicator().getOutputParameterInfo(i);
/*  581 */       if ((outputParameterInfo.getColor() == null) && (this.outputColors.length <= JColorComboBox.COLORS.length)) {
/*  582 */         this.outputColors[i] = JColorComboBox.COLORS[i];
/*  583 */         this.outputColors2[i] = JColorComboBox.COLORS[i];
/*      */       }
/*      */       else {
/*  586 */         this.outputColors[i] = outputParameterInfo.getColor();
/*  587 */         this.outputColors2[i] = outputParameterInfo.getColor2();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void initDrawingStyles() {
/*  593 */     for (int i = 0; i < this.drawingStyles.length; i++) {
/*  594 */       OutputParameterInfo outputParameterInfo = this.indicatorHolder.getIndicator().getOutputParameterInfo(i);
/*  595 */       this.drawingStyles[i] = outputParameterInfo.getDrawingStyle();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void initOutputShifts() {
/*  600 */     for (int i = 0; i < this.outputShifts.length; i++) {
/*  601 */       OutputParameterInfo outputParameterInfo = this.indicatorHolder.getIndicator().getOutputParameterInfo(i);
/*  602 */       this.outputShifts[i] = outputParameterInfo.getShift();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void initLineWidths() {
/*  607 */     for (int i = 0; i < this.lineWidths.length; i++) {
/*  608 */       OutputParameterInfo outputParameterInfo = this.indicatorHolder.getIndicator().getOutputParameterInfo(i);
/*  609 */       this.lineWidths[i] = (outputParameterInfo.getLineWidth() <= 0 ? 1 : outputParameterInfo.getLineWidth());
/*      */     }
/*      */   }
/*      */ 
/*      */   private void extractMethods() {
/*  614 */     Class indicatorClass = this.indicatorHolder.getIndicator().getClass();
/*  615 */     if (IDrawingIndicator.class.isAssignableFrom(indicatorClass))
/*      */       try {
/*  617 */         this.selfdrawingMethod = indicatorClass.getMethod("drawOutput", new Class[] { Graphics.class, Integer.TYPE, Object.class, Color.class, Stroke.class, IIndicatorDrawingSupport.class, List.class, Map.class });
/*      */       } catch (NoSuchMethodException e) {
/*      */       }
/*      */       catch (Exception e) {
/*  621 */         LOGGER.error(e.getMessage(), e);
/*      */       }
/*      */     else
/*      */       try {
/*  625 */         this.selfdrawingMethod = indicatorClass.getMethod("drawOutput", new Class[] { Graphics.class, Integer.TYPE, Object.class, Color.class, IIndicatorDrawingSupport.class, List.class, Map.class });
/*      */       } catch (NoSuchMethodException e) {
/*      */       }
/*      */       catch (Exception e) {
/*  629 */         LOGGER.error(e.getMessage(), e);
/*      */       }
/*      */     try
/*      */     {
/*  633 */       this.minMaxMethod = indicatorClass.getMethod("getMinMax", new Class[] { Integer.TYPE, Object.class, Integer.TYPE, Integer.TYPE });
/*      */     } catch (NoSuchMethodException e) {
/*      */     }
/*      */     catch (Exception e) {
/*  637 */       LOGGER.error(e.getMessage(), e);
/*      */     }
/*      */   }
/*      */ 
/*      */   private OfferSide[] extractSidesForTicks(IIndicator indicator) {
/*  642 */     OfferSide[] sidesForTicks = new OfferSide[indicator.getIndicatorInfo().getNumberOfInputs()];
/*  643 */     for (int i = 0; i < sidesForTicks.length; i++) {
/*  644 */       sidesForTicks[i] = OfferSide.BID;
/*      */     }
/*  646 */     return sidesForTicks;
/*      */   }
/*      */ 
/*      */   private IIndicators.AppliedPrice[] extractAppliedPrices(IIndicator indicator) {
/*  650 */     IIndicators.AppliedPrice[] appliedPricesForCandles = new IIndicators.AppliedPrice[indicator.getIndicatorInfo().getNumberOfInputs()];
/*  651 */     for (int i = 0; i < appliedPricesForCandles.length; i++) {
/*  652 */       InputParameterInfo inputParameterInfo = indicator.getInputParameterInfo(i);
/*  653 */       appliedPricesForCandles[i] = inputParameterInfo.getAppliedPrice();
/*  654 */       if (appliedPricesForCandles[i] == null) {
/*  655 */         appliedPricesForCandles[i] = IIndicators.AppliedPrice.CLOSE;
/*      */       }
/*      */     }
/*  658 */     return appliedPricesForCandles;
/*      */   }
/*      */ 
/*      */   private Object[] extractOptParams(IIndicator indicator) {
/*  662 */     Object[] optParams = new Object[indicator.getIndicatorInfo().getNumberOfOptionalInputs()];
/*  663 */     for (int i = 0; i < optParams.length; i++) {
/*  664 */       OptInputParameterInfo info = indicator.getOptInputParameterInfo(i);
/*  665 */       if ((info.getDescription() instanceof IntegerListDescription)) {
/*  666 */         IntegerListDescription integerList = (IntegerListDescription)info.getDescription();
/*  667 */         optParams[i] = Integer.valueOf(integerList.getDefaultValue());
/*  668 */       } else if ((info.getDescription() instanceof IntegerRangeDescription)) {
/*  669 */         IntegerRangeDescription integerRange = (IntegerRangeDescription)info.getDescription();
/*  670 */         optParams[i] = Integer.valueOf(integerRange.getDefaultValue());
/*  671 */       } else if ((info.getDescription() instanceof DoubleListDescription)) {
/*  672 */         DoubleListDescription realList = (DoubleListDescription)info.getDescription();
/*  673 */         optParams[i] = Double.valueOf(realList.getDefaultValue());
/*  674 */       } else if ((info.getDescription() instanceof DoubleRangeDescription)) {
/*  675 */         DoubleRangeDescription realRange = (DoubleRangeDescription)info.getDescription();
/*  676 */         optParams[i] = Double.valueOf(realRange.getDefaultValue());
/*  677 */       } else if ((info.getDescription() instanceof BooleanOptInputDescription)) {
/*  678 */         BooleanOptInputDescription booleanOptDescription = (BooleanOptInputDescription)info.getDescription();
/*  679 */         optParams[i] = Boolean.valueOf(booleanOptDescription.getDefaultValue());
/*      */       } else {
/*  681 */         throw new IllegalArgumentException(new StringBuilder().append("Unsupported Description type - ").append(info.getDescription()).toString());
/*      */       }
/*      */     }
/*  684 */     return optParams;
/*      */   }
/*      */ 
/*      */   private float[] extractOpacityAlphas(IIndicator indicator) {
/*  688 */     float[] opacityAlphas = new float[indicator.getIndicatorInfo().getNumberOfOutputs()];
/*  689 */     for (int i = 0; i < opacityAlphas.length; i++) {
/*  690 */       OutputParameterInfo info = indicator.getOutputParameterInfo(i);
/*  691 */       if ((info.getOpacityAlpha() >= 0.0F) && (info.getOpacityAlpha() <= 1.0F)) {
/*  692 */         opacityAlphas[i] = info.getOpacityAlpha();
/*      */       }
/*      */       else {
/*  695 */         opacityAlphas[i] = 1.0F;
/*      */       }
/*      */     }
/*  698 */     return opacityAlphas;
/*      */   }
/*      */ 
/*      */   public int getId()
/*      */   {
/*  703 */     return this.id;
/*      */   }
/*      */ 
/*      */   public String getName() {
/*  707 */     return this.name;
/*      */   }
/*      */ 
/*      */   public String getNameWithParams() {
/*  711 */     StringBuffer indicatorDescription = new StringBuffer(this.name);
/*  712 */     if (this.optParams.length > 0) {
/*  713 */       indicatorDescription.append(":");
/*      */     }
/*      */ 
/*  716 */     for (int i = 0; i < this.optParams.length; i++) {
/*  717 */       Object optParam = this.optParams[i];
/*  718 */       if (i == 0) {
/*  719 */         indicatorDescription.append(" (");
/*      */       }
/*  721 */       OptInputParameterInfo info = this.indicatorHolder.getIndicator().getOptInputParameterInfo(i);
/*  722 */       if ((info.getDescription() instanceof IntegerListDescription)) {
/*  723 */         IntegerListDescription integerList = (IntegerListDescription)info.getDescription();
/*      */ 
/*  725 */         int index = -1;
/*  726 */         for (int j = 0; j < integerList.getValues().length; j++) {
/*  727 */           if (integerList.getValues()[j] == ((Integer)optParam).intValue()) {
/*  728 */             index = j;
/*  729 */             break;
/*      */           }
/*      */         }
/*  732 */         if (index == -1) {
/*  733 */           index = ((Integer)optParam).intValue();
/*      */         }
/*      */ 
/*  736 */         if (index > integerList.getValues().length - 1) {
/*  737 */           index = integerList.getValues().length - 1;
/*      */         }
/*  739 */         indicatorDescription.append(integerList.getValueNames()[index]);
/*      */       } else {
/*  741 */         indicatorDescription.append(optParam);
/*      */       }
/*      */ 
/*  744 */       if (i < this.optParams.length - 1)
/*  745 */         indicatorDescription.append(", ");
/*      */       else {
/*  747 */         indicatorDescription.append(")");
/*      */       }
/*      */     }
/*  750 */     return indicatorDescription.toString();
/*      */   }
/*      */ 
/*      */   public void setOfferSideForTicks(int paramIndex, OfferSide side) {
/*  754 */     this.sidesForTicks[paramIndex] = side;
/*      */   }
/*      */ 
/*      */   public void setAppliedPriceForCandles(int paramIndex, IIndicators.AppliedPrice type) {
/*  758 */     this.appliedPricesForCandles[paramIndex] = type;
/*      */   }
/*      */ 
/*      */   public OfferSide[] getOfferSidesForTicks() {
/*  762 */     return this.sidesForTicks;
/*      */   }
/*      */ 
/*      */   public IIndicators.AppliedPrice[] getAppliedPricesForCandles() {
/*  766 */     return this.appliedPricesForCandles;
/*      */   }
/*      */ 
/*      */   public void setOptParam(int paramIndex, Object value) {
/*  770 */     this.optParams[paramIndex] = value;
/*      */   }
/*      */ 
/*      */   public Object[] getOptParams() {
/*  774 */     return this.optParams;
/*      */   }
/*      */ 
/*      */   public boolean isRecalculateOnNewCandleOnly() {
/*  778 */     return this.recalculateOnNewCandleOnly;
/*      */   }
/*      */ 
/*      */   public void setRecalculateOnNewCandleOnly(boolean recalculateOnNewCandleOnly) {
/*  782 */     this.recalculateOnNewCandleOnly = recalculateOnNewCandleOnly;
/*      */   }
/*      */ 
/*      */   public String getPropsStr() {
/*  786 */     if (this.optParams.length == 0) {
/*  787 */       return null;
/*      */     }
/*  789 */     StringBuilder str = new StringBuilder();
/*      */ 
/*  791 */     for (int i = 0; i < this.optParams.length; i++) {
/*  792 */       Object param = this.optParams[i];
/*  793 */       if ((param instanceof Integer)) {
/*  794 */         OptInputParameterInfo info = this.indicatorHolder.getIndicator().getOptInputParameterInfo(i);
/*  795 */         if ((info.getDescription() instanceof IntegerListDescription)) {
/*  796 */           IntegerListDescription integerList = (IntegerListDescription)info.getDescription();
/*      */ 
/*  798 */           int index = -1;
/*  799 */           for (int j = 0; j < integerList.getValues().length; j++) {
/*  800 */             if (integerList.getValues()[j] == ((Integer)param).intValue()) {
/*  801 */               index = j;
/*  802 */               break;
/*      */             }
/*      */           }
/*  805 */           if (index == -1) {
/*  806 */             index = ((Integer)param).intValue();
/*      */           }
/*      */ 
/*  809 */           if (index > integerList.getValues().length - 1) {
/*  810 */             index = integerList.getValues().length - 1;
/*      */           }
/*  812 */           str.append(integerList.getValueNames()[index]).append(", ");
/*      */         } else {
/*  814 */           str.append(param.toString()).append(", ");
/*      */         }
/*  816 */       } else if ((param instanceof Double)) {
/*  817 */         getDecimalFormat(i).format(param);
/*  818 */         str.append(getDecimalFormat(i).format(((Double)param).doubleValue())).append(", ");
/*  819 */       } else if ((param instanceof Boolean)) {
/*  820 */         str.append(String.valueOf(param)).append(", ");
/*      */       }
/*      */     }
/*  823 */     str.setLength(str.length() - 2);
/*  824 */     return str.toString();
/*      */   }
/*      */ 
/*      */   public void setOutputColor(int paramIndex, Color color) {
/*  828 */     this.outputColors[paramIndex] = color;
/*      */   }
/*      */ 
/*      */   public Color[] getOutputColors() {
/*  832 */     return this.outputColors;
/*      */   }
/*      */ 
/*      */   public void setOutputColor2(int paramIndex, Color color) {
/*  836 */     this.outputColors2[paramIndex] = color;
/*      */   }
/*      */ 
/*      */   public Color[] getOutputColors2() {
/*  840 */     if (this.outputColors2 == null) {
/*  841 */       this.outputColors2 = ((Color[])getOutputColors().clone());
/*      */     }
/*  843 */     return this.outputColors2;
/*      */   }
/*      */ 
/*      */   public boolean[] getShowValuesOnChart() {
/*  847 */     return this.showValuesOnChart;
/*      */   }
/*      */ 
/*      */   public boolean showValueOnChart(int outputIdx) {
/*  851 */     return this.showValuesOnChart[outputIdx];
/*      */   }
/*      */ 
/*      */   public void setShowValueOnChart(int outputIdx, boolean showValue) {
/*  855 */     this.showValuesOnChart[outputIdx] = showValue;
/*      */   }
/*      */ 
/*      */   public boolean[] getShowOutputs() {
/*  859 */     return this.showOutputs;
/*      */   }
/*      */ 
/*      */   public boolean showOutput(int outputIdx) {
/*  863 */     return this.showOutputs[outputIdx];
/*      */   }
/*      */ 
/*      */   public void setShowOutput(int outputIdx, boolean showValue) {
/*  867 */     this.showOutputs[outputIdx] = showValue;
/*      */   }
/*      */ 
/*      */   public float[] getOpacityAlphas() {
/*  871 */     return this.opacityAlphas;
/*      */   }
/*      */ 
/*      */   public float getOpacityAlpha(int outputIdx) {
/*  875 */     return this.opacityAlphas[outputIdx];
/*      */   }
/*      */ 
/*      */   public void setOpacityAlpha(int outputIdx, float alphaValue) {
/*  879 */     this.opacityAlphas[outputIdx] = alphaValue;
/*      */   }
/*      */ 
/*      */   public void setDrawingStyle(int paramIndex, OutputParameterInfo.DrawingStyle drawingStyle) {
/*  883 */     this.drawingStyles[paramIndex] = drawingStyle;
/*  884 */     this.indicatorHolder.getIndicator().getOutputParameterInfo(paramIndex).setDrawingStyle(drawingStyle);
/*      */   }
/*      */ 
/*      */   public OutputParameterInfo.DrawingStyle[] getDrawingStyles() {
/*  888 */     return this.drawingStyles;
/*      */   }
/*      */ 
/*      */   public void setOutputShift(int paramIndex, int shift) {
/*  892 */     this.outputShifts[paramIndex] = shift;
/*  893 */     this.indicatorHolder.getIndicator().getOutputParameterInfo(paramIndex).setShift(shift);
/*      */   }
/*      */ 
/*      */   public int[] getOutputShifts() {
/*  897 */     if (this.outputShifts == null) {
/*  898 */       this.outputShifts = new int[this.indicatorHolder.getIndicator().getIndicatorInfo().getNumberOfOutputs()];
/*      */     }
/*  900 */     return this.outputShifts;
/*      */   }
/*      */ 
/*      */   public void setLineWidth(int paramIndex, int width) {
/*  904 */     this.lineWidths[paramIndex] = width;
/*      */   }
/*      */ 
/*      */   public int[] getLineWidths() {
/*  908 */     return this.lineWidths;
/*      */   }
/*      */ 
/*      */   public IIndicator getIndicator() {
/*  912 */     return this.indicatorHolder.getIndicator();
/*      */   }
/*      */ 
/*      */   public IndicatorHolder getIndicatorHolder() {
/*  916 */     return this.indicatorHolder;
/*      */   }
/*      */ 
/*      */   public Method getSelfdrawingMethod() {
/*  920 */     return this.selfdrawingMethod;
/*      */   }
/*      */ 
/*      */   public Method getMinMaxMethod() {
/*  924 */     return this.minMaxMethod;
/*      */   }
/*      */ 
/*      */   public void synchronizeDrawingStyles() {
/*      */     try {
/*  929 */       initDrawingStyles();
/*      */     }
/*      */     catch (Exception e) {
/*  932 */       LOGGER.error(e.getMessage(), e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void copySettingsFrom(IndicatorWrapper sourceIndicatorWrapper) {
/*  937 */     if (!sourceIndicatorWrapper.getIndicator().getIndicatorInfo().getName().toUpperCase().equals(this.indicatorHolder.getIndicator().getIndicatorInfo().getName().toUpperCase())) {
/*  938 */       throw new IllegalArgumentException("Cannot copy setting into indicator from some other type/formula indicator");
/*      */     }
/*  940 */     this.sidesForTicks = new OfferSide[sourceIndicatorWrapper.sidesForTicks.length];
/*  941 */     System.arraycopy(sourceIndicatorWrapper.sidesForTicks, 0, this.sidesForTicks, 0, this.sidesForTicks.length);
/*  942 */     this.appliedPricesForCandles = new IIndicators.AppliedPrice[sourceIndicatorWrapper.appliedPricesForCandles.length];
/*  943 */     System.arraycopy(sourceIndicatorWrapper.appliedPricesForCandles, 0, this.appliedPricesForCandles, 0, this.appliedPricesForCandles.length);
/*  944 */     this.optParams = new Object[sourceIndicatorWrapper.optParams.length];
/*  945 */     System.arraycopy(sourceIndicatorWrapper.optParams, 0, this.optParams, 0, this.optParams.length);
/*  946 */     this.outputColors = new Color[sourceIndicatorWrapper.outputColors.length];
/*  947 */     System.arraycopy(sourceIndicatorWrapper.outputColors, 0, this.outputColors, 0, this.outputColors.length);
/*  948 */     this.drawingStyles = new OutputParameterInfo.DrawingStyle[sourceIndicatorWrapper.drawingStyles.length];
/*  949 */     System.arraycopy(sourceIndicatorWrapper.drawingStyles, 0, this.drawingStyles, 0, this.drawingStyles.length);
/*  950 */     this.lineWidths = new int[sourceIndicatorWrapper.lineWidths.length];
/*  951 */     System.arraycopy(sourceIndicatorWrapper.lineWidths, 0, this.lineWidths, 0, this.lineWidths.length);
/*  952 */     this.showValuesOnChart = new boolean[sourceIndicatorWrapper.showValuesOnChart.length];
/*  953 */     System.arraycopy(sourceIndicatorWrapper.showValuesOnChart, 0, this.showValuesOnChart, 0, this.showValuesOnChart.length);
/*  954 */     this.showOutputs = new boolean[sourceIndicatorWrapper.showOutputs.length];
/*  955 */     System.arraycopy(sourceIndicatorWrapper.showOutputs, 0, this.showOutputs, 0, this.showOutputs.length);
/*  956 */     this.opacityAlphas = new float[sourceIndicatorWrapper.opacityAlphas.length];
/*  957 */     System.arraycopy(sourceIndicatorWrapper.opacityAlphas, 0, this.opacityAlphas, 0, this.opacityAlphas.length);
/*  958 */     this.outputShifts = new int[sourceIndicatorWrapper.outputShifts.length];
/*  959 */     System.arraycopy(sourceIndicatorWrapper.outputShifts, 0, this.outputShifts, 0, this.outputShifts.length);
/*  960 */     setRecalculateOnNewCandleOnly(sourceIndicatorWrapper.isRecalculateOnNewCandleOnly());
/*      */   }
/*      */ 
/*      */   public IndicatorWrapper clone()
/*      */   {
/*      */     try {
/*  966 */       IndicatorWrapper clone = (IndicatorWrapper)super.clone();
/*  967 */       clone.indicatorHolder = IndicatorsProvider.getInstance().getIndicatorHolder(this.indicatorHolder.getIndicator().getIndicatorInfo().getName());
/*  968 */       clone.sidesForTicks = new OfferSide[this.sidesForTicks.length];
/*  969 */       System.arraycopy(this.sidesForTicks, 0, clone.sidesForTicks, 0, this.sidesForTicks.length);
/*  970 */       clone.appliedPricesForCandles = new IIndicators.AppliedPrice[this.appliedPricesForCandles.length];
/*  971 */       System.arraycopy(this.appliedPricesForCandles, 0, clone.appliedPricesForCandles, 0, this.appliedPricesForCandles.length);
/*  972 */       clone.optParams = new Object[this.optParams.length];
/*  973 */       System.arraycopy(this.optParams, 0, clone.optParams, 0, this.optParams.length);
/*  974 */       clone.outputColors = new Color[this.outputColors.length];
/*  975 */       System.arraycopy(this.outputColors, 0, clone.outputColors, 0, this.outputColors.length);
/*  976 */       clone.drawingStyles = new OutputParameterInfo.DrawingStyle[this.drawingStyles.length];
/*  977 */       System.arraycopy(this.drawingStyles, 0, clone.drawingStyles, 0, this.drawingStyles.length);
/*  978 */       clone.lineWidths = new int[this.lineWidths.length];
/*  979 */       System.arraycopy(this.lineWidths, 0, clone.lineWidths, 0, this.lineWidths.length);
/*  980 */       clone.showValuesOnChart = new boolean[this.showValuesOnChart.length];
/*  981 */       System.arraycopy(this.showValuesOnChart, 0, clone.showValuesOnChart, 0, this.showValuesOnChart.length);
/*  982 */       clone.showOutputs = new boolean[this.showOutputs.length];
/*  983 */       System.arraycopy(this.showOutputs, 0, clone.showOutputs, 0, this.showOutputs.length);
/*  984 */       clone.opacityAlphas = new float[this.opacityAlphas.length];
/*  985 */       System.arraycopy(this.opacityAlphas, 0, clone.opacityAlphas, 0, this.opacityAlphas.length);
/*  986 */       clone.outputShifts = new int[this.outputShifts.length];
/*  987 */       System.arraycopy(this.outputShifts, 0, clone.outputShifts, 0, this.outputShifts.length);
/*      */ 
/*  989 */       return clone;
/*      */     } catch (Exception e) {
/*      */     }
/*  992 */     return null;
/*      */   }
/*      */ 
/*      */   public int hashCode()
/*      */   {
/*  998 */     int prime = 31;
/*  999 */     int result = 1;
/* 1000 */     result = 31 * result + this.id;
/* 1001 */     return result;
/*      */   }
/*      */ 
/*      */   public boolean equals(Object obj)
/*      */   {
/* 1006 */     if (this == obj)
/* 1007 */       return true;
/* 1008 */     if (obj == null)
/* 1009 */       return false;
/* 1010 */     if (getClass() != obj.getClass())
/* 1011 */       return false;
/* 1012 */     IndicatorWrapper other = (IndicatorWrapper)obj;
/*      */ 
/* 1014 */     return this.id == other.id;
/*      */   }
/*      */ 
/*      */   public boolean isOverlappingIndicator(int outputIdx)
/*      */   {
/* 1021 */     OutputParameterInfo outputParameterInfo = this.indicatorHolder.getIndicator().getOutputParameterInfo(outputIdx);
/* 1022 */     if (outputParameterInfo.getType() != OutputParameterInfo.Type.DOUBLE) {
/* 1023 */       return false;
/*      */     }
/*      */ 
/* 1026 */     return this.drawingStyles[outputIdx] != OutputParameterInfo.DrawingStyle.HISTOGRAM;
/*      */   }
/*      */ 
/*      */   public boolean shouldBeShownOnSubWin()
/*      */   {
/* 1032 */     return !this.indicatorHolder.getIndicator().getIndicatorInfo().isOverChart();
/*      */   }
/*      */ 
/*      */   public boolean isLevelsEnabled() {
/* 1036 */     if (shouldBeShownOnSubWin()) {
/* 1037 */       return true;
/*      */     }
/* 1039 */     for (OutputParameterInfo.DrawingStyle drawingStyle : getDrawingStyles()) {
/* 1040 */       if (!drawingStyle.isOutputAsLine()) {
/* 1041 */         return false;
/*      */       }
/*      */     }
/* 1044 */     return true;
/*      */   }
/*      */ 
/*      */   public Integer getSubPanelId()
/*      */   {
/* 1050 */     return this.subPanelId;
/*      */   }
/*      */ 
/*      */   public void setSubPanelId(Integer subPanelId) {
/* 1054 */     this.subPanelId = subPanelId;
/*      */   }
/*      */ 
/*      */   public List<IChartObject> getChartObjects() {
/* 1058 */     return this.chartObjects;
/*      */   }
/*      */ 
/*      */   public void setChartObjects(List<IChartObject> chartObjects) {
/* 1062 */     this.chartObjects = chartObjects;
/*      */   }
/*      */ 
/*      */   public List<LevelInfo> getLevelInfoList() {
/* 1066 */     if (this.levelInfoList == null) {
/* 1067 */       this.levelInfoList = new ArrayList();
/*      */ 
/* 1069 */       IIndicator indicator = getIndicator();
/* 1070 */       if (((indicator instanceof ConnectorIndicator)) && (((ConnectorIndicator)indicator).getLevels().size() > 0) && (getLevelInfoList().size() == 0))
/*      */       {
/* 1074 */         ArrayList levels = ((ConnectorIndicator)indicator).getLevels();
/* 1075 */         List levelInfo = new ArrayList();
/*      */ 
/* 1077 */         for (ConnectorIndicator.ConnectorIndicatorLevel level : levels) {
/* 1078 */           levelInfo.add(new LevelInfo(level.getValue()));
/*      */         }
/* 1080 */         setLevelInfoList(levelInfo);
/*      */       }
/*      */ 
/* 1083 */       String indicatorName = this.indicatorHolder.getIndicator().getIndicatorInfo().getName();
/* 1084 */       if (indicatorName.equalsIgnoreCase("RSI")) {
/* 1085 */         this.levelInfoList.add(new LevelInfo("", 30.0D, OutputParameterInfo.DrawingStyle.DASH_LINE, Color.BLACK, 1, 1.0F));
/* 1086 */         this.levelInfoList.add(new LevelInfo("", 70.0D, OutputParameterInfo.DrawingStyle.DASH_LINE, Color.BLACK, 1, 1.0F));
/* 1087 */       } else if (indicatorName.equalsIgnoreCase("STOCH")) {
/* 1088 */         this.levelInfoList.add(new LevelInfo("", 20.0D, OutputParameterInfo.DrawingStyle.DASH_LINE, Color.BLACK, 1, 1.0F));
/* 1089 */         this.levelInfoList.add(new LevelInfo("", 80.0D, OutputParameterInfo.DrawingStyle.DASH_LINE, Color.BLACK, 1, 1.0F));
/* 1090 */       } else if (indicatorName.equalsIgnoreCase("RVI")) {
/* 1091 */         this.levelInfoList.add(new LevelInfo("", 0.0D, OutputParameterInfo.DrawingStyle.DASH_LINE, Color.BLACK, 1, 1.0F));
/* 1092 */       } else if (indicatorName.equalsIgnoreCase("CCI")) {
/* 1093 */         this.levelInfoList.add(new LevelInfo("", 100.0D, OutputParameterInfo.DrawingStyle.DASH_LINE, Color.BLACK, 1, 1.0F));
/* 1094 */         this.levelInfoList.add(new LevelInfo("", 0.0D, OutputParameterInfo.DrawingStyle.DASH_LINE, Color.BLACK, 1, 1.0F));
/* 1095 */         this.levelInfoList.add(new LevelInfo("", -100.0D, OutputParameterInfo.DrawingStyle.DASH_LINE, Color.BLACK, 1, 1.0F));
/* 1096 */       } else if (indicatorName.equalsIgnoreCase("WILLR")) {
/* 1097 */         this.levelInfoList.add(new LevelInfo("", -20.0D, OutputParameterInfo.DrawingStyle.DASH_LINE, Color.BLACK, 1, 1.0F));
/* 1098 */         this.levelInfoList.add(new LevelInfo("", -80.0D, OutputParameterInfo.DrawingStyle.DASH_LINE, Color.BLACK, 1, 1.0F));
/* 1099 */       } else if (indicatorName.equalsIgnoreCase("MFI")) {
/* 1100 */         this.levelInfoList.add(new LevelInfo("", 80.0D, OutputParameterInfo.DrawingStyle.DASH_LINE, Color.BLACK, 1, 1.0F));
/* 1101 */         this.levelInfoList.add(new LevelInfo("", 20.0D, OutputParameterInfo.DrawingStyle.DASH_LINE, Color.BLACK, 1, 1.0F));
/* 1102 */       } else if (indicatorName.equalsIgnoreCase("OBV")) {
/* 1103 */         this.levelInfoList.add(new LevelInfo("", 0.0D, OutputParameterInfo.DrawingStyle.DASH_LINE, Color.BLACK, 1, 1.0F));
/*      */       }
/*      */     }
/* 1106 */     return this.levelInfoList;
/*      */   }
/*      */ 
/*      */   public void setLevelInfoList(List<LevelInfo> levelInfoList) {
/* 1110 */     this.levelInfoList = levelInfoList;
/*      */   }
/*      */ 
/*      */   public DecimalFormat getDecimalFormat(int optInputIndex) {
/* 1114 */     String format = "0";
/* 1115 */     OptInputParameterInfo optInputParameterInfo = this.indicatorHolder.getIndicator().getOptInputParameterInfo(optInputIndex);
/* 1116 */     if ((optInputParameterInfo.getDescription() instanceof DoubleRangeDescription)) {
/* 1117 */       DoubleRangeDescription doubleRangeDescription = (DoubleRangeDescription)optInputParameterInfo.getDescription();
/* 1118 */       if (doubleRangeDescription.getPrecision() > 0) {
/* 1119 */         format = new StringBuilder().append(format).append(".").toString();
/* 1120 */         for (int i = 0; i < doubleRangeDescription.getPrecision(); i++)
/* 1121 */           format = new StringBuilder().append(format).append("0").toString();
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/* 1126 */       format = new StringBuilder().append(format).append(".00").toString();
/*      */     }
/*      */ 
/* 1129 */     DecimalFormat decimalFormat = new DecimalFormat(format);
/* 1130 */     return decimalFormat;
/*      */   }
/*      */ 
/*      */   public Integer getChartPanelId()
/*      */   {
/* 1137 */     return this.chartPanelId;
/*      */   }
/*      */ 
/*      */   public void setChartPanelId(Integer chartPanelId) {
/* 1141 */     this.chartPanelId = chartPanelId;
/* 1142 */     if ((this.indicatorHolder.getIndicator() instanceof ConnectorIndicator))
/* 1143 */       ((ConnectorIndicator)this.indicatorHolder.getIndicator()).getConnector().setChartPanelId(chartPanelId.intValue());
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.IndicatorWrapper
 * JD-Core Version:    0.6.0
 */