
Android 屏幕适配的处理技巧
1.使用 ConstraintLayout，前身是 PercentLayout(百分比布局)
2.多 dimens 基于 dp 的适配方案
values- 后的 sw 指的是 smallest width，也就是最小宽度。
ndroid 系统在运行时会自动识别屏幕可用的最小宽度，然后根据识别的结果
去资源文件中查找相对应的资源文件中的属性值。比如有一个 360dpi 的手机
设备在运行 App 时，会自动到 values-sw360dp 文件夹中寻找对应的值。
https://blog.csdn.net/wolfking0608/article/details/79610431
3.文字 TextView
对于 TextView 的宽高，建议尽量使用 wrap_content 自适应
4.图片ImageView
一般是将 ImageView 的宽高设置为某一固定 dp 值。
还有另外一种做法就是在 Java 代码中动态设置 ImageView 的大小。

