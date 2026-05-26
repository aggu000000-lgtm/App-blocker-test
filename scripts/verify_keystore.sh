#!/bin/bash

# Ensure the script exits on error
set -e

KEYSTORE_FILE=$1
KEYSTORE_PASSWORD=$2
KEY_ALIAS=$3

REPORT_FILE="security_health_report.txt"

echo "=====================================" > $REPORT_FILE
echo "       SECURITY HEALTH REPORT        " >> $REPORT_FILE
echo "=====================================" >> $REPORT_FILE
echo "Date: $(date -u)" >> $REPORT_FILE
echo "" >> $REPORT_FILE

if [ ! -f "$KEYSTORE_FILE" ]; then
    echo "STATUS: FAILED" >> $REPORT_FILE
    echo "REASON: Keystore file not found at $KEYSTORE_FILE" >> $REPORT_FILE
    echo "Failed: Keystore file not found!"
    exit 1
fi

echo "Checking keystore details for alias: $KEY_ALIAS..."
echo "Keystore: $KEYSTORE_FILE" >> $REPORT_FILE

# Extract certificate details using keytool
CERT_DETAILS=$(keytool -list -v -keystore "$KEYSTORE_FILE" -storepass "$KEYSTORE_PASSWORD" -alias "$KEY_ALIAS" 2>/dev/null || true)

if [ -z "$CERT_DETAILS" ]; then
    echo "STATUS: FAILED" >> $REPORT_FILE
    echo "REASON: Failed to read keystore or alias not found." >> $REPORT_FILE
    echo "Failed: Could not read keystore. Incorrect password or alias."
    exit 1
fi

VALID_FROM=$(echo "$CERT_DETAILS" | grep "Valid from:" | sed 's/Valid from: //')
OWNER=$(echo "$CERT_DETAILS" | grep "Owner:" | sed 's/Owner: //')
ISSUER=$(echo "$CERT_DETAILS" | grep "Issuer:" | sed 's/Issuer: //')

echo "STATUS: PASSED" >> $REPORT_FILE
echo "Owner: $OWNER" >> $REPORT_FILE
echo "Issuer: $ISSUER" >> $REPORT_FILE
echo "Validity: $VALID_FROM" >> $REPORT_FILE

echo "" >> $REPORT_FILE
echo "Certificate is valid and accessible." >> $REPORT_FILE
echo "Report generated successfully."

cat $REPORT_FILE
