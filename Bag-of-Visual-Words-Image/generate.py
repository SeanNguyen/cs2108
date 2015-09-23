import argparse
from cPickle import load
from learn import extractSift, computeHistograms, writeHistogramsToFile
import os

HISTOGRAMS_FILE = 'visual_words_for_test_data'
CODEBOOK_FILE = 'codebook_b.file'
IMAGE_INDEX_FILE = 'image_indexe_test'

def parse_arguments():
    parser = argparse.ArgumentParser(description='generate visual words histogram for test image')
    parser.add_argument('-c', help='path to the codebook file', required=False, default=CODEBOOK_FILE)
    parser.add_argument('input_image', help='path to input image', nargs='+')
    args = parser.parse_args()
    return args

#print "## extract Sift features"
all_files = []
all_features = {}

args = parse_arguments()
codebook_file = args.c
fnames = args.input_image
all_features = extractSift(fnames)

with open(codebook_file, 'rb') as f:
    codebook = load(f)

word_histgram = computeHistograms(codebook, all_features[fnames[0]])
nclusters = codebook.shape[0]

result = [x for x in word_histgram]
print result

