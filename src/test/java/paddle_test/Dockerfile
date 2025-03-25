# docker commit stoic_newton my_paddle_image:v1
# 基础镜像（用户已有的修改后的镜像）
FROM my_paddle_image:v1

# 安装 Flask 依赖
RUN pip install flask==2.2.5 pillow numpy

# 复制 API 代码到容器
COPY app.py /app/app.py

# 暴露端口（Flask 默认端口）
EXPOSE 5000

# 启动服务
CMD ["python", "/app/app.py"]