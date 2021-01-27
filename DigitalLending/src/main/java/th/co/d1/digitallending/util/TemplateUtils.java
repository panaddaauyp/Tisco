/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.util;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import th.co.d1.digitallending.dao.ShelfTmpDetailDao;
import th.co.d1.digitallending.dao.ShelfTmpVcsDao;
import th.co.d1.digitallending.dao.SysLookupDao;
import th.co.d1.digitallending.entity.Memlookup;
import th.co.d1.digitallending.entity.ShelfTmpDetail;
import th.co.d1.digitallending.entity.ShelfTmpVcs;

/**
 *
 * @author Kritsana
 */
public class TemplateUtils {

    Logger logger = Logger.getLogger(TemplateUtils.class);

    public JSONArray getTemplateList(String dbEnv) {
        JSONArray jsonArr = new JSONArray();
        List<ShelfTmpVcs> shelfTmpVcs = new ShelfTmpVcsDao().getList(dbEnv);
        for (ShelfTmpVcs ver : shelfTmpVcs) {
            Memlookup memLookup = new SysLookupDao().getMemLookupByCode(dbEnv, ValidUtils.null2NoData(ver.getStatus()));
            JSONObject eachList = new JSONObject()
                    .put("id", ver.getTmpUuid().getUuid())
                    .put("vcsUuId", ver.getUuid())
                    .put("name", ver.getTmpUuid().getTmpName())
                    .put("description", ver.getTmpUuid().getDescription())
                    .put("version", (ver.getVersion() == 0 ? "" : String.valueOf(ver.getVersion())))
                    .put("effectiveDate", ver.getEffectiveDate())
                    .put("status", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupcode()) : "")
                    .put("statusNameTh", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupnameth()) : "")
                    .put("statusNameEn", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupnameen()) : "")
                    .put("updateDate", (ver.getUpdateAt() == null ? "" : ver.getUpdateAt()))
                    .put("updateBy", (ver.getUpdateBy() == null ? "" : ver.getUpdateBy()))
                    .put("createDate", ver.getCreateAt())
                    .put("createBy", ver.getCreateBy());
            jsonArr.put(eachList);
        }
        return jsonArr;
    }

    /* Update Or Create - end */
    public JSONArray getTemplateListByStatus(String dbEnv, int status) {
        JSONArray returnVal = new JSONArray();
        try {
//            int statusActive = StatusUtils.getActive(dbEnv).getStatusCode();
//        List<ShelfTmp> shelfTmps = new ShelfTmpDao().getListByStatus(dbEnv, statusActive);
            List<ShelfTmpVcs> list = new ShelfTmpVcsDao().getListByStatus(dbEnv, status);
//        int verNumb = 0;
//        String tmpVcsUuid = "";
            for (ShelfTmpVcs vcs : list) {
                /*for (ShelfTmpVcs vcs : shelfTmp.getShelfTmpVcsList()) {
                if (vcs.getStatus() == statusActive) {
                    verNumb = vcs.getVersion();
                    tmpVcsUuid = vcs.getUuid();
                    break;
                }
            }*/
                List<ShelfTmpDetail> shelfTmpDetail = new ShelfTmpDetailDao().getTemplateDetailByTemplateVcsUuid(dbEnv, vcs.getUuid(), status);
//                JSONArray compList = new JSONArray();
                List<JSONObject> compList = new ArrayList<>();
                for (ShelfTmpDetail tmpDtl : shelfTmpDetail) {
                    JSONObject tmpObj = new JSONObject();
                    tmpObj.put("uuid", ValidUtils.null2NoData(tmpDtl.getCompUuid().getUuid()));
                    tmpObj.put("seqNo", ValidUtils.null2NoData(tmpDtl.getSeqNo()));
                    tmpObj.put("compCode", ValidUtils.null2NoData(tmpDtl.getCompUuid().getCompCode()));
                    tmpObj.put("compName", ValidUtils.null2NoData(tmpDtl.getCompUuid().getCompName()));
                    tmpObj.put("description", ValidUtils.null2NoData(tmpDtl.getCompUuid().getDescription()));
                    tmpObj.put("pattern", ValidUtils.null2NoData(tmpDtl.getCompUuid().getPattern()));
                    tmpObj.put("value", ValidUtils.null2NoData(tmpDtl.getCompUuid().getValue()));
//                    compList.put(tmpObj);
                    compList.add(tmpObj);
                }
                Utils.sortJSONObjectByKey(compList, "seqNo", true);
                JSONObject eachList = new JSONObject()
                        .put("id", vcs.getTmpUuid().getUuid())
                        .put("vcsUuid", vcs.getUuid())
                        .put("name", vcs.getTmpUuid().getTmpName())
                        .put("value", vcs.getTmpUuid().getValue())
                        .put("currentVcsUuid", (vcs.getTmpUuid().getCurrentVcsUuid()))
                        .put("previousVcsUuid", vcs.getTmpUuid().getPreviousVcsUuid())
                        .put("description", vcs.getDescription())
                        .put("attr1", vcs.getAttr1())
                        .put("attr2", vcs.getAttr2())
                        .put("attr3", vcs.getAttr3())
                        .put("attr4", vcs.getAttr4())
                        .put("attr5", vcs.getAttr5())
                        .put("attr6", vcs.getAttr6())
                        .put("attr7", vcs.getAttr7())
                        .put("attr8", vcs.getAttr8())
                        .put("attr9", vcs.getAttr9())
                        .put("attr10", vcs.getAttr10())
                        .put("status", vcs.getStatus())
                        .put("version", vcs.getVersion())
                        .put("companyCode", vcs.getTmpUuid().getCompanyCode())
                        .put("businessDept", vcs.getTmpUuid().getBussinessDept())
                        .put("businessLine", vcs.getTmpUuid().getBusinessLine())
                        .put("updateDate", (vcs.getUpdateAt() == null ? "" : vcs.getUpdateAt()))
                        .put("updateBy", (vcs.getUpdateBy() == null ? "" : vcs.getUpdateBy()))
                        .put("createDate", vcs.getCreateAt())
                        .put("createBy", vcs.getCreateBy())
                        .put("effectiveDate", vcs.getEffectiveDate())
                        .put("component", compList);
                returnVal.put(eachList);
            }
        } catch (JSONException | NullPointerException e) {
            logger.error("" + e);
            e.printStackTrace();
        }
        return returnVal;
    }
}
