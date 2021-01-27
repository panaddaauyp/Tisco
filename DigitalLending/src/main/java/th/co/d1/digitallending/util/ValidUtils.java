package th.co.d1.digitallending.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

public class ValidUtils {

    final static Logger logger = Logger.getLogger(ValidUtils.class);

    public static boolean isNumber(String input) {
        return input.matches("[-+]?\\d*\\.?\\d+");
    }

    public static boolean strEmpty(String input) {
        return input == null || input.isEmpty() || input.equals("") || "".equals(input.trim()) || "null".equals(input.trim());
    }

    public static String null2Separator(Object input, String separator) {
        if (input != null) {
            if (input instanceof BigDecimal) {
                BigDecimal newInput = (BigDecimal) input;
                return newInput != null ? newInput.toString() : separator;
            } else if (input instanceof BigInteger) {
                BigInteger newInput = (BigInteger) input;
                return newInput != null ? newInput.toString() : separator;
            } else if (input instanceof Long) {
                Long newInput = (Long) input;
                return newInput != null ? newInput.toString() : separator;
            } else if (input instanceof Integer) {
                Integer newInput = (Integer) input;
                return newInput != null ? newInput.toString() : separator;
            } else if (input instanceof Double) {
                Double newInput = (Double) input;
                return newInput != null ? newInput.toString() : separator;
            } else if (input instanceof String) {
                String newInput = (String) input;
                return newInput != null ? newInput : separator;
            } else if (input instanceof Character) {
                Character newInput = (Character) input;
                return newInput != null ? newInput.toString() : separator;
            }
        }
        return separator;
    }

    public static String null2NoData(Object input) {
        return null2Separator(input, "");
    }

    public static boolean longEmpty(Long input) {
        return input == null || input == 0;
    }

    public static Character str2Char(String input) {
        if (strEmpty(input)) {
            return null;
        }
        return (Character) input.trim().charAt(0);
    }

    public static Date str2Date(String input) {
        Date date = null;
        if (input != null) {
            String str = (String) input;
            if (strEmpty(str)) {
                return null;
            }
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            try {
                date = df.parse(str);
            } catch (ParseException ex) {
                logger.error(ex.getMessage());
//                ex.printStackTrace();
            }
        }
        return date;
    }

    public static String dash2Slash(String input) {
        String[] date = input.split("-");
        String result = date[2] + "/" + date[1] + "/" + date[0];
        return result;
    }

    public static BigInteger str2BigInt(String input) {
        if (strEmpty(input) || !isNumber(input.replace(",", ""))) {
            return null;
        }
        input = input.replace(",", "").replace("null", "").trim();
        try {
            return new BigInteger(input);
        } catch (NumberFormatException e) {
            logger.error("" + e);
            e.printStackTrace();
            return null;
        }
    }

    public static BigDecimal str2Dec(String input) {
        if (strEmpty(input) || !isNumber(input.replace(",", ""))) {
            return null;
        }
        input = input.replace(",", "").replace("null", "").trim();
        try {
            return new BigDecimal(input);
        } catch (NumberFormatException e) {
            logger.error("" + e);
            e.printStackTrace();
            return null;
        }
    }

    public static Long str2BigLong(String input) {
        BigDecimal bigDec = str2Dec(input);
        return bigDec != null ? bigDec.longValue() : null;
    }

    public static long str2Long(String input) {
        BigDecimal bigDec = str2Dec(input);
        return bigDec != null ? bigDec.longValue() : 0;
    }

    public static Integer str2BigInteger(String input) {
        BigDecimal bigDec = str2Dec(input);
        return bigDec != null ? bigDec.intValue() : null;
    }

    public static int str2Int(String input) {
        BigDecimal bigDec = str2Dec(input);
        return bigDec != null ? bigDec.intValue() : 0;
    }

    public static short str2Short(String input) {
        BigDecimal bigDec = str2Dec(input);
        return bigDec != null ? bigDec.shortValue() : 0;
    }

    //convert Object to Other
    public static String obj2String(Object input) {
        if (input != null) {
            if (input instanceof Integer) {
                return input.toString();
            }
            String str = (String) input;
            if (strEmpty(str)) {
                return null;
            }
            return str.trim();
        }
        return null;
    }

    public static Character obj2Char(Object input) {
        String str = obj2String(input);
        if (strEmpty(str)) {
            return null;
        }
        return (Character) str.charAt(0);
    }

    public static BigDecimal obj2BigDec(Object input) {
        if (input != null) {
            if (input instanceof BigDecimal) {
                return (BigDecimal) input;
            } else if (input instanceof BigInteger) {
                return str2Dec("" + input);
            } else if (input instanceof Integer) {
                return str2Dec("" + input);
            } else if (input instanceof String) {
                return str2Dec(obj2String(input));
            } else {
                return str2Dec(("" + input).replace(",", ""));
            }
        }
        return null;
    }

    public static BigInteger obj2BigInt(Object input) {
        BigDecimal bigDec = obj2BigDec(input);
        return bigDec != null ? new BigInteger(bigDec.toString()) : null;
    }

    public static Long obj2Long(Object input) {
        BigDecimal bigDec;
        if (input instanceof BigDecimal) {
            bigDec = obj2BigDec(input);
            return bigDec != null ? bigDec.longValue() : null;
        } else if (input instanceof String) {
            bigDec = str2Dec(obj2String(input));
            return bigDec != null ? bigDec.longValue() : null;
        }
        return null;
    }

    public static int obj2Int(Object input) {
        BigDecimal bigDec = obj2BigDec(input);
        return bigDec != null ? bigDec.intValue() : 0;
    }

    public static short obj2Short(Object input) {
        BigDecimal bigDec = obj2BigDec(input);
        return bigDec != null ? bigDec.shortValue() : 0;
    }

    public static Timestamp date2TimeStamp(Date input) {
        if (DateUtils.dateEmpty(input)) {
            return null;
        }
        return new Timestamp(input.getTime());
    }

    public static Date str2Date(String date, String format) throws ParseException {
        if (strEmpty(date)) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.parse(date);
    }

    public static int obj2Integer(Object input) {
        BigDecimal bigDec = obj2BigDec(input);
        return bigDec != null ? bigDec.intValue() : null;
    }

    public static Double obj2Double(Object input) {
        BigDecimal bigDec = obj2BigDec(input);
        return bigDec != null ? bigDec.doubleValue() : 0;
    }

    public static Date str2Date543(String date) throws ParseException {
        String[] splitSlash = date.split("/");
        int fixYears = ValidUtils.str2Int(splitSlash[2]);
        fixYears = fixYears + 543;
        splitSlash[2] = String.valueOf(fixYears);
        Date endDate = ValidUtils.str2Date(splitSlash[0] + "/" + splitSlash[1] + "/" + splitSlash[2]);
        return endDate;
    }

    public static String getNewYear() {
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String[] splitDate = date.split("-");
        String newYear = splitDate[0] + "-01-01";
        return newYear;
    }

    public static String getCurrentDate() {
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        return date;
    }

    public static String escapeSql(String str) {
        String returnVal = "";
        if (str != null && !str.isEmpty()) {
            returnVal = StringEscapeUtils.escapeSql(StringEscapeUtils.escapeJavaScript(StringEscapeUtils.escapeHtml(str)));
        }
        return returnVal;
    }

    public static String priceWithDecimal(Double price) {
        DecimalFormat formatter = new DecimalFormat("###,###,###.00");
        return formatter.format(price);
    }

    public static String priceWithoutDecimal(Double price) {
        DecimalFormat formatter = new DecimalFormat("###,###,###.##");
        return formatter.format(price);
    }

    public static String priceToString(Double price) {
        String toShow = priceWithoutDecimal(price);
        if (toShow.indexOf(".") > 0) {
            return priceWithoutDecimal(price);
        } else {
            return priceWithDecimal(price);
        }
    }
}
