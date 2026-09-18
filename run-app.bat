@echo off
title FoodFlow - College Mess Management System
echo Launching FoodFlow JavaFX Desktop Application...

powershell -ExecutionPolicy Bypass -Command "$jars = (Get-ChildItem -Path '%USERPROFILE%\.m2\repository' -Filter '*.jar' -Recurse | Select-Object -ExpandProperty FullName) -join ';'; $cp = 'target\classes;' + $jars; Start-Process java -ArgumentList ('-cp', $cp, 'com.foodflow.Main')"

echo Application window launched on your desktop!
