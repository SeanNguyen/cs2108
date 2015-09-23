Make sure you download the Bag-of-Visual-Words-Image.zip and extract to your Assignment1/FeatureExtractor folder

https://drive.google.com/folderview?id=0B_nM-g91nfPZfmp5c2duQm9TblA5WU9idWtjVk5xRkxkQWw5bXpTdDlOX1hfVXI2SU5mUFk&usp=drive_web

And replace 3 of the python files in the folder with the ones i've comitted	

in line 37 of sift.py

if os.name == "posix":
            cmmd = ".." + os.path.sep + "FeatureExtractor" + os.path.sep + "Bag-of-Visual-Words-Image" + os.path.sep + "sift < " + imagename + " > " + resultname

this is for the linux binary for sift, i'm not sure if the path for it is correct you'll have to check

Also in the google drive extract the PretrainedCodebook and place the files in Assignment1\ImageData\train