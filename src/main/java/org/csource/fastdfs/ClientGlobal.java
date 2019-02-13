/**
 * Copyright (C) 2008 Happy Fish / YuQing
 * <p>
 * FastDFS Java Client may be copied only under the terms of the GNU Lesser
 * General Public License (LGPL).
 * Please visit the FastDFS Home Page http://www.csource.org/ for more detail.
 **/

package org.csource.fastdfs;

import org.csource.common.IniFileReader;
import org.csource.common.FastdfsException;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Global variables
 *
 * @author Happy Fish / YuQing
 * @version Version 1.11
 */
public class ClientGlobal {

    public static final String CONF_KEY_CONNECT_TIMEOUT        = "connect_timeout";
    public static final String CONF_KEY_NETWORK_TIMEOUT        = "network_timeout";
    public static final String CONF_KEY_CHARSET                = "charset";
    public static final String CONF_KEY_HTTP_ANTI_STEAL_TOKEN  = "http.anti_steal_token";
    public static final String CONF_KEY_HTTP_SECRET_KEY        = "http.secret_key";
    public static final String CONF_KEY_HTTP_TRACKER_HTTP_PORT = "http.tracker_http_port";
    public static final String CONF_KEY_TRACKER_SERVER         = "tracker_server";

    public static final String PROP_KEY_CONNECT_TIMEOUT_IN_SECONDS = "fastdfs.connect_timeout_in_seconds";
    public static final String PROP_KEY_NETWORK_TIMEOUT_IN_SECONDS = "fastdfs.network_timeout_in_seconds";
    public static final String PROP_KEY_CHARSET                    = "fastdfs.charset";
    public static final String PROP_KEY_HTTP_ANTI_STEAL_TOKEN      = "fastdfs.http_anti_steal_token";
    public static final String PROP_KEY_HTTP_SECRET_KEY            = "fastdfs.http_secret_key";
    public static final String PROP_KEY_HTTP_TRACKER_HTTP_PORT     = "fastdfs.http_tracker_http_port";
    public static final String PROP_KEY_TRACKER_SERVERS            = "fastdfs.tracker_servers";

    public static final int     DEFAULT_CONNECT_TIMEOUT        = 5; //second
    public static final int     DEFAULT_NETWORK_TIMEOUT        = 30; //second
    public static final String  DEFAULT_CHARSET                = "UTF-8";
    public static final boolean DEFAULT_HTTP_ANTI_STEAL_TOKEN  = false;
    public static final String  DEFAULT_HTTP_SECRET_KEY        = "FastDFS1234567890";
    public static final int     DEFAULT_HTTP_TRACKER_HTTP_PORT = 80;

    public static int     connectTimeout  = DEFAULT_CONNECT_TIMEOUT * 1000; //millisecond
    public static int     networkTimeout  = DEFAULT_NETWORK_TIMEOUT * 1000; //millisecond
    public static String  charset         = DEFAULT_CHARSET;
    public static boolean antiStealToken  = DEFAULT_HTTP_ANTI_STEAL_TOKEN; //if anti-steal token
    public static String  secretKey       = DEFAULT_HTTP_SECRET_KEY; //generage token secret key
    public static int     trackerHttpPort = DEFAULT_HTTP_TRACKER_HTTP_PORT;

    public static TrackerGroup trackerGroup;

    private ClientGlobal() {
    }

    /**
     * load global variables
     *
     * @param conf_filename config filename
     * @throws IOException ioex
     * @throws FastdfsException myex
     */
    public static void init(String conf_filename) throws IOException, FastdfsException {
        IniFileReader iniReader;
        String[] szTrackerServers;
        String[] parts;

        iniReader = new IniFileReader(conf_filename);

        connectTimeout = iniReader.getIntValue("connect_timeout", DEFAULT_CONNECT_TIMEOUT);
        if (connectTimeout < 0) {
            connectTimeout = DEFAULT_CONNECT_TIMEOUT;
        }
        connectTimeout *= 1000; //millisecond

        networkTimeout = iniReader.getIntValue("network_timeout", DEFAULT_NETWORK_TIMEOUT);
        if (networkTimeout < 0) {
            networkTimeout = DEFAULT_NETWORK_TIMEOUT;
        }
        networkTimeout *= 1000; //millisecond

        charset = iniReader.getStrValue("charset");
        if (charset == null || charset.length() == 0) {
            charset = "ISO8859-1";
        }

        szTrackerServers = iniReader.getValues("tracker_server");
        if (szTrackerServers == null) {
            throw new FastdfsException("item \"tracker_server\" in " + conf_filename + " not found");
        }

        InetSocketAddress[] tracker_servers = new InetSocketAddress[szTrackerServers.length];
        for (int i = 0; i < szTrackerServers.length; i++) {
            parts = szTrackerServers[i].split("\\:", 2);
            if (parts.length != 2) {
                throw new FastdfsException("the value of item \"tracker_server\" is invalid, the correct format is host:port");
            }

            tracker_servers[i] = new InetSocketAddress(parts[0].trim(), Integer.parseInt(parts[1].trim()));
        }
        trackerGroup = new TrackerGroup(tracker_servers);

        trackerHttpPort = iniReader.getIntValue("http.tracker_http_port", 80);
        antiStealToken = iniReader.getBoolValue("http.anti_steal_token", false);
        if (antiStealToken) {
            secretKey = iniReader.getStrValue("http.secret_key");
        }
    }

    /**
     * load from properties file
     *
     * @param propsFilePath properties file path, eg:
     *                      "fastdfs-client.properties"
     *                      "config/fastdfs-client.properties"
     *                      "/opt/fastdfs-client.properties"
     *                      "C:\\Users\\James\\config\\fastdfs-client.properties"
     *                      properties文件至少包含一个配置项 fastdfs.tracker_servers 例如：
     *                      fastdfs.tracker_servers = 10.0.11.245:22122,10.0.11.246:22122
     *                      server的IP和端口用冒号':'分隔
     *                      server之间用逗号','分隔
     * @throws IOException ioex
     * @throws FastdfsException myex
     */
    public static void initByProperties(String propsFilePath) throws IOException, FastdfsException {
        Properties props = new Properties();
        InputStream in = IniFileReader.loadFromOsFileSystemOrClasspathAsStream(propsFilePath);
        if (in != null) {
            props.load(in);
        }
        initByProperties(props);
    }

    public static void initByProperties(Properties props) throws IOException, FastdfsException {
        String trackerServersConf = props.getProperty(PROP_KEY_TRACKER_SERVERS);
        if (trackerServersConf == null || trackerServersConf.trim().length() == 0) {
            throw new FastdfsException(String.format("configure item %s is required", PROP_KEY_TRACKER_SERVERS));
        }
        initByTrackers(trackerServersConf.trim());

        String connectTimeoutInSecondsConf = props.getProperty(PROP_KEY_CONNECT_TIMEOUT_IN_SECONDS);
        String networkTimeoutInSecondsConf = props.getProperty(PROP_KEY_NETWORK_TIMEOUT_IN_SECONDS);
        String charsetConf = props.getProperty(PROP_KEY_CHARSET);
        String httpAntiStealTokenConf = props.getProperty(PROP_KEY_HTTP_ANTI_STEAL_TOKEN);
        String httpSecretKeyConf = props.getProperty(PROP_KEY_HTTP_SECRET_KEY);
        String httpTrackerHttpPortConf = props.getProperty(PROP_KEY_HTTP_TRACKER_HTTP_PORT);
        if (connectTimeoutInSecondsConf != null && connectTimeoutInSecondsConf.trim().length() != 0) {
            connectTimeout = Integer.parseInt(connectTimeoutInSecondsConf.trim()) * 1000;
        }
        if (networkTimeoutInSecondsConf != null && networkTimeoutInSecondsConf.trim().length() != 0) {
            networkTimeout = Integer.parseInt(networkTimeoutInSecondsConf.trim()) * 1000;
        }
        if (charsetConf != null && charsetConf.trim().length() != 0) {
            charset = charsetConf.trim();
        }
        if (httpAntiStealTokenConf != null && httpAntiStealTokenConf.trim().length() != 0) {
            antiStealToken = Boolean.parseBoolean(httpAntiStealTokenConf);
        }
        if (httpSecretKeyConf != null && httpSecretKeyConf.trim().length() != 0) {
            secretKey = httpSecretKeyConf.trim();
        }
        if (httpTrackerHttpPortConf != null && httpTrackerHttpPortConf.trim().length() != 0) {
            trackerHttpPort = Integer.parseInt(httpTrackerHttpPortConf);
        }
    }

    /**
     * load from properties file
     *
     * @param trackerServers 例如："10.0.11.245:22122,10.0.11.246:22122"
     *                       server的IP和端口用冒号':'分隔
     *                       server之间用逗号','分隔
     * @throws IOException ioex
     * @throws FastdfsException myex
     */
    public static void initByTrackers(String trackerServers) throws IOException, FastdfsException {
        List<InetSocketAddress> list = new ArrayList();
        String spr1 = ",";
        String spr2 = ":";
        String[] arr1 = trackerServers.trim().split(spr1);
        for (String addrStr : arr1) {
            String[] arr2 = addrStr.trim().split(spr2);
            String host = arr2[0].trim();
            int port = Integer.parseInt(arr2[1].trim());
            list.add(new InetSocketAddress(host, port));
        }
        InetSocketAddress[] trackerAddresses = list.toArray(new InetSocketAddress[list.size()]);
        initByTrackers(trackerAddresses);
    }

    public static void initByTrackers(InetSocketAddress[] trackerAddresses) throws IOException, FastdfsException {
        trackerGroup = new TrackerGroup(trackerAddresses);
    }

    /**
     * construct Socket object
     *
     * @param ip_addr ip address or hostname
     * @param port    port number
     * @return connected Socket object
     * @throws IOException ioex
     */
    public static Socket getSocket(String ip_addr, int port) throws IOException {
        Socket sock = new Socket();
        sock.setSoTimeout(ClientGlobal.networkTimeout);
        sock.connect(new InetSocketAddress(ip_addr, port), ClientGlobal.connectTimeout);
        return sock;
    }

    /**
     * construct Socket object
     *
     * @param addr InetSocketAddress object, including ip address and port
     * @return connected Socket object
     * @throws IOException ioex
     */
    public static Socket getSocket(InetSocketAddress addr) throws IOException {
        Socket sock = new Socket();
        sock.setSoTimeout(ClientGlobal.networkTimeout);
        sock.connect(addr, ClientGlobal.connectTimeout);
        return sock;
    }

    public static int getConnectTimeout() {
        return connectTimeout;
    }

    public static void setConnectTimeout(int connect_timeout) {
        ClientGlobal.connectTimeout = connect_timeout;
    }

    public static int getNetworkTimeout() {
        return networkTimeout;
    }

    public static void setNetworkTimeout(int network_timeout) {
        ClientGlobal.networkTimeout = network_timeout;
    }

    public static String getCharset() {
        return charset;
    }

    public static void setCharset(String charset) {
        ClientGlobal.charset = charset;
    }

    public static int getTrackerHttpPort() {
        return trackerHttpPort;
    }

    public static void setTrackerHttpPort(int tracker_http_port) {
        ClientGlobal.trackerHttpPort = tracker_http_port;
    }

    public static boolean getAntiStealToken() {
        return antiStealToken;
    }

    public static boolean isAntiStealToken() {
        return antiStealToken;
    }

    public static void setAntiStealToken(boolean anti_steal_token) {
        ClientGlobal.antiStealToken = anti_steal_token;
    }

    public static String getSecretKey() {
        return secretKey;
    }

    public static void setSecretKey(String secret_key) {
        ClientGlobal.secretKey = secret_key;
    }

    public static TrackerGroup getTrackerGroup() {
        return trackerGroup;
    }

    public static void setTrackerGroup(TrackerGroup tracker_group) {
        ClientGlobal.trackerGroup = tracker_group;
    }

    public static String configInfo() {
        String trackerServers = "";
        if (trackerGroup != null) {
            InetSocketAddress[] trackerAddresses = trackerGroup.tracker_servers;
            for (InetSocketAddress inetSocketAddress : trackerAddresses) {
                if (trackerServers.length() > 0) {
                    trackerServers += ",";
                }
                trackerServers += inetSocketAddress.toString().substring(1);
            }
        }
        return "{" + "\n  connect_timeout(ms) = " + connectTimeout + "\n  network_timeout(ms) = " + networkTimeout + "\n  charset = " +
                charset + "\n  anti_steal_token = " + antiStealToken +
                "\n  secret_key = " + secretKey + "\n  tracker_http_port = " + trackerHttpPort +
                "\n  trackerServers = " + trackerServers + "\n}";
    }

}
