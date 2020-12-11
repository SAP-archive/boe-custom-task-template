# use your BOE installation folder
set SCRIPT="c:\Program Files (x86)\SAP BusinessObjects\SAP BusinessObjects Enterprise XI 4.0\win64_x64\scripts\lcm_cli.bat"

call %SCRIPT% -lcmproperty "%~dp0\CustomTaskSamples.export.properties" >>export2LCMBIAR.txt
pause