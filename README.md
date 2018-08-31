# MagicScreenAdapter
不用写一句代码做好屏幕适配:

本适配方案是用的 今日头条的 适配方案, 界面在不同尺寸屏幕上是等比缩放的;     
预览的时候请使用 1920x1080 的屏幕分辨率预览,预览是什么样在任何设备都是这个样子      

> 如果某个Activity 不想启用适配功能,请打上注解:@IgnoreScreenAdapter   

### 依赖方法:
#### To get a Git project into your build:
#### Step 1. Add the JitPack repository to your build file
1.在全局build里面添加下面github仓库地址
Add it in your root build.gradle at the end of repositories:
```
buildscript {
    ...
    dependencies {
	...
        classpath 'cn.leo.plugin:magic-plugin:1.0.0' //java 用这个
	classpath 'com.hujiang.aspectjx:gradle-android-plugin-aspectjx:2.0.0' //kotlin 用这个
    }
}
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
google()和jcenter()这两个仓库一般是默认的，如果没有请加上

#### Step 2. Add the dependency
2.在app的build里面添加插件和依赖
```
apply plugin: 'cn.leo.plugin.magic' //java 用这个
apply plugin: 'android-aspectjx'  //kotlin 用这个，编译速度会慢点
...
dependencies {
	...
	implementation 'com.github.jarryleo:MagicScreenAdapter:v1.0'
}
```


> 用于支持kotlin的插件用的是 [aspectjx](https://github.com/HujiangTechnology/gradle_plugin_android_aspectjx)   
> 感谢插件作者    
> 因为编织所有二进制文件的问题导致编译速度慢的问题，请查看原作者提供的解决方案    
