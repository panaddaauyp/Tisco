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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import org.apache.commons.codec.binary.Base64;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.json.JSONObject;
import static th.co.d1.digitallending.dao.SysOperLogDao.logger;
import th.co.d1.digitallending.entity.ShelfLookup;
import th.co.d1.digitallending.entity.SysOperLog;
import th.co.d1.digitallending.util.DateUtils;
import static th.co.d1.digitallending.util.HibernateUtil.getSessionMaster;
import th.co.d1.digitallending.entity.SysErrorHandling;
import th.co.d1.digitallending.util.ValidUtils;

/**
 *
 * @author Ritthikriat
 */
public class SysErrorHandlingDao {

    Logger logger = Logger.getLogger(ShelfRoleFuncDao.class.getName());

    public JSONObject getListErrorhandling(String dbEnv, String txnId, String txnDateStart, String txnStartTime, String txnDateEnd, String txnEndTime, Integer status, String state) throws SQLException, UnsupportedEncodingException, ParseException {
        JSONObject ret = new JSONObject();
        PreparedStatement ps = null, psOperLog = null;
        ResultSet rs = null, stepDataRs = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            StringBuilder cmd = new StringBuilder();
            List params = new ArrayList<>();
            cmd.append("SELECT log.* ,LK.LOOKUP_CODE ST_CODE, LK.LOOKUP_NAME_EN STATE_NAME, LK.LOOKUP_NAME_TH, SL.LOOKUP_NAME_EN STATUS_NAME, SP.COMPANY COMPANY ,LK2.LOOKUP_NAME_TH ERR_NAME_TH, LK2.LOOKUP_NAME_EN ERR_NAME_EN, LK2.DESCRIPTION ERR_DESC, COMP.COMP_NAME COMPONENTNAME, ERR.UUID ERRUUID, ERR.PROD_CODE ERRPRODCODE, ERR.TXN_NO ERRTXNNO, ERR.STATUS ERRSTATUS, ERR.REMARK ERRREMARK, ERR.ATTR1 ERRATTR1, ERR.ATTR2 ERRATTR2, ERR.ATTR9 ERRATTR9, ERR.ATTR10 ERRATTR10, ERR.CREATE_AT ERRCREATEAT, ERR.CREATE_BY ERRCREATEBY, ERR.UPDATE_AT ERRUPDATEAT, ERR.UPDATE_BY ERRUPDATEBY, ERR.APPROVE_AT ERRAPPROVEAT, ERR.APPROVE_BY ERRAPPROVEBY "
                    + "FROM T_SYS_OPER_LOG LOG " 
                    + "INNER JOIN T_SHELF_LOOKUP LK ON LOG.STATE_CODE = LK.UUID  " 
                    + "INNER JOIN T_SYS_LOOKUP SL ON LOG.STATUS::TEXT = SL.LOOKUP_CODE " 
                    + "INNER JOIN T_SHELF_PRODUCT SP ON LOG.PRODUCT_ID = SP.UUID " 
                    + "INNER JOIN T_SYS_LOOKUP SL2 ON LOG.TRN_STATUS::TEXT = SL2.LOOKUP_CODE " 
                    + "INNER JOIN T_SHELF_COMP COMP ON LOG.PRODUCT_COMPONENT_ID = COMP.UUID " 
                    + "LEFT JOIN T_SYS_ERROR_HANDLING ERR ON LOG.TXN_NO = ERR.TXN_NO " 
                    + "LEFT JOIN T_SHELF_LOOKUP LK2 ON LOG.ATTR2 = LK2.LOOKUP_CODE  " 
                    + "WHERE log.uuid IN " 
                    + "( select uuid from ( " 
                    + "select uuid, row_number() over (partition by txn_no order by TO_TIMESTAMP( " 
                    + "log.ATTR4, 'yyyy-MM-dd HH24:MI:SS') desc, log.CREATE_AT desc) as ROW_NO " 
                    + "from t_sys_oper_log log )A  where A.ROW_NO = 1 ) " 
                    + "AND LOG.PRODUCT_COMPONENT_ID = COMP.UUID " 
                    + "AND LOG.STATE_CODE IN ('f5901202-0ed8-4f9a-a749-0832cff442b0', 'bf710db4-3625-4e68-a308-e1606a5e7155', '09c6873b-9a77-41fb-8ed6-14e94ed8bc56') ");
            if (null != state && !state.isEmpty()) {
                cmd.append(" AND LOG.STATE_CODE = (SELECT UUID FROM T_SHELF_LOOKUP WHERE GROUP_TYPE = 'PROCESS_STATE' AND LOOKUP_CODE = ?) ");
                params.add(state);
            }
            if (null != txnId && !txnId.isEmpty()) {
                cmd.append(" AND LOWER(LOG.TXN_NO) LIKE ? ");
                params.add("%" + txnId.toLowerCase() + "%");
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
                cmd.append(" AND LOG.CREATE_AT BETWEEN TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS') AND TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(txnDateStart + " " + txnStartTime);
                params.add(txnDateEnd + " " + txnEndTime);
            } else if ((null == txnDateStart || txnDateStart.isEmpty()) && (null != txnDateEnd && !txnDateEnd.isEmpty())) {
                cmd.append(" AND LOG.CREATE_AT <= TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(txnDateEnd + " " + txnEndTime);
            } else if ((null != txnDateStart && !txnDateStart.isEmpty()) && (null == txnDateEnd || txnDateEnd.isEmpty())) {
                cmd.append(" AND LOG.CREATE_AT >= TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(txnDateStart + " " + txnStartTime);
            }
            if (null != status) {
                cmd.append(" AND LOG.STATUS = ? ");
                params.add(status);
            }
            cmd.append(" ORDER BY LOG.CREATE_AT ASC");
            ps = session.doReturningWork((Connection conn) -> conn).prepareStatement(cmd.toString());
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
                
                cmd.setLength(0);
                cmd.append("SELECT STEP_DATA, PAYMENT_METHOD FROM T_SYS_OPER_LOG WHERE TXN_NO = ? AND STATE_CODE in (SELECT UUID FROM T_SHELF_LOOKUP WHERE GROUP_TYPE = 'PROCESS_STATE' AND LOOKUP_CODE in ('PRO1011','PRO1032'))"); //PRO1011
                psOperLog = session.doReturningWork((Connection conn) -> conn).prepareStatement(cmd.toString());
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
                            .put("componentName", !rs.getString("COMPONENTNAME").isEmpty() ? rs.getString("COMPONENTNAME") : "")
                            .put("erruuid", ValidUtils.null2NoData(rs.getString("ERRUUID")))
                            .put("errprodcode", ValidUtils.null2NoData(rs.getString("ERRPRODCODE")))
                            .put("errtxnNo", ValidUtils.null2NoData(rs.getString("ERRTXNNO")))
                            .put("errstatus", ValidUtils.null2NoData(rs.getString("ERRSTATUS")))
                            .put("errremark", ValidUtils.null2NoData(rs.getString("ERRREMARK")))
                            .put("errattr1", ValidUtils.null2NoData(rs.getString("ERRATTR1")))
                            .put("errattr2", ValidUtils.null2NoData(rs.getString("ERRATTR2")))
                            .put("errattr9", ValidUtils.null2NoData(rs.getString("ERRATTR9")))
                            .put("errattr10", ValidUtils.null2NoData(rs.getString("ERRATTR10")))
                            .put("errcreateAt", ValidUtils.null2NoData(rs.getString("ERRCREATEAT")))
                            .put("errcreateBy", ValidUtils.null2NoData(rs.getString("ERRCREATEBY")))
                            .put("errupdateAt", ValidUtils.null2NoData(rs.getString("ERRUPDATEAT")))
                            .put("errupdateBy", ValidUtils.null2NoData(rs.getString("ERRUPDATEBY")))
                            .put("errapproveAt", ValidUtils.null2NoData(rs.getString("ERRAPPROVEAT")))
                            .put("errapproveBy", ValidUtils.null2NoData(rs.getString("ERRAPPROVEBY")));
                    if (packageDataArr.length() == 0) {
                        String packageData = "[{'respDesc': '', 'code': '', 'data': { 'payDuedate': '', 'buDay': '', 'package': [], 'hpDueDate': '', 'channel': '', 'limit': '', 'hpNo': '', 'discount': '', 'term': '', 'payDate': '', 'lastDueDate': '' }, 'respCode': '', 'status': ''}]";
                        packageDataArr = new JSONArray(packageData);
                        cmd.setLength(0);
                        cmd.append("SELECT LK_VALUE PACKAGE_DATA FROM T_SHELF_PRODUCT_DTL "
                                + "WHERE TRN_UUID IN (SELECT UUID FROM T_SHELF_PRODUCT_VCS WHERE PROD_UUID = ? AND VER_PROD = ?) "
                                + "AND LK_CODE = 'summaryList'");
                        psOperLog = session.doReturningWork((Connection conn) -> conn).prepareStatement(cmd.toString());
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
                    cmd.setLength(0);
                    cmd.append("SELECT LK_CODE,LK_LABEL,LK_VALUE FROM T_SHELF_PRODUCT_DTL WHERE 1=1 "
                            + " AND TRN_UUID IN (SELECT UUID FROM T_SHELF_PRODUCT_VCS VCS WHERE VCS.PROD_UUID = ? AND VER_PROD = ? )"
                            + " AND LK_CODE IN ('cutOffTime','pcutOffDay','pcutOffWE','pcutOffTime','pcutOffTAllD','pcutOffTSpec', 'prodName', 'prodType')");
                    psOperLog = session.doReturningWork((Connection conn) -> conn).prepareStatement(cmd.toString());
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
                            .put("componentName", !rs.getString("COMPONENTNAME").isEmpty() ? rs.getString("COMPONENTNAME") : "")
                            .put("errtxnNo", ValidUtils.null2NoData(rs.getString("ERRTXNNO")))
                            .put("errstatus", ValidUtils.null2NoData(rs.getString("ERRSTATUS")))
                            .put("errremark", ValidUtils.null2NoData(rs.getString("ERRREMARK")))
                            .put("errattr1", ValidUtils.null2NoData(rs.getString("ERRATTR1")))
                            .put("errattr2", ValidUtils.null2NoData(rs.getString("ERRATTR2")))
                            .put("errattr9", ValidUtils.null2NoData(rs.getString("ERRATTR9")))
                            .put("errattr10", ValidUtils.null2NoData(rs.getString("ERRATTR10")))
                            .put("errcreateAt", ValidUtils.null2NoData(rs.getString("ERRCREATEAT")))
                            .put("errcreateBy", ValidUtils.null2NoData(rs.getString("ERRCREATEBY")))
                            .put("errupdateAt", ValidUtils.null2NoData(rs.getString("ERRUPDATEAT")))
                            .put("errupdateBy", ValidUtils.null2NoData(rs.getString("ERRUPDATEBY")))
                            .put("errapproveAt", ValidUtils.null2NoData(rs.getString("ERRAPPROVEAT")))
                            .put("errapproveBy", ValidUtils.null2NoData(rs.getString("ERRAPPROVEBY")));
                    if (packageDataArr.length() == 0) {
                        String packageData = "[{'respDesc': '', 'code': '', 'data': { 'payDuedate': '', 'buDay': '', 'package': [], 'hpDueDate': '', 'channel': '', 'limit': '', 'hpNo': '', 'discount': '', 'term': '', 'payDate': '', 'lastDueDate': '' }, 'respCode': '', 'status': ''}]";
                        packageDataArr = new JSONArray(packageData);
                        cmd.setLength(0);
                        cmd.append("SELECT LK_VALUE PACKAGE_DATA FROM T_SHELF_PRODUCT_DTL "
                                + "WHERE TRN_UUID IN (SELECT UUID FROM T_SHELF_PRODUCT_VCS WHERE PROD_UUID = ? AND VER_PROD = ?) "
                                + "AND LK_CODE = 'summaryList'");
                        psOperLog = session.doReturningWork((Connection conn) -> conn).prepareStatement(cmd.toString());
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

    public SysErrorHandling saveSysErrorHandling(String dbEnv, SysErrorHandling list) {

        Transaction trans = null;
        try {
            Session session = getSessionMaster(dbEnv).openSession();
            trans = session.beginTransaction();
            session.saveOrUpdate(list);
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            if (null != trans) {
                trans.rollback();
            }
        }
        return list;
    }

    public SysErrorHandling getSysErrorHandlingByUuid(String dbEnv, String uuid) {
        SysErrorHandling eh = new SysErrorHandling();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            eh = (SysErrorHandling) session.get(SysErrorHandling.class, uuid);
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            if (null != trans) {
                trans.rollback();
            }
            throw e;
        }
        return eh;
    }

    public JSONObject updateByUuid(String dbEnv, String Uuid, String username, int status, String Department) {
        Transaction trans = null;
        PreparedStatement ps = null;
        Date sysdate = new Date();
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            StringBuilder cmd = new StringBuilder();

            cmd.append("UPDATE T_SYS_ERROR_HANDLING A SET "
                    + "STATUS = " + status + " ,"
                    + "UPDATE_AT = '" + sysdate.toString() + "', "
                    + "UPDATE_BY = '" + username + "', "
                    + "ATTR2 = " + Department + ", "
                    + "ATTR10 = " + status + ", "
                    + "ATTR9 = CONCAT(A.ATTR9,'/'," + status + "/) "
                    + "where a.role_uuid = '" + Uuid + "' "
                    + "and a.status In (112,115) ");
            ps = session.doReturningWork((Connection conn) -> conn).prepareStatement(cmd.toString());

            ps.executeUpdate();
            trans.commit();
            ps.close();
            return new JSONObject().put("status", true).put("description", "");
        } catch (HibernateException | NullPointerException | SQLException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            if (null != trans) {
                trans.rollback();
            }
            return new JSONObject().put("status", false).put("description", "" + e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                logger.info(ex.getMessage());
            }
        }
    }

    
    
    public SysErrorHandling getSysErrorHandlingByTxnNo(String dbEnv, String txnNo) {
        SysErrorHandling err = new SysErrorHandling();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(SysErrorHandling.class);
            criteria.add(Restrictions.eq("txnNo", txnNo));
            List<SysErrorHandling> list = criteria.list();
            trans.commit();
            if (null != list && list.size() > 0) {
                err = list.get(0);
            }
        } catch (HibernateException | NullPointerException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            if (null != trans) {
                trans.rollback();
            }
            throw e;
        }
        return err;
    }
    
}
