package org.switchyard.quickstarts.httpproxy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.switchyard.Context;
import org.switchyard.Property;
import org.switchyard.component.common.label.ComponentLabel;
import org.switchyard.component.common.label.EndpointLabel;
import org.switchyard.component.http.composer.HttpBindingData;
import org.switchyard.component.http.composer.HttpContextMapper;
import org.switchyard.component.http.composer.HttpRequestBindingData;
import org.switchyard.component.http.composer.HttpResponseBindingData;
import org.switchyard.label.Label;

public class CopyAllHttpHeadersHttpContextMapper extends HttpContextMapper {

   public static final String HTTP_HEADERS_REQ = "http_headers_request";
   
   public static final String HTTP_HEADERS_RES = "http_headers_response";
   
   
   @Override
   public void mapFrom(HttpBindingData source, Context context) throws Exception {
      if (source instanceof HttpRequestBindingData){
         internalMapFrom((HttpRequestBindingData)source, context);
      }else{
         internalMapFrom((HttpResponseBindingData)source, context);
      }
   }
   
   @Override
   public void mapTo(Context context, HttpBindingData target) throws Exception {
      if (target instanceof HttpRequestBindingData){
         internalMapTo(context, (HttpRequestBindingData)target);
      }else{
         internalMapTo(context, (HttpResponseBindingData)target);
      }
   }
   /**
    * (1) Request from external client
    */
   public void internalMapFrom(HttpRequestBindingData source, Context context) throws Exception {
      super.mapFrom(source, context);
     
      Iterator<Map.Entry<String, List<String>>> entries = source.getHeaders().entrySet().iterator();
      while (entries.hasNext()) {
          Map.Entry<String, List<String>> entry = entries.next();
          String name = entry.getKey();
          if (matches(name)) {
              List<String> values = entry.getValue();
              if ((values != null) && (values.size() == 1)) {
                  context.setProperty(name, values.get(0)).addLabels(MapperLabel.HTTP_REQ.label());
                  System.out.println("Added label to: " + name);
              } else if ((values != null) && (values.size() > 1)) {
                  context.setProperty(name, values).addLabels(MapperLabel.HTTP_REQ.label());
                  System.out.println("Added label to: " + name);
              }
          }
      }
   }
   
   /**
    * (4) Response from external services
    */
   public void internalMapFrom(HttpResponseBindingData source, Context context) throws Exception {
      super.mapFrom(source, context);
   }
   
   /**
    * (3) Request to external services
    */
   public void internalMapTo(Context context, HttpRequestBindingData target) throws Exception {
      super.mapTo(context, target);
      
      Map<String, List<String>> httpHeaders = target.getHeaders();
      for (Property property : context.getProperties()) {
         String name = property.getName();
         Object value = property.getValue();
         if ((value != null) && property.hasLabel(MapperLabel.HTTP_REQ.label())) {
            if (value instanceof List) {
               httpHeaders.put(name, (List<String>) value);
               System.out.println("Set http header: " + name);
            } else if (value instanceof String) {
               List<String> list = new ArrayList<String>();
               list.add(String.valueOf(value));
               httpHeaders.put(name, list);
               System.out.println("Set http header: " + name);
            }
         }
      }
   }

   /**
    * (6) Response to external client
    */
   public void internalMapTo(Context context, HttpResponseBindingData target) throws Exception {
      super.mapTo(context, target);
   }
   
   public enum MapperLabel implements Label {

      /** Component labels. */
      HTTP_REQ, HTTP_RES;

      private final String _label;

      private MapperLabel() {
          _label = Label.Util.toSwitchYardLabel("contextmapper", name());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String label() {
          return _label;
      }

      /**
       * Gets the ComponentLabel enum via case-insensitive short-name.
       * @param name the case-insensitive short-name
       * @return the ComponentLabel enum
       */
      public static final ComponentLabel ofName(String name) {
          return Label.Util.ofName(ComponentLabel.class, name);
      }

      /**
       * Gets the full-form component label from the case-insensitive short-name.
       * @param name the case-insensitive short-name
       * @return the full-form component label
       */
      public static final String toLabel(String name) {
          ComponentLabel label = ofName(name);
          return label != null ? label.label() : null;
      }

      /**
       * Prints all known component labels.
       * @param args ignored
       */
      public static void main(String... args) {
          Label.Util.print(values());
      }

  }
}
