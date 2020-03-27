package com.bizzan.bitrade.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("aliyun")
public class AliyunConfig {
    private String accessKeyId;
    private String accessKeySecret;
    private String ossSchema = "http";
    private String ossEndpoint;
    private String ossBucketName;

    public String toUrl(String key){
        return String.format("%s://%s.%s/%s", ossSchema,ossBucketName,ossEndpoint,key);
    }

    public String getOssSchema() {
        return ossSchema;
    }

    public void setOssSchema(String ossSchema) {
        this.ossSchema = ossSchema;
    }

    public String getOssEndpoint() {
        return ossEndpoint;
    }

    public void setOssEndpoint(String ossEndpoint) {
        this.ossEndpoint = ossEndpoint;
    }

    public String getOssBucketName() {
        return ossBucketName;
    }

    public void setOssBucketName(String ossBucketName) {
        this.ossBucketName = ossBucketName;
    }

    public String getAccessKeyId() {
		return accessKeyId;
	}

	public void setAccessKeyId(String accessKeyId) {
		this.accessKeyId = accessKeyId;
	}

	public String getAccessKeySecret() {
		return accessKeySecret;
	}

	public void setAccessKeySecret(String accessKeySecret) {
		this.accessKeySecret = accessKeySecret;
	}

}
