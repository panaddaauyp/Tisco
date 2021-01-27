--
-- PostgreSQL database dump
--

-- Dumped from database version 11.6
-- Dumped by pg_dump version 11.3

-- Started on 2020-02-19 09:05:51

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 227 (class 1255 OID 16403)
-- Name: f_get_product_number(date, text, text, text); Type: FUNCTION; Schema: public; Owner: digitalshelf
--

CREATE FUNCTION public.f_get_product_number(p_doc_date date, p_prod_uuid text, p_prod_code text, p_user text) RETURNS text
    LANGUAGE plpgsql
    AS $_$

DECLARE
/***************************************************************************************
Date		Version		Create_by	Remark
-----------------------------------------------------------------------------------------
2020/02/06	v1.00		kritsana		create product number
***************************************************************************************/

   /***input parameter****/
  p_doc_date date := $1 ;
  p_prod_uuid	text := $2;
  p_prod_code text := $3;
  p_user	text := $4;
  
  /*Variable*/
  v_result  text;
  v_count	integer ;
  v_year text;
  
BEGIN
	select to_char(COALESCE(p_doc_date,current_date),'YYYY') into v_year;
	
	select count(*) 
	into v_count 
	From t_product_maxseq
	where prod_code = COALESCE(p_prod_code, '')
	and prod_uuid = COALESCE(p_prod_uuid, '')
	and attribute1 = v_year;
	
	IF (v_count > 0) THEN
		v_result := v_year||lpad(cast(v_count + 1 as TEXT),6,'0');
	ELSE
		v_result := v_year||'000001';
	END IF;
	v_result = p_prod_code||'-'||v_result;
		EXECUTE format('INSERT INTO public.t_product_maxseq(product_number, doc_date, prod_code, prod_uuid, created_by,attribute1)
                					VALUES($1,$2,$3,$4,$5,$6);') using v_result,p_doc_date,p_prod_code,p_prod_uuid,p_user,v_year;
    RETURN v_result;
EXCEPTION
WHEN OTHERS THEN
	RAISE INFO 'State: %,Error Name: %', SQLSTATE,SQLERRM;
    v_result := NULL;
    Return  v_result;
END;

$_$;


ALTER FUNCTION public.f_get_product_number(p_doc_date date, p_prod_uuid text, p_prod_code text, p_user text) OWNER TO digitalshelf;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 196 (class 1259 OID 16404)
-- Name: t_product_maxseq; Type: TABLE; Schema: public; Owner: digitalshelf
--

CREATE TABLE public.t_product_maxseq (
    product_number text,
    doc_date date,
    prod_code text,
    prod_uuid text,
    attribute1 text,
    attribute2 text,
    attribute3 text,
    attribute4 text,
    attribute5 text,
    attribute6 text,
    attribute7 text,
    attribute8 text,
    attribute9 text,
    attribute10 text,
    created_by text,
    created_at timestamp(4) without time zone DEFAULT now(),
    updated_by text,
    updated_at timestamp(4) without time zone
);


ALTER TABLE public.t_product_maxseq OWNER TO digitalshelf;

--
-- TOC entry 197 (class 1259 OID 16411)
-- Name: t_shelf_comp; Type: TABLE; Schema: public; Owner: digitalshelf
--

CREATE TABLE public.t_shelf_comp (
    uuid character varying(128) NOT NULL,
    seq_no bigint NOT NULL,
    comp_code character varying(100),
    comp_name character varying(100) NOT NULL,
    value text,
    attr1 text,
    attr2 text,
    attr3 text,
    attr4 text,
    attr5 text,
    attr6 text,
    attr7 text,
    attr8 text,
    attr9 text,
    attr10 text,
    description text,
    status integer NOT NULL,
    create_at timestamp(0) without time zone DEFAULT now(),
    create_by character varying(100) NOT NULL,
    update_at timestamp(0) without time zone,
    update_by character varying(100),
    pattern text
);


ALTER TABLE public.t_shelf_comp OWNER TO digitalshelf;

--
-- TOC entry 3982 (class 0 OID 0)
-- Dependencies: 197
-- Name: COLUMN t_shelf_comp.attr1; Type: COMMENT; Schema: public; Owner: digitalshelf
--

COMMENT ON COLUMN public.t_shelf_comp.attr1 IS 'json ของ component นั้นๆ';


--
-- TOC entry 3983 (class 0 OID 0)
-- Dependencies: 197
-- Name: COLUMN t_shelf_comp.attr10; Type: COMMENT; Schema: public; Owner: digitalshelf
--

COMMENT ON COLUMN public.t_shelf_comp.attr10 IS 'JSON ไว้ตรวจสอบในการนำข้อมูลไปสร้าง ใน 3rd Table';


--
-- TOC entry 198 (class 1259 OID 16418)
-- Name: t_shelf_comp_dtl; Type: TABLE; Schema: public; Owner: digitalshelf
--

CREATE TABLE public.t_shelf_comp_dtl (
    uuid character varying(128) NOT NULL,
    comp_uuid character varying(128) NOT NULL,
    lk_uuid character varying(128) NOT NULL,
    label_text character varying,
    data_value character varying,
    description character varying,
    attr1 text,
    attr2 text,
    attr3 text,
    attr4 text,
    attr5 text,
    attr6 text,
    attr7 text,
    attr8 text,
    attr9 text,
    attr10 text,
    seq integer,
    parent integer,
    status integer NOT NULL,
    create_at timestamp(0) without time zone DEFAULT now(),
    create_by character varying(100) NOT NULL,
    update_at timestamp(0) without time zone,
    update_by character varying(100),
    ele_id text,
    require boolean,
    validation text,
    pattern text,
    table_column text
);


ALTER TABLE public.t_shelf_comp_dtl OWNER TO digitalshelf;

--
-- TOC entry 3984 (class 0 OID 0)
-- Dependencies: 198
-- Name: COLUMN t_shelf_comp_dtl.data_value; Type: COMMENT; Schema: public; Owner: digitalshelf
--

COMMENT ON COLUMN public.t_shelf_comp_dtl.data_value IS 'list of data [{value:''m'',label:''Mobile''},{value:''w'',label:''Website''}]';


--
-- TOC entry 3985 (class 0 OID 0)
-- Dependencies: 198
-- Name: COLUMN t_shelf_comp_dtl.description; Type: COMMENT; Schema: public; Owner: digitalshelf
--

COMMENT ON COLUMN public.t_shelf_comp_dtl.description IS 'show description right of input field';


--
-- TOC entry 199 (class 1259 OID 16425)
-- Name: t_shelf_comp_seq_no_seq; Type: SEQUENCE; Schema: public; Owner: digitalshelf
--

CREATE SEQUENCE public.t_shelf_comp_seq_no_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.t_shelf_comp_seq_no_seq OWNER TO digitalshelf;

--
-- TOC entry 3986 (class 0 OID 0)
-- Dependencies: 199
-- Name: t_shelf_comp_seq_no_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: digitalshelf
--

ALTER SEQUENCE public.t_shelf_comp_seq_no_seq OWNED BY public.t_shelf_comp.seq_no;


--
-- TOC entry 200 (class 1259 OID 16427)
-- Name: t_shelf_lookup; Type: TABLE; Schema: public; Owner: digitalshelf
--

CREATE TABLE public.t_shelf_lookup (
    uuid character varying(128) NOT NULL,
    lookup_code character varying(255) NOT NULL,
    lookup_name_th character varying(255) NOT NULL,
    lookup_name_en character varying(255) NOT NULL,
    description character varying,
    group_type character varying(255) NOT NULL,
    lookup_type character varying(255) NOT NULL,
    lookup_value text,
    attr1 text,
    attr2 text,
    attr3 text,
    attr4 text,
    attr5 text,
    attr6 text,
    attr7 text,
    attr8 text,
    attr9 text,
    attr10 text,
    status integer NOT NULL,
    create_at timestamp(0) without time zone DEFAULT now(),
    create_by character varying(100) NOT NULL,
    update_at timestamp(0) without time zone,
    update_by character varying(100)
);


ALTER TABLE public.t_shelf_lookup OWNER TO digitalshelf;

--
-- TOC entry 3987 (class 0 OID 0)
-- Dependencies: 200
-- Name: COLUMN t_shelf_lookup.group_type; Type: COMMENT; Schema: public; Owner: digitalshelf
--

COMMENT ON COLUMN public.t_shelf_lookup.group_type IS 'ex. INPUT,GROUP,TABLE,BUTTON';


--
-- TOC entry 3988 (class 0 OID 0)
-- Dependencies: 200
-- Name: COLUMN t_shelf_lookup.lookup_type; Type: COMMENT; Schema: public; Owner: digitalshelf
--

COMMENT ON COLUMN public.t_shelf_lookup.lookup_type IS 'ex. ELEMENT';


--
-- TOC entry 201 (class 1259 OID 16434)
-- Name: t_shelf_product; Type: TABLE; Schema: public; Owner: digitalshelf
--

CREATE TABLE public.t_shelf_product (
    uuid character varying(128) NOT NULL,
    prod_code text,
    prod_name text,
    business_line text,
    business_dept text,
    company text,
    prod_type text,
    prod_url text,
    active_date timestamp(0) without time zone,
    end_date timestamp(0) without time zone,
    prod_day text,
    prod_time text,
    campaign_id text,
    campaign_name text,
    link_channel text,
    product_channel text,
    attr1 text,
    attr2 text,
    attr3 text,
    attr4 text,
    attr5 text,
    attr6 text,
    attr7 text,
    attr8 text,
    attr9 text,
    attr10 text,
    status integer NOT NULL,
    create_at timestamp(0) without time zone DEFAULT now(),
    create_by character varying(100) NOT NULL,
    update_at timestamp(0) without time zone,
    update_by character varying(100)
);


ALTER TABLE public.t_shelf_product OWNER TO digitalshelf;

--
-- TOC entry 202 (class 1259 OID 16441)
-- Name: t_shelf_product_attach; Type: TABLE; Schema: public; Owner: digitalshelf
--

CREATE TABLE public.t_shelf_product_attach (
    uuid character varying(128) NOT NULL,
    dtl_uuid character varying(50) NOT NULL,
    file_type character varying(50) NOT NULL,
    file_name text,
    file_value text,
    description text,
    attr1 text,
    attr2 text,
    attr3 text,
    attr4 text,
    attr5 text,
    attr6 text,
    attr7 text,
    attr8 text,
    attr9 text,
    attr10 text,
    status integer NOT NULL,
    create_at timestamp(0) without time zone DEFAULT now(),
    create_by character varying(100) NOT NULL,
    update_at timestamp(0) without time zone,
    update_by character varying(100)
);


ALTER TABLE public.t_shelf_product_attach OWNER TO digitalshelf;

--
-- TOC entry 203 (class 1259 OID 16448)
-- Name: t_shelf_product_dtl; Type: TABLE; Schema: public; Owner: digitalshelf
--

CREATE TABLE public.t_shelf_product_dtl (
    uuid character varying(128) NOT NULL,
    lk_uuid character varying(128),
    trn_uuid character varying(128) NOT NULL,
    lk_code text,
    lk_label text,
    lk_value text,
    lk_require boolean,
    lk_validation text,
    lk_description text,
    business_dept text,
    company text,
    prod_type text,
    prod_url text,
    active_date timestamp(0) without time zone,
    end_date timestamp(0) without time zone,
    prod_day text,
    prod_time text,
    campaign_id text,
    campaign_name text,
    link_channel text,
    product_channel text,
    attr1 text,
    attr2 text,
    attr3 text,
    attr4 text,
    attr5 text,
    attr6 text,
    attr7 text,
    attr8 text,
    attr9 text,
    attr10 text,
    status integer NOT NULL,
    create_at timestamp(0) without time zone DEFAULT now(),
    create_by character varying(100) NOT NULL,
    update_at timestamp(0) without time zone,
    update_by character varying(100),
    dtl_status integer
);


ALTER TABLE public.t_shelf_product_dtl OWNER TO digitalshelf;

--
-- TOC entry 3989 (class 0 OID 0)
-- Dependencies: 203
-- Name: COLUMN t_shelf_product_dtl.lk_uuid; Type: COMMENT; Schema: public; Owner: digitalshelf
--

COMMENT ON COLUMN public.t_shelf_product_dtl.lk_uuid IS 'id ของ json ตอนสร้าง component ไม่ได้หมายถึง lookup_uuid';


--
-- TOC entry 3990 (class 0 OID 0)
-- Dependencies: 203
-- Name: COLUMN t_shelf_product_dtl.lk_code; Type: COMMENT; Schema: public; Owner: digitalshelf
--

COMMENT ON COLUMN public.t_shelf_product_dtl.lk_code IS 'เก็บเป็น id ของ field นั้นๆ';


--
-- TOC entry 204 (class 1259 OID 16455)
-- Name: t_shelf_product_vcs; Type: TABLE; Schema: public; Owner: digitalshelf
--

CREATE TABLE public.t_shelf_product_vcs (
    uuid character varying(128) NOT NULL,
    prod_uuid character varying(128) NOT NULL,
    comp_uuid character varying(128),
    tem_uuid character varying(128) NOT NULL,
    theme_uuid character varying(128) NOT NULL,
    ver_comp integer NOT NULL,
    ver_tem integer NOT NULL,
    ver_prod integer NOT NULL,
    effective_date timestamp(0) without time zone,
    state character varying(255) NOT NULL,
    attr1 text,
    attr2 text,
    attr3 text,
    attr4 text,
    attr5 text,
    attr6 text,
    attr7 text,
    attr8 text,
    attr9 text,
    attr10 text,
    status integer NOT NULL,
    create_at timestamp(0) without time zone DEFAULT now(),
    create_by character varying(100) NOT NULL,
    update_at timestamp(0) without time zone,
    update_by character varying(100),
    comp_status integer
);


ALTER TABLE public.t_shelf_product_vcs OWNER TO digitalshelf;

--
-- TOC entry 3991 (class 0 OID 0)
-- Dependencies: 204
-- Name: COLUMN t_shelf_product_vcs.attr1; Type: COMMENT; Schema: public; Owner: digitalshelf
--

COMMENT ON COLUMN public.t_shelf_product_vcs.attr1 IS 'data of product';


--
-- TOC entry 205 (class 1259 OID 16462)
-- Name: t_shelf_theme; Type: TABLE; Schema: public; Owner: digitalshelf
--

CREATE TABLE public.t_shelf_theme (
    uuid character varying(128) NOT NULL,
    theme_code character varying(100),
    theme_name character varying(100) NOT NULL,
    value json NOT NULL,
    state text,
    attr1 text,
    attr2 text,
    attr3 text,
    attr4 text,
    attr5 text,
    attr6 text,
    attr7 text,
    attr8 text,
    attr9 text,
    attr10 text,
    description text,
    status integer NOT NULL,
    create_at timestamp(0) without time zone DEFAULT now(),
    create_by character varying(100) NOT NULL,
    update_at timestamp(0) without time zone,
    update_by character varying(100)
);


ALTER TABLE public.t_shelf_theme OWNER TO digitalshelf;

--
-- TOC entry 206 (class 1259 OID 16469)
-- Name: t_shelf_tmp; Type: TABLE; Schema: public; Owner: digitalshelf
--

CREATE TABLE public.t_shelf_tmp (
    uuid character varying(128) NOT NULL,
    tmp_name character varying(100) NOT NULL,
    value text,
    current_vcs_uuid character varying(128),
    previous_vcs_uuid character varying(128),
    attr1 text,
    attr2 text,
    attr3 text,
    attr4 text,
    attr5 text,
    attr6 text,
    attr7 text,
    attr8 text,
    attr9 text,
    attr10 text,
    description text,
    status integer NOT NULL,
    create_at timestamp(0) without time zone DEFAULT now(),
    create_by character varying(100) NOT NULL,
    update_at timestamp(0) without time zone,
    update_by character varying(100),
    company_code character varying(128),
    bussiness_dept character varying(128),
    business_line character varying(128)
);


ALTER TABLE public.t_shelf_tmp OWNER TO digitalshelf;

--
-- TOC entry 207 (class 1259 OID 16476)
-- Name: t_shelf_tmp_attach; Type: TABLE; Schema: public; Owner: digitalshelf
--

CREATE TABLE public.t_shelf_tmp_attach (
    uuid character varying(128) NOT NULL,
    tmp_uuid character varying(50) NOT NULL,
    type character varying(50) NOT NULL,
    value character varying NOT NULL,
    effective_date date NOT NULL,
    attr1 text,
    attr2 text,
    attr3 text,
    attr4 text,
    attr5 text,
    attr6 text,
    attr7 text,
    attr8 text,
    attr9 text,
    attr10 text,
    description text,
    status integer NOT NULL,
    create_at timestamp(0) without time zone DEFAULT now(),
    create_by character varying(100) NOT NULL,
    update_at timestamp(0) without time zone,
    update_by character varying(100),
    vcs_uuid character varying(128)
);


ALTER TABLE public.t_shelf_tmp_attach OWNER TO digitalshelf;

--
-- TOC entry 208 (class 1259 OID 16483)
-- Name: t_shelf_tmp_detail; Type: TABLE; Schema: public; Owner: digitalshelf
--

CREATE TABLE public.t_shelf_tmp_detail (
    uuid character varying(128) NOT NULL,
    comp_uuid character varying(128) NOT NULL,
    vcs_uuid character varying(128) NOT NULL,
    lookup_uuid character varying(128),
    attr1 text,
    attr2 text,
    attr3 text,
    attr4 text,
    attr5 text,
    attr6 text,
    attr7 text,
    attr8 text,
    attr9 text,
    attr10 text,
    description text,
    status integer NOT NULL,
    create_at timestamp(0) without time zone DEFAULT now(),
    create_by character varying(100) NOT NULL,
    update_at timestamp(0) without time zone,
    update_by character varying(100),
    seq_no integer NOT NULL,
    att_uuid character varying(128),
    value json,
    flag_enable boolean DEFAULT false
);


ALTER TABLE public.t_shelf_tmp_detail OWNER TO digitalshelf;

--
-- TOC entry 209 (class 1259 OID 16491)
-- Name: t_shelf_tmp_vcs; Type: TABLE; Schema: public; Owner: digitalshelf
--

CREATE TABLE public.t_shelf_tmp_vcs (
    uuid character varying(128) NOT NULL,
    tmp_uuid character varying(128) NOT NULL,
    state text,
    attr1 text,
    attr2 text,
    attr3 text,
    attr4 text,
    attr5 text,
    attr6 text,
    attr7 text,
    attr8 text,
    attr9 text,
    attr10 text,
    description text,
    status integer NOT NULL,
    create_at timestamp(0) without time zone DEFAULT now(),
    create_by character varying(100) NOT NULL,
    update_at timestamp(0) without time zone,
    update_by character varying(100),
    effective_date date NOT NULL,
    version integer NOT NULL,
    vcs_uuid character varying(128)
);


ALTER TABLE public.t_shelf_tmp_vcs OWNER TO digitalshelf;

--
-- TOC entry 210 (class 1259 OID 16498)
-- Name: t_sys_audit_log; Type: TABLE; Schema: public; Owner: digitalshelf
--

CREATE TABLE public.t_sys_audit_log (
    uuid character varying(128) NOT NULL,
    log_name character varying(200) NOT NULL,
    source character varying(255) NOT NULL,
    event_id character varying(128),
    level character varying(50) NOT NULL,
    task_category character varying(128),
    keywords character varying(255),
    computer character varying(255),
    account_name character varying(100),
    account_domain character varying(100),
    access_type character varying(128),
    object_name character varying(128),
    resource_attribute text,
    attr1 text,
    attr2 text,
    attr3 text,
    attr4 text,
    attr5 text,
    attr6 text,
    attr7 text,
    attr8 text,
    attr9 text,
    attr10 text,
    status integer NOT NULL,
    create_at timestamp(0) without time zone DEFAULT now(),
    create_by character varying(100) NOT NULL
);


ALTER TABLE public.t_sys_audit_log OWNER TO digitalshelf;

--
-- TOC entry 3992 (class 0 OID 0)
-- Dependencies: 210
-- Name: COLUMN t_sys_audit_log.uuid; Type: COMMENT; Schema: public; Owner: digitalshelf
--

COMMENT ON COLUMN public.t_sys_audit_log.uuid IS 'Logging ID';


--
-- TOC entry 3993 (class 0 OID 0)
-- Dependencies: 210
-- Name: COLUMN t_sys_audit_log.log_name; Type: COMMENT; Schema: public; Owner: digitalshelf
--

COMMENT ON COLUMN public.t_sys_audit_log.log_name IS 'Name of Logging';


--
-- TOC entry 3994 (class 0 OID 0)
-- Dependencies: 210
-- Name: COLUMN t_sys_audit_log.source; Type: COMMENT; Schema: public; Owner: digitalshelf
--

COMMENT ON COLUMN public.t_sys_audit_log.source IS 'Source of Logging Action';


--
-- TOC entry 3995 (class 0 OID 0)
-- Dependencies: 210
-- Name: COLUMN t_sys_audit_log.event_id; Type: COMMENT; Schema: public; Owner: digitalshelf
--

COMMENT ON COLUMN public.t_sys_audit_log.event_id IS 'Event Code';


--
-- TOC entry 3996 (class 0 OID 0)
-- Dependencies: 210
-- Name: COLUMN t_sys_audit_log.level; Type: COMMENT; Schema: public; Owner: digitalshelf
--

COMMENT ON COLUMN public.t_sys_audit_log.level IS 'Trace,Debug,Info,Warn,Error';


--
-- TOC entry 3997 (class 0 OID 0)
-- Dependencies: 210
-- Name: COLUMN t_sys_audit_log.keywords; Type: COMMENT; Schema: public; Owner: digitalshelf
--

COMMENT ON COLUMN public.t_sys_audit_log.keywords IS 'Keywords for searching';


--
-- TOC entry 3998 (class 0 OID 0)
-- Dependencies: 210
-- Name: COLUMN t_sys_audit_log.computer; Type: COMMENT; Schema: public; Owner: digitalshelf
--

COMMENT ON COLUMN public.t_sys_audit_log.computer IS 'Computer name of Doer';


--
-- TOC entry 3999 (class 0 OID 0)
-- Dependencies: 210
-- Name: COLUMN t_sys_audit_log.account_name; Type: COMMENT; Schema: public; Owner: digitalshelf
--

COMMENT ON COLUMN public.t_sys_audit_log.account_name IS 'Computer login name of Doer';


--
-- TOC entry 4000 (class 0 OID 0)
-- Dependencies: 210
-- Name: COLUMN t_sys_audit_log.account_domain; Type: COMMENT; Schema: public; Owner: digitalshelf
--

COMMENT ON COLUMN public.t_sys_audit_log.account_domain IS 'Compter domain name of Doer';


--
-- TOC entry 4001 (class 0 OID 0)
-- Dependencies: 210
-- Name: COLUMN t_sys_audit_log.access_type; Type: COMMENT; Schema: public; Owner: digitalshelf
--

COMMENT ON COLUMN public.t_sys_audit_log.access_type IS 'Querry, Insert, Update and Delete';


--
-- TOC entry 4002 (class 0 OID 0)
-- Dependencies: 210
-- Name: COLUMN t_sys_audit_log.object_name; Type: COMMENT; Schema: public; Owner: digitalshelf
--

COMMENT ON COLUMN public.t_sys_audit_log.object_name IS 'Table name or object name';


--
-- TOC entry 4003 (class 0 OID 0)
-- Dependencies: 210
-- Name: COLUMN t_sys_audit_log.resource_attribute; Type: COMMENT; Schema: public; Owner: digitalshelf
--

COMMENT ON COLUMN public.t_sys_audit_log.resource_attribute IS 'Condition for action';


--
-- TOC entry 214 (class 1259 OID 16659)
-- Name: t_sys_log; Type: TABLE; Schema: public; Owner: digitalshelf
--

CREATE TABLE public.t_sys_log (
    uuid character varying(128) NOT NULL,
    prod_code character varying(200) NOT NULL,
    case_id character varying(255) NOT NULL,
    group_product character varying(255),
    state character varying(255),
    attr1 text,
    attr2 text,
    attr3 text,
    attr4 text,
    attr5 text,
    attr6 text,
    attr7 text,
    attr8 text,
    attr9 text,
    attr10 text,
    status integer NOT NULL,
    create_at timestamp(0) without time zone DEFAULT now(),
    create_by character varying(100),
    update_at timestamp without time zone,
    update_by character varying(100)
);


ALTER TABLE public.t_sys_log OWNER TO digitalshelf;

--
-- TOC entry 211 (class 1259 OID 16505)
-- Name: t_sys_lookup; Type: TABLE; Schema: public; Owner: digitalshelf
--

CREATE TABLE public.t_sys_lookup (
    uuid character varying(128) NOT NULL,
    lookup_code character varying(255) NOT NULL,
    lookup_name_th character varying(255) NOT NULL,
    lookup_name_en character varying(255) NOT NULL,
    lookup_type character varying(255) NOT NULL,
    lookup_value text NOT NULL,
    attr1 text,
    attr2 text,
    attr3 text,
    attr4 text,
    attr5 text,
    attr6 text,
    attr7 text,
    attr8 text,
    attr9 text,
    attr10 text,
    status integer NOT NULL,
    description character varying,
    create_at timestamp(0) without time zone DEFAULT now(),
    create_by character varying(100) NOT NULL,
    update_at timestamp(0) without time zone,
    update_by character varying(100),
    flag_edit boolean,
    flag_create boolean
);


ALTER TABLE public.t_sys_lookup OWNER TO digitalshelf;

--
-- TOC entry 4004 (class 0 OID 0)
-- Dependencies: 211
-- Name: COLUMN t_sys_lookup.attr1; Type: COMMENT; Schema: public; Owner: digitalshelf
--

COMMENT ON COLUMN public.t_sys_lookup.attr1 IS 'Step ก่อนหน้าจะได้ สถานะนี้';


--
-- TOC entry 212 (class 1259 OID 16512)
-- Name: t_sys_oper_log; Type: TABLE; Schema: public; Owner: digitalshelf
--

CREATE TABLE public.t_sys_oper_log (
    uuid character varying(128) NOT NULL,
    trn_id character varying(200) NOT NULL,
    source character varying(255) NOT NULL,
    product_id character varying(128) NOT NULL,
    product_version_id character varying(128) NOT NULL,
    product_component_id character varying,
    task_category character varying(128),
    keywords character varying(255),
    trn_status integer,
    trn_sub_status integer,
    failure_reason text,
    source_device character varying(255),
    source_device_id character varying(100),
    source_cif_id character varying(100),
    account_name character varying(100),
    attr1 text,
    attr2 text,
    attr3 text,
    attr4 text,
    attr5 text,
    attr6 text,
    attr7 text,
    attr8 text,
    attr9 text,
    attr10 text,
    status integer NOT NULL,
    create_at timestamp(0) without time zone DEFAULT now(),
    create_by character varying(100),
    state_code character varying(128),
    state_time bigint
);


ALTER TABLE public.t_sys_oper_log OWNER TO digitalshelf;

--
-- TOC entry 4005 (class 0 OID 0)
-- Dependencies: 212
-- Name: COLUMN t_sys_oper_log.uuid; Type: COMMENT; Schema: public; Owner: digitalshelf
--

COMMENT ON COLUMN public.t_sys_oper_log.uuid IS 'Logging ID';


--
-- TOC entry 4006 (class 0 OID 0)
-- Dependencies: 212
-- Name: COLUMN t_sys_oper_log.trn_id; Type: COMMENT; Schema: public; Owner: digitalshelf
--

COMMENT ON COLUMN public.t_sys_oper_log.trn_id IS 'Product Transaction ID';


--
-- TOC entry 4007 (class 0 OID 0)
-- Dependencies: 212
-- Name: COLUMN t_sys_oper_log.source; Type: COMMENT; Schema: public; Owner: digitalshelf
--

COMMENT ON COLUMN public.t_sys_oper_log.source IS 'Source of Logging Action URL';


--
-- TOC entry 4008 (class 0 OID 0)
-- Dependencies: 212
-- Name: COLUMN t_sys_oper_log.product_id; Type: COMMENT; Schema: public; Owner: digitalshelf
--

COMMENT ON COLUMN public.t_sys_oper_log.product_id IS 'product_uuid';


--
-- TOC entry 4009 (class 0 OID 0)
-- Dependencies: 212
-- Name: COLUMN t_sys_oper_log.product_version_id; Type: COMMENT; Schema: public; Owner: digitalshelf
--

COMMENT ON COLUMN public.t_sys_oper_log.product_version_id IS 'product_version_uuid';


--
-- TOC entry 4010 (class 0 OID 0)
-- Dependencies: 212
-- Name: COLUMN t_sys_oper_log.product_component_id; Type: COMMENT; Schema: public; Owner: digitalshelf
--

COMMENT ON COLUMN public.t_sys_oper_log.product_component_id IS 'product detail uuid';


--
-- TOC entry 4011 (class 0 OID 0)
-- Dependencies: 212
-- Name: COLUMN t_sys_oper_log.task_category; Type: COMMENT; Schema: public; Owner: digitalshelf
--

COMMENT ON COLUMN public.t_sys_oper_log.task_category IS 'task of action';


--
-- TOC entry 4012 (class 0 OID 0)
-- Dependencies: 212
-- Name: COLUMN t_sys_oper_log.keywords; Type: COMMENT; Schema: public; Owner: digitalshelf
--

COMMENT ON COLUMN public.t_sys_oper_log.keywords IS 'Keywords for searching';


--
-- TOC entry 4013 (class 0 OID 0)
-- Dependencies: 212
-- Name: COLUMN t_sys_oper_log.trn_status; Type: COMMENT; Schema: public; Owner: digitalshelf
--

COMMENT ON COLUMN public.t_sys_oper_log.trn_status IS 'Transaction Status Code e.g. Pass, Fail';


--
-- TOC entry 4014 (class 0 OID 0)
-- Dependencies: 212
-- Name: COLUMN t_sys_oper_log.trn_sub_status; Type: COMMENT; Schema: public; Owner: digitalshelf
--

COMMENT ON COLUMN public.t_sys_oper_log.trn_sub_status IS 'Sub Status of Transaction Code e.g. Timeout, Success, Validation Fail, Response Code';


--
-- TOC entry 4015 (class 0 OID 0)
-- Dependencies: 212
-- Name: COLUMN t_sys_oper_log.failure_reason; Type: COMMENT; Schema: public; Owner: digitalshelf
--

COMMENT ON COLUMN public.t_sys_oper_log.failure_reason IS 'The explaination of failure';


--
-- TOC entry 4016 (class 0 OID 0)
-- Dependencies: 212
-- Name: COLUMN t_sys_oper_log.source_device; Type: COMMENT; Schema: public; Owner: digitalshelf
--

COMMENT ON COLUMN public.t_sys_oper_log.source_device IS 'Device name of Doer';


--
-- TOC entry 4017 (class 0 OID 0)
-- Dependencies: 212
-- Name: COLUMN t_sys_oper_log.source_device_id; Type: COMMENT; Schema: public; Owner: digitalshelf
--

COMMENT ON COLUMN public.t_sys_oper_log.source_device_id IS 'Source of Device ID';


--
-- TOC entry 4018 (class 0 OID 0)
-- Dependencies: 212
-- Name: COLUMN t_sys_oper_log.source_cif_id; Type: COMMENT; Schema: public; Owner: digitalshelf
--

COMMENT ON COLUMN public.t_sys_oper_log.source_cif_id IS 'Source Customer Info ID';


--
-- TOC entry 4019 (class 0 OID 0)
-- Dependencies: 212
-- Name: COLUMN t_sys_oper_log.account_name; Type: COMMENT; Schema: public; Owner: digitalshelf
--

COMMENT ON COLUMN public.t_sys_oper_log.account_name IS 'Computer login name of Doer';


--
-- TOC entry 213 (class 1259 OID 16519)
-- Name: t_sys_role; Type: TABLE; Schema: public; Owner: digitalshelf
--

CREATE TABLE public.t_sys_role (
    uuid character varying(128) NOT NULL,
    role_id character varying(256),
    role_code character varying(256),
    role_name_th character varying(256),
    role_name_en character varying(256),
    pemission text,
    attr1 text,
    attr2 text,
    attr3 text,
    attr4 text,
    attr5 text,
    attr6 text,
    attr7 text,
    attr8 text,
    attr9 text,
    attr10 text,
    status integer,
    create_at timestamp without time zone NOT NULL,
    create_by character varying(256) NOT NULL
);


ALTER TABLE public.t_sys_role OWNER TO digitalshelf;

--
-- TOC entry 3770 (class 2604 OID 16525)
-- Name: t_shelf_comp seq_no; Type: DEFAULT; Schema: public; Owner: digitalshelf
--

ALTER TABLE ONLY public.t_shelf_comp ALTER COLUMN seq_no SET DEFAULT nextval('public.t_shelf_comp_seq_no_seq'::regclass);


--
-- TOC entry 3957 (class 0 OID 16404)
-- Dependencies: 196
-- Data for Name: t_product_maxseq; Type: TABLE DATA; Schema: public; Owner: digitalshelf
--

COPY public.t_product_maxseq (product_number, doc_date, prod_code, prod_uuid, attribute1, attribute2, attribute3, attribute4, attribute5, attribute6, attribute7, attribute8, attribute9, attribute10, created_by, created_at, updated_by, updated_at) FROM stdin;
TOP-2020000001	2020-02-07	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-07 11:14:56.3785	\N	\N
TOP-2020000002	2020-02-07	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-07 12:23:17.35	\N	\N
TOP-2020000003	2020-02-07	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-07 12:23:23.4398	\N	\N
TOP-2020000004	2020-02-07	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-07 12:23:27.7201	\N	\N
TOP-2020000005	2020-02-07	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-07 12:26:17.9597	\N	\N
TOP-2020000006	2020-02-07	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-07 12:26:57.4112	\N	\N
TOP-2020000007	2020-02-07	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-07 12:28:05.8888	\N	\N
TOP-2020000008	2020-02-07	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-07 12:28:22.5002	\N	\N
TOP-2020000009	2020-02-07	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-07 12:28:28.5141	\N	\N
TOP-2020000010	2020-02-07	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-07 12:29:09.8572	\N	\N
TOP-2020000011	2020-02-07	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-07 12:29:52.0259	\N	\N
TOP-2020000012	2020-02-07	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-07 12:30:20.755	\N	\N
TOP-2020000013	2020-02-07	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-07 12:32:25.3505	\N	\N
TOP-2020000014	2020-02-07	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-07 14:19:00.3877	\N	\N
TOP-2020000015	2020-02-07	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-07 14:25:20.9959	\N	\N
TOP-2020000016	2020-02-07	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-07 14:25:27.9256	\N	\N
TOP-2020000017	2020-02-07	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-07 14:28:17.5127	\N	\N
TOP-2020000018	2020-02-07	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-07 14:35:13.6436	\N	\N
TOP-2020000019	2020-02-07	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-07 15:21:11.4469	\N	\N
TOP-2020000020	2020-02-07	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-07 15:29:06.5791	\N	\N
TOP-2020000021	2020-02-07	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-07 15:35:08.0509	\N	\N
TOP-2020000022	2020-02-11	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-11 13:55:03.9891	\N	\N
TOP-2020000023	2020-02-11	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-11 13:56:43.804	\N	\N
TOP-2020000024	2020-02-11	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-11 13:57:23.5035	\N	\N
TOP-2020000025	2020-02-11	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-11 13:58:42.5664	\N	\N
TOP-2020000026	2020-02-11	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-11 14:04:04.4424	\N	\N
TOP-2020000027	2020-02-11	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-11 14:06:20.5585	\N	\N
TOP-2020000028	2020-02-11	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-11 14:08:55.163	\N	\N
TOP-2020000029	2020-02-11	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-11 14:51:30.5374	\N	\N
TOP-2020000030	2020-02-11	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-11 14:52:57.488	\N	\N
TOP-2020000031	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 11:25:48.8515	\N	\N
TOP-2020000032	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 11:25:56.4453	\N	\N
TOP-2020000033	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 11:29:08.7431	\N	\N
TOP-2020000034	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 11:29:26.8489	\N	\N
TOP-2020000035	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 12:22:10.5427	\N	\N
TOP-2020000036	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 12:22:56.8533	\N	\N
TOP-2020000037	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 12:24:38.2628	\N	\N
TOP-2020000038	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 12:24:45.2123	\N	\N
TOP-2020000039	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 12:26:09.4575	\N	\N
TOP-2020000040	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 12:26:16.6747	\N	\N
TOP-2020000041	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 12:26:26.5582	\N	\N
TOP-2020000042	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 12:26:45.5539	\N	\N
TOP-2020000043	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 12:27:38.7777	\N	\N
TOP-2020000044	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 12:28:25.595	\N	\N
TOP-2020000045	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 12:29:42.2496	\N	\N
TOP-2020000046	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 12:45:03.7385	\N	\N
TOP-2020000047	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 12:45:12.9734	\N	\N
TOP-2020000048	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 13:10:54.1834	\N	\N
TOP-2020000049	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 13:59:24.6532	\N	\N
TOP-2020000050	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 14:20:59.9017	\N	\N
TOP-2020000051	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 14:31:09.8096	\N	\N
TOP-2020000052	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 14:58:02.3203	\N	\N
TOP-2020000053	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 15:02:03.2522	\N	\N
TOP-2020000054	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 15:04:24.3658	\N	\N
TOP-2020000055	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 15:09:12.2632	\N	\N
TOP-2020000056	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 15:09:21.0328	\N	\N
TOP-2020000057	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 15:09:26.1846	\N	\N
TOP-2020000058	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 15:09:41.0464	\N	\N
TOP-2020000059	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 15:09:45.3703	\N	\N
TOP-2020000060	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 15:10:47.1318	\N	\N
TOP-2020000061	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 15:10:50.8153	\N	\N
TOP-2020000062	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 15:11:32.7818	\N	\N
TOP-2020000063	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 15:17:09.4923	\N	\N
TOP-2020000064	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 15:18:54.3085	\N	\N
TOP-2020000065	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 15:25:43.9597	\N	\N
TOP-2020000066	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 15:41:44.8222	\N	\N
TOP-2020000067	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 15:41:51.4276	\N	\N
TOP-2020000069	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 16:22:15.8004	\N	\N
TOP-2020000070	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 16:23:19.1136	\N	\N
TOP-2020000071	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 16:23:29.4912	\N	\N
TOP-2020000072	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 16:25:16.6225	\N	\N
TOP-2020000073	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 16:26:18.6599	\N	\N
TOP-2020000074	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 16:26:23.0121	\N	\N
TOP-2020000075	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 16:26:27.1245	\N	\N
TOP-2020000076	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 16:26:37.8621	\N	\N
TOP-2020000077	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 16:26:46.3354	\N	\N
TOP-2020000078	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 16:26:51.1419	\N	\N
TOP-2020000079	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 16:27:01.9168	\N	\N
TOP-2020000080	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 16:27:53.5623	\N	\N
TOP-2020000081	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 16:28:31.3477	\N	\N
TOP-2020000082	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 16:29:01.6039	\N	\N
TOP-2020000083	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 16:29:34.0976	\N	\N
TOP-2020000084	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 16:29:54.107	\N	\N
TOP-2020000085	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 16:44:56.019	\N	\N
TOP-2020000086	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 16:48:43.1792	\N	\N
TOP-2020000087	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 16:49:06.9011	\N	\N
TOP-2020000088	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 16:49:14.4804	\N	\N
TOP-2020000089	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 16:55:02.7451	\N	\N
TOP-2020000090	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 16:55:09.5958	\N	\N
TOP-2020000091	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 16:58:05.2938	\N	\N
TOP-2020000092	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 16:58:14.8138	\N	\N
TOP-2020000093	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 16:58:25.5952	\N	\N
TOP-2020000094	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 16:58:34.6301	\N	\N
TOP-2020000095	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 16:59:46.7406	\N	\N
TOP-2020000096	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 17:00:33.0758	\N	\N
TOP-2020000097	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 17:00:37.8219	\N	\N
TOP-2020000098	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 17:00:42.3668	\N	\N
TOP-2020000099	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 17:00:48.593	\N	\N
TOP-2020000100	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 17:01:05.9801	\N	\N
TOP-2020000101	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 17:02:02.403	\N	\N
TOP-2020000102	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 17:03:56.4222	\N	\N
TOP-2020000068	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 15:44:36.2975	\N	\N
TOP-2020000103	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 17:04:59.3142	\N	\N
TOP-2020000104	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 17:05:29.898	\N	\N
TOP-2020000105	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 17:06:17.6792	\N	\N
TOP-2020000106	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 17:06:24.5046	\N	\N
TOP-2020000107	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 17:07:33.4987	\N	\N
TOP-2020000108	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 17:08:02.1626	\N	\N
TOP-2020000109	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 17:08:27.998	\N	\N
TOP-2020000110	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 17:08:37.0626	\N	\N
TOP-2020000111	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 17:10:04.6496	\N	\N
TOP-2020000112	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 17:10:46.4115	\N	\N
TOP-2020000113	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 17:10:55.9513	\N	\N
TOP-2020000114	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 17:11:35.3273	\N	\N
TOP-2020000115	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 17:11:46.269	\N	\N
TOP-2020000116	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 17:11:48.5184	\N	\N
TOP-2020000117	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 17:15:51.7638	\N	\N
TOP-2020000118	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 17:19:59.3819	\N	\N
TOP-2020000119	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 17:20:23.4874	\N	\N
TOP-2020000120	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 17:22:08.7762	\N	\N
TOP-2020000121	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 17:22:50.3078	\N	\N
TOP-2020000122	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 17:23:10.5064	\N	\N
TOP-2020000123	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 17:23:19.7223	\N	\N
TOP-2020000124	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 17:23:25.8946	\N	\N
TOP-2020000125	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 17:23:46.8826	\N	\N
TOP-2020000126	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 17:24:07.4619	\N	\N
TOP-2020000127	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 17:24:33.616	\N	\N
TOP-2020000128	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 17:24:39.699	\N	\N
TOP-2020000129	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 17:24:57.4746	\N	\N
TOP-2020000130	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 17:29:04.8971	\N	\N
TOP-2020000131	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 17:31:23.0668	\N	\N
TOP-2020000132	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 17:31:25.7099	\N	\N
TOP-2020000133	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 17:31:32.2658	\N	\N
TOP-2020000134	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 17:33:03.701	\N	\N
TOP-2020000135	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 17:33:06.0971	\N	\N
TOP-2020000136	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 17:33:18.4689	\N	\N
TOP-2020000137	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 17:43:10.8826	\N	\N
TOP-2020000138	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 17:45:36.6391	\N	\N
TOP-2020000139	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 17:46:23.9447	\N	\N
TOP-2020000140	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 17:46:52.9908	\N	\N
TOP-2020000141	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 17:47:19.8476	\N	\N
TOP-2020000142	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 17:56:24.5649	\N	\N
TOP-2020000143	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 18:49:57.9195	\N	\N
TOP-2020000144	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 18:50:04.9785	\N	\N
TOP-2020000145	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 19:08:24.4261	\N	\N
TOP-2020000146	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 19:18:25.0146	\N	\N
TOP-2020000147	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 19:19:33.0939	\N	\N
TOP-2020000148	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 19:19:52.3836	\N	\N
TOP-2020000149	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 19:38:56.119	\N	\N
TOP-2020000150	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 19:43:33.2201	\N	\N
TOP-2020000151	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 19:50:06.1409	\N	\N
TOP-2020000152	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 21:40:07.6678	\N	\N
TOP-2020000153	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 21:55:54.0711	\N	\N
TOP-2020000154	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 21:56:10.9023	\N	\N
TOP-2020000155	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 22:09:00.9394	\N	\N
TOP-2020000156	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 22:15:00.531	\N	\N
TOP-2020000157	2020-02-12	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-12 22:54:43.0187	\N	\N
TOP-2020000158	2020-02-13	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 09:04:23.6922	\N	\N
TOP-2020000159	2020-02-13	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 09:04:38.5336	\N	\N
TOP-2020000160	2020-02-13	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 09:09:12.9122	\N	\N
TOP-2020000161	2020-02-13	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 10:05:15.1726	\N	\N
TOP-2020000162	2020-02-13	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 10:05:35.6632	\N	\N
TOP-2020000163	2020-02-13	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 10:05:38.3051	\N	\N
TOP-2020000164	2020-02-13	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 10:06:09.8734	\N	\N
TOP-2020000165	2020-02-13	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 10:06:42.4573	\N	\N
TOP-2020000166	2020-02-13	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 10:07:18.0488	\N	\N
TOP-2020000167	2020-02-13	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 10:07:37.7949	\N	\N
TOP-2020000168	2020-02-13	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 10:07:39.7917	\N	\N
TOP-2020000169	2020-02-13	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 10:08:08.2083	\N	\N
TOP-2020000170	2020-02-13	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 10:08:49.1343	\N	\N
TOP-2020000171	2020-02-13	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 10:11:45.7151	\N	\N
TOP-2020000172	2020-02-13	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 10:16:06.1665	\N	\N
TOP-2020000173	2020-02-13	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 10:32:06.6756	\N	\N
TOP-2020000174	2020-02-13	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 10:33:00.291	\N	\N
TOP-2020000175	2020-02-13	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 10:39:31.7435	\N	\N
TOP-2020000176	2020-02-13	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 10:43:16.824	\N	\N
TOP-2020000177	2020-02-13	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 10:43:58.968	\N	\N
TOP-2020000178	2020-02-13	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 10:44:35.2582	\N	\N
TOP-2020000179	2020-02-13	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 10:45:45.0099	\N	\N
TOP-2020000180	2020-02-13	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 11:01:01.6289	\N	\N
TOP-2020000181	2020-02-13	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 11:13:29.9234	\N	\N
TOP-2020000182	2020-02-13	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 11:15:40.639	\N	\N
TOP-2020000183	2020-02-13	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 11:17:43.4151	\N	\N
TOP-2020000184	2020-02-13	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 11:17:49.5385	\N	\N
TOP-2020000185	2020-02-13	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 11:29:04.931	\N	\N
TOP-2020000186	2020-02-13	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 11:32:02.6183	\N	\N
TOP-2020000187	2020-02-13	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 11:36:52.8767	\N	\N
TOP-2020000188	2020-02-13	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 11:37:13.0864	\N	\N
TOP-2020000189	2020-02-13	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 11:46:41.7618	\N	\N
TOP-2020000190	2020-02-13	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 11:54:32.0595	\N	\N
TOP-2020000191	2020-02-13	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 11:54:56.719	\N	\N
TOP-2020000192	2020-02-13	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 12:05:54.4037	\N	\N
TOP-2020000193	2020-02-13	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 12:07:29.4692	\N	\N
TOP-2020000194	2020-02-13	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 12:25:28.109	\N	\N
TOP-2020000195	2020-02-13	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 12:34:57.2786	\N	\N
TOP-2020000196	2020-02-13	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 12:35:40.5793	\N	\N
TOP-2020000197	2020-02-13	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 12:37:49.8343	\N	\N
TOP-2020000198	2020-02-13	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 12:38:15.2062	\N	\N
TOP-2020000199	2020-02-13	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 12:40:42.7125	\N	\N
TOP-2020000200	2020-02-13	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 12:49:21.3365	\N	\N
TOP-2020000201	2020-02-13	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 12:53:27.2495	\N	\N
TOP-2020000202	2020-02-13	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 12:54:17.5555	\N	\N
TOP-2020000203	2020-02-13	TOP	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 12:57:43.0187	\N	\N
TOPUPEASY-2020000001	2020-02-13	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 14:57:43.4588	\N	\N
TOPUPEASY-2020000002	2020-02-13	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 17:20:30.8086	\N	\N
TOPUPEASY-2020000003	2020-02-13	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 17:20:38.2361	\N	\N
TOPUPEASY-2020000004	2020-02-13	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 17:21:32.2223	\N	\N
TOPUPEASY-2020000005	2020-02-13	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 17:25:21.4887	\N	\N
TOPUPEASY-2020000006	2020-02-13	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 17:26:24.775	\N	\N
TOPUPEASY-2020000007	2020-02-13	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 17:28:22.7321	\N	\N
TOPUPEASY-2020000008	2020-02-13	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 17:32:46.2451	\N	\N
TOPUPEASY-2020000009	2020-02-13	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 17:36:46.8666	\N	\N
TOPUPEASY-2020000010	2020-02-13	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 17:40:11.0674	\N	\N
TOPUPEASY-2020000011	2020-02-13	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 17:56:58.4237	\N	\N
TOPUPEASY-2020000012	2020-02-13	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 18:01:15.493	\N	\N
TOPUPEASY-2020000013	2020-02-13	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 18:01:52.3551	\N	\N
TOPUPEASY-2020000014	2020-02-13	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 18:08:51.2617	\N	\N
TOPUPEASY-2020000015	2020-02-13	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 18:43:24.8236	\N	\N
TOPUPEASY-2020000016	2020-02-13	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 18:44:30.4901	\N	\N
TOPUPEASY-2020000017	2020-02-13	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 19:20:35.1007	\N	\N
TOPUPEASY-2020000018	2020-02-13	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 19:46:20.5077	\N	\N
TOPUPEASY-2020000019	2020-02-13	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 19:47:21.0041	\N	\N
TOPUPEASY-2020000020	2020-02-13	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 20:14:38.4003	\N	\N
TOPUPEASY-2020000021	2020-02-13	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 20:15:20.7139	\N	\N
TOPUPEASY-2020000022	2020-02-13	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 20:19:20.2971	\N	\N
TOPUPEASY-2020000023	2020-02-13	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 20:20:47.4671	\N	\N
TOPUPEASY-2020000024	2020-02-13	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 20:36:57.0339	\N	\N
TOPUPEASY-2020000025	2020-02-13	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 22:36:06.3963	\N	\N
TOPUPEASY-2020000026	2020-02-13	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 22:39:46.0534	\N	\N
TOPUPEASY-2020000027	2020-02-13	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 23:09:40.5	\N	\N
TOPUPEASY-2020000028	2020-02-13	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 23:43:14.603	\N	\N
TOPUPEASY-2020000029	2020-02-13	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-13 23:53:37.6507	\N	\N
TOPUPEASY-2020000030	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 00:14:55.6573	\N	\N
TOPUPEASY-2020000031	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 09:37:23.7535	\N	\N
TOPUPEASY-2020000032	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 09:38:04.9772	\N	\N
TOPUPEASY-2020000033	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 10:05:48.4752	\N	\N
TOPUPEASY-2020000034	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 10:17:14.9963	\N	\N
TOPUPEASY-2020000035	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 10:18:49.2554	\N	\N
TOPUPEASY-2020000036	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 10:19:38.1036	\N	\N
TOPUPEASY-2020000037	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 10:31:39.1522	\N	\N
TOPUPEASY-2020000038	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 10:56:35.382	\N	\N
TOPUPEASY-2020000039	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 11:02:46.1306	\N	\N
TOPUPEASY-2020000040	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 11:13:09.6778	\N	\N
TOPUPEASY-2020000041	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 11:15:48.4908	\N	\N
TOPUPEASY-2020000042	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 11:16:03.5603	\N	\N
TOPUPEASY-2020000043	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 11:16:15.6209	\N	\N
TOPUPEASY-2020000044	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 11:16:34.9762	\N	\N
TOPUPEASY-2020000045	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 11:17:00.5004	\N	\N
TOPUPEASY-2020000046	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 11:25:41.5733	\N	\N
TOPUPEASY-2020000047	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 11:26:05.4417	\N	\N
TOPUPEASY-2020000048	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 11:26:45.0595	\N	\N
TOPUPEASY-2020000049	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 11:29:11.4498	\N	\N
TOPUPEASY-2020000050	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 11:36:43.0075	\N	\N
TOPUPEASY-2020000051	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 11:39:29.2348	\N	\N
TOPUPEASY-2020000052	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 11:42:34.5056	\N	\N
TOPUPEASY-2020000053	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 11:46:05.9794	\N	\N
TOPUPEASY-2020000054	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 11:52:07.9599	\N	\N
TOPUPEASY-2020000055	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 11:55:19.5162	\N	\N
TOPUPEASY-2020000056	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 11:55:53.8721	\N	\N
TOPUPEASY-2020000057	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 12:06:38.6335	\N	\N
TOPUPEASY-2020000058	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 12:07:31.884	\N	\N
TOPUPEASY-2020000059	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 12:10:50.3363	\N	\N
TOPUPEASY-2020000060	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 13:48:00.1986	\N	\N
TOPUPEASY-2020000061	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 13:55:45.1491	\N	\N
TOPUPEASY-2020000062	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 13:58:03.9657	\N	\N
TOPUPEASY-2020000063	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 14:21:19.0564	\N	\N
TOPUPEASY-2020000064	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 14:52:24.636	\N	\N
TOPUPEASY-2020000065	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 14:54:04.478	\N	\N
TOPUPEASY-2020000066	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 14:55:47.0017	\N	\N
TOPUPEASY-2020000067	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 14:56:28.7157	\N	\N
TOPUPEASY-2020000068	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 15:01:32.9816	\N	\N
TOPUPEASY-2020000069	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 15:06:09.7976	\N	\N
TOPUPEASY-2020000070	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 15:07:53.5403	\N	\N
TOPUPEASY-2020000071	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 15:08:27.8406	\N	\N
TOPUPEASY-2020000072	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 15:08:49.332	\N	\N
TOPUPEASY-2020000073	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 15:11:27.442	\N	\N
TOPUPEASY-2020000074	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 15:28:50.9478	\N	\N
TOPUPEASY-2020000075	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 15:52:12.1668	\N	\N
TOPUPEASY-2020000076	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 16:01:17.0195	\N	\N
TOPUPEASY-2020000077	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 16:12:09.3473	\N	\N
TOPUPEASY-2020000078	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 16:15:39.9958	\N	\N
TOPUPEASY-2020000079	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 16:19:31.595	\N	\N
TOPUPEASY-2020000080	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 16:24:39.4495	\N	\N
TOPUPEASY-2020000081	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 16:28:07.2226	\N	\N
TOPUPEASY-2020000082	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 16:42:11.0824	\N	\N
TOPUPEASY-2020000083	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 17:14:41.5236	\N	\N
TOPUPEASY-2020000084	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 19:42:41.2706	\N	\N
TOPUPEASY-2020000085	2020-02-14	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-14 21:56:58.1606	\N	\N
TOPUPEASY-2020000086	2020-02-15	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-15 15:51:11.2831	\N	\N
TOPUPEASY-2020000087	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 10:14:45.6158	\N	\N
TOPUPEASY-2020000088	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 10:15:12.0915	\N	\N
TOPUPEASY-2020000089	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 10:17:07.9018	\N	\N
TOPUPEASY-2020000090	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 10:17:50.3176	\N	\N
TOPUPEASY-2020000091	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 10:19:09.3674	\N	\N
TOPUPEASY-2020000092	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 10:20:16.8108	\N	\N
TOPUPEASY-2020000093	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 10:21:00.8192	\N	\N
TOPUPEASY-2020000094	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 10:21:11.0117	\N	\N
TOPUPEASY-2020000095	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 10:22:03.2638	\N	\N
TOPUPEASY-2020000096	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 10:22:12.1575	\N	\N
TOPUPEASY-2020000097	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 10:24:46.6515	\N	\N
TOPUPEASY-2020000098	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 10:31:41.3039	\N	\N
TOPUPEASY-2020000099	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 10:32:08.893	\N	\N
TOPUPEASY-2020000100	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 10:32:19.1427	\N	\N
TOPUPEASY-2020000101	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 10:38:17.9533	\N	\N
TOPUPEASY-2020000102	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 10:44:20.1975	\N	\N
TOPUPEASY-2020000103	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 11:20:22.8241	\N	\N
TOPUPEASY-2020000104	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 11:20:35.0361	\N	\N
TOPUPEASY-2020000105	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 11:20:43.2904	\N	\N
TOPUPEASY-2020000106	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 11:20:47.8411	\N	\N
TOPUPEASY-2020000107	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 11:20:55.6397	\N	\N
TOPUPEASY-2020000108	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 11:20:58.0656	\N	\N
TOPUPEASY-2020000109	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 11:21:01.9202	\N	\N
TOPUPEASY-2020000110	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 11:25:01.4592	\N	\N
TOPUPEASY-2020000111	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 11:25:17.0561	\N	\N
TOPUPEASY-2020000112	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 11:38:04.0927	\N	\N
TOPUPEASY-2020000113	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 11:39:55.4865	\N	\N
TOPUPEASY-2020000114	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 11:40:18.2984	\N	\N
TOPUPEASY-2020000115	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 11:43:10.2297	\N	\N
TOPUPEASY-2020000116	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 13:31:32.5729	\N	\N
TOPUPEASY-2020000117	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 13:33:31.2116	\N	\N
TOPUPEASY-2020000118	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 13:34:44.0284	\N	\N
TOPUPEASY-2020000119	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 13:34:53.8565	\N	\N
TOPUPEASY-2020000120	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 13:35:22.2298	\N	\N
TOPUPEASY-2020000121	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 13:37:10.6343	\N	\N
TOPUPEASY-2020000122	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 13:46:29.7306	\N	\N
TOPUPEASY-2020000123	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 14:10:00.1026	\N	\N
TOPUPEASY-2020000124	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 14:14:20.1246	\N	\N
TOPUPEASY-2020000125	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 14:21:12.3762	\N	\N
TOPUPEASY-2020000126	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 14:23:51.0122	\N	\N
TOPUPEASY-2020000127	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 14:30:50.7602	\N	\N
TOPUPEASY-2020000128	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 14:33:04.9104	\N	\N
TOPUPEASY-2020000129	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 14:47:36.7218	\N	\N
TOPUPEASY-2020000130	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 14:48:58.6233	\N	\N
TOPUPEASY-2020000131	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 14:50:44.6847	\N	\N
TOPUPEASY-2020000132	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 14:53:15.4795	\N	\N
TOPUPEASY-2020000133	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 14:53:40.481	\N	\N
TOPUPEASY-2020000134	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 14:59:46.7659	\N	\N
TOPUPEASY-2020000135	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 15:05:11.3198	\N	\N
TOPUPEASY-2020000136	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 15:07:32.0137	\N	\N
TOPUPEASY-2020000137	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 15:07:59.4176	\N	\N
TOPUPEASY-2020000138	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 15:09:51.0441	\N	\N
TOPUPEASY-2020000139	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 17:36:30.9477	\N	\N
TOPUPEASY-2020000140	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 17:39:40.2182	\N	\N
TOPUPEASY-2020000141	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 17:41:32.7776	\N	\N
TOPUPEASY-2020000142	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 17:41:37.983	\N	\N
TOPUPEASY-2020000143	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 17:47:08.5447	\N	\N
TOPUPEASY-2020000144	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 17:47:15.3527	\N	\N
TOPUPEASY-2020000145	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 17:47:59.4649	\N	\N
TOPUPEASY-2020000146	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 17:49:00.5948	\N	\N
TOPUPEASY-2020000147	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 18:01:59.9064	\N	\N
TOPUPEASY-2020000148	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 18:15:05.5439	\N	\N
TOPUPEASY-2020000149	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 18:21:01.764	\N	\N
TOPUPEASY-2020000150	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 19:01:33.0735	\N	\N
TOPUPEASY-2020000151	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 19:15:14.7122	\N	\N
TOPUPEASY-2020000152	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 19:15:33.8403	\N	\N
TOPUPEASY-2020000153	2020-02-17	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-17 19:20:18.6857	\N	\N
TOPUPEASY-2020000154	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 08:58:00.1588	\N	\N
TOPUPEASY-2020000155	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 09:27:11.5012	\N	\N
TOPUPEASY-2020000156	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 09:38:55.5158	\N	\N
TOPUPEASY-2020000157	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 09:41:27.285	\N	\N
TOPUPEASY-2020000158	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 10:42:20.1333	\N	\N
TOPUPEASY-2020000159	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 10:43:34.5611	\N	\N
TOPUPEASY-2020000160	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 10:54:54.1645	\N	\N
TOPUPEASY-2020000161	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 10:55:34.0605	\N	\N
TOPUPEASY-2020000162	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 10:55:57.1996	\N	\N
TOPUPEASY-2020000163	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 10:58:38.8564	\N	\N
TOPUPEASY-2020000164	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 11:06:28.4416	\N	\N
TOPUPEASY-2020000165	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 11:08:57.4335	\N	\N
TOPUPEASY-2020000166	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 11:09:20.8009	\N	\N
TOPUPEASY-2020000167	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 11:17:11.9891	\N	\N
TOPUPEASY-2020000168	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 11:19:05.0419	\N	\N
TOPUPEASY-2020000169	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 11:19:12.0545	\N	\N
TOPUPEASY-2020000170	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 11:22:37.7265	\N	\N
TOPUPEASY-2020000171	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 11:24:49.0716	\N	\N
TOPUPEASY-2020000172	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 11:25:54.3202	\N	\N
TOPUPEASY-2020000173	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 11:26:15.6761	\N	\N
TOPUPEASY-2020000174	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 11:35:20.5687	\N	\N
TOPUPEASY-2020000175	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 11:44:16.0337	\N	\N
TOPUPEASY-2020000176	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 11:46:50.7707	\N	\N
TOPUPEASY-2020000177	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 12:26:15.9932	\N	\N
TOPUPEASY-2020000178	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 12:27:43.1895	\N	\N
TOPUPEASY-2020000179	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 12:29:57.047	\N	\N
TOPUPEASY-2020000180	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 12:37:32.6945	\N	\N
TOPUPEASY-2020000181	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 12:44:13.8385	\N	\N
TOPUPEASY-2020000182	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 12:44:18.7085	\N	\N
TOPUPEASY-2020000183	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 12:44:23.0333	\N	\N
TOPUPEASY-2020000184	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 12:48:41.1138	\N	\N
TOPUPEASY-2020000185	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 13:21:59.1873	\N	\N
TOPUPEASY-2020000186	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 13:23:36.9789	\N	\N
TOPUPEASY-2020000187	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 13:27:47.9231	\N	\N
TOPUPEASY-2020000188	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 13:29:47.9844	\N	\N
TOPUPEASY-2020000189	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 13:30:03.3549	\N	\N
TOPUPEASY-2020000190	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 13:31:27.6994	\N	\N
TOPUPEASY-2020000191	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 13:38:24.9351	\N	\N
TOPUPEASY-2020000192	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 13:39:26.3463	\N	\N
TOPUPEASY-2020000193	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 13:39:49.0038	\N	\N
TOPUPEASY-2020000194	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 13:40:09.458	\N	\N
TOPUPEASY-2020000195	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 13:40:28.8819	\N	\N
TOPUPEASY-2020000196	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 13:40:58.3043	\N	\N
TOPUPEASY-2020000197	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 13:41:08.0467	\N	\N
TOPUPEASY-2020000198	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 13:41:14.2612	\N	\N
TOPUPEASY-2020000199	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 13:51:46.6199	\N	\N
TOPUPEASY-2020000200	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 14:37:20.0501	\N	\N
TOPUPEASY-2020000201	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 14:37:33.933	\N	\N
TOPUPEASY-2020000202	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 14:44:26.0401	\N	\N
TOPUPEASY-2020000203	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 14:48:11.6631	\N	\N
TOPUPEASY-2020000204	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 15:15:43.5227	\N	\N
TOPUPEASY-2020000205	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 15:45:27.6182	\N	\N
TOPUPEASY-2020000206	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 15:46:22.4119	\N	\N
TOPUPEASY-2020000207	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 16:01:18.2425	\N	\N
TOPUPEASY-2020000208	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 16:40:34.1868	\N	\N
TOPUPEASY-2020000209	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 16:52:21.0341	\N	\N
TOPUPEASY-2020000210	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 16:56:20.3056	\N	\N
TOPUPEASY-2020000211	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 17:06:51.5778	\N	\N
TOPUPEASY-2020000212	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 17:07:08.2156	\N	\N
TOPUPEASY-2020000213	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 17:23:43.6575	\N	\N
TOPUPEASY-2020000214	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 17:28:54.6623	\N	\N
TOPUPEASY-2020000215	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 17:30:20.8102	\N	\N
TOPUPEASY-2020000216	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 17:33:07.3566	\N	\N
TOPUPEASY-2020000217	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 17:34:54.1029	\N	\N
TOPUPEASY-2020000218	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 17:35:43.0148	\N	\N
TOPUPEASY-2020000219	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 17:35:52.1075	\N	\N
TOPUPEASY-2020000220	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 17:37:19.1527	\N	\N
TOPUPEASY-2020000221	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 17:37:28.1797	\N	\N
TOPUPEASY-2020000222	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 18:46:51.611	\N	\N
TOPUPEASY-2020000223	2020-02-18	TOPUPEASY	e0c80f8b-627e-48b4-9384-87d00dca5274	2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	Tor Alter	2020-02-18 20:07:39.9616	\N	\N
\.


--
-- TOC entry 3958 (class 0 OID 16411)
-- Dependencies: 197
-- Data for Name: t_shelf_comp; Type: TABLE DATA; Schema: public; Owner: digitalshelf
--

COPY public.t_shelf_comp (uuid, seq_no, comp_code, comp_name, value, attr1, attr2, attr3, attr4, attr5, attr6, attr7, attr8, attr9, attr10, description, status, create_at, create_by, update_at, update_by, pattern) FROM stdin;
723b3819-e1e0-4756-a6f2-50fdaf14d85d	9	009	PRODUCT INFO	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-02 09:16:56	tOr	\N	\N	\N
2787aafe-e4e8-4f09-a3b8-5838a7595dbe	10	010	AGREEMENT	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-02 09:17:24	tOr	\N	\N	\N
af0f3037-5d24-454f-b81e-ed33e0f5f334	1	001	SPLASH PAGE	\N	{\n\t"compUuid": "af0f3037-5d24-454f-b81e-ed33e0f5f334",\n\t"compName": "Splash Pages",\n\t"subComp": [\n\t\t{\n\t\t\t"subUuid": "23b2e944-0b08-4ecd-a318-ead86f7dcf3d",\n\t\t\t"subCompCode": "G001",\n\t\t\t"subCompLabel": "Splash Pages",\n\t\t\t"details": [{\n\t\t\t\t\t"id": "imgUpload",\n\t\t\t\t\t"code": "IN003",\n\t\t\t\t\t"label": "image",\n\t\t\t\t\t"value": "",\n\t\t\t\t\t"require": true,\n\t\t\t\t\t"validation": ["image/jpeg", "image/png"],\n\t\t\t\t\t"" : "",\n\t\t\t\t\t"description": "Support JPEG/PNG/JPG"\n\t\t\t\t}, {\n\t\t\t\t\t"id": "imageType",\n\t\t\t\t\t"code": "IN004",\n\t\t\t\t\t"label": "Type",\n\t\t\t\t\t"value": "",\n\t\t\t\t\t"require": true,\n\t\t\t\t\t"validation": [],\n\t\t\t\t\t"pattern" : "",\n\t\t\t\t\t"description": "",\n\t\t\t\t\t"data": [{\n\t\t\t\t\t\t\t"label": "Please Select",\n\t\t\t\t\t\t\t"value": ""\n\t\t\t\t\t\t}, {\n\t\t\t\t\t\t\t"label": "Website",\n\t\t\t\t\t\t\t"value": "w"\n\t\t\t\t\t\t}, {\n\t\t\t\t\t\t\t"label": "Mobile",\n\t\t\t\t\t\t\t"value": "m"\n\t\t\t\t\t\t}\n\t\t\t\t\t]\n\t\t\t\t}, {\n\t\t\t\t\t"id": "btnUpload",\n\t\t\t\t\t"code": "B001",\n\t\t\t\t\t"label": "Upload",\n\t\t\t\t\t"value": "",\n\t\t\t\t\t"require": true,\n\t\t\t\t\t"validation": [],\n\t\t\t\t\t"pattern" : "",\n\t\t\t\t\t"description": ""\n\t\t\t\t}, {\n\t\t\t\t\t"id": "imgSplashList",\n\t\t\t\t\t"code": "T001",\n\t\t\t\t\t"label": "",\n\t\t\t\t\t"value": "",\n\t\t\t\t\t"require": false,\n\t\t\t\t\t"validation": [],\n\t\t\t\t\t"pattern" : "",\n\t\t\t\t\t"description": "",\n\t\t\t\t\t"table": {\n\t\t\t\t\t\t"fileName": "File Name",\n\t\t\t\t\t\t"type": "Type",\n\t\t\t\t\t\t"delete": "Delete"\n\t\t\t\t\t},\n\t\t\t\t\t"data": [\n\t\t\t\t\t\t{\n\t\t\t\t\t\t\t"fileName": "xxxxx.png",\n\t\t\t\t\t\t\t"type": "Website",\n\t\t\t\t\t\t\t"delete": "Delete",\n\t\t\t\t\t\t\t"value": "base64"\n\t\t\t\t\t\t}, {\n\t\t\t\t\t\t\t"fileName": "xxxxx.png",\n\t\t\t\t\t\t\t"type": "Mobile",\n\t\t\t\t\t\t\t"delete": "Delete",\n\t\t\t\t\t\t\t"value": "base64"\n\t\t\t\t\t\t}\n\t\t\t\t\t]\n\t\t\t\t}, {\n\t\t\t\t\t"id": "btnText",\n\t\t\t\t\t"code": "IN001",\n\t\t\t\t\t"label": "Botton Text",\n\t\t\t\t\t"value": "",\n\t\t\t\t\t"require": true,\n\t\t\t\t\t"validation": [],\n\t\t\t\t\t"pattern" : "",\n\t\t\t\t\t"description": ""\n\t\t\t\t}, {\n\t\t\t\t\t"id": "activeDate",\n\t\t\t\t\t"code": "IN002",\n\t\t\t\t\t"label": "Active Date",\n\t\t\t\t\t"value": "",\n\t\t\t\t\t"require": true,\n\t\t\t\t\t"validation": [],\n\t\t\t\t\t"pattern" : "",\n\t\t\t\t\t"description": ""\n\t\t\t\t}\n\t\t\t]\n\t\t}, {\n\t\t\t"subUuid": "uuid",\n\t\t\t"subCompCode": "G008",\n\t\t\t"subCompLabel": "Version Control",\n\t\t\t"details": [\n\t\t\t\t{\n\t\t\t\t\t"id": "verSplashList",\n\t\t\t\t\t"code": "T001",\n\t\t\t\t\t"label": "",\n\t\t\t\t\t"value": "",\n\t\t\t\t\t"require": false,\n\t\t\t\t\t"validation": [],\n\t\t\t\t\t"pattern" : "",\n\t\t\t\t\t"description": "",\n\t\t\t\t\t"table": {\n\t\t\t\t\t\t"version": "Version",\n\t\t\t\t\t\t"createdDate": "Date",\n\t\t\t\t\t\t"activeDate": "Active Date",\n\t\t\t\t\t\t"status": "Status"\n\t\t\t\t\t},\n\t\t\t\t\t"data": [\n\t\t\t\t\t\t{\n\t\t\t\t\t\t\t"version": "1",\n\t\t\t\t\t\t\t"createdDate": "2020-01-01",\n\t\t\t\t\t\t\t"activeDate": "2020-01-01",\n\t\t\t\t\t\t\t"status": "Active",\n\t\t\t\t\t\t\t"uuid": "xxxxx"\n\t\t\t\t\t\t}, {\n\t\t\t\t\t\t\t"version": "2",\n\t\t\t\t\t\t\t"createdDate": "2019-12-23",\n\t\t\t\t\t\t\t"activeDate": "2020-01-01",\n\t\t\t\t\t\t\t"status": "InActive",\n\t\t\t\t\t\t\t"uuid": "xxxxx"\n\t\t\t\t\t\t}, {\n\t\t\t\t\t\t\t"version": "3",\n\t\t\t\t\t\t\t"createdDate": "2019-11-03",\n\t\t\t\t\t\t\t"activeDate": "2020-11-30",\n\t\t\t\t\t\t\t"status": "InActive",\n\t\t\t\t\t\t\t"uuid": "xxxxx"\n\t\t\t\t\t\t}\n\t\t\t\t\t]\n\t\t\t\t}\n\t\t\t]\n\t\t}\n\t]\n}	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-02 09:11:29	tOr	\N	\N	\N
169e986b-5a0a-45c9-8b08-890841b5b23f	3	003	PRODUCT SALE SHEET	\N	{\n\t"compUuid": "169e986b-5a0a-45c9-8b08-890841b5b23f",\n\t"compName": "Product Sale Sheet",\n\t"subComp": [{\n\t\t\t"subUuid": "23b2e944-0b08-4ecd-a318-ead86f7dcf3d",\n\t\t\t"subCompCode": "G001",\n\t\t\t"subCompLabel": "Product Sale Sheet",\n\t\t\t"details": [{\n\t\t\t\t\t"id": "headerText",\n\t\t\t\t\t"code": "IN001",\n\t\t\t\t\t"label": "Header Text",\n\t\t\t\t\t"value": "",\n\t\t\t\t\t"require": false,\n\t\t\t\t\t"validation": [],\n\t\t\t\t\t"description": ""\n\t\t\t\t}, {\n\t\t\t\t\t"id": "pdfUpload",\n\t\t\t\t\t"code": "IN003",\n\t\t\t\t\t"label": "Sale Sheet",\n\t\t\t\t\t"value": "",\n\t\t\t\t\t"require": true,\n\t\t\t\t\t"validation": "application/pdf",\n\t\t\t\t\t"pattern" : "",\n\t\t\t\t\t"description": "Support PDF, File size"\n\t\t\t\t}, {\n\t\t\t\t\t"id": "activeDate",\n\t\t\t\t\t"code": "IN002",\n\t\t\t\t\t"label": "Active Date",\n\t\t\t\t\t"value": "",\n\t\t\t\t\t"require": true,\n\t\t\t\t\t"validation": [],\n\t\t\t\t\t"pattern" : "",\n\t\t\t\t\t"description": ""\n\t\t\t\t}, {\n\t\t\t\t\t"subUuid": "uuid",\n\t\t\t\t\t"subCompCode": "G007",\n\t\t\t\t\t"subCompLabel": "Check Box Detail",\n\t\t\t\t\t"details": [{\n\t\t\t\t\t\t\t"id": "chkBoxLabel",\n\t\t\t\t\t\t\t"code": "IN001",\n\t\t\t\t\t\t\t"label": "Check Box Label",\n\t\t\t\t\t\t\t"value": "",\n\t\t\t\t\t\t\t"require": false,\n\t\t\t\t\t\t\t"validation": [],\n\t\t\t\t\t\t\t"pattern" : "",\n\t\t\t\t\t\t\t"description": ""\n\t\t\t\t\t\t}, {\n\t\t\t\t\t\t\t"id": "radioMandatory",\n\t\t\t\t\t\t\t"code": "IN009",\n\t\t\t\t\t\t\t"label": "Mandatory (Yes/No)",\n\t\t\t\t\t\t\t"value": "",\n\t\t\t\t\t\t\t"require": false,\n\t\t\t\t\t\t\t"validation": [],\n\t\t\t\t\t\t\t"pattern" : "",\n\t\t\t\t\t\t\t"description": "",\n\t\t\t\t\t\t\t"data": [{\n\t\t\t\t\t\t\t\t\t"label": "Yes",\n\t\t\t\t\t\t\t\t\t"value": "Y"\n\t\t\t\t\t\t\t\t}, {\n\t\t\t\t\t\t\t\t\t"label": "No",\n\t\t\t\t\t\t\t\t\t"value": "N"\n\t\t\t\t\t\t\t\t}, {\n\t\t\t\t\t\t\t\t\t"label": "",\n\t\t\t\t\t\t\t\t\t"value": "C"\n\t\t\t\t\t\t\t\t}\n\t\t\t\t\t\t\t]\n\t\t\t\t\t\t}\n\t\t\t\t\t]\n\t\t\t\t}\n\t\t\t]\n\t\t}, {\n\t\t\t"subUuid": "uuid",\n\t\t\t"subCompCode": "G008",\n\t\t\t"subCompLabel": "Version Control",\n\t\t\t"details": [{\n\t\t\t\t\t"id": "verProdSaleSheetList",\n\t\t\t\t\t"code": "T001",\n\t\t\t\t\t"label": "",\n\t\t\t\t\t"value": "",\n\t\t\t\t\t"require": false,\n\t\t\t\t\t"validation": [],\n\t\t\t\t\t"pattern" : "",\n\t\t\t\t\t"description": "",\n\t\t\t\t\t"table": {\n\t\t\t\t\t\t"version": "Version",\n\t\t\t\t\t\t"createdDate": "Date",\n\t\t\t\t\t\t"activeDate": "Active Date",\n\t\t\t\t\t\t"status": "Status"\n\t\t\t\t\t},\n\t\t\t\t\t"data": [{\n\t\t\t\t\t\t\t"version": "1",\n\t\t\t\t\t\t\t"createdDate": "2020-01-01",\n\t\t\t\t\t\t\t"activeDate": "2020-01-01",\n\t\t\t\t\t\t\t"status": "Active",\n\t\t\t\t\t\t\t"uuid": "xxxxx"\n\t\t\t\t\t\t}, {\n\t\t\t\t\t\t\t"version": "2",\n\t\t\t\t\t\t\t"createdDate": "2019-12-23",\n\t\t\t\t\t\t\t"activeDate": "2020-01-01",\n\t\t\t\t\t\t\t"status": "InActive",\n\t\t\t\t\t\t\t"uuid": "xxxxx"\n\t\t\t\t\t\t}, {\n\t\t\t\t\t\t\t"version": "3",\n\t\t\t\t\t\t\t"createdDate": "2019-11-03",\n\t\t\t\t\t\t\t"activeDate": "2020-11-30",\n\t\t\t\t\t\t\t"status": "InActive",\n\t\t\t\t\t\t\t"uuid": "xxxxx"\n\t\t\t\t\t\t}\n\t\t\t\t\t]\n\t\t\t\t}\n\t\t\t]\n\t\t}\n\t]\n}	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-02 09:13:24	tOr	\N	\N	\N
ae6ec58b-e9b1-4ddf-a116-4aea79dcefc5	4	004	TERM & CONDITION	\N	{\n        "compUuid": "ae6ec58b-e9b1-4ddf-a116-4aea79dcefc5",\n        "compName": "Terms & Condition",\n        "subComp": [{\n                "subUuid": "23b2e944-0b08-4ecd-a318-ead86f7dcf3d",\n                "subCompCode": "G001",\n                "subCompLabel": "Terms & Condition",\n                "details": [{\n                        "id": "headerText",\n                        "code": "IN001",\n                        "label": "Header Text",\n                        "value": "",\n                        "require": false,\n                        "validation": "",\n                        "description": ""\n                    },{\n                        "id": "termsNCondition",\n                        "code": "IN005",\n                        "label": "Terms & Condition",\n                        "value": "",\n                        "require": false,\n                        "validation": "",\n                        "description": ""\n                    }, {\n                        "id": "activeDate",\n                        "code": "IN002",\n                        "label": "Active Date",\n                        "value": "",\n                        "require": true,\n                        "validation": "",\n                        "description": ""\n                    },{\n                        "id": "version",\n                        "code": "IN001",\n                        "label": "Version",\n                        "value": "",\n                        "require": true,\n                        "validation": "",\n                        "description": ""\n                    }, {\n                        "subUuid": "uuid",\n                        "subCompCode": "G007",\n                        "subCompLabel": "Check Box Detail",\n                        "details": [{\n                                "id": "chkBoxLabel",\n                                "code": "IN001",\n                                "label": "Check Box Label",\n                                "value": "",\n                                "require": false,\n                                "validation": "",\n                                "description": ""\n                            }, {\n                                "id": "radioMandatory",\n                                "code": "IN009",\n                                "label": "Mandatory (Yes/No)",\n                                "value": "",\n                                "require": false,\n                                "validation": "",\n                                "description": "",\n                                "data": [{\n                                        "label": "Yes",\n                                        "value": "Y"\n                                    }, {\n                                        "label": "No",\n                                        "value": "N"\n                                    }, {\n                                        "label": "",\n                                        "value": "C"\n                                    }\n                                ]\n                            }\n                        ]\n                    }\n                ]\n            }, {\n                "subUuid": "uuid",\n                "subCompCode": "G008",\n                "subCompLabel": "Version Control",\n                "details": [{\n                        "id": "verTermsNConditionList",\n                        "code": "T001",\n                        "label": "",\n                        "value": "",\n                        "require": false,\n                        "validation": "",\n                        "description": "",\n                        "table": {\n                            "version": "Version",\n                            "createdDate": "Date",\n                            "activeDate": "Active Date",\n                            "status": "Status"\n                        },\n                        "data": [{\n                                "version": "1",\n                                "createdDate": "2020-01-01",\n                                "activeDate": "2020-01-01",\n                                "status": "Active",\n                                "uuid": "xxxxx"\n                            }, {\n                                "version": "2",\n                                "createdDate": "2019-12-23",\n                                "activeDate": "2020-01-01",\n                                "status": "InActive",\n                                "uuid": "xxxxx"\n                            }, {\n                                "version": "3",\n                                "createdDate": "2019-11-03",\n                                "activeDate": "2020-11-30",\n                                "status": "InActive",\n                                "uuid": "xxxxx"\n                            }\n                        ]\n                    }\n                ]\n            }\n        ]\n    }	\N	\N	\N	\N	\N	\N	\N	\N	{"type":"api","default":"consent","value":"https://test.com/test"}	\N	213	2020-01-02 09:13:57	tOr	\N	\N	\N
a34404a3-309e-4057-8f53-f386d026656b	5	005	AUTHENTICATE	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-02 09:14:28	tOr	\N	\N	\N
ed8880d3-e353-48c6-bfd7-debe70ba6c37	7	007	CUSTOMER INFORMATION	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-02 09:15:44	tOr	\N	\N	\N
d4882b6c-8e4b-441d-86fc-019c8eb4c232	8	008	CUSTOMER QUALIFICATION	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-02 09:16:15	tOr	\N	\N	\N
b29f1ed0-aa1e-4b4c-8053-2808fdb04431	2	002	CUSTOMER SUMMARY STEP	\N	{\n\t"compUuid": "b29f1ed0-aa1e-4b4c-8053-2808fdb04431",\n\t"compName": "Customer Summary Step",\n\t"subComp": [{\n\t\t\t"subUuid": "23b2e944-0b08-4ecd-a318-ead86f7dcf3d",\n\t\t\t"subCompCode": "G001",\n\t\t\t"subCompLabel": "Customer Summary Step",\n\t\t\t"details": [{\n\t\t\t\t\t"id": "headerText",\n\t\t\t\t\t"code": "IN001",\n\t\t\t\t\t"label": "Header Text",\n\t\t\t\t\t"value": "",\n\t\t\t\t\t"require": false,\n\t\t\t\t\t"validation": [],\n\t\t\t\t\t"pattern" : "",\n\t\t\t\t\t"description": ""\n\t\t\t\t}, {\n\t\t\t\t\t"id": "imgUpload",\n\t\t\t\t\t"code": "IN003",\n\t\t\t\t\t"label": "image",\n\t\t\t\t\t"value": "",\n\t\t\t\t\t"require": false,\n\t\t\t\t\t"validation": ["image/jpeg"," image/png"],\n\t\t\t\t\t"pattern" : "",\n\t\t\t\t\t"description": "Support JPEG/PNG/JPG"\n\t\t\t\t}, {\n\t\t\t\t\t"id": "imageType",\n\t\t\t\t\t"code": "IN004",\n\t\t\t\t\t"label": "Type",\n\t\t\t\t\t"value": "",\n\t\t\t\t\t"require": false,\n\t\t\t\t\t"validation": [],\n\t\t\t\t\t"pattern" : "",\n\t\t\t\t\t"description": "",\n\t\t\t\t\t"data": [{\n\t\t\t\t\t\t\t"label": "Please Select",\n\t\t\t\t\t\t\t"value": ""\n\t\t\t\t\t\t}, {\n\t\t\t\t\t\t\t"label": "Website",\n\t\t\t\t\t\t\t"value": "w"\n\t\t\t\t\t\t}, {\n\t\t\t\t\t\t\t"label": "Mobile",\n\t\t\t\t\t\t\t"value": "m"\n\t\t\t\t\t\t}\n\t\t\t\t\t]\n\t\t\t\t}, {\n\t\t\t\t\t"id": "btnUpload",\n\t\t\t\t\t"code": "B001",\n\t\t\t\t\t"label": "Upload",\n\t\t\t\t\t"value": "",\n\t\t\t\t\t"require": true,\n\t\t\t\t\t"validation": [],\n\t\t\t\t\t"pattern" : "",\n\t\t\t\t\t"description": ""\n\t\t\t\t}, {\n\t\t\t\t\t"id": "custSumStepList",\n\t\t\t\t\t"code": "T001",\n\t\t\t\t\t"label": "",\n\t\t\t\t\t"value": "",\n\t\t\t\t\t"require": false,\n\t\t\t\t\t"validation": [],\n\t\t\t\t\t"pattern" : "",\n\t\t\t\t\t"description": "",\n\t\t\t\t\t"table": {\n\t\t\t\t\t\t"fileName": "File Name",\n\t\t\t\t\t\t"type": "Type",\n\t\t\t\t\t\t"delete": "Delete"\n\t\t\t\t\t},\n\t\t\t\t\t"data": [{\n\t\t\t\t\t\t\t"fileName": "xxxxx.png",\n\t\t\t\t\t\t\t"type": "Website",\n\t\t\t\t\t\t\t"delete": "Delete",\n\t\t\t\t\t\t\t"value": "base64"\n\t\t\t\t\t\t}, {\n\t\t\t\t\t\t\t"fileName": "xxxxx.png",\n\t\t\t\t\t\t\t"type": "Mobile",\n\t\t\t\t\t\t\t"delete": "Delete",\n\t\t\t\t\t\t\t"value": "base64"\n\t\t\t\t\t\t}\n\t\t\t\t\t]\n\t\t\t\t}\n\t\t\t]\n\t\t}\n\t]\n}	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-02 09:12:40	tOr	\N	\N	\N
ec654003-0763-48d3-af08-8a9b053783e4	6	006	CONSENT	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	{"type":"api","default":"consent","value":"https://test.com/test"}	\N	213	2020-01-02 09:15:08	tOr	\N	\N	\N
\.


--
-- TOC entry 3959 (class 0 OID 16418)
-- Dependencies: 198
-- Data for Name: t_shelf_comp_dtl; Type: TABLE DATA; Schema: public; Owner: digitalshelf
--

COPY public.t_shelf_comp_dtl (uuid, comp_uuid, lk_uuid, label_text, data_value, description, attr1, attr2, attr3, attr4, attr5, attr6, attr7, attr8, attr9, attr10, seq, parent, status, create_at, create_by, update_at, update_by, ele_id, require, validation, pattern, table_column) FROM stdin;
cf686160-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	23b2e944-0b08-4ecd-a318-ead86f7dcf3d	Customer Information		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	1	\N	213	2020-01-23 15:42:10	user	\N	\N	g001	f			
cf6865ac-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	c1b3bf6a-b328-46be-a9ba-d60396025120	Header Text(H1)		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	2	1	213	2020-01-23 15:42:10	user	\N	\N	headerText	f			
cf686714-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	a5d5ea52-bba8-4d6a-bb3a-6ac979d35dbb	Citizen Information		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	3	1	213	2020-01-23 15:42:10	user	\N	\N	g002	f			
cf686854-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	c1b3bf6a-b328-46be-a9ba-d60396025120	Header Text(H2)		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	4	3	213	2020-01-23 15:42:10	user	\N	\N	headerText2	f			
cf68698a-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	603b12cd-d820-4d54-8fd7-a9f308ce7557	ข้อมุลบัตรประชาชน		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	5	3	213	2020-01-23 15:42:10	user	\N	\N	g003	f			
cf6872e0-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	603b12cd-d820-4d54-8fd7-a9f308ce7557	ทีอยู่ลูกค้า		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	11	3	213	2020-01-23 15:42:10	user	\N	\N	g004	f			
cf68832a-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	ถ่ายรูปบัตรประชาชนด้านหน้า		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	21	20	213	2020-01-23 15:42:10	user	\N	\N	ocr	f			
cf688460-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	ถ่ายรูปบัตรประชาชนด้านหลัง		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	22	20	213	2020-01-23 15:42:10	user	\N	\N	ocrLaserId	f			
cf68858c-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	ตรวจ DOPA		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	23	20	213	2020-01-23 15:42:10	user	\N	\N	dopa	f			
cf6878c6-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	603b12cd-d820-4d54-8fd7-a9f308ce7557	ข้อมูลอื่นๆ		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	14	3	213	2020-01-23 15:42:10	user	\N	\N	g005	f			
cf6881e0-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	d5bf8b97-6cd9-4871-accf-2f19f4e43fbb	Authentication		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	20	3	213	2020-01-23 15:42:10	user	\N	\N	g007	f			
cf6886ae-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	23b2e944-0b08-4ecd-a318-ead86f7dcf3d	Address Information		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	24	1	213	2020-01-23 15:42:10	user	\N	\N	g008	f			
cf6887da-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	c1b3bf6a-b328-46be-a9ba-d60396025120	Header Text (H2)		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	25	24	213	2020-01-23 15:42:10	user	\N	\N	addrHeaderText	f			
cf6888fc-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	603b12cd-d820-4d54-8fd7-a9f308ce7557	รายการเกี่ยวกับบ้าน		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	26	24	213	2020-01-23 15:42:10	user	\N	\N	g009	f			
cf688bea-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	เลขรหัสประจำบ้าน		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	27	26	213	2020-01-23 15:42:10	user	\N	\N	houseID	f			
cf688d20-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	ที่อยู่		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	28	26	213	2020-01-23 15:42:10	user	\N	\N	address	f			
cf68ccae-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	เชื้อเพลิง		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	65	49	213	2020-01-23 15:42:10	user	\N	\N	carFuel	f			
cf68cdf8-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	เลขถังแก๊ส		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	66	49	213	2020-01-23 15:42:10	user	\N	\N	gasTankNo	f			
cf6906b0-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	60dda655-621c-47c5-b891-bf526b6beeb7	Expend		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	101	100	213	2020-01-23 15:42:10	user	\N	\N	lblExpend	f			
cf69113c-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	60dda655-621c-47c5-b891-bf526b6beeb7	Debt		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	108	100	213	2020-01-23 15:42:10	user	\N	\N	lblDebt	f			
cf691cea-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	23b2e944-0b08-4ecd-a318-ead86f7dcf3d	Other KYC Section		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	115	1	213	2020-01-23 15:42:10	user	\N	\N	g017	f			
cf691e16-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	c8729a63-34ba-4dab-9fd8-1084ab018773	Text Label		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	116	115	213	2020-01-23 15:42:10	user	\N	\N	txtLabel	f			
cf690d18-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	70d96771-dae9-4488-a5de-f8a7b431c4dc			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	105	102	213	2020-01-23 15:42:10	user	\N	\N	chkExpend3	f			
fd591d4a-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	23b2e944-0b08-4ecd-a318-ead86f7dcf3d	Product Info		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	1	\N	213	2020-01-23 15:48:33	user	\N	\N	g001	f	\N	\N	
fd591fe8-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	a5d5ea52-bba8-4d6a-bb3a-6ac979d35dbb	Product Info Configuration		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	2	1	213	2020-01-23 15:48:33	user	\N	\N	g002	f	\N	\N	
fd59213c-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	c1b3bf6a-b328-46be-a9ba-d60396025120	Header Text		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	3	2	213	2020-01-23 15:48:33	user	\N	\N	headerText	f	\N	\N	
fd592272-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	c1b3bf6a-b328-46be-a9ba-d60396025120	Product Type		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	4	2	213	2020-01-23 15:48:33	user	\N	\N	productType	t	\N	\N	
fd59268c-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	c1b3bf6a-b328-46be-a9ba-d60396025120	Limit Adjustment		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	5	2	213	2020-01-23 15:48:33	user	\N	\N	limitAdjust	t	\N	\N	
fd5927ea-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	c1b3bf6a-b328-46be-a9ba-d60396025120	Minimum		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	6	2	213	2020-01-23 15:48:33	user	\N	\N	minimum	t	\N	\N	
fd59292a-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	c1b3bf6a-b328-46be-a9ba-d60396025120	Maximum		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	7	2	213	2020-01-23 15:48:33	user	\N	\N	maximum	t	\N	\N	
fd592b78-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	b4b5b749-3701-458f-81cf-7bae4aebc494	Day		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	9	8	213	2020-01-23 15:48:33	user	\N	\N	pcutOffDay	f	\N	\N	
fd592dc6-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	b4b5b749-3701-458f-81cf-7bae4aebc494	Time		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	11	8	213	2020-01-23 15:48:33	user	\N	\N	pcutOffTime	f	\N	\N	
b8e0201f-d930-4e3a-b455-c8d2b54ccd67	af0f3037-5d24-454f-b81e-ed33e0f5f334	c1b3bf6a-b328-46be-a9ba-d60396025120	Botton Text			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	6	1	213	2020-01-17 09:53:26	user	\N	\N	btnText	t			
7be1ff1e-72e0-46d4-a6f1-9ac39e5f4f6d	af0f3037-5d24-454f-b81e-ed33e0f5f334	7e6287df-63b6-4f75-9a9e-4ce5343143b2	Active Date			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	7	1	213	2020-01-17 09:53:26	user	\N	\N	activeDate	t			
cf688e56-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	แขวง		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	29	26	213	2020-01-23 15:42:10	user	\N	\N	subDistrict	f			
cf689072-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	เขต		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	30	26	213	2020-01-23 15:42:10	user	\N	\N	district	f			
cf6891a8-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	จังหวัด		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	31	26	213	2020-01-23 15:42:10	user	\N	\N	province	f			
cf6916d2-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	70d96771-dae9-4488-a5de-f8a7b431c4dc			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	110	109	213	2020-01-23 15:42:10	user	\N	\N	chkDebt1	f			
cf6892d4-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	ประเภทบ้าน		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	32	26	213	2020-01-23 15:42:10	user	\N	\N	addressType	f			
cf689662-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	603b12cd-d820-4d54-8fd7-a9f308ce7557	รายการบุคคลภายในบ้าน		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	33	24	213	2020-01-23 15:42:10	user	\N	\N	g010	f			
cf6897ca-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	ชื่อ		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	34	33	213	2020-01-23 15:42:10	user	\N	\N	name	f			
cf689900-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	นามสกุล		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	35	33	213	2020-01-23 15:42:10	user	\N	\N	lastName	f			
cf689a2c-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	สัญชาติ		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	36	33	213	2020-01-23 15:42:10	user	\N	\N	nationality	f			
cf689b58-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	เพศ		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	37	33	213	2020-01-23 15:42:10	user	\N	\N	gender	f			
cf689d1a-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	เลขบัตรประชาชน		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	38	33	213	2020-01-23 15:42:10	user	\N	\N	citizenID	f			
cf689e82-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	สถานภาพ		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	39	33	213	2020-01-23 15:42:10	user	\N	\N	status	f			
cf68a1d4-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	วันเกิด 		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	40	33	213	2020-01-23 15:42:10	user	\N	\N	birthDate	f			
cf68a33c-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	ชื่ิ่อบิดา		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	41	33	213	2020-01-23 15:42:10	user	\N	\N	fatherName	f			
cf68a472-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	เลขบัตรประชาชนของบิดา		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	42	33	213	2020-01-23 15:42:10	user	\N	\N	fatherID	f			
cf68a59e-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	สัญชาติของบิดา		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	43	33	213	2020-01-23 15:42:10	user	\N	\N	fatherNationality	f			
cf68a6ca-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	ชื่อมารดา		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	44	33	213	2020-01-23 15:42:10	user	\N	\N	motherName	f			
cf68a800-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	เลขบัตรประชาชนมารดา		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	45	33	213	2020-01-23 15:42:10	user	\N	\N	motherID	f			
cf68a92c-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	สัญชาติของมารดา		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	46	33	213	2020-01-23 15:42:10	user	\N	\N	motherNationality	f			
cf68aca6-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	23b2e944-0b08-4ecd-a318-ead86f7dcf3d	Vehicle Information		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	47	1	213	2020-01-23 15:42:10	user	\N	\N	g011	f			
cf68ade6-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	c1b3bf6a-b328-46be-a9ba-d60396025120	Header Text (H2)		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	48	47	213	2020-01-23 15:42:10	user	\N	\N	vehHeaderText	f			
cf68af12-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	603b12cd-d820-4d54-8fd7-a9f308ce7557	รายการจดทะเบียน		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	49	47	213	2020-01-23 15:42:10	user	\N	\N	g012	f			
cf68b048-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	วันจดทะเบียน		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	50	49	213	2020-01-23 15:42:10	user	\N	\N	registerDate	f			
cf68b17e-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	รายการจดทะเบียน		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	51	49	218	2020-01-23 15:42:10	user	\N	\N	registertmp	f			
cf68b2b4-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	เลขทะเบียนรถ		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	52	49	213	2020-01-23 15:42:10	user	\N	\N	registerID	f			
cf68b6a6-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	จังหวัดจดทะเบียน		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	53	49	213	2020-01-23 15:42:10	user	\N	\N	registerProvince	f			
cf68b818-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	ประเภท		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	54	49	213	2020-01-23 15:42:10	user	\N	\N	type	f			
cf68b958-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	รย.		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	55	49	213	2020-01-23 15:42:10	user	\N	\N	typeNo	f			
cf68ba8e-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	ลักษณะ		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	56	49	213	2020-01-23 15:42:10	user	\N	\N	category	f			
cf68bbb0-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	ยี่ห้อเครื่องยนต์		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	57	49	213	2020-01-23 15:42:10	user	\N	\N	motorBrand	f			
cf68bcf0-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	แบบ		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	58	49	213	2020-01-23 15:42:10	user	\N	\N	motorType	f			
cf68be26-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	รุ่นปี		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	59	49	213	2020-01-23 15:42:10	user	\N	\N	carRegisteredYear	f			
cf68c0ce-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	สี		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	60	49	213	2020-01-23 15:42:10	user	\N	\N	carColor	f			
cf68c222-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	เลขตัวรถ		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	61	49	213	2020-01-23 15:42:10	user	\N	\N	chassisNo	f			
fdef64e4-419b-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	59dd731e-419b-11ea-b77f-2e728ce88125	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	118	1	213	2020-01-28 14:16:06	user	\N	\N	maxDOPA	f	\N	\N	\N
cf68c448-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	อยู่ที่		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	62	49	213	2020-01-23 15:42:10	user	\N	\N	chasisPlace	f			
cf68c5ba-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	เลขเครื่องยนต์		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	63	49	213	2020-01-23 15:42:10	user	\N	\N	carNo	f			
cf68cb3c-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	อยู่ที่		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	64	49	213	2020-01-23 15:42:10	user	\N	\N	carPlace	f			
cf68cf24-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	จัำนวนลูกสูบ		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	67	49	213	2020-01-23 15:42:10	user	\N	\N	pistonsNumber	f			
cf68d050-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	จำนวนซ๊ซี		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	68	49	213	2020-01-23 15:42:10	user	\N	\N	carQuantity	f			
cf68d33e-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	แรงม้า		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	69	49	213	2020-01-23 15:42:10	user	\N	\N	carPower	f			
cf68d492-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	เพลา		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	70	49	213	2020-01-23 15:42:10	user	\N	\N	carShaft	f			
cf68d5be-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	ยาง		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	71	49	213	2020-01-23 15:42:10	user	\N	\N	carRubber	f			
cf68d6ea-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	น้ำหนักรถ		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	72	49	213	2020-01-23 15:42:10	user	\N	\N	carWeight	f			
cf68d816-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	น้ำหนัักบรรทุก		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	73	49	213	2020-01-23 15:42:10	user	\N	\N	carLoad	f			
cf68d938-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	น้ำหนักรวม		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	74	49	213	2020-01-23 15:42:10	user	\N	\N	carTotalLoad	f			
cf68dbd6-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	ที่นั่ง		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	75	49	213	2020-01-23 15:42:10	user	\N	\N	carSeatNo	f			
cf68dd20-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	603b12cd-d820-4d54-8fd7-a9f308ce7557	เจ้าของรถ		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	76	47	213	2020-01-23 15:42:10	user	\N	\N	g013	f			
cf68de4c-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	วันที่ครอบครองรถ		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	77	76	213	2020-01-23 15:42:10	user	\N	\N	ownDate	f			
cf68df78-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	ผู้ถือกรรมสิทธิ์		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	78	76	213	2020-01-23 15:42:10	user	\N	\N	ownerCar	f			
cf68e0a4-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	เลขที่บัตร		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	79	76	213	2020-01-23 15:42:10	user	\N	\N	ownerNo	f			
cf68e1c6-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	วันเกิด		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	80	76	213	2020-01-23 15:42:10	user	\N	\N	ownerBirthDate	f			
cf68e43c-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	สีัญชาติ		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	81	76	213	2020-01-23 15:42:10	user	\N	\N	ownerNatioanality	f			
cf68e572-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	ที่อยู่		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	82	76	213	2020-01-23 15:42:10	user	\N	\N	address	f			
cf68e694-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	แขวง		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	83	76	213	2020-01-23 15:42:10	user	\N	\N	subDistrict	f			
cf68e7c0-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	เขต		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	84	76	213	2020-01-23 15:42:10	user	\N	\N	district	f			
cf68e8e2-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	จังหวัด		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	85	76	213	2020-01-23 15:42:10	user	\N	\N	Province	f			
cf68ec0c-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	ผู้ครอบครองรถ		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	86	76	213	2020-01-23 15:42:10	user	\N	\N	occupantName	f			
cf68f260-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	เลขที่่บัตร		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	87	76	213	2020-01-23 15:42:10	user	\N	\N	occupantNo	f			
cf68f3dc-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	วันเกิด		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	88	76	213	2020-01-23 15:42:10	user	\N	\N	occupantBirthDate	f			
cf68f508-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	สัญชาติ		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	89	76	213	2020-01-23 15:42:10	user	\N	\N	occupantNationality	f			
cf68f634-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	23b2e944-0b08-4ecd-a318-ead86f7dcf3d	Income Information		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	90	1	213	2020-01-23 15:42:10	user	\N	\N	g014	f			
cf68fa26-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	d5bf8b97-6cd9-4871-accf-2f19f4e43fbb	Income		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	92	90	213	2020-01-23 15:42:10	user	\N	\N	g015	f			
cf690584-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	d5bf8b97-6cd9-4871-accf-2f19f4e43fbb	Expend & Debt		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	100	90	213	2020-01-23 15:42:10	user	\N	\N	g016	f			
cf68fc88-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	6f61a01f-6d49-405c-bbea-b479e20b3bad			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	94	92	213	2020-01-23 15:42:10	user	\N	\N	gIncome	f			
cf68fdaa-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	70d96771-dae9-4488-a5de-f8a7b431c4dc			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	95	94	213	2020-01-23 15:42:10	user	\N	\N	chkIncome1	f			
cf68feea-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	70d96771-dae9-4488-a5de-f8a7b431c4dc			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	96	94	213	2020-01-23 15:42:10	user	\N	\N	chkIncome2	f			
cf690016-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	70d96771-dae9-4488-a5de-f8a7b431c4dc			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	97	94	213	2020-01-23 15:42:10	user	\N	\N	chkIncome3	f			
cf690322-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	70d96771-dae9-4488-a5de-f8a7b431c4dc			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	98	94	213	2020-01-23 15:42:10	user	\N	\N	chkIncome4	f			
cf68f760-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	c1b3bf6a-b328-46be-a9ba-d60396025120	Header Text (H2)		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	91	90	213	2020-01-23 15:42:10	user	\N	\N	incHeaderText	f			
cf690462-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	70d96771-dae9-4488-a5de-f8a7b431c4dc			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	99	94	213	2020-01-23 15:42:10	user	\N	\N	chkIncome5	f			
cf692122-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	32223be9-8aa5-4bc0-8a7c-d7fee1f29594	Radio List	[{"label":"ไม่ใช่, ข้าพเจ้าไม่ได้อยู่ในพื้นที่นี้", "parameter":"N"},{"label":"ใช่, ข้าพเจ้าอยู่ในพื้นที่นี้", "parameter":"Y"}]	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	117	115	213	2020-01-23 15:42:10	user	\N	\N	radioList	f			
cf690f70-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	70d96771-dae9-4488-a5de-f8a7b431c4dc			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	107	102	213	2020-01-23 15:42:10	user	\N	\N	chkExpend5	f			
8a9f1c09-b531-4490-b6ea-b1dfa8eb0546	ec654003-0763-48d3-af08-8a9b053783e4	c1b3bf6a-b328-46be-a9ba-d60396025120	Header Text			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	2	1	213	2020-01-17 09:53:26	user	\N	\N	headerText	f			
cf690e44-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	70d96771-dae9-4488-a5de-f8a7b431c4dc			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	106	102	213	2020-01-23 15:42:10	user	\N	\N	chkExpend4	f			
cf690ba6-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	70d96771-dae9-4488-a5de-f8a7b431c4dc			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	104	102	213	2020-01-23 15:42:10	user	\N	\N	chkExpend2	f			
cf6908f4-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	70d96771-dae9-4488-a5de-f8a7b431c4dc			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	103	102	213	2020-01-23 15:42:10	user	\N	\N	chkExpend1	f			
cf6907d2-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	6f61a01f-6d49-405c-bbea-b479e20b3bad			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	102	100	213	2020-01-23 15:42:10	user	\N	\N	gExpend	f			
cf69133a-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	6f61a01f-6d49-405c-bbea-b479e20b3bad			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	109	100	213	2020-01-23 15:42:10	user	\N	\N	gDebt	f			
cf69183a-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	70d96771-dae9-4488-a5de-f8a7b431c4dc			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	111	109	213	2020-01-23 15:42:10	user	\N	\N	chkDebt2	f			
cf691970-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	70d96771-dae9-4488-a5de-f8a7b431c4dc			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	112	109	213	2020-01-23 15:42:10	user	\N	\N	chkDebt3	f			
cf691a9c-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	70d96771-dae9-4488-a5de-f8a7b431c4dc			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	113	109	213	2020-01-23 15:42:10	user	\N	\N	chkDebt4	f			
cf691bc8-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	70d96771-dae9-4488-a5de-f8a7b431c4dc			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	114	109	213	2020-01-23 15:42:10	user	\N	\N	chkDebt5	f			
fd593cf8-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	d5bf8b97-6cd9-4871-accf-2f19f4e43fbb	Campaign		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	14	1	213	2020-01-23 15:48:33	user	\N	\N	g004	f	\N	\N	
fd593e2e-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	2af4ffce-dd8d-496b-8b42-d4e0adfa126d	Campaign ID	[{ "label": "Please Select", "value": "" }, {"label": "Campaign 1", "value": "campaing1" }, {"label": "Campaign 2", "value": "campaing2" }  ]	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	15	14	213	2020-01-23 15:48:33	user	\N	\N	campaignId	t	\N	\N	
fd593f50-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	c1b3bf6a-b328-46be-a9ba-d60396025120	Campaign Name		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	16	14	213	2020-01-23 15:48:33	user	\N	\N	campaignName	f	\N	\N	
fd59407c-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	180971de-efe3-46e1-b4d6-fd9ccda44f08	Default Campaign		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	17	14	213	2020-01-23 15:48:33	user	\N	\N	defCampaign	f	\N	\N	
fd592c9a-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	ef3e5bc1-6a99-4463-b1ea-4e7e06d0b09e		[{value:"w",label:"Working Day"},{value:"e",label:"EveryDay"}]	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	10	9	213	2020-01-23 15:48:33	user	\N	\N	pcutOffWE	f	\N	\N	
fd5942c0-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	c1b3bf6a-b328-46be-a9ba-d60396025120			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	19	14	213	2020-01-23 15:48:33	user	\N	\N	defUrl	f	\N	\N	
fd5933b6-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	cdb2b5be-1d67-49a6-b42c-af7cad300abf			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	13	11	213	2020-01-23 15:48:33	user	\N	\N	pcutOffTSpec	f	\N	\N	
2f3a5c72-4963-4295-a67c-83014110760b	169e986b-5a0a-45c9-8b08-890841b5b23f	23b2e944-0b08-4ecd-a318-ead86f7dcf3d	ProductSaleSheet			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	1	\N	213	2020-01-17 09:53:26		\N	\N	g001	f			
5e04d32c-1c9e-4192-a8c9-0b099d84b500	169e986b-5a0a-45c9-8b08-890841b5b23f	b4b5b749-3701-458f-81cf-7bae4aebc494	CheckBoxDetail			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	5	1	213	2020-01-17 09:53:26	user	\N	\N	g002	f			
fd592a56-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	d5bf8b97-6cd9-4871-accf-2f19f4e43fbb	Product Operation Cut Off Time		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	8	1	213	2020-01-23 15:48:33	user	\N	\N	cutOffTime	f	\N	\N	
fd5934e2-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	d5bf8b97-6cd9-4871-accf-2f19f4e43fbb	Calculation : คำนวนยอดผ่อนต่อเดือน		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	20	1	213	2020-01-23 15:48:33	user	\N	\N	g003	f	\N	\N	
47a28528-d8f5-4dfe-9976-8b945f117d47	af0f3037-5d24-454f-b81e-ed33e0f5f334	43bc8cb8-f7df-429b-a422-241f0ea1683f		[{"fileName": "xxxxx.png","type": "Website","delete": "Delete","value": "base64"},{"fileName": "xxxxx.png","type": "Mobile","delete": "Delete","value": "base64"}]		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	5	1	213	2020-01-17 09:53:26	user	\N	\N	imgSplashList	f			[{"columnname": "File Name", "data":"fileName" },{"columnname": "Type", "data":"type" },{"columnname": "Delete", "data":"delete" }]
f5b12757-b726-46bf-b206-1d063d7d649d	af0f3037-5d24-454f-b81e-ed33e0f5f334	48acf7d7-a546-41f1-9a21-e04f3a97f2c7	image		Support JPEG/PNG/JPG	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	2	1	213	2020-01-17 09:53:26	user	\N	\N	imgUpload	t	["image/jpeg", "image/png"]		
bb8e7a63-5b8c-4340-ac1f-c018c6d7d647	af0f3037-5d24-454f-b81e-ed33e0f5f334	2af4ffce-dd8d-496b-8b42-d4e0adfa126d	Type	[{"label": "Please Select","value": ""}, {"label": "Website","value": "w"}, {"label": "Mobile","value": "m"}]		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	3	1	213	2020-01-17 09:53:26	user	\N	\N	imageType	t			
f4a5d416-e753-4295-b49e-306966d3753d	af0f3037-5d24-454f-b81e-ed33e0f5f334	5371acb6-a23e-45c9-a266-aeb1c27d10aa	Upload			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	4	1	213	2020-01-17 09:53:26	user	\N	\N	btnUpload	t			
0fda2099-3388-4d4b-8cda-7b97c5151893	af0f3037-5d24-454f-b81e-ed33e0f5f334	43bc8cb8-f7df-429b-a422-241f0ea1683f		[ {"version": "1","createdDate": "2020-01-01","activeDate": "2020-01-01","status": "Active","uuid": "xxxxx"}, {"version": "2","createdDate": "2019-12-23","activeDate": "2020-01-01","status": "InActive","uuid": "xxxxx"}, {"version": "3","createdDate": "2019-11-03","activeDate": "2020-11-30","status": "InActive","uuid": "xxxxx"} ]		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	9	8	213	2020-01-17 09:53:26	user	\N	\N	verSplashList	f			[{"columnname": "Version", "data":"version" },{"columnname": "Active Date", "data":"activeDate" },{"columnname": "Created Date", "data":"createdDate" },{"columnname": "Status", "data":"status" }]
053a9cf1-9319-4260-9313-ad9e8fbb2f9e	b29f1ed0-aa1e-4b4c-8053-2808fdb04431	c1b3bf6a-b328-46be-a9ba-d60396025120	Header Text			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	2	1	213	2020-01-17 09:53:26	user	\N	\N	headerText	f			
4e8fe7b4-8c87-4921-b102-5020854438d0	b29f1ed0-aa1e-4b4c-8053-2808fdb04431	48acf7d7-a546-41f1-9a21-e04f3a97f2c7	image		Support JPEG/PNG/JPG	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	3	1	213	2020-01-17 09:53:26	user	\N	\N	imgUpload	f	["image/jpeg"," image/png"]		
eb1b77d2-8d78-4d89-a60a-78ef34b970b0	b29f1ed0-aa1e-4b4c-8053-2808fdb04431	2af4ffce-dd8d-496b-8b42-d4e0adfa126d	Type	[{"label": "Please Select","value": ""}, {"label": "Website","value": "w"}, {"label": "Mobile","value": "m"}]		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	4	1	213	2020-01-17 09:53:26	user	\N	\N	imageType	t			
5e07488d-4191-4863-977f-a79e04dd246f	b29f1ed0-aa1e-4b4c-8053-2808fdb04431	5371acb6-a23e-45c9-a266-aeb1c27d10aa	Upload			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	4	1	213	2020-01-17 09:53:26	user	\N	\N	btnUpload	t			
c9bae741-ce6f-4db3-8c2d-9a3a17638989	169e986b-5a0a-45c9-8b08-890841b5b23f	c1b3bf6a-b328-46be-a9ba-d60396025120	HeaderText			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	2	1	213	2020-01-17 09:53:26	user	\N	\N	headerText	f			
74731bad-558a-4652-a8ac-548b2bf4014a	169e986b-5a0a-45c9-8b08-890841b5b23f	48acf7d7-a546-41f1-9a21-e04f3a97f2c7	SaleSheet		SupportPDF,Filesizexxx	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	3	1	213	2020-01-17 09:53:26	user	\N	\N	pdfUpload	t	["application/pdf"]		
40147b5a-bbe6-4297-808e-a5cfd3dae8ef	169e986b-5a0a-45c9-8b08-890841b5b23f	7e6287df-63b6-4f75-9a9e-4ce5343143b2	ActiveDate			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	4	1	213	2020-01-17 09:53:26	user	\N	\N	activeDate	t			
7e6d0ae7-5396-4178-8edb-dc50a3880f7b	169e986b-5a0a-45c9-8b08-890841b5b23f	ef3e5bc1-6a99-4463-b1ea-4e7e06d0b09e	Mandatory(Yes/No)	[{"label":"Yes","value":"Y"},{"label":"No","value":"N"}]		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	7	5	213	2020-01-17 09:53:26	user	\N	\N	radioMandatory	t			
d331f56c-0446-4541-a6f4-cc4b4a5fbe59	ae6ec58b-e9b1-4ddf-a116-4aea79dcefc5	c1b3bf6a-b328-46be-a9ba-d60396025120	Header Text			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	2	1	213	2020-01-17 09:53:26	user	\N	\N	headerText	f			
a3da1913-9b74-4062-9097-8b2c1a1df1ce	ae6ec58b-e9b1-4ddf-a116-4aea79dcefc5	c8729a63-34ba-4dab-9fd8-1084ab018773	Terms & Condition			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	3	1	213	2020-01-17 09:53:26	user	\N	\N	termsNCondition	f			
d16045c9-76ed-4431-a2b7-19b32b04251b	ae6ec58b-e9b1-4ddf-a116-4aea79dcefc5	7e6287df-63b6-4f75-9a9e-4ce5343143b2	Active Date			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	4	1	213	2020-01-17 09:53:26	user	\N	\N	activeDate	t			
23dc3c95-ebba-40a0-9dd4-017ee7e04740	ae6ec58b-e9b1-4ddf-a116-4aea79dcefc5	c1b3bf6a-b328-46be-a9ba-d60396025120	Version			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	5	1	213	2020-01-17 09:53:26	user	\N	\N	version	t			
623621f5-6a02-4bb0-bf87-3238b9a3831c	ae6ec58b-e9b1-4ddf-a116-4aea79dcefc5	ef3e5bc1-6a99-4463-b1ea-4e7e06d0b09e	Mandatory (Yes/No)	[{"label": "Yes","value": "Y"}, {"label": "No","value": "N"}]		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	8	6	213	2020-01-17 09:53:26	user	\N	\N	radioMandatory	t			
6c7416fe-5c37-4233-8ec6-4343ddf29404	169e986b-5a0a-45c9-8b08-890841b5b23f	43bc8cb8-f7df-429b-a422-241f0ea1683f		[{"version":"1","createdDate":"2020-01-01","activeDate":"2020-01-01","status":"Active","uuid":"xxxxx"},{"version":"2","createdDate":"2019-12-23","activeDate":"2020-01-01","status":"InActive","uuid":"xxxxx"},{"version":"3","createdDate":"2019-11-03","activeDate":"2020-11-30","status":"InActive","uuid":"xxxxx"}]		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	9	8	213	2020-01-17 09:53:26	user	\N	\N	verProdSaleSheetList	f			[{"columnname": "Version", "data":"version" },{"columnname": "Active Date", "data":"activeDate" },{"columnname": "Created Date", "data":"createdDate" },{"columnname": "Status", "data":"status" }]
b37f2551-33fe-42b7-a36d-2dd3b2b7fe49	a34404a3-309e-4057-8f53-f386d026656b	db4dc5de-8814-4a89-b349-9ab3b8ad8746		[ { "seq": "1", "checklist": "checkbox", "question": "XXXXX",  "type": "Text",  "validation": "Number"  },{ "seq": "2", "checklist": "checkbox", "question": "XXXXX", "type": "Dropdawn", "validation": "-" },{ "seq": "3", "checklist": "checkbox", "question": "XXXXX", "type": "Date", "validation": "-" } ]		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	11	10	213	2020-01-17 09:53:26	user	\N	\N	imgSecretQuestionList	f			[{"columnname": "No", "data":"seq" },{"columnname": "Checklist", "data":"checklist" },{"columnname": "Question", "data":"question" },{"columnname": "Type", "data":"type" },{"columnname": "Validation", "data":"validation" }]
af77c73f-57a6-40ca-9bd7-0d71f1c4b846	a34404a3-309e-4057-8f53-f386d026656b	c1b3bf6a-b328-46be-a9ba-d60396025120	Header Text			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	3	2	213	2020-01-17 09:53:26	user	\N	\N	otpHeaderText	f			
6d6af257-2bd0-43b6-9078-59d8b24310d3	a34404a3-309e-4057-8f53-f386d026656b	c1b3bf6a-b328-46be-a9ba-d60396025120	Template ID			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	4	2	213	2020-01-17 09:53:26	user	\N	\N	otpTemplateID	t			
80236865-ad8e-488f-903a-2372d14e211d	a34404a3-309e-4057-8f53-f386d026656b	c1b3bf6a-b328-46be-a9ba-d60396025120	Header Text			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	6	5	213	2020-01-17 09:53:26	user	\N	\N	secretHeaderText	f			
c239bb18-aa88-482f-a09e-a31cb01ef673	a34404a3-309e-4057-8f53-f386d026656b	c1b3bf6a-b328-46be-a9ba-d60396025120	Number of Questions			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	7	5	213	2020-01-17 09:53:26	user	\N	\N	otpNumberQuestion	t			
8e57ebbb-ef69-4878-a308-4ff40ce6d88a	a34404a3-309e-4057-8f53-f386d026656b	c1b3bf6a-b328-46be-a9ba-d60396025120	Number of Correct Answers			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	8	5	213	2020-01-17 09:53:26	user	\N	\N	numberCorrectAnswer	t			
c3e5dfac-5c77-4d0a-828f-efb350fc7cf6	a34404a3-309e-4057-8f53-f386d026656b	c1b3bf6a-b328-46be-a9ba-d60396025120	Incorrect Answers			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	9	5	213	2020-01-17 09:53:26	user	\N	\N	inCorrectAnswer	t			
6f23c443-a64a-4b2b-8fc8-16d054418b7c	ec654003-0763-48d3-af08-8a9b053783e4	afef3e6d-284e-4c3a-8462-1d0b8784ae79	Checkbox Consent			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	6	1	213	2020-01-17 09:53:26	user	\N	\N	chkConsentList	f			[{"columnname": "Select", "data":"select" },{"columnname": "Risk Level", "data":"level" }]
36328c9e-8a6c-4e47-a601-1a8ac7f7760d	ae6ec58b-e9b1-4ddf-a116-4aea79dcefc5	43bc8cb8-f7df-429b-a422-241f0ea1683f		[ {"version": "1","createdDate": "2020-01-01","activeDate": "2020-01-01","status": "Active","uuid": "xxxxx"}, {"version": "2","createdDate": "2019-12-23","activeDate": "2020-01-01","status": "InActive","uuid": "xxxxx"}, {"version": "3","createdDate": "2019-11-03","activeDate": "2020-11-30","status": "InActive","uuid": "xxxxx"} ]		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	10	9	213	2020-01-17 09:53:26	user	\N	\N	verTermsNConditionList	f			[{"columnname": "Version", "data":"version" },{"columnname": "Active Date", "data":"activeDate" },{"columnname": "Created Date", "data":"createdDate" },{"columnname": "Status", "data":"status" }]
6f7d16d1-b201-41f1-8c62-0f7b093f4b6b	b29f1ed0-aa1e-4b4c-8053-2808fdb04431	43bc8cb8-f7df-429b-a422-241f0ea1683f		[{"fileName": "xxxxx.png","type": "Website","delete": "Delete","value": "base64"},{"fileName": "xxxxx.png","type": "Mobile","delete": "Delete","value": "base64"}]		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	5	1	213	2020-01-17 09:53:26	user	\N	\N	imgSplashList	f			[{"columnname": "File Name", "data":"fileName" },{"columnname": "Type", "data":"type" },{"columnname": "Delete", "data":"delete" }]
56c4711d-ded1-45ba-8be6-c638ec23abe9	2787aafe-e4e8-4f09-a3b8-5838a7595dbe	48acf7d7-a546-41f1-9a21-e04f3a97f2c7	Upload Agreement		Support docx file	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	2	1	213	2020-01-17 09:53:26	user	\N	\N	imgUpload	t	["application/doc"]		
c0838de8-75c7-4cad-bcd9-fc8ac09fd2b5	ec654003-0763-48d3-af08-8a9b053783e4	ef3e5bc1-6a99-4463-b1ea-4e7e06d0b09e		[{"label": "TISCO Consent", "value": "TC" }, { "label": "Create Consent", "value": "CC" }  ]		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	3	1	213	2020-01-17 09:53:26	user	\N	\N	radioConsent	f			
cef5d6fc-9814-4deb-998f-5a6cc2956159	ec654003-0763-48d3-af08-8a9b053783e4	29b6768c-7539-4b8d-8c37-3b3ad68746dc	Content			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	5	1	213	2020-01-17 09:53:26	user	\N	\N	content	f			
61307a4a-3c22-484d-baa7-124ad2159f96	ec654003-0763-48d3-af08-8a9b053783e4	2af4ffce-dd8d-496b-8b42-d4e0adfa126d	Concent Name	[{"label": "Consent1", "value": "C1" }, { "label": " Consent2", "value": "C2" }  ]		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	4	1	213	2020-01-17 09:53:26	user	\N	\N	consentName	f			
0d97002f-ff57-4318-9e6a-ea5c185094c9	169e986b-5a0a-45c9-8b08-890841b5b23f	3c3f4cc6-ec4b-466f-a162-1001903c2811	VersionControl			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	8	\N	213	2020-01-17 09:53:26	user	\N	\N	g003	f			
3cd8073a-be1a-4afb-946b-171ea03278ce	2787aafe-e4e8-4f09-a3b8-5838a7595dbe	23b2e944-0b08-4ecd-a318-ead86f7dcf3d	Agreement			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	1	\N	213	2020-01-17 09:53:26	user	\N	\N	g001	f			
be1b7292-4db9-4f54-8f72-ef6bd6bb26e3	a34404a3-309e-4057-8f53-f386d026656b	23b2e944-0b08-4ecd-a318-ead86f7dcf3d	Authentication			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	1	\N	213	2020-01-17 09:53:26	user	\N	\N	g001	f			
ab6c1ba3-8541-49dd-bd5a-93a5d742881d	a34404a3-309e-4057-8f53-f386d026656b	dba0ca7c-2fe1-45b6-b199-94beefbdefa8	OTP (Retail)			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	2	1	213	2020-01-17 09:53:26	user	\N	\N	g002	f			
5b53f1b5-04f1-4a4f-9d79-a3350a0a0898	a34404a3-309e-4057-8f53-f386d026656b	dba0ca7c-2fe1-45b6-b199-94beefbdefa8	Secret Questions			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	5	1	213	2020-01-17 09:53:26	user	\N	\N	g003	f			
72e2c894-f5f5-4c37-95e0-43e0672628c5	a34404a3-309e-4057-8f53-f386d026656b	dba0ca7c-2fe1-45b6-b199-94beefbdefa8	Secret Questions			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	10	1	213	2020-01-17 09:53:26	user	\N	\N	g004	f			
b3226c8e-35eb-4ba1-a554-73424a04d985	ae6ec58b-e9b1-4ddf-a116-4aea79dcefc5	23b2e944-0b08-4ecd-a318-ead86f7dcf3d	Terms & Condition			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	1	\N	213	2020-01-17 09:53:26	user	\N	\N	g001	f			
b4a0b4d0-bbcd-429d-891f-e8c552b1d0c3	ae6ec58b-e9b1-4ddf-a116-4aea79dcefc5	b4b5b749-3701-458f-81cf-7bae4aebc494	Check Box Detail			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	6	1	213	2020-01-17 09:53:26	user	\N	\N	g002	f			
d99244f4-2de0-412d-adad-032d763a89fc	ae6ec58b-e9b1-4ddf-a116-4aea79dcefc5	3c3f4cc6-ec4b-466f-a162-1001903c2811	Version Control			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	9	\N	213	2020-01-17 09:53:26	user	\N	\N	g003	f			
58764588-c84c-4517-bb18-2e948742f2d9	af0f3037-5d24-454f-b81e-ed33e0f5f334	23b2e944-0b08-4ecd-a318-ead86f7dcf3d	Splash Pages			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	1	\N	213	2020-01-17 09:53:26	user	\N	\N	g001	f			
eec5112e-d3cd-4edc-8125-965b69707b04	af0f3037-5d24-454f-b81e-ed33e0f5f334	3c3f4cc6-ec4b-466f-a162-1001903c2811	Version Control			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	8	\N	213	2020-01-17 09:53:26	user	\N	\N	g002	f			
bcf290e6-4c51-4496-bd39-54669509c53b	b29f1ed0-aa1e-4b4c-8053-2808fdb04431	23b2e944-0b08-4ecd-a318-ead86f7dcf3d	Customer Summary Step			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	1	\N	213	2020-01-17 09:53:26	user	\N	\N	g001	f			
f152a688-02a6-4679-a3ed-21e504e735b3	d4882b6c-8e4b-441d-86fc-019c8eb4c232	23b2e944-0b08-4ecd-a318-ead86f7dcf3d	Customer Qualification			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	1	\N	213	2020-01-17 09:53:26	user	\N	\N	g001	f			
dbd9c2e6-36b3-435a-b6e4-32aa54b1bb7c	ec654003-0763-48d3-af08-8a9b053783e4	23b2e944-0b08-4ecd-a318-ead86f7dcf3d	Consent			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	1	\N	213	2020-01-17 09:53:26	user	\N	\N	g001	f			
cf68fb5c-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	ef3e5bc1-6a99-4463-b1ea-4e7e06d0b09e		[{"value":"t","label":"Income : Tier Dropdown"},{"value":"f","label":"Income : Free Text"}]	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	93	92	213	2020-01-23 15:42:10	user	\N	\N	incFreeText	f			
f72730e3-4760-4884-92f9-6d18d96c9e75	d4882b6c-8e4b-441d-86fc-019c8eb4c232	60dda655-621c-47c5-b891-bf526b6beeb7	Please Check API box to Validate Customer Qualification			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	2	1	213	2020-01-17 09:53:26	user	\N	\N	lbChkAPI	f			
3d9cd4af-fc38-4e27-8ac3-b3ba1fe7562b	d4882b6c-8e4b-441d-86fc-019c8eb4c232	180971de-efe3-46e1-b4d6-fd9ccda44f08	Campaign			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	3	1	213	2020-01-17 09:53:26	user	\N	\N	chkCampaign	f			
fa9d869f-9ed4-4ced-b637-12599a956dac	d4882b6c-8e4b-441d-86fc-019c8eb4c232	180971de-efe3-46e1-b4d6-fd9ccda44f08	Blacklist for Lending			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	4	1	213	2020-01-17 09:53:26	user	\N	\N	chkBlacklist	f			
e96d8047-c303-42bd-b326-38a62a7704d1	d4882b6c-8e4b-441d-86fc-019c8eb4c232	afef3e6d-284e-4c3a-8462-1d0b8784ae79				\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	6	5	213	2020-01-17 09:53:26	user	\N	\N	riskLevelList	f			[{"columnname": "Select", "data":"select" },{"columnname": "Risk Level", "data":"level" }]
fa623200-417f-11ea-b77f-2e728ce88125	d4882b6c-8e4b-441d-86fc-019c8eb4c232	6f61a01f-6d49-405c-bbea-b479e20b3bad	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	5	1	213	2020-01-28 10:44:31	user	\N	\N	g002	f	\N	\N	\N
fdef60fc-419b-11ea-b77f-2e728ce88125	a34404a3-309e-4057-8f53-f386d026656b	59dd731e-419b-11ea-b77f-2e728ce88125	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	12	1	213	2020-01-28 14:15:57	user	\N	\N	maxOTP	f	\N	\N	\N
fd59419e-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	ef3e5bc1-6a99-4463-b1ea-4e7e06d0b09e	API	[{"label": "API","value": "api","attr1":"/api/defaultcampaign/xxxxx"}, {"label": "URL","value": "url","attr1": "www.tisco.com"}]	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	18	14	213	2020-01-23 15:48:33	user	\N	\N	defCamp	f	\N	\N	
31cd112a-75b7-4fe9-9f88-352f29af0c9a	d4882b6c-8e4b-441d-86fc-019c8eb4c232	180971de-efe3-46e1-b4d6-fd9ccda44f08	Duplicate CPO			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	7	1	213	2020-01-17 09:53:26	user	\N	\N	dupCPO	f			
15785f74-419e-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	59dd731e-419b-11ea-b77f-2e728ce88125	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	119	1	218	2020-01-28 14:17:29	user	\N	\N	maxOTP	f	\N	\N	\N
fd59324e-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	ef3e5bc1-6a99-4463-b1ea-4e7e06d0b09e		[{"label": "All Day", "value": "all" }, { "label": "Product Cut Off Time", "value": "cutoff" }  ]	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	12	11	213	2020-01-23 15:48:33	user	\N	\N	pcutOffTAllD	f	\N	\N	
"c8da47fe-465c-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	2af4ffce-dd8d-496b-8b42-d4e0adfa126d	CalculationFactors		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	23	20	213	2020-02-03 15:15:45	develop	\N	\N	calFactor	t	\N	\N	
fd59360e-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	43bc8cb8-f7df-429b-a422-241f0ea1683f		[{"formular": "installmentTerm","description": "งวดการผ่อนชำระ(n)"}, {"formular": "installmentDueDate","description": "วันที่เริ่มชำระวันแรก"}, {"formular": "installmentDate","description": "วันที่ชำระของทุกเดือน"}, {"formular": "rateAmount","description": "อัตราดอกเบี้ย(i)"}, {"formular": "limit","description": "จำนวนเงินกู้ที่เลือก (FA/PV)"}, {"formular": "discount","description": "ส่วนลด"}]	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	21	20	213	2020-01-23 15:48:33	user	\N	\N	formularList	f	\N	\N	[{"columnname": "Formular", "data":"formular" },{"columnname": "Description", "data":"description" }]
fd59373a-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	c1b3bf6a-b328-46be-a9ba-d60396025120	Formular		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	24	20	213	2020-01-23 15:48:33	user	\N	\N	formular2	t	\N	\N	
fd59385c-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	2af4ffce-dd8d-496b-8b42-d4e0adfa126d	Round	[{"label": "Please Select", "value": "" }, { "label": "Round Up",  "value": "ru" }, { "label": "Round Down", "value": "rd" }  ]	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	25	20	213	2020-01-23 15:48:33	user	\N	\N	round	t	\N	\N	
fd593ba4-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	2af4ffce-dd8d-496b-8b42-d4e0adfa126d	Decimal		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	26	20	213	2020-01-23 15:48:33	user	\N	\N	decimal	f	\N	\N	
fd594748-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	180971de-efe3-46e1-b4d6-fd9ccda44f08	Calculation (Incom, Expend,Debt)		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	28	27	213	2020-01-23 15:48:33	user	\N	\N	calculationInEx	f	\N	\N	
fd594874-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	d5bf8b97-6cd9-4871-accf-2f19f4e43fbb	Product Package		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	29	1	213	2020-01-23 15:48:33	user	\N	\N	productPackage	f	\N	\N	
fd5949a0-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	c1b3bf6a-b328-46be-a9ba-d60396025120	Header Text (H1)		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	30	29	213	2020-01-23 15:48:33	user	\N	\N	headerText2	f	\N	\N	
fd594be4-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	d5bf8b97-6cd9-4871-accf-2f19f4e43fbb	Payment Method		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	32	1	213	2020-01-23 15:48:33	user	\N	\N	paymentMethod	f	\N	\N	
fd594f36-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	c1b3bf6a-b328-46be-a9ba-d60396025120	Header Text		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	33	32	213	2020-01-23 15:48:33	user	\N	\N	headerText3	f	\N	\N	
fd595076-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	180971de-efe3-46e1-b4d6-fd9ccda44f08	Prompt Pay IDCard		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	34	32	213	2020-01-23 15:48:33	user	\N	\N	promptPayID	f	\N	\N	
fd5951ac-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	180971de-efe3-46e1-b4d6-fd9ccda44f08	Prompt Pay Mobile		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	35	32	213	2020-01-23 15:48:33	user	\N	\N	promptPayMobile	f	\N	\N	
fd5952d8-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	180971de-efe3-46e1-b4d6-fd9ccda44f08	Account No		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	36	32	213	2020-01-23 15:48:33	user	\N	\N	accountNo	f	\N	\N	
fd5953fa-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	6f61a01f-6d49-405c-bbea-b479e20b3bad			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	37	32	213	2020-01-23 15:48:33	user	\N	\N	g005	f	\N	\N	
fd595b02-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	180971de-efe3-46e1-b4d6-fd9ccda44f08	Remark		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	41	32	213	2020-01-23 15:48:33	user	\N	\N	accountRemark	f	\N	\N	
fd595c2e-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	c8729a63-34ba-4dab-9fd8-1084ab018773			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	42	32	213	2020-01-23 15:48:33	user	\N	\N	g006	f	\N	\N	
fd595d64-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	d5bf8b97-6cd9-4871-accf-2f19f4e43fbb	Check CPO		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	43	1	213	2020-01-23 15:48:33	user	\N	\N	checkCPO	f	\N	\N	
fd596020-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	180971de-efe3-46e1-b4d6-fd9ccda44f08	CPO		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	44	43	213	2020-01-23 15:48:33	user	\N	\N	cpoCPO	f	\N	\N	
fd59616a-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	d5bf8b97-6cd9-4871-accf-2f19f4e43fbb	Summary		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	45	\N	213	2020-01-23 15:48:33	user	\N	\N	g007	f	\N	\N	
fd596296-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	23b2e944-0b08-4ecd-a318-ead86f7dcf3d	Summary Detail		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	46	45	213	2020-01-23 15:48:33	user	\N	\N	summaryDetail	f	\N	\N	
fd5963b8-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	c1b3bf6a-b328-46be-a9ba-d60396025120	Header Text		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	47	46	213	2020-01-23 15:48:33	user	\N	\N	headerText4	f	\N	\N	
fd5964da-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	32223be9-8aa5-4bc0-8a7c-d7fee1f29594		[{"label":"เดือน", "parameter":"เดือน","topic":"header"},{"label":"อัราดอกเบี้ย", "parameter":"อัตราดอกเบี้ย","topic":"header"}]	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	48	46	213	2020-01-23 15:48:33	user	\N	\N	summaryList	f	\N	\N	
fd596980-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	180971de-efe3-46e1-b4d6-fd9ccda44f08	SMS		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	50	49	213	2020-01-23 15:48:33	user	\N	\N	smsSMS	f	\N	\N	
fd596ade-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	180971de-efe3-46e1-b4d6-fd9ccda44f08	Email		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	51	49	213	2020-01-23 15:48:33	user	\N	\N	emailEmail	f	\N	\N	
fd596c0a-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	180971de-efe3-46e1-b4d6-fd9ccda44f08	Post		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	52	49	213	2020-01-23 15:48:33	user	\N	\N	postPost	f	\N	\N	
fd596d2c-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	23b2e944-0b08-4ecd-a318-ead86f7dcf3d	Thank You Page		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	53	45	213	2020-01-23 15:48:33	user	\N	\N	thankPage	f	\N	\N	
fd596e4e-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	c8729a63-34ba-4dab-9fd8-1084ab018773	Thank You Message		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	54	53	213	2020-01-23 15:48:33	user	\N	\N	thankMessage	f	\N	\N	
fd596f70-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	c8729a63-34ba-4dab-9fd8-1084ab018773	Footer & Signature		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	55	53	213	2020-01-23 15:48:33	user	\N	\N	footerSignature	f	\N	\N	
fd59709c-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	d5bf8b97-6cd9-4871-accf-2f19f4e43fbb	Failed Case		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	56	45	213	2020-01-23 15:48:33	user	\N	\N	failedCase	f	\N	\N	
fd59757e-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	aa6710a5-5607-4dba-bd22-ecab98c4ecc5			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	58	56	213	2020-01-23 15:48:33	user	\N	\N	errRemark	f	\N	\N	
fd5976f0-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	9ca56fd4-67eb-4a4d-8588-a21444661350			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	59	56	213	2020-01-23 15:48:33	user	\N	\N	errURL	f	\N	\N	
fd5965fc-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	23b2e944-0b08-4ecd-a318-ead86f7dcf3d	Invoice Receive		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	49	45	213	2020-01-23 15:48:33	user	\N	\N	invoiceReceive	f	\N	\N	
fd59461c-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	d5bf8b97-6cd9-4871-accf-2f19f4e43fbb	Calculation (Incom, Expend,Debt)		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	27	1	213	2020-01-23 15:48:33	user	\N	\N	calculationMonth	f	\N	\N	
fd59571a-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	180971de-efe3-46e1-b4d6-fd9ccda44f08	Bank Account		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	38	37	213	2020-01-23 15:48:33	user	\N	\N	bankAccount	f	\N	\N	
fd59738a-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	ef3e5bc1-6a99-4463-b1ea-4e7e06d0b09e		[{value:"em",label:"Error Message"},{value:"url",label:"URL"}]	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	57	56	213	2020-01-23 15:48:33	user	\N	\N	errMsg	f	\N	\N	
fd59588c-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	180971de-efe3-46e1-b4d6-fd9ccda44f08	Account Number		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	39	37	213	2020-01-23 15:48:33	user	\N	\N	accountNumber	f	\N	\N	
fd5959cc-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	180971de-efe3-46e1-b4d6-fd9ccda44f08	Take Photo		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	40	37	213	2020-01-23 15:48:33	user	\N	\N	accountPhoto	f	\N	\N	
cf686aac-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	Citizen ID		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	6	5	213	2020-01-23 15:42:10	user	\N	\N	citizenID	t			
cf686e08-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	Laser Number		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	7	5	213	2020-01-23 15:42:10	user	\N	\N	laserNumber	t			
cf686f52-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	Name		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	8	5	213	2020-01-23 15:42:10	user	\N	\N	name	t			
cf687088-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	Surname		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	9	5	213	2020-01-23 15:42:10	user	\N	\N	surname	t			
cf6871be-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	Date Of Birth		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	10	5	213	2020-01-23 15:42:10	user	\N	\N	dateOfBirth	t			
cf68740c-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	Permanent Address		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	12	11	213	2020-01-23 15:42:10	user	\N	\N	perAddress	t			
cf6875b0-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	Current Address		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	13	11	213	2020-01-23 15:42:10	user	\N	\N	currAddress	t			
cf687a10-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	Occupation		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	15	14	213	2020-01-23 15:42:10	user	\N	\N	occupation	t			
cf687b46-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	Phone Number		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	16	14	213	2020-01-23 15:42:10	user	\N	\N	phoneNumber	t			
cf687c72-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	Default 1		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	17	14	213	2020-01-23 15:42:10	user	\N	\N	default1	t			
cf687da8-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	Default 2		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	18	14	213	2020-01-23 15:42:10	user	\N	\N	default2	t			
cf687ede-3db4-11ea-b77f-2e728ce88125	ed8880d3-e353-48c6-bfd7-debe70ba6c37	180971de-efe3-46e1-b4d6-fd9ccda44f08	Default 3		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	19	14	213	2020-01-23 15:42:10	user	\N	\N	default3	t			
dfe6261b-7305-4964-9747-09d0164551d6	169e986b-5a0a-45c9-8b08-890841b5b23f	c1b3bf6a-b328-46be-a9ba-d60396025120	CheckBoxLabel			12345	\N	\N	\N	\N	\N	\N	\N	\N	\N	6	5	213	2020-01-17 09:53:26	user	\N	\N	chkBoxLabel	f			
fd594ac2-3db8-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	32223be9-8aa5-4bc0-8a7c-d7fee1f29594		[{"label": "จำนวนงวด","parameter": "factor_2_from","topic": "header","unit": "เดือน"}, {"label": "ผ่อนชำระต่อเดือน","parameter": "calAmount1","topic": "body","unit": "บาท"}, {"label": "อัตราดอกเบี้ย","parameter": "rateAmount","topic": "body","unit": "%"}, {"label": "หักส่วนลดต่างๆ","parameter": "discount","topic": "content","unit": "บาท"}]	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	31	29	213	2020-01-23 15:48:33	user	\N	\N	packageList	f	\N	\N	
2546d634-e47c-48ed-b0cb-b83ea1a318a4	ae6ec58b-e9b1-4ddf-a116-4aea79dcefc5	c1b3bf6a-b328-46be-a9ba-d60396025120	Check Box Label			12345	\N	\N	\N	\N	\N	\N	\N	\N	\N	7	6	213	2020-01-17 09:53:26	user	\N	\N	chkBoxLabel	f			
"c8da4592-465c-11ea-b77f-2e728ce88125	723b3819-e1e0-4756-a6f2-50fdaf14d85d	43bc8cb8-f7df-429b-a422-241f0ea1683f		[{code:"calAmount1",name:"ยอดผ่อนต่อเดือน",desc:"ยอดผ่อนต่อเดือน",formular:"(${limit}/((1-(1+(${rateAmount}/12))^(-${installmentTerm}))/(${rateAmount}/12)))",formularDesc:"วงเงินกู้/จำนวนเดือน",round:"up",unit:"100",decimal:2},{code:"calAmount2",name:"ค่าที่ได้จากการคำนวณ",desc:"งวดการผ่อนชำระ",formular:"((${rateAmount}/12)/${factor_2}*${limit})",formularDesc:"วงเงินกู้/จำนวนเดือน",round:"up",unit:"10", decimal:2},{code:"calAmount3",name:"ค่าที่ได้จากการคำนวณ",desc:"งวดการผ่อนชำระ",formular:"((${rateAmount}/12)/${factor_2}*${limit}-${discount})",formularDesc:"วงเงินกู้/จำนวนเดือน",round:"down",unit:"100",decimal:2}]	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	22	20	213	2020-02-03 15:15:42	develop	\N	\N	calFactorsList	f	\N	\N	[{"columnname": "Formular", "data":"code" },{"columnname": "Description", "data":"desc" }]
\.


--
-- TOC entry 3961 (class 0 OID 16427)
-- Dependencies: 200
-- Data for Name: t_shelf_lookup; Type: TABLE DATA; Schema: public; Owner: digitalshelf
--

COPY public.t_shelf_lookup (uuid, lookup_code, lookup_name_th, lookup_name_en, description, group_type, lookup_type, lookup_value, attr1, attr2, attr3, attr4, attr5, attr6, attr7, attr8, attr9, attr10, status, create_at, create_by, update_at, update_by) FROM stdin;
71227b54-4322-11ea-b77f-2e728ce88125	PRO1001	Prospect- Splash Page	Prospect- Splash Page	Prospect- Splash Page	PROCESS_STATE	PROCESS_STATE	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-30 12:38:45	develop	\N	\N
71227e06-4322-11ea-b77f-2e728ce88125	PRO1002	Prospect- Sale Sheet	Prospect- Splash Page	Prospect- Splash Page	PROCESS_STATE	PROCESS_STATE	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-30 12:38:45	develop	\N	\N
71227f5a-4322-11ea-b77f-2e728ce88125	PRO1003	Prospect- Term & Con	Prospect- Splash Page	Prospect- Splash Page	PROCESS_STATE	PROCESS_STATE	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-30 12:38:45	develop	\N	\N
712281b2-4322-11ea-b77f-2e728ce88125	PRO1004	Prospect- Summary Step	Prospect- Splash Page	Prospect- Splash Page	PROCESS_STATE	PROCESS_STATE	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-30 12:38:45	develop	\N	\N
71228306-4322-11ea-b77f-2e728ce88125	PRO1005	Prospect-Customer Info Camera	Prospect- Splash Page	Prospect- Splash Page	PROCESS_STATE	PROCESS_STATE	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-30 12:38:45	develop	\N	\N
71228446-4322-11ea-b77f-2e728ce88125	PRO1006	Prospect- Customer Info	Prospect- Splash Page	Prospect- Splash Page	PROCESS_STATE	PROCESS_STATE	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-30 12:38:45	develop	\N	\N
7122857c-4322-11ea-b77f-2e728ce88125	PRO1007	Prospect- Verify OTP	Prospect- Splash Page	Prospect- Splash Page	PROCESS_STATE	PROCESS_STATE	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-30 12:38:45	develop	\N	\N
712286b2-4322-11ea-b77f-2e728ce88125	PRO1008	Prospect- Questions	Prospect- Splash Page	Prospect- Splash Page	PROCESS_STATE	PROCESS_STATE	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-30 12:38:45	develop	\N	\N
712287fc-4322-11ea-b77f-2e728ce88125	PRO1009	Prospect- Consent	Prospect- Splash Page	Prospect- Splash Page	PROCESS_STATE	PROCESS_STATE	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-30 12:38:45	develop	\N	\N
71228b08-4322-11ea-b77f-2e728ce88125	PRO1010	Prospect- Choose Terms	Prospect- Splash Page	Prospect- Splash Page	PROCESS_STATE	PROCESS_STATE	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-30 12:38:45	develop	\N	\N
71228c52-4322-11ea-b77f-2e728ce88125	PRO1011	Prospect- Confirm Loan Summary	Prospect- Splash Page	Prospect- Splash Page	PROCESS_STATE	PROCESS_STATE	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-30 12:38:45	develop	\N	\N
71228d7e-4322-11ea-b77f-2e728ce88125	PRO1012	Prospect- Payment Method	Prospect- Splash Page	Prospect- Splash Page	PROCESS_STATE	PROCESS_STATE	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-30 12:38:45	develop	\N	\N
ef3e5bc1-6a99-4463-b1ea-4e7e06d0b09e	IN009	radio	radio	radio text right	INPUT	ELEMENT	\N	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-09 10:04:38	develop	\N	\N
71228ea0-4322-11ea-b77f-2e728ce88125	PRO1013	Prospect- Success	Prospect- Splash Page	Prospect- Splash Page	PROCESS_STATE	PROCESS_STATE	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-30 12:38:45	develop	\N	\N
71228fcc-4322-11ea-b77f-2e728ce88125	PRO1014	Wait for Book Loan	Prospect- Splash Page	Prospect- Splash Page	PROCESS_STATE	PROCESS_STATE	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-30 12:38:45	develop	\N	\N
712290f8-4322-11ea-b77f-2e728ce88125	PRO1015	Wait for Transfer	Prospect- Splash Page	Prospect- Splash Page	PROCESS_STATE	PROCESS_STATE	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-30 12:38:45	develop	\N	\N
71229436-4322-11ea-b77f-2e728ce88125	PRO1016	Transfer Fail	Prospect- Splash Page	Prospect- Splash Page	PROCESS_STATE	PROCESS_STATE	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-30 12:38:45	develop	\N	\N
71229594-4322-11ea-b77f-2e728ce88125	PRO1017	Transfer Complete	Prospect- Splash Page	Prospect- Splash Page	PROCESS_STATE	PROCESS_STATE	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-30 12:38:45	develop	\N	\N
712296c0-4322-11ea-b77f-2e728ce88125	PRO1018	Cancel	Prospect- Splash Page	Prospect- Splash Page	PROCESS_STATE	PROCESS_STATE	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-30 12:38:45	develop	\N	\N
b5cfbb6a-4654-11ea-b77f-2e728ce88125	calAmount3	ค่าที่ได้จากการคำนวณ	ค่าที่ได้จากการคำนวณ	ค่าที่ได้จากการคำนวณ	DEF_FORMULAR	LOOKUP_LIST	\N	N	3	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-02-03 14:17:53	develop	\N	\N
b5cfb426-4654-11ea-b77f-2e728ce88125	installmentDueDate	วันที่เริ่มชำระวันแรก	วันที่เริ่มชำระวันแรก	วันที่เริ่มชำระวันแรก	DEF_FACTOR	LOOKUP_LIST	\N	Y	2	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-02-03 14:17:53	develop	\N	\N
b5cfb570-4654-11ea-b77f-2e728ce88125	installmentDate	วันที่ชำระของทุกเดือน	วันที่ชำระของทุกเดือน	วันที่ชำระของทุกเดือน	DEF_FACTOR	LOOKUP_LIST	\N	Y	3	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-02-03 14:17:53	develop	\N	\N
b5cfb16a-4654-11ea-b77f-2e728ce88125	installmentTerm	งวดการผ่อนชำระ(n)	งวดการผ่อนชำระ(n)	งวดการผ่อนชำระ(n)	DEF_FACTOR	LOOKUP_LIST	\N	Y	1	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-02-03 14:17:53	develop	\N	\N
eae51d1c-47f4-11ea-b77f-2e728ce88125	rateAmount	อัตราดอกเบี้ย(i)	อัตราดอกเบี้ย(i)	อัตราดอกเบี้ย(i)	DEF_FACTOR	LOOKUP_LIST	\N	Y	4	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-02-05 15:55:38	develop	\N	\N
eae52136-47f4-11ea-b77f-2e728ce88125	limit	จำนวนเงินกู้ที่เลือก (FA/PV)	จำนวนเงินกู้ที่เลือก (FA/PV)	จำนวนเงินกู้ที่เลือก (FA/PV)	DEF_FACTOR	LOOKUP_LIST	\N	Y	5	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-02-05 15:55:38	develop	\N	\N
eae522bc-47f4-11ea-b77f-2e728ce88125	discount	ส่วนลด	ส่วนลด	ส่วนลด	DEF_FACTOR	LOOKUP_LIST	\N	Y	6	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-02-05 15:55:38	develop	\N	\N
b5cfb908-4654-11ea-b77f-2e728ce88125	calAmount1	ยอดผ่อนต่อเดือน	ยอดผ่อนต่อเดือน	ยอดผ่อนต่อเดือน	DEF_FORMULAR	LOOKUP_LIST	\N	Y	1	(${limit}/((1-(1+(${rateAmount}/12))^(-${installmentTerm}))/(${rateAmount}/12)))	\N	\N	\N	\N	\N	\N	\N	213	2020-02-03 14:17:53	develop	\N	\N
f0b850e6-47f5-11ea-b77f-2e728ce88125	w	Website	Website	Website	DEF_SOURCE	LOOKUP_LIST	\N	Y	1	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-02-05 16:03:47	develop	\N	\N
fc39cbc2-47f3-11ea-b77f-2e728ce88125	t	Income : Tier Dropdown	Income : Tier Dropdown	Income : Tier Dropdown	DEF_INCOME	LOOKUP_LIST	\N	Y	1	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-02-05 15:48:34	develop	\N	\N
fc39ce2e-47f3-11ea-b77f-2e728ce88125	f	Income : Free Text	Income : Free Text	Income : Free Text	DEF_INCOME	LOOKUP_LIST	\N	Y	2	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-02-05 15:48:34	develop	\N	\N
99fd507e-47f2-11ea-b77f-2e728ce88125	CC	Create Consent	Create Consent	Create Consent	DEF_CONSENT	LOOKUP_LIST	\N	Y	2	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-02-05 15:38:46	develop	\N	\N
99fd4c78-47f2-11ea-b77f-2e728ce88125	TC	TISCO Consent	TISCO Consent	TISCO Consent	DEF_CONSENT	LOOKUP_LIST	\N	Y	1	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-02-05 15:38:46	develop	\N	\N
b5cfba3e-4654-11ea-b77f-2e728ce88125	calAmount2	ค่าที่ได้จากการคำนวณ	ค่าที่ได้จากการคำนวณ	ค่าที่ได้จากการคำนวณ	DEF_FORMULAR	LOOKUP_LIST	\N	Y	2	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-02-03 14:17:53	develop	\N	\N
f0b85546-47f5-11ea-b77f-2e728ce88125	m	Mobile	Mobile	Mobile	DEF_SOURCE	LOOKUP_LIST	\N	Y	2	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-02-05 16:03:47	develop	\N	\N
c1b3bf6a-b328-46be-a9ba-d60396025120	IN001	input text	input text	input text left	INPUT	ELEMENT	\N	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-09 10:04:38	develop	\N	\N
7e6287df-63b6-4f75-9a9e-4ce5343143b2	IN002	input date	input date	input text มี icon ปฎิทิน & text left	INPUT	ELEMENT	\N	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-09 10:04:38	develop	\N	\N
48acf7d7-a546-41f1-9a21-e04f3a97f2c7	IN003	file	file	input type file accept JPG,PNG & text left	INPUT	ELEMENT	\N	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-09 10:04:38	develop	\N	\N
2af4ffce-dd8d-496b-8b42-d4e0adfa126d	IN004	select	select	select box text left	INPUT	ELEMENT	\N	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-09 10:04:38	develop	\N	\N
c8729a63-34ba-4dab-9fd8-1084ab018773	IN005	textarea	textarea	textarea with style & title top left	INPUT	ELEMENT	\N	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-09 10:04:38	develop	\N	\N
180971de-efe3-46e1-b4d6-fd9ccda44f08	IN006	checkbox	checkbox	checkbox text right	INPUT	ELEMENT	\N	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-09 10:04:38	develop	\N	\N
1a5a166e-f59c-47f9-bdab-ce24de2435da	IN007	checkbox	checkbox	checkbox text right and input right	INPUT	ELEMENT	\N	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-09 10:04:38	develop	\N	\N
61c695c2-7f2f-4bd3-8094-aa02eb20040b	IN008	checkbox	checkbox	checkbox text right	INPUT	ELEMENT	\N	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-09 10:04:38	develop	\N	\N
9ca56fd4-67eb-4a4d-8588-a21444661350	IN010	radio	radio	radio text right & input right	INPUT	ELEMENT	\N	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-09 10:04:38	develop	\N	\N
3ff504d3-c386-4cb2-9fab-c983b41a2113	IN011	radio	radio	radio time period	INPUT	ELEMENT	\N	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-09 10:04:38	develop	\N	\N
23b2e944-0b08-4ecd-a318-ead86f7dcf3d	G001	panel	panel	panel มี enable/disable	GROUP	ELEMENT	\N	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-09 10:04:38	develop	\N	\N
d5bf8b97-6cd9-4871-accf-2f19f4e43fbb	G002	panel	panel	panel ไม่มี enable/disable	GROUP	ELEMENT	\N	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-09 10:04:38	develop	\N	\N
a5d5ea52-bba8-4d6a-bb3a-6ac979d35dbb	G003	panel	panel	panel มี enable/disable และ show/hide	GROUP	ELEMENT	\N	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-09 10:04:38	develop	\N	\N
dba0ca7c-2fe1-45b6-b199-94beefbdefa8	G004	panel	panel	panel มี checkbox ด้านบนซ้าย	GROUP	ELEMENT	\N	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-09 10:04:38	develop	\N	\N
ccac6659-1281-41f9-a4e9-0f20765d74a4	G005	panel	panel	panel มี input seq ด้านบนซ้าย	GROUP	ELEMENT	\N	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-09 10:04:38	develop	\N	\N
aad9004f-4478-4dd7-ba15-1d29d4ad8b11	G006	fieldset	fieldset	fieldset no title	GROUP	ELEMENT	\N	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-09 10:04:38	develop	\N	\N
b4b5b749-3701-458f-81cf-7bae4aebc494	G007	fieldset	fieldset	fieldset title top left	GROUP	ELEMENT	\N	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-09 10:04:38	develop	\N	\N
3c3f4cc6-ec4b-466f-a162-1001903c2811	G008	panel	panel	panel show/hide	GROUP	ELEMENT	\N	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-09 10:04:38	develop	\N	\N
43bc8cb8-f7df-429b-a422-241f0ea1683f	T001	table	table	table default	TABLE	ELEMENT	\N	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-09 10:04:38	develop	\N	\N
5371acb6-a23e-45c9-a266-aeb1c27d10aa	B001	button	button	button	BUTTON	ELEMENT	\N	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-09 10:04:38	develop	\N	\N
6232c264-49d8-4e71-9fce-fa8a3b257de7	B002	button	button	button	BUTTON	ELEMENT	\N	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-09 10:04:38	develop	\N	\N
da208c49-66d4-486d-936d-530d211f2fbd	P001	preview	preview	preview page	PREVIEW	ELEMENT	\N	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-09 10:04:38	develop	\N	\N
29b6768c-7539-4b8d-8c37-3b3ad68746dc	IN012	textarea	textarea	textarea title top left	INPUT	ELEMENT	\N	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-10 17:32:26	develop	\N	\N
afef3e6d-284e-4c3a-8462-1d0b8784ae79	T002	table	table	TABLE edit data	TABLE	ELEMENT	\N	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-10 17:32:26	develop	\N	\N
db4dc5de-8814-4a89-b349-9ab3b8ad8746	T003	table	table	TABLE edit data2	TABLE	ELEMENT	\N	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-10 17:32:26	develop	\N	\N
60dda655-621c-47c5-b891-bf526b6beeb7	IN013	lagel	lagel	Label no input field	LABEL	ELEMENT	\N	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-10 17:32:26	develop	\N	\N
cdb2b5be-1d67-49a6-b42c-af7cad300abf	IN014	input time	input time	input time text left	INPUT	ELEMENT	\N	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-10 17:32:26	develop	\N	\N
6f61a01f-6d49-405c-bbea-b479e20b3bad	G009	group no-line	group no-line	group no-line	GROUP	ELEMENT	\N	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-10 17:32:26	develop	\N	\N
603b12cd-d820-4d54-8fd7-a9f308ce7557	G010	panel มี checkbox ด้านบนซ้าย show/hide	panel left checkbox and show/hide	panel มี checkbox ด้านบนซ้าย show/hide	GROUP	ELEMENT	\N	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-13 15:06:42	develop	\N	\N
32223be9-8aa5-4bc0-8a7c-d7fee1f29594	IN016	button add Radio List	button add Radio List	button add Radio List	INPUT	ELEMENT	\N	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-23 15:09:56	develop	\N	\N
aa6710a5-5607-4dba-bd22-ecab98c4ecc5	IN015	texarea no title	texarea no title	texarea no title	INPUT	ELEMENT	\N	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-13 15:06:42	develop	\N	\N
70d96771-dae9-4488-a5de-f8a7b431c4dc	IN017	checkbox & inputtext	checkbox & inputtext	checkbox ไม่มี label เป็น inputtext แทน	INPUT	ELEMENT	\N	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-23 15:08:08	develop	\N	\N
59dd731e-419b-11ea-b77f-2e728ce88125	IN018	hidden field	hidden field	hidden field	INPUT	ELEMENT	\N	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-28 14:15:35	develop	\N	\N
bdc92574-47f6-11ea-b77f-2e728ce88125	all	All Day	All Day	All Day	DEF_TIME	LOOKUP_LIST	\N	Y	1	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-02-05 16:08:28	develop	\N	\N
bdc92a4c-47f6-11ea-b77f-2e728ce88125	cutoff	Product Cut Off Time	Product Cut Off Time	Product Cut Off Time	DEF_TIME	LOOKUP_LIST	\N	Y	2	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-02-05 16:08:28	develop	\N	\N
94b89358-47f7-11ea-b77f-2e728ce88125	Y	Yes	Yes	Yes	DEF_MANDATORY	LOOKUP_LIST	\N	Y	1	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-02-05 16:13:53	develop	\N	\N
94b89894-47f7-11ea-b77f-2e728ce88125	N	No	No	No	DEF_MANDATORY	LOOKUP_LIST	\N	Y	2	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-02-05 16:13:53	develop	\N	\N
fc049ace-47f8-11ea-b77f-2e728ce88125	w	Working Day	Working Day	Working Day	DEF_DAY	LOOKUP_LIST	\N	Y	1	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-02-05 16:23:43	develop	\N	\N
fc049d8a-47f8-11ea-b77f-2e728ce88125	e	EveryDay	EveryDay	EveryDay	DEF_DAY	LOOKUP_LIST	\N	Y	2	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-02-05 16:23:43	develop	\N	\N
48faa7f6-47f9-11ea-b77f-2e728ce88125	em	Error Message	Error Message	Error Message	DEF_ERROR	LOOKUP_LIST	\N	Y	1	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-02-05 16:25:52	develop	\N	\N
48faaa6c-47f9-11ea-b77f-2e728ce88125	url	URL	URL	URL	DEF_ERROR	LOOKUP_LIST	\N	Y	2	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-02-05 16:25:52	develop	\N	\N
5a1ec254-47fb-11ea-b77f-2e728ce88125	ru	Round Up 10	Round Up 10	Round Up 10	DEF_ROUND	LOOKUP_LIST	\N	Y	1	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-02-05 16:43:39	develop	\N	\N
5a1ec754-47fb-11ea-b77f-2e728ce88125	ru	Round Up 100	Round Up 100	Round Up 100	DEF_ROUND	LOOKUP_LIST	\N	Y	2	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-02-05 16:43:39	develop	\N	\N
5a1ec98e-47fb-11ea-b77f-2e728ce88125	rd	Round Down 10	Round Down 10	Round Down 10	DEF_ROUND	LOOKUP_LIST	\N	Y	3	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-02-05 16:43:39	develop	\N	\N
5a1ecaec-47fb-11ea-b77f-2e728ce88125	rd	Round Down 100	Round Down 100	Round Down 100	DEF_ROUND	LOOKUP_LIST	\N	Y	4	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-02-05 16:43:39	develop	\N	\N
8aa67042-47fc-11ea-b77f-2e728ce88125	api	API	API	API	DEF_CAMPAIGN	LOOKUP_LIST	\N	Y	1	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-02-05 16:49:21	develop	\N	\N
8aa673bc-47fc-11ea-b77f-2e728ce88125	url	URL	URL	URL	DEF_CAMPAIGN	LOOKUP_LIST	\N	Y	2	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-02-05 16:49:21	develop	\N	\N
\.


--
-- TOC entry 3962 (class 0 OID 16434)
-- Dependencies: 201
-- Data for Name: t_shelf_product; Type: TABLE DATA; Schema: public; Owner: digitalshelf
--

COPY public.t_shelf_product (uuid, prod_code, prod_name, business_line, business_dept, company, prod_type, prod_url, active_date, end_date, prod_day, prod_time, campaign_id, campaign_name, link_channel, product_channel, attr1, attr2, attr3, attr4, attr5, attr6, attr7, attr8, attr9, attr10, status, create_at, create_by, update_at, update_by) FROM stdin;
e0c80f8b-627e-48b4-9384-87d00dca5274	TOPUPEASY	สินเชื่อเพิ่มวงเงิน (Easy)	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N
\.


--
-- TOC entry 3963 (class 0 OID 16441)
-- Dependencies: 202
-- Data for Name: t_shelf_product_attach; Type: TABLE DATA; Schema: public; Owner: digitalshelf
--

COPY public.t_shelf_product_attach (uuid, dtl_uuid, file_type, file_name, file_value, description, attr1, attr2, attr3, attr4, attr5, attr6, attr7, attr8, attr9, attr10, status, create_at, create_by, update_at, update_by) FROM stdin;
\.


--
-- TOC entry 3964 (class 0 OID 16448)
-- Dependencies: 203
-- Data for Name: t_shelf_product_dtl; Type: TABLE DATA; Schema: public; Owner: digitalshelf
--

COPY public.t_shelf_product_dtl (uuid, lk_uuid, trn_uuid, lk_code, lk_label, lk_value, lk_require, lk_validation, lk_description, business_dept, company, prod_type, prod_url, active_date, end_date, prod_day, prod_time, campaign_id, campaign_name, link_channel, product_channel, attr1, attr2, attr3, attr4, attr5, attr6, attr7, attr8, attr9, attr10, status, create_at, create_by, update_at, update_by, dtl_status) FROM stdin;
aa1da396-7a96-45a8-9401-2e0d48cf78c7	\N	7d09ce2f-657a-4a9c-b562-0fa4e2b066b8	template	\N	77dcd446-436d-404b-82bc-6ce8a925cec2	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
27e9935a-3aef-458a-afa3-580daf83eafe	\N	7d09ce2f-657a-4a9c-b562-0fa4e2b066b8	productChannel	\N		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
eb28d6eb-0604-422b-8162-33da16483878	\N	7d09ce2f-657a-4a9c-b562-0fa4e2b066b8	prodType	\N	pay	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
ac92a58d-a8c2-4d88-b06f-eedcff5daa43	\N	7d09ce2f-657a-4a9c-b562-0fa4e2b066b8	linkChannel	\N	https://web.facebook.com/TISCOFinancialGroup/?_rdc=1&_rdr	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
eec3a084-f928-42a1-a50c-2a689d504a82	\N	7d09ce2f-657a-4a9c-b562-0fa4e2b066b8	prodUrl	\N		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
7b7acb11-3ecc-4b0c-82f9-5e1291d05244	\N	7d09ce2f-657a-4a9c-b562-0fa4e2b066b8	prodTime	\N	20:00	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
4b6407bc-2b6d-4eb6-a4f8-f1f3cc82c58e	\N	7d09ce2f-657a-4a9c-b562-0fa4e2b066b8	businessDept	\N		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
d8a5d67a-6ada-4746-ba34-583ccb4a6e1b	\N	7d09ce2f-657a-4a9c-b562-0fa4e2b066b8	company	\N	10	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
c4ece405-d356-4366-a72f-5856be79d2ca	\N	b35522f7-db38-48fb-8164-13f1dd006103	imageType	Type		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
034cea70-4f86-4549-b9d0-9f5ce4ee434d	\N	b35522f7-db38-48fb-8164-13f1dd006103	btnUpload	Upload		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
2fa8bb96-d627-4544-92f1-25d531507056	\N	b35522f7-db38-48fb-8164-13f1dd006103	btnText	Botton Text	เริ่ม	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
743b8a00-b0ef-4dc2-839d-5ec3c607da1b	\N	b35522f7-db38-48fb-8164-13f1dd006103	activeDate	Active Date	2020-01-01 00:00:000	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
2f539538-ea44-4234-8a61-f8d82387e5cc	\N	b35522f7-db38-48fb-8164-13f1dd006103	verSplashList			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
74c18e40-3eb0-4fb1-b12c-5ccb7e5c227a	\N	492db4c5-e88f-4f67-b81d-9b19a01758b6	headerText	Header Text	CUSTOMER SUMMARY STEP	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
69438442-6107-448b-9f03-f425480ec422	\N	492db4c5-e88f-4f67-b81d-9b19a01758b6	imgUpload	image		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
e6c3d549-1802-42f8-b221-4ca204ad3908	\N	492db4c5-e88f-4f67-b81d-9b19a01758b6	imageType	Type		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
770bd3da-572f-4016-a43b-3f8a09e8be85	\N	492db4c5-e88f-4f67-b81d-9b19a01758b6	btnUpload	Upload		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
3cc388e0-e642-4100-a5cc-b11fb9e406cd	\N	0c7259bd-aa2b-471c-a23f-366583d79e91	headerText	Header Text	เงื่อนไขการให้บริการ	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
3276737b-db3e-42de-9763-a2b441a3ba75	\N	0c7259bd-aa2b-471c-a23f-366583d79e91	activeDate	Active Date	2020-01-01 00:00:000	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
b5c1d245-a316-4637-9c18-a7af2ea026e4	\N	0c7259bd-aa2b-471c-a23f-366583d79e91	version	Version		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
53eca270-f38f-419e-874f-0e6f8560926e	\N	0c7259bd-aa2b-471c-a23f-366583d79e91	radioMandatory	Mandatory (Yes/No)	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
5a3cddb5-6a16-43b7-95d2-25c8d0781215	\N	0c7259bd-aa2b-471c-a23f-366583d79e91	verTermsNConditionList			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
35de811c-6a1a-445e-9cc0-717165b938b1	\N	d4fb5258-e64e-4861-b59b-1c49747a2a06	g002	OTP (Retail)	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
887f7fa1-e3c3-42fc-a990-95671143c0c5	\N	d4fb5258-e64e-4861-b59b-1c49747a2a06	g003	Secret Questions	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
3fdc7c48-a69b-4310-8e71-00b79b82d6b8	\N	d4fb5258-e64e-4861-b59b-1c49747a2a06	secretHeaderText	Header Text		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
0332d23e-a899-4143-afd3-efc006e8420f	\N	d4fb5258-e64e-4861-b59b-1c49747a2a06	g004	Secret Questions	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
9a2b450d-fee0-4b47-a7a2-d9b61364ff6e	\N	b35522f7-db38-48fb-8164-13f1dd006103	g001	Splash Pages	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
5721e7ec-736a-440b-9b9d-735c13afe8c9	\N	d4fb5258-e64e-4861-b59b-1c49747a2a06	numberCorrectAnswer	Number of Correct Answers	1	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
1687f5e1-038d-4661-8841-319430115a8e	\N	d4fb5258-e64e-4861-b59b-1c49747a2a06	otpHeaderText	Header Text	คำถาม	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
6950d948-207d-4732-bc37-453f5b314176	\N	d4fb5258-e64e-4861-b59b-1c49747a2a06	otpNumberQuestion	Number of Questions	2	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
335629b7-bd10-4a40-b165-a5412ae71ae5	\N	492db4c5-e88f-4f67-b81d-9b19a01758b6	g001	Customer Summary Step	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
5f8bc82e-05e5-4ebf-b53a-e2c50e7beb9b	\N	d4fb5258-e64e-4861-b59b-1c49747a2a06	maxOTP		6	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
5d15f1d9-0340-46b0-9a39-30465324eceb	\N	183b450d-d189-4199-b320-cc8ecfa360f7	radioConsent		CC	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
b2692653-66ff-431d-92ed-9be3105ee36e	\N	183b450d-d189-4199-b320-cc8ecfa360f7	consentName	Concent Name	c286b59e-423e-11ea-b77f-2e728ce88125	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
d8d283f4-13e2-4ec2-a5ed-316310ccd316	\N	7bd626a4-1678-495f-9f1b-f5f7a7562e89	thankPage	Thank You Page	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
5a1b8775-561f-4886-b890-a44601aea5c4	\N	7d09ce2f-657a-4a9c-b562-0fa4e2b066b8	campaignName	\N	TOPUPEASY 02/2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
f18d4126-90c6-411c-a583-f2d2fcf936de	\N	183b450d-d189-4199-b320-cc8ecfa360f7	headerText	Header Text		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	1	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
cfeb3380-60b2-4131-9e38-40a2df992efc	\N	b35522f7-db38-48fb-8164-13f1dd006103	imgUpload	image	[{value:"data:image/jpeg;base64,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",fileName:"tiscobanner.png",type:"w"},\n{value:"data:image/jpeg;base64,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",fileName:"banner.jpeg",type:"m"}]	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
e07c26cb-6f74-45e9-9d87-424fbfb5fca8	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	headerText	Header Text(H1)	ข้อมูลส่วนตัว	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
ba33fc00-ef87-446c-a64b-2c9693b08e63	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	g002	Citizen Information	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
6009c502-56cd-454d-9a8b-323ace7ee7d4	\N	0c7259bd-aa2b-471c-a23f-366583d79e91	chkBoxLabel	Check Box Label	ยอมรับเงื่อนไขการให้บริการ	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	2C7E0091-4D6F-49AC-9C57-AD2A2C8D8A81	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
7cf3ba73-d517-4761-bf07-706df6e59d1f	\N	183b450d-d189-4199-b320-cc8ecfa360f7	content	Content		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N		\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
e3de3708-524b-4384-bf89-877a7218e174	\N	492db4c5-e88f-4f67-b81d-9b19a01758b6	imgSplashList		[{value:"data:image/jpeg;base64,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",type:"w",fileName:"sumweb"},{value:"data:image/jpeg;base64,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7w/tWX/QNhf/AAT/APbGVq/iG41hEhdIYIYyWWKCPy03HqxGTk/jVKKj/wAE4sTjqmKjGnKMIQi21CnHljd9bXepT0nSLjWbhba2UszHk9lHdmPYCm2oq7MMNhqmLqKjRV2930S7vsj6d0LRotCtEtIf4Rlm7sx6sf6egribu7n7FhMLDBUo0KfTd9292a9Sd4UAFABQAUAFABQAUAFABQAUAFABQAUAFABQAUAFABQAUAFABQAUAFABQAUAFABQBDcW0d3GYZlDxsMFWGQae2xnOEasXTqJSi909jybW/hgHYy6W4XPPlSZx/wFxkj6EH6it1U6M+FxfD9254KSX9yW3yl/mvmef3Pg7V7Q4a1lb3jHmD/xzd+tacy7nytTK8bSdpUZvziudf8AktyCLwtq0xwtpcA/7UbIPzcKP1p8yXVGUcvxk3aNCr84OP4ysjr9J+GV7cENfMtsndVId/px8o+uT9KzdRLY9/DZBXqNPFSVOPZPml+Hur72ew6PodpocXk2iBB/Ex5Zj6sep+nSsG29z7/C4SjgoezoRt3fV+rNapO8KACgAoAKACgAoAKACgAoAKACgAoAKACgAoAKACgAoAKACgAoAKACgAoAKACgAoAKACgAoAKACgAoAKACgAoAKACgAoAKACgAoAKACgAoAKACgAoAKACgAoAKACgAoAKACgAoAKACgAoAKACgAoAKACgAoAKACgAoAKACgAoAKACgAoAKACgAoAKACgAoAKACgAoAKACgAoAKACgAoAKACgAoAKACgAoAKACgAoAKACgAoAKACgAoAKACgAoAKACgAoAKACgAoAKACgAoAKACgAoAKACgAoAKACgAoAKACgAoAKAP/9k=",type:"m",fileName:"summobile"}]	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
260aa4c0-380d-4cfb-8bd9-31a85dffe5b9	\N	0c7259bd-aa2b-471c-a23f-366583d79e91	termsNCondition	Terms & Condition	1. ข้อกำหนดและเงื่อนไขในการบริการ ทีเคที	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	9BA78E04-9D8B-44FA-93EF-85AC60CFA3C2	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
28feace5-7572-4d71-8f3f-7925cef09aca	\N	7d09ce2f-657a-4a9c-b562-0fa4e2b066b8	groupProduct	\N	H6	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
526c260c-f594-45a8-8b34-0d248a7b8118	\N	7d09ce2f-657a-4a9c-b562-0fa4e2b066b8	prodDay	\N	w	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
c242172f-76e9-45f8-94c4-cb9e1565213b	\N	d4fb5258-e64e-4861-b59b-1c49747a2a06	otpTemplateID	Template ID	alpha_TISCO_OTP_22005_1_TH	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
e1624c68-f270-4942-b236-1ae5d00a6dc2	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	headerText2	Header Text(H2)	ข้อมูลลูกค้า	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
61a4c706-aac5-40ba-8aa4-d107b2b36e52	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	laserNumber	Laser Number	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
86329d1f-5fd7-42b0-a090-bed1d969be6b	\N	0c7259bd-aa2b-471c-a23f-366583d79e91	g001	Terms & Condition	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
ecf9db67-09dc-49e2-a791-cdb9ec54a14c	\N	d4fb5258-e64e-4861-b59b-1c49747a2a06	g001	Authentication	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
012bca51-f97a-4711-89c3-59d2319e4e3c	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	dateOfBirth	Date Of Birth	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
2656b980-6893-43ec-9243-a632ecd50139	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	currAddress	Current Address	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
b449df7a-fc79-4a90-a7eb-e86c053a93e4	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	occupation	Occupation	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
38456683-2945-410e-ac31-8d3c562bad69	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	phoneNumber	Phone Number	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
8ce96a4b-ef4e-4558-8faf-665d18c7bbc0	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	default1	Default 1	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
59557aa8-8d64-47dc-bed0-9b279338170f	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	default2	Default 2	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
838c6094-d4f0-40b3-9baf-380191d59b18	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	default3	Default 3	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
492b5fe5-d300-447d-b565-5c21381fe314	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	addrHeaderText	Header Text (H2)	โปรดระบุข้อมูลเกี่ยวกับบ้าน	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
8292ad77-cb5d-4758-98c2-aa250ea5e0ae	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	citizenID	เลขบัตรประชาชน	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
c5f13966-30a9-4b7f-b8db-0cdbfa73c2b3	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	perAddress	Permanent Address	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
642a1e80-58a4-403b-96e3-1c863fe92e90	\N	d4fb5258-e64e-4861-b59b-1c49747a2a06	imgSecretQuestionList		["1","2"]	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
2914e4fe-3386-4618-ad8f-556755b73a73	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	houseID	เลขรหัสประจำบ้าน	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
8097b58e-9d6f-4a70-be1e-1133218f1ea9	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	g011	Vehicle Information	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
9da8a2a9-3685-4b23-bc4f-8a940fee1a09	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	name	Name	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
ba8b16e6-fdd0-4ffe-a031-0bdb69f49b86	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	citizenID	Citizen ID	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
48a74c03-becc-4b31-a76c-2f48193bd62a	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	name	ชื่อ	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
7031b779-79e7-4402-9433-2533340e7a44	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	g008	Address Information	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
8079204d-d413-40e3-a93f-cfcdb6e93615	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	province	จังหวัด	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
30d9c230-728f-4323-9286-59f7b8a5a178	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	addressType	ประเภทบ้าน	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
783146fd-628b-41b1-a687-b4f86d7fcada	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	lastName	นามสกุล	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
19578429-225d-4f5b-9094-812b1ac3ca1f	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	nationality	สัญชาติ	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
0f6f667c-8016-4008-9779-d1e819ff7d4b	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	gender	เพศ	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
6eb82461-f9b4-4e0a-bcf6-dc9fb4d9531e	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	address	ที่อยู่	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
14ced592-c59d-481a-bbd6-e0226b98ca61	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	subDistrict	แขวง	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
2bbb7309-ea8b-4854-a493-5d03789d7986	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	district	เขต	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
293b8726-3af7-45aa-8497-d8bd6ff83c82	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	ocr	ถ่ายรูปบัตรประชาชนด้านหน้า	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
160a281d-77ba-4264-9437-d89016b2e7b7	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	ocrLaserId	ถ่ายรูปบัตรประชาชนด้านหลัง	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
04ffc913-63f3-4110-a7d3-9d70d111c1d2	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	dopa	ตรวจ DOPA	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
89e46f21-4fe7-46c9-b8f1-ad86958aac60	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	g014	Income Information	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
28a4abf7-4462-4c95-ae4b-e3906a31b422	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	incHeaderText	Header Text (H2)	โปรดระบุรายได้และค่าใช้จ่าย	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
0d599708-686e-43b9-a061-f25629793517	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	lblExpend	Expend		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
dcb3b5f6-809f-46f1-b455-6459c5f94f81	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	lblDebt	Debt		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
947b3132-8df7-4639-8536-fda4e20ac2d2	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	g017	Other KYC Section	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
db7220bc-9d95-46b4-90dc-1d415c6bff41	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	txtLabel	Text Label	ท่านมีถิ่นที่อยู่/ประกอบอาชีพ/มีสถานประกอบการ/มีแหล่งเงินได่้หรืออย่างใดอย่างหนึ่งอยู่ในต่างประเทศหรือในพื้นที่ชายแดนของประเทศไทยหรือไม่?	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
e6208c26-917f-4d69-a923-574179ad241b	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	radioList	Radio List	[{label:"ไม่ใช่ , ข้าพเจ้าไม่ได้อยู่ในพื้นที่นี้",param:"Y"},{label:"ใช่ , ข้าพเจ้าอยู่ในพื้นที่นี้",param:"N"}]	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
ce8c1e2a-faae-470f-af80-b3f949a39de3	\N	7d9fb9cc-c733-406e-be20-a43b5e1fbd9f	lbChkAPI	Please Check API box to Validate Customer Qualification		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
cca5f6d6-22a7-4735-8d28-c28667fcdb78	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	maxDOPA		3	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
4e2aa25a-3f67-4003-95aa-e1b7cd68720f	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	chkExpend3	ค่าใช้จ่าย 3	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
a62f0712-45bd-4580-9174-b8bb822e0985	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	chkExpend4	ค่าใช้จ่าย 4	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
30be12d9-184c-4af5-b27c-bdbbebd30a7a	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	carRegisteredYear	รุ่นปี	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
5fa5c923-4867-4cf7-8a45-95cfaeaf0c52	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	carColor	สี	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
34f9b700-a573-47c3-8502-3cab4b3c2395	\N	7d9fb9cc-c733-406e-be20-a43b5e1fbd9f	dupCPO	Duplicate CPO	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
7e225103-85bb-4590-8b23-b0764cbd644d	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	chkExpend5	ค่าใช้จ่าย 5	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
7e9f06c8-bc8d-42ee-8dbb-c871dc289b2a	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	chkDebt1	ภาระหนี้สิน 1	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
08354460-ccc9-4c0d-b013-485e03548cad	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	chkDebt3	ภาระหนี้สิน 3	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
c12f0630-8387-4e0c-934d-a6f3fa8ba83e	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	chkDebt4	ภาระหนี้สิน 4	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
9d7ee3fd-3cad-4918-8a37-b4fe6d2ae768	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	chkDebt5	ภาระหนี้สิน 5	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
55535e82-79e6-4ca6-8f2e-6f3d4ef77940	\N	7d9fb9cc-c733-406e-be20-a43b5e1fbd9f	chkCampaign	Campaign	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
c90c1178-478a-42c3-98b5-1c6b315f9356	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	maxOTP		6	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	218
6c5925df-7af0-4c51-ae5b-3af89ade6c4b	\N	183b450d-d189-4199-b320-cc8ecfa360f7	g001	Consent	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
603871fb-e575-4d89-87b0-06363670321e	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	g001	Customer Information	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
611120d6-9da1-4548-b600-5be2a782db81	\N	7d9fb9cc-c733-406e-be20-a43b5e1fbd9f	g001	Customer Qualification	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
6844d63e-2a31-4171-8c58-188262525dce	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	chkIncome1	รายได้ที่ 1	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
1a39d916-8f29-4bef-b4eb-ef7557ca3294	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	chkIncome2	รายได้ที่ 2	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
74f6fefb-8a5f-4279-be53-333a434b1f70	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	chkIncome3	รายได้ที่ 3	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
b071fee1-ef88-472c-bab4-27146cdb367a	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	chkIncome4	รายได้ที่ 4	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
ef718073-d7ee-47c7-a844-2d6310bbe4b7	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	chkIncome5	รายได้ที่ 5	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
054d25d4-59a0-4268-80b2-269c349cfa46	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	chkExpend1	ค่าใช้จ่าย 1	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
6930c600-38b0-4f1b-b60f-e0d023047efe	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	chkExpend2	ค่าใช้จ่าย 2	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
ab812749-0c6e-4918-9339-2cbbbe2c8359	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	chkDebt2	ภาระหนี้สิน 2	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
1026a009-c4bf-45a6-a3c4-0ce97e517c19	\N	7bd626a4-1678-495f-9f1b-f5f7a7562e89	g001	Product Info	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
e74da517-9a65-42ba-ac05-929f670931e0	\N	7bd626a4-1678-495f-9f1b-f5f7a7562e89	g002	Product Info Configuration	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
e8ff0eb0-97ab-42fd-bee5-534d134ce0a5	\N	7bd626a4-1678-495f-9f1b-f5f7a7562e89	headerText	Header Text	เลือกวงเงินสินเชื่อ	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
356f31da-11f7-455f-b48f-67ba2dd6784e	\N	7bd626a4-1678-495f-9f1b-f5f7a7562e89	productType	Product Type	pay	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
2df7d9a6-31fe-40f1-b7c5-94a7d8216f2e	\N	7bd626a4-1678-495f-9f1b-f5f7a7562e89	pcutOffTSpec			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
fa57edab-fcef-4113-8b14-9cf7d645821a	\N	7bd626a4-1678-495f-9f1b-f5f7a7562e89	formularList			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
bc450124-4bb5-4b61-9bc6-a55d10dc2883	\N	7bd626a4-1678-495f-9f1b-f5f7a7562e89	formular2	Formular		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
42cfc923-45fe-439b-aa3d-3025415cd23a	\N	7bd626a4-1678-495f-9f1b-f5f7a7562e89	round	Round	up	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
d09399af-3708-4996-9257-cf96a484a719	\N	7bd626a4-1678-495f-9f1b-f5f7a7562e89	decimal	Decimal	2	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
3336a0a8-91cd-4a72-9c72-60b81c7f9fb9	\N	7bd626a4-1678-495f-9f1b-f5f7a7562e89	headerText2	Header Text (H1)	เลือกอัตราผ่อนชำระ	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
6c1f71e8-e19c-4353-96e6-a2825199b322	\N	7bd626a4-1678-495f-9f1b-f5f7a7562e89	headerText3	Header Text	ช่องทางการรับเงิน	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
248524f7-5ee0-4e3e-8d65-a5dd6b945b5c	\N	7bd626a4-1678-495f-9f1b-f5f7a7562e89	promptPayID	Prompt Pay IDCard	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
b4d0994d-ae90-4a08-b008-154770c7e0fc	\N	7bd626a4-1678-495f-9f1b-f5f7a7562e89	promptPayMobile	Prompt Pay Mobile	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
84560105-f9ed-465e-b477-609260b2786e	\N	7bd626a4-1678-495f-9f1b-f5f7a7562e89	accountNo	Account No	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
9808511f-2dbd-4f33-b7d8-ff8744fa887f	\N	7bd626a4-1678-495f-9f1b-f5f7a7562e89	g006		Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
f3cccabf-4c83-4cf4-8bad-82b070767072	\N	7bd626a4-1678-495f-9f1b-f5f7a7562e89	summaryDetail	Summary Detail	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
5afb1789-bf88-4a72-8a50-83abc9c1a1cb	\N	7bd626a4-1678-495f-9f1b-f5f7a7562e89	headerText4	Header Text	สรุปวงเงินสินเชื่อ	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
e4973604-5c2e-491f-951f-c7e52ac01e95	\N	7bd626a4-1678-495f-9f1b-f5f7a7562e89	thankMessage	Thank You Message	<p style="text-align: center;">เงินกู้จะถูกโอนเข้าบัญชีปลายทางที่ท่านระบุ และสำเนาเอกสารยืนยันการกู้จะถูกส่งตามที่อยู่ที่ท่านแจ้งไว้</p>	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
8f8d5894-040a-4d17-b53a-f4f3d5c765f5	\N	7bd626a4-1678-495f-9f1b-f5f7a7562e89	footerSignature	Footer & Signature	สอบถามเพิ่มเติม 02 633 6000 กด 3	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
38c84472-52f7-415b-8fa4-355ca41609b5	\N	7bd626a4-1678-495f-9f1b-f5f7a7562e89	errURL			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
5f56054d-1535-460e-9aca-cf1b91de9db8	\N	7bd626a4-1678-495f-9f1b-f5f7a7562e89	minimum	Minimum	15000	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
4cb96846-3fe0-40ca-b19c-cb52d599fce5	\N	7bd626a4-1678-495f-9f1b-f5f7a7562e89	maximum	Maximum	70000	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
c86e8edf-edce-4cfe-8735-f62f3770f084	\N	7bd626a4-1678-495f-9f1b-f5f7a7562e89	campaignId	Campaign ID	TOPUPEASY 02/2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
33dddcef-8f15-49cc-8a86-10fa0df75778	\N	7bd626a4-1678-495f-9f1b-f5f7a7562e89	pcutOffTAllD		All	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
afa7e732-b766-402d-ba4b-d79d05d02e71	\N	7bd626a4-1678-495f-9f1b-f5f7a7562e89	pcutOffWE		e	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
68b6e2a6-1caa-409e-8d42-5627b25974c5	\N	7d9fb9cc-c733-406e-be20-a43b5e1fbd9f	chkBlacklist	Blacklist for Lending	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
e6203a36-f03b-47ab-9e19-b79b3b744b6e	\N	415e1bf3-5b45-4b55-b0ff-be44e7e7ee88	imgUpload	Upload Agreement		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
e668b6a9-3085-48bc-9dae-960b68788c74	\N	7bd626a4-1678-495f-9f1b-f5f7a7562e89	defCamp	API	url	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
142df68f-abe9-48c1-a6f9-bb4b86866258	\N	7bd626a4-1678-495f-9f1b-f5f7a7562e89	defCampaign	Default Campaign	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
aa56d43d-f133-4ea2-9158-0ce530110f10	\N	7bd626a4-1678-495f-9f1b-f5f7a7562e89	defUrl		www.tisco.com	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
c58044a0-73c7-4e9a-8d56-1f88d386207b	\N	415e1bf3-5b45-4b55-b0ff-be44e7e7ee88	g001	Agreement	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
204d523a-4660-11ea-b77f-2e728ce88125	\N	7bd626a4-1678-495f-9f1b-f5f7a7562e89	calFactor	Calculation Factors	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-02-03 15:40:58	develop	\N	\N	213
0ff59942-54ae-4a31-b881-5add4cd94914	\N	7bd626a4-1678-495f-9f1b-f5f7a7562e89	invoiceReceive	Invoice Receive	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
e5515fb5-5b00-4cc8-a4f6-74b1c30c9af0	\N	7bd626a4-1678-495f-9f1b-f5f7a7562e89	errRemark		Error Message Failed Case	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
a73d6154-7499-4c97-9c1f-5d51420fc651	\N	7bd626a4-1678-495f-9f1b-f5f7a7562e89	calculationInEx	Calculation (Incom, Expend,Debt)	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
f8b0a564-527f-439b-b343-969e550f0a83	\N	7bd626a4-1678-495f-9f1b-f5f7a7562e89	accountRemark	Remark	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
91e6ae61-8f63-49f7-b4e5-fa9b1a30524e	\N	7bd626a4-1678-495f-9f1b-f5f7a7562e89	bankAccount	Bank Account	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
ac80b0f5-2e9c-4a8c-ab30-8928c7737c9c	\N	0aac6fdf-ccb7-47fb-80f7-607972e357e2	g001	ProductSaleSheet	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
1bb9c56c-feab-427f-94cc-be94360024fa	\N	7bd626a4-1678-495f-9f1b-f5f7a7562e89	accountNumber	Account Number	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
22011738-1176-4899-bf3c-7532e7e150fa	\N	7bd626a4-1678-495f-9f1b-f5f7a7562e89	accountPhoto	Take Photo	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
5e8450e6-1804-465a-8208-25331eeb3fdd	\N	7bd626a4-1678-495f-9f1b-f5f7a7562e89	errMsg		em	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
7b6b7348-46f8-11ea-b77f-2e728ce88125	\N	7bd626a4-1678-495f-9f1b-f5f7a7562e89	smsSMS	SMS	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-02-04 09:54:23	develop	\N	\N	213
7b6b7640-46f8-11ea-b77f-2e728ce88125	\N	7bd626a4-1678-495f-9f1b-f5f7a7562e89	emailEmail	Email	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-02-04 09:54:26	develop	\N	\N	213
fffe1f02-46f8-11ea-b77f-2e728ce88125	\N	7bd626a4-1678-495f-9f1b-f5f7a7562e89	postPost	Post	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-02-04 09:54:28	develop	\N	\N	213
0b41750d-43ed-49ab-bf85-81cc478fec3a	\N	0aac6fdf-ccb7-47fb-80f7-607972e357e2	radioMandatory	Mandatory(Yes/No)	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
bb680682-fb44-43e7-8572-87ea831ac5b9	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	surname	Surname	Y	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
492257f4-2079-4204-aa2b-3e200719c90a	\N	0aac6fdf-ccb7-47fb-80f7-607972e357e2	headerText	HeaderText	Sale Sheet	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
0a2973f4-ea6b-444b-bb75-7152a0972b2f	\N	0aac6fdf-ccb7-47fb-80f7-607972e357e2	activeDate	ActiveDate	2020-01-01 00:00:000	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
64ceb39f-65d6-481b-8145-b480a25a4ad9	\N	0aac6fdf-ccb7-47fb-80f7-607972e357e2	verProdSaleSheetList			\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
06571f38-d2ec-4877-a586-32cf32a3f36e	\N	d4fb5258-e64e-4861-b59b-1c49747a2a06	inCorrectAnswer	Incorrect Answers	2	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
be74e29a-47cb-11ea-b77f-2e728ce88125	\N	7d09ce2f-657a-4a9c-b562-0fa4e2b066b8	prodDepartment	\N		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-02-05 11:00:58	develop	\N	\N	213
be74dfd4-47cb-11ea-b77f-2e728ce88125	\N	7d09ce2f-657a-4a9c-b562-0fa4e2b066b8	operDepartment	\N		\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-02-05 11:00:58	develop	\N	\N	213
36040de8-5bfe-4e95-95c1-702825b93107	\N	7d09ce2f-657a-4a9c-b562-0fa4e2b066b8	endDate	\N	2020-12-20	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
f8444486-f93d-4fc5-965e-22182218d1ee	\N	7d09ce2f-657a-4a9c-b562-0fa4e2b066b8	campaignId	\N	TOPUPEASY 02/2020	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
a72cbf51-1184-4e17-a045-796cec45e764	\N	7d09ce2f-657a-4a9c-b562-0fa4e2b066b8	prodCode	\N	TOPUPEASY	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
60c44de0-3ae0-4319-9e8d-cb04b9de37f9	\N	7d09ce2f-657a-4a9c-b562-0fa4e2b066b8	prodName	\N	สินเชื่อเพิ่มวงเงิน (Easy)	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
e407577c-76b2-4955-855d-30b017d87bc1	\N	7d09ce2f-657a-4a9c-b562-0fa4e2b066b8	activeDate	\N	2020-01-01	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
d3b02208-ce42-4b34-85e1-e0ee757f3acb	\N	7bd626a4-1678-495f-9f1b-f5f7a7562e89	cpoCPO	CPO	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
8651859e-56ab-4fc7-99bf-b20082cd39e5	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	incFreeText		f	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
fb6a9372-3391-4adf-a516-37b46adf09e7	\N	7bd626a4-1678-495f-9f1b-f5f7a7562e89	packageList		[{"label": "จำนวนงวด","parameter": "factor_2_from","topic": "header","unit": "เดือน"}, {"label": "ผ่อนชำระต่อเดือน","parameter": "calAmount1","topic": "body","unit": "บาท"}, {"label": "อัตราดอกเบี้ย","parameter": "rateAmount","topic": "body","unit": "%"}, {"label": "หักส่วนลดต่างๆ","parameter": "discount","topic": "content","unit": "บาท"}]	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
2c377b2e-596f-4383-a791-e812152f9400	\N	7bd626a4-1678-495f-9f1b-f5f7a7562e89	limitAdjust	Limit Adjustment	5000	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
36850889-1622-4f8f-b8c4-a14d0dcbd789	\N	7bd626a4-1678-495f-9f1b-f5f7a7562e89	campaignName	Campaign Name	Easy Topup	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
653c64d4-a009-4e86-b72b-082f6e6401b0	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	status	สถานภาพ	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
78102cdf-f058-4646-8d0f-d658a8376454	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	birthDate	วันเกิด 	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
c2e02ef9-815a-45d6-bec9-b1033ddc8cbb	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	fatherName	ชื่ิ่อบิดา	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
d0ae7ac5-124c-4b76-86a6-0bc47a112c48	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	fatherID	เลขบัตรประชาชนของบิดา	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
11f99b66-312b-4bf7-a3c2-4256e9df38d1	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	fatherNationality	สัญชาติของบิดา	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
22ba3b2e-f00d-4b07-9097-b7cac171d884	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	motherName	ชื่อมารดา	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
8292725e-8cfb-423f-ab4b-8cf4cfb84b7c	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	motherID	เลขบัตรประชาชนมารดา	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
72490d0c-6817-4849-9faf-3b762f7973cb	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	motherNationality	สัญชาติของมารดา	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
53df4a6c-e7d8-4838-ad3e-b2fc6ebec063	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	registerDate	วันจดทะเบียน	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
5a1ae085-1555-4d2d-8a7e-890ba77e9858	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	registerID	เลขทะเบียนรถ	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
92191f54-6043-44be-a2db-b2beee3167fe	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	registerProvince	จังหวัดจดทะเบียน	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
b97b9db8-3e77-4764-9b08-e74cc2732a8e	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	type	ประเภท	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
13b8f313-7891-4c1a-893b-e1415d706063	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	typeNo	รย.	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
2b962a18-d229-4283-8294-a40f3c778c8b	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	category	ลักษณะ	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
9e2227f8-c2f7-4d65-b153-393d1b8302a4	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	motorBrand	ยี่ห้อเครื่องยนต์	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
324ba77f-8bd7-42e6-90c0-c33400bfe677	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	vehHeaderText	Header Text (H2)	โปรดระบุข้อมูลเกี่ยวกับรถ	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
291da595-d343-4108-a2ae-87908a6f3327	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	chassisNo	เลขตัวรถ	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
dc539c28-01e0-4b72-a3a6-fda2483195bd	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	chasisPlace	อยู่ที่	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
b37be323-7c96-4351-87d7-920f14d1fc34	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	carNo	เลขเครื่องยนต์	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
dd5f7e7e-b376-4002-9d75-4ebcb3e2ffa0	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	carPlace	อยู่ที่	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
816de7a4-e8ea-4ca8-964a-b97396b19ecc	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	carFuel	เชื้อเพลิง	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
2a171030-ba9e-4fef-a7a8-fe42245b1d75	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	gasTankNo	เลขถังแก๊ส	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
718b4506-978d-42ba-b4a7-20069960c1be	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	pistonsNumber	จัำนวนลูกสูบ	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
73dbbf8b-e370-48dd-b94f-27dcbee9981e	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	carQuantity	จำนวนซ๊ซี	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
d5c635dd-94df-4a85-9e68-18af77116f3b	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	carPower	แรงม้า	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
62820ae6-d8d7-4329-866b-9aec2ac5e5a6	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	carShaft	เพลา	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
1c8cce66-ded8-408b-9448-a270004dca64	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	carRubber	ยาง	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
4f435f64-daa1-4cb9-9999-d99c15ae6839	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	carWeight	น้ำหนักรถ	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
65f4b2d3-f51a-4dad-8c93-bd4f59c9afe8	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	carLoad	น้ำหนัักบรรทุก	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
a4b583ab-6c03-4d84-9d16-f28cf3cc0718	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	carTotalLoad	น้ำหนักรวม	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
c357cdcd-f91c-45d9-8864-51f64d5f4fab	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	carSeatNo	ที่นั่ง	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
aa7f9809-2f3b-4094-b4b5-0ff202836f53	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	ownDate	วันที่ครอบครองรถ	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
c4da992d-bb83-4098-a28f-a243ccc14318	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	ownerCar	ผู้ถือกรรมสิทธิ์	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
83f0acf7-72e7-49ce-a3d2-41664fb4472b	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	ownerNo	เลขที่บัตร	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
76c08447-45a0-4e2f-8bbf-f1d543a3a7a8	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	ownerBirthDate	วันเกิด	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
6e2f8fa5-058b-428a-b581-0f21304030d6	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	ownerNatioanality	สีัญชาติ	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
1489c03a-07ff-4187-8544-a8f424f5d826	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	address	ที่อยู่	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
6dcc5d38-017b-4013-848e-7c4e061b5378	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	Province	จังหวัด	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
a5f25bd5-f6e4-48fe-982b-51cd29846ab8	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	occupantName	ผู้ครอบครองรถ	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
870dcc45-e9b8-45b9-b872-70dd68ec2f52	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	motorType	แบบ	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
fd1f7d14-3dc3-4333-8b9f-fe58871bc8f0	\N	183b450d-d189-4199-b320-cc8ecfa360f7	chkConsentList	Checkbox Consent	[{id:"1",seq:1,require:"Y"}]	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
4984639b-4c5e-4020-854d-138a1ffb59c2	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	occupantNo	เลขที่่บัตร	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
8e96d3b5-416d-48ee-b6d2-f5e5f97ccdf7	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	occupantBirthDate	วันเกิด	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
56240202-712b-4058-88a9-5592b278eaa5	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	occupantNationality	สัญชาติ	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
c306abb1-8ee1-4ac8-bb0d-1605a0629f31	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	subDistrict	แขวง	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
3c774dce-efb0-407d-a9d9-516be3629b66	\N	ba66a6f3-dbe3-4694-9aed-fafe09879bdb	district	เขต	N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
204d4f88-4660-11ea-b77f-2e728ce88125	\N	7bd626a4-1678-495f-9f1b-f5f7a7562e89	calFactorsList	\N	[{code:"calAmount1", name:"ยอดผ่อนต่อเดือน", desc:"งวดการผ่อนชำระ",formular:"(${limit}/((1-(1+(${rateAmount}/12))^(-${installmentTerm}))/(${rateAmount}/12)))",formularDesc:"วงเงินกู้/จำนวนเดือน", round:"up",unit:"100", decimal:2},{code:"calAmount2", name:"ค่าที่ได้จากการคำนวณ", desc:"งวดการผ่อนชำระ",formular:"((${rateAmount}/12)/${factor_2_from}*${limit})",formularDesc:"วงเงินกู้/จำนวนเดือน", round:"up",unit:"10", decimal:2},{code:"calAmount3", name:"ค่าที่ได้จากการคำนวณ", desc:"งวดการผ่อนชำระ",formular:"((${rateAmount}/12)/${factor_2_from}*${limit}-${discount})",formularDesc:"วงเงินกู้/จำนวนเดือน", round:"down",unit:"100", decimal:2}]	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-02-03 15:36:09	develop	\N	\N	213
8d7969f7-7209-4ff9-8a45-f9da9fed64ed	\N	0aac6fdf-ccb7-47fb-80f7-607972e357e2	chkBoxLabel	CheckBoxLabel	ข้าพเจ้าได้อ่านเนื้อหาและทำความเข้าใจอย่างละเอียด	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	97C9D6B2-0BC4-4ADA-A22D-17E5BF2037F2	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
b290f40c-0bb9-4a2b-947a-df1880a8fa0e	\N	7d09ce2f-657a-4a9c-b562-0fa4e2b066b8	productProcessor	\N	DBB-Product	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-02-13 12:08:08	develop	\N	\N	213
174f2a7b-bb0c-47a8-9348-2c07d651bfe9	\N	7d09ce2f-657a-4a9c-b562-0fa4e2b066b8	productController	\N	DBB-Product	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-02-13 12:08:08	develop	\N	\N	213
535becd9-dea0-49f2-a50b-9c307a9a2751	\N	7bd626a4-1678-495f-9f1b-f5f7a7562e89	summaryList		[{"label":"วงเงินสินเชื่อของท่าน","parameter":"limit","topic":"header","unit":"บาท"},{"label":"จำนวนงวด","parameter":"factor_2_from","topic":"body","unit":"เดือน"},{"label":"ผ่อนชำระต่อเดือน","parameter":"calAmount1","topic":"body","unit":"บาท"},{"label":"อัตราดอกเบี้ย","parameter":"rateAmount","topic":"body","unit":"%"},{"label":"หักส่วนลดต่างๆ","parameter":"discount","topic":"body","unit":"บาท"},{"label":"สัญญาที่สัญญาที่อ้างอิงก่อนหน้า","parameter":"factor_1_from","topic":"body","unit":""},{"label":"กำหนดชำระ","parameter":"paymentDueDate","topic":"body","unit":""},{"label":"ชำระทุกวันที่","parameter":"paymentDate","topic":"body","unit":""}]	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
066b17ce-6d5a-4df2-8b5b-a8270a086184	\N	7d9fb9cc-c733-406e-be20-a43b5e1fbd9f	riskLevelList		[{level:"C",riskName:"Black"},{level:"R",riskName:"Yellow"},{level:"O",riskName:"Grey"},{level:"L",riskName:"Red"},{level:"V",riskName:"Blue"}]	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
\.


--
-- TOC entry 3965 (class 0 OID 16455)
-- Dependencies: 204
-- Data for Name: t_shelf_product_vcs; Type: TABLE DATA; Schema: public; Owner: digitalshelf
--

COPY public.t_shelf_product_vcs (uuid, prod_uuid, comp_uuid, tem_uuid, theme_uuid, ver_comp, ver_tem, ver_prod, effective_date, state, attr1, attr2, attr3, attr4, attr5, attr6, attr7, attr8, attr9, attr10, status, create_at, create_by, update_at, update_by, comp_status) FROM stdin;
0c7259bd-aa2b-471c-a23f-366583d79e91	e0c80f8b-627e-48b4-9384-87d00dca5274	ae6ec58b-e9b1-4ddf-a116-4aea79dcefc5	77dcd446-436d-404b-82bc-6ce8a925cec2	0986c206-12b3-4130-972d-3c35e87e54e9	0	0	0	\N	inprogress	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
d4fb5258-e64e-4861-b59b-1c49747a2a06	e0c80f8b-627e-48b4-9384-87d00dca5274	a34404a3-309e-4057-8f53-f386d026656b	77dcd446-436d-404b-82bc-6ce8a925cec2	0986c206-12b3-4130-972d-3c35e87e54e9	0	0	0	\N	inprogress	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
183b450d-d189-4199-b320-cc8ecfa360f7	e0c80f8b-627e-48b4-9384-87d00dca5274	ec654003-0763-48d3-af08-8a9b053783e4	77dcd446-436d-404b-82bc-6ce8a925cec2	0986c206-12b3-4130-972d-3c35e87e54e9	0	0	0	\N	inprogress	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
ba66a6f3-dbe3-4694-9aed-fafe09879bdb	e0c80f8b-627e-48b4-9384-87d00dca5274	ed8880d3-e353-48c6-bfd7-debe70ba6c37	77dcd446-436d-404b-82bc-6ce8a925cec2	0986c206-12b3-4130-972d-3c35e87e54e9	0	0	0	\N	inprogress	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
7d9fb9cc-c733-406e-be20-a43b5e1fbd9f	e0c80f8b-627e-48b4-9384-87d00dca5274	d4882b6c-8e4b-441d-86fc-019c8eb4c232	77dcd446-436d-404b-82bc-6ce8a925cec2	0986c206-12b3-4130-972d-3c35e87e54e9	0	0	0	\N	inprogress	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
7bd626a4-1678-495f-9f1b-f5f7a7562e89	e0c80f8b-627e-48b4-9384-87d00dca5274	723b3819-e1e0-4756-a6f2-50fdaf14d85d	77dcd446-436d-404b-82bc-6ce8a925cec2	0986c206-12b3-4130-972d-3c35e87e54e9	0	0	0	\N	inprogress	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
415e1bf3-5b45-4b55-b0ff-be44e7e7ee88	e0c80f8b-627e-48b4-9384-87d00dca5274	2787aafe-e4e8-4f09-a3b8-5838a7595dbe	77dcd446-436d-404b-82bc-6ce8a925cec2	0986c206-12b3-4130-972d-3c35e87e54e9	0	0	0	\N	inprogress	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
7d09ce2f-657a-4a9c-b562-0fa4e2b066b8	e0c80f8b-627e-48b4-9384-87d00dca5274	\N	77dcd446-436d-404b-82bc-6ce8a925cec2	0986c206-12b3-4130-972d-3c35e87e54e9	0	0	0	\N	111	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	\N
b35522f7-db38-48fb-8164-13f1dd006103	e0c80f8b-627e-48b4-9384-87d00dca5274	af0f3037-5d24-454f-b81e-ed33e0f5f334	77dcd446-436d-404b-82bc-6ce8a925cec2	0986c206-12b3-4130-972d-3c35e87e54e9	0	0	0	\N	inprogress	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
492db4c5-e88f-4f67-b81d-9b19a01758b6	e0c80f8b-627e-48b4-9384-87d00dca5274	b29f1ed0-aa1e-4b4c-8053-2808fdb04431	77dcd446-436d-404b-82bc-6ce8a925cec2	0986c206-12b3-4130-972d-3c35e87e54e9	0	0	0	\N	inprogress	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
0aac6fdf-ccb7-47fb-80f7-607972e357e2	e0c80f8b-627e-48b4-9384-87d00dca5274	169e986b-5a0a-45c9-8b08-890841b5b23f	77dcd446-436d-404b-82bc-6ce8a925cec2	0986c206-12b3-4130-972d-3c35e87e54e9	0	0	0	\N	inprogress	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	develop	\N	\N	213
\.


--
-- TOC entry 3966 (class 0 OID 16462)
-- Dependencies: 205
-- Data for Name: t_shelf_theme; Type: TABLE DATA; Schema: public; Owner: digitalshelf
--

COPY public.t_shelf_theme (uuid, theme_code, theme_name, value, state, attr1, attr2, attr3, attr4, attr5, attr6, attr7, attr8, attr9, attr10, description, status, create_at, create_by, update_at, update_by) FROM stdin;
dba18394-0af5-4561-9fac-d31a256ca3e7		Digital Lending TemplateAB	{\n    "body": [\n        {\n            "colour": "#f2f9ff",\n            "tag": "body1"\n        }\n    ],\n    "logo": {\n        "type": "application/jpeg",\n        "value": "/9j/4AAQSkZJRgABAQAAAQABAAD/4gKgSUNDX1BST0ZJTEUAAQEAAAKQbGNtcwQwAABtbnRyUkdCIFhZWiAH4wAKAAgAAwADACNhY3NwQVBQTAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA9tYAAQAAAADTLWxjbXMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAtkZXNjAAABCAAAADhjcHJ0AAABQAAAAE53dHB0AAABkAAAABRjaGFkAAABpAAAACxyWFlaAAAB0AAAABRiWFlaAAAB5AAAABRnWFlaAAAB+AAAABRyVFJDAAACDAAAACBnVFJDAAACLAAAACBiVFJDAAACTAAAACBjaHJtAAACbAAAACRtbHVjAAAAAAAAAAEAAAAMZW5VUwAAABwAAAAcAHMAUgBHAEIAIABiAHUAaQBsAHQALQBpAG4AAG1sdWMAAAAAAAAAAQAAAAxlblVTAAAAMgAAABwATgBvACAAYwBvAHAAeQByAGkAZwBoAHQALAAgAHUAcwBlACAAZgByAGUAZQBsAHkAAAAAWFlaIAAAAAAAAPbWAAEAAAAA0y1zZjMyAAAAAAABDEoAAAXj///zKgAAB5sAAP2H///7ov///aMAAAPYAADAlFhZWiAAAAAAAABvlAAAOO4AAAOQWFlaIAAAAAAAACSdAAAPgwAAtr5YWVogAAAAAAAAYqUAALeQAAAY3nBhcmEAAAAAAAMAAAACZmYAAPKnAAANWQAAE9AAAApbcGFyYQAAAAAAAwAAAAJmZgAA8qcAAA1ZAAAT0AAACltwYXJhAAAAAAADAAAAAmZmAADypwAADVkAABPQAAAKW2Nocm0AAAAAAAMAAAAAo9cAAFR7AABMzQAAmZoAACZmAAAPXP/bAEMABQMEBAQDBQQEBAUFBQYHDAgHBwcHDwsLCQwRDxISEQ8RERMWHBcTFBoVEREYIRgaHR0fHx8TFyIkIh4kHB4fHv/bAEMBBQUFBwYHDggIDh4UERQeHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHv/CABEIAZABkAMBIgACEQEDEQH/xAAcAAEAAgIDAQAAAAAAAAAAAAAABgcDBAECBQj/xAAbAQEAAwEBAQEAAAAAAAAAAAAAAwQFAgEGB//aAAwDAQACEAMQAAABuUAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAwvMzwfOmry9DO3ccwR31opt0RzgAAAAAAAAAAAAAAAAAAcaXh17coSqNa+TVxe3fr3mg7dtLUp6vs8+FkjnmEtrDd6o24gk3zNnKIbIAAAAAAAAAAAAAAACFehU9/NzZMWXXx8vfHzx7x4szk/zf6FTq88FHbpLrZ8Hiv8AibeDGimXvVhMfpvy+4ckCntOxyIbQAAAAAAAAAAAAADDmg3cUB19bP8ARfObGbBl952LZhNk5OtyM/UAY8grOuPpKtaf01Z7OHHX2J1YFYSH638gswY30IAAAAAAAAAAAAACk7n+edCh2zYMuvi7ObXzeeWXLfA97576LkRWAAGPIKF8W1Kty/0L1/bj/v8A1P5baO/F5Rn2ORFYAAAAAAAAAAAAA8mhr0onUzsuTF30srZzauf2O1JVXFj4H0AV7QAAHgUbfdCUfsfV9rz976f84l81gs6oWAq3gAAAAAAAAAAAAPN+fPpP5y0qDvhy6edlza+Tqt6V1UTLKN21HXtjbgAAEYpqfRrnb3HXj6j81ms0jckwPpeRBaAAAAAAAAAAAAA4pi54rYhpTNqZtzJ2O+Lv7BmzauTqGZ2TRG7Ru3orr383TkzwdbnuT6EJjNqn15xtPKyc4pK5sHaPnfpwdAAAAAAAAAAAAAAUXGfoag9jNx5dTNdq7HbF2QZsmv26h2u+t3ebPbX7eM/ODkzsI2bej00x9oKOgAAAAAAAAAAAAAABx4Ege8/N2t9DU7r5/gZdPLcq7PbB394zc4u3sOXth594z84NzxjsH0pbk6vIz9MAAAAAAAAAAAAAAAAACIwG6+bEPzZ0+k/LtVqD73bn75o33Lp2Yuq/m+yp2gjlAAAAAAAAAAAAAAAAAAxY4VWd/N+ge1FyD3y1M1YZ4ZrEyUZY/vkp5+frS75mOKkvKli+iOK4sihfCOYAAAAAAAAAAAAAAAADSpC+6H08mceLJ4x1xswmcRq3Wn0MlEUgm9r0u/eGaAT+F2hLFS/0L87/AEPxJ3GVrgAAAAAAAAAAAAAAAAKGvmhtLKsWMSeMd8bezqR6WPPxu5ZYvd7wWZ17EStCmPbmg8D6Goq961rsMvWAAAAAAAAAAAAAAAAA4rSzEsEe8eciFeTZXPceh320Fitpl66WKFeLaDvjxvZK9gPOwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAP/EACwQAAAGAQIEBgIDAQAAAAAAAAABAgMEBQYRNRASE1AUICEwNEAVIjIzkDH/2gAIAQEAAQUC/wAO3HENpdtYaAu7bH5wwV2G7eMoMyWHu1SpLMZEu6dWFrW4rzRrCSyIdiw/2e1tkRg66484XkXIaQDmjxygU8g1JZc4QLJbRtrS4jsd7adHgXF11LZOvLc8pkI8p1kMPIeRXzFRVtrS4jsN7YFCj6mZkCBD9zOJj0p4MY9XoIqiuIl0dYoSsZRpOr5cIwZBpa2nI7qXm6iZ0XOwOrS23PlLmSyBAghJrVAiNxWfKtKVpuqDQgZCE70XhTyevH+/lsrpxSBAgQxxjqTPYyir9AZCCvnjVj/Ql/fyF/r2xAgQIYwjSF7CiJSbiJ4KeKs+Fe71of3XFEhClGtYIECGP7V7OYx+aMK7+4Y8vWN924Plqi4ECBDGV81d7OQI6lQK4v2GNn+/3b3aC4ECBDFn+WR7NxtYhJ5WRjf9/wB20QblaQLgQIRnlMPxXkSGPYyZ3p07KOo4XoQxkvX7p/8AJDRsSi4kCMUNj4VwvX2Mwkc7sdvpp4Y8jlgfey+N0bQjBcSBGKi2XFDDzb7fllyWozSzNyRwSRqVFaJmP97J4fjKwgRguJGCMR5DzC4t+4Qau4Ki/L14du4aRJvHlhxxbiuOPx+tL7Bktf4GcRgj8hGCMajUajUajUa8EJU4uujFEi9gsojU6JOivQpJGCPy6jUajUajUajUY/Xm0XYrmsasY82K/DkEYIwR+XUajUajUUdQZH2SwhR5zFtSSoAIwRgj88WO/KcqaZqJ2ixoYMsTMdsGDdaeZURjUaghGqrB84WOISGGWmG+1KIlE5WwFj8JVhFTXIDTDLXalrQgddkdZkKdbSaHEL4GtBKClJSRGSiCXEK7PmP9Artwyzc8O+XkFu4TtUZna39mcJDrjjq6Z9uLj1jYSJrhGZHj9u4bvZZMdmQU9JInYtFjuw8s3TEfSSozUrHYEXwWRKNVxjEFiSMmbRGrK9kpE28q4bdakzSpPqnstnuOIbblu6Yf8q2j+GsMPkat3274d8fMfi0W7ZBs4R/DstnuWIbblm6Yd8vMGPWgdNq2v93w74+Y/Fo92yDZwj+HZbPcsQ23LN0xI9JFjYSJx4zGU9Y5TGU1Pr58iCuX17PHG1qbXOtpcxiujKlzC7NLoJbsqihOQYl3USJs2kqn4TjGNuc0SMzEZlR2pLUjG181RGXEgWNAy+tGNyOaur2IKP8ADn//xAA0EQABBAADBQYEBQUAAAAAAAABAAIDBAUREhAhMUBBBhMUICIyMEJRYSMzNHCxUnGBkfD/2gAIAQMBAT8B/YclOtwt4uXj6/8AUmWI3+08rYuiPc3eVLO+Q+oriq/Z/EbIzZGcvvu/lSdl8TjGej+F31qk7TKD/lV7TJx6eSuWtHobx2QQPnkEbOJWGw4ZhQBd65P+4IdpK/VpUGJVrW5p3q9Qiss0yNzWL4RLhcnex+1VbAnZq5CV+hhcnEk5nZh0OQ17eCwnFiSIZj/Yq9SZZidG7qo9VG0YnfXLkLx9OSOysMoh5cMn8VVa48eq7VxCPEnZdVWfriDvj3OITxsqP1RDy9mnfhPC7SWBYxGRw4Dd/pYf+QPj3BwKe1EKlP3btJ4HynEW4Vhmfzv4JxLjmVTbphb8edmpmx7dle66Pc7eEy3E7qjYjHVS4i0ezerE8lh2uQ5qGIyPDUBkMuQsRaTmNjmIjy0q+ganceRIBGRUtct3jYWIsWlBpO4KtS0+p/KPgY5Gn9CvBuQpN6qOJjPaOUsXjE/TkhfJjL8kLbe67xyiuB7C87lBeEriMtydiYB3BQzNmbmOTxMDSCpgBUCkafDtKnLfCt0oMaKmoDeqkbXQPJCwv5uTxP2hTfpGrvGioGnqtDvC5/dRyiSuYhxVew2OJzD1WGNORPJywNlGTk6uxzNB4LwcWQBHBaRlp6KOtHG7U0J1KFxzyTWhoyH7D//EAC0RAAEDAwEGBQQDAAAAAAAAAAEAAgMEERIxBRATFCFAICIwMkFCUWFwMzRx/9oACAECAQE/Af0Q2mldo1cnN9k6J7dR2tPRmTqdFHCyPQbpK+nj6FyG06Y/KdDDUC7VNA6I9eyo6bLzu03SSCNuRVVUT1BsOgXKuToXMUUz4jdpsqSrZWNwfqp4TE63YRMzeGprQ0WG6vlucPBNB9TVDKYnhw+FKBUQZD/ew2e3zF2+oN5D4Zm4uWynZUwUzMHkevQe0obqpuMp8NVqFs6Ph07Qqz+Y+vs92o31kGbchqPDDTGpn/AQ6dFVOylPr0smEngno2v6t6FOpZW/CEEh+FFQuPv6KONsYs1Sv4bC5E37Cknzbidd1/FWT5nEadi1xabhQVbX9DrvvuJA6lVNZl5WdpHVSMQ2h9wufZ9k7aDvpCfM+T3HtKei4rMro0NpAy6dSO4vDapaQteGDrdT0RiaDe6bs1xHmKmhdE7F3Z7NJyIUJJqyo3DmHBQB3MuyRcTV2Kq5HNnaAVtP6ez2b7yoP7ZXDcasuHws28zb8KSIx1AkOl1UU7pJWvHwtpuFwOzimdEbtTZ3tfmNVzctyQdVkb3UlTJI3FxTayZosCnOLjc/of8A/8QAPRAAAgADAwUNBgYDAQAAAAAAAQIAAxEEEiEQIjFRcRMgIzAyQVBhcnOBscFAQlJikaEzNGOSotEUkOFD/9oACAEBAAY/Av8AR3edlUayY5Zc/KIzJDHaaR+W/n/yMbP/ADjODrtEcHNU9Veir05wvrF2zruY1nTF6Y7Oes7/AJd9dTRdOY+o9DmVJo877LG6TXLsec73TXZGEv7xyBGdLPgYweh1HIEnVZNfOIDIag8/Qhs1nPCe83w73HTqjE4at9TlLqMXkPhqjXLOkQHQ1U6OgrqHhn5PV1xUmpO8CSkLzDoUCL9qmCVXm0mM9Xm9pv6j8qkflqbGMVs9oIOp8Y4eUQvxDRlvocYvr4iNxc8G32PQLTHNFUVJh57c/JGobwKoqSaCAqqL3vNr3xVgCDpBhp9hHWZX9ZflOByXW5aYHoBbMpxmGp2DemadEsffiTbrOvegeeVdYwhG904HoCbjgmYPD/td6z/E/ElSKgw8n3dKbMjr45JbnTTH25nOgCsF2NSTU72V4+fFS7QBijUOw5D2cjp8Le3Wo/pN5b678DkcVaBqWv0xyO2ScOoe3Wnsb55B98VHhxVq7pvLJXXkm9n260oNJlNT6b5JyaVNYSbL5LDiZuOLUUQFimSe2z19vmyT/wCbld9uM08C/wDE8TKsiY0zm280fMdOW98bE+37sOTOWviN+JU6ryfusCZKcOvVvi8xtg1w8+ZjMc1OUKuJOAhJQ90U9vYoKzJWevrxF+TMZD1RS0Sg3WuEZzOm1f6j8f8AiYzb77F/uKSUErr0mL8xyzazvN1YZsvz6BvIOAm4r1dXsARBViaAQsoafeOs9AtZ5vPoOo64azzxRl+/Xx/+VPHCEZo+EdB3WzZi8h9UGRPS6w+/XxotNrXrVD69C7laErqPOsFxw0n41GjbxW5yJZc+UCbOpMnfZeiCyruEz4k/qKyws9flOP0i7OlPLOplpvc2zOo1vmwGtc2/8qYD6xuclFRdQ6LoQCIzrHI/YI/KL9TGFkleIrHBSkTsrTorOYDaY/FT90UE1PrFGdQesxmOrbDkulhU81clWYDbFQajJRXU7D0PI7RyWfvV84HdD1id2Iay2V7t3B3GmuqLOSandBAlSfxnH7Rri/NdnbWTCz5nJW95wTMYhOZBoEVGBhbLamvXsEc6a6uhgJ0tXporFoRRRVmMAPGDOmSlaYs3BjzYCB3Y9Yn93BYmpMSrUUDzSa1PNjE+vNQfaJs2el+7gAYkyJK3U3StPrEqQxwZsYabJlbm8ulKc8BlNCMRA6GtPet5w/enyEDux6xO7HrE6VTNvVXZE2ynmz19YtHa9In9oRJ7fpFn7UWjYPPINnQ1p71vOH70+Qgd2PWJ3Y9Yk2kdg+nrEmnvG6fGLR2vSJ/aESe36RZ+1Fo2DzyDZ0Nae9bzh+9PkIHdj1i0H9OF3YqAuhVGELMpmSs4+kbv7k3zhmkEZ2kMMIWeaGYrF6DVjCuhoymoMbjMKhOe6NMS5C85zuodDzZqzJIDuWGJ1w0qaysS97N8I3aU8tRdpnVia0x5ZvrTNjh7QoHyCBKkrdXzgypyXlMVs9oUjU4hLPMILLXRtgzLO+4sdIphHCWiUB1VMUlCrHSx0n/R1//EACsQAQACAAQEBgIDAQEAAAAAAAEAESExQVEQYaHwUHGBkbHBINEwQPHhkP/aAAgBAQABPyH/AMO/IDaJfgRpZ1yhsr5X7cDFeKHK31Fg5ng9JTDaXqe2fhVJpoa+Ql6PzdfR1nPwN0IQ/BkPcXXOUq3+W+T4PsbD9nPlH2aLsw4CEJvntjh6z5x/rzSr1IsAeQvB08mZv2kCKaweCK8X5XQc/j4FW1t4CEseJZCMZG1l+CcBUt07ynrYLOK7i/8A7nOARBaPAqQss7NYsnItXX8I7gNpYrv4f8zrKu5up9JTD18ZcuJueusU9Fio+pl7MzwCjGfr+4nAFUBnsm0N4GmwxjxXg92HgN/CNsEuJBVZ5R+Ga4MBqsfoZlYr9fkINqBYku1w7T69tonAU1e9vhjFdPMNHwBKM7zbtez+PAZu96sD7/gYJSde19ffeJwHubv0lmNL6bD+/YSz+Xq/Ea2zFsdwD7v+E0QKRMEihG+d+Xtl6RJS3bDv24ImtVXMwf7yk0peRLJDJusI4+D3b8/8RP2S+zM68OvfJwxzxtrYT/j/AHvJA90cBFHFLbsA/f8AEP8Aql+nDy4B37cPM6/P7/vBa98cOBxwkuGb66PZ6fxILuxcKVGLuXMxyfP94AbE81o4oRRxoMOOfKIVdo5cv4QLQ80Wl9LigZauxKAMAlzIG0/vBaHKMlbYb01ccIMfC1kGfU8ogCIjlX8FprYY3YdF+5Bx8cxLlx7uieWX0/30qMF0ifD68JQYMXA5Yh9G5yjY5r+Qmxg4mL2I25cGhyOWkuXAwt0bsQSw7b8/772SsAxaye3UI+EoMHiHnItn5msFPU+9v8lq5C79p3f1zM/p30gyO3/5R+jatsuXLnPZ4men9+3gKtXR21r3040MGDxAggg/ALjTSgasx4s3uHgImYY68dITQMpMjoOXGBgwYMIIIPxAbo4r1NfN8D3NgjFfqIqyWw2Op+AAwYMGEH4FQJTBuv6eCuqnTwbcY0Nzcx5Gnnl+AAy5cuXLm+Bay8zpGEZo+jd5+EOW9o4Pnk+IkxbxqpzX1cuRuqfXgEkYmjFYK5QqeeOfpHbaZ/8AYPSGgvRrwtYsaJc1NOoT7kWx9j98zvd3OF0PyPh4UJd/lgT/AD0GRFcADOUnhIkiBnQ8BAeUsTwry91UrCTJGKGcSsJmA+D9+24aPvM7ZvHQvmIWKscWxtXfNlCOrrjK2sYLifYjpqztMc2rTut6PeLJbjsL7ecJqhiI0kVvnB6je++Xgo812DdQ6ZIaAowmdTGiE79vDUhb+0UMi1dWXgC30oB7SxWYOQCDjXmXc3nL4m6F2Xyx/QYpnWsJZZJuCwp3zi9F2GjFY3PBu37+FjtW8H28UaAYDdifMtJi8zRw+nvOmfCd22nV47fkzvWzh0rwVnZ9/Cx3reOjwZmzfywaU1Vbn/VTo3wnftp1eK9/RncNnDoXgrOz7+FjvW8MDmW6xe0iAO83ut56Pv0jWqmI7ApOlxkEFXirKADo41YQh6fEQsYDRIMwerRh3gYNXI9VmA8FcoD0MbAU7RMlLWqqGvlCQaaq1i7HODradnP1I81VxGVPNy6wi44u63XVgdNM6O5CtRyUJ6mftMVThy4ploQXin+o/wAwynthG7ezjT/w6//aAAwDAQACAAMAAAAQ8888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888OF108888888888888888888774HRDU88888888888888888DWfSO1+id888888888888888zvmv88IHPu088888888888888VRzn8888eed88888888888888D/9l8888hrA88888888888888b5RV88881++88888888888888oGz8dqII/WL88888888888888sq4X9xD/ADmPPPPPPPPPPPPPPPPMZy7GcMfvPPPPPPPPPPPPPPPPPLGJ7KQPPPPPPPPPPPPPPPPPPPBvvlxWGvPPPPPPPPPPPPPPPPPPX8OaiSvPPPPPPPPPPPPPPPPPAfJJLLtPPPPPPPPPPPPPPPPPPNSq7foofPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP/xAApEQEAAgECBAUFAQEAAAAAAAABABEhMUFRcYGREEBhobEgMMHR4XDw/9oACAEDAQE/EP8ABwC2aSdM/E/6D+o3Qrzz28rd/iCW6z47QFUTMOb0HSxfSWJfkr2u+0KSvQJ2uXdZNTc8k2T6nhHMBDYP2uwGVnr1SFg81HW5cqlyP3GqZwOHpevS4uEHj8m4wSqtw7jweI7MPjCanB8gbG0d6jGXb1cHLxFVkt7DhNb4PG9mEHYK/vMg2MS6NX5BanfPaGnwEpwPf6BpuaohjmN+pTEPYH2IrWqe5h++bL0lL4F6fHb6WW6D8n8I5toDoB948fP5+/lhYXKGHY/gZf0Or5lG5tb6Ys5xBkWV54X3z9+6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"\n    },\n    "button": [\n        {\n            "btmColour": "#0f426e",\n            "tag": "button1",\n            "fontColour": "#ffffff"\n        }\n    ],\n    "theme": [\n        {\n            "btmColour": "#283d81",\n            "tag": "button1",\n            "fontSize": 16\n        },\n        {\n            "btmColour": "#283d81",\n            "tag": "button2",\n            "fontSize": 16\n        }\n    ],\n    "header": [\n        {\n            "colour": "#a7d7ff",\n            "tag": "gradient1"\n        },\n        {\n            "colour": "#ddebf8",\n            "tag": "gradient2"\n        },\n        {\n            "colour": "#ffcccf",\n            "tag": "gradient3"\n        }\n    ]\n}	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-16 16:37:47	Tor Alter	\N	\N
0986c206-12b3-4130-972d-3c35e87e54e9		Digital Lending Template	{\n    "body": [\n        {\n            "colour": "#f2f9ff",\n            "tag": "body1"\n        }\n    ],\n    "logo": {\n        "type": "application/jpeg",\n        "value": "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7+FjvW8MDmW6xe0iAO83ut56Pv0jWqmI7ApOlxkEFXirKADo41YQh6fEQsYDRIMwerRh3gYNXI9VmA8FcoD0MbAU7RMlLWqqGvlCQaaq1i7HODradnP1I81VxGVPNy6wi44u63XVgdNM6O5CtRyUJ6mftMVThy4ploQXin+o/wAwynthG7ezjT/w6//aAAwDAQACAAMAAAAQ8888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888OF108888888888888888888774HRDU88888888888888888DWfSO1+id888888888888888zvmv88IHPu088888888888888VRzn8888eed88888888888888D/9l8888hrA88888888888888b5RV88881++88888888888888oGz8dqII/WL88888888888888sq4X9xD/ADmPPPPPPPPPPPPPPPPMZy7GcMfvPPPPPPPPPPPPPPPPPLGJ7KQPPPPPPPPPPPPPPPPPPPBvvlxWGvPPPPPPPPPPPPPPPPPPX8OaiSvPPPPPPPPPPPPPPPPPAfJJLLtPPPPPPPPPPPPPPPPPPNSq7foofPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP/xAApEQEAAgECBAUFAQEAAAAAAAABABEhMUFRcYGREEBhobEgMMHR4XDw/9oACAEDAQE/EP8ABwC2aSdM/E/6D+o3Qrzz28rd/iCW6z47QFUTMOb0HSxfSWJfkr2u+0KSvQJ2uXdZNTc8k2T6nhHMBDYP2uwGVnr1SFg81HW5cqlyP3GqZwOHpevS4uEHj8m4wSqtw7jweI7MPjCanB8gbG0d6jGXb1cHLxFVkt7DhNb4PG9mEHYK/vMg2MS6NX5BanfPaGnwEpwPf6BpuaohjmN+pTEPYH2IrWqe5h++bL0lL4F6fHb6WW6D8n8I5toDoB948fP5+/lhYXKGHY/gZf0Or5lG5tb6Ys5xBkWV54X3z9+6DUzE28FKggXyEDxTniBWnuQujbi4Jnracg0A0AgA3hkNDyD7cxLw+Ep9FR67LT0P75GtGJvAROMJjkUR2i2XDX2P35TIpT6RoWapN97QXQ+fKOzeq3myGhV8YYlV4ogV0HnEJQC3fCVW843X4n/QzyZgGV/EUw1qI24p3YhRWS+YUxmARrvrALLn2LicOX58n79+J8aJRzaujLcGMulVfeFC0F7NwSW7V1KhFmGjt5MsGkAlhLLoWrLvNuNlbRFQXEufsah86D/B/wD/xAApEQEAAgADBgYDAQAAAAAAAAABABEhMUFAUWFxkaEQIIGxwdEw4fBw/9oACAECAQE/EP8ABwVomYHrh7z+Kfcz3NlosPuYBVXv1ihnEKV8Mfa4/WHmP1LdnMrv/XKNl0dNiHC8Bv8A1DCJFoIq5Hv5xTUmNJhBLlwik61m/iS48tHYFI1gEsDwQRkYvPyFT6hEjpVw9nJT62C3dCus08EW73t5bcMowHRTvOE7+c4nH4ijH4nHr5TjcIqGaX1x9oex7fn7GwZnHqfkPKYEy7fiAFJYeNdMPz0S5OD6+AzOOvxDGcb8sYhQ+jF7VN2sPlR/d4KGkRK5uwHcYO54EZyvIdvg7uwgHpIaYfY8vAahFkPsolJy9X62QGhs3M/VsdJdvuM0OcYuz22QSwbvSDqMXLdLVrrG4vdhymIwUKrfLzS7qv5n8Idjcxwr5lwuVwGvQeh+4Q1eDXJRh9mhy0yiSAw94c3P42Ptfme5gscKX6koxscHrd10jVlwdSothVL9G4o+ZfevrY2y5xAmKBURpeBpNfxzvWALQYzvu0xI9r/g/wD/xAArEAEAAQMDAwMEAgMBAAAAAAABEQAhMUFRYRBxgVCRoSBAsfAw0ZDB4fH/2gAIAQEAAT8Q/wABRTmr9MFqyemNQiyAvLak10hX7FC8NKd1O+wU2Ad6FDfDI9o0MPcgR7l+KtAsAEe6z2oTf0hmS1SavNdtgu/61paYcBBu1/0TTtZ1j7X06cOgoosyZ3oYs28tuxg4vHFMuWklzbC9mHihHWacPosxRXVnxs8GNYw5v8DBMwGBewQH0NATJUsMXQ/LHzQVBNGP4B/NQN2O+algDqgvZCkDGPwV7L2WrVm9s/K+BvtiKlHWWRPQzGKMGBDsX5O+jmjBCZVu1p655YW8eeClxerW/Lfz1SpqmpAIbKYctPxxV8OLWjbJ+lSazA248+OGHDogSFYpK1X0GcgDEnSUbW0HLog0MwBZVLKq5WsK09ClDoxEywXgytCS9wROGEtYh7KTs7NrcBBHeamX8lXuq0igcg7hAR+KBdRQ0aEQihjMJPN0twsWIPFSFTVgXQeUjUf29X3GQ31j+nakB2DYbXhYdrNr0Ikj6AL9cMClfYpVZEZA2PxdjKrrTpdOas4yIA8tEcrZ67OYKwad5aCC30GKaioChkRslEjyQL2i7r589lL6OSpqh9KDNgm3h+Jqyb0+XorL/XCPJOvoCurM6MMcfCClenalSxVpCPEX5DsPuFH04qaDNtaPQ5grPA3/AEyZqmpUc334fEUmq4cG89mHxSsP37tApJELD7l5pXp4pU8VBWAEMIPlU/gW1SfIIRNRKfINhMywnWQp1U9KZ1eJ7jRbXpjhyst48ovmrfezcHewK/inhLPlEr7071cOg6AOBKWscfwtITIaEivAB30lW1xNA03tIo+aB1+9ulDGdkD807U6vdamFuLfRJfde1c/wqb/AI7ogo0XyzRaM/5NT98IRKP4LvSdK/Rw0L0FJlNIN1GvH8KAiI/KR0tROvLxg/E+aaSdMF98iCLt1F8x05KfU41Rg4hlRokjwtQhY3lquRkeR/hBTYjxQ8X4qSITPvDQAQAAYApoyqyHe5PxRR94RIUQjrUwbOEXfuRPRfSw1YqXsyz1oPJYew6MnWCUpE3P4D30JMCHCJxslYquNjYOhpbTKLWEH596tfvcURYGGIAg3gbnppzrUdc1CwZZl/O/oabOGFbTDsmR4YTrNTRUXlaNEMGrjg1gpApwTC2JrwIHAddzoIcowB5oG1hkSC/ky+fvm16UhBcghh1ZmgZoQvQlx9BXJRuyMtoGiWHCJRzkgWs7rkXsikyc1Q0WZr1wm9Yh3kvhpv8ALC+QkB2RrO61MG18HHX760x4cCWPi/BN1FWj75JIpj7SDLnihZOQXRqCg3qbHUoK5a5+sclFCkqnWYUoiMBT2BkYy5dsBwHoNtfQJD9wOmoo2WpKMlEtydV8XGEQwVzVOfQHz9Pl6JQszRzptzWNqXuC62K0ZCRuoGvoOLFWtiZIrR3dpPJCVcdqi8+2qN+4wiF7ph9AK9VOdHRYdGuo2J120a7UAFvQvNaXoMN1TIeE4tcYuJRuzlIfO+5hcWKg1rlrlo96OVFChUqRsixwG6WHdoz7RYv8W4OriA1InHoyCQ0oRcoB787dSTq0IqyDGMWLLsqAhZIqG4AejzUOJpJmoAAlWiRmkwkeyjsaeSqVPZVAdh81gMiELu7u63fSQ2qIqzWRMHw0KzUlcvANMSB4R7FXBjv3Takf2Qr4FIUTFXjT0ckLUIpdpq7H7/enYMIargL1a9UyCN4WhyxIYO8PRnGQQJwZaa0QXBHu0Vx8BHslESgDek+Xwh3BoijW3otv7ltWio0gIVND97Gn6WhOoUuFZS6iWBogY5pVGVaSROOJJJDZSIDayuIc/q27UunFOcgoyjDlQHeruMjgG0nzL7QQCz9FyDCJhoOoyXDQr2BbyheaT6K//kBJIUoOkawGgOAKvocJtEeFWraGhGAQ3Q0vJ70qMq+ag6EMskDgjVlVvEAsIh9ZMD2vL5aHQvZioqBlgJtniIeUjQXBdbXY4p1D33INlBJonAIiRkq4SnMheJExbUoQZE5Gt4Rfc9FWzS6+My0x/pCpPlBKC3nYB3GgA4JMlgA2BH3dY9AP2tded/sVNn6iH+029FwfoGU+136llDzArnMTa8ye1EuNE2IgPBJ4OiFkE/8Atq/966mN3pNetX/qW9FwfoGU+0TsMTiSobIZpjNyqsGW2kS0o8lJZYgcz7HR3IZLHd7A5S7NCQMsvUkEZJcJmnDtYINHDnVdy1nKrY6R9ypiwSTdxeupMEExaxSgZdCmeODG6hrQhDAR6KJhvUXmwUQMRmGjcHFRUSwZl6VBsrlMFukWa1FizpSUywWptNjqjgEFzHZT01dRecxIL8AWAq9yxsIYQuJuUoorAeyad0KRwjnLgIkHDtQU3lz7qAjftJsFBz/s+wPmtAsFeNQgGgfLetfRTq96je9RavzX5rn4r87VG7Qbf4Cf/9k="\n    },\n    "button": [\n        {\n            "btmColour": "#0f426e",\n            "tag": "button1",\n            "fontColour": "#ffffff"\n        }\n    ],\n    "theme": [\n        {\n            "btmColour": "#283d81",\n            "tag": "button1",\n            "fontSize": 16\n        },\n        {\n            "btmColour": "#283d81",\n            "tag": "button2",\n            "fontSize": 16\n        }\n    ],\n    "header": [\n        {\n            "colour": "#a7d7ff",\n            "tag": "gradient1"\n        },\n        {\n            "colour": "#ddebf8",\n            "tag": "gradient2"\n        },\n        {\n            "colour": "#ffcccf",\n            "tag": "gradient3"\n        }\n    ]\n}	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	1	2020-01-21 09:47:41	Tor Alter	2020-01-21 09:53:20	Tor Alter
\.


--
-- TOC entry 3967 (class 0 OID 16469)
-- Dependencies: 206
-- Data for Name: t_shelf_tmp; Type: TABLE DATA; Schema: public; Owner: digitalshelf
--

COPY public.t_shelf_tmp (uuid, tmp_name, value, current_vcs_uuid, previous_vcs_uuid, attr1, attr2, attr3, attr4, attr5, attr6, attr7, attr8, attr9, attr10, description, status, create_at, create_by, update_at, update_by, company_code, bussiness_dept, business_line) FROM stdin;
77dcd446-436d-404b-82bc-6ce8a925cec2	Digital Lending Template	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	112	2020-01-10 12:27:20	Tor Alter	\N	\N	D1	implement2	develop
\.


--
-- TOC entry 3968 (class 0 OID 16476)
-- Dependencies: 207
-- Data for Name: t_shelf_tmp_attach; Type: TABLE DATA; Schema: public; Owner: digitalshelf
--

COPY public.t_shelf_tmp_attach (uuid, tmp_uuid, type, value, effective_date, attr1, attr2, attr3, attr4, attr5, attr6, attr7, attr8, attr9, attr10, description, status, create_at, create_by, update_at, update_by, vcs_uuid) FROM stdin;
3167535a-6f79-458c-a878-64a5dce6ad17	77dcd446-436d-404b-82bc-6ce8a925cec2	team&condition	{"type":"application/html","value":"PGI+dGVzdDwvYj4="}	2020-01-01	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	111	2020-01-10 12:27:20	Tor Alter	\N	\N	\N
\.


--
-- TOC entry 3969 (class 0 OID 16483)
-- Dependencies: 208
-- Data for Name: t_shelf_tmp_detail; Type: TABLE DATA; Schema: public; Owner: digitalshelf
--

COPY public.t_shelf_tmp_detail (uuid, comp_uuid, vcs_uuid, lookup_uuid, attr1, attr2, attr3, attr4, attr5, attr6, attr7, attr8, attr9, attr10, description, status, create_at, create_by, update_at, update_by, seq_no, att_uuid, value, flag_enable) FROM stdin;
b4d9252e-55b0-459d-8347-1c6448f446c6	af0f3037-5d24-454f-b81e-ed33e0f5f334	8ffe33b5-590f-468f-8950-04b1756f6fa8	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-10 12:27:20	Tor Alter	\N	\N	1	\N	{}	f
453f8b87-3e80-45e2-9c4e-112529b549e5	b29f1ed0-aa1e-4b4c-8053-2808fdb04431	8ffe33b5-590f-468f-8950-04b1756f6fa8	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-10 12:27:20	Tor Alter	\N	\N	2	\N	{}	t
a06695e2-114e-484b-8c1f-5cc2c627c7ee	169e986b-5a0a-45c9-8b08-890841b5b23f	8ffe33b5-590f-468f-8950-04b1756f6fa8	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-10 12:27:20	Tor Alter	\N	\N	3	\N	{"type":"text","value":"abcdefg"}	t
992f56d9-d145-46e0-b434-0d5b1084c6a1	ae6ec58b-e9b1-4ddf-a116-4aea79dcefc5	8ffe33b5-590f-468f-8950-04b1756f6fa8	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-10 12:27:20	Tor Alter	\N	\N	4	3167535a-6f79-458c-a878-64a5dce6ad17	\N	t
5a392cef-ad8f-457f-89ab-ad88c0155123	a34404a3-309e-4057-8f53-f386d026656b	8ffe33b5-590f-468f-8950-04b1756f6fa8	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-10 12:27:20	Tor Alter	\N	\N	5	\N	{}	t
35980872-ecab-447b-86b8-accbfdce9438	ec654003-0763-48d3-af08-8a9b053783e4	8ffe33b5-590f-468f-8950-04b1756f6fa8	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-10 12:27:20	Tor Alter	\N	\N	6	\N	{}	t
74d57885-359b-4991-94fb-fc7f6c1c7c65	ed8880d3-e353-48c6-bfd7-debe70ba6c37	8ffe33b5-590f-468f-8950-04b1756f6fa8	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-10 12:27:20	Tor Alter	\N	\N	7	\N	{}	t
68a3515b-9c33-40ec-adf5-88d60742cdc1	d4882b6c-8e4b-441d-86fc-019c8eb4c232	8ffe33b5-590f-468f-8950-04b1756f6fa8	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-10 12:27:20	Tor Alter	\N	\N	8	\N	{}	t
99c8de90-3969-43ed-b4d1-7fec64dffc4d	723b3819-e1e0-4756-a6f2-50fdaf14d85d	8ffe33b5-590f-468f-8950-04b1756f6fa8	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-10 12:27:20	Tor Alter	\N	\N	9	\N	{}	t
f9ffeb25-1db2-41b3-9173-439e8bc41c6b	2787aafe-e4e8-4f09-a3b8-5838a7595dbe	8ffe33b5-590f-468f-8950-04b1756f6fa8	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-10 12:27:20	Tor Alter	\N	\N	10	\N	{}	t
\.


--
-- TOC entry 3970 (class 0 OID 16491)
-- Dependencies: 209
-- Data for Name: t_shelf_tmp_vcs; Type: TABLE DATA; Schema: public; Owner: digitalshelf
--

COPY public.t_shelf_tmp_vcs (uuid, tmp_uuid, state, attr1, attr2, attr3, attr4, attr5, attr6, attr7, attr8, attr9, attr10, description, status, create_at, create_by, update_at, update_by, effective_date, version, vcs_uuid) FROM stdin;
8ffe33b5-590f-468f-8950-04b1756f6fa8	77dcd446-436d-404b-82bc-6ce8a925cec2	\N	[{\n        "compUuid": "ae6ec58b-e9b1-4ddf-a116-4aea79dcefc5",\n        "seqNo": 4,\n        "headerText": "เงื่อนไขการให้บริการ",\n        "radioMandatory": "",\n        "termsNCondition": "<p>ผู้ขอใช้บริการในฐานะเจ้าของบัญชีเงินฝากประจำ และ/หรือ บัญชีเงินฝากประเภทออมทรัพย์ และ/หรือ บัญชีเงินฝากกระแสรายวัน (ซึ่งต่อไปนี้ หากมิได้เรียกบัญชีเงินฝากประเภทใดประเภทหนึ่งเป็นการเฉพาะ จะรวมเรียกว่า \\"บัญชีเงินฝาก\\")ของธนาคารทิสโก้จำกัด (มหาชน) (ซึ่งต่อไปนี้จะเรียกว่า \\" ธนาคาร \\" ) ยินยอมปฏิบัติตามข้อตกลงและเงื่อนไขดังต่อไปนี้&nbsp;</p><p>การเปิดบัญชีเงินฝาก&nbsp;</p><p>1. การเปิดบัญชีเงินฝากให้เป็นไปตามเงื่อนไขและข้อกำหนดการใช้บริการของผลิตภัณฑ์เงินฝากประเภทนั้น ๆ โดยชื่อบัญชีเงินฝากต้องเป็น ชื่อบุคคลธรรมดาหรือนิติบุคคลที่เป็นเจ้าของบัญชี&nbsp;</p><p>2. จำนวนเงินฝากเข้าบัญชีครั้งแรก ณ วันเปิดบัญชี จะต้องไม่ต่ ากว่าจ านวนเงินที่ธนาคารก าหนด ดอกเบี้ย&nbsp;</p><p>3. ผู้ขอใช้บริการยินยอมให้ธนาคารจ่ายดอกเบี้ยในอัตราที่ธนาคารประกาศกำหนดไว้สำหรับบัญชีเงินฝากแต่ละประเภทตามหลักเกณฑ์ ที่ธนาคารแห่งประเทศไทยกำหนด โดยธนาคารจะคำนวณดอกเบี้ยเป็นรายวัน จากยอดเงินคงเหลือ ณ สิ้นวันของอัตราดอกเบี้ยตาม วงเงินที่ประกาศในขณะนั้น ทั้งนี้ธนาคารอาจเปลี่ยนแปลงอัตราดอกเบี้ยดังกล่าวได้ตามสภาวะการณ์ตลาด โดยจะประกาศ ณ ที่ท าการ ธนาคารทุกสาขาและเว็บไซต์ของธนาคาร&nbsp;</p><p>4. ธนาคารจะจ่ายดอกเบี้ยหลังจากมีการหักภาษีเงินได้ณ ที่จ่าย สำหรับดอกเบี้ยเงินฝากออมทรัพย์ที่ไม่เข้าหลักเกณฑ์และเงื่อนไขที่ได้รับ การยกเว้นภาษีเงินได้ ตามที่ประกาศกรมสรรพากรก าหนด การฝากเงิน และ การถอนเงิน&nbsp;</p><p>5. สามารถฝากเงินผ่านทุกสาขาของธนาคาร และตัวแทนที่ได้รับการแต่งตั้งจากธนาคาร ได้แก่ ที่ท าการไปรษณีย์ไทย โดยธนาคารจะยกเว้น การเรียกเก็บค่าธรรมเนียมฝากเงินข้ามเขต&nbsp;</p><p>6. ในการฝากเงินด้วยเช็ค ตั๋วแลกเงิน ตั๋วสัญญาใช้เงิน และ/หรือ ตราสารอื่นที่เปลี่ยนมือได้ซึ่งต่อไปนี้จะเรียกรวมกันว่า \\"ตราสารการเงิน\\" เข้าบัญชีเงินฝาก ผู้ขอใช้บริการตกลงจะปฏิบัติดังต่อไปนี้&nbsp;</p><p style=\\"margin-left: 20px;\\">6.1 ผู้ขอใช้บริการมีสิทธิถอนเงินตามตราสารการเงินที่ฝากในกรณีนี้ได้ก็ต่อเมื่อธนาคารเรียกเก็บเงินตามตราสารการเงินและนำเงินเข้า บัญชีของผู้ขอใช้บริการเรียบร้อยแล้ว&nbsp;</p><p style=\\" margin-left: 20px;\\">6.2 ในกรณีที่ธนาคารเรียกเก็บเงินตามตราสารการเงินไม่ได้ ธนาคารจะยกเลิกรายการรับฝากและจัดส่งตราสารการเงินดังกล่าวคืนไปยัง ผู้ขอใช้บริการ หรือจะมีหนังสือแจ้งให้ผู้ขอใช้บริการมารับคืนจากธนาคารก็ได้ ทั้งนี้ในการจัดส่งคืนหรือมีหนังสือแจ้งดังกล่าว ธนาคารจะจัดส่งหรือแจ้งไปยังที่อยู่ของผู้ขอใช้บริการที่ได้แจ้งไว้กับธนาคาร แต่หากส่งไม่ถึงหรือมีหนังสือแจ้งไปยังผู้ขอใช้บริการ ไม่ได้ เพราะผู้ขอใช้บริการย้ายที่อยู่หรือเพราะเหตุอื่นใด และมีความเสียหายเกิดขึ้น ธนาคารจะไม่รับผิดชอบในความเสียหาย ดังกล่าวแต่อย่างใดทั้งสิ้น&nbsp;</p><p style=\\"margin-left: 20px;\\">6.3 ในกรณีที่ผู้ขอใช้บริการซึ่งเป็นบุคคลธรรมดานำตราสารทางการเงินซึ่งสั่งจ่ายให้แก่นิติบุคคลใดๆ มาฝากเข้าบัญชีส่วนตัวของ ผู้ขอใช้บริการธนาคารอาจปฏิเสธไม่ยอมรับฝากก็ได้แม้ว่าตราสารการเงินนั้นจะมีผลเป็นการสั่งจ่ายให้แก่ผู้ถือหรือมีการสลักหลัง โดยชอบของนิติบุคคลนั้นๆแล้วก็ตาม </p>",\n        "g001": "Y",\n        "activeDate": "2020-01-01 00:00:000",\n        "chkBoxLabel": "",\n        "verTermsNConditionList": "",\n        "uuid": "0c7259bd-aa2b-471c-a23f-366583d79e91",\n        "compName": "TERM & CONDITION",\n        "version": ""\n    }, {\n        "compUuid": "ec654003-0763-48d3-af08-8a9b053783e4",\n        "chkConsentList": [{\n                "require": "N",\n                "seq": 1,\n\t\t\t\t"id":1\n            }, {\n                "require": "N",\n                "seq": 2,\n\t\t\t\t"id":2\n            }, {\n                "require": "N",\n                "seq": 3,\n\t\t\t\t"id":3\n            }\n        ],\n        "seqNo": 6,\n        "headerText": "{value:\\"\\",attr1:\\"1\\"}",\n        "g001": "Y",\n        "radioConsent": "CC",\n        "uuid": "183b450d-d189-4199-b320-cc8ecfa360f7",\n        "compName": "CONSENT",\n        "content": "",\n        "consentName": "c286b59e-423e-11ea-b77f-2e728ce88125"\n    }\n]	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-10 12:27:20	Tor Alter	\N	\N	2020-01-01	0	\N
\.


--
-- TOC entry 3971 (class 0 OID 16498)
-- Dependencies: 210
-- Data for Name: t_sys_audit_log; Type: TABLE DATA; Schema: public; Owner: digitalshelf
--

COPY public.t_sys_audit_log (uuid, log_name, source, event_id, level, task_category, keywords, computer, account_name, account_domain, access_type, object_name, resource_attribute, attr1, attr2, attr3, attr4, attr5, attr6, attr7, attr8, attr9, attr10, status, create_at, create_by) FROM stdin;
\.


--
-- TOC entry 3975 (class 0 OID 16659)
-- Dependencies: 214
-- Data for Name: t_sys_log; Type: TABLE DATA; Schema: public; Owner: digitalshelf
--

COPY public.t_sys_log (uuid, prod_code, case_id, group_product, state, attr1, attr2, attr3, attr4, attr5, attr6, attr7, attr8, attr9, attr10, status, create_at, create_by, update_at, update_by) FROM stdin;
\.


--
-- TOC entry 3972 (class 0 OID 16505)
-- Dependencies: 211
-- Data for Name: t_sys_lookup; Type: TABLE DATA; Schema: public; Owner: digitalshelf
--

COPY public.t_sys_lookup (uuid, lookup_code, lookup_name_th, lookup_name_en, lookup_type, lookup_value, attr1, attr2, attr3, attr4, attr5, attr6, attr7, attr8, attr9, attr10, status, description, create_at, create_by, update_at, update_by, flag_edit, flag_create) FROM stdin;
fd8d8c8b-5a1b-477b-82a5-95b6fd3eea3b	213	เปิดการใช้งาน	Active	status	active	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	2020-01-06 00:20:23	tOr	\N	\N	f	t
f6f628b7-640f-4c6b-9825-cf735724bfb1	111	กำลังจัดทำ	In Progress	status	inprogress	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	2020-01-06 00:24:07	tOr	\N	\N	t	f
11353d55-8321-421d-9aa7-cd760e7b671f	214	ปฎิเสธ	Reject	status	reject	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	2020-01-08 01:30:10	tOr	\N	\N	t	f
9f168c61-2f2e-403f-9e9f-d90dd632121a	225	ลบ	Delete	status	delete	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	2020-01-08 01:38:22	tOr	\N	\N	f	f
3d8b8f49-b519-4d4c-886c-8a35458ba514	226	เลิกใช้งาน	Terminate	status	terminate	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	2020-01-08 01:39:54	tOr	\N	\N	f	f
cf664fc3-af20-41ab-9ec1-a7ec209072c4	115	รอการอนุมัติ	Wait to Delete	status	waittodelete	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	2020-01-08 01:34:40	tOr	\N	\N	f	f
7217762c-cc95-4c09-872d-400e535f9813	0	หยุดชั่วเคราว	Pause	status	pause	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	2020-01-08 01:41:35	tOr	\N	\N	t	f
5c527f75-d3be-4870-bc06-e808f1b2284d	218	ปิดการใช้งาน	InActive	status	inactive	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	สถานะที่เกิดจากเงื่อนไขยังไม่ถึงวันที่ Active	2020-01-06 00:22:56	tOr	\N	\N	f	t
3976826d-29fe-4c95-ae8e-0001f61f803c	112	รออนุมัติ	Wait To Approve	status	waittoapprove	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	2020-01-05 19:59:53	tOr	\N	\N	f	f
8c3baee6-4323-11ea-b77f-2e728ce88125	200	ผ่าน	pass	status	pass	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	2020-01-30 12:47:01	develop	\N	\N	f	f
8c3bb170-4323-11ea-b77f-2e728ce88125	500	ไม่ผ่าน	fail	status	fail	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	2020-01-30 12:47:01	develop	\N	\N	f	f
3130c81a-434b-11ea-b77f-2e728ce88125	201	โอกาส	Prospect	status	prospect	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	มีโอกาสที่จะเป็นลูกค้า	2020-01-30 17:28:25	develop	\N	\N	f	f
\.


--
-- TOC entry 3973 (class 0 OID 16512)
-- Dependencies: 212
-- Data for Name: t_sys_oper_log; Type: TABLE DATA; Schema: public; Owner: digitalshelf
--

COPY public.t_sys_oper_log (uuid, trn_id, source, product_id, product_version_id, product_component_id, task_category, keywords, trn_status, trn_sub_status, failure_reason, source_device, source_device_id, source_cif_id, account_name, attr1, attr2, attr3, attr4, attr5, attr6, attr7, attr8, attr9, attr10, status, create_at, create_by, state_code, state_time) FROM stdin;
e7e98e07-b603-4d0e-9549-b0434d671ac7	7fb31bbe-d1c6-4edf-bbdf-87fd102d2604	web	e0c80f8b-627e-48b4-9384-87d00dca5274	1				500	500	ระบุข้อมูลไม่ถูกต้อง					\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	\N	\N	71227b54-4322-11ea-b77f-2e728ce88125	1234567
58be654f-83b6-4500-af58-95dcc3e3d6bf	0e90bfbe-063f-4791-9e18-37d7da82f9b4	web	e0c80f8b-627e-48b4-9384-87d00dca5275	1				500	500	ระบุข้อมูลไม่ถูกต้อง					\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-30 12:55:37	\N	71227b54-4322-11ea-b77f-2e728ce88125	1234567
1df6cb0c-d1d9-4eb5-a8af-ada84540888d	7fb31bbe-d1c6-4edf-bbdf-87fd102d2604	web	e0c80f8b-627e-48b4-9384-87d00dca5274	1				200	200						\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-30 13:55:06	\N	71227e06-4322-11ea-b77f-2e728ce88125	1234567
ec6d54cd-48dd-4e8c-a433-f676216aafe2	7fb31bbe-d1c6-4edf-bbdf-87fd102d2604	web	e0c80f8b-627e-48b4-9384-87d00dca5274	1				200	200						\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-30 14:22:07	\N	71227f5a-4322-11ea-b77f-2e728ce88125	1234567
\.


--
-- TOC entry 3974 (class 0 OID 16519)
-- Dependencies: 213
-- Data for Name: t_sys_role; Type: TABLE DATA; Schema: public; Owner: digitalshelf
--

COPY public.t_sys_role (uuid, role_id, role_code, role_name_th, role_name_en, pemission, attr1, attr2, attr3, attr4, attr5, attr6, attr7, attr8, attr9, attr10, status, create_at, create_by) FROM stdin;
0cd00aac-35a2-4285-92a1-ae9a02c65d49	maker	maker	ผู้ปฎิบัติงาน	maker	{"template":{"create":true,"edit":true,"delete":true,"approve":false}}	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-10 14:17:12.353	tOr
30be093e-dd09-4937-ba8a-eb8356b53cf3 	checker	checker	ผู้อนุมัติ	checker	{"template":{"create":false,"edit":false,"delete":false,"approve":true}}	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	213	2020-01-10 14:18:16.669	tOr
\.


--
-- TOC entry 4020 (class 0 OID 0)
-- Dependencies: 199
-- Name: t_shelf_comp_seq_no_seq; Type: SEQUENCE SET; Schema: public; Owner: digitalshelf
--

SELECT pg_catalog.setval('public.t_shelf_comp_seq_no_seq', 10, true);


--
-- TOC entry 3790 (class 2606 OID 16532)
-- Name: t_shelf_comp_dtl t_shelf_comp_dtl_pkey; Type: CONSTRAINT; Schema: public; Owner: digitalshelf
--

ALTER TABLE ONLY public.t_shelf_comp_dtl
    ADD CONSTRAINT t_shelf_comp_dtl_pkey PRIMARY KEY (uuid);


--
-- TOC entry 3788 (class 2606 OID 16534)
-- Name: t_shelf_comp t_shelf_comp_pkey; Type: CONSTRAINT; Schema: public; Owner: digitalshelf
--

ALTER TABLE ONLY public.t_shelf_comp
    ADD CONSTRAINT t_shelf_comp_pkey PRIMARY KEY (uuid);


--
-- TOC entry 3792 (class 2606 OID 16536)
-- Name: t_shelf_lookup t_shelf_lookup_pkey; Type: CONSTRAINT; Schema: public; Owner: digitalshelf
--

ALTER TABLE ONLY public.t_shelf_lookup
    ADD CONSTRAINT t_shelf_lookup_pkey PRIMARY KEY (uuid);


--
-- TOC entry 3796 (class 2606 OID 16538)
-- Name: t_shelf_product_attach t_shelf_product_dtl_attach_pkey; Type: CONSTRAINT; Schema: public; Owner: digitalshelf
--

ALTER TABLE ONLY public.t_shelf_product_attach
    ADD CONSTRAINT t_shelf_product_dtl_attach_pkey PRIMARY KEY (uuid);


--
-- TOC entry 3798 (class 2606 OID 16540)
-- Name: t_shelf_product_dtl t_shelf_product_dtl_pkey; Type: CONSTRAINT; Schema: public; Owner: digitalshelf
--

ALTER TABLE ONLY public.t_shelf_product_dtl
    ADD CONSTRAINT t_shelf_product_dtl_pkey PRIMARY KEY (uuid);


--
-- TOC entry 3794 (class 2606 OID 16542)
-- Name: t_shelf_product t_shelf_product_pkey; Type: CONSTRAINT; Schema: public; Owner: digitalshelf
--

ALTER TABLE ONLY public.t_shelf_product
    ADD CONSTRAINT t_shelf_product_pkey PRIMARY KEY (uuid);


--
-- TOC entry 3800 (class 2606 OID 16544)
-- Name: t_shelf_product_vcs t_shelf_product_vcs_pkey; Type: CONSTRAINT; Schema: public; Owner: digitalshelf
--

ALTER TABLE ONLY public.t_shelf_product_vcs
    ADD CONSTRAINT t_shelf_product_vcs_pkey PRIMARY KEY (uuid);


--
-- TOC entry 3806 (class 2606 OID 16546)
-- Name: t_shelf_tmp_attach t_shelf_tmp_attach_pkey; Type: CONSTRAINT; Schema: public; Owner: digitalshelf
--

ALTER TABLE ONLY public.t_shelf_tmp_attach
    ADD CONSTRAINT t_shelf_tmp_attach_pkey PRIMARY KEY (uuid);


--
-- TOC entry 3808 (class 2606 OID 16548)
-- Name: t_shelf_tmp_detail t_shelf_tmp_detail_pkey; Type: CONSTRAINT; Schema: public; Owner: digitalshelf
--

ALTER TABLE ONLY public.t_shelf_tmp_detail
    ADD CONSTRAINT t_shelf_tmp_detail_pkey PRIMARY KEY (uuid);


--
-- TOC entry 3804 (class 2606 OID 16550)
-- Name: t_shelf_tmp t_shelf_tmp_pkey; Type: CONSTRAINT; Schema: public; Owner: digitalshelf
--

ALTER TABLE ONLY public.t_shelf_tmp
    ADD CONSTRAINT t_shelf_tmp_pkey PRIMARY KEY (uuid);


--
-- TOC entry 3802 (class 2606 OID 16552)
-- Name: t_shelf_theme t_shelf_tmp_theme_pkey; Type: CONSTRAINT; Schema: public; Owner: digitalshelf
--

ALTER TABLE ONLY public.t_shelf_theme
    ADD CONSTRAINT t_shelf_tmp_theme_pkey PRIMARY KEY (uuid);


--
-- TOC entry 3810 (class 2606 OID 16554)
-- Name: t_shelf_tmp_vcs t_shelf_tmp_vcs_pkey; Type: CONSTRAINT; Schema: public; Owner: digitalshelf
--

ALTER TABLE ONLY public.t_shelf_tmp_vcs
    ADD CONSTRAINT t_shelf_tmp_vcs_pkey PRIMARY KEY (uuid);


--
-- TOC entry 3812 (class 2606 OID 16556)
-- Name: t_sys_audit_log t_sys_audit_log_pkey; Type: CONSTRAINT; Schema: public; Owner: digitalshelf
--

ALTER TABLE ONLY public.t_sys_audit_log
    ADD CONSTRAINT t_sys_audit_log_pkey PRIMARY KEY (uuid);


--
-- TOC entry 3820 (class 2606 OID 16667)
-- Name: t_sys_log t_sys_log_pkey; Type: CONSTRAINT; Schema: public; Owner: digitalshelf
--

ALTER TABLE ONLY public.t_sys_log
    ADD CONSTRAINT t_sys_log_pkey PRIMARY KEY (uuid);


--
-- TOC entry 3814 (class 2606 OID 16558)
-- Name: t_sys_lookup t_sys_lookup_pkey; Type: CONSTRAINT; Schema: public; Owner: digitalshelf
--

ALTER TABLE ONLY public.t_sys_lookup
    ADD CONSTRAINT t_sys_lookup_pkey PRIMARY KEY (uuid);


--
-- TOC entry 3816 (class 2606 OID 16560)
-- Name: t_sys_oper_log t_sys_oper_log_pkey; Type: CONSTRAINT; Schema: public; Owner: digitalshelf
--

ALTER TABLE ONLY public.t_sys_oper_log
    ADD CONSTRAINT t_sys_oper_log_pkey PRIMARY KEY (uuid);


--
-- TOC entry 3818 (class 2606 OID 16562)
-- Name: t_sys_role t_sys_role_pkey; Type: CONSTRAINT; Schema: public; Owner: digitalshelf
--

ALTER TABLE ONLY public.t_sys_role
    ADD CONSTRAINT t_sys_role_pkey PRIMARY KEY (uuid);


--
-- TOC entry 3829 (class 2606 OID 16563)
-- Name: t_shelf_tmp_attach attr_tmp_uuid; Type: FK CONSTRAINT; Schema: public; Owner: digitalshelf
--

ALTER TABLE ONLY public.t_shelf_tmp_attach
    ADD CONSTRAINT attr_tmp_uuid FOREIGN KEY (tmp_uuid) REFERENCES public.t_shelf_tmp(uuid);


--
-- TOC entry 3830 (class 2606 OID 16568)
-- Name: t_shelf_tmp_detail detail_sys_lookup_uuid; Type: FK CONSTRAINT; Schema: public; Owner: digitalshelf
--

ALTER TABLE ONLY public.t_shelf_tmp_detail
    ADD CONSTRAINT detail_sys_lookup_uuid FOREIGN KEY (lookup_uuid) REFERENCES public.t_sys_lookup(uuid);


--
-- TOC entry 3831 (class 2606 OID 16573)
-- Name: t_shelf_tmp_detail detail_tmp_att_uuid; Type: FK CONSTRAINT; Schema: public; Owner: digitalshelf
--

ALTER TABLE ONLY public.t_shelf_tmp_detail
    ADD CONSTRAINT detail_tmp_att_uuid FOREIGN KEY (att_uuid) REFERENCES public.t_shelf_tmp_attach(uuid);


--
-- TOC entry 3832 (class 2606 OID 16578)
-- Name: t_shelf_tmp_detail detail_tmp_comp_uuid; Type: FK CONSTRAINT; Schema: public; Owner: digitalshelf
--

ALTER TABLE ONLY public.t_shelf_tmp_detail
    ADD CONSTRAINT detail_tmp_comp_uuid FOREIGN KEY (comp_uuid) REFERENCES public.t_shelf_comp(uuid);


--
-- TOC entry 3833 (class 2606 OID 16583)
-- Name: t_shelf_tmp_detail detail_tmp_vcs_uuid; Type: FK CONSTRAINT; Schema: public; Owner: digitalshelf
--

ALTER TABLE ONLY public.t_shelf_tmp_detail
    ADD CONSTRAINT detail_tmp_vcs_uuid FOREIGN KEY (vcs_uuid) REFERENCES public.t_shelf_tmp_vcs(uuid);


--
-- TOC entry 3821 (class 2606 OID 16588)
-- Name: t_shelf_comp_dtl t_shelf_comp_dtl_comp_uuid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: digitalshelf
--

ALTER TABLE ONLY public.t_shelf_comp_dtl
    ADD CONSTRAINT t_shelf_comp_dtl_comp_uuid_fkey FOREIGN KEY (comp_uuid) REFERENCES public.t_shelf_comp(uuid);


--
-- TOC entry 3822 (class 2606 OID 16593)
-- Name: t_shelf_comp_dtl t_shelf_comp_dtl_lk_uuid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: digitalshelf
--

ALTER TABLE ONLY public.t_shelf_comp_dtl
    ADD CONSTRAINT t_shelf_comp_dtl_lk_uuid_fkey FOREIGN KEY (lk_uuid) REFERENCES public.t_shelf_lookup(uuid);


--
-- TOC entry 3823 (class 2606 OID 16598)
-- Name: t_shelf_product_attach t_shelf_product_attach_dtl_uuid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: digitalshelf
--

ALTER TABLE ONLY public.t_shelf_product_attach
    ADD CONSTRAINT t_shelf_product_attach_dtl_uuid_fkey FOREIGN KEY (dtl_uuid) REFERENCES public.t_shelf_product_dtl(uuid);


--
-- TOC entry 3824 (class 2606 OID 16603)
-- Name: t_shelf_product_dtl t_shelf_product_dtl_trn_uuid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: digitalshelf
--

ALTER TABLE ONLY public.t_shelf_product_dtl
    ADD CONSTRAINT t_shelf_product_dtl_trn_uuid_fkey FOREIGN KEY (trn_uuid) REFERENCES public.t_shelf_product_vcs(uuid);


--
-- TOC entry 3825 (class 2606 OID 16608)
-- Name: t_shelf_product_vcs t_shelf_product_vcs_comp_uuid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: digitalshelf
--

ALTER TABLE ONLY public.t_shelf_product_vcs
    ADD CONSTRAINT t_shelf_product_vcs_comp_uuid_fkey FOREIGN KEY (comp_uuid) REFERENCES public.t_shelf_comp(uuid);


--
-- TOC entry 3826 (class 2606 OID 16613)
-- Name: t_shelf_product_vcs t_shelf_product_vcs_prod_uuid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: digitalshelf
--

ALTER TABLE ONLY public.t_shelf_product_vcs
    ADD CONSTRAINT t_shelf_product_vcs_prod_uuid_fkey FOREIGN KEY (prod_uuid) REFERENCES public.t_shelf_product(uuid);


--
-- TOC entry 3827 (class 2606 OID 16618)
-- Name: t_shelf_product_vcs t_shelf_product_vcs_tem_uuid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: digitalshelf
--

ALTER TABLE ONLY public.t_shelf_product_vcs
    ADD CONSTRAINT t_shelf_product_vcs_tem_uuid_fkey FOREIGN KEY (tem_uuid) REFERENCES public.t_shelf_tmp(uuid);


--
-- TOC entry 3828 (class 2606 OID 16623)
-- Name: t_shelf_product_vcs t_shelf_product_vcs_theme_uuid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: digitalshelf
--

ALTER TABLE ONLY public.t_shelf_product_vcs
    ADD CONSTRAINT t_shelf_product_vcs_theme_uuid_fkey FOREIGN KEY (theme_uuid) REFERENCES public.t_shelf_theme(uuid);


--
-- TOC entry 3835 (class 2606 OID 16628)
-- Name: t_sys_oper_log t_sys_oper_log_state_code_fkey; Type: FK CONSTRAINT; Schema: public; Owner: digitalshelf
--

ALTER TABLE ONLY public.t_sys_oper_log
    ADD CONSTRAINT t_sys_oper_log_state_code_fkey FOREIGN KEY (state_code) REFERENCES public.t_shelf_lookup(uuid);


--
-- TOC entry 3834 (class 2606 OID 16633)
-- Name: t_shelf_tmp_vcs vsc_tmp_comp_uuid; Type: FK CONSTRAINT; Schema: public; Owner: digitalshelf
--

ALTER TABLE ONLY public.t_shelf_tmp_vcs
    ADD CONSTRAINT vsc_tmp_comp_uuid FOREIGN KEY (tmp_uuid) REFERENCES public.t_shelf_tmp(uuid);


--
-- TOC entry 3981 (class 0 OID 0)
-- Dependencies: 3
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: digitalshelf
--

REVOKE ALL ON SCHEMA public FROM rdsadmin;
REVOKE ALL ON SCHEMA public FROM PUBLIC;
GRANT ALL ON SCHEMA public TO digitalshelf;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2020-02-19 09:05:59

--
-- PostgreSQL database dump complete
--
