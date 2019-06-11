package es.upm.miw.data_services;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import es.upm.miw.documents.Provider;
import es.upm.miw.repositories.ProviderRepository;

@Service
public class DatabaseSeederService {

	private static final String VARIOUS_CODE = "1";

	private static final String VARIOUS_NAME = "Varios";

	@Autowired
	private Environment environment;

	@Value("${miw.admin.mobile}")
	private String mobile;
	@Value("${miw.admin.username}")
	private String username;
	@Value("${miw.admin.password}")
	private String password;

	@Value("${miw.databaseSeeder.ymlFileName:#{null}}")
	private String ymlFileName;

	@Autowired
	private ProviderRepository providerRepository;

	@PostConstruct
	public void constructor() {
		String[] profiles = this.environment.getActiveProfiles();
		if (Arrays.stream(profiles).anyMatch("dev"::equals)) {
			this.deleteAllAndInitializeAndLoadYml();
		} 
//		else if (Arrays.stream(profiles).anyMatch("prod"::equals)) {
//			this.initialize();
//		}
	}

	private void initialize() {
		if (!this.providerRepository.findById(VARIOUS_CODE).isPresent()) {
			LogManager.getLogger(this.getClass()).warn("------- Create provider -----------");
			Provider provider = new Provider(VARIOUS_NAME);
			this.providerRepository.save(provider);
		}
	}

	public void deleteAllAndInitialize() {
		LogManager.getLogger(this.getClass()).warn("------- Delete All -----------");
		// Delete Repositories -----------------------------------------------------
		this.providerRepository.deleteAll();
		// -------------------------------------------------------------------------
		this.initialize();
	}

	public void deleteAllAndInitializeAndLoadYml() {
		this.deleteAllAndInitialize();
		this.seedDatabase();
//		this.initialize();
	}

	public void seedDatabase() {
		if (this.ymlFileName != null) {
			try {
				LogManager.getLogger(this.getClass()).warn("------- Initial Load: " + this.ymlFileName + "-----------");
				this.seedDatabase(new ClassPathResource(this.ymlFileName).getInputStream());
			} catch (IOException e) {
				LogManager.getLogger(this.getClass())
						.error("File " + this.ymlFileName + " doesn't exist or can't be opened");
			}
		} else {
			LogManager.getLogger(this.getClass()).error("File db.yml doesn't configured");
		}
	}

	public void seedDatabase(InputStream input) {
		Yaml yamlParser = new Yaml(new Constructor(DatabaseGraph.class));
		DatabaseGraph tpvGraph = yamlParser.load(input);

		// Save Repositories -----------------------------------------------------
		this.providerRepository.saveAll(tpvGraph.getProviderList());
		// -----------------------------------------------------------------------

		LogManager.getLogger(this.getClass()).warn("------- Seed...   " + "-----------");
	}

	public String nextCodeEan() {
		throw new RuntimeException("Method nextCodeEan not implemented");
	}

}
