package es.upm.miw.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import es.upm.miw.documents.Provider;
import es.upm.miw.dtos.ProviderMinimunDto;

public interface ProviderRepository extends MongoRepository<Provider, String> {

	Optional<Provider> findById(String id);

	Optional<Provider> findByCompany(String company);

	@Query(value = "{}", fields = "{ 'company' : 1, 'nif' : 1}")
	List<ProviderMinimunDto> findAllProviders();

	List<ProviderMinimunDto> findByActiveTrue();

	@Query(value = "{$and:[" + "?#{ [0] == null ? { $where : 'true'} : { company : {$regex:[0]} } },"
			+ "?#{ [1] == null ? { $where : 'true'} : { nif : {$regex:[1]} } },"
			+ "?#{ [2] == null ? { $where : 'true'} : { email : {$regex:[2]} } },"
			+ "?#{ [3] == null ? { $where : 'true'} : { phone : {$regex:[3]} } },"
			+ "?#{ [4] == null ? { $where : 'true'} : { active : [4]} }" + "]}", fields = "{ 'company' : 1, 'nif' : 1}")
	List<ProviderMinimunDto> findByAttributesLike(String company, String nif, String email, String phone,
			boolean active);
}
