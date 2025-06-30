package com.example.christ_international

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import java.util.concurrent.TimeUnit

class TranslatorActivity : AppCompatActivity() {
    private lateinit var sourceLanguageAutoComplete: AutoCompleteTextView
    private lateinit var targetLanguageAutoComplete: AutoCompleteTextView
    private lateinit var sourceText: TextInputEditText
    private lateinit var translatedText: TextView
    private lateinit var translateButton: MaterialButton
    private lateinit var swapLanguagesButton: ImageButton
    private lateinit var translatedTextLabel: TextView
    private lateinit var progressBar: ProgressBar
    private var translator: Translator? = null
    private var isDownloading = false
    private var retryCount = 0
    private val maxRetries = 3

    private val languages = mapOf(
        "English" to "en",
        "Hindi" to "hi",
        "Kannada" to "kn",
        "Malayalam" to "ml",
        "Tamil" to "ta",
        "Telugu" to "te",
        "Bengali" to "bn",
        "French" to "fr",
        "German" to "de",
        "Spanish" to "es",
        "Arabic" to "ar",
        "Chinese" to "zh",
        "Japanese" to "ja",
        "Korean" to "ko",
        "Russian" to "ru",
        "Portuguese" to "pt",
        "Italian" to "it",
        "Urdu" to "ur"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_translator)

        // Set up toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Initialize views
        initializeViews()
        setupLanguageDropdowns()
        setupClickListeners()
    }

    private fun initializeViews() {
        sourceLanguageAutoComplete = findViewById(R.id.sourceLanguageAutoComplete)
        targetLanguageAutoComplete = findViewById(R.id.targetLanguageAutoComplete)
        sourceText = findViewById(R.id.sourceText)
        translatedText = findViewById(R.id.translatedText)
        translateButton = findViewById(R.id.translateButton)
        swapLanguagesButton = findViewById(R.id.swapLanguagesButton)
        translatedTextLabel = findViewById(R.id.translatedTextLabel)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupLanguageDropdowns() {
        val languageAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, languages.keys.toList())
        sourceLanguageAutoComplete.setAdapter(languageAdapter)
        targetLanguageAutoComplete.setAdapter(languageAdapter)

        // Set default languages
        sourceLanguageAutoComplete.setText("English", false)
        targetLanguageAutoComplete.setText("Hindi", false)
    }

    private fun setupClickListeners() {
        swapLanguagesButton.setOnClickListener {
            if (isDownloading) {
                showError("Please wait for the current translation to complete")
                return@setOnClickListener
            }

            val sourceLanguage = sourceLanguageAutoComplete.text.toString()
            val targetLanguage = targetLanguageAutoComplete.text.toString()
            val sourceTextContent = sourceText.text.toString()
            val translatedTextContent = translatedText.text.toString()

            sourceLanguageAutoComplete.setText(targetLanguage, false)
            targetLanguageAutoComplete.setText(sourceLanguage, false)
            
            if (translatedTextContent.isNotEmpty() && sourceTextContent.isNotEmpty()) {
                sourceText.setText(translatedTextContent)
                translatedText.text = ""
            }
        }

        translateButton.setOnClickListener {
            retryCount = 0
            validateAndTranslate()
        }
    }

    private fun validateAndTranslate() {
        if (isDownloading) {
            showError("Translation in progress, please wait")
            return
        }

        val sourceLanguage = sourceLanguageAutoComplete.text.toString()
        val targetLanguage = targetLanguageAutoComplete.text.toString()
        val textToTranslate = sourceText.text.toString()

        when {
            sourceLanguage.isEmpty() || targetLanguage.isEmpty() -> {
                showError("Please select both languages")
                return
            }
            textToTranslate.isEmpty() -> {
                showError("Please enter text to translate")
                return
            }
            sourceLanguage == targetLanguage -> {
                showError("Source and target languages cannot be the same")
                return
            }
        }

        val sourceCode = languages[sourceLanguage]
        val targetCode = languages[targetLanguage]
        if (sourceCode == null || targetCode == null) {
            showError("Selected language is not supported")
            return
        }

        translateText(sourceCode, targetCode, textToTranslate)
    }

    private fun translateText(sourceLanguage: String, targetLanguage: String, text: String) {
        // Show progress and disable controls
        isDownloading = true
        progressBar.visibility = View.VISIBLE
        translateButton.isEnabled = false
        swapLanguagesButton.isEnabled = false
        translatedText.text = "Preparing translator..."

        // Release previous translator
        translator?.close()

        try {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(sourceLanguage)
                .setTargetLanguage(targetLanguage)
                .build()

            translator = Translation.getClient(options)

            // Update translation label
            val targetLanguageName = languages.entries.find { it.value == targetLanguage }?.key ?: targetLanguage
            translatedTextLabel.text = "$targetLanguageName Translation"

            // Set download conditions
            val conditions = DownloadConditions.Builder()
                .requireWifi()
                .build()

            // Download model if needed
            translator!!.downloadModelIfNeeded(conditions)
                .addOnSuccessListener {
                    translatedText.text = "Translating..."
                    // Perform translation
                    translator!!.translate(text)
                        .addOnSuccessListener { translatedString ->
                            translatedText.text = translatedString
                            resetControls()
                        }
                        .addOnFailureListener { exception ->
                            handleTranslationError(exception, sourceLanguage, targetLanguage, text)
                        }
                }
                .addOnFailureListener { exception ->
                    handleTranslationError(exception, sourceLanguage, targetLanguage, text)
                }
        } catch (e: Exception) {
            handleTranslationError(e, sourceLanguage, targetLanguage, text)
        }
    }

    private fun handleTranslationError(exception: Exception, sourceLanguage: String? = null, 
                                     targetLanguage: String? = null, text: String? = null) {
        Log.e("TranslatorActivity", "Translation error: ${exception.localizedMessage}")
        
        if (retryCount < maxRetries && sourceLanguage != null && targetLanguage != null && text != null) {
            retryCount++
            translatedText.text = "Retrying... Attempt $retryCount of $maxRetries"
            // Wait for 2 seconds before retrying
            translateButton.postDelayed({
                translateText(sourceLanguage, targetLanguage, text)
            }, 2000)
        } else {
            showError("Translation failed. Please check your internet connection and try again.")
            resetControls()
        }
    }

    private fun resetControls() {
        isDownloading = false
        progressBar.visibility = View.GONE
        translateButton.isEnabled = true
        swapLanguagesButton.isEnabled = true
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        translator?.close()
    }
}
