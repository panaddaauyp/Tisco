/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.controller;

import com.tfglog.LogSingleton;
import com.tfglog.Log_decorator;
import com.tfglog.TfgLogger;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.ParseException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.logging.Logger;
import org.hibernate.HibernateException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import org.springframework.web.bind.annotation.ResponseBody;
import th.co.d1.digitallending.dao.SysOperLogDao;
import static th.co.d1.digitallending.util.ApplicationStartup.headersJSON;
import th.co.d1.digitallending.util.StatusUtils;

/**
 *
 * @author Kritsana Sasai
 *
 * DataOne Asia (Thailand) Company Limited 1023 MS Siam Tower, 30th Floor, Rama
 * 3 Rd., Chongnonsi, Yannawa, Bangkok, 10120
 *
 * @create 04-03-2020 12:27:09 PM
 */
@Controller
@RequestMapping("/shelf/report/v1")
public class ReportSysOperLogV1Controller {

    Logger logger = Logger.getLogger(ReportSysOperLogV1Controller.class.getName());
    TfgLogger log = LogSingleton.getTfgLogger();

    @Log_decorator
    @RequestMapping(value = "/search/reconcile", method = POST)
    @ResponseBody
    public ResponseEntity<?> getListReconcile(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String reqBody, @RequestHeader(value = "sub_state", required = false) String subState) {
        logger.info("POST : /shelf/report/v1/search/reconcile");
        log.info("POST : /shelf/report/v1/search/reconcile");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            JSONObject datas = new JSONObject(reqBody);
            SysOperLogDao dao = new SysOperLogDao();
            if (datas.has("data")) {
                JSONObject json = datas.getJSONObject("data");
                String prodCode = json.has("prodCode") ? json.getString("prodCode") : "";                 //productCode
                String startDate = json.has("startDate") ? json.getString("startDate") : "";    //BusinessDate
                String endDate = json.has("endDate") ? json.getString("endDate") : "";
                returnVal.put("datas", dao.getReconcileReport(subState, prodCode, startDate, endDate));
            }
        } catch (JSONException | SQLException | ParseException | UnsupportedEncodingException | HibernateException | NullPointerException e) {
            logger.info(e.getMessage());
            log.error(e.getMessage());
            //e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @Log_decorator
    @RequestMapping(value = "/transaction/preview", method = POST)
    @ResponseBody
    public ResponseEntity<?> getListTransactionPreview(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String reqBody, @RequestHeader(value = "sub_state", required = false) String subState) {
        logger.info("POST : /shelf/report/v1/transaction/preview");
        log.info("POST : /shelf/report/v1/transaction/preview");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
//            JSONArray jsonArr = new JSONArray();
            JSONObject datas = new JSONObject(reqBody);
            if (datas.has("data")) {
                JSONObject json = datas.getJSONObject("data");
                String prodCode = json.has("prodCode") ? json.getString("prodCode") : "";                 //attr1
                String txnId = json.has("txnId") ? json.getString("txnId") : "";
                String refNo = json.has("refNo") ? json.getString("refNo") : "";
                String ucId = json.has("ucId") ? json.getString("ucId") : "";
                String paymentMethod = json.has("paymentMethod") ? json.getString("paymentMethod") : "";
                String startDate = json.has("startDate") ? json.getString("startDate") : "";    //create_at
                String endDate = json.has("endDate") ? json.getString("endDate") : "";
                String startTime = json.has("startTime") ? json.getString("startTime") : "";    //create_at
                String endTime = json.has("endTime") ? json.getString("endTime") : "";
                Integer status = json.has("status") && !json.getString("status").isEmpty() ? Integer.parseInt(json.getString("status")) : null;
                String state = json.has("state") ? json.getString("state") : "";
                String refTxnId = (json.has("refTxnId") ? json.getString("refTxnId") : "");
                returnVal.put("datas", new SysOperLogDao().getTransactionReport(subState, prodCode, txnId, refNo, ucId, paymentMethod, startDate, endDate, startTime, endTime, status, state, refTxnId));
            }
        } catch (JSONException | SQLException | UnsupportedEncodingException | ParseException | NullPointerException | HibernateException e) {
            logger.info(e.getMessage());
            log.error("" + e);
            //e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @Log_decorator
    @RequestMapping(value = "/inquiry/search", method = POST)
    @ResponseBody
    public ResponseEntity<?> getListInquiryPreview(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String reqBody, @RequestHeader(value = "sub_state", required = false) String subState) {
        logger.info("POST : /shelf/report/v1/inquiry/search");
        log.info("POST : /shelf/report/v1/inquiry/search");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            JSONObject datas = new JSONObject(reqBody);
            if (datas.has("data")) {
                JSONObject json = datas.getJSONObject("data");
                String compName = (json.has("compName") ? json.getString("compName") : "");
                String groupProduct = (json.has("groupProduct") ? json.getString("groupProduct") : "");
                String ucid = (json.has("ucid") ? json.getString("ucid") : ""); //attr1
                String prodCode = (json.has("prodCode") ? json.getString("prodCode") : "");
                String refNo = (json.has("refNo") ? json.getString("refNo") : "");
                String paymentMethod = (json.has("paymentMethod") ? json.getString("paymentMethod") : "");
                String txnId = (json.has("txnId") ? json.getString("txnId") : "");
                Integer status = json.has("status") && !json.getString("status").isEmpty() ? Integer.parseInt(json.getString("status")) : null;
                String state = (json.has("state") ? json.getString("state") : "");
                String txnDateStart = json.has("txnDateStart") ? json.getString("txnDateStart") : "";    //create_at
                String txnDateEnd = json.has("txnDateEnd") ? json.getString("txnDateEnd") : "";
                String txnStartTime = json.has("txnStartTime") ? json.getString("txnStartTime") : "";    //create_at
                String txnEndTime = json.has("txnEndTime") ? json.getString("txnEndTime") : "";
                String paymentDateStart = json.has("paymentDateStart") ? json.getString("paymentDateStart") : "";    //create_at
                String paymentDateEnd = json.has("paymentDateEnd") ? json.getString("paymentDateEnd") : "";
                String refTxnId = (json.has("refTxnId") ? json.getString("refTxnId") : "");
                returnVal.put("datas", new SysOperLogDao().getInquiryTransaction(subState, compName, groupProduct, ucid, prodCode, refNo, paymentMethod, txnId, txnDateStart, txnStartTime, txnDateEnd, txnEndTime, status, state, paymentDateStart, paymentDateEnd, refTxnId));
            }
        } catch (JSONException | SQLException | UnsupportedEncodingException | ParseException | NullPointerException | HibernateException e) {
            logger.info(e.getMessage());
            log.error("" + e);
            //e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @Log_decorator
    @RequestMapping(value = "/transaction/error", method = POST)
    @ResponseBody
    public ResponseEntity<?> getListTransactionError(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String reqBody, @RequestHeader(value = "sub_state", required = false) String subState) {
        logger.info("POST : /shelf/report/v1/transaction/error");
        log.info("POST : /shelf/report/v1/transaction/error");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
//            JSONArray jsonArr = new JSONArray();
            JSONObject datas = new JSONObject(reqBody);
            if (datas.has("data")) {
                JSONObject data = datas.getJSONObject("data");
//                JSONObject data = new JSONObject();
                returnVal.put("datas", new SysOperLogDao().getErrorLogList(subState, data));
            }
        } catch (JSONException | SQLException | ParseException | HibernateException | NullPointerException e) {
            logger.info(e.getMessage());
            log.error("" + e);
            //e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @Log_decorator
    @RequestMapping(value = "/transfer/search", method = POST)
    @ResponseBody
    public ResponseEntity<?> getListTransferPreview(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String reqBody, @RequestHeader(value = "sub_state", required = false) String subState) {
        logger.info("POST : /shelf/report/v1/transfer/search");
        log.info("POST : /shelf/report/v1/transfer/search");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
//            JSONArray jsonArr = new JSONArray();
            JSONObject datas = new JSONObject(reqBody);
            if (datas.has("data")) {
                JSONObject json = datas.getJSONObject("data");
                String compCode = (json.has("compCode") ? json.getString("compCode") : "");
                String groupProduct = (json.has("groupProduct") ? json.getString("groupProduct") : "");
                String ucid = (json.has("ucid") ? json.getString("ucid") : ""); //attr1
                String prodCode = (json.has("prodCode") ? json.getString("prodCode") : "");
                String refNo = (json.has("refNo") ? json.getString("refNo") : "");
                String paymentMethod = (json.has("paymentMethod") ? json.getString("paymentMethod") : "");
                String txnId = (json.has("txnId") ? json.getString("txnId") : "");
                Integer status = json.has("status") && !json.getString("status").isEmpty() ? Integer.parseInt(json.getString("status")) : null;
                String state = (json.has("state") ? json.getString("state") : "");
                String txnDateStart = json.has("txnDateStart") ? json.getString("txnDateStart") : "";    //create_at
                String txnDateEnd = json.has("txnDateEnd") ? json.getString("txnDateEnd") : "";
                String txnStartTime = json.has("txnStartTime") ? json.getString("txnStartTime") : "";    //create_at
                String txnEndTime = json.has("txnEndTime") ? json.getString("txnEndTime") : "";
                String paymentDateStart = json.has("paymentDateStart") ? json.getString("paymentDateStart") : "";    //create_at
                String paymentDateEnd = json.has("paymentDateEnd") ? json.getString("paymentDateEnd") : "";
                String refTxnId = (json.has("refTxnId") ? json.getString("refTxnId") : "");
                String traceNo = (json.has("traceNo") ? json.getString("traceNo") : "");
                String minAmount = (json.has("minAmount") ? json.getString("minAmount") : "");
                String maxAmount = (json.has("maxAmount") ? json.getString("maxAmount") : "");
                Integer prospect = StatusUtils.getProspect(subState).getStatusCode();
                returnVal.put("datas", new SysOperLogDao().getTransferTransaction(subState, compCode, groupProduct, ucid, prodCode, refNo, paymentMethod, txnId, txnDateStart, txnStartTime, txnDateEnd, txnEndTime,
                        status, state, paymentDateStart, paymentDateEnd, refTxnId, traceNo, minAmount, maxAmount, prospect));
            }
        } catch (JSONException | SQLException | UnsupportedEncodingException | ParseException | NullPointerException | HibernateException e) {
            logger.info(e.getMessage());
            log.error("" + e);
//            e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    
    @Log_decorator
    @RequestMapping(value = "/history/search", method = POST)
    @ResponseBody
    public ResponseEntity<?> getListHistoryPreview(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String reqBody, @RequestHeader(value = "sub_state", required = false) String subState) {
        logger.info("POST : /shelf/report/v1/history/search");
        log.info("POST : /shelf/report/v1/history/search");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            JSONObject datas = new JSONObject(reqBody);
            if (datas.has("data")) {
                JSONObject json = datas.getJSONObject("data");
                String compName = (json.has("compName") ? json.getString("compName") : "");
                String groupProduct = (json.has("groupProduct") ? json.getString("groupProduct") : "");
                String ucid = (json.has("ucid") ? json.getString("ucid") : ""); //attr1
                String prodCode = (json.has("prodCode") ? json.getString("prodCode") : "");
                String refNo = (json.has("refNo") ? json.getString("refNo") : "");
                String paymentMethod = (json.has("paymentMethod") ? json.getString("paymentMethod") : "");
                String txnId = (json.has("txnId") ? json.getString("txnId") : "");
                Integer status = json.has("status") && !json.getString("status").isEmpty() ? Integer.parseInt(json.getString("status")) : null;
                String state = (json.has("state") ? json.getString("state") : "");
                String txnDateStart = json.has("txnDateStart") ? json.getString("txnDateStart") : "";    //create_at
                String txnDateEnd = json.has("txnDateEnd") ? json.getString("txnDateEnd") : "";
                String txnStartTime = json.has("txnStartTime") ? json.getString("txnStartTime") : "";    //create_at
                String txnEndTime = json.has("txnEndTime") ? json.getString("txnEndTime") : "";
                String paymentDateStart = json.has("paymentDateStart") ? json.getString("paymentDateStart") : "";    //create_at
                String paymentDateEnd = json.has("paymentDateEnd") ? json.getString("paymentDateEnd") : "";
                String refTxnId = (json.has("refTxnId") ? json.getString("refTxnId") : "");
                returnVal.put("datas", new SysOperLogDao().getDataHistory(subState, compName, groupProduct, ucid, prodCode, refNo, paymentMethod, txnId, txnDateStart, txnStartTime, txnDateEnd, txnEndTime, status, state, paymentDateStart, paymentDateEnd, refTxnId));
            }
        } catch (JSONException | SQLException | UnsupportedEncodingException | ParseException | NullPointerException | HibernateException e) {
            logger.info(e.getMessage());
            log.error("" + e);
            //e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }
}
