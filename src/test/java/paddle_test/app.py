from flask import Flask, request, jsonify
import paddleocr
import numpy as np
from PIL import Image
import io
import time  # 引入时间模块
import logging  # 引入日志模块

app = Flask(__name__)

# 配置日志记录器
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')

# 初始化 PaddleOCR 模型（假设模型已预装在基础镜像中）
# ocr = paddleocr.PaddleOCR(use_angle_cls=True, lang='ch', show_log=False)
ocr = paddleocr.PaddleOCR(use_angle_cls=False, lang='ch', show_log=False, use_gpu=False, cpu_threads=4)
@app.route('/ocr', methods=['POST'])
def ocr_predict():
    start_time = time.time()  # 记录请求开始时间

    if 'image' not in request.files:
        logging.error("No image uploaded")  # 记录错误日志
        return jsonify({'error': 'No image uploaded'}), 400

    try:
        # 读取上传的图片文件
        image_data = request.files['image'].read()
        image = np.array(Image.open(io.BytesIO(image_data)))

        # 使用 PaddleOCR 进行 OCR 识别
        result = ocr.ocr(image, cls=False)

        # 格式化输出结果
        output = [{'text': line[1][0], 'confidence': line[1][1]} for line in result[0]]

        # 计算处理时间
        processing_time = time.time() - start_time
        logging.info(f"OCR processing completed in {processing_time:.2f} seconds")  # 记录处理时间日志

        return jsonify(output)

    except Exception as e:
        # 捕获异常并记录错误日志
        logging.error(f"Error during OCR processing: {str(e)}")
        return jsonify({'error': 'An error occurred during OCR processing'}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)