package com.learn.ejbcaconnectorfeign.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.SslProvider;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.PrivateKey;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Configuration
public class WebClientConfig {

    @Value("${ejbca.api.base-url}")
    private String baseUrl;

    @Value("${ejbca.mtls.client-cert-path}")
    private Resource clientCert;

    @Value("${ejbca.mtls.client-key-path}")
    private Resource clientKey;

    @Bean
    public WebClient ejbcaWebClient() throws Exception {
        // Load client certificate
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        X509Certificate cert;
        try (InputStream certInputStream = clientCert.getInputStream()) {
            cert = (X509Certificate) certFactory.generateCertificate(certInputStream);
        }

        // Load private key
        String keyContent;
        try (InputStream keyInputStream = clientKey.getInputStream()) {
            keyContent = new String(keyInputStream.readAllBytes())
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
        }
        byte[] keyBytes = Base64.getDecoder().decode(keyContent);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(keySpec);

        // Create KeyStore and load client certificate and key
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(null, null);
        keyStore.setKeyEntry("client", privateKey, "changeit".toCharArray(), new java.security.cert.Certificate[]{cert});

        // Initialize KeyManagerFactory
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, "changeit".toCharArray());

        // Initialize SSLContext
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), null, null);

        // Configure HttpClient with SSL
        HttpClient httpClient = HttpClient.create()
                .secure(sslSpec -> sslSpec.sslContext(sslContext));

        // Create WebClient
        ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(connector)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}