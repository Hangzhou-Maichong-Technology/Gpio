# Gpio
## 迈冲科技 GPIO 库
gpio 接口通用实例。

## 一、创建 AndroidStudio 项目，导入库文件

将 mcGpio.aar 文件拷贝到 libs 目录下。

在```app```目录中的```build.gradle```文件中添加如下依赖：

```groovy
    implementation files('libs/mcGpio.aar')
```

## 二、使用接口
### 1，打开

使用GPIO前需打开设备。

```java
//打开 GPIO, 以 RK 平台为例
gpioUtils = GpioUtils.getInstance("/dev/rk_gpio");
```

### 2，关闭

```java
if (gpioUtils != null) {
    gpioUtils.close();
    gpioUtils = null;
}
```

### 3，写入

```java
gpioUtils.setGpioDirection(gpioId, GpioUtils.GPIO_DIRECTION_OUT);
gpioUtils.gpioSetValue(gpioId, GpioUtils.GPIO_VALUE_HIGH);
```

### 4，读取

```java
gpioUtils.setGpioDirection(gpioId, GpioUtils.GPIO_DIRECTION_IN);
gpioUtils.gpioGetValue(gpioId);
```

## 三、下载体验
[gpio 实例 apk 下载](https://github.com/Hangzhou-Maichong-Technology/Gpio/raw/master/apk/Gpio-v1.0.0.apk)

## 四、GPIO 号计算方式
![gpio 号计算](https://i.loli.net/2021/04/07/Jab9DgG8Cr7k16P.png)
