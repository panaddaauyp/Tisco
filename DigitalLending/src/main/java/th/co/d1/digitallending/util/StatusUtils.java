/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.util;

import java.util.logging.Logger;
import org.hibernate.HibernateException;
import th.co.d1.digitallending.dao.SysLookupDao;
import th.co.d1.digitallending.entity.Memlookup;

/**
 *
 * @author MEMEI
 */
public class StatusUtils {

    final static Logger logger = Logger.getLogger(StatusUtils.class.getName());

    public static Status getActive(String dbEnv) {
        try {
            Status status = new Status();
            Memlookup memLookup = new SysLookupDao().getMemLookupByValue(dbEnv, "active");
            status.setStatusCode(ValidUtils.str2BigInteger(memLookup.getLookupcode()));
            status.setStatusNameTh(memLookup.getLookupnameth());
            status.setStatusNameEn(memLookup.getLookupnameen());
            return status;
        } catch (NullPointerException | HibernateException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            return null;
        }
    }

    public static Status getInActive(String dbEnv) {
        try {
            Status status = new Status();
            Memlookup memLookup = new SysLookupDao().getMemLookupByValue(dbEnv, "inactive");
            status.setStatusCode(ValidUtils.str2BigInteger(memLookup.getLookupcode()));
            status.setStatusNameTh(memLookup.getLookupnameth());
            status.setStatusNameEn(memLookup.getLookupnameen());
            return status;
        } catch (NullPointerException | HibernateException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            return null;
        }
    }

    public static Status getWaittoApprove(String dbEnv) {
        try {
            Status status = new Status();
            Memlookup memLookup = new SysLookupDao().getMemLookupByValue(dbEnv, "waittoapprove");
            status.setStatusCode(ValidUtils.str2BigInteger(memLookup.getLookupcode()));
            status.setStatusNameTh(memLookup.getLookupnameth());
            status.setStatusNameEn(memLookup.getLookupnameen());
            return status;
        } catch (NullPointerException | HibernateException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            return null;
        }
    }

    public static Status getReject(String dbEnv) {
        try {
            Status status = new Status();
            Memlookup memLookup = new SysLookupDao().getMemLookupByValue(dbEnv, "reject");
            status.setStatusCode(ValidUtils.str2BigInteger(memLookup.getLookupcode()));
            status.setStatusNameTh(memLookup.getLookupnameth());
            status.setStatusNameEn(memLookup.getLookupnameen());
            return status;
        } catch (NullPointerException | HibernateException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            return null;
        }
    }

    public static Status getDelete(String dbEnv) {
        try {
            Status status = new Status();
            Memlookup memLookup = new SysLookupDao().getMemLookupByValue(dbEnv, "delete");
            status.setStatusCode(ValidUtils.str2BigInteger(memLookup.getLookupcode()));
            status.setStatusNameTh(memLookup.getLookupnameth());
            status.setStatusNameEn(memLookup.getLookupnameen());
            return status;
        } catch (NullPointerException | HibernateException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            return null;
        }
    }

    public static Status getTerminate(String dbEnv) {
        try {
            Status status = new Status();
            Memlookup memLookup = new SysLookupDao().getMemLookupByValue(dbEnv, "terminate");
            status.setStatusCode(ValidUtils.str2BigInteger(memLookup.getLookupcode()));
            status.setStatusNameTh(memLookup.getLookupnameth());
            status.setStatusNameEn(memLookup.getLookupnameen());
            return status;
        } catch (NullPointerException | HibernateException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            return null;
        }
    }

    public static Status getInprogress(String dbEnv) {
        try {
            Status status = new Status();
            Memlookup memLookup = new SysLookupDao().getMemLookupByValue(dbEnv, "inprogress");
            status.setStatusCode(ValidUtils.str2BigInteger(memLookup.getLookupcode()));
            status.setStatusNameTh(memLookup.getLookupnameth());
            status.setStatusNameEn(memLookup.getLookupnameen());
            return status;
        } catch (NullPointerException | HibernateException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            return null;
        }
    }

    public static Status getPause(String dbEnv) {
        try {
            Status status = new Status();
            Memlookup memLookup = new SysLookupDao().getMemLookupByValue(dbEnv, "pause");
            status.setStatusCode(ValidUtils.str2BigInteger(memLookup.getLookupcode()));
            status.setStatusNameTh(memLookup.getLookupnameth());
            status.setStatusNameEn(memLookup.getLookupnameen());
            return status;
        } catch (NullPointerException | HibernateException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            return null;
        }
    }

    public static Status getWaittoDelete(String dbEnv) {
        try {
            Status status = new Status();
            Memlookup memLookup = new SysLookupDao().getMemLookupByValue(dbEnv, "waittodelete");
            status.setStatusCode(ValidUtils.str2BigInteger(memLookup.getLookupcode()));
            status.setStatusNameTh(memLookup.getLookupnameth());
            status.setStatusNameEn(memLookup.getLookupnameen());
            return status;
        } catch (NullPointerException | HibernateException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            return null;
        }
    }

    public static Status getPass(String dbEnv) {
        try {
            Status status = new Status();
            Memlookup memLookup = new SysLookupDao().getMemLookupByValue(dbEnv, "pass");
            status.setStatusCode(ValidUtils.str2BigInteger(memLookup.getLookupcode()));
            status.setStatusNameTh(memLookup.getLookupnameth());
            status.setStatusNameEn(memLookup.getLookupnameen());
            return status;
        } catch (NullPointerException | HibernateException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            return null;
        }
    }

    public static Status getFail(String dbEnv) {
        try {
            Status status = new Status();
            Memlookup memLookup = new SysLookupDao().getMemLookupByValue(dbEnv, "fail");
            status.setStatusCode(ValidUtils.str2BigInteger(memLookup.getLookupcode()));
            status.setStatusNameTh(memLookup.getLookupnameth());
            status.setStatusNameEn(memLookup.getLookupnameen());
            return status;
        } catch (NullPointerException | HibernateException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            return null;
        }
    }

    public static Status getProspect(String dbEnv) {
        try {
            Status status = new Status();
            Memlookup memLookup = new SysLookupDao().getMemLookupByValue(dbEnv, "prospect");
            status.setStatusCode(ValidUtils.str2BigInteger(memLookup.getLookupcode()));
            status.setStatusNameTh(memLookup.getLookupnameth());
            status.setStatusNameEn(memLookup.getLookupnameen());
            return status;
        } catch (NullPointerException | HibernateException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            return null;
        }
    }

    public static Status getCancel(String dbEnv) {
        try {
            Status status = new Status();
            Memlookup memLookup = new SysLookupDao().getMemLookupByValue(dbEnv, "cancel");
            status.setStatusCode(ValidUtils.str2BigInteger(memLookup.getLookupcode()));
            status.setStatusNameTh(memLookup.getLookupnameth());
            status.setStatusNameEn(memLookup.getLookupnameen());
            return status;
        } catch (NullPointerException | HibernateException e) {
            logger.info(e.getMessage());
            return null;
        }
    }

    public static Status getExpired(String dbEnv) {
        try {
            Status status = new Status();
            Memlookup memLookup = new SysLookupDao().getMemLookupByValue(dbEnv, "expire");
            status.setStatusCode(ValidUtils.str2BigInteger(memLookup.getLookupcode()));
            status.setStatusNameTh(memLookup.getLookupnameth());
            status.setStatusNameEn(memLookup.getLookupnameen());
            return status;
        } catch (NullPointerException | HibernateException e) {
            logger.info(e.getMessage());
            return null;
        }
    }

    public static Status getNotUse(String dbEnv) {
        try {
            Status status = new Status();
            Memlookup memLookup = new SysLookupDao().getMemLookupByValue(dbEnv, "not use");
            status.setStatusCode(ValidUtils.str2BigInteger(memLookup.getLookupcode()));
            status.setStatusNameTh(memLookup.getLookupnameth());
            status.setStatusNameEn(memLookup.getLookupnameen());
            return status;
        } catch (NullPointerException | HibernateException e) {
            logger.info(e.getMessage());
            return null;
        }
    }

    public static Status getWaittoApprove2(String dbEnv) {
        try {
            Status status = new Status();
            Memlookup memLookup = new SysLookupDao().getMemLookupByValue(dbEnv, "waittoapprove2");
            status.setStatusCode(ValidUtils.str2BigInteger(memLookup.getLookupcode()));
            status.setStatusNameTh(memLookup.getLookupnameth());
            status.setStatusNameEn(memLookup.getLookupnameen());
            return status;
        } catch (NullPointerException | HibernateException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            return null;
        }
    }

    public static Status getStatusByCode(String dbEnv, String statusCode) {
        try {
            Status status = new Status();
            Memlookup memLookup = new SysLookupDao().getMemLookupByCode(dbEnv, statusCode);
            status.setStatusCode(ValidUtils.str2BigInteger(memLookup.getLookupcode()));
            status.setStatusNameTh(memLookup.getLookupnameth());
            status.setStatusNameEn(memLookup.getLookupnameen());
            return status;
        } catch (NullPointerException | HibernateException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            return null;
        }
    }

    public static Status getWaittoPause(String dbEnv) {
        try {
            Status status = new Status();
            Memlookup memLookup = new SysLookupDao().getMemLookupByValue(dbEnv, "waittopause");
            status.setStatusCode(ValidUtils.str2BigInteger(memLookup.getLookupcode()));
            status.setStatusNameTh(memLookup.getLookupnameth());
            status.setStatusNameEn(memLookup.getLookupnameen());
            return status;
        } catch (NullPointerException | HibernateException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            return null;
        }
    }

    public static Status getWaittoTerminate(String dbEnv) {
        try {
            Status status = new Status();
            Memlookup memLookup = new SysLookupDao().getMemLookupByValue(dbEnv, "waittoterminate");
            status.setStatusCode(ValidUtils.str2BigInteger(memLookup.getLookupcode()));
            status.setStatusNameTh(memLookup.getLookupnameth());
            status.setStatusNameEn(memLookup.getLookupnameen());
            return status;
        } catch (NullPointerException | HibernateException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            return null;
        }
    }

    public static Status getWaittoApprove3(String dbEnv) {
        try {
            Status status = new Status();
            Memlookup memLookup = new SysLookupDao().getMemLookupByValue(dbEnv, "waittoapprove3");
            status.setStatusCode(ValidUtils.str2BigInteger(memLookup.getLookupcode()));
            status.setStatusNameTh(memLookup.getLookupnameth());
            status.setStatusNameEn(memLookup.getLookupnameen());
            return status;
        } catch (NullPointerException | HibernateException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            return null;
        }
    }

    public static Status getWaittoStart(String dbEnv) {
        try {
            Status status = new Status();
            Memlookup memLookup = new SysLookupDao().getMemLookupByValue(dbEnv, "waittostart");
            status.setStatusCode(ValidUtils.str2BigInteger(memLookup.getLookupcode()));
            status.setStatusNameTh(memLookup.getLookupnameth());
            status.setStatusNameEn(memLookup.getLookupnameen());
            return status;
        } catch (NullPointerException | HibernateException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            return null;
        }
    }

    public static String getErrorMessageByCode(String dbEnv, String lookupCode) {
        try {
            Memlookup memLookup = new SysLookupDao().getMemLookupByCode(dbEnv, lookupCode);
            return memLookup.getLookupnameen();
        } catch (NullPointerException | HibernateException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            return "Data Not Found";
        }
    }

    public static class Status {

        private Integer statusCode;
        private String statusNameTh;
        private String statusNameEn;

        public Integer getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(Integer statusCode) {
            this.statusCode = statusCode;
        }

        public String getStatusNameTh() {
            return statusNameTh;
        }

        public void setStatusNameTh(String statusNameTh) {
            this.statusNameTh = statusNameTh;
        }

        public String getStatusNameEn() {
            return statusNameEn;
        }

        public void setStatusNameEn(String statusNameEn) {
            this.statusNameEn = statusNameEn;
        }

    }

    public static String setStatus(String currStatus, String nStatus) {
        String status = "";
        if ("".equals(ValidUtils.null2NoData(currStatus))) {
            status = nStatus;
        } else {
            String s[] = currStatus.split("/");
            if (s.length > 0) {
                if (!s[s.length - 1].equalsIgnoreCase(nStatus)) {
                    status = currStatus + "/" + nStatus;
                } else {
                    status = currStatus;
                }
            } else {
                status = nStatus;
            }

        }
        return status;
    }

    public static String setRemark(String currStatus, String nStatus) {
        String status = ValidUtils.null2NoData(currStatus) + "\n" + ValidUtils.null2NoData(nStatus);
        return status;
    }

    public static boolean isWaitTerminate(String state, Integer statusActive) {
        boolean isWait = false;
        String s[] = ValidUtils.null2Separator(state, "").split("/");
        if (s.length > 0 && s.length - 0 >= 0) {
            if (statusActive == Integer.parseInt(s[s.length - 2])) {
                isWait = true;
            }
        }
        return isWait;
    }
}
