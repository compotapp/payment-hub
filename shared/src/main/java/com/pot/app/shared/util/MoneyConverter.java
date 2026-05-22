package com.pot.app.shared.util;

import java.math.BigDecimal;

import static java.math.RoundingMode.HALF_UP;

public class MoneyConverter {

    /**
     * Конвертирует дробные единицы (копейки/центы) в основную валюту (BigDecimal).
     */
    public static BigDecimal toMajorUnit(long minorUnits) {
        return BigDecimal.valueOf(minorUnits)
                .movePointLeft(2)
                .setScale(2, HALF_UP);
    }

    /**
     * Конвертирует основную валюту (BigDecimal) в дробные единицы (long).
     */
    public static long toMinorUnit(BigDecimal majorUnits) {
        if (majorUnits == null) {
            return 0L;
        }
        return majorUnits.movePointRight(2)
                .setScale(0, HALF_UP)
                .longValue();
    }
}
