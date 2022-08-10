package club.javafamily.autoconfigre.resttemplate.config;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class ProxyNamePwdAuthenticator extends Authenticator {
    private String user ;
    private String password ;

    public ProxyNamePwdAuthenticator(String user, String password) {
        this.user = user;
        this.password = password;
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password.toCharArray());
    }
}