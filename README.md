> Based on happyfish100/fastdfs-client-java
> With more feature & bugs fixed and release to mvnrepository.com.

# FastDFS java client SDK

FastDFS Java Client API may be copied only under the terms of the BSD license.

## 依赖

在您的maven项目pom.xml中添加

```xml
	<dependency>
		<groupId>in.clouthink.repack</groupId>
		<artifactId>fastdfs-client-java</artifactId>
		<version>1.29.3</version>
	</dependency>
```

在您的gradle项目build.gradle中添加

```gradle
	compile "in.clouthink.repack:fastdfs-client-java:1.29.3"
```

## .conf 配置文件、所在目录、加载优先顺序

fastdfs-client.properties

```
## fastdfs-client.properties

fastdfs.connect_timeout_in_seconds = 5
fastdfs.network_timeout_in_seconds = 30

fastdfs.charset = UTF-8

fastdfs.http_anti_steal_token = false
fastdfs.http_secret_key = FastDFS1234567890
fastdfs.http_tracker_http_port = 80

fastdfs.tracker_servers = 10.0.11.201:22122,10.0.11.202:22122,10.0.11.203:22122

```

fdfs_client.conf

```properties
connect_timeout = 2
network_timeout = 30
charset = UTF-8
http.tracker_http_port = 8080
http.anti_steal_token = no
http.secret_key = FastDFS1234567890

tracker_server = 10.0.11.247:22122
tracker_server = 10.0.11.248:22122
tracker_server = 10.0.11.249:22122

```

* 配置文件名fdfs_client.conf(或使用其它文件名xxx_yyy.conf)
* 文件所在位置可以是项目classpath(或OS文件系统目录比如/opt/):
* 优先按OS文件系统路径读取，没有找到才查找项目classpath，尤其针对linux环境下的相对路径比如：
    * fdfs_client.conf
    * config/fdfs_client.conf
    

```properties
connect_timeout = 2
network_timeout = 30
charset = UTF-8
http.tracker_http_port = 80
http.anti_steal_token = no
http.secret_key = FastDFS1234567890

tracker_server = 10.0.11.247:22122
tracker_server = 10.0.11.248:22122
tracker_server = 10.0.11.249:22122
```

注1：tracker_server指向您自己IP地址和端口，1-n个
注2：除了tracker_server，其它配置项都是可选的


## .properties 配置文件、所在目录、加载优先顺序

* 配置文件名 fastdfs-client.properties(或使用其它文件名 xxx-yyy.properties)
* 文件所在位置可以是项目classpath(或OS文件系统目录比如/opt/):
* 优先按OS文件系统路径读取，没有找到才查找项目classpath，尤其针对linux环境下的相对路径比如：
    * fastdfs-client.properties
    * config/fastdfs-client.properties

```properties
fastdfs.connect_timeout_in_seconds = 5
fastdfs.network_timeout_in_seconds = 30
fastdfs.charset = UTF-8
fastdfs.http_anti_steal_token = false
fastdfs.http_secret_key = FastDFS1234567890
fastdfs.http_tracker_http_port = 80

fastdfs.tracker_servers = 10.0.11.201:22122,10.0.11.202:22122,10.0.11.203:22122
```

注1：properties 配置文件中属性名跟 conf 配置文件不尽相同，并且统一加前缀"fastdfs."，便于整合到用户项目配置文件
注2：fastdfs.tracker_servers 配置项不能重复属性名，多个 tracker_server 用逗号","隔开
注3：除了fastdfs.tracker_servers，其它配置项都是可选的


## 加载配置示例

加载原 conf 格式文件配置：

```java
ClientGlobal.init("fdfs_client.conf");
ClientGlobal.init("config/fdfs_client.conf");
ClientGlobal.init("/opt/fdfs_client.conf");
ClientGlobal.init("C:\\Users\\James\\config\\fdfs_client.conf");
```    

加载 properties 格式文件配置：

```java
ClientGlobal.initByProperties("fastdfs-client.properties");
ClientGlobal.initByProperties("config/fastdfs-client.properties");
ClientGlobal.initByProperties("/opt/fastdfs-client.properties");
ClientGlobal.initByProperties("C:\\Users\\James\\config\\fastdfs-client.properties");
```

加载 Properties 对象配置：
    
```java    
Properties props = new Properties();
props.put(ClientGlobal.PROP_KEY_TRACKER_SERVERS, "10.0.11.101:22122,10.0.11.102:22122");
ClientGlobal.initByProperties(props);
```

加载 trackerServers 字符串配置：
    
```java
String trackerServers = "10.0.11.101:22122,10.0.11.102:22122";
ClientGlobal.initByTrackers(trackerServers);
```

## 检查加载配置结果：
    
```java
System.out.println("ClientGlobal.configInfo(): " + ClientGlobal.configInfo());
```    
    
输出结果：

>	ClientGlobal.configInfo(): {
>	  g_connect_timeout(ms) = 5000
>	  g_network_timeout(ms) = 30000
>	  g_charset = UTF-8
>	  g_anti_steal_token = false
>	  g_secret_key = FastDFS1234567890
>	  g_tracker_http_port = 80
>	  trackerServers = 10.0.11.101:22122,10.0.11.102:22122
>	}

