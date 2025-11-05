import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class HomeWindow extends JFrame {

    // Optional: keep a theme consistent with your other window
    private static final Color DARKER_BACKGROUND = new Color(40, 40, 40);
    private static final Color DARK_BACKGROUND   = new Color(50, 50, 50);
    private static final Color ACCENT_COLOR      = new Color(0, 150, 136);

    public HomeWindow() {
        setTitle("BiaPy - Home");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 360);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(DARKER_BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(root);

        JLabel title = new JLabel("Welcome to BiaPy", SwingConstants.LEFT);
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        root.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new GridLayout(3, 1, 14, 14));
        root.add(center, BorderLayout.CENTER);

        JButton btnWizard = makePrimaryButton("Start New Project (Wizard)");
        JButton btnYaml   = makePrimaryButton("Open YAML Configuration");
        JButton btnISeg   = makePrimaryButton("Open Instance Segmentation");

        center.add(btnWizard);
        center.add(btnYaml);
        center.add(btnISeg);

        // Actions
        btnWizard.addActionListener(e -> openWizard());
        btnYaml.addActionListener(e -> openYamlEditorViaChooser());
        btnISeg.addActionListener(e -> openInstanceSegmentation());

        // Optional footer
        JLabel hint = new JLabel("Choose an option to begin.");
        hint.setForeground(new Color(220, 220, 220));
        root.add(hint, BorderLayout.SOUTH);
    }

    private JButton makePrimaryButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setBackground(ACCENT_COLOR);
        b.setForeground(Color.WHITE);
        b.setFont(b.getFont().deriveFont(Font.PLAIN, 16f));
        b.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        return b;
    }

    private void openWizard() {
        SwingUtilities.invokeLater(() -> {
            new WizardWindow(this).setVisible(true);
            // Optionally hide home while wizard is open:
            // setVisible(false);
        });
    }

    private void openYamlEditorViaChooser() {
        JFileChooser fc = new JFileChooser();
        int result = fc.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            SwingUtilities.invokeLater(() -> new YamlEditorWindow(this, file).setVisible(true));
            // Optionally hide home:
            // setVisible(false);
        }
    }

    private void openInstanceSegmentation() {
        SwingUtilities.invokeLater(() -> {
            BiaPyInstanceSegmentationGUI gui = new BiaPyInstanceSegmentationGUI();
            gui.setLocationRelativeTo(this);
            gui.setVisible(true);
            // Optionally hide home:
            // setVisible(false);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HomeWindow().setVisible(true));
    }
}
