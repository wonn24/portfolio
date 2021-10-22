최초 작성일: 2021-10-13

마지막 업데이트: 2021-10-19

요구 환경:
- Python 3.7
- Tensorflow 2.4 (GPU)
- OpenCV
- Faiss (GPU)

요구 파일:
- label_map.pbtxt: 라벨맵 파일
- saved_model/: Object Detection 모델 폴더
- ***.npy: 정답 셋 파일 (특징 벡터)
- ***.txt: 정답 셋 파일 (이미지 이름)
- ***.pt: Image Retrieval 모델 파일

- Object Detection:
  - input: numpy array image via OpenCV imread function
  - output: numpy array list consists of detected sub-images

- Image Retrieval:
  - input: one numpy array image
  - output: top K (10 as default) neighbors (sorted) 

소요 시간: avg. 0.05s (Object Detection), avg. 0.005s (Image Retrieval)

GPU 환경: 
name: Tesla P100-PCIE-16GB, computeCapability: 6.0, coreClock: 1.3285GHz, coreCount: 56, deviceMemorySize: 15.90GiB, deviceMemoryBandwidth: 681.88GiB/s

성능:
Object Detection MAP@IoU:.50:.95 - 93%, MAP@IoU:.50:.95 - 91%
Image Search: Acc 94%
