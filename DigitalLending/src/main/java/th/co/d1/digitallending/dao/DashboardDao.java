/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.dao;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.json.JSONObject;
import th.co.d1.digitallending.entity.SysOperLog;
import th.co.d1.digitallending.util.DateUtils;
import static th.co.d1.digitallending.util.HibernateUtil.getSessionMaster;
import th.co.d1.digitallending.util.ValidUtils;

/**
 *
 * @author Kritsana Sasai
 *
 * DataOne Asia (Thailand) Company Limited 1023 MS Siam Tower, 30th Floor, Rama
 * 3 Rd., Chongnonsi, Yannawa, Bangkok, 10120
 *
 * @create 05-03-2020 10:27:21 AM
 */
public class DashboardDao {

    static final Logger logger = Logger.getLogger(DashboardDao.class.getName());

    public List<SysOperLog> getListSysOperLogDashboard(String dbEnv, String productCode, Date startDate, Date endDate, Integer status, Integer trnStatus) {
        List<SysOperLog> list = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(SysOperLog.class);
            if (null != startDate && null != endDate) {
                criteria.add(Restrictions.between("createAt", DateUtils.utilDateToSqlDate(startDate), DateUtils.utilDateToSqlDate(DateUtils.addDate(endDate, 0, 0, 0, 23, 59, 59))));
            } else if (null == startDate && null != endDate) {
                criteria.add(Restrictions.lt("createAt", DateUtils.utilDateToSqlDate(DateUtils.addDate(endDate, 0, 0, 0, 23, 59, 59))));
            } else if (null != startDate && null == endDate) {
                criteria.add(Restrictions.gt("createAt", DateUtils.utilDateToSqlDate(startDate)));
            }
            criteria.add(Restrictions.eq("trnStatus", trnStatus));
            criteria.add(Restrictions.eq("status", status));
            if (null != productCode && !"".equals(productCode)) {
                criteria.add(Restrictions.eq("productCode", productCode));
            }
            list = criteria.list();
            trans.commit();
//            session.close();
        } catch (HibernateException | NullPointerException e) {
            if (null != trans) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            //e.printStackTrace();
            throw e;
        }
        return list;
    }

    public HashMap<String, JSONObject> getTrnDashBoard(String dbEnv, String prodCode, String startDate, String endDate, Integer status, Integer trnStatus, String state) throws SQLException, UnsupportedEncodingException {
        HashMap<String, JSONObject> hmap = new HashMap<>();
        ResultSet rs = null, rsSub = null;
        PreparedStatement ps = null, psCustNo = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            StringBuilder dashBoardCmd = new StringBuilder();
            List<Object> params = new ArrayList<>();
            dashBoardCmd.append("select log.*,'0' CUSTNO, '1' TRNNO, '0' AMOUNT, '0' PERCENT, LK.LOOKUP_CODE ST_CODE, LK.LOOKUP_NAME_TH ST_NAME_TH, LK.LOOKUP_NAME_EN ST_NAME_EN, LOG.STATUS AS LOG_STATUS, SL.LOOKUP_NAME_TH STATUS_NAME , SP.PROD_CODE PROD_CODE, SP.PROD_NAME PROD_NAME, SP.COMPANY, SP.BUSINESS_DEPT, SP.BUSINESS_LINE  "
                    + "from t_sys_oper_log LOG "
                    + "INNER JOIN T_SHELF_LOOKUP LK ON LOG.STATE_CODE = LK.UUID  "
                    + "INNER JOIN T_SYS_LOOKUP SL ON LOG.STATUS::TEXT = SL.LOOKUP_CODE "
                    + "INNER JOIN T_SHELF_PRODUCT SP ON LOG.PRODUCT_ID = SP.UUID "
                    + "INNER JOIN T_SYS_LOOKUP SL2 ON LOG.TRN_STATUS::TEXT = SL2.LOOKUP_CODE "
                    + "LEFT JOIN T_SYS_LOOKUP SL3 ON LOG.TRN_SUB_STATUS::TEXT = SL3.LOOKUP_CODE      "
                    + "LEFT JOIN T_SHELF_LOOKUP LK2 ON LOG.ATTR2 = LK2.LOOKUP_CODE  "
                    + "WHERE log.uuid IN "
                    + "( "
                    //                    + "        select uuid from ( "
                    //                    + "                SELECT uuid, row_number() over (partition by txn_no order by CREATE_AT desc) as ROW_NO "
                    //                    + "                        , MAX(CREATE_AT) OVER (PARTITION BY TXN_NO) MAX_DATE "
                    //                    + "                        , MAX(PAYMENT_DATE) OVER (PARTITION BY TXN_NO) PAY_DATE "
                    //                    + "                        , MAX(TO_TIMESTAMP(ATTR4, 'yyyy-MM-dd HH24:MI:SS')) OVER (PARTITION BY TXN_NO) UPDATE_DATE "
                    //                    + "            FROM t_sys_oper_log S "
                    //                    + "        )A  "
                    //                    + "        where A.ROW_NO = 1 "
                    + "         select uuid from ("
                    + "   select uuid, row_number() over (partition by txn_no order by TO_TIMESTAMP(log.ATTR4, 'yyyy-MM-dd HH24:MI:SS') desc, log.CREATE_AT desc) as ROW_NO "
                    + "   from t_sys_oper_log log "
                    + "        )A "
                    + "        where A.ROW_NO = 1 "
                    + " ) ");
            if (null != prodCode && !prodCode.isEmpty()) {
                dashBoardCmd.append(" AND SP.PROD_CODE = ? ");
                params.add(prodCode);
            }
            if ((null != startDate && !startDate.isEmpty()) && (null != endDate && !endDate.isEmpty())) {
                dashBoardCmd.append(" AND LOG.CREATE_AT BETWEEN TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS') AND TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(startDate + " 00:00:00");
                params.add(endDate + " 23:59:59");
            } else if ((null == startDate || startDate.isEmpty()) && (null != endDate && !endDate.isEmpty())) {
                dashBoardCmd.append(" AND LOG.CREATE_AT <= TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(endDate + " 23:59:59");
            } else if ((null != startDate && !startDate.isEmpty()) && (null == endDate || endDate.isEmpty())) {
                dashBoardCmd.append(" AND LOG.CREATE_AT >= TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(startDate + " 00:00:00");
            }
            if (null != status) {
                dashBoardCmd.append(" AND LOG.STATUS = ? ");
                params.add(status);
            }
            if (null != trnStatus) {
                dashBoardCmd.append(" AND LOG.TRN_STATUS = ? ");
                params.add(trnStatus);
            }
            if (null != state && !state.isEmpty()) {
                dashBoardCmd.append(" AND LK.LOOKUP_CODE = ? ");
                params.add(state);
            }
            dashBoardCmd.append(" ORDER BY LOG.CREATE_AT ASC ");
            ps = session.doReturningWork((Connection conn) -> conn).prepareStatement(dashBoardCmd.toString());
            if (params.size() > 0) {
                for (int i = 0; i < params.size(); i++) {
                    if (params.get(i) instanceof String) {
                        ps.setString(i + 1, (String) params.get(i));
                    } else {
                        ps.setInt(i + 1, (Integer) params.get(i));
                    }
                }
            }
            rs = ps.executeQuery();
            int idx = 1;
            while (rs.next()) {
                String txnNo = ValidUtils.obj2String(rs.getString("TXN_NO"));
                JSONObject data = new JSONObject().put("id", txnNo)
                        .put("txnNo", ValidUtils.null2NoData(rs.getString("TXN_NO")))
                        .put("refNo", ValidUtils.null2NoData(rs.getString("REF_NO")))
                        .put("prodcode", ValidUtils.null2NoData(rs.getString("PROD_CODE")))
                        .put("prodname", ValidUtils.null2NoData(rs.getString("PROD_NAME")))
                        .put("statecode", ValidUtils.null2NoData(rs.getString("ST_CODE")))
                        .put("statenameTh", ValidUtils.null2NoData(rs.getString("ST_NAME_TH")))
                        .put("statenameEn", ValidUtils.null2NoData(rs.getString("ST_NAME_EN")))
                        .put("companyname", ValidUtils.null2NoData(rs.getString("COMPANY")))
                        .put("trnDate", rs.getTimestamp("CREATE_AT"))
                        .put("channel", ValidUtils.null2NoData(rs.getString("PROD_CHANNEL")))
                        .put("custno", ValidUtils.null2NoData(rs.getString("CUSTNO")))
                        .put("trnno", ValidUtils.null2NoData(rs.getString("TRNNO")))
                        .put("amount", ValidUtils.null2NoData(rs.getString("AMOUNT")))
                        .put("percent", ValidUtils.null2NoData(rs.getString("PERCENT")))
                        .put("department", ValidUtils.null2NoData(rs.getString("BUSINESS_DEPT")))
                        .put("businessline", ValidUtils.null2NoData(rs.getString("BUSINESS_LINE")))
                        .put("time", ValidUtils.obj2Int(rs.getInt("STATE_TIME")))
                        .put("stepdata", "");
                String trnState = ValidUtils.null2NoData(rs.getString("TRN_STATUS"));
                /*
                    dashBoardCmd.setLength(0);
                    dashBoardCmd.append("SELECT '1' NUM_ROWS FROM T_SYS_OPER_LOG WHERE TXN_NO = ? AND STATE_CODE in (select uuid from t_shelf_lookup where lookup_code in ('PRO1013','PRO1014','PRO1015','PRO1017'))");
                    psCustNo = con.prepareStatement(dashBoardCmd.toString());
                    psCustNo.setString(1, txnNo);
                    rsSub = psCustNo.executeQuery();
                    while (rsSub.next()) {
                        data.put("custno", rsSub.getString("NUM_ROWS"));
                    }*/
                dashBoardCmd.setLength(0);
                /*dashBoardCmd.append("SELECT MAX(LOG.CREATE_AT) MAX_DATE, LOG.STEP_DATA "
                            + "FROM T_SYS_OPER_LOG LOG "
                            + "WHERE 1=1 "
                            + "AND TXN_NO = ? "
                            + "AND STATE_CODE = (select uuid from t_shelf_lookup where lookup_code = 'PRO1011')");*/
                dashBoardCmd.append("SELECT dd.* FROM (SELECT LOG.CREATE_AT, MAX(LOG.CREATE_AT) OVER (PARTITION BY LOG.TXN_NO) MAX_DATE, LOG.STEP_DATA  "
                        + "FROM T_SYS_OPER_LOG LOG "
                        + "WHERE 1=1 "
                        + "AND TXN_NO = ? "
                        //                        + "AND STATE_CODE = (select uuid from t_shelf_lookup where lookup_code = 'PRO1011')) dd WHERE CREATE_AT = MAX_DATE ");
                        + "AND STATE_CODE in (select uuid from t_shelf_lookup where lookup_code in ('PRO1011','PRO1032'))) dd WHERE CREATE_AT = MAX_DATE ");
                psCustNo = session.doReturningWork((Connection conn) -> conn).prepareStatement(dashBoardCmd.toString());
                psCustNo.setString(1, txnNo);
                rsSub = psCustNo.executeQuery();
                while (rsSub.next()) {
                    String stData = rsSub.getString("STEP_DATA");
                    if (null != stData && !"".equals(stData)) {
                        byte[] decodedBytes = java.util.Base64.getDecoder().decode(ValidUtils.null2NoData(stData));
                        String stepdata = new String(decodedBytes, "UTF-8");
                        data.put("stepdata", stepdata);
                        JSONArray arr = new JSONArray(stepdata);
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject stepDataObj = arr.getJSONObject(i);
                            if ("loan".equalsIgnoreCase(stepDataObj.getString("code"))) {
                                /*if (tmp.has("data")) {
                                        String limit = ValidUtils.null2Separator(tmp.getJSONObject("data").has("limit") ? tmp.getJSONObject("data").get("limit") : "0", "0");
                                        data.put("amount", !limit.isEmpty() ? limit : "0");
                                    }*/
                                if (stepDataObj.getJSONObject("data").has("package")) {
                                    for (int j = 0; j < stepDataObj.getJSONObject("data").getJSONArray("package").length(); j++) {
                                        JSONObject packageData = stepDataObj.getJSONObject("data").getJSONArray("package").getJSONObject(j);
                                        if (packageData.has("parameter")) {
                                            switch (packageData.getString("parameter")) {
                                                case "limit":
                                                    data.put("amount", packageData.getString("value"));
                                                    break;
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
                if (data.has("statecode")) {
                    if (("PRO1013".equalsIgnoreCase(data.getString("statecode")) || "PRO1014".equalsIgnoreCase(data.getString("statecode"))
                            || "PRO1015".equalsIgnoreCase(data.getString("statecode")) || "PRO1016".equalsIgnoreCase(data.getString("statecode"))
                            || "PRO1017".equalsIgnoreCase(data.getString("statecode")) || "PRO1050".equalsIgnoreCase(data.getString("statecode"))
                            || "PRO1051".equalsIgnoreCase(data.getString("statecode")) || "PRO1052".equalsIgnoreCase(data.getString("statecode"))
                            || "PRO1053".equalsIgnoreCase(data.getString("statecode")) || "PRO1054".equalsIgnoreCase(data.getString("statecode"))
                            || "PRO1055".equalsIgnoreCase(data.getString("statecode")) || "PRO1056".equalsIgnoreCase(data.getString("statecode"))
                            || "PRO1057".equalsIgnoreCase(data.getString("statecode")) || "PRO1058".equalsIgnoreCase(data.getString("statecode")))
                            && "200".equalsIgnoreCase(trnState)) {
                        data.put("custno", "1");
                    } else {
                        data.put("amount", "0");
                    }
                }
                String trnIdx = idx + "_" + txnNo;
                hmap.put(trnIdx, data);
                idx++;
                if (!rsSub.isClosed()) {
                    rsSub.close();
                }
                if (!psCustNo.isClosed()) {
                    psCustNo.close();
                }
            }
        } catch (HibernateException | NullPointerException e) {
            logger.info(e.getMessage());
            if (rsSub != null && !rsSub.isClosed()) {
                rsSub.close();
            }
            if (psCustNo != null && !psCustNo.isClosed()) {
                psCustNo.close();
            }
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            throw e;
        }
        return hmap;
    }

    public JSONObject getWebSiteTrackings(String dbEnv, String prodCode, String startDate, String endDate, Integer status, Integer trnStatus, String state) throws SQLException {
        JSONObject resp = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
        JSONArray arr = new JSONArray();
        ResultSet rs = null;
        PreparedStatement ps = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            StringBuilder dashBoardCmd = new StringBuilder();
            List<Object> params = new ArrayList<>();
            /*dashBoardCmd.append("select state_code, lookup_code, lookup_name_th, lookup_name_en, description, COALESCE(ROUND(sum_time / count_row, 2), 0.00) as average ,sum_time, count_row ,min_state_time, max_state_time from ( "
                    + " select sol.state_code, sl.lookup_code, sl.lookup_name_th, sl.lookup_name_en, sl.description, sum(sol.state_time) sum_time, count(sl.lookup_code) count_row ,min(sol.state_time) min_state_time, max(sol.state_time) max_state_time from t_sys_oper_log sol, t_shelf_lookup sl where 1=1 ");
            if ((null != startDate && !startDate.isEmpty()) && (null != endDate && !endDate.isEmpty())) {
                dashBoardCmd.append("  and sol.CREATE_AT BETWEEN TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS') AND TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS') ");
                params.add(startDate + " 00:00:00");
                params.add(endDate + " 23:59:59");
            } else if ((null == startDate || startDate.isEmpty()) && (null != endDate && !endDate.isEmpty())) {
                dashBoardCmd.append(" AND sol.CREATE_AT <= TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(endDate + " 23:59:59");
            } else if ((null != startDate && !startDate.isEmpty()) && (null == endDate || endDate.isEmpty())) {
                dashBoardCmd.append(" AND sol.CREATE_AT >= TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(startDate + " 00:00:00");
            }
            if (null != prodCode && !prodCode.isEmpty()) {
                dashBoardCmd.append(" and sol.product_code = ? ");
                params.add(prodCode);
            }
            if (null != status) {
                dashBoardCmd.append(" AND sol.status = ? ");
                params.add(status);
            }
            if (null != trnStatus) {
                dashBoardCmd.append(" AND sol.trn_status = ? ");
                params.add(trnStatus);
            }
            if (null != state && !state.isEmpty()) {
                dashBoardCmd.append(" AND sl.lookup_code = ? ");
                params.add(state);
            }
            dashBoardCmd.append("  and sol.state_code = sl.uuid group by sol.state_code, sl.lookup_code, sl.lookup_name_th, sl.lookup_name_en, sl.description order by sl.lookup_code) ss");*/
            dashBoardCmd.append("select lookup.uuid state_code, lookup.lookup_code, lookup.lookup_name_th, lookup.lookup_name_en, lookup.description "
                    + ",COALESCE(ROUND(sum_time2 / count_row2, 2), 0.00) as average,ss.sum_time2 as summ_time,ss.count_row2 as count_row ,ss.min_state_time2 as min_state_time, ss.max_state_time2 as max_state_time "
                    + "from t_shelf_lookup lookup "
                    + "left join ( "
                    + "    select state2.state_code, sum(state2.count_row) as count_row2 "
                    + "    ,sum(state2.sum_time) sum_time2,min(state2.min_state_time) as min_state_time2 ,max(state2.max_state_time) as max_state_time2 "
                    + "    from ( "
                    + "      select t.state_code,t.sum_time , t.min_state_time , t.max_state_time , count(t.state_code) as count_row "
                    + "      from ( "
                    + "       select sol.state_code,sol.txn_no ,sum(sol.state_time) sum_time, min(sol.state_time) as min_state_time ,max(sol.state_time) as max_state_time "
                    + "       from t_sys_oper_log sol "
                    + "       where 1=1  ");
            if ((null != startDate && !startDate.isEmpty()) && (null != endDate && !endDate.isEmpty())) {
                dashBoardCmd.append("  and sol.CREATE_AT BETWEEN TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS') AND TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS') ");
                params.add(startDate + " 00:00:00");
                params.add(endDate + " 23:59:59");
            } else if ((null == startDate || startDate.isEmpty()) && (null != endDate && !endDate.isEmpty())) {
                dashBoardCmd.append(" AND sol.CREATE_AT <= TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(endDate + " 23:59:59");
            } else if ((null != startDate && !startDate.isEmpty()) && (null == endDate || endDate.isEmpty())) {
                dashBoardCmd.append(" AND sol.CREATE_AT >= TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(startDate + " 00:00:00");
            }
            if (null != prodCode && !prodCode.isEmpty()) {
                dashBoardCmd.append(" and sol.product_code = ? ");
                params.add(prodCode);
            }
            if (null != status) {
                dashBoardCmd.append(" AND sol.status = ? ");
                params.add(status);
            }
            if (null != trnStatus) {
                dashBoardCmd.append(" AND sol.trn_status = ? ");
                params.add(trnStatus);
            }
            dashBoardCmd.append(" group by sol.state_code,sol.txn_no  "
                    + "       ) t "
                    + "      group by t.state_code ,t.sum_time  , t.min_state_time , t.max_state_time  "
                    + "     ) state2 "
                    + "    group by state2.state_code "
                    + "   ) ss on lookup.uuid  = ss.state_code "
                    + "where 1=1 "
                    + "and lookup.lookup_type = 'PROCESS_STATE' "
                    + "and lookup.lookup_code not in ('PRO1018') ");

            if (null != state && !state.isEmpty()) {
                dashBoardCmd.append(" AND lookup.lookup_code = ? ");
                params.add(state);
            }
            dashBoardCmd.append("order by lookup_code");
            ps = session.doReturningWork((Connection conn) -> conn).prepareStatement(dashBoardCmd.toString());
            if (params.size() > 0) {
                for (int i = 0; i < params.size(); i++) {
                    if (params.get(i) instanceof String) {
                        ps.setString(i + 1, (String) params.get(i));
                    } else {
                        ps.setInt(i + 1, (Integer) params.get(i));
                    }
                }
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                JSONObject data = new JSONObject().put("state_id", rs.getString("state_code"))
                        .put("state_code", ValidUtils.null2NoData(rs.getString("lookup_code")))
                        .put("state_name_th", ValidUtils.null2NoData(rs.getString("lookup_name_th")))
                        .put("state_name_en", ValidUtils.null2NoData(rs.getString("lookup_name_en")))
                        .put("state_desc", ValidUtils.null2NoData(rs.getString("description")))
                        .put("sum_time", ValidUtils.null2NoData(rs.getBigDecimal("summ_time")))
                        .put("average", ValidUtils.null2NoData(rs.getBigDecimal("average")))
                        .put("trn_no", ValidUtils.null2NoData(rs.getInt("count_row")))
                        .put("min_state_time", ValidUtils.null2NoData(rs.getInt("min_state_time")))
                        .put("max_state_time", ValidUtils.null2NoData(rs.getInt("max_state_time")));
                arr.put(data);
            }
            JSONObject data = new JSONObject().put("type", "tracking")
                    .put("detail", arr);
            resp.put("data", data);
        } catch (HibernateException | NullPointerException e) {
            logger.info(e.getMessage());
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            //e.printStackTrace();
            throw e;
        }
        return resp;
    }
}
