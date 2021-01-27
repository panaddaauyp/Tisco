/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
//import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
//import net.sf.jasperreports.engine.JREmptyDataSource;
//import net.sf.jasperreports.engine.JRException;
//import net.sf.jasperreports.engine.JasperExportManager;
//import net.sf.jasperreports.engine.JasperFillManager;
//import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Utils {

//    public static String OS = System.getProperty("os.name").toLowerCase();
//    static final Pattern pattern = Pattern.compile("([0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12})");
    final static Logger logger = Logger.getLogger(Utils.class);

    public static String escapeSql(String str) {
        String returnVal = "";
        if (str != null && !str.isEmpty()) {
            returnVal = StringEscapeUtils.escapeSql(StringEscapeUtils.escapeJavaScript(StringEscapeUtils.escapeHtml(str)));
        }
        return returnVal;
    }

    public static String convertFormatDate2Str(Date date) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return f.format(date);
    }

    public static long str2GetTime(String str_date) {
        try {
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
            Date parseDate = f.parse(str_date);
            return parseDate.getTime();
        } catch (ParseException e) {
            logger.error("" + e);
            e.printStackTrace();
            return 0000000;
        }
    }

    public static String getUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public static String getEncodePwd(String pwd) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");//SHA
        md.update(pwd.getBytes());

        byte byteData[] = md.digest();

        //convert the byte to hex format method 1
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
//        Base64.encodeBase64(byteData);
        return sb.toString();
    }

    public static boolean isUUID(String uuid) {
        if (uuid == null) {
            return false;
        } else {
            Pattern pattern = Pattern.compile("([0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12})");
            return pattern.matcher(uuid).matches();
        }
    }

    public static String getLeftMenu(HttpSession session) {
        String returnMenu = "left_menu";
        if (session.getAttribute("providerType").equals("proxy") || session.getAttribute("providerType").equals("AdminSystem")) {
            returnMenu = "left_menu.jsp";
        }
        if (session.getAttribute("providerType").equals("idp")) {
            returnMenu = "left_menu_idp.jsp";
        }
        if (session.getAttribute("providerType").equals("rp")) {
            returnMenu = "left_menu_rp.jsp";
        }
        return returnMenu;
    }

    /*
    public static String getSaltString(int i) {
        String SALTCHARS = "ABCDEFGHIJKLMNPQRSTUVWXYZabcdefghijklmnpqrstuvwxyz1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < i) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        return salt.toString();
    }
     */
    public static Date convert2Date(String strDate) {
        Date date = new Date();
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            date = formatter.parse(strDate);
        } catch (ParseException ex) {
            logger.error(ex.getMessage());
        }
        return date;
    }

    public static List<JSONObject> sortJSONObjectByKey(List<JSONObject> jsonList, String kName, boolean minToMax) {
        try {
            Collections.sort(jsonList, new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject a, JSONObject b) {
                    Long valA = null;
                    Long valB = null;
                    try {
                        valA = a.getLong(kName);
                        valB = b.getLong(kName);
                    } catch (JSONException e) {
                        //do something
                        logger.error("" + e);
                        e.printStackTrace();
                    }
                    if (minToMax) {
                        return valA.compareTo(valB);
                    } else {
                        return valB.compareTo(valA);
                    }
                }
            });
        } catch (NullPointerException e) {
            logger.error("" + e);
            e.printStackTrace();
        }
        return jsonList;
    }

    public static List<JSONObject> sortJSONObjectByKeyDate(List<JSONObject> jsonList, String kName, boolean minToMax) {
        try {
            Collections.sort(jsonList, new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject a, JSONObject b) {
                    Date valA = null;
                    Date valB = null;
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        try {
                            valA = sdf.parse(a.getString(kName));
                            valB = sdf.parse(b.getString(kName));
                        } catch (ParseException ex) {
                            java.util.logging.Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } catch (JSONException e) {
                        //do something
                        logger.error("" + e);
                        e.printStackTrace();
                    }
                    if (minToMax) {
                        return valA.compareTo(valB);
                    } else {
                        return valB.compareTo(valA);
                    }
                }
            });
        } catch (NullPointerException e) {
            logger.error("" + e);
            e.printStackTrace();
        }
        return jsonList;
    }
    public static List<JSONObject> sortJSONObjectByString(List<JSONObject> jsonList, String kName, boolean minToMax) {
        try {
            Collections.sort(jsonList, new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject a, JSONObject b) {
                    String valA = null;
                    String valB = null;
                    try {
                        valA = a.getString(kName);
                        valB = b.getString(kName);
                    } catch (JSONException e) {
                        //do something
                        logger.error("" + e);
                        e.printStackTrace();
                    }
                    if (minToMax) {
                        return valA.compareTo(valB);
                    } else {
                        return valB.compareTo(valA);
                    }
                }
            });
        } catch (NullPointerException e) {
            logger.error("" + e);
            e.printStackTrace();
        }
        return jsonList;
    }

    /*
    public JSONObject generatePDFIncreaseLimitContract(JSONObject pdfParameter, String outputPath) {
//        String outputPath = "", filePath = "", filePathLogo = "";
        JSONObject obj = new JSONObject();
        boolean result = true;
        File files = new File(outputPath);
        if (!files.exists()) {
            if (files.mkdirs()) {
                result = true;
            } else {
                result = false;
            }
        }
        String sourceFile = "/report/IncreaseLimitContract.jasper";
        if (result && !"".equalsIgnoreCase(sourceFile)) {
            String fileName = DateUtils.getLongDate() + ".pdf";
            try {
                InputStream jasperFileName = getClass().getResourceAsStream(sourceFile);
//                String jasperFileName = "C:/TISCO/" + sourceFile;
                String pdfFileName = outputPath + "/" + fileName;
                Iterator<String> keys = pdfParameter.keys();
                Map hm = new HashMap();
                while (keys.hasNext()) {
                    String key = keys.next();
                    if (pdfParameter.get(key) instanceof String) {
                        System.out.println("key : " + key + "value : " + pdfParameter.getString(key));
                        hm.put(key, pdfParameter.getString(key));
                    }
                }
//                System.out.println(hm);
                //-----เอา template กับ logo กับ บอกให้เชื่อมต่อ
                JasperPrint jprint = (JasperPrint) JasperFillManager.fillReport(jasperFileName, hm, new JREmptyDataSource());
//                JasperPrint jprint = JasperFillManager.fillReport(
//                        jasperFileName,
//                        hm,
//                        new JREmptyDataSource());
                JasperExportManager.exportReportToPdfFile(jprint, pdfFileName);
//                File pdf = new File(pdfFileName);
//                String base64 = encodeFileToBase64Binary(pdf);
//                Files.deleteIfExists(Paths.get(fileName + ".pdf"));
//                obj.put("base64", base64);
                obj.put("errorMsg", "");
                result = true;
            } //                f.printStackTrace();
            catch (JRException e) {
                result = false;
                obj.put("errorMsg", "" + e);
//                e.printStackTrace();
            }
        } else {
            result = false;
            obj.put("errorMsg", "No source file.");
        }
        obj.put("result", result);
        return obj;
    }

    public static String encodeFileToBase64Binary(File file) throws IOException {
        String encodedfile = null;
        try {
            FileInputStream fileInputStreamReader = new FileInputStream(file);
            byte[] bytes = new byte[(int) file.length()];
            fileInputStreamReader.read(bytes);
            encodedfile = new String(Base64.getEncoder().encode(bytes), "UTF-8");
            fileInputStreamReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
        return encodedfile;
    }
     */
    public static ResponseEntity<String> returnValue(String ContentType, String returnValue, HttpStatus httpStatus) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", ContentType + "; charset=UTF-8");
        return (new ResponseEntity<>(returnValue.replaceAll("\\\\", ""), headers, httpStatus));
    }

    public static String validateSubStateFromHeader(HttpServletRequest req) {
        String subState = HibernateUtil.defaultDB;
        if (req != null && req.getHeader("sub_state") != null) {
            subState = req.getHeader("sub_state");
        }
        return subState;
    }
}
