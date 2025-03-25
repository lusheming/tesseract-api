from flask import Flask, request, jsonify
import easyocr
import numpy as np
from PIL import Image
import io
import logging
import time  # 导入时间模块

# 配置日志
logging.basicConfig(
    level=logging.INFO,  # 设置日志级别为 INFO
    format='%(asctime)s - %(levelname)s - %(message)s',  # 日志格式
    handlers=[
        logging.StreamHandler()  # 将日志输出到控制台
    ]
)

app = Flask(__name__)

# 初始化 EasyOCR 读取器（支持中文和英文，禁用 GPU）
reader = easyocr.Reader(['en'], gpu=False)
logging.info("EasyOCR reader initialized.")

@app.route('/ocr', methods=['POST'])
def ocr_predict():
    # 记录请求开始时间
    start_time = time.time()

    # 检查是否有图片上传
    if 'image' not in request.files:
        logging.error("No image uploaded.")
        return jsonify({'error': 'No image uploaded'}), 400

    logging.info("Image file received.")

    # 读取上传的图片
    image_file = request.files['image']
    try:
        logging.info("Processing image file...")
        image = Image.open(io.BytesIO(image_file.read()))
        image = np.array(image)
        logging.info("Image processed successfully.")
    except Exception as e:
        logging.error(f"Invalid image file: {str(e)}")
        return jsonify({'error': f'Invalid image file: {str(e)}'}), 400

    # 执行 OCR 识别
    try:
        logging.info("Performing OCR recognition...")
        result = reader.readtext(image)
        logging.info(f"OCR recognition completed. Found {len(result)} text blocks.")
        # 格式化输出结果
        output = [{'text': item[1], 'confidence': float(item[2])} for item in result]
        logging.info(f"Formatted output: {output}")
    except Exception as e:
        logging.error(f"OCR processing failed: {str(e)}")
        return jsonify({'error': f'OCR processing failed: {str(e)}'}), 500

    # 记录请求结束时间并计算执行时间
    end_time = time.time()
    execution_time = end_time - start_time
    logging.info(f"Request completed in {execution_time:.2f} seconds.")

    # 返回结果
    return jsonify(output)

if __name__ == '__main__':
    logging.info("Starting Flask application on host 0.0.0.0 and port 2000.")
    app.run(host='0.0.0.0', port=2000)