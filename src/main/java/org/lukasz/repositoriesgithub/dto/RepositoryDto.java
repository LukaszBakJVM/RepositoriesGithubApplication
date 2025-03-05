package org.lukasz.repositoriesgithub.dto;

import java.util.List;

public record RepositoryDto(String name, OwnerDto owner, boolean fork, List<BranchDto> branches) {
}
