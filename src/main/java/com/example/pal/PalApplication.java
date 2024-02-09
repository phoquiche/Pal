package com.example.pal;

import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.config.YamlProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class PalApplication {
    public static void main(String[] args) {
        SpringApplication.run(PalApplication.class, args);

    }
}

@RestController
@RequestMapping("/api")
class MongoDbConnectController {
    @GetMapping(path = "/mongo")
    public String connectToMongoDb() {
       String result = afficherPal();
        return result;
    }
    @RequestMapping("/insert")
    public String insertData() {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("Pal");
        MongoCollection<Document> collection = database.getCollection("pals");
        int typeInt = (int)( Math.random()*10);
        if (typeInt % 2 == 0) {
            typeInt = 1;
        } else {
            typeInt = 2;
        }
        if (typeInt == 1) {
            Document document = new Document("name", "Pal"+Math.random())
                    .append("type", "Feu");
            collection.insertOne(document);
            return "Data inserted: \n"+ document.toJson();
        } else {
            Document document = new Document("name", "Pal"+Math.random())
                    .append("type", "Eau") ;
            collection.insertOne(document);
            return "Data inserted: \n"+ document.toJson();
        }

    }

    @RequestMapping("/dataupdate")
    public String updateData() {
        //afficher les noms des pals dans une liste deroulante html, on peut changer le type
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("Pal");
        MongoCollection<Document> collection = database.getCollection("pals");
        FindIterable<Document> iterDoc = collection.find();
        String result = "<form action=\"/api/update\" method=\"post\"><select name=\"name\">";
        for (Document doc : iterDoc) {
            result += "<option value=\""+doc.get("name")+"\">"+doc.get("name")+"</option>";
        }
        //list défilante de type
        result+= "</select><select name=\"type\"><option value=\"Feu\">Feu</option><option value=\"Eau\">Eau</option></select><input type=\"submit\" value=\"Submit\"></form>";
        return generateNavbar()+ result;
    }

    @RequestMapping("/update")
    public String updateData(@RequestBody String info) {
        //recuperer les données post
        String name = info.split("&")[0].split("=")[1];
        String types = info.split("&")[1].split("=")[1];
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("Pal");
        MongoCollection<Document> collection = database.getCollection("pals");
        collection.updateOne(Filters.eq("name", name), new Document("$set", new Document("types", types)));
        return generateNavbar()+"Data update "+ types+"   " + name+ "  " + info;
    }

    @RequestMapping("/findbyid")
    public String findDataById() {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("Pal");
        MongoCollection<Document> collection = database.getCollection("pals");
        FindIterable<Document> iterDoc = collection.find();
        String result = "<form action=\"/api/infopal\" method=\"post\"><select name=\"id\">";
        for (Document doc : iterDoc) {
            result += "<option value=\""+doc.get("_id")+"\">"+doc.get("id")+"</option>";
        }
        result+= "</select><input type=\"submit\" value=\"Submit\"></form>";
        return generateNavbar()+ result;
    }

    @RequestMapping("/infopal")
    public String infoPal(@RequestBody String info){
        String id = info.split("&")[0].split("=")[1];
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("Pal");
        MongoCollection<Document> collection = database.getCollection("pals");
        Document myDoc = collection.find(Filters.eq("_id", new ObjectId(id))).first();
        return generateNavbar()+"Nom: "+myDoc.get("name")+" Type: "+myDoc.get("types");
    }

    @RequestMapping("/findbyname")
    public String findDataByName() {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("Pal");
        MongoCollection<Document> collection = database.getCollection("pals");
        FindIterable<Document> iterDoc = collection.find();
        String result = "<form action=\"/api/infopalbyname\" method=\"post\"><select name=\"name\">";
        for (Document doc : iterDoc) {
            result += "<option value=\""+doc.get("name")+"\">"+doc.get("name")+"</option>";
        }
        result+= "</select><input type=\"submit\" value=\"Submit\"></form>";
        return generateNavbar()+ result;
    }

    @RequestMapping("/infopalbyname")
    public String infoPalByName(@RequestBody String info){
        String name = info.split("&")[0].split("=")[1];
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("Pal");
        MongoCollection<Document> collection = database.getCollection("pals");
        Document myDoc = collection.find(Filters.eq("name", name)).first();
        return generateNavbar()+"Nom: "+myDoc.get("name")+" Type: "+myDoc.get("types");
    }

    @RequestMapping("/getbytype")
    public String getByType() {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("Pal");
        MongoCollection<Document> collection = database.getCollection("pals");

        DistinctIterable tst = collection.distinct("types", String.class);
        List<String> list = new ArrayList<>();
        for (Object doc : tst) {
            if (doc != null) {
                list.add(doc.toString());


            }
        }
        String result = "<form action=\"/api/infopalbytype\" method=\"post\"><select name=\"type\">";
        for (String doc : list) {
            result += "<option value=\""+doc+"\">"+doc+"</option>";
        }

        result+= "</select><input type=\"submit\" value=\"Submit\"></form>";
        return generateNavbar()+ result;
    }

    @RequestMapping("/infopalbytype")
    public String getByType(@RequestBody String info){
        String type = info.split("&")[0].split("=")[1];
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("Pal");
        MongoCollection<Document> collection = database.getCollection("pals");
        FindIterable<Document> iterDoc = collection.find(Filters.eq("types", type));
        String result = "<table border=1><tr><th>Nom</th><th>Type</th><th>Skills</tr>";
        for (Document doc : iterDoc) {
            result += "<tr><td>" + doc.get("name") + "</td><td>" + doc.get("types") + "</td><td>";
            ArrayList<String> skills = new ArrayList<>();
            skills.add(doc.get("skills").toString());
            for (String skill : skills) {
                result += skill + " ";
            }
            result += "</td></tr>";


        }
        result += "</table>";
        return generateNavbar()+ result;
    }

    public String afficherPal(){
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("Pal");
        MongoCollection<Document> collection = database.getCollection("pals");
        FindIterable<Document> iterDoc = collection.find();
        StringBuilder result = new StringBuilder("<table border=1><tr><th>Nom</th><th>Type</th><th>Image</th></tr>");
        for (Document doc : iterDoc) {
            result.append("<tr><td>").append(doc.get("name")).append("</td><td>").append(doc.get("types")).append("</td><td><img src=\"").append(doc.get("imageWiki")).append("\"></td>");

        }



        result.append("</tr></table>");
        return generateNavbar()+ result;
    }

    public String generateNavbar(){
        return "  <link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css\" rel=\"stylesheet\">\n" +
                "  <script src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js\"></script>" +
                "</head><nav class=\"navbar navbar-expand-sm bg-success\">\n" +
                "\n" +
                "  <div class=\"container-fluid\">\n" +
                "    <ul class=\"navbar-nav\">\n" +
                "      <li class=\"nav-item\">\n" +
                "        <a class=\"nav-link\" href=\"#\">PalAPI</a>\n" +
                "      <li class=\"nav-item\">\n" +
                "        <a class=\"nav-link\" href=\"/api/mongo\">Accueil</a>\n" +
                "      </li>\n" +
                "      <li class=\"nav-item\">\n" +
                "        <a class=\"nav-link\" href=\"/api/dataupdate\">Mettre à jour le type</a>\n" +
                "      </li>\n" +
                "      <li class=\"nav-item\">\n" +
                "        <a class=\"nav-link\" href=\"/api/findbyid\">Afficher par ID</a>\n" +
                "      </li>\n" +
                "      <li class=\"nav-item\">\n" +
                "        <a class=\"nav-link\" href=\"/api/findbyname\">Afficher par nom</a>\n" +
                "      </li>\n" +
                "      <li class=\"nav-item\">\n" +
                "        <a class=\"nav-link\" href=\"/api/getbytype\">Afficher par type</a>\n" +
                "      </li>\n" +
                "    </ul>\n" +
                "  </div>\n" +
                "\n" +
                "</nav><br>";}



}




