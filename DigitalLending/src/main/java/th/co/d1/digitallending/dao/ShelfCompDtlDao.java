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
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import th.co.d1.digitallending.entity.ShelfComp;
import th.co.d1.digitallending.entity.ShelfCompDtl;
import th.co.d1.digitallending.util.HibernateUtil;

public class ShelfCompDtlDao {

    Logger logger = Logger.getLogger(ShelfCompDtlDao.class);
    private Session session;

    public List<ShelfCompDtl> getListShelfCompDtl(String dbEnv) {
        List<ShelfCompDtl> shelfCompDtlList = new ArrayList<>();
        try {
            session = HibernateUtil.getSessionMaster(dbEnv).openSession();
            Transaction trans = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ShelfCompDtl> cr = cb.createQuery(ShelfCompDtl.class);
            Root<ShelfCompDtl> root = cr.from(ShelfCompDtl.class);
            cr.select(root);
            Query<ShelfCompDtl> compDtlCmd = session.createQuery(cr);
            shelfCompDtlList = compDtlCmd.getResultList();
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
        return shelfCompDtlList;
    }

    public List<ShelfCompDtl> getListShelfCompDtlByCompUUID(String dbEnv, String compUUID) {
        List<ShelfCompDtl> shelfCompDtlList = new ArrayList<>();
        try {
            session = HibernateUtil.getSessionMaster(dbEnv).openSession();
            Transaction trans = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ShelfCompDtl> cr = cb.createQuery(ShelfCompDtl.class);
            Root<ShelfCompDtl> root = cr.from(ShelfCompDtl.class);
            Join<ShelfCompDtl, ShelfComp> joinObject = root.join("compUuid");
            cr.select(root).where(cb.equal(joinObject.get("uuid"), compUUID));
            cr.orderBy(cb.asc(root.get("seq")));
            Query<ShelfCompDtl> compDtlCmd = session.createQuery(cr);
            shelfCompDtlList = compDtlCmd.getResultList();
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
        return shelfCompDtlList;
    }

    public ShelfCompDtl getShelfCompDtlByUUID(String dbEnv, String uuid) {
        ShelfCompDtl shelfCompDtl = new ShelfCompDtl();
        try {
            session = HibernateUtil.getSessionMaster(dbEnv).openSession();
            Transaction trans = session.beginTransaction();
            shelfCompDtl = (ShelfCompDtl) session.get(ShelfCompDtl.class, uuid);
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
        return shelfCompDtl;
    }

    public ShelfCompDtl saveShelfCompDtl(String dbEnv, ShelfCompDtl shelfCompDtl) {
        Transaction trans = null;
        try {
            session = HibernateUtil.getSessionMaster(dbEnv).openSession();
            trans = session.beginTransaction();
            session.save(shelfCompDtl);
            trans.commit();
            session.close();
            return shelfCompDtl;
        } catch (HibernateException | NullPointerException e) {
            trans.rollback();
            logger.error("" + e);
            e.printStackTrace();
            return null;
        } finally {
            if (null != session) {
                session.close();
            }
        }
    }

    public ShelfCompDtl updateShelfCompDtl(String dbEnv, ShelfCompDtl shelfCompDtl) {
        Transaction trans = null;
        try {
            session = HibernateUtil.getSessionMaster(dbEnv).openSession();
            trans = session.beginTransaction();
            session.update(shelfCompDtl);
            trans.commit();
            session.close();
            return shelfCompDtl;
        } catch (HibernateException | NullPointerException e) {
            trans.rollback();
            logger.error("" + e);
            e.printStackTrace();
            return null;
        } finally {
            if (null != session) {
                session.close();
            }
        }
    }

}
