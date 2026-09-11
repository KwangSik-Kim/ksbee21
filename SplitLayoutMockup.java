import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;


/**
 * ============================================================
 * Pure Swing / Java2D UI Mockup
 *
 * - Java 8 compatible
 * - External library : NONE
 *
 * 구성
 * ------------------------------------------------------------
 * LEFT
 *   JTree
 *
 * RIGHT
 *   Geometry Table
 *   Feature Table
 *   Validation Table
 *
 *   Detail TextArea
 *
 *   Hierarchy Graph
 *
 * Vector Icon
 *   Tree Expand / Collapse
 *   Design
 *   Block
 *   Cell
 *   Layer
 *   Toolbar
 *   Status
 *
 * ============================================================
 */
public class FullVectorSwingMockup extends JFrame {


    // ========================================================
    // COLOR PALETTE
    // ========================================================

    private static final Color APP_BG =
            new Color(245, 247, 249);

    private static final Color PANEL_BG =
            Color.WHITE;

    private static final Color BORDER =
            new Color(205, 210, 216);

    private static final Color HEADER_BG =
            new Color(230, 235, 241);

    private static final Color HEADER_FG =
            new Color(45, 52, 60);

    private static final Color TEXT =
            new Color(55, 61, 68);

    private static final Color MUTED =
            new Color(110, 118, 126);

    private static final Color BLUE =
            new Color(60, 115, 180);

    private static final Color BLUE_LIGHT =
            new Color(225, 236, 249);

    private static final Color GREEN =
            new Color(45, 140, 78);

    private static final Color ORANGE =
            new Color(205, 132, 30);

    private static final Color RED =
            new Color(190, 58, 55);

    private static final Color PURPLE =
            new Color(120, 90, 160);

    private static final Color LAYER_BLUE =
            new Color(72, 130, 180);

    private static final Color LAYER_GREEN =
            new Color(75, 150, 110);

    private static final Color LAYER_ORANGE =
            new Color(205, 140, 65);


    // ========================================================
    // FONT
    // ========================================================

    private static final Font FONT_NORMAL =
            new Font(Font.SANS_SERIF, Font.PLAIN, 13);

    private static final Font FONT_BOLD =
            new Font(Font.SANS_SERIF, Font.BOLD, 13);

    private static final Font FONT_SECTION =
            new Font(Font.SANS_SERIF, Font.BOLD, 15);

    private static final Font FONT_HEADER =
            new Font(Font.SANS_SERIF, Font.BOLD, 13);

    private static final Font FONT_MONO =
            new Font(Font.MONOSPACED, Font.PLAIN, 13);


    // ========================================================
    // COMMON STROKE
    //
    // paint 때마다 new BasicStroke() 하지 않고 재사용
    // ========================================================

    private static final Stroke STROKE_THIN =
            new BasicStroke(
                    1.2f,
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND
            );

    private static final Stroke STROKE_NORMAL =
            new BasicStroke(
                    1.6f,
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND
            );

    private static final Stroke STROKE_BOLD =
            new BasicStroke(
                    2.0f,
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND
            );


    // ========================================================
    // CACHED ICONS
    //
    // Renderer에서 매번 new 하지 않음
    // ========================================================

    private static final Icon TREE_COLLAPSED_ICON =
            new ChevronIcon(
                    ChevronIcon.RIGHT,
                    12,
                    MUTED
            );

    private static final Icon TREE_EXPANDED_ICON =
            new ChevronIcon(
                    ChevronIcon.DOWN,
                    12,
                    BLUE
            );


    private static final Icon DESIGN_ICON =
            new DesignIcon(
                    16,
                    BLUE
            );

    private static final Icon BLOCK_ICON =
            new BlockIcon(
                    16,
                    PURPLE
            );

    private static final Icon CELL_ICON =
            new CellIcon(
                    15,
                    new Color(90, 105, 120)
            );

    private static final Icon LAYER_GROUP_ICON =
            new LayersIcon(
                    16,
                    LAYER_BLUE
            );

    private static final Icon LAYER_M1_ICON =
            new LayerBoxIcon(
                    13,
                    LAYER_BLUE
            );

    private static final Icon LAYER_M2_ICON =
            new LayerBoxIcon(
                    13,
                    LAYER_GREEN
            );

    private static final Icon LAYER_VIA_ICON =
            new LayerBoxIcon(
                    13,
                    LAYER_ORANGE
            );


    // Status icons cached

    private static final Icon STATUS_PASS_ICON =
            new StatusIcon(
                    StatusIcon.PASS,
                    14,
                    GREEN
            );

    private static final Icon STATUS_REVIEW_ICON =
            new StatusIcon(
                    StatusIcon.WARNING,
                    14,
                    ORANGE
            );

    private static final Icon STATUS_FAIL_ICON =
            new StatusIcon(
                    StatusIcon.FAIL,
                    14,
                    RED
            );


    // ========================================================
    // Constructor
    // ========================================================

    public FullVectorSwingMockup() {

        super("Design Data Inspection");

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setSize(
                1450,
                950
        );

        setMinimumSize(
                new Dimension(
                        1100,
                        750
                )
        );

        setLocationRelativeTo(null);

        getContentPane().setBackground(
                APP_BG
        );

        setLayout(
                new BorderLayout()
        );


        add(
                createToolBar(),
                BorderLayout.NORTH
        );

        add(
                createMainSplit(),
                BorderLayout.CENTER
        );

        add(
                createStatusBar(),
                BorderLayout.SOUTH
        );
    }


    // ========================================================
    // UI Manager
    // ========================================================

    private static void installVectorUI() {

        /*
         * JTree expand/collapse
         *
         * 이미지가 아니라 Java2D Icon.
         */

        UIManager.put(
                "Tree.collapsedIcon",
                TREE_COLLAPSED_ICON
        );

        UIManager.put(
                "Tree.expandedIcon",
                TREE_EXPANDED_ICON
        );
    }


    // ========================================================
    // MAIN SPLIT
    // ========================================================

    private JSplitPane createMainSplit() {

        JSplitPane split =
                new JSplitPane(
                        JSplitPane.HORIZONTAL_SPLIT,
                        createTreePanel(),
                        createRightPanel()
                );

        split.setDividerLocation(
                315
        );

        split.setResizeWeight(
                0.20
        );

        split.setDividerSize(
                7
        );

        split.setContinuousLayout(
                true
        );

        split.setOneTouchExpandable(
                true
        );

        return split;
    }


    // ========================================================
    // TREE PANEL
    // ========================================================

    private JPanel createTreePanel() {

        JPanel panel =
                new JPanel(
                        new BorderLayout()
                );

        panel.setBackground(
                APP_BG
        );

        panel.setBorder(
                new EmptyBorder(
                        8,
                        8,
                        8,
                        5
                )
        );


        // ----------------------------------------------------
        // Tree Data
        // ----------------------------------------------------

        DefaultMutableTreeNode root =
                new DefaultMutableTreeNode(
                        "Design"
                );


        DefaultMutableTreeNode blockA =
                new DefaultMutableTreeNode(
                        "BLOCK_A"
                );

        DefaultMutableTreeNode blockB =
                new DefaultMutableTreeNode(
                        "BLOCK_B"
                );


        blockA.add(
                new DefaultMutableTreeNode(
                        "CELL_A1"
                )
        );

        blockA.add(
                new DefaultMutableTreeNode(
                        "CELL_A2"
                )
        );

        blockA.add(
                new DefaultMutableTreeNode(
                        "CELL_A3"
                )
        );


        blockB.add(
                new DefaultMutableTreeNode(
                        "CELL_B1"
                )
        );

        blockB.add(
                new DefaultMutableTreeNode(
                        "CELL_B2"
                )
        );


        DefaultMutableTreeNode layers =
                new DefaultMutableTreeNode(
                        "Layers"
                );

        layers.add(
                new DefaultMutableTreeNode(
                        "M1 / 0"
                )
        );

        layers.add(
                new DefaultMutableTreeNode(
                        "M2 / 0"
                )
        );

        layers.add(
                new DefaultMutableTreeNode(
                        "VIA1 / 0"
                )
        );


        root.add(blockA);
        root.add(blockB);
        root.add(layers);


        // ----------------------------------------------------
        // JTree
        // ----------------------------------------------------

        JTree tree =
                new JTree(root);

        tree.setRootVisible(
                true
        );

        tree.setShowsRootHandles(
                true
        );

        tree.setFont(
                FONT_NORMAL
        );

        tree.setRowHeight(
                29
        );

        tree.setBorder(
                new EmptyBorder(
                        6,
                        5,
                        6,
                        5
                )
        );


        tree.setCellRenderer(
                new DesignTreeRenderer()
        );


        tree.expandRow(0);
        tree.expandRow(1);
        tree.expandRow(2);


        JScrollPane scroll =
                new JScrollPane(tree);

        scroll.setBorder(
                createSectionBorder(
                        "Design Tree"
                )
        );

        scroll
                .getViewport()
                .setBackground(Color.WHITE);


        panel.add(
                scroll,
                BorderLayout.CENTER
        );


        return panel;
    }


    // ========================================================
    // RIGHT AREA
    // ========================================================

    private JPanel createRightPanel() {

        JPanel panel =
                new JPanel(
                        new BorderLayout()
                );

        panel.setBackground(
                APP_BG
        );

        panel.setBorder(
                new EmptyBorder(
                        8,
                        5,
                        8,
                        8
                )
        );


        JSplitPane upper =
                new JSplitPane(
                        JSplitPane.VERTICAL_SPLIT,
                        createTableArea(),
                        createTextArea()
                );

        upper.setDividerLocation(
                510
        );

        upper.setResizeWeight(
                0.68
        );

        upper.setDividerSize(
                7
        );

        upper.setContinuousLayout(
                true
        );


        JSplitPane bottom =
                new JSplitPane(
                        JSplitPane.VERTICAL_SPLIT,
                        upper,
                        createGraphPanel()
                );

        bottom.setDividerLocation(
                700
        );

        bottom.setResizeWeight(
                0.80
        );

        bottom.setDividerSize(
                7
        );

        bottom.setContinuousLayout(
                true
        );


        panel.add(
                bottom,
                BorderLayout.CENTER
        );


        return panel;
    }


    // ========================================================
    // TABLE AREA
    // ========================================================

    private JPanel createTableArea() {

        JPanel panel =
                new JPanel();

        panel.setLayout(
                new BoxLayout(
                        panel,
                        BoxLayout.Y_AXIS
                )
        );

        panel.setBackground(
                APP_BG
        );

        panel.setBorder(
                new EmptyBorder(
                        3,
                        3,
                        5,
                        3
                )
        );


        // ----------------------------------------------------
        // Geometry
        // ----------------------------------------------------

        JTable geometry =
                createTable(
                        new String[]{
                                "Cell",
                                "Layer",
                                "Type",
                                "Count",
                                "Status"
                        },

                        new Object[][]{

                                {
                                        "CELL_A1",
                                        "M1",
                                        "BOX",
                                        245,
                                        "PASS"
                                },

                                {
                                        "CELL_A1",
                                        "M2",
                                        "POLYGON",
                                        132,
                                        "PASS"
                                },

                                {
                                        "CELL_A2",
                                        "VIA1",
                                        "BOX",
                                        67,
                                        "REVIEW"
                                },

                                {
                                        "CELL_A3",
                                        "M1",
                                        "PATH",
                                        28,
                                        "PASS"
                                }
                        }
                );


        geometry
                .getColumnModel()
                .getColumn(4)
                .setCellRenderer(
                        new StatusRenderer()
                );


        // ----------------------------------------------------
        // Features
        // ----------------------------------------------------

        JTable features =
                createTable(
                        new String[]{
                                "Feature",
                                "Reference",
                                "Current",
                                "Diff"
                        },

                        new Object[][]{

                                {
                                        "Density",
                                        "0.45",
                                        "0.42",
                                        "-0.03"
                                },

                                {
                                        "Polygon Count",
                                        "122",
                                        "118",
                                        "-4"
                                },

                                {
                                        "BBox Width",
                                        "125.2",
                                        "125.2",
                                        "0"
                                },

                                {
                                        "Hierarchy Depth",
                                        "5",
                                        "5",
                                        "0"
                                }
                        }
                );


        // ----------------------------------------------------
        // Validation
        // ----------------------------------------------------

        JTable validation =
                createTable(
                        new String[]{
                                "Check Item",
                                "Result",
                                "Score"
                        },

                        new Object[][]{

                                {
                                        "Image Similarity",
                                        "PASS",
                                        "0.97"
                                },

                                {
                                        "Geometry",
                                        "PASS",
                                        "0.99"
                                },

                                {
                                        "Hierarchy",
                                        "PASS",
                                        "1.00"
                                },

                                {
                                        "Rule Check",
                                        "REVIEW",
                                        "0.82"
                                }
                        }
                );


        validation
                .getColumnModel()
                .getColumn(1)
                .setCellRenderer(
                        new StatusRenderer()
                );


        JScrollPane p1 =
                createTableScroll(
                        geometry,
                        "Geometry"
                );

        JScrollPane p2 =
                createTableScroll(
                        features,
                        "Features"
                );

        JScrollPane p3 =
                createTableScroll(
                        validation,
                        "Validation"
                );


        Dimension max =
                new Dimension(
                        Integer.MAX_VALUE,
                        170
                );


        p1.setMaximumSize(max);
        p2.setMaximumSize(max);
        p3.setMaximumSize(max);


        panel.add(p1);

        panel.add(
                Box.createVerticalStrut(
                        12
                )
        );

        panel.add(p2);

        panel.add(
                Box.createVerticalStrut(
                        12
                )
        );

        panel.add(p3);


        return panel;
    }


    private JTable createTable(
            String[] columns,
            Object[][] data) {

        DefaultTableModel model =
                new DefaultTableModel(
                        data,
                        columns
                ) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column) {

                        return false;
                    }
                };


        JTable table =
                new JTable(model);


        table.setFont(
                FONT_NORMAL
        );

        table.setForeground(
                TEXT
        );

        table.setBackground(
                Color.WHITE
        );

        table.setRowHeight(
                29
        );

        table.setIntercellSpacing(
                new Dimension(
                        1,
                        4
                )
        );

        table.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        table.setFillsViewportHeight(
                true
        );

        table.setAutoCreateRowSorter(
                true
        );

        table.setShowVerticalLines(
                false
        );

        table.setShowHorizontalLines(
                true
        );

        table.setGridColor(
                new Color(
                        225,
                        228,
                        232
                )
        );


        JTableHeader header =
                table.getTableHeader();

        header.setFont(
                FONT_HEADER
        );

        header.setBackground(
                HEADER_BG
        );

        header.setForeground(
                HEADER_FG
        );

        header.setOpaque(
                true
        );

        header.setPreferredSize(
                new Dimension(
                        100,
                        34
                )
        );

        header.setReorderingAllowed(
                false
        );


        return table;
    }


    private JScrollPane createTableScroll(
            JTable table,
            String title) {

        JScrollPane scroll =
                new JScrollPane(table);

        scroll.setBorder(
                createSectionBorder(
                        title
                )
        );

        scroll.setPreferredSize(
                new Dimension(
                        100,
                        155
                )
        );

        scroll
                .getViewport()
                .setBackground(Color.WHITE);


        return scroll;
    }


    // ========================================================
    // DETAIL
    // ========================================================

    private JPanel createTextArea() {

        JPanel panel =
                new JPanel(
                        new BorderLayout()
                );

        panel.setBackground(
                APP_BG
        );

        panel.setBorder(
                new EmptyBorder(
                        5,
                        3,
                        5,
                        3
                )
        );


        JTextArea area =
                new JTextArea();

        area.setFont(
                FONT_MONO
        );

        area.setMargin(
                new Insets(
                        10,
                        12,
                        10,
                        12
                )
        );

        area.setText(

                "# Inspection Detail\n" +
                "\n" +
                "CELL             : CELL_A2\n" +
                "POSITION         : (12500.42, 8421.33)\n" +
                "LAYER            : M1 / M2 / VIA1\n" +
                "\n" +
                "Image Similarity : 0.971\n" +
                "Geometry Score   : 0.992\n" +
                "Hierarchy Score  : 1.000\n" +
                "\n" +
                "Result           : REVIEW\n" +
                "\n" +
                "Reason\n" +
                "-----------------------------------------------\n" +
                "- VIA1 count differs from reference.\n" +
                "- Geometry density is within tolerance.\n"
        );


        JScrollPane scroll =
                new JScrollPane(area);

        scroll.setBorder(
                createSectionBorder(
                        "Detail / Log / Source"
                )
        );


        panel.add(
                scroll,
                BorderLayout.CENTER
        );


        return panel;
    }


    // ========================================================
    // GRAPH
    // ========================================================

    private JPanel createGraphPanel() {

        JPanel panel =
                new JPanel(
                        new BorderLayout()
                );

        panel.setBackground(
                APP_BG
        );


        panel.setBorder(
                BorderFactory.createCompoundBorder(

                        new EmptyBorder(
                                5,
                                3,
                                3,
                                3
                        ),

                        createSectionBorder(
                                "Cell / Dependency Graph"
                        )
                )
        );


        JScrollPane scroll =
                new JScrollPane(
                        new GraphPanel()
                );

        scroll.setBorder(
                BorderFactory.createEmptyBorder()
        );


        panel.add(
                scroll,
                BorderLayout.CENTER
        );


        return panel;
    }


    // ========================================================
    // TOOLBAR
    // ========================================================

    private JToolBar createToolBar() {

        JToolBar bar =
                new JToolBar();

        bar.setFloatable(
                false
        );

        bar.setBorder(
                BorderFactory.createCompoundBorder(

                        BorderFactory.createMatteBorder(
                                0,
                                0,
                                1,
                                0,
                                BORDER
                        ),

                        new EmptyBorder(
                                6,
                                8,
                                6,
                                8
                        )
                )
        );


        bar.add(
                createToolbarButton(
                        "Open",
                        new FolderIcon(
                                17,
                                BLUE
                        )
                )
        );


        bar.add(
                Box.createHorizontalStrut(4)
        );


        bar.add(
                createToolbarButton(
                        "Reload",
                        new ReloadIcon(
                                17,
                                MUTED
                        )
                )
        );


        bar.addSeparator(
                new Dimension(
                        18,
                        20
                )
        );


        bar.add(
                createToolbarButton(
                        "Compare",
                        new CompareIcon(
                                17,
                                PURPLE
                        )
                )
        );


        bar.add(
                Box.createHorizontalStrut(4)
        );


        bar.add(
                createToolbarButton(
                        "Validate",
                        new CheckIcon(
                                17,
                                GREEN
                        )
                )
        );


        bar.addSeparator(
                new Dimension(
                        18,
                        20
                )
        );


        JLabel roi =
                new JLabel(
                        "ROI : "
                );

        roi.setFont(
                FONT_BOLD
        );

        bar.add(roi);


        JTextField field =
                new JTextField(
                        "12500.42, 8421.33"
                );

        field.setMaximumSize(
                new Dimension(
                        220,
                        29
                )
        );

        field.setPreferredSize(
                new Dimension(
                        220,
                        29
                )
        );

        bar.add(field);


        bar.add(
                Box.createHorizontalStrut(
                        5
                )
        );


        bar.add(
                createToolbarButton(
                        "Go",
                        new GoIcon(
                                15,
                                BLUE
                        )
                )
        );


        return bar;
    }


    private JButton createToolbarButton(
            String text,
            Icon icon) {

        JButton button =
                new JButton(
                        text,
                        icon
                );

        button.setFont(
                FONT_NORMAL
        );

        button.setFocusable(
                false
        );

        button.setIconTextGap(
                6
        );

        button.setMargin(
                new Insets(
                        5,
                        8,
                        5,
                        8
                )
        );


        return button;
    }


    // ========================================================
    // STATUS BAR
    // ========================================================

    private JPanel createStatusBar() {

        JPanel panel =
                new JPanel(
                        new BorderLayout()
                );


        panel.setBorder(
                BorderFactory.createCompoundBorder(

                        BorderFactory.createMatteBorder(
                                1,
                                0,
                                0,
                                0,
                                BORDER
                        ),

                        new EmptyBorder(
                                5,
                                9,
                                5,
                                9
                        )
                )
        );


        JLabel left =
                new JLabel(
                        "Ready  |  Design : sample.oas"
                );

        JLabel right =
                new JLabel(
                        "Memory : 512 MB"
                );


        panel.add(
                left,
                BorderLayout.WEST
        );

        panel.add(
                right,
                BorderLayout.EAST
        );


        return panel;
    }


    // ========================================================
    // BORDER
    // ========================================================

    private Border createSectionBorder(
            String title) {

        TitledBorder border =
                BorderFactory.createTitledBorder(

                        BorderFactory.createLineBorder(
                                BORDER
                        ),

                        title
                );


        border.setTitleFont(
                FONT_SECTION
        );

        border.setTitleColor(
                HEADER_FG
        );


        return BorderFactory.createCompoundBorder(

                border,

                new EmptyBorder(
                        6,
                        8,
                        8,
                        8
                )
        );
    }


    // ========================================================
    // TREE RENDERER
    // ========================================================

    static class DesignTreeRenderer
            extends DefaultTreeCellRenderer {

        DesignTreeRenderer() {

            setFont(
                    FONT_NORMAL
            );

            setBorder(
                    new EmptyBorder(
                            3,
                            5,
                            3,
                            5
                    )
            );
        }


        @Override
        public Component getTreeCellRendererComponent(
                JTree tree,
                Object value,
                boolean selected,
                boolean expanded,
                boolean leaf,
                int row,
                boolean hasFocus) {

            super.getTreeCellRendererComponent(
                    tree,
                    value,
                    selected,
                    expanded,
                    leaf,
                    row,
                    hasFocus
            );


            DefaultMutableTreeNode node =
                    (DefaultMutableTreeNode) value;


            String name =
                    String.valueOf(
                            node.getUserObject()
                    );


            if ("Design".equals(name)) {

                setIcon(
                        DESIGN_ICON
                );

                setFont(
                        FONT_BOLD
                );

            } else if (
                    "Layers".equals(name)) {

                setIcon(
                        LAYER_GROUP_ICON
                );

                setFont(
                        FONT_BOLD
                );

            } else if (
                    name.startsWith("BLOCK")) {

                setIcon(
                        BLOCK_ICON
                );

                setFont(
                        FONT_BOLD
                );

            } else if (
                    name.startsWith("CELL")) {

                setIcon(
                        CELL_ICON
                );

                setFont(
                        FONT_NORMAL
                );

            } else if (
                    name.startsWith("M1")) {

                setIcon(
                        LAYER_M1_ICON
                );

            } else if (
                    name.startsWith("M2")) {

                setIcon(
                        LAYER_M2_ICON
                );

            } else if (
                    name.startsWith("VIA")) {

                setIcon(
                        LAYER_VIA_ICON
                );

            } else {

                setIcon(
                        CELL_ICON
                );
            }


            return this;
        }
    }


    // ========================================================
    // STATUS TABLE RENDERER
    // ========================================================

    static class StatusRenderer
            extends DefaultTableCellRenderer {


        StatusRenderer() {

            setHorizontalAlignment(
                    SwingConstants.CENTER
            );

            setFont(
                    FONT_BOLD
            );

            setIconTextGap(
                    6
            );
        }


        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean selected,
                boolean hasFocus,
                int row,
                int column) {

            super.getTableCellRendererComponent(
                    table,
                    value,
                    selected,
                    hasFocus,
                    row,
                    column
            );


            setIcon(null);


            if (value == null) {
                return this;
            }


            String status =
                    value.toString()
                            .toUpperCase();


            switch (status) {

                case "PASS":
                case "OK":

                    setForeground(GREEN);
                    setIcon(
                            STATUS_PASS_ICON
                    );

                    break;


                case "REVIEW":
                case "CHECK":

                    setForeground(ORANGE);
                    setIcon(
                            STATUS_REVIEW_ICON
                    );

                    break;


                case "FAIL":
                case "ERROR":

                    setForeground(RED);
                    setIcon(
                            STATUS_FAIL_ICON
                    );

                    break;


                default:

                    setForeground(TEXT);
            }


            return this;
        }
    }


    // ========================================================
    // BASE VECTOR ICON
    // ========================================================

    static abstract class VectorIcon
            implements Icon {

        protected final int size;

        protected final Color color;


        VectorIcon(
                int size,
                Color color) {

            this.size =
                    size;

            this.color =
                    color;
        }


        @Override
        public int getIconWidth() {

            return size;
        }


        @Override
        public int getIconHeight() {

            return size;
        }


        protected Graphics2D copy(
                Graphics g) {

            Graphics2D g2 =
                    (Graphics2D) g.create();


            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );


            return g2;
        }
    }


    // ========================================================
    // CHEVRON
    // ========================================================

    static class ChevronIcon
            extends VectorIcon {

        static final int RIGHT = 0;

        static final int DOWN = 1;


        private final int direction;


        ChevronIcon(
                int direction,
                int size,
                Color color) {

            super(
                    size,
                    color
            );

            this.direction =
                    direction;
        }


        @Override
        public void paintIcon(
                Component component,
                Graphics graphics,
                int x,
                int y) {

            Graphics2D g =
                    copy(graphics);


            try {

                g.setColor(color);
                g.setStroke(STROKE_NORMAL);


                Path2D path =
                        new Path2D.Double();


                if (direction == RIGHT) {

                    path.moveTo(
                            x + 4,
                            y + 3
                    );

                    path.lineTo(
                            x + 8,
                            y + 6
                    );

                    path.lineTo(
                            x + 4,
                            y + 9
                    );

                } else {

                    path.moveTo(
                            x + 3,
                            y + 4
                    );

                    path.lineTo(
                            x + 6,
                            y + 8
                    );

                    path.lineTo(
                            x + 9,
                            y + 4
                    );
                }


                g.draw(path);

            } finally {

                g.dispose();
            }
        }
    }


    // ========================================================
    // DESIGN
    // ========================================================

    static class DesignIcon
            extends VectorIcon {

        DesignIcon(
                int size,
                Color color) {

            super(
                    size,
                    color
            );
        }


        @Override
        public void paintIcon(
                Component c,
                Graphics graphics,
                int x,
                int y) {

            Graphics2D g =
                    copy(graphics);


            try {

                g.setColor(
                        color
                );


                g.fillRect(
                        x + 1,
                        y + 1,
                        5,
                        5
                );

                g.fillRect(
                        x + size - 6,
                        y + 1,
                        5,
                        5
                );

                g.fillRect(
                        x + 1,
                        y + size - 6,
                        5,
                        5
                );

                g.fillRect(
                        x + size - 6,
                        y + size - 6,
                        5,
                        5
                );


                g.setStroke(
                        STROKE_THIN
                );


                g.drawRect(
                        x + 5,
                        y + 5,
                        size - 10,
                        size - 10
                );

            } finally {

                g.dispose();
            }
        }
    }


    // ========================================================
    // BLOCK
    // ========================================================

    static class BlockIcon
            extends VectorIcon {

        BlockIcon(
                int size,
                Color color) {

            super(
                    size,
                    color
            );
        }


        @Override
        public void paintIcon(
                Component c,
                Graphics graphics,
                int x,
                int y) {

            Graphics2D g =
                    copy(graphics);


            try {

                g.setColor(
                        new Color(
                                color.getRed(),
                                color.getGreen(),
                                color.getBlue(),
                                55
                        )
                );


                g.fillRoundRect(
                        x + 2,
                        y + 2,
                        size - 4,
                        size - 4,
                        4,
                        4
                );


                g.setColor(
                        color
                );

                g.setStroke(
                        STROKE_NORMAL
                );


                g.drawRoundRect(
                        x + 2,
                        y + 2,
                        size - 4,
                        size - 4,
                        4,
                        4
                );


                g.drawLine(
                        x + 5,
                        y + 6,
                        x + size - 5,
                        y + 6
                );

                g.drawLine(
                        x + 5,
                        y + 10,
                        x + size - 7,
                        y + 10
                );

            } finally {

                g.dispose();
            }
        }
    }


    // ========================================================
    // CELL
    // ========================================================

    static class CellIcon
            extends VectorIcon {

        CellIcon(
                int size,
                Color color) {

            super(
                    size,
                    color
            );
        }


        @Override
        public void paintIcon(
                Component c,
                Graphics graphics,
                int x,
                int y) {

            Graphics2D g =
                    copy(graphics);


            try {

                g.setColor(
                        color
                );

                g.setStroke(
                        STROKE_THIN
                );


                g.draw(
                        new RoundRectangle2D.Double(
                                x + 2,
                                y + 2,
                                size - 4,
                                size - 4,
                                3,
                                3
                        )
                );


                g.drawLine(
                        x + size / 2,
                        y + 4,
                        x + size / 2,
                        y + size - 4
                );


                g.drawLine(
                        x + 4,
                        y + size / 2,
                        x + size - 4,
                        y + size / 2
                );

            } finally {

                g.dispose();
            }
        }
    }


    // ========================================================
    // LAYERS GROUP
    // ========================================================

    static class LayersIcon
            extends VectorIcon {

        LayersIcon(
                int size,
                Color color) {

            super(
                    size,
                    color
            );
        }


        @Override
        public void paintIcon(
                Component c,
                Graphics graphics,
                int x,
                int y) {

            Graphics2D g =
                    copy(graphics);


            try {

                g.setStroke(
                        STROKE_THIN
                );


                drawLayer(
                        g,
                        x,
                        y + 1,
                        LAYER_BLUE
                );

                drawLayer(
                        g,
                        x,
                        y + 5,
                        LAYER_GREEN
                );

                drawLayer(
                        g,
                        x,
                        y + 9,
                        LAYER_ORANGE
                );

            } finally {

                g.dispose();
            }
        }


        private void drawLayer(
                Graphics2D g,
                int x,
                int y,
                Color c) {

            g.setColor(c);


            Path2D path =
                    new Path2D.Double();


            path.moveTo(
                    x + size / 2.0,
                    y
            );

            path.lineTo(
                    x + size - 2,
                    y + 3
            );

            path.lineTo(
                    x + size / 2.0,
                    y + 6
            );

            path.lineTo(
                    x + 2,
                    y + 3
            );

            path.closePath();


            g.draw(path);
        }
    }


    // ========================================================
    // SINGLE LAYER COLOR
    // ========================================================

    static class LayerBoxIcon
            extends VectorIcon {

        LayerBoxIcon(
                int size,
                Color color) {

            super(
                    size,
                    color
            );
        }


        @Override
        public void paintIcon(
                Component c,
                Graphics graphics,
                int x,
                int y) {

            Graphics2D g =
                    copy(graphics);


            try {

                g.setColor(
                        new Color(
                                color.getRed(),
                                color.getGreen(),
                                color.getBlue(),
                                60
                        )
                );


                g.fillRect(
                        x + 2,
                        y + 2,
                        size - 4,
                        size - 4
                );


                g.setColor(
                        color
                );


                g.drawRect(
                        x + 2,
                        y + 2,
                        size - 4,
                        size - 4
                );

            } finally {

                g.dispose();
            }
        }
    }


    // ========================================================
    // FOLDER
    // ========================================================

    static class FolderIcon
            extends VectorIcon {

        FolderIcon(
                int size,
                Color color) {

            super(
                    size,
                    color
            );
        }


        @Override
        public void paintIcon(
                Component c,
                Graphics graphics,
                int x,
                int y) {

            Graphics2D g =
                    copy(graphics);


            try {

                g.setColor(
                        color
                );


                Path2D p =
                        new Path2D.Double();


                p.moveTo(
                        x + 2,
                        y + 5
                );

                p.lineTo(
                        x + 7,
                        y + 5
                );

                p.lineTo(
                        x + 9,
                        y + 7
                );

                p.lineTo(
                        x + size - 2,
                        y + 7
                );

                p.lineTo(
                        x + size - 2,
                        y + size - 2
                );

                p.lineTo(
                        x + 2,
                        y + size - 2
                );

                p.closePath();


                g.fill(p);

            } finally {

                g.dispose();
            }
        }
    }


    // ========================================================
    // RELOAD
    // ========================================================

    static class ReloadIcon
            extends VectorIcon {

        ReloadIcon(
                int size,
                Color color) {

            super(
                    size,
                    color
            );
        }


        @Override
        public void paintIcon(
                Component c,
                Graphics graphics,
                int x,
                int y) {

            Graphics2D g =
                    copy(graphics);


            try {

                g.setColor(color);
                g.setStroke(STROKE_NORMAL);


                g.draw(
                        new Arc2D.Double(
                                x + 3,
                                y + 3,
                                size - 6,
                                size - 6,
                                35,
                                290,
                                Arc2D.OPEN
                        )
                );


                Path2D arrow =
                        new Path2D.Double();


                arrow.moveTo(
                        x + 2,
                        y + 5
                );

                arrow.lineTo(
                        x + 7,
                        y + 4
                );

                arrow.lineTo(
                        x + 6,
                        y + 9
                );

                arrow.closePath();


                g.fill(arrow);

            } finally {

                g.dispose();
            }
        }
    }


    // ========================================================
    // COMPARE
    // ========================================================

    static class CompareIcon
            extends VectorIcon {

        CompareIcon(
                int size,
                Color color) {

            super(
                    size,
                    color
            );
        }


        @Override
        public void paintIcon(
                Component c,
                Graphics graphics,
                int x,
                int y) {

            Graphics2D g =
                    copy(graphics);


            try {

                g.setColor(color);
                g.setStroke(STROKE_THIN);


                g.drawRect(
                        x + 1,
                        y + 3,
                        5,
                        size - 6
                );


                g.drawRect(
                        x + size - 6,
                        y + 3,
                        5,
                        size - 6
                );


                g.drawLine(
                        x + 8,
                        y + size / 2,
                        x + size - 9,
                        y + size / 2
                );


                g.drawLine(
                        x + 9,
                        y + size / 2 - 3,
                        x + 12,
                        y + size / 2
                );


                g.drawLine(
                        x + 9,
                        y + size / 2 + 3,
                        x + 12,
                        y + size / 2
                );

            } finally {

                g.dispose();
            }
        }
    }


    // ========================================================
    // CHECK
    // ========================================================

    static class CheckIcon
            extends VectorIcon {

        CheckIcon(
                int size,
                Color color) {

            super(
                    size,
                    color
            );
        }


        @Override
        public void paintIcon(
                Component c,
                Graphics graphics,
                int x,
                int y) {

            Graphics2D g =
                    copy(graphics);


            try {

                g.setColor(color);
                g.setStroke(STROKE_BOLD);


                g.draw(
                        new Ellipse2D.Double(
                                x + 2,
                                y + 2,
                                size - 4,
                                size - 4
                        )
                );


                Path2D check =
                        new Path2D.Double();


                check.moveTo(
                        x + size * 0.27,
                        y + size * 0.52
                );

                check.lineTo(
                        x + size * 0.44,
                        y + size * 0.69
                );

                check.lineTo(
                        x + size * 0.75,
                        y + size * 0.34
                );


                g.draw(check);

            } finally {

                g.dispose();
            }
        }
    }


    // ========================================================
    // GO
    // ========================================================

    static class GoIcon
            extends VectorIcon {

        GoIcon(
                int size,
                Color color) {

            super(
                    size,
                    color
            );
        }


        @Override
        public void paintIcon(
                Component c,
                Graphics graphics,
                int x,
                int y) {

            Graphics2D g =
                    copy(graphics);


            try {

                g.setColor(color);


                Path2D p =
                        new Path2D.Double();


                p.moveTo(
                        x + 4,
                        y + 2
                );

                p.lineTo(
                        x + size - 3,
                        y + size / 2.0
                );

                p.lineTo(
                        x + 4,
                        y + size - 2
                );

                p.closePath();


                g.fill(p);

            } finally {

                g.dispose();
            }
        }
    }


    // ========================================================
    // STATUS ICON
    // ========================================================

    static class StatusIcon
            extends VectorIcon {

        static final int PASS = 0;

        static final int WARNING = 1;

        static final int FAIL = 2;


        private final int type;


        StatusIcon(
                int type,
                int size,
                Color color) {

            super(
                    size,
                    color
            );

            this.type =
                    type;
        }


        @Override
        public void paintIcon(
                Component c,
                Graphics graphics,
                int x,
                int y) {

            Graphics2D g =
                    copy(graphics);


            try {

                g.setColor(color);
                g.setStroke(STROKE_NORMAL);


                if (type == PASS) {

                    g.drawOval(
                            x + 1,
                            y + 1,
                            size - 3,
                            size - 3
                    );


                    Path2D check =
                            new Path2D.Double();


                    check.moveTo(
                            x + 4,
                            y + 7
                    );

                    check.lineTo(
                            x + 6,
                            y + 9
                    );

                    check.lineTo(
                            x + 10,
                            y + 4
                    );


                    g.draw(check);


                } else if (
                        type == WARNING) {


                    Path2D tri =
                            new Path2D.Double();


                    tri.moveTo(
                            x + size / 2.0,
                            y + 1
                    );

                    tri.lineTo(
                            x + size - 1,
                            y + size - 2
                    );

                    tri.lineTo(
                            x + 1,
                            y + size - 2
                    );

                    tri.closePath();


                    g.draw(tri);


                    g.drawLine(
                            x + size / 2,
                            y + 4,
                            x + size / 2,
                            y + 8
                    );


                    g.fillOval(
                            x + size / 2 - 1,
                            y + 10,
                            2,
                            2
                    );


                } else {


                    g.drawOval(
                            x + 1,
                            y + 1,
                            size - 3,
                            size - 3
                    );


                    g.drawLine(
                            x + 4,
                            y + 4,
                            x + size - 5,
                            y + size - 5
                    );


                    g.drawLine(
                            x + size - 5,
                            y + 4,
                            x + 4,
                            y + size - 5
                    );
                }

            } finally {

                g.dispose();
            }
        }
    }


    // ========================================================
    // GRAPH
    // ========================================================

    static class GraphPanel
            extends JPanel {


        GraphPanel() {

            setBackground(
                    Color.WHITE
            );

            setPreferredSize(
                    new Dimension(
                            900,
                            230
                    )
            );
        }


        @Override
        protected void paintComponent(
                Graphics graphics) {

            super.paintComponent(
                    graphics
            );


            Graphics2D g =
                    (Graphics2D)
                            graphics.create();


            try {

                g.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );


                int w =
                        getWidth();


                GraphNode top =
                        new GraphNode(
                                w / 2 - 55,
                                18,
                                110,
                                38,
                                "TOP",
                                BLUE
                        );


                GraphNode a =
                        new GraphNode(
                                w / 4 - 55,
                                88,
                                110,
                                38,
                                "BLOCK_A",
                                PURPLE
                        );


                GraphNode b =
                        new GraphNode(
                                w * 3 / 4 - 55,
                                88,
                                110,
                                38,
                                "BLOCK_B",
                                PURPLE
                        );


                GraphNode a1 =
                        new GraphNode(
                                w / 8 - 55,
                                165,
                                110,
                                38,
                                "CELL_A1",
                                MUTED
                        );


                GraphNode a2 =
                        new GraphNode(
                                w * 3 / 8 - 55,
                                165,
                                110,
                                38,
                                "CELL_A2",
                                ORANGE
                        );


                GraphNode b1 =
                        new GraphNode(
                                w * 5 / 8 - 55,
                                165,
                                110,
                                38,
                                "CELL_B1",
                                MUTED
                        );


                GraphNode b2 =
                        new GraphNode(
                                w * 7 / 8 - 55,
                                165,
                                110,
                                38,
                                "CELL_B2",
                                MUTED
                        );


                drawEdge(g, top, a);
                drawEdge(g, top, b);

                drawEdge(g, a, a1);
                drawEdge(g, a, a2);

                drawEdge(g, b, b1);
                drawEdge(g, b, b2);


                drawNode(g, top);
                drawNode(g, a);
                drawNode(g, b);

                drawNode(g, a1);
                drawNode(g, a2);
                drawNode(g, b1);
                drawNode(g, b2);


            } finally {

                g.dispose();
            }
        }


        private void drawEdge(
                Graphics2D g,
                GraphNode from,
                GraphNode to) {

            int x1 =
                    from.x
                            + from.width / 2;

            int y1 =
                    from.y
                            + from.height;


            int x2 =
                    to.x
                            + to.width / 2;

            int y2 =
                    to.y;


            int middle =
                    (y1 + y2) / 2;


            g.setColor(
                    new Color(
                            150,
                            157,
                            165
                    )
            );

            g.setStroke(
                    STROKE_THIN
            );


            g.drawLine(
                    x1,
                    y1,
                    x1,
                    middle
            );

            g.drawLine(
                    x1,
                    middle,
                    x2,
                    middle
            );

            g.drawLine(
                    x2,
                    middle,
                    x2,
                    y2
            );
        }


        private void drawNode(
                Graphics2D g,
                GraphNode node) {

            Color c =
                    node.color;


            g.setColor(
                    new Color(
                            c.getRed(),
                            c.getGreen(),
                            c.getBlue(),
                            35
                    )
            );


            g.fillRoundRect(
                    node.x,
                    node.y,
                    node.width,
                    node.height,
                    10,
                    10
            );


            g.setColor(c);

            g.setStroke(
                    STROKE_NORMAL
            );


            g.drawRoundRect(
                    node.x,
                    node.y,
                    node.width,
                    node.height,
                    10,
                    10
            );


            g.setFont(
                    FONT_BOLD
            );


            FontMetrics fm =
                    g.getFontMetrics();


            int tx =
                    node.x
                            + (
                            node.width
                                    - fm.stringWidth(
                                    node.text
                            )
                    ) / 2;


            int ty =
                    node.y
                            + (
                            node.height
                                    + fm.getAscent()
                    ) / 2
                            - 3;


            g.setColor(TEXT);


            g.drawString(
                    node.text,
                    tx,
                    ty
            );
        }
    }


    static class GraphNode {

        int x;
        int y;

        int width;
        int height;

        String text;

        Color color;


        GraphNode(
                int x,
                int y,
                int width,
                int height,
                String text,
                Color color) {

            this.x = x;
            this.y = y;

            this.width = width;
            this.height = height;

            this.text = text;

            this.color = color;
        }
    }


    // ========================================================
    // MAIN
    // ========================================================

    public static void main(
            String[] args) {

        SwingUtilities.invokeLater(
                new Runnable() {

                    @Override
                    public void run() {

                        try {

                            UIManager.setLookAndFeel(
                                    UIManager
                                            .getSystemLookAndFeelClassName()
                            );

                        } catch (
                                Exception e) {

                            e.printStackTrace();
                        }


                        /*
                         * LookAndFeel 설정 후 Vector UI 적용.
                         */
                        installVectorUI();


                        FullVectorSwingMockup frame =
                                new FullVectorSwingMockup();


                        frame.setVisible(
                                true
                        );
                    }
                }
        );
    }
}
