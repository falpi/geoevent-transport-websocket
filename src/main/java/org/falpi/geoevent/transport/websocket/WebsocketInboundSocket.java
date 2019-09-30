/*
  Copyright 1995-2017 Esri

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

  For additional information, contact:
  Environmental Systems Research Institute, Inc.
  Attn: Contracts Dept
  380 New York Street
  Redlands, California, USA 92373

  email: contracts@esri.com
 */

package org.falpi.geoevent.transport.websocket;

import java.util.List;
import java.util.Map;

import com.neovisionaries.ws.client.*;

import com.esri.ges.framework.i18n.BundleLogger;
import com.esri.ges.framework.i18n.BundleLoggerFactory;

public class WebsocketInboundSocket extends WebSocketAdapter
{
  private static final BundleLogger LOGGER = BundleLoggerFactory.getLogger(WebsocketInboundSocket.class);

  private boolean                   session;
  private WebSocket                 socket;
  private WebSocketFactory          factory;
  private WebsocketInboundTransport transport;

  public WebsocketInboundSocket(WebsocketInboundTransport transport) throws Exception {
	  
	  this.session = false; 
	  this.transport = transport;		
	  
	  this.factory = new WebSocketFactory();	  
	  factory.getProxySettings().setServer("http://10.145.197.23:3128");
	  
	  this.socket = factory.createSocket(transport.uri).addListener(this).connect();
  }
   
  @Override
  public void onCloseFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
	  
	  session = false;
      
	  LOGGER.info("SOCKET_CLOSED", frame.getCloseCode(), frame.getCloseReason());
	  
	  if (transport.isRunning()) {
	     LOGGER.info("RECONNECT_MSG");
		 transport.reconnect();
      }
   }

  @Override
  public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
	  
	  session = true;	  
	  socket.sendText("{\"v\":22,\"i\":{},\"s\":false,\"x\":0,\"w\":0,\"tx\":0,\"tw\":1,\"a\":6,\"z\":2,\"b\":true,\"h\":\"#m=oss;t=3;s=0;o=0;b=0.00;y=37.9962;x=23.9063;z=2;d=2;dl=2;dc=0;\",\"l\":1,\"t\":1,\"from_lightningmaps_org\":true,\"p\":[86.5,261.2,-75.2,-213.4],\"r\":\"A\"}");
  }
  
  @Override
  public void onTextMessage(WebSocket websocket, String message) throws Exception {
	  
	  if (session) transport.receive(message);
  }
  
  public void close() {
    if (socket != null)
    	socket.sendClose(WebSocketCloseCode.NORMAL, "${org.falpi.geoevent.transport.websocket-transport.TRANSPORT_STOPPED}");
  }

  public boolean isConnected() {
    return socket.isOpen();
  }
  
}
