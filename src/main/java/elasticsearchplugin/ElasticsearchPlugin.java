package elasticsearchplugin;

import com.typesafe.config.ConfigFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import play.Application;
import play.Logger;
import play.Plugin;
import play.api.Play;

import java.lang.annotation.Annotation;

/**
 * @author Sebastian Hardt (s.hardt@micromata.de)
 * @author Johannes Unterstein (j.unterstein@micromata.de)
 *         elasticsearch plugin with spring data
 */
public class ElasticsearchPlugin extends Plugin {


  private static ThreadLocal<ServiceProvider> elasticsearchProvider = new ThreadLocal<>();

  private final Application application;

  private static AnnotationConfigApplicationContext springContext = null;

  private final static String SERVICE_PROVIDER_NAME_CFG = "elasticsearch.serviceProviderClass";

  private static Class<?> serviceProviderClass = null;

  public ElasticsearchPlugin(Application application) {
    this.application = application;
  }

  public void onStart() {

    String serviceProviderClassName = ConfigFactory.load().getString(SERVICE_PROVIDER_NAME_CFG);
    if (StringUtils.isEmpty(serviceProviderClassName) == true) {
      if (Logger.isErrorEnabled()) {
        Logger.error("No configuration for the elasticsearch ServiceProvider found: " + SERVICE_PROVIDER_NAME_CFG + " must be set for this plugin.");
      }
      return;
    }
    try {
      final ClassLoader classLoader = Play.classloader(Play.current());
      serviceProviderClass = Class.forName(serviceProviderClassName, false, classLoader);
      Annotation annotation = serviceProviderClass.getAnnotation(Component.class);
      if (annotation == null) {
        if (Logger.isErrorEnabled()) {
          Logger.error("Class : " + serviceProviderClassName + " must be annotated with: " + Component.class.getCanonicalName());
        }
        return;
      }
    } catch (ClassNotFoundException e) {
      if (Logger.isErrorEnabled()) {
        Logger.error("Error while getting elasticsearch class from configuration: " + SERVICE_PROVIDER_NAME_CFG + " = " + serviceProviderClassName, e);
      }
      return;
    }


    final String mode = ConfigFactory.load().getString("elasticsearch.mode");

    if (mode.equals("embedded")) {
      springContext = new AnnotationConfigApplicationContext(EmbeddedElasticsearchConfig.class);
    }
    if (mode.equals("remote")) {
//      springContext = new AnnotationConfigApplicationContext(RemoteElasticsearchConfig.class);
    }


    if (springContext == null) {
      Logger.error("Could not load config must be: embedded or embeddedWithWebServer");
    }

    springContext.start();
    //springContext.scan();
    springContext.getAutowireCapableBeanFactory().autowireBean(serviceProviderClass);
    springContext.registerShutdownHook();
  }

  public void onStop() {
    EmbeddedElasticsearchConfig.shutdownNode(); // TODO JU shutdown
    springContext.stop();
  }

  private boolean isPluginDisabled() {
    String status = application.configuration().getString("elasticsearchplugin");
    return status != null && status.equals("disabled");
  }

  @Override
  public boolean enabled() {
    return isPluginDisabled() == false;
  }


  //STFU INTELLIJ
  public void $init$() {

  }

  @SuppressWarnings("unchecked")
  public static <E extends ServiceProvider> E get() {
    elasticsearchProvider.set((ServiceProvider) springContext.getBean(serviceProviderClass));
    return (E) elasticsearchProvider.get();
  }

}
