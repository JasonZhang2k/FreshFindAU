以下是正常的安装使用流程，但是删除了google-services所以并没有谷歌服务，并且代码中并未包含数据信息，所以如果想正常使用软件，使用firebase提供服务，线上上传数据信息，然后根据数据修改代码中的数据结构。比如SearchResultActivity这个文件中的
private void queryAndLaunchDetailActivity(String foodName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String[] collections = {"fruit", "vegetable", "nut"};
这里的{"fruit", "vegetable", "nut"}就是预设的食物信息集，以及其他位置一些关于里面具体信息的调用的名字都需要根据你要使用的数据名进行一定的修改。

1. Preparatory Work
Make sure that the computer has Android Studio and the Java Development Kit (JDK) installed. 
Use Android Studio as your development environment, program in Java, and utilize API 29 as the virtual machine version for development.

2. Importing the Project
Open up Android Studio.
Select "Open an Existing Android Studio Project."
Navigate to the project folder and select it.

3. Configuring the Environment
Ensure that the build.gradle (module-level) and build.gradle (project-level) files are matched accordingly.
Make sure the AndroidManifest.xml file is aligned.

4. Compilation and Deployment
Choose the "Build" menu, then select "Rebuild Project" to compile the project.
If the compilation is successful, you can choose the "Run" menu and then select "Run 'app'" to run the project on a simulator or connected device.
To deploy the application, select the "Build" menu, then choose "Build Bundle(s) / APK(s)" and follow the prompts to generate an APK file.
The generated APK file can be directly installed on an Android device.
