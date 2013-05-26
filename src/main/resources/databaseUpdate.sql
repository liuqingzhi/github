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
	last_update_time TIMESTAMP/*上次更新时间*/
	)
	;