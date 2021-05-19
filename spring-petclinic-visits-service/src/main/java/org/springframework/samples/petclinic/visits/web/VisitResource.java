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
package org.springframework.samples.petclinic.visits.web;

import java.util.List;
import java.util.function.Supplier;

import javax.validation.Valid;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.visits.model.Visit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 * @author Maciej Szarlinski
 */
@AllArgsConstructor
@RestController
@Slf4j
@Timed("petclinic.visit")
class VisitResource {

    private final VisitDao visitDao;

    private final CustomerClient customerClient;

    @PostMapping("owners/*/pets/{petId}/visits")
    @ResponseStatus(HttpStatus.CREATED)
   public Visit create(
        @Valid @RequestBody Visit visit,
        @PathVariable("petId") int petId) {

        visit.setPetId(petId);
        log.info("Saving visit {}", visit);
        return visitDao.create(visit, petId);
    }

    @GetMapping("owners/*/pets/{petId}/visits")
   public List<Visit> visits(@PathVariable("petId") int petId) {
        log.info("Retrieving all visits for id {}", petId);
        customerClient.callOwnerForAccount();
        return visitDao.visits(petId);
    }

    @GetMapping("pets/visits")
   public Visits visitsMultiGet(@RequestParam("petId") List<Integer> petIds) {
        log.info("Retrieving all visits for ids {}", petIds);
        customerClient.callOwnerForAccount();
        final List<Visit> byPetIdIn =  visitDao.visitsMultiGet(petIds);
        return new Visits(byPetIdIn);
    }

    @Value
    static class Visits {
        List<Visit> items;
    }
}
