package com.satish.resumeanalyzer;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    public String getAIAnalysis(String resumeText, String domain) {

        try {

            String endpoint =
                    "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key="
                            + apiKey;

            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String prompt =
                    "Analyze this resume for " + domain + " jobs and return HTML format only.\n" +

                            "<h4>Professional Summary</h4>\n" +
                            "<h4>Strengths</h4>\n" +
                            "<h4>Areas To Improve</h4>\n" +
                            "<h4>Missing Skills</h4>\n" +
                            "<h4>Career Suggestions</h4>\n" +

                            "Rules:\n" +
                            "- Keep response concise.\n" +
                            "- Use short bullet points only.\n" +
                            "- Avoid excessive blank lines.\n" +
                            "- Keep total response under 250 words.\n" +
                            "- Use HTML tags only.\n" +
                            "- Use <ul><li> for bullet points.\n" +
                            "- Do not use markdown (**,#).\n\n" +

                            resumeText;

            JSONObject textPart = new JSONObject();
            textPart.put("text", prompt);

            JSONArray parts = new JSONArray();
            parts.put(textPart);

            JSONObject content = new JSONObject();
            content.put("parts", parts);

            JSONArray contents = new JSONArray();
            contents.put(content);

            JSONObject body = new JSONObject();
            body.put("contents", contents);

            OutputStream os = conn.getOutputStream();
            os.write(body.toString().getBytes());
            os.flush();
            os.close();

            Scanner scanner;

            if (conn.getResponseCode() == 200) {
                scanner = new Scanner(conn.getInputStream());
            } else {
                scanner = new Scanner(conn.getErrorStream());
            }

            StringBuilder response = new StringBuilder();

            while (scanner.hasNextLine()) {
                response.append(scanner.nextLine());
            }
            System.out.println(response.toString());

            scanner.close();

            JSONObject json = new JSONObject(response.toString());



            if (!json.has("candidates")) {

                return """
            
            <ul>
                <li>Currently unavailable.</li>
                <li>Please try again later.</li>
            </ul> 
            """;
            }

            return json
                    .getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");

        } catch (Exception e) {

            e.printStackTrace();

            return """
            <h4>AI Career Insights</h4>
            <ul>
                <li>Currently unavailable.</li>
                <li>Please try again later.</li>
            </ul>
            """;
        }
    }
}