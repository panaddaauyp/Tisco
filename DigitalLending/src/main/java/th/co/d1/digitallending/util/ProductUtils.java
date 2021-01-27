/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.util;

import com.google.gson.Gson;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.logging.Logger;
import org.hibernate.HibernateException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import th.co.d1.digitallending.dao.ShelfProductDao;
import th.co.d1.digitallending.dao.ShelfProductVcsDao;
import th.co.d1.digitallending.dao.ShelfThemeDao;
import th.co.d1.digitallending.dao.ShelfTmpDao;
import th.co.d1.digitallending.dao.ShelfTmpDetailDao;
import th.co.d1.digitallending.dao.ShelfTmpVcsDao;
import th.co.d1.digitallending.dao.SysLookupDao;
import th.co.d1.digitallending.entity.ShelfComp;
import th.co.d1.digitallending.entity.ShelfCompDtl;
import th.co.d1.digitallending.entity.ShelfProduct;
import th.co.d1.digitallending.entity.ShelfProductDtl;
import th.co.d1.digitallending.entity.ShelfProductVcs;
import th.co.d1.digitallending.entity.ShelfTheme;
import th.co.d1.digitallending.entity.ShelfTmp;
import th.co.d1.digitallending.entity.ShelfTmpVcs;
import static th.co.d1.digitallending.util.Utils.getUUID;

/**
 *
 * @author Kritsana
 */
public class ProductUtils {

    final static Logger logger = Logger.getLogger(ProductUtils.class.getName());

    public static JSONObject getInitialProductComponentByUUID(String dbEnv, ShelfComp shelfComp, List<ShelfCompDtl> shelfCompDtlList, boolean fullObj, String prodUuid) throws ParseException {
        JSONObject component = new JSONObject();
        JSONArray subComp = new JSONArray();
        component.put("compUuid", shelfComp.getUuid());
        component.put("compCode", shelfComp.getCompCode());
        component.put("compName", shelfComp.getCompName());
        component.put("seqNo", shelfComp.getSeqNo());
        component.put("page", ValidUtils.null2Separator(shelfComp.getAttr2(), "N"));
        try {
            Integer statusActive = StatusUtils.getActive(dbEnv).getStatusCode();
            if (!shelfCompDtlList.isEmpty()) {
                for (ShelfCompDtl shelfCompDtl : shelfCompDtlList) {
                    if (shelfCompDtl.getStatus() == statusActive) {
                        if (shelfCompDtl.getParent() != null) {
                            for (int i = 0; i < shelfCompDtlList.indexOf(shelfCompDtl); i++) {
                                if (shelfCompDtlList.get(i).getSeq().equals(shelfCompDtl.getParent())) {
                                    for (int jsonIndex = 0; jsonIndex < subComp.length(); jsonIndex++) {
                                        JSONObject obj = subComp.getJSONObject(jsonIndex);
                                        if (obj.getString("subUuid").equalsIgnoreCase(shelfCompDtlList.get(i).getUuid())) {
                                            JSONObject subCompObj = new JSONObject();
                                            subCompObj.put("eletype", ValidUtils.null2NoData(shelfCompDtl.getAttr3()));
                                            subCompObj.put("pagename", ValidUtils.null2NoData(shelfCompDtl.getAttr4()));
                                            if (shelfCompDtl.getLkUuid().getLookupCode().startsWith("G")) {
                                                subCompObj.put("subId", shelfCompDtl.getEleId());
                                                if (shelfCompDtl.getLkUuid().getAttr1().equalsIgnoreCase("Y")) {
                                                    subCompObj.put("value", shelfCompDtl.getAttr1() == null ? "" : shelfCompDtl.getAttr1());
                                                }
                                                subCompObj.put("subUuid", shelfCompDtl.getUuid());
                                                if (fullObj) {
                                                    subCompObj.put("subCompCode", shelfCompDtl.getLkUuid().getLookupCode());
                                                }
                                                subCompObj.put("subCompLabel", shelfCompDtl.getLabelText());
                                                JSONArray jsonArray = new JSONArray();
                                                subCompObj.put("details", jsonArray);
                                                obj.getJSONArray("details").put(subCompObj);
                                            } else {
                                                subCompObj.put("id", shelfCompDtl.getEleId());
                                                if (shelfCompDtl.getLkUuid().getAttr1().equalsIgnoreCase("Y")) {
                                                    subCompObj.put("value", shelfCompDtl.getAttr1() == null ? "" : shelfCompDtl.getAttr1());
                                                }
                                                if (fullObj) {
                                                    subCompObj.put("code", shelfCompDtl.getLkUuid().getLookupCode());
                                                    subCompObj.put("label", ValidUtils.null2Separator(shelfCompDtl.getAttr5(), shelfCompDtl.getLabelText()));
                                                    subCompObj.put("require", shelfCompDtl.getRequire());
                                                    subCompObj.put("validation", (null == shelfCompDtl.getValidation() || shelfCompDtl.getValidation().isEmpty()) ? new JSONArray() : new JSONArray(shelfCompDtl.getValidation()));
                                                    subCompObj.put("pattern", shelfCompDtl.getPattern());
                                                    subCompObj.put("description", shelfCompDtl.getDescription());
                                                }
                                                if (!fullObj && new ProductUtils().isId(shelfCompDtl.getEleId(), (subCompObj.has("subId") ? subCompObj.getString("subId") : ""))) {
                                                    subCompObj.put("require", shelfCompDtl.getRequire());
                                                }
                                                if ("T001".equalsIgnoreCase(shelfCompDtl.getLkUuid().getLookupCode()) || "T002".equalsIgnoreCase(shelfCompDtl.getLkUuid().getLookupCode()) || "T003".equalsIgnoreCase(shelfCompDtl.getLkUuid().getLookupCode())) {
                                                    subCompObj.put("table", new JSONArray(shelfCompDtl.getTableColumn()));
//                                                subCompObj.put("data", shelfCompDtl.getDataValue().isEmpty() ? new JSONArray() : new JSONArray(shelfCompDtl.getDataValue()));
                                                    if (null != shelfCompDtl.getAttr2()) {
                                                        subCompObj.put("data", new LookupUtils().getLookupList(dbEnv, ValidUtils.null2NoData(shelfCompDtl.getAttr2()), prodUuid));
                                                        if (subCompObj.has("value") && !subCompObj.getString("value").isEmpty()
                                                                && subCompObj.has("id") && "calFactorsList".equalsIgnoreCase(subCompObj.getString("id"))) {
                                                            subCompObj.put("data", new JSONArray(subCompObj.getString("value")));
                                                        }
                                                    }
                                                }
                                                if ("IN004".equalsIgnoreCase(shelfCompDtl.getLkUuid().getLookupCode()) || "IN009".equalsIgnoreCase(shelfCompDtl.getLkUuid().getLookupCode())
                                                        || "IN016".equalsIgnoreCase(shelfCompDtl.getLkUuid().getLookupCode()) || "IN020".equalsIgnoreCase(shelfCompDtl.getLkUuid().getLookupCode())) {
                                                    /*if (!"".equals(ValidUtils.null2NoData(shelfCompDtl.getDataValue()))) {
                                                    JSONArray jsonArray = new JSONArray(shelfCompDtl.getDataValue());
                                                    subCompObj.put("data", jsonArray);
                                                }*/
                                                    if (null != shelfCompDtl.getAttr2()) {
                                                        subCompObj.put("data", new LookupUtils().getLookupList(dbEnv, ValidUtils.null2NoData(shelfCompDtl.getAttr2()), prodUuid));
                                                    }
                                                }
                                                if (obj.has("details")) {
                                                    obj.getJSONArray("details").put(subCompObj);
                                                } else {
                                                    JSONArray jsonArray = new JSONArray();
                                                    jsonArray.put(subCompObj);
                                                    obj.put("details", jsonArray);
                                                }
                                            }
                                            break;
                                        } else {
                                            if (obj.has("details")) {
                                                JSONArray details = obj.getJSONArray("details");
                                                for (int dtlIndex = 0; dtlIndex < details.length(); dtlIndex++) {
                                                    JSONObject dtlObj = details.getJSONObject(dtlIndex);
                                                    if (dtlObj.has("subUuid") && dtlObj.getString("subUuid").equalsIgnoreCase(shelfCompDtlList.get(i).getUuid())) {
                                                        JSONObject subCompObj = new JSONObject();
                                                        subCompObj.put("eletype", ValidUtils.null2NoData(shelfCompDtl.getAttr3()));
                                                        subCompObj.put("pagename", ValidUtils.null2NoData(shelfCompDtl.getAttr4()));
                                                        if (shelfCompDtl.getLkUuid().getLookupCode().startsWith("G")) {
                                                            subCompObj.put("subId", shelfCompDtl.getEleId());
                                                            if (shelfCompDtl.getLkUuid().getAttr1().equalsIgnoreCase("Y")) {
                                                                subCompObj.put("value", shelfCompDtl.getAttr1() == null ? "" : shelfCompDtl.getAttr1());
                                                            }
                                                            subCompObj.put("subUuid", shelfCompDtl.getUuid());
                                                            if (fullObj) {
                                                                subCompObj.put("subCompCode", shelfCompDtl.getLkUuid().getLookupCode());
                                                            }
                                                            subCompObj.put("subCompLabel", shelfCompDtl.getLabelText());
                                                            JSONArray jsonArray = new JSONArray();
                                                            subCompObj.put("details", jsonArray);
                                                            dtlObj.getJSONArray("details").put(subCompObj);
                                                        } else {
                                                            subCompObj.put("id", shelfCompDtl.getEleId());
                                                            if (shelfCompDtl.getLkUuid().getAttr1().equalsIgnoreCase("Y")) {
                                                                subCompObj.put("value", shelfCompDtl.getAttr1() == null ? "" : shelfCompDtl.getAttr1());
                                                            }
                                                            if (fullObj) {
                                                                subCompObj.put("code", shelfCompDtl.getLkUuid().getLookupCode());
                                                                subCompObj.put("label", ValidUtils.null2Separator(shelfCompDtl.getAttr5(), shelfCompDtl.getLabelText()));
                                                                subCompObj.put("require", shelfCompDtl.getRequire());
                                                                subCompObj.put("validation", (null == shelfCompDtl.getValidation() || shelfCompDtl.getValidation().isEmpty()) ? new JSONArray() : new JSONArray(shelfCompDtl.getValidation()));
                                                                subCompObj.put("pattern", shelfCompDtl.getPattern());
                                                                subCompObj.put("description", shelfCompDtl.getDescription());
                                                            }
                                                            if (!fullObj && new ProductUtils().isId(shelfCompDtl.getEleId(), (dtlObj.has("subId") ? dtlObj.getString("subId") : ""))) {
                                                                subCompObj.put("require", shelfCompDtl.getRequire());
                                                            }
                                                            if ("T001".equalsIgnoreCase(shelfCompDtl.getLkUuid().getLookupCode()) || "T002".equalsIgnoreCase(shelfCompDtl.getLkUuid().getLookupCode()) || "T003".equalsIgnoreCase(shelfCompDtl.getLkUuid().getLookupCode())) {
                                                                subCompObj.put("table", new JSONArray(shelfCompDtl.getTableColumn()));
//                                                            subCompObj.put("data", shelfCompDtl.getDataValue().isEmpty() ? new JSONArray() : new JSONArray(shelfCompDtl.getDataValue()));
                                                                if (null != shelfCompDtl.getAttr2()) {
                                                                    if ("consList".equalsIgnoreCase(shelfCompDtl.getAttr2()) || "secretList".equalsIgnoreCase(shelfCompDtl.getAttr2())) {
                                                                        subCompObj.put("list", new LookupUtils().getLookupList(dbEnv, ValidUtils.null2NoData(shelfCompDtl.getAttr2()), prodUuid));
                                                                        subCompObj.put("data", new JSONArray());
                                                                    } else {
                                                                        subCompObj.put("data", new LookupUtils().getLookupList(dbEnv, ValidUtils.null2NoData(shelfCompDtl.getAttr2()), prodUuid));
                                                                        if (subCompObj.has("value") && !subCompObj.getString("value").isEmpty()
                                                                                && subCompObj.has("id") && "calFactorsList".equalsIgnoreCase(subCompObj.getString("id"))) {
                                                                            subCompObj.put("data", new JSONArray(subCompObj.getString("value")));
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                            if ("IN004".equalsIgnoreCase(shelfCompDtl.getLkUuid().getLookupCode()) || "IN009".equalsIgnoreCase(shelfCompDtl.getLkUuid().getLookupCode())
                                                                    || "IN016".equalsIgnoreCase(shelfCompDtl.getLkUuid().getLookupCode()) || "IN020".equalsIgnoreCase(shelfCompDtl.getLkUuid().getLookupCode())) {
                                                                /*if (!"".equals(ValidUtils.null2NoData(shelfCompDtl.getDataValue()))) {
                                                                JSONArray jsonArray = new JSONArray(shelfCompDtl.getDataValue());
                                                                subCompObj.put("data", jsonArray);
                                                            }*/
                                                                if (null != shelfCompDtl.getAttr2()) {
                                                                    if ("radioList".equalsIgnoreCase(shelfCompDtl.getAttr2())
                                                                            || "summaryList".equalsIgnoreCase(shelfCompDtl.getAttr2())
                                                                            || "packageList".equalsIgnoreCase(shelfCompDtl.getAttr2())
                                                                            || "secretList".equalsIgnoreCase(shelfCompDtl.getAttr2())) {
                                                                        subCompObj.put("list", new LookupUtils().getLookupList(dbEnv, ValidUtils.null2NoData(shelfCompDtl.getAttr2()), prodUuid));

                                                                        if ("radioList".equalsIgnoreCase(shelfCompDtl.getAttr2())) {  //set default 2
                                                                            JSONArray defArray = new JSONArray();
                                                                            JSONObject def = new JSONObject();
                                                                            def.put("label", "")
                                                                                    .put("param", "Y");
                                                                            defArray.put(def);
                                                                            def = new JSONObject();
                                                                            def.put("label", "")
                                                                                    .put("param", "N");
                                                                            subCompObj.put("data", defArray);
                                                                        } else if ("summaryList".equalsIgnoreCase(shelfCompDtl.getAttr2())) {  //set default 2
                                                                            JSONArray defArray = new JSONArray();
                                                                            JSONObject def = new JSONObject();
                                                                            def.put("label", "")
                                                                                    .put("parameter", "")
                                                                                    .put("topic", "header")
                                                                                    .put("unit", "");
                                                                            defArray.put(def);
                                                                            def = new JSONObject();
                                                                            def.put("label", "")
                                                                                    .put("parameter", "")
                                                                                    .put("topic", "body")
                                                                                    .put("unit", "");
                                                                            defArray.put(def);
                                                                            subCompObj.put("data", defArray);
                                                                        } else if ("packageList".equalsIgnoreCase(shelfCompDtl.getAttr2())) {  //set default 3
                                                                            JSONArray defArray = new JSONArray();
                                                                            JSONObject def = new JSONObject();
                                                                            def.put("label", "")
                                                                                    .put("parameter", "")
                                                                                    .put("topic", "header")
                                                                                    .put("unit", "");
                                                                            defArray.put(def);
                                                                            def = new JSONObject();
                                                                            def.put("label", "")
                                                                                    .put("parameter", "")
                                                                                    .put("topic", "body")
                                                                                    .put("unit", "");
                                                                            defArray.put(def);
                                                                            def = new JSONObject();
                                                                            def.put("label", "")
                                                                                    .put("parameter", "")
                                                                                    .put("topic", "content")
                                                                                    .put("unit", "");
                                                                            defArray.put(def);
                                                                            subCompObj.put("data", defArray);
                                                                        } else if ("secretList".equalsIgnoreCase(shelfCompDtl.getAttr2())) {
                                                                            subCompObj.put("data", new JSONArray());
                                                                        }
                                                                    } else {
                                                                        subCompObj.put("data", new LookupUtils().getLookupList(dbEnv, ValidUtils.null2NoData(shelfCompDtl.getAttr2()), prodUuid));
                                                                    }
                                                                }
                                                            }
                                                            if (dtlObj.has("details")) {
                                                                dtlObj.getJSONArray("details").put(subCompObj);
                                                            } else {
                                                                JSONArray jsonArray = new JSONArray();
                                                                jsonArray.put(subCompObj);
                                                                dtlObj.put("details", jsonArray);
                                                            }
                                                        }
                                                        break;
                                                    } else {
                                                        if (dtlObj.has("details")) {
                                                            JSONArray detailsSub = dtlObj.getJSONArray("details");
                                                            for (int dtlSubIndex = 0; dtlSubIndex < detailsSub.length(); dtlSubIndex++) {
                                                                JSONObject dtlSubObj = detailsSub.getJSONObject(dtlSubIndex);
                                                                if (dtlSubObj.has("subUuid") && dtlSubObj.getString("subUuid").equalsIgnoreCase(shelfCompDtlList.get(i).getUuid())) {
                                                                    JSONObject subCompObj = new JSONObject();
                                                                    subCompObj.put("eletype", ValidUtils.null2NoData(shelfCompDtl.getAttr3()));
                                                                    subCompObj.put("pagename", ValidUtils.null2NoData(shelfCompDtl.getAttr4()));
                                                                    if (shelfCompDtl.getLkUuid().getLookupCode().startsWith("G")) {
                                                                        subCompObj.put("subId", shelfCompDtl.getEleId());
                                                                        if (shelfCompDtl.getLkUuid().getAttr1().equalsIgnoreCase("Y")) {
                                                                            subCompObj.put("value", shelfCompDtl.getAttr1() == null ? "" : shelfCompDtl.getAttr1());
                                                                        }
                                                                        subCompObj.put("subUuid", shelfCompDtl.getUuid());
                                                                        if (fullObj) {
                                                                            subCompObj.put("subCompCode", shelfCompDtl.getLkUuid().getLookupCode());
                                                                        }
                                                                        subCompObj.put("subCompLabel", shelfCompDtl.getLabelText());
                                                                        JSONArray jsonArray = new JSONArray();
                                                                        subCompObj.put("details", jsonArray);
                                                                        dtlSubObj.getJSONArray("details").put(subCompObj);
                                                                    } else {
                                                                        subCompObj.put("id", shelfCompDtl.getEleId());
                                                                        if (shelfCompDtl.getLkUuid().getAttr1().equalsIgnoreCase("Y")) {
                                                                            subCompObj.put("value", shelfCompDtl.getAttr1() == null ? "" : shelfCompDtl.getAttr1());
                                                                        }
                                                                        if (fullObj) {
                                                                            subCompObj.put("code", shelfCompDtl.getLkUuid().getLookupCode());
                                                                            subCompObj.put("label", ValidUtils.null2Separator(shelfCompDtl.getAttr5(), shelfCompDtl.getLabelText()));
                                                                            subCompObj.put("require", shelfCompDtl.getRequire());
                                                                            subCompObj.put("validation", (null == shelfCompDtl.getValidation() || shelfCompDtl.getValidation().isEmpty()) ? new JSONArray() : new JSONArray(shelfCompDtl.getValidation()));
                                                                            subCompObj.put("pattern", shelfCompDtl.getPattern());
                                                                            subCompObj.put("description", shelfCompDtl.getDescription());
                                                                        }
                                                                        if (!fullObj && new ProductUtils().isId(shelfCompDtl.getEleId(), (dtlSubObj.has("subId") ? dtlSubObj.getString("subId") : ""))) {
                                                                            subCompObj.put("require", shelfCompDtl.getRequire());
                                                                        }
                                                                        if ("T001".equalsIgnoreCase(shelfCompDtl.getLkUuid().getLookupCode()) || "T002".equalsIgnoreCase(shelfCompDtl.getLkUuid().getLookupCode()) || "T003".equalsIgnoreCase(shelfCompDtl.getLkUuid().getLookupCode())) {
                                                                            subCompObj.put("table", new JSONArray(shelfCompDtl.getTableColumn()));
//                                                                        subCompObj.put("data", shelfCompDtl.getDataValue().isEmpty() ? new JSONArray() : new JSONArray(shelfCompDtl.getDataValue()));
                                                                            if (null != shelfCompDtl.getAttr2()) {
                                                                                subCompObj.put("data", new LookupUtils().getLookupList(dbEnv, ValidUtils.null2NoData(shelfCompDtl.getAttr2()), prodUuid));
                                                                                if (subCompObj.has("value") && !subCompObj.getString("value").isEmpty()
                                                                                        && subCompObj.has("id") && "calFactorsList".equalsIgnoreCase(subCompObj.getString("id"))) {
                                                                                    subCompObj.put("data", new JSONArray(subCompObj.getString("value")));
                                                                                }
                                                                            }
                                                                        }
                                                                        if ("IN004".equalsIgnoreCase(shelfCompDtl.getLkUuid().getLookupCode()) || "IN009".equalsIgnoreCase(shelfCompDtl.getLkUuid().getLookupCode())
                                                                                || "IN016".equalsIgnoreCase(shelfCompDtl.getLkUuid().getLookupCode()) || "IN020".equalsIgnoreCase(shelfCompDtl.getLkUuid().getLookupCode())) {
                                                                            /*if (!"".equals(ValidUtils.null2NoData(shelfCompDtl.getDataValue()))) {
                                                                            JSONArray jsonArray = new JSONArray(shelfCompDtl.getDataValue());
                                                                            subCompObj.put("data", jsonArray);
                                                                        }*/
                                                                            if (null != shelfCompDtl.getAttr2()) {
                                                                                subCompObj.put("data", new LookupUtils().getLookupList(dbEnv, ValidUtils.null2NoData(shelfCompDtl.getAttr2()), prodUuid));
                                                                            }
                                                                        }
                                                                        if (dtlSubObj.has("details")) {
                                                                            dtlSubObj.getJSONArray("details").put(subCompObj);
                                                                        } else {
                                                                            JSONArray jsonArray = new JSONArray();
                                                                            jsonArray.put(subCompObj);
                                                                            dtlSubObj.put("details", jsonArray);
                                                                        }
                                                                    }
                                                                    break;
                                                                } else {
                                                                    if (dtlSubObj.has("details")) {
                                                                        JSONArray detailsSubs = dtlSubObj.getJSONArray("details");
                                                                        for (int dtlSubsIndex = 0; dtlSubsIndex < detailsSubs.length(); dtlSubsIndex++) {
                                                                            JSONObject dtlSubsObj = detailsSubs.getJSONObject(dtlSubsIndex);
                                                                            if (dtlSubsObj.has("subUuid") && dtlSubsObj.getString("subUuid").equalsIgnoreCase(shelfCompDtlList.get(i).getUuid())) {
                                                                                JSONObject subCompObj = new JSONObject();
                                                                                subCompObj.put("eletype", ValidUtils.null2NoData(shelfCompDtl.getAttr3()));
                                                                                subCompObj.put("pagename", ValidUtils.null2NoData(shelfCompDtl.getAttr4()));
                                                                                if (shelfCompDtl.getLkUuid().getLookupCode().startsWith("G")) {
                                                                                    subCompObj.put("subId", shelfCompDtl.getEleId());
                                                                                    if (shelfCompDtl.getLkUuid().getAttr1().equalsIgnoreCase("Y")) {
                                                                                        subCompObj.put("value", shelfCompDtl.getAttr1() == null ? "" : shelfCompDtl.getAttr1());
                                                                                    }
                                                                                    subCompObj.put("subUuid", shelfCompDtl.getUuid());
                                                                                    if (fullObj) {
                                                                                        subCompObj.put("subCompCode", shelfCompDtl.getLkUuid().getLookupCode());
                                                                                    }
                                                                                    subCompObj.put("subCompLabel", shelfCompDtl.getLabelText());
                                                                                    JSONArray jsonArray = new JSONArray();
                                                                                    subCompObj.put("details", jsonArray);
                                                                                    dtlSubsObj.getJSONArray("details").put(subCompObj);
                                                                                } else {
                                                                                    subCompObj.put("id", shelfCompDtl.getEleId());
                                                                                    if (shelfCompDtl.getLkUuid().getAttr1().equalsIgnoreCase("Y")) {
                                                                                        subCompObj.put("value", shelfCompDtl.getAttr1() == null ? "" : shelfCompDtl.getAttr1());
                                                                                    }
                                                                                    if (fullObj) {
                                                                                        subCompObj.put("code", shelfCompDtl.getLkUuid().getLookupCode());
                                                                                        subCompObj.put("label", ValidUtils.null2Separator(shelfCompDtl.getAttr5(), shelfCompDtl.getLabelText()));
                                                                                        subCompObj.put("require", shelfCompDtl.getRequire());
                                                                                        subCompObj.put("validation", (null == shelfCompDtl.getValidation() || shelfCompDtl.getValidation().isEmpty()) ? new JSONArray() : new JSONArray(shelfCompDtl.getValidation()));
                                                                                        subCompObj.put("pattern", shelfCompDtl.getPattern());
                                                                                        subCompObj.put("description", shelfCompDtl.getDescription());
                                                                                    }
                                                                                    if (!fullObj && new ProductUtils().isId(shelfCompDtl.getEleId(), (dtlSubsObj.has("subId") ? dtlSubsObj.getString("subId") : ""))) {
                                                                                        subCompObj.put("require", shelfCompDtl.getRequire());
                                                                                    }
                                                                                    if ("T001".equalsIgnoreCase(shelfCompDtl.getLkUuid().getLookupCode()) || "T002".equalsIgnoreCase(shelfCompDtl.getLkUuid().getLookupCode()) || "T003".equalsIgnoreCase(shelfCompDtl.getLkUuid().getLookupCode())) {
                                                                                        subCompObj.put("table", new JSONArray(shelfCompDtl.getTableColumn()));
//                                                                                    subCompObj.put("data", shelfCompDtl.getDataValue().isEmpty() ? new JSONArray() : new JSONArray(shelfCompDtl.getDataValue()));
                                                                                        if (null != shelfCompDtl.getAttr2()) {
                                                                                            subCompObj.put("data", new LookupUtils().getLookupList(dbEnv, ValidUtils.null2NoData(shelfCompDtl.getAttr2()), prodUuid));
                                                                                            if (subCompObj.has("value") && !subCompObj.getString("value").isEmpty()
                                                                                                    && subCompObj.has("id") && "calFactorsList".equalsIgnoreCase(subCompObj.getString("id"))) {
                                                                                                subCompObj.put("data", new JSONArray(subCompObj.getString("value")));
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                    if ("IN004".equalsIgnoreCase(shelfCompDtl.getLkUuid().getLookupCode()) || "IN009".equalsIgnoreCase(shelfCompDtl.getLkUuid().getLookupCode())
                                                                                            || "IN016".equalsIgnoreCase(shelfCompDtl.getLkUuid().getLookupCode()) || "IN020".equalsIgnoreCase(shelfCompDtl.getLkUuid().getLookupCode())) {
                                                                                        /*if (!"".equals(ValidUtils.null2NoData(shelfCompDtl.getDataValue()))) {
                                                                                        JSONArray jsonArray = new JSONArray(shelfCompDtl.getDataValue());
                                                                                        subCompObj.put("data", jsonArray);
                                                                                    }*/
                                                                                        if (null != shelfCompDtl.getAttr2()) {
                                                                                            subCompObj.put("data", new LookupUtils().getLookupList(dbEnv, ValidUtils.null2NoData(shelfCompDtl.getAttr2()), prodUuid));
                                                                                        }
                                                                                    }
                                                                                    if (dtlSubsObj.has("details")) {
                                                                                        dtlSubsObj.getJSONArray("details").put(subCompObj);
                                                                                    } else {
                                                                                        JSONArray jsonArray = new JSONArray();
                                                                                        jsonArray.put(subCompObj);
                                                                                        dtlSubsObj.put("details", jsonArray);
                                                                                    }
                                                                                }
                                                                                break;
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    break;
                                }
                            }
                        } else {
                            JSONObject obj = new JSONObject();
                            obj.put("eletype", ValidUtils.null2NoData(shelfCompDtl.getAttr3()));
                            obj.put("pagename", ValidUtils.null2NoData(shelfCompDtl.getAttr4()));
                            obj.put("subId", shelfCompDtl.getEleId());
                            if (shelfCompDtl.getLkUuid().getAttr1().equalsIgnoreCase("Y")) {
                                obj.put("value", shelfCompDtl.getAttr1() == null ? "" : shelfCompDtl.getAttr1());
                            }
                            obj.put("subUuid", shelfCompDtl.getUuid());
                            if (fullObj) {
                                obj.put("subCompCode", shelfCompDtl.getLkUuid().getLookupCode());
                            }
                            obj.put("subCompLabel", shelfCompDtl.getLabelText());
                            JSONArray jsonArray = new JSONArray();
                            obj.put("details", jsonArray);
                            subComp.put(obj);
                        }
                    }
                }
            }
            component.put("subComp", subComp);
        } catch (JSONException | NullPointerException je) {
            logger.info(je.getMessage());
            throw je;
        }
        return component;
    }

    public static ShelfProduct getProduct(String dbEnv, JSONObject datas, Date sysdate, String username) throws JSONException, NullPointerException {
        Integer statusActive = StatusUtils.getActive(dbEnv).getStatusCode();
        Integer statusInactive = StatusUtils.getInActive(dbEnv).getStatusCode();
        ShelfProduct prod = new ShelfProduct();
        if (datas.has("data")) {
            JSONObject objData = datas.getJSONObject("data");
            ShelfTheme theme = null;
            if (objData.getJSONObject("product").has("theme")) {
                theme = new ShelfTheme(objData.getJSONObject("product").getString("theme"));
                objData.getJSONObject("product").remove("theme");
            }
            ShelfTmp template = null;
            if (objData.getJSONObject("product").has("template")) {
                template = new ShelfTmp(objData.getJSONObject("product").getString("template"));
                objData.getJSONObject("product").remove("template");
            }
            List<ShelfProductVcs> listVcs = new ArrayList<>();
            ShelfProductVcs vcs = new ShelfProductVcs();    //set object of product
            vcs.setUuid(getUUID());
            vcs.setThemeUuid(theme);
            vcs.setTemUuid(template.getUuid());
            vcs.setProdUuid(prod);
            vcs.setState(ValidUtils.null2NoData(StatusUtils.getInprogress(dbEnv).getStatusCode()));
            vcs.setCreateBy(username);
            vcs.setStatus(StatusUtils.getInprogress(dbEnv).getStatusCode());
            int prodVer = 0, tmpVer = 0;
            if (objData.has("product")) {
                List<ShelfProductDtl> listDtl = new ArrayList<>();
                JSONObject jsonProd = objData.getJSONObject("product");
                prod.setUuid(jsonProd.has("uuid") && !jsonProd.getString("uuid").isEmpty() ? jsonProd.getString("uuid") : getUUID());
                prod.setProdCode(jsonProd.has("prodCode") ? jsonProd.getString("prodCode") : "");
                prod.setProdName(jsonProd.has("prodName") ? jsonProd.getString("prodName") : "");
                prod.setCompany(jsonProd.has("company") ? jsonProd.getString("company") : "");
                prod.setBusinessDept(jsonProd.has("businessDept") ? jsonProd.getString("businessDept") : "");
                prod.setBusinessLine(jsonProd.has("businessLine") ? jsonProd.getString("businessLine") : "");
                prod.setProdType(jsonProd.has("prodType") ? jsonProd.getString("prodType") : "");
                if (jsonProd.has("statusName")) {
                    jsonProd.remove("statusName");
                }
                if (jsonProd.has("statusNameEn")) {
                    jsonProd.remove("statusNameEn");
                }
                if (jsonProd.has("templateName")) {
                    jsonProd.remove("templateName");
                }
                if (jsonProd.has("status")) {
                    jsonProd.remove("status");
                }
                if (jsonProd.has("verProd")) {
                    prodVer = ValidUtils.obj2Int(jsonProd.get("verProd"));
                    jsonProd.remove("verProd");
                }
                if (jsonProd.has("company")) {
                    jsonProd.remove("company");
                }
                if (jsonProd.has("businessDept")) {
                    jsonProd.remove("businessDept");
                }
                if (jsonProd.has("businessLine")) {
                    jsonProd.remove("businessLine");
                }
                if (jsonProd.has("tmpVer")) {
                    tmpVer = ValidUtils.obj2Int(jsonProd.get("tmpVer"));
                    jsonProd.remove("tmpVer");
                }
                if (jsonProd.has("confirm")) {
                    jsonProd.remove("confirm");
                }
                if (jsonProd.has("action")) {
                    jsonProd.remove("action");
                }
                jsonProd.remove("uuid");
                Iterator<?> prodKName = jsonProd.keys();
                while (prodKName.hasNext()) {
                    String kName = (String) prodKName.next();
                    ShelfProductDtl dtl = new ProductUtils().generateObject(dbEnv, jsonProd, kName);
                    if (null != dtl) {
                        dtl.setCreateBy(username);
                        dtl.setStatus(statusActive);
                        dtl.setDtlStatus(statusActive);
                        listDtl.add(dtl);
                    }
                }
                vcs.setShelfProductDtlList(listDtl);
                vcs.setVerProd(prodVer);
                vcs.setVerTem(tmpVer);
            }
//            vcs.setCompStatus(StatusUtils.getActive());
            listVcs.add(vcs);
            if (objData.has("component")) {
                List<ShelfProductVcs> list2 = new ProductUtils().getProductInfo(dbEnv, objData.getJSONArray("component"), username, prod, theme, template, prodVer, tmpVer);
                listVcs.addAll(list2);
            }
            ShelfProduct p2 = new ShelfProductDao().getShelfProductByUUID(dbEnv, prod.getUuid());
            if (null != p2) {
                List<ShelfProductVcs> newVcs = new ArrayList<>();
                HashMap hmapOriginVcs = new HashMap();
                for (ShelfProductVcs pvcs : p2.getShelfProductVcsList()) {
                    String kName = pvcs.getProdUuid().getUuid() + "_" + (null != pvcs.getCompUuid() ? pvcs.getCompUuid().getUuid() : "") + "_" + pvcs.getVerProd();
                    hmapOriginVcs.put(kName, pvcs);
                }
                for (ShelfProductVcs pvcs : listVcs) {
                    String kName = pvcs.getProdUuid().getUuid() + "_" + (null != pvcs.getCompUuid() ? pvcs.getCompUuid().getUuid() : "") + "_" + pvcs.getVerProd();
                    ShelfProductVcs data = (ShelfProductVcs) hmapOriginVcs.get(kName);
                    if (null == data) {
                        data = pvcs;
                        data.setStatus(StatusUtils.getInprogress(dbEnv).getStatusCode());
                        data.setState(StatusUtils.setStatus(data.getState(), ValidUtils.null2NoData(data.getStatus())));
                        data.setCreateAt(sysdate);
                        data.setCreateBy(username);
                        data.setVerTem(tmpVer);
                        newVcs.add(data);
                    } else {
                        if (null == data.getCompUuid()) {
//                                data.setState(ValidUtils.null2NoData(StatusUtils.getInprogress(dbEnv).getStatusCode()));
                        }
                        List<ShelfProductDtl> newDtl = new ArrayList<>();//                        เอาค่าใหม่ มาใส่รายการเก่า
                        data.setTemUuid(pvcs.getTemUuid());
                        data.setThemeUuid(pvcs.getThemeUuid());
                        data.setVerTem(tmpVer);
                        data.setState(StatusUtils.setStatus(data.getState(), pvcs.getState()));
                        data.setStatus(StatusUtils.getInprogress(dbEnv).getStatusCode());
                        data.setUpdateBy(username);
                        data.setUpdateAt(sysdate);
                        List<ShelfProductDtl> originList = data.getShelfProductDtlList();
                        List<ShelfProductDtl> newDtlList = pvcs.getShelfProductDtlList();
                        HashMap hmapOriginDtl = new HashMap();
                        for (ShelfProductDtl d2 : originList) {
                            hmapOriginDtl.put(d2.getLkCode(), d2);
                        }
                        for (ShelfProductDtl d2 : newDtlList) {
                            ShelfProductDtl dtl2 = (ShelfProductDtl) hmapOriginDtl.get(d2.getLkCode());
                            if (dtl2 == null) {
                                dtl2 = d2;
                                dtl2.setCreateAt(sysdate);
                                dtl2.setCreateBy(username);
                            } else {
                                dtl2.setLkLabel(d2.getLkLabel());
                                dtl2.setLkValue(d2.getLkValue());
                                dtl2.setUpdateBy(username);
                                dtl2.setUpdateAt(sysdate);
                                hmapOriginDtl.remove(d2.getLkCode());
                            }
                            dtl2.setStatus(statusActive);
                            dtl2.setDtlStatus(statusActive);
                            newDtl.add(dtl2);
                        }
                        Set<String> dtlKName = hmapOriginDtl.keySet();
                        for (String k2 : dtlKName) {
                            ShelfProductDtl dtl2 = (ShelfProductDtl) hmapOriginDtl.get(k2);
                            dtl2.setStatus(statusInactive);
                            dtl2.setCreateAt(sysdate);
                            dtl2.setCreateBy(username);
                            dtl2.setDtlStatus(statusInactive);
                            newDtl.add(dtl2);
                        }
                        data.setShelfProductDtlList(newDtl);
                        data.setCompStatus(statusActive);
                        newVcs.add(data);
                        hmapOriginVcs.remove(kName);
                    }
                }
                Set<String> kNames = hmapOriginVcs.keySet();
                for (String kName : kNames) {
                    ShelfProductVcs data = (ShelfProductVcs) hmapOriginVcs.get(kName);
                    if (prodVer == data.getVerProd()) {
                        if (null != data.getCompUuid()) {
                            data.setCompStatus(statusInactive);
                        }/* else if (null == data.getCompUuid()) {
                                data.setCompStatus(StatusUtils.getInprogress(dbEnv).getStatusCode());
                            }*/
                        data.setCreateAt(sysdate);
                        data.setCreateBy(username);
                        newVcs.add(data);
                    }
                }
                p2.setProdCode(prod.getProdCode());
                p2.setProdName(prod.getProdName());
                prod = p2;
                prod.setUpdateBy(username);
                prod.setUpdateAt(sysdate);
                prod.setStatus(StatusUtils.getInprogress(dbEnv).getStatusCode());
//                prod.setStatus(StatusUtils.getActive());
                prod.setShelfProductVcsList(newVcs);
            } else {
                prod.setCreateBy(username);
                prod.setStatus(StatusUtils.getInprogress(dbEnv).getStatusCode());
                prod.setShelfProductVcsList(listVcs);
            }
        }
        return prod;
    }

    private List<ShelfProductVcs> getProductInfo(String dbEnv, JSONArray datas, String username, ShelfProduct prod, ShelfTheme theme, ShelfTmp template, int verProd, int tmpVer) {
        List<ShelfProductVcs> list = new ArrayList<>();
        Integer statusActive = StatusUtils.getActive(dbEnv).getStatusCode();
        for (int i = 0; i < datas.length(); i++) {
            JSONObject data = datas.getJSONObject(i);
            ShelfProductVcs vcs = new ShelfProductVcs();
            vcs.setUuid(getUUID());
            vcs.setThemeUuid(theme);
            vcs.setCompUuid(new ShelfComp(data.getString("compUuid")));
            vcs.setTemUuid(template.getUuid());
//                        comp.getString("compName");
            vcs.setState(ValidUtils.null2NoData(StatusUtils.getInprogress(dbEnv).getStatusCode()));
            vcs.setProdUuid(prod);
            vcs.setCreateBy(username);
            vcs.setStatus(StatusUtils.getInprogress(dbEnv).getStatusCode());
            vcs.setCompStatus(statusActive);
            List<ShelfProductDtl> resp = getDtl(dbEnv, data.getJSONArray("subComp"), new ArrayList<>(), new ShelfProductVcs(), username);
            vcs.setShelfProductDtlList(resp);
            vcs.setVerProd(verProd);
            vcs.setVerTem(tmpVer);
            list.add(vcs);
        }
        return list;
    }

    private ShelfProductDtl generateObject(String dbEnv, JSONObject jsonProd, String lkCode) {
        Integer statusActive = StatusUtils.getActive(dbEnv).getStatusCode();
        try {
            ShelfProductDtl dtl = new ShelfProductDtl();
            dtl.setUuid(getUUID());
            dtl.setLkCode(lkCode);
            dtl.setLkValue(jsonProd.has(lkCode) ? ValidUtils.obj2String(jsonProd.get(lkCode)) : "");
            dtl.setStatus(statusActive);
            dtl.setDtlStatus(statusActive);
            return dtl;
        } catch (JSONException | NullPointerException e) {
            logger.info(e.getMessage());
            throw e;
        }
    }

    public static JSONObject getInitialProduct() {
        JSONObject ret = new JSONObject();
        ret.put("uuid", "");
        ret.put("prodCode", "");
        ret.put("prodName", "");
        ret.put("businessLine", "");
        ret.put("businessDept", "");
        ret.put("company", "");
        ret.put("prodType", "");
        ret.put("prodUrl", "");
        ret.put("activeDate", "");
        ret.put("endDate", "");
        ret.put("prodDay", "");
        ret.put("prodTime", "");
        ret.put("campaignId", "");
        ret.put("campaignName", "");
        ret.put("linkChannel", "");
        ret.put("productChannel", "");
//        ret.put("attr1", "");
//        ret.put("attr2", "");
//        ret.put("attr3", "");
//        ret.put("attr4", "");
//        ret.put("attr5", "");
//        ret.put("attr6", "");
//        ret.put("attr7", "");
//        ret.put("attr8", "");
//        ret.put("attr9", "");
//        ret.put("attr10", "");
        ret.put("status", "");
        ret.put("statusName", "");
        ret.put("template", "");
        ret.put("templateName", "");
//        ret.put("createAt", "");
//        ret.put("createBy", "");
//        ret.put("updateAt", "");
//        ret.put("updateBy", "");
        ret.put("productProcessor", "");
        ret.put("productController", "");
        ret.put("theme", "");
        ret.put("groupProduct", "");
        ret.put("operDepartment", "");
        ret.put("prodDepartment", "");
        ret.put("tmpVer", "");
        ret.put("ProdFooter", "");
        return ret;
    }

    public static JSONObject getProductDataByProduct(String dbEnv, List<ShelfProductVcs> shelfProductVcsList) throws ParseException {
        JSONObject retData = new JSONObject();
//        List<ShelfProductVcs> shelfProductVcsList = shelfProduct.getShelfProductVcsList();
        try {
            List<ShelfProductVcs>[] prodVerArr = ProductUtils.orderProductByVersion(shelfProductVcsList);
            List<ShelfProductVcs> vcsList = prodVerArr[0];
//        List<ShelfProductDtl> dtlList = new ArrayList<>();
//        for (ShelfProductVcs vcs : vcsList) {
//            if (vcs.getCompUuid() == null) {
//                dtlList = vcs.getShelfProductDtlList();
//                break;
//            }
//        }
//        JSONObject productTheme = new JSONObject();
            Integer statusActive = StatusUtils.getActive(dbEnv).getStatusCode();
            List<JSONObject> retComp = new ArrayList<>();
            String shelfTmpVcs = "";
            for (int i = 0; i < vcsList.size(); i++) {
                ShelfProductVcs shelfProductVcs = vcsList.get(i);
//                System.out.println("vcs :" + i + " << " + shelfProductVcs.getCompUuid());
                if (shelfProductVcs.getCompUuid() != null && null != shelfProductVcs.getCompStatus() && shelfProductVcs.getCompStatus().equals(statusActive)) {
                    if (!retData.has("theme")) {        //ไม่ต้องเอาออก ต้องใช้ตอน preview
                        Gson gson = new Gson();
                        JSONObject json = new JSONObject(gson.toJson(shelfProductVcs.getThemeUuid().getValue()));
                        retData.put("theme", json.has("info") ? json.getJSONObject("info") : json);
                    }
                    List<ShelfProductDtl> shelfProductDtlList = shelfProductVcs.getShelfProductDtlList();//assume to latest version(inprogres or latest)
//                    Map<String, Map<String, String>> prodDtlObj = new HashMap<>();
                    Map<String, Map<String, JSONObject>> prodDtlObj = new HashMap<>();
                    for (ShelfProductDtl prodDtl : shelfProductDtlList) {
                        if (prodDtl.getDtlStatus().equals(statusActive) && prodDtl.getTrnUuid().getCompUuid() != null) {
                            /*if (prodDtlObj.containsKey(prodDtl.getTrnUuid().getCompUuid().getUuid())) {
                                Map<String, String> dtlVal = (Map<String, String>) prodDtlObj.get(prodDtl.getTrnUuid().getCompUuid().getUuid());
                                dtlVal.put(prodDtl.getLkCode(), prodDtl.getLkValue());
                            } else {
                                Map<String, String> dtlVal = new HashMap<>();
                                dtlVal.put(prodDtl.getLkCode(), prodDtl.getLkValue());
                                prodDtlObj.put(prodDtl.getTrnUuid().getCompUuid().getUuid(), dtlVal);
                            }*/
                            if (prodDtlObj.containsKey(prodDtl.getTrnUuid().getCompUuid().getUuid())) {
                                Map<String, JSONObject> dtlVal = (Map<String, JSONObject>) prodDtlObj.get(prodDtl.getTrnUuid().getCompUuid().getUuid());
//                                dtlVal.put(prodDtl.getLkCode(), prodDtl.getLkValue());
                                dtlVal.put(prodDtl.getLkCode(), new JSONObject().put("value", prodDtl.getLkValue()).put("label", prodDtl.getLkLabel()));
                                prodDtlObj.put(prodDtl.getTrnUuid().getCompUuid().getUuid(), dtlVal);
                            } else {
                                Map<String, JSONObject> dtlVal = new HashMap<>();
                                dtlVal.put(prodDtl.getLkCode(), new JSONObject().put("value", prodDtl.getLkValue()).put("label", prodDtl.getLkLabel()));
                                prodDtlObj.put(prodDtl.getTrnUuid().getCompUuid().getUuid(), dtlVal);
                            }
                        }
                    }
                    ShelfTmpDao tmpDao = new ShelfTmpDao();
                    ShelfTmp shelfTmp = tmpDao.getShelfTmp(dbEnv, shelfProductVcs.getTemUuid());
//                List<ShelfTmpDetail> tmpDtl = new ArrayList<>();
                    for (ShelfTmpVcs tmpVcs : shelfTmp.getShelfTmpVcsList()) {
                        if (tmpVcs.getVersion() == shelfProductVcs.getVerTem()) {
                            shelfTmpVcs = tmpVcs.getUuid();
                            break;
                        }
                    }
//                tmpDtl.sort(Comparator.comparing(ShelfTmpDetail::getSeqNo));
                    /*for (Map.Entry<String, Map<String, String>> entry : prodDtlObj.entrySet()) {
//                    ShelfCompDao shelfCompDao = new ShelfCompDao();
//                    ShelfComp shelfComp = shelfCompDao.getShelfCompByUUID(entry.getKey());
                        ShelfTmpDetailDao shelfTmpDtlDao = new ShelfTmpDetailDao();
                        ShelfComp shelfComp = shelfTmpDtlDao.getShelfComponentByCompUUIDAndTemplateVCS(dbEnv, entry.getKey(), shelfTmpVcs);
                        List<ShelfCompDtl> shelfCompDtlList = shelfComp.getShelfCompDtlList();
                        Map<String, String> dtlVal = (Map<String, String>) prodDtlObj.get(entry.getKey());
                        for (int ci = 0; ci < shelfCompDtlList.size(); ci++) {
                            ShelfCompDtl shelfCompDtl = shelfCompDtlList.get(ci);
                            shelfCompDtl.setAttr1(dtlVal.get(shelfCompDtl.getEleId()));
                        }
                        shelfCompDtlList.sort(Comparator.comparing(ShelfCompDtl::getSeq));
                        JSONObject ret = ProductUtils.getInitialProductComponentByUUID(dbEnv, shelfComp, shelfCompDtlList, true, ((null != shelfProductVcs.getProdUuid()) ? shelfProductVcs.getProdUuid().getUuid() : null));
                        retComp.add(ret);
                    }*/
                    for (Map.Entry<String, Map<String, JSONObject>> entry : prodDtlObj.entrySet()) {
//                    ShelfCompDao shelfCompDao = new ShelfCompDao();
//                    ShelfComp shelfComp = shelfCompDao.getShelfCompByUUID(entry.getKey());
                        ShelfTmpDetailDao shelfTmpDtlDao = new ShelfTmpDetailDao();
                        ShelfComp shelfComp = shelfTmpDtlDao.getShelfComponentByCompUUIDAndTemplateVCS(dbEnv, entry.getKey(), shelfTmpVcs);
                        List<ShelfCompDtl> shelfCompDtlList = shelfComp.getShelfCompDtlList();
                        Map<String, JSONObject> dtlVal = (Map<String, JSONObject>) prodDtlObj.get(entry.getKey());
                        for (int ci = 0; ci < shelfCompDtlList.size(); ci++) {
                            ShelfCompDtl shelfCompDtl = shelfCompDtlList.get(ci);
                            JSONObject tmp = dtlVal.get(shelfCompDtl.getEleId());
                            shelfCompDtl.setAttr1(null != tmp && tmp.has("value") ? tmp.getString("value") : "");
                            shelfCompDtl.setAttr5(null != tmp && tmp.has("label") ? tmp.getString("label") : null);
                        }
                        shelfCompDtlList.sort(Comparator.comparing(ShelfCompDtl::getSeq));
                        JSONObject ret = ProductUtils.getInitialProductComponentByUUID(dbEnv, shelfComp, shelfCompDtlList, true, ((null != shelfProductVcs.getProdUuid()) ? shelfProductVcs.getProdUuid().getUuid() : null));
                        retComp.add(ret);
                    }
                } else {
                    if (shelfProductVcs.getCompUuid() == null) {
                        JSONObject prod = getInitialProduct();
                        prod.put("uuid", shelfProductVcs.getProdUuid().getUuid());
                        for (ShelfProductDtl prodDtl : shelfProductVcs.getShelfProductDtlList()) {
                            prod.put(prodDtl.getLkCode(), ValidUtils.null2NoData(prodDtl.getLkValue()));
                        }
                        ShelfTmp shelfTmp = new ShelfTmpDao().getShelfTmp(dbEnv, shelfProductVcs.getTemUuid());
                        StatusUtils.Status status = StatusUtils.getStatusByCode(dbEnv, String.valueOf(shelfProductVcs.getStatus()));
                        prod.put("status", status.getStatusCode() == null ? "" : status.getStatusCode());
                        prod.put("statusName", status.getStatusNameTh() == null ? "" : status.getStatusNameTh());
                        prod.put("statusNameEn", status.getStatusNameEn() == null ? "" : status.getStatusNameEn());
                        prod.put("template", shelfTmp.getUuid() == null ? "" : shelfTmp.getUuid());
                        prod.put("theme", shelfProductVcs.getThemeUuid().getUuid());
                        prod.put("templateName", shelfTmp.getTmpName() == null ? "" : shelfTmp.getTmpName());
//                    prod.put("productProcessor", null != shelfProduct.getAttr1() ? shelfProduct.getAttr1() : "");
//                    prod.put("productController", null != shelfProduct.getAttr2() ? shelfProduct.getAttr2() : "");
                        prod.put("businessLine", ValidUtils.null2NoData(shelfProductVcs.getProdUuid().getBusinessLine()));
                        prod.put("businessDept", ValidUtils.null2NoData(shelfProductVcs.getProdUuid().getBusinessDept()));
                        prod.put("company", ValidUtils.null2NoData(shelfProductVcs.getProdUuid().getCompany()));
                        prod.put("prodCode", ValidUtils.null2NoData(shelfProductVcs.getProdUuid().getProdCode()));
                        prod.put("prodName", ValidUtils.null2NoData(shelfProductVcs.getProdUuid().getProdName()));
                        prod.put("verProd", (shelfProductVcs.getVerProd() == 0 ? "" : ValidUtils.obj2Int(shelfProductVcs.getVerProd())));
                        prod.put("tmpVer", ValidUtils.obj2Int(shelfProductVcs.getVerTem()));    //เพิ่ม 07/07/2020

                        retData.put("product", prod);
                    }
                }
            }

            Utils.sortJSONObjectByKey(retComp, "seqNo", true);
            retData.put("component", retComp);
            retData.put("themeList", ThemeUtils.getThemeList(dbEnv, false));
//            retData.put("templateList", TemplateUtils.getTemplateList(dbEnv));
            retData.put("templateList", new TemplateUtils().getTemplateListByStatus(dbEnv, statusActive));
        } catch (NullPointerException | HibernateException e) {
            logger.info(e.getMessage());
            throw e;
        }
        return retData;
    }

    private List<ShelfProductDtl> getDtl(String dbEnv, JSONArray datas, ArrayList<ShelfProductDtl> list, ShelfProductVcs vcs, String username) {
        try {
            ShelfProductDtl dtl;
            Integer statusActive = StatusUtils.getActive(dbEnv).getStatusCode();
            for (int i = 0; i < datas.length(); i++) {
                JSONObject data = datas.getJSONObject(i);
                if (data.has("id")) {
                    dtl = new ShelfProductDtl();
                    dtl.setUuid(getUUID());
                    dtl.setLkCode(data.getString("id"));
                    dtl.setLkLabel(data.has("label") ? data.getString("label") : "");
                    dtl.setLkValue(data.has("value") ? data.get("value") + "" : "");
                    dtl.setCreateBy(username);
                    dtl.setTrnUuid(vcs);
                    dtl.setStatus(statusActive);
                    dtl.setDtlStatus(statusActive);
                    list.add(dtl);
                } else if (data.has("subUuid")) {
                    if (data.has("value")) {
                        dtl = new ShelfProductDtl();
                        dtl.setUuid(getUUID());
                        dtl.setLkCode(data.getString("subId"));
                        dtl.setLkLabel(data.has("subCompLabel") ? data.getString("subCompLabel") : "");
                        dtl.setLkValue(data.has("value") ? data.get("value") + "" : "");
                        dtl.setCreateBy(username);
                        dtl.setTrnUuid(vcs);
                        dtl.setStatus(statusActive);
                        dtl.setDtlStatus(statusActive);
                        list.add(dtl);
                    }
                    if (data.has("details")) {
                        JSONArray jdtl = data.getJSONArray("details");
                        for (int j = 0; j < jdtl.length(); j++) {
                            JSONObject data2 = jdtl.getJSONObject(j);
                            if (data2.has("id")) {
                                dtl = new ShelfProductDtl();
                                dtl.setUuid(getUUID());
                                dtl.setLkCode(data2.getString("id"));
                                dtl.setLkLabel(data2.has("label") ? data2.getString("label") : "");
                                dtl.setLkValue(data2.has("value") ? data2.get("value") + "" : "");
                                dtl.setCreateBy(username);
                                dtl.setTrnUuid(vcs);
                                dtl.setStatus(statusActive);
                                dtl.setDtlStatus(statusActive);
                                list.add(dtl);
                            } else if (data2.has("subUuid")) {
                                if (data2.has("value")) {
                                    dtl = new ShelfProductDtl();
                                    dtl.setUuid(getUUID());
                                    dtl.setLkCode(data2.getString("subId"));
                                    dtl.setLkLabel(data2.has("subCompLabel") ? data2.getString("subCompLabel") : "");
                                    dtl.setLkValue(data2.has("value") ? data2.get("value") + "" : "");
                                    dtl.setCreateBy(username);
                                    dtl.setTrnUuid(vcs);
                                    dtl.setStatus(statusActive);
                                    dtl.setDtlStatus(statusActive);
                                    list.add(dtl);
                                }
                                if (data2.has("details")) {
                                    JSONArray dtl2 = data2.getJSONArray("details");
                                    for (int k = 0; k < dtl2.length(); k++) {
                                        JSONObject data3 = dtl2.getJSONObject(k);
                                        if (data3.has("id")) {
                                            dtl = new ShelfProductDtl();
                                            dtl.setUuid(getUUID());
                                            dtl.setLkCode(data3.getString("id"));
                                            dtl.setLkLabel(data3.has("label") ? data3.getString("label") : "");
                                            dtl.setLkValue(data3.has("value") ? data3.get("value") + "" : "");
                                            dtl.setCreateBy(username);
                                            dtl.setTrnUuid(vcs);
                                            dtl.setStatus(statusActive);
                                            dtl.setDtlStatus(statusActive);
                                            list.add(dtl);
                                        } else if (data3.has("subUuid")) {
                                            if (data3.has("value")) {
                                                dtl = new ShelfProductDtl();
                                                dtl.setUuid(getUUID());
                                                dtl.setLkCode(data3.getString("subId"));
                                                dtl.setLkLabel(data3.has("subCompLabel") ? data3.getString("subCompLabel") : "");
                                                dtl.setLkValue(data3.has("value") ? data3.get("value") + "" : "");
                                                dtl.setCreateBy(username);
                                                dtl.setTrnUuid(vcs);
                                                dtl.setStatus(statusActive);
                                                dtl.setDtlStatus(statusActive);
                                                list.add(dtl);
                                            }
                                            if (data3.has("details")) {
                                                JSONArray dtl3 = data3.getJSONArray("details");
                                                for (int l = 0; l < dtl3.length(); l++) {
                                                    JSONObject data4 = dtl3.getJSONObject(l);
                                                    if (data4.has("id")) {
                                                        dtl = new ShelfProductDtl();
                                                        dtl.setUuid(getUUID());
                                                        dtl.setLkCode(data4.getString("id"));
                                                        dtl.setLkLabel(data4.has("label") ? data4.getString("label") : "");
                                                        dtl.setLkValue(data4.has("value") ? data4.get("value") + "" : "");
                                                        dtl.setCreateBy(username);
                                                        dtl.setTrnUuid(vcs);
                                                        dtl.setStatus(statusActive);
                                                        dtl.setDtlStatus(statusActive);
                                                        list.add(dtl);
                                                    } else if (data4.has("subUuid")) {
                                                        if (data4.has("value")) {
                                                            dtl = new ShelfProductDtl();
                                                            dtl.setUuid(getUUID());
                                                            dtl.setLkCode(data4.getString("subId"));
                                                            dtl.setLkLabel(data4.has("subCompLabel") ? data4.getString("subCompLabel") : "");
                                                            dtl.setLkValue(data4.has("value") ? data4.get("value") + "" : "");
                                                            dtl.setCreateBy(username);
                                                            dtl.setTrnUuid(vcs);
                                                            dtl.setStatus(statusActive);
                                                            dtl.setDtlStatus(statusActive);
                                                            list.add(dtl);
                                                        }
                                                        if (data4.has("details")) {
                                                            JSONArray dtl4 = data4.getJSONArray("details");
                                                            for (int m = 0; m < dtl4.length(); m++) {
                                                                JSONObject data5 = dtl4.getJSONObject(m);
                                                                if (data5.has("id")) {
                                                                    dtl = new ShelfProductDtl();
                                                                    dtl.setUuid(getUUID());
                                                                    dtl.setLkCode(data5.getString("id"));
                                                                    dtl.setLkLabel(data5.has("label") ? data5.getString("label") : "");
                                                                    dtl.setLkValue(data5.has("value") ? data5.get("value") + "" : "");
                                                                    dtl.setCreateBy(username);
                                                                    dtl.setTrnUuid(vcs);
                                                                    dtl.setStatus(statusActive);
                                                                    dtl.setDtlStatus(statusActive);
                                                                    list.add(dtl);
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            }
        } catch (JSONException | NullPointerException e) {
            logger.info(e.getMessage());
            throw e;
        }
        return list;
    }

    public static JSONArray groupProduct(String dbEnv, ShelfProduct pd) throws ParseException {
        HashMap hmp = new HashMap();
        JSONArray resp = new JSONArray();
        try {
            for (ShelfProductVcs vcs : pd.getShelfProductVcsList()) {
                String kName = vcs.getVerProd() + "_" + vcs.getStatus();
//            ShelfProduct data = (ShelfProduct) hmp.get(key);
                if (!hmp.containsKey(kName) && vcs.getCompUuid() != null) {
//                ShelfProduct shelfProd = (ShelfProduct)hmp.get(key);
//                ShelfProduct p = new ShelfProduct();
//                p.setUuid(pd.getUuid());
//                List<ShelfProductVcs> list = new ArrayList<>();
//                list.add(vcs);
//                shelfProd.getShelfProductVcsList().add(vcs);
                    hmp.put(kName, vcs.getProdUuid());
                }
//            else {
//                data.getShelfProductVcsList().add(vcs);
//                hmp.put(key, vcs.getProdUuid());
//            }
            }
            Set<String> kset = hmp.keySet();
            for (String k2 : kset) {
                ShelfProduct p = (ShelfProduct) hmp.get(k2);
                JSONObject retData = getProductDataByProduct(dbEnv, p.getShelfProductVcsList());
                StatusUtils.Status status = StatusUtils.getStatusByCode(dbEnv, String.valueOf(p.getShelfProductVcsList().get(0).getStatus()));
                retData.getJSONObject("product").put("ver_prod", p.getShelfProductVcsList().get(0).getVerProd()).put("status", status.getStatusCode()).put("status_name", status.getStatusNameTh()).put("status_name_en", status.getStatusNameEn());
                resp.put(retData);
            }
        } catch (JSONException | NullPointerException e) {
            logger.info(e.getMessage());
            throw e;
        }
        return resp;
    }

    public static JSONObject getProductFrontEnd(String dbEnv, ShelfProduct pd) throws ParseException {
        JSONObject resp = new JSONObject();
        List<JSONObject> arr = new ArrayList<>();
        try {
            boolean isActive = false;
            Integer statusActive = StatusUtils.getActive(dbEnv).getStatusCode();
            Integer wait2pause = StatusUtils.getWaittoPause(dbEnv).getStatusCode();
            Integer wait2Terminate = StatusUtils.getWaittoTerminate(dbEnv).getStatusCode();
            String shelfTmpVcs = "";
            for (ShelfProductVcs vcs : pd.getShelfProductVcsList()) {
                if ((vcs.getStatus() == statusActive) || (vcs.getStatus() == wait2pause)
                        || (vcs.getStatus() == wait2Terminate && StatusUtils.isWaitTerminate(vcs.getState(), statusActive))) {
                    isActive = true;
                    if (null == vcs.getCompUuid()) {
                        JSONObject jdtl = new JSONObject();
                        for (ShelfProductDtl dtl : vcs.getShelfProductDtlList()) {
                            if (dtl.getStatus() == statusActive) {
                                jdtl.put(dtl.getLkCode(), ValidUtils.null2NoData(dtl.getLkValue()));
                            }
                        }
                        jdtl.put("uuid", vcs.getProdUuid().getUuid());
                        jdtl.put("prodId", vcs.getProdUuid().getUuid());
                        jdtl.put("prodVer", (vcs.getVerProd() == 0 ? "" : ValidUtils.obj2Int(vcs.getVerProd())));
                        jdtl.put("trnId", getUUID());
                        jdtl.put("prodCode", vcs.getProdUuid().getProdCode());
                        jdtl.put("prodName", vcs.getProdUuid().getProdName());
//                    jdtl.put("productProcessor", null != vcs.getProdUuid() ? vcs.getProdUuid().getAttr1() : "");
//                    jdtl.put("productController", null != vcs.getProdUuid() ? vcs.getProdUuid().getAttr2() : "");
                        jdtl.put("businessLine", ValidUtils.null2NoData(vcs.getProdUuid().getBusinessLine()));
                        jdtl.put("businessDept", ValidUtils.null2NoData(vcs.getProdUuid().getBusinessDept()));
                        jdtl.put("company", ValidUtils.null2NoData(vcs.getProdUuid().getCompany()));
                        resp.put("product", jdtl);
                        JSONObject jtemplate = new JSONObject();
                        ShelfTmpDao tmpDao = new ShelfTmpDao();
                        ShelfTmp tmp = tmpDao.getShelfTmp(dbEnv, vcs.getTemUuid());
                        if (null != tmp) {
                            jtemplate.put("uuid", tmp.getUuid());
                            jtemplate.put("name", tmp.getTmpName());
                            JSONArray jarray = new JSONArray();
                            for (ShelfTmpVcs tv : tmp.getShelfTmpVcsList()) {
//                            ดึงที่ version ของ vcs ที่มาจาก product_vcs //Not sure  ?
                                if (tv.getVersion() == vcs.getVerTem()) {
                                    jarray = new JSONArray(tv.getAttr1());
                                }
                            }
                            List<JSONObject> listTmpComp = new ArrayList<>();
                            for (int i = 0; i < jarray.length(); i++) {
                                listTmpComp.add(jarray.getJSONObject(i));
                            }
                            Utils.sortJSONObjectByKey(listTmpComp, "seqNo", true);
                            jtemplate.put("component", jarray);
                        }
                        resp.put("template", jtemplate);
                        Gson gson = new Gson();
                        JSONObject json = new JSONObject(gson.toJson(null != vcs.getThemeUuid() ? vcs.getThemeUuid().getValue() : new JSONObject()));
                        resp.put("theme", json.has("info") ? json.getJSONObject("info") : json);
                    } else {
                        ShelfTmpDao tmpDao = new ShelfTmpDao();
                        ShelfTmp shelfTmp = tmpDao.getShelfTmp(dbEnv, vcs.getTemUuid());
//                List<ShelfTmpDetail> tmpDtl = new ArrayList<>();
                        if (shelfTmpVcs.equalsIgnoreCase("")) {
                            for (ShelfTmpVcs tmpVcs : shelfTmp.getShelfTmpVcsList()) {
                                if (tmpVcs.getVersion() == vcs.getVerTem()) {
                                    shelfTmpVcs = tmpVcs.getUuid();
                                    break;
                                }
                            }
                        }

                        ShelfTmpDetailDao shelfTmpDtlDao = new ShelfTmpDetailDao();
                        ShelfComp shelfComp = shelfTmpDtlDao.getShelfComponentByCompUUIDAndTemplateVCS(dbEnv, vcs.getCompUuid().getUuid(), shelfTmpVcs, true);
                        if (shelfComp != null) {
//                    Product Info & Customer Info
                            if (("007".equalsIgnoreCase(vcs.getCompUuid().getCompCode())
                                    || "009".equalsIgnoreCase(vcs.getCompUuid().getCompCode())
                                    || "012".equalsIgnoreCase(vcs.getCompUuid().getCompCode())
                                    || "013".equalsIgnoreCase(vcs.getCompUuid().getCompCode())) && null != vcs.getCompStatus() && vcs.getCompStatus().equals(statusActive)) {
                                JSONObject tmp = new JSONObject();
                                JSONObject tmp2 = new JSONObject();
                                for (ShelfProductDtl dtl : vcs.getShelfProductDtlList()) {
                                    if (dtl.getStatus() == statusActive) {
                                        tmp.put(dtl.getLkCode().trim(), dtl.getLkValue());
                                        tmp2.put(dtl.getLkCode().trim(), dtl.getLkLabel());
//                                tmp.put(dtl.getLkCode(), dtl.getLkValue());
                                    }
                                }
                                List<ShelfCompDtl> shelfCompDtlList = vcs.getCompUuid().getShelfCompDtlList();
                                shelfCompDtlList.sort(Comparator.comparing(ShelfCompDtl::getSeq));
                                JSONObject ret = ProductUtils.getInitialProductComponentByUUID(dbEnv, shelfComp, shelfCompDtlList, false, ((null != vcs.getProdUuid()) ? vcs.getProdUuid().getUuid() : null));
                                JSONObject jdtl = new JSONObject();
                                jdtl.put("uuid", vcs.getUuid());
                                jdtl.put("page", shelfComp.getAttr2());
                                jdtl.put("compUuid", shelfComp.getUuid());
                                jdtl.put("seqNo", shelfComp.getSeqNo());
                                jdtl.put("compName", shelfComp.getCompName());
                                JSONArray array = ret.getJSONArray("subComp");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject jtem = array.getJSONObject(i);
                                    if (jtem.has("id")) {
                                        jtem.put("value", tmp.has(jtem.getString("id").trim()) ? tmp.getString(jtem.getString("id").trim()) : "");
                                    } else if (jtem.has("details")) {
                                        if (jtem.has("value")) {
                                            jtem.put("value", tmp.has(jtem.getString("subId").trim()) ? tmp.getString(jtem.getString("subId").trim()) : "");
                                        }
                                        JSONArray array2 = jtem.getJSONArray("details");
                                        for (int j = 0; j < array2.length(); j++) {
                                            JSONObject jtem2 = array2.getJSONObject(j);
                                            if (jtem2.has("id")) {
                                                jtem2.put("value", tmp.has(jtem2.getString("id").trim()) ? tmp.getString(jtem2.getString("id").trim()) : "");
                                            } else if (jtem2.has("details")) {
                                                if (jtem2.has("value")) {
                                                    jtem2.put("value", tmp.has(jtem2.getString("subId").trim()) ? tmp.getString(jtem2.getString("subId").trim()) : "");
                                                }
//                                        System.out.println("array3 : " + jtem2.getJSONArray("details"));
                                                JSONArray array3 = jtem2.getJSONArray("details");
                                                for (int k = 0; k < array3.length(); k++) {
                                                    JSONObject jtem3 = array3.getJSONObject(k);
                                                    if (jtem3.has("id")) {
                                                        jtem3.put("value", tmp.has(jtem3.getString("id").trim()) ? tmp.getString(jtem3.getString("id").trim()) : "");
                                                        if (("radioList".equalsIgnoreCase(jtem3.getString("id"))
                                                                || "summaryList".equalsIgnoreCase(jtem3.getString("id"))
                                                                || "packageList".equalsIgnoreCase(jtem3.getString("id"))) && !"".equals(jtem3.get("value"))) {
                                                            jtem3.put("data", new JSONArray(jtem3.getString("value")));
                                                        }

                                                    } else if (jtem3.has("details")) {
                                                        if (jtem3.has("value")) {
                                                            jtem3.put("value", tmp.has(jtem3.getString("subId").trim()) ? tmp.getString(jtem3.getString("subId").trim()) : "");
                                                        }
//                                                System.out.println("array4 : " + jtem3.getJSONArray("details"));
                                                        JSONArray array4 = jtem3.getJSONArray("details");
                                                        for (int l = 0; l < array4.length(); l++) {
                                                            JSONObject jtem4 = array4.getJSONObject(l);
                                                            if (jtem4.has("id")) {
                                                                jtem4.put("value", tmp.has(jtem4.getString("id").trim()) ? tmp.getString(jtem4.getString("id").trim()) : "");
                                                            } else if (jtem4.has("details")) {
                                                                if (jtem4.has("value")) {
                                                                    jtem4.put("value", tmp.has(jtem4.getString("subId").trim()) ? tmp.getString(jtem4.getString("subId").trim()) : "");
                                                                }
//                                                        System.out.println("array5 : " + jtem4.getJSONArray("details"));
                                                                JSONArray array5 = jtem4.getJSONArray("details");
                                                                for (int m = 0; m < array5.length(); m++) {
                                                                    JSONObject jtem5 = array5.getJSONObject(m);
                                                                    if (jtem5.has("id")) {
                                                                        jtem5.put("value", tmp.has(jtem5.getString("id").trim()) ? tmp.getString(jtem5.getString("id").trim()) : "");
                                                                        if (jtem5.getString("id").startsWith("chkIncome")
                                                                                || jtem5.getString("id").startsWith("chkExpend")
                                                                                || jtem5.getString("id").startsWith("chkDebt")) {
                                                                            jtem5.put("label", tmp2.has(jtem5.getString("id").trim()) ? tmp2.getString(jtem5.getString("id").trim()) : "");
                                                                        }
                                                                    } else if (jtem5.has("details")) {
                                                                        if (jtem5.has("value")) {
                                                                            jtem5.put("value", tmp.has(jtem5.getString("subId").trim()) ? tmp.getString(jtem5.getString("subId").trim()) : "");
                                                                        }
//                                                                System.out.println("array6 : " + jtem5.getJSONArray("details"));
                                                                        JSONArray array6 = jtem5.getJSONArray("details");
                                                                        for (int n = 0; n < array6.length(); n++) {
                                                                            JSONObject jtem6 = array6.getJSONObject(n);
                                                                            if (jtem6.has("id")) {
                                                                                jtem6.put("value", tmp.has(jtem6.getString("id").trim()) ? tmp.getString(jtem6.getString("id").trim()) : "");
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                jdtl.put("details", array);
//                        arr.put(jdtl);
                                arr.add(jdtl);
                            } else {
                                if (null != vcs.getCompStatus() && vcs.getCompStatus().equals(statusActive)) {
                                    JSONObject jdtl = new JSONObject();
                                    jdtl.put("uuid", vcs.getUuid());
                                    jdtl.put("compUuid", shelfComp.getUuid());
                                    jdtl.put("page", shelfComp.getAttr2());
                                    jdtl.put("seqNo", shelfComp.getSeqNo());
                                    jdtl.put("compName", shelfComp.getCompName());
                                    for (ShelfProductDtl dtl : vcs.getShelfProductDtlList()) {
                                        if (dtl.getStatus() == statusActive) {
                                            if (null != dtl.getAttr1() && !"".equals(dtl.getAttr1())) {
                                                JSONObject obj = new JSONObject();
                                                obj.put("value", dtl.getLkValue());
                                                obj.put("attr1", dtl.getAttr1());
                                                jdtl.put(dtl.getLkCode(), obj.toString());
                                            } else {
                                                jdtl.put(dtl.getLkCode(), dtl.getLkValue());
                                            }
                                        }
                                    }
//                        arr.put(jdtl);
                                    arr.add(jdtl);
                                }
                            }
                        }
                    }
                }
            }
            if (isActive) {
                Utils.sortJSONObjectByKey(arr, "seqNo", true);
                resp.put("component", arr);
            } else {
                resp.put("status", 500)
                        .put("description", StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0001"));
            }
        } catch (NullPointerException | HibernateException e) {
            throw e;
        }
        return resp;
    }

    private boolean isId(String id, String subId) {
        List<String> groups = new ArrayList<>();
        groups.add("g003");
        groups.add("g004");
        groups.add("g005");
        List<String> array = new ArrayList<>();
        array.add("citizenID");
        array.add("laserNumber");
        array.add("name");
        array.add("surname");
        array.add("dateOfBirth");
        array.add("perAddress");
        array.add("currAddress");
        array.add("occupation");
        array.add("phoneNumber");
        array.add("default1");
        array.add("default2");
        array.add("default3");
        return (groups.contains(subId) && array.contains(id));
    }

    public static List<ShelfProductVcs>[] orderProductByVersion(List<ShelfProductVcs> shelfProductVcsList) {
        Map<Integer, List<ShelfProductVcs>> shelfProductVcsListGrouped = shelfProductVcsList.stream().collect(Collectors.groupingBy(w -> w.getVerProd()));
        List<Integer> sortedKName = new ArrayList(shelfProductVcsListGrouped.keySet());
        Collections.sort(sortedKName, Collections.reverseOrder());
        List<ShelfProductVcs>[] listData = new ArrayList[shelfProductVcsListGrouped.size()];
        int i = 0;
        try {
            if (sortedKName.get(sortedKName.size() - 1) == 0) {
                listData[0] = shelfProductVcsListGrouped.get(sortedKName.get(sortedKName.size() - 1));
                i++;
                sortedKName.remove(sortedKName.size() - 1);
            }
            for (int j = 0; j < sortedKName.size(); j++) {
                listData[i] = shelfProductVcsListGrouped.get(sortedKName.get(j));
                i++;
            }
        } catch (NullPointerException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
        }
        return listData;
    }

    public static JSONObject getProductDataByProduct2(String dbEnv, List<ShelfProductVcs> shelfProductVcsList) {
        JSONObject retData = new JSONObject();
        try {
            Integer statusActive = StatusUtils.getActive(dbEnv).getStatusCode();
            Integer statusWaitDelete = StatusUtils.getWaittoDelete(dbEnv).getStatusCode();
            Integer statusDelete = StatusUtils.getDelete(dbEnv).getStatusCode();
            Integer statusTerminate = StatusUtils.getTerminate(dbEnv).getStatusCode();
            boolean flagDel = true, flagEdit = true;
            for (ShelfProductVcs vcs : shelfProductVcsList) {
                if (statusWaitDelete.equals(vcs.getStatus()) || statusDelete.equals(vcs.getStatus()) || statusTerminate.equals(vcs.getStatus())) {
                    flagEdit = false;
                    flagDel = false;
                } else if (statusActive.equals(vcs.getStatus())) {
                    flagDel = false;
                }
            }
            List<ShelfProductVcs>[] prodVerArr = ProductUtils.orderProductByVersion(shelfProductVcsList);
            List<ShelfProductVcs> vcsList = prodVerArr[0];
            for (int i = 0; i < vcsList.size(); i++) {
                ShelfProductVcs shelfProductVcs = vcsList.get(i);
//                if (null == shelfProductVcs.getCompStatus()) {
                if (shelfProductVcs.getCompUuid() == null) {
                    JSONObject prod = getInitialProduct();
                    prod.put("uuid", shelfProductVcs.getProdUuid().getUuid());
                    for (ShelfProductDtl prodDtl : shelfProductVcs.getShelfProductDtlList()) {
                        prod.put(prodDtl.getLkCode(), ValidUtils.null2NoData(prodDtl.getLkValue()));
                    }
                    ShelfTmp shelfTmp = new ShelfTmpDao().getShelfTmp(dbEnv, shelfProductVcs.getTemUuid());
                    StatusUtils.Status status = StatusUtils.getStatusByCode(dbEnv, String.valueOf(shelfProductVcs.getStatus()));
                    prod.put("status", shelfProductVcs.getStatus());
                    prod.put("statusNameTh", status.getStatusNameTh() == null ? "" : status.getStatusNameTh());
                    prod.put("statusNameEn", status.getStatusNameEn() == null ? "" : status.getStatusNameEn());
                    prod.put("template", shelfTmp.getUuid() == null ? "" : shelfTmp.getUuid());
                    prod.put("templateName", shelfTmp.getTmpName() == null ? "" : shelfTmp.getTmpName());
                    prod.put("businessLine", ValidUtils.null2NoData(shelfProductVcs.getProdUuid().getBusinessLine()));
                    prod.put("businessDept", ValidUtils.null2NoData(shelfProductVcs.getProdUuid().getBusinessDept()));
                    prod.put("company", ValidUtils.null2NoData(shelfProductVcs.getProdUuid().getCompany()));
                    prod.put("verProd", (shelfProductVcs.getVerProd() == 0 ? "" : ValidUtils.obj2Int(shelfProductVcs.getVerProd())));
                    prod.put("edit", flagEdit);
                    prod.put("delete", flagDel);
                    prod.put("prodCode", ValidUtils.null2NoData(shelfProductVcs.getProdUuid().getProdCode()));
                    prod.put("prodName", ValidUtils.null2NoData(shelfProductVcs.getProdUuid().getProdName()));
                    prod.put("updateDate", ValidUtils.null2Separator(DateUtils.getDisplayEnDate(shelfProductVcs.getUpdateAt(), "yyyy-MM-dd"), DateUtils.getDisplayEnDate(shelfProductVcs.getCreateAt(), "yyyy-MM-dd")));
                    prod.put("updateBy", ValidUtils.null2Separator(shelfProductVcs.getUpdateBy(), shelfProductVcs.getCreateBy()));
                    /*เพิ่ม 05/06/2020*/ prod.put("CreateBy", ValidUtils.null2NoData(shelfProductVcs.getCreateBy()));
                    prod.put("tmpVer", ValidUtils.obj2Int(shelfProductVcs.getVerTem()));    //เพิ่ม 07/07/2020
                    retData.put("product", prod);
                    break;
                }
//                }
            }
        } catch (NullPointerException | HibernateException e) {
            //e.printStackTrace();
            logger.info(e.getMessage());
        }
        return retData;
    }

    public static JSONArray infoProducts(String dbEnv, String prodUuid, int status, int verProd) throws ParseException {
        JSONArray productList = new JSONArray();

//            ShelfProductDao shelfProductDao = new ShelfProductDao();
//            ShelfProduct shelfProduct = shelfProductDao.getShelfProductByUUID(dbEnv, prodUuid);
        ShelfProductVcsDao shelfProductVcsDao = new ShelfProductVcsDao();
        try {
//            List<ShelfProductVcs> shelfProductVcs = shelfProductVcsDao.getListShelfProduct(dbEnv, prodUuid, status, verProd);
            Integer arStatus[] = {status};
            List<ShelfProductVcs> shelfProductVcs = shelfProductVcsDao.getListShelfProductVcsListByStatus(dbEnv, prodUuid, verProd, arStatus);
            if (shelfProductVcs.size() > 0) {
//            System.out.println("size : " + shelfProductVcs.size());
                List<ShelfProductVcs>[] data = ProductUtils.orderProductByVersion(shelfProductVcs);
//            JSONObject retData = ProductUtils.getProductDataByProduct(dbEnv, shelfProduct);
//            Object[] data = ProductUtils.orderProductByVersion(shelfProduct.getShelfProductVcsList());
//            int statusVal = 0;
                for (int i = 0; i < data.length; i++) {
//                JSONObject product = new JSONObject();
                    List<ShelfProductVcs> vcsList = data[i];
                    List<ShelfProductDtl> dtlList = new ArrayList<>();
                    JSONObject prod = ProductUtils.getInitialProduct();
                    ShelfProductVcs compVcs = new ShelfProductVcs();
                    for (ShelfProductVcs vcs : vcsList) {
                        if (vcs.getCompUuid() == null) {
                            dtlList = vcs.getShelfProductDtlList();
                            compVcs = vcs;
//                        statusVal = vcs.getStatus();
//                        verProd = ValidUtils.obj2Int(vcs.getVerProd());
//                        product.put("productProcessor", null != shelfProduct.getAttr1() ? shelfProduct.getAttr1() : "");
//                        product.put("productController", null != shelfProduct.getAttr2() ? shelfProduct.getAttr2() : "");
                            break;
                        }
                    }
                    prod.put("uuid", compVcs.getProdUuid().getUuid());
                    for (ShelfProductDtl dtl : dtlList) {
                        prod.put(dtl.getLkCode(), ValidUtils.null2NoData(dtl.getLkValue()));
                    }
//                for (ShelfProductDtl prodDtl : compVcs.getShelfProductDtlList()) {
//                    prod.put(prodDtl.getLkCode(), prodDtl.getLkValue());
//                }
                    ShelfTmp shelfTmp = new ShelfTmpDao().getShelfTmp(dbEnv, compVcs.getTemUuid());
                    StatusUtils.Status s = StatusUtils.getStatusByCode(dbEnv, String.valueOf(compVcs.getStatus()));
                    prod.put("status", s.getStatusCode() == null ? "" : s.getStatusCode());
                    prod.put("statusName", s.getStatusNameTh() == null ? "" : s.getStatusNameTh());
                    prod.put("statusNameEn", s.getStatusNameEn() == null ? "" : s.getStatusNameEn());
                    prod.put("template", shelfTmp.getUuid() == null ? "" : shelfTmp.getUuid());
                    prod.put("theme", compVcs.getThemeUuid().getUuid());
                    prod.put("templateName", shelfTmp.getTmpName() == null ? "" : shelfTmp.getTmpName());
//                    prod.put("productProcessor", null != shelfProduct.getAttr1() ? shelfProduct.getAttr1() : "");
//                    prod.put("productController", null != shelfProduct.getAttr2() ? shelfProduct.getAttr2() : "");
                    prod.put("businessLine", ValidUtils.null2NoData(compVcs.getProdUuid().getBusinessLine()));
                    prod.put("businessDept", ValidUtils.null2NoData(compVcs.getProdUuid().getBusinessDept()));
                    prod.put("prodCode", ValidUtils.null2NoData(compVcs.getProdUuid().getProdCode()));
                    prod.put("prodName", ValidUtils.null2NoData(compVcs.getProdUuid().getProdName()));
                    prod.put("company", ValidUtils.null2NoData(compVcs.getProdUuid().getCompany()));
                    prod.put("verProd", ValidUtils.obj2Int(compVcs.getVerProd()));
                    prod.put("tmpVer", ValidUtils.obj2Int(compVcs.getVerTem()));    //เพิ่ม 07/07/2020
//                ShelfTmp shelfTmp = new ShelfTmpDao().getShelfTmp(dbEnv, product.getString("template"));
//                StatusUtils.Status st = StatusUtils.getStatusByCode(dbEnv, String.valueOf(statusVal));
//                product.put("status", st.getStatusCode() == null ? "" : st.getStatusCode());
//                product.put("statusName", st.getStatusNameTh() == null ? "" : st.getStatusNameTh());
//                product.put("template", shelfTmp.getUuid() == null ? "" : shelfTmp.getUuid());
//                product.put("templateName", shelfTmp.getTmpName() == null ? "" : shelfTmp.getTmpName());
//                product.put("verProd", verProd);
                    JSONObject retProduct = ProductUtils.getProductDataByProduct(dbEnv, vcsList);
                    retProduct.put("product", prod);
                    productList.put(retProduct);
                }
            }
        } catch (HibernateException | NullPointerException e) {
            throw e;
        }
        return productList;
    }

    public static JSONObject sendToApproveProduct(String dbEnv, String uuid, Integer statusWaitApp, Date sysdate, String username, String respCode, String remark) {
        JSONObject result = new JSONObject().put("status", 200).put("description", "");
        try {
            ShelfProductDao dao = new ShelfProductDao();
            ShelfProductVcsDao vcsDao = new ShelfProductVcsDao();
            ShelfProduct sp = new ShelfProduct();
            Integer sInprogress = StatusUtils.getInprogress(dbEnv).getStatusCode();
            Integer[] inprogress = {sInprogress};
            List<ShelfProductVcs> vcsInpList = vcsDao.getListShelfProductVcsListByStatus(dbEnv, uuid, inprogress);
            for (ShelfProductVcs vcs : vcsInpList) {
                vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(statusWaitApp)));
                vcs.setStatus(statusWaitApp);
                vcs.setUpdateAt(sysdate);
                vcs.setUpdateBy(username);
                if (null == vcs.getCompUuid()) {
                    sp = vcs.getProdUuid();
                }
                if (!respCode.isEmpty()) {
                    vcs.setAttr2(StatusUtils.setStatus(vcs.getAttr2(), ValidUtils.null2NoData(respCode)));
                }
                if (!remark.isEmpty()) {
                    vcs.setAttr3(StatusUtils.setRemark(vcs.getAttr3(), ValidUtils.null2NoData(remark)));
                }
            }
            sp.setStatus(statusWaitApp);
            sp.setUpdateAt(sysdate);
            sp.setUpdateBy(username);
            sp.setShelfProductVcsList(vcsInpList);
            dao.updateShelfProduct(dbEnv, sp, username);
        } catch (HibernateException | NullPointerException e) {
            //e.printStackTrace();
            logger.info(e.getMessage());
            result.put("status", 500).put("description", "" + e);
            throw e;
        }
        return result;
    }

    public static JSONObject approveProduct(String dbEnv, String uuid, ShelfProduct prod, List<ShelfProductVcs> list, String termsNCondition) {
        JSONObject result = new JSONObject().put("status", 200).put("description", "");
        try {
            Integer statusActive = StatusUtils.getActive(dbEnv).getStatusCode();
            Integer statusInactive = StatusUtils.getInActive(dbEnv).getStatusCode();
            prod.setShelfProductVcsList(list);
            ShelfProductDao dao = new ShelfProductDao();
            dao.updateShelfProduct(dbEnv, prod, termsNCondition, statusActive, statusInactive);  //save product , product vcs 
        } catch (HibernateException | NullPointerException e) {
            //e.printStackTrace();
            logger.info(e.getMessage());
            result.put("status", 500).put("description", "" + e);
        }
        return result;
    }

    public static JSONObject rejectApprove(String dbEnv, String uuid, int verProd, String respCode, String username, String remark) {
        JSONObject result = new JSONObject().put("status", 200).put("description", "");
        try {
            Date sysdate = new Date();
            ShelfProductDao dao = new ShelfProductDao();
            ShelfProductVcsDao vcsDao = new ShelfProductVcsDao();
            Integer reject = StatusUtils.getReject(dbEnv).getStatusCode();
            Integer statusWaitApp = StatusUtils.getWaittoApprove(dbEnv).getStatusCode();
            Integer[] waStatus = {statusWaitApp};
            List<ShelfProductVcs> vcsWaitList = vcsDao.getListShelfProductVcsListByStatus(dbEnv, uuid, verProd, waStatus);
            if (null != vcsWaitList && vcsWaitList.size() > 0) {
                ShelfProduct sp = vcsWaitList.get(0).getProdUuid();
                sp.setStatus(reject);
                sp.setUpdateBy(username);
                sp.setUpdateAt(sysdate);
                for (ShelfProductVcs vcs : vcsWaitList) {
                    vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(reject)));
                    vcs.setStatus(reject);
                    vcs.setUpdateAt(sysdate);
                    vcs.setUpdateBy(username);
                    if (!respCode.isEmpty()) {
                        vcs.setAttr2(StatusUtils.setStatus(vcs.getAttr2(), ValidUtils.null2NoData(respCode)));
                    }
                    if (!remark.isEmpty()) {
                        vcs.setAttr3(StatusUtils.setRemark(vcs.getAttr3(), ValidUtils.null2NoData(remark)));
                    }
                }
                sp.setShelfProductVcsList(vcsWaitList);
                dao.updateShelfProduct(dbEnv, sp, username);
            }
            Integer statusWaitApp2 = StatusUtils.getWaittoApprove2(dbEnv).getStatusCode();
            Integer[] wa2Status = {statusWaitApp2};
            List<ShelfProductVcs> vcsWait2List = vcsDao.getListShelfProductVcsListByStatus(dbEnv, uuid, verProd, wa2Status);
            if (null != vcsWait2List && vcsWait2List.size() > 0) {
                ShelfProduct sp = vcsWait2List.get(0).getProdUuid();
                sp.setStatus(reject);
                sp.setUpdateBy(username);
                sp.setUpdateAt(sysdate);
                for (ShelfProductVcs vcs : vcsWait2List) {
                    vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(reject)));
                    vcs.setStatus(reject);
                    vcs.setUpdateAt(sysdate);
                    vcs.setUpdateBy(username);
                    if (!respCode.isEmpty()) {
                        vcs.setAttr2(StatusUtils.setStatus(vcs.getAttr2(), ValidUtils.null2NoData(respCode)));
                    }
                    if (!remark.isEmpty()) {
                        vcs.setAttr3(StatusUtils.setRemark(vcs.getAttr3(), ValidUtils.null2NoData(remark)));
                    }
                }
                sp.setShelfProductVcsList(vcsWait2List);
                dao.updateShelfProduct(dbEnv, sp, username);
            }
        } catch (HibernateException | NullPointerException e) {
            //e.printStackTrace();
            logger.info(e.getMessage());
            result.put("status", 500).put("description", "" + e);
        }
        return result;
    }

    public static JSONObject pauseProduct(String dbEnv, String uuid, int verProd, String respCode, String username, String remark, Integer nextStatus, Integer[] currStatus) {
        JSONObject result = new JSONObject().put("status", 200).put("description", "");
        try {
            Integer rejectStatus = StatusUtils.getReject(dbEnv).getStatusCode();
            Integer activeStatus = StatusUtils.getActive(dbEnv).getStatusCode();
            Date sysdate = new Date();
            ShelfProductDao dao = new ShelfProductDao();
            ShelfProductVcsDao vcsDao = new ShelfProductVcsDao();
            List<ShelfProductVcs> vcsActiveList = vcsDao.getListShelfProductVcsListByStatus(dbEnv, uuid, verProd, currStatus);
            if (null != vcsActiveList && vcsActiveList.size() > 0) {
                ShelfProduct sp = vcsActiveList.get(0).getProdUuid();
                sp.setStatus(nextStatus);
                sp.setUpdateBy(username);
                sp.setUpdateAt(sysdate);
                for (ShelfProductVcs vcs : vcsActiveList) {
                    vcs.setAttr1((ValidUtils.null2NoData(vcs.getAttr1()) + " " + ValidUtils.null2NoData(remark)).trim());
                    if (nextStatus.equals(activeStatus)) {
                        vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(rejectStatus)));
                        vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(nextStatus)));
                    } else {
                        vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(nextStatus)));
                    }
                    vcs.setStatus(nextStatus);
                    vcs.setUpdateAt(sysdate);
                    vcs.setUpdateBy(username);
                    if (!respCode.isEmpty()) {
                        vcs.setAttr2(StatusUtils.setStatus(vcs.getAttr2(), ValidUtils.null2NoData(respCode)));
                    }
                    if (!remark.isEmpty()) {
                        vcs.setAttr3(StatusUtils.setRemark(vcs.getAttr3(), ValidUtils.null2NoData(remark)));
                    }
                }
                sp.setShelfProductVcsList(vcsActiveList);
                dao.updateShelfProduct(dbEnv, sp, username);
            }
        } catch (HibernateException | NullPointerException e) {
            //e.printStackTrace();
            logger.info(e.getMessage());
            result.put("status", 500).put("description", "" + e);
        }
        return result;
    }

    public static JSONObject startProduct(String dbEnv, String uuid, int verProd, String respCode, String username, String remark, Integer currStatus, Integer nextStatus) {
        JSONObject result = new JSONObject().put("status", 200).put("description", "");
        try {
            Date sysdate = new Date();
            ShelfProductDao dao = new ShelfProductDao();
            ShelfProductVcsDao vcsDao = new ShelfProductVcsDao();
            Integer[] pauseStatus = {currStatus};
            List<ShelfProductVcs> vcsPauseList = vcsDao.getListShelfProductVcsListByStatus(dbEnv, uuid, verProd, pauseStatus);
            if (null != vcsPauseList && vcsPauseList.size() > 0) {
                ShelfProduct sp = vcsPauseList.get(0).getProdUuid();
                sp.setStatus(nextStatus);
                sp.setUpdateAt(sysdate);
                sp.setUpdateBy(username);
                for (ShelfProductVcs vcs : vcsPauseList) {
                    vcs.setAttr1((ValidUtils.null2NoData(vcs.getAttr1()) + " " + ValidUtils.null2NoData(remark)).trim());
                    vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(nextStatus)));
                    vcs.setStatus(nextStatus);
                    vcs.setUpdateAt(sysdate);
                    vcs.setUpdateBy(username);
                    if (!respCode.isEmpty()) {
                        vcs.setAttr2(StatusUtils.setStatus(vcs.getAttr2(), ValidUtils.null2NoData(respCode)));
                    }
                    if (!remark.isEmpty()) {
                        vcs.setAttr3(StatusUtils.setRemark(vcs.getAttr3(), ValidUtils.null2NoData(remark)));
                    }
                }
                sp.setShelfProductVcsList(vcsPauseList);
                dao.updateShelfProduct(dbEnv, sp, username);
            }
        } catch (HibernateException | NullPointerException e) {
            //e.printStackTrace();
            logger.info(e.getMessage());
            result.put("status", 500).put("description", "" + e);
        }
        return result;
    }

    public static JSONObject sendDeleteProduct(String dbEnv, String uuid, int verProd, String respCode, String username, String remark) {
        JSONObject result = new JSONObject().put("status", 200).put("description", "");
        try {
            Date sysdate = new Date();
            ShelfProductDao dao = new ShelfProductDao();
            Integer status = StatusUtils.getWaittoDelete(dbEnv).getStatusCode();
            ShelfProduct sp = dao.getShelfProductByUUID(dbEnv, uuid);
            sp.setStatus(status);
            sp.setUpdateAt(sysdate);
            sp.setAttr1((ValidUtils.null2NoData(sp.getAttr1()) + " " + ValidUtils.null2NoData(remark)).trim());
            sp.setUpdateBy(username);
            for (ShelfProductVcs vcs : sp.getShelfProductVcsList()) {
                vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(status)));
                vcs.setStatus(status);
                vcs.setUpdateAt(sysdate);
                vcs.setUpdateBy(username);
                if (!respCode.isEmpty()) {
                    vcs.setAttr2(StatusUtils.setStatus(vcs.getAttr2(), ValidUtils.null2NoData(respCode)));
                }
                if (!remark.isEmpty()) {
                    vcs.setAttr3(StatusUtils.setRemark(vcs.getAttr3(), ValidUtils.null2NoData(remark)));
                }
            }
            dao.updateShelfProduct(dbEnv, sp, username);
        } catch (HibernateException | NullPointerException e) {
            //e.printStackTrace();
            logger.info(e.getMessage());
            result.put("status", 500).put("description", "" + e);
        }
        return result;
    }

    public static JSONObject rejectDelete(String dbEnv, String uuid, int verProd, String respCode, String username, String remark) {
        JSONObject result = new JSONObject().put("status", 200).put("description", "");
        try {
            Integer rejectStatus = StatusUtils.getReject(dbEnv).getStatusCode();
            Date sysdate = new Date();
            ShelfProductDao dao = new ShelfProductDao();
            Integer status = StatusUtils.getReject(dbEnv).getStatusCode();
            ShelfProduct sp = dao.getShelfProductByUUID(dbEnv, uuid);
            sp.setStatus(status);
            sp.setUpdateAt(sysdate);
            sp.setUpdateBy(username);
            sp.setAttr1((ValidUtils.null2NoData(sp.getAttr1()) + " " + ValidUtils.null2NoData(remark)).trim());
            for (ShelfProductVcs vcs : sp.getShelfProductVcsList()) {
                String states[] = vcs.getState().split("/");
                StatusUtils.Status st = null;
                if (states.length - 2 >= 0) {
                    st = StatusUtils.getStatusByCode(dbEnv, states[states.length - 2]);
                } else {
                    st = StatusUtils.getStatusByCode(dbEnv, states[states.length - 1]);
                }
                vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(rejectStatus)));
                vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(st.getStatusCode())));
                vcs.setStatus(st.getStatusCode());
                vcs.setUpdateAt(sysdate);
                vcs.setUpdateBy(username);
                if (!respCode.isEmpty()) {
                    vcs.setAttr2(StatusUtils.setStatus(vcs.getAttr2(), ValidUtils.null2NoData(respCode)));
                }
                if (!remark.isEmpty()) {
                    vcs.setAttr3(StatusUtils.setRemark(vcs.getAttr3(), ValidUtils.null2NoData(remark)));
                }
            }
            dao.updateShelfProduct(dbEnv, sp, username);
        } catch (HibernateException | NullPointerException e) {
            //e.printStackTrace();
            logger.info(e.getMessage());
            result.put("status", 500).put("description", "" + e);
        }
        return result;
    }

    public static JSONObject deleteProduct(String dbEnv, String uuid, int verProd, String respCode, String username, String remark) {
        JSONObject result = new JSONObject().put("status", 200).put("description", "");
        try {
            Date sysdate = new Date();
            ShelfProductDao dao = new ShelfProductDao();
            Integer status = StatusUtils.getDelete(dbEnv).getStatusCode();
            ShelfProduct sp = dao.getShelfProductByUUID(dbEnv, uuid);
            sp.setStatus(status);
            sp.setUpdateAt(sysdate);
            sp.setUpdateBy(username);
            sp.setAttr1((ValidUtils.null2NoData(sp.getAttr1()) + " " + ValidUtils.null2NoData(remark)).trim());
            for (ShelfProductVcs vcs : sp.getShelfProductVcsList()) {
                vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(status)));
                vcs.setStatus(status);
                vcs.setUpdateAt(sysdate);
                vcs.setUpdateBy(username);
                if (!respCode.isEmpty()) {
                    vcs.setAttr2(StatusUtils.setStatus(vcs.getAttr2(), ValidUtils.null2NoData(respCode)));
                }
                if (!remark.isEmpty()) {
                    vcs.setAttr3(StatusUtils.setRemark(vcs.getAttr3(), ValidUtils.null2NoData(remark)));
                }
            }
            dao.updateShelfProduct(dbEnv, sp, username);
        } catch (HibernateException | NullPointerException e) {
            //e.printStackTrace();
            logger.info(e.getMessage());
            result.put("status", 500).put("description", "" + e);
        }
        return result;
    }

    public static JSONObject terminateProduct(String dbEnv, String uuid, int verProd, String respCode, String username, String remark, Integer status) {
        JSONObject result = new JSONObject().put("status", 200).put("description", "");
        try {
            Date sysdate = new Date();
            ShelfProductDao dao = new ShelfProductDao();
            ShelfProduct sp = dao.getShelfProductByUUID(dbEnv, uuid);
            sp.setStatus(status);
            sp.setUpdateAt(sysdate);
            sp.setUpdateBy(username);
            sp.setAttr1((ValidUtils.null2NoData(sp.getAttr1()) + " " + ValidUtils.null2NoData(remark)).trim());
            for (ShelfProductVcs vcs : sp.getShelfProductVcsList()) {
                vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(status)));
                vcs.setStatus(status);
                vcs.setUpdateAt(sysdate);
                vcs.setUpdateBy(username);
                if (!respCode.isEmpty()) {
                    vcs.setAttr2(StatusUtils.setStatus(vcs.getAttr2(), ValidUtils.null2NoData(respCode)));
                }
                if (!remark.isEmpty()) {
                    vcs.setAttr3(StatusUtils.setRemark(vcs.getAttr3(), ValidUtils.null2NoData(remark)));
                }
            }
            dao.updateShelfProduct(dbEnv, sp, username);
        } catch (HibernateException | NullPointerException e) {
            //e.printStackTrace();
            logger.info(e.getMessage());
            result.put("status", 500).put("description", "" + e);
        }
        return result;
    }

    public static JSONObject rejectTerminateProduct(String dbEnv, String uuid, int verProd, String respCode, String username, String remark) {
        JSONObject result = new JSONObject().put("status", 200).put("description", "");
        try {
            Date sysdate = new Date();
            ShelfProductDao dao = new ShelfProductDao();
            ShelfProduct sp = dao.getShelfProductByUUID(dbEnv, uuid);
            Integer rejectStatus = StatusUtils.getReject(dbEnv).getStatusCode();
            sp.getShelfProductVcsList().sort(Comparator.comparing(ShelfProductVcs::getVerProd));
            Integer lastStatus = null;
            for (ShelfProductVcs vcs : sp.getShelfProductVcsList()) {
                String states[] = vcs.getState().split("/");
                StatusUtils.Status st = null;
                if (states.length - 2 >= 0) {
                    st = StatusUtils.getStatusByCode(dbEnv, states[states.length - 2]);
                } else {
                    st = StatusUtils.getStatusByCode(dbEnv, states[states.length - 1]);
                }
                vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(rejectStatus)));
                vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.null2NoData(st.getStatusCode())));
                vcs.setStatus(st.getStatusCode());
                vcs.setUpdateAt(sysdate);
                vcs.setUpdateBy(username);
                if (!respCode.isEmpty()) {
                    vcs.setAttr2(StatusUtils.setStatus(vcs.getAttr2(), ValidUtils.null2NoData(respCode)));
                }
                if (!remark.isEmpty()) {
                    vcs.setAttr3(StatusUtils.setRemark(vcs.getAttr3(), ValidUtils.null2NoData(remark)));
                }
                if (vcs.getVerProd() == 0) {
                    lastStatus = st.getStatusCode();
                }
                if (null == lastStatus) {
                    lastStatus = st.getStatusCode();
                }
            }
            sp.setStatus(lastStatus);
            sp.setUpdateAt(sysdate);
            sp.setUpdateBy(username);
            sp.setAttr1((ValidUtils.null2NoData(sp.getAttr1()) + " " + ValidUtils.null2NoData(remark)).trim());
            dao.updateShelfProduct(dbEnv, sp, username);
        } catch (HibernateException | NullPointerException e) {
            //e.printStackTrace();
            logger.info(e.getMessage());
            result.put("status", 500).put("description", "" + e);
        }
        return result;
    }

    public static JSONObject compareEndDateNActiveDate(List<ShelfProductVcs> vcsActiveList, List<ShelfProductVcs> vcsInprogressList, String dbEnv) {
        JSONObject result = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject().put("flagCon", false));
        try {
            String inproActiveDate = "", inproEndDate = "", activeActiveDate = "", activeEndDate = "", prodCode = "", prodName = "";
            if (vcsActiveList.size() > 0) {
                List<ShelfProductDtl> list = null;
                for (ShelfProductVcs vcs : vcsActiveList) {
                    if (null == vcs.getCompUuid()) {
                        prodCode = vcs.getProdUuid().getProdCode();
                        prodName = vcs.getProdUuid().getProdName();
                        list = vcs.getShelfProductDtlList();
                        break;
                    }
                }
                if (null != list && list.size() > 0) {
                    for (ShelfProductDtl dtl : list) {
                        if ("activeDate".equalsIgnoreCase(dtl.getLkCode().trim())) {
                            activeActiveDate = dtl.getLkValue();
                        } else if ("endDate".equalsIgnoreCase(dtl.getLkCode())) {
                            activeEndDate = dtl.getLkValue();
                        }/* else if ("prodCode".equalsIgnoreCase(dtl.getLkCode())) {
                            prodCode = dtl.getLkValue();
                        } else if ("prodName".equalsIgnoreCase(dtl.getLkCode())) {
                            prodName = dtl.getLkValue();
                        }*/
                        if (!"".equals(activeActiveDate) && !"".equals(activeEndDate.trim()) && !"".equals(prodCode) && !"".equals(prodName)) {
                            break;
                        }
                    }
                }
            }
            if (vcsInprogressList.size() > 0) {
                List<ShelfProductDtl> list = null;
                for (ShelfProductVcs vcs : vcsInprogressList) {
                    if (null == vcs.getCompUuid()) {
                        list = vcs.getShelfProductDtlList();
                        break;
                    }
                }
                if (null != list && list.size() > 0) {
                    for (ShelfProductDtl dtl : list) {
                        if ("activeDate".equalsIgnoreCase(dtl.getLkCode().trim())) {
                            inproActiveDate = dtl.getLkValue();
                        } else if ("endDate".equalsIgnoreCase(dtl.getLkCode())) {
                            inproEndDate = dtl.getLkValue();
                        }
                        if (!"".equals(inproActiveDate) && !"".equals(inproEndDate.trim())) {
                            break;
                        }
                    }
                }
            }
            if (!"".equals(inproActiveDate) && !"".equals(activeEndDate)) {
                Date inAcDate = ValidUtils.str2Date(inproActiveDate, "yyyy-MM-dd");
                Date acEndDate = ValidUtils.str2Date(activeEndDate, "yyyy-MM-dd");
                if (DateUtils.addDate(acEndDate, 0, 0, 1, 0, 0, 0).compareTo(inAcDate) < 0) {
                    JSONObject resInAct = new JSONObject()
                            .put("flagCon", true)
                            .put("detail", new JSONObject().put("errCode", "ERR004")
                                    .put("prodCode", prodCode)
                                    .put("prodName", prodName)
                                    .put("curStartDate", activeActiveDate)
                                    .put("curEndDate", activeEndDate)
                                    .put("newStartDate", inproActiveDate)
                                    .put("newEndDate", inproEndDate)
                                    .put("description", StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0011")));
                    result.put("data", resInAct);
                }
            }
        } catch (JSONException | ParseException e) {
            //e.printStackTrace();
            logger.info(e.getMessage());
            result.put("status", 500).put("description", "" + e);
        }
        return result;
    }

    public static JSONObject getProductVcsFrontEnd(String dbEnv, ShelfProduct pd, String compUuid, Integer compVer) {
        JSONObject resp = new JSONObject();
        List<JSONObject> arr = new ArrayList<JSONObject>();
        try {
            Integer statusActive = StatusUtils.getActive(dbEnv).getStatusCode();
            String shelfTmpVcs = "";
            for (ShelfProductVcs vcs : pd.getShelfProductVcsList()) {
                if (compVer.equals(vcs.getVerComp())) {
                    if (null == vcs.getCompUuid()) {
                        JSONObject jdtl = new JSONObject();
                        for (ShelfProductDtl dtl : vcs.getShelfProductDtlList()) {
                            if (dtl.getStatus() == statusActive) {
                                jdtl.put(dtl.getLkCode(), dtl.getLkValue());
                            }
                        }
                        jdtl.put("uuid", vcs.getProdUuid().getUuid());
                        jdtl.put("prodId", vcs.getProdUuid().getUuid());
                        jdtl.put("prodVer", vcs.getVerProd());
                        jdtl.put("prodCode", vcs.getProdUuid().getProdCode());
                        jdtl.put("prodName", vcs.getProdUuid().getProdName());
                        jdtl.put("businessLine", ValidUtils.null2NoData(vcs.getProdUuid().getBusinessLine()));
                        jdtl.put("businessDept", ValidUtils.null2NoData(vcs.getProdUuid().getBusinessDept()));
                        jdtl.put("company", ValidUtils.null2NoData(vcs.getProdUuid().getCompany()));
                        resp.put("product", jdtl);
                        JSONObject jtemplate = new JSONObject();
                        ShelfTmpDao tmpDao = new ShelfTmpDao();
                        ShelfTmp tmp = tmpDao.getShelfTmp(dbEnv, vcs.getTemUuid());
                        if (null != tmp) {
                            jtemplate.put("uuid", tmp.getUuid());
                            jtemplate.put("name", tmp.getTmpName());
                            JSONArray jarray = new JSONArray();
                            for (ShelfTmpVcs tv : tmp.getShelfTmpVcsList()) {
                                if (tv.getStatus() == statusActive) {   //Confirm by Panadda 23/07/2020
                                    jarray = new JSONArray(tv.getAttr1());
                                }
                            }
                            List<JSONObject> listTmpComp = new ArrayList<>();
                            for (int i = 0; i < jarray.length(); i++) {
                                listTmpComp.add(jarray.getJSONObject(i));
                            }
                            Utils.sortJSONObjectByKey(listTmpComp, "seqNo", true);
                            jtemplate.put("component", jarray);
                        }
                        resp.put("template", jtemplate);
                        Gson gson = new Gson();
                        JSONObject json = new JSONObject(gson.toJson(null != vcs.getThemeUuid() ? vcs.getThemeUuid().getValue() : new JSONObject()));
                        resp.put("theme", json.has("info") ? json.getJSONObject("info") : json);
                    } else if (compUuid.equalsIgnoreCase(vcs.getCompUuid().getUuid())) {
                        ShelfTmpDao tmpDao = new ShelfTmpDao();
                        ShelfTmp shelfTmp = tmpDao.getShelfTmp(dbEnv, vcs.getTemUuid());
                        if (shelfTmpVcs.equalsIgnoreCase("")) {
                            for (ShelfTmpVcs tmpVcs : shelfTmp.getShelfTmpVcsList()) {
                                if (tmpVcs.getStatus() == statusActive) {   //Confirm by Panadda 23/07/2020
                                    shelfTmpVcs = tmpVcs.getUuid();
                                    break;
                                }
                            }
                        }

                        ShelfTmpDetailDao shelfTmpDtlDao = new ShelfTmpDetailDao();
                        ShelfComp shelfComp = shelfTmpDtlDao.getShelfComponentByCompUUIDAndTemplateVCS(dbEnv, vcs.getCompUuid().getUuid(), shelfTmpVcs);
                        if (null != vcs.getCompStatus() && vcs.getCompStatus().equals(statusActive)) {
                            JSONObject jdtl = new JSONObject();
                            jdtl.put("uuid", vcs.getUuid());
                            jdtl.put("compUuid", shelfComp.getUuid());
                            jdtl.put("seqNo", shelfComp.getSeqNo());
                            jdtl.put("compName", shelfComp.getCompName());
                            for (ShelfProductDtl dtl : vcs.getShelfProductDtlList()) {
                                if (dtl.getStatus() == statusActive) {
                                    if (null != dtl.getAttr1() && !"".equals(dtl.getAttr1())) {
                                        JSONObject obj = new JSONObject();
                                        obj.put("value", dtl.getLkValue());
                                        obj.put("attr1", dtl.getAttr1());
                                        jdtl.put(dtl.getLkCode(), obj.toString());
                                    } else {
                                        jdtl.put(dtl.getLkCode(), dtl.getLkValue());
                                    }
                                }
                            }
                            arr.add(jdtl);
                        }
                    }
                }
            }
            resp.put("component", arr);
        } catch (NullPointerException | HibernateException e) {
            throw e;
        }
        return resp;
    }

    public static JSONObject setActiveExpireProduct(String dbEnv) throws SQLException, ParseException {
        JSONObject result = new JSONObject().put("status", 200).put("description", "");
        try {
            List<JSONObject> list = new ArrayList<>();
            Date sysdate = ValidUtils.str2Date(DateUtils.getDisplayEnDate(new Date(), "yyyy-MM-dd"), "yyyy-MM-dd");
            Integer statusInactive = StatusUtils.getInActive(dbEnv).getStatusCode();
            Integer statusActive = StatusUtils.getActive(dbEnv).getStatusCode();
            Integer statusExpire = StatusUtils.getExpired(dbEnv).getStatusCode();
            Integer statusPause = StatusUtils.getPause(dbEnv).getStatusCode();
            Integer statusNotUse = StatusUtils.getNotUse(dbEnv).getStatusCode();
            ShelfProductDao dao = new ShelfProductDao();
            HashMap mapActive = dao.getListProductByStatus(dbEnv, statusActive);
            HashMap mapInactive = dao.getListProductByStatus(dbEnv, statusInactive);
            HashMap mapPause = dao.getListProductByStatus(dbEnv, statusPause);
            Set<String> inActiveKName = mapInactive.keySet();
            for (String k2 : inActiveKName) {
                JSONObject data = (JSONObject) mapInactive.get(k2);
                if (data.has("activeDate")) {
                    Date activeDate = ValidUtils.str2Date(ValidUtils.null2NoData(data.get("activeDate")), "yyyy-MM-dd");
                    if (sysdate.compareTo(activeDate) >= 0) {
                        /*
                        set active to expire
                         */
                        if (null != mapActive.get(k2)) {
                            JSONObject dataActive = (JSONObject) mapActive.get(k2);
                            dataActive.put("status", statusExpire);
                            dataActive.put("state", StatusUtils.setStatus(dataActive.getString("state"), ValidUtils.obj2String(statusExpire)));
                            dataActive.put("expireDate", activeDate);
                            list.add(dataActive);
                            mapActive.remove(k2);
                        }
                        /*
                        set pause to notuse
                         */
                        if (null != mapPause.get(k2)) {
                            JSONObject dataPause = (JSONObject) mapPause.get(k2);
                            dataPause.put("status", statusNotUse);
                            dataPause.put("state", StatusUtils.setStatus(dataPause.getString("state"), ValidUtils.obj2String(statusNotUse)));
                            dataPause.put("expireDate", activeDate);
                            list.add(dataPause);
                            mapPause.remove(k2);
                        }
                        /*
                        set inactive to active
                         */
                        data.put("status", statusActive);
                        data.put("state", StatusUtils.setStatus(data.getString("state"), ValidUtils.obj2String(statusActive)));
                        list.add(data);
                    }
                }
            }
            Set<String> activeKName = mapActive.keySet();
            for (String k2 : activeKName) {
                JSONObject data = (JSONObject) mapActive.get(k2);
                if (data.has("endDate")) {
                    Date endDate = ValidUtils.str2Date(ValidUtils.null2NoData(data.get("endDate")), "yyyy-MM-dd");
                    if (sysdate.compareTo(endDate) > 0) {
                        /*
                        set active to expire
                         */
                        data.put("status", statusExpire);
                        data.put("state", StatusUtils.setStatus(data.getString("state"), ValidUtils.obj2String(statusExpire)));
                        data.put("expireDate", endDate);
                        list.add(data);
                    }

                }
            }
            ShelfProductVcsDao vcsDao = new ShelfProductVcsDao();
            vcsDao.updateShelfProductVcs(dbEnv, list);
        } catch (HibernateException | NullPointerException e) {
            //e.printStackTrace();
            logger.info(e.getMessage());
            result.put("status", 500).put("description", "" + e);
            throw e;
        }
        return result;
    }

    public static JSONObject validProduct(JSONObject datas, String dbEnv) {
        JSONObject ret = new JSONObject().put("status", true).put("description", "");
        boolean isReq = true;
        try {
            JSONObject objData = datas.getJSONObject("data");
            JSONObject jsonProd = objData.getJSONObject("product");
            List<String> colName = new ArrayList<>();
            if (!jsonProd.has("prodCode") || (jsonProd.has("prodCode") && jsonProd.getString("prodCode").isEmpty())) {
                colName.add("Product Code");
                isReq = false;
            }
            if (!jsonProd.has("prodName") || (jsonProd.has("prodName") && jsonProd.getString("prodName").isEmpty())) {
                colName.add("Product Name");
                isReq = false;
            }
            if (!jsonProd.has("theme") || (jsonProd.has("theme") && jsonProd.getString("theme").isEmpty())) {
                colName.add("Theme");
                isReq = false;
            }
            if (!jsonProd.has("template") || (jsonProd.has("template") && jsonProd.getString("template").isEmpty())) {
                colName.add("Template");
                isReq = false;
            }
            if (!jsonProd.has("tmpVer") || (jsonProd.has("tmpVer") && ValidUtils.null2NoData(jsonProd.get("tmpVer")).isEmpty())) {
                colName.add("Template Version");
                isReq = false;
            }
            if (!jsonProd.has("prodType") || (jsonProd.has("prodType") && jsonProd.getString("prodType").isEmpty())) {
                colName.add("Product Type");
                isReq = false;
            }
            if (!jsonProd.has("groupProduct") || (jsonProd.has("groupProduct") && jsonProd.getString("groupProduct").isEmpty())) {
                colName.add("Group Product");
                isReq = false;
            }
            if (!jsonProd.has("operDepartment") || (jsonProd.has("operDepartment") && jsonProd.getString("operDepartment").isEmpty())) {
                colName.add("Operation Department");
                isReq = false;
            }
            if (!jsonProd.has("prodDepartment") || (jsonProd.has("prodDepartment") && jsonProd.getString("prodDepartment").isEmpty())) {
                colName.add("Product Department");
                isReq = false;
            }
            if (!jsonProd.has("activeDate") || (jsonProd.has("activeDate") && jsonProd.getString("activeDate").isEmpty())) {
                colName.add("Active Date");
                isReq = false;
            }
            if (!jsonProd.has("endDate") || (jsonProd.has("endDate") && jsonProd.getString("endDate").isEmpty())) {
                colName.add("End Date");
                isReq = false;
            }
            Date effectiveDateTemplate = null;
            List<String> errMsg = new ArrayList<>();
            if (jsonProd.has("template") && !jsonProd.getString("template").isEmpty()
                    && jsonProd.has("tmpVer") && !ValidUtils.null2NoData(jsonProd.get("tmpVer")).isEmpty()) {
                String template = jsonProd.getString("template");
                int tmpVer = ValidUtils.obj2Int(jsonProd.get("tmpVer"));
                ShelfTmpVcsDao tmpDao = new ShelfTmpVcsDao();
                List<ShelfTmpVcs> list = tmpDao.getListByTmpUuidAndTmpVersion(dbEnv, template, tmpVer, false);
                if (list.isEmpty()) {
                    errMsg.add(StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0003"));
                    isReq = false;
                } else {
                    if (tmpVer == 1) {
                        effectiveDateTemplate = list.get(0).getEffectiveDate();
                    }
                }
            }
            if (jsonProd.has("theme") && !jsonProd.getString("theme").isEmpty()) {
                ShelfThemeDao dao = new ShelfThemeDao();
                ShelfTheme theme = dao.getShelfTmpTheme(dbEnv, jsonProd.getString("theme"));
                if (null == theme) {
                    errMsg.add(StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0004"));
                    isReq = false;
                }
            }
            if (jsonProd.has("activeDate") && !jsonProd.getString("activeDate").isEmpty()) {
                Date curDate = ValidUtils.str2Date(DateUtils.getDisplayEnDate(new Date(), "yyyy-MM-dd"), "yyyy-MM-dd");
                Date activeDate = ValidUtils.str2Date(jsonProd.getString("activeDate"), "yyyy-MM-dd");
                if (activeDate.compareTo(curDate) < 0) {
                    errMsg.add(StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0005"));
                    isReq = false;
                }
            }
            if (jsonProd.has("activeDate") && !jsonProd.getString("activeDate").isEmpty() && jsonProd.has("endDate") && !jsonProd.getString("endDate").isEmpty()) {
                Date activeDate = ValidUtils.str2Date(jsonProd.getString("activeDate"), "yyyy-MM-dd");
                Date endDate = ValidUtils.str2Date(jsonProd.getString("endDate"), "yyyy-MM-dd");
                if (endDate.compareTo(activeDate) < 0) {
                    errMsg.add(StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0006"));
                    isReq = false;
                }
                if (null != effectiveDateTemplate) {
                    Date effectDateTmp = ValidUtils.str2Date(DateUtils.getDisplayEnDate(effectiveDateTemplate, "yyyy-MM-dd"), "yyyy-MM-dd");
                    if (effectDateTmp.compareTo(activeDate) > 0) {
                        errMsg.add(StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0007"));
                        isReq = false;
                    }
                }
            }
            if (jsonProd.has("uuid") && !jsonProd.getString("uuid").isEmpty()) {
                String uuid = jsonProd.getString("uuid");
                Integer statusActive = StatusUtils.getActive(dbEnv).getStatusCode();
                Integer[] active = {statusActive};
                ShelfProductVcsDao vcsDao = new ShelfProductVcsDao();
                List<ShelfProductVcs> vcsActiveList = vcsDao.getListShelfProductVcsListByStatus(dbEnv, uuid, active);
                for (ShelfProductVcs vcs : vcsActiveList) {
                    if (vcs.getCompUuid() == null) {
                        for (ShelfProductDtl dtl : vcs.getShelfProductDtlList()) {
                            if ("activeDate".equalsIgnoreCase(dtl.getLkCode()) && !dtl.getLkValue().isEmpty()) {
                                Date activeDateCurrVer = ValidUtils.str2Date(dtl.getLkValue(), "yyyy-MM-dd");
                                Date activeDateNewVer = ValidUtils.str2Date(jsonProd.getString("activeDate"), "yyyy-MM-dd");
                                if (activeDateCurrVer.compareTo(activeDateNewVer) == 0) {
                                    errMsg.add(StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0008"));
                                    isReq = false;
                                }
                                Date currDate = ValidUtils.str2Date(DateUtils.getDisplayEnDate(new Date(), "yyyy-MM-dd"), "yyyy-MM-dd");
                                if (currDate.compareTo(activeDateNewVer) == 0) {
                                    errMsg.add(StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0009"));
                                    isReq = false;
                                }
                                break;
                            }
                        }
                        break;
                    }
                }
            }
            if (!isReq) {
                if (!colName.isEmpty()) {
                    String desc = (StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0002") + " " + colName.toString());
                    ret.put("description", desc);
                }
                if (!errMsg.isEmpty()) {
                    ret.put("description", ret.getString("description") + errMsg.toString());
                }
            }
            ret.put("status", isReq);
        } catch (JSONException | ParseException | NullPointerException | HibernateException e) {
            ret.put("status", false).put("description", "" + e);
        }
        return ret;
    }

    public static JSONObject confirmProduct(JSONObject datas, String dbEnv) {
        JSONObject ret = new JSONObject().put("status", true).put("description", "");
        boolean isReq = true;
        try {
            JSONObject objData = datas.getJSONObject("data");
            JSONObject jsonProd = objData.getJSONObject("product");
            List<String> errMsg = new ArrayList<>();
            if (jsonProd.has("uuid") && !jsonProd.getString("uuid").isEmpty()) {
                String uuid = jsonProd.getString("uuid");
                Integer statusInActive = StatusUtils.getInActive(dbEnv).getStatusCode();
                Integer[] inActive = {statusInActive};
                ShelfProductVcsDao vcsDao = new ShelfProductVcsDao();
                List<ShelfProductVcs> vcsInActiveList = vcsDao.getListShelfProductVcsListByStatus(dbEnv, uuid, inActive);
                if (vcsInActiveList.size() > 0) {
                    errMsg.add(StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0010"));
                    isReq = false;
                }
                Integer statusActive = StatusUtils.getActive(dbEnv).getStatusCode();
                Integer[] active = {statusActive};
                List<ShelfProductVcs> vcsActiveList = vcsDao.getListShelfProductVcsListByStatus(dbEnv, uuid, active);
                for (ShelfProductVcs vcs : vcsActiveList) {
                    if (vcs.getCompUuid() == null) {
                        String activeDate = "", endDate = "";
                        for (ShelfProductDtl dtl : vcs.getShelfProductDtlList()) {
                            if ("activeDate".equalsIgnoreCase(dtl.getLkCode()) && !dtl.getLkValue().isEmpty()) {
//                                activeDate = ValidUtils.str2Date(dtl.getLkValue(), "yyyy-MM-dd");
                                activeDate = dtl.getLkValue();
                            }
                            if ("endDate".equalsIgnoreCase(dtl.getLkCode()) && !dtl.getLkValue().isEmpty()) {
                                endDate = dtl.getLkValue();
                            }
                            if (!activeDate.isEmpty() && !endDate.isEmpty()) {
                                Date activeDateNewVer = ValidUtils.str2Date(jsonProd.getString("activeDate"), "yyyy-MM-dd");
                                Date endDateNewVer = ValidUtils.str2Date(jsonProd.getString("endDate"), "yyyy-MM-dd");
                                if (null != activeDateNewVer) {
                                    if (DateUtils.addDate(ValidUtils.str2Date(endDate, "yyyy-MM-dd"), 0, 0, 1, 0, 0, 0).compareTo(activeDateNewVer) < 0) {
                                        errMsg.add(StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0011"));
                                        isReq = false;
                                    }
                                }
                                if (null != endDateNewVer) {
                                    if (endDateNewVer.compareTo(ValidUtils.str2Date(endDate, "yyyy-MM-dd")) < 0) {
                                        errMsg.add(StatusUtils.getErrorMessageByCode(dbEnv, "SHELF0012"));
                                        isReq = false;
                                    }
                                }
                                break;
                            }
                        }
                        break;
                    }
                }

            }
            if (!isReq && !errMsg.isEmpty()) {
                ret.put("description", errMsg.toString());
            }
            ret.put("status", isReq);
        } catch (JSONException | ParseException e) {
            ret.put("status", false).put("description", "" + e);
        }
        return ret;
    }

    public static JSONObject getProductDataByProductAllVersion(String dbEnv, ShelfProductVcs shelfProductVcs) {
        JSONObject retData = new JSONObject();
        try {
            Integer statusInactive = StatusUtils.getInActive(dbEnv).getStatusCode();
            Integer statusTerminate = StatusUtils.getTerminate(dbEnv).getStatusCode();
            Integer statusExpire = StatusUtils.getExpired(dbEnv).getStatusCode();
            JSONObject prod = getInitialProduct();
            prod.put("uuid", shelfProductVcs.getProdUuid().getUuid());
            for (ShelfProductDtl prodDtl : shelfProductVcs.getShelfProductDtlList()) {
                prod.put(prodDtl.getLkCode(), ValidUtils.null2NoData(prodDtl.getLkValue()));
            }
            ShelfTmp shelfTmp = new ShelfTmpDao().getShelfTmp(dbEnv, shelfProductVcs.getTemUuid());
            StatusUtils.Status status = StatusUtils.getStatusByCode(dbEnv, String.valueOf(shelfProductVcs.getStatus()));
            prod.put("status", shelfProductVcs.getStatus());
            prod.put("statusNameTh", status.getStatusNameTh() == null ? "" : status.getStatusNameTh());
            prod.put("statusNameEn", status.getStatusNameEn() == null ? "" : status.getStatusNameEn());
            prod.put("template", shelfTmp.getUuid() == null ? "" : shelfTmp.getUuid());
            prod.put("templateName", shelfTmp.getTmpName() == null ? "" : shelfTmp.getTmpName());
            prod.put("businessLine", ValidUtils.null2NoData(shelfProductVcs.getProdUuid().getBusinessLine()));
            prod.put("businessDept", ValidUtils.null2NoData(shelfProductVcs.getProdUuid().getBusinessDept()));
            prod.put("company", ValidUtils.null2NoData(shelfProductVcs.getProdUuid().getCompany()));
            prod.put("verProd", (shelfProductVcs.getVerProd() == 0 ? "" : ValidUtils.obj2Int(shelfProductVcs.getVerProd())));
            prod.put("prodCode", ValidUtils.null2NoData(shelfProductVcs.getProdUuid().getProdCode()));
            prod.put("prodName", ValidUtils.null2NoData(shelfProductVcs.getProdUuid().getProdName()));
            prod.put("createDate", DateUtils.getDisplayEnDate(shelfProductVcs.getCreateAt(), "yyyy-MM-dd HH:mm:ss"));
            prod.put("updateDate", ValidUtils.null2Separator(DateUtils.getDisplayEnDate(shelfProductVcs.getUpdateAt(), "yyyy-MM-dd HH:mm:ss"), DateUtils.getDisplayEnDate(shelfProductVcs.getCreateAt(), "yyyy-MM-dd HH:mm:ss")));
            prod.put("updateBy", ValidUtils.null2Separator(shelfProductVcs.getUpdateBy(), shelfProductVcs.getCreateBy()));
            prod.put("CreateBy", ValidUtils.null2NoData(shelfProductVcs.getCreateBy()));
            prod.put("tmpVer", ValidUtils.obj2Int(shelfProductVcs.getVerTem()));
            boolean isDel = false;
            if (statusInactive.equals(shelfProductVcs.getStatus()) || statusTerminate.equals(shelfProductVcs.getStatus()) || statusExpire.equals(shelfProductVcs.getStatus())) {
                isDel = true;
            }
            prod.put("edit", true);
            prod.put("delete", isDel);
            retData.put("product", prod);
        } catch (NullPointerException | HibernateException e) {
            logger.info(e.getMessage());
        }
        return retData;
    }
    
    public static JSONArray infoProductsByUuidStatus(String dbEnv, String prodUuid, int status) throws ParseException {
        JSONArray productList = new JSONArray();

        ShelfProductVcsDao shelfProductVcsDao = new ShelfProductVcsDao();
        try {
            Integer arStatus[] = {status};
            List<ShelfProductVcs> shelfProductVcs = shelfProductVcsDao.getListProductVcsListByStatus(dbEnv, prodUuid,  arStatus);
            if (shelfProductVcs.size() > 0) {
                List<ShelfProductVcs>[] data = ProductUtils.orderProductByVersion(shelfProductVcs);
                for (int i = 0; i < data.length; i++) {
                    List<ShelfProductVcs> vcsList = data[i];
                    List<ShelfProductDtl> dtlList = new ArrayList<>();
                    JSONObject prod = ProductUtils.getInitialProduct();
                    ShelfProductVcs compVcs = new ShelfProductVcs();
                    for (ShelfProductVcs vcs : vcsList) {
                        if (vcs.getCompUuid() == null) {
                            dtlList = vcs.getShelfProductDtlList();
                            compVcs = vcs;
                            break;
                        }
                    }
                    prod.put("uuid", compVcs.getProdUuid().getUuid());
                    for (ShelfProductDtl dtl : dtlList) {
                        prod.put(dtl.getLkCode(), ValidUtils.null2NoData(dtl.getLkValue()));
                    }
                    ShelfTmp shelfTmp = new ShelfTmpDao().getShelfTmp(dbEnv, compVcs.getTemUuid());
                    StatusUtils.Status s = StatusUtils.getStatusByCode(dbEnv, String.valueOf(compVcs.getStatus()));
                    prod.put("status", s.getStatusCode() == null ? "" : s.getStatusCode());
                    prod.put("statusName", s.getStatusNameTh() == null ? "" : s.getStatusNameTh());
                    prod.put("statusNameEn", s.getStatusNameEn() == null ? "" : s.getStatusNameEn());
                    prod.put("template", shelfTmp.getUuid() == null ? "" : shelfTmp.getUuid());
                    prod.put("theme", compVcs.getThemeUuid().getUuid());
                    prod.put("templateName", shelfTmp.getTmpName() == null ? "" : shelfTmp.getTmpName());
                    prod.put("businessLine", ValidUtils.null2NoData(compVcs.getProdUuid().getBusinessLine()));
                    prod.put("businessDept", ValidUtils.null2NoData(compVcs.getProdUuid().getBusinessDept()));
                    prod.put("prodCode", ValidUtils.null2NoData(compVcs.getProdUuid().getProdCode()));
                    prod.put("prodName", ValidUtils.null2NoData(compVcs.getProdUuid().getProdName()));
                    prod.put("company", ValidUtils.null2NoData(compVcs.getProdUuid().getCompany()));
                    prod.put("verProd", ValidUtils.obj2Int(compVcs.getVerProd()));
                    prod.put("tmpVer", ValidUtils.obj2Int(compVcs.getVerTem()));
                    JSONObject retProduct = ProductUtils.getProductDataByProduct(dbEnv, vcsList);
                    retProduct.put("product", prod);
                    productList.put(retProduct);
                }
            }
        } catch (HibernateException | NullPointerException e) {
            throw e;
        }
        return productList;
    }
}
