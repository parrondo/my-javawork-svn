/*     */ package com.dukascopy.charts.math.indicators;
/*     */ 
/*     */ import com.dukascopy.api.JFException;
/*     */ import com.dukascopy.api.impl.CustIndicatorWrapper;
/*     */ import com.dukascopy.api.impl.IndicatorContext;
/*     */ import com.dukascopy.api.impl.IndicatorHolder;
/*     */ import com.dukascopy.api.impl.StrategyWrapper;
/*     */ import com.dukascopy.api.impl.TaLibIndicator;
/*     */ import com.dukascopy.api.impl.TaLibMetaData;
/*     */ import com.dukascopy.api.impl.talib.FuncInfoHolder;
/*     */ import com.dukascopy.api.impl.talib.TaGrpService;
/*     */ import com.dukascopy.api.indicators.BooleanOptInputDescription;
/*     */ import com.dukascopy.api.indicators.DoubleListDescription;
/*     */ import com.dukascopy.api.indicators.DoubleRangeDescription;
/*     */ import com.dukascopy.api.indicators.IDrawingIndicator;
/*     */ import com.dukascopy.api.indicators.IIndicator;
/*     */ import com.dukascopy.api.indicators.IIndicatorDrawingSupport;
/*     */ import com.dukascopy.api.indicators.IIndicatorsProvider;
/*     */ import com.dukascopy.api.indicators.IMinMax;
/*     */ import com.dukascopy.api.indicators.IndicatorInfo;
/*     */ import com.dukascopy.api.indicators.InputParameterInfo;
/*     */ import com.dukascopy.api.indicators.IntegerListDescription;
/*     */ import com.dukascopy.api.indicators.IntegerRangeDescription;
/*     */ import com.dukascopy.api.indicators.OptInputDescription;
/*     */ import com.dukascopy.api.indicators.OptInputParameterInfo;
/*     */ import com.dukascopy.api.indicators.OutputParameterInfo;
/*     */ import com.dukascopy.api.indicators.OutputParameterInfo.DrawingStyle;
/*     */ import com.dukascopy.api.indicators.OutputParameterInfo.Type;
/*     */ import com.dukascopy.charts.listener.CustomIndicatorsActionListener;
/*     */ import com.dukascopy.charts.persistence.EnabledIndicatorBean;
/*     */ import com.dukascopy.charts.persistence.SettingsStorage;
/*     */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*     */ import com.dukascopy.dds2.greed.util.IndicatorHelper;
/*     */ import com.dukascopy.dds2.greed.util.NotificationUtilsProvider;
/*     */ import com.dukascopy.indicators.ACIndicator;
/*     */ import com.dukascopy.indicators.APOscillator;
/*     */ import com.dukascopy.indicators.AlligatorIndicator;
/*     */ import com.dukascopy.indicators.AwesomeOscillator;
/*     */ import com.dukascopy.indicators.BWMFIIndicator;
/*     */ import com.dukascopy.indicators.BearPowerIndicator;
/*     */ import com.dukascopy.indicators.BollingerBands;
/*     */ import com.dukascopy.indicators.BullsPowerIndicator;
/*     */ import com.dukascopy.indicators.ButterworthFilterIndicator;
/*     */ import com.dukascopy.indicators.CamarillaPivotIndicator;
/*     */ import com.dukascopy.indicators.CenterOfGravityIndicator;
/*     */ import com.dukascopy.indicators.CustomCandleIndicator;
/*     */ import com.dukascopy.indicators.DMIOscillator;
/*     */ import com.dukascopy.indicators.DonchianChannel;
/*     */ import com.dukascopy.indicators.EMAEnvelopesIndicator;
/*     */ import com.dukascopy.indicators.EnvelopesIndicator;
/*     */ import com.dukascopy.indicators.FibonacciPivotIndicator;
/*     */ import com.dukascopy.indicators.ForceIndicator;
/*     */ import com.dukascopy.indicators.FractalIndicator;
/*     */ import com.dukascopy.indicators.FractalLinesIndicator;
/*     */ import com.dukascopy.indicators.GatorIndicator;
/*     */ import com.dukascopy.indicators.HMAIndicator;
/*     */ import com.dukascopy.indicators.HeikinAshiIndicator;
/*     */ import com.dukascopy.indicators.IchimokuIndicator;
/*     */ import com.dukascopy.indicators.KairiIndicator;
/*     */ import com.dukascopy.indicators.KeltnerChannel;
/*     */ import com.dukascopy.indicators.LWMAIndicator;
/*     */ import com.dukascopy.indicators.LaguerreACS1;
/*     */ import com.dukascopy.indicators.MACDEXTIndicator;
/*     */ import com.dukascopy.indicators.MAIndicator;
/*     */ import com.dukascopy.indicators.MAVPIndicator;
/*     */ import com.dukascopy.indicators.MAXIndicator;
/*     */ import com.dukascopy.indicators.MINIndicator;
/*     */ import com.dukascopy.indicators.MurreyChannelsIndicator;
/*     */ import com.dukascopy.indicators.OsMAIndicator;
/*     */ import com.dukascopy.indicators.PPOscillator;
/*     */ import com.dukascopy.indicators.PercentBollingerBands;
/*     */ import com.dukascopy.indicators.PivotIndicator;
/*     */ import com.dukascopy.indicators.PriceChannelIndicator;
/*     */ import com.dukascopy.indicators.RCIIndicator;
/*     */ import com.dukascopy.indicators.RMIIndicator;
/*     */ import com.dukascopy.indicators.RSIIndicator;
/*     */ import com.dukascopy.indicators.RelativeVigorIndicator;
/*     */ import com.dukascopy.indicators.SMIIndicator;
/*     */ import com.dukascopy.indicators.SMMAIndicator;
/*     */ import com.dukascopy.indicators.StochasticFastIndicator;
/*     */ import com.dukascopy.indicators.StochasticIndicator;
/*     */ import com.dukascopy.indicators.StochasticRSIIndicator;
/*     */ import com.dukascopy.indicators.SupportResistanceIndicator;
/*     */ import com.dukascopy.indicators.TD_IIndicator;
/*     */ import com.dukascopy.indicators.TD_Sequential;
/*     */ import com.dukascopy.indicators.TimeSegmentedVolumeIndicator;
/*     */ import com.dukascopy.indicators.TrendEnvelopesIndicator;
/*     */ import com.dukascopy.indicators.VolumeEXTIndicator;
/*     */ import com.dukascopy.indicators.VolumeIndicator;
/*     */ import com.dukascopy.indicators.VolumeWAP;
/*     */ import com.dukascopy.indicators.WSMTimeIndicator;
/*     */ import com.dukascopy.indicators.WaddahAttarTrend;
/*     */ import com.dukascopy.indicators.WallabyIndicator;
/*     */ import com.dukascopy.indicators.WoodiePivotIndicator;
/*     */ import com.dukascopy.indicators.ZigZagIndicator;
/*     */ import com.dukascopy.indicators.pattern.AscendingTrianglePatternIndicator;
/*     */ import com.dukascopy.indicators.pattern.ChannelDownPatternIndicator;
/*     */ import com.dukascopy.indicators.pattern.ChannelUpPatternIndicator;
/*     */ import com.dukascopy.indicators.pattern.DescendingTrianglePatternIndicator;
/*     */ import com.dukascopy.indicators.pattern.DoubleBottomPatternIndicator;
/*     */ import com.dukascopy.indicators.pattern.DoubleTopPatternIndicator;
/*     */ import com.dukascopy.indicators.pattern.FallingWedgePatternIndicator;
/*     */ import com.dukascopy.indicators.pattern.HeadAndShouldersPatternIndicator;
/*     */ import com.dukascopy.indicators.pattern.InverseHeadAndShouldersPatternIndicator;
/*     */ import com.dukascopy.indicators.pattern.InverseRectanglePatternIndicator;
/*     */ import com.dukascopy.indicators.pattern.PennantPatternIndicator;
/*     */ import com.dukascopy.indicators.pattern.RectanglePatternIndicator;
/*     */ import com.dukascopy.indicators.pattern.RisingWedgePatternIndicator;
/*     */ import com.dukascopy.indicators.pattern.TrianglePatternIndicator;
/*     */ import com.dukascopy.indicators.pattern.TripleBottomPatternIndicator;
/*     */ import com.dukascopy.indicators.pattern.TripleTopPatternIndicator;
/*     */ import com.dukascopy.indicators.patterns.MorningDojiStarPattern;
/*     */ import com.dukascopy.indicators.patterns.ThrustBarPattern;
/*     */ import com.dukascopy.indicators.patterns.ThrustOutsideBarPattern;
/*     */ import java.awt.Color;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Stroke;
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class IndicatorsProvider
/*     */   implements IIndicatorsProvider
/*     */ {
/* 133 */   private static final Logger LOGGER = LoggerFactory.getLogger(IndicatorsProvider.class);
/*     */   private static IndicatorsProvider indicatorsProvider;
/* 136 */   private volatile List<CustomIndicatorsActionListener> customIndicatorsActionListeners = new ArrayList();
/*     */   private SettingsStorage settingsStorage;
/* 140 */   private Collection<String> taLibIndicatorGroups = new HashSet();
/* 141 */   private Collection<String> dukascopyIndicatorGroups = new HashSet();
/* 142 */   private Collection<String> customIndicatorGroups = new HashSet();
/* 143 */   private Map<String, Set<String>> groupToTaLibIndicators = new HashMap();
/* 144 */   private Map<String, Set<String>> groupToDukascopyIndicators = new HashMap();
/* 145 */   private Map<String, Set<String>> groupToCustomIndicators = new HashMap();
/* 146 */   private Set<String> taLibIndicatorNames = new HashSet();
/* 147 */   private Set<String> dukascopyIndicatorNames = new HashSet();
/* 148 */   private Set<String> customIndicatorNames = new HashSet();
/* 149 */   private Map<String, String> dukascopyIndicatorTitles = new HashMap();
/* 150 */   private Map<String, String> customIndicatorTitles = new HashMap();
/*     */ 
/* 152 */   private Map<String, CustIndicatorWrapper> nameToCustIndWrapper = new HashMap();
/* 153 */   private Map<File, String> binFileToCustIndName = new HashMap();
/*     */ 
/* 155 */   private Map<String, Class<? extends IIndicator>> nameToDukaIndClass = new HashMap();
/*     */ 
/* 157 */   private Map<IIndicator, IndicatorHolder> indicatorHolders = new HashMap();
/*     */ 
/*     */   public static void createInstance(SettingsStorage settingsStorage) {
/* 160 */     indicatorsProvider = new IndicatorsProvider(settingsStorage);
/* 161 */     indicatorsProvider.init();
/*     */   }
/*     */ 
/*     */   private IndicatorsProvider(SettingsStorage settingsStorage) {
/* 165 */     this.settingsStorage = settingsStorage;
/*     */   }
/*     */ 
/*     */   public static IndicatorsProvider getInstance() {
/* 169 */     return indicatorsProvider;
/*     */   }
/*     */ 
/*     */   private void init() {
/*     */     try {
/* 174 */       TaLibMetaData.forEachGrp(new TaGrpService() {
/*     */         public void execute(String group, Set<TaLibMetaData> functions) throws Exception {
/* 176 */           IndicatorsProvider.this.taLibIndicatorGroups.add(group);
/* 177 */           Set indicators = new HashSet();
/* 178 */           for (TaLibMetaData coreMetaData : functions) {
/* 179 */             String name = coreMetaData.getFuncInfo().name;
/* 180 */             indicators.add(name);
/* 181 */             IndicatorsProvider.this.taLibIndicatorNames.add(name);
/*     */           }
/* 183 */           IndicatorsProvider.this.groupToTaLibIndicators.put(group, indicators);
/*     */         }
/*     */       });
/* 187 */       performRegistration(new TD_IIndicator());
/* 188 */       performRegistration(new VolumeIndicator());
/* 189 */       performRegistration(new TD_Sequential());
/*     */ 
/* 191 */       performRegistration(new AwesomeOscillator());
/* 192 */       performRegistration(new WaddahAttarTrend());
/* 193 */       performRegistration(new VolumeWAP());
/*     */ 
/* 195 */       performRegistration(new SMMAIndicator());
/* 196 */       performRegistration(new AlligatorIndicator());
/* 197 */       performRegistration(new FractalIndicator());
/*     */ 
/* 199 */       performRegistration(new SupportResistanceIndicator());
/* 200 */       performRegistration(new RelativeVigorIndicator());
/* 201 */       performRegistration(new ZigZagIndicator());
/*     */ 
/* 203 */       performRegistration(new HeikinAshiIndicator());
/* 204 */       performRegistration(new ButterworthFilterIndicator());
/* 205 */       performRegistration(new EnvelopesIndicator());
/* 206 */       performRegistration(new EMAEnvelopesIndicator());
/*     */ 
/* 208 */       performRegistration(new IchimokuIndicator());
/* 209 */       performRegistration(new TimeSegmentedVolumeIndicator());
/* 210 */       performRegistration(new PivotIndicator());
/*     */ 
/* 212 */       performRegistration(new PriceChannelIndicator());
/* 213 */       performRegistration(new CamarillaPivotIndicator());
/* 214 */       performRegistration(new BWMFIIndicator());
/*     */ 
/* 216 */       performRegistration(new BullsPowerIndicator());
/* 217 */       performRegistration(new BearPowerIndicator());
/* 218 */       performRegistration(new ForceIndicator());
/*     */ 
/* 220 */       performRegistration(new GatorIndicator());
/* 221 */       performRegistration(new OsMAIndicator());
/* 222 */       performRegistration(new ACIndicator());
/* 223 */       performRegistration(new DMIOscillator());
/*     */ 
/* 225 */       performRegistration(new KeltnerChannel());
/* 226 */       performRegistration(new WoodiePivotIndicator());
/* 227 */       performRegistration(new FibonacciPivotIndicator());
/* 228 */       performRegistration(new MurreyChannelsIndicator());
/*     */ 
/* 230 */       performRegistration(new LaguerreACS1());
/* 231 */       performRegistration(new WSMTimeIndicator());
/* 232 */       performRegistration(new DonchianChannel());
/* 233 */       performRegistration(new KairiIndicator());
/* 234 */       performRegistration(new CenterOfGravityIndicator());
/*     */ 
/* 236 */       performRegistration(new VolumeEXTIndicator());
/*     */ 
/* 238 */       performRegistration(new MAIndicator());
/* 239 */       performRegistration(new StochasticRSIIndicator());
/* 240 */       performRegistration(new StochasticFastIndicator());
/* 241 */       performRegistration(new StochasticIndicator());
/* 242 */       performRegistration(new APOscillator());
/* 243 */       performRegistration(new PPOscillator());
/* 244 */       performRegistration(new MAVPIndicator());
/* 245 */       performRegistration(new MACDEXTIndicator());
/* 246 */       performRegistration(new BollingerBands());
/* 247 */       performRegistration(new RMIIndicator());
/* 248 */       performRegistration(new RSIIndicator());
/* 249 */       performRegistration(new MINIndicator());
/* 250 */       performRegistration(new MAXIndicator());
/* 251 */       performRegistration(new PercentBollingerBands());
/* 252 */       performRegistration(new RCIIndicator());
/* 253 */       performRegistration(new HMAIndicator());
/* 254 */       performRegistration(new FractalLinesIndicator());
/* 255 */       performRegistration(new WallabyIndicator());
/* 256 */       performRegistration(new SMIIndicator());
/* 257 */       performRegistration(new CustomCandleIndicator());
/*     */ 
/* 260 */       performRegistration(new MorningDojiStarPattern());
/* 261 */       performRegistration(new ThrustBarPattern());
/* 262 */       performRegistration(new ThrustOutsideBarPattern());
/* 263 */       performRegistration(new HeadAndShouldersPatternIndicator());
/* 264 */       performRegistration(new InverseHeadAndShouldersPatternIndicator());
/* 265 */       performRegistration(new DoubleTopPatternIndicator());
/*     */ 
/* 267 */       performRegistration(new DoubleBottomPatternIndicator());
/* 268 */       performRegistration(new RectanglePatternIndicator());
/* 269 */       performRegistration(new InverseRectanglePatternIndicator());
/*     */ 
/* 271 */       performRegistration(new TripleTopPatternIndicator());
/* 272 */       performRegistration(new TripleBottomPatternIndicator());
/*     */ 
/* 274 */       performRegistration(new TrianglePatternIndicator());
/* 275 */       performRegistration(new AscendingTrianglePatternIndicator());
/* 276 */       performRegistration(new DescendingTrianglePatternIndicator());
/*     */ 
/* 278 */       performRegistration(new PennantPatternIndicator());
/* 279 */       performRegistration(new FallingWedgePatternIndicator());
/* 280 */       performRegistration(new RisingWedgePatternIndicator());
/*     */ 
/* 282 */       performRegistration(new ChannelUpPatternIndicator());
/* 283 */       performRegistration(new ChannelDownPatternIndicator());
/*     */ 
/* 285 */       performRegistration(new LWMAIndicator());
/* 286 */       performRegistration(new TrendEnvelopesIndicator());
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 290 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void performRegistration(IIndicator indicator) {
/* 295 */     registerIndicator(indicator, NotificationUtilsProvider.getNotificationUtils(), true);
/* 296 */     this.nameToDukaIndClass.put(indicator.getIndicatorInfo().getName().toUpperCase(), indicator.getClass());
/*     */   }
/*     */ 
/*     */   public void registerIndicatorsFromPrefs() {
/* 300 */     if (this.settingsStorage == null) {
/* 301 */       return;
/*     */     }
/* 303 */     List enabledIndicators = this.settingsStorage.getEnabledIndicators();
/* 304 */     for (EnabledIndicatorBean enabledIndicator : enabledIndicators)
/*     */       try {
/* 306 */         String fileName = enabledIndicator.getBinaryFullFileName();
/* 307 */         if (fileName != null) {
/* 308 */           File binFile = new File(fileName);
/*     */ 
/* 310 */           if (binFile.exists()) {
/* 311 */             CustIndicatorWrapper indicatorWrapper = new CustIndicatorWrapper();
/* 312 */             indicatorWrapper.setBinaryFile(binFile);
/* 313 */             if (enableIndicator(indicatorWrapper, NotificationUtilsProvider.getNotificationUtils()) == null)
/*     */             {
/* 315 */               this.settingsStorage.removeEnabledIndicator(enabledIndicator);
/*     */             }
/*     */           }
/*     */         }
/*     */       } catch (Exception e) {
/* 320 */         LOGGER.error(e.getMessage(), e);
/*     */       }
/*     */   }
/*     */ 
/*     */   public boolean isEnabledOnCharts(String indicatorName)
/*     */   {
/* 326 */     indicatorName = indicatorName.toUpperCase();
/* 327 */     return (!this.taLibIndicatorNames.contains(indicatorName)) || (IndicatorsFilter.getTitle(indicatorName) != null);
/*     */   }
/*     */ 
/*     */   public Collection<String> getAllNames() {
/* 331 */     Collection indicatorNames = new HashSet(this.taLibIndicatorNames.size() + this.dukascopyIndicatorNames.size() + this.customIndicatorNames.size());
/* 332 */     indicatorNames.addAll(this.taLibIndicatorNames);
/* 333 */     indicatorNames.addAll(this.dukascopyIndicatorNames);
/* 334 */     indicatorNames.addAll(this.customIndicatorNames);
/* 335 */     return indicatorNames;
/*     */   }
/*     */ 
/*     */   public Collection<String> getGroups() {
/* 339 */     Collection indicatorGroups = new HashSet(this.taLibIndicatorGroups.size() + this.dukascopyIndicatorGroups.size() + this.customIndicatorGroups.size());
/* 340 */     indicatorGroups.addAll(this.taLibIndicatorGroups);
/* 341 */     indicatorGroups.addAll(this.dukascopyIndicatorGroups);
/* 342 */     indicatorGroups.addAll(this.customIndicatorGroups);
/* 343 */     return indicatorGroups;
/*     */   }
/*     */ 
/*     */   public Collection<String> getCustomIndicatorGroups() {
/* 347 */     return Collections.unmodifiableCollection(this.customIndicatorGroups);
/*     */   }
/*     */ 
/*     */   public IIndicator getIndicator(String name) {
/* 351 */     IndicatorHolder indicatorHolder = getIndicatorHolder(name);
/* 352 */     if (indicatorHolder != null) {
/* 353 */       return getIndicatorHolder(name).getIndicator();
/*     */     }
/* 355 */     return null;
/*     */   }
/*     */ 
/*     */   public IndicatorHolder getIndicatorHolder(String name) {
/* 359 */     name = name.toUpperCase();
/* 360 */     if (this.dukascopyIndicatorNames.contains(name)) { Class clazz = (Class)this.nameToDukaIndClass.get(name);
/*     */       IIndicator indicator;
/*     */       try { indicator = (IIndicator)clazz.newInstance();
/*     */       } catch (Exception e) {
/* 366 */         LOGGER.error(e.getMessage(), e);
/* 367 */         return null;
/*     */       }
/* 369 */       IndicatorContext indicatorContext = IndicatorHelper.createIndicatorContext();
/*     */       try {
/* 371 */         indicator.onStart(indicatorContext);
/*     */       }
/*     */       catch (AbstractMethodError e)
/*     */       {
/*     */       }
/* 376 */       return new IndicatorHolder(indicator, indicatorContext); }
/* 377 */     if (this.taLibIndicatorNames.contains(name))
/*     */       try {
/* 379 */         return new IndicatorHolder(new TaLibIndicator(TaLibMetaData.getInstance(name)), IndicatorHelper.createIndicatorContext());
/*     */       } catch (Exception e) {
/* 381 */         LOGGER.error(e.getMessage(), e);
/* 382 */         return null;
/*     */       }
/* 384 */     if (this.customIndicatorNames.contains(name)) {
/* 385 */       CustIndicatorWrapper custIndicatorWrapper = (CustIndicatorWrapper)this.nameToCustIndWrapper.get(name);
/* 386 */       IIndicator indicator = custIndicatorWrapper.getIndicator();
/* 387 */       IndicatorContext indicatorContext = IndicatorHelper.createIndicatorContext();
/*     */       try {
/* 389 */         indicator.onStart(indicatorContext);
/*     */       }
/*     */       catch (AbstractMethodError e) {
/* 392 */         NotificationUtilsProvider.getNotificationUtils().postWarningMessage("onStart method not implemented in indicator, implement it and recompile indicator", true);
/*     */       }
/* 394 */       return new IndicatorHolder(indicator, indicatorContext);
/*     */     }
/* 396 */     return null;
/*     */   }
/*     */ 
/*     */   public void saveIndicatorHolder(IndicatorHolder indicatorHolder)
/*     */   {
/* 401 */     this.indicatorHolders.put(indicatorHolder.getIndicator(), indicatorHolder);
/*     */   }
/*     */ 
/*     */   public void removeSavedIndicatorHolder(IIndicator indicator) {
/* 405 */     this.indicatorHolders.remove(indicator);
/*     */   }
/*     */ 
/*     */   public IndicatorHolder getSavedIndicatorHolder(IIndicator indicator) {
/* 409 */     return (IndicatorHolder)this.indicatorHolders.get(indicator);
/*     */   }
/*     */ 
/*     */   public boolean isIndicatorRegistered(String name) {
/* 413 */     name = name.toUpperCase();
/* 414 */     return (this.taLibIndicatorNames.contains(name)) || (this.customIndicatorNames.contains(name)) || (this.dukascopyIndicatorNames.contains(name));
/*     */   }
/*     */ 
/*     */   public Collection<String> getNames(String groupName) {
/* 418 */     Collection taLibGroup = (Collection)this.groupToTaLibIndicators.get(groupName);
/* 419 */     Collection dukascopyIndicatorsGroup = (Collection)this.groupToDukascopyIndicators.get(groupName);
/* 420 */     Collection customIndicatorsGroup = (Collection)this.groupToCustomIndicators.get(groupName);
/* 421 */     if ((taLibGroup == null) && (dukascopyIndicatorsGroup == null) && (customIndicatorsGroup == null)) {
/* 422 */       return null;
/*     */     }
/* 424 */     Collection indicatorGroups = new HashSet();
/* 425 */     if (taLibGroup != null) {
/* 426 */       indicatorGroups.addAll(taLibGroup);
/*     */     }
/* 428 */     if (dukascopyIndicatorsGroup != null) {
/* 429 */       indicatorGroups.addAll(dukascopyIndicatorsGroup);
/*     */     }
/* 431 */     if (customIndicatorsGroup != null) {
/* 432 */       indicatorGroups.addAll(customIndicatorsGroup);
/*     */     }
/* 434 */     return indicatorGroups;
/*     */   }
/*     */ 
/*     */   public void registerUserIndicator(File compiledCustomIndcatorFile)
/*     */     throws JFException
/*     */   {
/* 441 */     if ((compiledCustomIndcatorFile == null) || (!compiledCustomIndcatorFile.canExecute())) {
/* 442 */       throw new JFException("Given file: " + compiledCustomIndcatorFile.getAbsolutePath() + " does not exists or can't be executed");
/*     */     }
/*     */ 
/* 445 */     CustIndicatorWrapper custIndicatorWrapper = new CustIndicatorWrapper();
/* 446 */     custIndicatorWrapper.setBinaryFile(compiledCustomIndcatorFile);
/*     */ 
/* 448 */     String indicatorName = enableIndicator(custIndicatorWrapper, NotificationUtilsProvider.getNotificationUtils());
/* 449 */     if (indicatorName == null)
/* 450 */       throw new JFException("Error while loading indicator file: " + compiledCustomIndcatorFile.getAbsolutePath());
/*     */   }
/*     */ 
/*     */   public Collection<String> getBuildInIndicatorNames()
/*     */   {
/* 455 */     Collection indicatorNames = new HashSet(this.taLibIndicatorNames.size() + this.dukascopyIndicatorNames.size() + this.customIndicatorNames.size());
/* 456 */     indicatorNames.addAll(this.taLibIndicatorNames);
/* 457 */     indicatorNames.addAll(this.dukascopyIndicatorNames);
/* 458 */     return indicatorNames;
/*     */   }
/*     */ 
/*     */   public Collection<String> getCustomIndictorNames() {
/* 462 */     Collection indicatorNames = new HashSet(this.customIndicatorNames.size());
/* 463 */     indicatorNames.addAll(this.customIndicatorNames);
/* 464 */     return indicatorNames;
/*     */   }
/*     */ 
/*     */   public String getTitle(String name) {
/* 468 */     name = name.toUpperCase();
/* 469 */     if (this.taLibIndicatorNames.contains(name))
/* 470 */       return IndicatorsFilter.getTitle(name);
/* 471 */     if (this.customIndicatorNames.contains(name))
/* 472 */       return (String)this.customIndicatorTitles.get(name);
/* 473 */     if (this.dukascopyIndicatorNames.contains(name)) {
/* 474 */       return (String)this.dukascopyIndicatorTitles.get(name);
/*     */     }
/* 476 */     return null;
/*     */   }
/*     */ 
/*     */   public String enableIndicator(CustIndicatorWrapper custIndicatorWrapper, INotificationUtils notificationUtils)
/*     */   {
/* 481 */     custIndicatorWrapper.reinit();
/*     */     try {
/* 483 */       if (!custIndicatorWrapper.requestFullAccess())
/* 484 */         return null;
/*     */     }
/*     */     catch (Exception e) {
/* 487 */       LOGGER.error(e.getMessage(), e);
/* 488 */       NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error while loading indicator: " + e.getMessage(), true);
/* 489 */       return null;
/*     */     }
/* 491 */     disableIndicator(custIndicatorWrapper.getBinaryFile());
/* 492 */     IIndicator indicator = custIndicatorWrapper.getIndicator();
/* 493 */     if (registerIndicator(indicator, notificationUtils, false)) {
/* 494 */       String name = indicator.getIndicatorInfo().getName().toUpperCase();
/* 495 */       this.nameToCustIndWrapper.put(name, custIndicatorWrapper);
/* 496 */       this.binFileToCustIndName.put(custIndicatorWrapper.getBinaryFile(), name);
/* 497 */       if (this.settingsStorage != null) {
/* 498 */         this.settingsStorage.saveEnabledIndicator(new EnabledIndicatorBean(name, custIndicatorWrapper.getSourceFile(), custIndicatorWrapper.getBinaryFile()));
/*     */       }
/*     */ 
/* 501 */       fireCustomIndicatorRegistered(custIndicatorWrapper);
/*     */ 
/* 503 */       return name;
/*     */     }
/* 505 */     return null;
/*     */   }
/*     */ 
/*     */   private boolean registerIndicator(IIndicator indicator, INotificationUtils notificationUtils, boolean dukascopy)
/*     */   {
/* 510 */     if (indicator == null) {
/* 511 */       notificationUtils.postErrorMessage("Cannot instantiate indicator", true);
/* 512 */       return false;
/*     */     }
/*     */     try {
/* 515 */       indicator.onStart(IndicatorHelper.createIndicatorContext());
/*     */     }
/*     */     catch (AbstractMethodError e) {
/* 518 */       NotificationUtilsProvider.getNotificationUtils().postWarningMessage("onStart method not implemented in indicator, implement it and recompile indicator", true);
/*     */     } catch (Throwable e) {
/* 520 */       notificationUtils.postErrorMessage("Exception in onStart method: " + StrategyWrapper.representError(indicator, e), e, true);
/* 521 */       return false;
/*     */     }
/*     */     try {
/* 524 */       if (!validateIndicator(indicator, notificationUtils, dukascopy))
/* 525 */         return false;
/*     */     }
/*     */     catch (Exception e) {
/* 528 */       notificationUtils.postErrorMessage("Exception while validating indicator: " + StrategyWrapper.representError(indicator, e), e, true);
/* 529 */       return false;
/*     */     }
/* 531 */     String name = indicator.getIndicatorInfo().getName().toUpperCase();
/* 532 */     String group = indicator.getIndicatorInfo().getGroupName();
/* 533 */     if (dukascopy)
/* 534 */       this.dukascopyIndicatorGroups.add(group);
/*     */     else {
/* 536 */       this.customIndicatorGroups.add(group);
/*     */     }
/* 538 */     Set indicators = dukascopy ? (Set)this.groupToDukascopyIndicators.get(group) : (Set)this.groupToCustomIndicators.get(group);
/* 539 */     if (indicators == null) {
/* 540 */       indicators = new HashSet();
/* 541 */       if (dukascopy)
/* 542 */         this.groupToDukascopyIndicators.put(group, indicators);
/*     */       else {
/* 544 */         this.groupToCustomIndicators.put(group, indicators);
/*     */       }
/*     */     }
/* 547 */     indicators.add(name);
/* 548 */     if (dukascopy) {
/* 549 */       this.dukascopyIndicatorNames.add(name);
/* 550 */       this.dukascopyIndicatorTitles.put(name, indicator.getIndicatorInfo().getTitle());
/*     */     } else {
/* 552 */       this.customIndicatorNames.add(name);
/* 553 */       this.customIndicatorTitles.put(name, indicator.getIndicatorInfo().getTitle());
/*     */     }
/* 555 */     return true;
/*     */   }
/*     */ 
/*     */   public void disableIndicator(File binIndicatorFile) {
/* 559 */     String name = (String)this.binFileToCustIndName.remove(binIndicatorFile);
/* 560 */     disableIndicator(name);
/*     */   }
/*     */ 
/*     */   public void disableIndicator(String name) {
/* 564 */     CustIndicatorWrapper custIndicatorWrapper = (CustIndicatorWrapper)this.nameToCustIndWrapper.remove(name);
/* 565 */     if (custIndicatorWrapper == null) {
/* 566 */       return;
/*     */     }
/* 568 */     IIndicator indicator = custIndicatorWrapper.getIndicator();
/*     */     try {
/* 570 */       indicator.onStart(IndicatorHelper.createIndicatorContext());
/*     */     }
/*     */     catch (AbstractMethodError e) {
/* 573 */       NotificationUtilsProvider.getNotificationUtils().postWarningMessage("onStart method not implemented in indicator, implement it and recompile indicator", true);
/*     */     }
/* 575 */     String group = indicator.getIndicatorInfo().getGroupName();
/* 576 */     this.customIndicatorTitles.remove(name);
/* 577 */     this.customIndicatorNames.remove(name);
/* 578 */     Set indicators = (Set)this.groupToCustomIndicators.get(group);
/* 579 */     indicators.remove(name);
/* 580 */     if (indicators.isEmpty()) {
/* 581 */       this.groupToCustomIndicators.remove(group);
/* 582 */       this.customIndicatorGroups.remove(group);
/*     */     }
/* 584 */     if (this.settingsStorage != null)
/* 585 */       this.settingsStorage.removeEnabledIndicator(new EnabledIndicatorBean(name, custIndicatorWrapper.getSourceFile(), custIndicatorWrapper.getBinaryFile()));
/*     */   }
/*     */ 
/*     */   private boolean validateIndicator(IIndicator indicator, INotificationUtils notificationUtils, boolean dukascopy)
/*     */   {
/* 590 */     IndicatorInfo indicatorInfo = indicator.getIndicatorInfo();
/* 591 */     if (indicatorInfo == null) {
/* 592 */       notificationUtils.postErrorMessage("Cannot enable indicator, getIndicatorInfo returns null", true);
/* 593 */       return false;
/*     */     }
/* 595 */     String name = indicatorInfo.getName().toUpperCase();
/* 596 */     if ((!dukascopy) && (getAllNames().contains(name))) {
/* 597 */       if (this.nameToCustIndWrapper.containsKey(name)) {
/* 598 */         disableIndicator(name);
/*     */       } else {
/* 600 */         notificationUtils.postErrorMessage("Cannot enable indicator, another indicator with name [" + name + "] already registered in the system", true);
/* 601 */         return false;
/*     */       }
/*     */     }
/* 604 */     if (indicatorInfo.getGroupName() == null) {
/* 605 */       notificationUtils.postErrorMessage("Cannot enable indicator, getGroupName returns null", true);
/* 606 */       return false;
/*     */     }
/* 608 */     if ((indicatorInfo.isOverChart()) && (indicatorInfo.isOverVolumes())) {
/* 609 */       notificationUtils.postErrorMessage("Cannot enable indicator, both isOverChart and isOverVolumes return true", true);
/* 610 */       return false;
/*     */     }
/* 612 */     int numberOfInputs = indicatorInfo.getNumberOfInputs();
/* 613 */     if (numberOfInputs < 0) {
/* 614 */       notificationUtils.postErrorMessage("Cannot enable indicator, getNumberOfInputs returns [" + numberOfInputs + "]", true);
/* 615 */       return false;
/*     */     }
/* 617 */     int numberOfOptionalInputs = indicatorInfo.getNumberOfOptionalInputs();
/* 618 */     if (numberOfOptionalInputs < 0) {
/* 619 */       notificationUtils.postErrorMessage("Cannot enable indicator, getNumberOfOptionalInputs returns [" + numberOfOptionalInputs + "]", true);
/* 620 */       return false;
/*     */     }
/* 622 */     int numberOfOutputs = indicatorInfo.getNumberOfOutputs();
/* 623 */     if (numberOfOutputs < 1) {
/* 624 */       notificationUtils.postErrorMessage("Cannot enable indicator, getNumberOfOutputs returns [" + numberOfOutputs + "]", true);
/* 625 */       return false;
/*     */     }
/* 627 */     for (int i = 0; i < numberOfInputs; i++) {
/* 628 */       InputParameterInfo inputInfo = indicator.getInputParameterInfo(i);
/* 629 */       if (inputInfo == null) {
/* 630 */         notificationUtils.postErrorMessage("Cannot enable indicator, getInputParameterInfo for input parameter with index [" + i + "] returns null", true);
/* 631 */         return false;
/*     */       }
/* 633 */       if (inputInfo.getType() == null) {
/* 634 */         notificationUtils.postErrorMessage("Cannot enable indicator, getType for input parameter with index [" + i + "] returns null", true);
/* 635 */         return false;
/*     */       }
/*     */     }
/* 638 */     for (int i = 0; i < numberOfOptionalInputs; i++) {
/* 639 */       OptInputParameterInfo optInputInfo = indicator.getOptInputParameterInfo(i);
/* 640 */       if (optInputInfo == null) {
/* 641 */         notificationUtils.postErrorMessage("Cannot enable indicator, getOptInputParameterInfo for optional input parameter with index [" + i + "] returns null", true);
/* 642 */         return false;
/*     */       }
/* 644 */       OptInputDescription description = optInputInfo.getDescription();
/* 645 */       if (description == null) {
/* 646 */         notificationUtils.postErrorMessage("Cannot enable indicator, getDescription for optional input parameter with index [" + i + "] returns null", true);
/* 647 */         return false;
/*     */       }
/* 649 */       if ((!(description instanceof IntegerListDescription)) && (!(description instanceof IntegerRangeDescription)) && (!(description instanceof DoubleListDescription)) && (!(description instanceof DoubleRangeDescription)) && (!(description instanceof BooleanOptInputDescription)))
/*     */       {
/* 656 */         notificationUtils.postErrorMessage("Cannot enable indicator, getDescription for optional input parameter with index [" + i + "] returns object that is not one of the allowed classes", true);
/* 657 */         return false;
/*     */       }
/* 659 */       if ((description instanceof IntegerListDescription)) {
/* 660 */         IntegerListDescription ilDescription = (IntegerListDescription)description;
/* 661 */         int[] values = ilDescription.getValues();
/* 662 */         if (values == null) {
/* 663 */           notificationUtils.postErrorMessage("Cannot enable indicator, getValues of description for optional input parameter with index [" + i + "] returns null", true);
/* 664 */           return false;
/*     */         }
/* 666 */         String[] valueNames = ilDescription.getValueNames();
/* 667 */         if (valueNames == null) {
/* 668 */           notificationUtils.postErrorMessage("Cannot enable indicator, getValueNames of description for optional input parameter with index [" + i + "] returns null", true);
/* 669 */           return false;
/*     */         }
/* 671 */         if (values.length != valueNames.length) {
/* 672 */           notificationUtils.postErrorMessage("Cannot enable indicator, getValueNames and getValues of description for optional input parameter with index [" + i + "] returns arrays of different length", true);
/* 673 */           return false;
/*     */         }
/* 675 */         int defaultValue = ilDescription.getDefaultValue();
/* 676 */         boolean found = false;
/* 677 */         for (int value : values) {
/* 678 */           if (value == defaultValue) {
/* 679 */             found = true;
/* 680 */             break;
/*     */           }
/*     */         }
/* 683 */         if (!found) {
/* 684 */           notificationUtils.postErrorMessage("Cannot enable indicator, getDefaultValue of description for optional input parameter with index [" + i + "] returns value that is not part of available values returned by getValues method", true);
/* 685 */           return false;
/*     */         }
/* 687 */       } else if ((description instanceof DoubleListDescription)) {
/* 688 */         DoubleListDescription dlDescription = (DoubleListDescription)description;
/* 689 */         double[] values = dlDescription.getValues();
/* 690 */         if (values == null) {
/* 691 */           notificationUtils.postErrorMessage("Cannot enable indicator, getValues of description for optional input parameter with index [" + i + "] returns null", true);
/* 692 */           return false;
/*     */         }
/* 694 */         String[] valueNames = dlDescription.getValueNames();
/* 695 */         if (valueNames == null) {
/* 696 */           notificationUtils.postErrorMessage("Cannot enable indicator, getValueNames of description for optional input parameter with index [" + i + "] returns null", true);
/* 697 */           return false;
/*     */         }
/* 699 */         if (values.length != valueNames.length) {
/* 700 */           notificationUtils.postErrorMessage("Cannot enable indicator, getValueNames and getValues of description for optional input parameter with index [" + i + "] returns arrays of different length", true);
/* 701 */           return false;
/*     */         }
/* 703 */         double defaultValue = dlDescription.getDefaultValue();
/* 704 */         boolean found = false;
/* 705 */         for (double value : values) {
/* 706 */           if (value == defaultValue) {
/* 707 */             found = true;
/* 708 */             break;
/*     */           }
/*     */         }
/* 711 */         if (!found) {
/* 712 */           notificationUtils.postErrorMessage("Cannot enable indicator, getDefaultValue of description for optional input parameter with index [" + i + "] returns value that is not part of available values returned by getValues method", true);
/* 713 */           return false;
/*     */         }
/* 715 */       } else if ((description instanceof IntegerRangeDescription)) {
/* 716 */         IntegerRangeDescription irDescription = (IntegerRangeDescription)description;
/* 717 */         int min = irDescription.getMin();
/* 718 */         int max = irDescription.getMax();
/* 719 */         if (min > max) {
/* 720 */           notificationUtils.postErrorMessage("Cannot enable indicator, getMin is bigger than getMax of description for optional input parameter with index [" + i + "]", true);
/* 721 */           return false;
/*     */         }
/* 723 */         int defaultValue = irDescription.getDefaultValue();
/* 724 */         if ((defaultValue < min) || (defaultValue > max)) {
/* 725 */           notificationUtils.postErrorMessage("Cannot enable indicator, getDefaultValue of description for optional input parameter with index [" + i + "] returns value that is not in range of getMin and getMax", true);
/* 726 */           return false;
/*     */         }
/* 728 */         int increment = irDescription.getSuggestedIncrement();
/* 729 */         if ((min != max) && (increment == 0)) {
/* 730 */           notificationUtils.postErrorMessage("Cannot enable indicator, getSuggestedIncrement of description for optional input parameter with index [" + i + "] returns zero", true);
/* 731 */           return false;
/*     */         }
/* 733 */         if ((min == max) && (increment != 0)) {
/* 734 */           notificationUtils.postErrorMessage("Cannot enable indicator, getSuggestedIncrement of description for optional input parameter with index [" + i + "] returns non zero while getMin == getMax", true);
/* 735 */           return false;
/*     */         }
/* 737 */         if ((min != max) && (increment > max - min)) {
/* 738 */           notificationUtils.postErrorMessage("Cannot enable indicator, getSuggestedIncrement of description for optional input parameter with index [" + i + "] returns value that is bigger than getMax - getMin", true);
/* 739 */           return false;
/*     */         }
/* 741 */       } else if ((description instanceof DoubleRangeDescription)) {
/* 742 */         DoubleRangeDescription drDescription = (DoubleRangeDescription)description;
/* 743 */         double min = drDescription.getMin();
/* 744 */         double max = drDescription.getMax();
/* 745 */         if (min > max) {
/* 746 */           notificationUtils.postErrorMessage("Cannot enable indicator, getMin is bigger than getMax of description for optional input parameter with index [" + i + "]", true);
/* 747 */           return false;
/*     */         }
/* 749 */         double defaultValue = drDescription.getDefaultValue();
/* 750 */         if ((defaultValue < min) || (defaultValue > max)) {
/* 751 */           notificationUtils.postErrorMessage("Cannot enable indicator, getDefaultValue of description for optional input parameter with index [" + i + "] returns value that is not in range of getMin and getMax", true);
/* 752 */           return false;
/*     */         }
/* 754 */         double increment = drDescription.getSuggestedIncrement();
/* 755 */         if ((min != max) && (increment == 0.0D)) {
/* 756 */           notificationUtils.postErrorMessage("Cannot enable indicator, getSuggestedIncrement of description for optional input parameter with index [" + i + "] returns zero", true);
/* 757 */           return false;
/*     */         }
/* 759 */         if ((min == max) && (increment != 0.0D)) {
/* 760 */           notificationUtils.postErrorMessage("Cannot enable indicator, getSuggestedIncrement of description for optional input parameter with index [" + i + "] returns non zero while getMin == getMax", true);
/* 761 */           return false;
/*     */         }
/* 763 */         if ((min != max) && (increment > max - min)) {
/* 764 */           notificationUtils.postErrorMessage("Cannot enable indicator, getSuggestedIncrement of description for optional input parameter with index [" + i + "] returns value that is bigger than getMax - getMin", true);
/* 765 */           return false;
/*     */         }
/* 767 */         int precision = drDescription.getPrecision();
/* 768 */         if ((precision < 0) || (precision > 10)) {
/* 769 */           notificationUtils.postErrorMessage("Cannot enable indicator, getPrecision of description for optional input parameter with index [" + i + "] returns value that is either less than zero or bigger than 10", true);
/* 770 */           return false;
/*     */         }
/*     */       } else {
/* 773 */         if (!(description instanceof BooleanOptInputDescription))
/*     */           continue;
/*     */       }
/*     */     }
/* 777 */     for (int i = 0; i < numberOfOutputs; i++) {
/* 778 */       OutputParameterInfo outputInfo = indicator.getOutputParameterInfo(i);
/* 779 */       if (outputInfo == null) {
/* 780 */         notificationUtils.postErrorMessage("Cannot enable indicator, getOutputParameterInfo for output parameter with index [" + i + "] returns null", true);
/* 781 */         return false;
/*     */       }
/* 783 */       if (outputInfo.getType() == null) {
/* 784 */         notificationUtils.postErrorMessage("Cannot enable indicator, getType for output parameter with index [" + i + "] returns null", true);
/* 785 */         return false;
/*     */       }
/* 787 */       OutputParameterInfo.DrawingStyle drawingStyle = outputInfo.getDrawingStyle();
/* 788 */       if ((indicatorInfo.isOverChart()) && (drawingStyle == OutputParameterInfo.DrawingStyle.HISTOGRAM)) {
/* 789 */         notificationUtils.postErrorMessage("Cannot enable indicator, getDrawingStyle for output parameter with index [" + i + "] returns style that is not compatible with \"over chart\" indicator", true);
/* 790 */         return false;
/*     */       }
/* 792 */       if ((indicatorInfo.isOverVolumes()) && ((drawingStyle == OutputParameterInfo.DrawingStyle.HISTOGRAM) || (drawingStyle == OutputParameterInfo.DrawingStyle.PATTERN_BOOL) || (drawingStyle == OutputParameterInfo.DrawingStyle.PATTERN_BULL_BEAR) || (drawingStyle == OutputParameterInfo.DrawingStyle.PATTERN_STRENGTH)))
/*     */       {
/* 795 */         notificationUtils.postErrorMessage("Cannot enable indicator, getDrawingStyle for output parameter with index [" + i + "] returns style that is not compatible with \"over volumes\" indicator", true);
/* 796 */         return false;
/*     */       }
/* 798 */       if (((drawingStyle == OutputParameterInfo.DrawingStyle.PATTERN_BOOL) || (drawingStyle == OutputParameterInfo.DrawingStyle.PATTERN_BULL_BEAR) || (drawingStyle == OutputParameterInfo.DrawingStyle.PATTERN_STRENGTH)) && ((!indicatorInfo.isOverChart()) || (outputInfo.getType() != OutputParameterInfo.Type.INT)))
/*     */       {
/* 801 */         notificationUtils.postErrorMessage("Cannot enable indicator, getDrawingStyle for output parameter with index [" + i + "] returns style that is possible only with \"over chart\" indicator and should have INT type", true);
/* 802 */         return false;
/*     */       }
/* 804 */       Class indicatorClass = indicator.getClass();
/* 805 */       if (outputInfo.isDrawnByIndicator())
/* 806 */         if (!(indicator instanceof IDrawingIndicator)) {
/* 807 */           NotificationUtilsProvider.getNotificationUtils().postWarningMessage("Please implement IDrawingIndicator interface to define drawOutput method", true);
/*     */           try {
/* 809 */             indicatorClass.getMethod("drawOutput", new Class[] { Graphics.class, Integer.TYPE, Object.class, Color.class, IIndicatorDrawingSupport.class, List.class, Map.class });
/*     */           } catch (NoSuchMethodException e) {
/* 811 */             notificationUtils.postErrorMessage("Cannot enable indicator, isDrawnByIndicator for output parameter with index [" + i + "] returns true, while indicator doesn't define drawOutput method", true);
/*     */ 
/* 813 */             return false;
/*     */           } catch (Exception e) {
/* 815 */             LOGGER.error(e.getMessage(), e);
/*     */           }
/*     */         } else {
/*     */           try {
/* 819 */             indicatorClass.getMethod("drawOutput", new Class[] { Graphics.class, Integer.TYPE, Object.class, Color.class, Stroke.class, IIndicatorDrawingSupport.class, List.class, Map.class });
/*     */           } catch (NoSuchMethodException e) {
/* 821 */             notificationUtils.postErrorMessage("Cannot enable indicator, isDrawnByIndicator for output parameter with index [" + i + "] returns true, while indicator doesn't define drawOutput method", true);
/*     */ 
/* 823 */             return false;
/*     */           } catch (Exception e) {
/* 825 */             LOGGER.error(e.getMessage(), e);
/*     */           }
/*     */         }
/*     */       try
/*     */       {
/* 830 */         indicatorClass.getMethod("getMinMax", new Class[] { Integer.TYPE, Object.class, Integer.TYPE, Integer.TYPE });
/* 831 */         if (!(indicator instanceof IMinMax))
/* 832 */           NotificationUtilsProvider.getNotificationUtils().postWarningMessage("Please implement IMinMax interface to define getMinMax method", true);
/*     */       }
/*     */       catch (NoSuchMethodException e) {
/*     */       }
/*     */       catch (Exception e) {
/* 837 */         LOGGER.error(e.getMessage(), e);
/*     */       }
/*     */     }
/* 840 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isCustomIndicatorEnabled(File binaryFile) {
/* 844 */     return this.binFileToCustIndName.containsKey(binaryFile);
/*     */   }
/*     */ 
/*     */   public CustIndicatorWrapper getCustomIndicatorWrapperByName(String name) {
/* 848 */     return (CustIndicatorWrapper)this.nameToCustIndWrapper.get(name);
/*     */   }
/*     */ 
/*     */   public void addCustomIndicatorsActionListener(CustomIndicatorsActionListener customIndicatorsActionListener) {
/* 852 */     this.customIndicatorsActionListeners.add(customIndicatorsActionListener);
/*     */   }
/*     */ 
/*     */   public void removeCustomIndicatorsActionListener(CustomIndicatorsActionListener customIndicatorsActionListener) {
/* 856 */     this.customIndicatorsActionListeners.remove(customIndicatorsActionListener);
/*     */   }
/*     */ 
/*     */   private void fireCustomIndicatorRegistered(CustIndicatorWrapper custIndicatorWrapper) {
/* 860 */     for (CustomIndicatorsActionListener listener : this.customIndicatorsActionListeners)
/* 861 */       listener.customIndicatorRegistered(custIndicatorWrapper);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.math.indicators.IndicatorsProvider
 * JD-Core Version:    0.6.0
 */