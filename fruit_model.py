# -*- coding: utf-8 -*-

# Part 1 - Building the CNN

batch_size = 16  # 32
import tensorflow
import keras
print(tensorflow.__version__)
print(keras.__version__)
from tensorflow.keras.preprocessing.image import ImageDataGenerator

# All images will be rescaled by 1./255
train_datagen = ImageDataGenerator(rescale=1 / 255)

# Flow training images in batches of 128 using train_datagen generator
train_generator = train_datagen.flow_from_directory(
    'Fruit/',  # This is the source directory for training images
    target_size=(200, 200),  # All images will be resized to 200 x 200
    batch_size=batch_size,
    # Specify the classes explicitly
    classes=["Anthracnose","Bacterial_Blight","Fruit_Borer","Fruit_Spot","Non_Diseased"],
    # Since we use categorical_crossentropy loss, we need categorical labels
    class_mode='categorical')

import tensorflow as tf

model = tf.keras.models.Sequential([
    # Note the input shape is the desired size of the image 200x 200 with 3 bytes color
    # The first convolution
    tf.keras.layers.Conv2D(16, (3, 3), activation='relu', input_shape=(200, 200, 3)),
    tf.keras.layers.MaxPooling2D(2, 2),
    # The second convolution
    tf.keras.layers.Conv2D(32, (3, 3), activation='relu'),
    tf.keras.layers.MaxPooling2D(2, 2),
    # The third convolution
    tf.keras.layers.Conv2D(64, (3, 3), activation='relu'),
    tf.keras.layers.MaxPooling2D(2, 2),
    # The fourth convolution
    tf.keras.layers.Conv2D(64, (3, 3), activation='relu'),
    tf.keras.layers.MaxPooling2D(2, 2),
    # The fifth convolution
    tf.keras.layers.Conv2D(64, (3, 3), activation='relu'),
    tf.keras.layers.MaxPooling2D(2, 2),
    # Flatten the results to feed into a dense layer
    tf.keras.layers.Flatten(),
    # 128 neuron in the fully-connected layer
    tf.keras.layers.Dense(128, activation='relu'),
    # 5 output neurons for 5 classes with the softmax activation
    tf.keras.layers.Dense(5, activation='softmax')
])

model.summary()

from tensorflow.keras.optimizers import RMSprop

model.compile(loss='categorical_crossentropy',
              optimizer=RMSprop(lr=0.001),
              metrics=['acc'])

total_sample = train_generator.n

n_epochs = 38  # 32
epoch_array = []
history = model.fit_generator(
    train_generator,
    steps_per_epoch=int(total_sample / batch_size),
    epochs=n_epochs,
    verbose=1)

print(model.history.history['loss'])
print(model.history.history['acc'])

import matplotlib.pyplot as plt

epoch_array = []
for i in range(n_epochs):
    epoch_array.append(i)

loss = model.history.history['loss']
acc = model.history.history['acc']

plt.plot(epoch_array, loss, "-o", label="Epoch")
plt.plot(loss, epoch_array, "-o", label="Loss")
plt.legend()
plt.show()

# acc=model.history.history['acc']
plt.plot(epoch_array, acc, "-o", label="Epoch")
plt.plot(acc, epoch_array, "-o", label="Accuraccy")
plt.legend()
plt.show()

model.save('fruit_model.h5')
