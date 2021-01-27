INSERT INTO public.t_shelf_lookup(
	uuid, lookup_code, lookup_name_th, lookup_name_en, description, group_type, lookup_type,  attr1, attr2, status, create_by)
	VALUES ('14b3fea8-57cf-11ea-82b4-0242ac130003', '1', 'Free Text', 'คำตอบแบบเป็นตัวเลขเท่านั้น', 'คำตอบแบบเป็นตัวเลขเท่านั้น', 'DEF_QUESTION', 'LOOKUP_LIST', 'Y', '1', 213, 'develop');;
INSERT INTO public.t_shelf_lookup(
	uuid, lookup_code, lookup_name_th, lookup_name_en, description, group_type, lookup_type,  attr1, attr2, status, create_by)
	VALUES ('14b40114-57cf-11ea-82b4-0242ac130003', '7', 'Free Text', 'text หรือ ตัวเลข', 'text หรือ ตัวเลข', 'DEF_QUESTION', 'LOOKUP_LIST', 'Y', '2', 213, 'develop');;
INSERT INTO public.t_shelf_lookup(
	uuid, lookup_code, lookup_name_th, lookup_name_en, description, group_type, lookup_type,  attr1, attr2, status, create_by)
	VALUES ('3e779426-57d8-11ea-82b4-0242ac130003', '3', 'Dropdown', 'Dropdown', 'Dropdown', 'DEF_QUESTION', 'LOOKUP_LIST', 'Y', '2', 213, 'develop');;
INSERT INTO public.t_shelf_lookup(
	uuid, lookup_code, lookup_name_th, lookup_name_en, description, group_type, lookup_type,  attr1, attr2, status, create_by)
	VALUES ('14b4022c-57cf-11ea-82b4-0242ac130003', 'pay', 'Payment', 'Payment', 'Payment', 'DEF_PAY_TYPE', 'LOOKUP_LIST', 'Y', '1', 213, 'develop');;
INSERT INTO public.t_shelf_lookup(
	uuid, lookup_code, lookup_name_th, lookup_name_en, description, group_type, lookup_type,  attr1, attr2, status, create_by)
	VALUES ('14b40326-57cf-11ea-82b4-0242ac130003', 'receipt', 'Receipt', 'Receipt', 'Receipt', 'DEF_PAY_TYPE', 'LOOKUP_LIST', 'Y', '2', 213, 'develop');;
INSERT INTO public.t_shelf_lookup(
	uuid, lookup_code, lookup_name_th, lookup_name_en, description, group_type, lookup_type,  attr1, attr2, status, create_by)
	VALUES ('14b403f8-57cf-11ea-82b4-0242ac130003', 'instagram', 'instagram', 'instagram', 'instagram', 'DEF_CHANNEL', 'LOOKUP_LIST', 'Y', '1', 213, 'develop');;
INSERT INTO public.t_shelf_lookup(
	uuid, lookup_code, lookup_name_th, lookup_name_en, description, group_type, lookup_type,  attr1, attr2, status, create_by)
	VALUES ('14b404d4-57cf-11ea-82b4-0242ac130003', 'facebook', 'Facebook', 'Facebook', 'Facebook', 'DEF_CHANNEL', 'LOOKUP_LIST', 'Y', '2', 213, 'develop');;
INSERT INTO public.t_shelf_lookup(
	uuid, lookup_code, lookup_name_th, lookup_name_en, description, group_type, lookup_type,  attr1, attr2, status, create_by)
	VALUES ('14b4059c-57cf-11ea-82b4-0242ac130003', 'line', 'Line', 'Line', 'Line', 'DEF_CHANNEL', 'LOOKUP_LIST', 'Y', '3', 213, 'develop');;
insert into t_shelf_comp_dtl (uuid,comp_uuid,lk_uuid,label_text,seq,parent,status,create_by,ele_id,require)
	values ('b36431a0-57e1-11ea-82b4-0242ac130003','723b3819-e1e0-4756-a6f2-50fdaf14d85d','c8729a63-34ba-4dab-9fd8-1084ab018773','',43,42,213,'user','remarkMsg',false);;


update t_shelf_comp_dtl set seq=44, parent=1 where uuid = 'fd595d64-3db8-11ea-b77f-2e728ce88125';;
update t_shelf_comp_dtl set seq=45, parent=44 where uuid = 'fd596020-3db8-11ea-b77f-2e728ce88125';;
update t_shelf_comp_dtl set seq=46 where uuid = 'fd59616a-3db8-11ea-b77f-2e728ce88125';;
update t_shelf_comp_dtl set seq=47, parent=46 where uuid = 'fd596296-3db8-11ea-b77f-2e728ce88125';;
update t_shelf_comp_dtl set seq=48, parent=47 where uuid = 'fd5963b8-3db8-11ea-b77f-2e728ce88125';;
update t_shelf_comp_dtl set seq=49, parent=47 where uuid = 'fd5964da-3db8-11ea-b77f-2e728ce88125';;
update t_shelf_comp_dtl set seq=50, parent=46 where uuid = 'fd5965fc-3db8-11ea-b77f-2e728ce88125';;
update t_shelf_comp_dtl set seq=51, parent=50 where uuid = 'fd596980-3db8-11ea-b77f-2e728ce88125';;
update t_shelf_comp_dtl set seq=52, parent=50 where uuid = 'fd596ade-3db8-11ea-b77f-2e728ce88125';;
update t_shelf_comp_dtl set seq=53, parent=50 where uuid = 'fd596c0a-3db8-11ea-b77f-2e728ce88125';;
update t_shelf_comp_dtl set seq=54, parent=46 where uuid = 'fd596d2c-3db8-11ea-b77f-2e728ce88125';;
update t_shelf_comp_dtl set seq=55, parent=54 where uuid = 'fd596e4e-3db8-11ea-b77f-2e728ce88125';;
update t_shelf_comp_dtl set seq=56, parent=54 where uuid = 'fd596f70-3db8-11ea-b77f-2e728ce88125';;
update t_shelf_comp_dtl set seq=57, parent=46 where uuid = 'fd59709c-3db8-11ea-b77f-2e728ce88125';;
update t_shelf_comp_dtl set seq=58, parent=57 where uuid = 'fd59738a-3db8-11ea-b77f-2e728ce88125';;
update t_shelf_comp_dtl set seq=59, parent=57 where uuid = 'fd59757e-3db8-11ea-b77f-2e728ce88125';;
update t_shelf_comp_dtl set seq=60, parent=57 where uuid = 'fd5976f0-3db8-11ea-b77f-2e728ce88125';;
update t_shelf_comp_dtl set lk_uuid='6f61a01f-6d49-405c-bbea-b479e20b3bad' where uuid = 'fd595c2e-3db8-11ea-b77f-2e728ce88125';;
update t_shelf_comp_dtl set label_text = 'List of Secret Questions' where uuid = '72e2c894-f5f5-4c37-95e0-43e0672628c5';;


insert into t_shelf_lookup (uuid,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,attr1,status,create_by)
values ('73f165c6-586b-11ea-82b4-0242ac130003','IN019','radio Mandatory','radio Mandatory','radio Mandatory','INPUT','ELEMENT','Y',213,'develop');;


update t_shelf_comp_dtl set lk_uuid = '73f165c6-586b-11ea-82b4-0242ac130003' where uuid = 'cf686aac-3db4-11ea-b77f-2e728ce88125';
update t_shelf_comp_dtl set lk_uuid = '73f165c6-586b-11ea-82b4-0242ac130003' where uuid = 'cf686e08-3db4-11ea-b77f-2e728ce88125';
update t_shelf_comp_dtl set lk_uuid = '73f165c6-586b-11ea-82b4-0242ac130003' where uuid = 'cf686f52-3db4-11ea-b77f-2e728ce88125';
update t_shelf_comp_dtl set lk_uuid = '73f165c6-586b-11ea-82b4-0242ac130003' where uuid = 'cf687088-3db4-11ea-b77f-2e728ce88125';
update t_shelf_comp_dtl set lk_uuid = '73f165c6-586b-11ea-82b4-0242ac130003' where uuid = 'cf6871be-3db4-11ea-b77f-2e728ce88125';

update t_shelf_comp_dtl set lk_uuid = '73f165c6-586b-11ea-82b4-0242ac130003' where uuid = 'cf68740c-3db4-11ea-b77f-2e728ce88125';
update t_shelf_comp_dtl set lk_uuid = '73f165c6-586b-11ea-82b4-0242ac130003' where uuid = 'cf6875b0-3db4-11ea-b77f-2e728ce88125';

update t_shelf_comp_dtl set lk_uuid = '73f165c6-586b-11ea-82b4-0242ac130003' where uuid = 'cf687a10-3db4-11ea-b77f-2e728ce88125';
update t_shelf_comp_dtl set lk_uuid = '73f165c6-586b-11ea-82b4-0242ac130003' where uuid = 'cf687b46-3db4-11ea-b77f-2e728ce88125';
update t_shelf_comp_dtl set lk_uuid = '73f165c6-586b-11ea-82b4-0242ac130003' where uuid = 'cf687c72-3db4-11ea-b77f-2e728ce88125';
update t_shelf_comp_dtl set lk_uuid = '73f165c6-586b-11ea-82b4-0242ac130003' where uuid = 'cf687da8-3db4-11ea-b77f-2e728ce88125';
update t_shelf_comp_dtl set lk_uuid = '73f165c6-586b-11ea-82b4-0242ac130003' where uuid = 'cf687ede-3db4-11ea-b77f-2e728ce88125';


insert into t_shelf_lookup (uuid,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,attr1,attr2,status,create_by) 
values ('81dabfea-5878-11ea-8e2d-0242ac130003','H1','Hire Purchase-HW','Hire Purchase-HW','Hire Purchase-HW','DEF_PROD_GROUP','LOOKUP_LIST','Y','1',213,'develop');;
	
insert into t_shelf_lookup (uuid,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,attr1,attr2,status,create_by) 
values ('81dac1fc-5878-11ea-8e2d-0242ac130003','H2','TBANK-HIRE PURCHASE (TL)','TBANK-HIRE PURCHASE (TL)','TBANK-HIRE PURCHASE (TL)','DEF_PROD_GROUP','LOOKUP_LIST','Y','2',213,'develop');;

insert into t_shelf_lookup (uuid,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,attr1,attr2,status,create_by) 
values ('81dac300-5878-11ea-8e2d-0242ac130003','H6','TBANK-HIRE PURCHASE (TCA)','TBANK-HIRE PURCHASE (TCA)','TBANK-HIRE PURCHASE (TCA)','DEF_PROD_GROUP','LOOKUP_LIST','Y','3',213,'develop');;

insert into t_shelf_lookup (uuid,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,attr1,attr2,status,create_by) 
values ('81dac3e6-5878-11ea-8e2d-0242ac130003','HP','Hire Purchase-TISCOFIN','Hire Purchase-TISCOFIN','Hire Purchase-TISCOFIN','DEF_PROD_GROUP','LOOKUP_LIST','Y','4',213,'develop');;

insert into t_shelf_lookup (uuid,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,attr1,attr2,status,create_by) 
values ('81dac4ae-5878-11ea-8e2d-0242ac130003','R1','Personal Loan','Personal Loan','Personal Loan','DEF_PROD_GROUP','LOOKUP_LIST','Y','5',213,'develop');;

insert into t_shelf_lookup (uuid,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,attr1,attr2,status,create_by) 
values ('81dac576-5878-11ea-8e2d-0242ac130003','R2','Loan to Provident Fund Member','Loan to Provident Fund Member','Loan to Provident Fund Member','DEF_PROD_GROUP','LOOKUP_LIST','Y','6',213,'develop');;

insert into t_shelf_lookup (uuid,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,attr1,attr2,status,create_by) 
values ('81dac65c-5878-11ea-8e2d-0242ac130003','RM','Retail Mortgage Finance','Retail Mortgage Finance','Retail Mortgage Finance','DEF_PROD_GROUP','LOOKUP_LIST','Y','7',213,'develop');;

insert into t_shelf_lookup (uuid,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,attr1,attr2,status,create_by) 
values ('81dac742-5878-11ea-8e2d-0242ac130003','RP','Personal Loan-TISCO Leasing','Personal Loan-TISCO Leasing','Personal Loan-TISCO Leasing','DEF_PROD_GROUP','LOOKUP_LIST','Y','8',213,'develop');;











