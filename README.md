# MagicScreenAdapter
**依赖本库**不用写一句代码做好屏幕适配:

本适配方案使用[今日头条的适配方案](https://mp.weixin.qq.com/s/d9QCoBP6kV9VSWvVldVVwA);     
预览的时候建议使用(Nexus5,xxhdpi,1920x1080,360dp宽度)的屏幕分辨率预览; 

### 布局预览是什么样在任何设备都是这个样子!       
### 布局预览是什么样在任何设备都是这个样子!     
### 布局预览是什么样在任何设备都是这个样子!  
(重要的事情说三遍)

```
(宽度一致,高度随设备变化,控件不要以底部为锚点依赖,否则在短屏幕上可能会和顶部依赖的控件重叠)      
最好给布局外面套一个ScrollView作为适配,不论是短屏幕还是分屏,页面可以适配各种情况      
这里的宽度定义是指物理意义上的屏幕宽度,跟屏幕朝向无关!例如常规手机,宽度是指屏幕的最窄边    
```
***

### 声明:
**本框架以布局预览为标准,跟设计图无关**        
**您之前怎么把设计图转换成布局还是按照以前方式来**

> 如果老项目一直用的其它宽度dp预览的布局,      
> 则可以在Application 里面重设全局预览宽度(单位dp)
> 这样可以快速适配老项目：
```
MagicScreenAdapter.initDesignWidthInDp(380);
```

### 以下内容如果您的布局预览都是是统一的，请忽略！
***
> 如果某个Activity 或者 Fragment 不想启用适配功能,
> 请在类上使用注解:@IgnoreScreenAdapter；   
> 如果某个Activity 或者 Fragment 有单独的预览宽度(单位dp),
> 请在类上使用注解:@ScreenAdapterDesignWidthInDp(400)；
> 如果一个Activity 同时展示多个Fragment ,并且Fragment之间    
> 或者 Fragment 和 Activity 之间的适配 宽度不一致,则      
> 会导致后续view的变化会以最后一个Fragment为准,解决方案:     
> view变化时,请用代码指定适配布局宽度: 

```
MagicScreenAdapter.adapt(this, 360); //指定后续view变化的适配宽度
MagicScreenAdapter.cancelAdapt(this);//或者后续view取消适配
// this 为指定view 所在 Fragment 或者 Activity
```
***
### 依赖方法:

#### 1.在全局build里面添加下面github仓库地址
```
buildscript {
    ...
    dependencies {
	...
	//java 用这个
	classpath 'cn.leo.plugin:magic-plugin:1.0.0' 
	
	//kotlin 或者 AS 版本低于3.0 用下面这个
	classpath 'com.hujiang.aspectjx:gradle-android-plugin-aspectjx:2.0.0' 
    }
}
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
> google()和jcenter()这两个仓库一般是默认的，如果没有请加上   

#### 2.在app的build里面添加插件和依赖
```
//java 用这个
apply plugin: 'cn.leo.plugin.magic' 

//kotlin 或者 AS 版本低于3.0 用下面这个
apply plugin: 'android-aspectjx'  
...
dependencies {
	...
	implementation 'com.github.jarryleo:MagicScreenAdapter:v1.5'
}
```
***
> 上面2个build里面的 java 和 kotlin 二选一,如果AS版本低于3.0 请使用kotlin 版本
> 用于支持kotlin的插件用的是 [aspectjx](https://github.com/HujiangTechnology/gradle_plugin_android_aspectjx)   
> 感谢原插件作者    
> 因为编织所有二进制文件的问题导致编译速度慢的问题，请查看原作者提供的解决方案
> 部分代码来源:https://blankj.com/2018/07/30/easy-adapt-screen/?tdsourcetag=s_pctim_aiomsg
