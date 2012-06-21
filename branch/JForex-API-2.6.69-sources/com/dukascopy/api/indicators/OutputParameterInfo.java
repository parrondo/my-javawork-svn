/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.indicators;

import java.awt.Color;
import java.util.Arrays;

/**
 * Describes output
 * 
 * @author Dmitry Shohov
 */
public class OutputParameterInfo {
    /**
     * Show output as line
     * @deprecated use {@link DrawingStyle#LINE} instead
     */
    public static final int LINE = 0x00000001;
    /**
     * Show output as dotted line
     * @deprecated use {@link DrawingStyle#DOT_LINE} instead
     */
    public static final int DOT_LINE = 0x00000002;
    /**
     * Show output as dashed line
     * @deprecated use {@link DrawingStyle#DASH_LINE} instead
     */
    public static final int DASH_LINE = 0x00000004;
    /**
     * Show output as dots
     * @deprecated use {@link DrawingStyle#DOTS} instead
     */
    public static final int DOT = 0x00000008;
    /**
     * Show output as histogram
     * @deprecated use {@link DrawingStyle#HISTOGRAM} instead
     */
    public static final int HISTO = 0x00000010;
    /**
     * If value is not zero, then draw pattern
     * @deprecated use {@link DrawingStyle#PATTERN_BOOL} instead
     */
    public static final int PATTERN_BOOL = 0x00000020;
    /**
     * If value:
     *      == 0 - no pattern
     *      > 0 - bullish
     *      < 0 - bearish
     * @deprecated use {@link DrawingStyle#PATTERN_BULL_BEAR} instead
     */
    public static final int PATTERN_BULL_BEAR = 0x00000040;
    /**
     * If value:
     *      == 0 - neutral
     *      ]0..100] getting bullish
     *      ]100..200] bullish
     *      [-100..0[ getting bearish
     *      [-200..100[ bearish
     * @deprecated use {@link DrawingStyle#PATTERN_STRENGTH} instead
     */
    public static final int PATTERN_STRENGTH = 0x00000080;
//    public static final int TA_OUT_POSITIVE = OutputFlags.TA_OUT_POSITIVE;
//    public static final int TA_OUT_NEGATIVE = OutputFlags.TA_OUT_NEGATIVE;
//    public static final int TA_OUT_ZERO = OutputFlags.TA_OUT_ZERO;
    
    /**
     * Specifies how to draw output
     * 
     * @author Dmitry Shohov
     */
    public enum DrawingStyle {
        /**
         * Don't draw anything
         */
        NONE(0),
        /**
         * Show output as line
         */
        LINE(0x00000001),
        /**
         * Show output as dotted line
         */
        DOT_LINE(0x00000002),
        /**
         * Show output as dashed line
         */
        DASH_LINE(0x00000004),
        /**
         * Show output as dashed and dotted line
         */
        DASHDOT_LINE(0),
        /**
         * Show output as dashed and double dotted line
         */
        DASHDOTDOT_LINE(0),
        /**
         * Show output as level. Levels are horizontal lines that are drawn by the price at last candle
         */
        LEVEL_LINE(0),
        /**
         * Show output as dotted level. Levels are horizontal lines that are drawn by the price at last candle
         */
        LEVEL_DOT_LINE(0),
        /**
         * Show output as dashed level. Levels are horizontal lines that are drawn by the price at last candle
         */
        LEVEL_DASH_LINE(0),
        /**
         * Show output as dashed and dotted level. Levels are horizontal lines that are drawn by the price at last candle
         */
        LEVEL_DASHDOT_LINE(0),
        /**
         * Show output as dashed and double dotted level. Levels are horizontal lines that are drawn by the price at last candle
         */
        LEVEL_DASHDOTDOT_LINE(0),
        /**
         * Show output as dots
         */
        DOTS(0x00000008),
        /**
         * Show output as histogram
         */
        HISTOGRAM(0x00000010),
        /**
         * Draws arrow symbol at non NaN or no Integer.MIN_VALUE values
         */
        ARROW_SYMBOL_UP(0),
        /**
         * Draw arrow symbol at non NaN or no Integer.MIN_VALUE values
         */
        ARROW_SYMBOL_DOWN(0),
//        /**
//         * Draws line between no NaN or no Integer.MIN_VALUE values
//         */
//        SECTION(0),
        /**
         * If value is not zero, then draw pattern
         */
        PATTERN_BOOL(0x00000020),
        /**
         * If value:
         *      == 0 - no pattern
         *      > 0 - bullish
         *      < 0 - bearish
         */
        PATTERN_BULL_BEAR(0x00000040),
        /**
         * If value:
         *      == 0 - neutral
         *      ]0..100] getting bullish
         *      ]100.. bullish
         *      [-100..0[ getting bearish
         *      ..100[ bearish
         */
        PATTERN_STRENGTH(0x00000080);
        
        public static final DrawingStyle[] LINE_STYLE = new DrawingStyle[] {
    		DrawingStyle.LINE,
    		DrawingStyle.DOT_LINE,
    		DrawingStyle.DASH_LINE,
    		DrawingStyle.DASHDOT_LINE,
    		DrawingStyle.DASHDOTDOT_LINE,
    	};
        
        private int flag;
        private DrawingStyle(int flag) {
            this.flag = flag;
        }
        
        public static DrawingStyle fromFlagValue(int flag) {
            for (DrawingStyle style : DrawingStyle.values()) {
                if ((style.flag & flag) > 0) {
                    return style;
                }
            }
            return NONE;
        }
        
        public int getFlagValue() {
            return flag;
        }
        
        public boolean isOutputAsLine(){
        	return Arrays.binarySearch(LINE_STYLE, this) >= 0;
        }
    }
    
    /**
     * Type of the output
     * 
     * @author Dmitry Shohov
     */
    public enum Type {
        /**
         * Output is array of doubles, Double.NaN values means there is no value at that point
         */
        DOUBLE,
        /**
         * Output is array of integers, Integer.MIN_VALUE values means there is no value at that point
         */
        INT,
        /**
         * Any object, outputs with this type can be only interpreted by strategies or drawn by selfdrawing indicators
         */
        OBJECT
    }
    
    private String name;
    private Type type;
    private DrawingStyle drawingStyle;
    private Color color;
    private Color color2;
    private char arrowSymbol;
    private boolean histogramTwoColor;
    private int shift;
    private int lineWidth;
    private boolean drawnByIndicator;
    private boolean gapAtNaN;
    private boolean showValueOnChart = true;
    private boolean showOutput = true;
    private float opacityAlpha = 1f;
    

    /**
     * Creates empty parameter description without setting any field
     */
    public OutputParameterInfo() {
    }

    /**
     * Creates output parameter descriptor and sets all fields
     * 
     * @param name name of the output
     * @param type type of the output
     * @param flags flags of the output
     * @deprecated use {@link #OutputParameterInfo(String, Type, DrawingStyle)} instead
     */
    public OutputParameterInfo(String name, Type type, int flags) {
        this.drawingStyle = DrawingStyle.fromFlagValue(flags);
        this.name = name;
        this.type = type;
    }

    /**
     * Creates output parameter descriptor and sets all fields.
     * @param name name of the output
     * @param type type of the output
     * @param drawingStyle specifies how to draw this output
     */
    public OutputParameterInfo(String name, Type type, DrawingStyle drawingStyle) {
        this(name, type, drawingStyle, true);
    }

    /**
     * Creates output parameter descriptor and sets all fields.
     * @param name name of the output
     * @param type type of the output
     * @param drawingStyle specifies how to draw this output
     * @param lastValueOnChart <code>true</code> to show indicator's last value
     * for this output on chart.
     */
    public OutputParameterInfo(String name, Type type, DrawingStyle drawingStyle, boolean lastValueOnChart) {
        this.drawingStyle = drawingStyle;
        this.name = name;
        this.type = type;
        if (drawingStyle == DrawingStyle.ARROW_SYMBOL_DOWN) {
            arrowSymbol = '\u21D3';
        } else if (drawingStyle == DrawingStyle.ARROW_SYMBOL_UP) {
            arrowSymbol = '\u21D1';
        }
        setShowValueOnChart(lastValueOnChart);
    }
    
    /**
     * Returns name of the output
     * 
     * @return name of the output
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name of the output
     * 
     * @param name name of the output
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns type of the output
     * 
     * @return type of the output
     */
    public Type getType() {
        return type;
    }

    /**
     * Sets type of the output
     * 
     * @param type type of the output
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * Returns flags of the output
     * 
     * @return flags of the output
     * @deprecated use {@link #getDrawingStyle()} instead
     */
    public int getFlags() {
        return drawingStyle.flag;
    }

    /**
     * Sets flags of the output. Use | operator to specify more than one flag
     * 
     * @param flags flags of the output
     * @deprecated use {@link #setDrawingStyle(DrawingStyle)} instead
     */
    public void setFlags(int flags) {
        this.drawingStyle = DrawingStyle.fromFlagValue(flags);
    }

    /**
     * Returns style that specifies how to draw this output
     *
     * @return style that specifies how to draw this output
     */
    public DrawingStyle getDrawingStyle() {
        return drawingStyle;
    }

    /**
     * Sets style that specifies how to draw this output
     * 
     * @param drawingStyle specifies how to draw this output
     */
    public void setDrawingStyle(DrawingStyle drawingStyle) {
        this.drawingStyle = drawingStyle;
    }

    /**
     * Returns default color for output or null if no color was set
     * Used as <b>trend up</b> color for outputs with {@link DrawingStyle#isOutputAsLine()} is <tt>true</tt>
     * @return default color for output or null
     * 
     * @see #getColor2()
     */
    public Color getColor() {
        return color;
    }

    /**
     * Sets default color to use when drawing this output
     * @param color color to draw with
     */
    public void setColor(Color color) {
        this.color = color;
    }
    

	/**
	 * Used as <b>trend down</b> color for outputs with {@link DrawingStyle#isOutputAsLine()} is <tt>true</tt>
	 * By default equals to {@link #getColor() color}
	 * @return the color2
	 * 
	 * @see #getColor()
	 */
	public Color getColor2() {
		if (color2 == null){
			color2 = getColor();
		}
		return color2;
	}

	/**
	 * Sets default color used when drawing <b>trend down</b> outputs
	 * @param color2 the color2 to set
	 */
	public void setColor2(Color color2) {
		this.color2 = color2;
	}

	/**
     * Returns arrow character for outputs with {@link DrawingStyle#ARROW_SYMBOL_UP} or {@link DrawingStyle#ARROW_SYMBOL_DOWN} style
     * 
     * @return arrow character
     */
    public char getArrowSymbol() {
        return arrowSymbol;
    }

    /**
     * Sets arrow character for outputs with {@link DrawingStyle#ARROW_SYMBOL_UP} or {@link DrawingStyle#ARROW_SYMBOL_DOWN} style
     * 
     * @param arrowSymbol arrow character
     */
    public void setArrowSymbol(char arrowSymbol) {
        this.arrowSymbol = arrowSymbol;
    }
    
    /**
     * If set to true and drawing style is histogram, then it will be shown in two colors, positive values with green and negative with red
     * 
     * @param histogramTwoColor if true, then show histogram in two colors
     */
    public void setHistogramTwoColor(boolean histogramTwoColor) {
        this.histogramTwoColor = histogramTwoColor;
    }

    /**
     * Returns true if histogram should be shown in two colors
     * 
     * @return true if histogram should be shown in two colors
     */
    public boolean isHistogramTwoColor() {
        return histogramTwoColor;
    }

    /**
     * Shifts output by number of candles returned
     * @return number of candles to shift this output
     */
    public int getShift() {
        return shift;
    }

    /**
     * Sets number of candles to shift this output
     * @param shift number of candles to shift this output
     */
    public void setShift(int shift) {
        this.shift = shift;
    }

    /**
     * Returns true if indicator draws this output itself
     *
     * @return true if indicator draws this output itself
     */
    public boolean isDrawnByIndicator() {
        return drawnByIndicator;
    }

    /**
     * If set to true, than indicator should have public method
     * <code>public void drawOutput(Graphics g, int outputIdx, Object values, Color color, IIndicatorDrawingSupport indicatorDrawingSupport, List<Shape> shapes, Map<Color, List<Point>>Map<Color, List<Point>> handles)</code>
     * Also it's possible to define <code>public double[] getMinMax(int outputIdx, Object values)</code> methods,
     * that will be called to define minimum and maximum values for scale
     * Please look at the description of IIndicator interface for more info
     *
     * @param drawnByIndicator true if indicator draws this output itself
     */
    public void setDrawnByIndicator(boolean drawnByIndicator) {
        this.drawnByIndicator = drawnByIndicator;
    }

    /**
     * Returns true if drawing logic should make a gap in lines when there is a NaN or Integer.MIN_VALUE in output
     * @return true for gaps
     */
    public boolean isGapAtNaN() {
        return gapAtNaN;
    }

    /**
     * Set to true to make gaps at candles with Double.NaN or Integer.MIN_VALUE in output
     * @param gapAtNaN true for gaps
     */
    public void setGapAtNaN(boolean gapAtNaN) {
        this.gapAtNaN = gapAtNaN;
    }

    /**
     * Checks should last value be specially drawn on chart or not.
     * @return <code>true</code> to draw the last value, <code>false</code> to not.
     */
    public boolean isShowValueOnChart() {
        return showValueOnChart;
    }

    /**
     * Defines should last value be specially drawn on chart or not.
     * @param showValueOnChart <code>true</code> to draw the last value,
     * <code>false</code> to not.
     */
    public void setShowValueOnChart(boolean showValueOnChart) {
        this.showValueOnChart = showValueOnChart;
    }
    
    public boolean isShowOutput() {
		return showOutput;
	}

	public void setShowOutput(boolean showOutput) {
		this.showOutput = showOutput;
	}

	/**
	 * Returns transparency alpha value
	 * @return 
	 */
	public float getOpacityAlpha() {
    	return opacityAlpha;
    }

	public void setOpacityAlpha(float alpha) {
    	this.opacityAlpha = alpha;
    }

	/**
	 * Returns drawing line width
	 * @return drawing line width
	 */
    public int getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }
	
	
}
