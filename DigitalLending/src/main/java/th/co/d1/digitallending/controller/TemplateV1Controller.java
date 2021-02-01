package th.co.d1.digitallending.controller;

import com.tfglog.LogSingleton;
import com.tfglog.Log_decorator;
import com.tfglog.TfgLogger;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.logging.Logger;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
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
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import org.springframework.web.bind.annotation.ResponseBody;
import th.co.d1.digitallending.dao.ShelfCompDao;
import th.co.d1.digitallending.dao.ShelfProductVcsDao;
import th.co.d1.digitallending.dao.ShelfTmpAttDao;
import th.co.d1.digitallending.dao.ShelfTmpDao;
import th.co.d1.digitallending.dao.ShelfTmpDetailDao;
import th.co.d1.digitallending.dao.ShelfTmpVcsDao;
import th.co.d1.digitallending.dao.SysLookupDao;
import th.co.d1.digitallending.entity.Memlookup;
import th.co.d1.digitallending.entity.ShelfComp;
import th.co.d1.digitallending.entity.ShelfProductVcs;
import th.co.d1.digitallending.entity.ShelfTmp;
import th.co.d1.digitallending.entity.ShelfTmpAttach;
import th.co.d1.digitallending.entity.ShelfTmpDetail;
import th.co.d1.digitallending.entity.ShelfTmpVcs;
import static th.co.d1.digitallending.util.ApplicationStartup.headersJSON;
import th.co.d1.digitallending.util.HibernateUtil;
import static th.co.d1.digitallending.util.HibernateUtil.getSessionMaster;
import th.co.d1.digitallending.util.ProductUtils;
import th.co.d1.digitallending.util.StatusUtils;
import th.co.d1.digitallending.util.TemplateUtils;
import static th.co.d1.digitallending.util.Utils.*;
import th.co.d1.digitallending.util.ValidUtils;

@Controller
@RequestMapping("/shelf/template/v1")
public class TemplateV1Controller {

//    boolean premission = true;
    Logger logger = Logger.getLogger(TemplateV1Controller.class.getName());
    TfgLogger log = LogSingleton.getTfgLogger();

    /*first create template must get tmpUuid , vscUuid form api*/
    @Log_decorator
    @RequestMapping(value = "component", method = GET)
    @ResponseBody
    public ResponseEntity<?> getComponent(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestHeader(value = "sub_state", required = false) String subState) {
        logger.info("GET : /shelf/template/v1/component");
        log.info("GET : /shelf/template/v1/component");
        JSONObject returnVal = new JSONObject().put("status", HttpStatus.OK.value()).put("description", "").put("data", new JSONObject()).put("confirmmsg", "");
        try {
            returnVal.put("data", new JSONObject()
                    .put("tmpUuid", getUUID())
                    .put("vcsUuid", getUUID())
                    .put("componentList", getComponentList(subState)));

        } catch (JSONException | NullPointerException e) {
            logger.info(e.getMessage());
            log.error("" + e);
            //e.printStackTrace();
            returnVal.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value()).put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @Log_decorator
    @RequestMapping(value = "list", method = GET)
    @ResponseBody
    public ResponseEntity<?> getTemplate(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestHeader(value = "sub_state", required = false) String subState) {
        JSONObject returnVal = new JSONObject().put("status", HttpStatus.OK.value()).put("description", "").put("data", new JSONObject()).put("confirmmsg", "");
        logger.info("GET : /shelf/template/v1/list");
        log.info("GET : /shelf/template/v1/list");
        try {
            returnVal.put("data", new TemplateUtils().getTemplateList(subState));
        } catch (JSONException | NullPointerException e) {
            logger.info(e.getMessage());
            log.error("" + e);
            //e.printStackTrace();
            returnVal.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .put("description", "" + e);

        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @Log_decorator
    @RequestMapping(value = "info/{vcsUuid}", method = GET)
    @ResponseBody
    public ResponseEntity<?> getInfoByUuid(HttpSession session, HttpServletResponse response, HttpServletRequest request, @PathVariable String vcsUuid, @RequestHeader(value = "sub_state", required = false) String subState) {
        logger.info("GET : /shelf/template/v1/info/" + vcsUuid);
        log.info("GET : /shelf/template/v1/info/" + vcsUuid);
        HttpStatus httpStatus = HttpStatus.OK;
        JSONObject returnVal = new JSONObject().put("status", httpStatus.value()).put("description", "").put("data", new JSONObject()).put("confirmmsg", "");
        try {
            ShelfTmpVcs shelfTmpVcs = new ShelfTmpVcsDao().getListByUuid(subState, vcsUuid);
            int intStats = shelfTmpVcs.getStatus();
            JSONObject getStatusValue = getActionFromStatus(subState, intStats);
            Memlookup memLookup = new SysLookupDao().getMemLookupByCode(subState, ValidUtils.null2NoData(shelfTmpVcs.getStatus()));
            JSONObject header = new JSONObject()
                    .put("tmpUuid", shelfTmpVcs.getTmpUuid().getUuid())
                    .put("vcsUuid", shelfTmpVcs.getUuid())
                    .put("name", shelfTmpVcs.getTmpUuid().getTmpName())
                    .put("effectiveDate", th.co.d1.digitallending.util.DateUtils.getDisplayEnDate(shelfTmpVcs.getEffectiveDate(), "yyyy-MM-dd"))
                    .put("enable", true)
                    .put("readOnly", getStatusValue.has("readOnly") ? getStatusValue.getBoolean("readOnly") : "") // check status 
                    .put("action", "")
                    .put("status", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupcode()) : "")
                    .put("statusNameTh", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupnameth()) : "")
                    .put("statusNameEn", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupnameen()) : "")
                    .put("createDate", shelfTmpVcs.getCreateAt())
                    .put("createBy", shelfTmpVcs.getCreateBy())
                    .put("updateDate", (shelfTmpVcs.getUpdateAt() == null ? "" : shelfTmpVcs.getUpdateAt()))
                    .put("updateBy", (shelfTmpVcs.getUpdateBy() == null ? "" : shelfTmpVcs.getUpdateBy())
                    );

            List<ShelfTmpVcs> listTmpVcsTmpUuid = new ShelfTmpVcsDao().getListByTmpUuid(subState, shelfTmpVcs.getTmpUuid().getUuid());
            JSONArray vcsTmpVersion = new JSONArray();
            System.out.println("listTmpVcsTmpUuid : " + listTmpVcsTmpUuid.size());
            for (int i = 0; i < listTmpVcsTmpUuid.size(); i++) {
                ShelfTmpVcs obj = listTmpVcsTmpUuid.get(i);
                Memlookup vcsStatus = new SysLookupDao().getMemLookupByCode(subState, ValidUtils.null2NoData(obj.getStatus()));
                JSONObject eachDetail = new JSONObject()
                        .put("vcsUuid", obj.getUuid())
                        .put("version", obj.getVersion())
                        .put("data", obj.getAttr1())
                        .put("templateName", obj.getTmpUuid().getTmpName())
                        .put("status", null != vcsStatus ? ValidUtils.null2NoData(vcsStatus.getLookupcode()) : "")
                        .put("statusNameTh", null != vcsStatus ? ValidUtils.null2NoData(vcsStatus.getLookupnameth()) : "")
                        .put("statusNameEn", null != vcsStatus ? ValidUtils.null2NoData(vcsStatus.getLookupnameen()) : "")
                        .put("activeDate", th.co.d1.digitallending.util.DateUtils.getDisplayEnDate(obj.getEffectiveDate(), "yyyy-MM-dd"));
                vcsTmpVersion.put(eachDetail);
            }

            JSONArray infoData = new JSONArray();
            for (int i = 0; i < shelfTmpVcs.getShelfTmpDetailList().size(); i++) {
                ShelfTmpDetail detail = shelfTmpVcs.getShelfTmpDetailList().get(i);

                JSONObject eachDetail = new JSONObject().put("seq", detail.getSeqNo())
                        .put("compUuid", detail.getCompUuid().getUuid())
                        .put("detailUuid", detail.getUuid())
                        .put("compCode", detail.getCompUuid().getCompCode())
                        .put("compName", detail.getCompUuid().getCompName())
                        .put("enable", detail.getFlagEnable());

                if (detail.getCompUuid().getCompCode().equalsIgnoreCase("004")) {
                    if (shelfTmpVcs.getAttr1() != null && !shelfTmpVcs.getAttr1().isEmpty()) {
                        JSONArray attrArr = new JSONArray(shelfTmpVcs.getAttr1());
//                        System.out.println("004 : " + shelfTmpVcs.getAttr1());
                        for (int aai = 0; aai < attrArr.length(); aai++) {
                            JSONObject obj = attrArr.getJSONObject(aai);
//                            if (detail.getCompUuid().getUuid().equalsIgnoreCase(obj.getString("compUuid"))) {
                            if (obj.has("termsNCondition")) {
//                                obj.put("version", String.valueOf(shelfTmpVcs.getVersion()));
                                eachDetail.put("data", obj);
                            }
                        }

                    }
                    List<ShelfTmpVcs> list = new ShelfTmpVcsDao().getListByTmpUuidAndTmpVersion(subState, shelfTmpVcs.getTmpUuid().getUuid(), 0, true);
                    JSONArray termCons = new JSONArray();
                    list.forEach((tmpVcs) -> {
                        if (tmpVcs.getAttr1() != null && !tmpVcs.getAttr1().isEmpty()) {
                            JSONArray attrArr = new JSONArray(tmpVcs.getAttr1());
                            for (int aai = 0; aai < attrArr.length(); aai++) {
                                JSONObject obj = attrArr.getJSONObject(aai);
                                if (obj.has("termsNCondition")) {
//                                    obj.put("version", String.valueOf(shelfTmpVcs.getVersion()));
                                    termCons.put(obj);
                                }
                            }
                        }
                    });
                    eachDetail.put("termNConVer", termCons);
                } else if (detail.getCompUuid().getCompCode().equalsIgnoreCase("006")) {
                    if (shelfTmpVcs.getAttr1() != null && !shelfTmpVcs.getAttr1().isEmpty()) {
                        JSONArray attrArr = new JSONArray(shelfTmpVcs.getAttr1());
//                        System.out.println("005 : " + shelfTmpVcs.getAttr1());
                        for (int aai = 0; aai < attrArr.length(); aai++) {
                            JSONObject obj = attrArr.getJSONObject(aai);
//                            if (detail.getCompUuid().getUuid().equalsIgnoreCase(obj.getString("compUuid"))) {
                            if (obj.has("chkConsentList")) {
                                eachDetail.put("data", obj);
                            }
                        }
                    }
                } else {
                    eachDetail.put("data", detail.getValue() == null ? new JSONObject() : new JSONObject(detail.getValue()));
                }

                infoData.put(eachDetail);

            }
            JSONObject value = new JSONObject()
                    .put("header", header)
                    .put("info", infoData)
                    .put("vcsVersion", vcsTmpVersion);
            returnVal.put("data", value);
        } catch (JSONException | NullPointerException | HibernateException e) {
            logger.info(e.getMessage());
            log.error("" + e);
            //e.printStackTrace();
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    /* create new template */
    @Log_decorator
    @RequestMapping(value = "save", method = POST)
    @ResponseBody
    public ResponseEntity<?> setTemplate(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String reqBody, @RequestHeader(value = "sub_state", required = false) String subState) {
        logger.info("POST : /shelf/template/v1/save");
        log.info("POST : /shelf/template/v1/save");
        HttpStatus httpStatus = HttpStatus.OK;
        JSONObject returnVal = new JSONObject().put("status", httpStatus.value()).put("description", "").put("confirmmsg", "");
        try {
            JSONObject reqValue = new JSONObject(reqBody);

            String[] reqField = {"userInfo", "data"};
            for (int i = 0; i < reqField.length; i++) {
                if (!reqValue.has(reqField[i])) {
                    httpStatus = HttpStatus.BAD_REQUEST;
                    logger.info("## ERROR : " + reqField[i] + " Request Field");
                    returnVal.put("status", httpStatus.value()).put("description", reqField[i] + " is request Field");
                    return (new ResponseEntity<>(returnVal.toString(), headersJSON, httpStatus));
                }
            }

            /* override for create function */
//        reqValue.getJSONObject("data").getJSONObject("header").put("action","save");
            JSONObject data = reqValue.getJSONObject("data");
            //System.out.println("data : " + data);
            JSONObject header = data.getJSONObject("header");
            String action = header.getString("action");
            int vscStatus = getStatusByAction(subState, action);
            if (vscStatus != 0) {
                returnVal = saveOrUpdate(subState, reqValue, "newTemplate", vscStatus);
            } else {
                returnVal.put("status", HttpStatus.BAD_REQUEST.value()).put("description", "action is request Field");
            }
        } catch (JSONException | NullPointerException e) {
            logger.info(e.getMessage());
            log.error("" + e);
            //e.printStackTrace();
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    /* create new version or Update exsit version */
    @Log_decorator
    @RequestMapping(value = "save", method = PUT)
    @ResponseBody
    public ResponseEntity<?> putTemplate(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String reqBody, @RequestHeader(value = "sub_state", required = false) String subState) {
        logger.info("PUT : /shelf/template/v1/save");
        log.info("PUT : /shelf/template/v1/save");
        HttpStatus httpStatus = HttpStatus.OK;
        JSONObject returnVal = new JSONObject().put("status", httpStatus.value()).put("description", "").put("confirmmsg", "");
        try {
            JSONObject reqValue = new JSONObject(reqBody);
            //System.out.println("reqValue : " + reqValue);
            String[] reqField = {"userInfo", "data"};
            for (int i = 0; i < reqField.length; i++) {
                if (!reqValue.has(reqField[i])) {
                    httpStatus = HttpStatus.BAD_REQUEST;
                    logger.info("## ERROR : " + reqField[i] + " Request Field");
                    returnVal.put("status", httpStatus.value()).put("description", reqField[i] + " is request Field");
                    return (new ResponseEntity<>(returnVal.toString(), headersJSON, httpStatus));
                }
            }

            /*
        TO-DO แยกให้ได้ว่าเป็นการ สร้าง Version ใหม่ หรือเป็นการ Update Version เดิม
        - 1 version ของ Template มีได้ 1 Action (1)
        - 1 version ของ Template มีได้ 1 Wait to approve (99)
        - 1 Version ของ Template มีได้ 1 In Progress (88)
        สามารถแยกได้จาก ปุ่มหน้าขอ Param "action" 
         * กรณี send to approve = 99
         * กรณี save จะเป็น In Progress = 88
            - กรณี Save แล้วมี Version นี้อยู่ ในสถานะ 88 ให้ Update
             */
            JSONObject actionFlag = checkActionTemplateStatus(subState, reqValue);

            if (actionFlag.getBoolean("flag") && actionFlag.getInt("vcsStatus") != 0) {
                returnVal = saveOrUpdate(subState, reqValue, actionFlag.getString("sysAction"), actionFlag.getInt("vcsStatus"));
            } else {
                httpStatus = HttpStatus.BAD_REQUEST;
                logger.info("## ERROR : " + actionFlag.getString("description"));
                returnVal.put("status", httpStatus.value()).put("description", actionFlag.getString("description"));
            }
        } catch (JSONException | NullPointerException e) {
            logger.info(e.getMessage());
            log.error("" + e);
            //e.printStackTrace();
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, httpStatus));
    }

    @Log_decorator
    @RequestMapping(value = "pending/{reqStatus}", method = PUT)
    @ResponseBody
    public ResponseEntity<?> approveTmpVsc(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String reqBody, @PathVariable String reqStatus, @RequestHeader(value = "sub_state", required = false) String subState) {

        JSONObject returnVal = new JSONObject().put("status", HttpStatus.OK.value()).put("description", "");
        logger.info("PUT : /shelf/template/v1/pending/" + reqStatus);
        log.info("PUT : /shelf/template/v1/pending/" + reqStatus);
        HttpStatus httpStatus = HttpStatus.OK;
        JSONObject reqValue = new JSONObject(reqBody);
        try {

            String[] reqField = {"userInfo", "data"};
            for (int i = 0; i < reqField.length; i++) {
                if (!reqValue.has(reqField[i])) {
                    httpStatus = HttpStatus.BAD_REQUEST;
                    logger.info("## ERROR : " + reqField[i] + " Request Field");
                    returnVal.put("status", httpStatus.value()).put("description", reqField[i] + " is request Field");
                    return (new ResponseEntity<>(returnVal.toString(), headersJSON, httpStatus));
                }
            }

            JSONObject data = reqValue.getJSONObject("data");
            JSONObject header = data.getJSONObject("header");
            String vcsUuid = header.getString("vcsUuid");

            ShelfTmpDetail shelfTmpDetail = new ShelfTmpDetailDao().getAttrByVcsUuid(subState, vcsUuid);
            JSONObject getUpdateStatus = setStatusTemplate(subState, reqStatus, reqValue, shelfTmpDetail.getAttUuid().getUuid());

            if (!getUpdateStatus.getBoolean("status")) {
                httpStatus = HttpStatus.BAD_REQUEST;
                returnVal.put("status", httpStatus.value()).put("description", getUpdateStatus.getString("description"));
            }
        } catch (JSONException | NullPointerException | HibernateException e) {
            logger.info(e.getMessage());
            log.error("" + e);
            //e.printStackTrace();
        }
        return (new ResponseEntity<>(reqValue.toString(), headersJSON, httpStatus));

    }

    @Log_decorator
    @RequestMapping(value = "list/active", method = GET)
    @ResponseBody
    public ResponseEntity<?> getActiveTemplate(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestHeader(value = "sub_state", required = false) String subState) {
        logger.info("GET : /shelf/template/v1/list/active");
        log.info("GET : /shelf/template/v1/list/active");
//        CompletableFuture.runAsync(() -> {
        HttpStatus httpStatus = HttpStatus.OK;
        JSONArray returnVal = new JSONArray();
        try {
            TemplateUtils.setActiveExpireTemplate(subState);
//        });
            Integer statusActive = StatusUtils.getActive(subState).getStatusCode();
            returnVal = new TemplateUtils().getTemplateListByStatus(subState, statusActive);
        } catch (JSONException | NullPointerException | HibernateException | SQLException e) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, httpStatus));
    }

    @Log_decorator
    @RequestMapping(value = "list/activeinactive", method = GET)
    @ResponseBody
    public ResponseEntity<?> getActiveInActiveTemplate(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestHeader(value = "sub_state", required = false) String subState) {
        logger.info("GET : /shelf/template/v1/list/activeinactive");
        log.info("GET : /shelf/template/v1/list/activeinactive");
        HttpStatus httpStatus = HttpStatus.OK;
        JSONArray returnVal = new JSONArray();
        try {
            TemplateUtils.setActiveExpireTemplate(subState);
            returnVal = new TemplateUtils().getActiveInActiveTemplateList(subState);
        } catch (JSONException | NullPointerException | HibernateException | SQLException e) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, httpStatus));
    }

    @Log_decorator
    @RequestMapping(value = "list/{status}", method = GET)
    @ResponseBody
    public ResponseEntity<?> getTemplateListByStatus(HttpSession session, HttpServletResponse response, HttpServletRequest request, @PathVariable int status, @RequestHeader(value = "sub_state", required = false) String subState) {
        logger.info("PUT : /shelf/template/v1/list/{status}" + status);
        log.info("PUT : /shelf/template/v1/list/{status}" + status);
        JSONArray returnVal = new TemplateUtils().getTemplateListByStatus(subState, status);
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @Log_decorator
    @RequestMapping(value = "list/product", method = GET)
    @ResponseBody
    public ResponseEntity<?> getTemplateListAndProductUsage(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestHeader(value = "sub_state", required = false) String subState) {
        logger.info("GET : /shelf/template/v1/list/product");
        log.info("GET : /shelf/template/v1/list/product");
        JSONArray returnVal = new JSONArray();
        HttpStatus httpStatus = HttpStatus.OK;
        try {
            returnVal = new ShelfTmpDao().getTemplateListAndProductUsage(subState);
        } catch (JSONException | NullPointerException | HibernateException | SQLException e) {
            logger.info(e.getMessage());
            log.error("" + e);
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, httpStatus));
    }

    @Log_decorator
    @RequestMapping(value = "search", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> searchTemplate(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String reqBody, @RequestHeader(value = "sub_state", required = false) String subState) {
        logger.info("POST : /shelf/template/v1/search");
        log.info("POST : /shelf/template/v1/search");
        JSONObject returnVal = new JSONObject().put("status", HttpStatus.OK.value()).put("description", "").put("data", new JSONObject());
        try {
            TemplateUtils.setActiveExpireTemplate(subState);
            ProductUtils.setActiveExpireProduct(subState);
            JSONObject json = new JSONObject(reqBody);
            String templateName = json.has("templateName") ? json.getString("templateName") : "";
            JSONArray status = json.has("status") ? json.getJSONArray("status") : new JSONArray();
            Date startCreateDate = ValidUtils.str2Date(json.has("startCreateDate") ? (json.getString("startCreateDate").isEmpty() ? null : json.getString("startCreateDate")) : null);
            Date endCreateDate = ValidUtils.str2Date(json.has("endCreateDate") ? (json.getString("endCreateDate").isEmpty() ? null : json.getString("endCreateDate")) : null);
            Date startUpdateDate = ValidUtils.str2Date(json.has("startUpdateDate") ? (json.getString("startUpdateDate").isEmpty() ? null : json.getString("startUpdateDate")) : null);
            Date endUpdateDate = ValidUtils.str2Date(json.has("endUpdateDate") ? (json.getString("endUpdateDate").isEmpty() ? null : json.getString("endUpdateDate")) : null);
            Date effectiveDateFrom = ValidUtils.str2Date(json.has("effectiveDateFrom") ? (json.getString("effectiveDateFrom").isEmpty() ? null : json.getString("effectiveDateFrom")) : null);
            Date effectiveDateTo = ValidUtils.str2Date(json.has("effectiveDateTo") ? (json.getString("effectiveDateTo").isEmpty() ? null : json.getString("effectiveDateTo")) : null);
            returnVal.put("data", new ShelfTmpVcsDao().searchShelfTemplate(subState, templateName, status, startCreateDate, endCreateDate, startUpdateDate, endUpdateDate, effectiveDateFrom, effectiveDateTo));
        } catch (JSONException | NullPointerException | HibernateException | SQLException | ParseException e) {
            logger.info(e.getMessage());
            log.error("" + e);
            //e.printStackTrace();
            returnVal.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @Log_decorator
    @RequestMapping(value = "checkTemplateName", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> checkTemplateName(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String reqBody, @RequestHeader(value = "sub_state", required = false) String subState) {
        logger.info("POST : /shelf/theme/v1/checkTemplateName");
        log.info("POST : /shelf/theme/v1/checkTemplateName");
        JSONObject returnVal = new JSONObject().put("status", HttpStatus.OK.value()).put("description", "").put("data", new JSONObject());
        try {
            JSONObject json = new JSONObject(reqBody);
            String templateName = json.has("templateName") ? json.getString("templateName") : "";
            if (!templateName.trim().isEmpty()) {
                List<ShelfTmpVcs> templateVcs = new ShelfTmpVcsDao().getShelfTemplateByNameStatusActiveAndInActive(subState, templateName);
                if (templateVcs.isEmpty()) {
                    returnVal.put("data", new JSONObject().put("status", true).put("description", "You can use this name."));
                } else {
                    returnVal.put("data", new JSONObject().put("status", false).put("description", "Template name is used."));
                }
            } else {
                returnVal.put("status", HttpStatus.BAD_REQUEST.value())
                        .put("description", StatusUtils.getErrorMessageByCode(subState, "SHELF0042"));
            }
        } catch (JSONException | NullPointerException | HibernateException e) {
            logger.info(e.getMessage());
            log.error("" + e);
            //e.printStackTrace();
            returnVal.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .put("description", "" + e);

        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @Log_decorator
    @RequestMapping(value = "listStatus", method = POST)
    @ResponseBody
    public ResponseEntity<?> getTemplateListByStatus(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String reqBody, @RequestHeader(value = "sub_state", required = false) String subState) {
        JSONObject returnVal = new JSONObject().put("status", HttpStatus.OK.value()).put("description", "").put("data", new JSONObject());
        logger.info("POST : /shelf/template/v1/listStatus");
        log.info("POST : /shelf/template/v1/listStatus");
        try {
            TemplateUtils.setActiveExpireTemplate(subState);
            JSONArray reqStatus = new JSONArray(reqBody);
            List statusList = new ArrayList<>();
            for (int i = 0; i < reqStatus.length(); i++) {
                statusList.add(reqStatus.getInt(i));
            }
            returnVal.put("data", new TemplateUtils().getTemplateListByStatus(subState, statusList));
        } catch (JSONException | NullPointerException | SQLException e) {
            logger.info(e.getMessage());
            log.error("" + e);
            //e.printStackTrace();
            returnVal.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .put("description", "" + e);

        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @Log_decorator
    @RequestMapping(value = "api/check", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> checkTemplate(HttpSession session, HttpServletResponse response,
            HttpServletRequest request,
            @RequestBody String reqBody,
            @RequestHeader(value = "sub_state", required = false) String subState
    ) {
        logger.info(String.format("GET : /shelf/template/v1/api/check"));
        log.info(String.format("GET : /shelf/template/v1/api/check"));
        JSONObject returnVal = new JSONObject().put("status", HttpStatus.OK.value()).put("description", "").put("confirmmsg", "").put("data", new JSONObject());
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
                returnVal = checkTemplate(subState, datas.getJSONObject("data"));
            }
        } catch (JSONException | NullPointerException e) {
            logger.info(e.getMessage());
            log.error("" + e);
            e.printStackTrace();
            returnVal.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .put("description", "" + e);

        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    /* status */
    private JSONObject checkStatus(String dbEnv, JSONObject reqValue, String reqStatus) {
        JSONObject returnVal = new JSONObject();
        logger.info("Function : TemplateV1Controller -> checkStatus");
        HttpStatus httpStatus = HttpStatus.OK;
        try {
            Memlookup lookup = new Memlookup();
            SysLookupDao lookUpDao = new SysLookupDao();
            ShelfTmpVcsDao vcsDao = new ShelfTmpVcsDao();
            int setStatus = Integer.parseInt(lookup.getLookupcode());

            JSONObject userInfo = reqValue.getJSONObject("userInfo");
            JSONObject companyInfo = userInfo.getJSONObject("company");
            String userName = userInfo.getString("userName");
            String companyCode = companyInfo.getString("companyCode");
            String businessDept = companyInfo.getString("businessDept");
            String businessLine = companyInfo.getString("businessLine");

            JSONObject data = reqValue.getJSONObject("data");
            JSONObject header = data.getJSONObject("header");
            String tmpName = header.getString("name");
            String tmpUuid = header.getString("tmpUuid");
            String vcsUuid = header.getString("vcsUuid");
            //        String enable = header.getBoolean("enable");
            String action = header.getString("action");
            Date effectiveDate = convert2Date(header.getString("effectiveDate"));

            int currentVersion = vcsDao.maxVersion(dbEnv, tmpUuid);

            if (reqStatus.equalsIgnoreCase("approve")) {
                lookup = lookUpDao.getMemLookupByValue(dbEnv, "waittoapprove");
                logger.info(lookup.getLookupcode());

                new ShelfTmpDao().updateStatus(dbEnv, tmpUuid, setStatus);

            } else {
                /* no status in syslookup */
                httpStatus = HttpStatus.BAD_REQUEST;
                logger.info("## ERROR : state to action is not define");
                returnVal.put("status", httpStatus.value()).put("description", StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0043"));
            }

        } catch (NumberFormatException | JSONException | NullPointerException | SQLException | HibernateException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
        }

        return returnVal;
    }

    private JSONObject checkActionTemplateStatus(String dbEnv, JSONObject reqValue) {

        logger.info("checkActionTemplateStatus ");
        JSONObject returnVal = new JSONObject().put("readOnly", false);
        try {
//        JSONObject userInfo = reqValue.getJSONObject("userInfo");
//        String userName = userInfo.getString("userName");

//        JSONObject companyInfo = userInfo.getJSONObject("company");
//        String companyCode = companyInfo.getString("companyCode");
//        String businessDept = companyInfo.getString("businessDept");
//        String businessLine = companyInfo.getString("businessLine");
            JSONObject data = reqValue.getJSONObject("data");
            JSONObject header = data.getJSONObject("header");
            String tmpName = header.getString("name");
//        String tmpUuid = header.getString("tmpUuid");
            String vcsUuid = header.getString("vcsUuid");
//        String enable = header.getBoolean("enable");
            String action = header.getString("action");
//        Date effectiveDate = convert2Date(header.getString("effectiveDate"));

//        System.out.println("Action -> " + action);
            int vcsStatus = getStatusByAction(dbEnv, action);

            ShelfTmpVcs shelfTmpVcs = new ShelfTmpVcsDao().getListByUuid(dbEnv, vcsUuid);
            Memlookup memlookup = new SysLookupDao().getMemLookupByCode(dbEnv, String.valueOf(shelfTmpVcs.getStatus()));
            JSONObject flagAction = new JSONObject().put("edit", memlookup.getFlagedit()).put("create", memlookup.getFlagcreate());

            if (vcsStatus != 0) {
                if (flagAction.getBoolean("edit") || action.equalsIgnoreCase("sendapprove") || action.equalsIgnoreCase("senddelete") || action.equalsIgnoreCase("reject")) {
                    returnVal.put("flag", true)
                            .put("sysAction", "update")
                            .put("vcsStatus", vcsStatus)
                            .put("description", "");
                } else if (flagAction.getBoolean("create") || (action.equalsIgnoreCase("approve") || action.equalsIgnoreCase("delete"))) {
                    returnVal.put("flag", true)
                            .put("sysAction", "newVersion")
                            .put("vcsStatus", vcsStatus)
                            .put("description", "");
                } else {
                    returnVal.put("flag", false)
                            .put("sysAction", "")
                            .put("vcsStatus", vcsStatus)
                            .put("description", tmpName + "  - Version " + shelfTmpVcs.getVersion() + " -> Can't because Status " + memlookup.getLookupnameen());
                }
            } else {
                returnVal.put("flag", false)
                        .put("sysAction", "")
                        .put("vcsStatus", vcsStatus)
                        .put("description", tmpName + "  - Version " + shelfTmpVcs.getVersion() + " -> not define 'save or send to approve'");
            }

            if (!flagAction.getBoolean("edit") && flagAction.getBoolean("create")) {
                returnVal.put("readOnly", true);
            }
            logger.info(tmpName + " -> " + returnVal);
        } catch (NullPointerException | HibernateException e) {
            throw e;
        }
        return returnVal;
    }

    private JSONObject getActionFromStatus(String dbEnv, int vcsStatus) {
        JSONObject returnVal = new JSONObject().put("readOnly", false);
        try {
            Memlookup memlookup = new SysLookupDao().getMemLookupByCode(dbEnv, String.valueOf(vcsStatus));
            JSONObject flagAction = new JSONObject().put("edit", memlookup.getFlagedit()).put("create", memlookup.getFlagcreate());

            if (flagAction.getBoolean("edit")) {
                returnVal.put("flag", true)
                        .put("sysAction", "update")
                        .put("vcsStatus", vcsStatus)
                        .put("description", "");
            } else if (flagAction.getBoolean("create")) {
                returnVal.put("flag", true)
                        .put("sysAction", "newVersion")
                        .put("vcsStatus", vcsStatus)
                        .put("description", "");
            } else {
                returnVal.put("flag", false)
                        .put("sysAction", "")
                        .put("vcsStatus", vcsStatus)
                        .put("description", "Can't because Status " + memlookup.getLookupnameen());
            }

            if (!flagAction.getBoolean("edit") && flagAction.getBoolean("create")) {
                returnVal.put("readOnly", true);
            }
        } catch (NullPointerException | HibernateException e) {
            throw e;
        }
        return returnVal;
    }

    private int getStatusByAction(String dbEnv, String action) {
        int vcsStatus = 0;
        try {
            if (action.equalsIgnoreCase("save")) {
                vcsStatus = Integer.parseInt(new SysLookupDao().getMemLookupByValue(dbEnv, "inprogress").getLookupcode());
            } else if (action.equalsIgnoreCase("sendapprove")) {
                vcsStatus = Integer.parseInt(new SysLookupDao().getMemLookupByValue(dbEnv, "waittoapprove").getLookupcode());
            } else if (action.equalsIgnoreCase("approve")) {
                vcsStatus = Integer.parseInt(new SysLookupDao().getMemLookupByValue(dbEnv, "active").getLookupcode());
            } else if (action.equalsIgnoreCase("senddelete")) {
                vcsStatus = Integer.parseInt(new SysLookupDao().getMemLookupByValue(dbEnv, "waittodelete").getLookupcode());
            } else if (action.equalsIgnoreCase("delete")) {
                vcsStatus = Integer.parseInt(new SysLookupDao().getMemLookupByValue(dbEnv, "delete").getLookupcode());
            } else if (action.equalsIgnoreCase("reject")) {
                vcsStatus = Integer.parseInt(new SysLookupDao().getMemLookupByValue(dbEnv, "reject").getLookupcode());
            }
        } catch (NullPointerException | HibernateException e) {
            throw e;
        }
        return vcsStatus;
    }

    private JSONObject setStatusTemplate(String dbEnv, String reqStatus, JSONObject reqValue, String attUuid) {
//        int setStatus;
//        Memlookup lookup = new Memlookup();
//        SysLookupDao dao = new SysLookupDao();
        try {
//            Integer statusWaitToApprove = StatusUtils.getWaittoApprove(dbEnv).getStatusCode();
            Integer statusActive = StatusUtils.getActive(dbEnv).getStatusCode();
            Integer statusInActive = StatusUtils.getInActive(dbEnv).getStatusCode();
            Integer statusCancel = StatusUtils.getCancel(dbEnv).getStatusCode();
            Integer statusExpired = StatusUtils.getExpired(dbEnv).getStatusCode();
            Integer statusWaitToApprove = StatusUtils.getWaittoApprove(dbEnv).getStatusCode();
            Integer statusReject = StatusUtils.getReject(dbEnv).getStatusCode();
            Integer statusWaitToDelete = StatusUtils.getWaittoDelete(dbEnv).getStatusCode();
            Integer statusDelete = StatusUtils.getDelete(dbEnv).getStatusCode();
            Integer statusInprogress = StatusUtils.getInprogress(dbEnv).getStatusCode();
            Integer statusNotUse = StatusUtils.getNotUse(dbEnv).getStatusCode();
//            Integer statusTerminate = StatusUtils.getTerminate(dbEnv).getStatusCode();
//            ShelfTmpVcs activeVcs = new ShelfTmpVcs();
//            ShelfTmpVcs inactiveVcs = new ShelfTmpVcs();
//                lookup = dao.getMemLookupByValue(dbEnv, "waittoapprove");
//                logger.info(lookup.getLookupcode());
//                setStatus = Integer.parseInt(lookup.getLookupcode());

            JSONObject userInfo = reqValue.getJSONObject("userInfo");
//                JSONObject companyInfo = userInfo.getJSONObject("company");
//                String userName = userInfo.getString("userName");
//                String companyCode = companyInfo.getString("companyCode");
//                String businessDept = companyInfo.getString("businessDept");
//                String businessLine = companyInfo.getString("businessLine");

            JSONObject data = reqValue.getJSONObject("data");
            JSONObject header = data.getJSONObject("header");
//                String tmpName = header.getString("name");
            String tmpUuid = header.getString("tmpUuid");
            String vcsUuid = header.getString("vcsUuid");
            //        String enable = header.getBoolean("enable");
//                String action = header.getString("action");
//                Date effectiveDate = convert2Date(header.getString("effectiveDate"));

//                ShelfTmp shelfTmp = new ShelfTmpDao().getShelfTmp(dbEnv, tmpUuid);
//                List<ShelfTmpVcs> shelfTmpVcsList = shelfTmp.getShelfTmpVcsList();
            ShelfTmpVcs tmpVcs = new ShelfTmpVcsDao().getListByUuid(dbEnv, vcsUuid);
            List<ShelfTmpVcs> shelfTmpVcsList = new ShelfTmpVcsDao().getShelfTmpVcsByTmpUUID(dbEnv, tmpUuid, vcsUuid);
            ShelfTmp tmp = new ShelfTmpDao().getShelfTmp(dbEnv, tmpUuid);
            List prodStatus = new ArrayList<>();
            prodStatus.add(StatusUtils.getDelete(dbEnv).getStatusCode());//Delete
            prodStatus.add(StatusUtils.getTerminate(dbEnv).getStatusCode());//Terminate
            prodStatus.add(StatusUtils.getCancel(dbEnv).getStatusCode());//Cancel
            prodStatus.add(StatusUtils.getExpired(dbEnv).getStatusCode());//Expire
            prodStatus.add(StatusUtils.getNotUse(dbEnv).getStatusCode());//Not use
            prodStatus.add(StatusUtils.getPause(dbEnv).getStatusCode());//Pause
            Date sysdate = new Date();
            if (reqStatus.equalsIgnoreCase("approve")) {
                if (shelfTmpVcsList.isEmpty()) {
                    if (tmpVcs.getStatus() == statusWaitToDelete) {
                        new ShelfTmpDao().updateStatus(dbEnv, tmpUuid, statusDelete);
                        new ShelfTmpVcsDao().updateStatus(dbEnv, vcsUuid, statusDelete, StatusUtils.setStatus(tmpVcs.getState(), ValidUtils.null2NoData(statusDelete)), "", true);
//                            new ShelfTmpAttDao().updateStatus(dbEnv, vcs.get, statusActive);
                        new ShelfTmpDetailDao().updateStatus(dbEnv, vcsUuid, statusDelete);
                    } else if (tmpVcs.getStatus() == statusWaitToApprove) {
//                        Date sysdate = new Date();
                        if (tmpVcs.getEffectiveDate().compareTo(sysdate) <= 0) {
                            new ShelfTmpDao().updateStatus(dbEnv, tmpUuid, statusActive);
                            new ShelfTmpVcsDao().updateStatus(dbEnv, vcsUuid, statusActive, StatusUtils.setStatus(tmpVcs.getState(), ValidUtils.null2NoData(statusActive)), "", true);
//                            new ShelfTmpAttDao().updateStatus(dbEnv, vcs.get, statusActive);
                            new ShelfTmpDetailDao().updateStatus(dbEnv, vcsUuid, statusActive);
                        } else {
                            new ShelfTmpDao().updateStatus(dbEnv, tmpUuid, statusInActive);
                            new ShelfTmpVcsDao().updateStatus(dbEnv, vcsUuid, statusInActive, StatusUtils.setStatus(tmpVcs.getState(), ValidUtils.null2NoData(statusInActive)), "", true);
//                            new ShelfTmpAttDao().updateStatus(dbEnv, vcs.get, statusInActive);
                            new ShelfTmpDetailDao().updateStatus(dbEnv, vcsUuid, statusInActive);
                        }
                    }
                    /*
                    new ShelfTmpDao().updateStatus(dbEnv, tmpUuid, statusActive);
                    new ShelfTmpVcsDao().updateStatus(dbEnv, vcsUuid, statusActive, StatusUtils.setStatus(tmpVcs.getState(), ValidUtils.null2NoData(statusActive)), "", true);
//                    new ShelfTmpAttDao().updateStatus(dbEnv, attUuid, statusActive);
                    new ShelfTmpDetailDao().updateStatus(dbEnv, vcsUuid, statusActive);
                     */
                } else {
                    if (tmpVcs.getStatus() == statusWaitToDelete) {
                        for (ShelfTmpVcs vcs : tmp.getShelfTmpVcsList()) {
                            new ShelfTmpDao().updateStatus(dbEnv, vcs.getTmpUuid().getUuid(), statusDelete);
                            new ShelfTmpVcsDao().updateStatus(dbEnv, vcs.getUuid(), statusDelete, StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(statusDelete)), header.has("remark") ? header.getString("remark") : null, false);
//                            new ShelfTmpAttDao().updateStatus(dbEnv, vcs.get, statusCancel);
                            new ShelfTmpDetailDao().updateStatus(dbEnv, vcs.getUuid(), statusDelete);
                        }
                    } else if (tmpVcs.getStatus() == statusWaitToApprove) {
//                        boolean isIncreaseVer = false;
                        Integer vcsStatus = statusActive;
                        for (ShelfTmpVcs vcs : shelfTmpVcsList) {
                            if (vcs.getStatus() == statusActive) {
                                if (header.getBoolean("confirm")) {
                                    List<ShelfProductVcs> prodVcsList = new ShelfProductVcsDao().getProductByNotProductStatusTmpUuidAndTmpVer(dbEnv, tmpUuid, vcs.getVersion(), prodStatus);
                                    if (!prodVcsList.isEmpty()) {
                                        return new JSONObject().put("status", false).put("description", StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0044")).put("productUsed", true).put("confirmmsg", "");
                                    } else {
//                                    if (header.getBoolean("confirm")) {
                                        if (tmpVcs.getEffectiveDate().compareTo(sysdate) <= 0) {
                                            //change expire to not use
                                            new ShelfTmpDao().updateStatus(dbEnv, vcs.getTmpUuid().getUuid(), statusNotUse);
                                            new ShelfTmpVcsDao().updateStatus(dbEnv, vcs.getUuid(), statusNotUse, StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(statusNotUse)), "", false);
//                            new ShelfTmpAttDao().updateStatus(dbEnv, vcs.get, statusCancel);
                                            new ShelfTmpDetailDao().updateStatus(dbEnv, vcs.getUuid(), statusNotUse);

//                                            new ShelfTmpDao().updateStatus(dbEnv, tmpUuid, statusActive);
//                                            new ShelfTmpVcsDao().updateStatus(dbEnv, vcsUuid, statusActive, StatusUtils.setStatus(tmpVcs.getState(), ValidUtils.null2NoData(statusActive)), "", (!isIncreaseVer));
////                            new ShelfTmpAttDao().updateStatus(dbEnv, vcs.get, statusInActive);
//                                            new ShelfTmpDetailDao().updateStatus(dbEnv, vcsUuid, statusActive);
                                            vcsStatus = statusActive;
//                                        isIncreaseVer = true;
                                        } else {
//                                            new ShelfTmpDao().updateStatus(dbEnv, tmpUuid, statusInActive);
//                                            new ShelfTmpVcsDao().updateStatus(dbEnv, vcsUuid, statusInActive, StatusUtils.setStatus(tmpVcs.getState(), ValidUtils.null2NoData(statusInActive)), "", (!isIncreaseVer));
////                            new ShelfTmpAttDao().updateStatus(dbEnv, vcs.get, statusInActive);
//                                            new ShelfTmpDetailDao().updateStatus(dbEnv, vcsUuid, statusInActive);
                                            vcsStatus = statusInActive;
//                                        isIncreaseVer = true;
                                        }
//                                    }
                                    }
                                } else {
                                    if (tmpVcs.getEffectiveDate().compareTo(sysdate) <= 0) {
                                        return new JSONObject().put("status", false).put("confirmmsg", StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0045")).put("description", "");
                                    } else {
                                        vcsStatus = statusInActive;
                                    }
                                }
                            } else if (vcs.getStatus() == statusInActive) {
                                if (header.getBoolean("confirm")) {
                                    if (tmpVcs.getEffectiveDate().compareTo(sysdate) <= 0) {
//                                        new ShelfTmpDao().updateStatus(dbEnv, tmpUuid, statusActive);
//                                        new ShelfTmpVcsDao().updateStatus(dbEnv, vcsUuid, statusActive, StatusUtils.setStatus(tmpVcs.getState(), ValidUtils.null2NoData(statusActive)), "", (!isIncreaseVer));
//                                        new ShelfTmpDetailDao().updateStatus(dbEnv, vcsUuid, statusActive);
                                        vcsStatus = statusActive;
//                                        isIncreaseVer = true;
                                    } else {
//                                        new ShelfTmpDao().updateStatus(dbEnv, tmpUuid, statusInActive);
//                                        new ShelfTmpVcsDao().updateStatus(dbEnv, vcsUuid, statusInActive, StatusUtils.setStatus(tmpVcs.getState(), ValidUtils.null2NoData(statusInActive)), "", (!isIncreaseVer));
//                                        new ShelfTmpDetailDao().updateStatus(dbEnv, vcsUuid, statusInActive);

                                        new ShelfTmpDao().updateStatus(dbEnv, vcs.getTmpUuid().getUuid(), statusCancel);
                                        new ShelfTmpVcsDao().updateStatus(dbEnv, vcs.getUuid(), statusCancel, StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(statusCancel)), "", false);
//                            new ShelfTmpAttDao().updateStatus(dbEnv, vcs.get, statusCancel);
                                        new ShelfTmpDetailDao().updateStatus(dbEnv, vcs.getUuid(), statusCancel);
                                        vcsStatus = statusInActive;
//                                        isIncreaseVer = true;
                                    }
                                } else {
                                    if (tmpVcs.getEffectiveDate().compareTo(sysdate) > 0) {
                                        return new JSONObject().put("status", false).put("confirmmsg", StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0046")).put("description", "");
                                    } else {
                                        vcsStatus = statusActive;
                                    }
                                }
                            }
                        }
                        if (header.has("bank") && header.getBoolean("bank")) {
                            JSONArray attrArr = new JSONArray(tmpVcs.getAttr1());
                            for (int aai = 0; aai < attrArr.length(); aai++) {
                                JSONObject obj = attrArr.getJSONObject(aai);
                                if (obj.has("termsNCondition")) {
                                    obj.put("dataset", header.has("dataset") ? header.getString("dataset") : "");
                                }
                            }
                            tmpVcs.setAttr1(attrArr.toString());
                        }
                        new ShelfTmpVcsDao().updateVcs(dbEnv, tmpVcs);
                        new ShelfTmpDao().updateStatus(dbEnv, tmpUuid, vcsStatus);
                        new ShelfTmpVcsDao().updateStatus(dbEnv, vcsUuid, vcsStatus, StatusUtils.setStatus(tmpVcs.getState(), ValidUtils.null2NoData(vcsStatus)), "", true);
                        new ShelfTmpDetailDao().updateStatus(dbEnv, vcsUuid, vcsStatus);
                    }
                }
                return new JSONObject().put("status", true).put("description", "").put("confirmmsg", "");
            } else if (reqStatus.equalsIgnoreCase("sendapprove")) {
                if (shelfTmpVcsList.isEmpty()) {
                    new ShelfTmpDao().updateStatus(dbEnv, tmpVcs.getTmpUuid().getUuid(), statusWaitToApprove);
                    new ShelfTmpVcsDao().updateStatus(dbEnv, tmpVcs.getUuid(), statusWaitToApprove, StatusUtils.setStatus(tmpVcs.getState(), ValidUtils.null2NoData(statusWaitToApprove)), "", false);
//                            new ShelfTmpAttDao().updateStatus(dbEnv, vcs.get, statusCancel);
                    new ShelfTmpDetailDao().updateStatus(dbEnv, tmpVcs.getUuid(), statusWaitToApprove);
                } else {
                    for (ShelfTmpVcs vcs : shelfTmpVcsList) {
                        if (vcs.getStatus() == statusWaitToApprove) {
//                            vcs.setStatus(statusCancel);
//                            vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(statusCancel)));
                            return new JSONObject().put("status", false).put("description", StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0047")).put("confirmmsg", "");
                        } else if (vcs.getStatus() == statusActive) {
                            if (!header.getBoolean("confirm")) {
//                                new ShelfTmpDao().updateStatus(dbEnv, tmpUuid, statusWaitToApprove);
//                                new ShelfTmpVcsDao().updateStatus(dbEnv, vcsUuid, statusWaitToApprove, StatusUtils.setStatus(tmpVcs.getState(), ValidUtils.null2NoData(statusWaitToApprove)), "", false);
////                            new ShelfTmpAttDao().updateStatus(dbEnv, vcs.get, statusInActive);
//                                new ShelfTmpDetailDao().updateStatus(dbEnv, vcsUuid, statusWaitToApprove);
//                            } else {
                                if (tmpVcs.getEffectiveDate().compareTo(sysdate) <= 0) {
                                    return new JSONObject().put("status", false).put("confirmmsg", StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0048")).put("description", "");
                                }
                            }
                        } else if (vcs.getStatus() == statusInActive) {
                            if (!header.getBoolean("confirm")) {
//                                new ShelfTmpDao().updateStatus(dbEnv, tmpUuid, statusWaitToApprove);
//                                new ShelfTmpVcsDao().updateStatus(dbEnv, vcsUuid, statusWaitToApprove, StatusUtils.setStatus(tmpVcs.getState(), ValidUtils.null2NoData(statusWaitToApprove)), "", false);
////                            new ShelfTmpAttDao().updateStatus(dbEnv, vcs.get, statusInActive);
//                                new ShelfTmpDetailDao().updateStatus(dbEnv, vcsUuid, statusWaitToApprove);
//                            } else {
                                if (tmpVcs.getEffectiveDate().compareTo(sysdate) > 0) {
                                    return new JSONObject().put("status", false).put("confirmmsg", StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0049")).put("description", "");
                                }
                            }
                        }
                    }
                    if (tmpVcs.getStatus() == statusInprogress) {
//                            if (header.getBoolean("confirm")) {
                        new ShelfTmpDao().updateStatus(dbEnv, tmpUuid, statusWaitToApprove);
                        new ShelfTmpVcsDao().updateStatus(dbEnv, vcsUuid, statusWaitToApprove, StatusUtils.setStatus(tmpVcs.getState(), ValidUtils.null2NoData(statusWaitToApprove)), "", false);
//                            new ShelfTmpAttDao().updateStatus(dbEnv, vcs.get, statusInActive);
                        new ShelfTmpDetailDao().updateStatus(dbEnv, vcsUuid, statusWaitToApprove);
//                            }
                    }
                }
                return new JSONObject().put("status", true).put("description", "").put("confirmmsg", "");
            } else if (reqStatus.equalsIgnoreCase("delete")) {
                if (shelfTmpVcsList.isEmpty()) {
                    new ShelfTmpDao().updateStatus(dbEnv, tmpUuid, statusDelete);
                    new ShelfTmpVcsDao().updateStatus(dbEnv, vcsUuid, statusDelete, StatusUtils.setStatus(tmpVcs.getState(), ValidUtils.null2NoData(statusDelete)), "", true);
//                            new ShelfTmpAttDao().updateStatus(dbEnv, vcs.get, statusActive);
                    new ShelfTmpDetailDao().updateStatus(dbEnv, vcsUuid, statusDelete);
                    /*
                    new ShelfTmpDao().updateStatus(dbEnv, tmpUuid, statusActive);
                    new ShelfTmpVcsDao().updateStatus(dbEnv, vcsUuid, statusActive, StatusUtils.setStatus(tmpVcs.getState(), ValidUtils.null2NoData(statusActive)), "", true);
//                    new ShelfTmpAttDao().updateStatus(dbEnv, attUuid, statusActive);
                    new ShelfTmpDetailDao().updateStatus(dbEnv, vcsUuid, statusActive);
                     */
                } else {
                    if (tmp.getStatus() == statusWaitToDelete) {
                        if (header.getBoolean("confirm")) {
                            for (ShelfTmpVcs vcs : shelfTmpVcsList) {
                                new ShelfTmpDao().updateStatus(dbEnv, vcs.getTmpUuid().getUuid(), statusDelete);
                                new ShelfTmpVcsDao().updateStatus(dbEnv, vcs.getUuid(), statusDelete, StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(statusDelete)), "", false);
//                            new ShelfTmpAttDao().updateStatus(dbEnv, vcs.get, statusCancel);
                                new ShelfTmpDetailDao().updateStatus(dbEnv, vcs.getUuid(), statusDelete);
                            }
                        } else {
                            return new JSONObject().put("status", false).put("confirmmsg", StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0050")).put("description", "");
                        }
                    } else {
                        return new JSONObject().put("status", false).put("description", StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0051")).put("confirmmsg", "");
                    }
                }
                return new JSONObject().put("status", true).put("description", "").put("confirmmsg", "");
            } else if (reqStatus.equalsIgnoreCase("senddelete")) {
                List<ShelfProductVcs> prodVcsList = new ShelfProductVcsDao().getProductByNotProductStatusTmpUuidAndTmpVer(dbEnv, tmpUuid, tmpVcs.getVersion(), prodStatus);
                if (!prodVcsList.isEmpty()) {
                    return new JSONObject().put("status", false).put("description", StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0052")).put("productUsed", true).put("confirmmsg", "");
                }
                if (shelfTmpVcsList.isEmpty()) {
                    if (tmpVcs.getStatus() == statusInprogress) {
                        new ShelfTmpDao().updateStatus(dbEnv, tmpVcs.getTmpUuid().getUuid(), statusDelete);
                        new ShelfTmpVcsDao().updateStatus(dbEnv, tmpVcs.getUuid(), statusDelete, StatusUtils.setStatus(tmpVcs.getState(), ValidUtils.null2NoData(statusDelete)), "", false);
//                            new ShelfTmpAttDao().updateStatus(dbEnv, vcs.get, statusCancel);
                        new ShelfTmpDetailDao().updateStatus(dbEnv, tmpVcs.getUuid(), statusDelete);
                    } else {
                        new ShelfTmpDao().updateStatus(dbEnv, tmpVcs.getTmpUuid().getUuid(), statusWaitToDelete);
                        new ShelfTmpVcsDao().updateStatus(dbEnv, tmpVcs.getUuid(), statusWaitToDelete, StatusUtils.setStatus(tmpVcs.getState(), ValidUtils.null2NoData(statusWaitToDelete)), "", false);
//                            new ShelfTmpAttDao().updateStatus(dbEnv, vcs.get, statusCancel);
                        new ShelfTmpDetailDao().updateStatus(dbEnv, tmpVcs.getUuid(), statusWaitToDelete);
                    }
                } else {
//                        for (ShelfTmpVcs vcs : shelfTmpVcsList) {
                    if (tmpVcs.getStatus() == statusWaitToDelete) {
//                            vcs.setStatus(statusCancel);
//                            vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(statusCancel)));
                        return new JSONObject().put("status", false).put("description", StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0053")).put("confirmmsg", "");
                    } else if (tmpVcs.getStatus() == statusWaitToApprove) {
//                            vcs.setStatus(statusCancel);
//                            vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(statusCancel)));
                        return new JSONObject().put("status", false).put("description", StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0054")).put("confirmmsg", "");
                    } else if (tmpVcs.getStatus() == statusInprogress) {
                        new ShelfTmpDao().updateStatus(dbEnv, tmpVcs.getTmpUuid().getUuid(), statusDelete);
                        new ShelfTmpVcsDao().updateStatus(dbEnv, tmpVcs.getUuid(), statusDelete, StatusUtils.setStatus(tmpVcs.getState(), ValidUtils.null2NoData(statusDelete)), "", false);
//                            new ShelfTmpAttDao().updateStatus(dbEnv, vcs.get, statusCancel);
                        new ShelfTmpDetailDao().updateStatus(dbEnv, tmpVcs.getUuid(), statusDelete);
                    } else {
                        new ShelfTmpDao().updateStatus(dbEnv, tmpUuid, statusWaitToDelete);
                        new ShelfTmpVcsDao().updateStatus(dbEnv, tmpVcs.getUuid(), statusWaitToDelete, StatusUtils.setStatus(tmpVcs.getState(), ValidUtils.null2NoData(statusWaitToDelete)), "", false);
//                            new ShelfTmpAttDao().updateStatus(dbEnv, vcs.get, statusInActive);
                        new ShelfTmpDetailDao().updateStatus(dbEnv, tmpVcs.getUuid(), statusWaitToDelete);
                    }
//                        }
                }
                return new JSONObject().put("status", true).put("description", "").put("confirmmsg", "");
            } else if (reqStatus.equalsIgnoreCase("reject")) {
                if (tmpVcs.getStatus() == statusWaitToDelete) {
                    String states[] = tmpVcs.getState().split("/");
                    StatusUtils.Status st = null;
                    if (states.length - 2 >= 0) {
                        st = StatusUtils.getStatusByCode(dbEnv, states[states.length - 2]);
                    } else {
                        st = StatusUtils.getStatusByCode(dbEnv, states[states.length - 1]);
                    }
                    new ShelfTmpDao().updateStatus(dbEnv, tmpVcs.getTmpUuid().getUuid(), st.getStatusCode());
                    new ShelfTmpVcsDao().updateStatus(dbEnv, tmpVcs.getUuid(), st.getStatusCode(), StatusUtils.setStatus(tmpVcs.getState(), ValidUtils.null2NoData(st.getStatusCode())), header.has("remark") ? header.getString("remark") : null, false);
                    new ShelfTmpDetailDao().updateStatus(dbEnv, tmpVcs.getUuid(), st.getStatusCode());
                } else if (tmpVcs.getStatus() == statusWaitToApprove) {
                    new ShelfTmpDao().updateStatus(dbEnv, tmpVcs.getTmpUuid().getUuid(), statusReject);
                    new ShelfTmpVcsDao().updateStatus(dbEnv, tmpVcs.getUuid(), statusReject, StatusUtils.setStatus(tmpVcs.getState(), ValidUtils.null2NoData(statusReject)), header.has("remark") ? header.getString("remark") : null, false);
                    new ShelfTmpDetailDao().updateStatus(dbEnv, tmpVcs.getUuid(), statusReject);
                }
                return new JSONObject().put("status", true).put("description", "").put("confirmmsg", "");
            } else {

                logger.info("## ERROR : state to action is not define");
                return new JSONObject().put("status", false).put("description", StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0055")).put("confirmmsg", "");
            }
        } catch (NumberFormatException | JSONException | NullPointerException | HibernateException | SQLException e) {
//            logger.info("## ERROR saveOrUpdate");
//            e.printStackTrace();
            logger.info(e.getMessage());
            return new JSONObject().put("status", false).put("description", "" + e).put("confirmmsg", "");
        }
    }

    /* End status */
    private JSONArray getComponentList(String dbEnv) {
        JSONArray jsonArr = new JSONArray();
        try {
            List<ShelfComp> shelfComps = new ShelfCompDao().getListShelfComp(dbEnv);
            for (ShelfComp shelfComp : shelfComps) {
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("compUuid", shelfComp.getUuid())
                        //                    .put("code", shelfComp.getCompCode())
                        .put("name", shelfComp.getCompName())
                        .put("enable", false)
                        .put("data", new JSONObject())
                        //                    .put("data", (shelfComp.getValue() == null ? "" : shelfComp.getValue()))
                        .put("desc", shelfComp.getDescription());
                jsonArr.put(jsonObj);

            }
        } catch (JSONException | NullPointerException | NumberFormatException | HibernateException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
        }
        return jsonArr;
    }

    /* Update Or Create - start */
    private JSONObject saveOrUpdate(String dbEnv, JSONObject reqValue, String sysAction, int vscStatus) {
        HttpStatus httpStatus = HttpStatus.OK;
        JSONObject returnVal = new JSONObject().put("status", httpStatus.value()).put("description", "").put("confirmmsg", "");
        JSONObject saveTmpObj = new JSONObject();
        JSONObject saveVcsObj = new JSONObject();
        JSONObject saveAttObj;
//        JSONObject saveDetailObj;

        boolean flag = true;
        String errorMsg = "";
        try (Session session = getSessionMaster(dbEnv).openSession()) {

            JSONObject userInfo = reqValue.getJSONObject("userInfo");
            JSONObject companyInfo = userInfo.getJSONObject("company");
            String userName = userInfo.getString("username");
            String companyCode = companyInfo.getString("companyCode");
            String businessDept = companyInfo.getString("businessDept");
            String businessLine = companyInfo.getString("businessLine");

            JSONObject data = reqValue.getJSONObject("data");
            JSONObject header = data.getJSONObject("header");
            JSONArray info = data.getJSONArray("info");

            String tmpUuid = header.getString("tmpUuid").isEmpty() ? getUUID() : header.getString("tmpUuid");
            String tmpName = header.getString("name");
//            String code = header.getString("code");
            String vcsUuid = header.getString("vcsUuid").isEmpty() ? getUUID() : header.getString("vcsUuid");
            String action = header.getString("action");
            Date effectiveDate = convert2Date(header.getString("effectiveDate"));
            Date curdate = new Date();

            ShelfTmpVcs oldVcs = new ShelfTmpVcs();

            ShelfTmp tmp = null;
            ShelfTmpVcs vcs = new ShelfTmpVcs();
            ShelfTmpAttach att;
            ShelfTmpDetail detail;

            String actionStr = (sysAction.equals("newTemplate") ? "create" : "update");
            if (actionStr.equals("update")) {
                oldVcs = new ShelfTmpVcsDao().getListByUuid(dbEnv, vcsUuid);
            }
            /* Action to approve */
            if (action.equalsIgnoreCase("sendapprove") || action.equalsIgnoreCase("approve") || action.equalsIgnoreCase("reject") || action.equalsIgnoreCase("senddelete") || action.equalsIgnoreCase("delete")) {
//                ShelfTmpDetail shelfTmpDetail = new ShelfTmpDetailDao().getAttrByVcsUuid(dbEnv, vcsUuid);
                JSONObject getUpdateStatus = setStatusTemplate(dbEnv, action, reqValue, "");

                if (!getUpdateStatus.getBoolean("status")) {
                    returnVal.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value()).put("description", getUpdateStatus.getString("description")).put("confirmmsg", getUpdateStatus.getString("confirmmsg"));
                }
                return returnVal;
            }
            /* create new template or update value in template */
            if ((sysAction.equals("newTemplate") || sysAction.equals("update")) && action.equalsIgnoreCase("save")) {
                logger.info(actionStr + " Template ");
                tmp = new ShelfTmp();
                tmp.setUuid(tmpUuid);
                tmp.setTmpName(tmpName);
                tmp.setStatus(vscStatus);

                if (actionStr.equals("create")) {
                    tmp = createShelfTmp(tmpUuid, tmpName, vscStatus, userName, companyCode, businessDept, businessLine);
                    saveTmpObj = new ShelfTmpDao().saveTmp(session, tmp);
                } else if (actionStr.equals("update")) {
                    List<ShelfProductVcs> prodVcsList = new ShelfProductVcsDao().getProductByTmpUuidAndTmpVer(dbEnv, tmpUuid, oldVcs.getVersion());
                    if (!prodVcsList.isEmpty()) {
                        return new JSONObject().put("status", HttpStatus.INTERNAL_SERVER_ERROR.value()).put("description", StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0056")).put("productUsed", true).put("confirmmsg", "");
                    }
                    tmp = updateShelfTmp(oldVcs, tmpName, oldVcs.getStatus(), userName);
                    saveTmpObj = new ShelfTmpDao().updateTmp(session, tmp);
                }

                if (!saveTmpObj.getBoolean("status")) {
                    errorMsg = StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0058") + actionStr + " " + saveTmpObj.getString("description");
                    flag = false;
                }
            }

            /* สถานะเป็นสร้างใหม่ หรือ new version หรือ เป็นการ Update และ ต้องเป็นการ Save และ บันทึก Tmp สำเร็จ */
            if ((sysAction.equals("newTemplate") || sysAction.equals("newVersion") || sysAction.equals("update")) && action.equalsIgnoreCase("save") && (saveTmpObj.has("status") ? saveTmpObj.getBoolean("status") : true)) {
                if (actionStr.equals("create")) {
                    /* กรณี Create Version ใหม่ จะสร้าง Uuid ใหม่*/
                    if (sysAction.equals("newVersion")) {
                        vcsUuid = getUUID();
                    }
                    JSONObject retChkNewVer = checkCreateNewVersion(dbEnv, tmpUuid, effectiveDate, curdate, vcsUuid);
                    if (retChkNewVer.getInt("status") != httpStatus.OK.value()) {
                        if (!header.getBoolean("confirm")) {
                            return retChkNewVer;
                        }
                    }
                    vcs = createShelfTmpVcs(vcsUuid, tmpUuid, effectiveDate, vscStatus, userName);
                    saveVcsObj = new ShelfTmpVcsDao().saveVcs(session, vcs);
                    tmp.setCurrentVcsUuid(vcsUuid);
                    new ShelfTmpDao().updateTmp(session, tmp);
                } else if (actionStr.equals("update")) {
                    Integer statusInprogress = StatusUtils.getInprogress(dbEnv).getStatusCode();
                    List<ShelfTmpVcs> listInprogress = new ShelfTmpVcsDao().getListByTmpUuidAndStatus(dbEnv, tmpUuid, statusInprogress, vcsUuid);
                    if (listInprogress.size() > 0) {
                        return new JSONObject().put("status", HttpStatus.INTERNAL_SERVER_ERROR.value()).put("description", StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0057")).put("confirmmsg", "");
                    } else {
                        if (sysAction.equals("newVersion")) {
                            vcsUuid = getUUID();
                            JSONObject retChkNewVer = checkCreateNewVersion(dbEnv, tmpUuid, effectiveDate, curdate, vcsUuid);
                            if (retChkNewVer.getInt("status") != httpStatus.OK.value()) {
                                if (!header.getBoolean("confirm")) {
                                    return retChkNewVer;
                                }
                            }
                            vcs = createShelfTmpVcs(vcsUuid, tmpUuid, effectiveDate, vscStatus, userName);
                            saveVcsObj = new ShelfTmpVcsDao().saveVcs(session, vcs);
                            tmp = updateShelfTmp(oldVcs, tmpName, oldVcs.getStatus(), userName);
                            tmp.setCurrentVcsUuid(vcsUuid);
                            new ShelfTmpDao().updateTmp(session, tmp);

                        } else {
                            JSONObject retChkNewVer = checkCreateNewVersion(dbEnv, tmpUuid, effectiveDate, curdate, vcsUuid);
                            if (retChkNewVer.getInt("status") != httpStatus.OK.value()) {
                                if (!header.getBoolean("confirm")) {
                                    return retChkNewVer;
                                }
                            }
                            vcs = updateShelfTmpVcs(oldVcs, effectiveDate, vscStatus, userName);
                            saveVcsObj = new ShelfTmpVcsDao().updateVcs(session, vcs);
                        }
                    }
                }

                /* ตรวจสอบ การ บันทึก Version ลงฐานข้อมูล */
                if (saveVcsObj.getBoolean("status")) {
                    JSONArray compArr = new JSONArray();
                    for (int i = 0; i < info.length(); i++) {
                        JSONObject eachObj = info.getJSONObject(i);
                        int seqNo = eachObj.getInt("seq");
                        String compUuid = eachObj.getString("compUuid");
                        String detailUuid = (eachObj.has("detailUuid") ? eachObj.getString("detailUuid") : null);
                        boolean flagEnable = eachObj.getBoolean("enable");
                        JSONObject dataValue = eachObj.getJSONObject("data");

                        /* ตรวจสอบตำแหน่งในการเก็บในฐานข้อมูล เป็น 3rd(api) หรือ table ที่เกี่ยวข้อง*/
                        ShelfComp comp = new ShelfCompDao().getShelfCompByUUID(dbEnv, compUuid);
                        JSONObject compAction = (comp.getAttr10() != null ? new JSONObject(comp.getAttr10()) : new JSONObject()); // type to create
                        String type = "";
//                        String tbName = "";
                        String defaultStr = "";
                        if (compAction.has("type")) {
                            type = compAction.getString("type");
                        }
                        if (eachObj.getString("compCode").equalsIgnoreCase("004") || eachObj.getString("compCode").equalsIgnoreCase("006") && flagEnable) {
                            compArr.put(dataValue);
                        }
                        /* กรณี เป็น value หรือ api  */
                        if (actionStr.equals("create")) {
                            detail = createShelfTmpDetail(seqNo, compUuid, vcsUuid, flagEnable, vscStatus, userName);
                            if (type.equalsIgnoreCase("table")) { // table
//                                tbName = compAction.getString("value");
                                defaultStr = compAction.getString("default");
                                att = createShelfTmpAtt(tmpUuid, defaultStr, dataValue, effectiveDate, vscStatus, userName);
                                saveAttObj = new ShelfTmpAttDao().saveShelfTmpAttach(session, att);

                                if (!saveAttObj.getBoolean("status")) {
                                    errorMsg = StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0059") + actionStr + " " + saveAttObj.getString("description");
                                    flag = false;
                                }

                                detail.setAttUuid(new ShelfTmpAttach(att.getUuid()));
                            } else { // string and api
                                if (dataValue.length() > 0) {
                                    detail.setValue(dataValue.toString());
                                }
                            }
//                            saveDetailObj = new ShelfTmpDetailDao().saveShelfTmpDetail(session, detail);
                            new ShelfTmpDetailDao().saveShelfTmpDetail(session, detail);
                        } else if (actionStr.equals("update")) {
                            ShelfTmpDetail oldDetail;
                            if (detailUuid == null || detailUuid.isEmpty() || sysAction.equals("newVersion")) {
                                detail = createShelfTmpDetail(seqNo, compUuid, vcsUuid, flagEnable, vscStatus, userName);
                            } else {
                                oldDetail = new ShelfTmpDetailDao().getShelfTmpDetailByUUID(dbEnv, detailUuid);
                                detail = updateShelfTmpDetail(oldDetail, seqNo, flagEnable, vscStatus, userName);
                            }
                            if (type.equalsIgnoreCase("table")) { // table
//                                tbName = compAction.getString("value");
                                defaultStr = compAction.getString("default");
                                att = updateShelfTmpAtt(detail, defaultStr, dataValue, effectiveDate, vscStatus, userName);
                                saveAttObj = new ShelfTmpAttDao().updateShelfTmpAttach(session, att);

                                if (!saveAttObj.getBoolean("status")) {
                                    errorMsg = StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0059") + actionStr + " " + saveAttObj.getString("description");
                                    flag = false;
                                }

                                detail.setAttUuid(new ShelfTmpAttach(att.getUuid()));
//                                detail.setAttUuid(new ShelfTmpAttach(att.getUuid()));
                            } else { // string and api
                                if (dataValue.length() > 0) {
                                    detail.setValue(dataValue.toString());
                                }
                            }
//                            saveDetailObj = new ShelfTmpDetailDao().updateShelfTmpDetail(session, detail);
                            if (detailUuid == null || detailUuid.isEmpty() || sysAction.equals("newVersion")) {
                                new ShelfTmpDetailDao().saveShelfTmpDetail(session, detail);
                            } else {
                                new ShelfTmpDetailDao().updateShelfTmpDetail(session, detail);
                            }
                        }
                    }

                    //Update term&Condition and/or consent to ShelfTmpVcs for append to product
//                    ShelfTmpVcs shelfTmpVcs = new ShelfTmpVcsDao().getListByUuid(dbEnv, vcsUuid);
                    vcs.setAttr1(compArr.toString());
                    new ShelfTmpVcsDao().saveVcs(session, vcs);
                }
            }
            session.close();
            if (!flag) {
                return new JSONObject().put("status", HttpStatus.INTERNAL_SERVER_ERROR.value()).put("description", errorMsg).put("confirmmsg", "");
            } else {
                return returnVal;
            }

        } catch (HibernateException | JSONException | NullPointerException | ParseException e) {
//            logger.info("## ERROR saveOrUpdate");
            logger.info(e.getMessage());
            e.printStackTrace();
            return returnVal.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value()).put("description", "" + e);
        }
    }

    private ShelfTmp createShelfTmp(String tmpUuid, String tmpName, int vscStatus, String userName, String companyCode, String businessDept, String businessLine) {
        ShelfTmp tmp = new ShelfTmp();
        tmp.setUuid(tmpUuid);
        tmp.setTmpName(tmpName);
        tmp.setStatus(vscStatus);
        tmp.setCreateAt(new java.util.Date());
        tmp.setCreateBy(userName);
        tmp.setCompanyCode(companyCode);
        tmp.setBussinessDept(businessDept);
        tmp.setBusinessLine(businessLine);
        return tmp;
    }

    private ShelfTmp updateShelfTmp(ShelfTmpVcs oldVcs, String tmpName, int vscStatus, String userName) {
        ShelfTmp tmp = new ShelfTmp();
        tmp.setUuid(oldVcs.getTmpUuid().getUuid());
        tmp.setTmpName(tmpName);
        tmp.setStatus(vscStatus);
        tmp.setCompanyCode(oldVcs.getTmpUuid().getCompanyCode());
        tmp.setBussinessDept(oldVcs.getTmpUuid().getBussinessDept());
        tmp.setBusinessLine(oldVcs.getTmpUuid().getBusinessLine());
        tmp.setCreateAt(oldVcs.getTmpUuid().getCreateAt());
        tmp.setCreateBy(oldVcs.getTmpUuid().getCreateBy());
        tmp.setUpdateAt(new java.util.Date());
        tmp.setUpdateBy(userName);//updateUsername @update by
        return tmp;
    }

    private ShelfTmpVcs createShelfTmpVcs(String vcsUuid, String tmpUuid, Date effectiveDate, int vscStatus, String userName) {
        ShelfTmpVcs vcs = new ShelfTmpVcs();
        vcs.setUuid(vcsUuid);
        vcs.setTmpUuid(new ShelfTmp(tmpUuid));
        vcs.setEffectiveDate(effectiveDate);
        vcs.setVersion(0);
        vcs.setStatus(vscStatus);
        vcs.setState(ValidUtils.null2NoData(vscStatus));
        vcs.setCreateAt(new java.util.Date());
        vcs.setCreateBy(userName);
        return vcs;
    }

    private ShelfTmpVcs updateShelfTmpVcs(ShelfTmpVcs oldVcs, Date effectiveDate, int vscStatus, String userName) {
//        ShelfTmpVcs vcs = new ShelfTmpVcs();
//        oldVcs.setUuid(oldVcs.getUuid());
//        vcs.setTmpUuid(new ShelfTmp(oldVcs.getTmpUuid().getUuid()));
        oldVcs.setEffectiveDate(effectiveDate);
//        vcs.setVersion(oldVcs.getVersion());
        oldVcs.setStatus(vscStatus);
        oldVcs.setState(StatusUtils.setStatus(oldVcs.getState(), ValidUtils.null2NoData(vscStatus)));
//        vcs.setCreateAt(oldVcs.getCreateAt());
//        vcs.setCreateBy(oldVcs.getCreateBy());
        oldVcs.setUpdateAt(new java.util.Date());
        oldVcs.setUpdateBy(userName);
        return oldVcs;
    }

    private ShelfTmpAttach createShelfTmpAtt(String tmpUuid, String defaultStr, JSONObject data, Date effectiveDate, int vscStatus, String userName) {
        ShelfTmpAttach att = new ShelfTmpAttach();
        att.setUuid(getUUID());
        att.setTmpUuid(new ShelfTmp(tmpUuid));
        att.setType(defaultStr);
        att.setValue(data.toString());
        att.setEffectiveDate(effectiveDate);
        att.setStatus(vscStatus);
        att.setCreateAt(new java.util.Date());
        att.setCreateBy(userName);
        return att;
    }

    private ShelfTmpAttach updateShelfTmpAtt(ShelfTmpDetail oldDetail, String defaultStr, JSONObject data, Date effectiveDate, int vscStatus, String userName) {
        ShelfTmpAttach att = new ShelfTmpAttach();
        att.setUuid(oldDetail.getAttUuid().getUuid());
        att.setTmpUuid(new ShelfTmp(oldDetail.getAttUuid().getTmpUuid().getUuid()));
        att.setType(defaultStr);
        att.setValue(data.toString());
        att.setEffectiveDate(effectiveDate);
        att.setStatus(vscStatus);
        att.setCreateAt(oldDetail.getCreateAt());
        att.setCreateBy(oldDetail.getCreateBy());
        att.setUpdateAt(new java.util.Date());
        att.setUpdateBy(userName);
        return att;
    }

    private ShelfTmpDetail createShelfTmpDetail(int seqNo, String compUuid, String vcsUuid, boolean flagEnable, int vscStatus, String userName) {
        ShelfTmpDetail detail = new ShelfTmpDetail();
        detail.setUuid(getUUID());
        detail.setSeqNo(seqNo);
        detail.setCompUuid(new ShelfComp(compUuid));
        detail.setVcsUuid(new ShelfTmpVcs(vcsUuid));
        detail.setFlagEnable(flagEnable);
        detail.setStatus(vscStatus);
        detail.setCreateAt(new java.util.Date());
        detail.setCreateBy(userName);
        return detail;
    }

    private ShelfTmpDetail updateShelfTmpDetail(ShelfTmpDetail oldDetail, int seqNo, boolean flagEnable, int vscStatus, String userName) {
        ShelfTmpDetail detail = new ShelfTmpDetail();
        detail.setUuid(oldDetail.getUuid());
        detail.setSeqNo(seqNo);
        detail.setCompUuid(new ShelfComp(oldDetail.getCompUuid().getUuid()));
        detail.setVcsUuid(new ShelfTmpVcs(oldDetail.getVcsUuid().getUuid()));
        detail.setFlagEnable(flagEnable);
        detail.setStatus(vscStatus);
        detail.setCreateAt(oldDetail.getCreateAt());
        detail.setCreateBy(oldDetail.getCreateBy());
        detail.setUpdateAt(new java.util.Date());
        detail.setUpdateBy(userName);
        return detail;
    }

    private boolean approveTemplate(String dbEnv, String tmpUuid, String tmpVcsUuid) {
        ShelfTmpVcsDao tmpVcsDao = new ShelfTmpVcsDao();
        boolean retVal = true;
        ShelfTmpVcs vcs = tmpVcsDao.getListByUuid(dbEnv, tmpVcsUuid);
        Integer statusActive = StatusUtils.getActive(dbEnv).getStatusCode();
        Integer statusInActive = StatusUtils.getInActive(dbEnv).getStatusCode();
        Integer statusCancel = StatusUtils.getCancel(dbEnv).getStatusCode();
        Integer statusExpired = StatusUtils.getExpired(dbEnv).getStatusCode();
        List<ShelfTmpVcs> tmpVcsList = tmpVcsDao.getShelfTmpVcsByTmpUUID(dbEnv, tmpUuid, tmpVcsUuid);
        try (Session session = HibernateUtil.getSessionMaster(dbEnv).openSession()) {
            if (tmpVcsList.isEmpty()) {
                vcs.setStatus(statusActive);
                vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(statusActive)));
                tmpVcsDao.updateVcs(session, vcs);
            } else {
                for (ShelfTmpVcs tmpVcs : tmpVcsList) {
                    if (tmpVcs.getStatus() == statusInActive) {
                        tmpVcs.setStatus(statusCancel);
                        tmpVcs.setState(StatusUtils.setStatus(tmpVcs.getState(), ValidUtils.null2NoData(statusCancel)));
                        vcs.setStatus(statusInActive);
                        vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(statusInActive)));
                        tmpVcsDao.updateVcs(session, tmpVcs);
                        tmpVcsDao.updateVcs(session, vcs);
                    } else if (tmpVcs.getStatus() == statusActive) {
                        List<ShelfProductVcs> prodVcsList = new ShelfProductVcsDao().getProductByTmpUuidAndTmpVer(dbEnv, tmpUuid, vcs.getVersion());
                        if (prodVcsList.size() > 0) {
                            retVal = false;
                            break;
                        } else {
                            Date sysdate = new Date();
                            if (vcs.getEffectiveDate().compareTo(sysdate) == 0) {
                                tmpVcs.setStatus(statusExpired);
                                tmpVcs.setState(StatusUtils.setStatus(tmpVcs.getState(), ValidUtils.null2NoData(statusExpired)));
                                vcs.setStatus(statusActive);
                                vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(statusActive)));
                                tmpVcsDao.updateVcs(session, tmpVcs);
                                tmpVcsDao.updateVcs(session, vcs);
                            } else {
                                vcs.setStatus(statusInActive);
                                vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(statusInActive)));
                                tmpVcsDao.updateVcs(session, vcs);
                            }
                        }
                    }
                }
            }
        } catch (HibernateException | NullPointerException e) {
            logger.info(e.getMessage());
        }
        return retVal;
    }

    private boolean checkTmpVcsStatus(String dbEnv, String tmpUuid, Integer status, String tmpVcsUuid) {
        boolean retVal = true;
        List<ShelfTmpVcs> tmpVcsList = new ShelfTmpVcsDao().getListByTmpUuidAndStatus(dbEnv, tmpUuid, status, tmpVcsUuid);
        if (!tmpVcsList.isEmpty()) {
            retVal = false;
        }
        return retVal;
    }

    private boolean sendToApprove(String dbEnv, String tmpUuid, String tmpVcsUuid) {
        boolean retVal = true;
        ShelfTmpVcsDao tmpVcsDao = new ShelfTmpVcsDao();
        ShelfTmpVcs vcs = tmpVcsDao.getListByUuid(dbEnv, tmpVcsUuid);
        Integer statusActive = StatusUtils.getActive(dbEnv).getStatusCode();
        Integer statusInActive = StatusUtils.getInActive(dbEnv).getStatusCode();
        Integer statusCancel = StatusUtils.getCancel(dbEnv).getStatusCode();
        Integer statusExpired = StatusUtils.getExpired(dbEnv).getStatusCode();
        Integer statusWaitToApprove = StatusUtils.getWaittoApprove(dbEnv).getStatusCode();
        List<ShelfTmpVcs> tmpVcsList = tmpVcsDao.getShelfTmpVcsByTmpUUID(dbEnv, tmpUuid, tmpVcsUuid);
        try (Session session = HibernateUtil.getSessionMaster(dbEnv).openSession()) {
            if (tmpVcsList.isEmpty()) {
                vcs.setStatus(statusWaitToApprove);
                vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(statusWaitToApprove)));
                tmpVcsDao.updateVcs(session, vcs);
            } else {
                for (ShelfTmpVcs tmpVcs : tmpVcsList) {
                    if (tmpVcs.getStatus() == statusWaitToApprove) {
                        tmpVcs.setStatus(statusCancel);
                        tmpVcs.setState(StatusUtils.setStatus(tmpVcs.getState(), ValidUtils.null2NoData(statusCancel)));
                        vcs.setStatus(statusWaitToApprove);
                        vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(statusWaitToApprove)));
                        tmpVcsDao.updateVcs(session, tmpVcs);
                        tmpVcsDao.updateVcs(session, vcs);
                    }
                }
            }
        } catch (HibernateException | NullPointerException e) {
            logger.info(e.getMessage());
        }
        return retVal;
    }

    private JSONObject checkCreateNewVersion(String dbEnv, String tmpUuid, Date effectiveDate, Date curDate, String tmpVcsUuid) throws ParseException {
        JSONObject ret = new JSONObject().put("status", HttpStatus.OK.value()).put("description", "").put("confirmmsg", "");
        Integer statusActive = StatusUtils.getActive(dbEnv).getStatusCode();
        Integer statusInactive = StatusUtils.getInActive(dbEnv).getStatusCode();
        Integer statusInprogress = StatusUtils.getInprogress(dbEnv).getStatusCode();
        Integer statusPause = StatusUtils.getPause(dbEnv).getStatusCode();
        Integer statusWaitToApprove = StatusUtils.getWaittoApprove(dbEnv).getStatusCode();
        Integer statusWaitToDelete = StatusUtils.getWaittoDelete(dbEnv).getStatusCode();
        List statusProductIn = new ArrayList();
        statusProductIn.add(statusActive);
        statusProductIn.add(statusInactive);
        statusProductIn.add(statusInprogress);
        statusProductIn.add(statusPause);
        statusProductIn.add(statusWaitToApprove);
        statusProductIn.add(statusWaitToDelete);
        List<ShelfProductVcs> prodVcsList = new ShelfProductVcsDao().getProductByTmpUuid(dbEnv, tmpUuid, statusProductIn);
        if (prodVcsList.size() > 0) {
            for (ShelfProductVcs prodVcs : prodVcsList) {
                if (prodVcs.getStatus() == statusActive && DateUtils.isSameDay(effectiveDate, curDate)) {
                    return ret.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value()).put("description", StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0060"));
                }
            }
        } else {
            List<ShelfTmpVcs> listActive = new ShelfTmpVcsDao().getListByTmpUuidAndStatus(dbEnv, tmpUuid, statusActive, tmpVcsUuid);
            if (DateUtils.isSameDay(effectiveDate, curDate) && listActive.size() > 0) {
                return ret.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value()).put("confirmmsg", StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0061"));
            }
            effectiveDate = ValidUtils.str2Date(th.co.d1.digitallending.util.DateUtils.getDisplayEnDate(effectiveDate, "yyyy-MM-dd"), "yyyy-MM-dd");
            curDate = ValidUtils.str2Date(th.co.d1.digitallending.util.DateUtils.getDisplayEnDate(curDate, "yyyy-MM-dd"), "yyyy-MM-dd");
            List<ShelfTmpVcs> listInActive = new ShelfTmpVcsDao().getListByTmpUuidAndStatus(dbEnv, tmpUuid, statusInactive, tmpVcsUuid);
            if (effectiveDate.compareTo(curDate) > 0 && listInActive.size() > 0) {
                return ret.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value()).put("confirmmsg", StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0062"));
            }
        }
        return ret;
    }

    private JSONObject checkTemplate(String dbEnv, JSONObject data) {
        JSONObject ret = new JSONObject().put("status", HttpStatus.OK.value()).put("description", "").put("confirmmsg", "").put("data", new JSONObject()).put("api", true);
        Integer statusActive = StatusUtils.getActive(dbEnv).getStatusCode();
        Integer statusInActive = StatusUtils.getInActive(dbEnv).getStatusCode();
        Integer statusWaitToApprove = StatusUtils.getWaittoApprove(dbEnv).getStatusCode();
        String vcsUuid = data.getString("vcsUuid");
        ShelfTmpVcs tmpVcs = new ShelfTmpVcsDao().getListByUuid(dbEnv, vcsUuid);
        boolean hasTermCond = false;
        for (ShelfTmpDetail tmpDtl : tmpVcs.getShelfTmpDetailList()) {
            if (null != tmpDtl.getCompUuid() && "004".equalsIgnoreCase(tmpDtl.getCompUuid().getCompCode()) && tmpDtl.getFlagEnable()) {
                hasTermCond = true;
            }
        }
        if (!hasTermCond) {
            JSONObject retData = new JSONObject();
            retData.put("tmpUuid", tmpVcs.getTmpUuid().getUuid());
            retData.put("tmpName", tmpVcs.getTmpUuid().getTmpName());
            retData.put("headerText", "Terms & Condition");
            JSONArray attrArr = new JSONArray(tmpVcs.getAttr1());
            for (int aai = 0; aai < attrArr.length(); aai++) {
                JSONObject obj = attrArr.getJSONObject(aai);
                if (obj.has("termsNCondition")) {
                    retData.put("activeDate", obj.getString("activeDate"));
                    retData.put("verTermcond", obj.getString("version"));
                    retData.put("content", obj.getString("termsNCondition"));
                }
            }
            ret.put("data", retData).put("api", false);
            return ret;
        }
        List<ShelfTmpVcs> shelfTmpVcsList = new ShelfTmpVcsDao().getShelfTmpVcsByTmpUUID(dbEnv, tmpVcs.getTmpUuid().getUuid(), vcsUuid);
        Date sysdate = new Date();
        if (shelfTmpVcsList.isEmpty()) {
            if (tmpVcs.getStatus() != statusWaitToApprove) {
                return ret.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value()).put("description", StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0063")).put("confirmmsg", "");
            } else {
                JSONObject retData = new JSONObject();
                retData.put("tmpUuid", tmpVcs.getTmpUuid().getUuid());
                retData.put("tmpName", tmpVcs.getTmpUuid().getTmpName());
                retData.put("headerText", "Terms & Condition");
                JSONArray attrArr = new JSONArray(tmpVcs.getAttr1());
                for (int aai = 0; aai < attrArr.length(); aai++) {
                    JSONObject obj = attrArr.getJSONObject(aai);
                    if (obj.has("termsNCondition")) {
                        retData.put("activeDate", obj.getString("activeDate"));
                        retData.put("verTermcond", obj.getString("version"));
                        retData.put("content", obj.getString("termsNCondition"));
                    }
                }
                ret.put("data", retData);
            }
        } else {
            if (tmpVcs.getStatus() == statusWaitToApprove) {
                Integer vcsStatus = statusActive;
                for (ShelfTmpVcs vcs : shelfTmpVcsList) {
                    JSONObject retData = new JSONObject();
                    retData.put("tmpUuid", vcs.getTmpUuid().getUuid());
                    retData.put("tmpName", vcs.getTmpUuid().getTmpName());
                    retData.put("headerText", "Terms & Condition");
                    JSONArray attrArr = new JSONArray(vcs.getAttr1());
                    for (int aai = 0; aai < attrArr.length(); aai++) {
                        JSONObject obj = attrArr.getJSONObject(aai);
                        if (obj.has("termsNCondition")) {
                            retData.put("activeDate", obj.getString("activeDate"));
                            retData.put("verTermcond", obj.getString("version"));
                            retData.put("content", obj.getString("termsNCondition"));
                        }
                    }
                    if (vcs.getStatus() == statusActive) {
                        if (tmpVcs.getEffectiveDate().compareTo(sysdate) <= 0) {
                            return ret.put("status", HttpStatus.OK.value()).put("confirmmsg", StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0064")).put("description", "").put("data", retData);
                        }
                    } else if (vcs.getStatus() == statusInActive) {
                        if (tmpVcs.getEffectiveDate().compareTo(sysdate) > 0) {
                            return ret.put("status", HttpStatus.OK.value()).put("confirmmsg", StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0065")).put("description", "").put("data", retData);
                        }
                    }
                    ret.put("data", retData);
                }
            } else {
                return ret.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value()).put("description", StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0066")).put("confirmmsg", "");
            }
        }
        return ret;
    }
}
