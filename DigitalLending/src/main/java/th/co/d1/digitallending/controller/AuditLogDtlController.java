/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.controller;

import com.tfglog.LogSingleton;
import com.tfglog.Log_decorator;
import com.tfglog.TfgLogger;
import java.util.Date;
import java.util.logging.Level;
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
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import org.springframework.web.bind.annotation.ResponseBody;
import th.co.d1.digitallending.dao.AuditLogDtlDao;
import th.co.d1.digitallending.dao.ShelfLookupDao;
import th.co.d1.digitallending.entity.AuditLogDtl;
import static th.co.d1.digitallending.util.ApplicationStartup.headersJSON;
import th.co.d1.digitallending.util.StatusUtils;
import th.co.d1.digitallending.util.Utils;
import th.co.d1.digitallending.util.ValidUtils;

/**
 *
 * @author iConTroL
 */
@Controller
public class AuditLogDtlController {

    Logger logger = Logger.getLogger(AuditLogDtlController.class.getName());
    TfgLogger log = LogSingleton.getTfgLogger();

    @Log_decorator
    @RequestMapping(value = "/api/audit_detail_log/save", method = POST, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<?> saveAuditDetailLogLogin(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String reqBody, @RequestHeader(value = "sub_state", required = false) String subState) {
        logger.info("POST : /api/audit_detail_log/save");
        log.info("POST : /api/audit_detail_log/save");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", new JSONArray());
        try {
            JSONObject data = new JSONObject(reqBody);
            String username = "", businessDept = "", businessLine = "";
            if (data.has("userInfo")) {
                JSONObject objUser = data.getJSONObject("userInfo");
                username = (objUser.has("username") ? objUser.getString("username") : "");
                businessDept = (objUser.has("businessDept") ? objUser.getString("businessDept") : "");
                businessLine = (objUser.has("businessLine") ? objUser.getString("businessLine") : "");
            }
            AuditLogDtl auditLogDtl = new AuditLogDtl();
            if (data.has("data")) {
                JSONObject dataDetail = data.getJSONObject("data");
                auditLogDtl.setUuid(Utils.getUUID());
                auditLogDtl.setAuditUuid(dataDetail.has("auditUuid") ? dataDetail.getString("auditUuid") : "");
                auditLogDtl.setLookupCode(dataDetail.has("lookupCode") ? dataDetail.getString("lookupCode").toLowerCase() : "");
                String lookupUuid = new ShelfLookupDao().getShelfLookupByLkCode(subState, dataDetail.getString("lookupCode").toLowerCase(), "default_action").getUuid();
                auditLogDtl.setLookupUuid(null != lookupUuid ? lookupUuid : "");//t_shelf_lookup.lookupcode
                auditLogDtl.setTrnUuid(dataDetail.has("trnUuid") ? dataDetail.getString("trnUuid") : "");
                auditLogDtl.setMenu(dataDetail.has("menu") ? dataDetail.getString("menu") : "");
                auditLogDtl.setDetail(dataDetail.has("detail") ? dataDetail.getString("detail") : "");
                auditLogDtl.setErrorDesc(dataDetail.has("errorDesc") ? dataDetail.getString("errorDesc") : "");
                auditLogDtl.setRefId(dataDetail.has("refId") ? dataDetail.getString("refId") : "");
                auditLogDtl.setRefVer(dataDetail.has("refVer") ? dataDetail.getString("refVer") : "");
                auditLogDtl.setRefCode(dataDetail.has("refCode") ? dataDetail.getString("refCode") : "");//Theme, Template, Product
                auditLogDtl.setRefDesc(dataDetail.has("refDesc") ? dataDetail.getString("refDesc") : "");
                Integer trnStatus = 200;
                if (dataDetail.has("trnStatus")) {
                    trnStatus = ValidUtils.str2BigInteger(ValidUtils.null2NoData(dataDetail.get("trnStatus")));
                }
                
                Date currentDate = new Date();
                auditLogDtl.setCreateAt(currentDate);
                auditLogDtl.setCreateBy(username);
                auditLogDtl.setStatus(trnStatus);
            } else {
                returnVal.put("status", 400)
                        .put("description", StatusUtils.getErrorMessageByCode(subState, "SHELF0033"));
                return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.BAD_REQUEST));
            }
            JSONObject retJson = new JSONObject();
            AuditLogDtlDao dao = new AuditLogDtlDao();
            auditLogDtl = dao.saveSysAuditDetailLog(subState, auditLogDtl);
            retJson.put("uuid", auditLogDtl.getUuid());
            retJson.put("lastTime", auditLogDtl == null ? "" : ValidUtils.null2NoData(auditLogDtl.getAttr1()));
            returnVal.put("data", retJson);
        } catch (JSONException | HibernateException | NullPointerException e) {
            //e.printStackTrace();
            logger.log(Level.SEVERE, e.getMessage());
            log.error(e.getMessage());
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }
}
