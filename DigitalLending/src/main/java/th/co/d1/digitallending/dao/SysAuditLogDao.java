/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.dao;

import java.util.logging.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import th.co.d1.digitallending.entity.SysAuditLog;
import static th.co.d1.digitallending.util.HibernateUtil.getSessionMaster;

/**
 *
 * @author Kritsana Sasai
 *
 * DataOne Asia (Thailand) Company Limited 900/29 Rama III Rd. Bangpongpang,
 * Yannawa, Bangkok 10120 Tel :+66 (0) 2682 3000
 *
 * @create 29-05-2020 5:32:01 PM
 */
public class SysAuditLogDao {

    Logger logger = Logger.getLogger(SysAuditLogDao.class.getName());

    public SysAuditLog getSysAuditLogByUUID(String dbEnv, String uuid) {
        SysAuditLog sysAuditLog = new SysAuditLog();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            sysAuditLog = (SysAuditLog) session.get(SysAuditLog.class, uuid);
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return sysAuditLog;
    }

    public SysAuditLog saveSysAuditLog(String dbEnv, SysAuditLog sysAuditLog) {
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            session.save(sysAuditLog);
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return sysAuditLog;
    }

    public SysAuditLog updateSysAuditLog(String dbEnv, SysAuditLog sysAuditLog) {
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            session.update(sysAuditLog);
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return sysAuditLog;
    }

    public SysAuditLog getSysAuditLogByLogName(String dbEnv, String logName) {
        SysAuditLog sysAuditLog = new SysAuditLog();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(SysAuditLog.class);
            criteria.add(Restrictions.eq("logName", logName));
            criteria.addOrder(Order.desc("createAt"));
            criteria.setMaxResults(1);
            sysAuditLog = (SysAuditLog) criteria.uniqueResult();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return sysAuditLog;
    }
}
