/*      */ package com.dukascopy.transport.common.msg.request;
/*      */ 
/*      */ import com.dukascopy.transport.common.model.type.AccountState;
/*      */ import com.dukascopy.transport.common.model.type.GlobalAccountType;
/*      */ import com.dukascopy.transport.common.model.type.Money;
/*      */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*      */ import java.math.BigDecimal;
/*      */ import java.text.ParseException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Currency;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.StringTokenizer;
/*      */ import org.json.JSONArray;
/*      */ import org.json.JSONObject;
/*      */ 
/*      */ public class AccountInfoMessage extends ProtocolMessage
/*      */ {
/*      */   public static final String TYPE = "accountInfo";
/*      */   public static final String NAME = "name";
/*      */   public static final String LEVERAGE = "leverage";
/*      */   public static final String STATE = "state";
/*      */   public static final String CURRENCY = "currency";
/*      */   public static final String BALANCE = "balance";
/*      */   public static final String EQUITY = "equity";
/*      */   public static final String BASE_EQUITY = "b_equity";
/*      */   public static final String USABLE_MARGIN = "usableMargin";
/*      */   public static final String REPORTS = "reports";
/*      */   public static final String GLOBAL = "global";
/*      */   public static final String USE_EXTERNAL = "use_external";
/*      */   public static final String CLIENT_SETTINGS = "client_settings";
/*      */   public static final String IS_EXECUTOR = "executor";
/*      */   public static final String POSITION_IDS = "pos_ids";
/*      */   public static final String MIN_AMOUNT = "min_amount";
/*      */   public static final String WEEKEND_LEVERAGE = "weekend_leverage";
/*      */   public static final String FUND_ACCOUNT_ID = "facct_id";
/*      */   public static final String EXECUTOR_BLACKLIST = "execBlacklist";
/*      */   public static final String MC_EQUITY_LIMIT = "equityLimit";
/*      */   public static final String MC_LEVERAGE_USE = "mcLevUse";
/*      */   public static final String FUND_RATIO = "fundRatio";
/*      */   public static final String IS_MINIFX = "is_minifx";
/*      */   public static final String MAX_AMOUNT = "max_amount";
/*      */   public static final String SP_VINER_OPEN = "spVinerOpen";
/*      */   public static final String SP_VINER_CLOSE = "spVinerClose";
/*      */   public static final String RECORD_COUNT = "recCount";
/*      */   public static final String COM_VOL = "cvol";
/*      */   public static final String COM_PIP = "cpip";
/*      */   public static final String COM_PIP_PERC = "cpipp";
/*      */   public static final String SD_EXECUTION_DELAY = "sdDelay";
/*      */   public static final String SD_TRAILING_USD = "sdSleepage";
/*      */   public static final String CLIENTS = "clients";
/*      */   public static final String GAT = "gat";
/*      */   public static final String FEED_COMMISSION = "fcomm";
/*      */   public static final String MAX_GROUPS = "maxgr";
/*      */   public static final String MC_MODE = "mcMode";
/*      */   public static final String MARGIN_MODE = "marginMode";
/*      */   public static final String PRIME_BROKER = "pBroker";
/*      */   public static final String EXECUTION_MODE = "execMode";
/*      */   public static final String CLIENTIDS = "clientIds";
/*      */   public static final String FXCM_ACCOUNT_RESET = "fxcmAccReset";
/*      */   public static final String ACCOUNT_PROPERTIES = "props";
/*      */   public static final String EXECUTOR_TYPE = "exec_type";
/*      */   public static final String WL_FUND = "wlFund";
/*      */   public static final String WL_IS_FILTER = "wlFilter";
/*      */   public static final String ALLOW_SDEX = "allowSdx";
/*      */   public static final String MAX_ORDERS = "maxord";
/*      */   public static final String PL = "pl";
/*      */   public static final String COMM = "comm";
/*      */   public static final String WL_PARTNER_ID = "wlPart";
/*      */   public static final String WL_TIMES = "wlTimes";
/*      */   public static final String FEED_COMMISSION_MAP_ID = "fcMapId";
/*      */   public static final String FEED_COMMISSION_MAP = "fcMap";
/*      */   public static final String ORDER_GROUP_ID_PREFIX = "grIdPref";
/*      */ 
/*      */   public AccountInfoMessage()
/*      */   {
/*   91 */     setType("accountInfo");
/*      */   }
/*      */ 
/*      */   public AccountInfoMessage(ProtocolMessage message)
/*      */   {
/*  100 */     super(message);
/*  101 */     setType("accountInfo");
/*  102 */     put("name", message.getString("name"));
/*  103 */     put("balance", message.getString("balance"));
/*  104 */     put("currency", message.getString("currency"));
/*  105 */     put("equity", message.getString("equity"));
/*  106 */     put("usableMargin", message.getString("usableMargin"));
/*  107 */     put("leverage", message.getString("leverage"));
/*  108 */     put("state", message.getString("state"));
/*  109 */     put("reports", message.getJSONArray("reports"));
/*  110 */     put("global", message.getBool("global"));
/*  111 */     put("use_external", message.getBool("use_external"));
/*  112 */     put("client_settings", message.getJSONArray("client_settings"));
/*  113 */     put("executor", message.getBool("executor"));
/*  114 */     put("pos_ids", message.getString("pos_ids"));
/*  115 */     put("min_amount", message.getString("min_amount"));
/*  116 */     put("weekend_leverage", message.getString("weekend_leverage"));
/*  117 */     put("facct_id", message.getString("facct_id"));
/*  118 */     put("execBlacklist", message.getJSONArray("execBlacklist"));
/*  119 */     put("equityLimit", message.getString("equityLimit"));
/*  120 */     put("mcLevUse", message.getString("mcLevUse"));
/*  121 */     put("fundRatio", message.getString("fundRatio"));
/*  122 */     put("is_minifx", message.getBool("is_minifx"));
/*  123 */     put("max_amount", message.getString("max_amount"));
/*  124 */     put("cvol", message.getString("cvol"));
/*  125 */     put("cpip", message.getString("cpip"));
/*  126 */     put("cpipp", message.getString("cpipp"));
/*  127 */     put("sdDelay", message.getString("sdDelay"));
/*  128 */     put("sdSleepage", message.getString("sdSleepage"));
/*  129 */     put("clients", message.getJSONArray("clients"));
/*  130 */     put("gat", message.getString("gat"));
/*  131 */     put("b_equity", message.getString("b_equity"));
/*  132 */     put("spVinerOpen", message.getString("spVinerOpen"));
/*  133 */     put("spVinerClose", message.getString("spVinerClose"));
/*  134 */     put("recCount", message.getString("recCount"));
/*  135 */     put("fcomm", message.getString("fcomm"));
/*  136 */     put("maxgr", message.getString("maxgr"));
/*  137 */     put("mcMode", message.getString("mcMode"));
/*  138 */     put("marginMode", message.getString("marginMode"));
/*  139 */     put("pBroker", message.getJSONArray("pBroker"));
/*  140 */     put("execMode", message.getString("execMode"));
/*  141 */     put("clientIds", message.getString("clientIds"));
/*  142 */     put("fxcmAccReset", message.getString("fxcmAccReset"));
/*  143 */     put("props", message.getJSONObject("props"));
/*  144 */     put("exec_type", message.getInteger("exec_type"));
/*  145 */     put("wlFund", message.getString("wlFund"));
/*  146 */     put("wlFilter", message.getBool("wlFilter"));
/*  147 */     put("allowSdx", message.getBool("allowSdx"));
/*  148 */     put("maxord", message.getString("maxord"));
/*  149 */     put("pl", message.getString("pl"));
/*  150 */     put("comm", message.getString("comm"));
/*  151 */     put("wlPart", message.getString("wlPart"));
/*  152 */     put("fcMapId", message.getString("fcMapId"));
/*  153 */     put("fcMap", message.getJSONObject("fcMap"));
/*  154 */     put("wlTimes", message.getString("wlTimes"));
/*  155 */     put("grIdPref", message.getString("grIdPref"));
/*      */   }
/*      */ 
/*      */   public AccountInfoMessage(AccountInfoMessage message, boolean copySettings)
/*      */   {
/*  160 */     super(message);
/*  161 */     setType("accountInfo");
/*  162 */     put("name", message.getString("name"));
/*  163 */     put("balance", message.getString("balance"));
/*  164 */     put("currency", message.getString("currency"));
/*  165 */     put("equity", message.getString("equity"));
/*  166 */     put("b_equity", message.getString("b_equity"));
/*  167 */     put("usableMargin", message.getString("usableMargin"));
/*  168 */     put("leverage", message.getString("leverage"));
/*  169 */     put("state", message.getString("state"));
/*  170 */     put("global", message.getBool("global"));
/*  171 */     put("fcomm", message.getString("fcomm"));
/*  172 */     put("pl", message.getString("pl"));
/*  173 */     put("comm", message.getString("comm"));
/*  174 */     if (copySettings) {
/*  175 */       put("reports", message.getJSONArray("reports"));
/*  176 */       put("client_settings", message.getJSONArray("client_settings"));
/*  177 */       put("executor", message.getBool("executor"));
/*  178 */       put("pos_ids", message.getString("pos_ids"));
/*  179 */       put("cvol", message.getString("cvol"));
/*  180 */       put("cpip", message.getString("cpip"));
/*  181 */       put("cpipp", message.getString("cpipp"));
/*      */ 
/*  184 */       put("is_minifx", message.getBool("is_minifx"));
/*  185 */       put("min_amount", message.getString("min_amount"));
/*  186 */       put("max_amount", message.getString("max_amount"));
/*  187 */       put("equityLimit", message.getString("equityLimit"));
/*  188 */       put("mcLevUse", message.getString("mcLevUse"));
/*  189 */       put("weekend_leverage", message.getString("weekend_leverage"));
/*  190 */       put("use_external", message.getBool("use_external"));
/*  191 */       put("maxgr", message.getString("maxgr"));
/*  192 */       put("mcMode", message.getString("mcMode"));
/*  193 */       put("marginMode", message.getString("marginMode"));
/*  194 */       put("execMode", message.getString("execMode"));
/*      */ 
/*  201 */       put("clientIds", message.getString("clientIds"));
/*  202 */       put("fxcmAccReset", message.getString("fxcmAccReset"));
/*  203 */       put("exec_type", message.getInteger("exec_type"));
/*  204 */       put("maxord", message.getString("maxord"));
/*  205 */       put("wlPart", message.getString("wlPart"));
/*  206 */       put("fcMapId", message.getString("fcMapId"));
/*  207 */       put("fcMap", message.getJSONObject("fcMap"));
/*  208 */       put("wlTimes", message.getString("wlTimes"));
/*  209 */       put("grIdPref", message.getString("grIdPref"));
/*      */     }
/*      */   }
/*      */ 
/*      */   public AccountInfoMessage(String s)
/*      */     throws ParseException
/*      */   {
/*  222 */     super(s);
/*  223 */     setType("accountInfo");
/*      */   }
/*      */ 
/*      */   public AccountInfoMessage(JSONObject s) throws ParseException {
/*  227 */     super(s);
/*  228 */     setType("accountInfo");
/*      */   }
/*      */ 
/*      */   public BigDecimal getMinAmount()
/*      */   {
/*  238 */     String amountString = getString("min_amount");
/*  239 */     if (amountString != null) {
/*  240 */       return new BigDecimal(amountString).multiply(ONE_MILLION);
/*      */     }
/*  242 */     return null;
/*      */   }
/*      */ 
/*      */   public void setMinAmount(BigDecimal amount)
/*      */   {
/*  252 */     put("min_amount", amount.divide(ONE_MILLION).toPlainString());
/*      */   }
/*      */ 
/*      */   public BigDecimal getMaxAmount()
/*      */   {
/*  261 */     String amountString = getString("max_amount");
/*  262 */     if (amountString != null) {
/*  263 */       return new BigDecimal(amountString).multiply(ONE_MILLION);
/*      */     }
/*  265 */     return null;
/*      */   }
/*      */ 
/*      */   public void setMaxAmount(BigDecimal amount)
/*      */   {
/*  275 */     put("max_amount", amount.divide(ONE_MILLION).toPlainString());
/*      */   }
/*      */ 
/*      */   public String getName()
/*      */   {
/*  284 */     return getString("name");
/*      */   }
/*      */ 
/*      */   public void setName(String name)
/*      */   {
/*  293 */     put("name", name);
/*      */   }
/*      */ 
/*      */   public void setGlobal(boolean isGlobal)
/*      */   {
/*  302 */     put("global", isGlobal);
/*      */   }
/*      */ 
/*      */   public boolean isGlobal()
/*      */   {
/*  311 */     return getBool("global").booleanValue();
/*      */   }
/*      */ 
/*      */   public GlobalAccountType getGAT()
/*      */   {
/*  320 */     String gatStr = getString("gat");
/*  321 */     if (gatStr != null) {
/*  322 */       return GlobalAccountType.fromString(gatStr);
/*      */     }
/*  324 */     return null;
/*      */   }
/*      */ 
/*      */   public void setGAT(GlobalAccountType gat)
/*      */   {
/*  334 */     put("gat", gat.asString());
/*      */   }
/*      */ 
/*      */   public void setIsExecutor(boolean isExecutor)
/*      */   {
/*  343 */     put("executor", isExecutor);
/*      */   }
/*      */ 
/*      */   public boolean isExecutor()
/*      */   {
/*  352 */     return getBool("executor").booleanValue();
/*      */   }
/*      */ 
/*      */   public void setIsMiniFx(boolean isMiniFx)
/*      */   {
/*  362 */     put("is_minifx", isMiniFx);
/*      */   }
/*      */ 
/*      */   public boolean isMiniFx()
/*      */   {
/*  371 */     return getBool("is_minifx").booleanValue();
/*      */   }
/*      */ 
/*      */   public void setIsUsingExternal(boolean isUsingExternal)
/*      */   {
/*  380 */     put("use_external", isUsingExternal);
/*      */   }
/*      */ 
/*      */   public boolean isUsingExternal()
/*      */   {
/*  389 */     return getBool("use_external").booleanValue();
/*      */   }
/*      */ 
/*      */   public Currency getCurrency()
/*      */   {
/*  398 */     String currString = getString("currency");
/*  399 */     if (currString != null) {
/*  400 */       return Money.getCurrency(currString);
/*      */     }
/*  402 */     return null;
/*      */   }
/*      */ 
/*      */   public Money getBalance()
/*      */   {
/*  413 */     String balanceString = getString("balance");
/*  414 */     if ((balanceString != null) && (getCurrency() != null)) {
/*  415 */       return new Money(balanceString, getCurrency().getCurrencyCode());
/*      */     }
/*  417 */     return null;
/*      */   }
/*      */ 
/*      */   public void setBalance(Money balance)
/*      */   {
/*  427 */     put("balance", balance.getValue().toPlainString());
/*  428 */     put("currency", balance.getCurrency().getCurrencyCode());
/*      */   }
/*      */ 
/*      */   public Money getEquity()
/*      */   {
/*  460 */     String equityString = getString("equity");
/*  461 */     if ((equityString != null) && (getCurrency() != null)) {
/*  462 */       return new Money(equityString, getCurrency().getCurrencyCode());
/*      */     }
/*  464 */     return null;
/*      */   }
/*      */ 
/*      */   public void setEquity(Money equity)
/*      */   {
/*  474 */     put("equity", equity.getValue().toPlainString());
/*      */   }
/*      */ 
/*      */   public void setBaseEquity(Money equity)
/*      */   {
/*  483 */     put("b_equity", equity.getValue().toPlainString());
/*      */   }
/*      */ 
/*      */   public Money getBaseEquity()
/*      */   {
/*  492 */     String equityString = getString("b_equity");
/*  493 */     if ((equityString != null) && (getCurrency() != null)) {
/*  494 */       return new Money(equityString, getCurrency().getCurrencyCode());
/*      */     }
/*  496 */     return null;
/*      */   }
/*      */ 
/*      */   public Money getUsableMargin()
/*      */   {
/*  507 */     String marginString = getString("usableMargin");
/*  508 */     if ((marginString != null) && (getCurrency() != null)) {
/*  509 */       return new Money(marginString, getCurrency().getCurrencyCode());
/*      */     }
/*  511 */     return null;
/*      */   }
/*      */ 
/*      */   public void setUsableMargin(Money margin)
/*      */   {
/*  521 */     put("usableMargin", margin.getValue().toPlainString());
/*      */   }
/*      */ 
/*      */   public Integer getLeverage()
/*      */   {
/*  530 */     return getInteger("leverage");
/*      */   }
/*      */ 
/*      */   public void setLeverage(Integer leverage)
/*      */   {
/*  539 */     put("leverage", leverage);
/*      */   }
/*      */ 
/*      */   public Integer getWeekendLeverage()
/*      */   {
/*  548 */     String mwm = getString("weekend_leverage");
/*  549 */     if (mwm == null) {
/*  550 */       return null;
/*      */     }
/*  552 */     return Integer.valueOf(Integer.parseInt(mwm));
/*      */   }
/*      */ 
/*      */   public void setWeekendLeverage(Integer maxMargin)
/*      */   {
/*  561 */     String set = null;
/*  562 */     if (maxMargin != null) {
/*  563 */       set = maxMargin.toString();
/*      */     }
/*  565 */     put("weekend_leverage", set);
/*      */   }
/*      */ 
/*      */   public Integer getMcLeverageUse()
/*      */   {
/*  575 */     String mwm = getString("mcLevUse");
/*  576 */     if (mwm == null) {
/*  577 */       return Integer.valueOf(200);
/*      */     }
/*  579 */     return Integer.valueOf(Integer.parseInt(mwm));
/*      */   }
/*      */ 
/*      */   public void setMcLeverageUse(Integer lev)
/*      */   {
/*  588 */     String set = null;
/*  589 */     if (lev != null) {
/*  590 */       set = lev.toString();
/*      */     }
/*  592 */     put("mcLevUse", set);
/*      */   }
/*      */ 
/*      */   public AccountState getAccountState()
/*      */   {
/*  601 */     String stateString = getString("state");
/*  602 */     if (stateString != null) {
/*  603 */       return AccountState.fromString(stateString);
/*      */     }
/*  605 */     return null;
/*      */   }
/*      */ 
/*      */   public void setAccountState(AccountState state)
/*      */   {
/*  615 */     put("state", state.asString());
/*      */   }
/*      */ 
/*      */   public List<ReportInfo> getReports()
/*      */   {
/*  624 */     JSONArray a = getJSONArray("reports");
/*      */ 
/*  626 */     if (a == null) {
/*  627 */       return new ArrayList(0);
/*      */     }
/*  629 */     List set = new ArrayList(a.length());
/*  630 */     for (int i = 0; i < a.length(); i++) {
/*  631 */       ReportInfo report = (ReportInfo)ProtocolMessage.parse(a.getString(i));
/*  632 */       set.add(report);
/*      */     }
/*      */ 
/*  635 */     return set;
/*      */   }
/*      */ 
/*      */   public void setReports(List<ReportInfo> reports)
/*      */   {
/*  644 */     JSONArray a = new JSONArray();
/*      */ 
/*  646 */     for (ReportInfo report : reports) {
/*  647 */       a.put(report);
/*      */     }
/*      */ 
/*  650 */     put("reports", a);
/*      */   }
/*      */ 
/*      */   public Map getClientSettings()
/*      */   {
/*  659 */     JSONArray a = getJSONArray("client_settings");
/*  660 */     Map map = new HashMap();
/*      */ 
/*  662 */     if (a != null) {
/*  663 */       for (int i = 0; i < a.length(); i++) {
/*  664 */         MapWorkaround mw = null;
/*      */         try {
/*  666 */           mw = new MapWorkaround(new ProtocolMessage(a.getString(i)));
/*      */         } catch (ParseException e) {
/*  668 */           e.printStackTrace();
/*  669 */           return null;
/*      */         }
/*  671 */         if (mw == null) {
/*  672 */           return null;
/*      */         }
/*  674 */         map.put(mw.getKey(), mw.getValue());
/*      */       }
/*      */     }
/*  677 */     return map;
/*      */   }
/*      */ 
/*      */   public void setClientSettings(Map<String, String> settings)
/*      */   {
/*  686 */     JSONArray a = new JSONArray();
/*  687 */     for (String key : settings.keySet()) {
/*  688 */       a.put(new MapWorkaround(key, (String)settings.get(key)));
/*      */     }
/*  690 */     put("client_settings", a);
/*      */   }
/*      */ 
/*      */   public Collection getPositionIds()
/*      */   {
/*  737 */     Collection positions = new ArrayList();
/*  738 */     String positionString = getString("pos_ids");
/*  739 */     if (positionString == null) {
/*  740 */       return Collections.EMPTY_LIST;
/*      */     }
/*  742 */     StringTokenizer tokenizer = new StringTokenizer(positionString, ";");
/*  743 */     while (tokenizer.hasMoreTokens()) {
/*  744 */       positions.add(tokenizer.nextToken());
/*      */     }
/*  746 */     return positions;
/*      */   }
/*      */ 
/*      */   public String getPositionIdsAsString() {
/*  750 */     String positionIds = getString("pos_ids");
/*  751 */     return positionIds == null ? "" : positionIds;
/*      */   }
/*      */ 
/*      */   public void setPositionIds(String positionIds) {
/*  755 */     put("pos_ids", positionIds);
/*      */   }
/*      */ 
/*      */   public String getFundAcctId()
/*      */   {
/*  764 */     return getString("facct_id");
/*      */   }
/*      */ 
/*      */   public void setFundAcctId(String id)
/*      */   {
/*  773 */     put("facct_id", id);
/*      */   }
/*      */ 
/*      */   public void setExecutorsBlacklist(List<String> blacklist)
/*      */   {
/*  782 */     if (blacklist == null) {
/*  783 */       put("execBlacklist", null);
/*  784 */       return;
/*      */     }
/*  786 */     JSONArray execArray = new JSONArray();
/*  787 */     for (String exec : blacklist) {
/*  788 */       execArray.put(exec);
/*      */     }
/*  790 */     put("execBlacklist", execArray);
/*      */   }
/*      */ 
/*      */   public List<String> getExecutorsBlacklist()
/*      */   {
/*  797 */     List blacklist = new ArrayList();
/*  798 */     JSONArray execArray = getJSONArray("execBlacklist");
/*  799 */     if (execArray != null) {
/*  800 */       for (int i = 0; i < execArray.length(); i++) {
/*  801 */         blacklist.add(execArray.getString(i));
/*      */       }
/*      */     }
/*  804 */     return blacklist;
/*      */   }
/*      */ 
/*      */   public Money getMCEquityLimit()
/*      */   {
/*  813 */     String eqString = getString("equityLimit");
/*  814 */     if ((eqString != null) && (getCurrency() != null)) {
/*  815 */       return new Money(eqString, getCurrency().getCurrencyCode());
/*      */     }
/*  817 */     return new Money("0", getCurrency().getCurrencyCode());
/*      */   }
/*      */ 
/*      */   public void setMCEquityLimit(Money mcEquity)
/*      */   {
/*  827 */     put("equityLimit", mcEquity.getValue().toPlainString());
/*      */   }
/*      */ 
/*      */   public String getFundRatio()
/*      */   {
/*  836 */     return getString("fundRatio");
/*      */   }
/*      */ 
/*      */   public void setFundRatio(String fundRatio)
/*      */   {
/*  845 */     put("fundRatio", fundRatio);
/*      */   }
/*      */ 
/*      */   public BigDecimal getComVol()
/*      */   {
/*  855 */     String cmString = getString("cvol");
/*  856 */     if (cmString != null) {
/*  857 */       return new BigDecimal(cmString);
/*      */     }
/*  859 */     return new BigDecimal(0);
/*      */   }
/*      */ 
/*      */   public void setComVol(BigDecimal com)
/*      */   {
/*  869 */     if (com != null)
/*  870 */       put("cvol", com.stripTrailingZeros().toPlainString());
/*      */   }
/*      */ 
/*      */   public BigDecimal getComPip()
/*      */   {
/*  881 */     String cmString = getString("cpip");
/*  882 */     if (cmString != null) {
/*  883 */       return new BigDecimal(cmString);
/*      */     }
/*  885 */     return new BigDecimal(0);
/*      */   }
/*      */ 
/*      */   public void setComPip(BigDecimal com)
/*      */   {
/*  895 */     if (com != null)
/*  896 */       put("cpip", com.stripTrailingZeros().toPlainString());
/*      */   }
/*      */ 
/*      */   public BigDecimal getComPipPerc()
/*      */   {
/*  906 */     String cmString = getString("cpipp");
/*  907 */     if (cmString != null) {
/*  908 */       return new BigDecimal(cmString);
/*      */     }
/*  910 */     return new BigDecimal(0);
/*      */   }
/*      */ 
/*      */   public void setComPipPerc(BigDecimal com)
/*      */   {
/*  920 */     if (com != null)
/*  921 */       put("cpipp", com.stripTrailingZeros().toPlainString());
/*      */   }
/*      */ 
/*      */   public Long getSdExecutionDelay()
/*      */   {
/*  929 */     return getLong("sdDelay");
/*      */   }
/*      */ 
/*      */   public void setSdExecutionDelay(Long delay)
/*      */   {
/*  938 */     put("sdDelay", Long.toString(delay.longValue()));
/*      */   }
/*      */ 
/*      */   public Money getSdTrailingUSD()
/*      */   {
/*  946 */     String priceString = getString("sdSleepage");
/*  947 */     if (priceString != null) {
/*  948 */       return new Money(priceString, "USD");
/*      */     }
/*  950 */     return null;
/*      */   }
/*      */ 
/*      */   public void setSdTrailingUSD(Money trailing)
/*      */   {
/*  960 */     if (trailing != null)
/*  961 */       put("sdSleepage", trailing.getValue().toPlainString());
/*      */   }
/*      */ 
/*      */   public List<ClientInfoMessage> getClients()
/*      */     throws ParseException
/*      */   {
/*  972 */     JSONArray a = getJSONArray("clients");
/*      */ 
/*  974 */     if (a == null) {
/*  975 */       return new ArrayList(0);
/*      */     }
/*  977 */     List set = new ArrayList(a.length());
/*  978 */     for (int i = 0; i < a.length(); i++) {
/*  979 */       ClientInfoMessage client = (ClientInfoMessage)ProtocolMessage.parse(a.getString(i));
/*  980 */       set.add(client);
/*      */     }
/*  982 */     return set;
/*      */   }
/*      */ 
/*      */   public void setClients(List<ClientInfoMessage> clients)
/*      */   {
/*  991 */     JSONArray a = new JSONArray();
/*  992 */     String ids = "";
/*  993 */     for (ClientInfoMessage client : clients) {
/*  994 */       a.put(client);
/*  995 */       if (ids.equals(""))
/*  996 */         ids = client.getClientId();
/*      */       else {
/*  998 */         ids = ids + ";" + client.getClientId();
/*      */       }
/*      */     }
/* 1001 */     put("clients", a);
/* 1002 */     put("clientIds", ids);
/*      */   }
/*      */ 
/*      */   public Set<String> getClientIds() {
/* 1006 */     Set ret = null;
/* 1007 */     String ids = getString("clientIds");
/* 1008 */     if (ids != null) {
/* 1009 */       StringTokenizer tokenizer = new StringTokenizer(ids, ";");
/* 1010 */       ret = new HashSet();
/* 1011 */       while (tokenizer.hasMoreTokens()) {
/* 1012 */         ret.add(tokenizer.nextToken());
/*      */       }
/*      */     }
/* 1015 */     return ret;
/*      */   }
/*      */ 
/*      */   public BigDecimal getSPVinerOpen()
/*      */   {
/* 1024 */     String ratio = getString("spVinerOpen");
/* 1025 */     if (ratio == null) {
/* 1026 */       return null;
/*      */     }
/* 1028 */     return new BigDecimal(ratio);
/*      */   }
/*      */ 
/*      */   public void setSPVinerOpen(BigDecimal viner)
/*      */   {
/* 1038 */     if (viner != null)
/* 1039 */       put("spVinerOpen", viner.toPlainString());
/*      */   }
/*      */ 
/*      */   public BigDecimal getSPVinerClose()
/*      */   {
/* 1049 */     String ratio = getString("spVinerClose");
/* 1050 */     if (ratio == null) {
/* 1051 */       return null;
/*      */     }
/* 1053 */     return new BigDecimal(ratio);
/*      */   }
/*      */ 
/*      */   public void setSPVinerClose(BigDecimal viner)
/*      */   {
/* 1063 */     if (viner != null)
/* 1064 */       put("spVinerClose", viner.toPlainString());
/*      */   }
/*      */ 
/*      */   public Integer getRecordCount()
/*      */   {
/* 1074 */     Integer recCount = getInteger("recCount");
/* 1075 */     if (recCount == null) {
/* 1076 */       recCount = Integer.valueOf(0);
/*      */     }
/* 1078 */     return recCount;
/*      */   }
/*      */ 
/*      */   public void setRecordCount(Integer recCount)
/*      */   {
/* 1087 */     put("recCount", recCount);
/*      */   }
/*      */ 
/*      */   public BigDecimal getFeedCommssion()
/*      */   {
/* 1096 */     String fcomm = getString("fcomm");
/* 1097 */     if (fcomm != null) {
/* 1098 */       return new BigDecimal(fcomm);
/*      */     }
/* 1100 */     return null;
/*      */   }
/*      */ 
/*      */   public void setFeedCommission(BigDecimal fcomm)
/*      */   {
/* 1110 */     if (fcomm == null)
/* 1111 */       put("fcomm", null);
/*      */     else
/* 1113 */       put("fcomm", fcomm.toPlainString());
/*      */   }
/*      */ 
/*      */   public Integer getMaxGroups()
/*      */   {
/* 1124 */     String mwm = getString("maxgr");
/* 1125 */     if (mwm == null) {
/* 1126 */       return null;
/*      */     }
/* 1128 */     return Integer.valueOf(Integer.parseInt(mwm));
/*      */   }
/*      */ 
/*      */   public void setMaxGroups(Integer maxGroups)
/*      */   {
/* 1137 */     String set = null;
/* 1138 */     if (maxGroups != null) {
/* 1139 */       set = maxGroups.toString();
/*      */     }
/* 1141 */     put("maxgr", set);
/*      */   }
/*      */ 
/*      */   public int getMcMode()
/*      */   {
/* 1150 */     String mwm = getString("mcMode");
/* 1151 */     if (mwm == null) {
/* 1152 */       return 0;
/*      */     }
/* 1154 */     return Integer.parseInt(mwm);
/*      */   }
/*      */ 
/*      */   public void setMcMode(Integer mcmode)
/*      */   {
/* 1163 */     put("mcMode", mcmode.toString());
/*      */   }
/*      */ 
/*      */   public int getMarginMode()
/*      */   {
/* 1172 */     String mwm = getString("marginMode");
/* 1173 */     if (mwm == null) {
/* 1174 */       return 0;
/*      */     }
/* 1176 */     return Integer.parseInt(mwm);
/*      */   }
/*      */ 
/*      */   public void setMarginMode(Integer marginMode)
/*      */   {
/* 1186 */     put("marginMode", marginMode.toString());
/*      */   }
/*      */ 
/*      */   public void setPrimeBroker(List<String> primeBroker)
/*      */   {
/* 1196 */     JSONArray execArray = new JSONArray();
/* 1197 */     for (String exec : primeBroker) {
/* 1198 */       execArray.put(exec);
/*      */     }
/* 1200 */     put("pBroker", execArray);
/*      */   }
/*      */ 
/*      */   public List<String> getPrimeBroker()
/*      */   {
/* 1207 */     List primeBroker = new ArrayList();
/* 1208 */     JSONArray execArray = getJSONArray("pBroker");
/* 1209 */     if (execArray != null) {
/* 1210 */       for (int i = 0; i < execArray.length(); i++) {
/* 1211 */         primeBroker.add(execArray.getString(i));
/*      */       }
/*      */     }
/* 1214 */     return primeBroker;
/*      */   }
/*      */ 
/*      */   public void setExecutionMode(Integer execMode)
/*      */   {
/* 1223 */     put("execMode", execMode.toString());
/*      */   }
/*      */ 
/*      */   public int getExecutionMode()
/*      */   {
/* 1232 */     String execMode = getString("execMode");
/* 1233 */     if (execMode == null) {
/* 1234 */       return 0;
/*      */     }
/* 1236 */     return Integer.parseInt(execMode);
/*      */   }
/*      */ 
/*      */   public BigDecimal getFxcmAccountReset()
/*      */   {
/* 1246 */     String fcomm = getString("fxcmAccReset");
/* 1247 */     if (fcomm != null) {
/* 1248 */       return new BigDecimal(fcomm);
/*      */     }
/* 1250 */     return null;
/*      */   }
/*      */ 
/*      */   public void setFxcmAccountReset(BigDecimal fcomm)
/*      */   {
/* 1260 */     if (fcomm == null)
/* 1261 */       put("fxcmAccReset", null);
/*      */     else
/* 1263 */       put("fxcmAccReset", fcomm.toPlainString());
/*      */   }
/*      */ 
/*      */   public void setAccountProperties(Map<String, String> props)
/*      */   {
/* 1268 */     JSONObject obj = new JSONObject(props);
/* 1269 */     put("props", obj);
/*      */   }
/*      */ 
/*      */   public Map<String, String> getAccountProperties() {
/* 1273 */     JSONObject obj = getJSONObject("props");
/* 1274 */     if (obj != null) {
/* 1275 */       return obj.getMap();
/*      */     }
/* 1277 */     return null;
/*      */   }
/*      */ 
/*      */   public int getExecutorType()
/*      */   {
/* 1282 */     return getInteger("exec_type").intValue();
/*      */   }
/*      */ 
/*      */   public void setExecutorType(Integer execType) {
/* 1286 */     put("exec_type", execType);
/*      */   }
/*      */ 
/*      */   public String getWlFundAcctId()
/*      */   {
/* 1295 */     return getString("wlFund");
/*      */   }
/*      */ 
/*      */   public void setWlFundAcctId(String id)
/*      */   {
/* 1304 */     put("wlFund", id);
/*      */   }
/*      */ 
/*      */   public boolean isWlFIlter() {
/* 1308 */     return getBoolean("wlFilter");
/*      */   }
/*      */ 
/*      */   public void setIsWlFIlter(Boolean filter) {
/* 1312 */     put("wlFilter", filter);
/*      */   }
/*      */ 
/*      */   public boolean isAllowSdex() {
/* 1316 */     return getBoolean("allowSdx");
/*      */   }
/*      */ 
/*      */   public void setAllowSdexr(Boolean allow) {
/* 1320 */     put("allowSdx", allow);
/*      */   }
/*      */ 
/*      */   public Integer getMaxOrders()
/*      */   {
/* 1329 */     String mwm = getString("maxord");
/* 1330 */     if (mwm == null) {
/* 1331 */       return null;
/*      */     }
/* 1333 */     return Integer.valueOf(Integer.parseInt(mwm));
/*      */   }
/*      */ 
/*      */   public void setMaxOrders(Integer maxOrders)
/*      */   {
/* 1342 */     String set = null;
/* 1343 */     if (maxOrders != null) {
/* 1344 */       set = maxOrders.toString();
/*      */     }
/* 1346 */     put("maxord", set);
/*      */   }
/*      */ 
/*      */   public Money getPl()
/*      */   {
/* 1355 */     String balanceString = getString("pl");
/* 1356 */     if ((balanceString != null) && (getCurrency() != null)) {
/* 1357 */       return new Money(balanceString, getCurrency().getCurrencyCode());
/*      */     }
/* 1359 */     return null;
/*      */   }
/*      */ 
/*      */   public void setPl(Money pl)
/*      */   {
/* 1369 */     put("pl", pl.getValue().toPlainString());
/*      */   }
/*      */ 
/*      */   public Money getComm()
/*      */   {
/* 1378 */     String balanceString = getString("pl");
/* 1379 */     if ((balanceString != null) && (getCurrency() != null)) {
/* 1380 */       return new Money(balanceString, getCurrency().getCurrencyCode());
/*      */     }
/* 1382 */     return null;
/*      */   }
/*      */ 
/*      */   public void setComm(Money comm)
/*      */   {
/* 1392 */     put("pl", comm.getValue().toPlainString());
/*      */   }
/*      */ 
/*      */   public String getWlPartnerId()
/*      */   {
/* 1401 */     return getString("wlPart");
/*      */   }
/*      */ 
/*      */   public void setWlPartnerId(String id)
/*      */   {
/* 1410 */     put("wlPart", id);
/*      */   }
/*      */ 
/*      */   public String getFeedCommissionMapId()
/*      */   {
/* 1420 */     return getString("fcMapId");
/*      */   }
/*      */ 
/*      */   public void setFeedCommissionMapId(String id)
/*      */   {
/* 1429 */     put("fcMapId", id);
/*      */   }
/*      */ 
/*      */   public JSONObject getFeedCommissionMap()
/*      */   {
/* 1438 */     return getJSONObject("fcMap");
/*      */   }
/*      */ 
/*      */   public void setFeedCommissionMap(JSONObject map)
/*      */   {
/* 1447 */     put("fcMap", map);
/*      */   }
/*      */ 
/*      */   public FeedCommission getFeedCommissionAll(String instrument) {
/* 1451 */     JSONObject obj = getFeedCommissionMap();
/* 1452 */     if (obj == null) {
/* 1453 */       return null;
/*      */     }
/* 1455 */     JSONObject map = obj.getJSONObject(instrument);
/* 1456 */     if (map == null) {
/* 1457 */       map = obj.getJSONObject("null");
/*      */     }
/* 1459 */     if (map == null) {
/* 1460 */       return null;
/*      */     }
/* 1462 */     return new FeedCommission(map);
/*      */   }
/*      */ 
/*      */   public BigDecimal getFeedCommission(String instrument) {
/* 1466 */     FeedCommission fc = getFeedCommissionAll(instrument);
/* 1467 */     if (fc == null) {
/* 1468 */       return null;
/*      */     }
/* 1470 */     return fc.getFeedCommssion();
/*      */   }
/*      */ 
/*      */   public Map<String, FeedCommission> getFeedCommissions()
/*      */   {
/* 1475 */     JSONObject obj = getFeedCommissionMap();
/* 1476 */     if (obj == null) {
/* 1477 */       return null;
/*      */     }
/* 1479 */     Map map = new HashMap();
/* 1480 */     for (Iterator i = obj.keys(); i.hasNext(); ) {
/* 1481 */       String key = (String)i.next();
/* 1482 */       map.put(key, new FeedCommission(obj.getJSONObject(key)));
/*      */     }
/* 1484 */     return map;
/*      */   }
/*      */ 
/*      */   public String getWlTimes() {
/* 1488 */     return getString("wlTimes");
/*      */   }
/*      */ 
/*      */   public void setWlTimes(String times) {
/* 1492 */     put("wlTimes", times);
/*      */   }
/*      */   public void setWlTimes(int dayOpen, int timeOpen, int dayClose, int timeClose) {
/* 1495 */     setWlTimes(dayOpen + "_" + timeOpen + "_" + dayClose + "_" + timeClose);
/*      */   }
/*      */ 
/*      */   public String getOrderGroupIdPrefix()
/*      */   {
/* 1500 */     return getString("grIdPref");
/*      */   }
/*      */ 
/*      */   public void setOrderGroupIdPrefix(String prefix) {
/* 1504 */     put("grIdPref", prefix);
/*      */   }
/*      */ 
/*      */   class MapWorkaround extends ProtocolMessage
/*      */   {
/*      */     public static final String TYPE = "mw";
/*      */ 
/*      */     public MapWorkaround()
/*      */     {
/*  701 */       setType("mw");
/*      */     }
/*      */ 
/*      */     public MapWorkaround(String key, String value)
/*      */     {
/*  709 */       setType("mw");
/*  710 */       put("key", key);
/*  711 */       put("value", value);
/*      */     }
/*      */ 
/*      */     public MapWorkaround(ProtocolMessage message)
/*      */     {
/*  720 */       super();
/*  721 */       setType("mw");
/*  722 */       put("key", message.getString("key"));
/*  723 */       put("value", message.getString("value"));
/*      */     }
/*      */ 
/*      */     public String getKey() {
/*  727 */       return getString("key");
/*      */     }
/*      */ 
/*      */     public String getValue() {
/*  731 */       return getString("value");
/*      */     }
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.AccountInfoMessage
 * JD-Core Version:    0.6.0
 */