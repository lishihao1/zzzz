1：需要先建立zookeeper 初始节点：/registry  -> http://www.cnblogs.com/likehua/p/3999588.html
2：启动服务提供者 com.db.rpc.demo.privoder
3：启动服务消费者 com.db.rpc.demo.RpcConsumer

目录结构
	annotation 自定义注解
	auth 解码，编码
	client 客户端连接
	constant 常量，枚举
	demo 例子
	http 请求相关
	proxy 代理
	server 服务提供
		discovery 服务发现
	service 接口api
		impl 实现类
	registry 注册中心
	util 工具类
resources
	application-context.xml 服务提供者配置
	application-context-client.xml 消费者配置