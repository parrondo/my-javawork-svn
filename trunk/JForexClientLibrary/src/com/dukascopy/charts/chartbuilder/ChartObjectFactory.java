/*     */ package com.dukascopy.charts.chartbuilder;
/*     */ 
/*     */ import com.dukascopy.api.IChartObject;
/*     */ import com.dukascopy.api.drawings.IAndrewsPitchforkChartObject;
/*     */ import com.dukascopy.api.drawings.IChannelChartObject;
/*     */ import com.dukascopy.api.drawings.IChartObjectFactory;
/*     */ import com.dukascopy.api.drawings.ICyclesChartObject;
/*     */ import com.dukascopy.api.drawings.IEllipseChartObject;
/*     */ import com.dukascopy.api.drawings.IFiboArcChartObject;
/*     */ import com.dukascopy.api.drawings.IFiboExpansionChartObject;
/*     */ import com.dukascopy.api.drawings.IFiboFanChartObject;
/*     */ import com.dukascopy.api.drawings.IFiboRetracementChartObject;
/*     */ import com.dukascopy.api.drawings.IFiboTimeZonesChartObject;
/*     */ import com.dukascopy.api.drawings.IGannAnglesChartObject;
/*     */ import com.dukascopy.api.drawings.IGannGridChartObject;
/*     */ import com.dukascopy.api.drawings.IHorizontalLineChartObject;
/*     */ import com.dukascopy.api.drawings.ILabelChartObject;
/*     */ import com.dukascopy.api.drawings.ILongLineChartObject;
/*     */ import com.dukascopy.api.drawings.IOhlcChartObject;
/*     */ import com.dukascopy.api.drawings.IOrderLineChartObject;
/*     */ import com.dukascopy.api.drawings.IPercentChartObject;
/*     */ import com.dukascopy.api.drawings.IPolyLineChartObject;
/*     */ import com.dukascopy.api.drawings.IPriceMarkerChartObject;
/*     */ import com.dukascopy.api.drawings.IRayLineChartObject;
/*     */ import com.dukascopy.api.drawings.IRectangleChartObject;
/*     */ import com.dukascopy.api.drawings.IShortLineChartObject;
/*     */ import com.dukascopy.api.drawings.ISignalDownChartObject;
/*     */ import com.dukascopy.api.drawings.ISignalUpChartObject;
/*     */ import com.dukascopy.api.drawings.ITextChartObject;
/*     */ import com.dukascopy.api.drawings.ITimeMarkerChartObject;
/*     */ import com.dukascopy.api.drawings.ITriangleChartObject;
/*     */ import com.dukascopy.api.drawings.IVerticalLineChartObject;
/*     */ import com.dukascopy.charts.drawings.AndrewsPitchforkChartObject;
/*     */ import com.dukascopy.charts.drawings.ChannelChartObject;
/*     */ import com.dukascopy.charts.drawings.ChartObject;
/*     */ import com.dukascopy.charts.drawings.CyclesChartObject;
/*     */ import com.dukascopy.charts.drawings.EllipseChartObject;
/*     */ import com.dukascopy.charts.drawings.FiboArcChartObject;
/*     */ import com.dukascopy.charts.drawings.FiboExpansionChartObject;
/*     */ import com.dukascopy.charts.drawings.FiboFanChartObject;
/*     */ import com.dukascopy.charts.drawings.FiboRetracementChartObject;
/*     */ import com.dukascopy.charts.drawings.FiboTimeZonesChartObject;
/*     */ import com.dukascopy.charts.drawings.GannAnglesChartObject;
/*     */ import com.dukascopy.charts.drawings.GannGridChartObject;
/*     */ import com.dukascopy.charts.drawings.HLineChartObject;
/*     */ import com.dukascopy.charts.drawings.LabelChartObject;
/*     */ import com.dukascopy.charts.drawings.LongLineChartObject;
/*     */ import com.dukascopy.charts.drawings.OhlcChartObject;
/*     */ import com.dukascopy.charts.drawings.OrderLineChartObject;
/*     */ import com.dukascopy.charts.drawings.PercentChartObject;
/*     */ import com.dukascopy.charts.drawings.PolyLineChartObject;
/*     */ import com.dukascopy.charts.drawings.PriceMarkerChartObject;
/*     */ import com.dukascopy.charts.drawings.RayLineChartObject;
/*     */ import com.dukascopy.charts.drawings.RectangleChartObject;
/*     */ import com.dukascopy.charts.drawings.ShortLineChartObject;
/*     */ import com.dukascopy.charts.drawings.SignalDownChartObject;
/*     */ import com.dukascopy.charts.drawings.SignalUpChartObject;
/*     */ import com.dukascopy.charts.drawings.TextChartObject;
/*     */ import com.dukascopy.charts.drawings.TimeMarkerChartObject;
/*     */ import com.dukascopy.charts.drawings.TriangleChartObject;
/*     */ import com.dukascopy.charts.drawings.VLineChartObject;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class ChartObjectFactory
/*     */   implements IChartObjectFactory
/*     */ {
/*  74 */   private static Map<Class<? extends IChartObject>, Class<?>> TYPE_OBJECT_MAP = new HashMap();
/*     */ 
/*  76 */   private static Map<Class<?>, Class<?>> PARAMETER_TYPES = new HashMap();
/*     */ 
/*     */   private <T extends IChartObject> T doCreate(Class<T> objectClass, Object[] args)
/*     */   {
/* 130 */     if (objectClass == null) {
/* 131 */       return null;
/*     */     }
/*     */ 
/* 134 */     Class found = (Class)TYPE_OBJECT_MAP.get(objectClass);
/*     */ 
/* 136 */     if (found == null)
/* 137 */       return null;
/*     */     try
/*     */     {
/*     */       IChartObject newInstance;
/*     */       IChartObject newInstance;
/* 143 */       if ((args != null) && (args.length != 0))
/*     */       {
/* 145 */         for (int i = 0; i < args.length; i++) {
/* 146 */           if (args[i] == null) {
/* 147 */             throw new IllegalArgumentException("Given parameters contains null value(s), please specify not-null values for all parameters.");
/*     */           }
/*     */         }
/*     */ 
/* 151 */         Class[] argClasses = new Class[args.length];
/* 152 */         int i = 0;
/* 153 */         for (Object object : args) {
/* 154 */           Class clazz = object.getClass();
/* 155 */           Class parameterType = (Class)PARAMETER_TYPES.get(clazz);
/* 156 */           if (parameterType != null)
/* 157 */             argClasses[(i++)] = parameterType;
/*     */           else {
/* 159 */             argClasses[(i++)] = clazz;
/*     */           }
/*     */         }
/* 162 */         Constructor constructor = found.getConstructor(argClasses);
/* 163 */         newInstance = (IChartObject)constructor.newInstance(args);
/*     */       } else {
/* 165 */         newInstance = (IChartObject)found.newInstance();
/*     */       }
/* 167 */       ((ChartObject)newInstance).setUnderEdit(false);
/* 168 */       return newInstance; } catch (Exception e) {
/*     */     }
/* 170 */     throw new RuntimeException(e);
/*     */   }
/*     */ 
/*     */   public <T extends IChartObject> T create(Class<T> objectClass)
/*     */   {
/* 176 */     return doCreate(objectClass, new Object[0]);
/*     */   }
/*     */ 
/*     */   public <T extends IChartObject> T create(Class<T> objectClass, String key)
/*     */   {
/* 181 */     return doCreate(objectClass, new Object[] { key });
/*     */   }
/*     */ 
/*     */   public <T extends IChartObject> T create(Class<T> objectClass, Object[] args)
/*     */   {
/* 186 */     return doCreate(objectClass, args);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public IRectangleChartObject createRectangle(Object[] params)
/*     */   {
/* 194 */     return (IRectangleChartObject)create(IRectangleChartObject.class, params);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public IEllipseChartObject createEllipse(Object[] params) {
/* 200 */     return (IEllipseChartObject)create(IEllipseChartObject.class, params);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public ITriangleChartObject createTriangle(Object[] params) {
/* 206 */     return (ITriangleChartObject)create(ITriangleChartObject.class, params);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public IHorizontalLineChartObject createHorizontalLine(Object[] params) {
/* 212 */     return (IHorizontalLineChartObject)create(IHorizontalLineChartObject.class, params);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public IVerticalLineChartObject createVerticalLine(Object[] params) {
/* 218 */     return (IVerticalLineChartObject)create(IVerticalLineChartObject.class, params);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public ILongLineChartObject createLongLine(Object[] params) {
/* 224 */     return (ILongLineChartObject)create(ILongLineChartObject.class, params);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public ITextChartObject createText(Object[] params) {
/* 230 */     return (ITextChartObject)create(ITextChartObject.class, params);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public ISignalUpChartObject createSignalUp(Object[] params) {
/* 236 */     return (ISignalUpChartObject)create(ISignalUpChartObject.class, params);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public ISignalDownChartObject createSignalDown(Object[] params) {
/* 242 */     return (ISignalDownChartObject)create(ISignalDownChartObject.class, params);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public IShortLineChartObject createShortLine(Object[] params) {
/* 248 */     return (IShortLineChartObject)create(IShortLineChartObject.class, params);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public IRayLineChartObject createRayLine(Object[] params) {
/* 254 */     return (IRayLineChartObject)create(IRayLineChartObject.class, params);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public ITimeMarkerChartObject createTimeMarker(Object[] params) {
/* 260 */     return (ITimeMarkerChartObject)create(ITimeMarkerChartObject.class, params);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public IPriceMarkerChartObject createPriceMarker(Object[] params) {
/* 266 */     return (IPriceMarkerChartObject)create(IPriceMarkerChartObject.class, params);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public IPolyLineChartObject createPolyLine(Object[] params) {
/* 272 */     return (IPolyLineChartObject)create(IPolyLineChartObject.class, params);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public IChannelChartObject createChannel(Object[] params) {
/* 278 */     return (IChannelChartObject)create(IChannelChartObject.class, params);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public ICyclesChartObject createCycles(Object[] params) {
/* 284 */     return (ICyclesChartObject)create(ICyclesChartObject.class, params);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public IPercentChartObject createPercent(Object[] params) {
/* 290 */     return (IPercentChartObject)create(IPercentChartObject.class, params);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public IFiboArcChartObject createFiboArc(Object[] params) {
/* 296 */     return (IFiboArcChartObject)create(IFiboArcChartObject.class, params);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public IFiboFanChartObject createFiboFan(Object[] params) {
/* 302 */     return (IFiboFanChartObject)create(IFiboFanChartObject.class, params);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public IFiboRetracementChartObject createFiboRetracement(Object[] params) {
/* 308 */     return (IFiboRetracementChartObject)create(IFiboRetracementChartObject.class, params);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public IFiboTimeZonesChartObject createFiboTimeZones(Object[] params) {
/* 314 */     return (IFiboTimeZonesChartObject)create(IFiboTimeZonesChartObject.class, params);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public IFiboExpansionChartObject createFiboExpansion(Object[] params) {
/* 320 */     return (IFiboExpansionChartObject)create(IFiboExpansionChartObject.class, params);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public IAndrewsPitchforkChartObject createAndrewsPitchfork(Object[] params) {
/* 326 */     return (IAndrewsPitchforkChartObject)create(IAndrewsPitchforkChartObject.class, params);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public IOrderLineChartObject createOrderLine(Object[] params) {
/* 332 */     return (IOrderLineChartObject)create(IOrderLineChartObject.class, params);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public ILabelChartObject createLabel(Object[] params) {
/* 338 */     return (ILabelChartObject)create(ILabelChartObject.class, params);
/*     */   }
/*     */ 
/*     */   public IOhlcChartObject createOhlcInformer()
/*     */   {
/* 346 */     return (IOhlcChartObject)create(IOhlcChartObject.class);
/*     */   }
/*     */ 
/*     */   public IOhlcChartObject createOhlcInformer(String key)
/*     */   {
/* 351 */     return (IOhlcChartObject)create(IOhlcChartObject.class, key);
/*     */   }
/*     */ 
/*     */   public static Map<Class<? extends IChartObject>, Class<?>> getTYPE_OBJECT_MAP()
/*     */   {
/* 356 */     return TYPE_OBJECT_MAP;
/*     */   }
/*     */ 
/*     */   public IGannAnglesChartObject createGannAnglesChartObject()
/*     */   {
/* 361 */     return (IGannAnglesChartObject)create(IGannAnglesChartObject.class);
/*     */   }
/*     */ 
/*     */   public IGannAnglesChartObject createGannAnglesChartObject(String key)
/*     */   {
/* 366 */     return (IGannAnglesChartObject)create(IGannAnglesChartObject.class, key);
/*     */   }
/*     */ 
/*     */   public IGannGridChartObject createGannGridChartObject()
/*     */   {
/* 371 */     return (IGannGridChartObject)create(IGannGridChartObject.class);
/*     */   }
/*     */ 
/*     */   public IGannGridChartObject createGannGridChartObject(String key)
/*     */   {
/* 376 */     return (IGannGridChartObject)create(IGannGridChartObject.class, key);
/*     */   }
/*     */ 
/*     */   public ILabelChartObject createLabel()
/*     */   {
/* 381 */     return (ILabelChartObject)create(ILabelChartObject.class);
/*     */   }
/*     */ 
/*     */   public ILabelChartObject createLabel(String key)
/*     */   {
/* 386 */     return (ILabelChartObject)create(ILabelChartObject.class, key);
/*     */   }
/*     */ 
/*     */   public IRectangleChartObject createRectangle()
/*     */   {
/* 391 */     return (IRectangleChartObject)create(IRectangleChartObject.class);
/*     */   }
/*     */ 
/*     */   public IRectangleChartObject createRectangle(String key)
/*     */   {
/* 396 */     return (IRectangleChartObject)create(IRectangleChartObject.class, key);
/*     */   }
/*     */ 
/*     */   public IEllipseChartObject createEllipse()
/*     */   {
/* 401 */     return (IEllipseChartObject)create(IEllipseChartObject.class);
/*     */   }
/*     */ 
/*     */   public IEllipseChartObject createEllipse(String key)
/*     */   {
/* 406 */     return (IEllipseChartObject)create(IEllipseChartObject.class, key);
/*     */   }
/*     */ 
/*     */   public ITriangleChartObject createTriangle()
/*     */   {
/* 411 */     return (ITriangleChartObject)create(ITriangleChartObject.class);
/*     */   }
/*     */ 
/*     */   public ITriangleChartObject createTriangle(String key)
/*     */   {
/* 416 */     return (ITriangleChartObject)create(ITriangleChartObject.class, key);
/*     */   }
/*     */ 
/*     */   public IHorizontalLineChartObject createHorizontalLine()
/*     */   {
/* 421 */     return (IHorizontalLineChartObject)create(IHorizontalLineChartObject.class);
/*     */   }
/*     */ 
/*     */   public IHorizontalLineChartObject createHorizontalLine(String key)
/*     */   {
/* 426 */     return (IHorizontalLineChartObject)create(IHorizontalLineChartObject.class, key);
/*     */   }
/*     */ 
/*     */   public IVerticalLineChartObject createVerticalLine()
/*     */   {
/* 431 */     return (IVerticalLineChartObject)create(IVerticalLineChartObject.class);
/*     */   }
/*     */ 
/*     */   public IVerticalLineChartObject createVerticalLine(String key)
/*     */   {
/* 436 */     return (IVerticalLineChartObject)create(IVerticalLineChartObject.class, key);
/*     */   }
/*     */ 
/*     */   public ILongLineChartObject createLongLine()
/*     */   {
/* 441 */     return (ILongLineChartObject)create(ILongLineChartObject.class);
/*     */   }
/*     */ 
/*     */   public ILongLineChartObject createLongLine(String key)
/*     */   {
/* 446 */     return (ILongLineChartObject)create(ILongLineChartObject.class, key);
/*     */   }
/*     */ 
/*     */   public ITextChartObject createText()
/*     */   {
/* 451 */     return (ITextChartObject)create(ITextChartObject.class);
/*     */   }
/*     */ 
/*     */   public ITextChartObject createText(String key)
/*     */   {
/* 456 */     return (ITextChartObject)create(ITextChartObject.class, key);
/*     */   }
/*     */ 
/*     */   public ISignalUpChartObject createSignalUp()
/*     */   {
/* 461 */     return (ISignalUpChartObject)create(ISignalUpChartObject.class);
/*     */   }
/*     */ 
/*     */   public ISignalUpChartObject createSignalUp(String key)
/*     */   {
/* 466 */     return (ISignalUpChartObject)create(ISignalUpChartObject.class, key);
/*     */   }
/*     */ 
/*     */   public ISignalDownChartObject createSignalDown()
/*     */   {
/* 471 */     return (ISignalDownChartObject)create(ISignalDownChartObject.class);
/*     */   }
/*     */ 
/*     */   public ISignalDownChartObject createSignalDown(String key)
/*     */   {
/* 476 */     return (ISignalDownChartObject)create(ISignalDownChartObject.class, key);
/*     */   }
/*     */ 
/*     */   public IShortLineChartObject createShortLine()
/*     */   {
/* 481 */     return (IShortLineChartObject)create(IShortLineChartObject.class);
/*     */   }
/*     */ 
/*     */   public IShortLineChartObject createShortLine(String key)
/*     */   {
/* 486 */     return (IShortLineChartObject)create(IShortLineChartObject.class, key);
/*     */   }
/*     */ 
/*     */   public IRayLineChartObject createRayLine()
/*     */   {
/* 491 */     return (IRayLineChartObject)create(IRayLineChartObject.class);
/*     */   }
/*     */ 
/*     */   public IRayLineChartObject createRayLine(String key)
/*     */   {
/* 496 */     return (IRayLineChartObject)create(IRayLineChartObject.class, key);
/*     */   }
/*     */ 
/*     */   public ITimeMarkerChartObject createTimeMarker()
/*     */   {
/* 501 */     return (ITimeMarkerChartObject)create(ITimeMarkerChartObject.class);
/*     */   }
/*     */ 
/*     */   public ITimeMarkerChartObject createTimeMarker(String key)
/*     */   {
/* 506 */     return (ITimeMarkerChartObject)create(ITimeMarkerChartObject.class, key);
/*     */   }
/*     */ 
/*     */   public IPriceMarkerChartObject createPriceMarker()
/*     */   {
/* 511 */     return (IPriceMarkerChartObject)create(IPriceMarkerChartObject.class);
/*     */   }
/*     */ 
/*     */   public IPriceMarkerChartObject createPriceMarker(String key)
/*     */   {
/* 516 */     return (IPriceMarkerChartObject)create(IPriceMarkerChartObject.class, key);
/*     */   }
/*     */ 
/*     */   public IPolyLineChartObject createPolyLine()
/*     */   {
/* 521 */     return (IPolyLineChartObject)create(IPolyLineChartObject.class);
/*     */   }
/*     */ 
/*     */   public IPolyLineChartObject createPolyLine(String key)
/*     */   {
/* 526 */     return (IPolyLineChartObject)create(IPolyLineChartObject.class, key);
/*     */   }
/*     */ 
/*     */   public IChannelChartObject createChannel()
/*     */   {
/* 531 */     return (IChannelChartObject)create(IChannelChartObject.class);
/*     */   }
/*     */ 
/*     */   public IChannelChartObject createChannel(String key)
/*     */   {
/* 536 */     return (IChannelChartObject)create(IChannelChartObject.class, key);
/*     */   }
/*     */ 
/*     */   public ICyclesChartObject createCycles()
/*     */   {
/* 541 */     return (ICyclesChartObject)create(ICyclesChartObject.class);
/*     */   }
/*     */ 
/*     */   public ICyclesChartObject createCycles(String key)
/*     */   {
/* 546 */     return (ICyclesChartObject)create(ICyclesChartObject.class, key);
/*     */   }
/*     */ 
/*     */   public IPercentChartObject createPercent()
/*     */   {
/* 551 */     return (IPercentChartObject)create(IPercentChartObject.class);
/*     */   }
/*     */ 
/*     */   public IPercentChartObject createPercent(String key)
/*     */   {
/* 556 */     return (IPercentChartObject)create(IPercentChartObject.class, key);
/*     */   }
/*     */ 
/*     */   public IFiboArcChartObject createFiboArc()
/*     */   {
/* 561 */     return (IFiboArcChartObject)create(IFiboArcChartObject.class);
/*     */   }
/*     */ 
/*     */   public IFiboArcChartObject createFiboArc(String key)
/*     */   {
/* 566 */     return (IFiboArcChartObject)create(IFiboArcChartObject.class, key);
/*     */   }
/*     */ 
/*     */   public IFiboFanChartObject createFiboFan()
/*     */   {
/* 571 */     return (IFiboFanChartObject)create(IFiboFanChartObject.class);
/*     */   }
/*     */ 
/*     */   public IFiboFanChartObject createFiboFan(String key)
/*     */   {
/* 576 */     return (IFiboFanChartObject)create(IFiboFanChartObject.class, key);
/*     */   }
/*     */ 
/*     */   public IFiboRetracementChartObject createFiboRetracement()
/*     */   {
/* 581 */     return (IFiboRetracementChartObject)create(IFiboRetracementChartObject.class);
/*     */   }
/*     */ 
/*     */   public IFiboRetracementChartObject createFiboRetracement(String key)
/*     */   {
/* 586 */     return (IFiboRetracementChartObject)create(IFiboRetracementChartObject.class, key);
/*     */   }
/*     */ 
/*     */   public IFiboTimeZonesChartObject createFiboTimeZones()
/*     */   {
/* 591 */     return (IFiboTimeZonesChartObject)create(IFiboTimeZonesChartObject.class);
/*     */   }
/*     */ 
/*     */   public IFiboTimeZonesChartObject createFiboTimeZones(String key)
/*     */   {
/* 596 */     return (IFiboTimeZonesChartObject)create(IFiboTimeZonesChartObject.class, key);
/*     */   }
/*     */ 
/*     */   public IFiboExpansionChartObject createFiboExpansion()
/*     */   {
/* 601 */     return (IFiboExpansionChartObject)create(IFiboExpansionChartObject.class);
/*     */   }
/*     */ 
/*     */   public IFiboExpansionChartObject createFiboExpansion(String key)
/*     */   {
/* 606 */     return (IFiboExpansionChartObject)create(IFiboExpansionChartObject.class, key);
/*     */   }
/*     */ 
/*     */   public IAndrewsPitchforkChartObject createAndrewsPitchfork()
/*     */   {
/* 611 */     return (IAndrewsPitchforkChartObject)create(IAndrewsPitchforkChartObject.class);
/*     */   }
/*     */ 
/*     */   public IAndrewsPitchforkChartObject createAndrewsPitchfork(String key)
/*     */   {
/* 616 */     return (IAndrewsPitchforkChartObject)create(IAndrewsPitchforkChartObject.class, key);
/*     */   }
/*     */ 
/*     */   public IOrderLineChartObject createOrderLine()
/*     */   {
/* 621 */     return (IOrderLineChartObject)create(IOrderLineChartObject.class);
/*     */   }
/*     */ 
/*     */   public IOrderLineChartObject createOrderLine(String key)
/*     */   {
/* 626 */     return (IOrderLineChartObject)create(IOrderLineChartObject.class, key);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  79 */     PARAMETER_TYPES.put(Byte.class, Byte.TYPE);
/*  80 */     PARAMETER_TYPES.put(Character.class, Character.TYPE);
/*  81 */     PARAMETER_TYPES.put(Short.class, Short.TYPE);
/*  82 */     PARAMETER_TYPES.put(Integer.class, Integer.TYPE);
/*  83 */     PARAMETER_TYPES.put(Long.class, Long.TYPE);
/*  84 */     PARAMETER_TYPES.put(Float.class, Float.TYPE);
/*  85 */     PARAMETER_TYPES.put(Double.class, Double.TYPE);
/*  86 */     PARAMETER_TYPES.put(Boolean.class, Boolean.TYPE);
/*  87 */     PARAMETER_TYPES.put(String.class, String.class);
/*  88 */     PARAMETER_TYPES = Collections.unmodifiableMap(PARAMETER_TYPES);
/*     */ 
/*  92 */     TYPE_OBJECT_MAP.put(IEllipseChartObject.class, EllipseChartObject.class);
/*  93 */     TYPE_OBJECT_MAP.put(ITriangleChartObject.class, TriangleChartObject.class);
/*  94 */     TYPE_OBJECT_MAP.put(IRectangleChartObject.class, RectangleChartObject.class);
/*  95 */     TYPE_OBJECT_MAP.put(IHorizontalLineChartObject.class, HLineChartObject.class);
/*  96 */     TYPE_OBJECT_MAP.put(IVerticalLineChartObject.class, VLineChartObject.class);
/*  97 */     TYPE_OBJECT_MAP.put(ILongLineChartObject.class, LongLineChartObject.class);
/*  98 */     TYPE_OBJECT_MAP.put(ITextChartObject.class, TextChartObject.class);
/*  99 */     TYPE_OBJECT_MAP.put(ISignalUpChartObject.class, SignalUpChartObject.class);
/* 100 */     TYPE_OBJECT_MAP.put(ISignalDownChartObject.class, SignalDownChartObject.class);
/* 101 */     TYPE_OBJECT_MAP.put(IShortLineChartObject.class, ShortLineChartObject.class);
/* 102 */     TYPE_OBJECT_MAP.put(IRayLineChartObject.class, RayLineChartObject.class);
/* 103 */     TYPE_OBJECT_MAP.put(ITimeMarkerChartObject.class, TimeMarkerChartObject.class);
/* 104 */     TYPE_OBJECT_MAP.put(IPriceMarkerChartObject.class, PriceMarkerChartObject.class);
/* 105 */     TYPE_OBJECT_MAP.put(IPolyLineChartObject.class, PolyLineChartObject.class);
/* 106 */     TYPE_OBJECT_MAP.put(IChannelChartObject.class, ChannelChartObject.class);
/* 107 */     TYPE_OBJECT_MAP.put(ICyclesChartObject.class, CyclesChartObject.class);
/* 108 */     TYPE_OBJECT_MAP.put(IPercentChartObject.class, PercentChartObject.class);
/* 109 */     TYPE_OBJECT_MAP.put(IFiboArcChartObject.class, FiboArcChartObject.class);
/* 110 */     TYPE_OBJECT_MAP.put(IFiboFanChartObject.class, FiboFanChartObject.class);
/* 111 */     TYPE_OBJECT_MAP.put(IFiboRetracementChartObject.class, FiboRetracementChartObject.class);
/* 112 */     TYPE_OBJECT_MAP.put(IFiboTimeZonesChartObject.class, FiboTimeZonesChartObject.class);
/* 113 */     TYPE_OBJECT_MAP.put(IFiboExpansionChartObject.class, FiboExpansionChartObject.class);
/* 114 */     TYPE_OBJECT_MAP.put(IAndrewsPitchforkChartObject.class, AndrewsPitchforkChartObject.class);
/* 115 */     TYPE_OBJECT_MAP.put(IOrderLineChartObject.class, OrderLineChartObject.class);
/* 116 */     TYPE_OBJECT_MAP.put(ILabelChartObject.class, LabelChartObject.class);
/* 117 */     TYPE_OBJECT_MAP.put(IOhlcChartObject.class, OhlcChartObject.class);
/* 118 */     TYPE_OBJECT_MAP.put(IGannAnglesChartObject.class, GannAnglesChartObject.class);
/* 119 */     TYPE_OBJECT_MAP.put(IGannGridChartObject.class, GannGridChartObject.class);
/*     */ 
/* 121 */     TYPE_OBJECT_MAP = Collections.unmodifiableMap(TYPE_OBJECT_MAP);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.chartbuilder.ChartObjectFactory
 * JD-Core Version:    0.6.0
 */