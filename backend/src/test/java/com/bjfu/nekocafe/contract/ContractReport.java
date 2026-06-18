package com.bjfu.nekocafe.contract;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

final class ContractReport {
    private static final List<String[]> ROWS = new ArrayList<String[]>();

    private ContractReport() {}

    static synchronized void record(String consumer, String provider, String interaction, String status) {
        ROWS.add(new String[] { consumer, provider, interaction, status });
    }

    static synchronized void write() throws IOException {
        Path report = Paths.get("..", "tests", "reports", "pact.html");
        Files.createDirectories(report.getParent());
        StringBuilder html = new StringBuilder();
        html.append("<!doctype html><html><head><meta charset=\"UTF-8\"><title>NekoCafe Pact Report</title>");
        html.append("<style>body{font-family:Arial,sans-serif;margin:24px}table{border-collapse:collapse;width:100%}");
        html.append("th,td{border:1px solid #ddd;padding:8px}th{background:#f6f8fa}</style></head><body>");
        html.append("<h1>NekoCafe Contract Test Report</h1><table><thead><tr>");
        html.append("<th>Consumer</th><th>Provider</th><th>Interaction</th><th>Status</th></tr></thead><tbody>");
        for (String[] row : ROWS) {
            html.append("<tr><td>").append(escape(row[0])).append("</td><td>").append(escape(row[1])).append("</td><td>");
            html.append(escape(row[2])).append("</td><td>").append(escape(row[3])).append("</td></tr>");
        }
        html.append("</tbody></table></body></html>");
        Files.write(report, html.toString().getBytes(StandardCharsets.UTF_8));
    }

    private static String escape(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
