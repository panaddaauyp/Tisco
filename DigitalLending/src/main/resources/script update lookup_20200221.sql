insert into t_shelf_lookup (uuid,attr3,attr2,attr1,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,status,create_by) values('cea060ea-4979-11ea-b77f-2e728ce88125','103515002226','1','Y','C','Black กู้ไม่ได้','Black กู้ไม่ได้','Black กู้ไม่ได้','DEF_RISK','LOOKUP_LIST',213 ,'develop');
insert into t_shelf_lookup (uuid,attr3,attr2,attr1,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,status,create_by) values('cea063b0-4979-11ea-b77f-2e728ce88125','3200200203447','2','Y','R','Yellow กู้ได้  ชื่อ หรือ นามสกุล ตรง และ id ไม่ตรง','Yellow กู้ได้  ชื่อ หรือ นามสกุล ตรง และ id ไม่ตรง','Yellow กู้ได้  ชื่อ หรือ นามสกุล ตรง และ id ไม่ตรง','DEF_RISK','LOOKUP_LIST',213 ,'develop');
insert into t_shelf_lookup (uuid,attr3,attr2,attr1,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,status,create_by) values('cea0650e-4979-11ea-b77f-2e728ce88125','105535138249','3','Y','O','Grey กู้ไม่ได้','Grey กู้ไม่ได้','Grey กู้ไม่ได้','DEF_RISK','LOOKUP_LIST',213 ,'develop');
insert into t_shelf_lookup (uuid,attr3,attr2,attr1,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,status,create_by) values('cea068ce-4979-11ea-b77f-2e728ce88125','635534000028','4','Y','L','Red กู้ไม่ได้','Red กู้ไม่ได้','Red กู้ไม่ได้','DEF_RISK','LOOKUP_LIST',213 ,'develop');
insert into t_shelf_lookup (uuid,attr3,attr2,attr1,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,status,create_by) values('cea06a18-4979-11ea-b77f-2e728ce88125','3509901125961','5','Y','I','Purple กู้ไม่ได้','Purple กู้ไม่ได้','Purple กู้ไม่ได้','DEF_RISK','LOOKUP_LIST',213 ,'develop');
insert into t_shelf_lookup (uuid,attr3,attr2,attr1,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,status,create_by) values('cea06b58-4979-11ea-b77f-2e728ce88125','105535064539','6','Y','F','Pink กู้ไม่ได้','Pink กู้ไม่ได้','Pink กู้ไม่ได้','DEF_RISK','LOOKUP_LIST',213 ,'develop');


insert into t_shelf_lookup (uuid,attr2,attr1,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,status,create_by) values('f74a6fbe-4d47-11ea-b77f-2e728ce88125','1','Y','Y','ใช่','ใช่','ใช่','DEF_KYC_SECTION','LOOKUP_LIST',213,'develop');
insert into t_shelf_lookup (uuid,attr2,attr1,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,status,create_by) values('f74a7220-4d47-11ea-b77f-2e728ce88125','2','Y','N','ไม่ใช่','ไม่ใช่','ไม่ใช่','DEF_KYC_SECTION','LOOKUP_LIST',213,'develop');

insert into t_shelf_lookup (uuid,attr2,attr1,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,status,create_by) values('07ee3596-4d4b-11ea-b77f-2e728ce88125','1','Y','header','Header','Header','Header','DEF_TOPIC','LOOKUP_LIST ',213 ,'develop');
insert into t_shelf_lookup (uuid,attr2,attr1,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,status,create_by) values('07ee3816-4d4b-11ea-b77f-2e728ce88125','2','Y','body','Body','Body','Body','DEF_TOPIC','LOOKUP_LIST ',213 ,'develop');
insert into t_shelf_lookup (uuid,attr2,attr1,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,status,create_by) values('f67b6f6a-4d4d-11ea-b77f-2e728ce88125','2','Y','content','Content','Content','Content','DEF_TOPIC','LOOKUP_LIST ',213 ,'develop');

insert into t_shelf_lookup (uuid,attr2,attr1,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,status,create_by) values('07ee3596-4d4b-11ea-b77f-2e728ce88125','1','Y','header','Header','Header','Header','DEF_TOPIC','LOOKUP_LIST ',213 ,'develop');
insert into t_shelf_lookup (uuid,attr2,attr1,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,status,create_by) values('07ee3816-4d4b-11ea-b77f-2e728ce88125','2','Y','body','Body','Body','Body','DEF_TOPIC','LOOKUP_LIST ',213 ,'develop');
insert into t_shelf_lookup (uuid,attr2,attr1,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,status,create_by) values('f67b6f6a-4d4d-11ea-b77f-2e728ce88125','3','Y','content','Content','Content','Content','DEF_TOPIC','LOOKUP_LIST ',213 ,'develop');

update t_shelf_comp_dtl set attr2 = 'secretList' where ele_id = 'imgSecretQuestionList';
update t_shelf_comp_dtl set attr2 = 'consentNameList' where ele_id = 'consentName';
update t_shelf_comp_dtl set attr2 = 'consentList' where ele_id = 'radioConsent';
update t_shelf_comp_dtl set attr2 = 'incomeList' where ele_id = 'incFreeText';
update t_shelf_comp_dtl set attr2 = 'otherKYCSection' where ele_id = 'radioList';
update t_shelf_comp_dtl set attr2 = 'riskLevelList' where ele_id = 'riskLevelList';
update t_shelf_comp_dtl set attr2 = 'imgCusList' where ele_id = 'imgSplashList';
update t_shelf_comp_dtl set attr2 = 'sourceTypeList' where ele_id = 'imageType';
update t_shelf_comp_dtl set attr2 = 'formularList' where ele_id = 'calFactorsList';
update t_shelf_comp_dtl set attr2 = 'defCampaignList' where ele_id = 'defCamp';
update t_shelf_comp_dtl set attr2 = 'cutOffTimeList' where ele_id = 'pcutOffTAllD';
update t_shelf_comp_dtl set attr2 = 'factorList' where ele_id = 'formularList';
update t_shelf_comp_dtl set attr2 = 'roundList' where ele_id = 'round';
update t_shelf_comp_dtl set attr2 = 'summaryList' where ele_id = 'summaryList';
update t_shelf_comp_dtl set attr2 = 'errList' where ele_id = 'errMsg';
update t_shelf_comp_dtl set attr2 = 'packageList' where ele_id = 'packageList';
update t_shelf_comp_dtl set attr2 = 'cutOffDayList' where ele_id = 'pcutOffWE';
update t_shelf_comp_dtl set attr2 = 'campaignList' where ele_id = 'campaignId';
update t_shelf_comp_dtl set attr2 = 'mandatoryList' where ele_id = 'radioMandatory';
update t_shelf_comp_dtl set attr2 = 'vcsSaleSheetList' where ele_id = 'verProdSaleSheetList';
update t_shelf_comp_dtl set attr2 = 'imgSplashList' where ele_id = 'imgSplashList';
update t_shelf_comp_dtl set attr2 = 'vcsSplashPageList' where ele_id = 'verSplashList';
update t_shelf_comp_dtl set attr2 = 'sourceTypeList' where ele_id = 'imageType';
update t_shelf_comp_dtl set attr2 = 'vcsTermsNConList' where ele_id = 'verTermsNConditionList';
update t_shelf_comp_dtl set attr2 = 'mandatoryList' where ele_id = 'radioMandatory';

update t_shelf_lookup set lookup_type = 'LOOKUP_LIST' where lookup_type = 'LOOKUP_LIST ';

insert into t_shelf_lookup (uuid,attr2,attr1,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,status,create_by) 
values('21ccd42e-548d-11ea-a2e3-2e728ce88125','10000','0','1','0-10,0000','0-10,0000','0-10,0000','DEF_INC','LOOKUP_LIST',213 ,'develop');

insert into t_shelf_lookup (uuid,attr2,attr1,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,status,create_by) 
values('21ccd6c2-548d-11ea-a2e3-2e728ce88125','15000','10000','2','10,0000-15,0000','10,0000-15,0000','10,0000-15,0000','DEF_INC','LOOKUP_LIST',213 ,'develop');

insert into t_shelf_lookup (uuid,attr2,attr1,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,status,create_by) 
values('21ccd82a-548d-11ea-a2e3-2e728ce88125','30000','15001','3','15,0001-30,0000','15,0001-30,0000','15,0001-30,0000','DEF_INC','LOOKUP_LIST',213 ,'develop');

insert into t_shelf_lookup (uuid,attr2,attr1,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,status,create_by) 
values('21ccd96a-548d-11ea-a2e3-2e728ce88125','50000','30001','4','30,001-50,000','30,001-50,000','30,001-50,000','DEF_INC','LOOKUP_LIST',213 ,'develop');

insert into t_shelf_lookup (uuid,attr2,attr1,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,status,create_by) 
values('21ccda96-548d-11ea-a2e3-2e728ce88125','100000','50001','5','50,001-100,000','50,001-100,000','50,001-100,000','DEF_INC','LOOKUP_LIST',213 ,'develop');

insert into t_shelf_lookup (uuid,attr2,attr1,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,status,create_by) 
values('21ccdbc2-548d-11ea-a2e3-2e728ce88125','','100000','6','100,000 ขึ้นไป','100,000 ขึ้นไป','100,000 ขึ้นไป','DEF_INC','LOOKUP_LIST',213 ,'develop');

update t_shelf_lookup set attr3 = '/api/defaultcampaign/xxxxx' where uuid = '8aa67042-47fc-11ea-b77f-2e728ce88125';
update t_shelf_lookup set attr3 = 'www.tisco.co.th' where uuid = '8aa673bc-47fc-11ea-b77f-2e728ce88125';

update t_shelf_lookup set attr3 = '10' where uuid = '5a1ec254-47fb-11ea-b77f-2e728ce88125';
update t_shelf_lookup set attr3 = '100' where uuid = '5a1ec754-47fb-11ea-b77f-2e728ce88125';
update t_shelf_lookup set attr3 = '10' where uuid = '5a1ec98e-47fb-11ea-b77f-2e728ce88125';
update t_shelf_lookup set attr3 = '100' where uuid = '5a1ecaec-47fb-11ea-b77f-2e728ce88125';
update t_shelf_comp_dtl set attr1 = null where ele_id = 'chkBoxLabel';
update t_shelf_comp_dtl set attr2='consList' where ele_id = 'chkConsentList';

update t_shelf_comp_dtl set seq = 5 where uuid = '5e07488d-4191-4863-977f-a79e04dd246f';
update t_shelf_comp_dtl set seq = 6 where uuid = '6f7d16d1-b201-41f1-8c62-0f7b093f4b6b';



