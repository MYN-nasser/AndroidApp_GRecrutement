// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
}
dependencies {
    // Core Android KTX (si vous utilisez Java, c'est toujours utile pour les extensions)
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.6.1")

    // Material Design (pour une UI de haut niveau : CardView, TextInputLayout, Bottom Navigation)
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // --- Composants Android Jetpack pour l'Architecture MVVM ---

    // ViewModel et LiveData (Gestion du cycle de vie et données observées)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.0")

    // Navigation Component (Gestion des fragments et navigation)
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.navigation:navigation-ui:2.7.7")

    // (Optionnel mais recommandé pour les appels API : Retrofit)
    // implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    // implementation 'com.squareup.retrofit2:converter-gson:2.9.0'

    // Tests
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}