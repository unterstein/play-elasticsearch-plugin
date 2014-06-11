package elasticsearchplugin;

import com.typesafe.config.ConfigFactory;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

/**
 * @author Sebastian Hardt (s.hardt@micromata.de)
 * @author Johannes Unterstein (j.unterstein@micromata.de)
 *         elasticsearch plugin with spring data
 */
@Configuration
@ComponentScan("elasticsearch")
public class EmbeddedElasticsearchConfig extends AnnotationConfigApplicationContext {

  /**
   * Config key which defines where the embedded database path is.
   */
  private static String EMBEDDED_DB_CFG_KEY = "elasticsearch.embeddedDB";

  private static Settings elasticsearchSettings = ImmutableSettings.settingsBuilder()
      .put("path.data", "target/elasticsearch-data") // TODO JU configurable
      .put("http.port", 8200)  // TODO JU configurable
      .build();

  private static Node node = nodeBuilder().local(true).settings(elasticsearchSettings).node();

  private static Client client = node.client();


  @Bean
  public ElasticsearchTemplate elasticsearchTemplate() {
    String embeddedDB = ConfigFactory.load().getString(EMBEDDED_DB_CFG_KEY);
    if (StringUtils.isEmpty(embeddedDB) == true) {
      throw new RuntimeException("Could not find config for embedded DB: " + EMBEDDED_DB_CFG_KEY);
    }
    return new ElasticsearchTemplate(client);
  }

  public static void shutdownNode() {
    node.close();
  }
}
