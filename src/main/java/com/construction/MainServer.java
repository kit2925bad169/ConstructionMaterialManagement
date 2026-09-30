package com.construction;

import com.construction.dao.MaterialDAO;
import com.construction.dao.PurchaseDAO;
import com.construction.dao.SupplierDAO;
import com.construction.dao.UsageDAO;
import com.construction.model.Material;
import com.construction.model.MaterialUsage;
import com.construction.model.Purchase;
import com.construction.model.Supplier;
import com.construction.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.SQLException;
import java.util.*;

public class MainServer {

    private static final int PORT = 8080;
    private static final Path WEB_ROOT =
            Paths.get("src/main/resources/web").toAbsolutePath();

    private static final MaterialDAO materialDAO = new MaterialDAO();
    private static final SupplierDAO supplierDAO = new SupplierDAO();
    private static final PurchaseDAO purchaseDAO = new PurchaseDAO();
    private static final UsageDAO usageDAO = new UsageDAO();

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(
                new InetSocketAddress("localhost", PORT), 0);

        server.createContext("/api/materials", MainServer::handleMaterials);
        server.createContext("/api/suppliers", MainServer::handleSuppliers);
        server.createContext("/api/purchases", MainServer::handlePurchases);
        server.createContext("/api/usage", MainServer::handleUsage);
        server.createContext("/", MainServer::serveStatic);

        server.setExecutor(null);
        server.start();

        System.out.println("Construction Material Management System");
        System.out.println("Server running at http://localhost:" + PORT);
    }

    private static void handleMaterials(HttpExchange exchange) throws IOException {
        try {
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                List<Material> materials = materialDAO.getAllMaterials();
                StringBuilder json = new StringBuilder("[");

                for (int i = 0; i < materials.size(); i++) {
                    Material m = materials.get(i);

                    if (i > 0) json.append(",");

                    json.append("{")
                            .append("\"id\":").append(m.getMaterialId()).append(",")
                            .append("\"name\":\"").append(JsonUtil.escape(m.getMaterialName())).append("\",")
                            .append("\"category\":\"").append(JsonUtil.escape(m.getCategory())).append("\",")
                            .append("\"unit\":\"").append(JsonUtil.escape(m.getUnit())).append("\",")
                            .append("\"quantity\":").append(m.getQuantity()).append(",")
                            .append("\"minimumStock\":").append(m.getMinimumStock())
                            .append("}");
                }

                json.append("]");
                sendJson(exchange, 200, json.toString());
                return;
            }

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                Map<String, String> form = readForm(exchange);

                Material material = new Material(
                        form.get("name"),
                        form.get("category"),
                        form.get("unit"),
                        Double.parseDouble(form.get("quantity")),
                        Double.parseDouble(form.get("minimumStock"))
                );

                materialDAO.addMaterial(material);
                sendJson(exchange, 201, "{\"message\":\"Material added successfully\"}");
                return;
            }

            sendJson(exchange, 405, "{\"error\":\"Method not allowed\"}");
        } catch (Exception e) {
            sendError(exchange, e);
        }
    }

    private static void handleSuppliers(HttpExchange exchange) throws IOException {
        try {
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                List<Supplier> suppliers = supplierDAO.getAllSuppliers();
                StringBuilder json = new StringBuilder("[");

                for (int i = 0; i < suppliers.size(); i++) {
                    Supplier s = suppliers.get(i);

                    if (i > 0) json.append(",");

                    json.append("{")
                            .append("\"id\":").append(s.getSupplierId()).append(",")
                            .append("\"name\":\"").append(JsonUtil.escape(s.getSupplierName())).append("\",")
                            .append("\"phone\":\"").append(JsonUtil.escape(s.getPhone())).append("\",")
                            .append("\"email\":\"").append(JsonUtil.escape(s.getEmail())).append("\",")
                            .append("\"address\":\"").append(JsonUtil.escape(s.getAddress())).append("\"")
                            .append("}");
                }

                json.append("]");
                sendJson(exchange, 200, json.toString());
                return;
            }

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                Map<String, String> form = readForm(exchange);

                Supplier supplier = new Supplier(
                        form.get("name"),
                        form.get("phone"),
                        form.get("email"),
                        form.get("address")
                );

                supplierDAO.addSupplier(supplier);
                sendJson(exchange, 201, "{\"message\":\"Supplier added successfully\"}");
                return;
            }

            sendJson(exchange, 405, "{\"error\":\"Method not allowed\"}");
        } catch (Exception e) {
            sendError(exchange, e);
        }
    }

    private static void handlePurchases(HttpExchange exchange) throws IOException {
        try {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJson(exchange, 405, "{\"error\":\"POST required\"}");
                return;
            }

            Map<String, String> form = readForm(exchange);

            Purchase purchase = new Purchase(
                    Integer.parseInt(form.get("materialId")),
                    Integer.parseInt(form.get("supplierId")),
                    Double.parseDouble(form.get("quantity")),
                    Double.parseDouble(form.get("unitPrice")),
                    form.get("purchaseDate")
            );

            purchaseDAO.addPurchase(purchase);
            sendJson(exchange, 201, "{\"message\":\"Purchase recorded and stock increased\"}");

        } catch (Exception e) {
            sendError(exchange, e);
        }
    }

    private static void handleUsage(HttpExchange exchange) throws IOException {
        try {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJson(exchange, 405, "{\"error\":\"POST required\"}");
                return;
            }

            Map<String, String> form = readForm(exchange);

            MaterialUsage usage = new MaterialUsage(
                    Integer.parseInt(form.get("materialId")),
                    Double.parseDouble(form.get("quantityUsed")),
                    form.get("projectName"),
                    form.get("usageDate")
            );

            usageDAO.addUsage(usage);
            sendJson(exchange, 201, "{\"message\":\"Usage recorded and stock decreased\"}");

        } catch (Exception e) {
            sendError(exchange, e);
        }
    }

    private static Map<String, String> readForm(HttpExchange exchange)
            throws IOException {

        String body = new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8);

        Map<String, String> form = new HashMap<>();

        for (String pair : body.split("&")) {
            if (pair.isBlank()) continue;

            String[] parts = pair.split("=", 2);
            String key = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
            String value = parts.length > 1
                    ? URLDecoder.decode(parts[1], StandardCharsets.UTF_8)
                    : "";

            form.put(key, value);
        }

        return form;
    }

    private static void sendJson(
            HttpExchange exchange, int status, String json)
            throws IOException {

        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type", "application/json; charset=UTF-8");

        exchange.sendResponseHeaders(status, bytes.length);

        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }

    private static void sendError(
            HttpExchange exchange, Exception e)
            throws IOException {

        e.printStackTrace();

        String message = e.getMessage() == null
                ? "Something went wrong"
                : e.getMessage();

        sendJson(exchange, 400,
                "{\"error\":\"" + JsonUtil.escape(message) + "\"}");
    }

    private static void serveStatic(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        if (path.equals("/")) {
            path = "/index.html";
        }

        Path file = WEB_ROOT.resolve(path.substring(1)).normalize();

        if (!file.startsWith(WEB_ROOT) || !Files.exists(file)
                || Files.isDirectory(file)) {
            sendText(exchange, 404, "Page not found");
            return;
        }

        String contentType = getContentType(file);
        byte[] data = Files.readAllBytes(file);

        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(200, data.length);

        try (OutputStream out = exchange.getResponseBody()) {
            out.write(data);
        }
    }

    private static String getContentType(Path file) {
        String name = file.getFileName().toString().toLowerCase();

        if (name.endsWith(".html")) return "text/html; charset=UTF-8";
        if (name.endsWith(".css")) return "text/css; charset=UTF-8";
        if (name.endsWith(".js")) return "application/javascript; charset=UTF-8";

        return "application/octet-stream";
    }

    private static void sendText(
            HttpExchange exchange, int status, String text)
            throws IOException {

        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type", "text/plain; charset=UTF-8");

        exchange.sendResponseHeaders(status, bytes.length);

        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }
}
