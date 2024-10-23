from flask import Flask, request, jsonify

app = Flask(__name__)

@app.route('/ai-server', methods=['POST'])
def check_directory():
    
    data = request.get_json()
    dir = data.get('dir')  
    
    if dir is None:
        return jsonify({"error": "dir is required"}), 400
    
    # AI 이미지 판별 로직 .. 결과 반환
    imgResult = "피부병이 심하네용" # 결과
    return jsonify({'imgResult': imgResult})

if __name__ == '__main__':
    app.run(debug=True)