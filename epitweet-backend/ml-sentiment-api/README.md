# 📊 ML-Sentiment-API

This is a lightweight API for performing **sentiment analysis** on TinyX posts using a pre-trained Hugging Face model. It classifies text into **positive**, **neutral**, or **negative** sentiments with confidence scores.

---

## 🚀 Getting Started

### 📦 Install Dependencies

```bash
# Install the required Python packages
pip install -r requirements.txt
```

### ▶️ Run the API

```bash
# Start the Flask server
python3 app.py
```

Once running, the API will be available at:

```
http://localhost:8088
```

---

## 📨 Using the API

### 🔗 Endpoint

```
POST /analyse
```

### 📝 Request Body

Send a JSON payload with the content to analyse:

```json
{
  "content": "I love programming!"
}
```

### ✅ Example Curl Request

```bash
curl -X POST http://localhost:8088/analyse \
     -H "Content-Type: application/json" \
     -d '{"content": "I love programming!"}'
```

---

## 📤 Response Format

If successful, the API returns:

```json
{
    "confidence": 0.95,
    "sentiment": "positive",
}
```

- `sentiment`: Predicted sentiment (`positive`, `neutral`, or `negative`)
- `confidence`: Confidence score (between `0` and `1`)

---

## 📚 Model Info

This API uses the [`cardiffnlp/twitter-xlm-roberta-base-sentiment`](https://huggingface.co/cardiffnlp/twitter-xlm-roberta-base-sentiment) model from Hugging Face, designed for multilingual sentiment classification.
