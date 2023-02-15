//package com.javacodegeeks.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
//import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.spi.DataFormat;

//import org.apache.camel.;

public class CamelHelloWorldTimerExample {
    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();
        try {
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                   // from("timer://myTimer?period=2000")
                    from("file:c:/temp/camelread")
                            //.setBody()
                            //.simple("Hello World Camel fired at ${header.firedTime}")
//                            .to("file:C:/temp/camelout");
//                    .to("stream:out");
                            .split(body().tokenize("\n"))
                                .log("Received message: ${body}")
                                .log("=====================================");
                }
            });
            context.start();
            Thread.sleep(50000);
        } finally {
            context.stop();
        }
    }
}