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
import org.hibernate.criterion.Restrictions;
import org.json.JSONObject;

import th.co.d1.digitallending.entity.SysRole;
import static th.co.d1.digitallending.util.HibernateUtil.*;

/**
 *
 * @author Poomsakul Senakul
 */
public class SysRoleDao {

    private Session session;
    Logger logger = Logger.getLogger(SysRoleDao.class);

    /* get Lookup from Database to insert Mem */
    public List<SysRole> getListSysRole(String dbEnv) {
        List<SysRole> lookup = new ArrayList<>();
        try {
            session = getSessionMaster(dbEnv).openSession();
            Transaction trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(SysRole.class);
            lookup = criteria.list();
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
        return lookup;
    }

    public JSONObject getRoleByUUID(String dbEnv, String roleUuid) {
        JSONObject returnVal = new JSONObject();
        try {
            session = getSessionMaster(dbEnv).openSession();
            Transaction trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(SysRole.class);
            SysRole sysRole = (SysRole) session.get(SysRole.class, roleUuid);
            criteria.add(Restrictions.eq("status", new SysLookupDao().getMemLookupByValue(dbEnv, "active")));
            trans.commit();
            session.close();
            returnVal = new JSONObject(sysRole.getPemission());
        } catch (HibernateException | NullPointerException e) {
            logger.error("" + e);
            e.printStackTrace();
        } finally {
            if (null != session) {
                session.close();
            }
        }
        return returnVal;
    }

    public JSONObject getRoleById(String dbEnv, String roleId) {
        JSONObject returnVal = new JSONObject();
        try {
            session = getSessionMaster(dbEnv).openSession();
            Transaction trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(SysRole.class);
            criteria.add(Restrictions.eq("roleId", roleId).ignoreCase());
            criteria.add(Restrictions.eq("status", new SysLookupDao().getMemLookupByValue(dbEnv, "active")));
            SysRole sysRole = (SysRole) criteria.uniqueResult();
            trans.commit();
            session.close();
            returnVal = new JSONObject(sysRole.getPemission());
        } catch (HibernateException | NullPointerException e) {
            logger.error("" + e);
            e.printStackTrace();
        } finally {
            if (null != session) {
                session.close();
            }
        }
        return returnVal;
    }

    public JSONObject getRoleByCode(String dbEnv, String roleCode) {
        JSONObject returnVal = new JSONObject();
        try {
            session = getSessionMaster(dbEnv).openSession();
            Transaction trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(SysRole.class);
            criteria.add(Restrictions.eq("roleCode", roleCode).ignoreCase());
            criteria.add(Restrictions.eq("status", Integer.parseInt(new SysLookupDao().getMemLookupByValue(dbEnv, "active").getLookupcode())));
            SysRole sysRole = (SysRole) criteria.uniqueResult();
            trans.commit();
            session.close();
            returnVal = new JSONObject(sysRole.getPemission());
        } catch (HibernateException | NullPointerException e) {
            logger.error("" + e);
            e.printStackTrace();
        } finally {
            if (null != session) {
                session.close();
            }
        }
        return returnVal;
    }

}
