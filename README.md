# Overview

FastDFS Java Client API may be copied only under the terms of the BSD license.

## Dependencies

> for maven pom.xml

```xml
	<dependency>
		<groupId>in.clouthink.repack</groupId>
		<artifactId>fastdfs-client-java</artifactId>
		<version>1.29.3</version>
	</dependency>
```

> for gradle build.gradle

```gradle
	compile "in.clouthink.repack:fastdfs-client-java:1.29.3"
```

## Configuration sample

Format .conf(.ini) and .properties are support


> fastdfs-client.properties

```properties

fastdfs.connect_timeout_in_seconds = 5
fastdfs.network_timeout_in_seconds = 30

fastdfs.charset = UTF-8

fastdfs.http_anti_steal_token = false
fastdfs.http_secret_key = FastDFS1234567890
fastdfs.http_tracker_http_port = 80

fastdfs.tracker_servers = 10.0.11.201:22122,10.0.11.202:22122,10.0.11.203:22122

```

> fdfs_client.conf

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


注1：tracker_server指向您自己IP地址和端口，1-n个
注2：除了tracker_server，其它配置项都是可选的


## Load configuration sample

> Load by file .conf

```java
ClientGlobal.init("fdfs_client.conf");
ClientGlobal.init("config/fdfs_client.conf");
ClientGlobal.init("/opt/fdfs_client.conf");
ClientGlobal.init("C:\\Users\\James\\config\\fdfs_client.conf");
```    

> Load by file .properties

```java
ClientGlobal.initByProperties("fastdfs-client.properties");
ClientGlobal.initByProperties("config/fastdfs-client.properties");
ClientGlobal.initByProperties("/opt/fastdfs-client.properties");
ClientGlobal.initByProperties("C:\\Users\\James\\config\\fastdfs-client.properties");
```

> Load by java.util.Properties
    
```java    
Properties props = new Properties();
props.put(ClientGlobal.PROP_KEY_TRACKER_SERVERS, "10.0.11.101:22122,10.0.11.102:22122");
ClientGlobal.initByProperties(props);
```

> Load by programming
    
```java
String trackerServers = "10.0.11.101:22122,10.0.11.102:22122";
ClientGlobal.initByTrackers(trackerServers);
```

> Check loaded status
    
```java
System.out.println("ClientGlobal.configInfo(): " + ClientGlobal.configInfo());
```
    
Output：

>	ClientGlobal.configInfo(): {
>	  g_connect_timeout(ms) = 5000
>	  g_network_timeout(ms) = 30000
>	  g_charset = UTF-8
>	  g_anti_steal_token = false
>	  g_secret_key = FastDFS1234567890
>	  g_tracker_http_port = 80
>	  trackerServers = 10.0.11.101:22122,10.0.11.102:22122
>	}

