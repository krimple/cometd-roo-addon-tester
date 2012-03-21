package org.sillyweasel.cometd.tester;


import org.cometd.bayeux.server.*;
import org.cometd.common.ChannelId;
import org.cometd.server.authorizer.GrantAuthorizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public final class ChatService {

  @Autowired
  BayeuxServer server;

  @PostConstruct
  void init() {
    server.addListener(new BayeuxServer.SessionListener() {
      @Override
      public void sessionAdded(ServerSession session) {
        session.setAttribute("user", server.getContext().getHttpSessionAttribute("user"));
        server.getChannel("/chatroom").publish(session, "connected !", null);
      }

      @Override
      public void sessionRemoved(ServerSession session, boolean timedout) {
        server.getChannel("/chatroom").publish(session, "disconnected !", null);
        session.removeAttribute("user");
      }
    });
  }

  @Configure("/**")
  void any(ConfigurableServerChannel channel) {
    channel.addAuthorizer(GrantAuthorizer.GRANT_NONE);
  }

  @Configure("/chatroom")
  void configure(ConfigurableServerChannel channel) {
    channel.addAuthorizer(new Authorizer() {
      @Override
      public Result authorize(Operation operation, ChannelId channel, ServerSession session, ServerMessage message) {
        return session.getAttribute("user") != null ? Result.grant() : Result.deny("no user in session");
      }
    });
  }

  @Listener("/chatroom")
  void appendUser(ServerSession remote, ServerMessage.Mutable message) {
    message.setData("[" + remote.getAttribute("user") + "] " + message.getData());
  }

}
