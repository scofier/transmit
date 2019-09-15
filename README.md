# 第三方接口对接/数据转换工具：transmit

## 功能描述：

当项目中需要使用同一套数据对接多家第三方接口的时候, 比如对接保险公司接口, 对接支付公司接口. 以往的情况是针对每家公司的接口文档开发一套代码, 这样会添加很多不必要的工作量. 针对这种情况, 我开发了这个工具. 这个工具可以做到接口之间参数转换,转发. 节省了对接接口时的开发任务.

## 优点

1. 数据转换使用freemarker模板, 无需编写java代码.
2. 使用vert.x框架编写. 效率高, 代码量小.
3. 请求数据入库, 数据有迹可循.
4. 可自己编写插件, 完成其定义签名和自定义freemarker指令.

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


### 配置说明

#### 示例配置

```
{

  其他配置
  "config": {
    
    系统端口号
    "port": 9090,
    
    其他组件加载, 执行CLass.forName, 可以加载自己定义的一些插件, 完成类似数据入库, 接口签名之类的功能
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
      
      插件编号, 在ext中加载来的
      "extCode": "null",
      
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

#### 日志表sql

```sql
-- auto-generated definition
create table api_log
(
  id          varchar(64) null,
  type_code   varchar(64) null
  comment '类型',
  send_msg    text        null
  comment '请求内容',
  receive     text        null
  comment '接口返回数据',
  end_time    datetime    null
  comment '请求耗时',
  create_time datetime    null
  comment '请求时间',
  status      int         null
  comment '状态1:success, 0:error'
)
  comment '接口请求日志';

```

## 接口转换示例

### 请求参数:

```xml
<Demo>
  <Info>
    <Code>XXX-1</Code>
    <UUID>d83a011a-958d-4310-a51b-0fb3a4228ef5</UUID>
	<Time>2017-11-15 16:57:36</Time>
  </Info>
  <XXX>
    <Order>
      <SerialNo>0</SerialNo>
      <OrderNo>123123123</OrderNo>
	  <OrderCode>asdasdasd</OrderCode>
	  <Result>1</Result>
    </Order>
  </XXX>
</Demo>
```

### 转发接口需要的数据:

#### 格式 JSON(POST)

```json
{
    "header": {
        "code": "${ROOT.Info.Code}",
        "date": "${ROOT.Info.Time}"
    },
    "body": {
        "orderCode": "${ROOT.XXX.Order.OrderCode}"
    }
}
```

#### 格式 FROM(POST)

```
code=${ROOT.Info.Code}
date=${ROOT.Info.Time}
orderCode=${ROOT.XXX.Order.OrderCode}
```

#### 格式 QUERY(GET)    

```
code=${ROOT.Info.Code}&date=${ROOT.Info.Time}&orderCode=${ROOT.XXX.Order.OrderCode}
```

#### 格式 XML(POST)           

```xml
<xml>
    <header>
        <code>${ROOT.Info.Code}</code>
        <date>${ROOT.Info.Time}</date>
    </header>
    <body>
        <orderCode>${ROOT.XXX.Order.OrderCode}</orderCode>
    </body>
</xml>
```

