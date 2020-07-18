/*
 * Copyright 2019 Ingo Griebsch
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package com.github.ingogriebsch.sample.spring.data.jpa.testcontainers.utilization;

import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

import java.util.List;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

@AutoConfigureTestDatabase(replace = NONE)
@DataJpaTest
@RunWith(SpringRunner.class)
public class PersonRepositoryTests {

    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void findOneShouldReturnMatchingEntityIfAvailable() throws Exception {
        Person person = new Person(randomId(), "Ingo", 44);

        entityManager.persistAndFlush(person);
        Person found = personRepository.findOne(person.getId());

        Assertions.assertThat(found).isNotNull();
        Assertions.assertThat(found).isEqualTo(person);
    }

    @Test
    public void findOneShouldReturnNullIfNotAvailable() throws Exception {
        Person found = personRepository.findOne(randomId());
        Assertions.assertThat(found).isNull();
    }

    @Test
    public void insertShouldPersistEntity() throws Exception {
        Person person = new Person(randomId(), "Ingo", 44);

        Assertions.assertThat(personRepository.findOne(person.getId())).isNull();

        personRepository.save(person);
        entityManager.flush();

        Person found = personRepository.findOne(person.getId());
        Assertions.assertThat(found).isNotNull();
        Assertions.assertThat(found).isEqualTo(person);
    }

    @Test
    // Start transaction
    public void findByNameShouldReturnMatchingEntitiesIfAvailable() throws Exception {
        List<Person> persons = Lists.newArrayList(new Person(randomId(), "Ingo", 44), new Person(randomId(), "Jan", 32),
            new Person(randomId(), "Stephan", 34));
        persons.stream().forEach(p -> entityManager.persistAndFlush(p));
        Person person = persons.get(RandomUtils.nextInt(0, persons.size()));

        Iterable<Person> found = personRepository.findByName(person.getName());
        Assertions.assertThat(found).isNotNull().hasSize(1);
        Assertions.assertThat(found.iterator().next()).isEqualTo(person);
    }
    // Rollback transaction

    private static String randomId() {
        return RandomStringUtils.randomAlphanumeric(8);
    }

}
