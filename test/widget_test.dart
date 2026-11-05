import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

import 'package:crop_recommendation/main.dart';

void main() {
  testWidgets('App smoke test', (WidgetTester tester) async {
    // Build our app and trigger a frame.
    await tester.pumpWidget(const CropRecommendationApp());

    // Verify loading screen or login screen appears.
    expect(find.byType(MaterialApp), findsOneWidget);
  });
}
