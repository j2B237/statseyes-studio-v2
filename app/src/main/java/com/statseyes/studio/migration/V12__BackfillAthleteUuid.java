package com.statseyes.studio.migration;

import com.statseyes.studio.infrastructure.persistence.id.UuidV7;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class V12__BackfillAthleteUuid extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        Connection c = context.getConnection();
        List<Integer> ids = new ArrayList<>();

        try (
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery("SELECT id FROM athletes WHERE uuid IS NULL")
        ){
                while (rs.next()) {
                    // Get everything before writing ops
                    ids.add(rs.getInt(1));
                }
        }
        try (
            PreparedStatement ps = c.prepareStatement(
                    "UPDATE athletes SET uuid = ? WHERE id = ?")
        ){
            for (Integer id : ids){
                ps.setObject(1, UuidV7.generate());
                ps.setInt(2, id);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
}
