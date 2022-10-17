import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static Map<String, String> columnsToCheck = new HashMap<>();

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
        if(connection==null) {
            System.out.println("Conexion fallida");
            return;
        }

        excelReadData = new ExcelReadData("MalpicaPrueba.xlsx");

        Map<Integer, String> headersMap = excelReadData.getHeaders();

        List<Integer> headersToCheck = headersMap.entrySet().stream()
                .filter(integerStringEntry -> columnsToCheck.containsKey(integerStringEntry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        List<Integer> headersWithoutCheck = headersMap.entrySet().stream()
                .filter(integerStringEntry -> !columnsToCheck.containsKey(integerStringEntry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        ArrayList<Row> rowList = excelReadData.getRows();

        rowList.forEach(QueryMethods.insertaDatos(connection, headersMap, headersToCheck, headersWithoutCheck, columnsToCheck));

        connection.close();
    }
}