/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.controller;

import com.tfglog.LogSingleton;
import com.tfglog.Log_decorator;
import com.tfglog.TfgLogger;
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
import th.co.d1.digitallending.dao.ShelfMenuDao;
import th.co.d1.digitallending.entity.ShelfMenu;
import th.co.d1.digitallending.entity.ShelfRole;
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
@RequestMapping("/shelf/menu/v1")
public class ShefMenuV1Controller {

    final static Logger logger = Logger.getLogger(ShefMenuV1Controller.class.getName());
    final static TfgLogger log = LogSingleton.getTfgLogger();

    @Log_decorator
    @RequestMapping(value = "list", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getMenuList(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestHeader(value = "sub_state", required = false) String subState) {
        log.info("GET : /shelf/menu/v1/list");
        logger.info("GET : /shelf/menu/v1/list");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONObject());
        try {
            Integer statusActive = StatusUtils.getActive(subState).getStatusCode();
            List<JSONObject> arr = new ArrayList<>();
            ShelfMenuDao dao = new ShelfMenuDao();
            List<ShelfMenu> list = dao.getShelfMenuList(subState, statusActive);
            for (ShelfMenu menu : list) {
                JSONObject obj = new JSONObject()
                        .put("uuid", ValidUtils.null2NoData(menu.getUuid()))
                        .put("menu_code", ValidUtils.null2NoData(menu.getMenuCode()))
                        .put("menu_name", ValidUtils.null2NoData(menu.getMenuName()))
                        .put("menu_url", ValidUtils.null2NoData(menu.getMenuUrl()))
                        .put("description", ValidUtils.null2NoData(menu.getDescription()))
                        .put("status", menu.getStatus());

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
    @RequestMapping(value = "info/{menuUuid}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> infoMenu(HttpSession session, HttpServletResponse response, HttpServletRequest request, @PathVariable String menuUuid, @RequestHeader(value = "sub_state", required = false) String subState
    ) {
        logger.info("GET : /shelf/menu/v1/info/" + menuUuid);
        log.info("GET : /shelf/menu/v1/info/" + menuUuid);
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
        try {
            ShelfMenu menu = new ShelfMenuDao().getShelfMenu(subState, menuUuid);
            if (null != menu.getUuid()) {
                JSONObject obj = new JSONObject()
                        .put("uuid", ValidUtils.null2NoData(menu.getUuid()))
                        .put("menu_code", ValidUtils.null2NoData(menu.getMenuCode()))
                        .put("menu_name", ValidUtils.null2NoData(menu.getMenuName()))
                        .put("menu_url", ValidUtils.null2NoData(menu.getMenuUrl()))
                        .put("description", ValidUtils.null2NoData(menu.getDescription()))
                        .put("status", menu.getStatus());
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
    public ResponseEntity<?> saveMenu(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String reqBody, @RequestHeader(value = "sub_state", required = false) String subState) {
        logger.info("POST : /shelf/menu/v1/save");
        log.info("POST : /shelf/menu/v1/save");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
        try {
            JSONObject datas = new JSONObject(reqBody);
            if (datas.has("userInfo") && datas.has("data")) {
                JSONObject objUser = datas.getJSONObject("userInfo");
                String username = objUser.has("username") ? objUser.getString("username") : "";
                if (!"".equals(username)) {
                    ShelfMenuDao dao = new ShelfMenuDao();
                    JSONObject objData = datas.getJSONObject("data");
                    ShelfMenu menu = new ShelfMenu();
                    if (objData.has("uuid") && !objData.getString("uuid").isEmpty()) {
                        menu = dao.getShelfMenu(subState, objData.getString("uuid"));
                        if (null == menu || null == menu.getUuid()) {
                            menu = new ShelfMenu();
                            menu.setUuid(getUUID());
                        }
                    } else {
                        menu.setUuid(getUUID());
                    }
                    menu.setMenuCode(objData.has("menu_code") ? objData.getString("menu_code") : "");
                    menu.setMenuName(objData.has("menu_name") ? objData.getString("menu_name") : "");
                    menu.setMenuUrl(objData.has("menu_url") ? objData.getString("menu_url") : "");
                    menu.setDescription(objData.has("description") ? objData.getString("description") : "");
                    Integer status = objData.has("status") ? StatusUtils.getStatusByCode(subState, objData.getString("status")).getStatusCode() : StatusUtils.getActive(subState).getStatusCode();
                    menu.setStatus(status);
                    JSONObject resp = dao.saveShelfMenu(subState, menu, username);
                    if (resp.getBoolean("status")) {
                        returnVal.put("data", resp.get("menu"));
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
