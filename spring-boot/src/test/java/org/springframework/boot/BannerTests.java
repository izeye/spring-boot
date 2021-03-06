/*
 * Copyright 2012-2016 the original author or authors.
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

package org.springframework.boot;

import java.io.PrintStream;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.boot.Banner.Mode;
import org.springframework.boot.testutil.InternalOutputCapture;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link Banner} and its usage by {@link SpringApplication}.
 *
 * @author Phillip Webb
 * @author Michael Stummvoll
 * @author Michael Simons
 */
public class BannerTests {

	private ConfigurableApplicationContext context;

	@After
	public void cleanUp() {
		if (this.context != null) {
			this.context.close();
		}
	}

	@Rule
	public InternalOutputCapture out = new InternalOutputCapture();

	@Test
	public void testDefaultBanner() throws Exception {
		SpringApplication application = new SpringApplication(Config.class);
		application.setWebEnvironment(false);
		this.context = application.run();
		assertThat(this.out.toString()).contains(":: Spring Boot ::");
	}

	@Test
	public void testDefaultBannerInLog() throws Exception {
		SpringApplication application = new SpringApplication(Config.class);
		application.setWebEnvironment(false);
		this.context = application.run();
		assertThat(this.out.toString()).contains(":: Spring Boot ::");
	}

	@Test
	public void testCustomBanner() throws Exception {
		SpringApplication application = new SpringApplication(Config.class);
		application.setWebEnvironment(false);
		application.setBanner(new DummyBanner());
		this.context = application.run();
		assertThat(this.out.toString()).contains("My Banner");
	}

	@Test
	public void testBannerInContext() throws Exception {
		SpringApplication application = new SpringApplication(Config.class);
		application.setWebEnvironment(false);
		this.context = application.run();
		assertThat(this.context.containsBean("springBootBanner")).isTrue();
	}

	@Test
	public void testCustomBannerInContext() throws Exception {
		SpringApplication application = new SpringApplication(Config.class);
		application.setWebEnvironment(false);
		final DummyBanner dummyBanner = new DummyBanner();
		application.setBanner(dummyBanner);
		this.context = application.run();
		assertThat(this.context.getBean("springBootBanner")).isEqualTo(dummyBanner);
	}

	@Test
	public void testDisableBannerInContext() throws Exception {
		SpringApplication application = new SpringApplication(Config.class);
		application.setBannerMode(Mode.OFF);
		application.setWebEnvironment(false);
		this.context = application.run();
		assertThat(this.context.containsBean("springBootBanner")).isFalse();
	}

	@Test
	public void testDeprecatePrintBanner() throws Exception {
		SpringApplication application = new SpringApplication(Config.class) {

			@Override
			protected void printBanner(Environment environment) {
				System.out.println("I printed a deprecated banner");
			};

		};
		application.setWebEnvironment(false);
		this.context = application.run();
		assertThat(this.out.toString()).contains("I printed a deprecated banner");
		assertThat(this.context.containsBean("springBootBanner")).isFalse();
	}

	static class DummyBanner implements Banner {

		@Override
		public void printBanner(Environment environment, Class<?> sourceClass,
				PrintStream out) {
			out.println("My Banner");
		}

	}

	@Configuration
	public static class Config {

	}

}
