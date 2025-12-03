import pathlib 
import re 
from collections import Counter 
data=pathlib.Path('app/src/main/java/com/app/dreamiaselite/ui/screen/screens/tests/TestsScreen.kt').read_text() 
words=re.findall(r\"fun\s+(\w+)^\", data) 
print(Counter(words)) 
