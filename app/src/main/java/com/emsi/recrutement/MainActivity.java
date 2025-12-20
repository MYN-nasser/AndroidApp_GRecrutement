// Inside your MainActivity.java, add this method:
private void setupTestNavigation() {
    // Add a test button to navigate to Dashboard
    Button btnTestDashboard = findViewById(R.id.btnTestDashboard); // Add this button in your layout

    if (btnTestDashboard != null) {
        btnTestDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DashboardCandidateActivity.class);
            intent.putExtra("USERNAME", "Ahmed BENANI");
            startActivity(intent);
        });
    }
}

// Call this in onCreate() after setContentView()