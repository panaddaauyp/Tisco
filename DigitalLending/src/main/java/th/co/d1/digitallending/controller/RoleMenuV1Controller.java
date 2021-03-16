/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.controller;

import com.tfglog.LogSingleton;
import com.tfglog.Log_decorator;
import com.tfglog.TfgLogger;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
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
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import org.springframework.web.bind.annotation.ResponseBody;
import th.co.d1.digitallending.dao.ShelfRoleDao;
import th.co.d1.digitallending.dao.ShelfRoleMenuDao;
import th.co.d1.digitallending.entity.ShelfRole;
import th.co.d1.digitallending.entity.ShelfRoleFunc;
import th.co.d1.digitallending.entity.ShelfRoleMenu;
import static th.co.d1.digitallending.util.ApplicationStartup.headersJSON;
import th.co.d1.digitallending.util.StatusUtils;
import th.co.d1.digitallending.util.ValidUtils;

/**
 *
 * @author Kritsana Sasai
 *
 * DataOne Asia (Thailand) Company Limited 1023 MS Siam Tower, 30th Floor, Rama
 * 3 Rd., Chongnonsi, Yannawa, Bangkok, 10120
 *
 * @create 06-03-2020 9:33:01 AM
 */
@Controller
@RequestMapping("/shelf/rolemenu/v1")
public class RoleMenuV1Controller {

    Logger logger = Logger.getLogger(RoleMenuV1Controller.class.getName());
    TfgLogger log = LogSingleton.getTfgLogger();

    @Log_decorator
    @RequestMapping(value = "/role/{roleId}", method = GET)
    @ResponseBody
    public ResponseEntity<?> getRoleMenus(HttpSession session, HttpServletResponse response, HttpServletRequest request, @PathVariable String roleId, @RequestHeader(value = "sub_state", required = false) String subState) {
        logger.info(String.format("GET : /shelf/rolemenu/v1/role/%s", roleId));
        log.info(String.format("GET : /shelf/rolemenu/v1/role/%s", roleId));
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            JSONArray arr = new JSONArray();
            String dbEnv = subState;
            ShelfRoleMenuDao dao = new ShelfRoleMenuDao();
            Integer status = StatusUtils.getActive(dbEnv).getStatusCode();
            List<ShelfRoleMenu> list = dao.getShelfRoleMenus(dbEnv, roleId, status);
            for (ShelfRoleMenu sm : list) {
                arr.put(new JSONObject().put("menucode", sm.getMenuUuid().getMenuCode())
                        .put("menuname", sm.getMenuUuid().getMenuName())
                        .put("menudesc", sm.getMenuUuid().getDescription())
                        .put("menuurl", sm.getMenuUuid().getMenuUrl()));
            }
            returnVal.put("datas", arr);
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
    @RequestMapping(value = "/user/roles", method = POST)
    @ResponseBody
    public ResponseEntity<?> getRoles(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String roleIdList, @RequestHeader(value = "sub_state", required = false) String subState) {
        logger.info("POST : /shelf/rolemenu/v1/user/roles");
        log.info("POST : /shelf/rolemenu/v1/user/roles");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            JSONArray roleId = new JSONArray(roleIdList);
            String[] roles = new String[roleId.length()];
            for (int i = 0; i < roleId.length(); i++) {
                roles[i] = roleId.getString(i);
            }
            String dbEnv = subState;
            ShelfRoleDao dao = new ShelfRoleDao();
            Integer status = StatusUtils.getActive(dbEnv).getStatusCode();
            List<ShelfRole> list = dao.getShelfRoles(dbEnv, roles, status);
            JSONArray arr = new JSONArray();
            for (ShelfRole sm : list) {
                JSONObject obj = new JSONObject();
                obj.put("roleId", ValidUtils.null2NoData(sm.getRoleId()))
                        .put("roleCode", ValidUtils.null2NoData(sm.getRoleCode()))
                        .put("roleName", ValidUtils.null2NoData(sm.getRoleName()))
                        .put("attr1", ValidUtils.null2NoData(sm.getAttr1()))
                        .put("attr2", ValidUtils.null2NoData(sm.getAttr2()))
                        .put("attr3", ValidUtils.null2NoData(sm.getAttr3()));
                arr.put(obj);
            }
            returnVal.put("datas", arr);
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
    @RequestMapping(value = "/roles", method = POST)
    @ResponseBody
    public ResponseEntity<?> getRolesMenus(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String roleIdList, @RequestHeader(value = "sub_state", required = false) String subState) {
        logger.info("POST : /shelf/rolemenu/v1/rolemenu/v1/roles");
        log.info("POST : /shelf/rolemenu/v1/rolemenu/v1/roles");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            JSONArray roleId = new JSONArray(roleIdList);
            String[] roles = new String[roleId.length()];
            for (int i = 0; i < roleId.length(); i++) {
                roles[i] = roleId.getString(i);
            }
            String dbEnv = subState;
            ShelfRoleMenuDao dao = new ShelfRoleMenuDao();
            Integer status = StatusUtils.getActive(dbEnv).getStatusCode();
            List<ShelfRoleMenu> list = dao.getShelfRolesMenus(dbEnv, roles, status);
            HashMap hmap = new HashMap();
            for (ShelfRoleMenu sm : list) {
                String menuCode = sm.getMenuUuid().getMenuCode();
                if (null == hmap.get(menuCode)) {
                    hmap.put(menuCode, new JSONObject().put("menucode", sm.getMenuUuid().getMenuCode())
                            .put("menuname", sm.getMenuUuid().getMenuName())
                            .put("menudesc", sm.getMenuUuid().getDescription())
                            .put("menuurl", sm.getMenuUuid().getMenuUrl())
                            .put("seqNo", ValidUtils.null2NoData(sm.getMenuUuid().getAttr1())));
                }
            }
            JSONArray arr = new JSONArray();
            Set<String> kset = hmap.keySet();
            for (String k2 : kset) {
                JSONObject obj = (JSONObject) hmap.get(k2);
                arr.put(obj);
            }
            returnVal.put("datas", arr);
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
    @RequestMapping(value = "/action", method = POST)
    @ResponseBody
    public ResponseEntity<?> getActionRoleMenus(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String reqBody, @RequestHeader(value = "sub_state", required = false) String subState) {
        logger.info("POST : /shelf/rolemenu/v1/action");
        log.info("POST : /shelf/rolemenu/v1/action");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            JSONArray arr = new JSONArray();
            JSONObject datas = new JSONObject(reqBody);
            if (datas.has("data") && datas.getJSONObject("data").has("roles") && datas.getJSONObject("data").has("menuCode")) {
                String dbEnv = subState;
                JSONArray arrRoles = datas.getJSONObject("data").getJSONArray("roles");
                String[] roles = new String[arrRoles.length()];
                for (int i = 0; i < arrRoles.length(); i++) {
                    roles[i] = arrRoles.getString(i);
                }
                String menuCode = datas.getJSONObject("data").getString("menuCode");
                List<ShelfRoleMenu> list = new ShelfRoleMenuDao().getActionShelfRolesMenus(dbEnv, roles, menuCode);
                HashMap hmap = new HashMap();
                for (ShelfRoleMenu rm : list) {
//                    System.out.println(rm.getMenuUuid().getMenuCode() + " : " + rm.getShelfRoleFuncList().size());
                    String menucode = rm.getMenuUuid().getMenuCode();
                    JSONObject obj = new JSONObject();
                    if (null == hmap.get(menucode)) {
                        obj.put("fCreate", "N")
                                .put("fEdit", "N")
                                .put("fDelete", "N")
                                .put("fPreview", "N")
                                .put("fExport", "N")
                                .put("fApprove", "N");
                    } else {
                        obj = (JSONObject) hmap.get(menucode);
                    }
                    for (ShelfRoleFunc rf : rm.getShelfRoleFuncList()) {
                        obj.put("fCreate", null != rf.getFCreate() && "Y".equalsIgnoreCase(rf.getFCreate().toString()) ? rf.getFCreate() : obj.getString("fCreate"));
                        obj.put("fEdit", null != rf.getFEdit() && "Y".equalsIgnoreCase(rf.getFEdit().toString()) ? rf.getFEdit() : obj.getString("fEdit"));
                        obj.put("fDelete", null != rf.getFDelete() && "Y".equalsIgnoreCase(rf.getFDelete().toString()) ? rf.getFDelete() : obj.getString("fDelete"));
                        obj.put("fPreview", null != rf.getFPreview() && "Y".equalsIgnoreCase(rf.getFPreview().toString()) ? rf.getFPreview() : obj.getString("fPreview"));
                        obj.put("fExport", null != rf.getFExport() && "Y".equalsIgnoreCase(rf.getFExport().toString()) ? rf.getFExport() : obj.getString("fExport"));
                        obj.put("fApprove", null != rf.getFApprove() && "Y".equalsIgnoreCase(rf.getFApprove().toString()) ? rf.getFApprove() : obj.getString("fApprove"));
                    }
                    hmap.put(menucode, obj);
                }
                Set<String> kset = hmap.keySet();
                for (String k2 : kset) {
                    JSONObject obj = (JSONObject) hmap.get(k2);
                    arr.put(obj);
                }
            }
            returnVal.put("datas", arr);
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
