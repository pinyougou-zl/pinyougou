<html>
<head>
    <meta charset="utf-8">
    <title>Freemarker入门小DEMO </title>
</head>
<body>
<#--include嵌套指令-->
<#include "head.ftl">
<#--我只是一个注释，我不会有任何输出  -->
<#--3、插值（Interpolation）：即${..}部分,将使用数据模型中的部分替代输出-->
${name},你好。${message}
<#--FTL指令-->
<#--assign指令，定义简单类型-->
<#assign linkman="刘先生">
联系人：${linkman}
<#--定义对象类型-->
<#assign info={"mobile":"13301231212",'address':'北京市昌平区王府街'}>
电话：${info.mobile} 地址：${info.address}
<#--if,判断指令-->
<#if success==true>
  你已通过实名认证
<#else>
  你未通过实名认证
</#if>

<#--list指令-->
<#list goodsList as goods>
    ${goods_index+1} 商品名称： ${goods.name} 价格：${goods.price}<br>
</#list>

<#--内建函数-->
共${goodsList?size}条记录

当前日期：${today?date} <br>
当前时间：${today?time} <br>
当前日期+时间：${today?datetime} <br>
日期格式化：  ${today?string("yyyy年MM月")}
显示数字：${point?c}

除空值（判断是否为空）
<#if aaa??>
    aaa变量存在
    <#else>
    aaa变量不存在
</#if>
在代码中不对aaa赋值，也不会报错了 ，当aaa为null则返回！后边的内容-
${aaa!'-'}
</body>
</html>