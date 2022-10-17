import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.collections4.iterators.ReverseListIterator;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    private static Map<String, String> columnsToCheck = new HashMap<>();
    private static Map<Integer, ValuesToAdd> valuesToAdd = new HashMap<>();

    private static List<ValuesToAdd> repeatedvaluesToAdd = new ArrayList<>();
    private static List<ValuesToAdd> nonRepeatedvaluesToAdd = new ArrayList<>();

    private static Map<Integer, String> valuesToInsert = new HashMap<>();

    private static ObjectMapper mapper = new ObjectMapper();

    private static ArrayList<Mineral> minerals = new ArrayList<>();

    private static List<String> LOCALIDAD = Arrays.asList("localidad","estado");

    public static void main(String[] args) throws IOException, InvalidFormatException, SQLException {
        Connection connection = MariaDbConnection.createConnection();
        try {
            columnsToCheck = mapper.readValue(new File("columns.json")
                    , new TypeReference<Map<String, ArrayList<LinkedHashMap<String, String>>>>(){}).get("columns").get(0);
        }
        catch (DatabindException e){
            e.printStackTrace();
        }
        ExcelReadData excelReadData;
        if(connection!=null)
            excelReadData = new ExcelReadData("MalpicaNew.xlsx");
        else
            System.out.println("Conexion fallida");

        excelReadData = new ExcelReadData("MalpicaNew.xlsx");

        columnsToCheck.toString();
        Map<Integer, String> headersMap = excelReadData.getHeaders();

        List<Integer> headersToCheck = headersMap.entrySet().stream()
                .filter(integerStringEntry -> columnsToCheck.containsKey(integerStringEntry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        List<Integer> importWithoutCheck = headersMap.entrySet().stream()
                .filter(integerStringEntry -> !columnsToCheck.containsKey(integerStringEntry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());


        ArrayList<Row> rowList = excelReadData.getRows();

        importWithoutCheck.forEach(header -> valuesToInsert.put(header,rowList.get(92).getCell(header).toString()));


        // rowList.forEach(row -> {
        //     importWithoutCheck.forEach(header -> valuesToInsert.put(header,row.getCell(header).toString()));

        //     headersToCheck.forEach(header -> getInsertValues(connection,
        //     (String) columnsToCheck.get(headersMap.get(header)),
        //     row.getCell(header).getStringCellValue(),
        //     header));

        //     nonRepeatedvaluesToAdd.clear();
        //     repeatedvaluesToAdd.clear();
        // });


        headersToCheck.forEach(header -> getInsertValues(connection,
                (String) columnsToCheck.get(headersMap.get(header)),
                rowList.get(92).getCell(header).getStringCellValue(),
                header));

        valuesToInsert.toString();
        nonRepeatedvaluesToAdd.toString();

        if (!repeatedvaluesToAdd.isEmpty())
            repeatedvaluesToAdd.forEach(values -> valuesToInsert.put(values.getColumNumber(), values.getColumId().toString()));

        List<ValuesToAdd> localidad = nonRepeatedvaluesToAdd.stream()
        .filter(column -> LOCALIDAD.contains(column.getColumnName()))
        .sorted()
        .collect(Collectors.toList());

        Collections.reverse(localidad);

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

        System.out.println(repeatedvaluesToAdd.toString());



        minerals.add(new Mineral(valuesToInsert.get(0), Integer.parseInt(valuesToInsert.get(1)), valuesToInsert.get(2), valuesToInsert.get(3), Integer.parseInt(valuesToInsert.get(4)),
                valuesToInsert.get(5), valuesToInsert.get(6), Integer.parseInt(valuesToInsert.get(7)), Integer.parseInt(valuesToInsert.get(8)), Integer.parseInt(valuesToInsert.get(9)),
                Integer.parseInt(valuesToInsert.get(10)), Integer.parseInt(valuesToInsert.get(11)), parseIfNotNull(valuesToInsert.get(12)), parseIfNotNull(valuesToInsert.get(13)),
                parseIfNotNull(valuesToInsert.get(14)), valuesToInsert.get(15), Integer.parseInt(valuesToInsert.get(16)), valuesToInsert.get(17), Integer.parseInt(valuesToInsert.get(18)),
                valuesToInsert.get(19), valuesToInsert.get(20)));
        minerals.toString();

        assert connection != null;
        connection.close();



    }

    public static void getInsertValues(Connection connection, String table, String columnValue, int headerNum){
        Map<String, Integer> columnMap = new HashMap<>();

        try (PreparedStatement statement = connection.prepareStatement(
            "SELECT * \n" +
            "FROM " + table)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                columnMap.put(resultSet.getString(2), resultSet.getInt(1));
            }
            Integer columnId;
            if ((headerNum==7 || headerNum==8 || headerNum==9) && columnValue.equals("")){
                columnValue="Desconocido";
                if (headerNum==7){
                    if(columnMap.containsKey(columnValue)) {
                        columnId = columnMap.get(columnValue);
                        repeatedvaluesToAdd.add(new ValuesToAdd(headerNum, columnId, columnValue));
                    }
                    else {
                        columnId = columnMap.get(columnValue);
                        nonRepeatedvaluesToAdd.add(new ValuesToAdd(headerNum, columnId, columnValue, table));
                    }
                    return;
                }
                else {
                    if(columnMap.containsKey(columnValue)) {
                        columnId = columnMap.get(columnValue);
                        repeatedvaluesToAdd.add(new ValuesToAdd(headerNum, columnId, columnValue));
                    }
                    else {
                        columnId = columnMap.get(columnValue);
                        nonRepeatedvaluesToAdd.add(new ValuesToAdd(headerNum, columnId, columnValue, table));
                    }
                    return;
                }
            }
            if(columnMap.containsKey(columnValue)) {
                columnId = columnMap.get(columnValue);
                repeatedvaluesToAdd.add(new ValuesToAdd(headerNum, columnId, columnValue));
            }
            else {
                columnId = columnMap.get(columnValue);
                nonRepeatedvaluesToAdd.add(new ValuesToAdd(headerNum, columnId, columnValue, table));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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

    private static int insertNonRepeatedValues(Connection connection, String tableName,  String value) throws SQLException {
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

    private static void insertLocalidades(Connection connection, String tableName,  String value, ValuesToAdd column) throws SQLException {
        if(tableName.equals("estado")){
            column.setColumId(insertEstado(connection, tableName, value));
        }
        else{
            column.setColumId(insertLocalidad(connection, tableName, value));
        }
    }

    private static int insertLocalidad(Connection connection, String tableName,  String value) throws SQLException{
        String sqlQuery = "insert into " + tableName + " values (null, ?, ?)";
        int idPais;
        ValuesToAdd paisRepetido = repeatedvaluesToAdd.stream().filter(column -> column.getColumnName().equals("pais")).findFirst().orElse(null);
        ValuesToAdd paisSinRepetir = nonRepeatedvaluesToAdd.stream().filter(column -> column.getColumnName().equals("pais")).findFirst().orElse(null);
        if (paisRepetido!=null)
        {
            idPais = paisRepetido.getColumId();
        }
        else
        {
            idPais = paisSinRepetir.getColumId();
        }

        try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
            connection.setAutoCommit(false);
            pstmt.setString(1, value);
            pstmt.setInt(2, idPais);
            boolean result = pstmt.execute();
            System.out.println("Row inserted: "+ result);
            connection.commit();

            sqlQuery = "select * from localidad where idPais=? and idEstado=? order by idLocalidad desc limit 1";
            PreparedStatement pstmt2 = connection.prepareStatement(sqlQuery);
            pstmt2.setInt(0, idPais);
            ResultSet resultSet = pstmt2.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
            connection.rollback();
        }
        return 0;
    }

    private static int insertEstado(Connection connection, String tableName,  String value) throws SQLException{
        String sqlQuery = "insert into " + tableName + " values (null, ?, ?)";
        int idPais;
        ValuesToAdd paisRepetido = repeatedvaluesToAdd.stream().filter(column -> column.getColumnName().equals("pais")).findFirst().orElse(null);
        ValuesToAdd paisSinRepetir = nonRepeatedvaluesToAdd.stream().filter(column -> column.getColumnName().equals("pais")).findFirst().orElse(null);
        if (paisRepetido!=null)
        {
            idPais = paisRepetido.getColumId();
        }
        else
        {
            idPais = paisSinRepetir.getColumId();
        }

        try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
            connection.setAutoCommit(false);
            pstmt.setString(1, value);
            pstmt.setInt(2, idPais);
            boolean result = pstmt.execute();
            System.out.println("Row inserted: "+ result);
            connection.commit();

            sqlQuery = "select * from estado where idPais=? order by idEstado desc limit 1";
            PreparedStatement pstmt2 = connection.prepareStatement(sqlQuery);
            pstmt2.setInt(0, idPais);
            ResultSet resultSet = pstmt2.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
            connection.rollback();
        }
        return 0;
    }

    public static Double parseIfNotNull(String number){
        return !number.isEmpty() ? Double.parseDouble(number) : null;
    }

}