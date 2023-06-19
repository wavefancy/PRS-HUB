package com.prs.hub.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 腾讯cos相关配置
 * @author fanshupeng
 * @create 2023/6/15 11:18
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "cos")
public class COSConfig {

    /**
     * 访问域名
     */
    private String baseUrl;
    /**
     * 密钥id
     */
    private String secretId;
    /**
     * 密钥
     */
    private String secretKey;
    /**
     * 所属地域
     */
    private String regionName;
    /**
     * 存储桶名称
     */
    private String bucketName;
    /**
     * 前缀
     */
    private String folderPrefix;
}
