package org.example;

import static org.junit.jupiter.api.Assertions.*;

import org.example.searadar.mr231.convert.Mr231Converter;
import org.junit.jupiter.api.Test;
import ru.oogis.searadar.api.message.RadarSystemDataMessage;
import ru.oogis.searadar.api.message.SearadarStationMessage;
import ru.oogis.searadar.api.message.TrackedTargetMessage;
import ru.oogis.searadar.api.message.WaterSpeedHeadingMessage;
import ru.oogis.searadar.api.types.IFF;
import ru.oogis.searadar.api.types.TargetStatus;

import java.util.List;
public class Mr231ConverterTest {

    @Test
    public void testConvertTTM() {
        // Входные данные
        String mr231_TTM = "$RATTM,66,28.71,341.1,T,57.6,024.5,T,0.4,4.1,N,b,L,,457362,А*42";

        // Создание экземпляра конвертера
        Mr231Converter converter = new Mr231Converter();

        // Вызов метода convert
        List<SearadarStationMessage> result = converter.convert(mr231_TTM);

        // Утверждения
        assertNotNull(result);
        assertEquals(1, result.size());
        assertInstanceOf(TrackedTargetMessage.class, result.get(0));

        // Проверка корректности полей
        TrackedTargetMessage ttm = (TrackedTargetMessage) result.get(0);
        assertEquals(66, ttm.getTargetNumber());
        assertEquals(28.71, ttm.getDistance());
        assertEquals(341.1, ttm.getBearing());
        assertEquals(57.6, ttm.getSpeed());
        assertEquals(24.5, ttm.getCourse());
        assertEquals(TargetStatus.LOST, ttm.getStatus());
        assertEquals(IFF.FRIEND, ttm.getIff());
    }

    @Test
    public void testConvertVHW() {
        // Входные данные
        String mr231_VHW = "$RAVHW,115.6,T,,,46.0,N,,*71";

        // Создание экземпляра конвертера
        Mr231Converter converter = new Mr231Converter();

        // Вызов метода convert
        List<SearadarStationMessage> result = converter.convert(mr231_VHW);

        // Утверждения
        assertNotNull(result);
        assertEquals(1, result.size());
        assertInstanceOf(WaterSpeedHeadingMessage.class, result.get(0));

        // Проверка корректности полей
        WaterSpeedHeadingMessage vhw = (WaterSpeedHeadingMessage) result.get(0);
        assertEquals(115.6, vhw.getCourse());
        assertEquals("T", vhw.getCourseAttr());
        assertEquals(46.0, vhw.getSpeed());
    }

    @Test
    public void testConvertRSD() {
        // Входные данные
        String mr231_RSD = "$RARSD,50.5,309.9,64.8,132.3,,,,,52.6,155.0,48.0,K,N,S*28";

        // Создание экземпляра конвертера
        Mr231Converter converter = new Mr231Converter();

        // Вызов метода convert
        List<SearadarStationMessage> result = converter.convert(mr231_RSD);

        // Утверждения
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0) instanceof RadarSystemDataMessage);

        // Проверка корректности полей
        RadarSystemDataMessage rsd = (RadarSystemDataMessage) result.get(0);
        assertEquals(50.5, rsd.getInitialDistance());
        assertEquals(309.9, rsd.getInitialBearing());
        assertEquals(64.8, rsd.getMovingCircleOfDistance());
        assertEquals(132.3, rsd.getBearing());
        assertEquals(52.6, rsd.getDistanceFromShip());
        assertEquals(155.0, rsd.getBearing2());
        assertEquals(48.0, rsd.getDistanceScale());
        assertEquals("K", rsd.getDistanceUnit());
        assertEquals("N", rsd.getDisplayOrientation());
        assertEquals("S", rsd.getWorkingMode());
    }
}
