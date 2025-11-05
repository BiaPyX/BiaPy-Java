import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.*;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.ui.FlatBorder;
import java.net.URL;

public class BiaPyInstanceSegmentationGUI extends JFrame {
    private JPanel mainPanel;
    private JPanel rightPanel;
    private JPanel inputPanel;
    private JPanel chartContainerPanel;
    private JPanel statsPanel;
    private JPanel historyPanel;
    private JPanel titlePanel;

    // Custom colors for different shades
    private static final Color DARKER_BACKGROUND = new Color(40, 40, 40);
    private static final Color DARK_BACKGROUND = new Color(50, 50, 50);
    private static final Color MEDIUM_BACKGROUND = new Color(60, 60, 60);
    private static final Color LIGHT_BACKGROUND = new Color(70, 70, 70);
    private static final Color ACCENT_COLOR = new Color(0, 150, 136); // Teal accent

    public BiaPyInstanceSegmentationGUI() {
        setTitle("BiaPy - Instance Segmentation");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set FlatLaf Dark Theme
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
            UIManager.put( "Component.arc", 15 ); // Rounded corners
            UIManager.put( "Button.arc", 15 );
            UIManager.put( "ProgressBar.arc", 15 );
            UIManager.put( "TextComponent.arc", 15 );
        } catch (Exception e) {
            e.printStackTrace();
        }

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(DARKER_BACKGROUND);

        createTitlePanel();
        createRightPanel();
        createHistoryPanel();

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(historyPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private void createTitlePanel() {
        titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        titlePanel.setBackground(DARK_BACKGROUND);

        JLabel titleLabel = new JLabel("BiaPy - Instance Segmentation");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        // Add buttons with icons
        buttonPanel.add(createIconButton("", "icons/help.png", "Get help on using the application"));
        buttonPanel.add(createIconButton("", "icons/documentation.png", "View documentation"));
        buttonPanel.add(createIconButton("", "icons/github.png", "Visit GitHub repository"));
        titlePanel.add(buttonPanel, BorderLayout.EAST);
    }

    private void createRightPanel() {
        rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(DARKER_BACKGROUND);

        createInputPanel();
        createChartContainerPanel();
        createStatsPanel();

        rightPanel.add(inputPanel, BorderLayout.NORTH);
        rightPanel.add(chartContainerPanel, BorderLayout.CENTER);
        rightPanel.add(statsPanel, BorderLayout.SOUTH);
    }

    private void createInputPanel() {
        inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(createTitledBorder("Input Parameters"));
        inputPanel.setBackground(DARK_BACKGROUND);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        addLabelAndComponent(inputPanel, "Name:", new JTextField(20), gbc, 0, "icons/name.png");
        addLabelAndComponent(inputPanel, "Data Folder:", createFolderChooserPanel(), gbc, 1, "icons/folder.png");
        addLabelAndComponent(inputPanel, "Epochs:", new JTextField("100", 5), gbc, 2, "icons/epochs.png");
        addLabelAndComponent(inputPanel, "Model:", new JComboBox<>(new String[]{"Train from scratch", "Use pretrained model"}), gbc, 3, "icons/model.png");
        addLabelAndComponent(inputPanel, "Training Mode:", new JComboBox<>(new String[]{"Train", "Fine-tune"}), gbc, 4, "icons/training_mode.png");

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        // Add start and stop buttons with icons
        JButton startButton = createIconButton("Start Training", "icons/start.png", "Begin training process");
        JButton stopButton = createIconButton("Stop Training", "icons/stop.png", "Stop training process");

        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        inputPanel.add(buttonPanel, gbc);
    }

    private void createChartContainerPanel() {
        chartContainerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        chartContainerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        chartContainerPanel.setBackground(DARKER_BACKGROUND);

        chartContainerPanel.add(createChartPanel("Loss", "Training Loss", "Validation Loss"));
        chartContainerPanel.add(createChartPanel("Jaccard Index", "Jaccard Index"));
    }

    private ChartPanel createChartPanel(String title, String... seriesNames) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        for (String seriesName : seriesNames) {
            dataset.addSeries(new XYSeries(seriesName));
        }

        JFreeChart chart = ChartFactory.createXYLineChart(
                title,
                "Epoch",
                title,
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // Customize chart appearance
        chart.setBackgroundPaint(DARKER_BACKGROUND);
        chart.getTitle().setPaint(Color.WHITE);
        chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 16));
        chart.getLegend().setItemFont(new Font("SansSerif", Font.PLAIN, 12));
        chart.getLegend().setBackgroundPaint(DARK_BACKGROUND);
        chart.getLegend().setItemPaint(Color.WHITE);

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(MEDIUM_BACKGROUND);
        plot.setDomainGridlinePaint(Color.GRAY);
        plot.setRangeGridlinePaint(Color.GRAY);
        plot.getDomainAxis().setLabelPaint(Color.WHITE);
        plot.getDomainAxis().setTickLabelPaint(Color.WHITE);
        plot.getRangeAxis().setLabelPaint(Color.WHITE);
        plot.getRangeAxis().setTickLabelPaint(Color.WHITE);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBackground(DARKER_BACKGROUND);
        chartPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        return chartPanel;
    }

    private void createStatsPanel() {
        statsPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        statsPanel.setBorder(createTitledBorder("Training Statistics"));
        statsPanel.setBackground(DARK_BACKGROUND);

        addStatsLabel(statsPanel, "Current Epoch: 0 / 100", "icons/epoch.png");
        addStatsLabel(statsPanel, "Time Elapsed: 00:00:00", "icons/time.png");
        addStatsLabel(statsPanel, "Estimated Time Remaining: 00:00:00", "icons/time_remaining.png");
        addStatsLabel(statsPanel, "Current Loss: 0.0", "icons/loss.png");
        addStatsLabel(statsPanel, "Current Jaccard Index: 0.0", "icons/jaccard.png");
        addStatsLabel(statsPanel, "Learning Rate: 0.001", "icons/learning_rate.png");
    }

    private void createHistoryPanel() {
        historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBorder(createTitledBorder("Training History"));
        historyPanel.setPreferredSize(new Dimension(300, 0));
        historyPanel.setBackground(DARK_BACKGROUND);

        String[] columnNames = {"Name", "Loss", "Accuracy"};
        Object[][] data = {
                {"Run 1", "0.25", "0.85"},
                {"Run 2", "0.20", "0.88"},
                {"Run 3", "0.18", "0.90"}
        };
        JTable historyTable = new JTable(data, columnNames);
        historyTable.setRowHeight(30);
        historyTable.setFillsViewportHeight(true);
        historyTable.setBackground(MEDIUM_BACKGROUND);
        historyTable.setForeground(Color.WHITE);
        historyTable.setGridColor(Color.GRAY);
        historyTable.getTableHeader().setBackground(DARK_BACKGROUND);
        historyTable.getTableHeader().setForeground(Color.WHITE);
        historyTable.setShowVerticalLines(false);
        historyTable.setIntercellSpacing(new Dimension(0, 0));
        historyTable.setBorder(null);

        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(DARK_BACKGROUND);
        scrollPane.getViewport().setBackground(MEDIUM_BACKGROUND);

        historyPanel.add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createFolderChooserPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JTextField textField = new JTextField(20);
        JButton browseButton = createIconButton("", "icons/browse.png", "Browse for folder");

        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int option = fileChooser.showOpenDialog(BiaPyInstanceSegmentationGUI.this);
                if (option == JFileChooser.APPROVE_OPTION) {
                    textField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            }
        });

        panel.add(textField, BorderLayout.CENTER);
        panel.add(browseButton, BorderLayout.EAST);
        return panel;
    }

    private JButton createIconButton(String text, String iconPath, String toolTip) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(5, 10, 5, 10));
        button.setToolTipText(toolTip);
        button.setBackground(ACCENT_COLOR);
        button.setForeground(Color.WHITE);

        try {
            URL iconURL = getClass().getResource(iconPath);
            if (iconURL != null) {
                ImageIcon icon = new ImageIcon(iconURL);
                button.setIcon(icon);
                if (!text.isEmpty()) {
                    button.setHorizontalTextPosition(SwingConstants.RIGHT);
                    button.setIconTextGap(8);
                } else {
                    button.setText("");
                }
            } else {
                System.err.println("Icon not found: " + iconPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Apply rounded corners
        button.setBorder(new RoundedBorder(15));
        return button;
    }

    private void addLabelAndComponent(JPanel panel, String labelText, JComponent component, GridBagConstraints gbc, int gridy, String iconPath) {
        gbc.gridx = 0;
        gbc.gridy = gridy;
        JLabel label = new JLabel(labelText);
        label.setForeground(Color.WHITE);

        // Add icon to label
        try {
            URL iconURL = getClass().getResource(iconPath);
            if (iconURL != null) {
                ImageIcon icon = new ImageIcon(iconURL);
                label.setIcon(icon);
                label.setIconTextGap(8);
            } else {
                System.err.println("Icon not found: " + iconPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        component.setBorder(new RoundedBorder(8));
        panel.add(component, gbc);
    }

    private void addStatsLabel(JPanel panel, String text, String iconPath) {
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setForeground(Color.WHITE);

        // Add icon to label
        try {
            URL iconURL = getClass().getResource(iconPath);
            if (iconURL != null) {
                ImageIcon icon = new ImageIcon(iconURL);
                label.setIcon(icon);
                label.setIconTextGap(8);
            } else {
                System.err.println("Icon not found: " + iconPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        panel.add(label);
    }

    private TitledBorder createTitledBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP
        );
        border.setTitleColor(Color.WHITE);
        border.setTitleFont(new Font("SansSerif", Font.BOLD, 14));
        return border;
    }

    // Custom rounded border class
    class RoundedBorder extends AbstractBorder {
        private int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(UIManager.getColor("Component.borderColor"));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(4, 8, 4, 8);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = 8;
            insets.top = insets.bottom = 4;
            return insets;
        }
    }

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            BiaPyInstanceSegmentationGUI gui = new BiaPyInstanceSegmentationGUI();
//            gui.setVisible(true);
//        });
//    }
}
