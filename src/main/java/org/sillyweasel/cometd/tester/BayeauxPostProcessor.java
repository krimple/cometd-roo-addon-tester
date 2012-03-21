package org.sillyweasel.cometd.tester;

import org.cometd.server.BayeuxServerImpl;
import org.cometd.websocket.server.WebSocketTransport;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Created by IntelliJ IDEA.
 * User: kenrimple
 * Date: 3/21/12
 * Time: 2:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class BayeauxPostProcessor implements BeanPostProcessor {

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    if (bean instanceof BayeuxServerImpl) {
      BayeuxServerImpl bayeux = (BayeuxServerImpl) bean;

      // add the web socket transport
      bayeux.addTransport(new WebSocketTransport(bayeux));
    }

    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    return bean;
  }
}
