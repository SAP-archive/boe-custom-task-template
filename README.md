# Automation Framework - Custom Task Template samples

## Description
SAP BusinessObjects BI [Automation Framework](https://help.sap.com/viewer/55dcdd714f614d1ca77af92b79ff5e44/4.2.5/en-US) allows you to build, manage and run `Scenarios`, from simple to complex operation tasks of your BI platform. 
`Standard Task Templates` can be combined to build scenarios like WebI change source (unv to unx), Refresh WebI documents or Change Document ownership. 
The [*Custom Task Template Java SDK*](https://blogs.sap.com/2017/12/08/how-to-create-a-custom-task-template-for-bi-automation-framework) provides an API to develop additional tasks templates which will be deployed as plugins to your BI installation.

 * Learn -  how `Custom Task Templates` can be used to add functionality to Automation Framework `Scenarios` and simplify the administration of your BI landscapes.
   The project contains content to demonstrate use cases from different areas and can also be used as **Quick Start** for the development of new task templates. 
 * Try - deploy and run the samples **on your BI platform**, get excited about the capabilities and inspired to build your own solutions. 

The Custom Task Template `JavaScript` will introduce another **concept** to add functionality to task templates without the need to deploy plugins to your system. JavaScript samples will showcase the capabilities.

## Table of Contents
[List of samples](#samples)  
[Requirements](#requirements)  
[Installation & Configuration](#installation)  
[Learning & Development](#learning)  
[Known Issues](#knownissues)  
[Support](#support)  
[License](#license)  

<a name='samples'>

## List of samples

  Scenario          | Description
  ----------------- | -------------------------------------------------------------------------------------------------
  JavaScript-BasicTemplate | Query a list of Users and generate Scenario results using JavaScript
  JavaScript-AddWebiSamplesToCategory  |  Query a list of Web Intelligence documents and add them to a global cateogory  
  CompareWebiDatasets | Compare the data from a table in a Web Intelligence document (before and after refresh)  
  CompareRef_WebiDataset | Compare data from a CSV File with data from table in a Web Intelligence document  
  


<a name='requirements'>

## Requirements
* Automation Framework, introduced with BI Administration Console of SAP BI 4.2 SP05, installed and running 

<a name='installation'>

## Installation & Configuration
Clone or download the sample project and follow the instructions in [deploy](deploy/README.md).

<a name='learning'>

## Learning & Development
* Fast and easy start in using the deployed samples, learn more about the features & content in [learning](learning/README.md)  
* Additional details and the source code of the samples can be found in [source-java](source-java/README.md) and [source-javascript](source-javascript/README.md)  


<a name='knownissues'>

## Known Issues
None


<a name='support'>

## Support
This project is provided "as-is": there is no guarantee that raised issues will be answered or addressed in future releases.

<a name='license'>

## License
Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved. This project is licensed under the SAP Sample Code License, Version 1.0 except as noted otherwise in the LICENSE file.
