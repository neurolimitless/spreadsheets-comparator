package sample;

import javafx.application.Platform;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExcelProcessor {
    private static File firstFile; //Client points
    private static File secondFile; //Employee spreadsheet
    private static Employee currentEmployee;
    private static Map<String, ClientPoints> loadedClientPoints = new TreeMap<>();
    private static Map<ClientPoints, ClientPoints> difference = new HashMap<>();
    private static XSSFWorkbook differenceSpreadsheet;
    private static XSSFWorkbook clientPoints;
    private static XSSFWorkbook employeeSpreadsheet;
    private static String lastPath;

    public static boolean isReady() {
        return firstFile != null && secondFile != null;
    }

    public static String getLastPath() {
        return lastPath;
    }

    public static void setLastPath(String lastPath) {
        ExcelProcessor.lastPath = lastPath;
    }


    public static void execute() {
        try {
            if (difference.size() != 0) difference.clear();
            readClientPoints(firstFile);
            showEmployeeData(secondFile);
            saveLastPath();
            calculateDifference();
            saveDifference();
            Menu.changeVisible();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Menu.notifyUser();
                }
            });
        } catch (IOException | InvalidFormatException | CloneNotSupportedException | NullPointerException e) {
            String errorMessage = e.getMessage();
            if (errorMessage != null) e.printStackTrace();
        }
    }

    private static void saveLastPath() throws IOException {
        File file = new File("config.ini");
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(ExcelProcessor.getLastPath());
        fileWriter.flush();
        fileWriter.close();
    }

    public static void setFirstFile(File firstFile) {
        if (firstFile != null) {
            ExcelProcessor.firstFile = firstFile;
            lastPath = firstFile.getParent();
            System.out.println("First file loaded: " + firstFile.getName());
        }
    }

    public static void setSecondFile(File secondFile) {
        if (secondFile != null) {
            ExcelProcessor.secondFile = secondFile;
            lastPath = secondFile.getParent();
            System.out.println("Second file loaded: " + secondFile.getName());
        }
    }


    public static void showEmployeeData(File file) throws IOException, InvalidFormatException {
        employeeSpreadsheet = new XSSFWorkbook(new FileInputStream(file));
        readInfoTab();
        readEmployeeTabs();
        currentEmployee.convertTaskPoints();
    }

    //reading tech number + first and second name
    private static void readInfoTab() {
        XSSFSheet firstTab = employeeSpreadsheet.getSheetAt(0);
        XSSFRow techNumber = firstTab.getRow(0);
        XSSFRow firstName = firstTab.getRow(1);
        XSSFRow secondName = firstTab.getRow(2);
        String techNum = techNumber.getCell(1).getRawValue();

        String fName = firstName.getCell(1).getStringCellValue();
        String sName = secondName.getCell(1).getStringCellValue();
        currentEmployee = new Employee((int) Double.parseDouble(techNum), fName, sName);
    }

    //reading all tabs and adding to user task-list
    private static void readEmployeeTabs() {
        int tabCount = 7; //count of employee tabs exclude info tab.
        int taskCount = 9; //must be count of tasks + 1.
        for (int i = 1; i <= tabCount; i++) {
            XSSFSheet currentSheet = employeeSpreadsheet.getSheetAt(i);
            for (int k = 2; k <= taskCount; k++) { //reading all rows in current tab
                XSSFRow currentRow = currentSheet.getRow(k); // +1 because first two rows are headers of sheet
                String accountId = currentRow.getCell(0).getStringCellValue().trim();  //reading AccountID
                String address = currentRow.getCell(1).getStringCellValue();  //reading Address
                //j=2 because we already read first two cells
                int[] points = new int[33];
                for (int j = 2; j < currentRow.getPhysicalNumberOfCells(); j++) { //reading all points
                    int currentTaskPoint = 0;
                    if (currentRow.getCell(j).getRawValue() != null)
                        currentTaskPoint = (int) Double.parseDouble(currentRow.getCell(j).getRawValue());
                    points[j - 2] = currentTaskPoint;
                }
                currentEmployee.addTask(new Task(address, accountId, points));
            }
        }
    }


    public static void readClientPoints(File file) throws IOException {
        clientPoints = new XSSFWorkbook(new FileInputStream(file));
        XSSFSheet mainSheet = clientPoints.getSheetAt(0); //reading main sheet
        int tasks = mainSheet.getPhysicalNumberOfRows();
        int endingOfSpreadsheet = 3;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM.dd.yy");
        for (int i = 1; i < tasks - endingOfSpreadsheet; i++) {
            XSSFRow currentRow = mainSheet.getRow(i);
            ClientPoints clientPoints = new ClientPoints();
            int k = 0;
            clientPoints.setPrin(String.valueOf((int) Double.parseDouble(currentRow.getCell(k++).getRawValue())));
            clientPoints.setOrder(currentRow.getCell(k++).getStringCellValue());
            clientPoints.setJob(String.valueOf((int) Double.parseDouble(currentRow.getCell(k++).getRawValue())));
            clientPoints.setAccount(currentRow.getCell(k++).getStringCellValue());
            clientPoints.setName(currentRow.getCell(k++).getStringCellValue());
            clientPoints.setAddress(currentRow.getCell(k++).getStringCellValue());
            Date date = currentRow.getCell(k++).getDateCellValue();
            clientPoints.setCompldate(simpleDateFormat.format(date));
            clientPoints.setCode(currentRow.getCell(k++).getStringCellValue());
            clientPoints.setType(currentRow.getCell(k++).getStringCellValue());
            clientPoints.setTech(currentRow.getCell(k++).getStringCellValue());
            int[] tPoints = new int[33];
            int count = 0;
            for (int j = k; j < k + 33; j++) {
                try {
                    tPoints[count] = (int) Double.parseDouble(currentRow.getCell(j).getRawValue());
                    count++;
                } catch (NullPointerException e) {
                    tPoints[count] = 0;
                    count++;
                }
            }
            k += 33;
            clientPoints.setPoints(tPoints);
            clientPoints.setEqAdded(currentRow.getCell(k++).getStringCellValue());
            clientPoints.setEqRemoved(currentRow.getCell(k++).getStringCellValue());
            clientPoints.setCurEquip(currentRow.getCell(k++).getStringCellValue());
            clientPoints.setTotalPoints((int) Double.parseDouble(currentRow.getCell(k++).getRawValue()));
            loadedClientPoints.put(clientPoints.getAccount(), clientPoints);
        }
    }


    public static void calculateDifference() throws CloneNotSupportedException {
        System.out.println("Calculating");
        //saving all accountID's to arrayList
        List<String> accountIDs = new ArrayList<>();
        for (int i = 0; i < currentEmployee.getTaskCount(); i++) {
            accountIDs.add(currentEmployee.getTasks().get(i).getAccountID().trim());
        }
        System.out.println("User loaded tasks: " + accountIDs.size());
        System.out.println("Client task count: " + loadedClientPoints.size());
        for (String accountID : accountIDs) {
            for (Map.Entry<String, ClientPoints> entry : loadedClientPoints.entrySet()) {
                if (accountID.equals(entry.getKey())) {
                    ClientPoints clientPointsDifference = entry.getValue();
                    ClientPoints clientPointsOrigin = (ClientPoints) clientPointsDifference.clone();
                    int[] clientPoints = clientPointsOrigin.getPoints();
                    int[] userPoints = currentEmployee.getPointsByAccountId(accountID);
                    int[] differencePoints = new int[33];
                    int totalDif = 0;
                    for (int i = 0; i < 33; i++) {
                        if ((clientPoints[i] - userPoints[i]) < 0) {
                            differencePoints[i] = Math.abs(clientPoints[i] - userPoints[i]);
                            totalDif += differencePoints[i];
                        }
                    }
                    int originalTotal = clientPointsOrigin.getTotalPoints();
                    if (originalTotal < totalDif)
                        clientPointsDifference.setTotalPoints(Math.abs(originalTotal - totalDif));
                    clientPointsDifference.setTotalPoints(totalDif);
                    clientPointsDifference.setPoints(differencePoints);
                    //adding pair to map.
                    difference.put(clientPointsDifference, clientPointsOrigin);
                    break;
                }
            }
        }
        System.out.println("Finished");
    }

    public static void saveDifference() throws IOException {
        if (differenceSpreadsheet == null) {
            //creating new file with header.
            differenceSpreadsheet = new XSSFWorkbook();
            XSSFSheet currentSheet = differenceSpreadsheet.createSheet();
            ClientPoints clientPoints = new ClientPoints("Prin", "Order #", "Job #", "Account #",
                    "Name", "Address", "Compl Date", "Compl Code", "Job Type",
                    "Tech #", new int[0], "Equipment Added", "Equipment Removed", "Current equip",
                    0);
            XSSFRow currentRow = currentSheet.createRow(0); //header row
            for (int i = 0; i < 47; i++) {
                currentRow.createCell(i);
                XSSFCellStyle style = differenceSpreadsheet.createCellStyle();
                style.setAlignment(CellStyle.ALIGN_CENTER);
                style.setFillBackgroundColor(IndexedColors.PALE_BLUE.getIndex());
                style.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                style.setFillPattern(CellStyle.ALIGN_FILL);
                currentRow.getCell(i).setCellStyle(style);
            }
            setCellValue(clientPoints.getPrin(), currentRow.getCell(0));
            setCellValue(clientPoints.getOrder(), currentRow.getCell(1));
            setCellValue(clientPoints.getJob(), currentRow.getCell(2));
            setCellValue(clientPoints.getAccount(), currentRow.getCell(3));
            setCellValue(clientPoints.getName(), currentRow.getCell(4));
            setCellValue(clientPoints.getAddress(), currentRow.getCell(5));
            setCellValue(clientPoints.getCompldate(), currentRow.getCell(6));
            setCellValue(clientPoints.getCode(), currentRow.getCell(7));
            setCellValue(clientPoints.getType(), currentRow.getCell(8));
            setCellValue(clientPoints.getTech(), currentRow.getCell(9));
            for (int i = 0; i < 33; i++) {
                setCellValue("Task " + (i + 1), currentRow.getCell(i + 10));
            }
            setCellValue(clientPoints.getEqAdded(), currentRow.getCell(43));
            setCellValue(clientPoints.getEqRemoved(), currentRow.getCell(44));
            setCellValue(clientPoints.getCurEquip(), currentRow.getCell(45));
            setCellValue("Total Task Point", currentRow.getCell(46));
            saveDifference(); //after creating header -> fill spreadsheet with differences.
        } else if (difference != null && difference.size() > 0) {
            //appending
            XSSFSheet currentSheet = differenceSpreadsheet.getSheetAt(0);
            for (Map.Entry<ClientPoints, ClientPoints> entry : difference.entrySet()) {
                XSSFRow currentRow = currentSheet.createRow(currentSheet.getPhysicalNumberOfRows());
                ClientPoints clientPoints = entry.getValue();
                ClientPoints differencePoints = entry.getKey();
                saveToTable(clientPoints, currentRow);
                for (int i = 0; i < 33; i++) {
                    if (differencePoints.getPoint(i) != 0) {
                        currentRow = currentSheet.createRow(currentSheet.getPhysicalNumberOfRows());
                        saveToTable(differencePoints, currentRow);
                        formatDifferenceRow(currentRow);
                        break;
                    }
                }
            }
        }
        setAutoWidth();
    }

    private static void setAutoWidth() {
        XSSFRow row = differenceSpreadsheet.getSheetAt(0).getRow(0); //selecting header
        for (int i = 0; i < row.getLastCellNum(); i++) {
            differenceSpreadsheet.getSheetAt(0).autoSizeColumn(i);
        }
    }

    private static void formatDifferenceRow(XSSFRow row) {
        XSSFCellStyle style = differenceSpreadsheet.createCellStyle();
        XSSFCellStyle differenceStyle = differenceSpreadsheet.createCellStyle();
        XSSFFont font = differenceSpreadsheet.createFont();
        style.setFillBackgroundColor(IndexedColors.SKY_BLUE.getIndex());
        style.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        style.setFillPattern(XSSFCellStyle.ALIGN_FILL);
        font.setColor(IndexedColors.RED.getIndex());
        differenceStyle.setFont(font);
        differenceStyle.setFillBackgroundColor(IndexedColors.SKY_BLUE.getIndex());
        differenceStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        differenceStyle.setFillPattern(XSSFCellStyle.ALIGN_FILL);
        for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {
            XSSFCell cell = row.getCell(j);
            if (j >= 10 && j <= 42) {
                cell.setCellStyle(differenceStyle);
            } else {
                cell.setCellStyle(style);
            }
        }
    }


    private static void saveToTable(ClientPoints clientPoints, XSSFRow currentRow) {
        for (int i = 0; i < 47; i++) {
            currentRow.createCell(i);
        }
        setCellValue(clientPoints.getPrin(), currentRow.getCell(0));
        setCellValue(clientPoints.getOrder(), currentRow.getCell(1));
        setCellValue(clientPoints.getJob(), currentRow.getCell(2));
        setCellValue(clientPoints.getAccount(), currentRow.getCell(3));
        setCellValue(clientPoints.getName(), currentRow.getCell(4));
        setCellValue(clientPoints.getAddress(), currentRow.getCell(5));
        setCellValue(clientPoints.getCompldate(), currentRow.getCell(6));
        setCellValue(clientPoints.getCode(), currentRow.getCell(7));
        setCellValue(clientPoints.getType(), currentRow.getCell(8));
        setCellValue(clientPoints.getTech(), currentRow.getCell(9));
        for (int i = 0; i < 33; i++) {
            if (clientPoints.getPoint(i) != 0)
                setCellValue(String.valueOf(clientPoints.getPoint(i)), currentRow.getCell(i + 10));
            else setCellValue("", currentRow.getCell(i + 10));
        }
        setCellValue(clientPoints.getEqAdded(), currentRow.getCell(43));
        setCellValue(clientPoints.getEqRemoved(), currentRow.getCell(44));
        setCellValue(clientPoints.getCurEquip(), currentRow.getCell(45));
        setCellValue(String.valueOf(clientPoints.getTotalPoints()), currentRow.getCell(46));
    }

    public static void saveOutput() {
        try {
            if (differenceSpreadsheet != null) {
                File file = new File("difference.xlsx");
                if (file.exists()) file = new File(new Random().nextInt(250000) + ".xlsx");
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                differenceSpreadsheet.write(fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                System.out.println("Saved to file.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void setCellValue(String value, XSSFCell cell) {
        cell.setCellValue(value);
    }
}

