/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.qreator.vertx;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.ext.web.sstore.SessionStore;

public class VertxWebFormular {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);
        SessionStore store = LocalSessionStore.create(vertx);

        SessionHandler sessionHandler = SessionHandler.create(store);

        router.route().handler(CookieHandler.create());
        router.route().handler(sessionHandler);

        router.route("/anfrage").handler(routingContext -> {

            String typ = routingContext.request().getParam("typ");
            String name = routingContext.request().getParam("name");
            String passwort = routingContext.request().getParam("passwort");
            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "application/json");
            JsonObject jo = new JsonObject();
            
            Session session = routingContext.session();
            session.put("alter", "27");
            String alter=session.get("alter");

            if (typ.equals("namenKnopf") && passwort.equals("safetyfirst")) {
                jo.put("typ", "antwort");
                jo.put("text", "Der Benutzer ist " + name);
                jo.put("passwort", "Das Passwort war " + passwort);
            } else {
                jo.put("text", "Volltrottel: " + name);
                jo.put("passwort", " Falsches Passwort");
            }
            response.end(Json.encodePrettily(jo));
        });

        // statische html-Dateien werden über den Dateipfad static ausgeliefert
        router.route("/static/*").handler(StaticHandler.create().setDefaultContentEncoding("UTF-8"));

        // router::accept akzeptiert eine Anfrage und leitet diese an den Router weiter
        server.requestHandler(router::accept).listen(8080);
    }
}
