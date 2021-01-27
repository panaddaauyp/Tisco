package th.co.d1.digitallending.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import org.json.JSONObject;

public class HttpUtil {

    final static Logger logger = Logger.getLogger(HttpUtil.class.getName());
    /*
    static {
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }
            }

        };

        // Install the all-trusting trust manager
        SSLContext sc;
        try {
            sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (NoSuchAlgorithmException | KeyManagementException | NullPointerException e) {
            logger.info("" + e);  //e.printStackTrace();
        }

        HostnameVerifier allHostsValid = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }
*/
    /* getValue from request */
    public static JSONObject getPostParam(HttpServletRequest request) {
//        System.out.println("Request -> "+request.toString());
        JSONObject returnVal = new JSONObject();
        BufferedReader reader = null;
        try {
            StringBuilder jb = new StringBuilder();
            String line;
            reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }
            reader.close();
            //            System.out.println(new java.util.Date() + " : " + jb.toString());
            returnVal = new JSONObject(jb.toString());
        } catch (IOException | NullPointerException ex) {
            logger.info(ex.getMessage());
            return returnVal;
        } finally {
            try {
                reader.close();
            } catch (IOException | NullPointerException ex) {
                logger.info(ex.getMessage());
            }
        }
        return returnVal;

    }

    public static JSONObject getHeaderParam(HttpServletRequest request) {
        JSONObject returnValue = new JSONObject();
        try {
            String authorization = request.getHeader("Authorization");
//            String authCode = request.getHeader("authCode");

            if (authorization != null && authorization.toLowerCase().startsWith("basic")) {
                // Authorization: Basic base64credentials
                String base64Credentials = authorization.substring("Basic".length()).trim();
                byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
                String credentials = new String(credDecoded, StandardCharsets.UTF_8);
                // credentials = username:password
                String[] values = credentials.split(":", 2);
                returnValue.put("userName", values[0])
                        .put("password", values[1])
                        .put("type", "basic");
            } else if (authorization.toLowerCase().startsWith("bearer")) {
//                System.out.println(authorization.replace("Bearer", "").trim());
                returnValue.put("token", authorization.replace("Bearer", "").trim())
                        .put("type", "bearer");;

            }
        } catch (NullPointerException e) {
            logger.info("" + e);  //e.printStackTrace();
        }
        return returnValue;
    }
}
