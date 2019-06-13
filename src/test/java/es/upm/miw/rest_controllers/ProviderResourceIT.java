package es.upm.miw.rest_controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import es.upm.miw.dtos.ProviderDto;
import es.upm.miw.dtos.ProviderMinimunDto;
import es.upm.miw.dtos.ProviderSearchInputDto;

@ApiTestConfig
public class ProviderResourceIT {

	@Autowired
	private RestService restService;
	private ProviderMinimunDto existentProvider;

	@BeforeEach
	void before() {
		List<ProviderMinimunDto> providers = Arrays
				.asList(this.restService.loginAdmin().restBuilder(new RestBuilder<ProviderMinimunDto[]>())
						.clazz(ProviderMinimunDto[].class).path(ProviderResource.PROVIDERS).get().build());
		this.existentProvider = providers.get(0);
	}

	@Test
	void testReadNotFound() {
		HttpClientErrorException exception = assertThrows(HttpClientErrorException.class,
				() -> this.restService.loginAdmin().restBuilder(new RestBuilder<ProviderMinimunDto[]>())
						.clazz(ProviderMinimunDto[].class).path(ProviderResource.PROVIDERS).path("/non-existent-id")
						.get().build());
		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
	}

	@Test
	void testRead() {
		ProviderDto providerDto = this.restService.loginAdmin().restBuilder(new RestBuilder<ProviderDto>())
				.clazz(ProviderDto.class).path(ProviderResource.PROVIDERS).path("/" + existentProvider.getId()).get()
				.build();
	}

	@Test
	void testReadAll() {
		List<ProviderMinimunDto> providers = Arrays
				.asList(this.restService.loginAdmin().restBuilder(new RestBuilder<ProviderMinimunDto[]>())
						.clazz(ProviderMinimunDto[].class).path(ProviderResource.PROVIDERS).get().build());
		assertTrue(providers.size() > 1);
	}

	private RestBuilder<ProviderMinimunDto[]> restActiveService() {
		return this.restService.loginAdmin().restBuilder(new RestBuilder<ProviderMinimunDto[]>())
				.clazz(ProviderMinimunDto[].class).path(ProviderResource.PROVIDERS).path(ProviderResource.ACTIVES)
				.get();
	}

	@Test
	void testRealAllActives() {
		List<ProviderMinimunDto> actives = Arrays.asList(restActiveService().build());
		assertTrue(actives.size() > 1);
	}

	private RestBuilder<ProviderDto> restCreateService(ProviderDto providerDto) {
		return this.restService.loginAdmin().restBuilder(new RestBuilder<ProviderDto>()).clazz(ProviderDto.class)
				.path(ProviderResource.PROVIDERS).body(providerDto).post();
	}

	@Test
	void testCreate() {
		restCreateService(new ProviderDto("new-provider")).build();
	}

	@Test
	void testCreateConflict() {
		ProviderDto providerDto = new ProviderDto(existentProvider.getCompany());
		HttpClientErrorException exception = assertThrows(HttpClientErrorException.class,
				() -> restCreateService(providerDto).build());
		assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
	}

	@Test
	void testCreateNullCompany() {
		HttpClientErrorException exception = assertThrows(HttpClientErrorException.class,
				() -> restCreateService(new ProviderDto()).build());
		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
	}

	private RestBuilder<ProviderDto> restUpdateBuilder(String id, ProviderDto providerDto) {
		return this.restService.loginAdmin().restBuilder(new RestBuilder<ProviderDto>()).clazz(ProviderDto.class)
				.path(ProviderResource.PROVIDERS).path("/" + id).body(providerDto).put();
	}

	@Test
	void testUpdate() {
		ProviderDto providerDto = new ProviderDto(existentProvider.getCompany());
		providerDto.setId(existentProvider.getId());
		providerDto.setNif("updated-nif");
		ProviderDto result = restUpdateBuilder(existentProvider.getId(), providerDto).build();
		assertEquals(providerDto.getNif(), result.getNif());
	}

	@Test
	void testDelete() {
		this.restService.loginAdmin().restBuilder(new RestBuilder<ProviderDto>())
				.clazz(ProviderDto.class).path(ProviderResource.PROVIDERS).path("/" + existentProvider.getId())
				.delete().build();
		HttpClientErrorException exception = assertThrows(HttpClientErrorException.class,
				() -> this.restService.loginAdmin().restBuilder(new RestBuilder<ProviderDto>())
						.clazz(ProviderDto.class).path(ProviderResource.PROVIDERS)
						.path("/" + existentProvider.getId()).delete().build());
		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
	}

	@Test
	void testUpdateNullCompany() {
		ProviderDto providerDto = new ProviderDto();
		providerDto.setId(existentProvider.getId());
		HttpClientErrorException exception = assertThrows(HttpClientErrorException.class,
				() -> restUpdateBuilder(existentProvider.getId(), providerDto).build());
		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
	}

	// @Test //Test conflictivo y rígido
	void testFindByAttributesLike() {
		ProviderSearchInputDto providerSearchInputDto = new ProviderSearchInputDto(true);
		List<ProviderMinimunDto> actives = Arrays.asList(restActiveService().build());
		List<ProviderMinimunDto> activesSearch = Arrays
				.asList(this.restService.loginAdmin().restBuilder(new RestBuilder<ProviderMinimunDto[]>())
						.clazz(ProviderMinimunDto[].class).path(ProviderResource.PROVIDERS)
						.path(ProviderResource.SEARCH).body(providerSearchInputDto).post().build());
		assertEquals(actives.size(), activesSearch.size());

	}
}
