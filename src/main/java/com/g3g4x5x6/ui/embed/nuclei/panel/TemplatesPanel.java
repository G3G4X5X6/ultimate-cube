package com.g3g4x5x6.ui.embed.nuclei.panel;

import com.alibaba.fastjson.JSONObject;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.Map;

@Slf4j
public class TemplatesPanel extends JPanel {
    private static final String defaultNucleiTemplatesPath = System.getProperties().getProperty("user.home") + "/nuclei-templates";
    private JToolBar toolBar = new JToolBar();
    private JButton newBtn = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/addFile.svg"));
    private JButton openBtn = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/menu-open.svg"));
    private JButton saveBtn = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/SavedContext.svg"));
    private JButton saveAllBtn = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/menu-saveall.svg"));
    private JButton closeBtn = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/ignore_file.svg"));
    private JButton closeAllBtn = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/ignore_file.svg"));
    private JButton cutBtn = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/menu-cut.svg"));
    private JButton copyBtn = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/copy.svg"));
    private JButton pasteBtn = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/menu-paste.svg"));
    private JButton undoBtn = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/undo.svg"));
    private JButton redoBtn = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/redo.svg"));
    private JButton searchBtn = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/search.svg"));
    private JButton replaceBtn = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/replace.svg"));
    private JToggleButton lineWrapBtn = new JToggleButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/toggleSoftWrap.svg"));
    private JButton terminalBtn = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/changeView.svg"));
    private JTextField searchField = new JTextField();

    private JScrollPane tableScroll;
    private JTable templatesTable;
    private DefaultTableModel tableModel;
    private JPopupMenu tablePopMenu;

    private final LinkedList<String> templatesList = new LinkedList<>();

    public TemplatesPanel() {
        this.setLayout(new BorderLayout());

        toolBar.setFloatable(false);
        toolBar.add(newBtn);
        toolBar.add(openBtn);
        toolBar.add(saveBtn);
        toolBar.add(saveAllBtn);
        toolBar.add(closeBtn);
        toolBar.add(closeAllBtn);
        toolBar.addSeparator();
        toolBar.add(cutBtn);
        toolBar.add(copyBtn);
        toolBar.add(pasteBtn);
        toolBar.addSeparator();
        toolBar.add(undoBtn);
        toolBar.add(redoBtn);
        toolBar.addSeparator();
        toolBar.add(lineWrapBtn);
        toolBar.addSeparator();
        toolBar.add(searchBtn);
        toolBar.add(replaceBtn);
        toolBar.addSeparator();
        toolBar.add(terminalBtn);
        toolBar.addSeparator();
        toolBar.add(Box.createGlue());
        toolBar.add(searchField);


        tablePopMenu = new JPopupMenu();
        tablePopMenu.add(editAction);
        tablePopMenu.add(openDirAction);
        tablePopMenu.add(copyPathAction);
        tablePopMenu.addSeparator();
        tablePopMenu.add(generateWithSelectedAction);
        tablePopMenu.add(generateWithTagsAction);
        tablePopMenu.add(runWithSelectedAction);
        tablePopMenu.add(runWithTagsAction);


        templatesTable = new JTable();
        tableModel = new DefaultTableModel() {
            // 不可编辑
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };
        String[] columnNames = {
                "#",
                "templates_id",
                "templates_name",
                "templates_severity",
                "templates_tags",
                "templates_author",
                "templates_description",
                "templates_reference"};
        tableModel.setColumnIdentifiers(columnNames);
        templatesTable.setModel(tableModel);
        initDataForTable();
        tableScroll = new JScrollPane(templatesTable);
        tableScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        tableScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JTextField.CENTER);
        templatesTable.getColumn("#").setCellRenderer(centerRenderer);
        templatesTable.getColumn("templates_severity").setCellRenderer(centerRenderer);

        templatesTable.getColumn("#").setPreferredWidth(20);
        templatesTable.getColumn("templates_id").setPreferredWidth(60);
        templatesTable.getColumn("templates_name").setPreferredWidth(100);
        templatesTable.getColumn("templates_severity").setPreferredWidth(40);
        templatesTable.getColumn("templates_author").setPreferredWidth(30);
        templatesTable.setComponentPopupMenu(tablePopMenu);
        templatesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }
        });

        this.add(toolBar, BorderLayout.NORTH);
        this.add(tableScroll, BorderLayout.CENTER);
    }

    /**
     * 耗时操作
     */
    private void initDataForTable() {
        new Thread(() -> {
            try {
                // 初始化列表并输出列表大小
                log.debug("Templates Count: " + getAllTemplatesFromPath(defaultNucleiTemplatesPath));
            } catch (IOException e) {
                e.printStackTrace();
            }
            int count = 0;
            for (String path : templatesList) {
                Map template = getMapFromYaml(path);
                JSONObject jsonObject = new JSONObject(template);
                JSONObject info = jsonObject.getJSONObject("info");
                String id = jsonObject.getString("id");
                String name = info.getString("name");
                String severity = info.getString("severity");
                String author = info.getString("author");
                String description = info.getString("description");
                String reference = info.getString("reference");
                String tags = info.getString("tags");
                count++;
                tableModel.addRow(new String[]{String.valueOf(count), id, name, severity, tags, author, description, reference});
            }
        }).start();
    }

    private Map getMapFromYaml(String path) {
        Map template;
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 调基础工具类的方法
        Yaml yaml = new Yaml();
        template = yaml.loadAs(inputStream, Map.class);
        return template;
    }

    /**
     * 遍历出目录下的所有 yaml 文件
     *
     * @param rootPath 遍历目录
     * @return 匹配到的文件总数
     * @throws IOException 抛出异常
     */
    private int getAllTemplatesFromPath(String rootPath) throws IOException {
        if (Files.exists(Path.of(rootPath))){
            Files.walkFileTree(Paths.get(rootPath), new SimpleFileVisitor<>() {
                // 访问文件时触发
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (file.toString().endsWith(".yaml")) {
                        templatesList.add(file.toString());
                    }
                    return FileVisitResult.CONTINUE;
                }

                // 访问目录时触发
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    return FileVisitResult.CONTINUE;
                }
            });
            return templatesList.size();
        }
        return 0;
    }

    private AbstractAction editAction = new AbstractAction("Edit Template") {
        @Override
        public void actionPerformed(ActionEvent e) {
            log.debug("Edit This Template");
        }
    };

    private AbstractAction openDirAction = new AbstractAction("Open in Folder") {
        @Override
        public void actionPerformed(ActionEvent e) {
            log.debug("Open in Folder");
        }
    };

    private AbstractAction copyPathAction = new AbstractAction("Copy Path") {
        @Override
        public void actionPerformed(ActionEvent e) {
            log.debug("Copy Path");
        }
    };

    /**
     * 目标是可以做到多选
     */
    private AbstractAction generateWithSelectedAction = new AbstractAction("Generate command with Selected") {
        @Override
        public void actionPerformed(ActionEvent e) {
            log.debug("Generate command with Selected");
        }
    };

    private AbstractAction generateWithTagsAction = new AbstractAction("Generate command with Tags") {
        @Override
        public void actionPerformed(ActionEvent e) {
            log.debug("Generate command with Tags");
        }
    };

    private AbstractAction runWithSelectedAction = new AbstractAction("Run command with Selected") {
        @Override
        public void actionPerformed(ActionEvent e) {
            log.debug("Run command with Selected");
        }
    };

    private AbstractAction runWithTagsAction = new AbstractAction("Run command with Tags") {
        @Override
        public void actionPerformed(ActionEvent e) {
            log.debug("Run command with Tags");
        }
    };



}
