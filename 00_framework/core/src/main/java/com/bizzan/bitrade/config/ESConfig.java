package com.bizzan.bitrade.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


@Configuration
@Component
public class ESConfig {

    @Value("${es.username}")
    private String esUsername;

    @Value("${es.password}")
    private String esPassword;

    @Value("${es.mine.index}")
    private String esMineIndex;

    @Value("${es.mine.type}")
    private String esMineType;

    @Value("${es.public.ip}")
    private String publicNet;

    @Value("${es.private.ip}")
    private String privateNet;

    @Value("${es.port}")
    private int esPort;

    public int getEsPort() {
        return esPort;
    }

    public void setEsPort(int esPort) {
        this.esPort = esPort;
    }

    public String getPublicNet() {
        return publicNet;
    }

    public void setPublicNet(String publicNet) {
        this.publicNet = publicNet;
    }

    public String getPrivateNet() {
        return privateNet;
    }

    public void setPrivateNet(String privateNet) {
        this.privateNet = privateNet;
    }

    public String getEsUsername() {
        return esUsername;
    }

    public void setEsUsername(String esUsername) {
        this.esUsername = esUsername;
    }

    public String getEsPassword() {
        return esPassword;
    }

    public void setEsPassword(String esPassword) {
        this.esPassword = esPassword;
    }

    public String getEsMineIndex() {
        return esMineIndex;
    }

    public void setEsMineIndex(String esMineIndex) {
        this.esMineIndex = esMineIndex;
    }

    public String getEsMineType() {
        return esMineType;
    }

    public void setEsMineType(String esMineType) {
        this.esMineType = esMineType;
    }
}
