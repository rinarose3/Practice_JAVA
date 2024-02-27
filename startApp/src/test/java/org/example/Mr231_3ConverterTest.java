package org.example;

import static org.junit.jupiter.api.Assertions.*;

import org.example.searadar.mr231_3.convert.Mr231_3Converter;
import org.junit.jupiter.api.Test;
import ru.oogis.searadar.api.message.InvalidMessage;
import ru.oogis.searadar.api.message.RadarSystemDataMessage;
import ru.oogis.searadar.api.message.SearadarStationMessage;
import ru.oogis.searadar.api.message.TrackedTargetMessage;
import ru.oogis.searadar.api.types.IFF;
import ru.oogis.searadar.api.types.TargetStatus;

import java.util.List;

public class Mr231_3ConverterTest {

    @Test
    public void testConvertTTM() {
        // Входные данные
        String mr231_3_TTM = "$RATTM,28,28.99,160.0,T,88.4,064.0,T,4.7,77.7,N,b,L,,774920,А*59";

        // Создание экземпляра конвертера
        Mr231_3Converter converter = new Mr231_3Converter();

        // Вызов метода convert
        List<SearadarStationMessage> result = converter.convert(mr231_3_TTM);

        // Утверждения
        assertNotNull(result);
        assertEquals(1, result.size());
        assertInstanceOf(TrackedTargetMessage.class, result.get(0));

        // Проверка корректности полей
        TrackedTargetMessage ttm = (TrackedTargetMessage) result.get(0);
        assertEquals(28, ttm.getTargetNumber());
        assertEquals(28.99, ttm.getDistance());
        assertEquals(160.0, ttm.getBearing());
        assertEquals(88.4, ttm.getSpeed());
        assertEquals(64.0, ttm.getCourse());
        assertEquals(TargetStatus.LOST, ttm.getStatus());
        assertEquals(IFF.FRIEND, ttm.getIff());
    }

    @Test
    public void testConvertRSD() {
        // Входные данные
        String mr231_3_RSD = "$RARSD,36.5,331.4,8.4,320.6,,,,,11.6,185.3,96.0,N,N,S*33";

        // Создание экземпляра конвертера
        Mr231_3Converter converter = new Mr231_3Converter();

        // Вызов метода convert
        List<SearadarStationMessage> result = converter.convert(mr231_3_RSD);

        // Утверждения
        assertNotNull(result);
        assertEquals(1, result.size());
        assertInstanceOf(RadarSystemDataMessage.class, result.get(0));

        // Проверка корректности полей
        RadarSystemDataMessage rsd = (RadarSystemDataMessage) result.get(0);
        assertEquals(36.5, rsd.getInitialDistance());
        assertEquals(331.4, rsd.getInitialBearing());
        assertEquals(8.4, rsd.getMovingCircleOfDistance());
        assertEquals(320.6, rsd.getBearing());
        assertEquals(11.6, rsd.getDistanceFromShip());
        assertEquals(185.3, rsd.getBearing2());
        assertEquals(96.0, rsd.getDistanceScale());
        assertEquals("N", rsd.getDistanceUnit());
        assertEquals("N", rsd.getDisplayOrientation());
        assertEquals("S", rsd.getWorkingMode());
    }

    // Test invalid RSD message with wrong distance scale value
    @Test
    public void testInvalidRSDDistanceScale() {
        // Входные данные
        String invalidRSD = "$RARSD,36.5,331.4,8.4,320.6,,,,,11.6,185.3,500.0,N,N,S*33";

        // Создание экземпляра конвертера
        Mr231_3Converter converter = new Mr231_3Converter();

        // Вызов метода convert
        List<SearadarStationMessage> result = converter.convert(invalidRSD);

        // Утверждения
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0) instanceof InvalidMessage);

        // Обработка ошибки
        InvalidMessage invalidMessage = (InvalidMessage) result.get(0);
        assertEquals("RSD message. Wrong distance scale value: 500.0", invalidMessage.getInfoMsg());
    }


}
