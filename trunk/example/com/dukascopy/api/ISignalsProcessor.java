package com.dukascopy.api;

import java.util.List;

public interface ISignalsProcessor {

    public void add(ISignal signal);

    public void add(List<ISignal> signals);

    public List<ISignal> retrieve();

}
