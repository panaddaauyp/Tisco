/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.dao;

import java.util.Date;
import java.util.logging.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import th.co.d1.digitallending.entity.ShelfProductAttach;
import static th.co.d1.digitallending.util.HibernateUtil.getSessionMaster;

/**
 *
 * @author Kritsana Sasai
 *
 * DataOne Asia (Thailand) Company Limited 1023 MS Siam Tower, 30th Floor, Rama
 * 3 Rd., Chongnonsi, Yannawa, Bangkok, 10120
 *
 * @create 09-07-2020 4:46:00 PM
 */
public class ShelfProductAttachDao {

    Logger logger = Logger.getLogger(ShelfProductAttachDao.class.getName());

    public ShelfProductAttach getShelfProductAttachByUUID(String dbEnv, String uuid) {
        ShelfProductAttach shelfProductAttach = new ShelfProductAttach();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            shelfProductAttach = (ShelfProductAttach) session.get(ShelfProductAttach.class, uuid);
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return shelfProductAttach;
    }

    public ShelfProductAttach createShelfProductAttach(String dbEnv, ShelfProductAttach shelfProductAttach) throws HibernateException, NullPointerException {
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            if (null == shelfProductAttach.getCreateAt()) {
                shelfProductAttach.setCreateAt(new Date());
            } else {
                shelfProductAttach.setUpdateAt(new Date());
            }
            session.saveOrUpdate(shelfProductAttach);
            trans.commit();
            return shelfProductAttach;
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
    }
}
