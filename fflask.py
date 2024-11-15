from flask import Flask, request, jsonify

app = Flask(__name__)

@app.route('/ai-server', methods=['POST'])
def check_directory():
    
    data = request.get_json()
    dir = data.get('dir')  
    type = data.get('breed')
    symptom = data.get('symptom')
    print("사진이 저장된 경로는 : " + dir + ", 반려동물 타입은 : " + type + ", 증상은 : " + symptom)
    if dir is None:
        return jsonify({"error": "dir is required"}), 400
    
    # AI 이미지 판별 로직 .. 결과 반환
    imgResult = "피부병이 심하네용" # 결과
    return jsonify({'imgResult': imgResult})

if __name__ == '__main__':
    app.run(debug=True)