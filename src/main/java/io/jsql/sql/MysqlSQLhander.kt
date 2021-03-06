/*
 * Java-based distributed database like Mysql
 */


package io.jsql.sql

import com.alibaba.druid.sql.ast.SQLStatement
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser
import com.google.common.collect.Lists
import io.jsql.audit.SqlLog
import io.jsql.audit.sentoELServer
import io.jsql.config.ErrorCode
import io.jsql.hazelcast.MyHazelcast
import io.jsql.hazelcast.SqlUpdateLog
import io.jsql.my_config.MyConfig
import io.jsql.mysql.handler.SQLHander
import io.jsql.sql.handler.AllHanders
import io.jsql.sql.handler.data_define.*
import io.jsql.sql.handler.data_mannipulation.Mdo
import io.jsql.sql.handler.data_mannipulation.Mhandler
import io.jsql.sql.handler.data_mannipulation.Msubquery
import io.jsql.sql.parser.MSQLvisitor
import io.jsql.util.tosql
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

/**
 * Created by 长宏 on 2017/4/30 0030.
 * 处理sql语句的入口
 */
@Component
class MysqlSQLhander : SQLHander {
    @Autowired
    lateinit private var allHanders: AllHanders//所有的sql处理器容器

    @PostConstruct
    internal fun init() {
    }

    @Autowired
    lateinit internal var myHazelcast: MyHazelcast
    @Autowired
    lateinit var config: MyConfig

    override fun handle(sql: String, c: OConnection) {//处理正常的sql语句，前端连接
        if (config.audit) {
            SqlLog(sql, c.user ?: "null", c.host).sentoELServer()
        }
        logger.info(sql)
        if (logger.isDebugEnabled) {
            logger.debug(sql)
        }
        val sqlStatement: SQLStatement
        try {
            sqlStatement = sql.tosql()
            val hander = allHanders.handerMap[sqlStatement.javaClass]
            if (hander != null) {
                hander.handle(sqlStatement, c)
            } else {
                sqlStatement.accept(MSQLvisitor(c))
            }
            if (isupdatesql(sql)) {
                if (config.distributed) {
                    myHazelcast.exeSql(sql, if (c.schema == null) "" else c.schema!!)
                } else {
                    myHazelcast.exesqlLocal(sql, if (c.schema == null) "" else c.schema!!)
                }
            }
            return
        } catch (e: Exception) {//如果不是合法的mysql语句，就报错
            //            e.printStackTrace();
            //druid支持的语句就用上面的方法语句处理，如果不支持，就会有异常，就自己写代码解析sql语句，处理。
            //下面是drop event语句的例子，这个例子druid不支持，所以自己写
            handleotherStatement(sql, c, e)
        }


    }

    fun handle(sql: SqlUpdateLog, c: OConnection) {//处理来自其他服务器的sql语句，同步，不需要前端连接
        logger.info(sql.toString())
        if (logger.isDebugEnabled) {
            logger.debug(sql.toString())
        }
        val sqlStatement: SQLStatement
        try {
            val parser = MySqlStatementParser(sql.sql)
            sqlStatement = parser.parseStatement()
            val hander = allHanders.handerMap[sqlStatement.javaClass]
            if (hander != null) {
                hander.handle(sqlStatement, c)
            } else {
                sqlStatement.accept(MSQLvisitor(c))
            }
            return
        } catch (e: Exception) {//如果不是合法的mysql语句，就报错
            //druid支持的语句就用上面的方法语句处理，如果不支持，就会有异常，就自己写代码解析sql语句，处理。
            //下面是drop event语句的例子，这个例子druid不支持，所以自己写
            handleotherStatement(sql.sql, c, e)
        }


    }

    private fun isupdatesql(sql: String): Boolean {
        val sqll = sql.toLowerCase()
        val list = Lists.newArrayList("delete", "drop", "create", "insert", "update")
        for (s in list) {
            if (sqll.contains(s)) {
                return true
            }
        }
        return false
    }


    private fun handleotherStatement(sql: String, c: OConnection, exception: Exception) {
        if (AlterEvent.isme(sql)) {
            AlterEvent.handle(sql, c)
            return
        }

        if (AlterFunction.isme(sql)) {
            AlterFunction.handle(sql, c)
            return
        }
        if (AlterInstall.isme(sql)) {
            AlterInstall.handle(sql, c)
            return
        }
        if (AlterLOGfileGroup.isme(sql)) {
            AlterLOGfileGroup.handle(sql, c)
            return
        }
        if (AlterProcedure.isme(sql)) {
            AlterProcedure.handle(sql, c)
            return
        }
        if (AlterServer.isme(sql)) {
            AlterServer.handle(sql, c)
            return
        }
        if (AlterTableSpace.isme(sql)) {
            AlterTableSpace.handle(sql, c)
            return
        }
//        if(BeginEndStatement.isme(sql)){
//            BeginEndStatement.handle0(sql, c)
//            return
//        }
        if (CreateEvent.isme(sql)) {
            CreateEvent.handle(sql, c)
            return
        }
        if (CreateServer.isme(sql)) {
            CreateServer.handle(sql, c)
            return
        }
        if (CreateFunction.isme(sql)) {
            CreateFunction.handle(sql, c)
            return
        }
        if (CreateLogfilegroup.isme(sql)) {
            CreateLogfilegroup.handle(sql, c)
            return
        }
        if (CreateServer.isme(sql)) {
            CreateServer.handle(sql, c)
            return
        }
        if (CreateTableSpace.isme(sql)) {
            CreateTableSpace.handle(sql, c)
            return
        }
        if (DropEVENT.isdropevent(sql)) {//判断是不是dropevent语句
            DropEVENT.handle(sql, c)
            return
        }
        if (DropFunction.isme(sql)) {
            DropFunction.handle(sql, c)
            return
        }
        if (DropLOGFILEGROUP.isme(sql)) {
            DropLOGFILEGROUP.handle(sql, c)
            return
        }
        if (DropServer.isme(sql)) {
            DropServer.handle(sql, c)
            return
        }
        if (Mdo.isme(sql)) {
            Mdo.handle(sql, c)
            return
        }
        if (Mhandler.isme(sql)) {
            Mhandler.handle(sql, c)
            return
        }
        if (Msubquery.isme(sql)) {
            Msubquery.handle(sql, c)
            return
        }
        c.writeErrMessage(ErrorCode.ER_SP_BAD_SQLSTATE, exception.message ?: "error")
//        c.writeok()
    }

    companion object {
        private val logger = LoggerFactory
                .getLogger(MysqlSQLhander::class.java)
    }

}
