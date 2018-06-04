/*
 * Copyright 2016 the original author or authors.
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

package org.springframework.cloud.dataflow.server.kubernetes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.session.SessionAutoConfiguration;
import org.springframework.cloud.dataflow.server.EnableDataFlowServer;

/**
 * Bootstrap class for the Kubernetes Spring Cloud Data Flow Server.
 *
 * @author Thomas Risberg
 */
@SpringBootApplication(exclude = SessionAutoConfiguration.class)
@EnableDataFlowServer
public class KubernetesDataFlowServer {

	public static void main(String[] args) {
		SpringApplication.run(KubernetesDataFlowServer.class, args);
	}

}
