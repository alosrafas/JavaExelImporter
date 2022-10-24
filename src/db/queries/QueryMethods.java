package db.queries;

import models.Localidad;
import models.Mineral;
import models.ValuesToAdd;
import org.apache.poi.ss.usermodel.Row;

import java.sql.*;
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

    public static Consumer<Row> insertaDatos(Connection connection, Map<Integer, String> headersMap, List<Integer> headersToCheck, List<Integer> headersWithoutCheck, Map<String, String> columnsToCheck) throws SQLException {
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

            repeatedvaluesToAdd.clear();
            nonRepeatedvaluesToAdd.clear();
            valuesToInsert.clear();
        };
    }

    public static void insertValuesMinerales(Connection connection) throws SQLException {
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
                    if(mineral.getLargo()!=null)
                        pstmt.setDouble(13,mineral.getLargo());
                    else
                        pstmt.setNull(13, Types.DOUBLE);
                    if (mineral.getAlto()!=null)
                        pstmt.setDouble(14,mineral.getAlto());
                    else
                        pstmt.setNull(14, Types.DOUBLE);
                    if (mineral.getAncho()!=null)
                        pstmt.setDouble(15,mineral.getAncho());
                    else
                        pstmt.setNull(15, Types.DOUBLE);
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
            System.out.println("Numero de renglones insertados: " + result.length);
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("No se pudieron insertar los valores en la tabla Minerales ");
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
        if (table.equals("pais")) {
            if (columnValue.equals("")) {
                columnValue = "Desconocido";
            }
        }
        try (PreparedStatement statement = connection.prepareStatement(
            "SELECT * \n" +
            "FROM " + table +
            "WHERE nombre = ?")) {

            statement.setString(1, columnValue);
        
            ResultSet resultSet = statement.executeQuery();
            int columnId;

            if (resultSet.next()==true){
                columnId = resultSet.getInt(1);
                repeatedvaluesToAdd.add(new ValuesToAdd(headerNum, columnId, columnValue, table));
            }
            else
                nonRepeatedvaluesToAdd.add(new ValuesToAdd(headerNum, null, columnValue, table));
            
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
        if (columnValue.equals("")) {
            columnValue = "Desconocido";
        }
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * \n" +
                "FROM estado" +
                "WHERE idPais=?" +
                "AND nombre=?")
            ) {
            statement.setInt(1, idPais);
            statement.setString(2, columnValue);
            ResultSet resultSet = statement.executeQuery();

            Integer columnId;
            if(resultSet.next()){
                columnId = resultSet.getInt(1);
                repeatedvaluesToAdd.add(new ValuesToAdd(header, columnId, columnValue, "estado"));
            }
            else
                nonRepeatedvaluesToAdd.add(new ValuesToAdd(header, null, columnValue, "estado"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void getLocalidad(int header, Connection connection, int idPais, int idEstado, String columnValue) throws SQLException {
        if (columnValue.equals("")) {
            columnValue = "Desconocido";
        }
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * \n" +
                "FROM " + "localidad" +
                "WHERE idPais=?" +
                "AND idEstado=?" +
                "AND nombre=?")
            ) {
            statement.setInt(1, idPais);
            statement.setInt(2, idEstado);
            statement.setString(3, columnValue);

            ResultSet resultSet = statement.executeQuery();

            ArrayList<Localidad> localidades = new ArrayList<>();
            int localidadId;

            if(resultSet.next()){
                localidadId = resultSet.getInt(1);
                repeatedvaluesToAdd.add(new ValuesToAdd(header, localidadId, columnValue, "localidad"));
            }
            else
                nonRepeatedvaluesToAdd.add(new ValuesToAdd(header, null, columnValue, "localidad"));         
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static Double parseIfNotNull(String number){
        return !number.isEmpty() ? Double.parseDouble(number) : null;
    }
}
