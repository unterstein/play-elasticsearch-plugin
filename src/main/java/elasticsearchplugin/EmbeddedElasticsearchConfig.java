package elasticsearchplugin;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.cdi.ElasticsearchRepositoryBean;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

/**
 * @author Sebastian Hardt (s.hardt@micromata.de)
 * @author Johannes Unterstein (j.unterstein@micromata.de)
 *         elasticsearch plugin with spring data
 */
@Configuration
@EnableElasticsearchRepositories(basePackages = "elasticsearch.repositories", repositoryFactoryBeanClass = ElasticsearchRepositoryBean.class)
//@ComponentScan("elasticsearch")
public class EmbeddedElasticsearchConfig extends AnnotationConfigApplicationContext {

  private static Settings elasticsearchSettings = ImmutableSettings.settingsBuilder()
      .put("path.data", "target/elasticsearch-data") // TODO JU configurable
      .put("http.port", 8200)  // TODO JU configurable
      .build();

  private static Node node = nodeBuilder().local(true).settings(elasticsearchSettings).node();

  private static Client client = node.client();


  @Bean
  public ElasticsearchTemplate elasticsearchTemplate() {
    return new ElasticsearchTemplate(client);
  }

  @Bean
  public Client client() {
    return client;
  }

  public static void shutdownNode() {
    node.close();
  }
}
