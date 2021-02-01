/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.dao;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.codec.binary.Base64;
import java.util.logging.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.json.JSONObject;
import th.co.d1.digitallending.entity.ShelfLookup;
import th.co.d1.digitallending.entity.SysLog;
import th.co.d1.digitallending.entity.SysOperLog;
import th.co.d1.digitallending.util.DateUtils;
import static th.co.d1.digitallending.util.HibernateUtil.getSessionMaster;
import th.co.d1.digitallending.util.StatusUtils;
import th.co.d1.digitallending.util.Utils;
import th.co.d1.digitallending.util.ValidUtils;

/**
 *
 * @author Kritsana Sasai
 *
 * DataOne Asia (Thailand) Company Limited 1023 MS Siam Tower, 30th Floor, Rama
 * 3 Rd., Chongnonsi, Yannawa, Bangkok, 10120
 *
 * @create 30-01-2020 11:20:48 AM
 */
public class SysOperLogDao {

    final static Logger logger = Logger.getLogger(SysLookupDao.class.getName());
    private final SimpleDateFormat yearMonthDay = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat dayMonthYear = new SimpleDateFormat("dd/MM/yyyy");

    public List<SysOperLog> getListSysOperLog(String dbEnv) {
        List<SysOperLog> list = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(SysOperLog.class);
            list = criteria.list();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (null != trans) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return list;
    }

    public JSONObject saveSysOperLog(String dbEnv, SysOperLog sysOperLog, SysLog sysLog) throws HibernateException, NullPointerException {
        JSONObject resp = new JSONObject();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            Date sysdate = new Date();
            trans = session.beginTransaction();
            if (null == sysOperLog.getCreateAt()) {
                sysOperLog.setAttr4(DateUtils.getDisplayEnDate(sysdate, "yyyy-MM-dd HH:mm:ss"));
                sysOperLog.setCreateAt(sysdate);
            }
            session.save(sysOperLog);
            if (null != sysLog) {
                Criteria criteria = session.createCriteria(SysLog.class);
                criteria.add(Restrictions.eq("prodCode", sysOperLog.getProductCode()));
                criteria.add(Restrictions.eq("caseId", sysOperLog.getCaseId()));
                List<SysLog> list = criteria.list();
                if ((null == list || list.isEmpty()) && null != sysOperLog.getCaseId() && !"".equals(sysOperLog.getCaseId())) {
                    if (null == sysLog.getCreateAt()) {
                        sysLog.setCreateAt(sysdate);
                    }
                    session.save(sysLog);
                } else {
                    if (sysOperLog.getTrnStatus().equals(StatusUtils.getPass(dbEnv).getStatusCode())) {
                        SysLog sl2 = list.get(0);
                        sl2.setGroupProduct(sysLog.getGroupProduct());
                        sl2.setState(sysLog.getState());
                        sl2.setStatus(sysOperLog.getStatus());
                        if (null == sl2.getUpdateAt()) {
                            sl2.setUpdateAt(sysdate);
                        }
                        session.save(sl2);
                    }
                }
            }
            trans.commit();
            resp.put("status", true);
        } catch (HibernateException | NullPointerException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            if (null != trans) {
                trans.rollback();
            }
            resp.put("status", false);
            resp.put("description", "" + e);
        }
        return resp;
    }

    public SysOperLog getSysOperLog(String dbEnv, String uuid) {
        SysOperLog sysLog = new SysOperLog();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            sysLog = (SysOperLog) session.get(SysOperLog.class, uuid);
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (null != trans) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return sysLog;
    }

    public List<SysOperLog> getListSysOperLogByTrnDate(String dbEnv, Date startDate, Date endDate) {
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
            list = criteria.list();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (null != trans) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return list;
    }

    public List<SysOperLog> getListSysOperLogByProd(String dbEnv, String prodUuid) {
        List<SysOperLog> list = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(SysOperLog.class);
            criteria.add(Restrictions.eq("productId", prodUuid));
            list = criteria.list();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (null != trans) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return list;
    }

    public List<SysOperLog> getListSysOperLogByTrans(String dbEnv, String trnId) {
        List<SysOperLog> list = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(SysOperLog.class);
            criteria.add(Restrictions.eq("trnId", trnId));
            criteria.addOrder(Order.desc("createAt"));
            list = criteria.list();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (null != trans) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return list;
    }

    public List<SysOperLog> searchSysOperLog(String dbEnv, SysOperLog sysOperLog, Date startDate, Date endDate, Date payStartDate, Date payEndDate) {
        List<SysOperLog> list = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(SysOperLog.class);
            if (null != sysOperLog.getProductCode() && !"".equals(sysOperLog.getProductCode())) {
                criteria.add(Restrictions.eq("productCode", sysOperLog.getProductCode()));
            }
            if (null != sysOperLog.getGroupProduct() && !"".equals(sysOperLog.getGroupProduct())) {
                criteria.add(Restrictions.eq("groupProduct", sysOperLog.getGroupProduct()));
            }
            if (null != sysOperLog.getRefNo() && !"".equals(sysOperLog.getRefNo())) {
                criteria.add(Restrictions.eq("refNo", sysOperLog.getRefNo()));
            }
            if (null != sysOperLog.getTrnStatus() && sysOperLog.getTrnStatus() > 0) {
                criteria.add(Restrictions.eq("trnStatus", sysOperLog.getTrnStatus()));
            }
            if (null != sysOperLog.getStateCode() && !"".equals(sysOperLog.getStateCode())) {
                criteria.add(Restrictions.eq("stateCode", sysOperLog.getStateCode()));
            }
            if (null != startDate && null != endDate) {
                criteria.add(Restrictions.between("createAt", DateUtils.utilDateToSqlDate(startDate), DateUtils.utilDateToSqlDate(DateUtils.addDate(endDate, 0, 0, 0, 23, 59, 59))));
            } else if (null == startDate && null != endDate) {
                criteria.add(Restrictions.lt("createAt", DateUtils.utilDateToSqlDate(DateUtils.addDate(endDate, 0, 0, 0, 23, 59, 59))));
            } else if (null != startDate && null == endDate) {
                criteria.add(Restrictions.gt("createAt", DateUtils.utilDateToSqlDate(startDate)));
            }
            if (null != payStartDate || null != payEndDate) {
                criteria.add(Restrictions.eq("keywords", "hpContact"));
            }
            list = criteria.list();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            if (null != trans) {
                trans.rollback();
            }
            throw e;
        }
        return list;
    }

    public List<SysLog> searchSysLog(String dbEnv, SysLog sysLog, Date startDate, Date endDate) {
        List<SysLog> list = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(SysLog.class);
            if (null != sysLog.getProdCode() && !"".equals(sysLog.getProdCode())) {
                criteria.add(Restrictions.eq("prodCode", sysLog.getProdCode()));
            }
            if (null != sysLog.getCaseId() && !"".equals(sysLog.getCaseId())) {
                criteria.add(Restrictions.eq("caseId", sysLog.getCaseId()));
            }
            if (null != sysLog.getGroupProduct() && !"".equals(sysLog.getGroupProduct())) {
                criteria.add(Restrictions.eq("groupProduct", sysLog.getGroupProduct()));
            }
            if (null != sysLog.getState() && !"".equals(sysLog.getState())) {
                criteria.add(Restrictions.eq("state", sysLog.getState()));
            }
            if (sysLog.getStatus() > 0) {
                criteria.add(Restrictions.eq("status", sysLog.getStatus()));
            }
            if (null != startDate && null != endDate) {
                criteria.add(Restrictions.between("createAt", DateUtils.utilDateToSqlDate(startDate), DateUtils.utilDateToSqlDate(DateUtils.addDate(endDate, 0, 0, 0, 23, 59, 59))));
            } else if (null == startDate && null != endDate) {
                criteria.add(Restrictions.lt("createAt", DateUtils.utilDateToSqlDate(DateUtils.addDate(endDate, 0, 0, 0, 23, 59, 59))));
            } else if (null != startDate && null == endDate) {
                criteria.add(Restrictions.gt("createAt", DateUtils.utilDateToSqlDate(startDate)));
            }
            list = criteria.list();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            logger.info(e.getMessage());
            if (null != trans) {
                trans.rollback();
            }
            throw e;
        }
        return list;
    }

    public List<SysOperLog> getListSysOperLogByCaseId(String dbEnv, String caseId) {
        List<SysOperLog> list = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(SysOperLog.class);
            criteria.add(Restrictions.eq("caseId", caseId));
            criteria.add(Restrictions.isNotNull("attr4"));
            criteria.addOrder(Order.desc("attr4"));
            criteria.setFirstResult(0);
            criteria.setMaxResults(1);
//            criteria.addOrder(Order.asc("createAt"));
            list = criteria.list();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (null != trans) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return list;
    }

    /*public JSONArray getReconcileReport(String dbEnv, String prodCode, String startDate, String endDate) {
        Connection con = null;
        JSONArray ret = new JSONArray();
        PreparedStatement ps = null, psOperLog = null;
        ResultSet rs = null, stepDataRs = null;
        try {
            session = getSessionMaster(dbEnv).openSession();
            con = session.doReturningWork((Connection conn) -> conn);
            StringBuilder reconcileCmd = new StringBuilder();
            List params = new ArrayList<>();
            reconcileCmd.append("select log.* , LK.LOOKUP_CODE ST_CODE , LK.LOOKUP_NAME_EN STATE_NAME_EN, LK.LOOKUP_NAME_TH STATE_NAME_TH, SL.LOOKUP_NAME_EN STATUS_NAME   "
                    + "from t_sys_oper_log log, T_SHELF_LOOKUP LK, T_SYS_LOOKUP SL , T_SHELF_PRODUCT SP "
                    + "WHERE log.uuid IN "
                    + "( "
                    + "        select uuid from ( "
                    + "                SELECT uuid, row_number() over (partition by txn_no order by CREATE_AT desc) as ROW_NO "
                    + "                        , MAX(CREATE_AT) OVER (PARTITION BY TXN_NO) MAX_DATE "
                    + "                        , MAX(PAYMENT_DATE) OVER (PARTITION BY TXN_NO) PAY_DATE "
                    + "            FROM t_sys_oper_log S "
                    + "        )A  "
                    + "        where A.ROW_NO = 1 "
                    + " ) "
                    + "AND LOG.PRODUCT_ID = SP.UUID  "
                    + "AND LOG.BUSINESS_DATE IS NOT NULL  "
                    + "AND LOG.STATE_CODE = LK.UUID  "
                    + "AND LOG.STATUS::TEXT = SL.LOOKUP_CODE  "
                    + "AND STATE_CODE IN (SELECT UUID FROM T_SHELF_LOOKUP WHERE GROUP_TYPE = 'PROCESS_STATE' AND LOOKUP_CODE IN ('PRO1013', 'PRO1014', 'PRO1015', 'PRO1016', 'PRO1017'))  ");
            if (null != prodCode && !prodCode.isEmpty()) {
                reconcileCmd.append(" AND PRODUCT_CODE = ? ");
                params.add(prodCode);
            }
            if ((null != startDate && !startDate.isEmpty()) && (null != endDate && !endDate.isEmpty())) {
                reconcileCmd.append(" AND BUSINESS_DATE BETWEEN TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS') AND TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(startDate + " 00:00:00");
                params.add(endDate + " 23:59:59");
            } else if ((null == startDate || startDate.isEmpty()) && (null != endDate && !endDate.isEmpty())) {
                reconcileCmd.append(" AND BUSINESS_DATE <= TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(endDate + " 23:59:59");
            } else if ((null != startDate && !startDate.isEmpty()) && (null == endDate || endDate.isEmpty())) {
                reconcileCmd.append(" AND BUSINESS_DATE >= TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(startDate + " 00:00:00");
            }
            reconcileCmd.append(" ORDER BY CREATE_AT ASC");
//            System.out.println("reconcile : " + reconcileCmd.toString());
            try {
                ps = con.prepareStatement(reconcileCmd.toString());
                if (params.size() > 0) {
                    for (int i = 0; i < params.size(); i++) {
                        ps.setString(i + 1, (String) params.get(i));
                    }
                }
                rs = ps.executeQuery();
                JSONObject logObj = new JSONObject();
//                JSONObject detail = new JSONObject();
//                JSONArray listDetail = new JSONArray();
//                JSONObject header = new JSONObject();
//                header.put("businessDate", "")
//                        .put("productCode", "")
//                        .put("productName", "")
//                        .put("productCutOffTime", "");
//                JSONObject stepData = new JSONObject();
//                JSONObject summaryDtl = new JSONObject();
                while (rs.next()) {
                    SysOperLog sysOperLog = new SysOperLog();
                    sysOperLog.setUuid(rs.getString("uuid"));
                    sysOperLog.setTrnId(rs.getString("trn_id"));
                    sysOperLog.setSource(rs.getString("source"));
                    sysOperLog.setProductId(rs.getString("product_id"));
                    sysOperLog.setProductCode(rs.getString("product_code"));
                    sysOperLog.setProductVersionId(rs.getString("product_version_id"));
                    sysOperLog.setProductComponentId(rs.getString("product_component_id"));
                    sysOperLog.setTaskCategory(rs.getString("task_category"));
                    sysOperLog.setKeywords(rs.getString("keywords"));
                    sysOperLog.setTrnStatus(rs.getInt("trn_status"));
                    sysOperLog.setTrnSubStatus(rs.getInt("trn_sub_status"));
                    sysOperLog.setFailureReason(rs.getString("failure_reason"));
                    sysOperLog.setSourceDevice(rs.getString("source_device"));
                    sysOperLog.setSourceDeviceId(rs.getString("source_device_id"));
                    sysOperLog.setSourceCifId(rs.getString("source_cif_id"));
                    sysOperLog.setAccountName(rs.getString("account_name"));
                    sysOperLog.setBusinessDate(rs.getTimestamp("business_date"));
                    sysOperLog.setRefNo(rs.getString("ref_no"));
                    sysOperLog.setPaymentMethod(rs.getString("payment_method"));
                    sysOperLog.setPaymentDate(rs.getTimestamp("payment_date")); //paymentdate
                    sysOperLog.setStateCode(new ShelfLookup(rs.getString("state_code")));
                    sysOperLog.setProdChannel(rs.getString("prod_channel"));
                    sysOperLog.setStepData(rs.getString("step_data"));
                    sysOperLog.setCaseId(rs.getString("case_id"));
                    sysOperLog.setGroupProduct(rs.getString("group_product"));
                    sysOperLog.setTxnNo(rs.getString("txn_no"));
                    sysOperLog.setAttr1(rs.getString("attr1"));
                    sysOperLog.setAttr2(rs.getString("attr2"));
                    sysOperLog.setAttr3(rs.getString("attr3"));
                    sysOperLog.setAttr4(rs.getString("attr4"));
                    sysOperLog.setAttr5(rs.getString("attr5"));
                    sysOperLog.setAttr6(rs.getString("attr6"));
                    sysOperLog.setAttr7(rs.getString("attr7"));
                    sysOperLog.setAttr8(rs.getString("attr8"));
                    sysOperLog.setAttr9(rs.getString("attr9"));
                    sysOperLog.setAttr10(rs.getString("attr10"));
                    sysOperLog.setStatus(rs.getInt("status"));
                    sysOperLog.setCreateAt(rs.getTimestamp("create_at"));
                    sysOperLog.setCreateBy(rs.getString("create_by"));
                    sysOperLog.setStateTime(ValidUtils.str2BigInt(rs.getInt("state_time") + ""));
                    reconcileCmd.setLength(0);
                    reconcileCmd.append("SELECT STEP_DATA, PAYMENT_METHOD FROM T_SYS_OPER_LOG WHERE TXN_NO = ? AND STATE_CODE = '71228c52-4322-11ea-b77f-2e728ce88125'");
                    psOperLog = con.prepareStatement(reconcileCmd.toString());
                    psOperLog.setString(1, sysOperLog.getTxnNo());
                    stepDataRs = psOperLog.executeQuery();
                    JSONObject stepData = new JSONObject();
                    stepData.put("paymentMethod", sysOperLog.getPaymentMethod());
                    JSONArray packageDataArr = new JSONArray();
                    while (stepDataRs.next()) {
                        byte[] decoded = Base64.decodeBase64(stepDataRs.getString("STEP_DATA"));
                        JSONArray stepDataArray = new JSONArray(new String(decoded, "UTF-8"));
                        for (int i = 0; i < stepDataArray.length(); i++) {
                            JSONObject stepDataObj = stepDataArray.getJSONObject(i);
                            if (stepDataObj.getJSONObject("data").has("package")) {
                                packageDataArr.put(stepDataObj);
                            }
                            if (stepDataObj.has("code") && stepDataObj.getString("code").equalsIgnoreCase("loan")) {
                                if (stepDataObj.getJSONObject("data").has("package")) {
                                    for (int j = 0; j < stepDataObj.getJSONObject("data").getJSONArray("package").length(); j++) {
                                        JSONObject packageData = stepDataObj.getJSONObject("data").getJSONArray("package").getJSONObject(j);
                                        if (packageData.has("parameter")) {
                                            switch (packageData.getString("parameter")) {
                                                case "limit":
                                                    stepData.put("requestLimit", packageData.getString("value"));
                                                    break;
                                                case "factor_2_from":
                                                    stepData.put("month", packageData.getInt("value") + "");
                                                    break;
                                                case "calAmount1":
                                                    stepData.put("installmentPerMonth", packageData.getString("value"));
                                                    break;
                                                case "rateAmount":
                                                    stepData.put("interestRate", packageData.getDouble("value") + "");
                                                    break;
                                                case "paymentDate":
                                                    stepData.put("paymentDate", packageData.getString("value"));
                                                    break;
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    }
                    if (logObj.has(DateUtils.getDisplayEnDate(sysOperLog.getBusinessDate(), "dd-MM-yyyy"))) {
                        JSONObject obj = new JSONObject();
                        JSONObject detail = logObj.getJSONObject(DateUtils.getDisplayEnDate(sysOperLog.getBusinessDate(), "dd-MM-yyyy"));
                        obj.put("txnsId", ValidUtils.null2NoData(sysOperLog.getTxnNo()))
                                .put("refNo", sysOperLog.getRefNo())
                                .put("txnsDateTime", DateUtils.getDisplayEnDate(sysOperLog.getCreateAt(), "dd/MM/yyyy HH:mm"))
                                .put("paymentDate", stepData.has("paymentDate") ? stepData.getString("paymentDate") : "")
                                .put("requestLimit", stepData.has("requestLimit") ? stepData.getString("requestLimit") : "")
                                .put("month", stepData.has("month") ? stepData.getString("month") : "")
                                .put("installmentPerMonth", stepData.has("installmentPerMonth") ? stepData.getString("installmentPerMonth") : "")
                                .put("interestRate", stepData.has("interestRate") ? stepData.getString("interestRate") : "")
                                .put("paymentMethod", stepData.has("paymentMethod") ? stepData.getString("paymentMethod") : "")
                                .put("status", rs.getString("STATUS_NAME"))
                                .put("state", rs.getString("STATE_NAME_TH"));
                        reconcileCmd.setLength(0);
                        reconcileCmd.append("SELECT LK_CODE,LK_LABEL,LK_VALUE FROM T_SHELF_PRODUCT_DTL WHERE 1=1 "
                                + " AND TRN_UUID IN (SELECT UUID FROM T_SHELF_PRODUCT_VCS VCS WHERE VCS.PROD_UUID = ? AND VER_PROD = ? )"
                                + " AND LK_CODE IN ('cutOffTime','pcutOffDay','pcutOffWE','pcutOffTime','pcutOffTAllD','pcutOffTSpec', 'prodName', 'prodType')");
                        psOperLog = con.prepareStatement(reconcileCmd.toString());
                        psOperLog.setString(1, sysOperLog.getProductId());
                        psOperLog.setInt(2, ValidUtils.str2BigInteger(sysOperLog.getProductVersionId()));
                        stepDataRs = psOperLog.executeQuery();
                        while (stepDataRs.next()) {
                            if (stepDataRs.getString("LK_CODE").equalsIgnoreCase("prodType")) {
                                obj.put("productType", stepDataRs.getString("LK_VALUE"));
                            }
//                            header.put("businessDate", DateUtils.getDisplayEnDate(sysOperLog.getBusinessDate(), "dd-MM-yyyy"));
                        }
                        if (rs.getString("ST_CODE").equalsIgnoreCase("PRO1013")) {//Prospect
                            JSONObject prospect = detail.getJSONObject("summary").getJSONObject("prospect");
                            prospect.put("noOfTxns", prospect.getInt("noOfTxns") + 1)
                                    .put("limitAmount", ValidUtils.priceToString(
                                            ((prospect.has("limitAmount") ? ValidUtils.obj2Double(prospect.getString("limitAmount")) : 0.0) + ValidUtils.obj2Double(stepData.has("requestLimit") ? stepData.getString("requestLimit") : "0.00"))
                                    //                                            / summary.getInt("noOfTxns")
                                    ));
                        } else if (rs.getString("ST_CODE").equalsIgnoreCase("PRO1014")) {//Wait for Book Loan
                            JSONObject waitForBookLoan = detail.getJSONObject("summary").getJSONObject("waitForBookLoan");
                            waitForBookLoan.put("noOfTxns", waitForBookLoan.getInt("noOfTxns") + 1)
                                    .put("limitAmount", ValidUtils.priceToString(
                                            ((waitForBookLoan.has("limitAmount") ? ValidUtils.obj2Double(waitForBookLoan.getString("limitAmount")) : 0.0) + ValidUtils.obj2Double(stepData.has("requestLimit") ? stepData.getString("requestLimit") : "0.00"))
                                    //                                            / summary.getInt("noOfTxns")
                                    ));
                        } else if (rs.getString("ST_CODE").equalsIgnoreCase("PRO1015")) {//Wait for Transfer
                            JSONObject waitForTransfer = detail.getJSONObject("summary").getJSONObject("waitForTransfer");
                            waitForTransfer.put("noOfTxns", waitForTransfer.getInt("noOfTxns") + 1)
                                    .put("limitAmount", ValidUtils.priceToString(
                                            ((waitForTransfer.has("limitAmount") ? ValidUtils.obj2Double(waitForTransfer.getString("limitAmount")) : 0.0) + ValidUtils.obj2Double(stepData.has("requestLimit") ? stepData.getString("requestLimit") : "0.00"))
                                    //                                            / summary.getInt("noOfTxns")
                                    ));
                        } else if (rs.getString("ST_CODE").equalsIgnoreCase("PRO1016")) {//Transfer Fail
                            JSONObject transferFail = detail.getJSONObject("summary").getJSONObject("transferFail");
                            transferFail.put("noOfTxns", transferFail.getInt("noOfTxns") + 1)
                                    .put("limitAmount", ValidUtils.priceToString(
                                            ((transferFail.has("limitAmount") ? ValidUtils.obj2Double(transferFail.getString("limitAmount")) : 0.0) + ValidUtils.obj2Double(stepData.has("requestLimit") ? stepData.getString("requestLimit") : "0.00"))
                                    //                                            / summary.getInt("noOfTxns")
                                    ));
                        } else {//Completed
                            JSONObject transferComplete = detail.getJSONObject("summary").getJSONObject("transferComplete");
                            transferComplete.put("noOfTxns", transferComplete.getInt("noOfTxns") + 1)
                                    .put("limitAmount", ValidUtils.priceToString(
                                            ((transferComplete.has("limitAmount") ? ValidUtils.obj2Double(transferComplete.getString("limitAmount")) : 0.0) + ValidUtils.obj2Double(stepData.has("requestLimit") ? stepData.getString("requestLimit") : "0.00"))
                                    //                                            / summary.getInt("noOfTxns")
                                    ));
                        }
                        detail.getJSONArray("detail").put(obj);
                    } else {
                        JSONArray listDetail = new JSONArray();
                        JSONObject detail = new JSONObject();
                        JSONObject obj = new JSONObject();
                        JSONObject header = new JSONObject();
                        header.put("businessDate", "")
                                .put("productCode", "")
                                .put("productName", "")
                                .put("productCutOffTime", "");
                        obj.put("txnsId", ValidUtils.null2NoData(sysOperLog.getTxnNo()))
                                .put("refNo", sysOperLog.getRefNo())
                                .put("txnsDateTime", DateUtils.getDisplayEnDate(sysOperLog.getCreateAt(), "dd/MM/yyyy HH:mm"))
                                .put("paymentDate", stepData.has("paymentDate") ? stepData.getString("paymentDate") : "")
                                .put("requestLimit", stepData.has("requestLimit") ? stepData.getString("requestLimit") : "")
                                .put("month", stepData.has("month") ? stepData.getString("month") : "")
                                .put("installmentPerMonth", stepData.has("installmentPerMonth") ? stepData.getString("installmentPerMonth") : "")
                                .put("interestRate", stepData.has("interestRate") ? stepData.getString("interestRate") : "")
                                //                                .put("productType", sysOperLog.getProductId())//getFrom product
                                .put("paymentMethod", stepData.has("paymentMethod") ? stepData.getString("paymentMethod") : "")
                                .put("status", rs.getString("STATUS_NAME"))
                                .put("state", rs.getString("STATE_NAME_TH"));
                        reconcileCmd.setLength(0);
                        reconcileCmd.append("SELECT LK_CODE,LK_LABEL,LK_VALUE FROM T_SHELF_PRODUCT_DTL WHERE 1=1 "
                                + " AND TRN_UUID IN (SELECT UUID FROM T_SHELF_PRODUCT_VCS VCS WHERE VCS.PROD_UUID = ? AND VER_PROD = ? )"
                                + " AND LK_CODE IN ('cutOffTime','pcutOffDay','pcutOffWE','pcutOffTime','pcutOffTAllD','pcutOffTSpec', 'prodName', 'prodType')");
                        psOperLog = con.prepareStatement(reconcileCmd.toString());
                        psOperLog.setString(1, sysOperLog.getProductId());
                        psOperLog.setInt(2, ValidUtils.str2BigInteger(sysOperLog.getProductVersionId()));
                        stepDataRs = psOperLog.executeQuery();
                        while (stepDataRs.next()) {
                            if (stepDataRs.getString("LK_CODE").equalsIgnoreCase("prodName")) {
                                header.put("productName", stepDataRs.getString("LK_VALUE"));
                            } else if (stepDataRs.getString("LK_CODE").equalsIgnoreCase("pcutOffTSpec") || stepDataRs.getString("LK_CODE").equalsIgnoreCase("pcutOffTAllD")) {
                                if (!stepDataRs.getString("LK_VALUE").isEmpty()) {
                                    header.put("productCutOffTime", stepDataRs.getString("LK_VALUE"));
                                }
                            } else if (stepDataRs.getString("LK_CODE").equalsIgnoreCase("prodType")) {
                                obj.put("productType", stepDataRs.getString("LK_VALUE"));
                            }
                            header.put("businessDate", DateUtils.getDisplayEnDate(sysOperLog.getBusinessDate(), "dd-MM-yyyy"));
                        }
                        header.put("productCode", sysOperLog.getProductCode());
                        JSONObject summary = new JSONObject();
                        summary.put("prospect", new JSONObject().put("txnsStatus", "Prospect").put("noOfTxns", 0).put("limitAmount", "0.0"));
                        summary.put("waitForBookLoan", new JSONObject().put("txnsStatus", "Wait for Book Loan").put("noOfTxns", 0).put("limitAmount", "0.0"));
                        summary.put("waitForTransfer", new JSONObject().put("txnsStatus", "Wait for Transfer").put("noOfTxns", 0).put("limitAmount", "0.0"));
                        summary.put("transferFail", new JSONObject().put("txnsStatus", "Transfer Fail").put("noOfTxns", 0).put("limitAmount", "0.0"));
                        summary.put("transferComplete", new JSONObject().put("txnsStatus", "Transfer Complete").put("noOfTxns", 0).put("limitAmount", "0.0"));
                        if (rs.getString("ST_CODE").equalsIgnoreCase("PRO1013")) {//Prospect
                            summary.put("prospect", new JSONObject().put("txnsStatus", "Prospect").put("noOfTxns", 1).put("limitAmount", stepData.has("requestLimit") ? stepData.getString("requestLimit") : "0.0"));
                        } else if (rs.getString("ST_CODE").equalsIgnoreCase("PRO1014")) {//Wait for Book Loan
                            summary.put("waitForBookLoan", new JSONObject().put("txnsStatus", "Wait for Book Loan").put("noOfTxns", 1).put("limitAmount", stepData.has("requestLimit") ? stepData.getString("requestLimit") : "0.0"));
                        } else if (rs.getString("ST_CODE").equalsIgnoreCase("PRO1015")) {//Wait for Transfer
                            summary.put("waitForTransfer", new JSONObject().put("txnsStatus", "Wait for Transfer").put("noOfTxns", 1).put("limitAmount", stepData.has("requestLimit") ? stepData.getString("requestLimit") : "0.0"));
                        } else if (rs.getString("ST_CODE").equalsIgnoreCase("PRO1016")) {//Transfer Fail
                            summary.put("transferFail", new JSONObject().put("txnsStatus", "Transfer Fail").put("noOfTxns", 1).put("limitAmount", stepData.has("requestLimit") ? stepData.getString("requestLimit") : "0.0"));
                        } else {//Completed
                            summary.put("transferComplete", new JSONObject().put("txnsStatus", "Transfer Complete").put("noOfTxns", 1).put("limitAmount", stepData.has("requestLimit") ? stepData.getString("requestLimit") : "0.0"));
                        }
                        obj.put("package", packageDataArr);
                        listDetail.put(obj);
                        detail.put("detail", listDetail);
                        detail.put("summary", summary);
                        detail.put("header", header);
                        logObj.put(DateUtils.getDisplayEnDate(sysOperLog.getBusinessDate(), "dd-MM-yyyy"), detail);
                    }
                }
                ret.put(logObj);
            } catch (UnsupportedEncodingException ex) {
                logger.info(ex.getMessage());
            }
//            con.commit();
//            stepDataRs.close();
//            rs.close();
//            ps.close();
        } catch (SQLException | HibernateException | NullPointerException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
        } finally {
            try {
                if (stepDataRs != null) {
                    stepDataRs.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (NullPointerException | SQLException ex) {
                logger.info(ex.getMessage());
            }
            if (null != session) {
                session.close();
            }
//                if (!connection.isClosed()) {
//                    connection.close();
//                }
        }
        return ret;
    }*/
    public JSONArray getReconcileReport(String dbEnv, String prodCode, String startDate, String endDate) throws SQLException, ParseException, UnsupportedEncodingException {
        JSONArray ret = new JSONArray();
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            if ((null != startDate && !startDate.isEmpty()) && (null != endDate && !endDate.isEmpty())) {
                try {
                    Date sDate = sdf.parse(startDate);
                    Date eDate = sdf.parse(endDate);
                    int compareDate = sDate.compareTo(eDate);
                    while (compareDate <= 0) {
                        ret = queryReconcileReport(session, prodCode, sdf.format(sDate), ret, "");
                        Calendar c = Calendar.getInstance();
                        c.setTime(sDate);
                        c.add(Calendar.DATE, 1);
                        sDate = c.getTime();
                        compareDate = sDate.compareTo(eDate);
                    }
                } catch (ParseException ex) {
                    logger.info(ex.getMessage());
                }
            } else if ((null == startDate || startDate.isEmpty()) && (null != endDate && !endDate.isEmpty())) {
                ret = queryReconcileReport(session, prodCode, endDate, ret, "end");
            } else if ((null != startDate && !startDate.isEmpty()) && (null == endDate || endDate.isEmpty())) {
                ret = queryReconcileReport(session, prodCode, startDate, ret, "start");
            }
        } catch (HibernateException | NullPointerException e) {
            logger.info(e.getMessage());
            throw e;
        }
        return ret;
    }

    public JSONObject getTransactionReport(String dbEnv, String prodCode, String txnId, String refNo, String ucId, String paymentMethod, String startDate, String endDate, String startTime, String endTime, Integer status, String state, String refTxnId, int offSet) throws SQLException, UnsupportedEncodingException, ParseException {
        JSONObject ret = new JSONObject();
        PreparedStatement ps = null, psOperLog = null;
        ResultSet rs = null, stepDataRs = null;
        String cutOff = "";
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            StringBuilder transactionCmd = new StringBuilder();
            List params = new ArrayList<>();
            transactionCmd.append("select log.*, LK.LOOKUP_CODE ST_CODE, LK.LOOKUP_NAME_EN STATE_NAME, LK.LOOKUP_NAME_TH , SL.LOOKUP_NAME_EN STATUS_NAME ,LOG.ATTR2 AS PROCESS_ERROR  ,LK2.LOOKUP_NAME_TH AS PROCESS_ERROR_NAME_TH ,LK2.LOOKUP_NAME_EN AS PROCESS_ERROR_NAME_EN , LK2.DESCRIPTION PROCESS_ERROR_DESC "
                    + "from t_sys_oper_log LOG "
                    + "LEFT JOIN T_SHELF_LOOKUP LK2 ON LOG.ATTR2 = LK2.LOOKUP_CODE "
                    + "INNER JOIN T_SHELF_LOOKUP LK ON LOG.STATE_CODE = LK.UUID "
                    + "INNER JOIN T_SYS_LOOKUP SL ON LOG.STATUS::TEXT = SL.LOOKUP_CODE "
                    + "INNER JOIN T_SYS_LOOKUP SL2 ON LOG.TRN_STATUS::TEXT = SL2.LOOKUP_CODE "
                    + "INNER JOIN T_SHELF_PRODUCT SP ON LOG.PRODUCT_ID = SP.UUID "
                    + "WHERE log.uuid IN "
                    + "( "
                    + "         select uuid from ("
                    + "   select uuid, row_number() over (partition by txn_no order by TO_TIMESTAMP(log.ATTR4, 'yyyy-MM-dd HH24:MI:SS') desc, log.CREATE_AT desc) as ROW_NO "
                    + "   from t_sys_oper_log log "
                    + "        )A "
                    + "        where A.ROW_NO = 1 "
                    //                    + "        select uuid from ( "
                    //                    + "                SELECT uuid, row_number() over (partition by txn_no order by CREATE_AT desc) as ROW_NO "
                    //                    + "                        , MAX(CREATE_AT) OVER (PARTITION BY TXN_NO) MAX_DATE "
                    //                    + "                        , MAX(PAYMENT_DATE) OVER (PARTITION BY TXN_NO) PAY_DATE "
                    //                    + "                        , MAX(TO_TIMESTAMP(ATTR4, 'yyyy-MM-dd HH24:MI:SS')) OVER (PARTITION BY TXN_NO) UPDATE_DATE "
                    //                    + "            FROM t_sys_oper_log S "
                    //                    + "        )A  "
                    //                    + "        where A.ROW_NO = 1 "
                    + " ) ");
            if (null != prodCode && !prodCode.isEmpty()) {
                transactionCmd.append(" AND PRODUCT_CODE = ? ");
                params.add(prodCode);
            }
            if (null != txnId && !txnId.isEmpty()) {
                transactionCmd.append(" AND LOWER(LOG.TXN_NO) LIKE ? ");
                params.add("%" + txnId.toLowerCase() + "%");
            }
            if (null != refNo && !refNo.isEmpty()) {
                transactionCmd.append(" AND LOG.REF_NO LIKE ? ");
                params.add("%" + refNo + "%");
            }
            if (null != ucId && !ucId.isEmpty()) {
//                transactionCmd.append(" AND LOG.ATTR1 = ? ");
                String id[] = ucId.replace("(", "").replace(")", "").replace("'", "").split(",");
                StringBuilder subCmd = new StringBuilder();
                subCmd.append(" AND LOG.ATTR1 in ( ? ");

                for (int i = 1; i < id.length; i++) {
                    subCmd.append(", ? ");
                }
                subCmd.append(") ");
                for (int i = 0; i < id.length; i++) {
                    params.add(id[i]);
                }
                transactionCmd.append(subCmd);
            }
            if (null != refTxnId && !refTxnId.isEmpty()) {
//                inquiryCmd.append(" AND LOG.ATTR1 = ? ");
                String id[] = refTxnId.replace("(", "").replace(")", "").replace("'", "").split(",");
//                transactionCmd.append(" AND LOG.ATTR1 = ? ");
                StringBuilder subCmd = new StringBuilder();
                subCmd.append(" AND LOWER(LOG.TXN_NO) in ( ? ");
                for (int i = 1; i < id.length; i++) {
                    subCmd.append(", ? ");
                }
                subCmd.append(") ");
                for (int i = 0; i < id.length; i++) {
                    params.add(id[i].toLowerCase());
                }
                transactionCmd.append(subCmd);
            }
            if (null != paymentMethod && !paymentMethod.isEmpty()) {
                transactionCmd.append(" AND LOG.PAYMENT_METHOD = ? ");
                params.add(paymentMethod);
            }
            if (null != startTime && !startTime.isEmpty()) {
                startTime += ":00";
            } else {
                startTime = "00:00:00";
            }
            if (null != endTime && !endTime.isEmpty()) {
                endTime += ":59";
            } else {
                endTime = "23:59:59";
            }
            if ((null != startDate && !startDate.isEmpty()) && (null != endDate && !endDate.isEmpty())) {
                transactionCmd.append(" AND LOG.CREATE_AT BETWEEN TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS') AND TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(startDate + " " + startTime);
                params.add(endDate + " " + endTime);
            } else if ((null == startDate || startDate.isEmpty()) && (null != endDate && !endDate.isEmpty())) {
                transactionCmd.append(" AND LOG.CREATE_AT <= TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(endDate + " " + endTime);
            } else if ((null != startDate && !startDate.isEmpty()) && (null == endDate || endDate.isEmpty())) {
                transactionCmd.append(" AND LOG.CREATE_AT >= TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(startDate + " " + startTime);
            }
            if (null != status) {
                transactionCmd.append(" AND LOG.STATUS = ? ");
                params.add(status);
            }
            if (null != state && !state.isEmpty()) {
                transactionCmd.append(" AND LK.LOOKUP_CODE = ? ");
                params.add(state);
            }
            transactionCmd.append(" ORDER BY LOG.CREATE_AT ASC");
            transactionCmd.append(" LIMIT 10 OFFSET ? ");
            params.add(offSet);

            System.out.println("transactionCmd.toString() : " + transactionCmd.toString());

            ps = session.doReturningWork((Connection conn) -> conn).prepareStatement(transactionCmd.toString());
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
//                JSONObject logObj = new JSONObject();
//                JSONObject detail = new JSONObject();
            JSONArray listDetail = new JSONArray();
//                JSONObject summary = new JSONObject();
            JSONObject header = new JSONObject();
//                JSONObject stepData = new JSONObject();
//                JSONObject summaryDtl = new JSONObject();
            while (rs.next()) {
                SysOperLog sysOperLog = new SysOperLog();
                sysOperLog.setUuid(rs.getString("uuid"));
                sysOperLog.setTrnId(rs.getString("trn_id"));
                sysOperLog.setSource(rs.getString("source"));
                sysOperLog.setProductId(rs.getString("product_id"));
                sysOperLog.setProductCode(rs.getString("product_code"));
                sysOperLog.setProductVersionId(rs.getString("product_version_id"));
                sysOperLog.setProductComponentId(rs.getString("product_component_id"));
                sysOperLog.setTaskCategory(rs.getString("task_category"));
                sysOperLog.setKeywords(rs.getString("keywords"));
                sysOperLog.setTrnStatus(rs.getInt("trn_status"));
                sysOperLog.setTrnSubStatus(rs.getInt("trn_sub_status"));
                sysOperLog.setFailureReason(rs.getString("failure_reason"));
                sysOperLog.setSourceDevice(rs.getString("source_device"));
                sysOperLog.setSourceDeviceId(rs.getString("source_device_id"));
                sysOperLog.setSourceCifId(rs.getString("source_cif_id"));
                sysOperLog.setAccountName(rs.getString("account_name"));
                sysOperLog.setBusinessDate(rs.getTimestamp("business_date"));
                sysOperLog.setRefNo(rs.getString("ref_no"));
                sysOperLog.setPaymentMethod(rs.getString("payment_method"));
                sysOperLog.setPaymentDate(rs.getTimestamp("payment_date"));//paymentDate
                sysOperLog.setStateCode(new ShelfLookup(rs.getString("state_code")));
                sysOperLog.setProdChannel(rs.getString("prod_channel"));
                sysOperLog.setStepData(rs.getString("step_data"));
                sysOperLog.setCaseId(rs.getString("case_id"));
                sysOperLog.setGroupProduct(rs.getString("group_product"));
                sysOperLog.setTxnNo(rs.getString("txn_no"));
                sysOperLog.setAttr1(rs.getString("attr1"));
                sysOperLog.setAttr2(rs.getString("attr2"));
                sysOperLog.setAttr3(rs.getString("attr3"));
                sysOperLog.setAttr4(rs.getString("attr4"));
                sysOperLog.setAttr5(rs.getString("attr5"));
                sysOperLog.setAttr6(rs.getString("attr6"));
                sysOperLog.setAttr7(rs.getString("attr7"));
                sysOperLog.setAttr8(rs.getString("attr8"));
                sysOperLog.setAttr9(rs.getString("attr9"));
                sysOperLog.setAttr10(rs.getString("attr10"));
                String procError = ValidUtils.null2NoData(rs.getString("PROCESS_ERROR"));
                String procErrorNameTh = ValidUtils.null2NoData(rs.getString("PROCESS_ERROR_NAME_TH"));
                String procErrorNameEn = ValidUtils.null2NoData(rs.getString("PROCESS_ERROR_NAME_EN"));
                String procErrorDesc = ValidUtils.null2NoData(rs.getString("PROCESS_ERROR_DESC"));
                sysOperLog.setStatus(rs.getInt("status"));
                sysOperLog.setCreateAt(rs.getTimestamp("create_at"));
                sysOperLog.setCreateBy(rs.getString("create_by"));
                sysOperLog.setStateTime(ValidUtils.str2BigInt(rs.getInt("state_time") + ""));
                transactionCmd.setLength(0);
//                    transactionCmd.append("SELECT STEP_DATA, PAYMENT_METHOD FROM T_SYS_OPER_LOG WHERE TXN_NO = ? AND STATE_CODE = '71228c52-4322-11ea-b77f-2e728ce88125'"); //PRO1011
//                transactionCmd.append("SELECT STEP_DATA, PAYMENT_METHOD FROM T_SYS_OPER_LOG WHERE TXN_NO = ? AND STATE_CODE = (SELECT UUID FROM T_SHELF_LOOKUP WHERE GROUP_TYPE = 'PROCESS_STATE' AND LOOKUP_CODE = 'PRO1011')"); //PRO1011
                transactionCmd.append("SELECT STEP_DATA, PAYMENT_METHOD FROM T_SYS_OPER_LOG WHERE TXN_NO = ? AND STATE_CODE in (SELECT UUID FROM T_SHELF_LOOKUP WHERE GROUP_TYPE = 'PROCESS_STATE' AND LOOKUP_CODE in ('PRO1011','PRO1032'))"); //PRO1011
                psOperLog = session.doReturningWork((Connection conn) -> conn).prepareStatement(transactionCmd.toString());
                psOperLog.setString(1, sysOperLog.getTxnNo());
                stepDataRs = psOperLog.executeQuery();
                JSONObject stepData = new JSONObject();
                stepData.put("paymentMethod", sysOperLog.getPaymentMethod());
                JSONArray packageDataArr = new JSONArray();
                while (stepDataRs.next()) {
                    byte[] decoded = Base64.decodeBase64(stepDataRs.getString("STEP_DATA"));
                    String stapDataIn = new String(decoded, "UTF-8");
                    if (!stapDataIn.isEmpty()) {
                        JSONArray stepDataArray = new JSONArray(stapDataIn);
                        for (int i = 0; i < stepDataArray.length(); i++) {
                            JSONObject stepDataObj = stepDataArray.getJSONObject(i);
                            if (stepDataObj.getJSONObject("data").has("package")) {
                                packageDataArr.put(stepDataObj);
                            }
                            if (stepDataObj.has("code") && stepDataObj.getString("code").equalsIgnoreCase("loan")) {
                                if (stepDataObj.getJSONObject("data").has("package")) {
                                    for (int j = 0; j < stepDataObj.getJSONObject("data").getJSONArray("package").length(); j++) {
                                        JSONObject packageData = stepDataObj.getJSONObject("data").getJSONArray("package").getJSONObject(j);
                                        if (packageData.has("parameter")) {
                                            switch (packageData.getString("parameter")) {
                                                case "limit":
                                                    stepData.put("requestLimit", packageData.getString("value"));
                                                    break;
                                                case "factor_2_from":
                                                    stepData.put("month", packageData.getInt("value") + "");
                                                    break;
                                                case "calAmount1":
                                                    stepData.put("installmentPerMonth", packageData.getString("value"));
                                                    break;
                                                case "rateAmount":
                                                    stepData.put("interestRate", packageData.getDouble("value") + "");
                                                    break;
                                                case "paymentDate":
                                                    stepData.put("paymentDate", packageData.getString("value"));
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
                if (!stepDataRs.isClosed()) {
                    stepDataRs.close();
                }
                if (!psOperLog.isClosed()) {
                    psOperLog.close();
                }
                if (ret.has("detail")) {
                    JSONObject obj = new JSONObject();
                    obj.put("txnsId", ValidUtils.null2NoData(sysOperLog.getTxnNo()))
                            .put("refNo", sysOperLog.getRefNo())
                            .put("txnsDateTime", DateUtils.getDisplayEnDate(sysOperLog.getCreateAt(), "dd/MM/yyyy HH:mm"))
                            .put("businessDate", ValidUtils.null2NoData(DateUtils.getDisplayEnDate(sysOperLog.getBusinessDate(), "dd-MM-yyyy")))
                            //                                .put("paymentDate", stepData.has("paymentDate") ? stepData.getString("paymentDate") : "")

                            .put("installmentDate", stepData.has("paymentDate") ? stepData.getString("paymentDate") : "")
                            .put("paymentDate", ValidUtils.null2NoData(DateUtils.getDisplayEnDate(sysOperLog.getPaymentDate(), "dd/MM/yyyy HH:mm")))
                            .put("requestLimit", stepData.has("requestLimit") ? stepData.getString("requestLimit") : "")
                            .put("month", stepData.has("month") ? stepData.getString("month") : "")
                            .put("installmentPerMonth", stepData.has("installmentPerMonth") ? stepData.getString("installmentPerMonth") : "")
                            .put("interestRate", stepData.has("interestRate") ? stepData.getString("interestRate") : "")
                            .put("paymentMethod", stepData.has("paymentMethod") ? stepData.getString("paymentMethod") : "")
                            .put("status", rs.getString("STATUS_NAME"))
                            .put("state", rs.getString("STATE_NAME"))
                            .put("procError", procError)
                            .put("procErrorNameTh", procErrorNameTh)
                            .put("procErrorNameEn", procErrorNameEn)
                            .put("procErrorDesc", procErrorDesc)
                            .put("updatedDate", DateUtils.getDisplayEnDate(ValidUtils.str2Date(sysOperLog.getAttr4(), "yyyy-MM-dd HH:mm:ss"), "dd/MM/yyyy HH:mm"))
                            .put("ucId", ValidUtils.null2NoData(sysOperLog.getAttr1()));
                    obj.put("package", packageDataArr);
                    transactionCmd.setLength(0);
                    transactionCmd.append("SELECT LK_CODE,LK_LABEL,LK_VALUE FROM T_SHELF_PRODUCT_DTL WHERE 1=1 "
                            + " AND TRN_UUID IN (SELECT UUID FROM T_SHELF_PRODUCT_VCS VCS WHERE VCS.PROD_UUID = ? AND VER_PROD = ? )"
                            + " AND LK_CODE IN ('cutOffTime','pcutOffDay','pcutOffWE','pcutOffTime','pcutOffTAllD','pcutOffTSpec', 'prodName', 'prodType')");
                    psOperLog = session.doReturningWork((Connection conn) -> conn).prepareStatement(transactionCmd.toString());
                    psOperLog.setString(1, sysOperLog.getProductId());
                    psOperLog.setInt(2, ValidUtils.str2BigInteger(sysOperLog.getProductVersionId()));
                    stepDataRs = psOperLog.executeQuery();
                    while (stepDataRs.next()) {
                        if (stepDataRs.getString("LK_CODE").equalsIgnoreCase("prodType")) {
                            obj.put("productType", stepDataRs.getString("LK_VALUE"));
                        }
                    }
                    ret.getJSONArray("detail").put(obj);
                    if (!stepDataRs.isClosed()) {
                        stepDataRs.close();
                    }
                    if (!psOperLog.isClosed()) {
                        psOperLog.close();
                    }
                } else {
                    JSONObject obj = new JSONObject();
                    obj.put("txnsId", ValidUtils.null2NoData(sysOperLog.getTxnNo()))
                            .put("refNo", sysOperLog.getRefNo())
                            .put("txnsDateTime", DateUtils.getDisplayEnDate(sysOperLog.getCreateAt(), "dd/MM/yyyy HH:mm"))
                            .put("businessDate", ValidUtils.null2NoData(DateUtils.getDisplayEnDate(sysOperLog.getBusinessDate(), "dd-MM-yyyy")))
                            //                                .put("paymentDate", stepData.has("paymentDate") ? stepData.getString("paymentDate") : "")
                            .put("installmentDate", stepData.has("paymentDate") ? stepData.getString("paymentDate") : "")
                            .put("paymentDate", ValidUtils.null2NoData(DateUtils.getDisplayEnDate(sysOperLog.getPaymentDate(), "dd/MM/yyyy HH:mm")))
                            .put("requestLimit", stepData.has("requestLimit") ? stepData.getString("requestLimit") : "")
                            .put("month", stepData.has("month") ? stepData.getString("month") : "")
                            .put("installmentPerMonth", stepData.has("installmentPerMonth") ? stepData.getString("installmentPerMonth") : "")
                            .put("interestRate", stepData.has("interestRate") ? stepData.getString("interestRate") : "")
                            //                                .put("productType", sysOperLog.getProductId())//getFrom product
                            .put("paymentMethod", stepData.has("paymentMethod") ? stepData.getString("paymentMethod") : "")
                            .put("status", rs.getString("STATUS_NAME"))
                            .put("state", rs.getString("STATE_NAME"))
                            .put("procError", procError)
                            .put("procErrorNameTh", procErrorNameTh)
                            .put("procErrorNameEn", procErrorNameEn)
                            .put("procErrorDesc", procErrorDesc)
                            .put("updatedDate", DateUtils.getDisplayEnDate(ValidUtils.str2Date(sysOperLog.getAttr4(), "yyyy-MM-dd HH:mm:ss"), "dd/MM/yyyy HH:mm"))
                            .put("ucId", ValidUtils.null2NoData(sysOperLog.getAttr1()));
                    obj.put("package", packageDataArr);
                    if (null != prodCode && !prodCode.isEmpty()) {
                        transactionCmd.setLength(0);
                        transactionCmd.append("SELECT LK_CODE,LK_LABEL,LK_VALUE FROM T_SHELF_PRODUCT_DTL WHERE 1=1 "
                                + " AND TRN_UUID IN (SELECT UUID FROM T_SHELF_PRODUCT_VCS VCS WHERE VCS.PROD_UUID = ? AND VER_PROD = ? )"
                                + " AND LK_CODE IN ('cutOffTime','pcutOffDay','pcutOffWE','pcutOffTime','pcutOffTAllD','pcutOffTSpec', 'prodName', 'prodType')");
                        psOperLog = session.doReturningWork((Connection conn) -> conn).prepareStatement(transactionCmd.toString());
                        psOperLog.setString(1, sysOperLog.getProductId());
                        psOperLog.setInt(2, ValidUtils.str2BigInteger(sysOperLog.getProductVersionId()));
                        stepDataRs = psOperLog.executeQuery();
                        while (stepDataRs.next()) {
                            if (stepDataRs.getString("LK_CODE").equalsIgnoreCase("prodName")) {
                                header.put("productName", stepDataRs.getString("LK_VALUE"));
                            } else if (stepDataRs.getString("LK_CODE").equalsIgnoreCase("pcutOffTSpec") || stepDataRs.getString("LK_CODE").equalsIgnoreCase("pcutOffTAllD")) {
                                if (stepDataRs.getString("LK_CODE").equalsIgnoreCase("pcutOffTAllD")) {
                                    cutOff = stepDataRs.getString("LK_VALUE");
                                } else {
                                    if (!stepDataRs.getString("LK_VALUE").isEmpty() && (cutOff.equalsIgnoreCase("cutoff")) || cutOff.isEmpty()) {
                                        header.put("productCutOffTime", stepDataRs.getString("LK_VALUE"));
                                    } else {
                                        header.put("productCutOffTime", "All Day");
                                    }
                                }
                            } else if (stepDataRs.getString("LK_CODE").equalsIgnoreCase("prodType")) {
                                obj.put("productType", stepDataRs.getString("LK_VALUE"));
                            }
//                            header.put("businessDate", DateUtils.getDisplayEnDate(sysOperLog.getBusinessDate(), "dd-MM-yyyy"));
                        }
                        if (!stepDataRs.isClosed()) {
                            stepDataRs.close();
                        }
                        if (!psOperLog.isClosed()) {
                            psOperLog.close();
                        }
                        header.put("productCode", sysOperLog.getProductCode());

                    } else {
                        header.put("productName", "All");
                        header.put("productCode", "All");
                        header.put("productCutOffTime", "");
                    }
                    listDetail.put(obj);
                    ret.put("detail", listDetail);
                    ret.put("header", header);
                }
            }
        } catch (HibernateException | NullPointerException e) {
            logger.info(e.getMessage());
            throw e;
        } finally {
            if (stepDataRs != null && !stepDataRs.isClosed()) {
                stepDataRs.close();
            }
            if (psOperLog != null && !psOperLog.isClosed()) {
                psOperLog.close();
            }
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
        return ret;
    }

    public int getTransactionReportCount(String dbEnv, String prodCode, String txnId, String refNo, String ucId, String paymentMethod, String startDate, String endDate, String startTime, String endTime, Integer status, String state, String refTxnId) throws SQLException, UnsupportedEncodingException, ParseException {
        JSONObject ret = new JSONObject();
        PreparedStatement ps = null, psOperLog = null;
        ResultSet rs = null, stepDataRs = null;
        String cutOff = "";
        int total = 0 ; 
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            StringBuilder transactionCmd = new StringBuilder();
            List params = new ArrayList<>();
            transactionCmd.append("select count(LOG.uuid) AS total "
                    + "from t_sys_oper_log LOG "
                    + "LEFT JOIN T_SHELF_LOOKUP LK2 ON LOG.ATTR2 = LK2.LOOKUP_CODE "
                    + "INNER JOIN T_SHELF_LOOKUP LK ON LOG.STATE_CODE = LK.UUID "
                    + "INNER JOIN T_SYS_LOOKUP SL ON LOG.STATUS::TEXT = SL.LOOKUP_CODE "
                    + "INNER JOIN T_SYS_LOOKUP SL2 ON LOG.TRN_STATUS::TEXT = SL2.LOOKUP_CODE "
                    + "INNER JOIN T_SHELF_PRODUCT SP ON LOG.PRODUCT_ID = SP.UUID "
                    + "WHERE log.uuid IN "
                    + "( "
                    + "         select uuid from ("
                    + "   select uuid, row_number() over (partition by txn_no order by TO_TIMESTAMP(log.ATTR4, 'yyyy-MM-dd HH24:MI:SS') desc, log.CREATE_AT desc) as ROW_NO "
                    + "   from t_sys_oper_log log "
                    + "        )A "
                    + "        where A.ROW_NO = 1 "
                    //                    + "        select uuid from ( "
                    //                    + "                SELECT uuid, row_number() over (partition by txn_no order by CREATE_AT desc) as ROW_NO "
                    //                    + "                        , MAX(CREATE_AT) OVER (PARTITION BY TXN_NO) MAX_DATE "
                    //                    + "                        , MAX(PAYMENT_DATE) OVER (PARTITION BY TXN_NO) PAY_DATE "
                    //                    + "                        , MAX(TO_TIMESTAMP(ATTR4, 'yyyy-MM-dd HH24:MI:SS')) OVER (PARTITION BY TXN_NO) UPDATE_DATE "
                    //                    + "            FROM t_sys_oper_log S "
                    //                    + "        )A  "
                    //                    + "        where A.ROW_NO = 1 "
                    + " ) ");
            if (null != prodCode && !prodCode.isEmpty()) {
                transactionCmd.append(" AND PRODUCT_CODE = ? ");
                params.add(prodCode);
            }
            if (null != txnId && !txnId.isEmpty()) {
                transactionCmd.append(" AND LOWER(LOG.TXN_NO) LIKE ? ");
                params.add("%" + txnId.toLowerCase() + "%");
            }
            if (null != refNo && !refNo.isEmpty()) {
                transactionCmd.append(" AND LOG.REF_NO LIKE ? ");
                params.add("%" + refNo + "%");
            }
            if (null != ucId && !ucId.isEmpty()) {
//                transactionCmd.append(" AND LOG.ATTR1 = ? ");
                String id[] = ucId.replace("(", "").replace(")", "").replace("'", "").split(",");
                StringBuilder subCmd = new StringBuilder();
                subCmd.append(" AND LOG.ATTR1 in ( ? ");

                for (int i = 1; i < id.length; i++) {
                    subCmd.append(", ? ");
                }
                subCmd.append(") ");
                for (int i = 0; i < id.length; i++) {
                    params.add(id[i]);
                }
                transactionCmd.append(subCmd);
            }
            if (null != refTxnId && !refTxnId.isEmpty()) {
//                inquiryCmd.append(" AND LOG.ATTR1 = ? ");
                String id[] = refTxnId.replace("(", "").replace(")", "").replace("'", "").split(",");
//                transactionCmd.append(" AND LOG.ATTR1 = ? ");
                StringBuilder subCmd = new StringBuilder();
                subCmd.append(" AND LOWER(LOG.TXN_NO) in ( ? ");
                for (int i = 1; i < id.length; i++) {
                    subCmd.append(", ? ");
                }
                subCmd.append(") ");
                for (int i = 0; i < id.length; i++) {
                    params.add(id[i].toLowerCase());
                }
                transactionCmd.append(subCmd);
            }
            if (null != paymentMethod && !paymentMethod.isEmpty()) {
                transactionCmd.append(" AND LOG.PAYMENT_METHOD = ? ");
                params.add(paymentMethod);
            }
            if (null != startTime && !startTime.isEmpty()) {
                startTime += ":00";
            } else {
                startTime = "00:00:00";
            }
            if (null != endTime && !endTime.isEmpty()) {
                endTime += ":59";
            } else {
                endTime = "23:59:59";
            }
            if ((null != startDate && !startDate.isEmpty()) && (null != endDate && !endDate.isEmpty())) {
                transactionCmd.append(" AND LOG.CREATE_AT BETWEEN TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS') AND TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(startDate + " " + startTime);
                params.add(endDate + " " + endTime);
            } else if ((null == startDate || startDate.isEmpty()) && (null != endDate && !endDate.isEmpty())) {
                transactionCmd.append(" AND LOG.CREATE_AT <= TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(endDate + " " + endTime);
            } else if ((null != startDate && !startDate.isEmpty()) && (null == endDate || endDate.isEmpty())) {
                transactionCmd.append(" AND LOG.CREATE_AT >= TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(startDate + " " + startTime);
            }
            if (null != status) {
                transactionCmd.append(" AND LOG.STATUS = ? ");
                params.add(status);
            }
            if (null != state && !state.isEmpty()) {
                transactionCmd.append(" AND LK.LOOKUP_CODE = ? ");
                params.add(state);
            }
//            transactionCmd.append(" ORDER BY LOG.CREATE_AT ASC");
            ps = session.doReturningWork((Connection conn) -> conn).prepareStatement(transactionCmd.toString());
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
                total = rs.getInt("total");
            }
        } catch (HibernateException | NullPointerException e) {
            logger.info(e.getMessage());
            throw e;
        } finally {
            if (stepDataRs != null && !stepDataRs.isClosed()) {
                stepDataRs.close();
            }
            if (psOperLog != null && !psOperLog.isClosed()) {
                psOperLog.close();
            }
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
        return total;
    }

    public JSONObject getInquiryTransaction(String dbEnv, String company, String groupProduct, String ucId, String prodCode, String refNo, String paymentMethod, String txnId, String txnDateStart, String txnStartTime, String txnDateEnd, String txnEndTime, Integer status, String state, String paymentDateStart, String paymentDateEnd, String refTxnId) throws SQLException, UnsupportedEncodingException, ParseException {
        JSONObject ret = new JSONObject();
        PreparedStatement ps = null, psOperLog = null;
        ResultSet rs = null, stepDataRs = null;
        String cutOff = "";
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            logger.info("Report Inquiry To getInquiryTransaction DAO");
            StringBuilder inquiryCmd = new StringBuilder();
            List params = new ArrayList<>();
            inquiryCmd.append("select log.* ,LK.LOOKUP_CODE ST_CODE, LK.LOOKUP_NAME_EN STATE_NAME, LK.LOOKUP_NAME_TH, SL.LOOKUP_NAME_EN STATUS_NAME, SP.COMPANY COMPANY ,LK2.LOOKUP_NAME_TH ERR_NAME_TH, LK2.LOOKUP_NAME_EN ERR_NAME_EN, LK2.DESCRIPTION ERR_DESC "
                    + "from t_sys_oper_log LOG "
                    + "INNER JOIN T_SHELF_LOOKUP LK ON LOG.STATE_CODE = LK.UUID  "
                    + "INNER JOIN T_SYS_LOOKUP SL ON LOG.STATUS::TEXT = SL.LOOKUP_CODE "
                    + "INNER JOIN T_SHELF_PRODUCT SP ON LOG.PRODUCT_ID = SP.UUID "
                    + "INNER JOIN T_SYS_LOOKUP SL2 ON LOG.TRN_STATUS::TEXT = SL2.LOOKUP_CODE "
                    + "LEFT JOIN T_SHELF_LOOKUP LK2 ON LOG.ATTR2 = LK2.LOOKUP_CODE  "
                    + "WHERE log.uuid IN "
                    + "( "
                    + "         select uuid from ("
                    + "   select uuid, row_number() over (partition by txn_no order by TO_TIMESTAMP(log.ATTR4, 'yyyy-MM-dd HH24:MI:SS') desc, log.CREATE_AT desc) as ROW_NO "
                    + "   from t_sys_oper_log log "
                    + "        )A "
                    + "        where A.ROW_NO = 1 "
                    //                    + "        select uuid from ( "
                    //                    + "                SELECT uuid, row_number() over (partition by txn_no order by CREATE_AT desc) as ROW_NO "
                    //                    + "                        , MAX(CREATE_AT) OVER (PARTITION BY TXN_NO) MAX_DATE "
                    //                    + "                        , MAX(PAYMENT_DATE) OVER (PARTITION BY TXN_NO) PAY_DATE "
                    //                    + "                        , MAX(TO_TIMESTAMP(ATTR4, 'yyyy-MM-dd HH24:MI:SS')) OVER (PARTITION BY TXN_NO) UPDATE_DATE "
                    //                    + "            FROM t_sys_oper_log S "
                    //                    + "        )A  "
                    //                    + "        where A.ROW_NO = 1 "
                    + " ) ");
            if (null != state && !state.isEmpty()) {
                inquiryCmd.append(" AND LOG.STATE_CODE = (SELECT UUID FROM T_SHELF_LOOKUP WHERE GROUP_TYPE = 'PROCESS_STATE' AND LOOKUP_CODE = ?) ");
                params.add(state);
            }
            if (null != company && !company.isEmpty()) {
                inquiryCmd.append(" AND SP.COMPANY = ? ");
                params.add(company);
            }
            if (null != groupProduct && !groupProduct.isEmpty()) {
                inquiryCmd.append(" AND LOG.GROUP_PRODUCT = ? ");
                params.add(groupProduct);
            }
            if (null != prodCode && !prodCode.isEmpty()) {
                inquiryCmd.append(" AND LOG.PRODUCT_CODE = ? ");
                params.add(prodCode);
            }
            if (null != txnId && !txnId.isEmpty()) {
                inquiryCmd.append(" AND LOWER(LOG.TXN_NO) LIKE ? ");
                params.add("%" + txnId.toLowerCase() + "%");
            }
            if (null != refNo && !refNo.isEmpty()) {
                inquiryCmd.append(" AND LOG.REF_NO LIKE ? ");
                params.add("%" + refNo + "%");
            }
            if (null != ucId && !ucId.isEmpty()) {
//                inquiryCmd.append(" AND LOG.ATTR1 = ? ");
                String id[] = ucId.replace("(", "").replace(")", "").replace("'", "").split(",");
//                transactionCmd.append(" AND LOG.ATTR1 = ? ");
                StringBuilder subCmd = new StringBuilder();
                subCmd.append(" AND LOG.ATTR1 in ( ? ");

                for (int i = 1; i < id.length; i++) {
                    subCmd.append(", ? ");
                }
                subCmd.append(") ");
                for (int i = 0; i < id.length; i++) {
                    params.add(id[i]);
                }
                inquiryCmd.append(subCmd);
            }
            if (null != refTxnId && !refTxnId.isEmpty()) {
//                inquiryCmd.append(" AND LOG.ATTR1 = ? ");
                String id[] = refTxnId.replace("(", "").replace(")", "").replace("'", "").split(",");
//                transactionCmd.append(" AND LOG.ATTR1 = ? ");
                StringBuilder subCmd = new StringBuilder();
                subCmd.append(" AND LOWER(LOG.TXN_NO) in ( ? ");
                for (int i = 1; i < id.length; i++) {
                    subCmd.append(", ? ");
                }
                subCmd.append(") ");
                for (int i = 0; i < id.length; i++) {
                    params.add(id[i].toLowerCase());
                }
                inquiryCmd.append(subCmd);
            }
            if (null != paymentMethod && !paymentMethod.isEmpty()) {
                inquiryCmd.append(" AND LOG.PAYMENT_METHOD = ? ");
                params.add(paymentMethod);
            }
            if (null != txnStartTime && !txnStartTime.isEmpty()) {
                txnStartTime += ":00";
            } else {
                txnStartTime = "00:00:00";
            }
            if (null != txnEndTime && !txnEndTime.isEmpty()) {
                txnEndTime += ":59";
            } else {
                txnEndTime = "23:59:59";
            }
            if ((null != txnDateStart && !txnDateStart.isEmpty()) && (null != txnDateEnd && !txnDateEnd.isEmpty())) {
                inquiryCmd.append(" AND LOG.CREATE_AT BETWEEN TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS') AND TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(txnDateStart + " " + txnStartTime);
                params.add(txnDateEnd + " " + txnEndTime);
            } else if ((null == txnDateStart || txnDateStart.isEmpty()) && (null != txnDateEnd && !txnDateEnd.isEmpty())) {
                inquiryCmd.append(" AND LOG.CREATE_AT <= TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(txnDateEnd + " " + txnEndTime);
            } else if ((null != txnDateStart && !txnDateStart.isEmpty()) && (null == txnDateEnd || txnDateEnd.isEmpty())) {
                inquiryCmd.append(" AND LOG.CREATE_AT >= TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(txnDateStart + " " + txnStartTime);
            }
            if (null != status) {
                inquiryCmd.append(" AND LOG.STATUS = ? ");
                params.add(status);
            }
            if ((null != paymentDateStart && !paymentDateStart.isEmpty()) && (null != paymentDateEnd && !paymentDateEnd.isEmpty())) {
                inquiryCmd.append(" AND LOG.PAYMENT_DATE BETWEEN TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS') AND TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(paymentDateStart + " 00:00:00");
                params.add(paymentDateEnd + " 23:59:59");
            } else if ((null == paymentDateStart || paymentDateStart.isEmpty()) && (null != paymentDateEnd && !paymentDateEnd.isEmpty())) {
                inquiryCmd.append(" AND LOG.PAYMENT_DATE <= TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(paymentDateEnd + " 23:59:59");
            } else if ((null != paymentDateStart && !paymentDateStart.isEmpty()) && (null == paymentDateEnd || paymentDateEnd.isEmpty())) {
                inquiryCmd.append(" AND LOG.PAYMENT_DATE >= TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(paymentDateStart + " 00:00:00");
            }
            inquiryCmd.append(" ORDER BY LOG.CREATE_AT ASC");
            ps = session.doReturningWork((Connection conn) -> conn).prepareStatement(inquiryCmd.toString());
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
            logger.info("Report Inquiry DAO: Query Finish and set data to rs");
//                JSONObject logObj = new JSONObject();
//                JSONObject detail = new JSONObject();
            JSONArray listDetail = new JSONArray();
//                JSONObject summary = new JSONObject();
            JSONObject header = new JSONObject();
//                JSONObject stepData = new JSONObject();
//                JSONObject summaryDtl = new JSONObject();
            while (rs.next()) {
                SysOperLog sysOperLog = new SysOperLog();
                sysOperLog.setUuid(rs.getString("uuid"));
                sysOperLog.setTrnId(rs.getString("trn_id"));
                sysOperLog.setSource(rs.getString("source"));
                sysOperLog.setProductId(rs.getString("product_id"));
                sysOperLog.setProductCode(rs.getString("product_code"));
                sysOperLog.setProductVersionId(rs.getString("product_version_id"));
                sysOperLog.setProductComponentId(rs.getString("product_component_id"));
                sysOperLog.setTaskCategory(rs.getString("task_category"));
                sysOperLog.setKeywords(rs.getString("keywords"));
                sysOperLog.setTrnStatus(rs.getInt("trn_status"));
                sysOperLog.setTrnSubStatus(rs.getInt("trn_sub_status"));
                sysOperLog.setFailureReason(rs.getString("failure_reason"));
                sysOperLog.setSourceDevice(rs.getString("source_device"));
                sysOperLog.setSourceDeviceId(rs.getString("source_device_id"));
                sysOperLog.setSourceCifId(rs.getString("source_cif_id"));
                sysOperLog.setAccountName(rs.getString("account_name"));
                sysOperLog.setBusinessDate(rs.getTimestamp("business_date"));
                sysOperLog.setRefNo(rs.getString("ref_no"));
                sysOperLog.setPaymentMethod(rs.getString("payment_method"));
                sysOperLog.setPaymentDate(rs.getTimestamp("payment_date"));//paymentdate
                sysOperLog.setStateCode(new ShelfLookup(rs.getString("state_code")));
                sysOperLog.setProdChannel(rs.getString("prod_channel"));
                sysOperLog.setStepData(rs.getString("step_data"));
                sysOperLog.setCaseId(rs.getString("case_id"));
                sysOperLog.setGroupProduct(rs.getString("group_product"));
                sysOperLog.setTxnNo(rs.getString("txn_no"));
                sysOperLog.setAttr1(rs.getString("attr1"));
                sysOperLog.setAttr2(rs.getString("attr2"));
                sysOperLog.setAttr3(rs.getString("attr3"));
                sysOperLog.setAttr4(rs.getString("attr4"));
                sysOperLog.setAttr5(rs.getString("attr5"));
                sysOperLog.setAttr6(rs.getString("attr6"));
                sysOperLog.setAttr7(rs.getString("attr7"));
                sysOperLog.setAttr8(rs.getString("attr8"));
                sysOperLog.setAttr9(rs.getString("attr9"));
                sysOperLog.setAttr10(rs.getString("attr10"));
                sysOperLog.setStatus(rs.getInt("status"));
                sysOperLog.setCreateAt(rs.getTimestamp("create_at"));
                sysOperLog.setCreateBy(rs.getString("create_by"));
                sysOperLog.setStateTime(ValidUtils.str2BigInt(rs.getInt("state_time") + ""));
                String errNameTh = rs.getString("ERR_NAME_TH");
                String errNameEn = rs.getString("ERR_NAME_EN");
                String errNameDesc = rs.getString("ERR_DESC");
                inquiryCmd.setLength(0);
//                    inquiryCmd.append("SELECT STEP_DATA, PAYMENT_METHOD FROM T_SYS_OPER_LOG WHERE TXN_NO = ? AND STATE_CODE = '71228c52-4322-11ea-b77f-2e728ce88125'");
                inquiryCmd.append("SELECT STEP_DATA, PAYMENT_METHOD FROM T_SYS_OPER_LOG WHERE TXN_NO = ? AND STATE_CODE in (SELECT UUID FROM T_SHELF_LOOKUP WHERE GROUP_TYPE = 'PROCESS_STATE' AND LOOKUP_CODE in ('PRO1011','PRO1032'))"); //PRO1011
                psOperLog = session.doReturningWork((Connection conn) -> conn).prepareStatement(inquiryCmd.toString());
                psOperLog.setString(1, sysOperLog.getTxnNo());
                stepDataRs = psOperLog.executeQuery();
                JSONObject stepData = new JSONObject();
                stepData.put("paymentMethod", sysOperLog.getPaymentMethod());
                JSONArray packageDataArr = new JSONArray();
                while (stepDataRs.next()) {
                    byte[] decoded = Base64.decodeBase64(stepDataRs.getString("STEP_DATA"));
                    String stepDataIn = new String(decoded, "UTF-8");
                    if (!stepDataIn.isEmpty()) {
                        JSONArray stepDataArray = new JSONArray(stepDataIn);
                        for (int i = 0; i < stepDataArray.length(); i++) {
                            JSONObject stepDataObj = stepDataArray.getJSONObject(i);
                            if (stepDataObj.getJSONObject("data").has("package")) {
                                packageDataArr.put(stepDataObj);
                            }
                            if (stepDataObj.has("code") && stepDataObj.getString("code").equalsIgnoreCase("loan")) {
                                if (stepDataObj.getJSONObject("data").has("package")) {
                                    JSONArray packageDataArrLoan = stepDataObj.getJSONObject("data").getJSONArray("package");
                                    for (int j = 0; j < packageDataArrLoan.length(); j++) {
                                        JSONObject packageData = packageDataArrLoan.getJSONObject(j);
                                        if (packageData.has("parameter")) {
                                            switch (packageData.getString("parameter")) {
                                                case "limit":
                                                    stepData.put("requestLimit", packageData.getString("value"));
                                                    break;
                                                case "factor_2_from":
                                                    stepData.put("month", packageData.getInt("value") + "");
                                                    break;
                                                case "calAmount1":
                                                    stepData.put("installmentPerMonth", packageData.getString("value"));
                                                    break;
                                                case "rateAmount":
                                                    stepData.put("interestRate", packageData.getDouble("value") + "");
                                                    break;
                                                case "paymentDate":
                                                    stepData.put("paymentDate", packageData.getString("value"));
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
                if (!stepDataRs.isClosed()) {
                    stepDataRs.close();
                }
                if (!psOperLog.isClosed()) {
                    psOperLog.close();
                }
                if (ret.has("detail")) {
                    JSONObject obj = new JSONObject();
                    obj.put("txnsId", ValidUtils.null2NoData(sysOperLog.getTxnNo()))
                            .put("refNo", sysOperLog.getRefNo())
                            .put("txnsDateTime", DateUtils.getDisplayEnDate(sysOperLog.getCreateAt(), "dd/MM/yyyy HH:mm"))
                            //                                .put("paymentDate", stepData.has("paymentDate") ? stepData.getString("paymentDate") : "")
                            .put("installmentDate", stepData.has("paymentDate") ? stepData.getString("paymentDate") : "")
                            .put("paymentDate", ValidUtils.null2NoData(DateUtils.getDisplayEnDate(sysOperLog.getPaymentDate(), "dd/MM/yyyy HH:mm")))
                            .put("businessDate", ValidUtils.null2NoData(DateUtils.getDisplayEnDate(sysOperLog.getBusinessDate(), "dd-MM-yyyy")))
                            .put("requestLimit", stepData.has("requestLimit") ? stepData.getString("requestLimit") : "")
                            .put("month", stepData.has("month") ? stepData.getString("month") : "")
                            .put("installmentPerMonth", stepData.has("installmentPerMonth") ? stepData.getString("installmentPerMonth") : "")
                            .put("interestRate", stepData.has("interestRate") ? stepData.getString("interestRate") : "")
                            .put("paymentMethod", stepData.has("paymentMethod") ? stepData.getString("paymentMethod") : "")
                            .put("status", rs.getString("STATUS_NAME"))
                            .put("state", rs.getString("STATE_NAME"))
                            .put("procError", ValidUtils.null2NoData(sysOperLog.getAttr2()))
                            .put("procErrNameTh", ValidUtils.null2NoData(errNameTh))
                            .put("procErrNameEn", ValidUtils.null2NoData(errNameEn))
                            .put("procErrDesc", ValidUtils.null2NoData(errNameDesc))
                            .put("ucId", ValidUtils.null2NoData(sysOperLog.getAttr1()))
                            .put("updatedDate", DateUtils.getDisplayEnDate(ValidUtils.str2Date(sysOperLog.getAttr4(), "yyyy-MM-dd HH:mm:ss"), "dd/MM/yyyy HH:mm"))
                            .put("agreementDate", ValidUtils.null2NoData(sysOperLog.getAttr3()))
                            .put("trnStatus", ValidUtils.null2NoData(sysOperLog.getTrnStatus()));
                    if (packageDataArr.length() == 0) {
                        String packageData = "[{'respDesc': '', 'code': '', 'data': { 'payDuedate': '', 'buDay': '', 'package': [], 'hpDueDate': '', 'channel': '', 'limit': '', 'hpNo': '', 'discount': '', 'term': '', 'payDate': '', 'lastDueDate': '' }, 'respCode': '', 'status': ''}]";
                        packageDataArr = new JSONArray(packageData);
                        inquiryCmd.setLength(0);
                        inquiryCmd.append("SELECT LK_VALUE PACKAGE_DATA FROM T_SHELF_PRODUCT_DTL "
                                + "WHERE TRN_UUID IN (SELECT UUID FROM T_SHELF_PRODUCT_VCS WHERE PROD_UUID = ? AND VER_PROD = ?) "
                                + "AND LK_CODE = 'summaryList'");
                        psOperLog = session.doReturningWork((Connection conn) -> conn).prepareStatement(inquiryCmd.toString());
                        psOperLog.setString(1, sysOperLog.getProductId());
                        psOperLog.setInt(2, ValidUtils.str2BigInteger(sysOperLog.getProductVersionId()));
                        stepDataRs = psOperLog.executeQuery();
                        while (stepDataRs.next()) {
                            packageData = stepDataRs.getString("PACKAGE_DATA");
                            if (!packageData.isEmpty()) {
                                JSONArray defaultData = new JSONArray(packageData);
                                for (int dfi = 0; dfi < defaultData.length(); dfi++) {
                                    defaultData.getJSONObject(dfi).put("value", "");
                                }
                                packageDataArr.getJSONObject(0).getJSONObject("data").put("package", defaultData);
                            }
                        }
                        if (!stepDataRs.isClosed()) {
                            stepDataRs.close();
                        }
                        if (!psOperLog.isClosed()) {
                            psOperLog.close();
                        }
//                            packageDataArr.getJSONObject(0).getJSONObject("data").put("package", new JSONArray(packageData));
                        obj.put("package", packageDataArr);
                    } else {
                        obj.put("package", packageDataArr);
                    }
                    inquiryCmd.setLength(0);
                    inquiryCmd.append("SELECT LK_CODE,LK_LABEL,LK_VALUE FROM T_SHELF_PRODUCT_DTL WHERE 1=1 "
                            + " AND TRN_UUID IN (SELECT UUID FROM T_SHELF_PRODUCT_VCS VCS WHERE VCS.PROD_UUID = ? AND VER_PROD = ? )"
                            + " AND LK_CODE IN ('cutOffTime','pcutOffDay','pcutOffWE','pcutOffTime','pcutOffTAllD','pcutOffTSpec', 'prodName', 'prodType')");
                    psOperLog = session.doReturningWork((Connection conn) -> conn).prepareStatement(inquiryCmd.toString());
                    psOperLog.setString(1, sysOperLog.getProductId());
                    psOperLog.setInt(2, ValidUtils.str2BigInteger(sysOperLog.getProductVersionId()));
                    stepDataRs = psOperLog.executeQuery();
                    while (stepDataRs.next()) {
                        if (stepDataRs.getString("LK_CODE").equalsIgnoreCase("prodType")) {
                            obj.put("productType", stepDataRs.getString("LK_VALUE"));
                        }
                    }
                    if (!stepDataRs.isClosed()) {
                        stepDataRs.close();
                    }
                    if (!psOperLog.isClosed()) {
                        psOperLog.close();
                    }
                    ret.getJSONArray("detail").put(obj);
                } else {
                    JSONObject obj = new JSONObject();
                    obj.put("txnsId", ValidUtils.null2NoData(sysOperLog.getTxnNo()))
                            .put("refNo", sysOperLog.getRefNo())
                            .put("txnsDateTime", DateUtils.getDisplayEnDate(sysOperLog.getCreateAt(), "dd/MM/yyyy HH:mm"))
                            //                                .put("paymentDate", stepData.has("paymentDate") ? stepData.getString("paymentDate") : "")
                            .put("installmentDate", stepData.has("paymentDate") ? stepData.getString("paymentDate") : "")
                            .put("paymentDate", ValidUtils.null2NoData(DateUtils.getDisplayEnDate(sysOperLog.getPaymentDate(), "dd/MM/yyyy HH:mm")))
                            .put("businessDate", ValidUtils.null2NoData(DateUtils.getDisplayEnDate(sysOperLog.getBusinessDate(), "dd-MM-yyyy")))
                            .put("requestLimit", stepData.has("requestLimit") ? stepData.getString("requestLimit") : "")
                            .put("month", stepData.has("month") ? stepData.getString("month") : "")
                            .put("installmentPerMonth", stepData.has("installmentPerMonth") ? stepData.getString("installmentPerMonth") : "")
                            .put("interestRate", stepData.has("interestRate") ? stepData.getString("interestRate") : "")
                            //                                .put("productType", sysOperLog.getProductId())//getFrom product
                            .put("paymentMethod", stepData.has("paymentMethod") ? stepData.getString("paymentMethod") : "")
                            .put("status", rs.getString("STATUS_NAME"))
                            .put("state", rs.getString("STATE_NAME"))
                            .put("procError", ValidUtils.null2NoData(sysOperLog.getAttr2()))
                            .put("procErrNameTh", ValidUtils.null2NoData(errNameTh))
                            .put("procErrNameEn", ValidUtils.null2NoData(errNameEn))
                            .put("procErrDesc", ValidUtils.null2NoData(errNameDesc))
                            .put("ucId", ValidUtils.null2NoData(sysOperLog.getAttr1()))
                            .put("updatedDate", DateUtils.getDisplayEnDate(ValidUtils.str2Date(sysOperLog.getAttr4(), "yyyy-MM-dd HH:mm:ss"), "dd/MM/yyyy HH:mm"))
                            .put("agreementDate", ValidUtils.null2NoData(sysOperLog.getAttr3()))
                            .put("trnStatus", ValidUtils.null2NoData(sysOperLog.getTrnStatus()));
                    if (packageDataArr.length() == 0) {
                        String packageData = "[{'respDesc': '', 'code': '', 'data': { 'payDuedate': '', 'buDay': '', 'package': [], 'hpDueDate': '', 'channel': '', 'limit': '', 'hpNo': '', 'discount': '', 'term': '', 'payDate': '', 'lastDueDate': '' }, 'respCode': '', 'status': ''}]";
                        packageDataArr = new JSONArray(packageData);
                        inquiryCmd.setLength(0);
                        inquiryCmd.append("SELECT LK_VALUE PACKAGE_DATA FROM T_SHELF_PRODUCT_DTL "
                                + "WHERE TRN_UUID IN (SELECT UUID FROM T_SHELF_PRODUCT_VCS WHERE PROD_UUID = ? AND VER_PROD = ?) "
                                + "AND LK_CODE = 'summaryList'");
                        psOperLog = session.doReturningWork((Connection conn) -> conn).prepareStatement(inquiryCmd.toString());
                        psOperLog.setString(1, sysOperLog.getProductId());
                        psOperLog.setInt(2, ValidUtils.str2BigInteger(sysOperLog.getProductVersionId()));
                        stepDataRs = psOperLog.executeQuery();
                        while (stepDataRs.next()) {
                            packageData = stepDataRs.getString("PACKAGE_DATA");
                            if (!packageData.isEmpty()) {
                                JSONArray defaultData = new JSONArray(packageData);
                                for (int dfi = 0; dfi < defaultData.length(); dfi++) {
                                    defaultData.getJSONObject(dfi).put("value", "");
                                }
                                packageDataArr.getJSONObject(0).getJSONObject("data").put("package", defaultData);
                            }
                        }
                        if (!stepDataRs.isClosed()) {
                            stepDataRs.close();
                        }
                        if (!psOperLog.isClosed()) {
                            psOperLog.close();
                        }
                        obj.put("package", packageDataArr);
                    } else {
                        obj.put("package", packageDataArr);
                    }
                    if (null != prodCode && !prodCode.isEmpty()) {
                        inquiryCmd.setLength(0);
                        inquiryCmd.append("SELECT LK_CODE,LK_LABEL,LK_VALUE FROM T_SHELF_PRODUCT_DTL WHERE 1=1 "
                                + " AND TRN_UUID IN (SELECT UUID FROM T_SHELF_PRODUCT_VCS VCS WHERE VCS.PROD_UUID = ? AND VER_PROD = ? )"
                                + " AND LK_CODE IN ('cutOffTime','pcutOffDay','pcutOffWE','pcutOffTime','pcutOffTAllD','pcutOffTSpec', 'prodName', 'prodType')");
                        psOperLog = session.doReturningWork((Connection conn) -> conn).prepareStatement(inquiryCmd.toString());
                        psOperLog.setString(1, sysOperLog.getProductId());
                        psOperLog.setInt(2, ValidUtils.str2BigInteger(sysOperLog.getProductVersionId()));
                        stepDataRs = psOperLog.executeQuery();
                        while (stepDataRs.next()) {
                            if (stepDataRs.getString("LK_CODE").equalsIgnoreCase("prodName")) {
                                header.put("productName", stepDataRs.getString("LK_VALUE"));
                            } else if (stepDataRs.getString("LK_CODE").equalsIgnoreCase("pcutOffTSpec") || stepDataRs.getString("LK_CODE").equalsIgnoreCase("pcutOffTAllD")) {
                                if (stepDataRs.getString("LK_CODE").equalsIgnoreCase("pcutOffTAllD")) {
                                    cutOff = stepDataRs.getString("LK_VALUE");
                                } else {
                                    if (!stepDataRs.getString("LK_VALUE").isEmpty() && (cutOff.equalsIgnoreCase("cutoff")) || cutOff.isEmpty()) {
                                        header.put("productCutOffTime", stepDataRs.getString("LK_VALUE"));
                                    } else {
                                        header.put("productCutOffTime", "All Day");
                                    }
                                }
                            } else if (stepDataRs.getString("LK_CODE").equalsIgnoreCase("prodType")) {
                                obj.put("productType", stepDataRs.getString("LK_VALUE"));
                            }
//                            header.put("businessDate", DateUtils.getDisplayEnDate(sysOperLog.getBusinessDate(), "dd-MM-yyyy"));
                        }
                        if (!stepDataRs.isClosed()) {
                            stepDataRs.close();
                        }
                        if (!psOperLog.isClosed()) {
                            psOperLog.close();
                        }
                        header.put("productCode", sysOperLog.getProductCode());
                    } else {
                        header.put("productName", "All");
                        header.put("productCode", "All");
                        header.put("productCutOffTime", "");
                    }
                    listDetail.put(obj);
                    ret.put("detail", listDetail);
                    ret.put("header", header);
                }
            }
        } catch (HibernateException | NullPointerException e) {
            logger.info(e.getMessage());
            throw e;
        } finally {
            if (stepDataRs != null && !stepDataRs.isClosed()) {
                stepDataRs.close();
            }
            if (psOperLog != null && !psOperLog.isClosed()) {
                psOperLog.close();
            }
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
        logger.info("Report Inquiry : Return Data search DAO to Controller");
        return ret;
    }

    public JSONArray getErrorLogList(String dbEnv, JSONObject param) throws SQLException, ParseException {
        JSONArray arr = new JSONArray();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            StringBuilder errorLogCmd = new StringBuilder();
            List params = new ArrayList<>();
            errorLogCmd.append(" select log.*,LOG.CREATE_AT, LOG.TXN_NO,LOG.STATE_TIME,LOG.TASK_CATEGORY,SL2.LOOKUP_NAME_TH TRN_STATUS_NAME,SL3.LOOKUP_NAME_TH TRN_SUB_STATUS_NAME,LK.LOOKUP_CODE ST_CODE, LK.LOOKUP_NAME_TH ST_NAME_TH, LK.LOOKUP_NAME_EN ST_NAME_EN ,SL.LOOKUP_NAME_TH STATUS_NAME,SP.PROD_CODE PROD_CODE, SP.PROD_NAME PROD_NAME, SP.COMPANY,LOG.GROUP_PRODUCT,LOG.FAILURE_REASON, LOG.SOURCE_CIF_ID,LOG.ATTR2 AS PROCESS_ERROR  ,LK2.LOOKUP_NAME_TH AS PROCESS_ERROR_NAME_TH ,LK2.LOOKUP_NAME_EN AS PROCESS_ERROR_NAME_EN , LK2.DESCRIPTION PROCESS_ERROR_DESC "
                    + " from t_sys_oper_log log "
                    + "         LEFT JOIN T_SHELF_LOOKUP LK2 ON LOG.ATTR2 = LK2.LOOKUP_CODE  "
                    + "        INNER JOIN T_SHELF_LOOKUP LK ON LOG.STATE_CODE = LK.UUID  "
                    + "        INNER JOIN T_SYS_LOOKUP SL ON LOG.STATUS::TEXT = SL.LOOKUP_CODE  "
                    + "        INNER JOIN T_SHELF_PRODUCT SP ON LOG.PRODUCT_ID = SP.UUID  "
                    + "        INNER JOIN T_SYS_LOOKUP SL2 ON LOG.TRN_STATUS::TEXT = SL2.LOOKUP_CODE  "
                    + "        LEFT JOIN T_SYS_LOOKUP SL3 ON LOG.TRN_SUB_STATUS::TEXT = SL3.LOOKUP_CODE "
                    + "WHERE log.uuid IN "
                    + "( "
                    + "         select uuid from ("
                    + "   select uuid, row_number() over (partition by txn_no order by TO_TIMESTAMP(log.ATTR4, 'yyyy-MM-dd HH24:MI:SS') desc, log.CREATE_AT desc) as ROW_NO "
                    + "   from t_sys_oper_log log "
                    + "        )A "
                    + "        where A.ROW_NO = 1 "
                    //                    + "        select uuid from ( "
                    //                    + "                SELECT uuid, row_number() over (partition by txn_no order by CREATE_AT desc) as ROW_NO "
                    //                    + "                        , MAX(CREATE_AT) OVER (PARTITION BY TXN_NO) MAX_DATE "
                    //                    + "                        , MAX(PAYMENT_DATE) OVER (PARTITION BY TXN_NO) PAY_DATE "
                    //                    + "                        , MAX(TO_TIMESTAMP(ATTR4, 'yyyy-MM-dd HH24:MI:SS')) OVER (PARTITION BY TXN_NO) UPDATE_DATE "
                    //                    + "            FROM t_sys_oper_log S "
                    //                    + "        )A  "
                    //                    + "        where A.ROW_NO = 1 "
                    + " ) ");
            String prodCode = param.has("prodCode") ? ValidUtils.null2NoData(param.get("prodCode")) : "";
            String startDate = param.has("startDate") ? ValidUtils.null2NoData(param.get("startDate")) : "";
            String endDate = param.has("endDate") ? ValidUtils.null2NoData(param.get("endDate")) : "";
            String startTime = param.has("startTime") ? ValidUtils.null2NoData(param.get("startTime")) : "";
            String endTime = param.has("endTime") ? ValidUtils.null2NoData(param.get("endTime")) : "";
            Integer status = param.has("status") ? ValidUtils.str2BigInteger(ValidUtils.obj2String(param.get("status"))) : null;
            Integer trnStatus = param.has("trnStatus") ? ValidUtils.str2BigInteger(ValidUtils.obj2String(param.get("trnStatus"))) : null;
            String company = param.has("company") ? ValidUtils.null2NoData(param.get("company")) : "";
            String groupProduct = param.has("groupProduct") ? ValidUtils.null2NoData(param.get("groupProduct")) : "";
            String trnNo = param.has("trnNo") ? ValidUtils.null2NoData(param.get("trnNo")) : "";
            String stateCode = param.has("stateCode") ? ValidUtils.null2NoData(param.get("stateCode")) : "";
            String taskCategory = param.has("taskCategory") ? ValidUtils.null2NoData(param.get("taskCategory")) : "";
            String attr2 = param.has("processErr") ? ValidUtils.null2NoData(param.get("processErr")) : "";

            if (null != prodCode && !prodCode.isEmpty()) {
                errorLogCmd.append(" AND SP.PROD_CODE = ? ");
                params.add(prodCode);
            }
            if (null != startTime && !startTime.isEmpty()) {
                startTime += ":00";
            } else {
                startTime = "00:00:00";
            }
            if (null != endTime && !endTime.isEmpty()) {
                endTime += ":59";
            } else {
                endTime = "23:59:59";
            }
//            String fStartTime = "00:00:00", eStartTime = "00:00:00";
//            if (!startTime.isEmpty()) {
//                fStartTime = startTime + ":00";
//            }
//            if (!endTime.isEmpty()) {
//                eStartTime = endTime + ":59";
//            }
            if ((null != startDate && !startDate.isEmpty()) && (null != endDate && !endDate.isEmpty())) {
                errorLogCmd.append(" AND LOG.CREATE_AT BETWEEN TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS') AND TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(startDate + " " + startTime);
                params.add(endDate + " " + endTime);
            } else if ((null == startDate || startDate.isEmpty()) && (null != endDate && !endDate.isEmpty())) {
                errorLogCmd.append(" AND LOG.CREATE_AT <= TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(endDate + " " + endTime);
            } else if ((null != startDate && !startDate.isEmpty()) && (null == endDate || endDate.isEmpty())) {
                errorLogCmd.append(" AND LOG.CREATE_AT >= TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(startDate + " " + startTime);
            }
            if (null != company && !company.isEmpty()) {
                errorLogCmd.append(" AND SP.COMPANY = ? ");
                params.add(company);
            }
            if (null != groupProduct && !groupProduct.isEmpty()) {
                errorLogCmd.append(" AND LOG.GROUP_PRODUCT = ? ");
                params.add(groupProduct);
            }
            if (null != trnNo && !trnNo.isEmpty()) {
                errorLogCmd.append(" AND LOWER(LOG.TXN_NO) LIKE ? ");
                params.add("%" + trnNo.toLowerCase() + "%");
            }
//            if (null != trnNo && !trnNo.isEmpty()) {
//                errorLogCmd.append(" AND LOG.TXN_NO = ? ");
//                params.add(trnNo);
//            }
            if (null != trnStatus) {
                errorLogCmd.append(" AND LOG.TRN_STATUS = ? ");
                params.add(trnStatus);
            }
            if (null != stateCode && !stateCode.isEmpty()) {
                errorLogCmd.append(" AND LK.LOOKUP_CODE = ? ");
                params.add(stateCode);
            }
            if (null != taskCategory && !taskCategory.isEmpty()) {
                errorLogCmd.append(" AND LOG.TASK_CATEGORY = ? ");
                params.add(taskCategory);
            }
            if (null != status) {
                errorLogCmd.append(" AND log.STATUS = ? ");
                params.add(status);
            }
            if (null != attr2 && !attr2.isEmpty()) {
                errorLogCmd.append(" AND LOG.ATTR2 = ? ");
                params.add(attr2);
            }
            errorLogCmd.append(" ORDER BY LOG.CREATE_AT ASC ");
            ps = session.doReturningWork((Connection conn) -> conn).prepareStatement(errorLogCmd.toString());
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
                JSONObject data = new JSONObject()
                        .put("txnNo", ValidUtils.null2NoData(rs.getString("TXN_NO")))
                        .put("time", ValidUtils.obj2Int(rs.getInt("STATE_TIME")))
                        .put("trnDate", DateUtils.getDisplayEnDate(rs.getTimestamp("CREATE_AT"), "dd/MM/yyyy HH:mm:ssS"))
                        .put("prodcode", ValidUtils.null2NoData(rs.getString("PROD_CODE")))
                        .put("prodname", ValidUtils.null2NoData(rs.getString("PROD_NAME")))
                        .put("company", ValidUtils.null2NoData(rs.getString("COMPANY")))
                        .put("taskCategory", ValidUtils.null2NoData(rs.getString("TASK_CATEGORY")))
                        .put("trnStatus", ValidUtils.null2NoData(rs.getString("TRN_STATUS")))
                        .put("trnStatusName", ValidUtils.null2NoData(rs.getString("TRN_STATUS_NAME")))
                        .put("trnSubStatus", ValidUtils.null2NoData(rs.getString("TRN_SUB_STATUS")))
                        .put("trnSubStatusName", ValidUtils.null2NoData(rs.getString("TRN_SUB_STATUS_NAME")))
                        .put("failureReason", ValidUtils.null2NoData(rs.getString("FAILURE_REASON")))
                        .put("sourceCifId", ValidUtils.null2NoData(rs.getString("SOURCE_CIF_ID")))
                        .put("processError", ValidUtils.null2NoData(rs.getString("PROCESS_ERROR")))
                        .put("processErrorTh", ValidUtils.null2NoData(rs.getString("PROCESS_ERROR_NAME_TH")))
                        .put("processErrorEn", ValidUtils.null2NoData(rs.getString("PROCESS_ERROR_NAME_EN")))
                        .put("processErrorDesc", ValidUtils.null2NoData(rs.getString("PROCESS_ERROR_DESC")))
                        .put("groupProduct", ValidUtils.null2NoData(rs.getString("GROUP_PRODUCT")))
                        .put("logStatus", ValidUtils.null2NoData(rs.getString("STATUS_NAME")))
                        .put("stateCode", ValidUtils.null2NoData(rs.getString("ST_CODE")))
                        .put("updatedDate", DateUtils.getDisplayEnDate(ValidUtils.str2Date(rs.getString("ATTR4"), "yyyy-MM-dd HH:mm:ss"), "dd/MM/yyyy HH:mm"))
                        .put("stateName", ValidUtils.null2NoData(rs.getString("ST_NAME_TH")));
                arr.put(data);
            }
        } catch (HibernateException | NullPointerException e) {
            logger.info(e.getMessage());
            throw e;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
        return arr;
    }

    public JSONArray getListTaskCategory(String dbEnv) throws SQLException {
        JSONArray arr = new JSONArray();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            StringBuilder taskCategoryCmd = new StringBuilder();
            taskCategoryCmd.append("SELECT DISTINCT TASK_CATEGORY FROM T_SYS_OPER_LOG WHERE TASK_CATEGORY <> '' ORDER by TASK_CATEGORY ");
            ps = session.doReturningWork((Connection conn) -> conn).prepareStatement(taskCategoryCmd.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                JSONObject data = new JSONObject()
                        .put("value", ValidUtils.null2NoData(rs.getString("TASK_CATEGORY")))
                        .put("label", ValidUtils.null2NoData(rs.getString("TASK_CATEGORY")));
                arr.put(data);
            }
        } catch (HibernateException | NullPointerException e) {
            logger.info(e.getMessage());
            throw e;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
        return arr;
    }

    private JSONArray queryReconcileReport(Session session, String prodCode, String businessDate, JSONArray ret, String dateType) throws SQLException, ParseException, UnsupportedEncodingException {
//        JSONArray ret = new JSONArray();
        PreparedStatement ps = null, psOperLog = null;
        ResultSet rs = null, stepDataRs = null;
        String cutOff = "";
        try {
            StringBuilder reconcileCmd = new StringBuilder();
            StringBuilder productDetail = new StringBuilder();
            List params = new ArrayList<>();
//            reconcileCmd.append("SELECT * "
//                    + " FROM ("
//                    + " SELECT LOG.*, MAX(LOG.CREATE_AT) OVER (PARTITION BY LOG.TXN_NO) MAX_DATE, MAX( LK.LOOKUP_CODE ) OVER (PARTITION BY LOG.TXN_NO) MAX_STATE, LK.LOOKUP_CODE ST_CODE , LK.LOOKUP_NAME_EN STATE_NAME, LK.LOOKUP_NAME_TH, SL.LOOKUP_NAME_EN STATUS_NAME "
//                    + " FROM T_SYS_OPER_LOG LOG, T_SHELF_LOOKUP LK, T_SYS_LOOKUP SL "
//                    + " WHERE LOG.BUSINESS_DATE IS NOT NULL AND LOG.STATE_CODE = LK.UUID AND LOG.STATUS::TEXT = SL.LOOKUP_CODE"
//                    + " AND STATE_CODE IN (SELECT UUID FROM T_SHELF_LOOKUP WHERE GROUP_TYPE = 'PROCESS_STATE' AND LOOKUP_CODE IN ('PRO1013', 'PRO1014', 'PRO1015', 'PRO1016', 'PRO1017'))");
//            reconcileCmd.append(" ) AS S WHERE CREATE_AT = MAX_DATE ");
//            reconcileCmd.append(" AND ST_CODE = MAX_STATE ");
            reconcileCmd.append("select log.* , LK.LOOKUP_CODE ST_CODE , LK.LOOKUP_NAME_EN STATE_NAME_EN, LK.LOOKUP_NAME_TH STATE_NAME_TH, SL.LOOKUP_NAME_EN STATUS_NAME   "
                    + "from t_sys_oper_log log, T_SHELF_LOOKUP LK, T_SYS_LOOKUP SL , T_SHELF_PRODUCT SP ,T_SYS_LOOKUP SL2 "
                    + "WHERE log.uuid IN "
                    + "( "
                    + "         select uuid from ("
                    + "   select uuid, row_number() over (partition by txn_no order by TO_TIMESTAMP(log.ATTR4, 'yyyy-MM-dd HH24:MI:SS') desc, log.CREATE_AT desc) as ROW_NO "
                    + "   from t_sys_oper_log log "
                    + "        )A "
                    + "        where A.ROW_NO = 1 "
                    + " ) "
                    + "AND LOG.PRODUCT_ID = SP.UUID  "
                    + "AND LOG.BUSINESS_DATE IS NOT NULL  "
                    + "AND LOG.STATE_CODE = LK.UUID  "
                    + "AND LOG.STATUS::TEXT = SL.LOOKUP_CODE  "
                    + "AND LOG.TRN_STATUS::TEXT = SL2.LOOKUP_CODE  "
                    //                      + "AND STATE_CODE IN (SELECT UUID FROM T_SHELF_LOOKUP WHERE GROUP_TYPE = 'PROCESS_STATE' AND LOOKUP_CODE IN ('PRO1013', 'PRO1014', 'PRO1015', 'PRO1016', 'PRO1017','PRO1018'))  ");
                    + "AND STATE_CODE IN (SELECT UUID FROM T_SHELF_LOOKUP WHERE GROUP_TYPE = 'PROCESS_STATE' AND LOOKUP_CODE IN ('PRO1013', 'PRO1014', 'PRO1015','PRO1017','PRO1051','PRO1054', 'PRO1016','PRO1050','PRO1052','PRO1053','PRO1055','PRO1056','PRO1057','PRO1058', 'PRO1018'))  ");
            if (null != prodCode && !prodCode.isEmpty()) {
                reconcileCmd.append(" AND PRODUCT_CODE = ? ");
                params.add(prodCode);
            }
            if (dateType.equalsIgnoreCase("")) {
                if (null != businessDate && !businessDate.isEmpty()) {
                    reconcileCmd.append(" AND LOG.BUSINESS_DATE >= TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                    params.add(businessDate + " 00:00:00");
                    reconcileCmd.append(" AND LOG.BUSINESS_DATE <= TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                    params.add(businessDate + " 23:59:59");
                }
            } else if (dateType.equalsIgnoreCase("start")) {
                reconcileCmd.append(" AND LOG.BUSINESS_DATE <= TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(businessDate + " 23:59:59");
            } else if (dateType.equalsIgnoreCase("end")) {
                reconcileCmd.append(" AND LOG.BUSINESS_DATE >= TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(businessDate + " 00:00:00");
            }
            reconcileCmd.append(" ORDER BY LOG.CREATE_AT ASC");
            ps = session.doReturningWork((Connection conn) -> conn).prepareStatement(reconcileCmd.toString());
            if (params.size() > 0) {
                for (int i = 0; i < params.size(); i++) {
                    ps.setString(i + 1, (String) params.get(i));
                }
            }
            System.out.println(ps);
            rs = ps.executeQuery();
            JSONObject logObj = new JSONObject();
//                JSONObject detail = new JSONObject();
//                JSONArray listDetail = new JSONArray();
//                JSONObject header = new JSONObject();
//                header.put("businessDate", "")
//                        .put("productCode", "")
//                        .put("productName", "")
//                        .put("productCutOffTime", "");
//                JSONObject stepData = new JSONObject();
//                JSONObject summaryDtl = new JSONObject();
            if (rs.next() == false) {
                JSONObject detail = new JSONObject();
                JSONObject header = new JSONObject();
                header.put("businessDate", "")
                        .put("productCode", "")
                        .put("productName", "")
                        .put("productCutOffTime", "");
                if (null != prodCode && !prodCode.isEmpty()) {
                    productDetail.setLength(0);
                    productDetail.append("SELECT LK_CODE,LK_LABEL,LK_VALUE FROM T_SHELF_PRODUCT_DTL WHERE 1=1 "
                            + "AND TRN_UUID IN (SELECT UUID FROM T_SHELF_PRODUCT_VCS VCS WHERE VCS.PROD_UUID IN (select product_id from t_sys_oper_log where product_code = ? group by product_id)) "
                            //                                + "AND VER_PROD = (select COALESCE((select COALESCE(product_version_id, '0') from t_sys_oper_log where product_code = ?), '0') )::INTEGER) "
                            + "AND LK_CODE IN ('cutOffTime','pcutOffDay','pcutOffWE','pcutOffTime','pcutOffTAllD','pcutOffTSpec', 'prodName', 'prodType')");
                    psOperLog = session.doReturningWork((Connection conn) -> conn).prepareStatement(productDetail.toString());
                    psOperLog.setString(1, prodCode);
//                        psOperLog.setString(2, prodCode);
                    stepDataRs = psOperLog.executeQuery();
                    while (stepDataRs.next()) {
                        if (stepDataRs.getString("LK_CODE").equalsIgnoreCase("prodName")) {
                            header.put("productName", stepDataRs.getString("LK_VALUE"));
                        }
//                            else if (stepDataRs.getString("LK_CODE").equalsIgnoreCase("pcutOffTSpec") || stepDataRs.getString("LK_CODE").equalsIgnoreCase("pcutOffTAllD")) {
//                                if (!stepDataRs.getString("LK_VALUE").isEmpty()) {
//                                    header.put("productCutOffTime", stepDataRs.getString("LK_VALUE"));
//                                }
//                            }
                        header.put("businessDate", DateUtils.getDisplayEnDate(dayMonthYear.parse(businessDate), "dd-MM-yyyy"));
                    }
                    if (!stepDataRs.isClosed()) {
                        stepDataRs.close();
                    }
                    if (!psOperLog.isClosed()) {
                        psOperLog.close();
                    }
                    header.put("productCode", prodCode);
                    JSONArray prodscutoff = getProductCutoffTime(session, prodCode);
                    for (int i = 0; i < prodscutoff.length(); i++) {
                        Date buDate = dayMonthYear.parse(businessDate);
                        Date activeDate = yearMonthDay.parse(prodscutoff.getJSONObject(i).getString("activeDate"));
                        Date expireDate = yearMonthDay.parse(prodscutoff.getJSONObject(i).getString("expireDate"));
                        if (activeDate.before(buDate) && expireDate.after(buDate)) {
                            header.put("productCutOffTime", prodscutoff.getJSONObject(i).getString("cutoff"));
                        }
                    }
                }
                JSONObject summary = new JSONObject();
                summary.put("prospect", new JSONObject().put("txnsStatus", "Prospect").put("noOfTxns", 0).put("limitAmount", "0.0"));
                summary.put("prospectOnline", new JSONObject().put("txnsStatus", "ProspectOnline").put("noOfTxns", 0).put("limitAmount", "0.0"));
                summary.put("waitForBookLoan", new JSONObject().put("txnsStatus", "Wait for Book Loan").put("noOfTxns", 0).put("limitAmount", "0.0"));
                summary.put("waitForTransfer", new JSONObject().put("txnsStatus", "Wait for Transfer").put("noOfTxns", 0).put("limitAmount", "0.0"));
                summary.put("transferFail", new JSONObject().put("txnsStatus", "Transfer Fail").put("noOfTxns", 0).put("limitAmount", "0.0"));
                summary.put("transferComplete", new JSONObject().put("txnsStatus", "Transfer Complete").put("noOfTxns", 0).put("limitAmount", "0.0"));
                summary.put("transferCompleteOnline", new JSONObject().put("txnsStatus", "Transfer Complete (Online)").put("noOfTxns", 0).put("limitAmount", "0.0"));
                summary.put("transferCompleteSchedule", new JSONObject().put("txnsStatus", "Transfer Complete (Schedule)").put("noOfTxns", 0).put("limitAmount", "0.0"));
                summary.put("transferFailOnline", new JSONObject().put("txnsStatus", "Transfer Fail (Online)").put("noOfTxns", 0).put("limitAmount", "0.0"));
                summary.put("transferFailSchedule", new JSONObject().put("txnsStatus", "Transfer Fail (Schedule)").put("noOfTxns", 0).put("limitAmount", "0.0"));
                summary.put("bookLoanCompleteOnline", new JSONObject().put("txnsStatus", "Book Loan Complete (Online) ").put("noOfTxns", 0).put("limitAmount", "0.0"));
                summary.put("bookLoanCompleteSchedule", new JSONObject().put("txnsStatus", "Book Loan Complete (Schedule)").put("noOfTxns", 0).put("limitAmount", "0.0"));
                summary.put("bookLoanFailOnline", new JSONObject().put("txnsStatus", "Book Loan Fail (Online)").put("noOfTxns", 0).put("limitAmount", "0.0"));
                summary.put("bookLoanFailSchedule", new JSONObject().put("txnsStatus", "Book Loan Fail (Schedule)").put("noOfTxns", 0).put("limitAmount", "0.0"));
                summary.put("cancel", new JSONObject().put("txnsStatus", "Cancel").put("noOfTxns", 0).put("limitAmount", "0.0"));
                detail.put("detail", new JSONArray());
                detail.put("summary", summary);
                detail.put("header", header);
                logObj.put(DateUtils.getDisplayEnDate(dayMonthYear.parse(businessDate), "dd-MM-yyyy"), detail);
                ret.put(logObj);
            } else {
                do {
//                while (rs.next()) {
                    SysOperLog sysOperLog = new SysOperLog();
                    sysOperLog.setUuid(rs.getString("uuid"));
                    sysOperLog.setTrnId(rs.getString("trn_id"));
                    sysOperLog.setSource(rs.getString("source"));
                    sysOperLog.setProductId(rs.getString("product_id"));
                    sysOperLog.setProductCode(rs.getString("product_code"));
                    sysOperLog.setProductVersionId(rs.getString("product_version_id"));
                    sysOperLog.setProductComponentId(rs.getString("product_component_id"));
                    sysOperLog.setTaskCategory(rs.getString("task_category"));
                    sysOperLog.setKeywords(rs.getString("keywords"));
                    sysOperLog.setTrnStatus(rs.getInt("trn_status"));
                    sysOperLog.setTrnSubStatus(rs.getInt("trn_sub_status"));
                    sysOperLog.setFailureReason(rs.getString("failure_reason"));
                    sysOperLog.setSourceDevice(rs.getString("source_device"));
                    sysOperLog.setSourceDeviceId(rs.getString("source_device_id"));
                    sysOperLog.setSourceCifId(rs.getString("source_cif_id"));
                    sysOperLog.setAccountName(rs.getString("account_name"));
                    sysOperLog.setBusinessDate(rs.getTimestamp("business_date"));
                    sysOperLog.setRefNo(rs.getString("ref_no"));
                    sysOperLog.setPaymentMethod(rs.getString("payment_method"));
                    sysOperLog.setPaymentDate(rs.getTimestamp("payment_date")); //paymentdate
                    sysOperLog.setStateCode(new ShelfLookup(rs.getString("state_code")));
                    sysOperLog.setProdChannel(rs.getString("prod_channel"));
                    sysOperLog.setStepData(rs.getString("step_data"));
                    sysOperLog.setCaseId(rs.getString("case_id"));
                    sysOperLog.setGroupProduct(rs.getString("group_product"));
                    sysOperLog.setTxnNo(rs.getString("txn_no"));
                    sysOperLog.setAttr1(rs.getString("attr1"));
                    sysOperLog.setAttr2(rs.getString("attr2"));
                    sysOperLog.setAttr3(rs.getString("attr3"));
                    sysOperLog.setAttr4(rs.getString("attr4"));
                    sysOperLog.setAttr5(rs.getString("attr5"));
                    sysOperLog.setAttr6(rs.getString("attr6"));
                    sysOperLog.setAttr7(rs.getString("attr7"));
                    sysOperLog.setAttr8(rs.getString("attr8"));
                    sysOperLog.setAttr9(rs.getString("attr9"));
                    sysOperLog.setAttr10(rs.getString("attr10"));
                    sysOperLog.setStatus(rs.getInt("status"));
                    sysOperLog.setCreateAt(rs.getTimestamp("create_at"));
                    sysOperLog.setCreateBy(rs.getString("create_by"));
                    sysOperLog.setStateTime(ValidUtils.str2BigInt(rs.getInt("state_time") + ""));
                    reconcileCmd.setLength(0);
//                        reconcileCmd.append("SELECT STEP_DATA, PAYMENT_METHOD FROM T_SYS_OPER_LOG WHERE TXN_NO = ? AND STATE_CODE = '71228c52-4322-11ea-b77f-2e728ce88125'");
//                    reconcileCmd.append("SELECT STEP_DATA, PAYMENT_METHOD FROM T_SYS_OPER_LOG WHERE TXN_NO = ? AND STATE_CODE = (SELECT UUID FROM T_SHELF_LOOKUP WHERE GROUP_TYPE = 'PROCESS_STATE' AND LOOKUP_CODE = 'PRO1011')"); //PRO1011
                    reconcileCmd.append("SELECT STEP_DATA, PAYMENT_METHOD FROM T_SYS_OPER_LOG WHERE TXN_NO = ? AND STATE_CODE in (SELECT UUID FROM T_SHELF_LOOKUP WHERE GROUP_TYPE = 'PROCESS_STATE' AND LOOKUP_CODE in ('PRO1011','PRO1032') )"); //PRO1011
                    psOperLog = session.doReturningWork((Connection conn) -> conn).prepareStatement(reconcileCmd.toString());
                    psOperLog.setString(1, sysOperLog.getTxnNo());
                    stepDataRs = psOperLog.executeQuery();
                    JSONObject stepData = new JSONObject();
                    stepData.put("paymentMethod", sysOperLog.getPaymentMethod());
                    JSONArray packageDataArr = new JSONArray();
                    while (stepDataRs.next()) {
                        byte[] decoded = Base64.decodeBase64(stepDataRs.getString("STEP_DATA"));
                        String stepDataIn = new String(decoded, "UTF-8");
                        if (!stepDataIn.isEmpty()) {
                            JSONArray stepDataArray = new JSONArray(stepDataIn);
                            for (int i = 0; i < stepDataArray.length(); i++) {
                                JSONObject stepDataObj = stepDataArray.getJSONObject(i);
                                if (stepDataObj.getJSONObject("data").has("package")) {
                                    packageDataArr.put(stepDataObj);
                                }
                                if (stepDataObj.has("code") && stepDataObj.getString("code").equalsIgnoreCase("loan")) {
                                    if (stepDataObj.getJSONObject("data").has("package")) {
                                        for (int j = 0; j < stepDataObj.getJSONObject("data").getJSONArray("package").length(); j++) {
                                            JSONObject packageData = stepDataObj.getJSONObject("data").getJSONArray("package").getJSONObject(j);
                                            if (packageData.has("parameter")) {
                                                switch (packageData.getString("parameter")) {
                                                    case "limit":
                                                        stepData.put("requestLimit", packageData.getString("value"));
                                                        break;
                                                    case "factor_2_from":
                                                        stepData.put("month", packageData.getInt("value") + "");
                                                        break;
                                                    case "calAmount1":
                                                        stepData.put("installmentPerMonth", packageData.getString("value"));
                                                        break;
                                                    case "rateAmount":
                                                        stepData.put("interestRate", packageData.getDouble("value") + "");
                                                        break;
                                                    case "paymentDate":
                                                        stepData.put("paymentDate", packageData.getString("value"));
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
                    if (!stepDataRs.isClosed()) {
                        stepDataRs.close();
                    }
                    if (!psOperLog.isClosed()) {
                        psOperLog.close();
                    }
                    if (logObj.has(DateUtils.getDisplayEnDate(sysOperLog.getBusinessDate(), "dd-MM-yyyy"))) {
                        JSONObject obj = new JSONObject();
                        JSONObject detail = logObj.getJSONObject(DateUtils.getDisplayEnDate(sysOperLog.getBusinessDate(), "dd-MM-yyyy"));
                        obj.put("txnsId", ValidUtils.null2NoData(sysOperLog.getTxnNo()))
                                .put("refNo", sysOperLog.getRefNo())
                                .put("txnsDateTime", DateUtils.getDisplayEnDate(sysOperLog.getCreateAt(), "dd/MM/yyyy HH:mm"))
                                .put("installmentDate", stepData.has("paymentDate") ? stepData.getString("paymentDate") : "")
                                .put("paymentDate", ValidUtils.null2NoData(DateUtils.getDisplayEnDate(sysOperLog.getPaymentDate(), "dd/MM/yyyy HH:mm")))
                                .put("requestLimit", stepData.has("requestLimit") ? stepData.getString("requestLimit") : "")
                                .put("month", stepData.has("month") ? stepData.getString("month") : "")
                                .put("installmentPerMonth", stepData.has("installmentPerMonth") ? stepData.getString("installmentPerMonth") : "")
                                .put("interestRate", stepData.has("interestRate") ? stepData.getString("interestRate") : "")
                                .put("paymentMethod", stepData.has("paymentMethod") ? stepData.getString("paymentMethod") : "")
                                .put("status", rs.getString("STATUS_NAME"))
                                .put("updatedDate", DateUtils.getDisplayEnDate(ValidUtils.str2Date(sysOperLog.getAttr4(), "yyyy-MM-dd HH:mm:ss"), "dd/MM/yyyy HH:mm"))
                                .put("state", rs.getString("STATE_NAME_TH"))
                                .put("cutoff", sysOperLog.getAttr5());
                        reconcileCmd.setLength(0);
                        reconcileCmd.append("SELECT LK_CODE,LK_LABEL,LK_VALUE FROM T_SHELF_PRODUCT_DTL WHERE 1=1 "
                                + " AND TRN_UUID IN (SELECT UUID FROM T_SHELF_PRODUCT_VCS VCS WHERE VCS.PROD_UUID = ? AND VER_PROD = ? )"
                                + " AND LK_CODE IN ('cutOffTime','pcutOffDay','pcutOffWE','pcutOffTime','pcutOffTAllD','pcutOffTSpec', 'prodName', 'prodType')");
                        psOperLog = session.doReturningWork((Connection conn) -> conn).prepareStatement(reconcileCmd.toString());
                        psOperLog.setString(1, sysOperLog.getProductId());
                        psOperLog.setInt(2, ValidUtils.str2BigInteger(sysOperLog.getProductVersionId()));
                        stepDataRs = psOperLog.executeQuery();
                        while (stepDataRs.next()) {
                            if (stepDataRs.getString("LK_CODE").equalsIgnoreCase("prodType")) {
                                obj.put("productType", stepDataRs.getString("LK_VALUE"));
                            }
//                            header.put("businessDate", DateUtils.getDisplayEnDate(sysOperLog.getBusinessDate(), "dd-MM-yyyy"));
                        }
                        if (!stepDataRs.isClosed()) {
                            stepDataRs.close();
                        }
                        if (!psOperLog.isClosed()) {
                            psOperLog.close();
                        }
                        if (rs.getString("ST_CODE").equalsIgnoreCase("PRO1013")) {//Prospect manual
                            JSONObject prospect = detail.getJSONObject("summary").getJSONObject("prospect");
                            prospect.put("noOfTxns", prospect.getInt("noOfTxns") + 1)
                                    .put("limitAmount", ValidUtils.priceToString(
                                            ((prospect.has("limitAmount") ? ValidUtils.obj2Double(prospect.getString("limitAmount")) : 0.0) + ValidUtils.obj2Double(stepData.has("requestLimit") ? stepData.getString("requestLimit") : "0.00"))
                                    //                                            / summary.getInt("noOfTxns")
                                    ));
                        } else if (rs.getString("ST_CODE").equalsIgnoreCase("PRO1050")) {//Prospect Online
                            JSONObject prospect = detail.getJSONObject("summary").getJSONObject("prospectOnline");
                            prospect.put("noOfTxns", prospect.getInt("noOfTxns") + 1)
                                    .put("limitAmount", ValidUtils.priceToString(
                                            ((prospect.has("limitAmount") ? ValidUtils.obj2Double(prospect.getString("limitAmount")) : 0.0) + ValidUtils.obj2Double(stepData.has("requestLimit") ? stepData.getString("requestLimit") : "0.00"))
                                    //                                            / summary.getInt("noOfTxns")
                                    ));
                        } else if (rs.getString("ST_CODE").equalsIgnoreCase("PRO1014")) {//Wait for Book Loan
                            JSONObject waitForBookLoan = detail.getJSONObject("summary").getJSONObject("waitForBookLoan");
                            waitForBookLoan.put("noOfTxns", waitForBookLoan.getInt("noOfTxns") + 1)
                                    .put("limitAmount", ValidUtils.priceToString(
                                            ((waitForBookLoan.has("limitAmount") ? ValidUtils.obj2Double(waitForBookLoan.getString("limitAmount")) : 0.0) + ValidUtils.obj2Double(stepData.has("requestLimit") ? stepData.getString("requestLimit") : "0.00"))
                                    //                                            / summary.getInt("noOfTxns")
                                    ));
                        } else if (rs.getString("ST_CODE").equalsIgnoreCase("PRO1015")) {//Wait for Transfer
                            JSONObject waitForTransfer = detail.getJSONObject("summary").getJSONObject("waitForTransfer");
                            waitForTransfer.put("noOfTxns", waitForTransfer.getInt("noOfTxns") + 1)
                                    .put("limitAmount", ValidUtils.priceToString(
                                            ((waitForTransfer.has("limitAmount") ? ValidUtils.obj2Double(waitForTransfer.getString("limitAmount")) : 0.0) + ValidUtils.obj2Double(stepData.has("requestLimit") ? stepData.getString("requestLimit") : "0.00"))
                                    //                                            / summary.getInt("noOfTxns")
                                    ));
                        } else if (rs.getString("ST_CODE").equalsIgnoreCase("PRO1016")) {//Transfer Fail
                            JSONObject transferFail = detail.getJSONObject("summary").getJSONObject("transferFail");
                            transferFail.put("noOfTxns", transferFail.getInt("noOfTxns") + 1)
                                    .put("limitAmount", ValidUtils.priceToString(
                                            ((transferFail.has("limitAmount") ? ValidUtils.obj2Double(transferFail.getString("limitAmount")) : 0.0) + ValidUtils.obj2Double(stepData.has("requestLimit") ? stepData.getString("requestLimit") : "0.00"))
                                    //                                            / summary.getInt("noOfTxns")
                                    ));
                        } else if (rs.getString("ST_CODE").equalsIgnoreCase("PRO1017")) {//Completed
                            JSONObject transferComplete = detail.getJSONObject("summary").getJSONObject("transferComplete");
                            transferComplete.put("noOfTxns", transferComplete.getInt("noOfTxns") + 1)
                                    .put("limitAmount", ValidUtils.priceToString(
                                            ((transferComplete.has("limitAmount") ? ValidUtils.obj2Double(transferComplete.getString("limitAmount")) : 0.0) + ValidUtils.obj2Double(stepData.has("requestLimit") ? stepData.getString("requestLimit") : "0.00"))
                                    //                                            / summary.getInt("noOfTxns")
                                    ));
                        } else if (rs.getString("ST_CODE").equalsIgnoreCase("PRO1051")) {//Completed Transfer Payment Oneline
                            JSONObject transferComplete = detail.getJSONObject("summary").getJSONObject("transferCompleteOnline");
                            transferComplete.put("noOfTxns", transferComplete.getInt("noOfTxns") + 1)
                                    .put("limitAmount", ValidUtils.priceToString(
                                            ((transferComplete.has("limitAmount") ? ValidUtils.obj2Double(transferComplete.getString("limitAmount")) : 0.0) + ValidUtils.obj2Double(stepData.has("requestLimit") ? stepData.getString("requestLimit") : "0.00"))
                                    //                                            / summary.getInt("noOfTxns")
                                    ));
                        } else if (rs.getString("ST_CODE").equalsIgnoreCase("PRO1054")) {//Completed Transfer Schedule
                            JSONObject transferComplete = detail.getJSONObject("summary").getJSONObject("transferCompleteSchedule");
                            transferComplete.put("noOfTxns", transferComplete.getInt("noOfTxns") + 1)
                                    .put("limitAmount", ValidUtils.priceToString(
                                            ((transferComplete.has("limitAmount") ? ValidUtils.obj2Double(transferComplete.getString("limitAmount")) : 0.0) + ValidUtils.obj2Double(stepData.has("requestLimit") ? stepData.getString("requestLimit") : "0.00"))
                                    //                                            / summary.getInt("noOfTxns")
                                    ));
                        } else if (rs.getString("ST_CODE").equalsIgnoreCase("PRO1052")) {//Fail Transfer Online
                            JSONObject transferComplete = detail.getJSONObject("summary").getJSONObject("transferFailOnline");
                            transferComplete.put("noOfTxns", transferComplete.getInt("noOfTxns") + 1)
                                    .put("limitAmount", ValidUtils.priceToString(
                                            ((transferComplete.has("limitAmount") ? ValidUtils.obj2Double(transferComplete.getString("limitAmount")) : 0.0) + ValidUtils.obj2Double(stepData.has("requestLimit") ? stepData.getString("requestLimit") : "0.00"))
                                    //                                            / summary.getInt("noOfTxns")
                                    ));
                        } else if (rs.getString("ST_CODE").equalsIgnoreCase("PRO1053")) {//Fail Transfer Schedule
                            JSONObject transferComplete = detail.getJSONObject("summary").getJSONObject("transferFailSchedule");
                            transferComplete.put("noOfTxns", transferComplete.getInt("noOfTxns") + 1)
                                    .put("limitAmount", ValidUtils.priceToString(
                                            ((transferComplete.has("limitAmount") ? ValidUtils.obj2Double(transferComplete.getString("limitAmount")) : 0.0) + ValidUtils.obj2Double(stepData.has("requestLimit") ? stepData.getString("requestLimit") : "0.00"))
                                    //                                            / summary.getInt("noOfTxns")
                                    ));
                        } else if (rs.getString("ST_CODE").equalsIgnoreCase("PRO1055")) {//Complete Bookloan Online
                            JSONObject transferComplete = detail.getJSONObject("summary").getJSONObject("bookLoanCompleteOnline");
                            transferComplete.put("noOfTxns", transferComplete.getInt("noOfTxns") + 1)
                                    .put("limitAmount", ValidUtils.priceToString(
                                            ((transferComplete.has("limitAmount") ? ValidUtils.obj2Double(transferComplete.getString("limitAmount")) : 0.0) + ValidUtils.obj2Double(stepData.has("requestLimit") ? stepData.getString("requestLimit") : "0.00"))
                                    //                                            / summary.getInt("noOfTxns")
                                    ));
                        } else if (rs.getString("ST_CODE").equalsIgnoreCase("PRO1056")) {//Complete Bookloan Schedule
                            JSONObject transferComplete = detail.getJSONObject("summary").getJSONObject("bookLoanCompleteSchedule");
                            transferComplete.put("noOfTxns", transferComplete.getInt("noOfTxns") + 1)
                                    .put("limitAmount", ValidUtils.priceToString(
                                            ((transferComplete.has("limitAmount") ? ValidUtils.obj2Double(transferComplete.getString("limitAmount")) : 0.0) + ValidUtils.obj2Double(stepData.has("requestLimit") ? stepData.getString("requestLimit") : "0.00"))
                                    //                                            / summary.getInt("noOfTxns")
                                    ));
                        } else if (rs.getString("ST_CODE").equalsIgnoreCase("PRO1057")) {//Fail Bookloan Schedule
                            JSONObject transferComplete = detail.getJSONObject("summary").getJSONObject("bookLoanFailOnline");
                            transferComplete.put("noOfTxns", transferComplete.getInt("noOfTxns") + 1)
                                    .put("limitAmount", ValidUtils.priceToString(
                                            ((transferComplete.has("limitAmount") ? ValidUtils.obj2Double(transferComplete.getString("limitAmount")) : 0.0) + ValidUtils.obj2Double(stepData.has("requestLimit") ? stepData.getString("requestLimit") : "0.00"))
                                    //                                            / summary.getInt("noOfTxns")
                                    ));
                        } else if (rs.getString("ST_CODE").equalsIgnoreCase("PRO1058")) {//Fail Bookloan Schedule
                            JSONObject transferComplete = detail.getJSONObject("summary").getJSONObject("bookLoanFailSchedule");
                            transferComplete.put("noOfTxns", transferComplete.getInt("noOfTxns") + 1)
                                    .put("limitAmount", ValidUtils.priceToString(
                                            ((transferComplete.has("limitAmount") ? ValidUtils.obj2Double(transferComplete.getString("limitAmount")) : 0.0) + ValidUtils.obj2Double(stepData.has("requestLimit") ? stepData.getString("requestLimit") : "0.00"))
                                    //                                            / summary.getInt("noOfTxns")
                                    ));
                        } else {  //Cancel
                            JSONObject cancel = detail.getJSONObject("summary").getJSONObject("cancel");
                            cancel.put("noOfTxns", cancel.getInt("noOfTxns") + 1)
                                    .put("limitAmount", ValidUtils.priceToString(
                                            ((cancel.has("limitAmount") ? ValidUtils.obj2Double(cancel.getString("limitAmount")) : 0.0) + ValidUtils.obj2Double(stepData.has("requestLimit") ? stepData.getString("requestLimit") : "0.00"))
                                    //                                            / summary.getInt("noOfTxns")
                                    ));
                        }
                        detail.getJSONArray("detail").put(obj);
                    } else {
                        JSONArray listDetail = new JSONArray();
                        JSONObject detail = new JSONObject();
                        JSONObject obj = new JSONObject();
                        JSONObject header = new JSONObject();
                        header.put("businessDate", "")
                                .put("productCode", "")
                                .put("productName", "")
                                .put("productCutOffTime", "");
                        obj.put("txnsId", ValidUtils.null2NoData(sysOperLog.getTxnNo()))
                                .put("refNo", sysOperLog.getRefNo())
                                .put("txnsDateTime", DateUtils.getDisplayEnDate(sysOperLog.getCreateAt(), "dd/MM/yyyy HH:mm"))
                                .put("installmentDate", stepData.has("paymentDate") ? stepData.getString("paymentDate") : "")
                                .put("paymentDate", ValidUtils.null2NoData(DateUtils.getDisplayEnDate(sysOperLog.getPaymentDate(), "dd/MM/yyyy HH:mm")))
                                .put("requestLimit", stepData.has("requestLimit") ? stepData.getString("requestLimit") : "")
                                .put("month", stepData.has("month") ? stepData.getString("month") : "")
                                .put("installmentPerMonth", stepData.has("installmentPerMonth") ? stepData.getString("installmentPerMonth") : "")
                                .put("interestRate", stepData.has("interestRate") ? stepData.getString("interestRate") : "")
                                //                                .put("productType", sysOperLog.getProductId())//getFrom product
                                .put("paymentMethod", stepData.has("paymentMethod") ? stepData.getString("paymentMethod") : "")
                                .put("status", rs.getString("STATUS_NAME"))
                                .put("updatedDate", DateUtils.getDisplayEnDate(ValidUtils.str2Date(sysOperLog.getAttr4(), "yyyy-MM-dd HH:mm:ss"), "dd/MM/yyyy HH:mm"))
                                .put("state", rs.getString("STATE_NAME_TH"))
                                .put("cutoff", sysOperLog.getAttr5());
                        reconcileCmd.setLength(0);
                        reconcileCmd.append("SELECT LK_CODE,LK_LABEL,LK_VALUE FROM T_SHELF_PRODUCT_DTL WHERE 1=1 "
                                + " AND TRN_UUID IN (SELECT UUID FROM T_SHELF_PRODUCT_VCS VCS WHERE VCS.PROD_UUID = ? AND VER_PROD = ? )"
                                + " AND LK_CODE IN ('cutOffTime','pcutOffDay','pcutOffWE','pcutOffTime','pcutOffTAllD','pcutOffTSpec', 'prodName', 'prodType')");
                        psOperLog = session.doReturningWork((Connection conn) -> conn).prepareStatement(reconcileCmd.toString());
                        psOperLog.setString(1, sysOperLog.getProductId());
                        psOperLog.setInt(2, ValidUtils.str2BigInteger(sysOperLog.getProductVersionId()));
                        stepDataRs = psOperLog.executeQuery();
                        while (stepDataRs.next()) {
                            if (stepDataRs.getString("LK_CODE").equalsIgnoreCase("prodName")) {
                                header.put("productName", stepDataRs.getString("LK_VALUE"));
                            } else if (stepDataRs.getString("LK_CODE").equalsIgnoreCase("pcutOffTSpec") || stepDataRs.getString("LK_CODE").equalsIgnoreCase("pcutOffTAllD")) {
                                if (stepDataRs.getString("LK_CODE").equalsIgnoreCase("pcutOffTAllD")) {
                                    cutOff = stepDataRs.getString("LK_VALUE");
                                } else {
                                    if (!stepDataRs.getString("LK_VALUE").isEmpty() && (cutOff.equalsIgnoreCase("cutoff")) || cutOff.isEmpty()) {
                                        header.put("productCutOffTime", stepDataRs.getString("LK_VALUE"));
                                    } else {
                                        header.put("productCutOffTime", "All Day");
                                    }
                                }
                            } else if (stepDataRs.getString("LK_CODE").equalsIgnoreCase("prodType")) {
                                obj.put("productType", stepDataRs.getString("LK_VALUE"));
                            }
                            header.put("businessDate", DateUtils.getDisplayEnDate(sysOperLog.getBusinessDate(), "dd-MM-yyyy"));
                        }
                        if (!stepDataRs.isClosed()) {
                            stepDataRs.close();
                        }
                        if (!psOperLog.isClosed()) {
                            psOperLog.close();
                        }
                        header.put("productCode", sysOperLog.getProductCode());
                        JSONObject summary = new JSONObject();
                        summary.put("prospect", new JSONObject().put("txnsStatus", "Prospect").put("noOfTxns", 0).put("limitAmount", "0.0"));
                        summary.put("prospectOnline", new JSONObject().put("txnsStatus", "ProspectOnline").put("noOfTxns", 0).put("limitAmount", "0.0"));
                        summary.put("waitForBookLoan", new JSONObject().put("txnsStatus", "Wait for Book Loan").put("noOfTxns", 0).put("limitAmount", "0.0"));
                        summary.put("waitForTransfer", new JSONObject().put("txnsStatus", "Wait for Transfer").put("noOfTxns", 0).put("limitAmount", "0.0"));
                        summary.put("transferFail", new JSONObject().put("txnsStatus", "Transfer Fail").put("noOfTxns", 0).put("limitAmount", "0.0"));
                        summary.put("transferComplete", new JSONObject().put("txnsStatus", "Transfer Complete").put("noOfTxns", 0).put("limitAmount", "0.0"));
                        summary.put("cancel", new JSONObject().put("txnsStatus", "Cancel").put("noOfTxns", 0).put("limitAmount", "0.0"));
                        if (rs.getString("ST_CODE").equalsIgnoreCase("PRO1013")) {//Prospect
                            summary.put("prospect", new JSONObject().put("txnsStatus", "Prospect").put("noOfTxns", 1).put("limitAmount", stepData.has("requestLimit") ? stepData.getString("requestLimit") : "0.0"));
                        } else if (rs.getString("ST_CODE").equalsIgnoreCase("PRO1050")) {//Prospect Online
                            summary.put("prospectOnline", new JSONObject().put("txnsStatus", "ProspectOnline").put("noOfTxns", 1).put("limitAmount", stepData.has("requestLimit") ? stepData.getString("requestLimit") : "0.0"));
                        } else if (rs.getString("ST_CODE").equalsIgnoreCase("PRO1014")) {//Wait for Book Loan
                            summary.put("waitForBookLoan", new JSONObject().put("txnsStatus", "Wait for Book Loan").put("noOfTxns", 1).put("limitAmount", stepData.has("requestLimit") ? stepData.getString("requestLimit") : "0.0"));
                        } else if (rs.getString("ST_CODE").equalsIgnoreCase("PRO1015")) {//Wait for Transfer
                            summary.put("waitForTransfer", new JSONObject().put("txnsStatus", "Wait for Transfer").put("noOfTxns", 1).put("limitAmount", stepData.has("requestLimit") ? stepData.getString("requestLimit") : "0.0"));
                        } else if (rs.getString("ST_CODE").equalsIgnoreCase("PRO1016")) {//Transfer Fail
                            summary.put("transferFail", new JSONObject().put("txnsStatus", "Transfer Fail").put("noOfTxns", 1).put("limitAmount", stepData.has("requestLimit") ? stepData.getString("requestLimit") : "0.0"));
                        } else if (rs.getString("ST_CODE").equalsIgnoreCase("PRO1017")) {//Completed
                            summary.put("transferComplete", new JSONObject().put("txnsStatus", "Transfer Complete").put("noOfTxns", 1).put("limitAmount", stepData.has("requestLimit") ? stepData.getString("requestLimit") : "0.0"));
                        } else {
                            summary.put("cancel", new JSONObject().put("txnsStatus", "Cancel").put("noOfTxns", 1).put("limitAmount", stepData.has("requestLimit") ? stepData.getString("requestLimit") : "0.0"));
                        }
                        obj.put("package", packageDataArr);
                        listDetail.put(obj);
                        detail.put("detail", listDetail);
                        detail.put("summary", summary);
                        detail.put("header", header);
                        logObj.put(DateUtils.getDisplayEnDate(sysOperLog.getBusinessDate(), "dd-MM-yyyy"), detail);
                    }
//                        }
                } while (rs.next());

                ret.put(logObj);
            }
        } catch (HibernateException | NullPointerException e) {
            logger.info(e.getMessage());
            throw e;
        } finally {
            if (stepDataRs != null && !stepDataRs.isClosed()) {
                stepDataRs.close();
            }
            if (psOperLog != null && !psOperLog.isClosed()) {
                psOperLog.close();
            }
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
        return ret;

    }

    private JSONArray getProductCutoffTime(Session session, String prodid) throws SQLException {
        JSONArray arr = new JSONArray();
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            HashMap hmap = new HashMap();
            StringBuilder dashBoardCmd = new StringBuilder();
            dashBoardCmd.append("select vcs.prod_uuid, vcs.ver_prod ,dtl.LK_CODE,dtl.LK_VALUE, dtl.END_DATE AS EXPIRE_DATE "
                    + "from t_shelf_product_vcs vcs, T_SHELF_PRODUCT_DTL dtl "
                    + "where vcs.comp_uuid is null "
                    + "and vcs.uuid = dtl.trn_uuid "
                    + "and vcs.prod_uuid in (select uuid from t_shelf_product where prod_code = ? and attr2 ='Y') "
                    + "and dtl.lk_code in ('activeDate','endDate') "
                    + "union all "
                    + "select vcs.prod_uuid, vcs.ver_prod ,dtl.LK_CODE,dtl.LK_VALUE, dtl.END_DATE AS EXPIRE_DATE "
                    + "from t_shelf_product_vcs vcs, T_SHELF_PRODUCT_DTL dtl "
                    + "where vcs.comp_uuid in (select uuid from t_shelf_comp where comp_name = 'PRODUCT INFO')  "
                    + "and vcs.uuid = dtl.trn_uuid "
                    + "and vcs.prod_uuid in (select uuid from t_shelf_product where prod_code = ? and attr2 ='Y') "
                    + "and dtl.lk_code in ('pcutOffTAllD','pcutOffTSpec')");
            ps = session.doReturningWork((Connection conn) -> conn).prepareStatement(dashBoardCmd.toString());
            ps.setString(1, prodid);
            ps.setString(2, prodid);
            rs = ps.executeQuery();
            while (rs.next()) {
                String prodUuid = ValidUtils.null2NoData(rs.getString("prod_uuid"));
                String prodV = ValidUtils.null2NoData(rs.getInt("ver_prod"));
                String idVerProd = prodUuid + "_" + prodV;
                JSONObject obj = (JSONObject) hmap.get(idVerProd);
                if (null == obj) {
                    obj = new JSONObject();
                    obj.put("activeDate", "")
                            .put("expireDate", "")
                            .put("endDate", "")
                            .put("pcutOffTAllD", "")
                            .put("pcutOffTSpec", "");
                }
                obj.put("prodid", prodUuid)
                        .put("prodver", prodV);
                if ("activeDate".equalsIgnoreCase(rs.getString("LK_CODE"))) {
                    obj.put("activeDate", ValidUtils.null2NoData(rs.getString("LK_VALUE")));
                    obj.put("expireDate", ValidUtils.null2NoData(DateUtils.getDisplayEnDate(rs.getTimestamp("EXPIRE_DATE"), "yyyy-MM-dd")));  //dd/MM/yyyy HH:mm:ssS
                } else if ("endDate".equalsIgnoreCase(rs.getString("LK_CODE"))) {
                    obj.put("endDate", ValidUtils.null2NoData(rs.getString("LK_VALUE")));
                } else if ("pcutOffTAllD".equalsIgnoreCase(rs.getString("LK_CODE"))) {
                    obj.put("pcutOffTAllD", ValidUtils.null2NoData(rs.getString("LK_VALUE")));
                } else if ("pcutOffTSpec".equalsIgnoreCase(rs.getString("LK_CODE"))) {
                    obj.put("pcutOffTSpec", ValidUtils.null2NoData(rs.getString("LK_VALUE")));
                }
                hmap.put(idVerProd, obj);
            }
            for (Object kName : hmap.keySet()) {
                JSONObject tmp = (JSONObject) hmap.get(kName);
                JSONObject data = new JSONObject();
                data.put("prodid", tmp.getString("prodid"))
                        .put("prodver", tmp.getString("prodver"))
                        .put("activeDate", tmp.getString("activeDate"));
                if (!tmp.getString("expireDate").isEmpty()) {
                    data.put("expireDate", tmp.getString("expireDate"));
                } else {
                    data.put("expireDate", tmp.getString("endDate"));
                }
                if (!tmp.getString("pcutOffTAllD").isEmpty() && "cutoff".equalsIgnoreCase(tmp.getString("pcutOffTAllD"))) {
                    data.put("cutoff", tmp.getString("pcutOffTSpec"));
                } else {
                    data.put("cutoff", "All Day");
                }
                arr.put(data);
            }
        } catch (HibernateException | NullPointerException e) {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            logger.info(e.getMessage());
            throw e;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
        return arr;
    }

    public JSONArray getListComponentByCaseId(String dbEnv) throws SQLException {
        JSONArray jsonArr = new JSONArray();
        PreparedStatement psLog = null, ps = null;
        ResultSet logRs = null, rs = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            Integer inactive = StatusUtils.getInActive(dbEnv).getStatusCode();
            Integer active = StatusUtils.getActive(dbEnv).getStatusCode();
            StringBuilder productCmd = new StringBuilder();
            productCmd.append("select uuid, group_product, prod_code,case_id,attr1 as ucid from t_sys_log where status <> ?");
            psLog = session.doReturningWork((Connection conn) -> conn).prepareStatement(productCmd.toString());
            psLog.setInt(1, inactive);
            logRs = psLog.executeQuery();
            while (logRs.next()) {
                List<JSONObject> arr = new ArrayList<>();
                JSONObject json = new JSONObject();
                json.put("id", ValidUtils.null2NoData(logRs.getString("uuid")))
                        .put("group", ValidUtils.null2NoData(logRs.getString("group_product")))
                        .put("code", ValidUtils.null2NoData(logRs.getString("prod_code")))
                        .put("caseid", ValidUtils.null2NoData(logRs.getString("case_id")))
                        .put("ucid", ValidUtils.null2NoData(logRs.getString("ucid")));
                productCmd.setLength(0);
                productCmd.append("select tdtl.comp_uuid,tdtl.seq_no,sc.comp_code, sc.comp_name "
                        + "from t_shelf_tmp_detail tdtl "
                        + "inner join t_shelf_comp sc on tdtl.comp_uuid = sc.uuid "
                        + "inner join t_shelf_tmp_vcs vcs on tdtl.vcs_uuid = vcs.uuid "
                        + "inner join ( "
                        + "	select comp_uuid, tem_uuid,ver_tem ,comp_status "
                        + "	from t_shelf_product_vcs vcs "
                        + "	inner join ( "
                        + "			select * from t_sys_oper_log "
                        + "			where case_id = ? "
                        + "			and create_at = (select max(create_at) from t_sys_oper_log where case_id = ? ) "
                        + "			limit 1 		   "
                        + "	) s on vcs.ver_prod::text = s.product_version_id "
                        + "	where vcs.ver_prod::text = s.product_version_id "
                        + "	and vcs.prod_uuid = s.product_id "
                        + ")s3 on tdtl.comp_uuid = s3.comp_uuid "
                        + "where vcs.tmp_uuid = s3.tem_uuid "
                        + "and vcs.version = s3.ver_tem "
                        + "and s3.comp_status = ? "
                        + "order by tdtl.seq_no ");
                ps = session.doReturningWork((Connection conn) -> conn).prepareStatement(productCmd.toString());
                ps.setString(1, json.getString("caseid"));
                ps.setString(2, json.getString("caseid"));
                ps.setInt(3, active);
                rs = ps.executeQuery();
                while (rs.next()) {
                    JSONObject ret = new JSONObject()
                            .put("compCode", ValidUtils.null2NoData(rs.getString("comp_code")))
                            .put("compName", ValidUtils.null2NoData(rs.getString("comp_name")))
                            .put("seqNo", ValidUtils.obj2Integer(rs.getInt("seq_no")));
                    arr.add(ret);
                }
                if (!rs.isClosed()) {
                    rs.close();
                }
                if (!ps.isClosed()) {
                    ps.close();
                }
                json.put("components", arr);
                if (!arr.isEmpty()) {
                    jsonArr.put(json);
                }
            }
            if (!logRs.isClosed()) {
                logRs.close();
            }
            if (!psLog.isClosed()) {
                psLog.close();
            }
        } catch (HibernateException | NullPointerException e) {
            logger.info(e.getMessage());
            throw e;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (logRs != null && !logRs.isClosed()) {
                logRs.close();
            }
            if (psLog != null && !psLog.isClosed()) {
                psLog.close();
            }
        }
        return jsonArr;
    }

    public JSONObject getTransferTransaction(String dbEnv, String compCode, String groupProduct, String ucId, String prodCode, String refNo, String paymentMethod, String txnId, String txnDateStart,
            String txnStartTime, String txnDateEnd, String txnEndTime, Integer status, String state, String paymentDateStart, String paymentDateEnd, String refTxnId, String traceNo, String minAmount,
            String maxAmount, Integer prospect) throws SQLException, UnsupportedEncodingException, ParseException {
        JSONObject ret = new JSONObject();
        PreparedStatement ps = null, psOperLog = null;
        ResultSet rs = null, stepDataRs = null;
        String cutOff = "";
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            StringBuilder transferCmd = new StringBuilder();
            List params = new ArrayList<>();
            transferCmd.append("select log.* ,LK.LOOKUP_CODE ST_CODE, LK.LOOKUP_NAME_EN STATE_NAME, LK.LOOKUP_NAME_TH, SL.LOOKUP_NAME_EN STATUS_NAME, SP.COMPANY COMPANY ,LK2.LOOKUP_NAME_TH ERR_NAME_TH, LK2.LOOKUP_NAME_EN ERR_NAME_EN, LK2.DESCRIPTION ERR_DESC, COMP.COMP_NAME COMPNAME "
                    + "from t_sys_oper_log LOG "
                    + "INNER JOIN T_SHELF_LOOKUP LK ON LOG.STATE_CODE = LK.UUID  "
                    + "INNER JOIN T_SYS_LOOKUP SL ON LOG.STATUS::TEXT = SL.LOOKUP_CODE "
                    + "INNER JOIN T_SHELF_PRODUCT SP ON LOG.PRODUCT_ID = SP.UUID "
                    + "INNER JOIN T_SYS_LOOKUP SL2 ON LOG.TRN_STATUS::TEXT = SL2.LOOKUP_CODE "
                    + "INNER JOIN T_SHELF_COMP COMP ON LOG.PRODUCT_COMPONENT_ID = COMP.UUID "
                    + "LEFT JOIN T_SHELF_LOOKUP LK2 ON LOG.ATTR2 = LK2.LOOKUP_CODE "
                    + "WHERE LOG.PRODUCT_COMPONENT_ID = COMP.UUID ");
            if (null != state && !state.isEmpty()) {
                transferCmd.append(" AND LOG.STATE_CODE = (SELECT UUID FROM T_SHELF_LOOKUP WHERE GROUP_TYPE = 'PROCESS_STATE' AND LOOKUP_CODE = ?) ");
                params.add(state);
            }
            if (null != status) {
                transferCmd.append(" AND LOG.STATUS = ? ");
                params.add(status);
            }
            if (null != compCode && !compCode.isEmpty()) {
                transferCmd.append(" AND LOG.PRODUCT_COMPONENT_ID = ? ");
                params.add(compCode);
            }
            if (null != groupProduct && !groupProduct.isEmpty()) {
                transferCmd.append(" AND LOG.GROUP_PRODUCT = ? ");
                params.add(groupProduct);
            }
            if (null != prodCode && !prodCode.isEmpty()) {
                transferCmd.append(" AND LOG.PRODUCT_CODE = ? ");
                params.add(prodCode);
            }
            if (null != txnId && !txnId.isEmpty()) {
                transferCmd.append(" AND LOWER(LOG.TXN_NO) LIKE ? ");
                params.add("%" + txnId.toLowerCase() + "%");
            }
            if (null != refNo && !refNo.isEmpty()) {
                transferCmd.append(" AND LOG.REF_NO LIKE ? ");
                params.add("%" + refNo + "%");
            }
            if (null != ucId && !ucId.isEmpty()) {
                String id[] = ucId.replace("(", "").replace(")", "").replace("'", "").split(",");
                StringBuilder subCmd = new StringBuilder();
                subCmd.append(" AND LOG.ATTR1 in ( ? ");

                for (int i = 1; i < id.length; i++) {
                    subCmd.append(", ? ");
                }
                subCmd.append(") ");
                for (int i = 0; i < id.length; i++) {
                    params.add(id[i]);
                }
                transferCmd.append(subCmd);
            }
            if (null != refTxnId && !refTxnId.isEmpty()) {
                String id[] = refTxnId.replace("(", "").replace(")", "").replace("'", "").split(",");
                StringBuilder subCmd = new StringBuilder();
                subCmd.append(" AND LOWER(LOG.TXN_NO) in ( ? ");
                for (int i = 1; i < id.length; i++) {
                    subCmd.append(", ? ");
                }
                subCmd.append(") ");
                for (int i = 0; i < id.length; i++) {
                    params.add(id[i].toLowerCase());
                }
                transferCmd.append(subCmd);
            }
            if (null != paymentMethod && !paymentMethod.isEmpty()) {
                transferCmd.append(" AND LOG.PAYMENT_METHOD = ? ");
                params.add(paymentMethod);
            }
            if (null != txnStartTime && !txnStartTime.isEmpty()) {
                txnStartTime += ":00";
            } else {
                txnStartTime = "00:00:00";
            }
            if (null != txnEndTime && !txnEndTime.isEmpty()) {
                txnEndTime += ":59";
            } else {
                txnEndTime = "23:59:59";
            }
            if ((null != txnDateStart && !txnDateStart.isEmpty()) && (null != txnDateEnd && !txnDateEnd.isEmpty())) {
                transferCmd.append(" AND LOG.CREATE_AT BETWEEN TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS') AND TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(txnDateStart + " " + txnStartTime);
                params.add(txnDateEnd + " " + txnEndTime);
            } else if ((null == txnDateStart || txnDateStart.isEmpty()) && (null != txnDateEnd && !txnDateEnd.isEmpty())) {
                transferCmd.append(" AND LOG.CREATE_AT <= TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(txnDateEnd + " " + txnEndTime);
            } else if ((null != txnDateStart && !txnDateStart.isEmpty()) && (null == txnDateEnd || txnDateEnd.isEmpty())) {
                transferCmd.append(" AND LOG.CREATE_AT >= TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(txnDateStart + " " + txnStartTime);
            }
            if ((null != paymentDateStart && !paymentDateStart.isEmpty()) && (null != paymentDateEnd && !paymentDateEnd.isEmpty())) {
                transferCmd.append(" AND LOG.PAYMENT_DATE BETWEEN TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS') AND TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(paymentDateStart + " 00:00:00");
                params.add(paymentDateEnd + " 23:59:59");
            } else if ((null == paymentDateStart || paymentDateStart.isEmpty()) && (null != paymentDateEnd && !paymentDateEnd.isEmpty())) {
                transferCmd.append(" AND LOG.PAYMENT_DATE <= TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(paymentDateEnd + " 23:59:59");
            } else if ((null != paymentDateStart && !paymentDateStart.isEmpty()) && (null == paymentDateEnd || paymentDateEnd.isEmpty())) {
                transferCmd.append(" AND LOG.PAYMENT_DATE >= TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(paymentDateStart + " 00:00:00");
            }
            if (null != traceNo && !traceNo.isEmpty()) {
                transferCmd.append(" AND LOG.ATTR6 = ? ");
                params.add(traceNo);
            }
            if ((null != minAmount && !minAmount.isEmpty()) && (null != maxAmount && !maxAmount.isEmpty())) {
                transferCmd.append(" AND CAST (LOG.ATTR7 AS DOUBLE PRECISION) >= ? ");
                transferCmd.append(" AND CAST (LOG.ATTR7 AS DOUBLE PRECISION) <= ? ");
                params.add(ValidUtils.str2Dec(minAmount));
                params.add(ValidUtils.str2Dec(maxAmount));
            } else if ((null != minAmount && !minAmount.isEmpty()) && (null == maxAmount || maxAmount.isEmpty())) {
                transferCmd.append(" AND CAST (LOG.ATTR7 AS DOUBLE PRECISION) >= ? ");
                params.add(ValidUtils.str2Dec(minAmount));
            } else if ((null != maxAmount && !maxAmount.isEmpty()) && (null == minAmount || minAmount.isEmpty())) {
                transferCmd.append(" AND CAST (LOG.ATTR7 AS DOUBLE PRECISION) <= ? ");
                params.add(ValidUtils.str2Dec(maxAmount));
            }
            transferCmd.append(" ORDER BY LOG.CREATE_AT ASC ");
            ps = session.doReturningWork((Connection conn) -> conn).prepareStatement(transferCmd.toString());
            if (params.size() > 0) {
                for (int i = 0; i < params.size(); i++) {
                    if (params.get(i) instanceof String) {
                        ps.setString(i + 1, (String) params.get(i));
                    } else if (params.get(i) instanceof BigDecimal) {
                        ps.setBigDecimal(i + 1, (BigDecimal) params.get(i));
                    } else {
                        ps.setInt(i + 1, (Integer) params.get(i));
                    }
                }
            }
            rs = ps.executeQuery();
            JSONArray listDetail = new JSONArray();
            JSONObject header = new JSONObject();
            while (rs.next()) {
                SysOperLog sysOperLog = new SysOperLog();
                sysOperLog.setUuid(rs.getString("uuid"));
                sysOperLog.setTrnId(rs.getString("trn_id"));
                sysOperLog.setSource(rs.getString("source"));
                sysOperLog.setProductId(rs.getString("product_id"));
                sysOperLog.setProductCode(rs.getString("product_code"));
                sysOperLog.setProductVersionId(rs.getString("product_version_id"));
                sysOperLog.setProductComponentId(rs.getString("product_component_id"));
                sysOperLog.setTaskCategory(rs.getString("task_category"));
                sysOperLog.setKeywords(rs.getString("keywords"));
                sysOperLog.setTrnStatus(rs.getInt("trn_status"));
                sysOperLog.setTrnSubStatus(rs.getInt("trn_sub_status"));
                sysOperLog.setFailureReason(rs.getString("failure_reason"));
                sysOperLog.setSourceDevice(rs.getString("source_device"));
                sysOperLog.setSourceDeviceId(rs.getString("source_device_id"));
                sysOperLog.setSourceCifId(rs.getString("source_cif_id"));
                sysOperLog.setAccountName(rs.getString("account_name"));
                sysOperLog.setBusinessDate(rs.getTimestamp("business_date"));
                sysOperLog.setRefNo(rs.getString("ref_no"));
                sysOperLog.setPaymentMethod(rs.getString("payment_method"));
                sysOperLog.setPaymentDate(rs.getTimestamp("payment_date"));//paymentdate
                sysOperLog.setStateCode(new ShelfLookup(rs.getString("state_code")));
                sysOperLog.setProdChannel(rs.getString("prod_channel"));
                sysOperLog.setStepData(rs.getString("step_data"));
                sysOperLog.setCaseId(rs.getString("case_id"));
                sysOperLog.setGroupProduct(rs.getString("group_product"));
                sysOperLog.setTxnNo(rs.getString("txn_no"));
                sysOperLog.setAttr1(rs.getString("attr1"));
                sysOperLog.setAttr2(rs.getString("attr2"));
                sysOperLog.setAttr3(rs.getString("attr3"));
                sysOperLog.setAttr4(rs.getString("attr4"));
                sysOperLog.setAttr5(rs.getString("attr5"));
                sysOperLog.setAttr6(rs.getString("attr6"));
                sysOperLog.setAttr7(rs.getString("attr7"));
                sysOperLog.setAttr8(rs.getString("attr8"));
                sysOperLog.setAttr9(rs.getString("attr9"));
                sysOperLog.setAttr10(rs.getString("attr10"));
                sysOperLog.setStatus(rs.getInt("status"));
                sysOperLog.setCreateAt(rs.getTimestamp("create_at"));
                sysOperLog.setCreateBy(rs.getString("create_by"));
                sysOperLog.setStateTime(ValidUtils.str2BigInt(rs.getInt("state_time") + ""));
                String errNameTh = rs.getString("ERR_NAME_TH");
                String errNameEn = rs.getString("ERR_NAME_EN");
                String errNameDesc = rs.getString("ERR_DESC");
                transferCmd.setLength(0);
//                transferCmd.append("SELECT STEP_DATA, PAYMENT_METHOD FROM T_SYS_OPER_LOG WHERE TXN_NO = ? AND STATE_CODE = (SELECT UUID FROM T_SHELF_LOOKUP WHERE GROUP_TYPE = 'PROCESS_STATE' AND LOOKUP_CODE = 'PRO1011')"); //PRO1011
                transferCmd.append("SELECT STEP_DATA, PAYMENT_METHOD FROM T_SYS_OPER_LOG WHERE TXN_NO = ? AND STATE_CODE in (SELECT UUID FROM T_SHELF_LOOKUP WHERE GROUP_TYPE = 'PROCESS_STATE' AND LOOKUP_CODE in ('PRO1011','PRO1032'))"); //PRO1011
                psOperLog = session.doReturningWork((Connection conn) -> conn).prepareStatement(transferCmd.toString());
                psOperLog.setString(1, sysOperLog.getTxnNo());
                stepDataRs = psOperLog.executeQuery();
                JSONObject stepData = new JSONObject();
                stepData.put("paymentMethod", sysOperLog.getPaymentMethod());
                JSONArray packageDataArr = new JSONArray();
                while (stepDataRs.next()) {
                    byte[] decoded = Base64.decodeBase64(stepDataRs.getString("STEP_DATA"));
                    String stepDataIn = new String(decoded, "UTF-8");
                    if (!stepDataIn.isEmpty()) {
                        JSONArray stepDataArray = new JSONArray(stepDataIn);
                        for (int i = 0; i < stepDataArray.length(); i++) {
                            JSONObject stepDataObj = stepDataArray.getJSONObject(i);
                            if (stepDataObj.getJSONObject("data").has("package")) {
                                packageDataArr.put(stepDataObj);
                            }
                            if (stepDataObj.has("code") && stepDataObj.getString("code").equalsIgnoreCase("loan")) {
                                if (stepDataObj.getJSONObject("data").has("package")) {
                                    JSONArray packageDataArrLoan = stepDataObj.getJSONObject("data").getJSONArray("package");
                                    for (int j = 0; j < packageDataArrLoan.length(); j++) {
                                        JSONObject packageData = packageDataArrLoan.getJSONObject(j);
                                        if (packageData.has("parameter")) {
                                            switch (packageData.getString("parameter")) {
                                                case "limit":
                                                    stepData.put("requestLimit", packageData.getString("value"));
                                                    break;
                                                case "factor_2_from":
                                                    stepData.put("month", packageData.getInt("value") + "");
                                                    break;
                                                case "calAmount1":
                                                    stepData.put("installmentPerMonth", packageData.getString("value"));
                                                    break;
                                                case "rateAmount":
                                                    stepData.put("interestRate", packageData.getDouble("value") + "");
                                                    break;
                                                case "paymentDate":
                                                    stepData.put("paymentDate", packageData.getString("value"));
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
                if (!stepDataRs.isClosed()) {
                    stepDataRs.close();
                }
                if (!psOperLog.isClosed()) {
                    psOperLog.close();
                }
                if (ret.has("detail")) {
                    JSONObject obj = new JSONObject();
                    obj.put("txnsId", ValidUtils.null2NoData(sysOperLog.getTxnNo()))
                            .put("refNo", sysOperLog.getRefNo())
                            .put("txnsDateTime", DateUtils.getDisplayEnDate(sysOperLog.getCreateAt(), "dd/MM/yyyy HH:mm"))
                            .put("installmentDate", stepData.has("paymentDate") ? stepData.getString("paymentDate") : "")
                            .put("paymentDate", ValidUtils.null2NoData(DateUtils.getDisplayEnDate(sysOperLog.getPaymentDate(), "dd/MM/yyyy HH:mm")))
                            .put("businessDate", ValidUtils.null2NoData(DateUtils.getDisplayEnDate(sysOperLog.getBusinessDate(), "dd-MM-yyyy")))
                            .put("requestLimit", stepData.has("requestLimit") ? stepData.getString("requestLimit") : "")
                            .put("month", stepData.has("month") ? stepData.getString("month") : "")
                            .put("installmentPerMonth", stepData.has("installmentPerMonth") ? stepData.getString("installmentPerMonth") : "")
                            .put("interestRate", stepData.has("interestRate") ? stepData.getString("interestRate") : "")
                            .put("paymentMethod", stepData.has("paymentMethod") ? stepData.getString("paymentMethod") : "")
                            .put("status", rs.getString("STATUS_NAME"))
                            .put("state", rs.getString("STATE_NAME"))
                            .put("procError", ValidUtils.null2NoData(sysOperLog.getAttr2()))
                            .put("procErrNameTh", ValidUtils.null2NoData(errNameTh))
                            .put("procErrNameEn", ValidUtils.null2NoData(errNameEn))
                            .put("procErrDesc", ValidUtils.null2NoData(errNameDesc))
                            .put("ucId", ValidUtils.null2NoData(sysOperLog.getAttr1()))
                            .put("updatedDate", DateUtils.getDisplayEnDate(ValidUtils.str2Date(sysOperLog.getAttr4(), "yyyy-MM-dd HH:mm:ss"), "dd/MM/yyyy HH:mm"))
                            .put("agreementDate", ValidUtils.null2NoData(sysOperLog.getAttr3()))
                            .put("trnStatus", ValidUtils.null2NoData(sysOperLog.getTrnStatus()))
                            .put("failureReason", ValidUtils.null2NoData(sysOperLog.getFailureReason()))
                            .put("traceNo", ValidUtils.null2NoData(sysOperLog.getAttr6()))
                            .put("minMax", ValidUtils.null2NoData(sysOperLog.getAttr7()))
                            .put("compName", !rs.getString("COMPNAME").isEmpty() ? rs.getString("COMPNAME") : "");
                    if (packageDataArr.length() == 0) {
                        String packageData = "[{'respDesc': '', 'code': '', 'data': { 'payDuedate': '', 'buDay': '', 'package': [], 'hpDueDate': '', 'channel': '', 'limit': '', 'hpNo': '', 'discount': '', 'term': '', 'payDate': '', 'lastDueDate': '' }, 'respCode': '', 'status': ''}]";
                        packageDataArr = new JSONArray(packageData);
                        transferCmd.setLength(0);
                        transferCmd.append("SELECT LK_VALUE PACKAGE_DATA FROM T_SHELF_PRODUCT_DTL "
                                + "WHERE TRN_UUID IN (SELECT UUID FROM T_SHELF_PRODUCT_VCS WHERE PROD_UUID = ? AND VER_PROD = ?) "
                                + "AND LK_CODE = 'summaryList'");
                        psOperLog = session.doReturningWork((Connection conn) -> conn).prepareStatement(transferCmd.toString());
                        psOperLog.setString(1, sysOperLog.getProductId());
                        psOperLog.setInt(2, ValidUtils.str2BigInteger(sysOperLog.getProductVersionId()));
                        stepDataRs = psOperLog.executeQuery();
                        while (stepDataRs.next()) {
                            packageData = stepDataRs.getString("PACKAGE_DATA");
                            if (!packageData.isEmpty()) {
                                JSONArray defaultData = new JSONArray(packageData);
                                for (int dfi = 0; dfi < defaultData.length(); dfi++) {
                                    defaultData.getJSONObject(dfi).put("value", "");
                                }
                                packageDataArr.getJSONObject(0).getJSONObject("data").put("package", defaultData);
                            }
                        }
                        if (!stepDataRs.isClosed()) {
                            stepDataRs.close();
                        }
                        if (!psOperLog.isClosed()) {
                            psOperLog.close();
                        }
                        obj.put("package", packageDataArr);
                    } else {
                        obj.put("package", packageDataArr);
                    }
                    transferCmd.setLength(0);
                    transferCmd.append("SELECT LK_CODE,LK_LABEL,LK_VALUE FROM T_SHELF_PRODUCT_DTL WHERE 1=1 "
                            + " AND TRN_UUID IN (SELECT UUID FROM T_SHELF_PRODUCT_VCS VCS WHERE VCS.PROD_UUID = ? AND VER_PROD = ? )"
                            + " AND LK_CODE IN ('cutOffTime','pcutOffDay','pcutOffWE','pcutOffTime','pcutOffTAllD','pcutOffTSpec', 'prodName', 'prodType')");
                    psOperLog = session.doReturningWork((Connection conn) -> conn).prepareStatement(transferCmd.toString());
                    psOperLog.setString(1, sysOperLog.getProductId());
                    psOperLog.setInt(2, ValidUtils.str2BigInteger(sysOperLog.getProductVersionId()));
                    stepDataRs = psOperLog.executeQuery();
                    while (stepDataRs.next()) {
                        if (stepDataRs.getString("LK_CODE").equalsIgnoreCase("prodType")) {
                            obj.put("productType", stepDataRs.getString("LK_VALUE"));
                        }
                    }
                    if (!stepDataRs.isClosed()) {
                        stepDataRs.close();
                    }
                    if (!psOperLog.isClosed()) {
                        psOperLog.close();
                    }
                    ret.getJSONArray("detail").put(obj);
                } else {
                    JSONObject obj = new JSONObject();
                    obj.put("txnsId", ValidUtils.null2NoData(sysOperLog.getTxnNo()))
                            .put("refNo", sysOperLog.getRefNo())
                            .put("txnsDateTime", DateUtils.getDisplayEnDate(sysOperLog.getCreateAt(), "dd/MM/yyyy HH:mm"))
                            .put("installmentDate", stepData.has("paymentDate") ? stepData.getString("paymentDate") : "")
                            .put("paymentDate", ValidUtils.null2NoData(DateUtils.getDisplayEnDate(sysOperLog.getPaymentDate(), "dd/MM/yyyy HH:mm")))
                            .put("businessDate", ValidUtils.null2NoData(DateUtils.getDisplayEnDate(sysOperLog.getBusinessDate(), "dd-MM-yyyy")))
                            .put("requestLimit", stepData.has("requestLimit") ? stepData.getString("requestLimit") : "")
                            .put("month", stepData.has("month") ? stepData.getString("month") : "")
                            .put("installmentPerMonth", stepData.has("installmentPerMonth") ? stepData.getString("installmentPerMonth") : "")
                            .put("interestRate", stepData.has("interestRate") ? stepData.getString("interestRate") : "")
                            //                                .put("productType", sysOperLog.getProductId())//getFrom product
                            .put("paymentMethod", stepData.has("paymentMethod") ? stepData.getString("paymentMethod") : "")
                            .put("status", rs.getString("STATUS_NAME"))
                            .put("state", rs.getString("STATE_NAME"))
                            .put("procError", ValidUtils.null2NoData(sysOperLog.getAttr2()))
                            .put("procErrNameTh", ValidUtils.null2NoData(errNameTh))
                            .put("procErrNameEn", ValidUtils.null2NoData(errNameEn))
                            .put("procErrDesc", ValidUtils.null2NoData(errNameDesc))
                            .put("ucId", ValidUtils.null2NoData(sysOperLog.getAttr1()))
                            .put("updatedDate", DateUtils.getDisplayEnDate(ValidUtils.str2Date(sysOperLog.getAttr4(), "yyyy-MM-dd HH:mm:ss"), "dd/MM/yyyy HH:mm"))
                            .put("agreementDate", ValidUtils.null2NoData(sysOperLog.getAttr3()))
                            .put("trnStatus", ValidUtils.null2NoData(sysOperLog.getTrnStatus()))
                            .put("failureReason", ValidUtils.null2NoData(sysOperLog.getFailureReason()))
                            .put("traceNo", ValidUtils.null2NoData(sysOperLog.getAttr6()))
                            .put("minMax", ValidUtils.null2NoData(sysOperLog.getAttr7()))
                            .put("compName", !rs.getString("COMPNAME").isEmpty() ? rs.getString("COMPNAME") : "");
                    if (packageDataArr.length() == 0) {
                        String packageData = "[{'respDesc': '', 'code': '', 'data': { 'payDuedate': '', 'buDay': '', 'package': [], 'hpDueDate': '', 'channel': '', 'limit': '', 'hpNo': '', 'discount': '', 'term': '', 'payDate': '', 'lastDueDate': '' }, 'respCode': '', 'status': ''}]";
                        packageDataArr = new JSONArray(packageData);
                        transferCmd.setLength(0);
                        transferCmd.append("SELECT LK_VALUE PACKAGE_DATA FROM T_SHELF_PRODUCT_DTL "
                                + "WHERE TRN_UUID IN (SELECT UUID FROM T_SHELF_PRODUCT_VCS WHERE PROD_UUID = ? AND VER_PROD = ?) "
                                + "AND LK_CODE = 'summaryList'");
                        psOperLog = session.doReturningWork((Connection conn) -> conn).prepareStatement(transferCmd.toString());
                        psOperLog.setString(1, sysOperLog.getProductId());
                        psOperLog.setInt(2, ValidUtils.str2BigInteger(sysOperLog.getProductVersionId()));
                        stepDataRs = psOperLog.executeQuery();
                        while (stepDataRs.next()) {
                            packageData = stepDataRs.getString("PACKAGE_DATA");
                            if (!packageData.isEmpty()) {
                                JSONArray defaultData = new JSONArray(packageData);
                                for (int dfi = 0; dfi < defaultData.length(); dfi++) {
                                    defaultData.getJSONObject(dfi).put("value", "");
                                }
                                packageDataArr.getJSONObject(0).getJSONObject("data").put("package", defaultData);
                            }
                        }
                        if (!stepDataRs.isClosed()) {
                            stepDataRs.close();
                        }
                        if (!psOperLog.isClosed()) {
                            psOperLog.close();
                        }
                        obj.put("package", packageDataArr);
                    } else {
                        obj.put("package", packageDataArr);
                    }
                    if (null != prodCode && !prodCode.isEmpty()) {
                        transferCmd.setLength(0);
                        transferCmd.append("SELECT LK_CODE,LK_LABEL,LK_VALUE FROM T_SHELF_PRODUCT_DTL WHERE 1=1 "
                                + " AND TRN_UUID IN (SELECT UUID FROM T_SHELF_PRODUCT_VCS VCS WHERE VCS.PROD_UUID = ? AND VER_PROD = ? )"
                                + " AND LK_CODE IN ('cutOffTime','pcutOffDay','pcutOffWE','pcutOffTime','pcutOffTAllD','pcutOffTSpec', 'prodName', 'prodType')");
                        psOperLog = session.doReturningWork((Connection conn) -> conn).prepareStatement(transferCmd.toString());
                        psOperLog.setString(1, sysOperLog.getProductId());
                        psOperLog.setInt(2, ValidUtils.str2BigInteger(sysOperLog.getProductVersionId()));
                        stepDataRs = psOperLog.executeQuery();
                        while (stepDataRs.next()) {
                            if (stepDataRs.getString("LK_CODE").equalsIgnoreCase("prodName")) {
                                header.put("productName", stepDataRs.getString("LK_VALUE"));
                            } else if (stepDataRs.getString("LK_CODE").equalsIgnoreCase("pcutOffTSpec") || stepDataRs.getString("LK_CODE").equalsIgnoreCase("pcutOffTAllD")) {
                                if (stepDataRs.getString("LK_CODE").equalsIgnoreCase("pcutOffTAllD")) {
                                    cutOff = stepDataRs.getString("LK_VALUE");
                                } else {
                                    if (!stepDataRs.getString("LK_VALUE").isEmpty() && (cutOff.equalsIgnoreCase("cutoff")) || cutOff.isEmpty()) {
                                        header.put("productCutOffTime", stepDataRs.getString("LK_VALUE"));
                                    } else {
                                        header.put("productCutOffTime", "All Day");
                                    }
                                }
                            } else if (stepDataRs.getString("LK_CODE").equalsIgnoreCase("prodType")) {
                                obj.put("productType", stepDataRs.getString("LK_VALUE"));
                            }
//                            header.put("businessDate", DateUtils.getDisplayEnDate(sysOperLog.getBusinessDate(), "dd-MM-yyyy"));
                        }
                        if (!stepDataRs.isClosed()) {
                            stepDataRs.close();
                        }
                        if (!psOperLog.isClosed()) {
                            psOperLog.close();
                        }
                        header.put("productCode", sysOperLog.getProductCode());
                    } else {
                        header.put("productName", "All");
                        header.put("productCode", "All");
                        header.put("productCutOffTime", "");
                    }
                    listDetail.put(obj);
                    ret.put("detail", listDetail);
                    ret.put("header", header);
                }
            }
        } catch (HibernateException | NullPointerException e) {
            logger.info(e.getMessage());
            throw e;
        } finally {
            if (stepDataRs != null && !stepDataRs.isClosed()) {
                stepDataRs.close();
            }
            if (psOperLog != null && !psOperLog.isClosed()) {
                psOperLog.close();
            }
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
        return ret;
    }

    public JSONObject getDataHistory(String dbEnv, String company, String groupProduct, String ucId, String prodCode, String refNo, String paymentMethod, String txnId, String txnDateStart, String txnStartTime, String txnDateEnd, String txnEndTime, Integer status, String state, String paymentDateStart, String paymentDateEnd, String refTxnId) throws SQLException, UnsupportedEncodingException, ParseException {
        JSONObject ret = new JSONObject();
        PreparedStatement ps = null, psOperLog = null;
        ResultSet rs = null, stepDataRs = null;
        String cutOff = "";
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            StringBuilder inquiryCmd = new StringBuilder();
            List params = new ArrayList<>();
            inquiryCmd.append("select log.* ,LK.LOOKUP_CODE ST_CODE, LK.LOOKUP_NAME_EN STATE_NAME, LK.LOOKUP_NAME_TH, SL.LOOKUP_NAME_EN STATUS_NAME, SP.COMPANY COMPANY ,LK2.LOOKUP_NAME_TH ERR_NAME_TH, LK2.LOOKUP_NAME_EN ERR_NAME_EN, LK2.DESCRIPTION ERR_DESC, COMP.COMP_NAME COMPONENTNAME "
                    + "from t_sys_oper_log LOG "
                    + "INNER JOIN T_SHELF_LOOKUP LK ON LOG.STATE_CODE = LK.UUID  "
                    + "INNER JOIN T_SYS_LOOKUP SL ON LOG.STATUS::TEXT = SL.LOOKUP_CODE "
                    + "INNER JOIN T_SHELF_PRODUCT SP ON LOG.PRODUCT_ID = SP.UUID "
                    + "INNER JOIN T_SYS_LOOKUP SL2 ON LOG.TRN_STATUS::TEXT = SL2.LOOKUP_CODE "
                    + "INNER JOIN T_SHELF_COMP COMP ON LOG.PRODUCT_COMPONENT_ID = COMP.UUID "
                    + "LEFT JOIN T_SHELF_LOOKUP LK2 ON LOG.ATTR2 = LK2.LOOKUP_CODE  "
                    + "WHERE LOG.PRODUCT_COMPONENT_ID = COMP.UUID ");
            if (null != state && !state.isEmpty()) {
                inquiryCmd.append(" AND LOG.STATE_CODE = (SELECT UUID FROM T_SHELF_LOOKUP WHERE GROUP_TYPE = 'PROCESS_STATE' AND LOOKUP_CODE = ?) ");
                params.add(state);
            }
            if (null != company && !company.isEmpty()) {
                inquiryCmd.append(" AND SP.COMPANY = ? ");
                params.add(company);
            }
            if (null != groupProduct && !groupProduct.isEmpty()) {
                inquiryCmd.append(" AND LOG.GROUP_PRODUCT = ? ");
                params.add(groupProduct);
            }
            if (null != prodCode && !prodCode.isEmpty()) {
                inquiryCmd.append(" AND LOG.PRODUCT_CODE = ? ");
                params.add(prodCode);
            }
            if (null != txnId && !txnId.isEmpty()) {
                inquiryCmd.append(" AND LOWER(LOG.TXN_NO) LIKE ? ");
                params.add("%" + txnId.toLowerCase() + "%");
            }
            if (null != refNo && !refNo.isEmpty()) {
                inquiryCmd.append(" AND LOG.REF_NO LIKE ? ");
                params.add("%" + refNo + "%");
            }
            if (null != ucId && !ucId.isEmpty()) {
                String id[] = ucId.replace("(", "").replace(")", "").replace("'", "").split(",");
                StringBuilder subCmd = new StringBuilder();
                subCmd.append(" AND LOG.ATTR1 in ( ? ");

                for (int i = 1; i < id.length; i++) {
                    subCmd.append(", ? ");
                }
                subCmd.append(") ");
                for (int i = 0; i < id.length; i++) {
                    params.add(id[i]);
                }
                inquiryCmd.append(subCmd);
            }
            if (null != refTxnId && !refTxnId.isEmpty()) {
                String id[] = refTxnId.replace("(", "").replace(")", "").replace("'", "").split(",");
                StringBuilder subCmd = new StringBuilder();
                subCmd.append(" AND LOWER(LOG.TXN_NO) in ( ? ");
                for (int i = 1; i < id.length; i++) {
                    subCmd.append(", ? ");
                }
                subCmd.append(") ");
                for (int i = 0; i < id.length; i++) {
                    params.add(id[i].toLowerCase());
                }
                inquiryCmd.append(subCmd);
            }
            if (null != paymentMethod && !paymentMethod.isEmpty()) {
                inquiryCmd.append(" AND LOG.PAYMENT_METHOD = ? ");
                params.add(paymentMethod);
            }
            if (null != txnStartTime && !txnStartTime.isEmpty()) {
                txnStartTime += ":00";
            } else {
                txnStartTime = "00:00:00";
            }
            if (null != txnEndTime && !txnEndTime.isEmpty()) {
                txnEndTime += ":59";
            } else {
                txnEndTime = "23:59:59";
            }
            if ((null != txnDateStart && !txnDateStart.isEmpty()) && (null != txnDateEnd && !txnDateEnd.isEmpty())) {
                inquiryCmd.append(" AND LOG.CREATE_AT BETWEEN TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS') AND TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(txnDateStart + " " + txnStartTime);
                params.add(txnDateEnd + " " + txnEndTime);
            } else if ((null == txnDateStart || txnDateStart.isEmpty()) && (null != txnDateEnd && !txnDateEnd.isEmpty())) {
                inquiryCmd.append(" AND LOG.CREATE_AT <= TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(txnDateEnd + " " + txnEndTime);
            } else if ((null != txnDateStart && !txnDateStart.isEmpty()) && (null == txnDateEnd || txnDateEnd.isEmpty())) {
                inquiryCmd.append(" AND LOG.CREATE_AT >= TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(txnDateStart + " " + txnStartTime);
            }
            if (null != status) {
                inquiryCmd.append(" AND LOG.STATUS = ? ");
                params.add(status);
            }
            if ((null != paymentDateStart && !paymentDateStart.isEmpty()) && (null != paymentDateEnd && !paymentDateEnd.isEmpty())) {
                inquiryCmd.append(" AND LOG.PAYMENT_DATE BETWEEN TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS') AND TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(paymentDateStart + " 00:00:00");
                params.add(paymentDateEnd + " 23:59:59");
            } else if ((null == paymentDateStart || paymentDateStart.isEmpty()) && (null != paymentDateEnd && !paymentDateEnd.isEmpty())) {
                inquiryCmd.append(" AND LOG.PAYMENT_DATE <= TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(paymentDateEnd + " 23:59:59");
            } else if ((null != paymentDateStart && !paymentDateStart.isEmpty()) && (null == paymentDateEnd || paymentDateEnd.isEmpty())) {
                inquiryCmd.append(" AND LOG.PAYMENT_DATE >= TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(paymentDateStart + " 00:00:00");
            }
            inquiryCmd.append(" ORDER BY LOG.CREATE_AT ASC");
            ps = session.doReturningWork((Connection conn) -> conn).prepareStatement(inquiryCmd.toString());
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
            JSONArray listDetail = new JSONArray();
            JSONObject header = new JSONObject();
            while (rs.next()) {
                SysOperLog sysOperLog = new SysOperLog();
                sysOperLog.setUuid(rs.getString("uuid"));
                sysOperLog.setTrnId(rs.getString("trn_id"));
                sysOperLog.setSource(rs.getString("source"));
                sysOperLog.setProductId(rs.getString("product_id"));
                sysOperLog.setProductCode(rs.getString("product_code"));
                sysOperLog.setProductVersionId(rs.getString("product_version_id"));
                sysOperLog.setProductComponentId(rs.getString("product_component_id"));
                sysOperLog.setTaskCategory(rs.getString("task_category"));
                sysOperLog.setKeywords(rs.getString("keywords"));
                sysOperLog.setTrnStatus(rs.getInt("trn_status"));
                sysOperLog.setTrnSubStatus(rs.getInt("trn_sub_status"));
                sysOperLog.setFailureReason(rs.getString("failure_reason"));
                sysOperLog.setSourceDevice(rs.getString("source_device"));
                sysOperLog.setSourceDeviceId(rs.getString("source_device_id"));
                sysOperLog.setSourceCifId(rs.getString("source_cif_id"));
                sysOperLog.setAccountName(rs.getString("account_name"));
                sysOperLog.setBusinessDate(rs.getTimestamp("business_date"));
                sysOperLog.setRefNo(rs.getString("ref_no"));
                sysOperLog.setPaymentMethod(rs.getString("payment_method"));
                sysOperLog.setPaymentDate(rs.getTimestamp("payment_date"));//paymentdate
                sysOperLog.setStateCode(new ShelfLookup(rs.getString("state_code")));
                sysOperLog.setProdChannel(rs.getString("prod_channel"));
                sysOperLog.setStepData(rs.getString("step_data"));
                sysOperLog.setCaseId(rs.getString("case_id"));
                sysOperLog.setGroupProduct(rs.getString("group_product"));
                sysOperLog.setTxnNo(rs.getString("txn_no"));
                sysOperLog.setAttr1(rs.getString("attr1"));
                sysOperLog.setAttr2(rs.getString("attr2"));
                sysOperLog.setAttr3(rs.getString("attr3"));
                sysOperLog.setAttr4(rs.getString("attr4"));
                sysOperLog.setAttr5(rs.getString("attr5"));
                sysOperLog.setAttr6(rs.getString("attr6"));
                sysOperLog.setAttr7(rs.getString("attr7"));
                sysOperLog.setAttr8(rs.getString("attr8"));
                sysOperLog.setAttr9(rs.getString("attr9"));
                sysOperLog.setAttr10(rs.getString("attr10"));
                sysOperLog.setStatus(rs.getInt("status"));
                sysOperLog.setCreateAt(rs.getTimestamp("create_at"));
                sysOperLog.setCreateBy(rs.getString("create_by"));
                sysOperLog.setStateTime(ValidUtils.str2BigInt(rs.getInt("state_time") + ""));
                String errNameTh = rs.getString("ERR_NAME_TH");
                String errNameEn = rs.getString("ERR_NAME_EN");
                String errNameDesc = rs.getString("ERR_DESC");
                inquiryCmd.setLength(0);
                inquiryCmd.append("SELECT STEP_DATA, PAYMENT_METHOD FROM T_SYS_OPER_LOG WHERE TXN_NO = ? AND STATE_CODE in (SELECT UUID FROM T_SHELF_LOOKUP WHERE GROUP_TYPE = 'PROCESS_STATE' AND LOOKUP_CODE in ('PRO1011','PRO1032'))"); //PRO1011
                psOperLog = session.doReturningWork((Connection conn) -> conn).prepareStatement(inquiryCmd.toString());
                psOperLog.setString(1, sysOperLog.getTxnNo());
                stepDataRs = psOperLog.executeQuery();
                JSONObject stepData = new JSONObject();
                stepData.put("paymentMethod", sysOperLog.getPaymentMethod());
                JSONArray packageDataArr = new JSONArray();
                while (stepDataRs.next()) {
                    byte[] decoded = Base64.decodeBase64(stepDataRs.getString("STEP_DATA"));
                    String stepDataIn = new String(decoded, "UTF-8");
                    if (!stepDataIn.isEmpty()) {
                        JSONArray stepDataArray = new JSONArray(stepDataIn);
                        for (int i = 0; i < stepDataArray.length(); i++) {
                            JSONObject stepDataObj = stepDataArray.getJSONObject(i);
                            if (stepDataObj.getJSONObject("data").has("package")) {
                                packageDataArr.put(stepDataObj);
                            }
                            if (stepDataObj.has("code") && stepDataObj.getString("code").equalsIgnoreCase("loan")) {
                                if (stepDataObj.getJSONObject("data").has("package")) {
                                    JSONArray packageDataArrLoan = stepDataObj.getJSONObject("data").getJSONArray("package");
                                    for (int j = 0; j < packageDataArrLoan.length(); j++) {
                                        JSONObject packageData = packageDataArrLoan.getJSONObject(j);
                                        if (packageData.has("parameter")) {
                                            switch (packageData.getString("parameter")) {
                                                case "limit":
                                                    stepData.put("requestLimit", packageData.getString("value"));
                                                    break;
                                                case "factor_2_from":
                                                    stepData.put("month", packageData.getInt("value") + "");
                                                    break;
                                                case "calAmount1":
                                                    stepData.put("installmentPerMonth", packageData.getString("value"));
                                                    break;
                                                case "rateAmount":
                                                    stepData.put("interestRate", packageData.getDouble("value") + "");
                                                    break;
                                                case "paymentDate":
                                                    stepData.put("paymentDate", packageData.getString("value"));
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
                if (!stepDataRs.isClosed()) {
                    stepDataRs.close();
                }
                if (!psOperLog.isClosed()) {
                    psOperLog.close();
                }
                if (ret.has("detail")) {
                    JSONObject obj = new JSONObject();
                    obj.put("txnsId", ValidUtils.null2NoData(sysOperLog.getTxnNo()))
                            .put("refNo", sysOperLog.getRefNo())
                            .put("txnsDateTime", DateUtils.getDisplayEnDate(sysOperLog.getCreateAt(), "dd/MM/yyyy HH:mm"))
                            .put("installmentDate", stepData.has("paymentDate") ? stepData.getString("paymentDate") : "")
                            .put("paymentDate", ValidUtils.null2NoData(DateUtils.getDisplayEnDate(sysOperLog.getPaymentDate(), "dd/MM/yyyy HH:mm")))
                            .put("businessDate", ValidUtils.null2NoData(DateUtils.getDisplayEnDate(sysOperLog.getBusinessDate(), "dd-MM-yyyy")))
                            .put("requestLimit", stepData.has("requestLimit") ? stepData.getString("requestLimit") : "")
                            .put("month", stepData.has("month") ? stepData.getString("month") : "")
                            .put("installmentPerMonth", stepData.has("installmentPerMonth") ? stepData.getString("installmentPerMonth") : "")
                            .put("interestRate", stepData.has("interestRate") ? stepData.getString("interestRate") : "")
                            .put("paymentMethod", stepData.has("paymentMethod") ? stepData.getString("paymentMethod") : "")
                            .put("status", rs.getString("STATUS_NAME"))
                            .put("state", rs.getString("STATE_NAME"))
                            .put("procError", ValidUtils.null2NoData(sysOperLog.getAttr2()))
                            .put("procErrNameTh", ValidUtils.null2NoData(errNameTh))
                            .put("procErrNameEn", ValidUtils.null2NoData(errNameEn))
                            .put("procErrDesc", ValidUtils.null2NoData(errNameDesc))
                            .put("ucId", ValidUtils.null2NoData(sysOperLog.getAttr1()))
                            .put("updatedDate", DateUtils.getDisplayEnDate(ValidUtils.str2Date(sysOperLog.getAttr4(), "yyyy-MM-dd HH:mm:ss"), "dd/MM/yyyy HH:mm"))
                            .put("agreementDate", ValidUtils.null2NoData(sysOperLog.getAttr3()))
                            .put("trnStatus", ValidUtils.null2NoData(sysOperLog.getTrnStatus()))
                            .put("failureReason", ValidUtils.null2NoData(sysOperLog.getFailureReason()))
                            .put("componentName", !rs.getString("COMPONENTNAME").isEmpty() ? rs.getString("COMPONENTNAME") : "");
                    if (packageDataArr.length() == 0) {
                        String packageData = "[{'respDesc': '', 'code': '', 'data': { 'payDuedate': '', 'buDay': '', 'package': [], 'hpDueDate': '', 'channel': '', 'limit': '', 'hpNo': '', 'discount': '', 'term': '', 'payDate': '', 'lastDueDate': '' }, 'respCode': '', 'status': ''}]";
                        packageDataArr = new JSONArray(packageData);
                        inquiryCmd.setLength(0);
                        inquiryCmd.append("SELECT LK_VALUE PACKAGE_DATA FROM T_SHELF_PRODUCT_DTL "
                                + "WHERE TRN_UUID IN (SELECT UUID FROM T_SHELF_PRODUCT_VCS WHERE PROD_UUID = ? AND VER_PROD = ?) "
                                + "AND LK_CODE = 'summaryList'");
                        psOperLog = session.doReturningWork((Connection conn) -> conn).prepareStatement(inquiryCmd.toString());
                        psOperLog.setString(1, sysOperLog.getProductId());
                        psOperLog.setInt(2, ValidUtils.str2BigInteger(sysOperLog.getProductVersionId()));
                        stepDataRs = psOperLog.executeQuery();
                        while (stepDataRs.next()) {
                            packageData = stepDataRs.getString("PACKAGE_DATA");
                            if (!packageData.isEmpty()) {
                                JSONArray defaultData = new JSONArray(packageData);
                                for (int dfi = 0; dfi < defaultData.length(); dfi++) {
                                    defaultData.getJSONObject(dfi).put("value", "");
                                }
                                packageDataArr.getJSONObject(0).getJSONObject("data").put("package", defaultData);
                            }
                        }
                        if (!stepDataRs.isClosed()) {
                            stepDataRs.close();
                        }
                        if (!psOperLog.isClosed()) {
                            psOperLog.close();
                        }
                        obj.put("package", packageDataArr);
                    } else {
                        obj.put("package", packageDataArr);
                    }
                    inquiryCmd.setLength(0);
                    inquiryCmd.append("SELECT LK_CODE,LK_LABEL,LK_VALUE FROM T_SHELF_PRODUCT_DTL WHERE 1=1 "
                            + " AND TRN_UUID IN (SELECT UUID FROM T_SHELF_PRODUCT_VCS VCS WHERE VCS.PROD_UUID = ? AND VER_PROD = ? )"
                            + " AND LK_CODE IN ('cutOffTime','pcutOffDay','pcutOffWE','pcutOffTime','pcutOffTAllD','pcutOffTSpec', 'prodName', 'prodType')");
                    psOperLog = session.doReturningWork((Connection conn) -> conn).prepareStatement(inquiryCmd.toString());
                    psOperLog.setString(1, sysOperLog.getProductId());
                    psOperLog.setInt(2, ValidUtils.str2BigInteger(sysOperLog.getProductVersionId()));
                    stepDataRs = psOperLog.executeQuery();
                    while (stepDataRs.next()) {
                        if (stepDataRs.getString("LK_CODE").equalsIgnoreCase("prodType")) {
                            obj.put("productType", stepDataRs.getString("LK_VALUE"));
                        }
                    }
                    if (!stepDataRs.isClosed()) {
                        stepDataRs.close();
                    }
                    if (!psOperLog.isClosed()) {
                        psOperLog.close();
                    }
                    ret.getJSONArray("detail").put(obj);
                } else {
                    JSONObject obj = new JSONObject();
                    obj.put("txnsId", ValidUtils.null2NoData(sysOperLog.getTxnNo()))
                            .put("refNo", sysOperLog.getRefNo())
                            .put("txnsDateTime", DateUtils.getDisplayEnDate(sysOperLog.getCreateAt(), "dd/MM/yyyy HH:mm"))
                            .put("installmentDate", stepData.has("paymentDate") ? stepData.getString("paymentDate") : "")
                            .put("paymentDate", ValidUtils.null2NoData(DateUtils.getDisplayEnDate(sysOperLog.getPaymentDate(), "dd/MM/yyyy HH:mm")))
                            .put("businessDate", ValidUtils.null2NoData(DateUtils.getDisplayEnDate(sysOperLog.getBusinessDate(), "dd-MM-yyyy")))
                            .put("requestLimit", stepData.has("requestLimit") ? stepData.getString("requestLimit") : "")
                            .put("month", stepData.has("month") ? stepData.getString("month") : "")
                            .put("installmentPerMonth", stepData.has("installmentPerMonth") ? stepData.getString("installmentPerMonth") : "")
                            .put("interestRate", stepData.has("interestRate") ? stepData.getString("interestRate") : "")
                            .put("paymentMethod", stepData.has("paymentMethod") ? stepData.getString("paymentMethod") : "")
                            .put("status", rs.getString("STATUS_NAME"))
                            .put("state", rs.getString("STATE_NAME"))
                            .put("procError", ValidUtils.null2NoData(sysOperLog.getAttr2()))
                            .put("procErrNameTh", ValidUtils.null2NoData(errNameTh))
                            .put("procErrNameEn", ValidUtils.null2NoData(errNameEn))
                            .put("procErrDesc", ValidUtils.null2NoData(errNameDesc))
                            .put("ucId", ValidUtils.null2NoData(sysOperLog.getAttr1()))
                            .put("updatedDate", DateUtils.getDisplayEnDate(ValidUtils.str2Date(sysOperLog.getAttr4(), "yyyy-MM-dd HH:mm:ss"), "dd/MM/yyyy HH:mm"))
                            .put("agreementDate", ValidUtils.null2NoData(sysOperLog.getAttr3()))
                            .put("trnStatus", ValidUtils.null2NoData(sysOperLog.getTrnStatus()))
                            .put("failureReason", ValidUtils.null2NoData(sysOperLog.getFailureReason()))
                            .put("componentName", !rs.getString("COMPONENTNAME").isEmpty() ? rs.getString("COMPONENTNAME") : "");
                    if (packageDataArr.length() == 0) {
                        String packageData = "[{'respDesc': '', 'code': '', 'data': { 'payDuedate': '', 'buDay': '', 'package': [], 'hpDueDate': '', 'channel': '', 'limit': '', 'hpNo': '', 'discount': '', 'term': '', 'payDate': '', 'lastDueDate': '' }, 'respCode': '', 'status': ''}]";
                        packageDataArr = new JSONArray(packageData);
                        inquiryCmd.setLength(0);
                        inquiryCmd.append("SELECT LK_VALUE PACKAGE_DATA FROM T_SHELF_PRODUCT_DTL "
                                + "WHERE TRN_UUID IN (SELECT UUID FROM T_SHELF_PRODUCT_VCS WHERE PROD_UUID = ? AND VER_PROD = ?) "
                                + "AND LK_CODE = 'summaryList'");
                        psOperLog = session.doReturningWork((Connection conn) -> conn).prepareStatement(inquiryCmd.toString());
                        psOperLog.setString(1, sysOperLog.getProductId());
                        psOperLog.setInt(2, ValidUtils.str2BigInteger(sysOperLog.getProductVersionId()));
                        stepDataRs = psOperLog.executeQuery();
                        while (stepDataRs.next()) {
                            packageData = stepDataRs.getString("PACKAGE_DATA");
                            if (!packageData.isEmpty()) {
                                JSONArray defaultData = new JSONArray(packageData);
                                for (int dfi = 0; dfi < defaultData.length(); dfi++) {
                                    defaultData.getJSONObject(dfi).put("value", "");
                                }
                                packageDataArr.getJSONObject(0).getJSONObject("data").put("package", defaultData);
                            }
                        }
                        if (!stepDataRs.isClosed()) {
                            stepDataRs.close();
                        }
                        if (!psOperLog.isClosed()) {
                            psOperLog.close();
                        }
                        obj.put("package", packageDataArr);
                    } else {
                        obj.put("package", packageDataArr);
                    }
                    if (null != prodCode && !prodCode.isEmpty()) {
                        inquiryCmd.setLength(0);
                        inquiryCmd.append("SELECT LK_CODE,LK_LABEL,LK_VALUE FROM T_SHELF_PRODUCT_DTL WHERE 1=1 "
                                + " AND TRN_UUID IN (SELECT UUID FROM T_SHELF_PRODUCT_VCS VCS WHERE VCS.PROD_UUID = ? AND VER_PROD = ? )"
                                + " AND LK_CODE IN ('cutOffTime','pcutOffDay','pcutOffWE','pcutOffTime','pcutOffTAllD','pcutOffTSpec', 'prodName', 'prodType')");
                        psOperLog = session.doReturningWork((Connection conn) -> conn).prepareStatement(inquiryCmd.toString());
                        psOperLog.setString(1, sysOperLog.getProductId());
                        psOperLog.setInt(2, ValidUtils.str2BigInteger(sysOperLog.getProductVersionId()));
                        stepDataRs = psOperLog.executeQuery();
                        while (stepDataRs.next()) {
                            if (stepDataRs.getString("LK_CODE").equalsIgnoreCase("prodName")) {
                                header.put("productName", stepDataRs.getString("LK_VALUE"));
                            } else if (stepDataRs.getString("LK_CODE").equalsIgnoreCase("pcutOffTSpec") || stepDataRs.getString("LK_CODE").equalsIgnoreCase("pcutOffTAllD")) {
                                if (stepDataRs.getString("LK_CODE").equalsIgnoreCase("pcutOffTAllD")) {
                                    cutOff = stepDataRs.getString("LK_VALUE");
                                } else {
                                    if (!stepDataRs.getString("LK_VALUE").isEmpty() && (cutOff.equalsIgnoreCase("cutoff")) || cutOff.isEmpty()) {
                                        header.put("productCutOffTime", stepDataRs.getString("LK_VALUE"));
                                    } else {
                                        header.put("productCutOffTime", "All Day");
                                    }
                                }
                            } else if (stepDataRs.getString("LK_CODE").equalsIgnoreCase("prodType")) {
                                obj.put("productType", stepDataRs.getString("LK_VALUE"));
                            }
                        }
                        if (!stepDataRs.isClosed()) {
                            stepDataRs.close();
                        }
                        if (!psOperLog.isClosed()) {
                            psOperLog.close();
                        }
                        header.put("productCode", sysOperLog.getProductCode());
                    } else {
                        header.put("productName", "All");
                        header.put("productCode", "All");
                        header.put("productCutOffTime", "");
                    }
                    listDetail.put(obj);
                    ret.put("detail", listDetail);
                    ret.put("header", header);
                }
            }
        } catch (HibernateException | NullPointerException e) {
            logger.info(e.getMessage());
            throw e;
        } finally {
            if (stepDataRs != null && !stepDataRs.isClosed()) {
                stepDataRs.close();
            }
            if (psOperLog != null && !psOperLog.isClosed()) {
                psOperLog.close();
            }
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
        return ret;
    }

    public JSONObject getInquiryTransactionNew(String dbEnv, String company, String groupProduct, String ucId, String prodCode, String refNo, String paymentMethod, String txnId, String txnDateStart, String txnStartTime, String txnDateEnd, String txnEndTime, Integer status, String state, String paymentDateStart, String paymentDateEnd, String refTxnId) throws SQLException, UnsupportedEncodingException, ParseException {
        JSONObject ret = new JSONObject();
        PreparedStatement ps = null, psOperLog = null;
        ResultSet rs = null, stepDataRs = null;
        String cutOff = "";
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            StringBuilder inquiryCmd = new StringBuilder();
            List params = new ArrayList<>();
            inquiryCmd.append("select log.product_id, log.product_code, log.product_version_id, log.txn_no, log.ref_no, log.payment_method, log.create_at, LK.LOOKUP_NAME_EN STATE_NAME, SL.LOOKUP_NAME_EN STATUS_NAME "
                    + "from t_sys_oper_log LOG "
                    + "INNER JOIN T_SHELF_LOOKUP LK ON LOG.STATE_CODE = LK.UUID  "
                    + "INNER JOIN T_SYS_LOOKUP SL ON LOG.STATUS::TEXT = SL.LOOKUP_CODE "
                    + "INNER JOIN T_SHELF_PRODUCT SP ON LOG.PRODUCT_ID = SP.UUID "
                    + "WHERE log.uuid IN "
                    + "( "
                    + "         select uuid from ("
                    + "   select uuid, row_number() over (partition by txn_no order by TO_TIMESTAMP(log.ATTR4, 'yyyy-MM-dd HH24:MI:SS') desc, log.CREATE_AT desc) as ROW_NO "
                    + "   from t_sys_oper_log log "
                    + "        )A "
                    + "        where A.ROW_NO = 1 "
                    + " ) ");
            if (null != state && !state.isEmpty()) {
                inquiryCmd.append(" AND LOG.STATE_CODE = (SELECT UUID FROM T_SHELF_LOOKUP WHERE GROUP_TYPE = 'PROCESS_STATE' AND LOOKUP_CODE = ?) ");
                params.add(state);
            }
            if (null != company && !company.isEmpty()) {
                inquiryCmd.append(" AND SP.COMPANY = ? ");
                params.add(company);
            }
            if (null != groupProduct && !groupProduct.isEmpty()) {
                inquiryCmd.append(" AND LOG.GROUP_PRODUCT = ? ");
                params.add(groupProduct);
            }
            if (null != prodCode && !prodCode.isEmpty()) {
                inquiryCmd.append(" AND LOG.PRODUCT_CODE = ? ");
                params.add(prodCode);
            }
            if (null != txnId && !txnId.isEmpty()) {
                inquiryCmd.append(" AND LOWER(LOG.TXN_NO) LIKE ? ");
                params.add("%" + txnId.toLowerCase() + "%");
            }
            if (null != refNo && !refNo.isEmpty()) {
                inquiryCmd.append(" AND LOG.REF_NO LIKE ? ");
                params.add("%" + refNo + "%");
            }
            if (null != ucId && !ucId.isEmpty()) {
                String id[] = ucId.replace("(", "").replace(")", "").replace("'", "").split(",");
                StringBuilder subCmd = new StringBuilder();
                subCmd.append(" AND LOG.ATTR1 in ( ? ");

                for (int i = 1; i < id.length; i++) {
                    subCmd.append(", ? ");
                }
                subCmd.append(") ");
                for (int i = 0; i < id.length; i++) {
                    params.add(id[i]);
                }
                inquiryCmd.append(subCmd);
            }
            if (null != refTxnId && !refTxnId.isEmpty()) {
                String id[] = refTxnId.replace("(", "").replace(")", "").replace("'", "").split(",");
                StringBuilder subCmd = new StringBuilder();
                subCmd.append(" AND LOWER(LOG.TXN_NO) in ( ? ");
                for (int i = 1; i < id.length; i++) {
                    subCmd.append(", ? ");
                }
                subCmd.append(") ");
                for (int i = 0; i < id.length; i++) {
                    params.add(id[i].toLowerCase());
                }
                inquiryCmd.append(subCmd);
            }
            if (null != paymentMethod && !paymentMethod.isEmpty()) {
                inquiryCmd.append(" AND LOG.PAYMENT_METHOD = ? ");
                params.add(paymentMethod);
            }
            if (null != txnStartTime && !txnStartTime.isEmpty()) {
                txnStartTime += ":00";
            } else {
                txnStartTime = "00:00:00";
            }
            if (null != txnEndTime && !txnEndTime.isEmpty()) {
                txnEndTime += ":59";
            } else {
                txnEndTime = "23:59:59";
            }
            if ((null != txnDateStart && !txnDateStart.isEmpty()) && (null != txnDateEnd && !txnDateEnd.isEmpty())) {
                inquiryCmd.append(" AND LOG.CREATE_AT BETWEEN TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS') AND TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(txnDateStart + " " + txnStartTime);
                params.add(txnDateEnd + " " + txnEndTime);
            } else if ((null == txnDateStart || txnDateStart.isEmpty()) && (null != txnDateEnd && !txnDateEnd.isEmpty())) {
                inquiryCmd.append(" AND LOG.CREATE_AT <= TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(txnDateEnd + " " + txnEndTime);
            } else if ((null != txnDateStart && !txnDateStart.isEmpty()) && (null == txnDateEnd || txnDateEnd.isEmpty())) {
                inquiryCmd.append(" AND LOG.CREATE_AT >= TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(txnDateStart + " " + txnStartTime);
            }
            if (null != status) {
                inquiryCmd.append(" AND LOG.STATUS = ? ");
                params.add(status);
            }
            if ((null != paymentDateStart && !paymentDateStart.isEmpty()) && (null != paymentDateEnd && !paymentDateEnd.isEmpty())) {
                inquiryCmd.append(" AND LOG.PAYMENT_DATE BETWEEN TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS') AND TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(paymentDateStart + " 00:00:00");
                params.add(paymentDateEnd + " 23:59:59");
            } else if ((null == paymentDateStart || paymentDateStart.isEmpty()) && (null != paymentDateEnd && !paymentDateEnd.isEmpty())) {
                inquiryCmd.append(" AND LOG.PAYMENT_DATE <= TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(paymentDateEnd + " 23:59:59");
            } else if ((null != paymentDateStart && !paymentDateStart.isEmpty()) && (null == paymentDateEnd || paymentDateEnd.isEmpty())) {
                inquiryCmd.append(" AND LOG.PAYMENT_DATE >= TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(paymentDateStart + " 00:00:00");
            }
            inquiryCmd.append(" ORDER BY LOG.CREATE_AT ASC ");
            ps = session.doReturningWork((Connection conn) -> conn).prepareStatement(inquiryCmd.toString());
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
            JSONArray listDetail = new JSONArray();
            JSONObject header = new JSONObject();
            while (rs.next()) {
                SysOperLog sysOperLog = new SysOperLog();
                sysOperLog.setTxnNo(rs.getString("txn_no"));
                sysOperLog.setProductId(rs.getString("product_id"));
                sysOperLog.setProductCode(rs.getString("product_code"));
                sysOperLog.setProductVersionId(rs.getString("product_version_id"));
                sysOperLog.setRefNo(rs.getString("ref_no"));
                sysOperLog.setPaymentMethod(rs.getString("payment_method"));
                sysOperLog.setCreateAt(rs.getTimestamp("create_at"));
                inquiryCmd.setLength(0);
                inquiryCmd.append("SELECT STEP_DATA, PAYMENT_METHOD FROM T_SYS_OPER_LOG WHERE TXN_NO = ? AND STATE_CODE in (SELECT UUID FROM T_SHELF_LOOKUP WHERE GROUP_TYPE = 'PROCESS_STATE' AND LOOKUP_CODE in ('PRO1011','PRO1032'))"); //PRO1011
                psOperLog = session.doReturningWork((Connection conn) -> conn).prepareStatement(inquiryCmd.toString());
                psOperLog.setString(1, sysOperLog.getTxnNo());
                stepDataRs = psOperLog.executeQuery();
                JSONObject stepData = new JSONObject();
                stepData.put("paymentMethod", sysOperLog.getPaymentMethod());
                JSONArray packageDataArr = new JSONArray();
                while (stepDataRs.next()) {
                    byte[] decoded = Base64.decodeBase64(stepDataRs.getString("STEP_DATA"));
                    String stepDataIn = new String(decoded, "UTF-8");
                    if (!stepDataIn.isEmpty()) {
                        JSONArray stepDataArray = new JSONArray(stepDataIn);
                        for (int i = 0; i < stepDataArray.length(); i++) {
                            JSONObject stepDataObj = stepDataArray.getJSONObject(i);
                            if (stepDataObj.getJSONObject("data").has("package")) {
                                packageDataArr.put(stepDataObj);
                            }
                            if (stepDataObj.has("code") && stepDataObj.getString("code").equalsIgnoreCase("loan")) {
                                if (stepDataObj.getJSONObject("data").has("package")) {
                                    JSONArray packageDataArrLoan = stepDataObj.getJSONObject("data").getJSONArray("package");
                                    for (int j = 0; j < packageDataArrLoan.length(); j++) {
                                        JSONObject packageData = packageDataArrLoan.getJSONObject(j);
                                        if (packageData.has("parameter")) {
                                            switch (packageData.getString("parameter")) {
                                                case "limit":
                                                    stepData.put("requestLimit", packageData.getString("value"));
                                                    break;
                                                case "factor_2_from":
                                                    stepData.put("month", packageData.getInt("value") + "");
                                                    break;
                                                case "calAmount1":
                                                    stepData.put("installmentPerMonth", packageData.getString("value"));
                                                    break;
                                                case "rateAmount":
                                                    stepData.put("interestRate", packageData.getDouble("value") + "");
                                                    break;
                                                case "paymentDate":
                                                    stepData.put("paymentDate", packageData.getString("value"));
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
                if (!stepDataRs.isClosed()) {
                    stepDataRs.close();
                }
                if (!psOperLog.isClosed()) {
                    psOperLog.close();
                }
                if (ret.has("detail")) {
                    JSONObject obj = new JSONObject();
                    obj.put("txnsId", ValidUtils.null2NoData(sysOperLog.getTxnNo()))
                            .put("refNo", sysOperLog.getRefNo())
                            .put("txnsDateTime", DateUtils.getDisplayEnDate(sysOperLog.getCreateAt(), "dd/MM/yyyy HH:mm"))
                            .put("requestLimit", stepData.has("requestLimit") ? stepData.getString("requestLimit") : "")
                            .put("paymentMethod", stepData.has("paymentMethod") ? stepData.getString("paymentMethod") : "")
                            .put("status", rs.getString("STATUS_NAME"))
                            .put("state", rs.getString("STATE_NAME"));

                    inquiryCmd.setLength(0);
                    inquiryCmd.append("SELECT LK_CODE,LK_LABEL,LK_VALUE FROM T_SHELF_PRODUCT_DTL WHERE 1=1 "
                            + " AND TRN_UUID IN (SELECT UUID FROM T_SHELF_PRODUCT_VCS VCS WHERE VCS.PROD_UUID = ? AND VER_PROD = ? )"
                            + " AND LK_CODE IN ('cutOffTime','pcutOffDay','pcutOffWE','pcutOffTime','pcutOffTAllD','pcutOffTSpec', 'prodName', 'prodType')");
                    psOperLog = session.doReturningWork((Connection conn) -> conn).prepareStatement(inquiryCmd.toString());
                    psOperLog.setString(1, sysOperLog.getProductId());
                    psOperLog.setInt(2, ValidUtils.str2BigInteger(sysOperLog.getProductVersionId()));
                    stepDataRs = psOperLog.executeQuery();
                    while (stepDataRs.next()) {
                        if (stepDataRs.getString("LK_CODE").equalsIgnoreCase("prodType")) {
                            obj.put("productType", stepDataRs.getString("LK_VALUE"));
                        }
                    }
                    if (!stepDataRs.isClosed()) {
                        stepDataRs.close();
                    }
                    if (!psOperLog.isClosed()) {
                        psOperLog.close();
                    }
                    ret.getJSONArray("detail").put(obj);
                } else {
                    JSONObject obj = new JSONObject();
                    obj.put("txnsId", ValidUtils.null2NoData(sysOperLog.getTxnNo()))
                            .put("refNo", sysOperLog.getRefNo())
                            .put("txnsDateTime", DateUtils.getDisplayEnDate(sysOperLog.getCreateAt(), "dd/MM/yyyy HH:mm"))
                            .put("requestLimit", stepData.has("requestLimit") ? stepData.getString("requestLimit") : "")
                            .put("paymentMethod", stepData.has("paymentMethod") ? stepData.getString("paymentMethod") : "")
                            .put("status", rs.getString("STATUS_NAME"))
                            .put("state", rs.getString("STATE_NAME"));

                    if (null != prodCode && !prodCode.isEmpty()) {
                        inquiryCmd.setLength(0);
                        inquiryCmd.append("SELECT LK_CODE,LK_LABEL,LK_VALUE FROM T_SHELF_PRODUCT_DTL WHERE 1=1 "
                                + " AND TRN_UUID IN (SELECT UUID FROM T_SHELF_PRODUCT_VCS VCS WHERE VCS.PROD_UUID = ? AND VER_PROD = ? )"
                                + " AND LK_CODE IN ('cutOffTime','pcutOffDay','pcutOffWE','pcutOffTime','pcutOffTAllD','pcutOffTSpec', 'prodName', 'prodType')");
                        psOperLog = session.doReturningWork((Connection conn) -> conn).prepareStatement(inquiryCmd.toString());
                        psOperLog.setString(1, sysOperLog.getProductId());
                        psOperLog.setInt(2, ValidUtils.str2BigInteger(sysOperLog.getProductVersionId()));
                        stepDataRs = psOperLog.executeQuery();
                        while (stepDataRs.next()) {
                            if (stepDataRs.getString("LK_CODE").equalsIgnoreCase("prodName")) {
                                header.put("productName", stepDataRs.getString("LK_VALUE"));
                            } else if (stepDataRs.getString("LK_CODE").equalsIgnoreCase("pcutOffTSpec") || stepDataRs.getString("LK_CODE").equalsIgnoreCase("pcutOffTAllD")) {
                                if (stepDataRs.getString("LK_CODE").equalsIgnoreCase("pcutOffTAllD")) {
                                    cutOff = stepDataRs.getString("LK_VALUE");
                                } else {
                                    if (!stepDataRs.getString("LK_VALUE").isEmpty() && (cutOff.equalsIgnoreCase("cutoff")) || cutOff.isEmpty()) {
                                        header.put("productCutOffTime", stepDataRs.getString("LK_VALUE"));
                                    } else {
                                        header.put("productCutOffTime", "All Day");
                                    }
                                }
                            } else if (stepDataRs.getString("LK_CODE").equalsIgnoreCase("prodType")) {
                                obj.put("productType", stepDataRs.getString("LK_VALUE"));
                            }
//                            header.put("businessDate", DateUtils.getDisplayEnDate(sysOperLog.getBusinessDate(), "dd-MM-yyyy"));
                        }
                        if (!stepDataRs.isClosed()) {
                            stepDataRs.close();
                        }
                        if (!psOperLog.isClosed()) {
                            psOperLog.close();
                        }
                        header.put("productCode", sysOperLog.getProductCode());
                    } else {
                        header.put("productName", "All");
                        header.put("productCode", "All");
                        header.put("productCutOffTime", "");
                    }
                    listDetail.put(obj);
                    ret.put("detail", listDetail);
                    ret.put("header", header);
                }
            }
        } catch (HibernateException | NullPointerException e) {
            logger.info(e.getMessage());
            throw e;
        } finally {
            if (stepDataRs != null && !stepDataRs.isClosed()) {
                stepDataRs.close();
            }
            if (psOperLog != null && !psOperLog.isClosed()) {
                psOperLog.close();
            }
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
        return ret;
    }

}
