import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private static Map<Integer, String> otherColumns = new HashMap<>();

    private static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException, InvalidFormatException {
        Connection connection = MariaDbConnection.createConnection();

        try {
            columnsToCheck = mapper.readValue(new File("columns.json")
                    , new TypeReference<List<Map<String, ArrayList<LinkedHashMap<String, String>>>>>(){}).get(0).get("columns").get(0);
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

        //excelReadData.readData();

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

       // headersMap.forEach();

        ArrayList<Row> rowList = excelReadData.getRows();

        importWithoutCheck.forEach(header -> otherColumns.put(header,rowList.get(92).getCell(header).toString()));

        //ArrayList<Row> rowsToCheck = rowList.stream()

        //System.out.println(rowList.get(0).getCell(0).getStringCellValue());

        //headersMap.forEach((key, value) -> );

        headersToCheck.forEach(header -> getInsertValues(connection,
                (String) columnsToCheck.get(headersMap.get(header)),
                rowList.get(92).getCell(header).getStringCellValue(),
                header));

//        valuesToAdd.entrySet().forEach(integerMapEntry -> {
//            Integer columNumber = integerMapEntry.getValue().
//            otherColumns.put()
//        });

        repeatedvaluesToAdd.toString();
        otherColumns.toString();
        nonRepeatedvaluesToAdd.toString();
        //getInsertValues(connection, headersMap.get(0), rowList.get(0).getCell(0).getStringCellValue());

        //System.out.println(rowList.get(0).);

//        rowList.get(2).cellIterator().forEachRemaining(cell -> {
//            if (!cell.getStringCellValue().isEmpty())
//                System.out.println(cell.getStringCellValue());
//        });




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
            Map<Integer, String> values = new HashMap<>();
            if(columnMap.containsKey(columnValue)) {
                columnId = columnMap.get(columnValue);
                repeatedvaluesToAdd.add(new ValuesToAdd(headerNum, columnId, columnValue));
            }
            else {
                columnId = columnMap.get(columnValue);
                nonRepeatedvaluesToAdd.add(new ValuesToAdd(headerNum, columnId, columnValue));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}