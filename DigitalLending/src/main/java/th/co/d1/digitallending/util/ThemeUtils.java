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
                    .put("createAt", ValidUtils.null2NoData(DateUtils.getDisplayEnDate(theme.getCreateAt(), "yyyy-MM-dd HH:mm")))
                    .put("updateAt", ValidUtils.null2Separator(DateUtils.getDisplayEnDate(theme.getUpdateAt(), "yyyy-MM-dd HH:mm"), DateUtils.getDisplayEnDate(theme.getCreateAt(), "yyyy-MM-dd HH:mm")))
                    .put("data", (theme.getValue() == null ? "" : theme.getValue()))
                    .put("createBy", ValidUtils.null2NoData(theme.getCreateBy()))
                    .put("updateBy", ValidUtils.null2NoData(theme.getUpdateBy()));

            jsonArr.put(jsonObj);
        }
*/
        return jsonArr;
    }
    /*
    public static JSONObject setActiveExpireTemplate(String dbEnv) {
        JSONObject result = new JSONObject().put("status", 200).put("description", "");
        try {
            List<JSONObject> list = new ArrayList<>();
            Date sysdate = new Date();
            Integer statusInactive = StatusUtils.getInActive(dbEnv).getStatusCode();
            Integer statusActive = StatusUtils.getActive(dbEnv).getStatusCode();
            Integer statusExpire = StatusUtils.getExpired(dbEnv).getStatusCode();
            ShelfTmpVcsDao dao = new ShelfTmpVcsDao();
            List<ShelfTmpVcs> mapActive = dao.getListByStatus(dbEnv, statusActive);
            List<ShelfTmpVcs> mapInactive = dao.getListByStatus(dbEnv, statusInactive);
            
            for(ShelfTmpVcs vcs : mapInactive){
                
            }
//            Set<String> activeKName = mapActive.keySet();
            Date expireDate = new Date();
            for (String k2 : activeKName) {
                JSONObject data = (JSONObject) mapActive.get(k2);
                if (data.has("endDate")) {
                    Date endDate = ValidUtils.str2Date(ValidUtils.null2NoData(data.get("endDate")), "yyyy-MM-dd");
                    if (sysdate.compareTo(endDate) >= 0) {
                        data.put("status", statusExpire);
                        list.add(data);
                        expireDate = endDate;
                    }

                }
                if (null != mapInactive.get(k2)) {
                    JSONObject data2 = (JSONObject) mapInactive.get(k2);
                    if (data2.has("activeDate")) {
                        Date activeDate = ValidUtils.str2Date(ValidUtils.null2NoData(data2.get("activeDate")), "yyyy-MM-dd");
                        if (sysdate.compareTo(activeDate) >= 0) {
                            data.put("status", statusExpire);
                            list.add(data);
                            data2.put("status", statusActive);
                            list.add(data2);
                            mapInactive.remove(k2);
                            expireDate = activeDate;
                        }
                    }
                }
            }
            Set<String> inActiveKName = mapInactive.keySet();
            for (String k2 : inActiveKName) {
                JSONObject data = (JSONObject) mapInactive.get(k2);
                if (data.has("activeDate")) {
                    Date activeDate = ValidUtils.str2Date(ValidUtils.null2NoData(data.get("activeDate")), "yyyy-MM-dd");
                    if (sysdate.compareTo(activeDate) >= 0) {
                        data.put("status", statusActive);
                        list.add(data);
                    }
                }
            }
            ShelfProductVcsDao vcsDao = new ShelfProductVcsDao();
            vcsDao.updateShelfProductVcs(dbEnv, list, expireDate, statusExpire);
        } catch (HibernateException | NullPointerException | ParseException e) {
            //e.printStackTrace();
            logger.info(e.getMessage());
            result.put("status", 500).put("description", "" + e);
        }
        return result;
    }
*/
}
