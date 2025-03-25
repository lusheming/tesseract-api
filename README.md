docker 部署直接就是 restful 接口
- 实现 => 基于 demisto/tesseract:1.0.0.2038079 容器环境 增加 java http服务
- 增加两个识别接口(文件上传、文件地址识别)
- predictWords 多个参数中一个匹配 hit=true 多个参数传参英文逗号分割 例如： UU,DD
```curl
curl --location --request POST 'http://localhost:8080/api/ocr/file' \
--header 'User-Agent: Apifox/1.0.0 (https://apifox.com)' \
--header 'Accept: */*' \
--header 'Host: localhost:8080' \
--header 'Connection: keep-alive' \
--header 'Content-Type: multipart/form-data; boundary=--------------------------444921320147806363023567' \
--form 'file=@"/Users/cc/Downloads/test777.jpg"' \
--form 'predictWords="XXX"'
```

```curl
curl --location --request GET 'http://localhost:8080/api/ocr/url?url=https://cdn.whatchat.vip/im/1742382130358_1000074072.jpg&predictWords=XXX,XXXX' \
--header 'User-Agent: Apifox/1.0.0 (https://apifox.com)' \
--header 'Accept: */*' \
--header 'Host: localhost:8080' \
--header 'Connection: keep-alive'
```

```json
Response：
{
    "result": "9:57 @- Biull XXXXX",
    "hit": true
}
```
