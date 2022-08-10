package club.javafamily.autoconfigre.resttemplate.config;

import club.javafamily.autoconfigre.resttemplate.properties.ProxyConfig;
import org.apache.http.HttpHost;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;

public class SocksPlainConnectionSocketFactory extends PlainConnectionSocketFactory {

    private ProxyConfig proxyConfig;

    public SocksPlainConnectionSocketFactory(ProxyConfig proxyConfig) {
        this.proxyConfig = proxyConfig;
    }

    @Override
    public Socket createSocket(HttpContext context) throws IOException {
        if (proxyConfig != null) {
            //需要代理
            return new Socket(new Proxy(proxyConfig.getType(),
                    new InetSocketAddress(proxyConfig.getHost(), proxyConfig.getPort())));
        } else {
            return super.createSocket(context);
        }
    }

    @Override
    public Socket connectSocket(int connectTimeout, Socket socket, HttpHost host, InetSocketAddress remoteAddress,
                                InetSocketAddress localAddress, HttpContext context)
            throws IOException
    {
        if (proxyConfig != null) {
            // make proxy server to resolve host in http url
            remoteAddress = InetSocketAddress
                    .createUnresolved(proxyConfig.getHost(), proxyConfig.getPort());
        }
        return super.connectSocket(connectTimeout, socket, host, remoteAddress, localAddress, context);
    }

}
