#!/usr/bin/env python3
"""Tiny HTTP server that triggers desktop notifications on the host via D-Bus."""

from http.server import HTTPServer, BaseHTTPRequestHandler
import subprocess
import json


class NotifyHandler(BaseHTTPRequestHandler):
    def do_POST(self):
        length = int(self.headers.get("Content-Length", 0))
        body = self.rfile.read(length).decode() if length else ""

        title = "Claude Code"
        message = "A terminé de réfléchir"
        try:
            data = json.loads(body) if body else {}
            title = data.get("title", title)
            message = data.get("message", message)
        except json.JSONDecodeError:
            pass

        subprocess.run(["notify-send", title, message], timeout=5)
        subprocess.run(["canberra-gtk-play", "-i", "message-new-instant"], timeout=5)
        self.send_response(200)
        self.end_headers()
        self.wfile.write(b"ok")

    def log_message(self, format, *args):
        print(f"[notifier] {args[0]}")


if __name__ == "__main__":
    server = HTTPServer(("0.0.0.0", 8787), NotifyHandler)
    print("[notifier] Listening on :8787")
    server.serve_forever()
