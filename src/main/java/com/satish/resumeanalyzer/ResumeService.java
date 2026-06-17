package com.satish.resumeanalyzer;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResumeService {

    @Autowired
    private GeminiService geminiService;

    public String analyzeResume(MultipartFile file, String domain) {

        try {

            PDDocument document = PDDocument.load(file.getInputStream());

            PDFTextStripper stripper = new PDFTextStripper();

            String text = stripper.getText(document);
            System.out.println(text);

            document.close();

            String lowerText = text.toLowerCase();

            String result = "";

            int score = 0;

            // CONTACT DETAILS


            if (lowerText.contains("@")) {
                score += 5;
            }


            if (lowerText.matches("(?s).*\\d{10}.*")) {

                score += 5;
            }

            // EDUCATION
            // EDUCATION


            boolean degreeFound =
                    lowerText.contains("b.tech") ||
                            lowerText.contains("btech") ||
                            lowerText.contains("engineering") ||
                            lowerText.contains("b.e");

            if (degreeFound) {

                score += 5;
            }

            if (lowerText.contains("cgpa")) {

                score += 4;
            }

            if (lowerText.contains("class xii")
                    || lowerText.contains("intermediate")
                    || lowerText.contains("12th")) {


                score += 3;
            }

            if (lowerText.contains("class x")
                    || lowerText.contains("10th")) {


                score += 3;
            }
            // SKILLS


            String[] skills = {};
            int skillsFound = 0;

            if (domain.equals("CSE")) {

                skills = new String[]{
                        "java","python","c",
                        "spring","mysql",
                        "html","css",
                        "javascript","react",
                        "git","docker","aws"
                };

            } else if (domain.equals("IT")) {

                skills = new String[]{
                        "java","python","sql",
                        "html","css",
                        "javascript","react",
                        "spring boot","mysql"
                };

            } else if (domain.equals("CSBS")) {

                skills = new String[]{
                        "excel",
                        "sql",
                        "power bi",
                        "tableau",
                        "analytics",
                        "python",
                        "business analytics"
                };

            } else if (domain.equals("CSD")) {

                skills = new String[]{
                        "java",
                        "spring boot",
                        "html",
                        "css",
                        "javascript",
                        "react",
                        "mysql"
                };

            } else if (domain.equals("CSM")) {

                skills = new String[]{
                        "python",
                        "machine learning",
                        "data science",
                        "pandas",
                        "numpy",
                        "tensorflow"
                };

            } else if (domain.equals("Electronics (ECE)")) {

                skills = new String[]{
                        "embedded", "vlsi", "fpga",
                        "iot", "pcb", "verilog",
                        "matlab", "signal processing"
                };

            } else if (domain.equals("Electrical (EEE)")) {

                skills = new String[]{
                        "plc", "scada", "power systems",
                        "control systems",
                        "renewable energy"
                };

            } else if (domain.equals("Mechanical")) {

                skills = new String[]{
                        "autocad", "solidworks",
                        "catia", "ansys", "cnc"
                };

            } else if (domain.equals("Civil")) {

                skills = new String[]{
                        "autocad", "staad",
                        "etabs", "revit",
                        "primavera"
                };

            } else if (domain.equals("AIML")) {

                skills = new String[]{
                        "python",
                        "machine learning",
                        "deep learning",
                        "tensorflow",
                        "pytorch",
                        "nlp",
                        "computer vision",
                        "data science",
                        "pandas",
                        "numpy"
                };

            } else if (domain.equals("AIDS")) {

                skills = new String[]{
                        "python",
                        "data science",
                        "machine learning",
                        "sql",
                        "power bi",
                        "tableau",
                        "pandas",
                        "numpy",
                        "statistics",
                        "data analytics"
                };


            } else {

                skills = new String[]{
                        "communication",
                        "problem solving",
                        "teamwork",
                        "leadership"
                };
            }

// FOUND SKILLS
            for (String skill : skills) {

                if (lowerText.contains(skill.toLowerCase())) {


                    skillsFound++;
                }
            }

            int totalSkills = skills.length;

            if (totalSkills > 0) {

                int skillScore = (skillsFound * 20) / totalSkills;

                score += skillScore;
            }

// PROJECTS

            int projectCount = 0;

            if (lowerText.contains("project"))
                projectCount++;

            if (lowerText.contains("mini project"))
                projectCount++;

            if (lowerText.contains("major project"))
                projectCount++;

            if (lowerText.contains("developed"))
                projectCount++;

            if (lowerText.contains("implemented"))
                projectCount++;

            if (lowerText.contains("created"))
                projectCount++;

            if (projectCount == 1) {

                score += 8;

            } else if (projectCount == 2) {

                score += 14;

            } else if (projectCount > 2) {

                score += 20;
            }

// INTERNSHIP

            boolean internshipFound =
                    lowerText.contains("internship") ||
                            lowerText.contains("intern") ||
                            lowerText.contains("industrial training") ||
                            lowerText.contains("experience");

            if (internshipFound) {

                score += 10;
            }

// CERTIFICATIONS

            boolean certificateFound =
                    lowerText.contains("certificate") ||
                            lowerText.contains("certification") ||
                            lowerText.contains("nptel") ||
                            lowerText.contains("coursera") ||
                            lowerText.contains("udemy") ||
                            lowerText.contains("oracle") ||
                            lowerText.contains("aws");

            if (certificateFound) {

                score += 10;
            }

// ACHIEVEMENTS

            boolean achievementFound =
                    lowerText.contains("achievement") ||
                            lowerText.contains("winner") ||
                            lowerText.contains("hackathon") ||
                            lowerText.contains("semi-finalist") ||
                            lowerText.contains("award") ||
                            lowerText.contains("legend tier");

            if (achievementFound) {

                score += 5;
            }

// CLOUD SKILLS BONUS

            if (lowerText.contains("aws")
                    || lowerText.contains("google cloud")
                    || lowerText.contains("azure")) {

                score += 5;
            }

// GITHUB

            if (lowerText.contains("github")) {

                score += 5;
            }

// LINKEDIN

            Pattern linkedinPattern = Pattern.compile(
                    "linkedin",
                    Pattern.CASE_INSENSITIVE);

            Matcher linkedinMatcher = linkedinPattern.matcher(text);

            if (linkedinMatcher.find()) {

                score += 5;
            }

// LEVEL

            // LEVEL

            String level;

            if (score > 100) {
                score = 100;
            }

            if (score >= 90) {
                level = "Excellent";
            } else if (score >= 75) {
                level = "Advanced";
            } else if (score >= 50) {
                level = "Intermediate";
            } else {
                level = "Beginner";
            }

// FINAL OUTPUT

            result =
                    "<div style='background:#f5f5f5;padding:15px;border-radius:10px;margin-bottom:20px;'>"
                            + "<h2 style='color:green;margin:0;'>ATS Score: " + score + "/100</h2>"
                            + "<h3 style='margin-top:10px;'>Candidate Level: " + level + "</h3>"
                            + "</div><hr>";

            String aiReport = geminiService.getAIAnalysis(text, domain);

            result += "<h3 style='color:blue; margin-bottom:25px;'>AI Career Insights</h3>";

            aiReport = aiReport.replace("```html", "");
            aiReport = aiReport.replace("```", "");
            aiReport = aiReport.replace("**", "");
            aiReport = aiReport.replace("\n", "<br>");
            aiReport = aiReport.replace("<br><br>", "<br>");
            aiReport = aiReport.replace("<br>\n<br>", "<br>");

            aiReport = aiReport.replace(
                    "1. Professional Summary",
                    "<h4>Professional Summary</h4>");

            aiReport = aiReport.replace(
                    "2. Strengths",
                    "<h4>Strengths</h4>");

            aiReport = aiReport.replace(
                    "3. Areas to Improve",
                    "<h4>Areas to Improve</h4>");

            aiReport = aiReport.replace(
                    "4. Missing Skills",
                    "<h4>Missing Skills</h4>");

            aiReport = aiReport.replace(
                    "5. Career Suggestions",
                    "<h4>Career Suggestions</h4>");
            aiReport = aiReport.replace("<h4>", "<h4 style='margin-top:25px;'>");

            result += aiReport;

            return result;
        }catch(Exception e) {
            return "Error Reading PDF";
        }
    }
}