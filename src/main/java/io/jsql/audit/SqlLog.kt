package io.jsql.audit

import com.fasterxml.jackson.databind.ObjectMapper
import java.util.*


/**
\* Created with IntelliJ IDEA.
\* User: jiang
\* Date: 2017/6/15 0015
\* Time: 14:42
\*/
data class SqlLog(val sql:String,val user:String,val host:String,val time: Date=Date())


val jsonmapper=ObjectMapper()
fun SqlLog.sentoELServer() {
  elasticUtil.put(this)
}
fun main(args: Array<String>) {
    for (i in 1..20) {
        val login = LoginLog("user $i", "local", true)
        login.sendesServer()
    }
    for (i in 1..20) {
        val login = SqlLog("sql","user $i", "local")
        login.sentoELServer()
    }
}