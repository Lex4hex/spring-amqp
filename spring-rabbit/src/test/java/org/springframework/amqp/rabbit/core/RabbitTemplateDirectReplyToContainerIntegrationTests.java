/*
 * Copyright 2016-2018 the original author or authors.
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

package org.springframework.amqp.rabbit.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.Test;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.utils.test.TestUtils;

/**
 * @author Gary Russell
 * @since 2.0
 *
 */
public class RabbitTemplateDirectReplyToContainerIntegrationTests extends RabbitTemplateIntegrationTests {

	@Override
	protected RabbitTemplate createSendAndReceiveRabbitTemplate(ConnectionFactory connectionFactory) {
		RabbitTemplate template = super.createSendAndReceiveRabbitTemplate(connectionFactory);
		template.setUseDirectReplyToContainer(true);
		template.setBeanName(this.testName.getMethodName() + "SendReceiveRabbitTemplate");
		return template;
	}

	@SuppressWarnings("unchecked")
	@Test
	public void channelReleasedOnTimeout() {
		final CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
		RabbitTemplate template = createSendAndReceiveRabbitTemplate(connectionFactory);
		template.setReplyTimeout(1);
		Object reply = template.convertSendAndReceive(ROUTE, "foo");
		assertThat(reply).isNull();
		Object container = TestUtils.getPropertyValue(template, "directReplyToContainers", Map.class)
				.get(template.isUsePublisherConnection()
						? connectionFactory.getPublisherConnectionFactory()
						: connectionFactory);
		assertThat(TestUtils.getPropertyValue(container, "inUseConsumerChannels", Map.class)).hasSize(0);
	}

}
