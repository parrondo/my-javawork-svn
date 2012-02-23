package com.dukascopy.charts.data.datacache;

import com.dukascopy.api.Instrument;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.charts.data.datacache.listener.DataFeedServerConnectionListener;
import com.dukascopy.dds2.greed.gui.component.filechooser.CancelLoadingException;
import com.dukascopy.dds2.greed.gui.component.filechooser.FileProgressListener;
import com.dukascopy.transport.common.datafeed.FileAlreadyExistException;
import com.dukascopy.transport.common.datafeed.FileType;
import com.dukascopy.transport.common.datafeed.KeyNotFoundException;
import com.dukascopy.transport.common.datafeed.StorageException;
import com.dukascopy.transport.common.msg.datafeed.MergeData;
import com.dukascopy.transport.common.msg.datafeed.OrderData;
import com.dukascopy.transport.common.msg.datafeed.OrderGroupData;
import com.dukascopy.transport.common.msg.strategy.FileItem;
import com.dukascopy.transport.common.msg.strategy.FileItem.AccessType;
import com.dukascopy.transport.common.msg.strategy.StrategyParameter;
import java.util.List;
import java.util.Map;

public abstract interface ICurvesProtocolHandler
{
  public abstract void connect(IAuthenticator paramIAuthenticator, String paramString1, String paramString2, String paramString3);

  public abstract void disconnect();

  public abstract void close();

  public abstract Data[] loadData(Instrument paramInstrument, Period paramPeriod, OfferSide paramOfferSide, long paramLong1, long paramLong2, boolean paramBoolean, LoadingProgressListener paramLoadingProgressListener)
    throws NotConnectedException, DataCacheException;

  public abstract Data[] loadInProgressCandle(Instrument paramInstrument, long paramLong, LoadingProgressListener paramLoadingProgressListener)
    throws NotConnectedException, DataCacheException;

  public abstract Data[] loadFile(Instrument paramInstrument, Period paramPeriod, OfferSide paramOfferSide, long paramLong, LoadingProgressListener paramLoadingProgressListener)
    throws NotConnectedException, DataCacheException;

  public abstract OrdersDataStruct loadOrders(Instrument paramInstrument, long paramLong1, long paramLong2, LoadingProgressListener paramLoadingProgressListener)
    throws NotConnectedException, DataCacheException;

  public abstract List<FileItem> getFileList(FileType paramFileType, FileItem.AccessType paramAccessType, FileProgressListener paramFileProgressListener)
    throws StorageException, CancelLoadingException;

  public abstract Long uploadFile(FileItem paramFileItem, String paramString, LoadingProgressListener paramLoadingProgressListener)
    throws StorageException, FileAlreadyExistException;

  public abstract FileItem downloadFile(long paramLong, LoadingProgressListener paramLoadingProgressListener)
    throws StorageException;

  public abstract List<StrategyParameter> listStrategyParameters(long paramLong, LoadingProgressListener paramLoadingProgressListener)
    throws StorageException;

  public abstract FileItem useKey(String paramString1, FileType paramFileType, LoadingProgressListener paramLoadingProgressListener, String paramString2)
    throws StorageException, KeyNotFoundException;

  public abstract Data[] loadCandles(String paramString, Period paramPeriod, long paramLong1, long paramLong2, LoadingProgressListener paramLoadingProgressListener)
    throws DataCacheException;

  public abstract Map<Instrument, Map<Period, Long>> loadDataFeedStartTimes(List<Instrument> paramList, LoadingProgressListener paramLoadingProgressListener)
    throws NotConnectedException, DataCacheException;

  public abstract void addDFSConnectionListener(DataFeedServerConnectionListener paramDataFeedServerConnectionListener);

  public abstract List<DataFeedServerConnectionListener> getDFSConnectionListeners();

  public abstract void removeDFSConnectionListener(DataFeedServerConnectionListener paramDataFeedServerConnectionListener);

  public abstract boolean isDFSOnline();

  public abstract boolean pingDFS();

  public abstract void removeAllDFSConnectionListeners();

  public static class OrdersDataStruct
  {
    public List<OrderGroupData> groups;
    public List<OrderData> orders;
    public List<MergeData> merges;
  }
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.ICurvesProtocolHandler
 * JD-Core Version:    0.6.0
 */