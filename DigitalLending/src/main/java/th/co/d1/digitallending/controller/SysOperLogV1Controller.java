/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.controller;

import com.tfglog.LogSingleton;
import com.tfglog.Log_decorator;
import com.tfglog.TfgLogger;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.logging.Logger;
import org.hibernate.HibernateException;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import th.co.d1.digitallending.dao.ShelfLookupDao;
import th.co.d1.digitallending.dao.SysLogDao;
import th.co.d1.digitallending.dao.SysLookupDao;
import th.co.d1.digitallending.dao.SysOperLogDao;
import th.co.d1.digitallending.dao.UtilityDao;
import th.co.d1.digitallending.entity.Memlookup;
import th.co.d1.digitallending.entity.ShelfLookup;
import th.co.d1.digitallending.entity.SysLog;
import th.co.d1.digitallending.entity.SysOperLog;
import static th.co.d1.digitallending.util.ApplicationStartup.headersJSON;
import th.co.d1.digitallending.util.DateUtils;
import th.co.d1.digitallending.util.StatusUtils;
import static th.co.d1.digitallending.util.Utils.getUUID;
import th.co.d1.digitallending.util.ValidUtils;

/**
 *
 * @author Kritsana Sasai
 *
 * DataOne Asia (Thailand) Company Limited 1023 MS Siam Tower, 30th Floor, Rama
 * 3 Rd., Chongnonsi, Yannawa, Bangkok, 10120
 *
 * @create 30-01-2020 11:31:16 AM
 */
@Controller
@RequestMapping("/shelf/syslog/v1")
public class SysOperLogV1Controller {

    Logger logger = Logger.getLogger(SysOperLogV1Controller.class.getName());
    TfgLogger log = LogSingleton.getTfgLogger();

    @Log_decorator
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getSysOperLog(HttpSession session, HttpServletResponse response, HttpServletRequest request) {
        logger.info("GET : /shelf/syslog/v1");
        log.info("GET : /shelf/syslog/v1");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
        try {
            returnVal.put("data", new JSONObject().put("uuid", getUUID()));
        } catch (JSONException | NullPointerException e) {
            logger.info(e.getMessage());
            log.error("" + e);
            //e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);

        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @Log_decorator
    @RequestMapping(value = "list", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getSysOperLogs(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestHeader(value = "sub_state", required = false) String subState) {
        logger.info("GET : /shelf/syslog/v1/list");
        log.info("GET : /shelf/syslog/v1/list");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
        try {
            List<SysOperLog> list = new SysOperLogDao().getListSysOperLog(subState);
            JSONArray jsonArr = new JSONArray();
            for (SysOperLog sysLog : list) {
                if (null != sysLog.getStateCode()) {
                    byte[] decodedBytes = Base64.getDecoder().decode(ValidUtils.null2NoData(sysLog.getStepData()));
                    String stepId = new String(decodedBytes);
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("uuid", sysLog.getUuid())
                            .put("trnId", sysLog.getTrnId())
                            .put("source", sysLog.getSource())
                            .put("prodId", sysLog.getProductId())
                            .put("prodVer", sysLog.getProductVersionId())
                            .put("trnStatus", sysLog.getTrnStatus())
                            .put("failReason", sysLog.getFailureReason())
                            .put("sourceDevice", sysLog.getSourceDevice())
                            //                            .put("trnDate", sysLog.getCreateAt())
                            .put("stateCode", sysLog.getStateCode().getLookupCode())
                            .put("date", DateUtils.getDisplayEnDate(sysLog.getCreateAt(), "dd/MM/yyyy"))
                            .put("time", DateUtils.getDisplayEnDate(sysLog.getCreateAt(), "HH:mm:ssS"))
                            .put("productCode", ValidUtils.null2NoData(sysLog.getProductCode()))
                            .put("productChannel", ValidUtils.null2NoData(sysLog.getProdChannel()))
                            .put("stepId", stepId)
                            .put("caseId", ValidUtils.null2NoData(sysLog.getCaseId()))
                            .put("groupProduct", sysLog.getGroupProduct())
                            .put("refNo", sysLog.getRefNo())
                            .put("txnNo", sysLog.getTxnNo())
                            .put("stateTime", sysLog.getStateTime());

                    jsonArr.put(jsonObj);
                }
            }
            returnVal.put("data", jsonArr);
        } catch (JSONException | NullPointerException | HibernateException e) {
            logger.info(e.getMessage());
            log.error("" + e);
            //e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }

        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @Log_decorator
    @RequestMapping(value = "save", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<?> postSysOperLog(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String reqBody, @RequestHeader(value = "sub_state", required = false) String subState) {
        logger.info("POST : /shelf/syslog/v1/save");
        log.info("POST : /shelf/syslog/v1/save");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
        try {
            JSONObject datas = new JSONObject(reqBody);
            if (datas.has("data")) {
                JSONObject json = datas.getJSONObject("data");
                String trnId = json.has("trnId") && !"".equals(json.getString("trnId")) ? json.getString("trnId") : getUUID();
                SysOperLog sysLog = new SysOperLog();
                sysLog.setUuid(getUUID());
                sysLog.setTrnId(trnId);
                sysLog.setSource(json.getString("source"));
                sysLog.setProductId(json.getString("productId"));               //not null
                sysLog.setProductVersionId(json.getString("productVersionId")); //not null
                sysLog.setProductComponentId(json.has("productComponentId") ? json.getString("productComponentId") : "");
                sysLog.setTaskCategory(json.has("taskCategory") ? json.getString("taskCategory") : "");
                sysLog.setKeywords(json.has("keywords") ? json.getString("keywords") : "");
                Integer trnStatus = 200;
                if (json.has("trnStatus")) {
                    trnStatus = ValidUtils.str2BigInteger(ValidUtils.null2NoData(json.get("trnStatus")));
                }
                sysLog.setTrnStatus(null != trnStatus ? trnStatus : 200);
                String trnSubStatus = (json.has("trnSubStatus") ? ValidUtils.obj2String(json.get("trnSubStatus")) : "");
                Memlookup memLk = new SysLookupDao().getMemLookupByValue(subState, (null != trnSubStatus ? trnSubStatus.toLowerCase() : ""));
                if (null != memLk) {
                    sysLog.setTrnSubStatus(ValidUtils.str2BigInteger(memLk.getLookupcode()));
                }
                sysLog.setFailureReason(json.has("failureReason") ? json.getString("failureReason") : "");
                sysLog.setSourceDevice(json.has("sourceDevice") ? json.getString("sourceDevice") : "");
                sysLog.setSourceDeviceId(json.has("sourceDeviceId") ? json.getString("sourceDeviceId") : "");
                sysLog.setSourceCifId(json.has("sourceCifId") ? json.getString("sourceCifId") : "");
                sysLog.setAccountName(json.has("accountName") ? json.getString("accountName") : "");
                Integer status = StatusUtils.getProspect(subState).getStatusCode();
                if (json.has("status") && !json.getString("status").isEmpty()) {
                    memLk = new SysLookupDao().getMemLookupByCode(subState, json.getString("status"));
                    if (null != memLk) {
                        status = (ValidUtils.str2BigInteger(memLk.getLookupcode()));
                    }
                }
                sysLog.setStatus(status);
                sysLog.setStateTime(ValidUtils.str2BigInt(json.has("stateTime") ? json.getString("stateTime") : "0"));
                ShelfLookup state = new ShelfLookupDao().getShelfLookupByLkCode(subState, (json.has("stateCode") ? json.getString("stateCode") : ""), "PROCESS_STATE");
                sysLog.setStateCode(state);
                sysLog.setProductCode(json.getString("productCode"));
                sysLog.setProdChannel(json.getString("productChannel"));
                String encodedString = Base64.getEncoder().encodeToString(json.has("stepId") ? json.getString("stepId").getBytes() : null);
                sysLog.setStepData(encodedString);
                sysLog.setCaseId(json.has("caseId") ? json.getString("caseId") : "");
                sysLog.setGroupProduct(json.has("groupProduct") ? json.getString("groupProduct") : "");
                sysLog.setRefNo(json.has("refNo") ? json.getString("refNo") : "");
                sysLog.setTxnNo(json.has("txnNo") ? json.getString("txnNo") : "");
                sysLog.setAttr1(json.has("ucifId") ? json.getString("ucifId") : "");
                sysLog.setAttr2(json.has("processErr") ? json.getString("processErr") : "");
                sysLog.setBusinessDate(json.has("businessdate") ? ValidUtils.str2Date(json.getString("businessdate"), "dd-MM-yyyy") : null);
                sysLog.setPaymentMethod(json.has("paymentmethod") ? json.getString("paymentmethod") : "");
                sysLog.setAttr3(json.has("paymentdate") ? json.getString("paymentdate") : "");//paymentdate
                sysLog.setAttr5(new UtilityDao().getCutOffProduct(subState, sysLog.getProductId(), ValidUtils.str2BigInteger(sysLog.getProductVersionId())));
                sysLog.setAttr6(json.has("traceNo") ? json.getString("traceNo") : "");
                sysLog.setAttr7(ValidUtils.str2Dec(json.has("minMax") ? json.getString("minMax") : "00.00").toString());
                sysLog.setAttr9(json.has("atrr9") ? json.getString("atrr9") : "");
                sysLog.setAttr10(json.has("atrr10") ? json.getString("atrr10") : "");
                SysLog sl = null;
                if (null != sysLog.getCaseId() && !"".equals(sysLog.getCaseId()) && null != state && "PRO1013".equalsIgnoreCase(state.getLookupCode())) {
                    sl = new SysLog();
                    sl.setUuid(getUUID());
                    sl.setProdCode(sysLog.getProductCode());
                    sl.setCaseId(sysLog.getCaseId());
                    sl.setGroupProduct(sysLog.getGroupProduct());
                    sl.setState(sysLog.getStateCode().getLookupCode());
                    sl.setStatus(sysLog.getStatus());
                    sl.setAttr1(sysLog.getAttr1()); //ucifId
                    sl.setAttr2(sysLog.getAttr2());     //processErr
                }
                JSONObject resp = new SysOperLogDao().saveSysOperLog(subState, sysLog, sl);
                resp.put("trnId", trnId);
                returnVal.put("data", resp);
            }
        } catch (JSONException | NullPointerException | ParseException | HibernateException | SQLException e) {
            logger.info(e.getMessage());
            log.error("" + e);
//            e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);

        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    /*
    @RequestMapping(value = "info/{sysLogID}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> infoSysOperLog(HttpSession session, HttpServletResponse response, HttpServletRequest request, @PathVariable String sysLogID) {
        logger.info(String.format("GET : /shelf/syslog/v1/info/%s", sysLogID));
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
        try {
            SysOperLog sysLog = new SysOperLogDao().getSysOperLog(subState, sysLogID);
            if (null != sysLog) {
                byte[] decodedBytes = Base64.getDecoder().decode(ValidUtils.null2NoData(sysLog.getStepData()));
                String stepId = new String(decodedBytes);
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("uuid", sysLog.getUuid())
                        .put("trnId", sysLog.getTrnId())
                        .put("source", sysLog.getSource())
                        .put("prodId", sysLog.getProductId())
                        .put("prodVer", sysLog.getProductVersionId())
                        .put("trnStatus", sysLog.getTrnStatus())
                        .put("failReason", sysLog.getFailureReason())
                        .put("sourceDevice", sysLog.getSourceDevice())
                        //                        .put("trnDate", sysLog.getCreateAt())
                        .put("stateCode", sysLog.getStateCode().getLookupCode())
                        .put("date", DateUtils.getDisplayEnDate(sysLog.getCreateAt(), "dd/MM/yyyy"))
                        .put("time", DateUtils.getDisplayEnDate(sysLog.getCreateAt(), "HH:mm:ssS"))
                        .put("productCode", ValidUtils.null2NoData(sysLog.getProductCode()))
                        .put("productChannel", ValidUtils.null2NoData(sysLog.getProdChannel()))
                        .put("stepId", stepId)
                        .put("caseId", ValidUtils.null2NoData(sysLog.getCaseId()))
                        .put("groupProduct", sysLog.getGroupProduct())
                        .put("refNo", sysLog.getRefNo())
                        .put("txnNo", sysLog.getTxnNo())
                        .put("stateTime", sysLog.getStateTime());
                returnVal.put("data", jsonObj);
            } else {
                returnVal.put("data", new JSONObject());
            }
        } catch (JSONException | NullPointerException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);

        }

        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }*/
 /*
    @RequestMapping(value = "list/prods/{prodUuid}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getListSysLogByProd(HttpSession session, HttpServletResponse response, HttpServletRequest request, @PathVariable String prodUuid) {
        logger.info(String.format("GET : /shelf/syslog/v1/list/prods/%s", prodUuid));
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
        try {
            List<SysOperLog> list = new SysOperLogDao().getListSysOperLogByProd(subState, prodUuid);
            JSONArray jsonArr = new JSONArray();
            for (SysOperLog sysLog : list) {
                if (null != sysLog.getStateCode()) {
                    byte[] decodedBytes = Base64.getDecoder().decode(ValidUtils.null2NoData(sysLog.getStepData()));
                    String stepId = new String(decodedBytes);
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("uuid", sysLog.getUuid())
                            .put("trnId", sysLog.getTrnId())
                            .put("source", sysLog.getSource())
                            .put("prodId", sysLog.getProductId())
                            .put("prodVer", sysLog.getProductVersionId())
                            .put("trnStatus", sysLog.getTrnStatus())
                            .put("failReason", sysLog.getFailureReason())
                            .put("sourceDevice", sysLog.getSourceDevice())
                            //                            .put("trnDate", sysLog.getCreateAt())
                            .put("stateCode", sysLog.getStateCode().getLookupCode())
                            .put("date", DateUtils.getDisplayEnDate(sysLog.getCreateAt(), "dd/MM/yyyy"))
                            .put("time", DateUtils.getDisplayEnDate(sysLog.getCreateAt(), "HH:mm:ssS"))
                            .put("productCode", ValidUtils.null2NoData(sysLog.getProductCode()))
                            .put("productChannel", ValidUtils.null2NoData(sysLog.getProdChannel()))
                            .put("stepId", stepId)
                            .put("caseId", ValidUtils.null2NoData(sysLog.getCaseId()))
                            .put("groupProduct", sysLog.getGroupProduct())
                            .put("refNo", sysLog.getRefNo())
                            .put("stateTime", sysLog.getStateTime());

                    jsonArr.put(jsonObj);
                }
            }
            returnVal.put("data", jsonArr);
        } catch (JSONException | NullPointerException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);

        }

        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }*/
 /*
    @RequestMapping(value = "list/trans/{trnUuid}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getListSysLogByTrans(HttpSession session, HttpServletResponse response, HttpServletRequest request, @PathVariable String trnUuid) {
        logger.info(String.format("GET : /shelf/syslog/v1/list/trans/%s", trnUuid));
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
        try {
            List<SysOperLog> list = new SysOperLogDao().getListSysOperLogByTrans(subState, trnUuid);
            JSONArray jsonArr = new JSONArray();
            for (SysOperLog sysLog : list) {
                if (null != sysLog.getStateCode()) {
                    byte[] decodedBytes = Base64.getDecoder().decode(ValidUtils.null2NoData(sysLog.getStepData()));
                    String stepId = new String(decodedBytes);
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("uuid", sysLog.getUuid())
                            .put("trnId", sysLog.getTrnId())
                            .put("source", sysLog.getSource())
                            .put("prodId", sysLog.getProductId())
                            .put("prodVer", sysLog.getProductVersionId())
                            .put("trnStatus", sysLog.getTrnStatus())
                            .put("failReason", sysLog.getFailureReason())
                            .put("sourceDevice", sysLog.getSourceDevice())
                            //                            .put("trnDate", sysLog.getCreateAt())
                            .put("stateCode", sysLog.getStateCode().getLookupCode())
                            .put("date", DateUtils.getDisplayEnDate(sysLog.getCreateAt(), "dd/MM/yyyy"))
                            .put("time", DateUtils.getDisplayEnDate(sysLog.getCreateAt(), "HH:mm:ssS"))
                            .put("productCode", ValidUtils.null2NoData(sysLog.getProductCode()))
                            .put("productChannel", ValidUtils.null2NoData(sysLog.getProdChannel()))
                            .put("stepId", stepId)
                            .put("caseId", ValidUtils.null2NoData(sysLog.getCaseId()))
                            .put("groupProduct", sysLog.getGroupProduct())
                            .put("refNo", sysLog.getRefNo())
                            .put("txnNo", sysLog.getTxnNo())
                            .put("stateTime", sysLog.getStateTime());

                    jsonArr.put(jsonObj);
                }
            }
            returnVal.put("data", jsonArr);
        } catch (JSONException | NullPointerException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }

        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }*/
    @Log_decorator
    @RequestMapping(value = "search", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<?> searchSysOperLog(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String reqBody, @RequestHeader(value = "sub_state", required = false) String subState) {
        logger.info("POST : /shelf/syslog/v1/search");
        log.info("POST : /shelf/syslog/v1/search");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
        try {
            JSONArray jsonArr = new JSONArray();
            JSONObject datas = new JSONObject(reqBody);
            if (datas.has("data")) {
                JSONObject json = datas.getJSONObject("data");
//                String compName = (json.has("compName") ? json.getString("compName") : "");
                String groupProduct = (json.has("groupProduct") ? json.getString("groupProduct") : "");
//                String customerId = (json.has("customerId") ? json.getString("customerId") : "");
//                String customerName = (json.has("customerName") ? json.getString("customerName") : "");
                String prodCode = (json.has("prodCode") ? json.getString("prodCode") : "");
//                String prodName = (json.has("prodName") ? json.getString("prodName") : "");
                String refNo = (json.has("refNo") ? json.getString("refNo") : "");
                Date startDate = ValidUtils.str2Date(json.has("startDate") ? json.getString("startDate") : "");    //create_at
                Date endDate = ValidUtils.str2Date(json.has("endDate") ? json.getString("endDate") : "");
                Date payStartDate = ValidUtils.str2Date(json.has("payStartDate") ? json.getString("payStartDate") : "");
                Date payEndDate = ValidUtils.str2Date(json.has("payEndDate") ? json.getString("payEndDate") : "");
                Integer trnStatus = ValidUtils.obj2Integer((json.has("trnStatus") ? json.get("trnStatus") : null)); //trnStatus
                String stateCode = (json.has("stateCode") ? ValidUtils.obj2String(json.get("stateCode")) : ""); //stateCode
                ShelfLookup state = new ShelfLookupDao().getShelfLookupByLkCode(subState, stateCode, "PROCESS_STATE");
                SysOperLog sysOperLog = new SysOperLog();
                sysOperLog.setGroupProduct(groupProduct);
                sysOperLog.setProductCode(prodCode);
                sysOperLog.setRefNo(refNo);
                sysOperLog.setTrnStatus(trnStatus);
                sysOperLog.setStateCode(state);
                SysOperLogDao dao = new SysOperLogDao();
                List<SysOperLog> list = dao.searchSysOperLog(subState, sysOperLog, startDate, endDate, payStartDate, payEndDate);
                for (SysOperLog sysLog : list) {
                    byte[] decodedBytes = Base64.getDecoder().decode(ValidUtils.null2NoData(sysLog.getStepData()));
                    String stepId = new String(decodedBytes);
                    boolean result = false;
                    if (null != payStartDate || null != payEndDate) {
                        if (null != stepId && !"".equals(stepId)) {
                            JSONArray obj = new JSONArray(stepId);
                            JSONArray d2 = obj.getJSONObject(0).getJSONArray("data");
                            for (int k = 0; k < d2.length(); k++) {
                                JSONObject d3 = d2.getJSONObject(k);
                                Date payDate = ValidUtils.str2Date(d3.getString("payDate")); //dd/MM/yyyy
                                if (null != payStartDate && null != payEndDate) {
                                    if (payDate.compareTo(payStartDate) >= 0 && payDate.compareTo(payEndDate) <= 0) {
                                        result = true;
                                    }
                                } else if (null == payStartDate && null != payEndDate) {
                                    if (payDate.compareTo(payEndDate) <= 0) {
                                        result = true;
                                    }
                                } else if (null != payStartDate && null == payEndDate) {
                                    if (payDate.compareTo(payStartDate) > 0) {
                                        result = true;
                                    }
                                }
                            }
                        }
                    } else {
                        result = true;
                    }
                    if (result) {
                        JSONObject jsonObj = new JSONObject();
                        jsonObj.put("uuid", sysLog.getUuid())
                                .put("trnId", sysLog.getTrnId())
                                .put("source", sysLog.getSource())
                                .put("prodId", sysLog.getProductId())
                                .put("prodVer", sysLog.getProductVersionId())
                                .put("trnStatus", sysLog.getTrnStatus())
                                .put("failReason", sysLog.getFailureReason())
                                .put("sourceDevice", sysLog.getSourceDevice())
                                .put("stateCode", sysLog.getStateCode().getLookupCode())
                                .put("date", DateUtils.getDisplayEnDate(sysLog.getCreateAt(), "dd/MM/yyyy"))
                                .put("time", DateUtils.getDisplayEnDate(sysLog.getCreateAt(), "HH:mm:ssS"))
                                .put("productCode", ValidUtils.null2NoData(sysLog.getProductCode()))
                                .put("productChannel", ValidUtils.null2NoData(sysLog.getProdChannel()))
                                .put("stepId", stepId)
                                .put("caseId", ValidUtils.null2NoData(sysLog.getCaseId()))
                                .put("groupProduct", ValidUtils.null2NoData(sysLog.getGroupProduct()))
                                .put("refNo", ValidUtils.null2NoData(sysLog.getRefNo()))
                                .put("stateTime", sysLog.getStateTime());
                        jsonArr.put(jsonObj);

                    }
                }
                returnVal.put("data", jsonArr);
            }
        } catch (JSONException | NullPointerException | HibernateException e) {
            logger.info(e.getMessage());
            log.error("" + e);
            //e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    /*
    @RequestMapping(value = "status/search", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<?> searchSysLog(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String reqBody) {
        logger.info("POST : /shelf/syslog/v1/status/search");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
        try {
            JSONArray jsonArr = new JSONArray();
            JSONObject datas = new JSONObject(reqBody);
            if (datas.has("data")) {
                JSONObject json = datas.getJSONObject("data");
                String prodCode = (json.has("prodCode") ? json.getString("prodCode") : "");
                String caseId = (json.has("caseId") ? json.getString("caseId") : "");
                String groupProduct = (json.has("groupProduct") ? json.getString("groupProduct") : "");
                String state = (json.has("state") ? json.getString("state") : "");
                Date startDate = ValidUtils.str2Date(json.has("startDate") ? json.getString("startDate") : "");
                Date endDate = ValidUtils.str2Date(json.has("endDate") ? json.getString("endDate") : "");
                Memlookup memLk = new SysLookupDao().getMemLookupByValue(subState, ValidUtils.obj2String((json.has("status") ? json.get("status") : "")));
                SysLog sl = new SysLog();
                sl.setProdCode(prodCode);
                sl.setCaseId(caseId);
                sl.setGroupProduct(groupProduct);
                sl.setState(state);
                if (null != memLk) {
                    sl.setStatus(ValidUtils.str2BigInteger(memLk.getLookupcode()));
                }
                SysOperLogDao dao = new SysOperLogDao();
                List<SysLog> list = dao.searchSysLog(subState, sl, startDate, endDate);
                for (SysLog sysLog : list) {
                    Memlookup memLookup = new SysLookupDao().getMemLookupByValue(subState, ValidUtils.obj2String(sysLog.getStatus()));
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("uuid", sysLog.getUuid())
                            .put("prodCode", sysLog.getProdCode())
                            .put("caseId", sysLog.getCaseId())
                            .put("groupProduct", sysLog.getGroupProduct())
                            .put("state", sysLog.getState())
                            .put("status", null != memLookup ? memLookup.getLookupnameth() : "");

                    jsonArr.put(jsonObj);
                }
                returnVal.put("data", jsonArr);
            }
        } catch (JSONException | NullPointerException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);

        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }*/
    @Log_decorator
    @RequestMapping(value = "api/save", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<?> saveSysLog(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String reqBody, @RequestHeader(value = "sub_state", required = false) String subState) {
        logger.info("POST : /shelf/syslog/v1/api/save");
        log.info("POST : /shelf/syslog/v1/api/save");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
        try {
            JSONObject datas = new JSONObject(reqBody);
            if (datas.has("data")) {
                String dbEnv = subState;
                JSONObject json = datas.getJSONObject("data");
                String uuid = json.has("id") ? json.getString("id") : "";
                String status = json.has("status") ? json.getString("status") : "";     //active, inactive
                String state = json.has("state") ? json.getString("state") : "";        //PRO1014 - PRO10XX
                String trnStatus = json.has("trnStatus") ? json.getString("trnStatus") : "";    // waittotransfer, waitfor
                SysLogDao dao = new SysLogDao();
                SysLog sysLog = dao.getSysLogByUUID(dbEnv, uuid);
                Memlookup memTrnStatus = new SysLookupDao().getMemLookupByValue(dbEnv, trnStatus);
                ShelfLookup lkState = new ShelfLookupDao().getShelfLookupByLkCode(dbEnv, state, "PROCESS_STATE");
                Memlookup memLookup = new SysLookupDao().getMemLookupByValue(dbEnv, status);
                if (null != sysLog && null != memTrnStatus && null != lkState && null != memLookup) {
                    SysOperLogDao dao2 = new SysOperLogDao();
                    List<SysOperLog> list = dao2.getListSysOperLogByCaseId(dbEnv, sysLog.getCaseId());
                    SysOperLog sol = new SysOperLog();
                    if (list.size() > 0) {
                        sol = list.get(0);
                    }
                    String trnId = getUUID();
                    SysOperLog sysOperLog = new SysOperLog();
                    sysOperLog.setUuid(getUUID());
                    sysOperLog.setTrnId(trnId); //sol.getTrnId()
                    sysOperLog.setStatus(ValidUtils.str2BigInteger(memTrnStatus.getLookupcode())); // waittotransfer, waitfor
                    if (null != memTrnStatus && null != memTrnStatus.getLookupcode()) {
                        sysOperLog.setTrnSubStatus(ValidUtils.str2BigInteger(memTrnStatus.getLookupcode()));
                    }
                    sysOperLog.setTrnStatus(200);   //pass,fail
                    sysOperLog.setStateCode(lkState);
                    sysOperLog.setProductId(sol.getProductId());               //not null
                    sysOperLog.setProductVersionId(sol.getProductVersionId()); //not null
                    sysOperLog.setProductCode(sysLog.getProdCode());
                    sysOperLog.setCaseId(sysLog.getCaseId());
                    sysOperLog.setGroupProduct(sysLog.getGroupProduct());
                    sysOperLog.setRefNo(sol.getRefNo());
                    sysOperLog.setPaymentMethod(ValidUtils.null2Separator(sol.getPaymentMethod(), ""));
                    sysOperLog.setTxnNo(sol.getTxnNo());
                    sysOperLog.setBusinessDate(sol.getBusinessDate());
                    sysOperLog.setSource(sol.getSource());
                    sysOperLog.setKeywords("api");
                    sysOperLog.setSourceDevice(sol.getSourceDevice());
                    sysOperLog.setSourceDeviceId(sol.getSourceDeviceId());
                    sysOperLog.setSourceCifId(sol.getSourceCifId());
                    sysOperLog.setAccountName(sol.getAccountName());
                    sysOperLog.setProdChannel(sol.getProdChannel());
                    sysOperLog.setGroupProduct(sol.getGroupProduct());
                    sysOperLog.setAttr3(sol.getAttr3());
                    sysOperLog.setAttr1(sol.getAttr1());
                    sysOperLog.setCreateAt(sol.getCreateAt());
                    sysOperLog.setFailureReason("Success");
                    Date sysDate = new Date();
                    sysOperLog.setAttr4(DateUtils.getDisplayEnDate(sysDate, "yyyy-MM-dd HH:mm:ss"));
                    sysOperLog.setAttr5(sol.getAttr5());
                    sysOperLog.setAttr6(sol.getAttr6());
                    sysOperLog.setAttr7(sol.getAttr7());
                    if (null != lkState && "PRO1017".equalsIgnoreCase(lkState.getLookupCode())) {
                        sysOperLog.setPaymentDate(sysDate);
                    }
                    sysLog.setStatus(ValidUtils.str2BigInteger(memLookup.getLookupcode()));      //active, inactive
                    sysLog.setState(state);
                    if (null != sol.getStateCode() && state.equalsIgnoreCase(sol.getStateCode().getLookupCode())) {
                    } else {
                        JSONObject resp = new SysOperLogDao().saveSysOperLog(dbEnv, sysOperLog, sysLog);
                        returnVal.put("status", resp.getBoolean("status") ? 200 : 500)
                                .put("description", resp.getBoolean("status") ? "" : resp.getString("description"));
                    }
                } else {
                    returnVal.put("status", 400)
                            .put("description", StatusUtils.getErrorMessageByCode(subState, "SHELF0041"));
                }
            }
        } catch (JSONException | NullPointerException | HibernateException e) {
            logger.info(e.getMessage());
            log.error("" + e);
            //e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);

        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @Log_decorator
    @RequestMapping(value = "api/search", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> apiListLog(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestHeader(value = "sub_state", required = false) String subState) {
        logger.info("GET : /shelf/syslog/v1/api/search");
        log.info("GET : /shelf/syslog/v1/api/search");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            /*SysLogDao dao = new SysLogDao();
            List<SysLog> list = dao.getSysLogByNotStatus(subState, StatusUtils.getInActive(subState).getStatusCode());
            for (SysLog sl : list) {
                JSONObject json = new JSONObject();
                json.put("id", sl.getUuid())
                        .put("group", sl.getGroupProduct())
                        .put("code", sl.getProdCode())
                        .put("caseid", sl.getCaseId())
                        .put("ucid", sl.getAttr1());
                jsonArr.put(json);
            }*/
            SysOperLogDao dao = new SysOperLogDao();
            JSONArray jsonArr = dao.getListComponentByCaseId(subState);
            returnVal.put("datas", jsonArr);
            if (jsonArr.length() == 0) {
                returnVal.put("status", 400);
            }

        } catch (JSONException | NullPointerException | HibernateException | SQLException e) {
            logger.info(e.getMessage());
            log.error("" + e);
            //e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);

        }

        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }
}
