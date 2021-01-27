/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.controller;

import com.tfglog.LogSingleton;
import com.tfglog.Log_decorator;
import com.tfglog.TfgLogger;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import org.springframework.web.bind.annotation.ResponseBody;
import static th.co.d1.digitallending.util.ApplicationStartup.headersJSON;
import th.co.d1.digitallending.util.DashBoardUtils;
import th.co.d1.digitallending.util.ValidUtils;

/**
 *
 * @author Kritsana Sasai
 *
 * DataOne Asia (Thailand) Company Limited 1023 MS Siam Tower, 30th Floor, Rama
 * 3 Rd., Chongnonsi, Yannawa, Bangkok, 10120
 *
 * @create 04-03-2020 8:32:24 PM
 */
@Controller
@RequestMapping("/shelf/dashboard/v1")
public class DashBoardV1Controller {

    Logger logger = Logger.getLogger(DashBoardV1Controller.class.getName());
    TfgLogger log = LogSingleton.getTfgLogger();

    @Log_decorator
    @RequestMapping(value = "/search/all", method = POST)
    @ResponseBody
    public ResponseEntity<?> getListDashboardAll(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String reqBody, @RequestHeader(value = "sub_state", required = false) String subState) {
        logger.info("POST : /shelf/dashboard/v1/search/all");
        log.info("POST : /shelf/dashboard/v1/search/all");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            JSONObject datas = new JSONObject(reqBody);
            if (datas.has("data")) {
                String dbEnv = subState;
                JSONObject json = datas.getJSONObject("data");
                String prodCode = (json.has("prodCode") ? json.getString("prodCode") : "");
//                Date startDate = ValidUtils.str2Date(json.has("startDate") ? json.getString("startDate") : "");
//                Date endDate = ValidUtils.str2Date(json.has("endDate") ? json.getString("endDate") : "");
                Integer status = null;//StatusUtils.getProspect(dbEnv).getStatusCode();
                Integer trnStatus = null;//StatusUtils.getPass(dbEnv).getStatusCode();
                JSONObject resp = DashBoardUtils.getDatasSysOperLogAll(dbEnv, prodCode, (json.has("startDate") ? json.getString("startDate") : ""), (json.has("endDate") ? json.getString("endDate") : ""), status, trnStatus);
                if (resp.get("status").equals(200) && resp.has("datas") && resp.getJSONArray("datas").length() > 0) {
                    returnVal.put("datas", resp.getJSONArray("datas"));
                } else {
                    returnVal.put("status", 500)
                            .put("description", resp.has("description") ? resp.getString("description") : "");
                }
            }
        } catch (JSONException e) {
            logger.info(e.getMessage());
            log.error(e.getMessage());
            //e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @Log_decorator
    @RequestMapping(value = "/search/product", method = POST)
    @ResponseBody
    public ResponseEntity<?> getListDashboardByProduct(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String reqBody, @RequestHeader(value = "sub_state", required = false) String subState) {
        logger.info("POST : /shelf/dashboard/v1/search/product");
        log.info("POST : /shelf/dashboard/v1/search/product");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            JSONObject datas = new JSONObject(reqBody);
            if (datas.has("data")) {
                String dbEnv = subState;
                JSONObject json = datas.getJSONObject("data");
                String prodCode = (json.has("prodCode") ? json.getString("prodCode") : "");
//                Date startDate = ValidUtils.str2Date(json.has("startDate") ? json.getString("startDate") : "");
//                Date endDate = ValidUtils.str2Date(json.has("endDate") ? json.getString("endDate") : "");
                Integer status = null;//StatusUtils.getProspect(dbEnv).getStatusCode();
                Integer trnStatus = null;//StatusUtils.getPass(dbEnv).getStatusCode();
                JSONObject resp = DashBoardUtils.getDatasSysOperLogProduct(dbEnv, prodCode, (json.has("startDate") ? json.getString("startDate") : ""), (json.has("endDate") ? json.getString("endDate") : ""), status, trnStatus);
                if (resp.get("status").equals(200) && resp.has("datas") && resp.getJSONArray("datas").length() > 0) {
                    returnVal.put("datas", resp.getJSONArray("datas"));
                } else {
                    returnVal.put("status", 500)
                            .put("description", resp.getString("description"));
                }
            }
        } catch (JSONException e) {
            logger.info(e.getMessage());
            log.error(e.getMessage());
            //e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @Log_decorator
    @RequestMapping(value = "/search/channel", method = POST)
    @ResponseBody
    public ResponseEntity<?> getListDashboardByChannel(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String reqBody, @RequestHeader(value = "sub_state", required = false) String subState) {
        logger.info("POST : /shelf/dashboard/v1/search/channel");
        log.info("POST : /shelf/dashboard/v1/search/channel");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            JSONObject datas = new JSONObject(reqBody);
            if (datas.has("data")) {
                String dbEnv = subState;
                JSONObject json = datas.getJSONObject("data");
                String prodCode = (json.has("prodCode") ? json.getString("prodCode") : "");
                Integer status = null;//StatusUtils.getProspect(dbEnv).getStatusCode();
                Integer trnStatus = null;//StatusUtils.getPass(dbEnv).getStatusCode();
                JSONObject resp = DashBoardUtils.getDatasSysOperLogChannel(dbEnv, prodCode, (json.has("startDate") ? json.getString("startDate") : ""), (json.has("endDate") ? json.getString("endDate") : ""), status, trnStatus);
                if (resp.get("status").equals(200) && resp.has("datas") && resp.getJSONArray("datas").length() > 0) {
                    returnVal.put("datas", resp.getJSONArray("datas"));
                } else {
                    returnVal.put("status", 500)
                            .put("description", resp.getString("description"));
                }
            }
        } catch (JSONException e) {
            logger.info(e.getMessage());
            log.error(e.getMessage());
            //e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @Log_decorator
    @RequestMapping(value = "/search/state", method = POST)
    @ResponseBody
    public ResponseEntity<?> getListDashboardByState(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String reqBody, @RequestHeader(value = "sub_state", required = false) String subState) {
        logger.info("POST : /shelf/dashboard/v1/search/state");
        log.info("POST : /shelf/dashboard/v1/search/state");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            JSONObject datas = new JSONObject(reqBody);
            if (datas.has("data")) {
                String dbEnv = subState;
                JSONObject json = datas.getJSONObject("data");
                String prodCode = (json.has("prodCode") ? json.getString("prodCode") : "");
                Integer status = json.has("status") ? ValidUtils.str2BigInteger(ValidUtils.obj2String(json.get("status"))) : null;      //201,203,xxx
                Integer trnStatus = json.has("trnStatus") ? ValidUtils.str2BigInteger(ValidUtils.obj2String(json.get("trnStatus"))) : null;//200,500
                String state = (json.has("state") ? json.getString("state") : "");
                String startDate = (json.has("startDate") ? json.getString("startDate") : "");
                String endDate = (json.has("endDate") ? json.getString("endDate") : "");
                JSONObject resp = DashBoardUtils.getDatasSysOperLogState(dbEnv, prodCode, startDate, endDate, status, trnStatus, state);
                if (resp.get("status").equals(200) && resp.has("datas") && resp.getJSONArray("datas").length() > 0) {
                    returnVal.put("datas", resp.getJSONArray("datas"));
                } else {
                    returnVal.put("status", 500)
                            .put("description", resp.getString("description"));
                }
            }
        } catch (JSONException e) {
            logger.info(e.getMessage());
            log.error(e.getMessage());
            //e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @Log_decorator
    @RequestMapping(value = "/search/period", method = POST)
    @ResponseBody
    public ResponseEntity<?> getListDashboardByPeriod(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String reqBody, @RequestHeader(value = "sub_state", required = false) String subState) {
        logger.info("POST : /shelf/dashboard/v1/search/period");
        log.info("POST : /shelf/dashboard/v1/search/period");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            JSONObject datas = new JSONObject(reqBody);
            if (datas.has("data")) {
                String dbEnv = subState;
                JSONObject json = datas.getJSONObject("data");
                String prodCode = (json.has("prodCode") ? json.getString("prodCode") : "");
                Integer status = json.has("status") ? ValidUtils.str2BigInteger(ValidUtils.obj2String(json.get("status"))) : null;      //201,203,xxx
                Integer trnStatus = json.has("trnStatus") ? ValidUtils.str2BigInteger(ValidUtils.obj2String(json.get("trnStatus"))) : null;//200,500
                String state = (json.has("state") ? json.getString("state") : "");
                String startDate = (json.has("startDate") ? json.getString("startDate") : "");
                String endDate = (json.has("endDate") ? json.getString("endDate") : "");
                JSONObject resp = DashBoardUtils.getDatasSysOperLogPeroid(dbEnv, prodCode, startDate, endDate, status, trnStatus, state);
                if (resp.get("status").equals(200) && resp.has("datas") && resp.getJSONArray("datas").length() > 0) {
                    returnVal.put("datas", resp.getJSONArray("datas"));
                } else {
                    returnVal.put("status", 500)
                            .put("description", resp.getString("description"));
                }
            }
        } catch (JSONException e) {
            logger.info(e.getMessage());
            log.error(e.getMessage());
            //e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @Log_decorator
    @RequestMapping(value = "/search/campaign", method = POST)
    @ResponseBody
    public ResponseEntity<?> getListDashboardByCampaign(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String reqBody, @RequestHeader(value = "sub_state", required = false) String subState) {
        logger.info("POST : /shelf/dashboard/v1/search/campaign");
        log.info("POST : /shelf/dashboard/v1/search/campaign");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            JSONObject datas = new JSONObject(reqBody);
            if (datas.has("data")) {
                String dbEnv = subState;
                JSONObject json = datas.getJSONObject("data");
                String prodCode = (json.has("prodCode") ? json.getString("prodCode") : "");
                Integer status = json.has("status") ? ValidUtils.str2BigInteger(ValidUtils.obj2String(json.get("status"))) : null;      //201,203,xxx
                Integer trnStatus = json.has("trnStatus") ? ValidUtils.str2BigInteger(ValidUtils.obj2String(json.get("trnStatus"))) : null; //200,500
                String state = (json.has("state") ? json.getString("state") : "");
                String startDate = (json.has("startDate") ? json.getString("startDate") : "");
                String endDate = (json.has("endDate") ? json.getString("endDate") : "");
                JSONObject resp = DashBoardUtils.getDatasSysOperLogCampaign(dbEnv, prodCode, startDate, endDate, status, trnStatus, state);
                if (resp.get("status").equals(200) && resp.has("datas") && resp.getJSONArray("datas").length() > 0) {
                    returnVal.put("datas", resp.getJSONArray("datas"));
                } else {
                    returnVal.put("status", 500)
                            .put("description", resp.getString("description"));
                }
            }
        } catch (JSONException e) {
            logger.info(e.getMessage());
            log.error(e.getMessage());
            //e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @Log_decorator
    @RequestMapping(value = "/search/prodchann", method = POST)
    @ResponseBody
    public ResponseEntity<?> getListDashboardProductChannel(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String reqBody, @RequestHeader(value = "sub_state", required = false) String subState) {
        logger.info("POST : /shelf/dashboard/v1/search/prodchann");
        log.info("POST : /shelf/dashboard/v1/search/prodchann");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            JSONObject datas = new JSONObject(reqBody);
            if (datas.has("data")) {
                String dbEnv = subState;
                JSONObject json = datas.getJSONObject("data");
                String prodCode = (json.has("prodCode") ? json.getString("prodCode") : "");
                String startDate = (json.has("startDate") ? json.getString("startDate") : "");
                String endDate = (json.has("endDate") ? json.getString("endDate") : "");
                String state = (json.has("state") ? json.getString("state") : "");
                Integer status = json.has("status") ? ValidUtils.str2BigInteger(ValidUtils.obj2String(json.get("status"))) : null;
                Integer trnStatus = json.has("trnStatus") ? ValidUtils.str2BigInteger(ValidUtils.obj2String(json.get("trnStatus"))) : null;
                JSONObject resp = DashBoardUtils.getDatasSysOperLogProdChannel(dbEnv, prodCode, startDate, endDate, status, trnStatus, state);
                if (resp.get("status").equals(200) && resp.has("datas") && resp.getJSONArray("datas").length() > 0) {
                    returnVal.put("datas", resp.getJSONArray("datas"));
                } else {
                    returnVal.put("status", 500)
                            .put("description", resp.has("description") ? resp.getString("description") : "");
                }
            }
        } catch (JSONException e) {
            logger.info(e.getMessage());
            log.error(e.getMessage());
            //e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @Log_decorator
    @RequestMapping(value = "/search/prodstatestatus", method = POST)
    @ResponseBody
    public ResponseEntity<?> getListDashboardProductStateStatus(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String reqBody, @RequestHeader(value = "sub_state", required = false) String subState) {
        logger.info("POST : /shelf/dashboard/v1/search/prodstatestatus");
        log.info("POST : /shelf/dashboard/v1/search/prodstatestatus");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            JSONObject datas = new JSONObject(reqBody);
            if (datas.has("data")) {
                String dbEnv = subState;
                JSONObject json = datas.getJSONObject("data");
                String prodCode = (json.has("prodCode") ? json.getString("prodCode") : "");
                String startDate = (json.has("startDate") ? json.getString("startDate") : "");
                String endDate = (json.has("endDate") ? json.getString("endDate") : "");
                String state = (json.has("state") ? json.getString("state") : "");
                Integer status = json.has("status") ? ValidUtils.str2BigInteger(ValidUtils.obj2String(json.get("status"))) : null;
                Integer trnStatus = json.has("trnStatus") ? ValidUtils.str2BigInteger(ValidUtils.obj2String(json.get("trnStatus"))) : null;
                JSONObject resp = DashBoardUtils.getDatasSysOperLogProdStateStatus(dbEnv, prodCode, startDate, endDate, status, trnStatus, state);
                if (resp.get("status").equals(200) && resp.has("datas") && resp.getJSONArray("datas").length() > 0) {
                    returnVal.put("datas", resp.getJSONArray("datas"));
                } else {
                    returnVal.put("status", 500)
                            .put("description", resp.has("description") ? resp.getString("description") : "");
                }
            }
        } catch (JSONException e) {
            logger.info(e.getMessage());
            log.error(e.getMessage());
            //e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }
}
