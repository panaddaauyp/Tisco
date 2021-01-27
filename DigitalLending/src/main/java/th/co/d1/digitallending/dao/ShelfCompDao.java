/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import th.co.d1.digitallending.entity.ShelfComp;
import static th.co.d1.digitallending.util.HibernateUtil.getSessionMaster;

/**
 *
 * @author Poomsakul Senakul
 */
public class ShelfCompDao {

    Logger logger = Logger.getLogger(ShelfCompDao.class.getName());

    public List<ShelfComp> getListShelfComp(String dbEnv) {
        List<ShelfComp> shelfComp = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            int status = Integer.parseInt(new SysLookupDao().getMemLookupByValue(dbEnv, "active").getLookupcode());
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfComp.class);
            criteria.add(Restrictions.eq("status", status));
            criteria.addOrder(Order.asc("seqNo"));
            shelfComp = criteria.list();
            trans.commit();
        } catch (NumberFormatException | HibernateException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());  //e.printStackTrace();
            throw e;
        }
        return shelfComp;
    }

    public ShelfComp getShelfCompByUUID(String dbEnv, String uuid) {
        ShelfComp shelfComp = new ShelfComp();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            shelfComp = (ShelfComp) session.get(ShelfComp.class, uuid);
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());  //e.printStackTrace();
            throw e;
        }
        return shelfComp;
    }

    public ShelfComp saveShelfComp(Session session, ShelfComp shelfComp) {
        Transaction trans = null;
        try {
            trans = session.beginTransaction();
            session.save(shelfComp);
            trans.commit();
            return shelfComp;
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());  //e.printStackTrace();
            throw e;
        }
    }

    public ShelfComp updateShelfComp(Session session, ShelfComp shelfComp) {
        Transaction trans = null;
        try {
            trans = session.beginTransaction();
            session.update(shelfComp);
            trans.commit();
            return shelfComp;
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());  //e.printStackTrace();
            throw e;
        }
    }

}
