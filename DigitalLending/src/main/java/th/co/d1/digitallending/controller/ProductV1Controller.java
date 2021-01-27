/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.controller;

import com.google.gson.Gson;
import com.tfglog.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import th.co.d1.digitallending.dao.FunctionDao;
import th.co.d1.digitallending.dao.ShelfCompDao;
import th.co.d1.digitallending.dao.ShelfProductDao;
import th.co.d1.digitallending.dao.ShelfProductDtlDao;
import th.co.d1.digitallending.dao.ShelfProductVcsDao;
import th.co.d1.digitallending.dao.ShelfTmpDao;
import th.co.d1.digitallending.entity.ShelfComp;
import th.co.d1.digitallending.entity.ShelfCompDtl;
import th.co.d1.digitallending.entity.ShelfProduct;
import th.co.d1.digitallending.entity.ShelfProductDtl;
import th.co.d1.digitallending.entity.ShelfProductVcs;
import th.co.d1.digitallending.entity.ShelfTmp;
import th.co.d1.digitallending.entity.ShelfTmpVcs;
import static th.co.d1.digitallending.util.ApplicationStartup.headersJSON;
import th.co.d1.digitallending.util.DateUtils;
import th.co.d1.digitallending.util.ProductUtils;
import th.co.d1.digitallending.util.StatusUtils;
import th.co.d1.digitallending.util.TemplateUtils;
import th.co.d1.digitallending.util.Utils;
import static th.co.d1.digitallending.util.Utils.getUUID;
import th.co.d1.digitallending.util.ValidUtils;

/**
 *
 * @author Chalermpol Yaowachai
 *
 * DataOne Asia (Thailand) Company Limited 1023 MS Siam Tower, 30th Floor, Rama
 * 3 Rd., Chongnonsi, Yannawa, Bangkok, 10120
 *
 * @create 10-01-2020 11:06:29 AM
 */
@Controller
@RequestMapping("/shelf/product/v1")
public class ProductV1Controller {

    final static Logger logger = Logger.getLogger(ProductV1Controller.class.getName());
    final static TfgLogger log = LogSingleton.getTfgLogger();

    @Log_decorator
    @RequestMapping(value = "component", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> getComponentByCode(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String compUuidList, @RequestHeader(value = "sub_state", required = false) String subState) {
        log.info("GET : /shelf/product/v1/component");
        logger.info("GET : /shelf/product/v1/component");
        JSONObject retData = new JSONObject();
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", retData);
        try {
            JSONArray compUuid = new JSONArray(compUuidList);
            JSONArray retComp = new JSONArray();
            ShelfCompDao shelfCompDao = new ShelfCompDao();
            for (int i = 0; i < compUuid.length(); i++) {
                JSONObject tmp = compUuid.getJSONObject(i);
                ShelfComp shelfComp = shelfCompDao.getShelfCompByUUID(subState, tmp.getString("compUuid"));
                shelfComp.setSeqNo(tmp.getLong("seqNo"));
                List<ShelfCompDtl> shelfCompDtlList = shelfComp.getShelfCompDtlList();
                shelfCompDtlList.sort(Comparator.comparing(ShelfCompDtl::getSeq));
                JSONObject ret = ProductUtils.getInitialProductComponentByUUID(subState, shelfComp, shelfCompDtlList, true, null);
                retComp.put(ret);
            }
            JSONObject prod = ProductUtils.getInitialProduct();
            retData.put("product", prod);
            retData.put("component", retComp);
            returnVal.put("data", retData);
        } catch (JSONException | NullPointerException | HibernateException | ParseException e) {
            log.info("" + e);
            logger.info(e.getMessage());
            //e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @Log_decorator
    @RequestMapping(value = "save", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<?> postProduct(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String reqBody, @RequestHeader(value = "sub_state", required = false) String subState) {
        log.info("POST : /shelf/product/v1/save");
        logger.info("POST : /shelf/product/v1/save");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("confirmmsg", "").put("data", new JSONArray());
        try {
            Date sysdate = new Date();
            JSONObject datas = new JSONObject(reqBody);
            String username = "", businessDept = "", businessLine = "";
            if (datas.has("userInfo")) {
                JSONObject objUser = datas.getJSONObject("userInfo");
                username = (objUser.has("username") ? objUser.getString("username") : "");
                businessDept = (objUser.has("businessDept") ? objUser.getString("businessDept") : "");
                businessLine = (objUser.has("businessLine") ? objUser.getString("businessLine") : "");
            }
            if (datas.has("data")) {
                String dbEnv = subState;
                JSONObject validField = ProductUtils.validProduct(datas, dbEnv);
                JSONObject confirmField = ProductUtils.confirmProduct(datas, dbEnv);
                JSONObject objData = datas.getJSONObject("data");
                JSONObject jsonProd = objData.getJSONObject("product");
                boolean confirm = jsonProd.has("confirm") ? jsonProd.getBoolean("confirm") : false;
                if (validField.getBoolean("status") && (confirmField.getBoolean("status") || confirm)) {
                    ShelfProduct prod = ProductUtils.getProduct(subState, datas, sysdate, username);
                    if (null != prod) {
                        prod.setBusinessDept(ValidUtils.null2Separator(prod.getBusinessDept(), businessDept));
                        prod.setBusinessLine(ValidUtils.null2Separator(prod.getBusinessLine(), businessLine));
                        boolean save = true;
                        if (save) {
                            JSONArray retProd = new ShelfProductDao().updateShelfProduct(subState, prod, username);
                            returnVal.put("data", retProd);
                        }
                    } else {
                        returnVal.put("status", 500)
                                .put("description", StatusUtils.getErrorMessageByCode(subState, "SHELF0001"));
                    }
                } else {
                    returnVal.put("status", 500)
                            .put("description", validField.getString("description"));
                    if (!confirmField.getBoolean("status")) {
                        returnVal.put("confirmmsg", confirmField.getString("description"));
                    }
                }
            } else {
                returnVal.put("status", 500)
                        .put("description", StatusUtils.getErrorMessageByCode(subState, "SHELF0001"));

            }
        } catch (JSONException | NullPointerException | HibernateException e) {
            log.info("" + e);
            logger.info(e.getMessage());
            //e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @Log_decorator
    @RequestMapping(value = "save", method = RequestMethod.PUT, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<?> putProduct(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String reqBody, @RequestHeader(value = "sub_state", required = false) String subState) {
        log.info("PUT : /shelf/product/v1/save");
        logger.info("PUT : /shelf/product/v1/save");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("confirmmsg", "").put("data", new JSONArray());
        try {
            Date sysdate = new Date();
            JSONObject datas = new JSONObject(reqBody);
            String username = "", businessDept = "", businessLine = "";
            if (datas.has("userInfo")) {
                JSONObject objUser = datas.getJSONObject("userInfo");
                username = (objUser.has("username") ? objUser.getString("username") : "");
                businessDept = (objUser.has("businessDept") ? objUser.getString("businessDept") : "");
                businessLine = (objUser.has("businessLine") ? objUser.getString("businessLine") : "");
            }
            if (datas.has("data")) {
                String dbEnv = subState;
                JSONObject objData = datas.getJSONObject("data");
                JSONObject jsonProd = objData.getJSONObject("product");
                boolean confirm = jsonProd.has("confirm") ? jsonProd.getBoolean("confirm") : false;
                if (jsonProd.has("action") && !"save".equalsIgnoreCase(jsonProd.getString("action"))) {
                    String uuid = jsonProd.getString("uuid");
                    String remark = jsonProd.has("remark") ? jsonProd.getString("remark") : "";
                    String respCode = jsonProd.has("respCode") ? jsonProd.getString("respCode") : "";       //เก็บเหมือน state
                    Integer verProd = ValidUtils.obj2Int(jsonProd.get("verProd"));
                    ShelfProductVcsDao vcsDao = new ShelfProductVcsDao();
                    if ("sendapprove".equalsIgnoreCase(jsonProd.getString("action"))) {     //by version
                        Integer statusInp = StatusUtils.getInprogress(dbEnv).getStatusCode();
                        Integer[] inprogress = {statusInp};
                        List<ShelfProductVcs> vcsInprogressList = vcsDao.getListShelfProductVcsListByStatus(dbEnv, uuid, inprogress);
                        if (vcsInprogressList.isEmpty()) {
                            returnVal.put("status", 500)
                                    .put("description", StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0013"));
                        } else {
                            ShelfProductDao dao = new ShelfProductDao();
                            JSONObject resp = dao.getProductByStatus(dbEnv, statusInp, uuid);
                            jsonProd.put("activeDate", resp.get("activeDate"));
                            jsonProd.put("endDate", resp.get("endDate"));
                            JSONObject confirmField = ProductUtils.confirmProduct(datas, dbEnv);
                            if (confirmField.getBoolean("status") || confirm) {
                                Integer statusWaitApp = StatusUtils.getWaittoApprove(dbEnv).getStatusCode();
                                JSONObject result = ProductUtils.sendToApproveProduct(dbEnv, uuid, statusWaitApp, sysdate, username, respCode, remark);
                                returnVal.put("status", result.get("status"))
                                        .put("description", result.getString("description"));
                            } else {
                                returnVal.put("status", 500)
                                        .put("confirmmsg", confirmField.getString("description"));
                            }
                        }
                    } else if ("rejectapprove".equalsIgnoreCase(jsonProd.getString("action"))) {    //by version
                        Integer statusWaitApp = StatusUtils.getWaittoApprove(dbEnv).getStatusCode();
                        Integer statusWaitApp2 = StatusUtils.getWaittoApprove2(dbEnv).getStatusCode();
                        Integer[] waStatus = {statusWaitApp, statusWaitApp2};
                        List<ShelfProductVcs> vcsWaitList = vcsDao.getListShelfProductVcsListByStatus(dbEnv, uuid, verProd, waStatus);
                        if (!vcsWaitList.isEmpty()) {
                            JSONObject result = ProductUtils.rejectApprove(dbEnv, uuid, verProd, respCode, username, remark);
                            returnVal.put("status", result.get("status"))
                                    .put("description", result.getString("description"));
                        } else {
                            returnVal.put("status", 500)
                                    .put("description", StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0014"));
                        }
                    } else if ("approve".equalsIgnoreCase(jsonProd.getString("action"))) {
                        Integer statusWaitApp = StatusUtils.getWaittoApprove(dbEnv).getStatusCode();
                        Integer statusWaitApp2 = StatusUtils.getWaittoApprove2(dbEnv).getStatusCode();
                        Integer[] waStatus = {statusWaitApp, statusWaitApp2};
                        List<ShelfProductVcs> vcsWaitList = vcsDao.getListShelfProductVcsListByStatus(dbEnv, uuid, verProd, waStatus);
                        if (!vcsWaitList.isEmpty()) {
                            ShelfProductDao dao = new ShelfProductDao();
                            Integer statusWait = StatusUtils.getWaittoApprove(dbEnv).getStatusCode();
                            JSONObject resp = dao.getProductByStatus(dbEnv, statusWait, jsonProd.getString("uuid"));
                            jsonProd.put("activeDate", resp.get("activeDate"));
                            jsonProd.put("endDate", resp.get("endDate"));
                            if (jsonProd.getString("activeDate").isEmpty() && jsonProd.getString("endDate").isEmpty()) {
                                Integer statusWait2 = StatusUtils.getWaittoApprove2(dbEnv).getStatusCode();
                                resp = dao.getProductByStatus(dbEnv, statusWait2, jsonProd.getString("uuid"));
                                jsonProd.put("activeDate", resp.get("activeDate"));
                                jsonProd.put("endDate", resp.get("endDate"));
                            }
                            JSONObject confirmField = ProductUtils.confirmProduct(datas, dbEnv);
                            if (confirmField.getBoolean("status") || confirm) {
                                Integer statusActive = StatusUtils.getActive(dbEnv).getStatusCode();
                                Integer statusInactive = StatusUtils.getInActive(dbEnv).getStatusCode();
                                Integer statusWaiApp2 = StatusUtils.getWaittoApprove2(dbEnv).getStatusCode();
                                Integer statusExpire = StatusUtils.getExpired(dbEnv).getStatusCode();
                                Integer statusCancel = StatusUtils.getCancel(dbEnv).getStatusCode();
                                Integer statusPause = StatusUtils.getPause(dbEnv).getStatusCode();
                                Integer statusNotUse = StatusUtils.getNotUse(dbEnv).getStatusCode();
                                Integer statusWapp3 = StatusUtils.getWaittoApprove3(dbEnv).getStatusCode();
                                Integer[] waitapprove2 = {statusWaiApp2};
                                List<ShelfProductVcs> vcsWaitApprove2List = vcsDao.getListShelfProductVcsListByStatus(dbEnv, uuid, waitapprove2);
                                int newversion = new ShelfProductVcsDao().getMaxVersionProduct(dbEnv, uuid);
                                if (vcsWaitApprove2List != null && vcsWaitApprove2List.size() > 0) {
                                    Integer status = statusInactive;
                                    ShelfProductDao prodDao = new ShelfProductDao();
                                    HashMap mapWaitApp2 = prodDao.getProductByStatus(dbEnv, uuid, statusWaiApp2);
                                    Set<String> inWaitApp2KName = mapWaitApp2.keySet();
                                    for (String k2 : inWaitApp2KName) {
                                        JSONObject data = (JSONObject) mapWaitApp2.get(k2);
                                        if (data.has("activeDate")) {
                                            Date activeDate = ValidUtils.str2Date(data.getString("activeDate"), "yyyy-MM-dd");
                                            Date curDate = ValidUtils.str2Date(DateUtils.getDisplayEnDate(new Date(), "yyyy-MM-dd"), "yyyy-MM-dd");
                                            if (activeDate.compareTo(curDate) <= 0) {
                                                status = statusActive;
                                            }
                                        }
                                    }
                                    List<ShelfProductVcs> list = new ArrayList<>();
                                    ShelfProduct prod = vcsWaitApprove2List.get(0).getProdUuid();
                                    for (ShelfProductVcs vcs : vcsWaitApprove2List) {
                                        vcs.setStatus(status);
                                        if (jsonProd.has("bank") && jsonProd.getBoolean("bank") && (status.equals(statusActive) || status.equals(statusInactive))) {
                                            vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(statusWapp3)));
                                        }
                                        vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(status)));
                                        if (!respCode.isEmpty()) {
                                            vcs.setAttr2(StatusUtils.setStatus(vcs.getAttr2(), ValidUtils.null2NoData(respCode)));
                                        }
                                        if (!remark.isEmpty()) {
                                            vcs.setAttr3(StatusUtils.setRemark(vcs.getAttr3(), ValidUtils.null2NoData(remark)));
                                        }
                                        vcs.setVerProd(newversion);
                                        vcs.setVerComp(newversion);
                                        vcs.setUpdateBy(username);
                                        if (null == vcs.getCompUuid()) {
                                            prod.setStatus(vcs.getStatus());    //last status of product
                                            prod.setUpdateBy(username);
                                        }
                                        list.add(vcs);
                                    }
                                    prod.setAttr2("Y");
                                    JSONObject result = ProductUtils.approveProduct(dbEnv, uuid, prod, list, (jsonProd.has("termsNCondition") ? jsonProd.getString("termsNCondition") : ""));
                                    returnVal.put("status", result.get("status"))
                                            .put("description", result.getString("description"));
                                } else {
                                    if ((newversion - 1) == 0) {
                                        Integer[] waitapprove = {statusWaitApp};
                                        List<ShelfProductVcs> vcsWaitApproveList = vcsDao.getListShelfProductVcsListByStatus(dbEnv, uuid, waitapprove);
                                        if (vcsWaitApproveList.size() > 0) {
                                            ShelfProduct prod = vcsWaitApproveList.get(0).getProdUuid();
                                            List<ShelfProductVcs> list = new ArrayList<>();
                                            for (ShelfProductVcs vcs : vcsWaitApproveList) {
                                                vcs.setStatus(statusWaiApp2);
                                                if (jsonProd.has("bank") && jsonProd.getBoolean("bank") && (statusWaiApp2.equals(statusActive) || statusWaiApp2.equals(statusInactive))) {
                                                    vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(statusWapp3)));
                                                }
                                                vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(statusWaiApp2)));
                                                if (!respCode.isEmpty()) {
                                                    vcs.setAttr2(StatusUtils.setStatus(vcs.getAttr2(), ValidUtils.null2NoData(respCode)));
                                                }
                                                if (!remark.isEmpty()) {
                                                    vcs.setAttr3(StatusUtils.setRemark(vcs.getAttr3(), ValidUtils.null2NoData(remark)));
                                                }
                                                vcs.setUpdateBy(username);
                                                if (null == vcs.getCompUuid()) {
                                                    prod.setStatus(vcs.getStatus());    //last status of product
                                                    prod.setUpdateBy(username);
                                                }
                                                list.add(vcs);
                                            }
                                            JSONObject result = ProductUtils.approveProduct(dbEnv, uuid, prod, list, (jsonProd.has("termsNCondition") ? jsonProd.getString("termsNCondition") : ""));
                                            returnVal.put("status", result.get("status"))
                                                    .put("description", result.getString("description"));
                                        } else {
                                            returnVal.put("status", "500")
                                                    .put("description", StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0015"));
                                        }
                                    } else {
                                        Integer[] active = {statusActive};
                                        List<ShelfProductVcs> vcsActiveList = vcsDao.getListShelfProductVcsListByStatus(dbEnv, uuid, active);
                                        Integer[] inActive = {statusInactive};
                                        List<ShelfProductVcs> vcsInActiveList = vcsDao.getListShelfProductVcsListByStatus(dbEnv, uuid, inActive);
                                        Integer[] waitapprove = {statusWaitApp};
                                        List<ShelfProductVcs> vcsWaitApproveList = vcsDao.getListShelfProductVcsListByStatus(dbEnv, uuid, waitapprove);
                                        Integer[] pause = {statusPause};
                                        List<ShelfProductVcs> vcsPauseList = vcsDao.getListShelfProductVcsListByStatus(dbEnv, uuid, pause);
                                        if (vcsInActiveList.size() > 0) {
                                            List<ShelfProductVcs> list = new ArrayList<>();
                                            ShelfProduct prod = vcsInActiveList.get(0).getProdUuid();
                                            Integer statusAct2 = statusExpire;
                                            Integer status = statusInactive;
                                            ShelfProductDao prodDao = new ShelfProductDao();
                                            HashMap mapWaitApp = prodDao.getProductByStatus(dbEnv, uuid, statusWaitApp);
                                            Set<String> inWaitApp2KName = mapWaitApp.keySet();
                                            for (String k2 : inWaitApp2KName) {
                                                JSONObject data = (JSONObject) mapWaitApp.get(k2);
                                                if (data.has("activeDate")) {
                                                    Date activeDate = ValidUtils.str2Date(data.getString("activeDate"), "yyyy-MM-dd");
                                                    Date curDate = ValidUtils.str2Date(DateUtils.getDisplayEnDate(new Date(), "yyyy-MM-dd"), "yyyy-MM-dd");
                                                    if (activeDate.compareTo(curDate) <= 0) {
                                                        status = statusActive;
                                                    }
                                                }
                                            }
                                            if (status.equals(statusActive)) {
                                                for (ShelfProductVcs vcs : vcsActiveList) {
                                                    vcs.setStatus(statusAct2);
                                                    if (jsonProd.has("bank") && jsonProd.getBoolean("bank") && (statusAct2.equals(statusActive) || statusAct2.equals(statusInactive))) {
                                                        vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(statusWapp3)));
                                                    }
                                                    vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(statusAct2)));
                                                    if (!respCode.isEmpty()) {
                                                        vcs.setAttr2(StatusUtils.setStatus(vcs.getAttr2(), ValidUtils.null2NoData(respCode)));
                                                    }
                                                    if (!remark.isEmpty()) {
                                                        vcs.setAttr3(StatusUtils.setRemark(vcs.getAttr3(), ValidUtils.null2NoData(remark)));
                                                    }
                                                    vcs.setUpdateBy(username);
                                                    if (null == vcs.getCompUuid()) {
                                                        prod.setStatus(vcs.getStatus());    //last status of product
                                                        prod.setUpdateBy(username);
                                                        for (ShelfProductDtl dtl : vcs.getShelfProductDtlList()) {
                                                            if ("activeDate".equalsIgnoreCase(dtl.getLkCode())) {
                                                                dtl.setLkValue(DateUtils.getDisplayEnDate(new Date(), "yyyy-MM-dd"));
                                                            }
                                                        }
                                                    }
                                                    list.add(vcs);
                                                }
                                            } else {
                                                for (ShelfProductVcs vcs : vcsInActiveList) {
                                                    vcs.setStatus(statusCancel);
                                                    if (jsonProd.has("bank") && jsonProd.getBoolean("bank") && (statusCancel.equals(statusActive) || statusCancel.equals(statusInactive))) {
                                                        vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(statusWapp3)));
                                                    }
                                                    vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(statusCancel)));
                                                    if (!respCode.isEmpty()) {
                                                        vcs.setAttr2(StatusUtils.setStatus(vcs.getAttr2(), ValidUtils.null2NoData(respCode)));
                                                    }
                                                    if (!remark.isEmpty()) {
                                                        vcs.setAttr3(StatusUtils.setRemark(vcs.getAttr3(), ValidUtils.null2NoData(remark)));
                                                    }
                                                    vcs.setUpdateBy(username);
                                                    if (null == vcs.getCompUuid()) {
                                                        prod.setStatus(vcs.getStatus());    //last status of product
                                                        prod.setUpdateBy(username);
                                                    }
                                                    list.add(vcs);
                                                }
                                            }
                                            if (vcsPauseList.size() > 0 && status.equals(statusActive)) {
                                                for (ShelfProductVcs vcs : vcsPauseList) {
                                                    vcs.setStatus(statusNotUse);
                                                    if (jsonProd.has("bank") && jsonProd.getBoolean("bank") && (statusNotUse.equals(statusActive) || statusNotUse.equals(statusInactive))) {
                                                        vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(statusWapp3)));
                                                    }
                                                    vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(statusNotUse)));
                                                    if (!respCode.isEmpty()) {
                                                        vcs.setAttr2(StatusUtils.setStatus(vcs.getAttr2(), ValidUtils.null2NoData(respCode)));
                                                    }
                                                    if (!remark.isEmpty()) {
                                                        vcs.setAttr3(StatusUtils.setRemark(vcs.getAttr3(), ValidUtils.null2NoData(remark)));
                                                    }
                                                    vcs.setUpdateBy(username);
                                                    if (null == vcs.getCompUuid()) {
                                                        prod.setStatus(vcs.getStatus());    //last status of product
                                                        prod.setUpdateBy(username);
                                                    }
                                                    list.add(vcs);

                                                }
                                            }
                                            for (ShelfProductVcs vcs : vcsWaitApproveList) {
                                                vcs.setStatus(status);
                                                if (jsonProd.has("bank") && jsonProd.getBoolean("bank") && (status.equals(statusActive) || status.equals(statusInactive))) {
                                                    vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(statusWapp3)));
                                                }
                                                vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(status)));
                                                if (!respCode.isEmpty()) {
                                                    vcs.setAttr2(StatusUtils.setStatus(vcs.getAttr2(), ValidUtils.null2NoData(respCode)));
                                                }
                                                if (!remark.isEmpty()) {
                                                    vcs.setAttr3(StatusUtils.setRemark(vcs.getAttr3(), ValidUtils.null2NoData(remark)));
                                                }
                                                vcs.setVerProd(newversion);
                                                vcs.setVerComp(newversion);
                                                vcs.setUpdateBy(username);
                                                if (null == vcs.getCompUuid()) {
                                                    prod.setStatus(vcs.getStatus());    //last status of product
                                                    prod.setUpdateBy(username);
                                                }
                                                list.add(vcs);
                                            }
                                            prod.setAttr2("Y");
                                            JSONObject result = ProductUtils.approveProduct(dbEnv, uuid, prod, list, (jsonProd.has("termsNCondition") ? jsonProd.getString("termsNCondition") : ""));
                                            returnVal.put("status", result.get("status"))
                                                    .put("description", result.getString("description"));
                                        } else {
                                            ShelfProductDao prodDao = new ShelfProductDao();
                                            HashMap mapWaitApp = prodDao.getProductByStatus(dbEnv, uuid, statusWaitApp);
                                            Set<String> inWaitApp2KName = mapWaitApp.keySet();
                                            Integer statusAct2 = statusExpire;
                                            boolean isActive = false;
                                            for (String k2 : inWaitApp2KName) {
                                                JSONObject data = (JSONObject) mapWaitApp.get(k2);
                                                if (data.has("activeDate")) {
                                                    Date activeDate = ValidUtils.str2Date(data.getString("activeDate"), "yyyy-MM-dd");
                                                    Date curDate = ValidUtils.str2Date(DateUtils.getDisplayEnDate(new Date(), "yyyy-MM-dd"), "yyyy-MM-dd");
                                                    if (activeDate.compareTo(curDate) <= 0) {
                                                        isActive = true;
                                                    }
                                                }
                                            }
                                            if (isActive) {
                                                List<ShelfProductVcs> list = new ArrayList<>();
                                                ShelfProduct prod = null;
                                                if (vcsActiveList.size() > 0) {
                                                    prod = vcsInActiveList.get(0).getProdUuid();
                                                    for (ShelfProductVcs vcs : vcsActiveList) {
                                                        vcs.setStatus(statusAct2);
                                                        if (jsonProd.has("bank") && jsonProd.getBoolean("bank") && (statusAct2.equals(statusActive) || statusAct2.equals(statusInactive))) {
                                                            vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(statusWapp3)));
                                                        }
                                                        vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(statusAct2)));
                                                        if (!respCode.isEmpty()) {
                                                            vcs.setAttr2(StatusUtils.setStatus(vcs.getAttr2(), ValidUtils.null2NoData(respCode)));
                                                        }
                                                        if (!remark.isEmpty()) {
                                                            vcs.setAttr3(StatusUtils.setRemark(vcs.getAttr3(), ValidUtils.null2NoData(remark)));
                                                        }
                                                        vcs.setUpdateBy(username);
                                                        if (null == vcs.getCompUuid()) {
                                                            prod.setStatus(vcs.getStatus());    //last status of product
                                                            prod.setUpdateBy(username);
                                                            for (ShelfProductDtl dtl : vcs.getShelfProductDtlList()) {
                                                                if ("activeDate".equalsIgnoreCase(dtl.getLkCode())) {
                                                                    dtl.setLkValue(DateUtils.getDisplayEnDate(new Date(), "yyyy-MM-dd"));
                                                                }
                                                            }
                                                        }
                                                        list.add(vcs);
                                                    }
                                                }
                                                if (null == prod) {
                                                    prod = vcsWaitApproveList.get(0).getProdUuid();
                                                }
                                                if (vcsPauseList.size() > 0) {
                                                    for (ShelfProductVcs vcs : vcsPauseList) {
                                                        vcs.setStatus(statusNotUse);
                                                        if (jsonProd.has("bank") && jsonProd.getBoolean("bank") && (statusNotUse.equals(statusActive) || statusNotUse.equals(statusInactive))) {
                                                            vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(statusWapp3)));
                                                        }
                                                        vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(statusNotUse)));
                                                        if (!respCode.isEmpty()) {
                                                            vcs.setAttr2(StatusUtils.setStatus(vcs.getAttr2(), ValidUtils.null2NoData(respCode)));
                                                        }
                                                        if (!remark.isEmpty()) {
                                                            vcs.setAttr3(StatusUtils.setRemark(vcs.getAttr3(), ValidUtils.null2NoData(remark)));
                                                        }
                                                        vcs.setUpdateBy(username);
                                                        if (null == vcs.getCompUuid()) {
                                                            prod.setStatus(vcs.getStatus());    //last status of product
                                                            prod.setUpdateBy(username);
                                                        }
                                                        list.add(vcs);

                                                    }
                                                }
                                                for (ShelfProductVcs vcs : vcsWaitApproveList) {
                                                    vcs.setStatus(statusActive);
                                                    if (jsonProd.has("bank") && jsonProd.getBoolean("bank") && (statusActive.equals(statusActive) || statusActive.equals(statusInactive))) {
                                                        vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(statusWapp3)));
                                                    }
                                                    vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(statusActive)));
                                                    if (!respCode.isEmpty()) {
                                                        vcs.setAttr2(StatusUtils.setStatus(vcs.getAttr2(), ValidUtils.null2NoData(respCode)));
                                                    }
                                                    if (!remark.isEmpty()) {
                                                        vcs.setAttr3(StatusUtils.setRemark(vcs.getAttr3(), ValidUtils.null2NoData(remark)));
                                                    }
                                                    vcs.setUpdateBy(username);
                                                    vcs.setVerProd(newversion);
                                                    vcs.setVerComp(newversion);
                                                    if (null == vcs.getCompUuid()) {
                                                        prod.setStatus(vcs.getStatus());    //last status of product
                                                        prod.setUpdateBy(username);
                                                    }
                                                    list.add(vcs);
                                                }
                                                prod.setAttr2("Y");
                                                JSONObject result = ProductUtils.approveProduct(dbEnv, uuid, prod, list, (jsonProd.has("termsNCondition") ? jsonProd.getString("termsNCondition") : ""));
                                                returnVal.put("status", result.get("status"))
                                                        .put("description", result.getString("description"));
                                            } else {
                                                if (vcsWaitApproveList.size() > 0) {
                                                    List<ShelfProductVcs> list = new ArrayList<>();
                                                    ShelfProduct prod = vcsWaitApproveList.get(0).getProdUuid();
                                                    for (ShelfProductVcs vcs : vcsWaitApproveList) {
                                                        vcs.setStatus(statusInactive);
                                                        if (jsonProd.has("bank") && jsonProd.getBoolean("bank") && (statusInactive.equals(statusActive) || statusInactive.equals(statusInactive))) {
                                                            vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(statusWapp3)));
                                                        }
                                                        vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(statusInactive)));
                                                        if (!respCode.isEmpty()) {
                                                            vcs.setAttr2(StatusUtils.setStatus(vcs.getAttr2(), ValidUtils.null2NoData(respCode)));
                                                        }
                                                        if (!remark.isEmpty()) {
                                                            vcs.setAttr3(StatusUtils.setRemark(vcs.getAttr3(), ValidUtils.null2NoData(remark)));
                                                        }
                                                        vcs.setUpdateBy(username);
                                                        vcs.setVerProd(newversion);
                                                        vcs.setVerComp(newversion);
                                                        if (null == vcs.getCompUuid()) {
                                                            prod.setStatus(vcs.getStatus());    //last status of product
                                                            prod.setUpdateBy(username);
                                                        }
                                                        list.add(vcs);
                                                    }
                                                    prod.setAttr2("Y");
                                                    JSONObject result = ProductUtils.approveProduct(dbEnv, uuid, prod, list, (jsonProd.has("termsNCondition") ? jsonProd.getString("termsNCondition") : ""));
                                                    returnVal.put("status", result.get("status"))
                                                            .put("description", result.getString("description"));
                                                }
                                                if (vcsActiveList.size() > 0) {
                                                    List<ShelfProductVcs> list = new ArrayList<>();
                                                    ShelfProduct prod = vcsActiveList.get(0).getProdUuid();
                                                    HashMap mapActive = prodDao.getProductByStatus(dbEnv, uuid, statusActive);
                                                    Set<String> inActiveKName = mapActive.keySet();
                                                    for (String k2 : inActiveKName) {
                                                        JSONObject data = (JSONObject) mapActive.get(k2);
                                                        if (data.has("activeDate")) {
                                                            Date endDate = ValidUtils.str2Date(data.getString("endDate"), "yyyy-MM-dd");
                                                            Date curDate = ValidUtils.str2Date(DateUtils.getDisplayEnDate(new Date(), "yyyy-MM-dd"), "yyyy-MM-dd");
                                                            if (endDate.compareTo(curDate) < 0) {
                                                                for (ShelfProductVcs vcs : vcsWaitApproveList) {
                                                                    vcs.setStatus(statusExpire);
                                                                    if (jsonProd.has("bank") && jsonProd.getBoolean("bank") && (statusExpire.equals(statusActive) || statusExpire.equals(statusInactive))) {
                                                                        vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(statusWapp3)));
                                                                    }
                                                                    vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(statusExpire)));
                                                                    if (!respCode.isEmpty()) {
                                                                        vcs.setAttr2(StatusUtils.setStatus(vcs.getAttr2(), ValidUtils.null2NoData(respCode)));
                                                                    }
                                                                    if (!remark.isEmpty()) {
                                                                        vcs.setAttr3(StatusUtils.setRemark(vcs.getAttr3(), ValidUtils.null2NoData(remark)));
                                                                    }
                                                                    vcs.setUpdateBy(username);
                                                                    if (null == vcs.getCompUuid()) {
                                                                        prod.setStatus(vcs.getStatus());    //last status of product
                                                                        prod.setUpdateBy(username);
                                                                        for (ShelfProductDtl dtl : vcs.getShelfProductDtlList()) {
                                                                            if ("activeDate".equalsIgnoreCase(dtl.getLkCode())) {
                                                                                dtl.setLkValue(data.getString("endDate"));
                                                                            }
                                                                        }
                                                                    }
                                                                    list.add(vcs);
                                                                }
                                                                prod.setAttr2("Y");
                                                                JSONObject result = ProductUtils.approveProduct(dbEnv, uuid, prod, list, (jsonProd.has("termsNCondition") ? jsonProd.getString("termsNCondition") : ""));
                                                                returnVal.put("status", result.get("status"))
                                                                        .put("description", result.getString("description"));
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                returnVal.put("status", 500)
                                        .put("confirmmsg", confirmField.getString("description"));
                            }
                        } else {
                            returnVal.put("status", 500)
                                    .put("description", StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0016"));
                        }
                    } else if ("sendpause".equalsIgnoreCase(jsonProd.getString("action"))) {  //by version (set status to waittopause)
                        Integer statusActive = StatusUtils.getActive(dbEnv).getStatusCode();
                        Integer[] activeStatus = {statusActive};
                        List<ShelfProductVcs> vcsInprogressList = vcsDao.getListShelfProductVcsListByStatus(dbEnv, uuid, activeStatus);
                        if (!vcsInprogressList.isEmpty()) {
                            Integer waitPauseStatus = StatusUtils.getWaittoPause(dbEnv).getStatusCode();
                            JSONObject result = ProductUtils.pauseProduct(dbEnv, uuid, verProd, respCode, username, remark, waitPauseStatus, activeStatus);
                            returnVal.put("status", result.get("status"))
                                    .put("description", result.getString("description"));
                        } else {
                            returnVal.put("status", 500)
                                    .put("description", StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0017"));
                        }
                    } else if ("rejectpause".equalsIgnoreCase(jsonProd.getString("action"))) {  //by version (set status to active)
                        Integer statusWaitPause = StatusUtils.getWaittoPause(dbEnv).getStatusCode();
                        Integer[] waitpause = {statusWaitPause};
                        List<ShelfProductVcs> vcsWaitPauseList = vcsDao.getListShelfProductVcsListByStatus(dbEnv, uuid, waitpause);
                        if (!vcsWaitPauseList.isEmpty()) {
                            Integer activeStatus = StatusUtils.getActive(dbEnv).getStatusCode();
                            Integer[] waitPauseStatus = {statusWaitPause};
                            JSONObject result = ProductUtils.pauseProduct(dbEnv, uuid, verProd, respCode, username, remark, activeStatus, waitPauseStatus);
                            returnVal.put("status", result.get("status"))
                                    .put("description", result.getString("description"));
                        } else {
                            returnVal.put("status", 500)
                                    .put("description", StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0018"));
                        }
                    } else if ("pause".equalsIgnoreCase(jsonProd.getString("action"))) {  //by version
                        Integer statusWaitPause = StatusUtils.getWaittoPause(dbEnv).getStatusCode();
                        Integer[] waitpause = {statusWaitPause};
                        List<ShelfProductVcs> vcsWaitPauseList = vcsDao.getListShelfProductVcsListByStatus(dbEnv, uuid, waitpause);
                        if (!vcsWaitPauseList.isEmpty()) {
                            Integer pauseStatus = StatusUtils.getPause(dbEnv).getStatusCode();
                            Integer[] waitPauseStatus = {statusWaitPause};
                            JSONObject result = ProductUtils.pauseProduct(dbEnv, uuid, verProd, respCode, username, remark, pauseStatus, waitPauseStatus);
                            returnVal.put("status", result.get("status"))
                                    .put("description", result.getString("description"));
                        } else {
                            returnVal.put("status", 500)
                                    .put("description", StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0019"));
                        }
                    } else if ("sendstart".equalsIgnoreCase(jsonProd.getString("action"))) {  //by version
                        Integer currStatus = StatusUtils.getPause(dbEnv).getStatusCode();
                        Integer[] pause = {currStatus};
                        List<ShelfProductVcs> vcsPauseList = vcsDao.getListShelfProductVcsListByStatus(dbEnv, uuid, pause);
                        if (!vcsPauseList.isEmpty()) {
                            Integer nextStatus = StatusUtils.getWaittoStart(dbEnv).getStatusCode();
                            JSONObject result = ProductUtils.startProduct(dbEnv, uuid, verProd, respCode, username, remark, currStatus, nextStatus);
                            returnVal.put("status", result.get("status"))
                                    .put("description", result.getString("description"));
                        } else {
                            returnVal.put("status", 500)
                                    .put("description", StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0020"));
                        }
                    } else if ("rejectstart".equalsIgnoreCase(jsonProd.getString("action"))) {  //by version
                        Integer currStatus = StatusUtils.getWaittoStart(dbEnv).getStatusCode();
                        Integer[] pause = {currStatus};
                        List<ShelfProductVcs> vcsPauseList = vcsDao.getListShelfProductVcsListByStatus(dbEnv, uuid, pause);
                        if (!vcsPauseList.isEmpty()) {
                            Integer nextStatus = StatusUtils.getPause(dbEnv).getStatusCode();
                            JSONObject result = ProductUtils.startProduct(dbEnv, uuid, verProd, respCode, username, remark, currStatus, nextStatus);
                            returnVal.put("status", result.get("status"))
                                    .put("description", result.getString("description"));
                        } else {
                            returnVal.put("status", 500)
                                    .put("description", StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0020"));
                        }
                    } else if ("start".equalsIgnoreCase(jsonProd.getString("action"))) {  //by version
                        Integer currStatus = StatusUtils.getWaittoStart(dbEnv).getStatusCode();
                        Integer[] pause = {currStatus};
                        List<ShelfProductVcs> vcsPauseList = vcsDao.getListShelfProductVcsListByStatus(dbEnv, uuid, pause);
                        if (!vcsPauseList.isEmpty()) {
                            Integer nextStatus = StatusUtils.getActive(dbEnv).getStatusCode();
                            JSONObject result = ProductUtils.startProduct(dbEnv, uuid, verProd, respCode, username, remark, currStatus, nextStatus);
                            returnVal.put("status", result.get("status"))
                                    .put("description", result.getString("description"));
                        } else {
                            returnVal.put("status", 500)
                                    .put("description", StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0020"));
                        }
                    } else if ("senddelete".equalsIgnoreCase(jsonProd.getString("action"))) { // by product
                        Integer statusInactive = StatusUtils.getInActive(dbEnv).getStatusCode();   //inactive, terminate,expire
                        Integer statusTerminate = StatusUtils.getTerminate(dbEnv).getStatusCode();
                        Integer statusExpire = StatusUtils.getExpired(dbEnv).getStatusCode();
                        Integer statusNotUse = StatusUtils.getNotUse(dbEnv).getStatusCode();
                        Integer[] status = {statusInactive, statusTerminate, statusExpire, statusNotUse};
                        List<ShelfProductVcs> vcsActiveList = vcsDao.getListShelfProductVcsListByStatusNotIn(dbEnv, uuid, status);
                        if (vcsActiveList.isEmpty()) {
                            JSONObject result = ProductUtils.sendDeleteProduct(dbEnv, uuid, verProd, respCode, username, remark);
                            returnVal.put("status", result.get("status"))
                                    .put("description", result.getString("description"));
                        } else {
                            if (!vcsActiveList.isEmpty()) {
                                returnVal.put("status", 500)
                                        .put("description", StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0021"));
                            }
                        }
                    } else if ("rejectdelete".equalsIgnoreCase(jsonProd.getString("action"))) { //by product
                        Integer statusWDelete = StatusUtils.getWaittoDelete(dbEnv).getStatusCode();
                        Integer[] waitdelete = {statusWDelete};
                        List<ShelfProductVcs> vcsWaitDeleteList = vcsDao.getListShelfProductVcsListByStatus(dbEnv, uuid, waitdelete);
                        if (!vcsWaitDeleteList.isEmpty()) {
                            JSONObject result = ProductUtils.rejectDelete(dbEnv, uuid, verProd, respCode, username, remark);
                            returnVal.put("status", result.get("status"))
                                    .put("description", result.getString("description"));
                        } else {
                            returnVal.put("status", 500)
                                    .put("description", StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0022"));
                        }
                    } else if ("delete".equalsIgnoreCase(jsonProd.getString("action"))) { //by product
                        Integer statusWDelete = StatusUtils.getWaittoDelete(dbEnv).getStatusCode();
                        Integer[] waitdelete = {statusWDelete};
                        List<ShelfProductVcs> vcsWaitDeleteList = vcsDao.getListShelfProductVcsListByStatus(dbEnv, uuid, waitdelete);
                        List<ShelfProductVcs> vcsNotWDeleteList = vcsDao.getListShelfProductVcsListByStatusNotIn(dbEnv, uuid, waitdelete);
                        if (!vcsWaitDeleteList.isEmpty() && vcsNotWDeleteList.isEmpty()) {
                            JSONObject result = ProductUtils.deleteProduct(dbEnv, uuid, verProd, respCode, username, remark);
                            returnVal.put("status", result.get("status"))
                                    .put("description", result.getString("description"));
                        } else {
                            returnVal.put("status", 500)
                                    .put("description", StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0023"));
                        }
                    } else if ("sendterminate".equalsIgnoreCase(jsonProd.getString("action"))) {  //by product (set status to waittoterminate)
                        Integer statusActive = StatusUtils.getActive(dbEnv).getStatusCode();
                        Integer[] active = {statusActive};
                        List<ShelfProductVcs> vcsActiveList = vcsDao.getListShelfProductVcsListByStatus(dbEnv, uuid, active);
                        if (!vcsActiveList.isEmpty()) {
                            Integer status = StatusUtils.getWaittoTerminate(dbEnv).getStatusCode();
                            JSONObject result = ProductUtils.terminateProduct(dbEnv, uuid, verProd, respCode, username, remark, status);
                            returnVal.put("status", result.get("status"))
                                    .put("description", result.getString("description"));
                        } else {
                            returnVal.put("status", 500)
                                    .put("description", StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0024"));
                        }
                    } else if ("rejectterminate".equalsIgnoreCase(jsonProd.getString("action"))) {  //by product (last status before waittoterminate)
                        Integer statusWterminate = StatusUtils.getWaittoTerminate(dbEnv).getStatusCode();
                        Integer[] waitTerminate = {statusWterminate};
                        List<ShelfProductVcs> vcsWaitTerminateList = vcsDao.getListShelfProductVcsListByStatus(dbEnv, uuid, waitTerminate);
                        if (!vcsWaitTerminateList.isEmpty()) {
                            JSONObject result = ProductUtils.rejectTerminateProduct(dbEnv, uuid, verProd, respCode, username, remark);
                            returnVal.put("status", result.get("status"))
                                    .put("description", result.getString("description"));
                        } else {
                            returnVal.put("status", 500)
                                    .put("description", StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0025"));
                        }
                    } else if ("terminate".equalsIgnoreCase(jsonProd.getString("action"))) {        //by product
                        Integer statusWterminate = StatusUtils.getWaittoTerminate(dbEnv).getStatusCode();
                        Integer[] waitTerminate = {statusWterminate};
                        List<ShelfProductVcs> vcsWaitTerminateList = vcsDao.getListShelfProductVcsListByStatus(dbEnv, uuid, waitTerminate);
                        if (!vcsWaitTerminateList.isEmpty()) {
                            Integer status = StatusUtils.getTerminate(dbEnv).getStatusCode();
                            JSONObject result = ProductUtils.terminateProduct(dbEnv, uuid, verProd, respCode, username, remark, status);
                            returnVal.put("status", result.get("status"))
                                    .put("description", result.getString("description"));
                        } else {
                            returnVal.put("status", 500)
                                    .put("description", StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0026"));
                        }
                    }
                } else {
                    JSONObject validField = ProductUtils.validProduct(datas, dbEnv);
                    JSONObject confirmField = ProductUtils.confirmProduct(datas, dbEnv);
                    if (validField.getBoolean("status") && (confirmField.getBoolean("status") || confirm)) {
                        String uuid = (jsonProd.has("uuid") ? jsonProd.getString("uuid") : "");
                        Integer statusInprogress = StatusUtils.getInprogress(dbEnv).getStatusCode();
                        int prodVer = (jsonProd.has("prodVer") ? ValidUtils.obj2Int(jsonProd.get("prodVer")) : 0);
                        int status = (jsonProd.has("status") ? ValidUtils.obj2Int(jsonProd.get("status")) : 0);
                        boolean save = false;
                        if (prodVer == 0 && statusInprogress.equals(status) && !uuid.isEmpty()) {
                            save = true;
                        }
                        if (save) {
                            ShelfProduct prod = ProductUtils.getProduct(dbEnv, datas, sysdate, username);
                            if (null != prod) {
                                prod.setBusinessDept(ValidUtils.null2Separator(prod.getBusinessDept(), businessDept));
                                prod.setBusinessLine(ValidUtils.null2Separator(prod.getBusinessLine(), businessLine));
                                JSONArray retProd = new ShelfProductDao().updateShelfProduct(dbEnv, prod, username);
                                returnVal.put("data", retProd);
                            }
                        } else {
                            returnVal.put("status", 500)
                                    .put("description", StatusUtils.getErrorMessageByCode(subState, "SHELF0001"));
                        }
//                        }
                    } else {
                        if (!confirmField.getBoolean("status")) {
                            returnVal.put("confirmmsg", confirmField.getString("description"));
                        }
                        returnVal.put("status", 500)
                                .put("description", validField.getString("description"));
                    }
                }
            } else {
                returnVal.put("status", 500)
                        .put("description", StatusUtils.getErrorMessageByCode(subState, "SHELF0001"));
            }
        } catch (JSONException | NullPointerException | HibernateException | ParseException | SQLException e) {
            //e.printStackTrace();
            log.info("" + e);
            logger.info(e.getMessage());
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @Log_decorator
    @RequestMapping(value = "info/{prodUuid}/{status}/{verProd}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getProductInfo(HttpSession session, HttpServletResponse response,
            HttpServletRequest request,
            @PathVariable String prodUuid,
            @PathVariable int status,
            @PathVariable int verProd,
            @RequestHeader(value = "sub_state", required = false) String subState
    ) {//statusCode ==> lookup_value
        log.info(String.format("GET : /shelf/product/v1/info/%s/%d/%d", prodUuid, status, verProd));
        logger.info(String.format("GET : /shelf/product/v1/info/%s/%d/%d", prodUuid, status, verProd));
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            returnVal.put("datas", ProductUtils.infoProducts(subState, prodUuid, status, verProd));
        } catch (JSONException | NullPointerException | HibernateException | ParseException e) {
            log.info("" + e);
            logger.info(e.getMessage());
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @Log_decorator
    @RequestMapping(value = "list/{prodUuid}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getProductsInfo(HttpSession session, HttpServletResponse response,
            HttpServletRequest request,
            @PathVariable String prodUuid,
            @RequestHeader(value = "sub_state", required = false) String subState
    ) {
        log.info(String.format("GET : /shelf/product/v1/list/%s", prodUuid));
        logger.info(String.format("GET : /shelf/product/v1/list/%s", prodUuid));
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONObject());
        try {
            ShelfProductVcsDao shelfProductDao = new ShelfProductVcsDao();
            List<ShelfProductVcs> listVcs = shelfProductDao.getListShelfProductVcsListByProductUuid(subState, prodUuid);
//            returnVal.put("datas", ProductUtils.groupProduct(shelfProduct));
            JSONArray productList = new JSONArray();
            for (int i = 0; i < listVcs.size(); i++) {
                ShelfProductVcs vcs = listVcs.get(i);
                JSONObject dtl = new JSONObject()
                        .put("prodCode", ValidUtils.null2NoData(vcs.getProdUuid().getProdCode()))
                        .put("prodName", ValidUtils.null2NoData(vcs.getProdUuid().getProdName()))
                        .put("verProd", ValidUtils.null2NoData((vcs.getVerProd() == 0 ? "" : ValidUtils.obj2Int(vcs.getVerProd()))))
                        .put("statusCode", StatusUtils.getStatusByCode(subState,String.valueOf(vcs.getStatus())).getStatusCode())
                        .put("statusName", StatusUtils.getStatusByCode(subState, String.valueOf(vcs.getStatus())).getStatusNameEn());

                for (ShelfProductDtl spDtl : vcs.getShelfProductDtlList()) {
                    dtl.put(ValidUtils.null2NoData(spDtl.getLkCode()), ValidUtils.null2NoData(spDtl.getLkValue()));
                }
                productList.put(dtl);
            }
            returnVal.put("datas", productList);
        } catch (JSONException | NullPointerException | HibernateException e) {
            log.info("" + e);
            logger.info(e.getMessage());
            //e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @Log_decorator
    @RequestMapping(value = "product/{prodUuid}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getProductFrontEnd(HttpSession session, HttpServletResponse response, HttpServletRequest request,
            @PathVariable String prodUuid, @RequestHeader(value = "sub_state", required = false) String subState
    ) {
        log.info(String.format("GET : /shelf/product/v1/product/%s", prodUuid));
        logger.info(String.format("GET : /shelf/product/v1/product/%s", prodUuid));

        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
        try {
            TemplateUtils.setActiveExpireTemplate(subState);
            ProductUtils.setActiveExpireProduct(subState);
            ShelfProductDao shelfProductDao = new ShelfProductDao();
            ShelfProduct shelfProduct = shelfProductDao.getShelfProductByUUID(subState, prodUuid);
            Integer pause = StatusUtils.getPause(subState).getStatusCode();
            Integer active = StatusUtils.getActive(subState).getStatusCode();
            Integer wait2pause = StatusUtils.getWaittoPause(subState).getStatusCode();
            Integer wait2Terminate = StatusUtils.getWaittoTerminate(subState).getStatusCode();
            if (null != shelfProduct) {
                ShelfProductVcs vcsPause = new ShelfProductVcs();
                boolean isPause = false;
                boolean isActive = false;
                for (ShelfProductVcs vcs : shelfProduct.getShelfProductVcsList()) {
                    if ((vcs.getCompUuid() == null && active.equals(vcs.getStatus()))
                            || (vcs.getCompUuid() == null && wait2pause.equals(vcs.getStatus()))
                            || (vcs.getCompUuid() == null && wait2Terminate.equals(vcs.getStatus()))) {
                        vcsPause = vcs;
                        isActive = true;
                    } else if (vcs.getCompUuid() == null && pause.equals(vcs.getStatus())) {
                        vcsPause = vcs;
                        isPause = true;
                    }
                }
                if (isActive) {
                    JSONObject resp = ProductUtils.getProductFrontEnd(subState, shelfProduct);
                    if (resp.has("status") && resp.getInt("status") == 500) {
                        returnVal.put("status", 404)
                                .put("description", resp.getString("description"));
                    } else {
                        returnVal.put("data", resp);
                    }
                } else if (isPause) {
                    Integer statusActive = StatusUtils.getActive(subState).getStatusCode();
                    JSONObject data = new JSONObject()
                            .put("template", new JSONObject())
                            .put("product", new JSONObject())
                            .put("theme", new JSONObject());
                    if (null != vcsPause.getUuid()) {
                        JSONObject jtemplate = new JSONObject();
                        ShelfTmpDao tmpDao = new ShelfTmpDao();
                        ShelfTmp tmp = tmpDao.getShelfTmp(subState, vcsPause.getTemUuid());
                        if (null != tmp) {
                            jtemplate.put("uuid", tmp.getUuid());
                            jtemplate.put("name", tmp.getTmpName());
                            JSONArray jarray = new JSONArray();
                            for (ShelfTmpVcs tv : tmp.getShelfTmpVcsList()) {
                                if (tv.getVersion() == vcsPause.getVerTem()) {
                                    jarray = new JSONArray(tv.getAttr1());
                                }
                            }
                            List<JSONObject> listTmpComp = new ArrayList<>();
                            for (int i = 0; i < jarray.length(); i++) {
                                listTmpComp.add(jarray.getJSONObject(i));
                            }
                            Utils.sortJSONObjectByKey(listTmpComp, "seqNo", true);
                            jtemplate.put("component", jarray);
                        }
                        data.put("template", jtemplate);
                        Gson gson = new Gson();
                        JSONObject json = new JSONObject(gson.toJson(null != vcsPause.getThemeUuid() ? vcsPause.getThemeUuid().getValue() : new JSONObject()));
                        data.put("theme", json.has("info") ? json.getJSONObject("info") : json);

                        JSONObject jdtl = new JSONObject();
                        for (ShelfProductDtl dtl : vcsPause.getShelfProductDtlList()) {
                            if (dtl.getStatus() == statusActive) {
                                jdtl.put(dtl.getLkCode(), ValidUtils.null2NoData(dtl.getLkValue()));
                            }
                        }
                        jdtl.put("uuid", vcsPause.getProdUuid().getUuid());
                        jdtl.put("prodId", vcsPause.getProdUuid().getUuid());
                        jdtl.put("prodVer", vcsPause.getVerProd());
                        jdtl.put("trnId", getUUID());
                        jdtl.put("prodCode", vcsPause.getProdUuid().getProdCode());
                        jdtl.put("prodName", vcsPause.getProdUuid().getProdName());
//                    jdtl.put("productProcessor", null != vcs.getProdUuid() ? vcs.getProdUuid().getAttr1() : "");
//                    jdtl.put("productController", null != vcs.getProdUuid() ? vcs.getProdUuid().getAttr2() : "");
                        jdtl.put("businessLine", ValidUtils.null2NoData(vcsPause.getProdUuid().getBusinessLine()));
                        jdtl.put("businessDept", ValidUtils.null2NoData(vcsPause.getProdUuid().getBusinessDept()));
                        jdtl.put("company", ValidUtils.null2NoData(vcsPause.getProdUuid().getCompany()));
                        data.put("product", jdtl);
                    }
                    returnVal.put("status", 500)
                            .put("data", data)
                            .put("description", StatusUtils.getErrorMessageByCode(subState, "SHELF0032"));
                } else {
                    returnVal.put("status", 404)
                            .put("description", StatusUtils.getErrorMessageByCode(subState, "SHELF0001"));
                }
            } else {
                returnVal.put("status", 404)
                        .put("description", StatusUtils.getErrorMessageByCode(subState, "SHELF0001"));
            }
        } catch (JSONException | NullPointerException | HibernateException | SQLException | ParseException e) {
            log.info("" + e);
            logger.info(e.getMessage());
            //e.printStackTrace();
            returnVal.put("status", 404)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @Log_decorator
    @RequestMapping(value = "list", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getProductList(HttpSession session, HttpServletResponse response,
            HttpServletRequest request,
            @RequestHeader(value = "sub_state", required = false) String subState
    ) {
        log.info("GET : /shelf/product/v1/list");
        logger.info("GET : /shelf/product/v1/list");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONObject());
        try {
//            CompletableFuture.runAsync(() -> {
            ProductUtils.setActiveExpireProduct(subState);
//            });
//            CompletableFuture.runAsync(() -> {
            TemplateUtils.setActiveExpireTemplate(subState);
//            });
            List<JSONObject> productList = new ArrayList<>();
            ShelfProductDao shelfProductDao = new ShelfProductDao();
            List<ShelfProduct> shelfProductList = shelfProductDao.getListShelfProduct(subState);
            for (ShelfProduct shelfProd : shelfProductList) {
                JSONObject product = ProductUtils.getProductDataByProduct2(subState, shelfProd.getShelfProductVcsList());
                productList.add(product);
            }
            returnVal.put("datas", productList);
        } catch (JSONException | NullPointerException | HibernateException | SQLException | ParseException e) {
            log.info("" + e);
            logger.info(e.getMessage());
            //e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @Log_decorator
    @RequestMapping(value = "order", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> getProductNumber(HttpSession session, HttpServletResponse response,
            HttpServletRequest request,
            @RequestBody String reqBody,
            @RequestHeader(value = "sub_state", required = false) String subState
    ) {
        log.info("POST : /shelf/product/v1/order");
        logger.info("POST : /shelf/product/v1/order");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", "");
        JSONObject datas = new JSONObject(reqBody);
        String username = "";
        try {
            if (datas.has("userInfo")) {
                JSONObject objUser = datas.getJSONObject("userInfo");
                username = (objUser.has("username") ? objUser.getString("username") : "");
            }
            if (datas.has("data")) {
                Integer statusActive = StatusUtils.getActive(subState).getStatusCode();
                Integer wait2pause = StatusUtils.getWaittoPause(subState).getStatusCode();
                Integer wait2Terminate = StatusUtils.getWaittoTerminate(subState).getStatusCode();
                JSONObject objData = datas.getJSONObject("data");
                FunctionDao dao = new FunctionDao();
                Date sysdate = new Date();
                List<ShelfProduct> list = new ShelfProductDao().getShelfProductByUuidCode(subState, objData.has("uuid") ? objData.getString("uuid") : "", objData.has("prodCode") ? objData.getString("prodCode") : "");
                ShelfProduct product = null;
                if (null != list && list.size() > 0) {
                    for (ShelfProduct prod : list) {
                        List<ShelfProductVcs> listVcs = prod.getShelfProductVcsList();
                        for (ShelfProductVcs vcs : listVcs) {
                            if (vcs.getCompUuid() == null && (statusActive.equals(vcs.getStatus()) || (vcs.getStatus() == wait2pause)
                                    || (vcs.getStatus() == wait2Terminate && StatusUtils.isWaitTerminate(vcs.getState(), statusActive)))) {
                                product = vcs.getProdUuid();
                                break;
                            }
                        }
                    }
                }
                if (product != null) {
                    returnVal = dao.getProductNumber(subState, sysdate, product.getUuid(), product.getProdCode(), username);
                } else {
                    returnVal.put("description", "Data not found.");
                }
            }
        } catch (JSONException | NullPointerException | HibernateException e) {
            log.info("" + e);
            logger.info(e.getMessage());
            //e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    /*
    @Log_decorator
    @RequestMapping(value = "updatedata", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> updateProduct(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String reqBody) {
        log.info("POST : /shelf/product/v1/updatedata");
        logger.info("POST : /shelf/product/v1/updatedata");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", "");
        JSONObject datas = new JSONObject(reqBody);
        try {
            if (datas.has("data")) {
                JSONObject objData = datas.getJSONObject("data");
                String prodCode = objData.getString("prodCode");
                String compName = objData.getString("compName");
                String lkCode = objData.getString("lkCode");
                String lkValue = objData.getString("lkValue");
                ShelfProductDao dao = new ShelfProductDao();
                returnVal = dao.updateProduct(subState, prodCode, compName, lkCode, lkValue);
            }
        } catch (JSONException | NullPointerException e) {
            log.info("" + e);
            logger.info(e.getMessage());
            //e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);

        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }
     */
    @Log_decorator
    @RequestMapping(value = "list/status/{status}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getProductFrontEnd(HttpSession session, HttpServletResponse response,
            HttpServletRequest request,
            @PathVariable int status,
            @RequestHeader(value = "sub_state", required = false) String subState
    ) {
        log.info(String.format("GET : /shelf/product/v1/list/status/%s", status));
        logger.info(String.format("GET : /shelf/product/v1/list/status/%s", status));
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONObject());
        try {
            JSONArray array = new JSONArray();
            Integer[] status2 = {status};
            ShelfProductVcsDao vcsDao = new ShelfProductVcsDao();
            List<ShelfProductVcs> shelfProductList = vcsDao.getListShelfProductVcsListByStatus(subState, status2);
            Integer statusActive = StatusUtils.getActive(subState).getStatusCode();
            for (ShelfProductVcs vcs : shelfProductList) {
                JSONObject prod = new JSONObject();
                prod.put("uuid", vcs.getProdUuid().getUuid());
                for (ShelfProductDtl prodDtl : vcs.getShelfProductDtlList()) {
                    prod.put(prodDtl.getLkCode(), ValidUtils.null2NoData(prodDtl.getLkValue()));
                }
                ShelfTmp shelfTmp = new ShelfTmpDao().getShelfTmp(subState, vcs.getTemUuid());
                StatusUtils.Status st = StatusUtils.getStatusByCode(subState, String.valueOf(vcs.getStatus()));
                prod.put("status", vcs.getStatus());
                prod.put("statusName", st.getStatusNameTh() == null ? "" : st.getStatusNameTh());
                prod.put("statusNameEn", st.getStatusNameEn() == null ? "" : st.getStatusNameEn());
                prod.put("template", shelfTmp.getUuid() == null ? "" : shelfTmp.getUuid());
                prod.put("templateName", shelfTmp.getTmpName() == null ? "" : shelfTmp.getTmpName());
                prod.put("businessLine", ValidUtils.null2NoData(vcs.getProdUuid().getBusinessLine()));
                prod.put("businessDept", ValidUtils.null2NoData(vcs.getProdUuid().getBusinessDept()));
                prod.put("company", ValidUtils.null2NoData(vcs.getProdUuid().getCompany()));
                prod.put("verProd", (vcs.getVerProd() == 0 ? "" : ValidUtils.obj2Int(vcs.getVerProd())));
                prod.put("prodCode", vcs.getProdUuid().getProdCode());
                prod.put("prodName", vcs.getProdUuid().getProdName());
                prod.put("defProduct", ValidUtils.null2NoData(vcs.getProdUuid().getAttr3()));
                if (prod.has("activeDate") && statusActive.equals(status)) {
                    Date activeDate = ValidUtils.str2Date(ValidUtils.null2NoData(prod.get("activeDate")), "yyyy-MM-dd");
                    Date sysdate = ValidUtils.str2Date(DateUtils.getDisplayEnDate(new Date(), "yyyy-MM-dd"), "yyyy-MM-dd");
                    if (sysdate.compareTo(activeDate) >= 0) {
                        array.put(new JSONObject().put("product", prod));
                    }
                } else {
                    array.put(new JSONObject().put("product", prod));
                }
            }
            returnVal.put("datas", array);

        } catch (JSONException | NullPointerException | ParseException | HibernateException e) {
            log.info("" + e);
            logger.info(e.getMessage());
            //e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @Log_decorator
    @RequestMapping(value = "product/vcs", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> getProductVcsFrontEnd(HttpSession session, HttpServletResponse response,
            HttpServletRequest request,
            @RequestBody String reqBody,
            @RequestHeader(value = "sub_state", required = false) String subState
    ) {
        log.info("POST : /shelf/product/v1/product/vcs");
        logger.info("POST : /shelf/product/v1/product/vcs");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
        try {
            JSONObject datas = new JSONObject(reqBody);
            if (datas.has("userInfo")) {
                JSONObject objUser = datas.getJSONObject("userInfo");
            }
            String productId = "", compUuid = "";
            Integer compVer = null;
            if (datas.has("data")) {
                JSONObject objData = datas.getJSONObject("data");
                productId = objData.getString("prodUuid");
                compUuid = objData.getString("compUuid");
//                prodVer = ValidUtils.obj2Integer(objData.get("prodVer"));
                compVer = ValidUtils.obj2Integer(objData.get("compVer"));
            }
            ShelfProductDao shelfProductDao = new ShelfProductDao();
            ShelfProduct shelfProduct = shelfProductDao.getShelfProductByUUID(subState, productId);
            returnVal.put("data", ProductUtils.getProductVcsFrontEnd(subState, shelfProduct, compUuid, compVer));
        } catch (JSONException | NullPointerException | HibernateException e) {
            log.info("" + e);
            logger.info(e.getMessage());
            //e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    /*
    @Log_decorator
    @RequestMapping(value = "updatedataTmp", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> updateALLData(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String reqBody) {
        log.info("POST : /shelf/product/v1/updatedataTmp");
        logger.info("POST : /shelf/product/v1/updatedataTmp");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", "");
        JSONObject datas = new JSONObject(reqBody);
        try {
            if (datas.has("data")) {
                JSONObject objData = datas.getJSONObject("data");
                ShelfProductDao dao = new ShelfProductDao();
                returnVal = dao.updateAllData(subState, objData);
            }
        } catch (JSONException | NullPointerException e) {
            log.info("" + e);
            logger.info(e.getMessage());
            //e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);

        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }
     */
    @Log_decorator
    @RequestMapping(value = "topup/{prodCode}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getProductCodeFrontEnd(HttpSession session, HttpServletResponse response, HttpServletRequest request,
            @PathVariable String prodCode, @RequestHeader(value = "sub_state", required = false) String subState
    ) {
        log.info(String.format("GET : /shelf/product/v1/topup/%s", prodCode));
        logger.info(String.format("GET : /shelf/product/v1/topup/%s", prodCode));
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
        try {
            boolean isActive = false;
            boolean isPause = false;
            TemplateUtils.setActiveExpireTemplate(subState);
            ProductUtils.setActiveExpireProduct(subState);
            List<ShelfProduct> list = new ShelfProductDao().getShelfProductByUuidCode(subState, null, prodCode);
            Integer statusActive = StatusUtils.getActive(subState).getStatusCode();
            Integer wait2pause = StatusUtils.getWaittoPause(subState).getStatusCode();
            Integer wait2Terminate = StatusUtils.getWaittoTerminate(subState).getStatusCode();
            Integer pause = StatusUtils.getPause(subState).getStatusCode();
            ShelfProduct shelfProduct = null;
            ShelfProductVcs vcsPause = new ShelfProductVcs();
            if (null != list && list.size() > 0) {
                for (ShelfProduct prod : list) {
                    List<ShelfProductVcs> listVcs = prod.getShelfProductVcsList();
                    for (ShelfProductVcs vcs : listVcs) {
                        if (vcs.getCompUuid() == null && (statusActive.equals(vcs.getStatus()) || wait2pause.equals(vcs.getStatus()) || wait2Terminate.equals(vcs.getStatus()))) {
                            shelfProduct = vcs.getProdUuid();
                            isActive = true;
                        } else if (vcs.getCompUuid() == null && pause.equals(vcs.getStatus())) {
                            vcsPause = vcs;
                            isPause = true;
                        }
                    }
                }
            }
            if (isActive) {
                JSONObject resp = ProductUtils.getProductFrontEnd(subState, shelfProduct);
                if (resp.has("status") && resp.getInt("status") == 500) {
                    returnVal.put("status", 404)
                            .put("description", resp.getString("description"));
                } else {
                    returnVal.put("data", resp);
                }
            } else if (isPause && !isActive) {
                JSONObject data = new JSONObject()
                        .put("template", new JSONObject())
                        .put("product", new JSONObject())
                        .put("theme", new JSONObject());
                if (null != vcsPause.getUuid()) {
                    JSONObject jtemplate = new JSONObject();
                    ShelfTmpDao tmpDao = new ShelfTmpDao();
                    ShelfTmp tmp = tmpDao.getShelfTmp(subState, vcsPause.getTemUuid());
                    if (null != tmp) {
                        jtemplate.put("uuid", tmp.getUuid());
                        jtemplate.put("name", tmp.getTmpName());
                        JSONArray jarray = new JSONArray();
                        for (ShelfTmpVcs tv : tmp.getShelfTmpVcsList()) {
                            if (tv.getVersion() == vcsPause.getVerTem()) {
                                jarray = new JSONArray(tv.getAttr1());
                            }
                        }
                        List<JSONObject> listTmpComp = new ArrayList<>();
                        for (int i = 0; i < jarray.length(); i++) {
                            listTmpComp.add(jarray.getJSONObject(i));
                        }
                        Utils.sortJSONObjectByKey(listTmpComp, "seqNo", true);
                        jtemplate.put("component", jarray);
                    }
                    data.put("template", jtemplate);
                    Gson gson = new Gson();
                    JSONObject json = new JSONObject(gson.toJson(null != vcsPause.getThemeUuid() ? vcsPause.getThemeUuid().getValue() : new JSONObject()));
                    data.put("theme", json.has("info") ? json.getJSONObject("info") : json);

                    JSONObject jdtl = new JSONObject();
                    for (ShelfProductDtl dtl : vcsPause.getShelfProductDtlList()) {
                        if (dtl.getStatus() == statusActive) {
                            jdtl.put(dtl.getLkCode(), ValidUtils.null2NoData(dtl.getLkValue()));
                        }
                    }
                    jdtl.put("uuid", vcsPause.getProdUuid().getUuid());
                    jdtl.put("prodId", vcsPause.getProdUuid().getUuid());
                    jdtl.put("prodVer", vcsPause.getVerProd());
                    jdtl.put("trnId", getUUID());
                    jdtl.put("prodCode", vcsPause.getProdUuid().getProdCode());
                    jdtl.put("prodName", vcsPause.getProdUuid().getProdName());
//                    jdtl.put("productProcessor", null != vcs.getProdUuid() ? vcs.getProdUuid().getAttr1() : "");
//                    jdtl.put("productController", null != vcs.getProdUuid() ? vcs.getProdUuid().getAttr2() : "");
                    jdtl.put("businessLine", ValidUtils.null2NoData(vcsPause.getProdUuid().getBusinessLine()));
                    jdtl.put("businessDept", ValidUtils.null2NoData(vcsPause.getProdUuid().getBusinessDept()));
                    jdtl.put("company", ValidUtils.null2NoData(vcsPause.getProdUuid().getCompany()));
                    data.put("product", jdtl);
                }
                returnVal.put("status", 500)
                        .put("data", data)
                        .put("description", StatusUtils.getErrorMessageByCode(subState, "SHELF0032"));
            } else {
                returnVal.put("status", 404)
                        .put("description", StatusUtils.getErrorMessageByCode(subState, "SHELF0001"));
            }
        } catch (JSONException | NullPointerException | HibernateException | SQLException | ParseException e) {
            log.info("" + e);
            logger.info(e.getMessage());
            //e.printStackTrace();
            returnVal.put("status", 404)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @Log_decorator
//    @RequestMapping(value = ("file/upload"), headers = ("content-type=multipart/*"), method = RequestMethod.POST)
    @RequestMapping(value = ("file/upload"), method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> uploadFile(HttpServletResponse response, HttpServletRequest request, HttpSession session, @RequestHeader(value = "sub_state", required = false) String subState, @RequestBody String reqBody) {
        log.info("POST : /shelf/product/v1/file/upload");
        logger.info("POST : /shelf/product/v1/file/upload");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
        try {
//            String detail = request.getParameter("detail");
            JSONObject detailObj = new JSONObject(reqBody);
            if (detailObj.has("uuid")) {
//                InputStream is = fileUpload.getInputStream();
//                byte[] bytes = IOUtils.toByteArray(is);
//                String encoded = Base64.getEncoder().encodeToString(bytes);
                ShelfProductDtlDao prodDtlDao = new ShelfProductDtlDao();
                ShelfProductDtl prodDtl = prodDtlDao.getShelfProductDtlByUUID(subState, detailObj.getString("uuid"));
                if (detailObj.has("type")) {
                    JSONArray lkVal = new JSONArray(prodDtl.getLkValue());
                    for (int i = 0; i < lkVal.length(); i++) {
                        JSONObject dtlObj = lkVal.getJSONObject(i);
                        if (detailObj.getString("type").equalsIgnoreCase(dtlObj.getString("type"))) {
                            dtlObj.put("value", detailObj.getString("fileUpload"));
                            dtlObj.put("fileName", detailObj.getString("fileName"));
                            prodDtl.setLkValue(lkVal.toString());
                            break;
                        }
                    }
                } else if (detailObj.has("attr1")) {
                    JSONArray lkVal = new JSONArray(prodDtl.getLkValue());
                    for (int i = 0; i < lkVal.length(); i++) {
                        JSONObject dtlObj = lkVal.getJSONObject(i);
                        dtlObj.put("value", detailObj.getString("fileUpload"));
                        dtlObj.put("fileName", detailObj.getString("fileName"));
                        prodDtl.setLkValue(lkVal.toString());
                    }
                }
                prodDtlDao.updateShelfProductDtl(subState, prodDtl);
            } else {
                returnVal.put("description", StatusUtils.getErrorMessageByCode(subState, "SHELF0001"));
                return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.BAD_REQUEST));
            }
        } catch (JSONException | NullPointerException | HibernateException e) {
            log.info("" + e);
            logger.info(e.getMessage());
            returnVal.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
            returnVal.put("description", e.getMessage());
            return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.INTERNAL_SERVER_ERROR));
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    /*
    @RequestMapping(value = "file/download", method = RequestMethod.POST)
    public ResponseEntity<InputStreamResource> downloadPDFFile()
            throws IOException {
        File file = ResourceUtils.getFile("classpath:pdf.txt");
        String content = new String(Files.readAllBytes(file.toPath()));
        byte[] data = Base64.getDecoder().decode(content);
        InputStream stream = new ByteArrayInputStream(data);
        String fileName = "test.pdf";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("Content-Disposition", "attachment;filename=" + fileName);
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(stream.available())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new InputStreamResource(stream));
    }
     */
    @Log_decorator
    @RequestMapping(value = "file/download", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> downloadPDFFile(HttpServletRequest request, @RequestHeader(value = "sub_state", required = false) String subState, @RequestBody String reqBody) {
        log.info("POST : /shelf/product/v1/file/download");
        logger.info("POST : /shelf/product/v1/file/download");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
//        String detail = request.getParameter("detail");
        JSONObject detailObj = new JSONObject(reqBody);
        if (detailObj.has("uuid")) {
//                InputStream is = fileUpload.getInputStream();
//                byte[] bytes = IOUtils.toByteArray(is);
//                String encoded = Base64.getEncoder().encodeToString(bytes);
            ShelfProductDtlDao prodDtlDao = new ShelfProductDtlDao();
            try {
                ShelfProductDtl prodDtl = prodDtlDao.getShelfProductDtlByUUID(subState, detailObj.getString("uuid"));
//            if (detailObj.has("type")) {
                JSONArray lkVal = new JSONArray(prodDtl.getLkValue());
                detailObj.put("value", lkVal);
            } catch (JSONException | NullPointerException | HibernateException e) {
                returnVal.put("status", 500)
                        .put("description", "" + e);
                return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.INTERNAL_SERVER_ERROR));
            }
//                for (int i = 0; i < lkVal.length(); i++) {
//                    JSONObject dtlObj = lkVal.getJSONObject(i);
//                    if (detailObj.getString("type").equalsIgnoreCase(dtlObj.getString("type"))) {
//                        dtlObj.put("value", dtlObj.getString("fileUpload"));
//                        dtlObj.put("fileName", dtlObj.getString("fileName"));
//                        detailObj.put("value", dtlObj);
////                        prodDtl.setLkValue(lkVal.toString());
//                        break;
//                    }
//                }
//            } else if (detailObj.has("attr1")) {
//                JSONArray lkVal = new JSONArray(prodDtl.getLkValue());
//                for (int i = 0; i < lkVal.length(); i++) {
//                    JSONObject dtlObj = lkVal.getJSONObject(i);
//                    dtlObj.put("value", dtlObj.getString("value"));
//                    dtlObj.put("fileName", dtlObj.getString("fileName"));
//                    detailObj.put("value", dtlObj);
////                    prodDtl.setLkValue(lkVal.toString());
//                }
//            }
            returnVal.put("data", detailObj);
        } else {
            returnVal.put("description", StatusUtils.getErrorMessageByCode(subState, "SHELF0001"));
            return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.BAD_REQUEST));
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    /*
    @Log_decorator
    @RequestMapping(value = "listdataTmp", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> listALLData(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String reqBody) {
        log.info("POST : /shelf/product/v1/listdataTmp");
        logger.info("POST : /shelf/product/v1/listdataTmp");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", "");
        JSONObject datas = new JSONObject(reqBody);
        try {
            if (datas.has("data")) {
                JSONObject objData = datas.getJSONObject("data");
                ShelfProductDao dao = new ShelfProductDao();
                returnVal = dao.getAllData(subState, objData);
            }
        } catch (JSONException | NullPointerException e) {
            log.info("" + e);
            logger.info(e.getMessage());
            //e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);

        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }
     */
    @Log_decorator
    @RequestMapping(value = "search", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> searchProduct(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String reqBody, @RequestHeader(value = "sub_state", required = false) String subState) {
        log.info("POST : /shelf/product/v1/search");
        logger.info("POST : /shelf/product/v1/search");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
        try {
            JSONObject json = new JSONObject(reqBody);
            String templateName = json.has("templateName") ? json.getString("templateName") : null;
            String productName = json.has("productName") ? json.getString("productName") : null;
//            Integer status = json.has("status") ? ValidUtils.str2BigInteger(ValidUtils.obj2String(json.get("status"))) : null;
            JSONArray status = json.has("status") ? json.getJSONArray("status") : new JSONArray();
            String startActiveDate = json.has("startActiveDate") ? json.getString("startActiveDate") : null;
            String endActiveDate = json.has("endActiveDate") ? json.getString("endActiveDate") : null;
            String startUpdateDate = json.has("startUpdateDate") ? json.getString("startUpdateDate") : null;
            String endUpdateDate = json.has("endUpdateDate") ? json.getString("endUpdateDate") : null;
            String updateBy = json.has("updateBy") ? json.getString("updateBy") : null;
            returnVal.put("data", new ShelfProductDao().searchProduct(subState, templateName, productName, status, startActiveDate, endActiveDate, startUpdateDate, endUpdateDate, updateBy));
        } catch (JSONException | NullPointerException | HibernateException | SQLException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
            //e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @Log_decorator
    @RequestMapping(value = "checkProductCode", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> checkProductCode(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String reqBody, @RequestHeader(value = "sub_state", required = false) String subState) {
        logger.info("POST : /shelf/product/v1/checkProductCode");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
        try {
            JSONObject json = new JSONObject(reqBody);
            String productCode = json.has("productCode") ? json.getString("productCode") : "";
            if (!productCode.trim().isEmpty()) {
                JSONArray templateVcs = new ShelfProductDao().listProductUse(subState, productCode);
                if (templateVcs.length() == 0) {
                    returnVal.put("data", new JSONObject().put("status", true).put("description", StatusUtils.getErrorMessageByCode(subState, "SHELF0034")));
                } else {
                    returnVal.put("data", new JSONObject().put("status", false).put("description", StatusUtils.getErrorMessageByCode(subState, "SHELF0035")));
                }
            } else {
                returnVal.put("status", 400)
                        .put("description", StatusUtils.getErrorMessageByCode(subState, "SHELF0036"));
            }
        } catch (JSONException | NullPointerException | HibernateException | SQLException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @Log_decorator
    @RequestMapping(value = "list/use", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getProductListUse(HttpSession session, HttpServletResponse response,
            HttpServletRequest request,
            @RequestHeader(value = "sub_state", required = false) String subState
    ) {
        log.info("GET : /shelf/product/v1/list/use");
        logger.info("GET : /shelf/product/v1/list/use");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONObject());
        try {
            ShelfProductDao shelfProductDao = new ShelfProductDao();
            JSONArray shelfProductList = shelfProductDao.listProductUse(subState, null);
            returnVal.put("datas", shelfProductList);
        } catch (JSONException | NullPointerException | HibernateException | SQLException e) {
            log.info("" + e);
            logger.info(e.getMessage());
            //e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @Log_decorator
    @RequestMapping(value = "listStatus", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> getProductListStatus(HttpSession session, HttpServletResponse response,
            HttpServletRequest request,
            @RequestHeader(value = "sub_state", required = false) String subState,
            @RequestBody String reqBody
    ) {
        log.info("POST : /shelf/product/v1/listStatus");
        logger.info("POST : /shelf/product/v1/listStatus");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONObject());
        try {
            JSONArray array = new JSONArray();
            JSONArray reqStatus = new JSONArray(reqBody);
            Integer[] status = new Integer[reqStatus.length()];
            for (int i = 0; i < reqStatus.length(); i++) {
                status[i] = reqStatus.getInt(i);
            }
            ShelfProductVcsDao vcsDao = new ShelfProductVcsDao();
            List<ShelfProductVcs> shelfProductList = vcsDao.getListShelfProductVcsListByStatus(subState, status);
//            shelfProductList.sort(Comparator.comparing(ShelfProductVcs::getUpdateAt));    //17/09/2020 แก้ผิด service ต้องเป็น product/v1/search
            Integer statusActive = StatusUtils.getActive(subState).getStatusCode();
            for (int idx = shelfProductList.size() - 1; idx >= 0; idx--) {
                ShelfProductVcs vcs = shelfProductList.get(idx);
                JSONObject prod = new JSONObject();
                prod.put("uuid", vcs.getProdUuid().getUuid());
                for (ShelfProductDtl prodDtl : vcs.getShelfProductDtlList()) {
                    prod.put(prodDtl.getLkCode(), ValidUtils.null2NoData(prodDtl.getLkValue()));
                }
                ShelfTmp shelfTmp = new ShelfTmpDao().getShelfTmp(subState, vcs.getTemUuid());
                StatusUtils.Status st = StatusUtils.getStatusByCode(subState, String.valueOf(vcs.getStatus()));
                prod.put("status", vcs.getStatus());
                prod.put("statusName", st.getStatusNameTh() == null ? "" : st.getStatusNameTh());
                prod.put("statusNameEn", st.getStatusNameEn() == null ? "" : st.getStatusNameEn());
                prod.put("template", shelfTmp.getUuid() == null ? "" : shelfTmp.getUuid());
                prod.put("templateName", shelfTmp.getTmpName() == null ? "" : shelfTmp.getTmpName());
                prod.put("businessLine", ValidUtils.null2NoData(vcs.getProdUuid().getBusinessLine()));
                prod.put("businessDept", ValidUtils.null2NoData(vcs.getProdUuid().getBusinessDept()));
                prod.put("company", ValidUtils.null2NoData(vcs.getProdUuid().getCompany()));
                prod.put("verProd", (vcs.getVerProd() == 0 ? "" : ValidUtils.obj2Int(vcs.getVerProd())));
                prod.put("prodCode", vcs.getProdUuid().getProdCode());
                prod.put("prodName", vcs.getProdUuid().getProdName());
                prod.put("defProduct", ValidUtils.null2NoData(vcs.getProdUuid().getAttr3()));
                if (prod.has("activeDate") && statusActive.equals(status)) {
                    Date activeDate = ValidUtils.str2Date(ValidUtils.null2NoData(prod.get("activeDate")), "yyyy-MM-dd");
                    Date sysdate = ValidUtils.str2Date(DateUtils.getDisplayEnDate(new Date(), "yyyy-MM-dd"), "yyyy-MM-dd");
                    if (sysdate.compareTo(activeDate) >= 0) {
                        array.put(new JSONObject().put("product", prod));
                    }
                } else {
                    array.put(new JSONObject().put("product", prod));
                }
                prod.put("createDate", ValidUtils.null2Separator(DateUtils.getDisplayEnDate(vcs.getCreateAt(), "yyyy-MM-dd"), DateUtils.getDisplayEnDate(vcs.getUpdateAt(), "yyyy-MM-dd")));
                prod.put("createBy", ValidUtils.null2Separator(vcs.getCreateBy(), vcs.getUpdateBy()));
                prod.put("updateDate", ValidUtils.null2Separator(DateUtils.getDisplayEnDate(vcs.getUpdateAt(), "yyyy-MM-dd"), DateUtils.getDisplayEnDate(vcs.getCreateAt(), "yyyy-MM-dd")));
                prod.put("updateBy", ValidUtils.null2Separator(vcs.getUpdateBy(), vcs.getCreateBy()));
            }
            returnVal.put("datas", array);

        } catch (JSONException | NullPointerException | ParseException | HibernateException e) {
            log.info("" + e);
            logger.info(e.getMessage());
            //e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @Log_decorator
    @RequestMapping(value = "inprogress", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> getProductsInprogress(HttpSession session, HttpServletResponse response,
            HttpServletRequest request,
            @RequestBody String reqBody,
            @RequestHeader(value = "sub_state", required = false) String subState
    ) {
        logger.info(String.format("GET : /shelf/product/v1/inprogress"));
        log.info(String.format("GET : /shelf/product/v1/inprogress"));
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
        try {
            JSONObject datas = new JSONObject(reqBody);
            if (datas.has("uuid")) {
                Integer statusInp = StatusUtils.getInprogress(subState).getStatusCode();
                Integer[] inprogress = {statusInp};
                ShelfProductVcsDao vcsDao = new ShelfProductVcsDao();
                List<ShelfProductVcs> vcsWaitAppList = vcsDao.getListShelfProductVcsListByStatus(subState, datas.getString("uuid"), inprogress);
                if (!vcsWaitAppList.isEmpty()) {
                    returnVal.put("status", 500).put("description", StatusUtils.getErrorMessageByCode(subState, "SHELF0027"));
                }
            } else {
                returnVal.put("status", 500).put("description", StatusUtils.getErrorMessageByCode(subState, "SHELF0028"));
            }
        } catch (JSONException | NullPointerException | HibernateException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @Log_decorator
    @RequestMapping(value = "listall", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getProductsList(HttpSession session, HttpServletResponse response,
            HttpServletRequest request,
            @RequestHeader(value = "sub_state", required = false) String subState
    ) {
        log.info("GET : /shelf/product/v1/listall");
        logger.info("GET : /shelf/product/v1/listall");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONObject());
        try {
            TemplateUtils.setActiveExpireTemplate(subState);
            ProductUtils.setActiveExpireProduct(subState);
            List<JSONObject> productList = new ArrayList<>();
            String dbEnv = subState;
            Integer sDelete = StatusUtils.getDelete(dbEnv).getStatusCode();
            Integer sCancel = StatusUtils.getCancel(dbEnv).getStatusCode();
            Integer[] status = {sDelete, sCancel};
            ShelfProductVcsDao shelfProductDao = new ShelfProductVcsDao();
            List<ShelfProductVcs> shelfProductVcsList = shelfProductDao.getListShelfProductVcsListByNotStatus(subState, status);
            for (ShelfProductVcs shelfProdVcs : shelfProductVcsList) {
                JSONObject product = ProductUtils.getProductDataByProductAllVersion(subState, shelfProdVcs);
                productList.add(product);
            }
            returnVal.put("datas", productList);
        } catch (JSONException | NullPointerException | HibernateException | SQLException | ParseException e) {
            log.info("" + e);
            logger.info(e.getMessage());
            //e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @Log_decorator
    @RequestMapping(value = "compare", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> getProductsByVersion(HttpSession session, HttpServletResponse response,
            HttpServletRequest request,
            @RequestBody String reqBody,
            @RequestHeader(value = "sub_state", required = false) String subState
    ) {
        logger.info(String.format("GET : /shelf/product/v1/compare"));
        log.info(String.format("GET : /shelf/product/v1/compare"));
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
        try {
            JSONObject datas = new JSONObject(reqBody);
            if (datas.has("data") && datas.getJSONObject("data").has("uuid") && datas.getJSONObject("data").has("prevVer") && datas.getJSONObject("data").has("currVer")) {
                ShelfProductDao dao = new ShelfProductDao();
                HashMap prevHmp = dao.getProductComponentByVersion(subState, datas.getJSONObject("data").getString("uuid"), ValidUtils.obj2Int(datas.getJSONObject("data").get("prevVer")));
                HashMap currHmp = dao.getProductComponentByVersion(subState, datas.getJSONObject("data").getString("uuid"), ValidUtils.obj2Int(datas.getJSONObject("data").get("currVer")));
                Set<String> prevKName = prevHmp.keySet();
                JSONArray array = new JSONArray();
                JSONArray array2 = new JSONArray();
                for (String k2 : prevKName) {
                    JSONObject prevObj = (JSONObject) prevHmp.get(k2);
                    if (null != currHmp.get(k2)) {
                        JSONObject currObj = (JSONObject) currHmp.get(k2);
                        Iterator<?> preKName = prevObj.keys();
                        JSONArray arrTmp = new JSONArray();
                        while (preKName.hasNext()) {
                            String kName = (String) preKName.next();
                            JSONObject pdata = prevObj.getJSONObject(kName);
                            if (pdata != null && currObj.has(kName)) {
                                JSONObject cdata = currObj.getJSONObject(kName);
                                if (pdata.has("lkCode") && pdata.has("lkValue") && pdata.has("attr1")
                                        && cdata.has("lkCode") && cdata.has("lkValue") && cdata.has("attr1")
                                        && (!pdata.getString("lkCode").equalsIgnoreCase(cdata.getString("lkCode"))
                                        || !pdata.getString("lkValue").equalsIgnoreCase(cdata.getString("lkValue"))
                                        || !pdata.getString("attr1").equalsIgnoreCase(cdata.getString("attr1")))) {
                                    JSONObject tmp = new JSONObject();
                                    tmp.put("lkCode", pdata.getString("lkCode"))
                                            .put("plkValue", pdata.getString("lkValue"))
                                            .put("plkAttr1", pdata.getString("attr1"))
                                            .put("clkValue", cdata.getString("lkValue"))
                                            .put("clkAttr1", cdata.getString("attr1"));
                                    arrTmp.put(tmp);
                                }
                                currObj.remove(kName);
                            } else if (pdata != null && !currObj.has(kName)) {
                                JSONObject tmp = new JSONObject();
                                tmp.put("lkCode", pdata.getString("lkCode"))
                                        .put("plkValue", pdata.getString("lkValue"))
                                        .put("plkAttr1", pdata.getString("attr1"))
                                        .put("clkValue", "")
                                        .put("clkAttr1", "");
                                arrTmp.put(tmp);
                            }
                        }
                        if (currObj.length() > 0) {
                            Iterator<?> curKName = currObj.keys();
                            while (curKName.hasNext()) {
                                String kName = (String) curKName.next();
                                JSONObject cdata = currObj.getJSONObject(kName);
                                JSONObject tmp = new JSONObject();
                                tmp.put("lkCode", cdata.getString("lkCode"))
                                        .put("plkValue", "")
                                        .put("plkAttr1", "")
                                        .put("clkValue", cdata.getString("lkValue"))
                                        .put("clkAttr1", cdata.getString("attr1"));
                                arrTmp.put(tmp);
                            }
                            currObj = new JSONObject();
                        }
                        if (arrTmp.length() > 0) {
                            array.put(new JSONObject().put(k2, arrTmp));
                            array2.put(new JSONObject().put("compCode", k2).put("currComp", "Y").put("prevComp", "Y").put("diff", "Y"));
                        }
                        currHmp.remove(k2);
                    } else {    //มี prev ไม่มี curr
                        JSONArray arrTmp = new JSONArray();
                        Iterator<?> preKName = prevObj.keys();
                        while (preKName.hasNext()) {
                            String kName = (String) preKName.next();
                            JSONObject cdata = prevObj.getJSONObject(kName);
                            JSONObject tmp = new JSONObject();
                            tmp.put("lkCode", cdata.getString("lkCode"))
                                    .put("plkValue", cdata.getString("lkValue"))
                                    .put("plkAttr1", cdata.getString("attr1"))
                                    .put("clkValue", "")
                                    .put("clkAttr1", "");
                            arrTmp.put(tmp);
                            cdata.remove(kName);
                            if (cdata.length() == 0) {
                                prevObj.remove(kName);
                            }
                        }
                        if (arrTmp.length() > 0) {
                            array.put(new JSONObject().put(k2, arrTmp));
                            array2.put(new JSONObject().put("compCode", k2).put("currComp", "N").put("prevComp", "Y").put("diff", "Y"));
                        }
                    }
                }
                Set<String> currKName = currHmp.keySet();
                for (String k2 : currKName) {
                    JSONArray arrTmp = new JSONArray();
                    JSONObject currObj = (JSONObject) currHmp.get(k2);
                    Iterator<?> curKName = currObj.keys();
                    while (curKName.hasNext()) {
                        String kName = (String) curKName.next();
                        JSONObject cdata = currObj.getJSONObject(kName);
                        JSONObject tmp = new JSONObject();
                        tmp.put("lkCode", cdata.getString("lkCode"))
                                .put("plkValue", "")
                                .put("plkAttr1", "")
                                .put("clkValue", cdata.getString("lkValue"))
                                .put("clkAttr1", cdata.getString("attr1"));
                        arrTmp.put(tmp);
                        cdata.remove(kName);
                        if (cdata.length() == 0) {
                            currObj.remove(kName);
                        }
                    }
                    if (arrTmp.length() > 0) {
                        array.put(new JSONObject().put(k2, arrTmp));
                        array2.put(new JSONObject().put("compCode", k2).put("currComp", "Y").put("prevComp", "N").put("diff", "Y"));
                    }
                }
                returnVal.put("data", new JSONObject().put("list", array).put("component", array2));
            } else {
                returnVal.put("status", 500).put("description", StatusUtils.getErrorMessageByCode(subState, "SHELF0037"));
            }
        } catch (JSONException | NullPointerException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", ValidUtils.null2NoData(e.getMessage()));

        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @Log_decorator
    @RequestMapping(value = "api/check", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> checkProduct(HttpSession session, HttpServletResponse response,
            HttpServletRequest request,
            @RequestBody String reqBody,
            @RequestHeader(value = "sub_state", required = false) String subState
    ) {
        logger.info(String.format("GET : /shelf/product/v1/api/check"));
        log.info(String.format("GET : /shelf/product/v1/api/check"));
        JSONObject returnVal = new JSONObject().put("status", 200).put("code", "").put("description", "").put("data", new JSONObject());
        try {
            String username = "", businessDept = "", businessLine = "";
            JSONObject datas = new JSONObject(reqBody);
            if (datas.has("userInfo")) {
                JSONObject objUser = datas.getJSONObject("userInfo");
                username = (objUser.has("username") ? objUser.getString("username") : "");
                businessDept = (objUser.has("businessDept") ? objUser.getString("businessDept") : "");
                businessLine = (objUser.has("businessLine") ? objUser.getString("businessLine") : "");
            }
            if (datas.has("data")) {
                JSONObject objData = datas.getJSONObject("data");
                JSONObject jsonProd = objData.getJSONObject("product");
                String uuid = jsonProd.getString("uuid");
                Integer verProd = ValidUtils.obj2Int(jsonProd.get("verProd"));
                ShelfProductVcsDao vcsDao = new ShelfProductVcsDao();
                String dbEnv = subState;
                Integer statusWaitApp = StatusUtils.getWaittoApprove(dbEnv).getStatusCode();
                Integer statusWaitApp2 = StatusUtils.getWaittoApprove2(dbEnv).getStatusCode();
                Integer[] waStatus = {statusWaitApp, statusWaitApp2};
                List<ShelfProductVcs> vcsWaitList = vcsDao.getListShelfProductVcsListByStatus(dbEnv, uuid, verProd, waStatus);
                if (!vcsWaitList.isEmpty()) {
                    for (ShelfProductVcs vcs : vcsWaitList) {
                        if (null == vcs.getCompUuid()) {
                            for (ShelfProductDtl dtl : vcs.getShelfProductDtlList()) {
                                if ("activeDate".equalsIgnoreCase(dtl.getLkCode())) {
                                    jsonProd.put("activeDate", dtl.getLkValue());

                                } else if ("endDate".equalsIgnoreCase(dtl.getLkCode())) {
                                    jsonProd.put("endDate", dtl.getLkValue());

                                } else if (jsonProd.has("activeDate") && jsonProd.has("endDate")) {
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    JSONObject confirmField = ProductUtils.confirmProduct(datas, dbEnv);
                    String confirmMsg = "";
                    if (!confirmField.getBoolean("status")) {
                        confirmMsg = confirmField.getString("description");
                    }
                    int nextVer = new ShelfProductVcsDao().getMaxVersionProduct(dbEnv, uuid);
                    Integer[] waStatus2 = {statusWaitApp};
                    Integer[] waStatus3 = {statusWaitApp2};
                    List<ShelfProductVcs> listWait = vcsDao.getListShelfProductVcsListByStatus(dbEnv, uuid, verProd, waStatus2);
                    List<ShelfProductVcs> listWait2 = vcsDao.getListShelfProductVcsListByStatus(dbEnv, uuid, verProd, waStatus3);
                    if (!listWait.isEmpty() && (nextVer - 1) == 0) {
                        returnVal.put("status", 500)
                                .put("code", "001")
                                .put("description", confirmMsg);
                    } else if ((!listWait.isEmpty() && nextVer > 1)
                            || (!listWait2.isEmpty() && (nextVer - 1) == 0)) {
                        boolean isTermCond = false;
                        for (ShelfProductVcs vcs : listWait) {
                            if (null != vcs.getCompUuid() && "004".equalsIgnoreCase(vcs.getCompUuid().getCompCode())) {
                                isTermCond = true;
                            }
                        }
                        if (!isTermCond) {
                            for (ShelfProductVcs vcs : listWait2) {
                                if (null != vcs.getCompUuid() && "004".equalsIgnoreCase(vcs.getCompUuid().getCompCode())) {
                                    isTermCond = true;
                                }
                            }
                        }
                        if (!isTermCond) {
                            returnVal.put("status", 500)
                                    .put("code", "001")
                                    .put("description", "");
                        } else {
                            JSONObject data = new JSONObject()
                                    .put("prodUuid", "")
                                    .put("prodCode", "")
                                    .put("verTermcond", "")
                                    .put("headerText", "")
                                    .put("content", "")
                                    .put("activeDate", "")
                                    .put("verProd", "");
                            int index = 0;
                            for (ShelfProductVcs vcs : vcsWaitList) {
                                if (null == vcs.getCompUuid()) {
                                    data.put("prodUuid", uuid)
                                            .put("prodCode", vcs.getProdUuid().getProdCode());
                                    index++;
                                } else if (null != vcs.getCompUuid() && "004".equalsIgnoreCase(vcs.getCompUuid().getCompCode())) {
                                    for (ShelfProductDtl dtl : vcs.getShelfProductDtlList()) {
                                        if ("version".equalsIgnoreCase(dtl.getLkCode())) {
                                            data.put("verTermcond", dtl.getLkValue());
                                        } else if ("headerText".equalsIgnoreCase(dtl.getLkCode())) {
                                            data.put("headerText", dtl.getLkValue());
                                        } else if ("termsNCondition".equalsIgnoreCase(dtl.getLkCode())) {
                                            data.put("content", dtl.getLkValue());
                                        } else if ("activeDate".equalsIgnoreCase(dtl.getLkCode())) {
                                            data.put("activeDate", dtl.getLkValue());
                                        }
                                    }
                                    index++;
                                } else if (index == 2) {
                                    break;
                                }
                            }
                            returnVal.put("status", 200)
                                    .put("description", confirmMsg)
                                    .put("data", data.put("verProd", nextVer));
                        }
                    } else {
                        returnVal.put("status", 500)
                                .put("code", "002")
                                .put("description", StatusUtils.getErrorMessageByCode(subState, "SHELF0038"));
                    }
                } else {
                    returnVal.put("status", 500)
                            .put("code", "002")
                            .put("description", StatusUtils.getErrorMessageByCode(subState, "SHELF0038"));
                }
            } else {
                returnVal.put("status", 500)
                        .put("code", "002")
                        .put("description", StatusUtils.getErrorMessageByCode(subState, "SHELF0039"));
            }
        } catch (JSONException | NullPointerException | HibernateException e) {
            logger.info(e.getMessage());
            returnVal.put("status", 500)
                    .put("code", "002")
                    .put("description", "" + e);
        }

        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }
    
    @Log_decorator
    @RequestMapping(value = "infobycode/{prodCode}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getProductInfoByprodCode(HttpSession session, HttpServletResponse response,
            HttpServletRequest request,
            @PathVariable String prodCode,
            @RequestHeader(value = "sub_state", required = false) String subState
    ) throws SQLException, ParseException {//statusCode ==> lookup_value
        log.info(String.format("GET : /shelf/product/v1/infobycode/%s", prodCode));
        logger.info(String.format("GET : /shelf/product/v1/infobycode/%s", prodCode));
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        List<ShelfProduct> prodList = new ArrayList<>();
        ShelfProductDao prodDao = new ShelfProductDao();
        ShelfProduct prod = new ShelfProduct();
        try {
            prodList = prodDao.getProductByCode(subState, prodCode);
            String prodUuid = null;
            int status = 213;
            if (prodList != null || prodList.size() > 0) {
                for (ShelfProduct data : prodList) {
                    prod.setUuid(ValidUtils.null2NoData(data.getUuid()));
                    prodUuid = ValidUtils.null2NoData(data.getUuid());
                }
                System.out.println("prodUuid  : "+prodUuid);
                returnVal.put("datas", ProductUtils.infoProductsByUuidStatus(subState, prodUuid, status));
            }
            
        } catch (JSONException | NullPointerException | HibernateException | ParseException e) {
            log.info("" + e);
            logger.info(e.getMessage());
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }
}
