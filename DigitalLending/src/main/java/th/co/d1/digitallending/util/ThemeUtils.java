/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.util;

import org.json.JSONArray;

/**
 *
 * @author Kritsana
 */
public class ThemeUtils {

    public static JSONArray getThemeList(String dbEnv, boolean onlyActive) {
        JSONArray jsonArr = new JSONArray();
        /*
        List<ShelfTheme> list = new ShelfThemeDao().getListShelfTmpTheme(dbEnv, onlyActive);
        for (ShelfTheme theme : list) {
            Memlookup memLookup = new SysLookupDao().getMemLookupByCode(dbEnv, ValidUtils.null2NoData(theme.getStatus()));
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("uuid", theme.getUuid())
                    .put("code", ValidUtils.null2NoData(theme.getThemeCode()))
                    .put("name", ValidUtils.null2NoData(theme.getThemeName()))
                    .put("status", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupcode()) : "")
                    .put("statusNameTh", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupnameth()) : "")
                    .put("statusNameEn", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupnameen()) : "")
                    .put("createAt", ValidUtils.null2NoData(DateUtils.getDisplayEnDate(theme.getCreateAt(), "dd/MM/yyyy HH:mm")))
                    .put("updateAt", ValidUtils.null2Separator(DateUtils.getDisplayEnDate(theme.getUpdateAt(), "dd/MM/yyyy HH:mm"), DateUtils.getDisplayEnDate(theme.getCreateAt(), "dd/MM/yyyy HH:mm")))
                    .put("data", (theme.getValue() == null ? "" : theme.getValue()))
                    .put("createBy", ValidUtils.null2NoData(theme.getCreateBy()))
                    .put("updateBy", ValidUtils.null2NoData(theme.getUpdateBy()));

            jsonArr.put(jsonObj);
        }
*/
        return jsonArr;
    }
}
