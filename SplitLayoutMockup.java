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

public class SplitLayoutMockup extends JFrame {

    // =========================================================
    // UI Constants
    // =========================================================

    private static final Font SECTION_TITLE_FONT =
            new Font(Font.SANS_SERIF, Font.BOLD, 15);

    private static final Font TABLE_HEADER_FONT =
            new Font(Font.SANS_SERIF, Font.BOLD, 13);

    private static final Font NORMAL_FONT =
            new Font(Font.SANS_SERIF, Font.PLAIN, 13);

    private static final Font MONO_FONT =
            new Font(Font.MONOSPACED, Font.PLAIN, 13);

    private static final int TABLE_GAP = 12;

    private static final Color HEADER_BACKGROUND =
            new Color(232, 236, 241);

    private static final Color HEADER_FOREGROUND =
            new Color(45, 50, 56);

    private static final Color PANEL_BACKGROUND =
            new Color(246, 247, 249);

    private static final Color GRAPH_NODE_BACKGROUND =
            new Color(235, 240, 245);

    private static final Color GRAPH_NODE_BORDER =
            new Color(90, 100, 110);


    // =========================================================
    // Constructor
    // =========================================================

    public SplitLayoutMockup() {

        super("Data Inspection Mockup");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(1450, 950);

        setMinimumSize(
                new Dimension(1100, 750)
        );

        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        add(
                createToolBar(),
                BorderLayout.NORTH
        );

        add(
                createMainSplitPane(),
                BorderLayout.CENTER
        );

        add(
                createStatusBar(),
                BorderLayout.SOUTH
        );
    }


    // =========================================================
    // Main Layout
    // =========================================================

    private JSplitPane createMainSplitPane() {

        JPanel treePanel =
                createTreePanel();

        JPanel rightPanel =
                createRightPanel();

        JSplitPane mainSplit =
                new JSplitPane(
                        JSplitPane.HORIZONTAL_SPLIT,
                        treePanel,
                        rightPanel
                );

        mainSplit.setDividerLocation(300);

        mainSplit.setResizeWeight(0.20);

        mainSplit.setDividerSize(7);

        mainSplit.setOneTouchExpandable(true);

        mainSplit.setContinuousLayout(true);

        return mainSplit;
    }


    // =========================================================
    // LEFT : Tree
    // =========================================================

    private JPanel createTreePanel() {

        JPanel container =
                new JPanel(new BorderLayout());

        container.setBorder(
                new EmptyBorder(
                        8,
                        8,
                        8,
                        5
                )
        );

        container.setBackground(
                PANEL_BACKGROUND
        );


        // -----------------------------------------------------
        // Tree Node
        // -----------------------------------------------------

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

        DefaultMutableTreeNode layer =
                new DefaultMutableTreeNode(
                        "Layers"
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


        layer.add(
                new DefaultMutableTreeNode(
                        "M1 / 0"
                )
        );

        layer.add(
                new DefaultMutableTreeNode(
                        "M2 / 0"
                )
        );

        layer.add(
                new DefaultMutableTreeNode(
                        "VIA1 / 0"
                )
        );


        root.add(blockA);
        root.add(blockB);
        root.add(layer);


        // -----------------------------------------------------
        // JTree
        // -----------------------------------------------------

        JTree tree =
                new JTree(root);

        tree.setRootVisible(true);

        tree.setShowsRootHandles(true);

        tree.setFont(
                NORMAL_FONT
        );

        /*
         * 기본 Swing Tree는 상당히 조밀하기 때문에
         * Row Height를 증가시킴.
         */
        tree.setRowHeight(27);


        // -----------------------------------------------------
        // Tree Cell Renderer
        // -----------------------------------------------------

        DefaultTreeCellRenderer renderer =
                new DefaultTreeCellRenderer();

        renderer.setBorder(
                new EmptyBorder(
                        3,
                        6,
                        3,
                        6
                )
        );

        renderer.setFont(
                NORMAL_FONT
        );

        tree.setCellRenderer(
                renderer
        );


        // -----------------------------------------------------
        // Tree Scroll
        // -----------------------------------------------------

        JScrollPane treeScroll =
                new JScrollPane(tree);

        treeScroll.setBorder(
                createSectionBorder(
                        "Design Tree"
                )
        );


        container.add(
                treeScroll,
                BorderLayout.CENTER
        );


        return container;
    }


    // =========================================================
    // RIGHT
    // =========================================================

    private JPanel createRightPanel() {

        JPanel panel =
                new JPanel(
                        new BorderLayout()
                );

        panel.setBackground(
                PANEL_BACKGROUND
        );

        panel.setBorder(
                new EmptyBorder(
                        8,
                        5,
                        8,
                        8
                )
        );


        // -----------------------------------------------------
        // Table Area
        // -----------------------------------------------------

        JPanel tableArea =
                createTableArea();


        // -----------------------------------------------------
        // Text Area
        // -----------------------------------------------------

        JPanel textArea =
                createTextArea();


        // -----------------------------------------------------
        // Graph Area
        // -----------------------------------------------------

        JPanel graphArea =
                createGraphPanel();


        // -----------------------------------------------------
        // Table + Text
        // -----------------------------------------------------

        JSplitPane upperSplit =
                new JSplitPane(
                        JSplitPane.VERTICAL_SPLIT,
                        tableArea,
                        textArea
                );

        upperSplit.setResizeWeight(
                0.68
        );

        upperSplit.setDividerLocation(
                500
        );

        upperSplit.setDividerSize(
                7
        );

        upperSplit.setOneTouchExpandable(
                true
        );

        upperSplit.setContinuousLayout(
                true
        );


        // -----------------------------------------------------
        // Upper + Graph
        // -----------------------------------------------------

        JSplitPane finalSplit =
                new JSplitPane(
                        JSplitPane.VERTICAL_SPLIT,
                        upperSplit,
                        graphArea
                );

        finalSplit.setResizeWeight(
                0.80
        );

        finalSplit.setDividerLocation(
                700
        );

        finalSplit.setDividerSize(
                7
        );

        finalSplit.setOneTouchExpandable(
                true
        );

        finalSplit.setContinuousLayout(
                true
        );


        panel.add(
                finalSplit,
                BorderLayout.CENTER
        );

        return panel;
    }


    // =========================================================
    // Table Area
    // =========================================================

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
                PANEL_BACKGROUND
        );

        panel.setBorder(
                new EmptyBorder(
                        3,
                        3,
                        5,
                        3
                )
        );


        // =====================================================
        // Table #1 : Geometry
        // =====================================================

        JTable geometryTable =
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
                                        "CHECK"
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


        // =====================================================
        // Table #2 : Feature
        // =====================================================

        JTable featureTable =
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


        // =====================================================
        // Table #3 : Validation
        // =====================================================

        JTable validationTable =
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


        // 상태 컬럼 강조
        geometryTable
                .getColumnModel()
                .getColumn(4)
                .setCellRenderer(
                        new StatusCellRenderer()
                );

        validationTable
                .getColumnModel()
                .getColumn(1)
                .setCellRenderer(
                        new StatusCellRenderer()
                );


        // =====================================================
        // ScrollPane
        // =====================================================

        JScrollPane sp1 =
                new JScrollPane(
                        geometryTable
                );

        JScrollPane sp2 =
                new JScrollPane(
                        featureTable
                );

        JScrollPane sp3 =
                new JScrollPane(
                        validationTable
                );


        sp1.setBorder(
                createSectionBorder(
                        "Geometry"
                )
        );

        sp2.setBorder(
                createSectionBorder(
                        "Features"
                )
        );

        sp3.setBorder(
                createSectionBorder(
                        "Validation"
                )
        );


        // -----------------------------------------------------
        // BoxLayout 폭 문제 방지
        // -----------------------------------------------------

        Dimension maxSize =
                new Dimension(
                        Integer.MAX_VALUE,
                        170
                );

        sp1.setMaximumSize(maxSize);
        sp2.setMaximumSize(maxSize);
        sp3.setMaximumSize(maxSize);


        sp1.setPreferredSize(
                new Dimension(
                        100,
                        155
                )
        );

        sp2.setPreferredSize(
                new Dimension(
                        100,
                        155
                )
        );

        sp3.setPreferredSize(
                new Dimension(
                        100,
                        155
                )
        );


        // -----------------------------------------------------
        // Components
        // -----------------------------------------------------

        panel.add(sp1);

        panel.add(
                Box.createVerticalStrut(
                        TABLE_GAP
                )
        );

        panel.add(sp2);

        panel.add(
                Box.createVerticalStrut(
                        TABLE_GAP
                )
        );

        panel.add(sp3);


        return panel;
    }


    // =========================================================
    // Common JTable
    // =========================================================

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
                NORMAL_FONT
        );

        table.setRowHeight(
                27
        );

        table.setIntercellSpacing(
                new Dimension(
                        1,
                        3
                )
        );

        table.setAutoCreateRowSorter(
                true
        );

        table.setFillsViewportHeight(
                true
        );

        table.setSelectionMode(
                ListSelectionModel
                        .SINGLE_SELECTION
        );

        table.setShowHorizontalLines(
                true
        );

        table.setShowVerticalLines(
                false
        );

        table.setGridColor(
                new Color(
                        225,
                        228,
                        232
                )
        );


        // -----------------------------------------------------
        // Header
        // -----------------------------------------------------

        JTableHeader header =
                table.getTableHeader();

        header.setFont(
                TABLE_HEADER_FONT
        );

        header.setForeground(
                HEADER_FOREGROUND
        );

        header.setBackground(
                HEADER_BACKGROUND
        );

        header.setOpaque(true);

        header.setReorderingAllowed(
                false
        );

        header.setPreferredSize(
                new Dimension(
                        header
                                .getPreferredSize()
                                .width,
                        32
                )
        );


        return table;
    }


    // =========================================================
    // Text Area
    // =========================================================

    private JPanel createTextArea() {

        JPanel container =
                new JPanel(
                        new BorderLayout()
                );

        container.setBackground(
                PANEL_BACKGROUND
        );

        container.setBorder(
                new EmptyBorder(
                        5,
                        3,
                        5,
                        3
                )
        );


        JTextArea textArea =
                new JTextArea();

        textArea.setFont(
                MONO_FONT
        );

        textArea.setMargin(
                new Insets(
                        8,
                        10,
                        8,
                        10
                )
        );

        textArea.setLineWrap(
                false
        );

        textArea.setText(
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
                "----------------------------------------\n" +
                "- VIA1 count differs from reference.\n" +
                "- Geometry density is within tolerance.\n"
        );


        JScrollPane scrollPane =
                new JScrollPane(
                        textArea
                );

        scrollPane.setBorder(
                createSectionBorder(
                        "Detail / Log / Source"
                )
        );


        container.add(
                scrollPane,
                BorderLayout.CENTER
        );


        return container;
    }


    // =========================================================
    // Graph Panel
    // =========================================================

    private JPanel createGraphPanel() {

        JPanel outer =
                new JPanel(
                        new BorderLayout()
                );

        outer.setBackground(
                PANEL_BACKGROUND
        );

        outer.setBorder(
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


        GraphMockPanel graph =
                new GraphMockPanel();


        JScrollPane graphScroll =
                new JScrollPane(
                        graph
                );

        graphScroll.setBorder(
                BorderFactory.createEmptyBorder()
        );


        outer.add(
                graphScroll,
                BorderLayout.CENTER
        );


        return outer;
    }


    // =========================================================
    // Toolbar
    // =========================================================

    private JToolBar createToolBar() {

        JToolBar toolBar =
                new JToolBar();

        toolBar.setFloatable(
                false
        );

        toolBar.setBorder(
                new EmptyBorder(
                        5,
                        7,
                        5,
                        7
                )
        );


        JButton openButton =
                new JButton("Open");

        JButton reloadButton =
                new JButton("Reload");

        JButton compareButton =
                new JButton("Compare");

        JButton validateButton =
                new JButton("Validate");

        JButton goButton =
                new JButton("Go");


        toolBar.add(openButton);

        toolBar.add(
                Box.createHorizontalStrut(5)
        );

        toolBar.add(reloadButton);


        toolBar.addSeparator(
                new Dimension(
                        15,
                        20
                )
        );


        toolBar.add(compareButton);

        toolBar.add(
                Box.createHorizontalStrut(5)
        );

        toolBar.add(validateButton);


        toolBar.addSeparator(
                new Dimension(
                        15,
                        20
                )
        );


        toolBar.add(
                new JLabel(
                        "ROI : "
                )
        );


        JTextField roiField =
                new JTextField(
                        "12500.42, 8421.33",
                        20
                );

        roiField.setMaximumSize(
                new Dimension(
                        220,
                        28
                )
        );


        toolBar.add(roiField);

        toolBar.add(
                Box.createHorizontalStrut(5)
        );

        toolBar.add(goButton);


        return toolBar;
    }


    // =========================================================
    // Status Bar
    // =========================================================

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
                                new Color(
                                        210,
                                        213,
                                        218
                                )
                        ),

                        new EmptyBorder(
                                5,
                                8,
                                5,
                                8
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


        left.setFont(
                new Font(
                        Font.SANS_SERIF,
                        Font.PLAIN,
                        12
                )
        );

        right.setFont(
                new Font(
                        Font.SANS_SERIF,
                        Font.PLAIN,
                        12
                )
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


    // =========================================================
    // Section Border
    // =========================================================

    private Border createSectionBorder(
            String title) {

        TitledBorder titledBorder =
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(
                                new Color(
                                        200,
                                        204,
                                        210
                                )
                        ),
                        title
                );


        titledBorder.setTitleFont(
                SECTION_TITLE_FONT
        );

        titledBorder.setTitleColor(
                new Color(
                        45,
                        50,
                        55
                )
        );


        /*
         * TitledBorder + 내부 Padding
         */
        return BorderFactory.createCompoundBorder(

                titledBorder,

                new EmptyBorder(
                        6,
                        7,
                        7,
                        7
                )
        );
    }


    // =========================================================
    // Status Renderer
    // =========================================================

    static class StatusCellRenderer
            extends DefaultTableCellRenderer {

        public StatusCellRenderer() {

            setHorizontalAlignment(
                    SwingConstants.CENTER
            );

            setFont(
                    new Font(
                            Font.SANS_SERIF,
                            Font.BOLD,
                            12
                    )
            );
        }


        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {

            Component component =
                    super.getTableCellRendererComponent(
                            table,
                            value,
                            isSelected,
                            hasFocus,
                            row,
                            column
                    );


            if (!isSelected) {

                component.setBackground(
                        Color.WHITE
                );

                component.setForeground(
                        Color.DARK_GRAY
                );


                if (value != null) {

                    String status =
                            value
                                    .toString()
                                    .toUpperCase();


                    if ("PASS".equals(status)
                            || "OK".equals(status)) {

                        component.setForeground(
                                new Color(
                                        30,
                                        125,
                                        65
                                )
                        );

                    } else if (
                            "REVIEW".equals(status)
                                    || "CHECK".equals(status)) {

                        component.setForeground(
                                new Color(
                                        185,
                                        115,
                                        20
                                )
                        );

                    } else if (
                            "FAIL".equals(status)
                                    || "ERROR".equals(status)) {

                        component.setForeground(
                                new Color(
                                        185,
                                        45,
                                        45
                                )
                        );
                    }
                }
            }


            return component;
        }
    }


    // =========================================================
    // Graph Mock Panel
    // =========================================================

    static class GraphMockPanel
            extends JPanel {

        GraphMockPanel() {

            setPreferredSize(
                    new Dimension(
                            900,
                            220
                    )
            );

            setBackground(
                    Color.WHITE
            );
        }


        @Override
        protected void paintComponent(
                Graphics g) {

            super.paintComponent(g);


            Graphics2D g2 =
                    (Graphics2D) g.create();


            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );


            int width =
                    getWidth();


            // -------------------------------------------------
            // Node Position
            // -------------------------------------------------

            Node top =
                    new Node(
                            width / 2 - 55,
                            20,
                            110,
                            38,
                            "TOP"
                    );


            Node blockA =
                    new Node(
                            width / 4 - 55,
                            85,
                            110,
                            38,
                            "BLOCK_A"
                    );


            Node blockB =
                    new Node(
                            width * 3 / 4 - 55,
                            85,
                            110,
                            38,
                            "BLOCK_B"
                    );


            Node cellA1 =
                    new Node(
                            width / 8 - 55,
                            155,
                            110,
                            38,
                            "CELL_A1"
                    );


            Node cellA2 =
                    new Node(
                            width * 3 / 8 - 55,
                            155,
                            110,
                            38,
                            "CELL_A2"
                    );


            Node cellB1 =
                    new Node(
                            width * 5 / 8 - 55,
                            155,
                            110,
                            38,
                            "CELL_B1"
                    );


            Node cellB2 =
                    new Node(
                            width * 7 / 8 - 55,
                            155,
                            110,
                            38,
                            "CELL_B2"
                    );


            // -------------------------------------------------
            // Edge
            // -------------------------------------------------

            drawEdge(
                    g2,
                    top,
                    blockA
            );

            drawEdge(
                    g2,
                    top,
                    blockB
            );


            drawEdge(
                    g2,
                    blockA,
                    cellA1
            );

            drawEdge(
                    g2,
                    blockA,
                    cellA2
            );


            drawEdge(
                    g2,
                    blockB,
                    cellB1
            );

            drawEdge(
                    g2,
                    blockB,
                    cellB2
            );


            // -------------------------------------------------
            // Node
            // -------------------------------------------------

            drawNode(
                    g2,
                    top
            );

            drawNode(
                    g2,
                    blockA
            );

            drawNode(
                    g2,
                    blockB
            );

            drawNode(
                    g2,
                    cellA1
            );

            drawNode(
                    g2,
                    cellA2
            );

            drawNode(
                    g2,
                    cellB1
            );

            drawNode(
                    g2,
                    cellB2
            );


            g2.dispose();
        }


        private void drawEdge(
                Graphics2D g2,
                Node from,
                Node to) {

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


            g2.setColor(
                    new Color(
                            150,
                            155,
                            162
                    )
            );

            g2.setStroke(
                    new BasicStroke(
                            1.3f
                    )
            );


            /*
             * 직선 대신 중간 horizontal line을 사용하여
             * Tree 느낌을 약간 강화
             */

            int middleY =
                    (y1 + y2) / 2;


            g2.drawLine(
                    x1,
                    y1,
                    x1,
                    middleY
            );

            g2.drawLine(
                    x1,
                    middleY,
                    x2,
                    middleY
            );

            g2.drawLine(
                    x2,
                    middleY,
                    x2,
                    y2
            );
        }


        private void drawNode(
                Graphics2D g2,
                Node node) {

            g2.setColor(
                    GRAPH_NODE_BACKGROUND
            );


            g2.fillRoundRect(
                    node.x,
                    node.y,
                    node.width,
                    node.height,
                    10,
                    10
            );


            g2.setColor(
                    GRAPH_NODE_BORDER
            );


            g2.drawRoundRect(
                    node.x,
                    node.y,
                    node.width,
                    node.height,
                    10,
                    10
            );


            g2.setFont(
                    new Font(
                            Font.SANS_SERIF,
                            Font.BOLD,
                            12
                    )
            );


            FontMetrics metrics =
                    g2.getFontMetrics();


            int textX =
                    node.x
                            + (
                            node.width
                                    - metrics.stringWidth(
                                    node.text
                            )
                    ) / 2;


            int textY =
                    node.y
                            + (
                            node.height
                                    + metrics.getAscent()
                    ) / 2
                            - 3;


            g2.drawString(
                    node.text,
                    textX,
                    textY
            );
        }
    }


    // =========================================================
    // Graph Node
    // =========================================================

    static class Node {

        int x;
        int y;

        int width;
        int height;

        String text;


        Node(
                int x,
                int y,
                int width,
                int height,
                String text) {

            this.x = x;
            this.y = y;

            this.width = width;
            this.height = height;

            this.text = text;
        }
    }


    // =========================================================
    // Main
    // =========================================================

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

                        } catch (Exception e) {

                            e.printStackTrace();
                        }


                        SplitLayoutMockup frame =
                                new SplitLayoutMockup();


                        frame.setVisible(
                                true
                        );
                    }
                }
        );
    }
}
