package manager;
import java.util.ArrayList;

import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.hibernate.HibernateException;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import model.Bodega;
import model.Campo;
import model.Entrada;
import model.Vid;

public class Manager {
    private static Manager manager;
    private List<Entrada> entradas;
    private List<Campo> campos;
    private Bodega b;
    MongoDatabase database;
    MongoCollection<Document> collection;
    private ArrayList<Entrada> inputs;

    private Manager() {
        this.entradas = new ArrayList<>();
        this.campos = new ArrayList<>();
        this.inputs = new ArrayList<>();
    }

    public static Manager getInstance() {
        if (manager == null) {
            manager = new Manager();
        }
        return manager;
    }

    public void init() {
        createSession();
        getInputData();
        manageActions();
    }

    private void createSession() {
        String uri = "mongodb://localhost:27017";
        MongoClientURI mongoClientUri = new MongoClientURI(uri);
        MongoClient mongoClient = new MongoClient(mongoClientUri);
        database = mongoClient.getDatabase("ViticultureMongoDB");
    }

    
    private void manageActions() {
        for (Entrada entrada : inputs) {
            try {
                System.out.println(entrada.getInstruccion());
                switch (entrada.getInstruccion().toUpperCase().split(" ")[0]) {
                    case "B":
                    	addWinery(entrada.getInstruccion().split(" "));
                        break;
                    case "C":
                        addCampo(entrada.getInstruccion().split(" "), false);
                        break;
                    case "V":
                        addVid(entrada.getInstruccion().split(" "));
                        break;
                    case "M":
                        markAsVendimiado(entrada.getInstruccion().split(" "));
                        break;
                    case "#":
                        vendimia();
                        break;
                    default:
                        System.out.println("Instrucción incorrecta");
                }
            } catch (HibernateException e) {
                e.printStackTrace();
               System.out.println("Valiste burguer");
            }
        }
        
    }
    
    
    public ArrayList<Entrada> getInputData() {
        MongoCollection<Document> collection = database.getCollection("entrada");

        for (Document document : collection.find()) {
            Entrada input = new Entrada();
            input.setId(document.getObjectId("_id").toString());
            input.setInstruccion(document.getString("instruccion"));
            inputs.add(input);
        }
        System.out.println(inputs);
        return inputs;
    }

    public void addWinery(String[] parts) {
        if (parts.length >= 2) {
            String nombre = parts[1];
            Bodega bodega = new Bodega();
            bodega.setName(nombre);
            collection = database.getCollection("bodega");
            Document document = new Document();
            document.put("name", bodega.getName());
            collection.insertOne(document);
        } else {
            System.out.println("La instrucción no tiene el formato esperado.");
        }
    }
    
    private void addCampo(String[] split, boolean vendimiado) {
        try {
            String lastBodegaId = getLastBodegaId();
            Document document = new Document();
            document.put("vendimiado", vendimiado); // Establecer el valor de vendimiado
            collection = database.getCollection("campo");
            collection.insertOne(document);
            System.out.println("Campo agregado correctamente.");
            String nuevoCampoId = document.getObjectId("_id").toString();
            Campo nuevoCampo = new Campo();
            nuevoCampo.setId(nuevoCampoId);
            nuevoCampo.setVendimiado(vendimiado); // Establecer el valor de vendimiado en el objeto Campo
            campos.add(nuevoCampo);
        } catch (Exception e) {
            System.out.println("Error al agregar el campo: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private String getLastBodegaId() {
        MongoCollection<Document> bodegaCollection = database.getCollection("bodega");  
        Document lastBodega = bodegaCollection.find().sort(Sorts.descending("_id")).first();
        if (lastBodega != null) {
           
            String lastBodegaId = lastBodega.getObjectId("_id").toString();
            return lastBodegaId;
        } else {
            return null; 
        }
    }
    
    private String getLastCampoId() {
        MongoCollection<Document> campoCollection = database.getCollection("campo");
        Document lastCampo = campoCollection.find().sort(Sorts.descending("_id")).first();
        if (lastCampo != null) {
            String lastCampoId = lastCampo.getObjectId("_id").toString();
            return lastCampoId;
        } else {
            return null;
        }
    }
    
    private void addVid(String[] split) {
        if (campos.isEmpty()) {
            System.out.println("No hay campos registrados para agregar vid.");
            return;
        }

        String tipoVidStr = split[1].toLowerCase();
        int tipoVid;
        if (tipoVidStr.equals("blanca")) {
            tipoVid = 0;
        } else if (tipoVidStr.equals("negra")) {
            tipoVid = 1;
        } else {
            System.out.println("Tipo de vid no válido.");
            return;
        }
        int cantidad = Integer.parseInt(split[2]);      
        String campoId = getLastCampoId();       
        String bodegaId = getLastBodegaId();

        Document vidDocument = new Document("tipo_vid", tipoVid)
                                        .append("cantidad", cantidad)
                                        .append("campo_id", campoId)
                                        .append("bodegaId", bodegaId); 

        MongoCollection<Document> vidCollection = database.getCollection("vid");
        vidCollection.insertOne(vidDocument);
        System.out.println("Documento de vid agregado correctamente a la colección 'vid'.");
    }


    private void vendimia() {
        String bodegaId = getLastBodegaId();
        MongoCollection<Document> vidCollection = database.getCollection("vid");
        Bson filter = new Document("bodegaId", new Document("$exists", false)); 
        Bson update = Updates.set("bodegaId", bodegaId);
        UpdateResult updateResult = vidCollection.updateMany(filter, update);
        System.out.println("Número de documentos de vid actualizados: " + updateResult.getModifiedCount());
    }
    private void markAsVendimiado(String[] parts) {
        if (parts.length >= 2) {
            String campoId = parts[1];
           
            // Crear el filtro para buscar el campo por su ID
            Bson filter = Filters.eq("_id", new ObjectId(campoId));
           
            // Crear la actualización para establecer "vendimiado" en true
            Bson update = Updates.set("vendimiado", true);
           
            // Ejecutar la actualización en la colección de campos
            MongoCollection<Document> campoCollection = database.getCollection("campo");
            UpdateResult updateResult = campoCollection.updateOne(filter, update);
           
            // Verificar si se realizó la actualización correctamente
            if (updateResult.getModifiedCount() > 0) {
                System.out.println("Campo marcado como vendimiado correctamente.");
            } else {
                System.out.println("No se encontró ningún campo con el ID proporcionado.");
            }
        } else {
            System.out.println("La instrucción no tiene el formato esperado.");
        }
    }
}

