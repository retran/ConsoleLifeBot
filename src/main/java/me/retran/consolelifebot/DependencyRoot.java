package me.retran.consolelifebot;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.cfg4j.provider.ConfigurationProvider;
import org.cfg4j.provider.ConfigurationProviderBuilder;
import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.classpath.ClasspathConfigurationSource;

import java.nio.file.Paths;
import java.util.Arrays;

public class DependencyRoot extends AbstractModule {
    @Override
    protected void configure() {
    }

    @Provides
    protected Configuration provideConfiguration() {
        ConfigurationSource configurationSource = new ClasspathConfigurationSource(
                () -> Arrays.asList(Paths.get("application.properties")));
        ConfigurationProvider configurationProvider = new ConfigurationProviderBuilder()
                .withConfigurationSource(configurationSource)
                .build();
        return configurationProvider.bind("consolelifebot", Configuration.class);
    }
}
