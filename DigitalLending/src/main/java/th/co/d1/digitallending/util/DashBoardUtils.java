/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.util;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import org.hibernate.HibernateException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import th.co.d1.digitallending.dao.DashboardDao;
import th.co.d1.digitallending.dao.ShelfLookupDao;
import th.co.d1.digitallending.entity.ShelfLookup;

/**
 *
 * @author Kritsana Sasai
 *
 * DataOne Asia (Thailand) Company Limited 1023 MS Siam Tower, 30th Floor, Rama
 * 3 Rd., Chongnonsi, Yannawa, Bangkok, 10120
 *
 * @create 10-03-2020 1:56:50 PM
 */
public class DashBoardUtils {

    final static Logger logger = Logger.getLogger(DashBoardUtils.class.getName());

    private static JSONObject getDashboardProduct(Boolean isDtl, HashMap hmap, String productCode) {
        JSONObject resp = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
        try {
            JSONArray arr = new JSONArray();
            HashMap hmap2 = new HashMap();
            for (Object kName : hmap.keySet()) {
                JSONObject data = (JSONObject) hmap.get(kName);
                String prodcode = data.getString("prodcode");
                JSONObject tmp2 = new JSONObject();
                if (null != hmap2.get(prodcode)) {
                    JSONObject tmp3 = (JSONObject) hmap2.get(prodcode);
                    int custno = ValidUtils.obj2Int(data.get("custno")) + (ValidUtils.obj2Int(tmp3.get("custno")));
                    int trnno = ValidUtils.obj2Int(data.get("trnno")) + (ValidUtils.obj2Int(tmp3.get("trnno")));
                    BigDecimal amount = ValidUtils.obj2BigDec(ValidUtils.null2Separator(data.has("amount") ? data.get("amount") : "0", "0"))
                            .add(ValidUtils.obj2BigDec(ValidUtils.null2Separator(tmp3.has("amount") ? tmp3.get("amount") : "0", "0")));
                    tmp2 = tmp3;
                    tmp2.put("prodcode", prodcode)
                            .put("custno", custno)
                            .put("trnno", trnno)
                            .put("amount", amount);
                } else {
                    tmp2.put("prodcode", prodcode)
                            .put("prodname", data.get("prodname"))
                            .put("custno", data.get("custno"))
                            .put("trnno", data.get("trnno"))
                            .put("amount", ValidUtils.null2Separator(data.get("amount"), "0"));
                }
                hmap2.put(prodcode, tmp2);
                arr.put(data);
            }
            JSONArray detail = new JSONArray();
            int allcustno = 0;
            BigDecimal allamount = BigDecimal.ZERO;
            for (Object kName : hmap2.keySet()) {
                JSONObject tmp = (JSONObject) hmap2.get(kName);
                allcustno = allcustno + ValidUtils.obj2Int(tmp.get("custno"));
                allamount = allamount.add(ValidUtils.obj2BigDec(tmp.get("amount")));
                tmp.put("amount", ValidUtils.obj2BigDec(tmp.get("amount")));            //Reference Panadda 07/04/2020
//                tmp.put("amount", (ValidUtils.obj2Int(tmp.get("custno")) > 0) ? ValidUtils.obj2BigDec(tmp.get("amount")).divide(ValidUtils.obj2BigDec(tmp.get("custno")), 2, RoundingMode.HALF_UP) : 0);
                detail.put(tmp);
            }
            if (productCode.isEmpty()) {
                /*if (allcustno > 0) {          //Reference Panadda 07/04/2020
                    allamount = allamount.divide(new BigDecimal(allcustno), 2, RoundingMode.HALF_UP);
                    allamount = allamount.divide(new BigDecimal(allcustno), 2, RoundingMode.HALF_UP);
                }*/
                JSONObject all = new JSONObject().put("prodcode", "all")
                        .put("prodname", "All")
                        .put("custno", allcustno)
                        .put("trnno", hmap.size())
                        .put("amount", allamount);
                detail.put(all);
            }
            JSONObject data = new JSONObject().put("type", "product")
                    .put("detail", detail);
            if (isDtl) {
                HashMap mapDtl = new HashMap();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    String k = obj.getString("prodcode") + "_" + obj.getString("channel") + "_" + DateUtils.getDisplayEnDate((Date) obj.get("trnDate"), "yyyyMMdd");
                    JSONObject tmp = new JSONObject();
                    if (null != mapDtl.get(k)) {
                        tmp = (JSONObject) mapDtl.get(k);
                        int custno = ValidUtils.obj2Int(tmp.get("custno")) + ValidUtils.obj2Int(obj.get("custno"));
                        int trnno = ValidUtils.obj2Int(tmp.get("trnno")) + ValidUtils.obj2Int(obj.get("trnno"));
                        BigDecimal amount = ValidUtils.obj2BigDec(tmp.get("amount")).add(ValidUtils.obj2BigDec(obj.get("amount")));
                        tmp.put("custno", custno)
                                .put("trnno", trnno)
                                .put("amount", amount);

                    } else {
                        tmp.put("prodcode", ValidUtils.null2NoData(obj.get("prodcode")))
                                .put("prodname", ValidUtils.null2NoData(obj.get("prodname")))
                                .put("companyname", ValidUtils.null2NoData(obj.get("companyname")))
                                .put("department", ValidUtils.null2NoData(obj.get("department")))
                                .put("businessline", ValidUtils.null2NoData(obj.get("businessline")))
                                .put("channel", ValidUtils.null2NoData(obj.get("channel")))
                                .put("trnDate", DateUtils.getDisplayEnDate((Date) obj.get("trnDate"), "dd/MM/yyyy"))
                                .put("custno", ValidUtils.obj2Int(obj.get("custno")))
                                .put("trnno", ValidUtils.obj2Int(obj.get("trnno")))
                                .put("amount", ValidUtils.obj2BigDec(obj.get("amount")))
                                .put("percent", BigDecimal.ZERO);
                    }
                    mapDtl.put(k, tmp);
                }
                List<JSONObject> listDtl = new ArrayList<>();
                for (Object kName : mapDtl.keySet()) {
                    JSONObject tmp = (JSONObject) mapDtl.get(kName);
                    int custno = ValidUtils.obj2Int(tmp.get("custno"));
                    int trnno = ValidUtils.obj2Int(tmp.get("trnno"));
                    tmp.put("percent", trnno > 0 ? (double) (((double) custno / (double) trnno) * 100) : 0);      //Reference Panadda 07/04/2020
//                    tmp.put("percent", custno > 0 ? (double) ((trnno / custno) * 100) : 0);
                    listDtl.add(tmp);
                }
                data.put("list", listDtl);
            }
            resp.put("data", data);
        } catch (JSONException | NullPointerException | HibernateException e) {
            //e.printStackTrace();
            resp.put("status", 500)
                    .put("description", "" + e);
        }
        return resp;
    }

    private static JSONObject getDashboardChannel(Boolean isDtl, HashMap hmap, String productCode) {
        JSONObject resp = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
        try {
            JSONArray arr = new JSONArray();
            HashMap hmap2 = new HashMap();
            for (Object kName : hmap.keySet()) {
                JSONObject data = (JSONObject) hmap.get(kName);
                String channel = data.getString("channel");
                JSONObject tmp2 = new JSONObject();
                if (null != hmap2.get(channel)) {
                    JSONObject tmp3 = (JSONObject) hmap2.get(channel);
                    int custno = ValidUtils.obj2Int(data.get("custno")) + (ValidUtils.obj2Int(tmp3.get("custno")));
                    int trnno = ValidUtils.obj2Int(data.get("trnno")) + (ValidUtils.obj2Int(tmp3.get("trnno")));
                    BigDecimal amount = ValidUtils.obj2BigDec(data.get("amount")).add(ValidUtils.obj2BigDec(tmp3.get("amount")));
                    tmp2.put("channel", channel)
                            .put("custno", custno)
                            .put("trnno", trnno)
                            .put("amount", amount);
                } else {
                    tmp2.put("channel", channel)
                            .put("custno", data.get("custno"))
                            .put("trnno", data.get("trnno"))
                            .put("amount", data.get("amount"));
                }
                hmap2.put(channel, tmp2);
                arr.put(data);
            }
            JSONArray detail = new JSONArray();
            BigInteger allcustno = BigInteger.ZERO;
            BigDecimal allamount = BigDecimal.ZERO;
            for (Object kName : hmap2.keySet()) {
                JSONObject tmp = (JSONObject) hmap2.get(kName);
                allcustno = allcustno.add(ValidUtils.obj2BigInt(tmp.get("custno")));
                allamount = allamount.add(ValidUtils.obj2BigDec(tmp.get("amount")));
                tmp.put("amount", ValidUtils.obj2BigDec(tmp.get("amount")));        //Reference Panadda 07/04/2020
//                tmp.put("amount", (ValidUtils.obj2Int(tmp.get("custno")) > 0) ? ValidUtils.obj2BigDec(tmp.get("amount")).divide(ValidUtils.obj2BigDec(tmp.get("custno")), 2, RoundingMode.HALF_UP) : 0);
                detail.put(tmp);
            }
            if (productCode.isEmpty()) {
                /*          //Reference Panadda 07/04/2020
                if ((allamount.compareTo(BigDecimal.ZERO) == 1 && allcustno.compareTo(BigInteger.ZERO) == 1)) {
                    allamount = allamount.divide(new BigDecimal(allcustno), 2, RoundingMode.HALF_UP);
                }*/
                JSONObject all = new JSONObject().put("channel", "all")
                        .put("channelname", "All")
                        .put("custno", allcustno)
                        .put("trnno", hmap.size())
                        .put("amount", allamount);
                detail.put(all);
            }
            JSONObject data = new JSONObject().put("type", "channel")
                    .put("detail", detail);
            if (isDtl) {
                HashMap mapDtl = new HashMap();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    String k = obj.getString("prodcode") + "_" + obj.getString("channel") + "_" + DateUtils.getDisplayEnDate((Date) obj.get("trnDate"), "yyyyMMdd");
                    JSONObject tmp = new JSONObject();
                    if (null != mapDtl.get(k)) {
                        tmp = (JSONObject) mapDtl.get(k);
                        int custno = ValidUtils.obj2Int(tmp.get("custno")) + ValidUtils.obj2Int(obj.get("custno"));
                        int trnno = ValidUtils.obj2Int(tmp.get("trnno")) + ValidUtils.obj2Int(obj.get("trnno"));
                        BigDecimal amount = ValidUtils.obj2BigDec(tmp.get("amount")).add(ValidUtils.obj2BigDec(obj.get("amount")));
                        tmp.put("custno", custno)
                                .put("trnno", trnno)
                                .put("amount", amount);

                    } else {    //Product name,company name, วันที่ทำรายการ, channel, จำนวนผู้เข้าใช้งาน, จำนวนtxn, จำนวนเงิน, %จำนวนคนต่อ txn
                        tmp.put("prodcode", ValidUtils.null2NoData(obj.get("prodcode")))
                                .put("prodname", ValidUtils.null2NoData(obj.get("prodname")))
                                .put("companyname", ValidUtils.null2NoData(obj.get("companyname")))
                                .put("department", ValidUtils.null2NoData(obj.get("department")))
                                .put("businessline", ValidUtils.null2NoData(obj.get("businessline")))
                                .put("channel", ValidUtils.null2NoData(obj.get("channel")))
                                .put("trnDate", DateUtils.getDisplayEnDate((Date) obj.get("trnDate"), "dd/MM/yyyy"))
                                .put("custno", ValidUtils.obj2Int(obj.get("custno")))
                                .put("trnno", ValidUtils.obj2Int(obj.get("trnno")))
                                .put("amount", ValidUtils.obj2BigDec(obj.get("amount")))
                                .put("percent", BigDecimal.ZERO);
                    }
                    mapDtl.put(k, tmp);
                }
                List<JSONObject> listDtl = new ArrayList<>();
                for (Object kName : mapDtl.keySet()) {
                    JSONObject tmp = (JSONObject) mapDtl.get(kName);
                    int custno = ValidUtils.obj2Int(tmp.get("custno"));
                    int trnno = ValidUtils.obj2Int(tmp.get("trnno"));
                    tmp.put("percent", trnno > 0 ? (double) (((double) custno / (double) trnno) * 100) : 0);
//                    tmp.put("percent", custno > 0 ? (double) ((trnno / custno) * 100) : 0);     //Reference Panadda 07/04/2020
                    listDtl.add(tmp);
                }
                data.put("list", listDtl);
            }
            resp.put("data", data);
        } catch (JSONException | HibernateException | NullPointerException e) {
            resp.put("status", 500)
                    .put("description", "" + e);
            //e.printStackTrace();
        }
        return resp;
    }

    private static JSONObject getDashboardState(String dbEnv, Boolean isDtl, HashMap hmap) {
        JSONObject resp = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
        try {
            HashMap hmap2 = new HashMap();
            List<ShelfLookup> listState = new ShelfLookupDao().getActiveShelfLookupByGroupAndType(dbEnv, "PROCESS_STATE", "PROCESS_STATE");
//            listState.sort(Comparator.comparing(ShelfLookup::getLookupCode));
            listState.forEach((state) -> {
                JSONObject tmp2 = new JSONObject().put("statuscode", state.getLookupCode())
                        .put("custno", "0")
                        .put("trnno", "0")
                        .put("statusname", ValidUtils.null2NoData(state.getLookupNameTh()));
                hmap2.put(state.getLookupCode(), tmp2);
            });
            JSONArray arr = new JSONArray();
            for (Object kName : hmap.keySet()) {
                JSONObject tmp = (JSONObject) hmap.get(kName);
                String statecode = tmp.getString("statecode");
                JSONObject tmp2 = new JSONObject();
                if (null != hmap2.get(statecode)) {
                    JSONObject tmp3 = (JSONObject) hmap2.get(statecode);
                    if (!tmp3.has("time")) {
                        tmp3.put("time", ValidUtils.obj2Int(tmp.get("time")));
                    }
                    if (!tmp3.has("minTime")) {
                        tmp3.put("minTime", ValidUtils.obj2Int(tmp.get("time")));
                    }
                    if (!tmp3.has("maxTime")) {
                        tmp3.put("maxTime", ValidUtils.obj2Int(tmp.get("time")));
                    }
                    int custno = ValidUtils.obj2Int(tmp.get("custno")) + (ValidUtils.obj2Int(tmp3.get("custno")));
                    int trnno = ValidUtils.obj2Int(tmp.get("trnno")) + (ValidUtils.obj2Int(tmp3.get("trnno")));
                    int time = ValidUtils.obj2Int(tmp.get("time")) + (ValidUtils.obj2Int(tmp3.get("time")));
                    tmp2 = tmp3;
                    int minTime = tmp3.getInt("minTime") > ValidUtils.obj2Int(tmp.get("time")) ? ValidUtils.obj2Int(tmp.get("time")) : tmp3.getInt("minTime");
                    int maxTime = tmp3.getInt("maxTime") < ValidUtils.obj2Int(tmp.get("time")) ? ValidUtils.obj2Int(tmp.get("time")) : tmp3.getInt("maxTime");
                    tmp2.put("custno", custno)
                            .put("trnno", trnno)
                            .put("minTime", minTime)
                            .put("maxTime", maxTime)
                            .put("time", time);
                } else {
                    tmp2.put("statuscode", tmp.getString("statecode"))
                            .put("time", ValidUtils.obj2BigInt(tmp.get("time")))
                            .put("minTime", ValidUtils.obj2BigInt(tmp.get("time")))
                            .put("maxTime", ValidUtils.obj2BigInt(tmp.get("time")))
                            .put("custno", ValidUtils.obj2BigInt(tmp.get("custno")))
                            .put("trnno", ValidUtils.obj2BigInt(tmp.get("trnno")))
                            .put("statusname", tmp.getString("statenameTh"));
                }
                hmap2.put(statecode, tmp2);
                arr.put(tmp);
            }
            List<JSONObject> detail = new ArrayList<>();
            for (Object kName : hmap2.keySet()) {
                JSONObject tmp = (JSONObject) hmap2.get(kName);
                if (!tmp.has("time")) {
                    tmp.put("time", 0);
                }
                if (!tmp.has("minTime")) {
                    tmp.put("minTime", 0);
                }
                if (!tmp.has("maxTime")) {
                    tmp.put("maxTime", 0);
                }
                int time = ValidUtils.obj2Int(tmp.get("time"));
                int trnno = ValidUtils.obj2Int(tmp.get("trnno"));
                tmp.put("time", (trnno > 0) ? (time / (trnno)) : time);
                detail.add(tmp);
            }
            Utils.sortJSONObjectByString(detail, "statuscode", true);
            JSONObject data = new JSONObject().put("type", "status")
                    .put("detail", detail);
            if (isDtl) {
                HashMap mapDtl = new HashMap();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    String k = obj.getString("prodcode") + "_" + obj.getString("statecode") + "_" + DateUtils.getDisplayEnDate((Date) obj.get("trnDate"), "yyyyMMdd");
                    JSONObject tmp = new JSONObject();
                    if (null != mapDtl.get(k)) {
                        tmp = (JSONObject) mapDtl.get(k);
                        int custno = ValidUtils.obj2Int(tmp.get("custno")) + ValidUtils.obj2Int(obj.get("custno"));
                        int trnno = ValidUtils.obj2Int(tmp.get("trnno")) + ValidUtils.obj2Int(obj.get("trnno"));
                        BigDecimal amount = ValidUtils.obj2BigDec(tmp.get("amount")).add(ValidUtils.obj2BigDec(obj.get("amount")));
                        tmp.put("custno", custno)
                                .put("trnno", trnno)
                                .put("amount", amount);

                    } else {    //Product name,company name, วันที่ทำรายการ, channel, จำนวนผู้เข้าใช้งาน, จำนวนtxn, จำนวนเงิน, %จำนวนคนต่อ txn
                        tmp.put("prodcode", ValidUtils.null2NoData(obj.get("prodcode")))
                                .put("prodname", ValidUtils.null2NoData(obj.get("prodname")))
                                .put("companyname", ValidUtils.null2NoData(obj.get("companyname")))
                                .put("department", ValidUtils.null2NoData(obj.get("department")))
                                .put("businessline", ValidUtils.null2NoData(obj.get("businessline")))
                                .put("channel", ValidUtils.null2NoData(obj.get("channel")))
                                .put("trnDate", DateUtils.getDisplayEnDate((Date) obj.get("trnDate"), "dd/MM/yyyy"))
                                .put("custno", ValidUtils.obj2Int(obj.get("custno")))
                                .put("trnno", ValidUtils.obj2Int(obj.get("trnno")))
                                .put("amount", ValidUtils.obj2BigDec(obj.get("amount")))
                                .put("statecode", ValidUtils.null2NoData(obj.get("statecode")))
                                .put("statenameTh", ValidUtils.null2NoData(obj.get("statenameTh")))
                                .put("statenameEn", ValidUtils.null2NoData(obj.get("statenameEn")))
                                .put("percent", BigDecimal.ZERO);
                    }
                    mapDtl.put(k, tmp);
                }
                List<JSONObject> listDtl = new ArrayList<>();
                for (Object kName : mapDtl.keySet()) {
                    JSONObject tmp = (JSONObject) mapDtl.get(kName);
                    int custno = ValidUtils.obj2Int(tmp.get("custno"));
                    int trnno = ValidUtils.obj2Int(tmp.get("trnno"));
                    tmp.put("percent", trnno > 0 ? (double) (((double) custno / (double) trnno) * 100) : 0);
//                    tmp.put("percent", custno > 0 ? (double) ((trnno / custno) * 100) : 0);     //Reference Panadda 07/04/2020
                    listDtl.add(tmp);
                }
                data.put("list", listDtl);
            }
            resp.put("data", data);
        } catch (JSONException | NullPointerException | HibernateException e) {
            resp.put("status", 500)
                    .put("description", "" + e);
            //e.printStackTrace();
        }
        return resp;
    }

    private static JSONObject getDashboardCampaign(Boolean isDtl, HashMap hmap) {
        JSONObject resp = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
        try {
            HashMap hmap2 = new HashMap();
            JSONArray arr = new JSONArray();
            for (Object kName : hmap.keySet()) {
                JSONObject tmp = (JSONObject) hmap.get(kName);
                String statecode = tmp.getString("statecode");
                JSONObject tmp2 = new JSONObject();
                if (null != hmap2.get(statecode)) {
                    JSONObject tmp3 = (JSONObject) hmap2.get(statecode);
                    int custno = ValidUtils.obj2Int(tmp.get("custno")) + (ValidUtils.obj2Int(tmp3.get("custno")));
                    int trnno = ValidUtils.obj2Int(tmp.get("trnno")) + (ValidUtils.obj2Int(tmp3.get("trnno")));
                    int time = ValidUtils.obj2Int(tmp.get("time")) + (ValidUtils.obj2Int(tmp3.get("time")));
                    int minTime = tmp3.getInt("minTime") > ValidUtils.obj2Int(tmp.get("time")) ? ValidUtils.obj2Int(tmp.get("time")) : tmp3.getInt("minTime");
                    int maxTime = tmp3.getInt("maxTime") < ValidUtils.obj2Int(tmp.get("time")) ? ValidUtils.obj2Int(tmp.get("time")) : tmp3.getInt("maxTime");
                    tmp2 = tmp3;
                    tmp2.put("custno", custno)
                            .put("trnno", trnno)
                            .put("minTime", minTime)
                            .put("maxTime", maxTime)
                            .put("time", time);
                } else {
                    tmp2.put("statuscode", tmp.getString("statecode"))
                            .put("time", ValidUtils.obj2BigInt(tmp.get("time")))
                            .put("minTime", ValidUtils.obj2BigInt(tmp.get("time")))
                            .put("maxTime", ValidUtils.obj2BigInt(tmp.get("time")))
                            .put("custno", ValidUtils.obj2BigInt(tmp.get("custno")))
                            .put("trnno", ValidUtils.obj2BigInt(tmp.get("trnno")))
                            .put("statusname", tmp.getString("statenameTh"));
                }
                hmap2.put(statecode, tmp2);
                arr.put(tmp);
            }
            JSONArray detail = new JSONArray();
            for (Object kName : hmap2.keySet()) {
                JSONObject tmp = (JSONObject) hmap2.get(kName);
                int time = ValidUtils.obj2Int(tmp.get("time"));
                int trnno = ValidUtils.obj2Int(tmp.get("trnno"));
                tmp.put("time", (trnno > 0) ? (time / (trnno)) : time);
                detail.put(tmp);
            }
            JSONObject data = new JSONObject().put("type", "campaign")
                    .put("detail", detail);
            if (isDtl) {
                HashMap mapDtl = new HashMap();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    String k = obj.getString("prodcode") + "_" + obj.getString("statecode") + "_" + DateUtils.getDisplayEnDate((Date) obj.get("trnDate"), "yyyyMMdd");
                    JSONObject tmp = new JSONObject();
                    if (null != mapDtl.get(k)) {
                        tmp = (JSONObject) mapDtl.get(k);
                        int custno = ValidUtils.obj2Int(tmp.get("custno")) + ValidUtils.obj2Int(obj.get("custno"));
                        int trnno = ValidUtils.obj2Int(tmp.get("trnno")) + ValidUtils.obj2Int(obj.get("trnno"));
                        BigDecimal amount = ValidUtils.obj2BigDec(tmp.get("amount")).add(ValidUtils.obj2BigDec(obj.get("amount")));
                        tmp.put("custno", custno)
                                .put("trnno", trnno)
                                .put("amount", amount);

                    } else {    //Product name,company name, วันที่ทำรายการ, channel, จำนวนผู้เข้าใช้งาน, จำนวนtxn, จำนวนเงิน, %จำนวนคนต่อ txn
                        tmp.put("prodcode", ValidUtils.null2NoData(obj.get("prodcode")))
                                .put("prodname", ValidUtils.null2NoData(obj.get("prodname")))
                                .put("companyname", ValidUtils.null2NoData(obj.get("companyname")))
                                .put("department", ValidUtils.null2NoData(obj.get("department")))
                                .put("businessline", ValidUtils.null2NoData(obj.get("businessline")))
                                .put("channel", ValidUtils.null2NoData(obj.get("channel")))
                                .put("trnDate", DateUtils.getDisplayEnDate((Date) obj.get("trnDate"), "dd/MM/yyyy"))
                                .put("custno", ValidUtils.obj2Int(obj.get("custno")))
                                .put("trnno", ValidUtils.obj2Int(obj.get("trnno")))
                                .put("amount", ValidUtils.obj2BigDec(obj.get("amount")))
                                .put("statecode", ValidUtils.null2NoData(obj.get("statecode")))
                                .put("statenameTh", ValidUtils.null2NoData(obj.get("statenameTh")))
                                .put("statenameEn", ValidUtils.null2NoData(obj.get("statenameEn")))
                                .put("percent", BigDecimal.ZERO);
                    }
                    mapDtl.put(k, tmp);
                }
                List<JSONObject> listDtl = new ArrayList<>();
                for (Object kName : mapDtl.keySet()) {
                    JSONObject tmp = (JSONObject) mapDtl.get(kName);
                    int custno = ValidUtils.obj2Int(tmp.get("custno"));
                    int trnno = ValidUtils.obj2Int(tmp.get("trnno"));
                    tmp.put("percent", trnno > 0 ? (double) (((double) custno / (double) trnno) * 100) : 0);
//                    tmp.put("percent", custno > 0 ? (double) ((trnno / custno) * 100) : 0);     //Reference Panadda 07/04/2020
                    listDtl.add(tmp);
                }
                data.put("list", listDtl);
            }
            resp.put("data", data);
        } catch (JSONException |HibernateException | NullPointerException e) {
            resp.put("status", 500)
                    .put("description", "" + e);
            //e.printStackTrace();
        }
        return resp;
    }

    private static JSONObject getDashboardPeriod(Boolean isDtl, HashMap map) {
        JSONObject resp = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
        try {
            String display[] = {"00:00-01:00", "01:01-02:00", "02:01-03:00", "03:01-04:00", "04:01-05:00", "05:01-06:00", "06:01-07:00", "07:01-08:00",
                "08:01-09:00", "09:01-10:00", "10:01-11:00", "11:01-12:00", "12:01-13:00", "13:01-14:00", "14:01-15:00", "15:01-16:00", "16:01-17:00",
                "17:01-18:00", "18:01-19:00", "19:01-20:00", "20:01-21:00", "21:01-22:00", "22:01-23:00", "23:01-24:00"};
            HashMap hmap = new HashMap();
            JSONArray arr = new JSONArray();
            for (Object k : map.keySet()) {
                JSONObject sol = (JSONObject) map.get(k);
                if (null != sol.get("trnDate")) {
                    Date trnDate = (Date) sol.get("trnDate");
                    if (null != trnDate) {
                        String kName = DateUtils.getDisplayEnDate(trnDate, "HH");
                        JSONObject tmp = new JSONObject();
                        if (null != hmap.get(kName)) {
                            tmp = (JSONObject) hmap.get(kName);
                            tmp.put("trnno", ValidUtils.obj2BigInt(sol.get("trnno")).add(ValidUtils.obj2BigInt(tmp.get("trnno"))));
                            tmp.put("custno", ValidUtils.obj2BigInt(sol.get("custno")).add(ValidUtils.obj2BigInt(tmp.get("custno"))).toString());
                        } else {
                            tmp.put("trnno", ValidUtils.obj2BigInt(sol.get("trnno")));
                            tmp.put("custno", ValidUtils.obj2BigInt(sol.get("custno")));
                        }
                        hmap.put(kName, tmp);
                    }
                }
                arr.put(sol);
            }
            JSONArray detail = new JSONArray();
            List sortedKName = new ArrayList(hmap.keySet());
            Collections.sort(sortedKName);
            for (Object kName : sortedKName) {
                JSONObject tmp = (JSONObject) hmap.get(kName);
                int idx = ValidUtils.obj2Int(kName);
                if (idx <= 23) {
                    String str = display[idx];
                    tmp.put("period", str);
                    detail.put(tmp);
                }
            }
            JSONObject data = new JSONObject().put("type", "period")
                    .put("detail", detail);
            if (isDtl) {
                HashMap mapDtl = new HashMap();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    Date trnDate = (Date) obj.get("trnDate");
                    String k = obj.getString("prodcode") + "_" + DateUtils.getDisplayEnDate(trnDate, "HH") + "_" + DateUtils.getDisplayEnDate(trnDate, "yyyyMMdd");
                    JSONObject tmp = new JSONObject();
                    if (null != mapDtl.get(k)) {
                        tmp = (JSONObject) mapDtl.get(k);
                        int custno = ValidUtils.obj2Int(tmp.get("custno")) + ValidUtils.obj2Int(obj.get("custno"));
                        int trnno = ValidUtils.obj2Int(tmp.get("trnno")) + ValidUtils.obj2Int(obj.get("trnno"));
                        BigDecimal amount = ValidUtils.obj2BigDec(tmp.get("amount")).add(ValidUtils.obj2BigDec(obj.get("amount")));
                        tmp.put("custno", custno)
                                .put("trnno", trnno)
                                .put("amount", amount);

                    } else {    //Product name,company name, วันที่ทำรายการ, channel, จำนวนผู้เข้าใช้งาน, จำนวนtxn, จำนวนเงิน, %จำนวนคนต่อ txn
                        tmp.put("prodcode", ValidUtils.null2NoData(obj.get("prodcode")))
                                .put("prodname", ValidUtils.null2NoData(obj.get("prodname")))
                                .put("companyname", ValidUtils.null2NoData(obj.get("companyname")))
                                .put("department", ValidUtils.null2NoData(obj.get("department")))
                                .put("businessline", ValidUtils.null2NoData(obj.get("businessline")))
                                .put("channel", ValidUtils.null2NoData(obj.get("channel")))
                                .put("trnDate", DateUtils.getDisplayEnDate((Date) obj.get("trnDate"), "dd/MM/yyyy"))
                                .put("trnTime", DateUtils.getDisplayEnDate((Date) obj.get("trnDate"), "HH:mm"))
                                .put("custno", ValidUtils.obj2Int(obj.get("custno")))
                                .put("trnno", ValidUtils.obj2Int(obj.get("trnno")))
                                .put("amount", ValidUtils.obj2BigDec(obj.get("amount")))
                                .put("statecode", ValidUtils.null2NoData(obj.get("statecode")))
                                .put("statenameTh", ValidUtils.null2NoData(obj.get("statenameTh")))
                                .put("statenameEn", ValidUtils.null2NoData(obj.get("statenameEn")))
                                .put("period", display[ValidUtils.obj2Int(DateUtils.getDisplayEnDate(trnDate, "HH"))])
                                .put("percent", BigDecimal.ZERO);
                    }
                    mapDtl.put(k, tmp);
                }
                List<JSONObject> listDtl = new ArrayList<>();
                for (Object kName : mapDtl.keySet()) {
                    JSONObject tmp = (JSONObject) mapDtl.get(kName);
                    int custno = ValidUtils.obj2Int(tmp.get("custno"));
                    int trnno = ValidUtils.obj2Int(tmp.get("trnno"));
                    tmp.put("percent", trnno > 0 ? (double) (((double) custno / (double) trnno) * 100) : 0);
//                    tmp.put("percent", custno > 0 ? (double) ((trnno / custno) * 100) : 0);     //Reference Panadda 07/04/2020
                    listDtl.add(tmp);
                }
                data.put("list", listDtl);
            }
            resp.put("data", data);
        } catch (JSONException | NullPointerException | HibernateException e) {
            resp.put("status", 500)
                    .put("description", "" + e);
            //e.printStackTrace();
        }
        return resp;
    }

    public static JSONObject getDatasSysOperLogAll(String dbEnv, String productCode, String startDate, String endDate, Integer status, Integer trnStatus) {
        JSONObject result = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            JSONArray datas = new JSONArray();
            HashMap hmap = new DashboardDao().getTrnDashBoard(dbEnv, null, startDate, endDate, status, trnStatus, null);
            JSONObject resp = getDashboardState(dbEnv, false, hmap);
            if (resp.get("status").equals(200) && resp.has("data") && resp.getJSONObject("data").length() > 0) {
                datas.put(resp.getJSONObject("data"));
            }
            resp = getDashboardPeriod(false, hmap);
            if (resp.get("status").equals(200) && resp.has("data") && resp.getJSONObject("data").length() > 0) {
                datas.put(resp.getJSONObject("data"));
            }
            resp = getDashboardCampaign(false, hmap);
            if (resp.get("status").equals(200) && resp.has("data") && resp.getJSONObject("data").length() > 0) {
                datas.put(resp.getJSONObject("data"));
            }
            if (null != productCode && !productCode.isEmpty()) {
                hmap = new DashboardDao().getTrnDashBoard(dbEnv, productCode, startDate, endDate, status, trnStatus, null);
            }
            resp = getDashboardProduct(false, hmap, productCode);
            if (resp.get("status").equals(200) && resp.has("data") && resp.getJSONObject("data").length() > 0) {
                datas.put(resp.getJSONObject("data"));
            }
            resp = getDashboardChannel(false, hmap, productCode);
            if (resp.get("status").equals(200) && resp.has("data") && resp.getJSONObject("data").length() > 0) {
                datas.put(resp.getJSONObject("data"));
            }
            result.put("datas", datas);
        } catch (HibernateException | NullPointerException | SQLException | UnsupportedEncodingException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            result.put("status", 500)
                    .put("description", "" + e);
        }
        return result;
    }

    public static JSONObject getDatasSysOperLogProduct(String dbEnv, String productCode, String startDate, String endDate, Integer status, Integer trnStatus) {
        JSONObject result = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            JSONArray datas = new JSONArray();
            HashMap hmap = new DashboardDao().getTrnDashBoard(dbEnv, productCode, startDate, endDate, status, trnStatus, null);
            JSONObject resp = getDashboardProduct(true, hmap, productCode);
            if (resp.get("status").equals(200) && resp.has("data") && resp.getJSONObject("data").length() > 0) {
                datas.put(resp.getJSONObject("data"));
                result.put("datas", datas);
            } else {
                result.put("status", 500)
                        .put("description", resp.has("description") ? resp.getString("description") : "");
            }
        } catch (HibernateException | NullPointerException | SQLException | UnsupportedEncodingException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            result.put("status", 500)
                    .put("description", "" + e);
        }
        return result;
    }

    public static JSONObject getDatasSysOperLogChannel(String dbEnv, String productCode, String startDate, String endDate, Integer status, Integer trnStatus) {
        JSONObject result = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            JSONArray datas = new JSONArray();
            HashMap hmap = new DashboardDao().getTrnDashBoard(dbEnv, productCode, startDate, endDate, status, trnStatus, null);
            JSONObject resp = getDashboardChannel(true, hmap, productCode);
            if (resp.get("status").equals(200) && resp.has("data") && resp.getJSONObject("data").length() > 0) {
                datas.put(resp.getJSONObject("data"));
            }
            result.put("datas", datas);
        } catch (HibernateException | NullPointerException | SQLException | UnsupportedEncodingException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            result.put("status", 500)
                    .put("description", "" + e);
        }
        return result;
    }

    public static JSONObject getDatasSysOperLogState(String dbEnv, String productCode, String startDate, String endDate, Integer status, Integer trnStatus, String state) {
        JSONObject result = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            JSONArray datas = new JSONArray();
            HashMap hmap = new DashboardDao().getTrnDashBoard(dbEnv, productCode, startDate, endDate, status, trnStatus, state);
            JSONObject resp = getDashboardState(dbEnv, true, hmap);
            if (resp.get("status").equals(200) && resp.has("data") && resp.getJSONObject("data").length() > 0) {
                datas.put(resp.getJSONObject("data"));
            }
            result.put("datas", datas);
        } catch (HibernateException | NullPointerException | SQLException | UnsupportedEncodingException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            result.put("status", 500)
                    .put("description", "" + e);
        }
        return result;
    }

    public static JSONObject getDatasSysOperLogPeroid(String dbEnv, String productCode, String startDate, String endDate, Integer status, Integer trnStatus, String state) {
        JSONObject result = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            JSONArray datas = new JSONArray();
            HashMap hmap = new DashboardDao().getTrnDashBoard(dbEnv, productCode, startDate, endDate, status, trnStatus, state);
            JSONObject resp = getDashboardPeriod(true, hmap);
            if (resp.get("status").equals(200) && resp.has("data") && resp.getJSONObject("data").length() > 0) {
                datas.put(resp.getJSONObject("data"));
            }
            result.put("datas", datas);
        } catch (HibernateException | NullPointerException | UnsupportedEncodingException | SQLException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            result.put("status", 500)
                    .put("description", "" + e);
        }
        return result;
    }

    public static JSONObject getDatasSysOperLogCampaign(String dbEnv, String productCode, String startDate, String endDate, Integer status, Integer trnStatus, String state) {
        JSONObject result = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            JSONArray datas = new JSONArray();
            HashMap hmap = new DashboardDao().getTrnDashBoard(dbEnv, productCode, startDate, endDate, status, trnStatus, state);
            JSONObject resp = getDashboardCampaign(true, hmap);
            if (resp.get("status").equals(200) && resp.has("data") && resp.getJSONObject("data").length() > 0) {
                datas.put(resp.getJSONObject("data"));
            }
            result.put("datas", datas);
        } catch (HibernateException | NullPointerException | SQLException | UnsupportedEncodingException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            result.put("status", 500)
                    .put("description", "" + e);
        }
        return result;
    }

    public static JSONObject getDatasSysOperLogProdChannel(String dbEnv, String productCode, String startDate, String endDate, Integer status, Integer trnStatus, String state) {
        JSONObject result = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            JSONArray datas = new JSONArray();
            HashMap hmap = new DashboardDao().getTrnDashBoard(dbEnv, productCode, startDate, endDate, status, trnStatus, state);
            JSONObject resp = getDashboardProduct(false, hmap, productCode);
            if (resp.get("status").equals(200) && resp.has("data") && resp.getJSONObject("data").length() > 0) {
                datas.put(resp.getJSONObject("data"));
            }
            resp = getDashboardChannel(false, hmap, productCode);
            if (resp.get("status").equals(200) && resp.has("data") && resp.getJSONObject("data").length() > 0) {
                datas.put(resp.getJSONObject("data"));
            }
            result.put("datas", datas);
        } catch (HibernateException | NullPointerException | SQLException | UnsupportedEncodingException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            result.put("status", 500)
                    .put("description", "" + e);
        }
        return result;
    }

    public static JSONObject getDatasSysOperLogProdStateStatus(String dbEnv, String productCode, String startDate, String endDate, Integer status, Integer trnStatus, String state) {
        JSONObject result = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            JSONArray datas = new JSONArray();
            HashMap hmap = new DashboardDao().getTrnDashBoard(dbEnv, productCode, startDate, endDate, status, trnStatus, state);
            JSONObject resp = getDashboardState(dbEnv, false, hmap);
            if (resp.get("status").equals(200) && resp.has("data") && resp.getJSONObject("data").length() > 0) {
                datas.put(resp.getJSONObject("data"));
            }
            resp = getDashboardPeriod(false, hmap);
            if (resp.get("status").equals(200) && resp.has("data") && resp.getJSONObject("data").length() > 0) {
                datas.put(resp.getJSONObject("data"));
            }
            resp = getDashboardCampaign(false, hmap);
            if (resp.get("status").equals(200) && resp.has("data") && resp.getJSONObject("data").length() > 0) {
                datas.put(resp.getJSONObject("data"));
            }
            result.put("datas", datas);
            resp = new DashboardDao().getWebSiteTrackings(dbEnv, productCode, startDate, endDate, status, trnStatus, state);
            if (resp.get("status").equals(200) && resp.has("data") && resp.getJSONObject("data").length() > 0) {
                datas.put(resp.getJSONObject("data"));
            }
            result.put("datas", datas);
        } catch (HibernateException | NullPointerException | SQLException | UnsupportedEncodingException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            result.put("status", 500)
                    .put("description", "" + e);
        }
        return result;
    }

}
