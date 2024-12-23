package oop.BookList;

import org.apache.log4j.Logger;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;

import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.transform.dom.DOMSource;

import net.sf.jasperreports.engine.data.JRTableModelDataSource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.swing.*;
import javax.xml.transform.TransformerFactory;
import javax.swing.table.DefaultTableModel;

import javax.xml.transform.Transformer;

import net.sf.jasperreports.engine.*;

import java.io.File;
import java.awt.*;

public class App {
    /**
     * The main method to launch the application.
     */
    public void show() {
        initFrame();
        initToolBar();
        initTabbedPane();
        createSearchPanel();
        enableSorting();
        log.info("Application started");
        mainFrame.setVisible(true);
    }

    private static final Logger log = Logger.getLogger(App.class);

    private JScrollPane spozen, spxozein, spmesto, spdog;
    private JComboBox<String> cmbSearch;
    private JTextField txtSearch;
    private JFrame mainFrame;
    private DefaultTableModel mdlozen, mdlxozein, mdlmesto, mdldog;
    private JTable tblozen, tblxozin, tblmesto, tbldog;
    private String[][] origozen, origxozein, origmesto, origdog;
    private JButton btnSave, btnLoad, btnDel, btnTrash, btnReg, btnReset, btnAddRow;
    private JButton btnFilter;

    private void initFrame() {
        mainFrame = new JFrame("Dog Admin");
        mainFrame.setSize(800, 500);
        mainFrame.setLocation(250, 80);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void initToolBar() {
        JToolBar toolBar = new JToolBar(JToolBar.HORIZONTAL);

        toolBar.setLayout(new BoxLayout(toolBar, BoxLayout.X_AXIS));
        btnSave = createButton("./img/saving.png", "Save Tabs");
        btnAddRow = createButton("./img/plRows.png", "Add Row");
        btnLoad = createButton("./img/loading.png", "Add XML");
        btnDel = createButton("./img/delit.png", "Delete Row");
        btnTrash = createButton("./img/Trash.png", "Clear Tab");
        btnReg = createButton("./img/User.png", "Register Owner");
        JButton btnPdf = createButton("./img/PDF.png", "Generate PDF");
        JButton btnHtml = createButton("./img/html.png", "Generate HTML");

        setButtonColors(btnSave, btnAddRow, btnLoad, btnDel, btnTrash, btnReg, btnPdf, btnHtml);

        toolBar.add(btnLoad);
        toolBar.add(btnAddRow);
        toolBar.add(btnDel);
        toolBar.add(btnSave);
        toolBar.add(btnTrash);
        toolBar.add(btnReg);
        toolBar.add(btnPdf);
        toolBar.add(btnHtml);
        addEventListeners(btnPdf, btnHtml);
        mainFrame.setLayout(new BorderLayout());
        mainFrame.add(toolBar, BorderLayout.NORTH);
    }

    private JButton createButton(String iconPath, String toolTip) {
        JButton button = new JButton(new ImageIcon(iconPath));
        button.setPreferredSize(new Dimension(40, 30));
        button.setToolTipText(toolTip);
        return button;
    }

    private void setButtonColors(JButton... buttons) {
        for (JButton button : buttons) {
            button.setBackground(Color.YELLOW);
        }
    }

    private void addEventListeners(JButton pdfBtn, JButton htmlBtn) {
        pdfBtn.addActionListener(e -> generatePDF());
        htmlBtn.addActionListener(e -> generateHTML());
        btnAddRow.addActionListener(e -> addRow());
        btnSave.addActionListener(e -> saveData());
        btnLoad.addActionListener(e -> loadData());
        btnDel.addActionListener(e -> deleteRow());
        btnTrash.addActionListener(e -> clearTab());
        btnReg.addActionListener(e -> showRegDialog());
    }

    private void initTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        initTables();
        tabbedPane.addTab("Оценщики", spozen);
        tabbedPane.addTab("Собачники", spxozein);
        tabbedPane.addTab("Победители", spmesto);
        tabbedPane.addTab("О_собаке", spdog);

        setTabColors(tabbedPane);
        mainFrame.add(tabbedPane, BorderLayout.CENTER);
    }

    private void setTabColors(JTabbedPane tabbedPane) {
        tabbedPane.setBackgroundAt(0, Color.YELLOW);
        tabbedPane.setForegroundAt(0, Color.BLACK);
        tabbedPane.setBackgroundAt(1, Color.YELLOW);
        tabbedPane.setForegroundAt(1, Color.BLACK);
        tabbedPane.setBackgroundAt(2, Color.YELLOW);
        tabbedPane.setForegroundAt(2, Color.BLACK);
        tabbedPane.setBackgroundAt(3, Color.YELLOW);
        tabbedPane.setForegroundAt(3, Color.BLACK);
    }


    private void deleteRow() {
        int selectedTab = ((JTabbedPane) mainFrame.getContentPane().getComponent(1)).getSelectedIndex();
        JTable selectedTable;
        DefaultTableModel selectedModel;
        switch (selectedTab) {
            case 0:
                selectedTable = tblozen;
                selectedModel = mdlozen;
                break;
            case 1:
                selectedTable = tblxozin;
                selectedModel = mdlxozein;
                break;
            case 2:
                selectedTable = tblmesto;
                selectedModel = mdlmesto;
                break;
            case 3:
                selectedTable = tbldog;
                selectedModel = mdldog;
                break;
            default:
                return;
        }
        int selectedRow = selectedTable.getSelectedRow();
        if (selectedRow != -1) {
            selectedModel.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Select a row");
        }
    }

    private void initTables() {
        String[] colsozen = {"Порода_собаки", "Оценщик"};
        mdlozen = new DefaultTableModel(new String[][]{}, colsozen);
        tblozen = new JTable(mdlozen);
        spozen = new JScrollPane(tblozen);

        String[] colsxozein = {"Хозяин", "Кличка_собаки", "Дата_регистрации"};
        mdlxozein = new DefaultTableModel(new String[][]{}, colsxozein);
        tblxozin = new JTable(mdlxozein);
        spxozein = new JScrollPane(tblxozin);

        String[] colsmesto = {"Кличка_собаки", "Занятое_место"};
        mdlmesto = new DefaultTableModel(new String[][]{}, colsmesto);
        tblmesto = new JTable(mdlmesto);
        spmesto = new JScrollPane(tblmesto);

        String[] colsdog = {"Кличка_собаки", "Порода", "День_рождения"};
        mdldog = new DefaultTableModel(new String[][]{}, colsdog);
        tbldog = new JTable(mdldog);
        spdog = new JScrollPane(tbldog);


    }

    private void clearTab() {
        String[] options = {"Оценщики", "Собачники", "Победители", "О_собаке"};
        int tabChoice = JOptionPane.showOptionDialog(
                mainFrame,
                "Select tab to clear data:",
                "Tab Selection",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );
        DefaultTableModel selectedModel;
        switch (tabChoice) {
            case 0:
                selectedModel = mdlozen;
                break;
            case 1:
                selectedModel = mdlxozein;
                break;
            case 2:
                selectedModel = mdlmesto;
                break;
            case 3:
                selectedModel = mdldog;
                break;
            default:
                return;
        }
        int rows = selectedModel.getRowCount();
        for (int i = 0; i < rows; i++) selectedModel.removeRow(0);
    }

    private void addRow() {
        int selectedTab = ((JTabbedPane) mainFrame.getContentPane().getComponent(1)).getSelectedIndex();
        DefaultTableModel selectedModel;
        switch (selectedTab) {
            case 0:
                selectedModel = mdlozen;
                break;
            case 1:
                selectedModel = mdlxozein;
                break;
            case 2:
                selectedModel = mdlmesto;
                break;
            case 3:
                selectedModel = mdldog;
                break;
            default:
                return;
        }
        selectedModel.addRow(new String[selectedModel.getColumnCount()]);
        log.debug("Row added to tab: " + selectedTab);
    }

    private void resetSearch() {
        int selectedIndex = cmbSearch.getSelectedIndex();
        DefaultTableModel model;
        String[][] origData;
        switch (selectedIndex) {
            case 0:
                model = mdlozen;
                origData = origozen;
                break;
            case 1:
                model = mdlxozein;
                origData = origxozein;
                break;
            case 2:
                model = mdlmesto;
                origData = origmesto;
                break;
            case 3:
                model = mdldog;
                origData = origdog;
                break;
            default:
                return;
        }
        if (origData != null) {
            model.setRowCount(0);
            for (String[] row : origData) {
                model.addRow(row);
            }
        }
    }

    private void performSearch() {
        String keyword = txtSearch.getText().trim().toLowerCase();
        log.debug("Searching for: " + keyword);
        int selectedIndex = cmbSearch.getSelectedIndex();
        DefaultTableModel model;
        String[][] origData;
        switch (selectedIndex) {
            case 0:
                model = mdlozen;
                if (origozen == null) origozen = getTableData(model);
                origData = origozen;
                break;
            case 1:
                model = mdlxozein;
                if (origxozein == null) origxozein = getTableData(model);
                origData = origxozein;
                break;
            case 2:
                model = mdlmesto;
                if (origmesto == null) origmesto = getTableData(model);
                origData = origmesto;
                break;
            case 3:
                model = mdldog;
                if (origdog == null) origdog = getTableData(model);
                origData = origdog;
                break;
            default:
                return;
        }
        model.setRowCount(0);
        for (String[] row : origData) {
            boolean match = false;
            for (String cell : row) {
                if (cell.toLowerCase().contains(keyword)) {
                    match = true;
                    break;
                }
            }
            if (match) {
                model.addRow(row);
            }
        }
    }

    private String[][] getTableData(DefaultTableModel model) {
        int rowCount = model.getRowCount();
        int colCount = model.getColumnCount();
        String[][] data = new String[rowCount][colCount];
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < colCount; j++) {
                data[i][j] = (String) model.getValueAt(i, j);
            }
        }
        return data;
    }


    private void enableSorting() {
        tblozen.setAutoCreateRowSorter(true);
        tblxozin.setAutoCreateRowSorter(true);
        tblmesto.setAutoCreateRowSorter(true);
        tbldog.setAutoCreateRowSorter(true);
    }


    private void generateHTML() {
        JDialog dialog = new JDialog(mainFrame, "Select Tabs for HTML Generation", true);
        dialog.setSize(300, 200);
        dialog.setLayout(new GridLayout(0, 1));
        JCheckBox cbozen = new JCheckBox("Оценщики", true);
        JCheckBox cbxozein = new JCheckBox("Собачники", true);
        JCheckBox cbmesto = new JCheckBox("Победители", true);
        JCheckBox cbdog = new JCheckBox("О_собаке", true);
        dialog.add(cbozen);
        dialog.add(cbxozein);
        dialog.add(cbmesto);
        dialog.add(cbdog);
        JButton btnGenerate = new JButton("Generate HTML");
        dialog.add(btnGenerate);
        btnGenerate.addActionListener(e -> {
            try {
                if (cbozen.isSelected() && mdlozen.getRowCount() > 0) {
                    generateHTMLReportForTable("K:\\\\eclipse\\\\ecl_artifact\\\\Kyrs_ignat\\\\DogShowManager\\1.jasper", mdlozen, "Оценщики_Report.html");
                }
                if (cbxozein.isSelected() && mdlxozein.getRowCount() > 0) {
                    generateHTMLReportForTable("свой путь укажешь\\2.jasper", mdlxozein, "Собачники_Report.html");
                }
                if (cbmesto.isSelected() && mdlmesto.getRowCount() > 0) {
                    generateHTMLReportForTable("свой путь укажешь\\3.jasper", mdlmesto, "Победители_Report.html");
                }
                if (cbdog.isSelected() && mdldog.getRowCount() > 0) {
                    generateHTMLReportForTable("свой путь укажешь\\4.jasper", mdldog, "О_собаке_Report.html");
                }
                JOptionPane.showMessageDialog(mainFrame, "HTML reports generated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(mainFrame, "Error generating HTML report: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setVisible(true);
    }


    private void generatePDF() {
        JDialog dialog = new JDialog(mainFrame, "Select Tabs for PDF Generation", true);
        dialog.setSize(300, 200);
        dialog.setLayout(new GridLayout(0, 1));
        JCheckBox cbozen = new JCheckBox("Оценщики", true);
        JCheckBox cbxozein = new JCheckBox("Собачники", true);
        JCheckBox cbmesto = new JCheckBox("Победители", true);
        JCheckBox cbdog = new JCheckBox("О_собаке", true);
        dialog.add(cbozen);
        dialog.add(cbxozein);
        dialog.add(cbmesto);
        dialog.add(cbdog);
        JButton btnGenerate = new JButton("Generate PDF");
        dialog.add(btnGenerate);
        btnGenerate.addActionListener(e -> {
            try {
                if (cbozen.isSelected() && mdlozen.getRowCount() > 0) {
                    generateReport("C:\\Users\\ignat\\IdeaProjects\\OOP-CWORK\\1.jasper", mdlozen, "Оценщики_Report.pdf");
                }
                if (cbxozein.isSelected() && mdlxozein.getRowCount() > 0) {
                    generateReport("C:\\Users\\ignat\\IdeaProjects\\OOP-CWORK\\2.jasper", mdlxozein, "Собачники_Report.pdf");
                }
                if (cbmesto.isSelected() && mdlmesto.getRowCount() > 0) {
                    generateReport("C:\\Users\\ignat\\IdeaProjects\\OOP-CWORK\\3.jasper", mdlmesto, "Победители_Report.pdf");
                }
                if (cbdog.isSelected() && mdldog.getRowCount() > 0) {
                    generateReport("C:\\Users\\ignat\\IdeaProjects\\OOP-CWORK\\4.jasper", mdldog, "О_собаке_Report.pdf");
                }
                JOptionPane.showMessageDialog(mainFrame, "PDF reports generated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(mainFrame, "Error generating PDF report: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setVisible(true);
    }


    private void showRegDialog() {
        JDialog regDialog = new JDialog(mainFrame, "Register Dog Owner", true);
        regDialog.setSize(300, 200);
        regDialog.setLayout(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        JLabel lblName = new JLabel("Name:");
        JTextField txtName = new JTextField();
        JLabel lblPass = new JLabel("Password:");
        JPasswordField txtPass = new JPasswordField();
        inputPanel.add(lblName);
        inputPanel.add(txtName);
        inputPanel.add(lblPass);
        inputPanel.add(txtPass);
        JPanel btnPanel = new JPanel();
        JButton btnReg = new JButton("Register");
        JButton btnCancel = new JButton("Cancel");
        Dimension btnSize = new Dimension(120, 30);
        btnReg.setPreferredSize(btnSize);
        btnCancel.setPreferredSize(btnSize);
        btnPanel.add(btnReg);
        btnPanel.add(btnCancel);
        regDialog.add(inputPanel, BorderLayout.CENTER);
        regDialog.add(btnPanel, BorderLayout.SOUTH);
        btnReg.addActionListener(e -> {
            String name = txtName.getText();
            String pass = new String(txtPass.getPassword());
            if (name.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(regDialog, "Fields cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!name.matches("[a-zA-Zа-яА-Я]+")) {
                JOptionPane.showMessageDialog(regDialog, "Name must contain only letters.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(regDialog, "Registration successful!", "Info", JOptionPane.INFORMATION_MESSAGE);
            regDialog.dispose();
        });
        btnCancel.addActionListener(e -> regDialog.dispose());
        regDialog.setLocationRelativeTo(mainFrame);
        regDialog.setVisible(true);
    }

    private void saveData() {
        log.info("Saving XML data.");
        FileDialog saveDialog = new FileDialog(mainFrame, "Save XML", FileDialog.SAVE);
        saveDialog.setFile("data_dog.xml");
        saveDialog.setVisible(true);
        String dir = saveDialog.getDirectory();
        String file = saveDialog.getFile();
        if (dir == null || file == null) {
            log.warn("User  canceled save operation.");
            return;
        }
        String fileName = dir + file;
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element root = doc.createElement("data");
            doc.appendChild(root);
            saveTableData(doc, root, "Оценщики", mdlozen);
            saveTableData(doc, root, "Собачники", mdlxozein);
            saveTableData(doc, root, "Победители", mdlmesto);
            saveTableData(doc, root, "О_собаке", mdldog);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(fileName));
            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error saving data: " + e.getMessage());
        }
    }


    private void loadData() {
        log.info("Loading XML data.");
        FileDialog loadDialog = new FileDialog(mainFrame, "Load XML", FileDialog.LOAD);
        loadDialog.setVisible(true);
        String dir = loadDialog.getDirectory();
        String file = loadDialog.getFile();
        if (dir == null || file == null) {
            log.warn("User  canceled load operation.");
            return;
        }
        String fileName = dir + file;
        try {
            File xmlFile = new File(fileName);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            loadTableData(doc, "Оценщики", mdlozen);
            loadTableData(doc, "Собачники", mdlxozein);
            loadTableData(doc, "Победители", mdlmesto);
            loadTableData(doc, "О_собаке", mdldog);
            log.info("Data successfully loaded from " + fileName);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error loading data: " + e.getMessage());
        }
    }


    private void loadTableData(Document doc, String tagName, DefaultTableModel model) {
        NodeList nodeList = doc.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            NodeList rowList = nodeList.item(0).getChildNodes();
            for (int i = 0; i < rowList.getLength(); i++) {
                Node rowNode = rowList.item(i);
                if (rowNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element rowElement = (Element) rowNode;
                    String[] rowData = new String[model.getColumnCount()];
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        String colName = model.getColumnName(j);
                        NodeList cellNodes = rowElement.getElementsByTagName(colName);
                        if (cellNodes.getLength() > 0) {
                            rowData[j] = cellNodes.item(0).getTextContent();
                        } else {
                            rowData[j] = "";
                        }
                    }
                    model.addRow(rowData);
                }
            }
        }
    }


    private void saveTableData(Document doc, Element root, String tagName, DefaultTableModel model) {
        Element tableElement = doc.createElement(tagName);
        root.appendChild(tableElement);
        for (int i = 0; i < model.getRowCount(); i++) {
            Element rowElement = doc.createElement("row");
            tableElement.appendChild(rowElement);
            for (int j = 0; j < model.getColumnCount(); j++) {
                Element cellElement = doc.createElement(model.getColumnName(j));
                cellElement.appendChild(doc.createTextNode((String) model.getValueAt(i, j)));
                rowElement.appendChild(cellElement);
            }
        }
    }


    private void createSearchPanel() {
        JPanel searchPanel = new JPanel(); // Создаем панель для поиска
        searchPanel.setBackground(Color.YELLOW);
        cmbSearch = new JComboBox<>(new String[]{"Оценщики", "Собачники", "Победители", "О_собаке"});
        txtSearch = new JTextField("Search by word", 30);
        btnFilter = new JButton("Search");
        btnReset = new JButton("Reset Search");

        searchPanel.add(cmbSearch);
        searchPanel.add(txtSearch);
        searchPanel.add(btnFilter);
        searchPanel.add(btnReset);
        btnFilter.addActionListener(e -> performSearch());
        btnReset.addActionListener(e -> resetSearch());
        mainFrame.add(searchPanel, BorderLayout.SOUTH);
    }


    private void generateReport(String reportPath, DefaultTableModel model, String outputFileName) throws JRException {
        JRTableModelDataSource dataSource = new JRTableModelDataSource(model);
        JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, null, dataSource);
        JasperExportManager.exportReportToPdfFile(jasperPrint, outputFileName);
    }

    private void generateHTMLReportForTable(String reportPath, DefaultTableModel model, String outputFileName) throws JRException {
        JRTableModelDataSource dataSource = new JRTableModelDataSource(model);
        JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, null, dataSource);
        JasperExportManager.exportReportToHtmlFile(jasperPrint, outputFileName);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App().show());
    }
}