package littlesky.model.http;

import littlesky.InvalidInputException;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Optional;

public class HttpProxy {
    private final String host;
    private final Integer port;
    
    public HttpProxy(String host, Integer port) {
        if (port != null && (port < 0 || 65535 < port)) {
            throw new InvalidInputException("HTTP proxy port must be between 0 and 65535.");
        }
        
        this.host = host;
        this.port = port;
    }
    
    public Optional<String> getHost() {
        return Optional.ofNullable(this.host);
    }
    
    public Optional<Integer> getPort() {
        return Optional.ofNullable(this.port);
    }
    
    public Proxy toProxy() {
        if (this.host == null || this.host.isEmpty()) {
            return Proxy.NO_PROXY;
        }
        
        int port = this.port == null ? 80 : this.port;
        InetSocketAddress address = new InetSocketAddress(this.host, port);
        return new Proxy(Proxy.Type.HTTP, address);
    }
    
    
//    private static final int DEFAULT_PORT = 80;
//    private final ReadOnlyObjectWrapper<Proxy> proxy = new ReadOnlyObjectWrapper<>(Proxy.NO_PROXY);
//    private final StringProperty host = new SimpleStringProperty();
//    private final IntegerProperty port = new SimpleIntegerProperty(DEFAULT_PORT);
//    
//    public HttpProxy() {
//        this.proxy.bind(
//            binding(this.host, this.port)
//            .computeValue(() -> {
//                if (this.host.get().isEmpty()) {
//                    return Proxy.NO_PROXY;
//                } else {
//                    InetSocketAddress address = new InetSocketAddress(this.host.get(), this.port.get());
//                    return new Proxy(Proxy.Type.HTTP, address);
//                }
//            })
//        );
//    }
//    
//    public ReadOnlyObjectProperty<Proxy> proxyProperty() {
//        return this.proxy.getReadOnlyProperty();
//    }
//    
//    public String getHost() {
//        return this.host.get();
//    }
//    
//    public void setHost(String host) {
//        this.host.setValue(this.emptyIfNull(host));
//    }
//    
//    public void setPort(int port) {
//        if (port < 0 || 65535 < port) {
//            throw new InvalidInputException("HTTP proxy port must be between 0 and 65535.");
//        }
//        this.port.setValue(port);
//    }
//    
//    public int getPort() {
//        return this.port.get();
//    }
//    
//    public void setAuthInfo(String username, String password) {
//        Authenticator authenticator = null;
//        
//        if (!this.isEmpty(username)) {
//            authenticator = new Authenticator() {
//                @Override
//                protected PasswordAuthentication getPasswordAuthentication() {
//                    return new PasswordAuthentication(emptyIfNull(username), emptyIfNull(password).toCharArray());
//                }
//            };
//        }
//
//        Authenticator.setDefault(authenticator);
//    }
//    
    private String emptyIfNull(String text) {
        return text == null ? "" : text;
    }
//    
//    private boolean isEmpty(String text) {
//        return text == null || text.isEmpty();
//    }
}
