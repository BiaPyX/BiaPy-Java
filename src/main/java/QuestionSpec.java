import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Describes one wizard question with options and per-option key→value assignments. */
public class QuestionSpec {
    public final String stepTitle;     // e.g., "Question 1 of N"
    public final String shortTitle;
    public final String questionText;  // the question to show
    public final List<String> options; // dropdown items in order
    public final String helpHtml;      // HTML for the Help dialog
    /**
     * For each option (same index as in options), provide a map of key→value
     * pairs to write into the final config when that option is selected.
     * Example: optionAssignments.get(0).put("PROBLEM.NDIM", "3D")
     */
    public final List<Map<String, Object>> optionAssignments;
	

    public QuestionSpec(String stepTitle,
    					String shortTitle,
                        String questionText,
                        List<String> options,
                        String helpHtml,
                        List<Map<String, Object>> optionAssignments) {
        this.stepTitle = stepTitle;
        this.shortTitle = shortTitle;
        this.questionText = questionText;
        this.options = options;
        this.helpHtml = helpHtml;
        this.optionAssignments = optionAssignments;
        if (options.size() != optionAssignments.size()) {
            throw new IllegalArgumentException("options and optionAssignments must have same size");
        }
    }

    /** Helper to build a simple key→value map inline. */
    public static Map<String, Object> kv(Object... pairs) {
        if (pairs.length % 2 != 0) throw new IllegalArgumentException("kv requires even number of args");
        Map<String, Object> m = new LinkedHashMap<>();
        for (int i = 0; i < pairs.length; i += 2) {
            m.put((String) pairs[i], pairs[i + 1]);
        }
        return m;
    }
}
