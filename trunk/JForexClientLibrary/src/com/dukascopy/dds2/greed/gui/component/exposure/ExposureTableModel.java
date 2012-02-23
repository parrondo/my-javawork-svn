/*     */ package com.dukascopy.dds2.greed.gui.component.exposure;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*     */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsTable;
/*     */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsTableModel;
/*     */ import com.dukascopy.dds2.greed.gui.util.lotamount.LotAmountChanger;
/*     */ import com.dukascopy.dds2.greed.model.AccountStatement;
/*     */ import com.dukascopy.dds2.greed.model.CurrencyMarketWrapper;
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.Position;
/*     */ import com.dukascopy.transport.common.model.type.PositionSide;
/*     */ import com.dukascopy.transport.common.msg.request.AccountInfoMessage;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.RoundingMode;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Currency;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.swing.table.AbstractTableModel;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class ExposureTableModel extends AbstractTableModel
/*     */ {
/*     */   private static final Logger LOGGER;
/*     */   private static final int COLUMNS = 7;
/*     */   public static final int COL_CHECK = 0;
/*     */   public static final int COL_INSTRUMENT = 1;
/*     */   public static final int COL_DIRECTION = 2;
/*     */   public static final int COL_LS = 3;
/*     */   public static final int COL_AMOUNT = 4;
/*     */   public static final int COL_PRICE = 5;
/*     */   public static final int COL_PL = 6;
/*     */   private static BigDecimal ZERRO;
/*     */   private AccountStatement accountStatement;
/*  50 */   private final Map<String, ExposureHolder> exposureMap = new HashMap();
/*     */ 
/*     */   public ExposureTableModel()
/*     */   {
/*  54 */     this.accountStatement = ((AccountStatement)GreedContext.get("accountStatement"));
/*     */   }
/*     */ 
/*     */   public int getRowCount()
/*     */   {
/* 102 */     return this.exposureMap.size();
/*     */   }
/*     */ 
/*     */   public int getColumnCount() {
/* 106 */     return 7;
/*     */   }
/*     */ 
/*     */   public Class<?> getColumnClass(int columnIndex) {
/* 110 */     Class columnClass = super.getColumnClass(columnIndex);
/* 111 */     if (columnIndex == 3)
/* 112 */       return ExposureHolder.class;
/* 113 */     if (0 == columnIndex)
/* 114 */       return ExposureHolder.class;
/* 115 */     if (columnIndex == 6) {
/* 116 */       return Money.class;
/*     */     }
/* 118 */     return columnClass;
/*     */   }
/*     */ 
/*     */   public boolean isCellEditable(int rowIndex, int columnIndex)
/*     */   {
/* 123 */     ExposureHolder holder = (ExposureHolder)this.exposureMap.values().toArray()[rowIndex];
/* 124 */     if (null == holder) {
/* 125 */       return false;
/*     */     }
/* 127 */     if (holder.isDisabled) {
/* 128 */       return false;
/*     */     }
/* 130 */     return columnIndex == 0;
/*     */   }
/*     */ 
/*     */   public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
/* 134 */     if (0 == columnIndex) {
/* 135 */       Boolean checked = (Boolean)aValue;
/* 136 */       ExposureHolder exh = (ExposureHolder)this.exposureMap.values().toArray()[rowIndex];
/* 137 */       if (null != exh) {
/* 138 */         if (exh.isDisabled) {
/* 139 */           return;
/*     */         }
/* 141 */         ExposureHolder.access$102(exh, checked.booleanValue());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public ExposureHolder getHolder(int index) {
/* 147 */     if ((index < 0) || (index >= getRowCount())) return null;
/* 148 */     return (ExposureHolder)this.exposureMap.values().toArray()[index];
/*     */   }
/*     */ 
/*     */   public Collection<ExposureHolder> getHolders() {
/* 152 */     return this.exposureMap.values();
/*     */   }
/*     */ 
/*     */   public Object getValueAt(int row, int column) {
/* 156 */     if ((row < 0) || (row >= getRowCount())) return null;
/*     */ 
/* 158 */     ExposureHolder holder = (ExposureHolder)this.exposureMap.values().toArray()[row];
/* 159 */     if (null == holder) {
/* 160 */       return null;
/*     */     }
/* 162 */     switch (column) { case 0:
/* 163 */       return holder;
/*     */     case 1:
/* 164 */       return holder.instrument;
/*     */     case 3:
/* 166 */       return holder;
/*     */     case 4:
/* 170 */       BigDecimal divider = LotAmountChanger.getLotAmountForInstrument(Instrument.fromString(holder.instrument));
/*     */ 
/* 172 */       String currency = holder.instrument.substring(0, holder.instrument.indexOf("/"));
/* 173 */       String formattedAmount = currency + " " + trim(holder.amountL.subtract(holder.amountS).divide(divider, 6, RoundingMode.HALF_EVEN)).toPlainString();
/*     */ 
/* 176 */       return formattedAmount;
/*     */     case 2:
/* 179 */       int comparison = holder.amountL.subtract(holder.amountS).compareTo(BigDecimal.ZERO);
/* 180 */       switch (comparison) { case -1:
/* 181 */         return "SHORT";
/*     */       case 0:
/* 182 */         return "FLAT";
/*     */       case 1:
/* 183 */         return "LONG";
/*     */       }
/*     */ 
/*     */     case 6:
/* 187 */       if (holder.profitLoss != null) {
/* 188 */         return holder.profitLoss;
/*     */       }
/* 190 */       return "N/A";
/*     */     case 5:
/* 193 */       int comparison = holder.amountL.subtract(holder.amountS).compareTo(BigDecimal.ZERO);
/* 194 */       return 0 == comparison ? BigDecimal.ZERO : holder.price;
/*     */     }
/* 196 */     return null;
/*     */   }
/*     */ 
/*     */   public void updateExposure(Position position, UpdateMode mode) {
/* 200 */     switch (1.$SwitchMap$com$dukascopy$dds2$greed$gui$component$exposure$ExposureTableModel$UpdateMode[mode.ordinal()]) {
/*     */     case 1:
/*     */     case 2:
/* 203 */       updateExposureAddRemove(position, mode); break;
/*     */     case 3:
/* 207 */       updateExposureModify(position);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onMarketState(CurrencyMarketWrapper marketState)
/*     */   {
/* 215 */     boolean isNaN = false;
/* 216 */     String instrument = marketState.getInstrument();
/* 217 */     if (!this.exposureMap.containsKey(instrument)) {
/* 218 */       return;
/*     */     }
/* 220 */     ClientForm gui = (ClientForm)GreedContext.get("clientGui");
/* 221 */     if (gui == null) return;
/* 222 */     PositionsTableModel potam = (PositionsTableModel)gui.getPositionsPanel().getTable().getModel();
/* 223 */     List positions = potam.getPositions(instrument);
/* 224 */     Currency accountCurrency = this.accountStatement.getLastAccountState().getCurrency();
/* 225 */     Money pl = new Money(BigDecimal.ZERO, accountCurrency);
/*     */ 
/* 227 */     for (Position position : positions) {
/* 228 */       Money proLoss = potam.calculateProfitLoss(position);
/* 229 */       if ((null != proLoss) && (null != proLoss.getValue())) {
/* 230 */         pl = pl.add(proLoss);
/*     */       }
/*     */       else
/*     */       {
/* 239 */         isNaN = true;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 244 */     ExposureHolder holder = (ExposureHolder)this.exposureMap.get(instrument);
/* 245 */     if (null == holder) {
/* 246 */       return;
/*     */     }
/* 248 */     if (isNaN)
/* 249 */       holder.profitLoss = null;
/*     */     else {
/* 251 */       holder.profitLoss = pl;
/*     */     }
/* 253 */     this.exposureMap.put(instrument, holder);
/*     */ 
/* 256 */     int row = 0;
/* 257 */     for (ExposureHolder eh : this.exposureMap.values()) {
/* 258 */       if (instrument.equals(eh.instrument)) {
/*     */         break;
/*     */       }
/* 261 */       row++;
/*     */     }
/*     */ 
/* 264 */     fireTableRowsUpdated(row, row);
/*     */   }
/*     */ 
/*     */   private void updateExposureAddRemove(Position position, UpdateMode mode) {
/* 268 */     assert (mode != UpdateMode.UPDATE) : "invalid mode; must be ADD or REMOVE";
/* 269 */     String instrument = position.getInstrument();
/* 270 */     if (null == instrument) {
/* 271 */       return;
/*     */     }
/*     */ 
/* 274 */     PositionSide side = null;
/*     */     try {
/* 276 */       side = position.getPositionSide();
/*     */     } catch (Exception e) {
/* 278 */       LOGGER.error(e.getMessage(), e);
/* 279 */       return;
/*     */     }
/* 281 */     int countLong = PositionSide.LONG.equals(side) ? 1 : 0;
/* 282 */     int countShort = countLong == 1 ? 0 : 1;
/* 283 */     BigDecimal amount = null != position.getAmount() ? position.getAmount().getValue() : BigDecimal.ZERO;
/* 284 */     BigDecimal amountS = PositionSide.SHORT.equals(side) ? amount : BigDecimal.ZERO;
/* 285 */     BigDecimal amountL = PositionSide.LONG.equals(side) ? amount : BigDecimal.ZERO;
/* 286 */     BigDecimal price = BigDecimal.ZERO;
/*     */     try {
/* 288 */       price = calculatePrice(instrument);
/*     */     } catch (Exception e) {
/* 290 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */ 
/* 293 */     ExposureHolder holder = (ExposureHolder)this.exposureMap.get(instrument);
/* 294 */     Set keySet = this.exposureMap.keySet();
/* 295 */     int row = 0;
/* 296 */     for (String key : keySet) {
/* 297 */       if (key.equals(instrument)) {
/*     */         break;
/*     */       }
/* 300 */       row++;
/*     */     }
/*     */ 
/* 303 */     if (UpdateMode.ADD == mode) {
/* 304 */       if (null == holder) {
/* 305 */         holder = new ExposureHolder(instrument, countLong, countShort, amountL, amountS, price);
/* 306 */         holder.profitLoss = calculateProLoss(position);
/*     */       } else {
/* 308 */         holder.countL += countLong;
/* 309 */         holder.countS += countShort;
/* 310 */         holder.amountL = holder.amountL.add(amountL);
/* 311 */         holder.amountS = holder.amountS.add(amountS);
/* 312 */         holder.price = price;
/* 313 */         holder.profitLoss = calculateProLoss(position);
/*     */       }
/*     */ 
/* 318 */       holder.positions.add(position);
/*     */     } else {
/* 320 */       if (null == holder) {
/* 321 */         return;
/*     */       }
/* 323 */       holder.countL -= countLong;
/* 324 */       holder.countS -= countShort;
/* 325 */       holder.amountL = holder.amountL.subtract(amountL);
/* 326 */       holder.amountS = holder.amountS.subtract(amountS);
/* 327 */       holder.price = price;
/* 328 */       holder.positions.remove(position);
/*     */     }
/*     */ 
/* 332 */     if (GreedContext.IS_FXDD_LABEL) {
/* 333 */       int comparison = holder.amountL.subtract(holder.amountS).compareTo(BigDecimal.ZERO);
/* 334 */       if (comparison == 0) {
/* 335 */         this.exposureMap.remove(instrument);
/* 336 */         fireTableDataChanged();
/* 337 */         return;
/*     */       }
/*     */     }
/*     */ 
/* 341 */     if (0 == holder.countL + holder.countS) {
/* 342 */       this.exposureMap.remove(instrument);
/*     */ 
/* 344 */       fireTableDataChanged();
/*     */     } else {
/* 346 */       this.exposureMap.put(instrument, holder);
/*     */ 
/* 348 */       fireTableDataChanged();
/*     */     }
/*     */   }
/*     */ 
/*     */   private Money calculateProLoss(Position position)
/*     */   {
/* 355 */     ClientForm gui = (ClientForm)GreedContext.get("clientGui");
/* 356 */     PositionsTableModel potam = (PositionsTableModel)gui.getPositionsPanel().getTable().getModel();
/*     */ 
/* 360 */     return potam.calculateProfitLoss(position);
/*     */   }
/*     */ 
/*     */   private void updateExposureModify(Position pos)
/*     */   {
/* 366 */     if (null == pos) {
/* 367 */       return;
/*     */     }
/* 369 */     if (null == pos.getInstrument()) {
/* 370 */       return;
/*     */     }
/*     */ 
/* 373 */     String instrument = pos.getInstrument();
/*     */ 
/* 375 */     ClientForm gui = (ClientForm)GreedContext.get("clientGui");
/* 376 */     PositionsTableModel potam = (PositionsTableModel)gui.getPositionsPanel().getTable().getModel();
/* 377 */     List positions = potam.getPositions(instrument);
/* 378 */     ExposureHolder holder = null;
/* 379 */     BigDecimal vp = BigDecimal.ZERO;
/* 380 */     BigDecimal v = BigDecimal.ZERO;
/*     */ 
/* 383 */     for (Position position : positions) {
/* 384 */       BigDecimal volume = position.getAmount().getValue();
/* 385 */       PositionSide side = position.getPositionSide();
/* 386 */       if (PositionSide.SHORT.equals(side)) {
/* 387 */         volume = volume.negate();
/*     */       }
/* 389 */       BigDecimal price = position.getPriceOpen().getValue();
/* 390 */       v = v.add(volume);
/* 391 */       vp = vp.add(volume.multiply(price));
/* 392 */       int countLong = PositionSide.LONG.equals(side) ? 1 : 0;
/* 393 */       int countShort = countLong == 1 ? 0 : 1;
/* 394 */       BigDecimal amount = null != position.getAmount() ? position.getAmount().getValue() : BigDecimal.ZERO;
/* 395 */       BigDecimal amountS = PositionSide.SHORT.equals(side) ? amount : BigDecimal.ZERO;
/* 396 */       BigDecimal amountL = PositionSide.LONG.equals(side) ? amount : BigDecimal.ZERO;
/* 397 */       if (null == holder) {
/* 398 */         holder = new ExposureHolder(instrument, countLong, countShort, amountL, amountS, BigDecimal.ZERO);
/*     */       } else {
/* 400 */         holder.countL += countLong;
/* 401 */         holder.countS += countShort;
/* 402 */         holder.amountL = holder.amountL.add(amountL);
/* 403 */         holder.amountS = holder.amountS.add(amountS);
/*     */       }
/* 405 */       holder.positions.add(position);
/*     */     }
/*     */ 
/* 408 */     BigDecimal result = BigDecimal.ZERO;
/*     */ 
/* 410 */     if (!v.setScale(2, 0).equals(ZERRO))
/*     */       try {
/* 412 */         result = vp.divide(v, RoundingMode.HALF_EVEN);
/*     */       } catch (Exception e) {
/* 414 */         LOGGER.error(e.getMessage(), e);
/*     */       }
/*     */     else {
/* 417 */       LOGGER.debug("was ready to divide by zero, volume =" + v);
/*     */     }
/*     */ 
/* 420 */     if (null != holder) {
/* 421 */       holder.price = result;
/*     */ 
/* 423 */       ExposureHolder prewHolderInstance = (ExposureHolder)this.exposureMap.get(instrument);
/* 424 */       if (this.exposureMap.get(instrument) != null) {
/* 425 */         holder.setDisabled(prewHolderInstance.isDisabled());
/*     */       }
/*     */ 
/* 428 */       this.exposureMap.put(instrument, holder);
/*     */     }
/*     */   }
/*     */ 
/*     */   private BigDecimal calculatePrice(String instrument) {
/* 433 */     ClientForm gui = (ClientForm)GreedContext.get("clientGui");
/* 434 */     PositionsTableModel potam = (PositionsTableModel)gui.getPositionsPanel().getTable().getModel();
/* 435 */     List positions = potam.getPositions(instrument);
/* 436 */     BigDecimal vp = BigDecimal.ZERO;
/* 437 */     BigDecimal v = BigDecimal.ZERO;
/*     */ 
/* 441 */     for (Position position : positions) {
/* 442 */       PositionSide side = position.getPositionSide();
/* 443 */       BigDecimal volume = position.getAmount().getValue();
/* 444 */       if (PositionSide.SHORT == side) {
/* 445 */         volume = volume.negate();
/*     */       }
/* 447 */       BigDecimal price = position.getPriceOpen().getValue();
/* 448 */       v = v.add(volume);
/* 449 */       vp = vp.add(volume.multiply(price));
/*     */     }
/*     */ 
/* 453 */     if (v.setScale(2, RoundingMode.HALF_EVEN).compareTo(ZERRO) == 0) {
/* 454 */       return null;
/*     */     }
/*     */     BigDecimal result;
/*     */     try
/*     */     {
/* 460 */       result = vp.divide(v, RoundingMode.HALF_EVEN);
/*     */     } catch (Exception e) {
/* 462 */       LOGGER.error("volume is zero = " + v + " " + e.getMessage(), e);
/* 463 */       result = null;
/*     */     }
/* 465 */     return result;
/*     */   }
/*     */ 
/*     */   public void clear() {
/* 469 */     this.exposureMap.clear();
/* 470 */     fireTableDataChanged();
/*     */   }
/*     */ 
/*     */   private BigDecimal trim(BigDecimal value)
/*     */   {
/* 475 */     BigDecimal zero = new BigDecimal("0.0");
/*     */ 
/* 477 */     if (value.compareTo(zero) == 0) {
/* 478 */       return zero;
/*     */     }
/* 480 */     return value.stripTrailingZeros();
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  33 */     LOGGER = LoggerFactory.getLogger(ExposureTableModel.class);
/*     */ 
/*  47 */     ZERRO = new BigDecimal("0.00").setScale(2);
/*     */   }
/*     */   public class ExposureHolder { public String instrument;
/*     */     public int countL;
/*     */     public int countS;
/*     */     public BigDecimal amountL;
/*     */     public BigDecimal amountS;
/*     */     public Money profitLoss;
/*     */     public BigDecimal price;
/*  65 */     public List<Position> positions = new ArrayList();
/*     */     private boolean isSelected;
/*     */     private boolean isDisabled;
/*     */ 
/*  70 */     ExposureHolder(String instrument, int countL, int countS, BigDecimal amountL, BigDecimal amountS, BigDecimal price) { this.instrument = instrument;
/*  71 */       this.countL = countL;
/*  72 */       this.countS = countS;
/*  73 */       this.amountL = amountL;
/*  74 */       this.amountS = amountS;
/*  75 */       this.price = price;
/*  76 */       this.isSelected = (this.isDisabled = 0);
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/*  81 */       return this.instrument;
/*     */     }
/*     */ 
/*     */     public boolean isSelected() {
/*  85 */       return this.isSelected;
/*     */     }
/*     */ 
/*     */     public boolean setSelected(boolean isSelected) {
/*  89 */       return this.isSelected = isSelected;
/*     */     }
/*     */ 
/*     */     public boolean isDisabled() {
/*  93 */       return this.isDisabled;
/*     */     }
/*     */ 
/*     */     public boolean setDisabled(boolean isDisabled) {
/*  97 */       return this.isDisabled = isDisabled;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static enum UpdateMode
/*     */   {
/*  45 */     ADD, REMOVE, UPDATE;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.exposure.ExposureTableModel
 * JD-Core Version:    0.6.0
 */