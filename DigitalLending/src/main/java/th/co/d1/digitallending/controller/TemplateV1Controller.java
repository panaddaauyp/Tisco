package th.co.d1.digitallending.controller;

import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.web.bind.annotation.ResponseBody;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import static th.co.d1.digitallending.util.Utils.*;
import static th.co.d1.digitallending.util.HttpUtil.*;

import th.co.d1.digitallending.entity.ShelfComp;

import th.co.d1.digitallending.dao.ShelfCompDao;
import th.co.d1.digitallending.dao.ShelfProductVcsDao;
import th.co.d1.digitallending.dao.ShelfTmpAttDao;
import th.co.d1.digitallending.dao.ShelfTmpDao;
import th.co.d1.digitallending.dao.ShelfTmpDetailDao;
import th.co.d1.digitallending.dao.ShelfTmpVcsDao;
import th.co.d1.digitallending.dao.SysLookupDao;
import th.co.d1.digitallending.entity.Memlookup;
import th.co.d1.digitallending.entity.ShelfProductVcs;
import th.co.d1.digitallending.entity.ShelfTmp;
import th.co.d1.digitallending.entity.ShelfTmpAttach;
import th.co.d1.digitallending.entity.ShelfTmpDetail;
import th.co.d1.digitallending.entity.ShelfTmpVcs;
import static th.co.d1.digitallending.util.ApplicationStartup.headersJSON;
import th.co.d1.digitallending.util.HibernateUtil;
import static th.co.d1.digitallending.util.HibernateUtil.getSessionMaster;
import th.co.d1.digitallending.util.StatusUtils;
import th.co.d1.digitallending.util.TemplateUtils;
import th.co.d1.digitallending.util.Utils;
import th.co.d1.digitallending.util.ValidUtils;

@Controller
@RequestMapping("/shelf/template/v1")
public class TemplateV1Controller {

//    boolean premission = true;
    Logger logger = Logger.getLogger(TemplateV1Controller.class);

    /*first create template must get tmpUuid , vscUuid form api*/
    @RequestMapping(value = "component", method = GET)
    @ResponseBody
    public ResponseEntity<?> getComponent(HttpSession session, HttpServletResponse response, HttpServletRequest request) {
        logger.info("GET : /shelf/template/v1/component");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
        try {
            returnVal.put("data", new JSONObject()
                    .put("tmpUuid", getUUID())
                    .put("vcsUuid", getUUID())
                    .put("componentList", getComponentList(Utils.validateSubStateFromHeader(request))));

        } catch (JSONException | NullPointerException e) {
            logger.error("" + e);
            e.printStackTrace();
            returnVal.put("status", 500).put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @RequestMapping(value = "list", method = GET)
    @ResponseBody
    public ResponseEntity<?> getTemplate(HttpSession session, HttpServletResponse response, HttpServletRequest request) {
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
        logger.info("GET : /shelf/template/v1/list");
        try {
            returnVal.put("data", new TemplateUtils().getTemplateList(Utils.validateSubStateFromHeader(request)));
        } catch (JSONException | NullPointerException e) {
            logger.error("" + e);
            e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);

        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @RequestMapping(value = "info/{vcsUuid}", method = GET)
    @ResponseBody
    public ResponseEntity<?> getInfoByUuid(HttpSession session, HttpServletResponse response, HttpServletRequest request, @PathVariable String vcsUuid) {
        logger.info("GET : /shelf/template/v1/info/" + vcsUuid);
        HttpStatus httpStatus = HttpStatus.OK;
        JSONObject returnVal = new JSONObject().put("status", httpStatus).put("description", "").put("data", new JSONObject());
        try {
            ShelfTmpVcs shelfTmpVcs = new ShelfTmpVcsDao().getListByUuid(Utils.validateSubStateFromHeader(request), vcsUuid);
            int intStats = shelfTmpVcs.getStatus();
            JSONObject getStatusValue = getActionFromStatus(Utils.validateSubStateFromHeader(request), intStats);
//            String strStatus = new SysLookupDao().getMemLookupByCode(Utils.validateSubStateFromHeader(request), String.valueOf(shelfTmpVcs.getStatus())).getLookupnameen();

//        JSONObject userInfo = new JSONObject()
//                .put("userName",shelfTmpVcs.getCreateBy())
//                
//                .put("company", new JSONObject()
//                        .put("companyCode",shelfTmpVcs.getTmpUuid().getCompanyCode())
//                        .put("businessDept",shelfTmpVcs.getTmpUuid().getBussinessDept())
//                        .put("businessLine",shelfTmpVcs.getTmpUuid().getBusinessLine())
//                    );
//        logger.info(checkPermission("template","create","maker"));
            Memlookup memLookup = new SysLookupDao().getMemLookupByCode(Utils.validateSubStateFromHeader(request), ValidUtils.null2NoData(shelfTmpVcs.getStatus()));
            JSONObject header = new JSONObject()
                    .put("tmpUuid", shelfTmpVcs.getTmpUuid().getUuid())
                    .put("vcsUuid", shelfTmpVcs.getUuid())
                    .put("name", shelfTmpVcs.getTmpUuid().getTmpName())
                    .put("effectiveDate", shelfTmpVcs.getEffectiveDate())
                    .put("enable", true)
                    .put("readOnly", getStatusValue.has("readOnly") ? getStatusValue.getBoolean("readOnly") : "") // check status 
                    .put("action", "")
                    .put("status", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupcode()) : "")
                    .put("statusNameTh", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupnameth()) : "")
                    .put("statusNameEn", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupnameen()) : "")
                    .put("createDate", shelfTmpVcs.getCreateAt())
                    .put("createDate", shelfTmpVcs.getCreateBy())
                    .put("updateDate", (shelfTmpVcs.getUpdateAt() == null ? "" : shelfTmpVcs.getUpdateAt()))
                    .put("updateBy", (shelfTmpVcs.getUpdateBy() == null ? "" : shelfTmpVcs.getUpdateBy())
                    );

            JSONArray infoData = new JSONArray();
            for (int i = 0; i < shelfTmpVcs.getShelfTmpDetailList().size(); i++) {
                ShelfTmpDetail detail = shelfTmpVcs.getShelfTmpDetailList().get(i);
                JSONObject eachDetail = new JSONObject().put("seq", detail.getSeqNo())
                        .put("compUuid", detail.getCompUuid().getUuid())
                        .put("detailUuid", detail.getUuid())
                        .put("compCode", detail.getCompUuid().getCompCode())
                        .put("enable", detail.getFlagEnable());

                if (detail.getCompUuid().getCompCode().equalsIgnoreCase("004")) {
                    if (shelfTmpVcs.getAttr1() != null && !shelfTmpVcs.getAttr1().isEmpty()) {
                        JSONArray attrArr = new JSONArray(shelfTmpVcs.getAttr1());
//                        System.out.println("004 : " + shelfTmpVcs.getAttr1());
                        for (int aai = 0; aai < attrArr.length(); aai++) {
                            JSONObject obj = attrArr.getJSONObject(aai);
//                            if (detail.getCompUuid().getUuid().equalsIgnoreCase(obj.getString("compUuid"))) {
                            if (obj.has("termsNCondition")) {
                                eachDetail.put("data", obj);
                            }
                        }
                    }
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
                    .put("info", infoData);
            returnVal.put("data", value);
        } catch (JSONException | NullPointerException e) {
            logger.error("" + e);
            e.printStackTrace();
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    /* create new template */
    @RequestMapping(value = "save", method = POST)
    @ResponseBody
    public ResponseEntity<?> setTemplate(HttpSession session, HttpServletResponse response, HttpServletRequest request) {
        JSONObject returnVal = new JSONObject();
        logger.info("POST : /shelf/template/v1/save");
        HttpStatus httpStatus = HttpStatus.OK;
        try {
            JSONObject reqValue = getPostParam(request);

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
            System.out.println("data : " + data);
            JSONObject header = data.getJSONObject("header");
            String action = header.getString("action");
            int vscStatus = getStatusByAction(Utils.validateSubStateFromHeader(request), action);
            if (vscStatus != 0) {
                returnVal = saveOrUpdate(Utils.validateSubStateFromHeader(request), reqValue, "newTemplate", vscStatus);
            } else {
                httpStatus = HttpStatus.BAD_REQUEST;
                returnVal.put("status", httpStatus.value()).put("description", "action is request Field");
            }
        } catch (JSONException | NullPointerException e) {
            logger.error("" + e);
            e.printStackTrace();
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    /* create new version or Update exsit version */
    @RequestMapping(value = "save", method = PUT)
    @ResponseBody
    public ResponseEntity<?> putTemplate(HttpSession session, HttpServletResponse response, HttpServletRequest request) {
        JSONObject returnVal = new JSONObject();
        logger.info("PUT : /shelf/template/v1/save");
        HttpStatus httpStatus = HttpStatus.OK;
        try {
            JSONObject reqValue = getPostParam(request);
            System.out.println("reqValue : " + reqValue);
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
            JSONObject actionFlag = checkActionTemplateStatus(Utils.validateSubStateFromHeader(request), reqValue);

            if (actionFlag.getBoolean("flag") && actionFlag.getInt("vcsStatus") != 0) {
                returnVal = saveOrUpdate(Utils.validateSubStateFromHeader(request), reqValue, actionFlag.getString("sysAction"), actionFlag.getInt("vcsStatus"));
            } else {
                httpStatus = HttpStatus.BAD_REQUEST;
                logger.info("## ERROR : " + actionFlag.getString("description"));
                returnVal.put("status", httpStatus.value()).put("description", actionFlag.getString("description"));
            }
        } catch (JSONException | NullPointerException e) {
            logger.error("" + e);
            e.printStackTrace();
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, httpStatus));
    }

    @RequestMapping(value = "pending/{reqStatus}", method = PUT)
    @ResponseBody
    public ResponseEntity<?> approveTmpVsc(HttpSession session, HttpServletResponse response, HttpServletRequest request, @PathVariable String reqStatus) {

        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "");
        logger.info("PUT : /shelf/template/v1/pending/" + reqStatus);
        HttpStatus httpStatus = HttpStatus.OK;
        JSONObject reqValue = getPostParam(request);
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
//            String tmpName = header.getString("name");
//            String tmpUuid = header.getString("tmpUuid");
            String vcsUuid = header.getString("vcsUuid");

            ShelfTmpDetail shelfTmpDetail = new ShelfTmpDetailDao().getAttrByVcsUuid(Utils.validateSubStateFromHeader(request), vcsUuid);
            JSONObject getUpdateStatus = setStatusTemplate(Utils.validateSubStateFromHeader(request), reqStatus, reqValue, shelfTmpDetail.getAttUuid().getUuid());

            if (!getUpdateStatus.getBoolean("status")) {
                httpStatus = HttpStatus.BAD_REQUEST;
                returnVal.put("status", httpStatus.value()).put("description", getUpdateStatus.getString("description"));
            }
        } catch (JSONException | NullPointerException e) {
            logger.error("" + e);
            e.printStackTrace();
        }
        return (new ResponseEntity<>(reqValue.toString(), headersJSON, httpStatus));

    }

    @RequestMapping(value = "list/active", method = GET)
    @ResponseBody
    public ResponseEntity<?> getActiveTemplate(HttpSession session, HttpServletResponse response, HttpServletRequest request) {
        Integer statusActive = StatusUtils.getActive(Utils.validateSubStateFromHeader(request)).getStatusCode();
        JSONArray returnVal = new TemplateUtils().getTemplateListByStatus(Utils.validateSubStateFromHeader(request), statusActive);
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @RequestMapping(value = "list/{status}", method = GET)
    @ResponseBody
    public ResponseEntity<?> getTemplateListByStatus(HttpSession session, HttpServletResponse response, HttpServletRequest request, @PathVariable int status) {
        JSONArray returnVal = new TemplateUtils().getTemplateListByStatus(Utils.validateSubStateFromHeader(request), status);
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @RequestMapping(value = "list/product", method = GET)
    @ResponseBody
    public ResponseEntity<?> getTemplateListAndProductUsage(HttpSession session, HttpServletResponse response, HttpServletRequest request) {
        JSONArray returnVal = new ShelfTmpDao().getTemplateListAndProductUsage(Utils.validateSubStateFromHeader(request));
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @RequestMapping(value = "search", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> searchTemplage(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String payload) {
        logger.info("POST : /shelf/template/v1/search");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
        try {
            JSONObject json = new JSONObject(payload);
            String templateName = json.has("templateName") ? json.getString("templateName") : "";
            Integer status = json.has("status") ? ValidUtils.str2BigInteger(ValidUtils.obj2String(json.get("status"))) : null;
            Date startCreateDate = ValidUtils.str2Date(json.has("startCreateDate") ? (json.getString("startCreateDate").isEmpty() ? null : json.getString("startCreateDate")) : null);
            Date endCreateDate = ValidUtils.str2Date(json.has("endCreateDate") ? (json.getString("endCreateDate").isEmpty() ? null : json.getString("endCreateDate")) : null);
            Date startUpdateDate = ValidUtils.str2Date(json.has("startUpdateDate") ? (json.getString("startUpdateDate").isEmpty() ? null : json.getString("startUpdateDate")) : null);
            Date endUpdateDate = ValidUtils.str2Date(json.has("endUpdateDate") ? (json.getString("endUpdateDate").isEmpty() ? null : json.getString("endUpdateDate")) : null);
            Date effectiveDateFrom = ValidUtils.str2Date(json.has("effectiveDateFrom") ? (json.getString("effectiveDateFrom").isEmpty() ? null : json.getString("effectiveDateFrom")) : null);
            Date effectiveDateTo = ValidUtils.str2Date(json.has("effectiveDateTo") ? (json.getString("effectiveDateTo").isEmpty() ? null : json.getString("effectiveDateTo")) : null);
            returnVal.put("data", new ShelfTmpVcsDao().searchShelfTemplate(Utils.validateSubStateFromHeader(request), templateName, status, startCreateDate, endCreateDate, startUpdateDate, endUpdateDate, effectiveDateFrom, effectiveDateTo));
        } catch (JSONException | NullPointerException e) {
            logger.error("" + e);
            e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);

        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @RequestMapping(value = "checkTemplateName", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> checkTemplateName(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String payload) {
        logger.info("POST : /shelf/theme/v1/checkTemplateName");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
        try {
            JSONObject json = new JSONObject(payload);
            String templateName = json.has("templateName") ? json.getString("templateName") : "";
            if (!templateName.trim().isEmpty()) {
                List<ShelfTmpVcs> templateVcs = new ShelfTmpVcsDao().getShelfTemplateByNameStatusActiveAndInActive(Utils.validateSubStateFromHeader(request), templateName);
                if (templateVcs.isEmpty()) {
                    returnVal.put("data", new JSONObject().put("stauts", true).put("description", "You can use this name."));
                } else {
                    returnVal.put("data", new JSONObject().put("stauts", false).put("description", "Template name is used."));
                }
            } else {
                returnVal.put("status", 400)
                        .put("description", "Template name is empty.");
            }
        } catch (JSONException | NullPointerException e) {
            logger.error("" + e);
            e.printStackTrace();
            returnVal.put("status", 500)
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
                returnVal.put("status", httpStatus.value()).put("description", "state to action is not define");
            }

        } catch (NumberFormatException | JSONException | NullPointerException e) {
            logger.error("" + e);
            e.printStackTrace();
        }

        return returnVal;
    }

    private JSONObject checkActionTemplateStatus(String dbEnv, JSONObject reqValue) {

        logger.info("checkActionTemplateStatus ");
        JSONObject returnVal = new JSONObject().put("readOnly", false);
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
            if (flagAction.getBoolean("edit")) {
                returnVal.put("flag", true)
                        .put("sysAction", "update")
                        .put("vcsStatus", vcsStatus)
                        .put("description", "");
            } else if (flagAction.getBoolean("create") || (action.equalsIgnoreCase("approve") || action.equalsIgnoreCase("reject"))) {
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
        return returnVal;
    }

    private JSONObject getActionFromStatus(String dbEnv, int vcsStatus) {
        JSONObject returnVal = new JSONObject().put("readOnly", false);
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

        return returnVal;
    }

    private int getStatusByAction(String dbEnv, String action) {
        int vcsStatus = 0;
        if (action.equalsIgnoreCase("save")) {
            vcsStatus = Integer.parseInt(new SysLookupDao().getMemLookupByValue(dbEnv, "inprogress").getLookupcode());
        } else if (action.equalsIgnoreCase("sendapprove")) {
            vcsStatus = Integer.parseInt(new SysLookupDao().getMemLookupByValue(dbEnv, "waittoapprove").getLookupcode());
        } else if (action.equalsIgnoreCase("approve")) {
            vcsStatus = Integer.parseInt(new SysLookupDao().getMemLookupByValue(dbEnv, "active").getLookupcode());
        } else if (action.equalsIgnoreCase("reject")) {
            vcsStatus = Integer.parseInt(new SysLookupDao().getMemLookupByValue(dbEnv, "reject").getLookupcode());
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
            if (reqStatus.equalsIgnoreCase("approve")) {
                if (shelfTmpVcsList.isEmpty()) {
                    Date sysdate = new Date();
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
                    /*
                    new ShelfTmpDao().updateStatus(dbEnv, tmpUuid, statusActive);
                    new ShelfTmpVcsDao().updateStatus(dbEnv, vcsUuid, statusActive, StatusUtils.setStatus(tmpVcs.getState(), ValidUtils.null2NoData(statusActive)), "", true);
//                    new ShelfTmpAttDao().updateStatus(dbEnv, attUuid, statusActive);
                    new ShelfTmpDetailDao().updateStatus(dbEnv, vcsUuid, statusActive);
                     */
                } else {
                    for (ShelfTmpVcs vcs : shelfTmpVcsList) {
                        if (vcs.getStatus() == statusInActive) {
                            if (header.getBoolean("confirm")) {
                                new ShelfTmpDao().updateStatus(dbEnv, vcs.getTmpUuid().getUuid(), statusCancel);
                                new ShelfTmpVcsDao().updateStatus(dbEnv, vcs.getUuid(), statusCancel, StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(statusCancel)), "", false);
//                            new ShelfTmpAttDao().updateStatus(dbEnv, vcs.get, statusCancel);
                                new ShelfTmpDetailDao().updateStatus(dbEnv, vcs.getUuid(), statusCancel);

                                new ShelfTmpDao().updateStatus(dbEnv, tmpUuid, statusInActive);
                                new ShelfTmpVcsDao().updateStatus(dbEnv, vcsUuid, statusInActive, StatusUtils.setStatus(tmpVcs.getState(), ValidUtils.null2NoData(statusInActive)), "", true);
//                            new ShelfTmpAttDao().updateStatus(dbEnv, vcs.get, statusInActive);
                                new ShelfTmpDetailDao().updateStatus(dbEnv, vcsUuid, statusInActive);
                            } else {
                                return new JSONObject().put("status", false).put("description", "This template have some version status is inactive");
                            }
                        } else if (vcs.getStatus() == statusActive) {
                            List<ShelfProductVcs> prodVcsList = new ShelfProductVcsDao().getProductByTmpUuidAndTmpVerNotCancelAndDelete(dbEnv, tmpUuid, tmpVcs.getVersion());
                            if (!prodVcsList.isEmpty()) {
                                return new JSONObject().put("status", false).put("description", "Some product used this template. Please pause product and repeat.");
                            } else {
                                Date sysdate = new Date();
                                if (tmpVcs.getEffectiveDate().compareTo(sysdate) <= 0) {
                                    new ShelfTmpDao().updateStatus(dbEnv, vcs.getTmpUuid().getUuid(), statusExpired);
                                    new ShelfTmpVcsDao().updateStatus(dbEnv, vcs.getUuid(), statusExpired, StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(statusExpired)), "", false);
//                            new ShelfTmpAttDao().updateStatus(dbEnv, vcs.get, statusCancel);
                                    new ShelfTmpDetailDao().updateStatus(dbEnv, vcs.getUuid(), statusExpired);

                                    new ShelfTmpDao().updateStatus(dbEnv, tmpUuid, statusActive);
                                    new ShelfTmpVcsDao().updateStatus(dbEnv, vcsUuid, statusActive, StatusUtils.setStatus(tmpVcs.getState(), ValidUtils.null2NoData(statusActive)), "", true);
//                            new ShelfTmpAttDao().updateStatus(dbEnv, vcs.get, statusInActive);
                                    new ShelfTmpDetailDao().updateStatus(dbEnv, vcsUuid, statusActive);
                                } else {
                                    new ShelfTmpDao().updateStatus(dbEnv, tmpUuid, statusInActive);
                                    new ShelfTmpVcsDao().updateStatus(dbEnv, vcsUuid, statusInActive, StatusUtils.setStatus(tmpVcs.getState(), ValidUtils.null2NoData(statusInActive)), "", true);
//                            new ShelfTmpAttDao().updateStatus(dbEnv, vcs.get, statusInActive);
                                    new ShelfTmpDetailDao().updateStatus(dbEnv, vcsUuid, statusInActive);
                                }
                            }
                        }
                    }
                }
                return new JSONObject().put("status", true).put("description", "");
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
                            return new JSONObject().put("status", false).put("description", "This template have some version status is wait to approve");
                        } else if (vcs.getStatus() == statusInActive) {
                            if (header.getBoolean("confirm")) {
                                new ShelfTmpDao().updateStatus(dbEnv, tmpUuid, statusWaitToApprove);
                                new ShelfTmpVcsDao().updateStatus(dbEnv, vcsUuid, statusWaitToApprove, StatusUtils.setStatus(tmpVcs.getState(), ValidUtils.null2NoData(statusWaitToApprove)), "", false);
//                            new ShelfTmpAttDao().updateStatus(dbEnv, vcs.get, statusInActive);
                                new ShelfTmpDetailDao().updateStatus(dbEnv, vcsUuid, statusWaitToApprove);
                            } else {
                                return new JSONObject().put("status", false).put("description", "This template have some version status is inactive");
                            }
                        }
                    }
                }
                return new JSONObject().put("status", true).put("description", "");
            } else if (reqStatus.equalsIgnoreCase("reject")) {
                new ShelfTmpDao().updateStatus(dbEnv, tmpVcs.getTmpUuid().getUuid(), statusReject);
                new ShelfTmpVcsDao().updateStatus(dbEnv, tmpVcs.getUuid(), statusReject, StatusUtils.setStatus(tmpVcs.getState(), ValidUtils.null2NoData(statusReject)), header.getString("remark"), false);
//                            new ShelfTmpAttDao().updateStatus(dbEnv, vcs.get, statusCancel);
                new ShelfTmpDetailDao().updateStatus(dbEnv, tmpVcs.getUuid(), statusReject);
                return new JSONObject().put("status", true).put("description", "");
            } else {

                logger.info("## ERROR : state to action is not define");
                return new JSONObject().put("status", false).put("description", "state to action is not define");
            }
        } catch (NumberFormatException | JSONException | NullPointerException e) {
//            logger.info("## ERROR saveOrUpdate");
//            e.printStackTrace();
            logger.error("" + e);
            e.printStackTrace();
            return new JSONObject().put("status", false).put("description", "" + e);
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
        } catch (JSONException | NullPointerException e) {
            logger.error("" + e);
            e.printStackTrace();
        }
        return jsonArr;
    }

    /* Update Or Create - start */
    private JSONObject saveOrUpdate(String dbEnv, JSONObject reqValue, String sysAction, int vscStatus) {
        HttpStatus httpStatus = HttpStatus.OK;
        JSONObject returnVal = new JSONObject().put("status", HttpStatus.OK).put("description", "");
        JSONObject saveTmpObj = new JSONObject();
        JSONObject saveVcsObj = new JSONObject();
        JSONObject saveAttObj;
//        JSONObject saveDetailObj;

        boolean flag = true;
        String errorMsg = "";
        Session session = null;
        try {

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
            if (action.equalsIgnoreCase("sendapprove") || action.equalsIgnoreCase("approve") || action.equalsIgnoreCase("reject")) {
//                ShelfTmpDetail shelfTmpDetail = new ShelfTmpDetailDao().getAttrByVcsUuid(dbEnv, vcsUuid);
                JSONObject getUpdateStatus = setStatusTemplate(dbEnv, action, reqValue, "");

                if (!getUpdateStatus.getBoolean("status")) {
                    returnVal.put("status", httpStatus.value()).put("description", getUpdateStatus.getString("description"));
                } else {

                }
                return returnVal;
            }

            session = getSessionMaster(dbEnv).openSession();
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
                        return new JSONObject().put("status", false).put("description", "Some product used this template. Please pause product and repeat.");
                    }
                    tmp = updateShelfTmp(oldVcs, tmpName, vscStatus, userName);
                    saveTmpObj = new ShelfTmpDao().updateTmp(session, tmp);
                }

                if (!saveTmpObj.getBoolean("status")) {
                    errorMsg = "Table ShelfTmp can't " + actionStr + " " + saveTmpObj.getString("description");
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
                    vcs = createShelfTmpVcs(vcsUuid, tmpUuid, effectiveDate, vscStatus, userName);
                    saveVcsObj = new ShelfTmpVcsDao().saveVcs(session, vcs);
                    tmp.setCurrentVcsUuid(vcsUuid);
                    new ShelfTmpDao().updateTmp(session, tmp);
                } else if (actionStr.equals("update")) {
                    vcs = updateShelfTmpVcs(oldVcs, effectiveDate, vscStatus, userName);
                    saveVcsObj = new ShelfTmpVcsDao().updateVcs(session, vcs);
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
                                    errorMsg = "Table ShelfTmpAtt can't " + actionStr + " " + saveAttObj.getString("description");
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
                                    errorMsg = "Table ShelfTmpAtt can't " + actionStr + " " + saveAttObj.getString("description");
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
                return new JSONObject().put("status", 500).put("description", errorMsg);
            } else {
                return returnVal;
            }

        } catch (HibernateException | JSONException | NullPointerException e) {
//            logger.info("## ERROR saveOrUpdate");
//            e.printStackTrace();
            logger.error("" + e);
            e.printStackTrace();
            return returnVal.put("status", 500).put("description", "" + e);
        } finally {
            if (null != session) {
                session.close();
            }
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
        tmp.setUpdateBy(userName);
        return tmp;
    }

    private ShelfTmpVcs createShelfTmpVcs(String vcsUuid, String tmpUuid, Date effectiveDate, int vscStatus, String userName) {
        ShelfTmpVcs vcs = new ShelfTmpVcs();
        vcs.setUuid(vcsUuid);
        vcs.setTmpUuid(new ShelfTmp(tmpUuid));
        vcs.setEffectiveDate(effectiveDate);
        vcs.setVersion(0);
        vcs.setStatus(vscStatus);
        vcs.setCreateAt(new java.util.Date());
        vcs.setCreateBy(userName);
        return vcs;
    }

    private ShelfTmpVcs updateShelfTmpVcs(ShelfTmpVcs oldVcs, Date effectiveDate, int vscStatus, String userName) {
        ShelfTmpVcs vcs = new ShelfTmpVcs();
        vcs.setUuid(oldVcs.getUuid());
        vcs.setTmpUuid(new ShelfTmp(oldVcs.getTmpUuid().getUuid()));
        vcs.setEffectiveDate(effectiveDate);
        vcs.setVersion(oldVcs.getVersion());
        vcs.setStatus(vscStatus);
        vcs.setCreateAt(oldVcs.getCreateAt());
        vcs.setCreateBy(oldVcs.getCreateBy());
        vcs.setUpdateAt(new java.util.Date());
        vcs.setUpdateBy(userName);
        return vcs;
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
        Session session = HibernateUtil.getSessionMaster(dbEnv).openSession();
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
        return retVal;
    }

    private boolean checkTmpVcsStatus(String dbEnv, String tmpVcsUuid, Integer status) {
        boolean retVal = true;
        List<ShelfTmpVcs> tmpVcsList = new ShelfTmpVcsDao().getListByTmpUuidAndStatus(dbEnv, tmpVcsUuid, status);
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
        Session session = HibernateUtil.getSessionMaster(dbEnv).openSession();
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
        return retVal;
    }
}
