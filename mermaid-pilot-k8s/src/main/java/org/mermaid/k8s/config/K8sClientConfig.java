package org.mermaid.k8s.config;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.mermaid.common.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class K8sClientConfig {
    private static final Logger logger = LoggerFactory.getLogger(K8sClientConfig.class);
    @Value("${k8s.apiVersion:v1}")
    private String apiVersion;

    @Value("${k8s.master.url:https://10.30.30.25:6443}")
    private String masterUrl;

    @Value("${k8s.token:}")
    private String token;

    @Value("${k8s.ca:}")
    private String caCert;

    @Value("${k8s.config.mock:false}")
    private boolean mock;

    @Bean
    public KubernetesClient kubernetesClient() {
        if (mock) {
            return mockK8sClient();
        }
        token = getToken();
        caCert = getcaCert();
        Config config = new ConfigBuilder().withMasterUrl(masterUrl)
                .withApiVersion(apiVersion)
                .withCaCertData(caCert)
                .withOauthToken(token)
                .build();
        return new DefaultKubernetesClient(config);

    }

    private String getToken() {
        if (mock) {
            return "eyJhbGciOiJSUzI1NiIsImtpZCI6IiJ9.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJkZWZhdWx0Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZWNyZXQubmFtZSI6ImlzeXNjb3JlLWFwaS10b2tlbi12Y3o3bCIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50Lm5hbWUiOiJpc3lzY29yZS1hcGkiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC51aWQiOiIxMjFiYmRjYS03NGI1LTRjY2EtYmZhZS1jZTg2ODA0ZWM1MDciLCJzdWIiOiJzeXN0ZW06c2VydmljZWFjY291bnQ6ZGVmYXVsdDppc3lzY29yZS1hcGkifQ.hQLk1DCED8CFYFNxncX4nhYljyPKqpVIKKcuJdTvNGOFx4A9hrILkGgNYLmQDh9dXvZweo401fl7-q5ju4-HMKFhL_531YnCmEvf1VgrbS-OK-GZ9PYJsbH_cssgKlkyEUjSVSMZsSrJvPpytZXhW9RDv_D7Ui8GJVnjbn9Mmt-3-n6OO0Ejf5HDNGOmtJgUa3kd2WdZxjOPsXaUTnXAMZX2oGTkMoVQ8BucrA-yUUFKVE5lqDD2X8mY4yET4CFrfb1LF_uGnPVtjz5vcDGGYXgjtb_4Pr_HSnRTy3o-usxB7lwxhVSjfYsf9Fh8Uu5ZJGoW7janTHIrYb6VQ9oZzg";
        } else if (!StringUtils.hasText(token)){
            ClassPathResource classPathResource = new ClassPathResource("token.tk");
            if (null != classPathResource) {
                try {
                    return FileUtils.getFileContent(classPathResource.getPath());
                } catch (IOException e) {
                    logger.warn("获取token文件内容错误",e);
                    return "";
                }
            }
        }
        return token;
    }

    private String getcaCert() {
        if (mock) {
            return "LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUN5RENDQWJDZ0F3SUJBZ0lCQURBTkJna3Foa2lHOXcwQkFRc0ZBREFWTVJNd0VRWURWUVFERXdwcmRXSmwKY201bGRHVnpNQjRYRFRFNU1UQXlPREEzTlRNek9Gb1hEVEk1TVRBeU5UQTNOVE16T0Zvd0ZURVRNQkVHQTFVRQpBeE1LYTNWaVpYSnVaWFJsY3pDQ0FTSXdEUVlKS29aSWh2Y05BUUVCQlFBRGdnRVBBRENDQVFvQ2dnRUJBTi9jCkdZbm9vQVhsNm5CZG52bjVTQjVxZXArWWNHRXRXaFM0YlEwZ0J3QStGVEcvdW0yZnVSSG1qU1pNcHpiVEFINVoKMXMvUUY3NFNLN3VaMWRxbjgyRUtkbWFPRlcyZFNiTnRWcGMvV3BIN1NkTmFSRHlJbmIzTGtaTGNFMUo4bWN0TwoySUNGTFhKSjh5OUFaTjc3a2lTT01VeWtaR1pxMCsxNmYyT1lxTnVZeDJjamg0YXdKeWROall3NWR4T1hPK2tVCnJFc1I1MzV0ekVYekpBa1gzUVhsZXNuN3pWZmFBWDJrbGVSVk9oU0Y0a0FRTWtldHlrNWpKMnZUL1UwTVU1NUYKRnZYd1ZQRmI1MzMzNkVCa1RtalM0ZE9rWkU5MjBvN3JUMUFBYTZMRlhRYlpkcUVPSmtIQUdaeFAzZGVFYkUxUQptR0JkTThzTG9jcHp2Q0wrYVRNQ0F3RUFBYU1qTUNFd0RnWURWUjBQQVFIL0JBUURBZ0trTUE4R0ExVWRFd0VCCi93UUZNQU1CQWY4d0RRWUpLb1pJaHZjTkFRRUxCUUFEZ2dFQkFIbi90UGNXamMyTE5ya0E2NE80ZWlFcnNJenAKYko0UnlUeVBRTytsbXhkTWRGR0doT000TzhZUGFlVHVqNlNJamRONFVBQTFZanA4MXdkV01rY2RuV056Z2NmRwovNGJRbUFMSytHb0pCL1FIZ2UvS1FzTlBWRjZ6QTA1RndHTmIySGpMT1VhcHBBQ2dvZFU5NHhtM2FpY2ZVTWJhCklTNnF6WmpDRkRDOWlhN2M2azJlcnNJcFhiak9OSUg3aFRORTJQVFpwK2RSVUZDWXI1YXBTZmJ4bkRYL0ppck8KRjBVRWh0YWgwckgxL1J5dnc2SUJ1Wmo2b0RtbExmRld5Z1VNejdoSkZ0UXdxVU1TR2tuUmhXOVBqZ1RtNFBYWQo5NlpVS09uQzRnYk9hdVVqOUtJVUpzK3lCUExDTGJPL3JQOTlaNHBROVZaM3JxWnFTRjcwTGJIcEtoST0KLS0tLS1FTkQgQ0VSVElGSUNBVEUtLS0tLQo=";
        } else if (!StringUtils.hasText(caCert)) {
            try {
                return FileUtils.getFileContent(new ClassPathResource("ca.crt").getPath());
            } catch (IOException e) {
                logger.warn("获取ca.crt文件内容异常",e);
                return "";
            }
        }
        return caCert;
    }

    public KubernetesClient mockK8sClient() {
        this.mock = true;
        Config config = new ConfigBuilder().withMasterUrl("https://10.30.30.25:6443")
                .withCaCertData(getcaCert())
                .withOauthToken(getToken())
                .build();
        return new DefaultKubernetesClient(config);
    }

}
