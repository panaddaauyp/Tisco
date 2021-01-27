/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.controller;

import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import org.springframework.web.bind.annotation.ResponseBody;
import th.co.d1.digitallending.dao.SysOperLogDao;
import static th.co.d1.digitallending.util.ApplicationStartup.headersJSON;
import th.co.d1.digitallending.util.DateUtils;
import th.co.d1.digitallending.util.Utils;

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

    Logger logger = Logger.getLogger(ReportSysOperLogV1Controller.class);

    @RequestMapping(value = "/search/reconcile", method = POST)
    @ResponseBody
    public ResponseEntity<?> getListReconcile(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String payload) {
        logger.info("POST : /shelf/report/v1/search/reconcile");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            JSONObject datas = new JSONObject(payload);
            SysOperLogDao dao = new SysOperLogDao();
            if (datas.has("data")) {
                JSONObject json = datas.getJSONObject("data");
                String prodCode = json.has("prodCode") ? json.getString("prodCode") : "";                 //productCode
                String startDate = json.has("startDate") ? json.getString("startDate") : "";    //BusinessDate
                String endDate = json.has("endDate") ? json.getString("endDate") : "";
                returnVal.put("datas", dao.getReconcileReport(Utils.validateSubStateFromHeader(request), prodCode, startDate, endDate));
            }
        } catch (JSONException e) {
            logger.error("" + e);
            e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @RequestMapping(value = "/transaction/preview", method = POST)
    @ResponseBody
    public ResponseEntity<?> getListTransactionPreview(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String payload) {
        logger.info("POST : /shelf/report/v1/transaction/preview");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
//            JSONArray jsonArr = new JSONArray();
            JSONObject datas = new JSONObject(payload);
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
                returnVal.put("datas", new SysOperLogDao().getTransactionReport(Utils.validateSubStateFromHeader(request), prodCode, txnId, refNo, ucId, paymentMethod, startDate, endDate, startTime, endTime, status, state, refTxnId));
            }
        } catch (JSONException e) {
            logger.error("" + e);
            e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @RequestMapping(value = "/inquiry/search", method = POST)
    @ResponseBody
    public ResponseEntity<?> getListInquiryPreview(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String payload) {
        logger.info("POST : /shelf/report/v1/inquiry/search");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
//            JSONArray jsonArr = new JSONArray();
            JSONObject datas = new JSONObject(payload);
            if (datas.has("data")) {
                JSONObject json = datas.getJSONObject("data");
                String compName = (json.has("compName") ? json.getString("compName") : "");
                String groupProduct = (json.has("groupProduct") ? json.getString("groupProduct") : "");
                String ucid = (json.has("ucid") ? json.getString("ucid") : ""); //attr1
                String prodCode = (json.has("prodCode") ? json.getString("prodCode") : "");
                String refNo = (json.has("refNo") ? json.getString("refNo") : "");
                String paymentMethod = (json.has("paymentMethod") ? json.getString("paymentMethod") : "");
                String txnId = (json.has("txnId") ? json.getString("txnId") : "");
//                Date startDate = ValidUtils.str2Date(json.has("startTxnDate") ? json.getString("startTxnDate") : "");   //startTxnTime
//                Date endTxnDate = ValidUtils.str2Date(json.has("endTxnDate") ? json.getString("endTxnDate") : "");      //endTxnTime
//                Date startPayDate = ValidUtils.str2Date(json.has("startPayDate") ? json.getString("startPayDate") : "");
//                Date endPayDate = ValidUtils.str2Date(json.has("endPayDate") ? json.getString("endPayDate") : "");
                Integer status = json.has("status") && !json.getString("status").isEmpty() ? Integer.parseInt(json.getString("status")) : null;
                String state = (json.has("state") ? json.getString("state") : "");
                String txnDateStart = json.has("txnDateStart") ? json.getString("txnDateStart") : "";    //create_at
                String txnDateEnd = json.has("txnDateEnd") ? json.getString("txnDateEnd") : "";
                String txnStartTime = json.has("txnStartTime") ? json.getString("txnStartTime") : "";    //create_at
                String txnEndTime = json.has("txnEndTime") ? json.getString("txnEndTime") : "";
                String paymentDateStart = json.has("paymentDateStart") ? json.getString("paymentDateStart") : "";    //create_at
                String paymentDateEnd = json.has("paymentDateEnd") ? json.getString("paymentDateEnd") : "";
                String refTxnId = (json.has("refTxnId") ? json.getString("refTxnId") : "");
                //                String prodCode = (json.has("prodCode") ? json.getString("prodCode") : "");                 //attr1
                //                Date startDate = ValidUtils.str2Date(json.has("startDate") ? json.getString("startDate") : "");    //create_at
                //                Date endDate = ValidUtils.str2Date(json.has("endDate") ? json.getString("endDate") : "");
                
                returnVal.put("datas", new SysOperLogDao().getInquiryTransaction(Utils.validateSubStateFromHeader(request), compName, groupProduct, ucid, prodCode, refNo, paymentMethod, txnId, txnDateStart, txnStartTime, txnDateEnd, txnEndTime, status, state, paymentDateStart, paymentDateEnd, refTxnId));
            }
        } catch (JSONException e) {
            logger.error("" + e);
            e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @RequestMapping(value = "/transaction/error", method = POST)
    @ResponseBody
    public ResponseEntity<?> getListTransactionError(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String payload) {
        logger.info("POST : /shelf/report/v1/transaction/error");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
//            JSONArray jsonArr = new JSONArray();
            JSONObject datas = new JSONObject(payload);
            if (datas.has("data")) {
                JSONObject data = datas.getJSONObject("data");
//                JSONObject data = new JSONObject();
                returnVal.put("datas", new SysOperLogDao().getErrorLogList(Utils.validateSubStateFromHeader(request), data));
            }
        } catch (JSONException e) {
            logger.error("" + e);
            e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }
}
