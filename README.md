# Description

* 一个基于spray的HTTP服务，本来想做各银行信用卡办卡进度的查询，不过只实现了建行跟民生，农业银行做了一半。
* 结合了swagger进说服务入参出参的文件说明，应用起来后访问*/index.html进行查看
* process模块是发送http请求到银行并解析的模块 

# Preposition
* [maven](http://maven.apache.org)
* [spray](http://www.spray.io)
* [akka](http://www.akka.io)
* [swagger](http://www.swagger.io)
* [spray-swagger](https://github.com/gettyimages/spray-swagger)

# Structure
* resource包内为接收请求并处理的类，即可以理解成spring框架下的controller，都继承自routing包内的BBHttpService特质，最后Resources特质继承所有的 ***Resource
* service包内的 ***Service对应resource包内的 ***Resource,被 ***Resource依赖
* RestInterface负责实例化 ***Resource所依赖的所有 ***Service类，同时暴露出swagger地址，最后将所有的 ***Resource的Route串联形成一个链条

# Authority
* spray提供了一个基本的Http Basic Authentication，但有时我们并不需要，大多数情况下可能是根据clientId，clientToken的形式来校验，在RestInterface中实现了一个简单的ContextAuthenticator，我们可以把需要校验的route放在一起，用authenticate进行包装。


 