import concurrent.futures
import requests
from collections import Counter

URL = "http://127.0.0.1:8080/api/ping"
TOTAL_REQUESTS = 50
CONCURRENCY = 20

def fire():
    try:
        r = requests.get(URL, timeout=5)
        return r.status_code
    except Exception as e:
        return f"error: {e}"

results = []
with concurrent.futures.ThreadPoolExecutor(max_workers=CONCURRENCY) as executor:
    futures = [executor.submit(fire) for _ in range(TOTAL_REQUESTS)]
    for future in concurrent.futures.as_completed(futures):
        results.append(future.result())

counts = Counter(results)
print(f"Total requests fired: {TOTAL_REQUESTS}")
print(f"Status code breakdown: {dict(counts)}")
print(f"200 (allowed) count: {counts.get(200, 0)}")
print(f"429 (blocked) count: {counts.get(429, 0)}")