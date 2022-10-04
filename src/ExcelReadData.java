import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ExcelReadData
{
    private static Workbook wb;

    public ExcelReadData(String pathfile) throws IOException, InvalidFormatException {
        wb = WorkbookFactory.create(new File(pathfile));
    }

    public Map<Integer, String> getHeaders() {
        Sheet sheet = wb.getSheetAt(0);
        // Get the first row from the sheet
        Row row = sheet.getRow(0);

// Create a List to store the header data
        Map<Integer, String> headerData = new HashMap<>();
        int rowNum = 0;
// Iterate cells of the row and add data to the List
        for (Cell cell : row) {
            headerData.put(rowNum,cell.getStringCellValue());
            rowNum++;
        }

        return headerData;
    }

    public ArrayList<Row> getRows(){
        try
        {
            Sheet sheet = wb.getSheetAt(0);     //creating a Sheet object to retrieve object
            ArrayList<Row> rowsList = new ArrayList<>();
            //iterating over Excel file
            sheet.forEach(rowsList::add);
            rowsList.remove(0);
            return rowsList;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}