package org.example.searadar.mr231_3.convert;

import ru.oogis.searadar.api.message.InvalidMessage;
import ru.oogis.searadar.api.message.RadarSystemDataMessage;
import ru.oogis.searadar.api.message.SearadarStationMessage;
import ru.oogis.searadar.api.message.TrackedTargetMessage;
import ru.oogis.searadar.api.types.IFF;
import ru.oogis.searadar.api.types.TargetStatus;
import ru.oogis.searadar.api.types.TargetType;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Конвертер сообщений для формата МР-231-3.
 */
public class Mr231_3Converter {

    private static final Double[] DISTANCE_SCALE = {0.125, 0.25, 0.5, 1.5, 3.0, 6.0, 12.0, 24.0, 48.0, 96.0};
    private String[] fields;
    private String msgType;

    /**
     * Конвертирует сообщение в список объектов SearadarStationMessage.
     * @param message входное сообщение для конвертации
     * @return список объектов SearadarStationMessage, представляющих сконвертированные данные
     */
    public List<SearadarStationMessage> convert(String message) {

        List<SearadarStationMessage> msgList = new ArrayList<>();

        readFields(message);

        switch (msgType) {

            case "TTM" :
                msgList.add(getTTM());
                break;

            case "RSD" : {
                RadarSystemDataMessage rsd = getRSD();
                InvalidMessage invalidMessage = checkRSD(rsd);

                msgList.add(Objects.requireNonNullElse(invalidMessage, rsd));
                break;
            }

        }

        return msgList;
    }

    /** Чтение полей из сообщения */
    private void readFields(String msg) {

        String temp = msg.substring( 3, msg.indexOf("*") ).trim();

        fields = temp.split(Pattern.quote(","));
        msgType = fields[0];

    }

    /**
     * Получение сообщения типа TTM (Tracked Target Message).
     * @return объект TrackedTargetMessage, представляющий сообщение типа TTM
     */
    private TrackedTargetMessage getTTM() {
        TrackedTargetMessage ttm = new TrackedTargetMessage();
        Long msgRecTimeMillis = System.currentTimeMillis();

        ttm.setMsgTime(msgRecTimeMillis);
        IFF iff = IFF.UNKNOWN;
        TargetStatus status = TargetStatus.UNRELIABLE_DATA;
        TargetType type = TargetType.UNKNOWN;

        iff = switch (fields[11]) {
            case "b" -> IFF.FRIEND;
            case "p" -> IFF.FOE;
            case "d" -> IFF.UNKNOWN;
            default -> iff;
        };

        status = switch (fields[12]) {
            case "L" -> TargetStatus.LOST;
            case "Q" -> TargetStatus.UNRELIABLE_DATA;
            case "T" -> TargetStatus.TRACKED;
            default -> status;
        };

        ttm.setMsgRecTime(new Timestamp(System.currentTimeMillis()));
        ttm.setTargetNumber(Integer.parseInt(fields[1]));
        ttm.setDistance(Double.parseDouble(fields[2]));
        ttm.setBearing(Double.parseDouble(fields[3]));
        ttm.setCourse(Double.parseDouble(fields[6]));
        ttm.setSpeed(Double.parseDouble(fields[5]));
        ttm.setStatus(status);
        ttm.setIff(iff);

        ttm.setType(type);

        return ttm;
    }

    /**
     * Получение сообщения типа RSD (RadarSystemDataMessage).
     * @return объект RadarSystemDataMessage, представляющий сообщение типа RSD
     */
    private RadarSystemDataMessage getRSD() {

        RadarSystemDataMessage rsd = new RadarSystemDataMessage();

        rsd.setMsgRecTime(new Timestamp(System.currentTimeMillis()));

        rsd.setInitialDistance(Double.parseDouble(fields[1]));
        rsd.setInitialBearing(Double.parseDouble(fields[2]));
        rsd.setMovingCircleOfDistance(Double.parseDouble(fields[3]));
        rsd.setBearing(Double.parseDouble(fields[4]));
        rsd.setDistanceFromShip(Double.parseDouble(fields[9]));
        rsd.setBearing2(Double.parseDouble(fields[10]));
        rsd.setDistanceScale(Double.parseDouble(fields[11]));
        rsd.setDistanceUnit(fields[12]);
        rsd.setDisplayOrientation(fields[13]);
        rsd.setWorkingMode(fields[14]);

        return rsd;

    }

    /**
     * Проверка сообщения типа RSD (RadarSystemDataMessage) на корректность.
     * @param rsd объект RadarSystemDataMessage для проверки
     * @return объект InvalidMessage, если сообщение некорректно, иначе null
     */
    private InvalidMessage checkRSD(RadarSystemDataMessage rsd) {

        InvalidMessage invalidMessage = new InvalidMessage();
        String infoMsg = "";

        if (!Arrays.asList(DISTANCE_SCALE).contains(rsd.getDistanceScale())) {

            infoMsg = "RSD message. Wrong distance scale value: " + rsd.getDistanceScale();
            invalidMessage.setInfoMsg(infoMsg);
            return invalidMessage;
        }

        return null;
    }

}


