CREATE TABLE public.t_shelf_menu
(
    uuid character varying(128) COLLATE pg_catalog."default" NOT NULL,
    menu_code text COLLATE pg_catalog."default",
    menu_name text COLLATE pg_catalog."default",
    menu_url text COLLATE pg_catalog."default",
    attr1 text COLLATE pg_catalog."default",
    attr2 text COLLATE pg_catalog."default",
    attr3 text COLLATE pg_catalog."default",
    attr4 text COLLATE pg_catalog."default",
    attr5 text COLLATE pg_catalog."default",
    attr6 text COLLATE pg_catalog."default",
    attr7 text COLLATE pg_catalog."default",
    attr8 text COLLATE pg_catalog."default",
    attr9 text COLLATE pg_catalog."default",
    attr10 text COLLATE pg_catalog."default",
    description text COLLATE pg_catalog."default",
    status integer NOT NULL,
    create_at timestamp(0) without time zone DEFAULT now(),
    create_by character varying(100) COLLATE pg_catalog."default" NOT NULL,
    update_at timestamp(0) without time zone,
    update_by character varying(100) COLLATE pg_catalog."default",
    CONSTRAINT t_shelf_menu_pkey PRIMARY KEY (uuid)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;;

ALTER TABLE public.t_shelf_menu
    OWNER to digitalshelf;;


CREATE TABLE public.t_shelf_role
(
    uuid character varying(128) COLLATE pg_catalog."default" NOT NULL,
    role_id text COLLATE pg_catalog."default",
    role_code text COLLATE pg_catalog."default",
    role_name text COLLATE pg_catalog."default",
    attr1 text COLLATE pg_catalog."default",
    attr2 text COLLATE pg_catalog."default",
    attr3 text COLLATE pg_catalog."default",
    attr4 text COLLATE pg_catalog."default",
    attr5 text COLLATE pg_catalog."default",
    attr6 text COLLATE pg_catalog."default",
    attr7 text COLLATE pg_catalog."default",
    attr8 text COLLATE pg_catalog."default",
    attr9 text COLLATE pg_catalog."default",
    attr10 text COLLATE pg_catalog."default",
    description text COLLATE pg_catalog."default",
    status integer NOT NULL,
    create_at timestamp(0) without time zone DEFAULT now(),
    create_by character varying(100) COLLATE pg_catalog."default" NOT NULL,
    update_at timestamp(0) without time zone,
    update_by character varying(100) COLLATE pg_catalog."default",
    CONSTRAINT t_shelf_role_pkey PRIMARY KEY (uuid)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;;

ALTER TABLE public.t_shelf_role
    OWNER to digitalshelf;;
	
	

CREATE TABLE public.t_shelf_role_menu
(
    uuid character varying(128) COLLATE pg_catalog."default" NOT NULL,
    role_uuid character varying(128) COLLATE pg_catalog."default" NOT NULL,
    menu_uuid character varying(128) COLLATE pg_catalog."default" NOT NULL,
    attr1 text COLLATE pg_catalog."default",
    attr2 text COLLATE pg_catalog."default",
    attr3 text COLLATE pg_catalog."default",
    attr4 text COLLATE pg_catalog."default",
    attr5 text COLLATE pg_catalog."default",
    attr6 text COLLATE pg_catalog."default",
    attr7 text COLLATE pg_catalog."default",
    attr8 text COLLATE pg_catalog."default",
    attr9 text COLLATE pg_catalog."default",
    attr10 text COLLATE pg_catalog."default",
    description text COLLATE pg_catalog."default",
    status integer NOT NULL,
    create_at timestamp(0) without time zone DEFAULT now(),
    create_by character varying(100) COLLATE pg_catalog."default" NOT NULL,
    update_at timestamp(0) without time zone,
    update_by character varying(100) COLLATE pg_catalog."default",
    CONSTRAINT t_shelf_role_menu_pkey PRIMARY KEY (uuid),
    CONSTRAINT t_shelf_role_menu_menu_uuid_fkey FOREIGN KEY (menu_uuid)
        REFERENCES public.t_shelf_menu (uuid) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT t_shelf_role_menu_role_uuid_fkey FOREIGN KEY (role_uuid)
        REFERENCES public.t_shelf_role (uuid) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;;

ALTER TABLE public.t_shelf_role_menu
    OWNER to digitalshelf;;
	

insert into t_sys_lookup (uuid,lookup_code,lookup_name_th,lookup_name_en,lookup_type,lookup_value,status,description,create_by,flag_edit,flag_create)
values('fed27e36-5c54-11ea-bc55-0242ac130003','400','ยกเลิก','Cancel','status','cancel',213,'ยกเลิก เกิดจาก inactive มาเป็น cancel','develop',false,false);;

insert into t_sys_lookup (uuid,lookup_code,lookup_name_th,lookup_name_en,lookup_type,lookup_value,status,description,create_by,flag_edit,flag_create)
values('ef743950-5c97-11ea-bc55-0242ac130003','400','Expire','Expire','status','expire',213,'สำหรับ active มาเป็น expire','develop',false,false);;

insert into t_shelf_lookup (uuid,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,attr1,attr2,status,create_by) values('302acca2-5d24-11ea-bc55-0242ac130003','RES1001','อนุมัติ','อนุมัติ','อนุมัติ','DEF_TMP_APPROVE','LOOKUP_LIST','Y','1',213	,'develop');
insert into t_shelf_lookup (uuid,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,attr1,attr2,status,create_by) values('302acfb8-5d24-11ea-bc55-0242ac130003','RES1002','Template ที่สร้างไม่ครบถ้วน','Template ที่สร้างไม่ครบถ้วน','Template ที่สร้างไม่ครบถ้วน','DEF_TMP_REJECT','LOOKUP_LIST','Y','1',213	,'develop');
insert into t_shelf_lookup (uuid,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,attr1,attr2,status,create_by) values('302ad148-5d24-11ea-bc55-0242ac130003','RES1003','เปลี่ยนแปลงเงื่อนไขทางธุระกิจ','เปลี่ยนแปลงเงื่อนไขทางธุระกิจ','เปลี่ยนแปลงเงื่อนไขทางธุระกิจ','DEF_TMP_REJECT','LOOKUP_LIST','Y','2',213	,'develop');
insert into t_shelf_lookup (uuid,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,attr1,attr2,status,create_by) values('302ad2e2-5d24-11ea-bc55-0242ac130003','RES1004','อื่นๆ','อื่นๆ','อื่นๆ','DEF_TMP_REJECT','LOOKUP_LIST','Y','3',213	,'develop');
insert into t_shelf_lookup (uuid,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,attr1,attr2,status,create_by) values('302ad472-5d24-11ea-bc55-0242ac130003','RES1005','ยกเลิก Template','ยกเลิก Template','ยกเลิก Template','DEF_TMP_DELETE','LOOKUP_LIST','Y','1',213	,'develop');
insert into t_shelf_lookup (uuid,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,attr1,attr2,status,create_by) values('302ad5d0-5d24-11ea-bc55-0242ac130003','RES1006','สร้าง Template ไม่ถูกต้อง','สร้าง Template ไม่ถูกต้อง','สร้าง Template ไม่ถูกต้อง','DEF_TMP_DELETE','LOOKUP_LIST','Y','2',213	,'develop');
insert into t_shelf_lookup (uuid,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,attr1,attr2,status,create_by) values('302ad6c0-5d24-11ea-bc55-0242ac130003','RES1007','อื่นๆ','อื่นๆ','อื่นๆ','DEF_TMP_DELETE','LOOKUP_LIST','Y','3',213	,'develop');
insert into t_shelf_lookup (uuid,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,attr1,attr2,status,create_by) values('302ad800-5d24-11ea-bc55-0242ac130003','RES1008','XXXX','XXXX','XXXX','DEF_TMP_PAUSE','LOOKUP_LIST','Y','1',213	,'develop');
insert into t_shelf_lookup (uuid,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,attr1,attr2,status,create_by) values('302ad922-5d24-11ea-bc55-0242ac130003','RES1009','XXXX','XXXX','XXXX','DEF_TMP_PAUSE','LOOKUP_LIST','Y','2',213	,'develop');
insert into t_shelf_lookup (uuid,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,attr1,attr2,status,create_by) values('302ada6c-5d24-11ea-bc55-0242ac130003','RES1010','อื่นๆ','อื่นๆ','อื่นๆ','DEF_TMP_PAUSE','LOOKUP_LIST','Y','3',213	,'develop');


insert into t_shelf_lookup (uuid,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,attr1,attr2,status,create_by) values('5bada26e-5d24-11ea-bc55-0242ac130003','RES1014','อนุมัติ','อนุมัติ','อนุมัติ','DEF_PROD_APPROVE','LOOKUP_LIST','Y','1 ',213	,'	develop');
insert into t_shelf_lookup (uuid,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,attr1,attr2,status,create_by) values('5bada656-5d24-11ea-bc55-0242ac130003','RES1015','Product ที่สร้างไม่ครบถ้วน','Product ที่สร้างไม่ครบถ้วน','Product ที่สร้างไม่ครบถ้วน','DEF_PROD_REJECT','LOOKUP_LIST','Y','1',213	,'	develop');
insert into t_shelf_lookup (uuid,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,attr1,attr2,status,create_by) values('5bada75a-5d24-11ea-bc55-0242ac130003','RES1016','เปลี่ยนแปลงเงื่อนไขทางธุระกิจ','เปลี่ยนแปลงเงื่อนไขทางธุระกิจ','เปลี่ยนแปลงเงื่อนไขทางธุระกิจ','DEF_PROD_REJECT','LOOKUP_LIST','Y','2',213	,'	develop');
insert into t_shelf_lookup (uuid,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,attr1,attr2,status,create_by) values('5bada836-5d24-11ea-bc55-0242ac130003','RES1017','อื่นๆ','อื่นๆ','อื่นๆ','DEF_PROD_REJECT','LOOKUP_LIST','Y','3',213	,'	develop');
insert into t_shelf_lookup (uuid,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,attr1,attr2,status,create_by) values('5badaaa2-5d24-11ea-bc55-0242ac130003','RES1018','ยกเลิก Product','ยกเลิก Product','ยกเลิก Product','DEF_PROD_DELETE','LOOKUP_LIST','Y','1',213	,'	develop');
insert into t_shelf_lookup (uuid,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,attr1,attr2,status,create_by) values('5badab7e-5d24-11ea-bc55-0242ac130003','RES1019','สร้าง Product ไม่ถูกต้อง','สร้าง Product ไม่ถูกต้อง','สร้าง Product ไม่ถูกต้อง','DEF_PROD_DELETE','LOOKUP_LIST','Y','2',213	,'	develop');
insert into t_shelf_lookup (uuid,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,attr1,attr2,status,create_by) values('5badac50-5d24-11ea-bc55-0242ac130003','RES1020','อื่นๆ','อื่นๆ','อื่นๆ','DEF_PROD_DELETE','LOOKUP_LIST','Y','3',213	,'	develop');
insert into t_shelf_lookup (uuid,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,attr1,attr2,status,create_by) values('5badad18-5d24-11ea-bc55-0242ac130003','RES1021','XXXX','XXXX','XXXX','DEF_PROD_PAUSE','LOOKUP_LIST','Y','1',213	,'	develop');
insert into t_shelf_lookup (uuid,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,attr1,attr2,status,create_by) values('5badade0-5d24-11ea-bc55-0242ac130003','RES1022','XXXX','XXXX','XXXX','DEF_PROD_PAUSE','LOOKUP_LIST','Y','2',213	,'	develop');
insert into t_shelf_lookup (uuid,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,attr1,attr2,status,create_by) values('5badaea8-5d24-11ea-bc55-0242ac130003','RES1023','อื่นๆ','อื่นๆ','อื่นๆ','DEF_PROD_PAUSE','LOOKUP_LIST','Y','3',213	,'	develop');
insert into t_shelf_lookup (uuid,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,attr1,attr2,status,create_by) values('5badaf66-5d24-11ea-bc55-0242ac130003','RES1024','XXXX','XXXX','XXXX','DEF_PROD_TERMINATE','LOOKUP_LIST','Y','1',213	,'	develop');
insert into t_shelf_lookup (uuid,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,attr1,attr2,status,create_by) values('5badb254-5d24-11ea-bc55-0242ac130003','RES1025','XXXX','XXXX','XXXX','DEF_PROD_TERMINATE','LOOKUP_LIST','Y','2',213	,'	develop');
insert into t_shelf_lookup (uuid,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,attr1,attr2,status,create_by) values('5badb330-5d24-11ea-bc55-0242ac130003','RES1026','อื่นๆ','อื่นๆ','อื่นๆ','DEF_PROD_TERMINATE','LOOKUP_LIST','Y','3',213	,'	develop');


DROP TABLE t_sys_oper_log;;

CREATE TABLE public.t_sys_oper_log
(
    uuid character varying(128) COLLATE pg_catalog."default" NOT NULL,
    trn_id character varying(200) COLLATE pg_catalog."default" NOT NULL,
    source character varying(255) COLLATE pg_catalog."default" NOT NULL,
    product_id character varying(128) COLLATE pg_catalog."default" NOT NULL,
    product_code character varying(128) COLLATE pg_catalog."default" NOT NULL,
    product_version_id character varying(128) COLLATE pg_catalog."default" NOT NULL,
    product_component_id character varying COLLATE pg_catalog."default",
    task_category character varying(128) COLLATE pg_catalog."default",
    keywords character varying(255) COLLATE pg_catalog."default",
    trn_status integer,
    trn_sub_status integer,
    failure_reason text COLLATE pg_catalog."default",
    source_device character varying(255) COLLATE pg_catalog."default",
    source_device_id character varying(100) COLLATE pg_catalog."default",
    source_cif_id character varying(100) COLLATE pg_catalog."default",
    account_name character varying(100) COLLATE pg_catalog."default",
    business_date timestamp(0) without time zone,
    ref_no character varying(128) COLLATE pg_catalog."default" NOT NULL,
    payment_method character varying(128) COLLATE pg_catalog."default" NOT NULL,
    payment_date timestamp(0) without time zone,
    state_code character varying(128) COLLATE pg_catalog."default",
    prod_channel character varying(128) COLLATE pg_catalog."default",
    step_data character varying(128) COLLATE pg_catalog."default",
    case_id character varying(128) COLLATE pg_catalog."default",
    group_product character varying(128) COLLATE pg_catalog."default",
    txn_no character varying(128) COLLATE pg_catalog."default",
    attr1 text COLLATE pg_catalog."default",
    attr2 text COLLATE pg_catalog."default",
    attr3 text COLLATE pg_catalog."default",
    attr4 text COLLATE pg_catalog."default",
    attr5 text COLLATE pg_catalog."default",
    attr6 text COLLATE pg_catalog."default",
    attr7 text COLLATE pg_catalog."default",
    attr8 text COLLATE pg_catalog."default",
    attr9 text COLLATE pg_catalog."default",
    attr10 text COLLATE pg_catalog."default",
    status integer NOT NULL,
    create_at timestamp(0) without time zone DEFAULT now(),
    create_by character varying(100) COLLATE pg_catalog."default",
    state_time bigint,
    CONSTRAINT t_sys_oper_log_pkey PRIMARY KEY (uuid),
    CONSTRAINT t_sys_oper_log_state_code_fkey FOREIGN KEY (state_code)
        REFERENCES public.t_shelf_lookup (uuid) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;;

ALTER TABLE public.t_sys_oper_log
    OWNER to digitalshelf;;

COMMENT ON COLUMN public.t_sys_oper_log.uuid
    IS 'Logging ID';;

COMMENT ON COLUMN public.t_sys_oper_log.trn_id
    IS 'Product Transaction ID';;

COMMENT ON COLUMN public.t_sys_oper_log.source
    IS 'Source of Logging Action URL';;

COMMENT ON COLUMN public.t_sys_oper_log.product_id
    IS 'product_uuid';;

COMMENT ON COLUMN public.t_sys_oper_log.product_version_id
    IS 'product_version_uuid';;

COMMENT ON COLUMN public.t_sys_oper_log.product_component_id
    IS 'product detail uuid';;

COMMENT ON COLUMN public.t_sys_oper_log.task_category
    IS 'task of action';;

COMMENT ON COLUMN public.t_sys_oper_log.keywords
    IS 'Keywords for searching';;

COMMENT ON COLUMN public.t_sys_oper_log.trn_status
    IS 'Transaction Status Code e.g. Pass, Fail';;

COMMENT ON COLUMN public.t_sys_oper_log.trn_sub_status
    IS 'Sub Status of Transaction Code e.g. Timeout, Success, Validation Fail, Response Code';

COMMENT ON COLUMN public.t_sys_oper_log.failure_reason
    IS 'The explaination of failure';;

COMMENT ON COLUMN public.t_sys_oper_log.source_device
    IS 'Device name of Doer';;

COMMENT ON COLUMN public.t_sys_oper_log.source_device_id
    IS 'Source of Device ID';;

COMMENT ON COLUMN public.t_sys_oper_log.source_cif_id
    IS 'Source Customer Info ID';;

COMMENT ON COLUMN public.t_sys_oper_log.account_name
    IS 'Computer login name of Doer';;
	
	
	