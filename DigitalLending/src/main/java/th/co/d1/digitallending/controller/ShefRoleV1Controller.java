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
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.hibernate.HibernateException;
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
import static th.co.d1.digitallending.controller.ThemeV1Controller.logger;
import th.co.d1.digitallending.dao.ShelfRoleDao;
import th.co.d1.digitallending.dao.ShelfThemeDao;
import th.co.d1.digitallending.dao.SysLookupDao;
import th.co.d1.digitallending.entity.ShelfRole;
import th.co.d1.digitallending.entity.ShelfTheme;
import static th.co.d1.digitallending.util.ApplicationStartup.headersJSON;
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
 * @create 21-10-2020 10:00:56 AM
 */
@Controller
@RequestMapping("/shelf/role/v1")
public class ShefRoleV1Controller {

    final static Logger logger = Logger.getLogger(ShefRoleV1Controller.class.getName());
    final static TfgLogger log = LogSingleton.getTfgLogger();

    @Log_decorator
    @RequestMapping(value = "list", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getRoleList(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestHeader(value = "sub_state", required = false) String subState) {
        log.info("GET : /shelf/role/v1/list");
        logger.info("GET : /shelf/role/v1/list");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONObject());
        try {
            Integer statusActive = StatusUtils.getActive(subState).getStatusCode();
            List<JSONObject> arr = new ArrayList<>();
            List<ShelfRole> list = new ShelfRoleDao().getShelfRoleList(subState, statusActive);
            for (ShelfRole role : list) {
                JSONObject obj = new JSONObject()
                        .put("uuid", ValidUtils.null2NoData(role.getUuid()))
                        .put("role_id", ValidUtils.null2NoData(role.getRoleId()))
                        .put("role_code", ValidUtils.null2NoData(role.getRoleCode()))
                        .put("role_name", ValidUtils.null2NoData(role.getRoleName()))
                        .put("description", ValidUtils.null2NoData(role.getDescription()))
                        .put("role_level", ValidUtils.null2NoData(role.getAttr1()))
                        .put("status", role.getStatus());

                arr.add(obj);
            }
            returnVal.put("datas", arr);
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
    @RequestMapping(value = "info/{roleUuid}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> infoRole(HttpSession session, HttpServletResponse response, HttpServletRequest request, @PathVariable String roleUuid, @RequestHeader(value = "sub_state", required = false) String subState
    ) {
        logger.info("GET : /shelf/role/v1/info/" + roleUuid);
        log.info("GET : /shelf/role/v1/info/" + roleUuid);
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
        try {
            ShelfRole role = new ShelfRoleDao().getShelfRole(subState, roleUuid);
            if (null != role.getUuid()) {
                JSONObject obj = new JSONObject()
                        .put("uuid", ValidUtils.null2NoData(role.getUuid()))
                        .put("role_id", ValidUtils.null2NoData(role.getRoleId()))
                        .put("role_code", ValidUtils.null2NoData(role.getRoleCode()))
                        .put("role_name", ValidUtils.null2NoData(role.getRoleName()))
                        .put("description", ValidUtils.null2NoData(role.getDescription()))
                        .put("role_level", ValidUtils.null2NoData(role.getAttr1()))
                        .put("status", role.getStatus());
                returnVal.put("data", obj);
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
    @RequestMapping(value = "save", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<?> saveRole(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String reqBody, @RequestHeader(value = "sub_state", required = false) String subState) {
        logger.info("POST : /shelf/role/v1/save");
        log.info("POST : /shelf/role/v1/save");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
        try {
            JSONObject datas = new JSONObject(reqBody);
            if (datas.has("userInfo") && datas.has("data")) {
                JSONObject objUser = datas.getJSONObject("userInfo");
                String username = objUser.has("username") ? objUser.getString("username") : "";
                if (!"".equals(username)) {
                    ShelfRoleDao dao = new ShelfRoleDao();
                    JSONObject objData = datas.getJSONObject("data");
                    ShelfRole role = new ShelfRole();
                    if (objData.has("uuid") && !objData.getString("uuid").isEmpty()) {
                        role = dao.getShelfRole(subState, objData.getString("uuid"));
                        if (null == role || null == role.getUuid()) {
                            role = new ShelfRole();
                            role.setUuid(getUUID());
                        }
                    } else {
                        role.setUuid(getUUID());
                    }
                    role.setRoleId(objData.has("role_id") ? objData.getString("role_id") : "");
                    role.setRoleCode(objData.has("role_code") ? objData.getString("role_code") : "");
                    role.setRoleName(objData.has("role_name") ? objData.getString("role_name") : "");
                    role.setAttr1(objData.has("role_level") ? objData.getString("role_level") : "");
                    role.setDescription(objData.has("description") ? objData.getString("description") : "");
                    Integer status = objData.has("status") ? StatusUtils.getStatusByCode(subState, objData.getString("status")).getStatusCode() : StatusUtils.getActive(subState).getStatusCode();
                    role.setStatus(status);
                    JSONObject resp = dao.saveShelfRole(subState, role,username);
                    if (resp.getBoolean("status")) {
                        returnVal.put("data", resp.get("role"));
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
}
