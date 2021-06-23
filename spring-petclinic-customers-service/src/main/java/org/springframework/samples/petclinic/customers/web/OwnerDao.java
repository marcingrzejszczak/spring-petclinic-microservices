/*
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.samples.petclinic.customers.web;

import java.util.List;
import java.util.Optional;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cloud.sleuth.annotation.NewSpan;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.OwnerRepository;
import org.springframework.stereotype.Service;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 * @author Maciej Szarlinski
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Timed("petclinic.owner.dao")
class OwnerDao {

    private final OwnerRepository ownerRepository;

    private final RepositoryToBreak repositoryToBreak;

    /**
     * Create Owner
     */
    public Owner createOwner(Owner owner) {
        log.info("[DB] Creating owner {}", owner);
        return ownerRepository.save(owner);
    }

    public Optional<Owner> findOwner(int ownerId) {
        log.info("[DB] Finding owner via id {}", ownerId);
        return ownerRepository.findById(ownerId);
    }

    @NewSpan
    public String getAccountInformation(int ownerId) {
        log.info("[DB] Getting account information for owner {}", ownerId);
        return this.repositoryToBreak.getAccountInformation(ownerId);
    }

    public List<Owner> findAll() {
        log.info("[DB] Finding all owners");
        return ownerRepository.findAll();
    }

    public void saveOwner(Owner ownerModel) {
        log.info("[DB] Saving owner {}", ownerModel);
        ownerRepository.save(ownerModel);
    }
}
