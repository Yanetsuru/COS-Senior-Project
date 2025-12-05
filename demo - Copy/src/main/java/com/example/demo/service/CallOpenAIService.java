package com.example.demo.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CallOpenAIService {
    public String callOpenAiApi(List<Map<String, String>> input) throws InterruptedException {
        //String openAiApiKey = "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";
        //String openAiApiKey = "sk-proj-LeziL4sDBB-Ut9IgACy1fA7As3zD4YlecLj1TyOfnq3E4Y7tfTEJVfQy5Zf4-aaVcO_ZPkkMyT3BlbkFJ5K1sofO1I7SgVpMREZ18pGtQXvglfWpgGsnW2YAz94Tbjmd2CnNwYm8NC9C9SI2uqcsTkCEmsA";
        //String openAiApiKey = "sk-proj-H1P7PuSdfZ3YvQ8tlFSgJ_6tjGiP8VM-_aiFm7CFAWRak6vM7W3yIufake98ikP4uITd3-bGk9T3BlbkFJPhCjTt0GMTfeSQdmAbTf1wtojY6pX7w9xmwW4bBw5Ba_dg1jGn826AH5wyrsbnPNBK2Jmf9wwA";
        String openAiApiKey = "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";
        String apiUrl = "https://api.openai.com/v1/responses";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + openAiApiKey);
        headers.set("Content-Type", "application/json");
        headers.set("OpenAI-Beta", "responses=v1");


        List<Map<String, Object>> formattedHistory = formatHistoryForOpenAI(input);
        Map<String, Object> request = new HashMap<>();
        request.put("model", "gpt-4o-mini");
        request.put("input", formattedHistory);

            try {
                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
                ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);
                Map responseBody = response.getBody();
                //System.out.println("RESPONSE FROM OPENAI: " + responseBody);
                if (responseBody != null) {
                    if (responseBody.containsKey("output")) {
                        List<Map<String, Object>> out = (List<Map<String, Object>>) responseBody.get("output");
                        Map<String, Object> firstMessage = out.get(0);
                        List<Map<String, Object>> content = (List<Map<String, Object>>) firstMessage.get("content");
                        if (out == null || out.isEmpty()) {
                            throw new RuntimeException("No output returned from OpenAI");
                        }
                        String reply =  (String) content.get(0).get("text");

                        return reply;
                    }
                    if (responseBody.containsKey("output_text")) {
                        String out = responseBody.get("output_text").toString();
                        if (out == null || out.isEmpty()) {
                            throw new RuntimeException("No output returned from OpenAI");
                        }
                        return out;
                    }

                    throw new RuntimeException("Unknown OpenAI response format: " + responseBody);


                } else {
                    throw new RuntimeException("Unexpected API response format");
                }

            } catch (Exception e) {
                return "⚠️ AI error: " + e.getMessage();

            }

        }
    private List<Map<String, Object>> formatHistoryForOpenAI(List<Map<String, String>> history) {
        List<Map<String, Object>> formatted = new ArrayList<>();

        for (Map<String, String> msg : history) {
            String role = msg.get("role");
            String content = msg.get("content");

            if ("user".equals(role)) {
                formatted.add(Map.of(
                        "role", "user",
                        "content", List.of(Map.of(
                                "type", "input_text",
                                "text", content
                        ))
                ));
            } else if ("assistant".equals(role)) {
                formatted.add(Map.of(
                        "role", "assistant",
                        "content", List.of(Map.of(
                                "type", "output_text",
                                "text", content
                        ))
                ));
            }
        }

        return formatted;
    }



}
