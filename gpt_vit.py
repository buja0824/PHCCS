from flask import Flask, request, jsonify, render_template
# from flask_cors import CORS
import os
from transformers import AutoFeatureExtractor, AutoModelForImageClassification, pipeline
from PIL import Image
import openai
# import matplotlib.pyplot as plt
from dotenv import load_dotenv

# q = openai.chat.completions.create(
#     model = "gpt-4o",
#     messages=[{"role":"user", "content":"WAZZUP!"}]
# )

#.env=API 키 들어가있는 경로

load_dotenv("/root/capstone-vit/VIT_HUGGINGFACE/.env")


API_KEY = os.getenv("FLASK_API_KEY")

# 파인튜닝된 모델 체크포인트 경로
checkpoint = "/root/capstone-vit/completed_models/dog_y_vit_model_75e/checkpoint-206400"

# OpenAI API 키 설정 (YOUR_API_KEY는 실제 API 키로 대체해야 함)
openai.api_key = API_KEY

# Flask 앱 초기화
app = Flask(__name__)

# 모델 및 파이프라인 초기화
feature_extractor = AutoFeatureExtractor.from_pretrained(checkpoint)
model = AutoModelForImageClassification.from_pretrained(checkpoint)
image_classifier = pipeline("image-classification", model=model, feature_extractor=feature_extractor)

# 홈 페이지 라우트
@app.route('/', methods=['GET'])
def hello_world():
    return render_template('index.html')

# 이미지 업로드 및 예측 라우트
@app.route('/', methods=['POST'])
def predict():
    # 업로드된 이미지 파일 가져오기
    imagefile = request.files['imagefile']
    image_path = "/root/capstone-vit/temp_img/" + imagefile.filename
    imagefile.save(image_path)

    # 이미지 열기
    image = Image.open(image_path)

    # 이미지 분류 수행
    results = image_classifier(image)

    # 가장 높은 확률의 레이블 가져오기
    top_result = results[0]
    classification = f"{top_result['label']} with confidence {top_result['score'] * 100:.2f}%"

    # GPT API를 사용해 자연스러운 문장 도출
    response = openai.chat.completions.create( #Issue with openai.ChatCompletion.create() in Latest OpenAI Python Library. openai.chat.completions.create 이거 쓰셈 이걸로 대체됨.
        model="gpt-4o",
        messages=[
            {"role": "system", "content": "You are Korean Vet now."},
            {"role": "assistant", "content": f"The image classification result is: {classification}. Please generate a natural description in Korean."}
        ],
        max_tokens=400
    )
    natural_language_description = response.choices[0].message.content

    # 결과를 HTML로 렌더링하여 반환
    return render_template('index.html', prediction=natural_language_description)

# Flask 애플리케이션 실행
if __name__ == '__main__':
    app.run(port=3002, debug=True)
