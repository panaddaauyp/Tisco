***
- insert into t_shelf_lookup(uuid,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,status,create_by) values ('0d091b62-6e58-11ea-bc55-0242ac130003','PROCE044','Customer Info','Customer Info','Customer Info','PROCESS_ERROR','PROCESS_ERROR',213,'develop');
- aW5zZXJ0IGludG8gdF9zaGVsZl9sb29rdXAodXVpZCxsb29rdXBfY29kZSxsb29rdXBfbmFtZV90aCxsb29rdXBfbmFtZV9lbixkZXNjcmlwdGlvbixncm91cF90eXBlLGxvb2t1cF90eXBlLHN0YXR1cyxjcmVhdGVfYnkpIHZhbHVlcyAoJzBkMDkxYjYyLTZlNTgtMTFlYS1iYzU1LTAyNDJhYzEzMDAwMycsJ1BST0NFMDQ0JywnQ3VzdG9tZXIgSW5mbycsJ0N1c3RvbWVyIEluZm8nLCdDdXN0b21lciBJbmZvJywnUFJPQ0VTU19FUlJPUicsJ1BST0NFU1NfRVJST1InLDIxMywnZGV2ZWxvcCcpOw==
***

1. insert ข้อมูลสำหรับ รายการเครื่องหมาย คำนวณ DueDate
insert into t_shelf_lookup (uuid,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,attr1,attr2,status,create_by) values('680ea386-6cde-11ea-bc55-0242ac130003','plus','+','+','+','FORMULAR_MARK','LOOKUP_LIST','N','1',213,'develop');
insert into t_shelf_lookup (uuid,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,attr1,attr2,status,create_by) values('680ea5e8-6cde-11ea-bc55-0242ac130003','minus','-','-','-','FORMULAR_MARK','LOOKUP_LIST','N','2',213,'develop');
insert into t_shelf_lookup (uuid,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,attr1,attr2,status,create_by) values('680ea6e2-6cde-11ea-bc55-0242ac130003','multiple','x','x','x','FORMULAR_MARK','LOOKUP_LIST','N','3',213,'develop');
insert into t_shelf_lookup (uuid,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,attr1,attr2,status,create_by) values('680ea7be-6cde-11ea-bc55-0242ac130003','division','÷','÷','÷','FORMULAR_MARK','LOOKUP_LIST','N','4',213,'develop');
insert into t_shelf_lookup (uuid,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,attr1,attr2,status,create_by) values('680ea994-6cde-11ea-bc55-0242ac130003','greaterThanEqual','>=','>=','>=','FORMULAR_MARK','LOOKUP_LIST','N','5',213,'develop');
insert into t_shelf_lookup (uuid,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,attr1,attr2,status,create_by) values('680eaa66-6cde-11ea-bc55-0242ac130003','lessThanEqual','<=','<=','<=','FORMULAR_MARK','LOOKUP_LIST','N','6',213,'develop');
insert into t_shelf_lookup (uuid,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,attr1,attr2,status,create_by) values('680eab38-6cde-11ea-bc55-0242ac130003','leftParenthesis','(','(','(','FORMULAR_MARK','LOOKUP_LIST','N','7',213,'develop');
insert into t_shelf_lookup (uuid,lookup_code,lookup_name_th,lookup_name_en,description,group_type,lookup_type,attr1,attr2,status,create_by) values('680eac00-6cde-11ea-bc55-0242ac130003','rightParenthesis',')',')',')','FORMULAR_MARK','LOOKUP_LIST','N','8',213,'develop');

-- Base 64
aW5zZXJ0IGludG8gdF9zaGVsZl9sb29rdXAgKHV1aWQsbG9va3VwX2NvZGUsbG9va3VwX25hbWVfdGgsbG9va3VwX25hbWVfZW4sZGVzY3JpcHRpb24sZ3JvdXBfdHlwZSxsb29rdXBfdHlwZSxhdHRyMSxhdHRyMixzdGF0dXMsY3JlYXRlX2J5KSB2YWx1ZXMoJzY4MGVhMzg2LTZjZGUtMTFlYS1iYzU1LTAyNDJhYzEzMDAwMycsJ3BsdXMnLCcrJywnKycsJysnLCdGT1JNVUxBUl9NQVJLJywnTE9PS1VQX0xJU1QnLCdOJywnMScsMjEzLCdkZXZlbG9wJyk7Cmluc2VydCBpbnRvIHRfc2hlbGZfbG9va3VwICh1dWlkLGxvb2t1cF9jb2RlLGxvb2t1cF9uYW1lX3RoLGxvb2t1cF9uYW1lX2VuLGRlc2NyaXB0aW9uLGdyb3VwX3R5cGUsbG9va3VwX3R5cGUsYXR0cjEsYXR0cjIsc3RhdHVzLGNyZWF0ZV9ieSkgdmFsdWVzKCc2ODBlYTVlOC02Y2RlLTExZWEtYmM1NS0wMjQyYWMxMzAwMDMnLCdtaW51cycsJy0nLCctJywnLScsJ0ZPUk1VTEFSX01BUksnLCdMT09LVVBfTElTVCcsJ04nLCcyJywyMTMsJ2RldmVsb3AnKTsKaW5zZXJ0IGludG8gdF9zaGVsZl9sb29rdXAgKHV1aWQsbG9va3VwX2NvZGUsbG9va3VwX25hbWVfdGgsbG9va3VwX25hbWVfZW4sZGVzY3JpcHRpb24sZ3JvdXBfdHlwZSxsb29rdXBfdHlwZSxhdHRyMSxhdHRyMixzdGF0dXMsY3JlYXRlX2J5KSB2YWx1ZXMoJzY4MGVhNmUyLTZjZGUtMTFlYS1iYzU1LTAyNDJhYzEzMDAwMycsJ211bHRpcGxlJywneCcsJ3gnLCd4JywnRk9STVVMQVJfTUFSSycsJ0xPT0tVUF9MSVNUJywnTicsJzMnLDIxMywnZGV2ZWxvcCcpOwppbnNlcnQgaW50byB0X3NoZWxmX2xvb2t1cCAodXVpZCxsb29rdXBfY29kZSxsb29rdXBfbmFtZV90aCxsb29rdXBfbmFtZV9lbixkZXNjcmlwdGlvbixncm91cF90eXBlLGxvb2t1cF90eXBlLGF0dHIxLGF0dHIyLHN0YXR1cyxjcmVhdGVfYnkpIHZhbHVlcygnNjgwZWE3YmUtNmNkZS0xMWVhLWJjNTUtMDI0MmFjMTMwMDAzJywnZGl2aXNpb24nLCfDtycsJ8O3Jywnw7cnLCdGT1JNVUxBUl9NQVJLJywnTE9PS1VQX0xJU1QnLCdOJywnNCcsMjEzLCdkZXZlbG9wJyk7Cmluc2VydCBpbnRvIHRfc2hlbGZfbG9va3VwICh1dWlkLGxvb2t1cF9jb2RlLGxvb2t1cF9uYW1lX3RoLGxvb2t1cF9uYW1lX2VuLGRlc2NyaXB0aW9uLGdyb3VwX3R5cGUsbG9va3VwX3R5cGUsYXR0cjEsYXR0cjIsc3RhdHVzLGNyZWF0ZV9ieSkgdmFsdWVzKCc2ODBlYTk5NC02Y2RlLTExZWEtYmM1NS0wMjQyYWMxMzAwMDMnLCdncmVhdGVyVGhhbkVxdWFsJywnPj0nLCc+PScsJz49JywnRk9STVVMQVJfTUFSSycsJ0xPT0tVUF9MSVNUJywnTicsJzUnLDIxMywnZGV2ZWxvcCcpOwppbnNlcnQgaW50byB0X3NoZWxmX2xvb2t1cCAodXVpZCxsb29rdXBfY29kZSxsb29rdXBfbmFtZV90aCxsb29rdXBfbmFtZV9lbixkZXNjcmlwdGlvbixncm91cF90eXBlLGxvb2t1cF90eXBlLGF0dHIxLGF0dHIyLHN0YXR1cyxjcmVhdGVfYnkpIHZhbHVlcygnNjgwZWFhNjYtNmNkZS0xMWVhLWJjNTUtMDI0MmFjMTMwMDAzJywnbGVzc1RoYW5FcXVhbCcsJzw9JywnPD0nLCc8PScsJ0ZPUk1VTEFSX01BUksnLCdMT09LVVBfTElTVCcsJ04nLCc2JywyMTMsJ2RldmVsb3AnKTsKaW5zZXJ0IGludG8gdF9zaGVsZl9sb29rdXAgKHV1aWQsbG9va3VwX2NvZGUsbG9va3VwX25hbWVfdGgsbG9va3VwX25hbWVfZW4sZGVzY3JpcHRpb24sZ3JvdXBfdHlwZSxsb29rdXBfdHlwZSxhdHRyMSxhdHRyMixzdGF0dXMsY3JlYXRlX2J5KSB2YWx1ZXMoJzY4MGVhYjM4LTZjZGUtMTFlYS1iYzU1LTAyNDJhYzEzMDAwMycsJ2xlZnRQYXJlbnRoZXNpcycsJygnLCcoJywnKCcsJ0ZPUk1VTEFSX01BUksnLCdMT09LVVBfTElTVCcsJ04nLCc3JywyMTMsJ2RldmVsb3AnKTsKaW5zZXJ0IGludG8gdF9zaGVsZl9sb29rdXAgKHV1aWQsbG9va3VwX2NvZGUsbG9va3VwX25hbWVfdGgsbG9va3VwX25hbWVfZW4sZGVzY3JpcHRpb24sZ3JvdXBfdHlwZSxsb29rdXBfdHlwZSxhdHRyMSxhdHRyMixzdGF0dXMsY3JlYXRlX2J5KSB2YWx1ZXMoJzY4MGVhYzAwLTZjZGUtMTFlYS1iYzU1LTAyNDJhYzEzMDAwMycsJ3JpZ2h0UGFyZW50aGVzaXMnLCcpJywnKScsJyknLCdGT1JNVUxBUl9NQVJLJywnTE9PS1VQX0xJU1QnLCdOJywnOCcsMjEzLCdkZXZlbG9wJyk7

2. insert ข้อมูล input type
INSERT INTO public.t_shelf_lookup(uuid, lookup_code, lookup_name_th, lookup_name_en, description, group_type, lookup_type, attr1, status, create_by)	
VALUES ('51db6c60-6ce4-11ea-bc55-0242ac130003', 'IN020', 'Button Mark', 'Button Mark', 'Button Mark', 'INPUT', 'ELEMENT', 'Y', 213,'develop');

--Base64
SU5TRVJUIElOVE8gcHVibGljLnRfc2hlbGZfbG9va3VwKHV1aWQsIGxvb2t1cF9jb2RlLCBsb29rdXBfbmFtZV90aCwgbG9va3VwX25hbWVfZW4sIGRlc2NyaXB0aW9uLCBncm91cF90eXBlLCBsb29rdXBfdHlwZSwgYXR0cjEsIHN0YXR1cywgY3JlYXRlX2J5KQkKVkFMVUVTICgnNTFkYjZjNjAtNmNlNC0xMWVhLWJjNTUtMDI0MmFjMTMwMDAzJywgJ0lOMDIwJywgJ0J1dHRvbiBNYXJrJywgJ0J1dHRvbiBNYXJrJywgJ0J1dHRvbiBNYXJrJywgJ0lOUFVUJywgJ0VMRU1FTlQnLCAnWScsIDIxMywnZGV2ZWxvcCcpOw==

3. insert element in product-info component
update t_shelf_comp_dtl set seq=	33	,parent=	1	where uuid = 'fd59461c-3db8-11ea-b77f-2e728ce88125';
update t_shelf_comp_dtl set seq=	34	,parent=	33	where uuid = 'fd594748-3db8-11ea-b77f-2e728ce88125';
update t_shelf_comp_dtl set seq=	35	,parent=	1	where uuid = 'fd594874-3db8-11ea-b77f-2e728ce88125';
update t_shelf_comp_dtl set seq=	36	,parent=	35	where uuid = 'fd5949a0-3db8-11ea-b77f-2e728ce88125';
update t_shelf_comp_dtl set seq=	37	,parent=	35	where uuid = 'fd594ac2-3db8-11ea-b77f-2e728ce88125';
update t_shelf_comp_dtl set seq=	38	,parent=	1	where uuid = 'fd594be4-3db8-11ea-b77f-2e728ce88125';
update t_shelf_comp_dtl set seq=	39	,parent=	38	where uuid = 'fd594f36-3db8-11ea-b77f-2e728ce88125';
update t_shelf_comp_dtl set seq=	40	,parent=	38	where uuid = 'fd595076-3db8-11ea-b77f-2e728ce88125';
update t_shelf_comp_dtl set seq=	41	,parent=	38	where uuid = 'fd5951ac-3db8-11ea-b77f-2e728ce88125';
update t_shelf_comp_dtl set seq=	42	,parent=	38	where uuid = 'fd5952d8-3db8-11ea-b77f-2e728ce88125';
update t_shelf_comp_dtl set seq=	43	,parent=	38	where uuid = 'fd5953fa-3db8-11ea-b77f-2e728ce88125';
update t_shelf_comp_dtl set seq=	44	,parent=	43	where uuid = 'fd59571a-3db8-11ea-b77f-2e728ce88125';
update t_shelf_comp_dtl set seq=	45	,parent=	43	where uuid = 'fd59588c-3db8-11ea-b77f-2e728ce88125';
update t_shelf_comp_dtl set seq=	46	,parent=	43	where uuid = 'fd5959cc-3db8-11ea-b77f-2e728ce88125';
update t_shelf_comp_dtl set seq=	47	,parent=	38	where uuid = 'fd595b02-3db8-11ea-b77f-2e728ce88125';
update t_shelf_comp_dtl set seq=	48	,parent=	38	where uuid = 'fd595c2e-3db8-11ea-b77f-2e728ce88125';
update t_shelf_comp_dtl set seq=	49	,parent=	48	where uuid = 'b36431a0-57e1-11ea-82b4-0242ac130003';
update t_shelf_comp_dtl set seq=	50	,parent=	1	where uuid = 'fd595d64-3db8-11ea-b77f-2e728ce88125';
update t_shelf_comp_dtl set seq=	51	,parent=	50	where uuid = 'fd596020-3db8-11ea-b77f-2e728ce88125';
update t_shelf_comp_dtl set seq=	52	where uuid = 'fd59616a-3db8-11ea-b77f-2e728ce88125';
update t_shelf_comp_dtl set seq=	53	,parent=	52	where uuid = 'fd596296-3db8-11ea-b77f-2e728ce88125';
update t_shelf_comp_dtl set seq=	54	,parent=	53	where uuid = 'fd5963b8-3db8-11ea-b77f-2e728ce88125';
update t_shelf_comp_dtl set seq=	55	,parent=	53	where uuid = 'fd5964da-3db8-11ea-b77f-2e728ce88125';
update t_shelf_comp_dtl set seq=	56	,parent=	52	where uuid = 'fd5965fc-3db8-11ea-b77f-2e728ce88125';
update t_shelf_comp_dtl set seq=	57	,parent=	56	where uuid = 'fd596980-3db8-11ea-b77f-2e728ce88125';
update t_shelf_comp_dtl set seq=	58	,parent=	56	where uuid = 'fd596ade-3db8-11ea-b77f-2e728ce88125';
update t_shelf_comp_dtl set seq=	59	,parent=	56	where uuid = 'fd596c0a-3db8-11ea-b77f-2e728ce88125';
update t_shelf_comp_dtl set seq=	60	,parent=	52	where uuid = 'fd596d2c-3db8-11ea-b77f-2e728ce88125';
update t_shelf_comp_dtl set seq=	61	,parent=	60	where uuid = 'fd596e4e-3db8-11ea-b77f-2e728ce88125';
update t_shelf_comp_dtl set seq=	62	,parent=	60	where uuid = 'fd596f70-3db8-11ea-b77f-2e728ce88125';
update t_shelf_comp_dtl set seq=	63	,parent=	52	where uuid = 'fd59709c-3db8-11ea-b77f-2e728ce88125';
update t_shelf_comp_dtl set seq=	64	,parent=	63	where uuid = 'fd59738a-3db8-11ea-b77f-2e728ce88125';
update t_shelf_comp_dtl set seq=	65	,parent=	63	where uuid = 'fd59757e-3db8-11ea-b77f-2e728ce88125';
update t_shelf_comp_dtl set seq=	66	,parent=	63	where uuid = 'fd5976f0-3db8-11ea-b77f-2e728ce88125';

--Base64
dXBkYXRlIHRfc2hlbGZfY29tcF9kdGwgc2V0IHNlcT0JMzMJLHBhcmVudD0JMQl3aGVyZSB1dWlkID0gJ2ZkNTk0NjFjLTNkYjgtMTFlYS1iNzdmLTJlNzI4Y2U4ODEyNSc7CnVwZGF0ZSB0X3NoZWxmX2NvbXBfZHRsIHNldCBzZXE9CTM0CSxwYXJlbnQ9CTMzCXdoZXJlIHV1aWQgPSAnZmQ1OTQ3NDgtM2RiOC0xMWVhLWI3N2YtMmU3MjhjZTg4MTI1JzsKdXBkYXRlIHRfc2hlbGZfY29tcF9kdGwgc2V0IHNlcT0JMzUJLHBhcmVudD0JMQl3aGVyZSB1dWlkID0gJ2ZkNTk0ODc0LTNkYjgtMTFlYS1iNzdmLTJlNzI4Y2U4ODEyNSc7CnVwZGF0ZSB0X3NoZWxmX2NvbXBfZHRsIHNldCBzZXE9CTM2CSxwYXJlbnQ9CTM1CXdoZXJlIHV1aWQgPSAnZmQ1OTQ5YTAtM2RiOC0xMWVhLWI3N2YtMmU3MjhjZTg4MTI1JzsKdXBkYXRlIHRfc2hlbGZfY29tcF9kdGwgc2V0IHNlcT0JMzcJLHBhcmVudD0JMzUJd2hlcmUgdXVpZCA9ICdmZDU5NGFjMi0zZGI4LTExZWEtYjc3Zi0yZTcyOGNlODgxMjUnOwp1cGRhdGUgdF9zaGVsZl9jb21wX2R0bCBzZXQgc2VxPQkzOAkscGFyZW50PQkxCXdoZXJlIHV1aWQgPSAnZmQ1OTRiZTQtM2RiOC0xMWVhLWI3N2YtMmU3MjhjZTg4MTI1JzsKdXBkYXRlIHRfc2hlbGZfY29tcF9kdGwgc2V0IHNlcT0JMzkJLHBhcmVudD0JMzgJd2hlcmUgdXVpZCA9ICdmZDU5NGYzNi0zZGI4LTExZWEtYjc3Zi0yZTcyOGNlODgxMjUnOwp1cGRhdGUgdF9zaGVsZl9jb21wX2R0bCBzZXQgc2VxPQk0MAkscGFyZW50PQkzOAl3aGVyZSB1dWlkID0gJ2ZkNTk1MDc2LTNkYjgtMTFlYS1iNzdmLTJlNzI4Y2U4ODEyNSc7CnVwZGF0ZSB0X3NoZWxmX2NvbXBfZHRsIHNldCBzZXE9CTQxCSxwYXJlbnQ9CTM4CXdoZXJlIHV1aWQgPSAnZmQ1OTUxYWMtM2RiOC0xMWVhLWI3N2YtMmU3MjhjZTg4MTI1JzsKdXBkYXRlIHRfc2hlbGZfY29tcF9kdGwgc2V0IHNlcT0JNDIJLHBhcmVudD0JMzgJd2hlcmUgdXVpZCA9ICdmZDU5NTJkOC0zZGI4LTExZWEtYjc3Zi0yZTcyOGNlODgxMjUnOwp1cGRhdGUgdF9zaGVsZl9jb21wX2R0bCBzZXQgc2VxPQk0MwkscGFyZW50PQkzOAl3aGVyZSB1dWlkID0gJ2ZkNTk1M2ZhLTNkYjgtMTFlYS1iNzdmLTJlNzI4Y2U4ODEyNSc7CnVwZGF0ZSB0X3NoZWxmX2NvbXBfZHRsIHNldCBzZXE9CTQ0CSxwYXJlbnQ9CTQzCXdoZXJlIHV1aWQgPSAnZmQ1OTU3MWEtM2RiOC0xMWVhLWI3N2YtMmU3MjhjZTg4MTI1JzsKdXBkYXRlIHRfc2hlbGZfY29tcF9kdGwgc2V0IHNlcT0JNDUJLHBhcmVudD0JNDMJd2hlcmUgdXVpZCA9ICdmZDU5NTg4Yy0zZGI4LTExZWEtYjc3Zi0yZTcyOGNlODgxMjUnOwp1cGRhdGUgdF9zaGVsZl9jb21wX2R0bCBzZXQgc2VxPQk0NgkscGFyZW50PQk0Mwl3aGVyZSB1dWlkID0gJ2ZkNTk1OWNjLTNkYjgtMTFlYS1iNzdmLTJlNzI4Y2U4ODEyNSc7CnVwZGF0ZSB0X3NoZWxmX2NvbXBfZHRsIHNldCBzZXE9CTQ3CSxwYXJlbnQ9CTM4CXdoZXJlIHV1aWQgPSAnZmQ1OTViMDItM2RiOC0xMWVhLWI3N2YtMmU3MjhjZTg4MTI1JzsKdXBkYXRlIHRfc2hlbGZfY29tcF9kdGwgc2V0IHNlcT0JNDgJLHBhcmVudD0JMzgJd2hlcmUgdXVpZCA9ICdmZDU5NWMyZS0zZGI4LTExZWEtYjc3Zi0yZTcyOGNlODgxMjUnOwp1cGRhdGUgdF9zaGVsZl9jb21wX2R0bCBzZXQgc2VxPQk0OQkscGFyZW50PQk0OAl3aGVyZSB1dWlkID0gJ2IzNjQzMWEwLTU3ZTEtMTFlYS04MmI0LTAyNDJhYzEzMDAwMyc7CnVwZGF0ZSB0X3NoZWxmX2NvbXBfZHRsIHNldCBzZXE9CTUwCSxwYXJlbnQ9CTEJd2hlcmUgdXVpZCA9ICdmZDU5NWQ2NC0zZGI4LTExZWEtYjc3Zi0yZTcyOGNlODgxMjUnOwp1cGRhdGUgdF9zaGVsZl9jb21wX2R0bCBzZXQgc2VxPQk1MQkscGFyZW50PQk1MAl3aGVyZSB1dWlkID0gJ2ZkNTk2MDIwLTNkYjgtMTFlYS1iNzdmLTJlNzI4Y2U4ODEyNSc7CnVwZGF0ZSB0X3NoZWxmX2NvbXBfZHRsIHNldCBzZXE9CTUyCXdoZXJlIHV1aWQgPSAnZmQ1OTYxNmEtM2RiOC0xMWVhLWI3N2YtMmU3MjhjZTg4MTI1JzsKdXBkYXRlIHRfc2hlbGZfY29tcF9kdGwgc2V0IHNlcT0JNTMJLHBhcmVudD0JNTIJd2hlcmUgdXVpZCA9ICdmZDU5NjI5Ni0zZGI4LTExZWEtYjc3Zi0yZTcyOGNlODgxMjUnOwp1cGRhdGUgdF9zaGVsZl9jb21wX2R0bCBzZXQgc2VxPQk1NAkscGFyZW50PQk1Mwl3aGVyZSB1dWlkID0gJ2ZkNTk2M2I4LTNkYjgtMTFlYS1iNzdmLTJlNzI4Y2U4ODEyNSc7CnVwZGF0ZSB0X3NoZWxmX2NvbXBfZHRsIHNldCBzZXE9CTU1CSxwYXJlbnQ9CTUzCXdoZXJlIHV1aWQgPSAnZmQ1OTY0ZGEtM2RiOC0xMWVhLWI3N2YtMmU3MjhjZTg4MTI1JzsKdXBkYXRlIHRfc2hlbGZfY29tcF9kdGwgc2V0IHNlcT0JNTYJLHBhcmVudD0JNTIJd2hlcmUgdXVpZCA9ICdmZDU5NjVmYy0zZGI4LTExZWEtYjc3Zi0yZTcyOGNlODgxMjUnOwp1cGRhdGUgdF9zaGVsZl9jb21wX2R0bCBzZXQgc2VxPQk1NwkscGFyZW50PQk1Ngl3aGVyZSB1dWlkID0gJ2ZkNTk2OTgwLTNkYjgtMTFlYS1iNzdmLTJlNzI4Y2U4ODEyNSc7CnVwZGF0ZSB0X3NoZWxmX2NvbXBfZHRsIHNldCBzZXE9CTU4CSxwYXJlbnQ9CTU2CXdoZXJlIHV1aWQgPSAnZmQ1OTZhZGUtM2RiOC0xMWVhLWI3N2YtMmU3MjhjZTg4MTI1JzsKdXBkYXRlIHRfc2hlbGZfY29tcF9kdGwgc2V0IHNlcT0JNTkJLHBhcmVudD0JNTYJd2hlcmUgdXVpZCA9ICdmZDU5NmMwYS0zZGI4LTExZWEtYjc3Zi0yZTcyOGNlODgxMjUnOwp1cGRhdGUgdF9zaGVsZl9jb21wX2R0bCBzZXQgc2VxPQk2MAkscGFyZW50PQk1Mgl3aGVyZSB1dWlkID0gJ2ZkNTk2ZDJjLTNkYjgtMTFlYS1iNzdmLTJlNzI4Y2U4ODEyNSc7CnVwZGF0ZSB0X3NoZWxmX2NvbXBfZHRsIHNldCBzZXE9CTYxCSxwYXJlbnQ9CTYwCXdoZXJlIHV1aWQgPSAnZmQ1OTZlNGUtM2RiOC0xMWVhLWI3N2YtMmU3MjhjZTg4MTI1JzsKdXBkYXRlIHRfc2hlbGZfY29tcF9kdGwgc2V0IHNlcT0JNjIJLHBhcmVudD0JNjAJd2hlcmUgdXVpZCA9ICdmZDU5NmY3MC0zZGI4LTExZWEtYjc3Zi0yZTcyOGNlODgxMjUnOwp1cGRhdGUgdF9zaGVsZl9jb21wX2R0bCBzZXQgc2VxPQk2MwkscGFyZW50PQk1Mgl3aGVyZSB1dWlkID0gJ2ZkNTk3MDljLTNkYjgtMTFlYS1iNzdmLTJlNzI4Y2U4ODEyNSc7CnVwZGF0ZSB0X3NoZWxmX2NvbXBfZHRsIHNldCBzZXE9CTY0CSxwYXJlbnQ9CTYzCXdoZXJlIHV1aWQgPSAnZmQ1OTczOGEtM2RiOC0xMWVhLWI3N2YtMmU3MjhjZTg4MTI1JzsKdXBkYXRlIHRfc2hlbGZfY29tcF9kdGwgc2V0IHNlcT0JNjUJLHBhcmVudD0JNjMJd2hlcmUgdXVpZCA9ICdmZDU5NzU3ZS0zZGI4LTExZWEtYjc3Zi0yZTcyOGNlODgxMjUnOwp1cGRhdGUgdF9zaGVsZl9jb21wX2R0bCBzZXQgc2VxPQk2NgkscGFyZW50PQk2Mwl3aGVyZSB1dWlkID0gJ2ZkNTk3NmYwLTNkYjgtMTFlYS1iNzdmLTJlNzI4Y2U4ODEyNSc7

4. insert element in product-info component																																																											
insert into t_shelf_comp_dtl (uuid,comp_uuid,lk_uuid,label_text,attr2,seq,parent,status,create_by,ele_id,require,table_column) values('66c50e64-6e9d-11ea-bc55-0242ac130003','723b3819-e1e0-4756-a6f2-50fdaf14d85d','d5bf8b97-6cd9-4871-accf-2f19f4e43fbb','Calculation : คำนวณ First Due Date',null,27,1,213,'develop','calculateFirstDueDate',FALSE,'');
insert into t_shelf_comp_dtl (uuid,comp_uuid,lk_uuid,label_text,attr2,seq,parent,status,create_by,ele_id,require,table_column) values('66c510bc-6e9d-11ea-bc55-0242ac130003','723b3819-e1e0-4756-a6f2-50fdaf14d85d','180971de-efe3-46e1-b4d6-fd9ccda44f08','Calculation (คำนวณ First Due Date)',null,28,27,213,'develop','calculateFDueDate',FALSE,'');
insert into t_shelf_comp_dtl (uuid,comp_uuid,lk_uuid,label_text,attr2,seq,parent,status,create_by,ele_id,require,table_column) values('66c5130a-6e9d-11ea-bc55-0242ac130003','723b3819-e1e0-4756-a6f2-50fdaf14d85d','43bc8cb8-f7df-429b-a422-241f0ea1683f',null,null,29,27,213,'develop','calculateTable',FALSE,'[{"columnname": "Formular", "data":"formular" },{"columnname": "Description", "data":"description" }]');
insert into t_shelf_comp_dtl (uuid,comp_uuid,lk_uuid,label_text,attr2,seq,parent,status,create_by,ele_id,require,table_column) values('66c51472-6e9d-11ea-bc55-0242ac130003','723b3819-e1e0-4756-a6f2-50fdaf14d85d','aad9004f-4478-4dd7-ba15-1d29d4ad8b11',null,null,30,27,213,'develop','groupFormular',FALSE,'');
insert into t_shelf_comp_dtl (uuid,comp_uuid,lk_uuid,label_text,attr2,seq,parent,status,create_by,ele_id,require,table_column) values('66c51562-6e9d-11ea-bc55-0242ac130003','723b3819-e1e0-4756-a6f2-50fdaf14d85d','51db6c60-6ce4-11ea-bc55-0242ac130003',null,'markList',31,30,213,'develop','formularMark',FALSE,'');
insert into t_shelf_comp_dtl (uuid,comp_uuid,lk_uuid,label_text,attr2,seq,parent,status,create_by,ele_id,require,table_column) values('66c5163e-6e9d-11ea-bc55-0242ac130003','723b3819-e1e0-4756-a6f2-50fdaf14d85d','c1b3bf6a-b328-46be-a9ba-d60396025120','Formular',null,32,20,213,'develop','formularDueDate',FALSE,'');

--Base64
aW5zZXJ0IGludG8gdF9zaGVsZl9jb21wX2R0bCAodXVpZCxjb21wX3V1aWQsbGtfdXVpZCxsYWJlbF90ZXh0LGF0dHIyLHNlcSxwYXJlbnQsc3RhdHVzLGNyZWF0ZV9ieSxlbGVfaWQscmVxdWlyZSx0YWJsZV9jb2x1bW4pIHZhbHVlcygnNjZjNTBlNjQtNmU5ZC0xMWVhLWJjNTUtMDI0MmFjMTMwMDAzJywnNzIzYjM4MTktZTFlMC00NzU2LWE2ZjItNTBmZGFmMTRkODVkJywnZDViZjhiOTctNmNkOS00ODcxLWFjY2YtMmYxOWY0ZTQzZmJiJywnQ2FsY3VsYXRpb24gOiDguITguLPguJnguKfguJMgRmlyc3QgRHVlIERhdGUnLG51bGwsMjcsMSwyMTMsJ2RldmVsb3AnLCdjYWxjdWxhdGVGaXJzdER1ZURhdGUnLEZBTFNFLCcnKTsKaW5zZXJ0IGludG8gdF9zaGVsZl9jb21wX2R0bCAodXVpZCxjb21wX3V1aWQsbGtfdXVpZCxsYWJlbF90ZXh0LGF0dHIyLHNlcSxwYXJlbnQsc3RhdHVzLGNyZWF0ZV9ieSxlbGVfaWQscmVxdWlyZSx0YWJsZV9jb2x1bW4pIHZhbHVlcygnNjZjNTEwYmMtNmU5ZC0xMWVhLWJjNTUtMDI0MmFjMTMwMDAzJywnNzIzYjM4MTktZTFlMC00NzU2LWE2ZjItNTBmZGFmMTRkODVkJywnMTgwOTcxZGUtZWZlMy00NmUxLWI0ZDYtZmQ5Y2NkYTQ0ZjA4JywnQ2FsY3VsYXRpb24gKOC4hOC4s+C4meC4p+C4kyBGaXJzdCBEdWUgRGF0ZSknLG51bGwsMjgsMjcsMjEzLCdkZXZlbG9wJywnY2FsY3VsYXRlRkR1ZURhdGUnLEZBTFNFLCcnKTsKaW5zZXJ0IGludG8gdF9zaGVsZl9jb21wX2R0bCAodXVpZCxjb21wX3V1aWQsbGtfdXVpZCxsYWJlbF90ZXh0LGF0dHIyLHNlcSxwYXJlbnQsc3RhdHVzLGNyZWF0ZV9ieSxlbGVfaWQscmVxdWlyZSx0YWJsZV9jb2x1bW4pIHZhbHVlcygnNjZjNTEzMGEtNmU5ZC0xMWVhLWJjNTUtMDI0MmFjMTMwMDAzJywnNzIzYjM4MTktZTFlMC00NzU2LWE2ZjItNTBmZGFmMTRkODVkJywnNDNiYzhjYjgtZjdkZi00MjliLWE0MjItMjQxZjBlYTE2ODNmJyxudWxsLG51bGwsMjksMjcsMjEzLCdkZXZlbG9wJywnY2FsY3VsYXRlVGFibGUnLEZBTFNFLCdbeyJjb2x1bW5uYW1lIjogIkZvcm11bGFyIiwgImRhdGEiOiJmb3JtdWxhciIgfSx7ImNvbHVtbm5hbWUiOiAiRGVzY3JpcHRpb24iLCAiZGF0YSI6ImRlc2NyaXB0aW9uIiB9XScpOwppbnNlcnQgaW50byB0X3NoZWxmX2NvbXBfZHRsICh1dWlkLGNvbXBfdXVpZCxsa191dWlkLGxhYmVsX3RleHQsYXR0cjIsc2VxLHBhcmVudCxzdGF0dXMsY3JlYXRlX2J5LGVsZV9pZCxyZXF1aXJlLHRhYmxlX2NvbHVtbikgdmFsdWVzKCc2NmM1MTQ3Mi02ZTlkLTExZWEtYmM1NS0wMjQyYWMxMzAwMDMnLCc3MjNiMzgxOS1lMWUwLTQ3NTYtYTZmMi01MGZkYWYxNGQ4NWQnLCdhYWQ5MDA0Zi00NDc4LTRkZDctYmExNS0xZDI5ZDRhZDhiMTEnLG51bGwsbnVsbCwzMCwyNywyMTMsJ2RldmVsb3AnLCdncm91cEZvcm11bGFyJyxGQUxTRSwnJyk7Cmluc2VydCBpbnRvIHRfc2hlbGZfY29tcF9kdGwgKHV1aWQsY29tcF91dWlkLGxrX3V1aWQsbGFiZWxfdGV4dCxhdHRyMixzZXEscGFyZW50LHN0YXR1cyxjcmVhdGVfYnksZWxlX2lkLHJlcXVpcmUsdGFibGVfY29sdW1uKSB2YWx1ZXMoJzY2YzUxNTYyLTZlOWQtMTFlYS1iYzU1LTAyNDJhYzEzMDAwMycsJzcyM2IzODE5LWUxZTAtNDc1Ni1hNmYyLTUwZmRhZjE0ZDg1ZCcsJzUxZGI2YzYwLTZjZTQtMTFlYS1iYzU1LTAyNDJhYzEzMDAwMycsbnVsbCwnbWFya0xpc3QnLDMxLDMwLDIxMywnZGV2ZWxvcCcsJ2Zvcm11bGFyTWFyaycsRkFMU0UsJycpOwppbnNlcnQgaW50byB0X3NoZWxmX2NvbXBfZHRsICh1dWlkLGNvbXBfdXVpZCxsa191dWlkLGxhYmVsX3RleHQsYXR0cjIsc2VxLHBhcmVudCxzdGF0dXMsY3JlYXRlX2J5LGVsZV9pZCxyZXF1aXJlLHRhYmxlX2NvbHVtbikgdmFsdWVzKCc2NmM1MTYzZS02ZTlkLTExZWEtYmM1NS0wMjQyYWMxMzAwMDMnLCc3MjNiMzgxOS1lMWUwLTQ3NTYtYTZmMi01MGZkYWYxNGQ4NWQnLCdjMWIzYmY2YS1iMzI4LTQ2YmUtYTliYS1kNjAzOTYwMjUxMjAnLCdGb3JtdWxhcicsbnVsbCwzMiwyMCwyMTMsJ2RldmVsb3AnLCdmb3JtdWxhckR1ZURhdGUnLEZBTFNFLCcnKTs=










