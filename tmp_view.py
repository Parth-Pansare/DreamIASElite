import pathlib 
data=pathlib.Path('app/src/main/java/com/app/dreamiaselite/ui/screen/screens/tests/TestsScreen.kt').read_text().splitlines() 
start,end=148,170 
print('\n'.join(f'{i}: {line}' for i,line in enumerate(data[start-1:end], start))) 
