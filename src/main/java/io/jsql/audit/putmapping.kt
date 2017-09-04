/*
 * Java-based distributed database like Mysql
 */

package io.jsql.audit

import org.apache.http.entity.ContentType
import org.apache.http.nio.entity.NStringEntity

/**
\* Created with IntelliJ IDEA.
\* User: jiang
\* Date: 2017/6/15 0015
简历maping，也就是建立表
\* Time: 22:36
\*/
fun main(args: Array<String>) {
    var string = """
{
  "mappings": {
    "sqllog": {
      "_all":       { "enabled": false  },
      "properties": {
        "sql":    { "type": "text"  },
        "user":     { "type": "text"  },
        "host":      { "type": "text" } ,
"time" :{"type":"date"}
      }
    },
    "loginlog": {
      "_all":       { "enabled": false  },
      "properties": {
        "user":    { "type": "text"  },
        "host":     { "type": "text"  },
        "result":  {
          "type":   "boolean"
        },
        "time":  {
          "type":   "date"

        }
      }
    }
  }
}

"""
    var entity = NStringEntity(string, ContentType.APPLICATION_JSON)
    try {
        elasticUtil.esrestClient.performRequest("put", "/sqlindex", emptyMap(), entity)
        } catch (e: Exception) {
        elasticUtil.esrestClient.performRequest("delete", "/sqlindex", emptyMap())
        elasticUtil.esrestClient.performRequest("put", "/sqlindex", emptyMap(), entity)
    }
    finally {
        elasticUtil.close()
    }
}