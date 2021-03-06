/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.util.logging.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.json.JSONObject;
import th.co.d1.digitallending.entity.ShelfComp;
import th.co.d1.digitallending.entity.ShelfTmpDetail;
import static th.co.d1.digitallending.util.HibernateUtil.getSessionMaster;

/**
 *
 * @author Poomsakul Senakul
 */
public class ShelfTmpDetailDao {

    Logger logger = Logger.getLogger(ShelfTmpDetailDao.class.getName());

    public List<ShelfTmpDetail> getListShelfTmpAttach(String dbEnv) {
        List<ShelfTmpDetail> shelfTmpDetail = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfTmpDetail.class);
            criteria.add(Restrictions.eq("status", 1));
            shelfTmpDetail = criteria.list();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return shelfTmpDetail;
    }

    public ShelfTmpDetail getShelfTmpDetailByUUID(String dbEnv, String uuid) {
        ShelfTmpDetail shelfTmpDetail = new ShelfTmpDetail();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            shelfTmpDetail = (ShelfTmpDetail) session.get(ShelfTmpDetail.class, uuid);
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return shelfTmpDetail;
    }

    public ShelfTmpDetail getAttrByVcsUuid(String dbEnv, String vcsUuid) {
        ShelfTmpDetail shelfTmpDetail = new ShelfTmpDetail();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfTmpDetail.class);
            criteria.createAlias("vcsUuid", "vcs");
            criteria.createAlias("attUuid", "att");
            criteria.add(Restrictions.eq("vcs.uuid", vcsUuid));
            criteria.add(Restrictions.isNotNull("attUuid"));
            shelfTmpDetail = (ShelfTmpDetail) criteria.uniqueResult();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return shelfTmpDetail;

    }

    public JSONObject saveShelfTmpDetail(Session session, ShelfTmpDetail shelfTmpDetail) {
        Transaction trans = null;
        try {
            trans = session.beginTransaction();
            session.save(shelfTmpDetail);
            trans.commit();
            return new JSONObject().put("status", true).put("description", "");
        } catch (HibernateException | NullPointerException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            if (trans != null) {
                trans.rollback();
            }
            return new JSONObject().put("status", false).put("description", "" + e);
        }
    }

    public JSONObject updateShelfTmpDetail(Session session, ShelfTmpDetail shelfTmpDetail) {
        Transaction trans = null;
        try {
            trans = session.beginTransaction();
            session.update(shelfTmpDetail);
            trans.commit();
            return new JSONObject().put("status", true).put("description", "");
        } catch (HibernateException | NullPointerException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            if (trans != null) {
                trans.rollback();
            }
            return new JSONObject().put("status", false).put("description", "" + e);
        }
    }

    public JSONObject updateStatus(String dbEnv, String vcsUuid, int status) throws SQLException {
        Transaction trans = null;
        PreparedStatement ps = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            StringBuilder prodCmd = new StringBuilder();
            prodCmd.append("update t_shelf_tmp_detail set status = ? Where vcs_uuid = ?");
            ps = session.doReturningWork((Connection conn) -> conn).prepareStatement(prodCmd.toString());
            ps.setInt(1, status);
            ps.setString(2, vcsUuid);
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
            if (ps != null) {
                ps.close();
            }
        }
    }

    public ShelfComp getShelfComponentByCompUUIDAndTemplateVCS(String dbEnv, String compUuid, String templateVCS) {
        ShelfComp shelfComp = new ShelfComp();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ShelfTmpDetail> cr = cb.createQuery(ShelfTmpDetail.class);
            Root<ShelfTmpDetail> root = cr.from(ShelfTmpDetail.class);
            Join<ShelfTmpDetail, ShelfComp> compUuidObject = root.join("compUuid");
            Join<ShelfTmpDetail, ShelfTmpDetail> vcsUuidObject = root.join("vcsUuid");
            cr.select(root).where(cb.and(cb.equal(compUuidObject.get("uuid"), compUuid), cb.equal(vcsUuidObject.get("uuid"), templateVCS)));
            Query<ShelfTmpDetail> tmpDtlCmd = session.createQuery(cr);
            ShelfTmpDetail shelfTmpDtl = tmpDtlCmd.uniqueResult();
            shelfComp = shelfTmpDtl.getCompUuid();
//            System.out.println("tmpDtl SeqNO : " + shelfTmpDtl.getSeqNo());
            shelfComp.setSeqNo(shelfTmpDtl.getSeqNo());
//            System.out.println("shelfComp SeqNO : " + shelfTmpDtl.getSeqNo());
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (null != trans) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return shelfComp;
    }

    public List<ShelfTmpDetail> getTemplateDetailByTemplateVcsUuid(String dbEnv, String vcsUuid, int status) {
        List<ShelfTmpDetail> shelfTmpDetailList = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfTmpDetail.class);
            criteria.createAlias("vcsUuid", "vcs");
            criteria.add(Restrictions.eq("vcs.uuid", vcsUuid));
            if (status > 0) {
                criteria.add(Restrictions.eq("status", status));
            }
            shelfTmpDetailList = criteria.list();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (null != trans) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return shelfTmpDetailList;

    }

    public List<ShelfTmpDetail> getActiveInActiveByVcsUuid(String dbEnv, String vcsUuid) {
        List<ShelfTmpDetail> shelfTmpDetailList = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfTmpDetail.class);
            criteria.createAlias("vcsUuid", "vcs");
            criteria.add(Restrictions.eq("vcs.uuid", vcsUuid));
            shelfTmpDetailList = criteria.list();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (null != trans) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return shelfTmpDetailList;

    }

    public ShelfComp getShelfComponentByCompUUIDAndTemplateVCS(String dbEnv, String compUuid, String templateVCS, boolean compUseFlag) {
        ShelfComp shelfComp = new ShelfComp();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ShelfTmpDetail> cr = cb.createQuery(ShelfTmpDetail.class);
            Root<ShelfTmpDetail> root = cr.from(ShelfTmpDetail.class);
            Join<ShelfTmpDetail, ShelfComp> compUuidObject = root.join("compUuid");
            Join<ShelfTmpDetail, ShelfTmpDetail> vcsUuidObject = root.join("vcsUuid");
            cr.select(root).where(cb.and(cb.and(cb.equal(compUuidObject.get("uuid"), compUuid), cb.equal(vcsUuidObject.get("uuid"), templateVCS)), cb.equal(root.get("flagEnable"), compUseFlag)));
            Query<ShelfTmpDetail> tmpDtlCmd = session.createQuery(cr);
            ShelfTmpDetail shelfTmpDtl = tmpDtlCmd.uniqueResult();
            if (shelfTmpDtl == null) {
                shelfComp = null;
            } else {
                shelfComp = shelfTmpDtl.getCompUuid();
//            System.out.println("tmpDtl SeqNO : " + shelfTmpDtl.getSeqNo());
                shelfComp.setSeqNo(shelfTmpDtl.getSeqNo());
//            System.out.println("shelfComp SeqNO : " + shelfTmpDtl.getSeqNo());
            }
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (null != trans) {
                trans.rollback();
            }
            shelfComp = null;
            logger.info(e.getMessage());
            throw e;
        }
        return shelfComp;
    }
}
