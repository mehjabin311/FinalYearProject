#!/usr/bin/env python
import json
from PIL import Image
from flask import Flask, request
import os
import predict
import base64

language = 'en'
app = Flask(__name__)
path = "/"


@app.route('/detectDisease', methods=['GET', 'POST'])
def detectDisease():
    imageData = request.form['imageData']
    choice= request.form['selection']
    imgdata = base64.b64decode(imageData)
    path1 = 'C:/Users/MEHJABIN/Desktop/Pomegranate Leaf Fruit Disease Prediction'
    if not os.path.exists(path1):
        os.makedirs(path1)
    filename = path1 + "/test_image.jpg"
    with open(filename, 'wb') as f:
        f.write(imgdata)

    number = predict.predict(filename,choice)
    x = {"value":number}
    print(x)
    return json.dumps(x)



if __name__ == '__main__':
    app.run(host='0.0.0.0', threaded=True)
