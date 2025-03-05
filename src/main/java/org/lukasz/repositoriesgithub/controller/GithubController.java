package org.lukasz.repositoriesgithub.controller;


import org.lukasz.repositoriesgithub.dto.RepositoryDto;
import org.lukasz.repositoriesgithub.services.GithubServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


@RestController
@RequestMapping("/repositories")
public class GithubController {
    private final GithubServices services;

    public GithubController(GithubServices services) {
        this.services = services;
    }

    @GetMapping("/{username}")
    public ResponseEntity<Flux<RepositoryDto>> getGithubRepositories(@PathVariable String username) {
        return ResponseEntity.ok(services.getUserRepositories(username));

    }
}
