# Digital Card Directory

## Introduction

Digital Card Directory is a mobile application developed for Android devices to manage business card/visiting cards in a mobile application. It provides following functionalities:
- Allows users to Signup and Login into the mobile Application.
-	The user can click business card image through camera or device gallery(Photos) and upload that on server for further text extraction and Entity recognition.
- The mobile application will store all the extracted details of that card in the SQLite database enabling the user to access it later which eliminates the manual process of storing and managing the business cards.
-	User can view uploaded card through application and perform operations such as
    1. Place phone call and send mail
    2. View card image
    3. Add Comments
-	Delete already uploaded Cards.
-	Search the card in directory.
-	User can also share the Relevant Business Card among the their connections.

## SDK Build Version

- Compile SDK version – 28  
- Target SDK version – 28  
- Min SDK version - 19  

## Opensource libraries integrated
1. Retrofit – 2.5.0 : Backend APIs were consumed using Retrofit
2. gson-converter – 2.5.0 : It is used to convert API's JSON response
3. Fabric Crashlytics - 2.10.0 : It is used for real-time crash reporting
4. Picasso - 2.7 - It is used to load image from URL

## Demo
[Link](https://youtu.be/NnEHQ-F3Yxk)

## Application Flow Diagram
![flow_diagram](https://user-images.githubusercontent.com/42704669/63615339-fa35aa80-c599-11e9-93ad-986f9b2db805.png)

## Architecture Diagram
![arch_diagram](https://user-images.githubusercontent.com/42704669/63615411-218c7780-c59a-11e9-9b6a-feb165ef5728.png)

## Screenshots

#### Launch Screen
![1 copy](https://user-images.githubusercontent.com/42704669/63615760-f0607700-c59a-11e9-8599-59e7a5efd424.png)

#### SignUp Screen
![2 copy](https://user-images.githubusercontent.com/42704669/63615796-05d5a100-c59b-11e9-95e5-47645c4c118f.png)

#### Home Screen
![4 copy](https://user-images.githubusercontent.com/42704669/63616022-80062580-c59b-11e9-9513-0766de5bb1da.png)

#### Card List
![3 copy](https://user-images.githubusercontent.com/42704669/63616021-80062580-c59b-11e9-8ce6-08058c6d3b5f.png)

#### Add Card
![6 copy](https://user-images.githubusercontent.com/42704669/63616023-809ebc00-c59b-11e9-8380-a876f898504c.png)

#### Automatically scanned card details.
![7 copy](https://user-images.githubusercontent.com/42704669/63616024-809ebc00-c59b-11e9-835f-6bee10f7031e.png)

#### Card Detail - Call/Email contact
![9 copy](https://user-images.githubusercontent.com/42704669/63616025-809ebc00-c59b-11e9-8b2a-dce95422c971.png)

#### Add/Update Notes
![11 copy](https://user-images.githubusercontent.com/42704669/63616026-809ebc00-c59b-11e9-95ef-3d06c1ba3cbb.png)

#### Refer card via E-mail
![12 copy](https://user-images.githubusercontent.com/42704669/63616027-809ebc00-c59b-11e9-8130-1d92636e63ad.png)
