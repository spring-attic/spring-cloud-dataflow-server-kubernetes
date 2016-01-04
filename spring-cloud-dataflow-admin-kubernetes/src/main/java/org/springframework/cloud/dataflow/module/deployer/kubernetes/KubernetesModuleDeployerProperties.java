/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.dataflow.module.deployer.kubernetes;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Florian Rosenberg
 */
@ConfigurationProperties("kubernetes")
public class KubernetesModuleDeployerProperties {

	/**
	 * Use the default spring image. Override with --kubernetes.moduleLauncherImage
	 * as a Spring Admin parameter to use a different one.
	 */
	private static final String DEFAULT_IMAGE_NAME = "springcloud/stream-module-launcher:1.0.0.M3";

	/**
	 * The Docker image containing the Spring Cloud Stream module launcher.
	 */
	private String moduleLauncherImage = DEFAULT_IMAGE_NAME;

	/**
	 * Secrets for a access a private registry to pull images.
	 */
	private String imagePullSecret;

	/**
	 * Delay in seconds when the Kubernetes liveness check of the stream module container
	 * should start checking its health status.
	 */
	// See http://kubernetes.io/v1.0/docs/user-guide/production-pods.html#liveness-and-readiness-probes-aka-health-checks}
	private int livenessProbeDelay = 180;

	/**
	 * Timeout in seconds for the Kubernetes liveness check of the stream module container.
	 * If the health check takes longer than this value to return it is assumed as 'unavailable'.
	 */
	// see http://kubernetes.io/v1.0/docs/user-guide/production-pods.html#liveness-and-readiness-probes-aka-health-checks}
	private int livenessProbeTimeout = 2;

	/**
	 * Delay in seconds when the readiness check of the stream module container
	 * should start checking if the module is fully up and running.
	 */
	 // see http://kubernetes.io/v1.0/docs/user-guide/production-pods.html#liveness-and-readiness-probes-aka-health-checks}
	private int readinessProbeDelay = 120;

	/**
	 * Timeout in seconds that the stream module container has to respond its
	 * health status during the readiness check. 
	 */
	 // see http://kubernetes.io/v1.0/docs/user-guide/production-pods.html#liveness-and-readiness-probes-aka-health-checks}
	private int readinessProbeTimeout = 2;

	/**
	 * Memory to allocate for a Pod.
	 */
	private String memory = "512Mi";

	/**
	 * CPU to allocate for a Pod (quarter of a CPU).
	 */
	private String cpu = "250m";

	/**
	 * Additional properties to be passed to the ModuleLauncher itself.
	 */
	private Map<String, String> launcherProperties = new HashMap<>();

	/**
	 * The list of additional libraries to include at runtime.
	 */
	private String includes;

	public String getIncludes() {
		return includes;
	}

	public void setIncludes(String includes) {
		this.includes = includes;
	}

	public Map<String, String> getLauncherProperties() {
		return launcherProperties;
	}

	public void setLauncherProperties(Map<String, String> launcherProperties) {
		this.launcherProperties = launcherProperties;
	}

	public String getModuleLauncherImage() {
		return moduleLauncherImage;
	}

	public void setModuleLauncherImage(String moduleLauncherImage) {
		this.moduleLauncherImage = moduleLauncherImage;
	}

	public String getImagePullSecret() {
		return imagePullSecret;
	}

	public void setImagePullSecret(String imagePullSecret) {
		this.imagePullSecret = imagePullSecret;
	}

	public int getReadinessProbeTimeout() {
		return readinessProbeTimeout;
	}

	public void setReadinessProbeTimeout(int readinessProbeTimeout) {
		this.readinessProbeTimeout = readinessProbeTimeout;
	}

	public int getReadinessProbeDelay() {
		return readinessProbeDelay;
	}

	public void setReadinessProbeDelay(int readinessProbeDelay) {
		this.readinessProbeDelay = readinessProbeDelay;
	}

	public int getLivenessProbeTimeout() {
		return livenessProbeTimeout;
	}

	public void setLivenessProbeTimeout(int livenessProbeTimeout) {
		this.livenessProbeTimeout = livenessProbeTimeout;
	}

	public int getLivenessProbeDelay() {
		return livenessProbeDelay;
	}

	public void setLivenessProbeDelay(int livenessProbeDelay) {
		this.livenessProbeDelay = livenessProbeDelay;
	}

	public String getMemory() {
		return memory;
	}

	public void setMemory(String memory) {
		this.memory = memory;
	}

	public String getCpu() {
		return cpu;
	}

	public void setCpu(String cpu) {
		this.cpu = cpu;
	}

}
