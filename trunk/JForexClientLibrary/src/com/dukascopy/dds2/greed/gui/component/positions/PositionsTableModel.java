/*     */ package com.dukascopy.dds2.greed.gui.component.positions;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.dds2.calc.PriceUtil;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*     */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*     */ import com.dukascopy.dds2.greed.gui.component.AccountStatementPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.AccountStatementPanel.LabelType;
/*     */ import com.dukascopy.dds2.greed.gui.component.exposure.ExposurePanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.exposure.ExposureTable;
/*     */ import com.dukascopy.dds2.greed.gui.component.exposure.ExposureTableModel;
/*     */ import com.dukascopy.dds2.greed.gui.component.exposure.ExposureTableModel.UpdateMode;
/*     */ import com.dukascopy.dds2.greed.gui.component.status.GreedStatusBar;
/*     */ import com.dukascopy.dds2.greed.gui.component.table.PIP;
/*     */ import com.dukascopy.dds2.greed.gui.util.lotamount.LotAmountChanger;
/*     */ import com.dukascopy.dds2.greed.model.AccountStatement;
/*     */ import com.dukascopy.dds2.greed.model.CurrencyMarketWrapper;
/*     */ import com.dukascopy.dds2.greed.model.MarketStateWrapperListener;
/*     */ import com.dukascopy.dds2.greed.model.MarketView;
/*     */ import com.dukascopy.dds2.greed.util.CurrencyConverter;
/*     */ import com.dukascopy.dds2.greed.util.OrderUtils;
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.OrderDirection;
/*     */ import com.dukascopy.transport.common.model.type.OrderState;
/*     */ import com.dukascopy.transport.common.model.type.Position;
/*     */ import com.dukascopy.transport.common.model.type.PositionSide;
/*     */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*     */ import com.dukascopy.transport.common.msg.request.AccountInfoMessage;
/*     */ import com.dukascopy.transport.common.msg.request.CurrencyOffer;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.RoundingMode;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Currency;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.swing.table.AbstractTableModel;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class PositionsTableModel extends AbstractTableModel
/*     */   implements MarketStateWrapperListener
/*     */ {
/*     */   private static final Logger LOGGER;
/*     */   public static final int COLUMN_CHECK = 0;
/*     */   private static boolean isJForexRunning;
/*     */   public static int COLUMN_ID;
/*     */   public static int COLUMN_INSTRUMENT;
/*     */   public static int COLUMN_DIRECTION;
/*     */   public static int COLUMN_AMOUNT;
/*     */   public static int COLUMN_PRICE_OPEN;
/*     */   public static int COLUMN_PRICE_CURRENT;
/*     */   public static int COLUMN_STOP_LOSS;
/*     */   public static int COLUMN_TAKE_PROFIT;
/*     */   public static int COLUMN_PROFIT_LOSS_PIP;
/*     */   public static int COLUMN_PROFIT_LOSS;
/*     */   private static int COLUMN_COUNT;
/*     */   public static final int EXT_ID = 1;
/*  73 */   private final List<Position> positions = new ArrayList();
/*  74 */   private final Set<String> closedPositionIdSet = new HashSet();
/*  75 */   private final Map<String, Integer> positionsIndex = new HashMap();
/*  76 */   private final Map<String, OrderGroupMessage> closedPositions = new HashMap();
/*     */   private MarketView marketView;
/*     */   private CurrencyConverter converter;
/*     */   private AccountStatement accountStatement;
/*     */ 
/*     */   public Class<?> getColumnClass(int columnIndex)
/*     */   {
/*  89 */     Class columnClass = super.getColumnClass(columnIndex);
/*  90 */     if (columnIndex == 0)
/*  91 */       return Position.class;
/*  92 */     if (columnIndex == COLUMN_PROFIT_LOSS)
/*  93 */       return Money.class;
/*  94 */     if (columnIndex == COLUMN_PROFIT_LOSS_PIP) {
/*  95 */       return PIP.class;
/*     */     }
/*  97 */     return columnClass;
/*     */   }
/*     */ 
/*     */   public PositionsTableModel()
/*     */   {
/* 106 */     this.marketView = ((MarketView)GreedContext.get("marketView"));
/* 107 */     initState();
/*     */   }
/*     */ 
/*     */   public int getRowCount()
/*     */   {
/* 117 */     return this.positions.size();
/*     */   }
/*     */ 
/*     */   public int getColumnCount()
/*     */   {
/* 126 */     return COLUMN_COUNT;
/*     */   }
/*     */ 
/*     */   public Object getValueAt(int row, int column)
/*     */   {
/* 137 */     Position position = getPosition(row);
/* 138 */     if (null == position) {
/* 139 */       return null;
/*     */     }
/* 141 */     OrderGroupMessage orderGroup = position.getOrderGroup();
/*     */ 
/* 143 */     OrderMessage orderMessage = orderGroup.getOpeningOrder();
/* 144 */     String tag = null;
/* 145 */     if (orderMessage != null) {
/* 146 */       tag = orderMessage.getExternalSysId();
/*     */     }
/*     */     try
/*     */     {
/* 150 */       if (column == 0) {
/* 151 */         return position;
/*     */       }
/*     */ 
/* 154 */       if ((isJForexRunning) && 
/* 155 */         (column == 1)) {
/* 156 */         if (tag != null) {
/* 157 */           return tag;
/*     */         }
/* 159 */         return null;
/*     */       }
/*     */ 
/* 164 */       if (column == COLUMN_ID) {
/* 165 */         return GreedContext.getOrderGroupIdForView(position.getPositionID());
/*     */       }
/* 167 */       if (column == COLUMN_INSTRUMENT) {
/* 168 */         return position.getInstrument();
/*     */       }
/* 170 */       if (column == COLUMN_DIRECTION) {
/* 171 */         return position.getPositionSide();
/*     */       }
/* 173 */       if (column == COLUMN_AMOUNT) {
/* 174 */         return columnAmountValue(position);
/*     */       }
/* 176 */       if (column == COLUMN_PRICE_OPEN) {
/* 177 */         return columnPriceOpenValue(position);
/*     */       }
/* 179 */       if (column == COLUMN_PRICE_CURRENT) {
/* 180 */         return columnPriceCurrentValue(position);
/*     */       }
/* 182 */       if (column == COLUMN_STOP_LOSS) {
/* 183 */         return columnStopLossValue(orderGroup);
/*     */       }
/* 185 */       if (column == COLUMN_TAKE_PROFIT) {
/* 186 */         return columnTakeProfitValue(orderGroup);
/*     */       }
/* 188 */       if (column == COLUMN_PROFIT_LOSS_PIP) {
/* 189 */         return columnProfitLossPIPValue(position);
/*     */       }
/* 191 */       if (column == COLUMN_PROFIT_LOSS) {
/* 192 */         return columnProfitLossValue(position);
/*     */       }
/* 194 */       return null;
/*     */     }
/*     */     catch (Exception e) {
/* 197 */       LOGGER.error(e.getMessage(), e);
/* 198 */     }return "";
/*     */   }
/*     */ 
/*     */   private Object columnAmountValue(Position position)
/*     */   {
/* 203 */     BigDecimal divider = LotAmountChanger.getLotAmountForInstrument(Instrument.fromString(position.getInstrument()));
/*     */ 
/* 205 */     Money amount = position.getAmount();
/* 206 */     if (amount != null) {
/* 207 */       BigDecimal inMillions = amount.getValue().divide(divider, 6, RoundingMode.HALF_EVEN).stripTrailingZeros();
/* 208 */       return amount.getCurrency() + " " + inMillions.toPlainString();
/*     */     }
/* 210 */     return null;
/*     */   }
/*     */ 
/*     */   private Object columnPriceOpenValue(Position position)
/*     */   {
/* 215 */     Money po = position.getPriceOpen();
/* 216 */     return null != po ? po.getValue() : null;
/*     */   }
/*     */ 
/*     */   private Object columnPriceCurrentValue(Position position) {
/* 220 */     com.dukascopy.transport.common.model.type.OfferSide side = position.getPositionSide() == PositionSide.LONG ? com.dukascopy.transport.common.model.type.OfferSide.BID : com.dukascopy.transport.common.model.type.OfferSide.ASK;
/* 221 */     CurrencyOffer offer = this.marketView.getBestOffer(position.getInstrument(), side);
/* 222 */     if (offer != null) {
/* 223 */       return offer.getPrice().getValue();
/*     */     }
/* 225 */     return null;
/*     */   }
/*     */ 
/*     */   private Object columnStopLossValue(OrderGroupMessage orderGroup)
/*     */   {
/* 230 */     if (orderGroup != null) {
/* 231 */       OrderMessage order = orderGroup.getStopLossOrder();
/* 232 */       if ((order != null) && (!OrderState.CANCELLED.equals(order.getOrderState()))) {
/* 233 */         return order;
/*     */       }
/* 235 */       return null;
/*     */     }
/*     */ 
/* 238 */     return null;
/*     */   }
/*     */ 
/*     */   private Object columnTakeProfitValue(OrderGroupMessage orderGroup) {
/* 242 */     if (orderGroup != null) {
/* 243 */       OrderMessage order = orderGroup.getTakeProfitOrder();
/* 244 */       if ((order != null) && (!OrderState.CANCELLED.equals(order.getOrderState()))) {
/* 245 */         return order;
/*     */       }
/* 247 */       return null;
/*     */     }
/*     */ 
/* 250 */     return null;
/*     */   }
/*     */ 
/*     */   private Object columnProfitLossPIPValue(Position position)
/*     */   {
/* 255 */     BigDecimal priceOpen = null;
/* 256 */     if ((position.getPriceOpen() == null) || (position.getPriceOpen().getValue() == null)) {
/* 257 */       return "N/A";
/*     */     }
/* 259 */     priceOpen = position.getPriceOpen().getValue();
/*     */ 
/* 261 */     PositionSide positionSide = position.getPositionSide();
/* 262 */     com.dukascopy.transport.common.model.type.OfferSide oSide = positionSide == PositionSide.LONG ? com.dukascopy.transport.common.model.type.OfferSide.BID : com.dukascopy.transport.common.model.type.OfferSide.ASK;
/* 263 */     CurrencyOffer cOffer = this.marketView.getBestOffer(position.getInstrument(), oSide);
/*     */     BigDecimal priceCurr;
/* 264 */     if (cOffer != null)
/* 265 */       priceCurr = cOffer.getPrice().getValue();
/*     */     else
/* 267 */       return "N/A";
/*     */     BigDecimal priceCurr;
/* 269 */     int pipScale = Instrument.fromString(position.getInstrument()).getPipScale();
/* 270 */     BigDecimal result = PriceUtil.calculatePlPipsFromPriceDifference(priceCurr, priceOpen, positionSide, pipScale);
/* 271 */     return result;
/*     */   }
/*     */ 
/*     */   private Object columnProfitLossValue(Position position) {
/* 275 */     Money convertedProfitLoss = calculateProfitLoss(position);
/*     */ 
/* 277 */     Object result = null;
/* 278 */     if (convertedProfitLoss != null)
/* 279 */       result = convertedProfitLoss;
/*     */     else {
/* 281 */       result = "N/A";
/*     */     }
/*     */ 
/* 284 */     return result;
/*     */   }
/*     */ 
/*     */   private void initState() {
/* 288 */     if ((this.converter == null) || (this.accountStatement == null)) {
/* 289 */       this.converter = ((CurrencyConverter)GreedContext.get("currencyConverter"));
/* 290 */       this.accountStatement = ((AccountStatement)GreedContext.get("accountStatement"));
/*     */     }
/*     */   }
/*     */ 
/*     */   private void updatePositionsIndex(boolean modifyPositionsIndex)
/*     */   {
/* 300 */     if (modifyPositionsIndex) {
/* 301 */       this.positionsIndex.clear();
/*     */     }
/*     */ 
/* 304 */     int count = 0;
/*     */ 
/* 306 */     BigDecimal profitLoss = BigDecimal.ZERO; BigDecimal amount = BigDecimal.ZERO;
/* 307 */     int sideLong = 0; int sideShort = 0;
/* 308 */     for (Position position : this.positions) {
/* 309 */       if (modifyPositionsIndex) {
/* 310 */         this.positionsIndex.put(position.getPositionID(), Integer.valueOf(count++));
/*     */       }
/*     */ 
/* 313 */       if (position.getPositionSide().equals(PositionSide.LONG))
/* 314 */         sideLong++;
/*     */       else {
/* 316 */         sideShort++;
/*     */       }
/*     */ 
/* 319 */       if (null != position.getAmount()) {
/* 320 */         amount = amount.add(position.getAmount().getValue());
/*     */       }
/*     */ 
/* 323 */       if (null != position.getProfitLoss()) {
/* 324 */         profitLoss = profitLoss.add(position.getProfitLoss().getValue());
/*     */       }
/*     */     }
/* 327 */     CumulativePositionsInfo bag = new CumulativePositionsInfo(this.positions.size(), sideShort, sideLong, amount, profitLoss);
/*     */ 
/* 329 */     ClientForm clientForm = (ClientForm)GreedContext.get("clientGui");
/* 330 */     clientForm.getStatusBar().getAccountStatement().update(bag);
/*     */   }
/*     */ 
/*     */   public boolean isCellEditable(int rowIndex, int columnIndex)
/*     */   {
/* 396 */     Position position = getPosition(rowIndex);
/* 397 */     if (position.isDisabled()) {
/* 398 */       return false;
/*     */     }
/* 400 */     return columnIndex == 0;
/*     */   }
/*     */ 
/*     */   public void setValueAt(Object aValue, int rowIndex, int columnIndex)
/*     */   {
/* 412 */     if (0 == columnIndex) {
/* 413 */       Boolean checked = (Boolean)aValue;
/* 414 */       Position position = getPosition(rowIndex);
/* 415 */       if (position != null) {
/* 416 */         if (position.isDisabled()) {
/* 417 */           return;
/*     */         }
/* 419 */         if (checked.booleanValue())
/* 420 */           position.setSelected(true);
/*     */         else
/* 422 */           position.setSelected(false);
/*     */       }
/*     */       else {
/* 425 */         LOGGER.warn("position is null ");
/*     */       }
/*     */     } else {
/* 428 */       LOGGER.warn("bad column: " + columnIndex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public List<Position> getSelectedPositions()
/*     */   {
/* 438 */     List selected = new ArrayList();
/* 439 */     for (Position position : this.positions) {
/* 440 */       if ((position.isSelected()) && (!position.isDisabled())) {
/* 441 */         selected.add(position);
/*     */       }
/*     */     }
/* 444 */     return selected;
/*     */   }
/*     */ 
/*     */   public Position getPosition(int row)
/*     */   {
/* 454 */     assert ((row >= 0) && (row < this.positions.size()));
/* 455 */     GuiUtilsAndConstants.ensureEventDispatchThread();
/* 456 */     return (Position)this.positions.get(row);
/*     */   }
/*     */ 
/*     */   public Position getPosition(String orderGroupId)
/*     */   {
/* 465 */     GuiUtilsAndConstants.ensureEventDispatchThread();
/* 466 */     for (Position position : this.positions) {
/* 467 */       if (position.getPositionID().equals(orderGroupId)) {
/* 468 */         return position;
/*     */       }
/*     */     }
/* 471 */     return null;
/*     */   }
/*     */ 
/*     */   public void updatePositions(OrderGroupMessage orderGroup)
/*     */   {
/* 480 */     boolean enable = false;
/* 481 */     for (OrderMessage order : orderGroup.getOrders())
/*     */     {
/* 483 */       if ((OrderState.FILLED == order.getOrderState()) && (order.isOpening()) && 
/* 484 */         (this.closedPositionIdSet.contains(orderGroup.getOrderGroupId()))) {
/* 485 */         this.closedPositionIdSet.remove(orderGroup.getOrderGroupId());
/* 486 */         this.closedPositions.remove(orderGroup.getOrderGroupId());
/*     */       }
/*     */ 
/* 489 */       if ((OrderState.REJECTED == order.getOrderState()) || (OrderState.FILLED == order.getOrderState()))
/*     */       {
/* 491 */         enable = true;
/* 492 */         break;
/*     */       }
/*     */     }
/*     */ 
/* 496 */     Integer positionIndex = (Integer)this.positionsIndex.get(orderGroup.getOrderGroupId());
/*     */ 
/* 498 */     Position position = OrderUtils.calculatePositionModified(orderGroup);
/*     */ 
/* 500 */     ExposureTableModel.UpdateMode mode = ExposureTableModel.UpdateMode.UPDATE;
/*     */ 
/* 502 */     OrderMessage opening2 = orderGroup.getOpeningOrder();
/* 503 */     if ((opening2 != null) && (OrderState.EXECUTING.equals(opening2.getOrderState())) && (OrderDirection.OPEN.equals(opening2.getOrderDirection())) && (!opening2.isPlaceOffer()) && (positionIndex != null))
/*     */     {
/* 510 */       LOGGER.debug(orderGroup.getOrderGroupId() + ": ogm is skipped 1");
/* 511 */       return;
/*     */     }
/*     */ 
/* 522 */     boolean doUpdate = (position != null) && (position.getAmount().getValue().compareTo(BigDecimal.ZERO) > 0) && (!this.closedPositionIdSet.contains(orderGroup.getOrderGroupId()));
/*     */ 
/* 528 */     if (doUpdate)
/*     */     {
/* 530 */       if (positionIndex != null) {
/* 531 */         Position oldpos = (Position)this.positions.get(positionIndex.intValue());
/* 532 */         BigDecimal newAmount = position.getAmount().getValue();
/* 533 */         BigDecimal oldAmount = oldpos.getAmount().getValue();
/* 534 */         if (!oldAmount.equals(newAmount)) {
/* 535 */           position.setDisabled((!enable) && (oldpos.isDisabled()));
/* 536 */           position.setSelected((!enable) && (oldpos.isSelected()));
/* 537 */           position.setInClosingState((!enable) && (oldpos.isInClosingState()));
/*     */         }
/*     */ 
/* 540 */         if ((!GreedContext.isGlobal()) && (!GreedContext.isGlobalExtended())) {
/* 541 */           this.positions.set(positionIndex.intValue(), position);
/*     */         } else {
/* 543 */           Position oldPosition = (Position)this.positions.get(positionIndex.intValue());
/*     */ 
/* 545 */           oldPosition.setAmount(position.getAmount());
/* 546 */           oldPosition.setPriceOpen(position.getPriceOpen());
/* 547 */           oldPosition.setPositionSide(position.getPositionSide());
/* 548 */           oldPosition.setOrderGroup(orderGroup);
/*     */         }
/*     */ 
/* 551 */         updatePositionsIndex(false);
/* 552 */         fireTableRowsUpdated(positionIndex.intValue(), positionIndex.intValue());
/* 553 */         mode = ExposureTableModel.UpdateMode.UPDATE;
/*     */       }
/*     */       else {
/* 556 */         if (position.getProfitLoss() == null) {
/*     */           try {
/* 558 */             position.setProfitLoss(calculateProfitLosForNewPosition(position));
/*     */           }
/*     */           catch (Exception ex)
/*     */           {
/*     */           }
/*     */         }
/* 564 */         this.positions.add(position);
/* 565 */         Collections.sort(this.positions, new Comparator() {
/*     */           public int compare(Position o1, Position o2) {
/* 567 */             if ((o1 == null) || (o2 == null)) {
/* 568 */               return 0;
/*     */             }
/* 570 */             return o1.getPositionID().compareToIgnoreCase(o2.getPositionID());
/*     */           }
/*     */         });
/* 574 */         updatePositionsIndex(true);
/* 575 */         fireTableDataChanged();
/* 576 */         mode = ExposureTableModel.UpdateMode.ADD;
/*     */       }
/*     */ 
/*     */     }
/* 581 */     else if (positionIndex != null) {
/* 582 */       position = (Position)this.positions.get(positionIndex.intValue());
/* 583 */       this.positions.remove(positionIndex.intValue());
/* 584 */       updatePositionsIndex(true);
/* 585 */       fireTableRowsDeleted(positionIndex.intValue(), positionIndex.intValue());
/* 586 */       mode = ExposureTableModel.UpdateMode.REMOVE;
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 591 */       ExposureTableModel exposureTableModel = (ExposureTableModel)((ClientForm)GreedContext.get("clientGui")).getExposurePanel().getTable().getModel();
/* 592 */       exposureTableModel.updateExposure(position, mode);
/*     */     } catch (Exception e) {
/* 594 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private Money calculateProfitLosForNewPosition(Position position) throws NullPointerException {
/* 599 */     MarketView mv = (MarketView)GreedContext.get("marketView");
/*     */ 
/* 601 */     com.dukascopy.transport.common.model.type.OfferSide side = position.getPositionSide() == PositionSide.LONG ? com.dukascopy.transport.common.model.type.OfferSide.BID : com.dukascopy.transport.common.model.type.OfferSide.ASK;
/* 602 */     CurrencyOffer bestOffer = mv.getBestOffer(position.getInstrument(), side);
/*     */ 
/* 604 */     if (position.getPriceOpen() != null) {
/* 605 */       Money valueOpen = position.getPriceOpen().multiply(position.getAmount().getValue());
/* 606 */       Money valueClose = bestOffer.getPrice().multiply(position.getAmount().getValue());
/* 607 */       Money profitLoss = valueClose.subtract(valueOpen);
/* 608 */       if (position.getPositionSide() == PositionSide.SHORT) {
/* 609 */         profitLoss = new Money(profitLoss.getValue().negate(), profitLoss.getCurrency());
/*     */       }
/*     */ 
/* 612 */       return profitLoss;
/*     */     }
/*     */ 
/* 615 */     return null;
/*     */   }
/*     */ 
/*     */   public Set<String> getClosedPositionIdSet() {
/* 619 */     return this.closedPositionIdSet;
/*     */   }
/*     */ 
/*     */   public void onMarketState(CurrencyMarketWrapper marketState)
/*     */   {
/* 628 */     BigDecimal totalProfitLoss = BigDecimal.ZERO;
/* 629 */     for (Position position : this.positions) {
/* 630 */       if (position.getInstrument().equals(marketState.getInstrument())) {
/* 631 */         com.dukascopy.transport.common.model.type.OfferSide side = position.getPositionSide() == PositionSide.LONG ? com.dukascopy.transport.common.model.type.OfferSide.BID : com.dukascopy.transport.common.model.type.OfferSide.ASK;
/* 632 */         CurrencyOffer bestOffer = marketState.getBestOffer(side);
/* 633 */         if (bestOffer != null) {
/* 634 */           Money oldProfitLoss = position.getProfitLoss();
/* 635 */           position.setPriceCurrent(bestOffer.getPrice());
/*     */ 
/* 637 */           if ((position.getProfitLoss() == null) && (oldProfitLoss != null)) {
/* 638 */             position.setProfitLoss(oldProfitLoss);
/*     */           }
/* 640 */           Integer positionIndex = (Integer)this.positionsIndex.get(position.getPositionID());
/* 641 */           fireTableRowsUpdated(positionIndex.intValue(), positionIndex.intValue());
/*     */         }
/*     */       }
/* 644 */       Money posProfitLoss = calculateProfitLoss(position);
/* 645 */       if ((null != posProfitLoss) && (posProfitLoss.getValue() != null)) {
/* 646 */         totalProfitLoss = totalProfitLoss.add(posProfitLoss.getValue());
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 651 */     ClientForm clientForm = (ClientForm)GreedContext.get("clientGui");
/* 652 */     clientForm.getStatusBar().getAccountStatement().updateLabel(AccountStatementPanel.LabelType.PL, totalProfitLoss);
/*     */   }
/*     */ 
/*     */   public Money calculateProfitLoss(Position position) {
/* 656 */     if (position == null)
/* 657 */       return null;
/*     */     Currency accountCurrency;
/*     */     try {
/* 661 */       accountCurrency = this.accountStatement.getLastAccountState().getCurrency();
/*     */     } catch (NullPointerException e) {
/* 663 */       LOGGER.warn("Error while getting account currency");
/* 664 */       return null;
/*     */     }
/* 666 */     BigDecimal profitLoss = getProfitLoss(position);
/*     */ 
/* 668 */     com.dukascopy.api.OfferSide closeSide = position.getPositionSide() == PositionSide.LONG ? com.dukascopy.api.OfferSide.BID : com.dukascopy.api.OfferSide.ASK;
/* 669 */     if (profitLoss != null) {
/*     */       try {
/* 671 */         String instrument = position.getString("instrument");
/* 672 */         String sourceCurrencyString = instrument.substring(4);
/* 673 */         Currency sourceCurrency = Money.getCurrency(sourceCurrencyString);
/* 674 */         BigDecimal value = this.converter.convert(profitLoss, sourceCurrency, accountCurrency, closeSide);
/* 675 */         if (value != null) {
/* 676 */           return new Money(value, accountCurrency);
/*     */         }
/* 678 */         return null;
/*     */       }
/*     */       catch (IllegalStateException e) {
/* 681 */         return null;
/*     */       } catch (NullPointerException npe) {
/* 683 */         return null;
/*     */       }
/*     */     }
/* 686 */     return null;
/*     */   }
/*     */ 
/*     */   private BigDecimal getProfitLoss(Position position)
/*     */   {
/* 691 */     String profitLoss = position.getString("p_l");
/* 692 */     if (profitLoss != null) {
/* 693 */       return new BigDecimal(profitLoss);
/*     */     }
/* 695 */     return null;
/*     */   }
/*     */ 
/*     */   public List<Position> getPositions(String instrument)
/*     */   {
/* 706 */     GuiUtilsAndConstants.ensureEventDispatchThread();
/* 707 */     List result = new ArrayList();
/* 708 */     for (Position position : this.positions) {
/* 709 */       if (position.getInstrument().equals(instrument)) {
/* 710 */         result.add(position);
/*     */       }
/*     */     }
/* 713 */     return result;
/*     */   }
/*     */ 
/*     */   public List<Position> getPositions() {
/* 717 */     GuiUtilsAndConstants.ensureEventDispatchThread();
/* 718 */     return this.positions;
/*     */   }
/*     */ 
/*     */   public Position getLatestPosition(String instrument) {
/* 722 */     Date timestamp = new Date(0L);
/* 723 */     Position latestPosition = null;
/* 724 */     for (Position position : getPositions(instrument)) {
/* 725 */       if (position.getOrderGroup().getOpeningOrder().getTimestamp().after(timestamp)) {
/* 726 */         latestPosition = position;
/* 727 */         timestamp = position.getOrderGroup().getOpeningOrder().getTimestamp();
/*     */       }
/*     */     }
/* 730 */     return latestPosition;
/*     */   }
/*     */ 
/*     */   public void clear() {
/* 734 */     this.positions.clear();
/* 735 */     this.positionsIndex.clear();
/* 736 */     fireTableDataChanged();
/*     */   }
/*     */ 
/*     */   public static void reinitColumnIndexs() {
/* 740 */     isJForexRunning = GreedContext.isStrategyAllowed();
/*     */ 
/* 742 */     COLUMN_COUNT = isJForexRunning ? 12 : 11;
/*     */ 
/* 744 */     COLUMN_ID = isJForexRunning ? 2 : 1;
/* 745 */     COLUMN_INSTRUMENT = isJForexRunning ? 3 : 2;
/* 746 */     COLUMN_DIRECTION = isJForexRunning ? 4 : 3;
/* 747 */     COLUMN_AMOUNT = isJForexRunning ? 5 : 4;
/* 748 */     COLUMN_PRICE_OPEN = isJForexRunning ? 6 : 5;
/* 749 */     COLUMN_PRICE_CURRENT = isJForexRunning ? 7 : 6;
/* 750 */     COLUMN_STOP_LOSS = isJForexRunning ? 8 : 7;
/* 751 */     COLUMN_TAKE_PROFIT = isJForexRunning ? 9 : 8;
/* 752 */     COLUMN_PROFIT_LOSS_PIP = isJForexRunning ? 10 : 9;
/* 753 */     COLUMN_PROFIT_LOSS = isJForexRunning ? 11 : 10;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  53 */     LOGGER = LoggerFactory.getLogger(PositionsTableModel.class);
/*     */ 
/*  57 */     isJForexRunning = GreedContext.isStrategyAllowed();
/*     */ 
/*  59 */     COLUMN_ID = isJForexRunning ? 2 : 1;
/*  60 */     COLUMN_INSTRUMENT = isJForexRunning ? 3 : 2;
/*  61 */     COLUMN_DIRECTION = isJForexRunning ? 4 : 3;
/*  62 */     COLUMN_AMOUNT = isJForexRunning ? 5 : 4;
/*  63 */     COLUMN_PRICE_OPEN = isJForexRunning ? 6 : 5;
/*  64 */     COLUMN_PRICE_CURRENT = isJForexRunning ? 7 : 6;
/*  65 */     COLUMN_STOP_LOSS = isJForexRunning ? 8 : 7;
/*  66 */     COLUMN_TAKE_PROFIT = isJForexRunning ? 9 : 8;
/*  67 */     COLUMN_PROFIT_LOSS_PIP = isJForexRunning ? 10 : 9;
/*  68 */     COLUMN_PROFIT_LOSS = isJForexRunning ? 11 : 10;
/*  69 */     COLUMN_COUNT = isJForexRunning ? 12 : 11;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.positions.PositionsTableModel
 * JD-Core Version:    0.6.0
 */