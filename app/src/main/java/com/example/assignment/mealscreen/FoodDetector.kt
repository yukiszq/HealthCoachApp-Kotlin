package com.example.assignment.mealscreen

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
class FoodDetector {

    // Creates a Google ML Kit Image Labeler to recognise objects in images
    private val labeler = ImageLabeling.getClient(
        ImageLabelerOptions.DEFAULT_OPTIONS
    )

    fun detectFood(
        context: Context,
        imageUri: Uri,
        onResult: (String, Float) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            // Converts the selected image Uri into ML Kit's InputImage format
            val image = InputImage.fromFilePath(context, imageUri)
            // Sends the image to Google ML Kit for image detection
            labeler.process(image)
                // "labels" contains the detected labels and their confidence scores
                .addOnSuccessListener { labels ->

                    if (labels.isNotEmpty()) {

                        val knownFoods = listOf(
                            "fruit",
                            "vegetable",
                            "banana",
                            "apple",
                            "orange",
                            "pizza",
                            "burger",
                            "chicken",
                            "fried chicken",
                            "rice",
                            "fried rice",
                            "noodle",
                            "pasta",
                            "sandwich",
                            "bread",
                            "salad",
                            "cake",
                            "ice cream",
                            "egg",
                            "fish",
                            "soup"
                        )

                        val specificFood = labels
                            .filter { label ->

                                val text = label.text.lowercase()
                                // Accepts a specific food label only when ML Kit confidence is at least 50%
                                label.confidence >= 0.50f &&
                                        knownFoods.any { food ->
                                            // Checks whether the detected label contains the food keyword
                                            text.contains(food)
                                        }
                            }
                            // Selects the matching food label with the highest confidence score
                            .maxByOrNull {
                                it.confidence
                            }
                        // accepts a general "Food" label only when confidence is at least 70%
                        val generalFood = labels
                            .filter { label ->

                                val text = label.text.lowercase()

                                text.contains("food") &&
                                        label.confidence >= 0.70f
                            }
                            .maxByOrNull {
                                it.confidence
                            }
                        // If none is found it use the general food label which is "Food"
                        val selectedLabel = specificFood ?: generalFood

                        if (selectedLabel != null) {

                            onResult(
                                selectedLabel.text,
                                selectedLabel.confidence
                            )

                        } else {

                            onResult(
                                "Not Food",
                                0f
                            )
                        }

                    } else {

                        onResult(
                            "Not Food",
                            0f
                        )
                    }
                }
                .addOnFailureListener {
                    onError("Detection failed")
                }

        } catch (e: Exception) {
            onError("Unable to read image")
        }
    }
}