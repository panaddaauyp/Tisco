/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.util;

import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import th.co.d1.digitallending.dao.ShelfLookupDao;
import th.co.d1.digitallending.dao.ShelfProductVcsDao;
import th.co.d1.digitallending.entity.ShelfLookup;

/**
 *
 * @author Kritsana Sasai
 *
 * DataOne Asia (Thailand) Company Limited 1023 MS Siam Tower, 30th Floor, Rama
 * 3 Rd., Chongnonsi, Yannawa, Bangkok, 10120
 *
 * @create 06-02-2020 9:10:18 AM
 */
public class LookupUtils {

    public JSONArray getLookupList(String dbEnv, String kName, String uuid) {
        switch (kName) {
            case "secretList":
                return getDefaultQuestionList(dbEnv);
//            case "consentNameList":
//                return getConsentNameList(dbEnv);        //API
            case "consentList":
                return getDefaultconsentList(dbEnv);
            case "incomeList":
                return getDefaultIncomeList(dbEnv);
            case "otherKYCSection":
                return getDefaultKYCSectionList(dbEnv);
            case "riskLevelList":
                return getRiskLevelList(dbEnv);
            case "imgCusList":
                return getImageCustomerList();
            case "sourceTypeList":
                return getDefaultSourceList(dbEnv);
            case "formularList":
                return getDefaultFormularList(dbEnv);
            case "defCampaignList":
                return getDefaultCampaignList(dbEnv);        //API
            case "cutOffTimeList":
                return getDefaultTimeList(dbEnv);
            case "factorList":
                return getDefaultFactorList(dbEnv);
            case "roundList":
                return getDefaultRoundList(dbEnv);
            case "summaryList":
                return getSummaryList(dbEnv);
            case "errList":
                return getDefaultErrorList(dbEnv);
            case "packageList":
                return getPackageList(dbEnv);
            case "cutOffDayList":
                return getDefaultDayList(dbEnv);
            case "campaignList":
                return getCampaignList();
            case "mandatoryList":
                return getDefaultMandatoryList(dbEnv);
            case "vcsSaleSheetList":
                return getVCSSaleSheetList(dbEnv, uuid);
            case "imgSplashList":
                return getImageSplashList();
            case "vcsSplashPageList":
                return getVCSSplashList(dbEnv, uuid);
//            case "sourceTypeList":
//                return getDefaultSourceList();
            case "vcsTermsNConList":
                return getVCSTermsNConList(dbEnv, uuid);
//            case "mandatoryList":
//                return getDefaultMandatoryList();
            case "questionList":
                return getDefaultQuestionList(dbEnv);
            case "contentList":
                return getDefaultContentList(dbEnv);
            case "consList":
                return getDefaultMandatoryList(dbEnv);
            case "markList":
                return getDefaultMarkList(dbEnv);
            default:
                return new JSONArray();
        }
    }

    private JSONArray getDefaultconsentList(String dbEnv) {
        JSONArray arr = new JSONArray();
        ShelfLookupDao dao = new ShelfLookupDao();
        List<ShelfLookup> list = dao.getShelfLookupByLkCode(dbEnv, null, "DEF_CONSENT", "LOOKUP_LIST");
        for (ShelfLookup lk : list) {
            JSONObject data = new JSONObject();
            data.put("value", ValidUtils.null2NoData(lk.getLookupCode()));
            data.put("label", ValidUtils.null2NoData(lk.getLookupNameTh()));
            arr.put(data);
        }
        return arr;
    }

    private JSONArray getDefaultIncomeList(String dbEnv) {
        JSONArray arr = new JSONArray();
        ShelfLookupDao dao = new ShelfLookupDao();
        List<ShelfLookup> list = dao.getShelfLookupByLkCode(dbEnv, null, "DEF_INCOME", "LOOKUP_LIST");
        for (ShelfLookup lk : list) {
            JSONObject data = new JSONObject();
            data.put("value", ValidUtils.null2NoData(lk.getLookupCode()));
            data.put("label", ValidUtils.null2NoData(lk.getLookupNameTh()));
            if ("t".equalsIgnoreCase(ValidUtils.null2NoData(lk.getLookupCode()))) {
                data.put("attr", getDefaultIncList(dbEnv));
            }
            arr.put(data);
        }
        return arr;
    }

    private JSONArray getDefaultIncList(String dbEnv) {
        JSONArray arr = new JSONArray();
        ShelfLookupDao dao = new ShelfLookupDao();
        List<ShelfLookup> list = dao.getShelfLookupByLkCode(dbEnv, null, "DEF_INC", "LOOKUP_LIST");
        for (ShelfLookup lk : list) {
            JSONObject data = new JSONObject();
            data.put("value", ValidUtils.null2NoData(lk.getLookupCode()));
            data.put("label", ValidUtils.null2NoData(lk.getLookupNameTh()));
            data.put("min", ValidUtils.null2NoData(lk.getAttr1()));
            data.put("max", ValidUtils.null2NoData(lk.getAttr2()));
            arr.put(data);
        }
        return arr;
    }

    private JSONArray getDefaultKYCSectionList(String dbEnv) {
        JSONArray arr = new JSONArray();
        ShelfLookupDao dao = new ShelfLookupDao();
        List<ShelfLookup> list = dao.getShelfLookupByLkCode(dbEnv, null, "DEF_KYC_SECTION", "LOOKUP_LIST");
        for (ShelfLookup lk : list) {
            JSONObject data = new JSONObject();
            data.put("value", ValidUtils.null2NoData(lk.getLookupCode()));
            data.put("label", ValidUtils.null2NoData(lk.getLookupNameTh()));
            arr.put(data);
        }
        return arr;
    }

    private JSONArray getRiskLevelList(String dbEnv) {
        JSONArray arr = new JSONArray();
        ShelfLookupDao dao = new ShelfLookupDao();
        List<ShelfLookup> list = dao.getShelfLookupByLkCode(dbEnv, null, "DEF_RISK", "LOOKUP_LIST");
        for (ShelfLookup lk : list) {
            JSONObject data = new JSONObject();
            data.put("id", ValidUtils.null2NoData(lk.getAttr3()));
            data.put("level", ValidUtils.null2NoData(lk.getLookupCode()));
            data.put("riskName", ValidUtils.null2NoData(lk.getLookupNameTh()));
            arr.put(data);
        }
        return arr;
    }

    private JSONArray getImageCustomerList() {
        /*
        [{fileName": "xxxxx.png","type": "Website","delete": "Delete","value": "base64"},{"fileName": "xxxxx.png","type": "Mobile","delete": "Delete","value": "base64"}]

         */
        return new JSONArray();
    }

    private JSONArray getDefaultSourceList(String dbEnv) {
        JSONArray arr = new JSONArray();
        ShelfLookupDao dao = new ShelfLookupDao();
        List<ShelfLookup> list = dao.getShelfLookupByLkCode(dbEnv, null, "DEF_SOURCE", "LOOKUP_LIST");
        JSONObject tmp = new JSONObject();
        tmp.put("value", "");
        tmp.put("label", "Please Select");
        arr.put(tmp);
        for (ShelfLookup lk : list) {
            JSONObject data = new JSONObject();
            data.put("value", ValidUtils.null2NoData(lk.getLookupCode()));
            data.put("label", ValidUtils.null2NoData(lk.getLookupNameTh()));
            arr.put(data);
        }
        return arr;
    }

    private JSONArray getDefaultFormularList(String dbEnv) {
        JSONArray arr = new JSONArray();
        ShelfLookupDao dao = new ShelfLookupDao();
        List<ShelfLookup> list = dao.getShelfLookupByLkCode(dbEnv, null, "DEF_FORMULAR", "LOOKUP_LIST");
        for (ShelfLookup lk : list) {
            JSONObject data = new JSONObject();
            data.put("code", ValidUtils.null2NoData(lk.getLookupCode()));
            data.put("name", ValidUtils.null2NoData(lk.getLookupNameTh()));
            data.put("desc", ValidUtils.null2NoData(lk.getDescription()));
            data.put("formular", ValidUtils.null2NoData(lk.getAttr3()));
            data.put("formularDesc", ValidUtils.null2NoData(lk.getDescription()));
            data.put("round", "");
            data.put("unit", "");
            data.put("decimal", "");
            arr.put(data);
        }
        return arr;
    }

    private JSONArray getDefaultCampaignList(String dbEnv) {
        JSONArray arr = new JSONArray();
        ShelfLookupDao dao = new ShelfLookupDao();
        List<ShelfLookup> list = dao.getShelfLookupByLkCode(dbEnv, null, "DEF_CAMPAIGN", "LOOKUP_LIST");
        for (ShelfLookup lk : list) {
            JSONObject data = new JSONObject();
            data.put("value", ValidUtils.null2NoData(lk.getLookupCode()));
            data.put("label", ValidUtils.null2NoData(lk.getLookupNameTh()));
            data.put("attr1", ValidUtils.null2NoData(lk.getAttr3()));
            arr.put(data);
        }
        return arr;
    }

    private JSONArray getDefaultTimeList(String dbEnv) {
        JSONArray arr = new JSONArray();
        ShelfLookupDao dao = new ShelfLookupDao();
        List<ShelfLookup> list = dao.getShelfLookupByLkCode(dbEnv, null, "DEF_TIME", "LOOKUP_LIST");
        for (ShelfLookup lk : list) {
            JSONObject data = new JSONObject();
            data.put("value", ValidUtils.null2NoData(lk.getLookupCode()));
            data.put("label", ValidUtils.null2NoData(lk.getLookupNameTh()));
            arr.put(data);
        }
        return arr;
    }

    private JSONArray getDefaultFactorList(String dbEnv) {
        JSONArray arr = new JSONArray();
        ShelfLookupDao dao = new ShelfLookupDao();
        List<ShelfLookup> list = dao.getShelfLookupByLkCode(dbEnv, null, "DEF_FACTOR", "LOOKUP_LIST");
        for (ShelfLookup lk : list) {
            JSONObject data = new JSONObject();
            data.put("formular", ValidUtils.null2NoData(lk.getLookupCode()));
            data.put("description", ValidUtils.null2NoData(lk.getLookupNameTh()));
            arr.put(data);
        }
        return arr;
    }

    private JSONArray getDefaultRoundList(String dbEnv) {
        JSONArray arr = new JSONArray();
        ShelfLookupDao dao = new ShelfLookupDao();
        List<ShelfLookup> list = dao.getShelfLookupByLkCode(dbEnv, null, "DEF_ROUND", "LOOKUP_LIST");
        JSONObject tmp = new JSONObject();
        tmp.put("value", "");
        tmp.put("label", "Please Select");
        tmp.put("attr", "");
        arr.put(tmp);
        for (ShelfLookup lk : list) {
            JSONObject data = new JSONObject();
            data.put("value", ValidUtils.null2NoData(lk.getLookupCode()));
            data.put("label", ValidUtils.null2NoData(lk.getLookupNameTh()));
            data.put("attr", lk.getAttr3());
            arr.put(data);
        }
        return arr;
    }

    private JSONArray getSummaryList(String dbEnv) {
        JSONArray arr = new JSONArray();
        ShelfLookupDao dao = new ShelfLookupDao();
        List<ShelfLookup> list = dao.getShelfLookupByLkCode(dbEnv, null, "DEF_TOPIC", "LOOKUP_LIST");
        for (ShelfLookup lk : list) {
            if (!"content".equalsIgnoreCase(lk.getLookupCode())) {
                JSONObject data = new JSONObject();
                data.put("value", ValidUtils.null2NoData(lk.getLookupCode()));
                data.put("label", ValidUtils.null2NoData(lk.getLookupNameTh()));
                arr.put(data);
            }
        }
        return arr;
    }

    private JSONArray getDefaultErrorList(String dbEnv) {
        JSONArray arr = new JSONArray();
        ShelfLookupDao dao = new ShelfLookupDao();
        List<ShelfLookup> list = dao.getShelfLookupByLkCode(dbEnv, null, "DEF_ERROR", "LOOKUP_LIST");
        for (ShelfLookup lk : list) {
            JSONObject data = new JSONObject();
            data.put("value", ValidUtils.null2NoData(lk.getLookupCode()));
            data.put("label", ValidUtils.null2NoData(lk.getLookupNameTh()));
            arr.put(data);
        }
        return arr;
    }

    private JSONArray getPackageList(String dbEnv) {
        JSONArray arr = new JSONArray();
        ShelfLookupDao dao = new ShelfLookupDao();
        List<ShelfLookup> list = dao.getShelfLookupByLkCode(dbEnv, null, "DEF_TOPIC", "LOOKUP_LIST");
        for (ShelfLookup lk : list) {
            JSONObject data = new JSONObject();
            data.put("value", ValidUtils.null2NoData(lk.getLookupCode()));
            data.put("label", ValidUtils.null2NoData(lk.getLookupNameTh()));
            arr.put(data);
        }
        return arr;
    }

    private JSONArray getDefaultDayList(String dbEnv) {
        JSONArray arr = new JSONArray();
        ShelfLookupDao dao = new ShelfLookupDao();
        List<ShelfLookup> list = dao.getShelfLookupByLkCode(dbEnv, null, "DEF_DAY", "LOOKUP_LIST");
        for (ShelfLookup lk : list) {
            JSONObject data = new JSONObject();
            data.put("value", ValidUtils.null2NoData(lk.getLookupCode()));
            data.put("label", ValidUtils.null2NoData(lk.getLookupNameTh()));
            arr.put(data);
        }
        return arr;
    }

    private JSONArray getCampaignList() {
//        Call API
        return new JSONArray();
    }

    public JSONArray getDefaultMandatoryList(String dbEnv) {
        JSONArray arr = new JSONArray();
        ShelfLookupDao dao = new ShelfLookupDao();
        List<ShelfLookup> list = dao.getShelfLookupByLkCode(dbEnv, null, "DEF_MANDATORY", "LOOKUP_LIST");
        for (ShelfLookup lk : list) {
            JSONObject data = new JSONObject();
            data.put("value", ValidUtils.null2NoData(lk.getLookupCode()));
            data.put("label", ValidUtils.null2NoData(lk.getLookupNameTh()));
            arr.put(data);
        }
        return arr;
    }

    public JSONArray getDefaultMarkList(String dbEnv) {
        JSONArray arr = new JSONArray();
        ShelfLookupDao dao = new ShelfLookupDao();
        List<ShelfLookup> list = dao.getShelfLookupByLkCode(dbEnv, null, "FORMULAR_MARK", "LOOKUP_LIST");
        for (ShelfLookup lk : list) {
            JSONObject data = new JSONObject();
            data.put("value", ValidUtils.null2NoData(lk.getLookupCode()));
            data.put("label", ValidUtils.null2NoData(lk.getLookupNameTh()));
            arr.put(data);
        }
        return arr;
    }

    private JSONArray getVCSSaleSheetList(String dbEnv, String prodUuid) {
        ShelfProductVcsDao dao = new ShelfProductVcsDao();
        JSONArray arr = dao.getVCSComponent(dbEnv, prodUuid, "003");   //PRODUCT SALE SHEET
        return arr;
    }

    private JSONArray getImageSplashList() {
        /*
        [{fileName": "xxxxx.png","type": "Website","delete": "Delete","value": "base64"},{"fileName": "xxxxx.png","type": "Mobile","delete": "Delete","value": "base64"}]

         */
        return new JSONArray();
    }

    private JSONArray getVCSSplashList(String dbEnv, String prodUuid) {
        ShelfProductVcsDao dao = new ShelfProductVcsDao();
        JSONArray arr = dao.getVCSComponent(dbEnv, prodUuid, "001");   //SPLASH PAGE
        return arr;
    }

    private JSONArray getVCSTermsNConList(String dbEnv, String prodUuid) {
        ShelfProductVcsDao dao = new ShelfProductVcsDao();
        JSONArray arr = dao.getVCSComponent(dbEnv, prodUuid, "004");   //TERM & CONDITION
        return arr;
    }

    private JSONArray getDefaultQuestionList(String dbEnv) {
        JSONArray arr = new JSONArray();
        ShelfLookupDao dao = new ShelfLookupDao();
        List<ShelfLookup> list = dao.getShelfLookupByLkCode(dbEnv, null, "DEF_QUESTION", "LOOKUP_LIST");
        for (ShelfLookup lk : list) {
            JSONObject data = new JSONObject();
            data.put("value", ValidUtils.null2NoData(lk.getLookupCode()));
            data.put("label", ValidUtils.null2NoData(lk.getLookupNameTh()));
            arr.put(data);
        }
        return arr;
    }

    private JSONArray getDefaultContentList(String dbEnv) {
        JSONArray arr = new JSONArray();
        ShelfLookupDao dao = new ShelfLookupDao();
        List<ShelfLookup> list = dao.getShelfLookupByLkCode(dbEnv, null, "DEF_PAY_TYPE", "LOOKUP_LIST");
        for (ShelfLookup lk : list) {
            JSONObject data = new JSONObject();
            data.put("value", ValidUtils.null2NoData(lk.getLookupCode()));
            data.put("label", ValidUtils.null2NoData(lk.getLookupNameTh()));
            arr.put(data);
        }
        return arr;
    }

    public JSONArray getDefaultProductType(String dbEnv) {
        JSONArray arr = new JSONArray();
        ShelfLookupDao dao = new ShelfLookupDao();
        List<ShelfLookup> list = dao.getShelfLookupByLkCode(dbEnv, null, "DEF_PAY_TYPE", "LOOKUP_LIST");
        for (ShelfLookup lk : list) {
            JSONObject data = new JSONObject();
            data.put("value", ValidUtils.null2NoData(lk.getLookupCode()));
            data.put("label", ValidUtils.null2NoData(lk.getLookupNameTh()));
            arr.put(data);
        }
        return arr;
    }

    public JSONArray getDefaultProductGroup(String dbEnv) {
        JSONArray arr = new JSONArray();
        ShelfLookupDao dao = new ShelfLookupDao();
        List<ShelfLookup> list = dao.getShelfLookupByLkCode(dbEnv, null, "DEF_PROD_GROUP", "LOOKUP_LIST");
        for (ShelfLookup lk : list) {
            JSONObject data = new JSONObject();
            data.put("value", ValidUtils.null2NoData(lk.getLookupCode()));
            data.put("label", ValidUtils.null2NoData(lk.getLookupNameTh()));
            arr.put(data);
        }
        return arr;
    }

    public JSONArray getDefaultLookupByGroupType(String dbEnv, String groupType) {
        JSONArray arr = new JSONArray();
        ShelfLookupDao dao = new ShelfLookupDao();
        List<ShelfLookup> list = dao.getShelfLookupByLkCode(dbEnv, null, groupType, "LOOKUP_LIST");
        for (ShelfLookup lk : list) {
            JSONObject data = new JSONObject();
            data.put("value", ValidUtils.null2NoData(lk.getLookupCode()));
            data.put("label", ValidUtils.null2NoData(lk.getLookupNameTh()));
            arr.put(data);
        }
        return arr;
    }
}
