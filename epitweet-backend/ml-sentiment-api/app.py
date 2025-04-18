from flask import Flask, request, jsonify
from transformers import XLMRobertaTokenizer, AutoModelForSequenceClassification
import torch
import torch.nn.functional as F
import os

app = Flask(__name__)

MODEL_NAME = "cardiffnlp/twitter-xlm-roberta-base-sentiment"
tokenizer = XLMRobertaTokenizer.from_pretrained(MODEL_NAME)
model = AutoModelForSequenceClassification.from_pretrained(MODEL_NAME)

LABELS = ['negative', 'neutral', 'positive']
@app.route("/analyse", methods=["POST"])
def analyze_sentiment():
    try:
        print("Headers:", request.headers)
        print("Data (raw):", request.data)
        print("JSON:", request.get_json())

        data = request.get_json()
        text = data.get("content", "")

        if not text:
            return jsonify({"error": "Missing 'content' in request"}), 400

        inputs = tokenizer(text, return_tensors="pt", truncation=True)
        with torch.no_grad():
            logits = model(**inputs).logits
            probs = F.softmax(logits, dim=1)

        max_index = torch.argmax(probs)
        sentiment = LABELS[max_index]
        confidence = probs[0][max_index].item()

        return jsonify({
            "sentiment": sentiment,
            "confidence": round(confidence, 4)
        })

    except Exception as e:
        return jsonify({"error": str(e)}), 500


if __name__ == "__main__":
    port = int(os.environ.get("PORT", 8088))
    app.run(host="0.0.0.0", port=port)
