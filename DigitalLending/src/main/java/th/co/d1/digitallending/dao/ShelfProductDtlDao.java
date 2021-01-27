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
import th.co.d1.digitallending.entity.ShelfProduct;
import th.co.d1.digitallending.entity.ShelfProductDtl;
import th.co.d1.digitallending.entity.ShelfProductVcs;
import th.co.d1.digitallending.util.HibernateUtil;
import th.co.d1.digitallending.util.StatusUtils;

public class ShelfProductDtlDao {

    Logger logger = Logger.getLogger(ShelfProductDtlDao.class);
    private Session session;

    public List<ShelfProductDtl> getListShelfProductDtl(String dbEnv) {
        List<ShelfProductDtl> shelfProductDtlList = new ArrayList<>();
        try {
            session = HibernateUtil.getSessionMaster(dbEnv).openSession();
            Transaction trans = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ShelfProductDtl> cr = cb.createQuery(ShelfProductDtl.class);
            Root<ShelfProductDtl> root = cr.from(ShelfProductDtl.class);
            cr.select(root);
            Query<ShelfProductDtl> prodDtlCmd = session.createQuery(cr);
            shelfProductDtlList = prodDtlCmd.getResultList();
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
        return shelfProductDtlList;
    }

    public ShelfProductDtl getShelfProductDtlByUUID(String dbEnv, String uuid) {
        ShelfProductDtl shelfProductDtl = new ShelfProductDtl();
        try {
            session = HibernateUtil.getSessionMaster(dbEnv).openSession();
            Transaction trans = session.beginTransaction();
            shelfProductDtl = (ShelfProductDtl) session.get(ShelfProductDtl.class, uuid);
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
        return shelfProductDtl;
    }

    public ShelfProductDtl createShelfProductDtl(String dbEnv, ShelfProductDtl shelfProductDtl) {
        Transaction trans = null;
        try {
            session = HibernateUtil.getSessionMaster(dbEnv).openSession();
            trans = session.beginTransaction();
            session.save(shelfProductDtl);
            trans.commit();
            session.close();
            return shelfProductDtl;
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

    public ShelfProductDtl updateShelfProductDtl(String dbEnv, ShelfProductDtl shelfProductDtl) {
        Transaction trans = null;
        try {
            session = HibernateUtil.getSessionMaster(dbEnv).openSession();
            trans = session.beginTransaction();
            session.update(shelfProductDtl);
            trans.commit();
            session.close();
            return shelfProductDtl;
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

    public List<ShelfProductDtl> getShelfProductDtlByCompUuidAndProductUuid(String dbEnv, String compUuid, String productUuid) {
        List<ShelfProductDtl> shelfProductDtlList = new ArrayList<>();
        StatusUtils.Status inprogressStatus = StatusUtils.getInprogress(dbEnv);
        try {
            session = HibernateUtil.getSessionMaster(dbEnv).openSession();
            Transaction trans = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ShelfProductVcs> cr = cb.createQuery(ShelfProductVcs.class);
            Root<ShelfProductVcs> root = cr.from(ShelfProductVcs.class);
            Join<ShelfProductVcs, ShelfProduct> prodUuidObject = root.join("prodUuid");
            Join<ShelfProductVcs, ShelfComp> compUuidObject = root.join("compUuid");
            cr.select(root).where(cb.and(cb.equal(prodUuidObject.get("uuid"), productUuid), cb.equal(compUuidObject.get("uuid"), compUuid)));
            Query<ShelfProductVcs> prodVcsCmd = session.createQuery(cr);
            List<ShelfProductVcs> shelfProductVcsList = prodVcsCmd.getResultList();
            String shelfProductVcsUuid = "";
            for (ShelfProductVcs vcs : shelfProductVcsList) {
                if (vcs.getStatus() == inprogressStatus.getStatusCode()) {
                    shelfProductVcsUuid = vcs.getUuid();
                    break;
                } else if (vcs.getVerProd() > 0 && vcs.getStatus() != inprogressStatus.getStatusCode()) {
                    shelfProductVcsUuid = vcs.getUuid();
                } else {
                    shelfProductVcsUuid = vcs.getUuid();
                }
            }
            CriteriaQuery<ShelfProductDtl> crDtl = cb.createQuery(ShelfProductDtl.class);
            Root<ShelfProductDtl> rootDtl = crDtl.from(ShelfProductDtl.class);
            Join<ShelfProductDtl, ShelfProductVcs> trnUuidObject = root.join("trnUuid");
            crDtl.select(rootDtl).where(cb.equal(trnUuidObject.get("uuid"), shelfProductVcsUuid));
            Query<ShelfProductDtl> prodDtlCmd = session.createQuery(crDtl);
            shelfProductDtlList = prodDtlCmd.getResultList();
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
        return shelfProductDtlList;
    }

}
