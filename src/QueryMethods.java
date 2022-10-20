import org.apache.poi.ss.usermodel.Row;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class QueryMethods {
    private static final List<ValuesToAdd> repeatedvaluesToAdd = new ArrayList<>();
    private static final List<ValuesToAdd> nonRepeatedvaluesToAdd = new ArrayList<>();
    private static final ArrayList<Mineral> minerals = new ArrayList<>();
    private static final Map<Integer, String> valuesToInsert = new HashMap<>();
    private static final List<String> LOCALIDAD = Arrays.asList("localidad","estado");
    private static final List<Integer> LOCALIDADHEADERS = Arrays.asList(8, 7);

    public static Consumer<Row> insertaDatos(Connection connection, Map<Integer, String> headersMap, List<Integer> headersToCheck, List<Integer> headersWithoutCheck, Map<String, String> columnsToCheck) {
        return row -> {
            headersWithoutCheck.forEach(header -> valuesToInsert.put(header, row.getCell(header).toString()));

            headersToCheck.stream()
                    .filter(column -> !LOCALIDADHEADERS.contains(column))
                    .forEach(header -> getInsertValues(connection,
                    columnsToCheck.get(headersMap.get(header)),
                    row.getCell(header).getStringCellValue(),
                    header));


            List<Integer> localidadHeaders = headersToCheck.stream()
                    .filter(LOCALIDADHEADERS::contains)
                    .sorted(Comparator.reverseOrder())
                    .collect(Collectors.toList());

            localidadHeaders.forEach(header -> {
                try {
                    getInsertLocalidades(connection,
                                    columnsToCheck.get(headersMap.get(header)),
                                    row.getCell(header).getStringCellValue(),
                                    header);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            if (!repeatedvaluesToAdd.isEmpty())
                repeatedvaluesToAdd.forEach(values -> valuesToInsert.put(values.getColumNumber(), values.getColumId().toString()));

            List<ValuesToAdd> localidad = nonRepeatedvaluesToAdd.stream()
                    .filter(column -> LOCALIDAD.contains(column.getColumnName()))
                    .sorted(Comparator.comparing(ValuesToAdd::getColumnName))
                    .collect(Collectors.toList());

            nonRepeatedvaluesToAdd.forEach(column -> {
                try {
                    if (!LOCALIDAD.contains(column.getColumnName()))
                        column.setColumId(insertNonRepeatedValues(connection,
                                column.getColumnName(),
                                column.getColumnValue()));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            localidad.forEach(column -> {
                try {
                    insertLocalidades(connection, column.getColumnName(), column.getColumnValue(), column);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            });

            if (!nonRepeatedvaluesToAdd.isEmpty())
                nonRepeatedvaluesToAdd.forEach(values -> valuesToInsert.put(values.getColumNumber(), values.getColumId().toString()));

            minerals.add(new Mineral(valuesToInsert.get(0), Integer.parseInt(valuesToInsert.get(1)), valuesToInsert.get(2), valuesToInsert.get(3), Integer.parseInt(valuesToInsert.get(4)),
                    valuesToInsert.get(5), valuesToInsert.get(6), Integer.parseInt(valuesToInsert.get(7)), Integer.parseInt(valuesToInsert.get(8)), Integer.parseInt(valuesToInsert.get(9)),
                    Integer.parseInt(valuesToInsert.get(10)), Integer.parseInt(valuesToInsert.get(11)), parseIfNotNull(valuesToInsert.get(12)), parseIfNotNull(valuesToInsert.get(13)),
                    parseIfNotNull(valuesToInsert.get(14)), valuesToInsert.get(15), Integer.parseInt(valuesToInsert.get(16)), valuesToInsert.get(17), Integer.parseInt(valuesToInsert.get(18)),
                    valuesToInsert.get(19), valuesToInsert.get(20)));

            minerals.toString();
            repeatedvaluesToAdd.clear();
            nonRepeatedvaluesToAdd.clear();
            valuesToInsert.clear();
        };
    }

    private static void insertValuesMinerales(Connection connection) throws SQLException {
        String sqlQuery = "insert into minerales values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
            connection.setAutoCommit(false);
            minerals.forEach(mineral -> {
                try {
                    pstmt.setString(1, mineral.getNumero());
                    pstmt.setInt(2, mineral.getIdClasificacion());
                    pstmt.setString(3, mineral.getVariedad());
                    pstmt.setString(4, mineral.getFormulaQuimica());
                    pstmt.setInt(5, mineral.getIdSistemaCristalizacion());
                    pstmt.setString(6, mineral.getMineralAsociados());
                    pstmt.setString(7, mineral.getMatriz());
                    pstmt.setInt(8, mineral.getIdLocalidad());
                    pstmt.setInt(9, mineral.getIdEstado());
                    pstmt.setInt(10, mineral.getIdPais());
                    pstmt.setInt(11, mineral.getIdClaseQuimica());
                    pstmt.setInt(12, mineral.getIdGrupo());
                    pstmt.setDouble(13,mineral.getLargo());
                    pstmt.setDouble(14,mineral.getAlto());
                    pstmt.setDouble(15,mineral.getAncho());
                    pstmt.setString(16,mineral.getNotasCampo());
                    pstmt.setInt(17, mineral.getIdUbicacion());
                    pstmt.setString(18,mineral.getObservaciones());
                    pstmt.setInt(19, mineral.getIdColector());
                    pstmt.setString(20,mineral.getEstadoEjemplar());
                    pstmt.setString(21,mineral.getFechaIngreso());
                    pstmt.addBatch();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            int[] result = pstmt.executeBatch();
            System.out.println("The number of rows inserted: " + result.length);
            //connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
            connection.rollback();
        }
    }

    private static int insertNonRepeatedValues(Connection connection, String tableName, String value) throws SQLException {
        String sqlQuery = "insert into " + tableName + " values (null, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
            connection.setAutoCommit(false);
            pstmt.setString(1, value);
            boolean result = pstmt.execute();
            System.out.println("Row inserted: "+ result);
            connection.commit();

            sqlQuery = "SELECT LAST_INSERT_ID()";
            PreparedStatement pstmt2 = connection.prepareStatement(sqlQuery);
            ResultSet resultSet = pstmt2.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
            connection.rollback();
        }
        return 0;
    }

    private static void insertLocalidades(Connection connection, String tableName, String value, ValuesToAdd column) throws SQLException {
        if(tableName.equals("estado")){
            column.setColumId(insertEstado(connection, value));
        }
        else{
            column.setColumId(insertLocalidad(connection, value));
        }
    }

    private static int insertLocalidad(Connection connection, String value) throws SQLException{
        String sqlQuery = "call agregaLocalidad(?, ?, ?)";
        int idPais = getIdColumn("pais");
        int idEstado = getIdColumn("estado");

        try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
            connection.setAutoCommit(false);
            pstmt.setString(1, value);
            pstmt.setInt(2, idEstado);
            pstmt.setInt(3, idPais);
            boolean result = pstmt.execute();
            System.out.println("Row inserted: "+ result);
            connection.commit();

            sqlQuery = "select idLocalidad from localidad where idPais=? and idEstado=? order by idLocalidad desc limit 1";
            PreparedStatement pstmt2 = connection.prepareStatement(sqlQuery);
            pstmt2.setInt(1, idPais);
            pstmt2.setInt(2, idEstado);
            ResultSet resultSet = pstmt2.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
            connection.rollback();
        }
        return 0;
    }

    private static int insertEstado(Connection connection, String value) throws SQLException{
        String sqlQuery = "call agregaEstado(?, ?)";
        int idPais = getIdColumn("pais");

        try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
            connection.setAutoCommit(false);
            pstmt.setString(1, value);
            pstmt.setInt(2, idPais);
            boolean result = pstmt.execute();
            System.out.println("Row inserted: "+ result);
            connection.commit();

            sqlQuery = "select idEstado from estado where idPais=? order by idEstado desc limit 1";
            PreparedStatement pstmt2 = connection.prepareStatement(sqlQuery);
            pstmt2.setInt(1, idPais);
            ResultSet resultSet = pstmt2.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
            connection.rollback();
        }
        return 0;
    }

    private static int getIdColumn(String column) {
        ValuesToAdd idRepetido = repeatedvaluesToAdd.stream().filter(columnValue -> columnValue.getColumnName().equals(column)).findFirst().orElse(null);
        ValuesToAdd idSinRepetir = nonRepeatedvaluesToAdd.stream().filter(columnValue -> columnValue.getColumnName().equals(column)).findFirst().orElse(null);
        if (idRepetido!=null)
        {
            return idRepetido.getColumId();
        }
        else
        {
            assert idSinRepetir != null;
            return idSinRepetir.getColumId();
        }
    }

    private static void getInsertValues(Connection connection, String table, String columnValue, int headerNum){
        Map<String, Integer> columnMap = new HashMap<>();

        try (PreparedStatement statement = connection.prepareStatement(
            "SELECT * \n" +
            "FROM " + table)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                columnMap.put(resultSet.getString(2), resultSet.getInt(1));
            }
            Integer columnId;
            if (headerNum==9) {
                if (columnValue.equals("")) {
                    columnValue = "Desconocido";
                }
            }
            if (columnMap.containsKey(columnValue)) {
                columnId = columnMap.get(columnValue);
                repeatedvaluesToAdd.add(new ValuesToAdd(headerNum, columnId, columnValue, table));
            } else {
                nonRepeatedvaluesToAdd.add(new ValuesToAdd(headerNum, null, columnValue, table));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void getInsertLocalidades(Connection connection, String table, String columnValue, int headerNum) throws SQLException {

        ValuesToAdd pais = repeatedvaluesToAdd.stream().filter(values -> values.getColumnName().equals("pais")).findFirst().orElse(null);
        ValuesToAdd estado = null;
        if(pais!=null && pais.getColumId()!=null) {
            if(table.equals("estado")) {
                getEstado(LOCALIDADHEADERS.get(0), connection, pais.getColumId(), columnValue);
            }
            else {
                estado = repeatedvaluesToAdd.stream().filter(values -> values.getColumnName().equals("estado")).findFirst().orElse(null);
                if (estado!=null && estado.getColumId()!=null) {
                    getLocalidad(LOCALIDADHEADERS.get(1), connection, pais.getColumId(), estado.getColumId(), columnValue);
                }
                else{
                    nonRepeatedvaluesToAdd.add(new ValuesToAdd(headerNum, null, columnValue, table));
                }
            }
        }
        else{
            nonRepeatedvaluesToAdd.add(new ValuesToAdd(headerNum, null, columnValue, table));
        }
    }

    private static void getEstado(int header, Connection connection, int idPais, String columnValue) throws SQLException {

        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * \n" +
                        "FROM " + "estado")) {
            ResultSet resultSet = statement.executeQuery();

            ArrayList<String> values = new ArrayList<>();
            ArrayList<Integer> estadosId = new ArrayList<>();
            ArrayList<Integer> paisesId = new ArrayList<>();

            while (resultSet.next()) {
                values.add(resultSet.getString(2));
                estadosId.add(resultSet.getInt(1));
                paisesId.add(resultSet.getInt(3));
            }

            if (columnValue.equals("")) {
                    columnValue = "Desconocido";
            }
            Integer columnId;
            ValuesToAdd estado;
            if (paisesId.contains(idPais) && values.contains(columnValue)) {
                columnId = estadosId.get(paisesId.indexOf(idPais));
                repeatedvaluesToAdd.add(new ValuesToAdd(header, columnId, columnValue, "estado"));
            } else {
                nonRepeatedvaluesToAdd.add(new ValuesToAdd(header, null, columnValue, "estado"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void getLocalidad(int header, Connection connection, int idPais, int idEstado, String columnValue) throws SQLException {

        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * \n" +
                        "FROM " + "localidad")) {
            ResultSet resultSet = statement.executeQuery();

            ArrayList<String> values = new ArrayList<>();
            Map<Integer, Map<Integer, Integer>> localidadesMap = new HashMap<>();

            while (resultSet.next()) {
                values.add(resultSet.getString(2));
                Map<Integer, Integer> estadoMap = new HashMap<>();
                estadoMap.put(4, 1);
                localidadesMap.put(resultSet.getInt(3), estadoMap);
            }

            if (columnValue.equals("")) {
                columnValue = "Desconocido";
            }
            Integer columnId;

            if (localidadesMap.containsKey(idPais) && localidadesMap.get(idPais).containsKey(idEstado) && values.contains(columnValue)) {
                columnId = localidadesMap.get(idPais).get(idEstado);
                repeatedvaluesToAdd.add(new ValuesToAdd(header, columnId, columnValue, "localidad"));
            } else {
                nonRepeatedvaluesToAdd.add(new ValuesToAdd(header, null, columnValue, "localidad"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static Double parseIfNotNull(String number){
        return !number.isEmpty() ? Double.parseDouble(number) : null;
    }
}
