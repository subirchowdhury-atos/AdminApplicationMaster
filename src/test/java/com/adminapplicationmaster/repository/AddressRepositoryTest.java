package com.adminapplicationmaster.repository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.adminapplicationmaster.domain.entity.Address;

@DataJpaTest
@ActiveProfiles("test")
class AddressRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AddressRepository addressRepository;

    private Address testAddress1;
    private Address testAddress2;

    @BeforeEach
    void setUp() {
        testAddress1 = Address.builder()
                .street("123 Main St")
                .unitNumber("Apt 4")
                .city("Springfield")
                .state("IL")
                .zip("62701")
                .county("Sangamon")
                .build();

        testAddress2 = Address.builder()
                .street("456 Oak Ave")
                .unitNumber("Suite 200")
                .city("Chicago")
                .state("IL")
                .zip("60601")
                .county("Cook")
                .build();

        addressRepository.save(testAddress1);
        addressRepository.save(testAddress2);
        entityManager.flush();
    }

    @Test
    void findAll_shouldReturnAllAddresses() {
        List<Address> addresses = addressRepository.findAll();

        assertThat(addresses).hasSize(2);
        assertThat(addresses).extracting(Address::getCity)
                .containsExactlyInAnyOrder("Springfield", "Chicago");
    }

    @Test
    void findById_shouldReturnAddress() {
        Optional<Address> found = addressRepository.findById(testAddress1.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getStreet()).isEqualTo("123 Main St");
        assertThat(found.get().getCity()).isEqualTo("Springfield");
        assertThat(found.get().getState()).isEqualTo("IL");
        assertThat(found.get().getZip()).isEqualTo("62701");
        assertThat(found.get().getCounty()).isEqualTo("Sangamon");
        assertThat(found.get().getUnitNumber()).isEqualTo("Apt 4");
    }

    @Test
    void findById_shouldReturnEmptyForNonExistent() {
        Optional<Address> found = addressRepository.findById(999L);

        assertThat(found).isEmpty();
    }

    @Test
    void save_shouldPersistNewAddress() {
        Address newAddress = Address.builder()
                .street("789 Elm St")
                .unitNumber("Unit 5")
                .city("Naperville")
                .state("IL")
                .zip("60540")
                .county("DuPage")
                .build();

        Address saved = addressRepository.save(newAddress);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getStreet()).isEqualTo("789 Elm St");
        assertThat(saved.getCity()).isEqualTo("Naperville");
        
        Optional<Address> found = addressRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getCounty()).isEqualTo("DuPage");
    }

    @Test
    void save_shouldUpdateExistingAddress() {
        testAddress1.setStreet("123 Main Street");
        testAddress1.setUnitNumber("Apt 5");

        Address updated = addressRepository.save(testAddress1);

        assertThat(updated.getStreet()).isEqualTo("123 Main Street");
        assertThat(updated.getUnitNumber()).isEqualTo("Apt 5");
        
        Optional<Address> found = addressRepository.findById(testAddress1.getId());
        assertThat(found.get().getStreet()).isEqualTo("123 Main Street");
        assertThat(found.get().getUnitNumber()).isEqualTo("Apt 5");
    }

    @Test
    void save_shouldHandleNullUnitNumber() {
        Address addressWithoutUnit = Address.builder()
                .street("100 Park Place")
                .city("Aurora")
                .state("IL")
                .zip("60505")
                .county("Kane")
                .build();

        Address saved = addressRepository.save(addressWithoutUnit);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUnitNumber()).isNull();
        
        Optional<Address> found = addressRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getUnitNumber()).isNull();
    }

    @Test
    void delete_shouldRemoveAddress() {
        Long id = testAddress1.getId();
        
        addressRepository.delete(testAddress1);
        entityManager.flush();

        Optional<Address> found = addressRepository.findById(id);
        assertThat(found).isEmpty();
    }

    @Test
    void deleteById_shouldRemoveAddress() {
        Long id = testAddress1.getId();
        
        addressRepository.deleteById(id);
        entityManager.flush();

        Optional<Address> found = addressRepository.findById(id);
        assertThat(found).isEmpty();
    }

    @Test
    void count_shouldReturnCorrectNumber() {
        long count = addressRepository.count();

        assertThat(count).isEqualTo(2);
    }

    @Test
    void existsById_shouldReturnTrueForExistingAddress() {
        boolean exists = addressRepository.existsById(testAddress1.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void existsById_shouldReturnFalseForNonExistentAddress() {
        boolean exists = addressRepository.existsById(999L);

        assertThat(exists).isFalse();
    }

    @Test
    void save_shouldPersistAllFields() {
        Address completeAddress = Address.builder()
                .street("999 Complete St")
                .unitNumber("Floor 3")
                .city("Rockford")
                .state("IL")
                .zip("61101")
                .county("Winnebago")
                .build();

        Address saved = addressRepository.save(completeAddress);

        Optional<Address> found = addressRepository.findById(saved.getId());
        assertThat(found).isPresent();
        
        Address foundAddress = found.get();
        assertThat(foundAddress.getStreet()).isEqualTo("999 Complete St");
        assertThat(foundAddress.getUnitNumber()).isEqualTo("Floor 3");
        assertThat(foundAddress.getCity()).isEqualTo("Rockford");
        assertThat(foundAddress.getState()).isEqualTo("IL");
        assertThat(foundAddress.getZip()).isEqualTo("61101");
        assertThat(foundAddress.getCounty()).isEqualTo("Winnebago");
    }

    @Test
    void save_shouldHandleMultipleAddressesInSameCity() {
        Address address3 = Address.builder()
                .street("111 First St")
                .city("Springfield")
                .state("IL")
                .zip("62702")
                .county("Sangamon")
                .build();

        Address address4 = Address.builder()
                .street("222 Second St")
                .city("Springfield")
                .state("IL")
                .zip("62703")
                .county("Sangamon")
                .build();

        addressRepository.save(address3);
        addressRepository.save(address4);
        entityManager.flush();

        List<Address> allAddresses = addressRepository.findAll();
        assertThat(allAddresses).hasSize(4);
        
        long springfieldCount = allAddresses.stream()
                .filter(a -> "Springfield".equals(a.getCity()))
                .count();
        assertThat(springfieldCount).isEqualTo(3); // 1 from setup + 2 new
    }

    @Test
    void save_shouldUpdateSpecificFields() {
        Address original = addressRepository.findById(testAddress1.getId()).get();
        String originalStreet = original.getStreet();
        String originalCity = original.getCity();

        original.setZip("62702");
        original.setUnitNumber("Apt 10");

        Address updated = addressRepository.save(original);

        assertThat(updated.getStreet()).isEqualTo(originalStreet);
        assertThat(updated.getCity()).isEqualTo(originalCity);
        assertThat(updated.getZip()).isEqualTo("62702");
        assertThat(updated.getUnitNumber()).isEqualTo("Apt 10");
    }
}
