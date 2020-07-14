import javax.swing.table.AbstractTableModel;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

public class ListModel extends AbstractTableModel {

    private ArrayList<Command> list;
    private ArrayList<Command> filteredList;

    private ScreenDisplay display;

    public String[] columnNames = {"Name", "Description"};

    public ListModel() {
        list = new ArrayList<Command>();
        filteredList = new ArrayList<Command>();

        display = ScreenDisplay.DefaultDisplay;

        loadList();
        sort();
    }

    public void sort() {
        switch (display) {
            case DefaultDisplay:
                list.sort(Comparator.comparing(Command::getName));
                fireTableStructureChanged();
            case SearchingDisplay:
                filteredList.sort(Comparator.comparing(Command::getName));
                fireTableStructureChanged();
        }

    }

    // Search and filter list from a given string
    public void search(String str) {
        filteredList.clear();

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getName().contains(str) || list.get(i).getDescription().contains(str)) {
                filteredList.add(list.get(i));
            }
        }
        display = ScreenDisplay.SearchingDisplay;
        sort();
        fireTableStructureChanged();
    }

    public void add(Command command) {
        switch (display) {
            case DefaultDisplay:
                list.add(command);
                break;
            case SearchingDisplay:
                filteredList.add(command);
                break;
            default:
                throw new RuntimeException("Not valid display type");
        }
    }

    public Command remove(int i) {
        Command temp;
        switch (display) {
            case DefaultDisplay:
                temp = list.get(i);
                list.remove(i);
                return temp;
            case SearchingDisplay:
                temp = filteredList.get(i);
                filteredList.remove(i);
                return temp;
            default:
                throw new RuntimeException("Not valid display type");
        }
    }

    public Command get(int i) {
        switch (display) {
            case DefaultDisplay:
                return list.get(i);
            case SearchingDisplay:
                return filteredList.get(i);
            default:
                throw new RuntimeException("Not valid display type");
        }
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public int getRowCount() {
        switch (display) {
            case DefaultDisplay:
                return list.size();
            case SearchingDisplay:
                return filteredList.size();
            default:
                throw new RuntimeException("Not valid display type");
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (display) {
            case DefaultDisplay:
                switch (columnIndex) {
                    case 0:
                        return list.get(rowIndex).getName();
                    case 1:
                        return list.get(rowIndex).getDescription();
                    default:
                        throw new RuntimeException("Row or Column are out of range: " + rowIndex + " " + columnIndex);

                }
            case SearchingDisplay:
                switch (columnIndex) {
                    case 0:
                        return filteredList.get(rowIndex).getName();
                    case 1:
                        return filteredList.get(rowIndex).getDescription();
                    default:
                        throw new RuntimeException("Row or Column are out of range: " + rowIndex + " " + columnIndex);
                }
            default:
                throw new RuntimeException("Not valid display type");
        }
    }

    public void setDisplay(ScreenDisplay display) {
        this.display = display;
        fireTableStructureChanged();
    }

    public ScreenDisplay getDisplay() {
        return display;
    }

    public void loadList() {
        int c = 0;
        try {
            File file = new File("C:/Users/evanl/IdeaProjects/TerminalCommands/src/cmds.txt");
            Scanner scnr = new Scanner(file);
            while (scnr.hasNextLine()) {
                String in = scnr.nextLine();
                String[] data= in.split(", ");
                add(new Command(data[0], data[1]));
                c++;
            }
            scnr.close();
        } catch (FileNotFoundException e) {
            System.out.println("File read error occurred");
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
            System.out.println("No delimiter found at line: " + c);
        }
    }
}