import javax.swing.*;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;

public class Main {
    public static void main(String args[]) {
        Object rows[][] = {{"one", "ichi"}, {"two", "ni"},
                {"three", "san"}, {"four", "shi"}, {"five", "go"},
                {"six", "roku"}, {"seven", "shichi"}, {"eight", "hachi"},
                {"nine", "kyu"}, {"ten", "ju"}};
        Object headers[] = {"English", "Japanese"};
        String title = (args.length == 0 ? "JTable Sample" : args[0]);
        JFrame frame = new JFrame(title);

        TableColumnModel columnModel = new DefaultTableColumnModel();
        TableColumn firstColumn = new TableColumn(1);
        firstColumn.setHeaderValue(headers[1]);
        columnModel.addColumn(firstColumn);
        TableColumn secondColumn = new TableColumn(0);
        secondColumn.setHeaderValue(headers[0]);
        columnModel.addColumn(secondColumn);
        ListSelectionModel selectionModel = new DefaultListSelectionModel();
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //    JTable table = new JTable(model, columnModel, selectionModel);
        JTable table = new JTable(rows, headers);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.setSize(300, 150);
        frame.setVisible(true);
    }
}