import pathlib 
text=pathlib.Path('app/src/main/java/com/app/dreamiaselite/ui/screen/screens/tests/TestsScreen.kt').read_text() 
bad=False 
for ch in text: 
 if ch=='{': bal+= 
 elif ch=='}': bal-= 
