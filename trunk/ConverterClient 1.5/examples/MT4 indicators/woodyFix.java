package jforex.converted;
import java.awt.Color;
import com.dukascopy.api.*;
import com.dukascopy.connector.engine.*; 
import com.dukascopy.connector.engine.Properties; 
import com.dukascopy.connector.engine.IndicatorBuffer;
@RequiresFullAccess 
@Library("J:/javawork/ConverterClient 1.5/libs/MQL4Connector-2.6.38.5.jar")
public class woodyFix extends MQL4ConnectorIndicator {

protected void initProperties() {
if (properties == null) {
properties = new Properties();
}
properties.setProperty("connector_recalculate_all", "true");
properties.setProperty("connector_calculate_once_per_bar", "true");
properties.setProperty("connector_max_bars", "500");
properties.setProperty("indicator_level1","300");
properties.setProperty("indicator_level2","200");
properties.setProperty("indicator_level3","100");
properties.setProperty("indicator_buffers","8");
properties.setProperty("link","www.gnu.org");
properties.setProperty("indicator_levelcolor","Black");
properties.setProperty("indicator_separate_window","true");
properties.setProperty("indicator_color2","Red");
properties.setProperty("indicator_levelstyle","STYLE_DOT");
properties.setProperty("copyright","Under The GNU General Public License");
properties.setProperty("indicator_color1","Blue");
properties.setProperty("indicator_levelwidth","1");
properties.setProperty("indicator_color7","Lime");
properties.setProperty("indicator_color8","Red");
properties.setProperty("indicator_color5","Black");
properties.setProperty("indicator_color6","Crimson");
properties.setProperty("indicator_color3","Gray");
properties.setProperty("indicator_color4","Gold");
properties.setProperty("indicator_level6","-300");
properties.setProperty("indicator_level5","-200");
properties.setProperty("indicator_level4","-100");
}
protected Color indicator_color2 = Red;
protected Color indicator_color1 = Blue;
protected Color indicator_color7 = Lime;
protected Color indicator_color8 = Red;
protected Color indicator_color5 = Black;
protected Color indicator_color6 = Crimson;
protected Color indicator_color3 = Gray;
protected Color indicator_color4 = Gold;
@Configurable("") public int TrendCCI_Period=toInt(14);
@Configurable("") public int EntryCCI_Period=toInt(6);
@Configurable("") public int LSMAPeriod=toInt(25);
@Configurable("") public int Trend_period=toInt(5);
@Configurable("") public int CountBars=toInt(1000);
@Configurable("") public int CCISize=toInt(2);
@Configurable("") public int TCCISize=toInt(1);
@Configurable("") public int TrendSize=toInt(1);
@Configurable("") public int NoTrendSize=toInt(1);
@Configurable("") public boolean ShowLSMA=Bool(true);
@Configurable("") public int LineSize3=toInt(1);
@IndicatorBuffer ("") public double[] TrendCCI = new double[0];
@IndicatorBuffer ("") public double[] EntryCCI = new double[0];
@IndicatorBuffer ("") public double[] CCITrendUp = new double[0];
@IndicatorBuffer ("") public double[] CCITrendDown = new double[0];
@IndicatorBuffer ("") public double[] CCINoTrend = new double[0];
@IndicatorBuffer ("") public double[] CCITimeBar = new double[0];
double[] ZeroLine = new double[4000];
@IndicatorBuffer ("") public double[] LSMABuffer1 = new double[0];
@IndicatorBuffer ("") public double[] LSMABuffer2 = new double[0];
int EMAPeriod=toInt(34);
int FromZero=toInt(0);
double[] LineHighEMA = new double[4000];
double[] LineLowEMA = new double[4000];
int trendUp = 0;
int trendDown = 0;
public int init() throws JFException {
SetIndexStyle(4,DRAW_LINE,STYLE_SOLID,CCISize);
SetIndexBuffer(4,TrendCCI);
SetIndexLabel(4,"TrendCCI");
SetIndexStyle(0,DRAW_HISTOGRAM,0,TrendSize);
SetIndexBuffer(0,CCITrendUp);
SetIndexLabel(0,null);
SetIndexStyle(1,DRAW_HISTOGRAM,0,TrendSize);
SetIndexBuffer(1,CCITrendDown);
SetIndexLabel(1,null);
SetIndexStyle(2,DRAW_HISTOGRAM,0,NoTrendSize);
SetIndexBuffer(2,CCINoTrend);
SetIndexLabel(2,null);
SetIndexStyle(3,DRAW_HISTOGRAM,0,NoTrendSize);
SetIndexBuffer(3,CCITimeBar);
SetIndexLabel(3,null);
SetIndexStyle(5,DRAW_LINE,STYLE_SOLID,TCCISize);
SetIndexBuffer(5,EntryCCI);
SetIndexLabel(5,"EntryCCI");
SetIndexStyle(6,DRAW_ARROW,STYLE_SOLID,LineSize3);
SetIndexBuffer(6,LSMABuffer2);
SetIndexLabel(6,null);
SetIndexArrow(6,167);
SetIndexStyle(7,DRAW_ARROW,STYLE_SOLID,LineSize3);
SetIndexBuffer(7,LSMABuffer1);
SetIndexArrow(7,167);
SetIndexLabel(7,null);
if(true)return toInt(0);return 0;
}
public int deinit() throws JFException {
if(true)return toInt(0);return 0;
}
public int start() throws JFException {
int limit = 0;
int i = 0;
int trendCCI = 0;
int entryCCI = 0;
int counted_bars = 0;
long prevtime = System.currentTimeMillis();
double sum = 0.0;
double lengthvar = 0.0;
double tmp = 0.0;
double wt = 0.0;
int shift = 0;
int Draw4HowLong = 0;
int loopbegin = 0;
double EmaValue = 0.0;
counted_bars = toInt(IndicatorCounted());
prevtime = toLong(0L);
if (counted_bars<0){
if(true)return toInt(-1);}
if (counted_bars>0){
counted_bars--;
}
limit = toInt(Bars);
SetIndexDrawBegin(0,Bars-CountBars);
SetIndexDrawBegin(1,Bars-CountBars);
SetIndexDrawBegin(2,Bars-CountBars);
SetIndexDrawBegin(3,Bars-CountBars);
SetIndexDrawBegin(4,Bars-CountBars);
SetIndexDrawBegin(5,Bars-CountBars);
SetIndexDrawBegin(6,Bars-CountBars);
SetIndexDrawBegin(7,Bars-CountBars);
trendCCI = toInt(TrendCCI_Period);
entryCCI = toInt(EntryCCI_Period);
IndicatorShortName("[CCI: "+trendCCI+"] [TCCI: "+entryCCI+"] [per LSMA: "+LSMAPeriod+"] [Trend: "+Trend_period+"] Values CCI|TCCI:");
;
for(i=(int)limit;i>=0;i--){
CCINoTrend[toInt(i)]=toDouble(0);
CCITrendDown[toInt(i)]=toDouble(0);
CCITimeBar[toInt(i)]=toDouble(0);
CCITrendUp[toInt(i)]=toDouble(0);
ZeroLine[toInt(i)]=toDouble(0);
TrendCCI[toInt(i)]=toDouble(iCCI(null,0,trendCCI,PRICE_TYPICAL,i));
EntryCCI[toInt(i)]=toDouble(iCCI(null,0,entryCCI,PRICE_TYPICAL,i));
if (TrendCCI[i]>0&&TrendCCI[i+1]<0){
if (trendDown>Trend_period){
trendUp = toInt(0);
}}
if (TrendCCI[i]>0){
if (trendUp<Trend_period){
CCINoTrend[toInt(i)]=toDouble(TrendCCI[toInt(i)]);
trendUp++;
}
if (trendUp==Trend_period){
CCITimeBar[toInt(i)]=toDouble(TrendCCI[toInt(i)]);
trendUp++;
}
if (trendUp>Trend_period){
CCITrendUp[toInt(i)]=toDouble(TrendCCI[toInt(i)]);
}}
if (TrendCCI[i]<0&&TrendCCI[i+1]>0){
if (trendUp>Trend_period){
trendDown = toInt(0);
}}
if (TrendCCI[i]<0){
if (trendDown<Trend_period){
CCINoTrend[toInt(i)]=toDouble(TrendCCI[toInt(i)]);
trendDown++;
}
if (trendDown==Trend_period){
CCITimeBar[toInt(i)]=toDouble(TrendCCI[toInt(i)]);
trendDown++;
}
if (trendDown>Trend_period){
CCITrendDown[toInt(i)]=toDouble(TrendCCI[toInt(i)]);
}}
}
if (ShowLSMA==true){
if (counted_bars<0){
if(true)return 0;}
if (counted_bars>0){
counted_bars--;
}
counted_bars = toInt(Bars-counted_bars);
;
for(shift=(int)0;shift<counted_bars;shift++){
LineLowEMA[toInt(shift)]=toDouble(-FromZero);
LineHighEMA[toInt(shift)]=toDouble(-FromZero);
EmaValue = toDouble(iMA(null,0,EMAPeriod,0,MODE_EMA,PRICE_TYPICAL,shift));
if (Close(shift)>EmaValue){
LineHighEMA[toInt(shift)]=toDouble(EMPTY_VALUE);
}
if (Close(shift)<EmaValue){
LineLowEMA[toInt(shift)]=toDouble(EMPTY_VALUE);
}
}
Draw4HowLong = toInt(Bars-LSMAPeriod-5);
loopbegin = toInt(Draw4HowLong-LSMAPeriod-1);
;
for(shift=(int)loopbegin;shift>=0;shift--){
sum = toDouble(0);
;
for(i=(int)LSMAPeriod;i>=1;i--){
lengthvar = toDouble(LSMAPeriod+1);
lengthvar /= toDouble(3);
tmp = toDouble(0);
tmp = toDouble((i-lengthvar)*Close(LSMAPeriod-i+shift));
sum += toDouble(tmp);
}
wt = toDouble(sum*6/(LSMAPeriod*(LSMAPeriod+1)));
LSMABuffer1[toInt(shift)]=toDouble(0);
LSMABuffer2[toInt(shift)]=toDouble(0);
if (wt>Close(shift)){
LSMABuffer2[toInt(shift)]=toDouble(EMPTY_VALUE);
}
if (wt<Close(shift)){
LSMABuffer1[toInt(shift)]=toDouble(EMPTY_VALUE);
}
}
}
if(true)return toInt(0);return 0;
}

/**/};