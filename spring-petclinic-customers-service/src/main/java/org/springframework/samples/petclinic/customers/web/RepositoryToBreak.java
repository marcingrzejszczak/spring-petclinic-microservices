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
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.OwnerRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 * @author Maciej Szarlinski
 */
@Repository
@RequiredArgsConstructor
@Slf4j
@Timed(value = "petclinic.owner.db.account", histogram = true)
class RepositoryToBreak {

    private final Random random = new Random();

    private final AtomicInteger counter = new AtomicInteger();

    public String getAccountInformation(int ownerId) {
        try {
            Thread.sleep(9_000 + Math.abs(random.nextInt(2000)));
        }
        catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
        if (counter.incrementAndGet() % 2 == 0) {
            throw new IllegalStateException("Database instance is down!");
        }
        return "{ \"account\" : \"1283718236192379123\" }";
    }
}
