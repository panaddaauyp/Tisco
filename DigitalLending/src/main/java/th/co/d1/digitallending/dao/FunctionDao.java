/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.dao;

import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONException;
import org.json.JSONObject;
import th.co.d1.digitallending.util.DateUtils;
import static th.co.d1.digitallending.util.HibernateUtil.getSessionMaster;

/**
 *
 * @author Kritsana Sasai
 *
 * DataOne Asia (Thailand) Company Limited 1023 MS Siam Tower, 30th Floor, Rama
 * 3 Rd., Chongnonsi, Yannawa, Bangkok, 10120
 *
 * @create 06-02-2020 3:16:31 PM
 */
public class FunctionDao {

    private Session session;

    Logger logger = Logger.getLogger(FunctionDao.class);

    public JSONObject getProductNumber(String dbEnv, Date sysdate, String prodUuid, String prodCode, String username) {
        JSONObject result = new JSONObject().put("status", 200).put("description", "").put("data", "");
        try {
            session = getSessionMaster(dbEnv).openSession();
            Transaction trans = session.beginTransaction();
            EntityManager entityManager = session.getEntityManagerFactory().createEntityManager();
            StoredProcedureQuery funcProdNo = entityManager.createStoredProcedureQuery("f_get_product_number")
                    .registerStoredProcedureParameter("p_doc_date", java.sql.Date.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_prod_uuid", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_prod_code", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_user", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("output", String.class, ParameterMode.OUT)
                    .setParameter("p_doc_date", DateUtils.utilDateToSqlDate(sysdate))
                    .setParameter("p_prod_uuid", prodUuid)
                    .setParameter("p_prod_code", prodCode)
                    .setParameter("p_user", username);
            funcProdNo.execute();
            result.put("data", (String) funcProdNo.getOutputParameterValue("output"));
            entityManager.close();
            trans.commit();
            session.close();
        } catch (HibernateException | JSONException | NullPointerException e) {
            logger.error("" + e);
            e.printStackTrace();
            result.put("status", 500)
                    .put("description", "" + e);
        } finally {
            if (null != session) {
                session.close();
            }
        }
        return result;
    }

}
