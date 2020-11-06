package com.itembase.currency;

import java.time.LocalDateTime;
import java.util.HashMap;

public abstract class BaseExchangeData implements IExchangeData {
    public abstract String getBase();
    public abstract void setBase(String base);
    public abstract HashMap<String, Double> getRates();
    public abstract void setRates(HashMap<String, Double> rates);
    public abstract String getLastUpdate();
    public abstract void setLastUpdate(String date);

    @Override
    public String toString()
    {
        return "ExchangeData{" +
                "base='" + getBase() + '\'' +
                ", getRates='" + getRates() + '\'' +
                ", lastUpdate='" + getLastUpdate() + '\'' +
                '}';
    }
}
