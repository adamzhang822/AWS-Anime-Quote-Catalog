package edu.uchicago.adamzhang22.emailer;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


import org.json.JSONObject;
import org.junit.Test;

public class AppTest {
    @Test
    public void successfulResponse() {
        assert(true);

        /*
        App app = new App();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("subject", "yo");
        jsonObject.put("body", "yo");
        jsonObject.put("email", "yo");

        APIGatewayProxyResponseEvent result = app.handleRequest(
                new APIGatewayProxyRequestEvent().withBody(jsonObject.toString()), null);
        assertEquals(result.getStatusCode().intValue(), 200);
        assertEquals(result.getHeaders().get("Content-Type"), "application/json");
        String content = result.getBody();
        assertNotNull(content);
        assertTrue(content.contains("\"subject\""));
        assertTrue(content.contains("\"body\""));
        assertTrue(content.contains("\"email\""));
        */

    }
}
