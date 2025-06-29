#!/bin/bash

echo "Setting up Firestore security rules for test mode..."

# Create firestore.rules file
cat > firestore.rules << 'EOF'
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Allow all read/write operations for testing
    // WARNING: This is NOT secure for production!
    match /{document=**} {
      allow read, write: if true;
    }
  }
}
EOF

echo "Firestore rules file created: firestore.rules"
echo ""
echo "To deploy these rules to your Firebase project:"
echo "1. Install Firebase CLI: npm install -g firebase-tools"
echo "2. Login to Firebase: firebase login"
echo "3. Initialize Firebase in your project: firebase init firestore"
echo "4. Deploy rules: firebase deploy --only firestore:rules"
echo ""
echo "Or you can manually set the rules in the Firebase Console:"
echo "1. Go to https://console.firebase.google.com/project/cashi-3e4bd/firestore/rules"
echo "2. Replace the rules with the content from firestore.rules"
echo "3. Click 'Publish'"
echo ""
echo "⚠️  WARNING: These rules allow anyone to read/write your data!"
echo "   Only use this for development/testing!" 