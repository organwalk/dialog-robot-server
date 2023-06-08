import requests


def xeno_nlper(text, suffix="/model/parse"):
    base_url = "http://localhost:5005"
    url = base_url + suffix
    # headers = {'Content-Type': "application/x-www-form-urlencoded"}
    response_content = requests.post(url, json={"text": text}).json()
    response_status = requests.post(url, json={"text": text}).status_code
    # print(response)
    return response_content, response_status
