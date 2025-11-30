package net.javaguids.popin.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import net.javaguids.popin.database.EventDAO;
import net.javaguids.popin.database.ReportDAO;
import net.javaguids.popin.models.Event;

import java.util.Map;

public class ReportsController {

    @FXML private TableView<ReportRow> reportsTable;
    @FXML private TableColumn<ReportRow, String> colEventTitle;
    @FXML private TableColumn<ReportRow, Integer> colCount;

    private final ReportDAO reportDAO = new ReportDAO();
    private final EventDAO eventDAO = new EventDAO();

    @FXML
    public void initialize() {
        colEventTitle.setCellValueFactory(new PropertyValueFactory<>("eventTitle"));
        colCount.setCellValueFactory(new PropertyValueFactory<>("reportCount"));

        loadData();
    }

    private void loadData() {
        Map<Integer, Integer> counts = reportDAO.getReportCountsByEvent();
        for (Map.Entry<Integer, Integer> entry : counts.entrySet()) {
            int eventId = entry.getKey();
            int count = entry.getValue();
            Event e = eventDAO.findById(eventId);
            if (e != null) {
                reportsTable.getItems().add(
                        new ReportRow(e.getTitle(), count)
                );
            }
        }
    }

    // simple POJO for table rows
    public static class ReportRow {
        private final String eventTitle;
        private final int reportCount;

        public ReportRow(String eventTitle, int reportCount) {
            this.eventTitle = eventTitle;
            this.reportCount = reportCount;
        }

        public String getEventTitle() {
            return eventTitle;
        }

        public int getReportCount() {
            return reportCount;
        }
    }
}