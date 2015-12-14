package org.springframework.cloud.dataflow.module.deployer.kubernetes;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import static io.fabric8.utils.Asserts.assertAssertionError;
import static io.fabric8.kubernetes.assertions.Assertions.assertThat;
import static com.jayway.awaitility.Awaitility.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.cloud.dataflow.admin.spi.kubernetes.KubernetesModuleDeployerConfiguration;
import org.springframework.cloud.dataflow.core.ArtifactCoordinates;
import org.springframework.cloud.dataflow.core.ModuleDefinition;
import org.springframework.cloud.dataflow.core.ModuleDeploymentId;
import org.springframework.cloud.dataflow.core.ModuleDeploymentRequest;
import org.springframework.cloud.dataflow.module.DeploymentState;
import org.springframework.cloud.dataflow.module.ModuleStatus;
import org.springframework.cloud.dataflow.module.deployer.ModuleDeployer;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = KubernetesModuleDeployerConfiguration.class)
public class KubernetesModuleDeployerIT {

	@Autowired
	private ModuleDeployer processModuleDeployer;

	@Autowired
	private KubernetesClient kubernetesClient;

	private ExecutorService executor;

	@Before
	public void setup() {
		executor = Executors.newSingleThreadExecutor();

		final Map<ModuleDeploymentId, ModuleStatus> status = processModuleDeployer.status();
		for (ModuleDeploymentId moduleDeploymentId : status.keySet()) {
			processModuleDeployer.undeploy(moduleDeploymentId);
		}
	}

	@After
	public void teardown() {
		executor.shutdownNow();
	}

	@Test
	@Ignore
	public void end2endDeployment1() throws InterruptedException, ExecutionException  {

		String group = "deployment-test-0";
		String name = "http";

		ModuleDefinition d = new ModuleDefinition.Builder()
				.setName("foobar")
				.setGroup(group)
				.setLabel(name)
				.setParameter("server.port", "9999")
				.build();

		ArtifactCoordinates c = new ArtifactCoordinates.Builder()
				.setGroupId("org.springframework.cloud.stream.module")
				.setArtifactId("http-source")
				.setExtension("jar")
				.setVersion("1.0.0.BUILD-SNAPSHOT")
				.build();
								
		ModuleDeploymentRequest request = new ModuleDeploymentRequest(d, c);

		final ModuleDeploymentId id = processModuleDeployer.deploy(request);
		assertNotNull(id);
		
		ModuleStatus status = processModuleDeployer.status(id);
		assertNotNull(status);
		assertSame(id, status.getModuleDeploymentId());

		// wait for "deploying" state. 
		await().atMost(15, TimeUnit.SECONDS).until(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return processModuleDeployer.status(id).getState() == DeploymentState.deploying;
			}
		});

		// make sure our requested pod is running
		await().atMost(10, TimeUnit.SECONDS).until(new Runnable() {
			@Override
			public void run() {
				assertThat(kubernetesClient).pods().filterLabel("scsm-label", "http").runningStatus().hasSize(1);
			}
		});


		processModuleDeployer.undeploy(id);


		// ensure the corresponding Kubernetes entities have been undeployed
		Map<String, String> labels = createLabelMap(group, name);
		assertTrue(kubernetesClient.services().withLabels(labels).list().getItems().isEmpty());
		assertTrue(kubernetesClient.replicationControllers().withLabels(labels).list().getItems().isEmpty());

		// make sure our pod is not running
		await().atMost(10, TimeUnit.SECONDS).until(new Runnable() {
			@Override
			public void run() {
				assertThat(kubernetesClient).pods().filterLabel("scsm-label", "http").runningStatus().hasSize(0);
			}
		});
	}

	private Map<String, String> createLabelMap(String group, String label) {
		Map<String, String> map = new HashMap<>();
		map.put(KubernetesModuleDeployer.SCSM_GROUP_KEY, group);
		map.put(KubernetesModuleDeployer.SCSM_LABEL_KEY, label);
		return map;
	}

}
