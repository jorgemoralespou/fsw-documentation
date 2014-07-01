package org.switchyard.quickstarts.httpproxy;

import org.apache.camel.Message;
import org.switchyard.Context;
import org.switchyard.Property;
import org.switchyard.Scope;
import org.switchyard.component.camel.common.composer.CamelBindingData;
import org.switchyard.component.camel.common.composer.CamelContextMapper;
import org.switchyard.quickstarts.httpproxy.CopyAllHttpHeadersHttpContextMapper.MapperLabel;

public class CopyAllHttpHeadersCamelContextMapper extends CamelContextMapper{

   @Override
   public void mapFrom(CamelBindingData source, Context context) throws Exception {
      super.mapFrom(source, context);
   }
   
   @Override
   public void mapTo(Context context, CamelBindingData target) throws Exception {
      super.mapTo(context, target);
      
      Message message = target.getMessage();
      // Iterate over the properties with label
      for (Property prop: context.getProperties(Scope.MESSAGE)){
         if (prop.hasLabel(MapperLabel.HTTP_REQ.label())){
            message.setHeader(prop.getName(), prop.getValue());
            // TODO: Why some other headers are copied???
            System.out.println("Set http header: " + prop.getName());
         }
      }
   }
}
