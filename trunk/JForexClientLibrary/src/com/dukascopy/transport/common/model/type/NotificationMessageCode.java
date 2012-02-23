/*     */ package com.dukascopy.transport.common.model.type;
/*     */ 
/*     */ public enum NotificationMessageCode
/*     */ {
/*  10 */   SYSTEM_UNAVAILABLE("SYSTEM_UNAVAILABLE"), 
/*  11 */   ACCOUNT_BLOCKED("ACCOUNT_BLOCKED"), 
/*  12 */   ACCOUNT_BLOCKED_CLOSED("ACCOUNT_BLOCKED_CLOSED"), 
/*  13 */   MERGE_IN_PROGRESS("MERGE_IN_PROGRESS"), 
/*  14 */   NO_ORDER_FOUND("NO_ORDER_FOUND"), 
/*  15 */   VALIDATION_ERROR("VALIDATION_ERROR"), 
/*  16 */   UPDATE_ORDER_NOT_FOUND("UPDATE_ORDER_NOT_FOUND"), 
/*  17 */   ORDER_ALREADY_FILLED("ORDER_ALREADY_FILLED"), 
/*  18 */   ORDER_IN_EXEC_STATE("ORDER_IN_EXEC_STATE"), 
/*  19 */   ORDER_IN_ERROR_STATE("ORDER_IN_ERROR_STATE"), 
/*  20 */   ORDER_FILLED("ORDER_FILLED"), 
/*  21 */   ORDER_FILLED_PARTIALLY("ORDER_FILLED_PARTIALLY"), 
/*  22 */   REJECTED_COUNTERPARTY("REJECTED_COUNTERPARTY"), 
/*  23 */   REJECT_AND_RESUBMIT("REJECT_AND_RESUBMIT"), 
/*  24 */   MAX_SUBMIT_COUNT_REACHED("MAX_SUBMIT_COUNT_REACHED"), 
/*  25 */   MC_NOT_RESUBMIT("MC_NOT_RESUBMIT"), 
/*  26 */   REJECT_INVALID_ORDER("REJECT_INVALID_ORDER"), 
/*  27 */   REJECT_NO_MARGIN("REJECT_NO_MARGIN"), 
/*  28 */   FAILED_CANCEL_OFFER("FAILED_CANCEL_OFFER"), 
/*  29 */   FAILED_EDIT_OFFER("FAILED_EDIT_OFFER"), 
/*  30 */   REJECTED_MIN_OPEN_AMOUNT("REJECTED_MIN_OPEN_AMOUNT"), 
/*  31 */   AMOUNT_GREATER_REMAINING("AMOUNT_GREATER_REMAINING"), 
/*  32 */   UNSUFFICIENT_MARGIN("UNSUFFICIENT_MARGIN"), 
/*  33 */   CANCELING_OFFER_MC("CANCELING_OFFER_MC"), 
/*  34 */   EQUITY_LIMIT_REACHED("EQUITY_LIMIT_REACHED"), 
/*  35 */   OFFER_CANCELED("OFFER_CANCELED"), 
/*  36 */   OFFER_CANCELED_FILLED("OFFER_CANCELED_FILLED"), 
/*  37 */   OFFER_CANCELED_LEVERAGE("OFFER_CANCELED_LEVERAGE"), 
/*  38 */   ORDER_CANCELED("ORDER_CANCELED"), 
/*  39 */   SL_TP_DISABLED_NONHADGE("SL_TP_DISABLED_NONHADGE"), 
/*  40 */   AMOUNT_EMPTY_OR_ZERO("AMOUNT_EMPTY_OR_ZERO"), 
/*  41 */   PRICE_STOP_INVALID("PRICE_STOP_INVALID"), 
/*  42 */   STEP_LESS_3PIP("STEP_LESS_3PIP"), 
/*  43 */   PRICE_CLIENT_EMPTY("PRICE_CLIENT_EMPTY"), 
/*  44 */   AMOUNT_TO_LARGE("AMOUNT_TO_LARGE"), 
/*  45 */   INSTRUMENT_EMPTY("INSTRUMENT_EMPTY"), 
/*  46 */   ORDER_DIRECTION_EMPTY("ORDER_DIRECTION_EMPTY"), 
/*  47 */   ORDER_STATE_EMPTY("ORDER_STATE_EMPTY"), 
/*  48 */   ORDER_SIDE_EMPTY("ORDER_SIDE_EMPTY"), 
/*  49 */   INVALID_ORDER("INVALID_ORDER"), 
/*  50 */   GROUP_MISSED("GROUP_MISSED"), 
/*  51 */   ORDER_NOT_FOUND("ORDER_NOT_FOUND"), 
/*  52 */   ACCOUNT_DISABLED("ACCOUNT_DISABLED"), 
/*  53 */   MAS_CLOSE_FAIL_BAD_ORDER("MAS_CLOSE_FAIL_BAD_ORDER"), 
/*  54 */   UNSUFF_MARGIN_ON_CLOSE("UNSUFF_MARGIN_ON_CLOSE"), 
/*  55 */   POSITION_MERGED_CLOSED("POSITION_MERGED_CLOSED"), 
/*  56 */   POSITION_MERGED_TO("POSITION_MERGED_TO"), 
/*  57 */   POSITION_MERGE_FAILED("POSITION_MERGE_FAILED"), 
/*  58 */   ACCOUNT_SETTING_SAVE_FAIL("ACCOUNT_SETTING_SAVE_FAIL"), 
/*  59 */   STRING_MESSAGE("STRING_MESSAGE"), 
/*  60 */   OCO_GROUPED("OCO_GROUPED"), 
/*  61 */   OCO_UNGROUPED("OCO_UNGROUPED"), 
/*  62 */   MC_MARGIN_CUT_WEEKEND("MC_MARGIN_CUT_WEEKEND"), 
/*  63 */   MC_MARGIN_CUT("MC_MARGIN_CUT"), 
/*  64 */   ACCOUNT_STATUS_DISABLED("ACCOUNT_STATUS_DISABLED"), 
/*  65 */   POSITION_LIMIT_REACHED("POSITION_LIMIT_REACHED"), 
/*  66 */   NEWS_REQUEST_LIMIT_EXCEEDED("NEWS_REQUEST_LIMIT_EXCEEDED"), 
/*  67 */   MC_CLOSING("MC_CLOSING");
/*     */ 
/*     */   private String value;
/*     */ 
/*  72 */   private NotificationMessageCode(String value) { this.value = value; }
/*     */ 
/*     */   public String asString()
/*     */   {
/*  76 */     return this.value;
/*     */   }
/*     */ 
/*     */   public static NotificationMessageCode fromString(String value) {
/*  80 */     if (SYSTEM_UNAVAILABLE.asString().equals(value))
/*  81 */       return SYSTEM_UNAVAILABLE;
/*  82 */     if (ACCOUNT_BLOCKED.asString().equals(value))
/*  83 */       return ACCOUNT_BLOCKED;
/*  84 */     if (MERGE_IN_PROGRESS.asString().equals(value))
/*  85 */       return MERGE_IN_PROGRESS;
/*  86 */     if (NO_ORDER_FOUND.asString().equals(value))
/*  87 */       return NO_ORDER_FOUND;
/*  88 */     if (VALIDATION_ERROR.asString().equals(value))
/*  89 */       return VALIDATION_ERROR;
/*  90 */     if (UPDATE_ORDER_NOT_FOUND.asString().equals(value))
/*  91 */       return UPDATE_ORDER_NOT_FOUND;
/*  92 */     if (ORDER_ALREADY_FILLED.asString().equals(value))
/*  93 */       return ORDER_ALREADY_FILLED;
/*  94 */     if (ORDER_IN_EXEC_STATE.asString().equals(value))
/*  95 */       return ORDER_IN_EXEC_STATE;
/*  96 */     if (ORDER_IN_ERROR_STATE.asString().equals(value))
/*  97 */       return ORDER_IN_ERROR_STATE;
/*  98 */     if (ORDER_FILLED.asString().equals(value))
/*  99 */       return ORDER_FILLED;
/* 100 */     if (ORDER_FILLED_PARTIALLY.asString().equals(value))
/* 101 */       return ORDER_FILLED_PARTIALLY;
/* 102 */     if (REJECTED_COUNTERPARTY.asString().equals(value))
/* 103 */       return REJECTED_COUNTERPARTY;
/* 104 */     if (REJECT_AND_RESUBMIT.asString().equals(value))
/* 105 */       return REJECT_AND_RESUBMIT;
/* 106 */     if (MAX_SUBMIT_COUNT_REACHED.asString().equals(value))
/* 107 */       return MAX_SUBMIT_COUNT_REACHED;
/* 108 */     if (MC_NOT_RESUBMIT.asString().equals(value))
/* 109 */       return MC_NOT_RESUBMIT;
/* 110 */     if (REJECT_INVALID_ORDER.asString().equals(value))
/* 111 */       return REJECT_INVALID_ORDER;
/* 112 */     if (REJECT_NO_MARGIN.asString().equals(value))
/* 113 */       return REJECT_NO_MARGIN;
/* 114 */     if (FAILED_CANCEL_OFFER.asString().equals(value))
/* 115 */       return FAILED_CANCEL_OFFER;
/* 116 */     if (FAILED_EDIT_OFFER.asString().equals(value))
/* 117 */       return FAILED_EDIT_OFFER;
/* 118 */     if (REJECTED_MIN_OPEN_AMOUNT.asString().equals(value))
/* 119 */       return REJECTED_MIN_OPEN_AMOUNT;
/* 120 */     if (AMOUNT_GREATER_REMAINING.asString().equals(value))
/* 121 */       return AMOUNT_GREATER_REMAINING;
/* 122 */     if (UNSUFFICIENT_MARGIN.asString().equals(value))
/* 123 */       return UNSUFFICIENT_MARGIN;
/* 124 */     if (CANCELING_OFFER_MC.asString().equals(value))
/* 125 */       return CANCELING_OFFER_MC;
/* 126 */     if (EQUITY_LIMIT_REACHED.asString().equals(value))
/* 127 */       return EQUITY_LIMIT_REACHED;
/* 128 */     if (OFFER_CANCELED.asString().equals(value))
/* 129 */       return OFFER_CANCELED;
/* 130 */     if (OFFER_CANCELED_FILLED.asString().equals(value))
/* 131 */       return OFFER_CANCELED_FILLED;
/* 132 */     if (OFFER_CANCELED_LEVERAGE.asString().equals(value))
/* 133 */       return OFFER_CANCELED_LEVERAGE;
/* 134 */     if (ORDER_CANCELED.asString().equals(value))
/* 135 */       return ORDER_CANCELED;
/* 136 */     if (SL_TP_DISABLED_NONHADGE.asString().equals(value))
/* 137 */       return SL_TP_DISABLED_NONHADGE;
/* 138 */     if (AMOUNT_EMPTY_OR_ZERO.asString().equals(value))
/* 139 */       return AMOUNT_EMPTY_OR_ZERO;
/* 140 */     if (PRICE_STOP_INVALID.asString().equals(value))
/* 141 */       return PRICE_STOP_INVALID;
/* 142 */     if (STEP_LESS_3PIP.asString().equals(value))
/* 143 */       return STEP_LESS_3PIP;
/* 144 */     if (AMOUNT_TO_LARGE.asString().equals(value))
/* 145 */       return AMOUNT_TO_LARGE;
/* 146 */     if (INSTRUMENT_EMPTY.asString().equals(value))
/* 147 */       return INSTRUMENT_EMPTY;
/* 148 */     if (ORDER_DIRECTION_EMPTY.asString().equals(value))
/* 149 */       return ORDER_DIRECTION_EMPTY;
/* 150 */     if (ORDER_STATE_EMPTY.asString().equals(value))
/* 151 */       return ORDER_STATE_EMPTY;
/* 152 */     if (ORDER_SIDE_EMPTY.asString().equals(value))
/* 153 */       return ORDER_SIDE_EMPTY;
/* 154 */     if (INVALID_ORDER.asString().equals(value))
/* 155 */       return INVALID_ORDER;
/* 156 */     if (GROUP_MISSED.asString().equals(value))
/* 157 */       return GROUP_MISSED;
/* 158 */     if (ORDER_NOT_FOUND.asString().equals(value))
/* 159 */       return ORDER_NOT_FOUND;
/* 160 */     if (ACCOUNT_DISABLED.asString().equals(value))
/* 161 */       return ACCOUNT_DISABLED;
/* 162 */     if (MAS_CLOSE_FAIL_BAD_ORDER.asString().equals(value))
/* 163 */       return MAS_CLOSE_FAIL_BAD_ORDER;
/* 164 */     if (UNSUFF_MARGIN_ON_CLOSE.asString().equals(value))
/* 165 */       return UNSUFF_MARGIN_ON_CLOSE;
/* 166 */     if (POSITION_MERGED_CLOSED.asString().equals(value))
/* 167 */       return POSITION_MERGED_CLOSED;
/* 168 */     if (POSITION_MERGED_TO.asString().equals(value))
/* 169 */       return POSITION_MERGED_TO;
/* 170 */     if (POSITION_MERGE_FAILED.asString().equals(value))
/* 171 */       return POSITION_MERGE_FAILED;
/* 172 */     if (ACCOUNT_SETTING_SAVE_FAIL.asString().equals(value))
/* 173 */       return ACCOUNT_SETTING_SAVE_FAIL;
/* 174 */     if (STRING_MESSAGE.asString().equals(value))
/* 175 */       return STRING_MESSAGE;
/* 176 */     if (OCO_GROUPED.asString().equals(value))
/* 177 */       return OCO_GROUPED;
/* 178 */     if (OCO_UNGROUPED.asString().equals(value))
/* 179 */       return OCO_UNGROUPED;
/* 180 */     if (MC_MARGIN_CUT_WEEKEND.asString().equals(value))
/* 181 */       return MC_MARGIN_CUT_WEEKEND;
/* 182 */     if (MC_MARGIN_CUT.asString().equals(value))
/* 183 */       return MC_MARGIN_CUT;
/* 184 */     if (ACCOUNT_STATUS_DISABLED.asString().equals(value))
/* 185 */       return ACCOUNT_STATUS_DISABLED;
/* 186 */     if (ACCOUNT_BLOCKED_CLOSED.asString().equals(value))
/* 187 */       return ACCOUNT_BLOCKED_CLOSED;
/* 188 */     if (PRICE_CLIENT_EMPTY.asString().equals(value))
/* 189 */       return PRICE_CLIENT_EMPTY;
/* 190 */     if (POSITION_LIMIT_REACHED.asString().equals(value))
/* 191 */       return POSITION_LIMIT_REACHED;
/* 192 */     if (NEWS_REQUEST_LIMIT_EXCEEDED.asString().equals(value))
/* 193 */       return NEWS_REQUEST_LIMIT_EXCEEDED;
/* 194 */     if (MC_CLOSING.asString().equals(value)) {
/* 195 */       return MC_CLOSING;
/*     */     }
/* 197 */     throw new IllegalArgumentException("Invalid notification message code: " + value);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.model.type.NotificationMessageCode
 * JD-Core Version:    0.6.0
 */