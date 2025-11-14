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
		    "Question 4",
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
		    "Question 5",
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
		
		// --- Q6 ---
        String helpHtmlStep6 =
            "<html>"
          + "<p>"
          + "This question is meant to determine the size of the objects of interest in your images. "
          + "For example, if you're working on cell nucleus segmentation, you should have a general idea of the "
          + "size these nuclei will appear in the images."
          + "</p>"
          + "<p>"
          + "Continuing with the example, in the figure below, several cell nuclei are shown, all roughly the same size. "
          + "If we measure one at random and find it is 24 pixels wide and 44 pixels tall, we can estimate the size range "
          + "as 25–100 px. These measurements don't need to be exact, but they help BiaPy to set up a more optimized workflow."
          + "</p>"
          + "<div style='text-align:center;'>"
          + "  <img src='" + classpathImageUrl("/wizard/object_size.png") + "' width='200px'/>"
          + "</div>"
          + "<br><br><br><br><br><br><br><br><br><br><br><br><br><br>"
          + "</html>";
        
    	QuestionSpec q6 = new QuestionSpec(
            "Question 6",
            "What is the average object width/height in pixels?",
            Arrays.asList(
                "0-25 px",
                "25-100 px",
                "100-200 px",
                "200-500 px",
                "More than 500 px"
            ),
            helpHtmlStep6,
            Arrays.asList(
                QuestionSpec.kv(
                    "DATA.PATCH_SIZE_XY", new int[]{256, 256},
                    "TEST.POST_PROCESSING.REMOVE_CLOSE_POINTS_RADIUS", 10
                ),
                QuestionSpec.kv(
                    "DATA.PATCH_SIZE_XY", new int[]{256, 256},
                    "TEST.POST_PROCESSING.REMOVE_CLOSE_POINTS_RADIUS", 20
                ),
                QuestionSpec.kv(
                    "DATA.PATCH_SIZE_XY", new int[]{512, 512},
                    "TEST.POST_PROCESSING.REMOVE_CLOSE_POINTS_RADIUS", 30
                ),
                QuestionSpec.kv(
                    "DATA.PATCH_SIZE_XY", new int[]{512, 512},
                    "TEST.POST_PROCESSING.REMOVE_CLOSE_POINTS_RADIUS", 30
                ),
                QuestionSpec.kv(
                    "DATA.PATCH_SIZE_XY", new int[]{1024, 1024},
                    "TEST.POST_PROCESSING.REMOVE_CLOSE_POINTS_RADIUS", 30
                )
            )
        );

        // condition: OR on PROBLEM.TYPE, AND on MODEL.SOURCE
        QuestionCondition cond6 = new QuestionCondition();
        cond6.orCond.add(new Object[]{
            "PROBLEM.TYPE",
            Arrays.asList("SEMANTIC_SEG", "INSTANCE_SEG", "DETECTION", "CLASSIFICATION", "IMAGE_TO_IMAGE")
        });
        cond6.andCond.add(new Object[]{"MODEL.SOURCE", "biapy"});

        specs.add(q6);
        conditions.add(cond6);
		
        // --- Q7 ---
        String helpHtmlStep7 =
            "<html>"
          + "<p>"
          + "This question aims to determine the size of the objects of interest in your images along the Z axis. "
          + "For example, if you're working on cell nucleus segmentation, you should have a rough idea of how many slices "
          + "the nuclei will appear across in the images. Refer to the image below for a clearer, visual understanding of this concept."
          + "</p>"
          + "<div style='text-align:center;'>"
          + "  <img src='" + classpathImageUrl("/wizard/object_slices.png") + "' width='200px'/>"
          + "</div>"
          + "<br><br><br><br><br><br><br><br><br><br><br><br><br><br>"
          + "</html>";

        QuestionSpec q7 = new QuestionSpec(
            "Question 7",
            "How many slices can an object be represented in?",
            Arrays.asList(
                "1-5 slices",
                "5-10 slices",
                "10-20 slices",
                "20-60 slices",
                "More than 60 slices"
            ),
            helpHtmlStep7,
            Arrays.asList(
                QuestionSpec.kv("DATA.PATCH_SIZE_Z", 5),
                QuestionSpec.kv("DATA.PATCH_SIZE_Z", 10),
                QuestionSpec.kv("DATA.PATCH_SIZE_Z", 20),
                QuestionSpec.kv("DATA.PATCH_SIZE_Z", 40),
                QuestionSpec.kv("DATA.PATCH_SIZE_Z", 80)
            )
        );

        // condition: AND on PROBLEM.NDIM & MODEL.SOURCE, OR on PROBLEM.TYPE
        QuestionCondition cond7 = new QuestionCondition();
        cond7.andCond.add(new Object[]{"PROBLEM.NDIM", "3D"});
        cond7.andCond.add(new Object[]{"MODEL.SOURCE", "biapy"});
        cond7.orCond.add(new Object[]{"PROBLEM.TYPE", Arrays.asList(
            "SEMANTIC_SEG", "INSTANCE_SEG", "DETECTION", "CLASSIFICATION", "IMAGE_TO_IMAGE"
        )});

        specs.add(q7);
        conditions.add(cond7);

        
		// --- Q8 ---
		String helpHtmlStep8 =
		    "<html>"
		  + "<p>"
		  + "This question is meant to determine which phases of the workflow you want to perform."
		  + "</p>"
		  + "<p>"
		  + "Before using a deep learning model, it needs to be trained. The goal is to train the model on a specific task, "
		  + "then apply it to new images. For instance, you might train a model to classify images based on their labels. "
		  + "If the training is successful, the model should be able to classify new images automatically. "
		  + "This final phase is known as 'Test', sometimes referred to as 'Inference' or 'Prediction', "
		  + "which all describe the process of applying the model's knowledge to new data."
		  + "</p>"
		  + "</html>";

		QuestionSpec q8 = new QuestionSpec(
		    "Question 8",
		    "What do you want to do?",
		    Arrays.asList(
		        "Train a model",
		        "Test a model",
		        "Train and test the model"
		    ),
		    helpHtmlStep8,
		    Arrays.asList(
		        QuestionSpec.kv(
		            "TRAIN.ENABLE", Boolean.TRUE,
		            "TEST.ENABLE", Boolean.FALSE
		        ),
		        QuestionSpec.kv(
		            "TRAIN.ENABLE", Boolean.FALSE,
		            "TEST.ENABLE", Boolean.TRUE
		        ),
		        QuestionSpec.kv(
		            "TRAIN.ENABLE", Boolean.TRUE,
		            "TEST.ENABLE", Boolean.TRUE
		        )
		    )
		);

		specs.add(q8);
		conditions.add(null);

		
        // --- Q9 ---
        String helpHtmlStep9 =
            "<html>"
          + "<p>"
          + "In this step, you need to specify the folder where the images for training the network are located. "
          + "All these images must have the same number of channels. It's also important that each channel contains "
          + "the same type of information to avoid confusing the model."
          + "</p>"
          + "<p>"
          + "The 'Check data' button will verify if the images within the selected folder can be read correctly by "
          + "analyzing them one by one. This process may take some time, but it ensures that the images will be "
          + "correctly read later in BiaPy. For this specific case, the following will be checked:"
          + "</p>"
          + "<ul>"
          + "  <li><p>All images can be read properly.</p></li>"
          + "  <li><p>All images have the same number of input channels.</p></li>"
          + "  <li><p>All images are within the same value range. For example, if the images are in uint8 format, "
          + "their values should be between 0 and 255. This must be true for all images.</p></li>"
          + "</ul>"
          + "</html>";

        QuestionSpec q9 = new QuestionSpec(
            "Question 9",
            "Could you please specify the location of the training raw image folder? After that, click on the 'Check data' button to analyze the data.",
            Arrays.asList("PATH"),     // same format as your wizard for path questions
            helpHtmlStep9,
            Arrays.asList(
                QuestionSpec.kv(
                    "DATA.TRAIN.PATH", ""   // empty string as in Python wizard
                )
            )
        );

        // condition: show only when TRAIN.ENABLE = true
        QuestionCondition cond9 = new QuestionCondition();
        cond9.andCond.add(new Object[]{"TRAIN.ENABLE", Boolean.TRUE});

        specs.add(q9);
        conditions.add(cond9);

        // --- Q10 ---
        String helpHtmlStep10 =
            "<html>"
          + "<p>"
          + "In this step, you need to specify the directory where the training target data, also known as 'ground truth', "
          + "is located. This target data will be used to train the network."
          + "</p>"
          + "<p>"
          + "The 'Check data' button will verify that the files in the selected folder can be read properly by analyzing "
          + "them one by one. This process may take some time, but it ensures that the data will be correctly read by BiaPy later."
          + "</p>"
          + "<p>"
          + "The target and its verification vary depending on the workflow:"
          + "</p>"
          + "<ul>"
          + "<li>"
          + "For <strong>Semantic segmentation</strong>, single-channel images are expected, specifically semantic masks. "
          + "Checks: all images readable, same number of channels, and the number of classes is extracted automatically."
          + "</li>"
          + "<li>"
          + "For <strong>Instance segmentation</strong>, one or two-channel images are expected. The first channel contains "
          + "instance masks; the second, if present, stores class information. Checks: readability, same channels, and auto class extraction."
          + "</li>"
          + "<li>"
          + "For <strong>Detection</strong>, files with object center coordinates are expected. Checks: readability, presence of required "
          + "columns, and auto-detection of number of classes if a 'class' column exists."
          + "</li>"
          + "<li>"
          + "For <strong>Super-resolution</strong>, high-resolution images are expected (2× or 4× larger). Checks: readability, same channels, "
          + "and consistent value range (e.g., uint8 → 0–255)."
          + "</li>"
          + "<li>"
          + "For <strong>Image-to-Image</strong>, images are expected. Checks: readability, same channels, and consistent value range."
          + "</li>"
          + "</ul>"
          + "</html>";

        QuestionSpec q10 = new QuestionSpec(
            "Question 10",
            "Could you please specify the location of the training ground truth (target) folder? "
          + "After that, click on the 'Check data' button to analyze the data.",
            Arrays.asList("PATH"),
            helpHtmlStep10,
            Arrays.asList(
                QuestionSpec.kv(
                    "DATA.TRAIN.GT_PATH", ""   // empty string as in Python wizard
                )
            )
        );

        // Condition: TRAIN.ENABLE = true  AND  PROBLEM.TYPE ∈ {...}
        QuestionCondition cond10 = new QuestionCondition();
        cond10.andCond.add(new Object[]{"TRAIN.ENABLE", Boolean.TRUE});
        cond10.orCond.add(new Object[]{
            "PROBLEM.TYPE",
            Arrays.asList(
                "SEMANTIC_SEG",
                "INSTANCE_SEG",
                "DETECTION",
                "SUPER_RESOLUTION",
                "IMAGE_TO_IMAGE"
            )
        });

        specs.add(q10);
        conditions.add(cond10);

        
        // --- Q11 ---
        String helpHtmlStep11 =
            "<html>"
          + "<p>"
          + "In this step, you need to specify the folder where the images for testing the network are located. "
          + "All these images must have the same number of channels. It's also important that each channel contains "
          + "the same type of information to avoid confusing the model."
          + "</p>"
          + "<p>"
          + "The 'Check data' button will verify if the images within the selected folder can be read correctly by "
          + "analyzing them one by one. This process may take some time, but it ensures that the images will be "
          + "correctly read later in BiaPy. For this specific case, the following will be checked:"
          + "</p>"
          + "<ul>"
          + "  <li><p>All images can be read properly.</p></li>"
          + "  <li><p>All images have the same number of input channels.</p></li>"
          + "  <li><p>All images are within the same value range. For example, if the images are in uint8 format, "
          +          "their values should be between 0 and 255. This must be true for all images.</p></li>"
          + "</ul>"
          + "</html>";

        QuestionSpec q11 = new QuestionSpec(
            "Question 11",
            "Could you please specify the location of the test raw image folder? "
          + "After that, click on the 'Check data' button to analyze the data.",
            Arrays.asList("PATH"),
            helpHtmlStep11,
            Arrays.asList(
                QuestionSpec.kv(
                    "DATA.TEST.PATH", ""     // empty string as in Python wizard
                )
            )
        );

        // Condition: TEST.ENABLE = true
        QuestionCondition cond11 = new QuestionCondition();
        cond11.andCond.add(new Object[]{"TEST.ENABLE", Boolean.TRUE});

        specs.add(q11);
        conditions.add(cond11);

        
        // --- Q12 ---
        String helpHtmlStep12 =
            "<html>"
          + "<p>"
          + "This question is meant to determine whether you have target data, or ground truth, for the test data. "
          + "If you have this, BiaPy will be able to calculate various metrics, which will vary depending on the "
          + "selected workflow, to measure the model's performance."
          + "</p>"
          + "<p>"
          + "If you answer 'Yes', a follow-up question will appear asking for the path to the target data."
          + "</p>"
          + "</html>";

        QuestionSpec q12 = new QuestionSpec(
            "Question 12",
            "Do you have test ground truth (target) data?",
            Arrays.asList(
                "No",
                "Yes"
            ),
            helpHtmlStep12,
            Arrays.asList(
                QuestionSpec.kv(
                    "DATA.TEST.LOAD_GT", Boolean.FALSE
                ),
                QuestionSpec.kv(
                    "DATA.TEST.LOAD_GT", Boolean.TRUE
                )
            )
        );

        // Condition:
        // AND: TEST.ENABLE = true
        // OR : PROBLEM.TYPE ∈ {SEMANTIC_SEG, INSTANCE_SEG, DETECTION, SUPER_RESOLUTION, IMAGE_TO_IMAGE}
        QuestionCondition cond12 = new QuestionCondition();
        cond12.andCond.add(new Object[]{"TEST.ENABLE", Boolean.TRUE});
        cond12.orCond.add(new Object[]{
            "PROBLEM.TYPE",
            Arrays.asList(
                "SEMANTIC_SEG",
                "INSTANCE_SEG",
                "DETECTION",
                "SUPER_RESOLUTION",
                "IMAGE_TO_IMAGE"
            )
        });

        specs.add(q12);
        conditions.add(cond12);

        // --- Q13 ---
        String helpHtmlStep13 =
            "<html>"
          + "<p>"
          + "In this step, you need to specify the directory where the test target data, also known as 'ground truth', "
          + "is located. This target data will be used to train the network."
          + "</p>"
          + "<p>"
          + "The 'Check data' button will verify that the files in the selected folder can be read properly by analyzing "
          + "them one by one. This process may take some time, but it ensures that the data will be correctly read by BiaPy later."
          + "</p>"
          + "<p>"
          + "The target and its verification vary depending on the workflow:"
          + "</p>"
          + "<ul>"
          + "<li>"
          + "For <strong>Semantic segmentation</strong>, single-channel images are expected, specifically semantic masks. "
          + "Checks: readability, consistent channels, and automatic class extraction."
          + "</li>"
          + "<li>"
          + "For <strong>Instance segmentation</strong>, instance masks (and optionally class masks) are expected. "
          + "Checks: readability, consistent channels, and automatic class extraction if class masks exist."
          + "</li>"
          + "<li>"
          + "For <strong>Detection</strong>, files containing object centroid coordinates are expected. "
          + "Checks: readability, presence of needed columns, and automatic class count extraction when 'class' column exists."
          + "</li>"
          + "<li>"
          + "For <strong>Super-resolution</strong>, high-resolution images are expected. "
          + "Checks: readability, consistent channels, and consistent value ranges."
          + "</li>"
          + "<li>"
          + "For <strong>Image-to-Image</strong>, images are expected. "
          + "Checks: readability, consistent channels, and consistent value ranges (e.g., uint8 → 0–255)."
          + "</li>"
          + "</ul>"
          + "</html>";

        QuestionSpec q13 = new QuestionSpec(
            "Question 13",
            "Could you please specify the location of the test ground truth (target) folder? "
          + "After that, click on the 'Check data' button to analyze the data.",
            Arrays.asList("PATH"),
            helpHtmlStep13,
            Arrays.asList(
                QuestionSpec.kv(
                    "DATA.TEST.GT_PATH", ""   // empty default value
                )
            )
        );

        // Conditions:
        // AND: TEST.ENABLE = true, DATA.TEST.LOAD_GT = true
        // OR:  PROBLEM.TYPE ∈ supported workflows
        QuestionCondition cond13 = new QuestionCondition();
        cond13.andCond.add(new Object[]{"TEST.ENABLE", Boolean.TRUE});
        cond13.andCond.add(new Object[]{"DATA.TEST.LOAD_GT", Boolean.TRUE});
        cond13.orCond.add(new Object[]{
            "PROBLEM.TYPE",
            Arrays.asList(
                "SEMANTIC_SEG",
                "INSTANCE_SEG",
                "DETECTION",
                "SUPER_RESOLUTION",
                "IMAGE_TO_IMAGE"
            )
        });

        specs.add(q13);
        conditions.add(cond13);

        // Build panels
        stepPanels = new ArrayList<>();
        int idx = 0;
        for (QuestionSpec spec : specs) {
            // Number steps nicely if you want: "Step X of N"
            String numbered = spec.stepTitle;
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
            updateNavButtons(back, next, finish);
        });
        back.addActionListener(e -> {
            applyStepSelection(step);
            int p = prevVisibleFrom(step);
            if (p != step) {
                step = p;
                cardLayout.show(cards, String.valueOf(step));
            }
            updateNavButtons(back, next, finish);
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
        // find first and last *visible* steps
        int firstVisible = 0;
        for (int i = 0; i < specs.size(); i++) {
            if (isVisibleStep(i)) {
                firstVisible = i;
                break;
            }
        }

        int lastVisible = 0;
        for (int i = specs.size() - 1; i >= 0; i--) {
            if (isVisibleStep(i)) {
                lastVisible = i;
                break;
            }
        }

        back.setEnabled(step > firstVisible);
        next.setEnabled(nextVisibleFrom(step) != step);
        finish.setEnabled(step == lastVisible);
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


