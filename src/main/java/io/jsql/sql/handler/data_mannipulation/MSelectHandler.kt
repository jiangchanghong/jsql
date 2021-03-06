/*
 * Java-based distributed database like Mysql
 */
package io.jsql.sql.handler.data_mannipulation

import com.alibaba.druid.sql.ast.SQLStatement
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock
import com.orientechnologies.orient.core.record.OElement
import com.orientechnologies.orient.core.record.impl.ODocument
import io.jsql.orientstorage.constant.Minformation_schama
import io.jsql.orientstorage.constant.MvariableTable
import io.jsql.sql.OConnection
import io.jsql.sql.handler.MyResultSet
import io.jsql.sql.handler.SqlStatementHander
import io.jsql.sql.response.*
import io.jsql.sql.util.Mcomputer
import org.springframework.stereotype.Component

import kotlin.streams.toList

/**
 * @author changhong
 * *         select 语句有些是selct table from,,,,,，
 * *         select 6+1;
 * *         select dabase();
 * *         有些比如是select suer（）；
 */
@Component
class MSelectHandler : SqlStatementHander() {


    override fun supportSQLstatement(): Class<out SQLStatement> {
        return SQLSelectStatement::class.java
    }

    @Throws(Exception::class)
    override fun handle0(sqlStatement: SQLStatement, c: OConnection): Any? {
        val selectStatement = sqlStatement as SQLSelectStatement
        val queryBlock = selectStatement.select.query as MySqlSelectQueryBlock
        if (queryBlock.from != null) {
            return MorientSelectResponse.getdata(selectStatement, c!!)
        }
        val selectItem = queryBlock.selectList[0]
        var number = true
        try {
            java.lang.Double.parseDouble(selectItem.toString())
        } catch (e: Exception) {
            //            e.printStackTrace();
            number = false
        }

        if (number) {
            val element = ODocument()
            element.setProperty(selectItem.toString(), selectItem.toString())
            val ts = listOf<OElement>(element)
            val singleton = listOf(selectItem.toString())
            val resultSet = MyResultSet(ts, singleton)
            return resultSet
        }
        if (selectItem.expr is SQLBinaryOpExpr) {
            return handleopexpr(selectItem.expr as SQLBinaryOpExpr)
        }

        val sqlExpr = selectItem.expr
        if (selectStatement.toString().contains("@") && selectStatement.toString().contains("AS")) {
            val column = MselectVariables.getcolumn(selectStatement)
            val value = MselectVariables.getbs(selectStatement, column)
            val resultSet = MyResultSet(value!!, column)
            return resultSet
        }
        var what = sqlExpr.toString().toUpperCase()
        if (what.contains("VERSION_COMMENT")) {
            return MSelectVersionComment.getdata()
        }
        if (what.contains("DATABASE")) {
            return MSelectDatabase.getdata(c!!)
        }
        if (what.contains("CONNECTION_ID")) {
            return MSelectConnnectID.getdata()

        }
        if (what.contains("USER")) {
            return MSelectUser.getdata()
        }
        if (what.contains("VERSION")) {
            return MSelectVersion.getdata()

        }
        //SESSION_INCREMENT
        if (what.contains("INCREMENT")) {
            return MSessionIncrement.getdata()
        }
        //SESSION_ISOLATION
        if (what.contains("ISOLATION")) {
            return SessionIsolation.getdata()
        }
        if (what.contains("LAST_INSERT_ID")) {
            return MSelectLastInsertId.getdata()
        }
        if (what.contains("IDENTITY")) {
            return MSelectIdentity.getdata()
        }
        if (what.contains("SELECT_VAR_ALL")) {
            return ShowVariables.getdata()


        }
        //SESSION_TX_READ_ONLY
        if (what.contains("TX_READ_ONLY")) {
            return MSelectTxReadOnly.getdata()
        }
        what = sqlExpr.toString()
        if (what.contains("@@")) {
            val index = what.indexOf(".")
            if (index != -1) {
                what = what.substring(index + 1)
                what = "select value from " + MvariableTable.tablename + "  where Variable_name='" + what + "';"
                val documents = OConnection.DB_ADMIN!!.query(what, Minformation_schama.dbname)
                val column = listOf("value")
//                val data = documents.collect<List<OElement?>, Any>(toList<OElement>()) as List<OElement>
                return MyResultSet(documents.toList(), column)
                //                    MSelect1Response.response(c, what, Arrays.asList(documents.findAny().get().getProperty("value")));

            }
        }
        return null
    }
    //     private void handle0(SQLSelectStatement selectStatement, OConnection c) {
    //
    //        MySqlSelectQueryBlock queryBlock = (MySqlSelectQueryBlock) selectStatement.getSelect().getQuery();
    //        if (queryBlock.getFrom() != null) {
    //            MorientSelectResponse.responseselect(c, selectStatement.toString(), selectStatement);
    //            return;
    //        }
    //        SQLSelectItem selectItem = queryBlock.getSelectList().get(0);
    //        boolean number = true;
    //        try {
    //            Double.parseDouble(selectItem.toString());
    //        } catch (Exception e) {
    ////            e.printStackTrace();
    //            number = false;
    //        }
    //        if (number) {
    //            MSelect1Response.response(c, selectItem.toString(), Collections.singletonList(selectItem.toString()));
    //            return;
    //        }
    //        if (selectItem.getExpr() instanceof SQLBinaryOpExpr) {
    ////            return
    ////            handleopexpr((SQLBinaryOpExpr) selectItem.getExpr());
    //
    //        }
    //
    //        SQLExpr sqlExpr = selectItem.getExpr();
    ////        if (selectStatement.toString().contains("@") && selectStatement.toString().contains("AS")) {
    ////            List<String> column = selectVariables.getcolumn(selectStatement);
    ////            List<String> value = selectVariables.getbs(selectStatement, column);
    ////            MselectNResponse.response(c, column, value);
    ////            return;
    ////        }
    //        String what = sqlExpr.toString().toUpperCase();
    //        if (what.contains("VERSION_COMMENT")) {
    //            MSelectVersionComment.response(c);
    //            return;
    //        }
    //        if (what.contains("DATABASE")) {
    //            MSelectDatabase.response(c);
    //            return;
    //        }
    //        if (what.contains("CONNECTION_ID")) {
    //            MSelectConnnectID.response(c);
    //            return;
    //        }
    //        if (what.contains("USER")) {
    //            MSelectUser.response(c);
    //            return;
    //        }
    //        if (what.contains("VERSION")) {
    //            MSelectVersion.response(c);
    //            return;
    //        }
    //        //SESSION_INCREMENT
    //        if (what.contains("INCREMENT")) {
    //            MSessionIncrement.response(c);
    //            return;
    //        }
    //        //SESSION_ISOLATION
    //        if (what.contains("ISOLATION")) {
    //            SessionIsolation.response(c);
    //            return;
    //        }
    //        if (what.contains("LAST_INSERT_ID")) {
    //            MSelectLastInsertId.response(c, selectStatement.toString(), 1);
    //            return;
    //        }
    //        if (what.contains("IDENTITY")) {
    //            MSelectIdentity.response(c, selectStatement.toString(), 1, "mysql");
    //            return;
    //        }
    //        if (what.contains("SELECT_VAR_ALL")) {
    //            ShowVariables.response(c);
    //            return;
    //
    //        }
    //        //SESSION_TX_READ_ONLY
    //        if (what.contains("TX_READ_ONLY")) {
    //            MSelectTxReadOnly.response(c);
    //            return;
    //        }
    //        what = sqlExpr.toString();
    //        if (what.contains("@@")) {
    //            int index = what.indexOf(".");
    //            if (index != -1) {
    //                what = what.substring(index + 1);
    //                what = "select value from " + MvariableTable.tablename + "  where Variable_name='" + what + "';";
    //                try {
    //                    Stream<OElement> documents = OConnection.DB_ADMIN.query(what, Minformation_schama.dbname);
    //                    MSelect1Response.response(c, what, Arrays.asList(documents.findAny().get().getProperty("value")));
    //                    return;
    //                } catch (StorageException e) {
    //                    e.printStackTrace();
    //                    c.writeErrMessage(e.getMessage());
    //                    return;
    //                }
    //            }
    //        }
    //        c.writeNotSurrport();
    //    }

    private fun handleopexpr(sqlBinaryOpExpr: SQLBinaryOpExpr): Any {
        val columnname = sqlBinaryOpExpr.toString()
        val element = ODocument()
        element.setProperty(columnname, Mcomputer.compute(columnname).toString() + "")
        return MyResultSet(listOf<OElement>(element), listOf(columnname))
    }


}
