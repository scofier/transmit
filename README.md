# 报文格式转换工具：transmit

## 功能描述：

因为每家保险公司的接口都是不相同的, 开发人员不能每对接一家公司, 就单独开发一套保险产品接口.
所以写了这个工具.

可以做到接口之间参数转换,转发. 节省了对接接口时的开发任务.

通过配置和模板的形式, 支持任意类型的参数转换.

模板使用freemarker.

开发框架使用vert.x

## 使用说明

#### 打包命令
```bash
clean package -D skipTests dependency:copy-dependencies -DoutputDirectory=./target/lib -f pom.xml
```

#### 文件目录
```
.
├── config 参数转换模板文件
│   └── XXXX
│       ├── JAH-transit-result.json
│       └── JAH-transit-send.xml
│
├── config.json 项目启动要加载的配置
│
├── lib  项目打包后的文件
│   └── vertx-web-common-3.8.0.jar
│   └── ....
│
├── log  日志
│   ├── error  
│   │   └── error.2019-09-09.log
│   └── info   
│       └── info.2019-09-09.log
│
└── start.sh  启动脚本

```

#### 启动脚本

```bash
#!/bin/bash

JAVA_OPTS=""
JAVA_MEM_OPTS=""

# 进入当前文件目录
cd `dirname $0`
# 项目路径
DEPLOY_DIR=`pwd`
CONF_DIR=$DEPLOY_DIR/conf
STDOUT_FILE=$DEPLOY_DIR/sout.log

# lib
LIB_DIR=$DEPLOY_DIR/lib
LIB_JARS=`ls $LIB_DIR|grep .jar|awk '{print "'$LIB_DIR'/"$0}'|tr "\n" ":"`

# 判断是否已经启动
PID=`ps -ef | grep "$DEPLOY_DIR" | grep -v 'grep' | awk '{print $2}'`

if [ -n "$PID" ]; then
    echo "已经启动"
else
    # 启动命令
    java $JAVA_OPTS $JAVA_MEM_OPTS -classpath $CONF_DIR:$LIB_JARS com.hebaibai.ctrt.Main -c config.json > $STDOUT_FILE 2>&1 &
    echo "启动成功"
fi

```

#### 停止脚本
```bash
#!/bin/bash

# 进入当前文件目录
cd `dirname $0`
# 项目路径
DEPLOY_DIR=`pwd`

PID=`ps -ef | grep "$DEPLOY_DIR" | grep -v 'grep' | awk '{print $2}'`

if [ -n "$PID" ]; then
    echo PID: $PID
    kill -9 $PID
fi
```


## 配置说明
```
{

  其他配置
  "config": {
    
    系统端口号
    "port": 9090,
    
    其他组件加载, 执行CLass.forName
    "ext": [
      "com.hebaibai.ctrt.Driver"
    ],
    
    数据库配置, 用于保存接口请求日志
    "db": {
      "host": "127.0.0.1",
      "database": "dbname",
      "port": 3306,
      "username": "root",
      "password": "root"
    }
  },
  
  配置示例
  "config-demo": {
  
    接受请求
    "request": {
    
      接受请求的地址 127.0.0.1:9090/downloadPolicy
      "path": "/downloadPolicy",
      
      请求的方式
      "method": "GET",
      
      请求参数类型: FORM(表单提交),  JSON(json),  QUERY(?key=value&key2=value),  TEXT(文本),  XML(xml),
      "request-type": "QUERY",
      
      返回参数类型
      "response-type": "TEXT"
    },
    
    转发的接口配置
    "api": {
    
      接口请求地址
      "url": "http://47.98.105.202:9003/api/downloadPolicyUrl",
      
      接口签名编号(需要手动实现)
      "signCode": "null",
      
      接口请求地址
      "method": "GET",
      
      请求参数类型
      "request-type": "QUERY",
      
      请求超时设置,默认3000 ms, 单位ms
      "timeout": 1,
      
      返回参数类型
      "response-type": "TEXT",
      
      请求参数转换模板
      "request-ftl": "/home/hjx/work/myProject/transmit/file/downloadPolicy-req.ftl",
      
      响应参数转换模板
      "response-ftl": "/home/hjx/work/myProject/transmit/file/downloadPolicy-res.ftl"
    }
  }
}
```

    


​           