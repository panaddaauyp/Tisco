/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.dao;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import java.util.logging.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import th.co.d1.digitallending.entity.Memlookup;
import th.co.d1.digitallending.entity.ShelfLookup;
import th.co.d1.digitallending.entity.SysLookup;
import th.co.d1.digitallending.entity.SysOperLog;
import th.co.d1.digitallending.util.HibernateUtil;
import static th.co.d1.digitallending.util.HibernateUtil.*;
import th.co.d1.digitallending.util.StatusUtils;

/**
 *
 * @author Poomsakul Senakul
 */
public class SysLookupDao {

    Logger logger = Logger.getLogger(SysLookupDao.class.getName());

    /* get Lookup from Database to insert Mem */
    public List<SysLookup> getListLookup(String dbEnv) {
        List<SysLookup> lookup = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(SysLookup.class);
            lookup = criteria.list();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return lookup;
    }

    /* Mem DB */
    public List<Memlookup> getMemListLookup() {
        List<Memlookup> lookup = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMem().openSession()) {
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(Memlookup.class);
            lookup = criteria.list();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return lookup;
    }

    public Memlookup getMemLookupByUUID(String uuid) {
        Memlookup lookup = new Memlookup();
        Transaction trans = null;
        try (Session session = getSessionMem().openSession()) {
            trans = session.beginTransaction();
            lookup = (Memlookup) session.get(Memlookup.class, uuid);
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return lookup;
    }

    public Memlookup getMemLookupByCode(String dbEnv, String lookupCode) {
        Memlookup lookup = new Memlookup();
        Transaction trans = null;
        try (Session session = getSessionMem().openSession()) {
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(Memlookup.class);
            criteria.add(Restrictions.eq("lookupcode", lookupCode).ignoreCase());
            if (dbEnv == null || dbEnv.isEmpty()) {
                dbEnv = HibernateUtil.defaultDB;
            } else if (new HibernateUtil().master.get(dbEnv) == null) {
                dbEnv = HibernateUtil.defaultDB;
            }
            criteria.add(Restrictions.eq("attr10", dbEnv));
            lookup = (Memlookup) criteria.uniqueResult();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return lookup;
    }

    public Memlookup getMemLookupByValue(String dbEnv, String valueStr) {
        Memlookup lookup = new Memlookup();
        Transaction trans = null;
        try (Session session = getSessionMem().openSession()) {
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(Memlookup.class);
            criteria.add(Restrictions.eq("lookupvalue", valueStr).ignoreCase());
            if (dbEnv == null || dbEnv.isEmpty()) {
                dbEnv = HibernateUtil.defaultDB;
            } else if (HibernateUtil.master.get(dbEnv) == null) {
                dbEnv = HibernateUtil.defaultDB;
            }
            criteria.add(Restrictions.eq("attr10", dbEnv));
            lookup = (Memlookup) criteria.uniqueResult();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return lookup;
    }

    public List<Memlookup> getMemLookupByType(String dbEnv, String lookupType) {
        List<Memlookup> lookups = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMem().openSession()) {
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(Memlookup.class);
            criteria.add(Restrictions.eq("lookuptype", lookupType));
            if (dbEnv == null || dbEnv.isEmpty()) {
                dbEnv = HibernateUtil.defaultDB;
            } else if (HibernateUtil.master.get(dbEnv) == null) {
                dbEnv = HibernateUtil.defaultDB;
            }
            criteria.add(Restrictions.eq("attr10", dbEnv));
//            criteria.add(Restrictions.eq("status", "ACTIVE"));
//            criteria.addOrder(Order.asc("nameth"));
//            criteria.addOrder(Order.asc("attr3"));
            lookups = criteria.list();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return lookups;
    }

    public List<Memlookup> getListLookupFromSysOperLog(String dbEnv) {
        List<Memlookup> sysLookup = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<SysOperLog> cr = cb.createQuery(SysOperLog.class);
            Root<SysOperLog> root = cr.from(SysOperLog.class);
            cr.select(root.get("status"));
            Expression<Integer> groupByExp = root.get("status").as(Integer.class);
            cr.groupBy(groupByExp);
            Query sysOperLogCmd = session.createQuery(cr);
            List<Integer> listSysOperLogStatus = sysOperLogCmd.list();
            trans.commit();
//            session.close();
            Memlookup memLookup;
            for (Integer status : listSysOperLogStatus) {
                memLookup = getMemLookupByCode(dbEnv, String.valueOf(status));
                if (memLookup.getStatus().compareTo(StatusUtils.getActive(dbEnv).getStatusCode()) == 0) {
                    sysLookup.add(memLookup);
                }
            }
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return sysLookup;
    }

    public List<Memlookup> getListLookupFromTrnStatus(String dbEnv) {
        List<Memlookup> sysLookup = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<SysOperLog> cr = cb.createQuery(SysOperLog.class);
            Root<SysOperLog> root = cr.from(SysOperLog.class);
            cr.select(root.get("trnStatus"));
            Expression<Integer> groupByExp = root.get("trnStatus").as(Integer.class);
            cr.groupBy(groupByExp);
            Query sysOperLogCmd = session.createQuery(cr);
            List<Integer> listSysOperLogStatus = sysOperLogCmd.list();
            trans.commit();
//            session.close();
            Memlookup memLookup;
            for (Integer status : listSysOperLogStatus) {
                memLookup = getMemLookupByCode(dbEnv, String.valueOf(status));
                if (memLookup.getStatus().compareTo(StatusUtils.getActive(dbEnv).getStatusCode()) == 0) {
                    sysLookup.add(memLookup);
                }
            }
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return sysLookup;
    }

    public List<ShelfLookup> getListLookupFromProcError(String dbEnv) {
        List<ShelfLookup> sysLookup = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<SysOperLog> cr = cb.createQuery(SysOperLog.class);
            Root<SysOperLog> root = cr.from(SysOperLog.class);
            cr.select(root.get("attr2"));
            Expression<String> groupByExp = root.get("attr2").as(String.class);
            cr.groupBy(groupByExp);
            Query sysOperLogCmd = session.createQuery(cr);
            List<String> listSysOperLogErrProc = sysOperLogCmd.list();
            Criteria criteria = session.createCriteria(ShelfLookup.class);
            criteria.add(Restrictions.in("lookupCode", listSysOperLogErrProc));
            sysLookup = criteria.list();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return sysLookup;
    }

    public List<SysLookup> getListSysLookups(String dbEnv, List<String> lkValues) {
        List<SysLookup> list = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(SysLookup.class);
            criteria.add(Restrictions.in("lookupValue", lkValues));
            list = criteria.list();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return list;
    }

    public List<SysLookup> getListSysLookupsAll(String dbEnv) {
        List<SysLookup> list = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(SysLookup.class);
            list = criteria.list();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return list;
    }
}
