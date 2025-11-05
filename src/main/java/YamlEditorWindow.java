import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.yaml.snakeyaml.Yaml;
import java.util.Map;

public class YamlEditorWindow extends JFrame {
    private final JFrame parent;
    private final File yamlFile;
    private final JTextArea textArea = new JTextArea();

    public YamlEditorWindow(JFrame parent, File yamlFile) {
        this.parent = parent;
        this.yamlFile = yamlFile;

        setTitle("YAML Config: " + yamlFile.getName());
        setSize(800, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        add(buildUI());
        loadFile();
    }

    private JComponent buildUI() {
        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        root.add(new JScrollPane(textArea), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Save");
        JButton btnOpenEditor = new JButton("Open Structured Editor");
        JButton btnClose = new JButton("Close");

        btnSave.addActionListener(this::saveFile);
        btnOpenEditor.addActionListener(e -> openStructuredEditor());
        btnClose.addActionListener(e -> {
            dispose();
            if (parent != null) parent.setVisible(true);
        });

        actions.add(btnSave);
        actions.add(btnOpenEditor);
        actions.add(btnClose);
        root.add(actions, BorderLayout.SOUTH);

        return root;
    }
    
    private String sanitizeYaml(String yamlText) {
        // Replace tabs with 4 spaces before parsing
        return yamlText.replace("\t", "    ");
    }
    
    private void loadFile() {
        try {
        	String content = Files.readString(yamlFile.toPath(), StandardCharsets.UTF_8);
        	content = sanitizeYaml(content);
        	Yaml yaml = new Yaml();
        	Map<String, Object> data = yaml.load(content);
            textArea.setText(data.toString());
            textArea.setCaretPosition(0);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to read file:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveFile(ActionEvent e) {
        try (Writer w = new OutputStreamWriter(new FileOutputStream(yamlFile), StandardCharsets.UTF_8)) {
            w.write(textArea.getText());
            JOptionPane.showMessageDialog(this, "Saved.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to save:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openStructuredEditor() {
        // Placeholder: here you could parse YAML and build a form.
        // For now, just show a message.
        JOptionPane.showMessageDialog(this,
                "Structured editor not implemented yet.\n" +
                "Tip: add SnakeYAML to parse the file and build form fields.",
                "Info", JOptionPane.INFORMATION_MESSAGE);
    }
}
