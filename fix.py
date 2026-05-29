import re
with open('app/src/main/java/com/example/appblocker/ui/foundation/BloomIndication.kt', 'r') as f:
    content = f.read()

# remove extra }
content = content.replace("    override fun hashCode(): Int = -1\n}\n}", "    override fun hashCode(): Int = -1\n}")

with open('app/src/main/java/com/example/appblocker/ui/foundation/BloomIndication.kt', 'w') as f:
    f.write(content)
