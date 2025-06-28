package com.tbear9.openfarm.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import lombok.Getter;

public class Plants {
    @Getter private Connection connection;
    public boolean startConnection(){
        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/plants",
                    "root",
                    "1234"
            );
            connection.createStatement().execute("CREATE TABLE IF NOT EXIST plantlist(" +
                    "current_page int default 1 NOT NULL UNIQUE," +
                    " data JSON" +
                    ")"
            );
            connection.createStatement().execute("CREATE TABLE IF NOT EXIST plantdetails(" +
                    ""
            );
            return connection.createStatement().execute(
                    "CREATE TABLE IF NOT EXIST plantlist("+
                            "id int NOT NULL, "+
                            "common_name varchar(255) NOT NULL, "+
                            "scientific_name varchar(255) NOT NULL, "+
                            "family varchar(255), "+
                            "hybrid varchar(255), "+
                            "authority varchar(255), "+
                            "subspecies varchar(255), "+
                            "cultivar varchar(255), "+
                            "variety varchar(255), "+
                            "species_epithet varchar(255), " +
                            "genus varhcar(255)," +
                            ""
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
