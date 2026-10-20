package br.com.fiap.curie.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI curieOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CURIE API")
                        .version("1.0.0")
                        .description("""
                                API RESTful do **Projeto CURIE** — *Um laboratorio nuclear em cada escola por R$ 300*.
                                
                                Gerencia escolas, usuarios (alunos e professores), kits de medicao (ESP32 + Geiger-Muller + NFC)
                                e as medicoes de radiacao que alimentam o mapa colaborativo nacional.
                                
                                Challenge FIAP 2026 · CTMSP / Marinha do Brasil · Grupo CURIE · Turma 2TDSPB
                                """)
                        .contact(new Contact().name("Grupo CURIE — Matheus Carneiro Maciel (RM 567753), Murilo Marques (RM 568224), Paulo Henrique da Silva Kian (RM 563343)")))
                .tags(List.of(
                        new Tag().name("Escolas").description("Instituicoes que adotam o kit CURIE"),
                        new Tag().name("Usuarios").description("Alunos e professores, com pontuacao e ranking"),
                        new Tag().name("Kits").description("Dispositivos fisicos de medicao"),
                        new Tag().name("Medicoes").description("Leituras de radiacao e base do mapa colaborativo")));
    }
}
