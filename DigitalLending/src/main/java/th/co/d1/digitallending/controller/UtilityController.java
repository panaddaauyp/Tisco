/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import org.springframework.web.bind.annotation.ResponseBody;
import th.co.d1.digitallending.dao.ShelfLookupDao;
import th.co.d1.digitallending.dao.ShelfProductDao;
import th.co.d1.digitallending.dao.ShelfProductDtlDao;
import th.co.d1.digitallending.dao.SysAuditLogDao;
import th.co.d1.digitallending.dao.SysLookupDao;
import th.co.d1.digitallending.dao.SysOperLogDao;
import th.co.d1.digitallending.entity.ShelfLookup;
import th.co.d1.digitallending.entity.ShelfProduct;
import th.co.d1.digitallending.entity.ShelfProductDtl;
import th.co.d1.digitallending.entity.SysAuditLog;
import th.co.d1.digitallending.entity.SysLookup;
import static th.co.d1.digitallending.util.ApplicationStartup.headersJSON;
import th.co.d1.digitallending.util.ConstainValue;
import static th.co.d1.digitallending.util.HibernateUtil.getSessionMaster;
import th.co.d1.digitallending.util.LookupUtils;
import th.co.d1.digitallending.util.Utils;
import th.co.d1.digitallending.util.ValidUtils;

/**
 *
 * @author iConTroL
 */
@Controller
public class UtilityController {

    final static Logger logger = Logger.getLogger(UtilityController.class);

    /*
    @RequestMapping(value = "/api/en_de_code", method = GET)
    @ResponseBody
    public String encodeAndDecode(HttpServletRequest request, HttpServletResponse response, HttpSession session, String type, String value) throws IOException, Exception {
        logger.info("GET : /api/en_de_code");
        try {
//            System.out.println("/api/en_de_code" + " | type : " + type + " | value : " + value);
            EncryptAndDecrypt nCrypeDcrypt = new EncryptAndDecrypt();
            if (type.equals("e")) {
                return nCrypeDcrypt.enCodeValue(value);
            } else if (type.equals("d")) {
                return nCrypeDcrypt.deCodeValue(value);
            }
        } catch (Exception e) {
            logger.error("" + e);
            e.printStackTrace();
//            Logger.getLogger(UtilityController.class.getName()).log(Level.SEVERE, null, e);
            return "error";
        }
        return "Sorry ,You don't have pemission";
    }
     */
    @RequestMapping(value = "/api/database", method = GET)
    @ResponseBody
    public void getDatabaseConnect(HttpServletRequest request) {
        logger.info("GET : /api/database");
        getSessionMaster(Utils.validateSubStateFromHeader(request));
    }

    @RequestMapping(value = "/", method = GET)
    public String getLandingPage() {
        logger.info("GET : /");
        return "index";
    }

    @RequestMapping(value = "/App-HealthCheck", method = GET)
    public String getHealthCheck() {
        logger.info("GET : /App-HealthCheck");
        return "index";
    }

    @RequestMapping(value = "/api/question/{productUuid}", method = GET)
    @ResponseBody
    public ResponseEntity<?> getRandomQuestion(HttpSession session, HttpServletResponse response, HttpServletRequest request, @PathVariable String prodUuid) {
        logger.info("GET : /api/question/" + prodUuid);
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
        try {
            ShelfProductDtlDao shelfProductDtlDao = new ShelfProductDtlDao();
            List<ShelfProductDtl> shelfProductDtlList = shelfProductDtlDao.getShelfProductDtlByCompUuidAndProductUuid(Utils.validateSubStateFromHeader(request), "a34404a3-309e-4057-8f53-f386d026656b", prodUuid);
            JSONObject questionObject = new JSONObject();
            for (ShelfProductDtl shelfProductDtl : shelfProductDtlList) {
                if (shelfProductDtl.getLkCode().equalsIgnoreCase("otpNumberQuestion")) {
                    questionObject.put("otpNumberQuestion", shelfProductDtl.getLkValue());
                } else if (shelfProductDtl.getLkCode().equalsIgnoreCase("numberCorrectAnswer")) {
                    questionObject.put("numberCorrectAnswer", shelfProductDtl.getLkValue());
                } else if (shelfProductDtl.getLkCode().equalsIgnoreCase("inCorrectAnswer")) {
                    questionObject.put("inCorrectAnswer", shelfProductDtl.getLkValue());
                } else if (shelfProductDtl.getLkCode().equalsIgnoreCase("g004")) {
                    questionObject.put("g004", shelfProductDtl.getLkValue());
                }
            }
            if (questionObject.has("g004")) {
                List<JSONObject> questionList = new ArrayList<>();
                JSONArray listQuestion = questionObject.getJSONArray("g004");
                for (int i = 0; i < listQuestion.length(); i++) {
                    JSONObject questiionObject = listQuestion.getJSONObject(i);
                    questionList.add(questiionObject);
                }
                Collections.shuffle(questionList);
                listQuestion = new JSONArray();
                for (int j = 0; j < questionObject.getInt("otpNumberQuestion"); j++) {
                    listQuestion.put(questionList.get(j));
                }
                questionObject.put("g004", listQuestion);
            }
        } catch (JSONException | NullPointerException e) {
//            e.printStackTrace();
            logger.error("" + e);
            e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    /*
    @RequestMapping(value = "/api/export/pdf", method = GET)
    @ResponseBody
    public ResponseEntity<?> exportPDF(HttpSession session, HttpServletResponse response, HttpServletRequest request) {
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", new JSONObject());
        Utils utils = new Utils();
        try {            
            JSONObject parameter = new JSONObject();
            parameter.put("contract_no", "XXX-XXXX-XXX");
            parameter.put("contract_date", "02/02/2020");
            parameter.put("customer_name", "นายทดสอบ ยื่นสินเชื่อ");
            parameter.put("customer_address", "tisco");
            parameter.put("loan_amount", "50,000");
            parameter.put("loan_amount_text", ThaiBaht.getThaiBaht(new BigDecimal("50,000".replaceAll(",", ""))));
            parameter.put("interest_rate", "3");
            parameter.put("effective_interest_rate", "4");
            parameter.put("flat_interest_rate", "5");
            parameter.put("loan_fee", "500");
            parameter.put("period", "60");
            parameter.put("first_to_period", "60");
            parameter.put("amount_per_period", "1,000");
            parameter.put("period_number", "1");
            parameter.put("to_period", "60");
            parameter.put("amount_per_period_s", "1,000");
            parameter.put("due_date", "01");
            parameter.put("first_period_date", "01/04/2020");
            parameter.put("contract_no_s", "XXXXXX-XXXXX-XX");
            parameter.put("contract_date_s", "01/03/2018");
            parameter.put("asset_detail", "ทดสอบรายละเอียด");
            parameter.put("brand", "Honda");
            parameter.put("register_no", "กก 9999");
            parameter.put("province", "กรุงเทพมหานคร");
            parameter.put("register_year", "2018");
            parameter.put("machine_no", "MC-XXXX");
            parameter.put("vehicle_no", "MM-MMMM-MMMM");
            String outputPath = "C://TISCO/PDF";
            JSONObject result = utils.generatePDFIncreaseLimitContract(parameter, outputPath);
        } catch (JSONException e) {
            e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }    
     */
    @RequestMapping(value = "/api/update/memory-database", method = PUT)
    @ResponseBody
    public ResponseEntity<?> updateMemoryDatabase(HttpServletRequest request) {
        logger.info("PUT : /api/update/memory-database");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "");
        try {
            int ret = new ConstainValue().updateLookUp();
            String retMsg = "";
            if (ret == 1) {
                retMsg = "Memory database updated.";
            } else {
                retMsg = "Could'n update memory database";
            }
            returnVal.put("status", ret == 1 ? 200 : 500)
                    .put("description", retMsg);
        } catch (JSONException | NullPointerException e) {
            logger.error("" + e);
            e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @RequestMapping(value = "/api/producttype", method = GET)
    @ResponseBody
    public ResponseEntity<?> getProductTypeList(HttpServletRequest request) {
        logger.info("GET : /api/producttype");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            returnVal.put("datas", new LookupUtils().getDefaultProductType(Utils.validateSubStateFromHeader(request)));
        } catch (JSONException e) {
            e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @RequestMapping(value = "/api/productchannel", method = GET)
    @ResponseBody
    public ResponseEntity<?> getProductChannelList(HttpServletRequest request) {
        logger.info("GET : /api/productchannel");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            returnVal.put("datas", new LookupUtils().getDefaultProductType(Utils.validateSubStateFromHeader(request)));
        } catch (JSONException e) {
            e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @RequestMapping(value = "/api/prodgroup", method = GET)
    @ResponseBody
    public ResponseEntity<?> getProductGroupList(HttpServletRequest request) {
        logger.info("GET : /api/prodgroup");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            returnVal.put("datas", new LookupUtils().getDefaultProductGroup(Utils.validateSubStateFromHeader(request)));
        } catch (JSONException e) {
            e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @RequestMapping(value = "/api/temp/list/approve", method = GET)
    @ResponseBody
    public ResponseEntity<?> getTemplateApproveList(HttpServletRequest request) {
        logger.info("GET : /api/temp/list/approve");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            returnVal.put("datas", new LookupUtils().getDefaultLookupByGroupType(Utils.validateSubStateFromHeader(request), "DEF_TMP_APPROVE"));
        } catch (JSONException e) {
            e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @RequestMapping(value = "/api/temp/list/reject", method = GET)
    @ResponseBody
    public ResponseEntity<?> getTemplateRejectList(HttpServletRequest request) {
        logger.info("GET : /api/temp/list/reject");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            returnVal.put("datas", new LookupUtils().getDefaultLookupByGroupType(Utils.validateSubStateFromHeader(request), "DEF_TMP_REJECT"));
        } catch (JSONException e) {
            e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @RequestMapping(value = "/api/temp/list/delete", method = GET)
    @ResponseBody
    public ResponseEntity<?> getTemplateDeleteList(HttpServletRequest request) {
        logger.info("GET : /api/temp/list/delete");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            returnVal.put("datas", new LookupUtils().getDefaultLookupByGroupType(Utils.validateSubStateFromHeader(request), "DEF_TMP_DELETE"));
        } catch (JSONException e) {
            e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @RequestMapping(value = "/api/temp/list/pause", method = GET)
    @ResponseBody
    public ResponseEntity<?> getTemplatePauseList(HttpServletRequest request) {
        logger.info("GET : /api/temp/list/pause");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            returnVal.put("datas", new LookupUtils().getDefaultLookupByGroupType(Utils.validateSubStateFromHeader(request), "DEF_TMP_PAUSE"));
        } catch (JSONException e) {
            e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @RequestMapping(value = "/api/product/list/approve", method = GET)
    @ResponseBody
    public ResponseEntity<?> getProductApproveList(HttpServletRequest request) {
        logger.info("GET : /api/product/list/approve");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            returnVal.put("datas", new LookupUtils().getDefaultLookupByGroupType(Utils.validateSubStateFromHeader(request), "DEF_PROD_APPROVE"));
        } catch (JSONException e) {
            e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @RequestMapping(value = "/api/product/list/reject", method = GET)
    @ResponseBody
    public ResponseEntity<?> getProductRejectList(HttpServletRequest request) {
        logger.info("GET : /api/product/list/reject");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            returnVal.put("datas", new LookupUtils().getDefaultLookupByGroupType(Utils.validateSubStateFromHeader(request), "DEF_PROD_REJECT"));
        } catch (JSONException e) {
            e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @RequestMapping(value = "/api/product/list/delete", method = GET)
    @ResponseBody
    public ResponseEntity<?> getProductDeleteList(HttpServletRequest request) {
        logger.info("GET : /api/product/list/delete");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            returnVal.put("datas", new LookupUtils().getDefaultLookupByGroupType(Utils.validateSubStateFromHeader(request), "DEF_PROD_DELETE"));
        } catch (JSONException e) {
            e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @RequestMapping(value = "/api/product/list/pause", method = GET)
    @ResponseBody
    public ResponseEntity<?> getProductPauseList(HttpServletRequest request) {
        logger.info("GET : /api/product/list/pause");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            returnVal.put("datas", new LookupUtils().getDefaultLookupByGroupType(Utils.validateSubStateFromHeader(request), "DEF_PROD_PAUSE"));
        } catch (JSONException e) {
            e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @RequestMapping(value = "/api/product/list/terminate", method = GET)
    @ResponseBody
    public ResponseEntity<?> getProductTerminateList(HttpServletRequest request) {
        logger.info("GET : /api/product/list/terminate");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            returnVal.put("datas", new LookupUtils().getDefaultLookupByGroupType(Utils.validateSubStateFromHeader(request), "DEF_PROD_TERMINATE"));
        } catch (JSONException e) {
            e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @RequestMapping(value = "/api/report/list/product", method = GET)
    @ResponseBody
    public ResponseEntity<?> getListProductReport(HttpServletRequest request) {
        logger.info("GET : /api/report/list/product");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            JSONArray retProduct = new JSONArray();
            List<ShelfProduct> listProductOnShelf = new ShelfProductDao().getListShelfProductOnShelf(Utils.validateSubStateFromHeader(request));
            listProductOnShelf.forEach((prod) -> {
                JSONObject prodObj = new JSONObject();
                prodObj.put("uuid", prod.getUuid())
                        .put("productCode", prod.getProdCode())
                        .put("productName", prod.getProdName());
                retProduct.put(prodObj);
            });
            returnVal.put("datas", retProduct);
        } catch (JSONException e) {
            e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @RequestMapping(value = "/api/report/list/status", method = GET)
    @ResponseBody
    public ResponseEntity<?> getListStatusReport(HttpServletRequest request) {
        logger.info("GET : /api/report/list/status");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            JSONArray retStatus = new JSONArray();
            List<String> lkValues = new ArrayList<>();
            lkValues.add("prospect");
            lkValues.add("PRO1014");
            lkValues.add("PRO1015");
            lkValues.add("PRO1016");
            lkValues.add("PRO1017");
            lkValues.add("PRO1018");
            List<SysLookup> listStatus = new SysLookupDao().getListSysLookups(Utils.validateSubStateFromHeader(request), lkValues);
//            List<Memlookup> listStatus = StatusUtils.getPass(Utils.validateSubStateFromHeader(request));
            listStatus.forEach((status) -> {
                JSONObject prodObj = new JSONObject();
                prodObj.put("uuid", status.getUuid())
                        .put("lookupCode", status.getLookupCode())
                        .put("lookupNameTh", status.getLookupNameTh())
                        .put("lookupNameEn", status.getLookupNameEn())
                        .put("lookupValue", status.getLookupValue());
                retStatus.put(prodObj);
            });
            returnVal.put("datas", retStatus);
        } catch (JSONException e) {
            e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @RequestMapping(value = "/api/report/list/state", method = GET)
    @ResponseBody
    public ResponseEntity<?> getListStateReport(HttpServletRequest request) {
        logger.info("GET : /api/report/list/state");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            JSONArray retState = new JSONArray();
            List<ShelfLookup> listState = new ShelfLookupDao().getActiveShelfLookupByGroupAndType(Utils.validateSubStateFromHeader(request), "PROCESS_STATE", "PROCESS_STATE");
            listState.forEach((state) -> {
                JSONObject prodObj = new JSONObject();
                prodObj.put("uuid", state.getUuid())
                        .put("lookupCode", state.getLookupCode())
                        .put("lookupNameTh", state.getLookupNameTh())
                        .put("lookupNameEn", state.getLookupNameEn())
                        .put("lookupValue", state.getLookupValue());
                retState.put(prodObj);
            });
            returnVal.put("datas", retState);
        } catch (JSONException e) {
            e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @RequestMapping(value = "/api/report/list/category", method = GET)
    @ResponseBody
    public ResponseEntity<?> getListCategoryReport(HttpServletRequest request) {
        logger.info("GET : /api/report/list/category");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            returnVal.put("datas", new SysOperLogDao().getListTaskCategory(Utils.validateSubStateFromHeader(request)));
        } catch (JSONException e) {
            e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @RequestMapping(value = "api/error/{code}", method = GET)
    @ResponseBody
    public ResponseEntity<?> getErrorMsg(HttpSession session, HttpServletResponse response, HttpServletRequest request, @PathVariable String code) {
        logger.info(String.format("GET : api/error/%s", code));
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            JSONArray arr = new JSONArray();
            ShelfLookupDao dao = new ShelfLookupDao();
            List<ShelfLookup> list = dao.getShelfLookupByLkCode(Utils.validateSubStateFromHeader(request), code, "CODE_ERROR", "CODE_ERROR");
            for (ShelfLookup lk : list) {
                JSONObject data = new JSONObject();
                data.put("value", ValidUtils.null2NoData(lk.getLookupCode()));
                data.put("label", ValidUtils.null2NoData(lk.getLookupNameTh()));
                arr.put(data);
            }
            returnVal.put("datas", arr);
        } catch (JSONException | NullPointerException e) {
            logger.error("" + e);
            e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);

        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @RequestMapping(value = "api/process/{code}", method = GET)
    @ResponseBody
    public ResponseEntity<?> getProcessErrorMsg(HttpSession session, HttpServletResponse response, HttpServletRequest request, @PathVariable String code) {
        logger.info(String.format("GET : api/process/%s", code));
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            JSONArray arr = new JSONArray();
            ShelfLookupDao dao = new ShelfLookupDao();
            List<ShelfLookup> list = dao.getShelfLookupByLkCode(Utils.validateSubStateFromHeader(request), code, "PROCESS_ERROR", "PROCESS_ERROR");
            for (ShelfLookup lk : list) {
                JSONObject data = new JSONObject();
                data.put("value", ValidUtils.null2NoData(lk.getLookupCode()));
                data.put("label", ValidUtils.null2NoData(lk.getLookupNameTh()));
                arr.put(data);
            }
            returnVal.put("datas", arr);
        } catch (JSONException | NullPointerException e) {
            logger.error("" + e);
            e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);

        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @RequestMapping(value = "/api/lookup/list", method = POST)
    @ResponseBody
    public ResponseEntity<?> getListLookupByGroup(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String payload) {
        logger.info("POST : /api/lookup/list");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            JSONObject datas = new JSONObject(payload);
            String lookupType = "", groupType = "";
            if (datas.has("data")) {
                JSONObject data = datas.getJSONObject("data");
                lookupType = data.has("lookupType") ? data.getString("lookupType") : "";
                groupType = data.has("groupType") ? data.getString("groupType") : "";
            }
            JSONArray arr = new JSONArray();
            ShelfLookupDao dao = new ShelfLookupDao();
            List<ShelfLookup> list = dao.getShelfLookupByLkCode(Utils.validateSubStateFromHeader(request), null, groupType, lookupType);
            for (ShelfLookup lk : list) {
                JSONObject data = new JSONObject();
                data.put("value", ValidUtils.null2NoData(lk.getLookupCode()));
                data.put("label", ValidUtils.null2NoData(lk.getLookupNameTh()));
                data.put("groupType", ValidUtils.null2NoData(lk.getGroupType()));
                data.put("lookupType", ValidUtils.null2NoData(lk.getLookupType()));
                data.put("attr1", ValidUtils.null2NoData(lk.getAttr1()));
                data.put("attr2", ValidUtils.null2NoData(lk.getAttr2()));
                data.put("attr3", ValidUtils.null2NoData(lk.getAttr3()));
                arr.put(data);
            }
            returnVal.put("datas", arr);
        } catch (JSONException e) {
            e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @RequestMapping(value = "/api/report/list/trn_status", method = GET)
    @ResponseBody
    public ResponseEntity<?> getListTrnStatusReport(HttpServletRequest request) {
        logger.info("GET : /api/report/list/trn_status");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            JSONArray retStatus = new JSONArray();
            List<String> lkValues = new ArrayList<>();
            lkValues.add("pass");
            lkValues.add("fail");
            List<SysLookup> listStatus = new SysLookupDao().getListSysLookups(Utils.validateSubStateFromHeader(request), lkValues);

//            List<Memlookup> listStatus = new SysLookupDao().getListLookupFromTrnStatus(Utils.validateSubStateFromHeader(request));
            listStatus.forEach((status) -> {
                JSONObject prodObj = new JSONObject();
                prodObj.put("uuid", status.getUuid())
                        .put("lookupCode", status.getLookupCode())
                        .put("lookupNameTh", status.getLookupNameTh())
                        .put("lookupNameEn", status.getLookupNameEn())
                        .put("lookupValue", status.getLookupValue());
                retStatus.put(prodObj);
            });
            returnVal.put("datas", retStatus);
        } catch (JSONException e) {
            e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @RequestMapping(value = "/api/report/list/process_error", method = GET)
    @ResponseBody
    public ResponseEntity<?> getListProcessErrorReport(HttpServletRequest request) {
        logger.info("GET : /api/report/list/process_error");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("datas", new JSONArray());
        try {
            JSONArray retStatus = new JSONArray();
            List<ShelfLookup> listStatus = new SysLookupDao().getListLookupFromProcError(Utils.validateSubStateFromHeader(request));
            listStatus.forEach((status) -> {
                JSONObject prodObj = new JSONObject();
                prodObj.put("uuid", status.getUuid())
                        .put("lookupCode", status.getLookupCode())
                        .put("lookupNameTh", status.getLookupNameTh())
                        .put("lookupNameEn", status.getLookupNameEn())
                        .put("lookupValue", status.getLookupValue());
                retStatus.put(prodObj);
            });
            returnVal.put("datas", retStatus);
        } catch (JSONException e) {
            e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @RequestMapping(value = "/api/audit_log/save", method = POST)
    @ResponseBody
    public ResponseEntity<?> saveAuditLogLogin(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestBody String payload) {
        logger.info("POST : /api/audit_log/save");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", new JSONArray());
        try {
            JSONObject data = new JSONObject(payload);
            SysAuditLog sysAuditLog = new SysAuditLog();
            if (data.length() > 0) {
                sysAuditLog.setUuid(Utils.getUUID());
                sysAuditLog.setLogName(data.has("logName") ? data.getString("logName") : "");
                sysAuditLog.setSource(data.has("source") ? data.getString("source") : "");
                sysAuditLog.setEventId(data.has("eventId") ? data.getString("eventId") : "");
                sysAuditLog.setLevel(data.has("level") ? data.getString("level") : "");
                sysAuditLog.setTaskCategory(data.has("taskCategory") ? data.getString("taskCategory") : "");
                sysAuditLog.setKeywords(data.has("keyWords") ? data.getString("keyWords") : "");
                sysAuditLog.setComputer(data.has("computer") ? data.getString("computer") : "");
                sysAuditLog.setAccountName(data.has("accountName") ? data.getString("accountName") : "");
                sysAuditLog.setAccountDomain(data.has("accountDomain") ? data.getString("accountDomain") : "");
                sysAuditLog.setAccessType(data.has("accessType") ? data.getString("accessType") : "");
                sysAuditLog.setObjectName(data.has("objectName") ? data.getString("objectName") : "");
                sysAuditLog.setResourceAttribute(data.has("resourceAttribute") ? data.getString("resourceAttribute") : "");
                Date currentDate = new Date();
                sysAuditLog.setAttr1(Utils.convertFormatDate2Str(currentDate));
//                sysAuditLog.setLogName(data.has("lookupType") ? data.getString("lookupType") : "");
                sysAuditLog.setStatus(data.has("status") ? data.getInt("status") : 400);
                sysAuditLog.setCreateAt(currentDate);
                sysAuditLog.setCreateBy(data.has("accountName") ? data.getString("accountName") : "");
            } else {
                returnVal.put("status", 400)
                        .put("description", "No data available.");
                return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.BAD_REQUEST));
            }
            JSONObject retJson = new JSONObject();
            SysAuditLogDao dao = new SysAuditLogDao();
            SysAuditLog sysAuditLogs = dao.getSysAuditLogByLogName(Utils.validateSubStateFromHeader(request), sysAuditLog.getLogName());
            sysAuditLog = dao.saveSysAuditLog(Utils.validateSubStateFromHeader(request), sysAuditLog);
            retJson.put("uuid", sysAuditLog.getUuid());
            retJson.put("lastTime", sysAuditLogs == null ? "" : ValidUtils.null2NoData(sysAuditLogs.getAttr1())); //เปลี่ยนAttr2 เป็น getAttr1 05/06/2020
            returnVal.put("data", retJson);
        } catch (JSONException e) {
            e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }

    @RequestMapping(value = "/api/audit_log/save/{uuid}", method = PUT)
    @ResponseBody
    public ResponseEntity<?> updateAuditLogLogin(HttpSession session, HttpServletResponse response, HttpServletRequest request, @PathVariable String uuid) {
        logger.info("PUT : /api/audit_log/save");
        JSONObject returnVal = new JSONObject().put("status", 200).put("description", "").put("data", new JSONArray());
        try {
            SysAuditLog sysAuditLog = new SysAuditLog();
            if (uuid == null || uuid.isEmpty()) {
                returnVal.put("status", 400)
                        .put("description", "No data available.");
                return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.BAD_REQUEST));
            }
            JSONObject retJson = new JSONObject();
            SysAuditLogDao dao = new SysAuditLogDao();
            sysAuditLog = dao.getSysAuditLogByUUID(Utils.validateSubStateFromHeader(request), uuid);
            Date currentDate = new Date();
            sysAuditLog.setAttr2(Utils.convertFormatDate2Str(currentDate));
            sysAuditLog = dao.updateSysAuditLog(Utils.validateSubStateFromHeader(request), sysAuditLog);
            retJson.put("uuid", sysAuditLog.getUuid());
            returnVal.put("data", retJson);
        } catch (JSONException e) {
            e.printStackTrace();
            returnVal.put("status", 500)
                    .put("description", "" + e);
        }
        return (new ResponseEntity<>(returnVal.toString(), headersJSON, HttpStatus.OK));
    }
}
