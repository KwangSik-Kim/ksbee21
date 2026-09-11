import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.table.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.StringSelection;
import java.awt.geom.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Java 8+ / Swing + Java2D only.
 * This is an EVENT / MODULE COMPOSITION demo, not a production repository.
 * Save is memory-only; scores are sample constants; Compare is side-by-side
 * text preview, NOT a diff algorithm. Closing the program discards all data.
 *
 * javac -encoding UTF-8 ModularEventMockup.java
 * java ModularEventMockup                 (full workspace)
 * java ModularEventMockup edit            (no history / graph)
 * java ModularEventMockup history         (read-only history view)
 *
 * Classes are nested only to make this example easy to copy and compile.
 * Main areas: Data -> Service -> Controller -> View / Modules -> EventAdapter.
 * Context menus: tree, all three tables, graph nodes and empty canvas areas.
 * Popup actions capture stable IDs; opening a popup does not open a file.
 * Shift+F10 / context-menu key opens the menu for the focused selection.
 */
public class ModularEventMockup {
    // ---- UI theme / cached Java2D icons ------------------------------------
    static final Color BG = new Color(244, 247, 250), WHITE = Color.WHITE;
    static final Color INK = new Color(42, 54, 68), MUTED = new Color(104, 118, 132);
    static final Color LINE = new Color(213, 222, 231), HEADER = new Color(231, 238, 246);
    static final Color BLUE = new Color(44, 106, 174), PURPLE = new Color(124, 81, 157);
    static final Color GREEN = new Color(38, 125, 84), AMBER = new Color(166, 104, 18);
    static final Color SELECT = new Color(221, 235, 252), STRIPE = new Color(249, 251, 253);
    static final Font BODY = new Font(Font.SANS_SERIF, Font.PLAIN, 13);
    static final Font BOLD = BODY.deriveFont(Font.BOLD), TITLE = BODY.deriveFont(Font.BOLD, 15f);
    static final Font MONO = new Font(Font.MONOSPACED, Font.PLAIN, 12);
    static final Font SMALL = BODY.deriveFont(11f);
    static final BasicStroke STROKE = new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    static final Icon RIGHT = new VectorIcon("right", MUTED), DOWN = new VectorIcon("down", BLUE);
    static final Icon UP = new VectorIcon("up", BLUE), PRODUCT = new VectorIcon("box", BLUE);
    static final Icon FAMILY = new VectorIcon("box", PURPLE), FILE = new VectorIcon("file", BLUE);
    static final Icon SAVE = new VectorIcon("save", GREEN), COMPARE = new VectorIcon("compare", PURPLE);
    static final Icon GENERATE = new VectorIcon("plus", AMBER);
    static final Icon COPY = new VectorIcon("copy", BLUE), INFO = new VectorIcon("info", MUTED);
    static final Icon REFRESH = new VectorIcon("refresh", GREEN);
    static final DateTimeFormatter CLOCK = DateTimeFormatter.ofPattern("HH:mm:ss");

    enum Preset { FULL, EDIT, HISTORY }

    // =====================================================================
    // 1. DATA: immutable inputs / outputs. No Swing classes.
    // =====================================================================
    static final class FileInfo {
        final String id, productId, name, group, head;
        FileInfo(String id, String productId, String name, String group, String head) {
            this.id = id; this.productId = productId; this.name = name;
            this.group = group; this.head = head;
        }
    }
    static final class Revision {
        final String id, parentId, text, note;
        Revision(String id, String parentId, String text, String note) {
            this.id = id; this.parentId = parentId; this.text = text; this.note = note;
        }
    }
    static final class Candidate {
        final String id, name, reason;
        final int sampleScore;
        Candidate(String id, String name, int sampleScore, String reason) {
            this.id = id; this.name = name; this.sampleScore = sampleScore; this.reason = reason;
        }
    }
    static final class WorkspaceData {
        final FileInfo file;
        final List<Revision> revisions;
        final List<Candidate> candidates;
        WorkspaceData(FileInfo file, List<Revision> revisions, List<Candidate> candidates) {
            this.file = file;
            this.revisions = Collections.unmodifiableList(new ArrayList<>(revisions));
            this.candidates = Collections.unmodifiableList(new ArrayList<>(candidates));
        }
        Revision latest() { return revisions.get(revisions.size() - 1); }
    }
    static final class SaveCommand {
        final String fileId, baseRevision, text;
        SaveCommand(String fileId, String baseRevision, String text) {
            this.fileId = Objects.requireNonNull(fileId);
            this.baseRevision = Objects.requireNonNull(baseRevision);
            this.text = Objects.requireNonNull(text);
        }
    }

    // =====================================================================
    // 2. SERVICE PORT / MOCK: independent of UI and event types.
    // =====================================================================
    interface WorkspaceService {
        List<FileInfo> filesFor(String productId);
        WorkspaceData load(String fileId);
        Revision revision(String fileId, String revisionId);
        Revision save(SaveCommand command);
        String generatePreview(String targetId, String candidateId, String currentText);
    }
    static final class MockWorkspaceService implements WorkspaceService {
        static final class Entry {
            final String id, product, name, group;
            final List<Revision> revisions = new ArrayList<>();
            Entry(String product, String group, String suffix) {
                this.id = product + "-" + group; this.product = product; this.group = group;
                name = product + "_M1_" + suffix + ".tcl";
                for (int i = 1; i <= 3; i++) {
                    String text = "# MOCK DATA - not a production runset\n"
                            + "# " + name + " / r00" + i + "\n"
                            + "set PRODUCT " + product + "\nset MODULE_GROUP " + group
                            + "\nset TARGET_LAYER M1\nset CHECK_MODE " + suffix.toUpperCase(Locale.ROOT)
                            + "\nset MIN_VALUE 0.0" + (3 + i) + "0\n";
                    revisions.add(new Revision("r00" + i, i == 1 ? null : "r00" + (i - 1),
                            text, i == 1 ? "Imported sample" : "Sample revision " + i));
                }
            }
            FileInfo info() { return new FileInfo(id, product, name, group, last().id); }
            Revision last() { return revisions.get(revisions.size() - 1); }
        }
        final Map<String, Entry> entries = new LinkedHashMap<>();
        MockWorkspaceService() {
            for (String product : Arrays.asList("P100", "P200", "P300")) {
                String[] suffixes = {"width", "spacing", "via"};
                for (int i = 0; i < suffixes.length; i++) {
                    Entry e = new Entry(product, String.valueOf((char) ('A' + i)), suffixes[i]);
                    entries.put(e.id, e);
                }
            }
        }
        Entry entry(String id) {
            Entry e = entries.get(id);
            if (e == null) throw new IllegalArgumentException("Unknown file: " + id);
            return e;
        }
        public List<FileInfo> filesFor(String productId) {
            List<FileInfo> result = new ArrayList<>();
            for (Entry e : entries.values()) if (e.product.equals(productId)) result.add(e.info());
            return Collections.unmodifiableList(result);
        }
        public WorkspaceData load(String fileId) {
            Entry current = entry(fileId);
            List<Candidate> candidates = new ArrayList<>();
            for (Entry e : entries.values()) {
                if (!e.id.equals(fileId) && e.group.equals(current.group)) {
                    candidates.add(new Candidate(e.id, e.name, candidates.isEmpty() ? 92 : 85,
                            "Same group " + e.group + " (sample score; not calculated)"));
                }
            }
            return new WorkspaceData(current.info(), current.revisions, candidates);
        }
        public Revision revision(String fileId, String revisionId) {
            for (Revision r : entry(fileId).revisions) if (r.id.equals(revisionId)) return r;
            throw new IllegalArgumentException("Unknown revision: " + revisionId);
        }
        public Revision save(SaveCommand command) {
            Entry e = entry(command.fileId);
            if (!e.last().id.equals(command.baseRevision))
                throw new IllegalStateException("Base revision changed. Compare before saving.");
            if (command.text.trim().isEmpty()) throw new IllegalArgumentException("Empty text is not saved.");
            Revision r = new Revision(String.format(Locale.ROOT, "r%03d", e.revisions.size() + 1),
                    e.last().id, command.text, "Saved in memory only");
            e.revisions.add(r);
            return r;
        }
        public String generatePreview(String targetId, String candidateId, String currentText) {
            entry(targetId);
            Entry source = entry(candidateId);
            // A harmless demo operation: append a note, do not overwrite edits.
            // A real generation recipe / parser is intentionally NOT implemented.
            return currentText + "\n# MOCK generation request from " + source.id + " / " + source.last().id
                    + "\n# A real generation service would apply its recipe here.\n";
        }
    }

    // =====================================================================
    // 3. CONTROLLER CONTRACT / VIEW PORT.
    // The interface is shared as a contract, NOT as a global singleton.
    // =====================================================================
    interface WorkspaceActions {
        void selectProduct(String productId);
        void selectFile(String fileId);
        void selectCandidate(String fileId);
        void previewRevision(String revisionId);
        void editorChanged(String text);
        void compare();
        void generate();
        void save();
        // Context-menu commands use stable IDs, never Swing events or row numbers.
        void refreshProduct(String productId);
        void inspectFile(String fileId);
        void compareCandidate(String ownerFileId, String candidateId);
        void generateFromCandidate(String ownerFileId, String candidateId);
        void previewRevision(String ownerFileId, String revisionId);
        void inspectRevision(String ownerFileId, String revisionId);
    }
    interface WorkspaceViewPort {
        void showFiles(String productId, List<FileInfo> files);
        void showWorkspace(WorkspaceData data, String draftText, boolean dirty);
        void showComparison(String label, String reference, String current, String revisionId);
        void showCandidate(String candidateId);
        void markDirty(boolean dirty);
        void capabilities(boolean save, boolean compare, boolean generate);
        void status(String text);
        void trace(String stage, String text);
    }
    static final class Draft {
        String baseRevision, baseline, text;
        Draft(Revision r) { baseRevision = r.id; baseline = r.text; text = r.text; }
        boolean dirty() { return !baseline.equals(text); }
    }
    static final class WorkspaceController implements WorkspaceActions {
        final WorkspaceViewPort view;
        final WorkspaceService service;
        final boolean editable;
        final Map<String, Draft> drafts = new HashMap<>(); // one workspace/window, not static
        String productId, fileId, candidateId;
        WorkspaceController(WorkspaceViewPort view, WorkspaceService service, boolean editable) {
            this.view = view; this.service = service; this.editable = editable;
        }
        void start() { selectProduct("P100"); }
        public void selectProduct(String id) {
            if (id.equals(productId)) return;
            view.trace("CONTROLLER", "selectProduct(" + id + ")");
            view.trace("SERVICE", "filesFor(" + id + ") [memory]");
            List<FileInfo> files = service.filesFor(id);
            productId = id; fileId = null; candidateId = null;
            view.showFiles(id, files);
            if (!files.isEmpty()) selectFile(files.get(0).id);
        }
        public void selectFile(String id) {
            if (id.equals(fileId)) return;
            view.trace("CONTROLLER", "selectFile(" + id + ")");
            view.trace("SERVICE", "load(" + id + ") [memory]");
            WorkspaceData data = service.load(id);
            fileId = id; candidateId = null;
            Draft draft = drafts.get(id);
            if (draft == null) { draft = new Draft(data.latest()); drafts.put(id, draft); }
            view.showWorkspace(data, draft.text, draft.dirty());
            updateCapabilities();
        }
        public void selectCandidate(String id) {
            if (fileId == null || id.equals(candidateId)) return;
            candidateId = id;
            view.trace("CONTROLLER", "selectCandidate(" + id + ") [no service call]");
            view.showCandidate(id);
            updateCapabilities();
        }
        public void previewRevision(String id) {
            if (fileId == null) return;
            view.trace("CONTROLLER", "previewRevision(" + id + ")");
            view.trace("SERVICE", "revision(" + fileId + ", " + id + ") [memory]");
            Revision r = service.revision(fileId, id);
            view.showComparison(fileId + " / " + id, r.text, drafts.get(fileId).text, id);
        }
        public void editorChanged(String text) {
            if (!editable || fileId == null) return;
            Draft d = drafts.get(fileId);
            boolean wasDirty = d.dirty();
            d.text = text;
            view.markDirty(d.dirty());
            if (d.dirty() != wasDirty) view.trace("CONTROLLER", "draft dirty = " + d.dirty());
            updateCapabilities();
        }
        public void compare() {
            if (fileId == null || candidateId == null) return;
            view.trace("CONTROLLER", "compare(" + fileId + ", " + candidateId + ")");
            view.trace("SERVICE", "load comparison source [memory; NO diff algorithm]");
            WorkspaceData source = service.load(candidateId);
            view.showComparison(source.file.name + " / " + source.latest().id,
                    source.latest().text, drafts.get(fileId).text, null);
        }
        public void generate() {
            if (!editable || fileId == null || candidateId == null) return;
            view.trace("CONTROLLER", "generate preview");
            view.trace("SERVICE", "generatePreview() [append demo note only]");
            Draft d = drafts.get(fileId);
            d.text = service.generatePreview(fileId, candidateId, d.text);
            view.showWorkspace(service.load(fileId), d.text, d.dirty());
            view.showCandidate(candidateId);
            view.status("Mock preview added to draft. Existing edits are retained. Not saved.");
            updateCapabilities();
        }
        public void save() {
            if (!editable || fileId == null || !drafts.get(fileId).dirty()) return;
            Draft d = drafts.get(fileId);
            SaveCommand command = new SaveCommand(fileId, d.baseRevision, d.text);
            view.trace("CONTROLLER", "save(SaveCommand) -> ONE use-case operation");
            view.trace("SERVICE", "save() [memory-only revision; no file / REST I/O]");
            try {
                Revision r = service.save(command);
                d.baseRevision = r.id; d.baseline = r.text;
                view.showFiles(productId, service.filesFor(productId));
                view.showWorkspace(service.load(fileId), d.text, false);
                if (candidateId != null) view.showCandidate(candidateId);
                view.status("Saved " + fileId + " / " + r.id + " IN MEMORY ONLY. Exit discards all changes.");
            } catch (IllegalArgumentException | IllegalStateException ex) {
                view.status("Save blocked: " + ex.getMessage());
                view.trace("ERROR", ex.getMessage());
            }
            updateCapabilities();
        }
        public void refreshProduct(String id) {
            String previous = Objects.equals(id, productId) ? fileId : null;
            view.trace("CONTROLLER", "refreshProduct(" + id + ") [drafts retained]");
            view.trace("SERVICE", "filesFor(" + id + ") [memory]");
            List<FileInfo> files = service.filesFor(id);
            productId = id; fileId = null; candidateId = null;
            view.showFiles(id, files);
            String restore = files.isEmpty() ? null : files.get(0).id;
            for (FileInfo f : files) if (f.id.equals(previous)) restore = previous;
            if (restore != null) selectFile(restore);
            updateCapabilities();
        }
        public void inspectFile(String id) {
            view.trace("CONTROLLER", "inspectFile(" + id + ")");
            view.trace("SERVICE", "load file info [memory]");
            WorkspaceData data = service.load(id);
            FileInfo f = data.file;
            String info = "File=" + f.id + ", product=" + f.productId + ", name=" + f.name
                    + ", group=" + f.group + ", HEAD=" + f.head + ", revisions=" + data.revisions.size();
            view.trace("INFO", info); view.status(info);
        }
        boolean currentOwner(String expected) {
            if (expected != null && expected.equals(fileId)) return true;
            view.trace("BLOCKED", "Context owner changed; open the menu again.");
            view.status("The target file changed. Open the context menu again.");
            return false;
        }
        public void compareCandidate(String owner, String candidate) {
            if (!currentOwner(owner)) return;
            selectCandidate(candidate); compare();
        }
        public void generateFromCandidate(String owner, String candidate) {
            if (!editable || !currentOwner(owner)) return;
            selectCandidate(candidate); generate();
        }
        public void previewRevision(String owner, String revision) {
            if (currentOwner(owner)) previewRevision(revision);
        }
        public void inspectRevision(String owner, String revision) {
            if (!currentOwner(owner)) return;
            view.trace("CONTROLLER", "inspectRevision(" + owner + ", " + revision + ")");
            view.trace("SERVICE", "revision info [memory]");
            Revision r = service.revision(owner, revision);
            String info = owner + " / " + r.id + " | parent=" + (r.parentId == null ? "-" : r.parentId)
                    + " | " + r.note + " | " + r.text.length() + " characters";
            view.trace("INFO", info); view.status(info);
        }
        void updateCapabilities() {
            boolean selected = fileId != null;
            view.capabilities(editable && selected && drafts.get(fileId).dirty(),
                    selected && candidateId != null, editable && selected && candidateId != null);
        }
    }

    // =====================================================================
    // 4. UI MODULES. No service or sibling-module references.
    // =====================================================================
    interface UiModule {
        String id();
        JComponent component();
        default void dispose() { }
    }
    static class PanelModule extends JPanel implements UiModule {
        final String moduleId;
        final JLabel heading;
        final JPanel body = new JPanel(new BorderLayout());
        PanelModule(String moduleId, String title) {
            super(new BorderLayout(0, 7)); this.moduleId = moduleId;
            setBackground(WHITE);
            setBorder(new CompoundBorder(new LineBorder(LINE), new EmptyBorder(8, 10, 8, 10)));
            heading = new JLabel(title); heading.setFont(TITLE); heading.setForeground(INK);
            body.setOpaque(false);
            add(heading, BorderLayout.NORTH); add(body, BorderLayout.CENTER);
            setMinimumSize(new Dimension(100, 85));
        }
        public String id() { return moduleId; }
        public JComponent component() { return this; }
    }
    static final class ProductNode {
        final String label, id;
        ProductNode(String label, String id) { this.label = label; this.id = id; }
        public String toString() { return label; }
    }
    static final class ProductTreeModule extends PanelModule {
        final JTree tree;
        final JScrollPane scrollPane;
        final Map<String, TreePath> paths = new HashMap<>();
        ProductTreeModule() {
            super("products", "Product families");
            DefaultMutableTreeNode root = node("All products", null);
            DefaultMutableTreeNode memory = node("Memory", null), io = node("I/O", null);
            root.add(memory); root.add(io);
            memory.add(node("P100 / Alpha", "P100")); memory.add(node("P200 / Beta", "P200"));
            io.add(node("P300 / Gamma", "P300"));
            tree = new JTree(root);
            tree.setUI(new BasicTreeUI() {
                protected void installDefaults() {
                    super.installDefaults(); setExpandedIcon(DOWN); setCollapsedIcon(RIGHT);
                }
            });
            tree.setFont(BODY); tree.setRowHeight(30);
            tree.setRootVisible(true); tree.setShowsRootHandles(true);
            tree.setBorder(new EmptyBorder(6, 3, 6, 3));
            tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
            tree.setCellRenderer(new DefaultTreeCellRenderer() {
                public Component getTreeCellRendererComponent(JTree t, Object value, boolean selected,
                        boolean expanded, boolean leaf, int row, boolean focus) {
                    super.getTreeCellRendererComponent(t, value, selected, expanded, leaf, row, focus);
                    ProductNode n = (ProductNode) ((DefaultMutableTreeNode) value).getUserObject();
                    setFont(n.id == null ? BOLD : BODY);
                    setIcon(n.id == null ? FAMILY : PRODUCT);
                    setTextSelectionColor(INK); setBackgroundSelectionColor(SELECT);
                    setTextNonSelectionColor(INK); setIconTextGap(7);
                    return this;
                }
            });
            Enumeration<?> nodes = root.depthFirstEnumeration();
            while (nodes.hasMoreElements()) {
                DefaultMutableTreeNode n = (DefaultMutableTreeNode) nodes.nextElement();
                ProductNode data = (ProductNode) n.getUserObject();
                if (data.id != null) paths.put(data.id, new TreePath(n.getPath()));
            }
            for (int i = 0; i < tree.getRowCount(); i++) tree.expandRow(i);
            scrollPane = scroll(tree); body.add(scrollPane);
        }
        static DefaultMutableTreeNode node(String label, String id) {
            return new DefaultMutableTreeNode(new ProductNode(label, id));
        }
        String selectedId() {
            DefaultMutableTreeNode n = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            return n == null ? null : ((ProductNode) n.getUserObject()).id;
        }
        void select(String id) { tree.setSelectionPath(paths.get(id)); }
    }
    static final class TableRow {
        final String id;
        final Object[] cells;
        TableRow(String id, Object... cells) { this.id = id; this.cells = cells.clone(); }
    }
    static final class RowsModel extends AbstractTableModel {
        final String[] columns;
        final Class<?>[] types;
        List<TableRow> rows = Collections.emptyList();
        RowsModel(String[] columns, Class<?>[] types) { this.columns = columns; this.types = types; }
        public int getRowCount() { return rows.size(); }
        public int getColumnCount() { return columns.length; }
        public String getColumnName(int c) { return columns[c]; }
        public Class<?> getColumnClass(int c) { return types[c]; }
        public Object getValueAt(int r, int c) { return rows.get(r).cells[c]; }
        void replace(List<TableRow> data) { rows = new ArrayList<>(data); fireTableDataChanged(); }
    }
    static final class TableModule extends PanelModule {
        final JTable table;
        final RowsModel model;
        final JScrollPane scrollPane;
        TableModule(String id, String title, String[] columns, Class<?>... types) {
            super(id, title);
            model = new RowsModel(columns, types); table = new JTable(model);
            table.setFont(BODY); table.setRowHeight(27); table.setFillsViewportHeight(true);
            table.setAutoCreateRowSorter(true); table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.setShowVerticalLines(false); table.setGridColor(LINE);
            table.setSelectionBackground(SELECT); table.setSelectionForeground(INK);
            table.setDefaultRenderer(Object.class, new BodyRenderer());
            table.setDefaultRenderer(Integer.class, new BodyRenderer());
            JTableHeader h = table.getTableHeader();
            h.setReorderingAllowed(false); h.setPreferredSize(new Dimension(100, 30));
            h.setDefaultRenderer(new HeaderRenderer());
            scrollPane = scroll(table); body.add(scrollPane);
            setPreferredSize(new Dimension(650, 153));
        }
        String idAtViewRow(int viewRow) {
            if (viewRow < 0 || viewRow >= table.getRowCount()) return null;
            return model.rows.get(table.convertRowIndexToModel(viewRow)).id;
        }
        String selectedId() { return idAtViewRow(table.getSelectedRow()); }
        String idAt(Point point) { return idAtViewRow(table.rowAtPoint(point)); }
        void select(String id) {
            for (int i = 0; i < model.rows.size(); i++) if (model.rows.get(i).id.equals(id)) {
                int viewRow = table.convertRowIndexToView(i);
                if (viewRow >= 0) table.setRowSelectionInterval(viewRow, viewRow);
                return;
            }
        }
    }
    static final class BodyRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable t, Object value, boolean selected,
                boolean focus, int row, int column) {
            super.getTableCellRendererComponent(t, value, selected, focus, row, column);
            // Renderer instances are reused: reset properties on EVERY call.
            setFont(BODY); setIcon(null); setForeground(INK);
            setBackground(selected ? SELECT : row % 2 == 0 ? WHITE : STRIPE);
            setHorizontalAlignment(value instanceof Number ? RIGHT : LEFT);
            setBorder(new EmptyBorder(2, 7, 2, 7));
            if ("SAMPLE".equals(value)) { setForeground(AMBER); setFont(BOLD); }
            return this;
        }
    }
    static final class HeaderRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable t, Object value, boolean selected,
                boolean focus, int row, int column) {
            super.getTableCellRendererComponent(t, value, false, false, row, column);
            setFont(BOLD); setBackground(HEADER); setForeground(INK); setIcon(null);
            setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 1, LINE), new EmptyBorder(3, 7, 3, 7)));
            setHorizontalAlignment(LEFT); setHorizontalTextPosition(LEFT);
            if (t.getRowSorter() != null && !t.getRowSorter().getSortKeys().isEmpty()) {
                RowSorter.SortKey key = t.getRowSorter().getSortKeys().get(0);
                if (key.getColumn() == t.convertColumnIndexToModel(column))
                    setIcon(key.getSortOrder() == SortOrder.ASCENDING ? UP : DOWN);
            }
            return this;
        }
    }
    static final class EditorModule extends PanelModule {
        final JTextArea editor = textArea(true), reference = textArea(false), current = textArea(false);
        final JTabbedPane tabs = new JTabbedPane();
        final JLabel referenceLabel = new JLabel("Reference"), previewNote = new JLabel("No comparison selected");
        final boolean editable;
        String fileName = "No file";
        EditorModule(boolean editable) {
            super("editor", editable ? "Editor / Compare" : "Revision / Compare (read-only)");
            this.editable = editable; editor.setEditable(editable);
            tabs.setFont(BODY); tabs.addTab(editable ? "Edit" : "Current content", scroll(editor));
            JPanel ref = new JPanel(new BorderLayout(0, 5));
            referenceLabel.setFont(BOLD); ref.add(referenceLabel, BorderLayout.NORTH); ref.add(scroll(reference));
            JPanel right = new JPanel(new BorderLayout(0, 5));
            JLabel label = new JLabel("Current draft (comparison snapshot)"); label.setFont(BOLD);
            right.add(label, BorderLayout.NORTH); right.add(scroll(current));
            JPanel comparison = new JPanel(new BorderLayout(0, 5));
            comparison.add(split(JSplitPane.HORIZONTAL_SPLIT, ref, right, .5), BorderLayout.CENTER);
            previewNote.setFont(BODY.deriveFont(11f)); previewNote.setForeground(AMBER);
            comparison.add(previewNote, BorderLayout.SOUTH);
            tabs.addTab("Compare preview", comparison); body.add(tabs);
            setMinimumSize(new Dimension(180, 130));
        }
        void showDraft(String name, String text, boolean dirty) {
            fileName = name;
            editor.setText(text); editor.setCaretPosition(0); tabs.setSelectedIndex(0);
            reference.setText(""); current.setText(""); referenceLabel.setText("Reference");
            previewNote.setText("Side-by-side preview only. Diff alignment/highlighting is NOT implemented.");
            markDirty(dirty);
        }
        void markDirty(boolean dirty) {
            heading.setText((dirty ? "* " : "") + fileName + (editable ? " / Draft" : " / Read-only"));
            heading.setForeground(dirty ? AMBER : INK);
        }
        void compare(String label, String base, String draft) {
            referenceLabel.setText(label); reference.setText(base); current.setText(draft);
            reference.setCaretPosition(0); current.setCaretPosition(0);
            previewNote.setText("Two read-only snapshots. No diff algorithm. Edit tab changes are preserved.");
            tabs.setSelectedIndex(1);
        }
    }
    static final class EventLogModule extends PanelModule {
        final JTextArea text = textArea(false);
        EventLogModule() {
            super("events", "Event trace"); text.setFont(MONO.deriveFont(11f));
            text.setLineWrap(true); text.setWrapStyleWord(true); body.add(scroll(text));
        }
        void append(String stage, String message) {
            text.append(CLOCK.format(LocalTime.now()) + " [" + stage + "] " + message + "\n");
            try {
                if (text.getLineCount() > 350) text.getDocument().remove(0, text.getLineEndOffset(50));
            } catch (javax.swing.text.BadLocationException ex) { throw new IllegalStateException(ex); }
            text.setCaretPosition(text.getDocument().getLength());
        }
    }
    static final class GraphNode {
        final Revision revision;
        final RoundRectangle2D shape;
        GraphNode(Revision r, int index) {
            revision = r; shape = new RoundRectangle2D.Double(25 + index * 170, 30, 135, 62, 12, 12);
        }
    }
    static final class LineageGraphModule extends PanelModule {
        final GraphCanvas canvas = new GraphCanvas();
        final JScrollPane scrollPane;
        LineageGraphModule() {
            super("lineage", "File lineage / Click a revision");
            scrollPane = scroll(canvas); body.add(scrollPane);
            setMinimumSize(new Dimension(150, 130));
        }
        public void dispose() { canvas.setToolTipText(null); }
    }
    static final class GraphCanvas extends JPanel {
        final List<GraphNode> nodes = new ArrayList<>();
        String selected;
        double zoom = 1.0;
        GraphCanvas() {
            setBackground(WHITE); setFocusable(true);
            setToolTipText("Click: preview / Right-click: menu / Wheel: zoom");
        }
        GraphNode node(String id) {
            for (GraphNode n : nodes) if (n.revision.id.equals(id)) return n;
            return null;
        }
        void resetZoom() { zoom = 1.0; updateSize(); repaint(); }
        void setRevisions(List<Revision> revisions) {
            nodes.clear(); for (Revision r : revisions) nodes.add(new GraphNode(r, nodes.size()));
            selected = revisions.isEmpty() ? null : revisions.get(revisions.size() - 1).id;
            updateSize(); repaint();
        }
        void updateSize() { setPreferredSize(new Dimension((int) ((nodes.size() * 170 + 30) * zoom), (int) (125 * zoom))); revalidate(); }
        String hit(Point p) {
            for (GraphNode n : nodes) if (n.shape.contains(p.x / zoom, p.y / zoom)) return n.revision.id;
            return null;
        }
        void zoomBy(int wheelRotation) {
            zoom = Math.max(.65, Math.min(1.8, zoom - wheelRotation * .1)); updateSize(); repaint();
        }
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            try {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.scale(zoom, zoom); g.setStroke(STROKE);
                g.setColor(MUTED);
                // This demo only has a linear revision chain. A general DAG is not implemented.
                for (int i = 1; i < nodes.size(); i++) {
                    int x = (int) nodes.get(i).shape.getX();
                    g.drawLine(x - 35, 61, x - 4, 61);
                    g.drawLine(x - 9, 57, x - 4, 61); g.drawLine(x - 9, 65, x - 4, 61);
                }
                for (GraphNode n : nodes) {
                    int x = (int) n.shape.getX(); boolean active = n.revision.id.equals(selected);
                    g.setColor(active ? SELECT : STRIPE); g.fill(n.shape);
                    g.setColor(active ? BLUE : LINE); g.draw(n.shape);
                    g.setFont(BOLD); g.setColor(active ? BLUE : INK); g.drawString(n.revision.id, x + 15, 53);
                    g.setFont(SMALL); g.setColor(MUTED);
                    g.drawString(n.revision.parentId == null ? "imported sample" : "parent: " + n.revision.parentId, x + 15, 75);
                }
            } finally { g.dispose(); }
        }
    }

    // =====================================================================
    // 5. VIEW / COMPOSER: place modules, distribute display data, no services.
    // =====================================================================
    static final class WorkspaceView extends JPanel implements WorkspaceViewPort {
        final Preset preset;
        final ProductTreeModule products = new ProductTreeModule();
        final TableModule files = new TableModule("files", "Files / Select to open",
                new String[]{"ID", "File", "Group", "Head"}, String.class, String.class, String.class, String.class);
        final TableModule candidates, history;
        final EditorModule editor;
        final LineageGraphModule graph;
        final EventLogModule log = new EventLogModule();
        final JToolBar toolbar = new JToolBar();
        final JLabel statusLabel = new JLabel("MOCK: memory only. No files are changed.");
        Action saveAction, compareAction, generateAction;
        int renderDepth;
        String shownFileId, shownProductId;
        long contextVersion; // Changes when displayed files / revisions are replaced.
        WorkspaceView(Preset preset) {
            super(new BorderLayout(8, 8)); this.preset = preset;
            candidates = preset == Preset.HISTORY ? null : new TableModule("candidates", "Candidates / Sample scores",
                    new String[]{"File", "Score (demo)", "Reason"}, String.class, Integer.class, String.class);
            history = preset == Preset.EDIT ? null : new TableModule("history", "History / Select to preview",
                    new String[]{"Revision", "Parent", "Note"}, String.class, String.class, String.class);
            graph = preset == Preset.EDIT ? null : new LineageGraphModule();
            editor = new EditorModule(preset != Preset.HISTORY);
            setBackground(BG); setBorder(new EmptyBorder(8, 8, 6, 8));
            toolbar.setFloatable(false); toolbar.setOpaque(false);
            statusLabel.setFont(BODY.deriveFont(12f)); statusLabel.setForeground(INK);
            add(toolbar, BorderLayout.NORTH); add(new WorkspaceComposer().compose(this), BorderLayout.CENTER);
            add(statusLabel, BorderLayout.SOUTH);
        }
        boolean isRendering() { return renderDepth > 0; }
        void render(Runnable operation) {
            if (!SwingUtilities.isEventDispatchThread()) throw new IllegalStateException("EDT required");
            renderDepth++;
            try { operation.run(); } finally { renderDepth--; }
        }
        public void showFiles(String product, List<FileInfo> data) {
            contextVersion++; shownProductId = product; shownFileId = null;
            render(() -> {
                products.select(product);
                List<TableRow> rows = new ArrayList<>();
                for (FileInfo f : data) rows.add(new TableRow(f.id, f.id, f.name, f.group, f.head));
                files.model.replace(rows);
            });
            trace("VIEW", "product tree + file table updated");
        }
        public void showWorkspace(WorkspaceData data, String draft, boolean dirty) {
            contextVersion++; shownFileId = data.file.id;
            render(() -> {
                files.select(data.file.id);
                if (candidates != null) {
                    List<TableRow> rows = new ArrayList<>();
                    for (Candidate c : data.candidates) rows.add(new TableRow(c.id, c.name, c.sampleScore, c.reason));
                    candidates.model.replace(rows);
                }
                if (history != null) {
                    List<TableRow> rows = new ArrayList<>();
                    for (Revision r : data.revisions) rows.add(new TableRow(r.id, r.id, r.parentId == null ? "-" : r.parentId, r.note));
                    history.model.replace(rows); history.select(data.latest().id);
                }
                editor.showDraft(data.file.name, draft, dirty);
                if (graph != null) graph.canvas.setRevisions(data.revisions);
            });
            status("Selected " + data.file.id + " | Memory-only demo | unsaved drafts survive file selection");
            trace("VIEW", "candidate/history/editor/graph updated silently (no command re-entry)");
        }
        public void showComparison(String label, String base, String draft, String revisionId) {
            render(() -> {
                editor.compare(label, base, draft);
                if (revisionId != null && history != null) history.select(revisionId);
                if (revisionId != null && graph != null) { graph.canvas.selected = revisionId; graph.canvas.repaint(); }
            });
            trace("VIEW", "Compare preview opened; editable draft was NOT replaced");
            status("Comparison snapshot: " + label + " | Side-by-side only, no diff algorithm");
        }
        public void showCandidate(String id) {
            render(() -> { if (candidates != null) candidates.select(id); });
            status("Candidate " + id + " | Compare, double-click, or right-click for preview");
        }
        public void markDirty(boolean dirty) { editor.markDirty(dirty); }
        public void capabilities(boolean save, boolean compare, boolean generate) {
            if (saveAction != null) saveAction.setEnabled(save);
            if (compareAction != null) compareAction.setEnabled(compare);
            if (generateAction != null) generateAction.setEnabled(generate);
        }
        public void status(String message) { statusLabel.setText(message); }
        public void trace(String stage, String message) { log.append(stage, message); }
        void disposeModules() { if (graph != null) graph.dispose(); }
    }
    static final class WorkspaceComposer {
        JComponent compose(WorkspaceView v) {
            JSplitPane left = split(JSplitPane.VERTICAL_SPLIT, v.products.component(), v.log.component(), .40);
            JPanel tables = new JPanel(new GridLayout(0, 1, 0, 10)); tables.setOpaque(false);
            tables.add(v.files.component());
            if (v.candidates != null) tables.add(v.candidates.component());
            if (v.history != null) tables.add(v.history.component());
            tables.setMinimumSize(new Dimension(200, v.preset == Preset.FULL ? 270 : 175));
            JComponent lower = v.editor.component();
            if (v.graph != null) lower = split(JSplitPane.VERTICAL_SPLIT, lower, v.graph.component(), .60);
            JSplitPane right = split(JSplitPane.VERTICAL_SPLIT, tables, lower, v.preset == Preset.FULL ? .54 : .37);
            JSplitPane main = split(JSplitPane.HORIZONTAL_SPLIT, left, right, .245);
            left.setPreferredSize(new Dimension(310, 750)); right.setPreferredSize(new Dimension(1050, 750));
            return main;
        }
    }

    // =====================================================================
    // 6. EVENT ADAPTER: Swing events -> typed intent. Only this layer binds.
    // =====================================================================
    /** Shared popup gesture / keyboard handling. Contains no business logic. */
    static final class ContextMenuSupport extends MouseAdapter implements AutoCloseable {
        final JComponent owner;
        final JViewport viewport;
        final Function<Point, JPopupMenu> builder;
        final Supplier<Point> keyboardAnchor;
        final InputMap input;
        final Object actionKey = new Object();
        final KeyStroke[] keys = {
                KeyStroke.getKeyStroke(KeyEvent.VK_F10, InputEvent.SHIFT_DOWN_MASK),
                KeyStroke.getKeyStroke(KeyEvent.VK_CONTEXT_MENU, 0)
        };
        final Object[] previous = new Object[keys.length];
        JPopupMenu popup;
        boolean shownOnPress, closed;
        ContextMenuSupport(JComponent owner, JViewport viewport,
                Function<Point, JPopupMenu> builder, Supplier<Point> keyboardAnchor) {
            this.owner = owner; this.viewport = viewport;
            this.builder = builder; this.keyboardAnchor = keyboardAnchor;
            owner.addMouseListener(this); viewport.addMouseListener(this);
            input = owner.getInputMap(JComponent.WHEN_FOCUSED);
            for (int i = 0; i < keys.length; i++) {
                previous[i] = input.get(keys[i]); input.put(keys[i], actionKey);
            }
            owner.getActionMap().put(actionKey, new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    Point p = keyboardAnchor.get();
                    if (p != null) showAt(owner, p);
                }
            });
        }
        public void mousePressed(MouseEvent e) {
            shownOnPress = false;
            if (e.isPopupTrigger()) { shownOnPress = true; showFor(e); }
        }
        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger() && !shownOnPress) showFor(e);
            shownOnPress = false;
        }
        void showFor(MouseEvent e) {
            e.consume();
            showAt((JComponent) e.getComponent(), e.getPoint());
        }
        void showAt(JComponent invoker, Point p) {
            if (closed || !owner.isEnabled() || !invoker.isShowing()) return;
            if (popup != null) popup.setVisible(false);
            // Viewport empty-space events must be converted to owner coordinates.
            Point targetPoint = SwingUtilities.convertPoint(invoker, p, owner);
            JPopupMenu next = builder.apply(targetPoint);
            if (next == null) return;
            popup = next;
            next.addPopupMenuListener(new PopupMenuListener() {
                public void popupMenuWillBecomeVisible(PopupMenuEvent e) { }
                public void popupMenuWillBecomeInvisible(PopupMenuEvent e) { clear(); }
                public void popupMenuCanceled(PopupMenuEvent e) { clear(); }
                void clear() { if (popup == next) popup = null; }
            });
            owner.requestFocusInWindow();
            next.show(invoker, p.x, p.y);
        }
        public void close() {
            if (closed) return; closed = true;
            if (popup != null) popup.setVisible(false);
            owner.removeMouseListener(this); viewport.removeMouseListener(this);
            for (int i = 0; i < keys.length; i++) {
                if (previous[i] == null) input.remove(keys[i]);
                else input.put(keys[i], previous[i]);
            }
            owner.getActionMap().remove(actionKey);
        }
    }

    static final class UiEventAdapter implements AutoCloseable {
        final WorkspaceView view;
        final WorkspaceActions target;
        final List<Runnable> removals = new ArrayList<>();
        boolean installed, closed;
        UiEventAdapter(WorkspaceView view, WorkspaceActions target) { this.view = view; this.target = target; }
        void install() {
            if (installed || closed) throw new IllegalStateException("Adapter must be installed once");
            installed = true;
            installActions(); installTree();
            bindSelection(view.files, "File selection", target::selectFile);
            installTableMouse(view.files);
            if (view.candidates != null) {
                bindSelection(view.candidates, "Candidate selection", target::selectCandidate);
                installTableMouse(view.candidates);
            }
            if (view.history != null) {
                bindSelection(view.history, "History selection", target::previewRevision);
                installTableMouse(view.history);
            }
            installEditor(); if (view.graph != null) installGraph();
        }
        void event(String text) { view.trace("EVENT", text); }
        Action action(String name, Icon icon, Runnable call) {
            return new AbstractAction(name, icon) {
                public void actionPerformed(ActionEvent e) {
                    if (!isEnabled() || closed) return;
                    event("ActionEvent: " + name);
                    try { call.run(); }
                    catch (RuntimeException ex) {
                        view.trace("ERROR", ex.toString()); view.status("Command failed: " + ex.getMessage());
                    }
                }
            };
        }
        void installActions() {
            view.saveAction = action("Save (mock)", SAVE, target::save);
            view.compareAction = action("Compare", COMPARE, target::compare);
            view.generateAction = action("Generate (mock)", GENERATE, target::generate);
            for (Action a : Arrays.asList(view.saveAction, view.compareAction, view.generateAction)) {
                JButton b = new JButton(a); b.setFont(BODY); b.setIconTextGap(6);
                view.toolbar.add(b); view.toolbar.add(Box.createHorizontalStrut(5));
            }
            view.toolbar.addSeparator();
            JButton clear = new JButton(action("Clear trace", null, () -> view.log.text.setText("")));
            clear.setFont(BODY); view.toolbar.add(clear);
            view.toolbar.add(Box.createHorizontalGlue());
            JLabel label = new JLabel("LAYOUT: " + view.preset + "  |  MOCK / NO FILE I/O");
            label.setFont(BOLD); label.setForeground(AMBER); view.toolbar.add(label);
            view.capabilities(false, false, false);
            bindKey(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), "save", view.saveAction);
            bindKey(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK), "compare", view.compareAction);
        }
        void bindKey(KeyStroke key, String name, Action action) {
            InputMap input = view.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
            Object previous = input.get(key);
            Action oldAction = view.getActionMap().get(name);
            input.put(key, name); view.getActionMap().put(name, action);
            removals.add(() -> {
                if (previous == null) input.remove(key); else input.put(key, previous);
                if (oldAction == null) view.getActionMap().remove(name);
                else view.getActionMap().put(name, oldAction);
            });
        }
        JMenuBar menuBar() {
            JMenuBar bar = new JMenuBar(); JMenu menu = new JMenu("Workspace");
            menu.add(new JMenuItem(view.saveAction)); menu.add(new JMenuItem(view.compareAction));
            menu.add(new JMenuItem(view.generateAction)); bar.add(menu); return bar;
        }
        void bindPopup(JComponent owner, JScrollPane pane, Function<Point, JPopupMenu> builder,
                Supplier<Point> anchor) {
            ContextMenuSupport support = new ContextMenuSupport(owner, pane.getViewport(), builder, anchor);
            removals.add(support::close);
        }
        // Menus capture context version and stable IDs when opened.
        // Do NOT query the selected row again from inside a menu Action.
        JPopupMenu menu(String caption) {
            event("Context menu: " + caption + " [no business call until a command is chosen]");
            JPopupMenu p = new JPopupMenu();
            p.putClientProperty("contextVersion", view.contextVersion);
            JLabel title = new JLabel(caption); title.setFont(BOLD); title.setForeground(BLUE);
            title.setBorder(new EmptyBorder(6, 10, 6, 10)); p.add(title); p.addSeparator();
            return p;
        }
        void item(JPopupMenu p, String name, Icon icon, boolean enabled, Runnable call) {
            final long version = (Long) p.getClientProperty("contextVersion");
            Action a = action(name, icon, () -> {
                if (version != view.contextVersion) {
                    view.trace("BLOCKED", "Stale context menu: " + name);
                    view.status("The displayed data changed. Open the context menu again.");
                    return;
                }
                call.run();
            });
            a.setEnabled(enabled);
            JMenuItem entry = new JMenuItem(a); entry.setFont(BODY); entry.setIconTextGap(7); p.add(entry);
        }
        void copyText(String text) {
            try {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
                view.trace("UI", "Copied: " + text); view.status("Copied: " + text);
            } catch (IllegalStateException | HeadlessException | SecurityException ex) {
                view.trace("ERROR", "Clipboard unavailable: " + ex.getMessage());
                view.status("Clipboard is not available.");
            }
        }
        Point anchor(JComponent c, Rectangle bounds) {
            if (bounds == null) return null;
            c.scrollRectToVisible(bounds);
            return new Point(bounds.x + Math.min(12, Math.max(1, bounds.width / 2)),
                    bounds.y + bounds.height / 2);
        }
        void installTree() {
            JTree tree = view.products.tree;
            TreeSelectionListener selection = e -> {
                if (view.isRendering()) return;
                String id = view.products.selectedId();
                event("TreeSelectionEvent -> " + (id == null ? "family (local selection only)" : id));
                if (id != null) target.selectProduct(id);
            };
            tree.addTreeSelectionListener(selection); removals.add(() -> tree.removeTreeSelectionListener(selection));
            TreeExpansionListener expansion = new TreeExpansionListener() {
                public void treeExpanded(TreeExpansionEvent e) { local("expanded", e); }
                public void treeCollapsed(TreeExpansionEvent e) { local("collapsed", e); }
                void local(String state, TreeExpansionEvent e) {
                    if (!view.isRendering()) event("TreeExpansionEvent: " + state + " " + e.getPath().getLastPathComponent() + " [UI only]");
                }
            };
            tree.addTreeExpansionListener(expansion); removals.add(() -> tree.removeTreeExpansionListener(expansion));
            bindPopup(tree, view.products.scrollPane, this::treePopup,
                    () -> anchor(tree, tree.getPathBounds(tree.getSelectionPath())));
        }
        JPopupMenu treePopup(Point point) {
            JTree tree = view.products.tree;
            // Exact hit test: empty space must NOT resolve to the nearest node.
            final TreePath path = tree.getPathForLocation(point.x, point.y);
            if (path == null) {
                JPopupMenu p = menu("Tree background / no node");
                TreePath root = new TreePath(tree.getModel().getRoot());
                item(p, "Expand all", DOWN, true, () -> expandSubtree(root));
                item(p, "Collapse all", RIGHT, true, () -> collapseSubtree(root));
                return p;
            }
            ProductNode n = (ProductNode) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
            final String id = n.id, label = n.label;
            JPopupMenu p = menu((id == null ? "Family: " : "Product: ") + label);
            if (id != null) {
                item(p, "Open this product", PRODUCT, true, () -> target.selectProduct(id));
                item(p, "Refresh this product (mock)", REFRESH, true, () -> target.refreshProduct(id));
                item(p, "Copy product ID", COPY, true, () -> copyText(id));
            } else {
                item(p, "Expand subtree", DOWN, true, () -> expandSubtree(path));
                item(p, "Collapse subtree", RIGHT, true, () -> collapseSubtree(path));
                item(p, "Copy family name", COPY, true, () -> copyText(label));
            }
            return p;
        }
        void expandSubtree(TreePath root) {
            JTree tree = view.products.tree;
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) root.getLastPathComponent();
            Enumeration<?> nodes = node.preorderEnumeration();
            while (nodes.hasMoreElements()) {
                DefaultMutableTreeNode n = (DefaultMutableTreeNode) nodes.nextElement();
                tree.expandPath(new TreePath(n.getPath()));
            }
        }
        void collapseSubtree(TreePath root) {
            JTree tree = view.products.tree;
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) root.getLastPathComponent();
            Enumeration<?> nodes = node.depthFirstEnumeration();
            while (nodes.hasMoreElements()) {
                DefaultMutableTreeNode n = (DefaultMutableTreeNode) nodes.nextElement();
                tree.collapsePath(new TreePath(n.getPath()));
            }
        }
        void bindSelection(TableModule module, String name, Consumer<String> callback) {
            ListSelectionListener listener = e -> {
                if (view.isRendering() || e.getValueIsAdjusting()) return;
                String id = module.selectedId();
                if (id != null) { event("ListSelectionEvent: " + name + " -> " + id); callback.accept(id); }
            };
            module.table.getSelectionModel().addListSelectionListener(listener);
            removals.add(() -> module.table.getSelectionModel().removeListSelectionListener(listener));
        }
        void installTableMouse(TableModule m) {
            MouseAdapter doubleClick = new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (!SwingUtilities.isLeftMouseButton(e) || e.getClickCount() != 2 || e.isPopupTrigger()) return;
                    String id = m.idAt(e.getPoint()); if (id == null) return;
                    event("MouseEvent: " + m.id() + " double-click -> " + id);
                    if (m == view.files) target.selectFile(id);
                    else if (m == view.candidates) target.compareCandidate(view.shownFileId, id);
                    else target.previewRevision(view.shownFileId, id);
                }
            };
            m.table.addMouseListener(doubleClick); removals.add(() -> m.table.removeMouseListener(doubleClick));
            bindPopup(m.table, m.scrollPane, point -> tablePopup(m, point), () -> {
                int row = m.table.getSelectedRow();
                return row < 0 ? null : anchor(m.table, m.table.getCellRect(row, 0, true));
            });
        }
        JPopupMenu tablePopup(TableModule m, Point point) {
            final String id = m.idAt(point); // view row -> model row -> immutable ID
            final String owner = view.shownFileId;
            JPopupMenu p = menu(m.id() + ": " + (id == null ? "background / no row" : id));
            if (id == null) {
                item(p, "No row at pointer", null, false, () -> { });
            } else if (m == view.files) {
                item(p, "Open this file", FILE, true, () -> target.selectFile(id));
                item(p, "Show file info in trace", INFO, true, () -> target.inspectFile(id));
                item(p, "Copy file ID", COPY, true, () -> copyText(id));
            } else if (m == view.candidates) {
                item(p, "Select this candidate", FILE, owner != null, () -> target.selectCandidate(id));
                item(p, "Compare this candidate", COMPARE, owner != null, () -> target.compareCandidate(owner, id));
                item(p, "Generate from this candidate (mock)", GENERATE, owner != null && view.editor.editable,
                        () -> target.generateFromCandidate(owner, id));
                item(p, "Show candidate info in trace", INFO, true, () -> target.inspectFile(id));
                item(p, "Copy candidate ID", COPY, true, () -> copyText(id));
            } else {
                revisionItems(p, owner, id);
            }
            p.addSeparator();
            item(p, "Clear sorting (UI only)", REFRESH, true, () -> m.table.getRowSorter().setSortKeys(null));
            return p;
        }
        void revisionItems(JPopupMenu p, String owner, String revision) {
            item(p, "Compare revision with draft", COMPARE, owner != null,
                    () -> target.previewRevision(owner, revision));
            item(p, "Show revision info in trace", INFO, owner != null,
                    () -> target.inspectRevision(owner, revision));
            item(p, "Copy file@revision", COPY, owner != null, () -> copyText(owner + "@" + revision));
        }
        void installEditor() {
            DocumentListener document = new DocumentListener() {
                public void insertUpdate(DocumentEvent e) { changed(); }
                public void removeUpdate(DocumentEvent e) { changed(); }
                public void changedUpdate(DocumentEvent e) { changed(); }
                void changed() {
                    if (view.isRendering() || !view.editor.editable) return;
                    event("DocumentEvent -> editorChanged(text) [no service call]");
                    target.editorChanged(view.editor.editor.getText());
                }
            };
            view.editor.editor.getDocument().addDocumentListener(document);
            removals.add(() -> view.editor.editor.getDocument().removeDocumentListener(document));
        }
        void installGraph() {
            GraphCanvas canvas = view.graph.canvas;
            MouseAdapter mouse = new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1 && !e.isPopupTrigger()) {
                        canvas.requestFocusInWindow();
                        String id = canvas.hit(e.getPoint());
                        if (id != null) {
                            event("MouseEvent: graph node -> " + id);
                            target.previewRevision(view.shownFileId, id);
                        }
                    }
                }
                public void mouseMoved(MouseEvent e) {
                    String id = canvas.hit(e.getPoint());
                    canvas.setCursor(Cursor.getPredefinedCursor(id == null ? Cursor.DEFAULT_CURSOR : Cursor.HAND_CURSOR));
                    canvas.setToolTipText(id == null ? "Right-click: canvas menu / Wheel: zoom" : id + " / right-click: revision menu");
                }
                public void mouseWheelMoved(MouseWheelEvent e) {
                    canvas.zoomBy(e.getWheelRotation()); e.consume();
                    event("MouseWheelEvent: graph zoom " + String.format(Locale.ROOT, "%.1f", canvas.zoom) + " [UI only]");
                }
            };
            canvas.addMouseListener(mouse); canvas.addMouseMotionListener(mouse); canvas.addMouseWheelListener(mouse);
            removals.add(() -> { canvas.removeMouseListener(mouse); canvas.removeMouseMotionListener(mouse); canvas.removeMouseWheelListener(mouse); });
            bindPopup(canvas, view.graph.scrollPane, this::graphPopup, () -> {
                GraphNode node = canvas.node(canvas.selected);
                if (node == null) return new Point(8, 8);
                Rectangle r = new Rectangle((int) (node.shape.getX() * canvas.zoom),
                        (int) (node.shape.getY() * canvas.zoom), (int) (node.shape.getWidth() * canvas.zoom),
                        (int) (node.shape.getHeight() * canvas.zoom));
                return anchor(canvas, r);
            });
        }
        JPopupMenu graphPopup(Point point) {
            GraphCanvas canvas = view.graph.canvas;
            final String revision = canvas.hit(point), owner = view.shownFileId;
            JPopupMenu p = menu(revision == null ? "Graph background / no node" : owner + " / " + revision);
            if (revision != null) {
                revisionItems(p, owner, revision);
                GraphNode n = canvas.node(revision);
                final String parent = n.revision.parentId;
                item(p, "Compare parent with draft", COMPARE, parent != null && owner != null,
                        () -> target.previewRevision(owner, parent));
                p.addSeparator();
            }
            item(p, "Zoom in (UI only)", GENERATE, true, () -> canvas.zoomBy(-1));
            item(p, "Zoom out (UI only)", null, true, () -> canvas.zoomBy(1));
            item(p, "Reset zoom to 100%", REFRESH, true, canvas::resetZoom);
            return p;
        }
        public void close() {
            if (closed) return; closed = true;
            for (int i = removals.size() - 1; i >= 0; i--) removals.get(i).run();
            removals.clear(); view.capabilities(false, false, false); view.disposeModules();
        }
    }

    // =====================================================================
    // 7. Small pure UI helpers. No business utilities / disk access.
    // =====================================================================
    static JScrollPane scroll(Component c) {
        JScrollPane s = new JScrollPane(c); s.setBorder(new LineBorder(LINE));
        s.getViewport().setBackground(WHITE); s.setMinimumSize(new Dimension(60, 45)); return s;
    }
    static JTextArea textArea(boolean editable) {
        JTextArea t = new JTextArea(); t.setEditable(editable); t.setFont(MONO);
        t.setForeground(INK); t.setBackground(WHITE); t.setMargin(new Insets(8, 10, 8, 10)); return t;
    }
    static JSplitPane split(int orientation, Component a, Component b, double ratio) {
        JSplitPane s = new JSplitPane(orientation, a, b);
        s.setBorder(null); s.setDividerSize(8); s.setContinuousLayout(true); s.setResizeWeight(ratio);
        // Apply initial proportional positions after the components are realized.
        SwingUtilities.invokeLater(() -> s.setDividerLocation(ratio));
        return s;
    }
    static final class VectorIcon implements Icon {
        final Color color;
        final List<Shape> shapes = new ArrayList<>();
        VectorIcon(String type, Color color) {
            this.color = color;
            if ("right".equals(type)) path(6, 4, 10, 8, 6, 12);
            else if ("down".equals(type)) path(4, 6, 8, 10, 12, 6);
            else if ("up".equals(type)) path(4, 10, 8, 6, 12, 10);
            else if ("box".equals(type)) {
                shapes.add(new RoundRectangle2D.Double(2, 2, 12, 12, 3, 3)); path(5, 6, 11, 6); path(5, 10, 9, 10);
            } else if ("file".equals(type)) { path(4, 2, 10, 2, 13, 5, 13, 14, 4, 14, 4, 2); path(6, 8, 11, 8); }
            else if ("save".equals(type)) { shapes.add(new Rectangle2D.Double(2, 2, 12, 12)); path(5, 2, 5, 6, 11, 6, 11, 2); path(5, 14, 5, 10, 11, 10, 11, 14); }
            else if ("compare".equals(type)) { path(1, 3, 6, 3, 6, 13, 1, 13, 1, 3); path(10, 3, 15, 3, 15, 13, 10, 13, 10, 3); }
            else if ("copy".equals(type)) {
                shapes.add(new Rectangle2D.Double(5, 5, 9, 9)); path(11, 3, 11, 1, 1, 1, 1, 11, 3, 11);
            } else if ("info".equals(type)) {
                shapes.add(new Ellipse2D.Double(2, 2, 12, 12)); path(8, 7, 8, 11); path(8, 5, 8, 5.1);
            } else if ("refresh".equals(type)) {
                shapes.add(new Arc2D.Double(3, 3, 10, 10, 35, 290, Arc2D.OPEN)); path(10, 2, 14, 3, 13, 7);
            } else { path(8, 3, 8, 13); path(3, 8, 13, 8); }
        }
        void path(double... xy) {
            Path2D p = new Path2D.Double(); p.moveTo(xy[0], xy[1]);
            for (int i = 2; i < xy.length; i += 2) p.lineTo(xy[i], xy[i + 1]); shapes.add(p);
        }
        public int getIconWidth() { return 16; }
        public int getIconHeight() { return 16; }
        public void paintIcon(Component c, Graphics graphics, int x, int y) {
            Graphics2D g = (Graphics2D) graphics.create();
            try {
                g.translate(x, y); g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(c != null && !c.isEnabled() ? MUTED : color); g.setStroke(STROKE);
                for (Shape shape : shapes) g.draw(shape);
            } finally { g.dispose(); }
        }
    }

    // =====================================================================
    // 8. COMPOSITION ROOT: explicit constructor injection, no DI framework.
    // =====================================================================
    static void installLookAndFeel() {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (Exception ex) { System.err.println("Using default Swing look and feel: " + ex.getMessage()); }
    }
    static JFrame createFrame(Preset preset) {
        WorkspaceService service = new MockWorkspaceService();
        WorkspaceView view = new WorkspaceView(preset);
        WorkspaceController controller = new WorkspaceController(view, service, preset != Preset.HISTORY);
        UiEventAdapter events = new UiEventAdapter(view, controller); events.install();
        JFrame frame = new JFrame("File Workspace - Module / Event Mockup (" + preset + ")");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setContentPane(view); frame.setJMenuBar(events.menuBar());
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) { events.close(); }
        });
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(Math.min(1460, screen.width - 60), Math.min(1040, screen.height - 60));
        frame.setMinimumSize(new Dimension(Math.min(1000, screen.width - 60), Math.min(720, screen.height - 60)));
        frame.setLocationRelativeTo(null); controller.start(); return frame;
    }
    public static void main(String[] args) {
        final Preset preset;
        try { preset = args.length == 0 ? Preset.FULL : Preset.valueOf(args[0].toUpperCase(Locale.ROOT)); }
        catch (IllegalArgumentException ex) { System.err.println("Usage: java ModularEventMockup [full|edit|history]"); return; }
        if (GraphicsEnvironment.isHeadless()) { System.err.println("A graphical desktop / DISPLAY is required."); return; }
        SwingUtilities.invokeLater(() -> { installLookAndFeel(); createFrame(preset).setVisible(true); });
    }
}
