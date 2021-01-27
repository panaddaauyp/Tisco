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
import javax.persistence.criteria.Root;
import java.util.logging.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import th.co.d1.digitallending.entity.ShelfLookup;
import static th.co.d1.digitallending.util.HibernateUtil.getSessionMaster;
import th.co.d1.digitallending.util.StatusUtils;

public class ShelfLookupDao {

    Logger logger = Logger.getLogger(ShelfLookupDao.class.getName());

    public List<ShelfLookup> getListShelfLookup(String dbEnv) {
        List<ShelfLookup> shelfLookupList = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ShelfLookup> cr = cb.createQuery(ShelfLookup.class);
            Root<ShelfLookup> root = cr.from(ShelfLookup.class);
            cr.select(root);
            Query<ShelfLookup> lookupCmd = session.createQuery(cr);
            shelfLookupList = lookupCmd.getResultList();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return shelfLookupList;
    }

    public ShelfLookup getShelfLookupByUUID(String dbEnv, String uuid) {
        ShelfLookup shelfLookup = new ShelfLookup();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            shelfLookup = (ShelfLookup) session.get(ShelfLookup.class, uuid);
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return shelfLookup;
    }

    public ShelfLookup saveShelfLookup(String dbEnv, ShelfLookup shelfLookup) {
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            session.save(shelfLookup);
            trans.commit();
            return shelfLookup;
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
    }

    public ShelfLookup updateShelfLookup(String dbEnv, ShelfLookup shelfLookup) {
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            session.update(shelfLookup);
            trans.commit();
            return shelfLookup;
        } catch (HibernateException | NullPointerException e) {
            trans.rollback();
            logger.info(e.getMessage());
            throw e;
        }
    }

    public ShelfLookup getShelfLookupByLkCode(String dbEnv, String lkCode, String lkType) {
        ShelfLookup shelfLookup = new ShelfLookup();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfLookup.class);
            criteria.add(Restrictions.eq("lookupType", lkType));
            criteria.add(Restrictions.eq("lookupCode", lkCode));
            List<ShelfLookup> list = criteria.list();
            trans.commit();
            if (null != list && list.size() > 0) {
                shelfLookup = list.get(0);
            }
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return shelfLookup;
    }

    public List<ShelfLookup> getShelfLookupByLkCode(String dbEnv, String lkCode, String groupType, String lkType) {
        List<ShelfLookup> list = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfLookup.class);
            if (null != lkCode && !"".equals(lkCode)) {
                criteria.add(Restrictions.eq("lookupCode", lkCode));
            }
            if (null != groupType && !"".equals(groupType)) {
                criteria.add(Restrictions.eq("groupType", groupType));
            }
            if (null != lkType && !"".equals(lkType)) {
                criteria.add(Restrictions.eq("lookupType", lkType));
            }
            criteria.addOrder(Order.asc("attr2"));
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

    public List<ShelfLookup> getActiveShelfLookupByGroupAndType(String dbEnv, String groupType, String lkType) {
        List<ShelfLookup> list = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfLookup.class);
            if (null != groupType && !"".equals(groupType)) {
                criteria.add(Restrictions.eq("groupType", groupType));
            }
            if (null != lkType && !"".equals(lkType)) {
                criteria.add(Restrictions.eq("lookupType", lkType));
            }
            criteria.add(Restrictions.eq("status", StatusUtils.getActive(dbEnv).getStatusCode()));
            criteria.addOrder(Order.asc("lookupCode"));
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
