//MQL4 Reference

//metatrader types
typedef char* string;
typedef long datetime;
typedef long Color;
typedef long color;
typedef double object;


//Standard constants Series arrays
#define MODE_OPEN       0       //Open price.
#define MODE_LOW        1       //Low price.
#define MODE_HIGH       2       //High price.
#define MODE_CLOSE      3       //Close price.
#define MODE_VOLUME     4       //Volume, used in iLowest() and iHighest() functions.
#define MODE_TIME       5       //Bar open time, used in ArrayCopySeries() function.

//Timeframes

#define PERIOD_M1       1       //1 minute.
#define PERIOD_M5       5       //5 minutes.
#define PERIOD_M15      15      //15 minutes.
#define PERIOD_M30      30      //30 minutes.
#define PERIOD_H1       60      //1 hour.
#define PERIOD_H4       240     //4 hour.
#define PERIOD_D1       1440    //Daily.
#define PERIOD_W1       10080   //Weekly.
#define PERIOD_MN1      43200   //Monthly.
#define ZERO   0//0 (zero)        0       Timeframe used on the chart.
// Trade operations
#define OP_BUY  0       //Buying position.
#define OP_SELL 1       //Selling position.
#define OP_BUYLIMIT     2 //      Buy limit pending position.
#define OP_SELLLIMIT    3  //     Sell limit pending position.
#define OP_BUYSTOP      4      // Buy stop pending position.
#define OP_SELLSTOP     5      // Sell stop pending position.
// Price constants
#define PRICE_CLOSE     0      // Close price.
#define PRICE_OPEN      1      // Open price.
#define PRICE_HIGH      2      // High price.
#define PRICE_LOW       3      // Low price.
#define PRICE_MEDIAN    4      // Median price, (high+low)/2.
#define PRICE_TYPICAL   5      // Typical price, (high+low+close)/3.
#define PRICE_WEIGHTED  6      // Weighted close price, (high+low+close+close)/4.

// MarketInfo
//Market information identifiers, used with MarketInfo() function.
//It can be any of the following values:
#define MODE_LOW        1     //  Low day price.
#define MODE_HIGH       2     //  High day price.
#define MODE_TIME       5     //  The last incoming tick time (last known server time).
#define MODE_BID        9     //  Last incoming bid price. For the current symbol, it is stored in the predefined variable Bid
#define MODE_ASK        10    //  Last incoming ask price. For the current symbol, it is stored in the predefined variable Ask
#define MODE_POINT      11    //  Point size in the quote currency. For the current symbol, it is stored in the predefined variable Point
#define MODE_DIGITS     12    //  Count of digits after decimal point in the symbol prices. For the current symbol, it is stored in the predefined variable Digits
#define MODE_SPREAD     13    //  Spread value in points.
#define MODE_STOPLEVEL  14    //  Stop level in points.
#define MODE_LOTSIZE    15    //  Lot size in the base currency.
#define MODE_TICKVALUE  16    //  Tick value in the deposit currency.
#define MODE_TICKSIZE   17    //  Tick size in points.
#define MODE_SWAPLONG   18    //  Swap of the long position.
#define MODE_SWAPSHORT  19    //  Swap of the short position.
#define MODE_STARTING   20    //  Market starting date (usually used for futures).
#define MODE_EXPIRATION 21    //  Market expiration date (usually used for futures).
#define MODE_TRADEALLOWED       22    //  Trade is allowed for the symbol.
#define MODE_MINLOT     23    //  Minimum permitted amount of a lot.
#define MODE_LOTSTEP    24    //  Step for changing lots.
#define MODE_MAXLOT     25    //  Maximum permitted amount of a lot.
#define MODE_SWAPTYPE   26    //  Swap calculation method. 0 - in points; 1 - in the symbol base currency; 2 - by interest; 3 - in the margin currency.
#define MODE_PROFITCALCMODE     27    //  Profit calculation mode. 0 - Forex; 1 - CFD; 2 - Futures.
#define MODE_MARGINCALCMODE     28    //  Margin calculation mode. 0 - Forex; 1 - CFD; 2 - Futures; 3 - CFD for indices.
#define MODE_MARGININIT 29    // Initial margin requirements for 1 lot.
#define MODE_MARGINMAINTENANCE  30    //  Margin to maintain open positions calculated for 1 lot.
#define MODE_MARGINHEDGED       31    //  Hedged margin calculated for 1 lot.
#define MODE_MARGINREQUIRED     32    //  Free margin required to open 1 lot for buying.
#define MODE_FREEZELEVEL        33    //  Order freeze level in points. If the execution price lies within the range defined by the freeze level, the order cannot be modified, cancelled or closed.

// Drawing styles
#define DRAW_LINE       0     //  Drawing line.
#define DRAW_SECTION    1     //  Drawing sections.
#define DRAW_HISTOGRAM  2     //  Drawing histogram.
#define DRAW_ARROW      3     //  Drawing arrows (symbols).
#define DRAW_ZIGZAG     4     //  Drawing sections between even and odd indicator buffers.
#define DRAW_NONE       12    //  No drawing.

#define STYLE_SOLID     0     //  The pen is solid.
#define STYLE_DASH      1     //  The pen is dashed.
#define STYLE_DOT       2     //  The pen is dotted.
#define STYLE_DASHDOT   3     //  The pen has alternating dashes and dots.
#define STYLE_DASHDOTDOT        4     // The pen has alternating dashes and double dots.

//Arrow codes
#define SYMBOL_THUMBSUP 67    //  Thumb up symbol (C).
#define SYMBOL_THUMBSDOWN       68    //  Thumb down symbol (D).
#define SYMBOL_ARROWUP  241   //  Arrow up symbol (ñ).
#define SYMBOL_ARROWDOWN        242   //  Arrow down symbol (ò).
#define SYMBOL_STOPSIGN 251   //  Stop sign symbol (û).
#define SYMBOL_CHECKSIGN        252   //  Check sign symbol (ü).

#define SYMBOL_LEFTPRICE        5     //  Left sided price label.
#define SYMBOL_RIGHTPRICE       6     //  Right sided price label.

//Indicator lines

#define MODE_MAIN       0     //  Base indicator line.
#define MODE_SIGNAL     1     //  Signal line.

#define MODE_MAIN       0     //  Base indicator line.
#define MODE_PLUSDI     1     //  +DI indicator line.
#define MODE_MINUSDI    2     //  -DI indicator line.

#define MODE_UPPER      1     //  Upper line.
#define MODE_LOWER      2     //  Lower line.

// Ichimoku Kinko Hyo
#define MODE_TENKANSEN  1     //  Tenkan-sen.
#define MODE_KIJUNSEN   2     //  Kijun-sen.
#define MODE_SENKOUSPANA        3    //   Senkou Span A.
#define MODE_SENKOUSPANB        4    //   Senkou Span B.
#define MODE_CHINKOUSPAN        5    //   Chinkou Span.

//Moving Average methods
#define MODE_SMA        0     //  Simple moving average,
#define MODE_EMA        1     //  Exponential moving average,
#define MODE_SMMA       2     //  Smoothed moving average,
#define MODE_LWMA       3     //  Linear weighted moving average.

// MessageBox
#define IDOK    1     //  OK button was selected.
#define IDCANCEL        2     //  Cancel button was selected.
#define IDABORT 3     //  Abort button was selected.
#define IDRETRY 4       Retry button was selected.
#define IDIGNORE        5     //  Ignore button was selected.
#define IDYES   6     //  Yes button was selected.
#define IDNO    7     //  No button was selected.
#define IDTRYAGAIN      10    //  Try Again button was selected.
#define IDCONTINUE      11    //  Continue button was selected.

#define MB_OK   0x00000000    // The message box contains one push button: OK. This is the default.
#define MB_OKCANCEL     0x00000001    // The message box contains two push buttons: OK and Cancel.
#define MB_ABORTRETRYIGNORE     0x00000002     // The message box contains three push buttons: Abort, Retry, and Ignore.
#define MB_YESNOCANCEL  0x00000003    //  The message box contains three push buttons: Yes, No, and Cancel.
#define MB_YESNO        0x00000004    //  The message box contains two push buttons: Yes and No.
#define MB_RETRYCANCEL  0x00000005    //  The message box contains two push buttons: Retry and Cancel.
#define MB_CANCELTRYCONTINUE    0x00000006    //  Windows 2000: The message box contains three push buttons: Cancel, Try Again, Continue. Use this message box type instead of MB_ABORTRETRYIGNORE.

#define MB_ICONSTOP 0x00000010
#define MB_ICONERROR 0x00000010
#define MB_ICONHAND  0x00000010     // A stop-sign icon appears in the message box.
#define MB_ICONQUESTION 0x00000020   //   A question-mark icon appears in the message box.
#define MB_ICONEXCLAMATION 0x00000030
#define MB_ICONWARNING      0x00000030    //  An exclamation-point icon appears in the message box.
#define MB_ICONINFORMATION 0x00000040
#define MB_ICONASTERISK     0x00000040    //  An icon consisting of a lowercase letter i in a circle appears in the message box.

#define MB_DEFBUTTON1   0x00000000     // The first button is the default button. MB_DEFBUTTON1 is the default unless MB_DEFBUTTON2, MB_DEFBUTTON3, or MB_DEFBUTTON4 is specified.
#define MB_DEFBUTTON2   0x00000100     // The second button is the default button.
#define MB_DEFBUTTON3   0x00000200     // The third button is the default button.
#define MB_DEFBUTTON4   0x00000300     // The fourth button is the default button.

// Object types

#define OBJ_VLINE       0     //  Vertical line. Uses time part of first coordinate.
#define OBJ_HLINE       1     //  Horizontal line. Uses price part of first coordinate.
#define OBJ_TREND       2     //  Trend line. Uses 2 coordinates.
#define OBJ_TRENDBYANGLE        3     // Trend by angle. Uses 1 coordinate. To set angle of line use ObjectSet() function.
#define OBJ_REGRESSION  4     //  Regression. Uses time parts of first two coordinates.
#define OBJ_CHANNEL     5     //  Channel. Uses 3 coordinates.
#define OBJ_STDDEVCHANNEL       6      // Standard deviation channel. Uses time parts of first two coordinates.
#define OBJ_GANNLINE    7     //  Gann line. Uses 2 coordinate, but price part of second coordinate ignored.
#define OBJ_GANNFAN     8     //  Gann fan. Uses 2 coordinate, but price part of second coordinate ignored.
#define OBJ_GANNGRID    9     //  Gann grid. Uses 2 coordinate, but price part of second coordinate ignored.
#define OBJ_FIBO        10    //  Fibonacci retracement. Uses 2 coordinates.
#define OBJ_FIBOTIMES   11    //  Fibonacci time zones. Uses 2 coordinates.
#define OBJ_FIBOFAN     12    //  Fibonacci fan. Uses 2 coordinates.
#define OBJ_FIBOARC     13    //  Fibonacci arcs. Uses 2 coordinates.
#define OBJ_EXPANSION   14    //  Fibonacci expansions. Uses 3 coordinates.
#define OBJ_FIBOCHANNEL 15    //  Fibonacci channel. Uses 3 coordinates.
#define OBJ_RECTANGLE   16    //  Rectangle. Uses 2 coordinates.
#define OBJ_TRIANGLE    17    //  Triangle. Uses 3 coordinates.
#define OBJ_ELLIPSE     18    //  Ellipse. Uses 2 coordinates.
#define OBJ_PITCHFORK   19    //  Andrews pitchfork. Uses 3 coordinates.
#define OBJ_CYCLES      20    //  Cycles. Uses 2 coordinates.
#define OBJ_TEXT        21    //  Text. Uses 1 coordinate.
#define OBJ_ARROW       22    //  Arrows. Uses 1 coordinate.
#define OBJ_LABEL       23    //  Text label. Uses 1 coordinate in pixel

//Object properties

#define OBJPROP_TIME1   0     //  datetime        Datetime value to set/get first coordinate time part.
#define OBJPROP_PRICE1  1     //  double  Double value to set/get first coordinate price part.
#define OBJPROP_TIME2   2     //  datetime        Datetime value to set/get second coordinate time part.
#define OBJPROP_PRICE2  3     //  double  Double value to set/get second coordinate price part.
#define OBJPROP_TIME3   4     //  datetime        Datetime value to set/get third coordinate time part.
#define OBJPROP_PRICE3  5     //  double  Double value to set/get third coordinate price part.
#define OBJPROP_COLOR   6     //  color   Color value to set/get object color.
#define OBJPROP_STYLE   7     //  int     Value is one of STYLE_SOLID, STYLE_DASH, STYLE_DOT, STYLE_DASHDOT, STYLE_DASHDOTDOT constants to set/get object line style.
#define OBJPROP_WIDTH   8     //  int     Integer value to set/get object line width. Can be from 1 to 5.
#define OBJPROP_BACK    9     //  bool    Boolean value to set/get background drawing flag for object.
#define OBJPROP_RAY     10    //  bool    Boolean value to set/get ray flag of object.
#define OBJPROP_ELLIPSE 11    //  bool    Boolean value to set/get ellipse flag for fibo arcs.
#define OBJPROP_SCALE   12    //  double  Double value to set/get scale object property.
#define OBJPROP_ANGLE   13    //  double  Double value to set/get angle object property in degrees.
#define OBJPROP_ARROWCODE       14    //  int     Integer value or arrow enumeration to set/get arrow code object property.
#define OBJPROP_TIMEFRAMES      15    //  int     Value can be one or combination (bitwise addition) of object visibility constants to set/get timeframe object property.
#define OBJPROP_DEVIATION       16    //  double  Double value to set/get deviation property for Standard deviation objects.
#define OBJPROP_FONTSIZE        100   //  int     Integer value to set/get font size for text objects.
#define OBJPROP_CORNER  101   //  int     Integer value to set/get anchor corner property for label objects. Must be from 0-3.
#define OBJPROP_XDISTANCE       102   //  int     Integer value to set/get anchor X distance object property in pixels.
#define OBJPROP_YDISTANCE       103   //  int     Integer value is to set/get anchor Y distance object property in pixels.
#define OBJPROP_FIBOLEVELS      200   //  int     Integer value to set/get Fibonacci object level count. Can be from 0 to 32.
#define OBJPROP_LEVELCOLOR      201   //  color   Color value to set/get object level line color.
#define OBJPROP_LEVELSTYLE      202   //  int     Value is one of STYLE_SOLID, STYLE_DASH, STYLE_DOT, STYLE_DASHDOT, STYLE_DASHDOTDOT constants to set/get object level line style.
#define OBJPROP_LEVELWIDTH      203   //  int     Integer value to set/get object level line width. Can be from 1 to 5.
#define OBJPROP_FIRSTLEVEL      210   //   int     Integer value to set/get the value of Fibonacci object level with index n. Index n can be from 0 (number of levels -1), but not larger than 31.

//Object visibility

#define OBJ_PERIOD_M1   0x0001 // Object shown is only on 1-minute charts.
#define OBJ_PERIOD_M5   0x0002 // Object shown is only on 5-minute charts.
#define OBJ_PERIOD_M15  0x0004 // Object shown is only on 15-minute charts.
#define OBJ_PERIOD_M30  0x0008 // Object shown is only on 30-minute charts.
#define OBJ_PERIOD_H1   0x0010 // Object shown is only on 1-hour charts.
#define OBJ_PERIOD_H4   0x0020 // Object shown is only on 4-hour charts.
#define OBJ_PERIOD_D1   0x0040 // Object shown is only on daily charts.
#define OBJ_PERIOD_W1   0x0080 // Object shown is only on weekly charts.
#define OBJ_PERIOD_MN1  0x0100 // Object shown is only on monthly charts.
#define OBJ_ALL_PERIODS 0x01FF // Object shown is on all timeframes.
//#define NULL    0     // Object shown is on all timeframes.
#define EMPTY   -1    // Hidden object on all timeframes.

//Uninitialize reason codes

#define REASON_REMOVE   1      // Expert removed from chart.
#define REASON_RECOMPILE        2      // Expert recompiled.
#define REASON_CHARTCHANGE      3      // symbol or timeframe changed on the chart.
#define REASON_CHARTCLOSE       4      // Chart closed.
#define REASON_PARAMETERS       5      // Inputs parameters was changed by user.
#define REASON_ACCOUNT  6     //  Other account activated.

// Special constants
#ifndef NULL
#define NULL    0     //  Indicates empty state of the string.
#endif
#define EMPTY   -1    //  Indicates empty state of the parameter.
#define EMPTY_VALUE     0x7FFFFFFF     // Default custom indicator empty value.
#define CLR_NONE        0xFFFFFFFF     // Indicates empty state of colors.
#define WHOLE_ARRAY     0      // Used with array functions. Indicates that all array elements will be processed.

//---- errors returned from trade server
#define ERR_NO_ERROR                                  0
#define ERR_NO_RESULT                                 1
#define ERR_COMMON_ERROR                              2
#define ERR_INVALID_TRADE_PARAMETERS                  3
#define ERR_SERVER_BUSY                               4
#define ERR_OLD_VERSION                               5
#define ERR_NO_CONNECTION                             6
#define ERR_NOT_ENOUGH_RIGHTS                         7
#define ERR_TOO_FREQUENT_REQUESTS                     8
#define ERR_MALFUNCTIONAL_TRADE                       9
#define ERR_ACCOUNT_DISABLED                         64
#define ERR_INVALID_ACCOUNT                          65
#define ERR_TRADE_TIMEOUT                           128
#define ERR_INVALID_PRICE                           129
#define ERR_INVALID_STOPS                           130
#define ERR_INVALID_TRADE_VOLUME                    131
#define ERR_MARKET_CLOSED                           132
#define ERR_TRADE_DISABLED                          133
#define ERR_NOT_ENOUGH_MONEY                        134
#define ERR_PRICE_CHANGED                           135
#define ERR_OFF_QUOTES                              136
#define ERR_BROKER_BUSY                             137
#define ERR_REQUOTE                                 138
#define ERR_ORDER_LOCKED                            139
#define ERR_LONG_POSITIONS_ONLY_ALLOWED             140
#define ERR_TOO_MANY_REQUESTS                       141
#define ERR_TRADE_MODIFY_DENIED                     145
#define ERR_TRADE_CONTEXT_BUSY                      146
#define ERR_TRADE_EXPIRATION_DENIED                 147
#define ERR_TRADE_TOO_MANY_ORDERS                   148
#define ERR_TRADE_HEDGE_PROHIBITED                  149
#define ERR_TRADE_PROHIBITED_BY_FIFO                150
//---- mql4 run time errors
#define ERR_NO_MQLERROR                            4000
#define ERR_WRONG_FUNCTION_POINTER                 4001
#define ERR_ARRAY_INDEX_OUT_OF_RANGE               4002
#define ERR_NO_MEMORY_FOR_CALL_STACK               4003
#define ERR_RECURSIVE_STACK_OVERFLOW               4004
#define ERR_NOT_ENOUGH_STACK_FOR_PARAM             4005
#define ERR_NO_MEMORY_FOR_PARAM_STRING             4006
#define ERR_NO_MEMORY_FOR_TEMP_STRING              4007
#define ERR_NOT_INITIALIZED_STRING                 4008
#define ERR_NOT_INITIALIZED_ARRAYSTRING            4009
#define ERR_NO_MEMORY_FOR_ARRAYSTRING              4010
#define ERR_TOO_LONG_STRING                        4011
#define ERR_REMAINDER_FROM_ZERO_DIVIDE             4012
#define ERR_ZERO_DIVIDE                            4013
#define ERR_UNKNOWN_COMMAND                        4014
#define ERR_WRONG_JUMP                             4015
#define ERR_NOT_INITIALIZED_ARRAY                  4016
#define ERR_DLL_CALLS_NOT_ALLOWED                  4017
#define ERR_CANNOT_LOAD_LIBRARY                    4018
#define ERR_CANNOT_CALL_FUNCTION                   4019
#define ERR_EXTERNAL_CALLS_NOT_ALLOWED             4020
#define ERR_NO_MEMORY_FOR_RETURNED_STR             4021
#define ERR_SYSTEM_BUSY                            4022
#define ERR_INVALID_FUNCTION_PARAMSCNT             4050
#define ERR_INVALID_FUNCTION_PARAMVALUE            4051
#define ERR_STRING_FUNCTION_INTERNAL               4052
#define ERR_SOME_ARRAY_ERROR                       4053
#define ERR_INCORRECT_SERIESARRAY_USING            4054
#define ERR_CUSTOM_INDICATOR_ERROR                 4055
#define ERR_INCOMPATIBLE_ARRAYS                    4056
#define ERR_GLOBAL_VARIABLES_PROCESSING            4057
#define ERR_GLOBAL_VARIABLE_NOT_FOUND              4058
#define ERR_FUNC_NOT_ALLOWED_IN_TESTING            4059
#define ERR_FUNCTION_NOT_CONFIRMED                 4060
#define ERR_SEND_MAIL_ERROR                        4061
#define ERR_STRING_PARAMETER_EXPECTED              4062
#define ERR_INTEGER_PARAMETER_EXPECTED             4063
#define ERR_DOUBLE_PARAMETER_EXPECTED              4064
#define ERR_ARRAY_AS_PARAMETER_EXPECTED            4065
#define ERR_HISTORY_WILL_UPDATED                   4066
#define ERR_TRADE_ERROR                            4067
#define ERR_END_OF_FILE                            4099
#define ERR_SOME_FILE_ERROR                        4100
#define ERR_WRONG_FILE_NAME                        4101
#define ERR_TOO_MANY_OPENED_FILES                  4102
#define ERR_CANNOT_OPEN_FILE                       4103
#define ERR_INCOMPATIBLE_FILEACCESS                4104
#define ERR_NO_ORDER_SELECTED                      4105
#define ERR_UNKNOWN_SYMBOL                         4106
#define ERR_INVALID_PRICE_PARAM                    4107
#define ERR_INVALID_TICKET                         4108
#define ERR_TRADE_NOT_ALLOWED                      4109
#define ERR_LONGS_NOT_ALLOWED                      4110
#define ERR_SHORTS_NOT_ALLOWED                     4111
#define ERR_OBJECT_ALREADY_EXISTS                  4200
#define ERR_UNKNOWN_OBJECT_PROPERTY                4201
#define ERR_OBJECT_DOES_NOT_EXIST                  4202
#define ERR_UNKNOWN_OBJECT_TYPE                    4203
#define ERR_NO_OBJECT_NAME                         4204
#define ERR_OBJECT_COORDINATES_ERROR               4205
#define ERR_NO_SPECIFIED_SUBWINDOW                 4206
#define ERR_SOME_OBJECT_ERROR                      4207
//

double  Ask;
int Bars;
double Bid;
double Close[255];
int Digits;
double High[255];
double Low[255];
double Open[255];
double Point;
datetime Time[255];
double Volume[255];

// Account information
double AccountBalance();
double AccountCredit();
string AccountCompany();
string AccountCurrency();
double AccountEquity();
double AccountFreeMargin();
double AccountFreeMarginCheck(  	string symbol, int cmd, double volume);
double AccountFreeMarginMode();
int AccountLeverage();
double AccountMargin();
string AccountName();
int AccountNumber();
double AccountProfit();
string AccountServer();
int AccountStopoutLevel();
int AccountStopoutMode();

#define MODE_ASCEND 0
#define MODE_DESCEND 1
// Array functions
/*
int ArrayBsearch(double array[], double value, int count=WHOLE_ARRAY, int start=0, int direction=MODE_ASCEND);
int ArrayBsearch(double array[], double value, int count=WHOLE_ARRAY, int start=0, int direction=MODE_ASCEND);
int ArrayCopy(void dest[], object source[], int start_dest=0, int start_source=0, int count=WHOLE_ARRAY);
int ArrayCopyRates(  	void dest_array[], string symbol=NULL, int timeframe=0);
int ArrayCopySeries(  	void array[], int series_index, string symbol=NULL, int timeframe=0);
int ArrayDimension(  	object array[]);
bool ArrayGetAsSeries(  	object array[]);
int ArrayInitialize(  	void array[], double value);
bool ArrayIsSeries(  	object array[]);
int ArrayMaximum(  	double array[], int count=WHOLE_ARRAY, int start=0);
int ArrayMinimum(  	double array[], int count=WHOLE_ARRAY, int start=0);
int ArrayRange(  	object array[], int range_index);
int ArrayResize(  	void array[], int new_size);
bool ArraySetAsSeries(  	void array[], bool set);
int ArraySize(  	object array[]);
int ArraySort(  	void array[], int count=WHOLE_ARRAY, int start=0, int sort_dir=MODE_ASCEND);
*/

int ArrayBsearch(double array[255], double value, int count=WHOLE_ARRAY, int start=0, int direction=MODE_ASCEND);
int ArrayCopy(double dest[244], double source[255], int start_dest=0, int start_source=0, int count=WHOLE_ARRAY);
int ArrayCopyRates(  	double dest_array[255], string symbol=NULL, int timeframe=0);
int ArrayCopySeries(  	double array[255], int series_index, string symbol=NULL, int timeframe=0);
int ArrayDimension(  	double array[255]);
bool ArrayGetAsSeries(  	double array[255]);
int ArrayInitialize(  	double array[255], double value);
bool ArrayIsSeries(  	double array[255]);
int ArrayMaximum(  	double array[255], int count=WHOLE_ARRAY, int start=0);
int ArrayMinimum(  	double array[255], int count=WHOLE_ARRAY, int start=0);
int ArrayRange(  	double array[255], int range_index);
int ArrayResize(  	double array[255], int new_size);
bool ArraySetAsSeries(  	double array[255], bool set);
int ArraySize(  	double array[255]);
int ArraySort(  	double array[255], int count=WHOLE_ARRAY, int start=0, int sort_dir=MODE_ASCEND);

// Checkup
int GetLastError(  	);
bool IsConnected(  	);
bool IsDemo(  	);
bool  IsDllsAllowed(  	);
bool  IsExpertEnabled(  	);
bool  IsLibrariesAllowed(  	);
bool  IsOptimization(  	);
bool  IsStopped(  	);
bool  IsTesting(  	);
bool  IsTradeAllowed(  	);
bool  IsTradeContextBusy(  	);
bool  IsVisualMode(  	);
int  UninitializeReason(  	);

//Client terminal
string TerminalCompany(  	);
string TerminalName(  	);
string TerminalPath(  	);

//Common functions
void Alert(  	...);
void Comment(  	...);
int GetTickCount(  	);
double MarketInfo(  	string symbol, int type);
int MessageBox(  	string text=NULL, string caption=NULL, int flags=EMPTY);
void PlaySound(  	string filename);
void Print(  	...);
bool SendFTP(  	string filename, string ftp_path=NULL);
void SendMail(  	string subject, string some_text);
void Sleep(  	int milliseconds);

#define TIME_DATE 0
#define TIME_MINUTES 1
//Conversion functions
string CharToStr(  	int char_code);
string DoubleToStr(  	double value, int digits);
double NormalizeDouble(  	double value, int digits);
double StrToDouble(  	string value);
int StrToInteger(  	string value);
datetime StrToTime(  	string value);
string TimeToStr(datetime value, int mode=TIME_DATE|TIME_MINUTES);

//Custom indicators

void IndicatorBuffers(  	int count);
int IndicatorCounted(  	);
void IndicatorDigits(  	int digits);
void IndicatorShortName(  	string name);
void SetIndexArrow(  	int index, int code);
bool SetIndexBuffer(  	int index, double array[]);
void SetIndexDrawBegin(int index, int begin);
void SetIndexEmptyValue(int index, double value);
void SetIndexLabel(int index, string text);
void SetIndexShift(  	int index, int shift);
void SetIndexStyle(  	int index, int type, int style=EMPTY, int width=EMPTY, color clr=CLR_NONE);
void SetLevelStyle(  	int draw_style, int line_width, color clr=CLR_NONE);
void SetLevelValue(  	int level, double value);

// Date & Time functions
int  Day(  	);
int  DayOfWeek(  	);
int  DayOfYear(  	);
int  Hour(  	);
int  Minute(  	);
int  Month(  	);
int  Seconds(  	);
datetime   TimeCurrent(  	);
int  TimeDay(  	datetime date);
int  TimeDayOfWeek(  	datetime date);
int  TimeDayOfYear(  	datetime date);
int  TimeHour(  	datetime date);
int  TimeLocal(  	datetime date);
int  TimeMinute(  	datetime date);
int  TimeMonth(  	datetime date);
int  TimeSeconds(  	datetime date);
int  TimeYear(  	datetime date);
int  Year(  	);

#define DOUBLE_VALUE sizeof(double)
#define LONG_VALUE sizeof(long)
//File functions
void FileClose(  	int handle);
void FileDelete(  	string filename);
void FileFlush(  	int handle);
bool FileIsEnding(  	int handle);
bool FileIsLineEnding(  	int handle);
int FileOpen(  	string filename, int mode, int delimiter=';');
int FileOpenHistory(  	string filename, int mode, int delimiter=';');
//int FileReadArray(  	int handle, void array[255], int start, int count);
int FileReadArray(  	int handle, double array[255], int start, int count);
double FileReadDouble(  	int handle, int size=DOUBLE_VALUE);
int FileReadInteger(  	int handle, int size=LONG_VALUE);
double FileReadNumber(  	int handle);
string FileReadString(  	int handle, int length=0);
bool FileSeek(  	int handle, int offset, int origin);
int FileSize(  	int handle);
int FileTell(  	int handle);
int FileWrite(  	int handle, ...);
int FileWriteArray(  	int handle, object array[255], int start, int count);
int FileWriteDouble(  	int handle, double value, int size=DOUBLE_VALUE);
int FileWriteInteger(  	int handle, int value, int size=LONG_VALUE);
int FileWriteString(  	int handle, string value, int length);

//Global variables
bool GlobalVariableCheck(  	string name);
bool GlobalVariableDel(  	string name);
double GlobalVariableGet(  	string name);
string GlobalVariableName(  	int index);
datetime GlobalVariableSet(  	string name, double value);
bool GlobalVariableSetOnCondition(  	string name, double value, double check_value);
int GlobalVariablesDeleteAll(  	string prefix_name=NULL);
int GlobalVariablesTotal(  	);

// Math & Trig
double MathAbs(  	double value);
double MathArccos(  	double value);
double MathArcsin(  	double value);
double MathArctan(  	double value);
double MathCeil(  	double value);
double MathCos(  	double value);
double MathExp(  	double value);
double MathFloor(  	double value);
double MathLog(  	double value);
double MathMax(  	double value1, double value2);
double MathMin(  	double value1, double value2);
double MathMod(  	double value1, double value2);
double MathPow(  	double value1, double value2);
int MathRand(  	);
double MathRound(  	double value);
double MathSin(  	double value);
double MathSqrt(  	double value);
void MathSrand(  	int seed);
double MathTan(  	double value);

//Object functions
bool ObjectCreate(string name, int type, int window, datetime time1, double price1, datetime time2=0, double price2=0, datetime time3=0, double price3=0);
bool ObjectDelete(  	string name);
string ObjectDescription(  	string name);
int ObjectFind(  	string name);
double ObjectGet(  	string name, int index);
string ObjectGetFiboDescription(  	string name, int index);
int ObjectGetShiftByValue(  	string name, double value);
double ObjectGetValueByShift(  	string name, int shift);
bool ObjectMove(  	string name, int point, datetime time1, double price1);
string ObjectName(  	int index);
int ObjectsDeleteAll(  	int window=EMPTY, int type=EMPTY);
bool ObjectSet(  	string name, int index, double value);
bool ObjectSetFiboDescription(  	string name, int index, string text);
bool ObjectSetText(  	string name, string text, int font_size, string font=NULL, color text_color=CLR_NONE);
int ObjectsTotal(  	int type=EMPTY);
int ObjectType(  	string name);

// String functions
string StringConcatenate(  	...);
int StringFind(  	string text, string matched_text, int start=0);
int StringGetChar(  	string text, int pos);
int StringLen(  	string text);
string StringSetChar(  	string text, int pos, int value);
string StringSubstr(  	string text, int start, int length=0);
string StringTrimLeft(  	string text);
string StringTrimRight(  	string text);

// Technical indicators
double iAC(  	string symbol, int timeframe, int shift);
double iAD(  	string symbol, int timeframe, int shift);
double iAlligator(  	string symbol, int timeframe, int jaw_period, int jaw_shift, int teeth_period, int teeth_shift, int lips_period, int lips_shift, int ma_method, int applied_price, int mode, int shift);
double iADX(  	string symbol, int timeframe, int period, int applied_price, int mode, int shift);
double iATR(  	string symbol, int timeframe, int period, int shift);
double iAO(  	string symbol, int timeframe, int shift);
double iBearsPower(  	string symbol, int timeframe, int period, int applied_price, int shift);
double iBands(  	string symbol, int timeframe, int period, int deviation, int bands_shift, int applied_price, int mode, int shift);
double iBandsOnArray(  	double array[], int total, int period, int deviation, int bands_shift, int mode, int shift);
double iBullsPower(  	string symbol, int timeframe, int period, int applied_price, int shift);
double iCCI(  	string symbol, int timeframe, int period, int applied_price, int shift);
double iCCIOnArray(  	double array[], int total, int period, int shift);
//orig
//double iCustom(  	string symbol, int timeframe, string name, ..., int mode, int shift);
double iCustom(  	string symbol, int timeframe, string name, int mode, int shift, ...);
double iDeMarker(  	string symbol, int timeframe, int period, int shift);
double iEnvelopes(  	string symbol, int timeframe, int ma_period, int ma_method, int ma_shift, int applied_price, double deviation, int mode, int shift);
double iEnvelopesOnArray(  	double array[], int total, int ma_period, int ma_method, int ma_shift, double deviation, int mode, int shift);
double iForce(  	string symbol, int timeframe, int period, int ma_method, int applied_price, int shift);
double iFractals(  	string symbol, int timeframe, int mode, int shift);
double iGator(  	string symbol, int timeframe, int jaw_period, int jaw_shift, int teeth_period, int teeth_shift, int lips_period, int lips_shift, int ma_method, int applied_price, int mode, int shift);
double iIchimoku(  	string symbol, int timeframe, int tenkan_sen, int kijun_sen, int senkou_span_b, int mode, int shift);
double iBWMFI(  	string symbol, int timeframe, int shift);
double iMomentum(  	string symbol, int timeframe, int period, int applied_price, int shift);
double iMomentumOnArray(  	double array[], int total, int period, int shift);
double iMFI(  	string symbol, int timeframe, int period, int shift);
double iMA(  	string symbol, int timeframe, int period, int ma_shift, int ma_method, int applied_price, int shift);
double iMAOnArray(  	double array[], int total, int period, int ma_shift, int ma_method, int shift);
double iOsMA(  	string symbol, int timeframe, int fast_ema_period, int slow_ema_period, int signal_period, int applied_price, int shift);
double iMACD(  	string symbol, int timeframe, int fast_ema_period, int slow_ema_period, int signal_period, int applied_price, int mode, int shift);
double iOBV(  	string symbol, int timeframe, int applied_price, int shift);
double iSAR(  	string symbol, int timeframe, double step, double maximum, int shift);
double iRSI(  	string symbol, int timeframe, int period, int applied_price, int shift);
double iRSIOnArray(  	double array[], int total, int period, int shift);
double iRVI(  	string symbol, int timeframe, int period, int mode, int shift);
double iStdDev(  	string symbol, int timeframe, int ma_period, int ma_shift, int ma_method, int applied_price, int shift);
double iStdDevOnArray(  	double array[], int total, int ma_period, int ma_shift, int ma_method, int shift);
double iStochastic(  	string symbol, int timeframe, int Kperiod, int Dperiod, int slowing, int method, int price_field, int mode, int shift);
double iWPR(  	string symbol, int timeframe, int period, int shift);

//Timeseries access
int iBars(  	string symbol, int timeframe);
int iBarShift(  	string symbol, int timeframe, datetime time, bool exact=false);
double iClose(  	string symbol, int timeframe, int shift);
double iHigh(  	string symbol, int timeframe, int shift);
int iHighest(  	string symbol, int timeframe, int type, int count=WHOLE_ARRAY, int start=0);
double iLow(  	string symbol, int timeframe, int shift);
int iLowest(  	string symbol, int timeframe, int type, int count=WHOLE_ARRAY, int start=0);
double iOpen(  	string symbol, int timeframe, int shift);
datetime iTime(  	string symbol, int timeframe, int shift);
double iVolume(  	string symbol, int timeframe, int shift);

#define MODE_TRADES 0
#define MODE_HISTORY 1

#define SELECT_BY_POS 0
#define SELECT_BY_TICKET 1

//Trading functions
bool OrderClose(  	int ticket, double lots, double price, int slippage, color Color=CLR_NONE);
bool OrderCloseBy(  	int ticket, int opposite, color Color=CLR_NONE);
double OrderClosePrice(  	);
datetime OrderCloseTime(  	);
string OrderComment(  	);
double OrderCommission(  	);
bool OrderDelete(  	int ticket, color Color=CLR_NONE);
datetime OrderExpiration(  	);
double OrderLots(  	);
int OrderMagicNumber(  	);
bool OrderModify(  	int ticket, double price, double stoploss, double takeprofit, datetime expiration, color arrow_color=CLR_NONE);
double OrderOpenPrice(  	);
datetime OrderOpenTime(  	);
void OrderPrint(  	);
double OrderProfit(  	);
bool OrderSelect(  	int index, int select, int pool=MODE_TRADES);
int OrderSend(  	string symbol, int cmd, double volume, double price, int slippage, double stoploss, double takeprofit, string comment=NULL, int magic=0, datetime expiration=0, color arrow_color=CLR_NONE);
int OrdersHistoryTotal(  	);
double OrderStopLoss(  	);
int OrdersTotal(  	);
double OrderSwap(  	);
string OrderSymbol(  	);
double OrderTakeProfit(  	);
int OrderTicket(  	);
int OrderType(  	);

//Window functions
void HideTestIndicators(  	bool hide);
int Period(  	);
bool RefreshRates(  	);
string Symbol(  	);
int WindowBarsPerChart(  	);
string WindowExpertName(  	);
int WindowFind(  	string name);
int WindowFirstVisibleBar(  	);
int WindowHandle(  	string symbol, int timeframe);
bool WindowIsVisible(  	int index);
int WindowOnDropped(  	);
double WindowPriceMax(  	int index=0);
double WindowPriceMin(  	int index=0);
double WindowPriceOnDropped(  	);
void WindowRedraw(  	);
bool WindowScreenShot(  	string filename, int size_x, int size_y, int start_bar=-1, int chart_scale=-1, int chart_mode=-1);
datetime WindowTimeOnDropped(  	);
int WindowsTotal(  	);
int WindowXOnDropped(  	);
int WindowYOnDropped(  	);

//stdlib
string ErrorDescription(int error_code);
int    RGB(int red_value,int green_value,int blue_value);
bool   CompareDoubles(double number1,double number2);
string DoubleToStrMorePrecision(double number,int precision);
string IntegerToHexString(int integer_number);

