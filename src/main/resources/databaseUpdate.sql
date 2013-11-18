CREATE TABLE m_sys_database_init
	(ID INT NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT m_sys_database_init_PK PRIMARY KEY, 
	current_Version INT DEFAULT 0,/*数据库当前版本*/
	last_update_time TIMESTAMP/*上次更新时间*/
	)
	;
CREATE TABLE m_sys_query
	(ID INT NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT m_sys_query_PK PRIMARY KEY, 
	name VARCHAR(200),/*名称*/
	description VARCHAR(1000),/*描述*/
	after_Parameter_Html VARCHAR(8000),/*在显示完查询参数后要显示的html*/
	show_Execute_Button SMALLINT,/*是否显示默认的执行查询的按钮*/
	system_init_create SMALLINT,/*是否由系统创建,1是,0不是*/
	system_default SMALLINT,/*是否作为系统默认查询，当用户没有指定要使用哪个查询时，根据本字段得到默认查询,1是,0不是*/
	finished SMALLINT,/*是否已经编辑完成*/
	java_code CLOB,/*java代码*/
	last_update_time TIMESTAMP/*上次更新时间*/
	)
	;
CREATE TABLE m_sys_query_parameter
	(ID INT NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT m_sys_query_parameter_PK PRIMARY KEY,
	query_id int,/*查询的ID，对应了m_sys_query.id*/
	title VARCHAR(200),/*名称*/
	description VARCHAR(1000),/*描述*/
	html_Type VARCHAR(200),/*显示参数的html输入框的方式，如：单选文本框、多行文本框*/
	custom_Name  VARCHAR(200),/*自定义的查询参数名称,就是在http请求时的parameter名字*/
	style  VARCHAR(2000),/*css 样式*/
	style_class VARCHAR(2000),/*css class*/
	show SMALLINT DEFAULT 1,/*是否显示,1显示，0不显示*/
	last_update_time TIMESTAMP/*上次更新时间*/
	)
	;
CREATE TABLE m_sys_role
	(ID INT NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT m_sys_role_PK PRIMARY KEY,
	role_Code VARCHAR(200),/*角色的代码*/
	role_Title VARCHAR(200)/*角色的显示名称*/
	)
	;
CREATE TABLE m_sys_user
	(ID INT NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT m_sys_user_PK PRIMARY KEY,
	login_Name VARCHAR(200),/*角色的代码*/
	password VARCHAR(200),/*角色的显示名称*/
	nick VARCHAR(200),/*昵称*/
	createDate TIMESTAMP/*创建时间*/
	)
	;
CREATE TABLE m_sys_user_role
	(ID INT NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT m_sys_user_role_PK PRIMARY KEY,
	user_id int,/*用户ID*/
	role_id int/*角色Id*/
	)
	;	
insert into  m_sys_user (LOGIN_NAME,PASSWORD,NICK,CREATEDATE) values ('admin','123456','系统管理员',CURRENT_TIMESTAMP);
insert into  m_sys_user (LOGIN_NAME,PASSWORD,NICK,CREATEDATE) values ('user1','123456','普通用户1',CURRENT_TIMESTAMP);
insert into  m_sys_role (ROLE_CODE,ROLE_TITLE) values ('role_admin','系统管理员角色');
insert into  m_sys_role (ROLE_CODE,ROLE_TITLE) values ('role_user','普通用户');
insert into  m_sys_user_role (USER_ID,ROLE_ID) values (1,1);
insert into  m_sys_user_role (USER_ID,ROLE_ID) values (1,2);
insert into  m_sys_user_role (USER_ID,ROLE_ID) values (2,2);

