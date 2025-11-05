import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class OptionMappingStepPanel extends JPanel {
    private final JComboBox<String> combo;
    private final QuestionSpec spec;
    private final Map<String, Object> config;

    public OptionMappingStepPanel(QuestionSpec spec, Map<String, Object> config) {
        super(new BorderLayout(10, 10));
        this.spec = spec;
        this.config = config;
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Header: title (left) + Help (right)
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel(spec.stepTitle);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        header.add(title, BorderLayout.WEST);

        JButton help = new JButton("Help");
        help.addActionListener(e -> {
            // scrollable help for long HTML
            JEditorPane pane = new JEditorPane("text/html", spec.helpHtml);
            pane.setEditable(false);
            JScrollPane sp = new JScrollPane(pane);
            sp.setPreferredSize(new Dimension(520, 320));
            JOptionPane.showMessageDialog(this, sp, "Help", JOptionPane.INFORMATION_MESSAGE);
        });
        JPanel helpWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        helpWrap.setOpaque(false);
        helpWrap.add(help);
        header.add(helpWrap, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Center: question + dropdown
        JPanel center = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        center.add(new JLabel(spec.questionText), gbc);

        combo = new JComboBox<>(spec.options.toArray(new String[0]));
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        center.add(combo, gbc);
        add(center, BorderLayout.CENTER);

        // Initialize config with default selection, then keep it synced
        applyCurrentSelectionToConfig();
        combo.addActionListener(e -> applyCurrentSelectionToConfig());
        
    }

    public void applyCurrentSelectionToConfig() {
        int idx = Math.max(0, combo.getSelectedIndex());
        Map<String, Object> assign = spec.optionAssignments.get(idx);
        // Write each key→value for this option into the shared config map
        for (Map.Entry<String, Object> en : assign.entrySet()) {
            config.put(en.getKey(), en.getValue());
        }
    }
}
