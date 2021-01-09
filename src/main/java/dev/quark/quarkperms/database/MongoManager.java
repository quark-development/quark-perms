package dev.quark.quarkperms.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import dev.quark.quarkperms.QuarkPerms;
import dev.quark.quarkperms.framework.config.ConfigurationFile;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MongoManager {

    private final QuarkPerms core = QuarkPerms.getInstance();
    private final ConfigurationFile config = core.getConfigManager().getFile("db.yml");

    private MongoDatabase mongoDatabase;

    public MongoManager() {
        if (config.getBoolean("MONGO.auth.enabled")) {
            mongoDatabase = new MongoClient(
                    new ServerAddress(
                            config.getString("MONGO.host"),
                            config.getInteger("MONGO.port")
                    ),
                    MongoCredential.createCredential(
                            config.getString("MONGO.auth.user"),
                            "admin",
                            config.getString("MONGO.auth.password").toCharArray()
                    ),
                    MongoClientOptions.builder().build()
            ).getDatabase(config.getString("MONGO.database"));

        } else {
            mongoDatabase = new MongoClient(
                    config.getString("MONGO.host"),
                    config.getInteger("MONGO.port")
            ).getDatabase(config.getString("MONGO.database"));
        }
    }

}
