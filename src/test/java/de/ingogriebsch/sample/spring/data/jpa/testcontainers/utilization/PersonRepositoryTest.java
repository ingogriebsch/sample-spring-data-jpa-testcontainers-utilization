/*-
 * Copyright 2018-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.ingogriebsch.sample.spring.data.jpa.testcontainers.utilization;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

import java.util.List;
import java.util.Optional;

import de.ingogriebsch.sample.spring.data.jpa.testcontainers.utilization.Person;
import de.ingogriebsch.sample.spring.data.jpa.testcontainers.utilization.PersonRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = NONE)
@DataJpaTest
class PersonRepositoryTest {

    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    // Start transaction
    void findById_should_return_matching_entity_if_available() throws Exception {
        // given
        Person person = new Person("Ingo", 44);
        entityManager.persistAndFlush(person);

        // when
        Optional<Person> found = personRepository.findById(person.getId());

        // then
        assertThat(found).isNotNull().get().isEqualTo(person);
    }
    // Rollback transaction

    @Test
    // Start transaction
    void findById_should_return_null_if_not_available() throws Exception {
        // given... then... when
        assertThat(personRepository.findById(1L)).isNotNull().isEmpty();
    }
    // Rollback transaction

    @Test
    // Start transaction
    void insert_should_persist_entity() throws Exception {
        // given
        Person person = new Person("Ingo", 44);

        // when
        personRepository.save(person);
        entityManager.flush();

        // then
        Optional<Person> found = personRepository.findById(person.getId());
        assertThat(found).isNotNull().get().isEqualTo(person);
    }
    // Rollback transaction

    @Test
    // Start transaction
    void findByName_should_return_matching_entities_if_available() throws Exception {
        // given
        List<Person> persons = newArrayList( //
            new Person("Ingo", 44), //
            new Person("Jan", 32), //
            new Person("Stephan", 34) //
        );
        persons.forEach(p -> entityManager.persistAndFlush(p));

        // when
        Person person = persons.get(nextInt(0, persons.size()));
        Iterable<Person> found = personRepository.findByName(person.getName());

        // then
        assertThat(found).isNotNull().hasSize(1);
        assertThat(found.iterator().next()).isEqualTo(person);
    }
    // Rollback transaction

}
