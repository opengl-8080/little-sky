package littlesky.model.http;

import littlesky.InvalidInputException;
import littlesky.util.Logger;

import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.util.Optional;

public class HttpProxy {
    private static final Logger logger = Logger.getInstance();
    private final String host;
    private final Integer port;
    private final String username;
    private final String password;
    
    public HttpProxy(String host, Integer port, String username, String password) {
        if (port != null && (port < 0 || 65535 < port)) {
            throw new InvalidInputException("HTTP proxy port must be between 0 and 65535.");
        }
        
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }
    
    public Optional<String> getHost() {
        return Optional.ofNullable(this.host);
    }
    
    public Optional<Integer> getPort() {
        return Optional.ofNullable(this.port);
    }
    
    public Optional<String> getUsername() {
        return Optional.ofNullable(this.username);
    }
    
    public Optional<String> getPassword() {
        return Optional.ofNullable(this.password);
    }
    
    public Proxy toProxy() {
        this.initAuthenticator();
        
        if (this.host == null || this.host.isEmpty()) {
            logger.debug("not proxy");
            return Proxy.NO_PROXY;
        }
        
        int port = this.port == null ? 80 : this.port;
        InetSocketAddress address = new InetSocketAddress(this.host, port);
        Proxy proxy = new Proxy(Proxy.Type.HTTP, address);
        logger.debug(() -> "proxy = " + proxy);
        return proxy;
    }
    
    private void initAuthenticator() {
        if (this.username == null || this.username.isEmpty()) {
            logger.debug("set Authenticator to null.");
            Authenticator.setDefault(null);
            return;
        }
        
        logger.debug("set Authenticator.");
        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                char[] pass = password == null ? "".toCharArray() : password.toCharArray();
                return new PasswordAuthentication(username, pass);
            }
        });
    }
}
