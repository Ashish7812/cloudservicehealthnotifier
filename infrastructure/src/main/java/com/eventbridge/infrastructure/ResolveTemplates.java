package com.eventbridge.infrastructure;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import org.trimou.Mustache;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.locator.ClassPathTemplateLocator;
import org.trimou.exception.MustacheException;

public class ResolveTemplates {
	private static final String PRODUCTION = "PRODUCTION";
	private static final String DEVELOPMENT = "DEVELOPMENT";
	private static final String STAGE = "Stage";
	
	private static final String TEMPLATE_NAME = "infrastructure.template.json";
	private static final String STATE_MACHINE_TEMPLATE_NAME = "cloudhealth-service-state-machine.asl.json";
	private static final String CFN_TEMPLATE = "/template/"+TEMPLATE_NAME;
	private static final String STATE_MACHINE_CFN_TEMPLATE = "/template/" + STATE_MACHINE_TEMPLATE_NAME;
	
	private static final String EVENT_PROCESSOR_HANDLER = "EventProcessorHandler";
	private static final String MESSAGE_NOTIFIER_HANDLER = "MessageNotifierHandler";
	
	private static String PREFIX = "/artifacts/";
	
	public static void main(String[] args) {
		String deploymentType = args[0];
		String cfnLocation = args[1];
		PREFIX += "/";

		String resolvedTemplate = resolveTemplate(CFN_TEMPLATE, getTemplateParameters(deploymentType));
		String resolvedStateMachineTemplate = resolveTemplate(STATE_MACHINE_CFN_TEMPLATE, Collections.emptyMap());

		new File(cfnLocation + PREFIX).mkdirs();
		
		writeResolvedTemplate(resolvedTemplate, cfnLocation, TEMPLATE_NAME);
		writeResolvedTemplate(resolvedStateMachineTemplate, cfnLocation, STATE_MACHINE_TEMPLATE_NAME);
	}
	
	private static Map<String, Object> getTemplateParameters(String deploymentType) {
		Map<String, Object> params = new HashMap<>();
		params.put(STAGE, deploymentType);
		
		if (PRODUCTION.equals(deploymentType)) {
			params.putAll(getPropertiesInfo("lambda-package-info.properties"));
		}
		
		if (DEVELOPMENT.equals(deploymentType)) {
			params.putAll(getPropertiesInfo("test-lambda-package-info.properties"));
		}
		
		return params;
	}
	
	private static void writeResolvedTemplate(String content, String location, String templateName) {
		String fileName = location + PREFIX + templateName;
		
        Path path = Paths.get(fileName);

        try (BufferedWriter writer =
                     Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {

            writer.write(content);

        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	private static Map<String, String> getPropertiesInfo(String propertiesLocation) {
		Map<String, String> params = new HashMap<>();
		
		try {
            Properties lambdaProperties = new Properties();

            InputStream in = ResolveTemplates.class.getClassLoader().getResourceAsStream(propertiesLocation);
            lambdaProperties.load(in);
            
            params.put(EVENT_PROCESSOR_HANDLER, lambdaProperties.getProperty(EVENT_PROCESSOR_HANDLER));
            params.put(MESSAGE_NOTIFIER_HANDLER, lambdaProperties.getProperty(MESSAGE_NOTIFIER_HANDLER));
            
            return params;
        } catch (IOException e) {
            throw new RuntimeException("Unable to load the lambda.properties file.", e);
        }
	}
	
	public static String resolveTemplate(String templateName, Map<String, Object> attributes) {
		final int PRIORITY_ONE = 1;
		MustacheEngineBuilder engineBuilder = MustacheEngineBuilder.newBuilder()
				.addTemplateLocator(new ClassPathTemplateLocator(PRIORITY_ONE, "", null,
						ResolveTemplates.class.getClassLoader(), false));

		MustacheEngine engine = engineBuilder.build();
		try {
			Mustache mustache = engine.getMustache(templateName);

			return Optional.ofNullable(mustache)
					.map(m -> m.render(attributes))
					.orElseThrow(() -> new RuntimeException("Could not locate the given template: " + templateName));
		} catch (MustacheException me) {
			throw me;
		}
	}
}
