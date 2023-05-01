package net.doemges.kogniswarm.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.convert.ApplicationConversionService
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.support.DefaultConversionService
import org.springframework.format.support.FormattingConversionService
import org.springframework.shell.boot.CompleterAutoConfiguration.CompleterAdapter
import org.springframework.shell.config.ShellConversionServiceSupplier

@Configuration
class ShellConfig {
    @Bean
    @ConditionalOnMissingBean
    fun shellConversionServiceSupplier(applicationContext: ApplicationContext?): ShellConversionServiceSupplier =
        ShellConversionServiceSupplier {
            FormattingConversionService().also { service ->
                DefaultConversionService.addDefaultConverters(service)
                DefaultConversionService.addCollectionConverters(service)
                ApplicationConversionService.addBeans(service, applicationContext)
            }
        }

    @Bean
    fun completer(): CompleterAdapter = CompleterAdapter()
}