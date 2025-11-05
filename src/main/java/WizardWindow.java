import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.ArrayList;

public class WizardWindow extends JFrame {
    private final Map<String, Object> config = new LinkedHashMap<>();
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);
    private int step = 0;
    private java.util.List<JPanel> stepPanels;
    private final java.util.List<QuestionCondition> conditions = new java.util.ArrayList<>();
    private java.util.List<QuestionSpec> specs;
    
    public WizardWindow(Frame parent) {
        super("New Project Wizard");
        setSize(760, 520);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(buildUI());
    }

    private static String classpathImageUrl(String absoluteResourcePath) {
        // absoluteResourcePath must start with '/', e.g. "/wizard/semantic_seg_collage.png"
        java.net.URL u = WizardWindow.class.getResource(absoluteResourcePath);
        if (u == null) {
            // Dev fallback: print a clear error. You can also throw if you prefer.
            System.err.println("Resource NOT found on classpath: " + absoluteResourcePath);
            return ""; // empty --> image won't render, but app won't crash
        }
        return u.toExternalForm(); // Works in IDE and in exported JAR
    }
    
    private JComponent buildUI() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // ===================== Q1 =====================
        String helpHtmlStep1 =
            "<html>" +
            "<p>The purpose of this question is to determine the type of images you will use. " +
            "This will help decide which deep learning model will be applied to process your images. The options are:</p>" +
            "<ul>" +
            "<li><p><strong>2D images</strong>&nbsp;are considered those that can be represented as (x, y, channels). For example, (512, 1024, 2).</p></li>" +
            "<li><p><strong>3D images</strong>&nbsp;are those that can be represented as (x, y, z, channels). For example, (400, 400, 50, 1).</p></li>" +
            "</ul>" +
            "<p>The last option, &quot;No, but I would like to have a 3D stack output,&quot; refers to working with 2D images. " +
            "After processing them with the deep learning model, the images will be combined in sequence to create a 3D stack. " +
            "This option is useful if your 2D images together form a larger 3D volume.</p>" +
            "</html>";
        QuestionSpec q1 = new QuestionSpec(
            "Question 1",
            "Are your images in 3D?",
            Arrays.asList("Yes", "No", "No but I would like to have a 3D stack output"),
            helpHtmlStep1,
            Arrays.asList(
                QuestionSpec.kv("PROBLEM.NDIM", "3D", "TEST.ANALIZE_2D_IMGS_AS_3D_STACK", Boolean.FALSE),
                QuestionSpec.kv("PROBLEM.NDIM", "2D", "TEST.ANALIZE_2D_IMGS_AS_3D_STACK", Boolean.FALSE),
                QuestionSpec.kv("PROBLEM.NDIM", "2D", "TEST.ANALIZE_2D_IMGS_AS_3D_STACK", Boolean.TRUE)
            )
        );
        specs = new ArrayList<>(Arrays.asList(q1));
        conditions.add(null);
        
        // --- Q2 ---
        String helpHtmlStep2 =
        	    "<html>"
        	  + "<p>"
        	  + "This question determines the type of workflow you want to run. Each option corresponds to a workflow implemented in BiaPy. "
        	  + "The options are:"
        	  + "</p>"

        	  // Semantic segmentation
        	  + "<p>"
        	  + "<strong>&#8226; &quot;Generate masks of different (or just one) objects/regions within the image&quot;</strong>&nbsp;"
        	  + "refers to the workflow called &quot;Semantic segmentation&quot;. The goal of this workflow is to assign a class to each pixel (or voxel) of the input image, thus producing a label image with semantic masks. "
        	  + "The simplest case would be binary classification, as in the figure depicted below. There, only two labels are present in the label image: black pixels (usually with value 0) represent the background, "
        	  + "and white pixels represent the foreground (the wound in this case, usually labeled with 1 or 255 value)."
        	  + "</p>"
        	  + "<div style='text-align:center;'>"
        	  + "  <img src='" + classpathImageUrl("/wizard/semantic_seg_collage.png") + "' width='200px'/>"
        	  + "</div>"
        	  + "<br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>"
        	  + "<p>Find more information about semantic segmentation workflow in "
        	  + "<a href='https://biapy.readthedocs.io/en/latest/workflows/semantic_segmentation.html'>its documentation</a>.</p>"

        	  // Instance segmentation
        	  + "<p>"
        	  + "<strong>&#8226; &quot;Generate masks for each object in the image&quot;</strong>&nbsp;"
        	  + "refers to the workflow called &quot;Instance segmentation&quot;. The goal of this workflow is to assign a unique ID, i.e. an integer value, to each object of the input image, thus producing a label image with instance masks. "
        	  + "An example of this task is displayed in the figure below, with an electron microscopy image used as input (left) and its corresponding instance label image identifying each individual mitochondrion (right). "
        	  + "Each color in the mask image corresponds to a unique object."
        	  + "</p>"
        	  + "<div style='text-align:center;'>"
        	  + "  <img src='" + classpathImageUrl("/wizard/instance_seg_collage.png") + "' width='200px'/>"
        	  + "</div>"
        	  + "<br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>"
        	  + "<p>Find more information about instance segmentation workflow in "
        	  + "<a href='https://biapy.readthedocs.io/en/latest/workflows/instance_segmentation.html'>its documentation</a>.</p>"

        	  // Detection
        	  + "<p>"
        	  + "<strong>&#8226; &quot;Identify and count roughly circular objects in the images, without needing an exact outline around each one&quot;</strong>&nbsp;"
        	  + "refers to the workflow called &quot;Detection&quot;. The goal of this workflow is to localize objects in the input image, not requiring a pixel-level class. "
        	  + "Common strategies produce either bounding boxes containing the objects or individual points at their center of mass, which is the one adopted by BiaPy."
        	  + "</p>"
        	  + "<p>"
        	  + "An example of this task is displayed in the figure below (credits to "
        	  + "<a href='https://zenodo.org/records/3715492#.Y4m7FjPMJH6'>Jukkala & Jacquemet</a>), with a fluorescence microscopy image used as input (left) and its corresponding nuclei detection results (right). "
        	  + "Each red dot in the right image corresponds to a unique predicted object."
        	  + "</p>"
        	  + "<div style='text-align:center;'>"
        	  + "  <img src='" + classpathImageUrl("/wizard/detection_collage.png") + "' width='200px'/>"
        	  + "</div>"
        	  + "<br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>"
        	  + "<p>Find more information about detection workflow in "
        	  + "<a href='https://biapy.readthedocs.io/en/latest/workflows/detection.html'>its documentation</a>.</p>"

        	  // Denoising
        	  + "<p>"
        	  + "<strong>&#8226; &quot;Clean noisy images&quot;</strong>&nbsp;"
        	  + "refers to the workflow called &quot;Denoising&quot;. The goal of this workflow is to remove noise from the input images. "
        	  + "BiaPy makes use of <a href='https://openaccess.thecvf.com/content_CVPR_2019/html/Krull_Noise2Void_-_Learning_Denoising_From_Single_Noisy_Images_CVPR_2019_paper.html'>Noise2Void</a> with any of the U-Net-like models provided. "
        	  + "The main advantage of Noise2Void is neither relying on noise image pairs nor clean target images since frequently clean images are simply unavailable."
        	  + "</p>"
        	  + "<p>"
        	  + "An example of this task is displayed in the figure below, with a noisy fluorescence image and its corresponding denoised output. "
        	  + "This image was obtained from a <a href='https://zenodo.org/record/5156913'>Convallaria dataset</a> used in the same "
        	  + "<a href='https://openaccess.thecvf.com/content_CVPR_2019/html/Krull_Noise2Void_-_Learning_Denoising_From_Single_Noisy_Images_CVPR_2019_paper.html'>Noise2Void</a> project."
        	  + "</p>"
        	  + "<div style='text-align:center;'>"
        	  + "  <img src='" + classpathImageUrl("/wizard/denoising_collage.png") + "' width='200px'/>"
        	  + "</div>"
        	  + "<br><br><br><br><br><br><br><br><br><br><br><br><br><br>"
        	  + "<p>Find more information about denoising workflow in "
        	  + "<a href='https://biapy.readthedocs.io/en/latest/workflows/denoising.html'>its documentation</a>.</p>"

        	  // Super-resolution
        	  + "<p>"
        	  + "<strong>&#8226; &quot;Upsample images into higher resolution&quot;</strong>&nbsp;"
        	  + "refers to the workflow called &quot;Super resolution&quot;. The goal of this workflow is to reconstruct high-resolution (HR) images from low-resolution (LR) ones. "
        	  + "If there is a difference in the size of the LR and HR images, typically determined by a scale factor (x2, x4), this task is known as single-image super-resolution. "
        	  + "If the size of the LR and HR images is the same, this task is usually referred to as image restoration."
        	  + "</p>"
        	  + "<p>"
        	  + "An example of this task is displayed in the figure below, with a LR fluorescence microscopy image used as input (left) and its corresponding HR image (x2 scale factor). "
        	  + "Credits of this image to <a href='https://figshare.com/articles/dataset/BioSR/13264793'>F-actin dataset by Qiao et al</a>."
        	  + "</p>"
        	  + "<div style='text-align:center;'>"
        	  + "  <img src='" + classpathImageUrl("/wizard/sr_collage.png") + "' width='200px'/>"
        	  + "</div>"
        	  + "<br><br><br><br><br><br><br><br><br><br><br><br><br><br>"
        	  + "<p>Find more information about super-resolution workflow in "
        	  + "<a href='https://biapy.readthedocs.io/en/latest/workflows/super_resolution.html'>its documentation</a>.</p>"

        	  // Classification
        	  + "<p>"
        	  + "<strong>&#8226; &quot;Assign a label to each image&quot;</strong>&nbsp;"
        	  + "refers to the workflow called &quot;Classification&quot;. The goal of this workflow is to assign a label to the input image. "
        	  + "In the figure below a few examples of this workflow's input are depicted where each image is classified as a result. "
        	  + "Images obtained from <a href='https://medmnist.com/'>MedMNIST v2</a>, concretely from DermaMNIST."
        	  + "</p>"
        	  + "<div style='text-align:center;'>"
        	  + "  <img src='" + classpathImageUrl("/wizard/classification_collage.png") + "' width='200px'/>"
        	  + "</div>"
        	  + "<br><br><br><br><br><br><br><br><br><br><br><br><br>"
        	  + "<p>Find more information about classification workflow in "
        	  + "<a href='https://biapy.readthedocs.io/en/latest/workflows/classification.html'>its documentation</a>.</p>"

        	  // Image-to-Image
        	  + "<p>"
        	  + "<strong>&#8226; &quot;I want to restore a degraded image&quot; and &quot;I want to generate new images based on an input one&quot;</strong>&nbsp;"
        	  + "both refer to the workflow called &quot;Image to image&quot;. The goal of this workflow is translating/mapping input images into target images. "
        	  + "This workflow is like super-resolution but with no upsampling (scale factor x1)."
        	  + "</p>"
        	  + "<p>"
        	  + "In the figure below an example of paired microscopy images (brightfield) is depicted. "
        	  + "The images were obtained from <a href='https://lightmycells.grand-challenge.org/'>Light My Cells dataset</a>."
        	  + "</p>"
        	  + "<div style='text-align:center;'>"
        	  + "  <img src='" + classpathImageUrl("/wizard/i2i_collage.png") + "' width='200px'/>"
        	  + "</div>"
        	  + "<br><br><br><br><br><br><br><br><br><br><br><br><br><br>"
        	  + "<p>Find more information about image to image workflow in "
        	  + "<a href='https://biapy.readthedocs.io/en/latest/workflows/image_to_image.html'>its documentation</a>.</p>"
        	  + "</html>";


        QuestionSpec q2 = new QuestionSpec(
            "Question 2",
            "Do you want to:",
            Arrays.asList(
                "Generate masks of different (or just one) objects/regions within the image",
                "Generate masks for each object in the image",
                "Identify and count roughly circular objects in the images, without needing an exact outline around each one",
                "Clean noisy images",
                "Upsample images into higher resolution",
                "Assign a label to each image",
                "Restore a degraded image",
                "Generate new images based on an input one"
            ),
            helpHtmlStep2,
            Arrays.asList(
                QuestionSpec.kv("PROBLEM.TYPE", "SEMANTIC_SEG"),
                QuestionSpec.kv("PROBLEM.TYPE", "INSTANCE_SEG"),
                QuestionSpec.kv("PROBLEM.TYPE", "DETECTION"),
                QuestionSpec.kv("PROBLEM.TYPE", "DENOISING"),
                QuestionSpec.kv("PROBLEM.TYPE", "SUPER_RESOLUTION"),
                QuestionSpec.kv("PROBLEM.TYPE", "CLASSIFICATION"),
                QuestionSpec.kv("PROBLEM.TYPE", "IMAGE_TO_IMAGE"),
                QuestionSpec.kv("PROBLEM.TYPE", "IMAGE_TO_IMAGE")
            )
        );
        specs.add(q2);
        conditions.add(null);
        
        // --- Q3 ---
        String helpHtmlStep3 =
            "<html>"
          + "<p>"
          + "This question determines how the deep learning model will be built. "
          + "Based on your choice, additional questions may appear to guide the model setup process."
          + "</p>"
          + "<p>"
          + "Before using a deep learning model, it must be trained. The idea is to train the model on a specific task and then apply it to new images. "
          + "For example, you could train a model to classify images based on their labels. If the training is successful, the model should be able to classify new images automatically."
          + "</p>"
          + "<p>"
          + "Training is a crucial step to ensure good results, but it requires labeled data, also known as 'ground truth'. Using the image classification example, you would need to label the training images first, "
          + "like 'image1.png' as class 1, 'image2.png' as class 2, etc. Because training can be time-consuming, using a pre-trained model can be a major advantage. "
          + "A pre-trained model has already been trained on a dataset and has some level of knowledge."
          + "</p>"
          + "<p>"
          + "If your images are similar to those used to train the pre-trained model, you might get good results without retraining it. "
          + "If not, having similar images will at least make the retraining process faster and require fewer images. "
          + "That's why it’s always a good idea to check for available pre-trained models that match your needs. Here are the options available:"
          + "</p>"
          + "<ul>"
          + "  <li><p><strong>'No, I want to build a model from scratch'</strong>&nbsp; "
          + "  This option configures BiaPy to create a model from the ground up. No additional input will be required, and BiaPy will automatically configure the model based on the selected workflow and image dimensions.</p></li>"
          + "  <li><p><strong>'Yes, I have a model previously trained in BiaPy'</strong>&nbsp; "
          + "  Choose this option if you already have a model trained with BiaPy. The model must be stored in the folder you’ve selected for saving results, specifically in the 'checkpoints' folder, as a file with a '.pth' extension. "
          + "  You’ll need to specify this file when prompted after selecting this option.</p></li>"
          + "  <li><p><strong>'Yes, I want to check if there is a pretrained model I can use'</strong>&nbsp; "
          + "  This option allows you to load a pre-trained model from an external source. Currently, BiaPy supports loading models from the "
          + "  <a href='https://bioimage.io/#/'>BioImage Model Zoo</a> and <a href='https://pytorch.org/vision/stable/models.html'>Torchvision</a>. "
          + "  Depending on the workflow and image dimensions, you can search for compatible models to use.</p></li>"
          + "</ul>"
          + "</html>";

        QuestionSpec q3 = new QuestionSpec(
            "Question 3",
            "Do you want to use a pre-trained model?",
            Arrays.asList(
                "No, I want to build a model from scratch",
                "Yes, I have a model previously trained in BiaPy",
                "Yes, I want to check if there is a pretrained model I can use"
            ),
            helpHtmlStep3,
            Arrays.asList(
                // MODEL.SOURCE, MODEL.LOAD_CHECKPOINT, MODEL.LOAD_MODEL_FROM_CHECKPOINT
                QuestionSpec.kv(
                    "MODEL.SOURCE", "biapy",
                    "MODEL.LOAD_CHECKPOINT", Boolean.FALSE,
                    "MODEL.LOAD_MODEL_FROM_CHECKPOINT", Boolean.FALSE
                ),
                QuestionSpec.kv(
                    "MODEL.SOURCE", "biapy",
                    "MODEL.LOAD_CHECKPOINT", Boolean.TRUE,
                    "MODEL.LOAD_MODEL_FROM_CHECKPOINT", Boolean.TRUE
                ),
                QuestionSpec.kv(
                    "MODEL.SOURCE", "bmz",
                    "MODEL.LOAD_CHECKPOINT", Boolean.FALSE,
                    "MODEL.LOAD_MODEL_FROM_CHECKPOINT", Boolean.FALSE
                )
            )
        );
		specs.add(q3);	
		conditions.add(null);
		
		// --- Q4 ---
		String helpHtmlStep4 =
		    "<html><p>"
		  + "This question is about setting the path to the pre-trained model in BiaPy. "
		  + "The model should be stored in the folder you previously selected for saving results. "
		  + "Specifically, in the 'checkpoints' folder, there will be a file with a '.pth' extension. "
		  + "You need to locate this file by clicking the 'Browse' button."
		  + "</p></html>";

		QuestionSpec q4 = new QuestionSpec(
		    "Step 4",
		    "Please select the pretrained model trained with BiaPy before:",
		    Arrays.asList("MODEL_BIAPY"),  // one “option” just to keep the structure uniform
		    helpHtmlStep4,
		    Arrays.asList(
		        QuestionSpec.kv("PATHS.CHECKPOINT_FILE", "")
		    )
		);

		// condition: show only if MODEL.SOURCE=biapy AND MODEL.LOAD_CHECKPOINT=true
		QuestionCondition cond4 = new QuestionCondition();
		cond4.andCond.add(new Object[]{"MODEL.SOURCE", "biapy"});
		cond4.andCond.add(new Object[]{"MODEL.LOAD_CHECKPOINT", Boolean.TRUE});
		specs.add(q4);
		conditions.add(cond4);
		
		// --- Q5 ---
		String helpHtmlStep5 =
		    "<html>"
		  + "<p>This question allows you to load a pre-trained model from an external source. "
		  + "Currently, BiaPy supports loading models from the "
		  + "<a href='https://bioimage.io/#/'>BioImage Model Zoo</a> and "
		  + "<a href='https://pytorch.org/vision/stable/models.html'>Torchvision</a>.</p>"
		  + "<p>To search for all available models, click the 'Check models' button. "
		  + "This will start an online search requiring an internet connection and may take a while. "
		  + "Once complete, a new window will appear displaying all compatible models.</p>"
		  + "</html>";

		QuestionSpec q5 = new QuestionSpec(
		    "Step 5",
		    "Please select a pretrained model by pressing 'Check models' below. "
		      + "This process requires internet connection and may take a while.",
		    Arrays.asList("MODEL_OTHERS"),
		    helpHtmlStep5,
		    Arrays.asList(
		        QuestionSpec.kv("MODEL.BMZ.SOURCE_MODEL_ID", "")
		    )
		);

		// condition: OR of three blocks
		QuestionCondition cond5 = new QuestionCondition();
		cond5.orCond.add(new Object[]{"MODEL.SOURCE", Arrays.asList("bmz", "torchvision")});
		cond5.orCond.add(new Object[]{"PROBLEM.TYPE", Arrays.asList(
		    "SEMANTIC_SEG","INSTANCE_SEG","DETECTION","DENOISING",
		    "SUPER_RESOLUTION","SELF_SUPERVISED","CLASSIFICATION","IMAGE_TO_IMAGE"
		)});
		cond5.orCond.add(new Object[]{"PROBLEM.NDIM", Arrays.asList("2D","3D")});

		specs.add(q5);
		conditions.add(cond5);
		
        // Build panels
        stepPanels = new ArrayList<>();
        int idx = 0;
        for (QuestionSpec spec : specs) {
            // Number steps nicely if you want: "Step X of N"
            String numbered = spec.stepTitle + " of " + specs.size();
            QuestionSpec numberedSpec = new QuestionSpec(
                    numbered, spec.questionText, spec.options, spec.helpHtml, spec.optionAssignments
            );
            OptionMappingStepPanel p = new OptionMappingStepPanel(numberedSpec, config);
            stepPanels.add(p);
            cards.add(p, String.valueOf(idx++));
        }

        root.add(cards, BorderLayout.CENTER);

        // Navigation
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton back = new JButton("Back");
        JButton next = new JButton("Next");
        JButton finish = new JButton("Finish");

        next.addActionListener(e -> {
            applyStepSelection(step);
            int n = nextVisibleFrom(step);
            if (n != step) {
                step = n;
                cardLayout.show(cards, String.valueOf(step));
            }
        });
        back.addActionListener(e -> {
            applyStepSelection(step);
            int p = prevVisibleFrom(step);
            if (p != step) {
                step = p;
                cardLayout.show(cards, String.valueOf(step));
            }
        });

        finish.addActionListener((ActionEvent e) -> {
            applyStepSelection(step);
            // For now, just show it:
            JOptionPane.showMessageDialog(this,
                    "Collected config:\n" + config,
                    "Wizard Result", JOptionPane.INFORMATION_MESSAGE);

            // OPTIONAL: dump to YAML if SnakeYAML is on your classpath.
            // dumpToYamlFile(config, new File("biapy_config.yaml"));

            dispose();
        });

        updateNavButtons(back, next, finish);
        nav.add(back);
        nav.add(next);
        nav.add(finish);
        root.add(nav, BorderLayout.SOUTH);

        return root;
    }

    private void updateNavButtons(JButton back, JButton next, JButton finish) {
        back.setEnabled(step > 0);
        next.setEnabled(step < stepPanels.size() - 1);
        finish.setEnabled(step == stepPanels.size() - 1);
    }

    private boolean isVisibleStep(int idx) {
        QuestionCondition c = conditions.get(idx);
        return c == null || c.matches(config);
    }

    private int nextVisibleFrom(int idx) {
        for (int i = idx + 1; i < specs.size(); i++)
            if (isVisibleStep(i)) return i;
        return idx;
    }
   
    private int prevVisibleFrom(int idx) {
        for (int i = idx - 1; i >= 0; i--)
            if (isVisibleStep(i)) return i;
        return idx;
    }
    
    private void applyStepSelection(int stepIndex) {
        Component c = stepPanels.get(stepIndex);
        if (c instanceof OptionMappingStepPanel) {
            ((OptionMappingStepPanel) c).applyCurrentSelectionToConfig();
        }
    }

    public Map<String, Object> getCollectedConfig() { return config; }

}

//Used to decide whether a question should be visible, based on config values
class QuestionCondition {
	java.util.List<Object[]> orCond = new ArrayList<>();
	java.util.List<Object[]> andCond = new ArrayList<>();
	
	boolean matches(Map<String, Object> config) {
		boolean orOk = orCond.isEmpty();
	    for (Object[] c : orCond) {
	    	String key = (String) c[0];
	        Object val = config.get(key);
	        Object expected = c[1];
	        if (expected instanceof Collection<?>) {
	            if (((Collection<?>) expected).contains(val)) { orOk = true; break; }
	        } else if (Objects.equals(val, expected)) { orOk = true; break; }
	    }
	
	    boolean andOk = true;
	    for (Object[] c : andCond) {
	        String key = (String) c[0];
	        Object val = config.get(key);
	        Object expected = c[1];
	        boolean ok = expected instanceof Collection<?>
	            ? ((Collection<?>) expected).contains(val)
	            : Objects.equals(val, expected);
	        if (!ok) { andOk = false; break; }
	    }
	    return orOk && andOk;
	 }
}


