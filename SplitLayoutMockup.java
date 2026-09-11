import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

public class SplitLayoutMockup extends JFrame {

    public SplitLayoutMockup() {
        super("Data Inspection Mockup");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        add(createToolBar(), BorderLayout.NORTH);
        add(createMainSplitPane(), BorderLayout.CENTER);
        add(createStatusBar(), BorderLayout.SOUTH);
    }

    /*
     * 전체:
     *
     * Left  : Tree
     * Right : 여러 영역
     */
    private JSplitPane createMainSplitPane() {

        JScrollPane treePane = createTreePanel();

        JPanel rightPanel = createRightPanel();

        JSplitPane mainSplit = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                treePane,
                rightPanel
        );

        mainSplit.setDividerLocation(300);
        mainSplit.setResizeWeight(0.20);
        mainSplit.setOneTouchExpandable(true);

        return mainSplit;
    }

    /*
     * 왼쪽 Tree
     */
    private JScrollPane createTreePanel() {

        DefaultMutableTreeNode root =
                new DefaultMutableTreeNode("Design");

        DefaultMutableTreeNode blockA =
                new DefaultMutableTreeNode("BLOCK_A");

        DefaultMutableTreeNode blockB =
                new DefaultMutableTreeNode("BLOCK_B");

        DefaultMutableTreeNode layer =
                new DefaultMutableTreeNode("Layers");

        blockA.add(new DefaultMutableTreeNode("CELL_A1"));
        blockA.add(new DefaultMutableTreeNode("CELL_A2"));
        blockA.add(new DefaultMutableTreeNode("CELL_A3"));

        blockB.add(new DefaultMutableTreeNode("CELL_B1"));
        blockB.add(new DefaultMutableTreeNode("CELL_B2"));

        layer.add(new DefaultMutableTreeNode("M1 / 0"));
        layer.add(new DefaultMutableTreeNode("M2 / 0"));
        layer.add(new DefaultMutableTreeNode("VIA1 / 0"));

        root.add(blockA);
        root.add(blockB);
        root.add(layer);

        JTree tree = new JTree(root);

        tree.setRootVisible(true);
        tree.setShowsRootHandles(true);

        JScrollPane scrollPane = new JScrollPane(tree);

        scrollPane.setBorder(
                BorderFactory.createTitledBorder("Design Tree")
        );

        return scrollPane;
    }

    /*
     * 오른쪽 전체
     *
     * Table Group
     * Text Area
     * Graph
     */
    private JPanel createRightPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        // ------------------------------
        // 상단: Table 영역
        // ------------------------------

        JPanel tableArea = createTableArea();

        // ------------------------------
        // 중간: Text
        // ------------------------------

        JScrollPane textArea = createTextArea();

        // ------------------------------
        // 하단: Graph
        // ------------------------------

        JPanel graphArea = createGraphPanel();

        /*
         * Table
         * -----
         * Text
         */
        JSplitPane upperSplit =
                new JSplitPane(
                        JSplitPane.VERTICAL_SPLIT,
                        tableArea,
                        textArea
                );

        upperSplit.setResizeWeight(0.65);
        upperSplit.setDividerLocation(470);
        upperSplit.setOneTouchExpandable(true);

        /*
         * [Table + Text]
         * --------------
         * Graph
         */
        JSplitPane finalSplit =
                new JSplitPane(
                        JSplitPane.VERTICAL_SPLIT,
                        upperSplit,
                        graphArea
                );

        finalSplit.setResizeWeight(0.78);
        finalSplit.setDividerLocation(650);
        finalSplit.setOneTouchExpandable(true);

        panel.add(finalSplit, BorderLayout.CENTER);

        return panel;
    }

    /*
     * Table 여러 개
     */
    private JPanel createTableArea() {

        JPanel panel = new JPanel();

        panel.setLayout(
                new BoxLayout(panel, BoxLayout.Y_AXIS)
        );

        JTable table1 = createTable(
                new String[]{
                        "Cell",
                        "Layer",
                        "Type",
                        "Count",
                        "Status"
                },
                new Object[][]{
                        {"CELL_A1", "M1", "BOX", 245, "OK"},
                        {"CELL_A1", "M2", "POLYGON", 132, "OK"},
                        {"CELL_A2", "VIA1", "BOX", 67, "CHECK"},
                        {"CELL_A3", "M1", "PATH", 28, "OK"}
                }
        );

        JTable table2 = createTable(
                new String[]{
                        "Feature",
                        "Reference",
                        "Current",
                        "Diff"
                },
                new Object[][]{
                        {"Density", "0.45", "0.42", "-0.03"},
                        {"Polygon Count", "122", "118", "-4"},
                        {"BBox Width", "125.2", "125.2", "0"},
                        {"Hierarchy Depth", "5", "5", "0"}
                }
        );

        JTable table3 = createTable(
                new String[]{
                        "Check Item",
                        "Result",
                        "Score"
                },
                new Object[][]{
                        {"Image Similarity", "PASS", "0.97"},
                        {"Geometry", "PASS", "0.99"},
                        {"Hierarchy", "PASS", "1.00"},
                        {"Rule Check", "REVIEW", "0.82"}
                }
        );

        JScrollPane sp1 = new JScrollPane(table1);
        JScrollPane sp2 = new JScrollPane(table2);
        JScrollPane sp3 = new JScrollPane(table3);

        sp1.setBorder(
                BorderFactory.createTitledBorder("Geometry")
        );

        sp2.setBorder(
                BorderFactory.createTitledBorder("Features")
        );

        sp3.setBorder(
                BorderFactory.createTitledBorder("Validation")
        );

        sp1.setPreferredSize(new Dimension(100, 150));
        sp2.setPreferredSize(new Dimension(100, 150));
        sp3.setPreferredSize(new Dimension(100, 150));

        panel.add(sp1);
        panel.add(sp2);
        panel.add(sp3);

        return panel;
    }

    private JTable createTable(
            String[] columns,
            Object[][] data) {

        DefaultTableModel model =
                new DefaultTableModel(data, columns);

        JTable table = new JTable(model);

        table.setRowHeight(24);
        table.setAutoCreateRowSorter(true);
        table.setFillsViewportHeight(true);

        return table;
    }

    /*
     * Text Area
     */
    private JScrollPane createTextArea() {

        JTextArea textArea = new JTextArea();

        textArea.setFont(
                new Font(
                        Font.MONOSPACED,
                        Font.PLAIN,
                        13
                )
        );

        textArea.setText(
                "# Inspection Detail\n" +
                "\n" +
                "CELL       : CELL_A2\n" +
                "POSITION   : (12500.42, 8421.33)\n" +
                "LAYER      : M1 / M2 / VIA1\n" +
                "\n" +
                "Image Similarity  : 0.971\n" +
                "Geometry Score    : 0.992\n" +
                "Hierarchy Score   : 1.000\n" +
                "\n" +
                "Result : REVIEW\n" +
                "\n" +
                "Reason:\n" +
                "- VIA1 count differs from reference.\n" +
                "- Geometry density is within tolerance.\n"
        );

        JScrollPane pane =
                new JScrollPane(textArea);

        pane.setBorder(
                BorderFactory.createTitledBorder(
                        "Detail / Log / Source"
                )
        );

        return pane;
    }

    /*
     * Graph Tree 영역
     */
    private JPanel createGraphPanel() {

        JPanel outer =
                new JPanel(new BorderLayout());

        outer.setBorder(
                BorderFactory.createTitledBorder(
                        "Cell / Dependency Graph"
                )
        );

        GraphMockPanel graph =
                new GraphMockPanel();

        outer.add(graph, BorderLayout.CENTER);

        return outer;
    }

    /*
     * Toolbar
     */
    private JToolBar createToolBar() {

        JToolBar toolBar = new JToolBar();

        toolBar.setFloatable(false);

        toolBar.add(new JButton("Open"));
        toolBar.add(new JButton("Reload"));

        toolBar.addSeparator();

        toolBar.add(new JButton("Compare"));
        toolBar.add(new JButton("Validate"));

        toolBar.addSeparator();

        toolBar.add(new JLabel(" ROI : "));

        JTextField roiField =
                new JTextField(
                        "12500.42, 8421.33",
                        20
                );

        toolBar.add(roiField);

        toolBar.add(new JButton("Go"));

        return toolBar;
    }

    /*
     * 하단 상태바
     */
    private JPanel createStatusBar() {

        JPanel panel =
                new JPanel(new BorderLayout());

        panel.setBorder(
                new EmptyBorder(4, 8, 4, 8)
        );

        panel.add(
                new JLabel(
                        "Ready | Design : sample.oas"
                ),
                BorderLayout.WEST
        );

        panel.add(
                new JLabel(
                        "Memory : 512 MB"
                ),
                BorderLayout.EAST
        );

        return panel;
    }

    /*
     * Graph 목업
     *
     * 실제 Graph library 없이
     * Swing Graphics2D 로 간단히 표현
     */
    static class GraphMockPanel extends JPanel {

        public GraphMockPanel() {
            setPreferredSize(
                    new Dimension(600, 200)
            );

            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {

            super.paintComponent(g);

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int w = getWidth();

            Node top =
                    new Node(
                            w / 2 - 50,
                            15,
                            100,
                            35,
                            "TOP"
                    );

            Node blockA =
                    new Node(
                            w / 4 - 50,
                            75,
                            100,
                            35,
                            "BLOCK_A"
                    );

            Node blockB =
                    new Node(
                            w * 3 / 4 - 50,
                            75,
                            100,
                            35,
                            "BLOCK_B"
                    );

            Node cellA1 =
                    new Node(
                            w / 8 - 50,
                            140,
                            100,
                            35,
                            "CELL_A1"
                    );

            Node cellA2 =
                    new Node(
                            w * 3 / 8 - 50,
                            140,
                            100,
                            35,
                            "CELL_A2"
                    );

            Node cellB1 =
                    new Node(
                            w * 5 / 8 - 50,
                            140,
                            100,
                            35,
                            "CELL_B1"
                    );

            Node cellB2 =
                    new Node(
                            w * 7 / 8 - 50,
                            140,
                            100,
                            35,
                            "CELL_B2"
                    );

            // Edge
            drawEdge(g2, top, blockA);
            drawEdge(g2, top, blockB);

            drawEdge(g2, blockA, cellA1);
            drawEdge(g2, blockA, cellA2);

            drawEdge(g2, blockB, cellB1);
            drawEdge(g2, blockB, cellB2);

            // Node
            drawNode(g2, top);
            drawNode(g2, blockA);
            drawNode(g2, blockB);

            drawNode(g2, cellA1);
            drawNode(g2, cellA2);
            drawNode(g2, cellB1);
            drawNode(g2, cellB2);

            g2.dispose();
        }

        private void drawEdge(
                Graphics2D g2,
                Node from,
                Node to) {

            int x1 =
                    from.x + from.width / 2;

            int y1 =
                    from.y + from.height;

            int x2 =
                    to.x + to.width / 2;

            int y2 =
                    to.y;

            g2.setColor(Color.GRAY);

            g2.drawLine(
                    x1,
                    y1,
                    x2,
                    y2
            );
        }

        private void drawNode(
                Graphics2D g2,
                Node node) {

            g2.setColor(
                    new Color(235, 240, 245)
            );

            g2.fillRoundRect(
                    node.x,
                    node.y,
                    node.width,
                    node.height,
                    10,
                    10
            );

            g2.setColor(Color.DARK_GRAY);

            g2.drawRoundRect(
                    node.x,
                    node.y,
                    node.width,
                    node.height,
                    10,
                    10
            );

            FontMetrics fm =
                    g2.getFontMetrics();

            int tx =
                    node.x +
                    (node.width -
                            fm.stringWidth(node.text))
                            / 2;

            int ty =
                    node.y +
                    (node.height +
                            fm.getAscent())
                            / 2 - 3;

            g2.drawString(
                    node.text,
                    tx,
                    ty
            );
        }
    }

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

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            try {

                UIManager.setLookAndFeel(
                        UIManager
                                .getSystemLookAndFeelClassName()
                );

            } catch (Exception ignored) {
            }

            SplitLayoutMockup frame =
                    new SplitLayoutMockup();

            frame.setVisible(true);
        });
    }
}
