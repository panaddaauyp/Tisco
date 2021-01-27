/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import th.co.d1.digitallending.entity.ShelfRole;
import th.co.d1.digitallending.util.HibernateUtil;

/**
 *
 * @author Kritsana Sasai
 *
 * DataOne Asia (Thailand) Company Limited 1023 MS Siam Tower, 30th Floor, Rama
 * 3 Rd., Chongnonsi, Yannawa, Bangkok, 10120
 *
 * @create 05-05-2020 4:08:43 PM
 */
public class ShelfRoleDao {

    private Session session;

    Logger logger = Logger.getLogger(ShelfRoleDao.class);

    public List<ShelfRole> getShelfRoles(String dbEnv, String[] role, Integer status) {
        List<ShelfRole> list = new ArrayList<>();
        Transaction trans;
        try {
            List<String> roles = Arrays.asList(role);
            session = HibernateUtil.getSessionMaster(dbEnv).openSession();
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfRole.class);
            if (null != roles && roles.size() > 0) {
                criteria.add(Restrictions.in("roleId", roles));
            }
            if (null != status && status > 0) {
                criteria.add(Restrictions.eq("status", status));
            }
            list = criteria.list();
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
        return list;
    }
}
