package es.upm.miw.repositories;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.upm.miw.TestConfig;
import es.upm.miw.documents.Provider;
import es.upm.miw.dtos.ProviderMinimunDto;

@TestConfig
class ProviderRepositoryIT {

	@Autowired
	private ProviderRepository providerRepository;
	private Provider inactive;
	private Provider active;

	private static boolean containsCompany(List<ProviderMinimunDto> providers, String company) {
		return providers.stream().anyMatch(item -> company.equals(item.getCompany()));
	}

	@BeforeEach
	void seedDb() {
		this.active = new Provider("active-company", "_nif_", "_address_", "_phone_", "_email_", "_note_", true);
		this.inactive = new Provider("inactive-company");
		this.inactive.setActive(false);
		this.providerRepository.save(inactive);
		this.providerRepository.save(active);
	}

	@Test
	void testFindById() {
		Optional<Provider> provider = this.providerRepository.findById(active.getId());
		assertTrue(provider.isPresent());
		assertEquals("active-company", provider.get().getCompany());
	}

	@Test
	void testFindByIdNotFound() {
		Optional<Provider> provider = this.providerRepository.findById("non-existent-id");
		assertFalse(provider.isPresent());
	}

	@Test
	void testReadAll() {
		assertTrue(this.providerRepository.findAll().size() > 1);
	}

	@Test
	void testFindAllProviders() {
		List<ProviderMinimunDto> providers = providerRepository.findAllProviders();
		assertTrue(providers.size() > 1);
	}

	@Test
	void testFindByActiveTrue() {
		List<ProviderMinimunDto> providers = providerRepository.findByActiveTrue();
		assertTrue(containsCompany(providers, "active-company"));
		assertFalse(containsCompany(providers, "inactive-company"));
	}

	@Test
	void testFindByAttributesLike() {
		List<ProviderMinimunDto> providers = providerRepository.findByAttributesLike("company", "nif", "email", "phone",
				true);
		assertTrue(containsCompany(providers, "active-company"));
	}

	@Test
	void testFindByAttributesLikeNull() {
		List<ProviderMinimunDto> activesProviders = providerRepository.findByActiveTrue();
		List<ProviderMinimunDto> nullSearchActiveProviders = providerRepository.findByAttributesLike(null, null, null,
				null, true);
		assertEquals(activesProviders.size(), nullSearchActiveProviders.size());
	}

	@AfterEach
	void delete() {
		this.providerRepository.delete(inactive);
		this.providerRepository.delete(active);
	}

}
