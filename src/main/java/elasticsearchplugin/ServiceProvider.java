package elasticsearchplugin;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

/**
 * @author Sebastian Hardt (s.hardt@micromata.de)
 * @author Johannes Unterstein (j.unterstein@micromata.de)
 *         elasticsearch plugin with spring data
 */
public class ServiceProvider {

  @Autowired
  public ElasticsearchTemplate template;
}
