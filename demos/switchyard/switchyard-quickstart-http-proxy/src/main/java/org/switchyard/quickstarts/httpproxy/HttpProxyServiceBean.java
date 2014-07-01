package org.switchyard.quickstarts.httpproxy;

import javax.inject.Inject;

import org.apache.camel.component.http.HttpOperationFailedException;
import org.switchyard.Context;
import org.switchyard.Exchange;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.Property;
import org.switchyard.Scope;
import org.switchyard.component.bean.Reference;
import org.switchyard.component.bean.ReferenceInvocation;
import org.switchyard.component.bean.ReferenceInvoker;
import org.switchyard.component.bean.Service;
import org.switchyard.component.common.label.EndpointLabel;
import org.switchyard.component.http.composer.HttpComposition;
import org.switchyard.component.http.composer.HttpContextMapper;
import org.switchyard.component.http.composer.HttpRequestInfo;
import org.switchyard.quickstarts.httpproxy.CopyAllHttpHeadersHttpContextMapper.MapperLabel;

@Service(HttpProxyService.class)
public class HttpProxyServiceBean implements HttpProxyService {


   @Inject
   @Reference("RealHttpService")
   private ReferenceInvoker referenceInvoker;

   @Inject
   private Exchange exchange;

   @Override
   public String proxyContent(String content) {
      String replyContent = "";
      int statusCode = 500;
      ReferenceInvocation invocation = null;

      // We get information from incoming request
      HttpRequestInfo requestInfo = exchange.getMessage().getContext().getPropertyValue(HttpComposition.HTTP_REQUEST_INFO);
      
//      String baseURL = exchange.getMessage().getContext().getPropertyValue("base_url");
//      if (baseURL == null || "".equals(baseURL)){
//         replyWithHTTPStatusCode(exchange, 500);
//         System.out.println("You need to send base_url http header with the endpoint to call. Ej: http://localhost:8080");
//         return "";         
//      }
      String baseURL = "http://localhost:8080/rest-binding";
      logInvocation(requestInfo, content);

      // We set information for outgoing request
      try {
         invocation = referenceInvoker.newInvocation();
         
         // Copies properties from contexts
         exchange.getMessage().getContext().mergeInto(invocation.getMessage().getContext());
         
         copyRequestInfo(requestInfo, invocation.getMessage().getContext(), baseURL);
         
         replyContent = invocation.invoke(content).getMessage().getContent(String.class);
         
         statusCode = 200;
         // FIXME: NPE in invocation.getContext() or getPropertyValue
         // statusCode = invocation.getContext().getPropertyValue(HttpContextMapper.HTTP_RESPONSE_STATUS);
      } catch (HttpOperationFailedException e) {
         statusCode = e.getStatusCode();
         replyWithHTTPStatusCode(exchange, statusCode);
      } catch (Exception e) {
         e.printStackTrace();
         if (invocation != null) {
            statusCode = retrieveStatusCode(invocation);
            replyWithHTTPStatusCode(exchange, statusCode);
         }
      }

      return replyContent;
   }

   private void replyWithHTTPStatusCode(Exchange currentExchange, int statusCode) {
      Message reply = currentExchange.createMessage();
      reply.setContent("StatusCode=" + statusCode);
      reply.getContext().setProperty(HttpContextMapper.HTTP_RESPONSE_STATUS, statusCode).addLabels(EndpointLabel.HTTP.label());
      currentExchange.send(reply);
   }
   
   private void copyRequestInfo(HttpRequestInfo requestInfo, Context context, String REFERENCE_BASE_URL) {
      context.setProperty(org.apache.camel.Exchange.HTTP_METHOD, requestInfo.getMethod(), Scope.MESSAGE);
      context.setProperty(org.apache.camel.Exchange.HTTP_URI, REFERENCE_BASE_URL + requestInfo.getPathInfo(), Scope.MESSAGE);
      context.setProperty(org.apache.camel.Exchange.HTTP_QUERY, requestInfo.getQueryString(), Scope.MESSAGE);
   }

   private void logInvocation(HttpRequestInfo requestInfo, String content) {
      System.out.println("********************************************************************************");
      System.out.println("method      = " + requestInfo.getMethod());
      System.out.println("pathInfo    = " + requestInfo.getPathInfo());
      System.out.println("queryString = " + requestInfo.getQueryString());
      System.out.println("content     = " + content);
      System.out.println("********************************************************************************");
   }
   
   private int retrieveStatusCode(ReferenceInvocation invocation) {
      // Checking response when invoking via SwitchYard Http Reference Binding
      // statusCode = invocation.getContext().getPropertyValue(HttpContextMapper.HTTP_RESPONSE_STATUS);
      // If call via camel.uri then we need to check this other way
      Exception exception = invocation.getContext().getPropertyValue(org.apache.camel.Exchange.EXCEPTION_CAUGHT);
      if (!(exception instanceof HandlerException)) { return 500; }

      HandlerException handlerEx = (HandlerException) exception;
      HttpOperationFailedException httpOperationFailed = (HttpOperationFailedException) handlerEx.getCause();
      return httpOperationFailed.getStatusCode();
  }
}
