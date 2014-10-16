package com.example.switchyard.switchyard_pollenrich_example;

import org.apache.camel.builder.RouteBuilder;

public class CamelServiceRoute extends RouteBuilder {

	/**
	 * The Camel route is configured via this method.  The from endpoint is required to be a SwitchYard service.
	 */
	public void configure() {
		from("switchyard://ProgramaticReload")
		.log("Going to read the file")
		.pollEnrich("file:///input?fileName=in.txt",1000)
		.log("Received message for 'ProgramaticReload' : ${body}")
		.to("switchyard://FileWriterService");
	}

}
