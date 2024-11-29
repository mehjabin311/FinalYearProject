# -*- coding: utf-8 -*-
import os

import tensorflow as tf
import warnings

warnings.filterwarnings("ignore")
fruit_classifier = tf.keras.models.load_model('fruit_model.h5')
leaf_classifier = tf.keras.models.load_model('leaf_model.h5')
import numpy as np
from keras.utils import load_img
from keras.preprocessing import image


def predict(img_, selection):
    # 0 fruit
    if selection == 0:
        test_image =load_img(img_, target_size=(200, 200))
        test_image = np.expand_dims(test_image, axis=0)
        result = fruit_classifier.predict(test_image)
        print(result[0])
        maxpos = list(result[0]).index(max(list(result[0])))
        print(maxpos)
        if maxpos == 0:
            return 0
        elif maxpos == 1:
            return 1
        elif maxpos == 2:
            return 2
        elif maxpos == 3:
            return 3
        elif maxpos == 4:
            return 4
    else:
        test_image = load_img(img_, target_size=(200, 200))
        test_image = np.expand_dims(test_image, axis=0)
        result = leaf_classifier.predict(test_image)
        maxpos = list(result[0]).index(max(list(result[0])))
        print(maxpos)
        if maxpos == 0:
            return 0
        elif maxpos == 1:
            return 1
        elif maxpos == 2:
            return 2
        elif maxpos == 3:
            return 3
