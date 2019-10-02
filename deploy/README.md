# Installation & Configuration

Custom Task Template plug-ins must be copied to the Automation Framework folder of your SAP BI platform server.  
The content can be deployed using the the Central Management Console, configuration will be done in the BI Administration Console.

### Table of Contents  
[Preparation](#prepare)  
[Install plug-ins](#install)  
[Deploy content](#deploy)  
[Check content](#check)    
[Configuration](#configuration)  

<a name='prepare'>  

## Preparation  
Clone or download the sample project.
If you download the ZIP, please extract the files.  
You will use the files from folder `..\deploy` in the next steps.

<a name='install'>  

## Install plug-ins  
1. Copy the Custom Task Template plug-ins to your system.  
   **Source**:  `<your folder>\deploy\plugins\*.jar`
   ![](z-images/CTT-plug-ins.png)  
   
   **Target**: `<BOE folder>\AdminConsole\Agent\plugins\task_bundle`
   ![](z-images/plugins-task_bundle.png)  
   
 1. The BI Agent service should use the new task template plug-ins without restart, but we recommend to restart to service.  
    ![](z-images/Restart-BIAgent.png)  
    
<a name='deploy'>  

## Deploy content
Import the content using the Promotion Management of the CMC.
1. Open the CMC  
1. Select Promotion Management  
  
1. Select Menu &gt; Import &gt; Import file - choose Browse  
![](z-images/PM-importFile.png)  

1. Select the sample content file (LCMBIAR) - choose Open - choose Ok  
![](z-images/PM-browseLCMBIAR.png)  

1. Enter your System as Destination - choose Create  
![](z-images/PM-Destination.png)  

1. Select Menu &gt; Promote - choose Promote  
![](z-images/PM-Promote.png)  

<a name='check'>  

## Check content

To check if the import was successful.  
1. Select the Job in the list - choose Menu &gt; History  
The status should be success
![](z-images/PM-JobHistory.png)  

1. From CMC Home or dropdown &gt; select BI Administration Console - choose Task Template - choose Custom  
All available Custom Task Templates will be listed
![](z-images/PM-BIAdminConsole.png)  

1. From CMC Home or dropdown &gt; select Folders - choose SAP-AFW-Samples - choose Custom Scripts  
The JavaScript samples will be listed
![](z-images/PM-FoldersSamples.png)  


<a name='configuration'>  

## Configuration
Set the Landscape configuration in the BI Administration Console.
1. Select Landscape Management  
![](z-images/LandscapeManagment.png)  

1. From the landscape list
![](z-images/LandscapeList.png)  

1. Edit the samples landscape and enter the system parameters
![](z-images/LandscapeEdit.png)  
  
1. Enter the users credentials for the landscape  
![](z-images/LandscapeCredentials.png)  

   The credentials are stored for the user who is currently logged in to the Administration Console.
   If another user wants to execute scenarios, he must also enter the credentials for the landscape.

