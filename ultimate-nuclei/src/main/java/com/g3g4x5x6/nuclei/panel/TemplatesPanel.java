package com.g3g4x5x6.nuclei.panel;

import com.alibaba.fastjson.JSONObject;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.icons.FlatSearchIcon;
import com.g3g4x5x6.nuclei.NucleiFrame;
import com.g3g4x5x6.nuclei.model.SelectedTagsConfig;
import com.g3g4x5x6.nuclei.model.SelectedTemplatesConfig;
import com.g3g4x5x6.nuclei.panel.connector.ConsolePanel;
import com.g3g4x5x6.ultils.NucleiConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

@Slf4j
public class TemplatesPanel extends JPanel {
    private static final String defaultNucleiTemplatesPath = NucleiConfig.getProperty("nuclei.templates.path");
    private JButton openBtn = new JButton(new FlatSVGIcon("icons/menu-open.svg"));
    private JButton severityBtn = new JButton(new FlatSVGIcon("icons/sortBySeverity.svg"));
    private JButton refreshBtn = new JButton(new FlatSVGIcon("icons/refresh.svg"));
    private JTextField searchField = new JTextField();

    private JScrollPane tableScroll;
    private JTable templatesTable;
    private DefaultTableModel tableModel;
    private JPopupMenu tablePopMenu;
    private TableRowSorter<DefaultTableModel> sorter;
    private Clipboard clipboard;

    private final JPopupMenu severityPopupMenu = new JPopupMenu();
    private JCheckBox infoBox = new JCheckBox("Information");
    private JCheckBox lowBox = new JCheckBox("Low");
    private JCheckBox mediumBox = new JCheckBox("Medium");
    private JCheckBox highBox = new JCheckBox("High");
    private JCheckBox criticalBox = new JCheckBox("Critical");

    private LinkedList<LinkedHashMap<String, String>> templates = new LinkedList<>();

    public TemplatesPanel() {
        this.setLayout(new BorderLayout());

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.add(openBtn);
        toolBar.addSeparator();
        toolBar.add(severityBtn);
        toolBar.addSeparator();
        toolBar.add(refreshBtn);
        toolBar.addSeparator();
        toolBar.add(Box.createGlue());
        toolBar.add(searchField);
        initToolBarAction();


        tablePopMenu = new JPopupMenu();
        tablePopMenu.add(editAction);
        tablePopMenu.add(openDirAction);
        tablePopMenu.add(copyPathAction);
        tablePopMenu.add(deleteTemplateAction);
        tablePopMenu.addSeparator();
        tablePopMenu.add(generateWithSelectedAction);
        tablePopMenu.add(generateWithTagsAction);
        JMenu selectedTemplatesMenu = new JMenu("运行选中的模板");
        selectedTemplatesMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                log.debug("menuSelected");
                selectedTemplatesMenu.removeAll();
                LinkedHashMap<String, ConsolePanel> consolePanels = getConsolePanels();
                for (String title : consolePanels.keySet()) {
                    JMenuItem tempItem = new JMenuItem(title);
                    tempItem.addActionListener(new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            log.debug("Execute in " + title);
                            runTemplatesInSelectedConsole(consolePanels.get(title));
                        }
                    });
                    selectedTemplatesMenu.add(tempItem);
                }
            }

            @Override
            public void menuDeselected(MenuEvent e) {
                log.debug("menuDeselected");
            }

            @Override
            public void menuCanceled(MenuEvent e) {
                log.debug("menuCanceled");
            }
        });
        tablePopMenu.add(selectedTemplatesMenu);
        JMenu selectedTagsMenu = new JMenu("运行包含选中标签的模板");
        selectedTagsMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                selectedTagsMenu.removeAll();
                LinkedHashMap<String, ConsolePanel> consolePanels = getConsolePanels();
                for (String title : consolePanels.keySet()) {
                    JMenuItem tempItem = new JMenuItem(title);
                    tempItem.addActionListener(new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            log.debug("Execute in " + title);
                            runTagsInSelectedConsole(consolePanels.get(title));
                        }
                    });
                    selectedTagsMenu.add(tempItem);
                }
            }

            @Override
            public void menuDeselected(MenuEvent e) {

            }

            @Override
            public void menuCanceled(MenuEvent e) {

            }
        });
        tablePopMenu.add(selectedTagsMenu);

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
        refreshDataForTable();
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

    private void initToolBarAction() {
        infoBox.setSelected(true);
        lowBox.setSelected(true);
        mediumBox.setSelected(true);
        highBox.setSelected(true);
        criticalBox.setSelected(true);
        severityPopupMenu.add(infoBox);
        severityPopupMenu.add(lowBox);
        severityPopupMenu.add(mediumBox);
        severityPopupMenu.add(highBox);
        severityPopupMenu.add(criticalBox);
        JMenuItem okMenuItem = new JMenuItem("确认");
        okMenuItem.setIcon(new FlatSVGIcon("icons/inspectionsOK.svg"));
        okMenuItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO 过滤风险级别
            }
        });
        severityPopupMenu.add(okMenuItem);
        severityBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                severityPopupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        });
        refreshBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(() -> {
                    // nuclei -ut  [-ut, -update-templates         update nuclei-templates to latest released version]
                    ConsolePanel consolePanel = (ConsolePanel) RunningPanel.tabbedPane.getSelectedComponent();
                    consolePanel.write("nuclei -ut\r");

                    // 清除旧列表
                    templates.clear();
                    log.debug("Templates Count: " + templates.size());
                    // 展示列表内信息
                    refreshDataForTable();
                    log.debug("Templates Count: " + templates.size());
                }).start();
            }
        });
        searchField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search, Enter");
        searchField.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_ICON, new FlatSearchIcon());
        searchField.registerKeyboardAction(e -> {
                    String searchKeyWord = searchField.getText().strip();
                    sorter.setRowFilter(RowFilter.regexFilter(searchKeyWord));
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
                JComponent.WHEN_FOCUSED);
    }

    /**
     * 耗时操作
     */
    private void refreshDataForTable() {
        // 搜索功能
        sorter = new TableRowSorter<>(tableModel);
        templatesTable.setRowSorter(sorter);
        new Thread(() -> {
            tableModel.setRowCount(0);
            templates.clear();
            try {
                // 初始化列表并输出列表大小
                log.debug("Templates Count: " + getAllTemplatesFromPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            int count = 0;
            for (LinkedHashMap<String, String> templateInfo : templates) {
                count++;
                String id = templateInfo.get("id");
                String name = templateInfo.get("name");
                String severity = templateInfo.get("severity");
                String author = templateInfo.get("author");
                String description = templateInfo.get("description");
                String reference = templateInfo.get("reference");
                String tags = templateInfo.get("tags");
                tableModel.addRow(new String[]{String.valueOf(count), id, name, severity, tags, author, description, reference});
            }
        }).start();
    }

    private LinkedHashMap<String, String> getTemplate(String path) {
        LinkedHashMap<String, String> templateInfo = new LinkedHashMap<>();
        Map map = getMapFromYaml(path);
        if (map != null) {
            JSONObject jsonObject = new JSONObject(map);
            JSONObject info = jsonObject.getJSONObject("info");

            templateInfo.put("path", path);
            templateInfo.put("id", jsonObject.getString("id") == null ? "空" : jsonObject.getString("id"));
            templateInfo.put("name", info.getString("name"));
            templateInfo.put("severity", info.getString("severity"));
            templateInfo.put("author", info.getString("author"));
            templateInfo.put("description", info.getString("description"));
            templateInfo.put("reference", info.getString("reference"));
            templateInfo.put("tags", info.getString("tags"));
        } else {
            templateInfo.put("path", path);
            templateInfo.put("id", "空");
            templateInfo.put("name", "空");
            templateInfo.put("severity", "空");
            templateInfo.put("author", "空");
            templateInfo.put("description", "空");
            templateInfo.put("reference", "空");
            templateInfo.put("tags", "空");
        }
        return templateInfo;
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
     * @return 匹配到的文件总数
     * @throws IOException 抛出异常
     */
    private int getAllTemplatesFromPath() throws IOException {
        if (Files.exists(Path.of(TemplatesPanel.defaultNucleiTemplatesPath))) {
            Files.walkFileTree(Paths.get(TemplatesPanel.defaultNucleiTemplatesPath), new SimpleFileVisitor<>() {
                // 访问文件时触发
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (file.toString().endsWith(".yaml")) {
                        templates.add(getTemplate(file.toString()));
                    }
                    return FileVisitResult.CONTINUE;
                }

                // 访问目录时触发
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    return FileVisitResult.CONTINUE;
                }
            });
            return templates.size();
        }
        return 0;
    }

    private int getTemplatesFromPath(String rootPath) throws IOException {
        if (Files.exists(Path.of(rootPath))) {
            Files.walkFileTree(Paths.get(rootPath), new SimpleFileVisitor<>() {
                // 访问文件时触发
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (file.toString().endsWith(".yaml")) {
                        templates.add(getTemplate(file.toString()));
                    }
                    return FileVisitResult.CONTINUE;
                }

                // 访问目录时触发
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    return FileVisitResult.CONTINUE;
                }
            });
            return templates.size();
        }
        return 0;
    }

    private LinkedHashMap<String, ConsolePanel> getConsolePanels() {
        LinkedHashMap<String, ConsolePanel> consolePanels = new LinkedHashMap<>();
        int count = RunningPanel.tabbedPane.getTabCount();
        for (int i = 0; i < count; i++) {
            consolePanels.put(RunningPanel.tabbedPane.getTitleAt(i), (ConsolePanel) RunningPanel.tabbedPane.getComponentAt(i));
        }
        return consolePanels;
    }

    @SneakyThrows
    private void runTemplatesInSelectedConsole(ConsolePanel consolePanel) {
        log.debug("Run command with Selected 666");
        if (!NucleiFrame.targetPanel.getTextArea().getText().strip().equals("")) {
            SelectedTemplatesConfig selected = new SelectedTemplatesConfig();

            for (int index : templatesTable.getSelectedRows()) {
                int num = Integer.parseInt(templatesTable.getValueAt(index, 0).toString()) - 1;
                String savePath = templates.get(num).get("path");
                if (savePath.contains("workflow")) {
                    selected.addWorkflow(savePath);
                } else {
                    selected.addTemplate(savePath);
                }
            }
            selected.setTarget(Arrays.asList(NucleiFrame.targetPanel.getTextArea().getText().split("\\s+")));

            String configPath = NucleiConfig.getWorkPath() + "/temp/nuclei/" + UUID.randomUUID() + ".yaml";
            Yaml yaml = new Yaml();
            File file = new File(configPath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            yaml.dump(selected, new FileWriter(configPath));
            consolePanel.write("nuclei -config " + configPath + " -markdown-export " + NucleiFrame.reportDir + "\r");
            NucleiFrame.frameTabbedPane.setSelectedIndex(2);
            RunningPanel.tabbedPane.setSelectedComponent(consolePanel);
        } else {
            JOptionPane.showMessageDialog(NucleiFrame.nucleiFrame, "请先填写扫描目标", "警告", JOptionPane.WARNING_MESSAGE);
        }
    }

    @SneakyThrows
    private void runTagsInSelectedConsole(ConsolePanel consolePanel) {
        if (!NucleiFrame.targetPanel.getTextArea().getText().strip().equals("")) {
            // 配置对象
            SelectedTagsConfig selected = new SelectedTagsConfig();
            selected.setTarget(Arrays.asList(NucleiFrame.targetPanel.getTextArea().getText().split("\\s+")));

            ArrayList<String> tempTags = new ArrayList<>();
            for (int index : templatesTable.getSelectedRows()) {
                int num = Integer.parseInt(templatesTable.getValueAt(index, 0).toString()) - 1;
                String tags = templates.get(num).get("tags");
                tempTags.addAll(Arrays.asList(tags.split(",")));
            }
            selected.setTags(tempTags);
            // 保存配置对象
            String configPath = NucleiConfig.getWorkPath() + "/temp/nuclei/tags_" + UUID.randomUUID() + ".yaml";
            Yaml yaml = new Yaml();
            yaml.dump(selected, new FileWriter(configPath));
            // 执行命令
            String command = "nuclei -config " + configPath + " -markdown-export " + NucleiFrame.reportDir;
            consolePanel.write(command + "\r");
            NucleiFrame.frameTabbedPane.setSelectedIndex(2);
            RunningPanel.tabbedPane.setSelectedComponent(consolePanel);
        } else {
            JOptionPane.showMessageDialog(NucleiFrame.nucleiFrame, "请先填写扫描目标", "警告", JOptionPane.WARNING_MESSAGE);
        }
    }

    private AbstractAction editAction = new AbstractAction("编辑-概念验证模板") {
        @Override
        public void actionPerformed(ActionEvent e) {
            log.debug("Edit This Template");
            for (int index : templatesTable.getSelectedRows()) {
                int num = Integer.parseInt(templatesTable.getValueAt(index, 0).toString()) - 1;
                String savePath = templates.get(num).get("path");
                log.debug(savePath);
                EditTemplatePanel editPanel = new EditTemplatePanel(savePath);
                NucleiFrame.frameTabbedPane.addTab(editPanel.getTitle(), editPanel.getIcon(), editPanel);
                NucleiFrame.frameTabbedPane.setSelectedIndex(NucleiFrame.frameTabbedPane.getTabCount() - 1);
                log.debug(templates.get(num).toString());
            }
        }
    };

    private AbstractAction openDirAction = new AbstractAction("打开-模板所在文件夹") {
        @Override
        public void actionPerformed(ActionEvent e) {
            log.debug("Open in Folder");
            for (int index : templatesTable.getSelectedRows()) {
                int num = Integer.parseInt(templatesTable.getValueAt(index, 0).toString()) - 1;
                String savePath = templates.get(num).get("path");
                log.debug(savePath);
                new Thread(() -> {
                    try {
                        Desktop.getDesktop().open(new File(savePath).getParentFile());
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }).start();
            }
        }
    };

    private AbstractAction copyPathAction = new AbstractAction("复制-模板绝对路径") {
        @Override
        public void actionPerformed(ActionEvent e) {
            log.debug("Copy Path");
            String savePath = "\n";
            for (int index : templatesTable.getSelectedRows()) {
                int num = Integer.parseInt(templatesTable.getValueAt(index, 0).toString()) - 1;
                savePath += templates.get(num).get("path") + "\n";
                log.debug(savePath);
            }
            savePath = savePath.strip();
            if (clipboard == null)
                clipboard = Toolkit.getDefaultToolkit().getSystemClipboard(); //获得系统剪贴板
            Transferable transferable = new StringSelection(savePath);
            clipboard.setContents(transferable, null);
        }
    };

    private AbstractAction deleteTemplateAction = new AbstractAction("删除-选中的模板") {
        @SneakyThrows
        @Override
        public void actionPerformed(ActionEvent e) {
            for (int index : templatesTable.getSelectedRows()) {
                int num = Integer.parseInt(templatesTable.getValueAt(index, 0).toString()) - 1;
                String savePath = templates.get(num).get("path");
                Files.delete(Path.of(savePath));
                templates.remove(num);
            }
            refreshDataForTable();
        }
    };

    /**
     * 目标是可以做到多选
     * <html><font style='color:red'></font></html>
     */
    private AbstractAction generateWithSelectedAction = new AbstractAction("<html>为选中的<font style='color:red'>模板</font>生成执行命令</html>") {
        @SneakyThrows
        @Override
        public void actionPerformed(ActionEvent e) {
            log.debug("Generate command with Selected");
            if (!NucleiFrame.targetPanel.getTextArea().getText().strip().equals("")) {
                SelectedTemplatesConfig selected = new SelectedTemplatesConfig();

                ArrayList<String> tempTemplate = new ArrayList<>();
                for (int index : templatesTable.getSelectedRows()) {
                    int num = Integer.parseInt(templatesTable.getValueAt(index, 0).toString()) - 1;
                    String savePath = templates.get(num).get("path");
                    tempTemplate.add(savePath);
                }
                selected.setTemplates(tempTemplate);
                selected.setTarget(Arrays.asList(NucleiFrame.targetPanel.getTextArea().getText().split("\\s+")));

                String configPath = NucleiConfig.getWorkPath() + "/temp/nuclei/templates_" + UUID.randomUUID() + ".yaml";
                Yaml yaml = new Yaml();
                yaml.dump(selected, new FileWriter(configPath));
                String command = "nuclei -config " + configPath + " -markdown-export " + NucleiFrame.reportDir;
                if (clipboard == null)
                    clipboard = Toolkit.getDefaultToolkit().getSystemClipboard(); //获得系统剪贴板
                Transferable transferable = new StringSelection(command);
                clipboard.setContents(transferable, null);

                ConsolePanel consolePanel = (ConsolePanel) RunningPanel.tabbedPane.getSelectedComponent();
                consolePanel.write(command);
                NucleiFrame.frameTabbedPane.setSelectedIndex(2);
            } else {
                JOptionPane.showMessageDialog(NucleiFrame.nucleiFrame, "请先填写扫描目标", "警告", JOptionPane.WARNING_MESSAGE);
            }
        }
    };

    private AbstractAction generateWithTagsAction = new AbstractAction("<html>为选中的<font style='color:blue'>标签</font>生成执行命令</html>") {
        @SneakyThrows
        @Override
        public void actionPerformed(ActionEvent e) {
            log.debug("Generate command with Tags");
            if (!NucleiFrame.targetPanel.getTextArea().getText().strip().equals("")) {
                // 配置对象
                SelectedTagsConfig selected = new SelectedTagsConfig();
                selected.setTarget(Arrays.asList(NucleiFrame.targetPanel.getTextArea().getText().split("\\s+")));

                ArrayList<String> tempTags = new ArrayList<>();
                for (int index : templatesTable.getSelectedRows()) {
                    int num = Integer.parseInt(templatesTable.getValueAt(index, 0).toString()) - 1;
                    String tags = templates.get(num).get("tags");
                    tempTags.addAll(Arrays.asList(tags.split(",")));
                }
                selected.setTags(tempTags);
                // 保存配置对象
                String configPath = NucleiConfig.getWorkPath() + "/temp/nuclei/tags_" + UUID.randomUUID() + ".yaml";
                Yaml yaml = new Yaml();
                yaml.dump(selected, new FileWriter(configPath));
                // 复制命令到粘贴板
                String command = "nuclei -config " + configPath + " -markdown-export " + NucleiFrame.reportDir;
                if (clipboard == null)
                    clipboard = Toolkit.getDefaultToolkit().getSystemClipboard(); //获得系统剪贴板
                Transferable transferable = new StringSelection(command);
                clipboard.setContents(transferable, null);
                // 跳转到命令行面板
                ConsolePanel consolePanel = (ConsolePanel) RunningPanel.tabbedPane.getSelectedComponent();
                consolePanel.write(command);
                NucleiFrame.frameTabbedPane.setSelectedIndex(2);
            } else {
                JOptionPane.showMessageDialog(NucleiFrame.nucleiFrame, "请先填写扫描目标", "警告", JOptionPane.WARNING_MESSAGE);
            }
        }
    };
}
