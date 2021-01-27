/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import th.co.d1.digitallending.dao.FunctionDao;
import th.co.d1.digitallending.dao.ShelfCompDao;
import th.co.d1.digitallending.dao.ShelfProductDao;
import th.co.d1.digitallending.dao.ShelfProductVcsDao;
import th.co.d1.digitallending.dao.ShelfTmpDao;
import th.co.d1.digitallending.entity.ShelfComp;
import th.co.d1.digitallending.entity.ShelfCompDtl;
import th.co.d1.digitallending.entity.ShelfProduct;
import th.co.d1.digitallending.entity.ShelfProductDtl;
import th.co.d1.digitallending.entity.ShelfProductVcs;
import th.co.d1.digitallending.entity.ShelfTmp;
import th.co.d1.digitallending.util.ProductUtils;
import th.co.d1.digitallending.util.StatusUtils;
import th.co.d1.digitallending.util.ValidUtils;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import com.tfglog.*;
import java.text.ParseException;
import org.springframework.context.annotation.ComponentScan;
import static th.co.d1.digitallending.util.ApplicationStartup.headersJSON;
import th.co.d1.digitallending.util.Utils;

/**
 *
 * @author Chalermpol Yaowachai
 *
 * DataOne Asia (Thailand) Company Limited 1023 MS Siam Tower, 30th Floor, Rama
 * 3 Rd., Chongnonsi, Yannawa, Bangkok, 10120
 *
 * @create 10-01-2020 11:06:29 AM
 */
@ComponentScan("com.tfglog.*")
@Controller
@RequestMapping("/shelf/product/v1")
public class ProductV1Controller {

    final static Logger logger = Logger.getLogger(ProductV1Controller.class);
    final static TfgLogger log = LogSingleton.getTfgLogger();

    @Log_decorator
    @RequestMapping(value = "component", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> getComponentByCode(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String compUuidList) {
        log.info("GET : /shelf/product/v1/component");
        logger.info("GET : /shelf/product/v1/component");
        JSONObject retData = new JSONObject();
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", retData);
        try {
            JSONArray compUuid = new JSONArray(compUuidList);
            JSONArray retComp = new JSONArray();
            for (int i = 0; i < compUuid.length(); i++) {
                ShelfCompDao shelfCompDao = new ShelfCompDao();
                ShelfComp shelfComp = shelfCompDao.getShelfCompByUUID(Utils.validateSubStateFromHeader(request), compUuid.getString(i));
                List<ShelfCompDtl> shelfCompDtlList = shelfComp.getShelfCompDtlList();
                shelfCompDtlList.sort(Comparator.comparing(ShelfCompDtl::getSeq));
                JSONObject ret = ProductUtils.getInitialProductComponentByUUID(Utils.validateSubStateFromHeader(request), shelfComp, shelfCompDtlList, true, null);
                retComp.put(ret);
            }
            JSONObject prod = ProductUtils.getInitialProduct();
            retData.put("product", prod);
            retData.put("component", retComp);
            returnVal.put("data", retData);
        } catch (JSONException | NullPointerException e) {
            log.error("" + e);
            logger.error("" + e);
            e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @Log_decorator
    @RequestMapping(value = "save", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<?> postProduct(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String payload) {
        log.info("POST : /shelf/product/v1/save");
        logger.info("POST : /shelf/product/v1/save");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
        try {
            Date sysdate = new Date();
            JSONObject datas = new JSONObject(payload);
            String username = "", businessDept = "", businessLine = "";
            if (datas.has("userInfo")) {
                JSONObject objUser = datas.getJSONObject("userInfo");
                username = (objUser.has("username") ? objUser.getString("username") : "");
                businessDept = (objUser.has("businessDept") ? objUser.getString("businessDept") : "");
                businessLine = (objUser.has("businessLine") ? objUser.getString("businessLine") : "");
            }
            ShelfProduct prod = ProductUtils.getProduct(Utils.validateSubStateFromHeader(request), datas, sysdate, username);
            if (null != prod) {
                prod.setBusinessDept(ValidUtils.null2Separator(prod.getBusinessDept(), businessDept));
                prod.setBusinessLine(ValidUtils.null2Separator(prod.getBusinessLine(), businessLine));
                boolean save = true;
                if (null != prod.getUuid() && !"".equals(prod.getUuid())) {
                    ShelfProductVcsDao vcsDao = new ShelfProductVcsDao();
                    Integer statusInprogress = StatusUtils.getInprogress(Utils.validateSubStateFromHeader(request)).getStatusCode();
                    Integer[] inProgress = {statusInprogress};
                    List<ShelfProductVcs> vcsInProgressList = vcsDao.getListShelfProductVcsListByStatus(Utils.validateSubStateFromHeader(request), prod.getUuid(), inProgress);
                    if (vcsInProgressList.size() > 0) {
                        returnVal.put("status", 500)
                                .put("errcode", "ERR001")
                                .put("description", "");
                        save = false;
                    }
                }
                if (save) {
                    prod = new ShelfProductDao().updateShelfProduct(Utils.validateSubStateFromHeader(request), prod);
                    /*prod = new ShelfProductDao().getShelfProductByUUID(Utils.validateSubStateFromHeader(request), prod.getUuid());
                    JSONObject retData = ProductUtils.getProductDataByProduct(Utils.validateSubStateFromHeader(request), prod.getShelfProductVcsList());
                    returnVal.put("data", retData);*/
                }
            } else {
                returnVal.put("status", 500)
                        .put("description", "Data not't found.");

            }
        } catch (JSONException | NullPointerException | HibernateException e) {
            log.error("" + e);
            logger.error("" + e);
            e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @Log_decorator
    @RequestMapping(value = "save", method = RequestMethod.PUT, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<?> putProduct(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String payload) {
        log.info("PUT : /shelf/product/v1/save");
        logger.info("PUT : /shelf/product/v1/save");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
        try {
            Date sysdate = new Date();
            JSONObject datas = new JSONObject(payload);
            String username = "", businessDept = "", businessLine = "";
            if (datas.has("userInfo")) {
                JSONObject objUser = datas.getJSONObject("userInfo");
                username = (objUser.has("username") ? objUser.getString("username") : "");
                businessDept = (objUser.has("businessDept") ? objUser.getString("businessDept") : "");
                businessLine = (objUser.has("businessLine") ? objUser.getString("businessLine") : "");
            }
            if (datas.has("data")) {
                String dbEnv = Utils.validateSubStateFromHeader(request);
                JSONObject objData = datas.getJSONObject("data");
                JSONObject jsonProd = objData.getJSONObject("product");
                if (jsonProd.has("action") && !"save".equalsIgnoreCase(jsonProd.getString("action"))) {
                    String uuid = jsonProd.getString("uuid");
                    String remark = jsonProd.getString("remark");
                    String respCode = jsonProd.getString("respCode");       //เก็บเหมือน state
                    Integer verProd = ValidUtils.obj2Int(jsonProd.get("verProd"));
                    ShelfProductVcsDao vcsDao = new ShelfProductVcsDao();
                    if ("sendapprove".equalsIgnoreCase(jsonProd.getString("action"))) {     //by version
                        Integer statusWaitApp = StatusUtils.getWaittoApprove(dbEnv).getStatusCode();
                        Integer[] waitApp = {statusWaitApp};
                        List<ShelfProductVcs> vcsWaitAppList = vcsDao.getListShelfProductVcsListByStatus(dbEnv, uuid, waitApp);
                        if (vcsWaitAppList.size() > 0) {
                            returnVal.put("status", 500)
                                    .put("errcode", "G006")
                                    .put("description", "ไม่สามารถส่งรายการเพื่อขออนุมัติได้");
                        } else {
                            boolean confirm = jsonProd.getBoolean("confirm");
                            boolean flagCon = false;
                            JSONArray errArr = new JSONArray();
                            if (!confirm) {
                                Integer statusInactive = StatusUtils.getInActive(dbEnv).getStatusCode();
                                Integer[] inActive = {statusInactive};
                                List<ShelfProductVcs> vcsInActiveList = vcsDao.getListShelfProductVcsListByStatus(dbEnv, uuid, inActive);

                                Integer statusActive = StatusUtils.getActive(dbEnv).getStatusCode();
                                Integer[] active = {statusActive};
                                List<ShelfProductVcs> vcsActiveList = vcsDao.getListShelfProductVcsListByStatus(dbEnv, uuid, active);

                                Integer statusInprogress = StatusUtils.getInprogress(dbEnv).getStatusCode();
                                Integer[] inprogress = {statusInprogress};
                                List<ShelfProductVcs> vcsInprogressList = vcsDao.getListShelfProductVcsListByStatus(dbEnv, uuid, inprogress);

                                if (vcsInActiveList.size() > 0) {
                                    JSONObject resInAct = new JSONObject().put("errCode", "G004")
                                            .put("description", "วันที่ Active Date ที่ท่านระบุมีรอใช้งานอยู่ หากท่านยืนยันรายการเวิร์ชั่นที่รอการใช้งานจะถูกยกเลิกอัตโนมัติ");
                                    errArr.put(resInAct);
                                    flagCon = true;
                                    JSONObject resp = ProductUtils.compareEndDateNActiveDate(vcsActiveList, vcsInprogressList);
                                    if (resp.get("status").equals(200)) {
                                        if (resp.getJSONObject("data").getBoolean("flagCon")) {
                                            resInAct = resp.getJSONObject("data").getJSONObject("detail");
                                            errArr.put(resInAct);
                                            flagCon = true;
                                        }
                                    }
                                } else {
                                    JSONObject resp = ProductUtils.compareEndDateNActiveDate(vcsActiveList, vcsInprogressList);
                                    if (resp.get("status").equals(200)) {
                                        if (resp.getJSONObject("data").getBoolean("flagCon")) {
                                            JSONObject resInAct = resp.getJSONObject("data").getJSONObject("detail");
                                            errArr.put(resInAct);
                                            flagCon = true;
                                        }
                                    }
                                }
                            }
                            System.out.println("flagCon : " + flagCon);
                            System.out.println("confirm : " + confirm);
                            if (flagCon && !confirm) {
                                returnVal.put("status", 500)
                                        .put("data", errArr)
                                        .put("description", "");
                            } else if (!flagCon) {
                                JSONObject result = ProductUtils.sendToApproveProduct(dbEnv, uuid, statusWaitApp, sysdate, username, respCode);
                                returnVal.put("status", result.get("status"))
                                        .put("description", result.getString("description"));
                            }
                        }
                    } else if ("rejectapprove".equalsIgnoreCase(jsonProd.getString("action"))) {    //by version
                        JSONObject result = ProductUtils.rejectApprove(dbEnv, uuid, verProd, respCode, username, remark);
                        returnVal.put("status", result.get("status"))
                                .put("description", result.getString("description"));
                    } else if ("approve".equalsIgnoreCase(jsonProd.getString("action"))) {
                        boolean confirm = jsonProd.getBoolean("confirm");
                        Integer statusActive = StatusUtils.getActive(dbEnv).getStatusCode();
                        Integer[] active = {statusActive};
                        List<ShelfProductVcs> vcsActiveList = vcsDao.getListShelfProductVcsListByStatus(dbEnv, uuid, active);
                        Integer statusInactive = StatusUtils.getInActive(dbEnv).getStatusCode();
                        Integer[] inActive = {statusInactive};
                        List<ShelfProductVcs> vcsInActiveList = vcsDao.getListShelfProductVcsListByStatus(dbEnv, uuid, inActive);
                        Integer statusWaiApp = StatusUtils.getWaittoApprove(dbEnv).getStatusCode();
                        Integer[] waitapprove = {statusWaiApp};
                        List<ShelfProductVcs> vcsWaitApproveList = vcsDao.getListShelfProductVcsListByStatus(dbEnv, uuid, waitapprove);
                        int newversion = new ShelfProductVcsDao().getMaxVersionProduct(dbEnv, uuid);
                        Integer statusCancel = StatusUtils.getCancel(dbEnv).getStatusCode();
                        if (vcsInActiveList.size() > 0) {
                            if (confirm) {
                                List<ShelfProductVcs> list = new ArrayList<>();
                                ShelfProduct prod = null;
                                for (ShelfProductVcs vcs : vcsInActiveList) {
                                    vcs.setStatus(statusCancel);
                                    vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(statusCancel)));
                                    vcs.setUpdateBy(username);
                                    if (null == vcs.getCompUuid()) {
                                        prod = vcs.getProdUuid();
                                        prod.setStatus(vcs.getStatus());    //last status of product
                                        prod.setUpdateBy(username);
                                    }
                                    list.add(vcs);
                                }
                                for (ShelfProductVcs vcs : vcsWaitApproveList) {
                                    vcs.setStatus(statusInactive);
                                    vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(statusInactive)));
                                    vcs.setVerProd(newversion);
                                    vcs.setVerComp(newversion);
                                    vcs.setUpdateBy(username);
                                    if (null == vcs.getCompUuid()) {
                                        prod = vcs.getProdUuid();
                                        prod.setStatus(vcs.getStatus());    //last status of product
                                        prod.setUpdateBy(username);
                                    }
                                    list.add(vcs);
                                }
                                JSONObject result = ProductUtils.approveProduct(dbEnv, uuid, prod, list);    //  set inactive to cancel set current vertion to inactive
                                returnVal.put("status", result.get("status"))
                                        .put("description", result.getString("description"));
                            } else {
                                JSONArray errArr = new JSONArray();
                                JSONObject resInAct = new JSONObject().put("errCode", "G004")
                                        .put("description", "วันที่ Active Date ที่ท่านระบุมีรอใช้งานอยู่ หากท่านยืนยันรายการเวิร์ชั่นที่รอการใช้งานจะถูกยกเลิกอัตโนมัติ");
                                errArr.put(resInAct);
                                JSONObject resp = ProductUtils.compareEndDateNActiveDate(vcsActiveList, vcsWaitApproveList);
                                if (resp.get("status").equals(200)) {
                                    if (resp.getJSONObject("data").getBoolean("flagCon")) {
                                        resInAct = resp.getJSONObject("data").getJSONObject("detail");
                                        errArr.put(resInAct);
                                        returnVal.put("status", 500)
                                                .put("data", errArr)
                                                .put("description", "");
                                    }
                                }
                            }
                        } else {
                            if (vcsActiveList.size() > 0) {
                                if (confirm) {
                                    ShelfProduct prod = null;
                                    for (ShelfProductVcs vcs : vcsWaitApproveList) {
                                        vcs.setStatus(statusInactive);
                                        vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(statusInactive)));
                                        vcs.setVerProd(newversion);
                                        vcs.setVerComp(newversion);
                                        vcs.setUpdateBy(username);
                                        if (null == vcs.getCompUuid()) {
                                            prod = vcs.getProdUuid();
                                            prod.setStatus(vcs.getStatus());    //last status of product
                                            prod.setUpdateBy(username);
                                        }
                                    }
                                    JSONObject result = ProductUtils.approveProduct(dbEnv, uuid, prod, vcsWaitApproveList);    //set current version to inactive
                                    returnVal.put("status", result.get("status"))
                                            .put("description", result.getString("description"));
                                } else {
                                    JSONObject resp = ProductUtils.compareEndDateNActiveDate(vcsActiveList, vcsWaitApproveList);
                                    if (resp.get("status").equals(200)) {
                                        if (resp.getJSONObject("data").getBoolean("flagCon")) {
                                            JSONObject resInAct = resp.getJSONObject("data").getJSONObject("detail");
                                            returnVal.put("status", 500)
                                                    .put("data", new JSONArray().put(resInAct))
                                                    .put("description", "");
                                        }
                                    }
                                }
                            } else {
                                ShelfProduct prod = null;
                                for (ShelfProductVcs vcs : vcsWaitApproveList) {
                                    vcs.setStatus(statusActive);
                                    vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(statusActive)));
                                    vcs.setVerProd(newversion);
                                    vcs.setVerComp(newversion);
                                    vcs.setUpdateBy(username);
                                    if (null == vcs.getCompUuid()) {
                                        prod = vcs.getProdUuid();
                                        prod.setStatus(vcs.getStatus());        //last status of product
                                        prod.setAttr2("Y");
                                        prod.setUpdateBy(username);
                                    }
                                }
                                JSONObject result = ProductUtils.approveProduct(dbEnv, uuid, prod, vcsWaitApproveList);    //set current version to active
                                returnVal.put("status", result.get("status"))
                                        .put("description", result.getString("description"));
                            }
                        }
                    } else if ("pause".equalsIgnoreCase(jsonProd.getString("action"))) {  //by version
                        JSONObject result = ProductUtils.pauseProduct(dbEnv, uuid, verProd, respCode, username, remark);
                        returnVal.put("status", result.get("status"))
                                .put("description", result.getString("description"));
                    } else if ("start".equalsIgnoreCase(jsonProd.getString("action"))) {  //by version
                        JSONObject result = ProductUtils.startProduct(dbEnv, uuid, verProd, respCode, username, remark);
                        returnVal.put("status", result.get("status"))
                                .put("description", result.getString("description"));
                    } else if ("senddelete".equalsIgnoreCase(jsonProd.getString("action"))) { // by product
                        JSONObject result = ProductUtils.sendDeleteProduct(dbEnv, uuid, verProd, respCode, username, remark);
                        returnVal.put("status", result.get("status"))
                                .put("description", result.getString("description"));
                    } else if ("rejectdelete".equalsIgnoreCase(jsonProd.getString("action"))) { //by product
                        JSONObject result = ProductUtils.rejectDelete(dbEnv, uuid, verProd, respCode, username, remark);
                        returnVal.put("status", result.get("status"))
                                .put("description", result.getString("description"));
                    } else if ("delete".equalsIgnoreCase(jsonProd.getString("action"))) { //by product
                        JSONObject result = ProductUtils.deleteProduct(dbEnv, uuid, verProd, respCode, username, remark);
                        returnVal.put("status", result.get("status"))
                                .put("description", result.getString("description"));
                    } else if ("terminate".equalsIgnoreCase(jsonProd.getString("action"))) {  //by product
                        JSONObject result = ProductUtils.terminateProduct(dbEnv, uuid, verProd, respCode, username, remark);
                        returnVal.put("status", result.get("status"))
                                .put("description", result.getString("description"));
                    }
                } else {
                    /*
                    update รายการที่ยังไม่มี version สถานะที่เป็นไปได้คือ inprogress & reject
                    update รายการที่มี version สถานะคือเป็น reject
                     */
                    int prodVer = (jsonProd.has("prodVer") ? ValidUtils.obj2Int(jsonProd.get("prodVer")) : 0);
                    boolean save = false;
                    int verProd = 0, status = 0;
                    if (prodVer == 0) {
                        save = true;
                    } else {
                        String uuid = (jsonProd.has("uuid") ? jsonProd.getString("uuid") : "");
                        status = (jsonProd.has("status") ? ValidUtils.obj2Int(jsonProd.get("status")) : 0);
                        verProd = (jsonProd.has("verProd") ? ValidUtils.obj2Int(jsonProd.get("verProd")) : 0);
                        ShelfProductVcsDao vcsDao = new ShelfProductVcsDao();
                        List<ShelfProductVcs> list = vcsDao.getListShelfProduct(dbEnv, uuid, status, verProd);
                        List<ShelfProductVcs> list2 = vcsDao.getListShelfProduct(dbEnv, uuid, StatusUtils.getInprogress(dbEnv).getStatusCode(), 0);
                        if (list.size() > 0 && (list2 == null || list2.isEmpty())) {
                            save = true;
                        } else {
//                            System.out.println("version : " + verProd + " data is : " + list.size());
//                            System.out.println("version : " + verProd + " data is : " + list.size());
                        }
                    }
                    if (save) {
                        ShelfProduct prod = ProductUtils.getProduct(dbEnv, datas, sysdate, username);
                        if (null != prod) {
                            prod.setBusinessDept(ValidUtils.null2Separator(prod.getBusinessDept(), businessDept));
                            prod.setBusinessLine(ValidUtils.null2Separator(prod.getBusinessLine(), businessLine));
                            new ShelfProductDao().updateShelfProduct(dbEnv, prod);
                            /*prod = new ShelfProductDao().getShelfProductByUUID(dbEnv, prod.getUuid());
                            JSONArray ret = ProductUtils.infoProducts(dbEnv, prod.getUuid(), status, verProd);
                            returnVal.put("data", (ret.length() > 0 && ret.getJSONObject(0).has("product")) ? ret.getJSONObject(0).getJSONObject("product") : new JSONObject());*/
                        }
                    } else {
                        returnVal.put("status", 500)
                                .put("description", "ข้อมูลไม่ถูกต้อง");
                    }
                }
            }
        } catch (JSONException | NullPointerException | HibernateException e) {
            e.printStackTrace();
            log.error("" + e);
            logger.error("" + e);
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @Log_decorator
    @RequestMapping(value = "info/{prodUuid}/{status}/{verProd}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getProductInfo(HttpSession session, HttpServletResponse response, HttpServletRequest request, @PathVariable String prodUuid, @PathVariable int status, @PathVariable int verProd) {//statusCode ==> lookup_value
        log.info(String.format("GET : /shelf/product/v1/info/%s/%d/%d", prodUuid, status, verProd));
        logger.info(String.format("GET : /shelf/product/v1/info/%s/%d/%d", prodUuid, status, verProd));
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            returnVal.put("datas", ProductUtils.infoProducts(Utils.validateSubStateFromHeader(request), prodUuid, status, verProd));
        } catch (JSONException | NullPointerException e) {
            log.error("" + e);
            logger.error("" + e);
            returnVal.put("status", 500)
                    .put("description", "" + e);

        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @Log_decorator
    @RequestMapping(value = "list/{prodUuid}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getProductsInfo(HttpSession session, HttpServletResponse response, HttpServletRequest request, @PathVariable String prodUuid) {
        log.info(String.format("GET : /shelf/product/v1/list/%s", prodUuid));
        logger.info(String.format("GET : /shelf/product/v1/list/%s", prodUuid));
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONObject());
        try {
            ShelfProductDao shelfProductDao = new ShelfProductDao();
            ShelfProduct shelfProduct = shelfProductDao.getShelfProductByUUID(Utils.validateSubStateFromHeader(request), prodUuid);
//            returnVal.put("datas", ProductUtils.groupProduct(shelfProduct));
            JSONArray productList = new JSONArray();
            Object[] data = ProductUtils.orderProductByVersion(shelfProduct.getShelfProductVcsList());
            int statusVal = 0;
            for (int i = 0; i < data.length; i++) {
                JSONObject product = new JSONObject();
                List<ShelfProductVcs> vcsList = (List<ShelfProductVcs>) data[i];
                List<ShelfProductDtl> dtlList = new ArrayList<>();
                String businessLine = "", businessDept = "", company = "", prodCode = "", prodName = "";
                for (ShelfProductVcs vcs : vcsList) {
                    if (vcs.getCompUuid() == null) {
                        dtlList = vcs.getShelfProductDtlList();
                        statusVal = vcs.getStatus();
//                        product.put("productProcessor", null != shelfProduct.getAttr1() ? shelfProduct.getAttr1() : "");
//                        product.put("productController", null != shelfProduct.getAttr2() ? shelfProduct.getAttr2() : "");
                        businessLine = ValidUtils.null2NoData(vcs.getProdUuid().getBusinessLine());
                        businessDept = ValidUtils.null2NoData(vcs.getProdUuid().getBusinessDept());
                        company = ValidUtils.null2NoData(vcs.getProdUuid().getCompany());
                        prodCode = vcs.getProdUuid().getProdCode();
                        prodName = vcs.getProdUuid().getProdName();
                        product.put("template", vcs.getTemUuid());
                        break;
                    }
                }
                for (ShelfProductDtl dtl : dtlList) {
                    product.put(dtl.getLkCode(), dtl.getLkValue());
                }
                ShelfTmp shelfTmp = new ShelfTmpDao().getShelfTmp(Utils.validateSubStateFromHeader(request), product.getString("template"));
                StatusUtils.Status status = StatusUtils.getStatusByCode(Utils.validateSubStateFromHeader(request), String.valueOf(statusVal));
                product.put("status", status.getStatusCode() == null ? "" : status.getStatusCode());
                product.put("statusName", status.getStatusNameTh() == null ? "" : status.getStatusNameTh());
                product.put("statusNameEn", status.getStatusNameEn() == null ? "" : status.getStatusNameEn());
                product.put("template", shelfTmp.getUuid() == null ? "" : shelfTmp.getUuid());
                product.put("templateName", shelfTmp.getTmpName() == null ? "" : shelfTmp.getTmpName());
                product.put("company", company);
                product.put("businessLine", businessLine);
                product.put("businessDept", businessDept);
                product.put("prodCode", prodCode);
                product.put("prodName", prodName);
                productList.put(product);
            }
            returnVal.put("datas", productList);
        } catch (JSONException | NullPointerException e) {
            log.error("" + e);
            logger.error("" + e);
            e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);

        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @Log_decorator
    @RequestMapping(value = "product/{prodUuid}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getProductFrontEnd(HttpSession session, HttpServletResponse response, HttpServletRequest request, @PathVariable String prodUuid) {
        log.info(String.format("GET : /shelf/product/v1/product/%s", prodUuid));
        logger.info(String.format("GET : /shelf/product/v1/product/%s", prodUuid));
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
        try {
            ShelfProductDao shelfProductDao = new ShelfProductDao();
            ShelfProduct shelfProduct = shelfProductDao.getShelfProductByUUID(Utils.validateSubStateFromHeader(request), prodUuid);
            returnVal.put("data", ProductUtils.getProductFrontEnd(Utils.validateSubStateFromHeader(request), shelfProduct));
        } catch (JSONException | NullPointerException e) {
            log.error("" + e);
            logger.error("" + e);
            e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);

        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @Log_decorator
    @RequestMapping(value = "list", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getProductList(HttpSession session, HttpServletResponse response, HttpServletRequest request) {
        log.info("GET : /shelf/product/v1/list");
        logger.info("GET : /shelf/product/v1/list");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONObject());
        try {
            ProductUtils.setActiveExpireProduct(Utils.validateSubStateFromHeader(request));
            List<JSONObject> productList = new ArrayList<>();
            ShelfProductDao shelfProductDao = new ShelfProductDao();
            List<ShelfProduct> shelfProductList = shelfProductDao.getListShelfProduct(Utils.validateSubStateFromHeader(request));
            for (ShelfProduct shelfProd : shelfProductList) {
                JSONObject product = ProductUtils.getProductDataByProduct2(Utils.validateSubStateFromHeader(request), shelfProd.getShelfProductVcsList());
                productList.add(product);
            }
            returnVal.put("datas", productList);
        } catch (JSONException | NullPointerException e) {
            log.error("" + e);
            logger.error("" + e);
            e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);

        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @Log_decorator
    @RequestMapping(value = "order", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> getProductNumber(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String payload) {
        log.info("POST : /shelf/product/v1/order");
        logger.info("POST : /shelf/product/v1/order");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", "");
        JSONObject datas = new JSONObject(payload);
        String username = "";
        try {
            if (datas.has("userInfo")) {
                JSONObject objUser = datas.getJSONObject("userInfo");
                username = (objUser.has("username") ? objUser.getString("username") : "");
            }
            if (datas.has("data")) {
                JSONObject objData = datas.getJSONObject("data");
                FunctionDao dao = new FunctionDao();
                Date sysdate = new Date();
                List<ShelfProduct> list = new ShelfProductDao().getShelfProductByUuidCode(Utils.validateSubStateFromHeader(request), objData.has("uuid") ? objData.getString("uuid") : "", objData.has("prodCode") ? objData.getString("prodCode") : "");
                ShelfProduct product = null;
                if (null != list && list.size() > 0) {
                    for (ShelfProduct prod : list) {
                        List<ShelfProductVcs> listVcs = prod.getShelfProductVcsList();
                        for (ShelfProductVcs vcs : listVcs) {
                            if (vcs.getCompUuid() == null && StatusUtils.getActive(Utils.validateSubStateFromHeader(request)).getStatusCode().equals(vcs.getStatus())) {
                                product = vcs.getProdUuid();
                                break;
                            }
                        }
                    }
                }
                if (product != null) {
                    returnVal = dao.getProductNumber(Utils.validateSubStateFromHeader(request), sysdate, product.getUuid(), product.getProdCode(), username);
                } else {
                    returnVal.put("description", "Data not found.");
                }
            }
        } catch (JSONException | NullPointerException e) {
            log.error("" + e);
            logger.error("" + e);
            e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);

        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    /*
    @Log_decorator
    @RequestMapping(value = "updatedata", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> updateProduct(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String payload) {
        log.info("POST : /shelf/product/v1/updatedata");
        logger.info("POST : /shelf/product/v1/updatedata");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", "");
        JSONObject datas = new JSONObject(payload);
        try {
            if (datas.has("data")) {
                JSONObject objData = datas.getJSONObject("data");
                String prodCode = objData.getString("prodCode");
                String compName = objData.getString("compName");
                String lkCode = objData.getString("lkCode");
                String lkValue = objData.getString("lkValue");
                ShelfProductDao dao = new ShelfProductDao();
                returnVal = dao.updateProduct(Utils.validateSubStateFromHeader(request), prodCode, compName, lkCode, lkValue);
            }
        } catch (JSONException | NullPointerException e) {
            log.error("" + e);
            logger.error("" + e);
            e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);

        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }
     */
    @Log_decorator
    @RequestMapping(value = "list/status/{status}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getProductFrontEnd(HttpSession session, HttpServletResponse response, HttpServletRequest request, @PathVariable int status) {
        log.info(String.format("GET : /shelf/product/v1/list/status/%s", status));
        logger.info(String.format("GET : /shelf/product/v1/list/status/%s", status));
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONObject());
        try {
            Date sysdate = new Date();
            JSONArray array = new JSONArray();
            Integer[] status2 = {status};
            ShelfProductVcsDao vcsDao = new ShelfProductVcsDao();
            List<ShelfProductVcs> shelfProductList = vcsDao.getListShelfProductVcsListByStatus(Utils.validateSubStateFromHeader(request), status2);
            Integer statusActive = StatusUtils.getActive(Utils.validateSubStateFromHeader(request)).getStatusCode();
            for (ShelfProductVcs vcs : shelfProductList) {
                JSONObject prod = new JSONObject();
                prod.put("uuid", vcs.getProdUuid().getUuid());
                for (ShelfProductDtl prodDtl : vcs.getShelfProductDtlList()) {
                    prod.put(prodDtl.getLkCode(), ValidUtils.null2NoData(prodDtl.getLkValue()));
                }
                ShelfTmp shelfTmp = new ShelfTmpDao().getShelfTmp(Utils.validateSubStateFromHeader(request), vcs.getTemUuid());
                StatusUtils.Status st = StatusUtils.getStatusByCode(Utils.validateSubStateFromHeader(request), String.valueOf(vcs.getStatus()));
                prod.put("status", vcs.getStatus());
                prod.put("statusName", st.getStatusNameTh() == null ? "" : st.getStatusNameTh());
                prod.put("statusNameEn", st.getStatusNameEn() == null ? "" : st.getStatusNameEn());
                prod.put("template", shelfTmp.getUuid() == null ? "" : shelfTmp.getUuid());
                prod.put("templateName", shelfTmp.getTmpName() == null ? "" : shelfTmp.getTmpName());
                prod.put("businessLine", ValidUtils.null2NoData(vcs.getProdUuid().getBusinessLine()));
                prod.put("businessDept", ValidUtils.null2NoData(vcs.getProdUuid().getBusinessDept()));
                prod.put("company", ValidUtils.null2NoData(vcs.getProdUuid().getCompany()));
                prod.put("verProd", ValidUtils.obj2Int(vcs.getVerProd()));
                prod.put("prodCode", vcs.getProdUuid().getProdCode());
                prod.put("prodName", vcs.getProdUuid().getProdName());
                prod.put("defProduct", ValidUtils.null2NoData(vcs.getProdUuid().getAttr3()));
                if (prod.has("activeDate") && statusActive.equals(status)) {
                    Date activeDate = ValidUtils.str2Date(ValidUtils.null2NoData(prod.get("activeDate")), "yyyy-MM-dd");
                    if (sysdate.compareTo(activeDate) >= 0) {
                        array.put(new JSONObject().put("product", prod));
                    }
                } else {
                    array.put(new JSONObject().put("product", prod));
                }
            }
            returnVal.put("datas", array);

        } catch (JSONException | NullPointerException | ParseException e) {
            log.error("" + e);
            logger.error("" + e);
            e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);

        }

        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @Log_decorator
    @RequestMapping(value = "product/vcs", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> getProductVcsFrontEnd(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String payload) {
        log.info("POST : /shelf/product/v1/product/vcs");
        logger.info("POST : /shelf/product/v1/product/vcs");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
        try {
            JSONObject datas = new JSONObject(payload);
            if (datas.has("userInfo")) {
                JSONObject objUser = datas.getJSONObject("userInfo");
            }
            String productId = "", compUuid = "";
            Integer prodVer = null, compVer = null;
            if (datas.has("data")) {
                JSONObject objData = datas.getJSONObject("data");
                productId = objData.getString("prodUuid");
                compUuid = objData.getString("compUuid");
                prodVer = ValidUtils.obj2Integer(objData.get("prodVer"));
                compVer = ValidUtils.obj2Integer(objData.get("compVer"));
            }
            ShelfProductDao shelfProductDao = new ShelfProductDao();
            ShelfProduct shelfProduct = shelfProductDao.getShelfProductByUUID(Utils.validateSubStateFromHeader(request), productId);
            returnVal.put("data", ProductUtils.getProductVcsFrontEnd(Utils.validateSubStateFromHeader(request), shelfProduct, compUuid, prodVer, compVer));
        } catch (JSONException | NullPointerException e) {
            log.error("" + e);
            logger.error("" + e);
            e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);

        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    /*
    @Log_decorator
    @RequestMapping(value = "updatedataTmp", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> updateALLData(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String payload) {
        log.info("POST : /shelf/product/v1/updatedataTmp");
        logger.info("POST : /shelf/product/v1/updatedataTmp");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", "");
        JSONObject datas = new JSONObject(payload);
        try {
            if (datas.has("data")) {
                JSONObject objData = datas.getJSONObject("data");
                ShelfProductDao dao = new ShelfProductDao();
                returnVal = dao.updateAllData(Utils.validateSubStateFromHeader(request), objData);
            }
        } catch (JSONException | NullPointerException e) {
            log.error("" + e);
            logger.error("" + e);
            e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);

        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }
     */
    @Log_decorator
    @RequestMapping(value = "topup/{prodCode}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getProductCodeFrontEnd(HttpSession session, HttpServletResponse response, HttpServletRequest request, @PathVariable String prodCode) {
        log.info(String.format("GET : /shelf/product/v1/topup/%s", prodCode));
        logger.info(String.format("GET : /shelf/product/v1/topup/%s", prodCode));
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
        try {
            List<ShelfProduct> list = new ShelfProductDao().getShelfProductByUuidCode(Utils.validateSubStateFromHeader(request), null, prodCode);
            ShelfProduct shelfProduct = null;
            if (null != list && list.size() > 0) {
                for (ShelfProduct prod : list) {
                    List<ShelfProductVcs> listVcs = prod.getShelfProductVcsList();
                    for (ShelfProductVcs vcs : listVcs) {
                        if (vcs.getCompUuid() == null && StatusUtils.getActive(Utils.validateSubStateFromHeader(request)).getStatusCode().equals(vcs.getStatus())) {
                            shelfProduct = vcs.getProdUuid();
                            break;
                        }
                    }
                }
            }
            returnVal.put("data", ProductUtils.getProductFrontEnd(Utils.validateSubStateFromHeader(request), shelfProduct));
        } catch (JSONException | NullPointerException e) {
            log.error("" + e);
            logger.error("" + e);
            e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);

        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    /*
    @Log_decorator
    @RequestMapping(value = "listdataTmp", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> listALLData(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String payload) {
        log.info("POST : /shelf/product/v1/listdataTmp");
        logger.info("POST : /shelf/product/v1/listdataTmp");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", "");
        JSONObject datas = new JSONObject(payload);
        try {
            if (datas.has("data")) {
                JSONObject objData = datas.getJSONObject("data");
                ShelfProductDao dao = new ShelfProductDao();
                returnVal = dao.getAllData(Utils.validateSubStateFromHeader(request), objData);
            }
        } catch (JSONException | NullPointerException e) {
            log.error("" + e);
            logger.error("" + e);
            e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);

        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }
     */
}
