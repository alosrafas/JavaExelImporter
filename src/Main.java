import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    private static Map<String,String> columnsToCheck = new HashMap<>();
    private static Map<Integer, String> valuesToAdd = new HashMap<>();
    private static Map<Integer, String> nonRepeatedvaluesToAdd = new HashMap<>();

    public static void main(String[] args) throws IOException, InvalidFormatException {
        Connection connection = MariaDbConnection.createConnection();

        columnsToCheck.put("Clasificación", "clasificacion");
        columnsToCheck.put("Sistema de Cristalización" ,"sistemaCristalizacion");
        columnsToCheck.put("Localidad" ,"localidad");
        columnsToCheck.put("Estado" ,"estado");
        columnsToCheck.put("País" ,"pais");
        columnsToCheck.put("Clase Química" ,"claseQuimica");
        columnsToCheck.put("Grupo" ,"grupo");
        columnsToCheck.put("Ubicación" ,"ubicacion");
        columnsToCheck.put("Colector Donador" ,"colectorDonador");

        ExcelReadData excelReadData;
        if(connection!=null)
            excelReadData = new ExcelReadData("MalpicaNew.xlsx");
        else
            System.out.println("Conexion fallida");

        excelReadData = new ExcelReadData("MalpicaNew.xlsx");

        //excelReadData.readData();


        Map<Integer, String> headersMap = excelReadData.getHeaders();

        List<Integer> headersToCheck = headersMap.entrySet().stream()
                .filter(integerStringEntry -> columnsToCheck.containsKey(integerStringEntry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

       // headersMap.forEach();

        ArrayList<Row> rowList = excelReadData.getRows();

        //ArrayList<Row> rowsToCheck = rowList.stream()

        //System.out.println(rowList.get(0).getCell(0).getStringCellValue());

        //headersMap.forEach((key, value) -> );

        headersToCheck.forEach((header -> getInsertValues(connection, columnsToCheck.get(headersMap.get(header)), rowList.get(0).getCell(header).getStringCellValue())));

        valuesToAdd.toString();
        nonRepeatedvaluesToAdd.toString();
        //getInsertValues(connection, headersMap.get(0), rowList.get(0).getCell(0).getStringCellValue());

        //System.out.println(rowList.get(0).);

//        rowList.get(2).cellIterator().forEachRemaining(cell -> {
//            if (!cell.getStringCellValue().isEmpty())
//                System.out.println(cell.getStringCellValue());
//        });




    }

    public static void getInsertValues(Connection connection, String table, String columnValue){
        Map<String, Integer> columnMap = new HashMap<>();

        try (PreparedStatement statement = connection.prepareStatement(
            "SELECT * \n" +
            "FROM " + table)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                columnMap.put(resultSet.getString(2), resultSet.getInt(1));
            }
            Integer columnId;
            if(columnMap.containsKey(columnValue)) {
                columnId = columnMap.get(columnValue);
                valuesToAdd.put(columnId, columnValue);
            }
            else {
                columnId = columnMap.get(columnValue);
                nonRepeatedvaluesToAdd.put(columnId, columnValue);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}