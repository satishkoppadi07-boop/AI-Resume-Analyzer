package com.satish.resumeanalyzer;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpSession;
import com.satish.resumeanalyzer.entity.User;

@Controller
public class HomeController {

    @Autowired
    private ResumeService resumeService;

    private String latestReport = "";

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/home")
    public String homePage() {
        return "home";
    }

    @GetMapping("/upload")
    public String uploadPage(HttpSession session, Model model) {

        User user = (User) session.getAttribute("loggedUser");

        if(user == null){
            return "redirect:/login";
        }

        model.addAttribute("username", user.getName());

        return "index";
    }

    @PostMapping("/analyze")
    public String analyzeResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam("domain") String domain,
            Model model,
            HttpSession session) {

        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/login";
        }

        String result = resumeService.analyzeResume(file, domain);

        latestReport = result;

        model.addAttribute("message", result);

        return "index";
    }

    @GetMapping("/download")
    public void downloadReport(
            HttpServletResponse response,
            HttpSession session) throws Exception {

        if (session.getAttribute("loggedUser") == null) {
            response.sendRedirect("/login");
            return;
        }

        response.setContentType("application/pdf");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=Resume_Report.pdf"
        );

        Document document = new Document();

        PdfWriter.getInstance(
                document,
                response.getOutputStream()
        );

        document.open();

        Font titleFont =
                new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);

        Font headingFont =
                new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);

        Font contentFont =
                new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);

        document.add(
                new Paragraph(
                        "RESUME ANALYSIS REPORT",
                        titleFont
                )
        );

        document.add(new Paragraph(" "));

        if (latestReport != null && !latestReport.isEmpty()) {

            String plainText = latestReport;

            plainText = plainText.replace("<h2>", "");
            plainText = plainText.replace("</h2>", "\n");

            plainText = plainText.replace("<h3>", "");
            plainText = plainText.replace("</h3>", "\n\n");

            plainText = plainText.replace("<h4>", "");
            plainText = plainText.replace("</h4>", "\n\n");

            plainText = plainText.replace("<li>", "• ");
            plainText = plainText.replace("</li>", "\n");

            plainText = plainText.replace("<ul>", "");
            plainText = plainText.replace("</ul>", "");

            plainText = plainText.replace("<br>", "\n");
            plainText = plainText.replace("<br/>", "\n");
            plainText = plainText.replace("<br />", "\n");

            plainText = plainText.replace("<hr>", "\n");

            plainText = plainText.replace("&nbsp;", " ");

            plainText = plainText.replaceAll("<[^>]*>", "");

            String[] lines = plainText.split("\n");

            for (String line : lines) {

                line = line.trim();

                if (line.isEmpty()) {
                    continue;
                }

                if (
                        line.equalsIgnoreCase("AI Career Insights") ||
                                line.equalsIgnoreCase("Professional Summary") ||
                                line.equalsIgnoreCase("Strengths") ||
                                line.equalsIgnoreCase("Areas To Improve") ||
                                line.equalsIgnoreCase("Missing Skills") ||
                                line.equalsIgnoreCase("Career Suggestions")
                ) {

                    document.add(new Paragraph(" "));
                    document.add(
                            new Paragraph(
                                    line.toUpperCase(),
                                    headingFont
                            )
                    );

                } else {

                    document.add(
                            new Paragraph(
                                    line,
                                    contentFont
                            )
                    );
                }
            }

        } else {

            document.add(
                    new Paragraph(
                            "No resume analyzed yet. Please analyze a resume first.",
                            contentFont
                    )
            );
        }

        document.close();
    }
}