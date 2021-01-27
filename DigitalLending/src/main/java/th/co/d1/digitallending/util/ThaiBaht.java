/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 *
 * @author Kritsana Sasai
 *
 * DataOne Asia (Thailand) Company Limited 900/29 Rama III Rd. Bangpongpang,
 * Yannawa, Bangkok 10120 Tel :+66 (0) 2682 3000
 *
 * @create 05-09-2018 1:58:03 PM
 */
public class ThaiBaht {

    public static String getThaiBaht(BigDecimal amount) {
        String[] SYMBOLS_TH = {"ลบ", "บาท", "ถ้วน", "สตางค์", "ยี่", "เอ็ด", ",", " ", "฿"};
        StringBuilder builder = new StringBuilder();
//        if (null != amount && BigDecimal.ZERO.compareTo(amount) > 0) {
        BigDecimal absolute = amount.abs();
        int precision = absolute.precision();
        int scale = absolute.scale();
        int rounded_precision = ((precision - scale) + 2);
        MathContext mc = new MathContext(rounded_precision, RoundingMode.HALF_UP);
        BigDecimal rounded = absolute.round(mc);
        BigDecimal[] compound = rounded.divideAndRemainder(BigDecimal.ONE);
        boolean negativeAmount = (-1 == amount.compareTo(BigDecimal.ZERO));

        compound[0] = compound[0].setScale(0);
        compound[1] = compound[1].movePointRight(2);

        if (negativeAmount) {
            builder.append(SYMBOLS_TH[0]);
        }

        builder.append(getNumberText(compound[0].toBigIntegerExact()));
        builder.append(SYMBOLS_TH[1]);

        if (0 == compound[1].compareTo(BigDecimal.ZERO)) {
            builder.append(SYMBOLS_TH[2]);
        } else {
            builder.append(getNumberText(compound[1].toBigIntegerExact()));
            builder.append(SYMBOLS_TH[3]);
        }
//        } else {
//            builder.append(DIGIT_TH[0]);
//            builder.append(SYMBOLS_TH[1]);
//            builder.append(SYMBOLS_TH[2]);
//        }
        return builder.toString();
    }

    private static String getNumberText(BigInteger number) {
        StringBuffer buffer = new StringBuffer();
        char[] digits = number.toString().toCharArray();
        String[] SCALE_TH = {"ล้าน", "สิบ", "ร้อย", "พัน", "หมื่น", "แสน", ""};
        String[] DIGIT_TH = {"ศูนย์", "หนึ่ง", "สอง", "สาม", "สี่", "ห้า", "หก", "เจ็ด", "แปด", "เก้า"};
        String[] SYMBOLS_TH = {"ลบ", "บาท", "ถ้วน", "สตางค์", "ยี่", "เอ็ด", ",", " ", "฿"};
        for (int index = digits.length;
                index > 0; --index) {
            int digit = Integer.parseInt(String.valueOf(digits[digits.length - index]));
            String digitText = DIGIT_TH[digit];
            int scaleIdx = ((1 < index) ? ((index - 1) % 6) : 6);
            if ((1 == scaleIdx) && (2 == digit)) {
                digitText = SYMBOLS_TH[4];
            }
            switch (digit) {
                case 1:
                    switch (scaleIdx) {
                        case 0:
                        case 6:
                            buffer.append((index < digits.length) ? SYMBOLS_TH[5] : digitText);
                            break;
                        case 1:
                            break;
                        default:
                            buffer.append(digitText);
                            break;
                    }   break;
                case 0:
                    if (0 == scaleIdx) {
                        buffer.append(SCALE_TH[scaleIdx]);
                    } else if (digits.length == 1 && digit == 0) {
                        buffer.append(DIGIT_TH[digit]);
                    }
                    continue;
                default:
                    buffer.append(digitText);
                    break;
            }
            buffer.append(SCALE_TH[scaleIdx]);
        }

        return buffer.toString();
    }
}
