package com.khushbu.imageimport;

import java.io.*;
import java.util.Scanner;

public class ImageImporter {
	private static final String PIXEL_DELIMITER = " ";
	private static final char FILE_NAME_DELIMITER = File.pathSeparatorChar;

	private int xSize;
	private int ySize;

	public ImageImporter(int xSize, int ySize) {
		this.xSize = xSize;
		this.ySize = ySize;
	}

    String data_2d[];

	public Image loadSingleImage(String filePath) {
		File file = new File(filePath);

        try {
        FileInputStream fileInputStream = null;

            fileInputStream = new FileInputStream(filePath);

        Scanner scan = new Scanner(fileInputStream);

        // Discard the magic number
        scan.nextLine();
        // Read pic width, height and max value
        int picWidth = scan.nextInt();
        int picHeight = scan.nextInt();
        int maxvalue = scan.nextInt();


            fileInputStream.close();


        // Now parse the file as binary data
        fileInputStream = new FileInputStream(filePath);
        DataInputStream dis = new DataInputStream(fileInputStream);

        // look for 4 lines (i.e.: the header) and discard them
        int numnewlines = 3;
        while (numnewlines > 0) {
            char c;
            do {
                c = (char) (dis.readUnsignedByte());
            } while (c != '\n');
            numnewlines--;
        }

        // read the image data
        double[][] data2D = new double[picHeight][picWidth];
        data_2d=new String[picHeight];


            for (int row = 0; row < picHeight; row++) {
            data_2d[row]="";
            for (int col = 0; col < picWidth; col++) {
                data2D[row][col] = dis.readUnsignedByte();
                double temp=data2D[row][col];
                data_2d[row]=data_2d[row]+ String.valueOf((int)temp)+" ";
            }
            data_2d[row]=data_2d[row].substring(0,data_2d[row].length()-1);
        }


            String s2=filePath.substring(filePath.indexOf("image_"),filePath.length()-4)+".txt";
			s2="ImageTexts/"+s2;
           // System.out.println("\n path is "+filePath);
           // System.out.println("\n s2 is " + s2);

			try(BufferedReader br = new BufferedReader(new FileReader(s2))) {
				StringBuilder sb = new StringBuilder();
				String line = br.readLine();
                int x=0;
				while (line != null) {
					sb.append(line);
					sb.append(System.lineSeparator());
					line = br.readLine();
                    if(line!=null) {
                        String s3[] = line.split(" ");
                      //  System.out.println("\n\nS3 length is " + s3.length + "\n\n");
                        data_2d[x]=line.substring(0,line.length()-1);
                        //System.out.println("\nline is " + line);
                        x++;
                    }
				}
			}






        } catch (IOException e) {
            e.printStackTrace();
        }





        ImageBuilder imgBuilder = new ImageBuilder();
		try {
			FileInputStream fileStream = new FileInputStream(file);
			InputStreamReader inputStreamReader = new InputStreamReader(
					fileStream);
			BufferedReader reader = new BufferedReader(inputStreamReader);

			// TODO: assumes unique file names!
			String imageName = filePath.substring(filePath
					.lastIndexOf(FILE_NAME_DELIMITER) + 1);
			imgBuilder.newImage(xSize, ySize).setName(imageName);

			//System.out.println("Loading image " + imageName + "...");
			readPrefixes(reader, imgBuilder);
			readContent(reader, imgBuilder);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return imgBuilder.buildImage();
	}



	private void readContent(BufferedReader reader, ImageBuilder imgBuilder) throws IOException {
		String line = reader.readLine();
		int xPos = 0;
		int yPos = 0;

        for(int j=0;j<data_2d.length;j++)
        {
			line = data_2d[j];
			String[] values = line.trim().split(PIXEL_DELIMITER);

			for (int i = 0; i < values.length; i++) {
                short value = Short.parseShort(values[i]);
				imgBuilder.setPoint(xPos, yPos, value);
				xPos++;
				if (xPos == xSize) {
					// go to next line
					xPos = 0;
					yPos++;
				}				
			}
			line = reader.readLine(); 
		}
	}

	/**
	 * Removes comments from lines
	 * @param line
	 * @return
	 */
	private String trimLine(String line) {
		int commentDelimiterPosition = line.indexOf("#");
		if (commentDelimiterPosition == -1) {
			return line.trim();
		} else {
			return line.substring(0, commentDelimiterPosition).trim();
		}
	}

	private void readPrefixes(BufferedReader reader, ImageBuilder imgBuilder)
			throws IOException {
		int tokenCounter = 1;

		while (tokenCounter < 5) {
			String line = trimLine(reader.readLine());
			if (line.equals("")) {
				continue;
			} else {
				String[] tokens = line.split(PIXEL_DELIMITER);
				for (int i = 0; i < tokens.length; i++, tokenCounter++) {
					if (tokenCounter == 4) {
						imgBuilder.setMaximumGreyScaleValue(Short
								.parseShort(tokens[i]));
					}
				}
			}
		}

		if (tokenCounter > 5) {
			System.out
					.println("File began on same line as the greyscale value. Import will not work properly!");
		}

	}
}
