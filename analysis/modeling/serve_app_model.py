"""Optional local HTTP prediction service for the MindTrack app (alternative to on-device inference).

Run from the project root after executing notebooks/Pipeline.ipynb:
    python analysis/modeling/serve_app_model.py
Then POST JSON with every field listed in app/app_input_fields.csv to http://<pc-ip>:8765/predict
    GET /health   -> {"status": "ready", "features": [...]}
    POST /predict -> {"probability": 0.31, "level": "Low", "disclaimer": "..."}
The recommended path is on-device inference with app/mindtrack_model.json + app/MindTrackModel.kt.
"""
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
import json
from pathlib import Path

import joblib
import pandas as pd

ROOT = Path(__file__).resolve().parents[2]
BUNDLE = joblib.load(ROOT / "models" / "mindtrack_rf.joblib")
MODEL, FEATURES, THRESHOLDS = BUNDLE["model"], BUNDLE["features"], BUNDLE["thresholds"]
DISCLAIMER = "Screening estimate from questionnaire answers. Not a medical diagnosis."
HOST, PORT = "0.0.0.0", 8765


class Handler(BaseHTTPRequestHandler):
    def respond(self, status, body):
        data = json.dumps(body).encode("utf-8")
        self.send_response(status)
        self.send_header("Content-Type", "application/json; charset=utf-8")
        self.send_header("Content-Length", str(len(data)))
        self.end_headers()
        self.wfile.write(data)

    def do_GET(self):
        if self.path == "/health":
            self.respond(200, {"status": "ready", "features": FEATURES})
        else:
            self.respond(404, {"error": "Endpoint not found"})

    def do_POST(self):
        if self.path != "/predict":
            self.respond(404, {"error": "Endpoint not found"})
            return
        try:
            body = json.loads(self.rfile.read(int(self.headers.get("Content-Length", 0))) or b"{}")
            missing = [f for f in FEATURES if f not in body]
            if missing:
                self.respond(400, {"error": f"Missing fields: {missing}"})
                return
            row = pd.DataFrame([{f: float(body[f]) for f in FEATURES}])
            p = float(MODEL.predict_proba(row)[0, 1])
            level = "Elevated" if p >= THRESHOLDS["elevated"] else "Low"
            self.respond(200, {"probability": round(p, 4), "level": level, "disclaimer": DISCLAIMER})
        except (ValueError, TypeError) as exc:
            self.respond(400, {"error": str(exc)})


if __name__ == "__main__":
    print(f"MindTrack model service on http://{HOST}:{PORT}  (fields: {len(FEATURES)})")
    ThreadingHTTPServer((HOST, PORT), Handler).serve_forever()
