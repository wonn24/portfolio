This document contains several requirements of the software (a.k.a., Project CODE-X) and sample codes are also uploaded in the repository. 



## 1. Objective
No content


## 2. Software Description
### 2.1. Project Need
EDC is a cloud-based SaaS (Software as a Service) application, sometimes referred to as on-demand software, requires no software installation, and can be accessed anytime, anywhere. It is built to help users such as **, and ** enhance their research workflow during a clinical study and is compliant with industry regulations such as ** and **.

### 2.2	Users
User roles in EDC can be grouped based on organizations, and represented as follows:
- GroupA: User Administrator, **, **, **, Quality Management
- GroupB: **, **, **, **
- GroupC: **
- GroupD: **, **
- GroupE: **, **

Detailed user roles are described below.
1)	User Administrator (Super User): Responsible for creating and managing ** accounts and has read-only access to ** data.
2)	**: Performs study build activities (configure study parameters), has read-only access to ** data, manages user roles, ..., exports ** report in Excel format, opens and closes manual queries, reviews ** data, reviews queries, requests and finalizes DB **, downloads audit log, and generates ** report in PDF format.
3)	**: Have read-only access to ** data including audit trails, **, review and perform **.
4)	**: Have read-only access to ** data including audit trails, review and perform **.
5)	**: Responsible for monitoring ** data, has read-only access to ** data, reviews ** data, opens and closes manual queries, performs ** to verify whether the data entry is complete if required, and more.
6)	**: Has read-only access to ** data, downloads ** report, and more.
7)	**: Responsible for overseeing subject enrollment, data entry on **, reviews and responds to open queries, ..., confirms **.
8)	**: Responsible for enrolling subjects into EDC, performs data entry on **, reads and responds to open queries, inactivates/reactivates subjects **
9)	**: Responsible for checking data quality, **, performs data entry on **, reads and responds to open queries, and inactivates/reactivates subjects **.
10)	**: Enrolls subjects into EDC; performs data entry on **, reads and responds to open queries.
11)	**: Responsible for monitoring ** data, reviews **, opens and closes manual queries, performs ** to verify whether the data entry is complete. Has read-only access to ** data and verifies data when the review is complete.
12)	**: Have read-only access to ** data including audit trails, downloads audit log.
13)	**: Have read-only access to assigned study only. 
14)	**: Responsible to monitor **, ..., opens and closes manual queries.
15)	**: Responsible to manage **, uploading/downloading **, responds manual queries.

### 2.3	Overview of the software
The major functionalities supported by the system are detailed below:
1) Design: All questions in ** can be set for specific **. Also, related parameters such as ** and ** can be defined at the ** initiation phase.
2) ** data entry and validation: Electronic data collected from ** subjects during ** visit is entered in the EDC. The software verifies that the data entered the validated system, in which data is secure and traceable, complies with the validation checks. All changes and corrections are trailed.
3) Manage: Respective users can raise manual queries, review ** queries, and close the queries. Users can review collected ** data and verify whether the data matches with ** documents.
4) Report: Study-related and ** data can be extracted and downloaded for data analysis. 

### 2.4	Interfaces
EDC supports the following browsers and versions:
-	Apple Safari 5.1+
-	Google Chrome 16+
-	Mozilla Firefox 10+
-	Microsoft IE 11+ / Edge

Google Chrome is recommended for widescreen displays, and default mobile browsers must allow popups so that user’s camera can be accessed.

### 2.5 Overall process
The overall process of the software can be represented in six phases. Firstly, users shall be registered in the software with their e-mail addresses and their roles will be assigned properly by personnel who have the permission ‘Role assign’. The User Administrator generally assigns a registered user to the role ** in this phase. Once ** is assigned, ** can assign a user to ** roles, such as **, **, ** etc. In the next phase, ** can configure a study. In this phase, ** can set ** variables that should be used in the targeted clinical study. Also, ** parameters such as **, **, **, and so on is set in this phase. After ** parameters are set, subjects can be enrolled in the software. After a subject is enrolled in the software, the data entry for ** can be conducted. In this step, entered data is validated, related queries are generated and resolved (see more details below). Lastly, users can check several reports that show metrics of ** status, opened/closed queries. After the completion of final data validation, the study closure process will be conducted. The data archiving will be done by **.
More specifically, the EDC module that covers **, ... can be detailed as follows:

The overall process of the data entry can be detailed as follows:
1) Entry / Edit: ...
2) Auto-validation: ...
3) Save: ...
4) SDV (Source Data Verification): ...
5) Review: ...

DB Lock process can be described as follows:
1) Request: ...
2) Review: ...
3) Confirm: ...
	
The overall process of the Query Management can be described as follows:
1) Open: ...
2) Respond: ...
3) Close: ...
4) Review: ...
5) Inactivate study: ...
6) Data archiving: ...


## 3	Project Requirements
### 3.1	Functional Requirements
Note: ** users indicate **, **, **, and **.

#### 3.1.1	User Access
- UR01:	All users can access a registration page to create their accounts.
- UR02:	All users can stay in the system for a pre-defined period and extend the period if necessary.
- UR03:	All users registered in the system has no permission at all as a default.
- UR04:	** should be able to provide access to a user to be a ** only.
- UR05:	** should be able to revoke access of **.
- UR06:	** should be able to provide access to all users except ** and **.
- UR07:	** should be able to revoke access of all users except ** and **.
- UR08:	Users should be able to change their own user information (e.g., first name and last name) in the system.
- UR09:	Users should be able to change their passwords in the system.
- UR10:	Users should receive an e-mail with a temporary link for setting their initial or forgotten passwords.
- UR11:	Users should be able to check their last logged in time.

#### 3.1.2 Study Creation
- UR12:	** should be able to create a study and set related parameters.
- UR13:	** should be able to set user roles in a study build page.
- UR14:	** should be able to set ** in a build page.
- UR15:	** should be able to set/define relational error rules among questions in a study build page.
- UR16:	** should be able to set ** parameters in a build page.

#### 3.1.3 Data Entry
- UR17:	Users who have the permission ** should be able to enroll subjects.
- UR18:	Users who have the permission ** should be able to enter ** data.
- UR19:	Users who have the permission ** should be able to be informed that a field is missing or out of range based on the error rules via a special icon after saving the form.
- UR20:	Users who have the permission ** and ** should be able to in-/re-activate subjects.
- UR21:	Users who have the permissions **, **, and **, should be able to receive notification messages when an activity related with a ** is taken.

#### 3.1.4 Query Management
- UR22:	System should be able to open queries automatically based on pre-defined error rules.
- UR23:	System should be able to avoid generating duplicated auto queries even though modified data still meets one of error rules.
- UR24:	A user who has the permission ** should be able to open queries manually.
- UR25:	A user who has the permission ** should be able to respond queries.
- UR26:	A user who has the permission ** should be able to review queries.
- UR27:	A user who has the permission ** should be able to close queries.

#### 3.1.5 Review and SDV
- UR28:	A user who has the permission ** should be able to review data and flag a question as ** or **.
- UR29:	A user who has the permission ** should be able to perform SDV and flag a question as ** or **.
- UR30:	Flags reviewed and verified will be set as ** and ** respectively when any data change happens.
- UR31:	A user who has the permission ** or ** should be able to receive a notification message when any of verified/reviewed answers is modified.
- UR32:	A user who has the permission ** or ** should be able to verify or review CRF fields until **.

#### 3.1.6 Electronic Signature
- UR33:	System should allow proper users to perform e-signature.

#### 3.1.7 Database Lock and Unlock
- UR34:	....
- UR35:	...
- UR36:	...
- UR37:	...
- UR38:	...
- UR39:	...

#### 3.1.8 Inactivate and Reactivate 
- UR40:	...

#### 3.1.9 Audit Trail
- UR41:	...

#### 3.1.10 Export / Download
- UR42:	System should provide a function to export ... PDF.
- UR43:	System should provide a function to export ... PDF.
- UR44:	System should provide a function to download ... history.
- UR45:	System should provide a function to download ... records.
- UR46:	...

#### 3.1.11 Import / Upload
- UR47:	System should provide a function to import a bulk of ... in ...
- UR48:	System should provide a function to upload ...

### 3.2 Non-functional Requirements
- To follow industry regulations and manage software development processes and quality controls, three database servers (i.e., development server, validation server, and production server) and three app services connected respectively with each of the database servers are established.

#### 3.2.1 Performance
- The software should respond to any transactions within ** seconds on average to local users (or remote users respectively) where equal or less than *** users are accessing.

#### 3.2.2 Recoverability
- The latest version of the software will be running on **, and every version of the code will be stored in a stable repository such as GitHub for recovery purpose. 
- The latest version will be restored within 24 hours from the repository in case of system crashes (e.g., an operating system stops functioning properly), hardware failures or other similar problems. This restoration task will be performed manually by proper authorized personnel in the production server.

#### 3.2.3 Maintainability
- Database server should be rebooted at least once a **. This is to maintain and refresh computer resources and download/install Windows updates/hot fixes, if necessary. All copies of deployed versions should be stored and managed by **.

#### 3.2.4 Serviceability
- The software should be accessible 24/7 on both webhosting and database servers.

#### 3.2.5 Security
The security aspects of software and servers should be managed by **. All expected accesses will be handled by the software itself, but any unexpected attacks or unauthorized access attempts should be identified/defended by **.

### 3.3 Database Requirements
All study-related data as well as functional/flagging markups and records in the production server must be backed up daily. This backup is maintained until the study ends or until the expiry date agreed upon by the sponsor and PI at the study initiation phase.

### 3.4 Security Requirements
No content

### 3.5 Regulatory Requirements
- Weekly reviews of audit logs, user access logs should be performed, but they can also be done bi-weekly/monthly/bi-monthly based on agreements between ** and **.
- Training users regarding the system usage/maintenance should be done before they get access to the system.
- Any user password must be encrypted which means not readable for human and should be accepted for 90 days. 

### 3.6 Usability Requirements
No content



{END of the document}
