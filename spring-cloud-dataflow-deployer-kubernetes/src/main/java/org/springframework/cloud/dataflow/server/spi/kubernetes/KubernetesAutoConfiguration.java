package org.springframework.cloud.dataflow.server.spi.kubernetes;

import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudFactory;
import org.springframework.cloud.dataflow.server.config.DataFlowServerProperties;
import org.springframework.cloud.dataflow.module.deployer.ModuleDeployer;
import org.springframework.cloud.dataflow.module.deployer.kubernetes.ContainerFactory;
import org.springframework.cloud.dataflow.module.deployer.kubernetes.DefaultContainerFactory;
import org.springframework.cloud.dataflow.module.deployer.kubernetes.KubernetesModuleDeployer;
import org.springframework.cloud.dataflow.module.deployer.kubernetes.KubernetesModuleDeployerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * Spring Bean configuration for the {@link KubernetesModuleDeployer}.
 * @author Florian Rosenberg
 */
@Configuration
@EnableConfigurationProperties({KubernetesModuleDeployerProperties.class, DataFlowServerProperties.class})
public class KubernetesAutoConfiguration {
	
	@Autowired
	private KubernetesModuleDeployerProperties properties;

	@Bean
	public ModuleDeployer processModuleDeployer(KubernetesClient kubernetesClient,
												ContainerFactory containerFactory) {
		return new KubernetesModuleDeployer(kubernetesClient, containerFactory, properties);
	}

	@Bean
	public ModuleDeployer taskModuleDeployer(KubernetesClient kubernetesClient,
	                                         ContainerFactory containerFactory) {
		// Return same instance for now, to satisfy server application wiring
		return processModuleDeployer(kubernetesClient, containerFactory);
	}

	@Bean
	public KubernetesClient kubernetesClient() {
		return new DefaultKubernetesClient();
	}

	@Bean
	public ContainerFactory containerFactory(DataFlowServerProperties serverProperties) {
		return new DefaultContainerFactory(serverProperties, properties);
	}

	@Profile("cloud")
	@AutoConfigureBefore(RedisAutoConfiguration.class)
	protected static class RedisConfig {

		@Bean
		public Cloud cloud(CloudFactory cloudFactory) {
			return cloudFactory.getCloud();
		}

		@Bean
		public CloudFactory cloudFactory() {
			return new CloudFactory();
		}

		@Bean
		RedisConnectionFactory redisConnectionFactory(Cloud cloud) {
			return cloud.getSingletonServiceConnector(RedisConnectionFactory.class, null);
		}
	}
}
