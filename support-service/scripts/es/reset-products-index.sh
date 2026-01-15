#!/bin/bash
set -e

ES_HOST="${ES_HOST:-http://localhost:9200}"
INDEX_NAME="${INDEX_NAME:-products}"
MAPPING_FILE="$(cd "$(dirname "$0")" && pwd)/products-mapping.json"

echo "⚠️  This will DELETE and recreate the index: $INDEX_NAME"
read -p "Are you sure? (y/N): " CONFIRM

if [ "$CONFIRM" != "y" ]; then
  echo "❌ Aborted."
  exit 0
fi

echo "🔗 Checking Elasticsearch..."
curl -s "$ES_HOST" > /dev/null || {
  echo "❌ Cannot connect to Elasticsearch at $ES_HOST"
  exit 1
}

echo "🗑️  Deleting index '$INDEX_NAME'..."
curl -s -X DELETE "$ES_HOST/$INDEX_NAME" || true
echo ""

echo "🚀 Recreating index '$INDEX_NAME'..."
curl -s -X PUT "$ES_HOST/$INDEX_NAME" \
  -H "Content-Type: application/json" \
  --data-binary @"$MAPPING_FILE" | cat

echo ""
echo "🔍 Verifying settings..."
curl -s "$ES_HOST/$INDEX_NAME/_settings?pretty" | cat
echo ""
curl -s "$ES_HOST/$INDEX_NAME/_mapping?pretty" | cat

echo ""
echo "🎉 Index '$INDEX_NAME' reset completed."
