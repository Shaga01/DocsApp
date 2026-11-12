package com.shashwat.docsapp.service;

import com.shashwat.docsapp.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class FileImportService {

    public String convertToHtml(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Please choose a file");
        }

        String name = file.getOriginalFilename();
        if (name == null) {
            throw new BadRequestException("File has no name");
        }
        name = name.toLowerCase();

        String text;
        try {
            text = new String(file.getBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new BadRequestException("Could not read the file");
        }

        if (name.endsWith(".txt")) {
            return textToHtml(text);
        } else if (name.endsWith(".md")) {
            return markdownToHtml(text);
        } else {
            throw new BadRequestException("Only .txt and .md files are supported");
        }
    }

    public String titleFromFileName(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return "Imported document";
        }
        int dot = fileName.lastIndexOf('.');
        if (dot > 0) {
            return fileName.substring(0, dot);
        }
        return fileName;
    }

    // every line becomes a paragraph
    private String textToHtml(String text) {
        String[] lines = text.split("\\r?\\n");
        StringBuilder html = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            String line = escapeHtml(lines[i]);
            if (line.trim().isEmpty()) {
                html.append("<p><br></p>");
            } else {
                html.append("<p>").append(line).append("</p>");
            }
        }
        return html.toString();
    }

    // simple markdown: # headings, - bullets, 1. numbered, **bold**
    private String markdownToHtml(String text) {
        String[] lines = text.split("\\r?\\n");
        StringBuilder html = new StringBuilder();
        String openList = null; // "ul" or "ol" when we are inside a list

        for (int i = 0; i < lines.length; i++) {
            String line = escapeHtml(lines[i].trim());

            // check if this line is a list item
            String listType = null;
            String itemText = null;
            if (line.startsWith("- ") || line.startsWith("* ")) {
                listType = "ul";
                itemText = line.substring(2);
            } else if (line.matches("^\\d+\\.\\s.*")) {
                listType = "ol";
                itemText = line.substring(line.indexOf('.') + 1).trim();
            }

            // close the previous list if this line is not part of it
            if (openList != null && !openList.equals(listType)) {
                html.append("</").append(openList).append(">");
                openList = null;
            }

            if (listType != null) {
                if (openList == null) {
                    html.append("<").append(listType).append(">");
                    openList = listType;
                }
                html.append("<li>").append(applyBold(itemText)).append("</li>");
            } else if (line.startsWith("### ")) {
                html.append("<h3>").append(applyBold(line.substring(4))).append("</h3>");
            } else if (line.startsWith("## ")) {
                html.append("<h2>").append(applyBold(line.substring(3))).append("</h2>");
            } else if (line.startsWith("# ")) {
                html.append("<h1>").append(applyBold(line.substring(2))).append("</h1>");
            } else if (line.isEmpty()) {
                html.append("<p><br></p>");
            } else {
                html.append("<p>").append(applyBold(line)).append("</p>");
            }
        }

        if (openList != null) {
            html.append("</").append(openList).append(">");
        }
        return html.toString();
    }

    private String applyBold(String s) {
        return s.replaceAll("\\*\\*(.+?)\\*\\*", "<strong>$1</strong>");
    }

    // so html inside the file shows as text and does not run
    private String escapeHtml(String s) {
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}