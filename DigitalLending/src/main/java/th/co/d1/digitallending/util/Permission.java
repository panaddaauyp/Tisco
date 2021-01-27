/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.util;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import th.co.d1.digitallending.dao.SysRoleDao;

public class Permission {

    public JSONObject checkPermission(String dbEnv, String flow, String action, String roleCode) {
        flow = flow.toLowerCase();
        action = action.toLowerCase();
        JSONObject returnVal = new JSONObject().put("status", HttpStatus.UNAUTHORIZED).put("description", "User not have Permission");
        SysRoleDao dao = new SysRoleDao();
        JSONObject permission = dao.getRoleByCode(dbEnv, roleCode);
        if (permission.has(flow)) {
            JSONObject flowData = permission.getJSONObject(flow);
            if (flowData.has(action)) {
                if (flowData.getBoolean(action)) {
                    returnVal.put("status", HttpStatus.OK).put("description", "");
                }
            }
        }
        return returnVal;
    }

}
