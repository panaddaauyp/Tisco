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
import java.util.Date;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
import th.co.d1.digitallending.dao.ShelfRoleDao;
import th.co.d1.digitallending.dao.ShelfRoleFuncDao;
import th.co.d1.digitallending.dao.ShelfRoleMenuDao;
import th.co.d1.digitallending.dao.SysErrorHandlingDao;
import th.co.d1.digitallending.dao.SysOperLogDao;
import th.co.d1.digitallending.entity.ShelfMenu;
import th.co.d1.digitallending.entity.ShelfRole;
import th.co.d1.digitallending.entity.ShelfRoleFunc;
import th.co.d1.digitallending.entity.ShelfRoleMenu;
import static th.co.d1.digitallending.util.ApplicationStartup.headersJSON;
import th.co.d1.digitallending.util.StatusUtils;
import th.co.d1.digitallending.entity.SysErrorHandling;
import static th.co.d1.digitallending.util.Utils.getUUID;
import th.co.d1.digitallending.util.ValidUtils;

/**
 *
 * @author Ritthikriat
 */
@Controller
@RequestMapping("/sys/errorhandling/")
public class SysErrorHandlingController {

    final static Logger logger = Logger.getLogger(ShelfRoleMenuFunctionV1Controller.class.getName());
    final static TfgLogger log = LogSingleton.getTfgLogger();
    Date sysdate = new Date();

    //    @Log_decorator
    @RequestMapping(value = "/search", method = POST)
    @ResponseBody
    public ResponseEntity<?> getListErrorHandling(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String reqBody, @RequestHeader(value = "sub_state", required = false) String subState) throws SQLException, UnsupportedEncodingException, ParseException {
        logger.info("POST : /sys/errorhandling/search");
        log.info("POST : /sys/errorhandling/search");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "Det Data Complete").put("datas", new JSONArray());
        try {
            JSONObject datas = new JSONObject(reqBody);
            if (datas.has("data")) {
                JSONObject json = datas.getJSONObject("data");
                String txnNo = json.getString("txnId");
                String txnDateEnd = json.getString("txnDateEnd");
                String txnDateStart = json.getString("txnDateStart");
                String txnEndTime = json.getString("txnEndTime");
                String txnStartTime = json.getString("txnStartTime");
                
                if(txnStartTime != null && txnStartTime != ""){
                    txnDateStart = txnDateStart + " " + txnStartTime;
                }
                if(txnEndTime != null && txnEndTime != ""){
                    txnDateEnd = txnDateEnd + " " + txnStartTime;
                }
    
                if (txnNo != null && txnNo != "") {
                    returnVal.put("datas", new SysErrorHandlingDao().getListErrorhandling(subState, txnNo, txnDateStart,txnDateEnd));
                } else {
                    returnVal.put("status", 500).put("description", "require : txn_no !");
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

//    @Log_decorator
//    @RequestMapping(value = "/search", method = POST)
//    @ResponseBody
//    public ResponseEntity<?> getListErrorHandling(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String reqBody, @RequestHeader(value = "sub_state", required = false) String subState) throws SQLException, UnsupportedEncodingException, ParseException {
//        logger.info("POST : /sys/errorhandling/search");
//        log.info("POST : /sys/errorhandling/search");
//        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
//        try {
//            JSONObject datas = new JSONObject(reqBody);
//            if (datas.has("data")) {
//                JSONObject json = datas.getJSONObject("data");
//                String compName = (json.has("compName") ? json.getString("compName") : "");
//                String groupProduct = (json.has("groupProduct") ? json.getString("groupProduct") : "");
//                String ucid = (json.has("ucid") ? json.getString("ucid") : "");
//                String prodCode = (json.has("prodCode") ? json.getString("prodCode") : "");
//                String refNo = (json.has("refNo") ? json.getString("refNo") : "");
//                String paymentMethod = (json.has("paymentMethod") ? json.getString("paymentMethod") : "");
//                String txnId = (json.has("txnId") ? json.getString("txnId") : "");
//                Integer status = json.has("status") && !json.getString("status").isEmpty() ? Integer.parseInt(json.getString("status")) : null;
//                String state = (json.has("state") ? json.getString("state") : "");
//                String txnDateStart = json.has("txnDateStart") ? json.getString("txnDateStart") : "";
//                String txnDateEnd = json.has("txnDateEnd") ? json.getString("txnDateEnd") : "";
//                String txnStartTime = json.has("txnStartTime") ? json.getString("txnStartTime") : "";
//                String txnEndTime = json.has("txnEndTime") ? json.getString("txnEndTime") : "";
//                String paymentDateStart = json.has("paymentDateStart") ? json.getString("paymentDateStart") : "";
//                String paymentDateEnd = json.has("paymentDateEnd") ? json.getString("paymentDateEnd") : "";
//                String refTxnId = (json.has("refTxnId") ? json.getString("refTxnId") : "");
//               
//                returnVal.put("datas", new SysErrorHandlingDao().getListErrorhandling(subState, compName, groupProduct, ucid, prodCode, refNo, paymentMethod, txnId, txnDateStart, txnStartTime, txnDateEnd, txnEndTime, status, state, paymentDateStart, paymentDateEnd, refTxnId));
//            }
//        } catch (JSONException | NullPointerException | HibernateException e) {
//            logger.info(e.getMessage());
//            log.error("" + e);
//            //e.printStackTrace();
//            returnVal.put("status", 500)
//                      .put("description", "" + e);
//        }
//        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
//    }
    @Log_decorator
    @RequestMapping(value = "save", method = POST, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<?> saveErrorHandling(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String reqBody, @RequestHeader(value = "sub_state", required = false) String subState) {
        logger.info("POST : /sys/errorhandling/save");
        log.info("POST : /sys/errorhandling/save");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
        try {
            JSONObject datas = new JSONObject(reqBody);
            if (datas.has("userInfo") && datas.has("data")) {
                JSONObject objUser = datas.getJSONObject("userInfo");
                String username = objUser.has("username") ? objUser.getString("username") : "";
                if (!"".equals(username)) {
                    JSONObject objData = datas.getJSONObject("data");
                    SysErrorHandlingDao daoEh = new SysErrorHandlingDao();
                    String ehUuid = objData.has("uuid") ? objData.getString("uuid") : "";
                    String prodCode = objData.has("prodCode") ? objData.getString("prodCode") : "";
                    String txnNo = objData.has("txnNo") ? objData.getString("txnNo") : "";
                    String statusUuid = objData.has("statusUuid") ? objData.getString("statusUuid") : "";
                    String stateUuid = objData.has("stateUuid") ? objData.getString("stateUuid") : "";
                    String compUuid = objData.has("compUuid") ? objData.getString("compUuid") : "";
                    String remark = objData.has("remark") ? objData.getString("remark") : "";
                    String method = objData.has("method") ? objData.getString("method") : "";
                    String department = objData.has("department") ? objData.getString("department") : "";
                    Integer status = objData.has("status") ? objData.getInt("status") : 0;
                    String roleCode = objData.has("roleCode") ? objData.getString("roleCode") : "";
                    SysErrorHandling erh = new SysErrorHandling();
                    if (ehUuid.equals("")) {
                        erh = new SysErrorHandling();
                        erh.setUuid(getUUID());
                    } else {
                        sysdate = new Date();
                        erh = daoEh.getSysErrorHandlingByUuid(subState, ehUuid);
                        if (erh.getUuid() != null) {
                            erh.setUuid(erh.getUuid());

                            if (erh.getAttr9() != null || erh.getAttr9() != "") {
                                String concat = erh.getAttr9() + "/" + status.toString();
                                erh.setAttr9(concat);
                            }
                            erh.setAttr10(status.toString());
                            if (status == 213 || status == 214 || status == 400) {
                                erh.setApproveAt(sysdate);
                                erh.setApproveBy(username);
                            } else {
                                erh.setUpdateAt(sysdate);
                                erh.setUpdateBy(username);
                            }
                        }
                    }
                    sysdate = new Date();
                    if (erh.getCreateAt() == null || erh.getCreateAt().equals("")) {
                        erh.setCreateAt(sysdate);
                        erh.setCreateBy(username);
                    }
                    if (erh.getAttr9() == null || erh.getAttr9() == "") {
                        erh.setAttr9(status.toString());
                    }
                    if (erh.getAttr10() == null || erh.getAttr10() == "") {
                        erh.setAttr10(status.toString());
                    }
                    erh.setProdCode(prodCode);
                    erh.setTxnNo(txnNo);
                    erh.setStatusUuid(statusUuid);
                    erh.setStateUuid(stateUuid);
                    erh.setRemark(remark);
                    erh.setAttr1(method);
                    erh.setAttr2(department);
                    erh.setCompUuid(compUuid);
                    erh.setStatus(status);

                    erh = daoEh.saveSysErrorHandling(subState, erh);
                } else {
                    returnVal.put("status", 500)
                            .put("description", StatusUtils.getErrorMessageByCode(subState, "SHELF0029"));
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

    // @Log_decorator
    // @RequestMapping(value = "/updatestatus", method = POST)
    // @ResponseBody
    // public ResponseEntity<?> setUpdateStatus(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String reqBody, @RequestHeader(value = "sub_state", required = false) String subState) throws SQLException {
    //     logger.info("POST : /sys/errorhandling/updatestatus");
    //     log.info("POST : /sys/errorhandling/updatestatus");
    //     JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
    //     try {
    //         JSONObject datas = new JSONObject(reqBody);
    //         JSONObject objData = datas.getJSONObject("data");
    //         if (datas.has("data")) {
    //             String username = objData.has("username") ? objData.getString("username") : "";
    //             String uuid = objData.has("uuid") ? objData.getString("uuid") : "";
    //             String department = objData.has("department") ? objData.getString("department") : "";
    //             String roleCode = objData.has("roleCode") ? objData.getString("roleCode") : "";
    //             String txnNo = objData.has("txnNo") ? objData.getString("txnNo") : "";
    //             Integer status = objData.has("status") ? objData.getInt("status") : 0;
    //             if (username != "" && uuid != "") {
    //                 SysErrorHandlingDao dao = new SysErrorHandlingDao();
    //                 JSONObject obj = new JSONObject();
    //                 obj = dao.updateByUuid(subState, uuid, username, status, department);
    //             } else {
    //                 returnVal.put("status", 500)
    //                           .put("description", StatusUtils.getErrorMessageByCode(subState, "SHELF0029"));
    //             }
    //         }
    //     } catch (JSONException | NullPointerException | HibernateException e) {
    //         logger.info(e.getMessage());
    //         log.error("" + e);
    //         //e.printStackTrace();
    //         returnVal.put("status", 500)
    //                   .put("description", "" + e);
    //     }
    //     return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    // }
}
