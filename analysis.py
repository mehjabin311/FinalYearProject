# -*- coding: utf-8 -*-
import os

import tensorflow as tf

print(tf.__version__)

classifierLoad = tf.keras.models.load_model('fruit_model.h5')
main_dir = 'Fruit/'
import numpy as np

sub_dir = ["Anthracnose","Bacterial_Blight","Fruit_Borer","Fruit_Spot","Non_Diseased"]


def confusion_matrix_fruit():
    total_images = 0
    correct_prediction = 0
    actual_values=[]
    predicted_values=[]
    for dir_ in sub_dir:
        for image_ in os.listdir(main_dir + dir_ + "/"):
            if "Thumbs.db" not in image_:
                total_images += 1
                test_image = tf.keras.utils.load_img(main_dir + dir_ + "/" + image_, target_size=(200, 200))
                test_image = np.expand_dims(test_image, axis=0)
                result = classifierLoad.predict(test_image)
                if "Anthracnose" in dir_ + "/" + image_:
                    actual_values.append(0)
                    print(0)
                elif "Bacterial_Blight" in dir_ + "/" + image_:
                    actual_values.append(1)
                    print(1)
                elif "Fruit_Borer" in dir_ + "/" + image_:
                    actual_values.append(2)
                    print(2)
                elif "Fruit_Spot" in dir_ + "/" + image_:
                    actual_values.append(3)
                    print(3)
                elif "Non_Diseased" in dir_ + "/" + image_:
                    actual_values.append(4)
                    print(4)
                
                if result[0][0] == 1:
                    predicted_values.append(0)
                    print(0)
                elif result[0][1] == 1:
                    predicted_values.append(1)
                    print(1)
                elif result[0][2] == 1:
                    print(2)
                    predicted_values.append(2)
                elif result[0][3] == 1:
                    predicted_values.append(3)
                    print(3)
                elif result[0][4] == 1:
                    print(4)
                    predicted_values.append(4)
    return actual_values,predicted_values

