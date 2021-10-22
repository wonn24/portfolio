import base64
import os
import time
from matplotlib import pyplot as plt
import glob
import numpy as np
import tensorflow as tf
import cv2
import faiss
import torch
import torch.nn as nn

from albumentations.pytorch import ToTensorV2
import albumentations as A

PATH_TO_SAVED_MODEL = "../saved_model"
PATH_TO_LABELS = "../label_map.pbtxt"
TEST_IMAGE_DIR = "../test_images"

PATH_FEATURE_VECTORS = "../m16_v1.npy"
PATH_ALL_IMAGE_TAGS = "../m16_v1.txt"
PATH_TRAINED_IR_MODEL = "../model16.pt"

## numpy array image to base64 image (vice versea)
def openCVToBase64(arr):
    # OpenCV image to base64 image
    _, img_arr = cv2.imencode(".png", arr)
    im_bytes = img_arr.tobytes()
    img_b64 = base64.b64encode(im_bytes)
    return img_b64


def base64ToOpenCV(img_b64):
    # base64 image to OpenCV image
    im_bytes = base64.b64decode(img_b64)
    img_arr = np.frombuffer(im_bytes, dtype=np.uint8)
    image_np = cv2.imdecode(img_arr, flags=cv2.IMREAD_COLOR)
    return image_np


## resize an input image
def resize_(img, size):
    h, w = img.shape[:2]
    padColor = 255

    max = h if h > w else w
    max = max if max >= size else size

    sh, sw = max, max

    # interpolation method
    if h > sh or w > sw:  # shrinking image
        interp = cv2.INTER_AREA

    else:  # stretching image
        interp = cv2.INTER_CUBIC

    # aspect ratio of image
    aspect = float(w) / h
    saspect = float(sw) / sh

    if (saspect > aspect) or ((saspect == 1) and (aspect <= 1)):  # new horizontal image
        new_h = sh
        new_w = np.round(new_h * aspect).astype(int)
        pad_horz = float(sw - new_w) / 2
        pad_left, pad_right = np.floor(pad_horz).astype(int), np.ceil(pad_horz).astype(
            int
        )
        pad_top, pad_bot = 0, 0

    elif (saspect < aspect) or ((saspect == 1) and (aspect >= 1)):  # new vertical image
        new_w = sw
        new_h = np.round(float(new_w) / aspect).astype(int)
        pad_vert = float(sh - new_h) / 2
        pad_top, pad_bot = np.floor(pad_vert).astype(int), np.ceil(pad_vert).astype(int)
        pad_left, pad_right = 0, 0

    # set pad color
    if len(img.shape) == 3 and not isinstance(
        padColor, (list, tuple, np.ndarray)
    ):  # color image but only one color provided
        padColor = [padColor] * 3

    # scale and pad
    scaled_img = cv2.resize(img, (new_w, new_h), interpolation=interp)
    scaled_img = cv2.copyMakeBorder(
        scaled_img,
        pad_top,
        pad_bot,
        pad_left,
        pad_right,
        borderType=cv2.BORDER_CONSTANT,
        value=padColor,
    )

    resized_img = cv2.resize(scaled_img, (size, size), interpolation=cv2.INTER_AREA)

    return resized_img


## define detection function
def findBoxes(detect_fn, image_np):
    # input needs to be a tensor, convert it using `tf.convert_to_tensor`.
    input_tensor = tf.convert_to_tensor(image_np)

    # model expects a batch of images, so add an axis with `tf.newaxis`.
    input_tensor = input_tensor[tf.newaxis, ...]

    st = time.time()
    output_dict = detect_fn.signatures["serving_default"](input_tensor)
    num_detections = int(output_dict.pop("num_detections"))
    output_dict = {
        key: value[0, :num_detections].numpy() for key, value in output_dict.items()
    }
    output_dict["num_detections"] = num_detections
    output_dict["detection_classes"] = output_dict["detection_classes"].astype(np.int64)
    print(f"--- detection.. done in {(time.time() - st):.4f}s ---")

    # maximum top 10 boxes
    top = 10

    # sub_images_cropped = []
    sub_images_scores = []
    sub_images_boxes = []

    if num_detections > 0:
        scores = output_dict["detection_scores"][:top]
        boxes = (output_dict["detection_boxes"][:top] * 1000).astype(np.int64)

        # append top boxes for Image Retrieval
        for score, box in zip(scores, boxes):
            if score < 0.5:  # threshold
                continue

            # sub_images_cropped.append(gray)
            sub_images_scores.append(score)
            sub_images_boxes.append(box.tolist())

    return sub_images_scores, sub_images_boxes


class MyNetwork(nn.Module):
    def __init__(self):
        super(MyNetwork, self).__init__()

    ### hidden ###
    ### hidden ###
    ### hidden ###


def getTopNeighbors(INDEX_FLAT, vec, k, verdose=0):
    st = time.time()
    distances, indexes = INDEX_FLAT.search(vec, k)  # actual search
    if verdose == 1:
        print(f"Searching Top {k} with GPU.. \tdone in {(time.time() - st):.4f}s")

    return distances, indexes


def load_files():
    st = time.time()
    array_candidate = np.load(PATH_FEATURE_VECTORS)
    with open(PATH_ALL_IMAGE_TAGS) as f:
        content = f.readlines()
    IMG_TAGS = [x.strip() for x in content]
    print(f"--- Loading DB.. done in {(time.time() - st):.4f} seconds ---")

    d = array_candidate.shape[1]  # dimension
    xb = np.float32(array_candidate)
    print(f"> number of database: {d}")

    MODEL_IR = MyNetwork()
    ### hidden ###
    ### hidden ###
    ### hidden ###
    
    ## Using an basic index with GPU
    st = time.time()
    index_flat = faiss.IndexFlatL2(d)  # build a flat (CPU) index
    res = faiss.StandardGpuResources()  # use a single GPU
    GPU_INDEX_FLAT = faiss.index_cpu_to_gpu(res, 0, index_flat)
    GPU_INDEX_FLAT.add(xb)  # add vectors to the index
    print(
        f"--- Building INDEX with GPU.. done in {(time.time() - st):.4f} seconds ---",
        GPU_INDEX_FLAT.ntotal,
    )

    return IMG_TAGS, MODEL_IR, GPU_INDEX_FLAT


def getResources():
    ## 0. Ready for Object Detection and Image Retrieval
    # Load trained the model and the detection function
    print("[+] Preparing Object Detection... ", end="")
    start_time = time.time()
    detect_fn = tf.saved_model.load(PATH_TO_SAVED_MODEL)
    end_time = time.time()
    elapsed_time = end_time - start_time
    print(f"done in {elapsed_time:.4f} seconds")

    # Load saved vectors and models
    print("[+] Preparing Image Retrieval... ")
    start_time = time.time()
    IMG_TAGS, MODEL_IR, INDEX_FLAT = load_files()
    end_time = time.time()
    elapsed_time = end_time - start_time
    print(f"done in {elapsed_time:.4f} seconds")

    return detect_fn, IMG_TAGS, MODEL_IR, INDEX_FLAT


def getResults(detect_fn, IMG_TAGS, MODEL_IR, INDEX_FLAT, trans, img_base64):    
    # Detect boundries and return np array images    

    # base64 image to OpenCV image
    image_np = base64ToOpenCV(img_base64)

    # resize the image (1000x1000)
    image_resized = resize_(image_np, 1000)

    ## 1. Object Detection
    ### hidden ###
    ### hidden ###
    ### hidden ###

    ### BEGIN:: Object Detection ###
    result_scores, result_boxes = findBoxes(detect_fn, image_resized)
    ### END:: Object Detection ###

    print(f"scores:\t{result_scores}")  # scores
    print(f"boxes:\t{result_boxes}")  # boxes

    if len(result_scores) == 0:
        print(f"No result.")
        return

    for idx, box in enumerate(result_boxes, start=1):
        ymin, xmin, ymax, xmax = box
        # crop sub-image
        cropped_image = image_resized[ymin:ymax, xmin:xmax]
        gray = cv2.cvtColor(cropped_image, cv2.COLOR_BGR2GRAY)

        ## 2. Image Retrieval
        # Resize image (105x105) and extract feature vector
        stt = time.time()            
        ### hidden ###
        ### hidden ###
        ### hidden ###

        # searching neighbors from 140K images
        topK = 10 # top K neighbors
        distances, indexes = getTopNeighbors(...)

        # print final results
        for idx in indexes[0]:
            print(f"idx: {idx} \t tag: {IMG_TAGS[idx]}")

        print(f"All done in {(time.time() - stt):.4f} seconds\n")

    return


if __name__ == "__main__":
    detect_fn, IMG_TAGS, MODEL_IR, INDEX_FLAT = getResources()
    trans = A.Compose([A.Normalize(mean=0.8444, std=0.5329), ToTensorV2()])

    # Prepare base64 string list
    print("[+] Reading test images... ", end="")
    start_time = time.time()
    IMAGE_PATHS = glob.glob(TEST_IMAGE_DIR + "/*.png")

    N = 10    
    for image_path in IMAGE_PATHS[:N]:
        # read an image with OpenCV
        image_np = cv2.imread(image_path)

        # OpenCV image to base64 image
        img_base64 = openCVToBase64(image_np)
        image_name = image_path.split("/")[-1][:6]  # fist 6 digits only

        ## main function
        print(f"Image ID  {image_name}")
        getResults(detect_fn, IMG_TAGS, MODEL_IR, INDEX_FLAT, trans, img_base64)
        
    end_time = time.time()
    elapsed_time = end_time - start_time
    print(f"done in {elapsed_time:.4f} seconds")

    print("All done.")

    
