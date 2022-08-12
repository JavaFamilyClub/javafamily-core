package club.javafamily.autoconfigre.resttemplate.config;

import club.javafamily.autoconfigre.resttemplate.properties.ProxyConfig;
import org.apache.http.HttpHost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.protocol.HttpContext;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;

/**
 * @author Jack Li
 * @date 2022/8/10 下午11:31
 * @description socks proxy support for ssl
 */
public class SocksSSLConnectionSocketFactory extends SSLConnectionSocketFactory {

    private ProxyConfig proxyConfig;

    public SocksSSLConnectionSocketFactory(SSLContext sslContext,
                                           HostnameVerifier hostnameVerifier,
                                           ProxyConfig proxyConfig)
    {
        super(sslContext, hostnameVerifier);
        this.proxyConfig = proxyConfig;
    }

    @Override
    public Socket createSocket(HttpContext context) throws IOException {
        if (proxyConfig != null && proxyConfig.getType() != null) {
            // 需要代理
            return new Socket(new Proxy(proxyConfig.getType(),
                    new InetSocketAddress(proxyConfig.getHost(), proxyConfig.getPort())));
        }

        return super.createSocket(context);
    }

    @Override
    public Socket connectSocket(int connectTimeout, Socket socket, HttpHost host, InetSocketAddress remoteAddress,
                                InetSocketAddress localAddress, HttpContext context)
            throws IOException
    {
        if (proxyConfig != null && proxyConfig.getType() == Proxy.Type.SOCKS) {
            // make proxy server to resolve host in http url
            remoteAddress = InetSocketAddress
                    .createUnresolved(proxyConfig.getHost(), proxyConfig.getPort());
        }
        return super.connectSocket(connectTimeout, socket, host, remoteAddress, localAddress, context);
    }
}
