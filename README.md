# 报文格式转换工具：converter
## 功能描述：

可以把**xml**/**json**形式的数据根据自定义的模板来转换成为你想要的任何格式。

需要用到 **freemarker** 方面的知识。

## 示例

## 配置说明
```
{
  系统的端口号
  "port": 9090, 
  
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