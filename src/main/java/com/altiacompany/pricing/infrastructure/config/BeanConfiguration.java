package com.altiacompany.pricing.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.altiacompany.pricing.application.port.input.GetPriceUseCase;
import com.altiacompany.pricing.application.port.output.PriceRepository;
import com.altiacompany.pricing.application.service.GetPriceService;
import com.altiacompany.pricing.domain.service.PriceSelector;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class BeanConfiguration {

    @Bean
    public PriceSelector priceSelector() {
        return new PriceSelector();
    }

    @Bean
    public GetPriceUseCase getPriceUseCase(PriceRepository priceRepository,
            PriceSelector priceSelector) {
        return new GetPriceService(priceRepository, priceSelector);
    }

    @Bean
    public OpenAPI customOpenAPI(
            @Value("${info.app.name:}") String name,
            @Value("${info.app.description:}") String description,
            @Value("${info.app.version:}") String version) {
        // @formatter:off
        return new OpenAPI()
                .info(new Info()
                        .title(name)
                        .description(description)
                        .version(version));
        // @formatter:on
    }
}
