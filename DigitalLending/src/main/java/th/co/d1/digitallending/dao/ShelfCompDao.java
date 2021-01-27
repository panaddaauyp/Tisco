/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.dao;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
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

    private Session session;

    Logger logger = Logger.getLogger(ShelfCompDao.class);

    public List<ShelfComp> getListShelfComp(String dbEnv) {
        List<ShelfComp> shelfComp = new ArrayList<>();
        try {
            int status = Integer.parseInt(new SysLookupDao().getMemLookupByValue(dbEnv, "active").getLookupcode());
            session = getSessionMaster(dbEnv).openSession();
            Transaction trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfComp.class);
            criteria.add(Restrictions.eq("status", status));
            criteria.addOrder(Order.asc("seqNo"));
            shelfComp = criteria.list();
            trans.commit();
            session.close();
        } catch (NumberFormatException | HibernateException e) {
            logger.error("" + e);  e.printStackTrace();
        } finally {
            if (null != session) {
                session.close();
            }
        }
        return shelfComp;
    }

    public ShelfComp getShelfCompByUUID(String dbEnv, String uuid) {
        ShelfComp shelfComp = new ShelfComp();
        try {
            session = getSessionMaster(dbEnv).openSession();
            Transaction trans = session.beginTransaction();
            shelfComp = (ShelfComp) session.get(ShelfComp.class, uuid);
            trans.commit();
            session.close();
        } catch (HibernateException | NullPointerException e) {
            logger.error("" + e);  e.printStackTrace();
        } finally {
            if (null != session) {
                session.close();
            }
        }
        return shelfComp;
    }

    public ShelfComp saveShelfComp(Session session, ShelfComp shelfComp) {
        try {
            Transaction trans = session.beginTransaction();
            session.save(shelfComp);
            trans.commit();
            return shelfComp;
        } catch (HibernateException | NullPointerException e) {
            logger.error("" + e);  e.printStackTrace();
            return null;
        }
    }

    public ShelfComp updateShelfComp(Session session, ShelfComp shelfComp) {
        try {
            Transaction trans = session.beginTransaction();
            session.update(shelfComp);
            trans.commit();
            return shelfComp;
        } catch (HibernateException | NullPointerException e) {
            logger.error("" + e);  e.printStackTrace();
            return null;
        }
    }

}
