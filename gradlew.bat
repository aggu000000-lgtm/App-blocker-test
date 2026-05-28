@rem
@setlocal
set JAVA_EXE=java.exe
%JAVA_EXE% -cp "gradle\wrapper\gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain %*
