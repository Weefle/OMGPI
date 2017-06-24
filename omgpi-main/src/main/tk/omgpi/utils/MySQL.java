package tk.omgpi.utils;

import java.sql.*;

/**
 * Connects to and uses a MySQL database.
 *
 * @author -_Husky_-
 * @author tips48
 * @author BurnyDaKath - additions
 */
public class MySQL {
    public Connection connection;
    public String user;
    public String database;
    public String password;
    public String port;
    public String hostname;

    /**
     * Checks if a connection is open with the database
     *
     * @return true if the connection is open
     * @throws SQLException if the connection cannot be checked
     */
    public boolean checkConnection() throws SQLException {
        return connection != null && !connection.isClosed();
    }

    /**
     * Closes the connection with the database
     *
     * @return true if successful
     * @throws SQLException if the connection cannot be closed
     */
    public boolean closeConnection() throws SQLException {
        if (connection == null) {
            return false;
        }
        connection.close();
        return true;
    }

    /**
     * Creates a new MySQL instance for a specific database
     *
     * @param hostname Name of the host
     * @param port     Port number
     * @param database Database name
     * @param username Username
     * @param password Password
     */
    public MySQL(String hostname, String port, String database,
                 String username, String password) {
        this.hostname = hostname;
        this.port = port;
        this.database = database;
        this.user = username;
        this.password = password;
        mysql = this;
    }

    /**
     * Opens a connection with the database
     *
     * @return Opened connection
     * @throws SQLException if the connection can not be opened
     */
    public Connection openConnection() throws SQLException {
        if (checkConnection()) {
            return connection;
        }

        String connectionURL = "jdbc:mysql://"
                + this.hostname + ":" + this.port;
        if (database != null) {
            connectionURL = connectionURL + "/" + this.database + "?autoReconnect=true";
        }

        connection = DriverManager.getConnection(connectionURL, this.user, this.password);
        return connection;
    }

    public static MySQL mysql;

    public static void onDisable() {
        try {
            if (mysql != null) mysql.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Try and reconnect to the mysql server.
     *
     * @return True if success
     */
    public static boolean reconnect() {
        try {
            if (!mysql.checkConnection()) {
                mysql.closeConnection();
                mysql.openConnection();
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Add a key to a table. (or does not if key is present)
     *
     * @param checkColumn Column to check for key
     * @param key         Key to specify row
     * @param table       Table to add player to
     * @return Success, or player is present.
     */
    public static boolean add(String checkColumn, Object key, String table) {
        if (!reconnect()) throw new NullPointerException("Cannot connect to server");
        Statement statement;
        try {
            statement = mysql.connection.createStatement();
            ResultSet res = statement.executeQuery("SELECT * FROM " + table + " WHERE " + checkColumn + " = '" + key + "';");
            res.next();
            if (res.getString(checkColumn) != null) res.getObject(checkColumn);
            statement.close();
            return true;
        } catch (SQLException e) {
            try {
                statement = mysql.connection.createStatement();
                statement.executeUpdate("INSERT INTO " + table + " (`" + checkColumn + "`) VALUES ('" + key + "');");
                statement.close();
                return true;
            } catch (SQLException e2) {
                e2.printStackTrace();
                return false;
            }
        }
    }

    /**
     * Set player data in the table.
     * Does 2 attempts: First is UPDATE, which checks if player is already added; Second is INSERT, which adds player first.
     *
     * @param checkColumn Column to check for key
     * @param key         Key to specify row
     * @param setColumn   Column to set value in
     * @param value       Value to set
     * @param table       Table name
     * @return True if success.
     */
    public static boolean set(String checkColumn, Object key, String setColumn, Object value, String table) {
        if (!reconnect()) throw new NullPointerException("Cannot connect to server");
        Statement statement;
        try {
            statement = mysql.connection.createStatement();
            ResultSet res = statement.executeQuery("SELECT * FROM " + table + " WHERE " + checkColumn + " = '" + key + "';");
            res.next();
            if (res.getString(checkColumn) != null) res.getObject(setColumn);
            statement.executeUpdate("UPDATE " + table + " SET `" + checkColumn + "`='" + key + "', `" + setColumn + "`='" + value + "' WHERE " + checkColumn + " = '" + key + "';");
            statement.close();
            return true;
        } catch (SQLException e) {
            try {
                statement = mysql.connection.createStatement();
                statement.executeUpdate("INSERT INTO " + table + " (`" + checkColumn + "`, `" + setColumn + "`) VALUES ('" + key + "', '" + value + "');");
                statement.close();
                return true;
            } catch (SQLException e2) {
                e2.printStackTrace();
                return false;
            }
        }
    }

    /**
     * Reset data in SQL by given key in check-column.
     *
     * @param checkColumn Column to check for key
     * @param key         Key to specify row
     * @param setColumn   Column to reset value in
     * @param table       Table name
     * @return True if successful
     */
    public static boolean reset(String checkColumn, Object key, String setColumn, String table) {
        if (!reconnect()) throw new NullPointerException("Cannot connect to server");
        Statement statement;
        try {
            statement = mysql.connection.createStatement();
            ResultSet res = statement.executeQuery("SELECT * FROM " + table + " WHERE " + checkColumn + " = '" + key + "';");
            res.next();
            if (res.getString(checkColumn) != null) res.getObject(setColumn);
            statement.executeUpdate("UPDATE " + table + " SET `" + checkColumn + "`='" + key + "', `" + setColumn + "`=NULL WHERE " + checkColumn + " = '" + key + "';");
            statement.close();
            return true;
        } catch (SQLException e) {
            try {
                statement = mysql.connection.createStatement();
                statement.executeUpdate("INSERT INTO " + table + " (`" + checkColumn + "`, `" + setColumn + "`) VALUES ('" + key + "', NULL);");
                statement.close();
                return true;
            } catch (SQLException e2) {
                e2.printStackTrace();
                return false;
            }
        }
    }

    /**
     * Get data from SQL by given key in check-column.
     *
     * @param checkColumn Column to check for key
     * @param key         Key to specify row
     * @param getColumn   Column to get value from
     * @param table       Table name
     * @return Value
     */
    public static Object get(String checkColumn, Object key, String getColumn, String table) {
        if (!reconnect()) throw new NullPointerException("Cannot connect to server");
        Statement statement;
        Object result;
        try {
            statement = mysql.connection.createStatement();
            ResultSet res = statement.executeQuery("SELECT * FROM " + table + " WHERE " + checkColumn + " = '" + key + "';");
            if (res.next()) {
                if (res.getString(checkColumn) == null) result = null;
                else result = res.getObject(getColumn);
                statement.close();
                return result;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
