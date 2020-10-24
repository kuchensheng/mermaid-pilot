package org.mermaid.k8s.test;

import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.mermaid.k8s.config.K8sClientConfig;

public class K8sTest {
    public static void main(String[] args) {
        KubernetesClient client = new K8sClientConfig().mockK8sClient();
        PodList list = client.pods().inAnyNamespace().list();
        list.getItems().forEach(pod ->{

            System.out.println(String.format("%s\t%s\t%s\t%s",pod.getMetadata().getName(),pod.getMetadata().getNamespace(),pod.getStatus().getPhase(),pod.getStatus()));
        });
    }
}
