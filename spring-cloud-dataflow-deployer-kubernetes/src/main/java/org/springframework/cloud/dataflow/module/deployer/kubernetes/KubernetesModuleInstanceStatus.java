package org.springframework.cloud.dataflow.module.deployer.kubernetes;

import java.util.HashMap;
import java.util.Map;

import io.fabric8.kubernetes.api.model.ContainerStatus;
import io.fabric8.kubernetes.api.model.Pod;

import org.springframework.cloud.dataflow.module.DeploymentState;
import org.springframework.cloud.dataflow.module.ModuleInstanceStatus;

/**
 * Represents the status of a module.
 * @author Florian Rosenberg
 */
public class KubernetesModuleInstanceStatus implements ModuleInstanceStatus {

	private final Pod pod;
	private final String moduleId;
	private ContainerStatus containerStatus;

	public KubernetesModuleInstanceStatus(String moduleId, Pod pod) {
		this.moduleId = moduleId;
		this.pod = pod;
		// we assume one container per pod
		if (pod != null && pod.getStatus().getContainerStatuses().size() == 1) {
			this.containerStatus = pod.getStatus().getContainerStatuses().get(0);
		} else {
			this.containerStatus = null;
		}
	}

	@Override
	public String getId() {
		return String.format("%s:%s", moduleId, pod.getMetadata().getName());
	}

	@Override
	public DeploymentState getState() {
		return pod != null && containerStatus != null ? mapState() : DeploymentState.unknown;
	}

	// Maps Kubernetes phases onto Spring Cloud Dataflow states
	private DeploymentState mapState() {
		
		switch (pod.getStatus().getPhase()) {
			
			case "Pending":
				return DeploymentState.deploying;
				
			// We only report a module as running if the container is also ready to service requests.
			// We also implement the Readiness check as part of the container to ensure ready means
			// that the module is up and running and not only that the JVM has been created and the
			// Spring module is still starting up
			case "Running":
				// we assume we only have one container
				if (containerStatus.getReady())
					return DeploymentState.deployed;
				else
					return DeploymentState.deploying;

			case "Failed":
				return DeploymentState.failed;

			case "Unknown":
				return DeploymentState.unknown;

			default: 
				return DeploymentState.unknown;
		}
	}

	@Override
	public Map<String, String> getAttributes() {
		Map<String, String> result = new HashMap<>();

		result.put("pod_starttime", pod.getStatus().getStartTime());
		result.put("pod_ip", pod.getStatus().getPodIP());
		result.put("host_ip", pod.getStatus().getHostIP());
		result.put("container_restart_count", ""+ containerStatus.getRestartCount());

		return result;
	}
}
