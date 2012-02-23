package com.dukascopy.transport.common;

import com.dukascopy.transport.common.msg.request.CurrencyQuoteMessage;

public abstract interface QuoteListener
{
  public abstract void onQuote(CurrencyQuoteMessage paramCurrencyQuoteMessage);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.QuoteListener
 * JD-Core Version:    0.6.0
 */