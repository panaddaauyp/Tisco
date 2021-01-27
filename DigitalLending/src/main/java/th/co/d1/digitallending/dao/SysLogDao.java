/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import th.co.d1.digitallending.entity.SysLog;
import static th.co.d1.digitallending.util.HibernateUtil.getSessionMaster;
import th.co.d1.digitallending.util.StatusUtils;

/**
 *
 * @author Kritsana Sasai
 *
 * DataOne Asia (Thailand) Company Limited 1023 MS Siam Tower, 30th Floor, Rama
 * 3 Rd., Chongnonsi, Yannawa, Bangkok, 10120
 *
 * @create 18-02-2020 5:28:03 PM
 */
public class SysLogDao {

    private Session session;
    Logger logger = Logger.getLogger(SysLogDao.class);

    public SysLog saveSysLog(String dbEnv, String prodCode, String caseId, SysLog sysLog) {
        Transaction trans = null;
        try {
            session = getSessionMaster(dbEnv).openSession();
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(SysLog.class);
            criteria.add(Restrictions.eq("prodCode", prodCode));
            criteria.add(Restrictions.eq("caseId", caseId));
            List<SysLog> list = criteria.list();
            if (null == list) {
                if (null == sysLog.getCreateAt()) {
                    sysLog.setCreateAt(new Date());
                }
                session.save(sysLog);
            } else {
                if (sysLog.getStatus() == StatusUtils.getPass(dbEnv).getStatusCode()) {
                    SysLog sl = list.get(0);
                    sl.setStatus(sl.getStatus());
                    if (null == sl.getUpdateAt()) {
                        sl.setUpdateAt(new Date());
                    }
                    session.save(sl);
                }
            }
            trans.commit();
            session.close();
        } catch (HibernateException | NullPointerException e) {
            if (null != trans) {
                trans.rollback();
            }
            logger.error("" + e);
            e.printStackTrace();
        } finally {
            if (null != session) {
                session.close();
            }
        }
        return sysLog;
    }

    public List<SysLog> getSysLogByNotStatus(String dbEnv, int status) {
        List<SysLog> list = new ArrayList<>();
        Transaction trans = null;
        try {
            session = getSessionMaster(dbEnv).openSession();
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(SysLog.class);
            criteria.add(Restrictions.not(Restrictions.eq("status", status)));
            list = criteria.list();

            trans.commit();
            session.close();
        } catch (HibernateException | NullPointerException e) {
            logger.error("" + e);
            e.printStackTrace();
        } finally {
            if (null != session) {
                session.close();
            }
        }
        return list;
    }

    public SysLog getSysLogByUUID(String dbEnv, String uuid) {
        SysLog shelfProduct = new SysLog();
        try {
            session = getSessionMaster(dbEnv).openSession();
            Transaction trans = session.beginTransaction();
            shelfProduct = (SysLog) session.get(SysLog.class, uuid);
            trans.commit();
            session.close();
        } catch (HibernateException | NullPointerException e) {
            logger.error("" + e);
            e.printStackTrace();
        } finally {
            if (null != session) {
                session.close();
            }
        }
        return shelfProduct;
    }

}
