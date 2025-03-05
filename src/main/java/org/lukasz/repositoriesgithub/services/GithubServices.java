package org.lukasz.repositoriesgithub.services;

import org.lukasz.repositoriesgithub.dto.Mapper;
import org.lukasz.repositoriesgithub.dto.RepositoryDto;
import org.lukasz.repositoriesgithub.exceptions.UserNotFoundException;
import org.lukasz.repositoriesgithub.model.Branch;
import org.lukasz.repositoriesgithub.model.Repository;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class GithubServices {
    private final WebClient webClient;
    private final Mapper mapper;


    public GithubServices(WebClient.Builder webBuilder, Mapper mapper) {
        this.webClient = webBuilder.build();

        this.mapper = mapper;
    }


    public Flux<RepositoryDto> getUserRepositories(String username) {
        return webClient.get().uri("/users/{username}/repos", username).header("Accept", "application/json").retrieve().onStatus(HttpStatusCode::is4xxClientError, response -> {
            if (response.statusCode() == HttpStatus.NOT_FOUND) {
                return Mono.error(new UserNotFoundException(String.format("User %s not found", username)));
            }
            return Mono.error(new ResponseStatusException(response.statusCode()));
        }).bodyToFlux(Repository.class).filter(repository -> !repository.fork()).flatMap(repository -> this.getBranchesForRepository(username, repository.name()).map(branches -> mapper.fromRepositoryToDto(new Repository(repository.name(), repository.owner(), repository.fork(), branches

        ))));

    }


    private Mono<List<Branch>> getBranchesForRepository(String username, String repositoryName) {
        return webClient.get().uri("/repos/{username}/{repositoryName}/branches", username, repositoryName).retrieve().bodyToFlux(Branch.class).collectList();

    }
}
