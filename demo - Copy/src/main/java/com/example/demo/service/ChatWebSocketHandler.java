    package com.example.demo.service;
    import com.example.demo.dungeon.DungeonConfig;
    import com.example.demo.dungeon.DungeonGenerator;
    import com.example.demo.model.ActionData;
    import com.example.demo.model.DnDSession;
    import com.example.demo.model.SessionHistory;
    import com.example.demo.repository.CharacterRepository;
    import com.example.demo.repository.DnDSessionRepository;
    import com.example.demo.repository.SessionHistoryRepository;
    import com.example.demo.service.CallOpenAIService;
    import com.example.demo.controller.MainController;
    import com.fasterxml.jackson.databind.ObjectMapper;
    import jakarta.servlet.http.HttpSession;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Component;
    import org.springframework.web.socket.TextMessage;
    import org.springframework.web.socket.WebSocketSession;
    import org.springframework.web.socket.handler.TextWebSocketHandler;
    import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

    import java.io.IOException;
    import java.time.LocalDateTime;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.Map;
    import java.util.regex.Matcher;
    import java.util.regex.Pattern;

    @Component
    public class ChatWebSocketHandler extends TextWebSocketHandler {
        private final Map<String, Long> lastMessageTime = new java.util.concurrent.ConcurrentHashMap<>();
        private final CallOpenAIService openAIService;
        private final SessionHistoryRepository sessionHistoryRepository;
        private final DnDSessionRepository dnDSessionRepository;
        @Autowired
        private CharacterRepository characterRepository;


        @Autowired
        public ChatWebSocketHandler(CallOpenAIService openAIService,
                                    SessionHistoryRepository sessionHistoryRepository,
                                    DnDSessionRepository dnDSessionRepository) {
            this.openAIService = openAIService;
            this.sessionHistoryRepository = sessionHistoryRepository;
            this.dnDSessionRepository = dnDSessionRepository;
        }


        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws IOException, InterruptedException {
            if (session.getAttributes().get("conversationHistory") == null) {
                session.getAttributes().put("conversationHistory", new ArrayList<Map<String, String>>());
            }

            com.example.demo.model.Character character = (com.example.demo.model.Character) session.getAttributes().get("character");
            List<Map<String, String>> history = (List<Map<String, String>>) session.getAttributes().get("conversationHistory");


            if (character != null && history.isEmpty()) {
                DungeonConfig config = new DungeonConfig(80, 50, 10, 20, 5, 15);
                DungeonGenerator generator = new DungeonGenerator(config);

                char[][] dungeon = generator.generate();

                // Convert dungeon map to text
                StringBuilder mapBuilder = new StringBuilder();
                for (char[] row : dungeon) {
                    mapBuilder.append(new String(row)).append("\n");
                }

                String dungeonMap = mapBuilder.toString();
                // Build initial DM message
                String initialMessage = "You are a Dungeons & Dragons 5e Dungeon Master.\n\n" +
                        "Here is the player's character sheet:\n" +
                        "Name: " + character.getName() + "\n" +
                        "Age: " + character.getAge() + "\n" +
                        "Level: " + character.getLevel() + "\n" +
                        "HP: " + character.getHp() + "\n" +
                        "XP: " + character.getXp() + "\n" +
                        "Species: " + character.getSpecies() + "\n" +
                        "Background: " + character.getBackground() + "\n" +
                        "Class: " + character.getDndclass() + "\n" +
                        "STR: " + character.getStrength() + "\n" +
                        "DEX: " + character.getDexterity() + "\n" +
                        "INT: " + character.getIntelligence() + "\n" +
                        "CON: " + character.getConstitution() + "\n" +
                        "WIS: " + character.getWisdom() + "\n" +
                        "CHA: " + character.getCharisma() + "\n\n" +
                        "If you ever go into a dungeon, here is an ASCII grid of the dungeon:\n" +
                        dungeonMap + "\n\n" +
                        "Rules:\n" +
                        "1. Always describe the world, environment, NPC interactions, and consequences.\n" +
                        "2. Never decide the player's actions.\n" +
                        "3. When a roll is required, tell the player what to roll (e.g., 'Roll Dexterity').\n" +
                        "4. Keep responses short enough for chat but rich in detail.\n" +
                        "5. Start by welcoming the player into the world and describing where their adventure begins." +
                        "6. After every message you send to the player, include a hidden <action> JSON block with game effects.\n" +
                        "Example:\n" +
                        "<action>{\"damageToPlayer\":5,\"healPlayer\":0,\"xpGained\":0,\"enemyDamage\":0}</action>\n" +
                        "Do not explain the JSON. Do not comment on it. Always include it.";
                session.getAttributes().put("dungeonMapStorage", dungeonMap);

                // send dungeon to browser first
                session.sendMessage(new TextMessage(
                        "{\"type\": \"dungeon\", \"map\": \"" + dungeonMap.replace("\n", "\\n") + "\"}"
                ));
                history.add(Map.of("role", "user", "content", initialMessage));
                DnDSession dndSession = (DnDSession) session.getAttributes().get("activeSession");
                sessionHistoryRepository.save(
                        new SessionHistory("user", initialMessage, dndSession)
                );
                // Send to AI service
                List<Map<String, String>> input = new ArrayList<>();
                input.add(Map.of("role", "user", "content", initialMessage));
                String aiReply = openAIService.callOpenAiApi(input);
                history.add(Map.of("role", "assistant", "content", aiReply));
                sessionHistoryRepository.save(
                        new SessionHistory("assistant", aiReply, dndSession)
                );
                String cleanReply = aiReply.replaceAll("<action>.*?</action>", "").trim();

                // Send AI reply back to client immediately
                session.sendMessage(new TextMessage(cleanReply));

            }
        }
        private String escapeJSON(String text) {
            return text.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n");
        }
        private ActionData extractAction(String aiText) {
            try {
                Pattern p = Pattern.compile("<action>(.*?)</action>", Pattern.DOTALL);
                Matcher m = p.matcher(aiText);

                if (!m.find()) return null;

                String json = m.group(1);

                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(json, ActionData.class);

            } catch (Exception e) {
                System.out.println("Failed to parse action JSON: " + e.getMessage());
                return null;
            }
        }



        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            String sessionId = session.getId();
            long now = System.currentTimeMillis();
            long lastTime = lastMessageTime.getOrDefault(sessionId, 0L);
            if (now - lastTime < 1000) {
                session.sendMessage(new TextMessage("{\"error\":\"Rate limit exceeded. Wait 1 second.\"}"));
                return;
            }
            lastMessageTime.put(sessionId, now);
            String userMessage = message.getPayload();
            DnDSession dndSession = (DnDSession) session.getAttributes().get("activeSession");


            // Retrieve conversation history
            List<Map<String, String>> history = (List<Map<String, String>>) session.getAttributes().get("conversationHistory");

            if (history == null) {
                history = new ArrayList<>();
                session.getAttributes().put("conversationHistory", history);
            }

            // Add player message to history
            history.add(Map.of("role", "user", "content", userMessage));
            SessionHistory userEntry = new SessionHistory("user", userMessage, dndSession);
            sessionHistoryRepository.save(userEntry);

            // Call OpenAI API
            String aiResponse = openAIService.callOpenAiApi(history);
            ActionData action = extractAction(aiResponse);
            applyActionToCharacter(action, session);
            // Remove the action-tag before sending to client
            String cleanReply = aiResponse.replaceAll("<action>.*?</action>", "").trim();
            // Add AI response to history
            history.add(Map.of("role", "assistant", "content", cleanReply));
            // Save AI reply
            SessionHistory aiEntry = new SessionHistory("assistant", cleanReply, dndSession);
            sessionHistoryRepository.save(aiEntry);


            // Update session timestamp
            dndSession.setLastModified(LocalDateTime.now());
            dnDSessionRepository.save(dndSession);

            // Send AI response to client
            session.sendMessage(new TextMessage(cleanReply));

        }
        private void applyActionToCharacter(ActionData a, WebSocketSession session) throws IOException {
            if (a == null) return;

            com.example.demo.model.Character character =
                    (com.example.demo.model.Character) session.getAttributes().get("character");

            if (character == null) return;
            // HP updates
            character.setHp(character.getHp() - a.damageToPlayer);
            character.setHp(character.getHp() + a.healPlayer);
            character.setHp(Math.max(character.getHp(), 0));
            // XP
            character.setXp(character.getXp() + a.xpGained);
            while (character.getXp() >= character.getLevel()*100){
                character.setXp(character.getXp() - character.getLevel()*100);
                character.setLevel(character.getLevel() + 1);
            }


            // Save back to session attributes
            session.getAttributes().put("character", character);
            characterRepository.save(character);
            session.sendMessage(new TextMessage(
                    "{\"type\":\"characterUpdate\",\"hp\":" + character.getHp() + ",\"xp\":" + character.getXp() + ",\"level\":" + character.getLevel() + "}"
            ));

        }

    }
