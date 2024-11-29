import base64
import os
from PIL import Image
from flask import Flask
from flask.testing import FlaskClient
import pytest
from unittest.mock import patch

import predict


@pytest.fixture
def app():
    app = Flask(__name__)
    app.config['TESTING'] = True
    return app


@pytest.fixture
def client(app):
    with app.test_client() as client:
        yield client


@pytest.fixture
def image_data():
    with open('test_image.jpg', 'rb') as image_file:
        image_bytes = image_file.read()
        image_data = base64.b64encode(image_bytes).decode('utf-8')
    return image_data


@pytest.fixture
def create_temp_image_file(image_data):
    path = "/tmp"
    if not os.path.exists(path):
        os.makedirs(path)
    filename = os.path.join(path, "test_image.jpg")
    with open(filename, 'wb') as f:
        f.write(base64.b64decode(image_data))
    yield filename
    os.remove(filename)


@pytest.fixture
def mocked_predict():
    with patch('predict.predict') as mock:
        mock.return_value = 'disease_name'
        yield mock


def test_detect_disease_endpoint_returns_name(client, create_temp_image_file, mocked_predict):
    response = client.post('/detectDisease', data={'imageData': 'base64encodedimagedata'})
    assert response.status_code == 200
    assert response.data.decode() == 'disease_name'


def test_detect_disease_endpoint_saves_image(create_temp_image_file):
    assert os.path.exists('test_image.jpg')


if __name__ == '__main__':
    pytest.main()
