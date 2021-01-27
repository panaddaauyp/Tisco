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
import java.util.Date;
import java.util.List;
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
import th.co.d1.digitallending.dao.ShelfRoleFuncDao;
import th.co.d1.digitallending.dao.ShelfRoleMenuDao;
import th.co.d1.digitallending.entity.ShelfMenu;
import th.co.d1.digitallending.entity.ShelfRole;
import th.co.d1.digitallending.entity.ShelfRoleFunc;
import th.co.d1.digitallending.entity.ShelfRoleMenu;
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
 * @create 21-10-2020 3:19:49 PM
 */
@Controller
@RequestMapping("/shelf/func/v1")
public class ShelfRoleMenuFunctionV1Controller {

    final static Logger logger = Logger.getLogger(ShelfRoleMenuFunctionV1Controller.class.getName());
    final static TfgLogger log = LogSingleton.getTfgLogger();
    Date sysdate = new Date();

    @Log_decorator
    @RequestMapping(value = "edit", method = POST, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<?> editMenu(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String reqBody, @RequestHeader(value = "sub_state", required = false) String subState) {
        logger.info("POST : /shelf/func/v1/edit");
        log.info("POST : /shelf/func/v1/edit");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
        try {
            JSONObject datas = new JSONObject(reqBody);
            if (datas.has("userInfo") && datas.has("data")) {
                JSONObject objUser = datas.getJSONObject("userInfo");
                String username = objUser.has("username") ? objUser.getString("username") : "";
                if (!"".equals(username)) {
                    JSONArray arr = datas.getJSONArray("data");
                    ShelfRoleMenuDao daoRM = new ShelfRoleMenuDao();
                    ShelfRoleFuncDao daoRF = new ShelfRoleFuncDao();
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject objData = arr.getJSONObject(i);
                        String roleMenuId = objData.has("role_menu_id") ? objData.getString("role_menu_id") : "";
                        String roleFuncId = objData.has("role_func_id") ? objData.getString("role_func_id") : "";
                        String roleId = objData.has("role_id") ? objData.getString("role_id") : "";
                        String menuId = objData.has("menu_id") ? objData.getString("menu_id") : "";
                        String create = objData.has("create") ? objData.getString("create") : "N";
                        String edit = objData.has("edit") ? objData.getString("edit") : "N";
                        String delete = objData.has("delete") ? objData.getString("delete") : "N";
                        String preview = objData.has("preview") ? objData.getString("preview") : "N";
                        String export = objData.has("export") ? objData.getString("export") : "N";
                        String approve = objData.has("approve") ? objData.getString("approve") : "N";
                        String terminate = objData.has("terminate") ? objData.getString("terminate") : "N";
                        String pause = objData.has("pause") ? objData.getString("pause") : "N";
                        String start = objData.has("start") ? objData.getString("start") : "N";
                        String statusApprove = objData.has("status_approve") ? objData.getString("status_approve") : "";
                        Integer status = objData.has("status") ? StatusUtils.getStatusByCode(subState, objData.getString("status")).getStatusCode() : StatusUtils.getActive(subState).getStatusCode();

                        ShelfRoleMenu roleMenu = new ShelfRoleMenu();
                        if (roleMenuId.equals("")) {
                                roleMenu = new ShelfRoleMenu();
                                roleMenu.setUuid(getUUID());
                                roleMenu.setCreateAt(null);
                        } else {
                            roleMenu = daoRM.getShelfRoleMenu(subState, roleMenuId);
                            if (roleMenu.getUuid() != null) {
                                roleMenu.setUuid(roleMenu.getUuid());
                                roleMenu.setUpdateBy(username);
                            }
                        }
                        roleMenu.setRoleUuid(new ShelfRole(roleId));
                        roleMenu.setMenuUuid(new ShelfMenu(menuId));
                        roleMenu.setAttr9(statusApprove);
                        roleMenu.setAttr10(statusApprove);
                        roleMenu.setStatus(ValidUtils.obj2Integer(statusApprove));
                        sysdate = new Date();
                        if (roleMenu.getCreateAt() == null || roleMenu.getCreateAt().equals("")) {
                            roleMenu.setCreateAt(sysdate);
                            roleMenu.setCreateBy(username);
                        }
                        if (roleMenu.getUpdateBy() != null || roleMenu.getUpdateBy() != "") {
                            roleMenu.setUpdateAt(sysdate);
                        }
                        if (roleMenu.getCreateBy() == null) {
                            roleMenu.setCreateBy(username);
                        }
                        roleMenu = daoRM.saveShelfRoleMenu(subState, roleMenu, username);
                        if (roleMenu.getUuid() != null) {
                            ShelfRoleFunc roleFunc = new ShelfRoleFunc();
                            if (roleFuncId.equals("")) {
                                roleFunc = new ShelfRoleFunc();
                                roleFunc.setUuid(getUUID());
                                roleFunc.setCreateAt(null);
                            } else {
                                roleFunc = daoRF.getShelfRoleFunc(subState, roleFuncId);
                                if (roleFunc.getUuid() != null) {
                                    roleFunc.setUuid(roleFunc.getUuid());
                                    roleFunc.setUpdateBy(username);
                                }
                            }
                            roleFunc.setRoleMenuId(roleMenu);
                            roleFunc.setFCreate(create.charAt(0));
                            roleFunc.setFEdit(edit.charAt(0));
                            roleFunc.setFDelete(delete.charAt(0));
                            roleFunc.setFPreview(preview.charAt(0));
                            roleFunc.setFExport(export.charAt(0));
                            roleFunc.setFApprove(approve.charAt(0));
                            roleFunc.setfTerminate(terminate.charAt(0));
                            roleFunc.setfPause(pause.charAt(0));
                            roleFunc.setfStart(start.charAt(0));
                            roleFunc.setStatus(status);
                            sysdate = new Date();
                            if (roleFunc.getCreateAt() == null) {
                                roleFunc.setCreateAt(sysdate);
                                roleFunc.setCreateBy(username);
                            }
                            if (roleFunc.getUpdateBy() != null) {
                                roleFunc.setUpdateAt(sysdate);
                            }
                            roleFunc = daoRF.saveShelfRoleFunction(subState, roleFunc, username);

                            if (roleFunc.getUuid() != null) {
                                returnVal.put("status", 200).put("description", "Sucsess RoleMenuFunc1");
                            } else {
                                returnVal.put("status", 500).put("description", "Error RoleMenuFunc");
                            }
                        } else {
                            returnVal.put("status", 500).put("description", "Error RoleMenu");
                        }
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
    @RequestMapping(value = "list", method = POST)
    @ResponseBody
    public ResponseEntity<?> getRoleList(HttpSession session, HttpServletResponse response, @RequestBody String reqBody, HttpServletRequest request, @RequestHeader(value = "sub_state", required = false) String subState) {
        log.info("GET : /shelf/func/v1/list");
        logger.info("GET : /shelf/func/v1/list");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONObject());
        try {
            List<JSONObject> arr = new ArrayList<>();
            JSONObject datas = new JSONObject(reqBody);
            if (datas.has("userInfo") && datas.has("data")) {
                JSONObject objUser = datas.getJSONObject("userInfo");
                String username = objUser.has("username") ? objUser.getString("username") : "";
                if (!"".equals(username) && datas.has("data")) {
                    JSONObject objData = datas.getJSONObject("data");
                    String roleUid = objData.has("role_id") ? objData.getString("role_id") : "";
                    String menuUid = objData.has("menu_id") ? objData.getString("menu_id") : "";
                    Integer status = objData.has("status") ? StatusUtils.getStatusByCode(subState, objData.getString("status")).getStatusCode() : StatusUtils.getActive(subState).getStatusCode();
                    ShelfRoleMenuDao dao = new ShelfRoleMenuDao();
                    List<ShelfRoleMenu> list = dao.getShelfRoleByRoleMenu(subState, roleUid, menuUid, status);
                    for (ShelfRoleMenu role : list) {
                        JSONObject obj = new JSONObject()
                                .put("role_menu_id", ValidUtils.null2NoData(role.getUuid()))
                                .put("menu_id", ValidUtils.null2NoData(role.getMenuUuid().getUuid()))
                                .put("role_id", ValidUtils.null2NoData(role.getRoleUuid().getUuid()))
                                .put("status", role.getStatus())
                                .put("role_func_id", "")
                                .put("create", "")
                                .put("edit", "")
                                .put("delete", "")
                                .put("preview", "")
                                .put("export", "")
                                .put("approve", "")
                                .put("terminate", "")
                                .put("pause", "")
                                .put("start", "");
                        if (null != role.getShelfRoleFuncList() && role.getShelfRoleFuncList().size() > 0) {
                            ShelfRoleFunc func = role.getShelfRoleFuncList().get(0);
                            obj.put("role_func_id", ValidUtils.null2NoData(func.getUuid()))
                                .put("create", ValidUtils.null2Separator(func.getFCreate(), "N"))
                                .put("edit", ValidUtils.null2Separator(func.getFEdit(), "N"))
                                .put("delete", ValidUtils.null2Separator(func.getFDelete(), "N"))
                                .put("preview", ValidUtils.null2Separator(func.getFPreview(), "N"))
                                .put("export", ValidUtils.null2Separator(func.getFExport(), "N"))
                                .put("approve", ValidUtils.null2Separator(func.getFApprove(), "N"))
                                .put("terminate", ValidUtils.null2Separator(func.getfTerminate(), "N"))
                                .put("pause", ValidUtils.null2Separator(func.getfPause(), "N"))
                                .put("start", ValidUtils.null2Separator(func.getfStart(), "N"));
                        }
                        arr.add(obj);
                    }
                }
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

}
