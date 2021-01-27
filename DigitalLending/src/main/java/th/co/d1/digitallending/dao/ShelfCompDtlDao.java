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
import java.util.logging.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import th.co.d1.digitallending.entity.ShelfComp;
import th.co.d1.digitallending.entity.ShelfCompDtl;
import static th.co.d1.digitallending.util.HibernateUtil.getSessionMaster;

public class ShelfCompDtlDao {

    Logger logger = Logger.getLogger(ShelfCompDtlDao.class.getName());

    public List<ShelfCompDtl> getListShelfCompDtl(String dbEnv) {
        List<ShelfCompDtl> shelfCompDtlList = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ShelfCompDtl> cr = cb.createQuery(ShelfCompDtl.class);
            Root<ShelfCompDtl> root = cr.from(ShelfCompDtl.class);
            cr.select(root);
            Query<ShelfCompDtl> compDtlCmd = session.createQuery(cr);
            shelfCompDtlList = compDtlCmd.getResultList();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return shelfCompDtlList;
    }

    public List<ShelfCompDtl> getListShelfCompDtlByCompUUID(String dbEnv, String compUUID) {
        List<ShelfCompDtl> shelfCompDtlList = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ShelfCompDtl> cr = cb.createQuery(ShelfCompDtl.class);
            Root<ShelfCompDtl> root = cr.from(ShelfCompDtl.class);
            Join<ShelfCompDtl, ShelfComp> joinObject = root.join("compUuid");
            cr.select(root).where(cb.equal(joinObject.get("uuid"), compUUID));
            cr.orderBy(cb.asc(root.get("seq")));
            Query<ShelfCompDtl> compDtlCmd = session.createQuery(cr);
            shelfCompDtlList = compDtlCmd.getResultList();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return shelfCompDtlList;
    }

    public ShelfCompDtl getShelfCompDtlByUUID(String dbEnv, String uuid) {
        ShelfCompDtl shelfCompDtl = new ShelfCompDtl();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            shelfCompDtl = (ShelfCompDtl) session.get(ShelfCompDtl.class, uuid);
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return shelfCompDtl;
    }

    public ShelfCompDtl saveShelfCompDtl(String dbEnv, ShelfCompDtl shelfCompDtl) {
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            session.save(shelfCompDtl);
            trans.commit();
            return shelfCompDtl;
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
    }

    public ShelfCompDtl updateShelfCompDtl(String dbEnv, ShelfCompDtl shelfCompDtl) {
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            session.update(shelfCompDtl);
            trans.commit();
            return shelfCompDtl;
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
    }

}
