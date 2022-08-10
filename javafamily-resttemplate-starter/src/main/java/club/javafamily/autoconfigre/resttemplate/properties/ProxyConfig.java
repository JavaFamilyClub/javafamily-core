package club.javafamily.autoconfigre.resttemplate.properties;

import org.apache.http.HttpHost;

import java.net.Proxy;

/**
 * 代理配置
 */
public class ProxyConfig {

    /**
     * 代理类型
     */
    private Proxy.Type type;

    /**
     * 代理 schema
     */
    private String schema = HttpHost.DEFAULT_SCHEME_NAME;

    /**
     * 代理主机
     */
    private String host;

    /**
     * 代理端口
     */
    private Integer port;

    /**
     * 账户
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Proxy.Type getType() {
        return type;
    }

    public void setType(Proxy.Type type) {
        this.type = type;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
