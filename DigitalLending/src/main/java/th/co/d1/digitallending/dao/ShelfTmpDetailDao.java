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
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.json.JSONObject;
import th.co.d1.digitallending.entity.ShelfComp;
import th.co.d1.digitallending.entity.ShelfTmpDetail;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import org.apache.log4j.Logger;
import static th.co.d1.digitallending.util.HibernateUtil.getSessionMaster;

/**
 *
 * @author Poomsakul Senakul
 */
public class ShelfTmpDetailDao {

    private Session session;
    Logger logger = Logger.getLogger(ShelfTmpDetailDao.class);

    public List<ShelfTmpDetail> getListShelfTmpAttach(String dbEnv) {
        List<ShelfTmpDetail> shelfTmpDetail = new ArrayList<>();
        try {
            session = getSessionMaster(dbEnv).openSession();
            Transaction trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfTmpDetail.class);
            criteria.add(Restrictions.eq("status", 1));
            shelfTmpDetail = criteria.list();
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
        return shelfTmpDetail;
    }

    public ShelfTmpDetail getShelfTmpDetailByUUID(String dbEnv, String uuid) {
        ShelfTmpDetail shelfTmpDetail = new ShelfTmpDetail();
        try {
            session = getSessionMaster(dbEnv).openSession();
            Transaction trans = session.beginTransaction();
            shelfTmpDetail = (ShelfTmpDetail) session.get(ShelfTmpDetail.class, uuid);
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
        return shelfTmpDetail;
    }

    public ShelfTmpDetail getAttrByVcsUuid(String dbEnv, String vcsUuid) {
        ShelfTmpDetail shelfTmpDetail = new ShelfTmpDetail();

        try {
            session = getSessionMaster(dbEnv).openSession();
            Transaction trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfTmpDetail.class);
            criteria.createAlias("vcsUuid", "vcs");
            criteria.createAlias("attUuid", "att");
            criteria.add(Restrictions.eq("vcs.uuid", vcsUuid));
            criteria.add(Restrictions.isNotNull("attUuid"));
            shelfTmpDetail = (ShelfTmpDetail) criteria.uniqueResult();
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
            logger.error("" + e);
            e.printStackTrace();
            trans.rollback();
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
            logger.error("" + e);
            e.printStackTrace();
            trans.rollback();
            return new JSONObject().put("status", false).put("description", "" + e);
        }
    }

    public JSONObject updateStatus(String dbEnv, String vcsUuid, int status) {
        Transaction trans = null;
        Connection con = null;
        PreparedStatement ps = null;
        try {
            session = getSessionMaster(dbEnv).openSession();
            trans = session.beginTransaction();
            con = session.doReturningWork((Connection conn) -> conn);
            StringBuilder prodCmd = new StringBuilder();
            prodCmd.append("update t_shelf_tmp_detail set status = ? Where vcs_uuid = ?");
            ps = con.prepareStatement(prodCmd.toString());
            ps.setInt(1, status);
            ps.setString(2, vcsUuid);
            ps.executeUpdate();
            trans.commit();
            ps.close();
            session.close();
            return new JSONObject().put("status", true).put("description", "");
        } catch (HibernateException | NullPointerException | SQLException e) {
            logger.error("" + e);
            e.printStackTrace();
            if (null != trans) {
                trans.rollback();
            }
            return new JSONObject().put("status", false).put("description", "" + e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (null != session) {
                    session.close();
                }
//                if (!con.isClosed()) {
//                    con.close();
//                }
            } catch (SQLException ex) {
                logger.error("" + ex);
            }
        }
    }

    public ShelfComp getShelfComponentByCompUUIDAndTemplateVCS(String dbEnv, String compUuid, String templateVCS) {
        ShelfComp shelfComp = new ShelfComp();
        try {
            session = getSessionMaster(dbEnv).openSession();
            Transaction trans = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ShelfTmpDetail> cr = cb.createQuery(ShelfTmpDetail.class);
            Root<ShelfTmpDetail> root = cr.from(ShelfTmpDetail.class);
            Join<ShelfTmpDetail, ShelfComp> compUuidObject = root.join("compUuid");
            Join<ShelfTmpDetail, ShelfTmpDetail> vcsUuidObject = root.join("vcsUuid");
            cr.select(root).where(cb.and(cb.equal(compUuidObject.get("uuid"), compUuid), cb.equal(vcsUuidObject.get("uuid"), templateVCS)));
            Query<ShelfTmpDetail> tmpDtlCmd = session.createQuery(cr);
            ShelfTmpDetail shelfTmpDtl = tmpDtlCmd.getSingleResult();
            shelfComp = shelfTmpDtl.getCompUuid();
//            System.out.println("tmpDtl SeqNO : " + shelfTmpDtl.getSeqNo());
            shelfComp.setSeqNo(shelfTmpDtl.getSeqNo());
//            System.out.println("shelfComp SeqNO : " + shelfTmpDtl.getSeqNo());
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
        return shelfComp;
    }

    public List<ShelfTmpDetail> getTemplateDetailByTemplateVcsUuid(String dbEnv, String vcsUuid, int status) {
        List<ShelfTmpDetail> shelfTmpDetailList = new ArrayList<>();
        try {
            session = getSessionMaster(dbEnv).openSession();
            Transaction trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfTmpDetail.class);
            criteria.createAlias("vcsUuid", "vcs");
            criteria.add(Restrictions.eq("vcs.uuid", vcsUuid));
            if (status > 0) {
                criteria.add(Restrictions.eq("status", status));
            }
            shelfTmpDetailList = criteria.list();
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
        return shelfTmpDetailList;

    }
}
