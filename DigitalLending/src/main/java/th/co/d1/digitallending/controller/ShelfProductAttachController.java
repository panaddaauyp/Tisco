/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.controller;

import com.tfglog.LogSingleton;
import com.tfglog.Log_decorator;
import com.tfglog.TfgLogger;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.hibernate.HibernateException;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import th.co.d1.digitallending.dao.ShelfProductAttachDao;
import th.co.d1.digitallending.entity.ShelfProductAttach;
import static th.co.d1.digitallending.util.ApplicationStartup.headersJSON;
import th.co.d1.digitallending.util.StatusUtils;
import th.co.d1.digitallending.util.Utils;
import th.co.d1.digitallending.util.ValidUtils;

/**
 *
 * @author Kritsana Sasai
 *
 * DataOne Asia (Thailand) Company Limited 1023 MS Siam Tower, 30th Floor, Rama
 * 3 Rd., Chongnonsi, Yannawa, Bangkok, 10120
 *
 * @create 09-07-2020 5:05:54 PM
 */
@Controller
@RequestMapping("/shelf/attach/v1")
public class ShelfProductAttachController {

    final static Logger logger = Logger.getLogger(ShelfProductAttachController.class.getName());
    final static TfgLogger log = LogSingleton.getTfgLogger();

    @Log_decorator
    @RequestMapping(value = ("file/upload"), method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> uploadFile(HttpServletResponse response, HttpServletRequest request, HttpSession session, @RequestHeader(value = "sub_state", required = false) String subState, @RequestBody String reqBody) {
        log.info("POST : /shelf/attach/v1/file/upload");
        logger.info("POST : /shelf/attach/v1/file/upload");
        JSONObject returnVal = new JSONObject().put("status", 500).put("description", "").put("data", new JSONObject());
        try {
            JSONObject detailObj = new JSONObject(reqBody);
            String username = "", businessDept = "", businessLine = "";
            if (detailObj.has("userInfo")) {
                JSONObject objUser = detailObj.getJSONObject("userInfo");
                username = (objUser.has("username") ? objUser.getString("username") : "");
                businessDept = (objUser.has("businessDept") ? objUser.getString("businessDept") : "");
                businessLine = (objUser.has("businessLine") ? objUser.getString("businessLine") : "");
            }
            if (detailObj.has("data")) {
                JSONObject objData = detailObj.getJSONObject("data");
                ShelfProductAttachDao dao = new ShelfProductAttachDao();
                ShelfProductAttach attach = null;
                if (objData.has("uuid") && !objData.getString("uuid").trim().isEmpty()) {
                    attach = dao.getShelfProductAttachByUUID(subState, objData.getString("uuid"));
                    attach.setUpdateBy(username);
                }
                if (null == attach) {
                    attach = new ShelfProductAttach();
                    attach.setUuid(Utils.getUUID());
                    attach.setCreateBy(username);
                }
                attach.setFileType(objData.getString("type"));
                attach.setFileName(objData.getString("fileName"));
                attach.setFileValue(objData.getString("fileUpload"));

                attach = dao.createShelfProductAttach(subState, attach);
                if (null != attach) {
                    returnVal.put("status", 200)
                            .put("data", new JSONObject().put("attachid", attach.getUuid()));
                }
            } else {
                returnVal.put("description", StatusUtils.getErrorMessageByCode(subState, "SHELF0040"));
                return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.BAD_REQUEST));
            }
        } catch (JSONException | NullPointerException | HibernateException e) {
            log.info("" + e);
            logger.info(e.getMessage());
            returnVal.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
            returnVal.put("description", e.getMessage());
            e.printStackTrace();
            return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.INTERNAL_SERVER_ERROR));
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @Log_decorator
    @RequestMapping(value = ("file/download"), method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> downloadFile(HttpServletResponse response, HttpServletRequest request, HttpSession session, @RequestHeader(value = "sub_state", required = false) String subState, @RequestBody String reqBody) {
        log.info("POST : /shelf/attach/v1/file/download");
        logger.info("POST : /shelf/attach/v1/file/download");
        JSONObject returnVal = new JSONObject().put("status", 500).put("description", "").put("data", new JSONObject());
        try {
            JSONObject detailObj = new JSONObject(reqBody);
            String username = "", businessDept = "", businessLine = "";
            if (detailObj.has("userInfo")) {
                JSONObject objUser = detailObj.getJSONObject("userInfo");
                username = (objUser.has("username") ? objUser.getString("username") : "");
                businessDept = (objUser.has("businessDept") ? objUser.getString("businessDept") : "");
                businessLine = (objUser.has("businessLine") ? objUser.getString("businessLine") : "");
            }
            if (detailObj.has("data")) {
                JSONObject objData = detailObj.getJSONObject("data");
                ShelfProductAttachDao dao = new ShelfProductAttachDao();
                ShelfProductAttach attach = null;
                if (objData.has("attachid") && !objData.getString("attachid").trim().isEmpty()) {
                    attach = dao.getShelfProductAttachByUUID(subState, objData.getString("attachid"));
                    returnVal.put("status", 200)
                            .put("data", new JSONObject().put("attachid", attach.getUuid())
                                    .put("type", ValidUtils.null2NoData(attach.getFileType()))
                                    .put("fileName", ValidUtils.null2NoData(attach.getFileName()))
                                    .put("fileUpload", ValidUtils.null2NoData(attach.getFileValue())));
                }
            } else {
                returnVal.put("description", StatusUtils.getErrorMessageByCode(subState, "SHELF0040"));
                return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.BAD_REQUEST));
            }
        } catch (JSONException | NullPointerException | HibernateException e) {
            log.info("" + e);
            logger.info(e.getMessage());
            returnVal.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
            returnVal.put("description", e.getMessage());
            return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.INTERNAL_SERVER_ERROR));
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }
}
