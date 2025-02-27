package org.example.project.sqltolog;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class CustomSqlRowProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        String payload = exchange.getMessage().getBody(String.class);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonPayload = mapper.readTree(payload);
        exchange.getMessage().setBody(jsonPayload.get("data"));
    }
}
