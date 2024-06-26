package com.geeksforless.client.service.validation;

import com.geeksforless.client.model.ProxyConfigHolder;
import com.geeksforless.client.security.config.OkHttpBeanFactory;
import com.squareup.okhttp.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

@Service
public class ProxyValidationServiceImpl implements ProxyValidationService {

    private static final Logger logger = LogManager.getLogger(ProxyValidationServiceImpl.class);

    private final OkHttpBeanFactory okHttpBeanFactory;
    private final String validationUrl;
    private Response response;

    public ProxyValidationServiceImpl(OkHttpBeanFactory okHttpBeanFactory, @Value("${client.proxy.validation.url}") String validationUrl) {
        this.okHttpBeanFactory = okHttpBeanFactory;
        this.validationUrl = validationUrl;
    }

    @Override
    public boolean isValid(ProxyConfigHolder configHolder) {
        if (configHolder == null ||
                configHolder.getProxyNetworkConfig().getHostname() == null ||
                configHolder.getProxyNetworkConfig().getPort() == null) {
            logger.warn("ProxyConfigHolder is null or not specified hostname and port");
            return false;
        }
        try {
            createResponse(configHolder);
        } catch (IOException e) {
            logger.error("Error while validating proxy: " + e.getMessage());
            return false;
        }
        return response.isSuccessful();
    }

    private Proxy createProxy(String hostname, Integer port) {
        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(hostname, port));
    }

    private Request createRequest() {
        return new Request.Builder()
                .url(validationUrl)
                .build();
    }

    void createResponse(ProxyConfigHolder configHolder) throws IOException {
        Proxy proxy = createProxy(configHolder.getProxyNetworkConfig().getHostname(),
                configHolder.getProxyNetworkConfig().getPort());
        Authenticator proxyAuthenticator = new Authenticator() {
            @Override
            public Request authenticate(Proxy proxy, Response response) throws IOException {
                String credentials = Credentials.basic(configHolder.getProxyCredentials().getUsername(),configHolder.getProxyCredentials().getPassword());
                return response.request().newBuilder().header("Authorization",credentials).build();
            }

            @Override
            public Request authenticateProxy(Proxy proxy, Response response) throws IOException {
                String credentials = Credentials.basic(configHolder.getProxyCredentials().getUsername(),configHolder.getProxyCredentials().getPassword());
                return response.request().newBuilder().header("Proxy-Authorization",credentials).build();
            }
        };
        OkHttpClient client = okHttpBeanFactory.getObject();
        if (client == null) {
            logger.error("OkHttpClient Bean could not be initialized");
            throw new RuntimeException();
        }
        client.setProxy(proxy);
        client.setAuthenticator(proxyAuthenticator);
        client.setConnectTimeout(30, TimeUnit.SECONDS);
        client.setReadTimeout(30, TimeUnit.SECONDS);
        response = client.newCall(createRequest()).execute();
        // Close connection
        response.body().close();
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}