/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.controller;

import com.google.gson.Gson;
import com.tfglog.LogSingleton;
import com.tfglog.Log_decorator;
import com.tfglog.TfgLogger;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import th.co.d1.digitallending.dao.ShelfProductVcsDao;
import th.co.d1.digitallending.dao.ShelfThemeDao;
import th.co.d1.digitallending.dao.SysLookupDao;
import th.co.d1.digitallending.entity.Memlookup;
import th.co.d1.digitallending.entity.ShelfProductVcs;
import th.co.d1.digitallending.entity.ShelfTheme;
import static th.co.d1.digitallending.util.ApplicationStartup.headersJSON;
import th.co.d1.digitallending.util.StatusUtils;
import static th.co.d1.digitallending.util.Utils.getUUID;
import th.co.d1.digitallending.util.ValidUtils;

/**
 *
 * @author Kritsana Sasai
 *
 * DataOne Asia (Thailand) Company Limited 900/29 Rama III Rd. Bangpongpang,
 * Yannawa, Bangkok 10120 Tel :+66 (0) 2682 3000
 *
 * @create 03-01-2020 9:59:32 AM
 */
@Controller
@RequestMapping("/shelf/theme/v1")
public class ThemeV1Controller {

    final static Logger logger = Logger.getLogger(ThemeV1Controller.class.getName());
    TfgLogger log = LogSingleton.getTfgLogger();

    @Log_decorator
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getTheme(HttpSession session, HttpServletResponse response, HttpServletRequest request) {
        logger.info("GET : /shelf/theme/v1");
        log.info("GET : /shelf/theme/v1");
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
    public ResponseEntity<?> getThemes(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestHeader(value = "sub_state", required = false) String subState) {
        logger.info("GET : /shelf/theme/v1/list");
        log.info("GET : /shelf/theme/v1/list");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
        try {
//            returnVal.put("data", ThemeUtils.getThemeList(subState, true));
            returnVal.put("data", new ShelfThemeDao().getListShelfTmpTheme(subState, true, false));
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
    public ResponseEntity<?> postTheme(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String reqBody, @RequestHeader(value = "sub_state", required = false) String subState) {
        logger.info("POST : /shelf/theme/v1/save");
        log.info("POST : /shelf/theme/v1/save");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
        try {
            JSONObject datas = new JSONObject(reqBody);
            if (datas.has("userInfo")) {
                JSONObject objUser = datas.getJSONObject("userInfo");
                String username = objUser.has("username") ? objUser.getString("username") : "";
                if (!"".equals(username)) {
                    JSONObject objDate = datas.getJSONObject("data");
                    JSONObject header = objDate.getJSONObject("header");
                    JSONObject init = objDate.getJSONObject("init");
                    ShelfTheme theme = new ShelfTheme();
                    theme.setUuid(header.getString("themeUuid").isEmpty() ? getUUID() : header.getString("themeUuid"));
                    theme.setThemeCode(init.getString("code"));
                    theme.setThemeName(init.getString("name"));
                    theme.setValue(objDate.toString());
                    theme.setStatus(init.getBoolean("enable") ? Integer.parseInt(new SysLookupDao().getMemLookupByValue(subState, "active").getLookupcode()) : Integer.parseInt(new SysLookupDao().getMemLookupByValue(subState, "inactive").getLookupcode()));
                    theme.setCreateBy(username);
                    JSONObject resp = new ShelfThemeDao().saveShelfTmpTheme(subState, theme);
                    if (resp.getBoolean("status")) {
                        returnVal.put("data", objDate);
                    } else {
                        returnVal.put("status", 500)
                                .put("description", resp.getString("description"));
                    }
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

    @Log_decorator
    @RequestMapping(value = "info/{themeID}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> infoTheme(HttpSession session, HttpServletResponse response,
            HttpServletRequest request,
            @PathVariable String themeID,
            @RequestHeader(value = "sub_state", required = false) String subState
    ) {
        logger.info("GET : /shelf/theme/v1/info/" + themeID);
        log.info("GET : /shelf/theme/v1/info/" + themeID);
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
        try {
            ShelfTheme theme = new ShelfThemeDao().getShelfTmpTheme(subState, themeID);
            if (null != theme.getValue()) {
                Gson gson = new Gson();
                JSONObject json = new JSONObject(gson.toJson(theme.getValue()));
                returnVal.put("data", json);
            } else {
                returnVal.put("data", new JSONObject());
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
    @RequestMapping(value = "save", method = RequestMethod.PUT, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<?> putTheme(HttpSession session, HttpServletResponse response,
            HttpServletRequest request,
            @RequestBody String reqBody,
            @RequestHeader(value = "sub_state", required = false) String subState
    ) {
        logger.info("PUT : /shelf/theme/v1/save");
        log.info("PUT : /shelf/theme/v1/save");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
        try {
            JSONObject datas = new JSONObject(reqBody);
            if (datas.has("userInfo")) {
                JSONObject objUser = datas.getJSONObject("userInfo");
                String username = objUser.has("username") ? objUser.getString("username") : "";
                if (!"".equals(username)) {
                    JSONObject objDate = datas.getJSONObject("data");
                    JSONObject header = objDate.getJSONObject("header");
                    JSONObject init = objDate.getJSONObject("init");
                    String uuid = header.has("themeUuid") ? header.getString("themeUuid") : "";
                    String action = header.has("action") ? header.getString("action") : null;
                    ShelfTheme theme = new ShelfThemeDao().getShelfTmpTheme(subState, uuid);
                    if (null != theme) {
                        if (action != null && action.equalsIgnoreCase("cancel")) {
                            List<ShelfProductVcs> prodVcsList = new ShelfProductVcsDao().getProductByThemeUuid(subState, uuid);
                            if (!prodVcsList.isEmpty()) {
                                JSONArray prods = new JSONArray();
                                for (ShelfProductVcs prodVcs : prodVcsList) {
                                    JSONObject prod = new JSONObject();
                                    prod.put("prodCode", prodVcs.getProdUuid().getProdCode());
                                    prod.put("prodName", prodVcs.getProdUuid().getProdName());
                                    prod.put("prodVersion", (prodVcs.getVerProd() == 0 ? "" : ValidUtils.obj2Int(prodVcs.getVerProd())));
                                    prod.put("status", prodVcs.getStatus());
                                    Memlookup memLookup = new SysLookupDao().getMemLookupByCode(subState, ValidUtils.null2NoData(prodVcs.getStatus()));
                                    prod.put("status", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupcode()) : "");
                                    prod.put("statusNameTh", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupnameth()) : "");
                                    prod.put("statusNameEn", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupnameen()) : "");
                                    prods.put(prod);
                                }
                                returnVal.put("status", 500)
                                        .put("description", StatusUtils.getErrorMessageByCode(subState, "SHELF0067"));
                                return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.BAD_REQUEST));
                            }
                            theme.setStatus(Integer.parseInt(new SysLookupDao().getMemLookupByValue(subState, "cancel").getLookupcode()));
                        } else if (action != null && action.equalsIgnoreCase("active")) {
                            List<ShelfProductVcs> prodVcsList = new ShelfProductVcsDao().getProductByThemeUuid(subState, uuid);
                            if (!prodVcsList.isEmpty()) {
                                JSONArray prods = new JSONArray();
                                for (ShelfProductVcs prodVcs : prodVcsList) {
                                    JSONObject prod = new JSONObject();
                                    prod.put("prodCode", prodVcs.getProdUuid().getProdCode());
                                    prod.put("prodName", prodVcs.getProdUuid().getProdName());
                                    prod.put("prodVersion",(prodVcs.getVerProd() == 0 ? "" : ValidUtils.obj2Int(prodVcs.getVerProd())));
                                    prod.put("status", prodVcs.getStatus());
                                    Memlookup memLookup = new SysLookupDao().getMemLookupByCode(subState, ValidUtils.null2NoData(prodVcs.getStatus()));
                                    prod.put("status", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupcode()) : "");
                                    prod.put("statusNameTh", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupnameth()) : "");
                                    prod.put("statusNameEn", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupnameen()) : "");
                                    prods.put(prod);
                                }
                                returnVal.put("status", 500)
                                        .put("description", StatusUtils.getErrorMessageByCode(subState, "SHELF0067"));
                                return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.BAD_REQUEST));
                            }
                            theme.setStatus(Integer.parseInt(new SysLookupDao().getMemLookupByValue(subState, "active").getLookupcode()));
                        } else if (action != null && action.equalsIgnoreCase("inactive")) {
                            List<ShelfProductVcs> prodVcsList = new ShelfProductVcsDao().getProductByThemeUuid(subState, uuid);
                            if (!prodVcsList.isEmpty()) {
                                JSONArray prods = new JSONArray();
                                for (ShelfProductVcs prodVcs : prodVcsList) {
                                    JSONObject prod = new JSONObject();
                                    prod.put("prodCode", prodVcs.getProdUuid().getProdCode());
                                    prod.put("prodName", prodVcs.getProdUuid().getProdName());
                                    prod.put("prodVersion", (prodVcs.getVerProd() == 0 ? "" : ValidUtils.obj2Int(prodVcs.getVerProd())));
                                    prod.put("status", prodVcs.getStatus());
                                    Memlookup memLookup = new SysLookupDao().getMemLookupByCode(subState, ValidUtils.null2NoData(prodVcs.getStatus()));
                                    prod.put("status", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupcode()) : "");
                                    prod.put("statusNameTh", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupnameth()) : "");
                                    prod.put("statusNameEn", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupnameen()) : "");
                                    prods.put(prod);
                                }
                                returnVal.put("status", 500)
                                        .put("description", StatusUtils.getErrorMessageByCode(subState, "SHELF0030"));
                                return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.BAD_REQUEST));
                            }
                            theme.setStatus(Integer.parseInt(new SysLookupDao().getMemLookupByValue(subState, "inactive").getLookupcode()));
                        } else {
                            theme.setStatus(init.getBoolean("enable") ? Integer.parseInt(new SysLookupDao().getMemLookupByValue(subState, "active").getLookupcode()) : Integer.parseInt(new SysLookupDao().getMemLookupByValue(subState, "inactive").getLookupcode()));
                        }
                        theme.setUuid(uuid);
                        theme.setThemeCode(init.getString("code"));
                        theme.setThemeName(init.getString("name"));
                        theme.setValue(objDate.toString());
                        theme.setUpdateBy(username);
                        JSONObject resp = new ShelfThemeDao().updateShelfTmpTheme(subState, theme);
                        if (resp.getBoolean("status")) {
                            returnVal.put("data", objDate);
                        } else {
                            returnVal.put("status", 500)
                                    .put("description", resp.getString("description"));
                        }
                    } else {
                        returnVal.put("status", 500)
                                .put("description", StatusUtils.getErrorMessageByCode(subState, "SHELF0031"));
                    }
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

    @Log_decorator
    @RequestMapping(value = "search", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> searchThemes(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String reqBody, @RequestHeader(value = "sub_state", required = false) String subState) {
        logger.info("POST : /shelf/theme/v1/search");
        log.info("POST : /shelf/theme/v1/search");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
        try {
            JSONObject json = new JSONObject(reqBody);
            String themeName = json.has("themeName") ? json.getString("themeName") : "";
            Integer status = json.has("status") ? ValidUtils.str2BigInteger(ValidUtils.obj2String(json.get("status"))) : null;
            Date startCreateDate = ValidUtils.str2Date(json.has("startCreateDate") ? (json.getString("startCreateDate").isEmpty() ? null : json.getString("startCreateDate")) : null);
            Date endCreateDate = ValidUtils.str2Date(json.has("endCreateDate") ? (json.getString("endCreateDate").isEmpty() ? null : json.getString("endCreateDate")) : null);
            Date startUpdateDate = ValidUtils.str2Date(json.has("startUpdateDate") ? (json.getString("startUpdateDate").isEmpty() ? null : json.getString("startUpdateDate")) : null);
            Date endUpdateDate = ValidUtils.str2Date(json.has("endUpdateDate") ? (json.getString("endUpdateDate").isEmpty() ? null : json.getString("endUpdateDate")) : null);
            returnVal.put("data", new ShelfThemeDao().searchShelfTheme(subState, themeName, status, startCreateDate, endCreateDate, startUpdateDate, endUpdateDate));
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
    @RequestMapping(value = "checkThemeName", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> checkThemeName(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String reqBody, @RequestHeader(value = "sub_state", required = false) String subState) {
        logger.info("POST : /shelf/theme/v1/checkThemeName");
        log.info("POST : /shelf/theme/v1/checkThemeName");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
        try {
            JSONObject json = new JSONObject(reqBody);
            String themeName = json.has("themeName") ? json.getString("themeName") : "";
            if (!themeName.trim().isEmpty()) {
                List<ShelfTheme> themeList = new ShelfThemeDao().getShelfThemeByNameStatusActiveAndInActive(subState, themeName);
                if (themeList.isEmpty()) {
                    returnVal.put("data", new JSONObject().put("status", true).put("description", "You can use this name."));
                } else {
                    returnVal.put("data", new JSONObject().put("status", false).put("description", "Theme name is used."));
                }
            } else {
                returnVal.put("status", 400)
                        .put("description", StatusUtils.getErrorMessageByCode(subState, "SHELF0068"));
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
    @RequestMapping(value = "listAll", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getThemeListall(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestHeader(value = "sub_state", required = false) String subState) {
        logger.info("GET : /shelf/theme/v1/listAll");
        log.info("GET : /shelf/theme/v1/listAll");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
        try {
            returnVal.put("data", new ShelfThemeDao().getListShelfTmpTheme(subState, false, true));
        } catch (JSONException | NullPointerException | HibernateException e) {
            logger.info(e.getMessage());
            log.error("" + e);
            //e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);

        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }
}
