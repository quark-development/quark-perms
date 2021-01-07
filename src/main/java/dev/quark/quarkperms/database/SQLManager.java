package dev.quark.quarkperms.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.quark.quarkperms.QuarkPerms;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Getter @Setter
public class SQLManager {

    private Connection connection;
    private String url, host, port;

    private HikariDataSource source;
    private HikariConfig config = new HikariConfig();

    private PreparedStatement ps = null;
    private ResultSet rs = null;

    private final QuarkPerms core = QuarkPerms.getInstance();
    private final YamlConfiguration details = core.getConfigManager().getFile("db").getConfig();

    public SQLManager() {
        init();
    }

    protected void init() {
        config.setUsername(details.getString("SQL.user"));
        config.setPassword(details.getString("SQL.password"));
        config.addDataSourceProperty( "cachePrepStmts" , "true" );
        config.addDataSourceProperty( "prepStmtCacheSize" , "250" );
        config.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" );
        config.setMaxLifetime(50000);

        url = details.getString("SQL.database");
        host = details.getString("SQL.host");
        port = details.getString("SQL.port");

        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + url);

        try {
            establishConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        execStatement("CREATE TABLE IF NOT EXISTS `player-data` (`uuid` VARCHAR(36), `ranks` TEXT, `permissions` TEXT)");

    }

    private void establishConnection() throws SQLException {
        source = new HikariDataSource(config);
        connection = source.getConnection();
    }

    public void execUpdate(String update) {
        try {
            ps = connection.prepareStatement(update);
            ps.executeUpdate();
        } catch (SQLException e) {
            core.getLogger().severe(e.getMessage());
        } finally {
            try {
                if (rs != null && !rs.isClosed()) rs = null;
                if (ps != null && !ps.isClosed()) ps = null;
            } catch (SQLException e) {
                core.getLogger().severe(e.getMessage());
            }
        }
    }

    public ResultSet execQuery(String query) {
        try {
            ps = connection.prepareStatement(query);
            rs = ps.executeQuery();
            return rs;
        } catch (SQLException e) {
            core.getLogger().severe(e.getMessage());
        } finally {
            try {
                if (rs != null && !rs.isClosed()) rs = null;
                if (ps != null && !ps.isClosed()) ps = null;
            } catch (SQLException e) {
                core.getLogger().severe(e.getMessage());
            }
        }
        return null;
    }

    public void execStatement(String statement) {
        try {
            ps = connection.prepareStatement(statement);
            ps.execute();
        } catch (SQLException e) {
            core.getLogger().severe(e.getMessage());
        } finally {
            try {
                if (rs != null && !rs.isClosed()) rs = null;
                if (ps != null && !ps.isClosed()) ps = null;
            } catch (SQLException e) {
                core.getLogger().severe(e.getMessage());
            }
        }
    }

}
