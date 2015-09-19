import java.util.ArrayList;
import java.util.List;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ImageSearchUI extends JFrame
implements ActionListener {
	JFileChooser fc;
	JPanel contentPane;

	JButton openButton, searchButton;
	BufferedImage bufferedimage;

	JLabel [] imageLabels;

	File file = null;

	ImageSearch is = new ImageSearch();


	public ImageSearchUI() {

		imageLabels = new JLabel [ is.getResultSize() ];

		openButton = new JButton("Select an image...",
				createImageIcon("images/Open16.gif"));
		openButton.addActionListener(this);

		searchButton = new JButton("Search");
		searchButton.addActionListener(this);

		//For layout purposes, put the buttons in a separate panel
		JPanel buttonPanel = new JPanel(); //use FlowLayout
		buttonPanel.add(openButton);
		buttonPanel.add(searchButton);


		JPanel imagePanel = new JPanel();
		imagePanel.setLayout(new GridLayout(0,5));

		for (int i = 0; i<imageLabels.length;i++){
			imageLabels[i] = new JLabel();
			imagePanel.add(imageLabels[i]);
		}

		contentPane = (JPanel)this.getContentPane();
		setSize(1280,900);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		contentPane.add(buttonPanel, BorderLayout.PAGE_START);
		contentPane.add(imagePanel, BorderLayout.CENTER);

		contentPane.setVisible(true);
		setVisible(true);
		//        add(logScrollPane, BorderLayout.CENTER);

	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = ImageSearchUI.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	public void actionPerformed(ActionEvent e) {
		//Set up the file chooser.
		if (e.getSource() == openButton) {
			if (fc == null) {
				fc = new JFileChooser();

				//Add a custom file filter and disable the default
				//(Accept All) file filter.
				fc.addChoosableFileFilter(new ImageFilter());
				fc.setAcceptAllFileFilterUsed(false);

				//Add custom icons for file types.
				fc.setFileView(new ImageFileView());

				//Add the preview pane.
				fc.setAccessory(new ImagePreview(fc));
			} 


			//Show it.
			int returnVal = fc.showDialog(ImageSearchUI.this,
					"Select an image..");

			//Process the results.
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file = fc.getSelectedFile();

			}

			fc.setSelectedFile(null);
		}else if (e.getSource() == searchButton) {

			try {
				bufferedimage = ImageIO.read(file);
				List<ImageData> results = new ArrayList<ImageData>();
				results = is.search(SearchType.COLORHIST, bufferedimage);

				for(int i = 0; i<is.getResultSize(); i++) {
					imageLabels[i].setIcon(new ImageIcon(results.get(i).getImage()));
					System.out.println(results.get(i));
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {

		ImageSearchUI isApp = new ImageSearchUI();
	}
}
