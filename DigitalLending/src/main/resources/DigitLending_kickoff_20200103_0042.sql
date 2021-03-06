PGDMP     +    *                  x            DigitalLending    10.8    11.2     "           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                       false            #           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                       false            $           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                       false            %           1262    82383    DigitalLending    DATABASE     �   CREATE DATABASE "DigitalLending" WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'Thai_Thailand.874' LC_CTYPE = 'Thai_Thailand.874';
     DROP DATABASE "DigitalLending";
             postgres    false            �            1259    82409    t_shelf_comp    TABLE     l  CREATE TABLE public.t_shelf_comp (
    uuid character varying(128) NOT NULL,
    seq_no bigint NOT NULL,
    comp_code character varying(100) NOT NULL,
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
    update_by character varying(100)
);
     DROP TABLE public.t_shelf_comp;
       public         postgres    false            �            1259    82407    t_shelf_comp_seq_no_seq    SEQUENCE     �   CREATE SEQUENCE public.t_shelf_comp_seq_no_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 .   DROP SEQUENCE public.t_shelf_comp_seq_no_seq;
       public       postgres    false    197            &           0    0    t_shelf_comp_seq_no_seq    SEQUENCE OWNED BY     S   ALTER SEQUENCE public.t_shelf_comp_seq_no_seq OWNED BY public.t_shelf_comp.seq_no;
            public       postgres    false    196            �            1259    82419    t_shelf_tmp    TABLE     �  CREATE TABLE public.t_shelf_tmp (
    uuid character varying(128) NOT NULL,
    tmp_name character varying(100) NOT NULL,
    value text,
    effective_date date NOT NULL,
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
    update_by character varying(100)
);
    DROP TABLE public.t_shelf_tmp;
       public         postgres    false            �            1259    82428    t_shelf_tmp_attach    TABLE     y  CREATE TABLE public.t_shelf_tmp_attach (
    uuid character varying(128) NOT NULL,
    tmp_uuid character varying(50) NOT NULL,
    type character varying(50) NOT NULL,
    value text NOT NULL,
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
    update_by character varying(100)
);
 &   DROP TABLE public.t_shelf_tmp_attach;
       public         postgres    false            �            1259    82437    t_shelf_tmp_detail    TABLE     }  CREATE TABLE public.t_shelf_tmp_detail (
    uuid character varying(128) NOT NULL,
    comp_uuid character varying(128) NOT NULL,
    vcs_uuid character varying(128) NOT NULL,
    lookup_uuid character varying(128),
    value json,
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
 &   DROP TABLE public.t_shelf_tmp_detail;
       public         postgres    false            �            1259    82446    t_shelf_tmp_vcs    TABLE     P  CREATE TABLE public.t_shelf_tmp_vcs (
    uuid character varying(128) NOT NULL,
    tmp_uuid character varying(128) NOT NULL,
    version character varying(100) NOT NULL,
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
 #   DROP TABLE public.t_shelf_tmp_vcs;
       public         postgres    false            �            1259    82455    t_sys_lookup    TABLE     �  CREATE TABLE public.t_sys_lookup (
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
    update_by character varying(100)
);
     DROP TABLE public.t_sys_lookup;
       public         postgres    false            �
           2604    82412    t_shelf_comp seq_no    DEFAULT     z   ALTER TABLE ONLY public.t_shelf_comp ALTER COLUMN seq_no SET DEFAULT nextval('public.t_shelf_comp_seq_no_seq'::regclass);
 B   ALTER TABLE public.t_shelf_comp ALTER COLUMN seq_no DROP DEFAULT;
       public       postgres    false    196    197    197                      0    82409    t_shelf_comp 
   TABLE DATA               �   COPY public.t_shelf_comp (uuid, seq_no, comp_code, comp_name, value, attr1, attr2, attr3, attr4, attr5, attr6, attr7, attr8, attr9, attr10, description, status, create_at, create_by, update_at, update_by) FROM stdin;
    public       postgres    false    197   f3                 0    82419    t_shelf_tmp 
   TABLE DATA               �   COPY public.t_shelf_tmp (uuid, tmp_name, value, effective_date, current_vcs_uuid, previous_vcs_uuid, attr1, attr2, attr3, attr4, attr5, attr6, attr7, attr8, attr9, attr10, description, status, create_at, create_by, update_at, update_by) FROM stdin;
    public       postgres    false    198   U5                 0    82428    t_shelf_tmp_attach 
   TABLE DATA               �   COPY public.t_shelf_tmp_attach (uuid, tmp_uuid, type, value, effective_date, attr1, attr2, attr3, attr4, attr5, attr6, attr7, attr8, attr9, attr10, description, status, create_at, create_by, update_at, update_by) FROM stdin;
    public       postgres    false    199   r5                 0    82437    t_shelf_tmp_detail 
   TABLE DATA               �   COPY public.t_shelf_tmp_detail (uuid, comp_uuid, vcs_uuid, lookup_uuid, value, attr1, attr2, attr3, attr4, attr5, attr6, attr7, attr8, attr9, attr10, description, status, create_at, create_by, update_at, update_by) FROM stdin;
    public       postgres    false    200   �5                 0    82446    t_shelf_tmp_vcs 
   TABLE DATA               �   COPY public.t_shelf_tmp_vcs (uuid, tmp_uuid, version, state, attr1, attr2, attr3, attr4, attr5, attr6, attr7, attr8, attr9, attr10, description, status, create_at, create_by, update_at, update_by) FROM stdin;
    public       postgres    false    201   �5                 0    82455    t_sys_lookup 
   TABLE DATA               �   COPY public.t_sys_lookup (uuid, lookup_code, lookup_name_th, lookup_name_en, lookup_type, lookup_value, attr1, attr2, attr3, attr4, attr5, attr6, attr7, attr8, attr9, attr10, status, description, create_at, create_by, update_at, update_by) FROM stdin;
    public       postgres    false    202   �5       '           0    0    t_shelf_comp_seq_no_seq    SEQUENCE SET     F   SELECT pg_catalog.setval('public.t_shelf_comp_seq_no_seq', 10, true);
            public       postgres    false    196            �
           2606    82418    t_shelf_comp t_shelf_comp_pkey 
   CONSTRAINT     ^   ALTER TABLE ONLY public.t_shelf_comp
    ADD CONSTRAINT t_shelf_comp_pkey PRIMARY KEY (uuid);
 H   ALTER TABLE ONLY public.t_shelf_comp DROP CONSTRAINT t_shelf_comp_pkey;
       public         postgres    false    197            �
           2606    82436 *   t_shelf_tmp_attach t_shelf_tmp_attach_pkey 
   CONSTRAINT     j   ALTER TABLE ONLY public.t_shelf_tmp_attach
    ADD CONSTRAINT t_shelf_tmp_attach_pkey PRIMARY KEY (uuid);
 T   ALTER TABLE ONLY public.t_shelf_tmp_attach DROP CONSTRAINT t_shelf_tmp_attach_pkey;
       public         postgres    false    199            �
           2606    82445 *   t_shelf_tmp_detail t_shelf_tmp_detail_pkey 
   CONSTRAINT     j   ALTER TABLE ONLY public.t_shelf_tmp_detail
    ADD CONSTRAINT t_shelf_tmp_detail_pkey PRIMARY KEY (uuid);
 T   ALTER TABLE ONLY public.t_shelf_tmp_detail DROP CONSTRAINT t_shelf_tmp_detail_pkey;
       public         postgres    false    200            �
           2606    82427    t_shelf_tmp t_shelf_tmp_pkey 
   CONSTRAINT     \   ALTER TABLE ONLY public.t_shelf_tmp
    ADD CONSTRAINT t_shelf_tmp_pkey PRIMARY KEY (uuid);
 F   ALTER TABLE ONLY public.t_shelf_tmp DROP CONSTRAINT t_shelf_tmp_pkey;
       public         postgres    false    198            �
           2606    82454 $   t_shelf_tmp_vcs t_shelf_tmp_vcs_pkey 
   CONSTRAINT     d   ALTER TABLE ONLY public.t_shelf_tmp_vcs
    ADD CONSTRAINT t_shelf_tmp_vcs_pkey PRIMARY KEY (uuid);
 N   ALTER TABLE ONLY public.t_shelf_tmp_vcs DROP CONSTRAINT t_shelf_tmp_vcs_pkey;
       public         postgres    false    201            �
           2606    82463    t_sys_lookup t_sys_lookup_pkey 
   CONSTRAINT     ^   ALTER TABLE ONLY public.t_sys_lookup
    ADD CONSTRAINT t_sys_lookup_pkey PRIMARY KEY (uuid);
 H   ALTER TABLE ONLY public.t_sys_lookup DROP CONSTRAINT t_sys_lookup_pkey;
       public         postgres    false    202            �
           2606    82465     t_shelf_tmp_attach attr_tmp_uuid    FK CONSTRAINT     �   ALTER TABLE ONLY public.t_shelf_tmp_attach
    ADD CONSTRAINT attr_tmp_uuid FOREIGN KEY (tmp_uuid) REFERENCES public.t_shelf_tmp(uuid);
 J   ALTER TABLE ONLY public.t_shelf_tmp_attach DROP CONSTRAINT attr_tmp_uuid;
       public       postgres    false    199    2706    198            �
           2606    82480 )   t_shelf_tmp_detail detail_sys_lookup_uuid    FK CONSTRAINT     �   ALTER TABLE ONLY public.t_shelf_tmp_detail
    ADD CONSTRAINT detail_sys_lookup_uuid FOREIGN KEY (lookup_uuid) REFERENCES public.t_sys_lookup(uuid);
 S   ALTER TABLE ONLY public.t_shelf_tmp_detail DROP CONSTRAINT detail_sys_lookup_uuid;
       public       postgres    false    2714    202    200            �
           2606    82470 '   t_shelf_tmp_detail detail_tmp_comp_uuid    FK CONSTRAINT     �   ALTER TABLE ONLY public.t_shelf_tmp_detail
    ADD CONSTRAINT detail_tmp_comp_uuid FOREIGN KEY (comp_uuid) REFERENCES public.t_shelf_comp(uuid);
 Q   ALTER TABLE ONLY public.t_shelf_tmp_detail DROP CONSTRAINT detail_tmp_comp_uuid;
       public       postgres    false    2704    200    197            �
           2606    82475 &   t_shelf_tmp_detail detail_tmp_vcs_uuid    FK CONSTRAINT     �   ALTER TABLE ONLY public.t_shelf_tmp_detail
    ADD CONSTRAINT detail_tmp_vcs_uuid FOREIGN KEY (vcs_uuid) REFERENCES public.t_shelf_tmp_vcs(uuid);
 P   ALTER TABLE ONLY public.t_shelf_tmp_detail DROP CONSTRAINT detail_tmp_vcs_uuid;
       public       postgres    false    200    2712    201            �
           2606    82485 !   t_shelf_tmp_vcs vsc_tmp_comp_uuid    FK CONSTRAINT     �   ALTER TABLE ONLY public.t_shelf_tmp_vcs
    ADD CONSTRAINT vsc_tmp_comp_uuid FOREIGN KEY (tmp_uuid) REFERENCES public.t_shelf_tmp(uuid);
 K   ALTER TABLE ONLY public.t_shelf_tmp_vcs DROP CONSTRAINT vsc_tmp_comp_uuid;
       public       postgres    false    201    2706    198               �  x������0��>�6e�I#�L��6��q{�,�Jߟ*Z/Z\�O��~�9V����f���+$�J&*Xm%b��V�y�.���6��~����A���tz�{������dB�%#��DN���%0�RsBfj�h�Q�e|�/��Z�P��6bl�^HB	(��M���F�z����q�n���ݞ�qg�o���j%A	I�\!j�c�>���*n	X-�|�>u���t^��?�����Č	C+����J�2�K�6ߪ�<���|�q�ͽ��]Vg�u�K&���~��*�\��j��5{k{��f���vu�j��K*St+y����~[��8w��i�/���m��m�,b�k�^8�� ��텰JiG��Q�2��_������w
ׂlRxC�D(� ����,���,6��R�_��(b�m��6^|���b�p� �����m�9�Qa��/�8^v��x��?���&            x������ � �            x������ � �            x������ � �            x������ � �            x������ � �     