/*     */ package com.dukascopy.transport.common.msg.news;
/*     */ 
/*     */ public enum EventCategory
/*     */ {
/*  22 */   ADP_NATIONAL_EMPLYMENT_REPORT("ADP National Employment Report", 1), 
/*  23 */   ALL_INDUSTRY_ACTIVITY_INDEX("All Industry Activity Index", 2), 
/*  24 */   AUTO_EXPORTS("Auto Exports", 3), 
/*  25 */   AUTO_PRODUCTION("Auto Production", 4), 
/*  26 */   AUTO_SALES("Auto Sales", 5), 
/*  27 */   BALANCE_OF_PAYMENTS("Balance of Payments", 6), 
/*  28 */   BANK_LENDING("Bank Lending", 8), 
/*  29 */   BANK_OF_CANADA_MONTHLY_STATS("Bank of Canada Monthly Stats", 10), 
/*     */ 
/*  31 */   BANK_OF_CANADA_WEEKLY_STATS("Bank of Canada Weekly Stats", 11), 
/*  32 */   INTEREST_RATE_ANNOUNCEMENTS("Interest Rate Announcements", 12), 
/*  33 */   BUSINESS_AND_CONSUMER_CONFIDENCE("Business and Consumer Confidence", 14), 
/*  34 */   BUSINESS_CONFIDENCE("Business Confidence", 15), 
/*  35 */   BUSINESS_INVENTORIES("Business Inventories", 16), 
/*  36 */   BUSINESS_INVESTMENT("Business Investment", 17), 
/*  37 */   BUSINESS_SENTIMENT("Business Sentiment", 18), 
/*  38 */   CHICAGO_PMI("Chicago PMI", 19), 
/*  39 */   CONFERENCE_BOARD_CONSUMER_CONFIDENCE_SURVEY("Conference Board Consumer Confidence Survey", 20), 
/*     */ 
/*  41 */   CONSTRUCTION_SPENDING("Construction Spending", 21), 
/*  42 */   CONSUMER_CONFIDENCE("Consumer Confidence", 22), 
/*  43 */   CONSUMER_LENDING("Consumer Lending", 24), 
/*  44 */   CONSUMER_PRICE_INDEX("Consumer Price Index", 25), 
/*  45 */   CONSUMER_PRICE_INDEX_FINAL("Consumer Price Index - Final", 26), 
/*  46 */   CONSUMER_PRICE_INDEX_FLASH_EST("Consumer Price Index - Flash Est.", 27), 
/*  47 */   CONSUMER_PRICE_INDEX_PRELIMINARY("Consumer Price Index - Preliminary", 28), 
/*  48 */   CONSUMER_SPENDING("Consumer Spending", 29), 
/*  49 */   CORPORATE_GOODS_PRICE_INDEX("Corporate Goods Price Index", 30), 
/*     */ 
/*  51 */   CURRENT_ACCOUNT("Current Account", 31), 
/*  52 */   DALLAS_FED_MANUFACTURING_OUTLOOK_SURVEY("Dallas Fed Manufacturing Outlook Survey", 32), 
/*  53 */   DURABLE_GOODS("Durable Goods", 33), 
/*  54 */   EMPLOYMENT("Employment", 34), 
/*  55 */   EMPLOYMENT_COST_INDEXES("Employment Cost Indexes", 35), 
/*  56 */   EXISTING_HOME_SALES("Existing Home Sales", 38), 
/*  57 */   FACTORY_ORDERS("Factory Orders", 39), 
/*     */ 
/*  59 */   FOREIGN_RESERVES("Foreign Reserves", 41), 
/*  60 */   GDP("GDP", 42), 
/*  61 */   GDP_ADVANCE("GDP - Advance", 43), 
/*  62 */   GDP_FINAL("GDP - Final", 44), 
/*  63 */   GDP_FLASH_EST("GDP - Flash Est.", 45), 
/*  64 */   GDP_PRELIMINARY("GDP - Preliminary", 46), 
/*  65 */   GDP_REVISED("GDP - Revised", 47), 
/*  66 */   HOUSING_INDEX_HALIFAX_PENDING_SALES("Housing Index (Halifax/Pending Sales)", 48), 
/*  67 */   HOUSING_STARTS("Housing Starts", 49), 
/*  68 */   IFO_BUSINESS_SURVEY("IFO Business Survey", 50), 
/*     */ 
/*  70 */   IMPORT_PRICES("Import Prices", 51), 
/*  71 */   INDUSTRIAL_PRODUCTION("Industrial Production", 54), 
/*  72 */   INDUSTRIAL_PRODUCTION_PRELIMINARY("Industrial Production - Preliminary", 55), 
/*  73 */   INDUSTRIAL_PRODUCTION_REVISED("Industrial Production - Revised", 56), 
/*  74 */   INDUSTRIAL_PRODUCTION_AND_CAPACITY_UTILIZATION("Industrial Production and Capacity Utilization", 57), 
/*  75 */   INTERNATIONAL_SECURITIES_TRANSACTIONS("International Securities Transactions", 59), 
/*  76 */   ISM_MANUFACTURING_SURVEY("ISM Manufacturing Survey", 60), 
/*     */ 
/*  78 */   ISM_NON_MANUFACTURING_SURVEY("ISM Non-Manufacturing Survey", 61), 
/*  79 */   JOBLESS_CLAIMS("Jobless Claims", 62), 
/*  80 */   LEADING_ECONOMIC_INDICATORS("Leading Economic Indicators", 65), 
/*  81 */   MANUFACTURING_ORDERS("Manufacturing Orders", 68), 
/*  82 */   MANUFACTURING_SURVEY("Manufacturing Survey", 69), 
/*     */ 
/*  84 */   MONETARY_BASE("Monetary Base", 71), 
/*  85 */   MONETARY_RESERVES("Monetary Reserves", 72), 
/*  86 */   MONEY_SUPPLY("Money Supply", 73), 
/*  87 */   NAHB_HOUSING_INDEX("NAHB Housing Index", 74), 
/*  88 */   NATIONWIDE_HOUSE_PRICES("Nationwide House Prices", 75), 
/*  89 */   NEW_HOME_SALES("New Home Sales", 76), 
/*  90 */   NEW_ORDERS("New Orders", 77), 
/*  91 */   NY_FED_EMPIRE_STATE_SURVEY("NY Fed Empire State Survey", 78), 
/*  92 */   PERSONAL_INCOME("Personal Income", 79), 
/*  93 */   PHILADELPHIA_FED_BUSINESS_OUTLOOK_SURVEY("Philadelphia Fed Business Outlook Survey", 80), 
/*     */ 
/*  95 */   PMI_MANUFACTURING("PMI - Manufacturing", 81), 
/*  96 */   PMI_SERVICES("PMI - Services", 82), 
/*  97 */   PRELIMINARY_PORTFOLIO_DATA("Preliminary Portfolio Data", 83), 
/*  98 */   PRODUCER_PRICE_INDEX("Producer Price Index", 84), 
/*  99 */   PRODUCTIVITY_PRELIMINARY("Productivity - Preliminary", 87), 
/* 100 */   PRODUCTIVITY_REVISED("Productivity - Revised", 88), 
/* 101 */   PUBLIC_FINANCES("Public Finances", 89), 
/* 102 */   RETAIL_SALES("Retail Sales", 90), 
/*     */ 
/* 104 */   RICHMOND_FED_BUSINESS_ACTIVITY_SURVEY("Richmond Fed Business Activity Survey", 91), 
/* 105 */   RICS_HOUSE_PRICES("RICS House Prices", 92), 
/* 106 */   TANKAN("Tankan", 93), 
/* 107 */   TERTIARY_INDUSTRY_ACTIVITY_INDEX("Tertiary Industry Activity Index", 94), 
/* 108 */   TRADE_BALANCE("Trade Balance", 95), 
/* 109 */   REUTERS_UNIVERSITY_OF_MICHIGAN_CONSUMER_SENTIMENT_SURVEY_FINAL("Reuters/University of Michigan Consumer Sentiment Survey (Final)", 96), 
/* 110 */   WAGE_EARNERS_HOUSEHOLD_SPENDING("Wage-Earners Household Spending", 97), 
/* 111 */   WHOLESALE_TRADE("Wholesale Trade", 98), 
/* 112 */   ZEW_BUSINESS_CONFIDENCE("ZEW Business Confidence", 99), 
/* 113 */   INDUSTRIAL_AND_RAW_MATERIAL_PRICES("Industrial And Raw Material Prices", 100), 
/*     */ 
/* 115 */   BAVARIA_CPI("Bavaria CPI", 101), 
/* 116 */   BRANDENBURG_CPI("Brandenburg CPI", 102), 
/* 117 */   BADEN_WUERTTEMBERG_CPI("Baden-Wuerttemberg CPI", 103), 
/* 118 */   HESSE_CPI("Hesse CPI", 104), 
/* 119 */   NORTH_RHINE_WESTPHALIA_CPI("North Rhine Westphalia CPI", 105), 
/* 120 */   SAXONY_CPI("Saxony CPI", 106), 
/* 121 */   RBA_CASH_RATE("RBA Cash Rate", 107), 
/* 122 */   FOREX_TRANSACTION_SPOT_MARKET("Forex Transaction Spot Market", 108), 
/* 123 */   IVEY_PMI("Ivey PMI", 109), 
/* 124 */   PMI_FLASH_ESTIMATES("PMI - Flash Estimates", 110), 
/*     */ 
/* 126 */   RBNZ_CASH_RATE("RBNZ Cash Rate", 111), 
/* 127 */   TRADE_INDEX("Trade Index", 112), 
/* 128 */   BOE_MPC_MINUTES("BOE MPC Minutes", 113), 
/* 129 */   CBI_DISTRIBUTITIVE_TRADES_SURVEY("CBI Distributitive Trades Survey", 114), 
/* 130 */   FEDERAL_FUNDS_RATE("Federal Funds Rate", 115), 
/* 131 */   TIC_REPORT("TIC Report", 116), 
/* 132 */   REPO_RATE("Repo Rate", 118), 
/* 133 */   LIBOR_RATE_BANDS("Libor Rate bands", 119), 
/* 134 */   CONSUMER_SENTIMENT_INDEX("Consumer Sentiment Index", 120), 
/*     */ 
/* 136 */   KOF_ECONOMIC_BAROMETER("KOF Economic Barometer", 122), 
/* 137 */   SP_CASE_SHILLER_HOME_PX_INDEX("SP Case Shiller Home Px Index", 124), 
/* 138 */   NORGES_BANK_RATE("Norges Bank Rate", 125), 
/* 139 */   EMPLOYMENT_TRENDS("Employment Trends", 126), 
/* 140 */   BUILDING_APPROVALS("Building Approvals", 127), 
/* 141 */   CPI_NATION("CPI (Nation)", 128), 
/* 142 */   CPI_TOKYO("CPI (Tokyo)", 129), 
/* 143 */   UNEMPLOYMENT("Unemployment", 130), 
/*     */ 
/* 145 */   CHICAGO_FED_MIDWEST_MANUFACTURING_INDEX("Chicago Fed Midwest Manufacturing Index", 131), 
/* 146 */   CHICAGO_FED_NATIONAL_ACTIVITY_INDEX("Chicago Fed National Activity Index", 132), 
/* 147 */   FEDERAL_DISCOUNT_WINDOW("Federal Discount Window", 133), 
/* 148 */   FOREIGN_CUSTODY_HOLDING("Foreign Custody Holding", 134), 
/* 149 */   MBA_MORTGAGE_APPLICATION_SURVEY("MBA Mortgage Application Survey", 135), 
/* 150 */   CBI_INDUSTRIAL_TRENDS_SURVEY("CBI Industrial Trends Survey", 136), 
/* 151 */   HOUSING_FINANCE("Housing Finance", 137), 
/* 152 */   LABOUR_PRICE_INDEX("Labour Price Index", 138), 
/* 153 */   INDUSTRIAL_INDEX("Industrial Index", 139), 
/* 154 */   BRC_RETAIL_SALES("BRC Retail Sales", 140), 
/*     */ 
/* 156 */   REDBOOK_RETAIL_SALES("Redbook Retail Sales", 141), 
/* 157 */   MEDIAN_EXISTING_HOME_PRICE("Median Existing Home Price", 142), 
/* 158 */   US_ICSC_GOLDMAN_SACHS_CHAIN_STORE_SALES("US ICSC-Goldman Sachs Chain Store Sales", 143), 
/* 159 */   ECONOMIC_SENTIMENT("Economic Sentiment", 144), 
/* 160 */   ABC_NEWS_CONSUMER_CONFIDENCE("ABC News Consumer Confidence", 145), 
/* 161 */   DJ_BTMU_BUSINESS_BAROMETE("DJ-BTMU Business Baromete", 146), 
/* 162 */   SMALL_BUSINESS_OPTIMISM_INDEX("Small Business Optimism Index", 147), 
/* 163 */   LABOR_COST_INDEX("Labor Cost Index", 148), 
/* 164 */   KANSAS_CITY_FED_MFG_INDEX("Kansas City Fed Mfg Index", 150), 
/*     */ 
/* 166 */   NAPM_NY_BUSINESS_INDEX("NAPM-NY Business Index", 151), 
/* 167 */   INDUSTRIAL_CAPACITY_UTILIZATION_RATE("Industrial Capacity Utilization Rate", 152), 
/* 168 */   REUTERS_UNIVERSITY_OF_MICHIGAN_CONSUMER_SENTIMENT_SURVEY_PRELIM("Reuters/University of Michigan Consumer Sentiment Survey (Prelim)", 153), 
/* 169 */   BUSINESS_INDICATORS("Business Indicators", 154), 
/* 170 */   BUILDING_PERMITS("Building Permits", 155), 
/* 171 */   NEW_HOUSING_PRICE_INDEX("New Housing Price Index", 156), 
/* 172 */   ISM_NY_BUSINESS_INDEX("ISM NY Business Index", 157), 
/* 173 */   EIA_DOE_WEEKLY_PETROLIUM_STATUS_REPORT("EIA/DOE Weekly Petrolium Status Report", 300), 
/* 174 */   EIA_DOE_Weekly_Natural_Gas_Storage_Report("EIA/DOE Weekly Natural Gas Storage Report", 301), 
/* 175 */   API_WEEKLY_STATISTICAL_BULLETIN("API Weekly Statistical Bulletin", 302);
/*     */ 
/*     */   public final String description;
/*     */   public final int code;
/*     */ 
/* 181 */   private EventCategory(String description, int code) { this.description = description;
/* 182 */     this.code = code; }
/*     */ 
/*     */   public static EventCategory fromCodeString(String code)
/*     */   {
/* 186 */     if ((code == null) || (code.isEmpty()))
/* 187 */       return null;
/*     */     try
/*     */     {
/* 190 */       int intCode = Integer.parseInt(code);
/* 191 */       for (EventCategory eventCategory : values())
/* 192 */         if (eventCategory.code == intCode)
/* 193 */           return eventCategory;
/*     */     }
/*     */     catch (NumberFormatException exc) {
/*     */     }
/* 197 */     return null;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.news.EventCategory
 * JD-Core Version:    0.6.0
 */