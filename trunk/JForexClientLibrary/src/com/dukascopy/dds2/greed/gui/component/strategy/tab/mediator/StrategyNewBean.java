/*     */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator;
/*     */ 
/*     */ import com.dukascopy.api.IStrategy;
/*     */ import com.dukascopy.dds2.greed.agent.compiler.JFXPack;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.preset.StrategyPreset;
/*     */ import java.io.File;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import java.util.TimeZone;
/*     */ import javax.swing.Timer;
/*     */ 
/*     */ public class StrategyNewBean
/*     */ {
/*     */   public static final String TIME_FORMAT_PATTERN = "HH:mm:ss";
/*     */   public static final String EMPTY_CELL_PATTERN = "--";
/*  31 */   public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
/*     */   private Integer id;
/*     */   private String name;
/*     */   private String comments;
/*     */   private Date startTimeDate;
/*     */   private Date endTimeDate;
/*     */   private Date durationTimeDate;
/*     */   private String startTime;
/*     */   private String endTime;
/*     */   private String durationTime;
/*     */   private StrategyType type;
/*     */   private StrategyStatus status;
/*     */   private long runningProcessId;
/*     */   private String remoteProcessId;
/*     */   private String remoteRequestId;
/*  53 */   private List<StrategyPreset> strategyPresets = new ArrayList();
/*     */ 
/*  55 */   private Timer timer = null;
/*     */ 
/*  57 */   private IStrategy strategy = null;
/*  58 */   private JFXPack pack = null;
/*     */ 
/*  60 */   private File strategyBinaryFile = null;
/*  61 */   private File strategySourceFile = null;
/*     */ 
/*  63 */   private long lastModifiedDate = 0L;
/*     */ 
/*  65 */   private SimpleDateFormat utcDateFormat = new SimpleDateFormat("HH:mm:ss");
/*     */   private StrategyPreset activePreset;
/*     */   private String runningPresetName;
/*  70 */   private long startingTimestamp = -9223372036854775808L;
/*     */ 
/*     */   public StrategyNewBean()
/*     */   {
/*  74 */     this.utcDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
/*     */   }
/*     */ 
/*     */   public IStrategy getStrategy() {
/*  78 */     return this.strategy;
/*     */   }
/*     */ 
/*     */   public void setStrategy(IStrategy strategy) {
/*  82 */     this.strategy = strategy;
/*     */   }
/*     */ 
/*     */   public Timer getTimer() {
/*  86 */     return this.timer;
/*     */   }
/*     */ 
/*     */   public void setTimer(Timer timer) {
/*  90 */     this.timer = timer;
/*     */   }
/*     */ 
/*     */   public Integer getId() {
/*  94 */     return this.id;
/*     */   }
/*     */ 
/*     */   public void setId(Integer id) {
/*  98 */     this.id = id;
/*     */   }
/*     */ 
/*     */   public String getName() {
/* 102 */     return this.name;
/*     */   }
/*     */ 
/*     */   public void setName(String name) {
/* 106 */     this.name = name;
/*     */   }
/*     */ 
/*     */   public String getComments() {
/* 110 */     return this.comments;
/*     */   }
/*     */ 
/*     */   public void setComments(String comments) {
/* 114 */     this.comments = comments;
/*     */   }
/*     */ 
/*     */   public void setStartTime(Date date) {
/* 118 */     this.startTimeDate = date;
/* 119 */     this.startTime = DEFAULT_DATE_FORMAT.format(date);
/*     */   }
/*     */ 
/*     */   public String getStartTime() {
/* 123 */     return this.startTime;
/*     */   }
/*     */ 
/*     */   public Date getStartTimeAsDate() {
/* 127 */     return this.startTimeDate;
/*     */   }
/*     */ 
/*     */   public void setEndTime(Date date) {
/* 131 */     this.endTimeDate = date;
/* 132 */     this.endTime = DEFAULT_DATE_FORMAT.format(date);
/*     */   }
/*     */ 
/*     */   public String getEndTime() {
/* 136 */     return this.endTime;
/*     */   }
/*     */ 
/*     */   public Date getEndTimeAsDate() {
/* 140 */     return this.endTimeDate;
/*     */   }
/*     */ 
/*     */   public void setDurationTime(Date date) {
/* 144 */     this.durationTimeDate = date;
/* 145 */     this.durationTime = this.utcDateFormat.format(date);
/*     */   }
/*     */ 
/*     */   public String getDurationTime() {
/* 149 */     return this.durationTime;
/*     */   }
/*     */ 
/*     */   public Date getDurationTimeAsDate() {
/* 153 */     return this.durationTimeDate;
/*     */   }
/*     */ 
/*     */   public StrategyType getType() {
/* 157 */     return this.type;
/*     */   }
/*     */ 
/*     */   public void setType(StrategyType type) {
/* 161 */     this.type = type;
/*     */   }
/*     */ 
/*     */   public StrategyStatus getStatus() {
/* 165 */     return this.status;
/*     */   }
/*     */ 
/*     */   public void setStatus(StrategyStatus status) {
/* 169 */     this.status = status;
/* 170 */     if (status == StrategyStatus.STARTING)
/* 171 */       this.startingTimestamp = System.currentTimeMillis();
/*     */   }
/*     */ 
/*     */   public List<StrategyPreset> getStrategyPresets()
/*     */   {
/* 176 */     return this.strategyPresets;
/*     */   }
/*     */ 
/*     */   public void setStrategyPresets(List<StrategyPreset> presets) {
/* 180 */     this.strategyPresets = presets;
/*     */   }
/*     */ 
/*     */   public File getStrategyBinaryFile() {
/* 184 */     return this.strategyBinaryFile;
/*     */   }
/*     */ 
/*     */   public void setStrategyBinaryFile(File file) {
/* 188 */     this.strategyBinaryFile = file;
/*     */   }
/*     */ 
/*     */   public File getStrategySourceFile() {
/* 192 */     return this.strategySourceFile;
/*     */   }
/*     */ 
/*     */   public void setStrategySourceFile(File strategySourceFile) {
/* 196 */     this.strategySourceFile = strategySourceFile;
/*     */   }
/*     */ 
/*     */   public long getRunningProcessId() {
/* 200 */     return this.runningProcessId;
/*     */   }
/*     */ 
/*     */   public void setRunningProcessId(long runningProcessId) {
/* 204 */     this.runningProcessId = runningProcessId;
/*     */   }
/*     */ 
/*     */   public String getRemoteProcessId() {
/* 208 */     return this.remoteProcessId;
/*     */   }
/*     */ 
/*     */   public void setRemoteProcessId(String remoteProcessId) {
/* 212 */     this.remoteProcessId = remoteProcessId;
/*     */   }
/*     */ 
/*     */   public String getRemoteRequestId()
/*     */   {
/* 219 */     return this.remoteRequestId;
/*     */   }
/*     */ 
/*     */   public void setRemoteRequestId(String remoteRequestId)
/*     */   {
/* 226 */     this.remoteRequestId = remoteRequestId;
/*     */   }
/*     */ 
/*     */   public StrategyPreset getActivePreset() {
/* 230 */     return this.activePreset;
/*     */   }
/*     */ 
/*     */   public void setActivePreset(StrategyPreset activePreset) {
/* 234 */     this.activePreset = activePreset;
/*     */   }
/*     */ 
/*     */   public String getRunningPresetName() {
/* 238 */     return this.runningPresetName;
/*     */   }
/*     */ 
/*     */   public void setRunningPresetName(String runningPresetName) {
/* 242 */     this.runningPresetName = runningPresetName;
/*     */   }
/*     */ 
/*     */   public JFXPack getPack() {
/* 246 */     return this.pack;
/*     */   }
/*     */ 
/*     */   public void setPack(JFXPack pack) {
/* 250 */     this.pack = pack;
/*     */   }
/*     */ 
/*     */   public boolean isFullAccessGranted() {
/* 254 */     return (this.pack != null) && (this.pack.isFullAccess());
/*     */   }
/*     */ 
/*     */   public String getStrategyKey()
/*     */   {
/* 259 */     if (this.strategyBinaryFile != null) {
/* 260 */       return this.strategyBinaryFile.getName() + " " + this.pack.getMD5HexString();
/*     */     }
/*     */ 
/* 263 */     return getName() + " " + this.pack.getMD5HexString();
/*     */   }
/*     */ 
/*     */   public long getLastModifiedDate() {
/* 267 */     return this.lastModifiedDate;
/*     */   }
/*     */ 
/*     */   public void setLastModifiedDate(long lastModifiedDate) {
/* 271 */     this.lastModifiedDate = lastModifiedDate;
/*     */   }
/*     */ 
/*     */   public boolean isStartingOrRunning() {
/* 275 */     return (getStatus().equals(StrategyStatus.RUNNING)) || (getStatus().equals(StrategyStatus.STARTING));
/*     */   }
/*     */ 
/*     */   public boolean hasParameters()
/*     */   {
/* 280 */     if (this.strategyPresets != null) {
/* 281 */       for (StrategyPreset preset : this.strategyPresets) {
/* 282 */         if ("DEFAULT_PRESET_ID".equals(preset.getId())) {
/* 283 */           return (preset.getStrategyParameters() != null) && (preset.getStrategyParameters().size() > 0);
/*     */         }
/*     */       }
/*     */     }
/* 287 */     return false;
/*     */   }
/*     */ 
/*     */   public void resetDates() {
/* 291 */     this.startTimeDate = null;
/* 292 */     this.startTime = "--";
/*     */ 
/* 294 */     this.endTimeDate = null;
/* 295 */     this.endTime = "--";
/*     */ 
/* 297 */     this.durationTimeDate = null;
/* 298 */     this.durationTime = "--";
/*     */   }
/*     */ 
/*     */   public long getStartingTimestamp()
/*     */   {
/* 305 */     return this.startingTimestamp;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyNewBean
 * JD-Core Version:    0.6.0
 */