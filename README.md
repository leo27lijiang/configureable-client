# configureable-client

configureable-client是Databus( https://github.com/linkedin/databus )统一消费端的简易封装，提供配置文件的方式组织消费逻辑。

# 用法
配置 client.properties 设置relay的相关配置。

配置 db.propeties 数据源，目前客户端只支持单一数据库的导入，只配置一个数据源。

在 configure.xml 文件中定义消费的字段即可，区分Oracle和Mysql两种逻辑(Oracle使用Merge into导入，Mysql使用Replace into导入)。

```
<source name="com.lefu.boss.goods" id="40" table="goods" db="oracle">
	<fields>
		<field name="id" type="long" primaryKey="true"/>
		<field name="name"/>
		<field name="price" type="double"/>
	</fields>
</source>
```

id:对应到relay配置文件中的数据源ID

table:目标表名称

db:oracle/mysql

field中必须指定一个主键，并且唯一

field中的type是java中的对象，基础类型/class，默认是java.lang.String

field中alias是对源表中导出的字段名称与目标表不一致时的映射，alias的值代表源表导出的字段名称

客户端只会执行与field定义相关的字段多余的字段会被忽略
