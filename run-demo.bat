@echo off
title FoodFlow - Programmatic Demo & Syllabus Verification
echo Running FoodFlow Programmatic Verification Runner...

powershell -ExecutionPolicy Bypass -Command "$jars = (Get-ChildItem -Path '%USERPROFILE%\.m2\repository' -Filter '*.jar' -Recurse | Select-Object -ExpandProperty FullName) -join ';'; $cp = 'target\classes;' + $jars; java -cp $cp com.foodflow.DemoRunner"

pause
