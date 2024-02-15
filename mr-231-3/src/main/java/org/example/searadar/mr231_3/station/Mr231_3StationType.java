package org.example.searadar.mr231_3.station;

import org.example.searadar.mr231.convert.Mr231Converter;
import org.example.searadar.mr231_3.convert.Mr231_3Converter;

public class Mr231_3StationType {

    private static final String STATION_TYPE = "МР-231";
    private static final String CODEC_NAME = "mr231";

    public Mr231_3Converter createConverter() {
        return new Mr231_3Converter();
    }
}
